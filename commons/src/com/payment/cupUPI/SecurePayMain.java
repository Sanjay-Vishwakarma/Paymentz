package com.payment.cupUPI;

import com.payment.cupUPI.unionpay.acp.sdk.AcpService;
import com.payment.cupUPI.unionpay.acp.sdk.CertUtil;
import com.payment.cupUPI.unionpay.acp.sdk.DemoBase;
import com.payment.cupUPI.unionpay.acp.sdk.SDKConfig;

import javax.servlet.http.HttpServlet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Admin on 09-May-19.
 */
public class SecurePayMain extends HttpServlet
{
    public static void preAuth()
    {
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();

        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String orderId = "988765685555";
        String txnTime = String.valueOf(dateFormat.format(date));
        String txnAmt = "100";
        String phoneNo = "13012345678";
        //String accNo = "6250947000000014";//Card Number
        String accNo = "6223164991230014";//Card Number
        String acqInsCode = "38990826";

        Map<String, String> requestData = new HashMap<String, String>();
        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");
        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        requestData.put("version", DemoBase.version);   			  //M
        requestData.put("encoding", DemoBase.encoding); 			  //M
        requestData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
        //01: Pre-authorization
        //02: MOTO pre-authorization
        requestData.put("txnType", "02");               			  //M
        requestData.put("txnSubType", "01");            			  //M
        //000301: Merchant-hosted
        //000000: ExpressPay
        //000902: Token payment
        //001001: MOTO
        requestData.put("bizType", "000301");           			  //M
        //07: Internet
        //08: Mobile
        requestData.put("channelType", "07");           			  //M
        //0: Merchant direct access
        //1: Acquirer access
        //2: Platform merchant access
        requestData.put("accessType", "1");
        // Acquirer Code (Applied if access type is acquirer access)
        requestData.put("acqInsCode", acqInsCode);//C
        // Merchant Category (Applied if access type is acquirer access)
        requestData.put("merCatCode", "8999");// C
        // Merchant Name (Applied if access type is acquirer access)
        requestData.put("merName", "OSMM");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        requestData.put("merAbbr", "OSMM");// C

        requestData.put("merId", merId);    	          			  //M client number: Please modify it to the formal client number you have applied or the test client number 777 you have registered at open.unionpay.com.
        requestData.put("orderId",DemoBase.getOrderId());             //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed
        requestData.put("txnTime", DemoBase.getCurrentTime());        //M order delivery time: It must be system time in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        requestData.put("currencyCode", "156");         			  //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        requestData.put("txnAmt", txnAmt);             			      //M transaction amount: in cents, without any decimal point.

        requestData.put("frontUrl", DemoBase.frontUrl);   //C


        requestData.put("backUrl", DemoBase.backUrl);//M

        Map<String,String> customerInfoMap = new HashMap<String,String>();
        //customerInfoMap.put("smsCode", "111111");			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
        customerInfoMap.put("phoneNo", phoneNo);			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.
        customerInfoMap.put("cvn2", "123");           			        //The three digits corresponding to cvn2 at the back of the card
        customerInfoMap.put("expired", "3312");  				        //Validity: The year should come before the month.

        //requestData.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime() + 15 * 60 * 1000));

        String accNo1 = AcpService.encryptData(accNo, DemoBase.encoding);  //A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
        requestData.put("accNo", accNo1);
        requestData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
        String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,accNo,DemoBase.encoding);

        requestData.put("customerInfo", customerInfoStr);//M

