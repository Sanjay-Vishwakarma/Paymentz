package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Gpay.GpayPaymentzGateway;
import com.payment.Gpay.GpayUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by ThinkPadT410 on 2/2/2017.
 */
public class GPayFrontEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(GPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public GPayFrontEndServlet()
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
        transactionLogger.error("Inside GPayFrontEndServlet=============================");
        CommResponseVO commResponseVO = new CommResponseVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        TransactionManager transactionManager = new TransactionManager();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        Functions functions = new Functions();
        CommResponseVO transRespDetails = null;
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
        HttpSession session = req.getSession();
        ActionEntry entry = new ActionEntry();
        StringBuffer sb = new StringBuffer();

        String amount = "";
        String trackingId = "";
        String description = "";
        String status = "";
        String displayName = "";
        String toId = "";

        String remark = "";
        String accountId = "";
        String confirmationPage = "";
        String bankStatus = "";
        String pspid = "";
        String declineReason = "";
        String notificationUrl = "";
        String respStatus = "";
        String message = "";
        String dbStatus = "";
        String expDate = "";
        String expMonth = "";
        String expYear = "";
        String isService = "";
        String type = "";
        String ISOResp = "";
        String tmpl_amount = "";
        String currency = "";
        String authCode = "";
        String Currency = "";
        String firstname = "";
        String lastname = "";
        String CardHName = "";
        String CardNum = "";
        String firstsix = "";
        String lastfour = "";
        String ccnum = "";
        String redirectUrl = "";
        String orderDesc = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String paymentid = "";
        String transactionStatus = "";
        String transactionId = "";
        String confirmStatus = "";
        String responseStatus = "";
        String responseString = "";
        String custEmail = "";
        String terminalId = "";
        String tmpl_currency = "";
        String version = "";
        String eci = "";
        String ipAddress = "";
        String clKey = "";
        String logoName = "";
        String partnerName = "";
        String autoredirect = "";
        String responseAmount = "";
        String TXN_ID = "";
        String STATUS = "";
        String billingDesc = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName = "";

        String str = "";

        int result = 0;
        Enumeration enumeration = req.getParameterNames();
        Connection con = null;

        String xmlResponse = req.getParameter("params");
      //  type = req.getParameter("trackingId").split("_")[1];
        // type = req.getParameter("trackingId");

        transactionLogger.error("request type ::::" + req.getParameter("type"));
        transactionLogger.error("xmlResponse::::" + xmlResponse);


        BufferedReader br = req.getReader();
        StringBuilder responseMsg = new StringBuilder();

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            ;
            transactionLogger.error("---Key---" + key + "---Value---" + value);
        }
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }


        transactionLogger.error("-----GPayFrontEndServlet   response-----" + responseMsg);


            try
            {
                if (xmlResponse != null)
                {
                    String trackingStr = req.getParameter("trackingId");
                    transactionLogger.error("-----trackingStr in frontend ==============>" + trackingStr);
                    if(trackingStr != null && trackingStr.contains("_")){
                        // trackingId  = req.getParameter("trackingId").split("_")[0];
                        type        = req.getParameter("trackingId").split("_")[1];
                        transactionLogger.error("-----type in frontend==============>" + type);
                    }

                    Map<String, String> stringStringMap = GpayUtils.readGPayRedirectionXMLResponse(xmlResponse);

                trackingId = stringStringMap.get("ORef");
                bankStatus = stringStringMap.get("Result");
                pspid = stringStringMap.get("pspid");
                amount = stringStringMap.get("Value");
                declineReason = stringStringMap.get("ExtendedErr");
                ISOResp = stringStringMap.get("ISOResp");
                authCode = stringStringMap.get("AuthCode");
                Currency = stringStringMap.get("Currency");

                if (stringStringMap.containsKey("CardNum"))
                {
                    CardNum = stringStringMap.get("CardNum");
                    if (functions.isValueNull(CardNum))
                    {
                        if (CardNum.contains("******"))
                        {
                            firstsix = CardNum.substring(0, 6);
                            lastfour = CardNum.substring(CardNum.length() - 4, CardNum.length());
                        }
                        if (CardNum.contains(","))
                        {
                            firstsix = CardNum.substring(0, 6);
                            lastfour = CardNum.substring(CardNum.length() - 4, CardNum.length());
                        }
                    }
                }
                if (stringStringMap.containsKey("CardHName"))
                {
                    CardHName = stringStringMap.get("CardHName");
                    if (functions.isValueNull(CardHName))
                    {
                        String[] stringNameArr = CardHName.split("\\s+");
                        if (stringNameArr.length > 0)
                        {
                            firstname = stringNameArr[0];
                            lastname = stringNameArr[1];
                        }
                    }
                }
                transactionLogger.error("firstname----" + firstname);
                transactionLogger.error("lastname----" + lastname);
                transactionLogger.error("ffirstsix----" + firstsix);
                transactionLogger.error("lastfour----" + lastfour);
                transactionLogger.error("CardNum----" + CardNum);
                transactionLogger.error("CardHName----" + CardHName);
                transactionLogger.error("trackingId----" + trackingId);
                transactionLogger.error("authCode----" + authCode);
                transactionLogger.error("bankStatus----" + bankStatus);

                // pasuse for 5 second
                Thread.sleep(5000);
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                BlacklistManager blacklistManager = new BlacklistManager();
                BlacklistVO blacklistVO = new BlacklistVO();
                if (transactionDetailsVO != null)
                {
                    accountId = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    toId = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    amount = transactionDetailsVO.getAmount();
                    firstName = transactionDetailsVO.getFirstName();
                    lastName = transactionDetailsVO.getLastName();
                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    custEmail = transactionDetailsVO.getEmailaddr();
                    custId = transactionDetailsVO.getCustomerId();
                    paymentid = transactionDetailsVO.getPaymentId();
                    dbStatus = transactionDetailsVO.getStatus();
                    ipAddress = transactionDetailsVO.getIpAddress();
                    terminalId = transactionDetailsVO.getTerminalId();
                    tmpl_amount = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    version = transactionDetailsVO.getVersion();
                    ccnum = transactionDetailsVO.getCcnum();
                    if (functions.isValueNull(ccnum))
                    {
                        ccnum = PzEncryptor.decryptPAN(ccnum);
                        firstSix = functions.getFirstSix(ccnum);
                        lastFour = functions.getLastFour(ccnum);
                    }
                    if (functions.isValueNull(transactionDetailsVO.getName()))
                    {
                        cardHolderName = transactionDetailsVO.getName();
                    }
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                        String temp[] = expDate.split("/");

                        if (functions.isValueNull(temp[0]))
                        {
                            expMonth = temp[0];
                        }
                        if (functions.isValueNull(temp[1]))
                        {
                            expYear = temp[1];
                        }
                    }
                    eci = transactionDetailsVO.getEci();
                    MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                    merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toId);
                    StringBuffer dbBuffer = new StringBuffer();
                    transactionId = paymentid;


                    if (merchantDetailsVO != null)
                    {
                        clKey = merchantDetailsVO.getKey();
                        autoredirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                        isService = merchantDetailsVO.getIsService();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toId);
                    con = Database.getConnection();

                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    transactionLogger.error("dbStatus---> " + trackingId + " " + dbStatus);
                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {

                        GpayPaymentzGateway gpayPaymentzGateway = new GpayPaymentzGateway(accountId);
                        commResponseVO = (CommResponseVO) gpayPaymentzGateway.processQuery(trackingId, commRequestVO);

                        String inStatus = commResponseVO.getStatus();
                        String inRemark = commResponseVO.getRemark();
                        String inCode = commResponseVO.getAuthCode();
                        responseAmount = commResponseVO.getAmount();
                        TXN_ID = commResponseVO.getTransactionId();
                        String resCurrency = commResponseVO.getCurrency();
                        String error_code = commResponseVO.getAuthCode();
                        String creation_date = commResponseVO.getResponseTime();
                        String error_message = commResponseVO.getDescription();


                        transactionLogger.error("inquiry inStatus---> " + trackingId + " " + inStatus);
                        transactionLogger.error("inquiry amount---> " + trackingId + " " + responseAmount);
                        transactionLogger.error("inquiry inCode--->" + trackingId + " " + inCode);
                        transactionLogger.error("inquiry inRemark--->" + trackingId + " " + inRemark);
                        transactionLogger.error("inquiry transactionid--->" + trackingId + " " + TXN_ID);


                        if (functions.isValueNull(responseAmount))
                        {
                            Double compRsAmount = Double.valueOf(responseAmount);
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
                                transactionLogger.error("inside else Amount incorrect--->" + responseAmount);
                                remark = "Failed-IRA";
                                STATUS = "Failed";
                                amount = responseAmount;
                                blacklistVO.setVpaAddress(custId);
                                blacklistVO.setIpAddress(ipAddress);
                                blacklistVO.setEmailAddress(custEmail);
                                blacklistVO.setActionExecutorId(toId);
                                blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                                blacklistVO.setRemark("IncorrectAmount Trackingid : " + trackingId);
                                blacklistVO.setFirstSix(firstSix);
                                blacklistVO.setLastFour(lastFour);
                                blacklistVO.setName(cardHolderName);
                                blacklistManager.commonBlackListing(blacklistVO);
                            }
                        }

                        transactionLogger.error("RESPONSE STATUS--> " + trackingId + " " + STATUS);
                        if ("success".equalsIgnoreCase(STATUS))
                        {

                            if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                billingDesc = gatewayAccount.getDisplayName();
                                status = "success";
                                responseStatus = "Successful";
                                message = "Transaction Successful";
                                dbStatus = "authsuccessful";
                                commResponseVO.setDescription(error_message);
                                commResponseVO.setStatus(status);
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setTransactionId(TXN_ID);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setCurrency(resCurrency);
                                commResponseVO.setAuthCode(error_code);
                                commResponseVO.setErrorCode(error_code);
                                commResponseVO.setResponseTime(creation_date);


                                dbBuffer.append("update transaction_common set  paymentid='" + TXN_ID + "',templateamount='" + tmpl_amount + "',templatecurrency='" + tmpl_currency + "" + "',status='authsuccessful'" + " ,successtimestamp='" + functions.getTimestamp());
                                dbBuffer.append("',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                                transactionLogger.error("Update Query---" + dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            }
                            else//sale
                            {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                                commResponseVO = new CommResponseVO();
                                billingDesc = gatewayAccount.getDisplayName();
                                status = "success";
                                responseStatus = "Successful";
                                message = "Transaction Successful";
                                dbStatus = "capturesuccess";
                                commResponseVO.setDescription(error_message);
                                commResponseVO.setStatus(status);
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setTransactionId(TXN_ID);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setCurrency(resCurrency);
                                commResponseVO.setAuthCode(error_code);
                                commResponseVO.setErrorCode(error_code);
                                commResponseVO.setResponseTime(creation_date);


                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amount + "',templatecurrency='" + tmpl_currency + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                                dbBuffer.append("',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                                transactionLogger.error("Update Query---" + dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            }
                        }
                        else if ("Failed".equalsIgnoreCase(STATUS))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            status = "Failed";
                            message = remark;
                            responseStatus = remark;

                            dbStatus = "authfailed";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amount + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                            dbBuffer.append("',authorization_code='" + amount + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                            transactionLogger.error("Update Query---" + dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, ipAddress);
                        }
                        else
                        {
                            //pending
                            dbStatus = "authstarted";
                            status = "Pending";
                            message = "Pending";
                            responseStatus="Pending";
                            transactionLogger.error("inside GPAY pending condition --->" + STATUS + "--" + message);
                        }
                    }
                    else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        responseStatus = "Successful";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        responseStatus = "Successful";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status = "fail";
                        responseStatus = "failed";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if (!functions.isValueNull(message))
                            message = "Transaction Failed";
                        dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        status = "pending";
                        responseStatus = "pending";
                        message = "Transaction pending";
                        dbStatus = PZTransactionStatus.AUTH_STARTED.toString();

                    }

                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    // genericTransDetailsVO.setRedirectMethod(redirectMethod);
                    addressDetailsVO.setEmail(custEmail);
                    addressDetailsVO.setTmpl_amount(tmpl_amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);

                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setStatus(message);

                    commonValidatorVO.setReason(message);

                    if (functions.isValueNull(custEmail))
                    {
                        addressDetailsVO.setEmail(custEmail);
                    }
                    if (functions.isValueNull(firstName))
                    {
                        addressDetailsVO.setFirstname(firstName);
                    }

                    if (functions.isValueNull(lastName))
                    {
                        addressDetailsVO.setLastname(lastName);
                    }
                    addressDetailsVO.setTmpl_amount(tmpl_amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setEci(eci);
                    transactionUtility.setToken(commonValidatorVO, dbStatus);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBillingDesc(billingDesc);
                        transactionDetailsVO1.setTransactionMode("3D");
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    }
                    transactionLogger.debug("autoredirect in ---" + trackingId + " " + autoredirect);
                    if ("Y".equalsIgnoreCase(autoredirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, res, responseStatus, billingDesc);
                    }
                    else
                    {
                        session.setAttribute("ctoken", ctoken);

                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", responseStatus);
                        req.setAttribute("displayName", billingDesc);
                        req.setAttribute("remark", message);

                        confirmationPage = "";
                        version = (String) session.getAttribute("version");
                        transactionLogger.error("version===>" + trackingId + " " + version);
                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        {
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        }
                        else
                        {
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        }
                        session.invalidate();
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }
                }

            }
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException---->" + e);
            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError---->" + se);
            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                transactionLogger.error("InterruptedException---->" + e);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                Database.closeConnection(con);
            }

    }
}
