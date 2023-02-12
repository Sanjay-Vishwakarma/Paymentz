import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ShipmentStatusDetails extends HttpServlet
{
    static Logger log=new Logger("logger1");
    static final String classname= ShipmentStatusDetails.class.getName();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session= Functions.getNewSession(request);
        ServletContext application = getServletContext();
        if(!CustomerSupport.isLoggedIn(session))
        {
            response.sendRedirect("/support/logout.jsp");
            return;
        }
        User user= (User) session.getAttribute("ESAPIUserSessionKey");
        int pageno=1;
        int records=30;
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno", request.getParameter("SPageno"), "Numbers", 3, true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord", request.getParameter("SRecords"), "Numbers", 3, true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }
        String error="";
        String blank="";
        String success="";
        if(request.getAttribute("success")!=null)
            success= (String) request.getAttribute("success");
        if(request.getAttribute("error")!=null)
            error= (String) request.getAttribute("error");
        if(request.getAttribute("blank")!=null)
            blank= (String) request.getAttribute("blank");
        RequestDispatcher rd;
        try {
            String check = null;
            if(request.getAttribute("check")==null)
            {
                log.debug(classname+" first time call :after click of shipment status details");
            }else{
                check= (String) request.getAttribute("check");
                log.debug(classname+" check box not checked");
            }
            HashMap details=(HashMap)shipmentlist(pageno,records);
            request.setAttribute("totalrecords",details.get("totalrecords"));
            details.remove("totalrecords");
            request.setAttribute("shipmetdetails",details);
            request.setAttribute("check",check);
            log.debug(classname+" forwarding to shipmentStausDetails");
            request.setAttribute("error",error);
            request.setAttribute("blank",blank);
            request.setAttribute("success",success);
            rd=request.getRequestDispatcher("/shipmentStausDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        } catch (SQLException e) {
            log.error(classname+" SQL CONNECTION Exception",e);
            //To change body of catch statement use File | Settings | File Templates.
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    public HashMap shipmentlist(int pageno,int records) throws SQLException {
        Logger logger=new Logger("database");
        HashMap details = new HashMap();
        HashMap inn;
        Connection con=null;
        StringBuffer fquery;
        int i=0;
        int start=(pageno-1)*records;
        int end=records;
        ResultSet rs2;
        try
        {
            con= Database.getConnection();
            StringBuffer query=new StringBuffer("select tc.trackingid as TRACKINGID,tc.description,m.memberid,tc.name,tc.amount,tc.pod,tc.podbatch,tc.status from transaction_common as tc,members as m where tc.toid=m.memberid and tc.pod is not null and tc.podbatch is not null and (tc.status=\"authsuccessful\" or tc.status=\"capturesuccess\")    union select te.trackingid as TRACKINGID,te.description,m.memberid,te.name,te.amount,te.pod,te.podbatch,te.status from transaction_ecore as te,members as m where te.toid=m.memberid and te.pod is not null and te.podbatch is not null and (te.status=\"authsuccessful\" or te.status=\"capturesuccess\")   union select tq.trackingid as TRACKINGID,tq.description,m.memberid,tq.name,tq.amount,tq.pod,tq.podbatch,tq.status from transaction_common as tq,members as m where tq.toid=m.memberid and tq.pod is not null and tq.podbatch is not null and (tq.status=\"authsuccessful\" or tq.status=\"capturesuccess\")  order by TRACKINGID");
            StringBuffer countquery = new StringBuffer("select count(*) from (" + query + ") as temp");
            query.append(" limit "+start+","+end+"");
            rs2 = Database.executeQuery(countquery.toString(), con);
            Statement stmt = con.createStatement();
            ResultSet rs=stmt.executeQuery(query.toString());
            while(rs.next())
            {
                inn=new HashMap();
                inn.put("trackingid",rs.getInt(1));
                inn.put("description",rs.getString(2));
                inn.put("merchantid",rs.getInt(3));
                inn.put("customername",rs.getString(4));
                inn.put("amount",rs.getFloat(5));
                inn.put("pod",rs.getString(6));
                inn.put("podbatch",rs.getString(7));
                inn.put("status",rs.getString(8));
                log.debug(classname+"--DATABASE DATA OPTAINING--");
                log.debug(classname+" trackingid::"+rs.getInt(1)+" description::"+rs.getString(2)+" merchantid::"+rs.getInt(3)+" customername::"+rs.getString(4)+" amount::"+rs.getFloat(5)+" pod::"+rs.getString(6)+" podbatch::"+rs.getString(7));
                fquery=new StringBuffer("select ship.status from `shippingdetails` as ship where ship.trackingid="+rs.getInt(1));
                Statement stmt1=con.createStatement();
                ResultSet rs1=stmt1.executeQuery(fquery.toString());
                if(rs1.next())
                {
                    inn.put("shipstatus",rs1.getString(1));
                    logger.debug(classname+" shippingstatus present::"+rs1.getString(1));
                }
                else
                {
                    inn.put("shipstatus","");
                    logger.debug(" Shipping status not present");
                }
                details.put(i,inn);
                i++;
            }
            if (rs2.next())
            {
                details.put("totalrecords",rs2.getInt(1));
            }
            else
            {
                details.put("totalrecords",0);
            }

        }catch (SystemError e)
        {
            log.error(classname + " SYSTEM ERROR::", e);
        }finally {
            Database.closeConnection(con);
        }
        return details;
    }
}
