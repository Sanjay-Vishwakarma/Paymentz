package com.payment.decta.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Trupti on 5/20/2017.
 */
public class DectaMerchantPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaMerchantPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "dectam";

    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Decta");
    private final static String NOTIFICATION_URL = rb.getString("NOTIFY_URL");
    private final static String CALLBACK_URL = rb.getString("CALLBACK_URL");

    public final static String ELE_GUID = "guid";
    public final static String ELE_PASSWORD = "pwd";
    public final static String ELE_RS = "rs";
    public final static String ELE_UNIQUE_ID = "merchant_transaction_id";
    public final static String ELE_IP = "user_ip";
    public final static String ELE_DESCRIPTION = "description";
    public final static String ELE_AMOUNT = "amount";
    public final static String ELE_CURRENCY = "currency";
    public final static String ELE_CARD_NAME = "name_on_card";
    public final static String ELE_STREET = "street";
    public final static String ELE_CITY = "city";
    public final static String ELE_COUNTRY = "country";
    public final static String ELE_STATE = "state";
    public final static String ELE_ZIP = "zip";
    public final static String ELE_PHONE = "phone";
    public final static String ELE_EMAIL = "email";
    public final static String ELE_RETURN_URL = "custom_return_url";
    public final static String ELE_CALLBACK_URL = "custom_callback_url";
    public final static String ELE_CARD_BIN = "card_bin";

    public final static String ELE_EXTANDED_ID = "f_extended";
    public final static String ELE_CARD_NUM = "cc";
    public final static String ELE_CVV = "cvv";
    public final static String ELE_EXPIRE_CARD = "expire";
    public final static String ELE_INIT_TRANS_ID = "init_transaction_id";

    public final static String ELE_ACCOUNT_GUID = "account_guid";
    public final static String ELE_AMOUNT_REFUND = "amount_to_refund";

    private final static String TEST_INIT_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=init";
    private final static String TEST_COMPLETE_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=charge";
    private final static String TEST_CAPTURE_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=charge_hold";

    private final static String TEST_AUTH_INIT_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=init_dms";
    private final static String TEST_AUTH_COMP_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=make_hold";

    private final static String TEST_REFUND_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=refund";
    private final static String TEST_CANCEL_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=cancel_dms";


    private final static String LIVE_INIT_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=init";
    private final static String LIVE_COMPLETE_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=charge";
    private final static String LIVE_CAPTURE_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=charge_hold";

    private final static String LIVE_AUTH_INIT_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=init_dms";
    private final static String LIVE_AUTH_COMP_URL  = "https://www2.1stpayments.net/gwprocessor2.php?a=make_hold";

    private final static String LIVE_REFUND_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=refund";
    private final static String LIVE_CANCEL_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=cancel_dms";

    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DectaMerchantPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.debug("Inside DectaMerchantPaymentGateway prosessAuth---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String notificationUrl = "";
        //validateForSale(trackingID,requestVO);
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if (functions.isValueNull(commMerchantAccountVO.getHostUrl()))
        {
            notificationUrl = "https://"+commMerchantAccountVO.getHostUrl()+"/transaction/DCFrontendNotification?merchant_transaction_id="+trackingID;
            transactionLogger.error("from host url----" + notificationUrl);
        }
        else
        {
            notificationUrl = NOTIFICATION_URL+trackingID;
            transactionLogger.error("from resource bundle----"+notificationUrl);
        }
        try
        {
            Map<String, String> initMap = new TreeMap<String, String>();
            Map<String, String> completeMap = new TreeMap<String, String>();

            initMap.put(ELE_GUID, GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            initMap.put(ELE_PASSWORD, DectaUtils.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            initMap.put(ELE_RS,GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE());
            initMap.put(ELE_UNIQUE_ID,trackingID);
            initMap.put(ELE_DESCRIPTION,genericTransactionDetailsVO.getOrderId());
            BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            String name = genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
            initMap.put(ELE_AMOUNT,String.valueOf(amount.intValue()));
            initMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
            initMap.put(ELE_CARD_NAME,name);
            initMap.put(ELE_IP,genericAddressDetailsVO.getCardHolderIpAddress());
            initMap.put(ELE_STREET,genericAddressDetailsVO.getStreet());
            initMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
            initMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
            initMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
            initMap.put(ELE_STATE,genericAddressDetailsVO.getState());
            initMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
            initMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
            initMap.put(ELE_CARD_BIN,genericCardDetailsVO.getCardNum().substring(0, 6));
            initMap.put(ELE_RETURN_URL,notificationUrl);
            initMap.put(ELE_CALLBACK_URL,CALLBACK_URL);

            completeMap.put(ELE_EXTANDED_ID,"5");
            completeMap.put(ELE_CARD_NUM,genericCardDetailsVO.getCardNum());
            completeMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
            completeMap.put(ELE_EXPIRE_CARD,genericCardDetailsVO.getExpMonth()+"/"+genericCardDetailsVO.getExpYear().substring(2));//MM/YY

            //transactionLogger.debug("init map 1st step auth---"+initMap);

            String response = "";
            String responseComplete = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_AUTH_INIT_URL, initMap);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_AUTH_INIT_URL, initMap);
            }

            transactionLogger.error("response first step DectaMerchantPaymentGateway-------" + response);
            //step-2 sending card details.
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            if(response!=null || !response.equals("") )
            {
                String res[]=response.split(":");
                String initid=res[1];
                String text=res[0];
                if(text!=null || !text.equals("") && initid!=null || !initid.equals(""))
                {
                    if(text.equalsIgnoreCase("OK"))
                    {
                        completeMap.put(ELE_INIT_TRANS_ID, initid);
                        //transactionLogger.debug("comp map 2nd step---"+completeMap);
                        if(isTest)
                        {
                            responseComplete = DectaUtils.doPostHTTPSURLConnection(TEST_AUTH_COMP_URL,completeMap);
                        }
                        else
                        {
                            responseComplete = DectaUtils.doPostHTTPSURLConnection(LIVE_AUTH_COMP_URL,completeMap);
                        }

                        transactionLogger.error("response Complete after 2nd step-----" + responseComplete);
                        //System.out.println("response Complete after 2nd step-----" + responseComplete);

                        if(responseComplete != null && !responseComplete.equals(""))
                        {
                            String data[] = responseComplete.split("~");
                            String status = "";
                            System.out.println("responseComplete---"+responseComplete.contains("ERROR"));
                            if(responseComplete.contains("ERROR"))
                            {
                                status = "fail";
                                commResponseVO.setStatus(status);
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                            else if(data.length==1 && !responseComplete.contains("ERROR"))
                            {
                                transactionLogger.debug("data.length---"+data.length);
                                status = "pending3DConfirmation";
                                commResponseVO.setStatus(status);
                                commResponseVO.setUrlFor3DRedirect(responseComplete.substring(10));
                            }
                            else
                            {
                                transactionLogger.debug("inside else..."+data.length);
                                HashMap responseHash = new HashMap();

                                for(int i=0;i<data.length;i++)
                                {   String res2[] = data[i].split(":");
                                    if(res2.length==2)
                                    {
                                        responseHash.put(res2[0],res2[1]);
                                    }
                                    else
                                    {
                                        responseHash.put(res2[0],"null");
                                    }
                                }
                                transactionLogger.error("response in auth----" + responseHash.get("Status"));
                                if(responseHash.get("Status").equals("HoldOk"))
                                {
                                    status = "success";
                                }
                                else
                                {
                                    status = "fail";
                                }
                                transactionLogger.debug("response hash DectaBankMerchantPaymentGateway---"+responseHash);
                                commResponseVO.setTransactionId((String)responseHash.get("ID"));
                                commResponseVO.setStatus(status);
                                commResponseVO.setErrorCode((String) responseHash.get("ResultCode"));
                                commResponseVO.setResponseHashInfo((String) responseHash.get("ApprovalCode"));
                                commResponseVO.setDescriptor(descriptor);
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setTransactionId(initid);
                        commResponseVO.setStatus("fail");
                    }
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DectaMerchantPaymentGateway.class.getName(), "processSale()", null, "common", "NoSuchAlgorithm exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside DectaBankMerchantPaymentGateway prosessSale---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        Functions functions = new Functions();
        DectaUtils dectaUtils = new DectaUtils();
        String notificationUrl = "";

        CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
        //validateForSale(trackingID,requestVO);
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if (functions.isValueNull(commMerchantAccountVO.getHostUrl()))
        {
            notificationUrl = "https://"+commMerchantAccountVO.getHostUrl()+"/transaction/DCFrontendNotification?merchant_transaction_id="+trackingID;
            transactionLogger.error("from host url----" + notificationUrl);
        }
        else
        {
            notificationUrl = NOTIFICATION_URL+trackingID;
            transactionLogger.error("from resource bundle----"+notificationUrl);
        }
        try
        {
            Map<String, String> initMap = new TreeMap<String, String>();
            Map<String, String> completeMap = new TreeMap<String, String>();

            initMap.put(ELE_GUID, GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            initMap.put(ELE_PASSWORD, DectaUtils.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            initMap.put(ELE_RS,GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE());
            initMap.put(ELE_UNIQUE_ID,trackingID);
            initMap.put(ELE_DESCRIPTION,genericTransactionDetailsVO.getOrderId());
            BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            String name = genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
            initMap.put(ELE_AMOUNT,String.valueOf(amount.intValue()));
            initMap.put(ELE_CURRENCY,genericTransactionDetailsVO.getCurrency());
            initMap.put(ELE_CARD_NAME,name);
            initMap.put(ELE_IP,genericAddressDetailsVO.getCardHolderIpAddress());
            initMap.put(ELE_STREET,genericAddressDetailsVO.getStreet());
            initMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
            initMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
            initMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
            initMap.put(ELE_STATE,genericAddressDetailsVO.getState());
            initMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
            initMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
            initMap.put(ELE_CARD_BIN,genericCardDetailsVO.getCardNum().substring(0,6));
            initMap.put(ELE_RETURN_URL,notificationUrl);
            initMap.put(ELE_CALLBACK_URL,CALLBACK_URL);

            completeMap.put(ELE_EXTANDED_ID,"5");
            completeMap.put(ELE_CARD_NUM,genericCardDetailsVO.getCardNum());
            completeMap.put(ELE_CVV,genericCardDetailsVO.getcVV());
            completeMap.put(ELE_EXPIRE_CARD,genericCardDetailsVO.getExpMonth()+"/"+genericCardDetailsVO.getExpYear().substring(2));//MM/YY

            //transactionLogger.error("init map 1st step---" + initMap);
            String response = "";
            String responseComplete = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_INIT_URL, initMap);
                transactionLogger.error("Transaction_Init URL---"+TEST_INIT_URL);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_INIT_URL, initMap);
                transactionLogger.error("Transaction_Init URL---"+LIVE_INIT_URL);
            }

            transactionLogger.error("response first step DectaBankMerchantPaymentGateway-------" + response);
            //step-2 sending card details.
            String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            if(response!=null || !response.equals("") )
            {
                String res[]=response.split(":");
                String initid=res[1];
                String text=res[0];
                if(text!=null || !text.equals("") && initid!=null || !initid.equals(""))
                {
                    if(text.equalsIgnoreCase("OK"))
                    {
                        completeMap.put(ELE_INIT_TRANS_ID,initid);
                        //transactionLogger.error("comp map 2nd step---" + completeMap);
                        //todo gatewayCall
                        //Date date104=new Date();
                        if(isTest)
                        {
                            responseComplete = DectaUtils.doPostHTTPSURLConnection(TEST_COMPLETE_URL,completeMap);
                            transactionLogger.error("Transaction_Complete URL---"+TEST_COMPLETE_URL);
                        }
                        else
                        {
                            responseComplete = DectaUtils.doPostHTTPSURLConnection(LIVE_COMPLETE_URL,completeMap);
                            transactionLogger.error("Transaction_Complete URL---"+LIVE_COMPLETE_URL);
                        }

                        transactionLogger.error("response Complete after 2nd step-----" + responseComplete);

                        if(responseComplete != null && !responseComplete.equals(""))
                        {
                            String data[] = responseComplete.split("~");
                            String status = "";

                            if(responseComplete.contains("ERROR"))
                            {
                                status = "fail";
                                commResponseVO.setStatus(status);
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                            else if(data.length==1 && !responseComplete.contains("ERROR"))
                            {
                                transactionLogger.debug("data.length---"+data.length);
                                status = "pending3DConfirmation";
                                commResponseVO.setStatus(status);
                                commResponseVO.setUrlFor3DRedirect(responseComplete.substring(10));
                            }
                            else
                            {
                                transactionLogger.debug("inside else..."+data.length);
                                HashMap responseHash = new HashMap();

                                for(int i=0;i<data.length;i++)
                                {   String res2[] = data[i].split(":");
                                    if(res2.length==2)
                                    {
                                        responseHash.put(res2[0],res2[1]);
                                    }
                                    else
                                    {
                                        responseHash.put(res2[0],"null");
                                    }
                                }
                                if(responseHash.get("Status").equals("Success") || responseHash.get("ResultCode").equals("000"))
                                {
                                    status = "success";
                                }
                                else
                                {
                                    status = "fail";
                                }
                                transactionLogger.debug("response hash DectaBankMerchantPaymentGateway---"+responseHash);
                                String resDescription = dectaUtils.getResultDescriptionFromCode((String) responseHash.get("ResultCode"));
                                commResponseVO.setTransactionId((String)responseHash.get("ID"));
                                commResponseVO.setStatus(status);
                                commResponseVO.setErrorCode((String) responseHash.get("ResultCode"));
                                commResponseVO.setResponseHashInfo((String) responseHash.get("ApprovalCode"));
                                commResponseVO.setDescriptor(descriptor);
                                commResponseVO.setRemark(resDescription);
                                commResponseVO.setDescription(resDescription);
                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setTransactionId(initid);
                        commResponseVO.setStatus("fail");
                    }
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException in processSale DectaBankMerchantPG---",e);
            PZExceptionHandler.raiseTechnicalViolationException(DectaMerchantPaymentGateway.class.getName(),"processSale()",null,"common","NoSuchAlgorithm exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO)throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processCapture of DectaMerchantPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        //validateForSale(trackingID,requestVO);
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        try
        {
            Map<String, String> completeMap = new TreeMap<String, String>();

            completeMap.put(ELE_INIT_TRANS_ID,genericTransactionDetailsVO.getPreviousTransactionId());
            completeMap.put(ELE_GUID,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            completeMap.put(ELE_PASSWORD, DectaUtils.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));

            transactionLogger.error("-----Cancel request decta-----" + completeMap);
            String response = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_CAPTURE_URL,completeMap);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_CAPTURE_URL,completeMap);
            }

            transactionLogger.error("-----Cancel response decta-----" + response);
            if(response!=null && !response.equals(""))
            {
                String status = "fail";
                String sData[] = response.split(":");
                if("Success".equalsIgnoreCase(sData[1]))
                {
                    status = "success";
                }

                transactionLogger.debug("in DectaGateway-----"+status+"response---"+sData[1]);
                commResponseVO.setStatus(status);
                commResponseVO.setDescription(response);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setTransactionType("Capture");

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DectaMerchantPaymentGateway.class.getName(),"processSale()",null,"common","NoSuchAlgorithm exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        try
        {
            Map<String, String> refundMap = new TreeMap<String, String>();
            refundMap.put(ELE_ACCOUNT_GUID,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            refundMap.put(ELE_PASSWORD, DectaUtils.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            refundMap.put(ELE_INIT_TRANS_ID,genericTransactionDetailsVO.getPreviousTransactionId());
            BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            refundMap.put(ELE_AMOUNT_REFUND,String.valueOf(amount.intValue()));
            transactionLogger.error("refund request map---" + refundMap);
            String response = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_REFUND_URL,refundMap);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_REFUND_URL,refundMap);
            }

            transactionLogger.error("refund response ---" + response);
            if(response!=null && !response.equals(""))
            {
                String status = "fail";
                if(response.equalsIgnoreCase("Refund Success"))
                {
                    status = "success";
                    commResponseVO.setStatus(status);
                }
                commResponseVO.setDescription(response);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setTransactionType("Refund");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DectaMerchantPaymentGateway.class.getName(),"processRefund()",null,"common","NoSuchAlgorithm exception while Refunding transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        try
        {
            Map<String, String> refundMap = new TreeMap<String, String>();
            refundMap.put(ELE_ACCOUNT_GUID,GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            refundMap.put(ELE_PASSWORD, DectaUtils.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            refundMap.put(ELE_INIT_TRANS_ID,genericTransactionDetailsVO.getPreviousTransactionId());
            BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            refundMap.put(ELE_AMOUNT_REFUND,String.valueOf(amount.intValue()));
            transactionLogger.error("void request map---" + refundMap);
            String response = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_CANCEL_URL,refundMap);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_CANCEL_URL,refundMap);
            }

            transactionLogger.error("void response ---" + response);

            if(response!=null && !response.equals(""))
            {
                String status = "fail";
                if(response.equalsIgnoreCase("DMS canceled OK"))
                {
                    status = "success";
                    commResponseVO.setStatus(status);
                }
                commResponseVO.setDescription(response);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setTransactionType("Cancel");
                commResponseVO.setDescriptor(descriptor);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DectaMerchantPaymentGateway.class.getName(),"processVoid()",null,"common","NoSuchAlgorithm exception while Refunding transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return commResponseVO;
    }



}
