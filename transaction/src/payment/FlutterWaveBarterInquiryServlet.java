package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.flwBarter.FlutterWaveBarterPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Rihen on 5-Jan-20.
 */
public class FlutterWaveBarterInquiryServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(FlutterWaveBarterInquiryServlet.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
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
        transactionLogger.error("Inside FlutterWaveBarterInquiryServlet -----");
        TransactionManager transactionManager = new TransactionManager();
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

        String toId = "",accountId = "", dbStatus = "", status = "", amount = "", orderDesc = "", redirectUrl = "", logoName = "",
                partnerName = "",  responseStatus = "", currency = "", billingDesc = "", custEmail = "", tmpl_amt = "", tmpl_currency = "",
                payModeId = "", cardTypeId = "",  firstName = "", lastName = "", trackingId = "", customerId = "", version = "", notificationUrl = "", terminalId = "",
                autoRedirect = "", message = "", updatedStatus = "", transactionStatus="",transactionId="",confirmStatus="",ccnum="",expMonth="",expYear="",expDate="",
                paymentId="";

        String requestIp=Functions.getIpAddress(request);
        Connection con = null;


        Enumeration enumeration = request.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.debug("hasElements ----" + hasElements);
        String value="";
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = request.getParameter(key);
            transactionLogger.error("FlutterWaveBarterInquiryServlet Key-----" + key + "----FlutterWaveBarterInquiryServlet value----" + value);
        }

        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Notification JSON-----" + value);
        try
        {
            trackingId = value.toString();
            FlutterWaveBarterPaymentGateway flutterWaveBarterPaymentGateway = null;

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                dbStatus = transactionDetailsVO.getStatus();
                paymentId=transactionDetailsVO.getPaymentId();
                currency = transactionDetailsVO.getCurrency();
                transactionLogger.error("dbStatus-----" + dbStatus);

                flutterWaveBarterPaymentGateway = new FlutterWaveBarterPaymentGateway(accountId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                CommResponseVO commResponseVO = (CommResponseVO) flutterWaveBarterPaymentGateway.processInquiry(commRequestVO);

                transactionLogger.error("------- below inquiry call in servlet ------");
                transactionLogger.error("------- commResponseVO ------"+commResponseVO.getStatus());

                PrintWriter ps = response.getWriter();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", commResponseVO.getStatus());
                response.setContentType("application/json");
                response.setStatus(200);
                ps.write(jsonObject.toString());
                ps.flush();
                return;
            }
        }
        catch (PZDBViolationException tve)
        {
            transactionLogger.error("PZDBViolationException:::::", tve);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
