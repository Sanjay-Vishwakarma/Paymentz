package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;


/**
 * Created with IntelliJ IDEA.
 * User: Jinesh Dani
 * Date: 12/13/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class QWIPIReconciliationCron
{
    private static Logger logger = new Logger(QWIPIReconciliationCron.class.getName());
    public static void main(String args[])throws SystemError
    {
        QWIPIReconciliationCron qwipiReconciliationCron = new QWIPIReconciliationCron();
        String sResponse  = qwipiReconciliationCron.qwipiReconciliationCron(new Hashtable());
        //System.out.println("sResponse =" + sResponse);

    }

    public String qwipiReconciliationCron(Hashtable ht)
    {

        Connection connection = null;
        ResultSet resultSet = null;
        AbstractPaymentGateway pg = null;

        StringBuffer success = new StringBuffer();
        success.append("Authstarted Transaction List"+"<br>");

        String trackingid = null;
        String captureAmount = null;

        String accountid = null;

        String fromid = null;



        PreparedStatement ps= null;
        PreparedStatement p1= null;
        String updatequery = null;
        String selectquery = null;
        String sDescription= null;
        String toid=null;
        Database db = new Database();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();

        try
        {
            connection = db.getConnection();
            selectquery = "select trackingid, qwipiPaymentOrderNumber,status,amount, description, remark, accountid, fromid,toid from transaction_qwipi WHERE status='authstarted' and (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15";

            p1=connection.prepareStatement(selectquery);
            resultSet = p1.executeQuery();

            while (resultSet.next())
            {

                    trackingid = resultSet.getString("trackingid");
                    captureAmount = resultSet.getString("amount");
                    accountid = resultSet.getString("accountid");
                    sDescription = resultSet.getString("description");
                    fromid = resultSet.getString("fromid");
                    toid=resultSet.getString("toid");
                    pg = AbstractPaymentGateway.getGateway(accountid);

                    QwipiAddressDetailsVO addressDetailsVO = new QwipiAddressDetailsVO();
                    addressDetailsVO.setMd5info(getMD5Info(fromid,accountid,sDescription));

                    QwipiTransDetailsVO transDetailsVO = new QwipiTransDetailsVO();
                    transDetailsVO.setMerNo(fromid);
                    transDetailsVO.setOrderDesc(sDescription);

                    GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();

                    QwipiRequestVO requestVO = new QwipiRequestVO(cardDetailsVO, addressDetailsVO, transDetailsVO);
                    QwipiResponseVO responseVO =(QwipiResponseVO) pg.processInquiry(requestVO);
                    AuditTrailVO auditTrailVO=new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(toid);
                    auditTrailVO.setActionExecutorName("Admin Cron");


                    if(responseVO!=null)
                    {
                        if((responseVO.getResult().trim()).equals("O"))
                        {
                            if(responseVO.getStatus().equals("A"))
                            {
                                updatequery = "update transaction_qwipi set status='capturesuccess', captureamount=?, remark='Payment Success', qwipiPaymentOrderNumber=? where trackingid=?";
                                ps = connection.prepareStatement(updatequery);
                                ps.setString(1,captureAmount);
                                ps.setString(2,responseVO.getId());
                                ps.setString(3,trackingid);
                                int i = ps.executeUpdate();
                                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"capturesuccess",connection);
                                if(i==1)
                                {
                                    responseVO.setOperation("01");
                                    responseVO.setResultCode("0");
                                    responseVO.setPaymentOrderNo(responseVO.getId());
                                    responseVO.setRemark("Payment Success");
                                    //
                                    ActionEntry entry = new ActionEntry();

                                    int actionEntry = entry.actionEntryForQwipi(trackingid,captureAmount.toString(),ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL,null,responseVO,auditTrailVO);
                                    entry.closeConnection();
                                    //

                                    if(actionEntry==1)
                                    {
                                        success.append("Success  treatment(Status:- authstarted to capturesuccess) for trackingid : "+ trackingid);
                                        success.append("<br>");
                                    }
                                    else
                                    {
                                        success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to capturesuccess) for trackingid : "+ trackingid);
                                        success.append("<br>");
                                    }
                                }
                                else
                                {
                                    success.append("Failed  treatment in Primary Table. Cant continue for Detail Table Treatment(Status:- authstarted to capturesuccess) for trackingid : "+ trackingid);
                                    success.append("<br>");
                                }
                            }
                            if(responseVO.getStatus().equals("D"))
                            {
                                updatequery = "update transaction_qwipi set status='authfailed', remark='Payment Declined', qwipiPaymentOrderNumber=? where trackingid=?";
                                ps = connection.prepareStatement(updatequery);
                                ps.setString(1,responseVO.getId());
                                ps.setString(2,trackingid);
                                int i = ps.executeUpdate();
                                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"authfailed",connection);
                                if(i==1)
                                {
                                    responseVO.setOperation("01");
                                    responseVO.setResultCode("1");
                                    responseVO.setPaymentOrderNo(responseVO.getId());
                                    responseVO.setRemark("Payment Declined");
                                    //
                                    ActionEntry entry = new ActionEntry();
                                    int actionEntry = entry.actionEntryForQwipi(trackingid,captureAmount.toString(),ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,null,responseVO,auditTrailVO);
                                    entry.closeConnection();
                                    //

                                    if(actionEntry==1)
                                    {
                                        success.append("Success  treatment(Status:- authstarted to authfailed) for trackingid : "+ trackingid);
                                        success.append("<br>");
                                    }
                                    else
                                    {
                                        success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to authfailed) for trackingid : "+ trackingid);
                                        success.append("<br>");
                                    }
                                }
                                else
                                {
                                    success.append("Failed  treatment in Primary Table. Cant continue for Detail Table Treatment(Status:- authstarted to authfailed) for trackingid : "+ trackingid);
                                    success.append("<br>");
                                }
                            }
                        }
                        else
                        {
                            //QWIPI Query Operation Failed as it returns X. Transaction status to be set as 'Failed'
                            updatequery = "update transaction_qwipi set status='failed', remark='Cancelled By Admin' where trackingid=?";
                            ps = connection.prepareStatement(updatequery);
                            ps.setString(1,trackingid);
                            int i = ps.executeUpdate();
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,"failed",connection);
                            if(i==1)
                            {
                                success.append("Success  treatment(Status:- authstarted to failed) for trackingid : "+ trackingid);
                                success.append("<br>");
                            }
                            else
                            {
                                success.append("Failed  treatment(Status:- authstarted to failed) for trackingid : "+ trackingid);
                                success.append("<br>");
                            }
                        }
                    }
                    else
                    {
                        success.append("Failed  treatment as NULL Response from BANK (Status:- authstarted) for trackingid : "+ trackingid);
                        success.append("<br>");
                    }


            }
        }

        catch (PZTechnicalViolationException e)
        {
            success.append("Error while giving treatment : " +e.getMessage() + ". " + "(Status:- authstarted) for trackingid : "+ trackingid);
            success.append("<br>");
            logger.error("PZTechnicalViolationException",e);
            PZExceptionHandler.handleTechicalCVEException(e,toid,"QWIPI Reconcilation cron");
        }
        catch (PZConstraintViolationException e)
        {
            success.append("Error while giving treatment : " +e.getMessage() + ". " + "(Status:- authstarted) for trackingid : "+ trackingid);
            success.append("<br>");
            logger.error("PZConstraintViolationException",e);
            PZExceptionHandler.handleCVEException(e,toid,"QWIPI Reconcilation cron");
        }
        catch (PZDBViolationException e)
        {
            success.append("Error while giving treatment : " +e.getMessage() + ". " + "(Status:- authstarted) for trackingid : "+ trackingid);
            success.append("<br>");
            logger.error("PZDBViolationException",e);
            PZExceptionHandler.handleDBCVEException(e,toid,"QWIPI Reconcilation cron");
        }
        catch (SQLException e)
        {
            success.append("Error while fetching record from ResultSet in WHILE loop");
            success.append("<br>");
            PZExceptionHandler.raiseAndHandleDBViolationException(QWIPIReconciliationCron.class.getName(),"qwipiReconciliationCron()",null,"common","DB Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),toid,"Qwipi reconcilation cron");
            logger.error("SQL EXCEPTION",e);
        }
        catch (SystemError systemError)
        {
            success.append("Error while fetching record from ResultSet in WHILE loop");
            success.append("<br>");
            PZExceptionHandler.raiseAndHandleDBViolationException(QWIPIReconciliationCron.class.getName(),"qwipiReconciliationCron()",null,"common","DB Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,systemError.getMessage(),systemError.getCause(),toid,"Qwipi reconcilation cron");
            logger.error("SQL EXCEPTION",systemError);
        }
        finally
        {
            Database.closeConnection(connection);
        }

            SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
            sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "QWIPI", success.toString(),null);
        return success.toString();
    }
    public String qwipiAuthStartedCron()
    {
        //TODO: Fetch All authstarted transactions

        //TODO: Fill the VO Objects with -
        // (1)Merchant Number = qwipiRequestVO.getTransDetails().getMerNo()
        // (2)Order Number (Description) = qwipiRequestVO.getTransDetails().getOrderDesc()
        // (3) MD5Info(merchantNo+OrderNo+Md5Key) = qwipiRequestVO.getBillingAddr().getMd5info()
        //TODO: Inquire whether they are successful or not at QWIPI End. Merchant Number; Order Number (Description); MD5Info(merchantNo+OrderNo+Md5Key)


        //TODO: Result - O=Query check Successful; X= Query check Failed. Status - A=Transaction Successful; D=Transaction Failed
        //TODO: update in our DB
        return null;
    }
    public String getMD5Info(String sMerchantNo, String sAccountId, String sOrderNo)
    {
        Connection con = null;
        String sQuery="select midkey from gateway_accounts_qwipi where mid=? and accountid=?";
        String sMD5Key=null;
        String sMD5Info=null;
        try
        {
            con= Database.getConnection();
            PreparedStatement p= con.prepareStatement(sQuery);
            p.setString(1,sMerchantNo);
            p.setString(2,sAccountId);
            ResultSet rs =p.executeQuery();
            if(rs.next())
            {
                sMD5Key=rs.getString("midkey");
            }
            sMD5Info=Functions.convertmd5(sMerchantNo+sOrderNo+sMD5Key);

        }
        catch (NoSuchAlgorithmException e)
        {
            sMD5Info=e.getMessage();
        }
        catch (SystemError systemError)
        {
            sMD5Info=systemError.getMessage();
        }
        catch (SQLException xe)
        {
            sMD5Info=xe.getMessage();
        }
        catch (UnsupportedEncodingException use)
        {
            sMD5Info=use.getMessage();
        }
        finally
        {
            Database.closeConnection(con);
        }
        return sMD5Info;
    }
}
