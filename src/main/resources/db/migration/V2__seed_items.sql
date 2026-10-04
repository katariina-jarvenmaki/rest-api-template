-- The demo rows shown out of the box. Runs once per database;
-- Flyway tracks V2 in flyway_schema_history alongside V1, so
-- restarts and redeploys never duplicate the rows.

INSERT INTO items (name, description) VALUES
    ('Sample item', 'Ships with the template; edit or delete freely'),
    ('Second sample', 'Demonstrates a filled description'),
    ('Third sample', NULL);