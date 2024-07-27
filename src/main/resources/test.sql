SELECT er.id AS id,
       bc.id AS bc_id,
       bc.fullname AS bc_name,
       bc.code AS bc_code,
       bc.sign AS bc_sign,
       tc.id AS tc_sign,
       tc.fullname AS tc_name,
       tc.code AS tc_code,
       tc.sign AS tc_sign,
       er.rate AS rate
FROM exchange_rates er
JOIN currencies bc ON er.id = bc.id
JOIN currencies tc ON er.id = tc.id
WHERE er.id = 1;