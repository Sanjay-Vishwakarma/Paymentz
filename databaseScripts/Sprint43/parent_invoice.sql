ALTER TABLE `paymentz`.`invoice` ADD COLUMN `parentinvoiceno` INT(11) NOT NULL AFTER `invoiceno`; 


CREATE TABLE `parent_invoice` (
  `invoiceno` int(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(15,2) NOT NULL,
  `memberid` int(11) NOT NULL,
  `currency` varchar(4) DEFAULT NULL,
  `redirecturl` varchar(255) DEFAULT NULL,
  `orderid` varchar(255) NOT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dtstamp` int(11) NOT NULL,
  `taxamount` varchar(255) DEFAULT '0.00',
  `raisedby` varchar(255) DEFAULT NULL,
  KEY `invoiceno` (`invoiceno`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1
