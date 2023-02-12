ALTER TABLE `members` ADD COLUMN `ip_whitelist_invoice` ENUM('Y','N') DEFAULT 'Y' NOT NULL ;
ALTER TABLE `members` ADD COLUMN `address_validation_invoice` ENUM('Y','N') DEFAULT 'N' NOT NULL ; 