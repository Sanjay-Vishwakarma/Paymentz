package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.vouchermoney.VoucherMoneyResponse;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nikita on 7/7/2017.
 */
public class VoucherMoneyFrontEndServlet extends PzServlet
{
    private  TransactionLogger transactionLogger = new TransactionLogger(VoucherMoneyFrontEndServlet.class.getName());
    List<String> iplist = new ArrayList<String>();
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
    {
        doService(req,res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res)
    {
        iplist.add("35.156.87.13");
        iplist.add("35.158.0.227");
        iplist.add("35.156.187.139");
        iplist.add("52.58.176.85");

        iplist.add("127.0.0.1");
        //transactionLogger.error("Entering in VoucherMoneyFrontEndServlet::::");
        HttpSession session = req.getSession(true);
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Functions functions = new Functions();

        VoucherMoneyResponse voucherMoneyResponse = null;
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        ActionEntry entry = new ActionEntry();
        Transaction transaction = new Transaction();
        StringBuffer dbBuffer = new StringBuffer();
        String trackingId = "";

        if (!req.getParameterMap().isEmpty())
        {
            //transactionLogger.error("Status and trackingid from request---"+req.getParameter("STATUS")+"-"+req.getParameter("trackingId"));
            String transactionStatus = ESAPI.encoder().encodeForSQL(me, req.getParameter("STATUS"));

            //transactionLogger.error("trackingid and status---"+trackingId+"-"+transactionStatus);

            if(!functions.isNumericVal(req.getParameter("trackingId")))
                trackingId = "";
            else
                trackingId = ESAPI.encoder().encodeForSQL(me, req.getParameter("trackingId"));

            //Transaction Details
            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String currency = "";
            String amount = "";
            String remark = "";
            String status = "";
            String captureAmount="";

            //Merchant Details
            String clkey = "";
            String checksumAlgo = "";
            String autoredirect = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";
            String displayName = "";
            String dbStatus = "";
            String email = "";

            String paymodeid = "";
            String cardtypeid= "";
            String previousStatus = "";
            String commissionPaid = "";
            String commissionCurrency = "";

            String tmpl_amt = "";
            String tmpl_currency = "";
            String version="";
            String fStatus = "";

            Connection con = null;
            try
            {
                //transactionLogger.error("before fetching from common---");
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                //transactionLogger.error("after fetching from common---");
                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                amount = transactionDetailsVO.getAmount();
                captureAmount = transactionDetailsVO.getCaptureAmount();
                email = transactionDetailsVO.getEmailaddr();
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                clkey = merchantDetailsVO.getKey();
                checksumAlgo = merchantDetailsVO.getChecksumAlgo();
                autoredirect = merchantDetailsVO.getAutoRedirect();
                isPowerBy = merchantDetailsVO.getIsPoweredBy();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                version=transactionDetailsVO.getVersion();

                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();

                previousStatus = transactionDetailsVO.getStatus();


                transactionLogger.debug("tmpl_amt-----"+tmpl_amt);
                transactionLogger.debug("tmpl_currency-----"+tmpl_currency);

                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);


                if("authsuccessful".equalsIgnoreCase(previousStatus))
                {
                    //dbBuffer.append("update transaction_common set ");
                    if ("SUCCESS".equalsIgnoreCase(transactionStatus))
                    {
                        //transactionLogger.error("Inside success condition for Voucher---" + transactionStatus);
                        displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        remark = "Processed Successfully";
                        dbStatus = "Successful";
                        fStatus = "capturesuccess";
                        //dbBuffer.append(" status='capturesuccess',captureamount='"+amount+"'");

                        voucherMoneyResponse = new VoucherMoneyResponse();
                        voucherMoneyResponse.setDescriptor(displayName);
                        voucherMoneyResponse.setAmount(amount);
                        voucherMoneyResponse.setTransactionType("sale");
                        voucherMoneyResponse.setRemark(remark);
                        voucherMoneyResponse.setTransactionStatus(dbStatus);
                        voucherMoneyResponse.setStatus(transactionStatus);
                        voucherMoneyResponse.setCurrency(currency);
                        voucherMoneyResponse.setTmpl_Amount(tmpl_amt);
                        voucherMoneyResponse.setTmpl_Currency(tmpl_currency);
                        transactionLogger.debug("-----tmplAmount----"+voucherMoneyResponse.getTmpl_Amount());
                        transactionLogger.debug("-----tmplCurrency----"+voucherMoneyResponse.getTmpl_Currency());

                        paymentManager.updateTransactionForCommon(voucherMoneyResponse, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, trackingId, auditTrailVO, "transaction_common", "", "", null, voucherMoneyResponse.getRemark());
                        //transactionLogger.error("after update main and detail table from success---" + transactionStatus);
                        //entry.actionEntryForVoucherMoney(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, voucherMoneyResponse, null, auditTrailVO);
                        //statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                    }
                    /*else if ("AuthFailed".equalsIgnoreCase(transactionStatus))
                    {
                        transactionLogger.error("Inside failed condition for Voucher---" + transactionStatus);
                        remark = "Transaction Failed";
                        dbStatus = "Failed";

                        voucherMoneyResponse = new VoucherMoneyResponse();
                        voucherMoneyResponse.setAmount(amount);
                        voucherMoneyResponse.setTransactionType("sale");
                        voucherMoneyResponse.setRemark(remark);
                        voucherMoneyResponse.setTransactionStatus("authfailed");
                        voucherMoneyResponse.setStatus(dbStatus);

                        //dbBuffer.append(" status='authfailed',amount='"+amount+"'");
                        //dbBuffer.append(" amount='" + amount + "'");

                        paymentManager.updateTransactionForVoucherMoney(voucherMoneyResponse, ActionEntry.STATUS_AUTHORISTION_FAILED, trackingId, auditTrailVO, "transaction_common", "", "", null, voucherMoneyResponse.getRemark());
                        //entry.actionEntryForVoucherMoney(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, voucherMoneyResponse, null, auditTrailVO);
                        //statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());
                    }*/

                   /* dbBuffer.append(" ,remark='" + remark + "' where trackingid =" + trackingId + "");
                    con = Database.getConnection();
                    int result = Database.executeUpdate(dbBuffer.toString(), con);

                    if (result != 1)
                    {
                        Database.rollback(con);
                        Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                    }*/
                }
                else if("authstarted".equalsIgnoreCase(previousStatus))//rejected by BancEnd and VM
                {
                    if ("FAILURE".equalsIgnoreCase(transactionStatus))
                    {
                        //transactionLogger.error("Inside failed condition for Voucher---" + transactionStatus);
                        remark = "Transaction Failed";
                        dbStatus = "Failed";
                        fStatus = "authfailed";

                        voucherMoneyResponse = new VoucherMoneyResponse();
                        voucherMoneyResponse.setAmount(amount);
                        voucherMoneyResponse.setTransactionType("sale");
                        voucherMoneyResponse.setRemark(remark);
                        voucherMoneyResponse.setTransactionStatus("authfailed");
                        voucherMoneyResponse.setStatus(dbStatus);
                        voucherMoneyResponse.setCurrency(currency);
                        voucherMoneyResponse.setTmpl_Amount(tmpl_amt);
                        voucherMoneyResponse.setTmpl_Currency(tmpl_currency);

                        paymentManager.updateTransactionForCommon(voucherMoneyResponse, ActionEntry.STATUS_AUTHORISTION_FAILED, trackingId, auditTrailVO, "transaction_common", "", "", null, voucherMoneyResponse.getRemark());
                        //transactionLogger.error("after update main and detail table from success---" + transactionStatus);
                    }
                }
                String respstatus ="";
                String resStatus = "";
                String billingDesc = "";
                if (transactionStatus != null && "Success".equalsIgnoreCase(transactionStatus))
                {
                    respstatus = "Transaction Successful";
                    resStatus = "Y";
                    billingDesc = displayName;
                }
                else
                {
                    respstatus = "Transaction Failed";
                    resStatus = "N";
                }

                String custId = "";
                String custEmail = "";
                String custBankId = "";

                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);

                if (functions.isValueNull(custEmail))
                    addressDetailsVO.setEmail(custEmail);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);

                merchantDetailsVO.setPoweredBy(isPowerBy);

                HashMap extDetails = paymentManager.getExtnDetailsforNotification(trackingId,"VM");

                if(extDetails.size()>0)
                {
                    custId = (String)extDetails.get("customerId");
                    custBankId = (String)extDetails.get("customerBankId");
                    commissionPaid = (String)extDetails.get("commissionPaidToUser");
                    commissionCurrency = (String)extDetails.get("commPaidToUserCurrency");
                }

                transactionLogger.error("sending notification from FrontEnd---"+transactionDetailsVO.getNotificationUrl()+"---"+trackingId);
                if(functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                {
                    transactionLogger.error("inside sending notification from FrontEnd---"+transactionDetailsVO.getNotificationUrl());

                    transactionDetailsVO.setTemplateamount(tmpl_amt);
                    transactionDetailsVO.setTemplatecurrency(tmpl_currency);

                    transactionDetailsVO.setCommissionToPay(commissionPaid);
                    transactionDetailsVO.setCommCurrency(commissionCurrency);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,fStatus,remark,"VM");

                }

                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setCustomerBankId(custBankId);
                if(functions.isValueNull(custId))
                    commonValidatorVO.setCustomerId(custId);
                else
                    commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId() );
                commonValidatorVO.setCommissionPaidToUser(commissionPaid);
                commonValidatorVO.setCommPaidToUserCurrency(commissionCurrency);
                commonValidatorVO.setTrackingid(trackingId);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    //transactionLogger.error("before redirecting to merchant---" + transactionStatus);
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionUtility.doAutoRedirect(commonValidatorVO,res,dbStatus,billingDesc);
                    //transactionLogger.error("after redirecting to merchant---" + transactionStatus);
                }
                else
                {

                    req.setAttribute("responceStatus", respstatus);
                    req.setAttribute("displayName", displayName);
                    req.setAttribute("remark", remark);
                    req.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";


                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                    session.invalidate();
                }
                if(functions.isValueNull(email) && functions.isValueNull(trackingId))
                {
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), transactionStatus, remark, displayName);
                }

            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException----", e);
            }
            catch (ServletException e)
            {
                transactionLogger.error("ServletException----", e);
            }
            catch (IOException e)
            {
                transactionLogger.error("IOException----", e);
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError----", systemError);
            }
        }
    }
}