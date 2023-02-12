import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.CUPPaymentGateway;
import com.payment.AbstractPaymentProcess;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.cup.core.CupPaymentProcess;
import com.payment.request.PZCancelRequest;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sbm.core.SBMPaymentGateway;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

//import com.directi.pg.core.paymentgateway.PayLineVoucherGateway;

public class CancelTransaction extends HttpServlet
{
    private static Logger logger = new Logger(CancelTransaction.class.getName());
  //  private static TransactionLogger transactionLogger = new TransactionLogger(CancelTransaction.class.getName());

    public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException
    {
        doPost(httpservletrequest, httpservletresponse);
    }

    public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException
    {
        HttpSession session         = httpservletrequest.getSession();
        ResourceBundle rb1          = null;
        String language_property1   = (String)session.getAttribute("language_property");
        rb1                         = LoadProperties.getProperty(language_property1);
        String CancelTransaction_TrackingID_errormsg    = StringUtils.isNotEmpty(rb1.getString("CancelTransaction_TrackingID_errormsg"))?rb1.getString("CancelTransaction_TrackingID_errormsg"): "TrackingID is not Valid.";
        String CancelTransaction_cancel_errormsg        = StringUtils.isNotEmpty(rb1.getString("CancelTransaction_cancel_errormsg"))?rb1.getString("CancelTransaction_cancel_errormsg"): "Cancel Transaction Process is successful";
        String CancelTransaction_CancelTransaction_errormsg = StringUtils.isNotEmpty(rb1.getString("CancelTransaction_CancelTransaction_errormsg"))?rb1.getString("CancelTransaction_CancelTransaction_errormsg"): "CancelTransaction Failed";
        String CancelTransaction_Error_errormsg             = StringUtils.isNotEmpty(rb1.getString("CancelTransaction_Error_errormsg"))?rb1.getString("CancelTransaction_Error_errormsg"): "Error while Cancel Transaction";

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

       // int i = 0;
      //  int j = 0;
        String memberId         = null;
        String requestStatus    = null;
        httpservletresponse.setContentType("text/html");
        PrintWriter printwriter = httpservletresponse.getWriter();
        String trackingId       = null;
        String accountId        = null;
        String ipAddress        = Functions.getIpAddress(httpservletrequest);
        String currency         = httpservletrequest.getParameter("currency");
        String amount           = "";
        String notificationURL  = "";
        String terminalid       = "";
        int k = 1;
        int l = 30;
        AuditTrailVO auditTrailVO= new AuditTrailVO();
        try
        {
            trackingId  = ESAPI.validator().getValidInput("icicitransid", httpservletrequest.getParameter("icicitransid"), "Numbers", 10, false);
            accountId   = ESAPI.validator().getValidInput("accountid", httpservletrequest.getParameter("accountid"), "Numbers", 10, false);
        }
        catch (ValidationException e)
        {
            logger.error("TrackingID is not Valid::::", e);
            String message      = CancelTransaction_TrackingID_errormsg;
            httpservletrequest.setAttribute("error",message);
            RequestDispatcher rd = httpservletrequest.getRequestDispatcher("/servlet/Pod?ctoken="+user.getCSRFToken());
            rd.forward(httpservletrequest, httpservletresponse);
            return;
        }
        if (trackingId == null)
        {
            printwriter.println(Functions.NewShowConfirmation1("Error", "icicitransid not passed"));
            return;
        }
        try
        {
            k = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno", httpservletrequest.getParameter("SPageno"), "Numbers", 5, true), 1);
            l = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords", httpservletrequest.getParameter("SRecords"), "Numbers", 5, true), 30);
        }
        catch (ValidationException e)
        {
            logger.error("Invalid page no or records", e);
            k = 1;
            l = 30;
        }
        HttpSession httpsession = httpservletrequest.getSession();
        memberId                = (String) httpsession.getAttribute("merchantid");

        if (memberId == null)
        {
            printwriter.println("merchantid not present.");
            return;
        }
        Connection conn     = null;
        String gatewayType  = null;
        if (accountId != null && !accountId.equals(""))
        {
            gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        }
        String message=null;

        if (gatewayType != null && CUPPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
        {
            try
            {
                PZCancelRequest cancelRequest = new PZCancelRequest();
                cancelRequest.setAccountId(Integer.parseInt(accountId));
                cancelRequest.setTrackingId(Integer.parseInt(trackingId));
                CupPaymentProcess payment   = new CupPaymentProcess();
                PZCancelResponse response   = payment.cancel(cancelRequest);
                PZResponseStatus status     = response.getStatus();
                if (PZResponseStatus.ERROR.equals(status))
                {
                    throw new Exception();
                }
                else if (PZResponseStatus.FAILED.equals(status))
                {
                    throw new SystemError();
                }
                else if(PZResponseStatus.SUCCESS.equals(status))
                {
                    message = CancelTransaction_cancel_errormsg;
                }

            }
            catch (SystemError systemerror)
            {
                logger.error("System error in CancelTransaction", systemerror);
                message = CancelTransaction_CancelTransaction_errormsg;
            }
            catch (Exception exception)
            {
                logger.error("Exception in CancelTransaction", exception);
                message = CancelTransaction_Error_errormsg;
            }

        }
        // Payline Voucher Cancel
       /* else if (gatewayType != null && PayLineVoucherGateway.GATEWAY_TYPE.equals(gatewayType))
        {

        }*/
        else //Generic Cancel transaction
        {
            PreparedStatement pstmt = null;
            ResultSet rs            = null;
            try
            {
                conn            = Database.getConnection();
                String query    = "select toid,status,terminalid,amount,notificationUrl from transaction_common where trackingid = ? and accountid = ?";
                pstmt           = conn.prepareStatement(query);
                pstmt.setString(1, trackingId);
                pstmt.setString(2, accountId);
                rs = pstmt.executeQuery();

                if (rs.next())
                {
                    memberId        = rs.getString("toid");
                    requestStatus   = rs.getString("status");
                    terminalid      = rs.getString("terminalid");
                    amount          = rs.getString("amount");
                    notificationURL = rs.getString("notificationUrl");

                }
                else
                {
                    printwriter.println(Functions.NewShowConfirmation1("Error", "Invalid Tracking Id"));
                }

                PZCancelRequest cancelRequest = new PZCancelRequest();
                cancelRequest.setMemberId(Integer.parseInt(memberId));
                cancelRequest.setAccountId(Integer.parseInt(accountId));
                cancelRequest.setTrackingId(Integer.parseInt(trackingId));
                cancelRequest.setTerminalId(terminalid);
                cancelRequest.setAmount(amount);
                cancelRequest.setNotificationURL(notificationURL);
                cancelRequest.setIpAddress(ipAddress);
                cancelRequest.setCurrency(currency);
                cancelRequest.setCancelReason("Cancel Transaction " + trackingId);
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));
                PZCancelResponse response=null;

                auditTrailVO.setActionExecutorId(memberId);
                //auditTrailVO.setActionExecutorName("Merchant Cancel");
                String role = "";
                for (String s:user.getRoles())
                {
                    role=role.concat(s);
                }
                auditTrailVO.setActionExecutorName(role+"-"+session.getAttribute("username").toString());
                cancelRequest.setAuditTrailVO(auditTrailVO);

                if(gatewayType != null && SBMPaymentGateway.GATEWAY_TYPE.equals(gatewayType) && PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(requestStatus))
                {
                    response = paymentProcess.cancelCapture(cancelRequest);
                }
                else
                {
                    response= paymentProcess.cancel(cancelRequest);
                }
                PZResponseStatus status = response.getStatus();
                if (PZResponseStatus.ERROR.equals(status))
                {
                    throw new Exception();
                }
                else if (PZResponseStatus.FAILED.equals(status))
                {
                    throw new SystemError();

                }
                else if(PZResponseStatus.SUCCESS.equals(status))
                {
                    message="Cancel Transaction Process is successful";
                }
                else if(PZResponseStatus.PENDING.equals(status))
                {
                    message=response.getResponseDesceiption();
                }
            }
            catch (Exception exception)
            {
                logger.error("Exception in CancelTransaction", exception);
                message="Error while Cancel Transaction";
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }

        }

        httpservletrequest.setAttribute("error",message+" TrackingID: "+trackingId);
        RequestDispatcher rd = httpservletrequest.getRequestDispatcher("/servlet/Pod?ctoken="+user.getCSRFToken());
        rd.forward(httpservletrequest, httpservletresponse);


    }

}
