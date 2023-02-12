package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Admin on 8/12/2019.
 */
public class UpdateMonthlyAmountFlutterwave
{
    private  static Logger logger   = new Logger(UpdateMonthlyAmountFlutterwave.class.getName());

    public static void main(String args[]) throws SystemError
    {
        UpdateMonthlyAmountFlutterwave updateMonthlyAmountFlutterwave   = new UpdateMonthlyAmountFlutterwave();
        String sResponse                                               = updateMonthlyAmountFlutterwave.updateMonthlyAmountFlutterwave(new Hashtable());
    }

    public String updateMonthlyAmountFlutterwave(Hashtable hashtable)
    {
        logger.error("---inside updateMonthlyAmountFlutterwave ---");
        Functions functions                             = new Functions();
        List<GatewayAccount> gatewayAccountList         = new ArrayList<>();
        Date dateStart  = new Date();
        try
        {
            gatewayAccountList  = getFlutterWaveAccountList();
            logger.error("updateMonthlyAmountFlutterwave Start Time ----------> "+dateStart.getTime());
            for (GatewayAccount gatewayAccount : gatewayAccountList)
            {
                logger.error("gatewayAccount.getAccountId() -------> "+gatewayAccount.getAccountId());
                if(gatewayAccount.getDaily_Amount_Range_Check().equalsIgnoreCase("Y")){
                    boolean isMonthlyLimitOver = isMonthlyLimitOver(gatewayAccount);
                    logger.error("success AccountId -------> " + gatewayAccount.getAccountId() +" isMonthlyLimitOver -----> "+isMonthlyLimitOver);
                }
            }
            logger.error("updateMonthlyAmountFlutterwave End Time ----------> "+(new Date()).getTime());
            logger.error("updateMonthlyAmountFlutterwave Differ ----------> "+((new Date()).getTime() - dateStart.getTime()));
            try
            {
                GatewayAccountService.loadGatewayAccounts();
            }
            catch (Exception e)
            {
                logger.error("Exception while getting UpdateMonthlyAmountFlutterwave GatewayAccountsDetails ",e);
            }

            return "success";
        }
        catch (Exception e)
        {
            logger.error("Exception---->",e);
            e.printStackTrace();
        }
        return "failed";

    }

