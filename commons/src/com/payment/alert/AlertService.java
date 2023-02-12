package com.payment.alert;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 4/1/14
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlertService
{
    static Logger log = new Logger(AlertService.class.getName());
    Connection connection=null;

    public void sendAlertMail(Hashtable ht)
    {
        try
        {
            AlertService alertService=new AlertService();
            HashMap statusCountRecord=new HashMap();
            String fromDate="";
            String toDate="";

            connection = Database.getConnection();
            String listAccountid = "select * from gateway_accounts WHERE istest='N' AND isactive='Y'";
            PreparedStatement preparedStatement= connection.prepareStatement(listAccountid);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                String accountId=resultSet.getString("accountid");
                String mId=resultSet.getString("merchantid");
                String dispalyName=resultSet.getString("displayname");
                String gatewayName= GatewayAccountService.getGatewayAccount(accountId).getGateway();
                String tableName = Database.getTableName(gatewayName);

                String statusCount="SELECT STATUS,COUNT(*) AS COUNT,CONCAT(CURRENT_DATE,' 00:00:00') AS Fdate,CONCAT(CURRENT_DATE,' 23:59:59') AS todate FROM "+tableName+" WHERE fromid=? AND from_unixtime(dtstamp) >= CONCAT(CURRENT_DATE,' 00:00:00') AND from_unixtime(dtstamp) <= CONCAT(CURRENT_DATE,' 23:59:59') GROUP BY STATUS";
                PreparedStatement pstmt=connection.prepareStatement(statusCount);
                pstmt.setString(1,mId);
                ResultSet rs=pstmt.executeQuery();

                LinkedHashMap alertSummeryDetail=new LinkedHashMap();
                alertSummeryDetail.put("FromId",mId);
                alertSummeryDetail.put("BankName",gatewayName);
                alertSummeryDetail.put("Discriptor",dispalyName);
                alertSummeryDetail.put("authfailed","0");
                alertSummeryDetail.put("capturesuccess","0");
                alertSummeryDetail.put("authstarted","0");
                alertSummeryDetail.put("chargeback","0");
                alertSummeryDetail.put("markedforreversal","0");
                alertSummeryDetail.put("reversed","0");
                alertSummeryDetail.put("settled","0");
                alertSummeryDetail.put("failed","0");
                alertSummeryDetail.put("begun","0");

                int totalCount=0;
                double captureCount=0;
                double authFailedCount=0;
                double refundCount=0;
                while (rs.next())
                {
                    if(rs.getString("status").equals("authfailed"))
                    {
                        alertSummeryDetail.put("authfailed",rs.getString("count"));
                        authFailedCount= Integer.parseInt(rs.getString("count"));
                    }
                    if(rs.getString("status").equals("capturesuccess"))
                    {
                        alertSummeryDetail.put("capturesuccess",rs.getString("count"));
                        captureCount= Integer.parseInt(rs.getString("count"));
                    }
                    if(rs.getString("status").equals("authstarted"))
                    {
                        alertSummeryDetail.put("authstarted",rs.getString("count"));
                    }
                    if(rs.getString("status").equals("chargeback"))
                    {
                        alertSummeryDetail.put("chargeback",rs.getString("count"));
                    }
                    if(rs.getString("status").equals("markedforreversal"))
                    {
                        alertSummeryDetail.put("markedforreversal",rs.getString("count"));
                    }
                    if(rs.getString("status").equals("reversed"))
                    {
                        alertSummeryDetail.put("reversed",rs.getString("count"));
                        refundCount= Integer.parseInt(rs.getString("count"));
                    }
                    if(rs.getString("status").equals("settled"))
                    {
                        alertSummeryDetail.put("settled",rs.getString("count"));
                    }
                    if(rs.getString("status").equals("failed"))
                    {
                        alertSummeryDetail.put("failed",rs.getString("count"));
                    }
                    if(rs.getString("status").equals("begun"))
                    {
                        alertSummeryDetail.put("begun",rs.getString("count"));
                    }
                    totalCount=totalCount+Integer.parseInt(rs.getString("count"));

                    fromDate=rs.getString("fDate");
                    toDate=rs.getString("toDate");
                }
                alertSummeryDetail.put("TotalCount",String.valueOf(totalCount));
                alertSummeryDetail.put("Capture Success %",alertService.getPercentage(captureCount,totalCount)+" %");
                alertSummeryDetail.put("Auth Fail %",alertService.getPercentage(authFailedCount,totalCount)+" %");
                alertSummeryDetail.put("Refund %",alertService.getPercentage(refundCount,totalCount)+" %");
                statusCountRecord.put(String.valueOf(accountId),alertSummeryDetail);
            }
            if(!statusCountRecord.isEmpty())
            {
               // MailService mailService=new MailService();
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                statusCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION,asynchronousMailService.getDetailTable(statusCountRecord));
                statusCountRecord.put(MailPlaceHolder.FROMDATE,fromDate.toString());
                statusCountRecord.put(MailPlaceHolder.TODATE,toDate.toString());
                asynchronousMailService.sendMerchantSignup(MailEventEnum.ALERT_DAILY_STATUS_SUMMERY_REPORT, statusCountRecord);
            }
        }
        catch (SQLException e)
        {
            log.error("Sql Exception while fetching record from gateway accounts",e);
        }
        catch (SystemError systemError)
        {
            log.error("System Error while geting database connection",systemError);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public void sendChargebackAlertMail(Hashtable ht)
    {
        Connection conn = null;
        HashMap cbCountRecord = new HashMap();
        try
        {
            String fromDate = "";
            String toDate = "";

            conn = Database.getRDBConnection();
            //String cbCountQry="SELECT t.toid AS memberid,m.company_name AS companyname,t.terminalid,t.fromtype,t.accountid, t.fromid, COUNT(*) AS totalcount,SUM(chargebackamount) AS totalamount,CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 00:00:00') AS fdate,CONCAT(CURRENT_DATE,' 23:59:59') AS tdate FROM transaction_common AS t,members AS m WHERE t.toid=m.memberid  and m.partnerId=4 AND t.toid=? AND t.accountid=? AND t.paymodeid=? AND t.cardtypeid=? AND t.timestamp>= CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 50 DAY), ' 00:00:00') AND t.timestamp<= CONCAT(CURRENT_DATE,' 23:59:59') AND t.status='chargeback' GROUP BY toid,STATUS";
            String cbCountQry = "SELECT m.company_name AS companyname,t.fromid,t.terminalid,t.toid AS memberid,t.fromtype,t.accountid,COUNT(*) AS totalcount,SUM(chargebackamount) AS totalamount,CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 00:00:00') AS fdate,CONCAT(CURRENT_DATE,' 23:59:59') AS tdate FROM transaction_common AS t,members AS m WHERE t.toid=m.memberid  and m.partnerId=4 AND t.timestamp>= CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 50 DAY), ' 00:00:00') AND t.timestamp<= CONCAT(CURRENT_DATE,' 23:59:59') AND t.status='chargeback' GROUP BY toid,terminalid,STATUS";
            log.error("sendChargebackAlertMail.cbCountQry():" + cbCountQry);
            PreparedStatement p2 = conn.prepareStatement(cbCountQry);
            ResultSet rs = p2.executeQuery();
            int cbCount = 0;
            double cbTotalAmount = 0;
            while (rs.next())
            {
                String companyName = rs.getString("companyname");
                String fromId = rs.getString("fromid");
                String terminalId = rs.getString("terminalid");
                String memberId = rs.getString("memberid");
                String gatewayName = rs.getString("fromtype");
                String accountId = rs.getString("accountid");

                cbCount = rs.getInt("totalcount");
                cbTotalAmount = rs.getDouble("totalamount");
                fromDate = rs.getString("fdate");
                toDate = rs.getString("tdate");

                LinkedHashMap alertCBSummeryDetail = new LinkedHashMap();
                alertCBSummeryDetail.put("MerchantId", memberId);
                alertCBSummeryDetail.put("TerminalId", terminalId);
                alertCBSummeryDetail.put("CompanyName", companyName);
                alertCBSummeryDetail.put("AccountId", accountId);
                alertCBSummeryDetail.put("FromId", fromId);
                alertCBSummeryDetail.put("GatewayName", gatewayName);
                alertCBSummeryDetail.put("ChargebackCount", String.valueOf(cbCount));
                alertCBSummeryDetail.put("ChargebackTotalAmount", String.valueOf(cbTotalAmount));
                cbCountRecord.put(String.valueOf(terminalId), alertCBSummeryDetail);
            }

            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            if (!cbCountRecord.isEmpty())
            {
                cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, asynchronousMailService.getDetailTable(cbCountRecord));
                cbCountRecord.put(MailPlaceHolder.FROMDATE, fromDate.toString());
                cbCountRecord.put(MailPlaceHolder.TODATE, toDate.toString());
                asynchronousMailService.sendMerchantSignup(MailEventEnum.ALERT_WEEKLY_CHARGEBACK_SUMMERY_REPORT, cbCountRecord);
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::", e);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::", systemError);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    /*public void sendChargebackAlertMail_old(Hashtable ht)
    {
        try
        {
            HashMap cbCountRecord=new HashMap();
            String fromDate="";
            String toDate="";

            connection = Database.getConnection();
            String accountId=null;
            String qry= "SELECT memberid,terminalid,accountid,paymodeid,cardtypeid FROM member_account_mapping WHERE isActive='Y' AND isTest='N'";
            PreparedStatement p1=connection.prepareStatement(qry);
            ResultSet resultSet=p1.executeQuery();
            while (resultSet.next())
            {
                String memberid=resultSet.getString("memberid");
                String terminalId=resultSet.getString("terminalid");
                accountId=resultSet.getString("accountid");
                String paymodeId=resultSet.getString("paymodeid");
                String cardTypeId=resultSet.getString("cardtypeid");

                GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
                if(gatewayAccount==null){
                   continue;
                }

                String gatewayName= gatewayAccount.getGateway();
                String tableName = Database.getTableName(gatewayName);

                String cbCountQry="SELECT t.toid AS memberid,m.company_name AS companyname, t.accountid, t.fromid, COUNT(*) AS totalcount,SUM(chargebackamount) AS totalamount,CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 00:00:00') AS fdate,CONCAT(CURRENT_DATE,' 23:59:59') AS tdate FROM "+tableName+" AS t,members AS m WHERE t.toid=m.memberid AND t.toid=? AND t.accountid=? AND t.paymodeid=? AND t.cardtypeid=? AND t.timestamp>= CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 50 DAY), ' 00:00:00') AND t.timestamp<= CONCAT(CURRENT_DATE,' 23:59:59') AND t.status='chargeback' GROUP BY toid,STATUS";
                PreparedStatement p2=connection.prepareStatement(cbCountQry);
                p2.setString(1,memberid);
                p2.setString(2,accountId);
                p2.setString(3,paymodeId);
                p2.setString(4,cardTypeId);
                ResultSet rs=p2.executeQuery();
                int cbCount=0;
                double cbTotalAmount=0;

                if(rs.next())
                {
                    String companyname=rs.getString("companyname");
                    String fromid=rs.getString("fromid");
                    cbCount=rs.getInt("totalcount");
                    cbTotalAmount=rs.getDouble("totalamount");
                    fromDate=rs.getString("fdate");
                    toDate=rs.getString("tdate");
                    LinkedHashMap alertCBSummeryDetail=new LinkedHashMap();
                    alertCBSummeryDetail.put("MerchantId",memberid);
                    alertCBSummeryDetail.put("TerminalId",terminalId);
                    alertCBSummeryDetail.put("CompanyName",companyname);
                    alertCBSummeryDetail.put("AccountId",accountId);
                    alertCBSummeryDetail.put("FromId",fromid);
                    alertCBSummeryDetail.put("GatewayName",gatewayName);
                    alertCBSummeryDetail.put("ChargebackCount",String.valueOf(cbCount));
                    alertCBSummeryDetail.put("ChargebackTotalAmount",String.valueOf(cbTotalAmount));
                    cbCountRecord.put(String.valueOf(memberid),alertCBSummeryDetail);
                }
            }

            //MailService mailService=new MailService();
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            if(!cbCountRecord.isEmpty())
            {
                cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION,asynchronousMailService.getDetailTable(cbCountRecord));
                cbCountRecord.put(MailPlaceHolder.FROMDATE,fromDate.toString());
                cbCountRecord.put(MailPlaceHolder.TODATE,toDate.toString());
                asynchronousMailService.sendMerchantSignup(MailEventEnum.ALERT_WEEKLY_CHARGEBACK_SUMMERY_REPORT,cbCountRecord);
            }

        }
        catch (SQLException e)
        {
            log.error("Sql Exception while fetching record from gateway accounts in CB Summary Report", e);
        }
        catch (SystemError systemError)
        {
            log.error("System Error while geting database connection in CB Summary Report", systemError);
        }
        finally {
            Database.closeConnection(connection);
        }
    }*/

    private double getPercentage(double count,double totalcount)
    {
        double devideby=0;
        if(totalcount!=0)
        {
            devideby= count/totalcount;
        }
        double percentage=devideby*100;
        DecimalFormat f = new DecimalFormat("##.00");
        return Double.parseDouble(f.format(percentage));
    }

    /*public static void main(String args[])
    {
        AlertService alertService=new AlertService();
        Hashtable ht=new Hashtable();
        alertService.sendChargebackAlertMail(ht);
    }*/
}