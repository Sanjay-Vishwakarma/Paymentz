package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ReconManager;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jitendra
 * Date: 24/03/18
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerManualRecon extends HttpServlet
{
    private static Logger log = new Logger(PartnerManualRecon.class.getName());
    private Functions functions = new Functions()
            ;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken="+user.getCSRFToken());

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        int records=30;
        int pageno=1;

        int start = 0; // start index
        int end = 0; // end index

        ReconManager reconManager =new ReconManager();
        String EOL="<BR>";
        String error="";
        String fromDate = req.getParameter("fromdate");
        String toDate = req.getParameter("todate");
        String memberId=req.getParameter("memberid");
       // String accountId=req.getParameter("accountid");
       // String trackingId=req.getParameter("STrackingid");
        String accountId=null;
        String trackingId=req.getParameter("STrackingid");
        String paymentId=req.getParameter("paymentid");
        String orderId=req.getParameter("orderid");
        String status=req.getParameter("fstransstatus");
        String partnerId=req.getParameter("partnerid1");
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        StringBuffer trackingIds = new StringBuffer();
        if (functions.isValueNull(trackingId))
        {
            List<String> trackingidList=null;
            if(trackingId.contains(","))
            {
                trackingidList  = Arrays.asList(trackingId.split(","));
            }
            else
            {
                trackingidList= Arrays.asList(trackingId.split(" "));
            }

            int i = 0;
            Iterator itr = trackingidList.iterator();
            while (itr.hasNext())
            {
                if (i != 0)
                {
                    trackingIds.append(",");
                }
                trackingIds.append("" + itr.next() + "");
                i++;
            }
        }
        if (!ESAPI.validator().isValidInput("STrackingid",req.getParameter("STrackingid"),"Numbers",100,true))
        {
            log.error("Invalid TrackingID.");
            error="Invalid TrackingID.";
            req.setAttribute("error",error);
            rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if(!functions.isValueNull(partnerId))
        {
            if (user.getRoles().contains("superpartner") && user.getRoles().contains("childsuperpartner") && user.getRoles().contains("partner") && user.getRoles().contains("subpartner"))
            {
                partnerId = partnerFunctions.getListOfSubPartner((String) session.getAttribute("merchantid"));
            }
            else
                partnerId = (String) session.getAttribute("merchantid");
        }

        if(functions.isFutureDateComparisonWithFromAndToDate(fromDate, toDate, "dd/MM/yyyy"))
        {
            req.setAttribute("sErrorMessage","Invalid From & To date");
            rd.forward(req,res);
            return;
        }
        /*if (!ESAPI.validator().isValidInput("STrackingid",req.getParameter("STrackingid"),"OnlyNumber",20,true))
        {
            error=error+"Invalid TrackingID.";
        }
        else
        {
            if(functions.isValueNull(req.getParameter("STrackingid")))
            {
                trackingId = req.getParameter("STrackingid");
            }
        }*/
        if (!ESAPI.validator().isValidInput("accountid",req.getParameter("accountid"),"OnlyNumber",20,true))
        {
            log.error("Invalid AccountId."+EOL);
            error=error+"Invalid AccountId.<BR>";
        }
        else
        {
            if(functions.isValueNull(req.getParameter("accountid")))
            {
                accountId = req.getParameter("accountid");
            }
        }
        if (!ESAPI.validator().isValidInput("paymentid",req.getParameter("paymentid"),"OnlyNumber",20,true))
        {
            log.error("Invalid PaymentId."+EOL);
            error=error+"Invalid PaymentId.<BR>";
        }
        else
        {
            if(functions.isValueNull(req.getParameter("paymentid")))
            {
                paymentId = req.getParameter("paymentid");
            }
        }
        if (!ESAPI.validator().isValidInput("memberid",req.getParameter("memberid"),"OnlyNumber",20,true))
        {
            log.error("Invalid MemberId."+EOL);
            error=error+"Invalid MemberId.<BR>";
        }
        else
        {
            if(functions.isValueNull(req.getParameter("memberid")))
            {
                memberId = req.getParameter("memberid");
            }
        }
        if(functions.isValueNull(error))
        {
            req.setAttribute("sErrorMessage",error);
            rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e){
            log.error("ValidationException:::::",e);
            req.setAttribute("sErrorMessage",e.getMessage());
            rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 30);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }

        start = (pageno - 1) * records;
        end = records;

        try
        {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat targetFormat=new SimpleDateFormat("yyyy-MM-dd");

            String fromDate1=targetFormat.format(simpleDateFormat.parse(fromDate))+" 00:00:00";
            String toDate1=targetFormat.format(simpleDateFormat.parse(toDate))+" 23:59:59";
            log.error("fromDate1--->"+fromDate1);
            log.error("toDate1--->"+toDate1);
            Hashtable hash= reconManager.getReconListTransaction(accountId,partnerId,fromDate1, toDate1, memberId, trackingIds,paymentId ,orderId, status, start, end);
            req.setAttribute("transdetails",hash);
        }
        catch (PZDBViolationException pze){
            req.setAttribute("error","Invalid tracking id");
            log.error("PZDBViolationException:::::",pze);
        }
        catch (ParseException e){
            req.setAttribute("error","Internal error while processing your request");
            log.error("ParseException:::::",e);
        }
        rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        //inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.PAYMENTID);
        inputFieldsListMandatory.add(InputFields.ORDERID);
        inputFieldsListMandatory.add(InputFields.FDATE);
        inputFieldsListMandatory.add(InputFields.TDATE);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
        log.debug("line 134 in java:::");
    }
    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }
}