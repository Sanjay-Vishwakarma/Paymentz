package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.paymentgateway.*;
import com.jcraft.jsch.*;

import java.util.*;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.math.BigDecimal;
import org.apache.naming.NamingContextBindingsEnumeration;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 23, 2010
 * Time: 5:42:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudReportProcessor
{
    private static Logger log = new Logger(FraudReportProcessor.class.getName());
    private static final int NOT_DOWNLOADED = 0;
    private static final int NOT_SAVED_IN_DB = 1;
    private static final int NOT_PARSED = 2;
    private static final int PROCESSED = 999;

    private int stage = NOT_DOWNLOADED;

    public static void main(String[] args)
    {
        try
        {
            FraudReportProcessor f = new FraudReportProcessor();
            List<GatewayAccount> allSBMAccounts = getSBMAccounts();
            Date d = new Date();
            if (args.length == 0)
            {

                for (GatewayAccount account : allSBMAccounts)
                {
                    f.processFraudReport(d, 'V', account);
                    f.processFraudReport(d, 'M', account);
                    f.processChargebackReport(d, 'V', account);
                    f.processChargebackReport(getDatForMaster(d), 'M', account);
                }

            }
            else if (args.length >= 3)
            {

                int date = Integer.parseInt(args[0]);
                int month = Integer.parseInt(args[1]);
                int year = Integer.parseInt(args[2]);

                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, date);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                d = cal.getTime();
                Calendar today = Calendar.getInstance();

                do
                {
                if (args.length == 4)
                {
                    String accId = args[3];
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accId);

                    f.processFraudReport(d, 'V', account);
                    f.processFraudReport(d, 'M', account);
                    f.processChargebackReport(d, 'V', account);
                    f.processChargebackReport(getDatForMaster(d), 'M', account);

                }
                else
                {
                    for (GatewayAccount account : allSBMAccounts)
                    {
                        f.processFraudReport(d, 'V', account);
                        f.processFraudReport(d, 'M', account);
                        f.processChargebackReport(d, 'V', account);
                        f.processChargebackReport(getDatForMaster(d), 'M', account);
                    }
                }
              cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            while (!(cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)));
            }
            else if (args.length == 1 && "All".equalsIgnoreCase(args[0]))
            {

                File[] files = getFileListFromSystem(ApplicationProperties.getProperty("FRAUD_FILE_STORE"));
                log.debug("got files " + files);
                f.stage = NOT_SAVED_IN_DB;
                for (File file : files)
                {
                    if (file.isFile())
                        try
                        {
                            //passing account as null as not going to used as file is already downloaded.
                            f.processFraudReport(file.getName(), null);
                            //Moving file to temp folder
                            file.renameTo(new File(ApplicationProperties.getProperty("FRAUD_FILE_STORE") + "/temp/" + file.getName()));
                        }
                        catch (SystemError systemError)
                        {
                            log.error("Exception is raised while processing file " ,systemError);
                        }
                }

            }
            else if (args.length == 1 && "AllCB".equalsIgnoreCase(args[0]))
            {

                File[] files = getFileListFromSystem(ApplicationProperties.getProperty("CHARGEBACK_FILE_STORE"));
                log.debug("got files " + files);
                f.stage = NOT_SAVED_IN_DB;
                for (File file : files)
                {
                    if (file.isFile())
                        try
                        {
                            //passing already downloaded files.
                            //processChargebackFile(file.getName());
                            f.processChargebackReport(file.getName(), null);
                            //Moving file to temp folder
                            file.renameTo(new File(ApplicationProperties.getProperty("CHARGEBACK_FILE_STORE") + "/temp/" + file.getName()));
                        }
                        catch (SystemError systemError)
                        {
                            log.error("Exception is raised while processing file ",systemError);
                        }
                }

            }

        }
        catch (SystemError systemError)
        {
            log.error("Exception is raised In FraudReportProcessor :::::::: " ,systemError);
        }
    }

    public static List<GatewayAccount> getSBMAccounts()
    {
        List<GatewayAccount> allAccounts = AbstractPaymentGateway.getAccounts();
        List<GatewayAccount> allSBMAccounts = new ArrayList<GatewayAccount>();
        for (GatewayAccount account : allAccounts)
        {
            String gatewayType = account.getGateway();
            if (com.directi.pg.core.paymentgateway.SBMPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {
                allSBMAccounts.add(account);
            }
        }
        return allSBMAccounts;
    }

    public void processFraudReports(Hashtable ht) throws SystemError
    {
        try
        {
            List<GatewayAccount> allSBMAccounts = getSBMAccounts();
            Date d = new Date();
            log.info("Running Fraud Report processor for " + d);
            for (GatewayAccount account : allSBMAccounts)
            {
                processFraudReport(new Date(), 'V', account);
                processFraudReport(new Date(), 'M', account);

                processChargebackReport(new Date(), 'V', account);
                processChargebackReport(new Date(), 'M', account);
            }
            d.setDate(d.getDate() + 1);
            log.info("Running Fraud Report processor for " + d);

            for (GatewayAccount account : allSBMAccounts)
            {
                processFraudReport(d, 'V', account);
                processFraudReport(d, 'M', account);

                processChargebackReport(d, 'V', account);
                processChargebackReport(getDatForMaster(d), 'M', account);
            }
        }
        catch (SystemError systemError)
        {
            log.error("Exception in processFraudReports " ,systemError);
            Mail.sendAdminMail("Error in Cron - processFraudReports ", Util.getStackTrace(systemError));
        }

    }

    public void processFraudReport(Date d, char schemeFlag, GatewayAccount account) throws SystemError
    {

        String fileName = getMonthlyFraudFileName(d, schemeFlag, account);
        processChargebackReport(fileName, account);
        /*DownLoadAndSaveChargeBackFileFromSFTP(fileName, account);
        if (this.stage == NOT_SAVED_IN_DB)
        {
            processChargebackFile(fileName);
        }*/

    }

    public void processChargebackReport(Date d, char schemeFlag, GatewayAccount account) throws SystemError
    {

        String fileName = getMonthlyChargbackFileName(d, schemeFlag, account);
        DownLoadAndSaveChargeBackFileFromSFTP(fileName, account);
        processChargebackFile(fileName);

    }

    private static String getMonthlyFraudFileName(Date d, char schemeFlag, GatewayAccount account)
    {
        Calendar cal = getMauritainCalendar(d);
        String shortName = account.getFRAUD_FILE_SHORT_NAME();
        if (Functions.checkStringNull(shortName) == null)
            shortName = ApplicationProperties.getProperty("FRAUD_FILE_SHORT_NAME");
        return shortName + getFraudReportDate(cal) + "FW" + schemeFlag + ".TXT";
        // return "TRI1203FRV.TXT";
    }

    private static String getMonthlyChargbackFileName(Date d, char schemeFlag, GatewayAccount account)
    {
        Calendar cal = getMauritainCalendar(d);
        String shortName = account.getFRAUD_FILE_SHORT_NAME();
        if (Functions.checkStringNull(shortName) == null)
            shortName = ApplicationProperties.getProperty("FRAUD_FILE_SHORT_NAME");
        return shortName + getChargebackReportDate(cal) + schemeFlag + ".TXT";
        // return "TRI110701V.TXT";
    }

    public void processFraudReport(String fileName, GatewayAccount account) throws SystemError
    {
        log.debug("Trying to fetch with file name  " + fileName);
        checkProcesingStage(fileName);
        if (stage == NOT_DOWNLOADED)
            DownLoadAndSaveFraudFileFromSFTP(fileName, account);
        if (stage == NOT_SAVED_IN_DB)
            saveDatInDb(fileName);
        if (stage == NOT_PARSED)
            processFraudFile(fileName);

        log.debug("Fraud file processed.");

    }

    public void processChargebackReport(String fileName, GatewayAccount account) throws SystemError
    {
        log.info("Trying to fetch with file name  " + fileName);
        checkChargeBackProcesingStage(fileName);
        if (stage == NOT_DOWNLOADED)
            DownLoadAndSaveChargeBackFileFromSFTP(fileName, account);
        if (stage == NOT_SAVED_IN_DB)
            saveDatInDbForChargeback(fileName);
        if (stage == NOT_PARSED)
            processChargebackFile(fileName);

        log.info("Chargeback file processed.");

    }

    private void DownLoadAndSaveFraudFileFromSFTP(String fileName, GatewayAccount account) throws SystemError
    {
        log.debug("Inside DownLoadAndSaveFraudFile");


        String sftpSite;
        String sftpPath;
        String sftpUsername;
        String sftpPassword;
        String fraudFileStore = ApplicationProperties.getProperty("FRAUD_FILE_STORE");

        if (Functions.checkStringNull(account.getFRAUD_FTP_SITE()) == null)
        {
            sftpSite = ApplicationProperties.getProperty("FRAUD_FTP_SITE");
            sftpPath = ApplicationProperties.getProperty("FRAUD_FTP_PATH");
            sftpUsername = ApplicationProperties.getProperty("FRAUD_FTP_USERNAME");
            sftpPassword = ApplicationProperties.getProperty("FRAUD_FTP_PASSWORD");

        }
        else
        {
            sftpSite = account.getFRAUD_FTP_SITE();
            sftpPath = account.getFRAUD_FTP_PATH();
            sftpUsername = account.getFRAUD_FTP_USERNAME();
            sftpPassword = account.getFRAUD_FTP_PASSWORD();

        }

        log.debug("Fraud File local Store :  " );

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        try
        {
            log.debug("FRAUD_FTP_SITE  " + sftpSite);
            log.debug("sftpPath " + sftpPath);
            session = jsch.getSession(sftpUsername, sftpSite, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sftpPassword);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            sftpChannel.cd(sftpPath);
            log.debug("Remote location " + sftpChannel.pwd());

            sftpChannel.get(fileName, fraudFileStore + fileName);

            log.debug("Fraud report File " + fileName + " fetched");
            this.stage = NOT_SAVED_IN_DB;

            log.debug("File Saved : " + fraudFileStore + "/" + fileName);
        }
        catch (JSchException e)
        {
            log.error("Fraud report File " + fileName + " download Failed ",e);
            Mail.sendAdminMail("Error while downloading fraud file " + fileName + " from SFTP server", Util.getStackTrace(e));
        }
        catch (SftpException e)
        {
            //Not letting process stop due to FTP error
            if (e.id == 2)
            {
                log.debug("Fraud report File " + fileName + " not found on server ");
                log.debug("Error message from server " + Util.getStackTrace(e));
            }
            else
            {
                log.error("Fraud report File " + fileName + " download Failed ",e);
                Mail.sendAdminMail("Error while downloading fraud file " + fileName + " from SFTP server", Util.getStackTrace(e));
            }
            return;
        }
        finally
        {

            if (sftpChannel != null)
                sftpChannel.exit();
            if (session != null)
                session.disconnect();

        }


    }

    private void DownLoadAndSaveFraudFileFromFTP(String fileName) throws SystemError
    {
        log.debug("Inside DownLoadAndSaveFraudFile");
        String ftpSite = ApplicationProperties.getProperty("FRAUD_FTP_SITE");
        String ftpUsername = ApplicationProperties.getProperty("FRAUD_FTP_USERNAME");
        String ftpPassword = ApplicationProperties.getProperty("FRAUD_FTP_PASSWORD");

        StringBuilder fileData = new StringBuilder();
        try
        {
            String url = "ftp://" + ftpUsername + ":" + URLEncoder.encode(ftpPassword) + "@" + ftpSite + "/" + fileName;
            log.debug(" URL : " + url);
            URL ftpurl = new URL(url);
            URLConnection con = ftpurl.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line = br.readLine();

            while (line != null)
            {
                fileData.append(line);
                fileData.append("\r\n");
                line = br.readLine();
            }
            br.close();
            log.debug("Fraud report File " + fileName + " fetched");

        }
        catch (FileNotFoundException fnfe)
        {
            log.error("Fraud report File " + fileName + " not found on server",fnfe);
            return;
        }
        catch (IOException e)
        {
            //Not letting process stop due to FTP error
            log.error("Fraud report File " + fileName + " download Failed ",e);
            Mail.sendAdminMail("Error while downloading fraud file " + fileName + " from FTP server", Util.getStackTrace(e));
            return;
        }

        String fraudFileStore = ApplicationProperties.getProperty("FRAUD_FILE_STORE");
        log.debug("Fraud File local Store :  " + fraudFileStore);
        File f = new File(fraudFileStore);
        f.mkdirs();
        f = new File(fraudFileStore + "/" + fileName);
        FileWriter fw = null;
        try
        {
            log.debug(" Creating file " + f.getName());
            fw = new FileWriter(f);
            fw.write(fileData.toString());
            fw.flush();
            this.stage = NOT_SAVED_IN_DB;
            log.debug("File Saved : " + f.getAbsolutePath());
        }
        catch (IOException e)
        {
            log.error("Error while saving fraud file",e);
            Mail.sendAdminMail("Error while saving fraud file", Util.getStackTrace(e));
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
                    log.error("Could not close file handler",e);
                }
        }

        log.debug("leaving DownLoadAndSaveFraudFile");

    }

    private void DownLoadAndSaveChargeBackFileFromSFTP(String fileName, GatewayAccount account) throws SystemError
    {
        log.debug("Inside DownLoadAndSaveChargeBackFileFromSFTP");


        String sftpSite;
        String sftpPath;
        String sftpUsername;
        String sftpPassword;
        String fraudFileStore = ApplicationProperties.getProperty("CHARGEBACK_FILE_STORE");

        if (Functions.checkStringNull(account.getCHARGEBACK_FTP_PATH()) == null)
        {
            sftpSite = ApplicationProperties.getProperty("FRAUD_FTP_SITE");
            sftpPath = ApplicationProperties.getProperty("CHARGEBACK_FTP_PATH");
            sftpUsername = ApplicationProperties.getProperty("FRAUD_FTP_USERNAME");
            sftpPassword = ApplicationProperties.getProperty("FRAUD_FTP_PASSWORD");

        }
        else
        {
            sftpSite = account.getFRAUD_FTP_SITE();
            sftpPath = account.getCHARGEBACK_FTP_PATH();
            sftpUsername = account.getFRAUD_FTP_USERNAME();
            sftpPassword = account.getFRAUD_FTP_PASSWORD();

        }

        log.debug("Chargeback File local Store :  " + fraudFileStore);

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        try
        {
            log.debug("Chargeback_FTP_SITE  " + sftpSite);
            log.debug("sftpPath " + sftpPath);
            session = jsch.getSession(sftpUsername, sftpSite, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sftpPassword);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            sftpChannel.cd(sftpPath);
            log.debug("Remote location " + sftpChannel.pwd());

            sftpChannel.get(fileName, fraudFileStore + fileName);

            log.debug("Chargeback report File " + fileName + " fetched");
            this.stage = NOT_SAVED_IN_DB;

            log.debug("File Saved : " + fraudFileStore + "/" + fileName);
        }
        catch (JSchException e)
        {
            log.error("Chargeback report File " + fileName + " download Failed ",e);
            Mail.sendAdminMail("Error while downloading Chargeback file " + fileName + " from SFTP server", Util.getStackTrace(e));
        }
        catch (SftpException e)
        {
            //Not letting process stop due to FTP error
            if (e.id == 2)
            {
                log.debug("Chargeback report File " + fileName + " not found on server ");
                log.debug("Error message from server " + Util.getStackTrace(e));
            }
            else
            {
                log.error("Chargeback report File " + fileName + " download Failed ",e);
                Mail.sendAdminMail("Error while downloading Chargeback file " + fileName + " from SFTP server", Util.getStackTrace(e));
            }
            return;
        }
        finally
        {

            if (sftpChannel != null)
                sftpChannel.exit();
            if (session != null)
                session.disconnect();

        }


    }

    private void saveDatInDb(String fileName) throws SystemError
    {
        log.debug("Inside saveDatInDb");
        checkProcesingStage(fileName);
        if (this.stage != NOT_SAVED_IN_DB)
        {
            log.debug("Leaving saveDatInDb as current stage is not correct.");
            return;
        }

        Connection conn = null;
        try
        {
            String fraudFileStore = ApplicationProperties.getProperty("FRAUD_FILE_STORE");
            File f = new File(fraudFileStore + "/" + fileName);

            log.debug("Reading file " + fraudFileStore + "/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line = null;
            int lineCount = 0;
            StringBuilder fileData = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                fileData.append(line);
                fileData.append("\r\n");
                lineCount++;
            }
            br.close();

            conn = Database.getConnection();
            log.debug("Inserting file data in history table");
            String query="insert into fraud_report_process_history (file_name,filedata,no_of_rows) values (?,?,?)";
            PreparedStatement pstmt=conn.prepareStatement(query);
            pstmt.setString(1,fileName);
            pstmt.setString(2,fileData.toString());
            pstmt.setInt(3,lineCount);
           pstmt.executeUpdate();
            this.stage = NOT_PARSED;
        }
        catch (Exception e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving saveDatInDb");
    }

    private void saveDatInDbForChargeback(String fileName) throws SystemError
    {
        log.debug("Inside saveDatInDbForChargeback");
        checkChargeBackProcesingStage(fileName);
        if (this.stage != NOT_SAVED_IN_DB)
        {
            log.debug("Leaving saveDatInDb as current stage is not correct.");
            return;
        }

        Connection conn = null;
        try
        {
            String fraudFileStore = ApplicationProperties.getProperty("CHARGEBACK_FILE_STORE");
            File f = new File(fraudFileStore + "/" + fileName);

            log.debug("Reading file " + fraudFileStore + "/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line = null;
            int lineCount = 0;
            StringBuilder fileData = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                fileData.append(line);
                fileData.append("\r\n");
                lineCount++;
            }
            br.close();

            conn = Database.getConnection();
            log.debug("Inserting file data in history table");
            String query="insert into chargeback_report_process_history (file_name,filedata,no_of_rows) values (?,?,?)";
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,fileName);
            p.setString(2,fileData.toString());
            p.setInt(3,lineCount);
            p.executeUpdate();
            this.stage = NOT_PARSED;
        }
        catch (Exception e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving saveDatInDbForChargeback");
    }

    private static void processFraudFile(String fileName) throws SystemError
    {
        log.debug("Inside processFraudFile");
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String qry="select * from fraud_report_process_history where file_name=?";
            PreparedStatement p=conn.prepareStatement(qry);
            p.setString(1,fileName);
            ResultSet rs = p.executeQuery();
            int fraud_process_id = 0;
            String fileData = null;

            if (rs.next())
            {
                if ("N".equalsIgnoreCase(rs.getString("parsed")))
                {
                    fraud_process_id = rs.getInt("fraud_process_id");
                    fileData = rs.getString("filedata");
                    BufferedReader br = new BufferedReader(new StringReader(fileData));

                    String line = null;
                    List RefNoList = new ArrayList();
                    while ((line = br.readLine()) != null)
                    {
                        String referenceNo = getRefNo(line, 2);
                        RefNoList.add(referenceNo);
                        //ading ref no with 1 minuse to original
                        referenceNo = getRefNoNew(line, 2);
                        RefNoList.add(referenceNo);
                    }
                    br.close();

                    if (RefNoList.size() > 0)
                    {
                        //execute query for all ARN got above
                        String query = "select T.toid,T.description,T.amount,T.icicitransid,T.accountid,T.status,T.capturereceiptno from transaction_icicicredit T where " +
                                "capturereceiptno in(" + Util.getDelimitedList(RefNoList.toArray(), ",") + ")";



                        rs = Database.executeQuery(query, conn);

                        StringBuilder mailContent = new StringBuilder();
                        mailContent.append("Following transactions were received in fraud report. Please check and take necessary action. Please update status in fraud_transaction_list table\r\n");
                        mailContent.append("To Id\t\tTracking ID\t\torder Id\r\n");
                        int count = 0;
                        while (rs.next())
                        {
                            String toid = rs.getString("toid");
                            String description = rs.getString("description");
                            String amount = rs.getString("amount");
                            String icicitransid = rs.getString("icicitransid");
                            String accountId = rs.getString("accountid");
                            String status = rs.getString("status");
                            String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
                            count++;
                            char processed = 'Y';
                            if ("settled".equals(status))
                            {  processed = 'N';
                            }
                                String q="select * from fraud_transaction_list where icicitransid=?";
                                PreparedStatement pst=conn.prepareStatement(q);
                                pst.setString(1, icicitransid);
                                ResultSet rs1 = pst.executeQuery();
                                if (!rs1.next())
                                {
                                    log.debug("inserting fraud transaction details in fraud list table");
                                    String qu="insert into fraud_transaction_list(icicitransid,description,amount,merchantId,toid,processed,fk_fraud_process_id) values" +
                                            "(?,?,?,?,?,?,?)";
                                    PreparedStatement pa=conn.prepareStatement(qu);
                                    pa.setString(1,icicitransid);
                                    pa.setString(2,description);
                                    pa.setString(3,amount);
                                    pa.setString(4,merchantId);
                                    pa.setString(5,toid);
                                    pa.setInt(6,processed);
                                    pa.setInt(7,fraud_process_id);
                                    pa.executeUpdate();

                                    mailContent.append(toid + "\t\t" + icicitransid + "\t\t" + description);
                                }



                            RefNoList.remove(rs.getString("capturereceiptno"));
                        //Remove other ref no also from list
                            RefNoList.remove(getAnotherRefNo(rs.getString("capturereceiptno")));

                        }

                        Database.executeUpdate("update fraud_report_process_history set no_of_transactions=" + count + ",parsed='Y'," +
                                "unprocessed_transactions='" + Util.getDelimitedList(RefNoList.toArray(), ",") + "' where fraud_process_id =" + fraud_process_id, conn);

                        log.debug("Mail content " + mailContent);
                        Mail.sendAdminMail("Fraud Transactions List", mailContent.toString());
                    }

                }
                else
                    log.debug("File " + fileName + " is already parsed");
            }
        }
        catch (Exception e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving processFraudFile");
    }

    private static void processChargebackFile(String fileName) throws SystemError
    {
        log.debug("Inside processChargebackFile");
        Transaction transaction = new Transaction();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from chargeback_report_process_history where file_name='" + fileName + "'", conn);
            int chargeback_process_id = 0;
            String fileData = null;

            if (rs.next())
            {
                if ("N".equalsIgnoreCase(rs.getString("parsed")))
                {
                    chargeback_process_id = rs.getInt("chargeback_process_id");
                    fileData = rs.getString("filedata");
                    BufferedReader br = new BufferedReader(new StringReader(fileData));

                    StringBuilder mailContent = new StringBuilder();
                    mailContent.append("Following transactions were received in chargeback report. Please check and take necessary action.\r\n");
                    mailContent.append("ARN\t\tTo Id\t\tTracking ID\t\torder Id\t\tcapturereceiptno\r\n");

                    String line = null;
                    boolean error = false;
                    List RefNoList = new ArrayList();
                    int count = 0;

                    while ((line = br.readLine()) != null)
                    {
                        String referenceNo = getRefNo(line, 8);
                        String referenceNoNew = getRefNoNew(line, 8);

                        String[] fields = line.split("\\|");
                        String date = fields[0];
                        String ARN = fields[8];
                        String authcode = fields[4];

                        String cbamount = new BigDecimal(fields[7].trim()).divide(new BigDecimal(100)).toString();
                        String cbreason = fields[5];
                        String partial = Functions.checkStringNull(fields[9]);
                        String chargeBackIndicator = Functions.checkStringNull(fields[10]);

                        String query = "select T.toid,T.description,T.amount,T.icicitransid,T.accountid,T.status,T.capturereceiptno from transaction_icicicredit T where " +
                                "capturereceiptno in ('" + referenceNo + "','" + referenceNoNew + "') and authcode='" + authcode + "'";
                        ResultSet rs1 = Database.executeQuery(query, conn);

                        if (rs1.next())
                        {
                            String toid = rs1.getString("toid");
                            String description = rs1.getString("description");
                            String icicitransid = rs1.getString("icicitransid");
                            String accountId = rs1.getString("accountid");
                            String status = rs1.getString("status");
                            String capturereceiptno = rs1.getString("capturereceiptno");
                            String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
                            count++;
                            try
                            {
                                ResultSet rs2 = Database.executeQuery("select * from chargeback_transaction_list where icicitransid=" + icicitransid, conn);
                                if (!rs2.next())
                                {
                                    log.debug("inserting chargeback transaction details in chargeback list table");
                                    Database.executeUpdate("insert into chargeback_transaction_list(icicitransid,description,amount,merchantId,toid,fk_chargeback_process_id) values" +
                                            "(" + icicitransid + ",'" + description + "'," + cbamount + "," + merchantId + "," + toid + "," + chargeback_process_id + ")", conn);

                                }


                                if ("ADJM".equalsIgnoreCase(chargeBackIndicator) && "settled".equals(status))
                                {
                                    transaction.processChargeback(icicitransid, date, fileName.substring(0, fileName.length() - 4) + "-" + Calendar.getInstance().getTimeInMillis(), cbamount, cbreason, partial);
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid, conn);

                                }
                                //for chargeback reversal
                                else if ("CBRV".equalsIgnoreCase(chargeBackIndicator) && "chargeback".equals(status))
                                {

                                    //call to reverse chargeback transaction
                                    //Marked as processed when revered.
                                    //Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid, conn);

                                }
                                //For automatic Representment
                                else if ("AUTO".equalsIgnoreCase(chargeBackIndicator) && "reversed".equals(status))
                                {
                                    // No need to do anything, marked as processed
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid, conn);
                                }
                                else if ("ADJM".equalsIgnoreCase(chargeBackIndicator) && "chargeback".equals(status))
                                {
                                    // No need to do anything, marked as processed
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid, conn);
                                }
                                else
                                {
                                   error = true;
                                   mailContent.append(ARN + "\t\t" + toid + "\t\t" + icicitransid + "\t\t" + description + "\t\t" + capturereceiptno + "\r\n");
                                }

                            }
                            catch (Exception systemError)
                            {
                                error = true;
                                log.error(Util.getStackTrace(systemError));
                                RefNoList.add(capturereceiptno);
                                mailContent.append(ARN + "\t\t" + toid + "\t\t" + icicitransid + "\t\t" + description + "\t\t" + capturereceiptno + "\r\n");
                            }
                        }
                        else
                        {
                            error = true;
                            mailContent.append(ARN + "\t\t \t\t \t\t ");
                            RefNoList.add(referenceNo);
                            RefNoList.add(referenceNoNew);
                        }
                    }
                    br.close();

                    Database.executeUpdate("update chargeback_report_process_history set no_of_transactions=" + count + ",parsed='Y'," +
                            "unprocessed_transactions='" + Util.getDelimitedList(RefNoList.toArray(), ",") + "' where chargeback_process_id =" + chargeback_process_id, conn);

                    if (error)
                    {
                        log.debug("Mail content " + mailContent);
                        Mail.sendAdminMail("Not processed chargeback Transactions List ", mailContent.toString());
                    }

                }
                else
                    log.info("File " + fileName + " is already parsed");
            }
        }
        catch (Exception e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving processChargebackFile");
    }

    /*@Deprecated
    private static void processChargebackFile(String fileName) throws SystemError
    {
        log.debug("Inside processChargebackFile");
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String chargebackFileStore = ApplicationProperties.getProperty("CHARGEBACK_FILE_STORE");
            File f = new File(chargebackFileStore + "/" + fileName);


            File tempFile = new File(chargebackFileStore + "/Failed/" + fileName.substring(0,fileName.length()-4) + "_FAILED.TXT");
            FileWriter tempFileWriter = null;

            log.debug("Reading file " + chargebackFileStore + "/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(f));

            StringBuilder mailContent = new StringBuilder();
            mailContent.append("Following transactions were received in chargeback report. Please check and take necessary action.\r\n");
            mailContent.append("ARN\t\tTo Id\t\tTracking ID\t\torder Id\r\n");

            boolean error = false;
            String line = null;
            while ((line = br.readLine()) != null)
            {
                String referenceNo = getRefNo(line, 8);
                String[] fields = line.split("\\|");
                String date = fields[0];
                String ARN = fields[8];
                String authcode = fields[4];

                String cbamount = new BigDecimal(fields[7].trim()).divide(new BigDecimal(100)).toString();
                String cbreason = fields[5];
                String partial = Functions.checkStringNull(fields[9]);
                String chargeBackIndicator = Functions.checkStringNull(fields[10]);


                if (Functions.checkStringNull(referenceNo) != null && "ADJM".equalsIgnoreCase(chargeBackIndicator))
                {
                    //execute query for all ARN got above
                    String query = "select T.toid,T.description,T.amount,T.icicitransid,T.accountid,T.status,T.capturereceiptno from transaction_icicicredit T where " +
                            "capturereceiptno =? and authcode=?";
                    PreparedStatement p1=conn.prepareStatement(query);
                    p1.setString(1,referenceNo);
                    p1.setString(2,authcode);
                    ResultSet rs = p1.executeQuery();

                    if (rs.next())
                    {
                        String toid = rs.getString("toid");
                        String description = rs.getString("description");
                        String icicitransid = rs.getString("icicitransid");
                        String status = rs.getString("status");

                        if ("settled".equals(status))
                        {
                            try
                            {
                                Transaction.processChargeback(icicitransid, date, fileName, cbamount, cbreason, partial);
                            }
                            catch (Exception systemError)
                            {
                                error = true;
                                mailContent.append(ARN + "\t\t" + toid + "\t\t" + icicitransid + "\t\t" + description);
                                tempFile.createNewFile();
                                if(tempFileWriter==null)
                                    tempFileWriter = new FileWriter(tempFile);

                                tempFileWriter.write(line + "\r\n");

                            }

                        }

                    }
                    else
                    {
                        error = true;
                        mailContent.append(ARN + "\t\t \t\t \t\t ");
                        tempFile.createNewFile();
                        if (tempFileWriter == null)
                        {
                            log.debug("Error in processing record. Creating Failed file");
                            tempFileWriter = new FileWriter(tempFile);
                        }

                        tempFileWriter.write(line + "\r\n");
                    }

                }
            }

            if (error)
            {
                log.debug("Mail content " + mailContent);
                Mail.sendAdminMail("Fraud Transactions List", mailContent.toString());
            }

            log.debug("Moving file to temp folder");
            //Move file to temp folder as its processed.
            f.renameTo(new File(chargebackFileStore + "/temp/" + f.getName()));



            if (tempFileWriter != null)
                tempFileWriter.close();

            br.close();

        }
        catch (Exception e)
        {
            throw new SystemError("Error :  " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

        log.debug("Leaving processChargebackFile");
    }*/

    private static String getRefNo(String line, int pos)
    {
        String[] fields = line.split("\\|");
        String ARN = fields[pos];
        String referenceNo = null;
        if (ARN != null)
        {
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            String singleDigitYear = year.substring(year.length() - 1);

            referenceNo = singleDigitYear + ARN.substring(11, ARN.length() - 1);

        }

        return referenceNo;
    }
    private static String getRefNoNew(String line, int pos)
    {
        String[] fields = line.split("\\|");
        String ARN = fields[pos];
        String referenceNo = null;
        if (ARN != null)
        {
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1);
            String singleDigitYear = year.substring(year.length() - 1);

            referenceNo = singleDigitYear + ARN.substring(11, ARN.length() - 1);

        }

        return referenceNo;
    }
    private void checkProcesingStage(String fileName) throws SystemError
    {
        log.debug("Inside checkProcesingStage ");
        Connection conn = null;

        String fraudFileStore = ApplicationProperties.getProperty("FRAUD_FILE_STORE");
        File f = new File(fraudFileStore + "/" + fileName);

        //check if file is already downloaded
        if (!f.exists())
        {
            this.stage = NOT_DOWNLOADED;
        }
        else
        {
            try
            {
                conn = Database.getConnection();
                String q1= "select parsed from fraud_report_process_history where file_name=?";
                PreparedStatement p1=conn.prepareStatement(q1);
                p1.setString(1,fileName);
                ResultSet rs = p1.executeQuery();

                //Check if record exists
                if (rs.next())
                {
                    if ("N".equalsIgnoreCase(rs.getString("parsed")))
                    {
                        this.stage = NOT_PARSED;
                    }
                    else
                    {
                        //don't need to do anything so no other stage neeed
                        this.stage = PROCESSED;
                    }

                }
                else //No record exist
                {
                    this.stage = NOT_SAVED_IN_DB;
                }

            }
            catch (Exception e)
            {
                throw new SystemError("Error :  " + Util.getStackTrace(e));
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }

        log.debug("Leaving checkProcesingStage with stage " + this.stage);
    }

private void checkChargeBackProcesingStage(String fileName) throws SystemError
    {
        log.debug("Inside checkChargeBackProcesingStage ");
        Connection conn = null;

        String chargebackFileStore = ApplicationProperties.getProperty("CHARGEBACK_FILE_STORE");

        File f = new File(chargebackFileStore + "/" + fileName);

        //check if file is already downloaded
        if (!f.exists())
        {
            this.stage = NOT_DOWNLOADED;
        }
        else
        {
            try
            {
                conn = Database.getConnection();
                ResultSet rs = Database.executeQuery("select parsed from chargeback_report_process_history where file_name='" + fileName + "'", conn);

                //Check if record exists
                if (rs.next())
                {
                    if ("N".equalsIgnoreCase(rs.getString("parsed")))
                    {
                        this.stage = NOT_PARSED;
                    }
                    else
                    {
                        //don't need to do anything so no other stage neeed
                        this.stage = PROCESSED;
                    }

                }
                else //No record exist
                {
                    this.stage = NOT_SAVED_IN_DB;
                }

            }
            catch (Exception e)
            {
                throw new SystemError("Error :  " + Util.getStackTrace(e));
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }

        log.debug("Leaving checkChargeBackProcesingStage with stage " + this.stage);
    }
    private static Calendar getMauritainCalendar(Date d)
    {
        TimeZone mauritiusTZ = TimeZone.getTimeZone("Indian/Mauritius");
        Calendar cal = Calendar.getInstance(mauritiusTZ);
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal;
    }

    private static String getFraudReportDate(Calendar cal)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
        java.util.Date currentDate = cal.getTime();
        return dateFormat.format(currentDate);
    }

    private static String getChargebackReportDate(Calendar cal)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        java.util.Date currentDate = cal.getTime();
        return dateFormat.format(currentDate);
    }

    private static File[] getFileListFromSystem(String fraudFileStore)
    {
        File[] files = null;
        File f = new File(fraudFileStore);
        if (f.isDirectory())
        {
            files = f.listFiles();
        }

        return files;
    }
  private static Date getDatForMaster(Date d)
    {

        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();

    }

 private static String getAnotherRefNo(String ref)
    {

        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String singleDigitYear = year.substring(year.length() - 1);
        year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1);
        String singleDigitPrevYear = year.substring(year.length() - 1);
        if (ref.startsWith(singleDigitYear))
            ref = singleDigitPrevYear + ref.substring(1);
        else if (ref.startsWith(singleDigitPrevYear))
            ref = singleDigitYear + ref.substring(1);

        return ref;
    }
}


