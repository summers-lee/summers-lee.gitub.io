-- ============================================================
-- DAD 220 RMA Returns Analysis
-- Compiled from terminal screenshots (mysql client, Codio lab environment)
-- ============================================================

-- ------------------------------------------------------------
-- Query 1: Returns by State
-- ------------------------------------------------------------
-- NOTE: This query's TEXT was not visible in the screenshot — only its
-- output (48 rows: state name + return count, descending) was captured.
-- The query below is a RECONSTRUCTION based on that output shape, not a
-- verbatim copy. Verify this against your actual saved query (check your
-- Codio lab files or shell history) before using it in your enhancement
-- writeup or code review recording.
--
-- Reconstructed query (adjust table/column names to match your actual schema):
select c.State, count(*) as total_returns
from RMA r
join Orders o on r.OrderID = o.OrderID
join Customers c on o.CustomerID = c.CustomerID
group by c.State
order by total_returns desc;


-- ------------------------------------------------------------
-- Query 2: Returns by Product (raw Description field)
-- ------------------------------------------------------------
-- Captured directly from screenshot — verbatim.
select o.Description, count(*) as total_returns,
    round((count(*) * 100.0 / (select count(*) from RMA)), 2) as return_percentage
from RMA r
join Orders o on r.OrderID = o.OrderID
group by o.Description
order by return_percentage desc;


-- ------------------------------------------------------------
-- Query 3: Returns by Product (cleaned Description field)
-- ------------------------------------------------------------
-- Captured directly from screenshot — verbatim.
-- This query exists ONLY because Query 2's raw grouping produced
-- duplicate-looking rows due to stray \n and \r characters embedded
-- in the Description field — the exact data-quality symptom driving
-- the normalization enhancement.
select
    trim(replace(replace(o.Description, '\n', ''), '\r', '')) as CleanedDescription,
    count(*) as total_returns,
    round((count(*) * 100.0 / (select count(*) from RMA)), 2) as return_percentage
from RMA r
join Orders o on r.OrderID = o.OrderID
group by CleanedDescription
order by return_percentage desc;