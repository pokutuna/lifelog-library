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
  photo_id integer,
  address text,
  device_type text
);
DROP INDEX IF EXISTS tag_photo_id;
CREATE INDEX tag_photo_id on tags(photo_id);
DROP INDEX IF EXISTS tag_address;
CREATE INDEX tag_address on tags(address);
DROP INDEX IF EXISTS tag_device_type_address;
CREATE INDEX tag_device_type_address on tags(device_type, address);
