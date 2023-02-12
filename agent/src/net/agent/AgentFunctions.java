package net.agent;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.checkers.PaymentChecker;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationCredentialsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: saurabh
 * Date: 2/7/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentFunctions
{
    /* static String agent="agent";*/
    public static final String ROLE="agent";
    private static Logger logger = new Logger(AgentFunctions.class.getName());

    public boolean isAgent(String login) throws SystemError
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("check isAGENT method");
            /* logger.debug("ROLES::"+ROLE+" login::"+login);*/
            String selquery = "select accountid from `user` where login=? and roles=?";

            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            pstmt.setString(2,ROLE);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMember method: ",e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean isLoggedInAgent(HttpSession sess)
    {
        if (sess.getAttribute("merchantid") == null || sess.getAttribute("password") == null || sess.getAttribute("username") == null)
            return false;
        else
            return true;
    }

    public String generateKey(int size)
    {
        String pwdData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = pwdData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuffer key = new StringBuffer();
        int index = -1;

        for (int i = 0; i < size; i++)
        {
            index = rand.nextInt(len);
            key.append(pwdData.substring(index, index + 1));
        }

        return key.toString();
    }

    /*public AgentAuthenticate agentLoginAuthentication(String login, String password) throws SystemError
    {    //Connection conn = null;
        String fpasswd = null;
        String passwd =null;

        String hashPassword = null ;
        try
        {
            hashPassword = ESAPI.authenticator().hashPassword(password,login) ;
        }
        catch(Exception e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }
        AgentAuthenticate agentauthenticate = new AgentAuthenticate();

        logger.debug("check username and password in database");
        String query = "select * from agents where login = ? and (passwd = ? or (fpasswd= ? and (fdtstamp +3600) > unix_timestamp(now())))";

        agentauthenticate.authenticate = "false";
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1,login);
            pstmt.setString(2,hashPassword);
            pstmt.setString(3,hashPassword);
            ResultSet result = pstmt.executeQuery();


            if (result.next())
            {   fpasswd=result.getString("fpasswd");
                passwd = result.getString("passwd");

                agentauthenticate.agentid = result.getInt("agentId");
                agentauthenticate.activation = result.getString("activation");
                agentauthenticate.contactemails = result.getString("contact_emails");
                agentauthenticate.address=result.getString("address");
                agentauthenticate.agentname=result.getString("agentName");
                agentauthenticate.template=result.getString("template");
                agentauthenticate.telno=result.getString("telno");
                agentauthenticate.logoname=result.getString("logoName");
                if (passwd.equals(hashPassword))
                {
                    agentauthenticate.authenticate = "true";

                }

                if (fpasswd.equals(hashPassword))
                {
                    agentauthenticate.authenticate = "forgot";

                }

            }
            result=null;
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return agentauthenticate;
    }*/

    /*public boolean agentchangePassword(String oldpwd, String newpwd, String memberid, User user)
    {
        logger.debug("Updating new password in Database");
        int i = 0;
        Connection conn = null;
        String hashNewPassword = null ;
        String hashOldPassword = null ;
        String login = null;
        //Getting login name from member table
        try
        {
            conn = Database.getConnection();
            String query2 = "select login from agents where agentId = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setString(1,memberid);

            ResultSet rs = pstmt2.executeQuery();
            if (rs.next())
            {
                login = rs.getString("login");
            }

        }
        catch (SystemError se)
        {
            logger.error("System error occured::::",se);
            return false;
        }
        catch (Exception e)
        {
            logger.error("Getting Exception in changepassword method ::::", e);
            return false;
        }



        // Changing password in user table

        try
        {
            ESAPI.authenticator().changePassword(user,oldpwd,newpwd,newpwd);
        }
        catch(Exception e)
        {
            logger.error("Change password throwing Authetication Exception ",e);
            return false;
        }


        // getting hash password
        try
        {
            hashNewPassword = ESAPI.authenticator().hashPassword(newpwd,login) ;
            hashOldPassword = ESAPI.authenticator().hashPassword(oldpwd,login) ;
        }
        catch(Exception e)
        {
            logger.error("Getting Exception in changepassword method while doing hashing::::", e);
        }


        String updQuery = "update agents set passwd = ?, fpasswd=''  where agentId = ? and (passwd = ? or (fpasswd=? and (fdtstamp +3600) > unix_timestamp(now())))";


        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(updQuery);
            pstmt.setString(1,hashNewPassword);
            pstmt.setString(2,memberid );
            pstmt.setString(3,hashOldPassword);
            pstmt.setString(4,hashOldPassword);
            i = pstmt.executeUpdate();
            logger.debug("update records from change password");

        }
        catch (SystemError se)
        {
            logger.error("System error occure::::",se);
        }
        catch (Exception e)
        {
            logger.error("Getting Exception in changepassword method ::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return true;

    }*/

    public AgentAuthenticate agentLoginAuthentication(String login,HttpServletRequest request,String partnerid) throws SystemError
    {    //Connection conn = null;
        String fpasswd = null;
        String passwd =null;

        String hashPassword = null ;
        /*try
        {
            hashPassword = ESAPI.authenticator().hashPassword(password,login) ;
        }
        catch(Exception e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }*/
        AgentAuthenticate agentauthenticate = new AgentAuthenticate();

        logger.debug("check username and password in database");
//        String query = "select * from agents where login = ? and (passwd = ? or (fpasswd= ? and (fdtstamp +3600) > unix_timestamp(now())))";
        String query = "select * from agents where login =? and partnerid=? ";
        PaymentChecker paymentChecker = new PaymentChecker();
        String ipAddress = Functions.getIpAddress(request);
        agentauthenticate.authenticate = "false";

        PreparedStatement pstmt = null;
        ResultSet result = null;
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, login);
            pstmt.setString(2,partnerid);
//            pstmt.setString(2,hashPassword);
//            pstmt.setString(3,hashPassword);
            result = pstmt.executeQuery();


            if (result.next())
            {
                System.out.println("inside result.");
                fpasswd=result.getString("fpasswd");
                //passwd = result.getString("passwd");

                agentauthenticate.agentid = result.getInt("agentId");
                agentauthenticate.activation = result.getString("activation");
                agentauthenticate.contactemails = result.getString("contact_emails");
                agentauthenticate.address=result.getString("address");
                agentauthenticate.agentname=result.getString("agentName");
                agentauthenticate.template=result.getString("template");
                agentauthenticate.telno=result.getString("telno");
                //agentauthenticate.logoname=result.getString("logoName");
                agentauthenticate.partnerid=result.getString("partnerid");
                /*if (passwd.equals(hashPassword))
                {
                    agentauthenticate.authenticate = "true";

                }*/

                if (!"".equals(fpasswd))
                {
                    agentauthenticate.authenticate = "forgot";

                }

                if (!paymentChecker.isIpWhitelistedForAgent( result.getString("agentId"), ipAddress))
                {
                    throw new SystemError("Error : Ip is NOT WHITELISTED");
                }
            }
            else
            {
                logger.debug(" agent details not found in agentLoginAuthentication()");
                throw new SystemError("Error: UNAUTHORIZED USER");
            }

            result=null;
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(result);
            Database.closeConnection(conn);
        }
        return agentauthenticate;
    }


    public boolean agentchangePassword(String oldpwd, String newpwd, long accid, User user) throws AuthenticationCredentialsException
    {
        logger.debug("Updating new password in Database");
        int i = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String hashNewPassword = null ;
        String hashOldPassword = null ;
        String login = null;
        //Getting login name from member table
        try
        {
            conn = Database.getConnection();
            /* String query2 = "select login from agents where agentId = ?";*/
            String query2 = "select login from `user` where accountid = ? and roles=?";
            pstmt = conn.prepareStatement(query2);
            pstmt.setLong(1, accid);
            pstmt.setString(2,ROLE);


            rs = pstmt.executeQuery();
            if (rs.next())
            {
                login = rs.getString("login");
            }

        }
        catch (SystemError se)
        {
            logger.error("System error occured::::",se);
            return false;
        }
        catch (Exception e)
        {
            logger.error("Getting Exception in changepassword method ::::", e);
            return false;
        }
        finally   //add new finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);

        }


        // Changing password in user table

        try
        {
            ESAPI.authenticator().changePassword(user,oldpwd,newpwd,newpwd);
        }
        catch(Exception e)
        {
            logger.error("Change password throwing Authetication Exception ",e);
            if(e.getMessage().toLowerCase().contains("mismatch"))
            {
                throw new AuthenticationCredentialsException("Password MISMATCH", "Authentication failed for password change on user: " +login);
            }
            return false;
        }


        // getting hash password
        /*try
        {
            hashNewPassword = ESAPI.authenticator().hashPassword(newpwd,login) ;
            hashOldPassword = ESAPI.authenticator().hashPassword(oldpwd,login) ;
        }
        catch(Exception e)
        {
            logger.error("Getting Exception in changepassword method while doing hashing::::", e);
        }*/

        /*and (passwd = ? or (fpasswd=? and (fdtstamp +3600) > unix_timestamp(now())))  down condition*/
        String updQuery = "update agents set fpasswd=''  where accid = ? and login=?";
        PreparedStatement pstmt1 = null;

        try
        {
            conn = Database.getConnection();
            pstmt1= conn.prepareStatement(updQuery);
//            pstmt.setString(1,hashNewPassword);
            pstmt1.setLong(1,accid);
            pstmt1.setString(2,login);
//            pstmt.setString(3,hashOldPassword);
//            pstmt.setString(4,hashOldPassword);
            i = pstmt1.executeUpdate();
            logger.debug("update records from change password");

        }
        catch (SystemError se)
        {
            logger.error("System error occure::::",se);
        }
        catch (Exception e)
        {
            logger.error("Getting Exception in changepassword method ::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }

        return true;
    }

    /*public boolean agentForgotPassword(String login,User user) throws SystemError
    {
        Transaction transaction = new Transaction();
        boolean flag = false;
        if (isAgent(login))
        {

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            String hashPassword = null ;
            String oldPasshash = null;
            logger.debug("Entering forgotPassword() method : "+fpasswd);
            try
            {

                hashPassword = ESAPI.authenticator().hashPassword(fpasswd,login) ;
            }
            catch(Exception e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }

            Connection conn = null;
            //Getting login name from member table
            try
            {
                conn = Database.getConnection();
                String query2 = "select passwd from agents where login = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1,login);

                ResultSet rs = pstmt2.executeQuery();
                if (rs.next())
                {
                    oldPasshash = rs.getString("passwd");
                }

            }
            catch (SystemError se)
            {
                logger.error("System error occured::::",se);
                return false;
            }
            catch (Exception e)
            {
                logger.error("Getting Exception in changepassword method ::::", e);
                return false;
            }
            finally
            {
                Database.closeConnection(conn);
            }

            //Updated the temporary password in user table
            try
            {
                ((PzAuthenticator)ESAPI.authenticator()).forgetPassword(user,oldPasshash,hashPassword,hashPassword);
            }
            catch(Exception e)
            {
                logger.error("Change password throwing Authetication Exception ",e);
                return false;
            }

            logger.debug("user table updated with temporary password");


            try
            {
                conn = Database.getConnection();

                String query = "update agents set passwd= ? , fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,hashPassword);
                pstmt.setString(2,hashPassword);
                pstmt.setString(3,login);
                int success = pstmt.executeUpdate();
                if (success > 0)
                {
                    ResultSet rs = null;
                    logger.debug("new temporary password has been set");



                    // send mail to member warning to use this password within 24 hr.


                    String query1 = "select contact_emails,agentId from agents where login= ?";

                    pstmt= conn.prepareStatement(query1);
                    pstmt.setString(1,login);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String emailAddr = rs.getString("contact_emails");
                        String agentId = rs.getString("agentId");
                        logger.debug(fpasswd +"for"+login);
                        MailService mailService=new MailService();
                        HashMap mailValues=new HashMap();
                        mailValues.put(MailPlaceHolder.TOID,agentId);
                        mailValues.put(MailPlaceHolder.PASSWORD,fpasswd);
                        mailService.sendMail(MailEventEnum.AGENT_FORGOT_PASSWORD,mailValues);

                        flag=true;
                    }
                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating member table.");
                }
                logger.debug("query has been committed for forgotpassword  ");
            }//end try
            catch (SQLException e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }
            finally
            {
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        return flag;

    }*/

    public boolean agentForgotPassword(String login,long accid,User user,String remoteAddr,String Header,String actionExecuterId) throws SystemError
    {
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        boolean flag = false;
        if (isAgent(login))
        {
            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            String hashPassword = null ;
            String oldPasshash = null;
            String accountid=null;
            logger.debug("Entering forgotPassword() method : "+fpasswd);
            try
            {
                hashPassword = ESAPI.authenticator().hashPassword(fpasswd,login) ;
            }
            catch(Exception e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }

            Connection conn = null;
            //Getting login name from member table
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            try
            {
                conn = Database.getConnection();
                String query2 = "select hashedpasswd,accountid from `user` where login = ? and roles=? and accountid=?";
                pstmt = conn.prepareStatement(query2);
                pstmt.setString(1,login);
                pstmt.setString(2,ROLE);
                pstmt.setLong(3,accid);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    oldPasshash = rs.getString(1);
                    accountid=rs.getString(2);
                }

            }
            catch (SystemError se)
            {
                logger.error("System error occured::::",se);
                return false;
            }
            catch (Exception e)
            {
                logger.error("Getting Exception in changepassword method ::::", e);
                return false;
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeResultSet(rs);
                Database.closeConnection(conn);
            }

            //Updated the temporary password in user table
            try
            {
                ((PzAuthenticator)ESAPI.authenticator()).forgetPassword(user,oldPasshash,hashPassword,hashPassword);
            }
            catch(Exception e)
            {
                logger.error("Change password throwing Authetication Exception ",e);
                return false;
            }

            logger.debug("user table updated with temporary password");

            PreparedStatement pstmt1 = null;
            ResultSet rs1 = null;
            try
            {
                conn = Database.getConnection();

                String qry = "";
                String forgotDate ="";


                    qry = "select fdtstamp from agents where login= ? and accid=?";

                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, login);
                pstmt.setLong(2, user.getAccountId());
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    forgotDate = rs.getString("fdtstamp");
                }
                long diffHours = 1;
                Functions functions = new Functions();
                if (functions.isValueNull(forgotDate))
                {

                 try
                 {
                     forgotDate = Functions.convertDtstampToDBFormat(forgotDate);
                     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                     String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                     Date d1 = sdf.parse(forgotDate);
                     Date d2 = sdf.parse(currentDate);
                     long diff = d2.getTime() - d1.getTime();
                     diffHours = diff / (60 * 60 * 1000);
                 }catch(Exception E){
                     E.getMessage();
                 }
                }

                if (diffHours >= 1)
                {

                    String query = "update agents set  fpasswd=?,fdtstamp=unix_timestamp(now()) where accid=? and login=?";

                    pstmt1 = conn.prepareStatement(query);
                    pstmt1.setString(1, hashPassword);
                    pstmt1.setLong(2, user.getAccountId());
                    pstmt1.setString(3, login);
                    int success = pstmt1.executeUpdate();
                    if (success > 0)
                    {
                    /*ResultSet rs = null;*/
                        logger.debug("new temporary password has been set");
                        // send mail to member warning to use this password within 24 hr.
                        String query1 = "select contact_emails,agentId,partnerId from agents where login=? and accid=?";

                        pstmt1 = conn.prepareStatement(query1);
                        pstmt1.setString(1, login);
                        pstmt1.setString(2, accountid);
                        rs1 = pstmt1.executeQuery();
                        if (rs1.next())
                        {
                            String emailAddr = rs1.getString("contact_emails");
                            String agentId = rs1.getString("agentId");
                            String partnerId = rs1.getString("partnerId");
                            logger.debug("agentId======" + agentId);
                            logger.error(fpasswd + "--for--" + login);
                            // MailService mailService=new MailService();
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            HashMap mailValues = new HashMap();
                            mailValues.put(MailPlaceHolder.TOID, agentId);
                            mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                            mailValues.put(MailPlaceHolder.PARTNERID, partnerId);
                            //mailService.sendMail(MailEventEnum.AGENT_FORGOT_PASSWORD,mailValues);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.AGENT_FORGOT_PASSWORD, mailValues);

                            flag = true;
                            String Value= "Temporary Password=" + fpasswd;
                            activityTrackerVOs.setInterface(ActivityLogParameters.AGENT.toString());
                            activityTrackerVOs.setUser_name(login + "-" + agentId);
                            activityTrackerVOs.setRole("Agent");
                            activityTrackerVOs.setAction(ActivityLogParameters.FGTPASS.toString());
                            activityTrackerVOs.setModule_name(ActivityLogParameters.FGTPASS.toString());
                            activityTrackerVOs.setLable_values(Value);
                            activityTrackerVOs.setDescription(ActivityLogParameters.AGENTID.toString() + "-" + agentId);
                            activityTrackerVOs.setIp(remoteAddr);
                            activityTrackerVOs.setHeader(Header);
                            try
                            {
                                AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                                asyncActivityTracker.asyncActivity(activityTrackerVOs);
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception while AsyncActivityLog::::", e);
                            }
                        }
                    }
                    else
                    {
                        throw new SystemError("Error : Exception occurred while updating member table.");
                    }
                    logger.debug("query has been committed for forgotpassword  ");
                }else{
                    flag = false;
                    System.out.println("inside else forgot password less than 1 hour");
                    logger.debug("inside else forgot password less than 1 hour");
                }
            }//end try
            catch (SQLException e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeResultSet(rs1);
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        return flag;

    }
    public Hashtable listTransactionsNew(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String merchantid) throws SystemError
    {
        logger.debug("Entering listTransactions for agent");
        Connection cn=Database.getRDBConnection();
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }

        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc=ESAPI.encoder().encodeForSQL(me,desc);
        trackingid=ESAPI.encoder().encodeForSQL(me,trackingid);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        ResultSet rs = null;
        try
        {   Functions functions=new Functions();
            Iterator i = gatewayTypeSet.iterator();
            while(i.hasNext())
            {
                //tablename = "transaction_icicicredit";
                tablename = Database.getTableName((String)i.next());

                if (archive)
                {
                    //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                    tablename = "transaction_icicicredit_archive";
                }
                if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    fields = "icicitransid as transid,toid,status,name,amount,captureamount,refundamount,description,dtstamp,paymodeid,cardtype,cardtypeid,accountid";
                }
                else
                {
                    fields = "trackingid as transid,toid,status,name,amount,captureamount,refundamount,description,dtstamp,paymodeid,cardtype,cardtypeid,accountid,currency,remark";
                }

                query.append("select " + fields + " from " + tablename + " where");
                query.append(" toid IN(" + merchantid + ")");
                if (functions.isValueNull(status))
                    query.append(" and status='" + status + "'");
                if (functions.isValueNull(fdtstamp))
                    query.append(" and dtstamp >= " + fdtstamp);
                if (functions.isValueNull(tdtstamp))
                    query.append(" and dtstamp <= " + tdtstamp);
                if (functions.isValueNull(desc))
                    query.append(" and description like '%" + desc + "%'");
                if (functions.isValueNull(accountid))
                    query.append(" and accountid =" + accountid);

                if (functions.isValueNull(trackingid))
                {
                    if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and icicitransid=" + trackingid);
                    }
                    else
                    {
                        query.append(" and trackingid=" + trackingid);
                    }
                }
                if(i.hasNext())
                    query.append(" UNION ");

            }
            logger.debug("Agent Query---"+query);

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            query.append(" order by transid DESC");

            query.append(" limit " + start + "," + end);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));

            rs = Database.executeQuery(countquery.toString(), cn);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        logger.debug("Leaving listTransactions");
        return hash;
    }

    public Hashtable listTransactionsCPNew(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String merchantid) throws SystemError
    {
        logger.debug("Entering listTransactions for agent");
        Connection cn=Database.getRDBConnection();
        if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }

        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        StringBuffer query = new StringBuffer();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //status = ESAPI.encoder().encodeForSQL(me,status);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        desc=ESAPI.encoder().encodeForSQL(me,desc);
        trackingid=ESAPI.encoder().encodeForSQL(me,trackingid);

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        ResultSet rs = null;
        try
        {   Functions functions=new Functions();

                tablename = "transaction_card_present";
                //tablename = Database.getTableName((String)i.next());

                if (archive)
                {
                    //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                    tablename = "transaction_icicicredit_archive";
                }
                if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                {
                    fields = "icicitransid as transid,toid,status,name,amount,captureamount,refundamount,description,dtstamp,paymodeid,cardtype,cardtypeid,accountid";
                }
                else
                {
                    fields = "trackingid as transid,toid,status,name,amount,captureamount,refundamount,description,dtstamp,paymodeid,cardtype,cardtypeid,accountid,currency,remark,transactionTime";
                }

                query.append("select " + fields + " from " + tablename + " where");
                query.append(" toid IN(" + merchantid + ")");
                if (functions.isValueNull(status))
                    query.append(" and status='" + status + "'");

               if ( functions.isValueNull(fdtstamp))
               {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and transactionTime >= '" + startDate + "'");
               }
               if (functions.isValueNull(tdtstamp))
               {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and transactionTime <= '" + endDate + "'");
                }
                if (functions.isValueNull(desc))
                    query.append(" and description like '%" + desc + "%'");
                if (functions.isValueNull(accountid))
                    query.append(" and accountid =" + accountid);

                if (functions.isValueNull(trackingid))
                {
                    if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {
                        query.append(" and icicitransid=" + trackingid);
                    }
                    else
                    {
                        query.append(" and trackingid=" + trackingid);
                    }
                }

            logger.debug("Agent Query---"+query);

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            query.append(" order by transid DESC");

            query.append(" limit " + start + "," + end);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));

            rs = Database.executeQuery(countquery.toString(), cn);

            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {   logger.error("SQL Exception leaving listTransactions" ,se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        logger.debug("Leaving listTransactions");
        return hash;
    }

    public Hashtable<String, String> getAgentMemberDetailList(String agentid)
    {
        Hashtable<String, String> memberid = new Hashtable<String, String>();
        Connection con=null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            StringBuffer qry= new StringBuffer("SELECT m.memberid,m.company_name FROM members AS m inner join merchant_agent_mapping AS mam on m.memberid=mam.memberid and mam.agentId=? ORDER BY m.memberid");
            pstmt= con.prepareStatement(qry.toString());
            pstmt.setString(1,agentid);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                memberid.put(rs.getString("memberid"),rs.getString("company_name"));
            }
        }
        catch(Exception e)
        {
            logger.error("error",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return memberid;
    }

    public String getAgentMemberRS(String agentid)
    {
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        Connection con=null;
        String sMemberList="";
        try
        {
            con=Database.getConnection();
            String qry="select memberid from members where agentId=?";
            pstmt= con.prepareStatement(qry);
            pstmt.setString(1,agentid);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                sMemberList = sMemberList + rs.getString("memberid") + ",";
            }
            if (!sMemberList.equalsIgnoreCase(""))
            {
                sMemberList = sMemberList.substring(0,sMemberList.length()-1);
            }
        }
        catch(Exception e)
        {
            logger.error("error",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return sMemberList;
    }

    public String getAgentMemberList(String agentid)
    {
        ResultSet rs=null;
        Connection con=null;
        PreparedStatement pstmt=null;
        StringBuffer sMemberList = new StringBuffer();
        String memberList="";
        try
        {
            //con=Database.getConnection();
            con=Database.getRDBConnection();
            StringBuffer qry= new StringBuffer("select memberid from merchant_agent_mapping where agentId=?");
            pstmt= con.prepareStatement(qry.toString());
            pstmt.setString(1,agentid);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                sMemberList.append(rs.getString("memberid")+",");
            }
            if(sMemberList.length() > 0)
            {
                memberList = sMemberList.toString().substring(0, sMemberList.length() - 1);
            }
        }
        catch(Exception e)
        {
            logger.error("error",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return memberList;
    }

    public Hashtable loadAgentMailValue(String memberid)throws SystemError
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ResultSet rs1 = null;
        String agentid="0";
        Hashtable mailvalue=new Hashtable();
        if(memberid!=null)
        {
            try
            {
                con=Database.getConnection();
                String qry="SELECT agentId FROM members WHERE memberid=?";
                pstmt=con.prepareStatement(qry);
                pstmt.setString(1,memberid);
                rs= pstmt.executeQuery();
                if(rs.next())
                {
                    agentid=rs.getString("agentId");
                }

                String qry1="SELECT companysupportmailid,company_name,companyadminid,supporturl,siteurl,contact_emails,address,telno,salesemail,billingemail,companyfromemail,supportfromemail,notifyemail,smtp_password,smtp_user,smtp_port,smtp_host,superadminid FROM agents WHERE agentId=?";
                pstmt=con.prepareStatement(qry1);
                pstmt.setString(1,agentid);
                rs1=pstmt.executeQuery();
                if(rs1.next())
                {
                    mailvalue.put("COMPANY_ADMIN_EMAIL",rs1.getString("companyadminid"));
                    mailvalue.put("COMPANY_SUPPORT_URL",rs1.getString("supporturl"));
                    mailvalue.put("COMPANY_LIVE_URL",rs1.getString("siteurl"));
                    mailvalue.put("contact_emails",rs1.getString("contact_emails"));
                    mailvalue.put("address",rs1.getString("address"));
                    mailvalue.put("telno",rs1.getString("telno"));
                    mailvalue.put("COMPANY",rs1.getString("company_name"));
                    mailvalue.put("COMPANY_SALES_EMAIL",rs1.getString("salesemail"));
                    mailvalue.put("COMPANY_BILLING_EMAIL",rs1.getString("billingemail"));
                    mailvalue.put("COMPANY_FROM_ADDRESS",rs1.getString("companyfromemail"));
                    mailvalue.put("SUPPORT_FROM_ADDRESS",rs1.getString("supportfromemail"));
                    mailvalue.put("COMPANY_NOTIFY_EMAIL",rs1.getString("notifyemail"));

                    mailvalue.put("SuperAdminID",rs1.getString("superadminid"));
                    mailvalue.put("SMTP_HOST",rs1.getString("smtp_host"));
                    mailvalue.put("SMTP_PORT",rs1.getString("smtp_port"));
                    mailvalue.put("SMTP_AUTH_USER",rs1.getString("smtp_user"));
                    mailvalue.put("SMTP_AUTH_PWD",rs1.getString("smtp_password"));
                }
                rs1=null;
            }
            catch (SQLException e)
            {
                logger.error("SQLException",e);
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeResultSet(rs);
                Database.closeResultSet(rs1);
                Database.closeConnection(con);
            }
        }
        else
        {
            logger.error("Memberid is not provided");
        }
        return mailvalue;
    }

    public List<String> perCurrency(String agentName) throws SystemError
    {
        List<String> currencyList = new ArrayList<String>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            StringBuffer query = new StringBuffer();
            con = Database.getRDBConnection();

            String singleQuery = "SELECT currency AS curr FROM transaction_qwipi WHERE toid IN (SELECT memberid FROM merchant_agent_mapping WHERE agentid=?) GROUP BY currency\n" +
                    "UNION\n" +
                    "SELECT currency AS curr FROM transaction_common WHERE toid IN (SELECT memberid FROM merchant_agent_mapping WHERE agentid=?) GROUP BY currency\n" +
                    "UNION\n" +
                    "SELECT currency AS curr FROM transaction_ecore WHERE toid IN (SELECT memberid FROM merchant_agent_mapping WHERE agentid=?) GROUP BY currency";

            pstm = con.prepareStatement(singleQuery);
            pstm.setString(1, agentName);
            pstm.setString(2, agentName);
            pstm.setString(3, agentName);
            rs = pstm.executeQuery();
            while (rs.next())
            {
                currencyList.add(rs.getString("curr"));
            }

        }
        catch (SQLException e)
        {
            logger.debug("Error while connecting to database--"+e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstm);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }

        return currencyList;

    }

    public boolean isValueNull(String str)
    {
        if(str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }

    public String getRoleofPartner(String partnerid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_roll="";
        try
        {

            con = Database.getRDBConnection();
            String qry = "select roles from user u , partners p where u.login=p.login and p.partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                v_roll = v_roll+","+rs.getString("roles");
            }
        }
        catch (Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return v_roll;
    }


}