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
public class OTPValidationManager
{
    private Logger logger = new Logger(OTPValidationManager.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(OTPValidationManager.class.getName());
    private Functions functions = new Functions();

    public String insertOtp(CommonValidatorVO commonValidatorVO)
    {

        String partnerName = commonValidatorVO.getPartnerDetailsVO().getPartnerName();
        HashMap map = new HashMap();
        HashMap mailMap = new HashMap();
        String otp = generateOTP();
        String status = "", LastDate = "";
        long diffHours = 1;
        Connection con = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String maxOTPFailedCount = ApplicationProperties.getProperty("MAX_OTP_FAILED_COUNT");
        int i = 0;
        String id = "";
        String otp_date = "";
        int SentCount = 0;
        int failedCount = 0;
        String no_exist = "false";
        try
        {
            con = Database.getConnection();
            //To check if mobile no exist.
            String query = "select mobileno ,id, otp_date, SentCount  from signup_otp_varification where  mobileno='" + commonValidatorVO.getAddressDetailsVO().getPhone() + "' order by id desc limit 1";
            ResultSet rs = Database.executeQuery(query.toString(), con);
            if (rs.next())
            {
                if (commonValidatorVO.getAddressDetailsVO().getPhone().equals(rs.getString("mobileno")))
                {
                    no_exist = "true";
                }
                id = rs.getString("id");
                otp_date = rs.getString("otp_date");
                SentCount = rs.getInt("SentCount");
            }
            //To check diffrence  between last otp sent and now.
            if (no_exist.equals("true"))
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Date d1 = sdf.parse(otp_date);

                Date d2 = sdf.parse(currentDate);

                long diff = d2.getTime() - d1.getTime();
                diffHours = diff / (60 * 60 * 1000);

                int count = 1;
                if (SentCount >= 5 && diffHours == 0)
                {
                    status ="limitexceed";


                }
                else
                {

                    if (diffHours == 0)
                    {
                        count = SentCount + 1;
                    }

                    String query1 = "UPDATE signup_otp_varification SET otp=? ,SentCount=? where  id=?";
                    PreparedStatement preparedStatement = con.prepareStatement(query1);
                    preparedStatement.setString(1, otp);
                    preparedStatement.setInt(2, count);
                    preparedStatement.setString(3, id);

                    int x = preparedStatement.executeUpdate();
                    if (x == 1)
                    {
                        status = "success";
                        logger.error(status + "success------------------------");

                        //send otp to the customer through SMS
                        String message = "Welcome to the Invoizer. Your OTP is: " + otp + ".";
                        String phone = commonValidatorVO.getAddressDetailsVO().getPhone().replace("+", "").replace("-", "");
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        map.put("phone", phone);
                        map.put("otp", otp);
                        map.put("partnerid", commonValidatorVO.getParetnerId());
                        logger.error("commonValidatorVO.getParetnerId()---->"+commonValidatorVO.getParetnerId());
                        smsService.sendSMS(MailEventEnum.MERCHANT_SIGNUP_OTP, map);
                        logger.error("phone---->"+phone);

                        mailMap.put(MailPlaceHolder.TOID, commonValidatorVO.getParetnerId());
                        mailMap.put(MailPlaceHolder.OTP, otp);
                        mailMap.put(MailPlaceHolder.CustomerEmail, commonValidatorVO.getAddressDetailsVO().getEmail());
                        mailMap.put(MailPlaceHolder.PARTNERID, commonValidatorVO.getParetnerId());
                        //mailMap.put()

                        //MailService mailService = new MailService();
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_SIGNUP_OTP, mailMap);
                        //sendSMS(commonValidatorVO.getAddressDetailsVO().getPhone(), message);
                    }
                }
            }
            else
            {

                String query1 = "INSERT INTO signup_otp_varification (otp, mobileno,country,email,SentCount) VALUES(?,?,?,?,?)";
                PreparedStatement preparedStatement = con.prepareStatement(query1);
                //  preparedStatement.setString(1, commonValidatorVO.getMerchantDetailsVO().getLogin());
                preparedStatement.setString(1, otp);
                preparedStatement.setString(2, commonValidatorVO.getAddressDetailsVO().getPhone());
                // preparedStatement.setString(3, commonValidatorVO.getAddressDetailsVO().getTelnocc());
                preparedStatement.setString(3, commonValidatorVO.getAddressDetailsVO().getCountry());
                preparedStatement.setString(4, commonValidatorVO.getAddressDetailsVO().getEmail());
                preparedStatement.setInt(5, 1);

                i = preparedStatement.executeUpdate();
                if (i == 1)
                {
                    status = "success";

                    //send otp to the customer through SMS
                    String message = "Welcome to the Invoizer. Your OTP is: " + otp + ".";
                    String phone = commonValidatorVO.getAddressDetailsVO().getPhone().replace("+", "").replace("-", "");
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    map.put("phone", phone);
                    map.put("otp", otp);
                    map.put("partnerid", commonValidatorVO.getParetnerId());
                    smsService.sendSMS(MailEventEnum.MERCHANT_SIGNUP_OTP, map);

                    mailMap.put(MailPlaceHolder.TOID, commonValidatorVO.getParetnerId());
                    mailMap.put(MailPlaceHolder.OTP, otp);
                    mailMap.put(MailPlaceHolder.CustomerEmail, commonValidatorVO.getAddressDetailsVO().getEmail());
                    mailMap.put(MailPlaceHolder.PARTNERID, commonValidatorVO.getParetnerId());
                    //mailMap.put()

                    //MailService mailService = new MailService();
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANT_SIGNUP_OTP, mailMap);
                    //sendSMS(commonValidatorVO.getAddressDetailsVO().getPhone(), message);
                }
                else{
                    status = "failed";
                }
            }

        }
        catch (SQLException se)
        {

            logger.error("SQLException occure", se);
        }
        catch (Exception e)
        {

            logger.error("Exception ::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
    public static void main(String[] args)
    {
        String phone = "+91-9867493501";

        //System.out.println("phone----"+phone.substring(1));
        /*OTPValidationManager otpValidationManager = new OTPValidationManager();
        for(int i=0; i<=1000;i++)
        {
            System.out.println("otp---"+otpValidationManager.generateOTP());
        }*/
    }

    public String isVerifyMerchantOtp(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {

        String status = "";
        Connection con = null;
        String isValidate = "fail";
        PreparedStatement ps=null;
        String maxOTPFailedCount= ApplicationProperties.getProperty("MAX_OTP_FAILED_COUNT");
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        try
        {
            con = Database.getConnection();
          //  String query = "select otp from signup_otp_varification where otp='"+otp+"' AND mobileno='"+otp+"'";
            //String query = "select otp from signup_otp_varification where otp='"+commonValidatorVO.getMerchantDetailsVO().getOtp()+"' AND mobileno='"+commonValidatorVO.getAddressDetailsVO().getPhone()+"'";
            String query = "select id,otp,failedCount from signup_otp_varification where  mobileno='"+commonValidatorVO.getAddressDetailsVO().getPhone()+"' order by id desc limit 1";
            /*PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, otp);*/
            transactionLogger.debug("otp query----"+query);
            ResultSet rs = Database.executeQuery(query.toString(), con);
            int failedCount=0;
            if (rs.next() )
            {
                if(rs.getString("failedCount").equalsIgnoreCase(maxOTPFailedCount))
                {
                    String error = "Please generate new OTP.";
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MAX_OTP_LIMIT));
                    PZExceptionHandler.raiseConstraintViolationException(OTPValidationManager.class.getName(), "isVerifyMerchantOtp()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }
                if(commonValidatorVO.getMerchantDetailsVO().getOtp().equalsIgnoreCase(String.format("%06d", rs.getInt("otp"))))
                {
                    isValidate = "success";
                    failedCount =0;
                }
                else
                {
                    failedCount =rs.getInt("failedCount")+1;
                }
                String updateCount="update signup_otp_varification set failedCount=? where id=?";
                ps=con.prepareStatement(updateCount);
                ps.setInt(1,failedCount);
                ps.setString(2,rs.getString("id"));
                int i=ps.executeUpdate();

            }


        }
        catch (SQLException se)
        {
            logger.error("SQLException occure", se);
        }

        catch (SystemError systemError)
        {
            logger.error("SystemError occure", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isValidate;
    }

    public boolean isValidateMerchantOtp(String login, String otp)
    {

        String status = "";
        Connection con = null;
        boolean isValidate = false;
        try
        {
            con = Database.getConnection();
            String query = "select login from signup_otp_varification where login='"+login+"' AND otp='"+otp+"'";
            /*PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, otp);*/
            transactionLogger.debug("otp query----"+query);
            ResultSet rs = Database.executeQuery(query.toString(), con);

            if (rs.next() )
            {
                isValidate = true;
            }

        }
        catch (SQLException se)
        {
            logger.error("SQLException occure", se);
        }
        catch (Exception e)
        {
            logger.error("Exception ::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isValidate;
    }

    public String generateOTP()
    {
        /*String NUMERIC_STRING = "0123456789";
        StringBuilder builder = new StringBuilder();
        int count = 6;
        while(count-- != 0)
        {
            int character = (int)(Math.random()*NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
        }
        return builder.toString();*/
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
            //String SMS_URL = "https://www.smsglobal.com/http-api.php?action=sendsms&user=" + userName + "&password=" + password + "&to=" + cust_telnocc + "&from=" + fromNumber + "&text="+ URLEncoder.encode(message, "utf-8");
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
            logger.error("SQLException occur", se);
        }

    }


    public String insertOtpForLoginMerchant(CommonValidatorVO commonValidatorVO)
    {
        System.out.println("inside insertOtpForLoginMerchant");
        String email = "";
        String mobileNo = "";
        String memberId = "";
        String merchantTransactionId = "";
        String partnerId = "";

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
            email = commonValidatorVO.getAddressDetailsVO().getEmail();

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()))
            mobileNo = commonValidatorVO.getAddressDetailsVO().getPhone();

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            memberId = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getTransactionID()))
            merchantTransactionId= commonValidatorVO.getMerchantDetailsVO().getTransactionID();

        partnerId = getPartnerId(memberId);

        HashMap map = new HashMap();
        HashMap mailMap = new HashMap();
        String smsOtp = "";
        String emailOtp = "";

        String status = "", LastDate = "";
        long diffHours = 1;
        Connection con = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
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
           // String query = "select mobileno, id, email, otp_date, SentCount from otp_generation where email='" + email + "' or mobileno='" + mobileNo + "' ;";

            System.out.println("inside insertOtp email ===== " + email + " mobileNo ===== " + mobileNo + " memberId ===== " + memberId + " partnerId ===== " + partnerId + " isEmailVerified = " + isEmailVerified + ", isPhoneNoVerified = " + isPhoneNoVerified);

            // query to check if for current merchanttransactionid max limit is hit
            String query  = "select mobileno, id, email, otp_date, SentCount from otp_generation where  merchanttransactionid = '" + merchantTransactionId + "';";

            System.out.println("query to check if for current merchanttransactionid max limit is hit ===== " + query);
            System.out.println("query to check if for current merchanttransactionid max limit is hit===== " + query);

            ResultSet rs = Database.executeQuery(query, con);

            if (rs.next())
            {
                id = rs.getString("id");
                otp_date = rs.getString("otp_date");
                SentCount = rs.getInt("SentCount");

                System.out.println("id ===== " + id + ", otp_date ===== " + otp_date + ", SentCount ===== " + SentCount);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Date d1 = sdf.parse(otp_date);

                Date d2 = sdf.parse(currentDate);

                long diff = d2.getTime() - d1.getTime();
                diffHours = diff / (60 * 60 * 1000);
                int count = 1;
                int faileCount = 0;
                logger.error("diffHours+++"+diffHours);

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

                    query1.append(" ,failedCount = " + faileCount);

                    query1.append(" where  id=" + id);

                    System.out.println("query1 ===== ===== " + query1);
/*
                    if(functions.isValueNull(mobileNo))
                        commonValidatorVO.setIsMobileNoVerified(isPhoneNoVerified);

                    if(functions.isValueNull(email))
                        commonValidatorVO.setIsEmailVerified(isEmailVerified);*/

                    PreparedStatement preparedStatement = con.prepareStatement(query1.toString());

                    int x = preparedStatement.executeUpdate();
                    if (x == 1)
                    {
                        status = "success";
                        System.out.println(status + "success --------------");
                        System.out.println("mobileNo --------------"+mobileNo);
                        System.out.println("isPhoneNoVerified --------------"+isPhoneNoVerified);

                        //send otp to the customer through SMS
                        String message = "Welcome to Paymentz. Your OTP is: " + smsOtp + ".";

                        if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                        {
                            System.out.println("inside mobile number check and sent otp");
                            HashMap map1 = new HashMap();

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            map1.put("phone", mobileNo);
                            map1.put("otp", smsOtp);
                            map1.put("partnerid", partnerId);
                            map1.put("TOID", memberId);
                            System.out.println("mapdata"+map1);
                            smsService.sendSMS(MailEventEnum.MERCHANT_SIGNUP_OTP, map1);
                            System.out.println("phone---->" + mobileNo);
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

              /*  if(functions.isValueNull(mobileNo))
                    commonValidatorVO.setIsMobileNoVerified(isPhoneNoVerified);

                if(functions.isValueNull(email))
                    commonValidatorVO.setIsEmailVerified(isEmailVerified);
*/
                System.out.println("insert query ===== "  + preparedStatement);

                if (i == 1)
                {
                    status = "success";

                    //send otp to the customer through SMS
                    String message = "Welcome to Paymentz. Your OTP is: " + smsOtp + ".";
                    if(functions.isValueNull(mobileNo) && "N".equals(isPhoneNoVerified))
                    {
                        HashMap map1 = new HashMap();
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        map1.put("phone", mobileNo);
                        map1.put("otp", smsOtp);
                        map1.put("partnerid", partnerId);
                        map1.put("TOID", memberId);
                        System.out.println("mapdata"+map1);
                        smsService.sendSMS(MailEventEnum.MERCHANT_SIGNUP_OTP, map1);
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

            //System.out.println("SQLException occure", se);
        }
        catch (Exception e)
        {

            //System.out.println("Exception ::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

    public String isVerifyMerchantLoginOtp(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
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

        System.out.println("inside isVerifyMerchantOtp email ===== " + email + " mobileNo ===== " + mobileNo + " emailOtp ===== " + emailOtp + " smsOtp ===== " + smsOtp + " merchantTransactionId = " + merchantTransactionId);

        String status = "";
        Connection con = null;
        String isValidate = "fail";
        PreparedStatement ps=null;
        String maxOTPFailedCount= ApplicationProperties.getProperty("MAX_OTP_FAILED_COUNT");
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        try
        {
            con = Database.getConnection();

            String query = "select mobileno ,id,email, otp_date, failedCount, smsOtp, emailOtp from otp_generation where merchanttransactionid = '" + merchantTransactionId + "';";

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
                       /* isEmailVerified = "Y";
                        isPhoneVerified = "Y";*/
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
                        /*isPhoneVerified = "Y";*/
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
                        /*isEmailVerified = "Y";*/
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
            //System.out.println("SQLException occure", se);
        }

        catch (SystemError systemError)
        {
           // System.out.println("SystemError occure", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isValidate;
    }
    private String getPartnerId(String memberId)
    {
        String partnerId = "";
        Connection con = null;
        try
        {
            String query = "select partnerId FROM members where memberid = " + memberId;
            System.out.println("query for partnerid ====== " + query);

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
            System.out.println("systemError ==== " + systemError);
        }
        catch (SQLException e)
        {
            System.out.println("SQLException ==== " + e);
        }
        finally
        {
            Database.closeConnection(con);
        }


        return partnerId;
    }
}
