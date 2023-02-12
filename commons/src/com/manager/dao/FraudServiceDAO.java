package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/23/14
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudServiceDAO
{
    private Logger logger=new Logger(FraudServiceDAO.class.getName());
    private TransactionLogger transactionLogger=new TransactionLogger(FraudServiceDAO.class.getName());


    public FraudAccountDetailsVO getMerchantFraudConfigurationDetails(String memberId)
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        FraudAccountDetailsVO accountDetailsVO=new FraudAccountDetailsVO();
        try
        {
            Date date472=new Date();
            transactionLogger.debug("executeQuery start time 472########"+(date472.getTime()));
            con= Database.getConnection();
            StringBuilder query =new StringBuilder("SELECT mfsm.memberid,mfsm.isonlinefraudcheck,mfsm.isactive,fsam.accountname,fsam.password,fsasm.submerchantUsername,fsasm.submerchantPassword,fsam.isTest,fsam.fsid,fsasm.subaccountname,fsasm.subusername,fsasm.subpwd FROM merchant_fssubaccount_mappping AS mfsm JOIN fsaccount_subaccount_mapping AS fsasm  ON mfsm.fssubaccountid=fsasm.fssubaccountid JOIN fraudsystem_account_mapping AS fsam ON fsasm.fsaccountid=fsam.fsaccountid AND mfsm.memberid=? AND mfsm.isactive='Y'");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1, memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                accountDetailsVO.setFraudSystemMerchantId(rs.getString("accountname"));
                accountDetailsVO.setPassword(rs.getString("password"));
                accountDetailsVO.setSubmerchantUsername(rs.getString("submerchantUsername"));
                accountDetailsVO.setSubmerchantPassword(rs.getString("submerchantPassword"));
                accountDetailsVO.setSubMerchantId(rs.getString("subaccountname"));
                accountDetailsVO.setUserName(rs.getString("subusername"));
                accountDetailsVO.setUserNumber(rs.getString("subpwd"));
                accountDetailsVO.setIsTest(rs.getString("isTest"));
                accountDetailsVO.setFraudSystemId(rs.getString("fsid"));
                accountDetailsVO.setIsOnlineFraudCheck(rs.getString("isonlinefraudcheck"));
            }
            transactionLogger.debug("executeQuery getMerchantFraudConfigurationDetails end time 472########"+(new Date().getTime()));
            transactionLogger.debug("executeQuery getMerchantFraudConfigurationDetails diff time 472########"+(new Date().getTime()-date472.getTime()));


        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting fraud conf tables ",e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting  fraud conf tables ",systemError);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(pstmt);
            Database.closeResultSet(rs);
        }
        return accountDetailsVO;
    }
}
