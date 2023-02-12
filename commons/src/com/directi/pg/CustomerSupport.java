package com.directi.pg;

import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.*;
import java.util.Date;

public  class CustomerSupport
{
    /* static String support="support";*/
    static final String ROLE="support";
    public static Hashtable hash = null;
    static Logger logger = new Logger("logger1");
    static String classname=CustomerSupport.class.getName();
    private static Date cal;
    private static long ms;
    public static void refresh() throws SystemError
    {
        hash=new Hashtable();
        Hashtable hash1 = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("Entering Refresh");
            String query = "select accid,csId,csName,csLogin,csPassword,csEmail,csContactNumber,csCreationDate,csLastLogin from `customersupport`";


            ResultSet result = Database.executeQuery(query, conn);

            while (result.next())
            {
                hash1.put(result.getString("accid"), result.getString("csId"));
            }



            logger.info(classname+"::CustomerSupport reloaded");
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
        logger.debug(classname+"::Leaving refresh");

    }

    public static boolean isCustomerSuppEx(String login) throws SystemError
    {
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            logger.debug(classname+"::check isCustomerSupportEx method");
            String selquery = "select accountid from `user` where login = ? and roles =?";

            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            pstmt.setString(2,ROLE);
            //            pstmt.setString(2,support.toString());
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

    public static customerSupportExecutive addnew_customerSupport(long accid,Hashtable details) throws SystemError
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("insert into `customersupport`(accid,csName,csLogin,csEmail,csContactNumber,csCreationDate) values(");
        sb.append(" " + ESAPI.encoder().encodeForSQL(me, String.valueOf(accid)) + "");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("csName")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("csLogin")) + "'");
        //        try
        //        {
        ////            sb.append(",'" + ESAPI.authenticator().hashPassword((String) details.get("csPassword"),(String) details.get("csLogin")) + "'");
        //        }
        //        catch (Exception e)
        //        {
        //            logger.error("Error!", e);
        //            throw new SystemError("Error: " + e.getMessage());
        //        }

        // sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("company_type")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("csEmail"))+ "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("csContactNumber")) + "'");
        sb.append(",CURRENT_TIMESTAMP)");
        logger.debug("Add CustomerSupport");
        logger.debug(sb.toString());
        customerSupportExecutive cse=new customerSupportExecutive();
        Connection con=null;
        try{
            con= Database.getConnection();
            Database.executeUpdate(sb.toString(), con);
            String selquery = "select csId from `customersupport` where accid=? and csLogin=?";
            PreparedStatement smnt=con.prepareStatement(selquery);
            smnt.setLong(1, accid);
            smnt.setString(2,details.get("csLogin").toString());
            ResultSet rs=smnt.executeQuery();
            if(rs.next())
            {
                cse.csId=rs.getInt(1);
                cse.csEmail=details.get("csEmail").toString();
                cse.csContactNumber=details.get("csContactNumber").toString();
            }
            refresh();
        }catch (SystemError se)
        {
            logger.error("System error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        } finally
        {
            Database.closeConnection(con);
        }
        return cse;
    }
    public static customerSupportExecutive authenticate(String login,User user) throws SystemError
    {
        String fpasswd = null;
        /*String passwd = null;

        String hashPassword = null ;
        try
        {
            hashPassword = ESAPI.authenticator().hashPassword(password,login) ;
        }
        catch(Exception e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }*/
        customerSupportExecutive cse = new customerSupportExecutive();

        logger.debug(classname+"::check username and password in database::customersupport");
        String query = "select csId,csPassword,csEmail,csContactNumber,csCreationDate from `customersupport` where csLogin=?  and accid=?";

        cse.csauthenticate = "false";
        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            logger.debug(query);
            pstmt.setString(1,login);
            pstmt.setLong(2,user.getAccountId());

            ResultSet result = pstmt.executeQuery();


            if (result.next())
            {
                /* passwd = result.getString(4);*/
                fpasswd = result.getString(2);

                cse.csId =result.getInt(1);
                //                cse.csactivation = result.getString("activation");
                //                cse.=result.getString("partnerId");
                //                if ((result.getString("isservice")).equalsIgnoreCase("Y"))
                //                {
                //                    mem.isservice = true;
                //                }
                /*   if (passwd.equals(hashPassword))
                {
                    cse.csauthenticate = "true";
                }*/
                if (!"".equals(fpasswd))
                {
                    cse.csauthenticate = "forgot";
                }

                //                mem.address = result.getString("address") + ", " + result.getString("city") + " - " + result.getString("zip") + "  " + result.getString("country");

                cse.csContactNumber = result.getString(4);
                cse.csEmail = result.getString(3);
                cse.csLoginDate=result.getString(5);
                //                cse.setAccountId(result.getString("accountid"));
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
        return cse;
    }
    public static Hashtable getCustomerSupportDetails(int csId)
    {
        Hashtable detailhash=new Hashtable();
        Connection con=null;
        try{
            con= Database.getConnection();
            PreparedStatement psmt=con.prepareStatement("select csId,csName,csEmail,csContactNumber,csCreationDate from `customersupport` where csId=? ");
            psmt.setInt(1,csId);
            ResultSet rs=psmt.executeQuery();
            if(rs.next())
            {
                detailhash.put("csId",rs.getInt(1));
                detailhash.put("csName",rs.getString(2));
                detailhash.put("csEmail",rs.getString(3));
                detailhash.put("csContactNumber",rs.getString(4));
                detailhash.put("csCreationDate",rs.getDate(5));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError---->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SQLException e)
        {
            logger.error("SQLException---->", e);  //To change body of catch statement use File | Settings | File Templates.
        }finally
        {
            Database.closeConnection(con);
        }
        return detailhash;

    }
    public static boolean isLoggedIn(HttpSession sess)
    {
        if (sess.getAttribute("csId") == null || sess.getAttribute("password") == null || sess.getAttribute("username") == null)

            return false;
        else
            return true;
    }
    public static boolean changePassword(String oldPass, String newPass, long accid, User user)
    {
        logger.debug(classname+"Updating new password in Database");
        int i = 0;
        Connection conn = null;
        String hashNewPassword = null ;
        String hashOldPassword = null ;
        String csLogin = null;
        //Getting login name from member table
        try
        {
            conn = Database.getConnection();
            String query2 = "select login from `user` where accountid = ? and roles=?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setLong(1,accid);
            pstmt2.setString(2,ROLE);

            ResultSet rs = pstmt2.executeQuery();
            if (rs.next())
            {
                csLogin = rs.getString("login");
            }

        }
        catch (SystemError se)
        {
            logger.error(classname+"System error occure::::",se);
            return false;
        }
        catch (Exception e)
        {
            logger.error(classname+"Getting Exception in changepassword method ::::", e);
            return false;
        }
        finally
        {
            Database.closeConnection(conn);
        }


        // Changing password in user table

        try
        {
            ESAPI.authenticator().changePassword(user,oldPass,newPass,newPass);
        }
        catch(Exception e)
        {
            logger.error(classname+"Change password throwing Authentication Exception ",e);
            return false;
        }


        // getting hash password
//            try
//            {
//                hashNewPassword = ESAPI.authenticator().hashPassword(newPass,csLogin) ;
//                hashOldPassword = ESAPI.authenticator().hashPassword(oldPass,csLogin) ;
//            }
//            catch(Exception e)
//            {
//                logger.error("Getting Excrption in changepassword method while doing hashing::::", e);
//            }


        String updQuery = "update `customersupport` set csPassword='' where accid = ? and csLogin=?";


        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(updQuery);
            /*pstmt.setString(1,hashNewPassword);*/
            /*pstmt.setInt(2,csId.intValue());*/
            /*pstmt.setString(3,hashOldPassword);*/
            pstmt.setLong(1,accid);
            pstmt.setString(2,csLogin);
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

    public static int updateCSE(int csid, long LoginDate, String ipAddress) throws SQLException
    {
        Connection con = null;
        int rss = 0;
        //        int csid = 0;
        try
        {
            con = Database.getConnection();
            //            PreparedStatement ps1=con.prepareStatement("select csId from `customersupport` where csLogin=?");
            //            ps1.setString(1,csLogin);
            //            ResultSet rs=ps1.executeQuery();
            //            if(rs.next())
            //            {
            //               csid=rs.getInt(1);
            //            }
            PreparedStatement ps = con.prepareStatement("insert into customersupportlogindetails(csId,csLoginDetails,csIPaddress) VALUES(?,?,?) ");
            ps.setInt(1, csid);
            Timestamp sqldate = new Timestamp(LoginDate);
            //            logintimestamp=sqldate;
            ps.setTimestamp(2, sqldate);
            ps.setString(3, ipAddress);
            rss = ps.executeUpdate();
            logger.debug(classname + " login details inserted successfully::no of rows affected " + rss);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError--->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }

        return rss;
    }

    public static long getDate()
    {
        cal = Calendar.getInstance().getTime();
        ms = cal.getTime();
        return ms;
    }

    public static void updateLastLogin(long loginDate, int csid) throws SQLException
    {
        Connection con = null;
        Timestamp sqldate = null;
        int rss = 0;
        try
        {
            con = Database.getConnection();
            sqldate = new Timestamp(loginDate);
            PreparedStatement ps = con.prepareStatement("update customersupport set csLastLogin=? where csId=?");
            logger.debug("updated last Login" + sqldate);
            ps.setTimestamp(1, sqldate);
            ps.setInt(2, csid);
            rss = ps.executeUpdate();
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError--->",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }

        logger.debug(classname + " Last login details in Customersupport::no of rows affected " + rss + " where csId " + csid + " changed Last login date" + sqldate);
    }

    public static void updateLogout(int csid) throws SQLException
    {
        Connection con = null;
        //        long login=Long.parseLong(logintime);
        Timestamp sqldate = null, csLoginDetails = null;
        //        Timestamp logintimestamp=null;
        int rss = 0;
        try
        {
            con= Database.getConnection();
            long LoginDate = getDate();
            sqldate = new Timestamp(LoginDate);
            //            logintimestamp=new Timestamp(logintime);
            PreparedStatement psmt1 = con.prepareStatement("Select Max(csLoginDetails) from customersupportlogindetails where csId=?");
            psmt1.setInt(1, csid);
            ResultSet rs = psmt1.executeQuery();
            if (rs.next())
            {
                csLoginDetails = rs.getTimestamp(1);
            }
            PreparedStatement psmt = con.prepareStatement("update customersupportlogindetails set csLogoutDetails=? where csId=? and csLoginDetails =?");
            psmt.setTimestamp(1, sqldate);
            psmt.setInt(2, csid);
            psmt.setTimestamp(3, csLoginDetails);
            rss = psmt.executeUpdate();
        }
        catch (SystemError systemError)
        {
            logger.error(" System error::", systemError); //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }

        if (rss != 0)
            logger.debug("logoutDetails edited successfully::" + sqldate + " no of rows Affected::" + rss);
        else
            logger.debug("logoutDetails edited unsuccessfully");
        //log.debug("logout successfully");
    }

    public static void sessionout_updateLogout()
    {
        Connection con = null;
        Long accid = null;
        int csId = 0;
        try
        {
            con = Database.getConnection();
            PreparedStatement psmt = con.prepareStatement("SELECT accountid,login,timestmp FROM `user` WHERE roles=? ORDER BY timestmp DESC LIMIT 1");
            psmt.setString(1, ROLE);
            ResultSet rs = psmt.executeQuery();
            if (rs.next())
            {
                accid = rs.getLong(1);
                csId = customerSupportId(accid);
                if (csId > 0)
                {
                    updateLogout(csId);
                }
                else
                {
                    throw new SystemError("ERROR : while registering logout info");
                }
            }
            else
            {
                throw new SystemError("ERROR : while registering logout info");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("logout uopdate exception::", systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SQLException e)
        {

            logger.error("logout uopdate SQL exception::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }


    }

    public static Hashtable validateMandatoryParameters(HttpServletRequest req, List<InputFields> inputFieldsListMandatory)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListMandatory, errorList, false);
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListMandatory)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(), errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    public static Hashtable validateOptionalParameters(HttpServletRequest req, List<InputFields> inputFieldsListOptional)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);

        if(!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(), errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    public static Hashtable validateMandatoryParameters(Hashtable<InputFields, String> inputFieldsListMandatory)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(inputFieldsListMandatory, errorList, false);
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListMandatory.keySet())
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(), errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    public static int customerSupportId(long accid) throws SystemError
    {
        Connection conn = null;
        ResultSet rs;
        try
        {

            conn = Database.getConnection();
            logger.debug(classname + "::check isCustomerSupportEx method");
            String selquery = "select csId from `customersupport` where accid= ? ";

            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setLong(1, accid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return rs.getInt(1);
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in customersupportId: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in customersupportid: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return 0;
    }

    public static boolean checkOldPass(long accid, String oldpwd) throws SystemError
    {
        Connection con = null;
        ResultSet rs, rs1;
        try
        {
            con = Database.getConnection();
            String checkquery = "select login from `user` where accountid=? and roles=?";
            PreparedStatement ps = con.prepareStatement(checkquery);
            ps.setLong(1, accid);
            ps.setString(2, ROLE);
            rs = ps.executeQuery();
            if (rs.next())
            {
                String csLogin = rs.getString(1);
                String HashedPassword = ESAPI.authenticator().hashPassword(oldpwd, csLogin);
                String checkquery1 = "Select * from `user` where login=? and hashedpasswd=? and roles=?";
                PreparedStatement ps1 = con.prepareStatement(checkquery1);
                ps1.setString(1, csLogin);
                ps1.setString(2, HashedPassword);
                ps1.setString(3, ROLE);
                rs1 = ps1.executeQuery();
                if (rs1.next())
                {
                    return true;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in customersupportId: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMember method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return false;
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

    public static boolean forgotPassword(String login, User user) throws SystemError
    {
        //MailService mailService = new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        boolean flag = false;
        if (isCustomerSuppEx(login))
        {

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            String hashPassword = null;
            String oldPasshash = null;
            String accountid = null;
            try
            {
                hashPassword = ESAPI.authenticator().hashPassword(fpasswd, login);
            }
            catch (Exception e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }

            Connection conn = null;
            //Getting login name from member table
            try
            {
                conn = Database.getConnection();
                String query2 = "select hashedpasswd,accountid from `user` where login = ? and roles=? ";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1, login);
                pstmt2.setString(2, ROLE);
                ResultSet rs = pstmt2.executeQuery();
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


            try
            {
                conn = Database.getConnection();

                String query = "update `customersupport` set csPassword= ? where csLogin= ? and accid=?";
//                    String csId=hash.get(accountid).toString();
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, hashPassword);
                pstmt.setString(2, login);
                pstmt.setString(3, accountid);
                int success = pstmt.executeUpdate();
                if (success > 0)
                {
                    ResultSet rs = null;
                    logger.debug("new temporary password has been set");


                    // send mail to member warning to use this password within 24 hr.


                    String query1 = "select csEmail,csId from `customersupport` where csLogin= ? and accid=?";

                    pstmt = conn.prepareStatement(query1);
                    pstmt.setString(1, login);
                    pstmt.setString(2, accountid);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String emailAddr = rs.getString(1);
                        logger.debug(classname + " transaction Track email id::" + emailAddr);

                        HashMap mailValues = new HashMap();
                        mailValues.put(MailPlaceHolder.TOID, rs.getString("csId"));
                        mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                        //mailService.sendMail(MailEventEnum.ADMIN_CUSTOMERSUPPORT_FORGOT_PASSWORD, mailValues);
                        asynchronousMailService.sendMerchantSignup(MailEventEnum.ADMIN_CUSTOMERSUPPORT_FORGOT_PASSWORD, mailValues);
                        flag = true;
                    }
                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating Customer support table.");
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

    }// end forgotPassword

    public static Hashtable CSElist(String csId, String csName, int pageno, int records) throws SystemError
    {
        Connection con = null;
        Statement stmt;
        ResultSet rs;
        int start = 0; // start index
        int end = 0; // end index

        start = (pageno - 1) * records;
        end = records;

        Hashtable csehash = null;
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select csId,csName from customersupport where csId>0");
        if (csId != null)
            query.append(" and csId=" + ESAPI.encoder().encodeForSQL(me, csId.toString()));
        if (csName != null)
            query.append(" and csName LIKE '%" + ESAPI.encoder().encodeForSQL(me, csName.toString()) + "%'");
        try{
            con= Database.getConnection();
            stmt = con.createStatement();
            String countquery = "select count(*) from (" + query.toString() + ") as temp";
            query.append(" ORDER BY csId DESC LIMIT " + start + "," + end + "");
            csehash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), con));

            rs = Database.executeQuery(countquery.toString(), con);
            if(rs.next())
            {
                csehash.put("totalrecords", rs.getInt(1));
            }
            else
            {
                csehash.put("totalrecords", 0);
            }
            csehash.put("records", csehash.size() - 1);

        }
        catch (SystemError e)
        {
            logger.error("Exception CSElist: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        catch (SQLException e)
        {
            logger.error("SQLException--->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }
        return csehash;
    }

    public static Hashtable CSEinfo(String csId, int pageno, int records) throws SystemError
    {
        Connection con = null;
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select cs.csId,cs.csName,cs.csLogin,cs.csEmail,cs.csContactNumber,cs.csCreationDate,cast(cs.csLastLogin as char) as csLastLogin,csd.csLoginDetails,cast(csd.csLogoutDetails as char)as csLogoutDetails,csd.csIPaddress from `customersupport` as cs left join `customersupportlogindetails` as csd on cs.csId=csd.csId where cs.csId= ?");
        String countquery = "select count(*) from(" + query.toString() + ") as temp";
        int start = 0; // start index
        int end = 0; // end index

        start = (pageno - 1) * records;
        end = records;
        Hashtable CSEinfo = null;
        try
        {
            con = Database.getConnection();
            PreparedStatement ps1 = con.prepareStatement(countquery.toString());
            ps1.setString(1, csId);
            ResultSet rs = ps1.executeQuery();
            query.append(" order by csd.csLoginDetails desc Limit " + start + "," + end + " ");
            PreparedStatement ps = con.prepareStatement(query.toString());
            ps.setString(1, csId);
            CSEinfo = Database.getHashFromResultSet(ps.executeQuery());
            if (rs.next())
            {
                CSEinfo.put("totalrecords", rs.getInt(1));
            }
            else
            {
                CSEinfo.put("totalrecords", 0);
            }
            CSEinfo.put("records", CSEinfo.size() - 1);
        }
        catch (SystemError e)
        {
            logger.error("Exception CSElist: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        catch (SQLException e)
        {
            logger.error(classname + " sql exception::", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }
        return CSEinfo;
    }

    public static boolean adminUpdateCSE(int csId, String csEmail, String csContactNumber) throws SystemError
    {
        Connection con = null;
        boolean flag = false;
        String query = "update `customersupport` set  csEmail=? ,csContactNumber=? where csId=?";
        try
        {
            con = Database.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString());
            ps.setString(1, csEmail);
            ps.setString(2, csContactNumber);
            ps.setInt(3, csId);
            int rss = ps.executeUpdate();
            if (rss > 0)
            {
                flag = true;
            }
            else
            {
                flag = false;
            }
        }
        catch (SystemError systemError)
        {
            logger.error(classname + " except System error", systemError); //To change body of catch statement use File | Settings | File Templates.
            throw new SystemError("Error:" + systemError.getMessage());
        }
        catch (SQLException e)
        {
            logger.error(classname + " sql exception::", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }
        return flag;
    }

    public static Hashtable callerList(String trackingid, String toid, String fdstamp, String tdstamp, int pageno, int records) throws SystemError
    {
        Connection con=null;
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Hashtable callList = null;
        Timestamp fdate = new Timestamp(Long.parseLong(fdstamp));
        Timestamp tdate = new Timestamp(Long.parseLong(tdstamp));
        logger.debug(classname + " fdate::" + fdate + " tdate::" + tdate);
        String[] tablename = {"transaction_common", "transaction_qwipi", "transaction_ecore"};
        StringBuffer fquery = new StringBuffer("");
        int start = 0; // start index
        int end = 0; // end index

        start = (pageno - 1) * records;
        end = records;
        for (int i = 0; i <= 2; i++)
        {
            StringBuffer query = new StringBuffer(" select mem.memberid as memberid,mem.company_name as merchant,tran.trackingid as trackingid,tran.name as rname,ci.name as cname,ci.emailaddr as email,ci.phoneno as phoneno,ci.description as descr,ci.remark as remarks,ci.status as statuss,ci.lastcalldate as dates from caller_info as ci  join " + tablename[i] + " as tran on ci.trackingid=tran.trackingid  join members as mem on mem.memberid=tran.toid where ci.lastcalldate>=\"" + fdate + "\" and ci.lastcalldate<=\"" + tdate + "\"");

            if (trackingid != null)
            {
                query.append(" and ci.trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingid));
            }
            if (toid != null)
            {
                query.append(" and tran.toid=" + ESAPI.encoder().encodeForSQL(me, toid));
            }
            if ((i + 1 < (tablename.length)) && tablename[i + 1] != null)
            {
                query.append(" UNION");
            }
            fquery.append(query.toString());
        }
        if (trackingid == null && toid == null)
        {
            fquery.append(" union select NULL,NULL,NULL,NULL,ci.name as cname,ci.emailaddr as email,ci.phoneno as phoneno,ci.description as descr,ci.remark as remarks,ci.status as statuss,ci.lastcalldate as dates from caller_info as ci where ci.lastcalldate>=\"" + fdate + "\" and ci.lastcalldate<=\"" + tdate + "\" and ci.trackingid is Null");
        }
        StringBuffer countquery = new StringBuffer(" select count(*) from (" + fquery.toString() + ") as temp");
        fquery.append(" Limit " + start + "," + end + " ");
        logger.debug(classname + " QUERY FULL FORMAT::" + fquery.toString() + " countquery::" + countquery.toString());
        try
        {
            con= Database.getConnection();
            Statement stmt = con.createStatement();
            Statement stmt1 = con.createStatement();
            callList = Database.getHashFromResultSet(stmt1.executeQuery(fquery.toString()));
            ResultSet rs = Database.executeQuery(countquery.toString(), con);
            if(rs.next())
            {
                callList.put("totalrecords", rs.getInt(1));
            }
            else
            {
                callList.put("totalrecords", 0);
            }
            callList.put("records", callList.size() - 1);
        }
        catch (SystemError systemError)
        {
            logger.error(classname + " system Error::", systemError);
            throw new SystemError();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SQLException e)
        {
            logger.error(classname + " Sql exception::", e); //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(con);
        }
        return callList;
    }

    public static Hashtable selectMemberIdForDropDown()
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String query = "select memberid,login from members";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")), resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            logger.error(classname + "SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error(classname + "System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public static void DeleteBoth(String login) throws SystemError
    {
        Connection con=null;
        try
        {
            con= Database.getConnection();
            String query = " delete from `user` where login=? and roles=?";
            PreparedStatement ps=con.prepareStatement(query.toString());
            ps.setString(1, login);
            ps.setString(2, ROLE);
            String dquery = " delete from `customersupport` where csLogin=?";
            PreparedStatement ps1 = con.prepareStatement(dquery.toString());
            ps1.setString(1, login);
        }
        catch (SystemError systemError)
        {
            /*systemError.printStackTrace(); */ //To change body of catch statement use File | Settings | File Templates.
            logger.error(classname + " error ::", systemError);
            throw new SystemError("Error:"+systemError.getMessage());
        }
        catch (SQLException e)
        {
            /*e.printStackTrace();*/  //To change body of catch statement use File | Settings | File Templates.
            logger.error(classname + " ERROR", e);
            throw new SystemError("Error:" + e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public HashMap listTransactions(String trackingid, String name, String amount, String firstsixofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, Set tableName, String pod, String podbatch, String desc, String orderdesc, String perfectmatch, int pageno, int records) throws SQLException
    {
        Connection con=null;
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer fquery = new StringBuffer();
        StringBuffer fquery1 = new StringBuffer();
        //System.out.println("TABLE NAME"+tableName );
        Iterator j = tableName.iterator();
        logger.debug(classname + " table to be scanned" + tableName);
        String[] tablename = new String[tableName.size()];
        boolean check = false, flag = false;
        StringBuffer query = null, countquery = null, query1 = null;
        HashMap hmout = new HashMap();
        Statement stmt = null;
        ResultSet rs = null, rs1 = null, rs2 = null;
        int i = 0;
        int k = 0;
        int start = (pageno - 1) * records;
        int end = records;

        logger.debug("start::" + start + " end::" + end);
        //String[] table=tableName.split(",");
        //for(String tables:table)
        //{

        // }
        // System.out.println("INSIDE list transaction table name"+tableName);
        while (j.hasNext())
        {
            tablename[k] = (String) j.next();
            logger.debug(classname + " table fetched for search " + tablename[k]);
            ++k;
        }
        for (int l = 0; l < tablename.length; l++)
        {     //System.out.println("TABLE sssss"+tablename[l]+"position"+l);
            boolean table = true;
            if (tablename[l] != null)
            {
                //System.out.println("TABLE SEGREGATED"+tablename[l]+"position"+l);
                String[] datarow = null;
                String[] datarows = tablename[l].split("_");
                if (l + 1 < tablename.length)
                {
                    datarow = tablename[l + 1].split("_");
                }
                if ("details".equals(datarows[datarows.length - 1]))
                {
                    logger.debug(classname + " details table scanned with table name::" + tablename[l]);
                    //System.out.println("TABLE DETAILS===="+tablename[l]+"position"+l);
                    query = new StringBuffer("select trans.action,trans.status,trans.timestamp from " + tablename[l] + " as trans where trans.trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingid) + " ");
                    check = true;
                    if ((l + 1 < (tablename.length)) && tablename[l + 1] != null && "details".equals(datarow[datarow.length - 1]))
                    {
                        table = false;
                        logger.debug(classname + " union applied on " + tablename[l] + " & " + tablename[l + 1] + "  table::");
                        query.append(" UNION ");
                    }

                    fquery.append(query.toString());
                }
                else
                {
                    boolean isdata = false;
                    logger.debug(classname + " scannin table::" + tablename[l]);
                    query1 = new StringBuffer("select from_unixtime(trans.dtstamp) as datest ,trans.trackingid as Trackingid, trans.name as Name,trans.description as Description,bi.first_six as Firstsixccno,bi.last_four as lastfourccno,trans.amount as Amount,trans.captureamount as CaptureAmount,trans.refundamount as RefundAmount , trans.status as Status,mem.company_name as Companyname,mem.sitename as Sitename, trans.podbatch as Podid , trans.pod as Pods,trans.orderdescription from  " + tablename[l] + " as trans,bin_details as bi,members as mem where trans.trackingid=bi.icicitransid and trans.toid=mem.memberid ");
                    if (status != null && !status.equals(""))
                    {
                        query1.append(" and trans.status='" + ESAPI.encoder().encodeForSQL(me, status) + "'");
                        logger.debug(classname + " status entered::" + status);
                    }

                    if (amount != null)
                    {
                       /* Float amm= new Float(amount);*/
                        query1.append(" and trans.amount='" + ESAPI.encoder().encodeForSQL(me, amount) + "'");
                        logger.debug(classname + " entered amount::" + amount);
                    }

                    if (emailaddr != null)
                    {
                        query1.append(" and bi.emailaddr='" + ESAPI.encoder().encodeForSQL(me, emailaddr) + "'");
                        logger.debug(classname + " entered email::" + emailaddr);
                    }
                    if (firstsixofccnum != null)
                    {
                        query1.append(" and bi.first_six =" + ESAPI.encoder().encodeForSQL(me, firstsixofccnum));
                        logger.debug(classname + " firstsixcccno entered::" + firstsixofccnum);
                    }
                    if (lastfourofccnum != null)
                    {
                        query1.append(" and bi.last_four =" + ESAPI.encoder().encodeForSQL(me, lastfourofccnum));
                        logger.debug(classname + " Lastfourccno entered::" + lastfourofccnum);
                    }

                    if (name != null)
                    {
                        if (perfectmatch == null)
                        {
                            query1.append(" and trans.name like '%" + ESAPI.encoder().encodeForSQL(me, name) + "%'");
                            //countquery1.append(" and description like '%" + desc + "%'");
                            logger.debug(classname + " name entered::" + name + " perfect match::" + perfectmatch);
                        }
                        else
                        {
                            query1.append(" and trans.name='" + ESAPI.encoder().encodeForSQL(me, name) + "'");
                            logger.debug(classname + " name entered::" + name + " perfect match::" + perfectmatch);
                        }
                    }

                    if (fdtstamp != null)
                    {
                        query1.append(" and trans.dtstamp >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
                        logger.debug(classname + " fdtstamp entered::" + fdtstamp);
                    }
                    if (tdtstamp != null)
                    {
                        query1.append(" and trans.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
                        logger.debug(classname + " tdtstamp entered::" + tdtstamp);
                    }
                    if (trackingid != null)
                    {
                        query1.append(" and trans.trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingid));
                        logger.debug(classname + " tracking entered::" + trackingid);
                    }
                    if (pod != null)
                    {
                        query1.append(" and trans.pod='" + ESAPI.encoder().encodeForSQL(me, pod) + "'");
                        logger.debug(classname + " pod entered::" + pod);
                    }
                    if (podbatch != null)
                    {
                        query1.append(" and trans.podbatch='" + ESAPI.encoder().encodeForSQL(me, podbatch) + "'");
                        logger.debug(classname + " podbatch entered::" + podbatch);
                    }
                    if (desc != null)
                    {
                        if (perfectmatch == null)
                        {
                            query1.append(" and trans.description like '%" + ESAPI.encoder().encodeForSQL(me, desc) + "%'");
                            //countquery1.append(" and description like '%" + desc + "%'");
                            logger.debug(classname + " description entered::" + desc + " perfect match::" + perfectmatch);
                        }
                        else
                        {
                            query1.append(" and trans.description='" + ESAPI.encoder().encodeForSQL(me, desc) + "'");
                            //countquery1.append(" and description='" + desc + "'");
                            logger.debug(classname + " description entered::" + desc + " perfect match::" + perfectmatch);
                        }
                    }
                    if (orderdesc != null)
                    {
                        if (perfectmatch == null)
                        {
                            query1.append(" and trans.orderdescription like '%" + ESAPI.encoder().encodeForSQL(me, orderdesc) + "%'");
                            //countquery1.append(" and orderdescription like '%" + orderdesc + "%'");
                            logger.debug(classname + " ORDERdescription entered::" + orderdesc + " perfect match::" + perfectmatch);
                        }
                        else
                        {
                            query1.append(" and trans.orderdescription='" + ESAPI.encoder().encodeForSQL(me, orderdesc) + "'");
                            //countquery1.append(" and orderdescription='" + orderdesc + "'");
                            logger.debug(classname + " ORDERdescription entered::" + orderdesc + " perfect match::" + perfectmatch);
                        }
                    }
                    // query1.append(" order by datest DESC,Trackingid ");

                    if ((l + 1 < (tablename.length)) && tablename[l + 1] != null && !"details".equals(datarow[datarow.length - 1]))
                    {
                        table = false;
                        logger.debug(classname + " union applied on " + tablename[l] + " & " + tablename[l + 1] + "  table's");
                        // System.out.println("next contains: "+tablename[l+1] );
                        query1.append(" UNION ");
                    }
                    fquery1.append(query1.toString());
                }
                if (check && table)
                {
                    if ((l + 1 < (tablename.length)) && tablename[l + 1] != null && !"details".equals(datarow[datarow.length - 1]))
                    {
                        logger.debug(classname + " union applied on " + tablename[l] + " & " + tablename[l + 1] + "  table's");
                        // System.out.println("next contains: "+tablename[l+1] );
                        fquery1.append(" UNION ");
                    }
                    if ((l + 1 < (tablename.length)) && tablename[l + 1] != null && "details".equals(datarow[datarow.length - 1]))
                    {

                        logger.debug(classname + " union applied on " + tablename[l] + " & " + tablename[l + 1] + "  table's");
                        // System.out.println("next contains: "+tablename[l+1] );
                        fquery.append(" UNION ");
                    }
                }
            }
        }
        countquery = new StringBuffer("select count(*) from (" + fquery1.toString() + ") as temp");
        fquery1.append("  limit " + start + "," + end);

        //System.out.print("fquery " +fquery.toString());
        try
        {
            con = Database.getConnection();
            stmt = con.createStatement();
            logger.debug(classname + " countquery::" + countquery.toString());
            rs1 = Database.executeQuery(countquery.toString(), con);

            int totalrecords = 0;
            logger.debug(classname + " QUERY:::" + fquery.toString());

            HashMap hm;
            if (check)
            {
                flag = true;
                rs = stmt.executeQuery(fquery.toString());
                logger.debug(classname + " result set for action history on particular tracking id");
                while (rs.next())
                {
                    flag = false;
                    hm = new HashMap();
                    hmout.put("true", check);
                    hm.put("Action", rs.getString(1));
                    hm.put("AStatus", rs.getString(2));
                    hm.put("TimeStamp", rs.getString(3));
                    hmout.put("A" + i, hm);
                    i++;
                }
                if (flag)
                {
                    return hmout;
                }
            }
            rs2 = stmt.executeQuery(fquery1.toString());
            logger.debug(classname + " result set for all tracking id for particular search criteria");
            logger.debug(classname + " fquer1:::" + fquery1.toString());
            while (rs2.next())
            {
                hm = new HashMap();
                hm.put("Date", rs2.getDate(1));
                hm.put("Trackingid", rs2.getInt(2));
                hm.put("Name", rs2.getString(3));
                hm.put("desc", rs2.getString(4));
                hm.put("Firstsixccno", rs2.getInt(5));
                hm.put("Lastfourccno", rs2.getInt(6));
                hm.put("Amount", rs2.getFloat(7));
                hm.put("CaptureAmount", rs2.getFloat(8));
                hm.put("RefundAmount", rs2.getFloat(9));
                hm.put("Status", rs2.getString(10));
                hm.put("MerchantName", rs2.getString(11));
                hm.put("SiteName", rs2.getString(12));
                if (rs2.getString(13) == null)
                {
                    logger.debug("for tracking id::" + rs2.getInt(2) + " podbatch::" + podbatch);
                    hm.put("Podbatch", "<i><b>N.A</b></i>");
                }
                else
                {
                    logger.debug("for tracking id::" + rs2.getInt(2) + " podbatch::" + podbatch);
                    hm.put("Podbatch", rs2.getString(13));
                }
                if (rs2.getString(14) == null)
                {
                    logger.debug("for tracking id::" + rs2.getInt(2) + " pod::" + pod);
                    hm.put("Pod", "<i><b>N.A</b></i>");
                }
                else
                {
                    logger.debug("for tracking id::" + rs2.getInt(2) + " pod::" + pod);
                    hm.put("Pod", rs2.getString(14));
                }
                hm.put("orderdesc", rs2.getString(15));
                if (rs1.next())
                {
                    totalrecords = rs1.getInt(1);
                    hmout.put("totalrecords", totalrecords);
                }
                else
                {
                    hmout.put("totalrecords", totalrecords);
                }
                hmout.put(i, hm);
                i++;
            }


        }
        catch (Exception e)
        {
            logger.error(classname + " sql exception while listing transaction::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        hmout.put("records", hmout.size() - 2);
        return hmout;
    }

    public boolean updatecaller(Hashtable query) throws SQLException
    {
        int rss = 0, i = 1;
        Connection con = null;
        StringBuffer insert, values, fquery;
        String pscount = "";
        insert = new StringBuffer(" insert into `caller_info`(");
        values = new StringBuffer("values(");
        Enumeration enu = query.keys();
        String key = "";
        String value = "";
        while (enu.hasMoreElements())
        {
            key = (String) enu.nextElement();
            value = (String) query.get(key);
            pscount = pscount + "" + value;
            insert.append(key);
            values.append("?");
            if (i + 1 <= query.size())
            {
                values.append(",");
                insert.append(",");
                pscount = pscount + ";";
            }
            i++;
        }
        insert.append(")");
        values.append(")");
        try
        {

            con = Database.getConnection();
            fquery = new StringBuffer(insert + " " + values);
            logger.debug(classname + " query::" + fquery.toString());
            PreparedStatement ps = con.prepareStatement(fquery.toString());
            String[] pscon = pscount.split(";");
            logger.debug(classname + " pscon::" + pscon);
            for (i = 0; i < pscon.length; i++)
            {
                ps.setString(i + 1, pscon[i]);
                logger.debug(classname + " ps::" + i + ",value::" + pscon[i]);

            }
            rss = ps.executeUpdate();
            logger.debug(classname + " no of rows updated in caller_info" + rss);
        }
        catch (SystemError e)
        {
            logger.error(classname + " system error::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        if (rss > 0)
            return true;
        else
            return false;

    }

    public int updateShipment(Integer trackingid, String pod, String podbatch, String status) throws SQLException
    {
        Connection con=null;
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int rss = 0;
        try
        {
            con= Database.getConnection();
            Statement stmt = con.createStatement();
            StringBuffer query;
            StringBuffer fquery;
            ResultSet rs;
            if (pod == null && podbatch == null)
            {
                logger.debug(classname + " updating only status of shippingdetails");
                if (status == "")
                {
                    fquery = new StringBuffer("select * from shippingdetails where trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    rs = stmt.executeQuery(fquery.toString());
                    if (rs.next())
                    {
                        fquery = new StringBuffer("update `shippingdetails` as ship set ship.status = null where ship.trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "'");
                    }
                    else
                    {
                        fquery = new StringBuffer("insert into `shippingdetails`(`trackingid`) values('" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "')");
                    }
                    rss = stmt.executeUpdate(fquery.toString());
                    logger.debug(classname + " update shippingdetails no of rows affected without status::" + rss);
                }
                else
                {
                    fquery = new StringBuffer("select * from shippingdetails where trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    rs = stmt.executeQuery(fquery.toString());
                    if (rs.next())
                    {
                        fquery = new StringBuffer("update `shippingdetails` as ship set ship.status='" + ESAPI.encoder().encodeForSQL(me, status) + "' where ship.trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    }
                    else
                    {
                        fquery = new StringBuffer("insert into `shippingdetails` values('" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "','" + ESAPI.encoder().encodeForSQL(me, status) + "')");
                    }
                    rss = stmt.executeUpdate(fquery.toString());
                    logger.debug(classname + " update shippingdetails no of rows affected with status::" + rss);
                }
            }
            else
            {
                String updateTableName = null;
                String tableName[] = {"transaction_common", "transaction_ecore", "transaction_qwipi"};
                for (int i = 0; i < tableName.length; i++)
                {
                    query = new StringBuffer("Select * from `" + tableName[i] + "` where trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    logger.debug(classname + " table scanned::" + tableName[i]);
                    rs = stmt.executeQuery(query.toString());
                    if (rs.next())
                    {
                        updateTableName = tableName[i];
                        logger.debug(classname + "Updating table" + updateTableName + " out of table scanned" + tableName);
                    }
                }

                fquery = new StringBuffer("update " + updateTableName + " as tran set  tran.pod ='" + ESAPI.encoder().encodeForSQL(me, pod) + "', tran.podbatch='" + ESAPI.encoder().encodeForSQL(me, podbatch) + "' where tran.trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                rss = stmt.executeUpdate(fquery.toString());
                if (status == "")
                {
                    logger.debug(classname + " updating pod,podbatch");
                    fquery = new StringBuffer("select * from shippingdetails where trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "'");
                    rs = stmt.executeQuery(fquery.toString());
                    if (rs.next())
                    {
                        fquery = new StringBuffer("update `shippingdetails` as ship set ship.status='" + ESAPI.encoder().encodeForSQL(me, status) + "' where ship.trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    }
                    else
                    {
                        fquery = new StringBuffer("insert into `shippingdetails`(`trackingid`) values('" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "')");
                    }
                    rss = stmt.executeUpdate(fquery.toString());
                    logger.debug(classname + " no of rows affected in table shippingdetails without status::" + rss);
                }
                else
                {
                    logger.debug(classname + " updating pod,podbatch & status");
                    fquery = new StringBuffer("select * from shippingdetails where trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    rs = stmt.executeQuery(fquery.toString());
                    if (rs.next())
                    {
                        fquery = new StringBuffer("update `shippingdetails` as ship set ship.status='" + ESAPI.encoder().encodeForSQL(me, status) + "' where ship.trackingid='" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "' ");
                    }
                    else
                    {
                        fquery = new StringBuffer("insert into `shippingdetails` values('" + ESAPI.encoder().encodeForSQL(me, trackingid.toString()) + "','" + ESAPI.encoder().encodeForSQL(me, status) + "')");
                    }
                    rss = stmt.executeUpdate(fquery.toString());
                    logger.debug(classname + " no of rows affected in table shippingdetails with status::" + rss);
                }
            }
        }
        catch (SystemError e)
        {
            logger.error(classname + " system Error::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return rss;

    }

}
