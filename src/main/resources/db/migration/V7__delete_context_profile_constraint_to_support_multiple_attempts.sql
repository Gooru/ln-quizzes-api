--Delete context_profile_context_id_profile_id_uc constraint on context_profile to support multiple attempts
ALTER TABLE context_profile DROP CONSTRAINT context_profile_context_id_profile_id_uc;

--Create an index for context_profile on context_id and profile_id
CREATE INDEX context_profile_context_id_profile_id_idx ON context_profile (context_id, profile_id);
