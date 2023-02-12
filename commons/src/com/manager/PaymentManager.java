package com.manager;

import com.directi.pg.ActionEntry;
import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.PaymentDAO;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Ecospend.EcospendResponseVO;
import com.payment.PZTransactionStatus;
import com.payment.PayMitco.core.PayMitcoPaymentProcess;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.skrill.SkrillResponseVO;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/11/15
 * Time: 1:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentManager
{
    private static Logger logger = new Logger(PartnerManager.class.getName());
    private static PaymentDAO paymentDAO = new PaymentDAO();
    private static ActionEntry actionEntry=new ActionEntry();
    private static StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
    static Functions functions= new Functions();

    public static int insertAuthStartedTransactionEntryForVoucherMoney(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        actionEntry.actionEntryExtensionforVM(commonValidatorVO);
        return detailId;
    }

    public static int insertAuthStartedTransactionEntryForFlexepinVoucher(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        paymentDAO.updateAuthstartedTransactionforFlexepinVoucher(commonValidatorVO,trackingId);
        CommResponseVO commResponseVO                               = null;
        CommRequestVO commRequestVO                                 = new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO                   = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO           = new CommTransactionDetailsVO();

        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        actionEntry.actionEntryExtensionforVM(commonValidatorVO);
        return detailId;
    }

    public void updateCapturesuccessForFlexepinVoucher(HashMap<String,String> hashMap,Comm3DResponseVO transRespDetails,String paymentId,String trackingId,String status,String amount) throws PZDBViolationException
    {
        paymentDAO.updateCaptureforFlexepinVoucher(hashMap,transRespDetails,paymentId, trackingId, status, amount);
        statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
    }

    public int insertTransactionEntryForFlexepinVoucher(String trackingId,String payoutAmount,String currency, String status) throws PZDBViolationException
    {
        return paymentDAO.insertTransactionEntryForFlexepinVoucher(trackingId,payoutAmount,currency,status);
    }

    public static int updateCancelTransactionEntryForCommon(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId = 0;
        CommRequestVO commRequestVO=new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        paymentDAO.updateCancelTransactionforCommon(commonValidatorVO, trackingId);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CANCLLED_BY_CUSTOMER, ActionEntry.STATUS_CANCLLED_BY_CUSTOMER, null, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        return detailId;
    }

    public static int updateFailedSessionoutTransaction(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId = 0;
        paymentDAO.updateFailedSessionoutTransaction(commonValidatorVO, trackingId);
        //detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, null, null, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        return detailId;
    }

    public static int insertAuthStartedTransactionEntryForCommon(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        return detailId;
    }

    public static void insertSkrillNetellerDetailEntry(CommonValidatorVO commonValidatorVO,String tableName) throws PZDBViolationException
    {
        actionEntry.actionEntryExtensionforSkrillNeteller(commonValidatorVO,tableName);
    }

    public static void insertNetellerDetailEntry(CommonValidatorVO commonValidatorVO,String verificationLevel) throws PZDBViolationException
    {
        actionEntry.actionEntryExtensionforNeteller(commonValidatorVO, verificationLevel);
    }

    public static void updateSkrillNetellerDetailEntry(SkrillResponseVO commResponseVO,String trackingid) throws PZDBViolationException
    {
        actionEntry.actionEntryExtensionforSkrill(commResponseVO, trackingid);
    }

    public static int insertAuthStartedTransactionEntryForEpay(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        actionEntry.actionEntryExtensionforEpay(commonValidatorVO);
        return detailId;
    }

    public void getMerchantandTransactionDetails(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        //TerminalDAO terminalDAO = new TerminalDAO();
        paymentDAO.getTransactionDetails(commonValidatorVO);
        //paymentDAO.getMerchantDetails(commonValidatorVO);
        //TerminalVO terminalVO = terminalDAO.getMemberTerminalfromTerminal(commonValidatorVO.getTerminalId());
        /*commonValidatorVO.getMerchantDetailsVO().setAccountId(terminalVO.getAccountId());
        commonValidatorVO.setTerminalVO(terminalVO);*/
    }

    public int insertBegunTransactionEntry(CommonValidatorVO commonValidatorVO,String header) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId = paymentDAO.insertBegunTransactionForCommon(commonValidatorVO, header, "begun");

        /*String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tablename = Database.getTableName(gatewayType.getGateway());
        if(accountId!=null && tablename!=null)
        {
            if(tablename.equals("transaction_common"))
            {
                trackingId = paymentDAO.insertBegunTransactionForCommon(commonValidatorVO, header, "begun");
            }
            else if(tablename.equals("transaction_qwipi"))
            {
                trackingId = paymentDAO.insertBegunTransactionForQwipi(commonValidatorVO,header,"begun");
            }
            else if(tablename.equals("transaction_ecore"))
            {
                trackingId = paymentDAO.insertBegunTransactionForEcore(commonValidatorVO,header,"begun");
            }
        }*/
        return trackingId;
    }

    public int insertBegunTransactionEntry(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,String header) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId = paymentDAO.insertBegunTransactionForCommon(commonValidatorVO, header, "begun");
        CommResponseVO commResponseVO = null;
        if (functions.isValueNull(commonValidatorVO.getReason()))
        {
            commResponseVO = new CommResponseVO();
            commResponseVO.setRemark(commonValidatorVO.getReason());
        }
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        //actionEntry.actionEntryForCommon(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_BEGUN_PROCESSING, ActionEntry.STATUS_BEGUN, commResponseVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        actionEntry.actionEntryForCommon(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_BEGUN_PROCESSING, ActionEntry.STATUS_BEGUN, commResponseVO, commRequestVO,auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return trackingId;
    }

    public void updateDetailsTablewithErrorName(String errorName,String trackingid) throws PZDBViolationException
    {
        paymentDAO.updateDetailsTablewithErrorName(errorName,trackingid);
    }

    //inserting transaction for first time according to the status
    public int insertTransactionCommon(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder,String ipaddress,String  status, String terminalID) throws PZDBViolationException
    {
        return paymentDAO.insertTransactionCommon( toid,  totype,  fromid,  fromtype,  descreption,  orderdescription,  amount,  redirecturl,  accontid,  paymodeid,  cardtypeid,  currency,  httphadder, ipaddress, status, terminalID);
    }

    public int insertTransactionCommonForPayout(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder,String ipaddress,String  status, String terminalID,String name,String ccnum,String firstname,String lastname,String street,String country,String city,String state,String zip,String telno,String telnocc,String expdate,String email, String tmpl_amount, String tmpl_currency,String customerid, String notificationUrl,String ewalletid,String cardTypeName) throws PZDBViolationException
    {
        return paymentDAO.insertTransactionCommonForPayout( toid, totype, fromid, fromtype, descreption, orderdescription, amount, redirecturl, accontid, paymodeid, cardtypeid, currency, httphadder, ipaddress, status, terminalID,name,ccnum,firstname,lastname,street,country,city,state,zip,telno,telnocc,expdate,email, tmpl_amount,tmpl_currency,customerid,notificationUrl,ewalletid,cardTypeName);
    }

    public int insertTransactionCommonForPayoutSuccess(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder,String ipaddress,String  status, String terminalID,String name,String ccnum,String firstname,String lastname,String street,String country,String city,String state,String zip,String telno,String telnocc,String expdate,String email, String tmpl_amount, String tmpl_currency,String customerid, String notificationUrl,String ewalletid,String cardTypeName,String paymentid) throws PZDBViolationException
    {
        return paymentDAO.insertTransactionCommonForPayoutSuccess( toid, totype, fromid, fromtype, descreption, orderdescription, amount, redirecturl, accontid, paymodeid, cardtypeid, currency, httphadder, ipaddress, status, terminalID,name,ccnum,firstname,lastname,street,country,city,state,zip,telno,telnocc,expdate,email, tmpl_amount,tmpl_currency,customerid,notificationUrl,ewalletid,cardTypeName,paymentid);
    }
    //this is to insertNew Entry in the transaction_common_details according to the status
    public int actionEntryForCommon(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO,AuditTrailVO auditTrailVO,String remark) throws PZDBViolationException
    {
        return paymentDAO.actionEntryForCommon(trackingId, amount, action, status, responseVO, requestVO, auditTrailVO, remark);
    }

    //this is to update status of the transaction
    public void updateTransactionAfterResponseForCommon(String status, String amount, String machineid, String paymentid, String remark, String dateTime, String trackingId) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse("transaction_common", status, amount, machineid, paymentid, remark, dateTime, trackingId);
    }

    public void updateExtensionforEpay(String trackingid,String status,String pay_time, String stan, String bcode) throws PZDBViolationException
    {
        paymentDAO.updateExtensionforEpay(trackingid, status, pay_time, stan, bcode);
    }

    public HashMap getExtnDetailsForEpay(String trackingid) throws PZDBViolationException
    {
        return paymentDAO.getExtnDetailsForEpay(trackingid);
    }

    public HashMap getExtnDetailsForTrustly(String trackingid) throws PZDBViolationException
    {
        return paymentDAO.getExtnDetailsForTrustly(trackingid);
    }

    public HashMap getExtnDetailsForSkrill(String trackingid) throws PZDBViolationException
    {
        return paymentDAO.getExtnDetailsForSkrill(trackingid);
    }

    public HashMap getExtnDetailsForPM(String trackingid) throws PZDBViolationException
    {
        return paymentDAO.getExtnDetailsForPM(trackingid);
    }

    public HashMap getExtnDetailsForVM(String trackingid) throws PZDBViolationException
    {
        return paymentDAO.getExtnDetailsForVM(trackingid);
    }

    public HashMap getExtnDetailsforNotification(String trackingid,String table) throws PZDBViolationException
    {
        HashMap hashMap = new HashMap();

        if("VM".equalsIgnoreCase(table))
             hashMap = paymentDAO.getExtnDetailsForVM(trackingid);
        if("PM".equalsIgnoreCase(table))
            hashMap = paymentDAO.getExtnDetailsForPM(trackingid);
        if("SKRILL".equalsIgnoreCase(table))
            hashMap = paymentDAO.getExtnDetailsForSkrill(trackingid);
        if("TRUSTLY".equalsIgnoreCase(table))
            hashMap = paymentDAO.getExtnDetailsForTrustly(trackingid);
        if("EPAY".equalsIgnoreCase(table))
            hashMap = paymentDAO.getExtnDetailsForEpay(trackingid);

        return hashMap;
    }

    public int insertAuthStartedTransactionEntryForCup(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        detailId = actionEntry.actionEntryForCUP(commonValidatorVO.getTrackingid(),commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,null,commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),auditTrailVO,commonValidatorVO);
        return detailId;
    }

    public void updateCommonForExchanger(CommonValidatorVO commonValidatorVO,String status)throws PZDBViolationException
    {
        paymentDAO.updateTransactionforExchanger(commonValidatorVO, status);
    }



    public int insertAuthStartedTransactionEntryForAsyncFlow(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        detailId = paymentDAO.insertAuthstartedTransactionforAsync(commonValidatorVO);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return detailId;
    }

    public int insertAuthStartedTransactionEntryForPaySafeCard(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        actionEntry.actionEntryForPaySafeCardDetails(commonValidatorVO);
        return detailId;
    }

    public int insertAuthStartedTransactionEntryForCupUPI(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        // refered paymitco and PaySafeCard
        int detailId=0;
      //  paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        paymentDAO.updateAuthstartedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingId, "transaction_common");
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

       // actionEntry.actionEntryForCupUPIDetail(commonValidatorVO);
        return detailId;
    }

    public int insertSMSStartedTransactionEntryForCupUPI(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        // refered paymitco and PaySafeCard
        int detailId=0;
        paymentDAO.updateSMSstartedTransactionforCupUPI(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCupUPI(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_SMS_STARTED,ActionEntry.STATUS_SMS_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        return detailId;
    }

    public int insertEnrollmentStartedTransactionEntryForCupUPI(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO, String status) throws PZDBViolationException
    {
        // refered paymitco and PaySafeCard
        int detailId=0;
        paymentDAO.updateEnrollmentstartedTransactionforCupUPI(commonValidatorVO, trackingId,status);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        if (status.equalsIgnoreCase("enrollmentstarted"))
            detailId = actionEntry.actionEntryForCupUPI(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_ENROLLMENT_STARTED,ActionEntry.STATUS_ENROLLMENT_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        else
            detailId = actionEntry.actionEntryForCupUPI(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        // actionEntry.actionEntryForCupUPIDetail(commonValidatorVO);
        return detailId;
    }

    public int insertAuthStartedTransactionEntryForPayMitco(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionForPayMitco(commonValidatorVO, trackingId);
        //detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, null, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),auditTrailVO);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return detailId;
    }

    public void updatePaymentIdForCommon(CommResponseVO commResponseVO,String trackingId) throws PZDBViolationException
    {
        paymentDAO.updatePaymentIdforCommon(commResponseVO, trackingId);
    }

    public void insertEcospendDetails(EcospendResponseVO ecospendResponseVO,String trackingId) throws PZDBViolationException
    {
        paymentDAO.insertEcospendDetails(ecospendResponseVO, trackingId);
    }

    public void getTransactionDetails(String trackingid)
    {
        paymentDAO.getTransactionDetails(trackingid);
    }
    public void updatePaymentTransactionModeforCommon(String transactionmode,String trackingId) throws PZDBViolationException
    {
        paymentDAO.updatePaymentTransactionModeforCommon(transactionmode, trackingId);
    }


    public void updatePaymentRemarkforCommon(String remark,String trackingId) throws PZDBViolationException
    {
        paymentDAO.updatePaymentRemarkforCommon(remark, trackingId);
    }

    public void updatePaymentIdForCommon(String paymentId,String trackingId,String status) throws PZDBViolationException
    {
        paymentDAO.updatePaymentIdforCommon(paymentId,trackingId,status);
        if(status.equalsIgnoreCase("authsuccessful"))
        {
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authsuccessful");
        }
        else if(status.equalsIgnoreCase("authfailed"))
        {
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authfailed");
        }
        else if(status.equalsIgnoreCase("authfailed"))
        {
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authfailed");
        }

    }

    public void updateCapturesuccessForPaySafeCard(String paymentId,String trackingId,String status,String amount) throws PZDBViolationException
    {
        paymentDAO.updateCaptureforPaysafeCard(paymentId,trackingId,status,amount);
        statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"capturesuccess");
    }

    public int updateAuthStartedTransactionEntryForP4(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,boolean isSEPA) throws PZDBViolationException
    {
        int detailId=0;
        int details_Id=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        if(isSEPA)
        {
            details_Id = actionEntry.actionEntryForP4Details(commonValidatorVO.getTrackingid(), String.valueOf(detailId), commonValidatorVO);
        }
        return detailId;
    }

    public int updateAuthStartedTransactionEntryForB4(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        logger.debug("Inside updateAuthStartedTransactionEntryForB4---->");
        int detailId=0;
        int details_Id=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);


        details_Id = actionEntry.actionEntryForB4Details(commonValidatorVO.getTrackingid(), String.valueOf(detailId), commonValidatorVO);

        return detailId;
    }

    public void updateTransactionForCommon(CommResponseVO commResponseVO,String status,String trackingId,AuditTrailVO auditTrailVO,String tableName,String machineId,String respPaymentId,String dateTime,String remark) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName, status, commResponseVO.getAmount(), machineId, respPaymentId, remark, dateTime, trackingId);
        if(status.equals(PZTransactionStatus.CAPTURE_SUCCESS.toString()))
        {
            actionEntry.actionEntryForCommon(trackingId, commResponseVO.getAmount().toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO, null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,PZTransactionStatus.CAPTURE_SUCCESS.toString());
        }
        else if(status.equals(PZTransactionStatus.AUTH_FAILED.toString()))
        {
            actionEntry.actionEntryForCommon(trackingId, commResponseVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO,auditTrailVO, null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,PZTransactionStatus.AUTH_FAILED.toString());
        }
        else if(status.equals(PZTransactionStatus.AUTH_SUCCESS.toString()))
        {
            actionEntry.actionEntryForCommon(trackingId, commResponseVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO,auditTrailVO, null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,PZTransactionStatus.AUTH_SUCCESS.toString());
        }
        else if(status.equals(PZTransactionStatus.REVERSED.toString()))
        {
            actionEntry.actionEntryForCommon(trackingId, commResponseVO.getAmount(), ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, commResponseVO,auditTrailVO, null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,PZTransactionStatus.REVERSED.toString());
        }
        else if(status.equals(PZTransactionStatus.FAILED.toString()))
        {
            actionEntry.actionEntryForCommon(trackingId, commResponseVO.getAmount(), ActionEntry.STATUS_FAILED, ActionEntry.STATUS_FAILED, commResponseVO,auditTrailVO, null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,PZTransactionStatus.FAILED.toString());
        }
    }

    public int insertAuthStartedTransactionEntryForQwipiEcoreCommon(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,String tableName) throws PZDBViolationException
    {
        int detailId=0;
            paymentDAO.updateAuthstartedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingId, tableName);
            detailId = actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null,auditTrailVO);
        return detailId;
    }

    public int insertAuthStartedTransactionEntryForCommon(CommonValidatorVO commonValidatorVO,String trackingId,CommRequestVO commRequestVO,AuditTrailVO auditTrailVO,String tableName) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingId, tableName);
        CommResponseVO commResponseVO=null;
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        return detailId;
    }

    public void updateFailedTransactionEntryForQwipiEcoreCommon(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,String tableName,String machineId,String respPaymentId,String dateTime,String remark) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName, "failed", commonValidatorVO.getTransDetailsVO().getAmount(), machineId, respPaymentId, remark, dateTime, trackingId);
        actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null,auditTrailVO);
        statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"failed");
    }

    public void updateAuthFailedTransactionForQwipiEcoreCommon(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,String tableName,String machineId,String respPaymentId,String dateTime,String remark) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName,"authfailed",commonValidatorVO.getTransDetailsVO().getAmount(),machineId,respPaymentId,remark,dateTime,trackingId);
        actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null,auditTrailVO);
        statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authfailed");
    }



    public void updateAuthSuccessTransactionForQwipiEcoreCommon(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,String tableName,String machineId,String respPaymentId,String dateTime,String remark) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName,"authsuccessful",commonValidatorVO.getTransDetailsVO().getAmount(),machineId,respPaymentId,remark,dateTime,trackingId);
        actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null,auditTrailVO);
        statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authsuccessful");
    }

    public void updateCaptureSuccessTransactionForQwipiEcoreCommon(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,String tableName,String machineId,String respPaymentId,String dateTime,String remark) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName,"capturesuccess",commonValidatorVO.getTransDetailsVO().getAmount(),machineId,respPaymentId,remark,dateTime,trackingId);
        actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null,auditTrailVO);
        statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"capturesuccess");
    }

    public Hashtable getTransactionDetailsForCommon(String trackingId) throws PZDBViolationException
    {
        return paymentDAO.getTransactionDetailsForCommon(trackingId);
    }

    public Hashtable getAccountIdandPaymeIdForCommon(String trackingId) throws PZDBViolationException
    {
        return paymentDAO.getAccountIdandPaymeIdForCommon(trackingId);
    }

    public String getErrorCodeDescription(String gatewayName,String errorCode) throws PZDBViolationException
    {
        return paymentDAO.loadReason(errorCode,gatewayName);
    }

    public int insertAuthStartedTransactionEntryForReitumu3D(CommonValidatorVO commonValidatorVO,String trackingId,AuditTrailVO auditTrailVO,String tableName) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforCommon(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return detailId;
    }

    public int insertAuthStartedTransactionEntryForRecurringBilling(TransactionDetailsVO transactionDetailsVO,AuditTrailVO auditTrailVO,String tableName) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId= paymentDAO.insertAuthStartedForRecurring(transactionDetailsVO);
        actionEntry.actionEntryForGenericTransaction(tableName, String.valueOf(trackingId), transactionDetailsVO.getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, transactionDetailsVO.getIpAddress(),null,auditTrailVO);
        return trackingId;
    }
    //this is to update otherDetails of the transaction
    public boolean updateOtherDetailsOfTransaction(String ccnum,String firstname,String lastname,String street,String country,String city,String state,String zip,String telno,String telnocc,String expdate,String email,String trackingId) throws PZDBViolationException
    {
        return paymentDAO.updateDetailsOfTransactionForCommon( ccnum, firstname, lastname, street, country, city, state, zip, telno, telnocc, expdate,email, trackingId);
    }
    //Direct KIT flow
    //converting hashTable to  VO of commonValidator according toid and accountId

    public int insertSMSStartedTransactionEntryForCupUPIRest(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,CommRequestVO commRequestVO,String tableName) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId= paymentDAO.insertSMSStartedEntryForCupRest(commonValidatorVO,tableName);
        CommResponseVO commResponseVO=null;
        actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_SMS_STARTED, ActionEntry.STATUS_SMS_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return trackingId;
    }
    public int insertEnrollmentStartedTransactionEntryForCupUPIRest(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,CommRequestVO commRequestVO,String tableName) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId= paymentDAO.insertAuthStartedEntry(commonValidatorVO,tableName);
        CommResponseVO commResponseVO=null;

        if(commRequestVO==null){
            commRequestVO= new CommRequestVO();
            CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
            commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
                commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            }else {
                commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
                commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            }else{
                commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
            }
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        }

        actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_ENROLLMENT_STARTED, ActionEntry.STATUS_ENROLLMENT_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return trackingId;
    }

    public int insertAuthStartedTransactionEntry(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,CommRequestVO commRequestVO,String tableName) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId= paymentDAO.insertAuthStartedEntry(commonValidatorVO,tableName);
        CommResponseVO commResponseVO=null;

        if(commRequestVO==null){
            commRequestVO= new CommRequestVO();
            CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
            commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
                commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            }else {
                commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
                commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            }else{
                commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
            }
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        }

        actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return trackingId;
    }

    public int insertAuthStartedEntryForAccount(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,CommRequestVO commRequestVO,String tableName) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId= paymentDAO.insertAuthStartedEntryForAccount(commonValidatorVO, tableName);
        CommResponseVO commResponseVO=null;
        actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        return trackingId;
    }

    public int insertAuthStartedForCommon(CommonValidatorVO commonValidatorVO,String status)throws PZDBViolationException
    {
        int trackingId=0;
        trackingId=paymentDAO.insertAuthStartedForCommon(commonValidatorVO);
        return trackingId;

    }
    public void updateTransactionStatusAfterResponse(String trackingId,String status,String captureAmount,String remark,String paymentId,String transaction_mode )
    {
        paymentDAO.updateTransactionStatusAfterResponse(String.valueOf(trackingId),status,captureAmount,remark,paymentId,transaction_mode);
        if(!status.equalsIgnoreCase("pending"))
        {
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
        }
    }
    public void updateTransactionStatusAfterResponse(String trackingId,String status,String captureAmount,String remark,String paymentId )
    {
        String transaction_mode="";
        paymentDAO.updateTransactionStatusAfterResponse(String.valueOf(trackingId),status,captureAmount,remark,paymentId,transaction_mode);
        if(!status.equalsIgnoreCase("pending"))
        {
            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
        }
    }
    public CommonValidatorVO getCommontransactionDetails(String trackingid)
    {
       return paymentDAO.getTransDetailsForCommon(trackingid);
    }

    public void updateTransactionForPayMitco(CommResponseVO transRespDetails, String status, String trackingid, AuditTrailVO auditTrailVO, String tableName, String ipaddress) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName,status,transRespDetails.getAmount(),"",transRespDetails.getTransactionId(),transRespDetails.getRemark(),transRespDetails.getResponseTime(),trackingid);
        int detailId = 0;
        PayMitcoPaymentProcess payMitcoPaymentProcess = new PayMitcoPaymentProcess();
        if(status.equals(PZTransactionStatus.CAPTURE_SUCCESS.toString()))
        {
            detailId = actionEntry.actionEntryForCommon(trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null,auditTrailVO,ipaddress,null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid,PZTransactionStatus.CAPTURE_SUCCESS.toString());
            payMitcoPaymentProcess.actionEntryExtension(detailId, trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, status, transRespDetails, null);
        }
        else if(status.equals(PZTransactionStatus.AUTH_FAILED.toString()))
        {
            detailId = actionEntry.actionEntryForCommon(trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, null, auditTrailVO,ipaddress,null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid,PZTransactionStatus.AUTH_FAILED.toString());
            payMitcoPaymentProcess.actionEntryExtension(detailId, trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, status, transRespDetails, null);
        }
    }
    public void updateTransactionForEZPayNow(CommResponseVO transRespDetails, String status, String trackingid, AuditTrailVO auditTrailVO, String tableName, String ipaddress) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName,status,transRespDetails.getAmount(),"",transRespDetails.getTransactionId(),transRespDetails.getRemark(),transRespDetails.getResponseTime(),trackingid);
        int detailId = 0;
        PayMitcoPaymentProcess payMitcoPaymentProcess = new PayMitcoPaymentProcess();
        if(status.equals(PZTransactionStatus.CAPTURE_SUCCESS.toString()))
        {
            detailId = actionEntry.actionEntryForCommon(trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null,auditTrailVO,ipaddress,null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid,PZTransactionStatus.CAPTURE_SUCCESS.toString());
        }
        else if(status.equals(PZTransactionStatus.AUTH_FAILED.toString()))
        {
            detailId = actionEntry.actionEntryForCommon(trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, null, auditTrailVO,ipaddress,null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid,PZTransactionStatus.AUTH_FAILED.toString());
        }
    }
    public void updateTransactionForCupUpi(CommResponseVO transRespDetails, String status, String trackingid, AuditTrailVO auditTrailVO, String tableName, String ipaddress) throws PZDBViolationException
    {
        paymentDAO.updateTransactionAfterResponse(tableName,status,transRespDetails.getAmount(),"",transRespDetails.getTransactionId(),transRespDetails.getRemark(),transRespDetails.getResponseTime(),trackingid);
        int detailId = 0;
        if(status.equals(PZTransactionStatus.CAPTURE_SUCCESS.toString()))
        {
            detailId = actionEntry.actionEntryForCommon(trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null,auditTrailVO,ipaddress,null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid,PZTransactionStatus.CAPTURE_SUCCESS.toString());
        }
        else if(status.equals(PZTransactionStatus.AUTH_FAILED.toString()))
        {
            detailId = actionEntry.actionEntryForCommon(trackingid, transRespDetails.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, null, auditTrailVO,ipaddress,null);
            statusSyncDAO.updateAllTransactionFlowFlag(trackingid,PZTransactionStatus.AUTH_FAILED.toString());
        }
    }
    public String getSubCardType(String firstSix)
    {
        return "Consumer Card";
    }
    public String insertPerfectMoneyDetails(String trackingId,String transactionStatus,String paymentId,String paymentAmount,String paymentUnit,String payeeAccount,String payerAccount,String paymentBatchNumber,String timestampGmt,String email,String bankId)throws PZDBViolationException
    {
        return paymentDAO.insertPerfectMoneyDetails(trackingId,transactionStatus,paymentId,paymentAmount,paymentUnit,payeeAccount,payerAccount,paymentBatchNumber,timestampGmt,email,bankId);
    }
    public String insertJetonDetails(String trackingId,String status,String paymentId,String customerNumber)throws PZDBViolationException
    {
        return paymentDAO.insertJetonDetails(trackingId,status,paymentId,customerNumber);
    }



    public void addBinDetailsEntry(String trackingId,String accountId,String firstSix,String lastFour,String bin_Brand,String bin_Transaction_Type,String bin_Card_Type,String bin_Card_Category,String bin_Usage_Type) throws PZDBViolationException{
        paymentDAO.addBinDetailsEntry(trackingId,accountId,firstSix,lastFour,bin_Brand,bin_Transaction_Type,bin_Card_Type,bin_Card_Category,bin_Usage_Type);
    }

    public String getStatusOfTransaction(String trackingId)
    {
        return paymentDAO.getStatusOfTransaction(trackingId);
    }

    public String getTerminalFromTrackingid(String trackingId)
    {
        return paymentDAO.getTerminalFromTrackingid(trackingId);
    }

    public boolean isTransactionExist(String trackingId)
    {
        return paymentDAO.isTransactionExist(trackingId);
    }

    public int mark3DTransaction(String trackingId) throws PZDBViolationException
    {
        return paymentDAO.mark3DTransaction(trackingId);
    }
    /*public String getCvv(CommonValidatorVO commonValidatorVO)
    {
        return paymentDAO.getCvv(commonValidatorVO);
    }*/
    public CommonValidatorVO getTerminalBasedOnMarketPlaceSubmerchant(CommonValidatorVO commonValidatorVO,String terminalid)
    {
        return paymentDAO.getTerminalBasedOnMarketPlaceSubmerchant(commonValidatorVO,terminalid);
    }
    public MarketPlaceVO getMarketPlaceDetailsByTrackingid(String trackingid)
    {
        return paymentDAO.getMarketPlaceDetailsByTrackingid(trackingid);
    }
    public int insertBegunTransactionEntryForMarketPlace(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,String header,String toType) throws PZDBViolationException
    {
        int trackingId=0;
        String parent_trackingid=commonValidatorVO.getTrackingid();
        trackingId = paymentDAO.insertBegunTransactionForCommonMarketPlace(commonValidatorVO, header, "begun", toType);
        CommResponseVO commResponseVO = null;
        if (functions.isValueNull(commonValidatorVO.getReason()))
        {
            commResponseVO = new CommResponseVO();
            commResponseVO.setRemark(commonValidatorVO.getReason());
        }
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        //actionEntry.actionEntryForCommon(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_BEGUN_PROCESSING, ActionEntry.STATUS_BEGUN, commResponseVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        actionEntry.actionEntryForCommon(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_BEGUN_PROCESSING, ActionEntry.STATUS_BEGUN, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        return trackingId;
    }
    public int insertAuthStartedTransactionEntryForMarketPlaceCommon(CommonValidatorVO commonValidatorVO,String child_trackingId,String parent_trackingid,CommRequestVO commRequestVO,AuditTrailVO auditTrailVO,String tableName) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionForQwipiEcoreCommonForMarketPlace(commonValidatorVO, child_trackingId, commonValidatorVO.getMarketPlaceVO().getAmount(), tableName);
        CommResponseVO commResponseVO=null;
        detailId = actionEntry.actionEntryForCommon(child_trackingId, commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        return detailId;
    }

    public static int updateAuthSuccessfulTransactionForWallet(CommonValidatorVO commonValidatorVO, String trackingId) throws PZDBViolationException
    {
        int detailId=0;
        paymentDAO.updateAuthSuccessfulForWallet(commonValidatorVO,trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();


        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, commRequestVO, null, "");

        return detailId;
    }

    public int insertCaptureStartedTransactionEntryForStaticQR(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        logger.error("in insertCaptureStartedTransactionEntryForStaticQR ");
        int detailId=0;
        detailId = paymentDAO.insertCaptureStartedTransactionforStaticQR(commonValidatorVO);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }


        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletAmount())){
            commTransactionDetailsVO.setWalletAmount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
        }

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletCurrency())){
            commTransactionDetailsVO.setWalletCurrency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
        }

        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);

        return detailId;
    }

    public static int updateAuthStartedTransactionEntryForDynamicQR(CommonValidatorVO commonValidatorVO, String trackingId, AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        logger.error("in updateAuthStartedTransactionEntryForDynamicQR ");
        int detailId=0;
        paymentDAO.updateAuthstartedTransactionforDynamicQR(commonValidatorVO, trackingId);
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();

        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }/*else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }*/
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }/*else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }*/

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletAmount())){
            commTransactionDetailsVO.setWalletAmount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
        }

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletCurrency())){
            commTransactionDetailsVO.setWalletCurrency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
        }


        logger.error("wallet amount =="+commTransactionDetailsVO.getWalletAmount());
        logger.error("wallet currency =="+commTransactionDetailsVO.getWalletCurrency());

        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),commonValidatorVO);
        return detailId;
    }

    public int insertBegunTransactionEntryForQRCheckout(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        logger.error("in insertBegunTransactionEntryForQRCheckout ---- ");
        int detailId=0;
        detailId =  paymentDAO.insertBegunTransactionForCommon(commonValidatorVO, commonValidatorVO.getTransDetailsVO().getHeader(), "begun");
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else{
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        actionEntry.actionEntryForCommon(String.valueOf(detailId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_BEGUN_PROCESSING, ActionEntry.STATUS_BEGUN, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        return detailId;
    }

    public static int updateConfirmationStatusQR(CommonValidatorVO commonValidatorVO,String status, String trackingId, AuditTrailVO auditTrailVO, String captureAmount) throws PZDBViolationException
    {
        logger.error("in updateConfirmationStatusQR ");
        int detailId=0;
        paymentDAO.updateStatusForQR(status, trackingId, captureAmount, commonValidatorVO.getTransDetailsVO().getWalletAmount(), commonValidatorVO.getTransDetailsVO().getWalletCurrency());
        CommResponseVO commResponseVO=null;
        CommRequestVO commRequestVO= new CommRequestVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
            commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }
        else {
            commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }
        else {
            commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletAmount())){
            commTransactionDetailsVO.setWalletAmount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
        }

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletCurrency())){
            commTransactionDetailsVO.setWalletCurrency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
        }

        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);


        if("authsuccessful".equalsIgnoreCase(status)){
            detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null);
        }
        else if("capturesuccess".equalsIgnoreCase(status)){
            detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null);
        }
        else if("authfailed".equalsIgnoreCase(status)){
            detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null);
        }
        else if("capturefailed".equalsIgnoreCase(status)){
            detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        }
        else if("failed".equalsIgnoreCase(status)){
            detailId = actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CANCLLED_BY_CUSTOMER, ActionEntry.STATUS_CANCLLED_BY_CUSTOMER, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),null);
        }

        return detailId;
    }
    public List<String> getCurrencyByMemberId(String memberId)
    {
        return paymentDAO.getCurrencyByMemberId(memberId);
    }

    public int insertInitAuthTransactionEntry(CommonValidatorVO commonValidatorVO,AuditTrailVO auditTrailVO,CommRequestVO commRequestVO,String tableName) throws PZDBViolationException
    {
        int trackingId=0;
        trackingId= paymentDAO.insertInitAuthStartedEntry(commonValidatorVO,tableName);
        CommResponseVO commResponseVO=null;

        if(commRequestVO==null){
            commRequestVO= new CommRequestVO();
            CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
            commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
                commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            }else {
                commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
            }
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
                commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            }else{
                commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
            }
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
        }

        //actionEntry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        return trackingId;
    }


}