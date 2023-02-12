import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.BankManager;
import com.manager.vo.ActionVO;
import com.manager.vo.BankMerchantSettlementVO;
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
 * Date: 9/10/14
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBankMerchantSettlement extends HttpServlet
{
    private static Logger logger = new Logger(UpdateBankMerchantSettlement.class.getName());

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
        //VO instance
        BankMerchantSettlementVO bankMerchantSettlementVO = new BankMerchantSettlementVO();
        ActionVO actionVO = new ActionVO();
        try
        {
            RequestDispatcher rdError = request.getRequestDispatcher("/bankMerchantSettlementMaster.jsp?MES=ERR&ctoken="+user.getCSRFToken());
            RequestDispatcher rdSuccess = request.getRequestDispatcher("/bankMerchantSettlementMaster.jsp?UPDATE=Success&ctoken="+user.getCSRFToken());

            ValidationErrorList validationErrorList=validateParameter(request);
            if(!validationErrorList.isEmpty())
            {
                if(session.getAttribute("singleBankMerchantSettlementVO")!=null)
                {
                    actionVO.setEdit();
                    request.setAttribute("actionVO",actionVO);
                    bankMerchantSettlementVO= (BankMerchantSettlementVO) session.getAttribute("singleBankMerchantSettlementVO");
                    request.setAttribute("singleBankMerchantSettlementVO",bankMerchantSettlementVO);
                }

                request.setAttribute("error", validationErrorList);
                rdError.forward(request,response);
                return;
            }
            actionVO.setActionCriteria(request.getParameter("action"));
            //setting vo using request
            bankMerchantSettlementVO.setBankMerchantSettlementId(request.getParameter("bankmerchantid"));
            bankMerchantSettlementVO.setAccountId(request.getParameter("accountid"));
            bankMerchantSettlementVO.setMemberId(request.getParameter("toid"));
            bankMerchantSettlementVO.setBankReceivedSettlementId(request.getParameter("bankreceivedid"));
            bankMerchantSettlementVO.setPaid(request.getParameter("isPaid"));

            boolean b=bankManager.updateBankMerchantSettlement(bankMerchantSettlementVO, actionVO);
            if(b)
            {
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("singleBankMerchantSettlementVO",bankMerchantSettlementVO);
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
            logger.error("main class exception ::",e);
        }
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }

    private ValidationErrorList validateParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.BANKRECEIVEDID);
        inputFieldsListMandatory.add(InputFields.ISPAID);
        inputFieldsListMandatory.add(InputFields.SMALL_ACTION);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.BANKMERCHANTID);
        inputValidator.InputValidations(req,inputFieldsListOptional,validationErrorList,true);

        return validationErrorList;
    }
}


