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
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/20/14
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailMappingList extends HttpServlet
{
    private static Logger log = new Logger(MailMappingList.class.getName());
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
            log.error("Invelid description",e);
        }
        try
        {
            connection= Database.getConnection();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer qry= new StringBuffer("SELECT * FROM mappingmailtemplateevententity WHERE mappingId>0");
            if(eventId!=null && !eventId.equalsIgnoreCase("all"))
            {
                qry.append(" AND mailEventId="+ESAPI.encoder().encodeForSQL(me,eventId));
            }
            if(templateId!=null && !templateId.equalsIgnoreCase("all"))
            {
                qry.append(" AND mailTemplateId="+ESAPI.encoder().encodeForSQL(me,templateId));
            }
            resultSet=Database.executeQuery(qry.toString(), connection);

            HashMap mappingDetail=new HashMap();
            while(resultSet.next())
            {
                HashMap mapping=new HashMap();
                mapping.put("mappingId",resultSet.getString("mappingId"));
                mapping.put("mailEventId",mailUIUtils.getEventNameFromID(resultSet.getString("mailEventId")));
                mapping.put("mailFromEntityId",mailUIUtils.getEntityName(resultSet.getString("mailFromEntityId")));
                mapping.put("mailToEntityId",mailUIUtils.getEntityName(resultSet.getString("mailToEntityId")));
                mapping.put("mailTemplateId",mailUIUtils.getTemplateName(resultSet.getString("mailTemplateId")));
                mapping.put("mailSubject",resultSet.getString("mailSubject"));
                mappingDetail.put(resultSet.getString("mappingId"),mapping);
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
        RequestDispatcher rd = req.getRequestDispatcher("/mailmappinglist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
