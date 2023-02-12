import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.PayoutDAO;
import com.payment.request.PZPayoutRequest;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Pramod on 20/01/2021.
 */
public class UploadBulkPayOut extends HttpServlet
{
    private static Logger log = new Logger(UploadBulkPayOut.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("UploadBulkPayOut Started---");
        HttpSession session                 = request.getSession();
        User user                           = (User) session.getAttribute("ESAPIUserSessionKey");
        String[] orderIds                   = null;
        Hashtable hashtable                 = null;
        StringBuffer  orderIdStr            = new StringBuffer();
        BulkPayoutProcess bulkPayoutProcess = new BulkPayoutProcess();
        RequestDispatcher rd                = request.getRequestDispatcher("/bulkPayoutUpload.jsp?ctoken=" + user.getCSRFToken());
        Merchants merchants                 = new Merchants();
        try
        {
            if (!merchants.isLoggedIn(session))
            {
                log.debug("member is logout ");
                response.sendRedirect("/merchant/Logout.jsp");
                return;
            }
            String memberId = session.getAttribute("merchantid").toString();
            orderIds        = request.getParameterValues("id");

            String action = "";
            if (request.getParameterValues("action") != null)
            {
                action = request.getParameter("action");
            }

            log.debug("UploadBulkPayOut action -------> "+action);

            if (request.getParameterValues("id") != null)
            {
                orderIds = request.getParameterValues("id");
            }
            else
            {
                String message = "Invalid PayoutId. Select at least one Payout to process ";
                request.setAttribute("ERROR", message);

                rd.forward(request, response);
                return;
            }
            log.debug("UploadBulkPayOut End---" + orderIds);

            for (String icicitransid : orderIds)
            {
                orderIdStr.append(icicitransid + ",");
            }
            hashtable = new Hashtable();
            if (orderIdStr != null && orderIdStr.length() > 0)
            {

                orderIdStr = orderIdStr.deleteCharAt(orderIdStr.length() - 1);
                hashtable.put("orderIdStr", orderIdStr.toString());
            }
            hashtable.put("memberId", memberId);
            String messgae = "";
            if(action.equalsIgnoreCase("Payout")){
                 messgae = bulkPayoutProcess.bulkPayoutProcessStart(hashtable);
            }else if(action.equalsIgnoreCase("Delete")){
                messgae = bulkPayoutProcess.deleteUploadedPayOut(orderIdStr.toString(),memberId);

            }

            log.debug("UploadBulkPayOut Ids---" + orderIdStr);
            log.debug("UploadBulkPayOut messgae" + messgae);
            //request.setAttribute("Message", messgae);
            if(messgae.contains("Error") || !messgae.contains("table")){
                request.setAttribute("ERROR", messgae);
            }else{
                request.setAttribute("tableResult", messgae);

            }

            rd = request.getRequestDispatcher("/bulkPayoutUpload.jsp?ctoken=" + user.getCSRFToken());

            rd.forward(request, response);
            return;
        }catch (Exception e){
            log.debug("UploadBulkPayOut Exception" + e);
            request.setAttribute("ERROR", "Bulk Payout Failed");
            rd = request.getRequestDispatcher("/bulkPayoutUpload.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

    }
}
