package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import com.directi.pg.Functions;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Sanjay on 2/28/2022.
 */
public class CheckoutProcess extends HttpServlet
{
    Logger log = new Logger(CheckoutProcess.class.getName());

    public void doGet(HttpServletRequest req , HttpServletResponse res) throws IOException ,ServletException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req , HttpServletResponse res) throws IOException , ServletException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        int start = 0; // start index
        int end = 0; // end index
        String errormsg="";

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        int pageno=1;
        int records=15;

        errormsg = errormsg + validateParameters(req);
        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
        {
            errormsg = "Invalid Partner ID.";
        }
        if(functions.isValueNull(errormsg)){
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutConfiguration.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String memberid = req.getParameter("memberid");
        String month = req.getParameter("month");
        String year = req.getParameter("year");

        String partnerid = req.getParameter("partnerid");
        try
        {
            if (functions.isValueNull(req.getParameter("pid")) && partner.isPartnerMemberMapped(memberid, req.getParameter("pid")))
            {
                partnerid = req.getParameter("pid");
            }
            else if (!functions.isValueNull(req.getParameter("pid")) && partner.isPartnerSuperpartnerMembersMapped(memberid, req.getParameter("partnerid")))
            {
                partnerid = req.getParameter("partnerid");
            }
            else
            {
                req.setAttribute("error","Invalid partner member configuration.");
                RequestDispatcher rd = req.getRequestDispatcher("/checkoutConfiguration.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }catch(Exception e){
            log.error("Exception---" + e);
        }
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
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;

        StringBuilder query = new StringBuilder("SELECT m.memberid,m.partnerid,m.template,m.isPharma,m.ismerchantlogo,m.isSecurityLogo,m.ispcilogo,m.ispartnerlogo," +
                "m.vbvLogo,m.isPoweredBy,m.supportSection,m.supportNoNeeded,m.masterSecureLogo,m.consent,checkoutTimer,checkoutTimerTime FROM members AS m JOIN merchant_configuration AS " +
                "mc ON m.memberid=mc.memberid JOIN partners p ON m.partnerId = p.partnerId WHERE m.memberid=? AND (m.partnerid=? OR p.superadminid=?)");

        StringBuilder countquery = new StringBuilder("select count(*) from members m , partners p  where m.memberid=? and (m.partnerid=? or p.superadminid=?)");

        query.append(" order by memberid asc LIMIT " + start + "," + end);

        Connection con = null;
        PreparedStatement p=null ,p1= null;
        ResultSet rs=null,rs1=null;

        Date date1 = new Date();
        log.debug("before try block MerchantRisk::::::" + date1.getTime());
        try
        {
            //con = Database.getConnection();
            con = Database.getRDBConnection();
            if (partner.isValueNull(memberid) && partner.isValueNull(partnerid))
            {
                p=con.prepareStatement(query.toString());
                p.setString(1,memberid);
                p.setString(2,partnerid);
                p.setString(3,partnerid);
                log.debug("query inside checkout process :::::"+p);

                hash = Database.getHashFromResultSet(p.executeQuery());
                p1=con.prepareStatement(countquery.toString());
                p1.setString(1,memberid);
                p1.setString(2,partnerid);
                p1.setString(3,partnerid);
                log.debug("count query inside checkout process:::::"+p1);
                rs = p1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                req.setAttribute("accoutIDwiseMerchantHash", loadGatewayAccounts(partnerid));
                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }
            hash.put("month", "" + month);
            hash.put("year", "" + year);
            req.setAttribute("memberdetails", hash);

        }
        catch (SystemError se)
        {
            log.error("System Error::::",se);
            sErrorMessage.append("Internal System Error");
        }
        catch (Exception e)
        {
            log.error("Exception::::",e);
            sErrorMessage.append("Internal System Error");
        }
        finally
        {
            try
            {
                Database.closeResultSet(rs);
                Database.closeResultSet(rs1);
                Database.closePreparedStatement(p);
                Database.closeConnection(con);
                //con.close();
            }
            catch (Exception e)
            {
                log.error("SQL Exception::::",e);
                sErrorMessage.append("Internal System Error");
            }
        }


        PartnerManager partnerManager = new PartnerManager();
        Map<String,Object> merchantTemplateSetting= new HashMap<String, Object>();
        Map<String,Object> partnerTemplateSetting= new HashMap<String, Object>();

        try
        {
            if (functions.isValueNull(memberid))
            {
                merchantTemplateSetting=partnerManager.getSavedMemberTemplateDetails(memberid);
            }
            if(functions.isValueNull(partnerid))
            {
                partnerTemplateSetting = partnerManager.getPartnerSavedMemberTemplateDetails(partnerid);
            }


        }
        catch (PZDBViolationException e)
        {
            log.error("Exception while getting template preference",e);
            sErrorMessage.append("Kindly check Transaction setting After some time");
            PZExceptionHandler.handleDBCVEException(e, memberid, "While getting template Information for member in partner transaction setting");
        }
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
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        //chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error",errormsg);
        req.setAttribute("merchantTemplateSetting",merchantTemplateSetting);
        req.setAttribute("partnerTemplateSetting",partnerTemplateSetting);
        log.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/checkoutConfiguration.jsp?ctoken="+user.getCSRFToken());
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
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    TreeMap<Integer,String> loadGatewayAccounts(String partnerId)
    {
        TreeMap<Integer,String> gatewayaccounts=new TreeMap<Integer,String>();
        Connection conn=null;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            rs = Database.executeQuery("select g.accountid,g.merchantid,g.pgtypeid,t.currency,t.name from gateway_accounts as g JOIN gateway_type as t ON t.pgtypeid=g.pgtypeid JOIN gateway_account_partner_mapping as gapm ON g.accountid=gapm.accountid and gapm.isActive='Y' and gapm.partnerid="+partnerId+" order by g.accountid asc ", conn);
            while (rs.next())
            {
                gatewayaccounts.put(rs.getInt("accountid"), rs.getString("merchantid")+"-"+rs.getString("name")+"-"+rs.getString("currency"));
            }
        }
        catch(Exception e)
        {
            log.error("Exception while loading partner managed bank account",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return gatewayaccounts;

    }

}
