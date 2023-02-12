DROP TABLE IF EXISTS `gateway_accounts_clearsettle`;

CREATE TABLE `gateway_accounts_clearsettle` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `3d_secure_account` enum('N','Y') NOT NULL DEFAULT 'N',
  `only3d_secure_account` enum('N','Y') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Data for the table `gateway_accounts_clearsettle` */

insert  into `gateway_accounts_clearsettle`(`id`,`accountid`,`3d_secure_account`,`only3d_secure_account`) values (1,2669,'Y','N'),(2,2670,'N','N'),(3,2672,'N','N');



