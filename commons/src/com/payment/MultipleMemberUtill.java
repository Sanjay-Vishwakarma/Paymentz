package com.payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.vo.UserVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jinesh on 1/5/2016.
 */
public class MultipleMemberUtill
{
    public static final String ROLE ="sub_merchant";
    static Logger log = new Logger(MultipleMemberUtill.class.getName());
    Functions functions = new Functions();

    public Hashtable selectMemberIdForDropDown()
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String query = "select memberid,login from members ORDER BY memberid DESC ";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public Hashtable selectMemberIdForPartner(String partnerId)
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,login FROM members WHERE partnerId=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,partnerId);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }
    //new
    public LinkedHashMap selectMemberIdForPartners(String partnerId)
    {
        LinkedHashMap dataHash = new LinkedHashMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,login FROM members WHERE partnerId=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1,partnerId);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public TreeMap<String, String> getMemberid()
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,login FROM members ORDER BY memberid ASC";
            PreparedStatement p = conn.prepareStatement(query);
            log.debug("query::::"+p);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),String.valueOf(resultSet.getInt("memberid"))+"-"+resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public TreeMap<String, String> getPaymodeData()
    {
        TreeMap paymodedata= new TreeMap();
        Connection connection = null;

        try
        {
            connection= Database.getRDBConnection();
            String query= "Select paymodeid, paymentType from payment_type order by paymodeid asc";
            PreparedStatement pstmt= connection.prepareStatement(query);
            log.error("Query for paymode:: "+pstmt);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next())
            {
                paymodedata.put(String.valueOf(resultSet.getInt("paymodeid")),String.valueOf(resultSet.getInt("paymodeid"))+"-"+resultSet.getString("paymentType"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::: "+systemError.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SqlException:::: "+e.getMessage());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return  paymodedata;
    }
    public TreeMap<String, String> getActiveMemberid()
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
            PreparedStatement p = conn.prepareStatement(query);
            log.debug("Active memberid query::::"+p);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),String.valueOf(resultSet.getInt("memberid"))+"-"+resultSet.getString("company_name"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public TreeMap<String, String> getTerminalid(String memberid)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            PreparedStatement p = null;
            String query = "";
            if(memberid.equals(null) || memberid.equals(""))
            {
                query = "SELECT terminalid,paymodeid,cardtypeid,memberid,isActive FROM member_account_mapping ORDER BY terminalid";
                p = conn.prepareStatement(query);
            }
            else
            {
                query = "SELECT terminalid,paymodeid,cardtypeid,memberid,isActive FROM member_account_mapping where memberid = ? ORDER BY terminalid";
                p = conn.prepareStatement(query);
                p.setInt(1, Integer.parseInt(memberid));
            }
            log.debug("Terminal query::::"+p);
            ResultSet resultSet = p.executeQuery();

            String isActive="";
            String paymode = "";
            while(resultSet.next())
            {
                isActive = resultSet.getString("isActive").equals("Y")?"Active":"InActive" ;
                paymode = GatewayAccountService.getCardType(resultSet.getString("cardtypeid")) == null ? "-" : GatewayAccountService.getCardType(resultSet.getString("cardtypeid"));
                dataHash.put(resultSet.getString("terminalid"), resultSet.getString("terminalid")+"-"+ GatewayAccountService.getPaymentMode(resultSet.getString("paymodeid"))+"-"+paymode+"-"+isActive);
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public boolean isMemberTerminalidMapped(String memberid, String terminalid)
    {
        String mappedmemberid = "";
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            PreparedStatement p = null;
            String query = "SELECT memberid FROM member_account_mapping where terminalid = ?";
            p = conn.prepareStatement(query);
            p.setString(1, terminalid);
            ResultSet resultSet = p.executeQuery();
            if(resultSet.next())
            {
                mappedmemberid =  resultSet.getString("memberid");
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        if (mappedmemberid.equals(memberid))
        {
            return true;
        }
        return false;
    }

    public TreeMap<String, String> getRuleName(String memberid,String terminalid)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            PreparedStatement p = null;
            String query = "";
            if (functions.isValueNull(memberid) && functions.isValueNull(terminalid))
            {
                query = "SELECT mampm.monitoing_para_id,mpm.monitoing_para_name,terminalid FROM member_account_monitoringpara_mapping AS mampm JOIN monitoring_parameter_master AS mpm ON mampm.monitoing_para_id=mpm.monitoing_para_id WHERE memberid= ? AND terminalid= ? ";
                p = conn.prepareStatement(query);
                p.setString(1, memberid);
                p.setString(2, terminalid);

            }
            log.debug("rule name query::::"+p);
            ResultSet resultSet = p.executeQuery();
            while(resultSet.next())
            {
                dataHash.put(resultSet.getString("monitoing_para_id"), resultSet.getString("monitoing_para_name"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
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
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public TreeMap<String, String> getAgentMemberDetails(String agentid)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer();
            query.append("SELECT mam.mappingid,mam.memberid,mam.agentid,a.agentName AS agentname,m.company_name AS merchantname FROM merchant_agent_mapping AS mam JOIN agents AS a ON mam.agentid=a.agentid JOIN members AS m ON mam.memberid=m.memberid");
            if (functions.isValueNull(agentid))
            {
                query.append(" where mam.agentid='"+agentid+"'");
            }
            query.append(" ORDER BY mam.memberid ASC");
            PreparedStatement p = conn.prepareStatement(query.toString());
            ResultSet resultSet = p.executeQuery();
            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),String.valueOf(resultSet.getInt("memberid"))+"-"+ resultSet.getString("merchantname"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public TreeMap<String, String> getMemberAccountDetails(String agentId)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer();
            query.append("SELECT DISTINCT g.accountid,g.merchantid FROM gateway_accounts AS g JOIN member_account_mapping AS m JOIN merchant_agent_mapping AS mg WHERE m.memberid=mg.memberid AND g.`accountid`=m.`accountid`");
            if (functions.isValueNull(agentId))
            {
                query.append(" AND mg.agentid='"+agentId+"'");
            }
            query.append(" ORDER BY mg.agentid ASC");
            PreparedStatement p = conn.prepareStatement(query.toString());
            log.error("Query:::"+p);
            ResultSet resultSet = p.executeQuery();
            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("accountid")),String.valueOf(resultSet.getInt("accountid"))+"-"+ resultSet.getString("merchantid"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    //new
    public LinkedHashMap selectMemberId()
    {
        LinkedHashMap dataHash = new LinkedHashMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,login FROM members ORDER BY memberid ASC";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet resultSet = p.executeQuery();

            while(resultSet.next())
            {
                dataHash.put(String.valueOf(resultSet.getInt("memberid")),resultSet.getString("login"));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public boolean isUniqueChildMember(String login)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt= null;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            log.debug("check isUniqueChildMember method");
            String selquery = "select memberid from member_users where login = ? ";

            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SQLException e)
        {
            log.error("Exception in isUniqueChildMember method: ", e);
            PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","isUniqueChildMember()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SystemError e)
        {
            log.error("Exception in isUniqueChildMember method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","isUniqueChildMember()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public void addMerchantUser(Hashtable details) throws PZDBViolationException,PZTechnicalViolationException
    {
        Member mem = null;
        try
        {
            User user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "submerchant");
            addNewMerchantUser(user.getAccountId(), details);

        }
        catch (AuthenticationException ae)
        {
            String message=ae.getLogMessage();
            if(message.contains("Duplicate"))
            {
                PZExceptionHandler.raiseTechnicalViolationException(MultipleMemberUtill.class.getName(),"addMerchantUser()",null,"common",ae.getMessage(), PZTechnicalExceptionEnum.DUPLICATE_USER_CREATION_EXCEPTION,null,ae.getMessage(),ae.getCause());
            }

            try
            {
                deleteDetailsForMemberUserList((String) details.get("login"));
            }
            catch(Exception e)
            {
                log.error("Exception while Deleting the user while creation", e);
            }
            log.debug("AuthenticationException while add chlid member---"+ae);
            PZExceptionHandler.raiseTechnicalViolationException(MultipleMemberUtill.class.getName(),"addMerchantUser()",null,"common",ae.getMessage(), PZTechnicalExceptionEnum.ACCOUNT_AUTHENTICATION_EXCEPTION,null,ae.getMessage(),ae.getCause());
        }

        //MailService mailService=new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap merchantSignupMail=new HashMap();
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        merchantSignupMail.put(MailPlaceHolder.USERNAME,details.get("login"));
        merchantSignupMail.put(MailPlaceHolder.NAME,details.get("contact_persons"));
        merchantSignupMail.put(MailPlaceHolder.TOID,details.get("merchantid"));
        merchantSignupMail.put(MailPlaceHolder.USERID,details.get("userid"));
        merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
        merchantSignupMail.put(MailPlaceHolder.PARTNERID, details.get("partnerid"));
        merchantSignupMail.put(MailPlaceHolder.FROM_TYPE,details.get("company"));
        asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_USER_REGISTRATION,merchantSignupMail);

    }

    public int addUserTerminal(UserVO userVO)
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection connection = null;
        int i = 0;
        int j = 0;
        try
        {
            connection = Database.getConnection();

            //String query = "delete from member_user_account_mapping where memberid="+userVO.getUserMerchantId()+" and userid="+userVO.getUserid();
            /*PreparedStatement ps=connection.prepareStatement(query.toString());
            ps.setString(1,userVO.getUserMerchantId());*/
            /*ps.setString(2,userVO.getUserid());
            ps.setString(3,userVO.getUserPaymodeId());
            ps.setString(4,userVO.getUserCardTypeId());
            ps.setString(5,userVO.getUserTerminalId());*/
            /*j = Database.executeUpdate(query,connection);
            log.debug("delkete query---"+j);*/

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO member_user_account_mapping(memberid,userid,paymodeid,cardtypeid,accountid,terminalid) VALUES (");

            sb.append("" + userVO.getUserMerchantId() + ",");
            sb.append("" + userVO.getUserid() + ",");
            sb.append("" + userVO.getUserPaymodeId() + ",");
            sb.append("" + userVO.getUserCardTypeId() + ",");
            sb.append("" + userVO.getUserAccountId() + ",");
            sb.append("" + userVO.getUserTerminalId() + ")");

            i = Database.executeUpdate(sb.toString(), connection);

            log.debug("insert member_users---"+sb.toString());
        }
        catch (SystemError e)
        {
            log.error("Exception in addNewMerchantUser method: ",e);
            //PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","addNewMerchantUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        /*catch (SQLException e)
        {
            log.error("Exception in addNewMerchantUser method: ",e);
            //PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","addNewMerchantUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }*/
        finally
        {
            Database.closeConnection(connection);
        }
        return i;
    }
    public void addNewMerchantUser(long accid,Hashtable detailHash)throws PZDBViolationException
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection connection = null;
        int userId = 0;
        try
        {
            connection = Database.getConnection();
            //StringBuffer sb = new StringBuffer();

            String query = "insert into member_users (memberid,login,accid,contact_emails,actionExecutorId,actionExecutorName,telno,telcc) values (?,?,?,?,?,?,?,?)";

            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1,ESAPI.encoder().encodeForSQL(me, String.valueOf(detailHash.get("merchantid"))));
            p.setString(2,String.valueOf(detailHash.get("login")));
            p.setString(3,ESAPI.encoder().encodeForSQL(me, String.valueOf(accid)));
            p.setString(4, String.valueOf(detailHash.get("email")));
            p.setString(5, String.valueOf(detailHash.get("actionExecutorId")));
            p.setString(6,String.valueOf(detailHash.get("actionExecutorName")));
            p.setString(7, String.valueOf(detailHash.get("telno")));
            p.setString(8, String.valueOf(detailHash.get("telnocc")));


            /*sb.append("insert into member_users (memberid,login,accid,contact_emails) values (");

            sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(detailHash.get("merchantid")))+ ",");
            sb.append("'" +ESAPI.encoder().encodeForSQL(me,String.valueOf(detailHash.get("login")))+ "',");
            sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(accid))+ ",");
            sb.append("'" +ESAPI.encoder().encodeForSQL(me,String.valueOf(detailHash.get("email")))+ "')");

            int num = Database.executeUpdate(sb.toString(), connection);*/
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        userId = rs.getInt(1);
                    }
                }
            }
            detailHash.put("userid",String.valueOf(userId));
        }
        catch (SystemError e)
        {
            log.error("Exception in addNewMerchantUser method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","addNewMerchantUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SQLException e)
        {
            log.error("Exception in addNewMerchantUser method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","addNewMerchantUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public  void DeleteUserandMemberUser(String login) throws PZDBViolationException
    {
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String query="delete from `user` where login=? and roles=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,login);
            ps.setString(2,ROLE);
            String dquery="delete from `member_users` where login=?";
            PreparedStatement ps1=con.prepareStatement(dquery);
            ps1.setString(1,login);
        }
        catch (SQLException e)
        {
            log.error("Exception in DeleteUserandMemberUser method: ", e);
            PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","DeleteUserandMemberUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SystemError e)
        {
            log.error("Exception in DeleteUserandMemberUser method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultipleMemberUtill.java","DeleteUserandMemberUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


    public Hashtable getDetailsForSubmerchant(String memberid)
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        ResultSet resultSet1=null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;

        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("select * from member_users where memberid =?");
            StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM member_users WHERE memberid=?");

            pstmt = conn.prepareStatement(query.toString());
            pstmt1 = conn.prepareStatement(countQuery.toString());

            pstmt.setString(1, memberid);
            pstmt1.setString(1, memberid);

            dataHash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            resultSet1 = pstmt1.executeQuery();

            resultSet1.next();
            int totalrecords = resultSet1.getInt(1);
            dataHash.put("totalrecords", "" + totalrecords);
            if (totalrecords > 0)
            {
                //dataHash.put("records", "" + (dataHash.size() - 2));
                dataHash.put("records", "" + (dataHash.size() - 1));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving GetDetailsForSubmerchant ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeResultSet(resultSet1);
            Database.closeConnection(conn);
        }

        return dataHash;

    }

    public Hashtable getDetailsForSubmerchantUser(String memberid, String username)
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        ResultSet resultSet1=null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;

        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder("select * from member_users where memberid =? and login=?");
            StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM member_users WHERE memberid=? and login=?");

            pstmt = conn.prepareStatement(query.toString());
            pstmt1 = conn.prepareStatement(countQuery.toString());

            pstmt.setString(1, memberid);
            pstmt.setString(2, username);
            pstmt1.setString(1, memberid);
            pstmt1.setString(2, username);

            dataHash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            resultSet1 = pstmt1.executeQuery();

            resultSet1.next();
            int totalrecords = resultSet1.getInt(1);
            dataHash.put("totalrecords", "" + totalrecords);
            if (totalrecords > 0)
            {
                //dataHash.put("records", "" + (dataHash.size() - 2));
                dataHash.put("records", "" + (dataHash.size() - 1));
            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving GetDetailsForSubmerchant ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeResultSet(resultSet1);
            Database.closeConnection(conn);
        }

        return dataHash;

    }

    public Hashtable viewDetailsForMemberUserList(String login)
    {
        log.debug("inside viewDetailsForMemberUserList");
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            //conn = Database.getRDBConnection();
            String query = "SELECT * FROM member_users WHERE login = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,login);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next())
            {
                dataHash.put("memberid",rs.getString("memberid"));
                dataHash.put("login",rs.getString("login"));
                dataHash.put("emailaddress",rs.getString("contact_emails"));
                if(functions.isValueNull(rs.getString("telno"))){
                    dataHash.put("telno",rs.getString("telno"));
                }
                if(functions.isValueNull(rs.getString("telcc"))){
                    dataHash.put("telnocc",rs.getString("telcc"));
                }
               /* dataHash.put("telno",rs.getString("telno"));
                dataHash.put("telnocc",rs.getString("telcc"));*/
                dataHash.put("actionExecutorId",rs.getString("actionExecutorId"));
                dataHash.put("actionExecutorName",rs.getString("actionExecutorName"));
            }
            log.debug("query viewDetailsForMemberUserList"+preparedStatement);
        }
        catch (SystemError e)
        {
            log.error("System error", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException",e);
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return dataHash;
    }

    public int deleteUserwithModuleMapping(String userid)
    {
        Hashtable dataHash = null;
        Connection connection = null;
        int i = 0;
        try
        {
            connection = Database.getConnection();
            String query = "DELETE FROM merchant_users_modules_mapping WHERE userid=? ";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, userid);
            i = ps.executeUpdate();

        }
        catch (SystemError e)
        {
            log.error("System error", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }

        finally
        {
            Database.closeConnection(connection);
        }
        return i;
    }


    public int deleteDetailsForMemberUserList(String login)
    {
        Hashtable dataHash = null;
        Connection connection = null;

        String ROLE="submerchant";

        int i = 0;
        int j = 0;
        try
        {
            connection = Database.getConnection();
            String query = "DELETE FROM member_users WHERE login=? ";
            String query1 = "DELETE FROM user WHERE login=? and roles=?";
            PreparedStatement ps = connection.prepareStatement(query);
            PreparedStatement ps1 = connection.prepareStatement(query1);
            ps.setString(1, login);
            ps1.setString(1,login);
            ps1.setString(2,ROLE);
            i = ps.executeUpdate();
            j = ps1.executeUpdate();

        }
        catch (SystemError e)
        {
            log.error("System error", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }

        finally
        {
            Database.closeConnection(connection);
        }
        return i;
    }

    public int updateDetailsForMemberUserList(String login,Hashtable updateHash)
    {
        int i = 0;
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "UPDATE member_users SET contact_emails=?,telno=?,telcc=? WHERE login=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, (String) updateHash.get("emailaddress"));
            pstmt.setString(2, (String) updateHash.get("telno"));
            pstmt.setString(3, (String) updateHash.get("telnocc"));
            pstmt.setString(4,login);
            i = pstmt.executeUpdate();

            log.debug("update query===="+pstmt);

        }
        catch (SystemError e)
        {
            log.error("System error", e);
        }
        catch (SQLException e)
        {
            log.error("SQLException", e);
        }

        finally
        {
            Database.closeConnection(connection);
        }
        return i;
    }

    public List<UserVO> getUserList(String memberid)
    {
        List<UserVO> userVOList = new ArrayList<UserVO>();
        UserVO userVO = null;
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "select * from member_users where memberid =?";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, memberid);
            log.debug("query---"+pstmt);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                userVO = new UserVO();
                userVO.setUserid(rs.getString("userid"));
                userVO.setUserLogin(rs.getString("login"));
                userVOList.add(userVO);
            }
        }
        catch (SystemError se)
        {
            log.error("system error---",se);
        }
        catch (SQLException e)
        {
            log.error("SQLException---",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return userVOList;

    }

    public List<UserVO> getUserTerminalList(String memberid,String userid)
    {
        List<UserVO> userVOList = new ArrayList<UserVO>();
        UserVO userVO = null;
        Connection connection = null;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "select * from member_user_account_mapping where memberid =? AND userid=?";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, memberid);
            pstmt.setString(2, userid);
            log.debug("query---"+pstmt);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                userVO = new UserVO();
                userVO.setUserid(rs.getString("userid"));
                userVO.setUserTerminalId(rs.getString("terminalid"));
                userVO.setUserMerchantId(rs.getString("memberid"));
                userVOList.add(userVO);
            }
        }
        catch (SystemError se)
        {
            log.error("system error---",se);
        }
        catch (SQLException e)
        {
            log.error("SQLException---",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return userVOList;

    }
    public Set<String> getUserTerminalSet(String memberid,String userid)
    {
        Set<String> stringSet=new HashSet<String>();
        UserVO userVO = null;
        Connection connection = null;
        try
        {
            //connection = Database.getConnection();
            connection = Database.getRDBConnection();
            String query = "select * from member_user_account_mapping where memberid =? AND userid=?";

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, memberid);
            pstmt.setString(2, userid);
            log.debug("query---"+pstmt);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                stringSet.add(rs.getString("terminalid"));
            }
        }
        catch (SystemError se)
        {
            log.error("system error---",se);
        }
        catch (SQLException e)
        {
            log.error("SQLException---",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return stringSet;

    }

    public int deleteSelectedTerminal(UserVO userVO)
    {
        Connection connection = null;
        int i = 0;
        try
        {
            connection = Database.getConnection();
            String query = "delete from member_user_account_mapping where memberid=? AND userid=?";

            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, userVO.getUserMerchantId());
            ps.setString(2, userVO.getUserid());

            i = ps.executeUpdate();
            log.debug("delete terminal---"+ps+"-"+i);
        }
        catch (SystemError se)
        {
            log.error("System Error---", se);
        }
        catch (SQLException e)
        {
            log.error("System Error---",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return i;
    }
    public TreeMap<String, String> getTerminalidMember(String memberid)
    {
        TreeMap dataHash = new TreeMap();
        Connection conn = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            PreparedStatement p = null;
            String query = "";
            if(memberid.equals(null) || memberid.equals(""))
            {
                query = "SELECT mam.terminalid,m.memberid,mam.cardtypeid,mam.paymodeid,mam.isActive FROM member_account_mapping AS mam JOIN members AS m ON m.memberid = mam.memberid  ORDER BY mam.terminalid ASC";
                p = conn.prepareStatement(query);
            }
            else
            {
                query = "SELECT mam.terminalid,m.memberid,mam.cardtypeid,mam.paymodeid,mam.isActive FROM member_account_mapping AS mam JOIN members AS m ON m.memberid = mam.memberid WHERE m.memberid =? ORDER BY mam.terminalid ASC";
                p = conn.prepareStatement(query);
                p.setInt(1, Integer.parseInt(memberid));
            }
            log.debug("Terminal query::::"+p);
            ResultSet resultSet = p.executeQuery();

            String isActive="";
            String paymode = "";
            while(resultSet.next())
            {
                isActive = resultSet.getString("isActive").equals("Y")?"Active":"InActive" ;
                paymode = GatewayAccountService.getCardType(resultSet.getString("cardtypeid")) == null ? "-" : GatewayAccountService.getCardType(resultSet.getString("cardtypeid"));
                dataHash.put(resultSet.getString("terminalid"), resultSet.getString("terminalid")+"-"+ GatewayAccountService.getPaymentMode(resultSet.getString("paymodeid"))+"-"+paymode+"-"+isActive);

            }
        }
        catch (SQLException se)
        {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            log.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return dataHash;
    }

    public TreeMap<String,String> getCardTypeDetail(){
        TreeMap cardtypeListhash = new TreeMap();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn= Database.getRDBConnection();
            String query = "select cardtypeid, cardType from card_type ORDER BY cardtypeid ASC";
            log.error("query for cardType List::: "+ query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                cardtypeListhash.put(String.valueOf(rs.getInt("cardtypeid")),String.valueOf(rs.getInt("cardtypeid"))+"-"+rs.getString("cardType"));
            }
        }catch (SQLException se) {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se) {
            log.error("System Error ", se);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }

        return cardtypeListhash;
    }

    public TreeMap<String,String> getGatewayNameList()
    {
        TreeMap gatewayNameList = new TreeMap();
        Connection conn         = null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        try{
            conn         = Database.getRDBConnection();
            String query = "SELECT gt.gateway FROM payout_amount_limit AS p JOIN `gateway_accounts` AS g ON p.accountid = g.accountid JOIN `gateway_type` AS gt ON g.pgtypeid = gt.pgtypeid where p.accountid > 0  ORDER BY gt.gateway ASC";
            log.error("query for gateway Name List::: "+ query);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()){
                gatewayNameList.put(rs.getString("gateway"), rs.getString("gateway"));
            }
        }catch (SQLException se) {
            log.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se) {
            log.error("System Error ", se);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }

        return gatewayNameList;
    }


}