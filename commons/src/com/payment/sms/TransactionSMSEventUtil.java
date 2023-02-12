package com.payment.sms;

import com.directi.pg.*;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by admin on 2/26/2017.
 */
public class TransactionSMSEventUtil
{
    private static Logger logger = new Logger(TransactionSMSEventUtil.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TransactionSMSEventUtil.class.getName());

    public void sendTransactionEventSMS(MailEventEnum smsEventEnum, String trackingId, String status, String billingDescriptor)
    {
        if (smsEventEnum != null && trackingId != null && status != null)
        {
            SMSService smsService = new SMSService();
            Functions functions = new Functions();
            HashMap smsData = new HashMap();
            switch (smsEventEnum)
            {
                case PARTNERS_MERCHANT_SALE_TRANSACTION:
                    smsData = setSaleTransactionData(trackingId, status);
                    break;

                case PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION:
                    smsData = setSaleTransactionData(trackingId, status);
                    break;

                case REFUND_TRANSACTION:
                    smsData = setSaleTransactionData(trackingId,status);
                    break;
            }

            if (functions.isValueNull(billingDescriptor))
            {
                smsData.put(MailPlaceHolder.DISPLAYNAME, billingDescriptor);
            }
            smsService.sendSMS(smsEventEnum, smsData);
        }
    }

    public void sendNonTransactionEventSMS(MailEventEnum smsEventEnum, HashMap map)
    {

        if (smsEventEnum != null)
        {
            System.out.println("inside if step 4----"+smsEventEnum);
            HashMap smsData = new HashMap();
            SMSService smsService = new SMSService();
            switch (smsEventEnum)
            {

                case MERCHANT_SIGNUP_OTP:
                    smsData.put(MailPlaceHolder.SMS_TO_TELNO, map.get("phone"));//TO Tel No
                    smsData.put(MailPlaceHolder.OTP,map.get("otp"));//OTP
                    smsData.put(MailPlaceHolder.PARTNERID,map.get("partnerid"));
                    break;

                case GENERATING_INVOICE_BY_MERCHANT:
                    smsData.put(MailPlaceHolder.SMS_TO_TELNO, map.get("phone"));
                    smsData.put(MailPlaceHolder.SMS_TO_TELNOCC,map.get("phonecc"));
                    smsData.put(MailPlaceHolder.NAME, map.get("custname"));
                    smsData.put(MailPlaceHolder.INVOICENO,map.get("invoiceno"));
                    smsData.put(MailPlaceHolder.ORDERID,map.get("orderid"));
                    smsData.put(MailPlaceHolder.DESC,map.get("orderdesc"));
                    smsData.put(MailPlaceHolder.AMOUNT,map.get("amount"));
                    smsData.put(MailPlaceHolder.CURRENCY,map.get("currency"));
                    smsData.put(MailPlaceHolder.LIVEURL, map.get("LIVE_URL"));
                    smsData.put(MailPlaceHolder.TERMINALID, map.get("terminalid"));
                    smsData.put(MailPlaceHolder.MERCHANTCOMPANYNAME, map.get("companyname"));
                    smsData.put(MailPlaceHolder.CTOKEN, map.get("ctoken").toString().substring(0, 16));
                    break;
            }

            smsService.sendSMS(smsEventEnum, smsData);
        }
    }

    public HashMap setSaleTransactionData(String trackingId, String status)
    {
        HashMap smsDetails = new HashMap();
        HashMap transactionDetail = searchTransDetails(trackingId);
        smsDetails.put(MailPlaceHolder.TOID, transactionDetail.get("toid"));
        smsDetails.put(MailPlaceHolder.CustomerEmail, transactionDetail.get("email"));
        smsDetails.put(MailPlaceHolder.TRACKINGID, trackingId);
        smsDetails.put(MailPlaceHolder.IPADDRESS, transactionDetail.get("ipaddress"));
        smsDetails.put(MailPlaceHolder.ORDERDESCRIPTION, transactionDetail.get("orderdescription"));
        smsDetails.put(MailPlaceHolder.DESC, transactionDetail.get("description"));
        smsDetails.put(MailPlaceHolder.AMOUNT, transactionDetail.get("amount"));
        smsDetails.put(MailPlaceHolder.CURRENCY, transactionDetail.get("currency"));
        smsDetails.put(MailPlaceHolder.STATUS, status);
        smsDetails.put(MailPlaceHolder.NAME, transactionDetail.get("name"));
        smsDetails.put(MailPlaceHolder.SMS_TO_TELNO, transactionDetail.get("telno"));
        smsDetails.put(MailPlaceHolder.SMS_TO_TELNOCC, transactionDetail.get("telnocc"));
        return smsDetails;
    }

