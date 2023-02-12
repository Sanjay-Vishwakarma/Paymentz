package payment;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.neteller.NetellerUtils;
import com.payment.neteller.response.NetellerResponse;
import com.payment.neteller.response.NotificationResponse;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Sneha on 2/27/2017.
 */
public class NetellerBackEndServlet extends PzServlet
{
    private static Logger log = new Logger(NetellerBackEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NetellerBackEndServlet.class.getName());
    public NetellerBackEndServlet()
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
        transactionLogger.debug("-----Inside NetellerBackEndServlet------");
        transactionLogger.error("-----Inside NetellerBackEndServlet------");

        for (Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop NetellerBackEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }
        if (!req.getParameterMap().isEmpty())
        {

            StringBuffer notificationData = new StringBuffer();
            Functions functions = new Functions();
            String data = "";

            while ((data = req.getReader().readLine()) != null)
            {
                notificationData.append(data);
            }
            transactionLogger.debug("RESPONSE DATA---->" + notificationData.toString());

            if (!functions.isValueNull(notificationData.toString()))
            {
                transactionLogger.error("notificationData is empty");
                return;
            }

            NotificationResponse notificationResponse = new NotificationResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            notificationResponse = objectMapper.readValue(notificationData.toString(), NotificationResponse.class);

            String trackingId = notificationResponse.getId();
            transactionLogger.error("trackingId--->" + trackingId);
            TransactionManager transactionManager = new TransactionManager();
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            PaymentManager paymentManager = new PaymentManager();
            if (functions.isValueNull(trackingId))
            {
                // get AccountId from Tracking Id using PaymentQueryManager
                //Hashtable details = null;
                NetellerUtils netellerUtils = new NetellerUtils();
                try
                {
                    //details = paymentManager.getAccountIdandPaymeIdForCommon(trackingId);
                    //transactionLogger.error("details--->" + details);

                    if (transactionDetailsVO != null)
                    {
                        NetellerResponse netellerResponse = new NetellerResponse();

                        String accountid = transactionDetailsVO.getAccountId();
                        String toid = transactionDetailsVO.getToid();
                        String amount = transactionDetailsVO.getAmount();
                        String mid = GatewayAccountService.getGatewayAccount(accountid).getMerchantId();

                        transactionLogger.error("accountid--->" + accountid);
                        transactionLogger.error("toid--->" + toid);

                        String staus = netellerUtils.getTransactionStatus(notificationResponse.getEventType());
                        netellerResponse.setTransactionStatus(staus);
                        netellerResponse.setAmount(amount);
                        netellerResponse.setResponseTime(notificationResponse.getEventDate());
                        netellerResponse.setDescriptor(GatewayAccountService.getGatewayAccount(accountid).getDisplayName());

                        transactionLogger.error("status--->" + staus);
                        transactionLogger.error("amount--->" + amount);
                        transactionLogger.error("MID--->" + mid);
                        transactionLogger.error("Response Time--->" + notificationResponse.getEventDate());

                        paymentManager.updateTransactionForCommon(netellerResponse, staus, trackingId, null, "transaction_common", mid, null, notificationResponse.getEventDate(), null);

                        if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                        {
                            transactionLogger.error("inside sending notification neteller---" + transactionDetailsVO.getNotificationUrl());

                            HashMap hashMap = new HashMap();
                            TransactionUtility transactionUtility = new TransactionUtility();
                            transactionUtility.setMerchantNotification(hashMap, transactionDetailsVO, trackingId, staus, "");

                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,staus,"","");
                        }
                    }
                }
                catch (PZDBViolationException s)
                {
                    log.error("SQL Exception in NetellerBackEndServlet---", s);
                    transactionLogger.error("SQL Exception in NetellerBackEndServlet---", s);
                    PZExceptionHandler.handleDBCVEException(s, null, PZOperations.NETELLER_NOTIFICATION);
                }
            }
        }
    }
}