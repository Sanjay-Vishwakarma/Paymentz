package payment;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.decta.core.DectaUtils;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

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

/**
 * Created by Trupti on 5/19/2017.
 */
public class DCBackendNotification extends PzServlet
{
    private static Logger log = new Logger(DCBackendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DCBackendNotification.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public DCBackendNotification()
    {
        super();
    }

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
        transactionLogger.error("-------Enter in doService of DCBackendNotification-------");

        for (Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop DCBackendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop DCBackendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        TransactionManager transactionManager = new TransactionManager();
        CommResponseVO commResponseVO = new CommResponseVO();
        DectaUtils dectaUtils = new DectaUtils();
        Functions functions = new Functions();

        String trackingid = req.getParameter("MerchantID");
        String resultCode = req.getParameter("ResultCode");
        String paymentId = req.getParameter("ID");
        String toid = "";
        String isService = "";
        String amount = "";
        String respStatus = "";
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement p = null;
        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
            toid = transactionDetailsVO.getToid();
            amount = transactionDetailsVO.getAmount();

            con = Database.getConnection();
            String query = "select isService FROM members WHERE memberid=?";
            p = con.prepareStatement(query);
            p.setString(1, toid);
            rs = p.executeQuery();
            rs = p.executeQuery();
            if (rs.next())
            {
                isService = rs.getString("isService");
            }
            respStatus = dectaUtils.getResultDescriptionFromCode(resultCode);
            log.error("DectaBackendNotification Status Before Condition---" + transactionDetailsVO.getStatus());

            if (transactionDetailsVO != null && transactionDetailsVO.getStatus().equals(PZTransactionStatus.AUTH_STARTED.toString()))
            {
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("DCBackend");

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set ");
                if (resultCode.equals("000"))
                {
                    if("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                    {
                        transactionLogger.error("---inside isService Y---CaptureSuccess");
                        if(!functions.isValueNull(respStatus))
                            respStatus = "Successful Transaction";

                        commResponseVO.setDescription(respStatus);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setTransactionType("sale");
                        commResponseVO.setRemark(respStatus);
                        commResponseVO.setErrorCode(resultCode);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                        sb.append(" captureamount='" + amount + "'");
                        sb.append(", paymentid='" + paymentId + "'");
                        sb.append(", remark='" + respStatus + "'");
                        sb.append(", status='capturesuccess'");
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    }
                    else
                    {
                        transactionLogger.error("---inside isService N---AuthFailed");
                        if(!functions.isValueNull(respStatus))
                            respStatus = "Authorization Successful";

                        commResponseVO.setDescription(respStatus);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(paymentId);
                        commResponseVO.setTransactionType("auth");
                        commResponseVO.setRemark(respStatus);
                        commResponseVO.setErrorCode(resultCode);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                        sb.append(" amount='" + amount + "'");
                        sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                        sb.append(", remark='" + respStatus + "'");
                        sb.append(", status='authsuccessful'");
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    }
                }
                else
                {
                    transactionLogger.error("---inside Failed---");
                    if(!functions.isValueNull(respStatus))
                        respStatus = "Transaction Declined";
                    commResponseVO.setDescription(respStatus);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setRemark(respStatus);
                    commResponseVO.setErrorCode(resultCode);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    sb.append(" amount='" + amount + "'");
                    sb.append(", paymentid='" + paymentId + "'");
                    sb.append(", remark='" + respStatus + "'");
                    sb.append(", status='authfailed'");
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.ACTION_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                }
                sb.append(" where trackingid = " + trackingid);
                log.debug("common update query DectaBackendNotification---" + sb.toString());
                Database.executeUpdate(sb.toString(), con);
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---",se);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---",se);
        }
        catch (PZDBViolationException se)
        {
            transactionLogger.error("PZDBViolationException---",se);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }

    }
}