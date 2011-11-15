CREATE TABLE photo (
  id integer PRIMARY KEY AUTOINCREMENT,
  directory text,
  filename text,
  org_date text,
  latitude real,
  longitude real,
  width integer,
  height integer,
  file_time text,
  file_size integer,
  year integer,
  month integer,
  day integer,
  hour integer,
  minute integer,
  second integer,
  comment text
);
CREATE INDEX photo_directory_filename on photo(directory, filename);
CREATE INDEX photo_gps on photo(latitude, longitude);
CREATE INDEX photo_org_date on photo(org_date);
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
