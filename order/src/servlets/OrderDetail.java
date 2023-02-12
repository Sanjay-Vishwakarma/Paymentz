import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayTypeService;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public class OrderDetail extends HttpServlet
{
    private static Logger log = new Logger(OrderDetail.class.getName());

    public static Hashtable getTransactionDetails(String trackingId, String amount, String partnerId, String fromtype)
    {
        Hashtable transDetails = null;
        Set<String> tables = new HashSet<String>();
        Set<String> gatewaySet = new HashSet<String>();
        gatewaySet.addAll(GatewayTypeService.getGateways());
        System.out.println("gwset--"+gatewaySet);
        Iterator i = gatewaySet.iterator();
        while (i.hasNext())
        {
            tables.add(Database.getTableName((String) i.next()));
        }
        Iterator j = tables.iterator();
        try
        {
            Connection conn = null;
            String tablename = "";
            String fields = "";
            ResultSet rs = null;
            while (j.hasNext())
            {
                StringBuffer query = new StringBuffer();
                tablename = ((String) j.next());
                log.debug("Table Name ; " + tablename);
                try
                {
                    conn = Database.getConnection();
                    if (tablename.equals("transaction_icicicredit"))
                    {
                        fields = "t.accountid as accountid,t.expdate as expdate,t.dtstamp as dtstamp, t.icicitransid as trackingid,m.currency as currency, t.description as description, t.amount, DATE_FORMAT(from_unixtime(t.dtstamp),'%d %b %Y') as date, t.status" + " from " + tablename + " as t ";
                    }
                    else
                    {
                        fields = "t.accountid as accountid,t.expdate as expdate,t.dtstamp as dtstamp, t.trackingid as trackingid,t.currency as currency, t.description as description, t.amount, DATE_FORMAT(from_unixtime(t.dtstamp),'%d %b %Y') as date, t.status" + " from " + tablename + " as t ";
                    }
                    query.append("select " + fields + " where ");
                    if (tablename.equals("transaction_icicicredit"))
                    {
                        query.append("t.icicitransid ='" + trackingId + "'");
                    }
                    else
                    {
                        query.append("t.trackingid ='" + trackingId + "'");
                    }
                    //query.append(" and t.totype= '" + fromtype + "'");
                    if (amount != null && !amount.equals(""))
                        query.append(" and t.amount= " + amount);

                    log.debug("tracking id query :::" + query.toString());
                    rs = Database.executeQuery(query.toString(), conn);
                    transDetails = Database.getHashFromResultSet(rs);
                    if (transDetails.size() > 0)
                    {
                        break;
                    }
                }
                catch (SystemError systemError)
                {
                    log.error("Error", systemError);
                }
                catch (SQLException e)
                {
                    log.error("Error", e);
                }
                catch (Exception ex)
                {
                    log.error("Error", ex);
                }
                finally
                {
                    if (conn != null)
                        conn.close();
                }
            }
            return transDetails;
        }
        catch (SQLException e)
        {
            log.error("Error", e);
        }
        return transDetails;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("Anonymous");

        String trackingid ="";
        String amount="";
        String error="";

        String fromtype = (String) session.getAttribute("company");
        String partnerId = (String) session.getAttribute("partnerid");
        try
        {
            trackingid = ESAPI.validator().getValidInput("trackingid", req.getParameter("trackingid"), "Numbers", 20, true);
        }
        catch(ValidationException e)
        {
            log.error("Invalid TrackingID",e);
            RequestDispatcher rd=req.getRequestDispatcher("/index.jsp?action=E&ctoken="+user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
        try
        {
            amount = ESAPI.validator().getValidInput("amount", req.getParameter("amount"), "Amount", 20, true);
        }
        catch(ValidationException e)
        {
            log.error("Invalid Amount",e);
            RequestDispatcher rd=req.getRequestDispatcher("/index.jsp?action=F&ctoken="+user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
        try
        {
            Hashtable hash = getTransactionDetails(trackingid, amount,partnerId,fromtype);
            String billingDescriptor=getBillingDescriptor(trackingid);
            if(hash!=null)
            {
                hash.put("records", "" + hash.size());
                hash.put("totalrecords", "" + (hash.size() - 1));
                req.setAttribute("orderdetails", hash);
                req.setAttribute("billingDescriptor", billingDescriptor);
            }
            else
            {
                //out.println(Functions.NewShowConfirmation("Sorry", "No records found for Tracking Id :"+trackingid));
            }
        }
        catch (Exception e)
        {
            log.error("Exception:::::::::::"+e);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/order.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public String getBillingDescriptor(String trackingId)throws Exception
    {
        Connection conn=null;
        String billingDescriptor="-";
        String tablename="";
        Set<String> tables = new HashSet();
        Set<String> gatewaySet = new HashSet();
        gatewaySet.addAll(GatewayTypeService.getGateways());
        Iterator i = gatewaySet.iterator();
        while(i.hasNext())
        {
            tables.add(Database.getTableName((String)i.next()));
        }
        try
        {
            conn = Database.getConnection();
            Iterator j = tables.iterator();
            while (j.hasNext())
            {
                tablename = ((String) j.next());
                if (!"transaction_icicicredit".equals(tablename))
                {
                    String fieldName = "responsedescriptor";
                    if ("transaction_qwipi".equals(tablename) || "transaction_ecore".equals(tablename))
                    {
                        fieldName = "responseBillingDescription";
                    }

                    String detailQuery = "SELECT " + fieldName + " FROM " + tablename + "_details WHERE status in ('capturesuccess','authsuccessful') and trackingid=" + trackingId + " and " + fieldName + " IS NOT NULL AND " + fieldName + " !=\"\"";
                    log.debug("detailQuery====" + detailQuery);
                    ResultSet deResultSet = Database.executeQuery(detailQuery, conn);
                    if (deResultSet.next())
                    {
                        billingDescriptor = deResultSet.getString("responsedescriptor");
                        break;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            log.error("Error" ,e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return billingDescriptor;
    }
}