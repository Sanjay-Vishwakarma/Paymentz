package servlets;
import com.directi.pg.*;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.enums.BlacklistEnum;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZChargebackRequest;
import com.payment.response.PZChargebackRecord;
import com.payment.response.PZChargebackResponse;
import com.payment.response.PZResponseStatus;
import org.owasp.esapi.ESAPI;

import javax.servlet.http.HttpServlet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 7/30/2020.
 */
public class UploadChargebackList extends HttpServlet
{
    private static Logger log = new Logger(UploadChargebackList.class.getName());

    public StringBuffer processChargeback(List<PZChargebackRecord> vTransactions, AuditTrailVO auditTrailVO,String type,String gateway)
    {
        CommonPaymentProcess commProcess        = new CommonPaymentProcess();
        TransactionManager transactionManager   = new TransactionManager();
        Functions functions                     = new Functions();
        StringBuffer returnMsg                  = new StringBuffer();
        MerchantDetailsVO merchantDetailsVO     = null;
        MerchantDAO merchantDAO                 = new MerchantDAO();
        String mailStatus = "";

        String trackingId   = "";
        String paymentId    = null;
        String cbAmount     = "";
        String previousStatus   = "";
        String toId             = "";
        String accountId        = "";
        String amount           = "";
        String currency         = "";

        returnMsg.append("<tr class=\"tdstyle texthead\"><td>  Tracking Id  </td><td> Merchant Id  </td><td>   Account Id  </td><td>   Payment Id  </td><td>  Amount  </td><td>  CB Amount  </td><td>    Status   </td><td>   Status Description    </td><td>   Previous Status   </td><td>   Current Status   </td></tr>");

        for(PZChargebackRecord pzChargebackRecord : vTransactions)
        {
            try
            {
                log.error("TrackingId---"+pzChargebackRecord.getTrackingid()+"---paymentId---"+pzChargebackRecord.getPaymentid()+"----"+pzChargebackRecord.getChargebackDate());
                if (functions.isValueNull(pzChargebackRecord.getTrackingid()))
                {
                    trackingId = pzChargebackRecord.getTrackingid();
                }
                else if(type.equals("trackingId") && !functions.isValueNull(trackingId))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    TrackingId is  missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if (functions.isValueNull(pzChargebackRecord.getPaymentid()) && !pzChargebackRecord.getPaymentid().equals("0.00"))
                {
                    paymentId = pzChargebackRecord.getPaymentid();
                }
                log.error("type---"+type+"---paymentId---"+paymentId+"-----"+pzChargebackRecord.getPaymentid());
                if(type.equals("paymentId") && (!functions.isValueNull(pzChargebackRecord.getPaymentid()) || pzChargebackRecord.getPaymentid().equals("0.00")))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    PaymentId is  missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }

                if (functions.isValueNull(pzChargebackRecord.getChargebackAmount()))
                {
                    cbAmount = pzChargebackRecord.getChargebackAmount();
                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback amount is missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(!functions.isValueNull(pzChargebackRecord.getCurrency()))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback currency is missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(!functions.isValueNull(pzChargebackRecord.getChargebackReason()))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback reason is missing   </td><td> - </td><td> - </td></tr>");
                    continue;
                }

                if (!ESAPI.validator().isValidInput("cbamount", pzChargebackRecord.getChargebackAmount(), "Amount", 12, false))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Invalid Chargeback amount   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if (!ESAPI.validator().isValidInput("currency", pzChargebackRecord.getCurrency(), "StrictString", 3, false))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Invalid Chargeback currency   </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                if(functions.isValueNull(pzChargebackRecord.getChargebackDate()))
                {
                    if(!pzChargebackRecord.getChargebackDate().matches("([0-9]{4})-([0-9]{2})-([0-9]{2}) 00:00:00"))
                    {
                        returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Invalid Date Format of Please provide date in mm/dd/yyyy format. </td><td> - </td><td> - </td></tr>");
                        continue;
                    }
                    SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat df           = new SimpleDateFormat("yyyy-MM-dd");
                    Date currentDate        = new Date();
                    System.out.println("NOW--"+df.format(currentDate));
                    Date reqDate = null;
                    try
                    {
                        reqDate = sdf.parse(pzChargebackRecord.getChargebackDate());
                    }
                    catch (ParseException e) {
                       log.error("Catch ParseException..",e);
                    }
                    System.out.println("ReqDate--"+df.format(reqDate));
                    if(reqDate.compareTo(currentDate)>0){
                        returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback date is greater then current date.   </td><td> - </td><td> - </td></tr>");
                        continue;
                    }
                }else {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Chargeback date should not be empty   </td><td> - </td><td> - </td></tr>");
                    continue;
                }

                log.error("paymentId::::"+paymentId+"-----type---"+type);
                TransactionDetailsVO transactionDetailsVO   = null;
                if(functions.isValueNull(paymentId) && type.equals("paymentId") && functions.isValueNull(gateway))
                {
                    transactionDetailsVO    = transactionManager.getCommonTransactionDetailsForChargeBackByBankId(paymentId);
                    if(transactionDetailsVO != null && !transactionDetailsVO.getFromtype().equals(gateway)){
                        returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Invalid request gateway and record gateway.  </td><td> - </td><td> - </td></tr>");
                        continue;
                    }
                }
                else if(type.equals("trackingId")) {
                    transactionDetailsVO = transactionManager.getCommonTransactionDetailsForChargeBack(trackingId);
                }
                else
                    transactionDetailsVO = null;
                if (transactionDetailsVO != null)
                {
                    toId            = transactionDetailsVO.getToid();
                    previousStatus  = transactionDetailsVO.getStatus();
                    accountId       = transactionDetailsVO.getAccountId();
                    amount          = transactionDetailsVO.getAmount();
                    currency        = transactionDetailsVO.getCurrency();
                    trackingId      = transactionDetailsVO.getTrackingid();
                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + "  </td><td>     -    </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -    </td><td>  " + pzChargebackRecord.getChargebackAmount() + "  </td><td>  " + "   failed   </td><td>    Record not found   </td><td> - </td><td> - </td></tr>");
                    continue;
                }

                if(functions.isValueNull(currency) && !currency.equalsIgnoreCase(pzChargebackRecord.getCurrency()))
                {
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + " </td><td>     -   </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -   </td><td>  " + pzChargebackRecord.getChargebackAmount() + " </td><td>  " + "   failed  </td><td>    Currency mismatch  </td><td> - </td><td> - </td></tr>");
                    continue;
                }
                /*if(Double.parseDouble(cbAmount)>(Double.parseDouble(amount)-Double.parseDouble(refundamout))){
                    returnMsg.append("<tr class='report'><td>" + pzChargebackRecord.getTrackingid() + " </td><td>     -   </td><td>     -     </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>     -   </td><td>  " + pzChargebackRecord.getChargebackAmount() + " </td><td>  " + "   failed  </td><td>    ChargeBack Amount should Not be greater then Capture Amount  </td><td> - </td><td> - </td></tr>");
                    continue;
                }*/
                PZChargebackRequest request = new PZChargebackRequest();
                request.setTrackingId(Integer.parseInt(trackingId));
                request.setAccountId(Integer.parseInt(accountId));
                request.setMemberId(Integer.parseInt(toId));
                request.setCaptureAmount(amount);
                request.setCbAmount(String.format("%.2f", Double.parseDouble(cbAmount)));
                request.setCbReason(pzChargebackRecord.getChargebackReason());
                request.setAdmin(true);
                request.setAuditTrailVO(auditTrailVO);
                request.setChargebackDate(pzChargebackRecord.getChargebackDate());
                log.error("Request---"+trackingId+"---"+accountId+"---"+toId+"---"+String.format("%.2f", Double.parseDouble(cbAmount))+"---"+pzChargebackRecord.getChargebackReason()+"---"+pzChargebackRecord.getChargebackDate());

