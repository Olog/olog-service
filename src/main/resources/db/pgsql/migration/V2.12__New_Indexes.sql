--
-- Definition of table entries
--

CREATE INDEX created_fk ON entries (created);

--
-- Definition of table logbooks
--

CREATE UNIQUE INDEX name_fx ON logbooks (name);

--
-- Definition of table logs
--

CREATE INDEX modified_fk ON logs (modified);

--
-- Definition of table logs_logbooks
--

CREATE INDEX log_id_tk ON logs_logbooks (log_id);
CREATE INDEX logbook_id_tk ON logs_logbooks (logbook_id);

--
-- Definition of table properties
--

CREATE INDEX name_prop_tk ON properties (name);

--
-- Definition of table attributes
--

CREATE INDEX property_id_tk ON attributes (id);
CREATE INDEX attribute_name_tk ON attributes (name);

--
-- Definition of table logs_attributes
--

CREATE INDEX logs_attributes_log_id_tk ON logs_attributes (log_id);
CREATE INDEX logs_attributes_attribute_id_tk ON logs_attributes (attribute_id);

