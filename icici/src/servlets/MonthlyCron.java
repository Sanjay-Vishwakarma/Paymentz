import com.directi.pg.Balance;
import com.directi.pg.Logger;
import com.directi.pg.MonthlyReportCron;
import com.jalios.jdring.AlarmEntry;
import com.jalios.jdring.AlarmListener;
import com.jalios.jdring.AlarmManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MonthlyCron extends HttpServlet
{
    int minutes = 0;
    int hours = 0;
    int dayofmonth = 0;

    AlarmManager mgr = null;
    AlarmEntry ent = null;
    private static Logger logger = null;

    public void init() throws ServletException
    {
        logger = new Logger(MonthlyCron.class.getName());
        logger.debug("Entering Init");
        logger.debug("Entering Init");
        ServletContext application = getServletContext();
        synchronized (application)
        {
            if (application.getAttribute("MONTHLYLOADED") != null)
            {

                logger.debug("MONTHLYCron already Loaded. Again callded by - " + this.hashCode());
                throw new ServletException("CLass Already Loaded");
            }
            else
            {

                logger.debug("loading MONTHLYCron with classID " + this.hashCode());
                application.setAttribute("MONTHLYLOADED", "true");
            }
        }
        try
        {
            minutes = Integer.parseInt((String) application.getAttribute("monthly_minutes"));
            logger.debug("minutes loaded");
            hours = Integer.parseInt((String) application.getAttribute("monthly_hours"));
            logger.debug("hours loaded");
            dayofmonth = Integer.parseInt((String) application.getAttribute("monthly_dayofmonth"));

        }
        catch (Exception ex)
        {   logger.error("Exception in  initalizing monthly cron:",ex);

        }

        logger.debug("Alarm is set to :\n " + minutes + "m " + hours + "h " + dayofmonth + "d ");

        try
        {
            mgr = new AlarmManager();
            ent = mgr.addAlarm(minutes, hours, dayofmonth, -1, -1, -1, new AlarmListener()
            {
                public void handleAlarm(AlarmEntry entry)
                {
                    try
                    {
                        logger.debug("Calling MONTHLYCron");

                        logger.debug("Calling MonthlyReportCron");
                        MonthlyReportCron mr = new MonthlyReportCron();
                        mr.sendReport(0, 0);

                        Balance nr = new Balance();
                        nr.sendBalance(0, 0);

                        logger.debug("Sucess Called MonthlyReportCron");


                    }
                    catch (Exception e)
                    {
                        logger.error("Exception:::",e);
                    }
                }
            });


            logger.debug("isRepetitive : " + ent.isRepetitive);

        }
        catch (Exception e)
        {

            logger.error("Error during cron activity",e);

        }
        logger.debug("Leaving Init");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out = response.getWriter();
        String stop = request.getParameter("stop");

        if (stop != null && stop.equals("Y"))
        {
            mgr.removeAllAlarms();
            logger.debug("Removed All Alarms");
        }
        int month = 0, year = 0;
        if (request.getParameter("month") != null)
            month = Integer.parseInt((String) request.getParameter("month"));
        if (request.getParameter("year") != null)
            year = Integer.parseInt((String) request.getParameter("year"));

        MonthlyReportCron mr = new MonthlyReportCron();
        mr.sendReport(month, year);

        Balance nr = new Balance();
        nr.sendBalance(month, year);


    }

}
