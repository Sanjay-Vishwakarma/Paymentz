package net.agent;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.MerchantConfigManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.payoutVOs.MerchantWireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import java.util.*;

/**
 * Created by Sneha on 10/20/2015.
 */
public class MerchantWireReports extends HttpServlet
{
    private static Logger log = new Logger(MerchantWireReports.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Entering in MerchantWireReports");

        AgentFunctions agentFunctions = new AgentFunctions();
        Functions functions = new Functions();

        HttpSession session = Functions.getNewSession(request);
        if (!agentFunctions.isLoggedInAgent(session))
        {
            response.sendRedirect("/agent/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        StringBuffer errorMsg = new StringBuffer();
        RequestDispatcher rdError=request.getRequestDispatcher("/merchantWireReports.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantWireReports.jsp?Success=YES&ctoken="+user.getCSRFToken());

        String fromdate = null;
        String todate = null;

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        fromdate = request.getParameter("fromdate");
        todate = request.getParameter("todate");

        if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
        {
            request.setAttribute("catchError","Invalid From & To date");
            rdError.forward(request,response);
            return;
        }

        int records=30;
        int pageno=1;
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 30);

        try
        {
            String agentid = (String) session.getAttribute("merchantid");
            String terminalid = request.getParameter("terminalid");
            String memberid = request.getParameter("memberid");
            String ispaid = request.getParameter("ispaid");

            log.debug("Fromdate---->"+fromdate+"----todate----"+todate);

            String errormsg = "";
            errormsg = errormsg+validateParameters(request);
            if(functions.isValueNull(errormsg))
            {
                request.setAttribute("error",errormsg);
                rdError.forward(request, response);
                return;
            }

            //from Date
            date=sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate=String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear=String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(todate);
            rightNow.setTime(date);
            String tdate=String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear=String.valueOf(rightNow.get(Calendar.YEAR));

            log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            InputDateVO dateVO = new InputDateVO();
            dateVO.setFdtstamp(fdtstamp);
            dateVO.setTdtstamp(tdtstamp);

            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            PaginationVO paginationVO = new PaginationVO();

            paginationVO.setInputs("fromdate="+fromdate+"&todate="+todate+"&terminalid="+terminalid+"&paid="+ispaid+"&toid="+memberid);
            paginationVO.setPageNo(pageno);
            paginationVO.setPage(MerchantWireReports.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(records);

            List<MerchantWireVO> merchantWireVOList = merchantConfigManager.getAgentMerchantWireReport(agentid,terminalid, dateVO, memberid, ispaid, paginationVO);
            request.setAttribute("merchantWireVOList",merchantWireVOList);
            request.setAttribute("paginationVO",paginationVO);
            request.setAttribute("toid",memberid);
            request.setAttribute("terminalid",terminalid);
        }
        catch (PZDBViolationException e)
        {
            log.debug("PZDBViolationException::" + e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (ParseException e)
        {
            log.debug("ParseException::" + e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        //request.setAttribute("errorMsg",errorMsg);
        rdSuccess.forward(request,response);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.ISPAID);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
}
