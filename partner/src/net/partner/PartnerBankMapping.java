package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by Namrata Bari on 23-10-2019.
 */

public class PartnerBankMapping extends HttpServlet
{
    public static Logger logger = new Logger(PartnerBankMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        logger.debug("Enter in New partner ");
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        StringBuffer sb = new StringBuffer();
        StringBuffer error = new StringBuffer();
        ApplicationManager applicationManager = new ApplicationManager();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String PartnerBankMapping_bankid_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerBankMapping_bankid_errormsg")) ? rb1.getString("PartnerBankMapping_bankid_errormsg") : "Invalid Bank Id, kindly provide valid bank id";
        String PartnerBankMapping_partnerid_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerBankMapping_partnerid_errormsg")) ? rb1.getString("PartnerBankMapping_partnerid_errormsg") : "Invalid Partner Id, kindly provide valid partner id";
        String PartnerBankMapping_mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerBankMapping_mapping_errormsg")) ? rb1.getString("PartnerBankMapping_mapping_errormsg") : "Mapping already exist";
        String PartnerBankMapping_added_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerBankMapping_added_errormsg")) ? rb1.getString("PartnerBankMapping_added_errormsg") : "Mapping added successfully";
        String PartnerBankMapping_failed_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerBankMapping_failed_errormsg")) ? rb1.getString("PartnerBankMapping_failed_errormsg") : "Failed to map";
        String EOL = "<BR>";

        try
        {
            String bankIDetails= request.getParameter("bankId");
            String bankArr[] = bankIDetails.split("-");
            String bankId = bankArr[0];

            String partnerId =  request.getParameter("partnerid");
            if (!ESAPI.validator().isValidInput("bankId", bankIDetails, "SafeString", 255, false))
            {
                error.append(PartnerBankMapping_bankid_errormsg + EOL);
            }
            if (!ESAPI.validator().isValidInput("partnerid", partnerId, "SafeString", 255, false))
            {
                error.append(PartnerBankMapping_partnerid_errormsg + EOL);
            }
            if (error.length() > 0)
            {
                request.setAttribute("error", error.toString());
                RequestDispatcher rd = request.getRequestDispatcher("/partnerbankmapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            if(applicationManager.isPartnerBankMappingExist(bankId, partnerId))
            {
                sb.append(PartnerBankMapping_mapping_errormsg);
            }
            else if (applicationManager.addPartnerBankMapping(bankId, partnerId))
            {
                sb.append(PartnerBankMapping_added_errormsg);
            }
            else {
                sb.append(PartnerBankMapping_failed_errormsg);
            }
            request.setAttribute("error", error.toString());
            request.setAttribute("msg", sb.toString());
            RequestDispatcher rd = request.getRequestDispatcher("/partnerbankmapping.jsp?MES=ERR&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception---" + e);
        }
    }
}
