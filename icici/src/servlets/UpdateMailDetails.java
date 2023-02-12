import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
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
import java.sql.SQLException;
/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/21/14
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateMailDetails extends HttpServlet
{
    private static Logger log = new Logger(UpdateMailDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection connection=null;
        String mailEntity=null;
        String mailEvent=null;
        String mailTemplate=null;
        String smsTemplate = null;
        String errorMsg="";
        StringBuffer sb=new StringBuffer();
        Functions functions = new Functions();
        if (!ESAPI.validator().isValidInput("entity",req.getParameter("entity"),"StrictString",20,true) || functions.hasHTMLTags(req.getParameter("entity")))
        {
            errorMsg="Invalid Mail Entity Name";
            log.error(errorMsg);
            sb.append(errorMsg);
            sb.append(",");
        }
        else
            mailEntity=req.getParameter("entity");

        if (!ESAPI.validator().isValidInput("event",req.getParameter("event"),"SafeString",60,true) || functions.hasHTMLTags(req.getParameter("event")))
        {
            errorMsg="Invalid Mail Event Name";
            log.error(errorMsg);
            sb.append(errorMsg);
            sb.append(",");
        }
        else
            mailEvent=req.getParameter("event");

        if (!ESAPI.validator().isValidInput("template",req.getParameter("template"),"SafeString",50,true) || functions.hasHTMLTags(req.getParameter("template")))
        {
            errorMsg="Invalid Mail Template Name";
            log.error(errorMsg);
            sb.append(errorMsg);
            sb.append(",");
        }
        else
            mailTemplate=req.getParameter("template");

        if (!ESAPI.validator().isValidInput("smstemplate", req.getParameter("smstemplate"), "SafeString", 50, true) || functions.hasHTMLTags(req.getParameter("smstemplate")))
        {
            errorMsg = "Invalid SMS Template Name";
            log.error(errorMsg);
            sb.append(errorMsg);
            sb.append(",");
        }
        else
            smsTemplate = req.getParameter("smstemplate");

        if(sb.length()>0)
        {
            req.setAttribute("error",sb.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/addmailconfigdetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        PreparedStatement pstmt = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        try
        {
            connection= Database.getConnection();
            if (mailEntity != null || mailEvent != null || mailTemplate != null || smsTemplate != null)
            {
                if(mailEntity!=null && !mailEntity.equals(""))
                {
                    String qry="SELECT COUNT(*) as count FROM mailentity WHERE mailEntityName=?";
                    pstmt=connection.prepareStatement(qry);
                    pstmt.setString(1,mailEntity);
                    rs=pstmt.executeQuery();
                    if(rs.next())
                    {
                        int i=rs.getInt("count");
                        if(i>0)
                        {
                            errorMsg=errorMsg+"<BR> "+ mailEntity+" Name already exists In MailEntity  <BR>";
                        }
                        else
                        {
                            String update="INSERT INTO mailentity (mailEntityName,placeHolderTagName) VALUES (?,?)";
                            p1=connection.prepareStatement(update);
                            p1.setString(1,mailEntity);
                            p1.setString(2,mailEntity+"Email");
                            int j=p1.executeUpdate();
                            if(j==1)
                            {
                                errorMsg=errorMsg+"<BR> "+ mailEntity+" Entity Is Added Successfully.<BR>";
                            }
                            else
                            {
                                errorMsg=errorMsg+"<BR> "+ mailEntity+" Entity Adding Failed.<BR>";
                            }
                        }
                    }
                }
                if(mailEvent!=null && !mailEvent.equals(""))
                {
                    String qry="SELECT COUNT(*) as count FROM mailevent WHERE mailEventName=?";
                    pstmt=connection.prepareStatement(qry);
                    pstmt.setString(1,mailEvent);
                    rs=pstmt.executeQuery();
                    if(rs.next())
                    {
                        int i=rs.getInt("count");
                        if(i>0)
                        {
                            errorMsg=errorMsg+"<BR> "+ mailEvent+" Name already exists In MailEvent. <BR>";
                        }
                        else
                        {
                            String update="INSERT INTO mailevent (mailEventName) VALUES (?)";
                            p1=connection.prepareStatement(update);
                            p1.setString(1,mailEvent);
                            int j=p1.executeUpdate();
                            if(j==1)
                            {
                                errorMsg=errorMsg+"<BR> "+ mailEvent+" Event is Added Successfully.<BR>";
                            }
                            else
                            {
                                errorMsg=errorMsg+"<BR> "+ mailEvent+" Event Adding Failed.<BR>";
                            }
                        }
                    }
                }
                if(mailTemplate!=null && !mailTemplate.equals(""))
                {
                    String qry="SELECT COUNT(*) as count FROM mailtemplate WHERE templateName=?";
                    pstmt=connection.prepareStatement(qry);
                    pstmt.setString(1,mailTemplate);
                    rs=pstmt.executeQuery();
                    if(rs.next())
                    {
                        int i=rs.getInt("count");
                        if(i>0)
                        {
                            errorMsg=errorMsg+"<BR> "+ mailTemplate+" Name already exists In Mail Template. <BR>";
                        }
                        else
                        {
                            String update="INSERT INTO mailtemplate (templateName,templateFileName) VALUES (?,?)";
                            p1=connection.prepareStatement(update);
                            p1.setString(1,mailTemplate);
                            p1.setString(2,mailTemplate+".template");
                            int j=p1.executeUpdate();
                            if(j==1)
                            {
                                errorMsg=errorMsg+"<BR> "+ mailTemplate+" Template Is Added Successfully.<BR>";
                            }
                            else
                            {
                                errorMsg=errorMsg+"<BR> "+ mailTemplate+" Template Adding Failed.<BR>";
                            }
                        }
                    }
                }
                if (smsTemplate != null && !smsTemplate.equals(""))
                {
                    String qry = "SELECT COUNT(*) as count FROM sms_template WHERE sms_template_name=?";
                    pstmt = connection.prepareStatement(qry);
                    pstmt.setString(1, smsTemplate);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        int i = rs.getInt("count");
                        if (i > 0)
                        {
                            errorMsg = errorMsg + "<BR> " + smsTemplate + " SMS template is already available.<BR>";
                        }
                        else
                        {
                            String update = "INSERT INTO sms_template (sms_template_name,sms_emplate_filename) VALUES (?,?)";
                            p1 = connection.prepareStatement(update);
                            p1.setString(1, smsTemplate);
                            p1.setString(2, smsTemplate + ".template");
                            int j = p1.executeUpdate();
                            if (j == 1)
                            {
                                errorMsg = errorMsg + "<BR> " + smsTemplate + " SMS template is added successfully.<BR>";
                            }
                            else
                            {
                                errorMsg = errorMsg + "<BR> " + smsTemplate + " SMS template adding failed.<BR>";
                            }
                        }
                    }
                }
            }
            else
            {
                errorMsg = errorMsg + "<BR>All fields are empty kindly enter record.";
            }
        }
        catch (SystemError systemError)
        {
            log.error("System error while updating mail data",systemError);
        }
        catch (SQLException e)
        {
            log.error("Sql Exception",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(p1);
            Database.closeConnection(connection);
        }
        req.setAttribute("error",errorMsg);
        RequestDispatcher rd = req.getRequestDispatcher("/addmailconfigdetails.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
