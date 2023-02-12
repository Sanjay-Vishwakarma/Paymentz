package com.manager;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.sms.AsynchronousSmsService;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Randomizer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Admin on 5/4/2017.
 */
public final class OTPManager
{
    private Logger logger = new Logger(OTPManager.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(OTPManager.class.getName());
    private Functions functions = new Functions();

    public String insertOtp(CommonValidatorVO commonValidatorVO)
    {

        String email = "";
        String mobileNo = "";
        String memberId = "";
        String partnerId = "";
        String merchantTransactionId = "";

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
            email = commonValidatorVO.getAddressDetailsVO().getEmail();

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()))
            mobileNo = commonValidatorVO.getAddressDetailsVO().getPhone();

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            memberId = commonValidatorVO.getMerchantDetailsVO().getMemberId();

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTransactionID()))
            merchantTransactionId = commonValidatorVO.getMerchantDetailsVO().getTransactionID();

        partnerId = getPartnerId(memberId);

        HashMap map = new HashMap();
        HashMap mailMap = new HashMap();
        String smsOtp = "";
        String emailOtp = "";

        String status = "", LastDate = "";
        long diffHours = 1;
        Connection con = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        Functions functions = new Functions();
        String maxOTPSentCount = ApplicationProperties.getProperty("MAX_OTP_SENT_COUNT");

        int i = 0;
        String id = "";
        String otp_date = "";
        int SentCount = 0;
        String no_exist = "false";
        String email_exist = "false";
        String isPhoneNoVerified = "N";
        String isEmailVerified = "N";

        try
        {
            con = Database.getConnection();

            //To check if mobile no and email exist and is verified.
            String query = "select mobileno, id, email, otp_date, SentCount, isPhoneNoVerified, isEmailVerified from otp_generation where email='" + email + "' or mobileno='" + mobileNo + "' ;";

            transactionLogger.error("query to check if mobile no and email is verified ===== " + query);

            ResultSet rs = Database.executeQuery(query, con);

            while (rs.next())
            {
                if (mobileNo.equals(rs.getString("mobileno")) && "Y".equals(rs.getString("isPhoneNoVerified")))
                {
                    no_exist = "true";
                    isPhoneNoVerified = "Y";
                }

                if (email.equals(rs.getString("email")) && "Y".equals(rs.getString("isEmailVerified")))
                {
                    email_exist = "true";
                    isEmailVerified = "Y";
                }


                if(functions.isValueNull(email) && functions.isValueNull(mobileNo) && isPhoneNoVerified.equals("Y") && isEmailVerified.equals("Y"))
                {
                    commonValidatorVO.setIsMobileNoVerified(isPhoneNoVerified);
                    commonValidatorVO.setIsEmailVerified(isEmailVerified);
                    return status = "success";
                }

                else if(functions.isValueNull(email) && isEmailVerified.equals("Y") && !functions.isValueNull(mobileNo))
                {
                    commonValidatorVO.setIsEmailVerified(isEmailVerified);
                    return status = "success";
                }

                else if(functions.isValueNull(mobileNo) && isPhoneNoVerified.equals("Y") && !functions.isValueNull(email))
                {
                    commonValidatorVO.setIsMobileNoVerified(isPhoneNoVerified);
                    return status = "success";
                }
            }

            transactionLogger.error("inside insertOtp email ===== " + email + " mobileNo ===== " + mobileNo + " memberId ===== " + memberId + " partnerId ===== " + partnerId + " merchantTransactionId ===== " + merchantTransactionId + ", isEmailVerified = " + isEmailVerified + ", isPhoneNoVerified = " + isPhoneNoVerified);

            // query to check if for current merchanttransactionid max limit is hit
            query = "select mobileno, id, email, otp_date, SentCount from otp_generation where ((mobileno='" +  mobileNo + "'" + " and email='" + email + "') or (email='" + email + "' or mobileno='" + mobileNo + "')) and merchanttransactionid = '" + merchantTransactionId + "';";

            transactionLogger.error("query to check if for current merchanttransactionid max limit is hit ===== " + query);

            rs = Database.executeQuery(query, con);

            if (rs.next())
            {
                id = rs.getString("id");
                otp_date = rs.getString("otp_date");
                SentCount = rs.getInt("SentCount");

                transactionLogger.error("id ===== " + id + ", otp_date ===== " + otp_date + ", SentCount ===== " + SentCount);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Date d1 = sdf.parse(otp_date);

                Date d2 = sdf.parse(currentDate);

                long diff = d2.getTime() - d1.getTime();
                diffHours = diff / (60 * 60 * 1000);


                int count = 1;
                if (SentCount >= Integer.parseInt(maxOTPSentCount) && diffHours == 0)
                {
                   return status ="limitexceed";
                }
                else
                {
                    if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                        smsOtp = generateOTP();

                    if(functions.isValueNull(email) && "N".equals(isEmailVerified))
                        emailOtp = generateOTP();

                    if (diffHours == 0)
                    {
                        count = SentCount + 1;
                    }

                    StringBuffer query1 = new StringBuffer();
                    query1.append("UPDATE otp_generation SET SentCount = " + count);

                    if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                        query1.append(" ,smsOtp = " + smsOtp + ", mobileno = '" + mobileNo + "'");

                    if(functions.isValueNull(email) && "N".equals(isEmailVerified))
                        query1.append(" ,emailOTP = " + emailOtp + ", email = '" + email + "'");


                    query1.append(" where  id=" + id);

                    transactionLogger.error("query1 ===== " + query1);

                    if(functions.isValueNull(mobileNo))
                        commonValidatorVO.setIsMobileNoVerified(isPhoneNoVerified);

                    if(functions.isValueNull(email))
                        commonValidatorVO.setIsEmailVerified(isEmailVerified);

                    PreparedStatement preparedStatement = con.prepareStatement(query1.toString());

                    int x = preparedStatement.executeUpdate();
                    if (x == 1)
                    {
                        status = "success";
                        transactionLogger.error(status + "success --------------");

                        //send otp to the customer through SMS
                        String message = "Welcome to Paymentz. Your OTP is: " + smsOtp + ".";

                        if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                        {
                            String phone = mobileNo.replace("+", "").replace("-", "");
                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            map.put("phone", phone);
                            map.put("otp", smsOtp);
                            map.put("partnerid", partnerId);
                            smsService.sendSMS(MailEventEnum.MERCHANT_SIGNUP_OTP, map);
                            transactionLogger.error("phone---->" + phone);
                        }

                        if(functions.isValueNull(email) && "N".equals(isEmailVerified))
                        {
                            mailMap.put(MailPlaceHolder.TOID, partnerId);
                            mailMap.put(MailPlaceHolder.OTP, emailOtp);
                            mailMap.put(MailPlaceHolder.CustomerEmail, email);
                            mailMap.put(MailPlaceHolder.PARTNERID, partnerId);

                            //MailService mailService = new MailService();
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_SIGNUP_OTP, mailMap);
                        }
                    }
                }

                return status;
            }
            else
            {
                if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                    smsOtp = generateOTP();

                if(functions.isValueNull(email) && "N".equals(isEmailVerified))
                    emailOtp = generateOTP();

                String query1 = "INSERT INTO otp_generation (smsOtp, mobileno,email,SentCount,emailOtp, memberid, merchanttransactionid) VALUES(?,?,?,?,?, ?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(query1);
                if(functions.isValueNull(smsOtp))
                    preparedStatement.setInt(1, Integer.parseInt(smsOtp));
                else
                    preparedStatement.setInt(1, 0);

                if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                    preparedStatement.setString(2, mobileNo);
                else
                    preparedStatement.setString(2, null);

                if(functions.isValueNull(email) && "N".equals(isEmailVerified))
                    preparedStatement.setString(3, email);
                else
                    preparedStatement.setString(3, null);

                preparedStatement.setInt(4, 1);

                if(functions.isValueNull(emailOtp))
                    preparedStatement.setInt(5, Integer.parseInt(emailOtp));
                else
                    preparedStatement.setInt(5, 0);

                preparedStatement.setString(6, memberId);
                preparedStatement.setString(7, merchantTransactionId);

                i = preparedStatement.executeUpdate();

                if(functions.isValueNull(mobileNo))
                    commonValidatorVO.setIsMobileNoVerified(isPhoneNoVerified);

                if(functions.isValueNull(email))
                    commonValidatorVO.setIsEmailVerified(isEmailVerified);

                transactionLogger.error("insert query ===== "  + preparedStatement);
                if (i == 1)
                {
                    status = "success";

                    //send otp to the customer through SMS
                    String message = "Welcome to Paymentz. Your OTP is: " + smsOtp + ".";
                    if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                    {
                        String phone = mobileNo.replace("+", "").replace("-", "");
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        map.put("phone", phone);
                        map.put("otp", smsOtp);
                        map.put("partnerid", partnerId);
                        smsService.sendSMS(MailEventEnum.MERCHANT_SIGNUP_OTP, map);
                    }

                    if(functions.isValueNull(email) && "N".equals(isEmailVerified))
                    {
                        mailMap.put(MailPlaceHolder.TOID, partnerId);
                        mailMap.put(MailPlaceHolder.OTP, emailOtp);
                        mailMap.put(MailPlaceHolder.CustomerEmail, email);
                        mailMap.put(MailPlaceHolder.PARTNERID, partnerId);

                        //MailService mailService = new MailService();
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_SIGNUP_OTP, mailMap);
                    }
                }
                else{
                    status = "failed";
                }
            }

        }
        catch (SQLException se)
        {

            transactionLogger.error("SQLException occure", se);
        }
        catch (Exception e)
        {

            transactionLogger.error("Exception ::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    private String getPartnerId(String memberId)
    {
        String partnerId = "";
        Connection con = null;
        try
        {
            String query = "select partnerId FROM members where memberid = " + memberId;
            transactionLogger.error("query for partnerid ====== " + query);

            con = Database.getConnection();
            ResultSet rs = Database.executeQuery(query, con);
            if (rs.next())
            {
                if (functions.isValueNull(rs.getString("partnerId")))
                {
                    partnerId = rs.getString("partnerId");
                }
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("systemError ==== " + systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException ==== " + e);
        }
        finally
        {
            Database.closeConnection(con);
        }


        return partnerId;
    }

    public String isVerifyMerchantOtp(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        String email = "";
        String mobileNo = "";
        String emailOtp = "";
        String smsOtp = "";
        String dbSmsOtp = "";
        String dbEmailOtp = "";
        String merchantTransactionId = "";
        String isPhoneVerified = "N";
        String isEmailVerified = "N";

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
            email = commonValidatorVO.getAddressDetailsVO().getEmail();

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()))
            mobileNo = commonValidatorVO.getAddressDetailsVO().getPhone();

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getSmsOtp()))
            smsOtp = commonValidatorVO.getMerchantDetailsVO().getSmsOtp();

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getEmailOtp()))
            emailOtp = commonValidatorVO.getMerchantDetailsVO().getEmailOtp();

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTransactionID()))
            merchantTransactionId = commonValidatorVO.getMerchantDetailsVO().getTransactionID();

        transactionLogger.error("inside isVerifyMerchantOtp email ===== " + email + " mobileNo ===== " + mobileNo + " emailOtp ===== " + emailOtp + " smsOtp ===== " + smsOtp + " merchantTransactionId = " + merchantTransactionId);

        String status = "";
        Connection con = null;
        String isValidate = "fail";
        PreparedStatement ps=null;
        String maxOTPFailedCount= ApplicationProperties.getProperty("MAX_OTP_FAILED_COUNT");
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        try
        {
            con = Database.getConnection();

            String query = "select mobileno ,id,email, otp_date, failedCount, smsOtp, emailOtp from otp_generation where ((mobileno='" +  mobileNo + "'" + " and email='" + email + "') or (email='" + email + "' or mobileno='" + mobileNo + "')) and merchanttransactionid = '" + merchantTransactionId + "';";

            transactionLogger.debug("otp verify query ---- "+query);
            ResultSet rs = Database.executeQuery(query, con);
            int failedCount=0;
            if (rs.next() )
            {
                if(rs.getString("failedCount").equalsIgnoreCase(maxOTPFailedCount))
                {
                    String error = "Please generate new OTP.";
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MAX_OTP_LIMIT));
                    PZExceptionHandler.raiseConstraintViolationException(OTPManager.class.getName(), "isVerifyMerchantOtp()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }

                dbEmailOtp = String.valueOf(rs.getInt("emailOtp"));
                dbSmsOtp = String.valueOf(rs.getInt("smsOtp"));

                if(!dbEmailOtp.equals("0") && !dbSmsOtp.equals("0"))
                {
                    dbEmailOtp = String.format("%06d", rs.getInt("emailOtp"));
                    dbSmsOtp = String.format("%06d", rs.getInt("smsOtp"));
                    if(smsOtp.equalsIgnoreCase(dbSmsOtp) && emailOtp.equalsIgnoreCase(dbEmailOtp))
                    {
                        isValidate = "success";
                        failedCount = rs.getInt("failedCount");
                        isEmailVerified = "Y";
                        isPhoneVerified = "Y";
                    }
                    else
                    {
                        failedCount =rs.getInt("failedCount")+1;
                    }
                }
                else if(!dbSmsOtp.equals("0"))
                {
                    dbSmsOtp = String.format("%06d", rs.getInt("smsOtp"));
                    if (smsOtp.equalsIgnoreCase(dbSmsOtp))
                    {
                        isValidate = "success";
                        failedCount = rs.getInt("failedCount");
                        isPhoneVerified = "Y";
                    }
                    else
                    {
                        failedCount =rs.getInt("failedCount")+1;
                    }
                }
                else if(!dbEmailOtp.equals("0"))
                {
                    dbEmailOtp = String.format("%06d", rs.getInt("emailOtp"));
                    if (emailOtp.equalsIgnoreCase(dbEmailOtp))
                    {
                        isValidate = "success";
                        failedCount = rs.getInt("failedCount");
                        isEmailVerified = "Y";
                    }
                    else
                    {
                        failedCount =rs.getInt("failedCount")+1;
                    }
                }

                String updateCount="update otp_generation set failedCount=?, isPhoneNoVerified = ?, isEmailVerified = ? where id=?";
                ps=con.prepareStatement(updateCount);
                ps.setInt(1,failedCount);
                ps.setString(2, isPhoneVerified);
                ps.setString(3, isEmailVerified);
                ps.setString(4,rs.getString("id"));
                int i=ps.executeUpdate();
            }


        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException occure", se);
        }

        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError occure", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isValidate;
    }


    public String generateOTP()
    {
        Randomizer r = ESAPI.randomizer();
        int letters = r.getRandomInteger(000001, 999999);
        String otp=String.valueOf(letters);
        if(otp.length()!=6)
        {
            otp=String.format("%06d", letters);
        }
        return otp;
    }

    public void sendSMS(String receiver, String message)
    {
        try
        {
            String SMS_URL = "http://203.212.70.200/smpp/sendsms?username=tc2015&password=tc2015&to="+receiver+"&from=TRANSE&text="+ URLEncoder.encode(message, "utf-8");

            URL url = new URL(SMS_URL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-Type", "text/plain");
            httpConnection.setDoOutput(false);
            int responseCode = httpConnection.getResponseCode();
            /*int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                //TODO Coding
            }*/
        }
        catch(IOException se)
        {
            transactionLogger.error("SQLException occur", se);
        }

    }
}
