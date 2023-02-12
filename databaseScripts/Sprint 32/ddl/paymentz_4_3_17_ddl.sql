/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.5.49-37.9 : Database - paymentz
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`paymentz` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `paymentz`;

/*Table structure for table `ChargeVersionGatewayMaster` */

DROP TABLE IF EXISTS `ChargeVersionGatewayMaster`;

CREATE TABLE `ChargeVersionGatewayMaster` (
  `chargeversionId` int(10) NOT NULL AUTO_INCREMENT,
  `gatewayChargeValue` decimal(9,2) NOT NULL DEFAULT '0.00',
  `agentCommision` decimal(9,2) NOT NULL DEFAULT '0.00',
  `partnerCommision` decimal(9,2) NOT NULL DEFAULT '0.00',
  `effectiveStartDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `effectiveEndDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `gateway_accounts_charges_mapping_id` int(10) NOT NULL,
  PRIMARY KEY (`chargeversionId`)
) ENGINE=MyISAM AUTO_INCREMENT=88 DEFAULT CHARSET=latin1;

/*Table structure for table `ChargeVersionMemberMaster` */

DROP TABLE IF EXISTS `ChargeVersionMemberMaster`;

CREATE TABLE `ChargeVersionMemberMaster` (
  `chargeversionId` float NOT NULL AUTO_INCREMENT,
  `merchantChargeValue` decimal(9,2) NOT NULL,
  `agentCommision` decimal(10,2) NOT NULL,
  `partnerCommision` decimal(9,2) NOT NULL,
  `effectiveStartDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `effectiveEndDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `member_accounts_charges_mapping_id` int(10) NOT NULL,
  PRIMARY KEY (`chargeversionId`)
) ENGINE=MyISAM AUTO_INCREMENT=188 DEFAULT CHARSET=latin1;

/*Table structure for table `account_rule_mapping` */

DROP TABLE IF EXISTS `account_rule_mapping`;

