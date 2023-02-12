
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Nov 27, 2012
 * Time: 1:14:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualTerminal  extends HttpServlet
{

    private static Logger log = new Logger(VirtualTerminal.class.getName());
    Connection con = null;
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            doPost(request, response);
        }

        public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException
        {
            log.debug("Enter in VirtualTerminal ");
            HttpSession session = req.getSession();
            Merchants merchants = new Merchants();

            if (!merchants.isLoggedIn(session))
            {   log.debug("member is logout ");
                res.sendRedirect("/merchant/virtualLogout.jsp");
                return;
            }


            User user =  (User)session.getAttribute("ESAPIUserSessionKey");


            String toid = (String)session.getAttribute("merchantid");
            Hashtable hiddenvariables = new Hashtable();
            Hashtable paymodelist = new Hashtable();
            Hashtable cardtypelist = new Hashtable();
            Hashtable terminallist = new Hashtable();
            PreparedStatement pstmt1 = null;
            PreparedStatement pstmt2 = null;
            PreparedStatement pstmt3 = null;
            PreparedStatement pstmt4 = null;
            PreparedStatement pstmt5 = null;
            ResultSet rs1 = null;
            ResultSet rs2 = null;
            ResultSet rs3 = null;
            ResultSet rs4 = null;
            ResultSet rs5 = null;
            try
            {
            con=Database.getConnection();
            String query1= "select clkey,checksumalgo,autoSelectTerminal from members where memberid=?";
            pstmt1= con.prepareStatement(query1);
            pstmt1.setString(1,toid);
            rs1 = pstmt1.executeQuery();

            if (rs1.next())
            {
                hiddenvariables.put("key",rs1.getString("clkey"));
                hiddenvariables.put("checksumAlgorithm",rs1.getString("checksumalgo"));
                hiddenvariables.put("autoSelectTerminal",rs1.getString("autoSelectTerminal"));


            }

            String query2= "select distinct paymodeid from member_account_mapping where memberid=?";
            pstmt2= con.prepareStatement(query2);
            pstmt2.setString(1,toid);
            rs2 = pstmt2.executeQuery();
            while (rs2.next())
            {
                String paymodeid = rs2.getString("paymodeid");
                paymodelist.put(paymodeid,GatewayAccountService.getPaymentTypes(paymodeid));


            }

            String query3= "select distinct cardtypeid from member_account_mapping where memberid=?";
            pstmt3= con.prepareStatement(query3);
            pstmt3.setString(1,toid);
            rs3 = pstmt3.executeQuery();
            while (rs3.next())
            {
                String cardtypeid =  rs3.getString("cardtypeid");
                if("0"!=cardtypeid)
                cardtypelist.put(cardtypeid,GatewayAccountService.getCardType(cardtypeid));


            }

            String query4= "select distinct accountid from member_account_mapping where memberid=?";
            pstmt4= con.prepareStatement(query4);
            pstmt4.setString(1,toid);
            rs4 = pstmt4.executeQuery();
            String accountid = null;
            if (rs4.next())
            {
                accountid =  rs4.getString("accountid");


            }
            if(accountid!=null)
            {
            hiddenvariables.put("currency",GatewayAccountService.getGatewayAccount(accountid).getCurrency());

            }


                String query5= "select distinct terminalid,paymodeid,cardtypeid from member_account_mapping where memberid=?";
                pstmt5= con.prepareStatement(query5);
                pstmt5.setString(1,toid);
                rs5 = pstmt5.executeQuery();
                while (rs5.next())
                {
                    String terminalid =  rs5.getString("terminalid");
                    String paymodeid = rs5.getString("paymodeid");
                    String cardtypeid = rs5.getString("cardtypeid");
                    String terminalDetails = terminalid+"--"+GatewayAccountService.getPaymentTypes(paymodeid)+"--"+GatewayAccountService.getCardType(cardtypeid);
                    terminallist.put(terminalid,terminalDetails);


                }

            }
            catch(Exception e)
            {
                log.error("Exception occur",e);
            }
            finally
            {
                Database.closeResultSet(rs1);
                Database.closeResultSet(rs2);
                Database.closeResultSet(rs3);
                Database.closeResultSet(rs4);
                Database.closeResultSet(rs5);
                Database.closePreparedStatement(pstmt1);
                Database.closePreparedStatement(pstmt2);
                Database.closePreparedStatement(pstmt3);
                Database.closePreparedStatement(pstmt4);
                Database.closePreparedStatement(pstmt5);
                Database.closeConnection(con);
            }

            log.debug("CSRF check successful ");
            String error=req.getParameter("error");
            if(error!=null)
            {
                req.setAttribute("error",error);
            }
            req.setAttribute("hiddenvariables",hiddenvariables);
            req.setAttribute("paymodelist",paymodelist);
            req.setAttribute("cardtypelist",cardtypelist);
            req.setAttribute("terminallist",terminallist);
            RequestDispatcher rd = req.getRequestDispatcher("/virtualTerminal.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
           // session.setMaxInactiveInterval(2);

        }
}
