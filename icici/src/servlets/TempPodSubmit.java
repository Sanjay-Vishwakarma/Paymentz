import com.directi.pg.*;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
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
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;


public class TempPodSubmit extends HttpServlet
{

    private static Logger logger = new Logger(TempPodSubmit.class.getName());

    StringBuffer mailbody = null;
    String name = null;
    String value = null;
    boolean captured = false;

    Database db = null;
    Connection conn = null;
    String query = null;
    int count = 1;

    Enumeration enu = null;

    Hashtable podhash = null;

    String amt = null;
    String icicitransid = null;
    String merchantid = null;
    String description = null;
    String authid = null;
    float val = 0;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        logger.debug("success");

        logger.debug("Entering in TempPodSubmit");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        res.setContentType("text/html");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        PrintWriter out = res.getWriter();
        db = new Database();

        enu = req.getParameterNames();

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        captured = false;

        amt = null;
        icicitransid = null;
        description = null;
        authid = null;
        val = 0;

        podhash = new Hashtable();

        //logger.info(mailbody.toString());
        count = 1;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        try
        {

            while (enu.hasMoreElements())
            {

                name = (String) enu.nextElement();
                value = req.getParameter(name);
                //out.println("<br><br>"+name +" : "+ value +"<br>");




                if (!name.startsWith("RRN_") && !name.startsWith("capturecode_") && !value.trim().equals(""))
                {
                    mailbody = new StringBuffer("Following Transaction have been Captured Successfully.\r\n\r\n");
                    mailbody.append("trackingid").append("\t").append("description").append("\t").append("Amount\r\n");
                    mailbody.append("----------").append("\t").append("-----------").append("\t").append("------\r\n\r\n");

                    query = "update transaction_icicicredit set captureid=?,capturecode=?,capturereceiptno=?,status='capturesuccess',captureresult='Capture Done Manually using MA Interface.' where icicitransid=? and status='capturestarted'";

                    conn = db.getConnection();
                    pstmt=conn.prepareStatement(query);
                    pstmt.setString(1,value);
                    pstmt.setString(2,req.getParameter("capturecode_" + name));
                    pstmt.setString(3,req.getParameter("RRN_" + name));
                    pstmt.setString(4,name);
                    int num = pstmt.executeUpdate();
                    logger.debug("Number of tranactions occured " + num);

                    out.println("Tracking Id : " + name + "<br>");
                    out.println("Capture Id : " + value + "<br><br>");







                    query = "select captureamount,icicitransid,authid,toid,description from transaction_icicicredit where status='capturesuccess' and icicitransid=?";
                    pstmt1=conn.prepareStatement(query);
                    pstmt1.setString(1,name);
                    rs = pstmt1.executeQuery();

                    if (rs.next())
                    {

                        val = rs.getFloat("captureamount");
                        //amt = String.valueOf((int)(val*100));
                        merchantid = rs.getString("toid");
                        icicitransid = rs.getString("icicitransid");
                        description = rs.getString("description");
                        authid = rs.getString("authid");

                        mailbody.append(icicitransid).append("\t").append(description).append("\t").append(val).append("\r\n");
                        captured = true;
                        podhash.put(Integer.toString(count),new String (icicitransid+"|"+"capturesuccess"));
                        count++;


                         // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntry(icicitransid,String.valueOf(val),ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table
                    }
                    PreparedStatement pstmt2 = null;
                    ResultSet rs1 = null;
                    try
                    {
                        if (captured)
                        {
                            query = "select contact_emails from members where memberid=?";
                            pstmt2=conn.prepareStatement(query);
                            pstmt2.setString(1,merchantid);
                            rs1 = pstmt2.executeQuery();
                            if (rs1.next())
                            {
                                String contact_emails = rs1.getString("contact_emails");
                                logger.debug("calling SendMAil for Merchant -capture " + contact_emails);
                                Mail.sendmail(contact_emails, "", "Confirmation Capture Successfully", mailbody.toString());
                                logger.debug("called SendMAil for Merchant-capture");
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        logger.error("Failed SendMAil for Merchant-capture ", ex);

                    }
                    finally
                    {
                        Database.closeResultSet(rs1);
                        Database.closePreparedStatement(pstmt2);

                    }


                }
                sSuccessMessage.append(mailbody);
                //if ends

            }//while

             req.setAttribute("podcomfirm",podhash);




        }

        catch (SystemError se)
        {
            mailbody.append("\r\n\r\nFollowing Transaction have NOT been Captured .\r\n\r\n");
            mailbody.append("trackingid").append("\t").append("description").append("\t").append("Amount\r\n");
            mailbody.append("----------").append("\t").append("-----------").append("\t").append("------\r\n\r\n");
            mailbody.append(icicitransid).append("\t").append(description).append("\t").append(val).append("\r\n");

            PreparedStatement pstmt3 = null;
            try
            {
                String error = se.toString();
                int pos = error.indexOf("#1234#");

                if (pos != -1 && error.length() > pos + 6 + 1)
                {
                    error = error.substring(pos + 6 + 1);
                    //update data if exception in processCaptureAndTransaction
                    query = "update transaction_icicicredit set captureresult=? where icicitransid=?";

                    conn = db.getConnection();
                    pstmt3=conn.prepareStatement(query);
                    pstmt3.setString(1,error);
                    pstmt3.setString(2,name);
                    pstmt3.executeUpdate();
                }
            }
            catch (SQLException ise)
            {

                 sErrorMessage.append("Error while update : <br> \r\n");
            }
            catch (SystemError ise)
            {

                sErrorMessage.append("Error while update : <br> \r\n");
            }
            finally
            {
                Database.closePreparedStatement(pstmt3);
                Database.closeConnection(conn);
            }
            logger.error("Error while update :",se);

            sErrorMessage.append("Captured is not done in any Transection <br> \r\n");
        }
        catch (Exception e)
        {
            logger.error("Error while capture :" , e);

            sErrorMessage.append("Transaction is not done Invalid Input <br> \r\n");
        }//try catch ends
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/podconfirm.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
        return;
    }//post ends
}