        /**Sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
        Map<String, String> reqData = AcpService.sign(requestData,DemoBase.encoding);			//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
        String requestBackUrl = SDKConfig.getConfig().getBackRequestUrl();   			//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
        Map<String, String> rspData = AcpService.post(reqData,requestBackUrl,DemoBase.encoding); //Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.

        System.out.println("preauth request---"+reqData);
        System.out.println("preauth response---"+rspData);
        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        StringBuffer parseStr = new StringBuffer("");
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                System.out.println("Signature authentication succeeds");
                String respCode = rspData.get("respCode") ;
                System.out.println("preauth resp code---"+respCode);
                if(("00").equals(respCode)){
                    //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to update the order state after receiving the background notification, or actively initiate a transaction query to determine the transaction state.
                    //TODO
                    //If you have configured encryption for sensitive information, you can use the method below to decrypt the card number to obtain the plain text card number.
//					String accNo1 = rspData.get("accNo");
//					String accNo2 = AcpService.decryptData(accNo1, "UTF-8");  //To decrypt a card number, you need to use the private key certificate acpsdk.signCert.path signed by the client.
//					LogUtil.writeLog("Decrypted card number:"+accNo2);
//					parseStr.append("Decrypted card number:"+accNo2);
                }else if(("03").equals(respCode)||
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){
                    //Also, you can initiate a transaction state query later to determine the transaction state.
                    //TODO
                }else{
                    //Other response codes are failure. Please find the cause.
                    //TODO
                }
            }else{
                System.out.println("Signature authentication fails");
                //TODO Find the reason why the signature authentication fails
            }
        }else{
            //The returned http state code is incorrect.
            System.out.println("No returned message is obtained or the returned http state code is not 200.");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
    }


    public static void purchase()
    {
        DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();

        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String orderId = "988765689993";
        String txnTime = String.valueOf(dateFormat.format(date));
        String txnAmt = "100";
        String IIN = "38990826";

        Map<String, String> contentData = new HashMap<String, String>();
        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
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
        contentData.put("bizType", "000201");         //M
        // 0: Merchant direct access
        // 1: Acquirer Access
        // 2: Platform merchant access
        contentData.put("accessType", "1");// M
        // 07: Internet
        // 08: Mobile
        contentData.put("channelType", "07");// M
        // Acquirer Code (Applied if access type is acquirer access)
        contentData.put("acqInsCode", IIN);//C
        // Merchant Category (Applied if access type is acquirer access)
        contentData.put("merCatCode", "5411");// C
        // Merchant Name (Applied if access type is acquirer access)
        contentData.put("merName", "UPI Test Merchant");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        contentData.put("merAbbr", "UPI Test");// C
        // Merchant ID
        contentData.put("merId", merId);                   			  //M
        // Merchant order No
        contentData.put("orderId", orderId);             			   //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed
        // Date and time when merchant sends transaction
        contentData.put("txnTime", txnTime);         				   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        // Default value is 156.
        contentData.put("currencyCode", "156");						   //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        // The unit of transaction amount is cent.
        contentData.put("txnAmt", txnAmt);							   //M transaction amount: in cents, without any decimal point.

        //Consumption: The transaction element card number and authentication code depend on the service configuration (by default, an SMS authentication code is required).
        //Map<String,String> customerInfoMap = new HashMap<String,String>();
        //customerInfoMap.put("smsCode", "111111");			    	//SMS authentication code: You will not actually receive an SMS in the test environment. Therefore, always fill in 111111 here.

        ////////////If the client has enabled the right [encrypt sensitive information by the client], you need to encrypt accNo, pin, phoneNo, cvn2, and expired (if these fields will be sent later) for encryption of sensitive information.
        //String accNo = AcpService.encryptData("6216261000000000018", DemoBase.encoding);  //A test card number is used here because it is in test environment. In normal environment, please use a real card number instead.
        //contentData.put("accNo", accNo);
        //contentData.put("encryptCertId",AcpService.getEncryptCertId());       //certId of the encryption certificate, which is configured under the acpsdk.encryptCert.path property of the acp_sdk.properties file.
        //String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap,"6216261000000000018",DemoBase.encoding);

        //contentData.put("customerInfo", customerInfoStr);//M

        contentData.put("payTimeout", "");// O

        contentData.put("backUrl", SDKConfig.getConfig().getBackUrl());

        contentData.put("frontUrl", SDKConfig.getConfig().getFrontUrl());

        /**All request parameters have been set. Now, sign the request parameters and generate an html form. Then, write the form to the browser and jump to and open the UnionPay page.**/
        Map<String, String> submitFromData = AcpService.sign(contentData,DemoBase.encoding);  //In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.

