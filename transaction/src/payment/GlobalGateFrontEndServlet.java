package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.globalgate.GlobalGatePaymentGateway;
import com.payment.globalgate.GlobalGateUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by ThinkPadT410 on 2/2/2017.
 */
public class GlobalGateFrontEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(GlobalGateFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public GlobalGateFrontEndServlet()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("-----Entering into GlobalgateFrontEndServlet-----");
        HttpSession session = req.getSession(true);
        Functions functions = new Functions();

        Enumeration enumeration=req.getParameterNames();
        while (enumeration.hasMoreElements()){
            String key=(String)enumeration.nextElement();
            String value=req.getParameter(key);
            transactionLogger.error("key="+key+"-----value="+value);
        }
        String xmlResponse = req.getParameter("params");
        transactionLogger.error("xmlResponse::::" + xmlResponse);

        if (xmlResponse != null)
        {

            TransactionManager transactionManager = new TransactionManager();
            CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
            MerchantDAO merchantDAO = new MerchantDAO();
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
            GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
            TransactionUtility transactionUtility = new TransactionUtility();
            StringBuffer sb = new StringBuffer();
            ActionEntry entry = new ActionEntry();
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
            CommResponseVO commResponseVO = new CommResponseVO();
            Connection con = null;

            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String currency = "";

            String autoredirect = "";
            String powerBy = "";
            String displayName = "";
            String logoName = "";
            String partnerName = "";

            String amount = "";
            String trackingId = "";
            String status = "";

            String bankTransactionStatus = "";
            String resultCode = "";
            String email = "";

            String tmpl_amt = "";
            String tmpl_currency = "";
            String firstName = "";
            String lastName = "";
            String paymodeid = "";
            String cardtypeid = "";
            String custId = "";
            String ccnum="";
            String notificationUrl="";
            String message="";
            String version="";
            String dbStatus="";
            String pspid="";
            String captureAmount="";
            String declineReason="";
            String remark="";
            String respStatus="";
            String billingDesc="";
            String confirmStatus="";
            String firstSix="";
            String lastFour="";
            String expYear="";
            String isService="";
            String respAmount = "";
            String STATUS = "";
            String cardHolderName = "";
            String ipAddress = Functions.getIpAddress(req);
            int result=0;

            try
            {
                Map<String, String> stringStringMap = GlobalGateUtils.readGlobalgateRedirectionXMLResponse(xmlResponse);
                trackingId = stringStringMap.get("ORef");
                resultCode = stringStringMap.get("Result");
                pspid = stringStringMap.get("pspid");
                captureAmount = stringStringMap.get("Value");
                declineReason = stringStringMap.get("ExtendedErr");



                transactionLogger.error("pspid----" +pspid);
                transactionLogger.error("ORef----" +trackingId);
                transactionLogger.error("Result----" +resultCode);

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                BlacklistManager blacklistManager           = new BlacklistManager();
                BlacklistVO blacklistVO                     = new BlacklistVO();
                if (transactionDetailsVO != null)
                {
                    toid = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    accountId = transactionDetailsVO.getAccountId();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    amount = transactionDetailsVO.getAmount();
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    firstName = transactionDetailsVO.getFirstName();
                    lastName = transactionDetailsVO.getLastName();
                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    custId = transactionDetailsVO.getCustomerId();
                    notificationUrl=transactionDetailsVO.getNotificationUrl();
                    version=transactionDetailsVO.getVersion();
                    dbStatus=transactionDetailsVO.getStatus();
                    ccnum        = transactionDetailsVO.getCcnum();
                    if (functions.isValueNull(ccnum))
                    {
                        ccnum    = PzEncryptor.decryptPAN(ccnum);
                        firstSix = functions.getFirstSix(ccnum);
                        lastFour = functions.getLastFour(ccnum);
                    }
                    if(functions.isValueNull(transactionDetailsVO.getName())){
                        cardHolderName=transactionDetailsVO.getName();
                    }
                    if(!functions.isValueNull(captureAmount)){
                        captureAmount = amount;
                    }
                    transactionLogger.debug("amount-----"+captureAmount);

                    if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                   /* if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                        expDate = Encryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                        String temp[] = expDate.split("/");

                        if (functions.isValueNull(temp[0]))
                        {
                            expMonth = temp[0];
                        }
                        if (functions.isValueNull(temp[1]))
                        {
                            expYear = temp[1];
                        }
                    }*/

                    transactionLogger.debug("num------"+ccnum);
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    email = transactionDetailsVO.getEmailaddr();
                    powerBy = merchantDetailsVO.getPoweredBy();

                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toid);

                    transactionLogger.error("tmpl_amount===========>"+tmpl_amt);
                    transactionLogger.error("tmpl_currency===========>"+tmpl_currency);


                    if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                    {
                        GlobalGatePaymentGateway globalGatePaymentGateway = new GlobalGatePaymentGateway(accountId);
                        CommRequestVO commRequestVO = new CommRequestVO();
                        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

                        commTransactionDetailsVO.setOrderId(trackingId);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);


                        commResponseVO = (CommResponseVO) globalGatePaymentGateway.processInquiry(commRequestVO);
                        transactionLogger.error("After Inquery Call of Transaction==========" + trackingId + " status " + commResponseVO.getStatus());


                        String inStatus = commResponseVO.getStatus();
                        String transactionId = commResponseVO.getTransactionId();
                        remark = commResponseVO.getRemark();
                        String authcode = commResponseVO.getAuthCode();
                        respAmount = commResponseVO.getAmount();
                        transactionLogger.error("responseStatus===========>" + inStatus);
                        transactionLogger.error("transactionId===========>" + transactionId);
                        transactionLogger.error("remark===========>" + remark);
                        transactionLogger.error("authcode===========>" + authcode);
                        transactionLogger.error("billingDesc===========>" + billingDesc);

                       /* if (functions.isValueNull(commResponseVO.getAmount()))
                        {
                            respAmount = commResponseVO.getAmount();
                        }
                        else
                        {
                            respAmount = amount;
                        }*/

                        if (functions.isValueNull(respAmount))
                        {
                            Double compRsAmount = Double.valueOf(respAmount);
                            Double compDbAmount = Double.valueOf(amount);

                            transactionLogger.error("response amount --->" + compRsAmount);
                            transactionLogger.error(" DB Amount--->" + compDbAmount);

                            if (compDbAmount.equals(compRsAmount))
                            {
                                STATUS = commResponseVO.getStatus();
                                amount = commResponseVO.getAmount();
                                remark = commResponseVO.getRemark();
                            }
                            else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                            {
                                transactionLogger.error("inside else Amount incorrect--->" + respAmount);
                                remark = "Failed-IRA";
                                STATUS = "authfailed";
                                amount = respAmount;
                                blacklistVO.setVpaAddress(custId);
                                blacklistVO.setIpAddress(ipAddress);
                                blacklistVO.setEmailAddress(email);
                                blacklistVO.setActionExecutorId(toid);
                                blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                                blacklistVO.setRemark("IncorrectAmount Trackingid : " + trackingId);
                                blacklistVO.setFirstSix(firstSix);
                                blacklistVO.setLastFour(lastFour);
                                blacklistVO.setName(cardHolderName);
                                blacklistManager.commonBlackListing(blacklistVO);
                            }
                        }
                        StringBuffer dbBuffer = new StringBuffer();
                        if("success".equalsIgnoreCase(STATUS))
                        {
                            if ("OK".equals(resultCode) || "CAPTURED".equals(resultCode) || "APPROVED".equals(resultCode))
                            {
                              transactionLogger.error("Resultcode indise if====>" + resultCode);
                              if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                              {
                                  notificationUrl = transactionDetailsVO.getNotificationUrl();
                                transactionLogger.error("--- in Auth , authstarted---");
                                sb.append("update transaction_common set ");
                                respStatus = "Successful";
                                dbStatus = "authsuccessful";
                                // remark = resultCode;
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                commResponseVO.setDescription(remark);
                                //commResponseVO.setDescription("Transaction Successful");
                                commResponseVO.setAuthCode(authcode);
                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(authcode);


                                sb.append("status='authsuccessful'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_SUCCESS.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + transactionId + "',successtimestamp='" + functions.getTimestamp() + "',authorization_code='" + authcode + "' where trackingid =" + trackingId + "");
                                transactionLogger.error("querySuccess ===" + sb.toString());
                                con = Database.getConnection();
                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                              }
                              else //SALE
                              {
                                transactionLogger.error("Resultcode indise if====>" + resultCode);
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                transactionLogger.error("--- in Sale , capturesuccess ---");
                                sb.append("update transaction_common set ");
                                respStatus = "Successful";
                                // remark = resultCode;
                                dbStatus = "capturesuccess";
                                transactionDetailsVO.setBillingDesc(billingDesc);
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(remark);
                                //commResponseVO.setDescription("Transaction Successful");
                                commResponseVO.setDescription(remark);
                                commResponseVO.setAuthCode(authcode);
                                commResponseVO.setTmpl_Amount(tmpl_amt);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(authcode);
                                sb.append(" captureamount='" + amount + "'");
                                sb.append(", status='capturesuccess'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                sb.append(" ,remark='" + remark + "',paymentid='" + transactionId + "',successtimestamp='" + functions.getTimestamp() + "',authorization_code='" + authcode + "' where trackingid =" + trackingId + "");
                                con = Database.getConnection();
                                transactionLogger.error("query=========>" + sb);
                                result = Database.executeUpdate(sb.toString(), con);
                                if (result != 1)
                                {
                                    Database.rollback(con);
                                    //Mail.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                    asynchronousMailService.sendAdminMail("Exception while Updating status", "Exception has occured while updating status for tracking id=" + trackingId + "Auth message=" + status);
                                }
                              }
                            }
                        }
                        else if("fail".equalsIgnoreCase(inStatus))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            confirmStatus = "N";
                            respStatus="failed";
                            status = "failed";
                            dbStatus = "authfailed";
                            billingDesc = "";
                            displayName = "";
                            dbBuffer.append("update transaction_common set paymentid='" + commResponseVO.getResponseHashInfo() + "',failuretimestamp='" + functions.getTimestamp() + "',status='authfailed',remark = '" + message + "'");
                            dbBuffer.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);


                        }
                        else
                        {
                            respStatus = "pending";
                            status = "pending";
                            message = "Transaction Pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                        }


                       // AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), respStatus, commResponseVO.getRemark(), billingDesc);

                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), respStatus, commResponseVO.getRemark(), billingDesc);

                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        }
                        else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            if(functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            dbStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "fail";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else if (!functions.isValueNull(message))
                                message = "Transaction Failed";
                            dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        else
                        {
                            status = "pending";
                            message = "Transaction pending";
                            dbStatus = PZTransactionStatus.AUTH_STARTED.toString();

                        }
                    }
                    commonValidatorVO.setTrackingid(trackingId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setEmail(email);
                    cardDetailsVO.setCardNum(ccnum);
                    /*cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);*/
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);

                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    transactionUtility.setToken(commonValidatorVO, status);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setTransactionMode("3D");
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message,"");
                    }

                    if ("Y".equalsIgnoreCase(autoredirect))
                    {
                        transactionLogger.error("respStatus in Y---" + status);
                        transactionUtility.doAutoRedirect(commonValidatorVO, res, respStatus, billingDesc);
                    }
                    else
                    {

                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", respStatus);
                        req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());

                        String confirmationPage = "";
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }

                }
            }
            catch (SQLException e)
            {
                transactionLogger.error("SQL Exception in GlobalGateFrontEndServlet---", e);
            }
            catch (SystemError e)
            {
                transactionLogger.error("System Exception in GlobalGateFrontEndServlet---", e);
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("NoSuchAlgorithm Exception in GlobalGateFrontEndServlet---", e);
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("SQL Exception in GlobalGateFrontEndServlet---", e);
            }
            catch (Exception e)
            {
                transactionLogger.error("SQL Exception in GlobalGateFrontEndServlet---", e);
            }finally
            {
                Database.closeConnection(con);
            }
        }
    }
}
