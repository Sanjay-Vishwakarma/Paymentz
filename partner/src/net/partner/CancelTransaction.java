package net.partner;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.CUPPaymentGateway;
import com.directi.pg.core.paymentgateway.PayLineVoucherGateway;
import com.payment.AbstractPaymentProcess;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.cup.core.CupPaymentProcess;
import com.payment.request.PZCancelRequest;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sbm.core.SBMPaymentGateway;
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

public class CancelTransaction extends HttpServlet
{
    private static Logger logger = new Logger(CancelTransaction.class.getName());

    public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException
    {
        doPost(httpservletrequest, httpservletresponse);
    }

    public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
            throws ServletException, IOException
    {
        HttpSession session      = httpservletrequest.getSession();
        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            httpservletresponse.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("CSRF check successful ");
        logger.info("Entering in CancelTransaction");
        int i       = 0;
        int j       = 0;
        Object obj  = null;
        Object obj1 = null;
        Object obj2 = null;
        String memberId         = null;
        String requestStatus    = null;
        String terminalId       = "";
        String amount           = "";
        httpservletresponse.setContentType("text/html");
        PrintWriter printwriter = httpservletresponse.getWriter();
        String s1               = httpservletrequest.getParameter("data");
        String trackingId       = null;
        String accountId        = null;
        String ipAddress        = Functions.getIpAddress(httpservletrequest);
        int k = 1;
        int l = 30;
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        String notificationURL                  = "";
        Functions functions                     = new Functions();
        try
        {
            //System.out.println("trackingid----"+httpservletrequest.getParameter("icicitransid"));
            trackingId = ESAPI.validator().getValidInput("icicitransid", httpservletrequest.getParameter("icicitransid"), "Numbers", 10, false);
            accountId  = ESAPI.validator().getValidInput("accountid", httpservletrequest.getParameter("accountid"), "Numbers", 10, false);
        }
        catch (ValidationException e)
        {
            logger.error("TrackingID is not Valid::::", e);
            /* printwriter.println(Functions.NewShowConfirmation("Error", "Tracking Id not found"));*/
            String message          = "TrackingID is not Valid.";
            httpservletrequest.setAttribute("error",message);
            RequestDispatcher rd    = httpservletrequest.getRequestDispatcher("/net/PartnerCapture?ctoken="+user.getCSRFToken());
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
            i = (k - 1) * l;
            j = l;
        }
        catch (ValidationException e)
        {
            logger.error("Invalid page no or records", e);
            k = 1;
            l = 30;
        }
        HttpSession httpsession = httpservletrequest.getSession();
        memberId                = httpservletrequest.getParameter("memberid");

        if (memberId == null)
        {
            printwriter.println("merchantid not present.");
            return;
        }
        Connection  conn        = null;
        String      gatewayType = null;
        if (accountId != null && !accountId.equals(""))
        {
            gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        }
        String message=null;

        if (gatewayType != null && CUPPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
        {
            try
            {
                PZCancelRequest cancelRequest   = new PZCancelRequest();
                cancelRequest.setAccountId(Integer.parseInt(accountId));
                cancelRequest.setTrackingId(Integer.parseInt(trackingId));
                CupPaymentProcess payment       = new CupPaymentProcess();
                PZCancelResponse response       = payment.cancel(cancelRequest);
                PZResponseStatus status         = response.getStatus();
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
                //httpservletresponse.sendRedirect(httpservletrequest.getContextPath() + "/servlet/Pod?ctoken=" + user.getCSRFToken());

            }
            catch (SystemError systemerror)
            {
                logger.error("System error in CancelTransaction", systemerror);
                message="CancelTransaction Failed";
            }
            catch (Exception exception)
            {
                logger.error("Exception in CancelTransaction", exception);
                message="Error while Cancel Transaction";
            }

        }
        // Payline Voucher Cancel
        else if (gatewayType != null && PayLineVoucherGateway.GATEWAY_TYPE.equals(gatewayType))
        {

        }
        else //Generic Cancel transaction
        {
            PreparedStatement pstmt = null;
            ResultSet         rs    = null;
            try
            {


                logger.debug("Execute select query for CancelTransaction");
                conn            = Database.getConnection();
                String query    = "select * from transaction_common where trackingid = ? and accountid = ?";
                pstmt           = conn.prepareStatement(query);
                pstmt.setString(1, trackingId);
                pstmt.setString(2, accountId);
                rs = pstmt.executeQuery();
                logger.debug("cancel query----"+pstmt);

                if (rs.next())
                {
                    memberId        = rs.getString("toid");
                    requestStatus   = rs.getString("status");
                    terminalId      = rs.getString("terminalid");
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
                cancelRequest.setTerminalId(terminalId);
                cancelRequest.setAmount(amount);
                cancelRequest.setIpAddress(ipAddress);
                cancelRequest.setNotificationURL(notificationURL);
                cancelRequest.setCancelReason("Cancel Transaction " + trackingId);
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));
                PZCancelResponse response = null;
                logger.debug("request status cancel transaction----"+requestStatus);

                auditTrailVO.setActionExecutorId(memberId);
                //auditTrailVO.setActionExecutorName("Merchant Cancel");
                String role = "";
                for (String s:user.getRoles())
                {
                    role += s;
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
                logger.debug("status cancel transaction----"+response.getStatus());
                PZResponseStatus status     = response.getStatus();
                String cancelDescription    = response.getResponseDesceiption();
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
                //System.out.println(status+"--"+message);
                //httpservletresponse.sendRedirect(httpservletrequest.getContextPath() + "/servlet/Pod?ctoken=" + user.getCSRFToken());
            }
            catch (SystemError systemerror)
            {
                logger.error("System error in CancelTransaction", systemerror);
                message="CancelTransaction Failed";
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
        RequestDispatcher rd = httpservletrequest.getRequestDispatcher("/net/PartnerCapture?ctoken="+user.getCSRFToken());
        rd.forward(httpservletrequest, httpservletresponse);


    }

}
