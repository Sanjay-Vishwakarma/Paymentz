ALTER TABLE `paymentz`.`invoice_config` ADD COLUMN `isSMS` ENUM('Y','N') DEFAULT 'N' NULL AFTER `GST`, ADD COLUMN `isEmail` ENUM('Y','N') DEFAULT 'Y' NULL AFTER `isSMS`, ADD COLUMN `isApp` ENUM('Y','N') DEFAULT 'N' NULL AFTER `isEmail`, ADD COLUMN `paymentterms` VARCHAR(1000) NULL AFTER `isApp`, ADD COLUMN `isduedate` ENUM('Y','N') DEFAULT 'N' NULL AFTER `paymentterms`, ADD COLUMN `duedate` BIGINT(100) NULL AFTER `isduedate`, ADD COLUMN `islatefee` ENUM('Y','N') DEFAULT 'N' NULL AFTER `duedate`, ADD COLUMN `latefee` BIGINT(100) NULL AFTER `islatefee`, ADD COLUMN `unit` VARCHAR(20) NULL AFTER `latefee`; 



ALTER TABLE `paymentz`.`invoice_config` ADD COLUMN `loaddefaultproductlist` ENUM('Y','N') DEFAULT 'N' NULL AFTER `unit`;