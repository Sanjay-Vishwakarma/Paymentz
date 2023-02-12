package payment;
import com.directi.pg.*;
import com.manager.PartnerManager;
import com.manager.PaymentManager;
import com.manager.TokenManager;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
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
import java.io.PrintWriter;

/**
 * Created by Admin on 6/2/2018.
 */
public class CardRegistration extends HttpServlet
{
    private static Logger log = new Logger(CardRegistration.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CardRegistration.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("entering in CardRegistration----");
        String error = "";
        String errorName = "";
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        Functions functions = new Functions();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        PartnerManager partnerManager = new PartnerManager();
        PaymentManager paymentManager = new PaymentManager();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String httpProtocol = req.getScheme();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String hostName = httpProtocol + "://" + remoteAddr;
        String memberId = null;
        String key=null;
        String totype = null;
        String customerId=null;

        PrintWriter pWriter = res.getWriter();
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");

        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        try
        {
            standardKitValidatorVO = ReadRequest.getSTDProcessControllerRequestParametersForCardRegistration(req);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);
            standardKitValidatorVO.setVersion("2");

            if (standardKitValidatorVO == null)
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REQUEST_NULL);
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("CardRegistration.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMENT_DETAILS, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            //Member id validation for partner
            memberId = standardKitValidatorVO.getMerchantDetailsVO().getMemberId();
            totype = standardKitValidatorVO.getTransDetailsVO().getTotype();
            customerId=standardKitValidatorVO.getCustomerId();

            if (!functions.isValueNull(memberId) || !functions.isNumericVal(memberId) || memberId.length() > 10 || !ESAPI.validator().isValidInput("memberId", memberId, "Numbers", 10, false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("CardRegistration.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }
            if (!functions.isValueNull(totype) || totype.length() > 30 || !ESAPI.validator().isValidInput("totype", totype, "SafeString", 30, false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTYPE);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOTYPE.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("CardRegistration.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOTYPE, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            /*if (functions.isValueNull(standardKitValidatorVO.getCustomerId()))
            {
                TokenManager tokenManager = new TokenManager();
                if (!tokenManager.isCardholderRegistered(memberId, customerId))
                {
                    ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_CUSTOMER);
                    errorCodeVO.setErrorName(ErrorName.SYS_UNAUTHORIZE_CUSTOMER);
                    error = errorCodeVO.getErrorDescription();
                    errorCodeListVO.addListOfError(errorCodeVO);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.SYS_UNAUTHORIZE_CUSTOMER.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("CardRegistration.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_CUSTOMERID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
                }
            }*/

            User user = new DefaultUser(memberId);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session = req.getSession();
            session.setAttribute("ctoken", ctoken);
            session.setAttribute("version","2");
            req.setAttribute("ctoken", ctoken);

            standardKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
            standardKitValidatorVO = commonInputValidator.performStandardProcessStep1ValidationsForCardRegistration(standardKitValidatorVO,session);

            if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                for (ErrorCodeVO errorCodeVO:standardKitValidatorVO.getErrorCodeListVO().getListOfError())
                {
                    transactionLogger.debug("ErrorCode:::"+errorCodeVO.getErrorDescription());
                }
                PZExceptionHandler.raiseConstraintViolationException("CardRegistration.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, standardKitValidatorVO.getErrorCodeListVO(), null, null);
            }

            if (functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()))
            {
                session.setAttribute("merchantLogoName", standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }
            String merchantSiteName = standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain();

            transactionLogger.error("merchantSiteName PayProcessControl---" + merchantSiteName);
            transactionLogger.error("X-Frame-Options from session---" + session.getAttribute("X-Frame-Options"));
            transactionLogger.error("REFERER from header---" + req.getHeader("referer"));

            res.setHeader("Cache-control", "no-store"); //HTTP 1.1
            res.setHeader("Pragma","no-cache"); //HTTP1.0
            res.setDateHeader("Expire", 0); //prevents caching at the proxy server
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            transactionLogger.error("final header value from session---" + session.getAttribute("X-Frame-Options"));

            transactionLogger.debug("is processed from pay process---" + req.getParameter("isProcessed"));
            if ((!functions.isValueNull(req.getParameter("isProcessed")) || req.getParameter("isProcessed").equalsIgnoreCase("f")))
            {
                transactionLogger.debug("inside is processed if trackingid got---");
                partnerManager.getPartnerDetailFromMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), standardKitValidatorVO);
                standardKitValidatorVO.getTransDetailsVO().setTotype(standardKitValidatorVO.getPartnerName());
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(memberId);
                auditTrailVO.setActionExecutorName("CustomerCheckout");
            }

            if ((functions.isValueNull(standardKitValidatorVO.getPaymentType()) && functions.isValueNull(standardKitValidatorVO.getCardType())) || functions.isValueNull(standardKitValidatorVO.getTerminalId()))
            {
                req.setAttribute("transDetails", standardKitValidatorVO);
                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                RequestDispatcher rd = req.getRequestDispatcher("/cardRegistration.jsp?ctoken=" + ctoken);
                rd.forward(req, res);
                return;
            }
            else
            {
                req.setAttribute("transDetails", standardKitValidatorVO);
                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                RequestDispatcher rd = req.getRequestDispatcher("/cardRegistration.jsp?ctoken=" + ctoken);
                rd.forward(req, res);
                return;
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("-------PZConstraintViolationException in CardRegistration------", cve);
            transactionLogger.error("PZConstraintViolationException occured ", cve);

            error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
            if (standardKitValidatorVO != null && functions.isValueNull(standardKitValidatorVO.getTrackingid()))
            {
                errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
                try
                {
                    paymentManager.updateDetailsTablewithErrorName(errorName, standardKitValidatorVO.getTrackingid());
                }
                catch (PZDBViolationException d)
                {
                    log.error("----PZDBViolationException in update with error name-----", d);
                }
            }
            PZExceptionHandler.handleCVEException(cve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dve)
        {
            log.error("----PZDBViolationException in CardRegistration-----", dve);
            error = "Internal Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleDBCVEException(dve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + dve.getPzdbConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + dve.getPzdbConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }

        catch (PZGenericConstraintViolationException tve)
        {
            log.error("----PZGenericConstraintViolationException in CardRegistration-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleGenericCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + tve.getPzGenericConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
    }
}

