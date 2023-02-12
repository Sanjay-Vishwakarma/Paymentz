package com.manager.dao;

import com.directi.pg.*;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.AdminDetailsVO;
import com.manager.vo.PaginationVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/11/15
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminDAO
{
    private Logger logger=new Logger(AdminDAO.class.getName());
    Functions functions=new Functions();

    public List<AdminDetailsVO> getAdminUsersList(AdminDetailsVO adminDetailsInputVO,PaginationVO paginationVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        Functions function=new Functions();
        AdminDetailsVO adminDetailsVO=null;
        List<AdminDetailsVO> adminDetailsVOList=new ArrayList<AdminDetailsVO>();
        try
        {
            conn = Database.getRDBConnection();

            StringBuilder query=new StringBuilder("SELECT adminid,login,contact_emails,FROM_UNIXTIME(fdtstamp) AS signuptime,isActive FROM admin WHERE adminid>0");
            StringBuffer countQuery = new StringBuffer("SELECT COUNT(*) FROM admin WHERE adminid>0");

            if(function.isValueNull(adminDetailsInputVO.getAdminId()))
            {
                query.append(" and adminid=" + adminDetailsInputVO.getAdminId());
                countQuery.append(" and adminid=" + adminDetailsInputVO.getAdminId());
            }
            if(function.isValueNull(adminDetailsInputVO.getLogin()))
            {
                query.append(" and login='" + adminDetailsInputVO.getLogin() + "'");
                countQuery.append(" and login='" + adminDetailsInputVO.getLogin() + "'");
            }

            query.append(" order by adminid desc LIMIT " + paginationVO.getStart() + "," + paginationVO.getEnd());
            pstmt= conn.prepareStatement(query.toString());
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                adminDetailsVO=new AdminDetailsVO();
                adminDetailsVO.setAdminId(rs.getString("adminid"));
                adminDetailsVO.setLogin(rs.getString("login"));
                adminDetailsVO.setContactEmails(rs.getString("contact_emails"));
                adminDetailsVO.setSignUpTime(rs.getString("signuptime"));
                adminDetailsVO.setIsActive(rs.getString("isActive"));
                adminDetailsVOList.add(adminDetailsVO);
            }

            pstmt=conn.prepareStatement(countQuery.toString());
            rs=pstmt.executeQuery();
            rs.next();
            if(rs!=null)
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
            else
            {
                paginationVO.setTotalRecords(0);
            }

        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "getAdminUsersList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "getAdminUsersList()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return adminDetailsVOList;
    }

    public boolean isUniqueName(AdminDetailsVO adminDetailsVO)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        boolean status=true;
        String query=null;

        try
        {
            conn = Database.getRDBConnection();
            query = "select adminid from admin where login=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,adminDetailsVO.getLogin());
            resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                status=false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "removeAdminUserEntries()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "removeAdminUserEntries()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }
    public String addNewAdminUser(AdminDetailsVO adminDetailsInputVO,User user)throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        String status="";
        String query=null;
        try
        {
            conn = Database.getConnection();
            query = "insert into admin(login,contact_emails,accid,fdtstamp)values(?,?,?,unix_timestamp(now()))";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,adminDetailsInputVO.getLogin());
            pstmt.setString(2,adminDetailsInputVO.getContactEmails());
            pstmt.setLong(3,user.getAccountId());
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status="success";
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "addNewAdminUser()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "addNewAdminUser()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }
    public  void removeAdminUserEntries(AdminDetailsVO adminDetailsVO)throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        PreparedStatement pstm1=null;
        String query=null;
        String dquery=null;
        try
        {
            con=Database.getConnection();
            query=" delete from user where login=? and roles='admin'";
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,adminDetailsVO.getLogin());
            int k=pstmt.executeUpdate();

            dquery=" delete from admin where login=?";
            pstm1=con.prepareStatement(dquery.toString());
            pstmt.setString(1,adminDetailsVO.getLogin());

            int k1=pstm1.executeUpdate();


        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "removeAdminUserEntries()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("AdminDAO.java", "removeAdminUserEntries()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
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


    public boolean adminForgotPassword(String login, User user, String remoteAddr, String Header, String actionExecuterId,String adminid) throws SystemError
    {
        boolean flag = false;
        String role = "admin";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        logger.debug("Entering forgotPassword() method");
        String fpasswd = generateKey(8);
        logger.debug("Temporary Pasword :::::::::"+fpasswd);
        String hashPassword = null;
        String oldPasshash = null, accountid = null;
        // role = getUserRole(user);
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
            String query2 = "select hashedpasswd from `user` where login= ? and roles=? ";
            pstmt2 = conn.prepareStatement(query2);
            pstmt2.setString(1, login);
            pstmt2.setString(2, role);
            logger.debug("partner---" + pstmt2);
            rs = pstmt2.executeQuery();
            if (rs.next())
            {
                oldPasshash = rs.getString(1);
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
            conn=Database.getConnection();
            PreparedStatement pstmt1;
            String query = "update user set hashedpasswd=?,oldpasswdhashes=? where login=? and roles=?";
            pstmt1 = conn.prepareStatement(query);
            pstmt1.setString(1, hashPassword);
            pstmt1.setString(2, oldPasshash);
            pstmt1.setString(3, login);
            pstmt1.setString(4, role);
            logger.error("update query----" + pstmt1);
            int success = pstmt1.executeUpdate();
            logger.error("Success::::" + success);
        }
        catch (Exception e)
        {
            logger.error("Change password throwing Authetication Exception ", e);
            return false;
        }
        finally
        {
            Database.closeConnection(conn);
        }

        logger.debug("user table updated with temporary password");

        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();

            String qry = "";
            String forgotDate ="";
            // qry = "select date from partner_users where login= ? and role=?";

            qry = "select fdtstamp from admin where login= ? and adminid=?";

            pstmt = conn.prepareStatement(qry);
            pstmt.setString(1, login);
            pstmt.setString(2, adminid);
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

                //String tableName = "admin";
                // tableName = "partner_users";

                String query = "update admin set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and adminid=?";

                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, hashPassword);
                /*pstmt.setString(2,hashPassword);*/
                pstmt.setString(2, login);
                System.out.println("login"+login);
                pstmt.setString(3, adminid);
                logger.error("update query----" + pstmt);
                int success = pstmt.executeUpdate();
                logger.error("Success::::"+success);
                if (success > 0)
                {
                    /*ResultSet rs = null;*/
                    logger.debug("new temporary password has been set");

                    // send mail to member warning to use this password within 24 hr.
                    String query1 = "";

                    // query1 = "select partnerid,contact_emails,userid from partner_users where login= ? and role=?";

                    query1 = "select adminid,contact_emails from admin where login= ? and adminid=?";
                    try
                    {
                        pstmt = conn.prepareStatement(query1);
                        pstmt.setString(1, login);
                        pstmt.setString(2, adminid);
                        rs = pstmt.executeQuery();
                        if (rs.next())
                        {
                            String emailAddr = rs.getString("contact_emails");
                            String adminidmail = rs.getString("adminid");
                            String Subject = "Admin User Forget Password";
                            //MailService mailService=new MailService();
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            HashMap mailValues = new HashMap();
                            mailValues.put(MailPlaceHolder.TOID, adminidmail);
                            mailValues.put(MailPlaceHolder.USERID, login);
                            mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                            mailValues.put(MailPlaceHolder.PARTNERID, "1");
                            mailValues.put(MailPlaceHolder.ADMINEMAIL, "true");
                            mailValues.put(MailPlaceHolder.EmailToAddress, emailAddr);
                            mailValues.put(MailPlaceHolder.SUBJECT, Subject);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNER_USER_FORGOT_PASSWORD, mailValues);

                        }

                        flag = true;
                    }
                    finally
                    {
                    }

                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating admin table.");
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
        return flag;
    }

  public String getLoginAndisActive( String adminid)
  {
      Connection con=null;
      PreparedStatement preparedStatement= null;
      ResultSet resultSet= null;
      String status=null;
      try
      {
          con=Database.getConnection();
          StringBuffer query1= new StringBuffer("SELECT login,isActive FROM admin");
          if (functions.isValueNull(adminid))
          {
              query1.append(" where adminid= "+adminid);
          }
          preparedStatement= con.prepareStatement(query1.toString());
          logger.error(preparedStatement);
          resultSet= preparedStatement.executeQuery();

          if (resultSet.next())
          {
              status= resultSet.getString("login")+" "+resultSet.getString("isActive");
          }
          logger.error("STATUS++++ "+status);
      }
      catch (SQLException e)
      {
         logger.error("SQLEXCPTION::: ",e);
      }
      catch (Exception systemError)
      {
          logger.error("SystemError::: "+systemError);
      }
      finally
      {
          Database.closeResultSet(resultSet);
          Database.closePreparedStatement(preparedStatement);
          Database.closeConnection(con);
      }
      return status;
  }

    public String getAdminFromLogin(String login)
    {
        Connection con= null;
        PreparedStatement ps= null;
        ResultSet rs= null;
        String adminid= "";
        try
        {
            con=Database.getConnection();
            StringBuffer query= new StringBuffer( "select adminid from admin where ");
            if (functions.isValueNull(login))
            {
                query.append("login= '"+login+"'");
            }
             ps= con.prepareStatement(query.toString());
             logger.error("prepareStatement:::: "+ps);
             rs= ps.executeQuery();
            if (rs.next())
            {
                adminid= rs.getString("adminid");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException::: ",e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::: "+systemError.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        return adminid;
    }

    public String getUpdateAction(String adminid,String isActive, String login)
    {
        Connection connection= null;
        PreparedStatement pstmt= null;
        int count=0;
        String status= "";

        try
        {
            connection= Database.getConnection();
            String query2 = "Update admin set isActive=? where adminid=?";
            pstmt=connection.prepareStatement(query2);
            pstmt.setString(1,isActive );
            pstmt.setString(2, adminid );
            logger.error("preparedstatement::: "+pstmt);
            count= pstmt.executeUpdate();

            if (count==1 && isActive.equalsIgnoreCase("N"))
            {
                status = login+" Account Deactivated.";
            }
            else if (count== 1 && isActive.equalsIgnoreCase("Y"))
            {
                status= login+ " Account Activated.";
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException++ ",e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError++ ",systemError);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return status;
    }
}
