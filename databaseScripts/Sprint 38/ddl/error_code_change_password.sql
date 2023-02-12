INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SUCCESSFUL_TRANSACTION', 'SUCCESSFUL_MERCHANT_CHANGE_PASSWORD', '00016', 'Password changed successfully', '00016', 'Password changed successfully'); 

INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('REJECTED_TRANSACTION', 'REJECTED_MERCHANT_CHANGE_PASSWORD', '90017', 'Change password failed', '90017', 'Change password failed'); 

INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SYSCHECK', 'SYS_DUPLICATE_PASSWORD', '10177', 'Password must not be last 5 passwords', '10177', 'Password must not be last 5 passwords'); 

INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SYSCHECK', 'SYS_REGENERATE_INVOICE', '10177', 'Cannot regenerate invoice', '10177', 'Cannot regenerate invoice'); 

INSERT INTO `error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES('SUCCESSFUL_TRANSACTION','SUCCESSFUL_OTP_VERIFICATION','00017','Successful OTP verification','00017','Successful OTP verification');

INSERT INTO `error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES('REJECTED_TRANSACTION','OTP_VERIFICATION_FAILED','90018','OTP Verification Failed','90018','OTP Verification Failed');

INSERT INTO `error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES('VALIDATION','VALIDATION_COUNTRY','10182','Invalid Country','30092','Invalid Country');


ALTER TABLE signup_otp_varification ADD COLUMN `country` VARCHAR(100) NULL; 