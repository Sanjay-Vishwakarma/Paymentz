import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.core.GatewayAccountService;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.DefaultProductList;
import com.invoice.vo.InvoiceVO;
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
import java.util.List;
import java.util.TreeMap;

//import java.io.PrintWriter;
//import java.util.ArrayList;

/**
 * Created by Trupti on 5/31/2017.
 */
public class InvoiceConfiguration extends HttpServlet
{
    private static Logger log = new Logger(InvoiceConfiguration.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        Merchants merchants = new Merchants();
        //PrintWriter out = response.getWriter();
        InvoiceVO invoiceVO = null;
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        //TreeMap terminallist = new TreeMap();
        Hashtable hiddenvariables = new Hashtable();
        Hashtable paymodelist = new Hashtable();
        Connection con = null;
        Functions functions = new Functions();
        //String initTerminalValue = "";

        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");

        PreparedStatement pstmt4 = null;
        PreparedStatement pstmt5 = null;
        ResultSet rs4 = null;
        ResultSet rs5 = null;

        try
        {
            String memberid = (String) session.getAttribute("merchantid");
            invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(memberid);
            String redirectpage = "/invoiceConfiguration.jsp?ctoken=" + user.getCSRFToken();
            request.setAttribute("invoicevo", invoiceVO);

           /* if (functions.isValueNull(invoiceVO.getTerminalid()))
            {
                TreeMap<String,String> map = invoiceEntry.getPaymodeCardtype(invoiceVO.getTerminalid());
                initTerminalValue = invoiceVO.getTerminalid() + "-" + map.get(invoiceVO.getTerminalid());
            }*/

            con=Database.getRDBConnection();
            //con= Database.getConnection();

            String query4= "select distinct accountid from member_account_mapping where memberid=?";
            pstmt4= con.prepareStatement(query4);
            pstmt4.setString(1,memberid);
            rs4 = pstmt4.executeQuery();
            String accountid = null;
            if (rs4.next())
            {
                accountid =  rs4.getString("accountid");
            }
/*            rs4.close();
            pstmt4.close();*/
           /* if(accountid!=null)
            {
                hiddenvariables.put("currency", GatewayAccountService.getGatewayAccount(accountid).getCurrency());
            }*/
            //String query5= "SELECT DISTINCT mam.terminalid, mam.paymodeid, gt.currency, mam.cardtypeid FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE mam.memberid=? AND gt.currency=? AND mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid order by terminalid asc";
            String query5= "SELECT DISTINCT /*mam.terminalid,*/ mam.paymodeid, mam.cardtypeid FROM member_account_mapping AS mam WHERE mam.memberid=? and mam.isActive='Y'";
            pstmt5= con.prepareStatement(query5);
            pstmt5.setString(1,memberid);
            //pstmt5.setString(2,invoiceVO.getCurrency());
            rs5 = pstmt5.executeQuery();
            log.debug("terminal query for invoice----"+pstmt5);
            while (rs5.next())
            {
                //String terminalid =  rs5.getString("terminalid");
                String paymodeid = rs5.getString("paymodeid");
                String cardtypeid = rs5.getString("cardtypeid");
                //String currency = rs5.getString("currency");
                //String terminalDetails = terminalid+"-"+GatewayAccountService.getPaymentTypes(paymodeid)+"-"+ GatewayAccountService.getCardType(cardtypeid);
                //terminallist.put(terminalid,terminalDetails);

            }
            /*rs5.close();
            pstmt5.close();
*/
            List<String> unitLists = invoiceEntry.getUnitList(memberid);

            List<DefaultProductList> defaultProductList = null;

            defaultProductList = invoiceEntry.getDefaultProductList(invoiceVO.getMemberid());


            request.setAttribute("hiddenvariables", hiddenvariables);
            request.setAttribute("paymodelist",paymodelist);
            //request.setAttribute("terminallist",terminallist);
            //request.setAttribute("initTerminalValue",initTerminalValue);
            request.setAttribute("unitList",unitLists);
            request.setAttribute("defaultProductList",defaultProductList);

            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);

        }
        catch(Exception e)
        {
            log.error("Exception occur",e);
        }
        finally
        {
            Database.closeResultSet(rs4);
            Database.closeResultSet(rs5);
            Database.closePreparedStatement(pstmt4);
            Database.closePreparedStatement(pstmt5);
            Database.closeConnection(con);
        }
    }
}