import com.directi.pg.CustomerSupport;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class CustomerSupportInitServlet extends HttpServlet
{
    private static Logger Log= new Logger(CustomerSupportInitServlet.class.getName());
  public  void init(ServletConfig config) throws ServletException
  {
      super.init(config);
      Log.debug("initialising ... ");
      ServletContext application = getServletContext();

      try
      {
          Log.debug("CUSTOMER SUPPORT REFRESHING");
          //CustomerSupport.refresh();
          Log.debug("CUSTOMER SUPPORT Refreshed ");
          Log.debug("Initialising  variables ");
          ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.tc");
          Enumeration enu = RB.getKeys();
          while (enu.hasMoreElements())
          {
              String key = (String) enu.nextElement();
              Log.debug("loading...... " + key);
              application.setAttribute(key, RB.getString(key));
          }



Log.debug(" variables initialised");


      }
      catch (Exception ex)
      {
          Log.error("Exception occure:::::",ex);

      }
  }
}
