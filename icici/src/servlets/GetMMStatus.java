import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.paymentgateway.MyMonederoPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.MyMonederoRequestVO;
import com.directi.pg.core.valueObjects.MyMonederoResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 4/1/13
 * Time: 12:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetMMStatus extends HttpServlet
{
    public static Logger logger = new Logger(GetMMStatus.class.getName());
    Connection connection = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in ActionHistory");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String error="";
        Connection conn=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        String wctxnid=null,currency=null,amount=null,accountid=null;
        String trackingid=null;
        String searchtype=req.getParameter("searchtype");

        if(searchtype==null)
            searchtype="";
        if(searchtype.equals("trackingid"))
        {
            try{
                trackingid= ESAPI.validator().getValidInput("trackingid",req.getParameter("trackingid"),"Numbers",20,false );
            }
            catch(ValidationException ve)
            {
                logger.error("VALIDATION EXCEPTION OCCURED",ve);
                error+="<center><font color=red>Invalid Tracking ID</font></center><br>";
            }
            if(trackingid!=null)
            {
                try{
                    conn= Database.getConnection();
                    String query="select paymentid as wctxnid,accountid,amount,currency,status,refundamount from transaction_common where trackingid=?";
                    ps=conn.prepareStatement(query);
                    ps.setString(1,trackingid);
                    rs=ps.executeQuery();
                    rs.next();
                    accountid = rs.getString("accountid");
                    amount = rs.getString("amount");
                    currency = rs.getString("currency");
                    wctxnid=rs.getString("wctxnid");

                    if(rs.getString("status").equals("reversed"))
                        amount=rs.getString("refundamount");
                }
                catch(SystemError se)
                {

                    logger.error("SYSTEM ERROR OCCURED",se);
                    error+="<center><font color=red>Error while connecting to Database</font></center><br>";
                }
                catch(SQLException se)
                {
                    logger.error("SQL EXCEPTION OCCURED",se);
                    error+="<center><font color=red>Error while Executing Query</font></center><br>";
                }
                finally {
                    Database.closeResultSet(rs);
                    Database.closePreparedStatement(ps);
                    Database.closeConnection(conn);
                }
            }
        }
        else if(searchtype.equals("details"))
        {
            try{
                wctxnid= ESAPI.validator().getValidInput("wctxnid",req.getParameter("wctxnid"),"SafeString",20,false );
            }
            catch(ValidationException ve)
            {
                logger.error("VALIDATION EXCEPTION OCCURED",ve);
                error+="<center><font color=red>Invalid WCTXNID</font></center><br>";
            }
            try{
                amount= ESAPI.validator().getValidInput("amount",req.getParameter("amount"),"Numbers",20,false );
            }
            catch(ValidationException ve)
            {
                logger.error("VALIDATION EXCEPTION OCCURED",ve);
                error+="<center><font color=red>Invalid Amount</font></center><br>";
            }
            try{
                accountid= ESAPI.validator().getValidInput("accountid",req.getParameter("accountid"),"Numbers",4,false );
            }
            catch(ValidationException ve)
            {
                logger.error("VALIDATION EXCEPTION OCCURED",ve);
                error+="<center><font color=red>Invalid Account ID</font></center><br>";
            }
            try{
                currency= ESAPI.validator().getValidInput("currency",req.getParameter("currency"),"SafeString",4,false );
            }
            catch(ValidationException ve)
            {
                logger.error("VALIDATION EXCEPTION OCCURED",ve);
                error+="<center><font color=red>Invalid Currency</font></center><br>";
            }
        }
        else
        {
            error+="<center><font color=red>Invalid Search Type</font></center><br>";
        }

        Hashtable details=new Hashtable() ;
        if(error.equals(""))
        {
            logger.debug("Setting Details");
            MyMonederoRequestVO mmreq=new MyMonederoRequestVO();
            GenericTransDetailsVO gentr=new GenericTransDetailsVO();
            gentr.setAmount(amount);
            gentr.setCurrency(currency);
            mmreq.setWctxnid(wctxnid);
            mmreq.setGenericTransDetailsVO(gentr);
            MyMonederoResponseVO mmresp=null;
            logger.debug("Calling Gateway");
            try
            {
                MyMonederoPaymentGateway pg= new MyMonederoPaymentGateway(accountid);
                mmresp=(MyMonederoResponseVO ) pg.processQuery(trackingid,mmreq);
            }
            catch (PZConstraintViolationException e)
            {
                logger.error("SYSTEM ERROR OCCURED",e);
                error+="<center><FONT COLOR=RED> Could Not Connect To The Gateway</font></center><br>";
            }
            catch (PZTechnicalViolationException e)
            {
                logger.error("SYSTEM ERROR OCCURED",e);
                error+="<center><FONT COLOR=RED> Could Not Connect To The Gateway</font></center><br>";
            }
            try{
                if(mmresp!=null);
                {
                    details.put("sourceid",mmresp.getSourceID()!=null?mmresp.getSourceID():"");
                    details.put("destid",mmresp.getDestID()!=null?mmresp.getDestID():"");
                    details.put("error",mmresp.getError()!=null?mmresp.getError():"");
                    details.put("status",mmresp.getStatus()!=null?mmresp.getStatus():"");
                    details.put("date",mmresp.getTransactionDate()!=null?mmresp.getTransactionDate():"");
                    details.put("wctxnid",mmresp.getWctxnid()!=null?mmresp.getWctxnid():"");
                    details.put("trackingid",mmresp.getTrackingid()!=null?mmresp.getTrackingid():"");
                }   }
            catch(Exception e)
            {
                logger.error("ERROR OCCURED",e);
                error+="<center><FONT COLOR=RED> Could Not Connect To The Gateway</font></center><br>";
            }
        }
        logger.debug("Details---"+details);
        if(details.size()>0)
        {
            req.setAttribute("details",details);
        }

        req.setAttribute("searchtype",searchtype);
        req.setAttribute("error",error);
        req.getRequestDispatcher("/getMMStatus.jsp?ctoken="+user.getCSRFToken()).forward(req,res);
    }
}
