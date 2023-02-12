package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.Wirecardnew.WireCardNPaymentGateway;
import com.payment.Wirecardnew.WireCardPaymentProcess;
import com.payment.Wirecardnew.WireCardRequestVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
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
 * Created by Admin on 11/5/17.
 */
public class WCFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(WCFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public static String getPreviousTransactionId(String previouTransTrackingId)
    {
        Connection conn=null;
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            conn= Database.getConnection();
            String query="SELECT responsetransactionid FROM `transaction_common_details` WHERE trackingid=? ORDER BY detailId DESC LIMIT 1";
            stmt= conn.prepareStatement(query);
            stmt.setString(1,previouTransTrackingId);
            rs=stmt.executeQuery();
            if(rs.next()){

                previouTransTrackingId=(rs.getString("responsetransactionid"));
            }

        }catch (SystemError e)
        {
            transactionLogger.error("SystemError:::::"+e);

        }catch (SQLException se)
        {
            transactionLogger.error("SQLException:::::"+se);
        }finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return previouTransTrackingId;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
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

        String token = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus="";
        String firstName="";
        String lastName="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String ccnum="";
        String expMonth="";
        String expYear="";

        String transactionId = "";
        String transactionStatus = "";
        String PARes ="";
        String eci="";
        String md="";
        String customerid="";
        String notificationUrl="";
        String version="";
        String terminalid="";

        Functions functions = new Functions();
        Transaction transaction = new Transaction();

        try
        {

            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String keyName = (String) enumeration.nextElement();
                String keyValue = request.getParameter(keyName);
                transactionLogger.error(keyName + ":" + keyValue);
            }

            PARes = request.getParameter("PaRes");
            String trackingId = request.getParameter("trackingId");
            md=request.getParameter("MD");
            String num= PzEncryptor.decryptCVV(md);

            String remoteAddr = Functions.getIpAddress(request);
            String reqIp = "";
            if(remoteAddr.contains(","))
            {
                String sIp[] = remoteAddr.split(",");
                reqIp = sIp[0].trim();
            }
            else
            {
                reqIp = remoteAddr;
            }

            transactionLogger.debug("reqIp-----"+reqIp);
           /* if(functions.isValueNull(PARes)){
                ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
                paresDecodeRequestVO.setMassageID(trackingId);
                paresDecodeRequestVO.setPares(PARes);
                paresDecodeRequestVO.setTrackid(trackingId);

                EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
                ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

                if(functions.isValueNull(paresDecodeResponseVO.getEci())){
                    eci=paresDecodeResponseVO.getEci();
                    transactionLogger.error("ECI-----"+eci);
                }
            }*/


            WireCardPaymentProcess paymentProcess = new WireCardPaymentProcess();
            WireCardRequestVO commRequestVO = new WireCardRequestVO();

            paymentProcess.setWirecardRequestVO(commRequestVO, trackingId, PARes,num);
            commRequestVO.getAddressDetailsVO().setCardHolderIpAddress(reqIp);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            description = transactionDetailsVO.getOrderId();
            orderDescription = transactionDetailsVO.getOrderDesc();
            amount = transactionDetailsVO.getAmount();
            currency = transactionDetailsVO.getCurrency();
            toId = transactionDetailsVO.getToId();
            payModeId = transactionDetailsVO.getPaymentType();
            cardTypeId = transactionDetailsVO.getCardType();
            redirectUrl = transactionDetailsVO.getRedirectUrl();
            dbStatus=transactionDetailsVO.getPrevTransactionStatus();
            customerid=transactionDetailsVO.getCustomerId();
            notificationUrl=transactionDetailsVO.getNotificationUrl();
            version=transactionDetailsVO.getVersion();
            terminalid=transactionDetailsVO.getTerminalId();

            CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
            ccnum=commCardDetailsVO.getCardNum();
            expMonth=commCardDetailsVO.getExpMonth();
            expYear=commCardDetailsVO.getExpYear();

            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            email = commAddressDetailsVO.getEmail();
            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName=commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName=commAddressDetailsVO.getLastname();
            tmpl_Amount=commAddressDetailsVO.getTmpl_amount();
            tmpl_Currency=commAddressDetailsVO.getTmpl_currency();

            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
            accountId = commMerchantVO.getAccountId();

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(getPreviousTransactionId(trackingId));
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

            transactionLogger.error("TransactionId------" + commTransactionDetailsVO.getPreviousTransactionId());

            WireCardNPaymentGateway wireCardNPaymentGateway = new WireCardNPaymentGateway(accountId);
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MerchantDAO merchantDAO= new MerchantDAO();
            merchantDetailsVO = merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey = merchantDetailsVO.getKey();
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
            }

            String transType = "Sale";
            CommResponseVO transRespDetails = null;
            transactionLogger.debug("dbStatus-----"+dbStatus);
            try
            {
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    if ("N".equals(isService))
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                        transRespDetails = (CommResponseVO) wireCardNPaymentGateway.process3DAuthConfirmation(trackingId, commRequestVO);
                        transType = "Auth";
                    }
                    else
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                        transRespDetails = (CommResponseVO) wireCardNPaymentGateway.process3DSaleConfirmation(trackingId, commRequestVO);
                    }

                    if (transRespDetails != null)
                    {
                        transactionStatus = transRespDetails.getStatus();
                        transactionId = transRespDetails.getTransactionId();
                        message = transRespDetails.getRemark();
                    }
                    transactionLogger.debug("Remark------" + transRespDetails.getRemark());


                    String respTmpl_Amount="";
                    if(functions.isValueNull(transRespDetails.getAmount()) && !transRespDetails.getAmount().equalsIgnoreCase(commTransactionDetailsVO.getAmount())){
                        respTmpl_Amount= String.valueOf((Double.parseDouble(addressDetailsVO.getTmpl_amount())*Double.parseDouble(transRespDetails.getAmount()))/Double.parseDouble(commTransactionDetailsVO.getAmount()));
                        transactionLogger.debug("respTmpl_Amount-----"+respTmpl_Amount);
                        transRespDetails.setTmpl_Amount(respTmpl_Amount);
                    }
                    StringBuffer dbBuffer = new StringBuffer();
                    if ("success".equals(transactionStatus))
                    {
                        status = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        confirmStatus = "Y";
                        responceStatus = "Successful";
                        billingDesc = gatewayAccount.getDisplayName();
                        if ("Sale".equalsIgnoreCase(transType))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='"+eci+"'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        }
                        else
                        {
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='"+eci+"'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        }
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = PZTransactionStatus.AUTH_FAILED.toString();
                        responceStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='"+eci+"'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                    }
                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                    con = Database.getConnection();
                    Database.executeUpdate(dbBuffer.toString(), con);
                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }else
                {
                    if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        message = "Transaction Successful";
                        responceStatus = "Successful";
                    }
                    else
                    {
                        status = PZTransactionStatus.AUTH_FAILED.toString();
                        message = "Transaction Declined";
                        responceStatus = "Failed";
                    }
                }
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                addressDetailsVO.setTmpl_amount(tmpl_Amount);
                addressDetailsVO.setTmpl_currency(tmpl_Currency);
                //commonValidatorVO.setDisplayAmount(amount);
                //commonValidatorVO.setDisplayCurrency(currency);

                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setCustomerId(customerid);

                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);


                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setTerminalId(terminalid);

                transactionUtility.setToken(commonValidatorVO,responceStatus);

                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---"+notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,status,message,"");
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
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }

            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("WCFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally
            {
                Database.closeConnection(con);
            }

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("WCFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("WCFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("WCFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }

    }


}
