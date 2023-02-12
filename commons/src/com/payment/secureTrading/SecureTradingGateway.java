        package com.payment.secureTrading;

        import com.directi.pg.*;
        import com.directi.pg.core.GatewayAccount;
        import com.directi.pg.core.GatewayAccountService;
        import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
        import com.directi.pg.core.valueObjects.*;
        import com.manager.vo.RecurringBillingVO;
        import com.payment.Enum.PZProcessType;
        import com.payment.common.core.*;
        import com.payment.exceptionHandler.*;
        import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
        import org.apache.commons.lang3.StringEscapeUtils;
        import java.io.*;
        import java.util.Map;
        import java.util.ResourceBundle;

/**
 * Created by Jitendra on 19-Nov-18.
 */

public class SecureTradingGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "SecureTrad";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.SecureTrading");
    private static TransactionLogger transactionLogger = new TransactionLogger(SecureTradingGateway.class.getName());
    private static Logger log = new Logger(SecureTradingGateway.class.getName());

    public SecureTradingGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Entering into processSale:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        String Amount=SecureTradingUtils.getAmount(transDetailsVO.getAmount());
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        String reject3DCard=commRequestVO.getReject3DCard();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String member_id = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String isFxMID=gatewayAccount.getForexMid();
        String accounttypedescription="";
        if(functions.isValueNull(isFxMID) && "Y".equalsIgnoreCase(isFxMID))
            accounttypedescription="MOTO";
        else
            accounttypedescription="ECOM";
        String isDynamicDescriptor = gatewayAccount.getIsDynamicDescriptor();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String cardType=SecureTradingUtils.getCardType(cardDetailsVO.getCardType());
        RecurringBillingVO recurringBillingVO =commRequestVO.getRecurringBillingVO();
        String recurringType = commRequestVO.getRecurringBillingVO().getRecurringType();

        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else{
        termUrl = RB.getString("Term_url");
        transactionLogger.error("from RB----" + termUrl);
        }

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

        String ip="";
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
            ip=addressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=addressDetailsVO.getIp();
        }

        String country="";
        String zipCode="";
        String street="";
        String city="";
        String phone="";
        if (functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry();
        else
            country="";

        if (functions.isValueNull(addressDetailsVO.getZipCode()))
            zipCode=addressDetailsVO.getZipCode();
        else
            zipCode="";

        if (functions.isValueNull(addressDetailsVO.getStreet()))
            street=addressDetailsVO.getStreet();
        else
            street="";

        if (functions.isValueNull(addressDetailsVO.getCity()))
            city=addressDetailsVO.getCity();
        else
            city="";

        if (functions.isValueNull(addressDetailsVO.getPhone()))
            phone=addressDetailsVO.getPhone();
        else
            phone="";
     /*   if("INITIAL".equals(recurringType)||"Manual".equalsIgnoreCase(recurringType))
        {}*/
        String sitename=commMerchantVO.getSitename();
        String merchantPhone="";
        if(functions.isValueNull(sitename) && sitename.contains("https://"))
            sitename=sitename.replaceAll("https://","");
        else if(functions.isValueNull(sitename) && sitename.contains("http://"))
            sitename=sitename.replaceAll("http://","");

        if(functions.isValueNull(sitename) && sitename.contains("www."))
            sitename=sitename.replaceAll("www.","");
        transactionLogger.error("commMerchantVO.getMerchantSupportNumber()--->"+commMerchantVO.getMerchantSupportNumber());
        if(functions.isValueNull(commMerchantVO.getMerchantSupportNumber()))
            merchantPhone=commMerchantVO.getMerchantSupportNumber();
        String chargeDescription="";
        if(functions.isValueNull(sitename) && functions.isValueNull(merchantPhone))
        {
            chargeDescription = sitename + "" + merchantPhone;
            if(chargeDescription.length()>25)
                chargeDescription=sitename;
        }
        else
            chargeDescription=sitename;

        try
        {
            //CommResponseVO currencyResponseVO= (CommResponseVO) processDynamicCurrencyConversion(trackingID,requestVO);
            transactionLogger.error("inside try process sale");
            if (("Y".equals(is3dSupported) || "O".equals(is3dSupported))&& "ECOM".equalsIgnoreCase(accounttypedescription))
            {
                StringBuffer threeDEnrollmentReq = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                threeDEnrollmentReq.append("<requestblock version=\"3.67\">" +
                        "<alias>"+userName+"</alias>" +
                        "<request type=\"THREEDQUERY\">" +
                        " <operation>" +
                        " <authmethod>FINAL</authmethod>" +
                        " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                        " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                        " </operation>" +
                        " <merchant>" +
                        " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                    threeDEnrollmentReq.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                threeDEnrollmentReq.append(" <termurl>" + StringEscapeUtils.escapeXml(termUrl+trackingID) + "</termurl>" +
                        " </merchant>" +
                        " <settlement>" +
                        " <settlestatus>"+StringEscapeUtils.escapeXml("1")+"</settlestatus>" +
                        " </settlement>" +
                        " <customer>" +
                        " <accept>" + StringEscapeUtils.escapeXml("text/xml") + "</accept>" +
                        " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " </customer>" +
                        " <billing>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " <amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <payment type=\""+cardType+"\">" +
                        " <pan>" + StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum()) + "</pan>" +
                        " <expirydate>" + StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()+"/"+cardDetailsVO.getExpYear()) + "</expirydate>" +
                        " <securitycode>" + StringEscapeUtils.escapeXml(cardDetailsVO.getcVV()) + "</securitycode>" +
                        " </payment>" +
                        " </billing>" +
                        " </request>" +
                        "</requestblock>");

StringBuffer threeDEnrollmentReqlog = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                threeDEnrollmentReqlog.append("<requestblock version=\"3.67\">" +
                        "<alias>"+userName+"</alias>" +
                        "<request type=\"THREEDQUERY\">" +
                        " <operation>" +
                        " <authmethod>FINAL</authmethod>" +
                        " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                        " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                        " </operation>" +
                        " <merchant>" +
                        " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                    threeDEnrollmentReqlog.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                threeDEnrollmentReqlog.append(" <termurl>" + StringEscapeUtils.escapeXml(termUrl+trackingID) + "</termurl>" +
                        " </merchant>" +
                        " <settlement>" +
                        " <settlestatus>"+StringEscapeUtils.escapeXml("1")+"</settlestatus>" +
                        " </settlement>" +
                        " <customer>" +
                        " <accept>" + StringEscapeUtils.escapeXml("text/xml") + "</accept>" +
                        " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " </customer>" +
                        " <billing>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " <amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <payment type=\""+cardType+"\">" +
                        " <pan>" + functions.maskingPan(StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum())) + "</pan>" +
                        " <expirydate>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth())+"/"+functions.maskingNumber(cardDetailsVO.getExpYear())) + "</expirydate>" +
                        " <securitycode>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getcVV())) + "</securitycode>" +
                        " </payment>" +
                        " </billing>" +
                        " </request>" +
                        "</requestblock>");



                transactionLogger.error("-----3D enrollment request-----"+trackingID+"-->" + threeDEnrollmentReqlog);

                threeDEnrollmentReq = new StringBuffer(SecureTradingUtils.verifyXML(threeDEnrollmentReq.toString()));

                String threeDEnrollmentRes ="";
                if(isTest){
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    threeDEnrollmentRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), threeDEnrollmentReq.toString(), "BASIC", encodedCredentials);
                }else {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                    threeDEnrollmentRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), threeDEnrollmentReq.toString(), "BASIC", encodedCredentials);
                }

                transactionLogger.error("-----3D enrollment response-----"+trackingID+"-->" + threeDEnrollmentRes);

                if (functions.isValueNull(threeDEnrollmentRes))
                {
                    Map readResponse = SecureTradingUtils.readSoapResponse(threeDEnrollmentRes);

                    String enrolled = (String) readResponse.get("enrolled");
                    String paymentId = (String) readResponse.get("transactionreference");
                    String statusCode = (String) readResponse.get("code");
                    String data = (String) readResponse.get("data");
                    String message = (String) readResponse.get("message");
                    String timestamp = (String) readResponse.get("timestamp");
                    String settlestatus = (String) readResponse.get("settlestatus");

                    if ("Y".equalsIgnoreCase(enrolled) && "0".equals(statusCode))
                    {
                        String acs_url = (String) readResponse.get("acsurl");
                        String pareq = (String) readResponse.get("pareq");
                        String md = (String) readResponse.get("md");
                        if ("Y".equalsIgnoreCase(reject3DCard))
                        {
                            transactionLogger.error("rejecting 3d card as per configuration");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setDescription("SYS:3D Enrolled Card");
                            commResponseVO.setRemark("3D Enrolled Card");
                            return commResponseVO;
                        }
                        else
                        {
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(acs_url);
                            commResponseVO.setPaReq(pareq);
                            commResponseVO.setMd(md);
                            commResponseVO.setTerURL(termUrl + trackingID);
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setRemark(message);
                            commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            commResponseVO.setResponseTime(timestamp);
                            commResponseVO.setDescription("SYS:3D Authentication Pending");
                        }
                    }
                    else if ("N".equals(enrolled) || "U".equals(enrolled))
                    {
                        transactionLogger.debug("Inside 3D Not Enrolled");
                        if ("O".equals(is3dSupported))
                        {
                            transactionLogger.error("rejecting 3d card as per configuration");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setDescription("SYS:Only 3D card Supported");
                            commResponseVO.setRemark("Only 3D card Supported");
                            return commResponseVO;
                        }
                        else
                        {
                            String threeDNotEnrolledReq="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                                    "<requestblock version=\"3.67\">" +
                                    "<alias>"+userName+"</alias>" +
                                    "<request type=\"AUTH\">" +
                                    " <operation>" +
                                    " <parenttransactionreference>"+paymentId+"</parenttransactionreference>" +
                                    " <sitereference>"+member_id+"</sitereference>" +
                                    " </operation>" +
                                    "</request>" +
                                    "</requestblock>";
                            transactionLogger.error("-----3D NotEnrolled request-----"+trackingID+"-->" + threeDNotEnrolledReq);

                            threeDNotEnrolledReq = SecureTradingUtils.verifyXML(threeDNotEnrolledReq);

                            String threeDNotEnrolledRes ="";
                            if(isTest){
                                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                                threeDNotEnrolledRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), threeDNotEnrolledReq, "BASIC", encodedCredentials);
                            }else {
                                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                                threeDNotEnrolledRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), threeDNotEnrolledReq, "BASIC", encodedCredentials);
                            }
                            transactionLogger.error("-----3D NotEnrolled response-----"+trackingID+"-->" + threeDNotEnrolledRes);
                            if (functions.isValueNull(threeDNotEnrolledRes))
                            {
                                readResponse = SecureTradingUtils.readSoapResponse(threeDNotEnrolledRes);

                                if (readResponse != null && !readResponse.equals(""))
                                {
                                    settlestatus= (String) readResponse.get("settlestatus");
                                    paymentId=(String) readResponse.get("transactionreference");
                                    message=(String)readResponse.get("message");
                                    timestamp=(String) readResponse.get("timestamp");
                                    String code= (String) readResponse.get("code");
                                    String newData= (String) readResponse.get("data");
                                    String chargedescriptionRes= (String) readResponse.get("chargedescription");
                                    String rrn= (String) readResponse.get("retrievalreferencenumber");
                                    if (("0".equals(settlestatus) || "1".equals(settlestatus) || "100".equals(settlestatus) || "10".equals(settlestatus)) && "0".equals(code))
                                    {
                                        commResponseVO.setStatus("success");
                                        if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                                            commResponseVO.setDescriptor(chargedescriptionRes);
                                        else
                                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                        commResponseVO.setDescription("SYS:Transaction Successful");
                                        commResponseVO.setRemark(message);
                                    }
                                    else
                                    {
                                        commResponseVO.setStatus("fail");
                                        commResponseVO.setDescription(message);
                                        commResponseVO.setRemark(message+":"+newData+":ErrorCode-"+code);
                                    }
                                    commResponseVO.setBankTransactionDate(timestamp);
                                    commResponseVO.setTransactionId(paymentId);
                                    commResponseVO.setRrn(rrn);
                                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                                }
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setThreeDVersion("3Dv1");
                        commResponseVO.setStatus("Fail");
                        commResponseVO.setDescription(message+":"+data+":ErrorCode-"+statusCode);
                        commResponseVO.setRemark(message+":"+data+":ErrorCode-"+statusCode);
                        commResponseVO.setBankTransactionDate(timestamp);
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }
            }
            else
            {
                StringBuffer saleRequest =new StringBuffer();
                saleRequest.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<requestblock version=\"3.67\">" +
                                "<alias>"+userName+"</alias>" +
                                "<request type=\"AUTH\">" +
                                " <operation>" +
                                " <authmethod>FINAL</authmethod>" +
                                " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                                " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                                " <credentialsonfile>" + StringEscapeUtils.escapeXml("1") + "</credentialsonfile>" +
                                " </operation>" +
                                " <merchant>" +
                                " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                               saleRequest.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                saleRequest.append(" </merchant>" +
                                " <customer>" +
                                " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <name>" +
                                " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                " </customer>" +
                                " <settlement>" +
                                " <settlestatus>"+StringEscapeUtils.escapeXml("1")+"</settlestatus>" +
                                " </settlement>" +
                                " <billing>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">"+ Amount+"</amount>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                                " <name>" +
                                "<first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                "<last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <payment type=\""+StringEscapeUtils.escapeXml(cardType)+"\">" +
                                "<pan>" + StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expirydate>" + StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()+"/"+cardDetailsVO.getExpYear()) + "</expirydate>" +
                                "<securitycode>" + StringEscapeUtils.escapeXml(cardDetailsVO.getcVV()) + "</securitycode>" +
                                " </payment>" +
                                " </billing>" +
                                " </request>" +
                                "</requestblock>");
 StringBuffer saleRequestlog =new StringBuffer();
            saleRequestlog.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<requestblock version=\"3.67\">" +
                                "<alias>"+userName+"</alias>" +
                                "<request type=\"AUTH\">" +
                                " <operation>" +
                                " <authmethod>FINAL</authmethod>" +
                                " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                                " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                                " <credentialsonfile>" + StringEscapeUtils.escapeXml("1") + "</credentialsonfile>" +
                                " </operation>" +
                                " <merchant>" +
                                " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
            if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                saleRequestlog.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
            saleRequestlog.append(" </merchant>" +
                                " <customer>" +
                                " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <name>" +
                                " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                " </customer>" +
                                " <settlement>" +
                                " <settlestatus>"+StringEscapeUtils.escapeXml("1")+"</settlestatus>" +
                                " </settlement>" +
                                " <billing>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">"+ Amount+"</amount>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                                " <name>" +
                                "<first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                "<last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <payment type=\""+StringEscapeUtils.escapeXml(cardType)+"\">" +
                                " <pan>" + functions.maskingPan(StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum())) + "</pan>" +
                                " <expirydate>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()) + "/" + functions.maskingNumber(cardDetailsVO.getExpYear())) + "</expirydate>" +
                                " <securitycode>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getcVV())) + "</securitycode>" +
                                " </payment>" +
                                " </billing>" +
                                " </request>" +
                                "</requestblock>");

                transactionLogger.error("-----sale request-----"+trackingID+"-->" + saleRequestlog);

                String status = "";
                saleRequest = new StringBuffer(SecureTradingUtils.verifyXML(saleRequest.toString()));

                String saleResponse ="";
                if(isTest){
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    saleResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), saleRequest.toString(), "BASIC", encodedCredentials);
                }else {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                    saleResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), saleRequest.toString(), "BASIC", encodedCredentials);
                }
                transactionLogger.error("-----sale response-----"+trackingID+"-->" + saleResponse);
                Map readResponse = SecureTradingUtils.readSoapResponse(saleResponse);

                if (readResponse != null && !readResponse.equals(""))
                {
                    String settlestatus= (String) readResponse.get("settlestatus");
                    String code= (String) readResponse.get("code");
                    String data= (String) readResponse.get("data");
                    String message=(String)readResponse.get("message");
                    String transactionreference=(String) readResponse.get("transactionreference");
                    String amount= (String) readResponse.get("amount");
                    String timestamp= (String) readResponse.get("timestamp");
                    String chargedescriptionRes= (String) readResponse.get("chargedescription");
                    String rrn= (String) readResponse.get("retrievalreferencenumber");
                    if (("0".equals(settlestatus) || "1".equals(settlestatus) || "100".equals(settlestatus) || "10".equals(settlestatus)) && "0".equals(code)) {
                        status = "success";
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                        if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                            commResponseVO.setDescriptor(chargedescriptionRes);
                        else
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                    else {
                        status="fail";
                        commResponseVO.setDescription(message+":"+data+":ErrorCode-"+code);
                        commResponseVO.setRemark(message + ":" + data + ":ErrorCode-"+code);
                    }
                    commResponseVO.setStatus(status);
                    commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                    commResponseVO.setTransactionId(transactionreference);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setResponseTime(timestamp);
                    commResponseVO.setRrn(rrn);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }

        catch(UnsupportedEncodingException e)
        {
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----"+trackingID+"--->", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Entering into processAuthentication:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        String Amount=SecureTradingUtils.getAmount(transDetailsVO.getAmount());
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        String reject3DCard=commRequestVO.getReject3DCard();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String member_id = gatewayAccount.getMerchantId();
        String isFxMID=gatewayAccount.getForexMid();
        String accounttypedescription="";
        if(functions.isValueNull(isFxMID) && "Y".equalsIgnoreCase(isFxMID))
            accounttypedescription="MOTO";
        else
            accounttypedescription="ECOM";
        String cardType=SecureTradingUtils.getCardType(cardDetailsVO.getCardType());
        boolean isTest = gatewayAccount.isTest();
        String isDynamicDescriptor = gatewayAccount.getIsDynamicDescriptor();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("Term_url");
            transactionLogger.error("from RB----" + termUrl);
        }

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        String ip="";
        if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
            ip=addressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=addressDetailsVO.getIp();
        }

        String country="";
        String zipCode="";
        String street="";
        String city="";
        String phone="";
        if (functions.isValueNull(addressDetailsVO.getCountry()))
            country=addressDetailsVO.getCountry();
        else
            country="";

        if (functions.isValueNull(addressDetailsVO.getZipCode()))
            zipCode=addressDetailsVO.getZipCode();
        else
            zipCode="";

        if (functions.isValueNull(addressDetailsVO.getStreet()))
            street=addressDetailsVO.getStreet();
        else
            street="";

        if (functions.isValueNull(addressDetailsVO.getCity()))
            city=addressDetailsVO.getCity();
        else
            city="";

        if (functions.isValueNull(addressDetailsVO.getPhone()))
            phone=addressDetailsVO.getPhone();
        else
            phone="";

        String sitename=commMerchantVO.getSitename();
        String merchantPhone =commMerchantVO.getMerchantSupportNumber();
        if(functions.isValueNull(sitename) && sitename.contains("https://"))
            sitename=sitename.replaceAll("https://","");
        else if(functions.isValueNull(sitename) && sitename.contains("http://"))
            sitename=sitename.replaceAll("http://","");

        if(functions.isValueNull(sitename) && sitename.contains("www."))
            sitename=sitename.replaceAll("www.","");
        String chargeDescription="";
        if(functions.isValueNull(sitename) && functions.isValueNull(merchantPhone))
        {
            chargeDescription = sitename + "" + merchantPhone;
            if(chargeDescription.length()>25)
                chargeDescription=sitename;
        }
        else
            chargeDescription=sitename;

        try
        {
            if (("Y".equals(is3dSupported) || "O".equals(is3dSupported))&& "ECOM".equalsIgnoreCase(accounttypedescription))
            {
                StringBuffer threeDEnrollmentReq = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" );
                threeDEnrollmentReq.append("<requestblock version=\"3.67\">" +
                        "<alias>"+userName+"</alias>" +
                        "<request type=\"THREEDQUERY\">" +
                        " <operation>" +
                        " <authmethod>FINAL</authmethod>" +
                        " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                        " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                        " </operation>" +
                        " <merchant>" +
                        " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                    threeDEnrollmentReq.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                threeDEnrollmentReq.append(" <termurl>" + StringEscapeUtils.escapeXml(termUrl+trackingID) + "</termurl>" +
                        " </merchant>" +
                        " <settlement>" +
                        " <settlestatus>"+StringEscapeUtils.escapeXml("2")+"</settlestatus>" +
                        " </settlement>" +
                        " <customer>" +
                        " <accept>" + StringEscapeUtils.escapeXml("text/xml") + "</accept>" +
                        " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " </customer>" +
                        " <billing>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " <amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <payment type=\""+cardType+"\">" +
                        " <pan>" + StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum()) + "</pan>" +
                        " <expirydate>" + StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()+"/"+cardDetailsVO.getExpYear()) + "</expirydate>" +
                        " <securitycode>" + StringEscapeUtils.escapeXml(cardDetailsVO.getcVV()) + "</securitycode>" +
                        " </payment>" +
                        " </billing>" +
                        " </request>" +
                        "</requestblock>");

 StringBuffer threeDEnrollmentReqlog = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                threeDEnrollmentReqlog.append("<requestblock version=\"3.67\">" +
                        "<alias>"+userName+"</alias>" +
                        "<request type=\"THREEDQUERY\">" +
                        " <operation>" +
                        " <authmethod>FINAL</authmethod>" +
                        " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                        " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                        " </operation>" +

                        " <merchant>" +
                        " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                    threeDEnrollmentReqlog.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                threeDEnrollmentReqlog.append(" <termurl>" + StringEscapeUtils.escapeXml(termUrl+trackingID) + "</termurl>" +
                        " </merchant>" +
                        " <settlement>" +
                        " <settlestatus>"+StringEscapeUtils.escapeXml("2")+"</settlestatus>" +
                        " </settlement>" +
                        " <customer>" +
                        " <accept>" + StringEscapeUtils.escapeXml("text/xml") + "</accept>" +
                        " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " </customer>" +
                        " <billing>" +
                        " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                        " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                        " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                        " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                        " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                        " <amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                        " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                        " <name>" +
                        " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                        " <last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                        " </name>" +
                         " <payment type=\""+cardType+"\">" +
                         " <pan>" + functions.maskingPan(StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum())) + "</pan>" +
                         " <expirydate>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()) + "/" + functions.maskingNumber(cardDetailsVO.getExpYear())) + "</expirydate>" +
                         " <securitycode>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getcVV())) + "</securitycode>" +


                         " </payment>" +
                        " </billing>" +
                        " </request>" +
                        "</requestblock>");

                transactionLogger.error("-----3D enrollment request-----"+trackingID+"-->" + threeDEnrollmentReqlog);

                threeDEnrollmentReq = new StringBuffer(SecureTradingUtils.verifyXML(threeDEnrollmentReq.toString()));

                String threeDEnrollmentRes ="";
                if(isTest){
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    threeDEnrollmentRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), threeDEnrollmentReq.toString(), "BASIC", encodedCredentials);
                }else {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                    threeDEnrollmentRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), threeDEnrollmentReq.toString(), "BASIC", encodedCredentials);
                }

                transactionLogger.error("-----3D enrollment response-----"+trackingID+"-->" + threeDEnrollmentRes);

                if (functions.isValueNull(threeDEnrollmentRes))
                {
                    Map readResponse = SecureTradingUtils.readSoapResponse(threeDEnrollmentRes);
                    if (readResponse != null && !readResponse.equals(""))
                    {
                        String enrolled = (String) readResponse.get("enrolled");
                        String paymentId = (String) readResponse.get("transactionreference");
                        String statusCode = (String) readResponse.get("code");
                        String message = (String) readResponse.get("message");
                        String data = (String) readResponse.get("data");
                        String timestamp = (String) readResponse.get("timestamp");

                        if ("Y".equalsIgnoreCase(enrolled) && "0".equals(statusCode))
                        {
                            String acs_url = (String) readResponse.get("acsurl");
                            String pareq = (String) readResponse.get("pareq");
                            String md = (String) readResponse.get("md");
                            if ("Y".equalsIgnoreCase(reject3DCard))
                            {
                                transactionLogger.error("rejecting 3d card as per configuration");
                                commResponseVO.setStatus("failed");
                                commResponseVO.setTransactionId(paymentId);
                                commResponseVO.setDescription("SYS:3D Enrolled Card");
                                commResponseVO.setRemark("3D Enrolled Card");
                                return commResponseVO;
                            }
                            else
                            {
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setUrlFor3DRedirect(acs_url);
                                commResponseVO.setPaReq(pareq);
                                commResponseVO.setMd(md);
                                commResponseVO.setTerURL(termUrl + trackingID);
                                commResponseVO.setTransactionId(paymentId);
                                commResponseVO.setRemark(message);
                                commResponseVO.setDescription("SYS:3D Authentication Pending");
                                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                            }
                        }
                        else if ("N".equals(enrolled) || "U".equals(enrolled))
                        {
                            if ("O".equals(is3dSupported))
                            {
                                transactionLogger.error("rejecting 3d card as per configuration");
                                commResponseVO.setStatus("failed");
                                commResponseVO.setTransactionId(paymentId);
                                commResponseVO.setDescription("SYS:Only 3D card Supported");
                                commResponseVO.setRemark("Only 3D card Supported");
                                return commResponseVO;
                            }
                            else
                            {
                                String threeDNotEnrolledReq = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                                        "<requestblock version=\"3.67\">" +
                                        "<alias>"+userName+"</alias>" +
                                        "<request type=\"AUTH\">" +
                                        " <operation>" +
                                        " <parenttransactionreference>" + paymentId + "</parenttransactionreference>" +
                                        " <sitereference>" + member_id + "</sitereference>" +
                                        " </operation>" +
                                        "</request>" +
                                        "</requestblock>";
                                transactionLogger.error("-----3DNotEnrolled request-----"+trackingID+"-->" + threeDNotEnrolledReq);

                                threeDNotEnrolledReq = SecureTradingUtils.verifyXML(threeDNotEnrolledReq);

                                String threeDNotEnrolledRes = "";
                                if (isTest)
                                {
                                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                                    threeDNotEnrolledRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), threeDNotEnrolledReq, "BASIC", encodedCredentials);
                                }
                                else
                                {
                                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                                    threeDNotEnrolledRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), threeDNotEnrolledReq, "BASIC", encodedCredentials);
                                }
                                transactionLogger.error("-----3DNotEnrolled response-----"+trackingID+"-->" + threeDNotEnrolledRes);
                                if (functions.isValueNull(threeDNotEnrolledRes))
                                {
                                    readResponse = SecureTradingUtils.readSoapResponse(threeDNotEnrolledRes);
                                    if (readResponse != null && !readResponse.equals(""))
                                    {
                                        String settlestatus = (String) readResponse.get("settlestatus");
                                        String chargedescriptionRes = (String) readResponse.get("chargedescription");
                                        paymentId = (String) readResponse.get("transactionreference");
                                        message = (String) readResponse.get("message");
                                        String date = (String) readResponse.get("timestamp");
                                        String code = (String) readResponse.get("code");
                                        String newData = (String) readResponse.get("data");
                                        String rrn= (String) readResponse.get("retrievalreferencenumber");
                                        if ("2".equals(settlestatus) && "0".equals(code))
                                        {
                                            commResponseVO.setStatus("success");
                                            if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                                            commResponseVO.setDescriptor(chargedescriptionRes);
                                            else
                                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                            commResponseVO.setDescription("SYS:Transaction Successful");
                                            commResponseVO.setRemark(message);
                                        }
                                        else
                                        {
                                            commResponseVO.setStatus("fail");
                                            commResponseVO.setDescription(message);
                                            commResponseVO.setRemark(message + ":" + newData + ":ErrorCode-"+code);
                                        }
                                        commResponseVO.setBankTransactionDate(date);
                                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                        commResponseVO.setTransactionId(paymentId);
                                        commResponseVO.setRrn(rrn);
                                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                                    }
                                }
                            }

                        }
                        else
                        {
                            commResponseVO.setThreeDVersion("3Dv1");
                            commResponseVO.setStatus("Fail");
                            commResponseVO.setDescription(message+":"+data+":ErrorCode-"+statusCode);
                            commResponseVO.setRemark(message+":"+data+":ErrorCode-"+statusCode);
                            commResponseVO.setBankTransactionDate(timestamp);
                            commResponseVO.setTransactionId(paymentId);
                            commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                        }

                    }
                }
            }
            else
            {
                StringBuffer authRequest = new StringBuffer();
                authRequest.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<requestblock version=\"3.67\">" +
                                "<alias>"+userName+"</alias>" +
                                "<request type=\"AUTH\">" +
                                " <operation>" +
                                " <authmethod>FINAL</authmethod>" +
                                " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                                " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                                " <credentialsonfile>" + StringEscapeUtils.escapeXml("1") + "</credentialsonfile>" +
                                " </operation>" +
                                " <merchant>" +
                                " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                    authRequest.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                authRequest.append(" </merchant>" +
                                " <customer>" +
                                " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <name>" +
                                " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                " </customer>" +
                                " <settlement>" +
                                " <settlestatus>"+StringEscapeUtils.escapeXml("2")+"</settlestatus>" +
                                " </settlement>" +
                                " <billing>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(addressDetailsVO.getPhone()) + "</telephone>" +
                                " <name>" +
                                "<first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                "<last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <payment type=\""+cardType+"\">" +
                                "<pan>" + StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expirydate>" + StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()+"/"+cardDetailsVO.getExpYear()) + "</expirydate>" +
                                "<securitycode>" + StringEscapeUtils.escapeXml(cardDetailsVO.getcVV()) + "</securitycode>" +
                                " </payment>" +
                                " </billing>" +
                                " </request>" +
                                "</requestblock>");
