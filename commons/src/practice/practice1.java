/*package practice;

import com.directi.pg.core.valueObjects.JPBankTransferVO;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.jpbanktransfer.JPBankTransferUtils;

import javax.servlet.RequestDispatcher;

public class practice1

{
    else if (String.valueOf(PaymentModeEnum.BankTransfer.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.JPBANK.getValue()).equals(terminalVO.getCardTypeId()))
    {
        transactionLogger.error("Inside JP BankTransfer ---------------------");

        merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO); // paymentManager is object | insertAuthStartedTransactionEntryForCommon is method inside paymentManager | (commonValidatorVO, trackingid, auditTrailVO) are parameters

        if (functions.isValueNull(.getInvoiceId())) // if not null then commonValidatorVOit will give invoiceId from commonValidatorVO
        {
            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId()); // processInvoice have three parameters
        }
        // PayBoutiqueUtils payBoutiqueUtils=new PayBoutiqueUtils();
        CommRequestVO commRequestVO = new CommRequestVO();
        CommResponseVO transRespDetails = null;
        commRequestVO = JPBankTransferUtils.getCommRequestFromUtils(commonValidatorVO);
        transRespDetails = (JPBankTransferVO) pg.processSale(trackingid, commRequestVO);
        if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
        {
            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
            //  String html = payBoutiqueUtils.getRedirectForm(trackingid,transRespDetails);
            req.setAttribute("responseStatus", "pending");

            //  req.setAttribute("transRespDetails", transRespDetails);

            //  pWriter.println(html);
        }
        else
        {
            req.setAttribute("responseStatus", "fail");
            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
            mailtransactionStatus = transRespDetails.getStatus().trim();
        }

        req.setAttribute("transDetail", commonValidatorVO);
        req.setAttribute("transRespDetails", transRespDetails);
        req.setAttribute("displayName", billingDiscriptor);
        req.setAttribute("ctoken", ctoken);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout_JPBank.jsp?ctoken=" + ctoken);
        requestDispatcher.forward(req, res);
        return;
    }

}
*/