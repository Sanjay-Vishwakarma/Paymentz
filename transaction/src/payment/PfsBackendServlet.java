package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.manager.RecurringManager;
import com.manager.dao.TransactionDAO;
import com.manager.vo.BinDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 24, 2012
 * Time: 12:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class PfsBackendServlet extends PzServlet
{
    public PfsBackendServlet()
    {
        super();
    }
    private static Logger log = new Logger(PfsBackendServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PfsBackendServlet.class.getName());
    //Connection con = null;
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
        log.debug("PROCESSING PFS BACKEND NOTIFICATION FROM IP="+req.getRemoteAddr());
        log.debug("PfsBackend forwarded-for---"+req.getHeader("X-Forwarded-For"));
        log.debug("PfsBackend forwarded-host---"+req.getHeader("X-Forwarded-Host"));

        String data = req.getParameter("data");
        log.error("Pfs Backend response XML---"+data);
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String toid =  "";
        try
        {
        Map<String, String> responseMap = PfsPaymentGateway.getResponseMap(data);
        String trackingId = responseMap.get("R3");
        String status = "";
        String failedStatus = "";
        String accountid = "";
        String isService = "";
        String amount = "";
        String paymentid = "";
        log.error("response map---"+responseMap.toString());

            connection = Database.getConnection();
            String sql = "SELECT STATUS,tc.accountid,tc.toid,isService,amount,paymentid FROM transaction_common AS tc, members AS m WHERE trackingid=? AND tc.toid=m.memberid";
             ps = connection.prepareStatement(sql);
            ps.setString(1, trackingId);
            resultSet = ps.executeQuery();
            if(resultSet.next())
            {
                accountid = resultSet.getString("accountid");
                status = resultSet.getString("status");
                isService = resultSet.getString("isService");
                amount = resultSet.getString("amount");
                paymentid = resultSet.getString("paymentid");
                toid = resultSet.getString("toid");
            }

            ActionEntry entry = new ActionEntry();
            AuditTrailVO auditTrailVO=new AuditTrailVO();
            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountid));
            String pgtypeid = account.getPgTypeId();
            GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);

            CommResponseVO commResponseVO = new Comm3DResponseVO();
            commResponseVO.setTransactionId(paymentid);
            commResponseVO.setTransactionStatus(responseMap.get("R2"));
            commResponseVO.setErrorCode(responseMap.get("R1"));
            commResponseVO.setRemark(responseMap.get("R4"));

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            String bankIP = gatewayType.getBank_ipaddress();
            if(req.getHeader("X-Forwarded-For").equals(bankIP))
            {
                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set ");
                if (responseMap.get("R1")!=null && (responseMap.get("R1").equals("0000")))
                {
                    if("authstarted".equalsIgnoreCase(status))
                    {
                        if("Y".equalsIgnoreCase(isService))
                        {
                            sb.append(" captureamount='" + amount + "'");
                            sb.append(", status='capturesuccess'");
                            //entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                            log.debug("update from auth success if block isService--Y");
                        }
                        else
                        {
                            //sb.append(" captureamount='" + amount + "'");
                            sb.append("status='authsuccessful'");
                            commResponseVO.setTransactionType(responseMap.get("R10"));
                            //entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                            log.debug("update from auth success block isService--N");
                        }
                    }
                    else if("capturestarted".equalsIgnoreCase(status))
                    {
                        sb.append(" captureamount='" + amount + "'");
                        sb.append(", status='capturesuccess'");
                        commResponseVO.setTransactionType("Capture");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                        log.debug("update from capturestarted block isService--N");
                    }
                    else if("N".equalsIgnoreCase(isService) && "cancelstarted".equalsIgnoreCase(status))
                    {
                        sb.append("status='cancelled'");
                        commResponseVO.setTransactionType("Cancel");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_CANCLLED, ActionEntry.STATUS_AUTHORISTION_CANCLLED, commResponseVO,auditTrailVO,null);
                        log.debug("update from cancelstarted block isService--N");
                    }
                    else if("markedforreversal".equalsIgnoreCase(status))
                    {
                        sb.append(" refundamount='" + amount + "'");
                        sb.append(", status='reversed'");
                        commResponseVO.setTransactionType("Refund");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                        log.debug("update from markedforreversal block isService--N");
                    }
                }
                else
                {
                    if("authstarted".equalsIgnoreCase(status))
                    {
                        //sb.append(" captureamount='" + amount + "'");
                        sb.append("status='authfailed'");
                        //entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO,auditTrailVO,null);
                        log.debug("update from else auth fail block ");
                    }
                    else if("capturestarted".equalsIgnoreCase(status))
                    {
                        sb.append("status='capturefailed'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, commResponseVO,auditTrailVO,null);
                        log.debug("update from else capturestarted block isService--N");
                    }
                    else if("N".equalsIgnoreCase(isService) && "cancelstarted".equalsIgnoreCase(status))
                    {
                        sb.append("status='cancelstarted'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CANCEL_STARTED, ActionEntry.STATUS_CANCEL_STARTED, commResponseVO,auditTrailVO,null);
                        log.debug("update from else cancelstarted isService--N");
                    }
                    else if("markedforreversal".equalsIgnoreCase(status))
                    {
                        sb.append("status='markedforreversal'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_REVERSAL_REQUEST_SENT, ActionEntry.STATUS_REVERSAL_REQUEST_SENT, commResponseVO,auditTrailVO,null);
                        log.debug("update from markedforreversal block isService--N");
                    }
                    else
                    {
                        sb.append("status='authfailed'");
                        log.debug("update from else auth fail block ");
                    }
                }

                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, null,null);

                sb.append(" where trackingid = "+trackingId);
                log.debug("common update query in PfsBackendServlet---"+sb.toString());

                if(responseMap.get("RBID")!=null && !responseMap.get("RBID").equals(""))
                {
                    RecurringManager recurringManager = new RecurringManager();
                    RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

                    BinDetailsVO binDetailsVO = new BinDetailsVO();
                    TransactionDAO transactionDAO = new TransactionDAO();
                    binDetailsVO = transactionDAO.getBinDetails(trackingId);
                    StringBuffer rb = new StringBuffer();
                    rb.append("update recurring_transaction_subscription set first_six = "+binDetailsVO.getFirst_six()+", last_four = "+binDetailsVO.getLast_four()+", rbid = "+responseMap.get("RBID")+" where originatingTrackingid="+trackingId);
                    log.debug("RB Query---"+rb.toString());
                    Database.executeUpdate(rb.toString(), connection);

                    //Insert first transaction in recurring_transaction_details, registered first time

                    recurringBillingVO.setNewPzTransactionID(trackingId);
                    recurringBillingVO.setAmount(amount);
                    recurringBillingVO.setTransactionStatus(ActionEntry.STATUS_CAPTURE_SUCCESSFUL);

                    recurringManager.insertRecurringDetailsEntry(recurringBillingVO);

                }
                Database.executeUpdate(sb.toString(),connection);
            }
        }
        catch(PZDBViolationException dbe)
        {
            log.error("PZDBViolationException in PfsBackendServlet---",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,toid,"PfsBackendNotification Call");
        }
        catch(SystemError e)
        {
            log.error("Exception while connection to Database:::",e);
        }
        catch(SQLException se)
        {
            log.error("SQLException in PfsBackendServlet:::",se);
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException in PfsBackendServlet---", e);
            PZExceptionHandler.handleTechicalCVEException(e, toid, "PfsBackendNotification Call");
        }
        finally {
            Database.closePreparedStatement(ps);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }
    }
}
