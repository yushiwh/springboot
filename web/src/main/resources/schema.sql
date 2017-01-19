drop table BanthPerson;

CREATE TABLE `BanthPerson` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`age` INT(11) NOT NULL,
	`nation` VARCHAR(50) NOT NULL,
	`address` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB
;
