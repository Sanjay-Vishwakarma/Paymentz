package com.payment.validators;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.AESEncryptionManager;
import com.manager.ManualRecurringManager;
import com.manager.PartnerManager;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PartnerDAO;
import com.manager.dao.TerminalDAO;
import com.manager.enums.PartnerTemplatePreference;
import com.manager.enums.TemplatePreference;
import com.manager.enums.TransReqRejectCheck;
import com.manager.helper.TransactionHelper;
import com.manager.utils.FileHandlingUtil;
import com.manager.utils.TransactionUtil;
import com.manager.vo.*;
import com.payment.Enum.PaymentModeEnum;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.p4.gateway.P4DirectDebitPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import org.apache.http.protocol.HTTP;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 7/25/14
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonInputValidator extends AbstractInputValidator
{
    private static Logger log = new Logger(CommonInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CommonInputValidator.class.getName());

    PaymentChecker paymentChecker = new PaymentChecker();

    private Functions functions = new Functions();

    /*public CommonValidatorVO performStandardKitStep1Validations(CommonValidatorVO commonValidatorVO)throws PZConstraintViolationException,PZDBViolationException
    {
        String error = "";
        Functions functions=new Functions();
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;

        String terminalid = commonValidatorVO.getTerminalId();

        PartnerManager partnerManager = new PartnerManager();
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();

        //TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        String toid=merchantDetailsVO.getMemberId();
        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
        {
            commonValidatorVO.setErrorMsg(ErrorMessages.INVALID_TOID);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid,commonValidatorVO.getTransDetailsVO().getTotype());

            if(merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
                failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error, TransReqRejectCheck.VALIDATION_TOID_INVALID.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            setAllTemplateInformationRelatedToMerchant(merchantDetailsVO);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                failedTransactionLogEntry.nonWhitelistedTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error,TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {

            error = "The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        error = validateCommonParametersStep1(commonValidatorVO,"STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        //specific parameter
        error = validateStandardKitSpecificParametersStep1(commonValidatorVO);
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
        }

        return commonValidatorVO;
    }*/

    public void getPaymentPageTemplateDetails(CommonValidatorVO standardKitValidatorVO, HttpSession session)
    {
        Functions functions = new Functions();
        if ("Y".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getTemplate()) && "Y".equalsIgnoreCase(standardKitValidatorVO.getPartnerDetailsVO().getPartnertemplate()))
        {
            if(functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW()))
            {
                session.setAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR());
                session.setAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR());
                session.setAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_BUTTON_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR());
                session.setAttribute("NEW_CHECKOUT_ICON_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR());
                session.setAttribute("NEW_CHECKOUT_TIMER_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR());
                session.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_BOX_SHADOW","0 0 10px "+ standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW());

            }
            else if(functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getIconColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getIconColor()) && (functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getTimerColor()) && (functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBoxShadow()))))
            {
                session.setAttribute("panelheading_color", "");//Panel Heading Color
                session.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                session.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                session.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                session.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                session.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                session.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                session.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                session.setAttribute("textbox_color", "#ffffff");//Textbox Color
                session.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                session.setAttribute("timer_color","#000000");//Timer Color
                session.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
            }
            else if((functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getIconColor()) ) && (functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getIconColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTimerColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBoxShadow())))
            {
                session.setAttribute("panelheading_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                session.setAttribute("headpanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                session.setAttribute("bodypanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                session.setAttribute("panelbody_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                session.setAttribute("mainbackgroundcolor", standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
                session.setAttribute("bodybgcolor", standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor());
                session.setAttribute("bodyfgcolor", standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor());
                session.setAttribute("navigation_font_color", standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor());
                session.setAttribute("textbox_color", standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor());
                session.setAttribute("icon_color", standardKitValidatorVO.getMerchantDetailsVO().getIconColor());
                session.setAttribute("timer_color",standardKitValidatorVO.getMerchantDetailsVO().getTimerColor());
                session.setAttribute("box_shadow",standardKitValidatorVO.getMerchantDetailsVO().getBoxShadow());
            }
            else if((functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getIconColor())) && (functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getIconColor()) || functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getTimerColor()) || functions.isEmptyOrNull(standardKitValidatorVO.getPartnerDetailsVO().getBoxShadow())))
            {

                session.setAttribute("panelheading_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                session.setAttribute("headpanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                session.setAttribute("bodypanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                session.setAttribute("panelbody_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                session.setAttribute("mainbackgroundcolor", standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
                session.setAttribute("bodybgcolor", standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor());
                session.setAttribute("bodyfgcolor", standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor());
                session.setAttribute("navigation_font_color", standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor());
                session.setAttribute("textbox_color", standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor());
                session.setAttribute("icon_color", standardKitValidatorVO.getMerchantDetailsVO().getIconColor());
                session.setAttribute("timer_color",standardKitValidatorVO.getMerchantDetailsVO().getTimerColor());
                session.setAttribute("box_shadow",standardKitValidatorVO.getMerchantDetailsVO().getBoxShadow());
            }
            else if((functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getIconColor())) && (functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor()) && functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getIconColor()) || functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getTimerColor()) || functions.isEmptyOrNull(standardKitValidatorVO.getMerchantDetailsVO().getBoxShadow())))
        {
                session.setAttribute("panelheading_color", standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color());
                session.setAttribute("headpanelfont_color", standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color());
                session.setAttribute("bodypanelfont_color", standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color());
                session.setAttribute("panelbody_color", standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color());
                session.setAttribute("mainbackgroundcolor", standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color());
                session.setAttribute("bodybgcolor", standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor());
                session.setAttribute("bodyfgcolor", standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor());
                session.setAttribute("navigation_font_color", standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor());
                session.setAttribute("textbox_color", standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor());
                session.setAttribute("icon_color", standardKitValidatorVO.getPartnerDetailsVO().getIconColor());
                session.setAttribute("timer_color",standardKitValidatorVO.getPartnerDetailsVO().getTimerColor());
                session.setAttribute("box_shadow",standardKitValidatorVO.getPartnerDetailsVO().getBoxShadow());
            }

        }
        else if ("Y".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getTemplate()) && "N".equalsIgnoreCase(standardKitValidatorVO.getPartnerDetailsVO().getPartnertemplate()))
        {
            if(functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW()))
            {
                session.setAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BODYNFOOTER_COLOR());
                session.setAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADERBACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_COLOR());
                session.setAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_HEADER_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FULLBACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_LABEL_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_BUTTON_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BUTTON_COLOR());
                session.setAttribute("NEW_CHECKOUT_ICON_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_ICON_COLOR());
                session.setAttribute("NEW_CHECKOUT_TIMER_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_TIMER_COLOR());
                session.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR());
                session.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR",standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_BOX_SHADOW", "0 0 10px "+  standardKitValidatorVO.getMerchantDetailsVO().getNEW_CHECKOUT_BOX_SHADOW());
            }
            else if(functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getIconColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTimerColor()) || functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getBoxShadow()))
            {
                session.setAttribute("panelheading_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                session.setAttribute("headpanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                session.setAttribute("bodypanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                session.setAttribute("panelbody_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                session.setAttribute("mainbackgroundcolor", standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
                session.setAttribute("bodybgcolor", standardKitValidatorVO.getMerchantDetailsVO().getBodyBgColor());
                session.setAttribute("bodyfgcolor", standardKitValidatorVO.getMerchantDetailsVO().getBodyFgColor());
                session.setAttribute("navigation_font_color", standardKitValidatorVO.getMerchantDetailsVO().getNavigationFontColor());
                session.setAttribute("textbox_color", standardKitValidatorVO.getMerchantDetailsVO().getTextboxColor());
                session.setAttribute("icon_color", standardKitValidatorVO.getMerchantDetailsVO().getIconColor());
                session.setAttribute("timer_color",standardKitValidatorVO.getMerchantDetailsVO().getTimerColor());
                session.setAttribute("box_shadow",standardKitValidatorVO.getMerchantDetailsVO().getBoxShadow());
            }
            else
            {
                session.setAttribute("panelheading_color", "");//Panel Heading Color
                session.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                session.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                session.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                session.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                session.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                session.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                session.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                session.setAttribute("textbox_color", "#ffffff");//Textbox Color
                session.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                session.setAttribute("timer_color","#000000"); //Timer Color
                session.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)"); //Box Shadow
            }

        }
        else if ("Y".equalsIgnoreCase(standardKitValidatorVO.getPartnerDetailsVO().getPartnertemplate()) && "N".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getTemplate()))
        {
            if(((functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getIconColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getTimerColor()) || functions.isValueNull(standardKitValidatorVO.getPartnerDetailsVO().getBoxShadow()))))
            {
                session.setAttribute("panelheading_color", standardKitValidatorVO.getPartnerDetailsVO().getPanelHeading_color());
                session.setAttribute("headpanelfont_color", standardKitValidatorVO.getPartnerDetailsVO().getHeadPanelFont_color());
                session.setAttribute("bodypanelfont_color", standardKitValidatorVO.getPartnerDetailsVO().getBodyPanelFont_color());
                session.setAttribute("panelbody_color", standardKitValidatorVO.getPartnerDetailsVO().getPanelBody_color());
                session.setAttribute("mainbackgroundcolor", standardKitValidatorVO.getPartnerDetailsVO().getTemplateBackGround_color());
                session.setAttribute("bodybgcolor", standardKitValidatorVO.getPartnerDetailsVO().getBodyBgColor());
                session.setAttribute("bodyfgcolor", standardKitValidatorVO.getPartnerDetailsVO().getBodyFgColor());
                session.setAttribute("navigation_font_color", standardKitValidatorVO.getPartnerDetailsVO().getNavigationFontColor());
                session.setAttribute("textbox_color", standardKitValidatorVO.getPartnerDetailsVO().getTextboxColor());
                session.setAttribute("icon_color", standardKitValidatorVO.getPartnerDetailsVO().getIconColor());
                session.setAttribute("timer_color",standardKitValidatorVO.getPartnerDetailsVO().getTimerColor());
                session.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR",standardKitValidatorVO.getPartnerDetailsVO().getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR());
                session.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR",standardKitValidatorVO.getPartnerDetailsVO().getNEW_CHECKOUT_FOOTER_FONT_COLOR());
                session.setAttribute("box_shadow",standardKitValidatorVO.getPartnerDetailsVO().getBoxShadow());
            }
            else
            {
                session.setAttribute("panelheading_color", "");//Panel Heading Color
                session.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                session.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                session.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                session.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                session.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                session.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                session.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                session.setAttribute("textbox_color", "#ffffff");//Textbox Color
                session.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                session.setAttribute("timer_color","#000000");//Timer Color
                session.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                session.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR","#7eccad");
                session.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR","#000000");
            }

        }
        else
        {
            //default values set for new payment page
            if(functions.isValueNull(standardKitValidatorVO.getVersion()) && standardKitValidatorVO.getVersion().equals("2"))
            {
                session.setAttribute("panelheading_color", "#7eccad");//Panel Heading Color
                session.setAttribute("headpanelfont_color", "#ffffff");//Label Font Color
                session.setAttribute("bodypanelfont_color", "#ffffff");//Hading Label Font Color
                session.setAttribute("panelbody_color", "#f3f3f3");//Left Navigation Color
                session.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                session.setAttribute("bodybgcolor", "#808080");//Body BackGround Color
                session.setAttribute("bodyfgcolor", "#000000");//Body ForeGround Color
                session.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                session.setAttribute("textbox_color", "#7eccad");//Textbox Color
                session.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                session.setAttribute("timer_color","#000000");//Timer Color
                session.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                session.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR","#7eccad");
                session.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR","#000000");

            }
            else
            {
                //default values set for old payment page
                session.setAttribute("panelheading_color", "");//Panel Heading Color
                session.setAttribute("headpanelfont_color", "#000000");//Label Font Color
                session.setAttribute("bodypanelfont_color", "#fff");//Hading Label Font Color
                session.setAttribute("panelbody_color", "#7eccad");//Left Navigation Color
                session.setAttribute("mainbackgroundcolor", "#ffffff");//Template BackGround Color
                session.setAttribute("bodybgcolor", "#f3f3f3");//Body BackGround Color
                session.setAttribute("bodyfgcolor", "");//Body ForeGround Color
                session.setAttribute("navigation_font_color", "#000000");//Navigation Font Color
                session.setAttribute("textbox_color", "#ffffff");//Textbox Color
                session.setAttribute("icon_color", "#7eccad");//Icon Vector Color
                session.setAttribute("timer_color","#000000");//Timer Color
                session.setAttribute("box_shadow","0px 0px 20px rgba(0, 0, 0, 0.5)");//Box Shadow
                session.setAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR","#7eccad");
                session.setAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR","#000000");
            }
        }
    }

    public CommonValidatorVO performStandardProcessStep1Validations(CommonValidatorVO commonValidatorVO, HttpSession session)throws PZConstraintViolationException,PZDBViolationException
    {
        String error = "";
        Functions functions=new Functions();
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        TerminalManager terminalManager = new TerminalManager();

        HashMap paymentCardMap = new HashMap();
        HashMap terminalMap = new HashMap();

        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();
        String toid=merchantDetailsVO.getMemberId();
        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            error = ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID.toString(),ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());
            if(null==merchantDetailsVO)
            {
                //errorCodeVO=new ErrorCodeVO();
                /*error = ErrorMessages.MISCONFIGURED_TOID;
                commonValidatorVO.setErrorMsg(error);*/
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorReason(errorCodeVO.getErrorDescription());
                commonValidatorVO.setErrorMsg(errorCodeVO.getErrorDescription());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(),ErrorType.VALIDATION.toString());
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            if(merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                commonValidatorVO.setErrorMsg(error);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(),ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            partnerDetailsVO.setPartnerId(merchantDetailsVO.getPartnerId());
            partnerDetailsVO.setPartnertemplate(merchantDetailsVO.getPartnertemplate());
            setAllTemplateInformationRelatedToMerchant(merchantDetailsVO,commonValidatorVO.getVersion());
            setAllTemplateInformationRelatedToPartner(partnerDetailsVO,commonValidatorVO.getVersion());
            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            getPaymentPageTemplateDetails(commonValidatorVO, session);
            //transactionLogger.debug("MerchantLogoName from commonValidatorVO.merchantDetailsVO 1---" + commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            //PaymentType and CardType Validation
            error = validateStandardProcessStep0(commonValidatorVO, "STDKIT");
            if(!functions.isEmptyOrNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }

            if(!("t").equalsIgnoreCase(commonValidatorVO.getIsProcessed()))
            {
                boolean currencyMap=false;
                boolean paymentMap=false;
                boolean paymentcardMap=false;
                boolean terminalIdMap=false;
                //Paymode, Cardtype, terminal not provided
                if((!functions.isValueNull(commonValidatorVO.getPaymentType()) && !functions.isValueNull(commonValidatorVO.getCardType()) && !functions.isValueNull(commonValidatorVO.getTerminalId())))
                {
                    transactionLogger.error("--inside terminalId,paymentType and cardType null condition--");
                    if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        //Fetch all the payment type and cart type basis of memberid only
                        String currency = commonValidatorVO.getTransDetailsVO().getCurrency()+",ALL";
                        paymentCardMap = getListofPaymentandCardtype1(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), "ALL",commonValidatorVO.getAccountId());
                        terminalMap = terminalManager.getMultipleCurrencyPaymdeCardTerminalVO(toid, commonValidatorVO.getTransDetailsVO().getCurrency(),"ALL",commonValidatorVO.getAccountId());
                        log.debug("All PaymentType and CardType in CommonInputValidator on base of toid----" + paymentCardMap);
                    }
                    else
                    {
                        //Fetch all the payment type and cart type basis of memberid and currency
                        paymentCardMap = getListofPaymentandCardtype(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                        terminalMap = terminalManager.getPaymdeCardTerminalVO(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                        log.debug("All PaymentType and CardType in CommonInputValidator on base of toid and currency----" + paymentCardMap);
                    }
                    currencyMap=true;
                }
                else if(functions.isValueNull(commonValidatorVO.getTerminalId()))
                {
                    //Terminal Id provided
                    transactionLogger.error("--inside terminalId not null condition--");
                    paymentCardMap = getTerminaliIDForSKNewFlow(toid,commonValidatorVO.getTerminalId());
                    if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        terminalMap = terminalManager.getPaymdeCardTerminalVOfromTerminalID(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), "ALL", commonValidatorVO.getTerminalId(), commonValidatorVO.getAccountId());
                    }
                    else
                    {
                        terminalMap = terminalManager.getPaymdeCardTerminalVOfromTerminalID(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getTerminalId(), commonValidatorVO.getAccountId());
                    }
                    log.debug("paymentCardMap with TerminalID----"+paymentCardMap);
                    log.debug("paymentCardMap with terminal map----"+terminalMap);
                    terminalIdMap=true;
                }
                else if(functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isValueNull(commonValidatorVO.getCardType()))
                {
                    //Both, Payment type and card type provided
                    transactionLogger.error("--inside paymenttype and cardtype not null condition--");
                    List<String> cardList = new ArrayList<String>();
                    cardList.add(commonValidatorVO.getCardType());
                    paymentCardMap.put(commonValidatorVO.getPaymentType(), cardList);
                    if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        terminalMap = terminalManager.getPaymdeCardTerminalVOfromPaymodeCardType(toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(),"ALL",commonValidatorVO.getAccountId());
                    }
                    else
                    {
                        terminalMap = terminalManager.getPaymdeCardTerminalVOfromPaymodeCardType(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(),commonValidatorVO.getAccountId());
                    }
                    log.debug("paymentCardMap with PaymentType and CardType----"+paymentCardMap);
                    log.debug("terminalMap with PaymentType and CardType----"+terminalMap);
                    paymentcardMap=true;
                }
                else if (functions.isValueNull(commonValidatorVO.getPaymentType()) && !functions.isValueNull(commonValidatorVO.getCardType()))
                {
                    //only payment type provided
                    transactionLogger.error("--inside paymenttype not null condition--");
                    if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        paymentCardMap = getListofPaymentandCardtype(toid,"ALL",commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getPaymentType(),commonValidatorVO.getAccountId());
                        terminalMap = terminalManager.getPaymdeCardTerminalVO(toid,commonValidatorVO.getPaymentType(), commonValidatorVO.getTransDetailsVO().getCurrency(),"ALL",commonValidatorVO.getAccountId());
                    }
                    else
                    {
                        paymentCardMap = getListofPaymentandCardtype(toid,commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getPaymentType(),commonValidatorVO.getAccountId());
                        terminalMap = terminalManager.getPaymdeCardTerminalVO(toid, commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getPaymentType(),commonValidatorVO.getAccountId());
                    }
                    log.debug("paymentCardMap with Payment type---"+paymentCardMap);
                    log.debug("terminalMap with Payment type---"+terminalMap);
                    paymentMap=true;
                }
                //Currency Validation
                if(paymentCardMap.isEmpty() || terminalMap.isEmpty())
                {
                    //errorCodeVO = new ErrorCodeVO();
                    if(currencyMap)
                    {
                        if(functions.isValueNull(commonValidatorVO.getAccountId()))
                        {
                            error = ErrorMessages.INVALID_ACCOUNT_CURRENCY;
                            commonValidatorVO.setErrorMsg(error);
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_ACCOUNT_CURRENCY);
                            errorCodeVO.setErrorName(ErrorName.SYS_INVALID_ACCOUNT_CURRENCY);
                            errorCodeVO.setErrorReason(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_ACCOUNT_CURRENCY.toString(), ErrorType.SYSCHECK.toString());
                        }else
                        {
                            error = ErrorMessages.INVALID_CURRENCY;
                            commonValidatorVO.setErrorMsg(error);
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CURRENCY);
                            errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                            errorCodeVO.setErrorReason(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_CURRENCY.toString(), ErrorType.VALIDATION.toString());
                        }
                    }
                    else if(paymentMap)
                    {
                        if(functions.isValueNull(commonValidatorVO.getAccountId()))
                        {
                            error = ErrorMessages.INVALID_ACCOUNT_PAYMODE;
                            commonValidatorVO.setErrorMsg(error);
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_ACCOUNT_PAYMODE);
                            errorCodeVO.setErrorName(ErrorName.SYS_INVALID_ACCOUNT_PAYMODE);
                            errorCodeVO.setErrorReason(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_ACCOUNT_PAYMODE.toString(), ErrorType.SYSCHECK.toString());
                        }else
                        {
                            error = ErrorMessages.INVALID_PAYMENT_MODE;
                            commonValidatorVO.setErrorMsg(error);
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PAYMENT_MODE);
                            errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_MODE);
                            errorCodeVO.setErrorReason(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMENT_MODE.toString(), ErrorType.VALIDATION.toString());
                        }
                    }
                    else if(terminalIdMap)
                    {
                        if(functions.isValueNull(commonValidatorVO.getAccountId()))
                        {
                            error = ErrorMessages.INVALID_ACCOUNT_TERMINALID;
                            commonValidatorVO.setErrorMsg(error);
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL_ACCOUNT);
                            errorCodeVO.setErrorName(ErrorName.SYS_INVALID_TERMINAL_ACCOUNT);
                            errorCodeVO.setErrorReason(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_ACCOUNT.toString(), ErrorType.SYSCHECK.toString());
                        }else
                        {
                            error = ErrorMessages.INVALID_TERMINALID;
                            commonValidatorVO.setErrorMsg(error);
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                            errorCodeVO.setErrorName(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                            errorCodeVO.setErrorReason(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                        }
                    }
                    else if(paymentcardMap)
                    {
                        error = ErrorMessages.INVALID_PAYMODE_CARDTYPE;
                        commonValidatorVO.setErrorMsg(error);
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PAYMODE_CARDTYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMODE_CARDTYPE);
                        errorCodeVO.setErrorReason(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMODE_CARDTYPE.toString(),ErrorType.VALIDATION.toString());
                    }

                    if(commonValidatorVO.getErrorCodeListVO()!=null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return commonValidatorVO;
                }

                if(terminalMap.isEmpty())
                {
                    log.debug("terminalMap:::"+terminalIdMap);
                    errorCodeVO = new ErrorCodeVO();
                    error = ErrorMessages.INVALID_PAYMODE_CARDTYPE;
                    commonValidatorVO.setErrorMsg(error);

                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY);
                    /*errorCodeVO.setErrorName(ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY);
                    errorCodeVO.setErrorReason(error);*/
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY.toString(),ErrorType.SYSCHECK.toString());
                    if(commonValidatorVO.getErrorCodeListVO()!=null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return commonValidatorVO;
                }
                if(paymentCardMap.containsKey(String.valueOf(PaymentModeEnum.CardPresent.getValue())))
                {
                    if(paymentCardMap.size()==1)
                    {
                        errorCodeListVO=new ErrorCodeListVO();
                        transactionLogger.error("--- inside ECPCPPaymentGateway Card transaction not supported ---");
                        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        error=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED).getErrorDescription();
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }else
                    {
                        paymentCardMap.remove(String.valueOf(PaymentModeEnum.CardPresent.getValue()));
                        log.error("paymentCardMap-->" + paymentCardMap);
                    }
                }
                commonValidatorVO.setMapOfPaymentCardType(paymentCardMap);
                commonValidatorVO.setTerminalMap(terminalMap);
                transactionLogger.error("terminalMap---" + terminalMap);
            }
            commonValidatorVO.setTerminalMap(terminalMap);

        }

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the  Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(),ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any incomplete formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(),ErrorType.SYSCHECK.toString());
            //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        //setting Blank Address if Address Validation NA
        if(merchantDetailsVO.getAddressValidation().equalsIgnoreCase("NA"))
        {
            commonValidatorVO.getAddressDetailsVO().setCountry("");
            commonValidatorVO.getAddressDetailsVO().setCity("");
            commonValidatorVO.getAddressDetailsVO().setStreet("");
            commonValidatorVO.getAddressDetailsVO().setZipCode("");
            commonValidatorVO.getAddressDetailsVO().setState("");
            commonValidatorVO.getAddressDetailsVO().setPhone("");
            commonValidatorVO.getAddressDetailsVO().setTelnocc("");
        }

        error = validateStandardProcessStep1(commonValidatorVO, "STDKIT");

        transactionLogger.debug("MerchantLogoName from commonValidatorVO.merchantDetailsVO 2---" + commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }
        //specific parameter
        error = validateStandardKitSpecificParametersStep1(commonValidatorVO);
        transactionLogger.debug("MerchantLogoName from commonValidatorVO.merchantDetailsVO 3---" + commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }
        if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
        {
            error = validateMarketPlaceStandardProcess(commonValidatorVO, "STDKIT");

            if (!functions.isEmptyOrNull(error))
            {
                commonValidatorVO.setErrorMsg(error.trim());
                String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
        }else {
            commonValidatorVO.setMarketPlaceVOList(null);
        }

        return commonValidatorVO;
    }


    public CommonValidatorVO performStandardProcessStep1ValidationsForCardRegistration(CommonValidatorVO commonValidatorVO, HttpSession session)throws PZConstraintViolationException,PZDBViolationException
    {
        String error = "";
        Functions functions=new Functions();
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        TerminalManager terminalManager = new TerminalManager();

        HashMap paymentCardMap = new HashMap();
        HashMap terminalMap = new HashMap();

        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();
        String toid=merchantDetailsVO.getMemberId();
        //String key=merchantDetailsVO.getKey();
        String totype=commonValidatorVO.getTransDetailsVO().getTotype();
        String checksumRequest=commonValidatorVO.getTransDetailsVO().getChecksum();

        try
        {

            if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length() > 10 || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 10, false))
            {
                error = ErrorMessages.INVALID_TOID;
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
            else
            {
                merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());
                if (null == merchantDetailsVO)
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID);
                    errorCodeVO.setErrorReason(errorCodeVO.getErrorDescription());
                    commonValidatorVO.setErrorMsg(errorCodeVO.getErrorDescription());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(), ErrorType.VALIDATION.toString());
                    if (commonValidatorVO.getErrorCodeListVO() != null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    for (ErrorCodeVO errorCodeVO1:commonValidatorVO.getErrorCodeListVO().getListOfError())
                    {
                        transactionLogger.debug("error code in invalid toid---"+errorCodeVO1.getErrorCode()+"---"+errorCodeVO1.getApiDescription()+"---"+errorCodeVO1.getErrorDescription());
                    }
                    return commonValidatorVO;
                }

                if (merchantDetailsVO.getMemberId() == null)
                {
                    errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                    commonValidatorVO.setErrorMsg(error);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                    if (commonValidatorVO.getErrorCodeListVO() != null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(), ErrorType.VALIDATION.toString());
                    return commonValidatorVO;
                }
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                partnerDetailsVO.setPartnerId(merchantDetailsVO.getPartnerId());
                partnerDetailsVO.setPartnertemplate(merchantDetailsVO.getPartnertemplate());
                setAllTemplateInformationRelatedToMerchant(merchantDetailsVO, commonValidatorVO.getVersion());
                setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());
                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                getPaymentPageTemplateDetails(commonValidatorVO, session);
                error = validateCardRegistrationStep0(commonValidatorVO, "STDKIT");

                if (!functions.isEmptyOrNull(error))
                {
                    commonValidatorVO.setErrorMsg(error);
                    String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    return commonValidatorVO;
                }

                String checksum = Functions.generateChecksumV1(toid, totype, merchantDetailsVO.getKey(), "");
                transactionLogger.debug("checksum::::"+checksum);
                transactionLogger.debug("checksum request::::"+checksumRequest);
                if(!checksum.equals(checksumRequest))
                {
                    error =  "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CHECKSUM_ERROR.toString(),ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
                }

                if (!("t").equalsIgnoreCase(commonValidatorVO.getIsProcessed()))
                {
                    //Paymode, Cardtype, terminal not provided
                    if ((!functions.isValueNull(commonValidatorVO.getPaymentType()) && !functions.isValueNull(commonValidatorVO.getCardType()) && !functions.isValueNull(commonValidatorVO.getTerminalId())))
                    {
                        if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                        {
                            //Fetch all the payment type and cart type basis of memberid only
                            String currency = commonValidatorVO.getTransDetailsVO().getCurrency() + ",ALL";
                            paymentCardMap = getListofPaymentandCardtype1(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), "ALL",commonValidatorVO.getAccountId());
                            log.debug("All PaymentType and CardType in CommonInputValidator on base of toid----" + paymentCardMap);
                        }
                        else
                        {
                            //Fetch all the payment type and cart type basis of memberid and currency
                            paymentCardMap = getListofPaymentandCardtype(toid, commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getAccountId());
                            log.debug("All PaymentType and CardType in CommonInputValidator on base of toid and currency----" + paymentCardMap);
                        }
                    }
                    else if (functions.isValueNull(commonValidatorVO.getPaymentType()) && !functions.isValueNull(commonValidatorVO.getCardType()))
                    {
                        //only payment type provided
                        if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                        {
                            paymentCardMap = getListofPaymentandCardtype(toid, "ALL", commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getPaymentType(),commonValidatorVO.getAccountId());
                        }
                        else
                        {
                            paymentCardMap = getListofPaymentandCardtype(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getPaymentType(),commonValidatorVO.getAccountId());
                        }
                    }
                    else
                    {
                        //Both, Payment type and card type provided
                        if (functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isValueNull(commonValidatorVO.getCardType()))
                        {
                            List<String> cardList = new ArrayList<String>();
                            cardList.add(commonValidatorVO.getCardType());
                            paymentCardMap.put(commonValidatorVO.getPaymentType(), cardList);
                        }
                        //Terminal Id provided
                        if (functions.isValueNull(commonValidatorVO.getTerminalId()))
                        {
                            paymentCardMap = getTerminaliIDForSKNewFlow(toid, commonValidatorVO.getTerminalId());
                            if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                            {
                                terminalMap = terminalManager.getPaymdeCardTerminalVOfromTerminalID(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), "ALL", commonValidatorVO.getTerminalId(),commonValidatorVO.getAccountId());
                            }
                            else
                            {
                                terminalMap = terminalManager.getPaymdeCardTerminalVOfromTerminalID(toid, commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getTerminalId(),commonValidatorVO.getAccountId());
                            }
                            log.debug("paymentCardMap with TerminalID----" + paymentCardMap);
                            log.debug("paymentCardMap with terminal map----" + terminalMap);
                        }
                    }
                }
            }

            //IP Whitelist check
            if ("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
            {
                if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(), ErrorType.VALIDATION.toString());
                    //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            //Activation check
            if (!merchantDetailsVO.getActivation().equals("Y"))
            {
                error = "The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any incomplete formality from the Merchant Side. Please contact support so that they can activate your account.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(), ErrorType.VALIDATION.toString());
                //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            //setting Blank Address if Address Validation NA
            if (merchantDetailsVO.getAddressValidation().equalsIgnoreCase("NA"))
            {
                commonValidatorVO.getAddressDetailsVO().setCountry("");
                commonValidatorVO.getAddressDetailsVO().setCity("");
                commonValidatorVO.getAddressDetailsVO().setStreet("");
                commonValidatorVO.getAddressDetailsVO().setZipCode("");
                commonValidatorVO.getAddressDetailsVO().setState("");
                commonValidatorVO.getAddressDetailsVO().setPhone("");
                commonValidatorVO.getAddressDetailsVO().setTelnocc("");
            }

            //error = validateCardRegistrationStep0(commonValidatorVO, "STDKIT");
            //transactionLogger.debug("MerchantLogoName from commonValidatorVO.merchantDetailsVO 2---" + commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

            if (!functions.isEmptyOrNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("NoSuchAlgorithmException----->",e);
        }
        return commonValidatorVO;
    }


    public CommonValidatorVO performStandardKitStep2Validations(CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        String error= "";
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        String accountid= merchantDetailsVO.getAccountId();
        Functions functions=new Functions();
        String toid=merchantDetailsVO.getMemberId();
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
        {
            commonValidatorVO.setErrorMsg(ErrorMessages.INVALID_TOID);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid,commonValidatorVO.getTransDetailsVO().getTotype());

            merchantDetailsVO.setAccountId(accountid);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        error = validateCommonParametersStep2(commonValidatorVO, "STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        //Address Details Validation (Terminal Level Address flag)
        error = validateFlagBasedAddressField(commonValidatorVO,"STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        //Card Details Validation (Terminal Level Address flag)
        error = validateFlagBasedCardDetails(commonValidatorVO,"STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        //Personal Details Validation (Member Level Address flag)
        error = validateFlagBasedPersonalDetails(commonValidatorVO,"STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        //specific parameter
        error = validateStandardKitSpecificParametersStep2(commonValidatorVO,"STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountid));
        String addressValidation = GatewayAccountService.getGatewayAccount(accountid).getAddressValidation();
        error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO,"STDKIT",addressValidation);
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;

        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performVirtualTerminalValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        String error="";
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        Functions functions=new Functions();

        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();

        String toid=merchantDetailsVO.getMemberId();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();
        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
        {
            commonValidatorVO.setErrorMsg(ErrorMessages.INVALID_TOID);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid,commonValidatorVO.getTransDetailsVO().getTotype());

            if(merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                failedTransactionLogEntry.nonWhitelistedTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error,TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        error =  validateCommonParametersStep1(commonValidatorVO,"VT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error =  validateCommonParametersStep2(commonValidatorVO,"VT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        TransactionHelper transactionHelper=new TransactionHelper();
        commonValidatorVO = transactionHelper.performCommonSystemChecksStep1(commonValidatorVO);

        error = validateFlagBasedAddressField(commonValidatorVO,"VT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        //Card Details Validation (Terminal Level Address flag)
        error = validateFlagBasedCardDetails(commonValidatorVO,"VT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            return commonValidatorVO;
        }

        //Not required as all the checks are handle at gateway side
       /* error = validateDirectKitSpecificParameters(commonValidatorVO,"VT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }*/

        transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);
        log.error("accountID in commonInputvalidator --> "+accountId);
        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
        error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO,"VT",addressValidation);
        log.error("accountID error---> in commonInputvalidator --> "+error);
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
        }

        return commonValidatorVO;
    }

    public CommonValidatorVO performManualRecurringValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        String error="";

        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        Functions functions=new Functions();
        TransactionHelper transactionHelper = new TransactionHelper();
        GenericTransDetailsVO genericTransDetailsVO= commonValidatorVO.getTransDetailsVO();
        ManualRecurringManager manualRecurringManager = new ManualRecurringManager();

        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();

        String toid=merchantDetailsVO.getMemberId();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();

        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error,TransReqRejectCheck.VALIDATION_TOID.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());

            if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode()+"-"+ errorCodeVO.getErrorDescription();
               //errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
               //error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOTYPE;
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.VALIDATION_TOID_INVALID.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error,TransReqRejectCheck.VALIDATION_TOID_INVALID.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        String currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();
        //specifidc JPY Currency Validation
        if(currency.equals("JPY"))
        {
            if(!paymentChecker.isAmountValidForJPY(currency,genericTransDetailsVO.getAmount()))
            {
                error = "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
                errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
                failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.SYS_JPY_CURRENCY_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
            }
        }

        error =  validateManualRebillParameters(commonValidatorVO,"DKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        //Order uniqueness
        String uniqueorder = "";
        uniqueorder = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), "", commonValidatorVO.getTransDetailsVO().getOrderId());
      /*  if (!uniqueorder.equals(""))
        {
            error = commonValidatorVO.getTransDetailsVO().getOrderId()+"-Duplicate Order Id " + uniqueorder;
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }*/
        if (!uniqueorder.equals(""))
        {
           //commonValidatorVO.getTransDetailsVO().getOrderId() + "-"+errorCodeVo.getErrorCode()+"-Duplicate Order Id OR " + uniqueorder
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            errorCodeVO=errorCodeListVO.getListOfError().get(0);
            error = commonValidatorVO.getTransDetailsVO().getOrderId() +"-"+errorCodeVO.getErrorCode()+"-Duplicate Order Id OR " + uniqueorder;
            transactionLogger.error("error is -------"+error);
            //Transaction request rejected log entry with reason:Partner-DUPLICATE_ORDERID

           // failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.SYS_UNIQUEORDER.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_UNIQUEORDER.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);

           // PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        //checksum compare
        String checksumcal = "";
        try
        {
            checksumcal = Functions.generateChecksumManualRebill(toid, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), merchantDetailsVO.getKey());
        }
        catch(NoSuchAlgorithmException e )
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
            error =  "Error while generating checksum. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_CHECKSUM_ERROR);
            PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, e.getMessage(), e.getCause());
        }

        if (!genericTransDetailsVO.getChecksum().equals(checksumcal))
        {
            error =  "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            /*Transaction request rejected log entry with reason:Partner-MERCHANT_CHECKSUM_MISSMATCH*/
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MERCHANT_CHECKSUM_MISSMATCH.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performManualRecurringValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
        }
        manualRecurringManager.checkAmountlimitForRebill(commonValidatorVO.getTransDetailsVO().getAmount(),toid,accountId);
        return commonValidatorVO;
    }

    public CommonValidatorVO performDirectKitValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        String error="";
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        Functions functions=new Functions();


        String toid=merchantDetailsVO.getMemberId();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();

        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();

        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid,commonValidatorVO.getTransDetailsVO().getTotype());
            if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
                error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOTYPE;

                /*Transaction request rejected log entry with reason:Partner-PARTNER_MERCHANT_INVALID_CONFIGURATION*/
                failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error,TransReqRejectCheck.VALIDATION_TOID_INVALID.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        if ("Y".equals(merchantDetailsVO.getIsCardEncryptionEnable()))
        {
            try
            {
                String mKey = merchantDetailsVO.getKey();
                AESEncryptionManager encryptionManager=new AESEncryptionManager();
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getcVV()))
                    commonValidatorVO.getCardDetailsVO().setcVV(encryptionManager.decrypt(commonValidatorVO.getCardDetailsVO().getcVV(), mKey));

                commonValidatorVO.getCardDetailsVO().setCardNum(encryptionManager.decrypt(commonValidatorVO.getCardDetailsVO().getCardNum(), mKey));
                commonValidatorVO.getCardDetailsVO().setExpYear(encryptionManager.decrypt(commonValidatorVO.getCardDetailsVO().getExpYear(), mKey));
                commonValidatorVO.getCardDetailsVO().setExpMonth(encryptionManager.decrypt(commonValidatorVO.getCardDetailsVO().getExpMonth(), mKey));
            }
            catch (Exception e)
            {
                transactionLogger.error("Encryption Exception:::",e);

                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CARD_ENCRYPTION);
                error = errorCodeVO.getErrorCode() + "_" + ErrorMessages.INVALID_CARD_ENCRYPTION;
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            transactionLogger.debug("ip address--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {

                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                /*Transaction request rejected log entry with reason:Partner-MERCHANT_IP_WHITELIST*/
                failedTransactionLogEntry.nonWhitelistedTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error,TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            /*Transaction request rejected log entry with reason:Partner-CARD_SERIAL*/
            failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }
        error =  validateCommonParametersStep1(commonValidatorVO,"DKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error =  validatePayModeCardType(commonValidatorVO,"DKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error =  validateDirectParametersStep2(commonValidatorVO, "DKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        TransactionHelper transactionHelper=new TransactionHelper();
        commonValidatorVO = transactionHelper.performCommonSystemChecksStep1(commonValidatorVO);
        error = validateFlagBasedAddressField(commonValidatorVO,"DKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error = validateFlagBasedCardDetails(commonValidatorVO, "DKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error = validateDirectKitSpecificParameters(commonValidatorVO,"DKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }


        transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);

        log.error("accountID in commonInputvalidator --> "+accountId);


        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
        error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO,"DKIT",addressValidation);
        log.error("accountID error---> in commonInputvalidator --> "+error);
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performTokenTransactionValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        String error = "";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        Functions functions = new Functions();

        String toid = merchantDetailsVO.getMemberId();
        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();

        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;

        if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length() > 10 || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 20, false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode() + " " + ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());
            //If memberid and partnerid combination not found
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode() + " " + ErrorMessages.MISCONFIGURED_TOID;
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        //IP Whitelist check
        if ("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            transactionLogger.debug("ip address--------" +merchantDetailsVO.getMemberId() + "---" + commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        error = validateCommonParametersStep1(commonValidatorVO, "DKIT");
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        /*error =  validateDirectParametersStep2(commonValidatorVO, "DKIT");
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }*/

        TransactionHelper transactionHelper = new TransactionHelper();
        commonValidatorVO = transactionHelper.performCommonSystemChecksStep1(commonValidatorVO);
        error = validateFlagBasedAddressField(commonValidatorVO, "DKIT");
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String addressValidation = gatewayAccount.getAddressValidation();
        boolean isCvvRequired = gatewayAccount.isCvvRequired();

        error = validateCardDetailsForTokenTransaction(commonValidatorVO, "DKIT", isCvvRequired);
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error = validateDirectKitSpecificParameters(commonValidatorVO, "DKIT");
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);
        log.error("accountID in commonInputvalidator --> " + accountId);

        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "DKIT", addressValidation);

        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
        }

        error = paymentProcess.validateCVVForTokenTransaction(commonValidatorVO);
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
        }
        return commonValidatorVO;
    }
    public CommonValidatorVO perform3DKitValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        transactionLogger.error("inside 3d validator::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        String error="";
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        // TransactionUtility transactionUtility=new TransactionUtility();
        Functions functions=new Functions();


        String toid=merchantDetailsVO.getMemberId();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();

        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;

        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        else
        {

            merchantDetailsVO = getMerchantConfigDetails(toid,commonValidatorVO.getTransDetailsVO().getTotype());

            //If memberid and partnerid combination not found
            if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.MISCONFIGURED_TOID;
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {

            transactionLogger.debug("ip address--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        log.error("IsCardEncryptionEnable On Merchant:"+merchantDetailsVO.getIsCardEncryptionEnable());
        if("Y".equals(merchantDetailsVO.getIsCardEncryptionEnable()))
        {
            TerminalManager terminalManager=new TerminalManager();
            TerminalVO terminalVO=terminalManager.getTerminalByTerminalId(commonValidatorVO.getTerminalId());
            transactionLogger.error("IsCardEncryptionEnable On Terminal:"+terminalVO.getIsCardEncryptionEnable());
            if ("Y".equals(terminalVO.getIsCardEncryptionEnable())){
                try{
                    AESEncryptionManager encryptionManager=new AESEncryptionManager();
                    String cardNumber = encryptionManager.decrypt(genericCardDetailsVO.getCardNum(), merchantDetailsVO.getKey());
                    String expiryMonth = encryptionManager.decrypt(genericCardDetailsVO.getExpMonth(), merchantDetailsVO.getKey());
                    String expiryYear = encryptionManager.decrypt(genericCardDetailsVO.getExpYear(), merchantDetailsVO.getKey());

                    String cvv = "";
                    if (functions.isValueNull(genericCardDetailsVO.getcVV()))
                        cvv = encryptionManager.decrypt(genericCardDetailsVO.getcVV(), merchantDetailsVO.getKey());

                    if(!ESAPI.validator().isValidInput("cardnumber", cardNumber, "CC", 20, false) || !ESAPI.validator().isValidInput("cvv", cvv, "Numbers", 4, false) || !ESAPI.validator().isValidInput("expirymonth",expiryMonth,"Months", 2, false) || !ESAPI.validator().isValidInput("expiryyear", expiryYear, "Years", 4, false))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CARD_ENCRYPTION);
                        error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_CARD_ENCRYPTION;
                        commonValidatorVO.setErrorMsg(error);
                        return commonValidatorVO;
                    }
                    genericCardDetailsVO.setCardNum(cardNumber);
                    genericCardDetailsVO.setExpMonth(expiryMonth);
                    genericCardDetailsVO.setExpYear(expiryYear);
                    genericCardDetailsVO.setcVV(cvv);
                }
                catch (Exception e){
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CARD_ENCRYPTION);
                    error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_CARD_ENCRYPTION;
                    commonValidatorVO.setErrorMsg(error);
                    return commonValidatorVO;
                }
            }
        }
        error =  validateCommonParametersStep1(commonValidatorVO,"STDKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }


        error =  validateDirectParametersStep2(commonValidatorVO, "STDKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        TransactionHelper transactionHelper=new TransactionHelper();

        commonValidatorVO = transactionHelper.performCommonSystemChecksStep1(commonValidatorVO);

        error = validateFlagBasedAddressField(commonValidatorVO,"STDKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error = validateFlagBasedCardDetails(commonValidatorVO, "STDKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        error = validateDirectKitSpecificParameters(commonValidatorVO,"STDKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }


        transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);

        log.error("accountID in commonInputvalidator --> "+accountId);


        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
        error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO,"DKIT",addressValidation);
        log.error("accountID error---> in commonInputvalidator --> "+error);
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
        }
        return commonValidatorVO;
    }

    public String validateFlagBasedAddressField(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAddressValidation()))
        {

            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, false);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
        else if("N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAddressValidation()))
        {
            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
        return error;
    }

    public String validateFlagBasedPersonalDetails(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getPersonalInfoValidation()))
        {

            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getPersonalDetailValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, false);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
        else if("N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAddressValidation()))
        {
            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getPersonalDetailValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
        return error;
    }


    public String validateRestFlagBasedAddressField(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAddressValidation()))
        {

            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, false);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
        else if("N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAddressValidation()))
        {
            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }

        List<InputFields> inputStateAddressValidation = new ArrayList<InputFields>();
        inputStateAddressValidation.addAll(inputValiDatorUtils.getStateValidation());
        ValidationErrorList StateValidationError = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputStateAddressValidation, StateValidationError, true);
        error = error + inputValiDatorUtils.getError(StateValidationError,inputStateAddressValidation,actionName);

        return error;
    }



    public String validatePartnerMerchantFlagBasedAddressField(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
        ValidationErrorList addressValidationError = new ValidationErrorList();

        if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getAddressvalidation()) && "Y".equals(commonValidatorVO.getMerchantDetailsVO().getAddressValidation()))
            inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, false);
        else
            inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);

        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        return error;
    }

    public String validateFlagBasedRestCardDetails(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        String fromtype = commonValidatorVO.getTransDetailsVO().getFromtype();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardDetailRequired()))
        {
            List<InputFields> inputFlagBasedCardValidation = new ArrayList<InputFields>();
            inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getRestCardDetailValidation());
            ValidationErrorList cardValidationError = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedCardValidation, cardValidationError, false);
            error = error + inputValiDatorUtils.getError(cardValidationError,inputFlagBasedCardValidation,actionName);
        }
        else if("N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardDetailRequired()))
        {
            if (P4DirectDebitPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                List<InputFields> inputFlagBasedCardValidation = new ArrayList<InputFields>();
                inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getRestAccountDetailsValidation());
                ValidationErrorList cardValidationError = new ValidationErrorList();
                inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedCardValidation, cardValidationError, false);
                error = error + inputValiDatorUtils.getError(cardValidationError, inputFlagBasedCardValidation, actionName);
            }
        }
        return error;
    }

    public String validateFlagBasedCardDetails(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardDetailRequired()))
        {
            List<InputFields> inputFlagBasedCardValidation = new ArrayList();
            if (!gatewayAccount.isCvvRequired())
            {
                inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getCardDetailsExcludeCvvValidation());
            }
            else
            {
                inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getCardDetailValidation());
            }
            ValidationErrorList cardValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedCardValidation, cardValidationError, false);
            error = error + inputValiDatorUtils.getError(cardValidationError,inputFlagBasedCardValidation,actionName);
        }
        else if("N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardDetailRequired()))
        {
            List<InputFields> inputFlagBasedCardValidation = new ArrayList();
            if (!gatewayAccount.isCvvRequired())
            {
                inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getCardDetailsExcludeCvvValidation());
            }
            else
            {
                inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getCardDetailValidation());
            }
            ValidationErrorList cardValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedCardValidation, cardValidationError, true);
            error = error + inputValiDatorUtils.getError(cardValidationError,inputFlagBasedCardValidation,actionName);
        }
        return error;
    }

    public String validateCardDetailsForTokenTransaction(CommonValidatorVO commonValidatorVO, String actionName, boolean isCVVRequired)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFlagBasedCardValidation = new ArrayList<InputFields>();
        inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getCardFieldsForTokenTransaction(isCVVRequired));
        ValidationErrorList cardValidationError = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputFlagBasedCardValidation, cardValidationError, false);
        error = error + inputValiDatorUtils.getError(cardValidationError, inputFlagBasedCardValidation, actionName);
        return error;

    }
    public void performDirectKitRefundValidation(DirectRefundValidatorVO directRefundValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {


        //here input validation is taking into consideration

        performRefundStep1Validation(directRefundValidatorVO);
        if(functions.isValueNull(directRefundValidatorVO.getErrorMessage()))
            return;

        performRefundStep2Validation(directRefundValidatorVO);

    }

    public void performDirectKitInquiryValidation(DirectInquiryValidatorVO directInquiryValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        performInquiryStep1Validation(directInquiryValidatorVO);
        if(functions.isValueNull(directInquiryValidatorVO.getErrorMessage()))
            return;


        performInquiryStep2Validation(directInquiryValidatorVO);

    }

    public void performDirectKitCaptureValidation(DirectCaptureValidatorVO directCaptureValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        performCaptureStep1Validation(directCaptureValidatorVO);
        if(functions.isValueNull(directCaptureValidatorVO.getErrorMessage()))
            return;
        performCaptureStep2Validation(directCaptureValidatorVO);
    }

    public void performDirectKitCancelValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        performCancelStep1Validation(commonValidatorVO);
        if(functions.isValueNull(commonValidatorVO.getErrorMsg()))
            return;
        performCancelStep2Validation(commonValidatorVO);
    }

    private String validateRestKitParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validation for Rest Kit Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestKitMandatoryParameters());
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(),commonValidatorVO.getTerminalId()));
        //validate all parameters
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        //Validation for Rest Kit Optional parameters
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestKitOptionalParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputOptionalFieldsList,errorList1,true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);

        //Validation for Direct Kit specific field validation based on flag
        error = error + validateRestKitSpecificParameters(commonValidatorVO, actionName);

        return error;
    }

    private String validateCommonParametersStep1(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //prepare list for general Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getSTDKitMandatoryFieldStep1());
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));

        //validate all parameters
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        //prepare list for general Optional parameters
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getSTDKitOptionalFieldStep1());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputOptionalFieldsList,errorList1,true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);

        return error;
    }

    private String validatePayModeCardType(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //prepare list for general Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterListDK(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));

        //validate all parameters
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;
    }

    private String validateStandardProcessStep1(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        //prepare list for general Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        //inputMandatoryFieldsList.addAll(inputValiDatorUtils.getSTDFlowMandatoryFieldStep1());//check moved to validateStandardProcessStep0 before fatching terminals
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));

        //validate all parameters

        inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        //prepare list for general Optional parameters
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getSTDKitOptionalFieldStep1());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputOptionalFieldsList, errorList1, true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);
        return error;
    }

    private String validateStandardProcessStep0(CommonValidatorVO commonValidatorVO,String actionName)
    {
        log.debug("inside validation for PT ----"+commonValidatorVO.getPaymentType());
        log.debug("inside validation for CT----"+commonValidatorVO.getCardType());
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //prepare list for general Optional parameters
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getSKitOptionalForPTandCTFieldStep1());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputOptionalFieldsList,errorList1,true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);

        //prepare list for general Conditional parameters
        List<InputFields> inputConditionalFieldsList = new ArrayList<InputFields>();
        inputConditionalFieldsList.addAll(inputValiDatorUtils.getSKitConditionalFieldStep1(commonValidatorVO));
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputConditionalFieldsList,errorList2,false);
        error = error + inputValiDatorUtils.getError(errorList2,inputConditionalFieldsList,actionName);
        //prepare list for general Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getSTDFlowMandatoryFieldStep1());
        //inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));

        //validate all mandatory parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;
    }

    public String validateCardRegistrationStep0(CommonValidatorVO commonValidatorVO, String actionName)
    {
        GenericCardDetailsVO cardDetailsVO=commonValidatorVO.getCardDetailsVO();
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //prepare list for general Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getCardRegistrationMandatoryFieldStep1());

        //validate all mandatory parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getAddressFieldValidation());

        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputOptionalFieldsList,errorList1,true);
        error = error+ inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);

        if(functions.isValueNull(cardDetailsVO.getCardNum())){
            List<InputFields> inputCardFieldsList = new ArrayList<InputFields>();
            inputCardFieldsList.addAll(inputValiDatorUtils.getRestCardDetailValidation());

            ValidationErrorList errorList2 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO,inputCardFieldsList,errorList2,false);
            error = error+ inputValiDatorUtils.getError(errorList2,inputCardFieldsList,actionName);

        }

        return error;
    }

    private String validateCommonParametersStep2(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";
        InputValidator inputValidator = new InputValidator();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getMandatoryFieldStep2());
        ValidationErrorList errorList = new ValidationErrorList();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));
        inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

       /* if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getAddressValidation()))
        {
            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, false);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
        else if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getAddressValidation()))
        {
            List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
            inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
            ValidationErrorList addressValidationError = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
            error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);
        }
*/
        List<InputFields> inputMandatoryFieldsList2 = new ArrayList<InputFields>();
        inputMandatoryFieldsList2.addAll(inputValiDatorUtils.getSTDKitOptionalFieldStep2());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList2, errorList2, true);
        error = error + inputValiDatorUtils.getError(errorList2,inputMandatoryFieldsList2,actionName);

        return error;
    }

    private String validateDirectParametersStep2(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";
        InputValidator inputValidator = new InputValidator();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getDKMandatoryFieldStep2());
        ValidationErrorList errorList = new ValidationErrorList();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));
        inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

        List<InputFields> inputFlagBasedCardValidation = new ArrayList<InputFields>();
        inputFlagBasedCardValidation.addAll(inputValiDatorUtils.getCardDetailValidationForDK());
        ValidationErrorList cardValidaionError = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputFlagBasedCardValidation, cardValidaionError, true);
        error = error + inputValiDatorUtils.getError(cardValidaionError,inputFlagBasedCardValidation,actionName);

        List<InputFields> inputMandatoryFieldsList2 = new ArrayList<InputFields>();
        inputMandatoryFieldsList2.addAll(inputValiDatorUtils.getSTDKitOptionalFieldStep2());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList2, errorList2, true);
        error = error + inputValiDatorUtils.getError(errorList2,inputMandatoryFieldsList2,actionName);

        if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
        {
            List<InputFields> inputMandatoryFieldsList3 = new ArrayList<InputFields>();
            inputMandatoryFieldsList3.addAll(inputValiDatorUtils.getDKConditionalParam());
            ValidationErrorList errorList3 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList3, errorList3, false);
            error = error + inputValiDatorUtils.getError(errorList3,inputMandatoryFieldsList3,actionName);
        }
        else
        {
            List<InputFields> inputMandatoryFieldsList3 = new ArrayList<InputFields>();
            inputMandatoryFieldsList3.addAll(inputValiDatorUtils.getDKConditionalParam());
            ValidationErrorList errorList3 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList3, errorList3, true);
            error = error + inputValiDatorUtils.getError(errorList3,inputMandatoryFieldsList3,actionName);
        }


        return error;
    }

    private String validateStandardKitSpecificParametersStep1(CommonValidatorVO commonValidatorVO)
    {
        String error="";
        Functions functions=new Functions();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        /*List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        ValidationErrorList errorList = new ValidationErrorList();
        inputMandatoryFieldsList.add(InputFields.TMPL_AMOUNT);
        inputMandatoryFieldsList.add(InputFields.TMPL_CURRENCY);
        inputMandatoryFieldsList.add(InputFields.REDIRECT_URL);
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error =  error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,"STDKIT");
        if(!functions.isEmptyOrNull(error))
        {
            return error;
        }*/

        /*if(commonValidatorVO.getRecurringBillingVO()!=null)
        {
            List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
            ValidationErrorList oErrorList = new ValidationErrorList();
            inputOptionalFieldsList.add(InputFields.RESERVEFIELD1);
            inputValidator.InputValidations(commonValidatorVO, inputOptionalFieldsList, oErrorList, false);
            error = inputValiDatorUtils.getError(oErrorList, inputOptionalFieldsList, "STDKIT");
            return error;
        }
        else
        {
            List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
            ValidationErrorList oErrorList = new ValidationErrorList();
            inputOptionalFieldsList.add(InputFields.RESERVEFIELD1);
            inputValidator.InputValidations(commonValidatorVO, inputOptionalFieldsList, oErrorList, true);
            error = inputValiDatorUtils.getError(oErrorList, inputOptionalFieldsList, "STDKIT");
            return error;
        }*/
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        ValidationErrorList oErrorList = new ValidationErrorList();
        //inputOptionalFieldsList.add(InputFields.TMPL_AMOUNT);
        inputOptionalFieldsList.add(InputFields.TMPL_CURRENCY);
        // inputOptionalFieldsList.add(InputFields.RESERVEFIELD1);
        inputValidator.InputValidations(commonValidatorVO, inputOptionalFieldsList, oErrorList, true);
        error = inputValiDatorUtils.getError(oErrorList, inputOptionalFieldsList, "STDKIT");
        return error;
    }

    private String validateManualRebillParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();

        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //prepare list for general Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getManualRebillMandatoryFields());
        //validate all parameters
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;
    }


    public MerchantDetailsVO getMerchantConfigDetails(String toid, String toType) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberAndPartnerDetails(toid, toType);

        return merchantDetailsVO;
    }

    protected MerchantDetailsVO getMerchantDetailsForSTDFlow(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberDetails(toid);

        return merchantDetailsVO;
    }

    protected HashMap getListofPaymentandCardtype(String toid,String currency,String accountId) throws PZDBViolationException
    {
        HashMap cardMap = new HashMap();

        TerminalDAO terminalDAO = new TerminalDAO();
        cardMap = terminalDAO.getListofPaymentandCardtype(toid, currency,accountId);

        return cardMap;
    }

    protected HashMap getListofPaymentandCardtype1(String toid,String currency1,String currency2,String accountId) throws PZDBViolationException
    {
        HashMap cardMap = new HashMap();

        TerminalDAO terminalDAO = new TerminalDAO();
        cardMap = terminalDAO.getListofPaymentandCardtype1(toid,currency1,currency2,accountId);

        return cardMap;
    }

    protected HashMap getListofPaymentandCardtype(String toid) throws PZDBViolationException
    {
        HashMap cardMap = new HashMap();

        TerminalDAO terminalDAO = new TerminalDAO();
        cardMap = terminalDAO.getListofPaymentandCardtype(toid);

        return cardMap;
    }

    protected HashMap getListofPaymentandCardtype(String toid,String currency,String payId,String accountId) throws PZDBViolationException
    {
        HashMap cardMap = new HashMap();

        TerminalDAO terminalDAO = new TerminalDAO();
        cardMap = terminalDAO.getListofPaymentandCardtype(toid,currency,payId,accountId);

        return cardMap;
    }

    protected HashMap getListofPaymentandCardtype(String toid,String currency1,String currency,String payId,String accountId) throws PZDBViolationException
    {
        HashMap cardMap = new HashMap();

        TerminalDAO terminalDAO = new TerminalDAO();
        cardMap = terminalDAO.getListofPaymentandCardtype(toid,currency1,currency,payId,accountId);

        return cardMap;
    }

    protected HashMap getTerminaliIDForSKNewFlow(String toid,String terminalid)
    {
        HashMap map = new HashMap();

        TerminalDAO terminalDAO = new TerminalDAO();
        map = terminalDAO.getTerminaliIDForSKNewFlow(toid, terminalid);

        return map;
    }

    private MerchantDetailsVO getMerchantDetails(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberDetails(toid);

        return merchantDetailsVO;
    }

    private String validateStandardKitSpecificParametersStep2(CommonValidatorVO commonValidatorVO,String actionName)
    {
        return "";
    }

    public CommonValidatorVO validateResfield1(CommonValidatorVO commonValidatorVO,String actionName)throws PZDBViolationException
    {
        String error="";
        Functions functions=new Functions();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        ValidationErrorList errorList = new ValidationErrorList();
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();

        if(("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()) && "N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring())) || ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()) && commonValidatorVO.getRecurringBillingVO()!=null && functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getReqField1())))
        {
            inputMandatoryFieldsList.add(InputFields.RESERVEFIELD1);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
            error = inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
            if (functions.isValueNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, "VALIDATION_INVALID_PARAMETER",ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }

        }
        else
        {
            inputMandatoryFieldsList.add(InputFields.RESERVEFIELD1);
            inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,true);
            error =  inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
            if(functions.isValueNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, "VALIDATION_INVALID_PARAMETER",ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
        }

        /*if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring()) && commonValidatorVO.getRecurringBillingVO().getReqField1()!=null)
        {
            error = "This functionality is not allowed. Please contact customer care:::";
            commonValidatorVO.setErrorMsg(error);
        }*/
        return commonValidatorVO;

    }

    private String validateDirectKitSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        Functions functions=new Functions();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
        {
            List<InputFields> inputMandatoryFieldsList = new ArrayList();
            inputMandatoryFieldsList.add(InputFields.BIRTHDATE);
            inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,true);
            error =  inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
        {
            List<InputFields> inputMandatoryFieldsList = new ArrayList();
            inputMandatoryFieldsList.add(InputFields.CARDHOLDERIP);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, true);
            error = inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }

        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAddressValidation()))
        {
            List<InputFields> inputMandatoryFieldsList1 = new ArrayList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputMandatoryFieldsList1.add(InputFields.LANGUAGE);
            inputMandatoryFieldsList1.add(InputFields.REDIRECT_URL);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList1, errorList1, false);
            error = inputValiDatorUtils.getError(errorList1, inputMandatoryFieldsList1, actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }
        else
        {
            List<InputFields> inputMandatoryFieldsList1 = new ArrayList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputMandatoryFieldsList1.add(InputFields.LANGUAGE);
            inputMandatoryFieldsList1.add(InputFields.REDIRECT_URL);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList1, errorList1, true);
            error = inputValiDatorUtils.getError(errorList1, inputMandatoryFieldsList1, actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }
        if ((!commonValidatorVO.getAddressDetailsVO().getLanguage().trim().equals("ENG")) && (!commonValidatorVO.getAddressDetailsVO().getLanguage().trim().equals("RUS")) && (!commonValidatorVO.getAddressDetailsVO().getLanguage().trim().equals("CHN")))
        {
            log.debug("Invalid language");
            transactionLogger.debug("Invalid language");
            error = error + "Please Enter Language in Capital Letters Only OR Requested Language is not Supported";
            if(commonValidatorVO.getErrorCodeListVO()!=null)
            {
                ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LANGUAGE);
                errorCodeVO.setErrorReason("Please Enter Language in Capital Letters Only OR Requested Language is not Supported");
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            }
        }

        if ("Y".equalsIgnoreCase(account.getIsRecurring()))
            if(("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()) && "N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring())) || ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()) && commonValidatorVO.getRecurringBillingVO()!=null && functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getReqField1())))
            {
                List<InputFields> inputMandatoryFieldsList = new ArrayList();
                inputMandatoryFieldsList.add(InputFields.RESERVEFIELD1);
                inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
                error = inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
                if (functions.isValueNull(error))
                {
                    return error;
                }

            }
            else if (commonValidatorVO.getRecurringBillingVO() != null && functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getReqField1()))
            {
                List<InputFields> inputMandatoryFieldsList = new ArrayList();
                inputMandatoryFieldsList.add(InputFields.RESERVEFIELD1);
                inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,true);
                error =  inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
                if(functions.isValueNull(error))
                {
                    return error;
                }
            }

        if(commonValidatorVO.getReserveField2VO()!=null /*&& functions.isValueNull(commonValidatorVO.getReserveField2VO().getAccountType()+"|"+commonValidatorVO.getReserveField2VO().getAccountNumber()+"|"+commonValidatorVO.getReserveField2VO().getRoutingNumber())*/)
        {
            List<InputFields> inputMandatoryFieldsList = new ArrayList();
            inputMandatoryFieldsList.add(InputFields.RESERVEFIELD2);
            inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,true);
            error = inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }
        if (!account.isCvvRequired())
        {
            List<InputFields> inputMandatoryFieldsList = new ArrayList();
            inputMandatoryFieldsList.add(InputFields.CVV);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, true);
            error = inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }
        else
        {
            List<InputFields> inputMandatoryFieldsList = new ArrayList();
            inputMandatoryFieldsList.add(InputFields.CVV);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
            error = inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }
        return error;
    }

    private String validateRestKitSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        Functions functions=new Functions();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList();
        ValidationErrorList errorList = new ValidationErrorList();

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
        {
            inputMandatoryFieldsList.add(InputFields.BIRTHDATE);
            inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,true);
            error =  inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
            if (functions.isValueNull(error))
            {
                return error;
            }
        }

        if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getAddressValidation()))
        {
            List<InputFields> inputMandatoryFieldsList1 = new ArrayList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputMandatoryFieldsList1.add(InputFields.LANGUAGE);
            inputMandatoryFieldsList1.add(InputFields.REDIRECT_URL);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList1, errorList1, false);
            error = inputValiDatorUtils.getError(errorList1, inputMandatoryFieldsList1, actionName);
        }
        else
        {
            List<InputFields> inputMandatoryFieldsList1 = new ArrayList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputMandatoryFieldsList1.add(InputFields.LANGUAGE);
            inputMandatoryFieldsList1.add(InputFields.REDIRECT_URL);
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList1, errorList1, true);
            error = inputValiDatorUtils.getError(errorList1, inputMandatoryFieldsList1, actionName);
        }
        if ((!commonValidatorVO.getAddressDetailsVO().getLanguage().trim().equals("ENG")) && (!commonValidatorVO.getAddressDetailsVO().getLanguage().trim().equals("RUS")) && (!commonValidatorVO.getAddressDetailsVO().getLanguage().trim().equals("CHN")))
        {
            log.debug("Invalid language");
            transactionLogger.debug("Invalid language");
            error = error + "Please Enter Language in Capital Letters Only OR Requested Language is not Supported";
            if(commonValidatorVO.getErrorCodeListVO()!=null)
            {
                ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LANGUAGE);
                errorCodeVO.setErrorReason("Please Enter Language in Capital Letters Only OR Requested Language is not Supported");
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            }
        }

        return error;
    }
    /**
     * This is to Validate the Request for refund
     * @param directRefundValidatorVO
     */
    public void performRefundStep1Validation(DirectRefundValidatorVO directRefundValidatorVO)
    {
        InputValidator inputValidator=new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        if("Y".equalsIgnoreCase(directRefundValidatorVO.getFlightMode()))
            inputMandatoryFieldsList.add(InputFields.PARTNER_ID);
        else
            inputMandatoryFieldsList.add(InputFields.TOID);
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        inputMandatoryFieldsList.add(InputFields.CHECKSUM);

        inputMandatoryFieldsList.add(InputFields.AMOUNT);
        inputMandatoryFieldsList.add(InputFields.REASON);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(directRefundValidatorVO,inputMandatoryFieldsList,errorList,false);

        String error="";
        for(ValidationException validationException:errorList.errors())
        {
            PZValidationException pzValidationException=(PZValidationException)validationException;
            ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
            error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason()+ " | ";
        }
        if(functions.isValueNull(error))
            directRefundValidatorVO.setErrorMessage(error);
    }

    /**
     * This is to check User Input for Refunding transaction
     * @param directRefundValidatorVO
     * @throws PZConstraintViolationException
     * @throws PZTechnicalViolationException
     */
    private void performRefundStep2Validation(DirectRefundValidatorVO directRefundValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        //Support class for Checks
        PaymentChecker paymentChecker =new PaymentChecker();
        TransactionUtil transactionUtil = new TransactionUtil();
        //List of error code vo list
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        //errorCodeFVo declaration
        ErrorCodeVO errorCodeVO =null;
        //Other Vo instance
        MerchantDetailsVO merchantDetailsVO =null;

        if(!paymentChecker.isIpWhitelistedForMember(directRefundValidatorVO.getMerchantDetailsVO().getMemberId(),directRefundValidatorVO.getMerchantIpAddress()))
        {

            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_IPWHITELIST_CHECK);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.WHITELIST_IP);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performRefundStep2Validation()", null, "common", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.WHITELIST_IP, new Throwable(ErrorMessages.WHITELIST_IP));
        }

        if (directRefundValidatorVO.getMerchantDetailsVO()==null || !functions.isValueNull(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()) || Functions.convertStringtoInt(directRefundValidatorVO.getMerchantDetailsVO().getMemberId(), 0) == 0)
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performRefundStep2Validation()", null,"common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, ErrorMessages.INVALID_TOID, new Throwable(ErrorMessages.INVALID_TOID));
        }

        //Loading memberDetails for the toid given

        merchantDetailsVO=getMerchantDetails(directRefundValidatorVO.getMerchantDetailsVO().getMemberId());
        if(functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            directRefundValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        else
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performDirectKitRefundValidation()", null,"common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, ErrorMessages.INVALID_TOID, new Throwable(ErrorMessages.INVALID_TOID));
        }

        checksumVerificationForRefundREST(directRefundValidatorVO,directRefundValidatorVO.getMerchantDetailsVO().getKey());
    }

    private void performInquiryStep1Validation(DirectInquiryValidatorVO directInquiryValidatorVO)
    {
        InputValidator inputValidator=new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.CHECKSUM);
        inputMandatoryFieldsList.add(InputFields.TOID);
        if(functions.isValueNull(directInquiryValidatorVO.getTrackingId()))
            inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        if(functions.isValueNull(directInquiryValidatorVO.getDescription()))
            inputMandatoryFieldsList.add(InputFields.DESCRIPTION);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(directInquiryValidatorVO,inputMandatoryFieldsList,errorList,true);

        String error="";
        for(ValidationException validationException:errorList.errors())
        {
            PZValidationException pzValidationException=(PZValidationException)validationException;
            ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
            error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason()+ " | ";
        }
        if(functions.isValueNull(error))
            directInquiryValidatorVO.setErrorMessage(error);
    }

    private void performInquiryStep2Validation(DirectInquiryValidatorVO directInquiryValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        //Support class for Checks
        PaymentChecker paymentChecker =new PaymentChecker();
        TransactionUtil transactionUtil = new TransactionUtil();

        ErrorCodeVO errorCodeVO =null;
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();

        if (!functions.isValueNull(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId()) || null == directInquiryValidatorVO.getMerchantDetailsVO().getMemberId() || directInquiryValidatorVO.getMerchantDetailsVO().getMemberId().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(),"performInquiryStep2Validation()",null,"Common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,ErrorMessages.INVALID_TOID,new Throwable(ErrorMessages.INVALID_TOID));
        }
        else
        {
            MerchantDetailsVO merchantDetailsVO = null;
            merchantDetailsVO = getMerchantDetails(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId());
            if (null == merchantDetailsVO.getMemberId() || merchantDetailsVO.getMemberId().equals("") || merchantDetailsVO.getMemberId().equals("null"))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                if (errorCodeVO != null)
                {
                    errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                    errorCodeListVO.addListOfError(errorCodeVO);
                }
                PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performInquiryStep2Validation()", null, "Common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_TOID, new Throwable(ErrorMessages.INVALID_TOID));
            }
            else
            {
                directInquiryValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            }
        }

        if(!paymentChecker.isIpWhitelistedForMember(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(),directInquiryValidatorVO.getMerchantIpAddress()))
        {

            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_IPWHITELIST_CHECK);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.WHITELIST_IP);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performInquiryStep2Validation()", null, "Common", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.WHITELIST_IP, new Throwable(ErrorMessages.WHITELIST_IP));
        }

        if("N".equalsIgnoreCase(directInquiryValidatorVO.getMerchantDetailsVO().getActivation()))
        {
            errorCodeListVO = null;
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(),"performInquiryStep2Validation()",null,"Common", ErrorMessages.ACCOUNT_LIVE, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }

        if(!(transactionUtil.generateStatusChecksum(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), directInquiryValidatorVO.getDescription(), directInquiryValidatorVO.getTrackingId(), directInquiryValidatorVO.getMerchantDetailsVO().getKey(),directInquiryValidatorVO.getMerchantDetailsVO().getFlightMode())).equals(directInquiryValidatorVO.getCheckSum()))
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_CHECKSUM);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.CHECKSUM_MISMATCH);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performInquiryStep2Validation()", null, "common", ErrorMessages.CHECKSUM_MISMATCH, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.CHECKSUM_MISMATCH, new Throwable(ErrorMessages.CHECKSUM_MISMATCH));
        }
    }

    public void performCaptureStep1Validation(DirectCaptureValidatorVO directCaptureValidatorVO)
    {
        InputValidator inputValidator=new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        if("Y".equalsIgnoreCase(directCaptureValidatorVO.getFlightMode()))
            inputMandatoryFieldsList.add(InputFields.PARTNER_ID);
        else
            inputMandatoryFieldsList.add(InputFields.TOID);
        inputMandatoryFieldsList.add(InputFields.CHECKSUM);
        inputMandatoryFieldsList.add(InputFields.CAPTUREAMOUNT);
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(directCaptureValidatorVO,inputMandatoryFieldsList,errorList,false);

        String error="";
        for(ValidationException validationException:errorList.errors())
        {
            PZValidationException pzValidationException=(PZValidationException)validationException;
            ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
            error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason()+ " | ";
        }
        if(functions.isValueNull(error))
            directCaptureValidatorVO.setErrorMessage(error);
    }

    private void performCaptureStep2Validation(DirectCaptureValidatorVO directCaptureValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        //Support class for Checks
        PaymentChecker paymentChecker =new PaymentChecker();
        TransactionUtil transactionUtil = new TransactionUtil();

        ErrorCodeVO errorCodeVO =null;
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();

        MerchantDetailsVO merchantDetailsVO=null;
        merchantDetailsVO=getMerchantDetails(directCaptureValidatorVO.getMerchantDetailsVO().getMemberId());
        if(!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(),"performCaptureStep2Validation()",null,"Common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,ErrorMessages.INVALID_TOID,new Throwable(ErrorMessages.INVALID_TOID));
        }
        else
        {
            directCaptureValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        if(!paymentChecker.isIpWhitelistedForMember(directCaptureValidatorVO.getMerchantDetailsVO().getMemberId(),directCaptureValidatorVO.getMerchantIpAddress()))
        {

            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_IPWHITELIST_CHECK);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.WHITELIST_IP);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performCaptureStep2Validation()", null, "Common", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.WHITELIST_IP, new Throwable(ErrorMessages.WHITELIST_IP));
        }

        if("N".equalsIgnoreCase(directCaptureValidatorVO.getMerchantDetailsVO().getActivation()))
        {
            errorCodeListVO = null;
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(),"performCaptureStep2Validation()",null,"Common", ErrorMessages.ACCOUNT_LIVE, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }
        checksumVerificationForCaptureREST(directCaptureValidatorVO, directCaptureValidatorVO.getMerchantDetailsVO().getKey());

    }

    public void performCancelStep1Validation(CommonValidatorVO commonValidatorVO)
    {
        InputValidator inputValidator=new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        if("Y".equalsIgnoreCase(commonValidatorVO.getFlightMode()))
            inputMandatoryFieldsList.add(InputFields.PARTNER_ID);
        else
            inputMandatoryFieldsList.add(InputFields.TOID);

        inputMandatoryFieldsList.add(InputFields.CHECKSUM);
        inputMandatoryFieldsList.add(InputFields.DESCRIPTION);
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        /*if(functions.isValueNull(directInquiryValidatorVO.getTrackingId()))
            inputMandatoryFieldsList.add(InputFields.TRACKINGID);*/

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);

        String error="";
        for(ValidationException validationException:errorList.errors())
        {
            PZValidationException pzValidationException=(PZValidationException)validationException;
            ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
            error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason()+ " | ";
        }
        if(functions.isValueNull(error))
            commonValidatorVO.setErrorMsg(error);
    }

    private void performCancelStep2Validation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException, PZTechnicalViolationException
    {
        //Support class for Checks
        PaymentChecker paymentChecker =new PaymentChecker();
        TransactionUtil transactionUtil = new TransactionUtil();

        ErrorCodeVO errorCodeVO =null;
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();

        MerchantDetailsVO merchantDetailsVO = null;
        merchantDetailsVO=getMerchantDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        if(!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(),"performCancelStep2Validation()",null,"Common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,ErrorMessages.INVALID_TOID,new Throwable(ErrorMessages.INVALID_TOID));
        }
        else
        {
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        /*if(!paymentChecker.isIpWhitelistedForMember(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
        {

            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_IPWHITELIST_CHECK);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.WHITELIST_IP);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performCancelStep2Validation()", null, "Common", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.WHITELIST_IP, new Throwable(ErrorMessages.WHITELIST_IP));
        }*/

        if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getActivation()))
        {
            errorCodeListVO = null;
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(),"performCancelStep2Validation()",null,"Common", ErrorMessages.ACCOUNT_LIVE, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);
        }
        checksumVerificationForCancelREST(commonValidatorVO,commonValidatorVO.getMerchantDetailsVO().getKey());
        /*if(!(transactionUtil.generateCancelChecksum(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getMerchantDetailsVO().getKey())).equals(commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_CHECKSUM);
            if(errorCodeVO!=null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.CHECKSUM_MISMATCH);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performCancelStep2Validation()", null, "common", ErrorMessages.CHECKSUM_MISMATCH, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.CHECKSUM_MISMATCH, new Throwable(ErrorMessages.CHECKSUM_MISMATCH));
        }*/
    }
    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName) throws PZDBViolationException
    {
        String error="";
        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String action, String addressValidation)
    {
        return null;
    }

    private String validateTokenTransactionParameter(CommonValidatorVO commonValidatorVO,String actionName)
    {
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //prepare list for general Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getTokenTransactionMandatoryField());
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterList(merchantDetailsVO.getAutoSelectTerminal(), commonValidatorVO.getTerminalId()));

        //validate all parameters
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getSystemErrorCode(errorName);
        errorCodeVO.setErrorName(errorName);
        transactionLogger.debug("error code ---2866----"+errorCodeVO.getErrorCode());
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getSystemErrorCode(errorName);
        errorCodeVO.setErrorReason(reason);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    public CommonValidatorVO performRestTransactionValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        String error="";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

        Functions functions=new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        String toid=merchantDetailsVO.getMemberId();
        //String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();

        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;

            /*transactionLogger.debug("toid in validation----" + toid);
            merchantDetailsVO = getMerchantDetails(toid);

        //If memberid and partnerid combination not found
        if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
            error = ErrorMessages.MISCONFIGURED_TOID;
            commonValidatorVO.setErrorMsg(error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.INPUT_VALIDATION_FAILED.toString());
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }*/
        //merchantDetailsVO.setAccountId(accountId);
        //commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        //}

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            transactionLogger.debug("ip address--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_IPWHITELIST_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_ACTIVATION_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        //ToDO - check for Card Number Not Empty
        //Todo - new method to decrypt card before checksum verification both for member and partner

        //ToDO - check if paymentType, CardType and TerminalType all are empty , if yes and mode is IsFlightMode = Y then assign paymentType and cardType

        if (!functions.isValueNull(commonValidatorVO.getCardType()) && (commonValidatorVO.getTerminalId().equalsIgnoreCase("0") || !functions.isValueNull(commonValidatorVO.getTerminalId())))
        {
            if (commonValidatorVO.getPaymentType() != null && commonValidatorVO.getPaymentType().equals("1"))
            {
                if (merchantDetailsVO.getFlightMode().equalsIgnoreCase("Y"))
                {
                    String cardType = functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum());
                    commonValidatorVO.setCardType(GatewayAccountService.getCardId(cardType));
                }
            }
        }

        //ToDo - PaymentType will be 1 and CardTypeid to be checked from Card Number



        error =  validateRestKitParameters(commonValidatorVO, "DKIT");

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorname = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        /*log.error("accountID error---> in commonInputvalidator --> "+error);
        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }*/
        return commonValidatorVO;
    }

    public CommonValidatorVO getMerchantDetailsForIFE(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        String toid = merchantDetailsVO.getMemberId();
        String error = "";
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String accountId = merchantDetailsVO.getAccountId();
        merchantDetailsVO = getMerchantDetails(toid);

        //If memberid and partnerid combination not found
        if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
            error = ErrorMessages.MISCONFIGURED_TOID;
            commonValidatorVO.setErrorMsg(error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        merchantDetailsVO.setAccountId(accountId);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO decryptCardDetailsForIFEMerchant(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();

        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        String error = "";
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        transactionLogger.debug("enc getcVV---"+genericCardDetailsVO.getcVV());
        transactionLogger.debug("enc CardNu---"+genericCardDetailsVO.getCardNum());
        transactionLogger.debug("enc getExpYear---"+genericCardDetailsVO.getExpYear());
        transactionLogger.debug("enc getExpMonth---"+genericCardDetailsVO.getExpMonth());
        transactionLogger.debug("mKey---"+merchantDetailsVO.getKey());
        transactionLogger.debug("flag---"+merchantDetailsVO.getIsCardEncryptionEnable());

        if ("Y".equals(merchantDetailsVO.getIsCardEncryptionEnable()))
        {
            try
            {
                String mKey = merchantDetailsVO.getKey();
                AESEncryptionManager encryptionManager=new AESEncryptionManager();
                if (functions.isValueNull(genericCardDetailsVO.getcVV()))
                    genericCardDetailsVO.setcVV(encryptionManager.decrypt(genericCardDetailsVO.getcVV(), mKey));

                genericCardDetailsVO.setCardNum(encryptionManager.decrypt(genericCardDetailsVO.getCardNum(), mKey));
                genericCardDetailsVO.setExpYear(encryptionManager.decrypt(genericCardDetailsVO.getExpYear(), mKey));
                genericCardDetailsVO.setExpMonth(encryptionManager.decrypt(genericCardDetailsVO.getExpMonth(), mKey));
            }
            catch (Exception e)
            {
                transactionLogger.error("Encryption Exception:::",e);

                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CARD_ENCRYPTION);
                error = errorCodeVO.getErrorCode() + "_" + ErrorMessages.INVALID_CARD_ENCRYPTION;
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.VALIDATION_TOID_INVALID.toString(), ErrorType.VALIDATION.toString());
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }

        transactionLogger.debug("dec getcVV---"+genericCardDetailsVO.getcVV());
        transactionLogger.debug("dec CardNu---"+genericCardDetailsVO.getCardNum());
        transactionLogger.debug("dec getExpYear---"+genericCardDetailsVO.getExpYear());
        transactionLogger.debug("dec getExpMonth---"+genericCardDetailsVO.getExpMonth());

        if((genericCardDetailsVO.getCardNum()!="" || !genericCardDetailsVO.getCardNum().equals("")) && !Functions.isValid(genericCardDetailsVO.getCardNum()))
        {
            error = "Invalid Credit card number";
            errorCodeListVO = getErrorVO(ErrorName.SYS_LUHN_CHECK);
            /*Transaction request rejected log entry with reason:Partner-CARD_LUHN_SERIAL*/
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_LUHN_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", error, "Common", "Invalid Credit card number.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO decryptCardDetailsForIFEPartner(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();

        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        String error = "";
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        if ("Y".equals(partnerDetailsVO.getIsCardEncryptionEnable()))
        {
            try
            {
                String mKey = partnerDetailsVO.getPartnerKey();
                AESEncryptionManager encryptionManager=new AESEncryptionManager();
                genericCardDetailsVO.setcVV(encryptionManager.decrypt(genericCardDetailsVO.getcVV(), mKey));
                genericCardDetailsVO.setCardNum(encryptionManager.decrypt(genericCardDetailsVO.getCardNum().trim(), mKey));
                genericCardDetailsVO.setExpYear(encryptionManager.decrypt(genericCardDetailsVO.getExpYear().trim(), mKey));
                genericCardDetailsVO.setExpMonth(encryptionManager.decrypt(genericCardDetailsVO.getExpMonth().trim(), mKey));
                commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);

                //setting card type
                if (!functions.isValueNull(commonValidatorVO.getCardType()) && (commonValidatorVO.getTerminalId().equalsIgnoreCase("0") || !functions.isValueNull(commonValidatorVO.getTerminalId())))
                {
                    if (commonValidatorVO.getPaymentType() != null && commonValidatorVO.getPaymentType().equals("1"))
                    {
                        if (partnerDetailsVO.getFlightMode().equalsIgnoreCase("Y"))
                        {
                            String cardType = functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum());
                            transactionLogger.error("cardType no---"+cardType);
                            commonValidatorVO.setCardType(GatewayAccountService.getCardId(cardType));
                            transactionLogger.error("cardType ID---"+commonValidatorVO.getCardType());
                        }
                    }
                }
            }
            catch (Exception e)
            {
                transactionLogger.error("Encryption Exception:::",e);

                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CARD_ENCRYPTION);
                error = errorCodeVO.getErrorCode() + "_" + ErrorMessages.INVALID_CARD_ENCRYPTION;
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_CARD_ENCRYPTION.toString(), ErrorType.VALIDATION.toString());
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }

        if((commonValidatorVO.getCardDetailsVO().getCardNum()!="" || !commonValidatorVO.getCardDetailsVO().getCardNum().equals("")) && !Functions.isValid(commonValidatorVO.getCardDetailsVO().getCardNum()))
        {
            error = "Invalid Credit card number";
            errorCodeListVO = getErrorVO(ErrorName.SYS_LUHN_CHECK);
            /*Transaction request rejected log entry with reason:Partner-CARD_LUHN_SERIAL*/
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_LUHN_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", error, "Common", "Invalid Credit card number.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        return commonValidatorVO;
    }


    public void checksumverificationForMember(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        String calchecksum="";
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        try
        {
            calchecksum = Functions.generateChecksumDirectKit(merchantDetailsVO.getMemberId(),genericTransDetailsVO.getTotype(),genericTransDetailsVO.getAmount(),genericTransDetailsVO.getOrderId(),genericTransDetailsVO.getRedirectUrl(),genericCardDetailsVO.getCardNum(),merchantDetailsVO.getKey(),merchantDetailsVO.getChecksumAlgo());
        }
        catch(NoSuchAlgorithmException e )
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
            error =  "Error while generating checksum. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_CHECKSUM_ERROR);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CHECKSUM_ERROR.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, e.getMessage(), e.getCause());
        }

        if (!genericTransDetailsVO.getChecksum().equals(calchecksum))
        {
            error =  "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_CHECKSUM.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
    }

    public void checksumVerificationForPartner(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        SplitPaymentVO splitPaymentVO = commonValidatorVO.getSplitPaymentVO();

        String calchecksum="";
        try
        {
            calchecksum = Functions.generateChecksumDirectKit(commonValidatorVO.getParetnerId(),genericTransDetailsVO.getTotype(),genericTransDetailsVO.getAmount(),genericTransDetailsVO.getOrderId(),genericTransDetailsVO.getRedirectUrl(),genericCardDetailsVO.getCardNum(),commonValidatorVO.getPartnerDetailsVO().getPartnerKey(),merchantDetailsVO.getChecksumAlgo());
        }
        catch(NoSuchAlgorithmException e )
        {
            log.error("Exception occur", e);
            error =  "Error while generating checksum. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_CHECKSUM_ERROR);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CHECKSUM_ERROR.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, e.getMessage(), e.getCause());
        }

        if (!genericTransDetailsVO.getChecksum().equals(calchecksum))
        {
            error =  "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_CHECKSUM.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
        }
    }

    public void checksumVerificationForRefundREST(DirectRefundValidatorVO directRefundValidatorVO,String key) throws PZConstraintViolationException,PZTechnicalViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        //errorCodeFVo declaration
        ErrorCodeVO errorCodeVO =null;
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        TransactionUtil transactionUtil = new TransactionUtil();

        String genChecksum = "";
        String requestId = "";
        if("Y".equalsIgnoreCase(directRefundValidatorVO.getFlightMode()))
            requestId = directRefundValidatorVO.getParetnerId();
        else
            requestId = directRefundValidatorVO.getMerchantDetailsVO().getMemberId();

        genChecksum = transactionUtil.generateMD5ChecksumForRefund(requestId, directRefundValidatorVO.getTrackingid(), directRefundValidatorVO.getRefundAmount(), key);

        if (!genChecksum.equals(directRefundValidatorVO.getCheckSum()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_CHECKSUM);
            if (errorCodeVO != null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.CHECKSUM_MISMATCH);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performDirectKitRefundValidation()", null, "common", ErrorMessages.CHECKSUM_MISMATCH, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.CHECKSUM_MISMATCH, new Throwable(ErrorMessages.CHECKSUM_MISMATCH));
        }
    }

    public void checksumVerificationForCaptureREST(DirectCaptureValidatorVO directCaptureValidatorVO,String key) throws PZConstraintViolationException,PZTechnicalViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        //errorCodeFVo declaration
        ErrorCodeVO errorCodeVO =null;
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        TransactionUtil transactionUtil = new TransactionUtil();

        String genChecksum = "";
        String requestId = "";
        if("Y".equalsIgnoreCase(directCaptureValidatorVO.getFlightMode()))
            requestId = directCaptureValidatorVO.getParetnerId();
        else
            requestId = directCaptureValidatorVO.getMerchantDetailsVO().getMemberId();

        genChecksum = transactionUtil.generateMD5ChecksumForRefund(requestId, directCaptureValidatorVO.getTrackingid(), directCaptureValidatorVO.getCaptureAmount(), key);

        if (!genChecksum.equals(directCaptureValidatorVO.getCheckSum()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_CHECKSUM);
            if (errorCodeVO != null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.CHECKSUM_MISMATCH);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performDirectKitRefundValidation()", null, "common", ErrorMessages.CHECKSUM_MISMATCH, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.CHECKSUM_MISMATCH, new Throwable(ErrorMessages.CHECKSUM_MISMATCH));
        }
    }

    public void checksumVerificationForCancelREST(CommonValidatorVO commonValidatorVO,String key) throws PZConstraintViolationException,PZTechnicalViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        //errorCodeFVo declaration
        ErrorCodeVO errorCodeVO =null;
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        TransactionUtil transactionUtil = new TransactionUtil();

        String genChecksum = "";
        String requestId = "";
        if("Y".equalsIgnoreCase(commonValidatorVO.getFlightMode()))
            requestId = commonValidatorVO.getParetnerId();
        else
            requestId = commonValidatorVO.getMerchantDetailsVO().getMemberId();

        genChecksum = transactionUtil.generateMD5ChecksumForRefund(requestId, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderId(), key);

        if (!genChecksum.equals(commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_CHECKSUM);
            if (errorCodeVO != null)
            {
                errorCodeVO.setErrorReason(ErrorMessages.CHECKSUM_MISMATCH);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
            PZExceptionHandler.raiseConstraintViolationException(CommonInputValidator.class.getName(), "performDirectKitRefundValidation()", null, "common", ErrorMessages.CHECKSUM_MISMATCH, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.CHECKSUM_MISMATCH, new Throwable(ErrorMessages.CHECKSUM_MISMATCH));
        }
    }

    public void setAllTemplateInformationRelatedToMerchant(MerchantDetailsVO merchantDetailsVO,String version) throws PZDBViolationException
    {
        PartnerManager partnerManager = new PartnerManager();
        Map<String,Object> merchantTemplateInformation= partnerManager.getSavedMemberTemplateDetails(merchantDetailsVO.getMemberId());

        String merchant_uploaded_path=null;
        try
        {
            merchant_uploaded_path=ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
            log.debug("merchant_uploaded_path---"+merchant_uploaded_path);
        }
        catch(Exception e)
        {
            log.error("Exception while upload logo---",e);
        }

        if(merchantTemplateInformation!=null && merchantTemplateInformation.size()>0)
        {
            FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
            //String sessionVersion = (String)session.getAttribute("version");

            if(functions.isValueNull(version) && version.equals("2"))
            {
                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_BUTTON_FONT_COLOR.name()))
                {
                    merchantDetailsVO.setHeadPanelFont_color((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_BUTTON_FONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_HEADER_FONT_COLOR.name()))
                {
                    merchantDetailsVO.setBodyPanelFont_color((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_HEADER_FONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                {
                    merchantDetailsVO.setPanelHeading_color((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_HEADERBACKGROUND_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                {
                    merchantDetailsVO.setPanelBody_color((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_NAVIGATIONBAR_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_BODYNFOOTER_COLOR.name()))
                {
                    merchantDetailsVO.setTemplateBackGround_color((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_BODYNFOOTER_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_FULLBACKGROUND_COLOR.name()))
                {
                    merchantDetailsVO.setBodyBgColor((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_FULLBACKGROUND_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_LABEL_FONT_COLOR.name()))
                {
                    merchantDetailsVO.setBodyFgColor((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_LABEL_FONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                {
                    merchantDetailsVO.setNavigationFontColor((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_BUTTON_COLOR.name()))
                {
                    merchantDetailsVO.setTextboxColor((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_BUTTON_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_ICON_COLOR.name()))
                {
                    merchantDetailsVO.setIconColor((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_ICON_COLOR.name()));
                }
                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_TIMER_COLOR.name()))
                {
                    merchantDetailsVO.setTimerColor((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_TIMER_COLOR.name()));
                }
                if (merchantTemplateInformation.containsKey(TemplatePreference.CHECKOUT_BOX_SHADOW.name()))
                {
                    merchantDetailsVO.setBoxShadow((String) merchantTemplateInformation.get(TemplatePreference.CHECKOUT_BOX_SHADOW.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_BODYNFOOTER_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_HEADERBACKGROUND_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_NAVIGATIONBAR_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_BUTTON_FONT_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_HEADER_FONT_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_FULLBACKGROUND_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_LABEL_FONT_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_BUTTON_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_ICON_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_TIMER_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_FOOTER_FONT_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()));
                if (merchantTemplateInformation.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                    merchantDetailsVO.setNEW_CHECKOUT_BOX_SHADOW((String) merchantTemplateInformation.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()));
            }
            else
            {
                if (merchantTemplateInformation.containsKey(TemplatePreference.HEADPANELFONT_COLOR.name()))
                {
                    merchantDetailsVO.setHeadPanelFont_color((String) merchantTemplateInformation.get(TemplatePreference.HEADPANELFONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.BODYPANELFONT_COLOR.name()))
                {
                    merchantDetailsVO.setBodyPanelFont_color((String) merchantTemplateInformation.get(TemplatePreference.BODYPANELFONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.PANELHEADING_COLOR.name()))
                {
                    merchantDetailsVO.setPanelHeading_color((String) merchantTemplateInformation.get(TemplatePreference.PANELHEADING_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.PANELBODY_COLOR.name()))
                {
                    merchantDetailsVO.setPanelBody_color((String) merchantTemplateInformation.get(TemplatePreference.PANELBODY_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.MAINBACKGROUNDCOLOR.name()))
                {
                    merchantDetailsVO.setTemplateBackGround_color((String) merchantTemplateInformation.get(TemplatePreference.MAINBACKGROUNDCOLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.BODY_BACKGROUND_COLOR.name()))
                {
                    merchantDetailsVO.setBodyBgColor((String) merchantTemplateInformation.get(TemplatePreference.BODY_BACKGROUND_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.BODY_FOREGROUND_COLOR.name()))
                {
                    merchantDetailsVO.setBodyFgColor((String) merchantTemplateInformation.get(TemplatePreference.BODY_FOREGROUND_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.NAVIGATION_FONT_COLOR.name()))
                {
                    merchantDetailsVO.setNavigationFontColor((String) merchantTemplateInformation.get(TemplatePreference.NAVIGATION_FONT_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.TEXTBOX_COLOR.name()))
                {
                    merchantDetailsVO.setTextboxColor((String) merchantTemplateInformation.get(TemplatePreference.TEXTBOX_COLOR.name()));
                }

                if (merchantTemplateInformation.containsKey(TemplatePreference.ICON_VECTOR_COLOR.name()))
                {
                    merchantDetailsVO.setIconColor((String) merchantTemplateInformation.get(TemplatePreference.ICON_VECTOR_COLOR.name()));
                }
            }

            if(merchantTemplateInformation.containsKey(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name()) && merchantTemplateInformation.get(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name())!=null && merchantTemplateInformation.containsKey(TemplatePreference.MERCHANTLOGONAME.name()) && merchantTemplateInformation.get(TemplatePreference.MERCHANTLOGONAME.name())!=null)
            {
                log.debug(merchantTemplateInformation.get(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name()));
                byte[] bytes=null;

                bytes = (byte[]) merchantTemplateInformation.get(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name());

                String filePath=merchant_uploaded_path+"/merchant_"+merchantDetailsVO.getMemberId()+"."+getFileExtension((String) merchantTemplateInformation.get(TemplatePreference.MERCHANTLOGONAME.name()));
                File file=new File(filePath);
                merchantDetailsVO.setMerchantLogoName("merchant_"+merchantDetailsVO.getMemberId()+"."+getFileExtension((String) merchantTemplateInformation.get(TemplatePreference.MERCHANTLOGONAME.name())));
                transactionLogger.debug("Bytes:::"+filePath);
                if(!file.exists())
                {
                    try
                    {
                        fileHandlingUtil.createFileFromBytes(bytes, filePath);
                    }
                    catch (PZTechnicalViolationException e)
                    {
                        log.error("Exception while creating file::", e);
                    }
                }
            }
        }
    }

    public void setAllTemplateInformationRelatedToPartner(PartnerDetailsVO partnerDetailsVO,String version) throws PZDBViolationException
    {
        PartnerManager partnerManager = new PartnerManager();
        Map<String,Object> partnerTemplateInformation= partnerManager.getPartnerSavedMemberTemplateDetails(partnerDetailsVO.getPartnerId());


        if(partnerTemplateInformation!=null && partnerTemplateInformation.size()>0)
        {
            //String sessionVersion = (String)session.getAttribute("version");

            if(functions.isValueNull(version) && version.equals("2"))
            {
                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setHeadPanelFont_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setBodyPanelFont_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()))
                {
                    partnerDetailsVO.setPanelHeading_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()))
                {
                    partnerDetailsVO.setPanelBody_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()))
                {
                    partnerDetailsVO.setTemplateBackGround_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()))
                {
                    partnerDetailsVO.setBodyBgColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setBodyFgColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setNavigationFontColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()))
                {
                    partnerDetailsVO.setTextboxColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()))
                {
                    partnerDetailsVO.setIconColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()))
                {
                    partnerDetailsVO.setTimerColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.name()));
                }
                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setNEW_CHECKOUT_FOOTER_FONT_COLOR((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()));
                }
                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                {
                    partnerDetailsVO.setNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()))
                {
                    partnerDetailsVO.setBoxShadow((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.name()));
                }
            }
            else
            {
                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name()))
                {
                    partnerDetailsVO.setHeadPanelFont_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.AHEADPANELFONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name()))
                {
                    partnerDetailsVO.setBodyPanelFont_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.ABODYPANELFONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.APANELHEADING_COLOR.name()))
                {
                    partnerDetailsVO.setPanelHeading_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.APANELHEADING_COLOR.name()));
                }


                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.APANELBODY_COLOR.name()))
                {
                    partnerDetailsVO.setPanelBody_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.APANELBODY_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name()))
                {
                    partnerDetailsVO.setTemplateBackGround_color((String) partnerTemplateInformation.get(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name()))
                {
                    partnerDetailsVO.setBodyBgColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name()))
                {
                    partnerDetailsVO.setBodyFgColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setNavigationFontColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.ATEXTBOX_COLOR.name()))
                {
                    partnerDetailsVO.setTextboxColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.ATEXTBOX_COLOR.name()));
                }

                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.AICON_VECTOR_COLOR.name()))
                {
                    partnerDetailsVO.setIconColor((String) partnerTemplateInformation.get(PartnerTemplatePreference.AICON_VECTOR_COLOR.name()));
                }
                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()))
                {
                    partnerDetailsVO.setNEW_CHECKOUT_FOOTER_FONT_COLOR((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.name()));
                }
                if (partnerTemplateInformation.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()))
                {
                    partnerDetailsVO.setNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR((String) partnerTemplateInformation.get(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.name()));
                }
            }
        }
    }

    private String getFileExtension(String filename)
    {
        String extension=null;
        if(filename!=null)
        {
            int ext=filename.trim().lastIndexOf(".");
            extension=filename.substring(ext+1);
        }
        return extension;
    }

    protected MerchantDetailsVO getMerchantConfigDetailsByToid(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberDetails(toid);

        return merchantDetailsVO;
    }

    private String validateMarketPlaceStandardProcess(CommonValidatorVO commonValidatorVO,String actionName) throws PZDBViolationException, PZConstraintViolationException
    {
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();

        String error="";
        float totalamount=0;

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        MerchantDAO merchantDAO=new MerchantDAO();
        PartnerDAO partnerDAO=new PartnerDAO();
        MerchantDetailsVO merchantDetailsVO=null;

        List<MarketPlaceVO> marketPlaceVOList=commonValidatorVO.getMarketPlaceVOList();
        List<MarketPlaceVO> marketPlaceVOListNew=new ArrayList<>();
        if(marketPlaceVOList == null || marketPlaceVOList.size()==0)
        {
            error = "Market Place Parameters are Incomplete";
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PARAMETERS_MP);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_PARAMETERS_MP);
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
        }
        else
        {
            try
            {
                for (int i = 0; i < marketPlaceVOList.size(); i++)
                {
                    marketPlaceVO = marketPlaceVOList.get(i);
                    //prepare list for general Mandatory parameters
                    List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
                    inputMandatoryFieldsList.addAll(inputValiDatorUtils.getMarketPlaceMandatoryParams());
                    inputValidator.InputValidations(commonValidatorVO, marketPlaceVO, inputMandatoryFieldsList, errorList, false);
                    error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);

                    //prepare list for general Optional parameters
                    List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
                    inputOptionalFieldsList.addAll(inputValiDatorUtils.getMarketPlaceOptionalParams());
                    ValidationErrorList errorList1 = new ValidationErrorList();
                    inputValidator.InputValidations(commonValidatorVO, marketPlaceVO, inputOptionalFieldsList, errorList1, true);
                    error = error + inputValiDatorUtils.getError(errorList1, inputOptionalFieldsList, actionName);

                    if (functions.isValueNull(error))
                    {
                        return error;
                    }

                    merchantDetailsVO = merchantDAO.getMemberDetails(marketPlaceVO.getMemberid());
                    if (merchantDetailsVO == null)
                    {
                        error = "Invalid MemberId OR MemberId is Misconfigured.";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID_INVALID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID_MP);
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return error;
                    }
                    if (!commonValidatorVO.getMerchantDetailsVO().getPartnerId().equalsIgnoreCase(merchantDetailsVO.getPartnerId()) && !partnerDAO.isPartnerMemberMapped(marketPlaceVO.getMemberid(), commonValidatorVO.getMerchantDetailsVO().getPartnerId()))
                    {
                        error = "Invalid MemberId OR MemberId is Misconfigured.";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID_INVALID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID_MP);
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return error;
                    }
                    marketPlaceVO.setMerchantDetailsVO(merchantDetailsVO);
                    if (!merchantDetailsVO.getActivation().equals("Y"))
                    {
                        error = "The " + marketPlaceVO.getMemberid() + " Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any incomplete formality from the Merchant Side. Please contact support so that they can activate your account.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK_MP.toString(), ErrorType.VALIDATION.toString());
                        //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                        PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    totalamount = totalamount + Float.parseFloat(marketPlaceVO.getAmount());
                    marketPlaceVOListNew.add(marketPlaceVO);
                }
                commonValidatorVO.setMarketPlaceVOList(marketPlaceVOListNew);
                if (!String.format("%.2f", totalamount).equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getAmount()))
                {
                    error = "Amount Mismatch.";
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOTAL_AMOUNT_MP);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTAL_AMOUNT_MP);
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return error;
                }
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("validateMarketPlaceStandardProcess() SystemError--->",systemError);
                PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"validateMarketPlaceStandardProcess()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
            }
        }
        return error;
    }


    public CommonValidatorVO performVTCheckoutValidations(CommonValidatorVO commonValidatorVO, HttpSession session)throws PZConstraintViolationException,PZDBViolationException
    {
        String error = "";
        Functions functions=new Functions();
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        TerminalManager terminalManager = new TerminalManager();

        HashMap paymentCardMap = new HashMap();
        HashMap terminalMap = new HashMap();
        HashMap currencyListMap = new HashMap();

        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();
        String toid=merchantDetailsVO.getMemberId();
        String currency = "";

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())){
            currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        }

        if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            error = ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID.toString(),ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());
            if(null==merchantDetailsVO)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorReason(errorCodeVO.getErrorDescription());
                commonValidatorVO.setErrorMsg(errorCodeVO.getErrorDescription());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(),ErrorType.VALIDATION.toString());
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            if(merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                commonValidatorVO.setErrorMsg(error);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(),ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            partnerDetailsVO.setPartnerId(merchantDetailsVO.getPartnerId());
            partnerDetailsVO.setPartnertemplate(merchantDetailsVO.getPartnertemplate());
            setAllTemplateInformationRelatedToMerchant(merchantDetailsVO,commonValidatorVO.getVersion());
            setAllTemplateInformationRelatedToPartner(partnerDetailsVO,commonValidatorVO.getVersion());
            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            getPaymentPageTemplateDetails(commonValidatorVO, session);
            //PaymentType and CardType Validation
//            error = validateStandardProcessStep0(commonValidatorVO, "STDKIT");
            if(!functions.isEmptyOrNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }

            if(!("t").equalsIgnoreCase(commonValidatorVO.getIsProcessed()))
            {
                boolean currencyMap=false;
                boolean paymentMap=false;
                boolean paymentcardMap=false;
                boolean terminalIdMap=false;

                if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y") && functions.isValueNull(currency))
                {
                    //Fetch all the payment type and cart type basis of memberid and/or currency
                    paymentCardMap = terminalManager.getListofPaymentandCardtypeByToid(toid, currency);
                    terminalMap = terminalManager.getPaymdeCardTerminalVOFromMemberId(toid, currency);
                    if(terminalMap.size() > 0 || terminalMap.isEmpty()){
                        paymentCardMap = terminalManager.getPaymdeCardTerminalVOFromMemberId(toid, "ALL");
                    }
                    log.debug("All PaymentType and CardType in CommonInputValidator on base of toid and currency----" + paymentCardMap);
                    currencyMap = true;
                }
                else
                {
                    paymentCardMap = terminalManager.getListofPaymentandCardtypeByToid(toid, currency);
                    terminalMap = terminalManager.getPaymdeCardTerminalVOFromMemberId(toid, currency);
                    currencyListMap = terminalManager.getListofPaymentandCurrencyMapByToid(toid);
                    log.debug("All PaymentType and CardType in CommonInputValidator on base of toid and currency----" + paymentCardMap);
                    currencyMap = true;
                }

                //Currency Validation
                if(paymentCardMap.isEmpty() || terminalMap.isEmpty())
                {
                    //errorCodeVO = new ErrorCodeVO();
                    if(currencyMap)
                    {
                        error = ErrorMessages.INVALID_CURRENCY;
                        commonValidatorVO.setErrorMsg(error);
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorReason(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_CURRENCY.toString(), ErrorType.VALIDATION.toString());
                    }
                    else if(paymentMap)
                    {
                        error = ErrorMessages.INVALID_PAYMENT_MODE;
                        commonValidatorVO.setErrorMsg(error);
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PAYMENT_MODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_MODE);
                        errorCodeVO.setErrorReason(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMENT_MODE.toString(),ErrorType.VALIDATION.toString());
                    }
                    else if(terminalIdMap)
                    {
                        error = ErrorMessages.INVALID_TERMINALID;
                        commonValidatorVO.setErrorMsg(error);
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TERMINALID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TERMINALID);
                        errorCodeVO.setErrorReason(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TERMINALID.toString(),ErrorType.VALIDATION.toString());
                    }
                    else if(paymentcardMap)
                    {
                        error = ErrorMessages.INVALID_PAYMODE_CARDTYPE;
                        commonValidatorVO.setErrorMsg(error);
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PAYMODE_CARDTYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMODE_CARDTYPE);
                        errorCodeVO.setErrorReason(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMODE_CARDTYPE.toString(),ErrorType.VALIDATION.toString());
                    }

                    if(commonValidatorVO.getErrorCodeListVO()!=null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return commonValidatorVO;
                }

                if(terminalMap.isEmpty())
                {
                    log.debug("terminalMap:::"+terminalIdMap);
                    errorCodeVO = new ErrorCodeVO();
                    error = ErrorMessages.INVALID_PAYMODE_CARDTYPE;
                    commonValidatorVO.setErrorMsg(error);

                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY);
                    /*errorCodeVO.setErrorName(ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY);
                    errorCodeVO.setErrorReason(error);*/
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY.toString(),ErrorType.SYSCHECK.toString());
                    if(commonValidatorVO.getErrorCodeListVO()!=null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return commonValidatorVO;
                }
                commonValidatorVO.setMapOfPaymentCardType(paymentCardMap);
                commonValidatorVO.setTerminalMap(terminalMap);
                commonValidatorVO.setCurrencyListMap(currencyListMap);
                transactionLogger.error("terminalMap---" + terminalMap);
            }
            commonValidatorVO.setTerminalMap(terminalMap);

        }

        //IP Whitelist check
        if("Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListed()))
        {
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_IPWHITELIST_CHECK.toString(),ErrorType.VALIDATION.toString());
                //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any incomplete formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK.toString(),ErrorType.VALIDATION.toString());
            //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        //setting Blank Address if Address Validation NA
        if(merchantDetailsVO.getAddressValidation().equalsIgnoreCase("NA"))
        {
            commonValidatorVO.getAddressDetailsVO().setCountry("");
            commonValidatorVO.getAddressDetailsVO().setCity("");
            commonValidatorVO.getAddressDetailsVO().setStreet("");
            commonValidatorVO.getAddressDetailsVO().setZipCode("");
            commonValidatorVO.getAddressDetailsVO().setState("");
            commonValidatorVO.getAddressDetailsVO().setPhone("");
            commonValidatorVO.getAddressDetailsVO().setTelnocc("");
        }

        error = validateStandardProcessStep1(commonValidatorVO, "STDKIT");

        transactionLogger.debug("MerchantLogoName from commonValidatorVO.merchantDetailsVO 2---" + commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }
        //specific parameter
        error = validateStandardKitSpecificParametersStep1(commonValidatorVO);
        transactionLogger.debug("MerchantLogoName from commonValidatorVO.merchantDetailsVO 3---" + commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

        if(!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error.trim());
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName,ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        return commonValidatorVO;
    }



    public String validateCVVForTokenTransaction(CommonValidatorVO commonValidatorVO)
    {
        return null;
    }
}