import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.request.PZInquiryRequest;
import com.payment.response.PZInquiryResponse;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/5/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonInquiry extends HttpServlet
{
    private static Logger log = new Logger(CommonInquiry.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage   = new StringBuilder();
        PZInquiryRequest request        = new PZInquiryRequest();
        PZInquiryResponse response      = null;
        Functions functions             = new Functions();

        String val1 = "";
        String val2 = "";
        String val3 = "";
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("not valid data",e);
        }

       /* String trackingid = (String) req.getParameter("trackingid")   ;
        String description = (String) req.getParameter("description");
        String toid = (String) req.getParameter("toid");
        String accountid = (String) req.getParameter("accountid");*/

        String[] trackingidArr  =  req.getParameterValues("trackingid");
        String description      = null;
        String toid             = null;
        String accountid        = null;
        String mid              = "";
        String authCode         = "";
        String transactionid        = "";
        String transactionStatus    = "";
        String transactionType      = "";
        String amount               = "";
        String time                 = "";
        String currency             = "";
        String discription          = "";


        try
        {
            sSuccessMessage.append("<table align=center width=\"50%\" class=\"table table-striped table-bordered table-hover table-green dataTable\">");
            sSuccessMessage.append("<tr>");
            sSuccessMessage.append("<td class=\"th0\">Tracking ID</td>");
            sSuccessMessage.append("<td class=\"th0\">MID</td>");
            sSuccessMessage.append("<td class=\"th0\">Auth Code</td>");
            sSuccessMessage.append("<td class=\"th0\">Transaction ID</td>");
            sSuccessMessage.append("<td class=\"th0\">Transaction Status</td>");
            sSuccessMessage.append("<td class=\"th0\">Transaction Type</td>");
            sSuccessMessage.append("<td class=\"th0\">Amount</td>");
            sSuccessMessage.append("<td class=\"th0\">Currency</td>");
            sSuccessMessage.append("<td class=\"th0\">Time</td>");
            sSuccessMessage.append("<td class=\"th0\">Description</td>");
            sSuccessMessage.append("</tr>");

            for(String trackingid : trackingidArr)
            {
                if (functions.isValueNull(req.getParameter("toid_" + trackingid)))
                {
                    toid = req.getParameter("toid_" + trackingid);
                }
                if (functions.isValueNull(req.getParameter("desc_" + trackingid)))
                {
                    description = req.getParameter("desc_" + trackingid);
                }
                if (functions.isValueNull(req.getParameter("accountid_" + trackingid)))
                {
                    accountid = req.getParameter("accountid_" + trackingid);
                }
                else
                {
                    sSuccessMessage.append(getMessage(trackingid,"","","","","","","","","Account ID is missing."));
                    continue;
                }


                AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingid), Integer.valueOf(accountid));
                request.setMemberId(Integer.valueOf(toid));
                request.setAccountId(Integer.valueOf(accountid));
                request.setTrackingId(Integer.parseInt(trackingid));
                response = process.inquiry(request);
                if (response != null)
                {

                    //successlist.add(trackingid +"<BR>");
                    if (functions.isValueNull(response.getMid()))
                    {
                        mid = response.getMid();
                    }
                    if (functions.isValueNull(response.getAuthCode()))
                    {
                        authCode = response.getAuthCode();
                    }
                    if (functions.isValueNull(response.getResponseTransactionId()))
                    {
                        transactionid = response.getResponseTransactionId();
                    }
                    if (functions.isValueNull(response.getResponseTransactionStatus()))
                    {
                        transactionStatus = response.getResponseTransactionStatus();
                    }
                    if (functions.isValueNull(response.getTransactionType()))
                    {
                        transactionType = response.getTransactionType();
                    }
                    if (functions.isValueNull(response.getResponseAmount()))
                    {
                        amount = response.getResponseAmount();
                    }
                    if (functions.isValueNull(response.getResponseCurrency()))
                    {
                        currency = response.getResponseCurrency();
                    }
                    if (functions.isValueNull(response.getResponseTransactionTime()))
                    {
                        time = response.getResponseTransactionTime();
                    }else if (functions.isValueNull(response.getResponseTime()))
                    {
                        time = response.getResponseTime();
                    }
                    if (functions.isValueNull(response.getResponseDescription()))
                    {
                        discription = response.getResponseDescription();
                    }

                    sSuccessMessage.append(getMessage(trackingid, mid, authCode, transactionid, transactionStatus, transactionType, amount, currency, time, discription));
                    continue;

                /*sSuccessMessage.append("<tr>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+mid+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+authCode+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+transactionid+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+transactionStatus+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+transactionType+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+amount+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+currency+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+time+"</td>");
                sSuccessMessage.append("<td align=\"center\" class=\"tr1\">"+discription+"</td>");
                sSuccessMessage.append("</tr>");*/


                }
                else
                {
                    sSuccessMessage.append(getMessage(trackingid,"","","","","","","","","Transaction Inquiry is not provided for the transaction."));
                    continue;
                }

            }
            /*else
            {
                sSuccessMessage.append("Transaction Inquiry is not provided for the transaction.");
            }*/
        }
        catch(Exception e)
        {
            log.error("Exception",e);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());

        req.setAttribute("cbmessage", chargeBackMessage.toString());

        RequestDispatcher rd = req.getRequestDispatcher("/commoninquirylist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

    public String getMessage(String trackingid,String mid, String authCode, String transactionid, String transactionStatus, String transactionType, String amount, String currency, String time, String discription)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<tr>");
        sb.append("<td align=\"center\" class=\"tr1\">" + trackingid + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + mid + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + authCode + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + transactionid + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + transactionStatus + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + transactionType + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + amount + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + currency + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + time + "</td>");
        sb.append("<td align=\"center\" class=\"tr1\">" + discription + "</td>");
        sb.append("</tr>");
        return sb.toString();
    }
}
