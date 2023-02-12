import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.InputDateVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionReportVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
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
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/18/14
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionSummary  extends HttpServlet
{
    private static Logger logger = new Logger(TransactionSummary.class.getName());
    //this is for validation purpose
    private CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        //instance created of normal class
        Merchants merchants= new Merchants();
        Functions functions= new Functions();
        //manager instance
        TransactionManager transactionManager = new TransactionManager();
        TerminalManager terminalManager=new TerminalManager();
        //vo declaration
        InputDateVO inputDateVO= new InputDateVO();
        TransactionReportVO transactionReportVO= null;
        //Date transformation;
        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;
        //session
        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        session.getAttribute("colorPallet");
        RequestDispatcher rdError=request.getRequestDispatcher("/transactionSummary.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/transactionSummary.jsp?Success=YES&ctoken="+user.getCSRFToken());

        String terminalid = request.getParameter("terminalid");
        String toid = (String) session.getAttribute("merchantid");
        String country = request.getParameter("country");
        //String totype = (String) session.getAttribute("partnername");
        StringBuffer stringBufferbincountrysuccessful = new StringBuffer();
        StringBuffer stringBufferbincountryfailed = new StringBuffer();
        StringBuffer stringBufferipcountrysuccessful = new StringBuffer();
        StringBuffer stringBufferipcountryfailed = new StringBuffer();
        HashMap<String, String> hashMapbincountrysuccessful = null;
        HashMap<String, String> hashMapbincountryfailed = null;
        HashMap<String, String> hashMapipcountrysuccessful = null;
        HashMap<String, String> hashMapipcountryfailed = null;
        String bincountrysuccessful = "";
        String bincountryfailed = "";
        String ipcountrysuccessful = "";
        String ipcountryfailed = "";
        String sb = request.getParameter("terminalbuffer");
        // String currency = request.getParameter("currency");
        //  String accountid=null;

        // Validating Input Parameters
        ValidationErrorList mandatoryErrorList = validateMandatoryParameters(request);
        // ValidationErrorList optionalErrorList = validateOptionalParameters(request);
        try
        {
            // Validating Input Parameters

            if (!mandatoryErrorList.isEmpty() /*|| !optionalErrorList.isEmpty()*/)
            {
                logger.debug("Invalid data Provided for Account Summary");
                StringBuffer errorMessage = new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage, mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(TransactionSummary.class.getName(), "doPost()", null, "Merchant", errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }

            //getting date Parameter
            logger.debug("Valid data provided for Account Summary");
            logger.debug("From ::"+request.getParameter("fromdate")+" Todate::"+request.getParameter("todate"));
            String fromDate=request.getParameter("fromdate");
            String toDate= request.getParameter("todate");
            String startTime =request.getParameter("starttime");
            String endTime =request.getParameter("endtime");

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

            startTime=startTime.trim();
            endTime=endTime.trim();

            if (!functions.isValueNull(startTime))
            {
                startTime="00:00:00";
            }
            if (!functions.isValueNull(endTime))
            {
                endTime="23:59:59";
            }

            String startTimeArr[]=startTime.split(":");
            String endTimeArr[]=endTime.split(":");

            logger.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+fyear+" To date dd::"+tdate+" MM::"+tmonth+" YY::"+tyear);

            //conversion to dtstamp
            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            //setting in InputDateVO
            inputDateVO.setFdate(fdate);
            inputDateVO.setFmonth(fmonth);
            inputDateVO.setFyear(fyear);
            inputDateVO.setTdate(tdate);
            inputDateVO.setTmonth(tmonth);
            inputDateVO.setTyear(tyear);
            inputDateVO.setFdtstamp(fdtstamp);
            inputDateVO.setTdtstamp(tdtstamp);

            TerminalVO terminalVO=new TerminalVO();

              /*if (functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
              {
                  terminalVO = terminalManager.getTerminalByTerminalId(terminalid);
                  if(terminalVO  == null){
                      request.setAttribute("InvalidError", "Invalid Terminal ID");
                      rdSuccess.forward(request, response);
                  }else
                  {
                      terminalVO.setTerminalId("(" + terminalVO.getTerminalId() + ")");
                  }
                  //accountid=terminalVO.getAccountId();
              }
              else
              {*/
            terminalVO.setTerminalId(sb);
            //}


            //Getting TerminalVO
            logger.error(" getting terminalVo using terminalId");
            logger.error("request.getParameter(terminalid): "+ request.getParameter("terminalid"));
            if(functions.isValueNull(request.getParameter("terminalid")) && !terminalid.equalsIgnoreCase("all"))
            {
                terminalVO = terminalManager.getTerminalByTerminalId(request.getParameter("terminalid"));
                logger.error("terminalVO::" + terminalVO.toString());
            }
            else
            {
                terminalVO = new TerminalVO();
                terminalVO.setMemberId(session.getAttribute("merchantid").toString());
            }
            //Calling transactionManager
            transactionReportVO=transactionManager.getMerchantTransactionReportAndCharts(inputDateVO,terminalVO,session);

            try
            {
                hashMapbincountrysuccessful = transactionManager.getbincountrysuccessful( toid, terminalid, country, tdtstamp, fdtstamp);
                hashMapbincountryfailed = transactionManager.getbincountryfailed( toid, terminalid, country, tdtstamp, fdtstamp);
                hashMapipcountrysuccessful = transactionManager.getipcountrysuccessful( toid,terminalid, tdtstamp, fdtstamp);
                hashMapipcountryfailed = transactionManager.getipcountryfailed( toid,terminalid, tdtstamp, fdtstamp);

            }
            catch (PZDBViolationException e)
            {
                logger.error("PZDBViolationException In TransactionSummery---", e);
            }

            //For bincountrysuccessful
            if (hashMapbincountrysuccessful.size() > 0)
            {
                for (Map.Entry<String, String> entry : hashMapbincountrysuccessful.entrySet())
                {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferbincountrysuccessful.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else if (!functions.isValueNull(terminalid))
            {
                String blankData = "Please Select Terminal ID To Display Chart";
                stringBufferbincountrysuccessful.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            else
            {
                String blankData = "No Data To Display";
                stringBufferbincountrysuccessful.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }

            bincountrysuccessful = "data : [" + stringBufferbincountrysuccessful.toString() + "],";


            // For bincountryfailed
            if (hashMapbincountryfailed.size() > 0)
            {
                for (Map.Entry<String, String> entry : hashMapbincountryfailed.entrySet())
                {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferbincountryfailed.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else if (!functions.isValueNull(terminalid))
            {
                String blankData = "Please Select Terminal ID To Display Chart";
                stringBufferbincountryfailed.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            else
            {
                String blankData = "No Data To Display";
                stringBufferbincountryfailed.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }

            bincountryfailed = "data : [" + stringBufferbincountryfailed.toString() + "],";


            // For ipcountrysuccessful
            if (hashMapipcountrysuccessful.size() > 0)
            {
                for (Map.Entry<String, String> entry : hashMapipcountrysuccessful.entrySet())
                {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferipcountrysuccessful.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else if (!functions.isValueNull(terminalid))
            {
                String blankData = "Please Select Terminal ID To Display Chart";
                stringBufferipcountrysuccessful.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            else
            {
                String blankData = "No Data To Display";
                stringBufferipcountrysuccessful.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }

            ipcountrysuccessful = "data : [" + stringBufferipcountrysuccessful.toString() + "],";


            // For ipcountryfailed
            if (hashMapipcountryfailed.size() > 0)
            {
                for (Map.Entry<String, String> entry : hashMapipcountryfailed.entrySet())
                {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferipcountryfailed.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else if (!functions.isValueNull(terminalid))
            {
                String blankData = "Please Select Terminal ID To Display Chart";
                stringBufferipcountryfailed.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            else
            {
                String blankData = "No Data To Display";
                stringBufferipcountryfailed.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }

            ipcountryfailed = "data : [" + stringBufferipcountryfailed.toString() + "],";


            request.setAttribute("terminalid",request.getParameter("terminalid"));
            request.setAttribute("bincountrysuccessful", bincountrysuccessful);
            request.setAttribute("bincountryfailed", bincountryfailed);
            request.setAttribute("ipcountrysuccessful", ipcountrysuccessful);
            request.setAttribute("ipcountryfailed", ipcountryfailed);
            request.setAttribute("transactionReportVO",transactionReportVO);
            logger.debug(" Forwarding to transactionSummary.jsp");
            rdSuccess.forward(request, response);


        }
        catch (PZTechnicalViolationException tve)
        {
            logger.error("technical exception while creating XML file::",tve);  //To change body of catch statement use File | Settings | File Templates.
            PZExceptionHandler.handleTechicalCVEException(tve,session.getAttribute("merchantid").toString(),"Technical exception while creating Xml file for Transaction Summary");
            request.setAttribute("catchError","Kindly check transaction summary After sometime");
            rdError.forward(request,response);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("Sql exception while connecting to Db or due to incorrect query::", dbe);  //To change body of catch statement use File | Settings | File Templates.
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(),"Sql exception while getting transaction summary details");
            request.setAttribute("catchError","Kindly check transaction summary After sometime");
            rdError.forward(request,response);
        }
        catch (ParseException pe)
        {
            logger.error("parsing exception while parsing date picker to Java format::", pe);  //To change body of catch statement use File | Settings | File Templates.
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(TransactionSummary.class.getName(),"doPost()",null,"Merchant","Parsing exception while parsing From or To date from merchant", PZTechnicalExceptionEnum.DATE_PARSING_EXCEPTION,null,pe.getMessage(),pe.getCause(),session.getAttribute("merchantid").toString(),PZOperations.TRANSACTION_SUMMARY);
            request.setAttribute("catchError","Kindly check transaction summary After sometime");
            rdError.forward(request,response);
        }
        catch (PZConstraintViolationException cve)
        {
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.TRANSACTION_SUMMARY);
            request.setAttribute("errorM", mandatoryErrorList);
            // request.setAttribute("errorO",optionalErrorList);
            rdError.forward(request, response);
        }

    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public  ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.FDATE);
        inputMandatoryParameter.add(InputFields.TDATE);
        inputMandatoryParameter.add(InputFields.TERMINALID);
        inputMandatoryParameter.add(InputFields.CURRENCY);

        InputValidator inputValidator = new InputValidator();
        // Hashtable error = null;
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,false);

        return errorList;
    }
   /* public  ValidationErrorList validateOptionalParameters(HttpServletRequest req)
    {
        List<InputFields> inputOptionalParameter= new ArrayList<InputFields>();
        inputOptionalParameter.add(InputFields.TERMINALID);
        InputValidator inputValidator = new InputValidator();
       // Hashtable error = null;
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputOptionalParameter, errorList,false);

        return errorList;
    }*/
}
