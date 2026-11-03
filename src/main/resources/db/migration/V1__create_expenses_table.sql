CREATE TABLE expenses (
    id          BIGSERIAL      PRIMARY KEY,
    description VARCHAR(255)   NOT NULL,
    amount      NUMERIC(12, 2) NOT NULL,
    category    VARCHAR(20)    NOT NULL,
    date        DATE           NOT NULL,
    CONSTRAINT ck_expenses_category CHECK (category IN
        ('FOOD', 'TRANSPORT', 'HOUSING', 'HEALTH', 'EDUCATION', 'LEISURE', 'OTHER'))
);

-- Serves findByCategoryOrderByDateDesc.
CREATE INDEX idx_expenses_category_date ON expenses (category, date DESC);
