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
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/4/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTransMailList extends HttpServlet
{
    private static Logger log = new Logger(MerchantTransMailList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in MerchantTransMailList");
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
        Set<String> gatewaySet = new HashSet();

        String startDate = req.getParameter("startdate");
        String startTime = req.getParameter("starttime");
        String endDate = req.getParameter("enddate");
        String endTime = req.getParameter("endtime");
        String gateway = req.getParameter("gateway");
        String terminal = req.getParameter("terminalid");
        String memberid = req.getParameter("merchantid");
        String cardTypeId = "";
        String payModeTypeId = "";
        String gatewayCurrency = "";
        String terminalId = "";

        if(!"0".equals(gateway) && functions.isValueNull(gateway))
        {
            String arrGateway[] = gateway.split("-");
            gatewayCurrency = arrGateway[0] +"-"+ arrGateway[1];
        }
        if(!"0".equals(terminal) && functions.isValueNull(terminal))
        {
            String arrTerminal[] = terminal.split("-");
            terminalId = arrTerminal[0];
           /* cardTypeId = arrTerminal[1];
            payModeTypeId = arrTerminal[2];*/
        }

        RequestDispatcher rd = req.getRequestDispatcher("/merchanttransmaillist.jsp?ctoken=" + user.getCSRFToken());
        TransactionManager transactionManager = new TransactionManager();
        Map<String, List<TransactionVO>> transactionVOMap = null;
        Map<String, List<TransactionVO>> transactionVORefundMap = null;

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String initDateFormat = "dd/MM/yyyy HH:mm:ss";
        String targetDateFormat = "yyyy-MM-dd HH:mm:ss";

        String errormsg = "";

        try
        {
            List<String> errorList = new ArrayList<String>();
            if (!ESAPI.validator().isValidInput("startdate", startDate, "fromDate", 255, false))
            {
                errorList.add("From Date ");
            }
            if (!ESAPI.validator().isValidInput("enddate", endDate, "toDate", 255, false))
            {
                errorList.add("To Date ");
            }
            if (!ESAPI.validator().isValidInput("starttime", startTime, "time", 255, true))
            {
                errorList.add("Start Time  ");
            }
            if (!ESAPI.validator().isValidInput("endtime", endTime, "time", 255, true))
            {
                errorList.add("End Time ");
            }
            if (!ESAPI.validator().isValidInput("merchantid", req.getParameter("merchantid"), "Numbers", 255, false) || "0".equals(req.getParameter("merchantid")))
            {
                errorList.add("Member ID ");
            }

            try
            {
                Date fromDate = format.parse(req.getParameter("startdate"));
                Date toDate = format.parse(req.getParameter("enddate"));
                if (toDate.before(fromDate))
                {
                    errorList.add("From Date should not be greater than To Date");
                }
            }
            catch (ParseException e)
            {
               log.error("Catch ParseException...",e);
            }

            if (errorList.size() > 0)
            {
                errormsg = getErrorMessage(errorList);
                req.setAttribute("errormsg", errormsg);
                rd.forward(req, res);
                return;
            }

            if (functions.isValueNull(startTime)) { startDate = formatDate(startDate + " " + startTime, initDateFormat, targetDateFormat);}
            else {startDate = formatDate(startDate + " "+"00:00:00",initDateFormat,targetDateFormat);}

            if (functions.isValueNull(endTime)) {endDate = formatDate(endDate + " " + endTime, initDateFormat, targetDateFormat);}
            else {endDate = formatDate(endDate + " "+"23:59:59",initDateFormat,targetDateFormat);}


            if("0".equals(gateway) || !functions.isValueNull(gateway))
            {
                gatewaySet = transactionManager.getAllGatewaysAssociatedWithMerchant(memberid);

                transactionVO.setMemberId(memberid);
                transactionVO.setTerminalId(terminalId);
                transactionVO.setCardTypeId(cardTypeId);
                transactionVO.setPaymodeid(payModeTypeId);
                transactionVO.setStatus(req.getParameter("status"));
                transactionVO.setStartDate(startDate);
                transactionVO.setEndDate(endDate);

                if(gatewaySet != null && gatewaySet.size()>0)
                {
                    transactionVOMap = transactionManager.getGatewayWiseMerchantTransactionStatus(transactionVO, gatewaySet);
                    transactionVORefundMap = transactionManager.getGatewayWiseMerchantRefundStatus(transactionVO, gatewaySet);
                }
            }
            else
            {
                gatewaySet.add(gatewayCurrency);
                transactionVO.setMemberId(memberid);
                transactionVO.setTerminalId(terminalId);
                transactionVO.setCardTypeId(cardTypeId);
                transactionVO.setPaymodeid(payModeTypeId);
                transactionVO.setStatus(req.getParameter("status"));
                transactionVO.setStartDate(startDate);
                transactionVO.setEndDate(endDate);

                if(gatewaySet != null && gatewaySet.size()>0)
                {
                    transactionVOMap = transactionManager.getGatewayWiseMerchantTransactionStatus(transactionVO, gatewaySet);
                    transactionVORefundMap = transactionManager.getGatewayWiseMerchantRefundStatus(transactionVO, gatewaySet);
                }
            }
        }
        catch (ParseException e)
        {
           log.error("Catch ParseException..",e);
        }
        catch (PZDBViolationException e)
        {
            log.error("Catch PZDBViolationException..",e);
        }
        req.setAttribute("transactionVOMap", transactionVOMap);
        req.setAttribute("transactionVORefundMap",transactionVORefundMap);
        req.setAttribute("gatewaySet", gatewaySet);
        req.setAttribute("terminalid", terminal);
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

    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException
    {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }
}

