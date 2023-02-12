package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.CommResponseVO;
import com.payment.emax_high_risk.core.RequestData;
import com.payment.exceptionHandler.PZDBViolationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/13/15
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmaxCBNotification extends PzServlet
{
    private static TransactionLogger logger = new TransactionLogger(CupFrontendServlet.class.getName());
    public EmaxCBNotification()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException in EmaxCBNotification---" + e);
        }
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException in EmaxCBNotification---" + e);
        }
    }
    public void doService(HttpServletRequest req, HttpServletResponse res) throws PZDBViolationException,ServletException,IOException
    {

        logger.debug("Inside EmaxCBNotification:::" + req.getHeader("X-Forwarded-For"));

        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        String accountid = "";
        String cbAmount = "";
        String status = "";
        String reason = "";
        String paymentId = "";
        String trackingid = "";
        String toid ="";
        String remark="";
        String type = "";

        CommResponseVO commResponseVO = new CommResponseVO();

        String data=req.getParameter("req");
        logger.debug("request data:::::::" + data);

        ObjectMapper objectMapper = new ObjectMapper();
        RequestData requestData = new RequestData();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try
        {
            requestData = objectMapper.readValue(data,RequestData.class);
            cbAmount = requestData.getTransaction().getAmount();
            status = requestData.getTransaction().getStatus();
            reason = requestData.getTransaction().getMessage();
            paymentId = requestData.getTransaction().getUid();
            remark = requestData.getTransaction().getReason();
            type = requestData.getTransaction().getType();

            logger.debug("cbamount---" + cbAmount);
            logger.debug("status---" + status);
            logger.debug("reason---" + reason);
            logger.debug("paymentId---" + paymentId);

            connection = Database.getConnection();

            String query = "SELECT accountid,trackingid,toid FROM transaction_common WHERE paymentid=?";
            p = connection.prepareStatement(query);
            p.setString(1, paymentId);
            rs = p.executeQuery();
            if(rs.next())
            {
                accountid = rs.getString("accountid");
                trackingid = rs.getString("trackingid");
                toid = rs.getString("toid");
            }

            logger.debug("cb status---" + requestData.getTransaction().getChargeback().getStatus());
            String descriptor= GatewayAccountService.getGatewayAccount(accountid).getDisplayName();
            logger.debug("descriptor---" + descriptor);
            if(requestData.getTransaction().getChargeback().getStatus().equalsIgnoreCase("successful"))
            {
                String uQuery = "update transaction_common set chargebackamount=?,status=?,remark=? where paymentid=?";
                p = connection.prepareStatement(uQuery);
                p.setString(1, cbAmount);
                p.setString(2, status);
                p.setString(3, reason);
                p.setString(4, paymentId);
                p.executeUpdate();
            }
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorId(toid);
            auditTrailVO.setActionExecutorName("Chargeback Notification");

            commResponseVO.setTransactionId(paymentId);
            commResponseVO.setDescription(reason);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType(type);
            commResponseVO.setRemark(remark);

            ActionEntry entry = new ActionEntry();
            entry.actionEntryForCommon(trackingid,cbAmount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,status,commResponseVO,auditTrailVO,null);
        }
        catch (JsonParseException jpe)
        {
            logger.debug("JsonParseException------" + jpe);
        }
        catch (JsonMappingException jme)
        {
            logger.debug("JsonParseException-----" + jme);
        }
        catch (IOException ioe)
        {
            logger.debug("IOException-----------" + ioe);
        }
        catch (SystemError se)
        {
            logger.debug("SystemError in EmaxCBNotification---" + se);
        }
        catch (SQLException e)
        {
            logger.debug("SQLException in EmaxCBNotification---" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
    }
}