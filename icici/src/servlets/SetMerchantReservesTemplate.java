package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.MerchantConfigManager;
import com.manager.enums.TemplatePreference;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 9/6/2021.
 */
public class SetMerchantReservesTemplate extends HttpServlet
{
    private static Logger logger = new Logger(SetMerchantReservesTemplate.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        int updateRecords = 0;
        if (!Admin.isLoggedIn(session)){
            logger.debug("member is logout");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }

        Functions functions = new Functions();
        String errorMsg = "";
        String EOL = "<br>";
        String memberid = req.getParameter("memberid");
        String NEW_CHECKOUT_BODYNFOOTER_COLOR="";
        String NEW_CHECKOUT_HEADERBACKGROUND_COLOR="";
        String NEW_CHECKOUT_NAVIGATIONBAR_COLOR="";
        String NEW_CHECKOUT_BUTTON_FONT_COLOR="";
        String NEW_CHECKOUT_HEADER_FONT_COLOR="";
        String NEW_CHECKOUT_FULLBACKGROUND_COLOR="";
        String NEW_CHECKOUT_LABEL_FONT_COLOR="";
        String NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR="";
        String NEW_CHECKOUT_BUTTON_COLOR="";
        String NEW_CHECKOUT_ICON_COLOR="";
        String NEW_CHECKOUT_TIMER_COLOR="";
        String NEW_CHECKOUT_BOX_SHADOW="";
        String NEW_CHECKOUT_FOOTER_FONT_COLOR="";
        String NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR="";

        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR))))
        {
            errorMsg = errorMsg + "Invalid Body Color" + EOL;
            System.out.println("inside template if" + req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR)));
        }
        else
        {
            NEW_CHECKOUT_BODYNFOOTER_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR));
            System.out.println("inside template else" + req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR)));

        }
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR))))
            errorMsg = errorMsg + "Invalid Header Background Color" + EOL;
        else
            NEW_CHECKOUT_HEADERBACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR))))
            errorMsg = errorMsg + "Invalid NavigationBar Color" + EOL;
        else
            NEW_CHECKOUT_NAVIGATIONBAR_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR))))
            errorMsg = errorMsg + "Invalid Button Font Color" + EOL;
        else
            NEW_CHECKOUT_BUTTON_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR))))
            errorMsg = errorMsg + "Invalid Header Font Color" + EOL;
        else
            NEW_CHECKOUT_HEADER_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR))))
            errorMsg = errorMsg + "Invalid Full Background Color" + EOL;
        else
            NEW_CHECKOUT_FULLBACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR))))
            errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
        else
            NEW_CHECKOUT_LABEL_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR))))
            errorMsg = errorMsg + "Invalid NavigationBar Font Color" + EOL;
        else
            NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR))))
            errorMsg = errorMsg + "Invalid Botton Color" + EOL;
        else
            NEW_CHECKOUT_BUTTON_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR))))
            errorMsg = errorMsg + "Invalid Icon Color" + EOL;
        else
            NEW_CHECKOUT_ICON_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR))))
            errorMsg = errorMsg + "Invalid Timer Color" + EOL;
        else
            NEW_CHECKOUT_TIMER_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW))))
            errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
        else
            NEW_CHECKOUT_BOX_SHADOW = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR))))
            errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
        else
            NEW_CHECKOUT_FOOTER_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR));
        if (!functions.isValidHexCodeColor(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR))))
            errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
        else
            NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR));

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Map<String, Object> merchantTemplateInformationInsert = new HashMap<String, Object>();
        Map<String, Object> merchantTemplateInformationUpdate = new HashMap<String, Object>();
        Map<String, Object> merchantTemplateInformationDelete = new HashMap<String, Object>();
        boolean isMerchantSaved = false;
        boolean isMerchantUpdated = false;
        boolean isMerchantDeleted = false;

        try
        {
            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            if (functions.isValueNull(memberid)){
                Map<String,Object> merchantPresentTemplateDetails = merchantConfigManager.getSavedMemberTemplateDetails(memberid);

                if (NEW_CHECKOUT_BODYNFOOTER_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);
                else if (NEW_CHECKOUT_BODYNFOOTER_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);

                if (NEW_CHECKOUT_HEADERBACKGROUND_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);
                else if (functions.isValueNull(NEW_CHECKOUT_HEADERBACKGROUND_COLOR))
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);

                if (NEW_CHECKOUT_NAVIGATIONBAR_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);
                else if (NEW_CHECKOUT_NAVIGATIONBAR_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);

                if (NEW_CHECKOUT_BUTTON_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);
                else if (NEW_CHECKOUT_BUTTON_FONT_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);

                if (NEW_CHECKOUT_HEADER_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);
                else if (NEW_CHECKOUT_HEADER_FONT_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);

                if (NEW_CHECKOUT_FULLBACKGROUND_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);
                else if (NEW_CHECKOUT_FULLBACKGROUND_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);

                if (NEW_CHECKOUT_LABEL_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);
                else if (NEW_CHECKOUT_LABEL_FONT_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);

                if (NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);
                else if (NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);

                if (NEW_CHECKOUT_BUTTON_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), NEW_CHECKOUT_BUTTON_COLOR);
                else if (NEW_CHECKOUT_BUTTON_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), NEW_CHECKOUT_BUTTON_COLOR);

                if (NEW_CHECKOUT_ICON_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);
                else if (NEW_CHECKOUT_ICON_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);

                if (NEW_CHECKOUT_TIMER_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);
                else if (NEW_CHECKOUT_TIMER_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);

                if (NEW_CHECKOUT_BOX_SHADOW != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);
                else if (NEW_CHECKOUT_BOX_SHADOW != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);

                if (NEW_CHECKOUT_FOOTER_FONT_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);
                else if (NEW_CHECKOUT_FOOTER_FONT_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);

                if (NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR != null && merchantPresentTemplateDetails.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                    merchantTemplateInformationUpdate.put(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);
                else if (NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR != null)
                    merchantTemplateInformationInsert.put(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);


                isMerchantSaved   = merchantConfigManager.insertMemberTemplateDetails(merchantTemplateInformationInsert, memberid);
                isMerchantUpdated = merchantConfigManager.updateMemberTemplateDetails(merchantTemplateInformationUpdate, memberid);
//                isMerchantDeleted = merchantConfigManager.deleteMemberTemplateDetails(merchantTemplateInformationDelete, memberid);
                logger.debug("isMerchantTemplateSaved: "+ isMerchantSaved +" // isMerchantTemplateUpdated: "+ isMerchantUpdated +" // isMerchantTemplateDeleted: "+ isMerchantDeleted);
            }
        }catch (Exception e){
            logger.error("Exception-->",e);
        }

        sSuccessMessage.append(updateRecords + "Records Updated");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage);
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage);

        req.setAttribute("errormessage",errorMsg);
        String redirectpage = "/addMerchantTemplateColors.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage",sSuccessMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req,res);
        return;
    }
}
