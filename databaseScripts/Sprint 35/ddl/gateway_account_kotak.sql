CREATE TABLE `gateway_accounts_kotak` (
  `id` int(11) NOT NULL,
  `accountid` int(11) NOT NULL,
  `merchantname` varchar(255) NOT NULL,
  `terminalid` varchar(255) NOT NULL,
  `passcode` varchar(255) NOT NULL,
  `securesecret` varchar(255) NOT NULL,
  `enckey` varchar(255) NOT NULL,
  `mcc` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
