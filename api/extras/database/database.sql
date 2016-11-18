DROP DATABASE IF EXISTS ets;
CREATE DATABASE IF NOT EXISTS ets;
USE ets;


CREATE TABLE companies (
  id         INT         NOT NULL AUTO_INCREMENT,
  name       VARCHAR(20) NOT NULL,
  code       VARCHAR(20) NOT NULL,
  username   VARCHAR(20) NOT NULL,
  password   VARCHAR(20) NOT NULL,
  is_active  TINYINT(4)  NOT NULL DEFAULT 1,
  created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (username)
);

INSERT INTO companies (name,code,username,password) VALUES ('XYZ','xyzComp','xyzUser','xyzPassword');

CREATE TABLE employees (
  id          INT         NOT NULL AUTO_INCREMENT,
  company_id  INT         NOT NULL,
  name        VARCHAR(20) NOT NULL,
  imei        VARCHAR(20) NOT NULL,
  device_hash TEXT        NOT NULL,
  fcm_id      TEXT        NOT NULL,
  api_key     VARCHAR(20) NOT NULL,
  is_active   TINYINT(4)  NOT NULL DEFAULT 1,
  created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY (api_key),
  FOREIGN KEY (company_id) REFERENCES companies (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE location_histories (
  id          INT         NOT NULL AUTO_INCREMENT,
  employee_id INT         NOT NULL,
  lat         VARCHAR(20) NOT NULL,
  lon         VARCHAR(20) NOT NULL,
  created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (employee_id) REFERENCES employees (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
