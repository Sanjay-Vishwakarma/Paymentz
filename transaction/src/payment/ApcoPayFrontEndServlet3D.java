package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.apco.core.ApcoPayPaymentProcess;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.apco.core.ApcoPaymentGateway;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
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
public class ApcoPayFrontEndServlet3D extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ApcoPayFrontEndServlet3D.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public ApcoPayFrontEndServlet3D()
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
        transactionLogger.error("----enter into doPost-----");
        HttpSession session = req.getSession(true);
        Functions functions = new Functions();

        String key = "";
        String value = "";
        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            key = (String) enumeration.nextElement();
            value = req.getParameter(key);
            transactionLogger.debug("Key----" + key + "---Value---" + value);
        }

        transactionLogger.debug("Key----" + key + "---Value---" + value);
        String xmlResponse = req.getParameter("params");
        transactionLogger.debug("xmlResponse::::" + xmlResponse);

        if (xmlResponse != null)
        {

            ActionEntry entry                       = new ActionEntry();
            AuditTrailVO auditTrailVO               = new AuditTrailVO();
            TransactionManager transactionManager   = new TransactionManager();
            CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            TransactionUtility transactionUtility       = new TransactionUtility();
            GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
            MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
            MerchantDAO merchantDAO                     = new MerchantDAO();
            GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
            ApcoPayPaymentProcess paymentProcess        = new ApcoPayPaymentProcess();
            StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();

            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String currency = "";
            String clkey = "";
            String checksumAlgo = "";
            String checksum = "";
            String autoredirect = "";
            String isService = "";
            String displayName = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";

            String amount = "";
            String trackingId = "";
            String status = "";
            String remark = "";

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
            String transactionStatus = "";
            String confirmStatus = "";
            String responceStatus = "";
            String transactionId = "";
            String message = "";
            String billingDesc = "";
            String dbStatus = "";
            String eci = "";
            String paymentid = "";
            String cnum="";
            String expDate="";
            String notificationUrl="";
            String version="";
            String terminalid="";
            String street="";
            String zip="";
            String state="";
            String city="";
            String country="";
            String telno="";
            String RedirectMethod="";
            String telcc="";
            String ipAddress = Functions.getIpAddress(req);


            Connection con = null;
            try
            {
                Map<String, String> stringStringMap = ApcoPayUtills.readApcopayRedirectionXMLReponse(xmlResponse);
                trackingId = stringStringMap.get("ORef");
                resultCode = stringStringMap.get("Result");


                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {
                    toid            = transactionDetailsVO.getToid();
                    description     = transactionDetailsVO.getDescription();
                    redirectUrl     = transactionDetailsVO.getRedirectURL();
                    accountId       = transactionDetailsVO.getAccountId();
                    orderDesc       = transactionDetailsVO.getOrderDescription();
                    currency        = transactionDetailsVO.getCurrency();
                    amount          = transactionDetailsVO.getAmount();
                    tmpl_amt        = transactionDetailsVO.getTemplateamount();
                    tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                    firstName       = transactionDetailsVO.getFirstName();
                    lastName        = transactionDetailsVO.getLastName();
                    paymodeid       = transactionDetailsVO.getPaymodeId();
                    cardtypeid      = transactionDetailsVO.getCardTypeId();
                    custId          = transactionDetailsVO.getCustomerId();
                    paymentid       = transactionDetailsVO.getPaymentId();
                    dbStatus        = transactionDetailsVO.getStatus();
                    version         = transactionDetailsVO.getVersion();
                    terminalid      = transactionDetailsVO.getTerminalId();
                    zip             = transactionDetailsVO.getZip();
                    street          = transactionDetailsVO.getStreet();
                    state           = transactionDetailsVO.getState();
                    city            = transactionDetailsVO.getCity();
                    country         = transactionDetailsVO.getCountry();
                    telcc           = transactionDetailsVO.getTelcc();
                    telno           = transactionDetailsVO.getTelno();
                    RedirectMethod  = transactionDetailsVO.getRedirectMethod();

                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        cnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                        expDate=PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    }
 

                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    isPowerBy = merchantDetailsVO.getIsPoweredBy();
                    logoName = merchantDetailsVO.getLogoName();
                    isService = merchantDetailsVO.getIsService();
                    partnerName = merchantDetailsVO.getPartnerName();
                    email = transactionDetailsVO.getEmailaddr();

                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toid);

                    displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                    ApcoPaymentGateway apcoPaymentGateway = new ApcoPaymentGateway(accountId);
                    CommRequestVO commRequestVO = new CommRequestVO();
                    CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                    CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                    commAddressDetailsVO.setCardHolderIpAddress(ipAddress);
                    CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

                    commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                    commTransactionDetailsVO.setOrderId(trackingId);
                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                    commRequestVO.setCommMerchantVO(commMerchantVO);

                    String transType = "Sale";
                    CommResponseVO genericResponseVO = null;
                    transactionLogger.error("dbStatus-----" + dbStatus + "---" + trackingId);
                    transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    dbStatus = transactionDetailsVO.getStatus();
                    transactionLogger.debug("dbStatus-----" + dbStatus);
                    if (dbStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()))
                    {
                        if(resultCode.equalsIgnoreCase("OK") || resultCode.equalsIgnoreCase("CAPTURED") || resultCode.equalsIgnoreCase("APPROVED") || resultCode.equalsIgnoreCase("PROCESSED"))
                        {
                        if ("N".equals(isService) || (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                        {
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, genericResponseVO, commRequestVO, auditTrailVO, null);
                            genericResponseVO = (CommResponseVO) apcoPaymentGateway.processInquiry(commRequestVO);
                            transType = "Auth";
                        }
                        else
                        {
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, genericResponseVO, commRequestVO, auditTrailVO, null);
                            genericResponseVO = (CommResponseVO) apcoPaymentGateway.processInquiry(commRequestVO);
                        }

                        transactionStatus = genericResponseVO.getStatus();
                        transactionId = genericResponseVO.getTransactionId();
                        message = genericResponseVO.getDescription();

                        TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(trackingId);
                        String uDBStatus = transactionDetailsVO1.getStatus();
                        transactionLogger.error("Updated DB status---"+uDBStatus+"---"+trackingId);
                        if (genericResponseVO != null && uDBStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            StringBuffer dbBuffer = new StringBuffer();
                            if ("success".equals(transactionStatus))
                            {
                                status = "success";
                                confirmStatus = "Y";
                                responceStatus = "Successful";
                                billingDesc = displayName;
                                if ("Sale".equalsIgnoreCase(transType))
                                {
                                    genericResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "',remark='" + message + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, genericResponseVO, commRequestVO, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                }
                                else
                                {
                                    genericResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='" + eci + "',remark='" + message + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid = " + trackingId);
                                    con = Database.getConnection();
                                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, genericResponseVO, commRequestVO, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                }
                            }
                            else
                            {
                                confirmStatus = "N";
                                status = "fail";
                                responceStatus = "Failed(" + message + ")";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "' ,remark='" + message + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, genericResponseVO, auditTrailVO, ipAddress);
                            }

                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                        }
                        else
                        {
                            if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(uDBStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(uDBStatus))
                            {
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                status = "success";
                                message = "Transaction Successful";
                                responceStatus = "Successful";
                            }
                            else
                            {
                                status = "fail";
                                message = "Transaction Declined";
                                responceStatus = "Failed";
                            }
                        }
                        }
                        else
                        {
                            status = "pending";
                            message = "Transaction Pending";
                            responceStatus="Pending";
                        }
                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            responceStatus = "Successful";
                        }
                        else
                        {
                            status = "fail";
                            message = "Transaction Declined";
                            responceStatus = "Failed";
                        }
                    }

                        commonValidatorVO.setTrackingid(trackingId);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                        genericTransDetailsVO.setNotificationUrl(notificationUrl);
                        genericTransDetailsVO.setRedirectMethod(RedirectMethod);

                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        merchantDetailsVO.setPoweredBy(isPowerBy);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        //merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        addressDetailsVO.setFirstname(firstName);
                        addressDetailsVO.setLastname(lastName);
                        addressDetailsVO.setCity(city);
                        addressDetailsVO.setZipCode(zip);
                        addressDetailsVO.setStreet(street);
                        addressDetailsVO.setCountry(country);
                        addressDetailsVO.setTelnocc(telcc);
                        addressDetailsVO.setPhone(telno);
                        addressDetailsVO.setState(state);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                        commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                        if (functions.isValueNull(email))
                            addressDetailsVO.setEmail(email);
                        else
                            addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                        commonValidatorVO.setPaymentType(paymodeid);
                        commonValidatorVO.setCardType(cardtypeid);
                        commonValidatorVO.setTerminalId(terminalid);

                        cardDetailsVO.setCardNum(cnum);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                        commonValidatorVO.setCustomerId(custId);
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                        transactionUtility.setToken(commonValidatorVO, status);
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            transactionDetailsVO1.setExpdate(expDate);
                            transactionDetailsVO1.setTransactionMode("3D");
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                        }


                        if ("Y".equalsIgnoreCase(autoredirect))
                        {
                            transactionLogger.error("respStatus in Y---" + responceStatus);
                            transactionUtility.doAutoRedirect(commonValidatorVO, res, responceStatus, billingDesc);
                        }
                        else
                        {

                            session.setAttribute("ctoken", ctoken);
                            req.setAttribute("transDetail", commonValidatorVO);
                            req.setAttribute("responceStatus", status);
                            req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            String confirmationPage = "";
                            if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
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
                transactionLogger.error("SQL Exception in ApcoPayFrontEndServlet3D---", e);
            }
            catch (SystemError e)
            {
                transactionLogger.error("System Exception in ApcoPayFrontEndServlet3D---", e);
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("NoSuchAlgorithm Exception in ApcoPayFrontEndServlet3D---", e);
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("SQL Exception in ApcoPayFrontEndServlet3D---", e);
            }
            catch (Exception e)
            {
                transactionLogger.error("SQL Exception in ApcoPayFrontEndServlet3D---", e);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }

}