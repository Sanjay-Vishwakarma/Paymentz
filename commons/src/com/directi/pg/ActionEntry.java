package com.directi.pg;

import com.directi.pg.core.paymentgateway.*;
import com.directi.pg.core.valueObjects.*;
import com.payment.Enum.PZProcessType;
import com.payment.PayMitco.core.PayMitcoPaymentGateway;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sbm.core.SBMPaymentGateway;
import com.payment.skrill.SkrillResponseVO;
import com.payment.sofort.VO.SofortResponseVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jun 8, 2012
 * Time: 1:41:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionEntry
{

    //Actions

    //Begun
    public static final String ACTION_BEGUN_PROCESSING = "Begun Processing";
    //Failed
    public static final String ACTION_FAILED = "Failed";
    public static final String ACTION_FAILED_ATTEMPT = "Failed Attempt";

    //Authorisation
    public static final String ACTION_AUTHORISTION_STARTED = "Authorisation Started";
    public static final String ACTION_AUTHORISTION_SUCCESSFUL = "Authorisation Successful";
    public static final String ACTION_AUTHORISTION_CANCLLED = "Authorisation Cancelled";
    public static final String ACTION_AUTHORISTION_FAILED = "Authorisation Failed";

    //Sms
    public static final String ACTION_SMS_STARTED = "SMS Started"; // Added For UnionPayInternational

    //enrollment
    public static final String ACTION_ENROLLMENT_STARTED = "Enrollment Started"; // Added For UnionPayInternational

    //Capture
    public static final String ACTION_CAPTURE_STARTED = "Capture Started";
    public static final String ACTION_CAPTURE_SUCCESSFUL = "Capture Successful";
    public static final String ACTION_CAPTURE_FAILED = "Capture Failed";

    //Refund
    public static final String ACTION_REVERSAL_REQUEST_SENT = "Reversal Request Sent";
    public static final String ACTION_REVERSAL_REQUEST_ACCEPTED = "Reversal Request Accepted";  //Added for Sofort
    public static final String ACTION_REVERSAL_CONSOLIDATED = "Reversal Consolidated";       //Added for Sofort
    public static final String ACTION_REVERSAL_SUCCESSFUL = "Reversal Successful";
    public static final String ACTION_REVERSAL_REQUEST_DECLINED = "Reversal Request Declined"; // Added for SecureTrading
    public static final String ACTION_REVERSAL_REQUEST_SENT_FRAUD = "Reversal Req Sent (Fraud)";
    public static final String ACTION_REVERSAL_SUCCESSFUL_FRAUD = "Reversal Success (Fraud)";
    public static final String ACTION_REVERSAL_CANCELATION = "Reversal Canceled (Chargeback)";

    //Payout
    public static final String ACTION_PAYOUT_STARTED = "Payout Started";
    public static final String ACTION_PAYOUT_CANCEL_STARTED = "Payout Cancel Started";
    public static final String ACTION_PAYOUT_SUCCESSFUL = "Payout Successful";
    public static final String ACTION_PAYOUT_CANCEL_SUCCESSFUL = "Payout Cancel Successful";
    public static final String ACTION_PAYOUT_FAILED = "Payout Failed";
    public static final String ACTION_PAYOUT_CANCEL_FAILED = "Payout Cancel Failed";
    public static final String ACTION_PAYOUT_UPLOADED_SUCCESSFUL = "Payout Uploaded Successful";


    //Cancel
    public static final String ACTION_CANCEL_STARTED = "Cancel Started";
    public static final String ACTION_CANCEL_SUCCESSFUL = "Cancel Successful";

    public static final String ACTION_CANCLLED_BY_CUSTOMER = "Cancelled by Customer";
    public static final String ACTION_CANCLLED_TRANSACTION = "Cancelled Transactions";

    //3D
    public static final String ACTION_3D_AUTHORISATION_STARTED="3D Authorisation Started";
    public static final String ACTION_3D_CONFIRMATION_STARTED="3D Confirmation Started";
    public static final String ACTION_3D2_CONFIRMATION_STARTED="3D2 Confirmation Started";

    //Chargeback
    public static final String ACTION_CHARGEBACK_RACEIVED = "Chargeback Received";
    public static final String ACTION_CHARGEBACK_REVERSED = "Chargeback Reversed ";
    public static final String ACTION_PARTIAL_CHARGEBACK_REVERSED = "Partial Chrgbk Reversed";
    public static final String ACTION_PARTIAL_REFUND = "Partially Reversed";

    //Validation
    public static final String ACTION_VALIDATION_FAILED = "Validation Failed";
    public static final String ACTION_FRAUD_VALIDATION_FAILED = "Fraud Validation Failed";

    public static final String ACTION_PROOF_REQUIRED = "Proof Required";
    public static final String ACTION_POD_SENT = "POD sent";
    public static final String ACTION_CREDIT = "Credit";
    public static final String ACTION_REDIRECTION_STARTED = "Redirection Started";

    //3D2
    public static final String ACTION_3D2_INITIATED = "3D2 Initiated";
    public static final String ACTION_3D2_AUTH_STARTED = "3D2 Authentication Started";
    public static final String ACTION_3D2_AUTH_SUCCESSFUL = "3D2 Authentication Successful";
    public static final String ACTION_3D2_CHALLENGE = "3D2 Authentication Challenge";
    public static final String ACTION_3D2_AUTH_FAILED = "3D2 Authentication Failed";

    //Notification
    public static final String ACTION_NOTIFICATION_SENT =  "Notification sent";


    //Status
    public static final String STATUS_BEGUN = "begun";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_FAILED_ATTEMPT = "failed_attempt";
    public static final String STATUS_AUTHORISTION_STARTED = "authstarted";
    public static final String STATUS_SMS_STARTED = "smsstarted"; // Added For UnionPayInternational
    public static final String STATUS_ENROLLMENT_STARTED = "enrollmentstarted"; // Added For UnionPayInternational
    public static final String STATUS_AUTHORISTION_SUCCESSFUL = "authsuccessful";
    public static final String STATUS_AUTHORISTION_FAILED = "authfailed";
    public static final String STATUS_CAPTURE_STARTED = "capturestarted";
    public static final String STATUS_CAPTURE_SUCCESSFUL = "capturesuccess";
    public static final String STATUS_CAPTURE_FAILED = "capturefailed";
    public static final String STATUS_REVERSAL_REQUEST_SENT = "markedforreversal";
    public static final String STATUS_REVERSAL_REQUEST_ACCEPTED = "acceptedforreversal";    //Added for Sofort
    public static final String STATUS_REVERSAL_CONSOLIDATED = "consolidateforreversal";     //Added for Sofort
    public static final String STATUS_REVERSAL_SUCCESSFUL = "reversed";
    public static final String STATUS_REVERSAL_REQUEST_DECLINED = "declineforreversal";     //Added for SecureTrading
    public static final String STATUS_REVERSAL_CANCELED = "refundreversed";
    public static final String STATUS_PAYOUT_STARTED = "payoutstarted";
    public static final String STATUS_PAYOUT_CANCEL_STARTED = "payoutcancelstarted";
    public static final String STATUS_PAYOUT_SUCCESSFUL = "payoutsuccessful";
    public static final String STATUS_PAYOUT_CANCEL_SUCCESSFUL = "payoutcancelsuccessful";
    public static final String STATUS_PAYOUT_FAILED = "payoutfailed";
    public static final String STATUS_PAYOUT_CANCEL_FAILED = "payoutcancelfailed";
    public static final String STATUS_CANCEL_STARTED = "cancelstarted";
    public static final String STATUS_AUTHORISTION_CANCLLED = "authcancelled";
    public static final String STATUS_CANCLLED_BY_CUSTOMER = "failed";
    public static final String STATUS_CANCLLED_TRANSACTION = "cancelled";
    public static final String STATUS_3D_AUTHORISATION_STARTED="3D_authstarted";
    public static final String STATUS_3D_CONFIRMATION="3D_confirmation";
    public static final String STATUS_3D2_CONFIRMATION="3D2_confirmation";
    public static final String STATUS_CHARGEBACK_RACEIVED = "chargeback";
    public static final String STATUS_CHARGEBACK_REVERSED = "settled";
    public static final String STATUS_PARTIAL_CHARGEBACK_REVERSED = "reversed";
    public static final String STATUS_REVERSAL_REQUEST_SENT_FRAUD = "markedforreversal";
    public static final String STATUS_REVERSAL_SUCCESSFUL_FRAUD = "reversed";
    public static final String STATUS_POD_SENT = "podsent";
    public static final String STATUS_CREDIT = "settled";
    public static final String STATUS_PARTIAL_REFUND = "partialrefund";
    public static final String STATUS_PROOF_REQUIRED = "proofrequired";
    public static final String STATUS_INIT_AUTH = "initAuth";



    public static Hashtable statusHash = new Hashtable();
    static
    {
        statusHash.put("begun", "Begun Processing");
        statusHash.put("failed_attempt", "Failed Attempt");
        statusHash.put("failed", "Failed");
        statusHash.put("authstarted", "Authorisation Started");
        statusHash.put("authsuccessful", "Authorisation Successful");
        statusHash.put("authfailed", "Authorisation Failed");
        statusHash.put("authcancelled", "Authorisation Cancelled");
        statusHash.put("capturestarted", "Capture Started");
        statusHash.put("capturesuccess", "Capture Successful");
        statusHash.put("capturefailed", "Capture Failed");
        statusHash.put("markedforreversal", "Reversal Request Sent");
        statusHash.put("reversed", "Reversed");
        statusHash.put("partialrefund", "Partially Refunded");
        statusHash.put("payoutstarted", "Payout Started");
        statusHash.put("payoutsuccessful", "Payout Successful");
        statusHash.put("payoutfailed", "Payout Failed");
        statusHash.put("cancelled", "Cancelled Transactions");
        statusHash.put("cancelstarted","Cancel Initiated");
        statusHash.put("chargeback", "Chargeback");
        statusHash.put("3D_authstarted","3D Authorisation Started");
        statusHash.put("3D_confirmation","3D Confirmation Started");
        statusHash.put("proofrequired", "Proof Required");
        statusHash.put("podsent", "POD Sent");
        statusHash.put("settled", "Settled");
        statusHash.put("payoutcancelstarted", "Payout Cancel Started");
        statusHash.put("payoutcancelsuccessful", "Payout Cancel Successful");
        statusHash.put("payoutcancelfailed", "Payout Cancel Failed");
        statusHash.put("initAuth", "3D2 Initiated");
    }
    static Logger logger = new Logger(ActionEntry.class.getName());
    static TransactionLogger transactionLogger= new TransactionLogger(ActionEntry.class.getName());

    public ActionEntry()
    {
        /*try
        {
            cn = Database.getConnection();
        }
        catch (Exception e)
        {
            logger.error("Connection cannot be obtained in TransactionEntry.java ", e);
        }*/
    }

    public int actionEntry(String icicitransid, String amount, String action, String status) throws SystemError
    {
        logger.debug("Entering ActionEntry");

        Connection cn = null;
        try
        {
            String sql = "insert into action_history(icicitransid,amount,action,status) values (?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, icicitransid);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            int result = pstmt.executeUpdate();

            return result;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }

    public int genericActionEntry(String trackingid, String amount, String action, String status,String merchantIpAddress, String gatewayType,AuditTrailVO auditTrailVO) throws SystemError
    {
        logger.debug("Entering genericActionEntry 1");
        int result = 0;
        genericActionEntry(trackingid, amount, action, status,merchantIpAddress, gatewayType,null,auditTrailVO);
        return result;

    }

    public int genericActionEntry(String trackingid, String amount, String action, String status,String merchantIpAddress, String gatewayType, GenericResponseVO responseVO,AuditTrailVO auditTrailVO) throws SystemError
    {
        logger.debug("Entering genericActionEntry 2");
        int result = 0;
        genericActionEntry(trackingid, amount, action, status,merchantIpAddress, gatewayType, responseVO,null, auditTrailVO);
        return result;

    }

    public int genericActionEntry(String trackingid, String amount, String action, String status,String merchantIpAddress, String gatewayType, GenericResponseVO responseVO, GenericRequestVO requestVO,AuditTrailVO auditTrailVO) throws SystemError
    {
        logger.debug("Entering genericActionEntry 3");
        int result = 0;
        try
        {
            /*if (SBMPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                actionEntry(trackingid, amount, action, status);
            }*/
            if (QwipiPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                actionEntryForQwipi(trackingid, amount, action, status,merchantIpAddress, (QwipiResponseVO) responseVO, auditTrailVO);
            }
            else if (MyMonederoPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                actionEntryForMyMonedero(trackingid, amount, action, status, (MyMonederoResponseVO) responseVO,auditTrailVO);
            }
            else if (EcorePaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                actionEntryForEcore(trackingid, amount, action, status,merchantIpAddress, (EcoreResponseVO) responseVO,auditTrailVO);
            }
            else if (UGSPaymentGateway.GATEWAY_TYPE_UGS.equals(gatewayType))
            {
                actionEntryForUGSPay(trackingid, amount, action, status, (UGSPayResponseVO) responseVO,auditTrailVO);
            }
            else if (UGSPaymentGateway.GATEWAY_TYPE_FORT.equals(gatewayType))
            {
                actionEntryForUGSPay(trackingid, amount, action, status, (UGSPayResponseVO) responseVO,auditTrailVO);
            }
            else if (CUPPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                actionEntryForCUP(trackingid, amount, action, status, (CupResponseVO) responseVO,null,auditTrailVO,null);
            }
            else if (PayMitcoPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                actionEntryForPaymitco(trackingid, amount, action, status,(CommResponseVO) responseVO, null,auditTrailVO);
            }

            else
            {
                actionEntryForCommon(trackingid, amount, action, status, (CommResponseVO) responseVO, auditTrailVO,null );
            }
        }
        catch (PZDBViolationException tve)
        {
            logger.error("PZDBViolationException while update ActionEntry---",tve);
            throw new SystemError("Error while insert Action Entry");
        }
        return result;
    }

    public int actionEntryForPayDollar(String parentId, String amount, String action, String status) throws SystemError
    {
        logger.debug("Entering ActionEntry");

        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status) values (?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            int result = pstmt.executeUpdate();

            return result;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }

    public int actionEntryForPayVT(String parentId, String amount, String action, String status, String transactionid, String code, String result, String message) throws SystemError
    {
        logger.debug("Entering ActionEntry");

        Connection cn = null;
        try
        {
            String sql = "insert into transaction_payvt_details(parentid,amount,action,status,transaction_id,code,result,message) values (?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, transactionid);
            pstmt.setString(6, code);
            pstmt.setString(7, result);
            pstmt.setString(8, message);
            int results = pstmt.executeUpdate();

            return results;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }

    public int actionEntryForGenericTransaction(String tableName,String parentId, String amount, String action, String status,String ipAddress, CommResponseVO commResponseVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int i = 0;
        if(tableName!=null)
        {

            if(tableName.equals("transaction_common"))
            {
                Date date1 = new Date();
                transactionLogger.debug("ActionEntry.actionEntryForCommon start #######"+date1.getTime());
                i=actionEntryForCommon(parentId,amount,action,status,commResponseVO,auditTrailVO,ipAddress);
                transactionLogger.debug("ActionEntry.actionEntryForCommon end #######"+new Date().getTime());
                transactionLogger.debug("ActionEntry.actionEntryForCommon diff #######"+(new Date().getTime()-date1.getTime()));
            }
            else if(tableName.equals("transaction_qwipi"))
            {
                Date date1 = new Date();
                transactionLogger.debug("ActionEntry.actionEntryForQwipi start #######"+date1.getTime());
                i = actionEntryForQwipi(parentId,amount,action,status,ipAddress,null, auditTrailVO);
                transactionLogger.debug("ActionEntry.actionEntryForQwipi end #######"+new Date().getTime());
                transactionLogger.debug("ActionEntry.actionEntryForQwipi diff #######"+(new Date().getTime()-date1.getTime()));
            }
            else if (tableName.equals("transaction_ecore"))
            {
                i = actionEntryForEcore(parentId,amount,action,status,ipAddress,null,auditTrailVO);
            }

        }
        return i;
    }

    public int actionEntryForGenericTransaction(String tableName,String parentId, String amount, String action, String status,String ipAddress, CommResponseVO commResponseVO,CommRequestVO requestVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int i = 0;
        if (tableName != null)
        {

            if (tableName.equals("transaction_common"))
            {
                Date date1 = new Date();
                transactionLogger.debug("ActionEntry.actionEntryForCommon start #######" + date1.getTime());
                i = actionEntryForCommon(parentId, amount, action, status, commResponseVO,requestVO, auditTrailVO, ipAddress);
                transactionLogger.debug("ActionEntry.actionEntryForCommon end #######" + new Date().getTime());
                transactionLogger.debug("ActionEntry.actionEntryForCommon diff #######" + (new Date().getTime() - date1.getTime()));
            }
        }
        return i;
    }

    public int actionEntryForGenericTransactionForAccount(String tableName,String parentId, String amount, String action, String status,String ipAddress, CommResponseVO commResponseVO,CommRequestVO requestVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int i = 0;
        if (tableName != null)
        {

            if (tableName.equals("bankaccount_details"))
            {
                Date date1 = new Date();
                transactionLogger.debug("ActionEntry.actionEntryForCommon start #######" + date1.getTime());
                i = actionEntryForCommon(parentId, amount, action, status, commResponseVO,requestVO, auditTrailVO, ipAddress);
                transactionLogger.debug("ActionEntry.actionEntryForCommon end #######" + new Date().getTime());
                transactionLogger.debug("ActionEntry.actionEntryForCommon diff #######" + (new Date().getTime() - date1.getTime()));
            }
        }
        return i;
    }

    public int actionEntryForQwipi(String parentId, String amount, String action, String status,String ipAddress, QwipiResponseVO responseVO,AuditTrailVO auditTrailVO)throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry for Qwipi");
        String operation = "";
        String resultCode = "";

        String dateTime = "";
        String paymentOrderNo = "";
        String remark = "";
        String md5Info = "";
        String billingDescriptor = "";
        String actionExId ="0";
        String actionExname="";
        Connection cn = null;
        Functions functions = new Functions();
        if (responseVO != null)
        {

            operation = responseVO.getOperation();
            resultCode = responseVO.getResultCode();
            if (responseVO.getDateTime() != null)
            {

                dateTime = responseVO.getDateTime().trim();
                if (dateTime.length() > 14)
                {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
                    dateTime = sdf.format(date);
                }
            }


            if (responseVO.getPaymentOrderNo() != null)
            {
                paymentOrderNo = responseVO.getPaymentOrderNo();
            }
            if (responseVO.getRemark() != null)
            {
                remark = responseVO.getRemark();
            }
            else if (responseVO.getRefundRemark() != null)
            {
                remark = responseVO.getRefundRemark();
            }
            else if (responseVO.getCbText() != null)
            {
                remark = responseVO.getCbText();
            }
            else if (responseVO.getStText() != null)
            {
                remark = responseVO.getStText();
            }
            else
            {
                remark = "N/A";
            }
            if (functions.isValueNull(responseVO.getResultCode()) && ((responseVO.getResultCode().trim()).equals("0")))
            {
                billingDescriptor = responseVO.getBillingDescriptor();
            }

            md5Info = responseVO.getMd5Info();

        }
        if(auditTrailVO != null)
        {
            logger.debug("auditTrailVO is not null "+ipAddress);
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        int results=0;
        try
        {
            String sql = "insert into transaction_qwipi_details(parentid,amount,action,status,operationCode,responseResultcode,responseDateTime,qwipiPaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription,ipaddress,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, operation);
            pstmt.setString(6, resultCode);
            pstmt.setString(7, dateTime);
            pstmt.setString(8, paymentOrderNo);
            pstmt.setString(9, remark);
            pstmt.setString(10, md5Info);
            pstmt.setString(11, billingDescriptor);
            pstmt.setString(12,ipAddress);
            pstmt.setString(13,actionExId);
            pstmt.setString(14,actionExname);
            results = pstmt.executeUpdate();
            logger.debug("QRY---->"+pstmt);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForQwipi()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForQwipi()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(cn);
        }
        return results;
    }

    public int actionEntryForEcore(String parentId, String amount, String action, String status,String ipAddress, EcoreResponseVO responseVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry for Ecore");
        //String operation = "";
        String responseCode = "";

        //String dateTime = "";
        String paymentOrderNo = "";
        //String remark = "";
        //String md5Info = "";
        String respDescription = "";
        String processingTime = "";
        String statusDescription = "";
        String merchantIpAddress = "";
        String actionExId ="0";
        String actionExname="";

        if (responseVO != null)
        {
            //operation = responseVO.getOperation();
            //dateTime = responseVO.getDateTime();
            paymentOrderNo = responseVO.getTransactionID();
            //remark = responseVO.getRemark();
            //md5Info = responseVO.getMd5Info();
            //billingDescriptor = responseVO.getBillingDescriptor();
            responseCode = responseVO.getResponseCode();
            processingTime = responseVO.getProcessingTime();
            respDescription = responseVO.getDescription();
            statusDescription = responseVO.getStatusDescription();
        }
        if(auditTrailVO != null)
        {
            logger.debug("auditTrailVO is not null");
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        int results=0;
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_ecore_details(parentid,amount,action,status,responseResultcode,responseDateTime,responseRemark,responseBillingDescription, ecorePaymentOrderNumber,ipaddress,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            logger.debug("actionEntryForQwipi SQL---->" + sql);
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            //pstmt.setString(5, operation);
            pstmt.setString(5, responseCode);
            pstmt.setString(6, processingTime);
            //pstmt.setString(8, paymentOrderNo);
            pstmt.setString(7, respDescription);
            //pstmt.setString(10, md5Info);
            pstmt.setString(8, statusDescription);
            pstmt.setString(9, paymentOrderNo);
            pstmt.setString(10,ipAddress);
            pstmt.setString(11,actionExId);
            pstmt.setString(12,actionExname);
            results = pstmt.executeUpdate();

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForEcore()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForEcore()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(cn);
        }
        return results;
    }

    public int actionEntryForPaySafeCardDetails(CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        Connection cn = null;
        int results = 0;
        try
        {
            //GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();

            String sql = "insert into transaction_paysafecard_detils(trackingId,birthdate,firstname,lastname,emailid) values (?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, commonValidatorVO.getTrackingid());
            pstmt.setString(2, commonValidatorVO.getAddressDetailsVO().getBirthdate());
            pstmt.setString(3, commonValidatorVO.getAddressDetailsVO().getFirstname());
            pstmt.setString(4, commonValidatorVO.getAddressDetailsVO().getLastname());
            pstmt.setString(5, commonValidatorVO.getAddressDetailsVO().getEmail());

            results = pstmt.executeUpdate();

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForP4Details()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForP4Details()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }

    public void actionEntryExtensionforVM(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        transactionLogger.debug("enter in vouchermoney actionentry extension");
        transactionLogger.debug("enter in vouchermoney actionentry extension");
        int i=0;
        Connection conn= null;

        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_vouchermoney_details(trackingid,customerid,customerEmail,customerBankId) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, commonValidatorVO.getTrackingid() + "");
            pstmt.setString(2, commonValidatorVO.getCustomerId());
            pstmt.setString(3, commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(4, commonValidatorVO.getCustomerBankId());

            i = pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public void actionEntryExtensionforSkrillNeteller(CommonValidatorVO commonValidatorVO,String tableName) throws PZDBViolationException
    {
        transactionLogger.debug("enter in skrill actionentry extension");
        transactionLogger.debug("enter in skrill actionentry extension");
        int i=0;
        Connection conn= null;

        try
        {
            conn = Database.getConnection();
            String sql = "insert into "+tableName+"(trackingid,customerId,customerEmail,customerBankId) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, commonValidatorVO.getTrackingid() + "");
            pstmt.setString(2, commonValidatorVO.getCustomerId());
            pstmt.setString(3, commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(4, commonValidatorVO.getCustomerBankId());

            i = pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public void actionEntryExtensionforNeteller(CommonValidatorVO commonValidatorVO,String verificationLevel) throws PZDBViolationException
    {
        transactionLogger.debug("enter in Neteller actionentry extension");
        transactionLogger.debug("enter in Neteller actionentry extension");
        int i=0;
        Connection conn= null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_neteller_details (trackingid,customerId,customerEmail,customerBankId,verificationLevel) values (?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, commonValidatorVO.getTrackingid() + "");
            pstmt.setString(2, commonValidatorVO.getCustomerId());
            pstmt.setString(3, commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(4, commonValidatorVO.getCustomerBankId());
            pstmt.setString(5, verificationLevel);

            i = pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            if(conn!=null)
                Database.closeConnection(conn);
        }

    }

    public void actionEntryExtensionforSkrill(SkrillResponseVO commResponseVO,String trackingid) throws PZDBViolationException
    {
        transactionLogger.debug("enter in update skrill actionentry extension");
        transactionLogger.debug("enter in update skrill actionentry extension");
        int i=0;
        Connection conn= null;

        try
        {
            conn = Database.getConnection();
            String sql = "update transaction_skrill_details set transactionId=?,tmpl_amount=?,tmpl_currency=?,amount=?,currency=?,statusCode=?,merchantEmail=?,customerEmail=? where trackingId=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, commResponseVO.getTransactionId());
            pstmt.setString(2, commResponseVO.getCustomerAmount());
            pstmt.setString(3, commResponseVO.getCustomerCurrency());
            pstmt.setString(4, commResponseVO.getAmount());
            pstmt.setString(5, commResponseVO.getCurrency());
            pstmt.setString(6, commResponseVO.getErrorCode());
            pstmt.setString(7, commResponseVO.getResponseHashInfo());
            pstmt.setString(8, commResponseVO.getFromEmail());
            pstmt.setString(9, trackingid);

            logger.debug("update skrill---"+pstmt);

            i = pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtensionforSkrill()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtensionforSkrill()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public void actionEntryExtensionforEpay(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        transactionLogger.debug("enter in epay actionentry extension");
        transactionLogger.debug("enter in epay actionentry extension");
        int i=0;
        Connection conn= null;

        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_epay_details(trackingid,customerId,cemail,cin) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, commonValidatorVO.getTrackingid() + "");
            pstmt.setString(2, commonValidatorVO.getCustomerId());
            pstmt.setString(3, commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(4, commonValidatorVO.getCustomerBankId());

            i = pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ActionEntry.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public int actionEntryForPaymitco(String trackingId,String amount,String action,String status,CommResponseVO commResponseVO,String ipaddress,AuditTrailVO auditTrailVO)throws PZDBViolationException
    {
        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String transactionid = "";
        String transactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String actionExId ="0";
        String actionExname="";
        int results = 0;
        if (commResponseVO != null)
        {
            transactionid = commResponseVO.getTransactionId();
            transactionstatus = commResponseVO.getStatus();
            responsedescriptor = commResponseVO.getDescriptor();
            responseTime = commResponseVO.getResponseTime();
            transType = commResponseVO.getTransactionType();
        }
        if(auditTrailVO!=null)
        {
            logger.debug("auditTrailVO is not null");
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, transactionid);
            pstmt.setString(7, transactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15,actionExId);
            pstmt.setString(16,actionExname);
            results = pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForPaymitco()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForPaymitco()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }

    public int actionEntryForP4Details(String trackingId,String detailId,CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        String responsecode = "";

        String recipient_bic="";
        String recipient_iban="";
        String recipient_mandateId="";

        Functions functions = new Functions();

        int results = 0;
        int newDetailId = 0;
        if (commonValidatorVO != null)
        {
            recipient_bic=functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC())?commonValidatorVO.getCardDetailsVO().getBIC():"";
            recipient_iban=functions.isValueNull(commonValidatorVO.getCardDetailsVO().getIBAN())?commonValidatorVO.getCardDetailsVO().getIBAN():"";
            recipient_mandateId=functions.isValueNull(commonValidatorVO.getCardDetailsVO().getMandateId())?commonValidatorVO.getCardDetailsVO().getMandateId():"";
        }

        Connection cn = null;
        try
        {
            String sql = "insert into transaction_p4sepa_details(trackingId,detailId,IBAN,BIC,MandateId) values (?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, detailId);
            pstmt.setString(3, recipient_iban);
            pstmt.setString(4, recipient_bic);
            pstmt.setString(5, recipient_mandateId);

            results = pstmt.executeUpdate();

            if(results>0)
            {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                {
                    newDetailId = rs.getInt(1);
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForP4Details()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForP4Details()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return newDetailId;
    }

    public int actionEntryForB4Details(String trackingId,String detailId,CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        String responsecode = "";

        String recipient_bic="";
        String recipient_iban="";
        String recipient_mandateId="";

        Functions functions = new Functions();

        int results = 0;
        int newDetailId = 0;
        if (commonValidatorVO != null)
        {
            recipient_bic=functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC())?commonValidatorVO.getCardDetailsVO().getBIC():"";
            recipient_iban=functions.isValueNull(commonValidatorVO.getCardDetailsVO().getIBAN())?commonValidatorVO.getCardDetailsVO().getIBAN():"";
            recipient_mandateId=functions.isValueNull(commonValidatorVO.getCardDetailsVO().getMandateId())?commonValidatorVO.getCardDetailsVO().getMandateId():"";

            logger.debug("BIC IBAN MANDATEID-------->"+recipient_bic+"-----"+recipient_iban+"----"+recipient_mandateId);
            transactionLogger.debug("BIC IBAN MANDATEID-------->"+recipient_bic+"-----"+recipient_iban+"----"+recipient_mandateId);
        }

        Connection cn = null;
        try
        {
            String sql = "insert into transaction_b4sepaexpress_details(trackingId,detailId,IBAN,BIC,MandateId) values (?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, detailId);
            pstmt.setString(3, recipient_iban);
            pstmt.setString(4, recipient_bic);
            pstmt.setString(5, recipient_mandateId);

            results = pstmt.executeUpdate();

            if(results>0)
            {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next())
                {
                    newDetailId = rs.getInt(1);
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForP4Details()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForP4Details()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return newDetailId;
    }



    public int detailActionEntryForSofort(int newDetailId, String
            trackingId, String
                                                  amount, String
                                                  action, String
                                                  status, CommResponseVO
                                                  commResponseVO, CommRequestVO
                                                  commRequestVO)throws PZDBViolationException
    {


        String sender_holder="";
        String sender_accountNumber="";
        String sender_bankCode="";
        String sender_bankName="";
        String sender_countryCode="";
        String sender_bic="";
        String sender_iban="";

        String recipient_holder="";
        String recipient_accountNumber="";
        String recipient_bankCode="";
        String recipient_bankName="";
        String recipient_countryCode="";
        String recipient_bic="";
        String recipient_iban="";

        String paymentURL="";
        String paymentMethod="";
        String languageCode="";
        String amountRefunded="";


        int results = 0;
        if (commResponseVO != null && (commResponseVO instanceof SofortResponseVO))
        {
            if(((SofortResponseVO)commResponseVO).getSender()!=null)
            {
                sender_holder=((SofortResponseVO)commResponseVO).getSender().getHolder();
                sender_accountNumber=((SofortResponseVO)commResponseVO).getSender().getAccountNumber();
                sender_bankCode=((SofortResponseVO)commResponseVO).getSender().getBankCode();
                sender_bankName=((SofortResponseVO)commResponseVO).getSender().getBankName();
                sender_countryCode=((SofortResponseVO)commResponseVO).getSender().getCountryCode();
                sender_bic=((SofortResponseVO)commResponseVO).getSender().getBic();
                sender_iban=((SofortResponseVO)commResponseVO).getSender().getIban();
            }
            if(((SofortResponseVO)commResponseVO).getRecipient()!=null)
            {
                recipient_holder=((SofortResponseVO)commResponseVO).getRecipient().getHolder();
                recipient_accountNumber=((SofortResponseVO)commResponseVO).getRecipient().getAccountNumber();
                recipient_bankCode=((SofortResponseVO)commResponseVO).getRecipient().getBankCode();
                recipient_bankName=((SofortResponseVO)commResponseVO).getRecipient().getBankName();
                recipient_countryCode=((SofortResponseVO)commResponseVO).getRecipient().getCountryCode();
                recipient_bic=((SofortResponseVO)commResponseVO).getRecipient().getBic();
                recipient_iban=((SofortResponseVO)commResponseVO).getRecipient().getIban();
            }
            paymentURL=((SofortResponseVO)commResponseVO).getPaymentURL();
            paymentMethod=((SofortResponseVO)commResponseVO).getPaymentMethod();
            languageCode=((SofortResponseVO)commResponseVO).getLanguageCode();
            amountRefunded=((SofortResponseVO)commResponseVO).getAmountRefunded();


            Connection cn = null;
            try
            {
                String sql = "insert into transaction_sofort_details(detailid,trackingid,paymentURL,paymentMethod,sender_holder,sender_accountNumber,sender_bankCode,sender_bankName,sender_countryCode,sender_bic,sender_iban,recipient_holder,recipient_accountNumber,recipient_bankCode,recipient_bankName,recipient_countryCode,recipient_bic,recipient_iban,amountRefunded,languageCode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                cn = Database.getConnection();
                PreparedStatement pstmt = cn.prepareStatement(sql);
                pstmt.setInt(1, newDetailId);
                pstmt.setString(2, trackingId);
                pstmt.setString(3, paymentURL);
                pstmt.setString(4, paymentMethod);
                pstmt.setString(5, sender_holder);
                pstmt.setString(6, sender_accountNumber);
                pstmt.setString(7, sender_bankCode);
                pstmt.setString(8, sender_bankName);
                pstmt.setString(9, sender_countryCode);
                pstmt.setString(10, sender_bic);
                pstmt.setString(11, sender_iban);
                pstmt.setString(12, recipient_holder);
                pstmt.setString(13, recipient_accountNumber);
                pstmt.setString(14, recipient_bankCode);
                pstmt.setString(15,recipient_bankName);
                pstmt.setString(16,recipient_countryCode);
                pstmt.setString(17, recipient_bic);
                pstmt.setString(18, recipient_iban);
                pstmt.setString(19,amountRefunded);
                pstmt.setString(20,languageCode);


                results = pstmt.executeUpdate();
            }
            catch (SystemError se)
            {
                PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForPaySafeCard()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForPaySafeCard()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
            }
            finally
            {
                Database.closeConnection(cn);
            }
        }
        return results;
    }


    public int actionEntryForCUP(String parentId, String amount, String action, String status, CupResponseVO responseVO,String ipaddress,AuditTrailVO auditTrailVO,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry");
        //String operation = "";
        String responseCode = "";
        int detailId=0;
        //String dateTime = "";
        String paymentOrderNo = "";
        //String remark = "";
        //String md5Info = "";
        String respDescription = "";
        String processingTime = "";
        String transType = "";
        //String statusDescription = "";
        String actionExId ="0";
        String actionExname="";
        String fingerprint="";
        if(commonValidatorVO!=null)
        {
            if(commonValidatorVO.getDeviceDetailsVO()!=null){
                fingerprint=commonValidatorVO.getDeviceDetailsVO().getFingerprints();
            }
        }

        if (responseVO != null)
        {
            responseCode = responseVO.getResponseCode();
            respDescription = responseVO.getDescription();
            processingTime = responseVO.getProcessingTime();
            paymentOrderNo = responseVO.getTransactionID();
            transType = responseVO.getTransType();

        }
        if(auditTrailVO != null)
        {
            logger.debug("auditTrailVO is not null");
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,responsecode,responsetime,responsedescription, responsetransactionid, transtype,ipaddress,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            //pstmt.setString(5, operation);
            pstmt.setString(5, responseCode);
            pstmt.setString(6, processingTime);
            //pstmt.setString(8, paymentOrderNo);
            pstmt.setString(7, respDescription);
            //pstmt.setString(10, md5Info);
            //pstmt.setString(8, statusDescription);
            pstmt.setString(8, paymentOrderNo);
            pstmt.setString(9, transType);
            pstmt.setString(10,ipaddress);
            pstmt.setString(11,actionExId);
            pstmt.setString(12,actionExname);

            int results = pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            keys.next();
            detailId= keys.getInt(1);
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForCUP()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, se.getMessage(), se.getCause());

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForCUP()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, systemError.getMessage(), systemError.getCause());
        }
        finally {
            Database.closeConnection(cn);
        }
        return detailId;
    }
    public int actionEntryForPaysafeCard(String parentId, String amount, String action, String status, CommResponseVO responseVO,String ipaddress,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry");

        String responseCode = "";
        int detailId=0;
        String paymentOrderNo = "";
        String respDescription = "";
        String actionExId ="0";
        String actionExname="";

        if (responseVO != null)
        {
            responseCode = responseVO.getErrorCode();
            respDescription = responseVO.getDescription();
            paymentOrderNo = responseVO.getTransactionId();
        }
        if(auditTrailVO != null)
        {
            logger.debug("auditTrailVO is not null");
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,responsecode,responsedescription, responsetransactionid,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, responseCode);
            pstmt.setString(6, respDescription);
            pstmt.setString(7, paymentOrderNo);
            pstmt.setString(8,actionExId);
            pstmt.setString(9,actionExname);

            int results = pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            keys.next();
            detailId= keys.getInt(1);
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForPaysafeCard()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, se.getMessage(), se.getCause());

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "actionEntryForPaysafeCard()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,systemError.getMessage(), systemError.getCause());
        }
        finally {
            Database.closeConnection(cn);
        }
        return detailId;
    }
    public int actionEntryForMyMonedero(String parentId, String amount, String action, String status, MyMonederoResponseVO responseVO,AuditTrailVO auditTrailVO) throws SystemError
    {
        logger.debug("Entering ActionEntry");
        String wctxnid = "";
        String transactionstatus = "";
        String errorcode = "";
        String wcredirecturl = "";
        String transactionDate = "";
        String sourceID = "";
        String destID = "";
        String responseRemark = "";
        String actionExId ="0";
        String actionExname="";


        if (responseVO != null)
        {
            wctxnid = responseVO.getWctxnid();
            transactionstatus = responseVO.getStatus();
            errorcode = responseVO.getError();
            wcredirecturl = responseVO.getRedirecturl();
            transactionDate = responseVO.getTransactionDate();
            sourceID = responseVO.getSourceID();
            destID = responseVO.getDestID();
            responseRemark = responseVO.getResponseRemark();

        }
        if(auditTrailVO != null)
        {
            logger.debug("auditTrailVO is not null");
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,responsetransactionid,responsedescription,responsecode,responsetime,remark,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, wctxnid);
            pstmt.setString(6, transactionstatus);
            pstmt.setString(7, errorcode);

            pstmt.setString(8, transactionDate);

            pstmt.setString(9, responseRemark);
            pstmt.setString(10,actionExId);
            pstmt.setString(11,actionExname);

            int results = pstmt.executeUpdate();
            int detailid = 0;
            logger.debug("Results is ----" + results);
            if (results == 1)
            {
                ResultSet rs = pstmt.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        detailid = rs.getInt(1);
                    }
                }
            }
            sql = "insert into transaction_mymonedero_details(detailid,wcredirecturl,sourceid,destid,transdate) values (?,?,?,?,?)";
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, detailid + "");
            pstmt.setString(2, wcredirecturl);
            pstmt.setString(3, sourceID);
            pstmt.setString(4, destID);
            pstmt.setString(5, transactionDate);
            pstmt.executeUpdate();


            return results;
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }

    public int actionEntryForUGSPay(String parentId, String amount, String action, String status, UGSPayResponseVO responseVO,AuditTrailVO auditTrailVO) throws SystemError
    {
        logger.debug("Entering ActionEntry");
        String responseRemark = "";
        String ugstxnid = "";
        String transactionstatus = "";
        String errorcode = "";
        String errormessage = "";
        String FSResult = "";
        String FSStatus = "";
        String stmtDesc = "";
        String ACSUrl = "";
        String ACSRequestMessage = "";
        String result = "";
        String transdate = "";
        String actionExId ="0";
        String actionExname="";

        if (responseVO != null)
        {
            ACSUrl = responseVO.getACSUrl();
            ACSRequestMessage = responseVO.getACSRequestMessage();
            responseRemark = responseVO.getResponseRemark();
            ugstxnid = responseVO.getTransactionId();
            transactionstatus = responseVO.getTransactionStatus();
            errorcode = responseVO.getErrorCode();
            errormessage = responseVO.getDescription();
            FSResult = responseVO.getFSResult();
            FSStatus = responseVO.getFSStatus();
            stmtDesc = responseVO.getDescriptor();

        }
        if(auditTrailVO != null)
        {
            logger.debug("auditTrailVO is not null");
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,responsetransactionid,responsedescriptor,responsecode,responsedescription,remark,responsetransactionstatus,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, ugstxnid);
            pstmt.setString(6, stmtDesc);
            pstmt.setString(7, errorcode);
            pstmt.setString(8, errormessage);
            pstmt.setString(9, responseRemark);
            pstmt.setString(10, transactionstatus);
            pstmt.setString(11,actionExId);
            pstmt.setString(12,actionExname);
            int results = pstmt.executeUpdate();
            int detailid = 0;

            if (results == 1)
            {
                ResultSet rs = pstmt.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        detailid = rs.getInt(1);
                    }
                }
            }
            /*if(FSResult!=null && FSStatus!=null && ACSUrl!=null)
            {
                sql="insert into transaction_ugspay_details(detailid,FSResult,FSStatus,ACSUrl,ACSRequestMessage) values (?,?,?,?,?)";
                pstmt = cn.prepareStatement(sql);
                pstmt.setString(1,detailid+"");
                pstmt.setString(2,FSResult);
                pstmt.setString(3,FSStatus);
                pstmt.setString(4,ACSUrl);
                pstmt.setString(5,ACSRequestMessage);
                pstmt.executeUpdate();
            }*/
            return results;
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }



    public int actionEntryForCommon(String trackingId, String amount, String action, String status, CommResponseVO responseVO,AuditTrailVO auditTrailVO,String ipaddress) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry for Common123");
        Functions functions = new Functions();

        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String responsetransactionid ="" ;
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String errorName = "";
        String currency="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String walletAmount="";
        String walletCurrency="";
        String arn="";
        String rrn="";
        String authCode="";

        String actionExId ="0";
        String actionExname="";

        Connection cn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int results=0;
        int detailid = 0;

        if(functions.isValueNull(auditTrailVO.getCbReason()))
            remark = auditTrailVO.getCbReason();
        if (responseVO != null)
        {
            responsecode = responseVO.getErrorCode();
            responsetransactionid = String.valueOf(responseVO.getTransactionId());
            responsedescription = responseVO.getDescription();
            // responsedescriptor = responseVO.getDescriptor();
            responseTime = responseVO.getResponseTime();
            responsetransactionstatus = responseVO.getStatus();
            transType = responseVO.getTransactionType();
            responsehashinfo = responseVO.getResponseHashInfo();
            ipaddress = responseVO.getIpaddress();
            if (functions.isValueNull(responseVO.getErrorName()))
                errorName = responseVO.getErrorName();
            if(functions.isValueNull(responseVO.getCurrency())){
                currency=responseVO.getCurrency();
            }
            if(functions.isValueNull(responseVO.getTmpl_Amount())){
                tmpl_Amount=responseVO.getTmpl_Amount();
            }
            if(functions.isValueNull(responseVO.getTmpl_Currency())){
                tmpl_Currency=responseVO.getTmpl_Currency();
            }
            if(functions.isValueNull(responseVO.getWalletAmount())){
                walletAmount=responseVO.getWalletAmount();
            }
            if(functions.isValueNull(responseVO.getWalletCurrecny())){
                walletCurrency=responseVO.getWalletCurrecny();
            }
            if(functions.isValueNull(responseVO.getArn())){
                arn=responseVO.getArn();
            }
            if(functions.isValueNull(responseVO.getRrn())){
                rrn=responseVO.getRrn();
            }
            if(functions.isValueNull(responseVO.getAuthCode())){
                authCode=responseVO.getAuthCode();
            }
            if(!functions.isValueNull(remark))
                remark = responseVO.getRemark();

            transactionLogger.debug("tmp_amt----"+tmpl_Amount+"----tmplCurrecy----"+tmpl_Currency+"---currency---"+currency+"walletAmount----"+walletAmount+"walletCurrency-----"+walletCurrency);
            remark = responseVO.getRemark();
            transactionLogger.debug("remark in actionentry----"+remark);
            logger.debug("ipaddress from VT 3 "+ipaddress);
            if (functions.isValueNull(responseVO.getStatus()) && ("approved".equalsIgnoreCase(responseVO.getStatus()) || responseVO.getStatus().contains("success") || responseVO.getStatus().contains("Success")))
            {
                responsedescriptor = responseVO.getDescriptor();
            }
            else
            {
                responsedescriptor = "";
            }
        }
        transactionLogger.error("auditTrailVO.getActionExecutorId--------->"+auditTrailVO.getActionExecutorId());
        if(auditTrailVO != null)
        {
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        try
        {   logger.debug("ipaddress from VT 4 "+ipaddress);
            transactionLogger.debug("ActionEntry.actionEntryForCommon ::::::::: DB Call");
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname, errorName,currency,templateamount,templatecurrency,walletAmount,walletCurrency,arn,rrn,authorization_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            transactionLogger.error("sql query------->"+sql);
            transactionLogger.error("status------->"+status);
            transactionLogger.error("amount------->"+amount);
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, responsetransactionid);
            pstmt.setString(7, responsetransactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16,actionExname);
            pstmt.setString(17,errorName);
            pstmt.setString(18,currency);
            pstmt.setString(19,tmpl_Amount);
            pstmt.setString(20,tmpl_Currency);
            pstmt.setString(21,walletAmount);
            pstmt.setString(22,walletCurrency);
            pstmt.setString(23,arn);
            pstmt.setString(24,rrn);
            pstmt.setString(25,authCode);

            results = pstmt.executeUpdate();

            if (results == 1)
            {
                rs = pstmt.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        detailid = rs.getInt(1);
                    }
                }
            }

            logger.debug("action entry---"+pstmt+"--"+results);

            //@TODO : Gateways specific details entry using the gateway object

        }
        catch (SystemError se)
        {
            logger.error("SystemError in ActionEntry---",se);
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SystemError in ActionEntry---",e);
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java", "actionEntryForCommon()", null, "common", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally {

            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(cn);
        }
        return results;
    }

    public int actionEntryForCommon(String trackingId, String amount, String action, String status, CommResponseVO responseVO,CommRequestVO requestVO,AuditTrailVO auditTrailVO,String ipaddress,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry for Common123");
        Functions functions= new Functions();

        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String responsetransactionid ="" ;
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String consentStmnt="";
        String tmpl_Amount="";
        String tmp_Currency="";
        String currency="";
        String walletAmount = "";
        String walletCurrency = "";
        String fingerprint = "";

        String actionExId ="0";
        String actionExname="";
        Connection cn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int results=0;
        int detailid = 0;
        if(commonValidatorVO!=null)
        {
            if(commonValidatorVO.getDeviceDetailsVO()!=null)
            {
                fingerprint=commonValidatorVO.getDeviceDetailsVO().getFingerprints();
            }
        }
        if (responseVO != null)
        {
            responsecode = responseVO.getErrorCode();
            responsetransactionid = String.valueOf(responseVO.getTransactionId());
            responsedescription = responseVO.getDescription();
            responseTime = responseVO.getResponseTime();
            responsetransactionstatus = responseVO.getStatus();
            // responsedescriptor = responseVO.getDescriptor();


            if (functions.isValueNull(responseVO.getStatus()) && "success".equalsIgnoreCase(responseVO.getStatus()))
            {
                responsedescriptor = responseVO.getDescriptor();
            }
            logger.debug(" Billing Descriptor in ActionEntry---"+responsedescription);
            transactionLogger.debug(" Billing Descriptor in ActionEntry---"+responsedescription);


            transType = responseVO.getTransactionType();
            responsehashinfo = responseVO.getResponseHashInfo();
            remark = responseVO.getRemark();
            logger.debug("ipaddress from VT 3 "+ipaddress);
        }

        if (requestVO != null && responseVO==null)
        {
            if(requestVO.getAddressDetailsVO()!=null)
            {
                if (requestVO.getAddressDetailsVO().getCardHolderIpAddress() != null)
                {
                    ipaddress = requestVO.getAddressDetailsVO().getCardHolderIpAddress();
                }
                if (functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_amount()))
                {
                    tmpl_Amount = requestVO.getAddressDetailsVO().getTmpl_amount();
                }
                if (functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_currency()))
                {
                    tmp_Currency = requestVO.getAddressDetailsVO().getTmpl_currency();
                }
            }
            CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
            if(commTransactionDetailsVO!=null)
            {
                responsetransactionid = commTransactionDetailsVO.getResponseHashInfo();

                if(functions.isValueNull(commTransactionDetailsVO.getCurrency())){
                    currency=commTransactionDetailsVO.getCurrency();
                }

                if(functions.isValueNull(commTransactionDetailsVO.getWalletAmount())){
                    walletAmount=commTransactionDetailsVO.getWalletAmount();
                }

                if(functions.isValueNull(commTransactionDetailsVO.getWalletCurrency())){
                    walletCurrency=commTransactionDetailsVO.getWalletCurrency();
                }
            }
            transactionLogger.debug("Currency----"+currency+"----tmpl_Amount----"+tmpl_Amount+"----tmpl_Currency---"+tmp_Currency);
            CommMerchantVO commMerchantVO=requestVO.getCommMerchantVO();
            if(commMerchantVO!=null)
            {
                transactionLogger.debug("isService Flag----"+commMerchantVO.getIsService());
                if("N".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.AUTH.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
                else if("Y".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.SALE.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
            }
            consentStmnt=requestVO.getConsentStmnt();
        }

        if(auditTrailVO != null)
        {
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        try
        {   logger.debug("ipaddress from VT 4 "+ipaddress);
            transactionLogger.debug("ActionEntry.actionEntryForCommon ::::::::: DB Call");
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,consentStmnt,currency,templateamount,templatecurrency,walletAmount,walletCurrency) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, responsetransactionid);
            pstmt.setString(7, responsetransactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16,actionExname);
            pstmt.setString(17,consentStmnt);
            pstmt.setString(18,currency);
            pstmt.setString(19,tmpl_Amount);
            pstmt.setString(20,tmp_Currency);
            pstmt.setString(21,walletAmount);
            pstmt.setString(22,walletCurrency);

            results = pstmt.executeUpdate();

            if (results == 1)
            {
                rs = pstmt.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        detailid = rs.getInt(1);
                    }
                }
            }

            //@TODO : Gateways specific details entry using the gateway object

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(cn);
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);

        }
        return detailid;
    }

    public int actionEntryForCupUPI(String trackingId, String amount, String action, String status, CommResponseVO responseVO,CommRequestVO requestVO,AuditTrailVO auditTrailVO,String ipaddress) throws PZDBViolationException
    {
        logger.error("Entering actionEntryForCupUPI --- ");
        Functions functions= new Functions();

        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String responsetransactionid ="" ;
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String consentStmnt="";
        String tmpl_Amount="";
        String tmp_Currency="";
        String currency="";
        String walletAmount = "";
        String walletCurrency = "";

        String actionExId ="0";
        String actionExname="";
        Connection cn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int results=0;
        int detailid = 0;
        if (responseVO != null)
        {
            responsecode = responseVO.getErrorCode();
            responsetransactionid = String.valueOf(responseVO.getTransactionId());
            responsedescription = responseVO.getDescription();
            responseTime = responseVO.getResponseTime();
            responsetransactionstatus = responseVO.getStatus();

            if (functions.isValueNull(responseVO.getStatus()) && "success".equalsIgnoreCase(responseVO.getStatus()))
            {
                responsedescriptor = responseVO.getDescriptor();
            }
            logger.debug(" Billing Descriptor in ActionEntry---"+responsedescription);
            transactionLogger.debug(" Billing Descriptor in ActionEntry---"+responsedescription);


            transType = responseVO.getTransactionType();
            responsehashinfo = responseVO.getResponseHashInfo();
            remark = responseVO.getRemark();
            logger.debug("ipaddress from VT 3 "+ipaddress);
        }

        if (requestVO != null && responseVO==null)
        {
            if(requestVO.getAddressDetailsVO().getCardHolderIpAddress() != null)
            {
                ipaddress = requestVO.getAddressDetailsVO().getCardHolderIpAddress();
            }
            if(functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_amount())){
                tmpl_Amount=requestVO.getAddressDetailsVO().getTmpl_amount();
            }
            if(functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_currency())){
                tmp_Currency=requestVO.getAddressDetailsVO().getTmpl_currency();
            }
            CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
            if(commTransactionDetailsVO!=null)
            {
                responsetransactionid = commTransactionDetailsVO.getResponseHashInfo();

                if(functions.isValueNull(commTransactionDetailsVO.getCurrency())){
                    currency=commTransactionDetailsVO.getCurrency();
                }

                if(functions.isValueNull(commTransactionDetailsVO.getWalletAmount())){
                    walletAmount=commTransactionDetailsVO.getWalletAmount();
                }

                if(functions.isValueNull(commTransactionDetailsVO.getWalletCurrency())){
                    walletCurrency=commTransactionDetailsVO.getWalletCurrency();
                }
            }
            transactionLogger.debug("Currency----"+currency+"----tmpl_Amount----"+tmpl_Amount+"----tmpl_Currency---"+tmp_Currency);
            CommMerchantVO commMerchantVO=requestVO.getCommMerchantVO();
            if(commMerchantVO!=null)
            {
                transactionLogger.debug("isService Flag----"+commMerchantVO.getIsService());
                if("N".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.AUTH.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
                else if("Y".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.SALE.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
            }
            consentStmnt=requestVO.getConsentStmnt();
        }

        if(auditTrailVO != null)
        {
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        try
        { logger.debug("ipaddress from VT 4 "+ipaddress);
            transactionLogger.debug("ActionEntry.actionEntryForCommon ::::::::: DB Call");
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,consentStmnt,currency,templateamount,templatecurrency,walletAmount,walletCurrency) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, responsetransactionid);
            pstmt.setString(7, responsetransactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16,actionExname);
            pstmt.setString(17,consentStmnt);
            pstmt.setString(18,currency);
            pstmt.setString(19,tmpl_Amount);
            pstmt.setString(20,tmp_Currency);
            pstmt.setString(21,walletAmount);
            pstmt.setString(22,walletCurrency);

            results = pstmt.executeUpdate();

            if (results == 1)
            {
                rs = pstmt.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        detailid = rs.getInt(1);
                    }
                }
            }

