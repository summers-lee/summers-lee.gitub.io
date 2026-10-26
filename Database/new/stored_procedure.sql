-- ============================================================
-- MySQL stored procedure (DAD 220's actual database engine).
-- NOT run as part of the automated test suite: SQLite, used for
-- testing the schema/view logic in this environment, does not support
-- stored procedures at all. This file is the real deliverable syntax,
-- meant to be run against the actual MySQL lab database.
-- ============================================================

DELIMITER $$

CREATE PROCEDURE sp_GetTopReturnedProducts(IN TopN INT)
BEGIN
    SELECT ProductID, ProductName, total_returns, return_percentage
    FROM vw_ReturnsByProduct
    ORDER BY return_percentage DESC
    LIMIT TopN;
END$$

DELIMITER ;

-- Example call, using a bound parameter rather than a string-built value:
-- CALL sp_GetTopReturnedProducts(5);
