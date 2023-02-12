import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.AccountManager;
import com.manager.TerminalManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.AccountVO;
import com.manager.vo.InputDateVO;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
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
import java.util.ArrayList;
import java.util.List;

//import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 7/28/14
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountSummary1 extends HttpServlet
{
    private static Logger logger = new Logger(AccountSummary.class.getName());
    private CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        Merchants merchants= new Merchants();
        TerminalManager terminalManager=new TerminalManager();
        AccountManager accountManager= new AccountManager();
        InputDateVO inputDateVO= new InputDateVO();
        AccountVO accountVO= null;

        //Date transformation;
        //Calendar cal= Calendar.getInstance();
       // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
       // Date date= null;
        //session
        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/accountSummary.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/accountSummary.jsp?Success=YES&ctoken="+user.getCSRFToken());

        String terminalid = request.getParameter("terminalid");
        String sb = request.getParameter("terminalbuffer");
      //  String currency = request.getParameter("currency");
        //String accountid=null;

        // Validating Input Parameters
        ValidationErrorList mandatoryErrorList = validateMandatoryParameters(request);
        try
        {

            if(!mandatoryErrorList.isEmpty())
            {
                logger.debug("Invalid data Provided for Account Summary");
                StringBuffer errorMessage= new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage,mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(AccountSummary.class.getName(),"doPost()",null,"Merchant",errorMessage.toString(),PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            //getting date Parameter
            logger.debug("Valid data provided for Account Summary");
            logger.debug("From ::"+request.getParameter("fromdate")+" Todate::"+request.getParameter("todate"));

            //commented out for changes from & to date issue
           /* String fromDate=request.getParameter("fromdate");
            String toDate= request.getParameter("todate");
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
            inputDateVO.setTdtstamp(tdtstamp);*/

            TerminalVO terminalVO=new TerminalVO();
            Functions functions=new Functions();

            if(functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
            {
                terminalVO=terminalManager.getTerminalByTerminalId(terminalid);
                terminalVO.setTerminalId("("+terminalVO.getTerminalId()+")");
               // accountid=terminalVO.getAccountId();
            }
            else
            {
                terminalVO.setTerminalId(sb);
            }

            //Getting TerminalVO
            logger.debug(" getting terminalVo using terminalId");
            terminalVO=terminalManager.getTerminalByTerminalId(request.getParameter("terminalid"));
            logger.debug("terminalVO::"+terminalVO.toString());
            //Calling AccountManager
            logger.debug(" Calculating Charges ");
            accountVO=accountManager.generatePayoutReport(inputDateVO,terminalVO);

            request.setAttribute("terminalid",request.getParameter("terminalid"));
            request.setAttribute("AccountVO",accountVO);
            logger.debug(" Forwarding to accountSummary.jsp");
            rdSuccess.forward(request,response);
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("Db exception while getting Data for Account summary",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(),"Sql exception while getting Account summary details");
            request.setAttribute("catchError","Kindly check for the Account summary after sometime");
            rdError.forward(request,response);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("Constraint violation Exception::",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.Account_summary);
            request.setAttribute("error",mandatoryErrorList);
            rdError.forward(request,response);
            return;
        }

        /*catch(Exception e)
        {
            logger.error("Error main",e);
            Functions.NewShowConfirmation("Sorry","Internal Error While Showing Account Summary");
        }*/

    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public  ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.TERMINALID);
        /*inputMandatoryParameter.add(InputFields.FDATE);
        inputMandatoryParameter.add(InputFields.TDATE);*/
        InputValidator inputValidator = new InputValidator();
      //  Hashtable error = null;
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,false);

        return errorList;
    }
}
