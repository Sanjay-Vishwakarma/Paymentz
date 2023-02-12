package com.payment.ReitumuBank.core;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.payworld.PayWorldUtils;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/10/15
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReitumuBankSMSPaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(ReitumuBankSMSPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ReitumuBankSMSPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "rbg";

    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Reitumu3D");
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

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ReitumuBankSMSPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("ReitumuBankSMSPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
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
            initMap.put(ELE_PASSWORD,ReitumuUtills.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
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
            log.debug("-ip in reitumu pg--" + genericAddressDetailsVO.getCardHolderIpAddress());
            transactionLogger.debug("-ip in reitumu pg--" + genericAddressDetailsVO.getCardHolderIpAddress());
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
                response = ReitumuUtills.doPostHTTPSURLConnection(TEST_INIT_URL, initMap);
            }
            else
            {
                response = ReitumuUtills.doPostHTTPSURLConnection(LIVE_INIT_URL, initMap);
            }

            log.error("response-------"+response);
            transactionLogger.error("response-------"+response);

            if(response!=null && !response.equals(""))
            {
                String status = "fail";
                String res[] = response.split("~");

                String oStatus[] = res[0].split(":");

                if(oStatus[0].equalsIgnoreCase("OK"))
                {
                    status = "pending3DConfirmation";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setUrlFor3DRedirect(res[1].substring(15));
            }
        }
        catch (NoSuchAlgorithmException ne)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ReitumuBankSMSPaymentGateway.class.getName(),"processSale()",null,"common","NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ne.getMessage(),ne.getCause());
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
            refundMap.put(ELE_PASSWORD,ReitumuUtills.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            refundMap.put(ELE_INIT_TRANS_ID,genericTransactionDetailsVO.getPreviousTransactionId());
            BigDecimal amount = new BigDecimal(genericTransactionDetailsVO.getAmount());
            amount = amount.multiply(new BigDecimal(100)) ;
            refundMap.put(ELE_AMOUNT_REFUND,String.valueOf(amount.intValue()));
            log.debug("refund map---" + refundMap);
            transactionLogger.debug("refund map---" + refundMap);
            String response = "";
            if(isTest)
            {
                response = ReitumuUtills.doPostHTTPSURLConnection(TEST_REFUND_URL,refundMap);
            }
            else
            {
                response = ReitumuUtills.doPostHTTPSURLConnection(LIVE_REFUND_URL,refundMap);
            }

            log.error("refund response reitumu---" + response);
            transactionLogger.error("refund response reitumu---" + response);

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
            PZExceptionHandler.raiseTechnicalViolationException(ReitumuBankSMSPaymentGateway.class.getName(), "processRefund()", null, "common", "NoSuchAlgorithm Exception while Refunding transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, ne.getMessage(), ne.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.error("Innside processInquiry of Reitumu---");
        transactionLogger.error("Innside processInquiry of Reitumu---");

        HashMap hashMap = new HashMap();

        hashMap.put("000","Approved");
        hashMap.put("001","Approved, honour with identification");
        hashMap.put("002","Approved for partial amount");
        hashMap.put("003","Approved for VIP");
        hashMap.put("004","Approved, update track 3");
        hashMap.put("005","Approved, account type specified by card issuer");
        hashMap.put("006","Approved for partial amount, account type specified by card issuer");
        hashMap.put("007","Approved, update ICC");
        hashMap.put("100","Decline (general, no comments)");
        hashMap.put("101","Decline, expired card");
        hashMap.put("102","Decline, suspected fraud");
        hashMap.put("103","Decline, card acceptor contact acquirer");
        hashMap.put("104","Decline, restricted card");
        hashMap.put("105","Decline, card acceptor call acquirer's security department");
        hashMap.put("106","Decline, allowable PIN tries exceeded");
        hashMap.put("107","Decline, refer to card issuer");
        hashMap.put("108","Decline, refer to card issuer's special conditions");
        hashMap.put("109","Decline, invalid merchant");
        hashMap.put("110","Decline, invalid amount");
        hashMap.put("111","Decline, invalid card number");
        hashMap.put("112","Decline, PIN data required");
        hashMap.put("113","Decline, unacceptable fee");
        hashMap.put("114","Decline, no account of type requested");
        hashMap.put("115","Decline, requested function not supported");
        hashMap.put("116","Decline, not sufficient funds");
        hashMap.put("117","Decline, incorrect PIN");
        hashMap.put("118","Decline, no card record");
        hashMap.put("119","Decline, transaction not permitted to cardholder");
        hashMap.put("120","Decline, transaction not permitted to terminal");
        hashMap.put("121","Decline, exceeds withdrawal amount limit");
        hashMap.put("122","Decline, security violation");
        hashMap.put("123","Decline, exceeds withdrawal frequency limit");
        hashMap.put("124","Decline, violation of law");
        hashMap.put("125","Decline, card not effective");
        hashMap.put("126","Decline, invalid PIN block");
        hashMap.put("127","Decline, PIN length error");
        hashMap.put("128","Decline, PIN kay synch error");
        hashMap.put("129","Decline, suspected counterfeit card");
        hashMap.put("180","Decline, by cardholders wish");
        hashMap.put("200","Pick-up (general, no comments)");
        hashMap.put("201","Pick-up, expired card");
        hashMap.put("202","Pick-up, suspected fraud");
        hashMap.put("203","Pick-up, card acceptor contact card acquirer");
        hashMap.put("204","Pick-up, restricted card");
        hashMap.put("205","Pick-up, card acceptor call acquirer's security department");
        hashMap.put("206","Pick-up, allowable PIN tries exceeded");
        hashMap.put("207","Pick-up, special conditions");
        hashMap.put("208","Pick-up, lost card");
        hashMap.put("209","Pick-up, stolen card");
        hashMap.put("210","Pick-up, suspected counterfeit card");
        hashMap.put("300","Status message: file action successful");
        hashMap.put("301","Status message: file action not supported by receiver");
        hashMap.put("302","Status message: unable to locate record on file");
        hashMap.put("303","Status message: duplicate record, old record replaced");
        hashMap.put("304","Status message: file record field edit error");
        hashMap.put("305","Status message: file locked out");
        hashMap.put("306","Status message: file action not successful");
        hashMap.put("307","Status message: file data format error");
        hashMap.put("308","Status message: duplicate record, new record rejected");
        hashMap.put("309","Status message: unknown file");
        hashMap.put("400","Accepted (for reversal)");
        hashMap.put("499","Approved, no original message data");
        hashMap.put("500","Status message: reconciled, in balance");
        hashMap.put("501","Status message: reconciled, out of balance");
        hashMap.put("502","Status message: amount not reconciled, totals provided");
        hashMap.put("503","Status message: totals for reconciliation not available");
        hashMap.put("504","Status message: not reconciled, totals provided");
        hashMap.put("600","Accepted (for administrative info)");
        hashMap.put("601","Status message: impossible to trace back original transaction");
        hashMap.put("602","Status message: invalid transaction reference number");
        hashMap.put("603","Status message: reference number/PAN incompatible");
        hashMap.put("604","Status message: POS photograph is not available");
        hashMap.put("605","Status message: requested item supplied");
        hashMap.put("606","Status message: request cannot be fulfilled - required documentation is not availabe");
        hashMap.put("680","List ready");
        hashMap.put("681","List not ready");
        hashMap.put("700","Accepted (for fee collection)");
        hashMap.put("800","Accepted (for network management)");
        hashMap.put("900","Advice acknowledged, no financial liability accepted");
        hashMap.put("901","Advice acknowledged, financial liability accepted");
        hashMap.put("902","Decline reason message: invalid transaction");
        hashMap.put("903","Status message: re-enter transaction");
        hashMap.put("904","Decline reason message: format error");
        hashMap.put("905","Decline reason message: acqiurer not supported by switch");
        hashMap.put("906","Decline reason message: cutover in process");
        hashMap.put("907","Decline reason message: card issuer or switch inoperative");
        hashMap.put("908","Decline reason message: transaction destination cannot be found for routing");
        hashMap.put("909","Decline reason message: system malfunction");
        hashMap.put("910","Decline reason message: card issuer signed off");
        hashMap.put("911","Decline reason message: card issuer timed out");
        hashMap.put("912","Decline reason message: card issuer unavailable");
        hashMap.put("913","Decline reason message: duplicate transmission");
        hashMap.put("914","Decline reason message: not able to trace back to original transaction");
        hashMap.put("915","Decline reason message: reconciliation cutover or checkpoint error");
        hashMap.put("916","Decline reason message: MAC incorrect");
        hashMap.put("917","Decline reason message: MAC key sync error");
        hashMap.put("918","Decline reason message: no communication keys available for use");
        hashMap.put("919","Decline reason message: encryption key sync error");
        hashMap.put("920","Decline reason message: security software/hardware error - try again");
        hashMap.put("921","Decline reason message: security software/hardware error - no action");
        hashMap.put("922","Decline reason message: message number out of sequence");
        hashMap.put("923","Status message: request in progress");
        hashMap.put("950","Decline reason message: violation of business arrangement");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new Comm3DResponseVO();

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
            statusMap.put(ELE_PASSWORD,ReitumuUtills.convertSha1(GatewayAccountService.getGatewayAccount(accountId).getPassword()));
            statusMap.put(ELE_REQUEST_TYPE,"transaction_status");
            statusMap.put(ELE_MERCHANT_TRANSACTION_ID,((CommRequestVO) requestVO).getTransDetailsVO().getPreviousTransactionId());
            statusMap.put(ELE_EXTANDED_ID,"5");
            log.error("status request---" + statusMap);
            transactionLogger.error("status request---" + statusMap);
            log.debug("request map---"+statusMap);
            String response = "";
            if(isTest)
            {
                response = ReitumuUtills.doPostHTTPSURLConnection(TEST_STATUS_URL,statusMap);
                log.error("Transaction_Status URL---"+TEST_STATUS_URL);
                transactionLogger.error("Transaction_Status URL---"+TEST_STATUS_URL);
            }
            else
            {
                response = ReitumuUtills.doPostHTTPSURLConnection(LIVE_STATUS_URL,statusMap);
                log.error("Transaction_Status URL---"+LIVE_STATUS_URL);
                transactionLogger.error("Transaction_Status URL---"+LIVE_STATUS_URL);
            }


            log.error("response for reitumu status---"+response);
            transactionLogger.error("response for reitumu status---" + response);

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
                commResponseVO.setRemark((String)hashMap.get(responseHash.get("ResultCode")));
                commResponseVO.setTransactionId((String) responseHash.get("ID"));
                commResponseVO.setStatus((String) responseHash.get("Status"));
                commResponseVO.setErrorCode((String) responseHash.get("ResultCode"));
                transactionLogger.debug("remark---"+(String)hashMap.get(responseHash.get("ResultCode")));

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
            PZExceptionHandler.raiseTechnicalViolationException(ReitumuBankSMSPaymentGateway.class.getName(), "processInquiry()", null, "common", "NoSuchAlgorithm Exception while Inquiring transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, ne.getMessage(), ne.getCause());
        }
        /*catch (Exception ne)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ReitumuBankSMSPaymentGateway.class.getName(), "processInquiry()", null, "common", "NoSuchAlgorithm Exception while Inquiring transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, ne.getMessage(), ne.getCause());
        }*/
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public static void main(String[] args)
    {
        ReitumuBankSMSPaymentGateway rsPg = new ReitumuBankSMSPaymentGateway("2526");

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();
        log.debug("inside main---");
        try
        {

            Map<String, String> statusMap = new TreeMap<String, String>();
            statusMap.put(ELE_GUID, "HHIH-1594-HCWD-02A0");
            statusMap.put(ELE_PASSWORD,"876dba62a6dfca7688d63b7883ebdeab8f61aa25");

            statusMap.put(ELE_REQUEST_TYPE,"transaction_status");
            statusMap.put(ELE_MERCHANT_TRANSACTION_ID,"15946");
            statusMap.put(ELE_EXTANDED_ID,"5");

            //System.out.println("request----"+statusMap);
            String response = ReitumuUtills.doPostHTTPSURLConnection(TEST_STATUS_URL,statusMap);
            //System.out.println("res----"+response);

            String res[]=response.split("~");
            HashMap responseHash = new HashMap();

            if (res.length==1)
            {
                String error[] = response.split(":");
                responseHash.put("error",error[4]);
                log.debug("---"+(String)(responseHash.get("error")));
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
                transactionLogger.debug("---"+responseHash.get("ResultCode"));

            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }

    }
}