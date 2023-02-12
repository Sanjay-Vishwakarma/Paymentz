import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.logicboxes.cron.CronManager;
import com.logicboxes.util.Util;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

public class InitServlet extends HttpServlet
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.csv");
    final static ResourceBundle RBicici = LoadProperties.getProperty("com.directi.pg.ICICI");
    final static ResourceBundle RBPZ = LoadProperties.getProperty("com.directi.pg.tc");
    static Vector blockedemail = new Vector();
    static Vector blockeddomain = new Vector();
    static Vector blockedcountry = new Vector();
    static Logger logger = new Logger(InitServlet.class.getName());
    //Initialize global variables
    public void init(ServletConfig config) throws ServletException
    {
        Connection cn = null;
        super.init(config);
        logger.debug("initialising ... ");
        ServletContext application = getServletContext();

        try
        {
            //application.setAttribute("noOfClients", "0");
            Admin.refresh();
            application.log("initialising cvs properties..............");

            Enumeration enu = RB.getKeys();
            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                application.log("loading...... " + key);
                application.setAttribute(key, RB.getString(key));
            }

            application.log("CSV variables initialised");
            Enumeration icicienu = RBicici.getKeys();
            while (icicienu.hasMoreElements())
            {
                String icicikey = (String) icicienu.nextElement();
                application.log("loading...... " + icicikey);
                application.setAttribute(icicikey, RBicici.getString(icicikey));
            }

            application.log("ICICI variables initialised");
            Enumeration Enum = RBPZ.getKeys();
            application.setAttribute("merchantpath",RBPZ.getString("merchantpath"));
            application.setAttribute("NOOFRECORDSPERPAGE",RBPZ.getString("NOOFRECORDSPERPAGE"));
            application.setAttribute("PODNOOFRECORDSPERPAGE",RBPZ.getString("PODNOOFRECORDSPERPAGE"));
            /*
            {


            }*/

            application.log("variables initialised");
        }
        catch (Exception ex)
        {
            application.log("variables not initialsed " + ex.toString());
        }

        try
        {

            BufferedReader rin = new BufferedReader(new FileReader(application.getRealPath("/WEB-INF/lib/com/directi/pg/customer_hrt.template")));
            String temp = null;
            StringBuffer rawdataBuf = new StringBuffer();
            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();
            application.setAttribute("CUSTOMERHRT", rawdataBuf.toString());
            logger.debug("CUSTOMERHRT File Set");
        }
        catch (Exception ex)
        {
            logger.error("CUSTOMERHRT File Not Found ",ex);
        }
        try
        {
            BufferedReader rin = new BufferedReader(new FileReader(application.getRealPath("/WEB-INF/lib/com/directi/pg/merchant_hrt.template")));
            String temp = null;
            StringBuffer rawdataBuf = new StringBuffer();

            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();

            application.setAttribute("MERCHANTHRT", rawdataBuf.toString());
            logger.debug("MERCHANTHRT File Set");
        }
        catch (Exception ex)
        {
            logger.error("MERCHANTHRT File Not Found ",ex);
        }

        try
        {
            BufferedReader rin = new BufferedReader(new FileReader(application.getRealPath("/WEB-INF/lib/com/directi/pg/customer_notification.template")));
            String temp = null;
            StringBuffer rawdataBuf = new StringBuffer();
            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();
            logger.debug("CUSTOMERNOTIFICATION File Set " + application.getRealPath("/WEB-INF/lib/com/directi/pg/customer_notification.template"));
            application.setAttribute("CUSTOMERNOTIFICATION", rawdataBuf.toString());
        }
        catch (Exception ex)
        {
            logger.error("CUSTOMERNOTIFICATION File Not Found ",ex);
        }

        try
        {
            BufferedReader rin = new BufferedReader(new FileReader(application.getRealPath("/WEB-INF/lib/com/directi/pg/doProofReceivedMail.template")));
            String temp = null;
            StringBuffer rawdataBuf = new StringBuffer();
            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();
            application.setAttribute("DOPROOFRECEIVED", rawdataBuf.toString());
            logger.debug("DOPROOFRECEIVED MAIL TEMPLATE in merchant " + application.getRealPath("/WEB-INF/lib/com/directi/pg/doProofReceivedMail.template"));
        }
        catch (IOException e)
        {
            logger.error("doProofReceivedMail.template File Not Found ",e);
           // return;
        }
        try
        {
            cn = Database.getConnection();
            String query = "select emailaddr from blockedemail where type='email'";
            ResultSet rs = Database.executeQuery(query, cn);
            while (rs.next())
            {
                blockedemail.add(rs.getString(1));
            }
            application.setAttribute("BLOCKEDEMAIL", blockedemail);

            query = "select emailaddr from blockedemail where type='domain'";
            rs = Database.executeQuery(query, cn);
            while (rs.next())
            {
                blockeddomain.add(rs.getString(1));
            }
            application.setAttribute("BLOCKEDDOMAIN", blockeddomain);

            query = "select country from blockedcountry";
            rs = Database.executeQuery(query, cn);
            while (rs.next())
            {
                blockedcountry.add(rs.getString(1));
            }
            application.setAttribute("BLOCKEDCOUNTRY", blockedcountry);

            logger.debug("blocked email,domain and country are set in vector.");
        }
        catch (Exception e)
        {
            logger.error("exception while filling static hash for blocked items.",e);
        }
        finally
        {
            Database.closeConnection(cn);
        }

         try
        {
            application.log("Before loading CronManager");
            CronManager.getInstance();
            application.log("After loading CronManager");
        }
        catch (Throwable e)
        {
            application.log("Error in loading CronManager " + Util.getStackTrace(e));
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        PrintWriter out = res.getWriter();

        try
        {
            ServletContext application = getServletContext();
            Enumeration enu = RB.getKeys();

            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                out.println(key + "<br>");
                application.setAttribute(key, RB.getString(key));
            }

            //seting template path
            out.println("<BR> csv variables initialised <BR>");

            Enumeration icicienu = RBicici.getKeys();
            while (icicienu.hasMoreElements())
            {
                String icicikey = (String) icicienu.nextElement();
                out.println(icicikey + "<br>");
                application.setAttribute(icicikey, RBicici.getString(icicikey));
            }

            out.println("<BR>ICICI variables initialised<BR>");

        }
        catch (Exception ex)
        {
            out.println("variables not initilased " + ex.toString());
            //return;
        }

    }
}