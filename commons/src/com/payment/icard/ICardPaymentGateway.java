package com.payment.icard;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.Enum.PZProcessType;
import com.payment.FlutterWave.FlutterWaveUtils;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.apcoFastpay.ApcoFastpayUtils;
import com.payment.common.core.*;
import com.payment.endeavourmpi.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


/**
 * Created by Admin on 3/26/2019.
 */
public class ICardPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(ICardPaymentGateway.class.getName());
    private static ICardLogger transactionLogger = new ICardLogger(ICardPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "icard";
    private static Functions functions = new Functions();
    ICardUtils iCardUtils = new ICardUtils();

    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.icard");

    public ICardPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        logger.error("Process Sale for ICard");
        transactionLogger.error("Process Sale for ICard---"+trackingID);
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO= ((CommRequestVO)requestVO).getAddressDetailsVO();
        CommMerchantVO commMerchantVO = ((CommRequestVO)requestVO).getCommMerchantVO();
        CommDeviceDetailsVO deviceDetailsVO = ((CommRequestVO)requestVO).getCommDeviceDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        ActionEntry entry=new ActionEntry();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        auditTrailVO.setActionExecutorId("1");
        auditTrailVO.setActionExecutorName("S2S");
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String threeDsVersion = gatewayAccount.getThreeDsVersion();
        String attemptThreeD = ((CommRequestVO) requestVO).getAttemptThreeD();
        boolean isTest = gatewayAccount.isTest();
        String mpiMid=GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String stan="";
        String dttm=null;
        String datetime = formatter.format(date);
        boolean isInserted = false;
        transactionLogger.error("attemptThreeD-----"+trackingID+"-->" +attemptThreeD);
        transactionLogger.error("is3DSupported-----"+trackingID+"-->" +is3DSupported);
        transactionLogger.error("Datetime-----"+datetime);

        String currencyId = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        String year=cardDetailsVO.getExpYear();
        String expyear= year.substring(2,4);

        String termUrl ="";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----" + termUrl);
        }

        String ipaddress="";
        int port = 0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        try
        {
            if("O".equals(is3DSupported))
            {
                transactionLogger.error("Inside Only 3DSale Supported----");
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(iCardUtils.getCentAmount(transactionDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(transactionDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
                enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
                enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
                enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
                enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
                enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
                enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
                enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
                enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());
                EnrollmentResponseVO enrollmentResponseVO=null;
                transactionLogger.error("threeDsVersion--->"+threeDsVersion);
                if("3Dsv2".equalsIgnoreCase(threeDsVersion))
                    enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
                else
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                if (enrollmentResponseVO != null)
                {
                    transactionLogger.error("Inside 3DSale Enrollment ResponseVO-----");
                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();
                    transactionLogger.error("Enrolled-----"+result+"-- avr---->"+avr);
                    if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equals(avr)))
                    {
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String threeDSSessionData = enrollmentResponseVO.getThreeDSSessionData();
                        String creq = enrollmentResponseVO.getCreq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");

                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setTerURL(termUrl + trackId);
                        comm3DResponseVO.setTransactionId(trackId);
                        comm3DResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                        comm3DResponseVO.setTransactionType("Sale");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setCreq(creq);
                        comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                        comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                        comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    }
                    /*else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                        transactionLogger.error("Frictionless failed");
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                            comm3DResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                            comm3DResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                        }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                            comm3DResponseVO.setDescription("Authentication Rejected(Frictionless)");
                            comm3DResponseVO.setRemark("Authentication Rejected(Frictionless)");
                        }
                        return comm3DResponseVO;
                    }
                    else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                    {
                        stan=ICardUtils.getNextSTAN(trackingID,"SALE");
                        transactionLogger.error("Inside Frictionless 3D Card  enrolled-----");
                        StringBuffer request = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        request.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" +trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        request.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        request.append("</ipayin_request>");

                        StringBuffer requestLog = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        requestLog.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" +trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        requestLog.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        requestLog.append("</ipayin_request>");
                        transactionLogger.error("Request for Frictionless 3D  icard Sale--for--"+trackingID+"--" + requestLog);

                        String response = iCardUtils.doSocketConnection(request.toString(), ipaddress, port);

                        transactionLogger.error("Response for Frictionless 3D icard Sale--for--"+trackingID+"--" + response);

                        if (functions.isValueNull(response))
                        {
                            comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"SALE",stan);

                            if(comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                            {
                                //capture
                                transactionDetailsVO.setResponseHashInfo(comm3DResponseVO.getResponseHashInfo());
                                String approvalCode=comm3DResponseVO.getResponseHashInfo();
                                transactionDetailsVO.setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                                transactionLogger.error("TransactionID from transdetail vo----" + transactionDetailsVO.getPreviousTransactionId());
                                transactionLogger.error("ResponseHashInfo from transdetail vo ----" + transactionDetailsVO.getResponseHashInfo());
                                ((CommRequestVO) requestVO).setTransDetailsVO(transactionDetailsVO);
                                entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);

                                comm3DResponseVO = (Comm3DResponseVO) processCapture(trackingID, requestVO);

                                if (comm3DResponseVO!=null)
                                {
                                    if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                                    {
                                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                    }
                                    if(!functions.isValueNull(comm3DResponseVO.getAuthCode()))
                                        comm3DResponseVO.setAuthCode(approvalCode);
                                    transactionLogger.error("comm3DResponseVO.getAuthCode()-->"+comm3DResponseVO.getAuthCode());

                                }
                                else
                                {
                                    comm3DResponseVO.setStatus("fail");
                                    comm3DResponseVO.setRemark("Transaction Declined");
                                }
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("fail");
                                if(!functions.isValueNull(comm3DResponseVO.getRemark()))
                                    comm3DResponseVO.setRemark("Transaction Declined");
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            comm3DResponseVO.setDescription("Transaction Declined");
                        }
                    }*/
                    else
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescription("Card Not Enrolled For 3D");
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setResponseTime(datetime);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else if ("Y".equals(is3DSupported))
            {
                transactionLogger.error("Inside Y 3DSale Supported ----");
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(iCardUtils.getCentAmount(transactionDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(transactionDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
                enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
                enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
                enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
                enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
                enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
                enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
                enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
                enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());

                EnrollmentResponseVO enrollmentResponseVO=null;
                transactionLogger.error("threeDsVersion--->"+threeDsVersion);
                if("3Dsv2".equalsIgnoreCase(threeDsVersion))
                    enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
                else
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                if (enrollmentResponseVO != null)
                {
                    transactionLogger.error("Inside Enrollment ResponseVO-----");
                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();

                    if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equals(avr)))
                    {
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String threeDSSessionData = enrollmentResponseVO.getThreeDSSessionData();
                        String creq = enrollmentResponseVO.getCreq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        transactionLogger.error("trackId inside Enrolled ----" +trackId);

                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setTerURL(termUrl + trackId);
                        comm3DResponseVO.setTransactionId(trackId);
                        comm3DResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                        comm3DResponseVO.setTransactionType("Sale");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setCreq(creq);
                        comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                        comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                        comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    }
                    /*else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                        transactionLogger.error("Frictionless failed is3D Y");
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                            comm3DResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                            comm3DResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                        }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                            comm3DResponseVO.setDescription("Authentication Rejected(Frictionless)");
                            comm3DResponseVO.setRemark("Authentication Rejected(Frictionless)");
                        }
                        return comm3DResponseVO;
                    }*/
                    else
                    {
                            /*comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setDescription("Card Not Enrolled For 3D");

                            comm3DResponseVO.setRemark(result);
                            comm3DResponseVO.setResponseTime(datetime);*/
                        stan=ICardUtils.getNextSTAN(trackingID,"SALE");
                        /*String connectionSocket = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?><ipayin_request><command>5000</command>" +
                                "</ipayin_request>";

                        String connectionResponse = iCardUtils.doSocketConnection(connectionSocket, ipaddress, port);
                        transactionLogger.error("connectionResponse ---"+connectionResponse);*/

                        transactionLogger.error("Inside Non-3D Card Not enrolled-----");
                        StringBuffer request = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        request.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" +trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                        {
                            request.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        }
                        request.append("</ipayin_request>");


                        StringBuffer requestLog = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        requestLog.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" +trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                        {
                            requestLog.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        }
                        requestLog.append("</ipayin_request>");
                        transactionLogger.error("Request for Non3D icard Sale--for--"+trackingID+"--" + requestLog);

                        String response = iCardUtils.doSocketConnection(request.toString(), ipaddress, port);

                        transactionLogger.error("Response for Non3D icard Sale--for--"+trackingID+"--" + response);

                        if (functions.isValueNull(response))
                        {
                            comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"SALE",stan);
                            /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                                dttm=comm3DResponseVO.getResponseTime();
                            isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "SALE", dttm);
                            transactionLogger.error("Stan inserted------->"+isInserted);*/

                            if(comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                            {
                                //capture
                                transactionDetailsVO.setResponseHashInfo(comm3DResponseVO.getResponseHashInfo());
                                String approvalCode=comm3DResponseVO.getResponseHashInfo();
                                transactionDetailsVO.setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                                transactionLogger.error("TransactionID from transdetail vo----" + transactionDetailsVO.getPreviousTransactionId());
                                transactionLogger.error("ResponseHashInfo from transdetail vo ----" + transactionDetailsVO.getResponseHashInfo());
                                ((CommRequestVO) requestVO).setTransDetailsVO(transactionDetailsVO);
                                entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);

                                comm3DResponseVO = (Comm3DResponseVO) processCapture(trackingID, requestVO);

                                if (comm3DResponseVO!=null)
                                {
                                    if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                                    {
                                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                    }
                                    if(!functions.isValueNull(comm3DResponseVO.getAuthCode()))
                                        comm3DResponseVO.setAuthCode(approvalCode);
                                    transactionLogger.error("comm3DResponseVO.getAuthCode()-->"+comm3DResponseVO.getAuthCode());

                                }
                                else
                                {
                                    comm3DResponseVO.setStatus("fail");
                                    comm3DResponseVO.setRemark("Transaction Declined");
                                }
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("fail");
                                if(!functions.isValueNull(comm3DResponseVO.getRemark()))
                                    comm3DResponseVO.setRemark("Transaction Declined");
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            comm3DResponseVO.setDescription("Transaction Declined");
                        }
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else if ("R".equals(is3DSupported))
            {
                transactionLogger.error("Inside R 3DSale Supported ----");
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(iCardUtils.getCentAmount(transactionDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(transactionDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
                enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
                enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
                enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
                enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
                enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
                enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
                enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
                enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());

                EnrollmentResponseVO enrollmentResponseVO=null;
                transactionLogger.error("threeDsVersion--->"+threeDsVersion);
                if("3Dsv2".equalsIgnoreCase(threeDsVersion))
                    enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
                else
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                if (enrollmentResponseVO != null)
                {
                    transactionLogger.error("Inside Enrollment ResponseVO-----");
                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();

                    if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equals(avr)))
                    {
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String threeDSSessionData = enrollmentResponseVO.getThreeDSSessionData();
                        String creq = enrollmentResponseVO.getCreq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        transactionLogger.error("trackId inside Enrolled ----" +trackId);

                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setTerURL(termUrl+trackId);
                        comm3DResponseVO.setTransactionId(trackId);
                        comm3DResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                        comm3DResponseVO.setTransactionType("Sale");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setCreq(creq);
                        comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                        comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                        comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    }
                    /*else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                        transactionLogger.error("Frictionless failed is3D R");
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                            comm3DResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                            comm3DResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                        }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                            comm3DResponseVO.setDescription("Authentication Rejected(Frictionless)");
                            comm3DResponseVO.setRemark("Authentication Rejected(Frictionless)");
                        }
                        return comm3DResponseVO;
                    }
                    else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                    {
                        stan=ICardUtils.getNextSTAN(trackingID,"SALE");
                        transactionLogger.error("Inside Frictionless 3D Card Not enrolled-----");
                        StringBuffer request = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        request.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" +trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        request.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        request.append("</ipayin_request>");


                        StringBuffer requestLog = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        requestLog.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" +trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        requestLog.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        requestLog.append("</ipayin_request>");
                        transactionLogger.error("Request for Frictionless 3D icard Sale--for--"+trackingID+"--" + requestLog);

                        String response = iCardUtils.doSocketConnection(request.toString(), ipaddress, port);

                        transactionLogger.error("Response for Frictionless 3D icard Sale--for--"+trackingID+"--" + response);

                        if (functions.isValueNull(response))
                        {
                            comm3DResponseVO = iCardUtils.readICardXMLRespone(response, trackingID, "SALE", stan);

                            if(comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                            {
                                //capture
                                transactionDetailsVO.setResponseHashInfo(comm3DResponseVO.getResponseHashInfo());
                                String approvalCode=comm3DResponseVO.getResponseHashInfo();
                                transactionDetailsVO.setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                                transactionLogger.error("TransactionID from transdetail vo----" + transactionDetailsVO.getPreviousTransactionId());
                                transactionLogger.error("ResponseHashInfo from transdetail vo ----" + transactionDetailsVO.getResponseHashInfo());
                                ((CommRequestVO) requestVO).setTransDetailsVO(transactionDetailsVO);
                                entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);

                                comm3DResponseVO = (Comm3DResponseVO) processCapture(trackingID, requestVO);

                                if (comm3DResponseVO!=null)
                                {
                                    if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                                    {
                                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                    }
                                    if(!functions.isValueNull(comm3DResponseVO.getAuthCode()))
                                        comm3DResponseVO.setAuthCode(approvalCode);
                                    transactionLogger.error("comm3DResponseVO.getAuthCode()-->"+comm3DResponseVO.getAuthCode());

                                }
                                else
                                {
                                    comm3DResponseVO.setStatus("fail");
                                    comm3DResponseVO.setRemark("Transaction Declined");
                                }
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("fail");
                                if(!functions.isValueNull(comm3DResponseVO.getRemark()))
                                    comm3DResponseVO.setRemark("Transaction Declined");
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            comm3DResponseVO.setDescription("Transaction Declined");
                        }
                    }*/
                    else if(!"Only3D".equalsIgnoreCase(attemptThreeD))
                    {
                        String fromAccountId = gatewayAccount.getFromAccountId();
                        String fromMid = gatewayAccount.getFromMid();
                        TerminalManager terminalManager = new TerminalManager();
                        TerminalVO terminalVO = terminalManager.getRoutingTerminalByFromAccountId(fromAccountId, transactionDetailsVO.getToId(), transactionDetailsVO.getPaymentType(), transactionDetailsVO.getCardType());
                        if (terminalVO != null && functions.isValueNull(fromMid))
                        {
                            stan = ICardUtils.getNextSTAN(trackingID, "SALE");
                            transactionLogger.error("Routing Inside Non-3D Card Not enrolled-----");
                            String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                    "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                    "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                    "<mid>" + GatewayAccountService.getGatewayAccount(fromAccountId).getMerchantId() + "</mid>" +
                                    "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                    "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                    "</ipayin_request>";

                            String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                    "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                    "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                    "<mid>" + GatewayAccountService.getGatewayAccount(fromAccountId).getMerchantId() + "</mid>" +
                                    "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                    "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                    "</ipayin_request>";
                            transactionLogger.error("Request for Non3D icard Sale--for--" + trackingID + "--accountId--" + fromAccountId + "--" + requestLog);

                            String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                            transactionLogger.error("Response for Non3D icard Sale--for--" + trackingID + "--" + response);

                            if (functions.isValueNull(response))
                            {
                                comm3DResponseVO = iCardUtils.readICardXMLRespone(response, trackingID, "SALE", stan);

                                if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                                {
                                    //capture
                                    transactionDetailsVO.setResponseHashInfo(comm3DResponseVO.getResponseHashInfo());
                                    String approvalCode = comm3DResponseVO.getResponseHashInfo();
                                    transactionDetailsVO.setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                                    transactionLogger.error("TransactionID from transdetail vo----" + transactionDetailsVO.getPreviousTransactionId());
                                    transactionLogger.error("ResponseHashInfo from transdetail vo ----" + transactionDetailsVO.getResponseHashInfo());
                                    ((CommRequestVO) requestVO).setTransDetailsVO(transactionDetailsVO);
                                    entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);

                                    comm3DResponseVO = (Comm3DResponseVO) processCapture(trackingID, requestVO);

                                    if (comm3DResponseVO != null)
                                    {
                                        if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                                        {
                                            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                        }
                                        if (!functions.isValueNull(comm3DResponseVO.getAuthCode()))
                                            comm3DResponseVO.setAuthCode(approvalCode);
                                        transactionLogger.error("comm3DResponseVO.getAuthCode()-->" + comm3DResponseVO.getAuthCode());

                                    }
                                    else
                                    {
                                        comm3DResponseVO.setStatus("fail");
                                        comm3DResponseVO.setRemark("Transaction Declined");
                                    }
                                }
                                else
                                {
                                    comm3DResponseVO.setStatus("fail");
                                    if (!functions.isValueNull(comm3DResponseVO.getRemark()))
                                        comm3DResponseVO.setRemark("Transaction Declined");
                                }
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("fail");
                                comm3DResponseVO.setRemark("Transaction Declined");
                                comm3DResponseVO.setDescription("Transaction Declined");
                            }
                            comm3DResponseVO.setFromAccountId(fromAccountId);
                            comm3DResponseVO.setTerminalId(terminalVO.getTerminalId());
                            comm3DResponseVO.setFromMid(fromMid);
                        }else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Card Not Enrolled For 3D");
                            comm3DResponseVO.setDescription("Card Not Enrolled For 3D");
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                        comm3DResponseVO.setRemark("Card Not Enrolled For 3D");
                        comm3DResponseVO.setDescription("Card Not Enrolled For 3D");
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                transactionLogger.error("Inside Non-3D-----");
                stan=ICardUtils.getNextSTAN(trackingID,"SALE");
                String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                        "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                        "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                        "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                        "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                        "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                        "</ipayin_request>";

                String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                        "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                        "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                        "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                        "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                        "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                        "</ipayin_request>";
                transactionLogger.error("Request for Non3D icardSale--for--"+trackingID+"--" + requestLog);

                String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                transactionLogger.error("Response for Non3D icardSale--for--"+trackingID+"--" + response);

                if (functions.isValueNull(response))
                {
                    comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"SALE",stan);
                    /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                        dttm=comm3DResponseVO.getResponseTime();
                    isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "SALE", dttm);
                    transactionLogger.error("Stan inserted------->"+isInserted);*/

                    if(comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                    {
                        //capture
                        String approvalCode = comm3DResponseVO.getResponseHashInfo();
                        transactionDetailsVO.setResponseHashInfo(comm3DResponseVO.getResponseHashInfo());
                        transactionDetailsVO.setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                        transactionLogger.error("TransactionID from transdetail vo----" + transactionDetailsVO.getPreviousTransactionId());
                        transactionLogger.error("ResponseHashInfo from transdetail vo ----" + transactionDetailsVO.getResponseHashInfo());
                        ((CommRequestVO) requestVO).setTransDetailsVO(transactionDetailsVO);
                        entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);
                        comm3DResponseVO = (Comm3DResponseVO) processCapture(trackingID, requestVO);

                        if (comm3DResponseVO!=null)
                        {
                            if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                            {
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                comm3DResponseVO.setResponseHashInfo(approvalCode);
                            }
                            if(!functions.isValueNull(comm3DResponseVO.getAuthCode()))
                                comm3DResponseVO.setAuthCode(approvalCode);
                            transactionLogger.error("comm3DResponseVO.getAuthCode()-->"+comm3DResponseVO.getAuthCode());
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            if(functions.isValueNull(comm3DResponseVO.getDescription()))
                                comm3DResponseVO.setDescription("Transaction Declined");

                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                        if(!functions.isValueNull(comm3DResponseVO.getRemark()))
                            comm3DResponseVO.setRemark("Transaction Declined");
                        if(!functions.isValueNull(comm3DResponseVO.getDescription()))
                            comm3DResponseVO.setDescription("Transaction Declined");
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Sale Exception--for--"+trackingID+"--", e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        logger.debug("Inside ICard Authentication");
        transactionLogger.debug("Inside ICard Authentication");
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = ((CommRequestVO)requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO)requestVO).getCardDetailsVO();
        CommMerchantVO commMerchantVO = ((CommRequestVO)requestVO).getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommDeviceDetailsVO deviceDetailsVO=((CommRequestVO) requestVO).getCommDeviceDetailsVO();
        boolean isTest =  gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String threeDsVersion = gatewayAccount.getThreeDsVersion();
        String attemptThreeD = ((CommRequestVO) requestVO).getAttemptThreeD();
        String mpiMid=GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stan="";
        String dttm=null;
        boolean isInserted=false;

        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);
        transactionLogger.error("is3DSupported-----"+trackingID+"-->" +is3DSupported);
        transactionLogger.error("attemptThreeD-----"+trackingID+"-->" +attemptThreeD);

        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        String year=cardDetailsVO.getExpYear();
        String expyear= year.substring(2, 4);

        String termUrl ="";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----" + termUrl);
        }

        String ipaddress="";
        int port = 0 ;

        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        try
        {
            if("O".equals(is3DSupported))
            {
                transactionLogger.error("Inside Only 3DSale Supported----");
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(iCardUtils.getCentAmount(transactionDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(transactionDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
                enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
                enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
                enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
                enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
                enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
                enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
                enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
                enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());
                enrollmentRequestVO.setBrowserIp(addressDetailsVO.getCardHolderIpAddress());

                EnrollmentResponseVO enrollmentResponseVO=null;
                if("3Dsv2".equalsIgnoreCase(threeDsVersion))
                    enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
                else
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                if (enrollmentResponseVO != null)
                {
                    transactionLogger.error("Inside Enrollment ResponseVO-----");
                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();

                    if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equalsIgnoreCase(avr)))
                    {
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String threeDSSessionData = enrollmentResponseVO.getThreeDSSessionData();
                        String creq = enrollmentResponseVO.getCreq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        transactionLogger.error("acsUrl--------->"+acsUrl);
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setTerURL(termUrl + trackId);
                        comm3DResponseVO.setTransactionId(trackId);
                        comm3DResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                        comm3DResponseVO.setTransactionType("Auth");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setCreq(creq);
                        comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                        comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                        comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    }
                    /*else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                        transactionLogger.error("Frictionless failed");
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                            comm3DResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                            comm3DResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                        }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                            comm3DResponseVO.setDescription("Authentication Rejected(Frictionless)");
                            comm3DResponseVO.setRemark("Authentication Rejected(Frictionless)");
                        }
                        return comm3DResponseVO;
                    }
                    else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                    {
                        transactionLogger.error("Inside Frictionless 3D Card enrolled-----");
                        stan=ICardUtils.getNextSTAN(trackingID,"AUTH");
                        String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                "</ipayin_request>";

                        String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                "</ipayin_request>";
                        transactionLogger.error("Request for Frictionless 3D icard Auth--for--"+trackingID+"--" + requestLog);

                        String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                        transactionLogger.error("Response for Frictionless 3D icard Auth--for--"+trackingID+"--" + response);

                        if (functions.isValueNull(response))
                        {
                            comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"AUTH",stan);
                            if ("success".equals(comm3DResponseVO.getStatus()))
                            {
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            comm3DResponseVO.setDescription("Transaction Declined");
                        }
                    }*/
                    else
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescription("Card Not Enrolled For 3D");
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setResponseTime(datetime);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else if ("Y".equals(is3DSupported))
            {
                transactionLogger.error("Inside Only 3DSale Supported----");
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(iCardUtils.getCentAmount(transactionDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(transactionDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
                enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
                enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
                enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
                enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
                enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
                enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
                enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
                enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());
                enrollmentRequestVO.setBrowserIp(addressDetailsVO.getCardHolderIpAddress());

                EnrollmentResponseVO enrollmentResponseVO=null;
                if("3Dsv2".equalsIgnoreCase(threeDsVersion))
                    enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
                else
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                if (enrollmentResponseVO != null)
                {
                    transactionLogger.error("Inside Enrollment ResponseVO-----");
                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();

                    if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equalsIgnoreCase(avr)))
                    {
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String threeDSSessionData = enrollmentResponseVO.getThreeDSSessionData();
                        String creq = enrollmentResponseVO.getCreq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        transactionLogger.error("acsUrl--------->"+acsUrl);
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setTerURL(termUrl+trackId);
                        comm3DResponseVO.setTransactionId(trackId);
                        comm3DResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                        comm3DResponseVO.setTransactionType("Auth");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setCreq(creq);
                        comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                        comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                        comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    }
                    else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                        transactionLogger.error("Frictionless failed is 3D Y");
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                            comm3DResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                            comm3DResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                        }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                            comm3DResponseVO.setDescription("Authentication Rejected(Frictionless)");
                            comm3DResponseVO.setRemark("Authentication Rejected(Frictionless)");
                        }
                        return comm3DResponseVO;
                    }
                    else
                    {
                            /*comm3DResponseVO.setStatus("failed");
                            comm3DResponseVO.setDescription("Card Not Enrolled For 3D");

                            comm3DResponseVO.setRemark(result);
                            comm3DResponseVO.setResponseTime(datetime);*/

                        transactionLogger.error("Inside Non-3D Card Not enrolled-----");
                        stan=ICardUtils.getNextSTAN(trackingID,"AUTH");
                        StringBuffer request = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        request.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                        {
                            request.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        }
                        request.append("</ipayin_request>");

                        StringBuffer requestLog = new StringBuffer("<?xml version=\"1.0\" encoding=\"Windows-1251\"?>");
                        requestLog.append("<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>");
                        if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                        {
                            requestLog.append("<eci>" + enrollmentResponseVO.getEci() + "</eci><avv>" + enrollmentResponseVO.getCAVV() + "</avv><xid>" + enrollmentResponseVO.getXID() + "</xid>");
                        }
                        requestLog.append("</ipayin_request>");
                        transactionLogger.error("Request for Non3D icard Auth--for--"+trackingID+"--" + requestLog);

                        String response = iCardUtils.doSocketConnection(request.toString(), ipaddress, port);

                        transactionLogger.error("Response for Non3D icard Auth--for--"+trackingID+"--" + response);

                        if (functions.isValueNull(response))
                        {
                            comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"AUTH",stan);
                            /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                                dttm=comm3DResponseVO.getResponseTime();
                            isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "AUTH", dttm);
                            transactionLogger.error("Stan inserted------->"+isInserted);*/
                            if ("success".equals(comm3DResponseVO.getStatus()))
                            {
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            comm3DResponseVO.setDescription("Transaction Declined");
                        }
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else if ("R".equals(is3DSupported))
            {
                transactionLogger.error("Inside R Sale Supported----");
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(mpiMid);
                enrollmentRequestVO.setName(addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname());
                enrollmentRequestVO.setPan(cardDetailsVO.getCardNum());
                enrollmentRequestVO.setExpiry(expyear + "" + cardDetailsVO.getExpMonth());
                enrollmentRequestVO.setCurrency(currencyId);
                enrollmentRequestVO.setAmount(iCardUtils.getCentAmount(transactionDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(transactionDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);
                enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
                enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
                enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
                enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
                enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
                enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
                enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
                enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
                enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());
                enrollmentRequestVO.setBrowserIp(addressDetailsVO.getCardHolderIpAddress());

                EnrollmentResponseVO enrollmentResponseVO=null;
                if("3Dsv2".equalsIgnoreCase(threeDsVersion))
                    enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
                else
                    enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                if (enrollmentResponseVO != null)
                {
                    transactionLogger.error("Inside Enrollment ResponseVO-----");
                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();

                    if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equalsIgnoreCase(avr)))
                    {
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String threeDSSessionData = enrollmentResponseVO.getThreeDSSessionData();
                        String creq = enrollmentResponseVO.getCreq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");
                        transactionLogger.error("acsUrl--------->"+acsUrl);
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                        comm3DResponseVO.setPaReq(PAReq);
                        comm3DResponseVO.setTerURL(termUrl + trackId);
                        comm3DResponseVO.setTransactionId(trackId);
                        comm3DResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                        comm3DResponseVO.setTransactionType("Auth");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setCreq(creq);
                        comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                        comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                        comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    }
                    /*else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                        transactionLogger.error("Frictionless failed is3D R");
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                            comm3DResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                            comm3DResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                        }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                            comm3DResponseVO.setDescription("Authentication Rejected(Frictionless)");
                            comm3DResponseVO.setRemark("Authentication Rejected(Frictionless)");
                        }
                        return comm3DResponseVO;
                    }
                    else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                    {
                        transactionLogger.error("Inside Frictionless 3D Card enrolled-----");
                        stan=ICardUtils.getNextSTAN(trackingID,"AUTH");
                        String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                "</ipayin_request>";

                        String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                "</ipayin_request>";
                        transactionLogger.error("Request for Frictionless 3D icard Auth--for--"+trackingID+"--" + requestLog);

                        String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                        transactionLogger.error("Response for Frictionless 3D icard Auth--for--"+trackingID+"--" + response);

                        if (functions.isValueNull(response))
                        {
                            comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"AUTH",stan);
                            if ("success".equals(comm3DResponseVO.getStatus()))
                            {
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setRemark("Transaction Declined");
                            comm3DResponseVO.setDescription("Transaction Declined");
                        }
                    }*/
                    else if(!"Only3D".equalsIgnoreCase(attemptThreeD))
                    {
                        String fromAccountId = gatewayAccount.getFromAccountId();
                        String fromMid = gatewayAccount.getFromMid();
                        TerminalManager terminalManager = new TerminalManager();
                        TerminalVO terminalVO = terminalManager.getRoutingTerminalByFromAccountId(fromAccountId, transactionDetailsVO.getToId(), transactionDetailsVO.getPaymentType(), transactionDetailsVO.getCardType());
                        if (terminalVO != null)
                        {
                            transactionLogger.error("Inside Non-3D Card Not enrolled-----");
                            stan = ICardUtils.getNextSTAN(trackingID, "AUTH");
                            String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                    "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                                    "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                                    "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                    "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                    "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                    "</ipayin_request>";

                            String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                                    "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                                    "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                                    "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                                    "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                                    "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                                    "</ipayin_request>";
                            transactionLogger.error("Request for Non3D icard Auth--for--" + trackingID + "--" + requestLog);

                            String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                            transactionLogger.error("Response for Non3D icard Auth--for--" + trackingID + "--" + response);

                            if (functions.isValueNull(response))
                            {
                                comm3DResponseVO = iCardUtils.readICardXMLRespone(response, trackingID, "AUTH", stan);
                                if ("success".equals(comm3DResponseVO.getStatus()))
                                {
                                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                comm3DResponseVO.setFromAccountId(fromAccountId);
                                comm3DResponseVO.setTerminalId(terminalVO.getTerminalId());
                                comm3DResponseVO.setFromMid(fromMid);
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("fail");
                                comm3DResponseVO.setRemark("Transaction Declined");
                                comm3DResponseVO.setDescription("Transaction Declined");
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                            comm3DResponseVO.setDescription("Card Not Enrolled For 3D");
                            comm3DResponseVO.setRemark("Card Not Enrolled For 3D");
                            comm3DResponseVO.setResponseTime(datetime);
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                        comm3DResponseVO.setDescription("Card Not Enrolled For 3D");
                        comm3DResponseVO.setRemark("Card Not Enrolled For 3D");
                        comm3DResponseVO.setResponseTime(datetime);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                stan=ICardUtils.getNextSTAN(trackingID,"AUTH");
                String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                        "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                        "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                        "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                        "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                        "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                        "</ipayin_request>";

                String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                        "<ipayin_request><command>601</command><stan>" + stan +"</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                        "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                        "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                        "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                        "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                        "</ipayin_request>";
                transactionLogger.error("Request of Auth--for--" + trackingID + "--" + requestLog);

                String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                transactionLogger.error("Response of Auth--for--"+trackingID+"--" + response);

                if (functions.isValueNull(response))
                {
                    comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"AUTH",stan);
                    /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                        dttm=comm3DResponseVO.getResponseTime();
                    isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "AUTH", dttm);
                    transactionLogger.error("Stan inserted------->"+isInserted);*/
                    if ("success".equals(comm3DResponseVO.getStatus()))
                    {
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction is declined");
                    comm3DResponseVO.setDescription("Transaction is declined");
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Auth Exception--for--"+trackingID+"--" , e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        logger.debug("Inside ICard Capture");
        transactionLogger.debug("Inside ICard Capture");
        CommResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);

        String currencyId = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        // String approval = comm3DResponseVO.getResponseHashInfo();
        String approval = transactionDetailsVO.getResponseHashInfo();
        transactionLogger.error("Approval or ResponseHashInfo from Previous Transaction-----"+approval);

        String ipaddress="";
        String dttm=null;
        int port =0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        try
        {
            String stan=ICardUtils.getNextSTAN(trackingID,"CAPTURE");
            String request="<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                    "<ipayin_request><command>607</command><stan>"+stan+"</stan><dttm>"+datetime+"</dttm><trn>"+transactionDetailsVO.getPreviousTransactionId()+"</trn>"+
                    "<mid>"+GatewayAccountService.getGatewayAccount(accountId).getMerchantId()+"</mid><amount>"+transactionDetailsVO.getAmount()+"</amount>"+
                    "<currency>"+currencyId+"</currency><approval>"+approval+"</approval> " +
                    "</ipayin_request>";
            transactionLogger.error("Request for Capture--for--"+trackingID+"--"+request);

            String response = iCardUtils.doSocketConnection(request, ipaddress, port);

            transactionLogger.error("Response for Capture--for--"+trackingID+"--"+response);

            if (functions.isValueNull(response))
            {
                comm3DResponseVO = iCardUtils.readICardXMLResponeForCapture(response,trackingID,"CAPTURE",stan);
                /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                    dttm=comm3DResponseVO.getResponseTime();
                boolean isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "CAPTURE", dttm);
                transactionLogger.error("Stan inserted------->"+isInserted);*/

                if(!functions.isValueNull(comm3DResponseVO.getDescription()) && "success".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                    comm3DResponseVO.setDescription("Transaction Successful");
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                comm3DResponseVO.setResponseHashInfo(approval);
                comm3DResponseVO.setAuthCode(approval);
            }
            else
            {
                comm3DResponseVO.setStatus("Failed");
                comm3DResponseVO.setRemark("Transaction Declined");
                comm3DResponseVO.setDescription("Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception ", e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        logger.debug("Inside ICard Cancel");
        transactionLogger.debug("Inside ICard Cancel");
        CommResponseVO comm3DResponseVO = new Comm3DResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);

        CommAddressDetailsVO addressDetailsVO = ((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();

        String mid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String time="";
        transactionLogger.error("time------>"+time);
        String ipaddress="";
        String dttm=null;
        int port =0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }
        try
        {
            String stan=ICardUtils.getNextSTAN(trackingID,"CANCEL");
            time=iCardUtils.getTimeUsingTrackingid(trackingID);
            if(time.contains(".") && functions.isValueNull(time))
                time=time.split("\\.")[0];
            String request="<?xml version=\"1.0\" encoding=\"Windows-1251\"?><ipayin_request><command>602</command>" +
                    "<stan>"+stan+"</stan><dttm>"+datetime+"</dttm><original_stan>"+transactionDetailsVO.getResponseHashInfo()+"</original_stan>" +
                    "<original_dttm>"+time+"</original_dttm><mid>"+mid+"</mid></ipayin_request>";
            transactionLogger.error("Request of Cancel--for--"+trackingID+"--"+request);

            String response = iCardUtils.doSocketConnection(request, ipaddress, port);

            transactionLogger.error("Response of Cancel--for--"+trackingID+"--"+response);

            if (functions.isValueNull(response))
            {
                comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"CANCEL",stan);
                /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                    dttm=comm3DResponseVO.getResponseTime();
                boolean isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "CANCEL", dttm);
                transactionLogger.error("Stan inserted------->"+isInserted);*/

                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else
            {
                comm3DResponseVO.setStatus("Failed");
                comm3DResponseVO.setRemark("Transaction Declined");
                comm3DResponseVO.setDescription("Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Cancel Exception --for--"+trackingID+"-- ", e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Inside ICard Refund");
        transactionLogger.debug("Inside ICard Refund");
        CommResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);

        String currencyId =CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        // String approval = comm3DResponseVO.getResponseHashInfo();
        String approval = transactionDetailsVO.getResponseHashInfo();
        transactionLogger.error("Approval of Previous Transaction-----"+approval);

        String ipaddress="";
        String dttm=null;
        int port =0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        try
        {
            String stan=ICardUtils.getNextSTAN(trackingID,"REFUND");
            String request="<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                    "<ipayin_request><command>609</command><stan>"+stan+"</stan><dttm>"+datetime+"</dttm><trn>"+transactionDetailsVO.getPreviousTransactionId()+"</trn>"+
                    "<mid>"+GatewayAccountService.getGatewayAccount(accountId).getMerchantId()+"</mid><amount>"+transactionDetailsVO.getAmount()+"</amount>"+
                    "<currency>"+currencyId+"</currency><approval>"+approval+"</approval>"+
                    "</ipayin_request>";
            transactionLogger.error("Request of Refund--for--"+trackingID+"--"+request);

            String response= iCardUtils.doSocketConnection(request, ipaddress, port);

            transactionLogger.error("Response of Refund--for--"+trackingID+"--"+response);

            if (functions.isValueNull(response))
            {
                comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"REFUND",stan);
                /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                    dttm=comm3DResponseVO.getResponseTime();
                boolean isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "REFUND", dttm);
                transactionLogger.error("Stan inserted------->"+isInserted);*/
                if ("success".equals(comm3DResponseVO.getStatus()))
                {
                    transactionLogger.error("Trn--"+trackingID+"-->"+comm3DResponseVO.getTransactionId());
                    ((CommRequestVO) requestVO).getTransDetailsVO().setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    transactionLogger.error("--Before Refund Capture--trackingId--"+trackingID);
                    comm3DResponseVO= (CommResponseVO) processCapture(trackingID, requestVO);
                    transactionLogger.error("--After Refund Capture--trackingId--"+trackingID);

                }
            }
            else
            {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark("Transaction Declined");
                comm3DResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Refund Exception --for--"+trackingID+"--" , e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        logger.debug("Inside ICard Payout");
        transactionLogger.debug("Inside ICard Payout");
        CommResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO= ((CommRequestVO)requestVO).getCardDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3DSupported=gatewayAccount.get_3DSupportAccount();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);

        String currencyId = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        String year=cardDetailsVO.getExpYear();
        String expyear= year.substring(2,4);

        String ipaddress="";
        String dttm=null;
        int port =0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        try
        {
            String stan=ICardUtils.getNextSTAN(trackingId,"PAYOUT");
            String request="<?xml version=\"1.0\" encoding=\"Windows-1251\"?>"+
                    "<ipayin_request><command>603</command><stan>"+stan+"</stan><dttm>"+datetime+"</dttm><pan>"+cardDetailsVO.getCardNum() +"</pan>"+
                    "<expdt>"+expyear +""+cardDetailsVO.getExpMonth()+"</expdt><mid>"+GatewayAccountService.getGatewayAccount(accountId).getMerchantId()+"</mid>"+
                    "<amount>"+transactionDetailsVO.getAmount()+"</amount><currency>"+currencyId+"</currency><payment_ref>"+trackingId+"</payment_ref>"+
                    "<customer_credentials>"+addressDetailsVO.getEmail()+"</customer_credentials><business_application>BB</business_application>"+
                    "</ipayin_request>";

            String requestLog="<?xml version=\"1.0\" encoding=\"Windows-1251\"?>"+
                    "<ipayin_request><command>603</command><stan>"+stan+"</stan><dttm>"+datetime+"</dttm><pan>"+functions.maskingPan(cardDetailsVO.getCardNum()) +"</pan>"+
                    "<expdt>"+functions.maskingNumber(expyear) +""+functions.maskingNumber(cardDetailsVO.getExpMonth())+"</expdt><mid>"+GatewayAccountService.getGatewayAccount(accountId).getMerchantId()+"</mid>"+
                    "<amount>"+transactionDetailsVO.getAmount()+"</amount><currency>"+currencyId+"</currency><payment_ref>"+trackingId+"</payment_ref>"+
                    "<customer_credentials>"+addressDetailsVO.getEmail()+"</customer_credentials><business_application>BB</business_application>"+
                    "</ipayin_request>";
            transactionLogger.error("Request for Payout--for--"+trackingId+"--"+requestLog);

            String response=iCardUtils.doSocketConnection(request, ipaddress, port);

            transactionLogger.error("Response for Payout--for--"+trackingId+"--"+response);

            if (functions.isValueNull(response))
            {
                comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingId,"PAYOUT",stan);
                /*if(functions.isValueNull(comm3DResponseVO.getResponseTime()))
                    dttm=comm3DResponseVO.getResponseTime();
                boolean isInserted=iCardUtils.updateSTANSeqBankResponseTimeData(trackingId, stan, "PAYOUT", dttm);
                transactionLogger.error("Stan inserted------->"+isInserted);*/
                transactionLogger.error("Trn--"+trackingId+"-->"+comm3DResponseVO.getTransactionId());
                ((CommRequestVO) requestVO).getTransDetailsVO().setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                transactionLogger.error("--Before Payout Capture--trackingId--"+trackingId);
                comm3DResponseVO= (CommResponseVO) processCapture(trackingId, requestVO);
                transactionLogger.error("--After Payout Capture--trackingId--"+trackingId);
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else
            {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark("Transaction Declined");
                comm3DResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Payout Exception--for--"+trackingId+"--" , e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO, String PaRes) throws PZTechnicalViolationException
    {
        logger.error("Process Common 3D Sale for ICard");
        transactionLogger.error("Process Common 3D Sale for ICard");
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO= ((CommRequestVO)requestVO).getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        ActionEntry entry=new ActionEntry();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        auditTrailVO.setActionExecutorId("1");
        auditTrailVO.setActionExecutorName("S2S");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);

        String currencyId = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        String year=cardDetailsVO.getExpYear();
        String expyear= year.substring(2,4);

        String ipaddress="";
        int port =0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingID);

        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setMid(gatewayAccount.getCHARGEBACK_FTP_PATH());
        paresDecodeRequestVO.setTrackid(trackingID);
        paresDecodeRequestVO.setcRes(transactionDetailsVO.getCres());

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        EndeavourMPIV2Gateway endeavourMPIV2Gateway=new EndeavourMPIV2Gateway();
        ParesDecodeResponseVO paresDecodeResponseVO=null;
        paresDecodeResponseVO=((CommRequestVO) requestVO).getParesDecodeResponseVO();
        if(functions.isValueNull(transactionDetailsVO.getCres()))
            paresDecodeResponseVO=endeavourMPIV2Gateway.processRresDecode(paresDecodeRequestVO);
        else if(functions.isValueNull(paresDecodeRequestVO.getPares()))
            paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        if(paresDecodeResponseVO!=null && "3DS2".equalsIgnoreCase(paresDecodeResponseVO.getVersion()) &&("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus()) || "R".equalsIgnoreCase(paresDecodeResponseVO.getStatus())))
        {
            comm3DResponseVO.setStatus("failed");
            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            if("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus())) {
                comm3DResponseVO.setDescription("Authenticated Transaction Denied(Challenge)");
                comm3DResponseVO.setRemark("Authenticated Transaction Denied(Challenge)");
            }else if("R".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
            {
                comm3DResponseVO.setDescription("Authentication Rejected(Challenge)");
                comm3DResponseVO.setRemark("Authentication Rejected(Challenge)");
            }
            return comm3DResponseVO;
        }

        String XID="";
        String CAVV="";
        String ECI="";
        String dttm=null;

        if(paresDecodeResponseVO!=null && "3DS2".equalsIgnoreCase(paresDecodeResponseVO.getVersion()) && functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=com.directi.pg.Base64.encode(paresDecodeResponseVO.getXid().getBytes());
        }
        else if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }

        transactionLogger.error("XID Encoded-----"+com.directi.pg.Base64.encode(XID.getBytes()));
        transactionLogger.error("CAVV Encoded-----"+com.directi.pg.Base64.encode(CAVV.getBytes()));

        transactionLogger.error("ECI-----"+ECI);
        transactionLogger.error("CAVV-----"+CAVV);
        transactionLogger.error("XID-----"+XID);
        transactionLogger.error("paresDecodeResponse status-----"+paresDecodeResponseVO.getStatus());

        try
        {
            if("Y".equalsIgnoreCase(paresDecodeResponseVO.getStatus()) || "A".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
            {
                if (functions.isValueNull(ECI) && ("05".equals(ECI) || "06".equals(ECI) || "02".equals(ECI) || "01".equals(ECI)))
                {
                    transactionLogger.error("Inside Common 3D Sale request----");
                    String stan = ICardUtils.getNextSTAN(trackingID, "SALE");
                    String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                            "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                            "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                            "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                            "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                            "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                            "<eci>" + ECI + "</eci><avv>" + CAVV + "</avv><xid>" + XID + "</xid>" +
                            "</ipayin_request>";

                    String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                            "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                            "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                            "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                            "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                            "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                            "<eci>" + ECI + "</eci><avv>" + CAVV + "</avv><xid>" + XID + "</xid>" +
                            "</ipayin_request>";
                    transactionLogger.error("Request for Common 3D Sale--for--"+trackingID+"--" + requestLog);

                    String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                    transactionLogger.error("Response for Common 3D Sale--for--"+trackingID+"--" + response);

                    if (functions.isValueNull(response))
                    {
                        comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"SALE",stan);
                        String approval=comm3DResponseVO.getResponseHashInfo();
                        /*if (functions.isValueNull(comm3DResponseVO.getResponseTime()))
                            dttm = comm3DResponseVO.getResponseTime();
                        boolean isInserted = iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "SALE", dttm);
                        transactionLogger.error("Stan inserted------->" + isInserted);*/
                        transactionDetailsVO.setResponseHashInfo(comm3DResponseVO.getResponseHashInfo());
                        transactionDetailsVO.setPreviousTransactionId(comm3DResponseVO.getTransactionId());
                        transactionLogger.error("TransactionID from transdetail vo----" + transactionDetailsVO.getPreviousTransactionId());
                        //entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.ACTION_3D_AUTHORISATION_STARTED, comm3DResponseVO, auditTrailVO, null);
                        ((CommRequestVO) requestVO).setTransDetailsVO(transactionDetailsVO);

                        //String approval = comm3DResponseVO.getResponseHashInfo();

                        if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                        {
                            entry.actionEntryForCommon(trackingID, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, comm3DResponseVO, auditTrailVO, null);
                            comm3DResponseVO = (Comm3DResponseVO) processCapture(trackingID, requestVO);

                            if (comm3DResponseVO != null)
                            {
                                if (comm3DResponseVO.getStatus().equalsIgnoreCase("success"))
                                {
                                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                if(!functions.isValueNull(comm3DResponseVO.getAuthCode()))
                                    comm3DResponseVO.setAuthCode(approval);
                            }
                            else
                            {
                                comm3DResponseVO.setStatus("fail");
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("fail");
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                        comm3DResponseVO.setRemark("Transaction Declined");
                        comm3DResponseVO.setDescription("Transaction Declined");
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                comm3DResponseVO.setStatus("fail");
                if("U".equalsIgnoreCase(paresDecodeResponseVO.getStatus()) || functions.isValueNull(paresDecodeResponseVO.getStatus()))
                {
                    comm3DResponseVO.setRemark("Transaction Declined-Unable to Authenticate");
                    comm3DResponseVO.setDescription("Transaction Declined-Unable to Authenticate");
                }
                else if("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
                {
                    comm3DResponseVO.setRemark("Transaction Declined-Authentication Failed");
                    comm3DResponseVO.setDescription("Transaction Declined-Authentication Failed");
                }else
                {
                    comm3DResponseVO.setRemark("Transaction Declined "+paresDecodeResponseVO.getStatus());
                    comm3DResponseVO.setDescription("Transaction Declined "+paresDecodeResponseVO.getStatus());
                }
            }
            comm3DResponseVO.setEci(ECI);
        }
        catch (Exception e)
        {
            transactionLogger.error("processCommon3DSaleConfirmation Exception--for--"+trackingID+"--", e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO, String PaRes) throws PZTechnicalViolationException
    {

        logger.error("Process Common 3D Auth for ICard");
        transactionLogger.error("Process Common 3D Auth for ICard");
        CommResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO= ((CommRequestVO)requestVO).getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = formatter.format(date);
        transactionLogger.error("Datetime-----"+datetime);

        String currencyId = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionLogger.error("currencyId-----"+currencyId);

        String year=cardDetailsVO.getExpYear();
        String expyear= year.substring(2,4);

        String ipaddress="";
        int port =0;
        if (isTest)
        {
            transactionLogger.error("Inside TEST URL");
            ipaddress=RB.getString("TEST_IP");
            port =Integer.parseInt(RB.getString("TEST_PORT"));
        }
        else
        {
            transactionLogger.error("Inside LIVE URL");
            ipaddress=RB.getString("LIVE_IP");
            port =Integer.parseInt(RB.getString("LIVE_PORT"));
        }

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingID);
        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setTrackid(trackingID);
        paresDecodeRequestVO.setcRes(transactionDetailsVO.getCres());

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        EndeavourMPIV2Gateway endeavourMPIV2Gateway=new EndeavourMPIV2Gateway();
        ParesDecodeResponseVO paresDecodeResponseVO=null;
        if(functions.isValueNull(transactionDetailsVO.getCres()))
            paresDecodeResponseVO=endeavourMPIV2Gateway.processRresDecode(paresDecodeRequestVO);
        else if(functions.isValueNull(paresDecodeRequestVO.getPares()))
            paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        if(paresDecodeResponseVO!=null && "3DS2".equalsIgnoreCase(paresDecodeResponseVO.getVersion()) &&("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus()) || "R".equalsIgnoreCase(paresDecodeResponseVO.getStatus())))
        {
            comm3DResponseVO.setStatus("failed");
            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            if("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus())) {
                comm3DResponseVO.setDescription("Authenticated Transaction Denied(Challenge)");
                comm3DResponseVO.setRemark("Authenticated Transaction Denied(Challenge)");
            }else if("R".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
            {
                comm3DResponseVO.setDescription("Authentication Rejected(Challenge)");
                comm3DResponseVO.setRemark("Authentication Rejected(Challenge)");
            }
            return comm3DResponseVO;
        }
        String XID="";
        String CAVV="";
        String ECI="";
        String dttm=null;

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }

        transactionLogger.error("XID Encoded-----"+com.directi.pg.Base64.encode(XID.getBytes()));
        transactionLogger.error("CAVV Encoded-----"+com.directi.pg.Base64.encode(CAVV.getBytes()));

        transactionLogger.error("ECI-----"+ECI);
        transactionLogger.error("CAVV-----"+CAVV);
        transactionLogger.error("XID-----"+XID);

        try
        {
            transactionLogger.error("paresDecodeResponse status-----"+paresDecodeResponseVO.getStatus());
            if("Y".equalsIgnoreCase(paresDecodeResponseVO.getStatus())  || "A".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
            {
                if (functions.isValueNull(ECI) && ("05".equals(ECI) || "06".equals(ECI) || "02".equals(ECI) || "01".equals(ECI)))
                {
                    transactionLogger.error("Inside Common 3D Auth request--for--"+trackingID+"--");
                    String stan = ICardUtils.getNextSTAN(trackingID, "AUTH");
                    String request = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                            "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + cardDetailsVO.getCardNum() + "</pan>" +
                            "<expdt>" + expyear + "" + cardDetailsVO.getExpMonth() + "</expdt><cvc2>" + cardDetailsVO.getcVV() + "</cvc2>" +
                            "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                            "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                            "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                            "<eci>" + ECI + "</eci><aav>" + com.directi.pg.Base64.encode(CAVV.getBytes()) + "</aav><xid>" + com.directi.pg.Base64.encode(XID.getBytes()) + "</xid>" +
                            "</ipayin_request>";

                    String requestLog = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                            "<ipayin_request><command>601</command><stan>" + stan + "</stan><dttm>" + datetime + "</dttm><pan>" + functions.maskingPan(cardDetailsVO.getCardNum()) + "</pan>" +
                            "<expdt>" + functions.maskingNumber(expyear) + "" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "</expdt><cvc2>" + functions.maskingNumber(cardDetailsVO.getcVV()) + "</cvc2>" +
                            "<mid>" + GatewayAccountService.getGatewayAccount(accountId).getMerchantId() + "</mid>" +
                            "<amount>" + transactionDetailsVO.getAmount() + "</amount><currency>" + currencyId + "</currency><payment_ref>" + trackingID + "</payment_ref>" +
                            "<customer_ip>" + addressDetailsVO.getCardHolderIpAddress() + "</customer_ip><customer_credentials>" + addressDetailsVO.getEmail() + "</customer_credentials>" +
                            "<eci>" + ECI + "</eci><aav>" + com.directi.pg.Base64.encode(CAVV.getBytes()) + "</aav><xid>" + com.directi.pg.Base64.encode(XID.getBytes()) + "</xid>" +
                            "</ipayin_request>";
                    transactionLogger.error("Request for Common 3D Auth--for--"+trackingID+"--" + requestLog);

                    String response = iCardUtils.doSocketConnection(request, ipaddress, port);

                    transactionLogger.error("Response for Common 3D Auth-----" + response);

                    if (functions.isValueNull("response"))
                    {
                        comm3DResponseVO = iCardUtils.readICardXMLRespone(response,trackingID,"AUTH",stan);
                        /*if (functions.isValueNull(comm3DResponseVO.getResponseTime()))
                            dttm = comm3DResponseVO.getResponseTime();
                        boolean isInserted = iCardUtils.updateSTANSeqBankResponseTimeData(trackingID, stan, "AUTH", dttm);
                        transactionLogger.error("Stan inserted------->" + isInserted);*/
                        if ("success".equals(comm3DResponseVO.getStatus()))
                        {
                            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                        comm3DResponseVO.setRemark("Transaction Declined");
                        comm3DResponseVO.setDescription("Transaction Declined");
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark("Transaction Declined");
                    comm3DResponseVO.setDescription("Transaction Declined");
                }
            }else
            {
                comm3DResponseVO.setStatus("fail");
                if("U".equalsIgnoreCase(paresDecodeResponseVO.getStatus()) || functions.isValueNull(paresDecodeResponseVO.getStatus()))
                {
                    comm3DResponseVO.setRemark("Transaction Declined-Unable to Authenticate");
                    comm3DResponseVO.setDescription("Transaction Declined-Unable to Authenticate");
                }
                else if("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
                {
                    comm3DResponseVO.setRemark("Transaction Declined-Authentication Failed");
                    comm3DResponseVO.setDescription("Transaction Declined-Authentication Failed");
                }else
                {
                    comm3DResponseVO.setRemark("Transaction Declined "+paresDecodeResponseVO.getStatus());
                    comm3DResponseVO.setDescription("Transaction Declined "+paresDecodeResponseVO.getStatus());
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("processCommon3DAuthConfirmation Exception--for--"+trackingID+"--", e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Failed");
            comm3DResponseVO.setDescription("Failed");
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO)
    {

        transactionLogger.debug("Entering into processQuery of ApcoPaymentGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String Remark ="Transaction Declined";
        String amount = commTransactionDetailsVO.getAmount();

        try
        {
            commResponseVO.setStatus("failed");
            commResponseVO.setTransactionStatus("failed");
            commResponseVO.setRemark(Remark);
            commResponseVO.setAmount(amount);
            commResponseVO.setTransactionId(" ");

        }catch (Exception e)
        {
            transactionLogger.debug(e.getMessage());
        }
        return commResponseVO;
    }

}

