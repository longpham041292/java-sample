DROP TABLE IF EXISTS product_type;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS level;

CREATE TABLE level (
  code varchar(255) NOT NULL,
  uuid varchar(255) NOT NULL,
  PRIMARY KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE product (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  created_by varchar(255) DEFAULT NULL,
  created_date bigint(20) NOT NULL,
  last_modified_by varchar(255) DEFAULT NULL,
  last_modified_date bigint(20) DEFAULT NULL,
  uuid varchar(255) NOT NULL,
  commision double NOT NULL,
  expire_limit int(11) NOT NULL,
  num_of_sessions int(11) NOT NULL,
  promotion_price double NOT NULL,
  unit_price double NOT NULL,
  level_code varchar(255) DEFAULT NULL,
  active boolean DEFAULT TRUE,
  country varchar(255) NOT NULL,
  currency varchar(255) NOT NULL,
  PRIMARY KEY (id),
  KEY FK_product_level_code (`level_code`),
  CONSTRAINT FK_product_level_code FOREIGN KEY (level_code) REFERENCES level (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE product_type (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  created_by varchar(255) DEFAULT NULL,
  created_date bigint(20) NOT NULL,
  last_modified_by varchar(255) DEFAULT NULL,
  last_modified_date bigint(20) DEFAULT NULL,
  uuid varchar(255) NOT NULL,
  commision double NOT NULL,
  description varchar(300) DEFAULT NULL,
  unit_price double NOT NULL,
  level_code varchar(255) DEFAULT NULL,
  country varchar(255) NOT NULL,
  currency varchar(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_level_code_country (`level_code`,`country`),
  KEY FK_product_type_level_code (level_code),
  CONSTRAINT FK_product_type_level_code FOREIGN KEY (level_code) REFERENCES level (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

