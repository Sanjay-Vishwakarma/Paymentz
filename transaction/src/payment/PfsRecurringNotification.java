package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.manager.PaymentManager;
import com.manager.RecurringManager;
import com.manager.dao.RecurringDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/5/15
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PfsRecurringNotification extends PzServlet
{
    public PfsRecurringNotification()
    {
        super();
    }
    private static Logger log = new Logger(PfsRecurringNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PfsRecurringNotification.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setContentType("text/html");
        log.debug("-------Inside PfsRecurringNotification-----");
        transactionLogger.debug("-------Inside PfsRecurringNotification-----");
        log.debug("PfsRecurringNotification FROM IP="+req.getRemoteAddr());
        log.debug("PfsRecurringNotification forwarded-for---"+req.getHeader("X-Forwarded-For"));
        log.debug("PfsRecurringNotification forwarded-host---"+req.getHeader("X-Forwarded-Host"));

        //todo : ip check add
        //todo : authstarted transaction in transaction_common and transaction_common_details
        //todo : check real time transaction status from response and update accordingly in transaction_common and transaction_common_details
        //todo : update accordingly in recurring detail table
        //todo : send mail for recurring

        TransactionDAO transactionDAO = new TransactionDAO();
        TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
        PaymentManager paymentManager = new PaymentManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new Comm3DResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        RecurringManager recurringManager = new RecurringManager();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
        RecurringDAO recurringDAO = new RecurringDAO();

        Connection con = null;

        String data = req.getParameter("data");
        log.error("---Pfs Recurring Notification XML---"+data);
        transactionLogger.error("---Pfs Recurring Notification XML---"+data);
        String transactionStatus = "";
        try
        {
        Map<String, String> responseMap = PfsPaymentGateway.getResponseMap(data);

        commResponseVO.setErrorCode(responseMap.get("R1"));
        commResponseVO.setDescription(responseMap.get("R2"));
        commResponseVO.setTransactionStatus(responseMap.get("R4"));
        commResponseVO.setTransactionType("Recurring Transaction");
        commResponseVO.setTransactionId(responseMap.get("R6"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        String trackingid = responseMap.get("R5");
        int newTrackingid = 0;
            con = Database.getConnection();
            transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingid);
            newTrackingid = paymentManager.insertAuthStartedTransactionEntryForRecurringBilling(transactionDetailsVO,null,"transaction_common");

            if(responseMap != null && !responseMap.equals(""))
            {
                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set ");
                if(responseMap.get("R1").equals("0000"))
                {
                    transactionStatus = "capturesuccess";
                    sb.append(" captureamount='" + transactionDetailsVO.getAmount() + "'");
                    sb.append(", status='"+transactionStatus+"'");
                    commResponseVO.setRemark("Recurring Transaction Successful");
                    entry.actionEntryForCommon(String.valueOf(newTrackingid), transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                    recurringBillingVO.setTransactionStatus(ActionEntry.STATUS_CAPTURE_SUCCESSFUL);//R4
                    log.debug("update from R1=0000");
                    transactionLogger.debug("update from R1=0000");
                }
                else
                {
                    transactionStatus = "authfailed";
                    sb.append("status='"+transactionStatus+"'");
                    commResponseVO.setRemark("Recurring Transaction Failed");
                    entry.actionEntryForCommon(String.valueOf(newTrackingid), transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO,auditTrailVO,null);
                    recurringBillingVO.setTransactionStatus(ActionEntry.STATUS_AUTHORISTION_FAILED);//R4
                    log.debug("update from fail block");
                    transactionLogger.debug("update from fail block");
                }

                sb.append(" where trackingid = "+newTrackingid);
                log.debug("common update query in PfsBackendServlet---"+sb.toString());
                transactionLogger.debug("common update query in PfsBackendServlet---"+sb.toString());
                Database.executeUpdate(sb.toString(), con);

                recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingid);

                recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                recurringBillingVO.setParentBankTransactionID("");//response idi.e messageid
                recurringBillingVO.setBankRecurringBillingID(responseMap.get("R3"));//197
                recurringBillingVO.setNewBankTransactionID(responseMap.get("R6"));//R6
                recurringBillingVO.setParentPzTransactionID(responseMap.get("R5"));//R5
                recurringBillingVO.setAmount(transactionDetailsVO.getAmount());
                recurringBillingVO.setDescription(responseMap.get("R3"));//R3
                recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingid));//newTrackingid
                recurringBillingVO.setRecurringRunDate(String.valueOf(dateFormat.format(date)));

                recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                log.debug("At the end of PfsRecurringNotification======");
                String display = "";
                if(transactionStatus.contains("Declined") || transactionStatus.contains("Failed")|| transactionStatus.contains("fail")){
                    display ="style=\"display:none;\"";
                }
                transactionLogger.debug("display:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + display);

                HashMap mailData=new HashMap();
                mailData.put(MailPlaceHolder.TOID,transactionDetailsVO.getToid());
                mailData.put(MailPlaceHolder.CustomerEmail,transactionDetailsVO.getEmailaddr());
                mailData.put(MailPlaceHolder.TRACKINGID,newTrackingid);
                mailData.put(MailPlaceHolder.IPADDRESS,transactionDetailsVO.getIpAddress());
                mailData.put(MailPlaceHolder.ORDERDESCRIPTION,transactionDetailsVO.getOrderDescription());
                mailData.put(MailPlaceHolder.DESC,transactionDetailsVO.getOrderDescription());
                mailData.put(MailPlaceHolder.AMOUNT,transactionDetailsVO.getAmount());
                mailData.put(MailPlaceHolder.CURRENCY,transactionDetailsVO.getCurrency());
                mailData.put(MailPlaceHolder.DISPLAYNAME, GatewayAccountService.getGatewayAccount(transactionDetailsVO.getAccountId()).getDisplayName());
                mailData.put(MailPlaceHolder.STATUS,transactionStatus);
                mailData.put(MailPlaceHolder.NAME,transactionDetailsVO.getFirstName()+" "+transactionDetailsVO.getLastName());
                mailData.put(MailPlaceHolder.PARTNERID,transactionDetailsVO.getTotype());
                mailData.put(MailPlaceHolder.DISPLAYTR, display);
                MailService mailService=new MailService();
                mailService.sendMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION,mailData);
            }
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("Error Message While updating db---"+e.getMessage());
            log.error("DB Updation fails in PfsRecurringNotification---",e);
            transactionLogger.error("DB Updation fails in PfsRecurringNotification---",e);
            PZExceptionHandler.handleTechicalCVEException(e,transactionDetailsVO.getToid(),"Recurring Billing Notification");
        }
        catch (PZDBViolationException db)
        {
            log.error("Error Message---"+db.getMessage());
            log.error("DB Exception in PfsRecurringNotification---",db);
            transactionLogger.error("DB Exception in PfsRecurringNotification---",db);
            PZExceptionHandler.handleDBCVEException(db, transactionDetailsVO.getToid(), "Recurring Billing Notification");
        }
        catch (SystemError se)
        {
            log.error("Error Message While updating db---"+se.getMessage());
            log.error("DB Updation fails in PfsRecurringNotification---",se);
            transactionLogger.error("DB Updation fails in PfsRecurringNotification---", se);
            PZExceptionHandler.raiseAndHandleDBViolationException(PfsRecurringNotification.class.getName(),"doService()",null,"common","System Error:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),transactionDetailsVO.getToid(),"Recurring Billing Notification");
        }
        finally
        {
            Database.closeConnection(con);
        }

    }
}