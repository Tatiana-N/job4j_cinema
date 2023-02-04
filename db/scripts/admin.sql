insert into genres (name) values ('комедия');
insert into genres (name) values ('драма');
insert into genres (name) values ('боевик');
insert into files (name, path) values ('1', 'src/main/resources/static/images.jfif');
insert into films (name, description, minimal_age, year, file_id,genre_id,duration_in_minutes) values ('Крик 4', 'Сидни Прескотт последние несколько лет провела в относительной тишине и покое. Жизнь потихоньку налаживается, вдали от вспышек и видеокамер папарацци. Но когда находят труп одного из студентов, мир Сидни Прескотт вновь начинает рушиться. Всему виной возродившийся убийца в маске «Лицо призрака».', 18, 2007,2,4,111);
insert into halls (name, row_count, place_count, description)  values ('Зал №2 VIP',6, 6, 'VIP зал');
insert into film_sessions (film_id, halls_id, start_time, end_time) VALUES (3,2,'2023-10-13 13:00:03','2023-10-13 15:00:00');