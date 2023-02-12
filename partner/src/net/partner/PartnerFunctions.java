package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.manager.dao.PartnerDAO;
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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/7/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerFunctions
{
    public static final String ROLE = "partner";
    public static final String SUBPARTNER = "subpartner";
    public static final String CHILDSUBPARTNER = "childsuperpartner";
    public static final String SUPERPARTNER = "superpartner";
    static Logger logger = new Logger(Partner.class.getName());
    private Functions functions = new Functions();
    PartnerDAO partnerDAO= new PartnerDAO();

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    public static int convertStringtoInt(String convertstr, int defaultval)
    {
        int val = defaultval;

        if (convertstr != null)
        {
            convertstr = convertstr.trim();

            if (!convertstr.equals("") && !convertstr.equals("null"))
            {
                try
                {
                    val = Integer.parseInt(convertstr);
                }
                catch (NumberFormatException nfe)
                {
                    val = defaultval;
                }
            }

        }
        return val;
    }

    public static String comboval(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=N>N</option>";
        }
        else
        {
            str = str + "<option value=Y>Y</option>";
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
        }


        return str;

    }


    public static String comboval2(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Inactive</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Active</option>";
        }
        else
        {
            str = str + "<option value=Y>Active</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Inactive</option>";
        }
        return str;
    }

    public static String comboval14(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Not Required</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Required</option>";
        }
        else
        {
            str = str + "<option value=Y>Required</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Not Required</option>";
        }
        return str;
    }

    public static String comboval4(String check)
    {
        String str = "";
        if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>No</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Yes</option>";
        }
        else
        {
            str = str + "<option value=Y>Yes</option>";
            str = str + "<option value=" + check + " selected=\"selected\">No</option>";
        }
        return str;
    }


    public static String comboval1(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">" + check + "</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
        }
        else if (check != null && check.equals("terminal_Level"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
        }
        else if (check != null && check.equals("account_Level"))
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Account Level</option>";
            str = str + "<option value=account_member_Level>Account Member Level</option>";
        }
        else
        {
            str = str + "<option value=N>N</option>";
            str = str + "<option value=terminal_Level>Terminal Level</option>";
            str = str + "<option value=account_Level>Account Level</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Account Member Level</option>";
        }
        return str;
    }

    public static String comboval3(String check)
    {
        String str = "";
        if (check != null && check.equals("N"))
        {
            str = str + "<option value=" + check + " selected=\"selected\">Disable</option>";
            str = str + "<option value=Y>Enable</option>";
            str = str + "<option value=V>VIP Cards</option>";
        }
        else if (check != null && check.equals("Y"))
        {
            str = str + "<option value=N>Disable</option>";
            str = str + "<option value=" + check + " selected=\"selected\">Enable</option>";
            str = str + "<option value=V>VIP Cards</option>";
        }
        else
        {
            str = str + "<option value=N>Disable</option>";
            str = str + "<option value=Y>Enable</option>";
            str = str + "<option value=" + check + " selected=\"selected\">VIP Cards</option>";
        }


        return str;

    }

    public static String checkStringNull(String checkstr)
    {
        if (checkstr != null)
        {
            checkstr = checkstr.trim();

            if (checkstr.equals("null"))
            {
                checkstr = "";
            }

            if (checkstr.equals(""))
            {
                checkstr = null;
            }

        }
        return checkstr;
    }


    /*public PartnerAuthenticate partnerLoginAuthentication(String login, String password) throws SystemError
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
        PartnerAuthenticate partnerauthenticate = new PartnerAuthenticate();

        logger.debug("check username and password in database");
        String query = "select * from partners where login = ? and (passwd = ? or (fpasswd= ? and (fdtstamp +3600) > unix_timestamp(now())))";

        partnerauthenticate.authenticate = "false";
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

                partnerauthenticate.partnerid = result.getInt("partnerId");
                partnerauthenticate.activation = result.getString("activation");
                partnerauthenticate.contactemails = result.getString("contact_emails");
                partnerauthenticate.address=result.getString("address");
                partnerauthenticate.partnername=result.getString("partnerName");
                partnerauthenticate.template=result.getString("template");
                partnerauthenticate.telno=result.getString("telno");
                partnerauthenticate.logoname=result.getString("logoName");
                if (passwd.equals(hashPassword))
                {
                    partnerauthenticate.authenticate = "true";

                }

                if (fpasswd.equals(hashPassword))
                {
                    partnerauthenticate.authenticate = "forgot";

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
        return partnerauthenticate;
    }*/

    public static String converttomillisec(String tempmm, String tempdd, String tempyy)
    {
        return converttomillisec(tempmm, tempdd, tempyy, "0", "0", "0");
    }

    /*public boolean partnerchangePassword(String oldpwd, String newpwd, String memberid, User user)
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
            String query2 = "select login from partners where partnerId = ?";
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


        String updQuery = "update partners set passwd = ?, fpasswd=''  where partnerId = ? and (passwd = ? or (fpasswd=? and (fdtstamp +3600) > unix_timestamp(now())))";


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

    public static String convertmd5Test(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String md5 = null;

        if (null == value) return null;

        try
        {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(value.getBytes(), 0, value.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        }
        catch (NoSuchAlgorithmException e)
        {

            logger.error("Exception---" + e);
        }

        return md5;
    }

    /*public boolean partnerForgotPassword(String login,User user) throws SystemError
    {
        boolean flag = false;
        if (isPartner(login))
        {

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            String hashPassword = null ;
            String oldPasshash = null;
            String memberid=null;
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
                String query2 = "select passwd,partnerId from partners where login = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1,login);

                ResultSet rs = pstmt2.executeQuery();
                if (rs.next())
                {
                    oldPasshash = rs.getString("passwd");
                    memberid = rs.getString("partnerId");
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

                String query = "update partners set passwd= ? , fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ?";

                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,hashPassword);
                pstmt.setString(2,hashPassword);
                pstmt.setString(3,login);
                int success = pstmt.executeUpdate();
                if (success > 0)
                {
                    ResultSet rs = null;
                    logger.debug("new temporary password has been set");

                    MailService mailService=new MailService();
                    HashMap mailValues=new HashMap();
                    mailValues.put(MailPlaceHolder.TOID,memberid);
                    mailValues.put(MailPlaceHolder.PASSWORD,fpasswd);
                    mailService.sendMail(MailEventEnum.PARTNER_FORGOT_PASSWORD,mailValues);
                    flag=true;
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

    public static String converttomillisec(String tempmm, String tempdd, String tempyy, String temph, String tempm, String temps)
    {
        long tempdt = 0;
        String strdt = null;

        if (tempmm != null && tempdd != null && tempyy != null)
        {
            tempmm = tempmm.trim();
            tempdd = tempdd.trim();
            tempyy = tempyy.trim();

            if (tempmm.equals("") || tempdd.equals("") || tempyy.equals(""))
            {
                tempdt = 0;
                //		out.println("mm "+tempmm +" : " +"dd "+dd +" : " +"yy "+yy +"<br>" );
            }
            else
            {
                try
                {
                    //Date dt =new Date(Integer.parseInt(tempyy),Integer.parseInt(tempmm),Integer.parseInt(tempdd));
                    //tempdt=dt.getTime();

                    Calendar cal = Calendar.getInstance();
                    cal.set(Integer.parseInt(tempyy), Integer.parseInt(tempmm), Integer.parseInt(tempdd), Integer.parseInt(temph), Integer.parseInt(tempm), Integer.parseInt(temps));
                    tempdt = cal.getTime().getTime();

                    tempdt = tempdt / 1000;

                    strdt = "" + tempdt;
                }
                catch (NumberFormatException nfe)
                {
                    strdt = null;
                }
            }

        }

        return strdt;

    }

    public boolean isPartner(String login) throws SystemError
    {
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String role = partnerFunctions.getRole(login);
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "select accountid from `user` where login= ? and roles=?";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            pstmt.setString(2, role);
            logger.debug("forgot pstmt:::::"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMember method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean isPartnerUser(String login) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            // conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "select userid from partner_users where login = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }
    //new

    public boolean isLoggedInPartner(HttpSession sess)
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

    // new

    public PartnerAuthenticate partnerLoginAuthentication(String login, HttpServletRequest request) throws SystemError
    {
        String fpasswd = null;
        String passwd = null;
        PaymentChecker paymentChecker = new PaymentChecker();
        String ipAddress = Functions.getIpAddress(request);
        Connection conn = null;
        String partnerUserLogin = "";
        int partnerUserId = 0;
        PartnerAuthenticate partnerauthenticate = new PartnerAuthenticate();

        logger.debug("check username and password in database");
        PreparedStatement pstmt1 = null, pstmt = null;
        ResultSet rs = null, result = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String partnerUserQuery = "SELECT p.login,pu.userid FROM partners AS p,partner_users AS pu WHERE p.partnerId=pu.partnerid AND pu.login=?";
            pstmt1 = conn.prepareStatement(partnerUserQuery);
            pstmt1.setString(1, login);
            rs = pstmt1.executeQuery();
            if (rs.next())
            {
                partnerUserLogin = rs.getString("login");
                partnerUserId = rs.getInt("userid");
                if (partnerUserLogin != null && !partnerUserLogin.equals(""))
                {
                    login = partnerUserLogin;
                }
                logger.debug("user id from member_user---" + partnerUserId + "--" + partnerUserLogin + "--" + login);
                PartnerUser partnerUser = new PartnerUser();
                partnerUser.setPartnerUserId(partnerUserId);
                partnerauthenticate.setPartnerUser(partnerUser);
            }
            String query = "select * from partners where login=? ";

            partnerauthenticate.authenticate = "false";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, login);
            result = pstmt.executeQuery();
            if (result.next())
            {
                fpasswd = result.getString("fpasswd");
                partnerauthenticate.partnerid = result.getInt("partnerId");
                partnerauthenticate.activation = result.getString("activation");
                partnerauthenticate.contactemails = result.getString("contact_emails");
                partnerauthenticate.address = result.getString("address");
                partnerauthenticate.partnername = result.getString("partnerName");
                partnerauthenticate.template = result.getString("template");
                partnerauthenticate.telno = result.getString("telno");
                partnerauthenticate.logoname = result.getString("logoName");
                partnerauthenticate.isFlightPartner = ("Y".equals(result.getString("isFlightPartner")) ? true : false);
                partnerauthenticate.hostURL = (result.getString("hosturl"));
                partnerauthenticate.isRefund = (result.getString("isRefund"));
                partnerauthenticate.emiConfiguration = (result.getString("emi_configuration"));

                if (!"".equals(fpasswd))
                {
                    partnerauthenticate.authenticate = "forgot";
                }
                if (!paymentChecker.isIpWhitelistedForPartner(result.getString("partnerId"), ipAddress))
                {
                    throw new SystemError("Error : Ip is NOT WHITELISTED");
                }
            }
            else
            {
                logger.debug("Partner details not found in PartnerLoginAuthentication()");
                throw new SystemError("Error: UNAUTHORIZED USER");
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(result);
            Database.closePreparedStatement(pstmt1);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return partnerauthenticate;
    }

    public boolean partnerchangePassword(String oldpwd, String newpwd, String login, User user) throws AuthenticationCredentialsException
    {
        logger.debug("Updating new password in Database");

        int i = 0;
        Connection conn = null;
        String hashNewPassword = null;
        String hashOldPassword = null;
        /*String role = "subpartner";*/
        String role = getUserRole(user);
        //Getting login name from member table
        PreparedStatement pstmt2 = null;
        ResultSet rs1 = null;
        try
        {
            conn = Database.getConnection();
            String query2 = "select login from `user` where accountid = ? and roles=?";
            pstmt2 = conn.prepareStatement(query2);
            pstmt2.setLong(1, user.getAccountId());
            pstmt2.setString(2, ROLE);

            rs1 = pstmt2.executeQuery();
            if (rs1.next())
            {
                login = rs1.getString("login");
            }

        }
        catch (SystemError se)
        {
            logger.error("System error occure::::", se);
            return false;
        }
        catch (Exception e)
        {
            logger.error("Getting Excrption in changepassword method ::::", e);
            return false;
        }
        finally
        {
            Database.closeResultSet(rs1);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(conn);
        }


        // Changing password in user table

        try
        {
            ESAPI.authenticator().changePassword(user, oldpwd, newpwd, newpwd);
        }
        catch (Exception e)
        {
            logger.error("Change password throwing Authetication Exception ", e);
            if (e.getMessage().toLowerCase().contains("mismatch"))
            {
                throw new AuthenticationCredentialsException("Password MISMATCH", "Authentication failed for password change on user: " + login);
            }
            return false;
        }

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            logger.debug("inside try");
            String query1 = "";
            String accountid = "";
            conn = Database.getConnection();
            if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
            {
                query1 = "select partnerid,contact_emails,userid from partner_users where login= ?";
            }
            else
            {
                query1 = "select partnerId,contact_emails from partners where login= ?";
            }
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();
            logger.debug("inside query--------" + pstmt);
            if (rs.next())
            {
                String emailAddr = rs.getString("contact_emails");
                String memberid = rs.getString("partnerId");
                logger.debug("memberid in mailService" + memberid);

                //MailService mailService = new MailService();
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                HashMap mailValues = new HashMap();
                if (SUBPARTNER.equalsIgnoreCase(role)|| CHILDSUBPARTNER.equals(role))
                {
                    mailValues.put(MailPlaceHolder.TOID, memberid);
                    mailValues.put(MailPlaceHolder.USERID, rs.getString("userid"));
                    mailValues.put(MailPlaceHolder.PARTNERID, memberid);
                    asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_USER_CHANGE_PASSWORD, mailValues);
                }
                else
                {
                    mailValues.put(MailPlaceHolder.TOID, memberid);
                    mailValues.put(MailPlaceHolder.PARTNERID, memberid);
                    asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_CHANGE_PASSWORD, mailValues);
                }

            }
        }
        catch (Exception e)
        {
            logger.debug("Exception::::" + e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
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

        /*and (passwd = ? or (fpasswd=? and (fdtstamp +3600) > unix_timestamp(now()))*/
        String updQuery = "update partners set fpasswd=''  where accid=?";
        PreparedStatement pstmt3 = null;

        try
        {
            conn = Database.getConnection();
            pstmt3 = conn.prepareStatement(updQuery);
            /*pstmt.setString(1,hashNewPassword);*/
            /*pstmt.setString(2,memberid );*//*
            pstmt.setString(3,hashOldPassword);
            pstmt.setString(4,hashOldPassword);*/
            pstmt3.setLong(1, user.getAccountId());
            i = pstmt3.executeUpdate();
            logger.debug("update records from change password");

        }
        catch (SystemError se)
        {
            logger.error("System error occure::::", se);
        }
        catch (Exception e)
        {
            logger.error("Getting Excrption in changepassword method ::::", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt3);
            Database.closeConnection(conn);
        }

        return true;

    }

    public boolean partnerForgotPassword(String login, User user,String remoteAddr,String Header,String actionExecuterId) throws SystemError
    {
        boolean flag = false;
        String role = "partner";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        if ((isPartner(login) || isPartnerUser(login)) && user != null)
        {

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            logger.debug("Temporary Pasword :::::::::"+fpasswd);
            String hashPassword = null;
            String oldPasshash = null, accountid = null;
            role = getUserRole(user);
            try
            {

                hashPassword = ESAPI.authenticator().hashPassword(fpasswd, login);
            }
            catch (Exception e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }

            Connection conn = null;
            PreparedStatement pstmt2 = null;
            ResultSet rs = null;
            //Getting login name from member table
            try
            {
                conn = Database.getConnection();
                /*String query2 = "select passwd from partners where login = ?";*/
                String query2 = "select hashedpasswd,accountid from `user` where login= ? and roles=? ";
                pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1, login);
                pstmt2.setString(2, role);
                logger.debug("partner---" + pstmt2);
                rs = pstmt2.executeQuery();
                if (rs.next())
                {
                    oldPasshash = rs.getString(1);
                    accountid = rs.getString(2);
                }

            }
            catch (SystemError se)
            {
                logger.error("System error occure::::", se);
                return false;
            }
            catch (Exception e)
            {
                logger.error("Getting Excrption in changepassword method ::::", e);
                return false;
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt2);
                Database.closeConnection(conn);
            }

            //Updated the temporary password in user table
            try
            {
                ((PzAuthenticator) ESAPI.authenticator()).forgetPassword(user, oldPasshash, hashPassword, hashPassword);
            }
            catch (Exception e)
            {
                logger.error("Change password throwing Authetication Exception ", e);
                return false;
            }

            logger.debug("user table updated with temporary password");

            PreparedStatement pstmt = null;
            try
            {
                conn = Database.getConnection();

                String qry = "";
                String forgotDate ="";
                if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
                {
                    qry = "select fdtstamp from partner_users where login= ? and accid=?";
                }
                else
                {
                    qry = "select fdtstamp from partners where login= ? and accid=?";
                }
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, login);
                pstmt.setString(2, accountid);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    forgotDate = rs.getString("fdtstamp");
                }
                long diffHours = 1;

                if (functions.isValueNull(forgotDate))
                {
                    forgotDate =  Functions.convertDtstampToDBFormat(forgotDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Date d1 = sdf.parse(forgotDate);
                    Date d2 = sdf.parse(currentDate);
                    long diff = d2.getTime() - d1.getTime();
                    diffHours = diff / (60  * 60 * 1000);

                }
                if (diffHours >= 1)
                {

                    String tableName = "partners";

                    if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
                    {
                        tableName = "partner_users";
                    }
                    String query = "update " + tableName + " set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and accid=?";

                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, hashPassword);
                /*pstmt.setString(2,hashPassword);*/
                    pstmt.setString(2, login);
                    pstmt.setString(3, accountid);
                    logger.error("update query----" + pstmt);
                    int success = pstmt.executeUpdate();
                    logger.error("Success::::"+success);
                    if (success > 0)
                    {
                    /*ResultSet rs = null;*/
                        logger.debug("new temporary password has been set");

                        // send mail to member warning to use this password within 24 hr.
                        String query1 = "";
                        if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
                        {
                            query1 = "select partnerid,contact_emails,userid from partner_users where login= ? and accid=?";
                        }
                        else
                        {
                            query1 = "select partnerId,contact_emails from partners where login= ? and accid=?";
                        }
                        pstmt = conn.prepareStatement(query1);
                        pstmt.setString(1, login);
                        pstmt.setString(2, accountid);
                        rs = pstmt.executeQuery();
                        if (rs.next())
                        {
                            String emailAddr = rs.getString("contact_emails");
                            String memberid = rs.getString("partnerId");

                            //MailService mailService=new MailService();
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            HashMap mailValues = new HashMap();
                            if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equalsIgnoreCase(role))
                            {
                                mailValues.put(MailPlaceHolder.TOID, memberid);
                                mailValues.put(MailPlaceHolder.USERID, rs.getString("userid"));
                                mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                                mailValues.put(MailPlaceHolder.PARTNERID, memberid);
                                asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_USER_FORGOT_PASSWORD, mailValues);
                            }
                            else
                            {
                                mailValues.put(MailPlaceHolder.TOID, memberid);
                                mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                                mailValues.put(MailPlaceHolder.PARTNERID, memberid);
                                asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_FORGOT_PASSWORD, mailValues);
                            }
                            flag            = true;
                            String Value    = "Temporary Password=" + fpasswd;
                            activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                            activityTrackerVOs.setUser_name(login + "-" + memberid);
                            activityTrackerVOs.setRole(role);
                            activityTrackerVOs.setAction(ActivityLogParameters.FGTPASS.toString());
                            activityTrackerVOs.setModule_name(ActivityLogParameters.FGTPASS.toString());
                            activityTrackerVOs.setLable_values(Value);
                            activityTrackerVOs.setDescription(ActivityLogParameters.PARTNERID.toString() + "-" + memberid);
                            activityTrackerVOs.setIp(remoteAddr);
                            activityTrackerVOs.setHeader(Header);
                            activityTrackerVOs.setPartnerId(memberid);
                            try
                            {
                                AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                                asyncActivityTracker.asyncActivity(activityTrackerVOs);
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception while AsyncActivityLog::::", e);
                            }
                            logger.debug("inside if forgot password greater than 1 hour");
                        }
                    }
                    else
                    {
                        throw new SystemError("Error : Exception occurred while updating member table.");
                    }
                }
                else
                {
                    flag = false;
                    logger.debug("inside else forgot password less than 1 hour");
                }
                logger.debug("query has been committed for forgotpassword ");
            }//end try
            catch (SQLException e)
            {
                logger.error("SQLException in PartnerFunction---", e);
                throw new SystemError("Error : " + e.getMessage());
            }
            catch (Exception e)
            {
                logger.error("Exception in PartnerFunction---", e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        return flag;

    }

    public int getTransactionsCountNew(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String transactionMode) throws SystemError
    {
        logger.debug("Entering getTransactionsCountNew for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (/*!Functions.isValidSQL(trackingid) || */!Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        //desc = ESAPI.encoder().encodeForSQL(me, desc);
        //trackingid = ESAPI.encoder().encodeForSQL(me, trackingid);
        String pRefund              = "false";
        //PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter                 = 1;

        int start                   = 0; // start index
        int end                     = 0; // end index
        start                       = (pageno - 1) * records;
        end                         = records;
        int totalrecords = 0;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename   = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "t.trackingid as transid,t.toid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.currency,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.cardtypeid,t.customerId,t.accountid,t.remark,t.emailAddr,t.terminalid,t.orderdescription,t.timestamp,t.paymentid,t.transaction_mode";
            //}
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd where t.trackingid=bd.icicitransid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" as t ");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            }
            query.append("where ");

            //query.append("where t.trackingid>0 ");
            //query.append(" and t.toid IN(" + merchantid + ")");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" t.timestamp >='" + startDate + "'");
            }
            else
            {
                query.append("  t.dtstamp >="+fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <='" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }
            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            if (functions.isValueNull(desc))
            {

                query.append(" AND t.description like '");
                query.append(desc);
                query.append("%'");
            }
            if (functions.isValueNull(status))
            {

                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= ?");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }*/
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid.toString()+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode=?");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount ");
            }

            StringBuilder countquery    = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");


            Date date5 = new Date();
            logger.error("listTransactionsNew pstmt1 starts ############" + date5.getTime());
            pstmt1  = cn.prepareStatement(countquery.toString());
            logger.error("countquery >>>>>>>>>" + countquery);
            if (functions.isValueNull(status))
            {
                pstmt1.setString(counter, status);
                counter++;
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds   = Long.parseLong(fdtstamp + "000");
                Calendar calendar   = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                pstmt1.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstmt1.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds   = Long.parseLong(tdtstamp + "000");
                Calendar calendar   = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstmt1.setString(counter, endDate);
                counter++;
            }

            else
            {
                pstmt1.setString(counter, tdtstamp);
                counter++;
            }*/
            if (functions.isValueNull(accountid))
            {
                pstmt1.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt1.setString(counter, issuingbank);
                counter++;
            }
            if (functions.isValueNull(paymentId))
            {
                pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt1.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt1.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt1.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt1.setString(counter, customerId);
                counter++;
            }
            if (functions.isValueNull(transactionMode))
            {
                pstmt1.setString(counter, transactionMode);
                counter++;
            }
            logger.error("listTransactionsNew pstmt1 >>>>>> " + pstmt1);
            rs      = pstmt1.executeQuery();

            logger.error("getTransactionsCountNew ends ############"+new Date().getTime());
            logger.error("getTransactionsCountNew diff ############"+(new Date().getTime()-date5.getTime()));

            if (rs.next())
                totalrecords = rs.getInt(1);
            logger.error("getTransactionsCountNew pstmt1:::::" + pstmt1+" "+totalrecords);
        }

        catch (SQLException se)
        {
            logger.error("SQL Exception leaving getTransactionsCountNew", se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        logger.debug("Leaving getTransactionsCountNew");
        return totalrecords;
    }

    public HashMap listTransactionsNew(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String transactionMode,int totalRecords) throws SystemError
    {
        logger.debug("Entering listTransactions for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (/*!Functions.isValidSQL(trackingid) || */!Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        HashMap hash        = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        //desc = ESAPI.encoder().encodeForSQL(me, desc);
        //trackingid = ESAPI.encoder().encodeForSQL(me, trackingid);
        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        //PreparedStatement pstmt1    = null;
        int counter                 = 1;

        int start                   = 0; // start index
        int end                     = 0; // end index
        start                       = (pageno - 1) * records;
        end                         = records;

        if(totalRecords > records)
        {
            end = totalRecords-start;
            if(end>records)
            {
                end = records;
            }
        }
        else
        {
            end = totalRecords;
        }

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename   = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "t.trackingid as transid,t.toid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.currency,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.cardtypeid,t.customerId,t.accountid,t.remark,t.emailAddr,t.terminalid,t.orderdescription,t.timestamp,t.paymentid,t.transaction_mode";
            //}
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd where t.trackingid=bd.icicitransid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" as t ");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            }
            //query.append("where t.trackingid>0 ");
            query.append("where ");
            //query.append(" and t.toid IN(" + merchantid + ")");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" t.timestamp >='" + startDate + "'");
            }
            else
            {
                query.append(" t.dtstamp >="+fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <='" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }

            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            /*if (functions.isValueNull(customerId))
            {
                {

                    query.append(" AND t.customerId IN(");
                    query.append(customerId);
                    query.append(")");

                }
            }*/
            if (functions.isValueNull(desc))
            {

                query.append(" AND t.description like '");
                query.append(desc);
                query.append("%'");
            }
            if (functions.isValueNull(status))
            {

                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= ?");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {

                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }*/
            /*if (functions.isValueNull(desc))
            {
                query.append(" and t.description= ? ");
            }*/
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid.toString()+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode=?");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount ");
            }

            StringBuilder countquery    = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            query.append(" order by transid DESC");
            query.append(" limit ? , ? ");

            Date date4 = new Date();
            logger.error("listTransactionsNew pstmt starts ############" + date4.getTime());
            pstmt   = cn.prepareStatement(query.toString());

            /*Date date5 = new Date();
            logger.error("listTransactionsNew pstmt1 starts ############" + date5.getTime());
            pstmt1  = cn.prepareStatement(countquery.toString());*/
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                //pstmt1.setString(counter, status);
                counter++;
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds   = Long.parseLong(fdtstamp + "000");
                Calendar calendar   = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                //pstmt1.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstmt.setString(counter, fdtstamp);
                //pstmt1.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds   = Long.parseLong(tdtstamp + "000");
                Calendar calendar   = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                //pstmt1.setString(counter, endDate);
                counter++;
            }
            else
            {
                pstmt.setString(counter, tdtstamp);
                //pstmt1.setString(counter, tdtstamp);
                counter++;
            }
            */
            /*if (functions.isValueNull(desc))
            {
                pstmt.setString(counter,desc);
                pstmt1.setString(counter, desc);
                counter++;
            }*/
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                //pstmt1.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                //pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                //pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt.setString(counter, issuingbank);
                //pstmt1.setString(counter, issuingbank);
                counter++;
            }
            /*if (functions.isValueNull(trackingid))
            {
                pstmt.setString(counter, trackingid);
                pstmt1.setString(counter, trackingid);
                counter++;
            }*/
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                //pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                //pstmt1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                //pstmt1.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt.setString(counter, emailAddr);
                //pstmt1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                //pstmt1.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, terminalid);
                //pstmt1.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                //pstmt1.setString(counter, customerId);
                counter++;
            }
            if (functions.isValueNull(transactionMode))
            {
                pstmt.setString(counter, transactionMode);
                //pstmt1.setString(counter, transactionMode);
                counter++;
            }
            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);
            logger.error("listTransactionsNew pstmt >>>>>>>> "+pstmt);
            //hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
            hash    = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());

            logger.error("listTransactionsNew ends ############"+new Date().getTime());
            logger.error("listTransactionsNew diff ############"+(new Date().getTime()-date4.getTime()));
            logger.error("listTransactionsNew pstmt:::::" + pstmt);

            /*rs      = pstmt1.executeQuery();

            logger.error("listTransactionsNew ends ############"+new Date().getTime());
            logger.error("listTransactionsNew diff ############"+(new Date().getTime()-date5.getTime()));
            logger.error("listTransactionsNew pstmt1:::::" + pstmt1);
            int totalrecords = 0;
            if (rs.next())
                totalrecords = rs.getInt(1);*/


            hash.put("totalrecords", "" + totalRecords);
            hash.put("records", "0");

            if (totalRecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));

            }

        }

        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
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
    public HashMap listCardPrsentTransactions(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName) throws SystemError
    {
        logger.debug("Entering listTransactions for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (/*!Functions.isValidSQL(trackingid) ||*/ !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        HashMap hash        = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        //desc = ESAPI.encoder().encodeForSQL(me, desc);
        //trackingid = ESAPI.encoder().encodeForSQL(me, trackingid);
        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter = 1;

        int start       = 0; // start index
        int end         = 0; // end index
        start           = (pageno - 1) * records;
        end             = records;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename = "transaction_card_present";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "t.trackingid as transid,t.toid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.currency,t.description,t.dtstamp," +
                    "t.transactionTime,t.paymodeid,t.cardtype,t.cardtypeid,t.customerId,t.accountid,t.remark,t.emailAddr,t.terminalid," +
                    "t.orderdescription,t.timestamp,t.paymentid ";
            //}
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd where t.trackingid=bd.icicitransid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            //query.append(" as t where t.trackingid>0 ");
            query.append(" as t where ");

            if (functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" t.transactionTime >= '" + startDate + "'");
            }else
            {
                query.append("  t.dtstamp >="+fdtstamp);
            }

            if (functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= '" + endDate + "'" );
            }else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }
