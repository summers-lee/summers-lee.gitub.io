"""
Tests the DAD 220 database enhancement using SQLite as a stand-in for
MySQL (this environment has no MySQL server). SQLite supports standard
JOINs, GROUP BY, and VIEWs, which is enough to verify the schema and
view logic. SQLite does NOT support stored procedures -- that part of
the enhancement is written separately, in correct MySQL syntax, in
stored_procedure.sql, and is not exercised here.

Synthetic data is used because the original orders.csv / rma.csv files
were not available (only screenshots of query results). This data is
built to reproduce the SAME BUG PATTERN seen in the original screenshots
-- the same product stored as multiple differently-formatted strings --
not to match the original's exact row counts.
"""

import os
import sqlite3
import unittest


def load_sql(conn, path):
    with open(path) as f:
        conn.executescript(f.read())


ORIGINAL_SCHEMA = os.path.join(os.path.dirname(__file__), "..", "src", "original_schema.sql")
NORMALIZED_SCHEMA = os.path.join(os.path.dirname(__file__), "..", "src", "normalized_schema.sql")


def seed_original_dirty_data(conn):
    """
    Reproduces the original bug: the same product name stored three
    different ways across orders (clean, trailing \n, trailing \r\n),
    the same defect visible in the DAD 220 screenshots.
    """
    cur = conn.cursor()
    products_as_stored = (
        ["Basic Switch 10/100/1000 BaseT 48 port"] * 5
        + ["Basic Switch 10/100/1000 BaseT 48 port\n"] * 3
        + ["Basic Switch 10/100/1000 BaseT 48 port\r\n"] * 2
        + ["Enterprise Switch 40GigE SFP+ 48 port"] * 4
        + ["Enterprise Switch 40GigE SFP+ 48 port\n"] * 2
    )
    order_id = 1
    rma_id = 1
    for desc in products_as_stored:
        cur.execute("INSERT INTO Orders (OrderID, CustomerID, State, Description) "
                    "VALUES (?, ?, ?, ?)", (order_id, 100 + order_id, "TX", desc))
        cur.execute("INSERT INTO RMA (RMA_ID, OrderID) VALUES (?, ?)", (rma_id, order_id))
        order_id += 1
        rma_id += 1
    conn.commit()


def seed_normalized_data(conn):
    """Same real-world facts as seed_original_dirty_data, but correctly
    normalized: each product stored exactly once."""
    cur = conn.cursor()
    cur.execute("INSERT INTO Products (ProductID, ProductName, Category) "
                "VALUES (1, 'Basic Switch 10/100/1000 BaseT 48 port', 'Switches')")
    cur.execute("INSERT INTO Products (ProductID, ProductName, Category) "
                "VALUES (2, 'Enterprise Switch 40GigE SFP+ 48 port', 'Switches')")

    order_id = 1
    rma_id = 1
    # 10 orders for product 1 (5+3+2 from the dirty version), 6 for product 2 (4+2)
    for product_id, count in [(1, 10), (2, 6)]:
        for _ in range(count):
            cur.execute("INSERT INTO Orders (OrderID, CustomerID, State, ProductID) "
                        "VALUES (?, ?, ?, ?)", (order_id, 100 + order_id, "TX", product_id))
            cur.execute("INSERT INTO RMA (RMA_ID, OrderID) VALUES (?, ?)", (rma_id, order_id))
            order_id += 1
            rma_id += 1
    conn.commit()


