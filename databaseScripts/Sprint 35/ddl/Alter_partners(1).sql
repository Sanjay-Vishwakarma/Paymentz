ALTER TABLE `partners` ADD COLUMN `ip_whitelist_invoice` ENUM('Y','N') DEFAULT 'Y' NOT NULL 
ADD COLUMN `address_validation_invoice` ENUM('Y','N') DEFAULT 'N' NOT NULL ; 