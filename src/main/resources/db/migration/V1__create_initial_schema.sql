
CREATE OR REPLACE FUNCTION _update_updated_at()
RETURNS TRIGGER AS $$
  BEGIN
    NEW.updated_at = current_timestamp;
    RETURN NEW;
  END;
$$ language 'plpgsql';


CREATE TABLE context
(
    id                  UUID        PRIMARY KEY,
    collection_id       UUID        NOT NULL,
    is_collection       BOOLEAN     NOT NULL DEFAULT TRUE,
    profile_id          UUID        NOT NULL,
    class_id            UUID,
    context_data        JSONB,
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    start_date          TIMESTAMP,
    due_date            TIMESTAMP,
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    updated_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp
);
CREATE INDEX context_profile_id_idx ON context (profile_id);
CREATE INDEX context_class_id_idx ON context (class_id);
CREATE TRIGGER context_updated_at_trigger
    BEFORE UPDATE
    ON context
    FOR EACH ROW EXECUTE PROCEDURE _update_updated_at();


CREATE TABLE context_profile
(
    id                      UUID        PRIMARY KEY,
    context_id              UUID        NOT NULL REFERENCES context(id),
    profile_id              UUID        NOT NULL,
    current_resource_id     UUID,
    is_complete             BOOLEAN     NOT NULL DEFAULT FALSE,
    event_summary_data      JSONB,
    taxonomy_summary_data   JSONB,
    created_at              TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    updated_at              TIMESTAMP   NOT NULL DEFAULT current_timestamp
);
CREATE INDEX context_profile_context_id_idx ON context_profile (context_id);
CREATE INDEX context_profile_profile_id_idx ON context_profile (profile_id);
CREATE TRIGGER context_profile_updated_at_trigger
    BEFORE UPDATE
    ON context_profile
    FOR EACH ROW EXECUTE PROCEDURE _update_updated_at();


CREATE TABLE context_profile_event
(
    id                      UUID        PRIMARY KEY,
    context_profile_id      UUID        NOT NULL REFERENCES context_profile(id),
    resource_id             UUID        NOT NULL,
    event_data              JSONB,
    created_at              TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT context_profile_event_context_profile_id_resource_id_uc UNIQUE (context_profile_id, resource_id)
);


CREATE TABLE current_context_profile
(
    context_id              UUID        NOT NULL REFERENCES context(id),
    profile_id              UUID        NOT NULL,
    context_profile_id      UUID        NOT NULL REFERENCES context_profile(id),
    created_at              TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT current_context_profile_context_id_profile_id_uc UNIQUE (context_id, profile_id)
);
