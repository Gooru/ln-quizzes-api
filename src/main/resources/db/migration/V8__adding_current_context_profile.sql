CREATE TABLE current_context_profile
(
    context_id          UUID        NOT NULL REFERENCES context(id),
    profile_id          UUID        NOT NULL REFERENCES profile(id),
    context_profile_id  UUID        NOT NULL REFERENCES context_profile(id),
    created_at          TIMESTAMP   NOT NULL DEFAULT current_timestamp,
    CONSTRAINT current_context_profile_context_id_profile_id_uc UNIQUE (context_id, profile_id)
);
