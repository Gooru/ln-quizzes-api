
ALTER TABLE context ADD COLUMN context_map_key TEXT;

CREATE UNIQUE INDEX context_context_map_key_idx ON context (context_map_key);