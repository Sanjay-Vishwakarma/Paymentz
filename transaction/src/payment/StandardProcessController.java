package payment;

import com.directi.pg.DefaultUser;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.PartnerManager;
import com.manager.PaymentManager;
import com.manager.helper.TransactionHelper;
import com.payment.AbstractPaymentProcess;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.PaymentProcessFactory;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;
import payment.util.ReadRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Trupti
 * Date: 8/21/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardProcessController extends HttpServlet
{
    private static Logger log = new Logger(StandardProcessController.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(StandardProcessController.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("entering in StandardProcessController----");
        String error="";
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        Functions functions=new Functions();
        CommonInputValidator commonInputValidator=new CommonInputValidator();
        TransactionHelper transactionHelper=new TransactionHelper();
        PartnerManager partnerManager=new PartnerManager();
        PaymentManager paymentManager=new PaymentManager();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();

        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String httpProtocol=req.getScheme();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String hostName=httpProtocol+"://"+remoteAddr;
        String memberId = null;
        int paymentType=0;
        int cardType=0;

        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        try
        {
            standardKitValidatorVO = ReadRequest.getSTDProcessControllerRequestParametersForSale(req);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);

            if(standardKitValidatorVO==null)
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("StandardProcessController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_PAYMENT_DETAILS,PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            //Member id validation for partner
            memberId=standardKitValidatorVO.getMerchantDetailsVO().getMemberId();

            if(!functions.isValueNull(memberId) || !functions.isNumericVal(memberId) || memberId.length()>10 || !ESAPI.validator().isValidInput("memberId",memberId,"Numbers",20,false))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("StandardProcessController.class","doPost()",null,"Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            User user = new DefaultUser(memberId);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session = req.getSession();
            session.setAttribute("ctoken",ctoken);
            req.setAttribute("ctoken",ctoken);

            //validation
           // standardKitValidatorVO =  commonInputValidator.performStandardProcessStep1Validations(standardKitValidatorVO);
            if(!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("StandardProcessController.class","doPost()",null,"Transaction",standardKitValidatorVO.getErrorMsg(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            //System Check
            standardKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(standardKitValidatorVO);

            standardKitValidatorVO =  commonInputValidator.validateResfield1(standardKitValidatorVO, "STDKIT");
            if(!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("StandardProcessController.class","doPost()",null,"Transaction",standardKitValidatorVO.getErrorMsg(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            paymentType = Integer.parseInt(standardKitValidatorVO.getPaymentType());
            cardType = Integer.parseInt(standardKitValidatorVO.getCardType());
            String fromtype = standardKitValidatorVO.getTransDetailsVO().getFromtype();
            if(fromtype==null)
            {
                PZExceptionHandler.raiseGenericViolationException("StandardProcessController.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_FROMTYPE,null, "", null);
            }
            log.debug(PaymentModeEnum.VOUCHERS_PAYMODE.getValue()+" - "+paymentType+" -- "+cardType+" - "+CardTypeEnum.PAYSAFECARD_CARDTYPE.getValue());
            partnerManager.getPartnerDetailFromMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(),standardKitValidatorVO);
            standardKitValidatorVO.getTransDetailsVO().setTotype(standardKitValidatorVO.getPartnerName());
            int trackingId= paymentManager.insertBegunTransactionEntry(standardKitValidatorVO,header);
            if(trackingId!=0)
            {
                standardKitValidatorVO.setTrackingid(String.valueOf(trackingId));
            }
            else
            {
                PZExceptionHandler.raiseGenericViolationException("StandardProcessController.class","doPost()",null,"Transaction",ErrorMessages.DB_COMMUNICATION,null,null,null);
            }
            if((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal()==paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal()==paymentType) && CardTypeEnum.CUP_CARDTYPE.ordinal()==cardType)
            {
                //redirect to cup process
                session.setAttribute("childfile","cupPayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }

            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymentType && CardTypeEnum.INPAY_CARDTYPE.ordinal()==cardType)
            {   //NET_BANKING
                session.setAttribute("childfile","inpayPayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymentType && CardTypeEnum.PAYSEC_CARDTYPE.ordinal()==cardType)
            {
                //NET_BANKING
                session.setAttribute("childfile","inpayPayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.VOUCHERS_PAYMODE.ordinal()==paymentType && CardTypeEnum.PAYSAFECARD_CARDTYPE.ordinal()==cardType)
            {
                session.setAttribute("childfile","paysafecardPayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.POSTPAID_CARD_PAYMODE.ordinal()==paymentType && CardTypeEnum.MULTIBANCO.ordinal()==cardType)
            {
                session.setAttribute("childfile","apcopayspecificfields.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymentType && CardTypeEnum.SOFORT_CARDTYPE.ordinal()==cardType)
            {
                session.setAttribute("childfile","sofortPayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));

            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymentType && CardTypeEnum.IDEAL_CARDTYPE.ordinal()==cardType)
            {
                session.setAttribute("childfile","idealPayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));

            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymentType && CardTypeEnum.DIRECT_DEBIT.ordinal()==cardType)
            {
                session.setAttribute("childfile","p4Payment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.SEPA.ordinal()==paymentType && CardTypeEnum.SEPA_EXPRESS.ordinal()==cardType)
            {
                session.setAttribute("childfile","p4Payment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.SEPA.ordinal()==paymentType && CardTypeEnum.DIRECT_DEBIT.ordinal()==cardType)
            {
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                session.setAttribute("childfile", "p4SepaPayment.jsp");
            }

            else if(PaymentModeEnum.ACH.ordinal()==paymentType && CardTypeEnum.ACH.ordinal()==cardType)
            {
                session.setAttribute("childfile","paymitcopayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            else if(PaymentModeEnum.CHK.ordinal()==paymentType && CardTypeEnum.CHK.ordinal()==cardType)
            {
                session.setAttribute("childfile","paymitcopayment.jsp");
                req.setAttribute("transDetails",standardKitValidatorVO);
                req.setAttribute("ctoken",session.getAttribute("ctoken"));
            }
            /*else if(PaymentModeEnum.SEPA_DIRECT_DEBIT.ordinal()==paymentType && CardTypeEnum.P4_CARDTYPE.ordinal()==cardType)
            {
                if (!functions.isValueNull(standardKitValidatorVO.getReserveField2()))
                {
                    session.setAttribute("childfile", "p4SepaPayment.jsp");
                    req.setAttribute("transDetails", standardKitValidatorVO);
                    req.setAttribute("ctoken", session.getAttribute("ctoken"));
                }
                else
                {
                    session.setAttribute("childfile", "creditcardpayment.jsp");
                }
            }*/
            else if(PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal()==paymentType && (CardTypeEnum.VISA_CARDTYPE.ordinal()==cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal()==cardType || CardTypeEnum.DINER_CARDTYPE.ordinal()==cardType || CardTypeEnum.AMEX_CARDTYPE.ordinal()==cardType ||  CardTypeEnum.DISC_CARDTYPE.ordinal()==cardType || CardTypeEnum.JCB.ordinal()==cardType ||  CardTypeEnum.RUPAY.ordinal()==cardType ))
            {
                if(Functions.checkProcessGateways(fromtype))
                {
                    AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()+""));
                    String filename = paymentProcess.getSpecificVirtualTerminalJSP();
                    req.setAttribute("filename",filename);
                    session.setAttribute("filename",filename);
                    if(ReitumuBankSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype))
                    {
                        session.setAttribute("childfile","reitumuCreditPage.jsp");
                    }
                    else
                    {
                        session.setAttribute("childfile","creditcardpayment.jsp");
                    }
                    req.setAttribute("transDetails",standardKitValidatorVO);
                    req.setAttribute("ctoken",session.getAttribute("ctoken"));
                }
                else
                {
                    PZExceptionHandler.raiseGenericViolationException("StandardProcessController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_GATEWYAY,null,null,null);
                }
            }
            else
            {
                PZExceptionHandler.raiseGenericViolationException("StandardProcessController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_PAYMODE_CARDTYPE,null,null,null);
            }
            if(functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()))
            {
                session.setAttribute("merchantLogoName",standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
                log.debug("Payemetz maerchant Logo..."+standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }
            if("Y".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getTemplate()))
            {
                session.setAttribute("panelheading_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelHeading_color());
                session.setAttribute("headpanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getHeadPanelFont_color());
                session.setAttribute("bodypanelfont_color", standardKitValidatorVO.getMerchantDetailsVO().getBodyPanelFont_color());
                session.setAttribute("panelbody_color", standardKitValidatorVO.getMerchantDetailsVO().getPanelBody_color());
                session.setAttribute("mainbackgroundcolor", standardKitValidatorVO.getMerchantDetailsVO().getTemplateBackGround_color());
            }
            RequestDispatcher rd = req.getRequestDispatcher("/commonPayment.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            log.debug(PaymentModeEnum.VOUCHERS_PAYMODE.getValue()+" - "+paymentType+" -- "+cardType+" - "+CardTypeEnum.PAYSAFECARD_CARDTYPE.getValue());
            log.error("-------PZConstraintViolationException in StandardProcessController------",cve);
            transactionLogger.error("PZConstraintViolationException occured ",cve);
            error = errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO());
            transactionLogger.error("ConstraintViolation in Payprocess----"+error);
            PZExceptionHandler.handleCVEException(cve,standardKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error",error);
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch(PZDBViolationException dve)
        {
            log.error("----PZDBViolationException in StandardProcessController-----",dve);
            error = "Internal Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleDBCVEException(dve,standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error +" : "+dve.getPzdbConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error +" : "+dve.getPzGenericConstraint().getMessage());
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch(PZTechnicalViolationException tve)
        {
            log.error("----PZTechnicalViolationException in StandardProcessController-----",tve);
            error = "Technical Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleTechicalCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error",error +" : "+tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error+" : "+tve.getPzGenericConstraint().getMessage());
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch(PZGenericConstraintViolationException tve)
        {
            log.error("----PZGenericConstraintViolationException in StandardProcessController-----",tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleGenericCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error",error+" : "+tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error+" : "+tve.getPzGenericConstraint().getMessage());
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
    }
}