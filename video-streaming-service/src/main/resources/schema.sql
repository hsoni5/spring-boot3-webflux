CREATE TABLE IF NOT EXISTS FILE_META_DATA (id SERIAL PRIMARY KEY, name VARCHAR(255), type VARCHAR(255), file_path VARCHAR(255));

CREATE TABLE IF NOT EXISTS videos (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), thumbnail VARCHAR(255),duration VARCHAR(255),description VARCHAR(255),uri VARCHAR(255),created_by VARCHAR(255),last_modified_by VARCHAR(255),created_date timestamp, last_modified_date timestamp,version SERIAL);

