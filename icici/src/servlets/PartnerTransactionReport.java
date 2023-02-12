import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

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
 * Created by Vishal on 5/3/2017.
 */
public class PartnerTransactionReport extends HttpServlet
{
    private static Logger log = new Logger(PartnerTransactionReport.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in PartnerTransactionReport");
        HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Functions functions = new Functions();
        TransactionVO transactionVO = new TransactionVO();

        String startDate = req.getParameter("startdate");
        String startTime = req.getParameter("starttime");
        String endDate = req.getParameter("enddate");
        String endTime = req.getParameter("endtime");
        String gateway = req.getParameter("gateway");
        log.debug("gateway::::::"+gateway);
        log.debug("accountid::::::"+req.getParameter("accountid"));
        log.debug("partnerName::::::"+req.getParameter("partnerName"));
        String partnerid = "";
        String partnerName = "";
        String gatewayCurrency = "";
        /*if(!"0".equals(gateway) && functions.isValueNull(gateway))
        {
            String arrGateway[] = gateway.split("-");
            gatewayCurrency = arrGateway[0] +"-"+ arrGateway[1];
        }*/
        if (!"0".equals(req.getParameter("partnerName")) && functions.isValueNull(req.getParameter("partnerName")))
        {
            String partnerArr[] = req.getParameter("partnerName").split("-");
            partnerid = partnerArr[0];
            partnerName = partnerArr[1];
            log.debug("partnerName===="+partnerName);
        }

        TransactionManager transactionManager = new TransactionManager();
        Map<String, List<TransactionVO>> transactionVOMap = null;
        Map<String, List<TransactionVO>> transactionVORefundMap = null;
        Set<String> gatewaySet = new HashSet<String>();

        String initDateFormat = "dd/MM/yyyy HH:mm:ss";
        String targetDateFormat = "yyyy-MM-dd HH:mm:ss";
        String errormsg = "";
        try
        {
            List<String> errorList = new ArrayList<String>();
            if (!ESAPI.validator().isValidInput("startDate", startDate, "fromDate", 255, false))
            {
                errorList.add("Start Date ");
            }
            if (!ESAPI.validator().isValidInput("endDate", endDate, "toDate", 255, false))
            {
                errorList.add("End Date ");
            }
            if (!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true))
            {
                errorList.add("Start Time  ");
            }
            if (!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true))
            {
                errorList.add("End Time ");
            }
            if (!ESAPI.validator().isValidInput("merchantid", req.getParameter("merchantid"), "OnlyNumber", 255, true))
            {
                errorList.add("Member ID ");
            }
            if (!ESAPI.validator().isValidInput("partnerName", req.getParameter("partnerName"), "SafeString", 255, false))
            {
                errorList.add("Partner ID ");
            }
            if (!ESAPI.validator().isValidInput("gateway", req.getParameter("gateway"), "SafeString", 255, false))
            {
                errorList.add("Gateway Type ");
            }

            if (errorList.size() > 0)
            {
                errormsg = getErrorMessage(errorList);
                req.setAttribute("errormsg", errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/partnerTransactionReport.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            if (functions.isValueNull(startTime)) { startDate = formatDate(startDate + " " + startTime, initDateFormat, targetDateFormat);}
            else {startDate = formatDate(startDate + " "+"00:00:00",initDateFormat,targetDateFormat);}

            if (functions.isValueNull(endTime)) {endDate = formatDate(endDate + " " + endTime, initDateFormat, targetDateFormat);}
            else {endDate = formatDate(endDate + " "+"23:59:59",initDateFormat,targetDateFormat);}

            /*if ("0".equals(gateway) || !functions.isValueNull(gateway))*/
            if (functions.isEmptyOrNull(gateway))
            {
                gatewaySet = transactionManager.getAllGatewaysAssociatedWithPartner(partnerid);
                log.debug("gatewaySet===="+gatewaySet);

                transactionVO.setTerminalId("");
                transactionVO.setStartDate(startDate);
                transactionVO.setEndDate(endDate);
                transactionVO.setMemberId(req.getParameter("merchantid"));
                transactionVO.setAccountId(req.getParameter("accountid"));
                transactionVO.setToType(partnerName);

                if(gatewaySet != null && gatewaySet.size()>0)
                {
                    transactionVOMap = transactionManager.getGatewayWiseMerchantTransactionStatus(transactionVO, gatewaySet);
                    transactionVORefundMap = transactionManager.getGatewayWiseMerchantRefundStatus(transactionVO, gatewaySet);
                }
            }
            else
            {
                transactionVO.setTerminalId("");
                gatewaySet.add(gatewayCurrency);
                transactionVO.setStartDate(startDate);
                transactionVO.setEndDate(endDate);
                transactionVO.setMemberId(req.getParameter("merchantid"));
                transactionVO.setAccountId(req.getParameter("accountid"));
                transactionVO.setToType(partnerName);

                if(gatewaySet != null && gatewaySet.size()>0)
                {
                    transactionVOMap = transactionManager.getGatewayWiseMerchantTransactionStatus(transactionVO, gatewaySet);
                    transactionVORefundMap = transactionManager.getGatewayWiseMerchantRefundStatus(transactionVO, gatewaySet);
                }
            }
        }
        catch (ParseException pe)
        {
            log.error("ParseException:::::", pe);
        }
        catch (PZDBViolationException e)
        {
            log.error("ParseException:::::", e);
        }

        req.setAttribute("transactionVOMap", transactionVOMap);
        req.setAttribute("transactionVORefundMap", transactionVORefundMap);
        req.setAttribute("partnerName", partnerName);
        req.setAttribute("gatewaySet", gatewaySet);
        RequestDispatcher rd = req.getRequestDispatcher("/partnerTransactionReport.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    private String getErrorMessage( List<String> list)
    {
        StringBuffer errorMessage=new StringBuffer();
        errorMessage.append("Kindly provide below details \n\n");
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

    private String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }
}
