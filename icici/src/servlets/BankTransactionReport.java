import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
import com.manager.dao.PartnerDAO;
import com.manager.dao.TransactionDAO;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.common.core.CommResponseVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 6/2/14
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankTransactionReport extends HttpServlet
{
    private static Logger log = new Logger(BankTransactionReport.class.getName());
    final static ResourceBundle RBundel = LoadProperties.getProperty("com.directi.pg.inquiryCronGateway");
    final static ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.markedforreversalCronGateway");


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in BankTransactionReport");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions = new Functions();
        String startDate = req.getParameter("startdate");
        String startTime = req.getParameter("starttime");
        String endDate = req.getParameter("enddate");
        String endTime = req.getParameter("endtime");
        String gateway = req.getParameter("gateway");
        String merchantid = req.getParameter("merchantid");
        String accoutId = req.getParameter("accountid");
        String terminalId=req.getParameter("terminalid");
        String partnerId=req.getParameter("partnerName");
        String dayLight = req.getParameter("daylightsaving");
        String paymodeid = req.getParameter("paymodeid");
        String cardtypeid = req.getParameter("cardtypeid");
        String action=req.getParameter("button");
        String pgTypeId =getPgTypeId(req.getParameter("gateway"));
        String currency =getCurrency(req.getParameter("gateway"));
        String action1="";
        String action2="";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        String Login=(String)session.getAttribute("username");
        if (functions.isValueNull(req.getParameter("action")))
            action1=req.getParameter("action");

        if(functions.isValueNull(req.getParameter("action1")))
            action2 = req.getParameter("action1");

        Hashtable hash = new Hashtable();
        Hashtable hashRefund = new Hashtable();
        Hashtable hashPayout = new Hashtable();
        String initDateFormat = "dd/MM/yyyy HH:mm:ss";
        String targetDateFormat = "yyyy-MM-dd HH:mm:ss";

        String tablename = "";
        StringBuffer query = new StringBuffer();
        StringBuffer queryRefund = new StringBuffer();
        StringBuffer queryPayout= new StringBuffer();

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        String timeDiffNormal = "";
        String timeDiffDayLight = "";
        String errormsg = "";
        int total = 0;
        int refundtotal = 0;
        int payouttotal=0;

        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ResultSet rs1=null;
        ResultSet rs2=null;

        try
        {
            List<String> errorList=new ArrayList();
            if(!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 25, false)){
                errorList.add("Kindly provide valid Start Date ");
            }
            if(!ESAPI.validator().isValidInput("endDate", endDate, "fromDate", 25, false)){
                errorList.add("Kindly provide valid End Date ");
            }
            if(!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true)){
                errorList.add("Kindly provide valid Start Time  ");
            }
            if(!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true)){
                errorList.add("Kindly provide valid End Date ");
            }
            if(!ESAPI.validator().isValidInput("merchantid", merchantid, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid member ID ");
            }
            if(!ESAPI.validator().isValidInput("accountid", accoutId, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid Account ID ");
            }
            if(!ESAPI.validator().isValidInput("terminalid", terminalId, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid terminal ID ");
            }
            if(!ESAPI.validator().isValidInput("partnerId", partnerId, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid Partner ID");
            }
         /*   if(gateway.equals("0") || functions.isEmptyOrNull(gateway)){
                errorList.add("Kindly provide valid Gateway");
                System.out.println("insdie if");

            }*/
//           if(!functions.isValueNull(pgTypeId) || !ESAPI.validator().isValidInput("pgTypeId", pgTypeId, "Numbers", 10, true)){
//                errorList.add("Kindly provide valid Gateway");
//                System.out.println("insdie else if");
//
//            }

            if (!functions.isValueNull(gateway) && !functions.isValueNull(merchantid))
            {
                errorList.add("Kindly provide valid Gateway or Merchant Id");
            }
            else if ((functions.isValueNull(gateway) && !functions.isValueNull(pgTypeId)) || !ESAPI.validator().isValidInput("pgTypeId", pgTypeId, "Numbers", 10, true)){
                errorList.add("Kindly provide valid Gateway Type");
                System.out.println("insdie else if");
            }
            if(errorList.size()>0){
                errormsg=getErrorMessage(errorList);
                req.setAttribute("errormsg",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/bankTransactionReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            String partnerName=null;
            if(functions.isValueNull(partnerId)){
                PartnerDAO partnerDAO=new PartnerDAO();
                partnerName=partnerDAO.getPartnerName(partnerId);
            }
            Set<String> gatewayTypeSet = getGatewayHash(pgTypeId);

            conn = Database.getConnection();
            String timeQuery = "SELECT time_difference_normal,time_difference_daylight FROM gateway_type WHERE pgtypeid=?";
            pstmt = conn.prepareStatement(timeQuery);
            pstmt.setString(1,pgTypeId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                timeDiffNormal = rs.getString("time_difference_normal");
                timeDiffDayLight = rs.getString("time_difference_daylight");
            }

            if(functions.isValueNull(startTime))
            {
                startDate=formatDate(startDate + " "+startTime,initDateFormat,targetDateFormat);
            }
            else
            {
                if(dayLight.equalsIgnoreCase("y"))
                {
                    startDate=formatDate(startDate + " "+"00:00:00",initDateFormat,targetDateFormat);
                    startDate=addTime(startDate,timeDiffDayLight);
                }
                else
                {
                    startDate=formatDate(startDate + " "+"00:00:00",initDateFormat,targetDateFormat);
                    startDate=addTime(startDate,timeDiffNormal);
                }
            }
            if(functions.isValueNull(endTime))
            {
                endDate=formatDate(endDate + " "+endTime,initDateFormat,targetDateFormat);
            }
            else
            {
                if(dayLight.equalsIgnoreCase("y"))
                {
                    endDate=formatDate(endDate + " "+"23:59:59",initDateFormat,targetDateFormat);
                    endDate=addTime(endDate,timeDiffDayLight);
                }
                else
                {
                    endDate=formatDate(endDate + " "+"23:59:59",initDateFormat,targetDateFormat);
                    endDate=addTime(endDate,timeDiffNormal);
                }
            }
            String gatewayName = gatewayTypeSet.iterator().next();
            String isSupported="";
            String isreversalSupported="";
            try{
                if(RBundel.containsKey(gatewayName))
                isSupported = RBundel.getString(gatewayName);
                if(RBundel1.containsKey(gatewayName))
                isreversalSupported = RBundel1.getString(gatewayName);
            }catch (Exception e){}
            req.setAttribute("gatewayName",gatewayName);
            req.setAttribute("InquirySupported",isSupported);
            req.setAttribute("ReversalSupported",isreversalSupported);



            List<TransactionDetailsVO> trackinglist = null;

            if (action1.equalsIgnoreCase("settledAll"))
            {
                try
                {
                    trackinglist = getTrackingIDTransactionList("capturesuccess", accoutId,terminalId, startTime, startDate,endTime,endDate, merchantid, gatewayName,currency);
                    log.error("List-----" + trackinglist.size());
                    req.setAttribute("trackinglist", trackinglist);
                    RequestDispatcher rd = req.getRequestDispatcher("/detailsOfBankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (Exception e)
                {
                    log.error("Exception while update---", e);
                }
            }
            else if (action1.equalsIgnoreCase("captureAll"))
            {
                try
                {
                    trackinglist = getTrackingIDTransactionList("settled", accoutId,terminalId, startTime, startDate,endTime,endDate, merchantid, gatewayName,currency);
                    log.error("List-----" + trackinglist.size());
                    req.setAttribute("trackinglist", trackinglist);
                    RequestDispatcher rd = req.getRequestDispatcher("/detailsOfBankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (Exception e)
                {
                    log.error("Exception while update---", e);
                }
            }

            String trackingid="";
            String qry="";
            String status = "";
            int j=0;
            TransactionDAO transactionDAO = new TransactionDAO();
            TransactionDetailsVO transactionDetailsVO=new TransactionDetailsVO();
            ActionEntry entry = new ActionEntry();
            CommResponseVO commResponseVO = null;
            String role="Admin";
            String username=(String)session.getAttribute("username");
            String actionExecutorId=(String)session.getAttribute("merchantid");
            String actionExecutorName=role+"-"+username;
            if (action2.equalsIgnoreCase("updatesettled"))
            {
                try
                {
                    String id1 = req.getParameter("ids");
                    trackingid=id1;
                    List<String> trackingidList = new ArrayList<>();

                    if (id1.contains(","))
                    {
                        trackingidList = Arrays.asList(id1.split(","));
                    }
                    else
                    {
                        trackingidList = Arrays.asList(id1.split(" "));
                    }

                    int i=0;
                    for (String id : trackingidList)
                    {
                        transactionDetailsVO = transactionDAO.getDetailFromCommon(id);
                        boolean statusyes = false;
                        if (transactionDetailsVO.getStatus().equals("capturesuccess"))
                        {
                            qry = "UPDATE transaction_common SET STATUS='settled' WHERE trackingid in (" + id + ")";
                            pstmt = conn.prepareStatement(qry);
                            j = pstmt.executeUpdate();
                            if (j > 0)
                            {
                                statusyes = transactionDAO.isTransactionDetailInserted(id, ActionEntry.STATUS_CREDIT, transactionDetailsVO.getAmount(), actionExecutorId, actionExecutorName);
                                log.error("inserted into details table----" + statusyes);
                            }
                            if (statusyes == true)
                            {
                                i++;
                            }
                        }
                    }

                    if (i > 0)
                    {
                        status = "Total "+i+" records Settled.";
                        String remoteAddr = Functions.getIpAddress(req);
                        int serverPort = req.getServerPort();
                        String servletPath = req.getServletPath();
                        String onchanged = "List Of Settled Tracking ID's="+id1;
                        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                        activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                        activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                        activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                        activityTrackerVOs.setAction(ActivityLogParameters.SETTLED.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.BANK_TRANSACTION_REPORT.toString());
                        activityTrackerVOs.setLable_values(onchanged);
                        activityTrackerVOs.setDescription(ActivityLogParameters.GATEWAY.toString() + "-" + gateway );
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(header);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception e)
                        {
                            log.error("Exception while AsyncActivityLog::::", e);
                        }
                    }
                    else
                    {
                        status = "Unable to Settled "+i+" transactions.";
                    }
                    log.error("Status-----" + status);
                    req.setAttribute("status", status);
                    RequestDispatcher rd = req.getRequestDispatcher("/detailsOfBankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (Exception e)
                {
                    log.error("Exception while update---", e);
                }
                List<TransactionDetailsVO> list = getUpdatedTransactionList(trackingid);
                HashMap rfMail = new HashMap();
                rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, getUpdatedList(list));

                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.ADMIN_SETTLEMENT_REPORT, rfMail);
            }
            else if(action2.equalsIgnoreCase("updatecapture"))
            {
                try
                {
                    String id1 = req.getParameter("ids1");
                    trackingid=id1;
                    List<String> trackingidList = new ArrayList<>();

                    if (id1.contains(","))
                    {
                        trackingidList = Arrays.asList(id1.split(","));
                    }
                    else
                    {
                        trackingidList = Arrays.asList(id1.split(" "));
                    }

                    int i=0;
                    for (String id : trackingidList)
                    {
                        transactionDetailsVO = transactionDAO.getDetailFromCommon(id);
                        boolean statusyes=false;
                        qry ="UPDATE transaction_common SET STATUS='capturesuccess' WHERE trackingid in ("+id+")";
                        pstmt = conn.prepareStatement(qry);
                        j = pstmt.executeUpdate();
                        if (j > 0)
                        {
                            statusyes=transactionDAO.isTransactionDetailInserted(id, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transactionDetailsVO.getAmount(), actionExecutorId, actionExecutorName);
                        }
                        if(statusyes == true){
                            i++;
                        }
                    }
                    if (i > 0)
                    {
                        status = "Total "+i+" records Captured.";
                        String remoteAddr = Functions.getIpAddress(req);
                        int serverPort = req.getServerPort();
                        String servletPath = req.getServletPath();
                        String onchanged = "List Of Captured Tracking ID's="+id1;
                        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                        activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                        activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                        activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                        activityTrackerVOs.setAction(ActivityLogParameters.CAPTURED.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.BANK_TRANSACTION_REPORT.toString());
                        activityTrackerVOs.setLable_values(onchanged);
                        activityTrackerVOs.setDescription(ActivityLogParameters.GATEWAY.toString() + "-" + gateway );
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(header);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception e)
                        {
                            log.error("Exception while AsyncActivityLog::::", e);
                        }
                    }
                    else
                    {
                        status = "Unable to Captured "+i+" transactions.";
                    }
                    log.error("Status-----" +status);
                    req.setAttribute("status", status);
                    RequestDispatcher rd = req.getRequestDispatcher("/detailsOfBankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                }
                catch (Exception e)
                {
                    log.error("Exception while update---", e);
                }
                List<TransactionDetailsVO> list = getUpdatedTransactionList(trackingid);
                HashMap rfmail = new HashMap();
                rfmail.put(MailPlaceHolder.MULTIPALTRANSACTION, getUpdatedList(list));

                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.ADMIN_SETTLEMENT_REPORT, rfmail);
            }

            if(action.equalsIgnoreCase("search"))
            {
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String fields = "";
                String rFields = "";
                String pFields = "";
                tablename = Database.getTableName(gatewayName);

                fields = "STATUS,COUNT(*),SUM(amount) as amount,SUM(captureamount) as captureamount";
                rFields = "STATUS,COUNT(*),SUM(amount) as amount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount";
                pFields = "STATUS, COUNT(*),SUM(amount) as amount, SUM(payoutamount) as payoutamount";
                query.append("select " + fields + " from " + tablename + " where ");
                queryRefund.append("select " + rFields + " from " + tablename + " where ");
                queryPayout.append("select " + pFields + " from " + tablename + " where ");

                if (functions.isValueNull(startDate))
                {
                    long startdtstamp=simpleDateFormat.parse(startDate).getTime()/1000;
                    query.append(" dtstamp >='" + startdtstamp + "'");
                    queryPayout.append(" dtstamp >='" + startdtstamp + "'");
                    queryRefund.append(" TIMESTAMP>='" + startDate + "'");

                }
                if (functions.isValueNull(endDate))
                {
                    long enddtstamp=simpleDateFormat.parse(endDate).getTime()/1000;
                    query.append(" and dtstamp <='" + enddtstamp + "'and STATUS NOT IN('payoutstarted','payoutsuccessful','payoutfailed')");

                    queryRefund.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN('reversed','chargeback','chargebackreversed') ");

                    queryPayout.append(" and dtstamp <='" + enddtstamp + "' and STATUS IN('payoutstarted','payoutsuccessful','payoutfailed') ");

                }
                if (functions.isValueNull(partnerName))
                {
                    query.append(" and totype ='" + partnerName + "'");
                    queryRefund.append(" and totype ='" + partnerName + "'");
                    queryPayout.append(" and totype='"+ partnerName + "'");
                }
                if (functions.isValueNull(accoutId) && !accoutId.equals("0"))
                {
                    query.append(" and accountid =" + accoutId);
                    queryRefund.append(" and accountid =" + accoutId);
                    queryPayout.append(" and accountid =" + accoutId);
                }
                if (functions.isValueNull(pgTypeId) && !pgTypeId.equals("0"))
                {
                    query.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    queryRefund.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    queryPayout.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                }
                if (functions.isValueNull(paymodeid) && !paymodeid.equals("0"))
                {
                    query.append(" and paymodeid=" + paymodeid);
                    queryRefund.append(" and paymodeid=" + paymodeid);
                    queryPayout.append(" and paymodeid=" + paymodeid);
                }
                if (functions.isValueNull(cardtypeid) && !cardtypeid.equals("0"))
                {
                    query.append(" and cardtypeid=" + cardtypeid);
                    queryRefund.append(" and cardtypeid=" + cardtypeid );
                    queryPayout.append(" and cardtypeid=" + cardtypeid );
                }
                if (functions.isValueNull(merchantid) && !merchantid.equals("0"))
                {
                    query.append(" and toid =" + merchantid);
                    queryRefund.append(" and toid =" + merchantid);
                    queryPayout.append(" and toid =" + merchantid);
                }
                if (functions.isValueNull(terminalId) && !terminalId.equals("0"))
                {
                    query.append(" and terminalid =" + terminalId);
                    queryRefund.append(" and terminalid =" + terminalId);
                    queryPayout.append(" and terminalid =" + terminalId);
                }

                query.append(" GROUP BY STATUS");
                queryRefund.append(" GROUP BY STATUS");
                queryPayout.append(" GROUP BY STATUS");

                log.error("select query..." + query);
                log.error("refund query..." + queryRefund);
                log.error("payout queryyyyy---"+queryPayout);

                /*hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
                hashRefund = Database.getHashFromResultSet(Database.executeQuery(queryRefund.toString(), conn));*/
                rs=Database.executeQuery(query.toString(),conn);
                int count = 0;
                while (rs.next())
                {
                    total+=rs.getInt("COUNT(*)");
                    Hashtable innerHash = new Hashtable();
                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("COUNT(*)",rs.getString("COUNT(*)"));
                    innerHash.put("amount",rs.getString("amount"));
                    innerHash.put("captureamount",rs.getString("captureamount"));
                    count++;
                    hash.put(""+count,innerHash);
                }
                rs=Database.executeQuery(queryRefund.toString(),conn);
                count=0;
                while (rs.next())
                {
                    refundtotal+=rs.getInt("COUNT(*)");
                    Hashtable innerHash = new Hashtable();
                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("COUNT(*)",rs.getString("COUNT(*)"));
                    innerHash.put("amount",rs.getString("amount"));
                    innerHash.put("refundamount",rs.getString("refundamount"));
                    innerHash.put("chargebackamount",rs.getString("chargebackamount"));
                    count++;
                    hashRefund.put(""+count,innerHash);
                }
                rs=Database.executeQuery(queryPayout.toString(),conn);
                count=0;
                while(rs.next())
                {
                    payouttotal+=rs.getInt("COUNT(*)");
                    Hashtable innerHash = new Hashtable();
                    innerHash.put("STATUS",rs.getString("STATUS"));
                    innerHash.put("COUNT(*)",rs.getString("COUNT(*)"));
                    innerHash.put("amount",rs.getString("amount"));
                    innerHash.put("payoutamount",rs.getString("payoutamount"));
                    count++;
                    hashPayout.put(""+count,innerHash);
                }
            }
        }
        catch(SQLException se){
            log.error("SQL Exception",se);
        }
        catch (SystemError systemError){
            log.error("System exception",systemError);
        }
        catch (ParseException pe){
            log.error("ParseException:::::",pe);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closeResultSet(rs1);
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        String timediff= "";
        if("Y".equalsIgnoreCase(dayLight)){
            timediff=timeDiffDayLight;
        }
        else
        {
            timediff=timeDiffNormal;
        }

        req.setAttribute("timediff",timediff);
        req.setAttribute("bankdetails", hash);
        req.setAttribute("bankrefunddetails", hashRefund);
        req.setAttribute("bankpayoutdetails", hashPayout);
        req.setAttribute("startdate",startDate);
        req.setAttribute("starttime",startTime);
        req.setAttribute("enddate",endDate);
        req.setAttribute("endtime",endTime);
        req.setAttribute("gateway",gateway);
        req.setAttribute("merchantid",merchantid);
        req.setAttribute("terminalid",terminalId);
        req.setAttribute("total", String.valueOf(total));
        req.setAttribute("refundtotal", String.valueOf(refundtotal));
        req.setAttribute("payouttotal",String.valueOf(payouttotal));
        RequestDispatcher rd = req.getRequestDispatcher("/bankTransactionReport.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("all"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {
            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }
    public String getErrorMessage( List<String> list)
    {
        StringBuffer errorMessage=new StringBuffer();

        for(String message:list)
        {
            if(errorMessage.length()>0)
            {
                errorMessage.append(", ");
            }
            errorMessage.append(message);

        }
        return errorMessage.toString();
    }
    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {

        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public String addTime(String date,String time) throws ParseException
    {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(simpleDateFormat.parse(date));

        String timeArr[]=time.split(":");

        String hour=timeArr[0];
        String min=timeArr[1];
        String sec=timeArr[2];

        calendar.add(Calendar.HOUR,Integer.parseInt(hour));
        calendar.add(Calendar.MINUTE,Integer.parseInt(min));
        calendar.add(Calendar.SECOND,Integer.parseInt(sec));

        String newDate=simpleDateFormat.format(calendar.getTime());

        return  newDate;
    }
    public String getPgTypeId(String gatewayString){
        String pgTypeId = "";
        Functions functions=new Functions();
        if(functions.isValueNull(gatewayString)){
            String aGatewaySet[] = gatewayString.split("-");
            if (aGatewaySet.length == 3){
                pgTypeId = aGatewaySet[2];
            }
        }
        return pgTypeId;
    }
    public String getCurrency(String gatewayString){
        String currency = "";
        Functions functions=new Functions();
        if(functions.isValueNull(gatewayString)){
            String aGatewaySet[] = gatewayString.split("-");
            if (aGatewaySet.length == 3){
                currency = aGatewaySet[1];
            }
        }
        return currency;
    }

    public List<TransactionDetailsVO> getUpdatedTransactionList(String trackingid)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query3 = new StringBuffer("SELECT trackingid,STATUS,toid,terminalid FROM transaction_common WHERE trackingid IN ("+trackingid+")");
            pstmt = conn.prepareStatement(query3.toString());
            log.debug("Ouery3-----" + pstmt);
            ResultSet resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (Exception e)
        {
            log.error("Exception while updating----", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOList;
    }

    public String getUpdatedList(List<TransactionDetailsVO> transactionDetailsVOList)
    {
        StringBuffer table=new StringBuffer();
        String style="";

        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#7eccad\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingID</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#7eccad\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Terminal ID</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#7eccad\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Toid</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#7eccad\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";
        style=currentStyle;
        for(TransactionDetailsVO transactionDetailsVO:transactionDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getTrackingid()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getTerminalId()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getToid()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getStatus()+"</td>");
            table.append("</tr>");
        }
        return table.toString();
    }

    public List<TransactionDetailsVO> getTrackingIDTransactionList(String toStatus, String accountId,String terminalId, String startTime, String startDate, String endTime, String endDate, String memberId, String fromType,String currency)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        Functions functions = new Functions();
        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query1 = new StringBuffer("SELECT trackingid,accountid,toid,terminalid,STATUS FROM transaction_common WHERE fromtype=? AND currency=? AND STATUS=? AND FROM_UNIXTIME(dtstamp) >=? AND FROM_UNIXTIME(dtstamp) <=?");
            if(functions.isValueNull(memberId))
            {
                query1.append(" and toid ="+memberId);
            }
            if(functions.isValueNull(accountId))
            {
                query1.append(" and accountid ="+accountId);
            }
            if(functions.isValueNull(terminalId))
            {
                query1.append(" and terminalid ="+terminalId);
            }

            pstmt = conn.prepareStatement(query1.toString());
            pstmt.setString(1, fromType);
            pstmt.setString(2, currency);
            pstmt.setString(3, toStatus);
            pstmt.setString(4, startDate);
            pstmt.setString(5, endDate);
            log.debug("Ouery1-----" + pstmt);
            ResultSet resultSet=pstmt.executeQuery();
            while(resultSet.next())
            {
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }
        catch (Exception e)
        {
            log.error("Exception while updating----", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOList;
    }
}
