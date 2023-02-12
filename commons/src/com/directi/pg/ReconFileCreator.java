package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.handler.ACIKitHandler;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Apr 2, 2007
 * Time: 2:22:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReconFileCreator
{

    private static Logger log = new Logger(ReconFileCreator.class.getName());

    public static void createReconFile(int date, int month, int year) throws SystemError
    {

        Calendar cal = getMauritainCalendar(date, month, year);
        createReconFile(cal);
    }

    private static Calendar getMauritainCalendar(int date, int month, int year)
    {
        month--;
        TimeZone mauritiusTZ = TimeZone.getTimeZone("Indian/Mauritius");
        Calendar cal = Calendar.getInstance(mauritiusTZ);
        cal.set(year, month, date, 12, 0, 0);
        return cal;
    }

    public static void settleSBMTransactions(int date, int month, int year) throws SystemError
    {
        Calendar cal = getMauritainCalendar(date, month, year);
        settleSBMTransactions(cal);
    }


    public static void uploadToFtp(String fileName, String data) throws IOException
    {
        String ftpSite = ApplicationProperties.getProperty("FTP_SITE");
        String ftpUsername = ApplicationProperties.getProperty("FTP_USERNAME");
        String ftpPassword = ApplicationProperties.getProperty("FTP_PASSWORD");
        String url = "ftp://" + ftpUsername + ":" + URLEncoder.encode(ftpPassword) + "@" + ftpSite + "/" + fileName;
        log.debug(" URL : " + url);
        URL ftpurl = new URL(url);
        URLConnection con = ftpurl.openConnection();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(data);
        out.close();
    }


    private static void createReconFile(Calendar cal) throws SystemError
    {

        List<GatewayAccount> allSBMAccounts = getSBMAccounts();
        for (GatewayAccount account : allSBMAccounts)
        {
            String fileData = "";
            boolean reconCreated = false;
            String merchantId = account.getMerchantId();
            fileData = getFileData(account, cal);
            try
            {
                reconCreated = writeToFile(merchantId, fileData, cal);
                log.debug("Accountid : " + account.getAccountId() + "Recon : " + reconCreated + " Date :" + cal.getTime());
            }
            catch (IOException e)
            {
                throw new SystemError("Error while writing Recon File : " + Util.getStackTrace(e));
            }
            if (reconCreated)
            {
                log.debug("Updating transactions to podsent");
                updateToPodsent(account, cal);
                log.debug("Update complete");
            }
        }

    }

    private static void settleSBMTransactions(Calendar cal) throws SystemError
    {

        List<GatewayAccount> allSBMAccounts = getSBMAccounts();
        for (GatewayAccount account : allSBMAccounts)
        {
            settleTransactions(account, cal);
        }

    }

    private static void updateToPodsent(GatewayAccount account, Calendar cal) throws SystemError
    {
        Connection conn = null;
        int accountId = account.getAccountId();
        Hashtable<String, String> ht = getTimestamps(cal);
        String startTime = ht.get("starttime");
        String endTime = ht.get("endtime");
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_icicicredit set status = 'podsent',podbatch=timestamp,timestamp=timestamp where status = 'capturesuccess' and unix_timestamp(timestamp)>=? and unix_timestamp(timestamp)<=? and accountid =?";
            PreparedStatement p1=conn.prepareStatement(query);
            p1.setString(1,startTime);
            p1.setString(2,endTime);
            p1.setInt(3,accountId);
            int result = p1.executeUpdate();
            log.debug(" Rows Updated to podsent : " + result);
        }
        catch (Exception ex)
        {
            log.error("Status Failed : ",ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }


    }

    private static void settleTransactions(GatewayAccount account, Calendar cal) throws SystemError
    {
        TransactionEntry transactionEntry = new TransactionEntry();
        Connection conn = null;
        String merchantId = account.getMerchantId();
        int accountId = account.getAccountId();
        Hashtable<String, String> ht = getTimestamps(cal);
        String startTime = ht.get("starttime");
        String endTime = ht.get("endtime");
        try
        {
            conn = Database.getConnection();
            String query = "select T.toid,T.description,T.captureamount,T.amount,T.chargeper,T.fixamount,T.taxper,T.icicitransid,T.accountid from transaction_icicicredit T where T.status = 'podsent' and T.accountid =?" ;
           // String query = "select T.toid,T.description,T.amount,T.chargeper,T.fixamount,T.taxper,T.icicitransid,T.accountid from transaction_icicicredit T where T.status = 'podsent' and unix_timestamp(T.timestamp)>=" + startTime + " and unix_timestamp(T.timestamp)<=" + endTime + " and T.accountid =" + accountId;
            PreparedStatement p1=conn.prepareStatement(query);
            p1.setInt(1,accountId);
            ResultSet rs = p1.executeQuery();

            while (rs.next())
            {

                int toid = rs.getInt("toid");
                String description = rs.getString("description");
                //BigDecimal amount = new BigDecimal(rs.getString("amount"));
                BigDecimal amount = new BigDecimal(rs.getString("captureamount"));
                amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                String chargeper = rs.getString("chargeper");
                BigDecimal chargePer = new BigDecimal(chargeper);
                String transFixFee = rs.getString("fixamount");
                String taxPer = rs.getString("taxper");
                int trackingId = rs.getInt("icicitransid");

                AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(String.valueOf(accountId));
                Hashtable statusDetails = pg.getStatus(String.valueOf(trackingId));
                log.debug("Status details from gateway " + statusDetails);
                String status = (String) statusDetails.get("status");

                //getting status as APPROVED when merchant issevice and immediatly capture after auth.
                // In that case, SBM has both auth and capture within fraction of time and so when we ask for status
                // they return status for auth entry.
                //Normally when transaction is captured, we get CAPTURED as status
                if ("captured".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status))
                {
                    log.debug("Transaction captured at gateway so settling the transaction");
                    int transId = transactionEntry.creditTransaction(toid, merchantId, description, amount, chargePer, transFixFee, account, taxPer, trackingId);
                }
                else if ("voided".equalsIgnoreCase(status))
                {
                    log.debug("VOIDED on SBM so update details ");
                    query = "update transaction_icicicredit set status='authcancelled',captureresult='captured transaction voided on gateway' where icicitransid=? and status='podsent'";
                    PreparedStatement p=conn.prepareStatement(query);
                    p.setInt(1,trackingId);
                    p.executeUpdate();

                    // Start : Added for Action and Status Entry in Action History table
                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntry(String.valueOf(trackingId),String.valueOf(amount.doubleValue()),ActionEntry.ACTION_AUTHORISTION_CANCLLED,ActionEntry.STATUS_AUTHORISTION_CANCLLED);
                    entry.closeConnection();
                    // End : Added for Action and Status Entry in Action History table

                }

            }
        }
        catch (SQLException e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    private static boolean writeToFile(String merchantId, String fileData, Calendar cal) throws IOException, SystemError
    {
        if (fileData != null && fileData.length() != 0)
        {
            String reconDate = getReconDate(cal);
            String reconFileStore = ApplicationProperties.getProperty("RECON_FILE_STORE") + "Recon_" + reconDate;
            log.debug("Recon File Store :  " + reconFileStore);
            File f = new File(reconFileStore);
            f.mkdirs();
            f = getReconFile(reconDate, merchantId, reconFileStore);
            FileWriter fw = null;
            try
            {
                log.debug(" Creating file " + f.getName());
                fw = new FileWriter(f);
                fw.write(fileData);
                fw.flush();
            }
            catch (IOException e)
            {
                Mail.sendAdminMail("Error while creating recon file", Util.getStackTrace(e));
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
                        log.error("Could not close file handler" ,e);
                    }
            }
            log.debug("File Uploaded : " + f.getName());

            try
            {
                uploadToFtp(f.getName(), fileData);
            }
            catch (IOException e)
            {
                //Not letting process stop due to FTP error
                Mail.sendAdminMail("Error while uploading recon file " + f.getName() + " on FTP server", Util.getStackTrace(e));
            }
            return true;
        }
        return false;
    }

    private static File getReconFile(String reconDate, String merchantId, String reconFileStore)
    {
        String fileName;
        File f;
        fileName = reconDate + "_" + merchantId + ".txt";
        f = new File(reconFileStore + "/" + fileName);
        return f;
    }

    private static String getFileData(GatewayAccount account, Calendar cal) throws SystemError
    {
        Connection conn = null;
        int totalRecords = 0;
        String merchantId = account.getMerchantId();
        int accountId = account.getAccountId();
        String currency = account.getCurrency();
        String currencyCode = ACIKitHandler.getCurrencyCode(currency);
        String formattedDate = getReconDate(cal);
        String fileHeader = "FH" + formattedDate + merchantId;
        String fileData = "";
        Hashtable<String, String> ht = getTimestamps(cal);
        String startTime = ht.get("starttime");
        String endTime = ht.get("endtime");
        try
        {
            conn = Database.getConnection();
            String query = "select LPAD(' ',20,' ') as reserved,LPAD(icicimerchantid,12,' ') as merchantid ,LPAD(icicitransid,50,' ') as trackid ,LPAD(capturereceiptno,15,' ') as refno ,LPAD(date_format(from_unixtime(dtstamp),'%Y%m%d'),10,' ') as transactiondate,LPAD(?,5,' ') as currency,LPAD('00',2,' ') as responsecode,LPAD('5',2,' ') as actioncode,date_format(timestamp,'%H%i%S') as time,LPAD(captureid,18,' ') as transid,LPAD(captureamount,20,' ') as amount,LPAD('CAPTURED',20,' ') as resultcode,LPAD(authcode,6,' ') as authcode from transaction_icicicredit where status = 'capturesuccess' and accountid =?";
            //String query = "select LPAD(' ',20,' ') as reserved,LPAD(icicimerchantid,12,' ') as merchantid ,LPAD(icicitransid,50,' ') as trackid ,LPAD(capturereceiptno,15,' ') as refno ,LPAD(date_format(from_unixtime(dtstamp),'%Y%m%d'),10,' ') as transactiondate,LPAD('" + currencyCode + "',5,' ') as currency,LPAD('00',2,' ') as responsecode,LPAD('5',2,' ') as actioncode,date_format(timestamp,'%H%i%S') as time,LPAD(captureid,18,' ') as transid,LPAD(amount,20,' ') as amount,LPAD('CAPTURED',20,' ') as resultcode,LPAD(authcode,6,' ') as authcode from transaction_icicicredit where unix_timestamp(timestamp)>=" + startTime + " and unix_timestamp(timestamp)<=" + endTime + " and status = 'capturesuccess' and accountid =" + accountId;
            PreparedStatement p1=conn.prepareStatement(query);
            p1.setString(1,currencyCode);
            p1.setInt(2,accountId);
            ResultSet rs = p1.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next())
            {
                String rowData = "";
                int count = 1;
                while (rsmd.getColumnCount() >= count)
                {
                    log.debug("Column Count : " + rsmd.getColumnCount());
                    rowData += rs.getString(count++);
                }
                fileData += rowData + "\r\n";
                totalRecords++;

            }

        }
        catch (SQLException e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        // Append Footer Data
        if (totalRecords > 0)
        {
            fileData = fileHeader + "\r\n" + fileData + "FT" + totalRecords;
        }
        log.debug(" File Data : " + fileData);
        return fileData;
    }

    private static String getReconDate(Calendar cal)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        java.util.Date currentDate = cal.getTime();
        return dateFormat.format(currentDate);
    }

    public static void main(String[] args) throws Exception
    {
        Logger logger =new Logger(PzEncryptor.class.getName());
         //createReconFile(23,06,2012);
         /*settleSBMTransactions(23,06,2012);
        settleSBMTransactions(24,06,2012);*/
        if (args.length == 4)
        {
            String action = args[0];
            int date = 23;
            int month = Integer.parseInt(args[2]);
            int year = Integer.parseInt(args[3]);

            if ("settle".equalsIgnoreCase(action))
            {
                settleSBMTransactions(date, month, year);
            }
            else if ("recon".equalsIgnoreCase(action))
            {
                createReconFile(date, month, year);
            }
            else
            {
                logger.debug(" Actions : SETTLE/RECON ");
            }
        }
        else
        {
            logger.debug(" Usage : <SETTLE/RECON> dd mm yyyy");
        }
    }

    private static Hashtable<String, String> getTimestamps(Calendar cal)
    {
        int date = cal.get(Calendar.DATE);
        cal.set(Calendar.HOUR, 10);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DATE, -1);
        Hashtable<String, String> ht = new Hashtable<String, String>();
        ht.put("starttime", String.valueOf(cal.getTimeInMillis() / 1000));
        log.debug("Local Server Start Time" + cal.getTime());
        cal.add(Calendar.DATE, 1);
        ht.put("endtime", String.valueOf(cal.getTimeInMillis() / 1000));
        log.debug("Local Server End Time" + cal.getTime());
        return ht;

    }

    private static List<GatewayAccount> getSBMAccounts()
    {
        List<GatewayAccount> allAccounts = AbstractPaymentGateway.getAccounts();
        List<GatewayAccount> allSBMAccounts = new ArrayList<GatewayAccount>();
        for (GatewayAccount account : allAccounts)
        {
            String gatewayType = account.getGateway();
            if (SBMPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                allSBMAccounts.add(account);
            }
        }
        return allSBMAccounts;
    }


}
