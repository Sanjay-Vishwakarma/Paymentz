
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: admin1
 * Date: 2/9/13
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetGatewayAccount extends HttpServlet
{
    static Logger logger = new Logger(GetGatewayAccount.class.getName());


        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            doPost(request, response);
        }

        public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
        {   HttpSession session = req.getSession();
            User user =  (User)session.getAttribute("ESAPIUserSessionKey");



            String accountid=req.getParameter("accountid");


            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountid) ;
            Hashtable hash=new Hashtable();
            hash.put("accountid",gatewayAccount.getAccountId()+"");
            hash.put("aliasname",gatewayAccount.getAliasName());
            hash.put("displayname", gatewayAccount.getDisplayName());
            hash.put("pgtypeid",gatewayAccount.getPgTypeId());
            hash.put("merchantid",gatewayAccount.getMerchantId());
            hash.put("dailyamountlimit",gatewayAccount.getDailyAmountLimit()+"");
            hash.put("dailycardlimit",gatewayAccount.getDailyCardLimit()+"");
            hash.put("monthlyamountlimit",gatewayAccount.getMonthlyAmountLimit()+"");
            hash.put("weeklycardlimit",gatewayAccount.getWeeklyCardLimit()+"");
            hash.put("monthlycardlimit",gatewayAccount.getMonthlyCardLimit()+"");
            hash.put("mintxnamount",gatewayAccount.getMinTransactionAmount()+"");
            hash.put("maxtxnamount",gatewayAccount.getMazTransactionAmount());

            req.setAttribute("hiddenvariables",hash);
            req.getRequestDispatcher("/getGatewayAccount.jsp?ctoken="+user.getCSRFToken()).forward(req,res);
        }


}
