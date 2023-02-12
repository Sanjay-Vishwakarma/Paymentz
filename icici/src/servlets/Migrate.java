import com.directi.pg.*;
import org.owasp.esapi.User;

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
import java.sql.Statement;
import java.util.Hashtable;

public class Migrate extends HttpServlet
{

   private static Logger log=new Logger(Migrate.class.getName());
    static String classname=Migrate.class.getName();
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        HttpSession session= Functions.getNewSession(request);
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Hashtable role=new Hashtable();
        Hashtable error=new Hashtable();
        Hashtable success=new Hashtable();

        role.put(1,"merchant;members");
        role.put(2, "partner;partners");
        role.put(3,"agent;agents");
        role.put(4,"admin;admin");
        role.put(5,"support;customersupport");
        Connection con=null;
        ResultSet rs = null;
        try
        {
            con=Database.getConnection();
            for(int i=1;i<=role.size();i++)
            {
                String roledata=role.get(i).toString();
                String [] data=roledata.split(";");
                String  fetchq =" select login,accountid from `user` where roles='"+data[0]+"'";
                log.debug(classname+" user Query::"+fetchq.toString());
                Statement stmt=con.createStatement();
                rs=stmt.executeQuery(fetchq.toString());
                while(rs.next())
                {
                        String login=rs.getString(1);
                        String accid=rs.getString(2);
                    String updatequery=" update `"+data[1]+"` set accid="+accid+" where login='"+login+"'";
                    log.debug(classname+" query update");
                    try
                    {
                    Statement stm=con.createStatement();
                   int rss=stm.executeUpdate(updatequery.toString());
                         if(rss>0)
                         {
                        success.put(accid,data[0]+":"+login);
                         }
                        else
                         {
                        error.put(accid,data[0]+":"+login);
                         }
                    }catch(Exception E)
                    {
                      log.error(classname+" exception while updating");

                        error.put(accid,data[0]+":"+login);
                    }
                }
            }
        }
        catch (SystemError systemError)
        {

            log.error(classname + " System error" + systemError);
        }
        catch (SQLException e)
        {

            log.error(classname+" SQLException ::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
            request.setAttribute("success",success);
            request.setAttribute("error",error);
            RequestDispatcher rd=request.getRequestDispatcher("/databaseMigration.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        }

    }
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
}