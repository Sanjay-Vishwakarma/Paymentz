package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.CupRequestVO;
import com.directi.pg.core.valueObjects.CupResponseVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 8/23/14
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class CupAuthStartedCron
{
    private static Logger logger = new Logger(CupAuthStartedCron.class.getName());

    public String cupAuthStartedCron(Hashtable ht) throws SystemError
    {
        StringBuffer success = new StringBuffer();
        success.append("Authstarted Transaction List"+"<br>");
         AuditTrailVO auditTrailVO=new AuditTrailVO();
        Connection connection = null;
        ResultSet resultSet = null;
        ResultSet r = null;
        AbstractPaymentGateway pg = null;

        String captureAmount = "";
        String detailId = "";
        String responseTime = "";
        String accountId = "";
        String trackingId = "";
        String merchantId = "";
        String transType = "";
        String isService = "";
        String uStatus = "";
        String actionEntryAction = "";
        String actionEntryStatus = "";

        PreparedStatement ps= null;
        PreparedStatement p1= null;
        PreparedStatement ps2= null;
        String updatequery = null;
        String selectquery = null;
        String query = "";

        Database db = new Database();

        try
        {
            connection = db.getConnection();
            selectquery = "SELECT detailid,responsetime,t.trackingid,t.accountid,tc.amount FROM transaction_common_details tc,transaction_common t WHERE tc.trackingid=t.trackingid AND t.fromtype='CUP' AND t.status='authstarted' AND t.accountid=405";
            p1 = connection.prepareStatement(selectquery);
            resultSet = p1.executeQuery();
            logger.debug("Select Query1------"+selectquery);
            while (resultSet.next())
            {
                detailId = resultSet.getString("detailid");
                captureAmount = resultSet.getString("amount");
                responseTime = resultSet.getString("responsetime");
                accountId = resultSet.getString("accountid");
                trackingId = resultSet.getString("trackingid");

                pg = AbstractPaymentGateway.getGateway(accountId);

                query = "SELECT merchantid,isservice FROM gateway_accounts ga,members,gateway_accounts_cup gac WHERE ga.accountid=? AND gac.accountid=? AND memberid IN (SELECT memberid FROM `member_account_mapping` WHERE accountid=?)";
                ps = connection.prepareStatement(query);
                ps.setString(1,accountId);
                ps.setString(2,accountId);
                ps.setString(3,accountId);
                r=ps.executeQuery();
                if(r.next())
                {
                    merchantId= r.getString("merchantid");
                    isService = r.getString("isservice");

                }
                if ("Y".equals(isService))
                {
                    transType = "01";
                    uStatus = "capturesuccess";
                    actionEntryAction = ActionEntry.ACTION_CAPTURE_SUCCESSFUL;
                    actionEntryStatus = ActionEntry.STATUS_CAPTURE_SUCCESSFUL;
                }
                else
                {
                    transType = "02";
                    uStatus = "authsuccessful";
                    actionEntryAction = ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL;
                    actionEntryStatus = ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL;
                }

                CupRequestVO cupRequestVO = new CupRequestVO();
                cupRequestVO.setTransType(transType);
                cupRequestVO.setMerchantId(merchantId);
                cupRequestVO.setDetailsId(detailId);
                cupRequestVO.setOrderTime(responseTime);

                CupResponseVO cupResponseVO = (CupResponseVO) pg.processQuery(trackingId,cupRequestVO);

                if(cupResponseVO!=null && !cupResponseVO.equals(""))
                {
                    if(cupResponseVO.getResponseCode().trim().equals("00"))
                    {
                        updatequery = "update transaction_common set status='"+uStatus+"',captureamount=? where trackingid=? AND accountid=?";
                        logger.debug("updatequery1------"+updatequery);
                        ps2 = connection.prepareStatement(updatequery);
                        ps2.setString(1,captureAmount);
                        ps2.setString(2,trackingId);
                        ps2.setString(3,accountId);
                        int i = ps2.executeUpdate();
                        if(i==1)
                        {
                            cupResponseVO.setResponseCode("00");
                            cupResponseVO.setStatusDescription("Success!");
                            cupResponseVO.setTransactionID(detailId);
                            cupResponseVO.setProcessingTime(responseTime);
                            auditTrailVO.setActionExecutorId(merchantId);
                            auditTrailVO.setActionExecutorName("Customer");
                            ActionEntry entry = new ActionEntry();

                            int actionEntry = entry.actionEntryForCUP(trackingId,captureAmount,actionEntryAction,actionEntryStatus,cupResponseVO,null,auditTrailVO,null);

                            if(actionEntry==1)
                            {
                                success.append("Success  treatment(Status:- authstarted to capturesuccess) for trackingid : "+trackingId);
                                success.append("<br>");
                            }
                            else
                            {
                                success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to capturesuccess) for trackingid : "+ trackingId);
                                success.append("<br>");
                            }
                        }
                        else
                        {
                            success.append("Failed  treatment in Primary Table. Cant continue for Detail Table Treatment(Status:- authstarted to capturesuccess) for trackingid : "+ trackingId);
                            success.append("<br>");
                        }
                    }
                    else if(cupResponseVO.getResponseCode().equals("01") || cupResponseVO.getResponseCode().equals("02") || cupResponseVO.getResponseCode().equals("02") || cupResponseVO.getResponseCode().equals("03")
                            || cupResponseVO.getResponseCode().equals("06") || cupResponseVO.getResponseCode().equals("11") || cupResponseVO.getResponseCode().equals("14") || cupResponseVO.getResponseCode().equals("15")
                            || cupResponseVO.getResponseCode().equals("18") || cupResponseVO.getResponseCode().equals("18") || cupResponseVO.getResponseCode().equals("20") || cupResponseVO.getResponseCode().equals("21")
                            || cupResponseVO.getResponseCode().equals("36") || cupResponseVO.getResponseCode().equals("37") || cupResponseVO.getResponseCode().equals("39") || cupResponseVO.getResponseCode().equals("40")
                            || cupResponseVO.getResponseCode().equals("41") || cupResponseVO.getResponseCode().equals("42") || cupResponseVO.getResponseCode().equals("43") || cupResponseVO.getResponseCode().equals("56")
                            || cupResponseVO.getResponseCode().equals("57") || cupResponseVO.getResponseCode().equals("60") || cupResponseVO.getResponseCode().equals("61") || cupResponseVO.getResponseCode().equals("71")
                            || cupResponseVO.getResponseCode().equals("80") || cupResponseVO.getResponseCode().equals("81") || cupResponseVO.getResponseCode().equals("82") || cupResponseVO.getResponseCode().equals("83")
                            || cupResponseVO.getResponseCode().equals("85") || cupResponseVO.getResponseCode().equals("86") || cupResponseVO.getResponseCode().equals("87") || cupResponseVO.getResponseCode().equals("88")
                            || cupResponseVO.getResponseCode().equals("89") || cupResponseVO.getResponseCode().equals("90") || cupResponseVO.getResponseCode().equals("93") || cupResponseVO.getResponseCode().equals("95")
                            || cupResponseVO.getResponseCode().equals("97"))
                    {

                        updatequery = "update transaction_common set status='authfailed',captureamount=? where trackingid=? AND accountid=?";
                        logger.debug("updatequery1------"+updatequery);
                        ps2 = connection.prepareStatement(updatequery);
                        ps2.setString(1,captureAmount);
                        ps2.setString(2,trackingId);
                        ps2.setString(3,accountId);
                        int i = ps2.executeUpdate();
                        if(i==1)
                        {
                            cupResponseVO.setResponseCode("01");
                            cupResponseVO.setStatusDescription("Fail!");
                            cupResponseVO.setTransactionID(detailId);
                            cupResponseVO.setProcessingTime(responseTime);
                            auditTrailVO.setActionExecutorId(merchantId);
                            auditTrailVO.setActionExecutorName("Customer");
                            ActionEntry entry = new ActionEntry();

                            int actionEntry = entry.actionEntryForCUP(trackingId,captureAmount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,cupResponseVO,null,auditTrailVO,null);

                            if(actionEntry==1)
                            {
                                success.append("Success  treatment(Status:- authstarted to capturesuccess) for trackingid : "+trackingId);
                                success.append("<br>");
                            }
                            else
                            {
                                success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to capturesuccess) for trackingid : "+ trackingId);
                                success.append("<br>");
                            }
                        }
                        else
                        {
                            success.append("Failed  treatment in Primary Table. Cant continue for Detail Table Treatment(Status:- authstarted to capturesuccess) for trackingid : " + trackingId);
                            success.append("<br>");
                        }
                    }
                    else if(cupResponseVO.getResponseCode().equals("25") || cupResponseVO.getResponseCode().equals("30") || cupResponseVO.getResponseCode().equals("31") || cupResponseVO.getResponseCode().equals("32")
                         || cupResponseVO.getResponseCode().equals("72") || cupResponseVO.getResponseCode().equals("73") || cupResponseVO.getResponseCode().equals("74") || cupResponseVO.getResponseCode().equals("84")
                         || cupResponseVO.getResponseCode().equals("94"))
                    {

                        updatequery = "update transaction_common set status='authfailed',captureamount=? where trackingid=? AND accountid=?";
                        logger.debug("updatequery1------"+updatequery);
                        ps2 = connection.prepareStatement(updatequery);
                        ps2.setString(1,captureAmount);
                        ps2.setString(2,trackingId);
                        ps2.setString(3,accountId);
                        int i = ps2.executeUpdate();
                        if(i==1)
                        {
                            cupResponseVO.setResponseCode("01");
                            cupResponseVO.setStatusDescription("Fail!");
                            cupResponseVO.setTransactionID(detailId);
                            cupResponseVO.setProcessingTime(responseTime);
                            auditTrailVO.setActionExecutorId(merchantId);
                            auditTrailVO.setActionExecutorName("Customer");
                            ActionEntry entry = new ActionEntry();

                            int actionEntry = entry.actionEntryForCUP(trackingId,captureAmount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,cupResponseVO,null,auditTrailVO,null);

                            if(actionEntry==1)
                            {
                                success.append("Success  treatment(Status:- authstarted to capturesuccess) for trackingid : "+trackingId);
                                success.append("<br>");
                            }
                            else
                            {
                                success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to capturesuccess) for trackingid : "+ trackingId);
                                success.append("<br>");
                            }
                        }
                        else
                        {
                            success.append("Failed  treatment in Primary Table. Cant continue for Detail Table Treatment(Status:- authstarted to capturesuccess) for trackingid : " + trackingId);
                            success.append("<br>");
                        }
                    }
                    else
                    {
                        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
                        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT,"","CUP",success.toString(),null);
                    }
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("SQLException in CupAuthStartedCron",e);
            logger.error("SQLException in CupAuthStartedCron",e);
            PZExceptionHandler.handleDBCVEException(e,null,"Cup Auth Started Cron");
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraintViolationException in CupAuthStartedCron",e);
            PZExceptionHandler.handleCVEException(e,null,"Cup Auth Started Cron");
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZGenericConstraintViolationException in CupAuthStartedCron",e);
            PZExceptionHandler.handleGenericCVEException(e,null,"Cup Auth Started Cron");
        }
        catch (SQLException e)
        {
            logger.error("SQLException in CupAuthStartedCron",e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in CupAuthStartedCron",systemError);
        }
        finally {
            db.closeConnection(connection);
        }


        return success.toString();
    }
}
