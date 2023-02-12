package com.manager;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.MerchantDAO;
import com.manager.utils.FraudDefenderUtil;
import com.manager.utils.TransactionUtil;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.alliedwalled.core.message.com._381808.service.Merchant;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.icard.ICardPaymentGateway;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Vivek on 6/3/2020.
 */
public class RestFraudDefenderManager
{
    private FraudDefenderLogger fraudDefenderLogger = new FraudDefenderLogger(RestFraudDefenderManager.class.getName());
    private TransactionUtil transactionUtil = new TransactionUtil();
    private Functions functions = new Functions();
    private FraudDefenderUtil fraudDefenderUtil = new FraudDefenderUtil();
    public DirectKitResponseVO processQueryFraudefender (CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO=null;
        TransactionManager transactionManager=new TransactionManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        TransactionDetailsVO transactionDetailsVO = null;
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        /*GatewayAccountService gatewayAccountService=new GatewayAccountService();
        String gateway=gatewayAccountService.getGateway(commonValidatorVO.getTransDetailsVO().getMerchant_id());
        fraudDefenderLogger.error("gateway---->"+gateway);*/

        String fraudId=transactionManager.insertFraudDefenderDetails(commonValidatorVO);
        commonValidatorVO.setFraudId(fraudId);
        /*if(ICardPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
            transactionDetailsVO = transactionManager.getDetailFraudDefenderForICard(commonValidatorVO);
        else*/
        Date d2=new Date();
            transactionDetailsVO = transactionManager.getDetailFraudDefender(commonValidatorVO);
        fraudDefenderLogger.error("Select Query Diff Time ###"+(new Date().getTime()-d2.getTime()));
        if(transactionDetailsVO!=null){
            MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(transactionDetailsVO.getToid());
            fraudDefenderLogger.error("Member Select Query Diff Time ###"+(new Date().getTime()-d2.getTime()));
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setParetnerId(merchantDetailsVO.getPartnerId());
            transactionManager.updateFraudTransactionDetails(commonValidatorVO,transactionDetailsVO);
            directKitResponseVO.setStatus("success");
            d2=new Date();
            String transactionStatus=transactionDetailsVO.getStatus();
            double remaningAmount = Double.parseDouble(transactionDetailsVO.getAmount()) - Double.parseDouble(transactionDetailsVO.getRefundAmount());
            fraudDefenderLogger.error("transactionStatus---->"+transactionStatus);
            fraudDefenderLogger.error("remaningAmount--->"+remaningAmount);
            //refund code
            if(transactionStatus.equalsIgnoreCase(PZTransactionStatus.CAPTURE_SUCCESS.toString()) || transactionStatus.equalsIgnoreCase(PZTransactionStatus.SETTLED.toString()) || transactionStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()))
            {
                String marketPlaceFlag = "";
                fraudDefenderLogger.error("remaningAmount--->"+remaningAmount);
                PZRefundRequest refundRequest = new PZRefundRequest();
                refundRequest.setMarketPlaceFlag(marketPlaceFlag);
                refundRequest.setAccountId(Integer.valueOf(transactionDetailsVO.getAccountId()));
                refundRequest.setMemberId(Integer.valueOf(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
                refundRequest.setRefundAmount(String.format("%.2f", remaningAmount));
                refundRequest.setCurrency(transactionDetailsVO.getCurrency());
                String refundreason = "Auto Refunded due to Fraud received from same Customer("+transactionDetailsVO.getTrackingid()+")";
                refundRequest.setRefundReason(refundreason);
                refundRequest.setTransactionStatus(transactionDetailsVO.getStatus());
                refundRequest.setTrackingId(Integer.valueOf(transactionDetailsVO.getTrackingid()));
                refundRequest.setReversedAmount(transactionDetailsVO.getRefundAmount());
                refundRequest.setCaptureAmount(transactionDetailsVO.getCaptureAmount());
                auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                auditTrailVO.setActionExecutorName("fraudefender");
                refundRequest.setAuditTrailVO(auditTrailVO);
                commonValidatorVO.setTrackingid(transactionDetailsVO.getTrackingid());

                Hashtable transHash = new Hashtable();
                transHash.put("status", transactionDetailsVO.getStatus());
                transHash.put("amount", transactionDetailsVO.getAmount());
                transHash.put("captureamount", transactionDetailsVO.getCaptureAmount());
                transHash.put("refundamount", transactionDetailsVO.getRefundAmount());
                transHash.put("currency", transactionDetailsVO.getCurrency());
                transHash.put("trackingid", transactionDetailsVO.getTrackingid());
                transHash.put("transactiondate", transactionDetailsVO.getTransactionTime());
                transHash.put("toid", transactionDetailsVO.getToid());
                transHash.put("firstname", transactionDetailsVO.getFirstName());
                transHash.put("lastname", transactionDetailsVO.getLastName());
                transHash.put("paymentid", transactionDetailsVO.getPaymentId());
                transHash.put("name", transactionDetailsVO.getName());
                transHash.put("emailaddr", transactionDetailsVO.getEmailaddr());
                transHash.put("ccnum",transactionDetailsVO.getCcnum());
                transHash.put("accountid",transactionDetailsVO.getAccountId());
                transHash.put("transactionstatus", transactionDetailsVO.getStatus());
                commonValidatorVO.setTransHash(transHash);

                errorCodeListVO=fraudDefenderUtil.commonCallTypeCheckRefundFlag(commonValidatorVO);
                if (errorCodeListVO == null && remaningAmount>0)
                {
                    transactionDetailsVO.setRefund_exists("true");
                    transactionDetailsVO.setRefundAmount(transactionDetailsVO.getAmount());
                }else if(remaningAmount == 0 )
                {
                    transactionDetailsVO.setRefund_exists("true");
                    transactionDetailsVO.setRefundAmount(transactionDetailsVO.getAmount());
                    transactionManager.updateQueryRefundFraudTransactionDetails(commonValidatorVO.getFraudId(), "Y", transactionDetailsVO.getRefundAmount());
                }
                else
                {
                    transactionDetailsVO.setRefund_exists("false");
                }

                AsyncCommonRefund asyncCommonRefund = AsyncCommonRefund.getInstance();
                asyncCommonRefund.asyncRefund(refundRequest,commonValidatorVO);
            }else
            {
                transactionDetailsVO.setRefund_exists("false");
            }

        }
        else
        {
            transactionDetailsVO=new TransactionDetailsVO();
            transactionManager.updateFraudTransactionDetails(commonValidatorVO, transactionDetailsVO);
            directKitResponseVO.setStatus("failed");
            commonValidatorVO.getErrorCodeListVO().addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, "NO Record Found."));
            PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", "NO Record Found.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);

        }
        commonValidatorVO.setTransactionDetailsVO( transactionDetailsVO);


        return directKitResponseVO;
    }
    public DirectKitResponseVO processFruadDefenderRefund(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, PZTechnicalViolationException
    {
        Transaction transaction = new Transaction();
        TransactionManager transactionManager=new TransactionManager();
        PaymentChecker paymentChecker = new PaymentChecker();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        TransactionEntry transactionEntry = new TransactionEntry();
        ActionEntry entry = new ActionEntry();
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        MarketPlaceVO marketPlaceVO=null;
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        RestCommonInputValidator commonInputValidator=new RestCommonInputValidator();
        String stat = "";
        String statMessage = "";
        String trackingId = commonValidatorVO.getTransDetailsVO().getClientTransactionId();
        String refundAmt = commonValidatorVO.getTransDetailsVO().getRefundAmount();
        String refundCurrency =commonValidatorVO.getTransDetailsVO().getRefundCurrency();;
        String marketPlaceFlag="";
        String accountId = "";
        String currency="";
        String captureAmount = "";
        String refundAmount = "";
        String transactionStatus = "";
        String refundStatus = "";

        Connection connection=null;
        try
        {
            connection= Database.getConnection();
            Hashtable commHash = commonValidatorVO.getTransHash();
            if (commHash!= null && !commHash.isEmpty())
            {
                accountId = (String) commHash.get("accountid");
                if(functions.isValueNull((String)commHash.get("currency"))){
                    currency=(String) commHash.get("currency");
                }else{
                    currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                }
//check request currency
                captureAmount = (String) commHash.get("captureamount");
                refundAmount = (String) commHash.get("refundamount");
                transactionStatus = (String) commHash.get("status");

                if (currency.equals("JPY"))
                {
                    if (!paymentChecker.isAmountValidForJPY(currency, refundAmt))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_JPY_CURRENCY_CHECK, ErrorMessages.JPY_CURRENCY));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.JPY_CURRENCY, new Throwable(ErrorMessages.JPY_CURRENCY));
                    }
                }
                else
                {
                    if (!Functions.checkAccuracy(refundAmt, 2))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, ErrorMessages.INVALID_AMOUNT));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));
                    }
                }
                RefundChecker refundChecker=new RefundChecker();
                captureAmount = (String) commHash.get("captureamount");
                refundAmount = (String) commHash.get("refundamount");
                transactionStatus = (String) commHash.get("status");

                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));
                PZRefundRequest refundRequest = new PZRefundRequest();

                refundRequest.setMarketPlaceFlag(marketPlaceFlag);
                refundRequest.setAccountId(Integer.valueOf(accountId));
                refundRequest.setMemberId(Integer.valueOf(commonValidatorVO.getMerchantDetailsVO().getMemberId()));
                refundRequest.setRefundAmount(refundAmt);
                refundRequest.setCurrency(currency);
                refundRequest.setRefundReason("Auto Refunded due to Fraud Identified – Fraud Defender");
                refundRequest.setTransactionStatus(transactionStatus);
                refundRequest.setTrackingId(Integer.valueOf(trackingId));
                refundRequest.setReversedAmount(refundAmount);
                refundRequest.setCaptureAmount(captureAmount);

                // refundRequest.setIpAddress(directRefundValidatorVO.getMerchantIpAddress());
                auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                auditTrailVO.setActionExecutorName("fraudefender");
                refundRequest.setAuditTrailVO(auditTrailVO);


                PZRefundResponse refundResponse = paymentProcess.refund(refundRequest);
                PZResponseStatus responseStatus = refundResponse.getStatus();
                String refundDescription = refundResponse.getResponseDesceiption();

                if (PZResponseStatus.SUCCESS.equals(responseStatus))
                {
                    stat = "Y";
                    double refAmount=Double.parseDouble(refundAmt)+Double.parseDouble(refundAmount);
                    directKitResponseVO.setRefundAmount(refundAmt);
                    statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId, "reversed");
                    transactionManager.updateRefundFraudTransactionDetails(trackingId,"Y",String.format("%.2f",refAmount));
                    refundStatus = PZResponseStatus.SUCCESS.toString();


                    if (Double.parseDouble(refundAmt) < Double.parseDouble(captureAmount))
                    {
                        refundStatus = PZResponseStatus.PARTIALREFUND.toString();
                        refundResponse.setStatus(PZResponseStatus.PARTIALREFUND);

                    }

                    directKitResponseVO.setStatus("success");

                }
                else
                {
                    directKitResponseVO.setStatus("fail");
                    stat = "N";
                }
                String reason="Current transaction have been identified as fraudulent by the system. Auto refunded by the system to avoid any chargebacks";
                statMessage = refundDescription;
                if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsRefundEmailSent()))
                {
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, trackingId, responseStatus.toString(), reason, null);
                }
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", "System error while refunding transaction", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        // transactionUtil.setRefundSystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, commonValidatorVO, "Y".equals(stat) ? PZResponseStatus.SUCCESS : PZResponseStatus.FAILED, statMessage);
        return directKitResponseVO;
    }
}
