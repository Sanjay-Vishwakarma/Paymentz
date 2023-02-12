package payment.util;

import com.directi.pg.Functions;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericDeviceDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BinVerificationManager;
import com.manager.TerminalManager;
import com.manager.vo.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectKitValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import com.payment.validators.vo.ReserveField2VO;
import org.apache.log4j.LogManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/15/14
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadRequest
{
    private static TransactionLogger transactionLogger      = new TransactionLogger(ReadRequest.class.getName());
    private static com.directi.pg.Logger log                = new com.directi.pg.Logger(ReadRequest.class.getName());
    private static org.apache.log4j.Logger facileroLogger   = LogManager.getLogger("facilerolog");

    public static CommonValidatorVO getRequestParametersForRebill(HttpServletRequest req)
    {
        CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO                 = new MerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO         = new GenericTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO     = new GenericAddressDetailsVO();

        merchantDetailsVO.setMemberId(getValue("toid", req));
        genericTransDetailsVO.setTotype(getValue("totype", req));
        genericTransDetailsVO.setAmount(getValue("amount", req));
        genericTransDetailsVO.setOrderId(getValue("orderid", req));
        commonValidatorVO.setTrackingid(getValue("trackingid", req));
        genericTransDetailsVO.setChecksum(getValue("checksum", req));
        genericTransDetailsVO.setCvv(PzEncryptor.encryptCVV(getValue("cvv", req)));
        genericAddressDetailsVO.setIp(Functions.getIpAddress(req));
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);

        return commonValidatorVO;
    }

    public static DirectKitValidatorVO getRequestParametersForSale(HttpServletRequest req)
    {
        Hashtable requestMap = new Hashtable();
        DirectKitValidatorVO directKitValidatorVO=new DirectKitValidatorVO();
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericDeviceDetailsVO genericDeviceDetails=new GenericDeviceDetailsVO();
        RecurringBillingVO recurringBillingVO = null;
        ReserveField2VO reserveField2VO = null;
        Functions functions = new Functions();
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();

        requestMap.put("action","Sale");
        if(req.getParameter("version")!=null && !req.getParameter("version").equals(""))
        {
            directKitValidatorVO.setVersion(req.getParameter("version"));
        }
        else
        {
            directKitValidatorVO.setVersion(" ");
        }

        directKitValidatorVO.setAction("Sale");
        //memberdetails
        merchantDetailsVO.setMemberId(getValue("toid", req));
        genericTransDetailsVO.setTotype(getValue("totype",req));
        genericTransDetailsVO.setRedirectUrl(getValue("redirecturl",req));
        transactionLogger.debug("RedirectURL---"+getValue("redirecturl",req));
        genericTransDetailsVO.setChecksum(getValue("checksum",req));
        commonValidatorVO.setTrackingid(getValue("trackingid",req));
        //order
        genericTransDetailsVO.setAmount(getValue("amount",req));
        genericTransDetailsVO.setOrderDesc(getValue("orderdescription",req));
        genericTransDetailsVO.setOrderId(getValue("description",req));
        genericTransDetailsVO.setCurrency(getValue("currency",req));
        //customerdetails
        genericAddressDetailsVO.setCountry(getValue("countrycode",req));
        genericAddressDetailsVO.setCity(getValue("city",req));
        genericAddressDetailsVO.setState(getValue("state",req));
        genericAddressDetailsVO.setZipCode(getValue("zip",req));
        genericAddressDetailsVO.setStreet(getValue("street",req));
        genericAddressDetailsVO.setPhone(getValue("telno",req));
        genericAddressDetailsVO.setEmail(getValue("emailaddr",req));
        genericAddressDetailsVO.setLanguage(getValue("language",req));
        genericAddressDetailsVO.setFirstname(getValue("firstname",req));
        genericAddressDetailsVO.setLastname(getValue("lastname",req));
        genericAddressDetailsVO.setBirthdate(getValue("birthdate",req));
        genericAddressDetailsVO.setSsn(getValue("ssn",req));
        genericAddressDetailsVO.setTelnocc(getValue("telnocc",req));
        genericAddressDetailsVO.setCardHolderIpAddress(getValue("cardholderipaddress",req));
        String ip = Functions.getIpAddress(req);
        String reqIp = "";

        if(ip.contains(","))
        {
            String sIp[] = ip.split(",");
            reqIp = sIp[0].trim();
        }
        else
        {
            reqIp = ip;
        }
        transactionLogger.error("ip DK---"+ip);
        transactionLogger.error("reqIp ip DK---"+reqIp);
        genericAddressDetailsVO.setIp(reqIp);
        //genericAddressDetailsVO.setIp("127.0.0.1");
        //carddetails
        genericCardDetailsVO.setCardNum(getValue("cardnumber",req));
        genericCardDetailsVO.setcVV(getValue("cvv",req));
        genericCardDetailsVO.setExpMonth(getValue("expiry_month",req));
        genericCardDetailsVO.setExpYear(getValue("expiry_year",req));
        genericCardDetailsVO.setCardHolderName(getValue("firstname",req)+" "+getValue("lastname",req) );
        genericCardDetailsVO.setIBAN(getValue("iban", req));
        genericCardDetailsVO.setBIC(getValue("bic", req));
        genericCardDetailsVO.setMandateId(getValue("mandateId", req));

        //paymentdetails
       // directKitValidatorVO.setPaymentType(getValue("paymenttype",req));
        if(functions.isValueNull(getValue("paymenttype", req)))
        {
            if (functions.isNumericVal1(getValue("paymenttype", req)))
            {
                directKitValidatorVO.setPaymentType(getValue("paymenttype", req));
            }
            else
            {
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(getValue("paymenttype", req)));
                transactionLogger.error("PaymentType is ---"+GatewayAccountService.getPaymentId(getValue("paymenttype", req)));
            }
            //standardKitValidatorVO.setPaymentType(getValue("paymenttype", req));
            if(functions.isValueNull(directKitValidatorVO.getPaymentType()))
                directKitValidatorVO.setPaymentMode(GatewayAccountService.getPaymentTypes(directKitValidatorVO.getPaymentType()));
        }
        // directKitValidatorVO.setCardType(getValue("cardtype",req));
        if(functions.isValueNull(getValue("cardtype",req)))
        {
            if (functions.isNumericVal1(getValue("cardtype",req)))
            {
                directKitValidatorVO.setCardType(getValue("cardtype", req));
            }
            else
            {
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(getValue("cardtype", req)));
                transactionLogger.error("CardType is ---"+GatewayAccountService.getPaymentId(getValue("cardtype", req)));
            }
            if (functions.isValueNull(directKitValidatorVO.getCardType()))
                directKitValidatorVO.setPaymentBrand(GatewayAccountService.getCardType(directKitValidatorVO.getCardType()));
        }
        directKitValidatorVO.setTerminalId(getValue("terminalid",req));
        merchantDetailsVO.setAccountId(getValue("accountid",req));
        commonValidatorVO.setTrackingid(getValue("trackingid",req));
        directKitValidatorVO.setRequestedIP(Functions.getIpAddress(req));
        //commonValidatorVO.setReserveField2VO(getValue("reservedField2", req));

        //RecurringDetails
        if(!getValue("reservedField1",req).equals(""))
        {
            recurringBillingVO = new RecurringBillingVO();
            recurringBillingVO.setReqField1(getValue("reservedField1",req));
            if(getValue("reservedField1",req).contains("|"))
            {
                String values[] = getValue("reservedField1",req).split("\\|");
                if(values.length==4)
                {
                    recurringBillingVO.setRecurring(values[0]);
                    recurringBillingVO.setInterval(values[1]);
                    recurringBillingVO.setFrequency(values[2]);
                    recurringBillingVO.setRunDate(values[3]);

                }
            }
        }

        if(functions.isValueNull(getValue("reservedField2",req)) && getValue("reservedField2",req).contains("|"))
        {
            reserveField2VO = new ReserveField2VO();
            String resField2 = getValue("reservedField2",req);
            String values[] = resField2.split("\\|");
            if(values.length==3)
            {
                reserveField2VO.setRoutingNumber(values[2]);
                reserveField2VO.setAccountNumber(values[1]);
                reserveField2VO.setAccountType(values[0]);
            }
        }
        genericDeviceDetails.setUser_Agent(req.getHeader("User-Agent"));

        directKitValidatorVO.setCreateRegistration(getValue("createRegistration",req));

        directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
        directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        directKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        directKitValidatorVO.setReserveField2VO(reserveField2VO);
        directKitValidatorVO.setSplitPaymentVO(splitPaymentVO);
        directKitValidatorVO.setDeviceDetailsVO(genericDeviceDetails);

        return directKitValidatorVO;
    }

    public static CommonValidatorVO getSpecificRequestParametersForSale1(HttpServletRequest req)
    {
        CommonValidatorVO commonValidatorVO     = new CommonValidatorVO();
        Functions functions                     = new Functions();
        MerchantDetailsVO merchantDetailsVO     = new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = new GenericTransDetailsVO();
        RecurringBillingVO recurringBillingVO           = null;
        TerminalVO terminalVO           = new TerminalVO();
        ReserveField2VO reserveField2VO = null;
        SplitPaymentVO splitPaymentVO   = new SplitPaymentVO();
        GenericDeviceDetailsVO genericDeviceDetails     = new GenericDeviceDetailsVO();
        String userAgent                                = req.getHeader("User-Agent");

       /* Enumeration enumeration=req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = (String) req.getParameter(key);

            log.debug("SingleCall Name ---> " + key + " Value---> "+value);
        }*/

        log.debug("toid---" + getValue("toid", req));
        log.debug("trackingid---" + getValue("trackingid", req));
        log.debug("paymenttype---" + getValue("paymodeid", req));
        log.debug("cardtype---" + getValue("cardtypeid", req));
        log.debug("paymentBrand---" + getValue("paymentBrand", req));
        log.debug("paymentMode---" + getValue("paymentMode", req));
        log.debug("accountid---" + getValue("accountid", req));
        log.debug("merchantlogoname---" + getValue("merchantlogoname", req));
        log.debug("Amount---" + getValue("amount", req));
        log.debug("Currency---" + getValue("currency", req));
        log.debug("deviceNo ---" + getValue("deviceNo", req));
        log.debug("uniqueId ---" + getValue("uniqueId", req));
        log.debug("vpa_address ---"+getValue("upi", req));

        if (functions.isValueNull(getValue("upi",req)))
        {
           commonValidatorVO.setVpa_address(getValue("upi", req));
        }
        if(functions.isValueNull(getValue("deviceNo", req))){
            commonValidatorVO.setDeviceId(getValue("deviceNo", req));
        }
        if(functions.isValueNull(getValue("uniqueId", req))){
            commonValidatorVO.setUniqueId(getValue("uniqueId", req));
        }
        if(functions.isValueNull(getValue("amount", req))){
            genericTransDetailsVO.setAmount(getValue("amount", req));
        }
        if(functions.isValueNull(getValue("currency", req))){
            genericTransDetailsVO.setCurrency(getValue("currency", req));
        }
        merchantDetailsVO.setMemberId(getValue("toid", req));
        merchantDetailsVO.setAccountId(getValue("accountid", req));
        merchantDetailsVO.setKey(getValue("clkey", req));
        merchantDetailsVO.setPartnerId(getValue("partnerId", req));
        merchantDetailsVO.setIsPoweredBy(getValue("isPoweredBy", req));
        merchantDetailsVO.setOnlineFraudCheck(getValue("onlineFraudCheck", req));
        merchantDetailsVO.setMultiCurrencySupport(getValue("multiCurrencySupport", req));
        merchantDetailsVO.setMerchantLogoName(getValue("merchantlogoname", req));
        merchantDetailsVO.setLogoName(getValue("partnerlogoname", req));//Partner Logo Name
        merchantDetailsVO.setMerchantLogo(getValue("merchantlogoflag", req));//Merchant Logo Flag
        merchantDetailsVO.setPartnerLogoFlag(getValue("partnerlogoflag", req));//Partner Logo flag
        merchantDetailsVO.setSupportSection(getValue("supportSection", req));
        merchantDetailsVO.setAddress(getValue("merchant_address", req));
        merchantDetailsVO.setCity(getValue("merchant_city", req));
        merchantDetailsVO.setState(getValue("merchant_state", req));
        merchantDetailsVO.setZip(getValue("merchant_zip", req));
        merchantDetailsVO.setCountry(getValue("merchant_country", req));
        merchantDetailsVO.setTelNo(getValue("merchant_telno", req));
        merchantDetailsVO.setCompany_name(getValue("company_name", req));
        merchantDetailsVO.setBinRouting(getValue("binRouting", req));
        merchantDetailsVO.setConsentFlag(getValue("consentStmnt", req));
        merchantDetailsVO.setLimitRouting(getValue("limitRouting", req));
        merchantDetailsVO.setMerchantOrderDetailsDisplay(getValue("merchantOrderDetails", req));
        merchantDetailsVO.setMarketPlace(getValue("marketPlace", req));
        merchantDetailsVO.setCheckoutTimerFlag(getValue("checkoutTimerFlag", req));
        merchantDetailsVO.setCheckoutTimerTime(getValue("checkoutTimerTime", req));
        merchantDetailsVO.setCardExpiryDateCheck(getValue("cardExpiryDateCheck",req));
       // merchantDetailsVO.setCustomerId(getValue("customerId",req));

        /*genericTransDetailsVO.setFromid(getValue("fromid", req));
        genericTransDetailsVO.setFromtype(getValue("fromtype", req));*/

        /*terminalVO.setAccountId(getValue("accountid", req));
        terminalVO.setAddressValidation(getValue("addressValidation", req));
        terminalVO.setAddressDetails(getValue("addressDetails", req));
        terminalVO.setIsEmailWhitelisted(getValue("isEmailWhitelisted", req));
        terminalVO.setIsCardWhitelisted(getValue("isCardWhitelisted", req));
        terminalVO.setCardDetailRequired(getValue("isCardDetailsRequired", req));
        terminalVO.setIsRecurring(getValue("isAutomaticRecurring", req));
        terminalVO.setAddressDetails(getValue("isManualRecurring", req));
        terminalVO.setAddressDetails(getValue("max_transaction_amount", req));
        terminalVO.setAddressDetails(getValue("min_transaction_amount", req));
        terminalVO.setAddressDetails(getValue("currency_conversion", req));
        terminalVO.setAddressDetails(getValue("conversion_currency", req));
        terminalVO.setAddressDetails(getValue("isPSTTerminal", req));
        terminalVO.setAddressDetails(getValue("reject3DCard", req));
        terminalVO.setCardTypeId(getValue("cardtypeid", req));
        terminalVO.setPaymodeId(getValue("paymodeid", req));*/
        terminalVO.setAutoRedirectRequest("N");
        commonValidatorVO.setTrackingid(getValue("trackingid", req));
        commonValidatorVO.setTerminalId(getValue("terminalid", req));
        commonValidatorVO.setAccountId(getValue("requestAccountid", req));
        commonValidatorVO.setConsentStmnt(getValue("consentStmnt", req));
        commonValidatorVO.setAttemptThreeD(getValue("attemptThreeD", req));//TODO set from CC and DC
        commonValidatorVO.setSenderBankCode(getValue("senderBankCode",req));//TODO set from Ideal and Sofort

        commonValidatorVO.setPaymentType(getValue("paymodeid", req));
        commonValidatorVO.setCardType(getValue("cardtypeid", req));
        /*if(functions.isValueNull(getValue("paymentBrand", req)))
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(getValue("paymentBrand", req)));*/
        commonValidatorVO.setPaymentBrand(getValue("paymentBrand", req));
        commonValidatorVO.setPaymentMode(getValue("paymentMode", req));
        genericAddressDetailsVO.setCustomerid(getValue("customerid", req));
        commonValidatorVO.setCustomerId(getValue("customerid", req));
        if(commonValidatorVO.getPaymentBrand().contains("_"))
        {
            commonValidatorVO.setPaymentBrand(commonValidatorVO.getPaymentBrand().replaceAll("_", " "));
            transactionLogger.error("commonValidatorVO.getPaymentBrand()---->"+commonValidatorVO.getPaymentBrand());
        }
        if(functions.isValueNull(commonValidatorVO.getPaymentMode()) &&
                (commonValidatorVO.getPaymentMode().equalsIgnoreCase("NBI") || commonValidatorVO.getPaymentMode().equalsIgnoreCase("EWI") ||
                        commonValidatorVO.getPaymentMode().equalsIgnoreCase("NBB") || commonValidatorVO.getPaymentMode().equalsIgnoreCase("EWB"))
                )
        {
            transactionLogger.error("commonValidatorVO.getPaymentBrand()---->"+commonValidatorVO.getPaymentBrand());
            if(commonValidatorVO.getPaymentBrand().contains("-"))
            {
                String[] bankList = commonValidatorVO.getPaymentBrand().split("-");

                if(bankList.length==6)
                {
                    commonValidatorVO.setProcessorName(bankList[0]);
                    commonValidatorVO.setPaymentBrand(bankList[1]);
                if(functions.isValueNull(bankList[2]))
                    commonValidatorVO.setTerminalId(bankList[2]);
                if(functions.isValueNull(bankList[3]))
                    commonValidatorVO.setAccountId(bankList[3]);
                if(functions.isValueNull(bankList[5]))
                    commonValidatorVO.setProcessorBankName(bankList[5]);
                transactionLogger.error("bankList[5]---->"+bankList[5]);

                }
                else{
                if(bankList.length==3){
                    transactionLogger.error("inside else bankList.length==3---->"+bankList[1]);

                    commonValidatorVO.setProcessorName(bankList[0]);
                    commonValidatorVO.setPaymentBrand(bankList[1]);
                    commonValidatorVO.setProcessorBankName(bankList[2]);
                }

                }

            }
        }
        //customerdetails
        if(functions.isValueNull(getValue("country_input", req)))
            genericAddressDetailsVO.setCountry(getValue("country_input",req).trim());
        else
            genericAddressDetailsVO.setCountry("");
        if(functions.isValueNull(getValue("city", req)))
            genericAddressDetailsVO.setCity(getValue("city", req).trim());
        else
            genericAddressDetailsVO.setCity("");
        if(functions.isValueNull(getValue("state", req)))
            genericAddressDetailsVO.setState(getValue("state", req).trim());
        else
            genericAddressDetailsVO.setState("");
        if(functions.isValueNull(getValue("zip", req)))
            genericAddressDetailsVO.setZipCode(getValue("zip", req).trim());
        else
            genericAddressDetailsVO.setZipCode("");
        if(functions.isValueNull(getValue("street", req)))
            genericAddressDetailsVO.setStreet(getValue("street", req).trim());
        else
            genericAddressDetailsVO.setStreet("");
        genericAddressDetailsVO.setPhone(getValue("telno", req));
        if(functions.isValueNull(getValue("emailaddr", req)))
        {
            genericAddressDetailsVO.setEmail(getValue("emailaddr", req));
        }
        else
        {
            genericAddressDetailsVO.setEmail(getValue("requestemailaddr", req));
        }

        genericAddressDetailsVO.setLanguage(getValue("language", req));
        genericAddressDetailsVO.setBirthdate(getValue("birthDate", req));
       // genericAddressDetailsVO.setCustomerid(getValue("customerId",req));

        String splitName = getValue("firstname",req).trim();
        if(functions.isValueNull(getValue("firstname",req)) && !functions.isValueNull(getValue("lastname",req)))
        {
            if(splitName.contains(" "))
            {
                int index = splitName.lastIndexOf(" ");
                String firstName = splitName.substring(0, index).replaceAll(" ", "");
                String lastName = splitName.substring(index + 1, splitName.length());

                genericAddressDetailsVO.setFirstname(firstName);
                genericAddressDetailsVO.setLastname(lastName);
            }
            else
            {
                genericAddressDetailsVO.setFirstname(getValue("firstname",req));
                genericAddressDetailsVO.setLastname(getValue("firstname", req));
            }
        }
        else
        {
            genericAddressDetailsVO.setFirstname(getValue("firstname",req));
            genericAddressDetailsVO.setLastname(getValue("lastname",req));
        }
        if(!functions.isValueNull(getValue("firstname",req))&&(functions.isValueNull(getValue("requestfirstname",req))||functions.isValueNull(getValue("requestlastname",req))))
        {
         if(functions.isValueNull(getValue("requestlastname",req))&&functions.isValueNull(getValue("requestfirstname",req)))
         {
             genericAddressDetailsVO.setFirstname(getValue("requestfirstname",req));
             genericAddressDetailsVO.setLastname(getValue("requestlastname",req));
         }
         else if(!functions.isValueNull(getValue("requestlastname",req))&& functions.isValueNull(getValue("requestfirstname",req)))
         {
             genericAddressDetailsVO.setFirstname(getValue("requestfirstname",req));
             genericAddressDetailsVO.setLastname(getValue("requestfirstname",req));
         }
         else if(functions.isValueNull(getValue("requestlastname",req))&& !functions.isValueNull(getValue("requestfirstname",req)))
         {
             genericAddressDetailsVO.setFirstname(getValue("requestlastname",req));
             genericAddressDetailsVO.setLastname(getValue("requestlastname",req));
         }
        }
        //genericAddressDetailsVO.setBirthdate(getValue("byear",req)+getValue("bmonth",req)+getValue("bday",req));
        //genericAddressDetailsVO.setSsn(getValue("ssn",req));

        if(functions.isValueNull(getValue("emiCount",req))){
            genericTransDetailsVO.setEmiCount(getValue("emiCount",req));
        }

        genericAddressDetailsVO.setTelnocc(getValue("phone-CC",req));
        genericAddressDetailsVO.setIp(getValue("ip",req));
        genericAddressDetailsVO.setCardHolderIpAddress(getValue("customerIp",req));

        genericCardDetailsVO.setCardNum(getValue("cardnumber",req));
        genericCardDetailsVO.setcVV(getValue("cvv",req));

        //System.out.println("exp---"+getValue("expiry",req));
        if(getValue("expiry",req).contains("/"))
        {
            String[] exp = getValue("expiry",req).split("/");
            String expMonth = exp[0];
            genericCardDetailsVO.setExpMonth(expMonth);
            if(expMonth.length()==1)
                genericCardDetailsVO.setExpMonth("0"+expMonth);
            genericCardDetailsVO.setExpYear("20"+exp[1]);
        }

        /*if(functions.isValueNull(getValue("expirymonth",req)))
        {
            String expMonth = getValue("expirymonth",req);
            genericCardDetailsVO.setExpMonth(expMonth);
            if(expMonth.length()==1)
                genericCardDetailsVO.setExpMonth("0"+expMonth);
        }

        if(functions.isValueNull(getValue("expiryyear",req)))
        {
            String expYear = getValue("expiryyear",req);
            genericCardDetailsVO.setExpYear("20" + expYear);
        }*/

        genericCardDetailsVO.setCardHolderName(getValue("firstname",req)+" "+getValue("lastname",req) );
        genericCardDetailsVO.setIBAN(getValue("iban", req));
        genericCardDetailsVO.setBIC(getValue("bic", req));
        genericCardDetailsVO.setMandateId(getValue("mandateId", req));
        genericCardDetailsVO.setIsMandate(getValue("mandate", req));
        genericCardDetailsVO.setVoucherNumber(getValue("vouchernumber",req));
        genericCardDetailsVO.setSecurity_Code(getValue("securityCode",req));

        genericDeviceDetails.setUser_Agent(userAgent);
        genericDeviceDetails.setFingerprints(getValue("fingerprints",req));
        genericDeviceDetails.setAcceptHeader(req.getHeader("Accept"));

        commonValidatorVO.setParetnerId(getValue("partnerid",req));
        commonValidatorVO.setLogoName(getValue("logoname", req));
        commonValidatorVO.setPartnerName(getValue("partnername", req));
        commonValidatorVO.setActionType(getValue("action", req));
        commonValidatorVO.setInvoiceId(getValue("invoicenumber", req));
        commonValidatorVO.setDevice(getValue("device", req));

        commonValidatorVO.setIsPSTProcessingRequest(getValue("isPSTProcessingRequest", req));
        commonValidatorVO.setRequestedIP(getValue("requestedIp", req));
        log.debug("version from singlecall---" + getValue("requestedIp", req));
        commonValidatorVO.setVersion(getValue("version", req));
        commonValidatorVO.setCustomerBankId(getValue("customerBankId", req));

        if(!getValue("isRecurring",req).equals("") && !getValue("interval",req).equals("") && !getValue("frequency",req).equals(""))
        {
            recurringBillingVO = new RecurringBillingVO();
            recurringBillingVO.setRecurring(getValue("isRecurring",req));
            recurringBillingVO.setInterval(getValue("interval",req));
            recurringBillingVO.setFrequency(getValue("frequency",req));
            recurringBillingVO.setRunDate(getValue("runDate",req));
            commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
        }
        reserveField2VO = new ReserveField2VO();

        reserveField2VO.setAccountNumber(getValue("accountnumber",req));
        reserveField2VO.setRoutingNumber(getValue("routingnumber", req));
        reserveField2VO.setCheckNumber(getValue("checknumber", req));
        reserveField2VO.setAccountType(getValue("accountType",req));
        reserveField2VO.setBankName(getValue("bankName",req));
        reserveField2VO.setBankAddress(getValue("bankAddress", req));
        reserveField2VO.setBankCity(getValue("bankCity", req));
        reserveField2VO.setBankState(getValue("bankState", req));
        reserveField2VO.setBankZipcode(getValue("bankZipcode", req));

        reserveField2VO.setReservefield1(getValue("reservedField1",req));
        reserveField2VO.setReservefield2(getValue("reservedField2",req));
        reserveField2VO.setReservefield3(getValue("reservedField3",req));
        reserveField2VO.setReservefield4(getValue("reservedField4",req));
        reserveField2VO.setReservefield5(getValue("reservedField5",req));


        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setReserveField2VO(reserveField2VO);
        commonValidatorVO.setTerminalVO(terminalVO);
        commonValidatorVO.setSplitPaymentVO(splitPaymentVO);
        commonValidatorVO.setDeviceDetailsVO(genericDeviceDetails);

        return commonValidatorVO;
    }

    public static CommonValidatorVO getSpecificRequestParametersForSale(HttpServletRequest req)
    {
        transactionLogger.error( "in singlecall -------------");
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        Functions functions = new Functions();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        RecurringBillingVO recurringBillingVO = null;
        TerminalVO terminalVO = null;
        ReserveField2VO reserveField2VO = null;
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        boolean isTerminalAavailable = true;

        //Setting Merchant VO
        merchantDetailsVO.setMemberId(getValue("toid", req));
        merchantDetailsVO.setOnlineFraudCheck(getValue("onlineFraudCheck", req));
        merchantDetailsVO.setKey(getValue("key", req));
        merchantDetailsVO.setMultiCurrencySupport(getValue("multicurrencySupport", req));
        merchantDetailsVO.setPoweredBy(getValue("powerby",req));

        //
        genericTransDetailsVO.setTotype(getValue("totype",req));
        genericTransDetailsVO.setRedirectUrl(getValue("redirecturl",req));
        genericTransDetailsVO.setChecksum(getValue("checksum",req));
        genericTransDetailsVO.setAmount(getValue("amount",req));
        genericTransDetailsVO.setOrderDesc(getValue("orderdescription", req));
        genericTransDetailsVO.setOrderId(getValue("description", req));
        genericTransDetailsVO.setCurrency(getValue("currency", req));
        genericTransDetailsVO.setFromid(getValue("fromid", req));
        genericTransDetailsVO.setFromtype(getValue("fromtype", req));

        //customerdetails
        genericAddressDetailsVO.setCountry(getValue("countrycode",req));
        genericAddressDetailsVO.setCity(getValue("city", req));
        genericAddressDetailsVO.setState(getValue("state", req));
        genericAddressDetailsVO.setZipCode(getValue("zip", req));
        genericAddressDetailsVO.setStreet(getValue("street", req));
        genericAddressDetailsVO.setPhone(getValue("telno", req));
        genericAddressDetailsVO.setEmail(getValue("emailaddr", req));
        genericAddressDetailsVO.setLanguage(getValue("language", req));
        genericAddressDetailsVO.setFirstname(getValue("firstname",req));
        genericAddressDetailsVO.setLastname(getValue("lastname",req));
        genericAddressDetailsVO.setBirthdate(getValue("byear",req)+getValue("bmonth",req)+getValue("bday",req));
        if (!functions.isValueNull(genericAddressDetailsVO.getBirthdate()))
        {
            genericAddressDetailsVO.setBirthdate(getValue("dateOfBirth", req));
        }
        genericAddressDetailsVO.setSsn(getValue("ssn",req));
        genericAddressDetailsVO.setTelnocc(getValue("telnocc",req));
        genericAddressDetailsVO.setIp(getValue("ip",req));
        genericAddressDetailsVO.setCardHolderIpAddress(getValue("customerIp",req));
        genericAddressDetailsVO.setTmpl_amount(getValue("TMPL_amount",req));
        genericAddressDetailsVO.setTmpl_currency(getValue("TMPL_currency",req));

        genericCardDetailsVO.setCardNum(getValue("cardnumber",req));
        genericCardDetailsVO.setcVV(getValue("cvv",req));
        genericCardDetailsVO.setExpMonth(getValue("expiry_month",req));
        genericCardDetailsVO.setExpYear(getValue("expiry_year",req));
        genericCardDetailsVO.setCardHolderName(getValue("firstname",req)+" "+getValue("lastname",req) );
        genericCardDetailsVO.setIBAN(getValue("iban", req));
        genericCardDetailsVO.setBIC(getValue("bic", req));
        genericCardDetailsVO.setMandateId(getValue("mandateId", req));
        genericCardDetailsVO.setIsMandate(getValue("mandate", req));
        genericCardDetailsVO.setVoucherNumber(getValue("vouchernumber",req));
        genericCardDetailsVO.setSecurity_Code(getValue("securityCode",req));

        commonValidatorVO.setParetnerId(getValue("partnerid",req));
        commonValidatorVO.setLogoName(getValue("logoname", req));
        commonValidatorVO.setPartnerName(getValue("partnername", req));
        commonValidatorVO.setConsentStmnt(getValue("consentStmnt", req));
        commonValidatorVO.setTrackingid(getValue("trackingid",req));
        commonValidatorVO.setPaymentType(getValue("paymenttype", req));
        commonValidatorVO.setCardType(getValue("cardtype", req));
        commonValidatorVO.setPaymentBrand(getValue("paymentBrand", req));
        commonValidatorVO.setPaymentMode(getValue("paymentMode", req));
        commonValidatorVO.setTrackingid(getValue("trackingid", req));
        commonValidatorVO.setInvoiceId(getValue("invoicenumber", req));
        commonValidatorVO.setRequestedIP(getValue("requestedIp", req));
        commonValidatorVO.setSenderBankCode(getValue("senderBankCode", req));
        commonValidatorVO.setVersion(getValue("version", req));
        commonValidatorVO.setCustomerId(getValue("customerid", req));
        genericAddressDetailsVO.setCustomerid(getValue("customerid", req));
        commonValidatorVO.setCustomerBankId(getValue("customerBankId", req));

        if (functions.isValueNull(getValue("attemptThreeD", req)))
            commonValidatorVO.setAttemptThreeD(getValue("attemptThreeD", req));

        if(!"N".equalsIgnoreCase(getValue("binRouting", req)))
        {
            //TODO fetch terminal again
            TerminalManager terminalManager = new TerminalManager();
            if("Card".equalsIgnoreCase(getValue("binRouting", req)))
            {
                String cNum = getValue("cardnumber", req);
                terminalVO = terminalManager.getBinRoutingTerminalDetailsByCardNumber(cNum, merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());

                if (terminalVO != null)
                {
                    isTerminalAavailable = false;
                    merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                    commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                    commonValidatorVO.setIsPSTProcessingRequest(terminalVO.getIsPSTTerminal());
                    commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
                    commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
                    commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
                }
                else
                {
                    terminalVO = terminalManager.getBinRoutingTerminalDetails(merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());
                    if (terminalVO != null)
                    {
                        isTerminalAavailable = false;
                        merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                        commonValidatorVO.setIsPSTProcessingRequest(terminalVO.getIsPSTTerminal());
                        commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
                        commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
                        commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
                    }
                    else
                    {
                        isTerminalAavailable = true;
                    }
                }
            }
            else if("Bin_Country".equalsIgnoreCase(getValue("binRouting", req)))
            {
                BinVerificationManager binVerificationManager=new BinVerificationManager();
                try
                {
                    BinResponseVO binResponseVO=binVerificationManager.getBinDetailsFromFirstSix(functions.getFirstSix(genericCardDetailsVO.getCardNum()));
                    terminalVO = terminalManager.getBinCountryRoutingTerminalDetails(binResponseVO.getCountrycodeA3(), merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());
                }
                catch (PZDBViolationException e)
                {
                    log.error("Exception in Bin Country Routing ---->",e);
                }

                if (terminalVO != null)
                {
                    isTerminalAavailable = false;
                    merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                    commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                    commonValidatorVO.setIsPSTProcessingRequest(terminalVO.getIsPSTTerminal());
                    commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
                    commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
                    commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
                }
                else
                {
                    terminalVO = terminalManager.getBinRoutingTerminalDetails(merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());
                    if (terminalVO != null)
                    {
                        isTerminalAavailable = false;
                        merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                        commonValidatorVO.setIsPSTProcessingRequest(terminalVO.getIsPSTTerminal());
                        commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
                        commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
                        commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
                    }
                    else
                    {
                        isTerminalAavailable = true;
                    }
                }


            }
            else
            {
                String firstSix = getValue("cardnumber", req).substring(0, 6);
                terminalVO = terminalManager.getBinRoutingTerminalDetails(firstSix, merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());

                if (terminalVO != null)
                {
                    isTerminalAavailable = false;
                    merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                    commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                    commonValidatorVO.setIsPSTProcessingRequest(terminalVO.getIsPSTTerminal());
                    commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
                    commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
                    commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
                }
                else
                {
                    terminalVO = terminalManager.getBinRoutingTerminalDetails(merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());
                    if (terminalVO != null)
                    {
                        isTerminalAavailable = false;
                        merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                        commonValidatorVO.setIsPSTProcessingRequest(terminalVO.getIsPSTTerminal());
                        commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
                        commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
                        commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
                    }
                }
            }
        }
        if(isTerminalAavailable && !"N".equalsIgnoreCase(getValue("binRouting", req)))
        {
            TerminalVO terminalVO1=new TerminalVO();
            terminalVO1.setAccountId(getValue("accountid", req));
            terminalVO1.setTerminalId(getValue("terminalid", req));
            terminalVO1.setAddressValidation(getValue("addressValidation", req));
            terminalVO1.setAddressDetails(getValue("addressDetails", req));
            terminalVO1.setIsEmailWhitelisted(getValue("isEmailWhitelisted", req));
            terminalVO1.setIsCardWhitelisted(getValue("isCardWhitelisted", req));
            terminalVO1.setCardDetailRequired(getValue("isCardDetailsRequired", req));
            terminalVO1.setIsRecurring(getValue("isAutomaticRecurring", req));
            terminalVO1.setIsManualRecurring(getValue("isManualRecurring", req));
            terminalVO1.setWhitelisting(getValue("whitelisting", req));
            terminalVO1.setCardLimitCheckAccountLevel(getValue("cardLimitCheckAccountLevel", req));
            terminalVO1.setCardAmountLimitCheckAccountLevel(getValue("cardAmountLimitCheckAccountLevel", req));
            terminalVO1.setAmountLimitCheckAccountLevel(getValue("amountLimitCheckAccountLevel", req));
            terminalVO1.setCardLimitCheckTerminalLevel(getValue("cardLimitCheckTerminalLevel", req));
            terminalVO1.setCardAmountLimitCheckTerminalLevel(getValue("cardAmountLimitCheckTerminalLevel", req));
            terminalVO1.setAmountLimitCheckTerminalLevel(getValue("amountLimitCheckTerminalLevel", req));
            terminalVO1.setGateway_id(getValue("pgtypeid",req));
            merchantDetailsVO.setAccountId(getValue("accountid", req));
            commonValidatorVO.setTerminalId(getValue("terminalid", req));
            commonValidatorVO.setIsPSTProcessingRequest(getValue("isPSTProcessingRequest", req));
            commonValidatorVO.setReject3DCard(getValue("reject3DCard", req));
            commonValidatorVO.setCurrencyConversion(getValue("currencyConversion", req));
            commonValidatorVO.setConversionCurrency(getValue("conversionCurrency", req));
            commonValidatorVO.setRequestedTerminalVO(terminalVO1);
        }
        else if(isTerminalAavailable)
        {
            terminalVO = new TerminalVO();
            terminalVO.setAccountId(getValue("accountid", req));
            terminalVO.setTerminalId(getValue("terminalid", req));
            terminalVO.setAddressValidation(getValue("addressValidation", req));
            terminalVO.setAddressDetails(getValue("addressDetails", req));
            terminalVO.setIsEmailWhitelisted(getValue("isEmailWhitelisted", req));
            terminalVO.setIsCardWhitelisted(getValue("isCardWhitelisted", req));
            terminalVO.setCardDetailRequired(getValue("isCardDetailsRequired", req));
            terminalVO.setIsRecurring(getValue("isAutomaticRecurring", req));
            terminalVO.setIsManualRecurring(getValue("isManualRecurring", req));
            terminalVO.setWhitelisting(getValue("whitelisting", req));
            terminalVO.setCardLimitCheckAccountLevel(getValue("cardLimitCheckAccountLevel", req));
            terminalVO.setCardAmountLimitCheckAccountLevel(getValue("cardAmountLimitCheckAccountLevel", req));
            terminalVO.setAmountLimitCheckAccountLevel(getValue("amountLimitCheckAccountLevel", req));
            terminalVO.setCardLimitCheckTerminalLevel(getValue("cardLimitCheckTerminalLevel", req));
            terminalVO.setCardAmountLimitCheckTerminalLevel(getValue("cardAmountLimitCheckTerminalLevel", req));
            terminalVO.setAmountLimitCheckTerminalLevel(getValue("amountLimitCheckTerminalLevel", req));
            terminalVO.setGateway_id(getValue("pgtypeid",req));
            merchantDetailsVO.setAccountId(getValue("accountid", req));
            commonValidatorVO.setTerminalId(getValue("terminalid", req));
            commonValidatorVO.setIsPSTProcessingRequest(getValue("isPSTProcessingRequest", req));
            commonValidatorVO.setReject3DCard(getValue("reject3DCard", req));
            commonValidatorVO.setCurrencyConversion(getValue("currencyConversion", req));
            commonValidatorVO.setConversionCurrency(getValue("conversionCurrency", req));
        }

        if(!getValue("isRecurring",req).equals("") && !getValue("interval",req).equals("") && !getValue("frequency",req).equals(""))
        {
            recurringBillingVO = new RecurringBillingVO();
            recurringBillingVO.setRecurring(getValue("isRecurring",req));
            recurringBillingVO.setInterval(getValue("interval",req));
            recurringBillingVO.setFrequency(getValue("frequency",req));
            recurringBillingVO.setRunDate(getValue("runDate",req));
            commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
        }

        reserveField2VO = new ReserveField2VO();

        reserveField2VO.setAccountNumber(getValue("accountnumber",req));
        reserveField2VO.setRoutingNumber(getValue("routingnumber",req));
        reserveField2VO.setAccountType(getValue("accountType",req));

        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setReserveField2VO(reserveField2VO);
        commonValidatorVO.setTerminalVO(terminalVO);
        commonValidatorVO.setSplitPaymentVO(splitPaymentVO);

        return commonValidatorVO;
    }
    //PayProcessController
    public static CommonValidatorVO getSTDKitRequestParametersForSale(HttpServletRequest req)
    {
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        RecurringBillingVO recurringBillingVO=null;
        ReserveField2VO reserveField2VO = null;
        Functions functions = new Functions();

        log.debug("toid---"+getValue("toid", req)+"--totype---"+getValue("totype",req));

        merchantDetailsVO.setMemberId(getValue("toid", req));
        genericTransDetailsVO.setTotype(getValue("totype",req));
        genericTransDetailsVO.setAmount(getValue("amount",req));
        genericTransDetailsVO.setOrderDesc(getValue("orderdescription",req));
        genericTransDetailsVO.setOrderId(getValue("description",req));
        genericTransDetailsVO.setRedirectUrl(getValue("redirecturl",req));
        genericAddressDetailsVO.setCardHolderIpAddress(Functions.getIpAddress(req));
        //genericAddressDetailsVO.setCardHolderIpAddress("45.64.195.219");
        genericAddressDetailsVO.setIp(getValue("ipaddr",req)); //getValue("ipaddr",req)
        genericTransDetailsVO.setChecksum(getValue("checksum",req));
        genericAddressDetailsVO.setTmpl_currency(getValue("TMPL_CURRENCY",req));
        genericAddressDetailsVO.setTmpl_amount(getValue("TMPL_AMOUNT",req));
        genericAddressDetailsVO.setCountry(getValue("TMPL_COUNTRY",req).toUpperCase());
        //genericTransDetailsVO.setCurrency(getValue("TMPL_CURRENCY",req));

        genericAddressDetailsVO.setCity(getValue("TMPL_city",req));
        genericAddressDetailsVO.setState(getValue("TMPL_state", req));
        genericAddressDetailsVO.setZipCode(getValue("TMPL_zip", req));
        genericAddressDetailsVO.setStreet(getValue("TMPL_street", req));
        genericAddressDetailsVO.setPhone(getValue("TMPL_telno", req));
        genericAddressDetailsVO.setTelnocc(getValue("TMPL_telnocc", req));
        genericAddressDetailsVO.setEmail(getValue("TMPL_emailaddr", req));
        standardKitValidatorVO.setPaymentType(getValue("paymenttype", req));
        log.debug("Card type payprocess controller read request"+getValue("paymenttype",req));
        standardKitValidatorVO.setCardType(getValue("cardtype",req));
        standardKitValidatorVO.setTerminalId(getValue("terminalid",req));
        standardKitValidatorVO.setRequestedIP(Functions.getIpAddress(req));


        if(functions.isValueNull(getValue("reservedField1",req)) && getValue("reservedField1",req).contains("|"))
        {
            recurringBillingVO = new RecurringBillingVO();
            recurringBillingVO.setReqField1(getValue("reservedField1",req));
            String values[] = getValue("reservedField1",req).split("\\|");
            if(values.length==4)
            {
                recurringBillingVO.setRecurring(values[0]);
                recurringBillingVO.setInterval(values[1]);
                recurringBillingVO.setFrequency(values[2]);
                recurringBillingVO.setRunDate(values[3]);
            }
            standardKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
        }
        reserveField2VO = new ReserveField2VO();

        reserveField2VO.setAccountNumber(getValue("accountnumber",req));
        reserveField2VO.setRoutingNumber(getValue("routingnumber",req));
        reserveField2VO.setAccountType(getValue("accountType",req));


        standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        standardKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        standardKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        standardKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        standardKitValidatorVO.setReserveField2VO(reserveField2VO);

        return standardKitValidatorVO;
    }

    //StandardProcessController
    public static CommonValidatorVO getSTDProcessControllerRequestParametersForSale(HttpServletRequest req)
    {
        CommonValidatorVO standardKitValidatorVO        = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO             = new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = new GenericTransDetailsVO();
        RecurringBillingVO recurringBillingVO           = null;
        ReserveField2VO reserveField2VO = null;
        MarketPlaceVO marketPlaceVO     = new MarketPlaceVO();
        Functions functions             = new Functions();
        SplitPaymentVO splitPaymentVO   = new SplitPaymentVO();
        HttpSession session             = req.getSession(true);

        Enumeration enumeration=req.getParameterNames();
        transactionLogger.error("Standard Kit Request for---"+getValue("memberId", req));
        try
        {

            JSONObject merchantRequest = new JSONObject();
            while (enumeration.hasMoreElements())
            {
                String key      = (String) enumeration.nextElement();
                String value    = (String) req.getParameter(key);

                merchantRequest.put(key, value);
            }

           // transactionLogger.error("Merchant Request ===== " + merchantRequest);

            if (functions.isValueNull(getValue("totype", req))){
                genericTransDetailsVO.setTotype(getValue("totype", req));
            }
            transactionLogger.error("genericTransDetailsVO.getTotype() >>>>>> " + genericTransDetailsVO.getTotype());

            if(functions.isValueNull(genericTransDetailsVO.getTotype()) && genericTransDetailsVO.getTotype().equalsIgnoreCase("Facilero")){
                facileroLogger.error("Merchant Request =====> " + merchantRequest);
            }else{
                transactionLogger.error("Merchant Request =====> " + merchantRequest);
            }



            if (functions.isValueNull(getValue("memberId", req)))
                merchantDetailsVO.setMemberId(getValue("memberId", req));
            else if (functions.isValueNull(getValue("toid", req)))
                merchantDetailsVO.setMemberId(getValue("toid", req));

            if (functions.isValueNull(getValue("totype", req)))
                genericTransDetailsVO.setTotype(getValue("totype", req));

            if (functions.isValueNull(getValue("orderDescription", req)))
                genericTransDetailsVO.setOrderDesc(getValue("orderDescription", req));
            else if (functions.isValueNull(getValue("orderdescription", req)))
                genericTransDetailsVO.setOrderDesc(getValue("orderdescription", req));

            if (functions.isValueNull(getValue("merchantTransactionId", req)))
                genericTransDetailsVO.setOrderId(getValue("merchantTransactionId", req));
            else if (functions.isValueNull(getValue("description", req)))
                genericTransDetailsVO.setOrderId(getValue("description", req));

            log.debug("order id----" + genericTransDetailsVO.getOrderId());
            log.debug("order desc----" + genericTransDetailsVO.getOrderDesc());

            if (functions.isValueNull(getValue("merchantRedirectUrl", req)))
                genericTransDetailsVO.setRedirectUrl(getValue("merchantRedirectUrl", req));
            else if (functions.isValueNull(getValue("redirecturl", req)))
                genericTransDetailsVO.setRedirectUrl(getValue("redirecturl", req));

            if (functions.isValueNull(getValue("notificationUrl", req)))
                genericTransDetailsVO.setNotificationUrl(getValue("notificationUrl", req));

            if (functions.isValueNull(getValue("vpa_address", req)))
                standardKitValidatorVO.setVpa_address(getValue("vpa_address", req));

            String remoteAddr = Functions.getIpAddress(req);

            String reqIp = "";

            transactionLogger.error("ip SK1---" + remoteAddr);

            if (remoteAddr.contains(","))
            {
                String sIp[] = remoteAddr.split(",");
                reqIp = sIp[0].trim();
            }
            else
            {
                reqIp = remoteAddr;
            }
            transactionLogger.error("ip SK---" + remoteAddr);
            transactionLogger.error("reqIp ip SK---" + reqIp);

            genericAddressDetailsVO.setCardHolderIpAddress(reqIp);

            if (functions.isValueNull(getValue("ip", req)))
                genericAddressDetailsVO.setIp(getValue("ip", req)); //getValue("ipaddr",req)
            else if (functions.isValueNull(getValue("ipaddr", req)))
                genericAddressDetailsVO.setIp(getValue("ipaddr", req));

            transactionLogger.error("reqIp ip----" + genericAddressDetailsVO.getIp());
            transactionLogger.error("TMPL_telno ip----" + getValue("TMPL_telno", req));
            transactionLogger.error("phone ip----" + getValue("phone", req));

            genericTransDetailsVO.setChecksum(getValue("checksum", req));

            if (functions.isValueNull(getValue("attemptThreeD", req)))
                standardKitValidatorVO.setAttemptThreeD(getValue("attemptThreeD", req));

            if (functions.isValueNull(getValue("currency", req)))
                genericTransDetailsVO.setCurrency(getValue("currency", req).toUpperCase());
            else if (!functions.isValueNull(getValue("currency", req)) && functions.isValueNull(getValue("TMPL_CURRENCY", req)))
                genericTransDetailsVO.setCurrency(getValue("TMPL_CURRENCY", req).toUpperCase());

            if(functions.isValueNull(genericTransDetailsVO.getCurrency())&&"JPY".equalsIgnoreCase(genericTransDetailsVO.getCurrency())){
                if (functions.isValueNull(getValue("amount", req)))
                {
                    genericTransDetailsVO.setAmount(String.format("%.2f", Double.parseDouble(getValue("amount", req))));
                    genericTransDetailsVO.setChecksumAmount( getValue("amount", req));
                }
            }
            else{
                if (functions.isValueNull(getValue("amount", req)))
                    genericTransDetailsVO.setAmount(getValue("amount", req));
                    genericTransDetailsVO.setChecksumAmount(getValue("amount", req));
            }
           // facileroLogger.error("read request genericTransDetailsVO-->"+genericTransDetailsVO.getAmount());
            transactionLogger.error("read request genericTransDetailsVO-->"+genericTransDetailsVO.getAmount());
            if (functions.isValueNull(getValue("TMPL_CURRENCY", req)))
                genericAddressDetailsVO.setTmpl_currency(getValue("TMPL_CURRENCY", req).toUpperCase());
            else if (!functions.isValueNull(getValue("TMPL_CURRENCY", req)) && functions.isValueNull(getValue("currency", req)))
                genericAddressDetailsVO.setTmpl_currency(getValue("currency", req).toUpperCase());

            if (functions.isValueNull(getValue("firstName", req)))
                genericAddressDetailsVO.setFirstname(getValue("firstName", req));

            if (functions.isValueNull(getValue("lastName", req)))
                genericAddressDetailsVO.setLastname(getValue("lastName", req));

            if (functions.isValueNull(getValue("dateOfBirth", req)))
                genericAddressDetailsVO.setBirthdate(getValue("dateOfBirth", req));

            if (functions.isValueNull(getValue("TMPL_AMOUNT", req)))
            {
                genericAddressDetailsVO.setTmpl_amount(getValue("TMPL_AMOUNT", req));
            }
            else
            {
                genericAddressDetailsVO.setTmpl_amount(getValue("amount", req));
            }
            if (functions.isValueNull(getValue("country", req).toUpperCase()))
                genericAddressDetailsVO.setCountry(getValue("country", req).toUpperCase());
            else if (functions.isValueNull(getValue("TMPL_COUNTRY", req).toUpperCase()))
                genericAddressDetailsVO.setCountry(getValue("TMPL_COUNTRY", req).toUpperCase());
            else if (functions.isValueNull(getValue("countrycode", req).toUpperCase()))
                genericAddressDetailsVO.setCountry(getValue("countrycode", req).toUpperCase());

            if (functions.isValueNull(getValue("city", req)))
                genericAddressDetailsVO.setCity(getValue("city", req));
            else if (functions.isValueNull(getValue("TMPL_city", req)))
                genericAddressDetailsVO.setCity(getValue("TMPL_city", req));

            if (functions.isValueNull(getValue("state", req)))
                genericAddressDetailsVO.setState(getValue("state", req));
            else if (functions.isValueNull(getValue("TMPL_state", req)))
                genericAddressDetailsVO.setState(getValue("TMPL_state", req));

            if (functions.isValueNull(getValue("postcode", req)))
                genericAddressDetailsVO.setZipCode(getValue("postcode", req));
            else if (functions.isValueNull(getValue("TMPL_zip", req)))
                genericAddressDetailsVO.setZipCode(getValue("TMPL_zip", req));

            if (functions.isValueNull(getValue("street", req)))
                genericAddressDetailsVO.setStreet(getValue("street", req));
            else if (functions.isValueNull(getValue("TMPL_street", req)))
                genericAddressDetailsVO.setStreet(getValue("TMPL_street", req));

            if (functions.isValueNull(getValue("phone", req)))
                genericAddressDetailsVO.setPhone(getValue("phone", req));
            else if (functions.isValueNull(getValue("TMPL_telno", req)))
                genericAddressDetailsVO.setPhone(getValue("TMPL_telno", req));

            if (functions.isValueNull(genericAddressDetailsVO.getPhone()))
                genericAddressDetailsVO.setPhone(genericAddressDetailsVO.getPhone().trim());

            if (functions.isValueNull(getValue("telnocc", req)))
                genericAddressDetailsVO.setTelnocc(getValue("telnocc", req));
            else if (functions.isValueNull(getValue("TMPL_telnocc", req)))
                genericAddressDetailsVO.setTelnocc(getValue("TMPL_telnocc", req));

            if (functions.isValueNull(getValue("email", req)))
                genericAddressDetailsVO.setEmail(getValue("email", req));
            else if (functions.isValueNull(getValue("TMPL_emailaddr", req)))
                genericAddressDetailsVO.setEmail(getValue("TMPL_emailaddr", req));

            genericAddressDetailsVO.setLanguage(getValue("lang", req));
            session.setAttribute("language", getValue("lang", req));

            if (functions.isValueNull(getValue("device", req)))
                standardKitValidatorVO.setDevice(getValue("device", req));
            else
                standardKitValidatorVO.setDevice("");


            if (functions.isValueNull(getValue("isProcessed", req)))
                standardKitValidatorVO.setIsProcessed(getValue("isProcessed", req));
            else
                standardKitValidatorVO.setIsProcessed("");

            if (functions.isValueNull(getValue("invoicenumber", req)))
            {
                standardKitValidatorVO.setInvoiceId(getValue("invoicenumber", req));
            }

            standardKitValidatorVO.setTerminalId(getValue("terminalid", req));
            standardKitValidatorVO.setAccountId(getValue("accountid", req));

            if (functions.isValueNull(getValue("paymentMode", req)))
            {
                if (functions.isNumericVal1(getValue("paymentMode", req)))
                {
                    standardKitValidatorVO.setPaymentType(getValue("paymentMode", req));
                }
                else
                {
                    standardKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(getValue("paymentMode", req)));
                }
                if (functions.isValueNull(standardKitValidatorVO.getPaymentType()))
                    standardKitValidatorVO.setPaymentMode(GatewayAccountService.getPaymentTypes(standardKitValidatorVO.getPaymentType()));
            }
            else if (functions.isValueNull(getValue("paymenttype", req)))
            {
                if (functions.isNumericVal1(getValue("paymenttype", req)))
                {
                    standardKitValidatorVO.setPaymentType(getValue("paymenttype", req));
                }
                else
                {
                    standardKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(getValue("paymenttype", req)));
                }
                //standardKitValidatorVO.setPaymentType(getValue("paymenttype", req));
                if (functions.isValueNull(standardKitValidatorVO.getPaymentType()))
                    standardKitValidatorVO.setPaymentMode(GatewayAccountService.getPaymentTypes(standardKitValidatorVO.getPaymentType()));
            }

            if (functions.isValueNull(getValue("paymentBrand", req)))
            {
                if (functions.isNumericVal1(getValue("paymentBrand", req)))
                {
                    standardKitValidatorVO.setCardType(getValue("paymentBrand", req));
                }
                else
                {
                    standardKitValidatorVO.setCardType(GatewayAccountService.getCardId(getValue("paymentBrand", req)));
                }
                if (functions.isValueNull(standardKitValidatorVO.getPaymentBrand()))
                    standardKitValidatorVO.setPaymentBrand(GatewayAccountService.getCardType(standardKitValidatorVO.getPaymentBrand()));
            }
            else if (functions.isValueNull(getValue("cardtype", req)))
            {
                if (functions.isNumericVal1(getValue("cardtype", req)))
                {
                    standardKitValidatorVO.setCardType(getValue("cardtype", req));
                }
                else
                {
                    standardKitValidatorVO.setCardType(GatewayAccountService.getCardId(getValue("cardtype", req)));
                }
                if (functions.isValueNull(standardKitValidatorVO.getCardType()))
                    standardKitValidatorVO.setPaymentBrand(GatewayAccountService.getCardType(standardKitValidatorVO.getCardType()));

            }
            log.debug("e_wallet mode---" + standardKitValidatorVO.getPaymentMode());
            log.debug("e_wallet brand---" + standardKitValidatorVO.getPaymentBrand());

            if (functions.isValueNull(getValue("trackingid", req)))
            {
                standardKitValidatorVO.setTrackingid(getValue("trackingid", req));
            }

            if (functions.isValueNull(getValue("customerId", req)))
            {
                standardKitValidatorVO.setCustomerId(getValue("customerId", req));
            }

            if (functions.isValueNull(getValue("customerBankId", req)))
            {
                standardKitValidatorVO.setCustomerBankId(getValue("customerBankId", req));
            }

            if (functions.isValueNull(getValue("voucherNumber", req)))
                genericCardDetailsVO.setVoucherNumber(getValue("voucherNumber", req));

            if (functions.isValueNull(getValue("voucherCode", req)))
                genericCardDetailsVO.setSecurity_Code(getValue("voucherCode", req));

            if (functions.isValueNull(getValue("cardNumber", req)))
                genericCardDetailsVO.setCardNum(functions.decryptString(getValue("cardNumber", req)));
            if (functions.isValueNull(getValue("expire", req))){
                String deExpire=functions.decryptString(getValue("expire", req));
                String expireDate[]=deExpire.split("-");
                genericCardDetailsVO.setExpMonth(expireDate[0]);
                genericCardDetailsVO.setExpYear(expireDate[1]);
            }
            if (functions.isValueNull(getValue("cvv", req)))
                genericCardDetailsVO.setcVV(functions.decryptString(getValue("cvv", req)));

            //todo

            standardKitValidatorVO.setRequestedIP(Functions.getIpAddress(req));


            if (functions.isValueNull(getValue("reservedField1", req)) && getValue("reservedField1", req).contains("|"))
            {
                recurringBillingVO = new RecurringBillingVO();
                recurringBillingVO.setReqField1(getValue("reservedField1", req));
                String values[] = getValue("reservedField1", req).split("\\|");
                if (values.length == 4)
                {
                    recurringBillingVO.setRecurring(values[0]);
                    recurringBillingVO.setInterval(values[1]);
                    recurringBillingVO.setFrequency(values[2]);
                    recurringBillingVO.setRunDate(values[3]);
                }
                standardKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            }
            reserveField2VO = new ReserveField2VO();

            reserveField2VO.setAccountNumber(getValue("accountnumber", req));
            reserveField2VO.setRoutingNumber(getValue("routingnumber", req));
            reserveField2VO.setAccountType(getValue("accountType", req));

            reserveField2VO.setReservefield1(getValue("reservedField1", req));//Ecospend : schedule+standing payment(Date)
            reserveField2VO.setReservefield2(getValue("reservedField2", req));//Ecospend : Standing Order (Period)
            reserveField2VO.setReservefield3(getValue("reservedField3", req));//Ecospend : Standing Order (No. of Payments)
            reserveField2VO.setReservefield4(getValue("reservedField4", req));//Ecospend : Standing Order (First payment Amount)
            reserveField2VO.setReservefield5(getValue("reservedField5", req));//Ecospend : Standing Order (Last payment Amount)

            List<MarketPlaceVO> mpDetailsList = new ArrayList<>();
            String[] mp_Memberid = req.getParameterValues("MP_Memberid[]");
            String[] mp_Amount = req.getParameterValues("MP_Amount[]");
            String[] mp_Orderid = req.getParameterValues("MP_Orderid[]");
            String[] mp_Order_Description = req.getParameterValues("MP_Order_Description[]");

            if (mp_Memberid != null && mp_Amount != null && mp_Orderid != null && mp_Order_Description != null)
            {
                String error = "";
                int mp_memberid_len = mp_Memberid.length;
                int mp_amount_len = mp_Amount.length;
                int mp_orderid_len = mp_Orderid.length;
                int mp_orderDesc_len = mp_Order_Description.length;
                if (mp_memberid_len == mp_amount_len && mp_memberid_len == mp_orderid_len && mp_memberid_len == mp_orderDesc_len)
                {
                    for (int i = 0; i < mp_memberid_len; i++)
                    {
                        marketPlaceVO = new MarketPlaceVO();
                        transactionLogger.error("Memberid----->" + mp_Memberid[i]);
                        transactionLogger.error("Amount----->" + mp_Amount[i]);
                        transactionLogger.error("Orderid----->" + mp_Orderid[i]);
                        transactionLogger.error("Order Description----->" + mp_Order_Description[i]);
                        marketPlaceVO.setMemberid(mp_Memberid[i]);
                        marketPlaceVO.setAmount(mp_Amount[i]);
                        marketPlaceVO.setOrderid(mp_Orderid[i]);
                        marketPlaceVO.setOrderDesc(mp_Order_Description[i]);
                        mpDetailsList.add(i, marketPlaceVO);
                    }
                }
            }

            standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            standardKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            standardKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
            standardKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            standardKitValidatorVO.setReserveField2VO(reserveField2VO);
            standardKitValidatorVO.setSplitPaymentVO(splitPaymentVO);
            standardKitValidatorVO.setMarketPlaceVOList(mpDetailsList);
        }
        catch (JSONException e)
        {

        }
        return standardKitValidatorVO;
    }
    //Card Registration--
    public static CommonValidatorVO getSTDProcessControllerRequestParametersForCardRegistration(HttpServletRequest req)
    {
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        ReserveField2VO reserveField2VO = null;
        Functions functions = new Functions();
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();

        if(functions.isValueNull(getValue("memberId", req)))
            merchantDetailsVO.setMemberId(getValue("memberId", req));
        else if(functions.isValueNull(getValue("toid", req)))
            merchantDetailsVO.setMemberId(getValue("toid", req));

        genericTransDetailsVO.setChecksum(getValue("checksum", req));

        if(functions.isValueNull(getValue("totype",req)))
            genericTransDetailsVO.setTotype(getValue("totype", req));

        if(functions.isValueNull(getValue("paymentBrand",req)))
            standardKitValidatorVO.setCardType(getValue("paymentBrand", req));

        if(functions.isValueNull(getValue("merchantRedirectUrl",req)))
            genericTransDetailsVO.setRedirectUrl(getValue("merchantRedirectUrl", req));
        else if(functions.isValueNull(getValue("redirecturl",req)))
            genericTransDetailsVO.setRedirectUrl(getValue("redirecturl", req));

        if(functions.isValueNull(getValue("notificationUrl",req)))
            genericTransDetailsVO.setNotificationUrl(getValue("notificationUrl", req));

        if(functions.isValueNull(getValue("redirectMethod",req)))
            genericTransDetailsVO.setRedirectMethod(getValue("redirectMethod", req));

        /*if(functions.isValueNull(getValue("mkey",req)))
            merchantDetailsVO.setKey(getValue("mkey",req));*/

        String remoteAddr = Functions.getIpAddress(req);
        String reqIp = "";
        if(remoteAddr.contains(","))
        {
            String sIp[] = remoteAddr.split(",");
            reqIp = sIp[0].trim();
        }
        else
        {
            reqIp = remoteAddr;
        }

        genericAddressDetailsVO.setCardHolderIpAddress(reqIp);

        if(functions.isValueNull(getValue("ip", req)))
            genericAddressDetailsVO.setIp(getValue("ip", req)); //getValue("ipaddr",req)
        else if(functions.isValueNull(getValue("ipaddr",req)))
            genericAddressDetailsVO.setIp(getValue("ipaddr", req));

        transactionLogger.error("reqIp ip----" + genericAddressDetailsVO.getIp());

        genericTransDetailsVO.setChecksum(getValue("checksum", req));

        transactionLogger.error("lastName:::"+genericAddressDetailsVO.getLastname());

        if (functions.isValueNull(getValue("country",req).toUpperCase()))
            genericAddressDetailsVO.setCountry(getValue("country", req).toUpperCase());
        else if(functions.isValueNull(getValue("TMPL_COUNTRY", req).toUpperCase()))
            genericAddressDetailsVO.setCountry(getValue("TMPL_COUNTRY", req).toUpperCase());

        transactionLogger.error("country:::"+genericAddressDetailsVO.getCountry());

        if (functions.isValueNull(getValue("city", req)))
            genericAddressDetailsVO.setCity(getValue("city", req));
        else if (functions.isValueNull(getValue("TMPL_city", req)))
            genericAddressDetailsVO.setCity(getValue("TMPL_city", req));

        transactionLogger.error("city:::"+genericAddressDetailsVO.getCity());

        if (functions.isValueNull(getValue("state", req)))
            genericAddressDetailsVO.setState(getValue("state", req));
        else if (functions.isValueNull(getValue("TMPL_state", req)))
            genericAddressDetailsVO.setState(getValue("TMPL_state", req));

        transactionLogger.error("state:::"+genericAddressDetailsVO.getState());

        if (functions.isValueNull(getValue("postcode", req)))
            genericAddressDetailsVO.setZipCode(getValue("postcode", req));
        else if (functions.isValueNull(getValue("TMPL_zip", req)))
            genericAddressDetailsVO.setZipCode(getValue("TMPL_zip", req));

        transactionLogger.error("state:::"+genericAddressDetailsVO.getState());

        if (functions.isValueNull(getValue("street", req)))
            genericAddressDetailsVO.setStreet(getValue("street", req));
        else if (functions.isValueNull(getValue("TMPL_street", req)))
            genericAddressDetailsVO.setStreet(getValue("TMPL_street", req));

        transactionLogger.error("street:::"+genericAddressDetailsVO.getStreet());

        if (functions.isValueNull(getValue("phone", req)))
            genericAddressDetailsVO.setPhone(getValue("phone", req));
        else if (functions.isValueNull(getValue("TMPL_telno", req)))
            genericAddressDetailsVO.setPhone(getValue("TMPL_telno", req));

        transactionLogger.error("phone:::"+genericAddressDetailsVO.getPhone());

        if (functions.isValueNull(getValue("telnocc", req)))
            genericAddressDetailsVO.setTelnocc(getValue("telnocc", req));
        else if (functions.isValueNull(getValue("TMPL_telnocc", req)))
            genericAddressDetailsVO.setTelnocc(getValue("TMPL_telnocc", req));

        transactionLogger.error("telnocc:::"+genericAddressDetailsVO.getTelnocc());

        if (functions.isValueNull(getValue("email", req)))
            genericAddressDetailsVO.setEmail(getValue("email", req));
        else if (functions.isValueNull(getValue("TMPL_emailaddr", req)))
            genericAddressDetailsVO.setEmail(getValue("TMPL_emailaddr", req));

        transactionLogger.error("email:::"+genericAddressDetailsVO.getEmail());
        transactionLogger.debug("---line 992--"+getValue("customerId",req));

        if (functions.isValueNull(getValue("customerId",req)))
        {
            standardKitValidatorVO.setCustomerId(getValue("customerId", req));
        }
        if(functions.isValueNull(getValue("cardnumber",req)))
            genericCardDetailsVO.setCardNum(getValue("cardnumber",req));

        transactionLogger.error("cardNo::::"+genericCardDetailsVO.getCardNum());

        if(functions.isValueNull(getValue("expiry",req)))
            genericCardDetailsVO.setExpMonth(getValue("expiry", req));

        transactionLogger.error("cardNo::::"+genericCardDetailsVO.getExpMonth());

        if(functions.isValueNull(getValue("cvv",req)))
            genericCardDetailsVO.setcVV(getValue("cvv", req));

        transactionLogger.error("cardNo::::"+genericCardDetailsVO.getcVV());

        transactionLogger.debug("---customer ID---" + standardKitValidatorVO.getCustomerId());

        standardKitValidatorVO.setRequestedIP(Functions.getIpAddress(req));
        reserveField2VO = new ReserveField2VO();

        reserveField2VO.setAccountNumber(getValue("accountnumber",req));
        reserveField2VO.setRoutingNumber(getValue("routingnumber",req));
        reserveField2VO.setAccountType(getValue("accountType",req));

        standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        standardKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        standardKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        standardKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        standardKitValidatorVO.setReserveField2VO(reserveField2VO);
        standardKitValidatorVO.setSplitPaymentVO(splitPaymentVO);
        return standardKitValidatorVO;
    }

    public static CommonValidatorVO getCardRegistrationDetails(HttpServletRequest req)
    {
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        Functions functions=new Functions();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

        if(functions.isValueNull(getValue("toid",req)))
            merchantDetailsVO.setMemberId(getValue("toid",req));
        if(functions.isValueNull(getValue("totype",req)))
            genericTransDetailsVO.setTotype(getValue("totype", req));
        if(functions.isValueNull(getValue("paymentBrand",req)))
            standardKitValidatorVO.setCardType(getValue("paymentBrand", req));
        if(functions.isValueNull(getValue("customerId",req)))
            standardKitValidatorVO.setCustomerId(getValue("customerId",req));

        /*if(functions.isValueNull(getValue("mkey",req)))
            merchantDetailsVO.setKey(getValue("mkey",req));
*/
        if(functions.isValueNull(getValue("merchantRedirectUrl",req)))
            genericTransDetailsVO.setRedirectUrl(getValue("merchantRedirectUrl", req));
        else if(functions.isValueNull(getValue("redirecturl",req)))
            genericTransDetailsVO.setRedirectUrl(getValue("redirecturl", req));

        if(functions.isValueNull(getValue("notificationUrl",req)))
            genericTransDetailsVO.setNotificationUrl(getValue("notificationUrl", req));

        if(functions.isValueNull(getValue("redirectMethod",req)))
            genericTransDetailsVO.setRedirectMethod(getValue("redirectMethod", req));

        genericTransDetailsVO.setChecksum(getValue("checksum", req));

        merchantDetailsVO.setLogoName(getValue("logoname",req));
        standardKitValidatorVO.setLogoName(getValue("logoname",req));

        if(functions.isValueNull(getValue("cardnumber",req)))
            genericCardDetailsVO.setCardNum(getValue("cardnumber",req).replaceAll(" ",""));

        if(getValue("expiry",req).contains("/"))
        {
            String[] exp = getValue("expiry",req).split("/");
            String expMonth = exp[0];
            genericCardDetailsVO.setExpMonth(expMonth);
            if(expMonth.length()==1)
                genericCardDetailsVO.setExpMonth("0"+expMonth);
            genericCardDetailsVO.setExpYear("20"+exp[1]);
        }

        if(functions.isValueNull(getValue("cvv",req)))
            genericCardDetailsVO.setcVV(getValue("cvv", req));
        String splitName = getValue("firstname",req).trim();
        if(functions.isValueNull(getValue("firstname",req)) && !functions.isValueNull(getValue("lastname",req)))
        {
            if(splitName.contains(" "))
            {
                int index = splitName.lastIndexOf(" ");
                String firstName = splitName.substring(0, index).replaceAll(" ", "");
                String lastName = splitName.substring(index + 1, splitName.length());

                genericAddressDetailsVO.setFirstname(firstName);
                genericAddressDetailsVO.setLastname(lastName);
            }
            else
            {
                genericAddressDetailsVO.setFirstname(getValue("firstname",req));
                genericAddressDetailsVO.setLastname(getValue("firstname", req));
            }
        }
        if(functions.isValueNull(getValue("street",req)))
            genericAddressDetailsVO.setStreet(getValue("street", req));
        if(functions.isValueNull(getValue("city",req)))
            genericAddressDetailsVO.setCity(getValue("city", req));
        if(functions.isValueNull(getValue("country_input",req)))
            genericAddressDetailsVO.setCountry(getValue("country_input", req));
        if(functions.isValueNull(getValue("zip",req)))
            genericAddressDetailsVO.setZipCode(getValue("zip", req));
        if(functions.isValueNull(getValue("state",req)))
            genericAddressDetailsVO.setState(getValue("state", req));
        if(functions.isValueNull(getValue("phone-CC",req)))
            genericAddressDetailsVO.setTelnocc(getValue("phone-CC", req));
        if(functions.isValueNull(getValue("telno",req)))
            genericAddressDetailsVO.setPhone(getValue("telno", req));
        if(functions.isValueNull(getValue("email",req)))
            genericAddressDetailsVO.setEmail(getValue("email", req));


        standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        standardKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        standardKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        standardKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        standardKitValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return standardKitValidatorVO;
    }

    //PayProcess3DController
    public static CommonValidatorVO getSTDKit3DRequestParametersForSale(HttpServletRequest req,String ctoken)
    {
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        RecurringBillingVO recurringBillingVO=new RecurringBillingVO();
        Functions functions = new Functions();

        merchantDetailsVO.setMemberId(getValue("toid", req));
        genericTransDetailsVO.setTotype(getValue("totype", req));
        genericTransDetailsVO.setAmount(getValue("amount", req));
        genericTransDetailsVO.setCurrency(getValue("currency", req));
        genericTransDetailsVO.setOrderDesc(getValue("orderdescription", req));
        genericTransDetailsVO.setOrderId(getValue("description", req));
        genericTransDetailsVO.setRedirectUrl(getValue("redirecturl", req));
        genericTransDetailsVO.setNotificationUrl(getValue("notificationUrl",req));
        genericAddressDetailsVO.setCardHolderIpAddress(Functions.getIpAddress(req));
        genericAddressDetailsVO.setIp(getValue("ipaddr",req)); //getValue("ipaddr",req)
        genericTransDetailsVO.setChecksum(getValue("checksum",req));
        genericAddressDetailsVO.setTmpl_currency(getValue("TMPL_CURRENCY",req));
        genericAddressDetailsVO.setTmpl_amount(getValue("TMPL_AMOUNT",req));
        genericAddressDetailsVO.setCountry(getValue("TMPL_COUNTRY",req).toUpperCase());
        //genericTransDetailsVO.setCurrency(getValue("TMPL_CURRENCY",req));

        genericCardDetailsVO.setCardNum(getValue("cardNo",req));
        genericCardDetailsVO.setExpMonth(getValue("expMonth", req));
        genericCardDetailsVO.setExpYear(getValue("expYear",req));
        genericCardDetailsVO.setcVV(getValue("cvv",req));

        genericAddressDetailsVO.setFirstname(getValue("fname",req));
        genericAddressDetailsVO.setLastname(getValue("lname", req));

        genericAddressDetailsVO.setCity(getValue("TMPL_city",req));
        genericAddressDetailsVO.setState(getValue("TMPL_state",req));
        genericAddressDetailsVO.setZipCode(getValue("TMPL_zip",req));
        genericAddressDetailsVO.setStreet(getValue("TMPL_street",req));
        genericAddressDetailsVO.setPhone(getValue("TMPL_telno",req));
        genericAddressDetailsVO.setTelnocc(getValue("TMPL_telnocc",req));
        genericAddressDetailsVO.setEmail(getValue("TMPL_emailaddr",req));
        standardKitValidatorVO.setPaymentType(getValue("paymenttype",req));
        standardKitValidatorVO.setCardType(getValue("cardtype",req));
        standardKitValidatorVO.setTerminalId(getValue("terminalid",req));

        genericAddressDetailsVO.setLanguage(getValue("language",req));
        genericAddressDetailsVO.setBirthdate(getValue("birthDate", req));
        if(functions.isValueNull(getValue("customerId",req)))
        {
            standardKitValidatorVO.setCustomerId(getValue("customerId", req));
        }

        standardKitValidatorVO.setCtoken(ctoken);

        recurringBillingVO.setReqField1(getValue("reservedField1",req));
        if(functions.isValueNull(getValue("reservedField1",req)) && getValue("reservedField1",req).contains("|"))
        {
            String values[] = getValue("reservedField1",req).split("\\|");
            if(values.length==4)
            {
                recurringBillingVO.setRecurring(values[0]);
                recurringBillingVO.setInterval(values[1]);
                recurringBillingVO.setFrequency(values[2]);
                recurringBillingVO.setRunDate(values[3]);
            }
        }
        standardKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
        standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        standardKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        standardKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        standardKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);

        return standardKitValidatorVO;
    }

    public static DirectRefundValidatorVO getRequestParametersForRefund(HttpServletRequest req)
    {
        DirectRefundValidatorVO directRefundValidatorVO = new DirectRefundValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        directRefundValidatorVO.setActionType(getValue("action",req));
        if(req.getParameter("version")!=null && !req.getParameter("version").equals(""))
        {
            directRefundValidatorVO.setVersion(getValue("version", req));
        }
        else
        {
            directRefundValidatorVO.setVersion(" ");
        }

        merchantDetailsVO.setMemberId(getValue("toid",req));
        directRefundValidatorVO.setTrackingid(getValue("trackingid",req));
        directRefundValidatorVO.setRefundAmount(getValue("refundamount", req));
        directRefundValidatorVO.setRefundReason(getValue("reason",req));
        transDetailsVO.setChecksum(getValue("checksum",req).trim());
        transDetailsVO.setChecksum(getValue("checksum",req).trim());

        directRefundValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        directRefundValidatorVO.setTransDetailsVO(transDetailsVO);
        return directRefundValidatorVO;
    }

    public static CommonValidatorVO getRequestParametersForStatus(HttpServletRequest req)
    {
        //Hashtable requestMap = new Hashtable();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        commonValidatorVO.setActionType("status");

        //String version = req.getParameter("version");
        if(req.getParameter("version")!=null && !req.getParameter("version").equals(""))
        {
            commonValidatorVO.setVersion(req.getParameter("version"));
        }
        else
        {
            commonValidatorVO.setVersion(" ");
        }

        merchantDetailsVO.setMemberId(getValue("toid",req));
        commonValidatorVO.setTrackingid(getValue("trackingid",req));
        transDetailsVO.setOrderId(getValue("description",req));
        transDetailsVO.setChecksum(getValue("checksum",req));

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
    }

    public static CommonValidatorVO getRequestParametersForCapture(HttpServletRequest req)
    {
        //Hashtable requestMap = new Hashtable();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        commonValidatorVO.setActionType("capture");
        if(req.getParameter("version")!=null && !req.getParameter("version").equals(""))
        {
            commonValidatorVO.setVersion(req.getParameter("version"));
        }
        else
        {
            commonValidatorVO.setVersion(" ");
        }

        merchantDetailsVO.setMemberId(getValue("toid",req));
        commonValidatorVO.setTrackingid(getValue("trackingid",req));
        transDetailsVO.setAmount(getValue("captureamount",req));
        transDetailsVO.setChecksum(getValue("checksum",req));

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
    }
    private static String getValue(String sTag, HttpServletRequest req)
    {
        String value  ="";
        if(req.getParameter(sTag) != null && !req.getParameter(sTag).equals("") && !req.getParameter(sTag).equals("null") && !req.getParameter(sTag).equals(" "))
        {
            value = req.getParameter(sTag);
        }

        return value;
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        String amt = d.format(dObj2);
        return amt;
    }
}