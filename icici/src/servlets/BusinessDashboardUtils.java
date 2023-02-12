package servlets;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.PaymentzEncryptor;
import com.directi.pg.TransactionEntry;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import org.apache.poi.util.SystemOutLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 1/7/14
 * Time: 1:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class BusinessDashboardUtils
{
    private static Logger log = new Logger(BusinessDashboardUtils.class.getName());
    private static String xmlDataFilePath = ApplicationProperties.getProperty("XML_DATA_FILE_PATH");
    private static String[] colorPallet = new String[]{"AFD8F8", "F6BD0F", "8BBA00", "FF8E46", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE", "78F04A"};

    public static void prepareAllChartsData(String memberId, String accountId, String payMode, String cardType){

        String chartType = "Sales";
        //Prepare Sales Chart Data
        prepareColumnChartData(memberId,accountId,payMode,cardType,chartType);
        //Prepare Sales Meter Data
        prepareMeterChartData(memberId,accountId,payMode,cardType,chartType);

        chartType ="Refund";
        //Prepare Refund Chart Data
        prepareColumnChartData(memberId,accountId,payMode,cardType,chartType);
        //Prepare Refund Meter Data
        prepareMeterChartData(memberId,accountId,payMode,cardType,chartType);

        chartType ="Chargeback";
        //Prepare Chargeback Chart Data
        prepareColumnChartData(memberId,accountId,payMode,cardType,chartType);
        //Prepare Chargeback Meter Data
        prepareMeterChartData(memberId,accountId,payMode,cardType,chartType);

        chartType ="Fraud";
        //Prepare Fraud Chart Data
        prepareColumnChartData(memberId,accountId,payMode,cardType,chartType);
        //Prepare Fraud Meter Data
        prepareMeterChartData(memberId,accountId,payMode,cardType,chartType);
    }

    public static void prepareColumnChartData(String memberId, String accountId, String payMode, String cardType, String chartType){

        log.debug("Preparing column chart for "+chartType);
        TransactionEntry transEntry = new TransactionEntry();
        Hashtable gatewayHash = transEntry.getGatewayHash(memberId, accountId);

        String tableName;
        String fields;
        StringBuffer query = new StringBuffer();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -365);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
        String lastYearTime = String.valueOf(cal.getTimeInMillis() / 1000);
        Connection conn = null;
        ResultSet rs = null;

        try{
            conn = Database.getConnection();

            Iterator i = gatewayHash.keySet().iterator();
            while(i.hasNext())
            {
                String amountField ="";
                if(chartType.equals("Sales")){
                    amountField = "captureamount";
                }
                else if(chartType.equals("Refund")){
                    amountField = "refundamount";
                }
                else if(chartType.equals("Chargeback")){
                    amountField = "chargebackamount";
                }
                else if(chartType.equals("Fraud")){
                    amountField = "amount";
                }
                tableName = Database.getTableName((String)i.next());
                fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum("+amountField+") as amount";
                query.append("select " + fields + " from " + tableName + " where");
                query.append(" toid ='" + memberId + "'");
                if(accountId !=null && !accountId.equals("")){
                    query.append(" and accountid ='" + accountId + "'");
                }
                if(payMode !=null && !payMode.equals("")){
                    query.append(" and paymodeid ='" + payMode + "'");
                }
                if(cardType !=null && !cardType.equals("")){
                    query.append(" and cardtypeid ='" + cardType + "'");
                }
                query.append(" and dtstamp >='" + lastYearTime + "'");
                if(chartType.equals("Sales")){
                    query.append(" and status in ('capturesuccess','settled')");
                }
                else if(chartType.equals("Refund")){
                    query.append(" and status ='reversed'");
                }
                else if(chartType.equals("Chargeback")){
                    query.append(" and status ='chargeback'");
                }
                else if(chartType.equals("Fraud")){
                    query.append(" and trackingid IN (SELECT icicitransid FROM bin_details WHERE isFraud='Y')");
                }
                query.append(" group by month");
                if(i.hasNext())
                    query.append(" UNION ");
            }
            StringBuffer countquery = new StringBuffer("select month, SUM(amount) as amount from ( " + query + ")as temp  group by month ");

            log.debug("===countquery=="+countquery.toString());

            rs = Database.executeQuery(countquery.toString(), conn);

            StringBuilder chartData = new StringBuilder("<graph bgcolor='F2F2F2' showCanvasBg='0' borderColor='B8B7B7' showBorder='1' caption='Monthwise "+chartType+" for last 1 year' xAxisName='Month' yAxisName='Amount in USD' " +
                    " decimalPrecision='0' formatNumberScale='0' showValues='0' >");

            int colourCount = 0;
            Map<Date, String> treeMap = new TreeMap<Date, String>();
            SimpleDateFormat formatter = new SimpleDateFormat("MMM-yy");
            while (rs.next())
            {
                //chartData.append("<set name='" + rs.getString("month") + "' value='" + rs.getString("amount") + "' color='" + colorPallet[colourCount] + "' />");
                treeMap.put(formatter.parse(rs.getString("month")),"<set name='" + rs.getString("month") + "' value='" + rs.getString("amount") + "' color='" + colorPallet[colourCount] + "' />");
                colourCount++;
            }
            for(String data : treeMap.values()){
                chartData.append(data);
            }
            chartData.append("</graph>");
            createFile(xmlDataFilePath + "/"+chartType+"Data.xml", chartData);
        }
        catch(Exception e){
            log.error("Error in column chart data creation",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    public static void blockTransactions(String memberId){
        try{
            Connection conn = Database.getConnection();
            String query = "Update members set activation ='T' where memberid='"+memberId+"'";
            Database.executeUpdate(query, conn);
            conn.close();
        }
        catch (Exception e){
            log.error("Error while blocking the transactions.",e);
        }
    }

    public static void blockRefunds(String memberId){
        try{
            Connection conn = Database.getConnection();
            String query = "Update members set isrefund ='N' where memberid='"+memberId+"'";
            Database.executeUpdate(query, conn);
            conn.close();
        }
        catch (Exception e){
            log.error("Error while blocking the transactions.",e);
        }
    }

    public static void prepareMeterChartData(String memberId, String accountId, String payMode, String cardType, String chartType){
        log.debug("Preparing meter chart for "+chartType);
        Connection conn = null;
        ResultSet rs = null;
        try{
            String amount = getCurrentMonthAmount(memberId,accountId,payMode,cardType,chartType) ;
            conn = Database.getConnection();

            String limit = "0";
            String maxLimit ="0";
            String minLimit ="0";
            String meterValue = "0";
            String suffix ="$";
            if(chartType.equals("Sales")){
                meterValue = amount;
                String limitQuery = "select monthly_amount_limit from members where memberid='"+memberId+"'";
                rs = Database.executeQuery(limitQuery, conn);
                while (rs.next())
                {
                    Double salesMax = Math.round( rs.getDouble("monthly_amount_limit")*1.1 * 100.0 ) / 100.0;
                    Double salesMin = Math.round( rs.getDouble("monthly_amount_limit")*0.9 * 100.0 ) / 100.0;
                    limit =String.valueOf(rs.getDouble("monthly_amount_limit"));
                    maxLimit = String.valueOf(salesMax);
                    minLimit = String.valueOf(salesMin);
                }
            }
            else if (chartType.equals("Refund")){
                meterValue = amount;
                String limitQuery = "select refunddailylimit from members where memberid='"+memberId+"'";
                rs = Database.executeQuery(limitQuery, conn);
                while (rs.next())
                {
                    Double refundMax = Math.round( rs.getInt("refunddailylimit")*30*1.1 * 100.0 ) / 100.0;
                    Double refundMin = Math.round( rs.getInt("refunddailylimit")*30*0.9 * 100.0 ) / 100.0;
                    limit =String.valueOf(rs.getInt("refunddailylimit")*30);
                    maxLimit = String.valueOf(refundMax);
                    minLimit = String.valueOf(refundMin);
                }
            }
            else if (chartType.equals("Chargeback")){
                suffix="%";
                String salesAmount = getCurrentMonthAmount(memberId,accountId,payMode,cardType,"Sales") ;
                if(Double.parseDouble(amount) > 0 && Double.parseDouble(salesAmount) > 0) {
                    meterValue = String.valueOf((Double.parseDouble(amount)/Double.parseDouble(salesAmount))*100);
                }
                else{
                    meterValue = "0";
                }
                String limitQuery = "select chargebacksaleratio from members where memberid='"+memberId+"'";
                rs = Database.executeQuery(limitQuery, conn);
                while (rs.next())
                {
                    Double chargebackMax = Math.round( rs.getDouble("chargebacksaleratio")*1.1 * 1000.0 ) / 10.0;
                    Double chargebackMin = Math.round( rs.getDouble("chargebacksaleratio")*0.9 * 1000.0 ) / 10.0;
                    limit =String.valueOf(rs.getDouble("chargebacksaleratio")*100);
                    maxLimit = String.valueOf(chargebackMax);
                    minLimit = String.valueOf(chargebackMin);
                }
            }
            else if (chartType.equals("Fraud")){
                suffix="%";
                String salesAmount = getCurrentMonthAmount(memberId,accountId,payMode,cardType,"Sales") ;
                if(Double.parseDouble(amount) > 0 && Double.parseDouble(salesAmount) > 0) {
                    meterValue = String.valueOf((Double.parseDouble(amount)/Double.parseDouble(salesAmount))*100);
                }
                else{
                    meterValue = "0";
                }
                String limitQuery = "select fraudsaleratio from members where memberid='"+memberId+"'";
                rs = Database.executeQuery(limitQuery, conn);
                while (rs.next())
                {
                    Double fraudMax = Math.round( rs.getDouble("fraudsaleratio")*1.1 * 1000.0 ) / 10.0;
                    Double fraudMin = Math.round( rs.getDouble("fraudsaleratio")*0.9 * 1000.0 ) / 10.0;
                    limit =String.valueOf(rs.getDouble("fraudsaleratio")*100);
                    maxLimit = String.valueOf(fraudMax);
                    minLimit = String.valueOf(fraudMin);
                }
            }
            StringBuilder chartData = new StringBuilder("<chart lowerLimit=\"0\" upperLimit=\""+maxLimit+"\" lowerLimitDisplay=\"Min\" upperLimitDisplay=\"Max\" gaugeStartAngle=\"180\" gaugeEndAngle=\"0\" palette=\"1\" numberSuffix=\""+suffix+"\" tickValueDistance=\"20\" showValue=\"1\">");
            chartData.append("<colorRange>\n" +
                    "    <color minValue=\"0\" maxValue=\""+minLimit+"\" code=\"8BBA00\"/>\n" +
                    "    <color minValue=\""+minLimit+"\" maxValue=\""+limit+"\" code=\"F6BD0F\"/>\n" +
                    "    <color minValue=\""+limit+"\" maxValue=\""+maxLimit+"\" code=\"FF654F\"/>\n" +
                    "  </colorRange>\n" +
                    "  <dials>\n" +
                    "    <dial value=\""+meterValue+"\" rearExtension=\"10\"/>\n" +
                    "  </dials>\n" +
                    "</chart>");
            createFile(xmlDataFilePath + "/" + chartType + "Meter.xml", chartData);
        }
        catch(Exception e){
            log.error("Error in column chart data creation",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    public static String getCurrentMonthAmount (String memberId, String accountId, String payMode, String cardType, String chartType){

        TransactionEntry transEntry = new TransactionEntry();
        Hashtable gatewayHash = transEntry.getGatewayHash(memberId, accountId);

        String tableName;
        String fields;
        StringBuffer query = new StringBuffer();

        String amount = "0";

        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DAY_OF_YEAR, -365);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        String lastYearTime = String.valueOf(cal.getTimeInMillis() / 1000);

        try{
            Connection conn = Database.getConnection();

            Iterator i = gatewayHash.keySet().iterator();
            while(i.hasNext())
            {
                String amountField ="";
                if(chartType.equals("Sales")){
                    amountField = "captureamount";
                }
                else if(chartType.equals("Refund")){
                    amountField = "refundamount";
                }
                else if(chartType.equals("Chargeback")){
                    amountField = "chargebackamount";
                }
                else if(chartType.equals("Fraud")){
                    amountField = "amount";
                }
                tableName = Database.getTableName((String)i.next());
                fields = "date_format(from_unixtime(dtstamp),'%b-%y') as month,sum("+amountField+") as amount";
                query.append("select " + fields + " from " + tableName + " where");
                query.append(" toid ='" + memberId + "'");
                if(accountId !=null && !accountId.equals("")){
                    query.append(" and accountid ='" + accountId + "'");
                }
                if(payMode !=null && !payMode.equals("")){
                    query.append(" and paymodeid ='" + payMode + "'");
                }
                if(cardType !=null && !cardType.equals("")){
                    query.append(" and cardtypeid ='" + cardType + "'");
                }
                query.append(" and dtstamp >='" + lastYearTime + "'");
                if(chartType.equals("Sales")){
                    query.append(" and status in ('capturesuccess','settled')");
                }
                else if(chartType.equals("Refund")){
                    query.append(" and status ='reversed'");
                }
                else if(chartType.equals("Chargeback")){
                    query.append(" and status ='chargeback'");
                }
                else if(chartType.equals("Fraud")){
                    query.append(" and trackingid IN (SELECT icicitransid FROM bin_details WHERE isFraud='Y')");
                }
                query.append(" group by month");
                if(i.hasNext())
                    query.append(" UNION ");
            }
            StringBuffer countquery = new StringBuffer("select month, SUM(amount) as amount from ( " + query + ")as temp  group by month ");

            log.debug("===countquery=="+countquery.toString());

            ResultSet rs = Database.executeQuery(countquery.toString(), conn);

            while (rs.next())
            {
                //chartData.append("<set name='" + rs.getString("month") + "' value='" + rs.getString("amount") + "' color='" + colorPallet[colourCount] + "' />");
                amount =rs.getString("amount");
            }
            conn.close();
        }
        catch(Exception e){
            log.error("Error in sql query",e);
        }
        return amount;
    }

    private static void createFile(String fileName, StringBuilder statusChartXml)
    {
        File f = new File(fileName);

        FileWriter fw = null;
        try
        {
            log.debug(" Creating xml data file " );
            fw = new FileWriter(f);
            fw.write(statusChartXml.toString());
            fw.flush();

        }
        catch (IOException e)
        {
            log.error(" Error in creating xml data file ",e);
        }
        finally
        {
            if (fw != null)
                try
                {
                    fw.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close file handler" + Util.getStackTrace(e));
                }
        }
    }
}
