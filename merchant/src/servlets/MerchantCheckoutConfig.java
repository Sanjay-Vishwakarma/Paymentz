import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.enums.TemplatePreference;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
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
 * Created by Sanjay on 2/19/2022.
 */
public class MerchantCheckoutConfig extends HttpServlet
{

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {

        Logger log = new Logger(MerchantCheckoutConfig.class.getName());
        log.error("inside MerchantCheckoutConfig :::::");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rd = req.getRequestDispatcher("/merchantCheckoutConfig.jsp?ctoken=" + user.getCSRFToken());
        Merchants merchants = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            log.error("merchant logout===");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        Functions functions = new Functions();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();

        int updateRecords = 0;
        String errorMsg = "";
        String EOL = "<br>";
        String memberid = (String) session.getAttribute("merchantid");
        System.out.println("memberid :::" + memberid);
        String NEW_CHECKOUT_BODYNFOOTER_COLOR = "";
        String NEW_CHECKOUT_HEADERBACKGROUND_COLOR = "";
        String NEW_CHECKOUT_NAVIGATIONBAR_COLOR = "";
        String NEW_CHECKOUT_BUTTON_FONT_COLOR = "";
        String NEW_CHECKOUT_HEADER_FONT_COLOR = "";
        String NEW_CHECKOUT_FULLBACKGROUND_COLOR = "";
        String NEW_CHECKOUT_LABEL_FONT_COLOR = "";
        String NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = "";
        String NEW_CHECKOUT_BUTTON_COLOR = "";
        String NEW_CHECKOUT_ICON_COLOR = "";
        String NEW_CHECKOUT_TIMER_COLOR = "";
        String NEW_CHECKOUT_BOX_SHADOW = "";
        String NEW_CHECKOUT_FOOTER_FONT_COLOR = "";
        String NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = "";



        try
        {

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR)),req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR)),"colorcode", 8, false))
            {
                errorMsg = errorMsg + "Invalid Body Color" + EOL;
            }
            else
                NEW_CHECKOUT_BODYNFOOTER_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR)),req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR)),"colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Header Background Color" + EOL;
            else
                NEW_CHECKOUT_HEADERBACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR)),req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR)),"colorcode", 8, false))
                errorMsg = errorMsg + "Invalid NavigationBar Color" + EOL;
            else
                NEW_CHECKOUT_NAVIGATIONBAR_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Button Font Color" + EOL;
            else
                NEW_CHECKOUT_BUTTON_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Header Font Color" + EOL;
            else
                NEW_CHECKOUT_HEADER_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Full Background Color" + EOL;
            else
                NEW_CHECKOUT_FULLBACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Label Font Color" + EOL;
            else
                NEW_CHECKOUT_LABEL_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid NavigationBar Font Color" + EOL;
            else
                NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Botton Color" + EOL;
            else
                NEW_CHECKOUT_BUTTON_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Icon Color" + EOL;
            else
                NEW_CHECKOUT_ICON_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_ICON_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Timer Color" + EOL;
            else
                NEW_CHECKOUT_TIMER_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Box Shadow" + EOL;
            else
                NEW_CHECKOUT_BOX_SHADOW = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Footer Font Color" + EOL;
            else
                NEW_CHECKOUT_FOOTER_FONT_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR));

            if (functions.isValueNull(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR))) && !ESAPI.validator().isValidInput(req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR)), req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR)), "colorcode", 8, false))
                errorMsg = errorMsg + "Invalid Footer Background Color" + EOL;
            else
                NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = req.getParameter(String.valueOf(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR));

            StringBuilder sSuccessMessage = new StringBuilder();
            StringBuilder sErrorMessage = new StringBuilder();
            Map<String, Object> merchantTemplateInformationInsert = new HashMap<String, Object>();
            Map<String, Object> merchantTemplateInformationUpdate = new HashMap<String, Object>();
            boolean isMerchantSaved = false;
            boolean isMerchantUpdated = false;

            try
            {

                if (functions.isValueNull(memberid))
                {
                    Map<String, Object> merchantPresentTemplateDetails = merchantConfigManager.getSavedMemberTemplateDetails(memberid);

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


                    isMerchantSaved = merchantConfigManager.insertMemberTemplateDetails(merchantTemplateInformationInsert, memberid);
                    isMerchantUpdated = merchantConfigManager.updateMemberTemplateDetails(merchantTemplateInformationUpdate, memberid);
                    log.debug("isMerchantTemplateSaved::::: " + isMerchantSaved + " // isMerchantTemplateUpdated::::: " + isMerchantUpdated);

                }
            }
            catch (Exception e)
            {
                log.error("Exception ::::" + e);
                e.printStackTrace();
            }
            sSuccessMessage.append(updateRecords + "Records Updated");
            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage);
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage);

            CommonInputValidator commonInputValidator                   = new CommonInputValidator();
            CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
            MerchantDetailsVO merchantDetailsVO = null;
            MerchantDAO merchantDAO             = new MerchantDAO();
            PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

            try
            {
                merchantDetailsVO   = merchantDAO.getMemberDetails(memberid);
            }
            catch (PZDBViolationException e)
            {
                e.printStackTrace();
            }
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            System.out.println("merchantDetailsVO getPartnerId " + commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            System.out.println("merchantDetailsVO getPartnertemplate "+commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

            partnerDetailsVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            partnerDetailsVO.setPartnertemplate(commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
            commonValidatorVO.setVersion("2");
            try
            {
                commonInputValidator.setAllTemplateInformationRelatedToMerchant(commonValidatorVO.getMerchantDetailsVO(), commonValidatorVO.getVersion());
                commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());

            }
            catch (PZDBViolationException e)
            {
                e.printStackTrace();
            }
            if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getTemplate()) && "Y".equalsIgnoreCase(commonValidatorVO.getPartnerDetailsVO().getPartnertemplate()))
            {
                if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW()))
                {
                    req.setAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR());
                    req.setAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR());
                    req.setAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_BUTTON_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR());
                    req.setAttribute("NEW_CHECKOUT_ICON_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR());
                    req.setAttribute("NEW_CHECKOUT_TIMER_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_BOX_SHADOW","0 0 10px "+ commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW());

                }
                else if(functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getIconColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getIconColor()) && (functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getTimerColor()) && (functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBoxShadow()))))
                {
                    req.setAttribute("panelheading_color", "");//Panel Heading Color
                    req.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                    req.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                    req.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                    req.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                    req.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                    req.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                    req.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                    req.setAttribute("textbox_color", "#ffffff");//Textbox Color
                    req.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                    req.setAttribute("timer_color","#000000");//Timer Color
                    req.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                }
                else if((functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPanelBody_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyBgColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyFgColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTextboxColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getIconColor()) ) && (functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPanelBody_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyBgColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyFgColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTextboxColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getIconColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTimerColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBoxShadow())))
                {
                    req.setAttribute("panelheading_color", commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                    req.setAttribute("headpanelfont_color", commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                    req.setAttribute("bodypanelfont_color", commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                    req.setAttribute("panelbody_color", commonValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                    req.setAttribute("mainbackgroundcolor", commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
                    req.setAttribute("bodybgcolor", commonValidatorVO.getMerchantDetailsVO().getBodyBgColor());
                    req.setAttribute("bodyfgcolor", commonValidatorVO.getMerchantDetailsVO().getBodyFgColor());
                    req.setAttribute("navigation_font_color", commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor());
                    req.setAttribute("textbox_color", commonValidatorVO.getMerchantDetailsVO().getTextboxColor());
                    req.setAttribute("icon_color", commonValidatorVO.getMerchantDetailsVO().getIconColor());
                    req.setAttribute("timer_color",commonValidatorVO.getMerchantDetailsVO().getTimerColor());
                    req.setAttribute("box_shadow",commonValidatorVO.getMerchantDetailsVO().getBoxShadow());
                }
                else if((functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPanelBody_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyBgColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyFgColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTextboxColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getIconColor())) && (functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getIconColor()) || functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getTimerColor()) || functions.isEmptyOrNull(commonValidatorVO.getPartnerDetailsVO().getBoxShadow())))
                {

                    req.setAttribute("panelheading_color", commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                    req.setAttribute("headpanelfont_color", commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                    req.setAttribute("bodypanelfont_color", commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                    req.setAttribute("panelbody_color", commonValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                    req.setAttribute("mainbackgroundcolor", commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
                    req.setAttribute("bodybgcolor", commonValidatorVO.getMerchantDetailsVO().getBodyBgColor());
                    req.setAttribute("bodyfgcolor", commonValidatorVO.getMerchantDetailsVO().getBodyFgColor());
                    req.setAttribute("navigation_font_color", commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor());
                    req.setAttribute("textbox_color", commonValidatorVO.getMerchantDetailsVO().getTextboxColor());
                    req.setAttribute("icon_color", commonValidatorVO.getMerchantDetailsVO().getIconColor());
                    req.setAttribute("timer_color",commonValidatorVO.getMerchantDetailsVO().getTimerColor());
                    req.setAttribute("box_shadow",commonValidatorVO.getMerchantDetailsVO().getBoxShadow());
                }
                else if((functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPanelBody_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyBgColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyFgColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTextboxColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getIconColor())) && (functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getIconColor()) || functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getTimerColor()) || functions.isEmptyOrNull(commonValidatorVO.getMerchantDetailsVO().getBoxShadow())))
                {
                    req.setAttribute("panelheading_color", commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color());
                    req.setAttribute("headpanelfont_color", commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color());
                    req.setAttribute("bodypanelfont_color", commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color());
                    req.setAttribute("panelbody_color", commonValidatorVO.getPartnerDetailsVO().getPanelBody_color());
                    req.setAttribute("mainbackgroundcolor", commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color());
                    req.setAttribute("bodybgcolor", commonValidatorVO.getPartnerDetailsVO().getBodyBgColor());
                    req.setAttribute("bodyfgcolor", commonValidatorVO.getPartnerDetailsVO().getBodyFgColor());
                    req.setAttribute("navigation_font_color", commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor());
                    req.setAttribute("textbox_color", commonValidatorVO.getPartnerDetailsVO().getTextboxColor());
                    req.setAttribute("icon_color", commonValidatorVO.getPartnerDetailsVO().getIconColor());
                    req.setAttribute("timer_color",commonValidatorVO.getPartnerDetailsVO().getTimerColor());
                    req.setAttribute("box_shadow",commonValidatorVO.getPartnerDetailsVO().getBoxShadow());
                }

            }
            else if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getTemplate()) && "N".equalsIgnoreCase(commonValidatorVO.getPartnerDetailsVO().getPartnertemplate()))
            {
                if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW()))
                {
                    req.setAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR());
                    req.setAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR());
                    req.setAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_BUTTON_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR());
                    req.setAttribute("NEW_CHECKOUT_ICON_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR());
                    req.setAttribute("NEW_CHECKOUT_TIMER_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR",commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_BOX_SHADOW", "0 0 10px "+  commonValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW());
                }
                else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPanelBody_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyBgColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBodyFgColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTextboxColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getIconColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTimerColor()) || functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getBoxShadow()))
                {
                    req.setAttribute("panelheading_color", commonValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                    req.setAttribute("headpanelfont_color", commonValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                    req.setAttribute("bodypanelfont_color", commonValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                    req.setAttribute("panelbody_color", commonValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                    req.setAttribute("mainbackgroundcolor", commonValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
                    req.setAttribute("bodybgcolor", commonValidatorVO.getMerchantDetailsVO().getBodyBgColor());
                    req.setAttribute("bodyfgcolor", commonValidatorVO.getMerchantDetailsVO().getBodyFgColor());
                    req.setAttribute("navigation_font_color", commonValidatorVO.getMerchantDetailsVO().getNavigationFontColor());
                    req.setAttribute("textbox_color", commonValidatorVO.getMerchantDetailsVO().getTextboxColor());
                    req.setAttribute("icon_color", commonValidatorVO.getMerchantDetailsVO().getIconColor());
                    req.setAttribute("timer_color",commonValidatorVO.getMerchantDetailsVO().getTimerColor());
                    req.setAttribute("box_shadow",commonValidatorVO.getMerchantDetailsVO().getBoxShadow());
                }
                else
                {
                    req.setAttribute("panelheading_color", "");//Panel Heading Color
                    req.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                    req.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                    req.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                    req.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                    req.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                    req.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                    req.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                    req.setAttribute("textbox_color", "#ffffff");//Textbox Color
                    req.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                    req.setAttribute("timer_color","#000000"); //Timer Color
                    req.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)"); //Box Shadow
                }

            }
            else if ("Y".equalsIgnoreCase(commonValidatorVO.getPartnerDetailsVO().getPartnertemplate()) && "N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getTemplate()))
            {
                if(((functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPanelBody_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyBgColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBodyFgColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTextboxColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getIconColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getTimerColor()) || functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getBoxShadow()))))
                {
                    req.setAttribute("panelheading_color", commonValidatorVO.getPartnerDetailsVO().getPanelHeading_color());
                    req.setAttribute("headpanelfont_color", commonValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color());
                    req.setAttribute("bodypanelfont_color", commonValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color());
                    req.setAttribute("panelbody_color", commonValidatorVO.getPartnerDetailsVO().getPanelBody_color());
                    req.setAttribute("mainbackgroundcolor", commonValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color());
                    req.setAttribute("bodybgcolor", commonValidatorVO.getPartnerDetailsVO().getBodyBgColor());
                    req.setAttribute("bodyfgcolor", commonValidatorVO.getPartnerDetailsVO().getBodyFgColor());
                    req.setAttribute("navigation_font_color", commonValidatorVO.getPartnerDetailsVO().getNavigationFontColor());
                    req.setAttribute("textbox_color", commonValidatorVO.getPartnerDetailsVO().getTextboxColor());
                    req.setAttribute("icon_color", commonValidatorVO.getPartnerDetailsVO().getIconColor());
                    req.setAttribute("timer_color",commonValidatorVO.getPartnerDetailsVO().getTimerColor());
                    req.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR",commonValidatorVO.getPartnerDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR());
                    req.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR",commonValidatorVO.getPartnerDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR());
                    req.setAttribute("box_shadow",commonValidatorVO.getPartnerDetailsVO().getBoxShadow());
                }
                else
                {
                    req.setAttribute("panelheading_color", "");//Panel Heading Color
                    req.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                    req.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                    req.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                    req.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                    req.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                    req.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                    req.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                    req.setAttribute("textbox_color", "#ffffff");//Textbox Color
                    req.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                    req.setAttribute("timer_color","#000000");//Timer Color
                    req.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                    req.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR","#7eccad");
                    req.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR","#000000");
                }

            }
            else
            {
                //default values set for new payment page
                if(functions.isValueNull(commonValidatorVO.getVersion()) && commonValidatorVO.getVersion().equals("2"))
                {
                    req.setAttribute("panelheading_color", "#7eccad");//Panel Heading Color
                    req.setAttribute("headpanelfont_color", "#ffffff");//Label Font Color
                    req.setAttribute("bodypanelfont_color", "#ffffff");//Hading Label Font Color
                    req.setAttribute("panelbody_color", "#f3f3f3");//Left Navigation Color
                    req.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                    req.setAttribute("bodybgcolor", "#808080");//Body BackGround Color
                    req.setAttribute("bodyfgcolor", "#000000");//Body ForeGround Color
                    req.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                    req.setAttribute("textbox_color", "#7eccad");//Textbox Color
                    req.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                    req.setAttribute("timer_color","#000000");//Timer Color
                    req.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                    req.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR","#7eccad");
                    req.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR","#000000");

                }
                else
                {
                    //default values set for old payment page
                    req.setAttribute("panelheading_color", "");//Panel Heading Color
                    req.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                    req.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                    req.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                    req.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                    req.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                    req.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                    req.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                    req.setAttribute("textbox_color", "#ffffff");//Textbox Color
                    req.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                    req.setAttribute("timer_color","#000000");//Timer Color
                    req.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                    req.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR","#7eccad");
                    req.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR","#000000");
                }
            }
            req.setAttribute("errormessage", errorMsg);
            String redirectpage = "/merchantCheckoutConfig.jsp?ctoken=" + user.getCSRFToken();
            req.setAttribute("cbmessage", sSuccessMessage.toString());
            RequestDispatcher rd1 = req.getRequestDispatcher(redirectpage);
            rd1.forward(req, res);
            return;
        }
        catch (Exception e)
        {
            log.error("catch block exception error"+e);
            e.printStackTrace();
        }

    }


}
