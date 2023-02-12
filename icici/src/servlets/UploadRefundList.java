package servlets;
import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.dao.BlacklistDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.response.PZChargebackRecord;

import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.util.List;

public class UploadRefundList extends HttpServlet
{
    private static Logger log = new Logger(UploadRefundList.class.getName());
    public StringBuffer refund(List<PZChargebackRecord> vTransactions, String userName) throws SystemError
    {
        TransactionDAO transactionDAO = new TransactionDAO();
        Functions functions=new Functions();
        StringBuffer returnMsg = new StringBuffer();
        Connection con = Database.getConnection();
        BlacklistDAO blacklistDAO = new BlacklistDAO();

        String trackingId = "";
        String paymentId = "";
        String accountId = "";
        String captureAmount = "";
        String toId = "";
        String status = "";
        String fromtype = "";
        String refund_reason = "";
        int successCounter = 0;
        int failureCounter = 0;

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorId("0");
        auditTrailVO.setActionExecutorName(userName);
        auditTrailVO.setCbReason("chargeback");
        ActionEntry entry = new ActionEntry();

        returnMsg.append("<tr class=\"tdstyle texthead\"><td>  Tracking Id  </td><td> Merchant Id  </td><td>   Account Id  </td><td>  Reversed Amount  </td><td>    Status   </td></tr>");

        for(PZChargebackRecord pzChargebackRecord : vTransactions)
        {
            try
            {
                if (functions.isValueNull(pzChargebackRecord.getTrackingid()))
                {
                    trackingId = pzChargebackRecord.getTrackingid();
                }
                else if(!functions.isValueNull(pzChargebackRecord.getTrackingid()))
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>     -    </td><td>     -     </td><td>     -    </td><td>    TrackingId is  missing   </td></tr>");
                    failureCounter++;
                    continue;
                }

                log.error("TrackingId--- " + trackingId);

                TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingId);

                if(!functions.isValueNull(transactionDetailsVO.getTrackingid()))
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>     -    </td><td>     -     </td><td>     -    </td><td>   Invalid TrackingId  </td></tr>");

                    failureCounter++;
                    continue;
                }


                if(functions.isValueNull(transactionDetailsVO.getCaptureAmount()))
                {
                    captureAmount = transactionDetailsVO.getCaptureAmount();
                }

                if(functions.isValueNull(transactionDetailsVO.getFromtype()))
                {
                    fromtype = transactionDetailsVO.getFromtype();
                }

                if(functions.isValueNull(transactionDetailsVO.getStatus()))
                {
                    status = transactionDetailsVO.getStatus();
                }

                if(functions.isValueNull(transactionDetailsVO.getPaymentId()))
                {
                    paymentId = transactionDetailsVO.getPaymentId();
                }

                if(functions.isValueNull(transactionDetailsVO.getAccountId()))
                {
                    accountId = transactionDetailsVO.getAccountId();
                }

                if(functions.isValueNull(transactionDetailsVO.getToid()))
                {
                    toId = transactionDetailsVO.getToid();
                }

                if(functions.isValueNull(transactionDetailsVO.getDescription()))
                {
                    refund_reason = transactionDetailsVO.getDescription();
                }

                log.error("captureAmount ===== " + captureAmount);
                log.error("accountId ===== " + accountId);
                log.error("toId ===== " + toId);
                log.error("status ===== " + status);

                if(!status.equalsIgnoreCase("capturesuccess"))
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>     -    </td><td>     -     </td><td>     -    </td><td>   Only successful transaction can be chargeback   </td></tr>");

                    failureCounter++;
                    continue;
                }

                if(Double.parseDouble(captureAmount) <= 0.00)
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>     -    </td><td>     -     </td><td>     -    </td><td>   Refund Amount cannot be less than 0   </td></tr>");

                    failureCounter++;
                    continue;
                }

                AbstractPaymentGateway paymentGateway   = AbstractPaymentGateway.getGateway(accountId);
                CommRequestVO commRequestVO = new CommRequestVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setAmount(captureAmount);
                commTransactionDetailsVO.setPaymentId(paymentId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setOrderDesc(refund_reason);
                //commRequestVO.setTransDetailsVO();
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                CommResponseVO refundResponse = (CommResponseVO) paymentGateway.processRefund(String.valueOf(trackingId), commRequestVO);
                String responseStatus = refundResponse != null ? refundResponse.getStatus() : "";

                if (functions.isValueNull(responseStatus) && (responseStatus.equalsIgnoreCase("success")))
                {
                    StringBuffer dbBuffer = new StringBuffer();

                    dbBuffer.append("update transaction_common set status='chargeback'" + " where trackingid = " + trackingId);

                    Database.executeUpdate(dbBuffer.toString(), con);

                    if("UPI".equalsIgnoreCase(transactionDetailsVO.getCardtype()) && functions.isValueNull(transactionDetailsVO.getCustomerId()))
                    {
                        blacklistDAO.addCustomerVPAAddressBatch(transactionDetailsVO.getCustomerId(), "chargeback", auditTrailVO);
                    }

                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + toId + "  </td><td>  " + accountId + "  </td><td>  " + String.format("%.2f", Double.valueOf(captureAmount)) + "  </td><td>  " + "  Transaction refunded and chargeback successful   </td></tr>");
                    int actionEntry2 = entry.genericActionEntry(trackingId,captureAmount,ActionEntry.ACTION_CREDIT,ActionEntry.STATUS_CREDIT,null,fromtype,null,auditTrailVO);

                    int actionEntry = entry.genericActionEntry(trackingId,captureAmount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,ActionEntry.STATUS_CHARGEBACK_RACEIVED,null,fromtype, null,auditTrailVO);

                    successCounter++;

                }
                else
                {
                    returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + "" + "  </td><td>  " + "-" + "  </td><td>  " + "-" + "  </td><td>  " + "   chargeback failed   </td></tr>");
                    failureCounter++;
                    continue;
                }
            }
            catch (Exception e)
            {
                log.error("Exception::::", e);
                returnMsg.append("<tr class='report'><td>" + trackingId + "  </td><td>  " + "-" + "  </td><td>  " + "-" + "  </td><td>  " + "-" + "  </td><td>  " + "   Exception occurred   </td></tr>");
                failureCounter++;
            }
        }

        return returnMsg;
    }

}