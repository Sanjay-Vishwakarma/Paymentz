package com.directi.pg;


import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Supriya on 7/11/2016.
 */
public class FailedTransactionLogEntry
{
    private static Logger logger=new Logger(FailedTransactionLogEntry.class.getName());

    public String partnerMerchantMismatchInputEntry(String toId,String toType, String requestedIP, String requestedHost, String country, String httpHeader,String remark,String rejectReason,String cardholderIp)throws PZDBViolationException
    {
        String status="";
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getConnection();
            if (!ESAPI.validator().isValidInput("toid", toId, "SafeString", 255, true))
            {
                toId="";
            }
            if (!ESAPI.validator().isValidInput("totype", toType, "SafeString", 255, true))
            {
                toType="";
            }
            if (!ESAPI.validator().isValidInput("country", country, "SafeString", 10, true))
            {
                country="";
            }

            String query = "insert into transaction_fail_log(id,toid,totype,country,httpheader,requestedip,requestedhost,remark,rejectreason,dtstamp,cardholderip)values(null,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,toId);
            pstmt.setString(2,toType);
            pstmt.setString(3,country);
            pstmt.setString(4,httpHeader);
            pstmt.setString(5,requestedIP);
            pstmt.setString(6,requestedHost);
            pstmt.setString(7,remark);
            pstmt.setString(8,rejectReason);
            pstmt.setString(9,cardholderIp);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status="success";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting failed transaction log",systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"partnerMerchantMismatchInputEntry()",null,"Common","System error while Connecting to transaction_fail_log",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while inserting failed transaction log",e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"partnerMerchantMismatchInputEntry()",null,"Common","Sql exception  due to incorrect query to transaction_fail_log",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;

    }

