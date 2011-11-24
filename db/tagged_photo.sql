DROP TABLE IF EXISTS photo;
CREATE TABLE photo (
  id integer PRIMARY KEY AUTOINCREMENT,
  directory text,
  filename text,
  org_date text,
  latitude real,
  longitude real,
  width integer,
  height integer,
  year integer,
  month integer,
  day integer,
  hour integer,
  minute integer,
  second integer,
);

DROP INDEX IF EXISTS photo_directory_filename;
CREATE INDEX photo_directory_filename on photo(directory, filename);
DROP INDEX IF EXISTS photo_gps;
CREATE INDEX photo_gps on photo(latitude, longitude);
DROP INDEX IF EXISTS photo_org_date;
CREATE INDEX photo_org_date on photo(org_date);
DROP INDEX IF EXISTS photo_ymdhms;
CREATE INDEX photo_ymdhms on photo(year, month, day, hour, minute, second);


DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
  id integer PRIMARY KEY AUTOINCREMENT,
  photo_id integer,
  address text,
  longitude real,
  latitude real,
  device_type text,
);
DROP INDEX IF EXISTS tag_photo_id;
CREATE INDEX tag_photo_id on tag(photo_id);
DROP INDEX IF EXISTS tag_address;
CREATE INDEX tag_address on tag(address);
DROP INDEX IF EXISTS tag_gps;
CREATE INDEX tag_gps on tag(longitude, latitude);
DROP INDEX IF EXISTS tag_device_type_address;
CREATE INDEX tag_device_type_address on tag(device_type, address);
