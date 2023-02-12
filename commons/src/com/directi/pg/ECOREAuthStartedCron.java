package com.directi.pg;

import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/19/14
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECOREAuthStartedCron
{
    private static Logger logger = new Logger(ECOREAuthStartedCron.class.getName());
    public static void main(String args[])throws SystemError
    {
        ECOREAuthStartedCron ecoreAuthStartedCron = new ECOREAuthStartedCron();
        String sResponse  = ecoreAuthStartedCron.ecoreAuthStartedCron(new Hashtable());
        //System.out.println("sResponse =" + sResponse);
    }

    public String ecoreAuthStartedCron(Hashtable ht)
    {
        StringBuffer success = new StringBuffer();
        success.append("Authstarted Transaction List"+"<br>");

        AbstractPaymentGateway abstractPaymentGateway = null;
        Connection connection=null;
        String trackingid = null;
        String captureAmount = null;
        String accountid = null;
        String fromid = null;
        String sDescription= null;
        String toid=null;
        Database db = new Database();
        //String selectQuery = "SELECT trackingid , ecorePaymentOrderNumber,status,amount, description, remark, accountid, fromid FROM transaction_ecore WHERE STATUS='authstarted' and (TIMESTAMPDIFF(MINUTE, FROM_UNIXTIME(dtstamp), CURRENT_TIMESTAMP)) > 15";
        String selectQuery = "SELECT trackingid ,toid, ecorePaymentOrderNumber,status,amount, description, remark, accountid, fromid FROM transaction_ecore WHERE STATUS='authstarted'";

        ResultSet resultSet = null;
        PreparedStatement ps = null;
        String updateQuery = null;

        try
        {
            connection = db.getConnection();
            ps = connection.prepareStatement(selectQuery);
            resultSet = ps.executeQuery();
            while (resultSet.next())
            {
                AuditTrailVO auditTrailVO=new AuditTrailVO();
                trackingid = resultSet.getString("trackingid");
                captureAmount = resultSet.getString("amount");
                accountid = resultSet.getString("accountid");
                sDescription = resultSet.getString("description");
                fromid = resultSet.getString("fromid");
                toid=resultSet.getString("toid");
                //System.out.println(trackingid);
                abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountid);

                EcoreTransDetailsVO transDetailsVO = new EcoreTransDetailsVO();
                transDetailsVO.setMerNo(fromid);
                transDetailsVO.setOrderDesc(sDescription);
                transDetailsVO.setAmount(captureAmount);
                transDetailsVO.setOrderId(trackingid);
                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("ECOREAuthStartedCron");
                //System.out.println("fromid"+fromid+"desc"+sDescription+"capAmt"+captureAmount+"accId"+accountid+"trackingId"+trackingid);

                EcoreAddressDetailsVO addressDetailsVO = new EcoreAddressDetailsVO();

                GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();

                EcoreRequestVO requestVO = new EcoreRequestVO(cardDetailsVO,addressDetailsVO,transDetailsVO);
                EcoreResponseVO responseVO = (EcoreResponseVO) abstractPaymentGateway.processInquiry(requestVO);
                //System.out.println("resVO"+responseVO);
                //TODO if response is null take proper action on transaction
                if(responseVO!=null)
                {


                    if(responseVO.getResponseCode().trim().equals("100"))//Transaction approved.
                    {
                        //System.out.println("in status 100");
                        updateQuery = "UPDATE transaction_ecore SET STATUS='capturesuccess', captureamount=?, remark='Transaction approved.', ecorePaymentOrderNumber=? WHERE trackingid=?";
                        ps = connection.prepareStatement(updateQuery);
                        ps.setString(1,captureAmount);
                        ps.setString(2,responseVO.getTransactionID());
                        ps.setString(3,trackingid);
                        auditTrailVO.setActionExecutorId("0");
                        auditTrailVO.setActionExecutorName("AuthStarted Cron");
                        int rs = ps.executeUpdate();

                        if(rs == 1)
                        {

                            responseVO.setResponseCode("100");
                            responseVO.setDescription("Transaction approved");
                            responseVO.setTransactionID(responseVO.getTransactionID());

                            ActionEntry entry = new ActionEntry();
                            int actionEntry = entry.actionEntryForEcore(trackingid,captureAmount.toString(),ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL,null,responseVO,auditTrailVO);
                            entry.closeConnection();

                            if(actionEntry == 1)
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
                        else if(responseVO.getResponseCode().trim().equals("101")) //Transaction declined.
                        {
                            updateQuery = "UPDATE transaction_ecore SET STATUS='authfailed', remark='Transaction declined.', ecorePaymentOrderNumber=? WHERE trackingid=?";
                            ps.setString(1,responseVO.getTransactionID());
                            ps.setString(2,trackingid);
                            int i = ps.executeUpdate(updateQuery);

                            if(i == 1)
                            {

                                responseVO.setResponseCode("101");
                                responseVO.setDescription("Transaction Declined");
                                responseVO.setTransactionID(responseVO.getTransactionID());

                                ActionEntry entry = new ActionEntry() ;
                                int actionEntry = entry.actionEntryForEcore(trackingid,captureAmount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,null,responseVO,auditTrailVO);
                                entry.closeConnection();

                                if(actionEntry == 1)
                                {
                                    success.append("Success treatment(Status :- authstarted to authfailed) for trackingid : "+ trackingid);
                                    success.append("<br>");
                                }
                                else
                                {
                                    success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to authfailed) for trackingid : "+ trackingid);
                                    success.append("<br>");
                                }
                            }
                        }
                        else if(responseVO.getResponseCode().trim().equals("501")) //Transaction not found.
                        {
                            updateQuery = "UPDATE transaction_ecore SET STATUS='authfailed', remark='Transaction not found', ecorePaymentOrderNumber=? WHERE trackingid=?";
                            ps.setString(1,responseVO.getTransactionID());
                            ps.setString(2,trackingid);
                            int i = ps.executeUpdate(updateQuery);

                            if(i == 1)
                            {

                                responseVO.setResponseCode("501");
                                responseVO.setDescription("Transaction not found");
                                responseVO.setTransactionID(responseVO.getTransactionID());

                                ActionEntry entry = new ActionEntry() ;
                                int actionEntry = entry.actionEntryForEcore(trackingid,captureAmount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,null,responseVO,auditTrailVO);
                                entry.closeConnection();

                                if(actionEntry == 1)
                                {
                                    success.append("Success treatment(Status :- authstarted to authfailed) for trackingid : "+ trackingid);
                                    success.append("<br>");
                                }
                                else
                                {
                                    success.append("Success Treatment in Primary Table. Failed  treatment in Detail Table(Status:- authstarted to authfailed) for trackingid : "+ trackingid);
                                    success.append("<br>");
                                }
                            }
                        }
                        else
                        {
                            updateQuery = "UPDATE transaction_ecore SET STATUS='failed', remark='Cancelled By Admin'  WHERE trackingid=?";
                            ps.setString(1,trackingid);
                            int i = ps.executeUpdate();

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
                }
                else
                {
                    {
                        success.append("Failed  treatment as NULL Response from BANK (Status:- authstarted) for trackingid : "+ trackingid);
                        success.append("<br>");
                        //System.out.println("no response found");
                    }
                }
            }

        }

        catch (PZTechnicalViolationException e)
        {
            success.append("Failed  treatment as Error (Status:- authstarted) for trackingid : "+ trackingid);
            success.append("<br>");
            logger.error("PZTechnicalViolationException exception",e);
            PZExceptionHandler.handleTechicalCVEException(e,toid,"Ecore Auth started cron");
        }
        catch (PZConstraintViolationException e)
        {
            success.append("Failed  treatment as Error (Status:- authstarted) for trackingid : "+ trackingid);
            success.append("<br>");
            logger.error("PZConstraintViolationException",e);
            PZExceptionHandler.handleCVEException(e,toid,"Ecore Auth started cron");
        }
        catch (PZDBViolationException e)
        {
            success.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : "+ trackingid);
            success.append("<br>");
            logger.error("PZDBViolationException",e);
            PZExceptionHandler.handleDBCVEException(e,toid,"Ecore Auth started cron");
        }
        catch (SQLException e)
        {
            success.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingid);
            success.append("<br>");
            logger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(ECOREAuthStartedCron.class.getName(),"ecoreAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),toid,"Ecore Auth started cron");
        }
        catch (SystemError systemError)
        {
            success.append("Failed  treatment as Error  (Status:- authstarted) for trackingid : " + trackingid);
            success.append("<br>");
            logger.error("SystemError",systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(ECOREAuthStartedCron.class.getName(),"ecoreAuthStartedCron()",null,"common","DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),toid,"Ecore Auth started cron");
        }
        finally
        {
            Database.closeConnection(connection);
        }

        return success.toString();
    }
}