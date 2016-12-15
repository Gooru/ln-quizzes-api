-- the constraint adding below implicitly creates an index with owner_profile_id, so we drop this one
DROP INDEX IF EXISTS collection_owner_profile_id_idx;

-- this constraint means that only one "owner - external parent" combination is possible by collection
ALTER TABLE collection
ADD CONSTRAINT collection_owner_profile_id_external_parent_id_uc UNIQUE (owner_profile_id, DECODE(MD5(external_parent_id), 'HEX'));
