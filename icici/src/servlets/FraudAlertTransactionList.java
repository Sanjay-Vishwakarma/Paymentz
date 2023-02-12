import com.directi.pg.ActionEntry;
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.TransactionManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
    ServletContext ctx = null;
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        if (ctx == null) ctx = getServletContext();
        String errormsg="";
        String gateway=null;
        String accountid = req.getParameter("accountid");
        String EOL = "<BR>";
        Hashtable hash = null;
        TransactionManager transactionManager = new TransactionManager();
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid Input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            //flag = false;
            log.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/fraudAlertTransaction.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        try
        {
        String amount = req.getParameter("amount");
        String refundamount = req.getParameter("refundamount");
        String firstfourofccnum = req.getParameter("firstfourofccnum");
        String lastfourofccnum = req.getParameter("lastfourofccnum");
        String trackingid = req.getParameter("STrackingid");
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

        String currency = "";
        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

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
        req.setAttribute("toid",toid);
        req.setAttribute("accountid",accountid);

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
           log.error("Catch ParseException...",e);
        }
        if(isInvalid)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>From date should not be greater then To date " + EOL + "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/fraudAlertTransaction.jsp?ctoken="+user.getCSRFToken());
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

            Date date7=new Date();
            log.error("listTransactions startTime---" + date7.getTime());
            log.error("fdtstamp::::"+fdtstamp+"-------tdtstamp---"+tdtstamp);
            hash = transactionManager.fraudTransactionList(toid, mid,trackingIds.toString(),terminalId,transactionType,amount, refundamount, firstfourofccnum, lastfourofccnum, tdtstamp, fdtstamp, records, pageno,currency, accountid,paymentid, dateType,isRefund,rrn,authCode );
            log.error("listTransactions End time--->" + (new Date()).getTime());
            log.error("listTransactions Diff time--->" + ((new Date()).getTime() - date7.getTime()));
            req.setAttribute("transactionsdetails", hash);
        }
        catch (Exception e)
        {   log.error("Exception:::: errror in doPost",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/fraudAlertTransaction.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.COMMASEPRATED_TRACKINGID_TRA);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.TERMINALID);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.REFUNDAMOUNT);
        inputFieldsListMandatory.add(InputFields.FIRSTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.LASTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.STARTTIME);
        inputFieldsListMandatory.add(InputFields.ENDTIME);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}