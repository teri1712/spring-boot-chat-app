update inbox_log
set action = 'ADDITION'
where action = 'DELIVERED';