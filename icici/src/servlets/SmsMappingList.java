import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;
import servlets.MailUIUtils;
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
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naushad
 * Date: 2/28/17
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmsMappingList extends HttpServlet
{
    private static Logger log = new Logger(SmsMappingList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in SheepingDetailList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("success");
        MailUIUtils mailUIUtils=new MailUIUtils();
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection connection=null;
        ResultSet resultSet = null;
        String templateId="";
        String eventId="";
        try
        {
            eventId = ESAPI.validator().getValidInput("event",req.getParameter("event"),"SafeString",10,false);
            templateId = ESAPI.validator().getValidInput("template",req.getParameter("template"),"SafeString",10,false);
        }
        catch(ValidationException e)
        {
            log.error("Invalid description",e);
        }
        try
        {
            connection= Database.getConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer qry= new StringBuffer("SELECT * FROM sms_template_event_entity_mapping WHERE mapping_id>0");
            if(eventId!=null && !eventId.equalsIgnoreCase("all"))
            {
                qry.append(" AND event_id="+ESAPI.encoder().encodeForSQL(me,eventId));
            }
            if(templateId!=null && !templateId.equalsIgnoreCase("all"))
            {
                qry.append(" AND template_id="+ESAPI.encoder().encodeForSQL(me,templateId));
            }
            resultSet=Database.executeQuery(qry.toString(), connection);

            HashMap mappingDetail=new HashMap();
            while(resultSet.next())
            {
                HashMap mapping=new HashMap();
                mapping.put("mapping_id",resultSet.getString("mapping_id"));
                mapping.put("event_id",mailUIUtils.getEventNameFromID(resultSet.getString("event_id")));
                mapping.put("from_entity_id",mailUIUtils.getEntityName(resultSet.getString("from_entity_id")));
                mapping.put("to_entity_id",mailUIUtils.getEntityName(resultSet.getString("to_entity_id")));
                mapping.put("template_id",mailUIUtils.getSmsTemplateName(resultSet.getString("template_id")));
                mapping.put("subject",resultSet.getString("subject"));
                mappingDetail.put(resultSet.getString("mapping_id"),mapping);
            }
            req.setAttribute("event",eventId);
            req.setAttribute("template",templateId);
            req.setAttribute("mappingdetail",mappingDetail);
            log.debug(mappingDetail);
        }
        catch (SystemError systemError)
        {
            log.error("System Error",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/smsmappinglist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
