CREATE TABLE `signup_otp_varification` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `otp` int(6) NOT NULL,
  `mobileno` int(20) NOT NULL,
  `isUsed` enum('Y','N') NOT NULL DEFAULT 'N',
  `otp_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1


