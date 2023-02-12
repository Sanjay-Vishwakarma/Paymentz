package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.payvision.core.PayVisionPaymentGateway;
import com.payment.payvision.core.PayVisionPaymentProcess;
import com.payment.payvision.core.PayVisionRequestVO;
import com.payment.payvision.core.PayVisionResponseVO;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Admin on 1/8/18.
 */
public class PVFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(PVFrontEndServlet.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public static PayVisionRequestVO getPreviousTransactionDetails(String previouTransTrackingId)
    {
        Connection conn         = null;
        PreparedStatement stmt  = null;
        ResultSet rs            = null;
        PayVisionRequestVO payVisionRequestVO   = null;
        Functions functions                     = new Functions();
        try
        {
            conn            = Database.getConnection();
            String query    = "SELECT trackingmembercode,enrollmentid FROM `transaction_payvision_details` WHERE trackingid=?";
            stmt            = conn.prepareStatement(query);
            stmt.setString(1, previouTransTrackingId);
            rs = stmt.executeQuery();
            if (rs.next())
            {

                payVisionRequestVO = new PayVisionRequestVO();
                payVisionRequestVO.setEnrollmentTrackingMemberCode(rs.getString("trackingmembercode"));

                payVisionRequestVO.setEnrollmentId(rs.getInt("enrollmentid"));
            }
            transactionLogger.error("Query-----" + stmt);

        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError:::::" + e);

        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException:::::" + se);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return payVisionRequestVO;
    }

    public static void UpdateTransactionGuid(String status,String trackingmembercode,String transactionguid,String trackingId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String updateQuery = "UPDATE transaction_payvision_details SET status=?,trackingmembercode=?,transactionguid=?WHERE trackingid=?";
            ps = conn.prepareStatement(updateQuery);
            ps.setString(1, status);
            ps.setString(2, trackingmembercode);
            ps.setString(3, transactionguid);
            ps.setString(4, trackingId);
            ps.executeUpdate();

            transactionLogger.debug("-------UpdateTransactionGuid-------" + ps);

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "UpdateTransactionGuid()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java", "UpdateTransactionGuid()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String toId = "";
        Connection con = null;
        try
        {
            HttpSession session = request.getSession(true);

            ActionEntry entry                               = new ActionEntry();
            AuditTrailVO auditTrailVO                       = new AuditTrailVO();
            CommonValidatorVO commonValidatorVO             = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO     = new GenericTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO        = new GenericAddressDetailsVO();
            GenericCardDetailsVO cardDetailsVO              = new GenericCardDetailsVO();
            TransactionManager transactionManager           = new TransactionManager();
            TransactionUtility transactionUtility           = new TransactionUtility();
            String remoteAddr                               = Functions.getIpAddress(request);
            String reqIp = "";
            if(remoteAddr.contains(","))
            {
                String sIp[]    = remoteAddr.split(",");
                reqIp           = sIp[0].trim();
            }
            else
            {
                reqIp = remoteAddr;
            }
            transactionLogger.debug("reqIp-----"+reqIp);
            MerchantDetailsVO merchantDetailsVO = null;
            String payModeId = "";
            String cardTypeId = "";
            String isService = "";
            String accountId = "";
            String status = "";
            String responceStatus = "";
            String amount = "";
            String description = "";
            String orderDescription = "";
            String redirectUrl = "";
            String clKey = "";
            String checksumNew = "";
            String autoRedirect = "";
            String logoName = "";
            String partnerName = "";
            String confirmStatus = "";
            String isPowerBy = "";
            String firstName="";
            String lastName="";

            String token = "";
            String currency = "";
            String billingDesc = "";
            String message = "";
            String email = "";
            String dbStatus = "";
            String tmpl_Amount="";
            String tmpl_Currency="";
            String ccnum="";
            String expMonth="";
            String expYear="";

            String transactionId = "";
            String authorization_code = "";
            String transactionStatus = "";
            String trackingId = "";
            String cvv = "";
            String displayName = "";
            String merchantOrganizationName = "";
            String partnerSupportContactNumber = "";
            String PARes ="";
            String eci="";
            String customerId="";
            String version="";
            String notificationUrl="";
            String terminalid="";
            String ipAddress=Functions.getIpAddress(request);
            String street="";
            String zip="";
            String state="";
            String city="";
            String country="";
            String telno="";
            String telcc="";
            String redirectMethod="";
            Functions functions     = new Functions();
            Transaction transaction = new Transaction();

            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String keyName      = (String) enumeration.nextElement();
                String keyValue     = request.getParameter(keyName);
                transactionLogger.error(keyName + ":" + keyValue);
            }


            PARes                       = request.getParameter("PaRes");
            String trackingIdCVVHash    = request.getParameter("referenceNo");

           /* if(functions.isValueNull(PARes)){
                ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
                paresDecodeRequestVO.setMassageID(trackingIdCVVHash);
                paresDecodeRequestVO.setPares(PARes);
                paresDecodeRequestVO.setTrackid(trackingIdCVVHash);

                EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
                ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

                if(functions.isValueNull(paresDecodeResponseVO.getEci())){
                    eci=paresDecodeResponseVO.getEci();
                    transactionLogger.error("ECI-----"+eci);
                }
            }*/


            if (trackingIdCVVHash != null)
            {
                String trackingIdCVVString  = trackingIdCVVHash;
                String trackingIdCVVArr[]   = trackingIdCVVString.split("-");
                trackingId                  = trackingIdCVVArr[0];
                String num                  = trackingIdCVVArr[1];
                String URLdecoded           = ESAPI.encoder().decodeFromURL(num);
                String decoded              = URLdecoded.replaceAll(" ","+");
                cvv                         = PzEncryptor.decryptCVV(decoded);

            }
            transactionLogger.debug("num-----"+cvv);

            PayVisionPaymentProcess paymentProcess  = new PayVisionPaymentProcess();

            PayVisionRequestVO commRequestVO        = getPreviousTransactionDetails(trackingId);

            paymentProcess.setPayVisionRequestVO(commRequestVO, trackingId, PARes, cvv);

            commRequestVO.getAddressDetailsVO().setCardHolderIpAddress(ipAddress);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            description      = transactionDetailsVO.getOrderId();
            orderDescription = transactionDetailsVO.getOrderDesc();
            amount           = transactionDetailsVO.getAmount();
            currency         = transactionDetailsVO.getCurrency();
            toId             = transactionDetailsVO.getToId();
            payModeId        = transactionDetailsVO.getPaymentType();
            cardTypeId       = transactionDetailsVO.getCardType();
            redirectUrl      = transactionDetailsVO.getRedirectUrl();
            dbStatus         = transactionDetailsVO.getPrevTransactionStatus();
            customerId       = transactionDetailsVO.getCustomerId();
            version          = transactionDetailsVO.getVersion();
            notificationUrl  = transactionDetailsVO.getNotificationUrl();
            terminalid       = transactionDetailsVO.getTerminalId();
            zip              = commRequestVO.getAddressDetailsVO().getZipCode();
            street           = commRequestVO.getAddressDetailsVO().getStreet();
            state            = commRequestVO.getAddressDetailsVO().getState();
            city             = commRequestVO.getAddressDetailsVO().getCity();
            country          = commRequestVO.getAddressDetailsVO().getCountry();
            telcc            = commRequestVO.getAddressDetailsVO().getTelnocc();
            telno            = commRequestVO.getAddressDetailsVO().getPhone();
            redirectMethod   = transactionDetailsVO.getRedirectMethod();

            transactionLogger.debug("redirectUrl------" + redirectUrl);
            transactionLogger.debug("dbStatus------" + dbStatus);

            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            email = commAddressDetailsVO.getEmail();
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName   = commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName    = commAddressDetailsVO.getLastname();
            tmpl_Amount     = commAddressDetailsVO.getTmpl_amount();
            tmpl_Currency   = commAddressDetailsVO.getTmpl_currency();

            transactionLogger.debug("tmpl_Amount-------"+tmpl_Amount);
            transactionLogger.debug("tmpl_Currency-------"+tmpl_Currency);

            CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
            ccnum       = commCardDetailsVO.getCardNum();
            expMonth    = commCardDetailsVO.getExpMonth();
            expYear     = commCardDetailsVO.getExpYear();
            transactionLogger.debug("ccnum-------"+ccnum);

            CommMerchantVO commMerchantVO   = commRequestVO.getCommMerchantVO();
            accountId                       = commMerchantVO.getAccountId();

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);


            PayVisionPaymentGateway payVisionPaymentGateway = new PayVisionPaymentGateway(accountId);
            GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
            MerchantConfigManager merchantConfigManager     = new MerchantConfigManager();
            merchantDetailsVO                               = merchantConfigManager.getMerchantDetailFromToId(toId);
            String merchantTelNo                            = merchantDetailsVO.getPartnerSupportContactNumber();
            String mPhone = "";

            if (functions.isValueNull(merchantTelNo) && merchantTelNo.contains("-"))
            {
                String[] phone  = merchantTelNo.split("-");
                mPhone          = phone[1];
            }
            else
            {
                mPhone  = merchantTelNo;
            }

            transactionLogger.debug("mphone----"+mPhone);

            String contactPerson    = merchantDetailsVO.getContact_persons().trim();
            String mFirstName       = "";
            String mLastName        = "";

            if (functions.isValueNull(contactPerson) && contactPerson.contains(" "))
            {
                String[] person = contactPerson.split(" ");
                mFirstName      = person[0];
                mLastName       = person[1];
            }
            else
            {
                mFirstName  = contactPerson;
                mLastName   = contactPerson;
            }

            commMerchantVO.setMerchantOrganizationName(merchantDetailsVO.getCompany_name());
            commMerchantVO.setPartnerSupportContactNumber(mPhone);
            commMerchantVO.setIsService(merchantDetailsVO.getIsService());
            commMerchantVO.setZipCode(merchantDetailsVO.getZip());
            commMerchantVO.setFirstName(mFirstName);
            commMerchantVO.setLastName(mLastName);
            commRequestVO.setCommMerchantVO(commMerchantVO);

            transactionLogger.error("First name for Recipient---"+mFirstName);
            transactionLogger.error("Last name for Recipient---"+mLastName);
            transactionLogger.error("ZIP for Recipient---"+merchantDetailsVO.getZip());
            transactionLogger.error("Card for Recipient---"+commCardDetailsVO.getCardNum());

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey           = merchantDetailsVO.getKey();
                autoRedirect    = merchantDetailsVO.getAutoRedirect();
                logoName        = merchantDetailsVO.getLogoName();
                partnerName     = merchantDetailsVO.getPartnerName();
                isService       = merchantDetailsVO.getIsService();
            }

            transactionLogger.debug("isService----"+isService);


            String transType = "";

            PayVisionResponseVO transRespDetails = null;
            transactionLogger.debug("dbStatus-----"+dbStatus);
            String transactionType = "";
            try
            {

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    //TODO
                    String detailStatus = PVFrontEndServlet.getPreviousTransactionStatus(trackingId);
                    transactionLogger.debug("detailStatus-----"+detailStatus);

                    //if(detailStatus.equalsIgnoreCase(PZTransactionStatus.AUTHSTARTED_3D.toString()))
                    if(detailStatus.equalsIgnoreCase("3D_authstarted"))
                    {
                        if ("N".equals(isService))
                        {
                            transType = "Auth";
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                            transRespDetails = (PayVisionResponseVO) payVisionPaymentGateway.process3DAuthConfirmation(trackingId, commRequestVO);

                        }
                        else
                        {
                            transType = "Sale";
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                            transRespDetails = (PayVisionResponseVO) payVisionPaymentGateway.process3DSaleConfirmation(trackingId, commRequestVO);

                        }

                        if (transRespDetails != null)
                        {
                            transactionStatus           = transRespDetails.getStatus();
                            transactionId               = transRespDetails.getTransactionId();
                            authorization_code          = transRespDetails.getAuthCode();
                            message                     = transRespDetails.getDescription();
                            String transactionGuid      = transRespDetails.getTransactionGuid();
                            String trackingmembercode   = transRespDetails.getTrackingMemberCode();
                            transactionLogger.error("transactionGuid-----" + transactionGuid);
                            transactionLogger.error("trackingmembercode-----" + trackingmembercode);

                        }
                        String respTmpl_Amount = "";
                        transactionLogger.debug("transRespDetails.getAmount()-----"+transRespDetails.getAmount());
                        transactionLogger.debug("dbAmount-----"+commTransactionDetailsVO.getAmount());
                        if(functions.isValueNull(transRespDetails.getAmount()) && !transRespDetails.getAmount().equalsIgnoreCase(commTransactionDetailsVO.getAmount())){
                            respTmpl_Amount = String.valueOf((Double.parseDouble(addressDetailsVO.getTmpl_amount())*Double.parseDouble(transRespDetails.getAmount()))/Double.parseDouble(commTransactionDetailsVO.getAmount()));
                            transactionLogger.debug("respTmpl_Amount-----"+respTmpl_Amount);
                            transRespDetails.setTmpl_Amount(respTmpl_Amount);
                        }
                        transactionLogger.debug("Remark------" + transRespDetails.getDescription());

                        if (functions.isValueNull(gatewayAccount.getDisplayName().trim()) && gatewayAccount.getDisplayName().equals("*"))
                        {
                            displayName = "";
                        }
                        else if (functions.isValueNull(gatewayAccount.getDisplayName().trim()) && gatewayAccount.getDisplayName().contains("*"))
                        {
                            displayName = StringUtils.substringBefore(gatewayAccount.getDisplayName().trim(), "*") + "*";
                        }
                        else
                        {
                            displayName = gatewayAccount.getDisplayName().trim() + "*";
                        }

                        merchantOrganizationName    = displayName + merchantDetailsVO.getCompany_name();
                        partnerSupportContactNumber = mPhone;

                        if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
                        {
                            if (!merchantOrganizationName.equals("") && merchantOrganizationName.length() != 0 && merchantOrganizationName.length() > 26)
                            {
                                merchantOrganizationName = merchantOrganizationName.substring(0, 26);
                            }
                            if (!partnerSupportContactNumber.equals("") && partnerSupportContactNumber.length() != 0 && partnerSupportContactNumber.length() > 14)
                            {
                                partnerSupportContactNumber = partnerSupportContactNumber.substring(0, 14);
                            }
                            billingDesc = merchantOrganizationName + "|" + partnerSupportContactNumber;
                        }
                        else
                        {
                            billingDesc = gatewayAccount.getDisplayName();
                        }

                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equals(transactionStatus))
                        {
                            status          = "success";
                            confirmStatus   = "Y";
                            responceStatus  = "Successful(" + message + ")";
                            dbStatus        = "capturesuccess";
                            //billingDesc = gatewayAccount.getDisplayName();
                            transactionLogger.debug("transType-----" + transType);
                            if ("Sale".equalsIgnoreCase(transType))
                            {
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "',authorization_code='"+authorization_code+"'");
                               // entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO, null);
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                UpdateTransactionGuid(ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails.getTrackingMemberCode(), transRespDetails.getTransactionGuid(), trackingId);
                            }
                            else
                            {
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "',authorization_code='"+authorization_code+"'");
                              //  entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO, null);
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                UpdateTransactionGuid(ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails.getTrackingMemberCode(), transRespDetails.getTransactionGuid(), trackingId);
                            }
                        }
                        else
                        {
                            confirmStatus   = "N";
                            status          = "fail";
                            dbStatus        = "authfailed";
                            responceStatus  = "Failed(" + message + ")";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO, auditTrailVO, null);
                        }
                        dbBuffer.append(" ,customerIp='"+reqIp+"',customerIpCountry='"+functions.getIPCountryShort(reqIp)+"',remark='" + message + "' where trackingid = " + trackingId);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                    }
                    else
                    {
                        status          = "pending";
                        dbStatus        = "pending";
                        responceStatus  = "pending(transaction processing)";
                    }

                    StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }
                else
                {
                    if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        if (functions.isValueNull(gatewayAccount.getDisplayName()) && gatewayAccount.getDisplayName().contains("*"))
                        {
                            displayName = StringUtils.substringBefore(gatewayAccount.getDisplayName(), "*");
                        }
                        else
                        {
                            displayName = gatewayAccount.getDisplayName();
                        }

                        merchantOrganizationName = displayName + "*" + merchantDetailsVO.getCompany_name();
                        partnerSupportContactNumber = merchantDetailsVO.getPartnerSupportContactNumber();

                        //billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
                        {
                            if (!merchantOrganizationName.equals("") && merchantOrganizationName.length() != 0 && merchantOrganizationName.length() > 26)
                            {
                                merchantOrganizationName = merchantOrganizationName.substring(0, 26);
                            }
                            if (!partnerSupportContactNumber.equals("") && partnerSupportContactNumber.length() != 0 && partnerSupportContactNumber.length() > 14)
                            {
                                partnerSupportContactNumber = partnerSupportContactNumber.substring(0, 14);
                            }
                            billingDesc = merchantOrganizationName + "|" + partnerSupportContactNumber;
                        }
                        else
                        {
                            billingDesc = gatewayAccount.getDisplayName();
                        }

                        status          = "success";
                        message         = "Transaction Successful";
                        responceStatus  = "Successful";
                    }
                    else
                    {
                        status          = "fail";
                        message         = "Transaction Declined";
                        responceStatus  = "Failed";
                    }
                }
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setRedirectMethod(redirectMethod);

                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                addressDetailsVO.setCity(city);
                addressDetailsVO.setZipCode(zip);
                addressDetailsVO.setStreet(street);
                addressDetailsVO.setCountry(country);
                addressDetailsVO.setTelnocc(telcc);
                addressDetailsVO.setPhone(telno);
                addressDetailsVO.setState(state);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setCustomerId(customerId);
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setTerminalId(terminalid);

                transactionUtility.setToken(commonValidatorVO,responceStatus);

                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---"+notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,dbStatus,message,"");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, responceStatus, billingDesc);
                }
                else
                {
                    request.setAttribute("responceStatus", responceStatus);
                    request.setAttribute("displayName", billingDesc);
                    request.setAttribute("remark", message);
                    request.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";

                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    transactionLogger.debug("Version value---"+version+"---"+confirmationPage);
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }

            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("PVFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }


        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PVFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("PVFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PVFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }

        catch (EncodingException e)
        {
            transactionLogger.error("error:::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PVFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public static String getPreviousTransactionStatus(String previouTransTrackingId)
    {
        Connection conn=null;
        PreparedStatement stmt=null;
        ResultSet rs=null;
        String detailStatus = "";
        try{
            conn= Database.getConnection();
            String query="SELECT status FROM `transaction_common_details` WHERE trackingid=? ORDER BY detailId DESC LIMIT 1";
            stmt= conn.prepareStatement(query);
            stmt.setString(1,previouTransTrackingId);
            rs=stmt.executeQuery();
            if(rs.next())
                detailStatus=(rs.getString("status"));

        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError:::::",e);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException:::::"+se);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return detailStatus;
    }
}
