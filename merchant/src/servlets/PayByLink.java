import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.vo.VTResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectKitValidatorVO;
import com.transaction.utils.TransactionCoreUtils;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

public class PayByLink extends HttpServlet
{
    private static Logger log = new Logger(PayByLink.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        try
        {
            String remoteAddr = Functions.getIpAddress(request);
            String httpProtocol = request.getScheme();
            int serverPort = request.getServerPort();
            String servletPath = request.getServletPath();
            int trackingId = 0;
            HttpSession session = request.getSession();
            User user =  (User)session.getAttribute("ESAPIUserSessionKey");
            Merchants merchants=new Merchants();

            if (!merchants.isLoggedIn(session))
            {
                log.debug("member is logout ");
                response.sendRedirect("/merchant/Logout.jsp");
                return;
            }
            PaymentManager paymentManager = new PaymentManager();
            CommonValidatorVO directKitValidatorVO = VirtualSingleCall.getRequestParametersForSale(request);
            String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;

            TransactionCoreUtils transactionUtils = new TransactionCoreUtils();
            ServletContext application = getServletContext();
            VTResponseVO vtResponseVO = new VTResponseVO();

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
            directKitValidatorVO.getTransDetailsVO().setFromtype(gatewayAccount.getGateway());
            directKitValidatorVO.getTransDetailsVO().setCardType(request.getParameter("cardtype"));
            String paymodename=request.getParameter("paymodename");
            directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(paymodename));
            directKitValidatorVO.setTerminalId(request.getParameter("terminalid"));
            directKitValidatorVO.getAddressDetailsVO().setFirstname(request.getParameter("firstname"));


            trackingId = paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
            //trackingId = 150018;
            directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

            vtResponseVO = transactionUtils.singleCallPaybyLink(directKitValidatorVO, application);
            if (vtResponseVO != null)
            {
                Hashtable hiddenVariables = new Hashtable();
                hiddenVariables.put("status", vtResponseVO.getStatus());
                hiddenVariables.put("statusDesc", vtResponseVO.getStatusDescription());
                session.setAttribute("hiddenResponse", hiddenVariables);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/paybylinkResponse.jsp?ctoken=" + user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }

        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException==",dbe);
        }
    }

}
