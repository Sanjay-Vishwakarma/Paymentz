package net.partner;

import com.directi.pg.AsyncNotificationService;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Admin on 10/26/2020.
 */
public class SendNotification extends HttpServlet
{
    private Logger log = new Logger(SendNotification.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.error("Inside SendNotification class");
        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rd=request.getRequestDispatcher("/net/PartnerTransactionDetails?ctoken=" + user.getCSRFToken());
        TransactionManager transactionManager = new TransactionManager();
        Functions functions = new Functions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        String trackingId = request.getParameter("trackingid");
        String notificationUrl = "";
        String toid = "";
        String accountId = "";
        String clKey = "";
        String ccnum = "";
        String expDate = "";
        String billingDesc = "";
        String status = "";
        String message = "";
        String resStatus="Notification Sending failed!!";
        try
        {
            if (functions.isValueNull(trackingId))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
                {
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    toid = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    status = transactionDetailsVO.getStatus();
                    message = transactionDetailsVO.getRemark();
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    }
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    if (merchantDetailsVO != null)
                    {
                        clKey = merchantDetailsVO.getKey();
                    }
                    if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(status) || PZTransactionStatus.AUTH_SUCCESS.toString().equalsIgnoreCase(status))
                    {
                        billingDesc= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    }
                    log.error("notificationUrl--->"+notificationUrl);
                    if (functions.isValueNull(notificationUrl))
                    {
                        log.error("inside sending Notification condition--->"+notificationUrl);
                        log.error("billingDesc--->"+billingDesc);
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        transactionDetailsVO.setExpdate(expDate);
                        transactionDetailsVO.setCcnum(ccnum);
                        transactionDetailsVO.setSecretKey(clKey);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,status,message);
                        resStatus="Notification sent!!";
                    }
                }
            }

        }
        catch (PZDBViolationException e)
        {
           log.error("PZDBViolationException------>",e);
        }
        catch (Exception e)
        {
            log.error("Exception------>",e);
        }
        request.setAttribute("message",resStatus);
        rd.forward(request,response);
        return;
    }
}
