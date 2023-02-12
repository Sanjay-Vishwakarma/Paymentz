package com.payment.paynetics.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.RecurringManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.endeavourmpi.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.lang.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sandip on 9/5/2017.
 */
public class PayneticsGateway extends AbstractPaymentGateway
{
    public static final  String GATEWAY_TYPE="paynetics";
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.paynetics");
    TransactionLogger transactionLogger=new TransactionLogger(PayneticsGateway.class.getName());
    public PayneticsGateway (String accountId){this.accountId=accountId;}

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException,PZDBViolationException
    {
        String transactionType="sale";
        String bankApprovedID="";
        String P017="00";
        String P063="3";
        String errorName = "";
        String attemptThreeD = "";
        String encryptedCVV = "";
        String requestECI = "";
        String requestXID = "";
        String requestCAVV = "";
        String termUrl = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String currency = "";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommDeviceDetailsVO deviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions=new Functions();
        Calendar calendar = Calendar.getInstance();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionLogger.error("from RB----"+termUrl);
        }

        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);

        String is3DSecureRequest=account.get_3DSupportAccount();
        String reject3DCard="";
        String isCurrencyConversion="";
        String conversionCurrency="";
        String isTestWithSimulator=gatewayAccountVO.getIsTestWithSimulator();
        String terminalId="";
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        String threeDsVersion = account.getThreeDsVersion();
        boolean isTest = account.isTest();
        String isRecurring = account.getIsRecurring();

        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=genericAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=genericAddressDetailsVO.getTmpl_currency();
        }
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        if(functions.isValueNull(commRequestVO.getReject3DCard()))
        {
            reject3DCard=commRequestVO.getReject3DCard();
        }
        if(functions.isValueNull(commRequestVO.getCurrencyConversion()))
        {
            isCurrencyConversion=commRequestVO.getCurrencyConversion();
        }
        if(functions.isValueNull(commRequestVO.getConversionCurrency()))
        {
            conversionCurrency=commRequestVO.getConversionCurrency();
        }
        if(functions.isValueNull(genericTransDetailsVO.getTerminalId()))
        {
            terminalId=genericTransDetailsVO.getTerminalId();
        }

        transactionLogger.error("is3DSecureRequest:"+is3DSecureRequest);
        transactionLogger.error("reject3DCard:"+reject3DCard);
        transactionLogger.error("isTestWithSimulator:"+isTestWithSimulator);
        transactionLogger.error("isCurrencyConversion:"+isCurrencyConversion);
        transactionLogger.error("conversionCurrency:"+conversionCurrency);
        transactionLogger.error("terminalId:"+terminalId);
        transactionLogger.error("attemptThreeD:" + attemptThreeD);

        String transactionCurrency=genericTransDetailsVO.getCurrency();
        String transactionAmount=genericTransDetailsVO.getAmount();
        if("Y".equals(isCurrencyConversion) && functions.isValueNull(isCurrencyConversion)){
            Double exchangeRate=null;
            if(functions.isValueNull(conversionCurrency)){
                exchangeRate=payneticsUtils.getExchangeRate(transactionCurrency, conversionCurrency);
            }
            else{
                transactionLogger.error("rejecting transaction because conversion currency has not defined");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription("Conversion currency has not defined");
                commResponseVO.setRemark("Conversion currency has not defined");
                return commResponseVO;
            }
            if(exchangeRate!=null){
                transactionAmount=Functions.round(Double.valueOf(exchangeRate)*Double.valueOf(genericTransDetailsVO.getAmount()),2);
                transactionCurrency=conversionCurrency;
                TerminalManager terminalManager=new TerminalManager();
                if(functions.isValueNull(terminalId)){
                    TerminalVO terminalVO=terminalManager.getMemberTerminalfromTerminal(terminalId);
                    TerminalVO terminalVO1=terminalManager.getMemberTerminalDetailsForTerminalChange(terminalVO.getMemberId(),terminalVO.getPaymodeId(),terminalVO.getCardTypeId(),transactionCurrency);
                    if(terminalVO1!=null){
                        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(terminalVO1.getAccountId());
                        payneticsUtils.changeTerminalInfo(transactionAmount, transactionCurrency,String.valueOf(gatewayAccount.getAccountId()),gatewayAccount.getMerchantId(),genericTransDetailsVO.getAmount(),genericTransDetailsVO.getCurrency(),trackingId,terminalVO1.getTerminalId());
                        commResponseVO.setAmount(transactionAmount);
                    }else{
                        transactionLogger.error("rejecting transaction because "+transactionCurrency+" terminal not defined");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescriptor(account.getDisplayName());
                        commResponseVO.setTransactionId(bankApprovedID);
                        commResponseVO.setDescription(transactionCurrency+" terminal not defined");
                        commResponseVO.setRemark(transactionCurrency+" terminal not defined");
                        return commResponseVO;
                    }
                }else{
                    transactionLogger.error("rejecting transaction because invalid terminal configuration ");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescriptor(account.getDisplayName());
                    commResponseVO.setTransactionId(bankApprovedID);
                    commResponseVO.setDescription("Invalid terminal configuration");
                    commResponseVO.setRemark("Invalid terminal configuration");
                    return commResponseVO;
                }
            }else{
                transactionLogger.error("rejecting transaction because exchange rates not found");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription("Exchange rate has not been defined");
                commResponseVO.setRemark("Exchange rate has not been defined");
                return commResponseVO;
            }
            transactionLogger.error("FromCurrency:"+genericTransDetailsVO.getCurrency());
            transactionLogger.error("transactionCurrency:"+transactionCurrency);
            transactionLogger.error("ExChangeRate:"+exchangeRate);
            transactionLogger.error("transactionAmount:"+transactionAmount);
        }

        boolean _3DDataReceived = false;
        if (functions.isValueNull(genericTransDetailsVO.getEci()) && functions.isValueNull(genericTransDetailsVO.getXid()) && functions.isValueNull(genericTransDetailsVO.getVerificationId()))
        {
            requestECI = genericTransDetailsVO.getEci();
            requestXID = genericTransDetailsVO.getXid();
            requestCAVV = genericTransDetailsVO.getVerificationId();
            _3DDataReceived = true;
        }

        if ("Y".equals(is3DSecureRequest) && !("Direct".equalsIgnoreCase(attemptThreeD) && !_3DDataReceived))
        {
            EnrollmentRequestVO enrollmentRequestVO=new EnrollmentRequestVO();
            enrollmentRequestVO.setMid(gatewayAccountVO.getMpiMid());
            enrollmentRequestVO.setName(genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
            enrollmentRequestVO.setPan(genericCardDetailsVO.getCardNum());
            enrollmentRequestVO.setExpiry(payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear()));
            enrollmentRequestVO.setCurrency(CurrencyCodeISO4217.getNumericCurrencyCode(transactionCurrency));
            if(transactionCurrency.equalsIgnoreCase("JPY"))
                enrollmentRequestVO.setAmount(payneticsUtils.getJPYAmount(transactionAmount));
            else
                enrollmentRequestVO.setAmount(payneticsUtils.getCentAmount(transactionAmount));

            transactionLogger.error("Paynetics Amount---"+enrollmentRequestVO.getAmount());
            enrollmentRequestVO.setDesc(genericTransDetailsVO.getOrderDesc());
            if(functions.isValueNull(deviceDetailsVO.getUser_Agent()))
                enrollmentRequestVO.setUseragent(deviceDetailsVO.getUser_Agent());
            else
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
            enrollmentRequestVO.setAccept("en-us");
            enrollmentRequestVO.setTrackid(trackingId);
            enrollmentRequestVO.setFingerprint(deviceDetailsVO.getFingerprints());
            enrollmentRequestVO.setAcceptHeader(deviceDetailsVO.getAcceptHeader());
            enrollmentRequestVO.setHostUrl(commMerchantVO.getHostUrl());
            enrollmentRequestVO.setAcquirerMerchantID(account.getMerchantId());
            if(functions.isValueNull(commMerchantVO.getCountry()))
                enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(commMerchantVO.getCountry()));
            enrollmentRequestVO.setBrowserLanguage(deviceDetailsVO.getBrowserLanguage());
            enrollmentRequestVO.setBrowserTimezoneOffset(deviceDetailsVO.getBrowserTimezoneOffset());
            enrollmentRequestVO.setBrowserColorDepth(deviceDetailsVO.getBrowserColorDepth());
            enrollmentRequestVO.setBrowserScreenHeight(deviceDetailsVO.getBrowserScreenHeight());
            enrollmentRequestVO.setBrowserScreenWidth(deviceDetailsVO.getBrowserScreenWidth());
            enrollmentRequestVO.setBrowserJavaEnabled(deviceDetailsVO.getBrowserJavaEnabled());
            enrollmentRequestVO.setBrowserIp(genericAddressDetailsVO.getCardHolderIpAddress());
            EnrollmentResponseVO enrollmentResponseVO=null;
            transactionLogger.error("threeDsVersion--->"+threeDsVersion);
            /*if("3Dsv2".equalsIgnoreCase(threeDsVersion))
            {*/
            EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
            enrollmentResponseVO = endeavourMPIV2Gateway.processVerification(enrollmentRequestVO);
            /*}else
            {
                EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
                enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);
            }*/

            String result=enrollmentResponseVO.getResult();
            String avr=enrollmentResponseVO.getAvr();

            if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equals(avr)))
            {
                if("Y".equals(reject3DCard) && functions.isValueNull(reject3DCard)){
                    transactionLogger.error("rejecting 3d card as per configuration ");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescriptor(account.getDisplayName());
                    commResponseVO.setTransactionId(bankApprovedID);
                    commResponseVO.setDescription("3D Enrolled Card");
                    commResponseVO.setRemark("3D Enrolled Card");
                    commResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    return commResponseVO;
                }
                else{
                    transactionLogger.error("3D:card enrolled flow");
                    String PAReq=enrollmentResponseVO.getPAReq();
                    String acsUrl=enrollmentResponseVO.getAcsUrl();
                    String creq=enrollmentResponseVO.getCreq();
                    String threeDSSessionData=enrollmentResponseVO.getThreeDSSessionData();
                    try{
                        acsUrl= java.net.URLDecoder.decode(acsUrl, "UTF-8");
                    }
                    catch(UnsupportedEncodingException e){
                        transactionLogger.error("UnsupportedEncodingException:::::"+e);
                        PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                    }
                    transactionLogger.error("pareq:::"+PAReq);
                    transactionLogger.error("url:::"+acsUrl);
                    String status="pending3DConfirmation";
                    if (functions.isValueNull(genericCardDetailsVO.getcVV()))
                    {
                        encryptedCVV = PzEncryptor.encryptCVV(genericCardDetailsVO.getcVV());
                    }
                    Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
                    comm3DResponseVO.setPaReq(PAReq);
                    comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                    comm3DResponseVO.setMd(encryptedCVV);
                    comm3DResponseVO.setRedirectMethod("POST");
                    comm3DResponseVO.setTerURL(termUrl + trackingId);
                    comm3DResponseVO.setStatus(status);
                    comm3DResponseVO.setDescriptor(account.getDisplayName());
                    comm3DResponseVO.setTransactionType(transactionType);
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setTmpl_Amount(tmpl_amount);
                    comm3DResponseVO.setTmpl_Currency(tmpl_currency);
                    comm3DResponseVO.setCreq(creq);
                    comm3DResponseVO.setThreeDSSessionData(threeDSSessionData);
                    comm3DResponseVO.setThreeDSServerTransID(enrollmentResponseVO.getThreeDSServerTransID());
                    comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    return comm3DResponseVO;
                }
            }
            else if ("Only3D".equalsIgnoreCase(attemptThreeD) && !"Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
            {
                transactionLogger.error("Only 3D Card Required");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription("Only 3D Card Required");
                commResponseVO.setRemark("Only 3D Card Required");
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                return commResponseVO;
            }else if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("N".equalsIgnoreCase(enrollmentResponseVO.getAvr()) || "R".equalsIgnoreCase(enrollmentResponseVO.getAvr()))){
                transactionLogger.error("Frictionless failed");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                if("N".equalsIgnoreCase(enrollmentResponseVO.getAvr())) {
                    commResponseVO.setDescription("Authenticated Transaction Denied(Frictionless)");
                    commResponseVO.setRemark("Authenticated Transaction Denied(Frictionless)");
                }else if("R".equalsIgnoreCase(enrollmentResponseVO.getAvr())){
                    commResponseVO.setDescription("Authentication Rejected(Frictionless)");
                    commResponseVO.setRemark("Authentication Rejected(Frictionless)");
                }
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                return commResponseVO;
            }
            else
            {
                transactionLogger.error("3D:card not enrolled flow");
                String MTI = payneticsUtils.getMTI(transactionType);
                String P002 = genericCardDetailsVO.getCardNum();
                String P003 = payneticsUtils.getProcessingCode(transactionType);

                String P004 = payneticsUtils.getCentAmount(transactionAmount);
                if(transactionCurrency.equalsIgnoreCase("JPY"))
                    P004 = payneticsUtils.getJPYAmount(transactionAmount);
                String P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                String P012 = payneticsUtils.getLocalTime(calendar);
                String P013 = payneticsUtils.getLocalDate(calendar);
                String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
                String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
                String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
                String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
                String P041 = gatewayAccountVO.getTerminalId();
                String P042 = gatewayAccountVO.getMerchantId();
                String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
                String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
                String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(transactionCurrency);

                String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
                String P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                String electronicCommerceIndicator = payneticsUtils.getECI(P046);

                String P060="";
                if(functions.isValueNull(genericCardDetailsVO.getcVV()))//OK
                {
                    String P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(genericCardDetailsVO.getcVV(), "30");//CVV
                    if(functions.isValueNull(P060_30)){
                        P060=P060+P060_30;
                    }
                }
                if (functions.isValueNull(trackingId))//OK
                {
                    String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                    if(functions.isValueNull(P060_35)){
                        P060=P060+P060_35;
                    }
                }
                if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult())){
                    String ECI=enrollmentResponseVO.getEci();
                    if ("05".equals(ECI))
                    {
                        electronicCommerceIndicator = "10";
                    }
                    else if ("06".equals(ECI))
                    {
                        electronicCommerceIndicator = "12";
                    }
                    else if ("02".equals(ECI))
                    {
                        electronicCommerceIndicator = "11";
                    }
                    else if ("01".equals(ECI))
                    {
                        electronicCommerceIndicator = "13";
                    }
                    transactionLogger.error("Setting P060_40");
                    String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                    transactionLogger.error("P060_40--"+trackingId+"-->"+P060_40);
                    if(functions.isValueNull(P060_40)){
                        P060=P060+P060_40;
                    }
                }
                else if(functions.isValueNull(electronicCommerceIndicator))//OK
                {
                    String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                    if(functions.isValueNull(P060_40)){
                        P060=P060+P060_40;
                    }
                }

                //if(isTest && isRecurring.equalsIgnoreCase("Y"))
                if(isRecurring.equalsIgnoreCase("Y"))
                {
                    transactionLogger.error("Setting P060_41");
                    String P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT("01", "41");//Indicator For Electronic Commerce
                    if(functions.isValueNull(P060_41)){
                        P060=P060+P060_41;
                    }
                    transactionLogger.error("Setting P060_52");

                    //String P060_52_2 = String.format("%-1s", "2");
                    String P060_52_2 = payneticsUtils.getDATA_IN_LTV_FORMAT("2", "02");
                    String P060_52 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_52_2, "52");

                    if(functions.isValueNull(P060_52)){
                        P060=P060+P060_52;
                    }
                }
                if(!"Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()) && "81".equals(P046)){
                    transactionLogger.error("Setting P060_54");
                    String P060_54=payneticsUtils.getDATA_IN_LTV_FORMAT("01","54");
                    transactionLogger.error("P060_54--"+trackingId+"-->"+P060_54);
                    if(functions.isValueNull(P060_54)){
                        P060=P060+P060_54;
                    }
                }
                if("Frictionless".equalsIgnoreCase(enrollmentResponseVO.getResult()))
                {
                    if (enrollmentResponseVO.get_20BytesBinaryXIDBytes()!=null && enrollmentResponseVO.get_20BytesBinaryXIDBytes().length>0 && ("80".equals(P046) || "83".equals(P046)))//OK
                    {
                        transactionLogger.error("Setting P060_61");
                        String P060_61 = payneticsUtils.getDATA_IN_LTV_FORMAT(enrollmentResponseVO.get_20BytesBinaryXIDBytes(), "61");//xid
                        transactionLogger.error("P060_61--"+trackingId+"-->"+P060_61);
                        if (functions.isValueNull(P060_61))
                        {
                            P060 = P060 + P060_61;
                        }
                    }
                    if (enrollmentResponseVO.get_20BytesBinaryCAVVBytes()!=null && enrollmentResponseVO.get_20BytesBinaryCAVVBytes().length>0 && ("80".equals(P046) || "83".equals(P046)))//OK
                    {
                        transactionLogger.error("Setting P060_62");
                        String P060_62 = payneticsUtils.getDATA_IN_LTV_FORMATForCAVV(enrollmentResponseVO.get_20BytesBinaryCAVVBytes(), "62");//CAVV
                        transactionLogger.error("P060_62--"+trackingId+"-->"+P060_62);
                        if (functions.isValueNull(P060_62))
                        {
                            P060 = P060 + P060_62;
                        }
                    }
                    String UCAF=enrollmentResponseVO.getCAVV();
                    if (functions.isValueNull(UCAF) && ("81".equals(P046) || "82".equals(P046)))//OK
                    {
                        transactionLogger.error("Setting P060_63");
                        String P060_63 = payneticsUtils.getDATA_IN_LTV_FORMAT(UCAF, "63");//UCAF
                        transactionLogger.error("P060_63--"+trackingId+"-->"+P060_63);
                        if (functions.isValueNull(P060_63))
                        {
                            P060 = P060 + P060_63;
                        }
                    }

                    transactionLogger.error("Setting P060_72");
                    String P060_72=payneticsUtils.getDATA_IN_LTV_FORMAT("1","72");
                    transactionLogger.error("P060_72--"+trackingId+"-->"+P060_72);
                    if(functions.isValueNull(P060_72)){
                        P060=P060+P060_72;
                    }
                    transactionLogger.error("Setting P060_73");
                    String P060_73=payneticsUtils.getDATA_IN_LTV_FORMAT(enrollmentResponseVO.getDsTransId(),"73");
                    transactionLogger.error("P060_73--"+trackingId+"-->"+P060_73);
                    if(functions.isValueNull(P060_73)){
                        P060=P060+P060_73;
                    }
                }

                if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
                {
                    String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                    if(functions.isValueNull(P060_81)){
                        P060=P060+P060_81;
                    }
                }
                if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
                {
                    String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                    if(functions.isValueNull(P060_82)){
                        P060=P060+P060_82;
                    }
                }
                if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
                {
                    String P060_83_1="";
                    String P060_83_2="";
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                        P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
                    }else{
                        P060_83_1=String.format("%-15s",P060_83_1);
                    }
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                        P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
                    }else{
                        P060_83_2=String.format("%-4s",P060_83_2);
                    }
                    String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                    if(functions.isValueNull(P060_83)){
                        P060=P060+P060_83;
                    }
                }

                if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
                    String P060_84_1="";
                    String P060_84_2="";
                    String P060_84_3="";

                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                        P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
                    }else{
                        P060_84_1=String.format("%-10s",P060_84_1);
                    }
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                        P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
                    }else{
                        P060_84_2=String.format("%-25s",P060_84_2);
                    }
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                        P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
                    }else{
                        P060_84_3=String.format("%-13s",P060_84_3);
                    }
                    String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                    if(functions.isValueNull(P060_84)){
                        P060=P060+P060_84;
                    }
                }

                String P060_87_1 = String.format("%-1s", "1");
                if (functions.isValueNull(P060_87_1))
                {
                    P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                    if (functions.isValueNull(P060_87_1))
                    {
                        String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                        if (functions.isValueNull(P060_87))
                        {
                            P060 = P060 + P060_87;
                        }
                    }
                }

                HashMap<String,String> hashMap=new HashMap();
                hashMap.put("MTYP",MTI);
                hashMap.put("P002",P002);
                hashMap.put("P003",P003);
                hashMap.put("P004",P004);
                hashMap.put("P011", P011);
                hashMap.put("P012",P012);
                hashMap.put("P013",P013);
                hashMap.put("P014",P014);
                hashMap.put("P017",P017);
                hashMap.put("P022",P022);
                hashMap.put("P025",P025);
                hashMap.put("P032",P032);
                hashMap.put("P041",P041);
                hashMap.put("P042",P042);
                hashMap.put("P043",P043);
                hashMap.put("P046",P046);
                hashMap.put("P049",P049);
                hashMap.put("P057", P057);
                hashMap.put("P060",P060);
                hashMap.put("P063",P063);

                //record the data.
                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                if (inserted)
                {
                    transactionLogger.error("Recorded channel request:" + inserted);
                }

                String status="";
                String statusDescription="";
                String remark="";
                String descriptor="";
                String responseTime="";
                String resTStamp = "";
                try
                {
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
                    GenericPackager packager = new GenericPackager(inputStream);
                    ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
                    try{
                        ISOMsg _200_ResMessage=null;
                        String env=RB.getString("Env");
                        if("Y".equals(isTestWithSimulator)){
                            env="development";
                        }
                        if("development".equals(env)){
                            _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                            if(_200_ResMessage!=null){
                                String responseCode=_200_ResMessage.getString("39");
                                if("00".equalsIgnoreCase(responseCode)){
                                    status="success";
                                    descriptor=account.getDisplayName();
                                }
                                else
                                {
                                    status="failed";
                                    descriptor="";
                                    errorName = getErrorName(responseCode);
                                }
                                if(isRecurring.equalsIgnoreCase("Y"))
                                    payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                                else
                                    payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                                bankApprovedID=_200_ResMessage.getString("38");
                                statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                                remark=PayneticsErrorCodes.getErrorCode(responseCode);
                            }
                        }
                        else{
                            PzNACChannel channel = null;
                            Logger logger = new Logger();
                            logger.addListener(new SimpleLogListener(System.out));
                            if (isTest)
                            {
                                /*1)Send direct msg to paynetics.
                                *2)or Send msg to primary TQMux.
                                *3)or Send msg to DR TQMux when primary VPN is down.
                                * */
                                if ("Y".equals(isQMuxActive))
                                {
                                    channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        try
                                        {
                                            channel.connect();
                                        }
                                        catch (IOException e1)
                                        {
                                            transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                            channel.setLogger(logger, "test-channel");
                                            channel.connect();
                                        }
                                    }
                                }
                                else
                                {
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary Test VPN is down,connecting to DR TQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                /*1)Send direct msg to paynetics.
                                 *2)Send msg to primary LQMux
                                 *3)or Send msg to DR LQMux when primary VPN is down.
                                 * */
                                if ("Y".equals(isQMuxActive))
                                {
                                    channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        try
                                        {
                                            channel.connect();
                                        }
                                        catch (IOException e1)
                                        {
                                            transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                            channel.setLogger(logger, "live-channel");
                                            channel.connect();
                                        }
                                    }
                                }
                                else
                                {
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        channel.connect();
                                    }
                                }
                            }

                            channel.send(isoMsg);
                            _200_ResMessage =channel.receive ();
                            channel.disconnect();
                            if(_200_ResMessage!=null)
                            {
                                String responseCode=_200_ResMessage.getString("39");
                                if(isRecurring.equalsIgnoreCase("Y"))
                                {
                                    transactionLogger.error("BMP 61: Transaction stamp---" + _200_ResMessage.getString("61"));
                                    resTStamp = _200_ResMessage.getString("61");
                                }
                                if("00".equalsIgnoreCase(responseCode)){
                                    status="success";
                                    descriptor=account.getDisplayName();
                                }
                                else if("06".equals(_200_ResMessage.getString("39"))){
                                    status="pending";
                                    descriptor="";
                                }else{
                                    status="failed";
                                    descriptor="";
                                    errorName = getErrorName(responseCode);
                                }
                                if ("pending".equals(status))
                                {
                                    if(isRecurring.equalsIgnoreCase("Y"))
                                        payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                                    else
                                        payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                                    nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);

                                    P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                                    P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

                                    isoMsg.set("11", P011);
                                    isoMsg.set("57",P057);

                                    inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                    if (inserted)
                                    {
                                        transactionLogger.error("Recorded channel request:" + inserted);
                                    }

                                    channel.connect ();
                                    channel.send(isoMsg);
                                    _200_ResMessage =channel.receive ();
                                    channel.disconnect();
                                    responseCode=_200_ResMessage.getString("39");
                                    if("00".equalsIgnoreCase(responseCode)){
                                        status="success";
                                        descriptor=account.getDisplayName();
                                    }else{
                                        status="failed";
                                        descriptor="";
                                        errorName = getErrorName(responseCode);
                                    }
                                }
                                if(isRecurring.equalsIgnoreCase("Y"))
                                    payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                                else
                                    payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                                bankApprovedID=_200_ResMessage.getString("38");
                                statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                                remark=PayneticsErrorCodes.getErrorCode(responseCode);
                            }
                        }
                    }
                    catch(ISOException e)
                    {
                        status="failed";
                        statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                        remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                        transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE---",e);
                    }
                }
                catch(IOException e){
                    PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
                catch (ISOException e){
                    PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }

                /*recurring transaction subscription entry*/
                if("Y".equalsIgnoreCase(account.getIsRecurring())){
                    String cardNumber = genericCardDetailsVO.getCardNum();
                    String first_six=cardNumber.substring(0,6);
                    String last_four=cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                    RecurringManager recurringManager = new RecurringManager();
                    if("success".equalsIgnoreCase(status)){
                        recurringManager.updateRbidForSuccessfullRebill(bankApprovedID, first_six, last_four, trackingId);
                    }else{
                        recurringManager.deleteEntryForPFSRebill(trackingId);
                    }
                }

                commResponseVO.setStatus(status);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription(statusDescription);
                commResponseVO.setRemark(remark);
                commResponseVO.setResponseTime(responseTime);
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                if (functions.isValueNull(errorName))
                    commResponseVO.setErrorName(errorName);
                return commResponseVO;
            }
        }
        else
        {
            String electronicCommerceIndicator = "";
            if (_3DDataReceived && functions.isValueNull(requestECI))
            {
                if ("05".equals(requestECI))
                {
                    electronicCommerceIndicator = "10";
                }
                else if ("06".equals(requestECI))
                {
                    electronicCommerceIndicator = "12";
                }
                else if ("02".equals(requestECI))
                {
                    electronicCommerceIndicator = "11";
                }
                else if ("01".equals(requestECI))
                {
                    electronicCommerceIndicator = "13";
                }
            }
            else
            {
                electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());
            }

            String MTI = payneticsUtils.getMTI(transactionType);
            String P002 = genericCardDetailsVO.getCardNum();
            String P003 = payneticsUtils.getProcessingCode(transactionType);
            String P004 = payneticsUtils.getCentAmount(transactionAmount);
            if(transactionCurrency.equalsIgnoreCase("JPY"))
                P004 = payneticsUtils.getJPYAmount(transactionAmount);
            String P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
            String P012 = payneticsUtils.getLocalTime(calendar);
            String P013 = payneticsUtils.getLocalDate(calendar);
            String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
            String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
            String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
            String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
            String P041 = gatewayAccountVO.getTerminalId();
            String P042 = gatewayAccountVO.getMerchantId();
            String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
            String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
            String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(transactionCurrency);

            String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
            String P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

            String P060 = "";
            if(functions.isValueNull(genericCardDetailsVO.getcVV()))//OK
            {
                String P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(genericCardDetailsVO.getcVV(), "30");//CVV
                if(functions.isValueNull(P060_30)){
                    P060=P060+P060_30;
                }
            }
            if (functions.isValueNull(trackingId))//OK
            {
                String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                if(functions.isValueNull(P060_35)){
                    P060=P060+P060_35;
                }
            }
            if(functions.isValueNull(electronicCommerceIndicator))//OK
            {
                String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                if(functions.isValueNull(P060_40)){
                    P060=P060+P060_40;
                }
            }

            //if(isTest && isRecurring.equalsIgnoreCase("Y"))
            if(isRecurring.equalsIgnoreCase("Y"))
            {
                transactionLogger.error("Setting P060_41 when is3DSecure=N");
                String P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT("01", "41");//Indicator For Electronic Commerce
                if(functions.isValueNull(P060_41)){
                    P060=P060+P060_41;
                }
                transactionLogger.error("Setting P060_52 when is3DSecure=N");

                String P060_52_2 = payneticsUtils.getDATA_IN_LTV_FORMAT("2", "02");
                String P060_52 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_52_2, "52");

                //String P060_52 = payneticsUtils.getDATA_IN_LTV_FORMAT("003022", "52");//Indicator For Electronic Commerce
                if(functions.isValueNull(P060_52)){
                    P060=P060+P060_52;
                }
            }
            if("81".equals(P046)){
                transactionLogger.error("Setting P060_54");
                String P060_54=payneticsUtils.getDATA_IN_LTV_FORMAT("01","54");
                transactionLogger.error("P060_54--"+trackingId+"-->"+P060_54);
                if(functions.isValueNull(P060_54)){
                    P060=P060+P060_54;
                }
            }
            if (functions.isValueNull(requestXID) && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                String P060_61 = payneticsUtils.getDATA_IN_LTV_FORMAT(requestXID, "61");//xid
                if (functions.isValueNull(P060_61))
                {
                    P060 = P060 + P060_61;
                }
            }

            if (functions.isValueNull(requestCAVV) && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                String P060_62 = payneticsUtils.getDATA_IN_LTV_FORMAT(requestCAVV, "62");//CAVV
                if (functions.isValueNull(P060_62))
                {
                    P060 = P060 + P060_62;
                }
            }
            else if (functions.isValueNull(requestCAVV) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_63 = payneticsUtils.getDATA_IN_LTV_FORMAT(requestCAVV, "63");//UCAF
                if (functions.isValueNull(P060_63))
                {
                    P060 = P060 + P060_63;
                }
            }

            if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                if(functions.isValueNull(P060_81)){
                    P060=P060+P060_81;
                }
            }
            if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                if(functions.isValueNull(P060_82)){
                    P060=P060+P060_82;
                }
            }
            if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
            {
                String P060_83_1="";
                String P060_83_2="";
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                    P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
                }else{
                    P060_83_1=String.format("%-15s",P060_83_1);
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                    P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
                }else{
                    P060_83_2=String.format("%-4s",P060_83_2);
                }
                String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                if(functions.isValueNull(P060_83)){
                    P060=P060+P060_83;
                }
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
                String P060_84_1="";
                String P060_84_2="";
                String P060_84_3="";

                if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                    P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
                }else{
                    P060_84_1=String.format("%-10s",P060_84_1);
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                    P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
                }else{
                    P060_84_2=String.format("%-25s",P060_84_2);
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                    P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
                }else{
                    P060_84_3=String.format("%-13s",P060_84_3);
                }
                String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                if(functions.isValueNull(P060_84)){
                    P060=P060+P060_84;
                }
            }
            String P060_87_1 = String.format("%-1s", "1");
            if (functions.isValueNull(P060_87_1))
            {
                P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                if (functions.isValueNull(P060_87_1))
                {
                    String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                    if (functions.isValueNull(P060_87))
                    {
                        P060 = P060 + P060_87;
                    }
                }
            }

            HashMap<String,String> hashMap=new HashMap();
            hashMap.put("MTYP",MTI);
            hashMap.put("P002",P002);
            hashMap.put("P003",P003);
            hashMap.put("P004",P004);
            hashMap.put("P011", P011);
            hashMap.put("P012",P012);
            hashMap.put("P013",P013);
            hashMap.put("P014",P014);
            hashMap.put("P017",P017);
            hashMap.put("P022",P022);
            hashMap.put("P025",P025);
            hashMap.put("P032",P032);
            hashMap.put("P041",P041);
            hashMap.put("P042",P042);
            hashMap.put("P043",P043);
            hashMap.put("P046",P046);
            hashMap.put("P049",P049);
            hashMap.put("P057", P057);
            hashMap.put("P060",P060);
            hashMap.put("P063",P063);

            //record the data.
            boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
            if (inserted)
            {
                transactionLogger.error("Recorded channel request:" + inserted);
            }

            String status="";
            String statusDescription="";
            String remark="";
            String descriptor="";
            String responseTime="";
            String resTStamp="";
            try{
                InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
                GenericPackager packager = new GenericPackager(inputStream);
                ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
                try{
                    ISOMsg _200_ResMessage=null;
                    String env=RB.getString("Env");
                    if("Y".equals(isTestWithSimulator)){
                        env="development";
                    }
                    if("development".equals(env)){
                        _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                        if(_200_ResMessage!=null){
                            String responseCode=_200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }
                            else
                            {
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }

                            if(isRecurring.equalsIgnoreCase("Y"))
                                payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                            else
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedID=_200_ResMessage.getString("38");
                            statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                            remark=PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                    else{
                        PzNACChannel channel = null;
                        Logger logger = new Logger();
                        logger.addListener(new SimpleLogListener(System.out));
                        if (isTest)
                        {
                            /*1)Send direct msg to paynetics.
                            *2)or Send msg to primary TQMux.
                            *3)or Send msg to DR TQMux when primary VPN is down.
                            * */
                            if ("Y".equals(isQMuxActive))
                            {
                                channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e1)
                                    {
                                        transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                        channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary Test VPN is down,connecting to DR TQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            /*1)Send direct msg to paynetics.
                            *2)Send msg to primary LQMux
                            *3)or Send msg to DR LQMux when primary VPN is down.
                            * */
                            if ("Y".equals(isQMuxActive))
                            {
                                channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e1)
                                    {
                                        transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                        channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }

                        channel.send(isoMsg);
                        _200_ResMessage =channel.receive ();
                        channel.disconnect();
                        if(_200_ResMessage!=null)
                        {
                            if(isRecurring.equalsIgnoreCase("Y"))
                            {
                                transactionLogger.error("BMP 61: Transaction stamp when is3DSecure=N---" + _200_ResMessage.getString("61"));
                                resTStamp = _200_ResMessage.getString("61");
                            }

                            String responseCode=_200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }
                            else if("06".equals(_200_ResMessage.getString("39"))){
                                status="pending";
                                descriptor="";
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                            if ("pending".equals(status))
                            {
                                if(isRecurring.equalsIgnoreCase("Y"))
                                    payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                                else
                                    payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);

                                nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);

                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                                P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57",P057);

                                inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }
                                channel.connect ();
                                channel.send(isoMsg);
                                _200_ResMessage =channel.receive ();
                                channel.disconnect();
                                responseCode=_200_ResMessage.getString("39");
                                if("00".equalsIgnoreCase(responseCode)){
                                    status="success";
                                    descriptor=account.getDisplayName();
                                }else{
                                    status="failed";
                                    descriptor="";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            if(isRecurring.equalsIgnoreCase("Y"))
                                payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                            else
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedID=_200_ResMessage.getString("38");
                            statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                            remark=PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                }
                catch(ISOException e){
                    status="failed";
                    statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                    remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                    transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
                }
            }
            catch(IOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (ISOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }

            /*recurring transaction subscription entry*/
            if ("Y".equalsIgnoreCase(account.getIsRecurring()))
            {
                String cardNumber = genericCardDetailsVO.getCardNum();
                String first_six = cardNumber.substring(0, 6);
                String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                RecurringManager recurringManager = new RecurringManager();
                if ("success".equalsIgnoreCase(status))
                {
                    recurringManager.updateRbidForSuccessfullRebill(bankApprovedID, first_six, last_four, trackingId);
                }
                else
                {
                    recurringManager.deleteEntryForPFSRebill(trackingId);
                }
            }

            commResponseVO.setTransactionType(transactionType);
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionId(bankApprovedID);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(statusDescription);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(responseTime);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            if (functions.isValueNull(errorName))
                commResponseVO.setErrorName(errorName);
            return commResponseVO;
        }
    }
    public GenericResponseVO processAuthentication(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String transactionType = "auth";
        String P017 = "00";
        String P063 = "3";
        String errorName = "";
        String attemptThreeD = "";
        String encryptedCVV = "";
        String bankApprovedID="";
        String requestECI = "";
        String requestXID = "";
        String requestCAVV = "";
        String termUrl = "";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();//Optional

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions = new Functions();
        Calendar calendar = Calendar.getInstance();

        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);

        String reject3DCard=commRequestVO.getReject3DCard();
        String is3DSecureRequest=account.get_3DSupportAccount();
        String isCurrencyConversion=commRequestVO.getCurrencyConversion();
        String conversionCurrency=commRequestVO.getConversionCurrency();
        String isTestWithSimulator=gatewayAccountVO.getIsTestWithSimulator();
        String terminalId=genericTransDetailsVO.getTerminalId();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionLogger.error("from RB----"+termUrl);
        }

        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=genericAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=genericAddressDetailsVO.getTmpl_currency();
        }
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }

        transactionLogger.error("is3DSecureRequest:"+is3DSecureRequest);
        transactionLogger.error("reject3DCard:"+reject3DCard);
        transactionLogger.error("isTestWithSimulator:"+isTestWithSimulator);
        transactionLogger.error("isCurrencyConversion:"+isCurrencyConversion);
        transactionLogger.error("conversionCurrency:"+conversionCurrency);
        transactionLogger.error("terminalId:"+terminalId);
        transactionLogger.error("attemptThreeD:" + attemptThreeD);

        String transactionCurrency=genericTransDetailsVO.getCurrency();
        String transactionAmount=genericTransDetailsVO.getAmount();
        if("Y".equals(isCurrencyConversion)){
            Double exchangeRate=null;
            if(functions.isValueNull(conversionCurrency)){
                exchangeRate=payneticsUtils.getExchangeRate(transactionCurrency, conversionCurrency);
            }
            else{
                transactionLogger.error("rejecting transaction because conversion currency has not defined");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription("Conversion currency has not defined");
                commResponseVO.setRemark("Conversion currency has not defined");
                return commResponseVO;
            }
            if(exchangeRate!=null){
                transactionAmount=Functions.round(Double.valueOf(exchangeRate)*Double.valueOf(genericTransDetailsVO.getAmount()),2);
                transactionCurrency=conversionCurrency;
                TerminalManager terminalManager=new TerminalManager();
                TerminalVO terminalVO=terminalManager.getMemberTerminalfromTerminal(terminalId);
                TerminalVO terminalVO1=terminalManager.getMemberTerminalDetailsForTerminalChange(terminalVO.getMemberId(),terminalVO.getPaymodeId(),terminalVO.getCardTypeId(),transactionCurrency);
                if(terminalVO1!=null){
                    GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(terminalVO1.getAccountId());
                    payneticsUtils.changeTerminalInfo(transactionAmount, transactionCurrency,String.valueOf(gatewayAccount.getAccountId()),gatewayAccount.getMerchantId(),genericTransDetailsVO.getAmount(),genericTransDetailsVO.getCurrency(),trackingId,terminalVO1.getTerminalId());
                    commResponseVO.setAmount(transactionAmount);
                }else{
                    transactionLogger.error("rejecting transaction because "+transactionCurrency+" terminal not defined");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescriptor(account.getDisplayName());
                    commResponseVO.setTransactionId(bankApprovedID);
                    commResponseVO.setDescription(transactionCurrency+" terminal not defined");
                    commResponseVO.setRemark(transactionCurrency+" terminal not defined");
                    return commResponseVO;
                }
            }else{
                transactionLogger.error("rejecting transaction because exchange rates not found");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription("Exchange rate has not been defined");
                commResponseVO.setRemark("Exchange rate has not been defined");
                return commResponseVO;
            }
            transactionLogger.error("FromCurrency:"+genericTransDetailsVO.getCurrency());
            transactionLogger.error("transactionCurrency:"+transactionCurrency);
            transactionLogger.error("ExChangeRate:"+exchangeRate);
            transactionLogger.error("transactionAmount:"+transactionAmount);
        }

        boolean _3DDataReceived = false;
        if (functions.isValueNull(genericTransDetailsVO.getEci()) && functions.isValueNull(genericTransDetailsVO.getXid()) && functions.isValueNull(genericTransDetailsVO.getVerificationId()))
        {
            requestECI = genericTransDetailsVO.getEci();
            requestXID = genericTransDetailsVO.getXid();
            requestCAVV = genericTransDetailsVO.getVerificationId();
            _3DDataReceived = true;
        }

        if ("Y".equals(is3DSecureRequest) && !("Direct".equalsIgnoreCase(attemptThreeD)) && !_3DDataReceived)
        {
            EnrollmentRequestVO enrollmentRequestVO=new EnrollmentRequestVO();
            enrollmentRequestVO.setMid(gatewayAccountVO.getMpiMid());
            enrollmentRequestVO.setName(genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
            enrollmentRequestVO.setPan(genericCardDetailsVO.getCardNum());
            enrollmentRequestVO.setExpiry(payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear()));
            enrollmentRequestVO.setCurrency(CurrencyCodeISO4217.getNumericCurrencyCode(transactionCurrency));
            enrollmentRequestVO.setAmount(payneticsUtils.getCentAmount(transactionAmount));
            enrollmentRequestVO.setDesc(genericTransDetailsVO.getOrderDesc());
            enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
            enrollmentRequestVO.setAccept("en-us");
            enrollmentRequestVO.setTrackid(trackingId);

            EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
            EnrollmentResponseVO enrollmentResponseVO=endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

            String result=enrollmentResponseVO.getResult();
            String avr=enrollmentResponseVO.getAvr();

            if ("Enrolled".equals(result) && ("Y".equals(avr) || "C".equalsIgnoreCase(avr)))
            {
                if("Y".equals(reject3DCard)){
                    transactionLogger.error("rejecting 3d card as per configuration ");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescriptor(account.getDisplayName());
                    commResponseVO.setTransactionId(bankApprovedID);
                    commResponseVO.setDescription("3D Enrolled Card");
                    commResponseVO.setRemark("3D Enrolled Card");
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    return commResponseVO;
                }else{
                    transactionLogger.error("3D:card enrolled flow");
                    String PAReq=enrollmentResponseVO.getPAReq();
                    String acsUrl=enrollmentResponseVO.getAcsUrl();
                    try{
                        acsUrl= java.net.URLDecoder.decode(acsUrl, "UTF-8");
                    }
                    catch(UnsupportedEncodingException e){
                        transactionLogger.error("UnsupportedEncodingException:::::"+e);
                        PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                    }
                    transactionLogger.error("pareq:::"+PAReq);
                    transactionLogger.error("url:::"+acsUrl);
                    String status="pending3DConfirmation";
                    if (functions.isValueNull(genericCardDetailsVO.getcVV()))
                    {
                        encryptedCVV = PzEncryptor.encryptCVV(genericCardDetailsVO.getcVV());
                    }
                    Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
                    comm3DResponseVO.setPaReq(PAReq);
                    comm3DResponseVO.setUrlFor3DRedirect(acsUrl);
                    comm3DResponseVO.setMd(encryptedCVV);
                    comm3DResponseVO.setRedirectMethod("POST");
                    comm3DResponseVO.setTerURL(termUrl+trackingId);
                    comm3DResponseVO.setStatus(status);
                    comm3DResponseVO.setDescriptor(account.getDisplayName());
                    comm3DResponseVO.setTransactionType("auth");
                    comm3DResponseVO.setCurrency(currency);
                    comm3DResponseVO.setTmpl_Amount(tmpl_amount);
                    comm3DResponseVO.setTmpl_Currency(tmpl_currency);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setThreeDVersion(enrollmentResponseVO.getThreeDVersion());
                    return comm3DResponseVO;
                }
            }
            else if ("Only3D".equalsIgnoreCase(attemptThreeD))
            {
                transactionLogger.error("Only 3D Card Required");
                commResponseVO.setStatus("failed");
                commResponseVO.setDescriptor(account.getDisplayName());
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescription("Only 3D Card Required");
                commResponseVO.setRemark("Only 3D Card Required");
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                return commResponseVO;
            }
            else
            {
                transactionLogger.error("3D:card not enrolled flow");
                String MTI = payneticsUtils.getMTI(transactionType);
                String P002 = genericCardDetailsVO.getCardNum();
                String P003 = payneticsUtils.getProcessingCode(transactionType);
                String P004 = payneticsUtils.getCentAmount(transactionAmount);
                if(transactionCurrency.equalsIgnoreCase("JPY"))
                    P004 = payneticsUtils.getJPYAmount(transactionAmount);
                String P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                String P012 = payneticsUtils.getLocalTime(calendar);
                String P013 = payneticsUtils.getLocalDate(calendar);
                String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
                String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
                String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
                String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
                String P041 = gatewayAccountVO.getTerminalId();
                String P042 = gatewayAccountVO.getMerchantId();
                String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
                String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
                String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(transactionCurrency);

                String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
                String P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();
                String electronicCommerceIndicator = payneticsUtils.getECI(P046);

                String P060 = "";
                if(functions.isValueNull(genericCardDetailsVO.getcVV()))//OK
                {
                    String P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(genericCardDetailsVO.getcVV(), "30");//CVV
                    if(functions.isValueNull(P060_30)){
                        P060=P060+P060_30;
                    }
                }
                if (functions.isValueNull(trackingId))//OK
                {
                    String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                    if(functions.isValueNull(P060_35)){
                        P060=P060+P060_35;
                    }
                }
                if(functions.isValueNull(electronicCommerceIndicator))//OK
                {
                    String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                    if(functions.isValueNull(P060_40)){
                        P060=P060+P060_40;
                    }
                }
                if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
                {
                    String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                    if(functions.isValueNull(P060_81)){
                        P060=P060+P060_81;
                    }
                }
                if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
                {
                    String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                    if(functions.isValueNull(P060_82)){
                        P060=P060+P060_82;
                    }
                }
                if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
                {
                    String P060_83_1="";
                    String P060_83_2="";
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                        P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
                    }else{
                        P060_83_1=String.format("%-15s",P060_83_1);
                    }
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                        P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
                    }else{
                        P060_83_2=String.format("%-4s",P060_83_2);
                    }
                    String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                    if(functions.isValueNull(P060_83)){
                        P060=P060+P060_83;
                    }
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
                    String P060_84_1="";
                    String P060_84_2="";
                    String P060_84_3="";

                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                        P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
                    }else{
                        P060_84_1=String.format("%-10s",P060_84_1);
                    }
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                        P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
                    }else{
                        P060_84_2=String.format("%-25s",P060_84_2);
                    }
                    if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                        P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
                    }else{
                        P060_84_3=String.format("%-13s",P060_84_3);
                    }
                    String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                    if(functions.isValueNull(P060_84)){
                        P060=P060+P060_84;
                    }
                }
                String P060_87_1 = String.format("%-1s", "1");
                if (functions.isValueNull(P060_87_1))
                {
                    P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                    if (functions.isValueNull(P060_87_1))
                    {
                        String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                        if (functions.isValueNull(P060_87))
                        {
                            P060 = P060 + P060_87;
                        }
                    }
                }

                HashMap<String,String> hashMap=new HashMap();
                hashMap.put("MTYP",MTI);
                hashMap.put("P002",P002);
                hashMap.put("P003",P003);
                hashMap.put("P004",P004);
                hashMap.put("P011", P011);
                hashMap.put("P012",P012);
                hashMap.put("P013",P013);
                hashMap.put("P014",P014);
                hashMap.put("P017",P017);
                hashMap.put("P022",P022);
                hashMap.put("P025",P025);
                hashMap.put("P032",P032);
                hashMap.put("P041",P041);
                hashMap.put("P042",P042);
                hashMap.put("P043",P043);
                hashMap.put("P046",P046);
                hashMap.put("P049",P049);
                hashMap.put("P057", P057);
                hashMap.put("P060",P060);
                hashMap.put("P063",P063);

                //record the data.
                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                if (inserted)
                {
                    transactionLogger.error("Recorded channel request:" + inserted);
                }

                String status="";
                String statusDescription="";
                String remark="";
                String descriptor="";
                String responseTime="";
                try{
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
                    GenericPackager packager = new GenericPackager(inputStream);
                    ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
                    try
                    {
                        ISOMsg _100_ResMessage=null;
                        String env=RB.getString("Env");
                        if("Y".equals(isTestWithSimulator)){
                            env="development";
                        }
                        if("development".equals(env)){
                            _100_ResMessage = payneticsUtils.get_0100_SampleResponseMsg(isoMsg);
                            if(_100_ResMessage!=null){
                                String responseCode=_100_ResMessage.getString("39");
                                if("00".equalsIgnoreCase(responseCode)){
                                    status="success";
                                    descriptor=account.getDisplayName();
                                }
                                else
                                {
                                    status="failed";
                                    descriptor="";
                                    errorName = getErrorName(responseCode);
                                }
                                payneticsUtils.updatePayneticsDetails(trackingId, _100_ResMessage.getString("38"), _100_ResMessage.getString("39"), _100_ResMessage.getString("44"), P004);
                                bankApprovedID=_100_ResMessage.getString("38");
                                statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                                remark=PayneticsErrorCodes.getErrorCode(responseCode);
                            }
                        }
                        else{
                            PzNACChannel channel = null;
                            Logger logger = new Logger();
                            logger.addListener(new SimpleLogListener(System.out));
                            if (isTest)
                            {
                            /*1)Send direct msg to paynetics.
                            *2)or Send msg to primary TQMux.
                            *3)or Send msg to DR TQMux when primary VPN is down.
                            * */
                                if ("Y".equals(isQMuxActive))
                                {
                                    channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        try
                                        {
                                            channel.connect();
                                        }
                                        catch (IOException e1)
                                        {
                                            transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                            channel.setLogger(logger, "test-channel");
                                            channel.connect();
                                        }
                                    }
                                }
                                else
                                {
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary Test VPN is down,connecting to DR TQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                            /*1)Send direct msg to paynetics.
                            *2)Send msg to primary LQMux
                            *3)or Send msg to DR LQMux when primary VPN is down.
                            * */
                                if ("Y".equals(isQMuxActive))
                                {
                                    channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        try
                                        {
                                            channel.connect();
                                        }
                                        catch (IOException e1)
                                        {
                                            transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                            channel.setLogger(logger, "live-channel");
                                            channel.connect();
                                        }
                                    }
                                }
                                else
                                {
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e)
                                    {
                                        transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                        channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        channel.connect();
                                    }
                                }
                            }

                            channel.send(isoMsg);
                            _100_ResMessage =channel.receive ();
                            channel.disconnect();
                            if(_100_ResMessage!=null){
                                String responseCode=_100_ResMessage.getString("39");
                                if("00".equalsIgnoreCase(responseCode)){
                                    status="success";
                                    descriptor=account.getDisplayName();
                                }
                                else if("06".equals(_100_ResMessage.getString("39"))){
                                    status="pending";
                                    descriptor="";
                                }else{
                                    status="failed";
                                    descriptor="";
                                    errorName = getErrorName(responseCode);
                                }
                                if("pending".equals(status)){
                                    payneticsUtils.updatePayneticsDetails(trackingId, _100_ResMessage.getString("38"), _100_ResMessage.getString("39"), _100_ResMessage.getString("44"), P004);
                                    nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);

                                    P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                                    P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

                                    isoMsg.set("11", P011);
                                    isoMsg.set("57",P057);

                                    inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                    if (inserted)
                                    {
                                        transactionLogger.error("Recorded channel request:" + inserted);
                                    }

                                    channel.connect ();
                                    channel.send(isoMsg);
                                    _100_ResMessage =channel.receive ();
                                    channel.disconnect();
                                    responseCode=_100_ResMessage.getString("39");
                                    if("00".equalsIgnoreCase(responseCode)){
                                        status="success";
                                        descriptor=account.getDisplayName();
                                    }else{
                                        status="failed";
                                        descriptor="";
                                        errorName = getErrorName(responseCode);
                                    }
                                }
                                payneticsUtils.updatePayneticsDetails(trackingId, _100_ResMessage.getString("38"), _100_ResMessage.getString("39"), _100_ResMessage.getString("44"), P004);
                                bankApprovedID=_100_ResMessage.getString("38");
                                statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                                remark=PayneticsErrorCodes.getErrorCode(responseCode);
                            }
                        }
                    }
                    catch (ISOException e){
                        status="failed";
                        statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                        remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                        transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
                    }
                }
                catch(IOException e){
                    PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
                catch (ISOException e){
                    PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }

                commResponseVO.setStatus(status);
                commResponseVO.setTransactionId(bankApprovedID);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setDescription(statusDescription);
                commResponseVO.setRemark(remark);
                commResponseVO.setResponseTime(responseTime);
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                if (functions.isValueNull(errorName))
                    commResponseVO.setErrorName(errorName);
                return commResponseVO;
            }
        }
        else{
            String electronicCommerceIndicator = "";
            if (_3DDataReceived && functions.isValueNull(requestECI))
            {
                if ("05".equals(requestECI))
                {
                    electronicCommerceIndicator = "10";
                }
                else if ("06".equals(requestECI))
                {
                    electronicCommerceIndicator = "12";
                }
                else if ("02".equals(requestECI))
                {
                    electronicCommerceIndicator = "11";
                }
                else if ("01".equals(requestECI))
                {
                    electronicCommerceIndicator = "13";
                }
            }
            else
            {
                electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());
            }

            String MTI = payneticsUtils.getMTI(transactionType);
            String P002 = genericCardDetailsVO.getCardNum();
            String P003 = payneticsUtils.getProcessingCode(transactionType);
            String P004 = payneticsUtils.getCentAmount(transactionAmount);
            if(transactionCurrency.equalsIgnoreCase("JPY"))
                P004 = payneticsUtils.getJPYAmount(transactionAmount);
            String P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
            String P012 = payneticsUtils.getLocalTime(calendar);
            String P013 = payneticsUtils.getLocalDate(calendar);
            String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
            String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
            String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
            String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
            String P041 = gatewayAccountVO.getTerminalId();
            String P042 = gatewayAccountVO.getMerchantId();
            String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
            String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
            String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(transactionCurrency);

            String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
            String P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

            String P060 = "";
            if(functions.isValueNull(genericCardDetailsVO.getcVV()))//OK
            {
                String P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(genericCardDetailsVO.getcVV(), "30");//CVV
                if(functions.isValueNull(P060_30)){
                    P060=P060+P060_30;
                }
            }
            if (functions.isValueNull(trackingId))//OK
            {
                String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                if(functions.isValueNull(P060_35)){
                    P060=P060+P060_35;
                }
            }
            if(functions.isValueNull(electronicCommerceIndicator))//OK
            {
                String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                if(functions.isValueNull(P060_40)){
                    P060=P060+P060_40;
                }
            }

            if (functions.isValueNull(requestXID) && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                String P060_61 = payneticsUtils.getDATA_IN_LTV_FORMAT(requestXID, "61");//xid
                if (functions.isValueNull(P060_61))
                {
                    P060 = P060 + P060_61;
                }
            }

            if (functions.isValueNull(requestCAVV) && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                String P060_62 = payneticsUtils.getDATA_IN_LTV_FORMAT(requestCAVV, "62");//CAVV
                if (functions.isValueNull(P060_62))
                {
                    P060 = P060 + P060_62;
                }
            }
            else if (functions.isValueNull(requestCAVV) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_63 = payneticsUtils.getDATA_IN_LTV_FORMAT(requestCAVV, "63");//UCAF
                if (functions.isValueNull(P060_63))
                {
                    P060 = P060 + P060_63;
                }
            }

            if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                if(functions.isValueNull(P060_81)){
                    P060=P060+P060_81;
                }
            }
            if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                if(functions.isValueNull(P060_82)){
                    P060=P060+P060_82;
                }
            }
            if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
            {
                String P060_83_1="";
                String P060_83_2="";
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                    P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
                }else{
                    P060_83_1=String.format("%-15s",P060_83_1);
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                    P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
                }else{
                    P060_83_2=String.format("%-4s",P060_83_2);
                }
                String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                if(functions.isValueNull(P060_83)){
                    P060=P060+P060_83;
                }
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
                String P060_84_1="";
                String P060_84_2="";
                String P060_84_3="";
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                    P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
                }else{
                    P060_84_1=String.format("%-10s",P060_84_1);
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                    P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
                }else{
                    P060_84_2=String.format("%-25s",P060_84_2);
                }
                if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                    P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
                }else{
                    P060_84_3=String.format("%-13s",P060_84_3);
                }
                String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                if(functions.isValueNull(P060_84)){
                    P060=P060+P060_84;
                }
            }

            String P060_87_1 = String.format("%-1s", "1");
            if (functions.isValueNull(P060_87_1))
            {
                P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                if (functions.isValueNull(P060_87_1))
                {
                    String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                    if (functions.isValueNull(P060_87))
                    {
                        P060 = P060 + P060_87;
                    }
                }
            }

            HashMap<String,String> hashMap=new HashMap();
            hashMap.put("MTYP",MTI);
            hashMap.put("P002",P002);
            hashMap.put("P003",P003);
            hashMap.put("P004",P004);
            hashMap.put("P011",P011);
            hashMap.put("P012",P012);
            hashMap.put("P013",P013);
            hashMap.put("P014",P014);
            hashMap.put("P017",P017);
            hashMap.put("P022",P022);
            hashMap.put("P025",P025);
            hashMap.put("P032",P032);
            hashMap.put("P041",P041);
            hashMap.put("P042",P042);
            hashMap.put("P043",P043);
            hashMap.put("P046",P046);
            hashMap.put("P049",P049);
            hashMap.put("P057",P057);
            hashMap.put("P060",P060);
            hashMap.put("P063",P063);

            //record the data.
            boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
            if (inserted)
            {
                transactionLogger.error("Recorded channel request:" + inserted);
            }

            String status="";
            String statusDescription="";
            String remark="";
            String descriptor="";
            String responseTime="";
            try{
                InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
                GenericPackager packager = new GenericPackager(inputStream);
                ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
                try{
                    ISOMsg _100_ResMessage=null;
                    String env=RB.getString("Env");
                    if("Y".equals(isTestWithSimulator)){
                        env="development";
                    }
                    if ("development".equals(env))
                    {
                        _100_ResMessage = payneticsUtils.get_0100_SampleResponseMsg(isoMsg);
                        if(_100_ResMessage!=null){
                            String responseCode=_100_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }
                            else
                            {
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                            payneticsUtils.updatePayneticsDetails(trackingId, _100_ResMessage.getString("38"), _100_ResMessage.getString("39"), _100_ResMessage.getString("44"), P004);
                            bankApprovedID=_100_ResMessage.getString("38");
                            statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                            remark=PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                    else
                    {
                        PzNACChannel channel = null;
                        Logger logger = new Logger();
                        logger.addListener(new SimpleLogListener(System.out));
                        if (isTest)
                        {
                            /*1)Send direct msg to paynetics.
                            *2)or Send msg to primary TQMux.
                            *3)or Send msg to DR TQMux when primary VPN is down.
                            * */
                            if ("Y".equals(isQMuxActive))
                            {
                                channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e1)
                                    {
                                        transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                        channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary Test VPN is down,connecting to DR TQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            /*1)Send direct msg to paynetics.
                            *2)Send msg to primary LQMux
                            *3)or Send msg to DR LQMux when primary VPN is down.
                            * */
                            if ("Y".equals(isQMuxActive))
                            {
                                channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e1)
                                    {
                                        transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                        channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }

                        channel.send(isoMsg);
                        _100_ResMessage =channel.receive ();
                        channel.disconnect();
                        if(_100_ResMessage!=null){
                            String responseCode=_100_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }
                            else if ("06".equals(_100_ResMessage.getString("39")))
                            {
                                status = "pending";
                                descriptor = "";
                            }
                            else
                            {
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                            if ("pending".equals(status))
                            {
                                payneticsUtils.updatePayneticsDetails(trackingId, _100_ResMessage.getString("38"), _100_ResMessage.getString("39"), _100_ResMessage.getString("44"), P004);
                                nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);

                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57", P057);

                                inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }
                                channel.connect();
                                channel.send(isoMsg);
                                _100_ResMessage = channel.receive();
                                channel.disconnect();
                                responseCode = _100_ResMessage.getString("39");
                                if ("00".equalsIgnoreCase(responseCode))
                                {
                                    status = "success";
                                    descriptor = account.getDisplayName();
                                }
                                else
                                {
                                    status = "failed";
                                    descriptor = "";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            payneticsUtils.updatePayneticsDetails(trackingId, _100_ResMessage.getString("38"), _100_ResMessage.getString("39"), _100_ResMessage.getString("44"), P004);
                            bankApprovedID=_100_ResMessage.getString("38");
                            statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                            remark=PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                }
                catch (ISOException e){
                    status="failed";
                    statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                    remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                    transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
                }
            }
            catch(IOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (ISOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            commResponseVO.setTransactionType(transactionType);
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionId(bankApprovedID);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(statusDescription);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(responseTime);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            if (functions.isValueNull(errorName))
                commResponseVO.setErrorName(errorName);
            return commResponseVO;
        }
    }
    public GenericResponseVO processRefund(String trackingId, GenericRequestVO requestVO)throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String transactionType="refund";
        String bankApprovalId="";
        String P017="00";
        String P063="3";
        String currency="";
        String errorName = "";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();

        Functions functions = new Functions();
        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Calendar calendar = Calendar.getInstance();

        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);

        String isTestWithSimulator=gatewayAccountVO.getIsTestWithSimulator();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();
        transactionLogger.error("isTestWithSimulator:::::"+isTestWithSimulator);

        String MTI =payneticsUtils.getMTI(transactionType);
        String P002=genericCardDetailsVO.getCardNum();
        String P003=payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(genericTransDetailsVO.getAmount());
        if(genericTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(genericTransDetailsVO.getAmount());
        }
        String P011=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(),gatewayAccountVO.getTerminalId());
        String P012=payneticsUtils.getLocalTime(calendar);
        String P013=payneticsUtils.getLocalDate(calendar);
        String P014=payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
        String P022=payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025=payneticsUtils.getPOSConditionalCode(transactionType);
        String P032=gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P041=gatewayAccountVO.getTerminalId();
        String P042=gatewayAccountVO.getMerchantId();
        String P043=gatewayAccountVO.getCardAcceptorNameLocation();
        String P046=payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049=CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());
        String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
        String P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }

        String P060="";
        if(functions.isValueNull(gatewayAccountVO.getAdditionalMerchantData()))//OK
        {
            String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(gatewayAccountVO.getAdditionalMerchantData(), "35");//Additional Merchant Data
            if(functions.isValueNull(P060_35)){
                P060=P060+P060_35;
            }
        }
        if(functions.isValueNull(electronicCommerceIndicator))//OK
        {
            String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
            if(functions.isValueNull(P060_40)){
                P060=P060+P060_40;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
            if(functions.isValueNull(P060_81)){
                P060=P060+P060_81;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
            if(functions.isValueNull(P060_82)){
                P060=P060+P060_82;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
        {
            String P060_83_1="";
            String P060_83_2="";
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
            }else{
                P060_83_1=String.format("%-15s",P060_83_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
            }else{
                P060_83_2=String.format("%-4s",P060_83_2);
            }
            String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
            if(functions.isValueNull(P060_83)){
                P060=P060+P060_83;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
            String P060_84_1="";
            String P060_84_2="";
            String P060_84_3="";

            if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
            }else{
                P060_84_1=String.format("%-10s",P060_84_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
            }else{
                P060_84_2=String.format("%-25s",P060_84_2);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
            }else{
                P060_84_3=String.format("%-13s",P060_84_3);
            }
            String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
            if(functions.isValueNull(P060_84)){
                P060=P060+P060_84;
            }
        }
        String P060_87_1 = String.format("%-1s", "1");
        if (functions.isValueNull(P060_87_1))
        {
            P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
            if (functions.isValueNull(P060_87_1))
            {
                String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                if (functions.isValueNull(P060_87))
                {
                    P060 = P060 + P060_87;
                }
            }
        }

        HashMap<String,String> hashMap=new HashMap();
        hashMap.put("MTYP",MTI);
        hashMap.put("P002",P002);
        hashMap.put("P003",P003);
        hashMap.put("P004",P004);
        hashMap.put("P011",P011);
        hashMap.put("P012",P012);
        hashMap.put("P013",P013);
        hashMap.put("P014",P014);
        hashMap.put("P017",P017);
        hashMap.put("P022",P022);
        hashMap.put("P025",P025);
        hashMap.put("P032",P032);
        hashMap.put("P041",P041);
        hashMap.put("P042",P042);
        hashMap.put("P043",P043);
        hashMap.put("P046",P046);
        hashMap.put("P049",P049);
        hashMap.put("P057",P057);
        hashMap.put("P060",P060);
        hashMap.put("P063",P063);

        String status="";
        String statusDescription="";
        String remark="";
        String descriptor="";
        String responseTime="";
        try{
            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
            GenericPackager packager = new GenericPackager(inputStream);
            ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
            try{
                ISOMsg _200_ResMessage=null;
                String env=RB.getString("Env");
                if("Y".equals(isTestWithSimulator)){
                    env="development";
                }
                if("development".equals(env)){
                    _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor=account.getDisplayName();
                        }
                        else
                        {
                            status="failed";
                            descriptor="";
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
                else{
                    PzNACChannel channel = null;
                    Logger logger = new Logger();
                    logger.addListener(new SimpleLogListener(System.out));
                    if (isTest)
                    {
                        /*1)Send direct msg to paynetics.
                        *2)or Send msg to primary TQMux.
                        *3)or Send msg to DR TQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary Test VPN is down,connecting to DR TQMux server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                channel.connect();
                            }
                        }
                    }
                    else
                    {
                       /*1)Send direct msg to paynetics.
                       *2)Send msg to primary LQMux
                       *3)or Send msg to DR LQMux when primary VPN is down.
                       * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                channel.connect();
                            }
                        }
                    }

                    channel.send(isoMsg);
                    _200_ResMessage =channel.receive ();
                    channel.disconnect();
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor=account.getDisplayName();
                        }
                        else if("06".equals(_200_ResMessage.getString("39"))){
                            status="pending";
                            descriptor="";
                        }else{
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        if("pending".equals(status)){
                            payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                            String nextSTAN=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            isoMsg.set("11",nextSTAN);
                            nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);
                            P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();
                            isoMsg.set("57",P057);

                            channel.connect ();
                            channel.send(isoMsg);
                            _200_ResMessage =channel.receive ();
                            channel.disconnect();
                            responseCode=_200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
            }
            catch (ISOException e){
                status="failed";
                statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
            }
        }
        catch(IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ISOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovalId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        commResponseVO.setCurrency(currency);
        return commResponseVO;
    }
    public GenericResponseVO processCapture(String trackingId, GenericRequestVO requestVO)throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String transactionType="capture";
        String bankApprovalId="";
        String errorName = "";
        String electronicCommerceIndicator = "07";
        String P017="00";
        String P063="3";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Calendar calendar = Calendar.getInstance();
        Functions functions = new Functions();

        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);

        String isTestWithSimulator=gatewayAccountVO.getIsTestWithSimulator();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();
        transactionLogger.error("isTestWithSimulator:::::"+isTestWithSimulator);

        String MTI =payneticsUtils.getMTI(transactionType);
        String P002=genericCardDetailsVO.getCardNum();
        String P003=payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(commTransDetailsVO.getAmount());
        if(commTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(commTransDetailsVO.getAmount());
        }
        String P011=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
        String P012=payneticsUtils.getLocalTime(calendar);
        String P013=payneticsUtils.getLocalDate(calendar);
        String P014=payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
        String P022=payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025=payneticsUtils.getPOSConditionalCode(transactionType);
        String P032=gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P037=payneticsUtils.getApprovedSTAN(String.valueOf(trackingId),commTransDetailsVO.getPreviousTransactionId());
        String P038=commTransDetailsVO.getPreviousTransactionId();
        String P041=gatewayAccountVO.getTerminalId();
        String P042=gatewayAccountVO.getMerchantId();
        String P043=gatewayAccountVO.getCardAcceptorNameLocation();
        String P046=payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049=CurrencyCodeISO4217.getNumericCurrencyCode(commTransDetailsVO.getCurrency());

        String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
        String P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

        String P060="";
        if(functions.isValueNull(gatewayAccountVO.getAdditionalMerchantData()))//OK
        {
            String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(gatewayAccountVO.getAdditionalMerchantData(), "35");//Additional Merchant Data
            if(functions.isValueNull(P060_35)){
                P060=P060+P060_35;
            }
        }
        if (functions.isValueNull(electronicCommerceIndicator))//OK
        {
            String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
            if(functions.isValueNull(P060_40)){
                P060=P060+P060_40;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
            if(functions.isValueNull(P060_81)){
                P060=P060+P060_81;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
            if(functions.isValueNull(P060_82)){
                P060=P060+P060_82;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
        {
            String P060_83_1="";
            String P060_83_2="";
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
            }else{
                P060_83_1=String.format("%-15s",P060_83_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
            }else{
                P060_83_2=String.format("%-4s",P060_83_2);
            }
            String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
            if(functions.isValueNull(P060_83)){
                P060=P060+P060_83;
            }
        }

        if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
            String P060_84_1="";
            String P060_84_2="";
            String P060_84_3="";

            if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
            }else{
                P060_84_1=String.format("%-10s",P060_84_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
            }else{
                P060_84_2=String.format("%-25s",P060_84_2);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
            }else{
                P060_84_3=String.format("%-13s",P060_84_3);
            }
            String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
            if(functions.isValueNull(P060_84)){
                P060=P060+P060_84;
            }
        }

        String P060_87_1 = String.format("%-1s", "1");
        if (functions.isValueNull(P060_87_1))
        {
            P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
            if (functions.isValueNull(P060_87_1))
            {
                String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                if (functions.isValueNull(P060_87))
                {
                    P060 = P060 + P060_87;
                }
            }
        }

        HashMap<String,String> hashMap=new HashMap();
        hashMap.put("MTYP",MTI);
        hashMap.put("P002",P002);
        hashMap.put("P003",P003);
        hashMap.put("P004",P004);
        hashMap.put("P011",P011);
        hashMap.put("P012",P012);
        hashMap.put("P013",P013);
        hashMap.put("P014",P014);
        hashMap.put("P017",P017);
        hashMap.put("P022",P022);
        hashMap.put("P025",P025);
        hashMap.put("P032",P032);
        hashMap.put("P037",P037);
        hashMap.put("P038",P038);
        hashMap.put("P041",P041);
        hashMap.put("P042",P042);
        hashMap.put("P043",P043);
        hashMap.put("P046",P046);
        hashMap.put("P049",P049);
        hashMap.put("P057",P057);
        hashMap.put("P060",P060);
        hashMap.put("P063",P063);

        String status="";
        String statusDescription="";
        String remark="";
        String descriptor="";
        String responseTime="";
        try
        {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
            GenericPackager packager = new GenericPackager(inputStream);
            ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
            try{
                ISOMsg _200_ResMessage=null;
                String env=RB.getString("Env");
                if("Y".equals(isTestWithSimulator)){
                    env="development";
                }
                if("development".equals(env)){
                    _200_ResMessage = payneticsUtils.get_0220_SampleResponseMsg(isoMsg);
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor="";
                        }
                        else
                        {
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
                else{
                    PzNACChannel channel = null;
                    Logger logger = new Logger();
                    logger.addListener(new SimpleLogListener(System.out));
                    if (isTest)
                    {
                        /*1)Send direct msg to paynetics.
                        *2)or Send msg to primary TQMux.
                        *3)or Send msg to DR TQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary Test VPN is down connecting... to DR TQMux Server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                channel.connect();
                            }
                        }
                    }
                    else
                    {
                        /*1)Send direct msg to paynetics.
                        *2)Send msg to primary LQMux
                        *3)or Send msg to DR LQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                channel.connect();
                            }
                        }
                    }

                    channel.send(isoMsg);
                    _200_ResMessage =channel.receive ();
                    channel.disconnect();
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor = "";
                        }
                        else if("06".equals(_200_ResMessage.getString("39"))){
                            status="pending";
                            descriptor="";
                        }else{
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        if("pending".equals(status)){
                            payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                            String nextSTAN=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            isoMsg.set("11",nextSTAN);
                            nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);
                            P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();
                            isoMsg.set("57",P057);

                            channel.connect ();
                            channel.send(isoMsg);
                            _200_ResMessage =channel.receive ();
                            channel.disconnect();

                            responseCode=_200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
            }
            catch (ISOException e){
                status="failed";
                statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
            }
        }
        catch(IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ISOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovalId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        if (functions.isValueNull(errorName))
            commResponseVO.setErrorName(errorName);
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingId, GenericRequestVO requestVO)throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        String transactionType="reversal";
        String bankApprovalId="";
        String P017="00";
        String P063="3";
        String errorName = "";
        String electronicCommerceIndicator = "07";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Calendar calendar = Calendar.getInstance();
        Functions functions = new Functions();

        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);

        String isTestWithSimulator=gatewayAccountVO.getIsTestWithSimulator();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();
        transactionLogger.error("isTestWithSimulator:::::"+isTestWithSimulator);

        String MTI =payneticsUtils.getMTI(transactionType);
        String P002=genericCardDetailsVO.getCardNum();
        String P003=payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(commTransDetailsVO.getAmount());
        if(commTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(commTransDetailsVO.getAmount());
        }
        String P011=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
        String P012=payneticsUtils.getLocalTime(calendar);
        String P013=payneticsUtils.getLocalDate(calendar);
        String P014=payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
        String P022=payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025=payneticsUtils.getPOSConditionalCode(transactionType);
        String P032=gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P037=payneticsUtils.getApprovedSTAN(String.valueOf(trackingId), commTransDetailsVO.getPreviousTransactionId());
        String P038=commTransDetailsVO.getPreviousTransactionId();
        String P041=gatewayAccountVO.getTerminalId();
        String P042=gatewayAccountVO.getMerchantId();
        String P043=gatewayAccountVO.getCardAcceptorNameLocation();
        String P046=payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049=CurrencyCodeISO4217.getNumericCurrencyCode(commTransDetailsVO.getCurrency());

        String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
        String P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

        String P060="";
        if(functions.isValueNull(gatewayAccountVO.getAdditionalMerchantData()))//OK
        {
            String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(gatewayAccountVO.getAdditionalMerchantData(), "35");//Additional Merchant Data
            if(functions.isValueNull(P060_35)){
                P060=P060+P060_35;
            }
        }
        if (functions.isValueNull(electronicCommerceIndicator))//OK
        {
            String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
            if(functions.isValueNull(P060_40)){
                P060=P060+P060_40;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
            if(functions.isValueNull(P060_81)){
                P060=P060+P060_81;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
            if(functions.isValueNull(P060_82)){
                P060=P060+P060_82;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
        {
            String P060_83_1="";
            String P060_83_2="";
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
            }else{
                P060_83_1=String.format("%-15s",P060_83_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
            }else{
                P060_83_2=String.format("%-4s",P060_83_2);
            }
            String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
            if(functions.isValueNull(P060_83)){
                P060=P060+P060_83;
            }
        }

        if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
            String P060_84_1="";
            String P060_84_2="";
            String P060_84_3="";

            if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
            }else{
                P060_84_1=String.format("%-10s",P060_84_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
            }else{
                P060_84_2=String.format("%-25s",P060_84_2);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
            }else{
                P060_84_3=String.format("%-13s",P060_84_3);
            }
            String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
            if(functions.isValueNull(P060_84)){
                P060=P060+P060_84;
            }
        }

        String P060_87_1 = String.format("%-1s", "1");
        if (functions.isValueNull(P060_87_1))
        {
            P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
            if (functions.isValueNull(P060_87_1))
            {
                String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                if (functions.isValueNull(P060_87))
                {
                    P060 = P060 + P060_87;
                }
            }
        }

        HashMap<String,String> hashMap=new HashMap();
        hashMap.put("MTYP",MTI);
        hashMap.put("P002",P002);
        hashMap.put("P003",P003);
        hashMap.put("P004",P004);
        hashMap.put("P011",P011);
        hashMap.put("P012",P012);
        hashMap.put("P013",P013);
        hashMap.put("P014",P014);
        hashMap.put("P017",P017);
        hashMap.put("P022",P022);
        hashMap.put("P025",P025);
        hashMap.put("P032",P032);
        hashMap.put("P037",P037);
        hashMap.put("P038",P038);
        hashMap.put("P041",P041);
        hashMap.put("P042",P042);
        hashMap.put("P043",P043);
        hashMap.put("P046",P046);
        hashMap.put("P049",P049);
        hashMap.put("P057",P057);
        hashMap.put("P060",P060);
        hashMap.put("P063",P063);

        String status="";
        String statusDescription="";
        String remark="";
        String descriptor="";
        String responseTime="";
        try
        {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
            GenericPackager packager = new GenericPackager(inputStream);
            ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
            try{
                ISOMsg _200_ResMessage=null;
                String env=RB.getString("Env");
                if("Y".equals(isTestWithSimulator)){
                    env="development";
                }
                if("development".equals(env)){
                    _200_ResMessage = payneticsUtils.get_0400_SampleResponseMsg(isoMsg);
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor="";
                        }
                        else
                        {
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
                else{
                    PzNACChannel channel = null;
                    Logger logger = new Logger();
                    logger.addListener(new SimpleLogListener(System.out));
                    if (isTest)
                    {
                        /*1)Send direct msg to paynetics.
                        *2)or Send msg to primary TQMux.
                        *3)or Send msg to DR TQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary Test VPN is down connecting... to DR TQMux Server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                channel.connect();
                            }
                        }
                    }
                    else
                    {
                       /*1)Send direct msg to paynetics.
                       *2)Send msg to primary LQMux
                       *3)or Send msg to DR LQMux when primary VPN is down.
                       * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                channel.connect();
                            }
                        }
                    }

                    channel.send(isoMsg);
                    _200_ResMessage =channel.receive ();
                    channel.disconnect();
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor = "";
                        }
                        else if("06".equals(_200_ResMessage.getString("39"))){
                            status="pending";
                            descriptor="";
                        }else{
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        if("pending".equals(status)){
                            payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                            String nextSTAN=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            isoMsg.set("11",nextSTAN);
                            nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);
                            P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();
                            isoMsg.set("57",P057);

                            channel.connect ();
                            channel.send(isoMsg);
                            _200_ResMessage =channel.receive ();
                            channel.disconnect();

                            responseCode=_200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
            }
            catch (ISOException e){
                status="failed";
                statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
            }
        }
        catch(IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ISOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovalId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        return commResponseVO;
    }
    public GenericResponseVO processNewRebilling(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException,PZConstraintViolationException
    {
        String transactionType="recurring";
        String indicatorForRecurring="02";
        //String cardholderNotPresent="003022";
        String bankApprovalId="";
        String P017="00";
        String P063="3";
        String P061="";
        String errorName = "";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Calendar calendar = Calendar.getInstance();
        Functions functions = new Functions();

        PayneticsGatewayAccountVO gatewayAccountVO=payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);

        String isTestWithSimulator=gatewayAccountVO.getIsTestWithSimulator();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();
        transactionLogger.error("isTestWithSimulator:::::"+isTestWithSimulator);

        String electronicCommerceIndicator=payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());
        //String expiryDate = PzEncryptor.decryptExpiryDate(genericCardDetailsVO.getExpMonth());
        //String dateArr[] = expiryDate.split("/");
        String expMonth = genericCardDetailsVO.getExpMonth();
        String year = genericCardDetailsVO.getExpYear();

        String MTI =payneticsUtils.getMTI(transactionType);
        String P002 =genericCardDetailsVO.getCardNum();
        String P003=payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(genericTransDetailsVO.getAmount());
        if(genericTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(genericTransDetailsVO.getAmount());
        }
        String P011=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(),gatewayAccountVO.getTerminalId());
        String P012=payneticsUtils.getLocalTime(calendar);
        String P013=payneticsUtils.getLocalDate(calendar);
        String P014=payneticsUtils.getCardExpiry(expMonth,year);
        String P022=payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025=payneticsUtils.getPOSConditionalCode(transactionType);
        String P032=gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P041=gatewayAccountVO.getTerminalId();
        String P042=gatewayAccountVO.getMerchantId();
        String P043=gatewayAccountVO.getCardAcceptorNameLocation();
        String P046=payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049=CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
        String P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();

        transactionLogger.error("Previous trackingid---" + genericTransDetailsVO.getPreviousTransactionId());
        P061 = payneticsUtils.getTransactionSTAMP(genericTransDetailsVO.getPreviousTransactionId());

        String P060="";
        if(functions.isValueNull(gatewayAccountVO.getAdditionalMerchantData()))//OK
        {
            String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(gatewayAccountVO.getAdditionalMerchantData(), "35");//Additional Merchant Data
            if(functions.isValueNull(P060_35)){
                P060=P060+P060_35;
            }
        }
        if(functions.isValueNull(electronicCommerceIndicator))//OK
        {
            String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
            if(functions.isValueNull(P060_40)){
                P060=P060+P060_40;
            }
        }
        if(functions.isValueNull(indicatorForRecurring))//OK
        {
            String P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT(indicatorForRecurring, "41");//Indicator For Recurring
            if(functions.isValueNull(P060_41)){
                P060=P060+P060_41;
            }
        }

        transactionLogger.error("Setting P060_52 for Recurring");

        String P060_52_2 = payneticsUtils.getDATA_IN_LTV_FORMAT("2", "02");
        String P060_52 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_52_2, "52");

        if(functions.isValueNull(P060_52)){
            P060=P060+P060_52;
        }

        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
            if(functions.isValueNull(P060_81)){
                P060=P060+P060_81;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
            if(functions.isValueNull(P060_82)){
                P060=P060+P060_82;
            }
        }
        if(functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
        {
            String P060_83_1="";
            String P060_83_2="";
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantId())){//Sub-Merchant:ID
                P060_83_1=String.format("%-15s",gatewayAccountVO.getSubMerchantId());
            }else{
                P060_83_1=String.format("%-15s",P060_83_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode())){//Sub-Merchant:MCC code
                P060_83_2=String.format("%-4s",gatewayAccountVO.getSubMerchantMccCode());
            }else{
                P060_83_2=String.format("%-4s",P060_83_2);
            }
            String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
            if(functions.isValueNull(P060_83)){
                P060=P060+P060_83;
            }
        }

        if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){
            String P060_84_1="";
            String P060_84_2="";
            String P060_84_3="";

            if(functions.isValueNull(gatewayAccountVO.getSubMerchantZip())){//Merchant Address Details-ZIP Code
                P060_84_1=String.format("%-10s",gatewayAccountVO.getSubMerchantZip());
            }else{
                P060_84_1=String.format("%-10s",P060_84_1);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantStreet())){//Merchant Address Details-Street
                P060_84_2=String.format("%-25s",gatewayAccountVO.getSubMerchantStreet());
            }else{
                P060_84_2=String.format("%-25s",P060_84_2);
            }
            if(functions.isValueNull(gatewayAccountVO.getSubMerchantCity())){//Merchant Address Details-City
                P060_84_3=String.format("%-13s",gatewayAccountVO.getSubMerchantCity());
            }else{
                P060_84_3=String.format("%-13s",P060_84_3);
            }
            String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
            if(functions.isValueNull(P060_84)){
                P060=P060+P060_84;
            }
        }

        String P060_87_1 = String.format("%-1s", "1");
        if (functions.isValueNull(P060_87_1))
        {
            P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
            if (functions.isValueNull(P060_87_1))
            {
                String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                if (functions.isValueNull(P060_87))
                {
                    P060 = P060 + P060_87;
                }
            }
        }

        HashMap<String,String> hashMap=new HashMap();
        hashMap.put("MTYP",MTI);
        hashMap.put("P002",P002);
        hashMap.put("P003",P003);
        hashMap.put("P004",P004);
        hashMap.put("P011",P011);
        hashMap.put("P012",P012);
        hashMap.put("P013",P013);
        hashMap.put("P014",P014);
        hashMap.put("P017",P017);
        hashMap.put("P022",P022);
        hashMap.put("P025",P025);
        hashMap.put("P032",P032);
        hashMap.put("P041",P041);
        hashMap.put("P042",P042);
        hashMap.put("P043",P043);
        hashMap.put("P046",P046);
        hashMap.put("P049",P049);
        hashMap.put("P057",P057);
        hashMap.put("P060",P060);
        hashMap.put("P061",P061);
        hashMap.put("P063",P063);

        String status="";
        String statusDescription="";
        String remark="";
        String descriptor="";
        String responseTime="";
        try{
            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH")+"paynetics_basic_v6.xml"));
            GenericPackager packager = new GenericPackager(inputStream);
            ISOMsg isoMsg=payneticsUtils.getISOMessage(hashMap,packager);
            try{
                ISOMsg _200_ResMessage=null;
                String env=RB.getString("Env");
                if("Y".equals(isTestWithSimulator)){
                    env="development";
                }
                if("development".equals(env)){
                    _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor=account.getDisplayName();
                        }
                        else
                        {
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
                else{
                    PzNACChannel channel = null;
                    Logger logger = new Logger();
                    logger.addListener(new SimpleLogListener(System.out));
                    if (isTest)
                    {
                        /*1)Send direct msg to paynetics.
                        *2)or Send msg to primary TQMux.
                        *3)or Send msg to DR TQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary Test VPN is down connecting... to DR TQMux Server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                channel.connect();
                            }
                        }
                    }
                    else
                    {
                        /*1)Send direct msg to paynetics.
                        *2)Send msg to primary LQMux
                        *3)or Send msg to DR LQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                channel.connect();
                            }
                        }
                    }

                    channel.send(isoMsg);
                    _200_ResMessage =channel.receive ();
                    channel.disconnect();
                    if(_200_ResMessage!=null){
                        String responseCode=_200_ResMessage.getString("39");
                        if("00".equalsIgnoreCase(responseCode)){
                            status="success";
                            descriptor=account.getDisplayName();
                        }
                        else if("06".equals(_200_ResMessage.getString("39"))){
                            status="pending";
                            descriptor="";
                        }else{
                            status="failed";
                            descriptor="";
                            errorName = getErrorName(responseCode);
                        }
                        if("pending".equals(status)){
                            payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                            String nextSTAN=payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            isoMsg.set("11",nextSTAN);
                            nextSequenceNumber=sequenceNumberSynchronizeProcess(channel,isoMsg,env);
                            P057=ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+gatewayAccountVO.getGenerationNumber();
                            isoMsg.set("57",P057);

                            channel.connect ();
                            channel.send(isoMsg);
                            _200_ResMessage =channel.receive ();
                            channel.disconnect();

                            responseCode=_200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                        }
                        payneticsUtils.insertDataNew(trackingId,_200_ResMessage.getString("11"),_200_ResMessage.getString("38"),P042,P041,P004,nextSequenceNumber,gatewayAccountVO.getGenerationNumber(),P046,_200_ResMessage.getString("39"),_200_ResMessage.getString("44"),transactionType);
                        bankApprovalId=_200_ResMessage.getString("38");
                        statusDescription=PayneticsErrorCodes.getErrorCode(responseCode);
                        remark=PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
            }
            catch (ISOException e){
                status="failed";
                statusDescription="ACQUIRER TEMPORARILY NOT REACHABLE";
                remark="ACQUIRER TEMPORARILY NOT REACHABLE";
                transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
            }
        }
        catch(IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processRebilling()", null, "common", "Technical Exception while rebilling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ISOException e){
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processRebilling()", null, "common", "Technical Exception while rebilling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovalId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        return commResponseVO;
    }
    public GenericResponseVO processRebilling(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException,PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        PayneticsUtils payneticsUtils = new PayneticsUtils();
        Functions functions = new Functions();

        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        int transid=Integer.parseInt(genericTransDetailsVO.getPreviousTransactionId());
        transactionLogger.error("transid--------->"+transid);
        boolean isTest = account.isTest();
        if(isTest)
        {
            if (transid < 2861470)
            {
                transactionLogger.error("----------Inside if(transid<2861470)condition---------");
                String trackingid = payneticsUtils.getTrackingid(commRequestVO.getCardDetailsVO().getCardNum());
                transactionLogger.error("trackingid--------->" + trackingid);
                genericTransDetailsVO.setPreviousTransactionId(trackingid);
            }
        }
        String resTranStamp = payneticsUtils.getTransactionSTAMP(genericTransDetailsVO.getPreviousTransactionId());

        if(commRequestVO.getCardDetailsVO()!=null)
        {
            transactionLogger.error("Card Number------->"+commRequestVO.getCardDetailsVO().getCardNum());
            transactionLogger.error("Expiry Date------->"+commRequestVO.getCardDetailsVO().getExpMonth()+"/"+commRequestVO.getCardDetailsVO().getExpYear());
            transactionLogger.error("Cvv------->"+commRequestVO.getCardDetailsVO().getcVV());
        }
        try
        {
            transactionLogger.error("IsTest Flag for recurring---"+isTest+"-ResTranStamp-"+resTranStamp);
            if(functions.isValueNull(resTranStamp))
                commResponseVO = (CommResponseVO)this.processNewRebilling(trackingId,commRequestVO);
            else
                commResponseVO = (CommResponseVO)processSale(trackingId, commRequestVO);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("processRebilling Error--->",e);
        }

        return commResponseVO;
    }
    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        String transactionType = "payout";
        String bankApprovalId = "";
        String P017 = "00";
        String P063 = "3";

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        CommResponseVO commResponseVO = new CommResponseVO();
        Calendar calendar = Calendar.getInstance();
        Functions functions = new Functions();

        PayneticsGatewayAccountVO gatewayAccountVO = payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String isTestWithSimulator = gatewayAccountVO.getIsTestWithSimulator();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();
        transactionLogger.error("isTestWithSimulator:::::" + isTestWithSimulator);

        String electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());

        String MTI = payneticsUtils.getMTI(transactionType);
        String P002 = genericCardDetailsVO.getCardNum();
        String P003 = payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(genericTransDetailsVO.getAmount());
        if(genericTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(genericTransDetailsVO.getAmount());
        }
        String P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
        String P012 = payneticsUtils.getLocalTime(calendar);
        String P013 = payneticsUtils.getLocalDate(calendar);
        String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
        String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
        String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P041 = gatewayAccountVO.getTerminalId();
        String P042 = gatewayAccountVO.getMerchantId();
        String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
        String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
        String P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

        String P060 = "";
        if (functions.isValueNull(trackingId))//OK
        {
            String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
            if (functions.isValueNull(P060_35))
            {
                P060 = P060 + P060_35;
            }
        }
        if (functions.isValueNull(electronicCommerceIndicator))//OK
        {
            String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
            if (functions.isValueNull(P060_40))
            {
                P060 = P060 + P060_40;
            }
        }
        if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
            if (functions.isValueNull(P060_81))
            {
                P060 = P060 + P060_81;
            }
        }
        if (functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
        {
            String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
            if (functions.isValueNull(P060_82))
            {
                P060 = P060 + P060_82;
            }
        }
        if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
        {
            String P060_83_1 = "";
            String P060_83_2 = "";
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantId()))
            {//Sub-Merchant:ID
                P060_83_1 = String.format("%-15s", gatewayAccountVO.getSubMerchantId());
            }
            else
            {
                P060_83_1 = String.format("%-15s", P060_83_1);
            }
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode()))
            {//Sub-Merchant:MCC code
                P060_83_2 = String.format("%-4s", gatewayAccountVO.getSubMerchantMccCode());
            }
            else
            {
                P060_83_2 = String.format("%-4s", P060_83_2);
            }
            String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
            if (functions.isValueNull(P060_83))
            {
                P060 = P060 + P060_83;
            }
        }
        if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
        {
            String P060_84_1 = "";
            String P060_84_2 = "";
            String P060_84_3 = "";

            if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()))
            {//Merchant Address Details-ZIP Code
                P060_84_1 = String.format("%-10s", gatewayAccountVO.getSubMerchantZip());
            }
            else
            {
                P060_84_1 = String.format("%-10s", P060_84_1);
            }
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()))
            {//Merchant Address Details-Street
                P060_84_2 = String.format("%-25s", gatewayAccountVO.getSubMerchantStreet());
            }
            else
            {
                P060_84_2 = String.format("%-25s", P060_84_2);
            }
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
            {//Merchant Address Details-City
                P060_84_3 = String.format("%-13s", gatewayAccountVO.getSubMerchantCity());
            }
            else
            {
                P060_84_3 = String.format("%-13s", P060_84_3);
            }
            String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
            if (functions.isValueNull(P060_84))
            {
                P060 = P060 + P060_84;
            }
        }

        String P060_87_1 = String.format("%-1s", "1");
        if (functions.isValueNull(P060_87_1))
        {
            P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
            if (functions.isValueNull(P060_87_1))
            {
                String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                if (functions.isValueNull(P060_87))
                {
                    P060 = P060 + P060_87;
                }
            }
        }

        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("MTYP", MTI);
        hashMap.put("P002", P002);
        hashMap.put("P003", P003);
        hashMap.put("P004", P004);
        hashMap.put("P011", P011);
        hashMap.put("P012", P012);
        hashMap.put("P013", P013);
        hashMap.put("P014", P014);
        hashMap.put("P017", P017);
        hashMap.put("P022", P022);
        hashMap.put("P025", P025);
        hashMap.put("P032", P032);
        hashMap.put("P041", P041);
        hashMap.put("P042", P042);
        hashMap.put("P043", P043);
        hashMap.put("P046", P046);
        hashMap.put("P049", P049);
        hashMap.put("P057", P057);
        hashMap.put("P060", P060);
        hashMap.put("P063", P063);

        String status = "";
        String statusDescription = "";
        String remark = "";
        String descriptor = "";
        String responseTime = "";
        try
        {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH") + "paynetics_basic_v6.xml"));
            GenericPackager packager = new GenericPackager(inputStream);
            ISOMsg isoMsg = payneticsUtils.getISOMessage(hashMap, packager);
            try
            {
                ISOMsg _200_ResMessage = null;
                String env = RB.getString("Env");
                if ("Y".equals(isTestWithSimulator))
                {
                    env = "development";
                }
                if ("development".equals(env))
                {
                    _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                    if (_200_ResMessage != null)
                    {
                        String responseCode = _200_ResMessage.getString("39");
                        if ("00".equalsIgnoreCase(responseCode))
                        {
                            status = "success";
                            descriptor = account.getDisplayName();
                        }
                        else
                        {
                            status = "failed";
                            descriptor = "";
                        }
                        payneticsUtils.insertDataNew(trackingId, _200_ResMessage.getString("11"), _200_ResMessage.getString("38"), P042, P041, P004, nextSequenceNumber, gatewayAccountVO.getGenerationNumber(), P046, _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), transactionType);
                        bankApprovalId = _200_ResMessage.getString("38");
                        statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                        remark = PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
                else
                {
                    PzNACChannel channel = null;
                    Logger logger = new Logger();
                    logger.addListener(new SimpleLogListener(System.out));
                    if (isTest)
                    {
                        /*1)Send direct msg to paynetics.
                        *2)or Send msg to primary TQMux.
                        *3)or Send msg to DR TQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                            channel.setLogger(logger, "test-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary Test VPN is down connecting... to DR TQMux Server...");
                                channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                channel.connect();
                            }
                        }
                    }
                    else
                    {
                        /*1)Send direct msg to paynetics.
                        *2)Send msg to primary LQMux
                        *3)or Send msg to DR LQMux when primary VPN is down.
                        * */
                        if ("Y".equals(isQMuxActive))
                        {
                            channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e1)
                                {
                                    transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                            channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                            channel.setLogger(logger, "live-channel");
                            try
                            {
                                channel.connect();
                            }
                            catch (IOException e)
                            {
                                transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                channel.connect();
                            }
                        }
                    }

                    channel.send(isoMsg);
                    _200_ResMessage = channel.receive();
                    channel.disconnect();
                    if (_200_ResMessage != null)
                    {
                        String responseCode = _200_ResMessage.getString("39");
                        if ("00".equalsIgnoreCase(responseCode))
                        {
                            status = "success";
                            descriptor = account.getDisplayName();
                        }
                        else if ("06".equals(_200_ResMessage.getString("39")))
                        {
                            status = "pending";
                            descriptor = "";
                        }
                        else
                        {
                            status = "failed";
                            descriptor = "";
                        }
                        if ("pending".equals(status))
                        {
                            payneticsUtils.insertDataNew(trackingId, _200_ResMessage.getString("11"), _200_ResMessage.getString("38"), P042, P041, P004, nextSequenceNumber, gatewayAccountVO.getGenerationNumber(), P046, _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), transactionType);
                            String nextSTAN = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            isoMsg.set("11", nextSTAN);
                            nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);
                            P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();
                            isoMsg.set("57", P057);

                            channel.connect();
                            channel.send(isoMsg);
                            _200_ResMessage = channel.receive();
                            channel.disconnect();

                            responseCode = _200_ResMessage.getString("39");
                            if ("00".equalsIgnoreCase(responseCode))
                            {
                                status = "success";
                                descriptor = account.getDisplayName();
                            }
                            else
                            {
                                status = "failed";
                                descriptor = "";
                            }
                        }
                        payneticsUtils.insertDataNew(trackingId, _200_ResMessage.getString("11"), _200_ResMessage.getString("38"), P042, P041, P004, nextSequenceNumber, gatewayAccountVO.getGenerationNumber(), P046, _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), transactionType);
                        bankApprovalId = _200_ResMessage.getString("38");
                        statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                        remark = PayneticsErrorCodes.getErrorCode(responseCode);
                    }
                }
            }
            catch (ISOException e)
            {
                status = "failed";
                statusDescription = "ACQUIRER TEMPORARILY NOT REACHABLE";
                remark = "ACQUIRER TEMPORARILY NOT REACHABLE";
                transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
            }
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while payout transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ISOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while payout transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovalId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        return commResponseVO;
    }
    public GenericResponseVO process3DSaleConfirmation(String trackingId, GenericRequestVO requestVO,String transactionType) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("3D transactionType:" + transactionType);
        String bankApprovedId = "";
        String P017 = "00";
        String P063 = "3";
        String errorName = "";
        String status = "";
        String statusDescription = "";
        String remark = "";
        String descriptor = "";
        String responseTime = "";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";

        PayneticsRequestVO commRequestVO = (PayneticsRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        Calendar calendar = Calendar.getInstance();

        PayneticsGatewayAccountVO gatewayAccountVO = payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String isTestWithSimulator = gatewayAccountVO.getIsTestWithSimulator();
        String isQMuxActive = gatewayAccountVO.getIsQMuxActive();
        boolean isTest = account.isTest();
        String isRecurring = account.getIsRecurring();
        String isForexMID = account.getForexMid();
        transactionLogger.error("isTestWithSimulator:::::" + isTestWithSimulator);
        transactionLogger.error("isForexMID:::::" + isForexMID);

        String MTI = payneticsUtils.getMTI(transactionType);
        String P002 = genericCardDetailsVO.getCardNum();
        String P003 = payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(genericTransDetailsVO.getAmount());
        if(genericTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(genericTransDetailsVO.getAmount());
        }
        String P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
        String P012 = payneticsUtils.getLocalTime(calendar);
        String P013 = payneticsUtils.getLocalDate(calendar);
        String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());
        String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
        String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P041 = gatewayAccountVO.getTerminalId();
        String P042 = gatewayAccountVO.getMerchantId();
        String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
        String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());
        String nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
        String P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

        ParesDecodeRequestVO paresDecodeRequestVO = new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingId);
        paresDecodeRequestVO.setPares(commRequestVO.getPARes());
        paresDecodeRequestVO.setTrackid(trackingId);
        paresDecodeRequestVO.setcRes(commRequestVO.getTransDetailsVO().getCres());
        paresDecodeRequestVO.setMid(gatewayAccountVO.getMpiMid());

        EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
        EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
        ParesDecodeResponseVO paresDecodeResponseVO=commRequestVO.getParesDecodeResponseVO();
        if(functions.isValueNull(commRequestVO.getTransDetailsVO().getCres()))
            paresDecodeResponseVO = endeavourMPIV2Gateway.processRresDecode(paresDecodeRequestVO);
        else if(functions.isValueNull(paresDecodeRequestVO.getPares()))
            paresDecodeResponseVO = endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        if(paresDecodeResponseVO!=null && "3DS2".equalsIgnoreCase(paresDecodeResponseVO.getVersion()) &&("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus()) || "R".equalsIgnoreCase(paresDecodeResponseVO.getStatus())))
        {
            commResponseVO.setStatus("failed");
            commResponseVO.setDescriptor(account.getDisplayName());
            if("N".equalsIgnoreCase(paresDecodeResponseVO.getStatus())) {
                commResponseVO.setDescription("Authenticated Transaction Denied(Challenge)");
                commResponseVO.setRemark("Authenticated Transaction Denied(Challenge)");
            }else if("R".equalsIgnoreCase(paresDecodeResponseVO.getStatus()))
            {
                commResponseVO.setDescription("Authentication Rejected(Challenge)");
                commResponseVO.setRemark("Authentication Rejected(Challenge)");
            }
            return commResponseVO;
        }
        String UCAF = "";
        String ECI = "";

        byte[] XIDBytes = null;
        byte[] CAVVBytes = null;

        if (paresDecodeResponseVO.get_20BytesBinaryXIDBytes() != null && paresDecodeResponseVO.get_20BytesBinaryXIDBytes().length > 0)
        {
            XIDBytes = paresDecodeResponseVO.get_20BytesBinaryXIDBytes();
        }
        if (paresDecodeResponseVO.get_20BytesBinaryCAVVBytes() != null && paresDecodeResponseVO.get_20BytesBinaryCAVVBytes().length > 0)
        {
            CAVVBytes = paresDecodeResponseVO.get_20BytesBinaryCAVVBytes();
        }
        if (functions.isValueNull(paresDecodeResponseVO.getCavv()))
        {
            UCAF = paresDecodeResponseVO.getCavv();
        }
        if (functions.isValueNull(paresDecodeResponseVO.getEci()))
        {
            ECI = paresDecodeResponseVO.getEci();
        }

        transactionLogger.error("UCAF:" + UCAF);
        transactionLogger.error("ECI:" + ECI);

        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=genericAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=genericAddressDetailsVO.getTmpl_currency();
        }

        if (functions.isValueNull(ECI) && ("05".equals(ECI) || "06".equals(ECI) || "02".equals(ECI) || "01".equals(ECI)))
        {
            /*BMP>60 Data*/
            String P060 = "";
            if (functions.isValueNull(genericCardDetailsVO.getcVV()))//OK
            {
                String P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(genericCardDetailsVO.getcVV(), "30");//CVV
                if (functions.isValueNull(P060_30))
                {
                    P060 = P060 + P060_30;
                }
            }
            if (functions.isValueNull(trackingId))//OK
            {
                String P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                if (functions.isValueNull(P060_35))
                {
                    P060 = P060 + P060_35;
                }
            }
            transactionLogger.error("electronicCommerceIndicator--->"+electronicCommerceIndicator);
            if (functions.isValueNull(electronicCommerceIndicator))//OK
            {
                if ("05".equals(ECI))
                {
                    electronicCommerceIndicator = "10";
                }
                else if ("06".equals(ECI))
                {
                    electronicCommerceIndicator = "12";
                }
                else if ("02".equals(ECI))
                {
                    electronicCommerceIndicator = "11";
                }
                else if ("01".equals(ECI))
                {
                    electronicCommerceIndicator = "13";
                }
                transactionLogger.error("electronicCommerceIndicator--->"+electronicCommerceIndicator);
                String P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                if (functions.isValueNull(P060_40))
                {
                    P060 = P060 + P060_40;
                }
            }

            if (isForexMID.equalsIgnoreCase("Y"))//OK
            {
                transactionLogger.error("Setting P060_49---"+trackingId);
                String cryptoPurchase = "06";
                String P060_49 = payneticsUtils.getDATA_IN_LTV_FORMAT(cryptoPurchase, "49");//Indicator For CryptoCurrency
                if (functions.isValueNull(P060_49))
                {
                    P060 = P060 + P060_49;
                }
                transactionLogger.error("Setting P060_49---"+P060_49);
            }

            if(isRecurring.equalsIgnoreCase("Y"))
            {
                transactionLogger.error("Setting P060_41");
                String P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT("01", "41");//Indicator For Electronic Commerce
                if(functions.isValueNull(P060_41)){
                    P060=P060+P060_41;
                }
                transactionLogger.error("Setting P060_52");

                String P060_52_2 = payneticsUtils.getDATA_IN_LTV_FORMAT("2", "02");
                String P060_52 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_52_2, "52");

                if(functions.isValueNull(P060_52)){
                    P060=P060+P060_52;
                }
            }

            transactionLogger.error("Setting P060_72");
            String P060_72 = payneticsUtils.getDATA_IN_LTV_FORMAT("1", "72");
            transactionLogger.error("P060_72--" + trackingId + "-->" + P060_72);
            if (functions.isValueNull(P060_72))
            {
                P060 = P060 + P060_72;
            }

            if(functions.isValueNull(commRequestVO.getTransDetailsVO().getCres()) || "Frictionless".equalsIgnoreCase(commRequestVO.getAttemptThreeD()))
            {
                /*transactionLogger.error("Setting P060_54");
                String P060_54 = payneticsUtils.getDATA_IN_LTV_FORMAT("01", "54");
                transactionLogger.error("P060_54--" + trackingId + "-->" + P060_54);
                if (functions.isValueNull(P060_54))
                {
                    P060 = P060 + P060_54;
                }*/
                transactionLogger.error("Setting P060_73");
                String P060_73 = payneticsUtils.getDATA_IN_LTV_FORMAT(paresDecodeResponseVO.getDsTransId(), "73");
                transactionLogger.error("P060_73--" + trackingId + "-->" + P060_73);
                if (functions.isValueNull(P060_73))
                {
                    P060 = P060 + P060_73;
                }
            }

            if (XIDBytes != null && XIDBytes.length > 0 && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                String P060_61 = payneticsUtils.getDATA_IN_LTV_FORMAT(XIDBytes, "61");//xid
                transactionLogger.error("P060_61--->"+P060_61);
                if (functions.isValueNull(P060_61))
                {
                    P060 = P060 + P060_61;
                }
            }
            if (CAVVBytes != null && CAVVBytes.length > 0 && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                String P060_62 = payneticsUtils.getDATA_IN_LTV_FORMATForCAVV(CAVVBytes, "62");//CAVV
                transactionLogger.error("P060_62--->"+P060_62);
                if (functions.isValueNull(P060_62))
                {
                    P060 = P060 + P060_62;
                }
            }

            if (functions.isValueNull(UCAF) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_63 = payneticsUtils.getDATA_IN_LTV_FORMAT(UCAF, "63");//UCAF
                transactionLogger.error("P060_63--->"+P060_63);
                if (functions.isValueNull(P060_63))
                {
                    P060 = P060 + P060_63;
                }
            }

            if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                if (functions.isValueNull(P060_81))
                {
                    P060 = P060 + P060_81;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                String P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                if (functions.isValueNull(P060_82))
                {
                    P060 = P060 + P060_82;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
            {
                String P060_83_1 = "";
                String P060_83_2 = "";
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantId()))
                {//Sub-Merchant:ID
                    P060_83_1 = String.format("%-15s", gatewayAccountVO.getSubMerchantId());
                }
                else
                {
                    P060_83_1 = String.format("%-15s", P060_83_1);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode()))
                {//Sub-Merchant:MCC code
                    P060_83_2 = String.format("%-4s", gatewayAccountVO.getSubMerchantMccCode());
                }
                else
                {
                    P060_83_2 = String.format("%-4s", P060_83_2);
                }
                String P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                if (functions.isValueNull(P060_83))
                {
                    P060 = P060 + P060_83;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
            {
                String P060_84_1 = "";
                String P060_84_2 = "";
                String P060_84_3 = "";

                if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()))
                {//Merchant Address Details-ZIP Code
                    P060_84_1 = String.format("%-10s", gatewayAccountVO.getSubMerchantZip());
                }
                else
                {
                    P060_84_1 = String.format("%-10s", P060_84_1);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()))
                {//Merchant Address Details-Street
                    P060_84_2 = String.format("%-25s", gatewayAccountVO.getSubMerchantStreet());
                }
                else
                {
                    P060_84_2 = String.format("%-25s", P060_84_2);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
                {//Merchant Address Details-City
                    P060_84_3 = String.format("%-13s", gatewayAccountVO.getSubMerchantCity());
                }
                else
                {
                    P060_84_3 = String.format("%-13s", P060_84_3);
                }
                String P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                if (functions.isValueNull(P060_84))
                {
                    P060 = P060 + P060_84;
                }
            }

            String P060_87_1 = String.format("%-1s", "1");
            if (functions.isValueNull(P060_87_1))
            {
                P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                if (functions.isValueNull(P060_87_1))
                {
                    String P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                    if (functions.isValueNull(P060_87))
                    {
                        P060 = P060 + P060_87;
                    }
                }
            }

            HashMap<String, String> hashMap = new HashMap();
            hashMap.put("MTYP", MTI);
            hashMap.put("P002", P002);
            hashMap.put("P003", P003);
            hashMap.put("P004", P004);
            hashMap.put("P011", P011);
            hashMap.put("P012", P012);
            hashMap.put("P013", P013);
            hashMap.put("P014", P014);
            hashMap.put("P017", P017);
            hashMap.put("P022", P022);
            hashMap.put("P025", P025);
            hashMap.put("P032", P032);
            hashMap.put("P041", P041);
            hashMap.put("P042", P042);
            hashMap.put("P043", P043);
            hashMap.put("P046", P046);
            hashMap.put("P049", P049);
            hashMap.put("P057", P057);
            hashMap.put("P060", P060);
            hashMap.put("P063", P063);

            //record the data.
            boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
            if (inserted)
            {
                transactionLogger.error("Recorded channel request:" + inserted);
            }
            try
            {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH") + "paynetics_basic_v6.xml"));
                GenericPackager packager = new GenericPackager(inputStream);
                ISOMsg isoMsg = payneticsUtils.getISOMessage(hashMap, packager);
                try
                {
                    ISOMsg _200_ResMessage = null;
                    String resTStamp = "";
                    String env = RB.getString("Env");
                    if ("Y".equals(isTestWithSimulator))
                    {
                        env = "development";
                    }
                    if ("development".equals(env))
                    {
                        _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                        if (_200_ResMessage != null)
                        {
                            String responseCode = _200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }
                            else
                            {
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }

                            if(isRecurring.equalsIgnoreCase("Y"))
                                payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                            else
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);

                            bankApprovedId = _200_ResMessage.getString("38");
                            statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                            remark = PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                    else
                    {
                        PzNACChannel channel = null;
                        Logger logger = new Logger();
                        logger.addListener(new SimpleLogListener(System.out));
                        if (isTest)
                        {
                        /*1)Send direct msg to paynetics.
                        *2)or Send msg to primary TQMux.
                        *3)or Send msg to DR TQMux when primary VPN is down.
                        * */
                            if ("Y".equals(isQMuxActive))
                            {
                                channel = new PzNACChannel(RB.getString("PrimaryTQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryTQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary TQMux server is down,connecting to DR TQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e1)
                                    {
                                        transactionLogger.error("DR TQMux server is down,connecting to Paynetics test directly...");
                                        channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                        channel.setLogger(logger, "test-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                channel.setLogger(logger, "test-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary Test VPN is down,connecting to DR TQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRTQMuxServerIP"), Integer.parseInt(RB.getString("DRTQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "test-channel");
                                    channel.connect();
                                }
                            }
                        }
                        else
                        {
                        /*1)Send direct msg to paynetics.
                        *2)Send msg to primary LQMux
                        *3)or Send msg to DR LQMux when primary VPN is down.
                        * */
                            if ("Y".equals(isQMuxActive))
                            {
                                channel = new PzNACChannel(RB.getString("PrimaryLQMuxServerIP"), Integer.parseInt(RB.getString("PrimaryLQMuxServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary LQMux server is down,connecting to DR LQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    try
                                    {
                                        channel.connect();
                                    }
                                    catch (IOException e1)
                                    {
                                        transactionLogger.error("DR LQMux server is down,connecting to Paynetics live server directly...");
                                        channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                        channel.setLogger(logger, "live-channel");
                                        channel.connect();
                                    }
                                }
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                channel.setLogger(logger, "live-channel");
                                try
                                {
                                    channel.connect();
                                }
                                catch (IOException e)
                                {
                                    transactionLogger.error("Primary live VPN is down,connecting to DR LQMux server...");
                                    channel = new PzNACChannel(RB.getString("DRLQMuxServerIP"), Integer.parseInt(RB.getString("DRLQMuxServerPORT")), packager, null);
                                    channel.setLogger(logger, "live-channel");
                                    channel.connect();
                                }
                            }
                        }

                        channel.send(isoMsg);
                        _200_ResMessage = channel.receive();
                        channel.disconnect();
                        if (_200_ResMessage != null)
                        {
                            String responseCode = _200_ResMessage.getString("39");

                            if(isRecurring.equalsIgnoreCase("Y"))
                            {
                                transactionLogger.error("BMP 61: Transaction stamp when is3DSecure=N---" + _200_ResMessage.getString("61"));
                                resTStamp = _200_ResMessage.getString("61");
                            }


                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                            }
                            else if ("06".equals(_200_ResMessage.getString("39")))
                            {
                                status = "pending";
                                descriptor = "";
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                            if ("pending".equals(status))
                            {
                                if(isRecurring.equalsIgnoreCase("Y"))
                                    payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                                else
                                    payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);

                                nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);

                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57", P057);

                                inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }

                                channel.connect();
                                channel.send(isoMsg);
                                _200_ResMessage = channel.receive();
                                channel.disconnect();

                                responseCode = _200_ResMessage.getString("39");
                                if ("00".equalsIgnoreCase(responseCode))
                                {
                                    status = "success";
                                    descriptor = account.getDisplayName();
                                }
                                else
                                {
                                    status = "failed";
                                    descriptor = "";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            if(isRecurring.equalsIgnoreCase("Y"))
                                payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                            else
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);

                            //payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedId = _200_ResMessage.getString("38");
                            statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                            remark = PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                }
                catch (ISOException e)
                {
                    status = "failed";
                    statusDescription = "ACQUIRER TEMPORARILY NOT REACHABLE";
                    remark = "ACQUIRER TEMPORARILY NOT REACHABLE";
                    transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
                }
            }
            catch (IOException e)
            {
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing 3d transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (ISOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing 3d transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }

            if ("Y".equalsIgnoreCase(account.getIsRecurring()))
            {
                String cardNumber = genericCardDetailsVO.getCardNum();
                String first_six = cardNumber.substring(0, 6);
                String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                RecurringManager recurringManager = new RecurringManager();
                if ("success".equalsIgnoreCase(status))
                {
                    recurringManager.updateRbidForSuccessfullRebill(bankApprovedId, first_six, last_four, trackingId);
                }
                else
                {
                    recurringManager.deleteEntryForPFSRebill(trackingId);
                }
            }
        }
        else
        {
            status = "failed";
            statusDescription = "3D Authentication Failed";
            remark = "3D Authentication Failed";
        }

        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovedId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(currency);
        commResponseVO.setTmpl_Amount(tmpl_amount);
        commResponseVO.setTmpl_Currency(tmpl_currency);
        if (functions.isValueNull(errorName))
            commResponseVO.setErrorName(errorName);
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("Inside processCommon3DSaleConfirmation for Paynetics---"+trackingId);
        //transactionLogger.error("3D transactionType:" + transactionType);
        String transactionType="sale";
        Calendar calendar = Calendar.getInstance();
        String bankApprovedId = "";
        String indicatorForRecurring = "02";
        String P017 = "00";
        String P063 = "3";
        String errorName = "";

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        //GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();//Optional

        String cvv=PzEncryptor.decryptCVV(commRequestVO.getMd());
        transactionLogger.debug("num-----"+cvv);

        PayneticsGatewayAccountVO gatewayAccountVO = payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = account.isTest();
        String isRecurring = account.getIsRecurring();
        String isForexMID = account.getForexMid();
        String isTestWithSimulator = gatewayAccountVO.getIsTestWithSimulator();
        transactionLogger.error("isTestWithSimulator:::::" + isTestWithSimulator);
        transactionLogger.error("isForexMID:::::" + isForexMID);

        String electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());

        String MTI = payneticsUtils.getMTI(transactionType);
        String P002 = genericCardDetailsVO.getCardNum();
        String P003 = payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(genericTransDetailsVO.getAmount());
        if(genericTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
        {
            P004 = payneticsUtils.getJPYAmount(genericTransDetailsVO.getAmount());
        }

        transactionLogger.error("P004 ConfirmationSale---"+P004);

        String P012 = payneticsUtils.getLocalTime(calendar);
        String P013 = payneticsUtils.getLocalDate(calendar);
        String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());

        String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
        String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P041 = gatewayAccountVO.getTerminalId();
        String P042 = gatewayAccountVO.getMerchantId();
        String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
        String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String P057 = "";// ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();
        String P011 = "";// payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());//

        ParesDecodeRequestVO paresDecodeRequestVO = new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingId);
        paresDecodeRequestVO.setPares(commRequestVO.getPaRes());
        paresDecodeRequestVO.setTrackid(trackingId);

        EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO = endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        //String XID="";
        //String CAVV="";
        String UCAF = "";
        String ECI = "";

        byte[] XIDBytes = null;
        byte[] CAVVBytes = null;

        if (paresDecodeResponseVO.get_20BytesBinaryXIDBytes() != null && paresDecodeResponseVO.get_20BytesBinaryXIDBytes().length > 0)
        {
            XIDBytes = paresDecodeResponseVO.get_20BytesBinaryXIDBytes();
        }
        if (paresDecodeResponseVO.get_20BytesBinaryCAVVBytes() != null && paresDecodeResponseVO.get_20BytesBinaryCAVVBytes().length > 0)
        {
            CAVVBytes = paresDecodeResponseVO.get_20BytesBinaryCAVVBytes();
        }
    /*if(functions.isValueNull(paresDecodeResponseVO.get_20BytesBinaryXID())){
        XID=paresDecodeResponseVO.get_20BytesBinaryXID();
    }
    if(functions.isValueNull(paresDecodeResponseVO.get_20BytesBinaryCAVV())){
        CAVV=paresDecodeResponseVO.get_20BytesBinaryCAVV();
    }*/
        if (functions.isValueNull(paresDecodeResponseVO.getCavv()))
        {
            UCAF = paresDecodeResponseVO.getCavv();
        }
        if (functions.isValueNull(paresDecodeResponseVO.getEci()))
        {
            ECI = paresDecodeResponseVO.getEci();
        }

        //transactionLogger.error("XID:"+XID);
        //transactionLogger.error("CAVV:"+CAVV);
        transactionLogger.error("UCAF:" + UCAF);
        transactionLogger.error("ECI:" + ECI);

        String status = "";
        String statusDescription = "";
        String remark = "";
        String descriptor = "";
        String responseTime = "";

        String nextSequenceNumber = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=genericAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=genericAddressDetailsVO.getTmpl_currency();
        }

        if (functions.isValueNull(ECI) && ("05".equals(ECI) || "06".equals(ECI) || "02".equals(ECI) || "01".equals(ECI)))
        {
            /*BMP>60 Data*/
            String P060_30 = "";
            String P060_35 = "";
            String P060_40 = "";
            String P060_49 = "";
            //String P060_41 = "";
            String P060_61 = "";
            String P060_62 = "";
            String P060_63 = "";
            String P060_81 = "";
            String P060_82 = "";
            String P060_83 = "";
            String P060_84 = "";
            String P060_87_1 = "";
            String P060_87 = "";
            String P060 = "";

            if (functions.isValueNull(cvv))//OK
            {
                P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(cvv, "30");//CVV
                if (functions.isValueNull(P060_30))
                {
                    P060 = P060 + P060_30;
                }
            }
            if (functions.isValueNull(trackingId))//OK
            {
                P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                if (functions.isValueNull(P060_35))
                {
                    P060 = P060 + P060_35;
                }
            }
            if (functions.isValueNull(electronicCommerceIndicator))//OK
            {
                if ("05".equals(ECI))
                {
                    electronicCommerceIndicator = "10";
                }
                else if ("06".equals(ECI))
                {
                    electronicCommerceIndicator = "12";
                }
                else if ("02".equals(ECI))
                {
                    electronicCommerceIndicator = "11";
                }
                else if ("01".equals(ECI))
                {
                    electronicCommerceIndicator = "13";
                }
                P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                if (functions.isValueNull(P060_40))
                {
                    P060 = P060 + P060_40;
                }
            }

            if (isForexMID.equalsIgnoreCase("Y"))//OK
            {
                String cryptoPurchase = "06";
                P060_49 = payneticsUtils.getDATA_IN_LTV_FORMAT(cryptoPurchase, "49");//Indicator For CryptoCurrency
                if (functions.isValueNull(P060_49))
                {
                    P060 = P060 + P060_49;
                }
            }

            if(isRecurring.equalsIgnoreCase("Y"))
            {
                transactionLogger.error("Setting P060_41");
                String P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT("01", "41");//Indicator For Electronic Commerce
                if(functions.isValueNull(P060_41)){
                    P060=P060+P060_41;
                }
                transactionLogger.error("Setting P060_52");

                String P060_52_2 = payneticsUtils.getDATA_IN_LTV_FORMAT("2", "02");
                String P060_52 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_52_2, "52");

                if(functions.isValueNull(P060_52)){
                    P060=P060+P060_52;
                }
            }

            /*if ("Y".equalsIgnoreCase(account.getIsRecurring()) && functions.isValueNull(indicatorForRecurring) && "sale".equalsIgnoreCase(transactionType))//OK
            {
                P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT(indicatorForRecurring, "41");//Indicator For Recurring
                if (functions.isValueNull(P060_41))
                {
                    P060 = P060 + P060_41;
                }
            }*/
            if (XIDBytes != null && XIDBytes.length > 0 && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                P060_61 = payneticsUtils.getDATA_IN_LTV_FORMAT(XIDBytes, "61");//xid
                if (functions.isValueNull(P060_61))
                {
                    P060 = P060 + P060_61;
                }
            }
            if (CAVVBytes != null && CAVVBytes.length > 0 && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                P060_62 = payneticsUtils.getDATA_IN_LTV_FORMATForCAVV(CAVVBytes, "62");//CAVV
                if (functions.isValueNull(P060_62))
                {
                    P060 = P060 + P060_62;
                }
            }

            if (functions.isValueNull(UCAF) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                P060_63 = payneticsUtils.getDATA_IN_LTV_FORMAT(UCAF, "63");//UCAF
                if (functions.isValueNull(P060_63))
                {
                    P060 = P060 + P060_63;
                }
            }

            if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                if (functions.isValueNull(P060_81))
                {
                    P060 = P060 + P060_81;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                if (functions.isValueNull(P060_82))
                {
                    P060 = P060 + P060_82;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
            {
                String P060_83_1 = "";
                String P060_83_2 = "";
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantId()))
                {//Sub-Merchant:ID
                    P060_83_1 = String.format("%-15s", gatewayAccountVO.getSubMerchantId());
                }
                else
                {
                    P060_83_1 = String.format("%-15s", P060_83_1);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode()))
                {//Sub-Merchant:MCC code
                    P060_83_2 = String.format("%-4s", gatewayAccountVO.getSubMerchantMccCode());
                }
                else
                {
                    P060_83_2 = String.format("%-4s", P060_83_2);
                }
                P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                if (functions.isValueNull(P060_83))
                {
                    P060 = P060 + P060_83;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
            {
                String P060_84_1 = "";
                String P060_84_2 = "";
                String P060_84_3 = "";

                if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()))
                {//Merchant Address Details-ZIP Code
                    P060_84_1 = String.format("%-10s", gatewayAccountVO.getSubMerchantZip());
                }
                else
                {
                    P060_84_1 = String.format("%-10s", P060_84_1);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()))
                {//Merchant Address Details-Street
                    P060_84_2 = String.format("%-25s", gatewayAccountVO.getSubMerchantStreet());
                }
                else
                {
                    P060_84_2 = String.format("%-25s", P060_84_2);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
                {//Merchant Address Details-City
                    P060_84_3 = String.format("%-13s", gatewayAccountVO.getSubMerchantCity());
                }
                else
                {
                    P060_84_3 = String.format("%-13s", P060_84_3);
                }
                P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                if (functions.isValueNull(P060_84))
                {
                    P060 = P060 + P060_84;
                }
            }

            P060_87_1 = String.format("%-1s", "1");
            if (functions.isValueNull(P060_87_1))
            {
                P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                if (functions.isValueNull(P060_87_1))
                {
                    P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                    if (functions.isValueNull(P060_87))
                    {
                        P060 = P060 + P060_87;
                    }
                }
            }

            transactionLogger.error("P060_30:=" + P060_30);
            transactionLogger.error("P060_35:=" + P060_35);
            transactionLogger.error("P060_40:=" + P060_40);
            transactionLogger.error("P060_49:=" + P060_49);
            transactionLogger.error("P060_61:=" + P060_61);
            transactionLogger.error("P060_62:=" + P060_62);
            transactionLogger.error("P060_63:=" + P060_63);
            transactionLogger.error("P060_81:=" + P060_81);
            transactionLogger.error("P060_82:=" + P060_82);
            transactionLogger.error("P060_83:=" + P060_83);
            transactionLogger.error("P060_84:=" + P060_84);
            transactionLogger.error("P060_87:=" + P060_87);

            HashMap<String, String> hashMap = new HashMap();
            hashMap.put("MTYP", MTI);
            hashMap.put("P002", P002);
            hashMap.put("P003", P003);
            hashMap.put("P004", P004);
            hashMap.put("P012", P012);
            hashMap.put("P013", P013);
            hashMap.put("P014", P014);
            hashMap.put("P017", P017);
            hashMap.put("P022", P022);
            hashMap.put("P025", P025);
            hashMap.put("P032", P032);
            hashMap.put("P041", P041);
            hashMap.put("P042", P042);
            hashMap.put("P043", P043);
            hashMap.put("P046", P046);
            hashMap.put("P049", P049);
            hashMap.put("P060", P060);
            hashMap.put("P063", P063);

            synchronized (this)
            {
                nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));

                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();
                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());

                hashMap.put("P057", P057);
                hashMap.put("P011", P011);

                boolean checkChannelAvailability = payneticsUtils.checkChannelAvailability(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
                if (!checkChannelAvailability)
                {
                    //putting 5 sec in sleep
                    try
                    {
                        transactionLogger.error("before sleep");
                        TimeUnit.SECONDS.sleep(5);
                        transactionLogger.error("after sleep");
                    }
                    catch (InterruptedException ire)
                    {
                        transactionLogger.error("InterruptedException:::::" + ire);
                    }

                    boolean checkChannelAvailabilityAfter5Sec = payneticsUtils.checkChannelAvailability(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
                    if (!checkChannelAvailabilityAfter5Sec)
                    {
                        try
                        {
                            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH") + "paynetics_basic_v6.xml"));
                            GenericPackager packager = new GenericPackager(inputStream);
                            PzNACChannel channel = null;

                            ISOMsg isoMsg = payneticsUtils.getISOMessage(hashMap, packager);

                            Logger logger = new Logger();
                            logger.addListener(new SimpleLogListener(System.out));

                            if (isTest)
                            {
                                channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                ((LogSource) channel).setLogger(logger, "test-channel");
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                ((LogSource) channel).setLogger(logger, "live-channel");
                            }

                            nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, RB.getString("Env"));

                            P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                            hashMap.put("P011", P011);
                            hashMap.put("P057", P057);
                        }
                        catch (IOException e)
                        {
                            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                        }
                        catch (ISOException e)
                        {
                            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                        }
                    }
                    else
                    {
                        transactionLogger.error("channel is open now");
                    }
                }
                else
                {
                    transactionLogger.error("channel is open");
                }

                //record the data.
                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                if (inserted)
                {
                    transactionLogger.error("Recorded channel request:" + inserted);
                }
            }

            try
            {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH") + "paynetics_basic_v6.xml"));
                GenericPackager packager = new GenericPackager(inputStream);
                PzNACChannel channel = null;

                ISOMsg isoMsg = payneticsUtils.getISOMessage(hashMap, packager);

                Logger logger = new Logger();
                logger.addListener(new SimpleLogListener(System.out));

                if (isTest)
                {
                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                    ((LogSource) channel).setLogger(logger, "test-channel");
                }
                else
                {
                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                    ((LogSource) channel).setLogger(logger, "live-channel");
                }
                try
                {
                    ISOMsg _200_ResMessage = null;
                    String env = RB.getString("Env");
                    String resTStamp = "";
                    if ("Y".equals(isTestWithSimulator))
                    {
                        env = "development";
                    }
                    if ("development".equals(env))
                    {
                        byte[] data = isoMsg.pack();
                        transactionLogger.error(transactionType + " sample transaction byte length:" + data.length);
                        String strMessage = ISOUtil.hexString(data);
                        transactionLogger.error(transactionType + " sample transaction packed data:" + strMessage);

                        _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);//payneticsUtils.get_SequenceNumberError_SampleResponseMsg(isoMsg);
                        if (_200_ResMessage != null)
                        {
                            String responseCode = _200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                                //errorName = getErrorName("00");
                            }
                            else if ("06".equals(_200_ResMessage.getString("39")))
                            {
                                status = "pending";
                                descriptor = "";
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                            if ("pending".equals(status))
                            {
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);

                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());

                                nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);

                                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57", P057);


                                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }

                                data = isoMsg.pack();
                                transactionLogger.error(transactionType + " synchronized sample transaction byte length:" + data.length);
                                strMessage = ISOUtil.hexString(data);
                                transactionLogger.error(transactionType + " synchronized sample transaction packed data:" + strMessage);

                                _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                                responseCode = _200_ResMessage.getString("39");
                                if ("00".equalsIgnoreCase(responseCode))
                                {
                                    status = "success";
                                    descriptor = account.getDisplayName();
                                    // errorName = getErrorName("00");
                                }
                                else
                                {
                                    status = "failed";
                                    descriptor = "";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedId = _200_ResMessage.getString("38");
                            statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                            remark = PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                    else
                    {
                        byte[] data = isoMsg.pack();
                        transactionLogger.error(transactionType + " transaction byte length:" + data.length);
                        String strMessage = ISOUtil.hexString(data);
                        transactionLogger.error(transactionType + " transaction packed data:" + strMessage);

                        channel.connect();
                        channel.send(isoMsg);
                        _200_ResMessage = channel.receive();
                        channel.disconnect();

                        if (_200_ResMessage != null)
                        {
                            String responseCode = _200_ResMessage.getString("39");
                            if(isRecurring.equalsIgnoreCase("Y"))
                            {
                                transactionLogger.error("BMP 61: Transaction stamp for Confirmation 3D---" + _200_ResMessage.getString("61"));
                                resTStamp = _200_ResMessage.getString("61");
                            }

                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                                //errorName = getErrorName("00");
                            }
                            else if ("06".equals(_200_ResMessage.getString("39")))
                            {
                                status = "pending";
                                descriptor = "";
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }

                            if ("pending".equals(status))
                            {
                                if(isRecurring.equalsIgnoreCase("Y"))
                                    payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                                else
                                    payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());

                                nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);

                                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57", P057);

                                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }

                                data = isoMsg.pack();
                                transactionLogger.error(transactionType + " synchronized transaction byte length:" + data.length);
                                strMessage = ISOUtil.hexString(data);
                                transactionLogger.error(transactionType + " synchronized transaction packed data:" + strMessage);

                                channel.connect();
                                channel.send(isoMsg);
                                _200_ResMessage = channel.receive();
                                channel.disconnect();

                                responseCode = _200_ResMessage.getString("39");
                                if ("00".equalsIgnoreCase(responseCode))
                                {
                                    status = "success";
                                    descriptor = account.getDisplayName();
                                    //errorName = getErrorName("00");
                                }
                                else
                                {
                                    status = "failed";
                                    descriptor = "";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            if(isRecurring.equalsIgnoreCase("Y"))
                                payneticsUtils.updatePayneticsDetailsWithTStamp(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004,resTStamp);
                            else
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedId = _200_ResMessage.getString("38");
                            statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                            remark = PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                }
                catch (ISOException e)
                {
                    status = "failed";
                    statusDescription = "ACQUIRER TEMPORARILY NOT REACHABLE";
                    remark = "ACQUIRER TEMPORARILY NOT REACHABLE";
                    transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
                }
            }
            catch (IOException e)
            {
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing 3d transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (ISOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing 3d transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }

            if ("Y".equalsIgnoreCase(account.getIsRecurring()))
            {
                String cardNumber = genericCardDetailsVO.getCardNum();
                String first_six = cardNumber.substring(0, 6);
                String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                RecurringManager recurringManager = new RecurringManager();
                if ("success".equalsIgnoreCase(status))
                {
                    recurringManager.updateRbidForSuccessfullRebill(bankApprovedId, first_six, last_four, trackingId);
                }
                else
                {
                    recurringManager.deleteEntryForPFSRebill(trackingId);
                }
            }
        }
        else
        {
            status = "failed";
            statusDescription = "3D Authentication Failed";
            remark = "3D Authentication Failed";
        }

        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovedId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(currency);
        commResponseVO.setTmpl_Amount(tmpl_amount);
        commResponseVO.setTmpl_Currency(tmpl_currency);
        if (functions.isValueNull(errorName))
            commResponseVO.setErrorName(errorName);

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        String transactionType="auth";
        Calendar calendar = Calendar.getInstance();
        String bankApprovedId = "";
        String indicatorForRecurring = "02";
        String P017 = "00";
        String P063 = "3";
        String errorName = "";

        PayneticsUtils payneticsUtils = new PayneticsUtils();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        //GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();//Optional

        String cvv=PzEncryptor.decryptCVV(commRequestVO.getMd());
        transactionLogger.debug("num-----"+cvv);
        PayneticsGatewayAccountVO gatewayAccountVO = payneticsUtils.getAccountDetails(accountId);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = account.isTest();
        String isForexMID = account.getForexMid();
        String isTestWithSimulator = gatewayAccountVO.getIsTestWithSimulator();
        transactionLogger.error("isTestWithSimulator:::::" + isTestWithSimulator);

        String electronicCommerceIndicator = payneticsUtils.getEComIndicatorCode(commRequestVO.getPANEntryType());

        String MTI = payneticsUtils.getMTI(transactionType);
        String P002 = genericCardDetailsVO.getCardNum();
        String P003 = payneticsUtils.getProcessingCode(transactionType);
        String P004 = payneticsUtils.getCentAmount(genericTransDetailsVO.getAmount());
        if(genericTransDetailsVO.getCurrency().equalsIgnoreCase("JPY"))
            P004 = payneticsUtils.getJPYAmount(genericTransDetailsVO.getAmount());

        String P012 = payneticsUtils.getLocalTime(calendar);
        String P013 = payneticsUtils.getLocalDate(calendar);
        String P014 = payneticsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear());

        String P022 = payneticsUtils.getPOSEntryModeCode(commRequestVO.getPANEntryType());
        String P025 = payneticsUtils.getPOSConditionalCode(transactionType);
        String P032 = gatewayAccountVO.getAcquiringInstitutionIdCode();
        String P041 = gatewayAccountVO.getTerminalId();
        String P042 = gatewayAccountVO.getMerchantId();
        String P043 = gatewayAccountVO.getCardAcceptorNameLocation();
        String P046 = payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum());
        String P049 = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String P057 = "";// ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();
        String P011 = "";// payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());//

        ParesDecodeRequestVO paresDecodeRequestVO = new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingId);
        paresDecodeRequestVO.setPares(commRequestVO.getPaRes());
        paresDecodeRequestVO.setTrackid(trackingId);

        EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO = endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        //String XID="";
        //String CAVV="";
        String UCAF = "";
        String ECI = "";

        byte[] XIDBytes = null;
        byte[] CAVVBytes = null;

        if (paresDecodeResponseVO.get_20BytesBinaryXIDBytes() != null && paresDecodeResponseVO.get_20BytesBinaryXIDBytes().length > 0)
        {
            XIDBytes = paresDecodeResponseVO.get_20BytesBinaryXIDBytes();
        }
        if (paresDecodeResponseVO.get_20BytesBinaryCAVVBytes() != null && paresDecodeResponseVO.get_20BytesBinaryCAVVBytes().length > 0)
        {
            CAVVBytes = paresDecodeResponseVO.get_20BytesBinaryCAVVBytes();
        }
    /*if(functions.isValueNull(paresDecodeResponseVO.get_20BytesBinaryXID())){
        XID=paresDecodeResponseVO.get_20BytesBinaryXID();
    }
    if(functions.isValueNull(paresDecodeResponseVO.get_20BytesBinaryCAVV())){
        CAVV=paresDecodeResponseVO.get_20BytesBinaryCAVV();
    }*/
        if (functions.isValueNull(paresDecodeResponseVO.getCavv()))
        {
            UCAF = paresDecodeResponseVO.getCavv();
        }
        if (functions.isValueNull(paresDecodeResponseVO.getEci()))
        {
            ECI = paresDecodeResponseVO.getEci();
        }

        //transactionLogger.error("XID:"+XID);
        //transactionLogger.error("CAVV:"+CAVV);
        transactionLogger.error("UCAF:" + UCAF);
        transactionLogger.error("ECI:" + ECI);

        String status = "";
        String statusDescription = "";
        String remark = "";
        String descriptor = "";
        String responseTime = "";

        String nextSequenceNumber = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
        {
            currency=genericTransDetailsVO.getCurrency();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=genericAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=genericAddressDetailsVO.getTmpl_currency();
        }

        if (functions.isValueNull(ECI) && ("05".equals(ECI) || "06".equals(ECI) || "02".equals(ECI) || "01".equals(ECI)))
        {
            /*BMP>60 Data*/
            String P060_30 = "";
            String P060_35 = "";
            String P060_40 = "";
            String P060_41 = "";
            String P060_49 = "";
            String P060_61 = "";
            String P060_62 = "";
            String P060_63 = "";
            String P060_81 = "";
            String P060_82 = "";
            String P060_83 = "";
            String P060_84 = "";
            String P060_87_1 = "";
            String P060_87 = "";
            String P060 = "";

            if (functions.isValueNull(cvv))//OK
            {
                P060_30 = payneticsUtils.getDATA_IN_LTV_FORMAT(cvv, "30");//CVV
                if (functions.isValueNull(P060_30))
                {
                    P060 = P060 + P060_30;
                }
            }
            if (functions.isValueNull(trackingId))//OK
            {
                P060_35 = payneticsUtils.getDATA_IN_LTV_FORMAT(trackingId, "35");//Additional Merchant Data
                if (functions.isValueNull(P060_35))
                {
                    P060 = P060 + P060_35;
                }
            }
            if (functions.isValueNull(electronicCommerceIndicator))//OK
            {
                if ("05".equals(ECI))
                {
                    electronicCommerceIndicator = "10";
                }
                else if ("06".equals(ECI))
                {
                    electronicCommerceIndicator = "12";
                }
                else if ("02".equals(ECI))
                {
                    electronicCommerceIndicator = "11";
                }
                else if ("01".equals(ECI))
                {
                    electronicCommerceIndicator = "13";
                }
                P060_40 = payneticsUtils.getDATA_IN_LTV_FORMAT(electronicCommerceIndicator, "40");//Indicator For Electronic Commerce
                if (functions.isValueNull(P060_40))
                {
                    P060 = P060 + P060_40;
                }
            }

            if (isForexMID.equalsIgnoreCase("Y"))//OK
            {
                String cryptoPurchase = "06";
                P060_49 = payneticsUtils.getDATA_IN_LTV_FORMAT(cryptoPurchase, "49");//Indicator For CryptoCurrency
                if (functions.isValueNull(P060_49))
                {
                    P060 = P060 + P060_49;
                }
            }
            /*if ("Y".equalsIgnoreCase(account.getIsRecurring()) && functions.isValueNull(indicatorForRecurring) && "sale".equalsIgnoreCase(transactionType))//OK
            {
                P060_41 = payneticsUtils.getDATA_IN_LTV_FORMAT(indicatorForRecurring, "41");//Indicator For Recurring
                if (functions.isValueNull(P060_41))
                {
                    P060 = P060 + P060_41;
                }
            }*/
            if (XIDBytes != null && XIDBytes.length > 0 && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                P060_61 = payneticsUtils.getDATA_IN_LTV_FORMAT(XIDBytes, "61");//xid
                if (functions.isValueNull(P060_61))
                {
                    P060 = P060 + P060_61;
                }
            }
            if (CAVVBytes != null && CAVVBytes.length > 0 && ("80".equals(P046) || "83".equals(P046)))//OK
            {
                P060_62 = payneticsUtils.getDATA_IN_LTV_FORMATForCAVV(CAVVBytes, "62");//CAVV
                if (functions.isValueNull(P060_62))
                {
                    P060 = P060 + P060_62;
                }
            }

            if (functions.isValueNull(UCAF) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                P060_63 = payneticsUtils.getDATA_IN_LTV_FORMAT(UCAF, "63");//UCAF
                if (functions.isValueNull(P060_63))
                {
                    P060 = P060 + P060_63;
                }
            }

            if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                P060_81 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getPaymentFacilitatorId())), "81");//Payment Facilitator ID
                if (functions.isValueNull(P060_81))
                {
                    P060 = P060 + P060_81;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getIndependentSalesOrganization()) && ("81".equals(P046) || "82".equals(P046)))//OK
            {
                P060_82 = payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d", Integer.parseInt(gatewayAccountVO.getIndependentSalesOrganization())), "82");//ISO ID
                if (functions.isValueNull(P060_82))
                {
                    P060 = P060 + P060_82;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getPaymentFacilitatorId()) && "81".equals(P046))
            {
                String P060_83_1 = "";
                String P060_83_2 = "";
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantId()))
                {//Sub-Merchant:ID
                    P060_83_1 = String.format("%-15s", gatewayAccountVO.getSubMerchantId());
                }
                else
                {
                    P060_83_1 = String.format("%-15s", P060_83_1);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantMccCode()))
                {//Sub-Merchant:MCC code
                    P060_83_2 = String.format("%-4s", gatewayAccountVO.getSubMerchantMccCode());
                }
                else
                {
                    P060_83_2 = String.format("%-4s", P060_83_2);
                }
                P060_83 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_83_1 + P060_83_2, "83");
                if (functions.isValueNull(P060_83))
                {
                    P060 = P060 + P060_83;
                }
            }
            if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()) || functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()) || functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
            {
                String P060_84_1 = "";
                String P060_84_2 = "";
                String P060_84_3 = "";

                if (functions.isValueNull(gatewayAccountVO.getSubMerchantZip()))
                {//Merchant Address Details-ZIP Code
                    P060_84_1 = String.format("%-10s", gatewayAccountVO.getSubMerchantZip());
                }
                else
                {
                    P060_84_1 = String.format("%-10s", P060_84_1);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantStreet()))
                {//Merchant Address Details-Street
                    P060_84_2 = String.format("%-25s", gatewayAccountVO.getSubMerchantStreet());
                }
                else
                {
                    P060_84_2 = String.format("%-25s", P060_84_2);
                }
                if (functions.isValueNull(gatewayAccountVO.getSubMerchantCity()))
                {//Merchant Address Details-City
                    P060_84_3 = String.format("%-13s", gatewayAccountVO.getSubMerchantCity());
                }
                else
                {
                    P060_84_3 = String.format("%-13s", P060_84_3);
                }
                P060_84 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_84_1 + P060_84_2 + P060_84_3, "84");
                if (functions.isValueNull(P060_84))
                {
                    P060 = P060 + P060_84;
                }
            }

            P060_87_1 = String.format("%-1s", "1");
            if (functions.isValueNull(P060_87_1))
            {
                P060_87_1 = payneticsUtils.getDATA_IN_LTV_FORMAT(P060_87_1, "01");
                if (functions.isValueNull(P060_87_1))
                {
                    P060_87 = payneticsUtils.getDATA_IN_LTV_LTV_FORMAT(P060_87_1, "87");
                    if (functions.isValueNull(P060_87))
                    {
                        P060 = P060 + P060_87;
                    }
                }
            }

            transactionLogger.error("P060_30:=" + P060_30);
            transactionLogger.error("P060_35:=" + P060_35);
            transactionLogger.error("P060_40:=" + P060_40);
            transactionLogger.error("P060_41:=" + P060_41);
            transactionLogger.error("P060_61:=" + P060_61);
            transactionLogger.error("P060_62:=" + P060_62);
            transactionLogger.error("P060_63:=" + P060_63);
            transactionLogger.error("P060_81:=" + P060_81);
            transactionLogger.error("P060_82:=" + P060_82);
            transactionLogger.error("P060_83:=" + P060_83);
            transactionLogger.error("P060_84:=" + P060_84);
            transactionLogger.error("P060_87:=" + P060_87);

            HashMap<String, String> hashMap = new HashMap();
            hashMap.put("MTYP", MTI);
            hashMap.put("P002", P002);
            hashMap.put("P003", P003);
            hashMap.put("P004", P004);
            hashMap.put("P012", P012);
            hashMap.put("P013", P013);
            hashMap.put("P014", P014);
            hashMap.put("P017", P017);
            hashMap.put("P022", P022);
            hashMap.put("P025", P025);
            hashMap.put("P032", P032);
            hashMap.put("P041", P041);
            hashMap.put("P042", P042);
            hashMap.put("P043", P043);
            hashMap.put("P046", P046);
            hashMap.put("P049", P049);
            hashMap.put("P060", P060);
            hashMap.put("P063", P063);

            synchronized (this)
            {
                nextSequenceNumber = payneticsUtils.getNextSequenceNumber(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));

                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();
                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());

                hashMap.put("P057", P057);
                hashMap.put("P011", P011);

                boolean checkChannelAvailability = payneticsUtils.checkChannelAvailability(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
                if (!checkChannelAvailability)
                {
                    //putting 5 sec in sleep
                    try
                    {
                        transactionLogger.error("before sleep");
                        TimeUnit.SECONDS.sleep(5);
                        transactionLogger.error("after sleep");
                    }
                    catch (InterruptedException ire)
                    {
                        transactionLogger.error("InterruptedException:::::" + ire);
                    }

                    boolean checkChannelAvailabilityAfter5Sec = payneticsUtils.checkChannelAvailability(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId(), payneticsUtils.getCCTIID(genericCardDetailsVO.getCardNum()));
                    if (!checkChannelAvailabilityAfter5Sec)
                    {
                        try
                        {
                            InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH") + "paynetics_basic_v6.xml"));
                            GenericPackager packager = new GenericPackager(inputStream);
                            PzNACChannel channel = null;

                            ISOMsg isoMsg = payneticsUtils.getISOMessage(hashMap, packager);

                            Logger logger = new Logger();
                            logger.addListener(new SimpleLogListener(System.out));

                            if (isTest)
                            {
                                channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                                ((LogSource) channel).setLogger(logger, "test-channel");
                            }
                            else
                            {
                                channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                                ((LogSource) channel).setLogger(logger, "live-channel");
                            }

                            nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, RB.getString("Env"));

                            P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());
                            P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                            hashMap.put("P011", P011);
                            hashMap.put("P057", P057);
                        }
                        catch (IOException e)
                        {
                            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                        }
                        catch (ISOException e)
                        {
                            PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                        }
                    }
                    else
                    {
                        transactionLogger.error("channel is open now");
                    }
                }
                else
                {
                    transactionLogger.error("channel is open");
                }

                //record the data.
                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                if (inserted)
                {
                    transactionLogger.error("Recorded channel request:" + inserted);
                }
            }

            try
            {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(RB.getString("CONFIGURATION_FILE_PATH") + "paynetics_basic_v6.xml"));
                GenericPackager packager = new GenericPackager(inputStream);
                PzNACChannel channel = null;

                ISOMsg isoMsg = payneticsUtils.getISOMessage(hashMap, packager);

                Logger logger = new Logger();
                logger.addListener(new SimpleLogListener(System.out));

                if (isTest)
                {
                    channel = new PzNACChannel(RB.getString("TestServerIP"), Integer.parseInt(RB.getString("TestServerPORT")), packager, null);
                    ((LogSource) channel).setLogger(logger, "test-channel");
                }
                else
                {
                    channel = new PzNACChannel(RB.getString("LIVEServerIP"), Integer.parseInt(RB.getString("LIVEServerPORT")), packager, null);
                    ((LogSource) channel).setLogger(logger, "live-channel");
                }
                try
                {
                    ISOMsg _200_ResMessage = null;
                    String env = RB.getString("Env");
                    if ("Y".equals(isTestWithSimulator))
                    {
                        env = "development";
                    }
                    if ("development".equals(env))
                    {
                        byte[] data = isoMsg.pack();
                        transactionLogger.error(transactionType + " sample transaction byte length:" + data.length);
                        String strMessage = ISOUtil.hexString(data);
                        transactionLogger.error(transactionType + " sample transaction packed data:" + strMessage);

                        _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);//payneticsUtils.get_SequenceNumberError_SampleResponseMsg(isoMsg);
                        if (_200_ResMessage != null)
                        {
                            String responseCode = _200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                                //errorName = getErrorName("00");
                            }
                            else if ("06".equals(_200_ResMessage.getString("39")))
                            {
                                status = "pending";
                                descriptor = "";
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }
                            if ("pending".equals(status))
                            {
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);

                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());

                                nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);

                                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57", P057);


                                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }

                                data = isoMsg.pack();
                                transactionLogger.error(transactionType + " synchronized sample transaction byte length:" + data.length);
                                strMessage = ISOUtil.hexString(data);
                                transactionLogger.error(transactionType + " synchronized sample transaction packed data:" + strMessage);

                                _200_ResMessage = payneticsUtils.get_0200_SampleResponseMsg(isoMsg);
                                responseCode = _200_ResMessage.getString("39");
                                if ("00".equalsIgnoreCase(responseCode))
                                {
                                    status = "success";
                                    descriptor = account.getDisplayName();
                                    // errorName = getErrorName("00");
                                }
                                else
                                {
                                    status = "failed";
                                    descriptor = "";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedId = _200_ResMessage.getString("38");
                            statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                            remark = PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                    else
                    {
                        byte[] data = isoMsg.pack();
                        transactionLogger.error(transactionType + " transaction byte length:" + data.length);
                        String strMessage = ISOUtil.hexString(data);
                        transactionLogger.error(transactionType + " transaction packed data:" + strMessage);

                        channel.connect();
                        channel.send(isoMsg);
                        _200_ResMessage = channel.receive();
                        channel.disconnect();

                        if (_200_ResMessage != null)
                        {
                            String responseCode = _200_ResMessage.getString("39");
                            if("00".equalsIgnoreCase(responseCode)){
                                status="success";
                                descriptor=account.getDisplayName();
                                //errorName = getErrorName("00");
                            }
                            else if ("06".equals(_200_ResMessage.getString("39")))
                            {
                                status = "pending";
                                descriptor = "";
                            }else{
                                status="failed";
                                descriptor="";
                                errorName = getErrorName(responseCode);
                            }

                            if ("pending".equals(status))
                            {
                                payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                                P011 = payneticsUtils.getNextSTAN(gatewayAccountVO.getMerchantId(), gatewayAccountVO.getTerminalId());

                                nextSequenceNumber = sequenceNumberSynchronizeProcess(channel, isoMsg, env);

                                P057 = ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber)) + gatewayAccountVO.getGenerationNumber();

                                isoMsg.set("11", P011);
                                isoMsg.set("57", P057);

                                boolean inserted = payneticsUtils.registerSTANSeqData(trackingId, P011, P042, P041, nextSequenceNumber, P046, transactionType);
                                if (inserted)
                                {
                                    transactionLogger.error("Recorded channel request:" + inserted);
                                }

                                data = isoMsg.pack();
                                transactionLogger.error(transactionType + " synchronized transaction byte length:" + data.length);
                                strMessage = ISOUtil.hexString(data);
                                transactionLogger.error(transactionType + " synchronized transaction packed data:" + strMessage);

                                channel.connect();
                                channel.send(isoMsg);
                                _200_ResMessage = channel.receive();
                                channel.disconnect();

                                responseCode = _200_ResMessage.getString("39");
                                if ("00".equalsIgnoreCase(responseCode))
                                {
                                    status = "success";
                                    descriptor = account.getDisplayName();
                                    //errorName = getErrorName("00");
                                }
                                else
                                {
                                    status = "failed";
                                    descriptor = "";
                                    errorName = getErrorName(responseCode);
                                }
                            }
                            payneticsUtils.updatePayneticsDetails(trackingId, _200_ResMessage.getString("38"), _200_ResMessage.getString("39"), _200_ResMessage.getString("44"), P004);
                            bankApprovedId = _200_ResMessage.getString("38");
                            statusDescription = PayneticsErrorCodes.getErrorCode(responseCode);
                            remark = PayneticsErrorCodes.getErrorCode(responseCode);
                        }
                    }
                }
                catch (ISOException e)
                {
                    status = "failed";
                    statusDescription = "ACQUIRER TEMPORARILY NOT REACHABLE";
                    remark = "ACQUIRER TEMPORARILY NOT REACHABLE";
                    transactionLogger.error("ACQUIRER TEMPORARILY NOT REACHABLE");
                }
            }
            catch (IOException e)
            {
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing 3d transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (ISOException e){
                PZExceptionHandler.raiseTechnicalViolationException(PayneticsGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Technical Exception while placing 3d transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }

            if ("Y".equalsIgnoreCase(account.getIsRecurring()))
            {
                String cardNumber = genericCardDetailsVO.getCardNum();
                String first_six = cardNumber.substring(0, 6);
                String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                RecurringManager recurringManager = new RecurringManager();
                if ("success".equalsIgnoreCase(status))
                {
                    recurringManager.updateRbidForSuccessfullRebill(bankApprovedId, first_six, last_four, trackingId);
                }
                else
                {
                    recurringManager.deleteEntryForPFSRebill(trackingId);
                }
            }
        }
        else
        {
            status = "failed";
            statusDescription = "3D Authentication Failed";
            remark = "3D Authentication Failed";
        }

        commResponseVO.setTransactionType(transactionType);
        commResponseVO.setStatus(status);
        commResponseVO.setTransactionId(bankApprovedId);
        commResponseVO.setDescriptor(descriptor);
        commResponseVO.setDescription(statusDescription);
        commResponseVO.setRemark(remark);
        commResponseVO.setResponseTime(responseTime);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(currency);
        commResponseVO.setTmpl_Amount(tmpl_amount);
        commResponseVO.setTmpl_Currency(tmpl_currency);
        if (functions.isValueNull(errorName))
            commResponseVO.setErrorName(errorName);

        return commResponseVO;

    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Entered into  processInquiry");
        return null;
    }


    public synchronized String sequenceNumberSynchronizeProcess(PzNACChannel channel, ISOMsg transIsoMsg, String env) throws IOException, ISOException
    {//OK
        GenericPackager genericPackager = (GenericPackager) transIsoMsg.getPackager();
        String sequenceNumber = "";
        int sequenceNumber1 = 0;

        ISOMsg _800Msg = new ISOMsg();
        _800Msg.setPackager(genericPackager);
        _800Msg.setMTI("0800");
        _800Msg.set("11", transIsoMsg.getString("11"));
        _800Msg.set("12", transIsoMsg.getString("12"));
        _800Msg.set("13", transIsoMsg.getString("13"));
        _800Msg.set("25", "52");
        _800Msg.set("32", transIsoMsg.getString("32"));
        _800Msg.set("41", transIsoMsg.getString("41"));
        _800Msg.set("42", transIsoMsg.getString("42"));
        _800Msg.set("46", transIsoMsg.getString("46"));
        _800Msg.set("57", transIsoMsg.getString("57"));

        byte[] _800_data = _800Msg.pack();
        transactionLogger.error("sequence number synchronization process:0800 data byte length:" + _800_data.length);
        String strMessage = ISOUtil.hexString(_800_data);
        transactionLogger.error("sequence number synchronization process:0800 data packed data:" + strMessage);

        ISOMsg _800_resMessage = null;
        if ("development".equals(env))
        {
            PayneticsUtils payneticsUtils = new PayneticsUtils();
            _800_resMessage = payneticsUtils.get0800SampleResponseMessage(_800Msg);
            if (_800_resMessage != null)
            {
                String responseCode = _800_resMessage.getString("39");
                if ("00".equalsIgnoreCase(responseCode))
                {
                    sequenceNumber1 = Integer.parseInt(StringUtils.substring(_800_resMessage.getString("57").replace("F", ""), 0, 8));
                }
            }
        }
        else
        {
            channel.connect();
            channel.send(_800Msg);
            _800_resMessage = channel.receive();
            if (_800_resMessage != null)
            {
                String responseCode = _800_resMessage.getString("39");
                if ("00".equalsIgnoreCase(responseCode))
                {
                    sequenceNumber1 = Integer.parseInt(StringUtils.substring(_800_resMessage.getString("57").replace("F", ""), 0, 8));
                }
            }
            channel.disconnect();
        }
        sequenceNumber = String.format("%08d", sequenceNumber1 + 1);
        return sequenceNumber;
    }

    public boolean standardAutoReversal(PzNACChannel channel, ISOMsg transIsoMsg) throws IOException, ISOException
    {
        boolean isReversed = false;
        GenericPackager genericPackager = (GenericPackager) transIsoMsg.getPackager();

        ISOMsg _400_Msg = new ISOMsg();
        _400_Msg.setPackager(genericPackager);
        _400_Msg.setMTI("0400");
        _400_Msg.set("2", transIsoMsg.getString("2"));
        _400_Msg.set("3", transIsoMsg.getString("3"));
        _400_Msg.set("4", transIsoMsg.getString("4"));
        _400_Msg.set("11", transIsoMsg.getString("5"));
        _400_Msg.set("12", transIsoMsg.getString("12"));
        _400_Msg.set("13", transIsoMsg.getString("13"));
        _400_Msg.set("14", transIsoMsg.getString("14"));
        _400_Msg.set("17", transIsoMsg.getString("17"));
        _400_Msg.set("22", transIsoMsg.getString("22"));
        _400_Msg.set("25", transIsoMsg.getString("25"));
        _400_Msg.set("32", transIsoMsg.getString("32"));
        _400_Msg.set("37", "000001" + transIsoMsg.getString("11"));
        _400_Msg.set("41", transIsoMsg.getString("41"));
        _400_Msg.set("42", transIsoMsg.getString("42"));
        _400_Msg.set("43", transIsoMsg.getString("43"));
        _400_Msg.set("46", transIsoMsg.getString("46"));
        _400_Msg.set("49", transIsoMsg.getString("49"));
        _400_Msg.set("57", transIsoMsg.getString("57"));//to be updated
        _400_Msg.set("60", transIsoMsg.getString("60"));
        _400_Msg.set("63", "3");

        byte[] _400_data = _400_Msg.pack();
        transactionLogger.error("_400_data byte length:" + _400_data.length);
        String strMessage = ISOUtil.hexString(_400_data);
        transactionLogger.error("_400_data packed data:" + strMessage);

        channel.connect();
        if (channel.isConnected())
        {
            channel.send(_400_Msg);
            ISOMsg _400_resMessage = channel.receive();
            if (_400_resMessage != null)
            {
                String responseCode = _400_resMessage.getString("39");
                if ("00".equalsIgnoreCase(responseCode))
                {
                    isReversed = true;
                }
            }
            channel.disconnect();
        }
        return isReversed;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    private String getErrorName(String errorCode)
    {
        switch (errorCode)
        {
            case "02":
                return "REJECTED_CALL_VOICE_AUTHORIZATION";

            case "03":
                return "REJECTED_INVALID_MEMBER";

            case "04":
                return "REJECTED_RETAIN_CARD";

            case "05":
                return "REJECTED_AUTHORIZATION_DECLINED";

            case "06":
                return "REJECTED_SEQ_GENERATION_NUMBER_ERROR";

            case "09":
                return "REJECTED_WAIT_MESSAGE_FOR_POS_SYSTEM";

            case "10":
                return "REJECTED_PARTIAL_APPROVAL";

            case "12":
                return "REJECTED_INVALID_TRANS";

            case "13":
                return "REJECTED_INVALID_AMOUNT";

            case "14":
                return "REJECTED_INVALID_CARD";

            case "21":
                return "REJECTED_NO_ACTION_TAKEN";

            case "30":
                return "REJECTED_FORMAT_ERROR";

            case "33":
                return "REJECTED_CARD_EXPIRED";

            case "34":
                return "REJECTED_SUSPICION_OF_MANIPULATION";

            case "40":
                return "REJECTED_FUNCT_NOT_SUPPORTED";

            case "43":
                return "REJECTED_STOLEN_CARD";

            case "55":
                return "REJECTED_INVALID_PID";

            case "56":
                return "REJECTED_CARD_NOT_AUTHORIZED";

            case "57":
                return "REJECTED_REF_TRANSACTION_NOT_CARRIED";

            case "58":
                return "REJECTED_TERMINAL";

            case "62":
                return "REJECTED_RESTRICTED_CARD";

            case "64":
                return "REJECTED_HIGHER_TRANS_AMOUNT";

            case "65":
                return "REJECTED_CONTACTLESS_TRANS_DECLINED";

            case "75":
                return "REJECTED_INVALID_PIN";

            case "77":
                return "REJECTED_PIN_NECESSARY";

            case "78":
                return "REJECTED_TRANSACTION_STOPPED";

            case "79":
                return "REJECTED_REVOCATION_OF_ORDER";

            case "80":
                return "REJECTED_AMOUNT_NOT_AVAILABLE";

            case "81":
                return "REJECTED_MESSAGE_FLOW_ERROR";

            case "91":
                return "REJECTED_ISSUER_NOT_REACHABLE";

            case "92":
                return "REJECTED_CARD_NOT_PROCESSED";

            case "96":
                return "REJECTED_PROCESSING_NOT_POSSIBLE";

            case "97":
                return "REJECTED_SECURITY_BREACH";

            case "98":
                return "REJECTED_DATE_NOT_PLAUSIBLE";

            case "99":
                return "REJECTED_ERROR_IN_PAC_ENCRYPTION";

            default:
                return "TRANSACTION_REJECTED";

        }
    }
}