-- Insert sample catalog entries into item_info
INSERT INTO item_info (part_number, description, item_type, comments) VALUES
  ('P100', 'Main pump rotor',     'PUMP',      'Test catalog entry for pump'),
  ('V200', 'Check valve 1/2-inch','ADAPTER',   'Test catalog entry for valve'),
  ('S300', 'Sealing ring',        'SEAL',      'Test catalog entry for seal');

-- Insert sample items linked to item_info
-- We create 5 rows for each of the 5 pages (total 25 rows)
INSERT INTO items (
  item_info_part_number,
  client_part_number,
  serial_number,
  ownership,
  item_condition,
  item_status,
  job_order_id,
  comments
) VALUES
  -- 5 × stock_egypt: NEW or REPAIRED, ON_STOCK, ownership NOT CORP/RETS (use METCO)
  ('P100','CPN-100','SN-1000','METCO','NEW',      'ON_STOCK', NULL,'Stock Egypt #1'),
  ('V200','CPN-101','SN-1001','METCO','REPAIRED', 'ON_STOCK', NULL,'Stock Egypt #2'),
  ('S300','CPN-102','SN-1002','METCO','NEW',      'ON_STOCK', NULL,'Stock Egypt #3'),
  ('P100','CPN-103','SN-1003','METCO','REPAIRED', 'ON_STOCK', NULL,'Stock Egypt #4'),
  ('V200','CPN-104','SN-1004','METCO','NEW',      'ON_STOCK', NULL,'Stock Egypt #5'),

  -- 5 × outstanding: USED, ON_STOCK, ownership NOT CORP/RETS (use METCO)
  ('S300','CPN-200','SN-2000','METCO','USED',     'ON_STOCK', NULL,'Outstanding #1'),
  ('P100','CPN-201','SN-2001','METCO','USED',     'ON_STOCK', NULL,'Outstanding #2'),
  ('V200','CPN-202','SN-2002','METCO','USED',     'ON_STOCK', NULL,'Outstanding #3'),
  ('S300','CPN-203','SN-2003','METCO','USED',     'ON_STOCK', NULL,'Outstanding #4'),
  ('P100','CPN-204','SN-2004','METCO','USED',     'ON_STOCK', NULL,'Outstanding #5'),

  -- 5 × rne: USED, ON_STOCK, ownership = RETS
  ('V200','CPN-300','SN-3000','RETS','USED',      'ON_STOCK', NULL,'RNE #1'),
  ('S300','CPN-301','SN-3001','RETS','USED',      'ON_STOCK', NULL,'RNE #2'),
  ('P100','CPN-302','SN-3002','RETS','USED',      'ON_STOCK', NULL,'RNE #3'),
  ('V200','CPN-303','SN-3003','RETS','USED',      'ON_STOCK', NULL,'RNE #4'),
  ('S300','CPN-304','SN-3004','RETS','USED',      'ON_STOCK', NULL,'RNE #5'),

  -- 5 × stock_corporate: NEW or REPAIRED, ON_STOCK, ownership = CORP
  ('P100','CPN-400','SN-4000','CORP','NEW',      'ON_STOCK', NULL,'Stock Corp #1'),
  ('V200','CPN-401','SN-4001','CORP','REPAIRED', 'ON_STOCK', NULL,'Stock Corp #2'),
  ('S300','CPN-402','SN-4002','CORP','NEW',      'ON_STOCK', NULL,'Stock Corp #3'),
  ('P100','CPN-403','SN-4003','CORP','REPAIRED', 'ON_STOCK', NULL,'Stock Corp #4'),
  ('V200','CPN-404','SN-4004','CORP','NEW',      'ON_STOCK', NULL,'Stock Corp #5'),

  -- 5 × rne_corporate: USED, ON_STOCK, ownership = CORP
  ('S300','CPN-500','SN-5000','CORP','USED',     'ON_STOCK', NULL,'RNE Corp #1'),
  ('P100','CPN-501','SN-5001','CORP','USED',     'ON_STOCK', NULL,'RNE Corp #2'),
  ('V200','CPN-502','SN-5002','CORP','USED',     'ON_STOCK', NULL,'RNE Corp #3'),
  ('S300','CPN-503','SN-5003','CORP','USED',     'ON_STOCK', NULL,'RNE Corp #4'),
  ('P100','CPN-504','SN-5004','CORP','USED',     'ON_STOCK', NULL,'RNE Corp #5');

-- Client-specific part numbers for item_info
INSERT INTO item_info_client_parts (item_info_id, client, client_part_number) VALUES
  (1, 'CORP',  'G2210'),
  (2, 'RETS',  'QWERTY'),
  (3, 'METCO', 'M-VALVE-02');
