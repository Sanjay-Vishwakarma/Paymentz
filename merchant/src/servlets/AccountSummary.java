import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;


/**
 * Created by Sanjay on 2/14/2022.
 */
public class AccountSummary extends HttpServlet
{

    private static Logger log = new Logger(AccountSummary.class.getName());

    public void doGet(HttpServletRequest req , HttpServletResponse res) throws ServletException ,IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.error("inside AccountSummary do post....! ");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Merchants merchants = new Merchants();
        RequestDispatcher rd = req.getRequestDispatcher("/accountSummary.jsp?ctoken="+user.getCSRFToken());
        RequestDispatcher rd2 = req.getRequestDispatcher("/accountSummary.jsp?ctoken="+user.getCSRFToken());
        String uId = "";
        if (session.getAttribute("role").equals("submerchant"))
        {
            uId = (String) session.getAttribute("userid");
        }
        else
        {
            uId = (String) session.getAttribute("merchantid");
        }
        String error1 ="";
        String errormsg = "";
        String EOL="<BR>";
        if(!merchants.isLoggedIn(session))
        {
            log.debug("Merchant is log out");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        Functions functions = new Functions();
        String action=req.getParameter("button");
        String terminalid =req.getParameter("terminalid");
        String timezone =req.getParameter("timezone");
        String startTime = req.getParameter("starttime");
        String endTime = req.getParameter("endtime");
        String currency = Functions.checkStringNull(req.getParameter("currency")) == null ? "" : req.getParameter("currency");

        if(!ESAPI.validator().isValidInput("currency",currency,"StrictString",3,true))
        {
            error1 = "Invalid Currency";
            req.setAttribute("error",error1);
            rd.forward(req,res);
            return;
        }

        if (functions.isValueNull(timezone))
        {
            timezone = timezone.substring(0,timezone.indexOf("|"));
        }
        Calendar rightNow               = Calendar.getInstance();
        SimpleDateFormat sdf            = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone    = new SimpleDateFormat("yyyy-MM-dd");
        Date date                       = null;

        String fyear    = "";
        String fmonth   = "";
        String fdate    = "";
        String tyear    = "";
        String tmonth   = "";
        String tdate    = "";

        startTime   = startTime.trim();
        endTime     = endTime.trim();

        if (!functions.isValueNull(startTime))
        {
            startTime   = "00:00:00";
        }
        if (!functions.isValueNull(endTime))
        {
            endTime = "23:59:59";
        }

        Hashtable hash                    = new Hashtable();
        Hashtable hashReceived          = new Hashtable();
        Hashtable hashPayout          = new Hashtable();
        Hashtable hashChargeback          = new Hashtable();

        StringBuilder query                = new StringBuilder();
        StringBuilder queryReceived      = new StringBuilder();
        StringBuilder queryPayout      = new StringBuilder();
        StringBuilder queryChargeback      = new StringBuilder();

        int total=0;
        int sum=0;
        double sumAmount=0.00d;
        float sumAmountPayout=0.00f;
        int payoutTotal=0;
        int totalTransactionPayout=0;

        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        try{
            conn=Database.getRDBConnection();

            if (functions.isValueNull(timezone))
            {

                String fromdate = req.getParameter("fdate");
                String todate   = req.getParameter("tdate");
                //from date
                Date frtzdate       = sdf.parse(fromdate);
                String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
                //to Date
                Date totzdate       = sdf.parse(todate);
                String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
                String fdatetime1   = functions.convertDateTimeToTimeZone1(fdatetime, timezone);
                String tdatetime1   = functions.convertDateTimeToTimeZone1(tdatetime, timezone);
                String fdt[]        = fdatetime1.split(" ");
                String tdt[]        = tdatetime1.split(" ");
                date                = sdftimezone.parse(fdt[0]);
                rightNow.setTime(date);
                fdate       = String.valueOf(rightNow.get(Calendar.DATE));
                fmonth      = String.valueOf(rightNow.get(Calendar.MONTH));
                fyear       = String.valueOf(rightNow.get(Calendar.YEAR));
                startTime   = fdt[1];
                date        = sdftimezone.parse(tdt[0]);
                rightNow.setTime(date);
                tdate   = String.valueOf(rightNow.get(Calendar.DATE));
                tmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
                tyear   = String.valueOf(rightNow.get(Calendar.YEAR));
                endTime = tdt[1];
                boolean dateRangeIsValid = datedifference(fdatetime1, tdatetime1);
                log.error("dateRangeIsValid-->"+dateRangeIsValid);
                if(!dateRangeIsValid){
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ "Report date days should not be greater than 30 days!" +  EOL + "</b></font></center>";
                    req.setAttribute("dateRangeIsValid",errormsg);
                    rd.forward(req,res);
                    return;
                }

            }
            else
            {

                String fromdate     = req.getParameter("fdate");
                String todate       = req.getParameter("tdate");

                //from date
                Date frtzdate       = sdf.parse(fromdate);
                String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;

                //to Date
                Date totzdate       = sdf.parse(todate);
                String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
                String fdatetime1   = fdatetime;
                String tdatetime1   = tdatetime;
                boolean dateRangeIsValid = datedifference(fdatetime1, tdatetime1);
                log.error("dateRangeIsValid ::::->"+dateRangeIsValid);
                if(!dateRangeIsValid){
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ "Report date days should not be greater than 30 days!" +  EOL + "</b></font></center>";
                    req.setAttribute("dateRangeIsValid",errormsg);
                    rd.forward(req,res);
                    return;
                }

                date                = sdf.parse(fromdate);
                rightNow.setTime(date);
                fdate   = String.valueOf(rightNow.get(Calendar.DATE));
                fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
                fyear   = String.valueOf(rightNow.get(Calendar.YEAR));


                date    = sdf.parse(todate);
                rightNow.setTime(date);
                tdate   = String.valueOf(rightNow.get(Calendar.DATE));
                tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
                tyear   = String.valueOf(rightNow.get(Calendar.YEAR));

                log.debug("startTime::" + startTime);
                log.debug("endTime::" + endTime);

            }

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");
            log.debug("From date dd::"+fdate+" MM::"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);
            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            if(action.equalsIgnoreCase("search"))
            {

                log.error("inside if condn :::::::::::::");
                String fields = "";
                String  cFields = "";
                String  pFields = "";
                String  rFields = "";


                String tablename        = "transaction_common";

                fields= "STATUS,COUNT(*) as statusCount,SUM(amount) as amount";
                cFields="STATUS,COUNT(*),SUM(chargebackamount) as chargebackamount";
                pFields="STATUS,COUNT(*) as payoutCount,SUM(amount) as amount";
                rFields="STATUS,COUNT(*),SUM(refundamount) as refundamount";

                query.append("select "+ fields + " from "+ tablename +" where toid="+uId+"" );
                queryReceived.append("select "+ rFields + " from "+ tablename +" where toid="+uId+"" );
                queryPayout.append("select "+ pFields + " from "+ tablename +" where toid="+uId+"" );
                queryChargeback.append("select "+ cFields + " from "+ tablename +" where toid="+uId+"" );


                if (functions.isValueNull(fdtstamp))
                {
                    long milliSeconds       = Long.parseLong(fdtstamp + "000");
                    Calendar calendar       = Calendar.getInstance();
                    calendar.setTimeInMillis(milliSeconds);
                    DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String startDate        = formatter1.format(calendar.getTime());

                    query.append(" and dtstamp >='" + fdtstamp + "'");
                    queryReceived.append(" and TIMESTAMP>='" + startDate + "'");
                    queryChargeback.append(" and TIMESTAMP>='" + startDate + "'");
                    queryPayout.append(" and dtstamp >='" + fdtstamp + "'");

                }
                if (functions.isValueNull(tdtstamp))
                {
                    long milliSeconds = Long.parseLong(tdtstamp + "000");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(milliSeconds);
                    DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String endDate          = formatter2.format(calendar.getTime());

                    query.append(" and dtstamp <='" + tdtstamp + "' and STATUS  IN('authstarted','authstarted_3D','authfailed','markedforreversal','authsuccessful','capturesuccess','reversed','settled','chargeback')");
                    queryReceived.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN ('reversed')");
                    queryChargeback.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN ('chargeback','chargebackreversed')");
                    queryPayout.append(" and dtstamp <='" + tdtstamp + "' and STATUS IN('payoutstarted','payoutsuccessful','payoutfailed')");

                }
                if (functions.isValueNull(terminalid) && !terminalid.equals("0"))
                {
                    query.append(" and terminalid ='" + terminalid + "'");
                    queryReceived.append(" and terminalid ='" + terminalid + "'");
                    queryPayout.append(" and terminalid ='" + terminalid + "'");
                    queryChargeback.append(" and terminalid ='" + terminalid + "'");
                }
                if(functions.isValueNull(currency) && !currency.equals("0"))
                {
                    query.append(" and currency ='" + currency + "'");
                    queryReceived.append(" and currency ='" + currency + "'");
                    queryPayout.append(" and currency ='" + currency + "'");
                    queryChargeback.append(" and currency ='" + currency + "'");
                }

                query.append(" GROUP BY STATUS");
                queryReceived.append(" GROUP BY STATUS");
                queryPayout.append(" GROUP BY STATUS");
                queryChargeback.append(" GROUP BY STATUS");

                log.error("queryyyy--->"+query);
                log.error("queryReceived--->"+queryReceived);
                log.error("queryPayout--->"+queryPayout);
                log.error("queryChargeback--->"+queryChargeback);

                rs= Database.executeQuery(query.toString(),conn);
                int count = 0;
                int arr =0;
                double totalAmount =0.00d;

                while(rs.next())
                {
                    total+=rs.getInt("statusCount");
                    System.out.println("total in servlet---->"+total);
                    arr= Integer.valueOf(rs.getString("statusCount"));
                    sum=sum+arr;

                    totalAmount = Double.parseDouble(rs.getString("amount"));
                    sumAmount=sumAmount+totalAmount;
                    Hashtable innerHash = new Hashtable();
                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("statusCount",rs.getString("statusCount"));
                    innerHash.put("amount",rs.getString("amount"));
                    count++;
                    hash.put(""+count,innerHash);
                }
//                System.out.println("sumAmount:::"+sumAmount);
                rs= Database.executeQuery(queryReceived.toString(),conn);

                count=0;
                while(rs.next())
                {
                    total+=rs.getInt("COUNT(*)");
                    Hashtable innerHash = new Hashtable();
                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("COUNT(*)",rs.getString("COUNT(*)"));
                    innerHash.put("refundamount",rs.getString("refundamount"));
                    count++;
                    hashReceived.put(""+count,innerHash);
                }
                rs= Database.executeQuery(queryPayout.toString(),conn);
                count = 0;
                int arrr =0;
                float totalAmountPayout =0.00f;
                while(rs.next())
                {
                    payoutTotal+=rs.getInt("payoutCount");
                    Hashtable innerHash = new Hashtable();
                    arrr= Integer.parseInt(rs.getString("payoutCount"));
                    totalTransactionPayout=totalTransactionPayout+arrr;

                    totalAmountPayout= Float.parseFloat(rs.getString("amount"));
                    sumAmountPayout=sumAmountPayout+totalAmountPayout;

                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("payoutCount",rs.getString("payoutCount"));
                    innerHash.put("amount",rs.getString("amount"));
                    count++;
                    hashPayout.put(""+count,innerHash);
                }
//                System.out.println("sumAmountPayout:::"+sumAmountPayout);
                rs=Database.executeQuery(queryChargeback.toString(),conn);
                count=0;
                while(rs.next())
                {
                    Hashtable innerHash = new Hashtable();
                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("COUNT(*)",rs.getString("COUNT(*)"));
                    innerHash.put("chargebackamount",rs.getString("chargebackamount"));
                    count++;
                    hashChargeback.put(""+count,innerHash);
                }
            }
        }catch(Exception e)
        {
            log.error("Catch exception :::::"+e);
            e.printStackTrace();
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        req.setAttribute("bankdetails", hash);
        req.setAttribute("queryReceived",hashReceived);
        req.setAttribute("payoutDetails",hashPayout);
        req.setAttribute("chargeback",hashChargeback);
        req.setAttribute("total", String.valueOf(total));
        req.setAttribute("totalTransaction",String.valueOf(sum));
        req.setAttribute("totalTransactionPayout",String.valueOf(totalTransactionPayout));
        req.setAttribute("sumAmount",String.valueOf(sumAmount));
        req.setAttribute("sumAmountPayout",String.valueOf(sumAmountPayout));
        req.setAttribute("payouttotal",String.valueOf(payoutTotal));
        req.setAttribute("terminalid",String.valueOf(terminalid));
        rd.forward(req, res);
    }

    public Boolean datedifference(String startdate, String endDate){
        boolean isvaliddate= false;
        try{

            Date fromDate = stringToDate(startdate);
            Date toDate = stringToDate(endDate);
            LocalDateTime from = LocalDateTime.ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime to= LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault());

            if(Duration.between(from, to).toDays() <= 31){

                isvaliddate= true;
            }
            else {
                isvaliddate= false;
            }
        }
        catch (ParseException e)
        {
            log.error("ParseException--->",e);
        }

        return isvaliddate;
    }

    public static Date stringToDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateStr);
    }

}
