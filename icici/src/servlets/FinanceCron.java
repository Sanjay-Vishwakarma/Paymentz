import com.directi.pg.*;
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
import java.util.*;

public class FinanceCron extends HttpServlet
{
    int minutes = 0;
    int hours = 0;
    int dayofmonth = 0;

    AlarmManager mgr = null;
    AlarmEntry ent = null;
    Logger logger = null;
    Hashtable emailhash = null;

    public void init() throws ServletException
    {
        logger = new Logger(FinanceCron.class.getName());

        logger.debug("Entering Init");
        ServletContext application = getServletContext();
        synchronized (application)
        {
            if (application.getAttribute("FINANCELOADED") != null)
            {

                logger.debug("FINANCECron already Loaded. Again callded by - " + this.hashCode());
                throw new ServletException("CLass Already Loaded");
            }
            else
            {

                logger.debug("loading FINANCECron with classID " + this.hashCode());
                application.setAttribute("FINANCELOADED", "true");
            }
        }

        emailhash = new Hashtable();

        try
        {

            /*
            FileInputStream fis=new FileInputStream(application.getRealPath("")+"WEB-INF/lib/com/directi/pg/finance.properties");
            DataInputStream in=new DataInputStream(fis);
            Properties prop=new Properties();

            prop.load(in);
            fis.close();
            in.close();
            Enumeration enu=prop.propertyNames();
            */
            ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.finance");
            //Hashtable emailhash=new Hashtable();
            Enumeration enu = RB.getKeys();


            while (enu.hasMoreElements())
            {
                String key = (String) enu.nextElement();
                emailhash.put(key, RB.getString(key));
            }



            minutes = Integer.parseInt((String) emailhash.get("financeminutes"));
            logger.debug("minutes loaded");
            hours = Integer.parseInt((String) emailhash.get("financehours"));

            logger.debug("hours loaded");
            dayofmonth = Integer.parseInt((String) emailhash.get("financedayofmonth"));

        }
        catch (Exception ex)
        {
            logger.error("Exception in ::::::::: FinanceCron",ex);

        }


        logger.debug("Finance Alarm is set to :\n " + minutes + "m " + hours + "h " + dayofmonth + "d ");

        try
        {
            mgr = new AlarmManager();
            ent = mgr.addAlarm(minutes, hours, dayofmonth, -1, -1, -1, new AlarmListener()
            {
                public void handleAlarm(AlarmEntry entry)
                {
                    try
                    {
                        logger.debug("Calling Finance ");
                        Finance.sendMail(emailhash, new java.util.Date());
                        logger.debug("Called Finance");

                        logger.debug("Calling NegativeBalance");
                        NegativeBalance nr = new NegativeBalance();
                        nr.sendBalance();
                        logger.debug("Called NegativeBalance");

                        logger.debug("Calling Daily Transaction report ");
                        DailyTransactionReport.sendMail(new Date());
                        logger.debug("Called Daily Transaction report ");

                        logger.debug("Calling AutoCapture");
                        AutoCapture.capture();
                        logger.debug("Sucess Called AutoCapture");

                        logger.debug("Calling AutoReverse");
                        AutoReverse.reverse(null);
                        logger.debug("Sucess Called AutoReverse");

                        logger.debug("Calling ChangeReserve");
                        ChangeReserve.checkAndUpdateMembers();
                        logger.debug("Sucess Called ChangeReserve");

                        logger.debug("Calling FraudAlert  ");
                        FraudAlert fa = new FraudAlert(new java.util.Date());
                        //fa.sendMachineAlert();
                        logger.debug("Called FraudAlert ");


                    }
                    catch (Exception e)
                    {   logger.error("Exception::::::",e);
                        logger.info(e.toString());
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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        try
        {


            PrintWriter out = response.getWriter();

            out.println("emailhash " + emailhash);

            String type = request.getParameter("type");

            if (type != null && type.equals("autocapture"))
            {
                out.println("Capturing Pending Transactions ");
                out.flush();
                AutoCapture.capture();
                out.println(".... Transactions Captured");
                return;
            }

            if (type != null && type.equals("negative"))
            {
                logger.debug("Calling NegativeBalance");
                NegativeBalance nr = new NegativeBalance();
                nr.sendBalance();
                logger.debug("Called NegativeBalance");
                return;
            }

            String mm = request.getParameter("mm");
            String dd = request.getParameter("dd");
            String yy = request.getParameter("yy");


            String tomm = request.getParameter("tomm");
            String todd = request.getParameter("todd");
            String toyy = request.getParameter("toyy");

            String previous = request.getParameter("previous");

            Calendar tocal = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();

            if (mm != null && dd != null && yy != null)
            {
                cal.set(Integer.parseInt(yy), Integer.parseInt(mm), Integer.parseInt(dd));
            }
            else
            {
                out.println("Pass date mm/dd/yy");
                return;
            }


            if (type != null && type.equals("alert"))
            {
                FraudAlert fa = new FraudAlert(cal.getTime());
            }
            else if (type != null && type.equals("transaction"))
            {
                logger.debug("Calling transaction report");
                DailyTransactionReport.sendMail(cal.getTime());
            }
            else
            {
                if (previous == null && mm != null && dd != null && yy != null)
                {
                    logger.debug("Calling Finance.sendMail");


                    try
                    {
                        logger.debug("Calling Finance <br>");
                        //java.util.Date date =new java.util.Date(new Long(Functions.converttomillisec(mm,dd,yy)).longValue());
                        //	Calendar cal = Calendar.getInstance();
                        //	cal.set(Integer.parseInt(yy),Integer.parseInt(mm),Integer.parseInt(dd));

                        logger.debug("date " + cal.getTime() + "<br>");
                        Finance.sendMail(emailhash, cal.getTime());
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception ::::::",e);
                    }

                    logger.debug("Mails Sent");
                }
                else if (previous != null && mm != null && dd != null && yy != null)
                {
                    logger.debug("Calling Finance.sendMail all previous ");

                    if (tomm != null && todd != null && toyy != null)
                    {
                        tocal.set(Integer.parseInt(toyy), Integer.parseInt(tomm), Integer.parseInt(todd));
                    }
                    else
                    {
                        logger.debug("Pass date tomm/todd/toyy");
                        return;
                    }

                    Date now = new Date();

                    try
                    {
                        //	Calendar cal = Calendar.getInstance();
                        //	cal.set(Integer.parseInt(yy),Integer.parseInt(mm),Integer.parseInt(dd));

                        logger.debug("Calling Finance from " + cal.getTime() + " to <br>" + tocal.getTime());
                        //java.util.Date date =new java.util.Date(new Long(Functions.converttomillisec(mm,dd,yy)).longValue());
                        int t = 0;
                        while (true)
                        {
                            t++;
                            out.println("date " + cal.getTime() + "<br>");
                            Finance.sendMail(emailhash, cal.getTime());
                            cal.add(Calendar.DATE, 1);

                            if (tocal.getTime().compareTo(cal.getTime()) == 0)
                            {
                                logger.debug("breaking ");
                                break;
                            }
                        }

                        logger.debug("over " + t);

                    }
                    catch (Exception e)
                    {
                        logger.error("Exception 1:::::",e);
                    }

                    logger.debug("Mails Sent");
                }
                else
                {
                    logger.debug("Pass date mm/dd/yy");
                }
            }//alert if ends
        }
        catch (Exception ex)
        {   logger.error("Exception 2:::::",ex);
            throw new ServletException(ex);
        }

    }

}
