package servlets;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.request.PZInquiryRequest;
import com.payment.statussync.StatusSyncDAO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Balaji on 18-Feb-20.
 */
public class BankTransactionInquiry extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(BankTransactionInquiry.class.getName());
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
        transactionLogger.error("Inside BankTransactionInquiry servlet cron ---");
        HttpSession session                                         = req.getSession();
        User user                                                   = (User) session.getAttribute("ESAPIUserSessionKey");
        CommonBankTransactionInquiry commonBankTransactionInquiry   = new CommonBankTransactionInquiry();
        ActivityTrackerVOs activityTrackerVOs                       = new ActivityTrackerVOs();
        if (!Admin.isLoggedIn(session))
        {
            transactionLogger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions             = new Functions();
        AbstractPaymentGateway pg       = null;
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        String errormsg                 = "";
        int totalAuthstartedCount       = 0;
        int successCounter              = 0;
        int failCounter                 = 0;
        String Login                    = (String)session.getAttribute("username");
        String actionExecutorId         = (String)session.getAttribute("merchantid");
        String ctoken                   = req.getParameter("ctoken");
        String startDate                = req.getParameter("startdate") != null ? req.getParameter("startdate") : "";
        String startTime                = req.getParameter("starttime") != null ? req.getParameter("starttime") : "";
        String endDate                  = req.getParameter("enddate") != null ? req.getParameter("enddate") : "";
        String endTime                  = req.getParameter("endtime") != null ? req.getParameter("endtime") : "";
        String gateway                  = req.getParameter("gateway") != null ? req.getParameter("gateway") : "";
        String memberId                 = req.getParameter("merchantid") != null ? req.getParameter("merchantid") : "";
        String accountId                = req.getParameter("accountid") != null ? req.getParameter("accountid") : "";
        String curr                     = req.getParameter("currency") != null ? req.getParameter("currency") : "";
        String status                   = req.getParameter("status") != null ? req.getParameter("status") : "";
        StringBuffer onchangedValues    = new StringBuffer();
        String ActivityAction           = req.getParameter("ActivityAction");
        System.out.println(ActivityAction);
        String beforeCount              = req.getParameter("countbefore");
        try
        {

            String terminalId   = "";
            String partnerId    = req.getParameter("partnerName") != null ? partnerId = req.getParameter("partnerName") : "";

            List<String> errorList = new ArrayList();
            if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 25, false))
            {
                errorList.add("Kindly provide valid Start Date ");
            }
            else{
                onchangedValues.append("Start Date="+startDate);
            }
            if (!ESAPI.validator().isValidInput("endDate", startDate, "fromDate", 25, false))
            {
                errorList.add("Kindly provide valid End Date ");
            }else{
                onchangedValues.append("End Date=" + endDate);
            }
            if (!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true))
            {
                errorList.add("Kindly provide validStart Time  ");
            }
            else{
                onchangedValues.append("Start Time=" + startTime);
            }
            if (!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true))
            {
                errorList.add("Kindly provide valid End Date ");
            }else{
                onchangedValues.append("End Time=" + endTime);
            }
            if (!ESAPI.validator().isValidInput("merchantid", memberId, "OnlyNumber", 10, true))
            {
                errorList.add("Kindly provide valid member ID ");
            }else{
                if(functions.isValueNull(memberId))
                    onchangedValues.append("Member ID=" + memberId);
            }
            if (!ESAPI.validator().isValidInput("accountid", accountId, "OnlyNumber", 10, true))
            {
                errorList.add("Kindly provide valid Account ID ");
            }else{
                if(functions.isValueNull(accountId))
                    onchangedValues.append("Account ID=" + accountId);
            }
            if (!ESAPI.validator().isValidInput("terminalid", terminalId, "OnlyNumber", 10, true))
            {
                errorList.add("Kindly provide valid terminal ID ");
            }else{
                if(functions.isValueNull(terminalId))
                    onchangedValues.append("Terminal ID=" + terminalId);
            }

            String pgTypeId = getPgTypeId(req.getParameter("gateway"));

            if (gateway.equals("0") || functions.isEmptyOrNull(gateway) || functions.isEmptyOrNull(pgTypeId))
            {
                errorList.add("Kindly provide valid Gateway");
            }else{
                onchangedValues.append("Gateway=" + gateway);
            }
            if (errorList.size() > 0)
            {
                errormsg = getErrorMessage(errorList);
                req.setAttribute("errormsg", errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/bankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            String gatewayName      = getGatewayName(pgTypeId);

            SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2   = new SimpleDateFormat("dd/MM/yyyy");
            HashMap responsemap     = new HashMap();
            try
            {
                startDate   = sdf.format(sdf2.parse(startDate));
                endDate     = sdf.format(sdf2.parse(endDate));
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception --", e);
            }

            startDate   = startDate + " " + startTime;
            endDate     = endDate + " " + endTime;
            int totalpayoutstartedCount     = 0;
            int payoutsuccessCounter        = 0;
            int payoutfailCounter           = 0;
            int authStarted                 = 0;
            int payoutStarted               = 0;
            transactionLogger.error("gatewayName-------" + gatewayName);
            String TransactionInquiry   = "";
            RequestDispatcher rd        = req.getRequestDispatcher("/detailsOfBankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
            transactionLogger.error("action---> "+ActivityAction);
            if ("payoutstarted".equalsIgnoreCase(status) || "payoutfailed".equalsIgnoreCase(status) || "payoutsuccessful".equalsIgnoreCase(status))
            {

                responsemap             = commonBankTransactionInquiry.commomPayoutInquiryStatus("", status, terminalId, accountId, gatewayName, curr, memberId, startDate, endDate);
                totalpayoutstartedCount = (int) responsemap.get("payoutstartedCount");
                payoutsuccessCounter    = (int) responsemap.get("payoutsuccessCounter");
                payoutfailCounter       = (int) responsemap.get("payoutfailCounter");
                transactionLogger.error("redirecting to BankTransactionReport");
                transactionLogger.error("CTOKEN = " + ctoken);
                payoutStarted       = totalpayoutstartedCount - payoutsuccessCounter;
                payoutStarted       = payoutStarted - payoutfailCounter;
                TransactionInquiry  = "PayoutInquiry";
            }else if ("AuthFailedCron".equalsIgnoreCase(ActivityAction)){
                responsemap     = commonBankTransactionInquiry.authStartedToAuthFailedTransaction("", terminalId, accountId, gatewayName, curr, memberId, startDate, endDate,"authstarted");
                failCounter     = (int) responsemap.get("failCounter");
                transactionLogger.error("redirecting to BankTransactionReport AuthFailedCron "+responsemap.toString()+" failCounter---"+failCounter);
                transactionLogger.error("CTOKEN = " + ctoken);
            }else if ("PayoutFailedCron".equalsIgnoreCase(ActivityAction)){
                responsemap     = commonBankTransactionInquiry.authStartedToAuthFailedTransaction("", terminalId, accountId, gatewayName, curr, memberId, startDate, endDate,"payoutstarted");
                failCounter     = (int) responsemap.get("failCounter");
                transactionLogger.error("redirecting to BankTransactionReport PayoutfailedCron "+responsemap.toString()+" failCounter---"+failCounter);
                transactionLogger.error("CTOKEN = " + ctoken);
                //NotFoundFailedCron
            }else if ("NotFoundFailedCron".equalsIgnoreCase(ActivityAction)){
                transactionLogger.error("in else if  side NotFoundFailedCron");
                responsemap     = commonBankTransactionInquiry.bankTransactionsNotFoundInquiry("", terminalId, accountId, gatewayName, curr, memberId, startDate, endDate, "authstarted");
                failCounter     = (int) responsemap.get("failCounter");
                successCounter  = (int) responsemap.get("successCounter");
                transactionLogger.error("redirecting to BankTransactionReport BankAuthFailedCron "+responsemap.toString()+" failCounter---"+failCounter);
                transactionLogger.error("CTOKEN = " + ctoken);


            }
            else
            {
                transactionLogger.error("in sideelse coomon inquiry ");
                responsemap             = commonBankTransactionInquiry.commomTransactionInquiryStatus("", status, terminalId, accountId, gatewayName, curr, memberId, startDate, endDate);
                totalAuthstartedCount   = (int) responsemap.get("authstartedCount");
                successCounter          = (int) responsemap.get("successCounter");
                failCounter             = (int) responsemap.get("failCounter");
                transactionLogger.error("redirecting to BankTransactionReport");
                transactionLogger.error("CTOKEN = " + ctoken);
                authStarted         = totalAuthstartedCount - successCounter;
                authStarted         = authStarted - failCounter;
                TransactionInquiry  = "AuthstartedInquiry";
            }


            transactionLogger.error("authStarted--------------" + authStarted);
            transactionLogger.error("successCounter--------------" + successCounter);
            transactionLogger.error("failCounter--------------" + failCounter);

            if("authfailed".equalsIgnoreCase(status))
            {
                onchangedValues.append(",Before cron autfailedCount=" + beforeCount + ", authstartedCount=" + authStarted + ", successCounter=" + successCounter + ", After cron autfailedCount=" + failCounter);
            }
            else if ("payoutstarted".equalsIgnoreCase(status))
            {
                onchangedValues.append(",Before Cron payoutstartedCount="+beforeCount+",After Cron payoutstartedCount="+payoutStarted+",payoutsuccessCounter="+payoutsuccessCounter+",payoutfailCounter="+payoutfailCounter);
            }else if ("payoutfailed".equalsIgnoreCase(status))
            {
                onchangedValues.append(",Before Cron payoutfailedCount="+beforeCount+",After Cron payoutfailedCount="+payoutStarted+",payoutsuccessCounter="+payoutsuccessCounter+",payoutfailCounter="+payoutfailCounter);
            }else{
                onchangedValues.append(",Before cron authstartedCount=" + beforeCount + ", After cron authstartedCount=" + authStarted + ", successCounter=" + successCounter + ", failCounter=" + failCounter);
            }

            String remoteAddr       = Functions.getIpAddress(req);
            int serverPort          = req.getServerPort();
            String servletPath      = req.getServletPath();
            String header           = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
            if(functions.isValueNull(onchangedValues.toString()))
            {
                activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                activityTrackerVOs.setAction(ActivityAction);
                activityTrackerVOs.setModule_name(ActivityLogParameters.BANK_TRANSACTION_REPORT.toString());
                activityTrackerVOs.setLable_values(onchangedValues.toString());
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
                    transactionLogger.error("Exception while AsyncActivityLog::::", e);
                }
            }

            req.setAttribute("BankTransactionInquiry", "BankTransactionInquiry");
            req.setAttribute("TransactionInquiry", TransactionInquiry);
            req.setAttribute("successCounter", String.valueOf(successCounter));
            req.setAttribute("failCounter", String.valueOf(failCounter));
            req.setAttribute("authStarted", String.valueOf(authStarted));
            req.setAttribute("payoutsuccessCounter", String.valueOf(payoutsuccessCounter));
            req.setAttribute("payoutfailCounter", String.valueOf(payoutfailCounter));
            req.setAttribute("payoutStarted", String.valueOf(payoutStarted));
            rd.forward(req, res);
            return;

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception payout--------------" ,e);
        }

    }
    public String getPgTypeId(String gatewayString){
        String pgTypeId         = "";
        Functions functions     = new Functions();
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
        StringBuffer errorMessage   = new StringBuffer();

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
        String gatewayName  = "";
        Connection con      = null;
        try
        {
            con                     = Database.getConnection();
            String query            = "SELECT gateway FROM gateway_type WHERE pgtypeid=?";
            PreparedStatement ps    = con.prepareStatement(query);
            ps.setString(1,pgtypId);
            ResultSet rs            = ps.executeQuery();
            if(rs.next()){
                gatewayName = rs.getString("gateway");
            }
        }catch (Exception e){
            transactionLogger.error("Exception -----",e);
        }finally
        {
            Database.closeConnection(con);
        }
        return gatewayName;
    }
}