package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.TransactionManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FraudAlertTransactionList extends HttpServlet
{
    private static Logger log = new Logger(FraudAlertTransactionList.class.getName());
    Functions functions = new Functions();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/fraudAlertTransaction.jsp?ctoken="+user.getCSRFToken());

        String errormsg="";
        String gateway=null;
        String error="";
        String accountid = req.getParameter("accountid");
        String EOL = "<BR>";
        Hashtable hash = null;
        TransactionManager transactionManager = new TransactionManager();

        error = error + validateOptionalParameters(req);

        try
        {
            //validateOptionalParameter(req);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);
            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
            if (!ESAPI.validator().isValidInput("partnerlist", req.getParameter("partnerlist"), "Number",20, true))
            {

                error = error + "Invalid Partner Id";
            }
            if ( functions.hasHTMLTags(req.getParameter(req.getParameter("authCode"))))
            {

                error = error + "Invalid Authorization Code";
            }

            if (functions.hasHTMLTags(req.getParameter(req.getParameter("mid"))))
            {

                error = error + "Invalid MID";
            }

            if (functions.hasHTMLTags(req.getParameter(req.getParameter("rrn"))))
            {

                error = error + "Invalid RRN";
            }
            if(!error.isEmpty())
            {
                req.setAttribute("error",error);
                rd.forward(req,res);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }
        String amount = req.getParameter("amount");
        String refundamount = req.getParameter("refundamount");
        String trackingid = req.getParameter("trackingid");
        String mid = req.getParameter("mid");
        String toid = "";

        if (!req.getParameter("toid").equalsIgnoreCase("0"))
        {
            toid = req.getParameter("toid");
        }
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        String startTime =req.getParameter("starttime");
        String endTime =req.getParameter("endtime");
        String dateType = req.getParameter("datetype");
        String paymentid = req.getParameter("paymentid");
        String terminalId = req.getParameter("terminalid");
        String transactionType = req.getParameter("transactionType");
        String isRefund=req.getParameter("isRefund");
        String rrn=req.getParameter("rrn");
        String authCode=req.getParameter("authCode");
        String partnerid=req.getParameter("partnerid");
        String firstsix = req.getParameter("firstsix");
        String lastfour = req.getParameter("lastfour");

        String currency = "";
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone = new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;

        String fromdate = req.getParameter("fromdate");
        String todate = req.getParameter("todate");
        try
        {
            date = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            tdate = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear = String.valueOf(rightNow.get(Calendar.YEAR));
        }
        catch(ParseException e)
        {
            log.error("System error occure",e);

        }

        startTime=startTime.trim();
        endTime=endTime.trim();

        if (!functions.isValueNull(startTime))
        {
            startTime="00:00:00";
        }
        if (!functions.isValueNull(endTime))
        {
            endTime="23:59:59";
        }


        String startTimeArr[]=startTime.split(":");
        String endTimeArr[]=endTime.split(":");

        String fdtstamp=  Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp= Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        req.setAttribute("pgtypeid",gateway);
        req.setAttribute("toid", toid);
        req.setAttribute("accountid", accountid);

        PrintWriter out = res.getWriter();

        //Functions fn=new Functions();
        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);

        String fdatetime= fyear+"-"+fmonth+"-"+fdate+" "+startTime;
        String tdatetime= tyear+"-"+tmonth+"-"+tdate+" "+endTime;
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isInvalid=false;
        try
        {
            Date date1 = format.parse(fdatetime);
            Date date2 = format.parse(tdatetime);
            Timestamp sDate = new Timestamp(date1.getTime());
            Timestamp eDate = new Timestamp(date2.getTime());
            if(sDate.after(eDate))
            {
                isInvalid=true;
            }
        }
        catch (ParseException e)
        {
            log.error("Exception---" + e);
        }
        if(isInvalid)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>From date should not be greater then To date " + EOL + "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            rd.forward(req, res);
        }
        StringBuffer trackingIds = new StringBuffer();
        if (functions.isValueNull(trackingid))
        {
            List<String> trackingidList=null;
            if(trackingid.contains(","))
            {
                trackingidList  = Arrays.asList(trackingid.split(","));
            }
            else
            {
                trackingidList= Arrays.asList(trackingid.split(" "));
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
        if (!ESAPI.validator().isValidInput("trackingid",req.getParameter("trackingid"),"Numbers",100,true))
        {
            log.error("Invalid TrackingID.");
            error="Invalid TrackingID.";
            req.setAttribute("error",error);
            rd = req.getRequestDispatcher("/fraudAlertTransaction.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            trackingid = req.getParameter("trackingid");
        }
        try
        {
            Date date7=new Date();
            log.error("listTransactions startTime---" + date7.getTime());
            log.error("fdtstamp::::"+fdtstamp+"-------tdtstamp---"+tdtstamp);
            int totalRecords= transactionManager.getCountfraudTransactionListNew(toid, partnerid, mid, trackingIds.toString(), terminalId, transactionType, amount, refundamount, firstsix,lastfour, tdtstamp, fdtstamp, records, pageno, currency, accountid, paymentid, dateType, isRefund, rrn, authCode);
            hash = transactionManager.fraudTransactionListPartner(toid, partnerid, mid, trackingIds.toString(), terminalId, transactionType, amount, refundamount, firstsix,lastfour, tdtstamp, fdtstamp, records, pageno, currency, accountid, paymentid, dateType, isRefund, rrn, authCode,totalRecords);
            log.error("listTransactions End time--->" + (new Date()).getTime());
            log.error("listTransactions Diff time--->" + ((new Date()).getTime() - date7.getTime()));
            System.out.println("hash::::"+hash);
            req.setAttribute("transactionsdetails", hash);
            rd.forward(req, res);
        }
        catch (Exception e)
        {   log.error("Exception:::: errror in doPost",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.DESC);
       // inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);
        inputFieldsListOptional.add(InputFields.PID);
        //inputFieldsListOptional.add(InputFields.CARDNUMBER);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.REFUNDAMOUNT);
        inputFieldsListOptional.add(InputFields.AMOUNT);
        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.STARTTIME);
        inputFieldsListOptional.add(InputFields.ENDTIME);
        inputFieldsListOptional.add(InputFields.FIRST_SIX);
        inputFieldsListOptional.add(InputFields.LAST_FOUR);


        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage() + EOL);
                }
            }
        }
        return error;
    }
}