alter table budget add column author_id int;

alter table budget add foreign key (author_id) references author(id);

update budget set type = 'Расход' where type = 'Комиссия';