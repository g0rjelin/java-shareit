create table if not exists users(
                                    id BIGINT generated always as identity primary key,
                                    name varchar(100),
    email varchar(320),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
    );

create table if not exists requests(
                                       id BIGINT generated always as identity primary key,
                                       description varchar(1000),
    requestor_id bigint,
    constraint fk_requests_to_users foreign key(requestor_id) references users(id)
    );

create table if not exists items(
                                    id BIGINT generated always as identity primary key,
                                    name varchar(100),
    description varchar(1000),
    is_available bool,
    owner_id bigint,
    request_id bigint,
    constraint fk_items_to_users foreign key(owner_id) references users(id),
    constraint fk_items_to_requests foreign key(request_id) references requests(id)
    );

create table if not exists bookings(
                                       id BIGINT generated always as identity primary key,
                                       start_date timestamp without time zone,
                                       end_date timestamp without time zone,
                                       item_id bigint,
                                       booker_id bigint,
                                       status varchar(50),
    constraint fk_bookings_to_items foreign key(item_id) references items(id),
    constraint fk_bookings_to__users foreign key(booker_id) references users(id)
    );