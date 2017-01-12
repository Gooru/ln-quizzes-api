CREATE TABLE current_context_profile
(
    context_id          UUID NOT NULL REFERENCES context(id),
    profile_id          UUID NOT NULL REFERENCES profile(id),
    context_profile_id  UUID NOT NULL REFERENCES context_profile(id),
    is_complete         BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT current_context_profile_context_id_profile_id_uc UNIQUE (context_id, profile_id)
);

--This field is not necessary with the table 'current_context_profile'
ALTER TABLE context_profile DROP COLUMN IF EXISTS is_complete;