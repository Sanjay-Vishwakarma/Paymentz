package com.payment.decta.core;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Trupti on 5/19/2017.
 */
public class DectaSMSPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(DectaSMSPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaSMSPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "dectag";

    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Decta");
    private final static String NOTIFICATION_URL = rb.getString("NOTIFY_URL");

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
    public final static String ELE_RETURN_URL = "merchant_site_url";

    public final static String ELE_EXTANDED_ID = "f_extended";
    public final static String ELE_REQUEST_TYPE = "request_type";
    public final static String ELE_MERCHANT_TRANSACTION_ID = "merchant_transaction_id";

    public final static String ELE_INIT_TRANS_ID = "init_transaction_id";
    public final static String ELE_ACCOUNT_GUID = "account_guid";
    public final static String ELE_AMOUNT_REFUND = "amount_to_refund";

    private final static String TEST_INIT_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=init";
    private final static String TEST_STATUS_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=status_request";
    private final static String TEST_REFUND_URL = "https://gw2sandbox.tpro.lv:8443/gw2test/gwprocessor2.php?a=refund";

    private final static String LIVE_INIT_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=init";
    private final static String LIVE_STATUS_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=status_request";
    private final static String LIVE_REFUND_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=refund";

    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public DectaSMSPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("DectaSMSPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommTransactionDetailsVO genericTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        //validateForSale(trackingID,requestVO);
        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        try
        {
            Map<String, String> initMap = new TreeMap<String, String>();

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
            log.debug("-ip in decta pg--" + genericAddressDetailsVO.getCardHolderIpAddress());
            transactionLogger.debug("-ip in decta pg--" + genericAddressDetailsVO.getCardHolderIpAddress());
            initMap.put(ELE_STREET, genericAddressDetailsVO.getStreet());
            initMap.put(ELE_ZIP,genericAddressDetailsVO.getZipCode());
            initMap.put(ELE_CITY,genericAddressDetailsVO.getCity());
            initMap.put(ELE_COUNTRY,genericAddressDetailsVO.getCountry());
            initMap.put(ELE_STATE,genericAddressDetailsVO.getState());
            initMap.put(ELE_EMAIL,genericAddressDetailsVO.getEmail());
            initMap.put(ELE_PHONE,genericAddressDetailsVO.getPhone());
            initMap.put(ELE_RETURN_URL,NOTIFICATION_URL);
            log.debug("init map---" + initMap);
            transactionLogger.debug("init map---"+initMap);
            String response = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_INIT_URL, initMap);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_INIT_URL, initMap);
            }

            log.error("response-------"+response);
            transactionLogger.error("response-------"+response);

            if(response!=null && !response.equals(""))
            {
                String status = "fail";
                if(response.contains("~"))
                {
                    String res[] = response.split("~");

                    String oStatus[] = res[0].split(":");

                    if (oStatus[0].equalsIgnoreCase("OK"))
                    {
                        status = "pending3DConfirmation";

                        commResponseVO.setStatus(status);
                        commResponseVO.setUrlFor3DRedirect(res[1].substring(15));
                    }
                    else
                    {
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescription("Invalid Request");
                    }
                }
                else
                {
                    commResponseVO.setStatus(status);
                    commResponseVO.setDescription("Invalid Request");
                }

            }
        }
        catch (NoSuchAlgorithmException ne)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DectaSMSPaymentGateway.class.getName(), "processSale()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.error("Innside processInquiry of Decta---");
        transactionLogger.error("Innside processInquiry of Decta---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new Comm3DResponseVO();
        DectaUtils dectaUtils = new DectaUtils();

        String descriptor= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        log.error("isTest in Transaction_status---"+isTest);
        transactionLogger.error("isTest in Transaction_status---"+isTest);

        try
        {
            Map<String, String> statusMap = new TreeMap<String, String>();
            log.error("MerchantID in Inquiry---"+GatewayAccountService.getGatewayAccount(accountId).getMerchantId()+"--Password--"+GatewayAccountService.getGatewayAccount(accountId).getPassword());
            transactionLogger.error("MerchantID in Inquiry---"+GatewayAccountService.getGatewayAccount(accountId).getMerchantId()+"--Password--"+GatewayAccountService.getGatewayAccount(accountId).getPassword());
            statusMap.put(ELE_GUID, GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            statusMap.put(ELE_PASSWORD, DectaUtils.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            statusMap.put(ELE_REQUEST_TYPE,"transaction_status");
            statusMap.put(ELE_MERCHANT_TRANSACTION_ID,((CommRequestVO) requestVO).getTransDetailsVO().getPreviousTransactionId());
            statusMap.put(ELE_EXTANDED_ID,"100");
            log.error("status request---" + statusMap);
            transactionLogger.error("status request---" + statusMap);
            log.debug("request map---"+statusMap);
            String response = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_STATUS_URL,statusMap);
                log.error("Transaction_Status URL---"+TEST_STATUS_URL);
                transactionLogger.error("Transaction_Status URL---"+TEST_STATUS_URL);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_STATUS_URL,statusMap);
                log.error("Transaction_Status URL---"+LIVE_STATUS_URL);
                transactionLogger.error("Transaction_Status URL---"+LIVE_STATUS_URL);
            }


            log.error("response for Decta status---"+response);
            transactionLogger.error("response for Decta status---" + response);

            HashMap responseHash = new HashMap();
            String res[]=response.split("~");

            if (res.length==1)
            {
                String error[] = response.split(":");
                responseHash.put("error",error[4]);
                commResponseVO.setRemark((String) (responseHash.get("error")));
            }
            else
            {
                for(int i=0;i<res.length;i++)
                {   String res2[] = res[i].split(":");
                    if(res2.length==2)
                    {
                        responseHash.put(res2[0],res2[1]);
                    }
                    else
                    {
                        responseHash.put(res2[0],"null");
                    }
                }
                String responseRemark = dectaUtils.getResultDescriptionFromCode((String) responseHash.get("ResultCode"));
                commResponseVO.setTransactionId((String) responseHash.get("ID"));
                commResponseVO.setStatus((String) responseHash.get("Status"));
                commResponseVO.setErrorCode((String) responseHash.get("ResultCode"));
                commResponseVO.setRemark(responseRemark);
                commResponseVO.setDescription(responseRemark);
                transactionLogger.debug("remark---"+responseRemark);

                commResponseVO.setResponseHashInfo((String) responseHash.get("ApprovalCode"));
                //commResponseVO.setRemark("ResultCode:"+(String) responseHash.get("ResultCode")+"ApprovalCode:"+(String) responseHash.get("ApprovalCode"));
                commResponseVO.setDescriptor(descriptor);
                log.error("Trans ID---" + (String) responseHash.get("ID"));
                transactionLogger.error("Trans ID---" + (String) responseHash.get("ID"));
                log.error("status---" + (String) responseHash.get("Status"));
                transactionLogger.error("status---" + (String) responseHash.get("Status"));

            }



            //commResponseVO.setErrorCode((String) hashMap.get("ResultCode"));
        }
        catch (NoSuchAlgorithmException ne)
        {
            log.error("NoSuchAlgorithmException while processInquiry---",ne);
            PZExceptionHandler.raiseTechnicalViolationException(DectaSMSPaymentGateway.class.getName(), "processInquiry()", null, "common", "NoSuchAlgorithm Exception while Inquiring transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, ne.getMessage(), ne.getCause());
        }
        /*catch (Exception ne)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ReitumuBankSMSPaymentGateway.class.getName(), "processInquiry()", null, "common", "NoSuchAlgorithm Exception while Inquiring transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, ne.getMessage(), ne.getCause());
        }*/
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
            log.debug("refund map---" + refundMap);
            transactionLogger.debug("refund map---" + refundMap);
            String response = "";
            if(isTest)
            {
                response = DectaUtils.doPostHTTPSURLConnection(TEST_REFUND_URL,refundMap);
            }
            else
            {
                response = DectaUtils.doPostHTTPSURLConnection(LIVE_REFUND_URL,refundMap);
            }

            log.error("refund response decta---" + response);
            transactionLogger.error("refund response decta---" + response);

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
        catch (NoSuchAlgorithmException ne)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DectaSMSPaymentGateway.class.getName(), "processRefund()", null, "common", "NoSuchAlgorithm Exception while Refunding transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, ne.getMessage(), ne.getCause());
        }

        return commResponseVO;
    }




}
