CREATE TABLE currencies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code VARCHAR UNIQUE NOT NULL,
    fullname VARCHAR NOT NULL,
    sign VARCHAR
);

CREATE TABLE exchange_rates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id INTEGER NOT NULL,
    target_currency_id INTEGER NOT NULL,
    rate DECIMAL(6) NOT NULL,
    UNIQUE (base_currency_id, target_currency_id)
);