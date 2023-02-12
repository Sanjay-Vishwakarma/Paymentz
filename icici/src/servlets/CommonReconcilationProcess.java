import com.directi.pg.Admin;
import com.directi.pg.*;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZReconcilationRequest;
import com.payment.response.PZReconcilationResponce;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/30/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonReconcilationProcess extends HttpServlet
{
    private static Logger logger = new Logger(CommonReconcilationProcess.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        String icicimerchantid = null;
        String cbamount = null;
        String paymentid = null;
        String amount = null;
        String pzStatus=null;
        String toid = null;
        String company_name = null;
        String contact_emails = null;
        String accountId = null;

        String billno=null;
        BigDecimal bdConst = new BigDecimal("0.01");
        List failList = new ArrayList();
        List successlist = new ArrayList();
        String[] icicitransidStr =null;

        if (req.getParameterValues("trackingid")!= null)
        {
            icicitransidStr = req.getParameterValues("trackingid");
        }
        else
        {
            sErrorMessage.append("Invalid TransactionID.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            logger.debug("forwarding to CommonReconcilation");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            logger.debug("forwarding to CommonReconcilation");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        accountId = req.getParameter("accountid");
        if(accountId==null || accountId.equals(""))
        {
            sErrorMessage.append("AccountID should not be empty.");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        List<PZReconcilationRequest> listOfRecon = new ArrayList<PZReconcilationRequest>();
        PZReconcilationRequest reconcilationRequest = null;
        for (String icicitransid : icicitransidStr)
        {

            reconcilationRequest =new PZReconcilationRequest();
            reconcilationRequest.setTrackingId(Integer.valueOf(icicitransid));
            reconcilationRequest.setAccountId(Integer.valueOf(accountId));
            toid=req.getParameter("toid_" + icicitransid);
            reconcilationRequest.setMemberId(Integer.valueOf(toid));
            paymentid=req.getParameter("paymentid_" + icicitransid);
            reconcilationRequest.setPaymentid(paymentid);
            amount=req.getParameter("amount_" + icicitransid);
            reconcilationRequest.setAmount(amount);
            pzStatus=req.getParameter("status_" + icicitransid);
            reconcilationRequest.setPZstatus(pzStatus);
            listOfRecon.add(reconcilationRequest);
        }
        AbstractPaymentProcess recon = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));
        //CommonPaymentProcess recon=new CommonPaymentProcess();
        List<PZReconcilationResponce> listres=null;
        try
        {
            sSuccessMessage.append("<center><table align=center  cellpadding=\"2\" cellspacing=\"3\"  bgcolor=\"CCE0FF\" border=1>");
            sSuccessMessage.append("<tr>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Tracking ID </b></font></td>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Merchant ID </b></font></td>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Account ID </b></font></td>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Order ID</b></font></td>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Amount </b></font></td>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Current Status </b></font></td>");
            sSuccessMessage.append("<td><font  size=\"2\" face=\"Verdana, Arial\" ><b>Treatment Given </b></font></td>");
            sSuccessMessage.append("</tr>");

            listres = recon.reconcilationTransaction(listOfRecon);

            if(listres!=null)
            {
                Iterator<PZReconcilationResponce> iterator = listres.iterator();
                while(iterator.hasNext())
                {

                    PZReconcilationResponce response = iterator.next();
                    //errString.append("<br>" + response.getTrackingId() + "  |  " + response.getRestoid() + "  |  " + response.getResaccountid() + "  |  " + response.getOrderDesc() + "  |  " + amount + "  |  " + response.getResupdatedStatus() + "  |  " + response.getResponseDesceiption() + "\r\n");

                    sSuccessMessage.append("<tr>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getTrackingId()+"</font> </td>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getRestoid()+"</font> </td>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getResaccountid()+"</font></td>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getOrderDesc()+"</font> </td>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getAmount()+"</font> </td>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getResupdatedStatus()+"</font> </td>");
                    sSuccessMessage.append("<td><font color=\"red\" size=\"3\" face=\"Verdana, Arial\" >"+response.getResponseDesceiption()+"</font> </td>");

                    sSuccessMessage.append("</tr>");
                }
                sSuccessMessage.append("</table></center>");
            }
            req.setAttribute("cbmessage",sSuccessMessage.toString());
            logger.debug("forwarding to CommonReconcilation");
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/CommonReconcilationList?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (SystemError systemError)
        {
            logger.error("system error while CommonReconcilation",systemError);
        }
    }
}
