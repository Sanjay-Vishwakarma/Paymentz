import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.BankManager;
import com.manager.GatewayManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.BankRollingReserveVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/4/14
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateBankRollingReserve extends HttpServlet
{
    private static Logger logger = new Logger(UpdateBankRollingReserve.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)  throws IOException,ServletException
    {

        HttpSession session = request.getSession();
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String errormsg="";
        String EOL = "<BR>";

        RequestDispatcher rd = request.getRequestDispatcher("/addNewBankRollingReserve.jsp?ctoken="+user.getCSRFToken());

        Functions functions = new Functions();

        ValidationErrorList validationErrorList=validateMandatoryParameterForAdd(request);
        if(!validationErrorList.isEmpty())
        {
            StringBuffer errrorStr=new StringBuffer();
            for(Object errorList : validationErrorList.errors())
            {
                ValidationException ve = (ValidationException) errorList;
                errrorStr.append(ve.getMessage());
                errrorStr.append(",<br>");
            }
            if (functions.hasHTMLTags(request.getParameter("pgtypeid")))
            {
                errormsg = errormsg + "Gateway. " + EOL;
            }
            if (functions.hasHTMLTags(request.getParameter("accountid")))
            {
                errormsg = errormsg + "Account ID. " + EOL;
            }
            logger.error("Invalid Inputs:::"+errrorStr.toString());
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + errrorStr.toString() + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
            rd.forward(request, response);
            return;
        }
        String merchantId=null;
        String rollingReserveDateTime=null;
        String accountId=request.getParameter("accountid");
        String rollingReserveDate=request.getParameter("rollingreservedateupto");
        String rollingReserveTime=request.getParameter("rollingRelease_Time");
        try
        {
            BankManager bankManager = new BankManager();
            GatewayManager gatewayManager=new GatewayManager();
            CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

            if(bankManager.isRollingReserveAvailable(accountId))
            {
                logger.debug("Invalid Input ::::::");
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "This AccountID Already Available,You Can Change Date By Update Using Action " + EOL + "</b></font></center>";
                request.setAttribute("message",errormsg);
                rd.forward(request, response);
                return;
            }

            GatewayAccountVO gatewayAccountVO=gatewayManager.getGatewayAccountForAccountId(accountId);
            merchantId=gatewayAccountVO.getGatewayAccount().getMerchantId();
            rollingReserveDateTime=commonFunctionUtil.convertDatepickerToTimestamp(rollingReserveDate,rollingReserveTime);

            BankRollingReserveVO bankRollingReserveVO = new BankRollingReserveVO();
            bankRollingReserveVO.setAccountId(accountId);
            bankRollingReserveVO.setRollingReserveDateUpTo(rollingReserveDateTime);
            bankRollingReserveVO.setMerchantId(merchantId);

            boolean status=bankManager.addNewBankRollingReserve(bankRollingReserveVO);
            if(status)
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Rolling Reverse Is Added Successfully For Accountid="+accountId+"</b></font></center>";
                request.setAttribute("message",errormsg);
            }
            else
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error While Adding  Rolling Reverse For Accountid="+accountId+"</b></font></center>";
                request.setAttribute("message",errormsg);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::"+systemError);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Internal Error While Processing Request,Please Check The Log File " + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
        }
        catch (SQLException  se)
        {
            logger.error("SQLException::::"+se);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Internal Error While Processing Request,Please Check The Log File" + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
        }
        catch (Exception e)
        {
            logger.error("GenericException:::"+e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Invalid Gateway or Accountid" + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
        }
        rd.forward(request,response);
    }


    private ValidationErrorList validateMandatoryParameterForAdd(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList=new ValidationErrorList();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

      //  inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        //inputFieldsListMandatory.add(InputFields.PGTYPEID);
        inputFieldsListMandatory.add(InputFields.ROLLINGRESERVEDATEUPTO);
        inputFieldsListMandatory.add(InputFields.ROLLINGRELEASETIME);
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }
}
