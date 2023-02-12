package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

/**
 * Created by Admin on 8/2/2017.
 */
public class PartnerInitServlet extends HttpServlet
{
    private static Logger Log = new Logger(PartnerInitServlet.class.getName());
    //Initialize global variables
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        Log.debug("initialising ... ");
        ServletContext application = getServletContext();

        try
        {
            /*Log.debug("Merchants Refreshing ");
            Merchants.refresh();
            Log.debug("Merchants Refreshed ");*/

            /*	System.out.println("setting  Global Variables ");

            FileInputStream fis=new FileInputStream(application.getRealPath("")+"WEB-INF/classes/properties/.properties");

            DataInputStream in=new DataInputStream(fis);
            Properties prop=new Properties();

            prop.load(in);
            fis.close();
            in.close();
            Enumeration enu=prop.propertyNames();
            while(enu.hasMoreElements())
            {
                String key=(String)enu.nextElement();
                application.setAttribute(key,prop.getProperty(key));
            }
            System.out.println("Global Variables set ");
            */

            Log.debug("Initialising  variables ");

            ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.tc");
            application.setAttribute("merchantpath",RB.getString("merchantpath"));
            application.setAttribute("NOOFRECORDSPERPAGE",RB.getString("NOOFRECORDSPERPAGE"));
            application.setAttribute("PODNOOFRECORDSPERPAGE",RB.getString("PODNOOFRECORDSPERPAGE"));
            /*Enumeration enu = RB.getKeys();
            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                System.out.println("loading...... " + key);
                application.setAttribute(key, RB.getString(key));
            }*/


            Log.debug(" variables initialised");


        }
        catch (Exception ex)
        {
            Log.error("Exception occure:::::",ex);

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
            out.println(Merchants.hash.toString());
        }
        catch (Exception ex)
        {
            Log.error("variables not initilased ",ex);
            out.println(Functions.NewShowConfirmation("Error", "Internal System Error "));
            //return;
        }

    }
}
