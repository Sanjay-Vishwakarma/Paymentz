package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
import com.manager.dao.PartnerDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Diksha on 24-Aug-20.
 */
@WebServlet(name = "BankTransactionReport")
public class BankTransactionReport extends HttpServlet
{
    private static Logger log = new Logger(BankTransactionReport.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.inquiryCronGateway");
    final static ResourceBundle RB2 = LoadProperties.getProperty("com.directi.pg.markedforreversalCronGateway");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Inside BankTransactionReport----");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        Functions functions = new Functions();
        String action=request.getParameter("button");
        String gateway = request.getParameter("gateway");
        String startDate = request.getParameter("fromdate");
        String endDate = request.getParameter("todate");
        String startTime = request.getParameter("starttime");
        String endTime = request.getParameter("endtime");
        String pgTypeId = getPgTypeId(request.getParameter("gateway"));
        String accountId = request.getParameter("accountid");
        String terminalId = request.getParameter("terminalid");
        String currency = getCurrency(request.getParameter("gateway"));
        String dayLight = request.getParameter("daylightsaving");
        String merchantid = request.getParameter("memberid");
        String errormsg = "";
        String timeDiffNormal = "";
        String timeDiffDayLight = "";
        String initDateFormat = "dd/MM/yyyy HH:mm:ss";
        String targetDateFormat = "yyyy-MM-dd HH:mm:ss";
        String error = "";
        RequestDispatcher rd = request.getRequestDispatcher("/bankTransactionReport.jsp?ctoken=" + user.getCSRFToken());

        String pid = request.getParameter("pid");
        String partnerid=(String)session.getAttribute("merchantid");
        String partner_id = "";
        try
        {
            if (functions.isValueNull(pid) && partner.isPartnerSuperpartnerMapped(pid, partnerid))
            {
                partner_id = pid;
            }
            else if (functions.isValueNull(pid))
            {
                String Roles = partner.getRoleofPartner(partnerid);
                if (Roles.contains("superpartner"))
                {
                    LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
                    memberHash =partner.getPartnerDetails(partnerid);
                    partner_id = partnerid;
                    for(int partnerID : memberHash.keySet())
                    {
                        partner_id+= "," + Integer.toString(partnerID);
                    }
                }
                else
                {
                    partner_id = partnerid;
                }
            }
            else
            {
                request.setAttribute("error", "Invalid Partner Mapping");
                rd = request.getRequestDispatcher("/bankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }catch (Exception e){
            log.error("Exception---"+e);
        }

        try
        {
            List<String> errorList=new ArrayList();
            if(!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 25, false)){
                errorList.add("Kindly provide valid Start Date ");
            }
            if(!ESAPI.validator().isValidInput("endDate", startDate, "toDate", 25, false)){
                errorList.add("Kindly provide valid End Date ");
            }
            if(!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true)){
                errorList.add("Kindly provide validStart Time  ");
            }
            if(!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true)){
                errorList.add("Kindly provide valid End Date ");
            }
            if(!ESAPI.validator().isValidInput("merchantid", merchantid, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid member ID ");
            }
            if(!ESAPI.validator().isValidInput("accountid", accountId, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid Account ID ");
            }
            if(!ESAPI.validator().isValidInput("terminalid", terminalId, "OnlyNumber", 10, true)){
                errorList.add("Kindly provide valid terminal ID ");
            }
            if(!ESAPI.validator().isValidInput("partnerId", partner_id, "SafeString", 10, true)){
                errorList.add("Kindly provide Partner ID");
            }
            if(gateway.equals("0") || functions.isEmptyOrNull(gateway)){
                errorList.add("Kindly provide valid Gateway");
            }
            else if(!functions.isValueNull(pgTypeId) || !ESAPI.validator().isValidInput("pgTypeId", pgTypeId, "Numbers", 10, true)){
                errorList.add("Kindly provide valid Gateway");
            }
            if(errorList.size()>0){
                errormsg=getErrorMessage(errorList);
                request.setAttribute("errormsg",errormsg);
                rd = request.getRequestDispatcher("/bankTransactionReport.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
            log.error("Exception:::", e);
        }

        Set<String> gatewayTypeSet = getGatewayHash(pgTypeId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String timeQuery = "SELECT time_difference_normal,time_difference_daylight FROM gateway_type WHERE pgtypeid=?";
            pstmt = conn.prepareStatement(timeQuery);
            pstmt.setString(1, pgTypeId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                timeDiffNormal = rs.getString("time_difference_normal");
                timeDiffDayLight = rs.getString("time_difference_daylight");
            }
        }
        catch (SQLException e)
        {
            log.error("Exception:::", e);
        }
        catch (SystemError se)
        {
            log.error("SystemError:::", se);
        }

        try
        {
            if (functions.isValueNull(startTime))
            {
                try
                {
                    startDate = formatDate(startDate + " " + startTime, initDateFormat, targetDateFormat);
                }
                catch (ParseException e)
                {
                    log.error("Exception:::",e);
                }
            }
            else
            {
                if (dayLight.equalsIgnoreCase("y"))
                {
                    startDate = formatDate(startDate + " " + "00:00:00", initDateFormat, targetDateFormat);
                    startDate = addTime(startDate, timeDiffDayLight);
                }
                else
                {
                    startDate = formatDate(startDate + " " + "00:00:00", initDateFormat, targetDateFormat);
                    startDate = addTime(startDate, timeDiffNormal);
                }
            }

            if (functions.isValueNull(endTime))
            {
                endDate = formatDate(endDate + " " + endTime, initDateFormat, targetDateFormat);
            }
            else
            {
                if (dayLight.equalsIgnoreCase("y"))
                {
                    endDate = formatDate(endDate + " " + "23:59:59", initDateFormat, targetDateFormat);
                    endDate = addTime(endDate, timeDiffDayLight);
                }
                else
                {
                    endDate = formatDate(endDate + " " + "23:59:59", initDateFormat, targetDateFormat);
                    endDate = addTime(endDate, timeDiffNormal);
                }
            }

        }
        catch (ParseException e)
        {
            log.error("Exception:::",e);
        }

        String gatewayName = gatewayTypeSet.iterator().next();
        String isSupported="";
        String isreversalSupported="";
        try
        {
            isSupported = RB.getString(gatewayName);
            isreversalSupported = RB2.getString(gatewayName);
        }
        catch (Exception e){}

        request.setAttribute("gatewayName",gatewayName);
        request.setAttribute("InquirySupported",isSupported);
        request.setAttribute("ReversalSupported",isreversalSupported);


        ResultSet rs1=null;
        ResultSet rs2=null;
        ResultSet rs3=null;
        String fields = "";
        String pfields="";
        String rFields = "";
        String tablename = "";
        String total = "";
        String refundtotal = "";
        String payoutTotal = "";
        Hashtable hash = new Hashtable();
        Hashtable hashRefund = new Hashtable();
        Hashtable hashPayout = new Hashtable();
        PartnerDAO partnerDAO=new PartnerDAO();
        StringBuffer query = new StringBuffer();
        StringBuffer queryRefund = new StringBuffer();
        StringBuffer countquery = new StringBuffer();
        StringBuffer refundcountquery = new StringBuffer();

        StringBuffer querypfields = new StringBuffer();
        StringBuffer countquerypfields = new StringBuffer();

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        tablename = Database.getTableName(gatewayName);

        String partnerName="";
        partnerName=partnerDAO.getPartnerName(partner_id );

        try
        {
            if(functions.isValueNull(action) && action.equalsIgnoreCase("search"))
            {
                fields = "STATUS,COUNT(*),SUM(amount) as amount,SUM(captureamount) as captureamount";
                pfields = "STATUS,COUNT(*),SUM(amount) as amount,SUM(payoutamount) as payoutamount";
                rFields = "STATUS,COUNT(*),SUM(amount) as amount,SUM(refundamount) as refundamount,SUM(chargebackamount) as chargebackamount";

                query.append("select " + fields + " from " + tablename + " where ");
                countquery.append("select count(*) as count from " + tablename + " where ");
                queryRefund.append("select " + rFields + " from " + tablename + " where ");
                refundcountquery.append("select count(*) as count from " + tablename + " where ");
                countquerypfields.append("select count(*) as count from " + tablename + " where ");
                querypfields.append("select " + pfields + " from " + tablename + " where ");

                if (functions.isValueNull(startDate))
                {
                    query.append(" FROM_UNIXTIME(dtstamp) >='" + startDate + "'");
                    countquery.append(" FROM_UNIXTIME(dtstamp) >='" + startDate + "'");
                    queryRefund.append(" TIMESTAMP>='" + startDate + "'");
                    refundcountquery.append(" TIMESTAMP>='" + startDate + "'");
                    countquerypfields.append(" TIMESTAMP>='" + startDate + "'");
                    querypfields.append(" TIMESTAMP>='" + startDate + "'");
                }
                if (functions.isValueNull(endDate))
                {
                    query.append(" and FROM_UNIXTIME(dtstamp) <='" + endDate + "' and STATUS NOT IN('payoutstarted','payoutsuccessful','payoutfailed')");
                    countquery.append(" and FROM_UNIXTIME(dtstamp) <='" + endDate + "' and STATUS NOT IN('payoutstarted','payoutsuccessful','payoutfailed')");
                    queryRefund.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN('reversed','chargeback') ");
                    refundcountquery.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN('reversed','chargeback') ");
                    querypfields.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN('payoutstarted','payoutsuccessful','payoutfailed') ");
                    countquerypfields.append(" and TIMESTAMP <='" + endDate + "' and STATUS IN('payoutstarted','payoutsuccessful','payoutfailed') ");


                }
                if (functions.isValueNull(partnerName))
                {
                    query.append(" and totype ='" + partnerName + "'");
                    countquery.append(" and totype ='" + partnerName + "'");
                    queryRefund.append(" and totype ='" + partnerName + "'");
                    refundcountquery.append(" and totype ='" + partnerName + "'");
                    querypfields.append(" and totype ='" + partnerName + "'");
                    countquerypfields.append(" and totype ='" + partnerName + "'");
                }
                if (functions.isValueNull(accountId) && !accountId.equals("0"))
                {
                    query.append(" and accountid =" + accountId);
                    countquery.append(" and accountid =" + accountId);
                    queryRefund.append(" and accountid =" + accountId);
                    refundcountquery.append(" and accountid =" + accountId);
                    querypfields.append(" and accountid =" + accountId);
                    countquerypfields.append(" and accountid =" + accountId);
                }
                if (functions.isValueNull(pgTypeId) && !pgTypeId.equals("0"))
                {
                    query.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    countquery.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    queryRefund.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    refundcountquery.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    querypfields.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                    countquerypfields.append(" and fromtype='" + gatewayName + "' and currency='" + currency + "' ");
                }
                if (functions.isValueNull(merchantid) && !merchantid.equals("0"))
                {
                    query.append(" and toid =" + merchantid);
                    countquery.append(" and toid =" + merchantid);
                    queryRefund.append(" and toid =" + merchantid);
                    refundcountquery.append(" and toid =" + merchantid);
                    querypfields.append(" and toid =" + merchantid);
                    countquerypfields.append(" and toid =" + merchantid);
                }
                if (functions.isValueNull(terminalId) && !terminalId.equals("0"))
                {
                    query.append(" and terminalid =" + terminalId);
                    countquery.append(" and terminalid =" + terminalId);
                    queryRefund.append(" and terminalid =" + terminalId);
                    refundcountquery.append(" and terminalid =" + terminalId);
                    querypfields.append(" and terminalid =" + terminalId);
                    countquerypfields.append(" and terminalid =" + terminalId);
                }

                query.append(" GROUP BY STATUS");
                queryRefund.append(" GROUP BY STATUS");
                querypfields.append(" GROUP BY STATUS");

                log.debug("select query..." + query);
                log.debug("count query..." + countquery);
                log.debug("refund query..." + queryRefund);
                log.debug("refund query..." + refundcountquery);
                log.debug("payout query..." + countquerypfields);


                hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
                hashRefund = Database.getHashFromResultSet(Database.executeQuery(queryRefund.toString(), conn));
                hashPayout = Database.getHashFromResultSet(Database.executeQuery(querypfields.toString(), conn));
                rs1 = Database.executeQuery(countquery.toString(), conn);
                rs2 = Database.executeQuery(refundcountquery.toString(), conn);
                rs3 = Database.executeQuery(countquerypfields.toString(), conn);
                if (rs1.next())
                {
                    total = rs1.getString("count");
                }

                if (rs2.next())
                {
                    refundtotal = rs2.getString("count");
                }
                if (rs3.next())
                {
                    payoutTotal = rs3.getString("count");
                }
            }
        }
        catch (SQLException e)
        {
            log.error("SQL Exception:::", e);
        }
        catch (SystemError se)
        {
            log.error("SystemError:::",se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs1);
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        String timediff= "";
        if("Y".equalsIgnoreCase(dayLight))
        {
            timediff=timeDiffDayLight;
        }
        else
        {
            timediff=timeDiffNormal;
        }
        request.setAttribute("timediff",timediff);
        request.setAttribute("startdate",startDate);
        request.setAttribute("starttime",startTime);
        request.setAttribute("enddate",endDate);
        request.setAttribute("endtime",endTime);
        request.setAttribute("gateway",gateway);
        request.setAttribute("bankdetails", hash);
        request.setAttribute("bankrefunddetails", hashRefund);
        request.setAttribute("payoutdetails", hashPayout);
        request.setAttribute("payouttotal", payoutTotal);
        request.setAttribute("merchantid",merchantid);
        request.setAttribute("terminalid",terminalId);
        request.setAttribute("total", total);
        request.setAttribute("refundtotal", refundtotal);
        rd = request.getRequestDispatcher("/bankTransactionReport.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);


    }


    public String getPgTypeId(String gatewayString)
    {
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
    public String getCurrency(String gatewayString)
    {
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
}
