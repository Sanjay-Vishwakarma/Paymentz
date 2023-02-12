package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.directi.pg.core.GatewayAccountService;

import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Jimmy Mehta
 * Date: 21-May-2012
 * Time: 20:56:51
 * To change this template use File | Settings | File Templates.
 */
public class ChargebackReportProcessor
{
    private static Logger log = new Logger(ChargebackReportProcessor.class.getName());

    private File[] getFileListFromSystem(String chargeBackFileStore)
    {
        File[] files = null;
        File f = new File(chargeBackFileStore);
        if (f.isDirectory())
        {
            files = f.listFiles();
        }

        return files;
    }

    private String getRefNo(String line, int pos)
    {
        String[] fields = line.split("\\|");
        String ARN = fields[pos];
        String captureDate = fields[3];
        String referenceNo = null;
        if (ARN != null && captureDate != null)
        {
            /*  String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String singleDigitYear = year.substring(year.length() - 1);

                referenceNo = singleDigitYear + ARN.substring(11, ARN.length() - 1);*/
            referenceNo = captureDate.substring(captureDate.length()-1,captureDate.length()) + ARN.substring(11, ARN.length() - 1);
        }

        return referenceNo;
    }

    private String getRefNoNew(String line, int pos)
    {
        String[] fields = line.split("\\|");
        String ARN = fields[pos];
        String captureDate = fields[3];
        String referenceNo = null;
        if (ARN != null && captureDate != null)
        {
            String singleDigitYear = captureDate.substring(captureDate.length()-1,captureDate.length());
            int intSingleDigitYear=-1;
            try
            {
                intSingleDigitYear = Integer.parseInt(singleDigitYear);
            }
            catch(NumberFormatException nfe)
            {
                log.error("Error Occured in getRefNoNew(String,int) method = "+nfe);
            }
            if(intSingleDigitYear != -1)
            {
                if(intSingleDigitYear == 0)
                {
                    intSingleDigitYear = 9;
                }
                else
                {
                    intSingleDigitYear = intSingleDigitYear - 1;
                }
                referenceNo = intSingleDigitYear + ARN.substring(11, ARN.length() - 1);
            }
        }

        return referenceNo;
    }

    public static void main(String[] args)
    {
        ChargebackReportProcessor chrgBckRptProcessor1 = new ChargebackReportProcessor();
        chrgBckRptProcessor1.processChargebackReports(null);
    }

    public void processChargebackReports(Hashtable ht)
    {
        ChargebackReportProcessor chrgBckRptProcessor = new ChargebackReportProcessor();

        try
        {
            String sFreshFilePath = ApplicationProperties.getProperty("CHARGEBACK_FRESHFILE_STORE");
            String sProcessedFilePath = ApplicationProperties.getProperty("CHARGEBACK_PROCESSEDFILE_STORE");
            String sFailedFilePath = ApplicationProperties.getProperty("CHARGEBACK_FAILEDFILE_STORE");
            String sDailyProcessReportFilePath = ApplicationProperties.getProperty("CHARGEBACK_DAILYPROCESSREPORT_STORE");

            File[] files = chrgBckRptProcessor.getFileListFromSystem(sFreshFilePath);
            int cntFreshFileCounter =0;

            boolean isValidChargebackFileFormat = false;

            for (File file : files)
            {
                if (file.isFile())
                {
                   try
                   {
                       //Checking Filename
                       if(!chrgBckRptProcessor.isFileNameValid(file.getName()))
                       {
                          //Not the Chargeback File. So leave as it is in this folder.
                          continue;
                       }

                       cntFreshFileCounter++;
                       //Checking whether File is already processed
                       if(chrgBckRptProcessor.isFileProcessed(file.getName()))
                       {
                           //Already Processed File. Move File to Processed Folder
                           moveFile(sFreshFilePath,sProcessedFilePath, file.getName());
                           file.renameTo(new File(sProcessedFilePath + file.getName()));
                           continue;
                       }

                       //Checking File Format
                       isValidChargebackFileFormat = this.validateFileFormat(sFreshFilePath, file.getName());
                       if(!isValidChargebackFileFormat)
                       {
                           //Invalid File Format. Move File to FailedFolder
                           moveFile(sFreshFilePath,sFailedFilePath, file.getName());
                           file.renameTo(new File(sFailedFilePath + file.getName()));
                           continue;
                       }


                       if(!chrgBckRptProcessor.processChargebackFile(file))
                       {
                           //Exception while processing this file.
                           moveFile(sFreshFilePath,sFailedFilePath, file.getName());
                           file.renameTo(new File(sFailedFilePath + file.getName()));
                           continue;
                       }

                       //Successfully Processed.
                       file.renameTo(new File(sProcessedFilePath + file.getName()));                       
                   }
                   catch (Exception e)
                   {
                        log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
                   }
                }
            }
        }
        catch (Exception e)
        {
            log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
        }
    }

