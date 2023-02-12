import com.directi.pg.Admin;
import com.directi.pg.CommonBankTransactionInquiry;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TransactionManager;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Admin on 7/23/2020.
 */
public class TransactionDetailsInquiryServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(TransactionDetailsInquiryServlet.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }
    protected void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("Inside TransactionDetailsInquiryServlet servlet cron ---");
        Functions functions                         = new Functions();
        HttpSession session                         = req.getSession();
        TransactionManager transactionManager       = new TransactionManager();
        User user                                   = (User) session.getAttribute("ESAPIUserSessionKey");
        CommonBankTransactionInquiry commonBankTransactionInquiry   = new CommonBankTransactionInquiry();
        if (!Admin.isLoggedIn(session))
        {
            transactionLogger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
            String trackingid   = req.getParameter("trackingid");
            String status       = req.getParameter("status");
            String gateway      = req.getParameter("gateway");
            String toid         = req.getParameter("toid");
            int totalAuthstartedCount   = 0;
            int successCounter          = 0;
            int failCounter             = 0;
        HashMap responsemap             = null;
        if("payoutsuccessful".equalsIgnoreCase(status)||"payoutstarted".equalsIgnoreCase(status)||"payoutfailed".equalsIgnoreCase(status)){
            responsemap = commonBankTransactionInquiry.commomPayoutInquiryStatus(trackingid, status, "", "", gateway, "", "", "", "");
        }
        else{

            responsemap = commonBankTransactionInquiry.commomTransactionInquiryStatus(trackingid, status, "","",gateway,"","","","");
        }
   /*     totalAuthstartedCount= (int) responsemap.get("authstartedCount");
        successCounter= (int) responsemap.get("successCounter");
        failCounter= (int) responsemap.get("failCounter");*/

        try
        {     Hashtable hash = null;

            HashMap trackinghash;

            String accountid        = req.getParameter("accountid");
            String cardpresent      = req.getParameter("cardpresent");
            String statusType       = req.getParameter("statusType");
            String statusflag       = "all";
            StringBuffer trackingIds = new StringBuffer();
            if ("CP".equalsIgnoreCase(cardpresent))
            {
                Date date6  = new Date();
                transactionLogger.error("cardPresentlistTransactions startTime---" + date6.getTime());
               hash            = transactionManager.cardPresentlistTransactions(toid, trackingid, "", "", "", "", "", "", "", "", "", "", status, 1, 1, "", false, null, "", "", "", statusflag, "", "", accountid, "", "","","","","");
               trackinghash    = transactionManager.cardPresentlistTrackingIds(toid, trackingid, "", "", "", "", "", "", "", "", "", "", status, 1, 1, "", false, null, "", "", "", statusflag, "", "", accountid, "", "","","","","");
                transactionLogger.error("cardPresentlistTransactions End time--->" + (new Date()).getTime());
                transactionLogger.error("cardPresentlistTransactions Diff time--->" + ((new Date()).getTime() - date6.getTime()));
            }
            else
            {
                Date date7  = new Date();
                transactionLogger.error("listTransactions startTime---" + date7.getTime());
                if(functions.isValueNull(statusType) && "detail".equals(statusType)){
                   hash            = transactionManager.listTransactionsBasedOnDetailStatus(toid, trackingid, "", "", "", "", "", "", "", "", "", "", status, 1, 1, "", false, null, "", "", "", statusflag, "", "", accountid,"", "" ,"","","",statusType,"","","","","");
                   trackinghash    = transactionManager.listTrackingidsBasedOnDetailStatus(toid, trackingid, "", "", "", "", "", "", "", "", "", "", status, 1, 1, "", false, null, "", "", "", statusflag, "", "", accountid, "", "", "", "", "", statusType,"","","","","");
                }else {
                    hash            = transactionManager.listTransactions("", trackingid, "", "", "", "", "", "", "", "", "", "", "", 1, 1, "", false, null, "", "", "", "all", "", "", "","", "","","","" ,"","","","","","");
                   trackinghash    = transactionManager.listTrackingIds(toid, trackingid, "", "", "", "", "", "", "", "", "", "", status, 1, 1, "", false, null, "", "", "", statusflag, "", "", accountid, "", "", "", "", "","","","","","");
                }
                transactionLogger.error("listTransactions End time--->" + (new Date()).getTime());
                transactionLogger.error("hash--->" + hash);
                transactionLogger.error("listTransactions Diff time--->" + ((new Date()).getTime() - date7.getTime()));

            }
            req.setAttribute("transactionsdetails", hash);
            req.setAttribute("TrackingIDList1", trackinghash);
        }
        catch (Exception e)
        {   transactionLogger.error("Exception:::: errror in doPost",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        System.out.println("before RequestDispatcher---->");
        transactionLogger.error("before RequestDispatcher---->");
        RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        System.out.println("after RequestDispatcher---->");
        transactionLogger.error("after RequestDispatcher---->");
    }


}