/*
            query.append(" as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 ");
*/
            //query.append(" and t.toid IN(" + merchantid + ")");
            if (functions.isValueNull(partnerName))
            {
                query.append(" t.totype in("+partnerName+")");
            }
            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            /*if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }*/
            /*if (functions.isValueNull(customerId))
            {
                {

                    query.append(" AND t.customerId IN(");
                    query.append(customerId);
                    query.append(")");

                }
            }*/
            if (functions.isValueNull(desc))
            {
                {

                    query.append(" AND t.description like '");
                    query.append(desc);
                    query.append("%'");

                }
            }
            if (functions.isValueNull(status))
            {

                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            /*if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }*/
            /*if (functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" and t.transactionTime >= ?");
            }

            if (functions.isValueNull(tdtstamp))
            {

                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= ? ");
            }*/

            /*if (functions.isValueNull(desc))
            {
                query.append(" and t.description= ? ");
            }*/
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid.toString()+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount");
            }
            StringBuilder countquery = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            query.append(" order by transid DESC");
            query.append(" limit ? , ? ");
            pstmt   = cn.prepareStatement(query.toString());
            pstmt1  = cn.prepareStatement(countquery.toString());
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                pstmt1.setString(counter, status);
                counter++;
            }
            /*if (functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                pstmt1.setString(counter, startDate);
                counter++;
            }

            if ( functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                pstmt1.setString(counter, endDate);
                counter++;
            }*/

            /*if (functions.isValueNull(desc))
            {
                pstmt.setString(counter,desc);
                pstmt1.setString(counter, desc);
                counter++;
            }*/
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                pstmt1.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt.setString(counter, issuingbank);
                pstmt1.setString(counter, issuingbank);
                counter++;
            }
            /*if (functions.isValueNull(trackingid))
            {
                pstmt.setString(counter, trackingid);
                pstmt1.setString(counter, trackingid);
                counter++;
            }*/
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                pstmt1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                pstmt1.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt.setString(counter, emailAddr);
                pstmt1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                pstmt1.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, terminalid);
                pstmt1.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                pstmt1.setString(counter, customerId);
                counter++;
            }

            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);
            logger.error("pstmt:::::" + pstmt);
            logger.error("pstmt1:::::" + pstmt1);
            //hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
            hash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
            rs = pstmt1.executeQuery();
            int totalrecords = 0;
            if (rs.next())

                totalrecords = rs.getInt(1);


            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));

            }

        }

        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
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

    public HashMap getTrackingIdList(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String transactionMode) throws SystemError
    {
        logger.debug("Entering listTransactions for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (/*!Functions.isValidSQL(trackingid) || */!Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        HashMap hash        = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me        = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp        = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp        = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        //desc = ESAPI.encoder().encodeForSQL(me, desc);
        //trackingid = ESAPI.encoder().encodeForSQL(me, trackingid);
        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter                 = 1;

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            // fields = "t.trackingid as transid,t.toid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.currency,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.cardtypeid,t.customerId,t.accountid,t.remark,t.emailAddr,t.terminalid,t.orderdescription,t.timestamp,t.paymentid";
            fields = "t.trackingid as transid";
            //fields = "t.trackingid as transid,t.amount as amountcount";
            //}
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd where t.trackingid=bd.icicitransid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" as t ");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            }
            //query.append("where t.trackingid>0 ");
            query.append("where ");
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" t.timestamp >='" + startDate + "'");
            }
            else
            {
                query.append(" t.dtstamp >=" + fdtstamp );
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <='" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }
            //query.append(" and t.toid IN(" + merchantid + ")");
            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            /*if (functions.isValueNull(customerId))
            {
                {
                    query.append(" AND t.customerId IN(");
                    query.append(customerId);
                    query.append(")");
                }
            }*/
            if (functions.isValueNull(desc))
            {
                {
                    query.append(" AND t.description like '");
                    query.append(desc);
                    query.append("%'");
                }
            }
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= ?");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }*/
            /*if (functions.isValueNull(desc))
            {
                query.append(" and t.description= ? ");
            }*/
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode=  ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount");
            }
            StringBuilder countquery = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            query.append(" order by transid DESC");
            logger.debug("query::::" + query.toString());
            logger.debug("countquery::::"+countquery.toString());

            Date date4 = new Date();
            logger.error("getTrackingIdList pstmt starts ############"+date4.getTime());
            pstmt = cn.prepareStatement(query.toString());

            Date date5 = new Date();
            logger.error("getTrackingIdList pstmt1 starts ############"+date5.getTime());
            pstmt1 = cn.prepareStatement(countquery.toString());
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                pstmt1.setString(counter, status);
                counter++;
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                pstmt1.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstmt.setString(counter, fdtstamp);
                pstmt1.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                pstmt1.setString(counter, endDate);
                counter++;
            }

            else
            {
                pstmt.setString(counter, tdtstamp);
                pstmt1.setString(counter, tdtstamp);
                counter++;
            }*/

            logger.debug("pstmt:::----------::" + pstmt);
            //logger.debug("pstmt1::-----------:::" + pstmt1);
            /*if (functions.isValueNull(desc))
            {
                pstmt.setString(counter,desc);
                pstmt1.setString(counter, desc);
                counter++;
            }*/
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                pstmt1.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt.setString(counter, issuingbank);
                pstmt1.setString(counter, issuingbank);
                counter++;
            }
            /*if (functions.isValueNull(trackingid))
            {
                pstmt.setString(counter, trackingid);
                pstmt1.setString(counter, trackingid);
                counter++;
            }*/
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                pstmt1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                pstmt1.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt.setString(counter, emailAddr);
                pstmt1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                pstmt1.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, terminalid);
                pstmt1.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                pstmt1.setString(counter, customerId);
                counter++;
            }
            if (functions.isValueNull(transactionMode))
            {
                pstmt.setString(counter, transactionMode);
                pstmt1.setString(counter, transactionMode);
                counter++;
            }

            //hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
            hash    = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
            logger.error("getTrackingIdList pstmt ends ############"+new Date().getTime());
            logger.error("getTrackingIdList pstmt  diff ############"+(new Date().getTime()-date4.getTime()));
            logger.error("getTrackingIdList pstmt :::::" + pstmt);

           /* rs      = pstmt1.executeQuery();
            logger.error("getTrackingIdList pstmt1  ends ############"+new Date().getTime());
            logger.error("getTrackingIdList pstmt1  diff ############"+(new Date().getTime()-date5.getTime()));
            logger.error("getTrackingIdList pstmt1 :::::" + pstmt1);

            int totalrecords = 0;
            if (rs.next())

                totalrecords = rs.getInt(1);
            logger.debug("totalrecords::::::" + totalrecords);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            */

           /* if (totalrecords > 0)
            {*//*
            hash.put("records", "" + (hash.size() - 2));
          *//*  }*/
        }

        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
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

    public HashMap getCPTrackingIdList(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName) throws SystemError
    {
        logger.debug("Entering listTransactions for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (/*!Functions.isValidSQL(trackingid) ||*/ !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");
        }
        HashMap hash        = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me        = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp        = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp        = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        //desc = ESAPI.encoder().encodeForSQL(me, desc);
        //trackingid = ESAPI.encoder().encodeForSQL(me, trackingid);
        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter                 = 1;

        int start                   = 0; // start index
        int end                     = 0; // end index
        start                       = (pageno - 1) * records;
        end                         = records;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename = "transaction_card_present";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            // fields = "t.trackingid as transid,t.toid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.currency,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.cardtypeid,t.customerId,t.accountid,t.remark,t.emailAddr,t.terminalid,t.orderdescription,t.timestamp,t.paymentid";
            fields  = "t.trackingid as transid ";
            //}
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd where t.trackingid=bd.icicitransid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            //query.append(" as t where t.trackingid>0 ");
            query.append(" as t where ");
            /*query.append(" as t LEFT JOIN bin_details as b on t.trackingid = b.icicitransid where t.trackingid>0 ");*/
            //query.append(" and t.toid IN(" + merchantid + ")");

            if (functions.isValueNull(fdtstamp))
            {
                long milliSeconds   = Long.parseLong(fdtstamp + "000");
                Calendar calendar   = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" t.transactionTime >= '" + startDate + "'");
            }else
            {
                query.append("  t.dtstamp >="+fdtstamp);
            }

            if (functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <=  '" + endDate + "'");
            }else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }

            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            /*if (functions.isValueNull(customerId))
            {
                {
                    query.append(" AND t.customerId IN(");
                    query.append(customerId);
                    query.append(")");
                }
            }*/
            if (functions.isValueNull(desc))
            {
                {
                    query.append(" AND t.description like '");
                    query.append(desc);
                    query.append("%'");
                }
            }
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            /*if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }*/
            /*if (functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and t.transactionTime >= ?");
            }

            if (functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.transactionTime <= ? ");
            }*/

            /*if (functions.isValueNull(desc))
            {
                query.append(" and t.description= ? ");
            }*/
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid.toString()+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount");
            }

            StringBuilder countquery = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            query.append(" order by transid DESC");
            logger.debug("query::::" + query.toString());
           // logger.debug("countquery::::"+countquery.toString());
            pstmt   = cn.prepareStatement(query.toString());
            pstmt1  = cn.prepareStatement(countquery.toString());
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                pstmt1.setString(counter, status);
                counter++;
            }
            /*if (functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                pstmt1.setString(counter, startDate);
                counter++;
            }

            if (functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                pstmt1.setString(counter, endDate);
                counter++;
            }*/


            logger.debug("pstmt:::----------::" + pstmt);
            //logger.debug("pstmt1::-----------:::" + pstmt1);
            /*if (functions.isValueNull(desc))
            {
                pstmt.setString(counter,desc);
                pstmt1.setString(counter, desc);
                counter++;
            }*/
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                pstmt1.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt.setString(counter, issuingbank);
                pstmt1.setString(counter, issuingbank);
                counter++;
            }
           /* if (functions.isValueNull(trackingid))
            {
                pstmt.setString(counter, trackingid);
                pstmt1.setString(counter, trackingid);
                counter++;
            }*/
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                pstmt1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                pstmt1.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt.setString(counter, emailAddr);
                pstmt1.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                pstmt1.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, terminalid);
                pstmt1.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                pstmt1.setString(counter, customerId);
                counter++;
            }


            logger.error("pstmt:::::" + pstmt);
            //logger.error("pstmt1:::::" + pstmt1);

            //hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
            hash    = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
            /*rs      = pstmt1.executeQuery();
            int totalrecords = 0;
            if (rs.next())

                totalrecords = rs.getInt(1);
            logger.debug("totalrecords::::::" + totalrecords);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");*/

           /* if (totalrecords > 0)
            {*/
            //hash.put("records", "" + (hash.size() - 2));
          /*  }*/
        }

        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
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

    public Hashtable<String, String> getPartnerMemberDetails(String partnerid)
    {
        Hashtable<String, String> memberid = new Hashtable<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String qry = "select memberid from members where partnerId IN("+partnerid+")";
            pstmt = con.prepareStatement(qry);
            //pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = count + 1;
                memberid.put(String.valueOf(count), rs.getString("memberid"));
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
        return memberid;
    }

    // MODIFIED THE QUERY TO RETRIVE MEMBERS OF PARTNER OR SUPERPARTNER
    public Hashtable<String, String> getPartnerMemberDetailsNew(String partnerid)
    {
        Hashtable<String, String> memberid = new Hashtable<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String qry = "SELECT m.memberid members FROM members m , partners p WHERE m.partnerId=p.partnerId AND (p.superadminid=? OR m.partnerId=?)";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = count + 1;
                memberid.put(String.valueOf(count), rs.getString("members"));
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
        return memberid;
    }


    //converts strtig to int if string is "" then returns default value passed to it

    public Hashtable<String, String> getPartnerMembersDetails(String partnerid)
    {
        Hashtable<String, String> memberid = new Hashtable<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select memberid,company_name  from members where partnerId=? ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid.put(rs.getString("memberid"), rs.getString("memberid") + "-" + rs.getString("company_name"));
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
        return memberid;
    }


    //To fetch roles of login partner as per id.
    public static String getRoleofPartner(String partnerid)
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



    //To fetch login of login partner as per id.
    public String getNameofPartner(String partnerid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_roll=null;
        try
        {

            con = Database.getRDBConnection();
            String qry = "select login AS NAME from partners where partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                v_roll = rs.getString("NAME");
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

    //To fetch login of login partner as per id.
    public String getOldCheckFlag(String partnerid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String vreturn=null;
        try
        {

            con = Database.getRDBConnection();
            String qry = "select oldcheckout  from partners where partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                vreturn = rs.getString("oldcheckout");
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
        return vreturn;
    }

    //To fetch (partner name) of login partner as per id.
    public String getPartnerName(String partnerid)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_name=null;
        try
        {

            con = Database.getRDBConnection();
            String qry = "select partnerName AS NAME from partners where partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                v_name = rs.getString("NAME");
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
        return v_name;
    }


    //To fetch partnerid of partner name
    public String getPartnerIdBYname(String partnername)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String v_id=null;
        try
        {

            con = Database.getRDBConnection();
            String qry = "select partnerId AS ID from partners where partnerName=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnername);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                v_id = rs.getString("ID");
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
        return v_id;
    }



    //To fetch members(COMPANY NAME) of all partners under superpartner
    public TreeMap<String, String> getSuperPartnerMemberDetailsForUI(String partnerid)
    {
        TreeMap<String, String> memberid = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select m.memberid memberid,m.company_name company_name from members m , partners p where m.partnerId=p.partnerId and p.superadminid=? or m.partnerId=? ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);

            rs = pstmt.executeQuery();
            while (rs.next())
            {


                memberid.put(String.valueOf(rs.getInt("memberid")), String.valueOf(rs.getInt("memberid")) + "-" + rs.getString("company_name"));
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
        return memberid;
    }



    public TreeMap<String, String> getPartnerMemberDetailsForUI(String partnerid)
    {
        TreeMap<String, String> memberid = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select memberid,company_name from members where partnerId=? ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid.put(String.valueOf(rs.getInt("memberid")), String.valueOf(rs.getInt("memberid")) + "-" + rs.getString("company_name"));
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
        return memberid;
    }

    //To fetch all partners under superpartner with Current Superpartner.
    public TreeMap<String, String> getPartnerDetailsForUI(String partnerid)
    {
        TreeMap<String, String> memberid = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = Integer.valueOf(partnerid);
        memberid.put(partnerid, id + "-" + "Current Superpartner");
        try
        {
            con = Database.getRDBConnection();
            String qry = "select partnerId,partnerName from partners where activation='T' AND superadminid=? ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid.put(String.valueOf(rs.getInt("partnerId")), String.valueOf(rs.getInt("partnerId")) + "-" + rs.getString("partnerName"));
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
        return memberid;
    }

    //To fetch all partners under superpartner.
    public TreeMap<String, String> getPartnerDetailsForMap(String partnerid)
    {
        TreeMap<String, String> memberid = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = Integer.valueOf(partnerid);
        try
        {
            con = Database.getRDBConnection();
            String qry = "select partnerId,partnerName from partners where activation='T' AND superadminid=? ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid.put(String.valueOf(rs.getInt("partnerId")), String.valueOf(rs.getInt("partnerId")) + "-" + rs.getString("partnerName"));
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
        return memberid;
    }

    public LinkedHashMap<Integer, String> getPartnerMembersDetail(String partnerid)
    {
        LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select memberid,company_name,activation from members where partnerId=? ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String activation = "N";
                if ("Y".equals(rs.getString("activation"))) activation = "Y";
                memberHash.put(rs.getInt("memberid"), rs.getString("company_name") + "-" + activation);
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
        return memberHash;
    }

    //To fetch members of all partners under superpartner
    public LinkedHashMap<Integer, String> getSuperPartnerMembersDetail(String partnerid)
    {
        LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select m.memberid memberid,m.company_name company_name,m.activation activation from members m , partners p where m.partnerId=p.partnerId and (p.superadminid=? or m.partnerId=?)  ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String activation = "N";
                if ("Y".equals(rs.getString("activation"))) activation = "Y";
                memberHash.put(rs.getInt("memberid"), rs.getString("company_name") + "-" + activation);
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
        return memberHash;
    }
    //getting list of members under superpartner and there partners
    public List<String> getMembersUnderSuperpartner(String partnerid)
    {
        List<String> memberlist = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select m.memberid memberid from members m , partners p where m.partnerId=p.partnerId and (p.superadminid=? or m.partnerId=?)  ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberlist.add(rs.getString("memberid"));
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
        return memberlist;
    }

    //TO RETRIVE ALL PARTNERS UNDER SUPERPARTNER.
    public LinkedHashMap<Integer, String> getPartnerDetails(String partnerid)
    {
        LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT partnerid,partnerName FROM partners  WHERE superadminid = ? ORDER BY partnerid DESC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberHash.put(rs.getInt("partnerid"), rs.getString("partnerid") + "-" + rs.getString("partnerName"));
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
        return memberHash;
    }

    public LinkedHashMap<String, Integer> getPartnerCountryDetail(String partnerid)
    {
        LinkedHashMap<String, Integer> memberHash = new LinkedHashMap();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select memberid,country,activation from members where partnerId=? GROUP  BY country";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                String activation = "N";
                if ("Y".equals(rs.getString("activation"))) activation = "Y";
                memberHash.put(rs.getString("country"), rs.getInt("memberid"));
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
        return memberHash;
    }

    //new
    public TreeMap<Integer, String> getPartnerMemberDetail(String partnerid)
    {
        TreeMap<Integer, String> memberid = new TreeMap<Integer, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "select memberid,company_name from members where partnerId=? ORDER BY memberid ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid.put(rs.getInt("memberid"), rs.getString("company_name"));
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
        return memberid;
    }
    //converts mm/dd/yy to millisec

    //new
 /*   public TreeMap<Integer, String> getRiskRuleMapping(String partnerid)
    {   TreeMap<String, String> memberid = new TreeMap<String, String>();
        Connection con=null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String query = "SELECT mampm.monitoing_para_id,mpm.monitoing_para_name,terminalid FROM member_account_monitoringpara_mapping AS mampm JOIN monitoring_parameter_master AS mpm ON mampm.monitoing_para_id=mpm.monitoing_para_id WHERE memberid=?+  AND terminalid=?";
            PreparedStatement pstmt= con.prepareStatement(query);
            pstmt = con.prepareStatement(query);
            ResultSet rs=pstmt.executeQuery();
            rs = pstmt.executeQuery();
            int count=0;
            while(rs.next())
            {
                count=count+1;
                memberid.put(String.valueOf(count),rs.getString("memberid"));
            }


        }
        catch(Exception e)
        {
            logger.error("error",e);
        }
        finally {
            Database.closeConnection(con);
        }
        return memberid;
    }*/

    public String getPartnerId(String memberId)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        String sMemberList = "";
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String qry = "select partnerId from members where memberid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                sMemberList = rs.getString("partnerId");
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
        return sMemberList;
    }

    public String getPartnerMemberRS(String partnerid)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        String sMemberList = "";
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String qry = "select memberid from members where partnerId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                sMemberList = sMemberList + "'" + rs.getString("memberid") + "',";
            }
            if (!sMemberList.equalsIgnoreCase(""))
            {
                sMemberList = sMemberList.substring(0, sMemberList.length() - 1);
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
        return sMemberList;
    }

    //TO FETCH ALL MEMBERS OF PARTNER OR SUPERPARTNER.
    public String getSuperpartnersMemberRS(String partnerid)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        String sMemberList = "";
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String qry = "SELECT m.memberid members FROM members m , partners p WHERE m.partnerId=p.partnerId AND (p.superadminid=? OR m.partnerId=?)";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                sMemberList = sMemberList + "'" + rs.getString("members") + "',";
            }
            if (!sMemberList.equalsIgnoreCase(""))
            {
                sMemberList = sMemberList.substring(0, sMemberList.length() - 1);
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
        return sMemberList;
    }

    public boolean isValueNull(String str)
    {
        if (str != null && !str.equals("null") && !str.equals(""))
        {
            return true;
        }
        return false;
    }


    /*public static String generateSignature(String memberid)
    {

        ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");
        String invoicetemplate = "Y";
        Connection conn = null;
        String query2 = "select invoicetemplate from members where memberid= ?";
        try
        {
            conn = Database.getConnection();

            PreparedStatement pstmnt = conn.prepareStatement(query2);
            pstmnt.setString(1, (String) memberid);
            ResultSet x = pstmnt.executeQuery();
            x.next();
            invoicetemplate = x.getString("invoicetemplate");

        }
        catch (SQLException e)
        {
            logger.error("Exception Occured", e);
            return RB.getString("DEFAULTSIGN");

        }
        catch (SystemError se)
        {
            logger.error("Exception Occured", se);
            return RB.getString("DEFAULTSIGN");

        }
        finally
        {
            Database.closeConnection(conn);
        }
        if (invoicetemplate.equalsIgnoreCase("Y"))
        {
            return RB.getString("DEFAULTSIGN");
        }
        else
        {

            String signature = "";
            try
            {

                Hashtable details = Template.getMemberTemplateDetails(memberid);
                signature += "Customer Care <br>";
                if (details.get("PHONE1") != null)
                    signature += "<table border=0 ><tr><td><p><font size=\"2\" face=\"Verdana\">Phone No : </p></td><td><p><font size=\"2\" face=\"Verdana\">" + details.get("PHONE1") + "</p></td></tr>";
                if (details.get("PHONE2") != null)
                    signature += "<tr><td>&nbsp;</td><td><p><font size=\"2\" face=\"Verdana\">" + details.get("PHONE2") + "</p></td></tr>";

                String em = "";
                em = (String) details.get("EMAILS");
                if (em != null)
                {
                    String emails[] = em.split(",");
                    for (int count = 0; count < emails.length; count++)
                    {
                        if (count == 0)
                            signature += "<tr><td><p><font size=\"2\" face=\"Verdana\">Email Address : </p></td><td><p><font size=\"2\" face=\"Verdana\">" + emails[count] + "</p></td></tr>";
                        else
                            signature += "<tr><td>&nbsp;</td><td><p><font size=\"2\" face=\"Verdana\">" + emails[count] + "</p></td></tr>";
                    }

                }


            }
            catch (SystemError e)
            {
                logger.error("Exception Occured", e);
            }
            return signature;
        }
    }
*/

    public String getAgentMemberRS(String agentid)
    {
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        String sMemberList = "";
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            String qry = "select memberid from members where agentId=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, agentid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                sMemberList = sMemberList + rs.getString("memberid") + ",";
            }
            if (!sMemberList.equalsIgnoreCase(""))
            {
                sMemberList = sMemberList.substring(0, sMemberList.length() - 1);
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
        return sMemberList;
    }

    public boolean isEmptyOrNull(String str)
    {
        if (str == null || str.equals("null") || str.trim().equals(""))
        {
            return true;
        }
        return false;
    }

    public String getUserRole(User user)
    {
        String userRole = ROLE;
        for (String role : user.getRoles())
        {
            logger.debug("role in method---" + role);
            if (role.equals(SUBPARTNER))
            {
                userRole = SUBPARTNER;
            }
            else if (role.equals(CHILDSUBPARTNER))
            {
                userRole = CHILDSUBPARTNER;
            }
            else if (role.equals(SUPERPARTNER))
            {
                userRole = SUPERPARTNER;
            }
        }

        return userRole;
    }

    public String getPartnerLoginfromUser(String login)
    {
        String userLogin = null;
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String userQuery = "SELECT p.login,pu.userid FROM partners AS p , partner_users AS pu WHERE p.partnerid=pu.partnerid AND pu.login = ?";
            pstmt1 = conn.prepareStatement(userQuery);
            pstmt1.setString(1, login);
            rs = pstmt1.executeQuery();
            if (rs.next())
            {
                userLogin = rs.getString("login");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError---", se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return userLogin;
    }

    public List<String> perCurrency(String memberid, String company_name) throws SystemError
    {
        List<String> currencyList = new ArrayList<>();
        Connection con = null;
        StringBuffer query = new StringBuffer("SELECT currency FROM transaction_common");
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            query.append(" WHERE totype='" + company_name + "'");
            if (functions.isValueNull(memberid))
            {
                query.append(" AND toid =" + memberid);
            }
            query.append(" GROUP BY currency");
            pstm = con.prepareStatement(query.toString());
            logger.debug("currency pstm::::" + pstm);
            rs = pstm.executeQuery();
            while (rs.next())
            {
                currencyList.add(rs.getString("currency"));
            }

        }
        catch (SQLException e)
        {
            logger.debug("Error while connecting to database--" + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstm);
            Database.closeConnection(con);
        }

        return currencyList;

    }

    public String allCurrency(String memberid) throws SystemError
    {
        String currency = "";
        Connection con = null;
        StringBuffer query = new StringBuffer();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            query.append("SELECT currency FROM transaction_common");
            if (functions.isValueNull(memberid))
            {
                query.append(" WHERE toid=" + memberid);
            }
            query.append(" GROUP BY currency");
            pstm = con.prepareStatement(query.toString());
            rs = pstm.executeQuery();
            while (rs.next())
            {
                currency = currency + "'" + rs.getString("currency") + "',";
            }
            if (!currency.equalsIgnoreCase(""))
            {
                currency = currency.substring(0, currency.length() - 1);
            }

        }
        catch (SQLException e)
        {
            logger.debug("Error while connecting to database--" + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstm);
            Database.closeConnection(con);
        }
        return currency;

    }

    public String getAccountIds(String memberid) throws SystemError
    {
        String accountid = "";
        Connection con = null;
        StringBuffer query = new StringBuffer();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();
            query.append("SELECT accountid FROM transaction_common");
            if (functions.isValueNull(memberid))
            {
                query.append(" WHERE toid=" + memberid);
            }
            query.append(" GROUP BY accountid");
            pstm = con.prepareStatement(query.toString());
            rs = pstm.executeQuery();
            while (rs.next())
            {
                accountid =rs.getString("accountid");
            }
        }
        catch (SQLException e)
        {
            logger.debug("Error while connecting to database--" + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstm);
            Database.closeConnection(con);
        }
        return accountid;

    }


    public TreeMap<String, String> riskRuleList(String memberid, String terminalid) throws SystemError
    {
        TreeMap<String, String> riskrulename = new TreeMap<>();
        Connection con = null;
        String query = "";
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try
        {
            con = Database.getRDBConnection();

            if (functions.isValueNull(memberid) && functions.isValueNull(terminalid))
            {
                query = "SELECT mampm.monitoing_para_id,mpm.monitoing_para_name,terminalid FROM member_account_monitoringpara_mapping AS mampm JOIN monitoring_parameter_master AS mpm ON mampm.monitoing_para_id=mpm.monitoing_para_id WHERE memberid = ? AND terminalid = ?";
                pstm = con.prepareStatement(query);
                pstm.setString(1, memberid);
                pstm.setString(2, terminalid);
            }
            rs = pstm.executeQuery();
            while (rs.next())
            {
                riskrulename.put(rs.getString("monitoing_para_id"), rs.getString("monitoing_para_name"));
            }
        }
        catch (SQLException e)
        {
            logger.debug("Error while connecting to database--" + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstm);
            Database.closeConnection(con);
        }

        return riskrulename;

    }

    public boolean isPartnerMemberMapped(String memberid, String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String mappedPartnerId = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "SELECT partnerid FROM members WHERE memberid = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, memberid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                mappedPartnerId = rs.getString("partnerid");
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        if (functions.isValueNull(mappedPartnerId) && mappedPartnerId.equals(partnerid))
        {
            return true;
        }
        return false;
    }

    public boolean isPartneridExistforModules(String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String selquery = "SELECT 'X' FROM partner_users_modules_mapping  WHERE userid='0' AND partnerid = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1);
                if(status.equals("X")){
                    return  true;
                }
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    //TO CHECK IF GIVEN MEMBER ID IS MAPPED WITH PARTNER OR SUPERPARTNER.
    public boolean isPartnerSuperpartnerMembersMapped(String memberid , String partnerid){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;
        try{
            String query ;
            con = Database.getRDBConnection();
            query= "SELECT 'X' FROM members m , partners p WHERE m.partnerId=p.partnerId AND (p.superadminid=? OR m.partnerId=?) AND m.memberid=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            pstmt.setString(3, memberid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1) ;
                if(status.equals("X")){
                    return true;
                }
            }
        }
        catch(Exception e){
            logger.error("Exception in isMemberUser method: ", e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }



    //TO CHECK IF GIVEN PARTNER AND SUPER PARTNER IS MAPPED
    public boolean isPartnerSuperpartnerMapped(String partnerid , String superpartnerid){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;
        if(partnerid.equals(superpartnerid)){
            return true;
        }
        else
        {
            try
            {
                String query;
                con = Database.getRDBConnection();
                query = "SELECT 'X' FROM partners WHERE superadminid=? AND PARTNERID =?";
                pstmt = con.prepareStatement(query);
                pstmt.setString(1, superpartnerid);
                pstmt.setString(2, partnerid);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    status = rs.getString(1);
                    if (status.equals("X"))
                    {
                        return true;
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Exception in isMemberUser method: ", e);

            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(con);
            }
            return false;
        }

    }

    public HashMap getMerchantWireReports(String memberid, String terminalId, String fdtstamp, String tdtstamp,String settledId) throws SystemError
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String mappedPartnerId = null;
        HashMap memberHash = new HashMap<>();
        try
        {
            StringBuffer query = new StringBuffer();
            con = Database.getRDBConnection();
            query= new StringBuffer("SELECT * FROM merchant_wiremanager WHERE markedfordeletion='N' ");
            if(functions.isValueNull(memberid))
            {
                query.append(" and toid ='"+memberid+"'");
            }

            if (functions.isValueNull(terminalId))
            {
                query.append(" and terminalid ='"+terminalId+"'");
            }

            if(functions.isValueNull(fdtstamp))
            {
                query.append(" and wirecreationtime >='"+fdtstamp+"'");
            }

            if(functions.isValueNull(tdtstamp))
            {
                query.append(" and wirecreationtime <='"+tdtstamp+"'");
            }
            if(functions.isValueNull(settledId))
            {
                query.append(" and settledid ="+settledId);
            }
            query.append(" ORDER BY timestamp DESC");
            logger.debug("query:::::::::"+query);
            memberHash = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), con));

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        return memberHash;
    }

    public String getRole(String userName)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String role = null;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("get user name method");
            String selquery = "SELECT roles FROM user WHERE login = ? ";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, userName);
            logger.debug("pstmt:::::"+pstmt);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                logger.debug("role inside rs:::"+rs.getString("roles"));
                if("superpartner".equals(rs.getString("roles")))
                    role = "superpartner";
                else if("partner".equals(rs.getString("roles")))
                    role = "partner";
                else if("subpartner".equals(rs.getString("roles")))
                    role = "subpartner";
                else if("childsuperpartner".equals(rs.getString("roles")))
                    role = "childsuperpartner";
            }

        }

        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        logger.debug("role::::::::"+role);
        return role;
    }

    public Map<String, String> getPartnerLogoAndIcon(String partnerid)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> logoMap = new HashMap<>();
        try
        {
            conn = Database.getRDBConnection();
            String selquery = "SELECT logoName,iconName,faviconName FROM partners WHERE partnerid = ? ";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                logoMap.put("logoName",rs.getString("logoName"));
                logoMap.put("iconName",rs.getString("iconName"));
                logoMap.put("faviconName",rs.getString("faviconName"));
            }

        }

        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return logoMap;
    }

    public String getPrivacyURL(String partnerId){
        String URL="";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PartnerFunctions partner = new PartnerFunctions();
        try
        {
            conn = Database.getRDBConnection();
            String selquery = "SELECT privacyUrl FROM partners WHERE partnerId = ?";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                URL = rs.getString("privacyUrl");
            }

        }

        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return URL;
    }

    //Method to make dynamic cookies URL.
    public String getCookiesURL(String partnerId){
        String URL="";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PartnerFunctions partner = new PartnerFunctions();
        try
        {
            conn = Database.getRDBConnection();
            String selquery = "SELECT cookiesUrl FROM partners WHERE partnerId = ?";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                URL = rs.getString("cookiesUrl");
            }
        }

        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        return URL;
    }

    public Map<String, String> getPartnerLogo(String partnerid)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, String> logoMap = new HashMap<>();
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("get user name method");
            String selquery = "SELECT logoName,iconName,faviconName FROM partners WHERE partnerId=?";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, partnerid);
            logger.debug("pstmt:::::"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())

            {
                logoMap.put("logoName",rs.getString("logoName"));
                logoMap.put("iconName",rs.getString("iconName"));
                logoMap.put("faviconName",rs.getString("faviconName"));
            }

        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return logoMap;
    }

    public String getListOfSubPartner(String superPartnerId)
    {
        String stringList=null;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try
        {
            connection=Database.getRDBConnection();
            String query="SELECT partnerid FROM partners WHERE superadminid IN(?)";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,superPartnerId);
            resultSet=preparedStatement.executeQuery();
            stringList=superPartnerId;
            while (resultSet.next())
            {
                stringList+=","+resultSet.getString("partnerid");
            }
        }
        catch (SQLException sql)
        {
            logger.error("Exception---",sql);
        }
        catch (Exception e){
            logger.error("Exception---" + e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }
        return stringList;
    }

    public String getSubpartner(String superPartnerId)
    {
        String stringList="0";
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try
        {
            connection=Database.getRDBConnection();
            String query="SELECT partnerid FROM partners WHERE superadminid IN(?)";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,superPartnerId);
            resultSet=preparedStatement.executeQuery();
            while (resultSet.next())
            {
                stringList+=","+resultSet.getString("partnerid");
            }
        }
        catch (SQLException sql)
        {
            logger.error("Exception---",sql);
        }
        catch (Exception e){
            logger.error("Exception---" + e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }
        return stringList;
    }

    public String getListOfSubPartnerName(String partnerId)
    {
        String stringList=null;
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;
        try
        {
            connection=Database.getRDBConnection();
            String query="SELECT partnerName FROM partners WHERE partnerid IN("+partnerId+")";
            preparedStatement=connection.prepareStatement(query);
            resultSet=preparedStatement.executeQuery();
            while (resultSet.next())
            {
                stringList+=","+resultSet.getString("partnerName");
            }
        }
        catch (SQLException sql)
        {
            logger.error("Exception---",sql);
        }
        catch (Exception e){
            logger.error("Exception---" + e);
        }
        finally {
            Database.closePreparedStatement(preparedStatement);
            Database.closeResultSet(resultSet);
            Database.closeConnection(connection);
        }
        return stringList;
    }

    public String getUnblockStatus(String login)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String unblock="";
        try
        {

            con = Database.getRDBConnection();
            String qry = "select unblocked from user u where  login=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, login);
            logger.debug("partner query::::" + pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                unblock = rs.getString("unblocked");
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
        return unblock;
    }
    public Hashtable<String, String> getPartnerNameFromPartnerId(String partnerid,String memberId)
    {
        Hashtable<String, String> partnerName = new Hashtable<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select p.partnerName from partners p");
            if(functions.isValueNull(memberId))
                qry.append(" ,members m where p.partnerName=m.partnerId and m.memberId="+memberId);
            else
                qry.append(" where p.partnerId>0");
            qry.append(" and p.partnerId ="+partnerid);
            pstmt = con.prepareStatement(qry.toString());
            //pstmt.setString(1, partnerid);
            logger.error("getPartnerNameFromPartnerId--->"+pstmt);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = count + 1;
                partnerName.put(String.valueOf(count), rs.getString("partnerName"));
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
        return partnerName;
    }


    public Hashtable<String, String> getPartnerNameFromPartnerId1(String partnerid)
    {
        Hashtable<String, String> partnerName = new Hashtable<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("select p.partnerName from partners p where p.partnerId="+partnerid);


            pstmt = con.prepareStatement(qry.toString());
            //pstmt.setString(1, partnerid);
            logger.error("getPartnerNameFromPartnerId--->"+pstmt);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = count + 1;
                partnerName.put(String.valueOf(count), rs.getString("partnerName"));
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
        return partnerName;
    }

    public Hashtable<String, String> getPartnerNameFromPartnerIdAndSuperPartnerId(String partnerid,String memberId)
    {
        Hashtable<String, String> partnerName = new Hashtable<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT p.partnerName FROM partners p");
            if(functions.isValueNull(memberId))
                qry.append(" ,members m WHERE m.partnerId=p.partnerId and m.memberId="+memberId);
            else
                qry.append(" where p.partnerId>0");
            qry.append(" AND (p.superadminid=? OR p.partnerId=?)");
            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            logger.error("getPartnerNameFromPartnerIdAndSuperPartnerId--->"+pstmt);
            rs = pstmt.executeQuery();
            int count = 0;
            while (rs.next())
            {
                count = count + 1;
                partnerName.put(String.valueOf(count), rs.getString("partnerName"));
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
        return partnerName;
    }
    public TreeMap<String, String> getPartnerDetailsForPartnerUI(String partnerid)
    {
        TreeMap<String, String> memberid = new TreeMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = Integer.valueOf(partnerid);
        memberid.put(partnerid, id + "-" + partnerDAO.getPartnerName(partnerid));
        try
        {
            con = Database.getRDBConnection();
            String qry = "select partnerId,partnerName from partners where activation='T' AND superadminid=? ORDER BY partnerId ASC";
            pstmt = con.prepareStatement(qry);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                memberid.put(String.valueOf(rs.getInt("partnerId")), String.valueOf(rs.getInt("partnerId")) + "-" + rs.getString("partnerName"));
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
        return memberid;
    }

    public TreeMap<String, String> getAgentDetails()
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet resultSet = p.executeQuery();
            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("agentid")),String.valueOf(resultSet.getInt("agentid"))+"-"+ resultSet.getString("agentName"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }
    public TreeMap<String, String> getAgentDetailsPartnerSession(String partnerId)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT a.agentid,a.agentName FROM agents a,partners p WHERE a.partnerId=p.partnerId AND (p.superadminid='"+partnerId+"' OR p.partnerId='"+partnerId+"')";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet resultSet = p.executeQuery();
            System.out.println("query"+query);
            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("agentid")),String.valueOf(resultSet.getInt("agentid"))+"-"+ resultSet.getString("agentName"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }
    public TreeMap<String, String> getAgentDetailsPartner(String partnerId)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT a.agentid,a.agentName FROM agents a WHERE a.partnerId='"+partnerId+"'";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet resultSet = p.executeQuery();
            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("agentid")),String.valueOf(resultSet.getInt("agentid"))+"-"+ resultSet.getString("agentName"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }

    public TreeMap<String, String> getSuperPartnerAgentDetailsForUI(String partnerid)
    {
        TreeMap <String,String>agentid = new TreeMap<String,String>();
        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs= null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT a.agentid,a.agentName FROM agents a, partners p WHERE a.partnerId=p.partnerId and p.superadminid=? or a.partnerId=? ORDER BY agentid ASC";
            ps = conn.prepareStatement(query);
            ps.setString(1,partnerid);
            ps.setString(2,partnerid);
            logger.debug("partner query: "+ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                agentid.put(String.valueOf(rs.getInt("agentid")),String.valueOf(rs.getInt("agentid"))+"-"+ rs.getString("agentName"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return agentid;
    }

    public TreeMap<String, String> getPartnerAgentDetailsForUI(String partnerid)
    {
        TreeMap <String,String>agentid = new TreeMap<String,String>();
        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs= null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT agentid,agentName FROM agents WHERE partnerId=? ORDER BY agentid ASC";
            ps = conn.prepareStatement(query);
            ps.setString(1,partnerid);
            logger.debug("partner query: "+ps);
            rs = ps.executeQuery();
            while(rs.next())
            {
                agentid.put(String.valueOf(rs.getInt("agentid")),String.valueOf(rs.getInt("agentid"))+"-"+ rs.getString("agentName"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return agentid;
    }
    public boolean isAgentpartnerMapped(String agentid , String partnerid){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;
        try{
            String query ;
            con = Database.getRDBConnection();
            query= "SELECT 'X' FROM agents WHERE agentId=?  AND partnerId=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, agentid);
            pstmt.setString(2, partnerid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1) ;
                if(status.equals("X")){
                    return true;
                }
            }
        }
        catch(Exception e){
            logger.error("Exception in isMemberUser method: ", e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }

    public boolean isPartnerAgentMapped(String agentId, String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String mappedPartnerId = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isAgent method");
            String selquery = "SELECT partnerid FROM agents WHERE agentId = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, agentId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                mappedPartnerId = rs.getString("partnerid");
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isAgent method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isAgent method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        if (functions.isValueNull(mappedPartnerId) && mappedPartnerId.equals(partnerid))
        {
            return true;
        }
        return false;
    }

    public boolean isPartnerSuperpartnerAgentsMapped(String agentId , String partnerid){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String status = null;
        try{
            String query ;
            con = Database.getRDBConnection();
            query= "SELECT 'X' FROM agents a , partners p WHERE a.partnerId=p.partnerId AND (p.superadminid=? OR a.partnerId=?) AND a.agentId=?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
            pstmt.setString(3, agentId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                status = rs.getString(1) ;
                if(status.equals("X")){
                    return true;
                }
            }
        }
        catch(Exception e){
            logger.error("Exception in isMemberUser method: ", e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return false;
    }

    public String getTotalAmount(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String transactionMode) throws SystemError
    {
        logger.debug("Entering getTotalAmount for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        String amount   = "";
        if (!Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();

        Codec me        = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp        = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp        = ESAPI.encoder().encodeForSQL(me, tdtstamp);

        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter                 = 1;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename = "transaction_common";
            if (archive)
            {
                //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            fields = "SUM(t.amount) as amountcount";

            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" as t ");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            }
            //query.append("where t.trackingid>0 ");
            query.append("where ");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" t.timestamp >='" + startDate + "'");
            }
            else
            {
                query.append(" t.dtstamp >="+fdtstamp);
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <='" + endDate + "'");
            }
            else
            {
                query.append(" and t.dtstamp <="+tdtstamp);
            }

            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            if (functions.isValueNull(desc))
            {
                {
                    query.append(" AND t.description like '");
                    query.append(desc);
                    query.append("%'");
                }
            }
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status  = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= ?");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }*/
            /*if (functions.isValueNull(desc))
            {
                query.append(" and t.description= ? ");
            }*/
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode=  ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount");
            }

            //query.append(" order by transid DESC");
            logger.debug("query::::" + query.toString());

            Date date4  = new Date();
            logger.error("getTotalAmount starts ############"+date4.getTime());
            pstmt       = cn.prepareStatement(query.toString());
            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                counter++;
            }
            /*if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                counter++;
            }

            else
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }*/

            logger.debug("pstmt:::----------::" + pstmt);
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt.setString(counter, issuingbank);
                counter++;
            }
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                counter++;
            }
            if (functions.isValueNull(transactionMode))
            {
                pstmt.setString(counter, transactionMode);
                counter++;
            }

            rs = pstmt.executeQuery();

            rs.next();
            amount = rs.getString("amountcount");
            logger.error("getTotalAmount ends ############"+new Date().getTime());
            logger.error("getTotalAmount diff ############"+(new Date().getTime()-date4.getTime()));
            logger.error("getTotalAmount pstmt:::::" + pstmt);
            logger.debug("amount in getTotalAmount ===> " +amount);
        }

        catch (SQLException se)
        {
            logger.error("SQL Exception leaving getTotalAmount", se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        logger.debug("Leaving getTotalAmount");
        return amount;
    }
    public String getPartnerNameFromPartnerid(String partnerid)
    {
        //Hashtable<String, String> partnerName = new Hashtable<String, String>();
        String partnername      = "";
        Connection con          = null;
        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        try
        {
            //con=Database.getConnection();
            con = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT partnerName FROM partners where superadminid IN(?) OR partnerId IN(?)");

            pstmt = con.prepareStatement(qry.toString());
            pstmt.setString(1, partnerid);
            pstmt.setString(2, partnerid);
//            logger.debug("getPartnerNameFromPartnerIdAndSuperPartnerId Fraud--->" + pstmt);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                partnername = partnername + "'" + rs.getString("partnerName") + "',";

            }
            if (!partnername.equalsIgnoreCase(""))
            {
                partnername = partnername.substring(0, partnername.length() - 1);

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
        return partnername;
    }

    public int getCountOfPayoutList(String desc,String status, String tdtstamp, String fdtstamp, StringBuffer trackingid, String accountid, String merchantid, String emailaddr,String dateType, String terminalid, String partnerName,int pageno,int records, String bankaccount, String amount, String remark, String currency) throws Exception
    {

//    System.out.println("only partnerName ==="+partnerName);
        StringBuilder query         = new StringBuilder();
        Connection cn               = Database.getRDBConnection();
        ResultSet rs                = null;
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        String tablename            = "";
        String fields               = "";
        int totalrecords            = 0;


        // Encoding for sql injection
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp = ESAPI.encoder().encodeForSQL(me, tdtstamp);

        int counter = 1;
        try
        {
            tablename = "transaction_common";

            fields  = tablename+".trackingid as transid,"+tablename+".toid,"+tablename+".description,"+tablename+".amount,"+tablename+".dtstamp,"+tablename+".emailaddr,"+tablename+".accountid,"+tablename+".timestamp,"+tablename+".status,"+tablename+".remark,"+tablename+".currency,"+tablename+".terminalid,"+tablename+".totype,ts.bankaccount";

            query.append(" SELECT DISTINCT "+ fields + " From "+ tablename +" join transaction_safexpay_details as ts on ts.trackingid = "+tablename+".trackingid  where ");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" "+tablename+".timestamp >= '" + startDate + "'");
            }
            else if(functions.isValueNull(fdtstamp))
            {
                query.append(" "+tablename+".dtstamp >= "+fdtstamp);
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and "+tablename+".timestamp <= '" + endDate + "'");
            }
            else if(functions.isValueNull(tdtstamp))
            {
                query.append(" and "+tablename+".dtstamp <= "+tdtstamp);
            }

            if(functions.isValueNull(merchantid))
            {
                query.append(" and "+tablename+".toid="+merchantid);
            }

            if (functions.isValueNull(desc))
            {
                query.append(" and "+tablename+".description like '" + desc + "%'");
            }

            if(accountid !=null && !accountid.equals("") && !accountid.equals( "null")){

                query.append(" and "+tablename+".accountid="+accountid);
            }

            if(functions.isValueNull(emailaddr))
            {
                query.append(" and "+tablename+".emailaddr="+emailaddr);
            }
            if(functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount="+bankaccount);
            }
            if(functions.isValueNull(status))
            {
                query.append(" and "+tablename+".status=?");

            }else if(status.equals(""))
            {
                query.append(" and "+tablename+".status in ('payoutsuccessful','payoutfailed','payoutstarted')");
            }
            if(functions.isValueNull(amount))
            {
                query.append(" and "+tablename+".amount="+amount);
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and "+tablename+".trackingid IN("+trackingid.toString()+")");
            }
            StringBuilder countquery = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            Date date5 = new Date();
            logger.error("getCountOfPayoutList pstmt1 starts ############" + date5.getTime());
            pstmt1  = cn.prepareStatement(countquery.toString());


            if (functions.isValueNull(status))
            {
                pstmt1.setString(counter, status);
                counter++;
            }

            if (functions.isValueNull(emailaddr))
            {
                pstmt1.setString(counter, emailaddr);
                counter++;
            }

            logger.error("query--->"+pstmt1);
            rs      = pstmt1.executeQuery();
            logger.error("getCountOfPayoutList ends ############"+new Date().getTime());
            logger.error("getCountOfPayoutList diff ############"+(new Date().getTime()-date5.getTime()));

            if (rs.next())
                totalrecords = rs.getInt(1);
            logger.error("getCountOfPayoutList pstmt1----:::::" + pstmt1+totalrecords);
        }catch(Exception e)
        {
            logger.error("catch block exception ---"+e);
            throw new SystemError(e.toString());
        }
        return totalrecords;
    }

    public HashMap getTransactionListDetails(String desc,String status, String tdtstamp, String fdtstamp, StringBuffer trackingid, String accountid, String merchantid, String emailaddr,String dateType, String terminalid, String partnerName,int pageno,int records, String bankaccount, String amount, String remark,String currency,int totalRecords) throws Exception
    {
        logger.debug("inside getTransactionListDetails method ::");
        StringBuilder query     = new StringBuilder();
        Connection con          = Database.getRDBConnection();
        ResultSet rs            = null;
        HashMap hash            = null;
        PreparedStatement pstmt = null;
        String tablename        = "";
        String fields           = "";
        String orderby          = "";


        // Encoding for sql injection
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp = ESAPI.encoder().encodeForSQL(me, tdtstamp);

        int counter = 1;
        int start = 0;
        int end = 0;
        start = (pageno - 1) * records;
        end = records;
        if (totalRecords > records)
        {
            end = totalRecords - start;
            if (end > records)
            {
                end = records;
            }
        }
        else
        {
            end = totalRecords;
        }
        try
        {
            tablename = "transaction_common";

            fields  = tablename+".trackingid as transid,"+tablename+".toid,"+tablename+".description,"+tablename+".amount,"+tablename+".dtstamp,"+tablename+".emailaddr,"+tablename+".accountid,"+tablename+".timestamp,"+tablename+".status,"+tablename+".remark,"+tablename+".currency,"+tablename+".terminalid,"+tablename+".totype,ts.bankaccount";

            query.append(" SELECT DISTINCT "+ fields + " From "+ tablename +" join transaction_safexpay_details as ts on ts.trackingid = "+tablename+".trackingid where ");

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds       = Long.parseLong(fdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate        = formatter1.format(calendar.getTime());
                query.append(" "+tablename+".timestamp >= '" + startDate + "'");
            }
            else
            {
                query.append(" "+tablename+".dtstamp >= "+fdtstamp);
            }

            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds       = Long.parseLong(tdtstamp + "000");
                Calendar calendar       = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate          = formatter2.format(calendar.getTime());
                query.append(" and "+tablename+".timestamp <= '" + endDate + "'");
            }
            else
            {
                query.append(" and "+tablename+".dtstamp <= "+tdtstamp);
            }

            if(functions.isValueNull(merchantid))
            {
                query.append(" and "+tablename+".toid="+merchantid);
            }

            if (functions.isValueNull(desc))
            {
                query.append(" and "+tablename+".description like '" + desc + "%'");
            }
            if(accountid !=null && !accountid.equals("") && !accountid.equals( "null")){

                query.append(" and "+tablename+".accountid="+accountid);
            }

            if(functions.isValueNull(emailaddr))
            {
                query.append(" and "+tablename+".emailaddr="+emailaddr);
            }
            if(functions.isValueNull(bankaccount))
            {
                query.append(" and ts.bankaccount="+bankaccount);
            }
            if(functions.isValueNull(status))
            {
                query.append(" and "+tablename+".status=?");

            }else
            {
                query.append(" and "+tablename+".status in ('payoutsuccessful','payoutfailed','payoutstarted')");
            }
            if(functions.isValueNull(amount))
            {
                query.append(" and "+tablename+".amount="+amount);
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and "+tablename+".trackingid IN("+trackingid.toString()+")");
            }

            StringBuilder countquery    = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            query.append(" order by transid DESC");
            query.append(" limit ? , ? ");

            Date date4 = new Date();
            logger.error("listTransactionsNew pstmt starts ############" + date4.getTime());
            pstmt   = con.prepareStatement(query.toString());

            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                counter++;
            }

            pstmt.setInt(counter, start);
            counter++;
            pstmt.setInt(counter, end);

            hash    = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());

            logger.error("getTransactionListDetails pstmt:-----::::" + pstmt);

            hash.put("totalrecords", "" + totalRecords);
            hash.put("records", "0");

            if (totalRecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
        }
        catch (Exception e)
        {
            logger.error("Sql exception leaving transaction ######" + e);
            throw new SystemError(e.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        return hash;
    }

    public List<String> getListOfMerchantId(String desc, String tdtstamp, String fdtstamp, StringBuffer trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String gateway_name, String currency, String merchantid, String dateType, String firstName, String lastName, String emailAddr, String paymentId, String customerId, String statusflag, String issuingbank, String cardtype, String terminalid,String partnerName,String transactionMode)throws SystemError
    {
        logger.debug("Entering getListOfMerchantId for partner");
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (!Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        List<String> merchantlist= new ArrayList<>();
        String tablename    = "";
        String fields       = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me        = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp        = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp        = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter                 = 1;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename = "transaction_common";
            if (archive)
            {
                tablename = "transaction_common_archive";
            }

            fields = "DISTINCT t.toid as merchantids";
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" as t ");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingbank))
            {
                query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            }
            query.append("where t.trackingid>0 ");
            if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }
            if (functions.isValueNull(desc))
            {
                {
                    query.append(" AND t.description like '");
                    query.append(desc);
                    query.append("%'");
                }
            }
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= ?");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }

            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }
            if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }

            if (functions.isValueNull(issuingbank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddr))
            {
                query.append(" and t.emailAddr= ? ");
            }
            if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }
            if (terminalid != null && !terminalid.equals("") && !terminalid.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode=  ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount");
            }

            query.append(" order by transid DESC");
            logger.debug("query::::" + query.toString());
            pstmt = cn.prepareStatement(query.toString());

            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstmt.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                counter++;
            }

            else
            {
                pstmt.setString(counter, tdtstamp);
                counter++;
            }

            logger.debug("pstmt:::----------::" + pstmt);
            logger.debug("pstmt1::-----------:::" + pstmt1);
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                counter++;
            }
            if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }
            if (functions.isValueNull(issuingbank))
            {
                pstmt.setString(counter, issuingbank);
                counter++;
            }
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddr))
            {
                pstmt.setString(counter, emailAddr);
                counter++;
            }
            if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                counter++;
            }
            if (functions.isValueNull(terminalid))
            {
                pstmt.setString(counter, terminalid);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                counter++;
            }
            if (functions.isValueNull(transactionMode))
            {
                pstmt.setString(counter, transactionMode);
                counter++;
            }
            Date date4 = new Date();
            logger.error("getListOfMerchantId pstmt starts ############"+date4.getTime());
            rs    = pstmt.executeQuery();
            logger.error("getListOfMerchantId pstmt ends ############"+new Date().getTime());
            logger.error("getListOfMerchantId pstmt  diff ############"+(new Date().getTime()-date4.getTime()));
            logger.error("getListOfMerchantId pstmt :::::" + pstmt);
            while(rs.next())
            {
                merchantlist.add(rs.getString("merchantids"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception leaving listTransactions", se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        logger.debug("Leaving listTransactions");
        return merchantlist;
    }
}