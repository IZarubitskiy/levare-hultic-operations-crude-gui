CREATE TABLE IF NOT EXISTS item_info (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    part_number TEXT NOT NULL UNIQUE,
    description TEXT,
    item_type TEXT NOT NULL, -- enum ItemType
    comments TEXT
);

CREATE TABLE IF NOT EXISTS items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_info_id INTEGER NOT NULL REFERENCES item_info(id),
    client_part_number TEXT,
    serial_number TEXT NOT NULL UNIQUE,
    ownership TEXT NOT NULL, -- enum Client
    item_condition TEXT NOT NULL, -- enum ItemCondition
    item_status TEXT NOT NULL, -- enum ItemStatus
    job_order_id INTEGER REFERENCES job_orders(id),
    comments TEXT
);

CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    position TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS work_orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    work_order_number TEXT NOT NULL,
    client TEXT NOT NULL,
    well TEXT NOT NULL,
    request_date DATE NOT NULL,
    delivery_date DATE,
    status TEXT NOT NULL,
    requestor_id INTEGER REFERENCES users(id),
    comments TEXT
);

CREATE TABLE IF NOT EXISTS job_orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    work_order_id INTEGER REFERENCES work_orders(id),
    item_id INTEGER NOT NULL REFERENCES items(id),
    status TEXT NOT NULL, -- enum JobOrderStatus
    responsible_user_id INTEGER REFERENCES users(id),
    comments TEXT
);

CREATE TABLE IF NOT EXISTS work_order_items (
  work_order_id INTEGER NOT NULL REFERENCES work_orders(id),
  item_id       INTEGER NOT NULL REFERENCES items(id),
  PRIMARY KEY (work_order_id, item_id)
);