import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ChargeVO;
import com.manager.vo.payoutVOs.ChargeVersionVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/11/15
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageAccountChargesServlet extends HttpServlet
{
    Logger  logger=new Logger(ManageAccountChargesServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session)){
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String accountType=request.getParameter("accounttype");
        String responseMsg="";

        if("merchantaccount".equalsIgnoreCase(accountType))
        {
            //block code is pending for merchant account
        }
        else if("bankaccount".equalsIgnoreCase(accountType))
        {
            String EOL = "<BR>";
            StringBuilder sberror=new StringBuilder();
            if (!ESAPI.validator().isValidInput("pgtypeid",request.getParameter("pgtypeid"),"Description",100,true))
            {
                sberror.append("Invalid Gateway,");
            }
            if (!ESAPI.validator().isValidInput("accountid", request.getParameter("accountid"),"Numbers",10,false))
            {
                sberror.append("Invalid AccountId"+EOL);
            }
            if (!ESAPI.validator().isValidInput("chargename", request.getParameter("chargename"),"Numbers", 10, false))
            {
                sberror.append("Invalid Commission Name"+EOL);
            }
            if (!ESAPI.validator().isValidInput("chargevalue", request.getParameter("chargevalue"),"AmountStr", 10, false))
            {
                sberror.append("Invalid Commission Value"+EOL);
            }
            if (!ESAPI.validator().isValidInput("sequencenum", request.getParameter("sequencenum"), "Numbers", 10, false))
            {
                sberror.append("Invalid Sequence Number"+EOL);
            }
            if (sberror.length() > 0)
            {
                request.setAttribute("message",sberror.toString());
                RequestDispatcher rd=request.getRequestDispatcher("/manageGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }

            String accountId=request.getParameter("accountid");
            String isInputRequired=request.getParameter("isinputrequired");
            String chargeId=request.getParameter("chargename");
            String chargeValue=request.getParameter("chargevalue");
            String sequenceNo=request.getParameter("sequencenum");

            CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
            String effectiveStartDate=commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("effectivestartdate"),"00:00:00");
            String effectiveEndDate=commonFunctionUtil.convertDatepickerToTimestamp(request.getParameter("effectiveenddate"),"23:59:59");

            ChargeManager chargeManager=new ChargeManager();
            ChargeVO chargeVO=new ChargeVO();

            chargeVO.setAccountId(accountId);
            chargeVO.setIsInputRequired(isInputRequired);
            chargeVO.setChargevalue(chargeValue);
            chargeVO.setChargeid(chargeId);
            chargeVO.setSequencenum(sequenceNo);
            chargeVO.setActionExecutorId(actionExecutorId);
            chargeVO.setActionExecutorName(actionExecutorName);

            try
            {
                boolean checkAvailability = chargeManager.checkSequenceNoAvailability(chargeVO);
                if(checkAvailability)
                {
                    boolean checkCommissionAvailability = chargeManager.isChargeApplied(chargeVO);
                    if (checkCommissionAvailability)
                    {
                        int autoKey = chargeManager.addNewChargeOnGatewayAccount(chargeVO);
                        if (autoKey > 0)
                        {
                            //Create New version
                            ChargeVersionVO chargeVersionVO = new ChargeVersionVO();
                            chargeVersionVO.setAccounts_charges_mapping_id(autoKey);
                            chargeVersionVO.setChargeValue(Double.valueOf(chargeValue));
                            chargeVersionVO.setEffectiveStartDate(effectiveStartDate);
                            chargeVersionVO.setEffectiveEndDate(effectiveEndDate);
                            String status = chargeManager.createChargeVersionOnGatewayAccount(chargeVersionVO);
                            if ("success".equalsIgnoreCase(status))
                            {
                                responseMsg = "Commission Mapped Successfully On Account :" + accountId;
                            }
                            else
                            {
                                responseMsg = "Commission Mapping Failed On Account :" + accountId;
                            }
                        }
                        else
                        {
                            responseMsg = "Commission Mapping Failed On Account :" + accountId;
                        }
                        request.setAttribute("message", responseMsg);
                        RequestDispatcher rd = request.getRequestDispatcher("/manageGatewayAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                    else
                    {
                        responseMsg = "Commission Already Mapped On Account "+accountId;
                        request.setAttribute("message", responseMsg);
                        RequestDispatcher rd = request.getRequestDispatcher("/manageGatewayAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }
                }
                else
                {
                    responseMsg = "Please Provide Unique Sequence Number :"+sequenceNo;
                    request.setAttribute("message", responseMsg);
                    RequestDispatcher rd = request.getRequestDispatcher("/manageGatewayAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
            }
            catch (Exception e)
            {
                request.setAttribute("message",e.getMessage());
                RequestDispatcher rd=request.getRequestDispatcher("/manageGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
        }
        else
        {
            responseMsg="Invalid Request:::::::";
            request.setAttribute("message",responseMsg);
            RequestDispatcher rd=request.getRequestDispatcher("/manageGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }
    }
}
