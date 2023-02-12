package com.fraud;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.logicboxes.util.Util;
import com.manager.vo.fraudruleconfVOs.FraudSystemAccountVO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/8/15
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudSystemAccountService
{
    public static Logger logger =new Logger(FraudSystemAccountService.class.getName());
    public static Hashtable<String,FraudSystemAccountVO>fraudSystemAccount;
    static
    {
        try
        {
            loadFraudSystemAccount();
        }
        catch (Exception e)
        {
            logger.error("Error while loading fraud system account: " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
    public static FraudSystemAccountVO getFraudSystemAccount(String fsAccountId)
    {
        return fraudSystemAccount.get(fsAccountId);
    }
    public static void loadFraudSystemAccount() throws Exception
    {
        logger.info("loading from fraud system account");
        fraudSystemAccount= new Hashtable();
        Connection conn =null;
        try
        {
            conn=Database.getConnection();
            ResultSet rs=Database.executeQuery("select * from fraudsystem_account_mapping",conn);
            while (rs.next())
            {
                String fsAccountId=rs.getString("fsaccountid");
                fraudSystemAccount.put(fsAccountId,new FraudSystemAccountVO(rs));
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
}
