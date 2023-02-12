package com.payment.cupUPI;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.cupUPI.unionpay.acp.sdk.AcpService;
import com.payment.cupUPI.unionpay.acp.sdk.CertUtil;
import com.payment.cupUPI.unionpay.acp.sdk.DemoBase;
import com.payment.cupUPI.unionpay.acp.sdk.SDKConfig;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 05-Jul-19.
 */
public class UnionPayInternationalPaymentGateway extends  AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "CupUpi";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.UnionPayInt");

    private static UnionPayInternationalLogger transactionLogger= new UnionPayInternationalLogger(UnionPayInternationalLogger.class.getName());
    private static UnionPayInternationalLogger log = new UnionPayInternationalLogger(UnionPayInternationalLogger.class.getName());

    public UnionPayInternationalPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale :::");
        String propertyPath = RB.getString("PROPERTY_PATH");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        Functions functions= new Functions();
       // CommResponseVO commResponseVO= new CommResponseVO();
        UnionPayInternationalResponseVO unionPayInternationalResponseVO=new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String displayName = "";
        String termUrl = "";
        String merId = gatewayAccount.getMerchantId(); //first 8 digits, IIN last 7 any
        String orderId = "000"+trackingId;
        String txnTime = String.valueOf(dateFormat.format(date));
        transactionLogger.debug("txnTime ---"+txnTime);
       // System.out.println("Txn time---" + txnTime);
        String txnAmt =unionPayInternationalUtils.getCentAmount(transDetailsVO.getAmount());
        String phoneNo = addressDetailsVO.getPhone();
        String accNo = cardDetailsVO.getCardNum();
       // String accNo = commRequestVO.getCardDetailsVO().getCardNum();
        String expired=cardDetailsVO.getExpYear().substring(2,4)+cardDetailsVO.getExpMonth();
        transactionLogger.error("Expiry ---"+expired);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        try
        {
            Map<String, String> contentData = new HashMap<String, String>();
            // Constant value: 5.1.0
            contentData.put("version", DemoBase.version);                  //M
            // Default value: UTF-8
            contentData.put("encoding", DemoBase.encoding);           //M
            // Value: 01 (RSA)
            contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
            // Value: 01
            contentData.put("txnType", "01");                              //M
            // 01: Purchase, to differentiate the front-end purchase or back-end purchase through transaction request URL
            // 02: MOTO
            //05: Purchase with authentication (Applied to Product type 000301)
            contentData.put("txnSubType", "01");                           //Transaction sub-type 01-purchase
            //000301: Merchant-hosted
            //000000: ExpressPay
            //000902: Token payment
            //001001: MOTO
            contentData.put("bizType", bizType);         //M
            // 0: Merchant direct access
            // 1: Acquirer Access
            // 2: Platform merchant access
            contentData.put("accessType", "1");// M
            // 07: Internet
            // 08: Mobile
            contentData.put("channelType", "07");// M
            // Acquirer Code (Applied if access type is acquirer access)
            contentData.put("acqInsCode", "38990826");//C // --- gateway username
            // Merchant Category (Applied if access type is acquirer access)
            contentData.put("merCatCode", "8999");// C
            // Merchant Name (Applied if access type is acquirer access)
            contentData.put("merName", "OneStopMoneyManager");// C
            // Merchant Abbreviation (Applied if access type is acquirer access)
            contentData.put("merAbbr", "OSMM");// C
            // Merchant ID
            contentData.put("merId", merId);                   			  //M
            // Merchant order No
            contentData.put("orderId", orderId);             			   //M client order number, consisting of 8-40 alphanumeric characters, no “-�? or “_�? is allowed, but custom rules are allowed
            // Date and time when merchant sends transaction
            contentData.put("txnTime", txnTime);         				   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
            // Default value is 156.
            contentData.put("currencyCode",currencyId);						   //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
            // The unit of transaction amount is cent.
            //contentData.put("txnAmt", txnAmt);							   //M transaction amount: in cents, without any decimal point.
            contentData.put("txnAmt", txnAmt);							   //M transaction amount: in cents, without any decimal point.


            Map<String,String> customerInfoMap = new HashMap<String,String>();
            //customerInfoMap.put("smsCode", "111111");			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
            transactionLogger.error("phone -----"+addressDetailsVO.getPhone());
            customerInfoMap.put("phoneNo", phoneNo);			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
            //transactionLogger.debug("cvv --------" + cardDetailsVO.getcVV());
            customerInfoMap.put("cvn2", cardDetailsVO.getcVV());           			        //The three digits corresponding to cvn2 at the back of the card
            customerInfoMap.put("expired",expired);  				        //Validity: The year should come before the month.

            SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);

            ////////////If the client has enabled the right [encrypt sensitive information by the client], you need to encrypt accNo, pin, phoneNo, cvn2, and expired (if these fields will be sent later) for encryption of sensitive information.
            String accNo1 = AcpService.encryptData(accNo, DemoBase.encoding);  //A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
            contentData.put("accNo", accNo1);
            contentData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
            String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,accNo,DemoBase.encoding);

            contentData.put("customerInfo", customerInfoStr);//M

            contentData.put("payTimeout", "");// O

            Map<String,String> reservedInfoMap = new HashMap<String,String>();
            reservedInfoMap.put("eci", "10");// O
// String reversedInfoString = AcpService.getReservedInfoWithEncrypt(reservedInfoMap,DemoBase.encoding);
            contentData.put("reserved",reservedInfoMap.toString());
