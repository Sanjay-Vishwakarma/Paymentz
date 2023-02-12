package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.AccountManager;
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 4/11/15
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantWireReports  extends HttpServlet
{
    private static Logger logger = new Logger(MerchantWireReports.class.getName());
    //for validation error message
    private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    private Functions functions=new Functions();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        //TerminalManager terminalManager=new TerminalManager();
        AccountManager accountManager= new AccountManager();
        InputDateVO inputDateVO= new InputDateVO();
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        //outputVO
        WireReportsVO wireReportsVO ;
        //pagination Purpose VO
        PaginationVO paginationVO = new PaginationVO();
        //Date transformation;
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

       // PrintWriter out=response.getWriter();
        //session
        HttpSession session = Functions.getNewSession(request);
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/merchantWireReports.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        /*try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            request.setAttribute("message",e.getMessage());
            rdError=request.getRequestDispatcher("/merchantWireReports.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
            rdError.forward(request, response);
            return;
        }*/

        // Validating Input Parameters
        ValidationErrorList mandatoryErrorList = validateMandatoryParameters(request);
        ValidationErrorList optionalErrorList = validateOptionalParameter(request);

        try
        {
            if (!mandatoryErrorList.isEmpty())
            {
                logger.debug("invalid data Provided for Wire Reports");
                StringBuffer errorMessage = new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage, mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(MerchantWireReports.class.getName(), "doPost()", null, "Merchant", errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);

            }
            if (!optionalErrorList.isEmpty())
            {
                logger.debug("invalid data Provided for Wire Reports");
                StringBuffer errorMessage = new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage, optionalErrorList);
                PZExceptionHandler.raiseConstraintViolationException(MerchantWireReports.class.getName(), "doPost()", null, "Merchant", errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
            }

            //getting date Parameter
            logger.debug("Valid data provided for Wire Reports");
            logger.debug("From ::" + request.getParameter("fromdate") + " Todate::" + request.getParameter("todate"));
            String fromDate = request.getParameter("fromdate");
            String toDate = request.getParameter("todate");

            if(functions.isFutureDateComparisonWithFromAndToDate(fromDate, toDate, "dd/MM/yyyy"))
            {
                request.setAttribute("catchError","Invalid From & To date");
                rdError.forward(request,response);
                return;
            }
            //from date
            date = sdf.parse(fromDate);
            cal.setTime(date);
            String fdate = String.valueOf(cal.get(Calendar.DATE));
            String fmonth = String.valueOf(cal.get(Calendar.MONTH));
            String fyear = String.valueOf(cal.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(toDate);
            cal.setTime(date);
            String tdate = String.valueOf(cal.get(Calendar.DATE));
            String tmonth = String.valueOf(cal.get(Calendar.MONTH));
            String tyear = String.valueOf(cal.get(Calendar.YEAR));

            logger.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);
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
            String isPaid = request.getParameter("paid");
            //Getting TerminalVO
            TerminalVO terminalVO = new TerminalVO();
            logger.debug(" getting terminalVo using terminalId");
            if (functions.isValueNull(request.getParameter("terminalid")))
            {
               // terminalVO = terminalManager.getTerminalByTerminalId(request.getParameter("terminalid"));
                terminalVO.setTerminalId(request.getParameter("terminalid"));
            }
            //CHNAGES THE METHOD TO CHECK IF MEMBER AND PARTNER MAPPED, CHANGED METHOD TO FETCH ALL MEMBERS ACCORDING TO PARTNER AND SUPER PARTNER.
            String toidList = null;
            String partner = null;
           if(functions.isValueNull(request.getParameter("pid"))){
                partner = request.getParameter("pid");
            }
            else{
                partner = session.getAttribute("merchantid").toString();
            }

            try
            {
                if (functions.isValueNull(request.getParameter("memberid")) && partnerFunctions.isPartnerSuperpartnerMembersMapped(request.getParameter("memberid"), partner))
                {
                    terminalVO.setMemberId(request.getParameter("memberid"));

                }
                else if (!functions.isValueNull(request.getParameter("memberid")))
                {
                    if (functions.isValueNull(request.getParameter("pid")) && partnerFunctions.isPartnerSuperpartnerMapped(request.getParameter("pid"), session.getAttribute("merchantid").toString()))
                    {
                        toidList = partnerFunctions.getPartnerMemberRS(request.getParameter("pid"));
                    }
                    else if(!functions.isValueNull(request.getParameter("pid")))
                    {
                        toidList = partnerFunctions.getSuperpartnersMemberRS(session.getAttribute("merchantid").toString());

                    }
                }
            }
            catch (Exception e)
            {
                logger.debug("Exception::::" + e);
            }
            //setting Pagination VO
            paginationVO.setInputs("fromdate=" + fromDate + "&todate=" + toDate + "&terminalid=" + request.getParameter("terminalid") + "&paid=" + isPaid + "&memberid=" + request.getParameter("memberid") + "&pid=" + request.getParameter("pid"));
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(MerchantWireReports.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));

            logger.debug("terminalVO getMemberId::::" + terminalVO.getMemberId());
            logger.debug("toid List::::::" + toidList);
            //accountManager call for list of Reports
            wireReportsVO = accountManager.getWireReportsForPartner(inputDateVO, terminalVO, paginationVO, isPaid, toidList);
            request.setAttribute("wireReports", wireReportsVO);
        }
        catch(PZDBViolationException dbe)
        {
            logger.error("SQL exception",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), "Exception While getting Wire Report List.");
            //request.setAttribute("catchError","No Records Found.");
            rdError.forward(request,response);
            return;
        }
        catch (ParseException pe)
        {
            logger.error("Date parsing exception",pe);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(MerchantWireReports.class.getName(), "doPost()", null, "Merchant", "Parsing exception while getting date from Merchant", PZTechnicalExceptionEnum.DATE_PARSING_EXCEPTION, null, pe.getMessage(), pe.getCause(), session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
            //request.setAttribute("catchError","No Records Found.");
            rdError.forward(request,response);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("constrain Violation Exception exception",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
            if(mandatoryErrorList.size()>0)
            {
                request.setAttribute("error",mandatoryErrorList);
            }
            else if (optionalErrorList.size()>0)
            {
                request.setAttribute("error",optionalErrorList);
            }
            rdError.forward(request,response);
            return;
        }
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantWireReports.jsp?Success=YES&ctoken="+user.getCSRFToken());
        rdSuccess.forward(request, response);
        return;

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.FDATE);
        inputMandatoryParameter.add(InputFields.TDATE);

        InputValidator inputValidator = new InputValidator();
        //Hashtable error = new Hashtable();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,true);

        return errorList;
    }

    private ValidationErrorList validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.PID);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        //inputFieldsListOptional.add(InputFields.PAID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);
        return errorList;
    }
}
