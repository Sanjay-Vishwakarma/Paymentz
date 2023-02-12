package com.payment.utils;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.common.core.CommResponseVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/29/14
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionUtilsDAO
{
    private static Logger log = new Logger(TransactionUtilsDAO.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TransactionUtilsDAO.class.getName());
    private Functions functions = new Functions();

    public boolean getPartnerCombinationCheck(String toid,String totype,CommonValidatorVO commonValidatorVO)
    {
        boolean isPartnerAuthentic=false;
        Connection connection=null;
        PreparedStatement pstmt=null;
        String qry="";
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        try
        {
            connection = Database.getConnection();
            if(toid!=null && !toid.equals(""))
            {
                qry="select partnerId,partnerName,logoName from partners where partnerId=(select partnerId from members where memberid=?) and partnerName=?";
                pstmt=connection.prepareStatement(qry);
                pstmt.setString(1,toid);
                pstmt.setString(2,totype);
                ResultSet rs= pstmt.executeQuery();
                if(rs.next())
                {
                    isPartnerAuthentic=true;
                }
            }
            else
            {
                qry="select partnerId,partnerName,logoName from partners where partnerName=?";
                pstmt=connection.prepareStatement(qry);
                pstmt.setString(1,totype);
                ResultSet rs= pstmt.executeQuery();
                if(rs.next())
                {
                    merchantDetailsVO.setLogoName(rs.getString("logoName"));
                    merchantDetailsVO.setPartnerId(rs.getString("partnerId"));
                    merchantDetailsVO.setPartnerName(rs.getString("partnerName"));
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Error",e);
        }
        finally {
            Database.closeConnection(connection);
        }
        return isPartnerAuthentic;
    }

    public String checkValueNumeric(String paymentType,String cardType,String TerminalId)
    {
        Functions functions=new Functions();
        String error="";
        ErrorCodeVO errorCodeVO=null;
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        try
        {
            if(functions.isValueNull(paymentType))
            {
                if(functions.isNumericVal(paymentType))
                {
                    Integer.parseInt(paymentType);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_TYPE);
                    error = errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ ErrorMessages.INVALID_PAYMENT_TYPE+"|";
                }
            }

            if(functions.isValueNull(cardType))
            {
                if(functions.isNumericVal(cardType))
                {
                    Integer.parseInt(cardType);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CARD_TYPE);
                    error = errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ ErrorMessages.INVALID_CARD_TYPE+"|";
                }
            }

            if(functions.isValueNull(TerminalId))
            {
                if(functions.isNumericVal(TerminalId))
                {
                    Integer.parseInt(TerminalId);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TERMINALID);
                    error = errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ ErrorMessages.INVALID_TERMINALID+"|";
                }
            }
        }
        catch (NumberFormatException e)
        {

            error = "Kindly Pass Numeric value in paymentType , Card Type and Terminal ID fields. ";

        }
        return error;
    }

    public void updateAuthstartedTransactionforCup(CommonValidatorVO commonValidatorVO,String trackingid)
    {
        Connection connection=null;
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        PreparedStatement preparedStatement=null;
        try
        {
            connection = Database.getConnection();
            String updateRecord= "UPDATE transaction_common SET street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,emailaddr=?,STATUS='authstarted' WHERE trackingid=?";
            preparedStatement = connection.prepareStatement(updateRecord);
            preparedStatement.setString(1,genericAddressDetailsVO.getStreet());
            preparedStatement.setString(2,genericAddressDetailsVO.getCountry());
            preparedStatement.setString(3,genericAddressDetailsVO.getCity());
            preparedStatement.setString(4,genericAddressDetailsVO.getState());
            preparedStatement.setString(5,genericAddressDetailsVO.getZipCode());
            preparedStatement.setString(6,genericAddressDetailsVO.getPhone());
            preparedStatement.setString(7,genericAddressDetailsVO.getTelnocc());
            preparedStatement.setString(8,genericAddressDetailsVO.getEmail());
            preparedStatement.setString(9,trackingid);
            int i = preparedStatement.executeUpdate();

            //Update Bin Details
            //transaction.updateBinDetails(commonValidatorVO.getTrackingid(),genericCardDetailsVO.getCardNum(),merchantDetailsVO.getAccountId(),genericAddressDetailsVO.getEmail(),genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());

            String qry="insert into bin_details (icicitransid, accountid) values(?,?)";
            PreparedStatement pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,trackingid);
            pstmt.setString(2,commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.executeUpdate();
            // UpdateActionEntry
            //entry.actionEntryForGenericTransaction(tablename, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, genericAddressDetailsVO.getIp(), null);

        }
        catch (SystemError systemError)
        {
            log.error("System error in updateAuthstartedTransactionforCup", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException in updateAuthstartedTransactionforCup",e);
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }

    public int actionEntryForInpay(String trackingId,String amount,String action,String status,CommResponseVO commResponseVO,String ipaddress,AuditTrailVO auditTrailVO)throws SystemError
    {
        String responsecode = "";
        String dateTime = "";
        String remark = "";
        String transactionid = "";
        String transactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String actionExId="0";
        String actionExname="";
        int results = 0;
        if (commResponseVO != null)
        {
            transactionid = commResponseVO.getTransactionId();
            transactionstatus = commResponseVO.getStatus();
            responsedescriptor = commResponseVO.getDescriptor();
            responseTime = commResponseVO.getResponseTime();
            transType = commResponseVO.getTransactionType();

        }
        if(auditTrailVO != null)
        {

            actionExId=auditTrailVO.getActionExecutorId();
            actionExname=auditTrailVO.getActionExecutorName();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            cn = Database.getConnection();
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, amount);
            pstmt.setString(3, action);
            pstmt.setString(4, status);
            pstmt.setString(5, remark);
            pstmt.setString(6, transactionid);
            pstmt.setString(7, transactionstatus);
            pstmt.setString(8, responsecode);
            pstmt.setString(9, responseTime);
            pstmt.setString(10, responsedescription);
            pstmt.setString(11, responsedescriptor);
            pstmt.setString(12, responsehashinfo);
            pstmt.setString(13, transType);
            pstmt.setString(14, ipaddress);
            pstmt.setString(15, actionExId);
            pstmt.setString(16, actionExname);
            results = pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            log.error("Exception in Action Entry of InPayServlet",e);
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }

    public void updateAuthstartedTransaction(CommonValidatorVO commonValidatorVO,String trackingid,String tablename)
    {
        Connection connection=null;
        Transaction transaction=new Transaction();
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        PreparedStatement preparedStatement=null;

        CommResponseVO commResponseVO=new CommResponseVO();
        commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        try
        {
            connection = Database.getConnection();
            if(tablename.equals("transaction_common"))
            {
                String updateRecord= "UPDATE "+tablename+" SET ccnum=?, firstname=? , lastname=? ,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,telnocc =?,expdate=?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(2,genericAddressDetailsVO.getFirstname());
                preparedStatement.setString(3,genericAddressDetailsVO.getLastname());
                preparedStatement.setString(4,genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
                preparedStatement.setString(5,genericAddressDetailsVO.getStreet());
                preparedStatement.setString(6,genericAddressDetailsVO.getCountry());
                preparedStatement.setString(7,genericAddressDetailsVO.getCity());
                preparedStatement.setString(8,genericAddressDetailsVO.getState());
                preparedStatement.setString(9,genericAddressDetailsVO.getZipCode());
                preparedStatement.setString(10,genericAddressDetailsVO.getPhone());
                preparedStatement.setString(11,genericAddressDetailsVO.getTelnocc());
                preparedStatement.setString(12, PzEncryptor.encryptExpiryDate(genericCardDetailsVO.getExpMonth() + "/" + genericCardDetailsVO.getExpYear()));
                preparedStatement.setString(13, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(14,genericAddressDetailsVO.getEmail());
                preparedStatement.setString(15,genericAddressDetailsVO.getIp());
                preparedStatement.setString(16,trackingid);
            }
            else
            {
                String updateRecord= "UPDATE "+tablename+" SET ccnum=?, firstname=? , lastname=? ,name=?,street=?,country =?,city =?,state =?,zip =?,telno =?,birthdate =?,telnocc =?,LANGUAGE =?,expdate=?,cardtype=?,emailaddr=?,STATUS='authstarted',ipaddress=? WHERE trackingid=?";
                preparedStatement = connection.prepareStatement(updateRecord);
                preparedStatement.setString(1, PzEncryptor.encryptPAN(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(2,genericAddressDetailsVO.getFirstname());
                preparedStatement.setString(3,genericAddressDetailsVO.getLastname());
                preparedStatement.setString(4,genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
                preparedStatement.setString(5,genericAddressDetailsVO.getStreet());
                preparedStatement.setString(6,genericAddressDetailsVO.getCountry());
                preparedStatement.setString(7,genericAddressDetailsVO.getCity());
                preparedStatement.setString(8,genericAddressDetailsVO.getState());
                preparedStatement.setString(9,genericAddressDetailsVO.getZipCode());
                preparedStatement.setString(10,genericAddressDetailsVO.getPhone());
                preparedStatement.setString(11,genericAddressDetailsVO.getBirthdate());
                preparedStatement.setString(12,genericAddressDetailsVO.getTelnocc());
                preparedStatement.setString(13,genericAddressDetailsVO.getLanguage());
                preparedStatement.setString(14, PzEncryptor.encryptExpiryDate(genericCardDetailsVO.getExpMonth() + "/" + genericCardDetailsVO.getExpYear()));
                preparedStatement.setString(15, Functions.getCardType(genericCardDetailsVO.getCardNum()));
                preparedStatement.setString(16,genericAddressDetailsVO.getEmail());
                preparedStatement.setString(17,genericAddressDetailsVO.getIp());
                preparedStatement.setString(18,trackingid);
            }
            int i = preparedStatement.executeUpdate();

            //Update Bin Details
            transaction.updateBinDetails(commonValidatorVO.getTrackingid(),genericCardDetailsVO.getCardNum(),merchantDetailsVO.getAccountId(),genericAddressDetailsVO.getEmail(),genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());
            AuditTrailVO auditTrailVO=new AuditTrailVO();
            auditTrailVO.setActionExecutorId(merchantDetailsVO.getMemberId());
            auditTrailVO.setActionExecutorName("MerchantVT");
            // UpdateActionEntry
            entry.actionEntryForGenericTransaction(tablename, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO,auditTrailVO);

        }
        catch (SystemError systemError)
        {
            log.error("System error in updateAuthstartedTransaction",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException in updateAuthstartedTransaction",e);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException--->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            Database.closeConnection(connection);
        }
    }
    public void updateTransactionAfterResponse(String tablename,String status,String amount,String ip,String machineid,String paymentid,String remark,String dateTime,String trackingId,String rrn,String arn,String authCode,String transaction_mode)
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        try
        {
            if(tablename!=null)
            {
                connection = Database.getConnection();
                String updateRecord="";
                if(tablename.equals("transaction_common"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        transactionLogger.debug("TransactionUtilsDAO updateTransactionAfterResponse ::::::::: DB Call");
                        updateRecord="update transaction_common set status=?,captureamount=?,ipaddress=?,machineid=?,paymentid=?,remark=?,rrn=?,arn=?,authorization_code=?,transaction_mode=?, successtimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                        preparedStatement.setString(3,ip);
                        preparedStatement.setString(4,machineid);
                        preparedStatement.setString(5,paymentid);
                        preparedStatement.setString(6,remark);
                        preparedStatement.setString(7,rrn);
                        preparedStatement.setString(8,arn);
                        preparedStatement.setString(9,authCode);
                        preparedStatement.setString(10,transaction_mode);
                        preparedStatement.setString(11, functions.getTimestamp());
                        preparedStatement.setString(12,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        transactionLogger.debug("TransactionUtilsDAO updateTransactionAfterResponse ::::::::: DB Call");
                        updateRecord="update transaction_common set status=?,ipaddress=?,machineid=?,paymentid=?,remark=?,rrn=?,arn=?,authorization_code=?,transaction_mode=?, failuretimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,rrn);
                        preparedStatement.setString(7,arn);
                        preparedStatement.setString(8,authCode);
                        preparedStatement.setString(9,transaction_mode);
                        preparedStatement.setString(10, functions.getTimestamp());
                        preparedStatement.setString(11,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                else if(tablename.equals("transaction_qwipi"))
                {
                    transactionLogger.debug("TransactionUtilsDAO updateTransactionAfterResponse ::::::::: DB Call");
                    updateRecord="update transaction_qwipi set status = ?,captureamount=?,ipaddress=?,mid=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    preparedStatement.setString(3,ip);
                    preparedStatement.setString(4,machineid);
                    preparedStatement.setString(5,paymentid);
                    preparedStatement.setString(6,remark);
                    preparedStatement.setString(7,dateTime);
                    preparedStatement.setString(8,trackingId);
                    preparedStatement.executeUpdate();
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_ecore set status=?,captureamount=?,ipaddress=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                        preparedStatement.setString(3,ip);
                        preparedStatement.setString(4,machineid);
                        preparedStatement.setString(5,paymentid);
                        preparedStatement.setString(6,remark);
                        preparedStatement.setString(7,dateTime);
                        preparedStatement.setString(8,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord="update transaction_ecore set status=?,ipaddress=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,dateTime);
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
            }
        }
        catch (SystemError systemError)
        {
            log.error("System error in updateTransactionAfterResponse",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException in updateTransactionAfterResponse",e);
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }

    public void insertFailedTransaction(String trackingId,String remark,String tableName,String amount,String ipaddress)
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String updatesql="";
            ActionEntry entry=new ActionEntry();
            if(tableName.equals("transaction_qwipi"))
            {
                updatesql="UPDATE transaction_qwipi SET remark=?,STATUS=? WHERE trackingid=?";
                entry.actionEntryForQwipi(trackingId,amount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,ipaddress,null,null);
            }
            else if(tableName.equals("transaction_ecore"))
            {
                updatesql="UPDATE transaction_ecore SET remark=?,STATUS=? WHERE trackingid=?";
                entry.actionEntryForEcore(trackingId,amount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,ipaddress,null,null);
            }
            else if(tableName.equals("transaction_common"))
            {
                updatesql="UPDATE transaction_common SET remark=?,STATUS=?, failuretimestamp = ? WHERE trackingid=?";
                entry.actionEntryForCommon(trackingId,amount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,null,null,ipaddress);
            }
            PreparedStatement preparedStatement=connection.prepareStatement(updatesql);
            preparedStatement.setString(1,remark);
            preparedStatement.setString(2,"authfailed");
            if(tableName.equals("transaction_common"))
            {
                preparedStatement.setString(3, functions.getTimestamp());
                preparedStatement.setString(4,trackingId);
            }
            else
            {
                preparedStatement.setString(3,trackingId);
            }

            preparedStatement.executeUpdate();
        }
        catch (SystemError systemError)
        {
            log.error("System error while set failed status in transaction table",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException",e);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException--->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(connection);
        }


    }
    public String sendTransactionMail(String status,String trackingId,String remark,String mailSent,String billingDescriptor)
    {
        String mailtransactionStatus="Failed";
        if("capturesuccess".equalsIgnoreCase(status) || "authsuccessful".equalsIgnoreCase(status))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus="Successful ("+remark+")";
            }
            else
            {
                mailtransactionStatus="Successful";
            }
        }
        else if("authstarted".equalsIgnoreCase(status))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = remark;
            }
        }
        else if("authfailed".equalsIgnoreCase(status))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = mailtransactionStatus +" ("+remark+")";
            }
        }

        SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
        //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION,trackingId,mailtransactionStatus,null);
       /* if("Y".equalsIgnoreCase(mailSent))
        {*/
            //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDescriptor);
       /* }*/
        return mailtransactionStatus;
    }

    public void insertFailedTransaction(String trackingId,String remark,String tableName,String status,String amount,String ipaddress)
    {
        Connection connection=null;
        try
        {
            connection = Database.getConnection();
            String updatesql="";
            ActionEntry entry=new ActionEntry();
            if(tableName.equals("transaction_qwipi"))
            {
                updatesql="UPDATE transaction_qwipi SET remark=?,STATUS=? WHERE trackingid=?";
                entry.actionEntryForQwipi(trackingId,amount,ActionEntry.ACTION_FAILED,ActionEntry.STATUS_FAILED,ipaddress,null,null);
            }
            else if(tableName.equals("transaction_ecore"))
            {
                updatesql="UPDATE transaction_ecore SET remark=?,STATUS=? WHERE trackingid=?";
                entry.actionEntryForEcore(trackingId,amount,ActionEntry.ACTION_FAILED,ActionEntry.STATUS_FAILED,ipaddress,null,null);
            }
            else if(tableName.equals("transaction_common"))
            {
                updatesql="UPDATE transaction_common SET remark=?,STATUS=? WHERE trackingid=?";
                entry.actionEntryForCommon(trackingId,amount,ActionEntry.ACTION_FAILED,ActionEntry.STATUS_FAILED,null,null,ipaddress);
            }
            PreparedStatement preparedStatement=connection.prepareStatement(updatesql);
            preparedStatement.setString(1,remark);
            preparedStatement.setString(2,status);
            preparedStatement.setString(3,trackingId);
            preparedStatement.executeUpdate();
        }
        catch (SystemError systemError)
        {
            log.error("System error while set failed status in transaction table",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException",e);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException--->", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }
    public void updateTransactionAfterResponseFor3DRouting(String tablename,String status,String amount,String ip,String machineid,String paymentid,String remark,String dateTime,String trackingId,String rrn,String arn,String authCode,String terminalId,String accountId,String fromid,String transaction_mode)
    {
        Connection connection=null;
        PreparedStatement preparedStatement=null;
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        try
        {
            if(tablename!=null)
            {
                connection = Database.getConnection();
                String updateRecord="";
                if(tablename.equals("transaction_common"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        transactionLogger.debug("TransactionUtilsDAO updateTransactionAfterResponse ::::::::: DB Call");
                        updateRecord="update transaction_common set status=?,captureamount=?,ipaddress=?,machineid=?,paymentid=?,remark=?,rrn=?,arn=?,authorization_code=?,terminalId=?,accountId=?,fromid=?,transaction_mode=?, successtimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                        preparedStatement.setString(3,ip);
                        preparedStatement.setString(4,machineid);
                        preparedStatement.setString(5,paymentid);
                        preparedStatement.setString(6,remark);
                        preparedStatement.setString(7,rrn);
                        preparedStatement.setString(8,arn);
                        preparedStatement.setString(9,authCode);
                        preparedStatement.setString(10,terminalId);
                        preparedStatement.setString(11,accountId);
                        preparedStatement.setString(12,fromid);
                        preparedStatement.setString(13,transaction_mode);
                        preparedStatement.setString(14,functions.getTimestamp());
                        preparedStatement.setString(15,trackingId);
                        transactionLogger.error("updateTransactionAfterResponse --->"+preparedStatement);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        transactionLogger.debug("TransactionUtilsDAO updateTransactionAfterResponse ::::::::: DB Call");
                        updateRecord="update transaction_common set status=?,ipaddress=?,machineid=?,paymentid=?,remark=?,rrn=?,arn=?,authorization_code=?,terminalId=?,accountId=?,fromid=?,transaction_mode=?, failuretimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,rrn);
                        preparedStatement.setString(7,arn);
                        preparedStatement.setString(8,authCode);
                        preparedStatement.setString(9,terminalId);
                        preparedStatement.setString(10,accountId);
                        preparedStatement.setString(11,fromid);
                        preparedStatement.setString(12,transaction_mode);
                        preparedStatement.setString(13, functions.getTimestamp());
                        preparedStatement.setString(14,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                else if(tablename.equals("transaction_qwipi"))
                {
                    transactionLogger.debug("TransactionUtilsDAO updateTransactionAfterResponse ::::::::: DB Call");
                    updateRecord="update transaction_qwipi set status = ?,captureamount=?,ipaddress=?,mid=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    preparedStatement.setString(3,ip);
                    preparedStatement.setString(4,machineid);
                    preparedStatement.setString(5,paymentid);
                    preparedStatement.setString(6,remark);
                    preparedStatement.setString(7,dateTime);
                    preparedStatement.setString(8,trackingId);
                    preparedStatement.executeUpdate();
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_ecore set status=?,captureamount=?,ipaddress=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                        preparedStatement.setString(3,ip);
                        preparedStatement.setString(4,machineid);
                        preparedStatement.setString(5,paymentid);
                        preparedStatement.setString(6,remark);
                        preparedStatement.setString(7,dateTime);
                        preparedStatement.setString(8,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord="update transaction_ecore set status=?,ipaddress=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,dateTime);
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
            }
        }
        catch (SystemError systemError)
        {
            log.error("System error in updateTransactionAfterResponse",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException in updateTransactionAfterResponse",e);
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
    }
}
