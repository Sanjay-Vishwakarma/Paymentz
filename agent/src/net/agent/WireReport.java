package net.agent;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.AccountManager;
import com.manager.TerminalManager;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.payoutVOs.AgentWireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/11/14
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireReport extends HttpServlet
{
    private static Logger logger = new Logger(WireReport.class.getName());
    //for validation error message
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        AgentFunctions agentFunctions = new AgentFunctions();
        TerminalManager terminalManager=new TerminalManager();
        AccountManager accountManager= new AccountManager();
        InputDateVO inputDateVO= new InputDateVO();
        Functions functions = new Functions();

        //outputVO
        List<AgentWireVO> agentWireVOList = null;
        PaginationVO paginationVO = new PaginationVO();

        //session
        HttpSession session = Functions.getNewSession(request);
        if (!agentFunctions.isLoggedInAgent(session))
        {
            response.sendRedirect("/agent/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/wireReport.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/wireReport.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try
        {
            //getting date Parameter
            logger.debug("Inside Agent Wire Report");
            String terminalid = request.getParameter("terminalid");
            String isPaid=request.getParameter("paid");
            String agentid = request.getParameter("agentid");
            String fromdate =request.getParameter("fromdate");
            String todate =request.getParameter("todate");
            String errormsg = "";

            Calendar rightNow = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date= null;

            StringBuffer errorMsg = new StringBuffer();

            int records=30;
            int pageno=1;
            pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(request.getParameter("SRecords"), 30);

            /*if (!ESAPI.validator().isValidInput("terminalid", terminalid, "Numbers", 10, true))
            {
                errorMsg.append("Invalid Terminal<BR>");
                request.setAttribute("errorMsg",errorMsg.toString());
                rdError.forward(request,response);
            }
            if (!ESAPI.validator().isValidInput("isPaid", isPaid, "SafeString", 10, false))
            {
                errorMsg.append("Invalid IsPaid <BR>");
                request.setAttribute("errorMsg",errorMsg.toString());
                rdError.forward(request,response);
            }*/

            errormsg = errormsg + validateParameters(request);
            if(!errormsg.isEmpty())
            {
                request.setAttribute("catchError",errormsg);
                rdError.forward(request, response);
                return;
            }
            //rdError.forward(request,response);

            /*Calendar rightNow = Calendar.getInstance();
            if(fdate == null) fdate = "" + 1;
            if(tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

            if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
            if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

            if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
            if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/

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

            logger.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            //setting in InputDateVO
            inputDateVO.setFdate(fdate);
            inputDateVO.setFmonth(fmonth);
            inputDateVO.setFyear(fyear);
            inputDateVO.setTdate(tdate);
            inputDateVO.setTmonth(tmonth);
            inputDateVO.setTyear(tyear);
            inputDateVO.setFdtstamp(fdtstamp);
            inputDateVO.setTdtstamp(tdtstamp);
            //Getting TerminalVO
            if(!functions.isValueNull(terminalid))
            {
                terminalid = terminalManager.getTerminalByAgentId(agentid);
            }
            //setting Pagination VO
            paginationVO.setInputs("fromdate=" + fromdate + "&todate=" + todate + "&terminalid=" + request.getParameter("terminalid") + "&paid=" + isPaid);
            paginationVO.setPageNo(pageno);
            paginationVO.setPage(WireReport.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(records);

            //accountManager call for list of Reports
            agentWireVOList=accountManager.getListOfAgentWireReportByTerminal(inputDateVO, agentid, terminalid, paginationVO, isPaid);
            request.setAttribute("agentWireVOList",agentWireVOList);
            request.setAttribute("paginationVO", paginationVO);

        }
        catch(PZDBViolationException dbe)
        {
            logger.error("SQLException",dbe);
            /*PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), "Exception While getting Wire Report List.");
            request.setAttribute("catchError","Internal error while accessing data");
            rdError.forward(request,response);
            return;*/
            //Functions.NewShowConfirmation1();
        }
        catch (ParseException e)
        {
            logger.debug("ParseException::" + e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        request.setAttribute("catchError", "Internal error while accessing data");
        rdSuccess.forward(request,response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
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
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }

}

