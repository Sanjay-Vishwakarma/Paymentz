import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class Order extends HttpServlet {
    static Logger log = new Logger(Order.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("Anonymous");


        int start = 0; // start index
        int end = 0; // end index
        String str = null;
        String key = null;
        String checksum = null;
        String merchantid = null;
        ServletContext ctx = getServletContext();
        res.setContentType("text/html");
        log.debug("Entering in Order");
        PrintWriter out = res.getWriter();
        String ccnum=null;
        String firstfour=null;
        String secondfour=null;
        String thirdfour=null;
        String fourthfour=null;
        String firstsix=null;
        String expdate=null;
        String errormsg="";
        String errormsg1="";

        String partnerId = (String) session.getAttribute("partnerid");
        log.debug("partnerid in Order----"+partnerId);
        try
        {

            firstfour=ESAPI.validator().getValidInput("firstfour",req.getParameter("firstfour"),"Numbers",4,false);
            secondfour=ESAPI.validator().getValidInput("secondfour",req.getParameter("secondfour"),"Numbers",4,false);
            thirdfour=ESAPI.validator().getValidInput("thirdfour",req.getParameter("thirdfour"),"Numbers",4,false);
            fourthfour=ESAPI.validator().getValidInput("fourthfour",req.getParameter("fourthfour"),"Numbers",4,false);
            ccnum =""+firstfour+secondfour+thirdfour+fourthfour;
            firstsix = ""+firstfour+secondfour.substring(0,2);
            ccnum = ESAPI.validator().getValidInput("ccnum",ccnum,"CC",16,false);
            req.setAttribute("ccnum",ccnum);
        }
        catch(ValidationException e)
        {
           log.error("Invalid Card Number",e);
           out.println(Functions.NewShowConfirmation("Error", "Invalid Card number"));
            return;
        }

        try
        {
        expdate = ESAPI.validator().getValidInput("month",req.getParameter("month"),"Months",2,false) + "/" + ESAPI.validator().getValidInput("year",req.getParameter("year"),"Years",4,false);
        //ctx.log("Inside Order" + ccnum + ":" + expdate);
            //log.debug("----decryptString-----"+Functions.decryptString(ccnum));
        }
        catch(ValidationException e)
        {
            log.error("Invalid EXP date",e);
            out.println(Functions.ShowMessage("Error", "Invalid exp. Date"));
            return;
        }
        log.debug(expdate+"---expdate");
        log.debug("Done");


        if (ccnum == null || expdate == null) {
            out.println(Functions.ShowMessage("Error", "CCnum or name or expdate not filled in"));
            return;
        }

        if (ccnum != null && ccnum.length() != 16) {
            out.println(Functions.ShowMessage("Error", "Put 16-digits of the Credit Card Number"));
            return;
        }
        Set<String> gatewaySet = new HashSet<String>();
        Connection conn = null;

        StringBuffer query1 = new StringBuffer("select icicitransid as trackingid,accountid from bin_details  where");
        query1.append(" first_six = ?");
        query1.append(" and last_four = ?");
        try
        {
            conn = Database.getConnection();
            //log.debug(query1.toString());
            PreparedStatement pstmt1=conn.prepareStatement(query1.toString());
            pstmt1.setString(1,firstsix);
            pstmt1.setString(2,fourthfour);
            ResultSet rs1 = pstmt1.executeQuery();
            while(rs1.next())
            {
                String accountid = rs1.getString("accountid");
                gatewaySet.add(GatewayAccountService.getGatewayAccount(accountid).getGateway());
            }


        }
        catch (SystemError se) {
            log.error("SystemError is occur::::",se);
            out.println(Functions.ShowMessage("Error", "Internal Error While listing Transaction"));
            //System.out.println(se.toString());
        }
        catch (Exception e) {
            log.error("Exception is occur::::",e);
            out.println(Functions.ShowMessage("Error!", "Internal Error While listing Transaction"));
        }
        finally {
            Database.closeConnection(conn);
        }



        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        String orderby = "";
        StringBuffer query = new StringBuffer();
         //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        firstfour = ESAPI.encoder().encodeForSQL(me,firstfour);
        fourthfour= ESAPI.encoder().encodeForSQL(me,fourthfour);

        if(gatewaySet.size()==0)
        {
         log.debug("gateway list is empty adding icicicrdit");
         gatewaySet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }


        try
        {

            conn = Database.getConnection();

            Iterator i = gatewaySet.iterator();
            while(i.hasNext())
            {
               tablename = Database.getTableName((String)i.next());

                if(tablename.equals("transaction_icicicredit"))
                {
                fields = "t.accountid as accountid,t.expdate as expdate,t.dtstamp as dtstamp, t.icicitransid as trackingid, m.company_name as companyname, m.brandname as brandname,m.currency as currency, t.description as description, t.amount, DATE_FORMAT(from_unixtime(t.dtstamp),'%d %b %Y') as date, t.status"+ " from " + tablename + " as t left join bin_details as b on t.icicitransid=b.icicitransid,members as m";
                }
                else
                {
                fields = "t.accountid as accountid,t.expdate as expdate,t.dtstamp as dtstamp, t.trackingid as trackingid, m.company_name as companyname, m.brandname as brandname,m.currency as currency, t.description as description, t.amount, DATE_FORMAT(from_unixtime(t.dtstamp),'%d %b %Y') as date, t.status"+ " from " + tablename + " as t left join bin_details as b on t.trackingid=b.icicitransid,members as m";
                }

                query.append("select " + fields +" where");
                query.append(" b.first_six ="+firstsix);
                query.append(" and b.last_four ="+fourthfour);
                if(tablename.equals("transaction_icicicredit"))
                {
                query.append(" and t.toid=m.memberid and m.partnerId='"+partnerId+"'");
                }
                else
                {
                query.append(" and t.toid=m.memberid and m.partnerId='"+partnerId+"'");
                }


                if(i.hasNext())
                query.append(" UNION ");

            }

            query.append("order by dtstamp desc ");

            log.debug("card detail query"+query);
            ResultSet rs = Database.executeQuery(query.toString(), conn);
            hash = Database.getHashFromResultSetForOrder(rs,ccnum, expdate);
            //hash = Database.getHashFromResultSet(rs);

            hash.put("records", "" + hash.size());
            hash.put("totalrecords", "" + (hash.size() - 1));

            //	out.println(hash.toString());
            req.setAttribute("orderdetails", hash);
            log.debug("success");

            req.setAttribute("ccnum",ccnum);
            ccnum=null;
            firstfour=null;
            secondfour=null;
            thirdfour=null;
            fourthfour=null;
            firstsix=null;
            expdate=null;
            hash=null;
            RequestDispatcher rd = req.getRequestDispatcher("/order.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);



        }
        catch (SystemError se) {
            log.error("SystemError is occur::::",se);
            out.println(Functions.ShowMessage("Error", "Internal Error While listing Transaction"));
            //System.out.println(se.toString());
        }
        catch (Exception e) {
            log.error("Exception is occur::::",e);
            out.println(Functions.ShowMessage("Error!", "Internal Error While listing Transaction"));
        }
        finally {
            Database.closeConnection(conn);
        }
    }
}