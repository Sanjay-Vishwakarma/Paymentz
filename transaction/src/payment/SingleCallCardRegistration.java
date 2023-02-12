package payment;
import com.directi.pg.*;
import com.directi.pg.AsyncNotificationService;
import com.manager.RestDirectTransactionManager;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
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
import java.security.NoSuchAlgorithmException;
/**
 * Created by Mahima on 12/12/2018.
 */
public class SingleCallCardRegistration extends HttpServlet
{
    private static Logger log = new Logger(CardRegistration.class.getName());
    private RestDirectTransactionManager restDirectTransactionManager = new RestDirectTransactionManager();
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallCardRegistration.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("entering in SingleCallCardRegistration----"+req);
        TransactionHelper transactionHelper = new TransactionHelper();
        Functions functions=new Functions();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        CommonInputValidator commonInputValidator=new CommonInputValidator();
        CommonValidatorVO commonValidatorVO = null;
        TokenRequestVO tokenRequestVO = null;
        TokenResponseVO tokenResponseVO=null;
        ErrorCodeVO errorCodeVO = null;
        String memberId=null;
        String totype=null;
        String key=null;
        String error = "";
        String errorName = "";

        try
        {
            commonValidatorVO = ReadRequest.getCardRegistrationDetails(req);
            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            memberId = merchantDetailsVO.getMemberId();
            totype = commonValidatorVO.getTransDetailsVO().getTotype();
            key = merchantDetailsVO.getKey();
            String checksumRequest = commonValidatorVO.getTransDetailsVO().getChecksum();
            String cardNum = commonValidatorVO.getCardDetailsVO().getCardNum();
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

            if (!functions.isValueNull(memberId) || !functions.isNumericVal(memberId) || memberId.length() > 10 || !ESAPI.validator().isValidInput("memberId", memberId, "Numbers", 10, false))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallCardRgistration.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }
            if (!functions.isValueNull(totype) || totype.length() > 30 || !ESAPI.validator().isValidInput("totype", totype, "SafeString", 30, false))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTYPE);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("CardRegistration.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOTYPE, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            User user = new DefaultUser(memberId);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session = req.getSession();
            session.setAttribute("ctoken", ctoken);
            req.setAttribute("ctoken", ctoken);

            String checksum = Functions.generateChecksumV1(memberId, totype, key, cardNum);
            /*if(!checksum.equals(functions.isValueNull(checksumRequest)))
            {
                commonValidatorVO.setErrorMsg(error);
               // errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                RequestDispatcher rd = req.getRequestDispatcher("/cardRegistration.jsp?");
                rd.forward(req, res);
                return;
            }*/

            error = commonInputValidator.validateCardRegistrationStep0(commonValidatorVO, "STDKIT");
            if (!functions.isEmptyOrNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                req.setAttribute("error", error);
                req.setAttribute("transDetail", commonValidatorVO);
                RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?");
                rd.forward(req, res);
                return;
            }
            merchantDetailsVO = commonInputValidator.getMerchantConfigDetails(memberId, totype);
            if (null == merchantDetailsVO)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorReason(errorCodeVO.getErrorDescription());
                commonValidatorVO.setErrorMsg(errorCodeVO.getErrorDescription());
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return;
            }

            if (merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                commonValidatorVO.setErrorMsg(error);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return;
            }
            TerminalVO terminalVO = new TerminalVO();
            commonValidatorVO.setTerminalVO(terminalVO);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            commonValidatorVO = transactionHelper.performCardRegistrationChecks(commonValidatorVO);
            if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
            {
                errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                req.setAttribute("error", error);
                req.setAttribute("transDetail", commonValidatorVO);
                RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                rd.forward(req, res);
                return;
            }
            commonValidatorVO.getPartnerDetailsVO().setIsMerchantRequiredForCardRegistration(merchantDetailsVO.getIsMerchantRequiredForCardRegistration());

            tokenRequestVO = new TokenRequestVO();
            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
            tokenRequestVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
            tokenRequestVO.setAddressDetailsVO(commonValidatorVO.getAddressDetailsVO());
            tokenRequestVO.setPartnerDetailsVO(commonValidatorVO.getPartnerDetailsVO());
            tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
            tokenRequestVO=processTokenRequest(commonValidatorVO);
            transactionLogger.error("tokenRequestVO.getNotificationUrl()::::"+tokenRequestVO.getNotificationUrl());
            tokenResponseVO = restDirectTransactionManager.processTokenGeneration(tokenRequestVO);

            if(functions.isValueNull(tokenRequestVO.getNotificationUrl()))
            {
                com.directi.pg.AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(commonValidatorVO,tokenResponseVO.getStatus(),tokenResponseVO.getRegistrationToken());
            }

            if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
            {
                req.setAttribute("tokenDetails", tokenResponseVO);
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("errorName", errorName);
                session.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCardTokenization.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else
            {
                TransactionUtility transactionUtility=new TransactionUtility();
                String method=commonValidatorVO.getTransDetailsVO().getRedirectMethod();
                transactionLogger.error("method--------->"+method);
                if("GET".equalsIgnoreCase(method))
                    transactionUtility.doCardRegistrationRedirectGet(commonValidatorVO,res,tokenResponseVO.getStatus(),tokenResponseVO.getRegistrationToken());
                else
                    transactionUtility.doCardRegistrationRedirect(commonValidatorVO,res,tokenResponseVO.getStatus(),tokenResponseVO.getRegistrationToken());
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("-------PZConstraintViolationException in CardRegistration------", cve);
            transactionLogger.error("PZConstraintViolationException occured ", cve);
            error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
            if (commonValidatorVO != null && functions.isValueNull(commonValidatorVO.getTrackingid()))
            {
                errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
            }
            PZExceptionHandler.handleCVEException(cve, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetail", commonValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }

        catch (PZGenericConstraintViolationException tve)
        {
            log.error("----PZGenericConstraintViolationException in CardRegistration-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleCVEException((PZConstraintViolationException) tve, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetail", commonValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (NoSuchAlgorithmException pzdb){
            log.error("----PZGenericConstraintViolationException in CardRegistration-----", pzdb);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            req.setAttribute("error", error);
            req.setAttribute("transDetails", commonValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (SystemError systemError)
        {
            log.error("----PZGenericConstraintViolationException in CardRegistration systemError-----", systemError);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            req.setAttribute("error", error);
            req.setAttribute("transDetails", commonValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
    }

    public TokenRequestVO processTokenRequest(CommonValidatorVO commonValidatorVO) throws PZGenericConstraintViolationException
    {
        TokenRequestVO tokenRequestVO=new TokenRequestVO();
        Functions functions=new Functions();
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        }
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPartnerId()))
        {
            tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
        }
        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
        {
            tokenRequestVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        }
        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()))
        {
            tokenRequestVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        }
        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getcVV()))
        {
            tokenRequestVO.setCvv(commonValidatorVO.getCardDetailsVO().getcVV());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
        {
            tokenRequestVO.setFirstName(commonValidatorVO.getAddressDetailsVO().getFirstname());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getStreet()))
        {
            tokenRequestVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCity()))
        {
            tokenRequestVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCountry()))
        {
            tokenRequestVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getZipCode()))
        {
            tokenRequestVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getState()))
        {
            tokenRequestVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()))
        {
            tokenRequestVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTelnocc()))
        {
            tokenRequestVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
        {
            tokenRequestVO.setCustEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
        {
            tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        }
        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectUrl()))
        {
            tokenRequestVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        }
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getIsMerchantRequiredForCardRegistration()))
        {
            tokenRequestVO.setIsMerchantCardRegistration(commonValidatorVO.getMerchantDetailsVO().getIsMerchantRequiredForCardRegistration());
        }
        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getLogin()))
        {
            tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
        }
        if(functions.isValueNull(commonValidatorVO.getCustomerId()))
        {
            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
            tokenRequestVO.setCardholderId(commonValidatorVO.getCustomerId());
        }
        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
        {
            tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
        }
        tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
        tokenRequestVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
        tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
        tokenRequestVO.setAddressDetailsVO(commonValidatorVO.getAddressDetailsVO());
        tokenRequestVO.setPartnerDetailsVO(commonValidatorVO.getPartnerDetailsVO());
        return tokenRequestVO;
    }
}