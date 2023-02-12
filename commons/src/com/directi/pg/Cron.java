package com.directi.pg;

import com.directi.pg.core.BankConnection;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
//import com.fraud.PZOfflineFraudCheckerCron;
import com.logicboxes.util.Util;
import com.manager.MerchantMonitoringManager;
import com.manager.vo.merchantmonitoring.MerchantMonitoringAlertCron;
import com.payment.procesosmc.ProcesosMCAuthStartedCron;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: amit.j
 * Date: Oct 3, 2006
 * Time: 3:33:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Cron
{
    private static Logger log = new Logger(Cron.class.getName());

    public void sendMail(Hashtable ht)
    {
        Hashtable emailhash = new Hashtable();
        ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.finance");
        Enumeration enu = RB.getKeys();
        while (enu.hasMoreElements())
        {
            String key = (String) enu.nextElement();
            emailhash.put(key, RB.getString(key));
        }
        log.debug("trying to send notification mail");
        Finance.sendMail(emailhash, new java.util.Date());
        log.debug("notification mail sent");
    }

    public void sendNegativeBalance(Hashtable ht)
    {
        log.debug("trying to send NegativeBalance");
        NegativeBalance nb = new NegativeBalance();
        nb.sendBalance();
        log.debug("Negative Balance sent");
    }

    public void capture(Hashtable ht)
    {
        log.debug("Trying to capture");
        AutoCapture.capture();
        log.debug("captured");
    }

    public void DailyTransactionSendMail(Hashtable ht) throws Exception
    {
        log.debug("Calling Daily Transaction report ");
        DailyTransactionReport.sendMail(new java.util.Date());
        log.debug("Called Daily Transaction report ");
    }

    public void reverse(Hashtable ht)
    {
        log.debug("Calling Auto Reverse ");
        AutoReverse.reverse(null);
        log.debug("Called Auto Reverse");
    }

    public void checkAndUpdateMembers(Hashtable ht)
    {
        log.debug("calling checkAndUpdateMembers");
        ChangeReserve.checkAndUpdateMembers();
        log.debug("updated members");
    }

    public void fraudAlert(Hashtable ht)
    {
        try
        {
            log.debug("Calling FraudAlert  ");
            FraudAlert fa = new FraudAlert(new java.util.Date());
            log.debug("Called FraudAlert ");
        }
        catch (SystemError e)
        {
            log.error("Error while sending FraudAlert Mail",e);
            log.error(Util.getStackTrace(e));
        }
    }

    // Cron Methods from CSVCron
    public void CSVCreator(Hashtable ht)
    {
        try
        {
            log.debug("Calling CSVCreator");
            CSVCreator csv = new CSVCreator();
            csv.generate();
            log.debug("Sucess Called CSVCreator");
        }
        catch (Exception e)
        {
            log.error(" Error in Cron - CSVCreator ",e);
        }
    }

    public void setStatus(Hashtable ht)
    {
        try
        {
            log.debug("Calling Status");
            CSVCreator csv = new CSVCreator();
            Status s = new Status();
            s.setStatus(Integer.parseInt(csv.mindays));
            log.debug("Sucess Called Status");
        }
        catch (Exception e)
        {
            log.error(" Error in Cron - setStatus ",e);
        }
    }

    public void captureReminderToMerchantCron(Hashtable ht) throws SystemError
    {
        try
        {
            log.debug("Calling captureReminderCronToMerchant for all SBM transactions on status authsuccessful for more than 1 day");
            PODReminderCron pr = new PODReminderCron();
            pr.sendReminder(7, 2);
            log.debug("Sucess captureReminderCronToMerchant for 1 days for all SBM transactions");
        }
        catch (Exception e)
        {
            log.error(" Error in Cron - captureReminderCronToMerchant ",e);
            Mail.sendAdminMail("Error in Cron - captureReminderCronToMerchant ", Util.getStackTrace(e));
        }
    }

    public void SettlementReminder(Hashtable ht)
    {
        try
        {
            SettlementReminder sr = new SettlementReminder();
            log.debug("Calling SettlementReminder for 3 days");
            sr.sendReminder(7);
            log.debug("Sucess SettlementReminder for 3 days");
        }
        catch (Exception e)
        {
            log.error(" Error in Cron - SettlementReminder ",e);
        }
    }

    public void ProofReminderCron(Hashtable ht)
    {
        try
        {
            ProofReminderCron proof = new ProofReminderCron();

            BufferedReader rin = new BufferedReader(new FileReader(Template.MAILPATH + "customer_hrt.template"));
            String temp = null;
            StringBuffer rawdataBuf = new StringBuffer();

            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();


            log.debug("Calling ProofReminderCron for 2 days");
            proof.sendCustomerReminder(rawdataBuf.toString(), 6, 2);
            log.debug("Sucess ProofReminderCron for 2 days");

            log.debug("Calling ProofReminderCron for 4 days");
            proof.sendCustomerReminder(rawdataBuf.toString(), 6, 4);
            log.debug("Sucess ProofReminderCron for 4 days");

            log.debug("Calling ProofReminderCron for 5 days");
            proof.sendCustomerReminder(rawdataBuf.toString(), 6, 5);
            log.debug("Sucess ProofReminderCron for 5 days");

            log.debug("Calling Cancelled ProofReminderCron");
            proof.cancelProofRequired(6);
            log.debug("Sucess Cancelled ProofReminderCron");
        }
        catch (Exception e)
        {
           log.error(" Exception in Cron - ProofReminderCron ",e);
        }
    }

    public void ChangeStatus(Hashtable ht)
    {
        try
        {
            log.debug("Calling Change Status cron ");
            ChangeStatus cs = new ChangeStatus();
            cs.change(null);
            log.debug("Sucess Change Status cron ");
        }
        catch (Exception e)
        {
            log.error(" Error in Cron - ChangeStatus ",e);
        }
    }

    public void CaptureReminder(Hashtable ht)
    {
        log.debug("Calling CaptureReminder for 3 days");
        CaptureReminder cr = new CaptureReminder();
        cr.sendReminder(3);
        log.debug("Sucess CaptureReminder for 3 days");
    }

    public void TransactionReminderToCustomer(Hashtable ht)
    {
        try
        {
            BufferedReader rin = new BufferedReader(new FileReader(Template.MAILPATH + "customer_notification.template"));
            String temp = null;
            StringBuffer rawdataBuf = new StringBuffer();

            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();

            TransactionReminderToCustomer trc = new TransactionReminderToCustomer();
            log.debug("Calling TransactionReminderToCustomer for 7 days");
            trc.sendReminder(rawdataBuf.toString(), 7);
            log.debug("Sucess TransactionReminderToCustomer for 7  days");
        }
        catch (Exception e)
        {
            log.error("Exception in TransactionReminderToCustomer ", e);
        }
    }

    // Monthly Report Cron
    public void MonthlyReportCron(Hashtable ht)
    {
        try
        {
            log.debug("Calling MonthlyReportCron");
            MonthlyReportCron mr = new MonthlyReportCron();
            mr.sendReport(0, 0);
            log.debug("Sucess Called MonthlyReportCron");
        }
        catch (Exception e)
        {
            log.error("Error in Cron - MonthlyReportCron ", e);
        }
    }

    public void sendBalance(Hashtable ht)
    {
        try
        {
            log.debug("Calling sendBalance");
            Balance nr = new Balance();
            nr.sendBalance(0, 0);
            log.debug("Sucess called sendBalance");
        }
        catch (Exception e)
        {
            log.error("Error in Cron - sendBalance ",e);
        }
    }

    public void loadIpToCountryFile(Hashtable ht)
    {
        try
        {
            log.debug("Calling loadIpToCountryFile");
            DBDumper.loadIpToCountryFile();
            log.debug("Sucess called loadIpToCountryFile");
        }
        catch (Exception e)
        {
            log.error("Error in Cron - loadIpToCountryFile ",e);
        }
    }

    public void createReconFiles(Hashtable ht) throws SystemError
    {
        try
        {
            log.debug("Calling processFraudReport");
            Calendar cal = Calendar.getInstance();
            log.debug(" Current Date " + cal.getTime());
            cal.add(Calendar.DATE, -1);
            int date = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            log.debug(" Creating Recon Files for date " + cal.getTime());
            ReconFileCreator.createReconFile(date, month, year);
            log.debug("called processFraudReport");
        }
        catch (SystemError e)
        {
            log.error("Error in Cron -  createReconFiles",e);
            Mail.sendAdminMail("Unable to generate Recon file ", "Unable to generate Recon Files  " + Functions.getStackTrace(e));
        }
    }


    public void settleSBMTransactions(Hashtable ht) throws SystemError
    {
        try
        {
            log.debug("Calling SettleTransactions");
            Calendar cal = Calendar.getInstance();
            log.debug(" Current Date " + cal.getTime());
            cal.add(Calendar.DATE, -5);
            int date = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            log.debug(" Settling transactions for the date " + cal.getTime());
            ReconFileCreator.settleSBMTransactions(date, month, year);
            log.debug("called SettleTransactions");
        }
        catch (SystemError e)
        {
            log.error("Error in Cron -  settleSBMTransactions",e);
            Mail.sendAdminMail("Unable to settle SBM transactions ", "Unable to settle SBM transactions  " + Functions.getStackTrace(e));
        }
    }

    /*public static void main(String[] args) throws Exception
    {
        new Cron().settleSBMTransactions(null);
    }
*/
    public void autoCaptureTransactions(Hashtable ht) throws SystemError
    {
        try
        {
            log.debug("Calling Auto Capture ");
            AbstractPaymentGateway.autoCaptureCron();
            log.debug("Sucess called Calling Auto Capture ");
        }
        catch (SystemError e)
        {
            log.error("Error in Cron autoCaptureTransactions-  " ,e);
            Mail.sendAdminMail("Error Calling Auto Capture  ", "Error Calling Auto Capture Cron  " + Functions.getStackTrace(e));
        }

    }

    public void autoCancelTransactions(Hashtable ht) throws SystemError
    {
        try
        {
            log.debug("Calling Auto Cancel  ");
            AbstractPaymentGateway.autoCancelCron();
            log.debug("Sucess called Calling Auto Cancel ");
        }
        catch (SystemError e)
        {
            log.error("Error in Cron autoCaancelTransactions-  ",e);
            Mail.sendAdminMail("Error Calling Auto Cancel  ", "Error Calling Auto Cancel Cron  " + Functions.getStackTrace(e));
        }

    }

    public void bankConnection(Hashtable hr) throws SystemError
    {
        try
        {
            BankConnection bankConnection = new BankConnection();
            bankConnection.checkConnection();

        }
        catch (Exception e)
        {
            log.error("Error in Cron Bank Connection-  "+e);
        }

    }

    //Sending alert mail to merchants based on their Fraud Preferences and reaction time.

    public void sendFraudTransactionAlert(Hashtable hr) throws SystemError
    {
        try
        {

            /*PZOfflineFraudCheckerCron cron=new PZOfflineFraudCheckerCron();
            cron.sendFraudTransactionAlert();*/
        }
        catch (Exception e)
        {
            log.error("Error in Cron sendFraudTransactionAlert-  "+e);
        }

    }
    public void pzOfflineFraudChecker(Hashtable hr) throws SystemError
    {
        try
        {
            /*PZOfflineFraudCheckerCron cron=new PZOfflineFraudCheckerCron();

            cron.newTransaction();*/
        }
        catch (Exception e)
        {
            log.error("Error in Cron "+e);
            log.error(e.getStackTrace());
        }

    }
    public void updateTransToOfflinePZFraudChecker(Hashtable hr) throws SystemError
    {
        try
        {
            /*PZOfflineFraudCheckerCron cron=new PZOfflineFraudCheckerCron();

            cron.updateTransaction();*/
        }
        catch (Exception e)
        {
            log.error("Error in Cron -updateTransToPZFraudChecker "+e);
        }
    }
    public void sendStuckTransactionMailToAdmin(Hashtable hr) throws SystemError
    {
        try
        {
            MerchantMonitoringManager merchantMonitoringManager=new MerchantMonitoringManager();

            merchantMonitoringManager.sendStuckTransactionMailToAdmin();
        }
        catch (Exception e)
        {
            log.error("Error in Cron -sendAuthStartedTransactionMailToAdmin "+e);
        }

    }
    public void dailyMerchantMonitoringRiskAlerts(Hashtable hr) throws SystemError
    {
        try
        {
            MerchantMonitoringAlertCron mmsCron=new MerchantMonitoringAlertCron();

            mmsCron.dailyMerchantMonitoringRiskAlerts();
        }
        catch (Exception e)
        {
            log.error("Error in Cron -dailyMerchantMonitoringRiskAlerts "+e);
        }

    }
    public void weeklyMerchantMonitoringRiskAlerts(Hashtable hr) throws SystemError
    {
        try
        {
            MerchantMonitoringAlertCron mmsCron=new MerchantMonitoringAlertCron();

            mmsCron.weeklyMerchantMonitoringRiskAlerts();
        }
        catch (Exception e)
        {
            log.error("Error in Cron -weeklyMerchantMonitoringRiskAlerts "+e);
        }

    }
    public void monthlyMerchantMonitoringRiskAlerts(Hashtable hr) throws SystemError
    {
        try
        {
            MerchantMonitoringAlertCron mmsCron=new MerchantMonitoringAlertCron();

            mmsCron.monthlyMerchantMonitoringRiskAlert();
        }
        catch (Exception e)
        {
            log.error("Error in Cron -monthlyMerchantMonitoringRiskAlerts "+e);
        }

    }

    public void dailyProcesosMCAuthStartedCron(Hashtable hr) throws SystemError
    {
        try
        {
            ProcesosMCAuthStartedCron procesosMCAuthStartedCron = new ProcesosMCAuthStartedCron();
            procesosMCAuthStartedCron.pmcAuthStartedCron(hr);

        }
        catch (Exception e)
        {
            log.error("Error in Cron -DailyAuthStartedCron Execution:::: " + e);
        }

    }

}