package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.enums.PartnerTemplatePreference;
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
 * Created by Admin on 1/23/2018.
 */
public class SetReservesTemplate extends HttpServlet
{
    private static Logger logger = new Logger(AddTemplateColor.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        int updRecs = 0;
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("member is logout ");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
        System.out.println("dfghyujk");
        Functions functions = new Functions();
        String errorMsg = "";
        String EOL = "<br>";
        String partnerId = req.getParameter("partnerId");
        String AHEADPANELFONT_COLOR = "";
        String ABODYPANELFONT_COLOR = "";
        String APANELHEADING_COLOR = "";
        String APANELBODY_COLOR = "";
        String AMAINBACKGROUNDCOLOR = "";
        String ATRANSACTIONPAGEMERCHANTLOGO = req.getParameter(PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.toString());
        String ABODY_BACKGROUND_COLOR ="";
        String ABODY_FOREGROUND_COLOR = "";
        String ANAVIGATION_FONT_COLOR = "";
        String ATEXTBOX_COLOR = "";
        String AICON_VECTOR_COLOR = "";
        String AMAILBACKGROUNDCOLOR = "";
        String AMAIL_PANELHEADING_COLOR = "";
        String AMAIL_HEADPANELFONT_COLOR = "";
        String AMAIL_BODYPANELFONT_COLOR = "";

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

        if (!ESAPI.validator().isValidInput("ABODY_BACKGROUND_COLOR ", req.getParameter(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Body Background Color" + EOL;
        else
            ABODY_BACKGROUND_COLOR = req.getParameter(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.toString());

        if (!ESAPI.validator().isValidInput("AHEADPANELFONT_COLOR ", req.getParameter(PartnerTemplatePreference.AHEADPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
        else
            AHEADPANELFONT_COLOR = req.getParameter(PartnerTemplatePreference.AHEADPANELFONT_COLOR.toString());

        if (!ESAPI.validator().isValidInput("ABODYPANELFONT_COLOR ", req.getParameter(PartnerTemplatePreference.ABODYPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Heading Label Font Color" + EOL;
        else
            ABODYPANELFONT_COLOR = req.getParameter(PartnerTemplatePreference.ABODYPANELFONT_COLOR.toString());

        if (!ESAPI.validator().isValidInput("APANELHEADING_COLOR ", req.getParameter(PartnerTemplatePreference.APANELHEADING_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Panel Heading Color" + EOL;
        else
            APANELHEADING_COLOR = req.getParameter(PartnerTemplatePreference.APANELHEADING_COLOR.toString());
        if (!ESAPI.validator().isValidInput("APANELBODY_COLOR ", req.getParameter(PartnerTemplatePreference.APANELBODY_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Left Navigation Color" + EOL;
        else
            APANELBODY_COLOR = req.getParameter(PartnerTemplatePreference.APANELBODY_COLOR.toString());
        if (!ESAPI.validator().isValidInput("AMAINBACKGROUNDCOLOR ", req.getParameter(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Template BackGround Color" + EOL;
        else
            AMAINBACKGROUNDCOLOR = req.getParameter(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.toString());
        if (!ESAPI.validator().isValidInput("ABODY_FOREGROUND_COLOR ", req.getParameter(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Body ForeGround Color" + EOL;
        else
            ABODY_FOREGROUND_COLOR = req.getParameter(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.toString());

        if (!ESAPI.validator().isValidInput("ANAVIGATION_FONT_COLOR ", req.getParameter(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Navigation Font Color" + EOL;
        else
            ANAVIGATION_FONT_COLOR = req.getParameter(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.toString());
        if (!ESAPI.validator().isValidInput("ATEXTBOX_COLOR ", req.getParameter(PartnerTemplatePreference.ATEXTBOX_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Textbox Color" + EOL;
        else
            ATEXTBOX_COLOR = req.getParameter(PartnerTemplatePreference.ATEXTBOX_COLOR.toString());
        if (!ESAPI.validator().isValidInput("AICON_VECTOR_COLOR ", req.getParameter(PartnerTemplatePreference.AICON_VECTOR_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Icon Vector Color" + EOL;
        else
            AICON_VECTOR_COLOR = req.getParameter(PartnerTemplatePreference.AICON_VECTOR_COLOR.toString());
        if (!ESAPI.validator().isValidInput("AMAILBACKGROUNDCOLOR ", req.getParameter(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Mail Template BackGround Color" + EOL;
        else
            AMAILBACKGROUNDCOLOR = req.getParameter(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString());
        if (!ESAPI.validator().isValidInput("AMAIL_PANELHEADING_COLOR ", req.getParameter(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Mail Panel Heading Color" + EOL;
        else
            AMAIL_PANELHEADING_COLOR = req.getParameter(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString());

        if (!ESAPI.validator().isValidInput("AMAIL_HEADPANELFONT_COLOR ", req.getParameter(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Mail Label Font Color" + EOL;
        else
            AMAIL_HEADPANELFONT_COLOR = req.getParameter(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString());
        if (!ESAPI.validator().isValidInput("AMAIL_BODYPANELFONT_COLOR ", req.getParameter(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Table Color" + EOL;
        else
            AMAIL_BODYPANELFONT_COLOR = req.getParameter(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString());



        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_BODYNFOOTER_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Body & Footer Color" + EOL;
        else
            NEW_CHECKOUT_BODYNFOOTER_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_HEADERBACKGROUND_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Header Background Color" + EOL;
        else
            NEW_CHECKOUT_HEADERBACKGROUND_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_NAVIGATIONBAR_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Navigation Bar" + EOL;
        else
            NEW_CHECKOUT_NAVIGATIONBAR_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_BUTTON_FONT_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Button Font Color" + EOL;
        else
            NEW_CHECKOUT_BUTTON_FONT_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_HEADER_FONT_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Header Font Color" + EOL;
        else
            NEW_CHECKOUT_HEADER_FONT_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString());


        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_FULLBACKGROUND_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Full Background Color" + EOL;
        else
            NEW_CHECKOUT_FULLBACKGROUND_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_LABEL_FONT_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
        else
            NEW_CHECKOUT_LABEL_FONT_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Navigation Bar Font Color" + EOL;
        else
            NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_BUTTON_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Button Color" + EOL;
        else
            NEW_CHECKOUT_BUTTON_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_ICON_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Icon Color" + EOL;
        else
            NEW_CHECKOUT_ICON_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_TIMER_COLOR ", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()), "colorForTemplate", 10, true))
            errorMsg = errorMsg + "Invalid Template Color" + EOL;
        else
            NEW_CHECKOUT_TIMER_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_BOX_SHADOW", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()), "SafeString", 50, true))
            errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
        else
            NEW_CHECKOUT_BOX_SHADOW = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_FOOTER_FONT_COLOR", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()), "SafeString", 50, true))
            errorMsg = errorMsg + "Invalid Footer Font Color" + EOL;
        else
            NEW_CHECKOUT_FOOTER_FONT_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString());

        if (!ESAPI.validator().isValidInput("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR", req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()), "SafeString", 50, true))
            errorMsg = errorMsg + "Invalid Footer BackGround Color" + EOL;
        else
            NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = req.getParameter(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString());

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        boolean isPartnerSaved = false;
        Map<String, Object> partnerTemplateInformationUpdate = new HashMap<String, Object>();
        Map<String, Object> partnerTemplateInformationInsert = new HashMap<String, Object>();
        Map<String, Object> partnerTemplateInformationDelete = new HashMap<String, Object>();
        boolean isPartnerUpdated = false;
        boolean isPartnerDeleted = false;

        try
        {
            if (functions.isValueNull(partnerId))
            {
                PartnerManager partnerManager = new PartnerManager();

                Map<String, Object> partnerPresentTemplateDetails = partnerManager.getPartnerSavedMemberTemplateDetails(partnerId);
                if (!functions.isValueNull(ABODY_BACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name(), ABODY_BACKGROUND_COLOR);
                }
                else if (functions.isValueNull(ABODY_BACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name(), ABODY_BACKGROUND_COLOR);
                }
                else if (functions.isValueNull(ABODY_BACKGROUND_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name(), ABODY_BACKGROUND_COLOR);
                }

                if (!functions.isValueNull(ABODY_FOREGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name(), ABODY_FOREGROUND_COLOR);
                }
                else if (functions.isValueNull(ABODY_FOREGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name(), ABODY_FOREGROUND_COLOR);
                }
                else if (functions.isValueNull(ABODY_FOREGROUND_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name(), ABODY_FOREGROUND_COLOR);
                }

                if (!functions.isValueNull(ANAVIGATION_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name(), ANAVIGATION_FONT_COLOR);
                }
                else if (functions.isValueNull(ANAVIGATION_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name(), ANAVIGATION_FONT_COLOR);
                }
                else if (functions.isValueNull(ANAVIGATION_FONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name(), ANAVIGATION_FONT_COLOR);
                }
                if (!functions.isValueNull(ATEXTBOX_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ATEXTBOX_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.ATEXTBOX_COLOR.name(), ATEXTBOX_COLOR);
                }
                else if (functions.isValueNull(ATEXTBOX_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ATEXTBOX_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.ATEXTBOX_COLOR.name(), ATEXTBOX_COLOR);
                }
                else if (functions.isValueNull(ATEXTBOX_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.ATEXTBOX_COLOR.name(), ATEXTBOX_COLOR);
                }
                if (!functions.isValueNull(AICON_VECTOR_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AICON_VECTOR_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AICON_VECTOR_COLOR.name(), AICON_VECTOR_COLOR);
                }
                else if (functions.isValueNull(AICON_VECTOR_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AICON_VECTOR_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AICON_VECTOR_COLOR.name(), AICON_VECTOR_COLOR);
                }
                else if (functions.isValueNull(AICON_VECTOR_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AICON_VECTOR_COLOR.name(), AICON_VECTOR_COLOR);
                }
                if (!functions.isValueNull(AMAILBACKGROUNDCOLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.name(), AMAILBACKGROUNDCOLOR);
                }
                else  if (functions.isValueNull(AMAILBACKGROUNDCOLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.name(), AMAILBACKGROUNDCOLOR);
                }
                else if (functions.isValueNull(AMAILBACKGROUNDCOLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.name(), AMAILBACKGROUNDCOLOR);
                }
                if (!functions.isValueNull(AMAIL_PANELHEADING_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.name(), AMAIL_PANELHEADING_COLOR);
                }
                else   if (functions.isValueNull(AMAIL_PANELHEADING_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.name(), AMAIL_PANELHEADING_COLOR);
                }
                else if (functions.isValueNull(AMAIL_PANELHEADING_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.name(), AMAIL_PANELHEADING_COLOR);
                }
                if (!functions.isValueNull(AMAIL_HEADPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.name(), AMAIL_HEADPANELFONT_COLOR);
                }
                else if (functions.isValueNull(AMAIL_HEADPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.name(), AMAIL_HEADPANELFONT_COLOR);
                }
                else if (functions.isValueNull(AMAIL_HEADPANELFONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.name(), AMAIL_HEADPANELFONT_COLOR);
                }
                if (!functions.isValueNull(AMAIL_BODYPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.name(), AMAIL_BODYPANELFONT_COLOR);
                }
                else  if (functions.isValueNull(AMAIL_BODYPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.name(), AMAIL_BODYPANELFONT_COLOR);
                }
                else if (functions.isValueNull(AMAIL_BODYPANELFONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.name(), AMAIL_BODYPANELFONT_COLOR);
                }
                if (!functions.isValueNull(AHEADPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name(), AHEADPANELFONT_COLOR);
                }
                else  if (functions.isValueNull(AHEADPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name(), AHEADPANELFONT_COLOR);
                }
                else if (functions.isValueNull(AHEADPANELFONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name(), AHEADPANELFONT_COLOR);
                }
                if (!functions.isValueNull(ABODYPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name(), ABODYPANELFONT_COLOR);
                }
                else  if (functions.isValueNull(ABODYPANELFONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name(), ABODYPANELFONT_COLOR);
                }
                else if (functions.isValueNull(ABODYPANELFONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name(), ABODYPANELFONT_COLOR);
                }
                if (!functions.isValueNull(APANELHEADING_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.APANELHEADING_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.APANELHEADING_COLOR.name(), APANELHEADING_COLOR);
                }
                else  if (functions.isValueNull(APANELHEADING_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.APANELHEADING_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.APANELHEADING_COLOR.name(), APANELHEADING_COLOR);
                }
                else if (functions.isValueNull(APANELHEADING_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.APANELHEADING_COLOR.name(), APANELHEADING_COLOR);
                }
                if (!functions.isValueNull(APANELBODY_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.APANELBODY_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.APANELBODY_COLOR.name(), APANELBODY_COLOR);
                }
                else if (functions.isValueNull(APANELBODY_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.APANELBODY_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.APANELBODY_COLOR.name(), APANELBODY_COLOR);
                }
                else if (functions.isValueNull(APANELBODY_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.APANELBODY_COLOR.name(), APANELBODY_COLOR);
                }
                if (!functions.isValueNull(AMAINBACKGROUNDCOLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name(), AMAINBACKGROUNDCOLOR);
                }
                else if (functions.isValueNull(AMAINBACKGROUNDCOLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name(), AMAINBACKGROUNDCOLOR);
                }
                else if (functions.isValueNull(AMAINBACKGROUNDCOLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name(), AMAINBACKGROUNDCOLOR);
                }

                
                if (!functions.isValueNull(NEW_CHECKOUT_BODYNFOOTER_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BODYNFOOTER_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BODYNFOOTER_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name(), NEW_CHECKOUT_BODYNFOOTER_COLOR);
                }


                if (!functions.isValueNull(NEW_CHECKOUT_HEADERBACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_HEADERBACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_HEADERBACKGROUND_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name(), NEW_CHECKOUT_HEADERBACKGROUND_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_NAVIGATIONBAR_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);
                }
                else if (functions.isValueNull(ABODY_BACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_NAVIGATIONBAR_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_BUTTON_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BUTTON_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BUTTON_FONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name(), NEW_CHECKOUT_BUTTON_FONT_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_HEADER_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_HEADER_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_HEADER_FONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name(), NEW_CHECKOUT_HEADER_FONT_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_FULLBACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_FULLBACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_FULLBACKGROUND_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name(), NEW_CHECKOUT_FULLBACKGROUND_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_LABEL_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_LABEL_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_LABEL_FONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name(), NEW_CHECKOUT_LABEL_FONT_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name(), NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_BUTTON_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), ABODY_BACKGROUND_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BUTTON_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), NEW_CHECKOUT_BUTTON_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BUTTON_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name(), NEW_CHECKOUT_BUTTON_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_ICON_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_ICON_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_ICON_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name(), NEW_CHECKOUT_ICON_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_TIMER_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_TIMER_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_TIMER_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name(), NEW_CHECKOUT_TIMER_COLOR);
                }

                if (!functions.isValueNull(NEW_CHECKOUT_BOX_SHADOW) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BOX_SHADOW) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_BOX_SHADOW))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name(), NEW_CHECKOUT_BOX_SHADOW);
                }

                if(!functions.isValueNull(NEW_CHECKOUT_FOOTER_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                {
                 partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);
                }
                else if(functions.isValueNull(NEW_CHECKOUT_FOOTER_FONT_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_FOOTER_FONT_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name(), NEW_CHECKOUT_FOOTER_FONT_COLOR);
                }

                if(!functions.isValueNull(NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationDelete.put(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);
                }
                else if(functions.isValueNull(NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR) && partnerPresentTemplateDetails.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                {
                    partnerTemplateInformationUpdate.put(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);
                }
                else if (functions.isValueNull(NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR))
                {
                    partnerTemplateInformationInsert.put(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name(), NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR);
                }

                isPartnerDeleted= partnerManager.deletePartnerTemplateDetails(partnerTemplateInformationDelete, partnerId);
                isPartnerSaved = partnerManager.insertPartnerTemplateDetails(partnerTemplateInformationInsert, partnerId);
                isPartnerUpdated = partnerManager.updatePartnerTemplateDetails(partnerTemplateInformationUpdate, partnerId);
                logger.debug("isPartnerSaved::"+isPartnerSaved + isPartnerUpdated +isPartnerDeleted);
            }

        }
        catch (Exception e)
        {
            logger.error("Exception--->", e);
        }

        sSuccessMessage.append(updRecs + " Records Updated");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        req.setAttribute("errormessage",errorMsg);
        String redirectpage = "/addTemplateColors.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", sSuccessMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
        return;
    }
}

