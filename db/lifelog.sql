DROP TABLE IF EXISTS photo;
CREATE TABLE photo (
  directory text,
  filename text,
  org_date text,
  latitude real,
  longitude real,
  width integer,
  height integer,
  file_size integer,
  year integer,
  month integer,
  day integer,
  hour integer,
  minute integer,
  second integer,
  comment text
);
DROP INDEX IF EXISTS photo_directory_filename;
CREATE INDEX photo_directory_filename on photo(directory, filename);
DROP INDEX IF EXISTS photo_gps;
CREATE INDEX photo_gps on photo(latitude, longitude);
DROP INDEX IF EXISTS photo_org_date;
CREATE INDEX photo_org_date on photo(org_date);
DROP INDEX IF EXISTS photo_ymdhms;
CREATE INDEX photo_ymdhms on photo(year, month, day, hour, minute, second);

-- * original photo table *
-- CREATE TABLE photo (
--   directory text,
--   filename text,
--   org_date text,
--   latitude real,
--   longitude real,
--   width integer,
--   height integer,
--   file_time text,
--   file_size integer,
--   year integer,
--   month integer,
--   day integer,
--   hour integer,
--   minute integer,
--   second integer,
--   like integer,
--   good integer,
--   private integer,
--   face integer,
--   photomode integer,
--   isospeed integer,
--   shutterspeed integer,
--   brightness integer,
--   flash integer,
--   aperture integer,
--   comment text
-- );

-- * Not used tables *
-- CREATE TABLE directory_table (
--   directory text,
--   file_time text,
--   number_of_file integer
-- );
--
-- CREATE TABLE info_table (version text);
