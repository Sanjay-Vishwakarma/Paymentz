import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.AccountManager;
import com.manager.TerminalManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ChargeVO;
import com.manager.vo.PaginationVO;
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

//import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 11/19/14
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargesList extends HttpServlet
{
    private static final int RECORSPERPAGE =15;
    private static Logger logger = new Logger(ChargesList.class.getName());
    //for getting validation message
    private CommonFunctionUtil  commonFunctionUtil = new CommonFunctionUtil();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        //Merchants class is instantiated
        Merchants merchants= new Merchants();
        // Manager instantiated
        TerminalManager terminalManager=new TerminalManager();
        AccountManager accountManager= new AccountManager();
        //Vo instantiated
        PaginationVO paginationVO = new PaginationVO();
        // List of Vo instantiated
        List<ChargeVO> chargeVOList=null;

        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/charges.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/charges.jsp?Success=YES&ctoken="+user.getCSRFToken());

        String terminalid = request.getParameter("terminalid");
        String sb = request.getParameter("terminalbuffer");
       // String currency = request.getParameter("currency");
       // String accountid=null;

        //validation for mandatory parameters
        ValidationErrorList mandatoryErrorList = validateMandatoryParameters(request);
        try
        {
            // Validating Input Parameters
            if(!mandatoryErrorList.isEmpty())
            {
                logger.debug("Invalid data Provided for Charge List");
                StringBuffer errorMessage=new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage,mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(ChargesList.class.getName(),"doPost()",null,"Merchant",errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);

            }

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

            logger.debug(" getting terminalVo using terminalId");
            terminalVO=terminalManager.getTerminalByTerminalId(request.getParameter("terminalid"));

            //setting Pagination VO
            //paginationVO.setInputs("fromdate="+fromDate+"&todate="+toDate+"&terminalid="+request.getParameter("terminalid")+"&paid="+isPaid);
            paginationVO.setInputs("terminalid="+request.getParameter("terminalid"));
            paginationVO.setPageNo(Functions.convertStringtoInt(request.getParameter("SPageno"),1));
            paginationVO.setPage(ChargesList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(request.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(request.getParameter("SRecords"),RECORSPERPAGE));

            chargeVOList=accountManager.getChargesAsPerTerminal(terminalVO,paginationVO);

            request.setAttribute("terminalid",request.getParameter("terminalid"));
            //setting response to charges.jsp
            request.setAttribute("PaginationVO",paginationVO);
            request.setAttribute("chargeVOList",chargeVOList);
            request.setAttribute("terminalVO",terminalVO);

            logger.debug("forwarding to charges.jsp");
            rdSuccess.forward(request,response);

        }
        catch(PZDBViolationException dbe)
        {
            logger.error("ChargeList Sql exception",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(),"Sql exception while getting charges from DB");
            request.setAttribute("catchError","There is some System Failure while retrieving Charge List,Try After sometime");
            rdError.forward(request,response);
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("ChargeList ConstrainViolation exception",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.CHARGE_LIST);
            request.setAttribute("error",mandatoryErrorList);
            rdError.forward(request,response);
        }
    }
    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.TERMINALID);
        /*inputMandatoryParameter.add(InputFields.FDATE);
        inputMandatoryParameter.add(InputFields.TDATE);*/
        InputValidator inputValidator = new InputValidator();
        //Hashtable error = null;
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,false);

        return errorList;
    }
}
