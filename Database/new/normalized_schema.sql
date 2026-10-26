-- ============================================================
-- ENHANCED (normalized) schema.
-- Product text is stored exactly once, in Products. Orders references
-- it by ProductID, so the same product can never be split into two
-- differently-formatted rows the way it was in the original schema.
-- ============================================================

CREATE TABLE Products (
    ProductID   INTEGER PRIMARY KEY,
    ProductName TEXT NOT NULL,
    Category    TEXT
);

CREATE TABLE Orders (
    OrderID     INTEGER PRIMARY KEY,
    CustomerID  INTEGER,
    State       TEXT,
    ProductID   INTEGER NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE RMA (
    RMA_ID    INTEGER PRIMARY KEY,
    OrderID   INTEGER,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

-- View: defines the corrected join/aggregation logic once, instead of
-- being re-derived by hand in every analysis query.
CREATE VIEW vw_ReturnsByProduct AS
SELECT
    p.ProductID,
    p.ProductName,
    COUNT(*) AS total_returns,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM RMA), 2) AS return_percentage
FROM RMA r
JOIN Orders o   ON r.OrderID = o.OrderID
JOIN Products p ON o.ProductID = p.ProductID
GROUP BY p.ProductID, p.ProductName;

-- Same normalized structure also supports the returns-by-state analysis
-- without any text-cleaning step, since State was always a clean column.
CREATE VIEW vw_ReturnsByState AS
SELECT
    o.State,
    COUNT(*) AS total_returns,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM RMA), 2) AS return_percentage
FROM RMA r
JOIN Orders o ON r.OrderID = o.OrderID
GROUP BY o.State;
