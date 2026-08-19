ALTER TABLE agent_tasks
    ALTER COLUMN input_payload TYPE TEXT;

ALTER TABLE agent_tasks
    ALTER COLUMN error_message TYPE TEXT;

ALTER TABLE approvals
    ALTER COLUMN decision_comment TYPE TEXT;

ALTER TABLE approvals
    ALTER COLUMN reason TYPE TEXT;

ALTER TABLE artifacts
    ALTER COLUMN content TYPE TEXT;

ALTER TABLE code_reviews
    ALTER COLUMN summary TYPE TEXT;

ALTER TABLE project_events
    ALTER COLUMN payload_json TYPE TEXT;

ALTER TABLE project_requirements
    ALTER COLUMN raw_requirement TYPE TEXT;

ALTER TABLE project_requirements
    ALTER COLUMN requirements_json TYPE TEXT;

ALTER TABLE projects
    ALTER COLUMN original_requirement TYPE TEXT;

ALTER TABLE security_reports
    ALTER COLUMN summary TYPE TEXT;