                PZChargebackResponse pzChargebackResponse = commProcess.chargeback(request);

                if (pzChargebackResponse != null && (pzChargebackResponse.getStatus()).equals(PZResponseStatus.SUCCESS))
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>  " + String.format("%.2f", Double.valueOf(amount)) + "  </td><td>  " + String.format("%.2f", Double.valueOf(pzChargebackRecord.getChargebackAmount())) + "  </td><td>  " + "   success   </td><td>   " + pzChargebackResponse.getResponseDesceiption() + "  </td><td>  " + previousStatus + "  </td><td>  " + "chargeback </td></tr>");
                    mailStatus = "success";
                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>  " + String.format("%.2f", Double.valueOf(amount)) + "  </td><td>  " + String.format("%.2f", Double.valueOf(pzChargebackRecord.getChargebackAmount())) + "  </td><td>  " + "   failed   </td><td>   " + pzChargebackResponse.getResponseDesceiption() + "  </td><td>  " + previousStatus + "  </td><td>  " + previousStatus + " </td></tr>");
                    continue;
                }

                try
                {
                    merchantDetailsVO           = merchantDAO.getMemberDetails(toId);
                    String notificationUrl      = transactionDetailsVO.getNotificationUrl();
                    String notification_status  = "chargeback";
                    log.error("Notification Sending to---" + notificationUrl + "---" + trackingId + " status " + pzChargebackResponse.getDbStatus() + pzChargebackResponse.getResponseDesceiption());

                    log.error("ChargebackNotification flag for ---" + toId + "---" + merchantDetailsVO.getChargebackNotification());
                    if (functions.isValueNull(notificationUrl) && "Y".equals(merchantDetailsVO.getChargebackNotification()))
                    {
                        log.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, notification_status, pzChargebackResponse.getResponseDesceiption());
                    }
                }catch(Exception e){
                    log.error("PZDBViolationException--->", e);
                }
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getChargebackMailsend()))
                {
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.CHARGEBACK_TRANSACTION, trackingId, mailStatus, request.getCbReason(), null);
                }

                BlacklistManager blacklistManager = new BlacklistManager();
                WhiteListManager whiteListManager = new WhiteListManager();
                log.error("BlacklistDetails::::"+pzChargebackRecord.getIsBlacklist());
                log.error("RefundDetails::::"+pzChargebackRecord.getIsRefund());
                if (functions.isValueNull(pzChargebackRecord.getIsBlacklist()) && pzChargebackRecord.getIsBlacklist().equals("Y"))
                {
                    try
                    {
                        String blacklistReason = (pzChargebackRecord.getChargebackReason() +" (Chargeback Uploaded by Admin) "+"(" + pzChargebackRecord.getTrackingid() + ")");
                        whiteListManager.whiteListEntities(pzChargebackRecord.getTrackingid());
                        /*if(functions.isValueNull(transactionDetailsVO.getName())){
                            blacklistManager.addCustomerNameBatch(transactionDetailsVO.getName(), blacklistReason,auditTrailVO);
                        }*/
                        if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                            blacklistManager.addCustomerCardBatch(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()), blacklistReason,BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                        }
                        if(functions.isValueNull(transactionDetailsVO.getEmailaddr())){
                            blacklistManager.addCustomerEmailBatch(transactionDetailsVO.getEmailaddr(), blacklistReason,auditTrailVO);
                        }
                        /*if(functions.isValueNull(transactionDetailsVO.getTelno()))
                            blacklistManager.addCustomerPhone(transactionDetailsVO.getTelno(), blacklistReason,auditTrailVO);*/
                        log.error("transactionDetailsVO.getCardtype()--->"+transactionDetailsVO.getCardtype());
                        if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                        {
                            //vpa Address store in customerId field while transaction
                            blacklistManager.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(),blacklistReason,auditTrailVO);
                        }
                    }
                    catch (PZDBViolationException e)
                    {
                       log.error("Catch PZDBViolationException",e);
                    }
                }
                if (functions.isValueNull(pzChargebackRecord.getIsBlacklist()) && pzChargebackRecord.getIsBlacklist().equals("All"))
                {
                    try
                    {
                        String blacklistReason = (pzChargebackRecord.getChargebackReason() +" (Chargeback Uploaded by Admin) "+"(" + pzChargebackRecord.getTrackingid() + ")");
                        whiteListManager.whiteListEntities(pzChargebackRecord.getTrackingid());
                        Set<String> cardNum     = new HashSet<>();
                        Set<String> emailAddr   = new HashSet<>();
                        Set<String> phone       = new HashSet<>();
                        if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                        {
                            //vpa Address store in customerId field while transaction
                            blacklistManager.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(),blacklistReason,auditTrailVO);
                        }
                        /*if(functions.isValueNull(transactionDetailsVO.getName()))
                            transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);*/
                        /*if(functions.isValueNull(transactionDetailsVO.getName())){
                            blacklistManager.addCustomerNameBatch(transactionDetailsVO.getName(), blacklistReason,auditTrailVO);
                        }*/
                        log.error("emailAddr size---->" + emailAddr.size());
                        log.error("cardNum size---->" + cardNum.size());
                        if(emailAddr.size()>0){
                            blacklistManager.addCustomerEmailBatch(emailAddr, blacklistReason,auditTrailVO);
                        }
                        if(cardNum.size()>0){
                            blacklistManager.addCustomerCardBatch(cardNum, blacklistReason, BlacklistEnum.Chargeback_Received.toString(),auditTrailVO);
                        }
                       /* if(phone.size()>0)
                            blacklistManager.addCustomerPhoneBatch(phone, blacklistReason,auditTrailVO);*/
                    }
                    catch (PZDBViolationException e)
                    {
                        log.error("Catch PZDBViolationException..",e);
                    }
                }
                if (functions.isValueNull(pzChargebackRecord.getIsRefund()) && "Y".equals(pzChargebackRecord.getIsRefund()))
                {
                    log.error("entering in refund::::"+pzChargebackRecord.getIsRefund());
                    //Process refund
                    String refundreasonForMail = "Current transaction have been identified as fraudulent by the system. Auto refunded by the system to avoid any future chargebacks.";
                    String refundreason = "Auto Refunded by due to Chargeback received from same Customer.";
                    try
                    {
                        AsyncMultipleRefund asyncMultipleRefund=AsyncMultipleRefund.getInstance();
                        asyncMultipleRefund.asyncRefund(transactionDetailsVO, "0", auditTrailVO.getActionExecutorName(), refundreason, refundreasonForMail,"reversed");
                    }
                    catch (Exception e)
                    {
                        log.error("Exception while refund::::",e);
                        returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + pzChargebackRecord.getPaymentid() + "  </td><td>  " + String.format("%.2f", Double.valueOf(amount)) + "  </td><td>  " + String.format("%.2f", Double.valueOf(pzChargebackRecord.getChargebackAmount())) + "  </td><td>  " + "   success   </td><td>   " + "Exception while refund" + "  </td><td>  " + previousStatus + "  </td><td>  " + "chargeback </td></tr>");
                        continue;
                    }
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException::::", e);
            }
            catch (Exception e)
            {
                log.error("Exception::::", e);
            }
            log.error("returnMsg::::"+returnMsg.toString());
        }
        return returnMsg;
    }
}
