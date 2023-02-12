package payment.util;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 9/17/14
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallPaymentDAO
{
    private static Logger log = new Logger(SingleCallPaymentDAO.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallPaymentDAO.class.getName());

    public static String getMD5HashVal(String str) throws PZTechnicalViolationException
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest) {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SingleCallPaymentDAO.java","getMD5HashVal()",null,"Transaction","UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SingleCallPaymentDAO.java","getMD5HashVal()",null,"Transaction","UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return encryptedString;
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
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        auditTrailVO.setActionExecutorId(merchantDetailsVO.getMemberId());
        auditTrailVO.setActionExecutorName("Customer");
        CommResponseVO commResponseVO=new CommResponseVO();
        commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        try
        {

            connection = Database.getConnection();
            if(tablename.equals("transaction_common"))
            {
                transactionLogger.debug("inside transaction_common of updateAuthstartedTransaction");
                transactionLogger.debug("card type value:" + Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
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
                preparedStatement.setString(13, Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
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
                preparedStatement.setString(15, Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
                preparedStatement.setString(16,genericAddressDetailsVO.getEmail());
                preparedStatement.setString(17,genericAddressDetailsVO.getIp());
                preparedStatement.setString(18,trackingid);
            }
            int i = preparedStatement.executeUpdate();

            //Update Bin Details
            transaction.updateBinDetails(commonValidatorVO.getTrackingid(),genericCardDetailsVO.getCardNum(),merchantDetailsVO.getAccountId(),genericAddressDetailsVO.getEmail(),genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname());

            // UpdateActionEntry
            log.debug("before actionEntryForGenericTransaction "+auditTrailVO.getActionExecutorId());
            //entry.actionEntryForGenericTransaction(tablename, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO,auditTrailVO);
            log.debug("after actionEntryForGenericTransaction "+auditTrailVO.getActionExecutorId());

        }
        catch (SystemError se)
        {
        }
        catch (SQLException e)
        {
            log.error("SQLException in updateAuthstartedTransaction",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }

    public String transactionExistCheck(String tableName,String trackingId,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String message=null;
        Transaction transaction=new Transaction();
        HashMap transDetails= transaction.getTransactionDetailsFromTrackingID(trackingId,tableName);
        if(!transDetails.isEmpty())
        {
            if (!transDetails.get("status").equals("begun"))
            {
               message = "ERROR!!! Your Transaction is  already being processed. This can occur if you clicked on the back button and tried to submit this Transaction again. The transaction may succeed or fail, however the status of the Transaction will have to be set manually. Please contact the Merchant to verify the status of the transaction with the following reference numbers and inform him of this message. PLEASE DO NOT TRY to execute this transaction once more from the beginning, or you may end up charging your card twice.<br><br> Please visit at "+ ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" to know more about the reason for this error.";
            }
        }
        else
        {
           message = "Transaction is not found from database.";
        }

        return message;
    }

    public int updateTransactionAfterResponse(String tablename,String status,String amount,String ip,String machineid,String paymentid,String remark,String dateTime,String trackingId,String eci,String rrn,String arn,String authCode,String transaction_mode) throws PZDBViolationException
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        StatusSyncDAO statusSyncDAO         = new StatusSyncDAO();
        Functions functions                 = new Functions();
        try
        {
            if(tablename!=null)
            {
                connection          = Database.getConnection();
                String updateRecord = "";
                /*if(tablename.equals("transaction_qwipi"))
                {
                    updateRecord="update transaction_qwipi set status = ?,captureamount=?,mid=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,dateTime);
                    preparedStatement.setString(7,trackingId);
                    preparedStatement.executeUpdate();
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_ecore set status=?,captureamount=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                       // preparedStatement.setString(3,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,dateTime);
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord="update transaction_ecore set status=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                       // preparedStatement.setString(2,ip);
                        preparedStatement.setString(2,machineid);
                        preparedStatement.setString(3,paymentid);
                        preparedStatement.setString(4,remark);
                        preparedStatement.setString(5,dateTime);
                        preparedStatement.setString(6,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                else */if(tablename.equals("transaction_common"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_common set status=?,captureamount=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,transaction_mode=?, successtimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                        //preparedStatement.setString(3,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,eci);
                        preparedStatement.setString(7,rrn);
                        preparedStatement.setString(8,arn);
                        preparedStatement.setString(9,authCode);
                        preparedStatement.setString(10,transaction_mode);
                        preparedStatement.setString(11, functions.getTimestamp());
                        preparedStatement.setString(12,trackingId);
                        preparedStatement.executeUpdate();
                    }

                    else if(status.equals("authsuccessful"))
                    {
                        updateRecord="update transaction_common set status=?,captureamount=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,transaction_mode=?, successtimestamp = ? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                        //preparedStatement.setString(3,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,eci);
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
                        updateRecord="update transaction_common set status=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,transaction_mode=?, failuretimestamp=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        //preparedStatement.setString(2,ip);
                        preparedStatement.setString(2,machineid);
                        preparedStatement.setString(3,paymentid);
                        preparedStatement.setString(4,remark);
                        preparedStatement.setString(5,eci);
                        preparedStatement.setString(6,rrn);
                        preparedStatement.setString(7,arn);
                        preparedStatement.setString(8,authCode);
                        preparedStatement.setString(9,transaction_mode);
                        preparedStatement.setString(10,functions.getTimestamp());
                        preparedStatement.setString(11,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    transactionLogger.error("updateTransactionAfterResponse preparedStatement--->"+preparedStatement);
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
        return 0;
    }
    public int updateTransactionAfterResponse(String tablename,String status,String amount,String ip,String machineid,String paymentid,String remark,String dateTime,String trackingId,String eci,String rrn,String arn,String authCode) throws PZDBViolationException
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
                /*if(tablename.equals("transaction_qwipi"))
                {
                    updateRecord="update transaction_qwipi set status = ?,captureamount=?,mid=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,dateTime);
                    preparedStatement.setString(7,trackingId);
                    preparedStatement.executeUpdate();
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_ecore set status=?,captureamount=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                       // preparedStatement.setString(3,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,dateTime);
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord="update transaction_ecore set status=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                       // preparedStatement.setString(2,ip);
                        preparedStatement.setString(2,machineid);
                        preparedStatement.setString(3,paymentid);
                        preparedStatement.setString(4,remark);
                        preparedStatement.setString(5,dateTime);
                        preparedStatement.setString(6,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                else */if(tablename.equals("transaction_common"))
            {
                if(status.equals("capturesuccess"))
                {
                    updateRecord="update transaction_common set status=?,captureamount=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,transaction_mode=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,eci);
                    preparedStatement.setString(7,rrn);
                    preparedStatement.setString(8,arn);
                    preparedStatement.setString(9,authCode);
                    preparedStatement.setString(10,trackingId);
                    preparedStatement.executeUpdate();
                }
                else
                {
                    updateRecord="update transaction_common set status=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,transaction_mode=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    //preparedStatement.setString(2,ip);
                    preparedStatement.setString(2,machineid);
                    preparedStatement.setString(3,paymentid);
                    preparedStatement.setString(4,remark);
                    preparedStatement.setString(5,eci);
                    preparedStatement.setString(6,rrn);
                    preparedStatement.setString(7,arn);
                    preparedStatement.setString(8,authCode);
                    preparedStatement.setString(9,trackingId);
                    preparedStatement.executeUpdate();
                }
                transactionLogger.error("updateTransactionAfterResponse preparedStatement--->"+preparedStatement);
            }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
        return 0;
    }

    public String sendTransactionMail(String status,String trackingId,String remark, String emailSent,String billingDescriptor)
    {
        String mailtransactionStatus="Failed";
        if(status.equalsIgnoreCase("capturesuccess") || status.equalsIgnoreCase("authsuccessful"))
        {
            mailtransactionStatus = "Transaction Approved";
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus="Successful ("+remark+")";
            }
            else
            {
                mailtransactionStatus="Successful";
            }
        }
        else if(status.equalsIgnoreCase("authstarted"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = remark;
            }
        }
        else if(status.equalsIgnoreCase("authfailed"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = "Transaction Declined ( "+remark+" )";
            }else{
                mailtransactionStatus = "Transaction Declined";
            }

        }

        SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
        //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION,trackingId,mailtransactionStatus,null);
       /* if("Y".equalsIgnoreCase(emailSent))
        {*/

            //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
           // asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDescriptor);
       /* }*/
        return mailtransactionStatus;
    }

    public String checkValueNumeric(String paymentType,String cardType,String TerminalId) throws PZTechnicalViolationException
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
                {   errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_TYPE);
                    error = error + errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ ErrorMessages.INVALID_PAYMENT_TYPE+"|";
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
                    error = error + errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ ErrorMessages.INVALID_CARD_TYPE+"|";
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
                    error = error + errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ ErrorMessages.INVALID_TERMINALID+"|";
                }
            }
        }
        catch (NumberFormatException e)
        {
            error = "Kindly Pass Numeric value in paymentType , Card Type and Terminal ID fields. ";
            PZExceptionHandler.raiseTechnicalViolationException("SingleCallPaymentDAO.java","checkValueNumeric()",null,"transaction","Technical Exception Thrown:::",PZTechnicalExceptionEnum.NUMBER_FORMAT_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return error;
    }

    public void updateAuthstartedTransactionforCupInPay(CommonValidatorVO commonValidatorVO,String trackingid) throws PZDBViolationException
    {
        Connection connection=null;
        Transaction transaction=new Transaction();
        ActionEntry entry = new ActionEntry();
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

            String qry="insert into bin_details (icicitransid, accountid) values(?,?)";
            PreparedStatement pstmt=connection.prepareStatement(qry);
            pstmt.setString(1,commonValidatorVO.getTrackingid());
            pstmt.setString(2,commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateAuthstartedTransactionforCupInPay()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateAuthstartedTransactionforCupInPay()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }
    }

    public int actionEntryForInpay(String trackingId,String amount,String action,String status,CommResponseVO commResponseVO,String ipaddress)throws PZDBViolationException
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

        int results = 0;
        if (commResponseVO != null)
        {
            transactionid = commResponseVO.getTransactionId();
            transactionstatus = commResponseVO.getStatus();
            responsedescriptor = commResponseVO.getDescriptor();
            responseTime = commResponseVO.getResponseTime();
            transType = commResponseVO.getTransactionType();
        }
        Connection cn = null;
        try
        {
            String sql = "insert into transaction_common_details(trackingid,amount,action,status,remark,responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            results = pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForInpay()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","actionEntryForInpay()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }

    public boolean getPartnerCombinationCheck(String toid,String totype,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        boolean isPartnerAuthentic=false;
        Connection connection=null;
        PreparedStatement pstmt=null;
        String qry="";
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        try
        {
            connection = Database.getConnection();
            if(totype!=null && !totype.equals(""))
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
            if(toid!=null && !toid.equals(""))
            {
                qry="select partnerId,partnerName,logoName from partners where partnerId=(select partnerId from members where memberid=?) and partnerName=?";
                pstmt=connection.prepareStatement(qry);
                pstmt.setString(1,toid);
                pstmt.setString(2,totype);
                rs= pstmt.executeQuery();
                if(rs.next())
                {
                    isPartnerAuthentic=true;
                }
            }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","getPartnerCombinationCheck()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","getPartnerCombinationCheck()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }

        return isPartnerAuthentic;
    }
    public int updateTransactionAfterResponseFor3DRouting(String tablename,String status,String amount,String ip,String machineid,String paymentid,String remark,String dateTime,String trackingId,String eci,String rrn,String arn,String authCode,String terminalId,String fromAccountId,String fromid,String transaction_mode) throws PZDBViolationException
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
                /*if(tablename.equals("transaction_qwipi"))
                {
                    updateRecord="update transaction_qwipi set status = ?,captureamount=?,mid=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,dateTime);
                    preparedStatement.setString(7,trackingId);
                    preparedStatement.executeUpdate();
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_ecore set status=?,captureamount=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                       // preparedStatement.setString(3,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,dateTime);
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord="update transaction_ecore set status=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                       // preparedStatement.setString(2,ip);
                        preparedStatement.setString(2,machineid);
                        preparedStatement.setString(3,paymentid);
                        preparedStatement.setString(4,remark);
                        preparedStatement.setString(5,dateTime);
                        preparedStatement.setString(6,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                else */if(tablename.equals("transaction_common"))
            {
                if(status.equals("capturesuccess"))
                {
                    updateRecord="update transaction_common set status=?,captureamount=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,terminalid=?,accountid=?,fromid=?,transaction_mode=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,eci);
                    preparedStatement.setString(7,rrn);
                    preparedStatement.setString(8,arn);
                    preparedStatement.setString(9,authCode);
                    preparedStatement.setString(10,terminalId);
                    preparedStatement.setString(11,fromAccountId);
                    preparedStatement.setString(12,fromid);
                    preparedStatement.setString(13,transaction_mode);
                    preparedStatement.setString(14,trackingId);
                    preparedStatement.executeUpdate();
                }
                else
                {
                    updateRecord="update transaction_common set status=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,terminalid=?,accountid=?,fromid=?,transaction_mode=?,transaction_mode=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    //preparedStatement.setString(2,ip);
                    preparedStatement.setString(2,machineid);
                    preparedStatement.setString(3,paymentid);
                    preparedStatement.setString(4,remark);
                    preparedStatement.setString(5,eci);
                    preparedStatement.setString(6,rrn);
                    preparedStatement.setString(7,arn);
                    preparedStatement.setString(8,authCode);
                    preparedStatement.setString(9,terminalId);
                    preparedStatement.setString(10,fromAccountId);
                    preparedStatement.setString(11,fromid);
                    preparedStatement.setString(12,fromid);
                    preparedStatement.setString(13,trackingId);
                    preparedStatement.executeUpdate();
                }
                transactionLogger.error("updateTransactionAfterResponse preparedStatement--->"+preparedStatement);
            }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
        return 0;
    }
    public void updateMarketPlaceTransaction(CommonValidatorVO commonValidatorVO,CommResponseVO commResponseVO,CommRequestVO commRequestVO,String respStatus,String trackingId,String tableName,AuditTrailVO auditTrailVO,String transaction_mode) throws PZDBViolationException
    {
        GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();
        ActionEntry entry=new ActionEntry();
        List<MarketPlaceVO> mpDetailsList=commonValidatorVO.getMarketPlaceVOList();
        if(mpDetailsList!=null && mpDetailsList.size()>0)
        {
            MarketPlaceVO marketPlaceVO=null;
            for (int i = 0; i < mpDetailsList.size(); i++)
            {
                marketPlaceVO=mpDetailsList.get(i);
                String trackingid=marketPlaceVO.getTrackingid();
                updateTransactionAfterResponse(tableName, respStatus, marketPlaceVO.getAmount(), genericAddressDetailsVO.getIp(), "", commResponseVO.getTransactionId(), commResponseVO.getRemark(), commResponseVO.getResponseTime(), marketPlaceVO.getTrackingid(), commResponseVO.getEci(), commResponseVO.getRrn(), commResponseVO.getArn(), commResponseVO.getAuthCode(),transaction_mode);
                if(respStatus.equalsIgnoreCase("capturesuccess"))
                    entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                else if(respStatus.equalsIgnoreCase("authsuccessful"))
                    entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                else if ((commResponseVO.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                    entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                else if (respStatus.equalsIgnoreCase("authfailed"))
                    entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            }
        }
    }
    public int updateTransactionAfterResponseFor3DRouting(String tablename,String status,String amount,String ip,String machineid,String paymentid,String remark,String dateTime,String trackingId,String eci,String rrn,String arn,String authCode,String terminalId,String fromAccountId,String fromid) throws PZDBViolationException
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
                /*if(tablename.equals("transaction_qwipi"))
                {
                    updateRecord="update transaction_qwipi set status = ?,captureamount=?,mid=?,qwipiPaymentOrderNumber=?,remark=?,qwipiTransactionDateTime=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,dateTime);
                    preparedStatement.setString(7,trackingId);
                    preparedStatement.executeUpdate();
                }
                else if(tablename.equals("transaction_ecore"))
                {
                    if(status.equals("capturesuccess"))
                    {
                        updateRecord="update transaction_ecore set status=?,captureamount=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                        preparedStatement.setString(2,amount);
                       // preparedStatement.setString(3,ip);
                        preparedStatement.setString(3,machineid);
                        preparedStatement.setString(4,paymentid);
                        preparedStatement.setString(5,remark);
                        preparedStatement.setString(6,dateTime);
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                    }
                    else
                    {
                        updateRecord="update transaction_ecore set status=?,mid=?,ecorePaymentOrderNumber=?,remark=?,ecoreTransactionDateTime=? where trackingid =?";
                        preparedStatement = connection.prepareStatement(updateRecord);
                        preparedStatement.setString(1,status);
                       // preparedStatement.setString(2,ip);
                        preparedStatement.setString(2,machineid);
                        preparedStatement.setString(3,paymentid);
                        preparedStatement.setString(4,remark);
                        preparedStatement.setString(5,dateTime);
                        preparedStatement.setString(6,trackingId);
                        preparedStatement.executeUpdate();
                    }
                }
                else */if(tablename.equals("transaction_common"))
            {
                if(status.equals("capturesuccess"))
                {
                    updateRecord="update transaction_common set status=?,captureamount=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,terminalid=?,accountid=?,fromid=?,transaction_mode=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    preparedStatement.setString(2,amount);
                    //preparedStatement.setString(3,ip);
                    preparedStatement.setString(3,machineid);
                    preparedStatement.setString(4,paymentid);
                    preparedStatement.setString(5,remark);
                    preparedStatement.setString(6,eci);
                    preparedStatement.setString(7,rrn);
                    preparedStatement.setString(8,arn);
                    preparedStatement.setString(9,authCode);
                    preparedStatement.setString(10,terminalId);
                    preparedStatement.setString(11,fromAccountId);
                    preparedStatement.setString(12,fromid);
                    preparedStatement.setString(13,trackingId);
                    preparedStatement.executeUpdate();
                }
                else
                {
                    updateRecord="update transaction_common set status=?,machineid=?,paymentid=?,remark=?,eci=?,rrn=?,arn=?,authorization_code=?,terminalid=?,accountid=?,fromid=?,transaction_mode=?,transaction_mode=? where trackingid =?";
                    preparedStatement = connection.prepareStatement(updateRecord);
                    preparedStatement.setString(1,status);
                    //preparedStatement.setString(2,ip);
                    preparedStatement.setString(2,machineid);
                    preparedStatement.setString(3,paymentid);
                    preparedStatement.setString(4,remark);
                    preparedStatement.setString(5,eci);
                    preparedStatement.setString(6,rrn);
                    preparedStatement.setString(7,arn);
                    preparedStatement.setString(8,authCode);
                    preparedStatement.setString(9,terminalId);
                    preparedStatement.setString(10,fromAccountId);
                    preparedStatement.setString(11,fromid);
                    preparedStatement.setString(12,trackingId);
                    preparedStatement.executeUpdate();
                }
                transactionLogger.error("updateTransactionAfterResponse preparedStatement--->"+preparedStatement);
            }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,status);
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("SingleCallPaymentDAO.java","updateTransactionAfterResponse()",null,"transaction","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
            Database.closePreparedStatement(preparedStatement);
        }
        return 0;
    }
}