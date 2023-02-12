package payment;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.kotakbank.core.KotakPaymentProcess;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Base64;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Nikita on 3/15/2017.
 */
public class KotakFrontEndNotification extends PzServlet
{
    private static final String ALGO = "AES";
    private static Logger logger = new Logger(KotakFrontEndNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(KotakFrontEndNotification.class.getName());
    String encKey = "3a5c1c4e81d7eb133a5c1c4e81d7eb13";
    String SECURE_SECRET = "F3C63F6FFFC176E4AAD530C59833719D";
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public KotakFrontEndNotification()
    {
        super();
    }

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet())
        {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        html.append("<script language=\"javascript\">");
        html.append("document.pay_form.submit();");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        transactionLogger.debug("DATA--->" + html);
        return html.toString();
    }

    private static String null2unknown(String in) {
        if (in == null || in.length() == 0) {
            return "No Value Returned";
        } else {
            return in;
        }
    }

    public static String decrypt(String encryptedData, String keySet) throws Exception {
        byte[] keyByte = keySet.getBytes();
        Key key = generateKey(keyByte);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedByteValue = new Base64().decode(encryptedData.getBytes());
        byte[] decValue = c.doFinal(decryptedByteValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey(byte[] keyByte) throws Exception {
        Key key = new SecretKeySpec (keyByte, ALGO);
        return key;
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
        logger.debug("Enter in doService of KotakFrontEndNotification-------");
        transactionLogger.debug("Enter in doService of KotakFrontEndNotification-------");
        res.setContentType("text/html");
        HttpSession session = req.getSession(true);
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        try
        {
            //retrieve all the incoming parameters into a hash map
            Map fields = null;
            Functions functions= new Functions();
           if(functions.isValueNull(req.getParameter("EncData"))){

               if (req.getParameter("EncData") != null)
            {
                fields = new HashMap<String, String>();
                String encData = decrypt(req.getParameter("EncData"), encKey);
                transactionLogger.debug("encData KotakFrontEnd----" + encData);
                String pipeSplit[] = encData.toString().split("::");
                for (int i = 0; i < pipeSplit.length; i++)
                {
                    String pareValues[] = pipeSplit[i].split("=");
                    if (pareValues.length == 2)
                    {
                        fields.put(pareValues[0], URLDecoder.decode(pareValues[1], "UTF-8"));
                    }
                    transactionLogger.debug("fields KotakFrontEnd---" + fields);
                }
            }

            //extract the fields from response
            String ema_Txn_Secure_Hash = null2unknown((String) fields.remove("SecureHash"));
            String trackingId = null2unknown((String) fields.get("TxnRefNo"));
            String orderId = null2unknown((String) fields.get("OrderInfo"));
            String amount = null2unknown((String) fields.get("Amount"));
            String responseCode = null2unknown((String) fields.get("ResponseCode"));
            String status = null2unknown((String) fields.get("Message"));
            String paymentId = null2unknown((String) fields.get("RetRefNo"));
            String batchNo = null2unknown((String) fields.get("BatchNo"));
            String authCode = null2unknown((String) fields.get("AuthCode"));
            String transactionDate = null2unknown((String) fields.get("TransactionDate"));
            String transactionTime = null2unknown((String) fields.get("TransactionTime"));

            //String merchantId = null2unknown((String) fields.get("MerchantId"));
            //String terminalId = null2unknown((String) fields.get("TerminalId"));
            //String cardNo = null2unknown((String) fields.get("MaskedCardNo"));
            //String cardType = null2unknown((String) fields.get("CardType"));
            String CAVV = null2unknown((String) fields.get("CAVV"));
            String ECI = null2unknown((String) fields.get("UCAP"));
            String AuthStatus = null2unknown((String) fields.get("AuthStatus"));
            String ENROLLED = null2unknown((String) fields.get("ENROLLED"));
            String hashValidated = null;
            boolean errorExists = false;
            String merchantSecureHash = hashAllFields(fields);
            transactionLogger.debug("merchantSecureHash KotakFrontEnd---" + merchantSecureHash);
            if (ema_Txn_Secure_Hash.equalsIgnoreCase(merchantSecureHash))
            {
                hashValidated = "<font color='#00AA00'><strong>CORRECT</strong></font>";
            }
            else
            {
                errorExists = true;
                hashValidated = "<font color='#FF0066'><strong>INVALID HASH</strong></font>";
            }
            res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            res.setHeader("Pragma", "no-cache");
            res.setHeader("Expires", "0");
            res.setDateHeader("Expires", -1);
            res.setDateHeader("Last-Modified", 0);
            transactionLogger.debug("res KotakFrontEnd---" + res);

            TransactionManager transactionManager = new TransactionManager();
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
            ActionEntry entry = new ActionEntry();
            CommResponseVO commResponseVO = new CommResponseVO();
            AuditTrailVO auditTrailVO = new AuditTrailVO();
            CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
            MerchantDAO merchantDAO = new MerchantDAO();

            KotakPaymentProcess kotakPaymentProcess = new KotakPaymentProcess();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            String toid = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String currencyDb = "";
            String checksumAlgo = "";
            String checksumNew = "";
            String autoredirect = "";
            String displayName = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";
            String partnerId = "";
            String remark = "";
            String dbStatus = "";
            String confirmStatus = "";
            String isService = "";
            int detailId = 0;
            String email = "";

            if (functions.isValueNull(status))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                toid = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                dbStatus = transactionDetailsVO.getStatus();
                amount = transactionDetailsVO.getAmount();
                orderId = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                orderDesc = transactionDetailsVO.getOrderDescription();
                email = transactionDetailsVO.getEmailaddr();
                if (orderDesc != null && !orderDesc.equalsIgnoreCase("null"))
                {
                    orderDesc = transactionDetailsVO.getOrderDescription();
                }
                else
                {
                    orderDesc = "";
                }
                transactionLogger.debug("orderDesc----" + orderDesc);
                currencyDb = transactionDetailsVO.getCurrency();
                displayName = transactionDetailsVO.getFromtype();
                transactionLogger.debug("displayName-----" + displayName);

                auditTrailVO.setActionExecutorName("Customer");
                auditTrailVO.setActionExecutorId(toid);

                StringBuffer dbBuffer = new StringBuffer();
                con = Database.getConnection();
                String query = "select m.clkey,checksumalgo,m.autoredirect,m.partnerid,logoName,m.isPoweredBy,m.isService,partnerName FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";
                p = con.prepareStatement(query);
                p.setString(1, toid);
                rs = p.executeQuery();
                if (rs.next())
                {
                    checksumAlgo = rs.getString("checksumalgo");
                    autoredirect = rs.getString("autoredirect");
                    isPowerBy = rs.getString("isPoweredBy");
                    logoName = rs.getString("logoName");
                    partnerName = rs.getString("partnerName");
                    partnerId = rs.getString("partnerId");
                    isService = rs.getString("isService");
                }
                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                {
                    commResponseVO.setRemark(status);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setResponseHashInfo(authCode);

                    if (status.equalsIgnoreCase("Success") && responseCode.equals("00"))
                    {
                        if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                        {
                            dbStatus = "Success - Transaction successful";
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "', status='capturesuccess', paymentid='" + paymentId + "', remark='" + status + "'");
                            commResponseVO.setTransactionStatus(dbStatus);
                            commResponseVO.setStatus(status);
                            commResponseVO.setErrorCode(responseCode);
                            commResponseVO.setTransactionType("sale");
                            commResponseVO.setDescriptor(displayName);

                            confirmStatus = "Y";
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                            kotakPaymentProcess.actionEntryExtension(detailId, trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, status, commResponseVO, null);
                        }
                        else
                        {
                            dbStatus = "Success - Transaction successful";
                            dbBuffer.append("update transaction_common set amount='" + amount + "', status='authsuccessful', paymentid='" + paymentId + "', remark='" + status + "'");
                            commResponseVO.setTransactionStatus(dbStatus);
                            commResponseVO.setStatus(status);
                            commResponseVO.setErrorCode(responseCode);
                            commResponseVO.setTransactionType("auth");
                            commResponseVO.setDescriptor(displayName);

                            confirmStatus = "Y";
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_SUCCESS.toString());
                            kotakPaymentProcess.actionEntryExtension(detailId, trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, status, commResponseVO, null);
                        }
                    }
                    else
                    {
                        dbStatus = "Failed - Transaction failed";
                        dbBuffer.append("update transaction_common set status='authfailed', paymentid='" + paymentId + "', remark='" + status + "'");
                        commResponseVO.setTransactionStatus(dbStatus);
                        commResponseVO.setErrorCode(responseCode);
                        commResponseVO.setStatus(status);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());
                        // kotakPaymentProcess.actionEntryExtension(detailId,trackingId,amount,ActionEntry.ACTION_AUTHORISTION_FAILED,status,commResponseVO,commRequestVO);
                    }
                    dbBuffer.append(" where trackingid = " + trackingId);
                    logger.error("common update query in KotakFrontEndNotification---" + dbBuffer.toString());
                    try
                    {
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }
                    catch (SystemError se)
                    {
                        logger.error("error::::", se);
                        transactionLogger.error("error::::", se);
                        PZExceptionHandler.raiseAndHandleDBViolationException("KotakFrontEndNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
                    }
                    finally
                    {
                        Database.closeConnection(con);
                    }

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), confirmStatus, null, null);

                    checksumNew = Checksum.generateChecksumForStandardKit(trackingId, orderId, String.valueOf(amount), status, checksumAlgo);

                    commonValidatorVO.setTrackingid(trackingId);
                    genericTransDetailsVO.setOrderId(orderId);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currencyDb);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    Map saleMap = new TreeMap();
                    String respStatus = status;
                    String billingDesc = "";
                    if (status != null && status.contains("Success") && responseCode.equals("00"))
                    {
                        respStatus = "Transaction Successful (" + status + ")";
                        logger.debug("respstatus----" + respStatus);
                        status = "success";
                        billingDesc = displayName;
                    }
                    else
                    {
                        respStatus = "Transaction Failed (" + status + ")";
                        logger.debug("respstatus----" + respStatus);
                        status = "success";
                    }

                    saleMap.put("currency", currencyDb);
                    saleMap.put("captureAmount", amount);
                    saleMap.put("desc", orderId);
                    saleMap.put("checksum", checksumNew);
                    saleMap.put("amount", amount);
                    saleMap.put("trackingid", trackingId);
                    saleMap.put("status", respStatus);
                    saleMap.put("descriptor", billingDesc);

                    if ("Y".equalsIgnoreCase(autoredirect))
                    {
                        String redirect = generateAutoSubmitForm(redirectUrl, saleMap);
                        res.setContentType("text/html;charset=UTF-8");
                        res.setCharacterEncoding("UTF-8");

                        try
                        {
                            res.getWriter().write(redirect);
                        }
                        catch (IOException e)
                        {
                            logger.error("IO Exception in KotakFrontEndNotification---", e);
                            transactionLogger.error("IO Exception in KotakFrontEndNotification---", e);
                        }
                    }
                    else
                    {
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        merchantDetailsVO.setPoweredBy(isPowerBy);
                        addressDetailsVO.setEmail(email);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                        merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                        transactionLogger.error("trackingid---" + commonValidatorVO.getTrackingid() + "-Description--" + commonValidatorVO.getTransDetailsVO().getOrderId() + "-Amount-" + commonValidatorVO.getTransDetailsVO().getAmount() + "-status-" + status);
                        transactionLogger.error("trackingid---" + trackingId + "-Description--" + orderId + "-Amount-" + amount + "-status-" + status + "-" + commResponseVO.getDescriptor());

                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", respStatus);
                        req.setAttribute("displayName", displayName);
                        String confirmationPage = "";
                        String version = (String)session.getAttribute("version");
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        session.invalidate();
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }
                }
            }
        }
        }
        catch (SQLException e)
        {
            logger.error("SQLException",e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError", systemError);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException", e);
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuffer buf = new StringBuffer();
        buf.append(SECURE_SECRET);
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                buf.append(fieldValue);
                logger.debug("buf----"+buf);
            }
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(buf.toString().getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
