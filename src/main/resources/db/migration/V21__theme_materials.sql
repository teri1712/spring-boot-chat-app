alter table theme
    add column name varchar(255);

update theme
set name = 'silver-mist'
where id = (select id from theme order by id asc limit 1);
update theme
set name = 'ocean-breeze'
where id = (select id from theme order by id asc offset 1 limit 1);
update theme
set name = 'meadow'
where id = (select id from theme order by id asc offset 2 limit 1);
update theme
set name = 'golden-hour'
where id = (select id from theme order by id asc offset 3 limit 1);
update theme
set name = 'cappuccino'
where id = (select id from theme order by id asc offset 4 limit 1);

alter table message
    drop column room_name;
alter table message
    drop column room_avatar;
alter table message
    drop column theme;