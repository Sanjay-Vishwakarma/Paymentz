package servlets;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 1/7/14
 * Time: 1:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChargesUtils
{
    private static Logger log = new Logger(ChargesUtils.class.getName());
    private static Hashtable<String, String> charges;
    private static Hashtable<String, String> members;

    static
    {
        try
        {
            loadCharges();
            loadMembers();
        }
        catch (Exception e)
        {
            log.error("Error while loading Charges : " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    public static void loadMembers() throws Exception
    {
        log.info("Loading Members......");
        members = new Hashtable<String, String>();
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            rs = Database.executeQuery("select memberid, company_name from members where activation='Y' ORDER BY memberid ASC" , conn);
            while (rs.next())
            {

                members.put(rs.getInt("memberid")+"", rs.getString("company_name"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    public static void loadCharges() throws Exception
    {
        log.info("Loading Charges......");
        charges = new Hashtable<String, String>();
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            rs = Database.executeQuery("select chargeid,chargename  from charge_master", conn);
            while (rs.next())
            {

                charges.put(rs.getInt("chargeid")+"", rs.getString("chargename"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    public static String getChargeName(String chargeId)
    {
        return charges.get(chargeId);
    }

    public static Hashtable getHashFromResultSet(ResultSet rs) throws SQLException
    {
        int j = 0;
        if (rs == null)
            throw new SQLException("Empty ResultSet in getHashFromResultSet as parameter");

        Hashtable outerHash = new Hashtable();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int count = rsMetaData.getColumnCount();

        int i = 0;

        while (rs.next())
        {
            Hashtable innerHash = new Hashtable();

            for (i = 1; i <= count; i++)
            {
                if (rs.getString(i) != null)
                {


                    if (rsMetaData.getColumnLabel(i).equalsIgnoreCase("Expiry date") && !rs.getString(i).equals("") && rs.getString(i) != null)
                    {
                        innerHash.put(rsMetaData.getColumnLabel(i), PzEncryptor.decryptExpiryDate(rs.getString(i)));
                    }
                    else if (rsMetaData.getColumnLabel(i).equalsIgnoreCase("expdate") && !rs.getString(i).equals("") && rs.getString(i) != null)
                    {
                        innerHash.put(rsMetaData.getColumnLabel(i), PzEncryptor.decryptExpiryDate(rs.getString(i)));
                    }

                    //paymodeid
                    else if (rsMetaData.getColumnLabel(i).equalsIgnoreCase("paymodeid") && !rs.getString(i).equals("") && rs.getString(i) != null)
                    {     //System.out.println(""+GatewayAccountService.getPaymentMode(rs.getString(i)));
                        innerHash.put(rsMetaData.getColumnLabel(i), GatewayAccountService.getPaymentTypes(rs.getString(i)));
                    }

                    else if(rsMetaData.getColumnLabel(i).equalsIgnoreCase("cardtypeid") && !rs.getString(i).equals("") && rs.getString(i)!=null )
                    {     //System.out.println(""+GatewayAccountService.getCardType(rs.getString(i)));
                        innerHash.put(rsMetaData.getColumnLabel(i),GatewayAccountService.getCardType(rs.getString(i)));
                    }

                    else if(rsMetaData.getColumnLabel(i).equalsIgnoreCase("chargeid") && !rs.getString(i).equals("") && rs.getString(i)!=null )
                    {     //System.out.println(""+GatewayAccountService.getCardType(rs.getString(i)));
                        innerHash.put(rsMetaData.getColumnLabel(i),getChargeName(rs.getString(i)));
                    }
                    else
                    {
                        innerHash.put(rsMetaData.getColumnLabel(i), rs.getString(i));
                    }
                }
            }
            j++;
            outerHash.put("" + j, innerHash);
        }
        return outerHash;
    }

    public static String getGatewayName(String accountId){
        GatewayAccount account =GatewayAccountService.getGatewayAccount(accountId);
        return account.getDisplayName();
    }

    public static String getCompanyName(String memberId){
        return members.get(memberId);
    }

    public static Hashtable<String, String> getMembers(){
        return members;
    }

    public enum keyword{
        AuthFailed,
        AuthStarted,
        CaptureSuccess,
        Settled,
        Chargeback,
        Reversed,
        Total,
        Wire,
        Statement,
        Setup,
        TotalReserveGenerated,
        TotalReserveRefunded,
        GrossBalanceAmount,
        CalculatedReserveRefund,
        VerifyOrder,
        RefundAlert,
        RetrivalRequest,
        ServiceTax,
        NetProfit,
        BankIntegration,
        SSLCertificate,
        FraudulentTransaction,
        FraudAlert,
        NetFinalAmount,
        Payout,
        DomesticTotal,
        InternationalTotal,
        CaseFiling,
        OtherCharges,
        TotalFees,
        PreArbitration;
    }

    public enum frequency{
        Per_Transaction,
        Yearly,
        Monthly,
        Weekly,
        One_Time
    }

    public enum category{
        Success,
        Failure,
        Others
    }

    public enum unit{
        FlatRate,
        Percentage
    }

    public enum subKeyword{
         Amount,
         Count,
         ConsumerCardAmt ,
         CommercialCardAmt,
         ConsumerCardCount,
         CommercialCardCount
    }
}