    public String nonWhitelistedTransactionRequestEntry(String toId,String toType, String requestedIP, String requestedHost, String country, String httpHeader,String remark,String rejectReason,String cardholderIp)throws PZDBViolationException
    {
        String status="";
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getConnection();
            if (!ESAPI.validator().isValidInput("toid", toId, "SafeString", 255, true))
            {
                toId="";
            }
            if (!ESAPI.validator().isValidInput("totype", toType, "SafeString", 255, true))
            {
                toType="";
            }
            if (!ESAPI.validator().isValidInput("country", country, "SafeString", 10, true))
            {
                country="";
            }

            String query = "insert into transaction_fail_log(id,toid,totype,country,httpheader,requestedip,requestedhost,remark,rejectreason,dtstamp,cardholderip)values(null,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, toId);
            pstmt.setString(2, toType);
            pstmt.setString(3, country);
            pstmt.setString(4, httpHeader);
            pstmt.setString(5, requestedIP);
            pstmt.setString(6, requestedHost);
            pstmt.setString(7, remark);
            pstmt.setString(8, rejectReason);
            pstmt.setString(9, cardholderIp);

            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status="success";
            }
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            HashMap map = new HashMap();
            map.put(MailPlaceHolder.TOID, toId);
            map.put(MailPlaceHolder.ERROR_NAME, rejectReason);
            map.put(MailPlaceHolder.ERROR_TYPE, ErrorType.SYSCHECK);
            map.put(MailPlaceHolder.MESSAGE, remark);
            asynchronousMailService.sendMerchantSignup(MailEventEnum.REJECTED_TRANSACTION, map);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting failed transaction log",systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"nonWhitelistedTransactionRequestEntry()",null,"Common","System error while Connecting to transaction_fail_log",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while inserting failed transaction log",e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"nonWhitelistedTransactionRequestEntry()",null,"Common","Sql exception  due to incorrect query to transaction_fail_log",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String suspendedMerchantTransactionRequestEntry(String toId,String toType, String requestedIP, String requestedHost, String country, String httpHeader,String remark,String rejectReason,String cardholderIp)throws PZDBViolationException
    {
        String status="";
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        try
        {
            con = Database.getConnection();
            if (!ESAPI.validator().isValidInput("toid", toId, "SafeString", 255, true))
            {
                toId="";
            }
            if (!ESAPI.validator().isValidInput("totype", toType, "SafeString", 255, true))
            {
                toType="";
            }
            if (!ESAPI.validator().isValidInput("country", country, "SafeString", 10, true))
            {
                country="";
            }
            String query = "insert into transaction_fail_log(id,toid,totype,country,httpheader,requestedip,requestedhost,remark,rejectreason,dtstamp,cardholderip)values(null,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1,toId);
            pstmt.setString(2,toType);
            pstmt.setString(3,country);
            pstmt.setString(4,httpHeader);
            pstmt.setString(5,requestedIP);
            pstmt.setString(6,requestedHost);
            pstmt.setString(7,remark);
            pstmt.setString(8,rejectReason);
            pstmt.setString(9,cardholderIp);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status="success";
            }
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            HashMap map = new HashMap();
            map.put(MailPlaceHolder.TOID, toId);
            map.put(MailPlaceHolder.ERROR_NAME, rejectReason);
            map.put(MailPlaceHolder.ERROR_TYPE, ErrorType.SYSCHECK);
            map.put(MailPlaceHolder.MESSAGE, remark);
            asynchronousMailService.sendMerchantSignup(MailEventEnum.REJECTED_TRANSACTION, map);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting failed transaction log",systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"suspendedMerchantTransactionRequestEntry()",null,"Common","System error while Connecting to transaction_fail_log",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while inserting failed transaction log",e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"suspendedMerchantTransactionRequestEntry()",null,"Common","Sql exception  due to incorrect query to transaction_fail_log",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String genericBlockedInputTransactionRequestEntry(String toId,String toType, String requestedIP, String requestedHost, String country, String httpHeader,String firstName,String lastName,String email,String cardNum,String expiryMonth,String expiryYear,String amount,String orderId,String orderDescription,String terminalId,String remark,String rejectReason,String cardholderIp)throws PZDBViolationException
    {
        String status="";
        Connection con=null;
        PreparedStatement pstmt=null;
        Functions functions=new Functions();

        try
        {
            con = Database.getConnection();
            String firstSix="";
            String lastFour="";
            String expiry="";
            if(functions.isValueNull(cardNum))
            {
                firstSix=cardNum.substring(0, 6);
                lastFour=cardNum.substring(cardNum.length()-4,cardNum.length());
            }
            if(functions.isValueNull(expiryMonth)  && functions.isValueNull(expiryYear))
            {
                expiry= PzEncryptor.encryptExpiryDate(expiryMonth + "/" + expiryYear);
            }

            if (!ESAPI.validator().isValidInput("toid", toId, "SafeString", 255, true))
            {
                toId="";
            }
            if (!ESAPI.validator().isValidInput("totype", toType, "SafeString", 255, true))
            {
                toType="";
            }
            if (!ESAPI.validator().isValidInput("country", country, "SafeString", 10, true))
            {
                country="";
            }
            if (!ESAPI.validator().isValidInput("firstname", firstName, "SafeString", 255, true))
            {
                firstName="";
            }
            if (!ESAPI.validator().isValidInput("lastname", lastName, "SafeString", 255, true))
            {
                lastName="";
            }
            if (!ESAPI.validator().isValidInput("email", email, "SafeString", 255, true))
            {
                email="";
            }
            if(!ESAPI.validator().isValidInput("cardnumber",cardNum,"SafeString",255,true))
            {
                cardNum="";
            }
            if(!ESAPI.validator().isValidInput("expirydate",expiry,"SafeString",255,true))
            {
                expiry="";
            }
            if (!ESAPI.validator().isValidInput("terminalid", terminalId, "SafeString", 255, true))
            {
                terminalId="";
            }
            if (!ESAPI.validator().isValidInput("amount", amount, "SafeString", 255, true))
            {
                amount="";
            }
            if (!ESAPI.validator().isValidInput("description", orderId, "SafeString", 255, true))
            {
                orderId="";
            }
            if (!ESAPI.validator().isValidInput("orderdescription", orderDescription, "SafeString", 255, true))
            {
                orderDescription="";
            }
            if(!ESAPI.validator().isValidInput("firstsix", firstSix, "SafeString", 6,true))
            {
                firstSix="";
            }
            if(!ESAPI.validator().isValidInput("lastfour", lastFour, "SafeString", 4,true))
            {
                lastFour="";
            }
            if(functions.isValueNull(cardNum))
            {
                cardNum= PzEncryptor.encryptPAN(cardNum);
            }

            String query = "insert into transaction_fail_log(id,toid,totype,country,httpheader,requestedip,requestedhost,remark,rejectreason,firstname,lastname,email,dtstamp,cardnumber,expirydate,amount,description,orderdescription,terminalid,firstsix,lastfour,cardholderip)values(null,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, toId);
            pstmt.setString(2, toType);
            pstmt.setString(3, country);
            pstmt.setString(4, httpHeader);
            pstmt.setString(5, requestedIP);
            pstmt.setString(6, requestedHost);
            pstmt.setString(7, remark);
            pstmt.setString(8, rejectReason);
            pstmt.setString(9, firstName);
            pstmt.setString(10,lastName);
            pstmt.setString(11,email);
            pstmt.setString(12,cardNum);
            pstmt.setString(13,"");
            pstmt.setString(14,amount);
            pstmt.setString(15,orderId);
            pstmt.setString(16,orderDescription);
            pstmt.setString(17,terminalId);
            pstmt.setString(18,firstSix);
            pstmt.setString(19,lastFour);
            pstmt.setString(20,cardholderIp);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status="success";
            }
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            HashMap map = new HashMap();
            map.put(MailPlaceHolder.TOID, toId);
            map.put(MailPlaceHolder.ERROR_NAME, rejectReason);
            map.put(MailPlaceHolder.ERROR_TYPE, ErrorType.SYSCHECK);
            map.put(MailPlaceHolder.MESSAGE, remark);
            asynchronousMailService.sendMerchantSignup(MailEventEnum.REJECTED_TRANSACTION, map);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting failed transaction log",systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"blockedCustomerNameTransactionRequestEntry()",null,"Common","System error while Connecting to transaction_fail_log",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while inserting failed transaction log",e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"blockedCustomerNameTransactionRequestEntry()",null,"Common","Sql exception  due to incorrect query to transaction_fail_log",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;

    }

    public String genericBlockedInputTransactionRequestEntryForRejected(CommonValidatorVO commonValidatorVO,String remark, String rejectReason)throws PZDBViolationException
    {
        String status="";
        Connection con=null;
        PreparedStatement pstmt=null;
        Functions functions=new Functions();

        String toId = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String toType = commonValidatorVO.getTransDetailsVO().getTotype();
        String requestedIP = commonValidatorVO.getAddressDetailsVO().getIp();
        String requestedHost = commonValidatorVO.getAddressDetailsVO().getRequestedHost();
        String country = commonValidatorVO.getAddressDetailsVO().getCountry();
        String httpHeader = commonValidatorVO.getAddressDetailsVO().getRequestedHeader();
        String firstName = commonValidatorVO.getAddressDetailsVO().getFirstname();
        String lastName = commonValidatorVO.getAddressDetailsVO().getLastname();
        String email = commonValidatorVO.getAddressDetailsVO().getEmail();
        String cardNum = commonValidatorVO.getCardDetailsVO().getCardNum();
        String expiryMonth = commonValidatorVO.getCardDetailsVO().getExpMonth();
        String expiryYear = commonValidatorVO.getCardDetailsVO().getExpYear();
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String orderId = commonValidatorVO.getTransDetailsVO().getOrderId();
        String orderDescription = commonValidatorVO.getTransDetailsVO().getOrderDesc();
        String terminalId = commonValidatorVO.getTerminalId();
        String cardholderIp = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
        String splitPayment = "";
        String cardtypeid = commonValidatorVO.getCardType();
        String paymenttypeid = commonValidatorVO.getPaymentType();
        String currency = commonValidatorVO.getTransDetailsVO().getCurrency();

        if (commonValidatorVO.getSplitPaymentVO() != null)
        {
            if (functions.isValueNull(commonValidatorVO.getSplitPaymentVO().getSplitPaymentDetail()))
            {
                splitPayment = commonValidatorVO.getSplitPaymentVO().getSplitPaymentDetail();
            }
        }
        //System.out.println("remark-----"+remark);
        try
        {
            con = Database.getConnection();
            String firstSix="";
            String lastFour="";
            String expiry="";
            if(functions.isValueNull(cardNum) && cardNum.length()>=6)
            {
                firstSix=cardNum.substring(0, 6);
                lastFour=cardNum.substring(cardNum.length()-4,cardNum.length());
            }
            if(functions.isValueNull(expiryMonth)  && functions.isValueNull(expiryYear))
            {
                expiry= PzEncryptor.encryptExpiryDate(expiryMonth + "/" + expiryYear);
            }

            if (!ESAPI.validator().isValidInput("toid", toId, "SafeString", 255, true))
            {
                toId="";
            }
            if (!ESAPI.validator().isValidInput("totype", toType, "SafeString", 255, true))
            {
                toType="";
            }
            if (!ESAPI.validator().isValidInput("country", country, "SafeString", 10, true))
            {
                country="";
            }
            if (!ESAPI.validator().isValidInput("firstname", firstName, "SafeString", 255, true))
            {
                firstName="";
            }
            if (!ESAPI.validator().isValidInput("lastname", lastName, "SafeString", 255, true))
            {
                lastName="";
            }
            if (!ESAPI.validator().isValidInput("email", email, "SafeString", 255, true))
            {
                email="";
            }
            if(!ESAPI.validator().isValidInput("cardnumber",cardNum,"SafeString",255,true))
            {
                cardNum="";
            }
            if(!ESAPI.validator().isValidInput("expirydate",expiry,"SafeString",255,true))
            {
                expiry="";
            }
            if (!ESAPI.validator().isValidInput("terminalid", terminalId, "SafeString", 255, true))
            {
                terminalId="";
            }
            if (!ESAPI.validator().isValidInput("amount", amount, "SafeString", 255, true))
            {
                amount="";
            }
            if (!ESAPI.validator().isValidInput("description", orderId, "SafeString", 255, true))
            {
                orderId="";
            }
            if (!ESAPI.validator().isValidInput("orderdescription", orderDescription, "SafeString", 255, true))
            {
                orderDescription="";
            }
            if(!ESAPI.validator().isValidInput("firstsix", firstSix, "SafeString", 6,true))
            {
                firstSix="";
            }
            if(!ESAPI.validator().isValidInput("lastfour", lastFour, "SafeString", 4,true))
            {
                lastFour="";
            }
            if(functions.isValueNull(cardNum))
            {
                cardNum= PzEncryptor.encryptPAN(cardNum);
            }
            if(!ESAPI.validator().isValidInput("cardtypeid", cardtypeid, "Numbers", 2,true))
            {
                cardtypeid="0";
            }
            if(!ESAPI.validator().isValidInput("paymenttypeid", paymenttypeid, "Numbers", 2,true))
            {
                paymenttypeid="0";
            }
            if(!ESAPI.validator().isValidInput("currency", currency, "StrictString", 4,true))
            {
                currency="";
            }

            String query = "insert into transaction_fail_log(id,toid,totype,country,httpheader,requestedip,requestedhost,remark,rejectreason,firstname,lastname,email,dtstamp,cardnumber,expirydate,amount,description,orderdescription,terminalid,firstsix,lastfour,cardholderip,splitpayment,cardtypeid,paymenttypeid,currency)values(null,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, toId);
            pstmt.setString(2, toType);
            pstmt.setString(3, country);
            pstmt.setString(4, httpHeader);
            pstmt.setString(5, requestedIP);
            pstmt.setString(6, requestedHost);
            pstmt.setString(7, remark);
            pstmt.setString(8, rejectReason);
            pstmt.setString(9, firstName);
            pstmt.setString(10,lastName);
            pstmt.setString(11,email);
            pstmt.setString(12,cardNum);
            pstmt.setString(13,"");
            pstmt.setString(14,amount);
            pstmt.setString(15,orderId);
            pstmt.setString(16,orderDescription);
            pstmt.setString(17,terminalId);
            pstmt.setString(18,firstSix);
            pstmt.setString(19,lastFour);
            pstmt.setString(20,cardholderIp);
            pstmt.setString(21,splitPayment);
            pstmt.setString(22,cardtypeid);
            pstmt.setString(23,paymenttypeid);
            pstmt.setString(24,currency);
            int k = pstmt.executeUpdate();
            if (k == 1)
            {
                status="success";
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting failed transaction log",systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"blockedCustomerNameTransactionRequestEntryForRejected()",null,"Common","System error while Connecting to transaction_fail_log",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while inserting failed transaction log",e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"blockedCustomerNameTransactionRequestEntryForRejected()",null,"Common","Sql exception  due to incorrect query to transaction_fail_log",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;

    }

    public String genericBlockedInputTransactionRequestEntryForRejected(CommonValidatorVO commonValidatorVO,String remark, String rejectReason, String rejectType)throws PZDBViolationException
    {
        String status="";
        Connection con=null;
        PreparedStatement pstmt=null;
        Functions functions=new Functions();

        String toId = "";
        String toType = "";
        String requestedIP = "";
        String requestedHost = "";
        String country = "";
        String httpHeader = "";
        String firstName = "";
        String lastName = "";
        String email = "";
        String cardnumber = "";
        String expiryMonth = "";
        String expiryYear = "";
        String amount = "";
        String orderId = "";
        String orderDescription = "";
        String terminalid = "";
        String cardholderIp = "";
        String splitPayment = "";
        String cardtypeid = "0";
        String paymenttypeid = "0";
        String currency = "";
        String accountId= "";
        String city="";
        String phone="";
        String street="";
        String zipcode="";
        String state="";
        String isocountrycode="";
        String cvv="";
        String birthdate="";
        String customerId="";
        String requestedReferer="";
        String templateamount="";
        String redirecturl="";
        String notificationUrl="";
        String templatecurrency="";
        String attemptThreeD="";
        String transactionType="";
        String paymentMode="";
        String paymentBrand="";

        if (commonValidatorVO.getSplitPaymentVO() != null)
        {
            if (functions.isValueNull(commonValidatorVO.getSplitPaymentVO().getSplitPaymentDetail()))
            {
                splitPayment = commonValidatorVO.getSplitPaymentVO().getSplitPaymentDetail();
            }
        }
        //System.out.println("remark-----"+remark);
        try
        {
            con = Database.getConnection();
            String firstSix="";
            String lastFour="";
            String expiry="";

            if (commonValidatorVO.getCardDetailsVO() != null)
            {
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) )
                {
                    expiryMonth = commonValidatorVO.getCardDetailsVO().getExpMonth();
                }
                if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
                {
                    expiryYear = commonValidatorVO.getCardDetailsVO().getExpYear();
                    expiry = PzEncryptor.encryptExpiryDate(expiryMonth + "/" + expiryYear);
                }

            }

            if (commonValidatorVO.getMerchantDetailsVO() != null)
            {
                if (ESAPI.validator().isValidInput("toid", commonValidatorVO.getMerchantDetailsVO().getMemberId(), "SafeString", 255, true))
                {
                    toId = commonValidatorVO.getMerchantDetailsVO().getMemberId();
                }
            }

            if (commonValidatorVO.getCardDetailsVO() != null)
            {
                if(ESAPI.validator().isValidInput("cardnumber",commonValidatorVO.getCardDetailsVO().getCardNum(),"OnlyNumber",255,true))
                {
                    cardnumber=commonValidatorVO.getCardDetailsVO().getCardNum();
                    if(functions.isValueNull(cardnumber))
                    {
                        firstSix=cardnumber.substring(0, 6);
                        lastFour=cardnumber.substring(cardnumber.length()-4,cardnumber.length());
                        cardnumber= PzEncryptor.encryptPAN(cardnumber);
                    }
                }
                if(!ESAPI.validator().isValidInput("expirydate",expiry,"SafeString",255,true))
                {
                    expiry="";
                }
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getcVV()))
                {
                    cvv = commonValidatorVO.getCardDetailsVO().getcVV();
                }


            }

            if (commonValidatorVO.getAddressDetailsVO() != null)
            {
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getIp()) && commonValidatorVO.getAddressDetailsVO().getIp().length()<=255)
                {
                    requestedIP = commonValidatorVO.getAddressDetailsVO().getIp();
                }

                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getRequestedHost()))
                {
                    requestedHost = commonValidatorVO.getAddressDetailsVO().getRequestedHost();
                }

                if (ESAPI.validator().isValidInput("country", commonValidatorVO.getAddressDetailsVO().getCountry(), "SafeString", 10, true))
                {
                    country = commonValidatorVO.getAddressDetailsVO().getCountry();
                }
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getRequestedHeader()))
                {
                    httpHeader = commonValidatorVO.getAddressDetailsVO().getRequestedHeader();
                }
                if (ESAPI.validator().isValidInput("firstname", commonValidatorVO.getAddressDetailsVO().getFirstname(), "SafeString", 255, true))
                {
                    firstName=commonValidatorVO.getAddressDetailsVO().getFirstname();
                }
                if (ESAPI.validator().isValidInput("lastname", commonValidatorVO.getAddressDetailsVO().getLastname(), "SafeString", 255, true))
                {
                    lastName=commonValidatorVO.getAddressDetailsVO().getLastname();
                }
                if (ESAPI.validator().isValidInput("email", commonValidatorVO.getAddressDetailsVO().getEmail(), "SafeString", 255, true))
                {
                    email=commonValidatorVO.getAddressDetailsVO().getEmail();
                }

                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()) && commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress().length()<=255)
                {
                    cardholderIp = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                }

                if (ESAPI.validator().isValidInput("city",commonValidatorVO.getAddressDetailsVO().getCity(),"SafeString", 255, true))
                {
                    city = commonValidatorVO.getAddressDetailsVO().getCity();
                }

                if (ESAPI.validator().isValidInput("phone", commonValidatorVO.getAddressDetailsVO().getPhone(),"SafeString", 255, true))
                {
                    phone = commonValidatorVO.getAddressDetailsVO().getPhone();
                }

                if (ESAPI.validator().isValidInput("street", commonValidatorVO.getAddressDetailsVO().getStreet(), "SafeString", 255, true))
                {
                    street = commonValidatorVO.getAddressDetailsVO().getStreet();
                }

                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getZipCode()))
                {
                    zipcode = commonValidatorVO.getAddressDetailsVO().getZipCode();
                }

                if (ESAPI.validator().isValidInput("state", commonValidatorVO.getAddressDetailsVO().getState(), "SafeString", 255, true))
                {
                    state = commonValidatorVO.getAddressDetailsVO().getState();
                }

                if (ESAPI.validator().isValidInput("birthdate", commonValidatorVO.getAddressDetailsVO().getBirthdate(), "SafeString", 255, true))
                {
                    birthdate = commonValidatorVO.getAddressDetailsVO().getBirthdate();
                }
                if (ESAPI.validator().isValidInput("requestedReferer", commonValidatorVO.getAddressDetailsVO().getBirthdate(), "SafeString", 255, true))
                {
                    requestedReferer = commonValidatorVO.getAddressDetailsVO().getRequestedReferer();
                }
                if (ESAPI.validator().isValidInput("templateamount", commonValidatorVO.getAddressDetailsVO().getTmpl_amount(), "SafeString", 255, true))
                {
                    templateamount = commonValidatorVO.getAddressDetailsVO().getTmpl_amount();
                }
                if (ESAPI.validator().isValidInput("templatecurrency", commonValidatorVO.getAddressDetailsVO().getTmpl_currency(), "SafeString", 255, true))
                {
                    templatecurrency = commonValidatorVO.getAddressDetailsVO().getTmpl_currency();
                }

            }

            if (commonValidatorVO.getTransDetailsVO() != null)
            {
                if (ESAPI.validator().isValidInput("totype", commonValidatorVO.getTransDetailsVO().getTotype(), "SafeString", 255, true))
                {
                    toType = commonValidatorVO.getTransDetailsVO().getTotype();
                }
                if (ESAPI.validator().isValidInput("amount", commonValidatorVO.getTransDetailsVO().getAmount(), "SafeString", 255, true))
                {
                    amount=commonValidatorVO.getTransDetailsVO().getAmount();
                }
                if (ESAPI.validator().isValidInput("description", commonValidatorVO.getTransDetailsVO().getOrderId(), "SafeString", 255, true))
                {
                    orderId=commonValidatorVO.getTransDetailsVO().getOrderId();
                }
                if (ESAPI.validator().isValidInput("orderdescription", commonValidatorVO.getTransDetailsVO().getOrderDesc(), "SafeString", 255, true))
                {
                    orderDescription=commonValidatorVO.getTransDetailsVO().getOrderDesc();
                }
                if(ESAPI.validator().isValidInput("currency", commonValidatorVO.getTransDetailsVO().getCurrency(), "StrictString", 4,true))
                {
                    currency=commonValidatorVO.getTransDetailsVO().getCurrency();
                }
                if(ESAPI.validator().isValidInput("redirecturl", commonValidatorVO.getTransDetailsVO().getRedirectUrl(), "SafeString", 255,true))
                {
                    redirecturl=commonValidatorVO.getTransDetailsVO().getRedirectUrl();
                }
                if(ESAPI.validator().isValidInput("notificationUrl", commonValidatorVO.getTransDetailsVO().getNotificationUrl(), "SafeString", 255,true))
                {
                    notificationUrl=commonValidatorVO.getTransDetailsVO().getNotificationUrl();
                }
                /*if(ESAPI.validator().isValidInput("cvv", commonValidatorVO.getTransDetailsVO().getCvv(), "StrictString", 4,true))
                {
                    cvv=commonValidatorVO.getTransDetailsVO().getCvv();
                }*/
                //change
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCvv()))
                {
                    cvv = commonValidatorVO.getTransDetailsVO().getCvv();
                }

                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                {
                    customerId = commonValidatorVO.getCustomerId();
                }
            }


            if (ESAPI.validator().isValidInput("accountId", commonValidatorVO.getAccountId(), "SafeString", 255, true))
            {
                accountId = commonValidatorVO.getAccountId();
            }
            if (ESAPI.validator().isValidInput("terminalid", commonValidatorVO.getTerminalId(), "SafeString", 255, true))
            {
                terminalid=commonValidatorVO.getTerminalId();
            }
            if(!ESAPI.validator().isValidInput("firstsix", firstSix, "SafeString", 6,true))
            {
                firstSix="";
            }
            if(!ESAPI.validator().isValidInput("lastfour", lastFour, "SafeString", 4,true))
            {
                lastFour="";
            }

            if(functions.isValueNull(commonValidatorVO.getPaymentType()) && ESAPI.validator().isValidInput("paymenttypeid", commonValidatorVO.getPaymentType(), "Numbers", 5,true))
            {
                paymenttypeid=commonValidatorVO.getPaymentType();
            }
            if(functions.isValueNull(commonValidatorVO.getCardType()) && ESAPI.validator().isValidInput("cardtypeid", commonValidatorVO.getCardType(), "Numbers", 5,true))
            {
                cardtypeid=commonValidatorVO.getCardType();
            }

            if(functions.isValueNull(commonValidatorVO.getAttemptThreeD()))
            {
                attemptThreeD=commonValidatorVO.getAttemptThreeD();
            }

            if(functions.isValueNull(commonValidatorVO.getTransactionType()))
            {
                transactionType=commonValidatorVO.getTransactionType();
            }
            if(functions.isValueNull(commonValidatorVO.getPaymentMode()))
            {
                paymentMode=commonValidatorVO.getPaymentMode();
            }
            if(functions.isValueNull(commonValidatorVO.getPaymentBrand()))
            {
                paymentBrand=commonValidatorVO.getPaymentBrand();
            }

            if (!functions.isValueNull(rejectType))
            {
                rejectType = "";
            }

            String query = "insert into transaction_fail_log(id,toid,totype,country,httpheader,requestedip,requestedhost,remark,rejectreason,firstname,lastname,email,dtstamp,cardnumber,expirydate,amount,description,orderdescription,terminalid,firstsix,lastfour,cardholderip,splitpayment,cardtypeid,paymenttypeid,currency,rejectType,accountId,city,phone,street,zipcode,state,isocountrycode,birthdate,cvv,customerId,requestedReferer,templateamount,redirecturl,notificationUrl,templatecurrency,attemptThreeD,transactionType,paymentMode,paymentBrand)values(null,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, toId);
            pstmt.setString(2, toType);
            pstmt.setString(3, country);
            pstmt.setString(4, httpHeader);
            pstmt.setString(5, requestedIP);
            pstmt.setString(6, requestedHost);
            pstmt.setString(7, remark);
            pstmt.setString(8, rejectReason);
            pstmt.setString(9, firstName);
            pstmt.setString(10,lastName);
            pstmt.setString(11,email);
            pstmt.setString(12,cardnumber);
            pstmt.setString(13,"");
            pstmt.setString(14,amount);
            pstmt.setString(15,orderId);
            pstmt.setString(16,orderDescription);
            pstmt.setString(17,terminalid);
            pstmt.setString(18,firstSix);
            pstmt.setString(19,lastFour);
            pstmt.setString(20,cardholderIp);
            pstmt.setString(21,splitPayment);
            pstmt.setString(22,cardtypeid);
            pstmt.setString(23,paymenttypeid);
            pstmt.setString(24,currency);
            pstmt.setString(25,rejectType);
            pstmt.setString(26,accountId);
            pstmt.setString(27,city);
            pstmt.setString(28,phone);
            pstmt.setString(29,street);
            pstmt.setString(30,zipcode);
            pstmt.setString(31,state);
            pstmt.setString(32,isocountrycode);
            pstmt.setString(33,birthdate);
            pstmt.setString(34,"");
            pstmt.setString(35,customerId);
            pstmt.setString(36,requestedReferer);
            pstmt.setString(37,templateamount);
            pstmt.setString(38,redirecturl);
            pstmt.setString(39,notificationUrl);
            pstmt.setString(40,templatecurrency);
            pstmt.setString(41,attemptThreeD);
            pstmt.setString(42,transactionType);
            pstmt.setString(43,paymentMode);
            pstmt.setString(44,paymentBrand);
            int k = pstmt.executeUpdate();
            logger.debug("fail trans query----"+pstmt);
            if (k == 1)
            {
                status="success";
            }
            if(ErrorType.SYSCHECK.toString().equalsIgnoreCase(rejectType))
            {
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                HashMap map = new HashMap();
                map.put(MailPlaceHolder.TOID, toId);
                map.put(MailPlaceHolder.ERROR_NAME, rejectReason);
                map.put(MailPlaceHolder.ERROR_TYPE, rejectType);
                map.put(MailPlaceHolder.MESSAGE, remark);
                asynchronousMailService.sendMerchantSignup(MailEventEnum.REJECTED_TRANSACTION, map);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting failed transaction log",systemError);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"blockedCustomerNameTransactionRequestEntryForRejected()",null,"Common","System error while Connecting to transaction_fail_log",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while inserting failed transaction log",e);
            PZExceptionHandler.raiseDBViolationException(FailedTransactionLogEntry.class.getName(),"blockedCustomerNameTransactionRequestEntryForRejected()",null,"Common","Sql exception  due to incorrect query to transaction_fail_log",PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;

    }


}