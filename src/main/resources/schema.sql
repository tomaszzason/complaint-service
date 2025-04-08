DROP TABLE IF EXISTS complaints;

CREATE TABLE complaints (
                            id BIGSERIAL PRIMARY KEY,
                            product_id VARCHAR(255) NOT NULL,
                            content TEXT NOT NULL,
                            created_date TIMESTAMP NOT NULL,
                            reporter VARCHAR(255) NOT NULL,
                            country VARCHAR(255) NOT NULL,
                            counter INT NOT NULL DEFAULT 1,
                            last_modified TIMESTAMP
);

-- Indexes
CREATE INDEX idx_product_reporter ON complaints(product_id, reporter);
CREATE INDEX idx_country ON complaints(country);
CREATE INDEX idx_created_date ON complaints(created_date);