package payment;

import com.directi.pg.*;
import com.payment.Mail.AsynchronousMailService;
import com.payment.common.core.CommResponseVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 8/9/14
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class InPayBackEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(InPayBackEndServlet.class.getName());

    public InPayBackEndServlet()
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
        transactionLogger.debug("-----Inside InPayBackEndServlet------");

        String currency = "";
        String captureAmount = "";
        String paymentId = "";
        String checkSum = "";
        String amount = "";
        String trackingId = "";
        String resTime = "";
        String status = "";
        String toid = "";
        String displayName = "";
        String sStatus = "";

        ResultSet rs = null;
        Connection con = null;
        PreparedStatement p = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();

        for (Object key : req.getParameterMap().keySet())
        {
            currency = ESAPI.encoder().encodeForSQL(me, req.getParameter("invoice_currency"));
            captureAmount = req.getParameter("received_sum");
            paymentId = ESAPI.encoder().encodeForSQL(me, req.getParameter("invoice_reference"));
            checkSum = ESAPI.encoder().encodeForSQL(me, req.getParameter("checksum"));
            amount = req.getParameter("invoice_amount");
            trackingId = ESAPI.encoder().encodeForSQL(me, req.getParameter("order_id"));
            transactionLogger.debug("order_id------" + req.getParameter("order_id"));
            resTime = ESAPI.encoder().encodeForSQL(me, req.getParameter("invoice_created_at"));
            status = ESAPI.encoder().encodeForSQL(me, req.getParameter("invoice_status"));

            transactionLogger.debug("---------" + key + "=" + req.getParameter((String) key) + "--------------");
        }
        if (functions.isValueNull(trackingId))
        {

            try
            {
                con = Database.getConnection();
                String sql = "select toid, description, status, redirecturl, t.accountid, orderdescription, currency, displayname, amount from transaction_common AS t,gateway_accounts AS g where trackingid = ? AND t.accountid=g.accountid ";
                p = con.prepareStatement(sql);
                p.setString(1, trackingId);
                rs = p.executeQuery();
                while (rs.next())
                {
                    toid = rs.getString("toid");
                    displayName = rs.getString("displayname");
                    sStatus = rs.getString("status");
                }
                transactionLogger.debug("query---" + p);

                CommResponseVO commResponseVO = new CommResponseVO();
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();

                commResponseVO.setDescription(status);
                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setDescriptor(displayName);
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(commResponseVO.getMerchantId());
                auditTrailVO.setActionExecutorName("Customer");
                transactionLogger.debug("query---" + commResponseVO.getDescriptor());

                ActionEntry entry = new ActionEntry();
                StringBuffer sb = new StringBuffer();

                sb.append("update transaction_common set ");

                if ("pending".equals(status))
                {
                    commResponseVO.setStatus("pending");
                    sb.append(" amount='" + amount + "'");
                    sb.append(", status='authstarted'");
                    if (!sStatus.equalsIgnoreCase("authstarted"))
                    {
                        entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, auditTrailVO, null);
                    }
                    transactionLogger.debug("p.status---" + status);
                }
                else if ("approved".equals(status))
                {
                    transactionLogger.debug("display name in approve state-----" + displayName);
                    commResponseVO.setDescriptor(displayName);
                    commResponseVO.setStatus("success");
                    sb.append(" captureamount='" + captureAmount + "'");
                    sb.append(", status='capturesuccess'");
                    entry.actionEntryForCommon(trackingId, captureAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    transactionLogger.debug("a.status---" + status);
                    transactionLogger.debug("success query---" + sb.toString());
                }
                sb.append(" where trackingid = '" + trackingId + "'");
                transactionLogger.debug("inpay query---" + sb.toString());
                int result = Database.executeUpdate(sb.toString(), con);
                if ("approved".equals(status) || "create".equals(status))
                {
                    if (result != 1)
                    {
                        Database.rollback(con);
                        transactionLogger.debug("Leaving do service with error in result, result is " + result + " and not 1");
                        //Mail.sendAdminMail("Exception while Updating status", "\r\n\r\nException has occured while updating status for tracking id=" + trackingId + "\r\nAuth message=" + status);
                        asynchronousMailService.sendAdminMail("Exception while Updating status", "\r\n\r\nException has occured while updating status for tracking id=" + trackingId + "\r\nAuth message=" + status);
                    }
                }
                //}
            }
            catch (Exception se)
            {
                transactionLogger.error("Exception in InPayBackendServlet---", se);
            }
            finally
            {
                Database.closePreparedStatement(p);
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }
        }
    }
}