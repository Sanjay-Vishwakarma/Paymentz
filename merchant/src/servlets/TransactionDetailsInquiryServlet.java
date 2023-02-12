import com.directi.pg.*;
import com.manager.MerchantConfigManager;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.vo.TerminalVO;
import com.merchant.vo.requestVOs.Merchant;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by Admin on 11/7/2020.
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

        transactionLogger.error("Inside merchant TransactionDetailsInquiryServlet servlet cron ---");

        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        Functions functions                         = new Functions();
        HttpSession session                         = req.getSession();
        TransactionManager transactionManager       = new TransactionManager();
        Merchants merchants                         = new Merchants();
        User user                                   = (User) session.getAttribute("ESAPIUserSessionKey");
        CommonBankTransactionInquiry commonBankTransactionInquiry   = new CommonBankTransactionInquiry();
        String merchantid                                           = (String) session.getAttribute("merchantid");

        if (!merchants.isLoggedIn(session))
        {
            transactionLogger.debug("Admin is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String trackingid   = req.getParameter("trackingid");
        String status       = req.getParameter("status");
        String gateway      = req.getParameter("gateway");
        String toid         = req.getParameter("toid");
        int totalAuthstartedCount   = 0;
        int successCounter          = 0;
        int failCounter             = 0;
        HashMap responsemap         = null;


        try
        {     Hashtable hash = null;

            if("payoutstarted".equalsIgnoreCase(status) || "payoutfailed".equalsIgnoreCase(status)){
                responsemap = commonBankTransactionInquiry.commomPayoutInquiryStatus(trackingid, status, "", "", gateway, "", "", "", "");
            }
            else{

                responsemap = commonBankTransactionInquiry.commomTransactionInquiryStatus(trackingid, status, "","",gateway,"","","","");
            }
            HashMap trackinghash;
            TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
            String accountid                    = req.getParameter("accountid");
            //String cardpresent = req.getParameter("cardpresent");
           // String statusType = req.getParameter("statusType");
           String cardtype      = req.getParameter("cardtype");
            System.out.println("cardtype------>"+cardtype);
            String statusflag   = "all";
            Set<String> gatewayTypeSet      = new HashSet();
            TerminalManager terminalManager = new TerminalManager();
            TerminalVO terminalVO       = new TerminalVO();
            StringBuffer trackingIds    = new StringBuffer();

                if (functions.isValueNull(cardtype)&& cardtype.equals("CP"))
                {
                    System.out.println("inside if cardtype");
                    // Entering for Card present (transaction_card_present)
                    hash    = transactionentry.listCardTransactions("", "", "", trackingid, null, 1, 1, false, gatewayTypeSet, accountid, terminalVO, "", "", "", "", "", "", "all", "", "", "");
                }
                else
                {
                    System.out.println("inside else hash");
                    // Entering for Card not present(transaction_common)
                    hash    = transactionentry.listTransactions("", "", "", trackingid, null, 1, 1, false, gatewayTypeSet, accountid, terminalVO, "", "", "", "", "", "", "all", "", "", "","");
                }
            req.setAttribute("transactionsdetails", hash);
          //  req.setAttribute("TrackingIDList1", trackinghash);
        }
        catch (Exception e)
        {   transactionLogger.error("Exception:::: errror in doPost",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        try
        {
            if (functions.isValueNull(merchantid))
            {
                Map<String,Object> merchantTemplateSetting  = new HashMap<String, Object>();
                merchantTemplateSetting                     = merchantConfigManager.getSavedMemberTemplateDetails(merchantid);
                req.setAttribute("merchantTemplateSetting",merchantTemplateSetting);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while getting template preference",e);
        }
        transactionLogger.error("before RequestDispatcher ");
        RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        transactionLogger.error("after  RequestDispatcher ");
    }

}
