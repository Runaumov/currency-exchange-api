INSERT INTO currencies
(code, fullname, sign)
VALUES
    ('USD', 'US Dollar', '$'),
    ('EUR', 'Euro', '€'),
    ('GBP', 'British Pound Sterling', '£'),
    ('JPY', 'Japanese Yen', '¥'),
    ('CHF', 'Swiss Franc', 'CHF'),
    ('CAD', 'Canadian Dollar', '$'),
    ('AUD', 'Australian Dollar', '$'),
    ('CNY', 'Chinese Yuan', '¥'),
    ('INR', 'Indian Rupee', '₹'),
    ('RUB', 'Russian Ruble', '₽');

INSERT INTO exchange_rates
(base_currency_id, target_currency_id, rate)
VALUES
    (1, 2, 0.93),
    (1, 3, 0.78),
    (1, 4, 161.62),
    (1, 10, 88.51),
    (2, 9, 90.08),
    (2, 8, 7.88),
    (3, 8, 9.31),
    (3, 9, 106.48),
    (3, 10, 112.79);