class TestOriginalSchemaReproducesTheBug(unittest.TestCase):
    """Proves the data-quality bug is real, not just a theoretical concern."""

    def setUp(self):
        self.conn = sqlite3.connect(":memory:")
        load_sql(self.conn, ORIGINAL_SCHEMA)
        seed_original_dirty_data(self.conn)

    def tearDown(self):
        self.conn.close()

    def test_raw_grouping_splits_one_product_into_three_rows(self):
        cur = self.conn.execute(
            "SELECT Description, COUNT(*) FROM Orders o "
            "JOIN RMA r ON r.OrderID = o.OrderID "
            "GROUP BY Description")
        rows = cur.fetchall()
        # The "Basic Switch" product appears as 3 separate rows due to
        # the \n / \r\n variants -- this is the bug.
        basic_switch_rows = [r for r in rows if r[0].startswith("Basic Switch")]
        self.assertEqual(len(basic_switch_rows), 3,
            "Expected the dirty data to split one product into 3 rows")

    def test_cleaned_query_correctly_merges_them_back_into_one_row(self):
        cur = self.conn.execute(
            "SELECT TRIM(REPLACE(REPLACE(Description, char(10), ''), char(13), '')) "
            "AS Cleaned, COUNT(*) FROM Orders o "
            "JOIN RMA r ON r.OrderID = o.OrderID "
            "GROUP BY Cleaned")
        rows = dict(cur.fetchall())
        self.assertEqual(rows["Basic Switch 10/100/1000 BaseT 48 port"], 10)
        self.assertEqual(rows["Enterprise Switch 40GigE SFP+ 48 port"], 6)


class TestNormalizedSchemaAvoidsTheBug(unittest.TestCase):
    """Proves the normalized schema gets the right answer with no
    text-cleaning step at all, and that its numbers match the manually
    cleaned original query -- the enhancement is correct, not just tidier."""

    def setUp(self):
        self.conn = sqlite3.connect(":memory:")
        load_sql(self.conn, NORMALIZED_SCHEMA)
        seed_normalized_data(self.conn)

    def tearDown(self):
        self.conn.close()

    def test_view_returns_one_row_per_product_with_no_cleaning_needed(self):
        cur = self.conn.execute(
            "SELECT ProductName, total_returns FROM vw_ReturnsByProduct "
            "ORDER BY ProductName")
        rows = cur.fetchall()
        self.assertEqual(len(rows), 2)  # one row per product, not three

    def test_view_totals_match_the_manually_cleaned_original_query(self):
        cur = self.conn.execute(
            "SELECT ProductName, total_returns FROM vw_ReturnsByProduct")
        totals = dict(cur.fetchall())
        self.assertEqual(totals["Basic Switch 10/100/1000 BaseT 48 port"], 10)
        self.assertEqual(totals["Enterprise Switch 40GigE SFP+ 48 port"], 6)

    def test_return_percentage_sums_to_100_across_products(self):
        cur = self.conn.execute("SELECT SUM(return_percentage) FROM vw_ReturnsByProduct")
        total_pct = cur.fetchone()[0]
        self.assertAlmostEqual(total_pct, 100.0, places=1)

    def test_top_n_query_equivalent_to_the_stored_procedure_logic(self):
        # SQLite can't run the real MySQL stored procedure, but this
        # mirrors its logic (ORDER BY ... LIMIT ?) using a bound
        # parameter, the same parameterization principle the real
        # sp_GetTopReturnedProducts(TopN) procedure uses.
        top_n = 1
        cur = self.conn.execute(
            "SELECT ProductName FROM vw_ReturnsByProduct "
            "ORDER BY return_percentage DESC LIMIT ?", (top_n,))
        result = cur.fetchall()
        self.assertEqual(len(result), 1)
        self.assertEqual(result[0][0], "Basic Switch 10/100/1000 BaseT 48 port")

    def test_products_table_rejects_order_with_unknown_product(self):
        # Foreign key enforcement needs to be turned on explicitly in SQLite
        self.conn.execute("PRAGMA foreign_keys = ON")
        with self.assertRaises(sqlite3.IntegrityError):
            self.conn.execute(
                "INSERT INTO Orders (OrderID, CustomerID, State, ProductID) "
                "VALUES (999, 1, 'TX', 9999)")  # ProductID 9999 does not exist


if __name__ == "__main__":
    unittest.main()
