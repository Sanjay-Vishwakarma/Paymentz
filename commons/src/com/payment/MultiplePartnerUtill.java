package com.payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Member;
import com.directi.pg.SystemError;
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
public class MultiplePartnerUtill
{
    public static final String ROLE_SUBPARTNER ="subpartner";
    static Logger log = new Logger(MultiplePartnerUtill.class.getName());

    public Hashtable selectMemberIdForDropDown()
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String query = "select memberid,login from members";
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
            conn = Database.getConnection();
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

    public HashMap getBinDetails(String memberid)
    {
        HashMap dataHash = null;
        Connection conn = null;
        ResultSet resultSet1 = null;

        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select bin,accountid,memberid from whitelist_bins where memberid=").append(memberid);
            StringBuffer countquery = new StringBuffer("select count(*) from whitelist_bins where memberid=").append(memberid);
            dataHash = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            resultSet1 = Database.executeQuery(countquery.toString(), conn);

            resultSet1.next();
            int totalrecords = resultSet1.getInt(1);
            dataHash.put("totalrecords", "" + totalrecords);
            if (totalrecords > 0)
            {
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
            Database.closeResultSet(resultSet1);
            Database.closeConnection(conn);
        }

        return dataHash;

    }
    public boolean isUniqueChildMember(String login)throws PZDBViolationException
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            log.debug("check isUniqueChildMember method");
            String selquery = "select partnerId from partner_users where login = ? ";

            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SQLException e)
        {
            log.error("Exception in isUniqueChildMember method: ", e);
            PZExceptionHandler.raiseDBViolationException("MultiplePartnerUtill.java","isUniqueChildMember()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SystemError e)
        {
            log.error("Exception in isUniqueChildMember method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultiplePartnerUtill.java","isUniqueChildMember()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    // Modification in order to differ role for partner and super partner user.
    public void addPartnerUser(Hashtable details) throws PZDBViolationException,PZTechnicalViolationException
    {
        Member mem = null;
        try
        {
            User user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), (String) details.get("role"));
            addNewPartnerUser(user.getAccountId(), details);

        }
        catch (AuthenticationException ae)
        {
            String message=ae.getLogMessage();
            if(message.contains("Duplicate"))
            {
                PZExceptionHandler.raiseTechnicalViolationException(MultipleMemberUtill.class.getName(),"addPartnerUser()",null,"common",ae.getMessage(), PZTechnicalExceptionEnum.DUPLICATE_USER_CREATION_EXCEPTION,null,ae.getMessage(),ae.getCause());
            }

            try
            {
                DeleteUserandPartnerUser((String) details.get("login"));
            }
            catch(Exception e)
            {
                log.error("Exception while Deleting the user while creation", e);
            }
            log.debug("AuthenticationException while add chlid member---"+ae);

            log.debug("AuthenticationException while add chlid member---"+ae);
            PZExceptionHandler.raiseTechnicalViolationException("MultiplePartnerUtill.java","addPartnerUser()",null,"common",ae.getMessage(), PZTechnicalExceptionEnum.ACCOUNT_AUTHENTICATION_EXCEPTION,null,ae.getMessage(),ae.getCause());
        }

        //System.out.println("userid---"+details.get("userid"));
        HashMap mailvalue=new HashMap();
        mailvalue.put(MailPlaceHolder.NAME,details.get("company_name"));
        mailvalue.put(MailPlaceHolder.USERNAME,details.get("login"));
        mailvalue.put(MailPlaceHolder.USERID,details.get("userid"));
        mailvalue.put(MailPlaceHolder.PARTNERID,details.get("partnerid"));
        mailvalue.put(MailPlaceHolder.TOID,details.get("partnerid"));
        //mailvalue.put(MailPlaceHolder.ADMINEMAIL,details.get("adminmailid"));
        //mailvalue.put(MailPlaceHolder.SUPPORTEMAIL,details.get("supportmailid"));
        //mailvalue.put(MailPlaceHolder.BILLINGEMAIL,details.get("billingemail"));
        //mailvalue.put(MailPlaceHolder.COMPANYFROMADDRESS,details.get("companyfromaddress"));
        //mailvalue.put(MailPlaceHolder.IPADDRESS,"127.0.0.1");
        //mailvalue.put(MailPlaceHolder.SUPPORTURL,details.get("supporturl"));
        mailvalue.put(MailPlaceHolder.CONTECTEMAIL,details.get("email"));
        mailvalue.put(MailPlaceHolder.FROM_TYPE,details.get("company"));

        log.debug("send mail to new method");
        //MailService mailService=new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_USER_REGISTRATION,mailvalue);
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

            StringBuffer sb = new StringBuffer();
            sb.append("INSERT INTO partner_user_account_mapping(memberid,userid,paymodeid,cardtypeid,accountid,terminalid) VALUES (");

            sb.append("" + userVO.getUserMerchantId() + ",");
            sb.append("" + userVO.getUserid() + ",");
            sb.append("" + userVO.getUserPaymodeId() + ",");
            sb.append("" + userVO.getUserCardTypeId() + ",");
            sb.append("" + userVO.getUserAccountId() + ",");
            sb.append("" + userVO.getUserTerminalId() + ")");

            i = Database.executeUpdate(sb.toString(), connection);

            log.debug("insert partner_users---"+sb.toString());
        }
        catch (SystemError e)
        {
            log.error("Exception in addNewPartnerUser method: ",e);
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
    public void addNewPartnerUser(long accid,Hashtable detailHash)throws PZDBViolationException
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection connection = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        int userId = 0;
        try
        {
            connection = Database.getConnection();
            /*StringBuffer sb = new StringBuffer();

            sb.append("insert into partner_users (partnerid,login,accid,contact_emails) values (");

            sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(detailHash.get("partnerid")))+ ",");
            sb.append("'" +ESAPI.encoder().encodeForSQL(me,String.valueOf(detailHash.get("login")))+ "',");
            sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(accid))+ ",");
            sb.append("'" +ESAPI.encoder().encodeForSQL(me,String.valueOf(detailHash.get("email")))+ "')");*/

            String query = "insert into partner_users (partnerid,login,accid,contact_emails,actionExecutorId,actionExecutorName) values (?,?,?,?,?,?)";

            p = connection.prepareStatement(query);
            p.setString(1,ESAPI.encoder().encodeForSQL(me, String.valueOf(detailHash.get("partnerid"))));
            p.setString(2, String.valueOf(detailHash.get("login")));
            p.setString(3,ESAPI.encoder().encodeForSQL(me, String.valueOf(accid)));
            p.setString(4, String.valueOf(detailHash.get("email")));

            p.setString(5, String.valueOf(detailHash.get("actionExecutorId")));
            p.setString(6,String.valueOf(detailHash.get("actionExecutorName")));

            int num = p.executeUpdate();
            if (num == 1)
            {
                rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        userId = rs.getInt(1);
                    }
                }
            }
            detailHash.put("userid",String.valueOf(userId));

            //Database.executeUpdate(sb.toString(), connection);
            log.debug("insert partner_users---"+p+"---"+userId);
        }
        catch (SystemError e)
        {
            log.error("Exception in addNewPartnerUser method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultiplePartnerUtill.java","addNewPartnerUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SQLException e)
        {
            log.error("Exception in addNewPartnerUser method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultiplePartnerUtill.java","addNewPartnerUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(connection);
        }
    }

    public  void DeleteUserandPartnerUser(String login) throws PZDBViolationException
    {
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String query="delete from `user` where login=? and roles=?";
            PreparedStatement ps=con.prepareStatement(query.toString());
            ps.setString(1,login);
            ps.setString(2,ROLE_SUBPARTNER);
            String dquery="delete from `member_users` where login=?";
            PreparedStatement ps1=con.prepareStatement(dquery.toString());
            ps1.setString(1,login);
        }
        catch (SQLException e)
        {
            log.error("Exception in DeleteUserandPartnerUser method: ", e);
            PZExceptionHandler.raiseDBViolationException("MultiplePartnerUtill.java","DeleteUserandPartnerUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SystemError e)
        {
            log.error("Exception in DeleteUserandPartnerUser method: ",e);
            PZExceptionHandler.raiseDBViolationException("MultiplePartnerUtill.java","DeleteUserandPartnerUser()",null,"common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


    public Hashtable getDetailsForSubpartner(String partnerid)
    {
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        ResultSet resultSet1 = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;

        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            //conn = Database.getRDBConnection();
            StringBuilder query = new StringBuilder( "select * from partner_users where partnerid IN ("+partnerid +")");
            StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM partner_users WHERE partnerid IN ("+partnerid +")");
            pstmt = conn.prepareStatement(query.toString());
            pstmt1 = conn.prepareStatement(countQuery.toString());
            dataHash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            resultSet1 = pstmt1.executeQuery();
            resultSet1.next();
            int totalrecords = resultSet1.getInt(1);
            dataHash.put("totalrecords", "" + totalrecords);
            if (totalrecords > 0)
                //dataHash.put("records", "" + (dataHash.size() - 2));
                dataHash.put("records", "" + (dataHash.size() - 1));
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

    public Hashtable viewDetailsForPartnerUserList(String login)
    {
        log.debug("inside viewDetailsForPartnerUserList");
        Hashtable dataHash = new Hashtable();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM partner_users WHERE login = ?";

            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,login);
            rs = preparedStatement.executeQuery();

            while(rs.next())
            {
                dataHash.put("partnerid",rs.getString("partnerid"));
                dataHash.put("login",rs.getString("login"));
                dataHash.put("emailaddress",rs.getString("contact_emails"));
                dataHash.put("actionExecutorId",rs.getString("actionExecutorId"));
                dataHash.put("actionExecutorName",rs.getString("actionExecutorName"));
            }
            log.debug("query viewDetailsForPartnerUserList"+preparedStatement);
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
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
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
            String query = "DELETE FROM partner_users_modules_mapping WHERE userid=? ";
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


    public int deleteDetailsForPartnerUserList(String login)
    {
        Hashtable dataHash = null;
        Connection connection = null;
        PreparedStatement ps = null, ps1 = null;

        int i = 0;
        int j = 0;
        try
        {
            connection = Database.getConnection();
            String query = "DELETE FROM partner_users WHERE login=? ";
            String query1 = "DELETE FROM user WHERE login=? ";
            ps = connection.prepareStatement(query);
            ps1 = connection.prepareStatement(query1);
            ps.setString(1, login);
            ps1.setString(1,login);
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
            Database.closePreparedStatement(ps);
            Database.closePreparedStatement(ps1);
            Database.closeConnection(connection);
        }
        return i;
    }

    public int updateDetailsForPartnerUserList(String login,Hashtable updateHash)
    {
        int i = 0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        try
        {
            connection = Database.getConnection();
            String query = "UPDATE partner_users SET contact_emails=? WHERE login=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, (String) updateHash.get("emailaddress"));
            pstmt.setString(2,login);
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
            Database.closePreparedStatement(pstmt);
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
            String query = "select * from partner_users where memberid =?";

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
            connection = Database.getConnection();
            String query = "select * from partner_user_account_mapping where memberid =? AND userid=?";

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
            connection = Database.getConnection();
            String query = "select * from partner_user_account_mapping where memberid =? AND userid=?";

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
            String query = "delete from partner_user_account_mapping where memberid=? AND userid=?";

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
    public boolean deleteBin(String id )
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String deleteQuery = "DELETE FROM whitelist_bins WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1,id);
            int i = preparedStatement.executeUpdate();
            if(i>0)
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            log.debug("Exception----------" + se);
        }
        catch (SQLException e)
        {
            log.debug("Exception................." + e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        log.debug("inside false");
        return false;
    }
    public boolean isBinRecordAvailableInSystem(String bin, String accountid, String memberid)throws PZDBViolationException
    {
        Connection conn = null;
        boolean isRecordAuthentic=false;
        PreparedStatement preparedStatement=null;
        ResultSet result=null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query =new StringBuffer("SELECT * FROM whitelist_bins WHERE bin=? AND accountid=? AND memberid=?");
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1,bin);
            preparedStatement.setString(2,accountid);
            preparedStatement.setString(3,memberid);
            result = preparedStatement.executeQuery();
            if(result.next()){
                isRecordAuthentic =true;
            }
        }
        catch (SQLException e){
            log.error("SQLException:::::", e);

        }
        catch (SystemError systemError){
            log.error("SystemError:::::",systemError);

        }
        finally
        {
            Database.closeResultSet(result);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isRecordAuthentic;
    }

    public boolean isMember(String login) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String selquery = "SELECT partnerId FROM partners WHERE login = ?";
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
            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean addBinDetails(String startBin,String endBin,String accountid,String memberid, String actionExecutorId,String actionExecutorName)
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String query = "insert into whitelist_bins (startBin,endBin,accountId,memberId,actionExecutorId,actionExecutorName) values (?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,startBin);
            preparedStatement.setString(2,endBin);
            preparedStatement.setString(3,accountid);
            preparedStatement.setString(4,memberid);
            preparedStatement.setString(5,actionExecutorId);
            preparedStatement.setString(6,actionExecutorName);
            int i = preparedStatement.executeUpdate();
            if(i>0)
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            log.debug("Exception----------" + se);
        }
        catch (SQLException e)
        {
            log.debug("Exception................." + e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return false;
    }
}
