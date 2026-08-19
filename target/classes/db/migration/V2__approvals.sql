create table approvals (
    id uuid primary key,
    project_id uuid not null,
    action varchar(32) not null,
    status varchar(32) not null,
    reason varchar(10000),
    decision_comment varchar(10000),
    decided_at timestamp,
    created_at timestamp not null default current_timestamp,
    constraint fk_approvals_project foreign key (project_id) references projects(id)
);

create index idx_approvals_project_id on approvals(project_id);
create index idx_approvals_status on approvals(status);