StringBuffer authRequestlog = new StringBuffer();
                authRequestlog.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
                                "<requestblock version=\"3.67\">" +
                                "<alias>"+userName+"</alias>" +
                                "<request type=\"AUTH\">" +
                                " <operation>" +
                                " <authmethod>FINAL</authmethod>" +
                                " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                                " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                                " <credentialsonfile>" + StringEscapeUtils.escapeXml("1") + "</credentialsonfile>" +
                                " </operation>" +
                                " <merchant>" +
                                " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
                if(functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                    authRequestlog.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
                authRequestlog.append(" </merchant>" +
                                " <customer>" +
                                " <ip>" + StringEscapeUtils.escapeXml(ip) + "</ip>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <name>" +
                                " <first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                " <last>"  + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(phone) + "</telephone>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <country>" + StringEscapeUtils.escapeXml(country) + "</country>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                " </customer>" +
                                " <settlement>" +
                                " <settlestatus>"+StringEscapeUtils.escapeXml("2")+"</settlestatus>" +
                                " </settlement>" +
                                " <billing>" +
                                " <street>" + StringEscapeUtils.escapeXml(street) + "</street>" +
                                " <town>" + StringEscapeUtils.escapeXml(city) + "</town>" +
                                " <county>" + StringEscapeUtils.escapeXml(country) + "</county>" +
                                " <postcode>" + StringEscapeUtils.escapeXml(zipCode) + "</postcode>" +
                                " <email>" + StringEscapeUtils.escapeXml(addressDetailsVO.getEmail()) + "</email>" +
                                "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                                " <telephone>" + StringEscapeUtils.escapeXml(addressDetailsVO.getPhone()) + "</telephone>" +
                                " <name>" +
                                "<first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                                "<last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                                " </name>" +
                                " <payment type=\""+cardType+"\">" +
                                " <pan>" + functions.maskingPan(StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum())) + "</pan>" +
                                " <expirydate>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth())+"/"+functions.maskingNumber(cardDetailsVO.getExpYear())) + "</expirydate>" +
                                " <securitycode>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getcVV())) + "</securitycode>" +
                                " </payment>" +
                                " </billing>" +
                                " </request>" +
                                "</requestblock>");

                transactionLogger.error("-----auth request-----"+trackingID+"-->" + authRequestlog);

                String status = "fail";
                authRequest = new StringBuffer(SecureTradingUtils.verifyXML(authRequest.toString()));
                String authResponse ="";
                if(isTest){
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    authResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), authRequest.toString(), "BASIC", encodedCredentials);
                }else {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                    authResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), authRequest.toString(), "BASIC", encodedCredentials);
                }
                transactionLogger.error("-----auth response-----"+trackingID+"-->" + authResponse);
                Map readResponse = SecureTradingUtils.readSoapResponse(authResponse.trim());

                if (readResponse != null && !readResponse.equals(""))
                {
                    String settlestatus= (String) readResponse.get("settlestatus");
                    String code= (String) readResponse.get("code");
                    String data= (String) readResponse.get("data");
                    String message= (String) readResponse.get("message");
                    String paymentId= (String) readResponse.get("transactionreference");
                    String amount= (String) readResponse.get("amount");
                    String chargedescriptionRes = (String) readResponse.get("chargedescription");
                    String rrn= (String) readResponse.get("retrievalreferencenumber");
                    if ("2".equals(settlestatus) && "0".equals(code))
                    {
                        status = "success";
                        if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        commResponseVO.setDescriptor(chargedescriptionRes);
                        else
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);
                    }
                    else
                    {
                        commResponseVO.setDescription(message+":"+data+":ErrorCode-"+code);
                        commResponseVO.setRemark(message+":"+data+":ErrorCode-"+code);
                    }
                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setRrn(rrn);
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }

        catch(UnsupportedEncodingException e)
        {
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----"+trackingID+"-->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error(":::::Enter into processRefund:::::");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String Amount=SecureTradingUtils.getAmount(commTransactionDetailsVO.getAmount());

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String isFxMID=gatewayAccount.getForexMid();
        String accounttypedescription="";
        if(functions.isValueNull(isFxMID) && "Y".equalsIgnoreCase(isFxMID))
            accounttypedescription="MOTO";
        else
            accounttypedescription="ECOM";

        String currency="";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String rCount = "";
        if (functions.isValueNull(commRequestVO.getCount()))
        {
            rCount = commRequestVO.getCount();
        }

        String parenttransactionreference=commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("previous Transaction ID in refund for Inquiry -----"+commRequestVO.getTransDetailsVO().getPreviousTransactionId());
        commResponseVO = (CommResponseVO) processInquiry(commRequestVO);

        try
        {
            if (commResponseVO.getStatus().equalsIgnoreCase("100"))
            {
                String refundRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<requestblock version=\"3.67\">" +
                        "<alias>"+userName+"</alias>" +
                        "<request type=\"REFUND\">" +
                        " <operation>" +
                        " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                        " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                        " <parenttransactionreference>" + StringEscapeUtils.escapeXml(parenttransactionreference) + "</parenttransactionreference>" +
                        " </operation>" +
                        " <billing>" +
                        " <amount currencycode=\"" + StringEscapeUtils.escapeXml(commTransactionDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                        " </billing>" +
                        " </request>" +
                        "</requestblock>";

                transactionLogger.error("-----refund request-----"+trackingID+"-->" + refundRequest);
                refundRequest = SecureTradingUtils.verifyXML(refundRequest);
                String refundResponse = "";
                if (isTest)
                {
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    refundResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), refundRequest, "BASIC", encodedCredentials);
                }
                else
                {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                    refundResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), refundRequest, "BASIC", encodedCredentials);
                }
                transactionLogger.error("-----refund response-----"+trackingID+"-->" + refundResponse);
                Map readResponse = SecureTradingUtils.readSoapResponse(refundResponse);
                String status = "fail";

                if (!readResponse.equals("") && readResponse != null)
                {
                    String settlestatus = (String) readResponse.get("settlestatus");
                    String code = (String) readResponse.get("code");
                    String message = (String) readResponse.get("message");
                    String authCode = (String) readResponse.get("authcode");
                    String transactionreference = (String) readResponse.get("transactionreference");
                    String amount = (String) readResponse.get("amount");
                    String timestamp = (String) readResponse.get("timestamp");
                    String rrn       = (String) readResponse.get("retrievalreferencenumber");

                    //transactionLogger.debug("-----amount in refund -----" + amount);
                    // transactionLogger.debug("-----settlestatus in refund -----" + settlestatus);
                    if (("0".equals(settlestatus) || "1".equals(settlestatus) || "100".equals(settlestatus) || "10".equals(settlestatus)) && "0".equals(code))
                    {
                        //  transactionLogger.debug("line 805 inside if");
                        status = "success";
                        commResponseVO.setDescription("SYS:Refund Successful");
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                    }
                    else
                    {
                        commResponseVO.setDescription("SYS:Refund Fail");
                    }
                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionId(transactionreference);
                    commResponseVO.setRemark(message);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setBankTransactionDate(timestamp);
                    commResponseVO.setRrn(rrn);
                }

                // transactionLogger.debug("line 822 commResponseVO.getStatus()"+commResponseVO.getStatus());
            }
            else if(commResponseVO.getStatus().equalsIgnoreCase("10"))
            {
                commResponseVO.setStatus("waitforreversal");
                commResponseVO.setRemark("Please try after 24 hours");
                commResponseVO.setDescription("Please try after 24 hours");
                return commResponseVO;
            }
            else if(commResponseVO.getStatus().equalsIgnoreCase("0") || commResponseVO.getStatus().equalsIgnoreCase("1"))
            {
                //transactionLogger.debug("line 826 commResponseVO.getStatus()"+commResponseVO.getStatus());
                // transactionLogger.debug("PreviousTransactionAmount----"+commTransactionDetailsVO.getPreviousTransactionAmount());
                // transactionLogger.debug("amount----------------"+commTransactionDetailsVO.getAmount());
                if(!commTransactionDetailsVO.getPreviousTransactionAmount().equalsIgnoreCase(commTransactionDetailsVO.getAmount()))
                {
                    commResponseVO.setStatus("waitforreversal");
                    commResponseVO.setRemark("Transaction cannot be Reversed , Please wait for 24 Hours");
                    commResponseVO.setDescription("Transaction cannot be Reversed , Please wait for 24 Hours");
                    return commResponseVO;
                }
                else
                {
                    //  transactionLogger.debug("inside last else----");
                    CommResponseVO commResponseVO1 =  (CommResponseVO)this.processVoid(trackingID, commRequestVO);

                    if(commResponseVO1.getStatus().equalsIgnoreCase("success"))
                    {
                        String status = "success";
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescription("SYS:Refund Successful");
                        commResponseVO.setRemark("SYS:Refund Successful");
                        commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                    }
                    else
                    {
                        transactionLogger.error("Cancelling failed during reversal "+commResponseVO1.getDescription());
                        commResponseVO.setStatus("waitforreversal");
                        commResponseVO.setRemark(commResponseVO1.getRemark());
                        commResponseVO.setDescription("Transaction cannot be Reversed , Please wait for 24 Hours");
                        return commResponseVO;
                    }
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch(UnsupportedEncodingException e)
        {
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("SecureTradingGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----"+trackingID+"--->",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processInquiry( GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error(":::::Entering into processQuery:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        try
        {
            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String queryReq =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                            "<requestblock version=\"3.67\">" +
                            "<alias>"+userName+"</alias>" +
                            "<request type=\"TRANSACTIONQUERY\">" +
                            " <filter>" +
                            " <sitereference>"+StringEscapeUtils.escapeXml(member_id)+"</sitereference>" +
                            " <transactionreference>"+StringEscapeUtils.escapeXml(commRequestVO.getTransDetailsVO().getPreviousTransactionId())+"</transactionreference>" +
                            " </filter>" +
                            " </request>" +
                            "</requestblock>";

            transactionLogger.error("-----query request-----"+commRequestVO.getTransDetailsVO().getOrderId()+"-->"+queryReq);
            queryReq = SecureTradingUtils.verifyXML(queryReq);
            String queryResponse ="";
            if(isTest){
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                queryResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), queryReq, "BASIC", encodedCredentials);
            }else {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                queryResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), queryReq, "BASIC", encodedCredentials);
            }
            transactionLogger.error("-----query response-----"+commRequestVO.getTransDetailsVO().getOrderId()+"-->" + queryResponse);
            Map readResponse=SecureTradingUtils.readSoapResponse(queryResponse);
            String status = "";
            if (!readResponse.equals("") && readResponse != null)
            {
                String settlestatus= (String) readResponse.get("settlestatus");
                String code= (String) readResponse.get("code");
                String message= (String) readResponse.get("message");
                String transactionreference= (String) readResponse.get("transactionreference");
                String authcode= (String) readResponse.get("authcode");
                String timestamp= (String) readResponse.get("timestamp");
                String amount= (String) readResponse.get("amount");

                commResponseVO.setRemark(message);
                commResponseVO.setAmount(amount);
                commResponseVO.setDescription(message);
                commResponseVO.setStatus(settlestatus);
                commResponseVO.setResponseTime(timestamp);
                commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                commResponseVO.setTransactionId(transactionreference);
                commResponseVO.setAuthCode(authcode);
                commResponseVO.setBankTransactionDate(timestamp);
                commResponseVO.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setTransactionStatus(message);

            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----"+commRequestVO.getTransDetailsVO().getOrderId()+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processVoid:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        try
        {
            String cancelReq =
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                            "<requestblock version=\"3.67\">" +
                            " <alias>"+userName+"</alias>" +
                            " <request type=\"TRANSACTIONUPDATE\">" +
                            " <filter>" +
                            " <sitereference>"+StringEscapeUtils.escapeXml(member_id)+"</sitereference>" +
                            " <transactionreference>"+StringEscapeUtils.escapeXml(commTransactionDetailsVO.getPreviousTransactionId())+"</transactionreference>" +
                            " </filter>" +
                            " <updates>" +
                            " <settlement>" +
                            " <settlestatus>3</settlestatus>" +
                            " </settlement>" +
                            " </updates>" +
                            " </request>" +
                            "</requestblock>";

            cancelReq = SecureTradingUtils.verifyXML(cancelReq);
            transactionLogger.error("-----cancel request-----"+trackingID+"-->"+cancelReq);
            String cancelResponse ="";
            if(isTest){
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                cancelResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), cancelReq, "BASIC", encodedCredentials);
            }else {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                cancelResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), cancelReq, "BASIC", encodedCredentials);
            }
            transactionLogger.error("-----cancel response-----"+trackingID+"-->" + cancelResponse);
            Map readResponse=SecureTradingUtils.readSoapResponse(cancelResponse);
            if(readResponse!=null && !readResponse.equals(""))
            {
                String message= (String) readResponse.get("message");
                String code= (String) readResponse.get("code");
                if ("0".equals(code))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                    commResponseVO.setDescription(message);
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----"+trackingID+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processCapture:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions=new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String isDynamicDescriptor = gatewayAccount.getIsDynamicDescriptor();
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency())) {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount())) {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency())) {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            String captureReq =
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                            "<requestblock version=\"3.67\">" +
                            " <alias>"+userName+"</alias>" +
                            " <request type=\"TRANSACTIONUPDATE\">" +
                            " <filter>" +
                            " <sitereference>"+StringEscapeUtils.escapeXml(member_id)+"</sitereference>" +
                            " <transactionreference>"+StringEscapeUtils.escapeXml(commTransactionDetailsVO.getPreviousTransactionId())+"</transactionreference>" +
                            " </filter>" +
                            " <updates>" +
                            " <settlement>" +
                            " <settlestatus>1</settlestatus>" +
                            " </settlement>" +
                            " </updates>" +
                            " </request>" +
                            "</requestblock>";

            captureReq = SecureTradingUtils.verifyXML(captureReq);
            transactionLogger.error("-----capture request-----"+trackingID+"-->"+captureReq);
            String captureRes ="";
            if(isTest){
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                captureRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), captureReq, "BASIC", encodedCredentials);
            }else {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                captureRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), captureReq, "BASIC", encodedCredentials);
            }
            transactionLogger.error("-----capture response-----"+trackingID+"-->" + captureRes);
            Map readResponse=SecureTradingUtils.readSoapResponse(captureRes);
            if(readResponse!=null && !readResponse.equals(""))
            {
                String message= (String) readResponse.get("message");
                String code= (String) readResponse.get("code");
                String chargedescriptionRes = (String) readResponse.get("chargedescription");
                if ("0".equals(code))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                    commResponseVO.setDescription(message);
                    commResponseVO.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
                    if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        commResponseVO.setDescriptor(chargedescriptionRes);
                    else
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----"+trackingID+"--->",e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into processPayout:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String parenttransactionreference=commTransactionDetailsVO.getPaymentId();
        // transactionLogger.debug("-----parenttransactionreference-----"+parenttransactionreference);
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency())) {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount())) {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency())) {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            String payoutReq =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                            "<requestblock version=\"3.67\">" +
                            "<alias>"+userName+"</alias>" +
                            "<request type=\"REFUND\">" +
                            " <operation>" +
                            " <sitereference>"+StringEscapeUtils.escapeXml(member_id)+"</sitereference>" +
                            " <accounttypedescription>"+StringEscapeUtils.escapeXml("CFT")+"</accounttypedescription>" +
                            " <parenttransactionreference>"+StringEscapeUtils.escapeXml(parenttransactionreference)+"</parenttransactionreference>"+
                            " </operation>" +
                            " </request>" +
                            "</requestblock>";

            payoutReq = SecureTradingUtils.verifyXML(payoutReq);
            transactionLogger.error("-----payout request-----"+trackingId+"-->"+payoutReq);
            String payoutRes ="";
            if(isTest){
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                payoutRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), payoutReq, "BASIC", encodedCredentials);
            }else {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                payoutRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), payoutReq, "BASIC", encodedCredentials);
            }
            transactionLogger.error("-----payout response-----"+trackingId+"-->" + payoutRes);

            Map readResponse=SecureTradingUtils.readSoapResponse(payoutRes);
            if(readResponse!=null && !readResponse.equals(""))
            {
                String transactionRef= (String) readResponse.get("transactionreference");
                String timeStamp= (String) readResponse.get("timestamp");
                String settleDuedate= (String) readResponse.get("settleduedate");
                String settleStatus= (String) readResponse.get("settlestatus");
                String parentTransRef= (String) readResponse.get("parenttransactionreference");
                String message= (String) readResponse.get("message");
                String code= (String) readResponse.get("code");

                String status="";
                if(("0".equals(settleStatus) || "1".equals(settleStatus)) && "0".equals(code))
                {
                    status="success";
                    commResponseVO.setDescription("SYS: Transaction Successful");

                }
                else
                {
                    status="fail";
                    commResponseVO.setDescription("SYS: Transaction Failed");
                }
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(message);
                commResponseVO.setTransactionId(transactionRef);
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----"+trackingId+"-->",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processCommon3DAuthConfirmation-----");

        Functions functions = new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String isDynamicDescriptor = gatewayAccount.getIsDynamicDescriptor();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String pares = commRequestVO.getPaRes();
        String md = commRequestVO.getMd();

        try
        {
            String finalAuthRequest ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><requestblock version=\"3.67\">" +
                    "<alias>"+userName+"</alias>" +
                    "<request type=\"AUTH\">" +
                    " <operation>" +
                    " <md>" + StringEscapeUtils.escapeXml(md) + "</md>" +
                    " <pares>" + StringEscapeUtils.escapeXml(pares) + "</pares>" +
                    " </operation>" +
                    " </request>" +
                    "</requestblock>";

            finalAuthRequest = SecureTradingUtils.verifyXML(finalAuthRequest);

            transactionLogger.error("finalAuthRequest-----"+trackingID+"-->" + finalAuthRequest);

            String finalAuthResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                finalAuthResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), finalAuthRequest, "BASIC", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                finalAuthResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), finalAuthRequest, "BASIC", encodedCredentials);
            }
            transactionLogger.error("finalAuthResponse-----"+trackingID+"-->" + finalAuthResponse);

            if (functions.isValueNull(finalAuthResponse))
            {
                Map readResponse = SecureTradingUtils.readSoapResponse(finalAuthResponse);

                String statusCode = (String) readResponse.get("status");
                String settlestatus = (String) readResponse.get("settlestatus");
                String eci = (String) readResponse.get("eci");
                String cavv = (String) readResponse.get("cavv");
                String code = (String) readResponse.get("code");
                String message = (String) readResponse.get("message");
                String paymentId = (String) readResponse.get("transactionreference");
                String date = (String) readResponse.get("timestamp");
                String chargedescriptionRes = (String) readResponse.get("chargedescription");
                String rrn= (String) readResponse.get("retrievalreferencenumber");

                if ("Y".equalsIgnoreCase(statusCode) || ("0".equals(code) && "2".equalsIgnoreCase(settlestatus)))
                {
                    comm3DResponseVO.setStatus("success");
                    if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        comm3DResponseVO.setDescriptor(chargedescriptionRes);
                    else
                        comm3DResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    if("3".equalsIgnoreCase(settlestatus))
                        message="Failed by bank";
                }
                comm3DResponseVO.setRemark(message);
                comm3DResponseVO.setDescription(message);
                comm3DResponseVO.setTransactionId(paymentId);
                comm3DResponseVO.setBankTransactionDate(date);
                comm3DResponseVO.setEci(eci);
                comm3DResponseVO.setRrn(rrn);
            }
        }catch(Exception e){
            transactionLogger.error("Exception-----"+trackingID+"--->", e);
        }
        return comm3DResponseVO;
    }


    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processCommon3DSaleConfirmation-----");

        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO=(Comm3DRequestVO)requestVO;
        Comm3DResponseVO comm3DResponseVO =  new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String isDynamicDescriptor=gatewayAccount.getIsDynamicDescriptor();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String pares = commRequestVO.getPaRes();
        String md = commRequestVO.getMd();

        try
        {
            String finalSaleRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><requestblock version=\"3.67\">" +
                    "<alias>"+userName+"</alias>" +
                    "<request type=\"AUTH\">" +
                    " <operation>" +
                    " <md>" + StringEscapeUtils.escapeXml(md) + "</md>" +
                    " <pares>" + StringEscapeUtils.escapeXml(pares) + "</pares>" +
                    " </operation>" +
                    " </request>" +
                    "</requestblock>";

            finalSaleRequest = SecureTradingUtils.verifyXML(finalSaleRequest);

            transactionLogger.error("finalSaleRequest-----"+trackingID+"-->"+finalSaleRequest);

            String finalSaleResponse="";
            if(isTest){
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                finalSaleResponse=SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), finalSaleRequest, "BASIC", encodedCredentials);
            }else {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                finalSaleResponse=SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), finalSaleRequest, "BASIC", encodedCredentials);
            }
            transactionLogger.error("finalSaleResponse-----"+trackingID+"-->"+finalSaleResponse);

            if(functions.isValueNull(finalSaleResponse)){
                Map readResponse = SecureTradingUtils.readSoapResponse(finalSaleResponse);

                String statusCode= (String) readResponse.get("status");
                String eci= (String) readResponse.get("eci");
                String cavv= (String) readResponse.get("cavv");
                String code= (String) readResponse.get("code");
                String message=(String ) readResponse.get("message");
                String settlestatus=(String ) readResponse.get("settlestatus");
                String paymentId=(String) readResponse.get("transactionreference");
                String date=(String) readResponse.get("timestamp");
                String acquirerresponsemessage=(String) readResponse.get("acquirerresponsemessage");
                String chargedescriptionRes = (String) readResponse.get("chargedescription");
                String rrn= (String) readResponse.get("retrievalreferencenumber");
                transactionLogger.error("chargedescriptionRes---->"+chargedescriptionRes);


                if (("0".equals(settlestatus) || "1".equals(settlestatus) || "100".equals(settlestatus) || "10".equals(settlestatus)) && "0".equals(code))
                {
                    comm3DResponseVO.setStatus("success");
                    if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        comm3DResponseVO.setDescriptor(chargedescriptionRes);
                    else
                        comm3DResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                }else {
                    comm3DResponseVO.setStatus("fail");
                    if("3".equalsIgnoreCase(settlestatus))
                        acquirerresponsemessage="Failed by bank";

                }
                if(functions.isValueNull(acquirerresponsemessage))
                {
                    comm3DResponseVO.setRemark(message + "-" + acquirerresponsemessage);
                    comm3DResponseVO.setDescription(message + "-" + acquirerresponsemessage);
                }
                else
                {
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                }

                comm3DResponseVO.setTransactionId(paymentId);
                comm3DResponseVO.setBankTransactionDate(date);
                comm3DResponseVO.setEci(eci);
                comm3DResponseVO.setRrn(rrn);
            }

        }catch (Exception e)
        {
            transactionLogger.error("Exception-----"+trackingID+"-->",e);
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
       {
           transactionLogger.error(":::::Entering into processRecurring:::::");
           CommRequestVO commRequestVO                          = (CommRequestVO) requestVO;
           Functions functions                                  = new Functions();
           CommResponseVO commResponseVO                        = new CommResponseVO();
           CommTransactionDetailsVO commTransactionDetailsVO    = commRequestVO.getTransDetailsVO();
           CommAddressDetailsVO commAddressDetailsVO            = commRequestVO.getAddressDetailsVO();
           String userName                  = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
           String password                  = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
           String userPassword              = userName + ":" + password;
           String encodedCredentials        = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
           String member_id                 = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
           GatewayAccount gatewayAccount    = GatewayAccountService.getGatewayAccount(accountId);
           CommTransactionDetailsVO transDetailsVO  = commRequestVO.getTransDetailsVO();
           boolean isTest                           = gatewayAccount.isTest();
           String previousTrackingId                = commTransactionDetailsVO.getPreviousTransactionId();
           String parenttransactionreference        = SecureTradingUtils.getParentTransactionReference(previousTrackingId);
           transactionLogger.debug("-----parenttransactionreference-----"+parenttransactionreference);
           String currency          = "";
           String tmpl_amount       = "";
           String tmpl_currency     = "";
           String baseamount        = transDetailsVO.getAmount();
           String number            = SecureTradingUtils.getsubscriptionnumber(trackingID,previousTrackingId,"RECURRING");

           if (functions.isValueNull(commTransactionDetailsVO.getCurrency())) {
               currency = commTransactionDetailsVO.getCurrency();
           }

           if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount())) {
               tmpl_amount = commAddressDetailsVO.getTmpl_amount();
           }

           if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency())) {
               tmpl_currency = commAddressDetailsVO.getTmpl_currency();
           }

           if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
               baseamount   = SecureTradingUtils.getJPYAmount(transDetailsVO.getAmount());
           else if("KWD".equalsIgnoreCase(transDetailsVO.getCurrency()))
               baseamount   = SecureTradingUtils.getKWDSupportedAmount(transDetailsVO.getAmount());
           else if("KRW".equalsIgnoreCase(transDetailsVO.getCurrency()))
               baseamount   = SecureTradingUtils.getKRWAmount(transDetailsVO.getAmount());
           else
               baseamount   = SecureTradingUtils.getAmount(transDetailsVO.getAmount());

           try
           {

               String RecurringRequest ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                       "<requestblock version=\"3.67\">" +
                       "<alias>"+userName+"</alias>" +
                       "<request type=\"AUTH\">" +
                       "<operation>" +
                       " <sitereference>"+ StringEscapeUtils.escapeXml(member_id)+"</sitereference>" +
                       " <accounttypedescription>"+StringEscapeUtils.escapeXml("RECUR")+"</accounttypedescription>" +
                       " <parenttransactionreference>"+StringEscapeUtils.escapeXml(parenttransactionreference)+"</parenttransactionreference>" +
                       " <credentialsonfile>2</credentialsonfile>"+
                       "</operation>" +
                       "<billing>"+
                       " <subscription type=\""+StringEscapeUtils.escapeXml("RECURRING")+"\">"+
                       "  <number>"+StringEscapeUtils.escapeXml(number)+"</number>"+
                       " </subscription>"+
                       " <amount currencycode=\""+StringEscapeUtils.escapeXml(transDetailsVO.getCurrency())+"\">"+StringEscapeUtils.escapeXml(String.valueOf(baseamount))+"</amount>"+
                       "</billing>"+
                       " </request>" +
                       "</requestblock>";

 //"<number>"+StringEscapeUtils.escapeXml(String.valueOf(trackingID))+"</number>"+
               RecurringRequest = SecureTradingUtils.verifyXML(RecurringRequest);
               transactionLogger.error("-----Recurring Request-----"+trackingID+"-->"+RecurringRequest);
               String payoutRes ="";
               String transactionRef        = "";
               if(isTest){
                   transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                   payoutRes = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), RecurringRequest, "BASIC", encodedCredentials);
               }else {
                   transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                   payoutRes = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), RecurringRequest, "BASIC", encodedCredentials);
               }
               transactionLogger.error("-----Recurring response-----"+trackingID+"-->" + payoutRes);

               Map readResponse = SecureTradingUtils.readSoapResponse(payoutRes);

               if(readResponse != null && !readResponse.equals(""))
               {
                   String acquirerresponsemessage = "";
                   transactionRef        = (String) readResponse.get("transactionreference");
                   String timeStamp             = (String) readResponse.get("timestamp");
                   String settleDuedate         = (String) readResponse.get("settleduedate");
                   String settleStatus          = (String) readResponse.get("settlestatus");
                   String parentTransRef        = (String) readResponse.get("parenttransactionreference");
                   String message               = (String) readResponse.get("message");
                   String code                  = (String) readResponse.get("code");
                   String acquirerresponsecode  = (String)readResponse.get("acquirerresponsecode");
                   String authcode              =(String)readResponse.get("authcode");
                   String rrn                   = (String) readResponse.get("retrievalreferencenumber");
                   acquirerresponsemessage      = (String)readResponse.get("acquirerresponsemessage");
                   String billidesc             = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();


                   if(functions.isValueNull(acquirerresponsemessage)){
                       acquirerresponsemessage = "";
                   }
                   transactionLogger.error("trackingID="+trackingID+"---acquirerresponsecode="+acquirerresponsecode+"---transactionRef="+transactionRef+"---settleStatus="+settleStatus+"---code="+code+"---authcode="+authcode+"---billidesc="+billidesc);

                   String status="";
                   if(("0".equals(settleStatus) || "1".equals(settleStatus)) && "0".equals(code)||"00".equals(acquirerresponsecode))
                   {
                       status       = "success";
                       commResponseVO.setDescription("SYS: Transaction Successful");
                   }
                   else
                   {
                       status   = "failed";
                       commResponseVO.setDescription("SYS: Transaction Failed");
                   }


                   if(functions.isValueNull(acquirerresponsemessage))
                   {
                       commResponseVO.setRemark(acquirerresponsemessage);
                       commResponseVO.setDescription(acquirerresponsemessage);
                   }else{
                       commResponseVO.setRemark(message);
                       commResponseVO.setDescription(message);
                   }
                   commResponseVO.setStatus(status);
                  //commResponseVO.setRemark(message);
                   commResponseVO.setTransactionId(transactionRef);
                   commResponseVO.setAuthCode(authcode);
                   commResponseVO.setDescriptor(billidesc);
                   commResponseVO.setRrn(rrn);
                   commResponseVO.setTransactionType(PZProcessType.REBILLING.toString());

               }
               commResponseVO.setCurrency(currency);
               commResponseVO.setTmpl_Amount(tmpl_amount);
               commResponseVO.setTmpl_Currency(tmpl_currency);
               commResponseVO.setTransactionId(transactionRef);
           }
           catch(Exception e)
           {
               transactionLogger.error("Exception-----"+trackingID+"--->",e);
           }
           return commResponseVO;
       }

    public GenericResponseVO processDynamicCurrencyConversion(String trackingId,GenericRequestVO genericRequestVO)
    {
        Functions functions=new Functions();
        CommResponseVO commResponseVO=new CommResponseVO();
        CommRequestVO commRequestVO=(CommRequestVO)genericRequestVO;
        CommTransactionDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=commRequestVO.getCardDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String member_id = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String Amount=SecureTradingUtils.getAmount(transDetailsVO.getAmount());
        String baseAmount="";
        String currencyiso3a="";
        String transactionreference="";
        String dcctype="";
        String conversionrate="";
        String dcccurrencyiso3a="";
        String dccbaseamount="";
        try
        {
            String request = "";
            request=("<?xml version='1.0' encoding='utf-8'?>" +
                    "<requestblock version=\"3.67\">" +
                    "<alias>" + userName + "</alias>" +
                    "<request type=\"CURRENCYRATE\">" +
                    "<operation>" +
                    "<sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                    "<accounttypedescription>CURRENCYRATE</accounttypedescription>" +
                    "</operation>" +
                    "<merchant>" +
                    "<orderreference>" + StringEscapeUtils.escapeXml(trackingId) + "</orderreference>" +
                    "</merchant>" +
                    "<billing>" +
                    "<dcc type=\"DCC\">" +
                    "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + StringEscapeUtils.escapeXml(Amount) + "</amount>" +
                    "</dcc>" +
                    "<payment>" +
                    "<pan>" + StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum()) + "</pan>" +
                    "<expirydate>" + StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear()) + "</expirydate>" +
                    "<securitycode>" + StringEscapeUtils.escapeXml(cardDetailsVO.getcVV()) + "</securitycode>" +
                    "</payment>" +
                    "</billing>" +
                    "</request>" +
                    "</requestblock>");
            transactionLogger.error("processDynamicCurrencyConversion Request---"+trackingId+"--->"+request);
            request = SecureTradingUtils.verifyXML(request);
            String response = "";
            if (isTest)
            {
                transactionLogger.error("processDynamicCurrencyConversion inside isTest-----" + RB.getString("TEST_URL"));
                response = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), request, "BASIC", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                response = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), request, "BASIC", encodedCredentials);
            }
            transactionLogger.error("processDynamicCurrencyConversion Response---"+trackingId+"--->"+response);
            if(functions.isValueNull(response))
            {
                Map readResponse=SecureTradingUtils.readSoapResponse(response);
                if(readResponse!=null && !readResponse.equals(""))
                {
                    baseAmount= (String) readResponse.get("amount");
                    currencyiso3a= (String) readResponse.get("currencyiso3a");
                    dcctype= (String) readResponse.get("dcctype");
                    conversionrate= (String) readResponse.get("conversionrate");
                    dcccurrencyiso3a= (String) readResponse.get("dcccurrencyiso3a");
                    dccbaseamount= (String) readResponse.get("dccamount");
                    transactionreference= (String) readResponse.get("transactionreference");
                }
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException---"+trackingId+"-->",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---" + trackingId + "-->", e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processReAuthorization(String trackingID,GenericRequestVO requestVO){
        transactionLogger.error(":::::Entering into processSale:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        String Amount=SecureTradingUtils.getAmount(transDetailsVO.getAmount());
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String member_id = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String isFxMID=gatewayAccount.getForexMid();
        String accounttypedescription="";
        if(functions.isValueNull(isFxMID) && "Y".equalsIgnoreCase(isFxMID))
            accounttypedescription="MOTO";
        else
            accounttypedescription="ECOM";
        String isDynamicDescriptor = gatewayAccount.getIsDynamicDescriptor();
        String previousTransactionId=transDetailsVO.getPreviousTransactionId();

        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else{
            termUrl = RB.getString("Term_url");
            transactionLogger.error("from RB----" + termUrl);
        }

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

        String sitename=commMerchantVO.getSitename();
        String merchantPhone="";
        if(functions.isValueNull(sitename) && sitename.contains("https://"))
            sitename=sitename.replaceAll("https://","");
        else if(functions.isValueNull(sitename) && sitename.contains("http://"))
            sitename=sitename.replaceAll("http://","");

        if(functions.isValueNull(sitename) && sitename.contains("www."))
            sitename=sitename.replaceAll("www.","");
        transactionLogger.error("commMerchantVO.getMerchantSupportNumber()--->"+commMerchantVO.getMerchantSupportNumber());
        if(functions.isValueNull(commMerchantVO.getMerchantSupportNumber()))
            merchantPhone=commMerchantVO.getMerchantSupportNumber();
        String chargeDescription="";
        if(functions.isValueNull(sitename) && functions.isValueNull(merchantPhone))
        {
            chargeDescription = sitename + "" + merchantPhone;
            if(chargeDescription.length()>25)
                chargeDescription=sitename;
        }
        else
            chargeDescription=sitename;

        try
        {
            StringBuffer saleRequest = new StringBuffer();
            saleRequest.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                    "<requestblock version=\"3.67\">" +
                    "<alias>" + userName + "</alias>" +
                    "<request type=\"AUTH\">" +
                    " <operation>" +
                    " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                    " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                    " <credentialsonfile>" + StringEscapeUtils.escapeXml("2") + "</credentialsonfile>" +
                    "<parenttransactionreference>"+previousTransactionId+"</parenttransactionreference>" +
                    "<initiationreason>S</initiationreason>"+
                    " </operation>" +
                    " <merchant>" +
                    " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
            if (functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                saleRequest.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
            saleRequest.append(" </merchant>" +
                    " <billing>" +
                    "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + Amount + "</amount>" +
                    " <name>" +
                    "<first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                    "<last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                    " </name>" +
                    " <payment>" +
                    "<pan>" + StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum()) + "</pan>" +
                    "<expirydate>" + StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear()) + "</expirydate>" +
                    "<securitycode>" + StringEscapeUtils.escapeXml(cardDetailsVO.getcVV()) + "</securitycode>" +
                    " </payment>" +
                    " </billing>" +
                    " </request>" +
                    "</requestblock>");
            StringBuffer saleRequestlog = new StringBuffer();
            saleRequestlog.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                    "<requestblock version=\"3.67\">" +
                    "<alias>" + userName + "</alias>" +
                    "<request type=\"AUTH\">" +
                    " <operation>" +
                    " <sitereference>" + StringEscapeUtils.escapeXml(member_id) + "</sitereference>" +
                    " <accounttypedescription>" + StringEscapeUtils.escapeXml(accounttypedescription) + "</accounttypedescription>" +
                    " <credentialsonfile>" + StringEscapeUtils.escapeXml("2") + "</credentialsonfile>" +
                    "<parenttransactionreference>"+previousTransactionId+"</parenttransactionreference>" +
                    "<initiationreason>S</initiationreason>"+
                    " </operation>" +
                    " <merchant>" +
                    " <orderreference>" + StringEscapeUtils.escapeXml(trackingID) + "</orderreference>");
            if (functions.isValueNull(chargeDescription) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                saleRequestlog.append(" <chargedescription>" + StringEscapeUtils.escapeXml(chargeDescription) + "</chargedescription>");
            saleRequestlog.append(" </merchant>" +
                    " <billing>" +
                    "<amount currencycode=\"" + StringEscapeUtils.escapeXml(transDetailsVO.getCurrency()) + "\">" + Amount + "</amount>" +
                    " <name>" +
                    "<first>" + StringEscapeUtils.escapeXml(addressDetailsVO.getFirstname()) + "</first>" +
                    "<last>" + StringEscapeUtils.escapeXml(addressDetailsVO.getLastname()) + "</last>" +
                    " </name>" +
                    " <payment>" +
                    " <pan>" + functions.maskingPan(StringEscapeUtils.escapeXml(cardDetailsVO.getCardNum())) + "</pan>" +
                    " <expirydate>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getExpMonth()) + "/" + functions.maskingNumber(cardDetailsVO.getExpYear())) + "</expirydate>" +
                    " <securitycode>" + functions.maskingNumber(StringEscapeUtils.escapeXml(cardDetailsVO.getcVV())) + "</securitycode>" +
                    " </payment>" +
                    " </billing>" +
                    " </request>" +
                    "</requestblock>");

            transactionLogger.error("-----Re-Auth request-----" + trackingID + "-->" + saleRequestlog);

            String status = "";
            saleRequest = new StringBuffer(SecureTradingUtils.verifyXML(saleRequest.toString()));

            String saleResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                saleResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("TEST_URL"), saleRequest.toString(), "BASIC", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                saleResponse = SecureTradingUtils.doHttpPostConnection(RB.getString("LIVE_URL"), saleRequest.toString(), "BASIC", encodedCredentials);
            }
            transactionLogger.error("-----Re-Auth response-----" + trackingID + "-->" + saleResponse);

            if (functions.isValueNull(saleResponse))
            {
                Map readResponse = SecureTradingUtils.readSoapResponse(saleResponse.trim());
                String settlestatus= (String) readResponse.get("settlestatus");
                String code= (String) readResponse.get("code");
                String data= (String) readResponse.get("data");
                String message= (String) readResponse.get("message");
                String paymentId= (String) readResponse.get("transactionreference");
                String amount= (String) readResponse.get("amount");
                String chargedescriptionRes = (String) readResponse.get("chargedescription");
                if (("0".equals(settlestatus) || "1".equals(settlestatus) || "100".equals(settlestatus) || "10".equals(settlestatus)) && "0".equals(code)) {
                    status = "success";
                    commResponseVO.setDescription(message);
                    commResponseVO.setRemark(message);
                    if(functions.isValueNull(chargedescriptionRes) && "Y".equalsIgnoreCase(isDynamicDescriptor))
                        commResponseVO.setDescriptor(chargedescriptionRes);
                    else
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                }
                else {
                    status="fail";
                    commResponseVO.setDescription(message + ":" + data + ":ErrorCode-" + code);
                    commResponseVO.setRemark(message + ":" + data + ":ErrorCode-"+code);
                }

                commResponseVO.setStatus(status);
                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setAmount(amount);
                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException---"+trackingID+"-->",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}