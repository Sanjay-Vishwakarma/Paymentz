package servlets;

import com.directi.pg.ActionEntry;
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.TransactionManager;
import com.sofort.lib.products.response.parts.TransactionDetails;
import org.owasp.esapi.User;

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

/**
 * Created by Diksha on 24-Sep-21.
 */
public class PayoutTransactionDetails extends HttpServlet
{
    private static Logger log = new Logger(PayoutTransactionDetails.class.getName());
    Functions functions = new Functions();
    ServletContext ctx = null;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        log.error("Inside PayoutTransactionDetails-------------");
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        if (ctx == null) ctx = getServletContext();
        String errormsg="";
        String EOL = "<BR>";
        Hashtable hash = null;
        TransactionManager transactionManager=new TransactionManager();
        String perfectmatch = null;
        perfectmatch        = Functions.checkStringNull(req.getParameter("perfectmatch"));
        boolean flag        = false;
        String massage      = "";
        Hashtable actionhash    = null;
        String iciciTransId     = null;
        String gateway          = null;
        String status           = req.getParameter("status");
        String accountid        = req.getParameter("accountid");
        String cardpresent      = req.getParameter("cardpresent");
        Hashtable hash_payout = null;
        String merchantId = null;
        boolean archive= false;
        archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();
        ActionEntry entry                       = new ActionEntry();


        if(functions.isValueNull(req.getParameter("action")) && "TransactionDetails".equals(req.getParameter("action")) && functions.isValueNull(req.getParameter("STrackingid")) && !req.getParameter("accountid").equalsIgnoreCase("0") )
        {
            String icicitransid = req.getParameter("STrackingid");

            try
            {

                hash            = transactionManager.getTransactionDetails(icicitransid, archive,accountid);

                hash_payout    = transactionManager.getPayoutStartDetails(icicitransid);

                actionhash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId,gateway);

                if(req.getAttribute("message")!=null && functions.isValueNull((String) req.getAttribute("message")))
                {
                    massage = "<center><font class=\"text\" face=\"arial\"><b>" + (String) req.getAttribute("message") + "</b></font></center>";
                }
                log.debug("fetch record through getTransactionDetails");
                req.setAttribute("transactionsdetails", hash);
                req.setAttribute("transpayoutdetails",hash_payout);

                req.setAttribute("message", massage);
                //req.setAttribute("childhash",childhash);
                entry.closeConnection();
                req.setAttribute("actionHistory", actionhash);
                log.debug("forwarding to payoutTransactionDetails.jsp");
                //hash=null;
                RequestDispatcher rd = req.getRequestDispatcher("/payoutTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);

            }
            catch (Exception e)
            {   log.error("Exception in doGet method",e);
                errormsg    += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
                flag        = false;
                log.debug("message..." + e.getMessage());
                req.setAttribute("errormessage",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/payoutTransactions.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            return;
        }

        try
        {
            String amount = req.getParameter("amount");
            String bankAccountName = req.getParameter("bankAccountName");
            String bankNumber = req.getParameter("bankNumber");
            String bankIfsc = req.getParameter("bankIfsc");
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
            String startTime = req.getParameter("starttime");
            String endTime = req.getParameter("endtime");
            String dateType = req.getParameter("datetype");
            String terminalId = req.getParameter("terminalid");
            String transactionType = req.getParameter("transactionType");
            String email = req.getParameter("email");
            String description = req.getParameter("description");
            String authCode = req.getParameter("authCode");

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
            req.setAttribute("toid", toid);
            req.setAttribute("accountid", accountid);

            PrintWriter out = res.getWriter();

            //Functions fn=new Functions();
            int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);

            String fdatetime = fyear + "-" + fmonth + "-" + fdate + " " + startTime;
            String tdatetime = tyear + "-" + tmonth + "-" + tdate + " " + endTime;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            boolean isInvalid = false;
            try
            {
                Date date1 = format.parse(fdatetime);
                Date date2 = format.parse(tdatetime);
                Timestamp sDate = new Timestamp(date1.getTime());
                Timestamp eDate = new Timestamp(date2.getTime());
                if (sDate.after(eDate))
                {
                    isInvalid = true;
                }
            }
            catch (ParseException e)
            {
                log.error("Catch ParseException...", e);
            }
            if (isInvalid)
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>From date should not be greater then To date " + EOL + "</b></font></center>";
                req.setAttribute("errormessage", errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/fraudAlertTransaction.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }

            StringBuffer trackingIds = new StringBuffer();
            if (functions.isValueNull(trackingid))
            {
                List<String> trackingidList = null;
                if (trackingid.contains(","))
                {
                    trackingidList = Arrays.asList(trackingid.split(","));
                }
                else
                {
                    trackingidList = Arrays.asList(trackingid.split(" "));
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

            String totype="";
            hash = transactionManager.payoutTransactionList(toid, trackingIds.toString(), terminalId, amount, email, description, accountid, tdtstamp, fdtstamp, records, pageno, totype,dateType, bankAccountName, bankNumber, bankIfsc,status);
            //System.out.println("hash java[==="+hash.size());
            req.setAttribute("transactionsdetails", hash);
        }
        catch (Exception e)
        {   log.error("Exception:::: errror in doPost",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Payout Transactions");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/payoutTransactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}























































