-- Insert sample catalog entries into item_info
INSERT INTO item_info (part_number, description, item_type, comments) VALUES
  ('P100', 'Main pump rotor', 'PUMP', 'Test catalog entry for pump'),
  ('V200', 'Check valve 1/2-inch', 'VALVE', 'Test catalog entry for valve'),
  ('S300', 'Sealing ring', 'SEAL', 'Test catalog entry for seal');

-- Insert sample items linked to item_info
INSERT INTO items (
  item_info_part_number,
  client_part_number,
  serial_number,
  ownership,
  item_type,
  item_condition,
  item_status,
  job_order_id,
  comments
) VALUES
  ('P100', 'CPN-001', 'SN-0001', 'PETCO', 'PUMP', 'NEW', 'IN_STOCK', NULL, 'Initial stock item'),
  ('V200', 'CPN-002', 'SN-0002', 'METCO', 'VALVE', 'USED', 'IN_REPAIR', NULL, 'Sent for repair'),
  ('S300', 'CPN-003', 'SN-0003', 'RETCO', 'SEAL', 'NEW', 'IN_STOCK', NULL, 'New sealing ring');

-- Client-specific part numbers for internal item_info
INSERT INTO item_info_client_parts (part_number, client, client_part_number) VALUES
  ('P100', 'PETCO', 'G2210'),
  ('P100', 'KETCO', 'qwerty'),
  ('V200', 'METCO', 'M-VALVE-02');