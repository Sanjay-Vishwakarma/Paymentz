package com.payment.cashflow.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.RecurringManager;
import com.manager.vo.RecurringBillingVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 10/2/14
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CashFlowPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(CashFlowPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CashFlowPaymentGateway.class.getName());
    Functions functions = new Functions();
    public static final String GATEWAY_TYPE = "cashflow";
    private final static String TESTURL = "https://secure.cashflows.com/gateway/remote_auth";
    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public  CashFlowPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("P4DirectDebitPayment","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

        /*PZGenericConstraint genConstraint = new PZGenericConstraint("CashFlowPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);*/

    }
    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processSale of CashFlowPaymentGateway...");
        transactionLogger.debug("Entering processSale of CashFlowPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();
        RecurringManager recurringManager = new RecurringManager();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(2);

        CashFlowUtills cashFlowUtills = new CashFlowUtills();
        CashFlowAccounts cashFlowAccounts = new CashFlowAccounts();
        Hashtable getMid = cashFlowAccounts.getMidPassword(accountId);
        String isTest = "0";
        String tran_type = "";
        if(getMid.get("istest").equals("Y"))
        {
            isTest = "1";
        }
        if(!"Y".equalsIgnoreCase(account.getIsRecurring()))
        {
            tran_type = "sale";
        }
        else
        {
            tran_type = "verify";
        }

        try
        {
            String authid = URLEncoder.encode((String) getMid.get("mid"), "UTF-8");
            String auth_pass = URLEncoder.encode((String) getMid.get("password"), "UTF-8");

            String card_num = URLEncoder.encode(genericCardDetailsVO.getCardNum(), "UTF-8");
            String card_cvv = URLEncoder.encode(genericCardDetailsVO.getcVV(), "UTF-8");
            String card_expiry = URLEncoder.encode(expMonth + expYear, "UTF-8");
            String card_expiryLog = URLEncoder.encode(functions.maskingNumber(expMonth) + functions.maskingPan(expYear), "UTF-8");

            String cust_name = URLEncoder.encode(genericAddressDetailsVO.getFirstname(), "UTF-8");

            String cust_address = URLEncoder.encode(genericAddressDetailsVO.getStreet(), "UTF-8");
            String cust_postcode = URLEncoder.encode(genericAddressDetailsVO.getZipCode(), "UTF-8");
            String cust_country = URLEncoder.encode(genericAddressDetailsVO.getCountry(), "UTF-8");
            String cust_ip = URLEncoder.encode(genericAddressDetailsVO.getIp(), "UTF-8");
            String cust_email = URLEncoder.encode(genericAddressDetailsVO.getEmail(), "UTF-8");

            String tran_ref = URLEncoder.encode(trackingID, "UTF-8");
            String tran_amount = URLEncoder.encode(genericTransDetailsVO.getAmount(), "UTF-8");
            String tran_currency = URLEncoder.encode(genericTransDetailsVO.getCurrency(), "UTF-8");
            String tran_testmode = URLEncoder.encode(isTest, "UTF-8");
            tran_type = URLEncoder.encode(tran_type, "UTF-8");
            String tran_class = URLEncoder.encode("ecom", "UTF-8");

            String postData = "auth_id=" + authid + "&auth_pass=" + auth_pass + "&card_num=" + card_num + "&card_cvv=" + card_cvv + "&card_expiry=" + card_expiry + "&cust_name=" + cust_name +
                    "&cust_address=" + cust_address + "&cust_postcode=" + cust_postcode + "&cust_country=" + cust_country + "&cust_ip=" + cust_ip + "&cust_email=" + cust_email +
                    "&tran_ref=" + tran_ref + "&tran_amount=" + tran_amount + "&tran_currency=" + tran_currency + "&tran_testmode=" + tran_testmode + "&tran_type=" + tran_type +
                    "&tran_class=" + tran_class;

            String postDataLog = "auth_id=" + authid + "&auth_pass=" + auth_pass + "&card_num=" + functions.maskingPan(card_num) + "&card_cvv=" + functions.maskingNumber(card_cvv) + "&card_expiry=" + card_expiryLog + "&cust_name=" + cust_name +
                    "&cust_address=" + cust_address + "&cust_postcode=" + cust_postcode + "&cust_country=" + cust_country + "&cust_ip=" + cust_ip + "&cust_email=" + cust_email +
                    "&tran_ref=" + tran_ref + "&tran_amount=" + tran_amount + "&tran_currency=" + tran_currency + "&tran_testmode=" + tran_testmode + "&tran_type=" + tran_type +
                    "&tran_class=" + tran_class;

            log.debug("request cash flow---" + postDataLog);
            transactionLogger.debug("request cash flow---" + postDataLog);

            String saleResponse = CashFlowUtills.doPostHTTPSURLConnection(TESTURL, postData);
            log.debug("response---" + saleResponse);
            transactionLogger.debug("response---" + saleResponse);
            Hashtable responseHash = cashFlowUtills.spiltResponse(saleResponse);

            log.debug("response---" + responseHash);
            transactionLogger.debug("response---" + responseHash);

            String cardNumber = genericCardDetailsVO.getCardNum();

            if(!responseHash.equals("") && responseHash!=null)
            {
                String first_six = cardNumber.substring(0, 6);
                String last_four = cardNumber.substring((cardNumber.length() - 4), cardNumber.length());
                String rbid = (String)responseHash.get("transactionId");
                log.debug("rbid----"+rbid);

                String status = "fail";
                if(responseHash.get("authorised").equals("A"))
                {
                    status = "success";
                }

                if (tran_type.equalsIgnoreCase("sale"))
                {
                    commResponseVO.setStatus(status);
                    commResponseVO.setDescription((String) responseHash.get("authRequest"));
                    commResponseVO.setTransactionId((String) responseHash.get("transactionId"));
                    commResponseVO.setErrorCode((String) responseHash.get("authCode"));
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setTransactionType("sale");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
                else if (tran_type.equalsIgnoreCase("verify"))
                {
                    commResponseVO.setStatus(status);
                    commResponseVO.setDescription((String) responseHash.get("authRequest"));
                    commResponseVO.setTransactionId((String) responseHash.get("transactionId"));
                    commResponseVO.setErrorCode((String) responseHash.get("authCode"));
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setTransactionType("verify");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    if (status.equalsIgnoreCase("success"))
                    {
                        try
                        {
                            String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
                            String isManualRecurring = recurringBillingVO.getIsManualRecurring();

                            if ("Y".equalsIgnoreCase(isManualRecurring) && "N".equalsIgnoreCase(isAutomaticRecurring))
                            {
                                recurringManager.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
                            }
                            else if ("N".equalsIgnoreCase(isManualRecurring) && "Y".equalsIgnoreCase(isAutomaticRecurring))
                            {
                                recurringManager.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
                            }
                        }
                        catch (PZDBViolationException db)
                        {
                            log.error("db violation exception", db);
                            transactionLogger.error("PZDBViolationException in SingleCallManualRebill---", db);
                        }
                    }
                    else
                    {
                        try
                        {
                            recurringManager.deleteEntryForPFSRebill(trackingID);
                        }
                        catch (PZDBViolationException e)
                        {
                            log.error("db violation exception", e);
                            transactionLogger.error("PZDBViolationException in SingleCallManualRebill---", e);
                        }
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowPaymentGateway.class.getName(),"processSale()",null,"Common","Unsupported Encoding Exception while making transaction via CashFlow", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processSale of CashFlowPaymentGateway...");
        transactionLogger.debug("Entering processSale of CashFlowPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        CashFlowUtills cashFlowUtills = new CashFlowUtills();
        CashFlowAccounts cashFlowAccounts = new CashFlowAccounts();
        Hashtable getMid = cashFlowAccounts.getMidPassword(accountId);

        try
        {
            String authid = URLEncoder.encode((String)getMid.get("mid"), "UTF-8");
            String auth_pass = URLEncoder.encode((String)getMid.get("password"),"UTF-8");

            String tran_amount = URLEncoder.encode(genericTransDetailsVO.getAmount(),"UTF-8");
            String tran_currency = URLEncoder.encode(genericTransDetailsVO.getCurrency(),"UTF-8");
            String tran_testmode = URLEncoder.encode("1","UTF-8");
            String tran_type = URLEncoder.encode("refund","UTF-8");
            String tran_class = URLEncoder.encode("ecom","UTF-8");
            String tran_id = URLEncoder.encode(genericTransDetailsVO.getPreviousTransactionId());

            String refundData = "auth_id="+authid+"&auth_pass="+auth_pass+"&tran_amount="+tran_amount+"&tran_currency="+tran_currency+"&tran_testmode="+tran_testmode+
                    "&tran_type="+tran_type+"&tran_class="+tran_class+"&tran_orig_id="+tran_id;

            String refundResponse = CashFlowUtills.doPostHTTPSURLConnection(TESTURL, refundData);
            log.debug("refund response---" + refundResponse);
            transactionLogger.debug("refund response---"+refundResponse);
            Hashtable refundHash = cashFlowUtills.spiltResponse(refundResponse);

            if(!refundHash.equals("") && refundHash!=null)
            {
                String status = "fail";
                if(refundHash.get("authorised").equals("A"))
                {
                    status = "success";
                }

                commResponseVO.setStatus(status);
                commResponseVO.setDescription((String) refundHash.get("authRequest"));
                commResponseVO.setTransactionId((String) refundHash.get("transactionId"));
                commResponseVO.setErrorCode((String) refundHash.get("authCode"));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowPaymentGateway.class.getName(),"processRefund()",null,"Common","Unsupported Encoding Exception while making transaction via CashFlow", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        log.debug("Inside ProcessRebilling Cashflow------");
        transactionLogger.debug("Inside ProcessRebilling Cashflow------");

        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        log.debug("card number----"+commRequestVO.getCardDetailsVO().getCardNum());
        log.debug("card number in genericCardDetailsVO----"+genericCardDetailsVO.getCardNum());
        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();
        CashFlowUtills cashFlowUtills = new CashFlowUtills();

        CashFlowAccounts cashFlowAccounts = new CashFlowAccounts();
        Hashtable getMid = cashFlowAccounts.getMidPassword(accountId);

        String cardNum = PzEncryptor.decryptPAN(genericCardDetailsVO.getCardNum());

        String EXP= PzEncryptor.decryptExpiryDate(genericCardDetailsVO.getExpMonth());
        String dateArr[]=EXP.split("/");
        String expMonth = dateArr[0];
        String expYear = dateArr[1].substring(2, 4);

        try
        {
            String authid = URLEncoder.encode((String)getMid.get("mid"), "UTF-8");
            String auth_pass = URLEncoder.encode((String)getMid.get("password"),"UTF-8");

            String card_num = URLEncoder.encode(cardNum,"UTF-8");
            String card_expiry = URLEncoder.encode(expMonth+""+expYear,"UTF-8");
            String card_expiryLog = URLEncoder.encode(functions.maskingNumber(expMonth) + functions.maskingPan(expYear), "UTF-8");

            String cust_name = URLEncoder.encode(genericAddressDetailsVO.getFirstname(),"UTF-8");
            String cust_address = URLEncoder.encode(genericAddressDetailsVO.getStreet()+" "+genericAddressDetailsVO.getState(),"UTF-8");
            String cust_postcode = URLEncoder.encode(genericAddressDetailsVO.getZipCode(),"UTF-8");
            String cust_country = URLEncoder.encode(genericAddressDetailsVO.getCountry(),"UTF-8");
            String cust_ip = URLEncoder.encode(genericAddressDetailsVO.getIp(),"UTF-8");
            String cust_email = URLEncoder.encode(genericAddressDetailsVO.getEmail(),"UTF-8");

            String tran_amount = URLEncoder.encode(genericTransDetailsVO.getAmount(),"UTF-8");
            String tran_currency = URLEncoder.encode(genericTransDetailsVO.getCurrency(),"UTF-8");
            String tran_ref = URLEncoder.encode(genericTransDetailsVO.getOrderId(),"UTF-8");
            String tran_testmode = URLEncoder.encode("1","UTF-8");
            String tran_type = URLEncoder.encode("sale","UTF-8");
            String tran_class = URLEncoder.encode("cont","UTF-8");

            String rebillData = "auth_id="+authid+"&auth_pass="+auth_pass+
                                "&card_num="+card_num+"&card_expiry="+card_expiry+"&cust_name="+cust_name+"&cust_address="+cust_address+"&cust_postcode="+cust_postcode+"&cust_country="+cust_country+"&cust_ip="+cust_ip+"&cust_email="+cust_email+
                                "&tran_amount="+tran_amount+"&tran_currency="+tran_currency+"&tran_ref="+tran_ref+"&tran_testmode="+tran_testmode+"&tran_type="+tran_type+"&tran_class="+tran_class;

            String rebillDataLog = "auth_id="+authid+"&auth_pass="+auth_pass+
                    "&card_num="+functions.maskingPan(card_num)+"&card_expiry="+card_expiryLog+"&cust_name="+cust_name+"&cust_address="+cust_address+"&cust_postcode="+cust_postcode+"&cust_country="+cust_country+"&cust_ip="+cust_ip+"&cust_email="+cust_email+
                    "&tran_amount="+tran_amount+"&tran_currency="+tran_currency+"&tran_ref="+tran_ref+"&tran_testmode="+tran_testmode+"&tran_type="+tran_type+"&tran_class="+tran_class;

            log.debug("RebillRequest---->"+rebillDataLog);
            String rebillResponse = CashFlowUtills.doPostHTTPSURLConnection(TESTURL,rebillData);

            log.debug("RebillResponse in Rebilling----" + rebillResponse);
            transactionLogger.debug("RebillResponse in Rebilling----"+rebillResponse);

            Hashtable rebillHash = cashFlowUtills.spiltResponse(rebillResponse);

            log.debug("response---" + rebillHash);
            transactionLogger.debug("response---" + rebillHash);


            if(rebillHash!=null && !rebillHash.equals(""))
            {
                String status = "fail";
                if(rebillHash.get("authorised").equals("A"))
                {
                    status = "success";
                }

                commResponseVO.setStatus(status);
                commResponseVO.setDescription((String) rebillHash.get("authorised"));
                commResponseVO.setTransactionId((String) rebillHash.get("transactionId"));
                commResponseVO.setErrorCode((String) rebillHash.get("authCode"));
                commResponseVO.setRemark((String) rebillHash.get("authRequest"));
                commResponseVO.setDescriptor(GATEWAY_TYPE);
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CashFlowPaymentGateway.class.getName(),"processRefund()",null,"Common","Unsupported Encoding Exception while making transaction via CashFlow", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {


        String request = "auth_id=5942159&auth_pass=GgXKgNcQa1&card_num=4000000000000002&card_expiry=0120&cust_name=Swamy&cust_postcode=400025&cust_country=IN&cust_ip=45.64.195.218&cust_email=<emailaddress>&tran_ref=01P02F95524&tran_amount=100.00&tran_currency=GBP&tran_testmode=1&tran_type=sale&tran_class=cont";
//        auth_id=5942159&auth_pass=GgXKgNcQa1&card_num=4000000000000002&card_expiry=011&cust_name=Swamy&cust_address=Malad+MH&cust_postcode=400023&cust_country=IN&cust_ip=122.127.69.70&cust_email=<emailaddress>&tran_amount=50.00&tran_currency=GBP&tran_ref=6thApr16-02&tran_testmode=1&tran_type=sale&tran_class=cont
        String rebillResponse = CashFlowUtills.doPostHTTPSURLConnection(TESTURL,request);

        //System.out.println("Cashflow Response-------------"+rebillResponse);
    }

}
