DROP TABLE IF EXISTS bt_detected;
CREATE TABLE bt_detected (
  address text,
  date_time text,
  file_id integer
);
DROP INDEX IF EXISTS bt_detected_address_date_time;
CREATE INDEX bt_detected_address_date_time on bt_detected(address, date_time);
DROP INDEX IF EXISTS bt_detected_date_time;
CREATE INDEX bt_detected_date_time on bt_detected(date_time);

DROP TABLE IF EXISTS bt_devices;
CREATE TABLE bt_devices (
  address text unique,
  name text
);
DROP INDEX IF EXISTS bt_devices_address;
CREATE INDEX bt_devices_address on bt_devices(address);

DROP TABLE IF EXISTS wifi_detected;
CREATE TABLE wifi_detected (
  address text,
  date_time text,
  strength integer,
  file_id integer
);
DROP INDEX IF EXISTS wifi_detected_address_datetime;
CREATE INDEX wifi_detected_address_datetime on wifi_detected(address, date_time);
DROP INDEX IF EXISTS wifi_detected_date_time;
CREATE INDEX wifi_detected_date_time on wifi_detected(date_time);

DROP TABLE IF EXISTS wifi_devices;
CREATE TABLE wifi_devices (
  address text unique,
  name text
);
DROP INDEX IF EXISTS wifi_devices_address;
CREATE INDEX wifi_devices_address on wifi_devices(address);

DROP TABLE IF EXISTS registered_files;
CREATE TABLE registered_files (
  file_id integer PRIMARY KEY AUTOINCREMENT,
  file_name text,
  md5_hex text
);

-- * original table *
-- CREATE TABLE registered_files (
--   file_id integer primary key,
--   file_name text unique,
--   file_time text,
--   file_size integer
-- );

-- * Not used tables *
-- CREATE TABLE events (
--   date_time text,
--   event_name text,
--   file_id integer
-- );
-- CREATE TABLE locations (
--   latitude real,
--   longitude real,
--   date_time text,
--   file_id integer
-- );
