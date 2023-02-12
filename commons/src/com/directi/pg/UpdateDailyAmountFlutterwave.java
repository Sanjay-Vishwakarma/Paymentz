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
public class UpdateDailyAmountFlutterwave
{
    private  static Logger logger   = new Logger(UpdateDailyAmountFlutterwave.class.getName());

    public static void main(String args[]) throws SystemError
    {
        UpdateDailyAmountFlutterwave updateDailyAmountFlutterwave   = new UpdateDailyAmountFlutterwave();
        String sResponse                                            = updateDailyAmountFlutterwave.updateDailyAmountFlutterwave(new Hashtable());
    }

    public String updateDailyAmountFlutterwave(Hashtable hashtable)
    {
        logger.error("---inside updateDailyAmountFlutterwave ---");
        Functions functions                             = new Functions();
        List<GatewayAccount> gatewayAccountList         = new ArrayList<>();
        List<GatewayAccount> updateGatewayAccountList   = new ArrayList<>();
        Date dateStart  = new Date();
        try
        {
            gatewayAccountList  = getFlutterWaveAaccountList();
            int startNumber     = 0;
            int endNumber       = 0;
            logger.error("updateDailyAmountFlutterwave Start Time ----------> "+dateStart.getTime());
            for (GatewayAccount gatewayAccount : gatewayAccountList)
            {
                Random random = new Random();
                logger.error("gatewayAccount ---> "+gatewayAccount.getAccountId() +" Daily_Amount_Range --> "+gatewayAccount.getDaily_Amount_Range());

                if (functions.isValueNull(gatewayAccount.getDaily_Amount_Range()) && gatewayAccount.getDaily_Amount_Range().contains("-"))
                {
                    String[] amount_range = gatewayAccount.getDaily_Amount_Range().split("-");

                    if (amount_range[0].contains(".") && amount_range[0].split("\\.").length > 0)
                    {
                        startNumber = Integer.parseInt(amount_range[0].split("\\.")[0]);
                    }
                    else
                    {
                        startNumber = Integer.parseInt(String.format("%.2f", Double.parseDouble(amount_range[0])));
                    }

                    if (amount_range[1].contains(".") && amount_range[1].split("\\.").length > 0)
                    {
                        endNumber = Integer.parseInt(amount_range[1].split("\\.")[0]);
                    }
                    else
                    {
                        endNumber = Integer.parseInt(String.format("%.2f", Double.parseDouble(amount_range[1])));
                    }

                    String dailyAmountStr   = random.ints(startNumber, endNumber).findFirst().getAsInt() + "";
                    double dailyAmount      = Double.parseDouble(dailyAmountStr);
                    if (dailyAmount > 0)
                    {
                        gatewayAccount.setDaily_Account_Amount_Limit(dailyAmount + "");
                        updateGatewayAccountList.add(gatewayAccount);
                    }

                    logger.error("gatewayAccount.getAccountId() "+gatewayAccount.getAccountId()+" DailyAmount "+dailyAmount);
                }
            }

            insertFlutterWaveAccountHistory(updateGatewayAccountList);
            updateDailyAmountLimit(updateGatewayAccountList);

            logger.error("updateDailyAmountFlutterwave End Time ----------> "+(new Date()).getTime());
            logger.error("updateDailyAmountFlutterwave Differ ----------> "+((new Date()).getTime() - dateStart.getTime()));

            try
            {
                GatewayAccountService.loadGatewayAccounts();
            }
            catch (Exception e)
            {
                logger.error("Exception while getting UpdateDailyAmountFlutterwave GatewayAccountsDetails ",e);
            }

            return "success";
        }
        catch (Exception e)
        {
            logger.error("UpdateDailyAmountFlutterwave Exception---->",e);
            e.printStackTrace();
        }
        return "failed";

    }

    public List<GatewayAccount> getFlutterWaveAaccountList()
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
            StringBuffer gatewayAccountQuery    = new StringBuffer("select accountid,daily_amount_range,daily_amount_limit from `gateway_accounts` where pgtypeid= ? and daily_amount_range_check= ?");

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

    public void updateDailyAmountLimit(List<GatewayAccount> gatewayAccountList)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE gateway_accounts SET daily_amount_limit=? WHERE accountid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            connection.setAutoCommit(false);

            for (GatewayAccount gatewayAccount : gatewayAccountList)
            {
                if (!gatewayAccount.getDaily_Account_Amount_Limit().equalsIgnoreCase("0.0") || !gatewayAccount.getDaily_Account_Amount_Limit().equalsIgnoreCase("0"))
                {
                    ps2.setString(1, gatewayAccount.getDaily_Account_Amount_Limit());
                    ps2.setString(2, gatewayAccount.getAccountId() + "");
                    ps2.addBatch();
                }

            }

            int[] count = ps2.executeBatch();
            logger.error(" count --------> "+count.length);
            connection.commit();
        }
        catch (SQLException se)
        {
            logger.error("updateDailyAmountLimit SQLException---", se);
            se.printStackTrace();
        }
        catch (SystemError s)
        {
            logger.error("updateDailyAmountLimit SystemError---", s);
            s.printStackTrace();
        }
        finally
        {
            if (connection != null)
            {
                Database.closeConnection(connection);
            }
        }
    }

    public void insertFlutterWaveAccountHistory(List<GatewayAccount> gatewayAccountList)
    {
        Connection connection                   = null;
        PreparedStatement preparedStatement     = null;
        try
        {
            connection          = Database.getConnection();
            String query        = "INSERT INTO flutterwaveAccountHistory (AccountId,dailyAmountLimit) VALUES(?,?)";
            preparedStatement   = connection.prepareStatement(query);

            connection.setAutoCommit(false);

            for(GatewayAccount gatewayAccount : gatewayAccountList)
            {
                preparedStatement.setInt(1, gatewayAccount.getAccountId());
                preparedStatement.setFloat(2, gatewayAccount.getDailyAmountLimit());

                preparedStatement.addBatch();
            }
            int[] count = preparedStatement.executeBatch();
            logger.error(" Insert count -------->"+count.length);
            connection.commit();

        }
        catch (SystemError systemError)
        {
            logger.error("insertFlutterWaveAccountHistory SystemError -->",systemError);
            systemError.printStackTrace();
        }
        catch (SQLException e)
        {
            logger.error("insertFlutterWaveAccountHistory SQLException -->",e);
            e.printStackTrace();
        }
    }


}