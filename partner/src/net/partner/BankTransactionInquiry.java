package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Diksha on 28-Aug-20.
 */
@WebServlet(name = "BankTransactionInquiry")
public class BankTransactionInquiry extends HttpServlet
{
    TransactionLogger transactionLogger     = new TransactionLogger(BankTransactionInquiry.class.getName());
    Logger logger                           = new Logger(BankTransactionInquiry.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("Inside BankTransactionInquiry ---");
        HttpSession session             = req.getSession();
        User user                       = (User) session.getAttribute("ESAPIUserSessionKey");
        CommonBankTransactionInquiry commonBankTransactionInquiry   = new CommonBankTransactionInquiry();
        PartnerFunctions partner                = new PartnerFunctions();
        ActivityTrackerVOs activityTrackerVOs   = new ActivityTrackerVOs();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        Functions functions = new Functions();
        String errormsg = "";
        int totalAuthstartedCount=0;
        int successCounter=0;
        int failCounter=0;

        String ctoken = req.getParameter("ctoken");
        String startDate = req.getParameter("fromdate");
        String startTime = req.getParameter("starttime")!=null?req.getParameter("starttime"):"";
        String endDate = req.getParameter("todate");
        String pid = req.getParameter("partnerName");
        String endTime = req.getParameter("endtime")!=null?req.getParameter("endtime"):"";
        String gateway = req.getParameter("gateway")!=null?req.getParameter("gateway"):"";
        String memberId = req.getParameter("memberid")!=null?req.getParameter("memberid"):"";
        String accountId = req.getParameter("accountid")!=null?req.getParameter("accountid"):"";
        String curr = req.getParameter("currency")!=null?req.getParameter("currency"):"";
        String status = req.getParameter("status")!=null?req.getParameter("status"):"";
        String dayLight = req.getParameter("daylightsaving");
        StringBuffer onchangedValues = new StringBuffer();
        String ActivityAction = req.getParameter("ActivityAction");

        String terminalId="";

        List<String> errorList=new ArrayList();
        if(!ESAPI.validator().isValidInput("fromdate", startDate, "fromDate", 25, false)){
            errorList.add("Kindly provide valid Start Date ");
        }else{
            onchangedValues.append("Start Date="+startDate);
        }
        if(!ESAPI.validator().isValidInput("todate", endDate, "toDate", 25, false)){
            errorList.add("Kindly provide valid End Date ");
        }else{
            onchangedValues.append("End Date=" + endDate);
        }
        if(!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true)){
            errorList.add("Kindly provide valid Start Time  ");
        }else{
            onchangedValues.append("Start Time=" + startTime);
        }
        if(!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true)){
            errorList.add("Kindly provide valid End Time ");
        }else{
            onchangedValues.append("End Time=" + endTime);
        }
        if(!ESAPI.validator().isValidInput("memberid", memberId, "OnlyNumber", 10, true)){
            errorList.add("Kindly provide valid Member ID ");
        }else{
            if(functions.isValueNull(memberId))
            onchangedValues.append("Member ID=" + memberId);
        }
        if(!ESAPI.validator().isValidInput("accountid", accountId, "OnlyNumber", 10, true)){
            errorList.add("Kindly provide valid Account ID ");
        }else{
            if(functions.isValueNull(accountId))
            onchangedValues.append("Account ID=" + accountId);
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalId, "OnlyNumber", 10, true)){
            errorList.add("Kindly provide valid terminal ID ");
        }else{
            if(functions.isValueNull(terminalId))
                onchangedValues.append("Terminal ID=" + terminalId);
        }

        String pgTypeId =getPgTypeId(req.getParameter("gateway"));

        if(gateway.equals("0") || functions.isEmptyOrNull(gateway) || functions.isEmptyOrNull(pgTypeId)){
            errorList.add("Kindly provide valid Gateway");
        }else{
            onchangedValues.append("Gateway=" + gateway);
        }
        if(errorList.size()>0){
            errormsg=getErrorMessage(errorList);
            req.setAttribute("errormsg",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/bankTransactionReport.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        String gatewayName=getGatewayName(pgTypeId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            startDate = sdf.format(sdf2.parse(startDate));
            endDate = sdf.format(sdf2.parse(endDate));
        }catch (Exception e){
            transactionLogger.error("Exception --",e);
        }

        startDate=startDate+" "+startTime;
        endDate=endDate+" "+endTime;

        transactionLogger.error("gatewayName-------"+gatewayName);
        HashMap responsemap=commonBankTransactionInquiry.commomTransactionInquiryStatusPartner("", status, terminalId, accountId, gatewayName, curr, memberId, startDate, endDate, pid);
        totalAuthstartedCount= (int) responsemap.get("authstartedCount");
        successCounter= (int) responsemap.get("successCounter");
        failCounter= (int) responsemap.get("failCounter");
        transactionLogger.error("redirecting to BankTransactionReport");
        transactionLogger.error("CTOKEN = "+ctoken);
        int authStarted = totalAuthstartedCount-successCounter;
        authStarted=authStarted-failCounter;
        RequestDispatcher rd = req.getRequestDispatcher("/bankTransactionReportDetails.jsp?ctoken=" + user.getCSRFToken());
        onchangedValues.append(",authstartedCount=" + totalAuthstartedCount+",failCounter="+failCounter+",successCounter="+successCounter);
        transactionLogger.error("authStarted--------------"+authStarted);
        transactionLogger.error("successCounter--------------"+successCounter);
        transactionLogger.error("failCounter--------------"+failCounter);

        Set<String> gatewayTypeSet = getGatewayHash(pgTypeId);

        String timeDiffNormal= "";
        String timeDiffDayLight= "";
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
            logger.error("Exception:::", e);
        }
        catch (SystemError se)
        {
            logger.error("SystemError:::", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        String timediff= "";
        if("Y".equalsIgnoreCase(dayLight))
        {
            timediff=timeDiffNormal;
        }
        else
        {
            timediff=timeDiffDayLight;
        }
        transactionLogger.error("Creating Activity for edit Gatway account");
        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String Login=user.getAccountName();
        String actionExecutorId=(String) session.getAttribute("merchantid");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String activityrole="";
        /*String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("partner")){
            activityrole=ActivityLogParameters.PARTNER.toString();
        }
        else if(Roles.contains("childsuperpartner"))
        {
            activityrole=ActivityLogParameters.CHILEDSUPERPARTNER.toString();
        }else if(Roles.contains("superpartner"))
        {
            activityrole=ActivityLogParameters.SUPERPARTNER.toString();
        }else if(Roles.contains("subpartner"))
        {
            activityrole=ActivityLogParameters.SUBPARTNER.toString();
        }*/
        if(functions.isValueNull(onchangedValues.toString()))
        {
            activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
            activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
            activityTrackerVOs.setRole(partner.getUserRole(user));
            activityTrackerVOs.setAction(ActivityAction);
            activityTrackerVOs.setModule_name(ActivityLogParameters.BANK_TRANSACTION_REPORT.toString());
            activityTrackerVOs.setLable_values(onchangedValues.toString());
            activityTrackerVOs.setDescription(ActivityLogParameters.GATEWAY.toString() + "-" + gateway );
            activityTrackerVOs.setIp(remoteAddr);
            activityTrackerVOs.setHeader(header);
            activityTrackerVOs.setPartnerId(actionExecutorId);

            try
            {
                AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                asyncActivityTracker.asyncActivity(activityTrackerVOs);
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception while AsyncActivityLog::::", e);
            }
        }
        req.setAttribute("timediff",timediff);
        req.setAttribute("BankTransactionInquiry","BankTransactionInquiry");
        req.setAttribute("successCounter",String.valueOf(successCounter));
        req.setAttribute("failCounter",String.valueOf(failCounter));
        req.setAttribute("authStarted",String.valueOf(authStarted));
        rd.forward(req, res);
        return;

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

    public String getGatewayName( String pgtypId)
    {
        String gatewayName="";
        Connection con=null;
        try{
            con= Database.getConnection();
            String query = "SELECT gateway FROM gateway_type WHERE pgtypeid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,pgtypId);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                gatewayName=rs.getString("gateway");
            }
        }catch (Exception e){
            transactionLogger.error("Exception -----",e);
        }finally
        {
            Database.closeConnection(con);
        }
        return gatewayName;
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

}

