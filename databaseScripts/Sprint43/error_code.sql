UPDATE `paymentz`.`error_code` SET `error_code` = '401' , `api_code` = '401' WHERE `error_name` = 'SYS_AUTHTOKEN_FAILED'; 

UPDATE `paymentz`.`error_code` SET `error_code` = '403' , `api_code` = '403' WHERE `error_name` = 'SYS_TOKEN_GENERATION_FAILED'; 

UPDATE `paymentz`.`error_code` SET `error_description` = 'Token generation forbidden' , `api_description` = 'Token generation forbidden' WHERE `error_name` = 'SYS_TOKEN_GENERATION_FAILED'; 


UPDATE `paymentz`.`error_code` SET `error_code` = '200' , `api_code` = '200' WHERE `error_name` = 'SUCCESSFUL_AUTH_TOKEN_GENERATED'; 