//@TODO : Gateways specific details entry using the gateway object

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(cn);
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);

        }
        return detailid;
    }

    public int actionEntryFor3DCommon(String trackingId, String amount, String action, String status, CommResponseVO responseVO,CommRequestVO requestVO,AuditTrailVO auditTrailVO,String ipaddress) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry for Common123");
        Functions functions= new Functions();

        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String responsetransactionid ="" ;
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";

        String actionExId ="0";
        String actionExname="";
        String tmpl_Amount="";
        String tmp_Currency="";
        String currency="";
        String walletAmount = "";
        String walletCurrency = "";

        Connection cn = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        int results=0;
        int detailid = 0;
        if (responseVO != null)
        {
            responsecode = responseVO.getErrorCode();
            responsetransactionid = String.valueOf(responseVO.getTransactionId());
            responsedescription = responseVO.getDescription();
            responseTime = responseVO.getResponseTime();
            responsetransactionstatus = responseVO.getStatus();
            // responsedescriptor = responseVO.getDescriptor();

            if ("success".equalsIgnoreCase(responseVO.getStatus().trim()))
            {
                responsedescriptor = responseVO.getDescriptor();
            }
            logger.debug("ACH Billing Descriptor in ActionEntry---"+responsedescription);
            transactionLogger.debug("ACH Billing Descriptor in ActionEntry---"+responsedescription);


            transType = responseVO.getTransactionType();
            responsehashinfo = responseVO.getResponseHashInfo();
            transactionLogger.error("responsehashinfo action entry--------------------------"+responseVO.getResponseHashInfo());
            remark = responseVO.getRemark();
            logger.debug("ipaddress from VT 3 "+ipaddress);
        }

        if (requestVO != null && responseVO==null)
        {
            if(requestVO.getAddressDetailsVO().getCardHolderIpAddress() != null)
            {
                ipaddress = requestVO.getAddressDetailsVO().getCardHolderIpAddress();
            }
            if(functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_amount())){
                tmpl_Amount=requestVO.getAddressDetailsVO().getTmpl_amount();
            }
            if(functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_currency())){
                tmp_Currency=requestVO.getAddressDetailsVO().getTmpl_currency();
            }
            CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
            if(commTransactionDetailsVO!=null)
            {
                responsetransactionid = commTransactionDetailsVO.getResponseHashInfo();
                if(functions.isValueNull(commTransactionDetailsVO.getCurrency())){
                    currency=commTransactionDetailsVO.getCurrency();
                }

                if(functions.isValueNull(commTransactionDetailsVO.getWalletAmount())){
                    walletAmount = commTransactionDetailsVO.getWalletAmount();
                }

                if(functions.isValueNull(commTransactionDetailsVO.getWalletCurrency())){
                    walletCurrency = commTransactionDetailsVO.getWalletCurrency();
                }

            }
            CommMerchantVO commMerchantVO=requestVO.getCommMerchantVO();
            if(commMerchantVO!=null)
            {
                transactionLogger.debug("isService Flag----"+commMerchantVO.getIsService());
                if("N".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.THREE_D_AUTH.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
                else if("Y".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.THREE_D_SALE.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
            }


        }

        if(auditTrailVO != null)
        {
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        try
        {   logger.debug("ipaddress from VT 4 "+ipaddress);
            transactionLogger.debug("ActionEntry.actionEntryForCommon ::::::::: DB Call");
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,currency,templateamount,templatecurrency,walletAmount,walletCurrency) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, responsetransactionid);
            pstmt.setString(7, responsetransactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16,actionExname);
            pstmt.setString(17,currency);
            pstmt.setString(18,tmpl_Amount);
            pstmt.setString(19,tmp_Currency);
            pstmt.setString(20,walletAmount);
            pstmt.setString(21,walletCurrency);

            results = pstmt.executeUpdate();

            if (results == 1)
            {
                resultSet = pstmt.getGeneratedKeys();

                if (resultSet != null)
                {
                    while (resultSet.next())
                    {

                        detailid = resultSet.getInt(1);
                    }
                }
            }

            //@TODO : Gateways specific details entry using the gateway object

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(resultSet);
            Database.closeConnection(cn);
        }
        return detailid;
    }

    public int actionEntryForPayVoucher(String parentId, String amount, String action, String status, PayLineVoucherResponseVO responseVO) throws SystemError
    {
        logger.debug("Entering ActionEntry");

        String channel = "";
        String shortid = "";
        String uniqueid = "";
        String paymentcode = "";
        String returncurrency = "";
        String processingcode = "";
        String timestampreturned = "";
        String result = "";
        String statuscode = "";
        String respstatus = "";
        String reasoncode = "";
        String reason = "";
        String returncode = "";
        String respreturn = "";
        String returnamount = "";
        if (responseVO != null)
        {
            channel = responseVO.getChannel();
            shortid = responseVO.getShortId();
            uniqueid = responseVO.getUniqueId();
            paymentcode = responseVO.getPaymentCode();
            returncurrency = responseVO.getCurrency();
            processingcode = responseVO.getProcessingCode();
            timestampreturned = responseVO.getTimeStamp();
            result = responseVO.getResult();
            statuscode = responseVO.getStatusCode();
            respstatus = responseVO.getStatus();
            reasoncode = responseVO.getErrorCode();
            reason = responseVO.getDescription();
            returncode = responseVO.getReturnCode();
            respreturn = responseVO.getReturnMessage();
            returnamount = responseVO.getClearingAmount();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_payvoucher_details(parentid,amount,action,status,channel,shortid,uniqueid,paymentcode,returncurrency,processingcode,timestampreturned,result,statuscode,respstatus,reasoncode,reason,returncode,returnmessage,returnamount) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, parentId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, channel);
            pstmt.setString(6, shortid);
            pstmt.setString(7, uniqueid);
            pstmt.setString(8, paymentcode);
            pstmt.setString(9, returncurrency);
            pstmt.setString(10, processingcode);
            pstmt.setString(11, timestampreturned);
            pstmt.setString(12, result);
            pstmt.setString(13, statuscode);
            pstmt.setString(14, respstatus);
            pstmt.setString(15, reasoncode);
            pstmt.setString(16, reason);
            pstmt.setString(17, returncode);
            pstmt.setString(18, respreturn);
            pstmt.setString(19, returnamount);

            int results = pstmt.executeUpdate();

            return results;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }


    public Hashtable getActionHistoryByTrackingId(String icicitransid) throws SystemError
    {
        logger.debug("Entering getActionHistoryByTrackingId");
        Hashtable transactionHistory = new Hashtable();

        Connection cn = null;
        try
        {
            String sql = "select icicitransid,amount,action,status,timestamp from action_history where icicitransid=? order by timestamp DESC";
            String count = "select count(*) from action_history where icicitransid=? ";//order by  DESC LIMIT"+start+","+end;
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, icicitransid);
            transactionHistory = Database.getHashFromResultSet(pstmt.executeQuery());
            PreparedStatement ps = cn.prepareStatement(count);
            ps.setString(1, icicitransid);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));

            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }

    public Hashtable getActionHistoryByTrackingIdAndGateway(String icicitransid, String gatewayType) throws SystemError
    {
        logger.debug("Entering getActionHistoryByTrackingId");
        Hashtable transactionHistory = new Hashtable();

        String tableName = Database.getTableName(gatewayType);
        StringBuilder sql = new StringBuilder();
        StringBuilder count = new StringBuilder();
        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {

            if (tableName.equals("transaction_icicicredit"))
            {
                sql.append("select icicitransid,amount,action,status,timestamp from action_history where icicitransid=?");
            }
            else if (tableName.equals("transaction_common"))
            {

                //sql.append("select trackingid as icicitransid,amount,currency,templatecurrency,templateamount,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetransactionid from " + tableName + "_details" + " where trackingid=? order by timestamp DESC");
                sql.append("select trackingid ,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetransactionid,responsetime as ResponseTime,ipAddress,transactionReceiptImg,rrn from ");
                sql.append(tableName);
                sql.append("_details");
                //sql.append("transaction_common AS t");
                sql.append(" where trackingid=?  ORDER BY detailid DESC");
            }
            else
            {

                //sql.append("select parentid as icicitransid,amount,currency,templatecurrency,templateamount,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark, errorName from " + tableName + "_details" + " where parentid=? order by timestamp DESC");
                sql.append("select parentid,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark,responseDateTime as ResponseTime,errorName from ");
                sql.append(tableName);
                sql.append("_details");
                sql.append(" where parentid=? ORDER BY detailid DESC");
            }
            //cn = Database.getConnection();
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(sql.toString());
            pstmt.setString(1, icicitransid);
            logger.error("query getActionHistoryByTrackingIdAndGateway ---- "+pstmt);

            transactionHistory = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            if (tableName.equals("transaction_icicicredit"))
            {
                count.append("select count(*) from action_history where icicitransid=? ");
            }
            else if (tableName.equals("transaction_common"))
            {
                //count.append("select count(*) from " + tableName + "_details" + " where trackingid=? ");
                count.append("select count(*) from ");
                count.append(tableName);
                count.append("_details");
                count.append(" where trackingid=? ");
            }
            else
            {
                //count = "select count(*) from " + tableName + "_details" + " where parentid=? ";
                count.append("select count(*) from ");
                count.append(tableName);
                count.append("_details");
                count.append(" where parentid=? ");
            }
            ps = cn.prepareStatement(count.toString());

            ps.setString(1, icicitransid);
            rs = ps.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
            {
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));
            }

            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(ps);
            Database.closeConnection(cn);
        }

    }

    // To get action history by tracking ID and gayway for card present transaction.
    public Hashtable getActionHistoryByTrackingIdAndGatewayCP(String icicitransid, String gatewayType) throws SystemError
    {
        System.out.println("enterd");
        logger.debug("Entering getActionHistoryByTrackingId");
        Hashtable transactionHistory = new Hashtable();

        String tableName = Database.getTableName(gatewayType);
        StringBuilder sql = new StringBuilder();
        StringBuilder count = new StringBuilder();
        Connection cn = null;
        PreparedStatement pstmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {

            if (tableName.equals("transaction_icicicredit"))
            {
                sql.append("select icicitransid,amount,action,status,timestamp from action_history where icicitransid=?");
            }
            else if (tableName.equals("transaction_common"))
            {

                //sql.append("select trackingid as icicitransid,amount,currency,templatecurrency,templateamount,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetransactionid from " + tableName + "_details" + " where trackingid=? order by timestamp DESC");
                sql.append("select trackingid ,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetransactionid,ipAddress,responsetime as ResponseTime,rrn from ");
                sql.append(tableName);
                sql.append("_details_card_present");
                //sql.append("transaction_common AS t");
                sql.append(" where trackingid=?  ORDER BY TIMESTAMP DESC,detailid DESC");
            }
            else
            {
                //sql.append("select parentid as icicitransid,amount,currency,templatecurrency,templateamount,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark, errorName from " + tableName + "_details" + " where parentid=? order by timestamp DESC");
                sql.append("select parentid,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark, errorName,responsetime as ResponseTime from ");
                sql.append(tableName);
                sql.append("_details_card_present");
                sql.append(" where parentid=? ORDER BY TIMESTAMP DESC");
            }
            //cn = Database.getConnection();
            cn = Database.getRDBConnection();
            pstmt = cn.prepareStatement(sql.toString());
            pstmt.setString(1, icicitransid);

            transactionHistory = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            if (tableName.equals("transaction_icicicredit"))
            {
                count.append("select count(*) from action_history where icicitransid=? ");
            }
            else if (tableName.equals("transaction_common"))
            {
                //count.append("select count(*) from " + tableName + "_details" + " where trackingid=? ");
                count.append("select count(*) from ");
                count.append(tableName);
                count.append("_details_card_present");
                count.append(" where trackingid=? ");
            }
            else
            {
                //count = "select count(*) from " + tableName + "_details" + " where parentid=? ";
                count.append("select count(*) from ");
                count.append(tableName);
                count.append("_details_card_present");
                count.append(" where parentid=? ");
            }
            ps = cn.prepareStatement(count.toString());

            ps.setString(1, icicitransid);
            rs = ps.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
            {
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));
            }

            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(ps);
            Database.closeConnection(cn);
        }

    }

    public Hashtable getActionHistoryByTrackingIdAndGatewaySet(String icicitransid, Set<String> gatewayTypeSet) throws SystemError
    {
        logger.debug("Entering getActionHistoryByTrackingIdandGatewaySet");
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Hashtable transactionHistory = new Hashtable();
        StringBuffer sql = new StringBuffer();
        String fields = null;
        Connection conn = null;
        try
        {


            Iterator i = gatewayTypeSet.iterator();
            while (i.hasNext())
            {
                String temp = (String) i.next();
                logger.debug(temp);
                String tableName = Database.getTableName(temp);
                String detailstableName = Database.getDetailsTableName(temp);
                if (tableName.equals("transaction_icicicredit"))
                {
                    fields = " select actionid as ActionId,icicitransid as TrackingId,amount,action,status,timestamp from " + detailstableName + " where icicitransid=";
                }
                else if (tableName.equals("transaction_common"))
                {
                    fields = " select detailid as ActionId,trackingid as TrackingId ,amount,action,status,timestamp from " + detailstableName + " where trackingid=";
                }
                else
                {
                    fields = " select trackingid as ActionId,parentid as TrackingId ,amount,action,status,timestamp from " + detailstableName + " where parentid=";
                }
                sql.append(fields + "" + ESAPI.encoder().encodeForSQL(me, icicitransid));
                if (i.hasNext())
                    sql.append(" union ");
            }
            logger.debug(sql);

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + sql + ") as temp ");

            conn = Database.getConnection();

            transactionHistory = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sql.toString(), conn));

            ResultSet rs = Database.executeQuery(countquery.toString(), conn);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));

            logger.debug("transactionHistory ----" + transactionHistory.toString());

            return transactionHistory;
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }

    }


    public Hashtable getActionHistoryByMerchantId(String merchantId, int start, int end) throws SystemError
    {
        logger.debug("Entering getActionHistoryByMerchantId");
        Hashtable transactionHistory = new Hashtable();
        Connection cn = null;
        logger.debug("Entering getActionHistoryByMerchantId  start" + start + "  end" + end);
        try
        {
            String sql = "select A.icicitransid, A.amount,A.action,A.status,A.timestamp from action_history as A,transaction_icicicredit as T where A.icicitransid = T.icicitransid and T.toid=? order by A.icicitransid DESC, A.timestamp DESC  limit ?,? ";
            String count = "select count(*)  from action_history as A,transaction_icicicredit as T where A.icicitransid = T.icicitransid and T.toid=?";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);

            pstmt.setString(1, merchantId);
            pstmt.setInt(2, start);
            pstmt.setInt(3, end);

            transactionHistory = Database.getHashFromResultSet(pstmt.executeQuery());
            PreparedStatement ps = cn.prepareStatement(count);
            ps.setString(1, merchantId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));


            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }


    }

    public Hashtable getActionHistoryByMerchantIdandGatewaySet(String merchantId, int start, int end, Set<String> gatewayTypeSet) throws SystemError
    {
        logger.debug("Entering getActionHistoryByMerchantIdandGatewaySet");
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Hashtable transactionHistory = new Hashtable();
        StringBuffer sql = new StringBuffer();
        String fields = null;
        Connection conn = null;
        try
        {


            Iterator i = gatewayTypeSet.iterator();
            while (i.hasNext())
            {
                String temp = (String) i.next();
                String tableName = Database.getTableName(temp);
                String detailstableName = Database.getDetailsTableName(temp);
                if (tableName == "transaction_icicicredit")
                {
                    fields = (detailstableName + ".icicitransid as TrackingId," + detailstableName + ".amount," + detailstableName + ".action," + detailstableName + ".status," + detailstableName + ".timestamp,"+  detailstableName +".actionexecutorname from " + detailstableName + "," + tableName + " where " + detailstableName + ".icicitransid = " + tableName + ".icicitransid");
                }
                else if (tableName == "transaction_common")
                {
                    fields = (detailstableName + ".trackingid as TrackingId," + detailstableName + ".amount," + detailstableName + ".action," + detailstableName + ".status," + detailstableName + ".timestamp ,"+  detailstableName +".actionexecutorname from " + detailstableName + "," + tableName + " where " + detailstableName + ".trackingid = " + tableName + ".trackingid");
                }
                else
                {
                    fields = (detailstableName + ".parentid as TrackingId," + detailstableName + ".amount," + detailstableName + ".action," + detailstableName + ".status," + detailstableName + ".timestamp,"+  detailstableName +".actionexecutorname from " + detailstableName + "," + tableName + " where " + detailstableName + ".parentid = " + tableName + ".trackingid");
                }
                sql.append("select " + fields);
                sql.append(" and " + tableName + ".toid = " + ESAPI.encoder().encodeForSQL(me, merchantId));
                if (i.hasNext())
                    sql.append(" union ");

            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + sql + ") as temp ");
            sql.append("");
            sql.append("  limit " + start + "," + end);
            conn = Database.getConnection();
            logger.debug(sql);
            transactionHistory = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sql.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);


            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));


            logger.debug("transactionHistory---" + transactionHistory);
            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }


    }


    public Hashtable getActionHistoryByMID(String mid, int start, int end) throws SystemError
    {
        logger.debug("Entering getActionHistoryByMID");
        Hashtable transactionHistory = new Hashtable();

        Connection cn = null;
        try
        {
            String sql = "select A.icicitransid, A.amount,A.action,A.status,A.timestamp from action_history as A,transaction_icicicredit as T where A.icicitransid = T.icicitransid and T.mid=? order by A.icicitransid DESC, A.timestamp DESC limit ?,?";
            String count = "select count(*) from action_history as A,transaction_icicicredit as T where A.icicitransid = T.icicitransid and T.mid=? ";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);

            pstmt.setString(1, mid);
            pstmt.setInt(2, start);
            pstmt.setInt(3, end);
            transactionHistory = Database.getHashFromResultSet(pstmt.executeQuery());


            PreparedStatement ps = cn.prepareStatement(count);
            ps.setString(1, mid);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);
            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");
            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));
            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally {
            Database.closeConnection(cn);
        }

    }

    public Hashtable getActionHistoryByMIDandGatewaySet(String mid, int start, int end, Set<String> gatewayTypeSet) throws SystemError
    {
        logger.debug("Entering getActionHistoryByMerchantIdandGatewaySet");
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Hashtable transactionHistory = new Hashtable();
        StringBuffer sql = new StringBuffer();
        String fields = null;
        Connection conn = null;
        try
        {


            Iterator i = gatewayTypeSet.iterator();
            while (i.hasNext())
            {
                String temp = (String) i.next();
                String tableName = Database.getTableName(temp);
                String detailstableName = Database.getDetailsTableName(temp);
                if (tableName == "transaction_icicicredit")
                {
                    fields = detailstableName + ".icicitransid as TrackingId, " + detailstableName + ".amount," + detailstableName + ".action," + detailstableName + ".status, " + detailstableName + ".timestamp from " + detailstableName + "," + tableName + " where " + detailstableName + ".icicitransid = " + tableName + ".icicitransid";

                }
                else if (tableName == "transaction_common")
                {
                    fields = detailstableName + ".trackingid as TrackingId, " + detailstableName + ".amount," + detailstableName + ".action," + detailstableName + ".status, " + detailstableName + ".timestamp from " + detailstableName + "," + tableName + " where " + detailstableName + ".trackingid = " + tableName + ".trackingid";
                }
                else
                {
                    fields = detailstableName + ".parentid as TrackingId, " + detailstableName + ".amount," + detailstableName + ".action," + detailstableName + ".status, " + detailstableName + ".timestamp from " + detailstableName + "," + tableName + " where " + detailstableName + ".parentid = " + tableName + ".trackingid";
                }
                sql.append("select " + fields);
                sql.append(" and " + tableName + ".mid ='" + ESAPI.encoder().encodeForSQL(me, mid) + "");

                if (i.hasNext())
                    sql.append(" union ");


            }


            StringBuffer countquery = new StringBuffer("select count(*) from ( " + sql + ") as temp ");
            sql.append("");
            sql.append("  limit " + start + "," + end);
            conn = Database.getConnection();
            logger.debug(sql);
            transactionHistory = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sql.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);


            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            transactionHistory.put("totalrecords", "" + totalrecords);
            transactionHistory.put("records", "0");

            if (totalrecords > 0)
                transactionHistory.put("records", "" + (transactionHistory.size() - 2));


            logger.debug("transactionHistory---" + transactionHistory);
            return transactionHistory;

        }
        catch (SQLException se)
        {
            logger.error("SQL Exception:::::", se);

            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public int actionEntryForCommon(String trackingId, String amount, String action, String status, CommResponseVO responseVO,CommRequestVO requestVO,AuditTrailVO auditTrailVO,String ipaddress) throws PZDBViolationException
    {
        logger.debug("Entering ActionEntry for Common123");
        Functions functions= new Functions();

        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String responsetransactionid ="" ;
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String consentStmnt="";
        String tmpl_Amount="";
        String tmp_Currency="";
        String currency="";

        String actionExId ="0";
        String actionExname="";
        Connection cn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int results=0;
        int detailid = 0;
        if (responseVO != null)
        {
            responsecode = responseVO.getErrorCode();
            responsetransactionid = String.valueOf(responseVO.getTransactionId());
            responsedescription = responseVO.getDescription();
            responseTime = responseVO.getResponseTime();
            responsetransactionstatus = responseVO.getStatus();
            // responsedescriptor = responseVO.getDescriptor();


            if (functions.isValueNull(responseVO.getStatus()) && "success".equalsIgnoreCase(responseVO.getStatus()))
            {
                responsedescriptor = responseVO.getDescriptor();
            }
            logger.debug(" Billing Descriptor in ActionEntry---"+responsedescription);
            transactionLogger.debug(" Billing Descriptor in ActionEntry---"+responsedescription);


            transType = responseVO.getTransactionType();
            responsehashinfo = responseVO.getResponseHashInfo();
            remark = responseVO.getRemark();
            logger.debug("ipaddress from VT 3 "+ipaddress);
        }

        if (requestVO != null && responseVO==null)
        {
            if(requestVO.getAddressDetailsVO().getCardHolderIpAddress() != null)
            {
                ipaddress = requestVO.getAddressDetailsVO().getCardHolderIpAddress();
            }
            if(functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_amount())){
                tmpl_Amount=requestVO.getAddressDetailsVO().getTmpl_amount();
            }
            if(functions.isValueNull(requestVO.getAddressDetailsVO().getTmpl_currency())){
                tmp_Currency=requestVO.getAddressDetailsVO().getTmpl_currency();
            }
            CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
            if(commTransactionDetailsVO!=null)
            {
                responsetransactionid = commTransactionDetailsVO.getResponseHashInfo();

                if(functions.isValueNull(commTransactionDetailsVO.getCurrency())){
                    currency=commTransactionDetailsVO.getCurrency();
                }
            }
            transactionLogger.debug("Currency----"+currency+"----tmpl_Amount----"+tmpl_Amount+"----tmpl_Currency---"+tmp_Currency);
            CommMerchantVO commMerchantVO=requestVO.getCommMerchantVO();
            if(commMerchantVO!=null)
            {
                transactionLogger.debug("isService Flag----"+commMerchantVO.getIsService());
                if("N".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.AUTH.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
                else if("Y".equalsIgnoreCase(commMerchantVO.getIsService()))
                {
                    transType= PZProcessType.SALE.toString();
                    transactionLogger.debug("transType-----"+transType);
                }
            }
            consentStmnt=requestVO.getConsentStmnt();
        }

        if(auditTrailVO != null)
        {
            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        try
        {   logger.debug("ipaddress from VT 4 "+ipaddress);
            transactionLogger.debug("ActionEntry.actionEntryForCommon ::::::::: DB Call");
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,consentStmnt,currency,templateamount,templatecurrency) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, responsetransactionid);
            pstmt.setString(7, responsetransactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16,actionExname);
            pstmt.setString(17,consentStmnt);
            pstmt.setString(18,currency);
            pstmt.setString(19,tmpl_Amount);
            pstmt.setString(20,tmp_Currency);
            results = pstmt.executeUpdate();

            if (results == 1)
            {
                rs = pstmt.getGeneratedKeys();

                if (rs != null)
                {
                    while (rs.next())
                    {

                        detailid = rs.getInt(1);
                    }
                }
            }

            //@TODO : Gateways specific details entry using the gateway object

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("ActionEntry.java","actionEntryForCommon()",null,"common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(cn);
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);

        }
        return detailid;
    }

    public Hashtable getExportActionHistoryByTrackingIdAndGatewayCP(String trackingid, String gatewayType) throws SystemError
    {
        logger.debug("Entering getExportActionHistoryByTrackingIdAndGatewayCP");
        String fields = "";
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        String tableName = Database.getTableName(gatewayType);
        Functions functions = new Functions();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            if (tableName.equals("transaction_icicicredit"))
            {
                query.append("select icicitransid,amount,action,status,timestamp from action_history");
                if (functions.isValueNull(trackingid))
                {
                    query.append(" and icicitransid IN (" + trackingid + ")");
                }
            }
            else if (tableName.equals("transaction_common"))
            {
                query.append("select trackingid ,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetransactionid,ipAddress,responsetime as ResponseTime from ");
                query.append(tableName);
                query.append("_details_card_present");
                if (functions.isValueNull(trackingid))
                {
                    query.append(" where trackingid IN (" + trackingid + ")");
                }
                query.append(" ORDER BY trackingid DESC");
            }
            else
            {
                query.append("select parentid,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark, errorName,responsetime as ResponseTime from ");
                query.append(tableName);
                query.append("_details_card_present");
                if (functions.isValueNull(trackingid))
                {
                    query.append(" where parentid IN (" + trackingid + ")");
                }
                query.append(" ORDER BY trackingid DESC");
            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            logger.error("query----"+query);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving getExportActionHistoryByTrackingIdAndGatewayCP ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;

    }
    public Hashtable getExportActionHistoryByTrackingIdAndGateway(String trackingid, String gatewayType) throws SystemError
    {
        logger.debug("Entering getExportActionHistoryByTrackingIdAndGateway");
        String fields = "";
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        String tableName = Database.getTableName(gatewayType);
        Functions functions = new Functions();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();

                if (tableName.equals("transaction_icicicredit"))
                {
                    query.append("select icicitransid,amount,action,status,timestamp from action_history where icicitransid>0");
                    if (functions.isValueNull(trackingid))
                    {
                        query.append(" and icicitransid IN (" + trackingid + ")");
                    }
                }
                else if (tableName.equals("transaction_common"))
                {
                    query.append("select trackingid ,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetransactionid,responsetime as ResponseTime,ipAddress,transactionReceiptImg from ");
                    query.append(tableName+"_details");
                    if (functions.isValueNull(trackingid))
                    {
                        query.append(" where trackingid IN (" + trackingid + ")");
                    }
                    query.append(" ORDER BY trackingid DESC");
                }
                else
                {
                    query.append("select parentid,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark,responseDateTime as ResponseTime,errorName from ");
                    query.append(tableName+"_details");
                    if (functions.isValueNull(trackingid))
                    {
                        query.append(" where parentid IN (" + trackingid + ")");
                    }
                    query.append(" ORDER BY trackingid DESC");
                }
                StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
                logger.error("query----"+query);
                hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
                ResultSet rs = Database.executeQuery(countquery.toString(), conn);
                rs.next();
                int totalrecords = rs.getInt(1);
                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                if (totalrecords > 0)
                    hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving getExportActionHistoryByTrackingIdAndGateway ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;

    }
    public void closeConnection()
    {
        //Database.closeConnection(cn);
    }

    public Hashtable getStatusHash()
    {
        return statusHash;
    }



    public Hashtable getExportActionHistoryByTrackingId(String trackingid) throws SystemError
    {
        logger.debug("Entering getExportActionHistoryByTrackingIdAndGateway");
        String fields = "";
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        //String tableName = Database.getTableName();
       String tableName="transaction_common";
        Functions functions = new Functions();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();

            if (tableName.equals("transaction_common"))
            {
                query.append("select trackingid ,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responsedescriptor as responsedescriptor,responsecode as responsecode,remark,responsedescription,responsehashinfo,arn,responsetime as ResponseTime,ipAddress,transactionReceiptImg,rrn from ");
                query.append(tableName+"_details");
                if (functions.isValueNull(trackingid))
                {
                    query.append(" where trackingid IN (" + trackingid + ")");
                }
                query.append(" ORDER BY trackingid DESC");
            }

            else
            {
                query.append("select parentid,amount,currency,templatecurrency,templateamount,walletAmount,walletCurrency,action,status,timestamp,actionexecutorid,actionexecutorname,responseBillingDescription as responsedescriptor,operationCode as responsecode,responseRemark as remark,responseDateTime as ResponseTime,errorName from ");
                query.append(tableName+"_details");
                if (functions.isValueNull(trackingid))
                {
                    query.append(" where parentid IN (" + trackingid + ")");
                }
                query.append(" ORDER BY trackingid DESC");
            }
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");
            logger.error("query for getExportActionHistoryByTrackingId----"+query);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving getExportActionHistoryByTrackingIdAndGateway ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;

    }
}