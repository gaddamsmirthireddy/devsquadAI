create table projects (
    id uuid primary key,
    original_requirement varchar(4000) not null,
    status varchar(64) not null,
    current_phase varchar(128) not null,
    approval_required boolean not null default false,
    quality_score double precision,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    version bigint not null default 0
);

create table project_requirements (
    id uuid primary key,
    project_id uuid not null unique,
    raw_requirement varchar(4000) not null,
    requirements_json varchar(10000),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    version bigint not null default 0,
    constraint fk_project_requirements_project foreign key (project_id) references projects(id)
);

create table artifacts (
    id uuid primary key,
    project_id uuid not null,
    artifact_type varchar(64) not null,
    created_by_agent_type varchar(64) not null,
    prompt_version varchar(64) not null,
    content varchar(10000) not null,
    created_at timestamp not null default current_timestamp,
    version bigint not null default 0,
    constraint fk_artifacts_project foreign key (project_id) references projects(id)
);

create table agent_tasks (
    id uuid primary key,
    project_id uuid not null,
    agent_type varchar(64) not null,
    status varchar(32) not null,
    priority integer not null,
    retry_count integer not null,
    input_artifact_id uuid,
    output_artifact_id uuid,
    expected_output_type varchar(128) not null,
    input_payload varchar(10000),
    error_message varchar(10000),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    started_at timestamp,
    completed_at timestamp,
    version bigint not null default 0,
    constraint fk_agent_tasks_project foreign key (project_id) references projects(id)
);

create table project_events (
    id uuid primary key,
    project_id uuid not null,
    event_type varchar(64) not null,
    payload_json varchar(10000),
    source_task_id uuid,
    occurred_at timestamp not null default current_timestamp,
    constraint fk_project_events_project foreign key (project_id) references projects(id)
);

create index idx_artifacts_project_id on artifacts(project_id);
create index idx_agent_tasks_project_id on agent_tasks(project_id);
create index idx_agent_tasks_status on agent_tasks(status);
create index idx_agent_tasks_agent_type on agent_tasks(agent_type);
create index idx_project_events_project_id on project_events(project_id);
create index idx_project_events_event_type on project_events(event_type);