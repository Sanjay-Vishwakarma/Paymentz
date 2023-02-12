package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.QwipiResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by blue team on 6/6/2016.
 */
public class QwipiCBNotification extends PzServlet
{
    public QwipiCBNotification()
    {
        super();
    }
    private static Logger log = new Logger(QwipiCBNotification.class.getName());

    Connection con = null;
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException QwipiCBNotification:::",e);
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
            log.error("PZDBViolationException QwipiCBNotification:::",e);
        }
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws PZDBViolationException,ServletException,IOException
    {

        log.debug("Inside QwipiCBNotification:::" + req.getHeader("X-Forwarded-For"));

        try
        {
            Connection connection = null;
            PrintWriter printWriter = res.getWriter();
            String merNo = req.getParameter("merNo");
            String orderId = req.getParameter("orderId");
            String billNo = req.getParameter("billNo");
            String chargeBacked = req.getParameter("chargebacked");
            String dateChargeBack = req.getParameter("dateChargeback");
            String md5Info = req.getParameter("md5Info");

            QwipiResponseVO qwipiResponseVO = new QwipiResponseVO();

            log.debug("merId---"+merNo);
            log.debug("orderId---"+orderId);
            log.debug("billno---"+billNo);
            log.debug("cb---"+chargeBacked);

            String trackingid = "";
            String toid = "";
            String amount = "";
            String accountid="";
            String chargeback="";
            Functions functions = new Functions();

            connection= Database.getConnection();
            if(functions.isValueNull(orderId))
            {
                String query = "SELECT trackingid,toid,accountid,amount FROM transaction_qwipi WHERE qwipiPaymentOrderNumber=?";
                PreparedStatement p = connection.prepareStatement(query);
                p.setString(1, orderId);

                ResultSet resultSet = p.executeQuery();

                if (resultSet.next())
                {
                    trackingid = resultSet.getString("trackingid");
                    toid = resultSet.getString("toid");
                    amount = resultSet.getString("amount");
                    accountid = resultSet.getString("accountid");
                }
                String descriptor = GatewayAccountService.getGatewayAccount(accountid).getDisplayName();

                log.debug("bildescp----" + descriptor);
                if(functions.isValueNull(trackingid) && functions.isValueNull(toid))
                {


                    if ("Y".equalsIgnoreCase(chargeBacked))
                    {
                        String uQuery = "UPDATE transaction_qwipi SET chargebackamount=?, STATUS=?, remark=? WHERE qwipiPaymentOrderNumber=?";
                        PreparedStatement ps = connection.prepareStatement(uQuery);
                        ps.setString(1, amount);
                        ps.setString(2, "chargeback");
                        ps.setString(3, "Chargeback received");
                        ps.setString(4, orderId);
                        ps.executeUpdate();
                        log.debug("update query---" + ps);

                        AuditTrailVO auditTrailVO = new AuditTrailVO();
                        auditTrailVO.setActionExecutorId(toid);
                        auditTrailVO.setActionExecutorName("Chargeback Notification");

                        qwipiResponseVO.setPaymentOrderNo(orderId);
                        qwipiResponseVO.setBillingDescriptor(descriptor);
                        qwipiResponseVO.setDateTime(dateChargeBack);

                        ActionEntry entry = new ActionEntry();
                        entry.actionEntryForQwipi(trackingid, amount, ActionEntry.ACTION_CHARGEBACK_RACEIVED, ActionEntry.STATUS_CHARGEBACK_REVERSED, "", qwipiResponseVO, auditTrailVO);

                    }
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError---", systemError);
        }
        catch (SQLException e)
        {
            log.error("SystemError---",e);
        }

    }
}