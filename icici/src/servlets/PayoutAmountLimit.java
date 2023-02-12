import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

public class PayoutAmountLimit extends HttpServlet
{
    private static Logger logger = new Logger(PayoutAmountLimit.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info(":::::: PayoutAmountLimit Start :::::: ");
        HttpSession session         = request.getSession();
        User user                   = (User) session.getAttribute("ESAPIUserSessionKey");
        boolean flag                = true;
        String errormsg             = "";
        String EOL                  = "<BR>";
        MerchantDAO merchantDAO     = new MerchantDAO();
        Functions functions         = new Functions();
        String error                = "";
        String accountId            = "";
        String gatewayname          = "";
        String currentPayoutAmount          = "";
        String addedPayoutAmount            = "";
        String action                       = "";
        String payoutAmountLimitId          = "";
        RequestDispatcher requestDispatcher = null;
        TransactionDAO transactionDAO       = new TransactionDAO();
        Hashtable<String,String> hashtable  = null;
        GatewayAccountService gatewayAccountService = new GatewayAccountService();
        TransactionManager transactionManager = new TransactionManager();
        try
        {

            if (!Admin.isLoggedIn(session))
            {
                logger.debug("Admin is logout ");
                response.sendRedirect("/icici/logout.jsp");
                return;
            }
            response.setContentType("text/html");
            action = request.getParameter("action");

            logger.info("action--------->" + action);
            request.setAttribute("accountId", accountId);
            request.setAttribute("currentPayoutAmount", currentPayoutAmount);
            request.setAttribute("addedPayoutAmount", addedPayoutAmount);


            if (functions.isValueNull(request.getParameter("gatewayname")))
            {
                if (request.getParameter("gatewayname").split("-").length > 0)
                    gatewayname = request.getParameter("gatewayname").split("-")[0];
                else
                    gatewayname = request.getParameter("gatewayname");

            }
            accountId                   = request.getParameter("accountId");
            currentPayoutAmount         = request.getParameter("currentPayoutAmount");
            addedPayoutAmount           = request.getParameter("addedPayoutAmount");
            payoutAmountLimitId         = request.getParameter("payoutAmountLimitId");

            logger.error("PayoutAmountLimitSet----accountId---->"+accountId);
            logger.error("PayoutAmountLimitSet----currentPayoutAmount---->" + currentPayoutAmount);
            logger.error("PayoutAmountLimitSet----addedPayoutAmount---->"+addedPayoutAmount);
            logger.error("PayoutAmountLimitSet----payoutAmountLimitId---->"+payoutAmountLimitId);
            logger.error("PayoutAmountLimitSet----gatewayname---->"+ gatewayname);

            if ("showPayoutDetails".equalsIgnoreCase(action))
            {
                int pageno      = 1;
                int pagerecords = 15;
                try
                {
                    pageno      = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
                    pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
                    hashtable   = transactionManager.getPayoutAmountLimitDetails(gatewayname, pageno, pagerecords);
                    request.setAttribute("CurrentPayoutDetails", hashtable);
                    logger.error("hashtablesize:" + hashtable.size());
                }
                catch (Exception e)
                {
                    logger.error("Exception while showPayoutDetails : ", e);
                }
                RequestDispatcher rd=request.getRequestDispatcher("/payoutLimitAmount.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }

            if (!ESAPI.validator().isValidInput(request.getParameter("accountId"), request.getParameter("accountId"), "OnlyNumber", 10, false)){
                errormsg = errormsg +"Invalid Account Id. It accepts Only Numeric value"+EOL;
            }

            String pgtypeid = "";
            if (!"updateSingleRecord".equalsIgnoreCase(action))
            {
                if (functions.isValueNull(request.getParameter("pgtypeid")))
                {
                    String str[] = request.getParameter("pgtypeid").split("-");
                    pgtypeid = str[2];
                    request.setAttribute("pgtypeid", request.getParameter("pgtypeid"));
                }
                else
                {
                    errormsg = errormsg + "Invalid Gateway OR AccountID. Please select a proper Gateway and AccountID.";
                }
                logger.error("PayoutAmountLimitSet----pgtypeid---->" + pgtypeid);
                if (functions.isValueNull(pgtypeid) && functions.isValueNull(accountId))
                {
                    if (!gatewayAccountService.isGatewayAccountIDMapped(pgtypeid, accountId))
                    {
                        errormsg = errormsg + "AccountID doesn't exist.";
                    }

                }
            }
            if(!errormsg.toString().isEmpty()){
                request.setAttribute("error", errormsg);
                requestDispatcher = request.getRequestDispatcher("/payoutLimitAmount.jsp");
                requestDispatcher.forward(request, response);
                return;
            }

            if("search".equalsIgnoreCase(action) ){
                hashtable = transactionDAO.getPayoutLimitAmount(accountId);

                if(hashtable== null || hashtable.isEmpty() ){
                    errormsg =  errormsg +"No Record Found"+EOL;
                }
                request.setAttribute("requesHashtable", hashtable);


            }else {

               /* if (!ESAPI.validator().isValidInput("currentPayoutAmount", request.getParameter("currentPayoutAmount"), "Amount", 15, false)){
                    errormsg = errormsg +"Invalid Current Payout Amount. It accepts Only Numeric value"+EOL;
                }*/
                if (!ESAPI.validator().isValidInput("addedPayoutAmount", request.getParameter("addedPayoutAmount"), "Amount", 15, false)){
                    errormsg = errormsg +"Invalid Added Payout Amount. It accepts Only Numeric value"+EOL;
                }

                if(!functions.isValueNull(errormsg)){
                    if("update".equalsIgnoreCase(action) || "updateSingleRecord".equalsIgnoreCase(action)){
                        hashtable                   = transactionDAO.getPayoutLimitAmount(accountId);
                        double currentAmount        = Double.parseDouble(hashtable.getOrDefault("currentPayoutAmount","0"));
                        double addedAmount          = Double.parseDouble(addedPayoutAmount);
                        double currentTotalAmount   = addedAmount +currentAmount ;

                        currentPayoutAmount = currentTotalAmount+"";


                      boolean isUpdated =  transactionDAO.updatePayoutAmountLimit(accountId, addedPayoutAmount, addedPayoutAmount);
                        if(isUpdated){
                            errormsg = "Payout Amount Limit Updated Successfully"+EOL;
                        }else{
                            errormsg = "Payout Amount Limit Not Updated"+EOL;
                        }
                    }else if("save".equalsIgnoreCase(action)){
                        currentPayoutAmount=addedPayoutAmount;

                        hashtable = transactionDAO.getPayoutLimitAmount(accountId);
                        if(hashtable == null || hashtable.isEmpty()){
                         String id =   transactionDAO.insertPayoutAmountLimit(accountId, currentPayoutAmount, addedPayoutAmount);
                            errormsg = "Payout Amount Limit  Set Successfully"+EOL;
                        }else{
                            errormsg = errormsg +"Payout Amount Limit is Already Set"+EOL;
                        }
                    }
                }


            }


            if(!errormsg.toString().isEmpty()){
               request.setAttribute("error", errormsg);
               requestDispatcher = request.getRequestDispatcher("/payoutLimitAmount.jsp");
               requestDispatcher.forward(request, response);
               return;
            }

            }
            catch (Exception e)
            {
                logger.error("Exception while adding PayoutAmountLimit", e);
                error = error + "Could Not Updated PayoutAmountLimit.";
                request.setAttribute("error", error);
            }


        logger.info(":::::: Payout Limit Amount End :::::: ");
         request.setAttribute("error1", error);
         requestDispatcher = request.getRequestDispatcher("/payoutLimitAmount.jsp?ctoken=" + user.getCSRFToken());
        requestDispatcher.forward(request, response);
}
}
