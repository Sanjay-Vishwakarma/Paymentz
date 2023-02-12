package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.AccountManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.payoutVOs.WireReportsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang.StringUtils;
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
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 4/13/15
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerWireReports extends HttpServlet
{
    private static Logger logger = new Logger(PartnerWireReports.class.getName());
    private Functions functions=new Functions();
    //for validation error message
    private CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

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

        //session
        HttpSession session = Functions.getNewSession(request);
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/partnerWireReports.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String PartnerWireReports_From_To_date_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerWireReports_From_To_date_errormsg")) ? rb1.getString("PartnerWireReports_From_To_date_errormsg") : "Invalid From & To date";

        // Validating Input Parameters
        ValidationErrorList mandatoryErrorList = validateMandatoryParameters(request);
        try
        {

            if (!mandatoryErrorList.isEmpty())
            {
                logger.debug("invalid data Provided for Wire Reports");
                StringBuffer errorMessage = new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage, mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(MerchantWireReports.class.getName(), "doPost()", null, "Merchant", errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);

            }

            //getting date Parameter
            logger.debug("Valid data provided for Wire Reports");
            logger.debug("From ::" + request.getParameter("fromdate") + " Todate::" + request.getParameter("todate"));
            String fromDate = request.getParameter("fromdate");
            String toDate = request.getParameter("todate");
            String partnerId=request.getParameter("partnerId");
            if(!functions.isValueNull(partnerId)){
                partnerId=partnerFunctions.getSubpartner((String) session.getAttribute("merchantid"));
            }

            if(functions.isFutureDateComparisonWithFromAndToDate(fromDate, toDate, "dd/MM/yyyy"))
            {
                request.setAttribute("catchError",PartnerWireReports_From_To_date_errormsg);
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

            //setting Pagination VO
            paginationVO.setInputs("fromdate=" + fromDate + "&todate=" + toDate + "&paid=" + isPaid);
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"), 1));
            paginationVO.setPage(PartnerWireReports.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"), 15));

            //accountManager call for list of Reports
            wireReportsVO = accountManager.getPartnerWireReports(inputDateVO, paginationVO, isPaid, partnerId);
            request.setAttribute("wireReports", wireReportsVO);

        }
        catch(PZDBViolationException dbe)
        {
            logger.error("SQL exception",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), "Exception While getting Wire Report List.");
            //request.setAttribute("catchError","Kindly check on for the Wire Reports after some time");
            rdError.forward(request,response);
            return;
        }
        catch (ParseException pe)
        {
            logger.error("Date parsing exception",pe);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(MerchantWireReports.class.getName(),"doPost()",null,"Merchant","Parsing exception while getting date from Merchant", PZTechnicalExceptionEnum.DATE_PARSING_EXCEPTION,null,pe.getMessage(),pe.getCause(),session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
            //request.setAttribute("catchError","Kindly check on for the Wire Reports after some time");
            rdError.forward(request,response);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("constrain Violation Exception exception",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.Wire_Reports);
            request.setAttribute("error",mandatoryErrorList);
            rdError.forward(request,response);
            return;
        }
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/partnerWireReports.jsp?Success=YES&ctoken="+user.getCSRFToken());
        rdSuccess.forward(request, response);
        return;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.FDATE);
        inputMandatoryParameter.add(InputFields.TDATE);
        inputMandatoryParameter.add(InputFields.PAID);
        inputMandatoryParameter.add(InputFields.PAGENO);
        inputMandatoryParameter.add(InputFields.RECORDS);
        InputValidator inputValidator = new InputValidator();
       // Hashtable error = new Hashtable();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,true);

        return errorList;
    }

}
