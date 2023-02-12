package com.directi.pg;

import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.manager.vo.PartnerDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/19/14
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */public class Agent
    {
        static Logger logger = new Logger(Agent.class.getName());
        /*static String agent="agent";*/
        static final String ROLE="agent";
        public boolean isAgent(String login) throws SystemError
        {
            Connection conn = null;
            try
            {
                conn = Database.getConnection();
                logger.debug("check isMember method");
                String selquery = "select accountid from `user` where login = ? ";

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

        public AgentAuthenticate agentLoginAuthentication(long accid) throws SystemError
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
            String query = "select * from agents where accid =?  ";

            agentauthenticate.authenticate = "false";
            Connection conn = null;

            try
            {
                conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setLong(1,accid);
//            pstmt.setString(2,hashPassword);
//            pstmt.setString(3,hashPassword);
                ResultSet result = pstmt.executeQuery();


                if (result.next())
                {
                    fpasswd=result.getString("fpasswd");
                    passwd = result.getString("passwd");

                    agentauthenticate.agentid = result.getInt("agentId");
                    agentauthenticate.activation = result.getString("activation");
                    agentauthenticate.contactemails = result.getString("contact_emails");
                    agentauthenticate.address=result.getString("address");
                    agentauthenticate.agentname=result.getString("agentName");
                    agentauthenticate.template=result.getString("template");
                    agentauthenticate.telno=result.getString("telno");
                    agentauthenticate.logoname=result.getString("logoName");

                    /*if (passwd.equals(hashPassword))
                    {
                        agentauthenticate.authenticate = "true";

                    }*/

                    if (!"".equals(fpasswd))
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
        }


        public boolean agentchangePassword(String oldpwd, String newpwd, long accid, User user)
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
                /* String query2 = "select login from agents where agentId = ?";*/
                String query2 = "select login from `user` where accountid = ? and roles=?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setLong(1, accid);
                pstmt2.setString(2,ROLE);


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


            try
            {
                conn = Database.getConnection();
                PreparedStatement pstmt= conn.prepareStatement(updQuery);
//            pstmt.setString(1,hashNewPassword);
                pstmt.setLong(1,accid);
                pstmt.setString(2,login);
//            pstmt.setString(3,hashOldPassword);
//            pstmt.setString(4,hashOldPassword);
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

        public boolean agentForgotPassword(String login,long accid,User user) throws SystemError
        {
            Transaction transaction = new Transaction();
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
                try
                {

                    conn = Database.getConnection();
                    String query2 = "select hashedpasswd,accountid from `user` where login = ? and roles=? and accountid=?";
                    PreparedStatement pstmt2 = conn.prepareStatement(query2);
                    pstmt2.setString(1,login);
                    pstmt2.setString(2,ROLE);
                    pstmt2.setLong(3,accid);
                    ResultSet rs = pstmt2.executeQuery();
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

                    String query = "update agents set  fpasswd=?,fdtstamp=unix_timestamp(now()) where accid= ? and login=?";

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1,hashPassword);
                    pstmt.setString(2,accountid);
                    pstmt.setString(3,login);
                    int success = pstmt.executeUpdate();
                    if (success > 0)
                    {
                        ResultSet rs = null;
                        logger.debug("new temporary password has been set");



                        // send mail to member warning to use this password within 24 hr.


                        String query1 = "select contact_emails,agentId from agents where login= ? and accid= ?";

                        pstmt= conn.prepareStatement(query1);
                        pstmt.setString(1,login);
                        pstmt.setString(2,accountid);
                        rs = pstmt.executeQuery();
                        if (rs.next())
                        {
                            String emailAddr = rs.getString("contact_emails");
                            String agentId = rs.getString("agentId");
                            logger.debug("agentId======"+agentId);
                            logger.debug(fpasswd +"for"+login);
                            //MailService mailService=new MailService();
                            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                            HashMap mailValues=new HashMap();
                            mailValues.put(MailPlaceHolder.TOID,agentId);
                            mailValues.put(MailPlaceHolder.PASSWORD,fpasswd);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.AGENT_FORGOT_PASSWORD,mailValues);

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

        }
        public Hashtable listTransactionsNew(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive, Set<String> gatewayTypeSet, String accountid, String merchantid) throws SystemError
        {
            logger.debug("Entering listTransactions for agent");
            Connection cn=Database.getConnection();
            if(!Functions.isValidSQL(trackingid) || !Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
            {
                throw new SystemError("In valid data received");

            }

            // logger.debug("trackingid===1===//=in transactionENTRY"+trackingid);
            Hashtable hash = null;
            String tablename = "";
            String fields = "";
            String orderby = "";
            StringBuffer query = new StringBuffer();
            //Encoding for SQL Injection check

            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            status = ESAPI.encoder().encodeForSQL(me,status);
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

            try
            {
                Iterator i = gatewayTypeSet.iterator();
                while(i.hasNext())
                {
                    //tablename = "transaction_icicicredit";

                    tablename = Database.getTableName((String)i.next());
                    if (archive)
                    {
                        //tablename += "_archive";
                        tablename = "transaction_icicicredit_archive";
                    }
                    if(tablename.equals("transaction_icicicredit")||tablename.equals("transaction_icicicredit_archive"))
                    {
                        fields = "icicitransid as transid,toid,status,name,amount,captureamount,refundamount,description,dtstamp,paymodeid,cardtype,accountid";
                    }
                    else
                    {
                        fields = "trackingid as transid,toid,status,name,amount,captureamount,refundamount,description,dtstamp,paymodeid,cardtype,accountid";
                    }

                    query.append("select " + fields + " from " + tablename + " where");
                    query.append(" toid IN(" + merchantid + ")");
                    if (status != null)
                        query.append(" and status='" + status + "'");
                    if (fdtstamp != null)
                        query.append(" and dtstamp >= " + fdtstamp);
                    if (tdtstamp != null)
                        query.append(" and dtstamp <= " + tdtstamp);
                    if (desc != null)
                        query.append(" and description like '%" + desc + "%'");
                    if (accountid!=null && !accountid.equals("") && !accountid.equals("null"))
                        query.append(" and accountid =" + accountid);

                    if (trackingid != null)
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


                StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

                query.append(" order by dtstamp ,transid ASC");

                query.append(" limit " + start + "," + end);

                hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));

                ResultSet rs = Database.executeQuery(countquery.toString(), cn);

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
                Database.closeConnection(cn);
            }

            logger.debug("Leaving listTransactions");
            return hash;
        }

        public Hashtable<String, String> getAgentMemberDetails(String agentid)
        {   Hashtable<String, String> memberid = new Hashtable<String, String>();
            Connection con=null;
            try
            {
                con=Database.getConnection();
                String qry="select memberid,company_name from members where agentId=? ORDER BY memberid ASC";
                PreparedStatement pstmt= con.prepareStatement(qry);
                pstmt.setString(1,agentid);
                ResultSet rs=pstmt.executeQuery();

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
                Database.closeConnection(con);
            }
            return memberid;
        }
        public String getAgentMemberRS(String agentid)
        {
            ResultSet rs=null;
            Connection con=null;
            String sMemberList="";
            try
            {
                con=Database.getConnection();
                String qry="select memberid from members where agentId=?";
                PreparedStatement pstmt= con.prepareStatement(qry);
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
            finally {
                Database.closeConnection(con);
            }
            return sMemberList;
        }

        public Hashtable loadAgentMailValue(String memberid)throws SystemError
        {
            Connection con=null;
            PreparedStatement pstmt=null;
            ResultSet rs=null;
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
                    ResultSet rs1=pstmt.executeQuery();
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
                    Database.closeConnection(con);
                }
            }
            else
            {
                logger.error("Memberid is not provided");
            }
            return mailvalue;
        }
        public static void DeleteBoth(String login) throws SystemError
        {
            Connection con=null;
            try
            {
                con=Database.getConnection();
                String query=" delete from `user` where login=? and roles=?";
                PreparedStatement ps=con.prepareStatement(query.toString());
                ps.setString(1,login);
                ps.setString(2,ROLE);
                String dquery=" delete from `agents` where login=?";
                PreparedStatement ps1=con.prepareStatement(dquery.toString());
                ps1.setString(1,login);
            }
            catch (SystemError systemError)
            {
                /*systemError.printStackTrace(); */ //To change body of catch statement use File | Settings | File Templates.
                logger.error(" error ::", systemError);
                throw new SystemError("Error:"+systemError.getMessage());
            }
            catch (SQLException e)
            {
                /*e.printStackTrace();*/  //To change body of catch statement use File | Settings | File Templates.
                logger.error(" ERROR", e);
                throw new SystemError("Error:"+e.getMessage());
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
        //getting partnerDetail when sessionOut
        public static PartnerDetailsVO sessionout_getLogoAndCompanyName()
        {
            Connection con = null;
            Long accid = null;
            int csId = 0;
            PartnerDetailsVO partnerDetailsVO = null;
            try
            {
                con = Database.getConnection();
                PreparedStatement psmt = con.prepareStatement("SELECT par.logoName AS logo,par.partnerName AS companyName,par.partnerId AS partnerid FROM partners AS par JOIN agents AS agn ON agn.partnerId=par.partnerId WHERE agn.accid =(SELECT accountid FROM `user` WHERE roles=? ORDER BY timestmp DESC LIMIT 1)");
                psmt.setString(1, ROLE);
                ResultSet rs = psmt.executeQuery();
                if (rs.next())
                {
                    partnerDetailsVO = new PartnerDetailsVO();
                    partnerDetailsVO.setPartnerId(rs.getString("partnerid"));
                    partnerDetailsVO.setCompanyName(rs.getString("companyName"));
                    partnerDetailsVO.setLogoName(rs.getString("logo"));

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

            return partnerDetailsVO;
        }
    }
