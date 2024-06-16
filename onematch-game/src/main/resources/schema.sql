CREATE TABLE IF NOT EXISTS solved (
	invalid TEXT(8) NOT NULL,
	equation TEXT(8) NOT NULL,
	difficulty TEXT(10) NOT NULL,
	CONSTRAINT solved_pk PRIMARY KEY (invalid)
);
CREATE INDEX IF NOT EXISTS solved_equation_IDX ON solved (equation);