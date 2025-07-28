CREATE TABLE IF NOT EXISTS item_info (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    part_number TEXT NOT NULL UNIQUE,
    description TEXT,
    item_type TEXT NOT NULL,
    series INTEGER,
    comments TEXT
);

CREATE TABLE IF NOT EXISTS items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_info_id INTEGER NOT NULL REFERENCES item_info(id),
    client_part_number TEXT,
    serial_number TEXT NOT NULL UNIQUE,
    old_serial_number TEXT NOT NULL UNIQUE,
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
    status TEXT NOT NULL,
    type TEXT NOT NULL,
    planned_date DATE,
    updated_date DATE,
    finished_date DATE,
    comments TEXT
);

CREATE TABLE IF NOT EXISTS work_order_items (
  work_order_id INTEGER NOT NULL REFERENCES work_orders(id),
  item_id       INTEGER NOT NULL REFERENCES items(id),
  PRIMARY KEY (work_order_id, item_id)
);

CREATE TABLE IF NOT EXISTS serial_numbers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  serial_number  NVARCHAR(20) NOT NULL UNIQUE,
  part_number    NVARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS item_history (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    old_item_id   TEXT    NOT NULL,
    new_item_id  TEXT    NOT NULL,
    changed_at          INTEGER NOT NULL, -- epoch-ms
    reason              TEXT
);

CREATE TABLE IF NOT EXISTS tracking_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    record_date TEXT    NOT NULL,  -- хранится в формате 'YYYY-MM-DD'
    action_target TEXT  NOT NULL,  -- одно из значений ActionTarget.name()
    action_type TEXT    NOT NULL,  -- одно из значений ActionType.name()
    client TEXT,
    target_work_order_id INTEGER,  -- nullable, ссылка на work_orders.id
    target_job_order_id  INTEGER,  -- nullable, ссылка на job_orders.id
    target_pn            TEXT,     -- part number
    target_sn            TEXT,     -- serial number
    target_description   TEXT,     -- описание
    reason               TEXT      -- причина действия
);

-- Индексы для ускорения поиска по часто фильтруемым полям
CREATE INDEX IF NOT EXISTS idx_tracking_date
    ON tracking_records(record_date);

CREATE INDEX IF NOT EXISTS idx_tracking_target
    ON tracking_records(action_target);

CREATE INDEX IF NOT EXISTS idx_tracking_action
    ON tracking_records(action_type);

CREATE INDEX IF NOT EXISTS idx_tracking_wo
    ON tracking_records(target_work_order_id);

CREATE INDEX IF NOT EXISTS idx_tracking_jo
    ON tracking_records(target_job_order_id);