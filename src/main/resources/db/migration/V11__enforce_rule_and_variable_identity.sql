ALTER TABLE automationrule
  ALTER COLUMN id SET NOT NULL;

ALTER TABLE automationrule
  ADD CONSTRAINT automationrule_pkey PRIMARY KEY (id);

CREATE INDEX IF NOT EXISTS automationrule_recipient_id_idx
  ON automationrule (recipient_id);

ALTER TABLE automationvariable
  ALTER COLUMN id SET NOT NULL;

ALTER TABLE automationvariable
  ADD CONSTRAINT automationvariable_pkey PRIMARY KEY (id);

CREATE INDEX IF NOT EXISTS automationvariable_recipient_id_idx
  ON automationvariable (recipient_id);
