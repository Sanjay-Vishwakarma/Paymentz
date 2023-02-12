import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.PaymentProcessFactory;
import com.payment.request.PZCaptureRequest;
import com.payment.response.PZCaptureResponse;
import com.payment.response.PZResponseStatus;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;

//import java.io.PrintWriter;
//import java.io.StringWriter;

public class PodSubmit extends HttpServlet
{
    private static Logger logger = new Logger(PodSubmit.class.getName());
   // private static TransactionLogger transactionLogger = new TransactionLogger(PodSubmit.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Inside Pod Sumbit");
        Merchants merchants = new Merchants();
        HttpSession session = req.getSession();
        if (!merchants.isLoggedIn(session))
        {   logger.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String accountId=null;
        String shippingSite=null;
        String shippingId=null;
        String memberid = (String) session.getAttribute("merchantid");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        boolean captured = false;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        String query = null;
        int count = 1;
        Hashtable podhash = null;
        String description = null;
        String amount = "";

        /*res.setContentType("text/html");*/
        String ipaddress=Functions.getIpAddress(req);
       // PrintWriter out = res.getWriter();
        String message = null;
        boolean exceptionocccured = false;

       // StringWriter sw = new StringWriter();

        captured = false;
        description = null;

        podhash = new Hashtable();
        count = 1;
        int failcount = 1;
        int successcount = 1;
        AsynchronousMailService mailService=new AsynchronousMailService();
        //MailService mailService=new MailService();
        HashMap captureMailDetails=new HashMap();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/logout.jsp");
            return;
        }
        String[] icicitransidStr =null;
        icicitransidStr=req.getParameterValues("trackingid");
        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            message="Invalid TransactionID. Select at least one transaction to process capture";
            req.setAttribute("error",message);
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/Pod?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        String notificationUrl = "";
        try
        {
            conn=Database.getConnection();

        for (String icicitransid : icicitransidStr)
        {
            Functions functions=new Functions();
            if(!functions.isValueNull(req.getParameter("podNO_"+icicitransid)) && !functions.isValueNull(req.getParameter("podSITE_"+icicitransid)))
            {
                 message="Invalid POD(Shipment Tracking No) or shipment tracker site,POD or site should not be empty.";
                 req.setAttribute("error",message);
                 Hashtable hashtable= (Hashtable) session.getAttribute("poddetails");
                 req.setAttribute("poddetails",hashtable);
                RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if(!functions.isValueNull(req.getParameter("podNO_"+icicitransid)) || !functions.isValueNull(req.getParameter("podSITE_"+icicitransid)))
            {
                message="Shipment Tracking number OR SiteName cannot be left blank.";
                req.setAttribute("error",message);
                Hashtable hashtable= (Hashtable) session.getAttribute("poddetails");
                req.setAttribute("poddetails",hashtable);
                RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if (!ESAPI.validator().isValidInput("accountid",req.getParameter("accountid_"+icicitransid),"Numbers",10,false))
            {
                message="Invalid AccountID.";
                req.setAttribute("error",message);
                Hashtable hashtable= (Hashtable) session.getAttribute("poddetails");
                req.setAttribute("poddetails",hashtable);
                RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                accountId=req.getParameter("accountid_"+icicitransid);
            }

            if (!ESAPI.validator().isValidInput("podSITE_",req.getParameter("podSITE_"+icicitransid),"URL",25,false))
            {
                message="kindly enter valid site name ex: https://www.xyz.com OR entered value is longer than the maximum allowed characters ";
                req.setAttribute("error",message);
                Hashtable hashtable= (Hashtable) session.getAttribute("poddetails");
                req.setAttribute("poddetails",hashtable);
                RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                shippingSite=req.getParameter("podSITE_"+icicitransid);
            }

            if (!ESAPI.validator().isValidInput("podNO_",req.getParameter("podNO_"+icicitransid),"SafeString",20,false))
            {
                message="Invalid Shipment Tracking number OR entered value is longer than the maximum allowed characters.";
                req.setAttribute("error",message);
                Hashtable hashtable= (Hashtable) session.getAttribute("poddetails");
                req.setAttribute("poddetails",hashtable);
                RequestDispatcher rd = req.getRequestDispatcher("/pod.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else
            {
                shippingId=req.getParameter("podNO_"+icicitransid);
            }


            LinkedHashMap captureTrans=new LinkedHashMap();

            String gatewayType = null;
            String currency=null;
            if (accountId != null && !accountId.equals(""))
            {
                gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();
                if(functions.isValueNull(req.getParameter("currency")))
                {
                    currency = req.getParameter("currency");
                }else{
                    currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                }
            }
            if (shippingId!=null && !shippingId.trim().equalsIgnoreCase("n/a") && !shippingId.trim().equalsIgnoreCase("na") && shippingSite!=null && !shippingSite.trim().equals("") && !shippingSite.trim().equalsIgnoreCase("n/a") && !shippingSite.trim().equalsIgnoreCase("na"))
            {
                String refundDescription=null;
                query       = "select amount,description,status,notificationUrl from transaction_common where status in('authsuccessful','capturesuccess') and trackingid= ? ";
                pstmt       = conn.prepareStatement(query);
                pstmt.setString(1, icicitransid);
                rs          = pstmt.executeQuery();
                if (rs.next())
                {
                    amount              = rs.getString("amount");
                    description         = rs.getString("description");
                    notificationUrl     = rs.getString("notificationUrl");
                    PZCaptureResponse captureResponse   = new PZCaptureResponse();

                    if(rs.getString("status").equalsIgnoreCase("capturesuccess"))
                    {
                        refundDescription = updateShippingDetails(icicitransid,shippingId,shippingSite,"transaction_common",memberid);
                        captureResponse.setResponseDesceiption(message);
                    }
                    else
                    {
                        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(icicitransid), Integer.parseInt(accountId));

                        PZCaptureRequest captureRequest = new PZCaptureRequest();

                        captureRequest.setAccountId(Integer.valueOf(accountId));
                        captureRequest.setMemberId(Integer.valueOf(memberid));
                        captureRequest.setTrackingId(Integer.valueOf(icicitransid));
                        captureRequest.setAmount(Double.valueOf(amount));
                        captureRequest.setPod(shippingId);
                        captureRequest.setIpAddress(ipaddress);
                        captureRequest.setPodBatch(shippingSite);
                        captureRequest.setCurrency(currency);
                        captureRequest.setNotificationUrl(notificationUrl);

                        AuditTrailVO auditTrailVO   = new AuditTrailVO();
                        auditTrailVO.setActionExecutorId(memberid);
                        String role = "";
                        for (String s:user.getRoles())
                        {
                            role=role.concat(s);
                        }
                        //auditTrailVO.setActionExecutorName(session.getAttribute("username").toString());
                        auditTrailVO.setActionExecutorName(role+"-"+session.getAttribute("username").toString());
                        captureRequest.setAuditTrailVO(auditTrailVO);

                        captureResponse                 = paymentProcess.capture(captureRequest);
                        PZResponseStatus responseStatus = captureResponse.getStatus();
                        refundDescription               = captureResponse.getResponseDesceiption();
                        if (PZResponseStatus.ERROR.equals(responseStatus))
                        {
                            throw new Exception();
                        }
                        else if (PZResponseStatus.FAILED.equals(responseStatus))
                        {
                            throw new SystemError();
                        }
                        else if (PZResponseStatus.PENDING.equals(responseStatus))
                        {
                            message = refundDescription;
                        }

                    }
                }
                message=refundDescription;
                podhash.put("" + count, icicitransid + "|" + refundDescription);
                if (refundDescription.equals("There was an error while executing the capture"))
                {
                    failcount++;
                    exceptionocccured = true;
                }
                else
                {
                    successcount++;
                    captured = true;
                }
                count++;

            }


            //}
            //send Fail or success capture transaction details to Admin,Partner,Merchant
            captureTrans.put("Memberid",memberid);
            captureTrans.put("Tracking Id",icicitransid);
            captureTrans.put("Order Id",description);
            captureTrans.put("Amount",currency+" "+amount);
            captureTrans.put("Capture Amount",currency+" "+amount);
            captureTrans.put("Remark",message);
            captureTrans.put("Shipping Site",shippingSite);
            captureTrans.put("Shipping Id",shippingId);
            captureMailDetails.put(icicitransid,captureTrans);
        }//while

            captureMailDetails.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTable(captureMailDetails).toString());
            captureMailDetails.put(MailPlaceHolder.TOID,memberid);

            //Sending Multiple capture in table
            mailService.sendMerchantMonitoringAlert(MailEventEnum.CAPTURE_TRANSACTION, captureMailDetails);
            req.setAttribute("podconfirm", podhash);
        }
        catch (SystemError se)
        {
            exceptionocccured = true;
            failcount++;
            logger.error("Error while capture :", se);

        }
        catch (Exception e)
        {
            exceptionocccured = true;
            logger.error("Error while capture :", e);

        }//try catch ends
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        String error="";
        if(exceptionocccured)
        {
            error= "We have encountered an error while you tried to Capture this transaction. However, you need not to worry, as we will Capture this transaction from our end.";
            req.setAttribute("error",error);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/podconfirm.jsp");
        rd.forward(req, res);

    }//post ends

    private String updateShippingDetails(String trackingid,String pod, String podBatch, String tableName,String memberid)
    {
        String message="Shipping details submitted Fail";
        Connection connection=null;
        PreparedStatement pstmt = null;
        try
        {

            connection=Database.getConnection();
            String query = "update "+tableName+" set pod=?, podbatch=? where toid=? and status='capturesuccess' and trackingid=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,pod);
            pstmt.setString(2,podBatch);
            pstmt.setString(3,memberid);
            pstmt.setString(4,trackingid);
            int i = pstmt.executeUpdate();

            if(i>0)
            {
                message="Shipping details submitted successfully";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while update shipping details",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Error while update shipping details",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return message;
    }
}