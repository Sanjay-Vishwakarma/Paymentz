package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BackOfficeAccess extends HttpServlet
{
    private static Logger logger = new Logger(BackOfficeAccess.class.getName());
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
        String errormsg="";

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
       // errormsg = errormsg + validateParameters(req);

        String memberId = req.getParameter("memberid");
        String partnerid = "";
        Functions functions = new Functions();
        try
        {
            if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
            {
                errormsg = "Invalid Partner ID.";
            }
            else if (functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerSuperpartnerMapped(req.getParameter("pid"), req.getParameter("partnerid"))){
                errormsg = "Invalid partner configuration.";
            }
           else if (!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 10, false))
            {
                errormsg = "Invalid Merchant ID.";
            }
            else if (functions.isValueNull(req.getParameter("pid")) && partner.isPartnerMemberMapped(memberId, req.getParameter("pid")))
                {
                    partnerid = req.getParameter("pid");
                }
        else if (!functions.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMembersMapped(memberId, req.getParameter("partnerid")))
                {
                    partnerid = req.getParameter("partnerid");
                }
                else
                {
                    errormsg = "Invalid partner member configuration.";
                }

        }catch(Exception e){
            logger.error("Exception---"+e);
        }

        String month = req.getParameter("month");
        String year = req.getParameter("year");

        int newmonth = 0;
        if (month != null)
        {
            newmonth = Integer.parseInt(month);
        }

        if (newmonth != 0)
        {
            if (newmonth < 10)
            {
                month = "0" + newmonth; // require as mysql require month in 01 formate
            }
            else
            {
                month = "" + newmonth;
            }
        }

        HashMap hash = null;
        try
        {
            hash = partnerManager.getPartnerBackOfficeAccessList(memberId, partnerid, month, year);
            req.setAttribute("memberdetails", hash);
        }
        catch (SystemError se)
        {
            logger.error("System Error::::",se);
            sErrorMessage.append("Internal System Error");
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
            sErrorMessage.append("Internal System Error");
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error",errormsg);
        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/backofficeaccess.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
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
