create table book (
    id serial primary key
  , isbn text
  , title text
);

insert into book (isbn, title) values
    ('12345679', 'The Pragmatic Programmer')
    ;

