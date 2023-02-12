import com.directi.pg.Admin;
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
 * User: sandip
 * Date: 12/6/14
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModifyBankRollingReserve extends HttpServlet
{
    private static Logger logger = new Logger(ModifyBankRollingReserve.class.getName());

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

        RequestDispatcher rd = request.getRequestDispatcher("/actionRollingReserve.jsp?ctoken="+user.getCSRFToken());


        ValidationErrorList validationErrorList=validateMandatoryParameterForAdd(request);
        if(!validationErrorList.isEmpty())
        {
            StringBuffer errrorStr=new StringBuffer();
            for(Object errorList : validationErrorList.errors())
            {
                ValidationException ve = (ValidationException) errorList;
                errrorStr.append(ve.getMessage());
                errrorStr.append(",");
            }
            logger.error("Invalid Inputs:::"+errrorStr.toString());
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + errrorStr.toString() + EOL + "</b></font></center>";
            request.setAttribute("message",errormsg);
            RequestDispatcher rd1 = request.getRequestDispatcher("/bankRollingReserveList.jsp?ctoken="+user.getCSRFToken());
            rd1.forward(request, response);
            return;
        }

        String merchantid=null;
        String rollingReserveDateTime=null;
        String mappingId=request.getParameter("id");
        String accountId=request.getParameter("accountid");
        String rollingReserveDate=request.getParameter("rollingreservedateupto");
        String rollingReserveTime=request.getParameter("rollingRelease_Time");

        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        GatewayManager gatewayManager=new GatewayManager();
        BankManager bankManager=new BankManager();

        rollingReserveDateTime=commonFunctionUtil.convertDatepickerToTimestamp(rollingReserveDate,rollingReserveTime);


        GatewayAccountVO gatewayAccountVO=gatewayManager.getGatewayAccountForAccountId(accountId);
        merchantid=gatewayAccountVO.getGatewayAccount().getMerchantId();


        BankRollingReserveVO bankRollingReserveVO = new BankRollingReserveVO();
        bankRollingReserveVO.setBankRollingReserveId(mappingId);
        bankRollingReserveVO.setAccountId(accountId);
        bankRollingReserveVO.setMerchantId(merchantid);
        bankRollingReserveVO.setRollingReserveDateUpTo(rollingReserveDate);
        bankRollingReserveVO.setRollingRelease_time(rollingReserveTime);
        bankRollingReserveVO.setBankRollingReserveDateTime(rollingReserveDateTime);

        try
        {

            boolean status=bankManager.updateBankRollingReserveNew(bankRollingReserveVO);

            if(status)
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Rolling Reverse Is Updated Successfully For Accountid="+accountId+"</b></font></center>";
                request.setAttribute("action","modify");
                request.setAttribute("data",bankRollingReserveVO);
                request.setAttribute("errormessage",errormsg);
            }
            else
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>Internal Error While Adding  Rolling Reverse For Accountid="+accountId+"</b></font></center>";
                request.setAttribute("action","modify");
                request.setAttribute("data",bankRollingReserveVO);
                request.setAttribute("errormessage",errormsg);
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::"+systemError);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Internal Error While Processing Request,Please Check The Log File " + EOL + "</b></font></center>";
            request.setAttribute("action","modify");
            request.setAttribute("data",bankRollingReserveVO);
            request.setAttribute("errormessage",errormsg);
        }
        catch (SQLException  se)
        {
            logger.error("SQLException::::"+se);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Internal Error While Processing Request,Please Check The Log File" + EOL + "</b></font></center>";
            request.setAttribute("action","modify");
            request.setAttribute("data",bankRollingReserveVO);
            request.setAttribute("errormessage",errormsg);
        }
        catch (Exception e)
        {
            logger.error("GenericException:::"+e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Internal Error While Processing Request,Please Check The Log File" + EOL + "</b></font></center>";
            request.setAttribute("action","modify");
            request.setAttribute("data",bankRollingReserveVO);
            request.setAttribute("errormessage",errormsg);
        }
        rd.forward(request,response);
    }


    private ValidationErrorList validateMandatoryParameterForAdd(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList=new ValidationErrorList();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.ROLLINGRESERVEDATEUPTO);
        inputFieldsListMandatory.add(InputFields.ROLLINGRELEASETIME);
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,false);
        return validationErrorList;
    }
}
