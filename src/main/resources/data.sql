-- Sample data
INSERT INTO complaints (product_id, content, created_date, reporter, country, counter, last_modified)
VALUES
    ('PROD-001', 'Produkt nie działa zgodnie z opisem.', CURRENT_TIMESTAMP, 'tzason@example.com', 'PL', 1, CURRENT_TIMESTAMP),
    ('PROD-002', 'Das Produkt wurde nicht zum Verkauf angeboten.', CURRENT_TIMESTAMP, 'tzason@example.de', 'DE', 1, CURRENT_TIMESTAMP),