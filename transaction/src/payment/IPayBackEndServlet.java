package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.log4j.LogManager;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;


/**
 * Created by Admin on 10/16/2020.
 */
public class IPayBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(IPayBackEndServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {   doService(req, res);

    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        doService(req,res);
    }


    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        transactionlogger.error("Entering IPayBackEndServlet ......");


        HttpSession session         = req.getSession();
        Functions functions         = new Functions();
        ActionEntry entry           = new ActionEntry();
        AuditTrailVO auditTrailVO   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO         = new MerchantDAO();
        CommResponseVO commResponseVO   = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        PaymentManager paymentManager   = new PaymentManager();
        CommRequestVO requestVO         = new CommRequestVO();
        TransactionUtility transactionUtility       = new TransactionUtility();
        CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        String toid         = "";
        String description  = "";
        String redirectUrl  = "";
        String accountId    = "";
        String orderDesc    = "";
        String currency     = "";
        String autoredirect = "";
        String isService    = "";
        String displayName  = "";
        String isPowerBy    = "";
        String logoName     = "";
        String partnerName  = "";

        String amount       = "";
        String trackingId   = "";
        String status = "";
        String remark = "";

        String bankTransactionStatus = "";
        String resultCode = "";
        String email = "";

        String tmpl_amt         = "";
        String tmpl_currency    = "";
        String firstName    = "";
        String lastName     = "";
        String paymodeid    = "";
        String cardtypeid   = "";
        String custId       = "";
        String transactionStatus    = "";
        String confirmStatus        = "";
        String responseStatus   = "";
        String transactionId    = "";
        String message          = "";
        String billingDesc      = "";
        String dbStatus         = "";
        String eci              = "";
        String paymentid        = "";
        String errorCode        = "";
        String name             = "";
        String notificationUrl  = "";
        String ccnum        = "";
        String expMonth     = "";
        String expYear      = "";
        String requestIp    = "";
        String merchantKey  = "";
        String paymentId    = "";
        String autoRedirect     = "";
        String updatedStatus    = "";
        String responseAmount   = "";
        String RESPONSE_CODE    = "";



        StringBuilder responseMsg   = new StringBuilder();
        BufferedReader br           = req.getReader();
        Enumeration enumeration     = req.getParameterNames();
        String updated = "";
        String CID     = "";
        String refId   = "";
        String refIdPartner = "";
        String gateway      = "";
        String gwd          = "";
        String reqAmount    = "";
        String resamount       = "";
        String fee          = "";
        String netAmount    = "";
        String resStatus    = "";
        String err          = "";
        String token        = "";
        StringBuilder responseBackend   = new StringBuilder();
        JSONObject responseObject       = null;
        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
            responseBackend.append(key+"="+value+"&");
        }

        BufferedReader bufferedReader = req.getReader();
        String string;
        while ((string = bufferedReader.readLine()) != null)
        {
            responseMsg.append(string);
        }

        transactionlogger.error("-----IPayBackEndServlet   response--------- " + responseMsg);
