package com.payment.checkers;

import com.directi.pg.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 15/3/14
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class RiskCheckers  extends Checkers
{

    private static Logger log = new Logger(RiskCheckers.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RiskCheckers.class.getName());

    static Vector blockedemail = new Vector();
    static Vector blockeddomain = new Vector();
    static Vector blockedcountry = new Vector();

    public RiskCheckers()
    {
        log.debug("Initializing RiskCheckers.");
        transactionLogger.debug("Initializing RiskCheckers.");
        Connection cn = null;
        try
        {
            cn = Database.getConnection();
            String query = "select emailaddr from blockedemail where type='email'";

            ResultSet rs = Database.executeQuery(query, cn);
            while (rs.next())
            {
                blockedemail.add(rs.getString(1));
            }
            

            query = "select emailaddr from blockedemail where type='domain'";

            rs = Database.executeQuery(query, cn);
            while (rs.next())
            {
                blockeddomain.add(rs.getString(1));
            }
            

            query = "select country from blockedcountry";

            rs = Database.executeQuery(query, cn);
            while (rs.next())
            {
                blockedcountry.add(rs.getString(1));
            }
            

            log.debug("blocked email,domain and country are set in vector.");
            transactionLogger.debug("blocked email,domain and country are set in vector.");
        }
        catch (Exception e)
        {
            log.error("exception while filling static hash for blocked items.",e);
            transactionLogger.error("exception while filling static hash for blocked items.",e);
            // return;
        }
        finally
        {
            Database.closeConnection(cn);
        }
        
    }
    
   public void checkBlockedEmail(boolean proofrequired,String emailaddr, String HRCode )
   {
       log.debug("email check starts ");
       transactionLogger.debug("email check starts ");

       if (blockedemail != null)
       {
           if (blockedemail.contains(emailaddr))
           {
               proofrequired = true;
               HRCode = HRCode + "-BEM";
               log.debug("proof require is true because " + emailaddr + " is blocked by admin.");
               transactionLogger.debug("proof require is true because " + emailaddr + " is blocked by admin.");

           }
       }

   }

   
    public void checkBlockedDomain(boolean proofrequired,String emailaddr, String HRCode )
    {
        log.debug("domain check starts ");
        transactionLogger.debug("domain check starts ");

        String domain = emailaddr.substring(emailaddr.indexOf("@") + 1, emailaddr.length());
        domain = domain.trim();
       

        if (blockeddomain != null && !blockeddomain.isEmpty())
        {
            for (int i = 0; i < blockeddomain.size(); i++)
            {
                if (domain.indexOf((String) blockeddomain.get(i)) > 0) //will chaeck any appender before domain name
                {
                    log.debug("block is true for " + domain + " as " + (String) blockeddomain.get(i) + " is blocked.");
                    transactionLogger.debug("block is true for " + domain + " as " + (String) blockeddomain.get(i) + " is blocked.");
                    proofrequired = true;
                    HRCode = HRCode + "-BDOM";
                    log.debug("proof require is true because " + domain + " is blocked by admin.");
                    transactionLogger.debug("proof require is true because " + domain + " is blocked by admin.");

                    break;
                }
            }
            if (blockeddomain.contains(domain))
            {
                proofrequired = true;
                HRCode = HRCode + "-BDOM";
                log.debug("proof require is true because " + domain + " is blocked by admin.");
                transactionLogger.debug("proof require is true because " + domain + " is blocked by admin.");

            }
        }

    }


    public void checkBlockedIP(boolean proofrequired,String ipaddress,long tempcode, String HRCode )
    {
        log.debug("ip check starts ");
        transactionLogger.debug("ip check starts ");
        Connection con = null;
        try
        {
            String query = "select * from blockedip where startipcode<=? and endipcode>=?";
            con=Database.getConnection();
            PreparedStatement p2=con.prepareStatement(query);
            p2.setLong(1,tempcode);
            p2.setLong(2,tempcode);
            ResultSet iprs = p2.executeQuery();

            if (iprs.next())
            {
                proofrequired = true;
                HRCode = HRCode + "-BIP";
                log.debug("proof require is true because " + ipaddress + " is blocked by admin.");
                transactionLogger.debug("proof require is true because " + ipaddress + " is blocked by admin.");
            }
        }
        catch (SQLException e)
        {
            log.error("Error while checking master card support ", e);
            transactionLogger.error("Error while checking master card support ", e);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkBlockedIP()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause(),null,null);
        }
        catch (SystemError se)
        {
            log.error("Error while checking master card support ", se);
            transactionLogger.error("Error while checking master card support ", se);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkBlockedIP()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause(),null,null);
        }
        finally
        {
            Database.closeConnection(con);
        }


    }


    public void checkBlockedCountry(boolean proofrequired,String country, String HRCode)
    {
        log.debug("different country check starts ");
        transactionLogger.debug("different country check starts ");

        //check whether country is blocked
        if (blockedcountry != null && !blockedcountry.isEmpty())
        {
            if (blockedcountry.contains(country))
            {
                proofrequired = true;
                HRCode = HRCode + "-BCON";
                log.debug("proof required is true because country " + country + " is blocked by admin.");
                transactionLogger.debug("proof required is true because country " + country + " is blocked by admin.");

            }
        }


    }

    public void checkDifferentCountry(boolean proofrequired,String country, String HRCode,long tempcode ,boolean diffCountry)
    {
        log.debug("country check starts ");
        transactionLogger.debug("country check starts ");

        String query = "select country from ipmap where startip<=? and endip>=?";
        Connection con = null;
        String countryinrec="";
        try
        {
            con = Database.getConnection();
            PreparedStatement p3=con.prepareStatement(query);
            p3.setLong(1,tempcode);
            p3.setLong(2,tempcode);
            ResultSet rs0 = p3.executeQuery();
            if (rs0.next())
            {
                countryinrec = rs0.getString("country");
                log.debug("country entered by customer=" + country + " country got:" + countryinrec);
                transactionLogger.debug("country entered by customer=" + country + " country got:" + countryinrec);
                if (!countryinrec.equals(country))
                {
                    diffCountry = true;

                    HRCode = HRCode + "-DCON";
                    log.debug("High Risk fraud caught for diff. country");
                    transactionLogger.debug("High Risk fraud caught for diff. country");
                }
            }
        }
        catch (SQLException e)
        {
            log.error("Error while checking master card support ", e);
            transactionLogger.error("Error while checking master card support ", e);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkDifferentCountry()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause(),null,null);
        }
        catch (SystemError se)
        {
            log.error("Error while checking master card support ", se);
            transactionLogger.error("Error while checking master card support ", se);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkDifferentCountry()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause(),null,null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        // different country check ends

    }

    public void checkEmail(boolean proofrequired,String emailaddr, String HRCode,String ccpan,String boilname,boolean repeatEmail,String tablename)
    {
        log.debug("email check starts ");
        transactionLogger.debug("email check starts ");
        String preboilname = "";
        Calendar cal;
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        long previousMonthTimeinSec = cal.getTimeInMillis() / 1000;
        int emailfraudcount = 0;
        Connection con = null;
        try
        {
            String query = "select count(*),ccnum,boiledname from "+tablename+" where emailaddr=? and dtstamp > ? group by ccnum,boiledname";
            con = Database.getConnection();
            PreparedStatement p5=con.prepareStatement(query);
            p5.setString(1,emailaddr);
            p5.setLong(2,previousMonthTimeinSec);
            ResultSet rs2 = p5.executeQuery();
            while (rs2.next())
            {
                // emailfraudcount += rs2.getInt(1);
                log.debug("decrypt ccnum");
                transactionLogger.debug("decrypt ccnum");
                if (!ccpan.equals(PzEncryptor.decryptPAN(rs2.getString("ccnum"))))
                {
                    preboilname = rs2.getString("boiledname");
                    emailfraudcount += 1;
                    if (!boilname.equals(preboilname) || emailfraudcount > 2)
                    {
                        repeatEmail = true;
                        //HRMessage=HRMessage+"-Different Cards used from the same Email Address with different boiled name\r\n";
                        HRCode = HRCode + "-DEM";
                        log.debug("High Risk fraud caught for same emailaddress diff. boiledname");
                        transactionLogger.debug("High Risk fraud caught for same emailaddress diff. boiledname");
                        break;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            log.error("Error while checking master card support ", e);
            transactionLogger.error("Error while checking master card support ", e);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkEmail()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause(),null,null);
        }
        catch (SystemError se)
        {
            log.error("Error while checking master card support ", se);
            transactionLogger.error("Error while checking master card support ", se);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkEmail()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause(),null,null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public void checkMachine(boolean proofrequired,String machineid, String HRCode,String ccpan,String boilname,boolean repeatmachine,String tablename)
    {
        log.debug("email check starts ");
        transactionLogger.debug("email check starts ");
        String preboilname = "";
        Calendar cal;
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        long previousMonthTimeinSec = cal.getTimeInMillis() / 1000;
        int machinefraudcount = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "select count(*),ccnum,boiledname from "+tablename+" where mid=? and dtstamp > ? group by ccnum,boiledname";
            PreparedStatement p6=con.prepareStatement(query);
            p6.setString(1,machineid);
            p6.setLong(2,previousMonthTimeinSec);
            ResultSet rs3 = p6.executeQuery();
            while (rs3.next())
            {
                //  machinefraudcount += rs3.getInt(1);
                if (!ccpan.equals(PzEncryptor.decryptPAN(rs3.getString("ccnum"))))
                {
                    preboilname = rs3.getString("boiledname");
                    machinefraudcount += 1;
                    if (!boilname.equals(preboilname) || machinefraudcount > 2)
                    {
                        repeatmachine = true;
                        //HRMessage=HRMessage+"-Different Cards used from the same Machine with different boiled name\r\n";
                        HRCode = HRCode + "-DMC";
                        log.debug("**High Risk fraud caught for same machine diff. boiledname");
                        transactionLogger.debug("**High Risk fraud caught for same machine diff. boiledname");
                        break;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            log.error("Error while checking master card support ", e);
            transactionLogger.error("Error while checking master card support ", e);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkMachine()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,e.getMessage(),e.getCause(),null,null);
        }
        catch (SystemError se)
        {
            log.error("Error while checking master card support ", se);
            transactionLogger.error("Error while checking master card support ", se);
            //PZExceptionHandler.raiseAndHandleDBViolationException("RiskChecker.java","checkMachine()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,se.getMessage(),se.getCause(),null,null);
        }
        finally
        {
            Database.closeConnection(con);
        }


    }
    
    public long getIPCode(String ipaddress)
    {
        int ipcode = -9999;
        long tempcode = -9999;

        try
        {
            ipcode = InetAddress.getByName(ipaddress).hashCode();
            tempcode = ipcode - 2147483647;
            tempcode = tempcode + 2147483647L;  //convert into long value.
        }
        catch (UnknownHostException nex)
        {
            log.error("ipaddress is either null or not in properformat. :::::",nex);
            transactionLogger.error("ipaddress is either null or not in properformat. :::::",nex);
            //PZExceptionHandler.raiseAndHandleTechnicalViolationException("RiskCheckers.java","getIPCode()",null,"common","UnKnown Host Exception:::", PZTechnicalExceptionEnum.UNKNOWN_HOST_EXCEPTION,nex.getMessage(),nex.getCause(),null,null);
        }

        return tempcode;

    }



     
    
}
