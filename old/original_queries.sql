-- Captured verbatim from the DAD 220screenshots.

-- Query A: raw grouping (the one that produced duplicate-looking rows)
select o.Description, count(*) as total_returns,
    round((count(*) * 100.0 / (select count(*) from RMA)), 2) as return_percentage
from RMA r
join Orders o on r.OrderID = o.OrderID
group by o.Description
order by return_percentage desc;

-- Query B: cleaned grouping (written only to work around Query A's problem)
select
    trim(replace(replace(o.Description, '\n', ''), '\r', '')) as CleanedDescription,
    count(*) as total_returns,
    round((count(*) * 100.0 / (select count(*) from RMA)), 2) as return_percentage
from RMA r
join Orders o on r.OrderID = o.OrderID
group by CleanedDescription
order by return_percentage desc;
