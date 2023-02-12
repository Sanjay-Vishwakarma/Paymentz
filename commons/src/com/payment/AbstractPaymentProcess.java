package com.payment;


import com.directi.pg.AuditTrailVO;
import com.directi.pg.SystemError;
import com.manager.vo.DirectCommResponseVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.VTResponseVO;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.*;
import com.payment.response.*;
import com.payment.validators.vo.CommonValidatorVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPaymentProcess
{

    //public abstract Hashtable transactionAPI(Hashtable<String, String> transactionParameters);

    public abstract int insertTransactionDetails(Hashtable parameters) throws SystemError;

    public abstract String getRedirectPage(Hashtable parameters) throws SystemError;

  //  public abstract Hashtable<String,String> transaction(Map<String, String> transactionRequestParameters,Hashtable<String,Object> transactionAttributes);

    public abstract PZTransactionResponse transaction(PZTransactionRequest transactionRequest);

    public abstract PZRefundResponse refund(PZRefundRequest refundRequest);

    public abstract PZCaptureResponse capture(PZCaptureRequest captureRequest);

    public abstract PZCancelResponse cancel(PZCancelRequest cancelRequest);

    public abstract PZChargebackResponse chargeback(PZChargebackRequest pzChargebackRequest);

    public abstract PZStatusResponse status(PZStatusRequest pzStatusRequest);

    public abstract List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError;

    public abstract List<PZChargebackRecord> readChargebackFile(PZFileVO fileName) throws SystemError;

    public abstract List<PZChargebackRecord> readChargebackFileXlsx(PZFileVO fileName) throws SystemError;

  //To be used to get Status from gateway
    public abstract PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest);

    public abstract List<PZReconcilationResponce> reconcilationTransaction(List<PZReconcilationRequest> pzReconcilationRequests) throws SystemError;

    public abstract int actionEntry(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO,AuditTrailVO auditTrailVO,String remark) throws  PZDBViolationException;

    public abstract int actionEntry(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO,AuditTrailVO auditTrailVO) throws  PZDBViolationException;

    public abstract String getAdminEmailAddress();

    public abstract String getSpecificCreditPageTemplate();

    public abstract Hashtable specificValidationAPI(Hashtable<String, String> transactionParameters);

    public abstract String specificValidationAPI(HttpServletRequest request);

    public abstract Hashtable specificValidation(Map<String, String> transactionRequestParameters);

    public abstract String getSpecificVirtualTerminalJSP();

    public abstract void setTransactionVOExtension(CommRequestVO commRequestVO,CommonValidatorVO commonValidatorVO) throws PZDBViolationException;

    public abstract PZCancelResponse cancelCapture(PZCancelRequest cancelRequest);

    public abstract String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D);

    public abstract String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D);

    public abstract String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D);

    public abstract Hashtable process3DConfirmation(String trackingId, String paRes);

    public abstract void getIntegrationSpecificTransactionDetails(TransactionDetailsVO transactionDetailsVO,String trackingId,String toid);

    public abstract CommInquiryResponseVO getCommonInquiryResponseVO(TransactionDetailsVO transactionDetailsVO);

    public abstract void setAddtionalResponseParameters(CommInquiryResponseVO commInquiryResponseVO,TransactionDetailsVO transactionDetailsVO);

    public abstract DirectCommResponseVO transactionAPI(CommonValidatorVO commonValidatorVO);

    public abstract void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D);

    public abstract void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO);

    public abstract DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO);

    public abstract StringBuffer processFraudulentTransactions(int accountid, List<PZTC40Record> vTransactions, String toAddress, String isRefund);

    public abstract List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError;

    public abstract void setTransactionResponseVOParamsExtension(CommResponseVO responseVO,VTResponseVO vtResponseVO);

    public abstract StringBuffer processChargeback(List<PZChargebackRecord> vTransactions, String gateway, AuditTrailVO auditTrailVO);

    public abstract PZPayoutResponse payout(PZPayoutRequest payoutRequest);

    public abstract CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId)throws PZDBViolationException;

    public abstract CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO);

    public abstract void setTransactionVOParamsExtension(CommRequestVO commRequestVO, CommonValidatorVO commonValidatorVO) throws PZDBViolationException;

    public abstract void setTransactionResponseVOParamsExtension(CommResponseVO commResponseVO, DirectCommResponseVO directCommResponseVO) throws PZDBViolationException;

    public abstract DirectKitResponseVO setHostedPaymentResponseVO(DirectKitResponseVO directKitResponseVO,CommonValidatorVO commonValidatorVO);

    public abstract void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException;

    public abstract void setEnrollmentRequestVOExtention(EnrollmentRequestVO enrollmentRequestVO,TransactionDetailsVO transactionDetailsVO) throws PZDBViolationException;

    public abstract Hashtable readBulkPayoutFile(PZFileVO pzFileVO) throws SystemError;


}