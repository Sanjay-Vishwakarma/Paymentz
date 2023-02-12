INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SUCCESSFUL_TRANSACTION', 'SUCCESSFUL_OTP_CREATION', '00015', 'OTP created successfully', '00015', 'OTP created successfully'); 
 INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('REJECTED_TRANSACTION', 'REJECTED_OTP_CREATION', '90014', 'OTP creation failed', '90014', 'OTP creation failed'); 
INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('REJECTED_TRANSACTION', 'REJECTED_MERCHANT_SIGNUP', '90015', 'Merchant signup failed', '90015', 'Merchant signup failed'); 


INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SYSCHECK', 'SYS_OTP_SENT', '10176', 'OTP sent', '10176', 'OTP sent'); 


INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SUCCESSFUL_TRANSACTION', 'SUCCESSFUL_MERCHANT_LOGIN', '00014', 'Successful merchant login', '00014', 'Successful merchant login'); 
INSERT INTO `paymentz`.`error_code` (`error_type`, `error_name`, `error_code`, `error_description`, `api_code`, `api_description`) VALUES ('SYSCHECK', 'SYS_LOGIN_FAILED', '10175', 'Merchant login failed', '20085', 'Merchant login failed'); 



INSERT INTO error_code (error_type, error_name, error_code, error_description, api_code, api_description) VALUES ('VALIDATION', 'VALIDATION_CONTACT_NAME', '10172', 'Invalid merchant contact name, contact name should not be empty and accepts only [A-Za-z0-9.]', '30086', 'Invalid merchant contact name, contact name should not be empty and accepts only [A-Za-z0-9.]');

