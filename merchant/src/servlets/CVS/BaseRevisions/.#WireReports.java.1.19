import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.AccountManager;
import com.manager.TerminalManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.WireReportsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

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

//import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/11/14
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireReports extends HttpServlet
{
    private static final int RECORSPERPAGE =15;
    private static Logger logger = new Logger(WireReports.class.getName());
    //for validation error message
    private CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        Merchants merchants= new Merchants();
        TerminalManager terminalManager=new TerminalManager();
        AccountManager accountManager= new AccountManager();
        InputDateVO inputDateVO= new InputDateVO();
        Functions functions = new Functions();


        //outputVO
        WireReportsVO wireReportsVO ;
        //pagination Purpose VO
        PaginationVO paginationVO = new PaginationVO();
        //Date transformation;
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        //PrintWriter out=response.getWriter();
        //session
        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        String errormsg = "";
        String EOL = "<BR>";

        RequestDispatcher rdError=request.getRequestDispatcher("/reports.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/reports.jsp?Success=YES&ctoken="+user.getCSRFToken());
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);

        String terminalid = request.getParameter("terminalid");
        String sb = request.getParameter("terminalbuffer");
     //   String currency = request.getParameter("currency");
        //String accountid=null;

        try
        {
            //validateOptionalParameter(request);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(request,inputFieldsListMandatory,true);
        }
        catch (ValidationException e)
        {

            ValidationErrorList validationErrorList = new ValidationErrorList();
            logger.error("Enter valid input",e);
            if(functions.isValueNull(e.getMessage()) && e.getMessage().equalsIgnoreCase("Invalid Terminal ID")){
                validationErrorList.addError(terminalid, new ValidationException(!functions.isEmptyOrNull(rb1.getString("Invalid_terminalid"))?rb1.getString("Invalid_terminalid"): "Invalid terminalid", "Invalid Terminal ID :::" + terminalid));
            }
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..."+e.getMessage());
            request.setAttribute("message",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/reports.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        // Validating Input Parameters
        ValidationErrorList mandatoryErrorList = validateMandatoryParameters(request);
        try
        {
            if(!mandatoryErrorList.isEmpty())
            {
                logger.debug("Invalid data Provided for Wire Reports");
                StringBuffer errorMessage= new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage,mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(WireReports.class.getName(),"doPost()",null,"Merchant",errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);

            }

            //getting date Parameter
            logger.debug("Valid data provided for Wire Reports");
            logger.debug("From ::"+request.getParameter("fromdate")+" Todate::"+request.getParameter("todate"));
            String fromDate=request.getParameter("fromdate");
            String toDate= request.getParameter("todate");

            if(functions.isFutureDateComparisonWithFromAndToDate(fromDate, toDate, "dd/MM/yyyy"))
            {
                request.setAttribute("catchError","Invalid From & To date");
                rdError.forward(request,response);
                return;
            }

            //from date
            date=sdf.parse(fromDate);
            cal.setTime(date);
            String fdate=String.valueOf(cal.get(Calendar.DATE));
            String fmonth=String.valueOf(cal.get(Calendar.MONTH));
            String fyear=String.valueOf(cal.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(toDate);
            cal.setTime(date);
            String tdate=String.valueOf(cal.get(Calendar.DATE));
            String tmonth=String.valueOf(cal.get(Calendar.MONTH));
            String tyear=String.valueOf(cal.get(Calendar.YEAR));

            logger.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+fyear+" To date dd::"+tdate+" MM::"+tmonth+" YY::"+tyear);

            //conversion to dtstamp
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
            //getting IsPaid parameter
            String isPaid=request.getParameter("paid");

            TerminalVO terminalVO=new TerminalVO();

            if(functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
            {
                terminalVO=terminalManager.getTerminalByTerminalId(terminalid);
                terminalVO.setTerminalId("("+terminalVO.getTerminalId()+")");
                //accountid=terminalVO.getAccountId();
            }
            else
            {
                terminalVO.setTerminalId(sb);
            }

            //Getting TerminalVO
            logger.debug(" getting terminalVo using terminalId");
            terminalVO=terminalManager.getTerminalByTerminalId(request.getParameter("terminalid"));
            logger.debug("terminalVO::"+terminalVO.toString());
            //setting Pagination VO
            paginationVO.setInputs("fromdate="+fromDate+"&todate="+toDate+"&terminalid="+request.getParameter("terminalid")+"&paid="+isPaid);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"),1));
            paginationVO.setPage(WireReports.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),RECORSPERPAGE));

            //accountManager call for list of Reports
            wireReportsVO=accountManager.getWireReports(inputDateVO,terminalVO,paginationVO,isPaid);

            request.setAttribute("terminalid",request.getParameter("terminalid"));
            request.setAttribute("wireReports",wireReportsVO);
            rdSuccess.forward(request,response);

        }
        catch(PZDBViolationException dbe)
        {
            logger.error("SQL exception",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), "Exception While getting Wire Report List.");
            //request.setAttribute("catchError","Kindly check on for the Wire Reports after some time");
            rdError.forward(request, response);
            return;
        }
        catch (ParseException pe)
        {
            logger.error("Date parsing exception",pe);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(WireReports.class.getName(),"doPost()",null,"Merchant","Parsing exception while getting date from Merchant", PZTechnicalExceptionEnum.DATE_PARSING_EXCEPTION,null,pe.getMessage(),pe.getCause(),session.getAttribute("merchantid").toString(),PZOperations.Wire_Reports);
            //request.setAttribute("catchError","Kindly check on for the Wire Reports after some time");
            rdError.forward(request, response);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("constrain Violation Exception exception",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
            request.setAttribute("error",mandatoryErrorList);
            rdError.forward(request, response);
            return;
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.TERMINALID);
        inputMandatoryParameter.add(InputFields.FDATE);
        inputMandatoryParameter.add(InputFields.TDATE);
        inputMandatoryParameter.add(InputFields.PAID);
        InputValidator inputValidator = new InputValidator();
        //Hashtable error = null;
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,false);

        return errorList;
    }

   /* private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/
}

