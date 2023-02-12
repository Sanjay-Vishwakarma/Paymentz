import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Hashtable;
/**
 * Created by Suraj on 7/21/2017.
 */
public class GetPayoutDetails extends HttpServlet{
    private static Logger logger = new Logger(GetPayoutDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{

        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        Functions functions=new Functions();

        if (!merchants.isLoggedIn(session)){
            res.sendRedirect("/merchant/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        String iciciTransId = null;
        String accountId=null;

        try{
            iciciTransId = ESAPI.validator().getValidInput("icicitransid",req.getParameter("icicitransid"),"Numbers",25,false);
            accountId=  ESAPI.validator().getValidInput("accountid",req.getParameter("accountid"),"Numbers",25,false);
        }
        catch(ValidationException e){
            logger.error("Invalid input",e);
            iciciTransId= null;
        }

        Hashtable hash = null;
        String gateway =null;
        if(functions.isValueNull(accountId))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        }

        String tableName = Database.getTableName(gateway);
        StringBuilder query =null;
        if(tableName.equals("transaction_icicicredit")){
            query = new StringBuilder("select status,icicitransid,transid,description,captureamount,refundamount,accountid,terminalid from transaction_icicicredit where icicitransid= ? ");
        }
        else{
            query = new StringBuilder("select status,trackingid as icicitransid,transid,description,currency,captureamount,refundamount,accountid,terminalid,fromtype from ").append(tableName).append(" where trackingid= ? ");
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            hash = Database.getHashFromResultSet(pstmt.executeQuery());
            req.setAttribute("payoutdetails", hash);
            RequestDispatcher rd = req.getRequestDispatcher("/payoutreason.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se){
            logger.error("SystemError:::::",se);
        }
        catch (Exception e){
            logger.error("Exception:::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

    }
}
