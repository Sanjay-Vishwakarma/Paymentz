import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.vo.ActionVO;
import com.manager.vo.BankRecievedSettlementCycleVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/2/14
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBankReceivedSettlement extends HttpServlet
{
    private static Logger logger = new Logger(UpdateBankReceivedSettlement.class.getName());


    public void doPost(HttpServletRequest request,HttpServletResponse response)  throws IOException,ServletException
    {


        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        //manager instance
        BankManager bankManager = new BankManager();
        GatewayManager gatewayManager=new GatewayManager();
        //VO instance
        BankRecievedSettlementCycleVO bankRecievedSettlementCycleVO = new BankRecievedSettlementCycleVO();
        ActionVO actionVO = new ActionVO();

        RequestDispatcher rdError = request.getRequestDispatcher("/bankRecievedSettlementMaster.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankRecievedSettlementMaster.jsp?UPDATE=Success&ctoken="+user.getCSRFToken());

        try
        {
            //validation for the parameters
            ValidationErrorList validationErrorList=validateMandatoryParameter(request);

            if(request.getParameter("action").contains("_Add"))
            {
                validationErrorList= validateAddParameters(request,validationErrorList);
            }
            else
            {
                logger.debug("inside else condition of add action");
                validationErrorList=validateUpdateParameter(request,validationErrorList);
            }
            if(!validationErrorList.isEmpty())
            {
                if(session.getAttribute("singleBankRecievedSettlementCycleVO")!=null)
                {
                    actionVO.setEdit();
                    request.setAttribute("actionVO",actionVO);
                    bankRecievedSettlementCycleVO= (BankRecievedSettlementCycleVO) session.getAttribute("singleBankRecievedSettlementCycleVO");
                    request.setAttribute("singleBankRecievedSettlementCycleVO",bankRecievedSettlementCycleVO);
                }

                request.setAttribute("error", validationErrorList);
                rdError.forward(request,response);
                return;
            }
            actionVO.setActionCriteria(request.getParameter("action"));
            actionVO.setYesOrNoCriteria(request.getParameter("isdaylight"));
            //setting vo using request
            bankRecievedSettlementCycleVO.setBankSettlementReceivedId(request.getParameter("bankreceivedid"));
            bankRecievedSettlementCycleVO.setAccountId(request.getParameter("accountid"));
            bankRecievedSettlementCycleVO.setPgTypeId(request.getParameter("pgtypeid"));
            bankRecievedSettlementCycleVO.setSettlementDate(request.getParameter("settlementdate"));
            bankRecievedSettlementCycleVO.setBank_settlementId(request.getParameter("settlementcycleid"));
            bankRecievedSettlementCycleVO.setExpected_startDate(request.getParameter("expected_startDate"));
            bankRecievedSettlementCycleVO.setExpected_endDate(request.getParameter("expected_endDate"));
            bankRecievedSettlementCycleVO.setActual_startDate(request.getParameter("actual_startDate"));
            bankRecievedSettlementCycleVO.setActual_endDate(request.getParameter("actual_endDate"));
            bankRecievedSettlementCycleVO.setSettlementCronExecuted(request.getParameter("issettlementcron"));
            bankRecievedSettlementCycleVO.setPayoutCronExecuted(request.getParameter("ispayoutcron"));
            //loading gateWayTypeVO
            GatewayTypeVO gatewayTypeVO=gatewayManager.getGatewayTypeForPgTypeId(bankRecievedSettlementCycleVO.getPgTypeId());

            boolean b=bankManager.updateBankReceivedSettlementCycle(bankRecievedSettlementCycleVO,actionVO,gatewayTypeVO);
            if(b)
            {
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("singleBankRecievedSettlementCycleVO",bankRecievedSettlementCycleVO);
                rdSuccess.forward(request,response);
            }
            else
            {   validationErrorList = new ValidationErrorList();
                validationErrorList.addError("System Error",new ValidationException("System Error","Due to internal Issue"));
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
            }
        }
        catch (Exception e)
        {
            if(e.getMessage().toLowerCase().contains("adding failed"))
            {
                ValidationErrorList validationErrorList = new ValidationErrorList();
                validationErrorList.addError("Adding new Bank Received Settlement Not Allowed",new ValidationException("Adding new Bank Received Settlement Not Allowed","Due to internal Issue"));
                request.setAttribute("error",validationErrorList);
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("singleBankRecievedSettlementCycleVO",bankRecievedSettlementCycleVO);
                rdError.forward(request,response);
                return;
            }
            if(e.getMessage().toLowerCase().contains("not matching"))
            {
                ValidationErrorList validationErrorList = new ValidationErrorList();
                validationErrorList.addError("AccountId and PgTypeId is not matching",new ValidationException("AccountId and PgTypeId is not matching","No matching account id and pgTypeId"));
                request.setAttribute("error",validationErrorList);
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("singleBankRecievedSettlementCycleVO",bankRecievedSettlementCycleVO);
                rdError.forward(request,response);
                return;
            }
            if(e.getMessage().toLowerCase().contains("unique"))
            {
                ValidationErrorList validationErrorList = new ValidationErrorList();
                validationErrorList.addError("Please enter unique BankSettlement Id",new ValidationException("Please enter unique BankSettlement Id","Bank Settlement id should be unique"));
                request.setAttribute("error",validationErrorList);
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("singleBankRecievedSettlementCycleVO",bankRecievedSettlementCycleVO);
                rdError.forward(request,response);
                return;
            }
            logger.error("main class exception ::",e);
        }
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }

    private ValidationErrorList validateMandatoryParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        //inputFieldsListMandatory.add(InputFields.PGTYPEID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.EXPECTED_STARTDATE);
        inputFieldsListMandatory.add(InputFields.EXPECTED_ENDDATE);
        inputFieldsListMandatory.add(InputFields.SETTLEMENTCYCLEID);
        inputFieldsListMandatory.add(InputFields.SMALL_ACTION);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }
    private ValidationErrorList validateUpdateParameter(HttpServletRequest request,ValidationErrorList validationErrorList)
    {
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFieldsListCondition = new ArrayList<InputFields>();
        inputFieldsListCondition.add(InputFields.BANKRECEIVEDID);
        inputFieldsListCondition.add(InputFields.SETTLEMENTDATE);
        inputFieldsListCondition.add(InputFields.ACTUAL_SARTDATE);
        inputFieldsListCondition.add(InputFields.ACTUAL_ENDDATE);
        inputFieldsListCondition.add(InputFields.ISSETTLEMENTCRONEXECCUTED);
        inputFieldsListCondition.add(InputFields.ISPAYOUCRONEXECUTED);


        inputValidator.InputValidations(request,inputFieldsListCondition,validationErrorList,false);
        return validationErrorList;
    }
    private ValidationErrorList validateAddParameters(HttpServletRequest request,ValidationErrorList validationErrorList)
    {
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFieldsListCondition = new ArrayList<InputFields>();
        inputFieldsListCondition.add(InputFields.ISDAYLIGHT);

        inputValidator.InputValidations(request,inputFieldsListCondition,validationErrorList,false);
        return validationErrorList;
    }
}
