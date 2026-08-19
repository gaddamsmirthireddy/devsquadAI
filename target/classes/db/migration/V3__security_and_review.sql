create table security_reports (
    id uuid primary key,
    project_id uuid not null,
    critical_count integer not null,
    high_count integer not null,
    medium_count integer not null,
    low_count integer not null,
    passed boolean not null,
    summary varchar(10000),
    created_at timestamp not null default current_timestamp,
    constraint fk_security_reports_project foreign key (project_id) references projects(id)
);

create table code_reviews (
    id uuid primary key,
    project_id uuid not null,
    critical_count integer not null,
    high_count integer not null,
    medium_count integer not null,
    low_count integer not null,
    passed boolean not null,
    summary varchar(10000),
    created_at timestamp not null default current_timestamp,
    constraint fk_code_reviews_project foreign key (project_id) references projects(id)
);

create index idx_security_reports_project_id on security_reports(project_id);
create index idx_code_reviews_project_id on code_reviews(project_id);