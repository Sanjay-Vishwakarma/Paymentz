package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.MerchantDAO;
import com.manager.enums.TransReqRejectCheck;
import com.manager.vo.ManualRebillResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.utils.ManualTransactionUtils;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import payment.util.ReadRequest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


/**
 * Created by admin on 10/19/2015.
 */
public class SingleCallManualRebill extends PzServlet
{
    private static Logger log = new Logger(SingleCallGenericServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallManualRebill.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("inside SingleCallManualRebill");

        ManualRebillResponseVO manualRebillResponseVO = new ManualRebillResponseVO();
        ManualTransactionUtils manualTransactionUtils = new ManualTransactionUtils();
        TransactionUtility transactionError = new TransactionUtility();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        TransactionUtility transactionUtility = new TransactionUtility();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PrintWriter pWriter = res.getWriter();
        Transaction transaction = new Transaction();
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        Functions functions = new Functions();

        String remoteAddr = Functions.getIpAddress(req);
        ServletContext application=getServletContext();
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();

        String key="";
        String algoName="";
        String error = "";
        String status = "";
        String statusMsg = "";
        String mailtransactionstatus = "";
        String requesttype = req.getParameter("requesttype");
        commonValidatorVO = ReadRequest.getRequestParametersForRebill(req);

        String trackingId=commonValidatorVO.getTrackingid();
        log.debug("trackingID--->"+trackingId);
        String description=commonValidatorVO.getTransDetailsVO().getOrderId();
        String amount=commonValidatorVO.getTransDetailsVO().getAmount();
        String toId=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String merchantIp = commonValidatorVO.getAddressDetailsVO().getIp();
        String accountId = "";


        commonValidatorVO.getMerchantDetailsVO().setResponseType("String");
        commonValidatorVO.getMerchantDetailsVO().setResponseLength("Default");

        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;

        if(commonValidatorVO!=null)
        {
            try
            {
                if(!functions.isValueNull(toId) || !functions.isNumericVal(toId) || toId.length()>10 || !ESAPI.validator().isValidInput("toId",toId,"Numbers",10,false))
                {
                   // error = ErrorMessages.INVALID_TOID;
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                    //error = ErrorMessages.INVALID_TOID;
                    error = errorCodeVO.getErrorCode()+"- "+ errorCodeVO.getErrorDescription();
                    manualRebillResponseVO.setErrorMessage(error);
                    failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error, TransReqRejectCheck.VALIDATION_TOID.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter,manualRebillResponseVO,commonValidatorVO);
                    return;
                }

                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO == null || !functions.isValueNull(merchantDetailsVO.getMemberId()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID_INVALID);
                    error = errorCodeVO.getErrorCode()+"-"+ errorCodeVO.getErrorDescription();
                    //error = ErrorMessages.INVALID_TOID;
                    manualRebillResponseVO.setErrorMessage(error);
                    failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),error, TransReqRejectCheck.VALIDATION_TOID_INVALID.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter, manualRebillResponseVO, commonValidatorVO);
                    return;
                }
                else
                {
                    commonValidatorVO.getMerchantDetailsVO().setKey(merchantDetailsVO.getKey());
                }

                if (!functions.isValueNull(trackingId) || !functions.isNumericVal(trackingId) || trackingId.length() > 20 || !ESAPI.validator().isValidInput("trackingid", trackingId, "Numbers", 10, false))
                {
                    error = ErrorMessages.INVALID_TRACKINGID;
                    manualRebillResponseVO.setErrorMessage(error);
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter, manualRebillResponseVO, commonValidatorVO);
                    return;
                }

                accountId = transaction.getAccountID(trackingId);

                if (!functions.isValueNull(accountId))
                {
                    error = "Invalid trackingId, Or Tracking should not be empty.";
                    manualRebillResponseVO.setErrorMessage(error);
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter, manualRebillResponseVO, commonValidatorVO);
                    return;
                }

                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                String fromtype = account.getGateway();
                commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);
                if(!transactionUtility.isTrackingIdExistInDB(fromtype,trackingId,accountId,toId))
                {
                    error = "No Record found for given Details";
                    manualRebillResponseVO.setErrorMessage(error);
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter,manualRebillResponseVO,commonValidatorVO);
                    return;
                }

                commonValidatorVO.getTransDetailsVO().setAmount(amount);
                commonValidatorVO.getTransDetailsVO().setHeader(header);
                commonValidatorVO.getAddressDetailsVO().setIp(merchantIp);

                //Perform Manual Rebill Validation
                if(!functions.isValueNull(accountId))
                {
                    manualRebillResponseVO.setErrorMessage("No Record found for given Details");
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter,manualRebillResponseVO,commonValidatorVO);
                    return;
                }

                commonValidatorVO.getMerchantDetailsVO().setAccountId(accountId);
                commonValidatorVO = commonInputValidator.performManualRecurringValidation(commonValidatorVO);
                if (commonValidatorVO.getErrorMsg() != null && commonValidatorVO.getErrorMsg().trim().length() > 0)
                {
                    manualRebillResponseVO.setErrorMessage(commonValidatorVO.getErrorMsg());
                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter,manualRebillResponseVO,commonValidatorVO);
                    return;
                }
                else
                {
                    User user = new DefaultUser(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    ESAPI.authenticator().setCurrentUser(user);

                    manualRebillResponseVO = manualTransactionUtils.manualSingleCall(commonValidatorVO,application);
                    if("Y".equalsIgnoreCase(manualRebillResponseVO.getStatus()))
                    {
                        mailtransactionstatus = "successfull";
                    }
                    else
                    {
                        mailtransactionstatus = "Failed";
                    }

                    if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getEmailSent()))
                    {
                        Date date72 = new Date();
                        transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                        //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, manualRebillResponseVO.getTrackingId(), mailtransactionstatus, null,null);
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                    }

                    transactionError.calCheckSumAndWriteStatusForRebill(pWriter,manualRebillResponseVO,commonValidatorVO);
                    return;

                }

            }
            catch (PZConstraintViolationException cve)
            {
                log.error("PZConstraintViolationException in SingleCallManualRebill---",cve);
                transactionLogger.error("PZConstraintViolationException in SingleCallManualRebill---",cve);
                PZExceptionHandler.handleCVEException(cve,commonValidatorVO.getMerchantDetailsVO().getMemberId()+"IpAddress:"+remoteAddr,PZOperations.MANUAL_REBILL);
                status = "N";
                statusMsg = cve.getPzConstraint().getMessage();
                transactionError.calCheckSumAndWriteStatusForRebill(pWriter, "", description, amount, status, statusMsg, key, algoName, "");

                /*log.error("PZConstraintViolationException---",e);
                PZExceptionHandler.handleCVEException(e, toId, PZOperations.MANUAL_REBILL);
                transactionError.calCheckSumAndWriteStatusForRebill(pWriter, "", description, amount, "N", e.getPzConstraint().getMessage(), key, algoName,null);
                return;*/
            }
            catch (PZDBViolationException e)
            {
                log.error("db violation exception",e);
                transactionLogger.error("PZConstraintViolationException in SingleCallManualRebill---",e);
                PZExceptionHandler.handleDBCVEException(e, toId, PZOperations.MANUAL_REBILL);

                transactionError.calCheckSumAndWriteStatusForRebill(pWriter, String.valueOf(trackingId), description, amount, "N", "Internal Error Occured, Please contact Customer Support", key, algoName, "");
                return;
            }
        }
    }
}