    private synchronized boolean validateFileFormat(String sFreshFilePath, String sFilename) throws SystemError
    {
        boolean isValidFile = false;
        Connection con = null;
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        try
        {

            fstream = new FileInputStream(sFreshFilePath + sFilename);
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            int rowCount = 0;
            StringBuilder fileData = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                String[] fields = line.split("\\|");
                if(fields.length != 12)
                {
                    br.close();
                    in.close();
                    fstream.close();
                    con = Database.getConnection();
                    Database.executeUpdate("update chargeback_report_process_history set parsed='X'" + " where file_name='"+ sFilename + "'", con);
                    return isValidFile;
                }
                rowCount++;
                fileData.append(line);
                fileData.append("\r\n");
            }
            con = Database.getConnection();
            Database.executeUpdate("update chargeback_report_process_history set filedata='"+fileData+"', no_of_rows=" + rowCount + " where file_name='"+ sFilename + "'", con);
            br.close();
            in.close();
            fstream.close();
            fileData = null;
            isValidFile=true;
        }
        catch(Exception e)
        {
            //throw new SystemError("Error :  " + Util.getStackTrace(e));
            log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
            isValidFile=false;
        }
        finally
        {
            Database.closeConnection(con);
        }
        fstream = null;
        in = null;
        br = null;
        return  isValidFile;
    }
    private boolean isFileProcessed(String fileName) throws SystemError
    {
        boolean isFileProcessed = false;
        Connection con = null;
        ResultSet rs = null;
        try
        {
            con = Database.getConnection();
            String q1= "select parsed from chargeback_report_process_history where file_name=?";
            PreparedStatement pS=con.prepareStatement(q1);
            pS.setString(1,fileName);
            rs = pS.executeQuery();
            if (rs.next())
            {
                if ("Y".equalsIgnoreCase(rs.getString("parsed")) || "Z".equalsIgnoreCase(rs.getString("parsed")))
                {
                    isFileProcessed=true;
                }
                else
                {
                    String q2 = "delete from chargeback_transaction_list where fk_chargeback_process_id=(select chargeback_process_id from chargeback_report_process_history where file_name='"+fileName +"')";
                    Database.executeUpdate(q2,con);
                }
            }
            else
            {
                String query ="insert into chargeback_report_process_history (file_name) values (?)";
                PreparedStatement pstmt=con.prepareStatement(query);
                pstmt.setString(1,fileName);
                pstmt.executeUpdate();
            }
        }
        catch(Exception e)
        {
            //throw new SystemError("Error :  " + Util.getStackTrace(e));
            log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isFileProcessed;
    }

    private boolean isFileNameValid(String fileName)
    {
        //Sample Valid Filename: TRI120521V.TXT
        boolean isValidFilename = false;

        if(fileName.length()!=14)
        {
            return isValidFilename;
        }
        if(!fileName.substring(10).equalsIgnoreCase(".TXT"))
        {
            return isValidFilename;
        }
        if((!fileName.substring(9,10).equalsIgnoreCase("V")) && (!fileName.substring(9,10).equalsIgnoreCase("M")))
        {
            return isValidFilename;
        }

        isValidFilename = true;
        return isValidFilename;
    }

    //Processing Chargeback
    private boolean processChargebackFile(File file) throws SystemError
    {
        boolean isFileProcessed = false;
        Connection conn = null;
        ResultSet rsFile =null;
        String fileName = file.getName();
        try
        {
            conn = Database.getConnection();
            rsFile = Database.executeQuery("select chargeback_process_id from chargeback_report_process_history where file_name='" + fileName + "'", conn);
            int chargeback_process_id = 0;
            if (!rsFile.next())
            {
                return isFileProcessed;
            }
            chargeback_process_id = rsFile.getInt("chargeback_process_id");
            FileInputStream fstream = null;
            DataInputStream in = null;
            BufferedReader br = null;
            try
            {
                fstream = new FileInputStream(file);
                in = new DataInputStream(fstream);
                br = new BufferedReader(new InputStreamReader(in));
                String line = null;
                int noOfTransactionsProcessed = 0;
                StringBuilder unProcessedTransactions= new StringBuilder();
                while ((line = br.readLine()) != null)
                {
                    String referenceNo = getRefNo(line, 8);
                    String referenceNoNew = getRefNoNew(line, 8);
                    String[] fields = line.split("\\|");
                    String date = fields[0];
                    String ARN = fields[8];
                    String authcode = fields[4];

                    String cbamount = new BigDecimal(fields[7].trim()).divide(new BigDecimal(100)).toString();
                    String cbreason = fields[5].trim();
                    String partial = Functions.checkStringNull(fields[9]);
                    String chargeBackIndicator = Functions.checkStringNull(fields[10]);

                    String query = "select T.toid,T.description,T.amount,T.icicitransid,T.accountid,T.status,T.capturereceiptno from transaction_icicicredit T where " +
                                "capturereceiptno in ('" + referenceNo + "','" + referenceNoNew + "') and authcode='" + authcode + "'";
                    ResultSet rsTransaction = Database.executeQuery(query, conn);

                    if (rsTransaction.next())
                    {
                        String toid = rsTransaction.getString("toid");
                        String description = rsTransaction.getString("description");
                        String icicitransid = rsTransaction.getString("icicitransid");
                        String accountId = rsTransaction.getString("accountid");
                        String status = rsTransaction.getString("status");
                        String capturereceiptno = rsTransaction.getString("capturereceiptno");
                        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
                        try
                        {
                            Database.executeUpdate("insert into chargeback_transaction_list(icicitransid,description,amount,merchantId,toid,fk_chargeback_process_id, cb_date, cb_reason, cb_partial, cb_indicator) values" +
                                        "(" + icicitransid + ",'" + description + "'," + cbamount + "," + merchantId + "," + toid + "," + chargeback_process_id + ",'" + date + "','" + cbreason + "','" + partial +"','" +chargeBackIndicator+"')", conn);
                            noOfTransactionsProcessed++;

                            //For ChargeBack
                            if ("ADJM".equalsIgnoreCase(chargeBackIndicator))
                            {
                                if("settled".equals(status))
                                {
                                    /*Transaction.processChargeback(icicitransid, date, fileName.substring(0, fileName.length() - 4) + "-" + Calendar.getInstance().getTimeInMillis(), cbamount, cbreason, partial);
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);*/
                                    //Do nothing as already processsed = N
                                }
                                else if("chargeback".equals(status))
                                {
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                }
                                else
                                {
                                    //Transaction Status is not in Sync
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Z' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                }
                            }
                            //For Chargeback Reversal
                            else if ("CBRV".equalsIgnoreCase(chargeBackIndicator))
                            {
                                if ("chargeback".equals(status))
                                {
                                    //TODO:call to reverse chargeback transaction
                                    //Code is unavailable for chargeback reversal.
                                    //Marked as processed when revered.Marking processed='X' to differential these transactions
                                    //Database.executeUpdate("update chargeback_transaction_list set processed='X' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                    //Do nothing as already processsed = N
                                }
                                else if("settled".equals(status))
                                {
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                }
                                else
                                {
                                    //Transaction Status is not in Sync
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Z' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                }
                            }
                            //For automatic Representment
                            else if ("AUTO".equalsIgnoreCase(chargeBackIndicator))
                            {
                                if("reversed".equals(status))
                                {
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Y' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                }
                                else
                                {
                                    //Transaction Status is not in Sync
                                    Database.executeUpdate("update chargeback_transaction_list set processed='Z' where icicitransid=" + icicitransid + " and fk_chargeback_process_id=" + chargeback_process_id, conn);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
                            //Unknown Technical Issue while processiong the Transaction. Keeping the status as it is processed='N'
                        }
                    }
                    else
                    {
                        //Transaction is not in our System. Leaving the Transaction as processed='N'
                        unProcessedTransactions.append(line);
                        unProcessedTransactions.append("\r\n");
                    }
                }
                Database.executeUpdate("update chargeback_report_process_history set no_of_transactions=" + noOfTransactionsProcessed + ", parsed='Z'," +
                            "unprocessed_transactions='" + unProcessedTransactions.toString() + "' where chargeback_process_id =" + chargeback_process_id, conn);
                isFileProcessed = true;
            }
            catch (Exception e)
            {
                //log the Exception
                log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
            }
            finally
            {
                br.close();
                in.close();
                fstream.close();
            }

        }
        catch (Exception e)
        {
            //log the Exception
            log.error("ChargebackReportProcessor : Error : "+ e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return isFileProcessed;
    }

    private boolean moveFile(String sourceFolder, String destinationFolder, String fileName)
    {
        boolean isFileMoved =false;
        File sourceFile = new File(sourceFolder + fileName);
        File destinationFile = new File(destinationFolder+fileName);
        if(sourceFile.exists())
        {
            if(destinationFile.exists())
            {

                destinationFile.renameTo(new File(destinationFolder + fileName.substring(0,fileName.length()-4) + "_" + getCurrentTimeStamp() + ".TXT"));
                isFileMoved = true;
            }
        }
        sourceFile = null;
        destinationFile = null;
        return isFileMoved;
    }
    private String getCurrentTimeStamp()
    {
        java.util.Date vDt = new Date();
        TimeZone mauritiusTZ = TimeZone.getTimeZone("Indian/Mauritius");
        Calendar cal = Calendar.getInstance(mauritiusTZ);
        cal.setTime(vDt);
        java.util.Date currentDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyyyyHHmmss");
        String vTimeStamp = dateFormat.format(currentDate);
        return vTimeStamp;
    }
}
