package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
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
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Admin on 6/8/2021.
 */
public class QikPayBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(QikPayBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering QikPayBackEndServlet ......",e);
        }
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering QikPayBackEndServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("Entering QikPayBackEndServlet ......");


        HttpSession session = req.getSession();
        Functions functions = new Functions();
        //PayGateCryptoUtils payGateCryptoUtils = new PayGateCryptoUtils();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        PaymentManager paymentManager = new PaymentManager();
        TransactionUtility transactionUtility = new TransactionUtility();

        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String clkey = "";
        String checksumAlgo = "";
        String checksum = "";
        String autoredirect = "";
        String isService = "";
        String displayName = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";

        String amount = "";
        String trackingId = "";
        String status = "";
        String remark = "";

        String bankTransactionStatus = "";
        String resultCode = "";
        String email = "";

        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String transactionStatus = "";
        String confirmStatus = "";
        String responseStatus = "";
        String transactionId = "";
        String message = "";
        String billingDesc = "";
        String dbStatus = "";
        String eci = "";
        String paymentid = "";
        String errorCode = "";
        String name = "";
        String notificationUrl = "";
        String ccnum = "";
        String expMonth = "";
        String expYear = "";
        String requestIp = "";
        String merchantKey = "";
        String MOP_TYPE="";
        String txtdesc="";


        String RESPONSE_DATE_TIME ="";
        String RESPONSE_CODE ="";
        String AMOUNT ="";
        String TXN_ID ="";
        String TXNTYPE ="";
        String CURRENCY_CODE ="";
        String STATUS ="";
        String CLIENT_ID ="";
        String CUST_EMAIL ="";
        String CUST_NAME ="";
        String RRN ="";
        String ORDER_ID ="";
        String MSG ="";
        String CUST_VPA ="";
        String MERCHANT_PAYMENT_TYPE ="";
        String notificationAmount ="";
        BufferedReader br = req.getReader();
        String cardType="";
        String restatus="";
        String responseAmount="";
        String toId="";
        String custIp="";
        String updatedStatus="";
        String str;
        Enumeration enumeration = req.getParameterNames();
        StringBuilder responseMsg = new StringBuilder();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }

        transactionlogger.error("-----Qikpaybackendservlet response-----" + responseMsg);
        try
        {

            if(functions.isValueNull(req.getParameter("STATUS")))
            {
                STATUS = req.getParameter("STATUS");
            }
            if(functions.isValueNull(req.getParameter("CLIENT_ID")))
            {
                trackingId = req.getParameter("CLIENT_ID");
            }
            if(functions.isValueNull(req.getParameter("AMOUNT")))
            {
                AMOUNT = req.getParameter("AMOUNT");
            }
            if(functions.isValueNull(req.getParameter("RRN")))
            {
                RRN = req.getParameter("RRN");
            }
            if(functions.isValueNull(req.getParameter("MSG")))
            {
                message = req.getParameter("MSG");
            }




            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                cardType = transactionDetailsVO.getCardtype();
                toId = transactionDetailsVO.getToid();
                dbStatus = transactionDetailsVO.getStatus();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                custId = transactionDetailsVO.getCustomerId();
                custIp = transactionDetailsVO.getCustomerIp();
                email = transactionDetailsVO.getEmailaddr();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));

                auditTrailVO.setActionExecutorName("QPbackendservlet");
                auditTrailVO.setActionExecutorId(toId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());


                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                transactionlogger.error("DbStatus------>" + dbStatus);
                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();
                if (PZTransactionStatus.PAYOUT_SUCCESS.toString().equalsIgnoreCase(dbStatus)&&!"SUCCESS".equalsIgnoreCase(STATUS))
                {

                    transactionlogger.error("inside if ------" );
                    con = Database.getConnection();

                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getCurrency());
                   /* if(functions.isValueNull(AMOUNT))
                    {
                        Double compRsAmount= Double.valueOf(AMOUNT);
                        Double compDbAmount= Double.valueOf(amount);
                        transactionlogger.error("QikPay backend response amount --->"+compRsAmount);
                        transactionlogger.error("QikPay backend DB Amount--->"+compDbAmount);

                   if(compDbAmount.equals(compRsAmount)){*/
                        StringBuffer dbBuffer = new StringBuffer();

                   if ("REFUNDED".equalsIgnoreCase(STATUS))
                    {
                        restatus="Failed";
                        //   message = txnStatus;
                        commResponseVO.setTransactionId(RRN);
                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(message);
                        dbBuffer.append("update transaction_common set status='payoutfailed',paymentid='" + RRN + "' , payoutamount='',remark='" + message + "'");

                        dbBuffer.append(" where trackingid = " + trackingId);

                        transactionlogger.error("dbBuffer->" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                   else
                   {
                       restatus = "pending";
                       message = "pending";

                   }

                    if (functions.isValueNull(updatedStatus) && functions.isValueNull(notificationUrl))
                    {
                        transactionlogger.error("Inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, restatus, message, "");
                    }
                }
            }

            res.setStatus(200);
            return;
        }

        catch (Exception ex)
        {
            transactionlogger.error("Exception---->",ex);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

}

