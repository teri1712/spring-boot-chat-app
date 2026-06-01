update theme
set background = 'https://i.pinimg.com/736x/7d/ec/62/7dec625b5a1bb168d8f472e75982cb88.jpg'
where id = 1;


INSERT INTO theme (id, background)
values (nextval('theme_seq'), 'https://i.pinimg.com/1200x/d3/1b/9d/d31b9dcca8abe587c39ce24c886961b6.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/736x/4c/02/62/4c0262c1337e81543eb05e4cdaad71a9.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/1200x/d5/76/e3/d576e32afeee903278a0c94858b31371.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/1200x/1b/fb/64/1bfb64e8bf12b474abc8afa10eb600f0.jpg');


update setting
set theme_id = null;