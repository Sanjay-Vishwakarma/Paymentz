package payment;

import com.directi.pg.DefaultUser;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.PartnerManager;
import com.manager.PaymentManager;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.vo.TokenRequestVO;
import com.manager.vo.TokenResponseVO;
import com.manager.vo.VTResponseVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.PaymentProcessFactory;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.transaction.utils.TransactionCoreUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;
import payment.util.ReadRequest;
import payment.util.SingleCallPaymentDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 8/21/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayProcess3DController extends HttpServlet
{
    //private static Logger log = new Logger(PayProcess3DController.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayProcess3DController.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        PrintWriter pWriter = res.getWriter();

        String error="";
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        Functions functions=new Functions();
        CommonInputValidator commonInputValidator=new CommonInputValidator();
        SingleCallPaymentDAO singleCallPaymentDAO=new SingleCallPaymentDAO();
        //TransactionHelper transactionHelper=new TransactionHelper();
        PartnerManager partnerManager=new PartnerManager();
        PaymentManager paymentManager=new PaymentManager();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        VTResponseVO vtResponseVO = new VTResponseVO();
        //TransactionUtils transactionUtils = new TransactionUtils();
        TransactionCoreUtils transactionUtils = new TransactionCoreUtils();

        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String toid=null;
        String totype=null;
        int paymentType=0;
        int cardType=0;
        String token="";
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        try
        {
            //standardKitValidatorVO = ReadRequest.getSTDKitRequestParametersForSale(req);
            standardKitValidatorVO = ReadRequest.getSTDKit3DRequestParametersForSale(req,ctoken);

            if(standardKitValidatorVO==null)
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("PayProcess3DController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_PAYMENT_DETAILS,PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            //toid validation for partner
            toid=standardKitValidatorVO.getMerchantDetailsVO().getMemberId();
            totype=standardKitValidatorVO.getTransDetailsVO().getTotype();

            if(!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("PayProcess3DController.class","doPost()",null,"Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            if(!functions.isValueNull(totype) || totype.length()>25 || !ESAPI.validator().isValidInput("totype",totype,"StrictString",25,false))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOTYPE);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("PayProcess3DController.java","doPost()",null,"Transaction",ErrorMessages.INVALID_TOTYPE,PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            if(!singleCallPaymentDAO.getPartnerCombinationCheck(toid,totype,standardKitValidatorVO))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_MEMBER_AUTHENTICATION);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("PayProcess3DController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_MEMBER_AUTHENTICATION,PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            User user = new DefaultUser(toid);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session = req.getSession();
            session.setAttribute("ctoken",ctoken);
            req.setAttribute("ctoken",ctoken);

            //validation
            //standardKitValidatorVO =  commonInputValidator.performStandardKitStep1Validations(standardKitValidatorVO);
            standardKitValidatorVO =  commonInputValidator.perform3DKitValidation(standardKitValidatorVO);

            if(!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("PayProcess3DController.class","doPost()",null,"Transaction",standardKitValidatorVO.getErrorMsg(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            //System Check
            //standardKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(standardKitValidatorVO);

            paymentType = Integer.parseInt(standardKitValidatorVO.getPaymentType());
            cardType = Integer.parseInt(standardKitValidatorVO.getCardType());
            String fromtype = standardKitValidatorVO.getTransDetailsVO().getFromtype();
            if(fromtype==null)
            {
                PZExceptionHandler.raiseGenericViolationException("PayProcess3DController.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_FROMTYPE,null, "", null);
            }

            partnerManager.getPartnerDetailFromMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(),standardKitValidatorVO);
            int trackingId= paymentManager.insertBegunTransactionEntry(standardKitValidatorVO,header);
            if(trackingId!=0)
            {
                standardKitValidatorVO.setTrackingid(String.valueOf(trackingId));
            }
            else
            {
                PZExceptionHandler.raiseGenericViolationException("PayProcess3DController.class","doPost()",null,"Transaction",ErrorMessages.DB_COMMUNICATION,null,null,null);
            }

            if (PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType && (CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == cardType || CardTypeEnum.DINER_CARDTYPE.ordinal() == cardType || CardTypeEnum.AMEX_CARDTYPE.ordinal() == cardType || CardTypeEnum.JCB.ordinal() == cardType || CardTypeEnum.MAESTRO.ordinal() == cardType || CardTypeEnum.INSTAPAYMENT.ordinal() == cardType || CardTypeEnum.DISC_CARDTYPE.ordinal() == cardType))
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
                        standardKitValidatorVO.getTransDetailsVO().setHeader(header);
                        vtResponseVO = transactionUtils.singleCall(standardKitValidatorVO,null);
                        if(vtResponseVO!=null)
                        {
                            transactionLogger.debug("is transaction successful::::" +vtResponseVO.getIsSuccessful());
                            if("pending3DConfirmation".equalsIgnoreCase(vtResponseVO.getStatus()))
                            {
                                String form = vtResponseVO.getHtmlFormValue();
                                transactionLogger.debug("form 3D controller---"+form);
                                pWriter.print(form);
                                return;
                            }
                            if("Y".equals(vtResponseVO.getIsSuccessful()) && "Y".equals(standardKitValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()))
                            {
                                transactionLogger.debug("Tokenisation active on merchant account");
                                TerminalManager terminalManager = new TerminalManager();
                                if(terminalManager.isTokenizationActiveOnTerminal(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), standardKitValidatorVO.getTerminalId()))
                                {
                                    transactionLogger.debug("Tokenisation active on merchant terminal");
                                    TokenManager tokenManager=new TokenManager();
                                    String strToken=tokenManager.isCardAvailable(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), standardKitValidatorVO.getCardDetailsVO().getCardNum());
                                    if(functions.isValueNull(strToken))
                                    {
                                        token=strToken;
                                        standardKitValidatorVO.setToken(token);
                                    }
                                    else
                                    {
                                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                        TokenResponseVO tokenResponseVO=null;
                                        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                        commCardDetailsVO.setCardType(String.valueOf(cardType));
                                        commCardDetailsVO.setCardNum(standardKitValidatorVO.getCardDetailsVO().getCardNum());
                                        commCardDetailsVO.setExpMonth(standardKitValidatorVO.getCardDetailsVO().getExpMonth());
                                        commCardDetailsVO.setExpYear(standardKitValidatorVO.getCardDetailsVO().getExpYear());
                                        commCardDetailsVO.setcVV(standardKitValidatorVO.getCardDetailsVO().getcVV());

                                        tokenRequestVO.setMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                                        tokenRequestVO.setTrackingId(String.valueOf(vtResponseVO.getTrackingId()));
                                        tokenRequestVO.setTerminalId(standardKitValidatorVO.getTerminalId());
                                        tokenRequestVO.setDescription(standardKitValidatorVO.getTransDetailsVO().getOrderId());

                                        tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                        tokenRequestVO.setAddressDetailsVO(standardKitValidatorVO.getAddressDetailsVO());

                                        tokenResponseVO=tokenManager.createToken(tokenRequestVO);
                                        if("success".equals(tokenResponseVO.getStatus()))
                                        {
                                            token=tokenResponseVO.getToken();
                                            standardKitValidatorVO.setToken(token);
                                        }
                                    }
                                }
                            }

                        }
                    }
                    req.setAttribute("transDetails",standardKitValidatorVO);
                    req.setAttribute("ctoken",session.getAttribute("ctoken"));
                }
                else
                {
                    PZExceptionHandler.raiseGenericViolationException("PayProcess3DController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_GATEWYAY,null,null,null);
                }
            }
            else
            {
                PZExceptionHandler.raiseGenericViolationException("PayProcess3DController.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMODE_CARDTYPE, null, null, null);
            }

            //transactionLogger.error("auto redirect flag 3d kit---"+standardKitValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            if(standardKitValidatorVO.getMerchantDetailsVO().getAutoRedirect().equalsIgnoreCase("Y")){
                try{
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionUtility.doAutoRedirect(standardKitValidatorVO, res, vtResponseVO.getStatusDescription(), vtResponseVO.getBillingDescriptor());
                }
                catch (SystemError systemError){
                    transactionLogger.error("SystemError while redirecting to redirect url", systemError);
                }
            }
            else
            {
                req.setAttribute("transDetail", standardKitValidatorVO);
                req.setAttribute("responceStatus", vtResponseVO.getStatusDescription());
                req.setAttribute("displayName", vtResponseVO.getBillingDescriptor());
                req.setAttribute("errorName", vtResponseVO.getErrorName());
                session.setAttribute("ctoken",ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken="+ctoken);
                requestDispatcher.forward(req,res);
                return;
            }
        }
        catch (PZConstraintViolationException cve){
            transactionLogger.error("PZConstraintViolationException:::::",cve);
            error = errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO());
            standardKitValidatorVO.setErrorMsg(error);
            try{
                TransactionUtility transactionUtility = new TransactionUtility();
                transactionUtility.doAutoRedirect(standardKitValidatorVO, res,"Fail","");
            }
            catch (SystemError systemError){
                transactionLogger.error("SystemError while redirecting to redirect url", systemError);
            }
        }
        catch(PZDBViolationException dve){
            transactionLogger.error("PZDBViolationException:::::", dve);
            error = "Internal error occured,please contact customer support for further help";
            standardKitValidatorVO.setErrorMsg(error);
            try
            {
                TransactionUtility transactionUtility = new TransactionUtility();
                transactionUtility.doAutoRedirect(standardKitValidatorVO, res,"Fail","");
            }
            catch (SystemError systemError){
                transactionLogger.error("SystemError while redirecting to redirect url", systemError);
            }

        }
        catch(PZTechnicalViolationException tve){
            transactionLogger.error("PZTechnicalViolationException:::::",tve);
            error = "Technical error occured,please contact customer support for further help";
            standardKitValidatorVO.setErrorMsg(error);
            try
            {
                TransactionUtility transactionUtility = new TransactionUtility();
                transactionUtility.doAutoRedirect(standardKitValidatorVO, res,"Fail","");
            }
            catch (SystemError systemError){
                transactionLogger.error("SystemError while redirecting to redirect url", systemError);
            }
        }
        catch(PZGenericConstraintViolationException tve){
            transactionLogger.error("PZGenericConstraintViolationException:::::",tve);
            error = "Generic error occured,please contact customer support for further help";
            standardKitValidatorVO.setErrorMsg(error);
            try
            {
                TransactionUtility transactionUtility = new TransactionUtility();
                transactionUtility.doAutoRedirect(standardKitValidatorVO, res,"Fail","");
            }
            catch (SystemError systemError){
                transactionLogger.error("SystemError while redirecting to redirect url", systemError);
            }
        }
    }
}