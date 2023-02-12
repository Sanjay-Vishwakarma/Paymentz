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

public class CSVCron extends HttpServlet
{
    int minutes = 0;
    int hours = 0;
    int dayofmonth = 0;

    AlarmManager mgr = null;
    AlarmEntry ent = null;
    Logger logger = null;
    String hrtmail = null;
    String custnotifymail = null;

    public void init() throws ServletException
    {
        logger = new Logger(CSVCron.class.getName());
        logger.debug("Entering Init");

        final ServletContext application = getServletContext();
        synchronized (application)
        {
            if (application.getAttribute("CSVLOADED") != null)
            {

                logger.debug("CSVCron already Loaded. Again called by - " + this.hashCode());
                throw new ServletException("CLass Already Loaded");
            }
            else
            {

                logger.debug("loading CSVCron with classID " + this.hashCode());
                application.setAttribute("CSVLOADED", "true");
            }
        }

        try
        {
            minutes = Integer.parseInt((String) application.getAttribute("minutes"));
            logger.debug("minutes loaded");
            hours = Integer.parseInt((String) application.getAttribute("hours"));
            logger.debug("hours loaded");
            dayofmonth = Integer.parseInt((String) application.getAttribute("dayofmonth"));

            hrtmail = (String) application.getAttribute("CUSTOMERHRT");
            logger.debug("Customer proof letter template set");
            custnotifymail = (String) application.getAttribute("CUSTOMERNOTIFICATION");
            logger.debug("Customer Transaction 7 day reminder letter set,");
        }
        catch (Exception ex)
        {

            logger.error(" Exception while initalizing cron",ex);
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


                        logger.debug("Calling CSVCreator");
                        CSVCreator csv = new CSVCreator();
                        //csv.generate("388,402","00000317","Y");
                        //csv.generate("388,402","00000486","N");
                        csv.generate();
                        logger.debug("Sucess Called CSVCreator");

                        logger.debug("Calling Status");
                        Status s = new Status();
                        s.setStatus(Integer.parseInt(csv.mindays));
                        logger.debug("Sucess Called Status");

                        PODReminderCron pr = new PODReminderCron();

                        logger.debug("Calling PODReminderCron for 3 days");
                        pr.sendReminder(Integer.parseInt(csv.mindays), 3);
                        logger.debug("Sucess PODReminderCron for 3 days");

                        logger.debug("Calling PODReminderCron for 7 days");
                        pr = new PODReminderCron();
                        pr.sendReminder(Integer.parseInt(csv.mindays), 7);
                        logger.debug("Sucess PODReminderCron for 7 days");

                        logger.debug("Calling PODReminderCron for 11 days");
                        pr = new PODReminderCron();
                        pr.sendReminder(Integer.parseInt(csv.mindays), 11);
                        logger.debug("Sucess PODReminderCron for 11 days");


                        SettlementReminder sr = new SettlementReminder();
                        logger.debug("Calling SettlementReminder for 7 days");
                        sr.sendReminder(7);
                        logger.debug("Sucess SettlementReminder for 7 days");


                        logger.debug("Calling SettlementReminder for 3 days");
                        sr.sendReminder(3);
                        logger.debug("Sucess SettlementReminder for 3 days");

                        ProofReminderCron proof = new ProofReminderCron();

                        logger.debug("Calling ProofReminderCron for 2 days");
                        //proof.sendReminder(4,2);
                        proof.sendCustomerReminder(hrtmail, 6, 2);
                        logger.debug("Sucess ProofReminderCron for 2 days");

                        logger.debug("Calling ProofReminderCron for 4 days");
                        proof.sendCustomerReminder(hrtmail, 6, 4);
                        logger.debug("Sucess ProofReminderCron for 4 days");

                        logger.debug("Calling ProofReminderCron for 5 days");
                        proof.sendCustomerReminder(hrtmail, 6, 5);
                        logger.debug("Sucess ProofReminderCron for 5 days");

                        logger.debug("Calling Cancelled ProofReminderCron");
                        proof.cancelProofRequired(6);
                        logger.debug("Sucess Cancelled ProofReminderCron");


                        logger.debug("Calling Change Status cron ");
                        ChangeStatus cs = new ChangeStatus();
                        cs.change(null);
                        logger.debug("Sucess Change Status cron ");


                        CaptureReminderToMerchant crm = new CaptureReminderToMerchant();
                        logger.debug("Calling CaptureReminderToMerchant for 3 days");
                        crm.sendReminder(3);
                        logger.debug("Sucess CaptureReminderToMerchant for 3 days");


                        CaptureReminder cr = new CaptureReminder();
                        logger.debug("Calling CaptureReminder for 3 days");
                        cr.sendReminder(3);
                        logger.debug("Sucess CaptureReminder for 3 days");

                        TransactionReminderToCustomer trc = new TransactionReminderToCustomer();
                        logger.debug("Calling TransactionReminderToCustomer for 7 days");
                        trc.sendReminder(custnotifymail, 7);
                        logger.debug("Sucess TransactionReminderToCustomer for 7  days");

                        /*
                          logger.info( "Calling Fund Transfer cron " );
                          Reseller r = new Reseller();
                          String transferedFundString = r.transferFund( null );
                          logger.info( "Sucessfully called Fund Transfer cron " );

                          //send this string to SFNB
                          logger.info( "Sending String to SFNB: " + transferedFundString.toString() );
                          StringBuffer data = new StringBuffer(); //to collect data from SFNB
                          try
                          {
                              String checksum = Functions.generateChecksum( transferedFundString.toString() , ( String ) application.getAttribute( "SFNB_KEY" ) );
                              StringBuffer url = new StringBuffer( ( String ) application.getAttribute( "SFNB_ADDFUND_URL" ) );
                              url.append( "?addfundstring=" + URLEncoder.encode( transferedFundString.toString() ) );
                              url.append( "&checksum=" + checksum );

                              URL sfnbURL = new URL( url.toString() );
                              logger.info( "Connecting to: " + sfnbURL.toExternalForm() );
                              HttpURLConnection con = ( HttpURLConnection ) sfnbURL.openConnection();
                              con.setDoInput( true );
                              logger.info( "Successfully Connected to url, retrieving data............" );
                              InputStream in = con.getInputStream();
                              BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

                              String line;
                              while ( ( line = br.readLine() ) != null )
                              {
                                  data.append( line );
                              }
                              logger.info( "Got from SFNB = " + data.toString() );
                          }
                          catch ( IOException e )
                          {
                              logger.info( "Error got while sending transfer fund String" + e );
                              Mail.sendmail( "admin@.com" , "Do_Not_Reply@.com" , "Error got  " , "\r\n\r\nError was received while sending transfer fund string. Please check. Below string was trying to send\r\n" + transferedFundString.toString() + "\r\n\r\nRegards,\r\n Team." );

                          }
                          catch ( Exception e )
                          {
                              logger.info( "Error got while sending transfer fund  String" + e );
                              Mail.sendmail( "admin@.com" , "Do_Not_Reply@.com" , "Error got  " , "\r\n\r\nError was received while sending transfer fund string. Please check. Below string was trying to send\r\n" + transferedFundString.toString() + "\r\n\r\nRegards,\r\ Team." );

                          }

                          //if got something from SFNB update record from our side
                          //I will get conformation in this format  merchantid:status:trf-transid~merchantid:status:trf-transid

                          try
                          {
                              logger.info( "Updating  record." );
                              r.completeTransfer( data.toString() );
                              logger.info( " record successfully updated." );
                          }
                          catch ( Exception e )
                          {
                              logger.info( "Error got while sending transfer fund  String" + e );
                              Mail.sendmail( "admin@
                             .com" , "Do_Not_Reply@.com" , "Error got  " , "\r\n\r\nError was received while updating status in  record. Please check. Below string was received from SFNB.\r\n" + data.toString() + "\r\n\r\nRegards,\r\ Team." );

                          }

                        */

                        /*  IPHistoryRemovalCron hrc=new IPHistoryRemovalCron();
                          logger.info("Calling removeOldRecord ");
                          hrc.removeOldRecord();
                          logger.info("Sucess removeOldRecord ");

                          */
                        /*
						logger.info("Calling NegativeBalance");
						NegativeBalance nr=new NegativeBalance();
						nr.sendBalance();
						logger.info("Called NegativeBalance");

						ProofReminder prf=new ProofReminder();
						logger.info("Calling ProofReminder for 5 days");
						prf=new ProofReminder();
						prf.sendReminder(5);
						logger.info("Sucess ProofReminder for 5 days");
						*/

                    }
                    catch (Exception e)
                    {
                        logger.error("Exception in CVSCrone::::::",e);
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
        ServletContext application = getServletContext();
        String stop = request.getParameter("stop");
        String icicitransid = request.getParameter("trackingid");
        //  String transferfund = request.getParameter( "transferfund" );

        if (stop != null && stop.equals("Y"))
        {
            mgr.removeAllAlarms();
            out.println("Removed All Alarms");
        }

        try
        {
            ChangeStatus cs = new ChangeStatus();
            cs.change(icicitransid);

            logger.debug("Calling CSVCreator");
            CSVCreator csv = new CSVCreator();
            csv.generate();
            logger.debug("Sucess Called CSVCreator");

            SettlementReminder sr = new SettlementReminder();
            logger.debug("Calling SettlementReminder for 7 days");
            sr.sendReminder(7);
            logger.debug("Sucess SettlementReminder for 7 days");

        }
        catch (Exception e)
        {

            logger.error("Error during cron activity",e);

        }
        /*   if ( transferfund != null && transferfund.equalsIgnoreCase( "Y" ) )
           {
               try
               {
                   out.println( "Calling Fund Transfer cron<br> " );
                   Reseller r = new Reseller();
                   String transferedFundString = r.transferFund( null );
                   out.println( "Sucessfully called Fund Transfer cron <br>" );

                   //send this string to SFNB
                   out.println( "Sending String to SFNB:" + transferedFundString + " <br>" );

                   String checksum = Functions.generateChecksum( transferedFundString.toString() , ( String ) application.getAttribute( "SFNB_KEY" ) );
                   StringBuffer url = new StringBuffer( ( String ) application.getAttribute( "SFNB_ADDFUND_URL" ) );
                   url.append( "?addfundstring=" + URLEncoder.encode( transferedFundString.toString() ) );
                   url.append( "&checksum=" + checksum );
                   out.println( "Final URL: " + url + "<br>" );
                   URL sfnbURL = new URL( url.toString() );
                   out.println( "Connecting to: " + sfnbURL.toExternalForm() + "<br>" );
                   HttpURLConnection con = ( HttpURLConnection ) sfnbURL.openConnection();
                   con.setDoInput( true );
                   out.println( "Successfully Connected to url, retrieving data............<br><br>" );
                   InputStream in = con.getInputStream();
                   BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
                   StringBuffer data = new StringBuffer();

                   String line;
                   while ( ( line = br.readLine() ) != null )
                   {
                       data.append( line );
                   }
                   out.println( "Got from SFNB =" + data.toString() + "<br>" );
                   try
                   {
                       out.println( "Updating record.<br>" );
                       r.completeTransfer( data.toString() );
                       out.println( " record successfully updated.<br>" );
                   }
                   catch ( Exception e )
                   {
                       logger.info( "Error got while sending transfer fund  String" + e );
                       Mail.sendmail( "admin@.com" , "Do_Not_Reply@.com" , "Error got  " , "\r\n\r\nError was received while updating status in  record. Please check. Below string was received from SFNB.\r\n" + data.toString() + "\r\n\r\nRegards,\r\ Team." );

                   }
               }
               catch ( Exception e )
               {
                   out.println( "Error occured" + e.getMessage() );
               }
           }  */

    }

}
