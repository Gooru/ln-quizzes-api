
-- Adding event_summary_data column to context_profile entity
ALTER TABLE context_profile ADD COLUMN event_summary_data JSONB;

-- Removing deprecated entities
DROP TABLE event;
DROP TABLE event_index;
DROP TABLE collection_on_air;