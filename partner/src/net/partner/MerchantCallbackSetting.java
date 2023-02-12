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
 * Created by Kanchan on 04-03-2021.
 */
public class MerchantCallbackSetting extends  HttpServlet
{
    private static Logger logger= new Logger(MerchantCallbackSetting.class.getName());
    private Functions functions= new Functions();

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        HttpSession session= request.getSession();
        User user= (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner= new PartnerFunctions();
        PartnerManager partnerManager= new PartnerManager();

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        Functions function= new Functions();
        String errorMsg= "";
        HashMap hash=null;
        StringBuilder sErrormsg= new StringBuilder();
        errorMsg = errorMsg + validateParameters(request);

        if (!ESAPI.validator().isValidInput("pid",request.getParameter("pid"),"Numbers",10, true))
        {
            errorMsg= "Invalid Partner Id";
        }
        if (function.isValueNull(errorMsg))
        {
            request.setAttribute("error1",errorMsg);
            RequestDispatcher rd= request.getRequestDispatcher("/merchantcallbacksettings.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }
        String memberId = request.getParameter("memberid");

        String partnerid =request.getParameter("partnerid");
        try
        {
            if (functions.isValueNull(request.getParameter("pid")) && partner.isPartnerMemberMapped(memberId, request.getParameter("pid")))
            {
                partnerid = request.getParameter("pid");
            }
            else if (!functions.isValueNull(request.getParameter("pid")) && partner.isPartnerSuperpartnerMembersMapped(memberId, request.getParameter("partnerid")))
            {
                partnerid = request.getParameter("partnerid");
            }
            else
            {
                request.setAttribute("error1","Invalid partner member configuration.");
                RequestDispatcher rd = request.getRequestDispatcher("/merchantcallbacksettings.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
        }catch(Exception e){
            logger.error("Exception---" + e);
        }

        try
        {
            hash= partnerManager.getPartnerMerchantCallbackSettingList(memberId,partnerid);
        }
        catch (Exception e)
        {
            logger.error("Exception:: ",e);
            sErrormsg.append("Internal error while processing your request");
            request.setAttribute("error1",errorMsg);
            RequestDispatcher rd= request.getRequestDispatcher("/merchantcallbacksettings.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }
        request.setAttribute("memberdetails",hash);
        request.setAttribute("sMessage",sErrormsg.toString());
        request.setAttribute("error1",errorMsg);
        RequestDispatcher rd= request.getRequestDispatcher("/merchantcallbacksettings.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request,response);
    }
    private String validateParameters(HttpServletRequest request)
    {
        InputValidator inputValidator= new InputValidator();
        String error= "";

        List<InputFields> inputFieldsListOptional= new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        ValidationErrorList validationErrorList= new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListOptional,validationErrorList,false);

        if (!validationErrorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if(validationErrorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(validationErrorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(validationErrorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}
