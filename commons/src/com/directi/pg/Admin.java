package com.directi.pg;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationCredentialsException;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 12, 2012
 * Time: 9:07:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Admin
{
    public static final String ROLE="admin";
    public static Hashtable hash = null;
    static Logger logger = new Logger(Admin.class.getName());

    public static void refresh() throws SystemError
    {
        Hashtable hash1 = new Hashtable();
        hash1.put("5GBGW52HW9", "Visa/Mastercard");

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("Entering Refresh");
            String query = "select * from admin";
            ResultSet result = Database.executeQuery(query,conn);
            while (result.next())
            {
                hash1.put(result.getString("adminid"), result.getString("login"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        //End of inserts.
        hash = hash1;
        logger.debug("Leaving refresh");
    }
    public static boolean isAdmin(String login) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("check isMember method");
            String selquery = "select adminid from admin where login = ? ";
            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            ResultSet rs = pstmt.executeQuery();
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
            Database.closeConnection(conn);
        }
        return false;
    }

    public static boolean isLoggedIn(HttpSession sess)
    {
        if (sess.getAttribute("merchantid") == null || sess.getAttribute("password") == null || sess.getAttribute("username") == null)
            return false;
        else
            return true;
    }

     public static String generateKey(int size)
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
    public static String generateKey()
    {
        return generateKey(32);
    }
     /*public static Member Admin(String login, String password) throws SystemError
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
        Member mem = new Member();

        logger.debug("check username and password in database");
        String query = "select * from admin where login = ? and (passwd = ? or (fpasswd= ? and (fdtstamp +3600) > unix_timestamp(now())))";

        mem.authenticate = "false";
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

                mem.memberid = result.getInt("adminid");

                logger.debug(passwd);
                logger.debug(hashPassword);
                if (passwd.equals(hashPassword))
                {
                    mem.authenticate = "true";

                }

                if (fpasswd.equals(hashPassword))
                {
                    mem.authenticate = "forgot";

                }

             }
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
        return mem;
    }*/

    public static Member Admin(String login,User user) throws SystemError
    {    //Connection conn = null;
        String fpasswd = null;
        String passwd =null;

        String hashPassword = null ;
        /* try
        {
            hashPassword = ESAPI.authenticator().hashPassword(password,login) ;
        }
        catch(Exception e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }*/
        Member mem = new Member();

        logger.debug("check username and password in database");
        /* and (passwd = ? or (fpasswd= ? and (fdtstamp +3600) > unix_timestamp(now())))*/
        String query = "select * from admin where login = ? and accid=?";

        mem.authenticate = "false";
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1,login);
            /*pstmt.setString(2,hashPassword);
            pstmt.setString(3,hashPassword);*/
            pstmt.setLong(2,user.getAccountId());
            ResultSet result = pstmt.executeQuery();


            if (result.next())
            {
                fpasswd=result.getString("fpasswd");
                /*passwd = result.getString("passwd");*/

                mem.memberid = result.getInt("adminid");

                /* logger.debug(passwd);*/
                /*logger.debug(hashPassword);*/
                /* if (passwd.equals(hashPassword))
                {
                    mem.authenticate = "true";

                }*/

                if (!"".equals(fpasswd))
                {
                    mem.authenticate = "forgot";

                }

            }
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
        return mem;
    }

    /*public static boolean AdminchangePassword(String oldpwd, String newpwd, String memberid, User user)
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
                    String query2 = "select login from admin where adminid = ?";
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
                        logger.error("System error occure::::",se);
                        return false;
                    }
                    catch (Exception e)
                    {
                        logger.error("Getting Excrption in changepassword method ::::", e);
                        return false;
                    }
                    finally
                    {
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
                logger.error("Getting Excrption in changepassword method while doing hashing::::", e);
            }


        String updQuery = "update admin set passwd = ?, fpasswd=''  where adminid = ? and (passwd = ? or (fpasswd=? and (fdtstamp +3600) > unix_timestamp(now())))";


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
            logger.error("Getting Excrption in changepassword method ::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return true;

    }*/

    public static boolean AdminchangePassword(String oldpwd, String newpwd, String memberid, User user) throws AuthenticationCredentialsException
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
            String query2 = "select login from `user` where  accountid= ? and roles=?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setLong(1,user.getAccountId());
            pstmt2.setString(2,ROLE);
            logger.debug("admin login query---"+pstmt2);

            ResultSet rs = pstmt2.executeQuery();
            if (rs.next())
            {
                login = rs.getString("login");
            }
            logger.debug("login in Admin.java----"+login);
            logger.debug("roles in Admin.java----"+ROLE);

        }
        catch (SystemError se)
        {
            logger.error("System error occure::::",se);
            return false;
        }
        catch (Exception e)
        {
            logger.error("Getting Excrption in changepassword method ::::", e);
            return false;
        }
        finally
        {
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
        /* try
        {
            hashNewPassword = ESAPI.authenticator().hashPassword(newpwd,login) ;
            hashOldPassword = ESAPI.authenticator().hashPassword(oldpwd,login) ;
        }
        catch(Exception e)
        {
            logger.error("Getting Excrption in changepassword method while doing hashing::::", e);
        }*/

        /* and (passwd = ? or (fpasswd=? and (fdtstamp +3600) > unix_timestamp(now()))*/
        String updQuery = "update admin set fpasswd=''  where adminid = ? and accid=? and login=?";


        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(updQuery);
            /*pstmt.setString(1,hashNewPassword);*/
            pstmt.setString(1,memberid );
            /*pstmt.setString(3,hashOldPassword);*/
            /*pstmt.setString(4,hashOldPassword);*/
            pstmt.setLong(2,user.getAccountId());
            pstmt.setString(3,login);
            i = pstmt.executeUpdate();
            logger.debug("update records from change password");

        }
        catch (SystemError se)
        {
            logger.error("System error occure::::",se);
        }
        catch (Exception e)
        {
            logger.error("Getting Excrption in changepassword method ::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return true;

    }

   /* public static boolean AdminforgotPassword(String login,User user) throws SystemError
    {
        Transaction transaction = new Transaction();
        boolean flag = false;
        if (isAdmin(login))
        {

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            String hashPassword = null ;
            String oldPasshash = null;
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
                    String query2 = "select passwd from admin where login = ?";
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
                        logger.error("System error occure::::",se);
                        return false;
                    }
                    catch (Exception e)
                    {
                        logger.error("Getting Excrption in changepassword method ::::", e);
                        return false;
                    }
                    finally
                    {
                        Database.closeConnection(conn);
                    }

            //Updated the temporary password in user table
             try
             {
               ESAPI.authenticator().changePassword(user,oldPasshash,hashPassword,hashPassword);
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

                String query = "update admin set passwd= ? , fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ?";

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


                    String query1 = "select contact_emails from admin where login= ?";

                    pstmt= conn.prepareStatement(query1);
                    pstmt.setString(1,login);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String emailAddr = rs.getString("contact_emails");
                        flag = transaction.sendForgotPasswordMail(emailAddr, login, fpasswd);
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

    public static boolean AdminforgotPassword(String login,User user) throws SystemError
    {
        boolean flag = false;
        Transaction transaction= new Transaction();
        if (isAdmin(login))
        {

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            String hashPassword = null ;
            String oldPasshash = null;
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
                /*String query2 = "select passwd from admin where login = ?";*/
                String query2 = "select hashedpasswd,accountid from `user` where login = ? and roles=? and accountid=?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1,login);
                pstmt2.setString(2,ROLE);
                pstmt2.setLong(3,user.getAccountId());

                ResultSet rs = pstmt2.executeQuery();
                if (rs.next())
                {
                    oldPasshash = rs.getString(1);
                }

            }
            catch (SystemError se)
            {
                logger.error("System error occure::::",se);
                return false;
            }
            catch (Exception e)
            {
                logger.error("Getting Excrption in changepassword method ::::", e);
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

                String query = "update admin set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and accid=? ";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,hashPassword);
                pstmt.setString(2,login);
                pstmt.setLong(3,user.getAccountId());
                int success = pstmt.executeUpdate();
                if (success > 0)
                {
                    ResultSet rs = null;
                    logger.debug("new temporary password has been set");



                    // send mail to member warning to use this password within 24 hr.


                    String query1 = "select contact_emails from admin where login= ? and accid=?";

                    pstmt= conn.prepareStatement(query1);
                    pstmt.setString(1,login);
                    pstmt.setLong(2,user.getAccountId());
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String emailAddr = rs.getString("contact_emails");
                        flag = transaction.sendForgotPasswordMail(emailAddr, login, fpasswd);
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

    }

    public static String getAdminEmailID()
    {
        Connection con=null;
        String adminEmail=null;
        try
        {
            con=Database.getConnection();
            String query="SELECT * FROM admin ORDER BY adminid LIMIT 1";
            PreparedStatement pstmt=con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next())
            {
                adminEmail=rs.getString("contact_emails");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error occure while fetch record from Admin",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception occure while fetch record from Admin",e);
        }
        finally {
            Database.closeConnection(con);
        }
        return adminEmail;
    }

    public static String getAdminFromEmailID()
    {
        String fromaddress=null;
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String query="SELECT fromemail,mailcount FROM adminfromemail WHERE mailcount=( SELECT MIN(mailcount) FROM `adminfromemail`) LIMIT 1";
            PreparedStatement pstmt=con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next())
            {
                fromaddress=rs.getString("fromemail");
            }

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError---->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SQLException e)
        {
            logger.error("SQLException---->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }
        return fromaddress;
    }
}
    