CREATE TABLE `account_rule_mapping` (
  `accountruleid` int(10) NOT NULL AUTO_INCREMENT,
  `fsaccountid` int(10) NOT NULL,
  `ruleid` int(10) NOT NULL,
  `score` int(10) NOT NULL,
  `status` enum('Enable','Disable') NOT NULL DEFAULT 'Enable',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`accountruleid`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;

/*Table structure for table `action_history` */

DROP TABLE IF EXISTS `action_history`;

CREATE TABLE `action_history` (
  `actionid` int(11) NOT NULL AUTO_INCREMENT,
  `icicitransid` varchar(255) NOT NULL,
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `action` varchar(255) NOT NULL,
  `status` varchar(50) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`actionid`),
  KEY `icicitransid` (`icicitransid`),
  KEY `actionid` (`actionid`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `admin` */

DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `adminid` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL DEFAULT '',
  `passwd` varchar(255) NOT NULL DEFAULT '',
  `fpasswd` varchar(255) NOT NULL DEFAULT '',
  `contact_emails` varchar(255) NOT NULL DEFAULT '',
  `fdtstamp` int(11) DEFAULT '0',
  `accid` bigint(20) NOT NULL,
  PRIMARY KEY (`adminid`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `admin_modules_mapping` */

DROP TABLE IF EXISTS `admin_modules_mapping`;

CREATE TABLE `admin_modules_mapping` (
  `mappingid` int(10) NOT NULL AUTO_INCREMENT,
  `adminid` int(10) NOT NULL,
  `moduleid` int(10) NOT NULL,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=latin1;

/*Table structure for table `admin_modules_master` */

DROP TABLE IF EXISTS `admin_modules_master`;

CREATE TABLE `admin_modules_master` (
  `moduleid` int(10) NOT NULL AUTO_INCREMENT,
  `modulename` varchar(255) NOT NULL DEFAULT '',
  `modulecreationtime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`moduleid`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

/*Table structure for table `agent` */

DROP TABLE IF EXISTS `agent`;

CREATE TABLE `agent` (
  `aid` int(7) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(100) NOT NULL DEFAULT '',
  `user_password` varchar(100) NOT NULL DEFAULT '',
  `rid` int(7) NOT NULL DEFAULT '0',
  `name1` varchar(100) NOT NULL DEFAULT '',
  `name2` varchar(100) NOT NULL DEFAULT '',
  `company1` varchar(100) NOT NULL DEFAULT '',
  `company2` varchar(100) NOT NULL DEFAULT '',
  `address1` varchar(255) NOT NULL DEFAULT '',
  `address2` varchar(255) NOT NULL DEFAULT '',
  `city` varchar(64) NOT NULL DEFAULT '',
  `state` varchar(64) NOT NULL DEFAULT '',
  `country` varchar(64) NOT NULL DEFAULT '',
  `zipcode` varchar(32) NOT NULL DEFAULT '',
  `phone1` varchar(30) NOT NULL DEFAULT '',
  `phone2` varchar(30) NOT NULL DEFAULT '',
  `mobile1` varchar(30) NOT NULL DEFAULT '',
  `mobile2` varchar(30) NOT NULL DEFAULT '',
  `email1` varchar(200) NOT NULL DEFAULT '',
  `email2` varchar(200) NOT NULL DEFAULT '',
  `agent_description` text NOT NULL,
  `flag` char(1) NOT NULL DEFAULT '',
  `exec` char(1) NOT NULL DEFAULT '',
  `dot` date DEFAULT '0000-00-00',
  PRIMARY KEY (`aid`),
  UNIQUE KEY `idxdetails` (`user_name`)
) ENGINE=MyISAM AUTO_INCREMENT=58 DEFAULT CHARSET=latin1;

/*Table structure for table `agent_commission` */

DROP TABLE IF EXISTS `agent_commission`;

CREATE TABLE `agent_commission` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `agentid` int(10) NOT NULL,
  `memberid` int(10) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `chargeid` int(10) NOT NULL,
  `commission_value` decimal(7,2) NOT NULL DEFAULT '0.00',
  `startdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `enddate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `sequence_no` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=latin1;

/*Table structure for table `agent_wiremanager` */

DROP TABLE IF EXISTS `agent_wiremanager`;

CREATE TABLE `agent_wiremanager` (
  `settledid` int(10) NOT NULL AUTO_INCREMENT,
  `settledate` timestamp NULL DEFAULT NULL,
  `settlementstartdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `settlementenddate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `agenttype` varchar(255) NOT NULL,
  `agentchargeamount` double(9,2) NOT NULL DEFAULT '0.00',
  `agentunpaidamount` double(9,2) NOT NULL DEFAULT '0.00',
  `agenttotalfundedamount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(5) NOT NULL,
  `status` varchar(25) NOT NULL,
  `settlementreportfilename` varchar(255) NOT NULL,
  `markedfordeletion` enum('Y','N') NOT NULL DEFAULT 'N',
  `agentid` int(10) NOT NULL,
  `wirecreationtime` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `toid` varchar(255) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `accountid` int(10) NOT NULL,
  `paymodeid` int(10) NOT NULL,
  `cardtypeid` int(10) NOT NULL,
  `declinedcoverdateupto` timestamp NULL DEFAULT NULL,
  `reversedcoverdateupto` timestamp NULL DEFAULT NULL,
  `chargebackcoverdateupto` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`settledid`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

/*Table structure for table `agents` */

DROP TABLE IF EXISTS `agents`;

CREATE TABLE `agents` (
  `agentId` int(4) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL DEFAULT '',
  `passwd` varchar(255) NOT NULL DEFAULT '',
  `fpasswd` varchar(255) NOT NULL DEFAULT '',
  `fdtstamp` int(11) NOT NULL DEFAULT '0',
  `clkey` varchar(100) NOT NULL DEFAULT '',
  `activation` varchar(5) NOT NULL DEFAULT 'T',
  `template` char(3) NOT NULL DEFAULT 'N',
  `agentName` varchar(255) NOT NULL,
  `contact_persons` varchar(255) DEFAULT NULL,
  `contact_emails` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `telno` varchar(255) DEFAULT NULL,
  `faxno` varchar(255) DEFAULT NULL,
  `logoName` varchar(255) DEFAULT NULL,
  `notifyemail` varchar(255) NOT NULL DEFAULT '',
  `haspaid` enum('Y','N') NOT NULL DEFAULT 'N',
  `isAgentInterface` enum('Y','N') NOT NULL DEFAULT 'N',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `siteurl` varchar(255) DEFAULT NULL,
  `supporturl` varchar(255) DEFAULT NULL,
  `salesemail` varchar(255) DEFAULT NULL,
  `billingemail` varchar(255) DEFAULT NULL,
  `superadminid` int(3) DEFAULT NULL,
  `partnerId` int(4) NOT NULL DEFAULT '1',
  `isIpWhitelisted` enum('Y','N') NOT NULL DEFAULT 'N',
  `accid` bigint(20) NOT NULL,
  `maincontact_ccmailid` varchar(255) DEFAULT NULL,
  `maincontact_phone` varchar(255) DEFAULT NULL,
  `cbcontact_name` varchar(255) DEFAULT NULL,
  `cbcontact_mailid` varchar(255) DEFAULT NULL,
  `cbcontact_ccmailid` varchar(255) DEFAULT NULL,
  `cbcontact_phone` varchar(255) DEFAULT NULL,
  `refundcontact_name` varchar(255) DEFAULT NULL,
  `refundcontact_mailid` varchar(255) DEFAULT NULL,
  `refundcontact_ccmailid` varchar(255) DEFAULT NULL,
  `refundcontact_phone` varchar(255) DEFAULT NULL,
  `salescontact_name` varchar(255) DEFAULT NULL,
  `salescontact_ccmailid` varchar(255) DEFAULT NULL,
  `salescontact_phone` varchar(255) DEFAULT NULL,
  `fraudcontact_name` varchar(255) DEFAULT NULL,
  `fraudcontact_mailid` varchar(255) DEFAULT NULL,
  `fraudcontact_ccmailid` varchar(255) DEFAULT NULL,
  `fraudcontact_phone` varchar(255) DEFAULT NULL,
  `technicalcontact_name` varchar(255) DEFAULT NULL,
  `technicalcontact_mailid` varchar(255) DEFAULT NULL,
  `technicalcontact_ccmailid` varchar(255) DEFAULT NULL,
  `technicalcontact_phone` varchar(255) DEFAULT NULL,
  `billingcontact_ccmailid` varchar(255) DEFAULT NULL,
  `billingcontact_phone` varchar(255) DEFAULT NULL,
  `billingcontact_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`agentId`),
  UNIQUE KEY `agentName` (`agentName`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;

/*Table structure for table `application_manager` */

DROP TABLE IF EXISTS `application_manager`;

CREATE TABLE `application_manager` (
  `application_id` int(20) NOT NULL AUTO_INCREMENT,
  `member_id` int(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `MAF_status` varchar(255) DEFAULT NULL,
  `KYC_status` varchar(255) DEFAULT NULL,
  `isapplicationsaved` enum('Y','N') NOT NULL DEFAULT 'Y',
  `appliedToModify` enum('Y','N') NOT NULL DEFAULT 'N',
  `maf_user` varchar(100) DEFAULT NULL,
  `speed_status` varchar(50) DEFAULT NULL,
  `speed_user` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=218 DEFAULT CHARSET=latin1;

/*Table structure for table `application_preference` */

DROP TABLE IF EXISTS `application_preference`;

CREATE TABLE `application_preference` (
  `apid` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `value` blob,
  `creationdt` int(11) DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`apid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `applicationmanager_bankprofile` */

DROP TABLE IF EXISTS `applicationmanager_bankprofile`;

CREATE TABLE `applicationmanager_bankprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) NOT NULL,
  `currencyrequested_productssold` varchar(255) DEFAULT NULL,
  `currencyrequested_bankaccount` varchar(255) DEFAULT NULL,
  `bankinfo_bic` varchar(255) DEFAULT NULL,
  `bankinfo_bank_name` varchar(255) DEFAULT NULL,
  `bankinfo_bankaddress` varchar(255) DEFAULT NULL,
  `bankinfo_bankphonenumber` varchar(255) DEFAULT NULL,
  `bankinfo_aba_routingcode` varchar(255) DEFAULT NULL,
  `bankinfo_accountholder` varchar(255) DEFAULT NULL,
  `salesvolume_lastmonth` int(20) DEFAULT NULL,
  `salesvolume_2monthsago` int(20) DEFAULT NULL,
  `salesvolume_3monthsago` int(20) DEFAULT NULL,
  `salesvolume_4monthsago` int(20) DEFAULT NULL,
  `salesvolume_5monthsago` int(20) DEFAULT NULL,
  `salesvolume_6monthsago` int(20) DEFAULT NULL,
  `numberoftransactions_lastmonth` int(20) DEFAULT NULL,
  `numberoftransactions_2monthsago` int(20) DEFAULT NULL,
  `numberoftransactions_3monthsago` int(20) DEFAULT NULL,
  `numberoftransactions_4monthsago` int(20) DEFAULT NULL,
  `numberoftransactions_5monthsago` int(20) DEFAULT NULL,
  `numberoftransactions_6monthsago` int(20) DEFAULT NULL,
  `chargebackvolume_lastmonth` int(20) DEFAULT NULL,
  `chargebackvolume_2monthsago` int(20) DEFAULT NULL,
  `chargebackvolume_3monthsago` int(20) DEFAULT NULL,
  `chargebackvolume_4monthsago` int(20) DEFAULT NULL,
  `chargebackvolume_5monthsago` int(20) DEFAULT NULL,
  `chargebackvolume_6monthsago` int(20) DEFAULT NULL,
  `numberofchargebacks_lastmonth` int(20) DEFAULT NULL,
  `numberofchargebacks_2monthsago` int(20) DEFAULT NULL,
  `numberofchargebacks_3monthsago` int(20) DEFAULT NULL,
  `numberofchargebacks_4monthsago` int(20) DEFAULT NULL,
  `numberofchargebacks_5monthsago` int(20) DEFAULT NULL,
  `numberofchargebacks_6monthsago` int(20) DEFAULT NULL,
  `chargebackratio_lastmonth` varchar(20) DEFAULT NULL,
  `chargebackratio_2monthsago` varchar(20) DEFAULT NULL,
  `chargebackratio_3monthsago` varchar(20) DEFAULT NULL,
  `chargebackratio_4monthsago` varchar(20) DEFAULT NULL,
  `chargebackratio_5monthsago` varchar(20) DEFAULT NULL,
  `chargebackratio_6monthsago` varchar(20) DEFAULT NULL,
  `refundsvolume_lastmonth` int(20) DEFAULT NULL,
  `refundsvolume_2monthsago` int(20) DEFAULT NULL,
  `refundsvolume_3monthsago` int(20) DEFAULT NULL,
  `refundsvolume_4monthsago` int(20) DEFAULT NULL,
  `refundsvolume_5monthsago` int(20) DEFAULT NULL,
  `refundsvolume_6monthsago` int(20) DEFAULT NULL,
  `numberofrefunds_lastmonth` int(20) DEFAULT NULL,
  `numberofrefunds_2monthsago` int(20) DEFAULT NULL,
  `numberofrefunds_3monthsago` int(20) DEFAULT NULL,
  `numberofrefunds_4monthsago` int(20) DEFAULT NULL,
  `numberofrefunds_5monthsago` int(20) DEFAULT NULL,
  `numberofrefunds_6monthsago` int(20) DEFAULT NULL,
  `refundratio_lastmonth` varchar(20) DEFAULT NULL,
  `refundratio_2monthsago` varchar(20) DEFAULT NULL,
  `refundratio_3monthsago` varchar(20) DEFAULT NULL,
  `refundratio_4monthsago` varchar(20) DEFAULT NULL,
  `refundratio_5monthsago` varchar(20) DEFAULT NULL,
  `refundratio_6monthsago` varchar(20) DEFAULT NULL,
  `currency_products_INR` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_USD` enum('Y','N') NOT NULL DEFAULT 'Y',
  `currency_products_EUR` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_GBP` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_JPY` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_PEN` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_INR` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_USD` enum('Y','N') NOT NULL DEFAULT 'Y',
  `currency_payments_EUR` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_GBP` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_JPY` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_PEN` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_HKD` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_AUD` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_CAD` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_DKK` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_SEK` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_products_NOK` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_HKD` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_AUD` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_CAD` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_DKK` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_SEK` enum('Y','N') NOT NULL DEFAULT 'N',
  `currency_payments_NOK` enum('Y','N') NOT NULL DEFAULT 'N',
  `aquirer` varchar(255) DEFAULT NULL,
  `reason_aquirer` varchar(255) DEFAULT NULL,
  `bankcontactperson` varchar(255) DEFAULT NULL,
  `isbankprofilesaved` enum('Y','N') NOT NULL DEFAULT 'Y',
  `bank_accountnumber_IBAN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bankprofile_uniquekey` (`application_id`),
  CONSTRAINT `applicationmanager_bankprofile_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=latin1;

/*Table structure for table `applicationmanager_businessprofile` */

DROP TABLE IF EXISTS `applicationmanager_businessprofile`;

CREATE TABLE `applicationmanager_businessprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) NOT NULL,
  `foreigntransactions_us` int(20) DEFAULT NULL,
  `foreigntransactions_Europe` int(20) DEFAULT NULL,
  `foreigntransactions_Asia` int(20) DEFAULT NULL,
  `foreigntransactions_cis` int(20) DEFAULT NULL,
  `foreigntransactions_canada` int(20) DEFAULT NULL,
  `foreigntransactions_RestoftheWorld` int(20) DEFAULT NULL,
  `methodofacceptance_moto` int(20) DEFAULT NULL,
  `methodofacceptance_internet` int(20) unsigned DEFAULT NULL,
  `methodofacceptance_swipe` int(20) DEFAULT NULL,
  `averageticket` varchar(255) DEFAULT NULL,
  `highestticket` varchar(255) DEFAULT NULL,
  `urls` varchar(255) DEFAULT NULL,
  `descriptor_creditcardstmt` varchar(255) DEFAULT NULL,
  `descriptionofproducts` varchar(255) DEFAULT NULL,
  `recurringservices` enum('Y','N') NOT NULL DEFAULT 'N',
  `recurringservicesyes` varchar(255) DEFAULT NULL,
  `isacallcenterused` enum('Y','N') NOT NULL DEFAULT 'N',
  `isacallcenterusedyes` varchar(255) DEFAULT NULL,
  `isafulfillmenthouseused` enum('Y','N') NOT NULL DEFAULT 'N',
  `isafulfillmenthouseused_yes` varchar(255) DEFAULT NULL,
  `cardtypesaccepted_visa` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_mastercard` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_americanexpress` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_discover` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_diners` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_jcb` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_other` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardtypesaccepted_other_yes` varchar(255) DEFAULT NULL,
  `kyc_processes` enum('Y','N') NOT NULL DEFAULT 'N',
  `visa_master_cardlogos` enum('Y','N') NOT NULL DEFAULT 'N',
  `threeD_secure_compulsory` enum('Y','N') NOT NULL DEFAULT 'N',
  `price_displayed` enum('Y','N') NOT NULL DEFAULT 'N',
  `transaction_currency` enum('Y','N') NOT NULL DEFAULT 'N',
  `cardholder_asked` enum('Y','N') NOT NULL DEFAULT 'N',
  `dynamic_descriptors` enum('Y','N') NOT NULL DEFAULT 'N',
  `shopping_cart` enum('Y','N') NOT NULL DEFAULT 'N',
  `shopping_cart_details` varchar(255) DEFAULT NULL,
  `pricing_policies_website` enum('Y','N') NOT NULL DEFAULT 'N',
  `fulfillment_timeframe` enum('Y','N') NOT NULL DEFAULT 'N',
  `goods_policy` enum('Y','N') NOT NULL DEFAULT 'N',
  `MCC_Ctegory` enum('Y','N') NOT NULL DEFAULT 'N',
  `countries_blocked` enum('Y','N') NOT NULL DEFAULT 'N',
  `countries_blocked_details` varchar(255) DEFAULT NULL,
  `customer_support` enum('Y','N') NOT NULL DEFAULT 'N',
  `affiliate_programs` enum('Y','N') NOT NULL DEFAULT 'N',
  `affiliate_programs_details` varchar(255) DEFAULT NULL,
  `listfraudtools` varchar(255) DEFAULT NULL,
  `customers_identification` varchar(255) DEFAULT NULL,
  `coolingoffperiod` varchar(255) DEFAULT NULL,
  `customersupport_email` varchar(255) DEFAULT NULL,
  `custsupportwork_hours` varchar(255) DEFAULT NULL,
  `technical_contact` varchar(255) DEFAULT NULL,
  `securitypolicy` enum('Y','N') NOT NULL DEFAULT 'N',
  `confidentialitypolicy` enum('Y','N') NOT NULL DEFAULT 'N',
  `applicablejurisdictions` enum('Y','N') NOT NULL DEFAULT 'N',
  `privacy_anonymity_dataprotection` enum('Y','N') NOT NULL DEFAULT 'N',
  `App_Services` enum('Y','N') NOT NULL DEFAULT 'N',
  `product_requires` enum('Y','N') NOT NULL DEFAULT 'N',
  `lowestticket` varchar(255) DEFAULT NULL,
  `login_id` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `companyidentifiable` enum('Y','N') DEFAULT 'N',
  `clearlypresented` enum('Y','N') DEFAULT 'N',
  `trackingnumber` enum('Y','N') DEFAULT 'N',
  `domainsowned` enum('Y','N') DEFAULT 'N',
  `sslsecured` enum('Y','N') DEFAULT 'N',
  `copyright` enum('Y','N') DEFAULT 'N',
  `sourcecontent` varchar(255) DEFAULT NULL,
  `directmail` enum('Y','N') DEFAULT 'N',
  `Yellowpages` enum('Y','N') DEFAULT 'N',
  `radiotv` enum('Y','N') DEFAULT 'N',
  `internet` enum('Y','N') DEFAULT 'N',
  `networking` enum('Y','N') DEFAULT 'N',
  `outboundtelemarketing` enum('Y','N') DEFAULT 'N',
  `inhouselocation` varchar(255) DEFAULT NULL,
  `contactperson` varchar(255) DEFAULT NULL,
  `otherlocation` varchar(255) DEFAULT NULL,
  `mainsuppliers` varchar(255) DEFAULT NULL,
  `shipmentassured` enum('Y','N') DEFAULT 'N',
  `billing_model` enum('recurring','one_time') NOT NULL DEFAULT 'one_time',
  `billing_timeframe` enum('daily','weekly','monthly','quaterly','yearly') DEFAULT NULL,
  `recurring_amount` varchar(255) DEFAULT NULL,
  `automatic_recurring` enum('Y','N') DEFAULT 'N',
  `multiple_membership` enum('Y','N') DEFAULT 'N',
  `free_membership` enum('Y','N') DEFAULT 'N',
  `creditcard_Required` enum('Y','N') DEFAULT 'N',
  `automatically_billed` enum('Y','N') DEFAULT 'N',
  `pre_authorization` enum('Y','N') DEFAULT 'N',
  `isbuisnessprofilesaved` enum('Y','N') NOT NULL DEFAULT 'Y',
  `timeframe` varchar(255) DEFAULT NULL,
  `livechat` enum('Y','N') NOT NULL DEFAULT 'N',
  `merchantcode` varchar(255) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `businessprofile_uniquekey` (`application_id`),
  CONSTRAINT `applicationmanager_businessprofile_ibfk_2` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARSET=latin1;

/*Table structure for table `applicationmanager_cardholderprofile` */

DROP TABLE IF EXISTS `applicationmanager_cardholderprofile`;

CREATE TABLE `applicationmanager_cardholderprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) NOT NULL,
  `compliance_swapp` enum('Y','N') NOT NULL DEFAULT 'N',
  `compliance_thirdpartyappform` varchar(255) DEFAULT NULL,
  `compliance_thirdpartysoft` varchar(255) DEFAULT NULL,
  `compliance_version` varchar(255) DEFAULT NULL,
  `compliance_companiesorgateways` enum('Y','N') NOT NULL DEFAULT 'N',
  `compliance_companiesorgateways_yes` varchar(255) DEFAULT NULL,
  `compliance_electronically` enum('Y','N') NOT NULL DEFAULT 'N',
  `compliance_carddatastored` enum('Merchant','ThirdParty','Both') NOT NULL DEFAULT 'Both',
  `compliance_pcidsscompliant` enum('Y','N') NOT NULL DEFAULT 'N',
  `compliance_qualifiedsecurityassessor` varchar(255) DEFAULT NULL,
  `compliance_dateofcompliance` datetime DEFAULT NULL,
  `compliance_dateoflastscan` datetime DEFAULT NULL,
  `compliance_datacompromise` enum('Y','N') NOT NULL DEFAULT 'N',
  `compliance_datacompromise_yes` varchar(255) DEFAULT NULL,
  `siteinspection_merchant` enum('Y','N') NOT NULL DEFAULT 'N',
  `siteinspection_landlord` varchar(255) DEFAULT NULL,
  `siteinspection_buildingtype` enum('ShoppingCtr','OfficeBldg','IndustrialBldg','Residence') NOT NULL DEFAULT 'ShoppingCtr',
  `siteinspection_areazoned` enum('Commercial','Industrial','Residential') NOT NULL DEFAULT 'Commercial',
  `siteinspection_squarefootage` enum('0-500','501-2500','2501-5000','5001-10000') NOT NULL DEFAULT '0-500',
  `siteinspection_operatebusiness` enum('Y','N') NOT NULL DEFAULT 'N',
  `siteinspection_principal1` varchar(255) DEFAULT NULL,
  `siteinspection_principal1_date` datetime DEFAULT NULL,
  `siteinspection_principal2` varchar(255) DEFAULT NULL,
  `siteinspection_principal2_date` datetime DEFAULT NULL,
  `iscardholderprofilesaved` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`),
  UNIQUE KEY `cardholderprofile_uniquekey` (`application_id`),
  CONSTRAINT `applicationmanager_cardholderprofile_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=latin1;

/*Table structure for table `applicationmanager_companyprofile` */

DROP TABLE IF EXISTS `applicationmanager_companyprofile`;

CREATE TABLE `applicationmanager_companyprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) DEFAULT NULL,
  `merchantname` varchar(255) DEFAULT 'NULL',
  `corporatename` varchar(255) DEFAULT 'NULL',
  `locationaddress` varchar(255) DEFAULT NULL,
  `corporateaddress` varchar(255) DEFAULT NULL,
  `merchantcity` varchar(255) DEFAULT NULL,
  `merchantstate` varchar(255) DEFAULT NULL,
  `merchantzipcode` varchar(255) DEFAULT NULL,
  `merchantcountry` varchar(255) DEFAULT NULL,
  `corporatecity` varchar(255) DEFAULT NULL,
  `corporatestate` varchar(255) DEFAULT NULL,
  `corporatezipcode` varchar(255) DEFAULT NULL,
  `corporatecountry` varchar(255) DEFAULT NULL,
  `contactname` varchar(255) DEFAULT NULL,
  `contactemailaddress` varchar(255) DEFAULT NULL,
  `technicalcontactname` varchar(255) DEFAULT NULL,
  `technicalemailaddress` varchar(255) DEFAULT NULL,
  `contactname_telnocc1` int(20) DEFAULT NULL,
  `contactname_telephonenumber` varchar(20) DEFAULT NULL,
  `contact_faxnumber` varchar(20) DEFAULT NULL,
  `billingcontactname` varchar(255) DEFAULT NULL,
  `billingemailaddress` varchar(255) DEFAULT NULL,
  `countryofregistration` varchar(255) DEFAULT NULL,
  `companyregistrationnumber` varchar(255) DEFAULT NULL,
  `vatidentification` varchar(255) DEFAULT NULL,
  `company_registered_eu` enum('Y','N') DEFAULT 'N',
  `company_bankruptcy` enum('Y','N') DEFAULT 'N',
  `company_bankruptcydate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `company_typeofbusiness` enum('Corporation','LimitedLiabilityCompany','SoleProprietor','Partnership','NotforProfit') NOT NULL DEFAULT 'NotforProfit',
  `registered_corporatename` varchar(255) DEFAULT NULL,
  `registered_directors` varchar(255) DEFAULT NULL,
  `registered_directors_address` varchar(255) DEFAULT NULL,
  `registered_directors_city` varchar(255) DEFAULT NULL,
  `registered_directors_State` varchar(255) DEFAULT NULL,
  `registered_directors_postalcode` varchar(20) DEFAULT NULL,
  `registered_directors_country` varchar(255) DEFAULT NULL,
  `company_lengthoftime_business` timestamp NULL DEFAULT NULL,
  `company_capitalresources` varchar(255) DEFAULT NULL,
  `company_currencylastyear` varchar(255) DEFAULT NULL,
  `company_turnoverlastyear` varchar(255) DEFAULT NULL,
  `company_turnoverlastyear_unit` varchar(255) DEFAULT NULL,
  `company_numberofemployees` varchar(255) DEFAULT NULL,
  `Technical_telephonenumber` varchar(20) DEFAULT NULL,
  `Financial_telephonenumber` varchar(20) DEFAULT NULL,
  `Company_Date_Registration` datetime DEFAULT NULL,
  `License_required` enum('Y','N') NOT NULL DEFAULT 'N',
  `License_Permission` enum('Y','N') NOT NULL DEFAULT 'N',
  `technicalphonecc1` int(20) DEFAULT NULL,
  `financialphonecc1` int(20) DEFAULT NULL,
  `Companyphonecc1` int(20) DEFAULT NULL,
  `CompanyTelephoneNO` varchar(20) DEFAULT NULL,
  `CompanyFax` varchar(255) DEFAULT NULL,
  `CompanyEmailAddress` varchar(255) DEFAULT NULL,
  `FederalTaxID` varchar(255) DEFAULT NULL,
  `EURegistrationNumber` varchar(255) DEFAULT NULL,
  `SkypeIMaddress` varchar(255) DEFAULT NULL,
  `legal_proceeding` enum('Y','N') DEFAULT 'N',
  `iscompanyprofilesaved` enum('Y','N') DEFAULT 'Y',
  PRIMARY KEY (`id`),
  UNIQUE KEY `companyprofile_uniquekey` (`application_id`),
  CONSTRAINT `applicationmanager_companyprofile_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=214 DEFAULT CHARSET=latin1;

/*Table structure for table `applicationmanager_extradetailsprofile` */

DROP TABLE IF EXISTS `applicationmanager_extradetailsprofile`;

CREATE TABLE `applicationmanager_extradetailsprofile` (
  `extraprofile_id` int(30) NOT NULL AUTO_INCREMENT,
  `application_id` int(30) NOT NULL,
  `company_financialreport` enum('Y','N') NOT NULL DEFAULT 'N',
  `company_financialreportyes` varchar(255) DEFAULT NULL,
  `financialreport_institution` varchar(255) DEFAULT NULL,
  `financialreport_available` enum('Y','N') NOT NULL DEFAULT 'N',
  `financialreport_availableyes` varchar(255) DEFAULT NULL,
  `ownersince` datetime DEFAULT NULL,
  `socialsecurity` varchar(255) DEFAULT NULL,
  `company_formparticipation` varchar(255) DEFAULT NULL,
  `financialobligation` varchar(255) DEFAULT NULL,
  `compliance_punitivesanction` enum('Y','N') NOT NULL DEFAULT 'N',
  `compliance_punitivesanctionyes` varchar(255) DEFAULT NULL,
  `workingexperience` varchar(255) DEFAULT NULL,
  `goodsinsuranceoffered` enum('Y','N') NOT NULL DEFAULT 'N',
  `fulfillment_productemail` enum('Y','N') NOT NULL DEFAULT 'N',
  `fulfillment_productemailyes` varchar(255) DEFAULT NULL,
  `blacklistedaccountclosed` enum('Y','N') NOT NULL DEFAULT 'N',
  `blacklistedaccountclosedyes` varchar(255) DEFAULT NULL,
  `shiping_deliverymethod` varchar(255) DEFAULT NULL,
  `transactionmonitoringprocess` varchar(255) DEFAULT NULL,
  `bankaccountownerverification` enum('Y','N') NOT NULL DEFAULT 'N',
  `operationallicense` enum('Y','N') NOT NULL DEFAULT 'N',
  `supervisorregularcontrole` enum('Y','N') NOT NULL DEFAULT 'N',
  `deedofagreement` enum('Y','N') NOT NULL DEFAULT 'N',
  `deedofagreementyes` varchar(255) DEFAULT NULL,
  `isextradetailsprofile` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`extraprofile_id`),
  UNIQUE KEY `application_id` (`application_id`),
  CONSTRAINT `applicationmanager_extradetailsprofile_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;

/*Table structure for table `applicationmanager_ownershipprofile` */

DROP TABLE IF EXISTS `applicationmanager_ownershipprofile`;

CREATE TABLE `applicationmanager_ownershipprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_id` int(11) NOT NULL,
  `nameprincipal1` varchar(255) DEFAULT NULL,
  `nameprincipal1_lastname` varchar(255) DEFAULT NULL,
  `nameprincipal1_title` varchar(255) DEFAULT NULL,
  `nameprincipal1_owned` int(255) DEFAULT NULL,
  `nameprincipal1_telnocc1` int(20) DEFAULT NULL,
  `nameprincipal1_telephonenumber` varchar(20) DEFAULT NULL,
  `nameprincipal1_emailaddress` varchar(255) DEFAULT NULL,
  `nameprincipal1_dateofbirth` datetime DEFAULT NULL,
  `nameprincipal1_identificationtypeselect` varchar(255) DEFAULT NULL,
  `nameprincipal1_identificationtype` varchar(255) DEFAULT NULL,
  `nameprincipal1_State` varchar(255) DEFAULT NULL,
  `nameprincipal1_address` varchar(255) DEFAULT NULL,
  `nameprincipal1_city` varchar(255) DEFAULT NULL,
  `nameprincipal1_zip` varchar(255) DEFAULT NULL,
  `nameprincipal1_country` varchar(255) DEFAULT NULL,
  `nameprincipal1_nationality` varchar(255) DEFAULT NULL,
  `nameprincipal1_Passportexpirydate` datetime DEFAULT NULL,
  `nameprincipal2` varchar(255) DEFAULT NULL,
  `nameprincipal2_lastname` varchar(255) DEFAULT NULL,
  `nameprincipal2_title` varchar(255) DEFAULT NULL,
  `nameprincipal2_owned` int(255) DEFAULT NULL,
  `nameprincipal2_telnocc2` int(20) DEFAULT NULL,
  `nameprincipal2_telephonenumber` varchar(20) DEFAULT NULL,
  `nameprincipal2_emailaddress` varchar(255) DEFAULT NULL,
  `nameprincipal2_dateofbirth` datetime DEFAULT NULL,
  `nameprincipal2_identificationtypeselect` varchar(255) DEFAULT NULL,
  `nameprincipal2_identificationtype` varchar(255) DEFAULT NULL,
  `nameprincipal2_State` varchar(255) DEFAULT NULL,
  `nameprincipal2_address` varchar(255) DEFAULT NULL,
  `nameprincipal2_city` varchar(255) DEFAULT NULL,
  `nameprincipal2_zip` varchar(255) DEFAULT NULL,
  `nameprincipal2_country` varchar(255) DEFAULT NULL,
  `nameprincipal2_nationality` varchar(255) DEFAULT NULL,
  `nameprincipal2_Passportexpirydate` datetime DEFAULT NULL,
  `shareholderprofile1` varchar(255) DEFAULT NULL,
  `shareholderprofile1_lastname` varchar(255) DEFAULT NULL,
  `shareholderprofile1_title` varchar(255) DEFAULT NULL,
  `shareholderprofile1_owned` varchar(255) DEFAULT NULL,
  `shareholderprofile1_telnocc1` int(20) DEFAULT NULL,
  `shareholderprofile1_telephonenumber` varchar(255) DEFAULT NULL,
  `shareholderprofile1_emailaddress` varchar(255) DEFAULT NULL,
  `nameprincipal3` varchar(255) DEFAULT NULL,
  `nameprincipal3_lastname` varchar(255) DEFAULT NULL,
  `shareholderprofile1_dateofbirth` datetime DEFAULT NULL,
  `shareholderprofile1_identificationtypeselect` varchar(255) DEFAULT NULL,
  `shareholderprofile1_identificationtype` varchar(255) DEFAULT NULL,
  `shareholderprofile1_State` varchar(255) DEFAULT NULL,
  `shareholderprofile1_address` varchar(255) DEFAULT NULL,
  `shareholderprofile1_city` varchar(255) DEFAULT NULL,
  `shareholderprofile1_zip` varchar(255) DEFAULT NULL,
  `shareholderprofile1_country` varchar(255) DEFAULT NULL,
  `shareholderprofile1_nationality` varchar(255) DEFAULT NULL,
  `shareholderprofile1_Passportexpirydate` datetime DEFAULT NULL,
  `shareholderprofile2` varchar(255) DEFAULT NULL,
  `shareholderprofile2_lastname` varchar(255) DEFAULT NULL,
  `shareholderprofile2_title` varchar(255) DEFAULT NULL,
  `shareholderprofile2_owned` varchar(255) DEFAULT NULL,
  `shareholderprofile2_telnocc2` varchar(255) DEFAULT NULL,
  `shareholderprofile2_telephonenumber` varchar(255) DEFAULT NULL,
  `shareholderprofile2_emailaddress` varchar(255) DEFAULT NULL,
  `shareholderprofile2_dateofbirth` datetime DEFAULT NULL,
  `shareholderprofile2_identificationtypeselect` varchar(255) DEFAULT NULL,
  `shareholderprofile2_identificationtype` varchar(255) DEFAULT NULL,
  `shareholderprofile2_State` varchar(255) DEFAULT NULL,
  `shareholderprofile2_address` varchar(255) DEFAULT NULL,
  `shareholderprofile2_city` varchar(255) DEFAULT NULL,
  `shareholderprofile2_zip` varchar(255) DEFAULT NULL,
  `shareholderprofile2_country` varchar(255) DEFAULT NULL,
  `shareholderprofile2_nationality` varchar(255) DEFAULT NULL,
  `shareholderprofile2_Passportexpirydate` datetime DEFAULT NULL,
  `directorsprofile` varchar(255) DEFAULT NULL,
  `directorsprofile_lastname` varchar(255) DEFAULT NULL,
  `directorsprofile_title` varchar(255) DEFAULT NULL,
  `directorsprofile_telnocc1` int(20) DEFAULT NULL,
  `directorsprofile_telephonenumber` varchar(255) DEFAULT NULL,
  `directorsprofile_emailaddress` varchar(255) DEFAULT NULL,
  `directorsprofile_dateofbirth` datetime DEFAULT NULL,
  `directorsprofile_identificationtypeselect` varchar(255) DEFAULT NULL,
  `directorsprofile_identificationtype` varchar(255) DEFAULT NULL,
  `directorsprofile_State` varchar(255) DEFAULT NULL,
  `directorsprofile_address` varchar(255) DEFAULT NULL,
  `directorsprofile_city` varchar(255) DEFAULT NULL,
  `directorsprofile_zip` varchar(255) DEFAULT NULL,
  `directorsprofile_country` varchar(255) DEFAULT NULL,
  `directorsprofile_nationality` varchar(255) DEFAULT NULL,
  `directorsprofile_Passportexpirydate` datetime DEFAULT NULL,
  `authorizedsignatoryprofile` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_lastname` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_title` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_telnocc1` int(20) DEFAULT NULL,
  `authorizedsignatoryprofile_telephonenumber` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_emailaddress` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_dateofbirth` datetime DEFAULT NULL,
  `authorizedsignatoryprofile_identificationtypeselect` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_identificationtype` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_State` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_address` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_city` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_zip` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_country` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_nationality` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile_Passportexpirydate` datetime DEFAULT NULL,
  `nameprincipal3_title` varchar(255) DEFAULT NULL,
  `nameprincipal3_owned` int(255) DEFAULT NULL,
  `nameprincipal3_telnocc1` int(20) DEFAULT NULL,
  `nameprincipal3_telephonenumber` varchar(20) DEFAULT NULL,
  `nameprincipal3_emailaddress` varchar(255) DEFAULT NULL,
  `nameprincipal3_dateofbirth` datetime DEFAULT NULL,
  `nameprincipal3_identificationtypeselect` varchar(255) DEFAULT NULL,
  `nameprincipal3_identificationtype` varchar(255) DEFAULT NULL,
  `nameprincipal3_State` varchar(255) DEFAULT NULL,
  `nameprincipal3_address` varchar(255) DEFAULT NULL,
  `nameprincipal3_city` varchar(255) DEFAULT NULL,
  `nameprincipal3_zip` varchar(255) DEFAULT NULL,
  `nameprincipal3_country` varchar(255) DEFAULT NULL,
  `nameprincipal3_nationality` varchar(255) DEFAULT NULL,
  `nameprincipal3_Passportexpirydate` datetime DEFAULT NULL,
  `directorsprofile2` varchar(255) DEFAULT NULL,
  `directorsprofile2_lastname` varchar(255) DEFAULT NULL,
  `directorsprofile2_title` varchar(255) DEFAULT NULL,
  `directorsprofile2_telnocc1` int(20) DEFAULT NULL,
  `directorsprofile2_telephonenumber` varchar(255) DEFAULT NULL,
  `directorsprofile2_emailaddress` varchar(255) DEFAULT NULL,
  `directorsprofile2_dateofbirth` datetime DEFAULT NULL,
  `directorsprofile2_identificationtypeselect` varchar(255) DEFAULT NULL,
  `directorsprofile2_identificationtype` varchar(255) DEFAULT NULL,
  `directorsprofile2_State` varchar(255) DEFAULT NULL,
  `directorsprofile2_address` varchar(255) DEFAULT NULL,
  `directorsprofile2_city` varchar(255) DEFAULT NULL,
  `directorsprofile2_zip` varchar(255) DEFAULT NULL,
  `directorsprofile2_country` varchar(255) DEFAULT NULL,
  `directorsprofile2_nationality` varchar(255) DEFAULT NULL,
  `directorsprofile2_Passportexpirydate` datetime DEFAULT NULL,
  `directorsprofile3` varchar(255) DEFAULT NULL,
  `directorsprofile3_lastname` varchar(255) DEFAULT NULL,
  `directorsprofile3_title` varchar(255) DEFAULT NULL,
  `directorsprofile3_telnocc1` int(20) DEFAULT NULL,
  `directorsprofile3_telephonenumber` varchar(255) DEFAULT NULL,
  `directorsprofile3_emailaddress` varchar(255) DEFAULT NULL,
  `shareholderprofile3` varchar(255) DEFAULT NULL,
  `shareholderprofile3_lastname` varchar(255) DEFAULT NULL,
  `shareholderprofile3_title` varchar(255) DEFAULT NULL,
  `shareholderprofile3_owned` int(20) DEFAULT NULL,
  `shareholderprofile3_telnocc2` int(20) DEFAULT NULL,
  `shareholderprofile3_telephonenumber` varchar(255) DEFAULT NULL,
  `shareholderprofile3_emailaddress` varchar(255) DEFAULT NULL,
  `shareholderprofile3_dateofbirth` datetime DEFAULT NULL,
  `shareholderprofile3_identificationtypeselect` varchar(255) DEFAULT NULL,
  `shareholderprofile3_identificationtype` varchar(255) DEFAULT NULL,
  `shareholderprofile3_State` varchar(255) DEFAULT NULL,
  `shareholderprofile3_address` varchar(255) DEFAULT NULL,
  `shareholderprofile3_city` varchar(255) DEFAULT NULL,
  `shareholderprofile3_zip` varchar(255) DEFAULT NULL,
  `shareholderprofile3_country` varchar(255) DEFAULT NULL,
  `shareholderprofile3_nationality` varchar(255) DEFAULT NULL,
  `shareholderprofile3_Passportexpirydate` datetime DEFAULT NULL,
  `directorsprofile3_dateofbirth` datetime DEFAULT NULL,
  `directorsprofile3_identificationtypeselect` varchar(255) DEFAULT NULL,
  `directorsprofile3_identificationtype` varchar(255) DEFAULT NULL,
  `directorsprofile3_State` varchar(255) DEFAULT NULL,
  `directorsprofile3_address` varchar(255) DEFAULT NULL,
  `directorsprofile3_city` varchar(255) DEFAULT NULL,
  `directorsprofile3_zip` varchar(255) DEFAULT NULL,
  `directorsprofile3_country` varchar(255) DEFAULT NULL,
  `directorsprofile3_nationality` varchar(255) DEFAULT NULL,
  `directorsprofile3_Passportexpirydate` datetime DEFAULT NULL,
  `authorizedsignatoryprofile2` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_lastname` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_title` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_telnocc1` int(20) DEFAULT NULL,
  `authorizedsignatoryprofile2_telephonenumber` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_emailaddress` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_dateofbirth` datetime DEFAULT NULL,
  `authorizedsignatoryprofile2_identificationtypeselect` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_identificationtype` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_State` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_address` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_city` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_zip` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_country` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_nationality` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile2_Passportexpirydate` datetime DEFAULT NULL,
  `authorizedsignatoryprofile3` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_lastname` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_title` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_telnocc1` int(20) DEFAULT NULL,
  `authorizedsignatoryprofile3_telephonenumber` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_emailaddress` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_dateofbirth` datetime DEFAULT NULL,
  `authorizedsignatoryprofile3_identificationtypeselect` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_identificationtype` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_State` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_address` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_city` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_zip` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_country` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_nationality` varchar(255) DEFAULT NULL,
  `authorizedsignatoryprofile3_Passportexpirydate` datetime DEFAULT NULL,
  `corporateshareholder1_Name` varchar(255) DEFAULT NULL,
  `corporateshareholder1_RegNumber` varchar(255) DEFAULT NULL,
  `corporateshareholder1_Address` varchar(255) DEFAULT NULL,
  `corporateshareholder1_City` varchar(255) DEFAULT NULL,
  `corporateshareholder1_State` varchar(255) DEFAULT NULL,
  `corporateshareholder1_ZipCode` varchar(255) DEFAULT NULL,
  `corporateshareholder1_Country` varchar(255) DEFAULT NULL,
  `corporateshareholder2_Name` varchar(255) DEFAULT NULL,
  `corporateshareholder2_RegNumber` varchar(255) DEFAULT NULL,
  `corporateshareholder2_Address` varchar(255) DEFAULT NULL,
  `corporateshareholder2_City` varchar(255) DEFAULT NULL,
  `corporateshareholder2_State` varchar(255) DEFAULT NULL,
  `corporateshareholder2_ZipCode` varchar(255) DEFAULT NULL,
  `corporateshareholder2_Country` varchar(255) DEFAULT NULL,
  `corporateshareholder3_Name` varchar(255) DEFAULT NULL,
  `corporateshareholder3_RegNumber` varchar(255) DEFAULT NULL,
  `corporateshareholder3_Address` varchar(255) DEFAULT NULL,
  `corporateshareholder3_City` varchar(255) DEFAULT NULL,
  `corporateshareholder3_State` varchar(255) DEFAULT NULL,
  `corporateshareholder3_ZipCode` varchar(255) DEFAULT NULL,
  `corporateshareholder3_Country` varchar(255) DEFAULT NULL,
  `politicallyexposed1` enum('Y','N') NOT NULL DEFAULT 'N',
  `politicallyexposed2` enum('Y','N') NOT NULL DEFAULT 'N',
  `politicallyexposed3` enum('Y','N') NOT NULL DEFAULT 'N',
  `criminalrecord1` enum('Y','N') NOT NULL DEFAULT 'N',
  `criminalrecord2` enum('Y','N') NOT NULL DEFAULT 'N',
  `criminalrecord3` enum('Y','N') NOT NULL DEFAULT 'N',
  `isownershipprofilesaved` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`),
  KEY `applicationmanager_ownershipprofile_ibfk_1` (`application_id`),
  CONSTRAINT `applicationmanager_ownershipprofile_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=184 DEFAULT CHARSET=latin1;

/*Table structure for table `at_fraud_trans_details` */

DROP TABLE IF EXISTS `at_fraud_trans_details`;

CREATE TABLE `at_fraud_trans_details` (
  `ft_details_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fraud_trans_id` int(10) NOT NULL,
  `trackingid` int(10) DEFAULT NULL,
  `fs_transid` int(10) DEFAULT NULL,
  `score` decimal(9,2) DEFAULT NULL,
  `fs_responsecode` int(10) DEFAULT NULL,
  `fs_responsedesc` varchar(200) DEFAULT NULL,
  `fs_responserec` varchar(50) DEFAULT NULL,
  `fraud_trans_status` varchar(50) DEFAULT NULL,
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `updatechargebackstatus` varchar(90) DEFAULT NULL,
  `updatechargebackdesc` varchar(590) DEFAULT NULL,
  `updatereversstatus` varchar(90) DEFAULT NULL,
  `updatereversdesc` varchar(90) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ft_details_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3340 DEFAULT CHARSET=latin1;

/*Table structure for table `bank_merchant_settlement_master` */

DROP TABLE IF EXISTS `bank_merchant_settlement_master`;

CREATE TABLE `bank_merchant_settlement_master` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `cycleid` int(10) NOT NULL,
  `memberid` int(10) NOT NULL,
  `ispaid` enum('N','Y') NOT NULL DEFAULT 'N',
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=latin1;

/*Table structure for table `bank_rollingreserve_history` */

DROP TABLE IF EXISTS `bank_rollingreserve_history`;

CREATE TABLE `bank_rollingreserve_history` (
  `bank_rrhistoryid` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `merchantid` varchar(100) NOT NULL,
  `rollingreservereleaseupto` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bank_rrhistoryid`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;

/*Table structure for table `bank_rollingreserve_master` */

DROP TABLE IF EXISTS `bank_rollingreserve_master`;

CREATE TABLE `bank_rollingreserve_master` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `rollingreservereleaseupto` timestamp NULL DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

/*Table structure for table `bank_settlement_received_master` */

DROP TABLE IF EXISTS `bank_settlement_received_master`;

CREATE TABLE `bank_settlement_received_master` (
  `cycleid` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `pgtypeid` int(10) NOT NULL,
  `merchantid` int(10) NOT NULL,
  `settlementdate` datetime DEFAULT NULL,
  `expected_startdate` datetime NOT NULL,
  `expected_enddate` datetime NOT NULL,
  `actual_startdate` datetime NOT NULL,
  `actual_enddate` datetime NOT NULL,
  `bank_settlementid` int(10) NOT NULL,
  `issettlementcronexcecuted` enum('Y','N') NOT NULL DEFAULT 'N',
  `ispayoutcronexecuted` enum('Y','N') NOT NULL DEFAULT 'N',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cycleid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `bank_wiremanager` */

DROP TABLE IF EXISTS `bank_wiremanager`;

CREATE TABLE `bank_wiremanager` (
  `bankwiremanagerId` int(20) NOT NULL AUTO_INCREMENT,
  `settleddate` timestamp NULL DEFAULT NULL,
  `pgtypeId` int(10) NOT NULL,
  `accountId` int(10) NOT NULL,
  `mid` varchar(50) NOT NULL,
  `bank_start_date` timestamp NULL DEFAULT NULL,
  `bank_end_date` timestamp NULL DEFAULT NULL,
  `server_start_date` timestamp NULL DEFAULT NULL,
  `server_end_date` timestamp NULL DEFAULT NULL,
  `processing_amount` double(9,2) NOT NULL DEFAULT '0.00',
  `grossAmount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `netfinal_amount` double(9,2) NOT NULL DEFAULT '0.00',
  `unpaid_amount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) DEFAULT NULL,
  `isrollingreservereleasewire` enum('Y','N') NOT NULL DEFAULT 'N',
  `rollingreservereleasedateupto` timestamp NULL DEFAULT NULL,
  `declinedcoveredupto` timestamp NULL DEFAULT NULL,
  `chargebackcoveredupto` timestamp NULL DEFAULT NULL,
  `reversedCoveredUpto` timestamp NULL DEFAULT NULL,
  `banksettlement_report_file` varchar(255) DEFAULT NULL,
  `banksettlement_transaction_file` varchar(255) DEFAULT NULL,
  `isSettlementCronExceuted` enum('Y','N') NOT NULL DEFAULT 'N',
  `isPayoutCronExcuted` enum('Y','N') NOT NULL DEFAULT 'N',
  `ispaid` enum('Y','N') NOT NULL DEFAULT 'Y',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isPartnerCommCronExecuted` enum('Y','N') NOT NULL DEFAULT 'N',
  `isAgentCommCronExecuted` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`bankwiremanagerId`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;

/*Table structure for table `bankaccount_details` */

DROP TABLE IF EXISTS `bankaccount_details`;

CREATE TABLE `bankaccount_details` (
  `bank_accountid` int(10) NOT NULL AUTO_INCREMENT,
  `bic` varchar(50) DEFAULT NULL,
  `iban` varchar(50) DEFAULT NULL,
  `holder` varchar(255) DEFAULT NULL,
  `bankname` varchar(255) DEFAULT NULL,
  `bankCode` varchar(15) DEFAULT NULL,
  `country` varchar(5) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `mandate_id` varchar(256) DEFAULT NULL,
  `mandate_dateOfSignature` varchar(20) DEFAULT NULL,
  `transactionDueDate` varchar(10) DEFAULT NULL,
  `accountNumber` varchar(10) DEFAULT NULL,
  `routingNumber` varchar(10) DEFAULT NULL,
  `accountType` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`bank_accountid`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=latin1;

/*Table structure for table `bankapplicationmaster` */

DROP TABLE IF EXISTS `bankapplicationmaster`;

CREATE TABLE `bankapplicationmaster` (
  `bankapplicationid` int(20) NOT NULL AUTO_INCREMENT,
  `application_id` int(20) DEFAULT NULL,
  `pgtypeid` int(20) NOT NULL,
  `member_id` int(20) DEFAULT NULL,
  `bankfilename` varchar(255) DEFAULT NULL,
  `status` enum('GENERATED','INVALIDATED','VERIFIED') DEFAULT 'GENERATED',
  `remark` varchar(255) DEFAULT NULL,
  `dtstamp` int(20) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`bankapplicationid`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=latin1;

/*Table structure for table `bin_base` */

DROP TABLE IF EXISTS `bin_base`;

CREATE TABLE `bin_base` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_six` varchar(6) DEFAULT NULL,
  `last_four` varchar(4) DEFAULT NULL,
  `cardtype` varchar(10) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `bank_location` varchar(255) DEFAULT NULL,
  `card_level` varchar(255) DEFAULT NULL,
  `card_country` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `emailid` varchar(255) DEFAULT NULL,
  `ishighrisk` enum('Y','N') DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=latin1;

/*Table structure for table `bin_details` */

DROP TABLE IF EXISTS `bin_details`;

CREATE TABLE `bin_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `icicitransid` int(11) NOT NULL,
  `first_six` varchar(6) DEFAULT NULL,
  `last_four` varchar(4) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `emailaddr` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `isSettled` enum('Y','N') NOT NULL DEFAULT 'N',
  `isSuccessful` enum('Y','N') NOT NULL DEFAULT 'N',
  `isRefund` enum('Y','N') NOT NULL DEFAULT 'N',
  `isChargeback` enum('Y','N') NOT NULL DEFAULT 'N',
  `isFraud` enum('Y','N') NOT NULL DEFAULT 'N',
  `isRollingReserveKept` enum('Y','N') NOT NULL DEFAULT 'Y',
  `isRollingReserveReleased` enum('Y','N') NOT NULL DEFAULT 'N',
  `RollingReserveAmountKept` decimal(9,2) NOT NULL DEFAULT '0.00',
  `RollingReserveAmountReleased` decimal(9,2) NOT NULL DEFAULT '0.00',
  `isVerifyOrder` enum('Y','N') DEFAULT 'N',
  `isRefundAlert` enum('Y','N') DEFAULT 'N',
  `isRetrivalRequest` enum('Y','N') NOT NULL DEFAULT 'N',
  `flightMode` enum('Y','N') NOT NULL DEFAULT 'N',
  `subcard_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=158619 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `blacklist_cards` */

DROP TABLE IF EXISTS `blacklist_cards`;

CREATE TABLE `blacklist_cards` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `first_six` int(6) NOT NULL,
  `last_four` int(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `blacklist_country` */

DROP TABLE IF EXISTS `blacklist_country`;

CREATE TABLE `blacklist_country` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `country_code` varchar(5) DEFAULT NULL,
  `telnocc` int(5) DEFAULT NULL,
  `country` varchar(30) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `blacklist_email` */

DROP TABLE IF EXISTS `blacklist_email`;

CREATE TABLE `blacklist_email` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `emailAddress` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;

/*Table structure for table `blacklist_ip` */

DROP TABLE IF EXISTS `blacklist_ip`;

CREATE TABLE `blacklist_ip` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `ipaddress` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

/*Table structure for table `blacklist_name` */

DROP TABLE IF EXISTS `blacklist_name`;

CREATE TABLE `blacklist_name` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

/*Table structure for table `blockedcountry` */

DROP TABLE IF EXISTS `blockedcountry`;

CREATE TABLE `blockedcountry` (
  `country` char(2) DEFAULT NULL,
  `countryname` text,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `blockedemail` */

DROP TABLE IF EXISTS `blockedemail`;

CREATE TABLE `blockedemail` (
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `type` varchar(10) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `emailaddr` (`emailaddr`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `blockedip` */

DROP TABLE IF EXISTS `blockedip`;

CREATE TABLE `blockedip` (
  `startipcode` bigint(20) DEFAULT NULL,
  `startipaddress` varchar(255) DEFAULT NULL,
  `endipcode` bigint(20) DEFAULT NULL,
  `endipaddress` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `business_profile` */

DROP TABLE IF EXISTS `business_profile`;

CREATE TABLE `business_profile` (
  `profileid` int(30) NOT NULL AUTO_INCREMENT,
  `profile_name` varchar(255) DEFAULT NULL,
  `partner_id` int(30) DEFAULT NULL,
  PRIMARY KEY (`profileid`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

/*Table structure for table `business_profile_rule` */

DROP TABLE IF EXISTS `business_profile_rule`;

CREATE TABLE `business_profile_rule` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `profile_id` int(30) NOT NULL,
  `ruleid` int(30) NOT NULL,
  `operationid` int(30) NOT NULL,
  `isApplicable` enum('Y','N') NOT NULL DEFAULT 'N',
  `value1` varchar(255) DEFAULT NULL,
  `value2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `business_profile_ibfk_2` (`ruleid`),
  KEY `business_profile_ibfk_1` (`profile_id`),
  CONSTRAINT `business_profile_rule_ibfk_2` FOREIGN KEY (`ruleid`) REFERENCES `business_rule` (`id`),
  CONSTRAINT `fk_business_profile_rule_1` FOREIGN KEY (`profile_id`) REFERENCES `business_profile` (`profileid`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=latin1;

/*Table structure for table `business_rule` */

DROP TABLE IF EXISTS `business_rule`;

CREATE TABLE `business_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `rule_type` enum('COMPARATOR','DATABASE','REGULAR_EXPRESSION','FLAT_FILE') NOT NULL DEFAULT 'COMPARATOR',
  `query` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

/*Table structure for table `business_rule_operation` */

DROP TABLE IF EXISTS `business_rule_operation`;

CREATE TABLE `business_rule_operation` (
  `operationId` int(30) NOT NULL AUTO_INCREMENT,
  `ruleId` int(30) NOT NULL,
  `operationType` enum('EXECUTION','PRECONDITION','POSTCONDITION_FAIL','POSTCONDITION_PASS') NOT NULL DEFAULT 'EXECUTION',
  `inputName` varchar(255) DEFAULT NULL,
  `regex` varchar(255) DEFAULT NULL,
  `operator` enum('EQUAL_TO','NOT_EQUAL_TO','GREATER_THAN','LESS_THAN','GREATER_THAN_EQUALS_TO','LESS_THAN_EQUALS_TO','BETWEEN','CONTAINS','NOT_CONTAINS') NOT NULL DEFAULT 'EQUAL_TO',
  `comparator` enum('OR','AND') DEFAULT NULL,
  `inputType` enum('INT','VARCHAR','ENUM','DECIMAL') DEFAULT 'VARCHAR',
  `enumValue` varchar(1000) DEFAULT NULL,
  `isMandatory` enum('Y','N') NOT NULL DEFAULT 'N',
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`operationId`),
  KEY `ruleId` (`ruleId`),
  CONSTRAINT `business_rule_operation_ibfk_1` FOREIGN KEY (`ruleId`) REFERENCES `business_rule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=latin1;

/*Table structure for table `caller_info` */

DROP TABLE IF EXISTS `caller_info`;

CREATE TABLE `caller_info` (
  `complainid` int(255) NOT NULL AUTO_INCREMENT,
  `trackingid` int(11) DEFAULT NULL,
  `firstname` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `phoneno` varchar(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `lastcalldate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pod` varchar(255) DEFAULT NULL,
  `podbatch` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`complainid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

/*Table structure for table `card_type` */

DROP TABLE IF EXISTS `card_type`;

CREATE TABLE `card_type` (
  `cardtypeid` int(4) NOT NULL DEFAULT '0',
  `cardType` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`cardtypeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `cardholder_master` */

DROP TABLE IF EXISTS `cardholder_master`;

CREATE TABLE `cardholder_master` (
  `cardholderid` int(10) NOT NULL AUTO_INCREMENT,
  `toid` int(10) DEFAULT NULL,
  `firstname` varchar(255) NOT NULL,
  `lastname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `birthdate` varchar(50) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `gender` varchar(20) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `registeredtime` int(11) NOT NULL DEFAULT '0',
  `partnerid` int(11) DEFAULT NULL,
  PRIMARY KEY (`cardholderid`)
) ENGINE=InnoDB AUTO_INCREMENT=10250 DEFAULT CHARSET=utf8;

/*Table structure for table `cb_codes` */

DROP TABLE IF EXISTS `cb_codes`;

CREATE TABLE `cb_codes` (
  `cbid` int(7) NOT NULL AUTO_INCREMENT,
  `code` int(7) NOT NULL DEFAULT '0',
  `reason` text NOT NULL,
  PRIMARY KEY (`cbid`)
) ENGINE=MyISAM AUTO_INCREMENT=52 DEFAULT CHARSET=latin1;

/*Table structure for table `ccavenue_members` */

DROP TABLE IF EXISTS `ccavenue_members`;

CREATE TABLE `ccavenue_members` (
  `memberid` int(11) NOT NULL DEFAULT '0',
  `ccavenuemid` varchar(255) NOT NULL DEFAULT '',
  `ccavenueclkey` varchar(255) NOT NULL DEFAULT '',
  KEY `ccavenue_members_memberid_idx` (`memberid`),
  KEY `ccavenue_members_ccavenuemid_idx` (`ccavenuemid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `charge_master` */

DROP TABLE IF EXISTS `charge_master`;

CREATE TABLE `charge_master` (
  `chargeid` int(11) NOT NULL AUTO_INCREMENT,
  `chargename` varchar(25) NOT NULL,
  `isinputrequired` enum('Y','N') DEFAULT 'N',
  `keyname` varchar(25) NOT NULL,
  `valuetype` varchar(100) NOT NULL DEFAULT '',
  `category` varchar(100) NOT NULL DEFAULT '',
  `keyword` varchar(100) NOT NULL DEFAULT '',
  `subkeyword` varchar(100) NOT NULL DEFAULT '',
  `frequency` varchar(100) NOT NULL DEFAULT '',
  `sequencenum` int(10) NOT NULL,
  PRIMARY KEY (`chargeid`),
  UNIQUE KEY `chargename` (`chargename`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;

/*Table structure for table `chargeback_report_process_history` */

DROP TABLE IF EXISTS `chargeback_report_process_history`;

CREATE TABLE `chargeback_report_process_history` (
  `chargeback_process_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` text,
  `filedata` blob,
  `download_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `no_of_rows` int(5) DEFAULT '0',
  `no_of_transactions` int(5) DEFAULT '0',
  `parsed` enum('N','X','Z','Y') DEFAULT 'N',
  `unprocessed_transactions` text,
  PRIMARY KEY (`chargeback_process_id`)
) ENGINE=MyISAM AUTO_INCREMENT=771 DEFAULT CHARSET=latin1;

/*Table structure for table `chargeback_transaction_list` */

DROP TABLE IF EXISTS `chargeback_transaction_list`;

CREATE TABLE `chargeback_transaction_list` (
  `icicitransid` int(11) NOT NULL DEFAULT '0',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `merchantid` varchar(20) DEFAULT NULL,
  `toid` int(11) NOT NULL DEFAULT '0',
  `processed` enum('Y','N','Z','X') DEFAULT 'N',
  `last_update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fk_chargeback_process_id` int(11) NOT NULL DEFAULT '0',
  `cb_date` varchar(12) DEFAULT NULL,
  `cb_reason` varchar(4) DEFAULT NULL,
  `cb_partial` varchar(4) DEFAULT NULL,
  `cb_indicator` enum('ADJM','CBRV','AUTO') DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `consolidated_application` */

DROP TABLE IF EXISTS `consolidated_application`;

CREATE TABLE `consolidated_application` (
  `consolidated_id` int(20) NOT NULL AUTO_INCREMENT,
  `member_id` int(20) DEFAULT NULL,
  `pgtypeid` int(20) DEFAULT NULL,
  `bankapplicationid` int(20) DEFAULT NULL,
  `status` enum('GENERATED','INVALIDATED','APPROVED') DEFAULT 'GENERATED',
  `filename` varchar(255) DEFAULT NULL,
  `adminId` int(20) NOT NULL,
  `dtstamp` int(20) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`consolidated_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

/*Table structure for table `consolidated_application_history` */

DROP TABLE IF EXISTS `consolidated_application_history`;

CREATE TABLE `consolidated_application_history` (
  `consolidated_id` int(20) NOT NULL AUTO_INCREMENT,
  `member_id` int(20) DEFAULT NULL,
  `pgtypeid` int(20) DEFAULT NULL,
  `bankapplicationid` int(20) DEFAULT NULL,
  `status` enum('GENERATED','INVALIDATED','APPROVED','DELETED') DEFAULT 'GENERATED',
  `filename` varchar(255) DEFAULT NULL,
  `adminId` int(20) NOT NULL,
  `dtstamp` int(20) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`consolidated_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

/*Table structure for table `country_code2name` */

DROP TABLE IF EXISTS `country_code2name`;

CREATE TABLE `country_code2name` (
  `code` char(2) NOT NULL DEFAULT '',
  `name` text,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `cron` */

DROP TABLE IF EXISTS `cron`;

CREATE TABLE `cron` (
  `cronid` int(11) NOT NULL AUTO_INCREMENT,
  `cronname` varchar(128) NOT NULL DEFAULT '',
  `classname` varchar(128) NOT NULL DEFAULT '',
  `methodname` varchar(128) NOT NULL DEFAULT '',
  `isrepititive` enum('true','false') NOT NULL DEFAULT 'false',
  `minute` int(11) DEFAULT '-1',
  `hour` int(11) DEFAULT '-1',
  `dayofmonth` int(11) DEFAULT '-1',
  `month` int(11) DEFAULT '1',
  `dayofweek` int(11) DEFAULT '-1',
  `year` int(11) DEFAULT '-1',
  `creationdt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cronid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `cron_runtime_snapshot` */

DROP TABLE IF EXISTS `cron_runtime_snapshot`;

CREATE TABLE `cron_runtime_snapshot` (
  `classname` varchar(250) NOT NULL DEFAULT '',
  `methodname` varchar(250) NOT NULL DEFAULT '',
  `alarmtime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`classname`,`methodname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `currency_code` */

DROP TABLE IF EXISTS `currency_code`;

CREATE TABLE `currency_code` (
  `currency` char(3) DEFAULT NULL,
  `currencycode` int(4) DEFAULT NULL,
  `creationdt` int(15) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `customersupport` */

DROP TABLE IF EXISTS `customersupport`;

CREATE TABLE `customersupport` (
  `accid` bigint(20) NOT NULL,
  `csId` int(10) NOT NULL AUTO_INCREMENT,
  `csName` varchar(255) DEFAULT NULL,
  `csLogin` varchar(20) DEFAULT NULL,
  `csPassword` varchar(255) DEFAULT NULL,
  `csEmail` varchar(255) NOT NULL DEFAULT '',
  `csContactNumber` varchar(255) NOT NULL,
  `csCreationDate` datetime NOT NULL,
  `csLastLogin` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`csId`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;

/*Table structure for table `customersupportlogindetails` */

DROP TABLE IF EXISTS `customersupportlogindetails`;

CREATE TABLE `customersupportlogindetails` (
  `Id` int(7) NOT NULL AUTO_INCREMENT,
  `csId` int(10) NOT NULL,
  `csLoginDetails` datetime NOT NULL,
  `csLogoutDetails` datetime DEFAULT '0000-00-00 00:00:00',
  `csIPaddress` varchar(16) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=latin1;

/*Table structure for table `datakey` */

DROP TABLE IF EXISTS `datakey`;

CREATE TABLE `datakey` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `encryptkey` varchar(255) NOT NULL DEFAULT '',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `fdtstamp` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `error_code` */

DROP TABLE IF EXISTS `error_code`;

CREATE TABLE `error_code` (
  `id` int(15) NOT NULL AUTO_INCREMENT,
  `error_type` varchar(180) NOT NULL,
  `error_name` varchar(180) NOT NULL,
  `error_code` varchar(75) NOT NULL,
  `error_description` varchar(765) NOT NULL,
  `api_code` varchar(180) NOT NULL,
  `api_description` varchar(765) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=827 DEFAULT CHARSET=utf8;

/*Table structure for table `finance_activities` */

DROP TABLE IF EXISTS `finance_activities`;

CREATE TABLE `finance_activities` (
  `faid` int(11) NOT NULL AUTO_INCREMENT,
  `transid` int(11) NOT NULL DEFAULT '0',
  `description` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(255) NOT NULL DEFAULT 'todo',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `courierno` varchar(255) DEFAULT NULL,
  `chequeno` varchar(255) DEFAULT NULL,
  `issuedtstamp` int(11) DEFAULT NULL,
  PRIMARY KEY (`faid`),
  UNIQUE KEY `faid` (`faid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `firc` */

DROP TABLE IF EXISTS `firc`;

CREATE TABLE `firc` (
  `rssaleamt` decimal(10,2) NOT NULL DEFAULT '0.00',
  `usdsaleamt` decimal(10,2) NOT NULL DEFAULT '0.00',
  `discount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `paidamt` decimal(10,2) NOT NULL DEFAULT '0.00',
  `cardholder` varchar(255) NOT NULL DEFAULT '0',
  `date` varchar(255) NOT NULL DEFAULT '',
  `authid` varchar(11) NOT NULL DEFAULT '0',
  `memberid` int(11) NOT NULL DEFAULT '0',
  `transid` int(11) DEFAULT '0',
  `batch` int(11) NOT NULL DEFAULT '0',
  `fircid` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`fircid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `fraud_report_process_history` */

DROP TABLE IF EXISTS `fraud_report_process_history`;

CREATE TABLE `fraud_report_process_history` (
  `fraud_process_id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` text,
  `filedata` blob,
  `download_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `no_of_rows` int(5) DEFAULT '0',
  `no_of_transactions` int(5) DEFAULT '0',
  `parsed` enum('Y','N') DEFAULT 'N',
  `unprocessed_transactions` text,
  PRIMARY KEY (`fraud_process_id`)
) ENGINE=MyISAM AUTO_INCREMENT=282 DEFAULT CHARSET=latin1;

/*Table structure for table `fraud_transaction` */

DROP TABLE IF EXISTS `fraud_transaction`;

CREATE TABLE `fraud_transaction` (
  `fraud_transaction_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `trackingid` int(10) NOT NULL,
  `memberid` int(10) NOT NULL,
  `accountid` int(10) NOT NULL DEFAULT '0',
  `member_transid` varchar(255) DEFAULT NULL,
  `fsid` int(10) DEFAULT NULL,
  `fstransid` int(10) DEFAULT NULL,
  `fraud_trans_status` varchar(20) DEFAULT '',
  `attempts` int(10) DEFAULT NULL,
  `isAlertSent` enum('Y','N','N/A') DEFAULT 'N/A',
  `isReversed` enum('T','F','N/A') DEFAULT 'F',
  `status` enum('N/A','R','I') DEFAULT 'N/A',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dtstamp` int(11) DEFAULT '0',
  `maxScore` int(5) DEFAULT NULL,
  `autoReversalScore` int(5) DEFAULT NULL,
  PRIMARY KEY (`fraud_transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3291 DEFAULT CHARSET=latin1;

/*Table structure for table `fraud_transaction_list` */

DROP TABLE IF EXISTS `fraud_transaction_list`;

CREATE TABLE `fraud_transaction_list` (
  `icicitransid` int(11) NOT NULL DEFAULT '0',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `merchantid` varchar(20) DEFAULT NULL,
  `processed` enum('Y','N') DEFAULT 'N',
  `fk_fraud_process_id` int(11) NOT NULL DEFAULT '0',
  `toid` int(11) NOT NULL DEFAULT '0',
  `last_update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `toid` (`toid`,`icicitransid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `frauddetails` */

DROP TABLE IF EXISTS `frauddetails`;

CREATE TABLE `frauddetails` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tracking_id` int(11) DEFAULT NULL,
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `at_trans_id` int(11) DEFAULT NULL,
  `new_status` varchar(90) DEFAULT NULL,
  `new_description` varchar(300) DEFAULT NULL,
  `new_recommendation` varchar(30) DEFAULT NULL,
  `new_score` double DEFAULT NULL,
  `new_third_party` varchar(300) DEFAULT NULL,
  `memberid` varchar(20) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `updatechargebackstatus` varchar(90) DEFAULT NULL,
  `updatechargebackdesc` varchar(90) DEFAULT NULL,
  `updatereversstatus` varchar(90) DEFAULT NULL,
  `updatereversdesc` varchar(90) DEFAULT NULL,
  `isAlertSent` enum('N','Y','N/A') DEFAULT 'N/A',
  `isReversed` enum('T','F') DEFAULT 'F',
  `status` enum('N/A','I','R') DEFAULT 'N/A',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=latin1;

/*Table structure for table `fraudrule_change_intimation` */

DROP TABLE IF EXISTS `fraudrule_change_intimation`;

CREATE TABLE `fraudrule_change_intimation` (
  `changeintimationid` int(11) NOT NULL AUTO_INCREMENT,
  `fsid` int(11) NOT NULL,
  `fsaccountid` int(11) NOT NULL,
  `fssubaccountid` int(11) DEFAULT NULL,
  `partnerid` int(11) DEFAULT NULL,
  `memberid` int(11) DEFAULT NULL,
  `status` enum('Initiated','Intimated','Changed','Rejected') NOT NULL,
  `creationdate` int(11) NOT NULL DEFAULT '0',
  `lastupdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'on update CURRENT_TIMESTAMP',
  PRIMARY KEY (`changeintimationid`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;

/*Table structure for table `fraudrule_change_tracker` */

DROP TABLE IF EXISTS `fraudrule_change_tracker`;

CREATE TABLE `fraudrule_change_tracker` (
  `fraudruletrackerid` int(10) NOT NULL AUTO_INCREMENT,
  `fsid` int(10) DEFAULT NULL,
  `fsaccountid` int(10) DEFAULT NULL,
  `fssubaccountid` int(10) DEFAULT NULL,
  `ruleid` int(10) NOT NULL,
  `previousscore` int(3) NOT NULL,
  `newscore` int(3) NOT NULL,
  `previousstatus` enum('Enable','Disable') NOT NULL,
  `newstatus` enum('Disable','Enable') NOT NULL,
  `intimationid` int(11) NOT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`fraudruletrackerid`)
) ENGINE=InnoDB AUTO_INCREMENT=191 DEFAULT CHARSET=utf8;

/*Table structure for table `fraudsystem_account_mapping` */

DROP TABLE IF EXISTS `fraudsystem_account_mapping`;

CREATE TABLE `fraudsystem_account_mapping` (
  `fsaccountid` int(10) NOT NULL AUTO_INCREMENT,
  `accountname` varchar(255) NOT NULL DEFAULT '',
  `username` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `fsid` int(10) NOT NULL,
  `isTest` enum('Y','N') NOT NULL DEFAULT 'Y',
  `dtstamp` int(10) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `contact_name` varchar(255) NOT NULL DEFAULT '',
  `contact_email` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`fsaccountid`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Table structure for table `fraudsystem_master` */

DROP TABLE IF EXISTS `fraudsystem_master`;

CREATE TABLE `fraudsystem_master` (
  `fsid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fsname` varchar(25) DEFAULT NULL,
  `contact_person` varchar(50) DEFAULT NULL,
  `contact_email` varchar(50) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `offline` enum('Y','N') NOT NULL DEFAULT 'N',
  `online` enum('Y','N') NOT NULL DEFAULT 'N',
  `API` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`fsid`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

/*Table structure for table `fraudtransaction_rules_triggered` */

DROP TABLE IF EXISTS `fraudtransaction_rules_triggered`;

CREATE TABLE `fraudtransaction_rules_triggered` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rulename` varchar(255) DEFAULT '',
  `rulescore` varchar(255) DEFAULT '',
  `fraud_transid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1135 DEFAULT CHARSET=latin1;

/*Table structure for table `fsaccount_subaccount_mapping` */

DROP TABLE IF EXISTS `fsaccount_subaccount_mapping`;

CREATE TABLE `fsaccount_subaccount_mapping` (
  `fssubaccountid` int(10) NOT NULL AUTO_INCREMENT,
  `fsaccountid` int(10) NOT NULL,
  `subaccountname` varchar(255) NOT NULL DEFAULT '',
  `subusername` varchar(255) NOT NULL DEFAULT '',
  `subpwd` varchar(255) NOT NULL DEFAULT '',
  `isactive` enum('Y','N') NOT NULL DEFAULT 'N',
  `dtstamp` int(10) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`fssubaccountid`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_account_partner_mapping` */

DROP TABLE IF EXISTS `gateway_account_partner_mapping`;

CREATE TABLE `gateway_account_partner_mapping` (
  `mappingid` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `partnerid` int(10) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `creation_date` int(10) NOT NULL DEFAULT '0',
  `isActive` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=264 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_accounts` */

DROP TABLE IF EXISTS `gateway_accounts`;

CREATE TABLE `gateway_accounts` (
  `accountid` int(2) NOT NULL AUTO_INCREMENT,
  `merchantid` varchar(50) DEFAULT NULL,
  `pgtypeid` int(2) DEFAULT NULL,
  `aliasname` varchar(255) DEFAULT NULL,
  `displayname` varchar(255) DEFAULT NULL,
  `ismastercardsupported` tinyint(1) DEFAULT NULL,
  `creationdt` int(15) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `shortname` varchar(255) DEFAULT NULL,
  `site` varchar(50) DEFAULT NULL,
  `path` varchar(50) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `passwd` varchar(255) DEFAULT NULL,
  `chargeback_path` varchar(50) DEFAULT NULL,
  `isCVVrequired` enum('Y','N') NOT NULL DEFAULT 'Y',
  `daily_amount_limit` decimal(9,2) DEFAULT '100.00',
  `monthly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `daily_card_limit` int(11) DEFAULT '5',
  `weekly_card_limit` int(11) DEFAULT '10',
  `monthly_card_limit` int(11) DEFAULT '20',
  `min_transaction_amount` decimal(9,2) DEFAULT '25.00',
  `max_transaction_amount` decimal(9,2) DEFAULT '1000.00',
  `daily_card_amount_limit` decimal(9,2) DEFAULT '1000.00',
  `weekly_card_amount_limit` decimal(9,2) DEFAULT '5000.00',
  `monthly_card_amount_limit` decimal(9,2) DEFAULT '10000.00',
  `istest` enum('Y','N') NOT NULL DEFAULT 'N',
  `isactive` enum('Y','N') NOT NULL DEFAULT 'Y',
  `weekly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `partnerid` int(10) DEFAULT NULL,
  `agentid` int(10) DEFAULT NULL,
  `is_recurring` enum('Y','N') NOT NULL DEFAULT 'N',
  `addressValidation` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`accountid`)
) ENGINE=MyISAM AUTO_INCREMENT=2708 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_charges_mapping` */

DROP TABLE IF EXISTS `gateway_accounts_charges_mapping`;

CREATE TABLE `gateway_accounts_charges_mapping` (
  `mappingid` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `chargeid` int(11) NOT NULL,
  `chargevalue` decimal(9,2) NOT NULL,
  `agentchargevalue` decimal(9,2) DEFAULT NULL,
  `partnerchargevalue` decimal(9,2) DEFAULT NULL,
  `valuetype` enum('Percentage','FlatRate') DEFAULT 'FlatRate',
  `subkeyword` varchar(50) DEFAULT NULL,
  `frequency` varchar(50) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `keyword` varchar(50) DEFAULT NULL,
  `sequencenum` int(11) NOT NULL,
  `terminalid` int(11) DEFAULT NULL,
  `isinputrequired` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_cup` */

DROP TABLE IF EXISTS `gateway_accounts_cup`;

CREATE TABLE `gateway_accounts_cup` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) NOT NULL DEFAULT '',
  `midkey` varchar(255) DEFAULT NULL,
  `mintransamount` decimal(4,2) DEFAULT NULL,
  `maxtransamount` decimal(9,2) DEFAULT NULL,
  `monthlylimitinamount` decimal(9,2) DEFAULT NULL,
  `noofpercardtransactioninday` int(2) DEFAULT NULL,
  `merchantcategorycode` varchar(21) DEFAULT NULL,
  `istestaccount` enum('Y','N') DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `gateway_accounts_deltapay` */

DROP TABLE IF EXISTS `gateway_accounts_deltapay`;

CREATE TABLE `gateway_accounts_deltapay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `affiliate` varchar(25) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `agent_name` varchar(255) DEFAULT NULL,
  `isLive` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_ecore` */

DROP TABLE IF EXISTS `gateway_accounts_ecore`;

CREATE TABLE `gateway_accounts_ecore` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) NOT NULL DEFAULT '',
  `midkey` varchar(255) DEFAULT NULL,
  `mintransamount` decimal(4,2) DEFAULT NULL,
  `maxtransamount` decimal(9,2) DEFAULT NULL,
  `monthlylimitinamount` decimal(9,2) DEFAULT NULL,
  `noofpercardtransactioninday` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `gateway_accounts_frickbank` */

DROP TABLE IF EXISTS `gateway_accounts_frickbank`;

CREATE TABLE `gateway_accounts_frickbank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(20) DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `pwd` varchar(255) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_accounts_guardian` */

DROP TABLE IF EXISTS `gateway_accounts_guardian`;

CREATE TABLE `gateway_accounts_guardian` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `token` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_inpay` */

DROP TABLE IF EXISTS `gateway_accounts_inpay`;

CREATE TABLE `gateway_accounts_inpay` (
  `id` int(2) NOT NULL AUTO_INCREMENT,
  `accountid` int(5) NOT NULL,
  `mid` int(5) NOT NULL,
  `secretkey` varchar(9) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_mymonedero` */

DROP TABLE IF EXISTS `gateway_accounts_mymonedero`;

CREATE TABLE `gateway_accounts_mymonedero` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `merchantid` varchar(255) DEFAULT NULL,
  `merchantpass` varchar(255) DEFAULT NULL,
  `accesskey` varchar(255) DEFAULT NULL,
  `submerchantid` varchar(255) DEFAULT NULL,
  `submerchantpass` varchar(255) DEFAULT NULL,
  `isLive` tinyint(1) DEFAULT '0',
  `endpoint` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_nmi` */

DROP TABLE IF EXISTS `gateway_accounts_nmi`;

CREATE TABLE `gateway_accounts_nmi` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(20) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_accounts_opx` */

DROP TABLE IF EXISTS `gateway_accounts_opx`;

CREATE TABLE `gateway_accounts_opx` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(50) NOT NULL,
  `servicekey` varchar(255) NOT NULL,
  `routingkey` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_accounts_p4online` */

DROP TABLE IF EXISTS `gateway_accounts_p4online`;

CREATE TABLE `gateway_accounts_p4online` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `accountId` int(30) NOT NULL,
  `brand` enum('SOFORT','GIROPAY','YELLOWPAY','EPS','PRZELEWY','IDEAL','SAFETYPAY','SWEDBANKESTONIA','SWEDBANKLITHUANIA','SWEDBANKLATVIA') NOT NULL DEFAULT 'SOFORT',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_paydollar` */

DROP TABLE IF EXISTS `gateway_accounts_paydollar`;

CREATE TABLE `gateway_accounts_paydollar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `merchantid` varchar(255) DEFAULT NULL,
  `loginId` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `authUrl` varchar(255) DEFAULT NULL,
  `apiUrl` varchar(255) DEFAULT NULL,
  `isLive` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_payforasia` */

DROP TABLE IF EXISTS `gateway_accounts_payforasia`;

CREATE TABLE `gateway_accounts_payforasia` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `mid` int(10) NOT NULL,
  `gatewayno` int(10) NOT NULL,
  `signkey` varchar(50) NOT NULL,
  `accountid` int(10) NOT NULL,
  `isThreeDSec` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_paygateway` */

DROP TABLE IF EXISTS `gateway_accounts_paygateway`;

CREATE TABLE `gateway_accounts_paygateway` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(20) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `passphrase` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_payon` */

DROP TABLE IF EXISTS `gateway_accounts_payon`;

CREATE TABLE `gateway_accounts_payon` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `accountid` int(20) DEFAULT NULL,
  `connector` varchar(90) NOT NULL,
  `shortid` varchar(90) DEFAULT NULL,
  `type` varchar(150) DEFAULT NULL,
  `merchantname` varchar(150) DEFAULT NULL,
  `username` varchar(180) DEFAULT NULL,
  `url` varchar(180) DEFAULT NULL,
  `key` varchar(180) DEFAULT NULL,
  `terminalid` varchar(20) DEFAULT NULL,
  `password` varchar(150) DEFAULT NULL,
  `iban` varchar(150) DEFAULT NULL,
  `uploaduser` varchar(150) DEFAULT NULL,
  `apiUserName` varchar(765) DEFAULT NULL,
  `apiPassword` varchar(765) DEFAULT NULL,
  `eci` varchar(25) DEFAULT NULL,
  `verification` varchar(255) DEFAULT NULL,
  `xid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_payworld` */

DROP TABLE IF EXISTS `gateway_accounts_payworld`;

CREATE TABLE `gateway_accounts_payworld` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `merchantaccno` int(11) NOT NULL,
  `guid` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `str` varchar(255) DEFAULT NULL,
  `accountid` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_accounts_qwipi` */

DROP TABLE IF EXISTS `gateway_accounts_qwipi`;

CREATE TABLE `gateway_accounts_qwipi` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) NOT NULL DEFAULT '',
  `midkey` varchar(255) DEFAULT NULL,
  `mintransamount` decimal(4,2) DEFAULT NULL,
  `maxtransamount` decimal(9,2) DEFAULT NULL,
  `monthlylimitinamount` decimal(9,2) DEFAULT NULL,
  `noofpercardtransactioninday` int(2) DEFAULT NULL,
  `isksnurl` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `gateway_accounts_sbm` */

DROP TABLE IF EXISTS `gateway_accounts_sbm`;

CREATE TABLE `gateway_accounts_sbm` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `terminalid` int(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_sofort` */

DROP TABLE IF EXISTS `gateway_accounts_sofort`;

CREATE TABLE `gateway_accounts_sofort` (
  `id` int(2) NOT NULL AUTO_INCREMENT,
  `accountId` int(5) NOT NULL,
  `projectId` int(10) NOT NULL,
  `customerId` int(10) NOT NULL,
  `apiKey` varchar(255) NOT NULL,
  `isLive` tinyint(1) DEFAULT '0',
  `projectPass` varchar(255) DEFAULT NULL,
  `notificationPass` varchar(255) DEFAULT NULL,
  `accountHolderName` varchar(255) DEFAULT NULL,
  `bankName` varchar(255) DEFAULT NULL,
  `iban` varchar(255) DEFAULT NULL,
  `bic` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_swiffpay` */

DROP TABLE IF EXISTS `gateway_accounts_swiffpay`;

CREATE TABLE `gateway_accounts_swiffpay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(20) DEFAULT NULL,
  `mid` varchar(255) DEFAULT NULL,
  `merchantpin` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_accounts_ugspay` */

DROP TABLE IF EXISTS `gateway_accounts_ugspay`;

CREATE TABLE `gateway_accounts_ugspay` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `websiteid` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `websiteurl` varchar(255) DEFAULT NULL,
  `isLive` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_accounts_wsec` */

DROP TABLE IF EXISTS `gateway_accounts_wsec`;

CREATE TABLE `gateway_accounts_wsec` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` varchar(20) DEFAULT NULL,
  `accountkey` varchar(255) DEFAULT NULL,
  `siteurl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Table structure for table `gateway_payvoucher` */

DROP TABLE IF EXISTS `gateway_payvoucher`;

CREATE TABLE `gateway_payvoucher` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountid` int(11) NOT NULL,
  `version` varchar(4) DEFAULT NULL,
  `token` varchar(10) DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `isLive` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `gateway_type` */

DROP TABLE IF EXISTS `gateway_type`;

CREATE TABLE `gateway_type` (
  `pgtypeid` int(2) NOT NULL AUTO_INCREMENT,
  `gateway` varchar(10) DEFAULT NULL,
  `currency` char(3) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `chargepercentage` int(5) DEFAULT '500',
  `taxpercentage` int(5) DEFAULT '1224',
  `withdrawalcharge` int(2) DEFAULT NULL,
  `reversalcharge` int(2) DEFAULT NULL,
  `chargebackcharge` int(2) DEFAULT NULL,
  `chargesaccount` int(4) DEFAULT NULL,
  `taxaccount` int(4) DEFAULT NULL,
  `highriskamount` int(11) DEFAULT NULL,
  `address` text,
  `creationdt` int(15) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gateway_table_name` varchar(100) DEFAULT NULL,
  `time_difference_normal` time NOT NULL DEFAULT '00:00:00',
  `time_difference_daylight` time NOT NULL DEFAULT '00:00:00',
  `partnerid` int(10) DEFAULT NULL,
  `agentid` int(10) DEFAULT NULL,
  `bank_ipaddress` varchar(255) DEFAULT NULL,
  `templatename` varchar(255) DEFAULT NULL,
  `bank_emailid` varchar(255) DEFAULT NULL,
  `pspcode` varchar(100) DEFAULT NULL,
  `key` varchar(100) DEFAULT NULL,
  `wsservice` varchar(100) DEFAULT NULL,
  `wspassword` varchar(100) DEFAULT NULL,
  `excessCapturePercentage` decimal(9,2) NOT NULL DEFAULT '100.00',
  `isCvvOptional` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`pgtypeid`)
) ENGINE=MyISAM AUTO_INCREMENT=123 DEFAULT CHARSET=latin1;

/*Table structure for table `gatewaytype_agent_mapping` */

DROP TABLE IF EXISTS `gatewaytype_agent_mapping`;

CREATE TABLE `gatewaytype_agent_mapping` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `pgtypeId` int(10) NOT NULL,
  `agentId` int(10) NOT NULL,
  `creation_date` int(10) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=latin1;

/*Table structure for table `gatewaytype_partner_mapping` */

DROP TABLE IF EXISTS `gatewaytype_partner_mapping`;

CREATE TABLE `gatewaytype_partner_mapping` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `pgtypeid` int(10) NOT NULL,
  `partnerid` int(10) NOT NULL,
  `defaultApplication` enum('Y','N') NOT NULL DEFAULT 'N',
  `creation_date` int(10) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=latin1;

/*Table structure for table `icici_firc` */

DROP TABLE IF EXISTS `icici_firc`;

CREATE TABLE `icici_firc` (
  `fircid` int(11) NOT NULL AUTO_INCREMENT,
  `transid` int(11) NOT NULL DEFAULT '0',
  `date` varchar(20) DEFAULT NULL,
  `memberid` int(11) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `batchno` int(11) DEFAULT NULL,
  `dtstamp` int(11) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `usdval` decimal(9,2) DEFAULT '0.00',
  `convrate` decimal(9,2) DEFAULT '0.00',
  PRIMARY KEY (`fircid`),
  KEY `memberid` (`memberid`,`batchno`),
  KEY `idx_icici_firc_transid` (`transid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `invoice` */

DROP TABLE IF EXISTS `invoice`;

CREATE TABLE `invoice` (
  `invoiceno` int(11) NOT NULL AUTO_INCREMENT,
  `trackingid` int(11) DEFAULT NULL,
  `memberid` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) NOT NULL,
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `orderid` varchar(255) NOT NULL DEFAULT '',
  `orderdescription` varchar(255) DEFAULT '',
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `customeremail` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'generated',
  `ctoken` varchar(255) DEFAULT NULL,
  `accountid` int(4) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remindercounter` int(1) unsigned NOT NULL DEFAULT '0',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `cancelreason` varchar(255) DEFAULT NULL,
  `custname` varchar(255) DEFAULT NULL,
  `paymodeid` varchar(4) DEFAULT NULL,
  `isCredential` varchar(1) DEFAULT '0',
  `username` varchar(40) DEFAULT NULL,
  `pwd` varchar(40) DEFAULT NULL,
  `question` varchar(40) DEFAULT NULL,
  `answer` varchar(40) DEFAULT NULL,
  `merchantIpAddress` varchar(40) DEFAULT NULL,
  `terminalid` int(2) DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `expirationPeriod` bigint(100) NOT NULL DEFAULT '0',
  `raisedBy` varchar(20) NOT NULL,
  PRIMARY KEY (`invoiceno`)
) ENGINE=InnoDB AUTO_INCREMENT=1305 DEFAULT CHARSET=latin1;

/*Table structure for table `ipmap` */

DROP TABLE IF EXISTS `ipmap`;

CREATE TABLE `ipmap` (
  `country` char(2) DEFAULT NULL,
  `startip` bigint(20) DEFAULT NULL,
  `endip` bigint(20) DEFAULT NULL,
  KEY `idx_ipmap_startip` (`startip`),
  KEY `idx_ipmap_endip` (`endip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `ipwhitelist` */

DROP TABLE IF EXISTS `ipwhitelist`;

CREATE TABLE `ipwhitelist` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `memberId` int(11) DEFAULT NULL,
  `partnerId` int(11) DEFAULT NULL,
  `agentId` int(11) DEFAULT NULL,
  `ipAddress` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=latin1;

/*Table structure for table `iso_commission_wire_manager` */

DROP TABLE IF EXISTS `iso_commission_wire_manager`;

CREATE TABLE `iso_commission_wire_manager` (
  `iso_comm_id` int(10) NOT NULL AUTO_INCREMENT,
  `accountid` int(10) NOT NULL,
  `startdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `enddate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `settleddate` datetime DEFAULT NULL,
  `amount` double(9,2) NOT NULL DEFAULT '0.00',
  `netfinalamount` double(9,2) NOT NULL DEFAULT '0.00',
  `unpaidamount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(10) NOT NULL,
  `status` varchar(10) NOT NULL,
  `reportfilepath` varchar(255) NOT NULL,
  `transactionfilepath` varchar(255) DEFAULT NULL,
  `creationdate` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actionexecutor` varchar(255) NOT NULL,
  PRIMARY KEY (`iso_comm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `mailentity` */

DROP TABLE IF EXISTS `mailentity`;

CREATE TABLE `mailentity` (
  `mailEntityId` int(5) NOT NULL AUTO_INCREMENT,
  `mailEntityName` varchar(15) NOT NULL,
  `placeHolderTagName` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`mailEntityId`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

/*Table structure for table `mailevent` */

DROP TABLE IF EXISTS `mailevent`;

CREATE TABLE `mailevent` (
  `mailEventId` int(5) NOT NULL AUTO_INCREMENT,
  `mailEventName` varchar(150) NOT NULL DEFAULT '',
  PRIMARY KEY (`mailEventId`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=latin1;

/*Table structure for table `mailtemplate` */

DROP TABLE IF EXISTS `mailtemplate`;

CREATE TABLE `mailtemplate` (
  `templateId` int(5) NOT NULL AUTO_INCREMENT,
  `templateName` varchar(100) NOT NULL DEFAULT '',
  `templateFileName` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`templateId`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=latin1;

/*Table structure for table `mappingmailtemplateevententity` */

DROP TABLE IF EXISTS `mappingmailtemplateevententity`;

CREATE TABLE `mappingmailtemplateevententity` (
  `mappingId` int(5) NOT NULL AUTO_INCREMENT,
  `mailEventId` int(5) NOT NULL,
  `mailFromEntityId` int(5) NOT NULL,
  `mailToEntityId` int(5) NOT NULL,
  `mailTemplateId` int(5) NOT NULL,
  `mailSubject` varchar(255) NOT NULL,
  PRIMARY KEY (`mappingId`)
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=latin1;

/*Table structure for table `master_trackingid` */

DROP TABLE IF EXISTS `master_trackingid`;

CREATE TABLE `master_trackingid` (
  `id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `mcc` */

DROP TABLE IF EXISTS `mcc`;

CREATE TABLE `mcc` (
  `mccid` int(7) NOT NULL AUTO_INCREMENT,
  `mcc` int(7) NOT NULL DEFAULT '0',
  `category` text NOT NULL,
  PRIMARY KEY (`mccid`)
) ENGINE=MyISAM AUTO_INCREMENT=739 DEFAULT CHARSET=latin1;

/*Table structure for table `member_account_mapping` */

DROP TABLE IF EXISTS `member_account_mapping`;

CREATE TABLE `member_account_mapping` (
  `memberid` bigint(10) DEFAULT NULL,
  `accountid` bigint(10) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `daily_amount_limit` decimal(9,2) DEFAULT '100.00',
  `monthly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `daily_card_limit` int(11) DEFAULT '5',
  `weekly_card_limit` int(11) DEFAULT '10',
  `monthly_card_limit` int(11) DEFAULT '20',
  `chargePercentage` int(11) DEFAULT '500',
  `fixApprovalCharge` decimal(9,2) DEFAULT '0.00',
  `fixDeclinedCharge` decimal(9,2) DEFAULT '0.00',
  `taxper` int(11) DEFAULT '1224',
  `reversalcharge` int(2) DEFAULT '0',
  `withdrawalcharge` int(2) DEFAULT '0',
  `chargebackcharge` int(2) DEFAULT '0',
  `reservePercentage` int(11) DEFAULT '0',
  `fraudVerificationCharge` decimal(9,2) DEFAULT '0.00',
  `annualCharge` int(2) DEFAULT '0',
  `setupCharge` int(2) DEFAULT '0',
  `fxClearanceChargePercentage` int(11) DEFAULT '0',
  `monthlyGatewayCharge` int(2) DEFAULT '0',
  `monthlyAccountMntCharge` int(2) DEFAULT '0',
  `reportCharge` int(2) DEFAULT '0',
  `fraudulentCharge` int(2) DEFAULT '0',
  `autoRepresentationCharge` int(2) DEFAULT '0',
  `interchangePlusCharge` decimal(9,2) DEFAULT '0.00',
  `daily_card_amount_limit` decimal(9,2) DEFAULT '1000.00',
  `weekly_card_amount_limit` decimal(9,2) DEFAULT '5000.00',
  `monthly_card_amount_limit` decimal(9,2) DEFAULT '10000.00',
  `isActive` enum('Y','N') DEFAULT 'Y',
  `isTest` enum('Y','N') DEFAULT 'N',
  `priority` enum('1','2','3','4','5') DEFAULT '1',
  `min_transaction_amount` decimal(9,2) DEFAULT '1000.00',
  `max_transaction_amount` decimal(9,2) DEFAULT '1000.00',
  `terminalid` int(10) NOT NULL AUTO_INCREMENT,
  `weekly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `is_recurring` enum('Y','N') NOT NULL DEFAULT 'N',
  `isRestrictedTicketActive` enum('Y','N') NOT NULL DEFAULT 'N',
  `isTokenizationActive` enum('Y','N') NOT NULL DEFAULT 'N',
  `isManualRecurring` enum('Y','N') NOT NULL DEFAULT 'N',
  `addressDetails` enum('Y','N') NOT NULL DEFAULT 'Y',
  `addressValidation` enum('Y','N') NOT NULL DEFAULT 'Y',
  `cardDetailRequired` enum('Y','N') NOT NULL DEFAULT 'Y',
  `dtstamp` int(11) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `isPSTTerminal` enum('Y','N') NOT NULL DEFAULT 'N',
  `isCardEncryptionEnable` enum('Y','N') NOT NULL DEFAULT 'N',
  `riskruleactivation` enum('Y','N') NOT NULL DEFAULT 'N',
  `daily_avg_ticket` double(9,2) NOT NULL DEFAULT '0.00',
  `weekly_avg_ticket` double(9,2) NOT NULL DEFAULT '0.00',
  `monthly_avg_ticket` double(9,2) NOT NULL DEFAULT '0.00',
  `emailLimitEnabled` enum('Y','N') NOT NULL DEFAULT 'N',
  `dailyTrxnPerEmail` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`terminalid`)
) ENGINE=InnoDB AUTO_INCREMENT=594 DEFAULT CHARSET=utf8;

/*Table structure for table `member_account_mapping_archive` */

DROP TABLE IF EXISTS `member_account_mapping_archive`;

CREATE TABLE `member_account_mapping_archive` (
  `memberid` bigint(10) DEFAULT NULL,
  `accountid` bigint(10) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `chargePercentage` int(11) DEFAULT '500',
  `fixApprovalCharge` decimal(9,2) DEFAULT '0.00',
  `fixDeclinedCharge` decimal(9,2) DEFAULT '0.00',
  `taxper` int(11) DEFAULT '1224',
  `reversalcharge` int(2) DEFAULT '0',
  `withdrawalcharge` int(2) DEFAULT '0',
  `chargebackcharge` int(2) DEFAULT '0',
  `reservePercentage` int(11) DEFAULT '0',
  `fraudVerificationCharge` int(2) DEFAULT '0',
  `annualCharge` int(2) DEFAULT '0',
  `setupCharge` int(2) DEFAULT '0',
  `fxClearanceChargePercentage` int(11) DEFAULT '0',
  `monthlyGatewayCharge` int(2) DEFAULT '0',
  `monthlyAccountMntCharge` int(2) DEFAULT '0',
  `reportCharge` int(2) DEFAULT '0',
  `fraudulentCharge` int(2) DEFAULT '0',
  `autoRepresentationCharge` int(2) DEFAULT '0',
  `interchangePlusCharge` decimal(9,2) DEFAULT '0.00',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `daily_amount_limit` decimal(9,2) DEFAULT '100.00',
  `monthly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `daily_card_limit` int(11) DEFAULT '5',
  `weekly_card_limit` int(11) DEFAULT '10',
  `monthly_card_limit` int(11) DEFAULT '20',
  `daily_card_amount_limit` decimal(9,2) DEFAULT '1000.00',
  `weekly_card_amount_limit` decimal(9,2) DEFAULT '5000.00',
  `monthly_card_amount_limit` decimal(9,2) DEFAULT '10000.00',
  `isActive` enum('Y','N') DEFAULT 'Y',
  `isTest` enum('Y','N') DEFAULT 'N',
  `priority` enum('1','2','3','4','5') DEFAULT '1',
  `min_transaction_amount` decimal(9,2) DEFAULT '10.00',
  `max_transaction_amount` decimal(9,2) DEFAULT '1000.00',
  `terminalid` int(10) NOT NULL AUTO_INCREMENT,
  `isPSTTerminal` enum('Y','N') NOT NULL DEFAULT 'N',
  `isCardEncryptionEnable` enum('Y','N') NOT NULL DEFAULT 'N',
  `riskruleactivation` enum('Y','N') NOT NULL DEFAULT 'N',
  `daily_avg_ticket` double(9,2) NOT NULL DEFAULT '0.00',
  `weekly_avg_ticket` double(9,2) NOT NULL DEFAULT '0.00',
  `monthly_avg_ticket` double(9,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`terminalid`)
) ENGINE=InnoDB AUTO_INCREMENT=592 DEFAULT CHARSET=latin1;

/*Table structure for table `member_account_monitoringpara_mapping` */

DROP TABLE IF EXISTS `member_account_monitoringpara_mapping`;

CREATE TABLE `member_account_monitoringpara_mapping` (
  `mappingid` int(11) NOT NULL AUTO_INCREMENT,
  `memberid` int(11) NOT NULL DEFAULT '0',
  `terminalid` int(11) NOT NULL DEFAULT '0',
  `alert_threshold` double(9,2) DEFAULT '0.00',
  `suspension_threshold` double(9,2) DEFAULT '0.00',
  `alert_activation` enum('Y','N') NOT NULL DEFAULT 'N',
  `suspension_activation` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttoadmin` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttomerchant` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttopartner` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttoagent` enum('Y','N') NOT NULL DEFAULT 'N',
  `monitoing_para_id` int(11) NOT NULL DEFAULT '0',
  `dtstamp` int(11) DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `alertMessage` varchar(255) DEFAULT NULL,
  `mapping_frequency` varchar(255) NOT NULL DEFAULT '',
  `isalerttoadmin_sales` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_rf` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_cb` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_tech` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_sales` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_rf` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_cb` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_tech` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_sales` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_rf` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_cb` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_tech` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_sales` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_rf` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_cb` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_tech` enum('Y','N') DEFAULT 'N',
  `isdailyexecution` enum('Y','N') NOT NULL DEFAULT 'N',
  `isweeklyexecution` enum('Y','N') NOT NULL DEFAULT 'N',
  `ismonthlyexecution` enum('Y','N') NOT NULL DEFAULT 'N',
  `weekly_alert_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `weekly_suspension_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `monthly_alert_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `monthly_suspension_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=2200 DEFAULT CHARSET=latin1;

/*Table structure for table `member_accounts_charges_mapping` */

DROP TABLE IF EXISTS `member_accounts_charges_mapping`;

CREATE TABLE `member_accounts_charges_mapping` (
  `mappingid` int(11) NOT NULL AUTO_INCREMENT,
  `memberid` int(11) NOT NULL,
  `accountid` int(11) NOT NULL,
  `paymodeid` int(4) NOT NULL,
  `cardtypeid` int(4) NOT NULL,
  `chargeid` int(11) NOT NULL,
  `chargevalue` decimal(9,2) NOT NULL,
  `valuetype` enum('Percentage','FlatRate') NOT NULL DEFAULT 'FlatRate',
  `keyword` varchar(50) NOT NULL,
  `sequencenum` int(2) NOT NULL,
  `agentchargevalue` decimal(9,2) NOT NULL,
  `partnerchargevalue` decimal(9,2) NOT NULL,
  `subkeyword` varchar(50) NOT NULL,
  `frequency` varchar(50) NOT NULL,
  `category` varchar(50) NOT NULL,
  `terminalid` int(2) NOT NULL,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=latin1;

/*Table structure for table `member_amount_mapping` */

DROP TABLE IF EXISTS `member_amount_mapping`;

CREATE TABLE `member_amount_mapping` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `memberid` int(10) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `amount` decimal(9,2) NOT NULL,
  `creationon` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Table structure for table `member_document_history` */

DROP TABLE IF EXISTS `member_document_history`;

CREATE TABLE `member_document_history` (
  `mapping_detailid` int(20) NOT NULL AUTO_INCREMENT,
  `mapping_id` int(20) DEFAULT NULL,
  `member_id` int(20) DEFAULT NULL,
  `application_id` int(20) DEFAULT NULL,
  `document_name` varchar(255) DEFAULT NULL,
  `moved_document_name` varchar(255) DEFAULT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `replace_status` enum('Y','N') NOT NULL DEFAULT 'N',
  `upload_status` enum('Y','N') NOT NULL DEFAULT 'N',
  `deleted` enum('Y','N') NOT NULL DEFAULT 'N',
  `alternate_name` varchar(100) DEFAULT NULL,
  `label_id` int(20) DEFAULT NULL,
  `mappingcreation_date` int(20) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mapping_detailid`),
  KEY `label_id` (`label_id`),
  KEY `mapping_id` (`mapping_id`),
  CONSTRAINT `member_document_history_ibfk_1` FOREIGN KEY (`label_id`) REFERENCES `uploadfile_label` (`label_id`),
  CONSTRAINT `member_document_history_ibfk_2` FOREIGN KEY (`mapping_id`) REFERENCES `member_document_mapping` (`mapping_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1134 DEFAULT CHARSET=latin1;

/*Table structure for table `member_document_mapping` */

DROP TABLE IF EXISTS `member_document_mapping`;

CREATE TABLE `member_document_mapping` (
  `mapping_id` int(20) NOT NULL AUTO_INCREMENT,
  `application_id` int(20) DEFAULT NULL,
  `member_id` int(20) DEFAULT NULL,
  `document_name` varchar(255) DEFAULT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `replace_status` enum('Y','N') NOT NULL DEFAULT 'N',
  `upload_status` enum('Y','N') NOT NULL DEFAULT 'N',
  `alternate_name` varchar(100) DEFAULT NULL,
  `label_id` int(20) DEFAULT NULL,
  `mappingcreation_date` int(20) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`mapping_id`),
  KEY `member_document_mapping_ibfk_1` (`label_id`),
  KEY `application_id` (`application_id`),
  CONSTRAINT `member_document_mapping_ibfk_1` FOREIGN KEY (`label_id`) REFERENCES `uploadfile_label` (`label_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `member_document_mapping_ibfk_2` FOREIGN KEY (`application_id`) REFERENCES `application_manager` (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=847 DEFAULT CHARSET=latin1;

/*Table structure for table `member_fraudsystem_mapping` */

DROP TABLE IF EXISTS `member_fraudsystem_mapping`;

CREATE TABLE `member_fraudsystem_mapping` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fsid` int(10) NOT NULL,
  `memberid` int(10) NOT NULL,
  `isonlinefraudcheck` enum('Y','N') NOT NULL DEFAULT 'N',
  `isapiuser` enum('Y','N') NOT NULL DEFAULT 'N',
  `isActive` enum('Y','N') NOT NULL DEFAULT 'N',
  `isTest` enum('Y','N') DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `member_pg_preferences` */

DROP TABLE IF EXISTS `member_pg_preferences`;

CREATE TABLE `member_pg_preferences` (
  `memberid` int(11) NOT NULL DEFAULT '0',
  `icicimerchantid` varchar(255) NOT NULL DEFAULT '',
  `username` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `displayname` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `member_settlementcycle_details` */

DROP TABLE IF EXISTS `member_settlementcycle_details`;

CREATE TABLE `member_settlementcycle_details` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `memberid` int(10) DEFAULT NULL,
  `accountid` int(10) DEFAULT NULL,
  `terminalid` int(10) DEFAULT NULL,
  `cycleid` int(10) DEFAULT NULL,
  `verifyorder_count` int(10) DEFAULT NULL,
  `refundalert_count` int(10) DEFAULT NULL,
  `retrivalrequest_count` int(10) DEFAULT NULL,
  `lastsetupfeedate` datetime DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=162 DEFAULT CHARSET=latin1;

/*Table structure for table `member_user_account_mapping` */

DROP TABLE IF EXISTS `member_user_account_mapping`;

CREATE TABLE `member_user_account_mapping` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `memberid` bigint(10) NOT NULL,
  `userid` int(5) NOT NULL,
  `paymodeid` int(5) NOT NULL,
  `cardtypeid` int(5) NOT NULL,
  `terminalid` int(5) NOT NULL,
  `accountid` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=latin1;

/*Table structure for table `member_users` */

DROP TABLE IF EXISTS `member_users`;

CREATE TABLE `member_users` (
  `userid` int(9) NOT NULL AUTO_INCREMENT,
  `memberid` int(9) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `fpasswd` varchar(255) DEFAULT '',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `accid` bigint(20) NOT NULL,
  `contact_emails` varchar(50) NOT NULL DEFAULT '',
  `fdtstamp` int(11) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARSET=latin1;

/*Table structure for table `members` */

DROP TABLE IF EXISTS `members`;

CREATE TABLE `members` (
  `memberid` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL DEFAULT '',
  `passwd` varchar(255) NOT NULL DEFAULT '',
  `fpasswd` varchar(255) NOT NULL DEFAULT '',
  `fdtstamp` int(11) NOT NULL DEFAULT '0',
  `transpasswd` varchar(255) NOT NULL DEFAULT '',
  `clkey` varchar(100) NOT NULL DEFAULT '',
  `activation` varchar(5) NOT NULL DEFAULT 'T',
  `template` char(3) NOT NULL DEFAULT 'N',
  `company_name` varchar(255) NOT NULL DEFAULT '',
  `contact_persons` varchar(255) NOT NULL DEFAULT '',
  `contact_emails` varchar(255) NOT NULL DEFAULT '',
  `address` varchar(255) DEFAULT '',
  `city` varchar(255) DEFAULT '',
  `state` varchar(255) DEFAULT '',
  `zip` varchar(255) DEFAULT '',
  `country` varchar(255) DEFAULT '',
  `telno` varchar(255) DEFAULT '',
  `faxno` varchar(255) DEFAULT '',
  `notifyemail` varchar(255) NOT NULL DEFAULT '',
  `haspaid` enum('Y','N') NOT NULL DEFAULT 'N',
  `isservice` enum('Y','N') NOT NULL DEFAULT 'N',
  `icici` enum('Y','N') NOT NULL DEFAULT 'N',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `aptprompt` int(11) DEFAULT '0',
  `reserves` int(11) DEFAULT '0',
  `newcompany` varchar(255) DEFAULT '',
  `brandname` varchar(255) DEFAULT '',
  `sitename` varchar(255) DEFAULT '',
  `directors` varchar(255) DEFAULT NULL,
  `employees` varchar(255) DEFAULT NULL,
  `potentialbusiness` varchar(255) DEFAULT NULL,
  `bussinessaddress` varchar(255) DEFAULT NULL,
  `acdetails` varchar(255) DEFAULT NULL,
  `registeredaddress` varchar(255) DEFAULT NULL,
  `hralertproof` enum('Y','N') DEFAULT 'N',
  `mitigationmail` enum('Y','N') DEFAULT 'Y',
  `chargeper` int(11) DEFAULT '500',
  `fixamount` decimal(9,2) DEFAULT '0.00',
  `autoredirect` enum('Y','N') DEFAULT 'N',
  `datamismatchproof` enum('Y','N') DEFAULT 'N',
  `vbv` enum('Y','N') DEFAULT 'N',
  `bussinessdevexecutive` varchar(255) DEFAULT NULL,
  `version` varchar(6) DEFAULT '1',
  `custremindermail` enum('Y','N') DEFAULT 'Y',
  `reserve_reason` text,
  `proprietor` varchar(100) DEFAULT NULL,
  `proprietorAddress` varchar(255) DEFAULT NULL,
  `proprietorPhNo` varchar(25) DEFAULT NULL,
  `OrganisationRegNo` varchar(100) DEFAULT NULL,
  `partnerNameAddress` varchar(255) DEFAULT NULL,
  `directorsNameAddress` varchar(255) DEFAULT NULL,
  `modify_merchant_details` enum('Y','N') DEFAULT 'Y',
  `modify_company_details` enum('Y','N') DEFAULT 'Y',
  `pan` varchar(25) DEFAULT NULL,
  `company_type` varchar(50) DEFAULT 'Propritory',
  `currency` char(3) DEFAULT NULL,
  `hrparameterised` enum('Y','N') DEFAULT 'N',
  `taxper` int(11) DEFAULT '1224',
  `checksumalgo` varchar(10) DEFAULT 'MD5',
  `accountid` int(2) DEFAULT NULL,
  `reversalcharge` int(2) DEFAULT '0',
  `withdrawalcharge` int(2) DEFAULT '0',
  `chargebackcharge` int(2) DEFAULT '0',
  `partnerId` int(4) DEFAULT '1',
  `isPharma` enum('Y','N') NOT NULL DEFAULT 'N',
  `isValidateEmail` enum('Y','N') NOT NULL DEFAULT 'N',
  `daily_amount_limit` decimal(9,2) DEFAULT '100.00',
  `monthly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `daily_card_limit` int(11) DEFAULT '5',
  `weekly_card_limit` int(11) DEFAULT '10',
  `monthly_card_limit` int(11) DEFAULT '20',
  `check_limit` tinyint(1) DEFAULT '0',
  `masterCardSupported` enum('Y','N') NOT NULL DEFAULT 'N',
  `isPoweredBy` enum('Y','N') DEFAULT 'Y',
  `invoicetemplate` enum('Y','N') DEFAULT 'Y',
  `iswhitelisted` enum('Y','N') DEFAULT 'N',
  `notificationUrl` varchar(255) DEFAULT NULL,
  `card_transaction_limit` tinyint(1) DEFAULT '0',
  `card_check_limit` tinyint(1) DEFAULT '0',
  `daily_card_amount_limit` decimal(9,2) DEFAULT '1000.00',
  `weekly_card_amount_limit` decimal(9,2) DEFAULT '5000.00',
  `monthly_card_amount_limit` decimal(9,2) DEFAULT '10000.00',
  `ismerchantlogo` enum('Y','N') NOT NULL DEFAULT 'N',
  `merchantlogoname` varchar(50) DEFAULT NULL,
  `refunddailylimit` int(3) NOT NULL DEFAULT '3',
  `isrefund` enum('Y','N') NOT NULL DEFAULT 'Y',
  `isPODRequired` enum('Y','N') NOT NULL DEFAULT 'N',
  `chargebacksaleratio` decimal(3,2) NOT NULL DEFAULT '0.02',
  `fraudsaleratio` decimal(3,2) NOT NULL DEFAULT '0.02',
  `agentId` int(4) NOT NULL DEFAULT '1',
  `isIpWhitelisted` enum('Y','N') NOT NULL DEFAULT 'N',
  `autoSelectTerminal` enum('Y','N') DEFAULT 'Y',
  `maxScoreAllowed` int(5) NOT NULL DEFAULT '50',
  `maxScoreAutoReversal` int(5) NOT NULL DEFAULT '101',
  `accid` bigint(20) NOT NULL,
  `weekly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `isappmanageractivate` enum('Y','N') NOT NULL DEFAULT 'N',
  `ispcilogo` enum('Y','N') NOT NULL DEFAULT 'Y',
  `ispartnerlogo` enum('Y','N') NOT NULL DEFAULT 'Y',
  `iscardregistrationallowed` enum('Y','N') NOT NULL DEFAULT 'N',
  `is_recurring` enum('Y','N') NOT NULL DEFAULT 'N',
  `isRestrictedTicket` enum('Y','N') NOT NULL DEFAULT 'N',
  `isTokenizationAllowed` enum('Y','N') NOT NULL DEFAULT 'N',
  `tokenvaliddays` int(11) NOT NULL DEFAULT '30',
  `isAddrDetailsRequired` enum('Y','N') NOT NULL DEFAULT 'N',
  `blacklistTransaction` enum('Y','N') NOT NULL DEFAULT 'N',
  `addressValidation` enum('Y','N') NOT NULL DEFAULT 'Y',
  `flightMode` enum('Y','N') NOT NULL DEFAULT 'N',
  `onlineFraudCheck` enum('Y','N') NOT NULL DEFAULT 'N',
  `emailSent` enum('Y','N') NOT NULL DEFAULT 'Y',
  `isExcessCaptureAllowed` enum('Y','N') NOT NULL DEFAULT 'N',
  `isSplitPayment` enum('Y','N') NOT NULL DEFAULT 'N',
  `splitPaymentType` enum('Terminal','Merchant') NOT NULL DEFAULT 'Terminal',
  `addressDetails` enum('Y','N') NOT NULL DEFAULT 'Y',
  `isConicalStatusChart` enum('Y','N') NOT NULL DEFAULT 'Y',
  `refundallowed_days` int(10) NOT NULL DEFAULT '180',
  `chargebackallowed_days` int(10) NOT NULL DEFAULT '180',
  `isspeedoptionactivate` enum('Y','N') NOT NULL DEFAULT 'N',
  `isCardEncryptionEnable` enum('N','Y') NOT NULL DEFAULT 'N',
  `is_rest_whitelisted` enum('N','Y') NOT NULL DEFAULT 'Y',
  `smsactivation` enum('Y','N') NOT NULL DEFAULT 'N',
  `customersmsactivation` enum('Y','N') NOT NULL DEFAULT 'N',
  `emailLimitEnabled` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`memberid`),
  UNIQUE KEY `login` (`login`),
  KEY `idx_activation_icici_members` (`activation`,`icici`)
) ENGINE=InnoDB AUTO_INCREMENT=11023 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `members_limit` */

DROP TABLE IF EXISTS `members_limit`;

CREATE TABLE `members_limit` (
  `toid` int(11) NOT NULL DEFAULT '0',
  `max_amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `max_no_of_cards` int(5) DEFAULT '0',
  `masterCardSupported` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`toid`),
  KEY `members_limit_toid_idx` (`toid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `merchant_agent_mapping` */

DROP TABLE IF EXISTS `merchant_agent_mapping`;

CREATE TABLE `merchant_agent_mapping` (
  `mappingid` int(10) NOT NULL AUTO_INCREMENT,
  `memberid` int(10) NOT NULL,
  `agentid` int(10) NOT NULL,
  `creationtime` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;

/*Table structure for table `merchant_bankmapping` */

DROP TABLE IF EXISTS `merchant_bankmapping`;

CREATE TABLE `merchant_bankmapping` (
  `mappingid` int(20) NOT NULL AUTO_INCREMENT,
  `memberid` int(20) NOT NULL,
  `pgtypeid` int(20) NOT NULL,
  `creation_date` int(10) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=latin1;

/*Table structure for table `merchant_configuration` */

DROP TABLE IF EXISTS `merchant_configuration`;

CREATE TABLE `merchant_configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberid` int(11) NOT NULL,
  `maincontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `maincontact_phone` varchar(255) NOT NULL DEFAULT '',
  `cbcontact_name` varchar(255) NOT NULL DEFAULT '',
  `cbcontact_mailid` varchar(255) NOT NULL DEFAULT '',
  `cbcontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `cbcontact_phone` varchar(255) NOT NULL DEFAULT '',
  `refundcontact_name` varchar(255) NOT NULL DEFAULT '',
  `refundcontact_mailid` varchar(255) NOT NULL DEFAULT '',
  `refundcontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `refundcontact_phone` varchar(255) NOT NULL DEFAULT '',
  `salescontact_name` varchar(255) NOT NULL DEFAULT '',
  `salescontact_mailid` varchar(255) NOT NULL DEFAULT '',
  `salescontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `salescontact_phone` varchar(255) NOT NULL DEFAULT '',
  `fraudcontact_name` varchar(255) NOT NULL DEFAULT '',
  `fraudcontact_mailid` varchar(255) NOT NULL DEFAULT '',
  `fraudcontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `fraudcontact_phone` varchar(255) NOT NULL DEFAULT '',
  `technicalcontact_name` varchar(255) NOT NULL DEFAULT '',
  `technicalcontact_mailid` varchar(255) NOT NULL DEFAULT '',
  `technicalcontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `technicalcontact_phone` varchar(255) NOT NULL DEFAULT '',
  `billingcontact_ccmailid` varchar(255) NOT NULL DEFAULT '',
  `billingcontact_phone` varchar(255) NOT NULL DEFAULT '',
  `billingcontact_name` varchar(255) NOT NULL DEFAULT '',
  `billingcontact_mailid` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=latin1;

/*Table structure for table `merchant_fssubaccount_mappping` */

DROP TABLE IF EXISTS `merchant_fssubaccount_mappping`;

CREATE TABLE `merchant_fssubaccount_mappping` (
  `merchantfraudserviceid` int(10) NOT NULL AUTO_INCREMENT,
  `memberid` int(10) NOT NULL,
  `fssubaccountid` int(10) NOT NULL,
  `isactive` enum('Y','N') NOT NULL DEFAULT 'N',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isvisible` enum('Y','N') NOT NULL DEFAULT 'N',
  `isonlinefraudcheck` enum('Y','N') NOT NULL DEFAULT 'Y',
  `isapiuser` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`merchantfraudserviceid`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;

/*Table structure for table `merchant_modules_master` */

DROP TABLE IF EXISTS `merchant_modules_master`;

CREATE TABLE `merchant_modules_master` (
  `moduleid` int(10) NOT NULL AUTO_INCREMENT,
  `modulename` varchar(255) NOT NULL DEFAULT '',
  `modulecreationtime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`moduleid`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

/*Table structure for table `merchant_random_charges` */

DROP TABLE IF EXISTS `merchant_random_charges`;

CREATE TABLE `merchant_random_charges` (
  `merchantrdmchargeid` int(11) NOT NULL AUTO_INCREMENT,
  `bankwireid` int(11) NOT NULL,
  `memberid` int(11) NOT NULL,
  `terminalid` int(11) NOT NULL,
  `chargetype` enum('Added','Deducted') NOT NULL DEFAULT 'Deducted',
  `chargename` varchar(255) NOT NULL,
  `chargerate` decimal(9,2) NOT NULL,
  `valuetype` enum('Percentage','FlatRate') NOT NULL DEFAULT 'Percentage',
  `chargecounter` int(11) DEFAULT NULL,
  `chargeamount` decimal(9,2) NOT NULL,
  `chargevalue` decimal(9,2) NOT NULL,
  `chargeremark` varchar(255) NOT NULL,
  PRIMARY KEY (`merchantrdmchargeid`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

/*Table structure for table `merchant_users_modules_mapping` */

DROP TABLE IF EXISTS `merchant_users_modules_mapping`;

CREATE TABLE `merchant_users_modules_mapping` (
  `mappingid` int(10) NOT NULL AUTO_INCREMENT,
  `memberid` int(10) NOT NULL,
  `userid` int(10) NOT NULL,
  `moduleid` int(10) NOT NULL,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=163 DEFAULT CHARSET=latin1;

/*Table structure for table `merchant_wiremanager` */

DROP TABLE IF EXISTS `merchant_wiremanager`;

CREATE TABLE `merchant_wiremanager` (
  `settledid` int(10) NOT NULL AUTO_INCREMENT,
  `settledate` timestamp NULL DEFAULT NULL,
  `firstdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `amount` double(9,2) NOT NULL,
  `balanceamount` double(9,2) NOT NULL,
  `netfinalamount` double(9,2) NOT NULL,
  `unpaidamount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(5) NOT NULL,
  `status` varchar(25) NOT NULL,
  `settlementreportfilepath` varchar(255) NOT NULL,
  `settledtransactionfilepath` varchar(255) NOT NULL,
  `markedfordeletion` enum('Y','N') NOT NULL DEFAULT 'N',
  `wirecreationtime` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `toid` int(10) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `accountid` int(10) NOT NULL,
  `paymodeid` int(10) NOT NULL,
  `cardtypeid` int(10) NOT NULL,
  `isrollingreserveincluded` enum('Y','N') NOT NULL DEFAULT 'N',
  `rollingreservereleasedateupto` timestamp NULL DEFAULT NULL,
  `settlementcycle_no` int(10) DEFAULT NULL,
  `declinedcoverdateupto` timestamp NULL DEFAULT NULL,
  `reversedcoverdateupto` timestamp NULL DEFAULT NULL,
  `chargebackcoverdateupto` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`settledid`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

/*Table structure for table `mid_manager` */

DROP TABLE IF EXISTS `mid_manager`;

CREATE TABLE `mid_manager` (
  `mmid` int(7) NOT NULL AUTO_INCREMENT,
  `pspid` int(7) NOT NULL DEFAULT '0',
  `accountid` int(7) NOT NULL DEFAULT '0',
  `memberid` int(7) NOT NULL DEFAULT '0',
  `mcc` int(7) NOT NULL DEFAULT '0',
  `reserve` float(7,2) NOT NULL DEFAULT '0.00',
  `mvolume` float(9,2) NOT NULL DEFAULT '0.00',
  `commission` float(7,2) NOT NULL DEFAULT '0.00',
  `fixfee` float(7,2) NOT NULL DEFAULT '0.00',
  `master` char(1) NOT NULL DEFAULT '',
  `flag` char(1) NOT NULL DEFAULT '',
  PRIMARY KEY (`mmid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `monitoring_parameter_log_details` */

DROP TABLE IF EXISTS `monitoring_parameter_log_details`;

CREATE TABLE `monitoring_parameter_log_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberid` int(11) NOT NULL,
  `terminalid` int(11) NOT NULL,
  `alert_threshold` double(9,2) DEFAULT '0.00',
  `suspension_threshold` double(9,2) DEFAULT '0.00',
  `alert_activation` enum('Y','N') NOT NULL DEFAULT 'N',
  `suspension_activation` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttoadmin` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttomerchant` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttopartner` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttoagent` enum('Y','N') NOT NULL DEFAULT 'N',
  `monitoring_para_id` int(11) NOT NULL DEFAULT '0',
  `dtstamp` int(11) DEFAULT NULL,
  `alertMessage` varchar(255) DEFAULT '',
  `isalerttoadmin_sales` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_rf` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_cb` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_tech` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_sales` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_rf` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_cb` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_tech` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_sales` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_rf` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_cb` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_tech` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_sales` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_rf` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_cb` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_tech` enum('Y','N') DEFAULT 'N',
  `isdailyexecution` enum('Y','N') DEFAULT 'N',
  `isweeklyexecution` enum('Y','N') DEFAULT 'N',
  `ismonthlyexecution` enum('Y','N') DEFAULT 'N',
  `weekly_alert_threshold` double(9,2) DEFAULT '0.00',
  `weekly_suspension_threshold` double(9,2) DEFAULT '0.00',
  `monthly_alert_threshold` double(9,2) DEFAULT '0.00',
  `monthly_suspension_threshold` double(9,2) DEFAULT '0.00',
  `actionExecutor` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1251 DEFAULT CHARSET=latin1;

/*Table structure for table `monitoring_parameter_master` */

DROP TABLE IF EXISTS `monitoring_parameter_master`;

CREATE TABLE `monitoring_parameter_master` (
  `monitoing_para_id` int(11) NOT NULL AUTO_INCREMENT,
  `monitoing_para_name` varchar(255) NOT NULL DEFAULT '',
  `monitoing_para_tech_name` varchar(255) DEFAULT '',
  `monitoing_category` varchar(255) NOT NULL DEFAULT '',
  `monitoing_keyword` varchar(255) NOT NULL DEFAULT '',
  `monitoing_subkeyword` varchar(255) NOT NULL DEFAULT '',
  `monitoing_alert_category` varchar(255) NOT NULL DEFAULT '',
  `monitoing_frequency` varchar(255) NOT NULL DEFAULT '',
  `monitoing_onchannel` varchar(255) NOT NULL DEFAULT '',
  `monitoring_deviation` varchar(255) NOT NULL DEFAULT '',
  `monitoring_unit` varchar(255) NOT NULL DEFAULT '',
  `default_alert_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `default_suspension_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `default_alert_activation` enum('Y','N') NOT NULL DEFAULT 'N',
  `default_suspension_activation` enum('Y','N') NOT NULL DEFAULT 'N',
  `default_isAlertToAdmin` enum('Y','N') NOT NULL DEFAULT 'N',
  `default_isAlertToMerchant` enum('Y','N') NOT NULL DEFAULT 'N',
  `default_isAlertToPartner` enum('Y','N') NOT NULL DEFAULT 'N',
  `default_isAlertToAgent` enum('Y','N') NOT NULL DEFAULT 'N',
  `default_alertMsg` varchar(255) NOT NULL DEFAULT '',
  `isalerttoadmin_sales` enum('Y','N') NOT NULL DEFAULT 'N',
  `isalerttoadmin_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_rf` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_cb` enum('Y','N') DEFAULT 'N',
  `isalerttoadmin_tech` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_sales` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_rf` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_cb` enum('Y','N') DEFAULT 'N',
  `isalerttomerchant_tech` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_sales` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_rf` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_cb` enum('Y','N') DEFAULT 'N',
  `isalerttopartner_tech` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_sales` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_fraud` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_rf` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_cb` enum('Y','N') DEFAULT 'N',
  `isalerttoagent_tech` enum('Y','N') DEFAULT 'N',
  `isdailyexecution` enum('Y','N') NOT NULL DEFAULT 'N',
  `isweeklyexecution` enum('Y','N') NOT NULL DEFAULT 'N',
  `ismonthlyexecution` enum('Y','N') NOT NULL DEFAULT 'N',
  `weekly_alert_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `weekly_suspension_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `monthly_alert_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `monthly_suspension_threshold` double(9,2) NOT NULL DEFAULT '0.00',
  `displayChartType` enum('LineChart','DoughnutChart','BarChart','ProgressBarChart') DEFAULT NULL,
  PRIMARY KEY (`monitoing_para_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10110 DEFAULT CHARSET=latin1;

/*Table structure for table `partner_commission` */

DROP TABLE IF EXISTS `partner_commission`;

CREATE TABLE `partner_commission` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `partnerid` int(10) NOT NULL,
  `memberid` int(10) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `chargeid` int(10) NOT NULL,
  `commission_value` decimal(7,2) NOT NULL DEFAULT '0.00',
  `startdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `enddate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `sequence_no` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;

/*Table structure for table `partner_fsaccounts_mapping` */

DROP TABLE IF EXISTS `partner_fsaccounts_mapping`;

CREATE TABLE `partner_fsaccounts_mapping` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `partnerid` int(10) NOT NULL,
  `fsaccountid` int(10) NOT NULL,
  `dtstamp` int(10) NOT NULL DEFAULT '0',
  `isActive` enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

/*Table structure for table `partner_modules_mapping` */

DROP TABLE IF EXISTS `partner_modules_mapping`;

CREATE TABLE `partner_modules_mapping` (
  `mappingid` int(10) NOT NULL AUTO_INCREMENT,
  `partnerid` int(10) NOT NULL,
  `moduleid` int(10) NOT NULL,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `partner_modules_master` */

DROP TABLE IF EXISTS `partner_modules_master`;

CREATE TABLE `partner_modules_master` (
  `moduleid` int(10) NOT NULL AUTO_INCREMENT,
  `modulename` varchar(255) NOT NULL,
  `modulecreationtime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`moduleid`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

/*Table structure for table `partner_users` */

DROP TABLE IF EXISTS `partner_users`;

CREATE TABLE `partner_users` (
  `userid` int(9) NOT NULL AUTO_INCREMENT,
  `partnerid` int(10) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `fpasswd` varchar(255) DEFAULT '',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `accid` bigint(20) NOT NULL,
  `contact_emails` varchar(50) DEFAULT NULL,
  `fdtstamp` int(11) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=latin1;

/*Table structure for table `partner_users_modules_mapping` */

DROP TABLE IF EXISTS `partner_users_modules_mapping`;

CREATE TABLE `partner_users_modules_mapping` (
  `mappingid` int(10) NOT NULL AUTO_INCREMENT,
  `partnerid` int(10) NOT NULL,
  `userid` int(10) NOT NULL,
  `moduleid` int(10) NOT NULL,
  PRIMARY KEY (`mappingid`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=latin1;

/*Table structure for table `partner_wiremanager` */

DROP TABLE IF EXISTS `partner_wiremanager`;

CREATE TABLE `partner_wiremanager` (
  `settledid` int(10) NOT NULL AUTO_INCREMENT,
  `settledate` timestamp NULL DEFAULT NULL,
  `settlementstartdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `settlementenddate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `partnertype` varchar(50) NOT NULL,
  `partnerchargeamount` double(9,2) NOT NULL DEFAULT '0.00',
  `partnerunpaidamount` double(9,2) NOT NULL DEFAULT '0.00',
  `partnertotalfundedamount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(5) NOT NULL,
  `status` varchar(25) NOT NULL,
  `settlementreportfilename` varchar(255) NOT NULL,
  `markedfordeletion` enum('Y','N') NOT NULL DEFAULT 'N',
  `partnerid` int(10) NOT NULL,
  `wirecreationtime` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `toid` int(10) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `accountid` int(10) NOT NULL,
  `paymodeid` int(10) NOT NULL,
  `cardtypeid` int(10) NOT NULL,
  `declinedcoverdateupto` timestamp NULL DEFAULT NULL,
  `reversedcoverdateupto` timestamp NULL DEFAULT NULL,
  `chargebackcoverdateupto` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`settledid`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

/*Table structure for table `partners` */

DROP TABLE IF EXISTS `partners`;

CREATE TABLE `partners` (
  `partnerId` int(4) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL DEFAULT '',
  `passwd` varchar(255) NOT NULL DEFAULT '',
  `fpasswd` varchar(255) NOT NULL DEFAULT '',
  `fdtstamp` int(11) NOT NULL DEFAULT '0',
  `clkey` varchar(100) NOT NULL DEFAULT '',
  `activation` varchar(5) NOT NULL DEFAULT 'T',
  `template` char(3) NOT NULL DEFAULT 'N',
  `partnerName` varchar(255) NOT NULL,
  `contact_persons` varchar(255) DEFAULT NULL,
  `contact_emails` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `telno` varchar(255) DEFAULT NULL,
  `faxno` varchar(255) DEFAULT NULL,
  `logoName` varchar(255) DEFAULT NULL,
  `notifyemail` varchar(255) NOT NULL DEFAULT '',
  `haspaid` enum('Y','N') NOT NULL DEFAULT 'N',
  `isservice` enum('Y','N') NOT NULL DEFAULT 'N',
  `icici` enum('Y','N') NOT NULL DEFAULT 'N',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `vbv` enum('Y','N') DEFAULT 'N',
  `modify_merchant_details` enum('Y','N') DEFAULT 'Y',
  `modify_company_details` enum('Y','N') DEFAULT 'Y',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isPharma` enum('Y','N') NOT NULL DEFAULT 'N',
  `isValidateEmail` enum('Y','N') NOT NULL DEFAULT 'N',
  `daily_amount_limit` decimal(9,2) DEFAULT '100.00',
  `monthly_amount_limit` decimal(9,2) DEFAULT '100.00',
  `daily_card_limit` int(11) DEFAULT '5',
  `weekly_card_limit` int(11) DEFAULT '10',
  `monthly_card_limit` int(11) DEFAULT '20',
  `check_limit` tinyint(1) DEFAULT '0',
  `siteurl` varchar(255) DEFAULT NULL,
  `supporturl` varchar(255) DEFAULT NULL,
  `companyadminid` varchar(255) DEFAULT NULL,
  `companysupportmailid` varchar(255) DEFAULT NULL,
  `salesemail` varchar(255) DEFAULT NULL,
  `billingemail` varchar(255) DEFAULT NULL,
  `fraudemailid` varchar(255) DEFAULT NULL,
  `companyfromemail` varchar(255) DEFAULT NULL,
  `supportfromemail` varchar(255) DEFAULT NULL,
  `superadminid` int(3) NOT NULL,
  `smtp_host` varchar(255) DEFAULT NULL,
  `smtp_port` int(5) DEFAULT NULL,
  `smtp_user` varchar(255) DEFAULT NULL,
  `smtp_password` varchar(255) DEFAULT NULL,
  `ipaddress` varchar(20) DEFAULT NULL,
  `isIpWhitelisted` enum('Y','N') NOT NULL DEFAULT 'N',
  `isFlightPartner` enum('Y','N') NOT NULL DEFAULT 'N',
  `accid` bigint(20) NOT NULL,
  `hosturl` varchar(255) NOT NULL DEFAULT 'secure.paymentz.com',
  `responseType` enum('String','XML','JSON') NOT NULL DEFAULT 'String',
  `responseLength` enum('Default','Full') NOT NULL DEFAULT 'Default',
  `bankApplicationURL` varchar(255) DEFAULT NULL,
  `isRefund` enum('Y','N') NOT NULL DEFAULT 'N',
  `isTokenizationAllowed` enum('Y','N') DEFAULT 'N',
  `isMerchantRequiredForCardRegistration` enum('Y','N') DEFAULT 'Y',
  `isAddressRequiredForTokenTransaction` enum('Y','N') DEFAULT 'Y',
  `isMerchantRequiredForCardholderRegistration` enum('Y','N') DEFAULT 'Y',
  `tokenValidDays` int(10) DEFAULT '90',
  `isCardEncryptionEnable` enum('Y','N') NOT NULL DEFAULT 'N',
  `salescontactname` varchar(255) DEFAULT NULL,
  `fraudcontactname` varchar(255) DEFAULT NULL,
  `technicalemailid` varchar(255) DEFAULT NULL,
  `technicalcontactname` varchar(255) DEFAULT NULL,
  `chargebackemailid` varchar(255) DEFAULT NULL,
  `chargebackcontactname` varchar(255) DEFAULT NULL,
  `refundemailid` varchar(255) DEFAULT NULL,
  `refundcontactname` varchar(255) DEFAULT NULL,
  `billingcontactname` varchar(255) NOT NULL DEFAULT '',
  `notifycontactname` varchar(255) NOT NULL DEFAULT '',
  `is_rest_whitelisted` enum('N','Y') NOT NULL DEFAULT 'Y',
  `splitpayment` enum('Y','N') NOT NULL DEFAULT 'N',
  `splitpaymenttype` enum('terminal','merchant') NOT NULL DEFAULT 'merchant',
  `addressvalidation` enum('Y','N') NOT NULL DEFAULT 'Y',
  `addressdetaildisplay` enum('Y','N') NOT NULL DEFAULT 'Y',
  `flightMode` enum('Y','N') NOT NULL DEFAULT 'N',
  `iconName` varchar(255) NOT NULL DEFAULT '',
  `reporting_currency` enum('USD','EUR','GBP','CAD','INR','PEN','JPY') NOT NULL DEFAULT 'USD',
  `autoRedirect` enum('Y','N') DEFAULT 'N',
  `sms_user` varchar(255) NOT NULL DEFAULT '',
  `sms_password` varchar(255) NOT NULL DEFAULT '',
  `from_sms` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`partnerId`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8;

/*Table structure for table `payife_fields` */

DROP TABLE IF EXISTS `payife_fields`;

CREATE TABLE `payife_fields` (
  `fieldid` int(20) NOT NULL AUTO_INCREMENT,
  `tableid` int(20) NOT NULL,
  `field_name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `data_type` enum('INT','VARCHAR','ENUM','DECIMAL') NOT NULL DEFAULT 'VARCHAR',
  `enum_value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`fieldid`),
  KEY `tableid` (`tableid`),
  CONSTRAINT `payife_fields_ibfk_1` FOREIGN KEY (`tableid`) REFERENCES `payife_tables` (`tableid`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=latin1;

/*Table structure for table `payife_tables` */

DROP TABLE IF EXISTS `payife_tables`;

CREATE TABLE `payife_tables` (
  `tableid` int(20) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `aliasname` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`tableid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

/*Table structure for table `payment_brand` */

DROP TABLE IF EXISTS `payment_brand`;

CREATE TABLE `payment_brand` (
  `id` int(11) DEFAULT NULL,
  `brand` varchar(465) DEFAULT NULL,
  `paymodeid` int(11) DEFAULT NULL,
  `cardtypeid` int(11) DEFAULT NULL,
  `transactionType` enum('sync','async') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `payment_type` */

DROP TABLE IF EXISTS `payment_type`;

CREATE TABLE `payment_type` (
  `paymodeid` int(4) NOT NULL DEFAULT '0',
  `paymode` varchar(4) DEFAULT NULL,
  `paymentType` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`paymodeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `preferences` */

DROP TABLE IF EXISTS `preferences`;

CREATE TABLE `preferences` (
  `memberid` int(11) NOT NULL DEFAULT '0',
  `type` varchar(255) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `proof_received_data` */

DROP TABLE IF EXISTS `proof_received_data`;

CREATE TABLE `proof_received_data` (
  `icicitransid` int(11) NOT NULL DEFAULT '0',
  `cardno` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `expdate` varchar(255) DEFAULT NULL,
  `ipaddress` varchar(15) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `rawsettlement` */

DROP TABLE IF EXISTS `rawsettlement`;

CREATE TABLE `rawsettlement` (
  `rawid` int(11) NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) NOT NULL DEFAULT '',
  `totalno` int(11) NOT NULL DEFAULT '0',
  `settledno` int(11) NOT NULL DEFAULT '0',
  `unsettledno` int(11) NOT NULL DEFAULT '0',
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `settledamount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `unsettledamount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rawid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `recurring_transaction_details` */

DROP TABLE IF EXISTS `recurring_transaction_details`;

CREATE TABLE `recurring_transaction_details` (
  `recurring_transaction_details_id` int(10) NOT NULL AUTO_INCREMENT,
  `recurring_subscription_id` int(10) NOT NULL,
  `bankRecurringBillingID` varchar(50) DEFAULT NULL,
  `parentBankTransactionID` varchar(50) DEFAULT NULL,
  `newBankTransactionID` varchar(50) DEFAULT NULL,
  `parentPaymentzTransactionID` varchar(50) DEFAULT NULL,
  `newPaymentzTransactionID` varchar(50) DEFAULT NULL,
  `rec_interval` enum('Day','Month') DEFAULT NULL,
  `rec_frequency` int(2) DEFAULT NULL,
  `rec_runDate` int(2) DEFAULT NULL,
  `amount` decimal(9,2) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `transactionStatus` varchar(20) DEFAULT NULL,
  `recurringRunDate` varchar(50) DEFAULT NULL,
  `dtstamp` int(15) NOT NULL DEFAULT '0',
  PRIMARY KEY (`recurring_transaction_details_id`)
) ENGINE=InnoDB AUTO_INCREMENT=477 DEFAULT CHARSET=latin1;

/*Table structure for table `recurring_transaction_subscription` */

DROP TABLE IF EXISTS `recurring_transaction_subscription`;

CREATE TABLE `recurring_transaction_subscription` (
  `recurring_subscription_id` int(10) NOT NULL AUTO_INCREMENT,
  `originatingTrackingid` int(10) NOT NULL,
  `rec_interval` varchar(10) DEFAULT NULL,
  `rec_frequency` varchar(2) DEFAULT NULL,
  `rec_runDate` varchar(2) DEFAULT NULL,
  `amount` decimal(13,2) NOT NULL,
  `card_holder_name` varchar(255) NOT NULL,
  `first_six` varchar(6) DEFAULT NULL,
  `last_four` varchar(4) DEFAULT NULL,
  `memberid` int(10) DEFAULT NULL,
  `recurring_status` enum('Activated','Deactivated','Deleted') NOT NULL,
  `rbid` varchar(15) DEFAULT NULL,
  `dtstamp` int(15) NOT NULL DEFAULT '0',
  `TIMESTAMP` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `recurringType` varchar(10) DEFAULT NULL,
  `terminalid` int(10) NOT NULL,
  `iban` varchar(50) DEFAULT NULL,
  `accountNumber` varchar(10) DEFAULT NULL,
  `routingNumber` varchar(10) DEFAULT NULL,
  `accountType` varchar(10) DEFAULT NULL,
  `bic` varchar(50) DEFAULT NULL,
  `paymodeid` varchar(2) DEFAULT NULL,
  `cardtypeid` varchar(2) DEFAULT NULL,
  `original_banktransaction_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`recurring_subscription_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4809 DEFAULT CHARSET=latin1;

/*Table structure for table `registration_master` */

DROP TABLE IF EXISTS `registration_master`;

CREATE TABLE `registration_master` (
  `registration_id` int(10) NOT NULL AUTO_INCREMENT,
  `registration_token` varchar(100) NOT NULL,
  `tokenid` int(10) NOT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `terminalid` int(11) DEFAULT NULL,
  `isActive` enum('Y','N') NOT NULL DEFAULT 'Y',
  `partnerid` int(10) DEFAULT NULL,
  `customerid` varchar(50) DEFAULT NULL,
  `tokencreation_date` int(11) DEFAULT NULL,
  `tokeninactive_date` int(11) DEFAULT NULL,
  `country` varchar(4) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `telnocc` int(5) DEFAULT NULL,
  `telno` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `birthdate` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL,
  `memberid` int(10) DEFAULT NULL,
  PRIMARY KEY (`registration_id`)
) ENGINE=InnoDB AUTO_INCREMENT=233 DEFAULT CHARSET=latin1;

/*Table structure for table `registration_member_mapping` */

DROP TABLE IF EXISTS `registration_member_mapping`;

CREATE TABLE `registration_member_mapping` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `registration_tokenid` int(10) NOT NULL,
  `toid` int(10) NOT NULL,
  `tracking_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

/*Table structure for table `registration_transaction_details` */

DROP TABLE IF EXISTS `registration_transaction_details`;

CREATE TABLE `registration_transaction_details` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `toid` int(10) NOT NULL,
  `trackingid` int(10) NOT NULL,
  `amount` decimal(9,2) NOT NULL,
  `registrationid` int(10) NOT NULL,
  `tokentranstime` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=latin1;

/*Table structure for table `retrival_receipt_history` */

DROP TABLE IF EXISTS `retrival_receipt_history`;

CREATE TABLE `retrival_receipt_history` (
  `icicitransid` int(11) NOT NULL DEFAULT '0',
  `status` varchar(25) DEFAULT NULL,
  `rr_sent_date` int(11) DEFAULT NULL,
  `docs_received_date` int(11) DEFAULT NULL,
  PRIMARY KEY (`icicitransid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Table structure for table `risk_profile` */

DROP TABLE IF EXISTS `risk_profile`;

CREATE TABLE `risk_profile` (
  `profileid` int(30) NOT NULL AUTO_INCREMENT,
  `profile_name` varchar(255) DEFAULT NULL,
  `partner_id` int(30) DEFAULT NULL,
  PRIMARY KEY (`profileid`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

/*Table structure for table `risk_profile_rule` */

DROP TABLE IF EXISTS `risk_profile_rule`;

CREATE TABLE `risk_profile_rule` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `profile_id` int(30) NOT NULL,
  `ruleid` int(30) NOT NULL,
  `isApplicable` enum('Y','N') NOT NULL DEFAULT 'N',
  `score` int(30) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `risk_profile_ibfk_1` (`profile_id`),
  KEY `risk_profile_ibfk_2` (`ruleid`),
  CONSTRAINT `fk_risk_profile_rule_1` FOREIGN KEY (`profile_id`) REFERENCES `risk_profile` (`profileid`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `risk_profile_rule_ibfk_2` FOREIGN KEY (`ruleid`) REFERENCES `risk_rule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;

/*Table structure for table `risk_rule` */

DROP TABLE IF EXISTS `risk_rule`;

CREATE TABLE `risk_rule` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `rule_type` enum('COMPARATOR','DATABASE','REGULAR_EXPRESSION','FLAT_FILE') NOT NULL DEFAULT 'COMPARATOR',
  `query` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;

/*Table structure for table `risk_rule_operation` */

DROP TABLE IF EXISTS `risk_rule_operation`;

CREATE TABLE `risk_rule_operation` (
  `operationId` int(30) NOT NULL AUTO_INCREMENT,
  `ruleId` int(30) NOT NULL,
  `inputName` varchar(255) DEFAULT NULL,
  `regex` varchar(255) DEFAULT NULL,
  `operator` enum('EQUAL_TO','NOT_EQUAL_TO','GREATER_THAN','LESS_THAN','GREATER_THAN_EQUALS_TO','LESS_THAN_EQUALS_TO','BETWEEN','CONTAINS','NOT_CONTAINS') NOT NULL DEFAULT 'EQUAL_TO',
  `comparator` enum('OR','AND') DEFAULT NULL,
  `value1` varchar(255) DEFAULT NULL,
  `value2` varchar(255) DEFAULT NULL,
  `inputType` enum('VARCHAR','INT','DECIMAL','ENUM') NOT NULL DEFAULT 'VARCHAR',
  `enumValue` varchar(1000) DEFAULT NULL,
  `isMandatory` enum('Y','N') NOT NULL DEFAULT 'N',
  `sequence` int(11) NOT NULL,
  PRIMARY KEY (`operationId`),
  KEY `risk_rule_operation_ibfk_1` (`ruleId`),
  CONSTRAINT `risk_rule_operation_ibfk_1` FOREIGN KEY (`ruleId`) REFERENCES `risk_rule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=latin1;

/*Table structure for table `rs_codes` */

DROP TABLE IF EXISTS `rs_codes`;

CREATE TABLE `rs_codes` (
  `rsid` int(7) NOT NULL AUTO_INCREMENT,
  `code` varchar(7) NOT NULL DEFAULT '',
  `reason` text NOT NULL,
  `gateway` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`rsid`)
) ENGINE=MyISAM AUTO_INCREMENT=363 DEFAULT CHARSET=latin1;

/*Table structure for table `rule_master` */

DROP TABLE IF EXISTS `rule_master`;

CREATE TABLE `rule_master` (
  `ruleid` int(10) NOT NULL AUTO_INCREMENT,
  `rulename` varchar(255) NOT NULL DEFAULT '',
  `ruledescription` varchar(255) NOT NULL DEFAULT '',
  `rulegroup` enum('Dynamic','HardCoded','Other') NOT NULL,
  `score` int(50) NOT NULL,
  `dtstamp` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Enable','Disable') NOT NULL DEFAULT 'Enable',
  PRIMARY KEY (`ruleid`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

/*Table structure for table `sepa_token_master` */

DROP TABLE IF EXISTS `sepa_token_master`;

CREATE TABLE `sepa_token_master` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `mandate` varchar(100) NOT NULL,
  `toid` int(10) NOT NULL,
  `trackingid` int(11) DEFAULT NULL,
  `mandateURL` varchar(255) DEFAULT NULL,
  `revokeMandateURL` varchar(255) DEFAULT NULL,
  `isRecurring` enum('Y','N') NOT NULL DEFAULT 'N',
  `isactive` enum('N','Y') NOT NULL DEFAULT 'Y',
  `mandatelastupdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `mandatecreationtime` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=latin1;

/*Table structure for table `sepa_transaction_history` */

DROP TABLE IF EXISTS `sepa_transaction_history`;

CREATE TABLE `sepa_transaction_history` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `mandateId` int(30) NOT NULL,
  `trackingId` int(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=latin1;

/*Table structure for table `shippingdetails` */

DROP TABLE IF EXISTS `shippingdetails`;

CREATE TABLE `shippingdetails` (
  `trackingid` int(11) NOT NULL,
  `status` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`trackingid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `sms_template` */

DROP TABLE IF EXISTS `sms_template`;

CREATE TABLE `sms_template` (
  `sms_template_id` int(5) NOT NULL AUTO_INCREMENT,
  `sms_template_name` varchar(255) NOT NULL,
  `sms_emplate_filename` varchar(255) NOT NULL,
  PRIMARY KEY (`sms_template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Table structure for table `sms_template_event_entity_mapping` */

DROP TABLE IF EXISTS `sms_template_event_entity_mapping`;

CREATE TABLE `sms_template_event_entity_mapping` (
  `mapping_id` int(11) NOT NULL AUTO_INCREMENT,
  `template_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `from_entity_id` int(11) NOT NULL,
  `to_entity_id` int(11) NOT NULL,
  `subject` varchar(255) NOT NULL,
  PRIMARY KEY (`mapping_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;

/*Table structure for table `subaccount_rule_mapping` */

DROP TABLE IF EXISTS `subaccount_rule_mapping`;

CREATE TABLE `subaccount_rule_mapping` (
  `subaccountruleid` int(10) NOT NULL AUTO_INCREMENT,
  `fssubaccountid` int(10) NOT NULL,
  `ruleid` int(10) NOT NULL,
  `score` int(10) NOT NULL,
  `status` enum('Enable','Disable') NOT NULL DEFAULT 'Enable',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`subaccountruleid`)
) ENGINE=InnoDB AUTO_INCREMENT=428 DEFAULT CHARSET=utf8;

/*Table structure for table `temp` */

DROP TABLE IF EXISTS `temp`;

CREATE TABLE `temp` (
  `merchantid` int(11) DEFAULT NULL,
  `HRcount` int(11) DEFAULT NULL,
  `HRamt` decimal(11,2) DEFAULT NULL,
  `MRcount` int(11) DEFAULT NULL,
  `MRamt` decimal(11,2) DEFAULT NULL,
  `NRcount` int(11) DEFAULT NULL,
  `NRamt` decimal(11,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `template_preferences` */

DROP TABLE IF EXISTS `template_preferences`;

CREATE TABLE `template_preferences` (
  `memberid` int(11) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `value` blob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `token_master` */

DROP TABLE IF EXISTS `token_master`;

CREATE TABLE `token_master` (
  `tokenid` int(10) NOT NULL AUTO_INCREMENT,
  `token` varchar(100) NOT NULL,
  `toid` int(10) DEFAULT NULL,
  `trackingid` int(11) DEFAULT NULL,
  `merchant_orderid` varchar(255) DEFAULT '',
  `terminalid` int(11) DEFAULT NULL,
  `cnum` varchar(255) DEFAULT NULL,
  `expiry_date` varchar(255) DEFAULT NULL,
  `cardholder_firstname` varchar(255) DEFAULT NULL,
  `cardholder_lastname` varchar(255) DEFAULT NULL,
  `cardholderemail` varchar(255) DEFAULT NULL,
  `country` varchar(4) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `birthdate` varchar(50) DEFAULT NULL,
  `language` varchar(50) DEFAULT '',
  `isactive` enum('N','Y') NOT NULL DEFAULT 'Y',
  `tokencreationon` int(11) NOT NULL DEFAULT '0',
  `cardholderid` varchar(50) DEFAULT NULL,
  `partnerid` int(11) DEFAULT NULL,
  `generatedBy` varchar(50) DEFAULT NULL,
  `bank_accountid` int(10) DEFAULT NULL,
  PRIMARY KEY (`tokenid`),
  KEY `token` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=766 DEFAULT CHARSET=latin1;

/*Table structure for table `token_transaction_details` */

DROP TABLE IF EXISTS `token_transaction_details`;

CREATE TABLE `token_transaction_details` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `toid` int(10) NOT NULL,
  `trackingid` int(10) NOT NULL,
  `amount` decimal(9,2) NOT NULL,
  `tokenid` int(10) NOT NULL,
  `tokentranstime` int(11) NOT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=402 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_b4sepaexpress_details` */

DROP TABLE IF EXISTS `transaction_b4sepaexpress_details`;

CREATE TABLE `transaction_b4sepaexpress_details` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `trackingId` int(30) NOT NULL,
  `detailId` int(30) NOT NULL,
  `IBAN` varchar(50) DEFAULT NULL,
  `BIC` varchar(50) DEFAULT NULL,
  `MandateId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=193 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_bankfrick_details` */

DROP TABLE IF EXISTS `transaction_bankfrick_details`;

CREATE TABLE `transaction_bankfrick_details` (
  `detailid` int(11) NOT NULL,
  `referenceid` varchar(255) DEFAULT 'null',
  `shortid` varchar(255) DEFAULT 'null',
  `fxdate` varchar(255) DEFAULT 'null',
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_borgun_details` */

DROP TABLE IF EXISTS `transaction_borgun_details`;

CREATE TABLE `transaction_borgun_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trackingId` varchar(25) NOT NULL,
  `batchId` varchar(50) DEFAULT NULL,
  `authCode` varchar(50) DEFAULT NULL,
  `transaction` varchar(50) DEFAULT NULL,
  `rrn` varchar(50) DEFAULT NULL,
  `actionCode` varchar(50) DEFAULT NULL,
  `dateAndTime` varchar(50) DEFAULT NULL,
  `method` varchar(50) DEFAULT NULL,
  `cardAccId` varchar(50) DEFAULT NULL,
  `transType` varchar(50) DEFAULT NULL,
  `amount` varchar(50) DEFAULT NULL,
  `storeTerminal` varchar(50) DEFAULT NULL,
  `message` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1473 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_common` */

DROP TABLE IF EXISTS `transaction_common`;

CREATE TABLE `transaction_common` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `transid` int(11) NOT NULL DEFAULT '0',
  `accountid` int(11) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `orderdescription` varchar(255) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `captureamount` decimal(9,2) DEFAULT '0.00',
  `refundamount` decimal(9,2) DEFAULT '0.00',
  `chargebackamount` decimal(9,2) DEFAULT '0.00',
  `currency` varchar(3) NOT NULL DEFAULT '',
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) DEFAULT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `firstname` varchar(255) NOT NULL DEFAULT '',
  `lastname` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `ccnum` varchar(255) NOT NULL DEFAULT '',
  `expdate` varchar(255) NOT NULL DEFAULT '',
  `cardtype` varchar(10) DEFAULT NULL,
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `captureinfo` varchar(255) DEFAULT NULL,
  `refundinfo` varchar(255) DEFAULT NULL,
  `chargebackinfo` varchar(255) DEFAULT NULL,
  `ipcode` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `country` varchar(4) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `namecp` enum('Y','N') DEFAULT 'N',
  `cccp` enum('Y','N') DEFAULT 'N',
  `addrcp` enum('Y','N') DEFAULT 'N',
  `hrcode` varchar(30) DEFAULT NULL,
  `proofrequired` enum('Y','N') DEFAULT 'N',
  `machineid` varchar(255) DEFAULT NULL,
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(4) DEFAULT NULL,
  `paymentid` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `ewalletid` varchar(255) DEFAULT NULL,
  `terminalid` int(10) DEFAULT NULL,
  PRIMARY KEY (`trackingid`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=585019 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_common_archive_10Mar14` */

DROP TABLE IF EXISTS `transaction_common_archive_10Mar14`;

CREATE TABLE `transaction_common_archive_10Mar14` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `transid` int(11) NOT NULL DEFAULT '0',
  `accountid` int(11) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `orderdescription` varchar(255) DEFAULT NULL,
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `captureamount` decimal(9,2) DEFAULT '0.00',
  `refundamount` decimal(9,2) DEFAULT '0.00',
  `chargebackamount` decimal(9,2) DEFAULT '0.00',
  `currency` varchar(3) NOT NULL DEFAULT '',
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) DEFAULT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `firstname` varchar(255) NOT NULL DEFAULT '',
  `lastname` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `ccnum` varchar(255) NOT NULL DEFAULT '',
  `expdate` varchar(255) NOT NULL DEFAULT '',
  `cardtype` varchar(10) DEFAULT NULL,
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `captureinfo` varchar(255) DEFAULT NULL,
  `refundinfo` varchar(255) DEFAULT NULL,
  `chargebackinfo` varchar(255) DEFAULT NULL,
  `ipcode` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `country` varchar(4) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `namecp` enum('Y','N') DEFAULT 'N',
  `cccp` enum('Y','N') DEFAULT 'N',
  `addrcp` enum('Y','N') DEFAULT 'N',
  `hrcode` varchar(30) DEFAULT NULL,
  `proofrequired` enum('Y','N') DEFAULT 'N',
  `machineid` varchar(255) DEFAULT NULL,
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(4) DEFAULT NULL,
  `paymentid` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `ewalletid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`trackingid`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_common_details` */

DROP TABLE IF EXISTS `transaction_common_details`;

CREATE TABLE `transaction_common_details` (
  `detailid` int(11) NOT NULL AUTO_INCREMENT,
  `trackingid` int(11) NOT NULL,
  `action` varchar(25) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(13,2) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `responsetransactionid` varchar(255) DEFAULT NULL,
  `responsetransactionstatus` varchar(255) DEFAULT NULL,
  `responsecode` varchar(255) DEFAULT NULL,
  `responsedescription` varchar(255) DEFAULT NULL,
  `responsetime` varchar(255) DEFAULT NULL,
  `responsedescriptor` varchar(255) DEFAULT NULL,
  `responsehashinfo` varchar(255) DEFAULT NULL,
  `transtype` varchar(255) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `actionexecutorid` int(10) DEFAULT NULL,
  `actionexecutorname` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB AUTO_INCREMENT=10139409 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_credorax_details` */

DROP TABLE IF EXISTS `transaction_credorax_details`;

CREATE TABLE `transaction_credorax_details` (
  `detailid` int(11) NOT NULL AUTO_INCREMENT,
  `trackingid` int(11) NOT NULL,
  `action` varchar(25) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `amount` decimal(9,2) DEFAULT NULL,
  `authcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB AUTO_INCREMENT=10134839 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_cup` */

DROP TABLE IF EXISTS `transaction_cup`;

CREATE TABLE `transaction_cup` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) NOT NULL,
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) DEFAULT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `chargeper` int(11) DEFAULT '500',
  `ipcode` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `fixamount` decimal(9,2) DEFAULT '0.00',
  `taxper` int(11) DEFAULT '1224',
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) DEFAULT NULL,
  `proofrequired` enum('Y','N') DEFAULT 'N',
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(4) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `transType` int(2) DEFAULT NULL,
  `orderTime` varchar(16) DEFAULT NULL,
  `origQid` varchar(24) DEFAULT NULL,
  PRIMARY KEY (`trackingid`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_cup_details` */

DROP TABLE IF EXISTS `transaction_cup_details`;

CREATE TABLE `transaction_cup_details` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` int(11) NOT NULL,
  `action` varchar(25) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(12,0) DEFAULT NULL,
  `transType` int(2) DEFAULT NULL,
  `qid` varchar(24) DEFAULT NULL,
  `respCode` int(2) DEFAULT NULL,
  `respMsg` varchar(255) DEFAULT NULL,
  `respTime` varchar(16) DEFAULT NULL,
  `traceNumber` varchar(7) DEFAULT NULL,
  `traceTime` varchar(11) DEFAULT NULL,
  `settleAmount` decimal(12,0) DEFAULT NULL,
  `settleCurrency` int(4) DEFAULT NULL,
  `settleDate` varchar(5) DEFAULT NULL,
  `exchangeRate` varchar(9) DEFAULT NULL,
  `exchangeDate` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`trackingid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `transaction_ecore` */

DROP TABLE IF EXISTS `transaction_ecore`;

CREATE TABLE `transaction_ecore` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) NOT NULL,
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) DEFAULT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `firstname` varchar(255) NOT NULL DEFAULT '',
  `lastname` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `ccnum` varchar(255) NOT NULL DEFAULT '',
  `expdate` varchar(255) NOT NULL DEFAULT '',
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `refundinfo` varchar(255) DEFAULT NULL,
  `chargebackinfo` varchar(255) DEFAULT NULL,
  `chargeper` int(11) DEFAULT '500',
  `capturedata` varchar(255) DEFAULT NULL,
  `ipcode` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `ssn` varchar(100) DEFAULT NULL,
  `birthdate` varchar(100) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `captureamount` decimal(9,2) DEFAULT '0.00',
  `refundamount` decimal(9,2) DEFAULT '0.00',
  `refundcode` bigint(20) DEFAULT NULL,
  `namecp` enum('Y','N') DEFAULT 'N',
  `cccp` enum('Y','N') DEFAULT 'N',
  `addrcp` enum('Y','N') DEFAULT 'N',
  `hrcode` varchar(30) DEFAULT NULL,
  `fixamount` decimal(9,2) DEFAULT '0.00',
  `cbreason` varchar(255) DEFAULT NULL,
  `chargebackamount` decimal(9,2) DEFAULT '0.00',
  `taxper` int(11) DEFAULT '1224',
  `cardtype` varchar(10) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) DEFAULT NULL,
  `proofrequired` enum('Y','N') DEFAULT 'N',
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(4) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `ecorePaymentOrderNumber` varchar(21) DEFAULT NULL,
  `language` varchar(4) DEFAULT NULL,
  `ecoreTransactionDateTime` varchar(15) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `transid` int(11) NOT NULL DEFAULT '0',
  `terminalid` int(10) DEFAULT NULL,
  PRIMARY KEY (`trackingid`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=42392 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_ecore_details` */

DROP TABLE IF EXISTS `transaction_ecore_details`;

CREATE TABLE `transaction_ecore_details` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` int(11) NOT NULL,
  `action` varchar(25) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(9,2) DEFAULT NULL,
  `operationCode` varchar(3) DEFAULT NULL,
  `responseResultCode` varchar(5) DEFAULT NULL,
  `responseDateTime` varchar(15) DEFAULT NULL,
  `ecorePaymentOrderNumber` varchar(21) DEFAULT NULL,
  `responseRemark` varchar(255) DEFAULT NULL,
  `responseMD5Info` varchar(35) DEFAULT NULL,
  `responseBillingDescription` varchar(255) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `actionexecutorid` int(10) DEFAULT NULL,
  `actionexecutorname` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`trackingid`)
) ENGINE=InnoDB AUTO_INCREMENT=17091 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_emax_details` */

DROP TABLE IF EXISTS `transaction_emax_details`;

CREATE TABLE `transaction_emax_details` (
  `detailid` int(11) NOT NULL,
  `trackingid` int(11) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `stamp` varchar(255) DEFAULT NULL,
  `address_details` varchar(255) DEFAULT NULL,
  `card_holder` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_fail_log` */

DROP TABLE IF EXISTS `transaction_fail_log`;

CREATE TABLE `transaction_fail_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `toid` varchar(255) DEFAULT NULL,
  `totype` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `terminalid` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `cardnumber` varchar(255) DEFAULT NULL,
  `expirydate` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL,
  `country` varchar(10) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `httpheader` varchar(255) DEFAULT NULL,
  `requestedip` varchar(255) DEFAULT NULL,
  `rejectreason` varchar(255) DEFAULT NULL,
  `requestedhost` varchar(255) DEFAULT NULL,
  `dtstamp` int(10) DEFAULT NULL,
  `firstsix` varchar(10) DEFAULT NULL,
  `lastfour` varchar(10) DEFAULT NULL,
  `cardholderip` varchar(255) DEFAULT NULL,
  `splitpayment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13220 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_icicicredit` */

DROP TABLE IF EXISTS `transaction_icicicredit`;

CREATE TABLE `transaction_icicicredit` (
  `icicitransid` int(11) NOT NULL AUTO_INCREMENT,
  `transid` int(11) NOT NULL DEFAULT '0',
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `clientemail` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) NOT NULL DEFAULT '',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(255) NOT NULL DEFAULT '',
  `ccnum` varchar(255) NOT NULL DEFAULT '',
  `expdate` varchar(255) NOT NULL DEFAULT '',
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `authid` bigint(20) DEFAULT NULL,
  `authacquirerresponsecode` varchar(255) DEFAULT NULL,
  `authqsiresponsecode` varchar(255) DEFAULT NULL,
  `authqsiresponsedesc` varchar(255) DEFAULT NULL,
  `captureid` bigint(20) DEFAULT NULL,
  `captureresult` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `capturedata` varchar(255) DEFAULT NULL,
  `refundinfo` varchar(255) DEFAULT NULL,
  `chargebackinfo` varchar(255) DEFAULT NULL,
  `chargeper` int(11) DEFAULT '500',
  `authcode` varchar(255) DEFAULT NULL,
  `authbatchno` varchar(255) DEFAULT NULL,
  `authreceiptno` varchar(255) DEFAULT NULL,
  `authdr` varchar(255) DEFAULT NULL,
  `authdrid` varchar(255) DEFAULT NULL,
  `authavsresult` varchar(255) DEFAULT NULL,
  `authcscresult` varchar(255) DEFAULT NULL,
  `capturecode` varchar(255) DEFAULT NULL,
  `capturebatchno` varchar(255) NOT NULL DEFAULT '',
  `capturereceiptno` varchar(255) DEFAULT NULL,
  `capturedr` varchar(255) DEFAULT NULL,
  `capturedrid` varchar(255) DEFAULT NULL,
  `captureavsresult` varchar(255) DEFAULT NULL,
  `capturecscresult` varchar(255) DEFAULT NULL,
  `captureacquirerresponsecode` varchar(255) DEFAULT NULL,
  `captureqsiresponsecode` varchar(255) DEFAULT NULL,
  `captureqsiresponsedesc` varchar(255) DEFAULT NULL,
  `cbrefnumber` varchar(255) DEFAULT NULL,
  `ipcode` bigint(20) DEFAULT NULL,
  `mid` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `captureamount` decimal(9,2) DEFAULT '0.00',
  `refundamount` decimal(9,2) DEFAULT '0.00',
  `icicimerchantid` varchar(64) DEFAULT NULL,
  `refundid` bigint(20) DEFAULT NULL,
  `vbvstatus` char(1) DEFAULT NULL,
  `cavv` varchar(28) DEFAULT NULL,
  `eci` char(2) DEFAULT NULL,
  `xid` varchar(28) DEFAULT NULL,
  `namecp` enum('Y','N') DEFAULT 'N',
  `cccp` enum('Y','N') DEFAULT 'N',
  `addrcp` enum('Y','N') DEFAULT 'N',
  `hrcode` varchar(30) DEFAULT NULL,
  `fixamount` decimal(9,2) DEFAULT '0.00',
  `refundcode` varchar(255) DEFAULT NULL,
  `refundreceiptno` varchar(255) DEFAULT NULL,
  `refundqsiresponsecode` varchar(255) DEFAULT NULL,
  `refundqsiresponsedesc` varchar(255) DEFAULT NULL,
  `cbreason` varchar(255) DEFAULT NULL,
  `chargebackamount` decimal(9,2) DEFAULT '0.00',
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(255) DEFAULT NULL,
  `taxper` int(11) DEFAULT '1224',
  `cardtype` varchar(10) DEFAULT NULL,
  `accountid` int(2) DEFAULT NULL,
  `paymentid` varchar(28) DEFAULT NULL,
  `avr` char(1) DEFAULT NULL,
  `cvv2response` varchar(28) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`icicitransid`),
  UNIQUE KEY `cbrefnumber` (`cbrefnumber`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_mid` (`mid`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_transid` (`transid`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`),
  KEY `idx_transaction_icicicredit_refid` (`refundid`),
  KEY `idx_transaction_icicicredit_capid` (`captureid`),
  KEY `idx_authcode` (`authcode`),
  KEY `idx_transaction_icicicredit_ccnum` (`ccnum`)
) ENGINE=InnoDB AUTO_INCREMENT=13500 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_inicici_details` */

DROP TABLE IF EXISTS `transaction_inicici_details`;

CREATE TABLE `transaction_inicici_details` (
  `detailid` int(11) NOT NULL,
  `trackingid` int(11) NOT NULL,
  `status` varchar(25) DEFAULT NULL,
  `RTSRNumber` varchar(100) DEFAULT NULL,
  `rrnumber` varchar(100) DEFAULT NULL,
  `authcode` varchar(100) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `transaction_ipaydna_details` */

DROP TABLE IF EXISTS `transaction_ipaydna_details`;

CREATE TABLE `transaction_ipaydna_details` (
  `detailid` int(11) NOT NULL AUTO_INCREMENT,
  `INVOICENO` varchar(255) DEFAULT NULL COMMENT 'notnull',
  `AUTHORIZATIONCODE` varchar(25) DEFAULT NULL,
  `BATCHID` varchar(255) DEFAULT NULL,
  `AVSRESPONSE` varchar(25) DEFAULT NULL,
  `REFERRALORDERREFERENCE` varchar(255) DEFAULT NULL,
  `SETTLEMENTDATE` varchar(25) DEFAULT NULL,
  `SETTLEMENTSTATUSTEXT` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_lock` */

DROP TABLE IF EXISTS `transaction_lock`;

CREATE TABLE `transaction_lock` (
  `fromid` int(11) DEFAULT NULL,
  `dtstamp` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_mymonedero_details` */

DROP TABLE IF EXISTS `transaction_mymonedero_details`;

CREATE TABLE `transaction_mymonedero_details` (
  `detailid` int(11) NOT NULL,
  `wcredirecturl` varchar(255) DEFAULT NULL,
  `transdate` varchar(50) DEFAULT NULL,
  `sourceid` varchar(25) DEFAULT NULL,
  `destid` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_p4sepa_details` */

DROP TABLE IF EXISTS `transaction_p4sepa_details`;

CREATE TABLE `transaction_p4sepa_details` (
  `id` int(30) NOT NULL AUTO_INCREMENT,
  `trackingId` int(30) NOT NULL,
  `detailId` int(30) NOT NULL,
  `IBAN` varchar(50) DEFAULT NULL,
  `BIC` varchar(50) DEFAULT NULL,
  `MandateId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=249 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_paydollar_details` */

DROP TABLE IF EXISTS `transaction_paydollar_details`;

CREATE TABLE `transaction_paydollar_details` (
  `detailid` int(11) NOT NULL DEFAULT '0',
  `prc` varchar(10) DEFAULT NULL,
  `src` varchar(10) DEFAULT NULL,
  `ord` int(40) DEFAULT NULL,
  `holder` varchar(50) DEFAULT NULL,
  `successcode` int(10) DEFAULT NULL,
  `payref` int(40) DEFAULT NULL,
  `currcode` int(5) DEFAULT NULL,
  `authid` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_paymitco_details` */

DROP TABLE IF EXISTS `transaction_paymitco_details`;

CREATE TABLE `transaction_paymitco_details` (
  `detailid` int(11) NOT NULL DEFAULT '0',
  `trackingid` int(11) DEFAULT NULL,
  `amount` decimal(9,2) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `paymentType` varchar(5) DEFAULT NULL,
  `transactionType` varchar(5) DEFAULT NULL,
  `accountType` varchar(3) DEFAULT NULL,
  `accountNumber` varchar(50) DEFAULT NULL,
  `routingNumber` varchar(15) DEFAULT NULL,
  `customerId` varchar(50) DEFAULT NULL,
  `checknumber` varchar(50) DEFAULT NULL,
  `transactionID` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_payon_details` */

DROP TABLE IF EXISTS `transaction_payon_details`;

CREATE TABLE `transaction_payon_details` (
  `detailid` int(20) NOT NULL AUTO_INCREMENT,
  `trackingid` int(20) DEFAULT NULL,
  `amount` decimal(9,2) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `shortid` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `requesttimestamp` varchar(255) DEFAULT NULL,
  `responsetimestamp` varchar(255) DEFAULT NULL,
  `paypipeprocessingtime` varchar(255) DEFAULT NULL,
  `connectortime` varchar(20) DEFAULT NULL,
  `returncode` varchar(255) DEFAULT NULL,
  `returnmessage` varchar(255) DEFAULT NULL,
  `connectortxid1` varchar(255) DEFAULT NULL,
  `connectortxid2` varchar(255) DEFAULT NULL,
  `connectortxid3` varchar(255) DEFAULT NULL,
  `connectorcode` varchar(255) DEFAULT NULL,
  `connectorumessage` varchar(255) DEFAULT NULL,
  KEY `detailid` (`detailid`)
) ENGINE=InnoDB AUTO_INCREMENT=10139408 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_payvision_details` */

DROP TABLE IF EXISTS `transaction_payvision_details`;

CREATE TABLE `transaction_payvision_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `detailid` int(11) DEFAULT NULL,
  `trackingid` int(11) DEFAULT NULL,
  `amount` decimal(9,2) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `trackingmembercode` varchar(255) DEFAULT NULL,
  `transactionguid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_payvoucher` */

DROP TABLE IF EXISTS `transaction_payvoucher`;

CREATE TABLE `transaction_payvoucher` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) NOT NULL,
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) DEFAULT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `firstname` varchar(255) NOT NULL DEFAULT '',
  `lastname` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `refundinfo` varchar(255) DEFAULT NULL,
  `chargebackinfo` varchar(255) DEFAULT NULL,
  `chargeper` int(11) DEFAULT '500',
  `capturedata` varchar(255) DEFAULT NULL,
  `ipcode` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `captureamount` decimal(9,2) DEFAULT '0.00',
  `refundamount` decimal(9,2) DEFAULT '0.00',
  `refundcode` bigint(20) DEFAULT NULL,
  `namecp` enum('Y','N') DEFAULT 'N',
  `cccp` enum('Y','N') DEFAULT 'N',
  `addrcp` enum('Y','N') DEFAULT 'N',
  `hrcode` varchar(30) DEFAULT NULL,
  `fixamount` decimal(9,2) DEFAULT '0.00',
  `cbreason` varchar(255) DEFAULT NULL,
  `chargebackamount` decimal(9,2) DEFAULT '0.00',
  `taxper` int(11) DEFAULT '1224',
  `cardtype` varchar(10) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) DEFAULT NULL,
  `proofrequired` enum('Y','N') DEFAULT 'N',
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(4) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `voucherid` varchar(255) DEFAULT NULL,
  `transid` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`trackingid`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=13531 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_payvoucher_details` */

DROP TABLE IF EXISTS `transaction_payvoucher_details`;

CREATE TABLE `transaction_payvoucher_details` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` int(11) NOT NULL,
  `action` varchar(25) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(9,2) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `shortid` varchar(255) DEFAULT NULL,
  `uniqueid` varchar(255) DEFAULT NULL,
  `paymentcode` varchar(255) DEFAULT NULL,
  `returncurrency` varchar(4) DEFAULT NULL,
  `processingcode` varchar(255) DEFAULT NULL,
  `timestampreturned` varchar(255) DEFAULT NULL,
  `result` varchar(20) DEFAULT NULL,
  `statuscode` varchar(5) DEFAULT NULL,
  `respstatus` varchar(20) DEFAULT NULL,
  `reasoncode` varchar(20) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `returncode` varchar(255) DEFAULT NULL,
  `returnmessage` varchar(255) DEFAULT NULL,
  `returnamount` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`trackingid`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;

/*Table structure for table `transaction_procesosmc_details` */

DROP TABLE IF EXISTS `transaction_procesosmc_details`;

CREATE TABLE `transaction_procesosmc_details` (
  `detailid` int(11) NOT NULL,
  `trackingid` int(11) NOT NULL,
  `status` varchar(25) DEFAULT NULL,
  `authcode` varchar(255) DEFAULT NULL,
  `referencePMP` varchar(255) DEFAULT NULL,
  `number_of_fees` int(10) DEFAULT NULL,
  `first_fee_date` varchar(25) DEFAULT NULL,
  `fee_currency` varchar(4) DEFAULT NULL,
  `fee_amount` decimal(9,2) DEFAULT '0.00',
  `result_code` varchar(25) DEFAULT NULL,
  `result_description` varchar(255) DEFAULT NULL,
  `tx_acq_id` varchar(50) DEFAULT NULL,
  `card_countrycode` varchar(10) DEFAULT NULL,
  `banktrandate` varchar(25) DEFAULT NULL,
  `banktranstime` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_qwipi` */

DROP TABLE IF EXISTS `transaction_qwipi`;

CREATE TABLE `transaction_qwipi` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `toid` varchar(255) NOT NULL DEFAULT '',
  `totype` varchar(255) NOT NULL DEFAULT '',
  `fromid` varchar(255) NOT NULL DEFAULT '',
  `fromtype` varchar(255) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(3) NOT NULL,
  `redirecturl` varchar(255) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT 'begun',
  `podbatch` varchar(255) DEFAULT NULL,
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `firstname` varchar(255) NOT NULL DEFAULT '',
  `lastname` varchar(255) NOT NULL DEFAULT '',
  `name` varchar(255) NOT NULL DEFAULT '',
  `ccnum` varchar(255) NOT NULL DEFAULT '',
  `expdate` varchar(255) NOT NULL DEFAULT '',
  `emailaddr` varchar(255) NOT NULL DEFAULT '',
  `pod` varchar(255) DEFAULT NULL,
  `httpheader` text,
  `refundinfo` varchar(255) DEFAULT NULL,
  `chargebackinfo` varchar(255) DEFAULT NULL,
  `chargeper` int(11) DEFAULT '500',
  `capturedata` varchar(255) DEFAULT NULL,
  `ipcode` bigint(20) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `boiledname` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `ssn` varchar(100) DEFAULT NULL,
  `birthdate` varchar(100) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  `orderdescription` varchar(255) DEFAULT NULL,
  `telnocc` varchar(20) DEFAULT NULL,
  `telno` varchar(20) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `captureamount` decimal(9,2) DEFAULT '0.00',
  `refundamount` decimal(9,2) DEFAULT '0.00',
  `refundcode` bigint(20) DEFAULT NULL,
  `namecp` enum('Y','N') DEFAULT 'N',
  `cccp` enum('Y','N') DEFAULT 'N',
  `addrcp` enum('Y','N') DEFAULT 'N',
  `hrcode` varchar(30) DEFAULT NULL,
  `fixamount` decimal(9,2) DEFAULT '0.00',
  `cbreason` varchar(255) DEFAULT NULL,
  `chargebackamount` decimal(9,2) DEFAULT '0.00',
  `taxper` int(11) DEFAULT '1224',
  `cardtype` varchar(10) DEFAULT NULL,
  `accountid` int(11) DEFAULT NULL,
  `mid` varchar(255) DEFAULT NULL,
  `proofrequired` enum('Y','N') DEFAULT 'N',
  `templateamount` decimal(9,2) DEFAULT '0.00',
  `templatecurrency` varchar(4) DEFAULT NULL,
  `paymodeid` int(4) DEFAULT NULL,
  `cardtypeid` int(4) DEFAULT NULL,
  `qwipiPaymentOrderNumber` varchar(21) DEFAULT NULL,
  `language` varchar(4) DEFAULT NULL,
  `qwipiTransactionDateTime` varchar(15) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `transid` int(11) NOT NULL DEFAULT '0',
  `terminalid` int(10) DEFAULT NULL,
  PRIMARY KEY (`trackingid`),
  KEY `description` (`description`),
  KEY `toid` (`toid`),
  KEY `idx_ip_ipcode` (`ipcode`),
  KEY `idx_ip_emailaddr` (`emailaddr`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_dtstamp` (`dtstamp`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=42388 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_qwipi_details` */

DROP TABLE IF EXISTS `transaction_qwipi_details`;

CREATE TABLE `transaction_qwipi_details` (
  `trackingid` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` int(11) NOT NULL,
  `action` varchar(25) DEFAULT NULL,
  `status` varchar(25) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `amount` decimal(9,2) DEFAULT NULL,
  `operationCode` varchar(3) DEFAULT NULL,
  `responseResultCode` varchar(5) DEFAULT NULL,
  `responseDateTime` varchar(15) DEFAULT NULL,
  `qwipiPaymentOrderNumber` varchar(21) DEFAULT NULL,
  `responseRemark` varchar(255) DEFAULT NULL,
  `responseMD5Info` varchar(35) DEFAULT NULL,
  `responseBillingDescription` varchar(35) DEFAULT NULL,
  `ipaddress` varchar(255) DEFAULT NULL,
  `actionexecutorid` int(10) DEFAULT NULL,
  `actionexecutorname` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`trackingid`)
) ENGINE=InnoDB AUTO_INCREMENT=196695 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_safecharge_details` */

DROP TABLE IF EXISTS `transaction_safecharge_details`;

CREATE TABLE `transaction_safecharge_details` (
  `detailid` int(11) NOT NULL AUTO_INCREMENT,
  `AuthCode` varchar(25) DEFAULT NULL,
  `AVSCode` varchar(255) DEFAULT NULL,
  `ExErrCode` varchar(25) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB AUTO_INCREMENT=10134789 DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_sbm_details` */

DROP TABLE IF EXISTS `transaction_sbm_details`;

CREATE TABLE `transaction_sbm_details` (
  `detailid` int(11) NOT NULL DEFAULT '0',
  `trackingid` int(11) NOT NULL,
  `eci` int(11) NOT NULL DEFAULT '0',
  `isCvvValidated` enum('Y','N') NOT NULL DEFAULT 'N',
  `depositAmount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `paymentAuthCode` varchar(255) DEFAULT NULL,
  `processingAuthCode` varchar(255) DEFAULT NULL,
  `reffernceNumber` varchar(255) DEFAULT NULL,
  `acsURL` varchar(255) DEFAULT NULL,
  `acsRequest` varchar(255) DEFAULT NULL,
  `postdate` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_sofort_details` */

DROP TABLE IF EXISTS `transaction_sofort_details`;

CREATE TABLE `transaction_sofort_details` (
  `detailid` int(10) NOT NULL,
  `trackingid` int(10) DEFAULT NULL,
  `paymentURL` varchar(255) DEFAULT NULL,
  `paymentMethod` varchar(50) DEFAULT NULL,
  `sender_holder` varchar(255) DEFAULT NULL,
  `sender_accountNumber` varchar(255) DEFAULT NULL,
  `sender_bankCode` varchar(255) DEFAULT NULL,
  `sender_bankName` varchar(255) DEFAULT NULL,
  `sender_countryCode` varchar(255) DEFAULT NULL,
  `sender_bic` varchar(255) DEFAULT NULL,
  `sender_iban` varchar(255) DEFAULT NULL,
  `recipient_holder` varchar(255) DEFAULT NULL,
  `recipient_accountNumber` varchar(255) DEFAULT NULL,
  `recipient_bankCode` varchar(255) DEFAULT NULL,
  `recipient_bankName` varchar(255) DEFAULT NULL,
  `recipient_countryCode` varchar(255) DEFAULT NULL,
  `recipient_bic` varchar(255) DEFAULT NULL,
  `recipient_iban` varchar(255) DEFAULT NULL,
  `amountRefunded` varchar(255) DEFAULT NULL,
  `languageCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_swiffpay_details` */

DROP TABLE IF EXISTS `transaction_swiffpay_details`;

CREATE TABLE `transaction_swiffpay_details` (
  `detailid` int(11) NOT NULL,
  `historyid` varchar(255) DEFAULT 'null',
  `referencenumber` varchar(255) DEFAULT 'null',
  `merchantordernumber` varchar(255) DEFAULT 'null',
  `batchnumber` varchar(255) DEFAULT 'null',
  `AVSresultcode` varchar(255) DEFAULT 'null',
  `partialauth` varchar(255) DEFAULT 'null',
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `transaction_ugspay_details` */

DROP TABLE IF EXISTS `transaction_ugspay_details`;

CREATE TABLE `transaction_ugspay_details` (
  `detailid` int(11) NOT NULL,
  `FSResult` varchar(255) DEFAULT NULL,
  `FSStatus` varchar(25) DEFAULT NULL,
  `ACSUrl` varchar(255) DEFAULT NULL,
  `ACSRequestMessage` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `transaction_visanet_details` */

DROP TABLE IF EXISTS `transaction_visanet_details`;

CREATE TABLE `transaction_visanet_details` (
  `detailid` int(11) NOT NULL,
  `trackingid` int(11) NOT NULL,
  `status` varchar(25) DEFAULT NULL,
  `auth_code` varchar(255) DEFAULT NULL,
  `result_code` varchar(10) DEFAULT NULL,
  `result_description` varchar(200) DEFAULT NULL,
  `card_source` varchar(50) DEFAULT NULL,
  `cardissure_name` varbinary(200) DEFAULT NULL,
  `eci` int(20) DEFAULT NULL,
  `eci_description` varchar(200) DEFAULT NULL,
  `cvv_result` varchar(50) DEFAULT NULL,
  `tx_acq_id` varchar(50) DEFAULT NULL,
  `banktrans_date` varchar(25) DEFAULT NULL,
  `validation_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`detailid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `transaction_wsec_details` */

DROP TABLE IF EXISTS `transaction_wsec_details`;

CREATE TABLE `transaction_wsec_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `detailid` int(11) DEFAULT NULL,
  `trackingid` int(11) DEFAULT NULL,
  `amount` decimal(9,2) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `ipcountry` varchar(255) DEFAULT NULL,
  `cardcountry` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Table structure for table `transactions` */

DROP TABLE IF EXISTS `transactions`;

CREATE TABLE `transactions` (
  `transid` int(11) NOT NULL AUTO_INCREMENT,
  `toid` varchar(50) NOT NULL DEFAULT '',
  `totype` varchar(50) NOT NULL DEFAULT '',
  `fromid` varchar(50) NOT NULL DEFAULT '',
  `fromtype` varchar(50) NOT NULL DEFAULT '0',
  `amount` decimal(9,2) NOT NULL DEFAULT '0.00',
  `description` varchar(255) NOT NULL DEFAULT '',
  `dtstamp` int(11) NOT NULL DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`transid`),
  KEY `toid` (`toid`),
  KEY `fromid` (`fromid`),
  KEY `dtstamp` (`dtstamp`),
  KEY `transid` (`transid`),
  KEY `toid_2` (`toid`),
  KEY `description` (`description`),
  KEY `fromid_2` (`fromid`)
) ENGINE=InnoDB AUTO_INCREMENT=201160 DEFAULT CHARSET=latin1 PACK_KEYS=1;

/*Table structure for table `uploadfile_label` */

DROP TABLE IF EXISTS `uploadfile_label`;

CREATE TABLE `uploadfile_label` (
  `label_id` int(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(255) DEFAULT NULL,
  `alternate_name` varchar(100) DEFAULT NULL,
  `supportedfile_type` varchar(255) DEFAULT NULL,
  `functional_usage` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `accountid` bigint(20) NOT NULL,
  `login` varchar(50) NOT NULL DEFAULT '',
  `hashedpasswd` varchar(255) NOT NULL DEFAULT '',
  `roles` varchar(255) NOT NULL DEFAULT '',
  `unblocked` varchar(255) NOT NULL DEFAULT 'unlocked',
  `enabled` varchar(255) NOT NULL DEFAULT 'enabled',
  `csrfToken` varchar(255) NOT NULL DEFAULT '',
  `oldpasswdhashes` varchar(1500) NOT NULL DEFAULT '',
  `lasthostadd` varchar(255) DEFAULT NULL,
  `lastpasschgtamp` bigint(20) NOT NULL DEFAULT '0',
  `lastlogintamp` bigint(20) NOT NULL DEFAULT '0',
  `lastfaillogintamp` bigint(20) NOT NULL DEFAULT '0',
  `expirationtamp` bigint(20) NOT NULL DEFAULT '0',
  `faillogincount` int(11) NOT NULL DEFAULT '0',
  `timestmp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`accountid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_profile` */

DROP TABLE IF EXISTS `user_profile`;

CREATE TABLE `user_profile` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `memberid` int(20) DEFAULT NULL,
  `risk_profileid` int(20) NOT NULL,
  `business_profileid` int(20) NOT NULL,
  `partnerid` int(20) NOT NULL,
  `currency` varchar(45) DEFAULT NULL,
  `default_mode` varchar(255) DEFAULT NULL,
  `offline_transactionurl` varchar(300) DEFAULT NULL,
  `online_transactionurl` varchar(300) DEFAULT NULL,
  `online_threshold` varchar(45) DEFAULT NULL,
  `offline_threshold` varchar(45) DEFAULT NULL,
  `background` varchar(45) DEFAULT NULL,
  `foreground` varchar(45) DEFAULT NULL,
  `fontcolor` varchar(45) DEFAULT NULL,
  `logo` varchar(45) DEFAULT NULL,
  `addressVerification` enum('Y','N') DEFAULT 'N',
  `addressDetailDisplay` enum('Y','N') DEFAULT 'N',
  `autoRedirect` enum('Y','N') DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `fk_user_profile_1_idx` (`partnerid`),
  KEY `fk_user_profile_2_idx` (`business_profileid`),
  KEY `fk_user_profile_3_idx` (`risk_profileid`),
  CONSTRAINT `fk_user_profile_1` FOREIGN KEY (`partnerid`) REFERENCES `partners` (`partnerId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_profile_2` FOREIGN KEY (`business_profileid`) REFERENCES `business_profile` (`profileid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_profile_3` FOREIGN KEY (`risk_profileid`) REFERENCES `risk_profile` (`profileid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=latin1;

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `md5_id` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `full_name` tinytext COLLATE latin1_general_ci NOT NULL,
  `user_name` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `user_email` varchar(220) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `user_level` tinyint(4) NOT NULL DEFAULT '1',
  `pwd` varchar(220) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `address` text COLLATE latin1_general_ci NOT NULL,
  `country` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `tel` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `fax` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `website` text COLLATE latin1_general_ci NOT NULL,
  `date` date NOT NULL DEFAULT '0000-00-00',
  `users_ip` varchar(200) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `approved` int(1) NOT NULL DEFAULT '0',
  `activation_code` int(10) NOT NULL DEFAULT '0',
  `banned` int(1) NOT NULL DEFAULT '0',
  `ckey` varchar(220) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  `ctime` varchar(220) COLLATE latin1_general_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_email` (`user_email`),
  FULLTEXT KEY `idx_search` (`full_name`,`address`,`user_email`,`user_name`)
) ENGINE=MyISAM AUTO_INCREMENT=66 DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

/*Table structure for table `whitelist_details` */

DROP TABLE IF EXISTS `whitelist_details`;

CREATE TABLE `whitelist_details` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `firstsix` varchar(6) NOT NULL,
  `lastfour` varchar(4) NOT NULL,
  `accountid` int(11) DEFAULT NULL,
  `memberid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3468 DEFAULT CHARSET=latin1;

/*Table structure for table `wiremanager` */

DROP TABLE IF EXISTS `wiremanager`;

CREATE TABLE `wiremanager` (
  `settledid` int(10) NOT NULL AUTO_INCREMENT,
  `settledate` timestamp NULL DEFAULT NULL,
  `firstdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `lastdate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `amount` double(9,2) NOT NULL,
  `balanceamount` double(9,2) NOT NULL,
  `netfinalamount` double(9,2) NOT NULL,
  `unpaidamount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(5) NOT NULL,
  `status` varchar(25) NOT NULL,
  `settlementreportfilepath` varchar(255) NOT NULL,
  `settledtransactionfilepath` varchar(255) NOT NULL,
  `markedfordeletion` enum('Y','N') NOT NULL DEFAULT 'N',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `toid` int(10) NOT NULL,
  `terminalid` int(10) NOT NULL,
  `accountid` int(10) NOT NULL,
  `paymodeid` int(10) NOT NULL,
  `cardtypeid` int(10) NOT NULL,
  `isrollingreserveincluded` enum('Y','N') NOT NULL DEFAULT 'N',
  `rollingreservereleasedateupto` timestamp NULL DEFAULT NULL,
  `settlementcycle_no` int(10) DEFAULT NULL,
  `declinedcoverdateupto` timestamp NULL DEFAULT NULL,
  `reversedcoverdateupto` timestamp NULL DEFAULT NULL,
  `chargebackcoverdateupto` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`settledid`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=latin1;

/*Table structure for table `wl_invoice_manager` */

DROP TABLE IF EXISTS `wl_invoice_manager`;

CREATE TABLE `wl_invoice_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `partner_id` int(11) DEFAULT NULL,
  `pgtype_id` int(11) DEFAULT '0',
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `setteled_date` timestamp NULL DEFAULT NULL,
  `amount` double(9,2) DEFAULT '0.00',
  `status` varchar(255) DEFAULT NULL,
  `reportfile_path` varchar(255) DEFAULT NULL,
  `transactionfile_path` varchar(255) DEFAULT NULL,
  `creation_on` int(15) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'on update CURRENT_TIMESTAMP',
  `action_executer` varchar(255) DEFAULT NULL,
  `netfinal_amount` double(9,2) NOT NULL DEFAULT '0.00',
  `unpaid_amount` double(9,2) NOT NULL DEFAULT '0.00',
  `currency` char(3) DEFAULT 'USD',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `wl_partner_commission_mapping` */

DROP TABLE IF EXISTS `wl_partner_commission_mapping`;

CREATE TABLE `wl_partner_commission_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `partner_id` int(11) NOT NULL,
  `pgtype_id` int(11) DEFAULT NULL,
  `commission_id` int(11) NOT NULL,
  `commission_value` double(9,2) NOT NULL,
  `isinput_required` enum('Y','N') NOT NULL DEFAULT 'N',
  `sequence_no` int(10) NOT NULL,
  `start_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `end_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `creation_on` int(10) DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'on update CURRENT_TIMESTAMP',
  `isActive` enum('Y','N') NOT NULL DEFAULT 'Y',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=latin1;

/* Trigger structure for table `transaction_common` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `trg_transaction_common` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `trg_transaction_common` BEFORE INSERT ON `transaction_common` FOR EACH ROW BEGIN
declare new_id int;
UPDATE  master_trackingid set id=id+1;
select id into new_id from master_trackingid;
SET NEW.trackingid = new_id;
END */$$


DELIMITER ;

/* Trigger structure for table `transaction_ecore` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `trg_transaction_ecore` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `trg_transaction_ecore` BEFORE INSERT ON `transaction_ecore` FOR EACH ROW BEGIN
declare new_id int;
UPDATE  master_trackingid set id=id+1;
select id into new_id from master_trackingid;
SET NEW.trackingid = new_id;
END */$$


DELIMITER ;

/* Trigger structure for table `transaction_icicicredit` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `trg_transaction_icicicredit` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `trg_transaction_icicicredit` BEFORE INSERT ON `transaction_icicicredit` FOR EACH ROW BEGIN

declare new_id int;

UPDATE  master_trackingid set id=id+1;

select id into new_id from master_trackingid;

SET NEW.icicitransid = new_id;

END */$$


DELIMITER ;

/* Trigger structure for table `transaction_payvoucher` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `trg_transaction_payvoucher` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `trg_transaction_payvoucher` BEFORE INSERT ON `transaction_payvoucher` FOR EACH ROW BEGIN
declare new_id int;
UPDATE  master_trackingid set id=id+1;
select id into new_id from master_trackingid;
SET NEW.trackingid = new_id;
END */$$


DELIMITER ;

/* Trigger structure for table `transaction_qwipi` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `trg_transaction_qwipi` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `trg_transaction_qwipi` BEFORE INSERT ON `transaction_qwipi` FOR EACH ROW BEGIN
declare new_id int;
UPDATE  master_trackingid set id=id+1;
select id into new_id from master_trackingid;
SET NEW.trackingid = new_id;
END */$$


DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
