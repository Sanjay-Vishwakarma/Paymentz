package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 1/16/14
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */



public class RefundChecker
{

    private static Logger log = new Logger(RefundChecker.class.getName());
    PreparedStatement preparedStatement=null;
    Connection connection=null;


    
    public boolean isRefundAllowed(String toid) throws SystemError
    {
        boolean isRefundAllowed=false;
        ResultSet resultSet= null;
        int iRefundLimit = 0;
        String query="SELECT activation,isrefund, refunddailylimit FROM members WHERE memberid=? and activation='Y' and isrefund = 'Y'";
        //String query="";
        try
        {
            connection=Database.getConnection();
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.setString(1,toid);
            resultSet=preparedStatement.executeQuery();
            
            if(resultSet.next())
            {
                iRefundLimit = resultSet.getInt("refunddailylimit");
                query = "select DISTINCT accountid from member_account_mapping where memberid=?";
                preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,toid);
                ResultSet rsAccounts=preparedStatement.executeQuery();
                int iRefundCount = 0;
                while(rsAccounts.next())
                {
                    int accountId = rsAccounts.getInt("accountid");

                    GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
                    String tableName = Database.getTableName(gatewayType.getGateway());

                    String sRefundCountQuery = "select count(*) as 'cntRefund' from " + tableName + " where toid=? and accountid=? and status in ('reversed','markedforreversal','partialrefund') and timestamp >= CONCAT(CURRENT_DATE,' 00:00:00') AND timestamp<=CONCAT(CURRENT_DATE,' 23:59:59')";
                    preparedStatement=connection.prepareStatement(sRefundCountQuery);
                    preparedStatement.setString(1,toid);
                    preparedStatement.setInt(2,accountId);
                    ResultSet rsRefundCount=preparedStatement.executeQuery();
                    if(rsRefundCount.next())
                    {
                        iRefundCount = iRefundCount + rsRefundCount.getInt("cntRefund");
                    }
                }
                log.error("refund allowed query----"+preparedStatement);
                log.error("iRefundLimit : " + iRefundLimit);
                log.error("iRefundCount : "+iRefundCount);
                if(iRefundCount < iRefundLimit)
                {
                    isRefundAllowed=true;
                }
            }
        }
        catch (SQLException e)
        {
            log.error("SQL exception in isRefundAllowed method of RefundChecker class",e);
            throw new SystemError(e.getMessage());
            
        }
        catch (SystemError systemError)
        {
            log.error("SystemError in isRefundAllowed method of RefundChecker class",systemError);
            throw systemError;
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return isRefundAllowed;
    }
}
