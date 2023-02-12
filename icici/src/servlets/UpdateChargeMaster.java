import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.ChargeValidationManager;
import com.manager.vo.payoutVOs.ChargeMasterVO;
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
 * User: Administrator
 * Date: 1/19/15
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateChargeMaster extends HttpServlet
{

    private static Logger logger = new Logger(UpdateChargeMaster.class.getName());
    private static Functions functions = new Functions();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {

        HttpSession session = Functions.getNewSession(request);
        String role = "Admin";
        String username = (String) session.getAttribute("username");
        String actionExecutorId = (String) session.getAttribute("merchantid");
        String actionExecutorName = role + "-" + username;

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        String status = "failed";
        String statusDescription = "";
        RequestDispatcher rdError = request.getRequestDispatcher("/singleChargeMasterDetails.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/singleChargeMasterDetails.jsp?MES=success&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdUpdatingSuccess = request.getRequestDispatcher("/listChargeMaster.jsp?MES=success&ctoken=" + user.getCSRFToken());
        ChargeManager chargeManager = new ChargeManager();
        if (functions.isValueNull(request.getParameter("modify")))
        {
            try
            {
                ChargeMasterVO chargeMasterVO = null;
                logger.debug("inside getting single charge Master details");
                String businessChargeId = request.getParameter("modify");
                chargeMasterVO = chargeManager.getBusinessChargeDetails(businessChargeId);
                request.setAttribute("chargeMasterVO", chargeMasterVO);
                rdSuccess.forward(request, response);
            }
            catch (Exception e)
            {
                logger.error(e);
            }

        }
        else if (functions.isValueNull(request.getParameter("update")))
        {
            logger.debug("inside update charge master update");
            ChargeMasterVO chargeMasterVO = new ChargeMasterVO();
            chargeMasterVO.setChargeId(request.getParameter("chargeid"));
            chargeMasterVO.setChargeName(request.getParameter("chargename"));
            chargeMasterVO.setInInputRequired(request.getParameter("isinputrequired"));
            chargeMasterVO.setKeyName(request.getParameter("chargeTechName"));
            chargeMasterVO.setValueType(request.getParameter("valuetype"));
            chargeMasterVO.setCategory(request.getParameter("category"));
            chargeMasterVO.setKeyword(request.getParameter("keyword"));
            chargeMasterVO.setSubKeyword(request.getParameter("subkeyword"));
            chargeMasterVO.setFrequency(request.getParameter("frequency"));
            chargeMasterVO.setSequenceNumber(request.getParameter("sequencenum"));

            ChargeValidationManager validationManager = new ChargeValidationManager();
            String error = validationManager.performMandatoryChargeMasterValidation(chargeMasterVO);
            error = error + validationManager.performOptionalChargeMasterValidation(chargeMasterVO);
            if (functions.isValueNull(request.getParameter("chargename"))&&functions.hasHTMLTags(request.getParameter("chargename")))
            {
                error = error + "Invalid Chargename.";
            }
            if (functions.isValueNull(request.getParameter("chargeTechName"))&&functions.hasHTMLTags(request.getParameter("chargeTechName")))
            {
                error = error + "Invalid Charge Technical Name.";
            }
            if (error.length() > 0)
            {
                request.setAttribute("chargeMasterVO", chargeMasterVO);
                request.setAttribute("error", error);
                rdError.forward(request, response);
                return;
            }
            else
            {
                try
                {
                    boolean isChargeUpdated = chargeManager.updateBusinessCharge(chargeMasterVO);
                    if (isChargeUpdated)
                    {
                        status = "success";
                        statusDescription = "Charge Updated successfully";
                    }
                    else
                    {
                        status = "failed";
                        statusDescription = "Charge Updation Failed ";
                    }
                }
                catch (Exception e)
                {
                    logger.error("Exceptoin : ", e);
                    status = "failed";
                    statusDescription = e.getMessage();
                }
                request.setAttribute("status", status);
                request.setAttribute("statusDescription", statusDescription);
                rdUpdatingSuccess.forward(request, response);
            }

        }
    }
}