    public HashMap searchTransDetails(String trackingId)
    {
        HashMap transDetails = null;
        Connection conn = null;
        String tablename = "";
        ResultSet rs = null;
        try
        {
            Transaction transaction = new Transaction();
            conn = Database.getConnection();
            String query = "";
            tablename = transaction.getTableNameFromTrackingid(trackingId);
            if (tablename.equalsIgnoreCase("transaction_common"))
            {
                query = "select trackingid,accountid,toid,paymodeid,cardtypeid,fromid,amount,orderdescription,description,captureamount,refundamount,transid,status,name,emailaddr,currency,chargebackamount,chargebackinfo,pod,podbatch,ipaddress,emailaddr,refundinfo,telno,telnocc from transaction_common where trackingid=?";
            }
            else if (tablename.equalsIgnoreCase("transaction_qwipi"))
            {
                query = "select trackingid,accountid,toid,paymodeid,cardtypeid,fromid,amount,orderdescription,description,captureamount,refundamount,transid,status,name,emailaddr,currency,chargebackamount,cbreason as chargebackinfo,pod,podbatch,emailaddr,ipaddress,refundinfo,telno,telnocc from transaction_qwipi where trackingid=?";
            }
            else if (tablename.equalsIgnoreCase("transaction_ecore"))
            {
                query = "select trackingid,accountid,toid,paymodeid,cardtypeid,fromid,amount,orderdescription,description,captureamount,refundamount,transid,status,name,emailaddr,currency,chargebackamount,cbreason as chargebackinfo,pod,podbatch,emailaddr,ipaddress,refundinfo,telno,telnocc from transaction_ecore where trackingid=?";
            }
            else
            {
                query = "select trackingid,accountid,toid,paymodeid,cardtypeid,fromid,amount,orderdescription,description,captureamount,refundamount,transid,status,name,emailaddr,currency,chargebackamount,chargebackinfo,pod,podbatch,ipaddress,emailaddr,refundinfo,telno,telnocc from transaction_common where trackingid=?";
            }
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, trackingId);
            rs = p.executeQuery();
            if (rs.next())
            {
                transDetails = new HashMap();
                transDetails.put("accountid", rs.getString("accountid"));
                transDetails.put("toid", rs.getString("toid"));
                transDetails.put("paymodeid", rs.getString("paymodeid"));
                transDetails.put("cardtypeid", rs.getString("cardtypeid"));
                transDetails.put("fromid", rs.getString("fromid"));
                transDetails.put("amount", rs.getString("amount"));
                transDetails.put("description", rs.getString("description"));
                transDetails.put("captureamount", rs.getString("captureamount"));
                transDetails.put("refundamount", rs.getString("refundamount"));
                transDetails.put("transid", rs.getString("transid"));
                transDetails.put("status", rs.getString("status"));
                transDetails.put("name", rs.getString("name"));
                transDetails.put("email", rs.getString("emailaddr"));
                transDetails.put("trackingid", rs.getString("trackingid"));
                transDetails.put("currency", rs.getString("currency"));
                transDetails.put("chargebackamount", rs.getString("chargebackamount"));
                transDetails.put("chargebackinfo", rs.getString("chargebackinfo"));
                transDetails.put("ipaddress", rs.getString("ipaddress"));
                transDetails.put("telno", rs.getString("telno"));
                transDetails.put("telnocc", rs.getString("telnocc"));
                transDetails.put("orderdescription", rs.getString("orderdescription"));
                if (rs.getString("refundinfo") != null)
                    transDetails.put("refundinfo", rs.getString("refundinfo"));
                else
                    transDetails.put("refundinfo", "");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Error", systemError);
            transactionLogger.error("Error", systemError);
        }
        catch (SQLException e)
        {
            logger.error("Error", e);
            transactionLogger.error("Error", e);
        }
        catch (Exception ex)
        {
            logger.error("Error", ex);
            transactionLogger.error("Error", ex);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transDetails;
    }
}