//System.out.println("reversedInfoString---"+reversedInfoString);

           // System.out.println("Back URL---"+SDKConfig.getConfig().getBackUrl());
            contentData.put("backUrl", SDKConfig.getConfig().getBackUrl()+trackingId);
            transactionLogger.debug("Back URL---"+SDKConfig.getConfig().getBackUrl());

            /**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
            Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
            //transactionLogger.error("Purchase Request -------"+reqData);
            String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
            Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding); //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
            transactionLogger.error("Purchase Response -------"+rspData);


//            System.out.println("Purchase Request---"+reqData);
//            System.out.println("Purchase Response---"+rspData);
            /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
            //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
            StringBuffer parseStr = new StringBuffer("");
            if(!rspData.isEmpty())
            {
                if(AcpService.validate(rspData, DemoBase.encoding))
                {
                    transactionLogger.debug("Signature authentication succeeds");
                   // System.out.println("Signature authentication succeeds");
                    String respCode = rspData.get("respCode") ;
                    String queryId = rspData.get("queryId") ;
                    transactionLogger.error("respCode ---"+respCode);
                    transactionLogger.error("queryId ---"+queryId);
                   // System.out.println("Purchase response code---"+respCode);
                    if(("00").equals(respCode))
                    {
                        transactionLogger.error("Purchase response code"+respCode);
                        unionPayInternationalResponseVO.setStatus("success");

                        unionPayInternationalResponseVO.setDescription("PURCHASE SUCCESS");
                        unionPayInternationalResponseVO.setRemark("PURCHASE SUCCESS");
                        unionPayInternationalResponseVO.setTransactionId(queryId);
                        unionPayInternationalResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                        //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to update the order state after receiving the background notification, or actively initiate a transaction query to determine the transaction state.

                        //If you have configured encryption for sensitive information, you can use the method below to decrypt the card number to obtain the plain text card number.
//					String accNo1 = rspData.get("accNo");
//					String accNo2 = AcpService.decryptData(accNo1, "UTF-8");  //To decrypt a card number, you need to use the private key certificate acpsdk.signCert.path signed by the client.
//					LogUtil.writeLog("Decrypted card number:"+accNo2);
//					parseStr.append("Decrypted card number:"+accNo2);
                    }
                    else if(("03").equals(respCode)|| ("04").equals(respCode)||   ("05").equals(respCode))
                    {
                        transactionLogger.error("Purchase response code" + respCode);
                        unionPayInternationalResponseVO.setStatus("pending");
                        unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                        unionPayInternationalResponseVO.setRemark("PURCHASE PENDING / "+UnionPayInternationalErrorCode.getDescription(respCode));
                    }
                    else
                    {
                        transactionLogger.error("Purchase response code"+respCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                        unionPayInternationalResponseVO.setRemark("PURCHASE FAIL / "+UnionPayInternationalErrorCode.getDescription(respCode));

                    }
                    unionPayInternationalResponseVO.setAmount(transDetailsVO.getAmount());
                    unionPayInternationalResponseVO.setCurrency(transDetailsVO.getCurrency());
                    unionPayInternationalResponseVO.setTmpl_Amount(tmpl_amount);
                    unionPayInternationalResponseVO.setTmpl_Currency(tmpl_currency);
                    unionPayInternationalResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                }
                else
                {
                    transactionLogger.error("Signature authentication fails");
                   // System.out.println("Signature authentication fails");
                }
            }
            else
            {
                transactionLogger.error("Response data is empty");
                unionPayInternationalResponseVO.setStatus("fail");
                //The returned http state code is incorrect.
                transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
                //System.out.println("No returned message is obtained or the returned http state code is not 200.");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(UnionPayInternationalPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return unionPayInternationalResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processAuthentication :::");
        String propertyPath = RB.getString("PROPERTY_PATH");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        Functions functions= new Functions();
        // CommResponseVO commResponseVO= new CommResponseVO();
        UnionPayInternationalResponseVO unionPayInternationalResponseVO=new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String merchantId=gatewayAccount.getMerchantId();
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        String displayName = "";
        String termUrl = "";
        String merId = gatewayAccount.getMerchantId(); //first 8 digits, IIN last 7 any
        String orderId = "000"+trackingID;
        String txnTime = String.valueOf(dateFormat.format(date));
        transactionLogger.debug("txnTime ---"+txnTime);
        // System.out.println("Txn time---" + txnTime);
        String txnAmt =unionPayInternationalUtils.getCentAmount(transDetailsVO.getAmount());
        String phoneNo = addressDetailsVO.getPhone();
        String accNo = cardDetailsVO.getCardNum();
        // String accNo = commRequestVO.getCardDetailsVO().getCardNum();
        String expired=cardDetailsVO.getExpYear().substring(2,4)+cardDetailsVO.getExpMonth();
        transactionLogger.error("Expiry ---"+expired);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }


        try
        {
            Map<String, String> contentData = new HashMap<String, String>();
            // Constant value: 5.1.0
            contentData.put("version", DemoBase.version);                  //M
            // Default value: UTF-8
            contentData.put("encoding", DemoBase.encoding);           //M
            // Value: 01 (RSA)
            contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
            // Value: 01
            contentData.put("txnType", "02");                              //M
            // 01: Purchase, to differentiate the front-end purchase or back-end purchase through transaction request URL
            // 02: MOTO
            //05: Purchase with authentication (Applied to Product type 000301)
            contentData.put("txnSubType", "01");                           //Transaction sub-type 01-purchase
            //000301: Merchant-hosted
            //000000: ExpressPay
            //000902: Token payment
            //001001: MOTO
            contentData.put("bizType", bizType);         //M
            // 0: Merchant direct access
            // 1: Acquirer Access
            // 2: Platform merchant access
            contentData.put("accessType", "1");// M
            // 07: Internet
            // 08: Mobile
            contentData.put("channelType", "07");// M
            // Acquirer Code (Applied if access type is acquirer access)
            contentData.put("acqInsCode", "38990826");//C // --- gateway username
            // Merchant Category (Applied if access type is acquirer access)
            contentData.put("merCatCode", "8999");// C
            // Merchant Name (Applied if access type is acquirer access)
            contentData.put("merName", "OneStopMoneyManager");// C
            // Merchant Abbreviation (Applied if access type is acquirer access)
            contentData.put("merAbbr", "OSMM");// C
            // Merchant ID
            contentData.put("merId", merId);                   			  //M
            // Merchant order No
            contentData.put("orderId", orderId);             			   //M client order number, consisting of 8-40 alphanumeric characters, no “-�? or “_�? is allowed, but custom rules are allowed
            // Date and time when merchant sends transaction
            contentData.put("txnTime", txnTime);         				   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
            // Default value is 156.
            contentData.put("currencyCode",currencyId);						   //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
            // The unit of transaction amount is cent.
            //contentData.put("txnAmt", txnAmt);							   //M transaction amount: in cents, without any decimal point.
            contentData.put("txnAmt", txnAmt);							   //M transaction amount: in cents, without any decimal point.


            Map<String,String> customerInfoMap = new HashMap<String,String>();
            //customerInfoMap.put("smsCode", "111111");			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
            transactionLogger.error("phone -----"+addressDetailsVO.getPhone());
            customerInfoMap.put("phoneNo", phoneNo);			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
            //transactionLogger.error("cvv --------" + cardDetailsVO.getcVV());
            customerInfoMap.put("cvn2", cardDetailsVO.getcVV());           			        //The three digits corresponding to cvn2 at the back of the card
            customerInfoMap.put("expired",expired);  				        //Validity: The year should come before the month.

            SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);

            ////////////If the client has enabled the right [encrypt sensitive information by the client], you need to encrypt accNo, pin, phoneNo, cvn2, and expired (if these fields will be sent later) for encryption of sensitive information.
            String accNo1 = AcpService.encryptData(accNo, DemoBase.encoding);  //A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
            contentData.put("accNo", accNo1);
            contentData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
            String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,accNo,DemoBase.encoding);

            contentData.put("customerInfo", customerInfoStr);//M

            contentData.put("payTimeout", "");// O

            Map<String,String> reservedInfoMap = new HashMap<String,String>();
            reservedInfoMap.put("eci", "10");// O
// String reversedInfoString = AcpService.getReservedInfoWithEncrypt(reservedInfoMap,DemoBase.encoding);
            contentData.put("reserved",reservedInfoMap.toString());
//System.out.println("reversedInfoString---"+reversedInfoString);

            // System.out.println("Back URL---"+SDKConfig.getConfig().getBackUrl());
            contentData.put("backUrl", SDKConfig.getConfig().getBackUrl()+trackingID);
            transactionLogger.debug("Back URL---"+SDKConfig.getConfig().getBackUrl());

            /**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
            Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
           // transactionLogger.error("Purchase Request -------"+reqData);
            String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
            Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding); //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
            transactionLogger.error("Purchase Response -------"+rspData);


//            System.out.println("Purchase Request---"+reqData);
//            System.out.println("Purchase Response---"+rspData);
            /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
            //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
            StringBuffer parseStr = new StringBuffer("");
            if(!rspData.isEmpty())
            {
                if(AcpService.validate(rspData, DemoBase.encoding))
                {
                    transactionLogger.debug("Signature authentication succeeds");
                    // System.out.println("Signature authentication succeeds");
                    String respCode = rspData.get("respCode") ;
                    String queryId = rspData.get("queryId") ;
                    transactionLogger.error("respCode ---"+respCode);
                    transactionLogger.error("queryId ---"+queryId);
                    // System.out.println("Purchase response code---"+respCode);
                    if(("00").equals(respCode))
                    {
                        transactionLogger.error("Purchase response code"+respCode);
                        unionPayInternationalResponseVO.setStatus("success");

                        unionPayInternationalResponseVO.setDescription("PURCHASE SUCCESS");
                        unionPayInternationalResponseVO.setRemark("PURCHASE SUCCESS");
                        unionPayInternationalResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        unionPayInternationalResponseVO.setTransactionId(queryId);

                        //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to update the order state after receiving the background notification, or actively initiate a transaction query to determine the transaction state.

                        //If you have configured encryption for sensitive information, you can use the method below to decrypt the card number to obtain the plain text card number.
//					String accNo1 = rspData.get("accNo");
//					String accNo2 = AcpService.decryptData(accNo1, "UTF-8");  //To decrypt a card number, you need to use the private key certificate acpsdk.signCert.path signed by the client.
//					LogUtil.writeLog("Decrypted card number:"+accNo2);
//					parseStr.append("Decrypted card number:"+accNo2);
                    }
                    else if(("03").equals(respCode)|| ("04").equals(respCode)||   ("05").equals(respCode))
                    {
                        transactionLogger.error("Purchase response code" + respCode);
                        unionPayInternationalResponseVO.setStatus("pending");
                        unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                        unionPayInternationalResponseVO.setRemark("PURCHASE PENDING / "+UnionPayInternationalErrorCode.getDescription(respCode));
                    }
                    else
                    {
                        transactionLogger.error("Purchase response code"+respCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                        unionPayInternationalResponseVO.setRemark("PURCHASE FAIL / "+UnionPayInternationalErrorCode.getDescription(respCode));

                    }
                }
                else
                {
                    transactionLogger.error("Signature authentication fails");
                    // System.out.println("Signature authentication fails");
                }
            }
            else
            {
                transactionLogger.error("Response data is empty");
                unionPayInternationalResponseVO.setStatus("fail");
                //The returned http state code is incorrect.
                transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
                //System.out.println("No returned message is obtained or the returned http state code is not 200.");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(UnionPayInternationalPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return unionPayInternationalResponseVO;

    }

    public GenericResponseVO processSendSMS(String trackingid,CommRequestVO commRequestVO)
    {
        transactionLogger.error("Inside processSendSMS -------------");
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        UnionPayInternationalResponseVO unionPayInternationalResponseVO = new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        Functions functions=new Functions();

        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        String accountId=commRequestVO.getCommMerchantVO().getAccountId();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String displayName = "";
        String termUrl = "";
        String merId =gatewayAccount.getMerchantId(); //first 8 digits, IIN last 7 any
        String txnAmt =unionPayInternationalUtils.getCentAmount(transDetailsVO.getAmount());
        String orderId = "SMS"+trackingid;
        String txnTime = String.valueOf(dateFormat.format(date));
        //System.out.println("Txn time---"+txnTime);
        transactionLogger.debug("Txn time---" + txnTime);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);
        transactionLogger.error(""+txnAmt);
        String phoneNo = addressDetailsVO.getPhone();
       // String phoneCC = addressDetailsVO.getTelnocc();
       // String PhoneNoNew=phoneCC+"-"+phoneNo;
       // System.out.println("phone number ------"+phoneNo);
        transactionLogger.debug("phone number ------" + phoneNo);
        String accNo =cardDetailsVO.getCardNum();
        //transactionLogger.error("Crad Number In SMS --" + accNo);
        String expired=cardDetailsVO.getExpYear().substring(2,4)+cardDetailsVO.getExpMonth();
        //transactionLogger.error("Expiry ---"+expired);
        //transactionLogger.error("cvv ---"+cardDetailsVO.getcVV());
        String cvv=cardDetailsVO.getcVV();

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        Map<String, String> contentData = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        transactionLogger.error("contentData started ------------");
        contentData.put("version", DemoBase.version);                   //M
        contentData.put("encoding", DemoBase.encoding);            //M
        contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
        transactionLogger.error("content data 2");
        // Value: 77
        contentData.put("txnType", "77");// M
        //05: Easy enrollment
        //06: SecurePlus SMS-sending
        //07: Purchase
        contentData.put("txnSubType", "05");// M
        // Product type
        contentData.put("bizType", bizType);// M
        // Access type
        //0: Merchant direct access
        //1: Acquirer access
        //2: Platform type merchant access
        contentData.put("accessType", "1");// M
        // Channel type
        //07: Internet
        //08: Mobile
        contentData.put("channelType", "07");// M
        // Acquirer Code (Applied if access type is acquirer access)
        //
        contentData.put("merId", merId);//M client number (this client number is used only for transaction test and debugging and this client has configured to encrypt sensitive information). In the test, please modify this client number to the one you have applied [the test client number starting with 777 you have registered does not support product collecting]
        contentData.put("acqInsCode", "38990826");//C IIN
        // Merchant Category (Applied if access type is acquirer access)
        contentData.put("merCatCode", "8999");// C
        // Merchant Name (Applied if access type is acquirer access)
        contentData.put("merName", "OneStopMoneyManager");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        contentData.put("merAbbr", "OSMM");// C

        contentData.put("orderId", orderId);//M client order number, consisting of 8-40 alphanumeric characters, no “-�? or “_�? is allowed, but custom rules are allowed
        contentData.put("txnTime", txnTime);//M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        //contentData.put("currencyCode", "156");//M transaction currency (for domestic clients, it is usually 156, which indicates RMB. For other information, refer to the interface documents.)
        //contentData.put("txnAmt", txnAmt);//M transaction amount: in cents, without any decimal point.

        String propertyPath = RB.getString("PROPERTY_PATH");
        SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);
        //SDKConfig.getConfig().loadPropertiesFromSrc();

        String accNo1 = AcpService.encryptData(accNo, "UTF-8");  //M. A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
        //String accNo1 = accNo;  //M. A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
       // System.out.println("accNo1 -------"+accNo1);
        transactionLogger.debug("accNo1 -------" + accNo1);
        contentData.put("accNo", accNo1);

        //Send the phone number
        Map<String,String> customerInfoMap = new HashMap<String,String>();
        customerInfoMap.put("phoneNo",phoneNo);			        //Phone number
       // customerInfoMap.put("phoneNo","086-13012345678");			        //Phone number
        customerInfoMap.put("cvn2",cvv ); //The three digits corresponding to cvn2 at the back of the card

        customerInfoMap.put("expired",expired); //Validity: The year should come before the month.
        //customerInfoMap.put("smsCode", "111111");  				        //SMS authentication code: Always fill in 111111 in test environment.
       // System.out.println(customerInfoMap);
        transactionLogger.debug("customerInfoMap ----" + customerInfoMap);
        String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,null,DemoBase.encoding);
        //String customerInfoStr = AcpService.getCustomerInfo(customerInfoMap,null,DemoBase.encoding);
        contentData.put("customerInfo", customerInfoStr);	 //M
        //System.out.println("customerInfoStr ---"+customerInfoStr);
        transactionLogger.debug("customerInfoStr ---" + customerInfoStr);
        contentData.put("encryptCertId", CertUtil.getEncryptCertId());//certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
        //Consumption SMS: token number (obtain from the background notification provisioned from the foreground or obtain from the returned message provisioned from the background)

        /**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
        Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			 //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
        //transactionLogger.error("sms request  ----------------"+reqData);
        String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   								 //At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.

       // System.out.println("SMS Request URL---"+requestBackUrl);
        transactionLogger.error("SMS Request URL---" + requestBackUrl);
        Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding); //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
        transactionLogger.error(" sms response --------"+rspData);

       // System.out.println("SMS Verification Request---"+reqData);
        transactionLogger.error("SMS Verification Request---" + reqData);
       // System.out.println("SMS Verification Response---"+rspData);
        transactionLogger.error("SMS Verification Response---" + rspData);
        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty())
        {
            if(AcpService.validate(rspData, DemoBase.encoding))
            {
              //  System.out.println("Signature authentication succeeds");
                transactionLogger.error("Signature authentication succeeds");
                String bizType_response=rspData.get("bizType");
                String txnSubType=rspData.get("txnSubType");
                String txnType=rspData.get("txnType");
                String acqInsCode=rspData.get("acqInsCode");
                String res_txnTime=rspData.get("txnTime");
                String respCode = rspData.get("respCode") ;
               // System.out.println("respCode---"+respCode);
                transactionLogger.error("respCode---" + respCode);
                if(("00").equals(respCode))
                {
                    unionPayInternationalResponseVO.setStatus("success");
                    unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                    unionPayInternationalResponseVO.setRemark("SMS Send Successful");
                  //  unionPayInternationalResponseVO.setBankTransactionDate(res_txnTime);
                   // unionPayInternationalResponseVO.setResponseTime(res_txnTime);
                   // commResponseVO.setTxnTime(res_txnTime);
                }
                else
                {
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                    unionPayInternationalResponseVO.setRemark("SMS Send Failed / "+UnionPayInternationalErrorCode.getDescription(respCode));
                }
                unionPayInternationalResponseVO.setAmount(transDetailsVO.getAmount());
                unionPayInternationalResponseVO.setCurrency(transDetailsVO.getCurrency());
                unionPayInternationalResponseVO.setTmpl_Amount(tmpl_amount);
                unionPayInternationalResponseVO.setTmpl_Currency(tmpl_currency);
            }
            else
            {
                unionPayInternationalResponseVO.setStatus("fail");
               // System.out.println("Signature authentication fails");
                transactionLogger.error("Signature authentication fails");
            }
        }
        else
        {
            //The returned http state code is incorrect.
            commResponseVO.setStatus("fail");
            commResponseVO.setRemark("Issue while getting Response from bank");
           // System.out.println("No returned message is obtained or the returned http state code is not 200.");
            transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
        }
        return unionPayInternationalResponseVO;
    }

    public GenericResponseVO processEasyEnrollment(String trackingid,CommRequestVO commRequestVO,String smscode)
    {
        transactionLogger.error("sms code inside processEasyEnrollment ----"+smscode);
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        Functions functions=new Functions();
        UnionPayInternationalResponseVO unionPayInternationalResponseVO = new UnionPayInternationalResponseVO();
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        String accountId=commRequestVO.getCommMerchantVO().getAccountId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String displayName = "";
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);
        String termUrl = "";
        String merId =gatewayAccount.getMerchantId(); //first 8 digits, IIN last 7 any
        String orderId = "SMS"+trackingid;
        String txnTime = String.valueOf(dateFormat.format(date));
       // System.out.println("Txn time---"+txnTime);
        transactionLogger.error("Txn time---" + txnTime);
        String txnAmt =unionPayInternationalUtils.getCentAmount(transDetailsVO.getAmount());
        String phoneNo = addressDetailsVO.getPhone();
       // System.out.println("phone number ------"+phoneNo);
        transactionLogger.error("phone number ------" + phoneNo);
        //unionPayInternationalRequestVO.getPhone();
       // transactionLogger.error("phone number for enrollment-------"+commRequestVO.getPhone());
        String accNo =cardDetailsVO.getCardNum();
        String expired=cardDetailsVO.getExpYear().substring(2,4)+cardDetailsVO.getExpMonth();
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }


        Map<String, String> contentData = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        contentData.put("version", DemoBase.version);                    //M
        contentData.put("encoding", DemoBase.encoding);                //M
        contentData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
        // Value: 79
        contentData.put("txnType", "79");// M
        //04: Easy enrollment
        //07: SecurePlus enrollment
        contentData.put("txnSubType", "04");// M
        // 000301: Merchant-hosted
        // 000902: Token payment
        contentData.put("bizType", bizType);// M
        // 0: Merchant direct access
        // 1: Acquirer Access
        contentData.put("accessType", "1");// M                     //Second, the message field accType not necessary
        // Channel type
        contentData.put("channelType", "07");// M

        // Acquirer Code (Applied if access type is acquirer access)
        contentData.put("acqInsCode", "38990826");//C
        // Merchant Category (Applied if access type is acquirer access)
        contentData.put("merCatCode", "8999");// C
        // Merchant Name (Applied if access type is acquirer access)
        contentData.put("merName", "OneStopMoneyManager");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        contentData.put("merAbbr", "OSMM");// C

        contentData.put("merId", merId);                   			   //M client number (this client number is used only for transaction test and debugging and this client has configured to encrypt sensitive information). In the test, please modify this client number to the one you have applied [the test client number starting with 777 you have registered does not support product collecting]

        contentData.put("orderId", orderId);             			   //M client order number, consisting of 8-40 alphanumeric characters, no “-�? or “_�? is allowed, but custom rules are allowed
        contentData.put("txnTime", txnTime);         				   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        contentData.put("accType", "01");                              //M account number type

        Map<String,String> customerInfoMap = new HashMap<String,String>();
        customerInfoMap.put("phoneNo",phoneNo);			           //Phone number
        customerInfoMap.put("cvn2",cardDetailsVO.getcVV());           			        //The three digits corresponding to cvn2 at the back of the card
        customerInfoMap.put("expired", expired);  				        //Validity: The year should come before the month.
        customerInfoMap.put("smsCode", smscode);  				        //SMS authentication code: Always fill in 111111 in test environment.
        ////////////If the client has enabled the right [encrypt sensitive information by the client], you need to encrypt accNo and phoneNo for use.

        String propertyPath = RB.getString("PROPERTY_PATH");
        SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);

        String accNo1 = AcpService.encryptData(accNo, "UTF-8");  //A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
        contentData.put("accNo", accNo1);  //M
        contentData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
        String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,null,DemoBase.encoding);

        contentData.put("customerInfo", customerInfoStr);	//M

        /**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
        Map<String, String> reqData = AcpService.sign(contentData,DemoBase.encoding);			 //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
       // transactionLogger.error("easy enrollemt reqest --------"+reqData);
        String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   								 //At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
        Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding); //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
        transactionLogger.error("easy enrollemt response"+rspData);

       // System.out.println("Easy Enrollment Request---"+reqData);
        //transactionLogger.error("Easy Enrollment Request---"+reqData);
       // System.out.println("Easy Enrollment Response---" + rspData);
        transactionLogger.error("Easy Enrollment Response---" + rspData);
        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty())
        {
            if(AcpService.validate(rspData, DemoBase.encoding))
            {
               // System.out.println("Signature authentication succeeds");
                transactionLogger.error("Signature authentication succeeds");
                String respCode = rspData.get("respCode") ;
               // System.out.println("Easy Enrol Response code---"+respCode);
                transactionLogger.error("Easy Enrol Response code---" + respCode);
                if(("00").equals(respCode))
                {
                    unionPayInternationalResponseVO.setStatus("success");
                    unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                    unionPayInternationalResponseVO.setRemark("Enrollment Successful");
                    //Succeed
                }
                else
                {
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setDescription(UnionPayInternationalErrorCode.getDescription(respCode));
                    unionPayInternationalResponseVO.setRemark("Enrollment Failed / "+UnionPayInternationalErrorCode.getDescription(respCode));
                }
                unionPayInternationalResponseVO.setAmount(transDetailsVO.getAmount());
                unionPayInternationalResponseVO.setCurrency(transDetailsVO.getCurrency());
                unionPayInternationalResponseVO.setTmpl_Amount(tmpl_amount);
                unionPayInternationalResponseVO.setTmpl_Currency(tmpl_currency);
            }
            else
            {
                unionPayInternationalResponseVO.setStatus("fail");
                unionPayInternationalResponseVO.setRemark("Issue with ACPService");
               // System.out.println("Signature authentication fails");
                transactionLogger.error("Signature authentication fails");
            }
        }
        else
        {
            unionPayInternationalResponseVO.setStatus("fail");
            unionPayInternationalResponseVO.setRemark("Issue with Responce Data");
            //The returned http state code is incorrect.
           // System.out.println("No returned message is obtained or the returned http state code is not 200.");
            transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
        }
       return  unionPayInternationalResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processRefund of UnionPayInternational --->");
        Functions functions=new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        UnionPayInternationalResponseVO unionPayInternationalResponseVO=new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        String origQryId= transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("origQryId1 from transactionDetailsVO---->" + origQryId);
        String merId =gatewayAccount.getMerchantId();
        transactionLogger.error("merId --->"+merId);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
       // transactionLogger.debug("bizType ---------------"+bizType);
        transactionLogger.error("bizType --->"+bizType);
        String txnAmt="";
        if (functions.isValueNull(transactionDetailsVO.getAmount()))
        {
            txnAmt=unionPayInternationalUtils.getCentAmount(transactionDetailsVO.getAmount());
        }
        transactionLogger.error("txnAmt --->"+txnAmt);
        String propertyPath = RB.getString("PROPERTY_PATH");
        SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transactionDetailsVO.getCurrency())) {
            currency = transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        //CERT_PATH=/apps/tomcat/tomcat8/lib/com/directi/pg/ems/

        Map<String, String> data = new HashMap<String, String>();
        data.put("version", DemoBase.version);
        data.put("encoding", DemoBase.encoding);
        data.put("signMethod", SDKConfig.getConfig().getSignMethod());
        data.put("txnType", "04");
        data.put("txnSubType", "00");
        data.put("bizType",bizType);
        data.put("accessType", "1");
        data.put("channelType", "07");
        data.put("acqInsCode", "38990826");
        data.put("merCatCode", "8999");
        data.put("merName", "OneStopMoneyManager");
        data.put("merAbbr", "OSMM");
        data.put("merId", merId);
        data.put("orderId", DemoBase.getOrderId());
        data.put("txnTime", DemoBase.getCurrentTime());
        //	data.put("currencyCode", "156");
        data.put("txnAmt", txnAmt);
        data.put("backUrl",RB.getString("acpsdk_backUrl")+trackingID);
        data.put("origQryId", origQryId);

        Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);
        transactionLogger.error("Refund Request--->"+reqData);
        String url ="";
        if (isTest)
        {
            url=RB.getString("acpsdk_backTransUrl_Test");
        }
        else
        {
            url=RB.getString("acpsdk_backTransUrl_Live");
        }
        transactionLogger.error("url --->"+url);
        Map<String, String> rspData = AcpService.post(reqData, url,DemoBase.encoding);
        transactionLogger.error("Refund Response--->" + rspData);

        if(!rspData.isEmpty())
        {
            if(AcpService.validate(rspData, DemoBase.encoding))
            {
                transactionLogger.error("Signature authentication succeeds");
                String respCode = rspData.get("respCode");
                String responseMsg=UnionPayInternationalErrorCode.getDescription(respCode);
                transactionLogger.error("Refund respCode---"+respCode);
                transactionLogger.error("responseMsg from error code --->"+responseMsg);
                if(("00").equals(respCode))
                {
                    transactionLogger.error("Response Code ---->"+respCode);
                    unionPayInternationalResponseVO.setStatus("success");
                    unionPayInternationalResponseVO.setRemark(responseMsg);
                    unionPayInternationalResponseVO.setDescription(responseMsg);
                }
                else if(("03").equals(respCode)||  ("04").equals(respCode)||   ("05").equals(respCode))
                {
                    transactionLogger.error("Response Code ---->"+respCode);
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setRemark(responseMsg);
                    unionPayInternationalResponseVO.setDescription(responseMsg);
                }
                else
                {
                    transactionLogger.error("Different Response Code --->"+respCode);
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setRemark(responseMsg);
                    unionPayInternationalResponseVO.setDescription(responseMsg);
                }
                unionPayInternationalResponseVO.setAmount(transactionDetailsVO.getAmount());
                unionPayInternationalResponseVO.setCurrency(transactionDetailsVO.getCurrency());
                unionPayInternationalResponseVO.setTmpl_Amount(tmpl_amount);
                unionPayInternationalResponseVO.setTmpl_Currency(tmpl_currency);
            }
            else
            {
                transactionLogger.error("Signature authentication fails");
                unionPayInternationalResponseVO.setStatus("fail");
                unionPayInternationalResponseVO.setRemark("Signature authentication fails");
            }
        }
        else
        {
            transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
            unionPayInternationalResponseVO.setStatus("fail");
            unionPayInternationalResponseVO.setRemark("No returned message is obtained or the returned http state code is not 200.");
        }
       return unionPayInternationalResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Entering processVoid of UnionPayInternational --->");
        Functions functions=new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        UnionPayInternationalResponseVO unionPayInternationalResponseVO=new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        String origQryId= transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("origQryId1 from transactionDetailsVO---->" + origQryId);
        String merId =gatewayAccount.getMerchantId();
        transactionLogger.error("merId --->"+merId);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);
        String currencyCode="";
        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            currencyCode=CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        }
        String txnAmt="";
        if (functions.isValueNull(transactionDetailsVO.getAmount()))
        {
            txnAmt=unionPayInternationalUtils.getCentAmount(transactionDetailsVO.getAmount());
        }
        transactionLogger.error("txnAmt --->"+txnAmt);

        try
        {
            String propertyPath = RB.getString("PROPERTY_PATH");
            SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);

            Map<String, String> data = new HashMap<String, String>();
            data.put("version", DemoBase.version);
            data.put("encoding", DemoBase.encoding);
            data.put("signMethod", SDKConfig.getConfig().getSignMethod());
            data.put("txnType", "32");
            data.put("txnSubType", "00");
            data.put("bizType",bizType);
            data.put("channelType", "07");
            data.put("accessType", "1");
            data.put("acqInsCode","38990826");
            data.put("merCatCode", "");
            data.put("merName", "OneStopMoneyManager");
            data.put("merAbbr", "OSMM");
            data.put("merId", merId);
            data.put("orderId", DemoBase.getOrderId());
            data.put("txnTime", DemoBase.getCurrentTime());
            data.put("txnAmt", txnAmt);
            data.put("currencyCode",currencyCode);
            data.put("backUrl", RB.getString("acpsdk_backUrl")+trackingID);
            data.put("origQryId", origQryId);

            Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);
            String url = "";//SDKConfig.getConfig().getBackRequestUrl();
            if (isTest)
            {
              url=RB.getString("acpsdk_backTransUrl_Test") ;
            }
            else
            {
              url=RB.getString("acpsdk_backTransUrl_Live") ;
            }
            transactionLogger.error("Cancel Request --->"+reqData);
            Map<String,String> rspData = AcpService.post(reqData,url,DemoBase.encoding);
            transactionLogger.error("Cancel response --->"+rspData);


            if(!rspData.isEmpty())
            {
                if(AcpService.validate(rspData, DemoBase.encoding))
                {
                    transactionLogger.error("Signature authentication succeeds");
                    String respCode= respCode = rspData.get("respCode");
                    String responseMsg=UnionPayInternationalErrorCode.getDescription(respCode);
                    transactionLogger.error("Refund respCode---"+respCode);
                    transactionLogger.error("responseMsg from error code --->"+responseMsg);
                   transactionLogger.error("Resp Code--->" + respCode);
                    if("00".equals(respCode))
                    {
                        transactionLogger.error("Response Code ---->"+respCode);
                        unionPayInternationalResponseVO.setStatus("success");
                        unionPayInternationalResponseVO.setRemark(responseMsg);
                    }
                    else if("03".equals(respCode) ||   "04".equals(respCode) ||  "05".equals(respCode))
                    {
                        transactionLogger.error("Response Code ---->"+respCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setRemark(responseMsg);
                    }
                    else
                    {
                        transactionLogger.error("Different Response Code --->"+respCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setRemark(responseMsg);
                    }
                }
                else
                {
                    transactionLogger.error("Signature authentication fails");
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setRemark("Signature authentication fails");
                }
            }
            else
            {
                transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
                unionPayInternationalResponseVO.setStatus("fail");
                unionPayInternationalResponseVO.setRemark("No returned message is obtained or the returned http state code is not 200.");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return unionPayInternationalResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processCapture of UnionPayInternational --->");
        Functions functions=new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        UnionPayInternationalResponseVO unionPayInternationalResponseVO=new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        String origQryId= transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("origQryId1 from transactionDetailsVO---->" + origQryId);
        String merId =gatewayAccount.getMerchantId();
        transactionLogger.error("merId --->"+merId);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);
        String currencyCode="";
        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            currencyCode=CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        }
        String txnAmt="";
        if (functions.isValueNull(transactionDetailsVO.getAmount()))
        {
            txnAmt=unionPayInternationalUtils.getCentAmount(transactionDetailsVO.getAmount());
        }
        transactionLogger.error("txnAmt --->"+txnAmt);

        try
        {
            String propertyPath = RB.getString("PROPERTY_PATH");
            SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);
            Map<String, String> data = new HashMap<String, String>();

            data.put("version", DemoBase.version);
            data.put("encoding", DemoBase.encoding);
            data.put("signMethod", SDKConfig.getConfig().getSignMethod());
            data.put("txnType", "03");
            data.put("txnSubType", "00");
            data.put("bizType",bizType);
            data.put("channelType", "07");
            data.put("accessType", "1");
            data.put("acqInsCode","38990826");
            //data.put("merCatCode", "4511");
            data.put("merName", "OneStopMoneyManager");
            data.put("merAbbr", "OSMM");
            data.put("merId", merId);
            data.put("orderId", DemoBase.getOrderId());
            data.put("txnTime", DemoBase.getCurrentTime());
            data.put("txnAmt", txnAmt);
            data.put("currencyCode",currencyCode);
            data.put("backUrl", SDKConfig.getConfig().getBackUrl()+trackingID);
            data.put("origQryId", origQryId);

            Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);
            String url = "";//SDKConfig.getConfig().getBackRequestUrl();
            if (isTest)
            {
                url=RB.getString("acpsdk_backTransUrl_Test") ;
            }
            else
            {
                url=RB.getString("acpsdk_backTransUrl_Live") ;
            }
            transactionLogger.error("Capture Request --->"+reqData);
            Map<String,String> rspData = AcpService.post(reqData,url,DemoBase.encoding);
            transactionLogger.error("Capture Response --->"+rspData);

            if(!rspData.isEmpty())
            {
                if(AcpService.validate(rspData, DemoBase.encoding))
                {
                    transactionLogger.error("Signature authentication succeeds");
                    String respCode= respCode = rspData.get("respCode");
                    String responseMsg=UnionPayInternationalErrorCode.getDescription(respCode);
                    transactionLogger.error("Refund respCode---"+respCode);
                    transactionLogger.error("responseMsg from error code --->"+responseMsg);
                    if("00".equals(respCode))
                    {
                        transactionLogger.error("Response Code ---->"+respCode);
                        unionPayInternationalResponseVO.setStatus("success");
                        unionPayInternationalResponseVO.setRemark(responseMsg);
                        unionPayInternationalResponseVO.setTransactionId(origQryId);
                    }
                    else if("03".equals(respCode) ||  "04".equals(respCode) ||  "05".equals(respCode))
                    {
                        transactionLogger.error("Response Code ---->"+respCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setRemark(responseMsg);
                    }
                    else
                    {
                        transactionLogger.error("Different Response Code --->"+respCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setRemark(responseMsg);
                    }
                }
                else
                {
                    transactionLogger.error("Signature authentication fails");
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setRemark("Signature authentication fails");
                }
            }
            else
            {
                transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
                unionPayInternationalResponseVO.setStatus("fail");
                unionPayInternationalResponseVO.setRemark("No returned message is obtained or the returned http state code is not 200.");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return unionPayInternationalResponseVO;
    }

    public GenericResponseVO processInquiry( GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processInquiry of UnionPayInternational ---");
        Functions functions=new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        UnionPayInternationalResponseVO unionPayInternationalResponseVO=new UnionPayInternationalResponseVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        String origQryId= transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("origQryId1 from transactionDetailsVO---->" + origQryId);
        String merId =gatewayAccount.getMerchantId();
        transactionLogger.error("merId --->"+merId);
        String bizType=gatewayAccount.getCHARGEBACK_FTP_PATH();
        transactionLogger.error("bizType --->"+bizType);
        String currencyCode="";
        String transactionStatus="";
        String transactionTime="";
        if (functions.isValueNull(transactionDetailsVO.getOrderId()))
        {
             HashMap<String,String> hashMapDetails=unionPayInternationalUtils.getTransactionStatus(transactionDetailsVO.getOrderId());
              transactionStatus= hashMapDetails.get("dbStatus");
              transactionTime= hashMapDetails.get("transactionTime");
        }
        String txnAmt="";
        if (functions.isValueNull(transactionDetailsVO.getAmount()))
        {
            txnAmt=unionPayInternationalUtils.getCentAmount(transactionDetailsVO.getAmount());
        }
        transactionLogger.error("txnAmt --->"+txnAmt);

        try
        {
            String propertyPath = RB.getString("PROPERTY_PATH");
            SDKConfig.getConfig().loadPropertiesFromPath(propertyPath);

            Map<String, String> data = new HashMap<String, String>();
            data.put("version", DemoBase.version);
            data.put("encoding", DemoBase.encoding);
            data.put("signMethod", SDKConfig.getConfig().getSignMethod());
            data.put("txnType", "00");
            data.put("txnSubType", "00");
            data.put("bizType","000000");
            data.put("accessType", "1");
            data.put("acqInsCode","38990826");
            data.put("merCatCode", "");
            data.put("merName", " ");
            data.put("merAbbr", " ");
            data.put("merId", merId);
            data.put("orderId","000"+transactionDetailsVO.getOrderId());
            data.put("txnTime",DemoBase.getCurrentTime());
            Map<String, String> reqData = AcpService.sign(data,DemoBase.encoding);
            transactionLogger.error("Inquiry Request --->"+reqData);
            String url = ""; //SDKConfig.getConfig().getSingleQueryUrl();
            if (isTest)
            {
                url=RB.getString("acpsdk_singleQueryUrl_Test") ;
            }
            else
            {
                url=RB.getString("acpsdk_singleQueryUrl_Live") ;
            }
            Map<String, String> rspData = AcpService.post(reqData,url,DemoBase.encoding);
            transactionLogger.error("Inquiry response --->"+rspData);
            String origRespCode="";
            if(!rspData.isEmpty())
            {
                if(AcpService.validate(rspData, DemoBase.encoding))
                {
                    transactionLogger.error("Signature authentication succeeds");
                    if(("00").equals(rspData.get("respCode")))
                    {
                        origRespCode = rspData.get("origRespCode");
                        transactionLogger.error("origRespCode---" + origRespCode);
                        if(("00").equals(origRespCode))
                        {
                            transactionLogger.error("origRespCode---" + origRespCode);
                            unionPayInternationalResponseVO.setStatus("success");
                            unionPayInternationalResponseVO.setRemark("success");
                            unionPayInternationalResponseVO.setDescription("success");
                            unionPayInternationalResponseVO.setTransactionStatus(transactionStatus);
                            if (functions.isValueNull(transactionTime)) {
                                unionPayInternationalResponseVO.setResponseTime(transactionTime);
                                unionPayInternationalResponseVO.setBankTransactionDate(transactionTime);
                            }
                            else{
                                unionPayInternationalResponseVO.setResponseTime("-");
                                unionPayInternationalResponseVO.setBankTransactionDate("-");
                            }

                            unionPayInternationalResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                            unionPayInternationalResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            unionPayInternationalResponseVO.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
                            unionPayInternationalResponseVO.setAuthCode(origRespCode);
                            unionPayInternationalResponseVO.setTransactionId(transactionDetailsVO.getPreviousTransactionId());
                            unionPayInternationalResponseVO.setAmount(transactionDetailsVO.getAmount());
                            unionPayInternationalResponseVO.setMerchantId(merId);

                        }
                        else if(("03").equals(origRespCode)|| ("04").equals(origRespCode)|| ("05").equals(origRespCode))
                        {
                            transactionLogger.error("Response Code ---->" + origRespCode);
                            unionPayInternationalResponseVO.setStatus("fail");
                            unionPayInternationalResponseVO.setRemark("remark code-------" + origRespCode);
                            unionPayInternationalResponseVO.setTransactionStatus("success");
                            unionPayInternationalResponseVO.setDescription("fail");
                        }
                        else
                        {
                            transactionLogger.error("Different Response Code --->"+origRespCode);
                            unionPayInternationalResponseVO.setStatus("fail");
                            unionPayInternationalResponseVO.setRemark("remark code-------"+origRespCode);
                        }
                    }
                    else if(("34").equals(rspData.get("respCode")))
                    {
                        transactionLogger.error("Response Code ---->"+origRespCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setRemark("remark code-------"+origRespCode);

                    }
                    else
                    {
                        transactionLogger.error("Different Response Code --->"+origRespCode);
                        unionPayInternationalResponseVO.setStatus("fail");
                        unionPayInternationalResponseVO.setRemark("remark code-------"+origRespCode);
                    }
                }
                else
                {
                    transactionLogger.error("Signature authentication fails");
                    unionPayInternationalResponseVO.setStatus("fail");
                    unionPayInternationalResponseVO.setRemark("remark code-------" + origRespCode);
                }
            }
            else
            {
                transactionLogger.error("No returned message is obtained or the returned http state code is not 200.");
                unionPayInternationalResponseVO.setStatus("fail");
                unionPayInternationalResponseVO.setRemark("remark code-------"+origRespCode);
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return unionPayInternationalResponseVO;
    }


    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("processAutoRedirect in UnionPayInternationalPaymentGateway ---->");
        String html="";
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        html = unionPayInternationalUtils.getSecurePayhtml(commonValidatorVO);
        transactionLogger.error("html in UnionPayInternationalPaymentGateway --->"+html);
        return html;
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}