    public List<GatewayAccount> getFlutterWaveAccountList()
    {
        Connection con                      = null;
        ResultSet resultSetGateWayAccount   = null;
        ResultSet resultSetGateType         = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement pstmtGatewayType  = null;

        List<GatewayAccount> gatewayAccountList = new ArrayList<GatewayAccount>();

        try
        {
            con = Database.getRDBConnection();

            StringBuffer gateway_type           = new StringBuffer("select pgtypeid from `gateway_type` where gateway='Flutter'");
            StringBuffer gatewayAccountQuery    = new StringBuffer("select accountid,daily_amount_range,daily_amount_limit,monthly_amount_limit,daily_amount_range_check,merchantid from `gateway_accounts` where pgtypeid= ? and daily_amount_range_check= ?");

            pstmtGatewayType        = con.prepareStatement(gateway_type.toString());
            resultSetGateType       = pstmtGatewayType.executeQuery();
            logger.error("gateway_type ------------>  "+pstmtGatewayType);
            while (resultSetGateType.next())
            {
                logger.error("gateway type Id ------------>  "+resultSetGateType.getInt("pgtypeid"));

                preparedStatement = con.prepareStatement(gatewayAccountQuery.toString());
                preparedStatement.setInt(1, resultSetGateType.getInt("pgtypeid"));
                preparedStatement.setString(2, "Y");

                logger.error("resultSetGateWayAccount  " + preparedStatement);

                resultSetGateWayAccount = preparedStatement.executeQuery();

                while (resultSetGateWayAccount.next())
                {
                    GatewayAccount gatewayAccount = new GatewayAccount();

                    gatewayAccount.setAccountId(resultSetGateWayAccount.getInt("accountid"));
                    gatewayAccount.setDaily_Amount_Range(resultSetGateWayAccount.getString("daily_amount_range"));
                    gatewayAccount.setDailyAmountLimit(resultSetGateWayAccount.getFloat("daily_amount_limit"));
                    gatewayAccount.setMonthlyAmountLimit(resultSetGateWayAccount.getFloat("monthly_amount_limit"));
                    gatewayAccount.setMerchantId(resultSetGateWayAccount.getString("merchantid"));
                    gatewayAccount.setDaily_Amount_Range_Check(resultSetGateWayAccount.getString("daily_amount_range_check"));

                    gatewayAccountList.add(gatewayAccount);
                }
            }

        }
        catch (SystemError systemError)
        {
            logger.error(" getFlutterWaveAaccountList system exception ::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("getFlutterWaveAaccountList SQL exception::", e);
        }
        finally
        {
            Database.closeResultSet(resultSetGateWayAccount);
            Database.closePreparedStatement(preparedStatement);

            Database.closeResultSet(resultSetGateType);
            Database.closePreparedStatement(pstmtGatewayType);
            Database.closeConnection(con);
        }
        return gatewayAccountList;
    }


    public boolean isMonthlyLimitOver(GatewayAccount gatewayAccount)
    {
        boolean isMonthlyLimitOver  =  false;
        Calendar calendar2          = Calendar.getInstance();
        Connection connection       = null;
        ResultSet resultSet         = null;
        float monthly_amount        = 0;
        Date dateStart  = new Date();
        try
        {
            calendar2.set(Calendar.HOUR_OF_DAY, 0);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            logger.debug("First day of Month------>" + calendar2.getTime());
            long monthsStartTimeInSecs = calendar2.getTimeInMillis() / 1000;

            connection          = Database.getConnection();
            logger.error("isMonthlyLimitOver Start Time ----------> "+dateStart.getTime());
            float account_monthly_amount_limit      = gatewayAccount.getMonthlyAmountLimit();
            String queryString                      = "select SUM(tt.amount) as amount,tt.currency from transaction_common tt where tt.fromid=? and tt.status  in ('capturesuccess','authsuccessful','reversed','settled','markedforreversal') and tt.dtstamp >? and tt.parentTrackingid IS NULL group by tt.currency";

            PreparedStatement preparedStatement   = connection.prepareStatement(queryString);
            preparedStatement.setString(1, gatewayAccount.getMerchantId());
            preparedStatement.setLong(2, monthsStartTimeInSecs);

            logger.error("isMonthlyLimitOver Query ------>" + preparedStatement);

            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                monthly_amount = resultSet.getFloat("amount");
            }
            logger.error("isMonthlyLimitOver AccountId --> "+gatewayAccount.getAccountId() +" monthly_amount -----> " + monthly_amount +" account_monthly_amount_limit ----> "+account_monthly_amount_limit);

            if(monthly_amount >= account_monthly_amount_limit){
                isMonthlyLimitOver = true;
                updateDailyAmountLimit(gatewayAccount);
            }
            logger.error("isMonthlyLimitOver End Time ----------> "+(new Date()).getTime());
            logger.error("isMonthlyLimitOver Differ ----------> "+((new Date()).getTime() - dateStart.getTime()));
        }
        catch (Exception s)
        {
            logger.error("SystemError---", s);
            s.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                Database.closeConnection(connection);
            }
        }

        return isMonthlyLimitOver;
    }


    public int updateDailyAmountLimit(GatewayAccount gatewayAccount)
    {
        Connection connection   = null;
        ResultSet resultSet     = null;
        int count = 0;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE gateway_accounts SET daily_amount_limit=? WHERE accountid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);

            ps2.setString(1, "0.00");
            ps2.setString(2, gatewayAccount.getAccountId() + "");

            count =  ps2.executeUpdate();

        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
            se.printStackTrace();
        }
        catch (SystemError s)
        {
            logger.error("SystemError---", s);
            s.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                Database.closeConnection(connection);
            }
        }
        return  count;
    }


}