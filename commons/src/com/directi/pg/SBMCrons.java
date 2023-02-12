package com.directi.pg;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Jun 6, 2007
 * Time: 2:00:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class SBMCrons
{
    private static Logger log = new Logger(SBMCrons.class.getName());

    /*public static void autoCapture() throws SystemError
    {
        log.info("Entering autoCaptureCron Cron");
        List<GatewayAccount> allSBMAccounts = getSBMAccounts();
        if (allSBMAccounts.size() > 0)
        {
            captureTransactions(allSBMAccounts);
        }
    }*/

    /*private static void captureTransactions(List<GatewayAccount> accounts) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            for (GatewayAccount account : accounts)
            {
                String accountId = String.valueOf(account.getAccountId());
                AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                String selectStuckTransactionsQuery = "select * from transaction_icicicredit where status = 'capturestarted' and accountid = " + accountId;
                log.debug("Select Stuck Transactiosn Query :" + selectStuckTransactionsQuery);
                ResultSet rs = Database.executeQuery(selectStuckTransactionsQuery, conn);
                while (rs.next())
                {
                    try
                    {
                        String trackingId = rs.getString("icicitransid");
                        String captureAmount = rs.getString("captureamount");
                        String amount = rs.getString("amount");

                        Hashtable statusDetails = null;
                        statusDetails = pg.getStatus(trackingId);
                        log.debug("Status details from SBM " + statusDetails);
                        String status = (String) statusDetails.get("status");

                        if ("captured".equalsIgnoreCase(status))
                        {
                            if (amount.equalsIgnoreCase(captureAmount))
                            {
                                log.info("Captured on SBM with the details we had sent update detials in DB");
                                updateCaptureDetails(statusDetails, conn);
                            }
                        }
                        else if ("approved".equals(status))
                        {
                            log.info("Since transaction not captured capture it now");

                            String authId = rs.getString("authid");
                            String authcode = rs.getString("authcode");
                            String authRRN = rs.getString("authreceiptno");
                            Hashtable captureHash = pg.processCapture(trackingId, captureAmount, authId, authcode, authRRN);
                            Hashtable captureDetails = mapCaptureDetails(trackingId, captureHash);
                            updateCaptureDetails(captureDetails, conn);
                        }
                        else
                        {
                            Mail.sendAdminMail("Error while autoCaptureCron Cron", " Status in DB is 'capturestarted' while status in SBM is " + status + " Tracking Id :" + trackingId);
                        }
                    }
                    catch (SQLException e)
                    {
                        Mail.sendAdminMail("Error while autoCaptureCron Cron", " Stack Trace " + Util.getStackTrace(e));
                    }
                    catch (SystemError se)
                    {
                        Mail.sendAdminMail("Error while autoCaptureCron Cron", " Stack Trace " + Util.getStackTrace(se));
                    }

                }
            }
        }
        catch (SQLException e)
        {
            throw new SystemError("SQLException " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }


    }

    private static Hashtable mapCaptureDetails(String trackingId, Hashtable captureHash)
    {
        Hashtable returnHash = new Hashtable();
        String captureId = (String) captureHash.get("captureid");
        String captureCode = (String) captureHash.get("capturecode");
        String captureRRN = (String) captureHash.get("capturereceiptno");
        String captureQsiResponseCode = (String) captureHash.get("captureqsiresponsecode");
        String captureQsiResponseDesc = (String) captureHash.get("captureqsiresponsedesc");

        returnHash.put("trackingid", trackingId);
        returnHash.put("id", captureId);
        returnHash.put("code", captureCode);
        returnHash.put("receiptno", captureRRN);
        returnHash.put("qsiresponsecode", captureQsiResponseCode);
        returnHash.put("qsiresponsedesc", captureQsiResponseDesc);

        return returnHash;
    }

    private static void updateCaptureDetails(Hashtable details, Connection conn) throws SystemError
    {
        log.info("Updating Capture Details");
        String trackingId = (String) details.get("trackingid");
        String captureId = (String) details.get("id");
        String captureCode = (String) details.get("code");
        String captureRRN = (String) details.get("receiptno");
        String captureQsiResponseCode = (String) details.get("qsiresponsecode");
        String captureQsiResponseDesc = (String) details.get("qsiresponsedesc");
        String query = "";
        if (("0").equals(captureQsiResponseCode))
        {
            log.debug("Transaction captured successfully for trackingid--" + trackingId);
            query = "update transaction_icicicredit set captureqsiresponsecode='" + captureQsiResponseCode + "' ,captureqsiresponsedesc='" + captureQsiResponseDesc + "' ,captureId='" + captureId + "',captureCode='" + captureCode + "',capturereceiptno='" + captureRRN + "',status='capturesuccess',captureresult='Capture Done using Cron.' where icicitransid=" + trackingId + " and status='capturestarted'";
        }
        else
        {
            log.debug("Error while capturing for trackingid--" + trackingId);
            query = "update transaction_icicicredit set captureqsiresponsecode='" + captureQsiResponseCode + "' ,captureqsiresponsedesc='" + captureQsiResponseDesc + "' ,captureId='" + captureId + "',captureCode='" + captureCode + "',capturereceiptno='" + captureRRN + "' where icicitransid=" + trackingId + " and status='capturestarted'";
        }
        Database.executeUpdate(query, conn);
    }

    public static void autoCancel() throws SystemError
    {

        log.info("Entering autoCaptureCron Cron");
        List<GatewayAccount> allSBMAccounts = getSBMAccounts();
        if (allSBMAccounts.size() > 0)
        {
            cancelTransactions(allSBMAccounts);
        }

    }

    private static void cancelTransactions(List<GatewayAccount> accounts) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            for (GatewayAccount account : accounts)
            {
                String accountId = String.valueOf(account.getAccountId());
                AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                String selectStuckTransactionsQuery = "select * from transaction_icicicredit where status = 'authsuccessful' and (TO_DAYS(now())-TO_DAYS(from_unixtime(unix_timestamp(dtstamp)))) >= 3 and accountid = " + accountId;
                log.debug("Select Stuck Transactiosn Query :" + selectStuckTransactionsQuery);
                ResultSet rs = Database.executeQuery(selectStuckTransactionsQuery, conn);
                while (rs.next())
                {
                    try
                    {
                        String trackingId = rs.getString("icicitransid");
                        Hashtable statusDetails = pg.getStatus(trackingId);
                        log.debug("Status details" + statusDetails);
                        String status = (String) statusDetails.get("status");
                        if ("voided".equalsIgnoreCase(status))
                        {
                            log.info("VOIDED on SBM so update details ");
                            updateCancelDetails(statusDetails, conn);
                        }
                        else if ("approved".equalsIgnoreCase(status))
                        {
                            log.info("Approved on SBM so VOID Auth ");
                            Hashtable cancellationHash = pg.processVoidAuth(trackingId);
                            Hashtable cancellationDetails = mapCancellationDetials(trackingId, cancellationHash);
                            updateCancelDetails(cancellationDetails, conn);
                        }
                        else if ("captured".equalsIgnoreCase(status))
                        {
                            log.info("Captured on SBM so pls update details in db ");
                            updateCaptureDetails(statusDetails, conn);
                            // updating capture details in db required since we need the captureid while making a void capture call.
                            log.info("Now since the detials have been updated we can safely call void capture");
                            Hashtable cancellationHash = pg.processVoidCapture(trackingId);
                            Hashtable cancellationDetails = mapCancellationDetials(trackingId, cancellationHash);
                            updateCancelDetails(cancellationDetails, conn);
                        }
                        else
                        {
                            Mail.sendAdminMail("Error while autoCancel", " Status in DB is 'authsuccessful' while status in SBM is " + status + " Tracking Id :" + trackingId);
                        }
                    }
                    catch (SQLException e)
                    {
                        Mail.sendAdminMail("Error while autoCancel Cron", " Stack Trace " + Util.getStackTrace(e));
                    }
                    catch (SystemError se)
                    {
                        Mail.sendAdminMail("Error while autoCancel Cron", " Stack Trace " + Util.getStackTrace(se));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new SystemError("SQLException " + Util.getStackTrace(e));
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    private static Hashtable mapCancellationDetials(String trackingId, Hashtable cancellationHash)
    {
        Hashtable returnHash = new Hashtable();
        String cancellationId = (String) cancellationHash.get("cancellationid");
        String cancellationCode = (String) cancellationHash.get("cancellationcode");
        String cancellationRRN = (String) cancellationHash.get("cancellationreceiptno");
        String cancellationQsiResponseCode = (String) cancellationHash.get("cancellationqsiresponsecode");
        String cancellationQsiResponseDesc = (String) cancellationHash.get("cancellationqsiresponsedesc");

        returnHash.put("trackingid", trackingId);
        returnHash.put("id", cancellationId);
        returnHash.put("code", cancellationCode);
        returnHash.put("receiptno", cancellationRRN);
        returnHash.put("qsiresponsecode", cancellationQsiResponseCode);
        returnHash.put("qsiresponsedesc", cancellationQsiResponseDesc);

        return returnHash;
    }

    private static void updateCancelDetails(Hashtable details, Connection conn) throws SystemError
    {
        log.info("Updating Cancel Details");
        String trackingId = (String) details.get("trackingid");
        String status = (String) details.get("status");
        String cancellationId = (String) details.get("id");
        String cancellationCode = (String) details.get("code");
        String cancellationRRN = (String) details.get("receiptno");
        String cancellationQsiResponseCode = (String) details.get("qsiresponsecode");
        String cancellationQsiResponseDesc = (String) details.get("qsiresponsedesc");
        String query = "";

        if (("0").equals(cancellationQsiResponseCode))
        {
            log.debug("Transaction cancelled successfully for trackingid--" + trackingId);
            query = "update transaction_icicicredit set status='authcancelled' where icicitransid=" + trackingId + " and status='authsuccessful'";
        }
        else

        {
            log.debug("Error while cancelling trackingid--" + trackingId);
            query = null;
        }
        if (query != null)
        {
            Database.executeUpdate(query, conn);
        }
    }

    public static void main
            (String[] args) throws Exception
    {
//        autoCaptureCron();
//        autoCancel();
    }*/


}