//        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();

        try
        {
//            if(functions.isJSONValid(responseMsg.toString()))
            responseObject = new JSONObject(responseMsg.toString());

            if (responseObject.has("refIdPartner"))
            {
                transactionlogger.error("===Inside tracking Id =======");
                trackingId = responseObject.getString("refIdPartner");
            }
            transactionlogger.error("-----IPayBackEndServlet ----- " + trackingId);
            transactionlogger.error("-----Tracking ----- " + trackingId);

            if (responseObject.has("refId") && functions.isValueNull(responseObject.getString("refId")))
            {
                transactionId = responseObject.getString("refId");
            }
            if (responseObject.has("updated") && functions.isValueNull(responseObject.getString("updated")))
            {
                updated = responseObject.getString("updated");
            }
            if (responseObject.has("gateway") && functions.isValueNull(responseObject.getString("gateway")))
            {
                gateway = responseObject.getString("gateway");
            }
            if (responseObject.has("gwd") && functions.isValueNull(responseObject.getString("gwd")))
            {
                gwd = responseObject.getString("gwd");
            }

            if (responseObject.has("reqAmount") && functions.isValueNull(responseObject.getString("reqAmount")))
            {
                reqAmount = responseObject.getString("reqAmount");
                reqAmount = String.format("%.2f", Double.parseDouble(reqAmount));
                transactionlogger.error("amount_requested >>>  " + trackingId + " " + reqAmount);

            }

            if (responseObject.has("amount") && functions.isValueNull(responseObject.getString("amount")))
            {
                resamount = responseObject.getString("amount");
                resamount = String.format("%.2f", Double.parseDouble(resamount));
                transactionlogger.error("amount >>>  " + trackingId + " " + resamount);

            }
            if (responseObject.has("status") && functions.isValueNull(responseObject.getString("status")))
            {
                status = responseObject.getString("status");
            }
            if (responseObject.has("token") && functions.isValueNull(responseObject.getString("token")))
            {
                token = responseObject.getString("token");
            }


            transactionlogger.error("-----IPayBackEndServlet   response-----" + trackingId + "------------- " + responseMsg);

            if (functions.isValueNull(trackingId))
            {

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {
                    trackingId = transactionDetailsVO.getTrackingid();
                    toid = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    accountId = transactionDetailsVO.getAccountId();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    amount = transactionDetailsVO.getAmount();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    if ("Facilero".equalsIgnoreCase(transactionDetailsVO.getTotype()))
                    {
                        facileroLogger.error("InfiPay Response Backend ----> " + trackingId + " " + responseBackend.toString());
                    }
                    else{
                        transactionlogger.error("InfiPay Response Backend ----> " + trackingId + " " + responseBackend.toString());
                    }

                    if (!functions.isValueNull(transactionId))
                    {
                        transactionId = transactionDetailsVO.getPaymentId();
                    }
                    requestIp = Functions.getIpAddress(req);
                    commAddressDetailsVO.setCardHolderIpAddress(requestIp);
                    transactionlogger.error("requestIp --- >" + requestIp);
                    //  transactionlogger.error("notificationUrl ---" + notificationUrl);
                    if (functions.isValueNull(tmpl_amt))
                    {
                        tmpl_amt = transactionDetailsVO.getTemplateamount();
                    }

                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    if (functions.isValueNull(tmpl_currency))
                    {
                        tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    }

                    firstName = transactionDetailsVO.getFirstName();
                    lastName = transactionDetailsVO.getLastName();
                    name = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();


                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    custId = transactionDetailsVO.getCustomerId();
                    transactionlogger.error("trackingId-> " + trackingId + " transactionId ----->" + transactionDetailsVO.getPaymentId());
                    dbStatus = transactionDetailsVO.getStatus();
                    transactionlogger.error("dbStatus--> " + trackingId + " " + dbStatus);

                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    isPowerBy = merchantDetailsVO.getIsPoweredBy();
                    logoName = merchantDetailsVO.getLogoName();
                    isService = merchantDetailsVO.getIsService();
                    partnerName = merchantDetailsVO.getPartnerName();
                    email = transactionDetailsVO.getEmailaddr();

                    auditTrailVO.setActionExecutorName("IPayBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    BlacklistManager blacklistManager = new BlacklistManager();
                    BlacklistVO blacklistVO = new BlacklistVO();

                    transactionlogger.error("accountid " + trackingId + " " + accountId);
                    transactionlogger.error("status " + trackingId + " " + status);
                    // displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                    {
                        transactionStatus = status;
                        transactionlogger.error("inside AUTH_STARTED---");
                        StringBuffer dbBuffer = new StringBuffer();

                        commResponseVO.setTmpl_Amount(tmpl_amt);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setIpaddress(requestIp);
                        commResponseVO.setDescription(status);
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(resamount);
                        commResponseVO.setCurrency(currency);
//                                commResponseVO.setErrorCode(confirmation_number);
//                                commResponseVO.setResponseTime(date_deposited);

                        transactionlogger.error("inside transactionStatus " + transactionStatus);
                        if ("SUCCESS".equalsIgnoreCase(transactionStatus))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            billingDesc = displayName;
                            status = "success";
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setStatus(status);
                            confirmStatus = "Y";
                            responseStatus = "Successful";
                            dbStatus = "capturesuccess";
                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            //dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                            dbBuffer.append("update transaction_common set captureamount='" + resamount + "',status='capturesuccess',paymentid='',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + status + "' where trackingid = " + trackingId);
                            transactionlogger.error("Query for success update is >>>>>>>>>..."+dbBuffer.toString());
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, resamount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);

                        }
                        else if ("PENDING".equalsIgnoreCase(transactionStatus) || "PROCESSING".equalsIgnoreCase(transactionStatus))
                        {
                            status = "pending";
                            dbStatus = "pending";
                            responseStatus = "pending";
                            billingDesc = "";
                            displayName = "";

                            dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);

                        }
                        else if ("FAILED".equalsIgnoreCase(transactionStatus))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            confirmStatus = "N";
                            status = "failed";
                            dbStatus = "authfailed";
                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                            billingDesc = "";
                            displayName = "";
                            //dbBuffer.append("update transaction_common set paymentid='" + commResponseVO.getResponseHashInfo() + "',status='authfailed',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "', remark = '" + message + "'");
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "', remark = '" + status + "'");
                                  /*  if ("11111".equalsIgnoreCase(RESPONSE_CODE))
                                    {
                                        dbBuffer.append(",captureamount='" + amount + "'");
                                    }*/
                            dbBuffer.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, resamount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                            responseStatus = "fail";

                        }
                        else
                        {
                            status = "pending";
                            dbStatus = "pending";
                            message = "Transaction pending";

                        }
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                            /*}*/
                    }
                    else if (PZTransactionStatus.PAYOUT_STARTED.toString().equals(dbStatus) || PZTransactionStatus.PAYOUT_FAILED.toString().equalsIgnoreCase(dbStatus))
                    {
                        transactionlogger.error("Inside payout condition ========================>");
                        StringBuffer dbBuffer = new StringBuffer();
                        transactionStatus = status;
                        if ("SUCCESS".equalsIgnoreCase(transactionStatus))
                        {
                            transactionlogger.error("Inside success condition ========================>");
                            notificationUrl = transactionDetailsVO.getNotificationUrl();

                            responseStatus = "payoutsuccessful";
                            remark = transactionStatus;
                            updatedStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                            StringBuffer sb = new StringBuffer();

                            transactionDetailsVO.setBillingDesc(displayName);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription("payout Successful");
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setResponseHashInfo(transactionId);

                            sb.append("update transaction_common set ");
                            sb.append(" payoutamount='" + resamount + "'");
                            sb.append(", status='payoutsuccessful'");
                            sb.append(" ,remark='" + remark + "' ,paymentid='" + transactionId + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                            transactionlogger.error("payoutquery " + trackingId + " " + sb.toString());
                            con = Database.getConnection();
                            int result = Database.executeUpdate(sb.toString(), con);
                            transactionlogger.error("payoutquery " + trackingId + " " + result);

                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_SUCCESS.toString());

                            if (result != 1)
                            {
                                Database.rollback(con);
                            }

                        }
                        else if ("FAILED".equalsIgnoreCase(transactionStatus))
                        {

                            transactionlogger.error("Inside Failed condition =====================>");
                            transactionStatus = status;

                            responseStatus = "payoutfailed";
                            remark = transactionStatus;
                            updatedStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                            StringBuffer sb = new StringBuffer();

                            notificationUrl = transactionDetailsVO.getNotificationUrl();

                            transactionDetailsVO.setBillingDesc(displayName);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescription("Payout failed");
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());

                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setResponseHashInfo(transactionId);

                            sb.append("update transaction_common set ");
                            // sb.append(" payoutamount='" + OrderAmount + "'");
                            sb.append(" status='payoutfailed'");
                            sb.append(" ,remark='" + remark + "' ,paymentid='" + transactionId + "',payouttimestamp='" + functions.getTimestamp() + "' where trackingid =" + trackingId + "");
                            transactionlogger.error("payoutquery " + trackingId + " " + sb.toString());
                            con = Database.getConnection();
                            int result = Database.executeUpdate(sb.toString(), con);
                            transactionlogger.error("payoutquery " + trackingId + " " + result);

                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.PAYOUT_FAILED.toString());

                            if (result != 1)
                            {
                                Database.rollback(con);
                            }

                        }
                        else
                        {
                            responseStatus = PZResponseStatus.PENDING.toString();
                            updatedStatus = "pending";
                            remark = status;

                        }

                    }
                    else
                    {
                        transactionlogger.error("Inside else >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "failed";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else if (!functions.isValueNull(message))
                                message = "Transaction Failed";
                            dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        else
                        {
                            status = "pending";
                            message = "Transaction pending";
                            dbStatus = PZTransactionStatus.AUTH_STARTED.toString();
                        }
                    }

                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                }
                PrintWriter pWriter = res.getWriter();
                String responseCode = "0";
                String returnResStatus = "OK";
                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode", responseCode);
                jsonResObject.put("responseStatus", returnResStatus);

                res.setStatus(200);
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;
            }
        }


        catch(NullPointerException e)
        {
            transactionlogger.error("NullPointerException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("TPayBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch(Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("TPayBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}
