DROP TABLE IF EXISTS simple_photos;
CREATE TABLE simple_photos (
  id integer PRIMARY KEY AUTOINCREMENT,
  directory text,
  filename text,
  date_time text,
  latitude real,
  longitude real
);
DROP INDEX IF EXISTS photo_directory_filename;
CREATE INDEX photo_directory_filename on simple_photos(directory, filename);
DROP INDEX IF EXISTS photo_gps;
CREATE INDEX photo_gps on simple_photos(latitude, longitude);
DROP INDEX IF EXISTS photo_org_date;
CREATE INDEX photo_org_date on simple_photos(date_time);


DROP TABLE IF EXISTS tags;
CREATE TABLE tags (
  id integer PRIMARY KEY AUTOINCREMENT,
  device_id integer,
  photo_id integer,
  diff_sec integer
);
DROP INDEX IF EXISTS tag_photo_id;
CREATE INDEX tag_photo_id on tags(photo_id);
DROP INDEX IF EXISTS tag_ids;
CREATE INDEX tag_ids on tags(device_id, photo_id, diff_sec);
DROP INDEX IF EXISTS tag_diff_sec_photo_id;
CREATE INDEX tag_diff_sec_photo_id on tags(diff_sec, photo_id);
DROP INDEX IF EXISTS tag_dif_sec_ids;
CREATE INDEX tag_dif_sec_ids on tags(diff_sec, device_id, photo_id);


DROP TABLE IF EXISTS devices;
CREATE TABLE devices (
  id integer PRIMARY KEY AUTOINCREMENT,
  address text UNIQUE,
  device_type text
);
DROP INDEX IF EXISTS device_address;
CREATE INDEX device_address on devices(address);
DROP INDEX IF EXISTS device_device_type_address;
CREATE INDEX device_device_type_address on devices(device_type, address);


DROP TABLE IF EXISTS photo_distances;
CREATE TABLE photo_distances (
  id integer PRIMARY KEY AUTOINCREMENT,
  photo_id integer,
  pair_id integer,
  time_distance real,
  geo_distance real,
  tag_distance real
);
DROP INDEX IF EXISTS photo_distances_ids;
CREATE INDEX photo_distances_ids on photo_distances(photo_id, pair_id);


DROP TABLE IF EXISTS device_nomads;
CREATE TABLE device_nomads (
  device_id integer PRIMARY KEY,
  status text
);

DROP TABLE IF EXISTS device_labels;
CREATE TABLE device_labels (
  device_id integer PRIMARY KEY,
  label text
);

DROP TABLE IF EXISTS favorite_groups;
CREATE TABLE favorite_groups (
  id integer PRIMARY KEY AUTOINCREMENT,
  label text
);

DROP TABLE IF EXISTS favorite_devices;
CREATE TABLE favorite_devices (
  id integer PRIMARY KEY AUTOINCREMENT,
  group_id integer,
  device_id integer
);


DROP TABLE IF EXISTS db_info;
CREATE TABLE db_info (
  key text PRIMARY KEY,
  value text
);
