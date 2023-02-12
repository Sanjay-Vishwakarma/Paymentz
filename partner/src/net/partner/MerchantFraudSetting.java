package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/14/15
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantFraudSetting  extends HttpServlet
{
    private static Logger logger = new Logger(MerchantFraudSetting.class.getName());
    private Functions functions=new Functions();
    PartnerFunctions partnerFunctions = new PartnerFunctions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        Functions function = new Functions();
        HashMap hash = null;
        String errorMsg="";
        StringBuilder sErrorMessage = new StringBuilder();
        errorMsg = validateParameters(req);
        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
        {
            errorMsg = "Invalid Partner ID.";
        }
        if(function.isValueNull(errorMsg)){
            req.setAttribute("error",errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher("/merchantfraudsetting.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String memberId = req.getParameter("memberid");
        String partnerid ="";
        try
        {
            if (functions.isValueNull(req.getParameter("pid")) && partner.isPartnerMemberMapped(memberId, req.getParameter("pid")))
            {
                partnerid = req.getParameter("pid");
            }
            else if (!functions.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMembersMapped(memberId, req.getParameter("partnerid")))
            {
                partnerid = req.getParameter("partnerid");
            }
            else
            {
                req.setAttribute("error","Invalid partner member configuration.");
                RequestDispatcher rd = req.getRequestDispatcher("/merchantfraudsetting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }catch(Exception e){
            logger.error("Exception---" + e);
        }
         try
        {
            hash = partnerManager.getPartnerMerchantfraudSettingList(memberId, partnerid);
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
            sErrorMessage.append("Internal error while processing your request");
            req.setAttribute("error",errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher("/merchantfraudsetting.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        req.setAttribute("memberdetails", hash);
        req.setAttribute("sMessage", sErrorMessage.toString());
        req.setAttribute("error",errorMsg);
        RequestDispatcher rd = req.getRequestDispatcher("/merchantfraudsetting.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}
