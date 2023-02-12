/*
package practice;

import com.directi.pg.core.jpbanktransfer.JPBankTransfer_Utils;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.zotapaygateway.ZotapayUtils;

import javax.servlet.RequestDispatcher;
import java.util.HashMap;

*/
/**
 * Created by Admin on 4/22/2020.
 *//*

public class p5
{
  if (String.valueOf(PaymentModeEnum.BankTransfer.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.JPBANK.getValue()).equals(terminalVO.getCardTypeId()))
    {
        transactionLogger.error("Inside JP Bank Transfer ---------------------");
        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
        if (error != null && !error.equals(""))
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            errorCodeVO.setErrorReason(error);
            errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
            req.setAttribute("transDetails", commonValidatorVO);
            session.setAttribute("ctoken", ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }


        paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
        if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
        {
            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
        }
        JPBankTransfer_Utils jpBankTransferUtils = new JPBankTransfer_Utils();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = jpBankTransferUtils.getCommRequestFromUtils(commonValidatorVO);
        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
        if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
        {
            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
            String html = zotapayUtils.getRedirectForm(transRespDetails);
            transactionLogger.error("Html in processAutoRedirect -------" + html);
            req.setAttribute("responceStatus", "pending");
            pWriter.println(html);
            return;
        }else
        {
            req.setAttribute("responceStatus", "fail");
            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
            mailtransactionStatus = transRespDetails.getStatus().trim();
        }
        req.setAttribute("transDetail", commonValidatorVO);

        req.setAttribute("displayName", billingDiscriptor);
        req.setAttribute("ctoken", ctoken);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
        requestDispatcher.forward(req, res);
        return;
    }
}
*/