        String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //Obtain the requested foreground UnionPay address: acpsdk.frontTransUrl in the corresponding property file acp_sdk.properties
        String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,DemoBase.encoding);   //Generate an html form which can jump to UnionPay page automatically

        System.out.println("Print the request HTML, which is a request message and the basis for problem joint debugging and troubleshooting"+html);
        //Write the generated html to the browser to automatically jump to and open the UnionPay payment page. Here, do not modify the names and values of the form items in the html after calling signData or before writing the html to the browser. Such modification may cause failure of the signature authentication.
        //resp.getWriter().write(html);
    }

    public static void purchaseCancel()
    {
        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String origQryId = "321907041722438605028";
        String txnAmt = "100";
        String acqInsCode = "38990826";

        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");
        Map<String, String> data = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        data.put("version", DemoBase.version);            //M version number
        data.put("encoding", DemoBase.encoding);          //M character set code: Both UTF-8 and GBK can be used.
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M signature method
        data.put("txnType", "31");                        //M transaction type 31-purchase cancellation
        data.put("txnSubType", "00");                     //M transaction subtype  00 by default
        //000301: Merchant-hosted
        //000000: ExpressPay
        //000902: Token payment
        //001001: MOTO
        //000501: Recurring
        data.put("bizType", "000301");       //M       
        //07: Internet
        //08: Mobile
        data.put("channelType", "07");                    //M
        //0: Merchant direct access
        //1: Acquirer access
        data.put("accessType", "1");    //M
        //
        data.put("merId", merId);            //M client number: Please modify it to the client number you have applied or the test client number 777 you have registered at open.unionpay.com.
        // Acquirer Code (Applied if access type is acquirer access)
        data.put("acqInsCode", acqInsCode);//C
        // Merchant Category (Applied if access type is acquirer access)
        data.put("merCatCode", "8999");// C
        // Merchant Name (Applied if access type is acquirer access)
        data.put("merName", "OneStopMoneyManager");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        data.put("merAbbr", "OSMM");// C

        data.put("orderId", DemoBase.getOrderId());       //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed. It is an order number regenerated, different from the one of the original purchase transaction.		

        data.put("txnTime", DemoBase.getCurrentTime());   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.

        data.put("txnAmt", txnAmt);                       //M [amount canceled], the amount canceled must be the same as the original purchase amount in a purchase cancellation.	

        data.put("currencyCode", "156");                  //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        //data.put("reqReserved", "Transparent transmit information");                 //It is a domain reserved by the requester and you can enable it if you want to use it. Transparent transmit field (can be used to track client's custom parameters): you can use this field to query the background notifications and transaction state of this transaction, and such information will be returned as it was in the reconciliation file. The client can upload such information as required and the length of the information to be uploaded should be in range of 1-1024 bytes. The occurrence of symbols like &, =, {}, and [] may cause failure to parsing response messages from the query interface. Therefore, you are recommended to transmit only alphanumeric characters and use | for separation. Or, you can conduct a base64 encoding at the outermost layer (a “=” appears after a base64 encoding can be neglected because it will not cause parse failure).		

        data.put("backUrl", SDKConfig.getConfig().getBackUrl());            //Background notification address. For details about background notification parameters, refer to Help Center > Download > Product Interface Specifications > Interface Specifications for Gateway Payment Products > Purchase Cancellation Transactions > Client Notification at open.unionpay.com. The other descriptions are the same as client notifications of purchase transactions.

        /***To debug a transaction so that it runs properly, you must modify the fields below.***/
        data.put("origQryId", origQryId);   			  //[Original transaction serial number]: queryId returned by the original purchase transaction, which can be obtained from the background notification interface or transaction state query interface for purchase transaction.

        /**All request parameters have been set successfully. Now, let's sign the requested parameters, and send http.post requests and receive synchronous response messages.**/
        Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
        String reqUrl = SDKConfig.getConfig().getBackRequestUrl();//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.


        Map<String,String> rspData = AcpService.post(reqData,reqUrl,DemoBase.encoding);//Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        System.out.println("Purchase Cancel Request---"+reqData);
        System.out.println("Purchase Cancel Response---"+rspData);
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                System.out.println("Signature authentication succeeds");
                String respCode = rspData.get("respCode");
                System.out.println("Purchase Cancel response code---"+respCode);

                if("00".equals(respCode)){
                    //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to wait for the background notification to determine whether the transaction is successful, or actively initiate a transaction query to determine the transaction state.
                    //TODO
                    System.out.println("respCode = 00");
                }else if("03".equals(respCode) ||
                        "04".equals(respCode) ||
                        "05".equals(respCode)){
                    //Also, you can initiate a transaction state query later to determine the transaction state.
                    //TODO
                }else{
                    //Other response codes are failure. Please find the cause.
                    //TODO
                }
            }else{
                System.out.println("Signature authentication fails");
                //TODO Find the reason why the signature authentication fails
            }
        }else{
            //The returned http state code is incorrect.
            System.out.println("No returned message is obtained or the returned http state code is not 200.");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
    }

    public static void refund()
    {
        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String origQryId = "161907041755038642508";
        String txnAmt = "100";
        String acqInsCode = "38990826";

        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");
        Map<String, String> data = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        data.put("version", DemoBase.version);               //Version number
        data.put("encoding", DemoBase.encoding);             //Character set code: Both UTF-8 and GBK can be used.
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //Signature method
        // Value: 04
        data.put("txnType", "04");// M
        // 00: Default
        data.put("txnSubType", "00");// M
        //000301: Merchant-hosted
        //000000: ExpressPay
        //000902: Token payment
        //001001: MOTO
        //000501: Recurring
        data.put("bizType", "000301");// M
        // 0: Merchant direct access
        // 1: Acquirer Access
        // 2: Platform merchant access
        data.put("accessType", "1");// M
        // 07: Internet
        // 08: Mobile
        data.put("channelType", "07");// M
        // Acquirer Code (Applied if access type is acquirer access)
        data.put("acqInsCode", acqInsCode);//C
        // Merchant Category (Applied if access type is acquirer access)
        data.put("merCatCode", "8999");// C
        // Merchant Name (Applied if access type is acquirer access)
        data.put("merName", "OneStopMoneyManager");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        data.put("merAbbr", "OSMM");// C

        data.put("merId", merId);                //M client number: Please modify it to the client number you have applied or the test client number 777 you have registered at open.unionpay.com.

        data.put("orderId", DemoBase.getOrderId());          //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed. It is an order number regenerated, different from the one of the original purchase transaction.
        data.put("txnTime", DemoBase.getCurrentTime());      //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        //	data.put("currencyCode", "156");                  Transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        data.put("txnAmt", txnAmt);                          //M**** refund amount: in cents, without any decimal point. The refund amount must be smaller than or equal to the original purchase amount. When it is smaller than the original purchase amount, multiple refunds are supported until the total refund amount is equal to the original purchase amount.
        data.put("backUrl", SDKConfig.getConfig().getBackUrl());               //M background notification address. For details about background notification parameters, refer to Help Center > Download > Product Interface Specifications > Interface Specifications for Gateway Payment Products > Refund Transactions > Client Notification at open.unionpay.com. The other descriptions are the same as background notifications of purchase transactions.

        /***To debug a transaction so that it runs properly, you must modify the fields below.***/
        data.put("origQryId", origQryId);      //M****: queryId returned by the original purchase transaction, which can be obtained from the background notification interface or transaction state query interface for purchase transaction.

        /**All request parameters have been set successfully. Now, let's sign the requested parameters, and send http.post requests and receive synchronous response messages.------------->**/

        Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);			//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
        String url = SDKConfig.getConfig().getBackRequestUrl();									 	//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
        Map<String, String> rspData = AcpService.post(reqData, url,DemoBase.encoding);	//Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.

        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        System.out.println("Refund Request---"+reqData);
        System.out.println("Refund Response---"+rspData);
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                System.out.println("Signature authentication succeeds");
                String respCode = rspData.get("respCode");
                System.out.println("Refund respCode---"+respCode);
                if(("00").equals(respCode)){
                    //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to update the order state after receiving the background notification, or actively initiate a transaction query to determine the transaction state.
                    //TODO
                }else if(("03").equals(respCode)||
                        ("04").equals(respCode)||
                        ("05").equals(respCode)){
                    //Also, you can initiate a transaction state query later to determine the transaction state.
                    //TODO
                }else{
                    //Other response codes are failure. Please find the cause.
                    //TODO
                }
            }else{
                System.out.println("Signature authentication fails");
                //TODO Find the reason why the signature authentication fails
            }
        }else{
            //The returned http state code is incorrect.
            System.out.println("No returned message is obtained or the returned http state code is not 200.");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
    }

    public static void preAuthCancel()
    {
        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String origQryId = "081907041759458682528";
        String txnAmt = "100";
        String acqInsCode = "38990826";

        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");

        Map<String, String> data = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        data.put("version", DemoBase.version);            //M
        data.put("encoding", DemoBase.encoding);          //M
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
        data.put("txnType", "32");                        //M
        data.put("txnSubType", "00");                     //M 00 by default.
        //000301 Merchant-hosted
        //	000000: ExpressPay
        //	000902: Token payment
        //	001001: MOTO
        data.put("bizType", "000301");                    //M business type
        //07: Internet
        //08: Mobile
        data.put("channelType", "07");           			  //M
        //0: Merchant direct access
        //1: Acquirer access
        //2: Platform merchant access
        data.put("accessType", "1");
        // Acquirer Code (Applied if access type is acquirer access)
        data.put("acqInsCode", acqInsCode);//C
        // Merchant Category (Applied if access type is acquirer access)
        data.put("merCatCode", "");// C
        // Merchant Name (Applied if access type is acquirer access)
        data.put("merName", "");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        data.put("merAbbr", "");// C

        data.put("merId", merId);             //M client number: Please modify it to the client number you have applied or the test client number 777 you have registered at open.unionpay.com.

        data.put("orderId", DemoBase.getOrderId());       //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed. It is an order number regenerated, different from the one of the original purchase transaction.
        data.put("txnTime", DemoBase.getCurrentTime());   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        data.put("txnAmt", txnAmt);                       //M [amount canceled], the amount canceled must be the same as the original purchase amount in a purchase cancellation.
        data.put("currencyCode", "156");                  //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        //data.put("reqReserved", "Transparent transmit information");                 //It is a domain reserved by the requester and you can enable it if you want to use it. Transparent transmit field (can be used to track client's custom parameters): you can use this field to query the background notifications and transaction state of this transaction, and such information will be returned as it was in the reconciliation file. The client can upload such information as required and the length of the information to be uploaded should be in range of 1-1024 bytes. The occurrence of symbols like &, =, {}, and [] may cause failure to parsing response messages from the query interface. Therefore, you are recommended to transmit only alphanumeric characters and use | for separation. Or, you can conduct a base64 encoding at the outermost layer (a “=” appears after a base64 encoding can be neglected because it will not cause parse failure).
        data.put("backUrl", SDKConfig.getConfig().getBackUrl());               //M background notification address. For details about background notification parameters, refer to Help Center > Download > Product Interface Specifications > Interface Specifications for Gateway Payment Products > Refund Transactions > Client Notification at open.unionpay.com. The other descriptions are the same as background notifications of purchase transactions.

        /***To debug a transaction so that it runs properly, you must modify the fields below.***/
        data.put("origQryId", origQryId);   			  //M [Original transaction serial number]: queryId returned by the original purchase transaction, which can be obtained from the background notification interface or transaction state query interface for purchase transaction.


        /**All request parameters have been set successfully. Now, let's sign the requested parameters, and send http.post requests and receive synchronous response messages.**/

        Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
        String url = SDKConfig.getConfig().getBackRequestUrl();//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.

        Map<String,String> rspData = AcpService.post(reqData,url,DemoBase.encoding);//Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.
        System.out.println("preauth cancel request---"+reqData);
        System.out.println("preauth cancel response---"+rspData);

        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                System.out.println("Signature authentication succeeds");
                String respCode = rspData.get("respCode");
                System.out.println("Resp Code---"+respCode);
                if("00".equals(respCode)){
                    //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to wait for the background notification to determine whether the transaction is successful, or actively initiate a transaction query to determine the transaction state.
                    //TODO
                }else if("03".equals(respCode) ||
                        "04".equals(respCode) ||
                        "05".equals(respCode)){
                    //Also, you can initiate a transaction state query later to determine the transaction state.
                    //TODO
                }else{
                    //Other response codes are failure. Please find the cause.
                    //TODO
                }
            }else{
                System.out.println("Signature authentication fails");
                //TODO Find the reason why the signature authentication fails
            }
        }else{
            //The returned http state code is incorrect.
            System.out.println("No returned message is obtained or the returned http state code is not 200.");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
    }

    public static void preAuthCapture()
    {
        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String origQryId = "381907041803188663758";
        String txnAmt = "100";
        String acqInsCode = "38990826";

        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");
        Map<String, String> data = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        data.put("version", DemoBase.version);            //M version number
        data.put("encoding", DemoBase.encoding);          //M character set code: Both UTF-8 and GBK can be used.
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M signature method
        data.put("txnType", "03");                        //M transaction type 03-pre-authorization completed
        data.put("txnSubType", "00");                     //M transaction subtype  00 by default
        //000301: Merchant-hosted
        //	000000: ExpressPay
        //	000902: Token payment
        //	001001: MOTO
        data.put("bizType", "000301");                    //M business type
        //07: Internet
        //08: Mobile
        data.put("channelType", "07");           			  //M
        //0: Merchant direct access
        //1: Acquirer access
        //2: Platform merchant access
        data.put("accessType", "1");                         //M access type: Always fill in 0 here and make no modification in case of client access.
        // Acquirer Code (Applied if access type is acquirer access)
        data.put("acqInsCode", acqInsCode);//C
        // Merchant Category (Applied if access type is acquirer access)
        data.put("merCatCode", "");// C
        // Merchant Name (Applied if access type is acquirer access)
        data.put("merName", "");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        data.put("merAbbr", "");// C
        data.put("merId", merId);             //M client number: Please modify it to the client number you have applied or the test client number 777 you have registered at open.unionpay.com.

        data.put("orderId", DemoBase.getOrderId());       //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed. It is an order number regenerated, different from the one of the original purchase transaction.

        data.put("txnTime", DemoBase.getCurrentTime());   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.

        data.put("txnAmt", txnAmt);                       //M [deal amount]: The amount should be 0-115% of the pre-authorized amount.

        data.put("currencyCode", "156");                  //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        //data.put("reqReserved", "Transparent transmit information");                 //It is a domain reserved by the requester and you can enable it if you want to use it. Transparent transmit field (can be used to track client's custom parameters): you can use this field to query the background notifications and transaction state of this transaction, and such information will be returned as it was in the reconciliation file. The client can upload such information as required and the length of the information to be uploaded should be in range of 1-1024 bytes. The occurrence of symbols like &, =, {}, and [] may cause failure to parsing response messages from the query interface. Therefore, you are recommended to transmit only alphanumeric characters and use | for separation. Or, you can conduct a base64 encoding at the outermost layer (a “=” appears after a base64 encoding can be neglected because it will not cause parse failure).
        data.put("backUrl", SDKConfig.getConfig().getBackUrl());            //M background notification address. For details about background notification parameters, refer to Help Center > Download > Product Interface Specifications > Interface Specifications for Gateway Payment Products > Purchase Cancellation Transactions > Client Notification at open.unionpay.com. The other descriptions are the same as client notifications of purchase transactions.


        /***To debug a transaction so that it succeeds, you must modify the fields below.***/
        data.put("origQryId", origQryId);   			  //M [Original transaction serial number]: queryId returned by the original purchase transaction, which can be obtained from the background notification interface or transaction state query interface for purchase transaction.

        /**All request parameters have been set successfully. Now, let's sign the requested parameters, and send http.post requests and receive synchronous response messages.**/

        Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.
        String url = SDKConfig.getConfig().getBackRequestUrl();//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.

        Map<String,String> rspData = AcpService.post(reqData,url,DemoBase.encoding);//Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.

        System.out.println("preauth completion request---"+reqData);
        System.out.println("preauth completion response---"+rspData);
        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                System.out.println("Signature authentication succeeds");
                String respCode = rspData.get("respCode");
                System.out.println("respCode---"+respCode);
                if("00".equals(respCode)){
                    //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to wait for the background notification to determine whether the transaction is successful, or actively initiate a transaction query to determine the transaction state.
                    //TODO
                }else if("03".equals(respCode) ||
                        "04".equals(respCode) ||
                        "05".equals(respCode)){
                    //Also, you can initiate a transaction state query later to determine the transaction state.
                    //TODO
                }else{
                    //Other response codes are failure. Please find the cause.
                    //TODO
                }
            }else{
                System.out.println("Signature authentication fails");
                //TODO Find the reason why the signature authentication fails
            }
        }else{
            //The returned http state code is incorrect.
            System.out.println("No returned message is obtained or the returned http state code is not 200.");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
    }

    public static void preAuthCompletionCancel()
    {
        String merId = "389908262345678";//first 8 digits, IIN and last 7 any
        String origQryId = "381907041803188663758";
        String txnAmt = "100";
        String acqInsCode = "38990826";

        SDKConfig.getConfig().loadPropertiesFromPath("D:/tomcat836/lib/com/directi/pg");
        Map<String, String> data = new HashMap<String, String>();

        /***For an all-channel UnionPay system, all parameters can be left unchanged except the one encoding, which you need to set as required.***/
        data.put("version", DemoBase.version);            //M
        data.put("encoding", DemoBase.encoding);          //M
        data.put("signMethod", SDKConfig.getConfig().getSignMethod()); //M
        data.put("txnType", "33");                        //M
        data.put("txnSubType", "00");                     //M
        //000301: Merchant-hosted
        //000000: ExpressPay
        //000902: Token payment
        //001001: MOTO
        data.put("bizType", "000301");                    //M
        //07: Internet
        //08: Mobile
        data.put("channelType", "07");           			  //M
        //0: Merchant direct access
        //1: Acquirer access
        //2: Platform merchant access
        data.put("accessType", "1");                         //M access type: Always fill in 0 here and make no modification in case of client access.
        // Acquirer Code (Applied if access type is acquirer access)
        data.put("acqInsCode", acqInsCode);//C
        // Merchant Category (Applied if access type is acquirer access)
        data.put("merCatCode", "");// C
        // Merchant Name (Applied if access type is acquirer access)
        data.put("merName", "");// C
        // Merchant Abbreviation (Applied if access type is acquirer access)
        data.put("merAbbr", "");// C
        //
        data.put("merId", merId);             //M client number: Please modify it to the client number you have applied or the test client number 777 you have registered at open.unionpay.com.

        data.put("orderId", DemoBase.getOrderId());       //M client order number, consisting of 8-40 alphanumeric characters, no “-” or “_” is allowed, but custom rules are allowed. It is an order number regenerated, different from the one of the original purchase transaction.
        data.put("txnTime", DemoBase.getCurrentTime());   //M order delivery time: It must be in format of YYYYMMDDhhmmss. Be sure to use the current time. Otherwise, an error of invalid txnTime will be reported.
        data.put("txnAmt", txnAmt);                       //M [deal amount]: The amount should be 0-115% of the pre-authorized amount.
        data.put("currencyCode", "156");                  //M transaction currency (for domestic clients, it is usually 156, which indicates RMB)
        //data.put("reqReserved", "Transparent transmit information");                 //It is a domain reserved by the requester and you can enable it if you want to use it. It is a transparent transmit field (can be used to track client's custom parameters) and you can use it to query the background notifications and transaction state of this transaction, and such information will be returned as it was in the reconciliation file. The client can upload such information as required and the length of the information to be uploaded should be in range of 1-1024 bytes. The occurrence of symbols like &, =, {}, and [] may cause failure to parsing response messages from the query interface. Therefore, you are recommended to transmit only alphanumeric characters and use | for separation. Or, you can conduct a base64 encoding at the outermost layer (a “=” appears after a base64 encoding can be neglected because it will not cause parse failure).
        data.put("backUrl", SDKConfig.getConfig().getBackUrl());            //M background notification address. For details about background notification parameters, refer to Help Center > Download > Product Interface Specifications > Interface Specifications for Gateway Payment Products > Purchase Cancellation Transactions > Client Notification at open.unionpay.com. The other descriptions are the same as client notifications of purchase transactions.


        /***To debug a transaction so that it runs properly, you must modify the fields below.***/
        data.put("origQryId", origQryId);   			  //M [Original transaction serial number]: queryId returned by the original purchase transaction, which can be obtained from the background notification interface or transaction state query interface for purchase transaction.


        /**All request parameters have been set successfully. Now, let's sign the requested parameters, and send http.post requests and receive synchronous response messages.**/

        Map<String, String> reqData  = AcpService.sign(data,DemoBase.encoding);//In a message, the values of certId and signature are obtained from the signData method and are assigned with values automatically. Therefore, you just need to ensure that the certificate is correctly configured.

        String url = SDKConfig.getConfig().getBackRequestUrl();//At the url of the transaction request, you can read the acpsdk.backTransUrl in the corresponding property file acp_sdk.properties from the configuration file.
        Map<String,String> rspData =  AcpService.post(reqData,url,DemoBase.encoding);//Send request messages and receive synchronous responses (the default connection timeout is 30s and the timeout for reading the returned result is 30s); Here, after calling signData, do not make any modification to the value of any key in submitFromData before calling submitUrl. Any such modification may cause failure to signature authentication.

        System.out.println("preauth completion cancel request---"+reqData);
        System.out.println("preauth completion cancel response---"+rspData);
        /**To proceed the response codes, you need to compile a program based on your business logics. The logics for response code processing below are for reference only.------------->**/
        //For response code specifications, refer to Part 5 “Appendix” in Specifications for Platform Access Interfaces- by selecting Help Center > Download > Product Interface Specifications at open.unionpay.com.
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, DemoBase.encoding)){
                System.out.println("Signature authentication succeeds");
                String respCode = rspData.get("respCode");
                System.out.println("respCode---"+respCode);
                if("00".equals(respCode)){
                    //Transaction accepted (this does not mean that the transaction is successful). When the transaction is in this state, you can choose to wait for the background notification to determine whether the transaction is successful, or actively initiate a transaction query to determine the transaction state.
                    //TODO
                }else if("03".equals(respCode) ||
                        "04".equals(respCode) ||
                        "05".equals(respCode)){
                    //Also, you can initiate a transaction state query later to determine the transaction state.
                    //TODO
                }else{
                    //Other response codes are failure. Please find the cause.
                    //TODO
                }
            }else{
                System.out.println("Signature authentication fails");
                //TODO Find the reason why the signature authentication fails
            }
        }else{
            //The returned http state code is incorrect.
            System.out.println("No returned message is obtained or the returned http state code is not 200.");
        }
        String reqMessage = DemoBase.genHtmlResult(reqData);
        String rspMessage = DemoBase.genHtmlResult(rspData);
    }


    public static void main(String[] args)
    {
        //sendSMS();
        //easyEnrollment();
        purchase();
        //purchaseCancel();
        //refund();
        //preAuth();
        //preAuthCancel();
        //preAuthCapture();
        //preAuthCompletionCancel();
    }
}
