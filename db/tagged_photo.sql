DROP TABLE IF EXISTS photo;
CREATE TABLE simple_photo (
  id integer PRIMARY KEY AUTOINCREMENT,
  directory text,
  filename text,
  org_date text,
  latitude real,
  longitude real
);
DROP INDEX IF EXISTS photo_directory_filename;
CREATE INDEX photo_directory_filename on photo(directory, filename);
DROP INDEX IF EXISTS photo_gps;
CREATE INDEX photo_gps on photo(latitude, longitude);
DROP INDEX IF EXISTS photo_org_date;
CREATE INDEX photo_org_date on photo(org_date);


DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
  id integer PRIMARY KEY AUTOINCREMENT,
  photo_id integer,
  address text,
  device_type text
);
DROP INDEX IF EXISTS tag_photo_id;
CREATE INDEX tag_photo_id on tag(photo_id);
DROP INDEX IF EXISTS tag_address;
CREATE INDEX tag_address on tag(address);
DROP INDEX IF EXISTS tag_device_type_address;
CREATE INDEX tag_device_type_address on tag(device_type, address);
