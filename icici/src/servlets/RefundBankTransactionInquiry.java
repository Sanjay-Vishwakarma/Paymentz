package servlets;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.request.PZInquiryRequest;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Admin on 8/18/2020.
 */
public class RefundBankTransactionInquiry extends HttpServlet
{
    TransactionLogger transactionLogger = new TransactionLogger(RefundBankTransactionInquiry.class.getName());

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
        transactionLogger.error("Inside RefundBankTransactionInquiry servlet cron ---");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        CommonBankTransactionInquiry commonBankTransactionInquiry = new CommonBankTransactionInquiry();
        if (!Admin.isLoggedIn(session))
        {
            transactionLogger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions = new Functions();
        AbstractPaymentGateway pg = null;
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String errormsg = "";
        int totalMarkorreversalCount = 0;
        int successCounter = 0;
        int failCounter = 0;

        String ctoken = req.getParameter("ctoken");
        String startDate = req.getParameter("startdate") != null ? req.getParameter("startdate") : "";
        String startTime = req.getParameter("starttime") != null ? req.getParameter("starttime") : "";
        String endDate = req.getParameter("enddate") != null ? req.getParameter("enddate") : "";
        String endTime = req.getParameter("endtime") != null ? req.getParameter("endtime") : "";
        String gateway = req.getParameter("gateway") != null ? req.getParameter("gateway") : "";
        String memberId = req.getParameter("merchantid") != null ? req.getParameter("merchantid") : "";
        String accountId = req.getParameter("accountid") != null ? req.getParameter("accountid") : "";
        String curr = req.getParameter("currency") != null ? req.getParameter("currency") : "";
        String status = req.getParameter("status") != null ? req.getParameter("status") : "";
        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        StringBuffer onchangedValues = new StringBuffer();
        onchangedValues.append(req.getParameter("onchangedvalue"));
        String ActivityAction = req.getParameter("ActivityAction");
        String beforeCount = req.getParameter("countbefore");
        String terminalId = "";
        String partnerId = req.getParameter("partnerName") != null ? partnerId = req.getParameter("partnerName") : "";

        List<String> errorList = new ArrayList();
        if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 25, false))
        {
            errorList.add("Kindly provide valid Start Date ");
        }
        if (!ESAPI.validator().isValidInput("endDate", startDate, "fromDate", 25, false))
        {
            errorList.add("Kindly provide valid End Date ");
        }
        if (!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true))
        {
            errorList.add("Kindly provide validStart Time  ");
        }
        if (!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true))
        {
            errorList.add("Kindly provide valid End Date ");
        }
        if (!ESAPI.validator().isValidInput("merchantid", memberId, "OnlyNumber", 10, true))
        {
            errorList.add("Kindly provide valid member ID ");
        }
        if (!ESAPI.validator().isValidInput("accountid", accountId, "OnlyNumber", 10, true))
        {
            errorList.add("Kindly provide valid Account ID ");
        }
        if (!ESAPI.validator().isValidInput("terminalid", terminalId, "OnlyNumber", 10, true))
        {
            errorList.add("Kindly provide valid terminal ID ");
        }

        String pgTypeId = getPgTypeId(req.getParameter("gateway"));

        if (gateway.equals("0") || functions.isEmptyOrNull(gateway) || functions.isEmptyOrNull(pgTypeId))
        {
            errorList.add("Kindly provide valid Gateway");
        }
        if (errorList.size() > 0)
        {
            errormsg = getErrorMessage(errorList);
            req.setAttribute("errormsg", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/bankTransactionReport.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        String gatewayName = getGatewayName(pgTypeId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            startDate = sdf.format(sdf2.parse(startDate));
            endDate = sdf.format(sdf2.parse(endDate));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception --", e);
        }

        startDate = startDate + " " + startTime;
        endDate = endDate + " " + endTime;

        transactionLogger.error("gatewayName-------" + gatewayName);
        HashMap responsemap = commonBankTransactionInquiry.commomTransactionInquiryStatus("", status, terminalId, accountId, gatewayName, curr, memberId, startDate, endDate);
        totalMarkorreversalCount = (int) responsemap.get("MarkedforreversalCount");
        successCounter = (int) responsemap.get("reversedCounter");
        failCounter = (int) responsemap.get("failCounter");
        transactionLogger.error("redirecting to BankTransactionReport");
        transactionLogger.error("CTOKEN = " + ctoken);
        int markedforreversal = totalMarkorreversalCount - successCounter;
        markedforreversal = markedforreversal - failCounter;
        RequestDispatcher rd = req.getRequestDispatcher("/detailsOfRefundTransactionReport.jsp?ctoken=" + user.getCSRFToken());

        transactionLogger.error("markedforreversal--------------" + markedforreversal);
        transactionLogger.error("successCounter--------------" + successCounter);
        transactionLogger.error("failCounter--------------" + failCounter);
        // transactionLogger.error("authStarted--------------"+authStarted);
        onchangedValues.append(",Before Cron markedforreversalCount="+beforeCount+" After Cron markedforreversalCount="+markedforreversal+", successCounter="+successCounter+", failCounter="+failCounter);

        transactionLogger.error("Creating Activity for edit Gatway account");
        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
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

        req.setAttribute("RefundBankTransactionInquiry", "RefundBankTransactionInquiry");
        req.setAttribute("successCounter", String.valueOf(successCounter));
        req.setAttribute("failCounter", String.valueOf(failCounter));
        req.setAttribute("markedforreversal", String.valueOf(markedforreversal));
        rd.forward(req, res);
        return;
    }

    public String getPgTypeId(String gatewayString)
    {
        String pgTypeId = "";
        Functions functions = new Functions();
        if (functions.isValueNull(gatewayString))
        {
            String aGatewaySet[] = gatewayString.split("-");
            if (aGatewaySet.length == 3)
            {
                pgTypeId = aGatewaySet[2];
            }
        }
        return pgTypeId;
    }

    public String getErrorMessage(List<String> list)
    {
        StringBuffer errorMessage = new StringBuffer();

        for (String message : list)
        {
            if (errorMessage.length() > 0)
            {
                errorMessage.append(", ");
            }
            errorMessage.append(message);

        }
        return errorMessage.toString();
    }

    public String getGatewayName(String pgtypId)
    {
        String gatewayName = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT gateway FROM gateway_type WHERE pgtypeid=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, pgtypId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                gatewayName = rs.getString("gateway");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception -----", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return gatewayName;
    }
}
