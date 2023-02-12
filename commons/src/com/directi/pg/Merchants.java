package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.logicboxes.util.Util;
import com.manager.dao.ActivityTrackerDAO;
import com.manager.dao.FraudTransactionDAO;
import com.manager.enums.ActivityLogParameters;
import com.manager.enums.PartnerTemplatePreference;
import com.manager.enums.TemplatePreference;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDefaultConfigVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.PaymentProcessFactory;
import com.payment.acqra.AcqraPaymentGateway;
import com.payment.checkers.PaymentChecker;
import com.payment.emexpay.vo.request;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.pbs.core.PbsPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationCredentialsException;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class Merchants
{
    public static Hashtable hash        = null;
    public static Hashtable partnerHash = null;
    public static Hashtable logoHash    = null;
    public static Hashtable iconHash    = null;
    public static Hashtable defaultTheme        = null;
    public static Hashtable currentTheme        = null;
    public static Hashtable adminPartnerHash    = null;
    public static Hashtable partnerDetailsHash  = null;
    public static Hashtable pciLogoHash     = null;
    public static Hashtable faviconHash     = null;
    public static boolean partnerexist      = false;
    public static String MERCHANT           = "merchant";
    public static String SUBMERCHANT        = "submerchant";
    public static final String ROLE = "partner";
    public static final String SUBPARTNER = "subpartner";
    public static final String CHILDSUBPARTNER = "childsuperpartner";
    public static final String SUPERPARTNER = "superpartner";
    public static String ADMIN        = "admin";
    static Logger logger                        = new Logger(Merchants.class.getName());
    static TransactionLogger transactionLogger  = new TransactionLogger(Merchants.class.getName());
    private Functions functions                 = new Functions();
    public static void refresh() throws SystemError
    {
        Hashtable hash1 = new Hashtable();
        Hashtable hash2 = new Hashtable();
        Hashtable hash3 = new Hashtable();
        Hashtable hash4 = new Hashtable();
        Hashtable hash5 = new Hashtable();
        Hashtable hash6 = new Hashtable();
        Hashtable hash7 = new Hashtable();
        Hashtable hash8 = new Hashtable();
        Hashtable hash9 = new Hashtable();
        Hashtable hash10 = new Hashtable();
        hash1.put("5GBGW52HW9", "Visa/Mastercard");

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("Entering Refresh");
            String query = "select memberid,company_name,reserves,aptprompt,partnerId from members";
            ResultSet result = Database.executeQuery(query,conn);
            while (result.next())
            {
                hash1.put(result.getString("memberid"), result.getString("company_name"));
                hash2.put(result.getString("memberid"), result.getString("partnerId"));
            }
            String query2 = "select partnerId,partnerName,logoName,iconName,default_theme,current_theme,ispcilogo,faviconName from partners";
            ResultSet result2 = Database.executeQuery(query2,conn);
            while (result2.next())
            {
                hash5.put(result2.getString("partnerId"),result2.getString("partnerName"));
                if(result2.getString("logoName")!=null)
                    hash3.put(result2.getString("partnerId"), result2.getString("logoName"));
                else
                    hash3.put(result2.getString("partnerId"), new String(""));

                if(result2.getString("iconName")!=null)
                    hash6.put(result2.getString("partnerId"), result2.getString("iconName"));
                else
                    hash6.put(result2.getString("partnerId"), new String(""));

                if(result2.getString("default_theme")!=null)
                    hash7.put(result2.getString("partnerId"), result2.getString("default_theme"));
                else
                    hash7.put(result2.getString("partnerId"), new String(""));

                if(result2.getString("current_theme")!=null)
                    hash8.put(result2.getString("partnerId"), result2.getString("current_theme"));
                else
                    hash8.put(result2.getString("partnerId"), new String(""));

                if(result2.getString("faviconName")!=null)
                    hash10.put(result2.getString("partnerId"), result2.getString("faviconName"));
                else
                    hash10.put(result2.getString("partnerId"), new String(""));

                if(result2.getString("ispcilogo")!=null)
                    hash9.put(result2.getString("partnerId"), result2.getString("ispcilogo"));
                else
                    hash9.put(result2.getString("partnerId"), new String("Y"));
            }
            String query3 = "select partnerId,partnerName,logoName,iconName,default_theme,current_theme,ispcilogo,faviconName from partners where partnerId='1' ";
            ResultSet result3 = Database.executeQuery(query3,conn);

            while (result3.next())
            {
                hash4.put("partnerid", result3.getString("partnerId"));
                hash4.put("logo", result3.getString("logoName"));
                hash4.put("icon", result3.getString("iconName"));
                hash4.put("fromtype", result3.getString("partnerName"));
                hash4.put("defaulttheme",result3.getString("default_theme"));
                hash4.put("currenttheme",result3.getString("current_theme"));
                hash4.put("ispcilogo",result3.getString("ispcilogo"));
                hash4.put("faviconName",result3.getString("FaviconName"));
            }
            logger.info("merchants loaded");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        //End of inserts.
        hash = hash1;
        partnerHash=hash2;
        logoHash =hash3;
        adminPartnerHash=hash4;
        partnerDetailsHash=hash5;
        iconHash=hash6;
        defaultTheme=hash7;
        currentTheme=hash8;
        pciLogoHash=hash9;
        faviconHash=hash10;
        logger.debug("Leaving refresh");
    }

    public static String generateKey()
    {
        return generateKey(32);
    }

    public static String generateKey(int size)
    {
        String pwdData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = pwdData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuffer key = new StringBuffer();
        int index = -1;

        for (int i = 0; i < size; i++)
        {
            index = rand.nextInt(len);
            key.append(pwdData.substring(index, index + 1));
        }
        return key.toString();
    }
    public String getCompany(String merchantid) throws Exception
    {
        return (String) hash.get(merchantid);
    }
    public String getPartnerLogo(String merchantid) throws Exception
    {
        return (String) logoHash.get(partnerHash.get(merchantid));
    }
    public String getPartnerIcon(String merchantid) throws Exception
    {
        return (String) iconHash.get(partnerHash.get(merchantid));
    }
    public Hashtable getColumns(String[] columnnames, String merchantid) throws SystemError
    {
        Hashtable innerHash = new Hashtable();
        Connection conn = null;
        try
        {
            String commaSeparatedCols = Util.getDelimitedList(columnnames, ",");
            conn = Database.getConnection();
            logger.debug("Entering getColumn");
            String query = "select " + commaSeparatedCols + " from members where memberid =?";
            PreparedStatement p1 = conn.prepareStatement(query);
            p1.setString(1, merchantid);
            ResultSet result = p1.executeQuery();
            ResultSetMetaData rsMetaData1 = result.getMetaData();
            int cols1 = rsMetaData1.getColumnCount();

            while (result.next()) {
                for (int i = 1; i <= cols1; i++)
                {
                    if (result.getString(i) != null)
                    {
                        innerHash.put(rsMetaData1.getColumnLabel(i), result.getString(i));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getColumn");
        return innerHash;
    }
    public String getColumn(String columnname, String merchantid) throws SystemError
    {
        return (String) (getColumns(new String[]{columnname}, merchantid).get(columnname));
    }

    public String getTimeZone(String memberid) throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt= null;
        ResultSet rs = null;
        String timezone = "";
        try
        {
            conn=Database.getRDBConnection();
            String query = "SELECT timezone FROM merchant_configuration where memberid=?";

            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,memberid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                timezone = rs.getString("timezone");
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  timezone;
    }

    public Hashtable getMemberDetails(String merchantid) throws Exception
    {
        Hashtable detailhash = new Hashtable();
        Connection conn = null;
        PreparedStatement pstmt= null;
        ResultSet res = null;
        try
        {
            //conn = Database.getConnection();
            conn=Database.getRDBConnection();
            logger.debug("Entering getMember Details");
            Functions functions = new Functions();
            String query = "SELECT m.*,mc.* FROM members AS m left JOIN merchant_configuration AS mc on m.memberid=mc.memberid where m.memberid=?";

            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,merchantid);
            res = pstmt.executeQuery();
            logger.debug("Old merchant Details is loading ");
            while (res.next())
            {
                detailhash.put("mitigationmail", res.getString("mitigationmail"));
                detailhash.put("hralertproof", res.getString("hralertproof"));
                detailhash.put("datamismatchproof", res.getString("datamismatchproof"));
                detailhash.put("company_name", res.getString("company_name"));
                detailhash.put("contact_persons", res.getString("contact_persons"));
                detailhash.put("contact_emails", res.getString("contact_emails"));
                detailhash.put("address", res.getString("address"));
                detailhash.put("city", res.getString("city"));
                detailhash.put("state", res.getString("state"));
                detailhash.put("zip", res.getString("zip"));
                detailhash.put("country", res.getString("country"));
                detailhash.put("telno", res.getString("telno"));
                detailhash.put("faxno", res.getString("faxno"));
                detailhash.put("brandname", res.getString("brandname"));
                detailhash.put("sitename", res.getString("sitename"));
                detailhash.put("notifyemail", res.getString("notifyemail"));
                detailhash.put("reserves", res.getString("reserves"));
                detailhash.put("aptprompt", res.getString("aptprompt"));
                detailhash.put("taxper", res.getString("taxper"));
                detailhash.put("checksumalgo", res.getString("checksumalgo"));
                detailhash.put("maxScoreAllowed", res.getString("maxScoreAllowed"));
                detailhash.put("maxScoreAutoReversal", res.getString("maxScoreAutoReversal"));
                if (functions.isValueNull(res.getString("timezone")))
                {
                    detailhash.put("timezone", res.getString("timezone"));
                }

                String accountId = res.getString("accountid");
                if (accountId != null)
                {
                    detailhash.put("accountid", accountId);
                }
                detailhash.put("reversalcharge", res.getString("reversalcharge"));
                detailhash.put("withdrawalcharge", res.getString("withdrawalcharge"));
                detailhash.put("chargebackcharge", res.getString("chargebackcharge"));
                detailhash.put("maxscoreallowed", res.getString("maxScoreAllowed"));
                detailhash.put("maxscoreautoreversal", res.getString("maxScoreAutoReversal"));

                if (functions.isValueNull(res.getString("maincontact_phone")))
                {
                    detailhash.put("maincontact_phone", res.getString("maincontact_phone"));
                }
                if (functions.isValueNull(res.getString("maincontact_ccmailid")))
                {
                    detailhash.put("maincontact_ccmailid", res.getString("maincontact_ccmailid"));
                }

                if (functions.isValueNull(res.getString("support_persons")))
                {
                    detailhash.put("support_persons", res.getString("support_persons"));
                }
                if (functions.isValueNull(res.getString("support_emails")))
                {
                    detailhash.put("support_emails", res.getString("support_emails"));
                }
                if (functions.isValueNull(res.getString("support_ccmailid")))
                {
                    detailhash.put("support_ccmailid", res.getString("support_ccmailid"));
                }
                if (functions.isValueNull(res.getString("support_phone")))
                {
                    detailhash.put("support_phone", res.getString("support_phone"));
                }
                if (functions.isValueNull(res.getString("cbcontact_name")))
                {
                    detailhash.put("cbcontact_name", res.getString("cbcontact_name"));
                }
                if (functions.isValueNull(res.getString("cbcontact_mailid")))
                {
                    detailhash.put("cbcontact_mailid", res.getString("cbcontact_mailid"));
                }
                if (functions.isValueNull(res.getString("cbcontact_ccmailid")))
                {
                    detailhash.put("cbcontact_ccmailid", res.getString("cbcontact_ccmailid"));
                }
                if (functions.isValueNull(res.getString("cbcontact_phone")))
                {
                    detailhash.put("cbcontact_phone", res.getString("cbcontact_phone"));
                }
                if (functions.isValueNull(res.getString("refundcontact_name")))
                {
                    detailhash.put("refundcontact_name", res.getString("refundcontact_name"));
                }
                if (functions.isValueNull(res.getString("refundcontact_mailid")))
                {
                    detailhash.put("refundcontact_mailid", res.getString("refundcontact_mailid"));
                }
                if (functions.isValueNull(res.getString("refundcontact_ccmailid")))
                {
                    detailhash.put("refundcontact_ccmailid", res.getString("refundcontact_ccmailid"));
                }
                if (functions.isValueNull(res.getString("refundcontact_phone")))
                {
                    detailhash.put("refundcontact_phone", res.getString("refundcontact_phone"));
                }
                if (functions.isValueNull(res.getString("salescontact_name")))
                {
                    detailhash.put("salescontact_name", res.getString("salescontact_name"));
                }
                if (functions.isValueNull(res.getString("salescontact_mailid")))
                {
                    detailhash.put("salescontact_mailid", res.getString("salescontact_mailid"));
                }
                if (functions.isValueNull(res.getString("salescontact_phone")))
                {
                    detailhash.put("salescontact_phone", res.getString("salescontact_phone"));
                }
                if (functions.isValueNull(res.getString("salescontact_ccmailid")))
                {
                    detailhash.put("salescontact_ccmailid", res.getString("salescontact_ccmailid"));
                }
                if (functions.isValueNull(res.getString("fraudcontact_name")))
                {
                    detailhash.put("fraudcontact_name", res.getString("fraudcontact_name"));
                }
                if (functions.isValueNull(res.getString("fraudcontact_mailid")))
                {
                    detailhash.put("fraudcontact_mailid", res.getString("fraudcontact_mailid"));
                }
                if (functions.isValueNull(res.getString("fraudcontact_ccmailid")))
                {
                    detailhash.put("fraudcontact_ccmailid", res.getString("fraudcontact_ccmailid"));
                }
                if (functions.isValueNull(res.getString("fraudcontact_phone")))
                {
                    detailhash.put("fraudcontact_phone", res.getString("fraudcontact_phone"));
                }
                if (functions.isValueNull(res.getString("technicalcontact_name")))
                {
                    detailhash.put("technicalcontact_name", res.getString("technicalcontact_name"));
                }
                if (functions.isValueNull(res.getString("technicalcontact_mailid")))
                {
                    detailhash.put("technicalcontact_mailid", res.getString("technicalcontact_mailid"));
                }
                if (functions.isValueNull(res.getString("technicalcontact_ccmailid")))
                {
                    detailhash.put("technicalcontact_ccmailid", res.getString("technicalcontact_ccmailid"));
                }
                if (functions.isValueNull(res.getString("technicalcontact_phone")))
                {
                    detailhash.put("technicalcontact_phone", res.getString("technicalcontact_phone"));
                }
                if (functions.isValueNull(res.getString("billingcontact_name")))
                {
                    detailhash.put("billingcontact_name", res.getString("billingcontact_name"));
                }
                if (functions.isValueNull(res.getString("billingcontact_mailid")))
                {
                    detailhash.put("billingcontact_mailid", res.getString("billingcontact_mailid"));
                }
                if (functions.isValueNull(res.getString("billingcontact_ccmailid")))
                {
                    detailhash.put("billingcontact_ccmailid", res.getString("billingcontact_ccmailid"));
                }
                if (functions.isValueNull(res.getString("billingcontact_phone")))
                {
                    detailhash.put("billingcontact_phone", res.getString("billingcontact_phone"));
                }
                if (functions.isValueNull(res.getString("actionExecutorId")))
                {
                    detailhash.put("actionExecutorId", res.getString("actionExecutorId"));
                }
                if (functions.isValueNull(res.getString("actionExecutorName")))
                {
                    detailhash.put("actionExecutorName", res.getString("actionExecutorName"));
                }
                if (functions.isValueNull(res.getString("merchant_verify_otp")))
                {
                    detailhash.put("merchant_verify_otp", res.getString("merchant_verify_otp"));
                }
            }
            logger.debug("merchant Details loaded");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");
        return detailhash;
    }
    public Hashtable getOrganisationDetails(String merchantid) throws Exception
    {
        Hashtable detailhash = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.info("Entering getMemberDetails");

            String query = "select company_type, proprietor, proprietorAddress, proprietorPhNo, OrganisationRegNo, partnerNameAddress, directorsNameAddress, pan, directors, employees, potentialbusiness, registeredaddress, bussinessaddress, notifyemail, acdetails from members where memberid =?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, merchantid);
            ResultSet res = pstmt.executeQuery();
            logger.debug("merchant loading");
            while (res.next())
            {
                if (res.getString("company_type") != null)
                    detailhash.put("company_type", res.getString("company_type"));
                if (res.getString("proprietor") != null)
                    detailhash.put("proprietor", res.getString("proprietor"));
                if (res.getString("proprietorAddress") != null)
                    detailhash.put("proprietorAddress", res.getString("proprietorAddress"));
                if (res.getString("proprietorPhNo") != null)
                    detailhash.put("proprietorPhNo", res.getString("proprietorPhNo"));
                if (res.getString("OrganisationRegNo") != null)
                    detailhash.put("OrganisationRegNo", res.getString("OrganisationRegNo"));
                if (res.getString("partnerNameAddress") != null)
                    detailhash.put("partnerNameAddress", res.getString("partnerNameAddress"));
                if (res.getString("directorsNameAddress") != null)
                    detailhash.put("directorsNameAddress", res.getString("directorsNameAddress"));
                if (res.getString("pan") != null)
                    detailhash.put("pan", res.getString("pan"));
                if (res.getString("directors") != null)
                    detailhash.put("directors", res.getString("directors"));
                if (res.getString("employees") != null)
                    detailhash.put("employees", res.getString("employees"));
                if (res.getString("potentialbusiness") != null)
                    detailhash.put("potentialbusiness", res.getString("potentialbusiness"));
                if (res.getString("registeredaddress") != null)
                    detailhash.put("registeredaddress", res.getString("registeredaddress"));
                if (res.getString("acdetails") != null)
                    detailhash.put("acdetails", res.getString("acdetails"));
                if (res.getString("bussinessaddress") != null)
                    detailhash.put("bussinessaddress", res.getString("bussinessaddress"));
                if (res.getString("notifyemail") != null)
                    detailhash.put("notifyemail", res.getString("notifyemail"));
            }
            logger.info("merchant loaded");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return detailhash;
    }

    public Hashtable getMemberTemplateDetails(String merchantid)
    {
        Hashtable detailhash = new Hashtable();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            // conn = Database.getConnection();
            conn=Database.getRDBConnection();
            logger.info("Entering getMemberDetails");

            String query = "select template,autoredirect,checksumalgo,vbv from members where memberid =? ";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, merchantid);
            res = pstmt.executeQuery();
            logger.debug("merchant loading");
            while (res.next())
            {
                detailhash.put("template", res.getString("template"));
                detailhash.put("autoredirect", res.getString("autoredirect"));
                detailhash.put("checksumalgo", res.getString("checksumalgo"));
                detailhash.put("vbv", res.getString("vbv"));
            }
            logger.debug("merchant loaded");
        }
        catch (SystemError systemError)
        {
            logger.error("System Error:", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLexception : ", e);
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");
        return detailhash;
    }

    public boolean changePassword(String oldpwd, String newpwd, HashMap passMap, User user) throws AuthenticationCredentialsException
    {
        logger.debug("Updating new password in Database");
        int i = 0;
        Connection conn = null;
        String hashNewPassword = null;
        String hashOldPassword = null;
        String role = getUserRole(user);
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        try
        {
            ESAPI.authenticator().changePassword(user, oldpwd, newpwd, newpwd);
        }
        catch (Exception e)
        {
            logger.error("Change password throwing Authetication Exception ", e);
            if (e.getMessage().toLowerCase().contains("mismatch"))
            {
                throw new AuthenticationCredentialsException("Password MISMATCH", "Authentication failed for password change on user: " + user.getAccountName());
            }
            return false;
        }
        if(SUBMERCHANT.equalsIgnoreCase(role))
        {
            // MailService mailService=new MailService();
            HashMap mailValue=new HashMap();
            mailValue.put(MailPlaceHolder.USERID,passMap.get("userid"));
            mailValue.put(MailPlaceHolder.TOID, passMap.get("merchantid"));
            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_USER_CHANGE_PASSWORD, mailValue);
        }
        else
        {
            // MailService mailService = new MailService();
            HashMap mailValue = new HashMap();
            mailValue.put(MailPlaceHolder.TOID, passMap.get("merchantid"));
            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_CHANGE_PASSWORD, mailValue);
            String updQuery = "update members set fpasswd=''  where memberid = ? and accid=? ";
            try
            {
                conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(updQuery);
                pstmt.setString(1, (String)passMap.get("merchantid"));
                pstmt.setLong(2, user.getAccountId());
                i = pstmt.executeUpdate();
                logger.debug("update records from change password");
            }
            catch (SystemError se)
            {
                logger.error("System error occure::::", se);
            }
            catch (Exception e)
            {
                logger.error("Getting Excrption in changepassword method ::::", e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }
        return true;
    }
    public boolean updateProfile(Hashtable details, String merchantid) throws SystemError
    {
        logger.debug("Update and set New record in merchant account");
        StringBuffer updQuery = new StringBuffer("update members set ");
        updQuery.append("hralertproof = ? ,");
        updQuery.append("datamismatchproof = ? ,");
        updQuery.append("mitigationmail = ? ,");
        updQuery.append("company_name = ? ,");
        updQuery.append("contact_persons = ? ,");
        updQuery.append("contact_emails = ? ,");
        updQuery.append("address = ? ,");
        updQuery.append("city = ? ,");
        updQuery.append("state = ? ,");
        updQuery.append("zip = ? ,");
        updQuery.append("country = ? ,");
        updQuery.append("telno = ? ,");
        updQuery.append("faxno = ? ,");
        updQuery.append("notifyemail = ? ,");
        updQuery.append("brandname = ? ,");
        updQuery.append("sitename = ? ,");
        updQuery.append("maxScoreAllowed= ? ,");
        updQuery.append("maxScoreAutoReversal= ? ");
        updQuery.append("where memberid = ? ");

        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            pstmt = conn.prepareStatement(updQuery.toString());
            pstmt.setString(1, (String) details.get("hralertproof"));
            pstmt.setString(2, (String) details.get("datamismatchproof"));
            pstmt.setString(3, (String) details.get("mitigationmail"));
            pstmt.setString(4, (String) details.get("company_name"));
            pstmt.setString(5, (String) details.get("contact_persons"));
            pstmt.setString(6, (String) details.get("contact_emails"));
            pstmt.setString(7, (String) details.get("address"));
            pstmt.setString(8, (String) details.get("city"));
            pstmt.setString(9, (String) details.get("state"));
            pstmt.setString(10, (String) details.get("zip"));
            pstmt.setString(11, (String) details.get("country"));
            pstmt.setString(12, (String) details.get("telno"));
            pstmt.setString(13, (String) details.get("faxno"));
            pstmt.setString(14, (String) details.get("notifyemail"));
            pstmt.setString(15, (String) details.get("brandname"));
            pstmt.setString(16, (String) details.get("sitename"));
            pstmt.setString(17, (String) details.get("maxscoreallowed"));
            pstmt.setString(18, (String) details.get("maxscoreautoreversal"));
            pstmt.setString(19, merchantid);
            logger.debug("pstmt merchent profile:::" + pstmt);
            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                String query = "select memberid from merchant_configuration where memberid=?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, merchantid);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next())
                {
                    String query1 = "insert into merchant_configuration(id,memberid)values(NULL,?)";
                    pstmt = conn.prepareStatement(query1);
                    pstmt.setString(1, merchantid);
                    int k2 = pstmt.executeUpdate();
                }

                String timezone = (String) details.get("timezone");
                if(functions.isValueNull(timezone))
                {
                    timezone = timezone.substring(0, timezone.indexOf("|"));
                }

                String query2 = "update merchant_configuration set maincontact_ccmailid=?,maincontact_phone=?,cbcontact_name=?,cbcontact_mailid=?,cbcontact_ccmailid=?,cbcontact_phone=?,refundcontact_name=?,refundcontact_mailid=?,refundcontact_ccmailid=?,refundcontact_phone=?,salescontact_name=?," +
                        "salescontact_mailid=?,salescontact_ccmailid=?,salescontact_phone=?,fraudcontact_name=?,fraudcontact_mailid=?,fraudcontact_ccmailid=?,fraudcontact_phone=?,technicalcontact_name=?,technicalcontact_mailid=?,technicalcontact_ccmailid=?,technicalcontact_phone=?," +
                        "billingcontact_name=?,billingcontact_mailid=?,billingcontact_ccmailid=?,billingcontact_phone=?,support_persons=?,support_emails=?,support_ccmailid=?,support_phone=?,timezone=? where memberid=?";

                pstmt = conn.prepareStatement(query2);
                pstmt.setString(1, (String) details.get("maincontact_ccmailid"));
                pstmt.setString(2, (String) details.get("maincontact_phone"));
                pstmt.setString(3, (String) details.get("cbcontact_name"));
                pstmt.setString(4, (String) details.get("cbcontact_mailid"));
                pstmt.setString(5, (String) details.get("cbcontact_ccmailid"));
                pstmt.setString(6, (String) details.get("cbcontact_phone"));
                pstmt.setString(7, (String) details.get("refundcontact_name"));
                pstmt.setString(8, (String) details.get("refundcontact_mailid"));
                pstmt.setString(9, (String) details.get("refundcontact_ccmailid"));
                pstmt.setString(10, (String) details.get("refundcontact_phone"));
                pstmt.setString(11, (String) details.get("salescontact_name"));
                pstmt.setString(12, (String) details.get("salescontact_mailid"));
                pstmt.setString(13, (String) details.get("salescontact_ccmailid"));
                pstmt.setString(14, (String) details.get("salescontact_phone"));
                pstmt.setString(15, (String) details.get("fraudcontact_name"));
                pstmt.setString(16, (String) details.get("fraudcontact_mailid"));
                pstmt.setString(17, (String) details.get("fraudcontact_ccmailid"));
                pstmt.setString(18, (String) details.get("fraudcontact_phone"));
                pstmt.setString(19, (String) details.get("technicalcontact_name"));
                pstmt.setString(20, (String) details.get("technicalcontact_mailid"));
                pstmt.setString(21, (String) details.get("technicalcontact_ccmailid"));
                pstmt.setString(22, (String) details.get("technicalcontact_phone"));
                pstmt.setString(23, (String) details.get("billingcontact_name"));
                pstmt.setString(24, (String) details.get("billingcontact_mailid"));
                pstmt.setString(25, (String) details.get("billingcontact_ccmailid"));
                pstmt.setString(26, (String) details.get("billingcontact_phone"));
                pstmt.setString(27, (String) details.get("support_persons"));
                pstmt.setString(28, (String) details.get("support_emails"));
                pstmt.setString(29, (String) details.get("support_ccmailid"));
                pstmt.setString(30, (String) details.get("support_phone"));
                pstmt.setString(31, (String) details.get("timezone"));
                pstmt.setString(32, merchantid);
                int k2 = pstmt.executeUpdate();
                refresh();
                FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
            }
        }
        catch (SystemError se)
        {
            logger.error("System error occure", se);
        }
        catch (Exception e)
        {
            logger.error("Exception occure from update profile", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving From Update Profile method");
        return true;
    }
    public boolean updateTemplate(Hashtable details, String merchantid) throws SystemError
    {
        int i = 0;
        StringBuffer updQuery = new StringBuffer("update members set ");
        updQuery.append("template = ? ,");
        updQuery.append("autoredirect = ? ,");
        updQuery.append("checksumalgo = ? ");
        updQuery.append("where memberid = ?");

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(updQuery.toString());
            pstmt.setString(1, (String) details.get("template"));
            pstmt.setString(2, (String) details.get("autoredirect"));
            pstmt.setString(3, (String) details.get("checksumalgo"));
            pstmt.setString(4, merchantid);
            i = pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            logger.debug(Functions.ShowMessage("Error", se.toString()));
        }
        catch (Exception e)
        {
            logger.debug(Functions.ShowMessage("Error!", e.toString()));
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return true;
    }
    public boolean changeTransPassword(String oldpwd, String newpwd, String memberid)
    {
        logger.debug("Updating member password in Database");
        Connection conn = null;
        String selectQuery = "select transpasswd from members where memberid = ?";
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setString(1, memberid);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (!Functions.decryptString(rs.getString("transpasswd")).equals(oldpwd))
                {
                    return false;
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("System error", se);
            return false;
        }
        catch (Exception e)
        {
            logger.error("Exception ::::", e);
            return false;
        }
        finally
        {
            Database.closeConnection(conn);
        }
        String updQuery = "update members set transpasswd = ? where memberid = ? ";
        int i = 0;
        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(updQuery);
            pstmt.setString(1, Functions.encryptString(newpwd));
            pstmt.setString(2, memberid);
            i = pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            logger.error("System error", se);
            return false;
        }
        catch (Exception e)
        {
            logger.error("Exception ::::", e);
            return false;
        }
        finally
        {
            Database.closeConnection(conn);
        }
        if (i == 1)
            return true;
        else
            return false;
    }
    public boolean isTransPassword(String transpwd, String memberid) throws SystemError
    {
        boolean flag = false;
        String tpresult = null;
        String query = "select transpasswd from members where memberid = ? ";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, memberid);
            ResultSet result = pstmt.executeQuery();
            while (result.next())
            {
                tpresult = result.getString("transpasswd");
            }
            if (Functions.decryptString(tpresult).equals(transpwd))
            {
                flag = true;
            }
        }
        catch (SQLException e)
        {
            logger.debug("Leaving Merchants throwing SQL Exception as System Error : " + e.getMessage());
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return flag;
    }
    public String getMemberLoginfromUser(String login)
    {
        String userLogin =null;
        Connection conn = null;
        try
        {
            conn                = Database.getConnection();
            String userQuery    = "SELECT m.login,mu.userid FROM members AS m , member_users AS mu WHERE m.memberid=mu.memberid AND mu.login = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(userQuery);
            pstmt1.setString(1, login);
            ResultSet rs = pstmt1.executeQuery();
            if (rs.next())
            {
                userLogin = rs.getString("login");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError---", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return userLogin;
    }
    public Member authenticate(String login, String partner, HttpServletRequest request) throws SystemError
    {
        String fpasswd  = null;
        String passwd   = null;
        PaymentChecker paymentChecker   = new PaymentChecker();
        String ipAddress                = Functions.getIpAddress(request);
        Connection conn                 = null;

        Member mem          = new Member();
        String userLogin    = "";
        int userId          = 0;
        String etoken       = "";

        logger.debug("check username and password in database" + partner);
        try
        {
            conn                = Database.getConnection();
            String userQuery    = "SELECT m.login,mu.userid FROM members AS m , member_users AS mu WHERE m.memberid=mu.memberid AND mu.login = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(userQuery);
            pstmt1.setString(1, login);
            ResultSet rs = pstmt1.executeQuery();
            if (rs.next())
            {
                userLogin = rs.getString("login");
                userId = rs.getInt("userid");
                if (userLogin != null && !userLogin.equals(""))
                {
                    login = userLogin;
                }
                logger.error("user id from member_user---" + userId + "--" + userLogin + "--" + login);
                MemberUser memberUser = new MemberUser();
                memberUser.setUserId(userId);
                mem.setMemberUser(memberUser);
            }
            String query        = "SELECT m.memberid,m.login,m.contact_persons,m.fpasswd,m.clkey, m.activation,m.isservice,m.address,m.city,m.zip,m.country,m.contact_emails,m.telno,m.accountid,m.partnerId,m.icici,m.isappmanageractivate,m.iscardregistrationallowed,m.is_recurring,m.isspeedoptionactivate,m.is_rest_whitelisted,m.country,p.default_theme,p.current_theme,m.emailtoken,m.isemailverified,m.multiCurrencySupport, m.isMobileVerified,mc.upi_support_invoice,mc.upi_qr_support_invoice,mc.paybylink_support_invoice,mc.AEPS_support_invoice FROM members as m join partners p on m.partnerId=p.partnerid , merchant_configuration as mc WHERE m.memberid=mc.memberid AND m.login=? AND m.partnerId=?";
            mem.authenticate    = "false";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, login);
            pstmt.setString(2, partner);
            logger.error("member table query authenticate---" + pstmt);
            ResultSet result = pstmt.executeQuery();
            if (result.next())
            {
                fpasswd         = result.getString("fpasswd");
                mem.memberid    = result.getInt("memberid");
                mem.activation  = result.getString("activation");
                mem.partnerid   = result.getString("partnerId");
                mem.secureKey   = result.getString("clkey");
                mem.contactemails = result.getString("contact_emails");
                mem.telno           = result.getString("telno");
                mem.login           = result.getString("login");
                mem.contactpersons  = result.getString("contact_persons");
                mem.country         = result.getString("country");
                mem.currentTheme    = result.getString("current_theme");
                mem.defaultTheme    = result.getString("default_theme");
                mem.etoken          = result.getString("emailtoken");
                mem.isemailverified = result.getString("isemailverified");
                mem.isMobileVerified        = result.getString("isMobileVerified");
                mem.multiCurrencySupport    = result.getString("multiCurrencySupport");
                mem.upiSupportInvoice    = result.getString("upi_support_invoice");
                mem.upiQRSupportIinvoice    = result.getString("upi_qr_support_invoice");
                mem.paybylinkSpportInvoice    = result.getString("paybylink_support_invoice");
                mem.AEPSSupportInvoice    = result.getString("AEPS_support_invoice");
                if ((result.getString("isservice")).equalsIgnoreCase("Y"))
                {
                    mem.isservice = true;
                }
                if (!"".equals(fpasswd))
                {
                    mem.authenticate = "forgot";
                }
                mem.isMerchantInterfaceAccess = result.getBoolean("icici");
                mem.address         = result.getString("address") + ", " + result.getString("city") + " - " + result.getString("zip") + "  " + result.getString("country");
                mem.telno           = result.getString("telno");
                mem.contactemails   = result.getString("contact_emails");
                mem.setAccountId(result.getString("accountid"));
                mem.isApplicationManagerAccess  = result.getString("isappmanageractivate");
                mem.isSpeedOptionAccess         = result.getString("isspeedoptionactivate");
                mem.isCardRegistrationAllowed   = result.getString("iscardregistrationallowed");
                mem.is_rest_whitelisted         = result.getString("is_rest_whitelisted");
                mem.isRecurringModuleAllowed    = result.getString("is_recurring");
                if (!mem.isMerchantInterfaceAccess)
                {
                    throw new SystemError("Error : Merchant Access Denied");
                }
                if (!paymentChecker.isIpWhitelistedForMember(result.getString("memberid"), ipAddress))
                {
                    throw new SystemError("Error : Ip is NOT WHITELISTED");
                }
            }
            else
            {
                logger.debug(" there is no data found of the member in authenticate");
                throw new SystemError("Error: UNAUTHORIZED USER");
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
    public boolean forgotTransPassword(String login) throws SystemError
    {
        boolean flag = false;
        logger.debug("Entering forgotTransPassword()");
        Connection conn = null;
        Transaction transaction = new Transaction();
        try
        {
            conn = Database.getConnection();
            String query = "select contact_emails,transpasswd,memberid from members where login= ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, login);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                String emailAddr = rs.getString("contact_emails");
                String transpasswd = rs.getString("transpasswd");
                String memberid = rs.getString("memberid");
            }
        }
        catch (SQLException e)
        {
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return flag;
    }
    public boolean forgotPassword(String login, User user) throws SystemError
    {
        boolean flag = false;
        String role = MERCHANT;
        if ((isMember(login) || isMemberUser(login))&& user!=null)
        {
            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            logger.error("Username::::" + login + "---Temporary Pasword :::::::::" + fpasswd);
            System.out.println("Username::::" + login + "---Temporary Pasword :::::::::" + fpasswd);
            String hashPassword = null;
            try
            {
                hashPassword = ESAPI.authenticator().hashPassword(fpasswd,login) ;
            }
            catch(Exception e)
            {
                throw new SystemError("Error getting hashpassword : " + e.getMessage());
            }
            try
            {
                ESAPI.authenticator().changePassword(user, null, fpasswd, fpasswd);
            }
            catch(Exception e)
            {
                logger.error("Change password throwing Authetication Exception ",e);
                return false;
            }
            role = getUserRole(user);
            logger.debug("user table updated with temporary password");
            Connection conn = null;
            try
            {
                conn = Database.getConnection();

                String tableName = "members";

                if(SUBMERCHANT.equalsIgnoreCase(role))
                {
                    tableName = "member_users";
                }
                String query = "update "+tableName+" set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and accid=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,hashPassword);
                pstmt.setString(2,login);
                pstmt.setLong(3, user.getAccountId());
                int success = pstmt.executeUpdate();
                if (success > 0)
                {
                    ResultSet rs = null;
                    logger.debug("new temporary password has been set");
                    String query1 ="";
                    if(SUBMERCHANT.equalsIgnoreCase(role))
                    {
                        query1 = "select contact_emails,memberid,userid from member_users where login= ? and accid=?";
                    }
                    else
                    {
                        query1 = "select contact_emails,memberid from members where login= ? and accid=?";
                    }
                    pstmt= conn.prepareStatement(query1);
                    pstmt.setString(1,login);
                    pstmt.setLong(2,user.getAccountId());
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String memberid=rs.getString("memberid");
                        String userid = "";
                        if(SUBMERCHANT.equalsIgnoreCase(role))
                        {
                            userid = rs.getString("userid");
                        }
                        //MailService mailService=new MailService();
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        HashMap mailValues=new HashMap();

                        if(SUBMERCHANT.equalsIgnoreCase(role))
                        {
                            mailValues.put(MailPlaceHolder.TOID, memberid);
                            mailValues.put(MailPlaceHolder.USERID, userid);
                            mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_USER_FORGOT_PASSWORD, mailValues);
                        }
                        else
                        {
                            mailValues.put(MailPlaceHolder.TOID, memberid);
                            mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_FORGOT_PASSWORD, mailValues);
                        }
                        flag=true;
                    }
                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating member table.");
                }
                logger.debug("query has been committed for forgotpassword  ");
            }//end try
            catch (SQLException e)
            {
                logger.error("Exception while forgot password",e);
                throw new SystemError("Error : " + e.getMessage());
            }
            finally
            {
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        return flag;
    }


    public boolean forgotPasswordNew(String login, User user,String remoteAddr,String Header,String actionExecuterId,String partnerid) throws SystemError
    {
        boolean flag = false;
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        System.out.println("usermerchant1---------------------"+user);

        String role = MERCHANT;
        if ((isMember(login) || isMemberUser(login))&& user!=null)
        {
            System.out.println("usermerchant2---------------------"+user);

            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            logger.error("Username::::" + login + "---Temporary Pasword :::::::::" + fpasswd);
            System.out.println("Username::::" + login + "---Temporary Pasword :::::::::" + fpasswd);
            String hashPassword = null;
            try
            {
                hashPassword = ESAPI.authenticator().hashPassword(fpasswd,login) ;
            }
            catch(Exception e)
            {
                throw new SystemError("Error getting hashpassword : " + e.getMessage());
            }
            try
            {
                ESAPI.authenticator().changePassword(user, null, fpasswd, fpasswd);
            }
            catch(Exception e)
            {
                logger.error("Change password throwing Authetication Exception ",e);
                return false;
            }
            role = getUserRole(user);
            System.out.println("role---merchant-------"+role);
            logger.debug("user table updated with temporary password");
            Connection conn = null;
            try
            {
                conn = Database.getConnection();

                String tableName = "members";

                if(SUBMERCHANT.equalsIgnoreCase(role))
                {
                    tableName = "member_users";
                }
                String query = "update "+tableName+" set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and accid=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,hashPassword);
                pstmt.setString(2,login);
                pstmt.setLong(3, user.getAccountId());
                logger.error("QUERY::: "+pstmt);
                int success = pstmt.executeUpdate();
                if (success > 0)
                {
                    ResultSet rs = null;
                    logger.debug("new temporary password has been set");
                    String query1 ="";
                    if(SUBMERCHANT.equalsIgnoreCase(role))
                    {
                        query1 = "select contact_emails,memberid,userid from member_users where login= ? and accid=?";
                    }
                    else
                    {
                        query1 = "select contact_emails,memberid from members where login= ? and accid=?";
                    }
                    pstmt= conn.prepareStatement(query1);
                    pstmt.setString(1,login);
                    pstmt.setLong(2,user.getAccountId());
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String memberid=rs.getString("memberid");
                        String userid = "";
                        if(SUBMERCHANT.equalsIgnoreCase(role))
                        {
                            userid = rs.getString("userid");
                        }
                        //MailService mailService=new MailService();
                        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                        HashMap mailValues=new HashMap();

                        if(SUBMERCHANT.equalsIgnoreCase(role))
                        {
                            mailValues.put(MailPlaceHolder.TOID, memberid);
                            mailValues.put(MailPlaceHolder.USERID, userid);
                            mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_USER_FORGOT_PASSWORD, mailValues);
                        }
                        else
                        {
                            mailValues.put(MailPlaceHolder.TOID, memberid);
                            mailValues.put(MailPlaceHolder.PASSWORD, fpasswd);
                            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_FORGOT_PASSWORD, mailValues);
                        }
                        flag=true;
                        String Value= "Temporary Password=" + fpasswd;
                        System.out.println("Value"+Value);
                        activityTrackerVOs.setInterface(ActivityLogParameters.MERCHANT.toString());
                        activityTrackerVOs.setUser_name(login + "-" + memberid);
                        if(functions.isValueNull(role) && role.equalsIgnoreCase(SUBMERCHANT))
                        {
                            activityTrackerVOs.setRole(String.valueOf(ActivityLogParameters.SUBMERCHANT));
                        }
                        else
                        {
                            activityTrackerVOs.setRole(String.valueOf(ActivityLogParameters.MERCHANT));
                        }
                        activityTrackerVOs.setAction(ActivityLogParameters.FGTPASS.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.FGTPASS.toString());
                        activityTrackerVOs.setLable_values(Value);
                        activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberid);
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(Header);
                        activityTrackerVOs.setPartnerId(partnerid);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception e)
                        {
                            logger.error("Exception while AsyncActivityLog::::", e);
                        }
                    }
                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating member table.");
                }
                logger.debug("query has been committed for forgotpassword  ");
            }//end try
            catch (SQLException e)
            {
                logger.error("Exception while forgot password",e);
                throw new SystemError("Error : " + e.getMessage());
            }
            finally
            {
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        return flag;
    }
    public String getUserRole(User user)
    {
        System.out.println("userrole");
        String userRole = MERCHANT;
        for(String role : user.getRoles())
        {
            if(role.equals(SUBMERCHANT))
            {
                userRole = SUBMERCHANT;
            }
        }
        return userRole;
    }
    public String getUserRolepartner(User user)
    {
        System.out.println("getUserRole-----");
        String userRole = ROLE;
        for (String role : user.getRoles())
        {
            logger.debug("role in method---" + role);
            if (role.equals(SUBPARTNER))
            {
                userRole = SUBPARTNER;
            }
            else if (role.equals(CHILDSUBPARTNER))
            {
                userRole = CHILDSUBPARTNER;
            }
            else if (role.equals(SUPERPARTNER))
            {
                userRole = SUPERPARTNER;
            }
        }

        return userRole;
    }
    public boolean isLoggedIn(HttpSession sess)
    {
        if (sess.getAttribute("merchantid") == null || sess.getAttribute("password") == null || sess.getAttribute("username") == null)
            return false;
        else
            return true;
    }
    public boolean isMember(String login)
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("check isMember method");
            String selquery = "select memberid from members where login = ? ";

            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            System.out.println("isMember------------------------ - "+pstmt);
                    ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }
    public boolean isMemberUser(String login) throws SystemError
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            logger.debug("check isMember method");
            String selquery = "select userid from member_users where login = ? ";
            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            System.out.println("isMemberUser-------------------"+pstmt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ",se);
            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ",e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }
    public String isMemberUser(String login,String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        String userId="";
        try
        {
            conn = Database.getConnection();
            logger.debug("check isMember method");
            String selquery = "select userid from member_users where login = ? AND memberId=?";
            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            pstmt.setString(2,memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                userId=rs.getString("userid");
            }
        }
        catch (SQLException s)
        {
            logger.error("isMemberUser Sqlerror", s);
            PZExceptionHandler.raiseDBViolationException(Merchants.class.getName(),"isMemberUser()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,s.getMessage(),s.getCause());
        }
        catch (SystemError e)
        {
            logger.error("isMemberUser Systemerror", e);
            PZExceptionHandler.raiseDBViolationException(Merchants.class.getName(),"getAllOldProcessingMerchants()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return userId;
    }
    public Member addMerchant(Hashtable details) throws SystemError
    {
        Codec MYSQL_CODEC = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("insert into members (login,passwd,transpasswd,clkey,company_name,contact_persons,contact_emails,address,city,state,zip,country,telno,faxno,notifyemail,bussinessdevexecutive,dtstamp,brandname,sitename,directors,employees,potentialbusiness,acdetails,registeredaddress,bussinessaddress) values (");

        sb.append("'" +  ESAPI.encoder().encodeForSQL(MYSQL_CODEC, (String) details.get("login") )  + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC, (String) details.get("passwd") )  + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("transpasswd")) + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("company_name")) + "'");
        sb.append(",'" +ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("contact_persons")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("contact_emails")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC, (String) details.get("address")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("city")) + "'");

        if (details.get("state") != null)
            sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("state")) + "'");
        else
            sb.append(",''");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("zip")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("telno")) + "'");

        if (details.get("faxno") != null)
            sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("faxno")) + "'");
        else
            sb.append(",''");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("notifyemail")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("bussinessdevexecutive")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("brandname")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("sitename")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("directors")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("employees")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("potentialbusiness")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("acdetails")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String) details.get("registeredaddress")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC, (String) details.get("bussinessaddress")) + "')");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC, (String) details.get("actionExecutorId"))+ "')");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(MYSQL_CODEC,(String)details.get("actionExecutorName"))+"')");
        Member mem = new Member();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            Database.executeUpdate(sb.toString(), conn);
            String selquery = "select memberid from members where login=?";
            PreparedStatement p1=conn.prepareStatement(selquery);
            p1.setString(1,(String) details.get("login"));
            ResultSet rs = p1.executeQuery();
            if (rs.next())
            {
                mem.memberid = rs.getInt("memberid");
                mem.address = (String) details.get("address");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isservice = false;
            }
            else
            {
                throw new SystemError("Member Already Exists");
            }
            refresh();
        }
        catch (SystemError se)
        {
            logger.error("System Error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
    public Member addMerchant_new(Hashtable details) throws SystemError
    {
        Connection conn = null;
        Member mem = new Member();

        try
        {
            conn = Database.getConnection();
            String insertQ="insert into members (login,passwd,clkey,company_name,contact_persons,contact_emails,country,telno,dtstamp,sitename,actionExecutorId,actionExecutorName) values (?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            PreparedStatement pstmt1=conn.prepareStatement(insertQ);
            pstmt1.setString(1,(String) details.get("login"));
            pstmt1.setString(2,ESAPI.authenticator().hashPassword((String) details.get("passwd"),(String) details.get("login")));
            pstmt1.setString(3,Merchants.generateKey());
            pstmt1.setString(4,(String) details.get("company_name"));
            pstmt1.setString(5,(String) details.get("contact_persons"));
            pstmt1.setString(6,(String) details.get("contact_emails"));
            pstmt1.setString(7,(String) details.get("country"));
            pstmt1.setString(8,(String) details.get("telno"));
            pstmt1.setString(9,(String) details.get("sitename"));
            pstmt1.setString(10,(String) details.get("actionExecutorId"));
            pstmt1.setString(11,(String)details.get("actionExecutorName"));
            int i=pstmt1.executeUpdate();

            String selquery = "select memberid from members where login=?";
            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,(String) details.get("login"));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                mem.memberid = rs.getInt("memberid");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isservice = false;
            }
            refresh();
        }
        catch (SystemError se)
        {
            logger.error("System error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
    public  Member addMerchant_new(long accid,Hashtable details) throws SystemError
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("insert into members " +
                "(accid,login,clkey,company_name," +
                "contact_persons,contact_emails,country," +
                "telno,emailtoken,dtstamp,sitename,partnerId) values (");

        sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(accid))+ "");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("login")) + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("company_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("contact_persons")) + "'");
        sb.append(",'" + details.get("contact_emails") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("telno")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("emailtoken")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("sitename")) + "'");
        if(details.get("partnerid")!=null)
        {
            sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("partnerid")) + "')");
        }
        else
        {
            sb.append(",1)");
        }
        logger.debug("Add newmerchant");
        Member mem = new Member();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            Database.executeUpdate(sb.toString(), conn);
            String selquery = "select memberid from members where login=? and accid=?";
            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,(String) details.get("login"));
            pstmt.setLong(2,accid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                mem.memberid = rs.getInt("memberid");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isservice = false;
            }
            String query="insert into merchant_configuration(id,memberid,maincontact_phone) values (NULL,?,?)";
            PreparedStatement preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,String.valueOf(mem.memberid));
            preparedStatement.setString(2, String.valueOf(details.get("maincontact_phone")));
            int k1=preparedStatement.executeUpdate();
            if (k1>0)
            {
                refresh();
                FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
            }
        }
        catch (SystemError se)
        {
            logger.error("System error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }

    public  Member addMerchant_new(long accid,Hashtable details,PartnerDefaultConfigVO partnerDefaultConfigVO) throws SystemError
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String activation = functions.getPartnerActivation((String) details.get("partnerid"));
        StringBuffer sb = new StringBuffer("insert into members " +
                "(accid,login,clkey,company_name," +
                "contact_persons,contact_emails,country," +
                "telno,emailtoken,dtstamp,sitename,domain,ispartialRefund,isMultipleRefund,icici,isappmanageractivate,is_recurring,iscardregistrationallowed,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,card_check_limit,card_transaction_limit,check_limit,weekly_amount_limit,activation,haspaid,blacklistTransaction,flightMode,isExcessCaptureAllowed,isservice,autoredirect,vbv,masterCardSupported,autoSelectTerminal,isPODRequired,isRestrictedTicket,chargebackallowed_days,emailLimitEnabled,binService,expDateOffset,supportSection,card_whitelist_level,multiCurrencySupport,isPharma,isPoweredBy,template,ispcilogo,isTokenizationAllowed,isAddrDetailsRequired,tokenvaliddays,isCardEncryptionEnable,iswhitelisted,isIpWhitelisted,is_rest_whitelisted,hralertproof,hrparameterised,isrefund,refunddailylimit,refundallowed_days,isValidateEmail,smsactivation,customersmsactivation,invoicetemplate,ip_whitelist_invoice,maxScoreAllowed,maxScoreAutoReversal,onlineFraudCheck,isSplitPayment,splitPaymentType,ispartnerlogo,binRouting,personal_info_display,personal_info_validation,hosted_payment_page,vbvLogo,masterSecureLogo,emiSupport,internalFraudCheck,consentStmnt,card_velocity_check,merchant_order_details,isSecurityLogo,supportNoNeeded,isShareAllowed,isSignatureAllowed,isSaveReceiptAllowed,defaultLanguage,url,ipAddress,httpheader,actionExecutorId,actionExecutorName");
        if (activation.equals("Y"))
        {
            sb.append(" ,activation_date");
        }
        sb.append(",partnerId ) values (");
        sb.append("" + ESAPI.encoder().encodeForSQL(me, String.valueOf(accid)) + "");
        sb.append(",'" + details.get("login")+"'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("company_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("contact_persons")) + "'");
        sb.append(",'" + details.get("contact_emails") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("telno")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("emailtoken")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("sitename")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("domain")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPartialRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsMultipleRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantInterfaceAccess()) + "'");//icici

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsAppManagerActivate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRecurring()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsCardRegistrationAllowed()) + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCardCheckLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCardTransactionLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCheckLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyAmountLimit() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getHasPaid())+ "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getBlacklistTransaction()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getFlightMode()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsExcessCaptureAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsService()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getAutoRedirect()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getVbv()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMastercardSupported()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getAutoSelectTerminal()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPodRequired()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRestrictedTicket()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getChargebackAllowedDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getEmailLimitEnabled()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBinService()) + "'");
        sb.append(",'" + partnerDefaultConfigVO.getExpDateOffset() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getSupportSection()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getCardWhiteListLevel()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getMultiCurrencySupport()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPharma()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPoweredBy()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getTemplate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPciLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsTokenizationAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsAddressDetailsRequired()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getTokenValidDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsCardEncryptionEnable()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsIpWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRestWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHrAlertProof()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHrparameterised()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getRefundDailyLimit()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getRefundAllowedDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsValidateEmail()) + "'");
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCustReminderMail()) + "'");*/
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantEmailSent()) + "'");*/
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantSmsActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCustomerSmsActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getInvoiceTemplate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIpWhiteListInvoice()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMaxScoreAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMaxScoreAutoReversal()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getOnlineFraudCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsSplitPayment()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getSplitPaymentType()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPartnerLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBinRouting()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getPersonalInfoDisplay()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getPersonalInfoValidation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHostedPaymentPage()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getVbvLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMasterSecureLogo()) + "'");
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getChargebackMailSend()) + "'");*/
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getEmiSupport()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getInternalFraudCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("consent")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getCardVelocityCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getMerchantOrderDetails()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsSecurityLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getSupportNoNeeded()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsShareAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsSignatureAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsSaveReceiptAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getDefaultLanguage()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String)details.get("url")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String)details.get("ipAddress")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String)details.get("httpheader")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String)details.get("actionExecutorId")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String)details.get("actionExecutorName")) + "'");

        if (activation.equals("Y"))
        {
            sb.append(",unix_timestamp(now())");
        }
        if(details.get("partnerid")!=null)
            sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("partnerid")) + "')");
        else
        {
            sb.append(",1)");
        }

        logger.error("Add newmerchant:::::" + sb.toString());
        Member mem = new Member();
        Connection conn = null;
        PreparedStatement pstmt = null, preparedStatement=null;
        try
        {
            conn = Database.getConnection();
           int ks= Database.executeUpdate(sb.toString(), conn);
            if(ks>0){
                Thread.sleep(5000);
            }

            String selquery = "select memberid from members where login=? and accid=?";
            pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,(String) details.get("login"));
            pstmt.setLong(2,accid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                logger.error("result set memberid:::::" + rs.getInt("memberid"));

                mem.memberid = rs.getInt("memberid");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isservice = false;
            }
            String query="insert into merchant_configuration(id,memberid,maincontact_phone,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,merchantmgt_access,settings_fraudrule_config_access,settings_merchant_config_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access,settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_register_card_access,tokenmgt_registration_history_access,merchantmgt_user_management_access,ip_validation_required,refundcontact_name,refundcontact_mailid,refundcontact_ccmailid,refundcontact_phone,salescontact_name,salescontact_mailid,salescontact_ccmailid,salescontact_phone,fraudcontact_name,fraudcontact_mailid,fraudcontact_ccmailid,fraudcontact_phone,technicalcontact_name,technicalcontact_mailid,technicalcontact_ccmailid,technicalcontact_phone,billingcontact_name,billingcontact_mailid,billingcontact_ccmailid,billingcontact_phone,maincontact_ccmailid,cbcontact_name,cbcontact_mailid,cbcontact_ccmailid,cbcontact_phone,support_persons,support_emails,support_ccmailid,support_phone,settings_whitelist_details,settings_blacklist_details,emi_configuration,limitRouting,marketplace,checkoutTimer,checkoutTimerTime,rejected_transaction,virtual_checkout,isVirtualCheckoutAllowed,isMobileAllowedForVC,isEmailAllowedForVC,isCvvStore,actionExecutorId,actionExecutorName,reconciliationNotification,transactionNotification,refundNotification,chargebackNotification,merchantRegistrationMail,merchantChangePassword,merchantChangeProfile,transactionSuccessfulMail,transactionFailMail,transactionCapture,transactionPayoutSuccess,transactionPayoutFail,refundMail,chargebackMail,transactionInvoice,cardRegistration,payoutReport,monitoringAlertMail,monitoringSuspensionMail,highRiskRefunds,fraudFailedTxn,dailyFraudReport,customerTransactionSuccessfulMail,customerTransactionFailMail,customerTransactionPayoutSuccess,customerTransactionPayoutFail,customerRefundMail,customerTokenizationMail,isUniqueOrderIdRequired,maincontact_bccmailid,cbcontact_bccmailid,refundcontact_bccmailid,salescontact_bccmailid,fraudcontact_bccmailid,technicalcontact_bccmailid,billingcontact_bccmailid,support_bccmailid,emailTemplateLang,successReconMail,refundReconMail,chargebackReconMail,payoutReconMail,isMerchantLogoBO,cardExpiryDateCheck,payoutNotification,vpaAddressLimitCheck,vpaAddressDailyCount,vpaAddressAmountLimitCheck,vpaAddressDailyAmountLimit,payoutBankAccountNoLimitCheck,bankAccountNoDailyCount,payoutBankAccountNoAmountLimitCheck,bankAccountNoDailyAmountLimit,isDomainWhitelisted,customerIpLimitCheck,customerIpDailyCount,customerIpAmountLimitCheck,customerIpDailyAmountLimit,customerNameLimitCheck,customerNameDailyCount,customerNameAmountLimitCheck,customerNameDailyAmountLimit,customerEmailLimitCheck,customerEmailDailyCount,customerEmailAmountLimitCheck,customerEmailDailyAmountLimit,customerPhoneLimitCheck,customerPhoneDailyCount,customerPhoneAmountLimitCheck,customerPhoneDailyAmountLimit,inquiryNotification,paybylink,transmgt_payout_transactions,isOTPRequired,isCardStorageRequired,vpaAddressMonthlyCount,vpaAddressMonthlyAmountLimit,customerEmailMonthlyCount,customerEmailMonthlyAmountLimit,customerPhoneMonthlyCount,customerPhoneMonthlyAmountLimit,bankAccountNoMonthlyCount,bankAccountNoMonthlyAmountLimit,customerIpMonthlyCount,customerIpMonthlyAmountLimit,customerNameMonthlyCount,customerNameMonthlyAmountLimit,merchant_verify_otp) values (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,String.valueOf(mem.memberid));
            preparedStatement.setString(2, String.valueOf(details.get("maincontact_phone")));
            preparedStatement.setString(3,partnerDefaultConfigVO.getDashboardAccess());
            preparedStatement.setString(4,partnerDefaultConfigVO.getAccountingAccess());
            preparedStatement.setString(5,partnerDefaultConfigVO.getSettingAccess());
            preparedStatement.setString(6,partnerDefaultConfigVO.getTransactionsAccess());
            preparedStatement.setString(7,partnerDefaultConfigVO.getInvoicingAccess());
            preparedStatement.setString(8,partnerDefaultConfigVO.getVirtualTerminalAccess());
            preparedStatement.setString(9,partnerDefaultConfigVO.getMerchantMgtAccess());
            preparedStatement.setString(10,partnerDefaultConfigVO.getSettingsFraudRuleConfigAccess());
            preparedStatement.setString(11,partnerDefaultConfigVO.getSettingsMerchantConfigAccess());
            preparedStatement.setString(12,partnerDefaultConfigVO.getAccountsAccountSummaryAccess());
            preparedStatement.setString(13,partnerDefaultConfigVO.getAccountsChargesSummaryAccess());
            preparedStatement.setString(14,partnerDefaultConfigVO.getAccountsTransactionSummaryAccess());
            preparedStatement.setString(15,partnerDefaultConfigVO.getAccountsReportsSummaryAccess());
            preparedStatement.setString(16,partnerDefaultConfigVO.getSettingsMerchantProfileAccess());
            preparedStatement.setString(17,partnerDefaultConfigVO.getSettingsOrganisationProfileAccess());
            preparedStatement.setString(18,partnerDefaultConfigVO.getSettingsCheckoutPageAccess());
            preparedStatement.setString(19,partnerDefaultConfigVO.getSettingsGenerateKeyAccess());
            preparedStatement.setString(20,partnerDefaultConfigVO.getSettingsInvoiceConfigAccess());
            preparedStatement.setString(21,partnerDefaultConfigVO.getTransMgtTransactionAccess());
            preparedStatement.setString(22,partnerDefaultConfigVO.getTransMgtCaptureAccess());
            preparedStatement.setString(23,partnerDefaultConfigVO.getTransMgtReversalAccess());
            preparedStatement.setString(24,partnerDefaultConfigVO.getTransMgtPayoutAccess());
            preparedStatement.setString(25,partnerDefaultConfigVO.getInvoiceGenerateAccess());
            preparedStatement.setString(26,partnerDefaultConfigVO.getInvoiceHistoryAccess());
            preparedStatement.setString(27,partnerDefaultConfigVO.getTokenMgtRegisterCardAccess());
            preparedStatement.setString(28,partnerDefaultConfigVO.getTokenMgtRegistrationHistoryAccess());
            preparedStatement.setString(29,partnerDefaultConfigVO.getMerchantMgtUserManagementAccess());
            preparedStatement.setString(30,partnerDefaultConfigVO.getIpValidationRequired());
            preparedStatement.setString(31, String.valueOf(details.get("refundcontact_name")));
            preparedStatement.setString(32, String.valueOf(details.get("refundcontact_mailid")));
            preparedStatement.setString(33, String.valueOf(details.get("refundcontact_ccmailid")));
            preparedStatement.setString(34, String.valueOf(details.get("refundcontact_phone")));
            preparedStatement.setString(35, String.valueOf(details.get("salescontact_name")));
            preparedStatement.setString(36, String.valueOf(details.get("salescontact_mailid")));
            preparedStatement.setString(37, String.valueOf(details.get("salescontact_ccmailid")));
            preparedStatement.setString(38, String.valueOf(details.get("salescontact_phone")));
            preparedStatement.setString(39,String.valueOf(details.get("fraudcontact_name")));
            preparedStatement.setString(40,String.valueOf(details.get("fraudcontact_mailid")));
            preparedStatement.setString(41,String.valueOf(details.get("fraudcontact_ccmailid")));
            preparedStatement.setString(42,String.valueOf(details.get("fraudcontact_phone")));
            preparedStatement.setString(43,String.valueOf(details.get("technicalcontact_name")));
            preparedStatement.setString(44, String.valueOf(details.get("technicalcontact_mailid")));
            preparedStatement.setString(45, String.valueOf(details.get("technicalcontact_ccmailid")));
            preparedStatement.setString(46, String.valueOf(details.get("technicalcontact_phone")));
            preparedStatement.setString(47, String.valueOf(details.get("billingcontact_name")));
            preparedStatement.setString(48, String.valueOf(details.get("billingcontact_mailid")));
            preparedStatement.setString(49, String.valueOf(details.get("billingcontact_ccmailid")));
            preparedStatement.setString(50, String.valueOf(details.get("billingcontact_phone")));
            preparedStatement.setString(51, String.valueOf(details.get("maincontact_ccmailid")));
            preparedStatement.setString(52, String.valueOf(details.get("cbcontact_name")));
            preparedStatement.setString(53, String.valueOf(details.get("cbcontact_mailid")));
            preparedStatement.setString(54, String.valueOf(details.get("cbcontact_ccmailid")));
            preparedStatement.setString(55, String.valueOf(details.get("cbcontact_phone")));
            preparedStatement.setString(56, String.valueOf(details.get("support_persons")));
            preparedStatement.setString(57, String.valueOf(details.get("support_emails")));
            preparedStatement.setString(58, String.valueOf(details.get("support_ccmailid")));
            preparedStatement.setString(59, String.valueOf(details.get("support_phone")));
            preparedStatement.setString(60, partnerDefaultConfigVO.getSettingWhiteListDetails());
            preparedStatement.setString(61, partnerDefaultConfigVO.getSettingBlacklistDetails());
            preparedStatement.setString(62, partnerDefaultConfigVO.getEmiConfiguration());
            preparedStatement.setString(63, partnerDefaultConfigVO.getLimitRouting());
            preparedStatement.setString(64, partnerDefaultConfigVO.getMarketplace());
            preparedStatement.setString(65, partnerDefaultConfigVO.getCheckoutTimer());
            preparedStatement.setString(66, partnerDefaultConfigVO.getCheckoutTimerTime());
            preparedStatement.setString(67, partnerDefaultConfigVO.getRejectedTransaction());
            preparedStatement.setString(68, partnerDefaultConfigVO.getVirtualCheckOut());
            preparedStatement.setString(69, partnerDefaultConfigVO.getIsVirtualCheckoutAllowed());
            preparedStatement.setString(70, partnerDefaultConfigVO.getIsMobileAllowedForVC());
            preparedStatement.setString(71, partnerDefaultConfigVO.getIsEmailAllowedForVC());
            preparedStatement.setString(72, partnerDefaultConfigVO.getIsCvvStore());
            preparedStatement.setString(73,partnerDefaultConfigVO.getActionExecutorId());
            preparedStatement.setString(74,partnerDefaultConfigVO.getActionExecutorName());
            preparedStatement.setString(75,partnerDefaultConfigVO.getReconciliationNotification());
            preparedStatement.setString(76,partnerDefaultConfigVO.getTransactionNotification());
            preparedStatement.setString(77,partnerDefaultConfigVO.getRefundNotification());
            preparedStatement.setString(78,partnerDefaultConfigVO.getChargebackNotification());
            preparedStatement.setString(79,partnerDefaultConfigVO.getMerchantRegistrationMail());
            preparedStatement.setString(80,partnerDefaultConfigVO.getMerchantChangePassword());
            preparedStatement.setString(81,partnerDefaultConfigVO.getMerchantChangeProfile());
            preparedStatement.setString(82,partnerDefaultConfigVO.getTransactionSuccessfulMail());
            preparedStatement.setString(83,partnerDefaultConfigVO.getTransactionFailMail());
            preparedStatement.setString(84,partnerDefaultConfigVO.getTransactionCapture());
            preparedStatement.setString(85,partnerDefaultConfigVO.getTransactionPayoutSuccess());
            preparedStatement.setString(86,partnerDefaultConfigVO.getTransactionPayoutFail());
            preparedStatement.setString(87,partnerDefaultConfigVO.getRefundMail());
            preparedStatement.setString(88,partnerDefaultConfigVO.getChargebackMail());
            preparedStatement.setString(89,partnerDefaultConfigVO.getTransactionInvoice());
            preparedStatement.setString(90,partnerDefaultConfigVO.getCardRegistration());
            preparedStatement.setString(91,partnerDefaultConfigVO.getPayoutReport());
            preparedStatement.setString(92,partnerDefaultConfigVO.getMonitoringAlertMail());
            preparedStatement.setString(93,partnerDefaultConfigVO.getMonitoringSuspensionMail());
            preparedStatement.setString(94,partnerDefaultConfigVO.getHighRiskRefunds());
            preparedStatement.setString(95,partnerDefaultConfigVO.getFraudFailedTxn());
            preparedStatement.setString(96,partnerDefaultConfigVO.getDailyFraudReport());
            preparedStatement.setString(97,partnerDefaultConfigVO.getCustomerTransactionSuccessfulMail());
            preparedStatement.setString(98,partnerDefaultConfigVO.getCustomerTransactionFailMail());
            preparedStatement.setString(99,partnerDefaultConfigVO.getCustomerTransactionPayoutSuccess());
            preparedStatement.setString(100,partnerDefaultConfigVO.getCustomerTransactionPayoutFail());
            preparedStatement.setString(101,partnerDefaultConfigVO.getCustomerRefundMail());
            preparedStatement.setString(102,partnerDefaultConfigVO.getCustomerTokenizationMail());
            preparedStatement.setString(103,partnerDefaultConfigVO.getIsUniqueOrderIdRequired());
            preparedStatement.setString(104, String.valueOf(details.get("maincontact_bccmailid")));
            preparedStatement.setString(105, String.valueOf(details.get("cbcontact_bccmailid")));
            preparedStatement.setString(106, String.valueOf(details.get("refundcontact_bccmailid")));
            preparedStatement.setString(107, String.valueOf(details.get("salescontact_bccmailid")));
            preparedStatement.setString(108, String.valueOf(details.get("fraudcontact_bccmailid")));
            preparedStatement.setString(109, String.valueOf(details.get("technicalcontact_bccmailid")));
            preparedStatement.setString(110,String.valueOf(details.get("billingcontact_bccmailid")));
            preparedStatement.setString(111,String.valueOf(details.get("support_bccmailid")));
            preparedStatement.setString(112,partnerDefaultConfigVO.getEmailTemplateLang());
            preparedStatement.setString(113,partnerDefaultConfigVO.getSuccessReconMail());
            preparedStatement.setString(114,partnerDefaultConfigVO.getRefundReconMail());
            preparedStatement.setString(115,partnerDefaultConfigVO.getChargebackReconMail());
            preparedStatement.setString(116,partnerDefaultConfigVO.getPayoutReconMail());
            preparedStatement.setString(117,partnerDefaultConfigVO.getIsMerchantLogoBO());
            preparedStatement.setString(118,partnerDefaultConfigVO.getCardExpiryDateCheck());
            preparedStatement.setString(119,partnerDefaultConfigVO.getPayoutNotification());
            preparedStatement.setString(120,partnerDefaultConfigVO.getVpaAddressLimitCheck());
            preparedStatement.setString(121,partnerDefaultConfigVO.getVpaAddressDailyCount());
            preparedStatement.setString(122,partnerDefaultConfigVO.getVpaAddressAmountLimitCheck());
            preparedStatement.setString(123,partnerDefaultConfigVO.getVpaAddressDailyAmountLimit());
            preparedStatement.setString(124,partnerDefaultConfigVO.getPayoutBankAccountNoLimitCheck());
            preparedStatement.setString(125,partnerDefaultConfigVO.getBankAccountNoDailyCount());
            preparedStatement.setString(126,partnerDefaultConfigVO.getPayoutBankAccountNoAmountLimitCheck());
            preparedStatement.setString(127,partnerDefaultConfigVO.getBankAccountNoDailyAmountLimit());
            preparedStatement.setString(128,partnerDefaultConfigVO.getIsDomainWhitelisted());
            preparedStatement.setString(129,partnerDefaultConfigVO.getCustomerIpLimitCheck());
            preparedStatement.setString(130,partnerDefaultConfigVO.getCustomerIpDailyCount());
            preparedStatement.setString(131,partnerDefaultConfigVO.getCustomerIpAmountLimitCheck());
            preparedStatement.setString(132,partnerDefaultConfigVO.getCustomerIpDailyAmountLimit());
            preparedStatement.setString(133,partnerDefaultConfigVO.getCustomerNameLimitCheck());
            preparedStatement.setString(134,partnerDefaultConfigVO.getCustomerNameDailyCount());
            preparedStatement.setString(135,partnerDefaultConfigVO.getCustomerNameAmountLimitCheck());
            preparedStatement.setString(136,partnerDefaultConfigVO.getCustomerNameDailyAmountLimit());
            preparedStatement.setString(137,partnerDefaultConfigVO.getCustomerEmailLimitCheck());
            preparedStatement.setString(138,partnerDefaultConfigVO.getCustomerEmailDailyCount());
            preparedStatement.setString(139,partnerDefaultConfigVO.getCustomerEmailAmountLimitCheck());
            preparedStatement.setString(140,partnerDefaultConfigVO.getCustomerEmailDailyAmountLimit());
            preparedStatement.setString(141,partnerDefaultConfigVO.getCustomerPhoneLimitCheck());
            preparedStatement.setString(142,partnerDefaultConfigVO.getCustomerPhoneDailyCount());
            preparedStatement.setString(143,partnerDefaultConfigVO.getCustomerPhoneAmountLimitCheck());
            preparedStatement.setString(144,partnerDefaultConfigVO.getCustomerPhoneDailyAmountLimit());
            preparedStatement.setString(145,partnerDefaultConfigVO.getInquiryNotification());
            preparedStatement.setString(146,partnerDefaultConfigVO.getPaybylink());
            preparedStatement.setString(147,partnerDefaultConfigVO.getTransMgtPayoutTransaction());
            preparedStatement.setString(148,partnerDefaultConfigVO.getIsOTPRequired());
            preparedStatement.setString(149,partnerDefaultConfigVO.getIsCardStorageRequired());
            preparedStatement.setString(150,partnerDefaultConfigVO.getVpaAddressMonthlyCount());
            preparedStatement.setString(151,partnerDefaultConfigVO.getVpaAddressMonthlyAmountLimit());
            preparedStatement.setString(152,partnerDefaultConfigVO.getCustomerEmailMonthlyCount());
            preparedStatement.setString(153,partnerDefaultConfigVO.getCustomerEmailMonthlyAmountLimit());
            preparedStatement.setString(154,partnerDefaultConfigVO.getCustomerPhoneMonthlyCount());
            preparedStatement.setString(155,partnerDefaultConfigVO.getCustomerPhoneMonthlyAmountLimit());
            preparedStatement.setString(156,partnerDefaultConfigVO.getBankAccountNoMonthlyCount());
            preparedStatement.setString(157,partnerDefaultConfigVO.getBankAccountNoMonthlyAmountLimit());
            preparedStatement.setString(158,partnerDefaultConfigVO.getCustomerIpMonthlyCount());
            preparedStatement.setString(159,partnerDefaultConfigVO.getCustomerIpMonthlyAmountLimit());
            preparedStatement.setString(160,partnerDefaultConfigVO.getCustomerNameMonthlyCount());
            preparedStatement.setString(161,partnerDefaultConfigVO.getCustomerNameMonthlyAmountLimit());
            preparedStatement.setString(162,partnerDefaultConfigVO.getMerchant_verify_otp());
            logger.error("merchant_configuration query:::::"+preparedStatement);
            int k1=preparedStatement.executeUpdate();

            if (k1>0)
            {
                refresh();
                FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
            }
        }
        catch (SystemError se)
        {
            logger.error("System error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return mem;
    }
    public  Member addMerchantNew(long accid, Hashtable details) throws SystemError
    {
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("insert into members (accid,login,clkey,company_name,contact_persons,contact_emails,country,telno,dtstamp,sitename,domain,emailtoken,partnerId,actionExecutorId,actionExecutorName) values (");

        sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(accid))+ "");
        sb.append(",'" +details.get("login") + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("company_name")) + "'");
        sb.append(",'" + details.get("contact_persons") + "'");
        sb.append(",'" + details.get("contact_emails") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("telno")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("sitename")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("domain")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("emailtoken")) + "'");
        sb.append(",'"+ ESAPI.encoder().encodeForSQL(me,(String)details.get("actionExecutorId"))+"'");
        sb.append(",'"+ ESAPI.encoder().encodeForSQL(me,(String)details.get("actionExecutorName"))+"'");
        if(details.get("partnerid")!=null)
        {
            sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("partnerid")) + "')");
        }
        else
            sb.append(",1)");

        logger.debug("Insert query====="+sb);
        Member mem = new Member();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            int k=Database.executeUpdate(sb.toString(), conn);
            if(k>0)
            {
                String selquery = "select memberid from members where login=? and accid=?";
                PreparedStatement pstmt= conn.prepareStatement(selquery);
                pstmt.setString(1,(String) details.get("login"));
                pstmt.setLong(2,accid);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                {
                    mem.memberid = rs.getInt("memberid");
                    mem.telno = (String) details.get("telno");
                    mem.contactemails = (String) details.get("contact_emails");
                    mem.isservice = false;
                }

                String query="insert into merchant_configuration(id,memberid,maincontact_ccmailid,maincontact_phone,cbcontact_name,cbcontact_mailid,cbcontact_ccmailid,cbcontact_phone,refundcontact_name,refundcontact_mailid,refundcontact_ccmailid,refundcontact_phone,salescontact_name,salescontact_mailid,salescontact_ccmailid,salescontact_phone,fraudcontact_name,fraudcontact_mailid,fraudcontact_ccmailid,fraudcontact_phone,technicalcontact_name,technicalcontact_mailid,technicalcontact_ccmailid,technicalcontact_phone,billingcontact_ccmailid,billingcontact_phone,billingcontact_name,billingcontact_mailid) values (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement=conn.prepareStatement(query);
                preparedStatement.setString(1,String.valueOf(mem.memberid));
                preparedStatement.setString(2,(String)details.get("maincontact_ccmailid"));
                preparedStatement.setString(3,(String)details.get("maincontact_phone"));
                preparedStatement.setString(4,(String)details.get("cbcontact_name"));
                preparedStatement.setString(5,(String)details.get("cbcontact_mailid"));
                preparedStatement.setString(6,(String)details.get("cbcontact_ccmailid"));
                preparedStatement.setString(7,(String)details.get("cbcontact_phone"));
                preparedStatement.setString(8,(String)details.get("refundcontact_name"));
                preparedStatement.setString(9,(String)details.get("refundcontact_mailid"));
                preparedStatement.setString(10,(String)details.get("refundcontact_ccmailid"));
                preparedStatement.setString(11,(String)details.get("refundcontact_phone"));
                preparedStatement.setString(12,(String)details.get("salescontact_name"));
                preparedStatement.setString(13,(String)details.get("salescontact_mailid"));
                preparedStatement.setString(14,(String)details.get("salescontact_ccmailid"));
                preparedStatement.setString(15,(String)details.get("salescontact_phone"));
                preparedStatement.setString(16,(String)details.get("fraudcontact_name"));
                preparedStatement.setString(17,(String)details.get("fraudcontact_mailid"));
                preparedStatement.setString(18,(String)details.get("fraudcontact_ccmailid"));
                preparedStatement.setString(19,(String)details.get("fraudcontact_phone"));
                preparedStatement.setString(20,(String)details.get("technicalcontact_name"));
                preparedStatement.setString(21,(String)details.get("technicalcontact_mailid"));
                preparedStatement.setString(22,(String)details.get("technicalcontact_ccmailid"));
                preparedStatement.setString(23,(String)details.get("technicalcontact_phone"));
                preparedStatement.setString(24,(String)details.get("billingcontact_ccmailid"));
                preparedStatement.setString(25,(String)details.get("billingcontact_phone"));
                preparedStatement.setString(26,(String)details.get("billingcontact_name"));
                preparedStatement.setString(27,(String)details.get("billingcontact_mailid"));
                int k1=preparedStatement.executeUpdate();
                if (k1>0)
                {
                    Merchants.refresh();
                    FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
                }
            }
        }
        catch (SystemError se)
        {
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
    public  Member addMerchantNew(long accid,Hashtable details,PartnerDefaultConfigVO partnerDefaultConfigVO) throws SystemError
    {


        logger.error("actionExecutorId------->"+details.get("actionExecutorId"));
        logger.error("actionExecutorName----->"+details.get("actionExecutorName"));




        String activation = functions.getPartnerActivation((String)details.get("partnerid"));
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuilder sb = new StringBuilder("insert into members (accid,login,clkey,company_name,contact_persons,contact_emails,country,telno,dtstamp,sitename,domain,emailtoken,ispartialRefund,isMultipleRefund,icici,isappmanageractivate,is_recurring,iscardregistrationallowed,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,card_check_limit,card_transaction_limit,check_limit,weekly_amount_limit,activation,haspaid,blacklistTransaction,flightMode,isExcessCaptureAllowed,isservice,autoredirect,vbv,masterCardSupported,autoSelectTerminal,isPODRequired,isRestrictedTicket,chargebackallowed_days,emailLimitEnabled,binService,expDateOffset,supportSection,card_whitelist_level,multiCurrencySupport,isPharma,isPoweredBy,template,ispcilogo,isTokenizationAllowed,isAddrDetailsRequired,tokenvaliddays,isCardEncryptionEnable,iswhitelisted,isIpWhitelisted,is_rest_whitelisted,hralertproof,hrparameterised,isrefund,refunddailylimit,refundallowed_days,isValidateEmail,smsactivation,customersmsactivation,invoicetemplate,ip_whitelist_invoice,maxScoreAllowed,maxScoreAutoReversal,onlineFraudCheck,isSplitPayment,splitPaymentType,ispartnerlogo,binRouting,personal_info_display,personal_info_validation,hosted_payment_page,vbvLogo,masterSecureLogo,emiSupport,internalFraudCheck,card_velocity_check,merchant_order_details,isSecurityLogo,url,ipAddress,httpheader,supportNoNeeded,isShareAllowed,isSignatureAllowed,isSaveReceiptAllowed,defaultLanguage");
        if (activation.equals("Y"))
        {
            sb.append(" ,activation_date");
        }

        sb.append(" ,partnerId,actionExecutorId,actionExecutorName ) values (");

        sb.append("" + ESAPI.encoder().encodeForSQL(me, String.valueOf(accid))+ "");
        sb.append(",'" + details.get("login") + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("company_name")) + "'");
        sb.append(",'" + details.get("contact_persons") + "'");
        sb.append(",'" + details.get("contact_emails") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("telno")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("sitename")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("domain")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("emailtoken")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPartialRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsMultipleRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantInterfaceAccess()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsAppManagerActivate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRecurring()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsCardRegistrationAllowed()) + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyCardAmountLimit() + "'");

        sb.append(",'" + partnerDefaultConfigVO.getWeeklyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCardCheckLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCardTransactionLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCheckLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyAmountLimit() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHasPaid())+ "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBlacklistTransaction()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getFlightMode()) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsExcessCaptureAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsService()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getAutoRedirect()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getVbv()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMastercardSupported()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getAutoSelectTerminal()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPodRequired()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRestrictedTicket()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getChargebackAllowedDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getEmailLimitEnabled()) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBinService()) + "'");
        sb.append(",'" + partnerDefaultConfigVO.getExpDateOffset() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getSupportSection()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCardWhiteListLevel()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMultiCurrencySupport()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPharma()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPoweredBy()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getTemplate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPciLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsTokenizationAllowed()) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsAddressDetailsRequired()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getTokenValidDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsCardEncryptionEnable()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsIpWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRestWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHrAlertProof()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHrparameterised()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getRefundDailyLimit()) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getRefundAllowedDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsValidateEmail()) + "'");
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCustReminderMail()) + "'");*/
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantEmailSent()) + "'");*/
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantSmsActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCustomerSmsActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getInvoiceTemplate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIpWhiteListInvoice()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMaxScoreAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMaxScoreAutoReversal()) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getOnlineFraudCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsSplitPayment()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getSplitPaymentType()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPartnerLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBinRouting()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getPersonalInfoDisplay()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getPersonalInfoValidation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHostedPaymentPage()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getVbvLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMasterSecureLogo()) + "'");
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getChargebackMailSend()) + "'");*/
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getEmiSupport()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getInternalFraudCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCardVelocityCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantOrderDetails()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsSecurityLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("url")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("ipAddress")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("httpheader")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getSupportNoNeeded()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsShareAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsSignatureAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getIsSaveReceiptAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getDefaultLanguage()) + "'");
        if (activation.equals("Y"))
        {
            sb.append(",unix_timestamp(now())");
        }

        if(details.get("partnerid")!=null)
        {
            sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("partnerid")) + "')");
        }
        else
            sb.append(",1");
            sb.append(",'"+ESAPI.encoder().encodeForSQL(me,(String) details.get("actionExecutorId"))+"'");
            sb.append(",'"+ESAPI.encoder().encodeForSQL(me,(String) details.get("actionExecutorName"))+"')");

        logger.error("Insert query====="+sb.toString());
        Member mem = new Member();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            int k=Database.executeUpdate(sb.toString(), conn);
            if(k>0)
            {
                String selquery = "select memberid from members where login=? and accid=?";
                PreparedStatement pstmt= conn.prepareStatement(selquery);
                pstmt.setString(1,(String) details.get("login"));
                pstmt.setLong(2,accid);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                {
                    mem.memberid = rs.getInt("memberid");
                    mem.telno = (String) details.get("telno");
                    mem.contactemails = (String) details.get("contact_emails");
                    mem.isservice = false;
                }
                String query="insert into merchant_configuration(id,memberid,maincontact_ccmailid,maincontact_phone,cbcontact_name,cbcontact_mailid,cbcontact_ccmailid,cbcontact_phone,refundcontact_name,refundcontact_mailid,refundcontact_ccmailid,refundcontact_phone,salescontact_name,salescontact_mailid,salescontact_ccmailid,salescontact_phone,fraudcontact_name,fraudcontact_mailid,fraudcontact_ccmailid,fraudcontact_phone,technicalcontact_name,technicalcontact_mailid,technicalcontact_ccmailid,technicalcontact_phone,billingcontact_ccmailid,billingcontact_phone,billingcontact_name,billingcontact_mailid,support_persons,support_emails,support_ccmailid,support_phone,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,merchantmgt_access,settings_fraudrule_config_access,settings_merchant_config_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access,settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_register_card_access,tokenmgt_registration_history_access,merchantmgt_user_management_access,ip_validation_required,settings_whitelist_details,settings_blacklist_details,emi_configuration,limitRouting,checkoutTimer,checkoutTimerTime,marketplace,rejected_transaction,virtual_checkout,isVirtualCheckoutAllowed,isMobileAllowedForVC,isEmailAllowedForVC,isCvvStore,reconciliationNotification,transactionNotification,refundNotification,chargebackNotification,merchantRegistrationMail,merchantChangePassword,merchantChangeProfile,transactionSuccessfulMail,transactionFailMail,transactionCapture,transactionPayoutSuccess,transactionPayoutFail,refundMail,chargebackMail,transactionInvoice,cardRegistration,payoutReport,monitoringAlertMail,monitoringSuspensionMail,highRiskRefunds,fraudFailedTxn,dailyFraudReport,customerTransactionSuccessfulMail,customerTransactionFailMail,customerTransactionPayoutSuccess,customerTransactionPayoutFail,customerRefundMail,customerTokenizationMail,isUniqueOrderIdRequired,maincontact_bccmailid,cbcontact_bccmailid,refundcontact_bccmailid,salescontact_bccmailid,fraudcontact_bccmailid,technicalcontact_bccmailid,billingcontact_bccmailid,support_bccmailid,emailTemplateLang,successReconMail,refundReconMail,chargebackReconMail,payoutReconMail,isMerchantLogoBO,cardExpiryDateCheck,payoutNotification,vpaAddressLimitCheck,vpaAddressDailyCount,vpaAddressAmountLimitCheck,vpaAddressDailyAmountLimit,payoutBankAccountNoLimitCheck,bankAccountNoDailyCount,payoutBankAccountNoAmountLimitCheck,bankAccountNoDailyAmountLimit,isDomainWhitelisted,customerIpLimitCheck,customerIpDailyCount,customerIpAmountLimitCheck,customerIpDailyAmountLimit,customerNameLimitCheck,customerNameDailyCount,customerNameAmountLimitCheck,customerNameDailyAmountLimit,customerEmailLimitCheck,customerEmailDailyCount,customerEmailAmountLimitCheck,customerEmailDailyAmountLimit,customerPhoneLimitCheck,customerPhoneDailyCount,customerPhoneAmountLimitCheck,customerPhoneDailyAmountLimit,inquiryNotification,paybylink,transmgt_payout_transactions,isOTPRequired,isCardStorageRequired,vpaAddressMonthlyCount,vpaAddressMonthlyAmountLimit,customerEmailMonthlyCount,customerEmailMonthlyAmountLimit,customerPhoneMonthlyCount,customerPhoneMonthlyAmountLimit,bankAccountNoMonthlyCount,bankAccountNoMonthlyAmountLimit,customerIpMonthlyCount,customerIpMonthlyAmountLimit,customerNameMonthlyCount,customerNameMonthlyAmountLimit) values (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement=conn.prepareStatement(query);
                preparedStatement.setString(1,String.valueOf(mem.memberid));
                preparedStatement.setString(2,(String)details.get("maincontact_ccmailid"));
                preparedStatement.setString(3,(String)details.get("maincontact_phone"));
                preparedStatement.setString(4,(String)details.get("cbcontact_name"));
                preparedStatement.setString(5,(String)details.get("cbcontact_mailid"));
                preparedStatement.setString(6,(String)details.get("cbcontact_ccmailid"));
                preparedStatement.setString(7,(String)details.get("cbcontact_phone"));
                preparedStatement.setString(8,(String)details.get("refundcontact_name"));
                preparedStatement.setString(9,(String)details.get("refundcontact_mailid"));
                preparedStatement.setString(10,(String)details.get("refundcontact_ccmailid"));
                preparedStatement.setString(11,(String)details.get("refundcontact_phone"));
                preparedStatement.setString(12,(String)details.get("salescontact_name"));
                preparedStatement.setString(13,(String)details.get("salescontact_mailid"));
                preparedStatement.setString(14,(String)details.get("salescontact_ccmailid"));
                preparedStatement.setString(15,(String)details.get("salescontact_phone"));
                preparedStatement.setString(16,(String)details.get("fraudcontact_name"));
                preparedStatement.setString(17,(String)details.get("fraudcontact_mailid"));
                preparedStatement.setString(18,(String)details.get("fraudcontact_ccmailid"));
                preparedStatement.setString(19,(String)details.get("fraudcontact_phone"));
                preparedStatement.setString(20,(String)details.get("technicalcontact_name"));
                preparedStatement.setString(21,(String)details.get("technicalcontact_mailid"));
                preparedStatement.setString(22,(String)details.get("technicalcontact_ccmailid"));
                preparedStatement.setString(23,(String)details.get("technicalcontact_phone"));
                preparedStatement.setString(24,(String)details.get("billingcontact_ccmailid"));
                preparedStatement.setString(25,(String)details.get("billingcontact_phone"));
                preparedStatement.setString(26,(String)details.get("billingcontact_name"));
                preparedStatement.setString(27,(String)details.get("billingcontact_mailid"));
                preparedStatement.setString(28,(String)details.get("support_persons"));
                preparedStatement.setString(29,(String)details.get("support_emails"));
                preparedStatement.setString(30,(String)details.get("support_ccmailid"));
                preparedStatement.setString(31,(String)details.get("support_phone"));
                preparedStatement.setString(32,partnerDefaultConfigVO.getDashboardAccess());
                preparedStatement.setString(33,partnerDefaultConfigVO.getAccountingAccess());
                preparedStatement.setString(34,partnerDefaultConfigVO.getSettingAccess());
                preparedStatement.setString(35,partnerDefaultConfigVO.getTransactionsAccess());
                preparedStatement.setString(36,partnerDefaultConfigVO.getInvoicingAccess());
                preparedStatement.setString(37,partnerDefaultConfigVO.getVirtualTerminalAccess());
                preparedStatement.setString(38,partnerDefaultConfigVO.getMerchantMgtAccess());
                preparedStatement.setString(39,partnerDefaultConfigVO.getSettingsFraudRuleConfigAccess());
                preparedStatement.setString(40,partnerDefaultConfigVO.getSettingsMerchantConfigAccess());
                preparedStatement.setString(41,partnerDefaultConfigVO.getAccountsAccountSummaryAccess());
                preparedStatement.setString(42,partnerDefaultConfigVO.getAccountsChargesSummaryAccess());
                preparedStatement.setString(43,partnerDefaultConfigVO.getAccountsTransactionSummaryAccess());
                preparedStatement.setString(44,partnerDefaultConfigVO.getAccountsReportsSummaryAccess());
                preparedStatement.setString(45,partnerDefaultConfigVO.getSettingsMerchantProfileAccess());
                preparedStatement.setString(46,partnerDefaultConfigVO.getSettingsOrganisationProfileAccess());
                preparedStatement.setString(47,partnerDefaultConfigVO.getSettingsCheckoutPageAccess());
                preparedStatement.setString(48,partnerDefaultConfigVO.getSettingsGenerateKeyAccess());
                preparedStatement.setString(49,partnerDefaultConfigVO.getSettingsInvoiceConfigAccess());
                preparedStatement.setString(50,partnerDefaultConfigVO.getTransMgtTransactionAccess());
                preparedStatement.setString(51,partnerDefaultConfigVO.getTransMgtCaptureAccess());
                preparedStatement.setString(52,partnerDefaultConfigVO.getTransMgtReversalAccess());
                preparedStatement.setString(53,partnerDefaultConfigVO.getTransMgtPayoutAccess());
                preparedStatement.setString(54,partnerDefaultConfigVO.getInvoiceGenerateAccess());
                preparedStatement.setString(55,partnerDefaultConfigVO.getInvoiceHistoryAccess());
                preparedStatement.setString(56,partnerDefaultConfigVO.getTokenMgtRegisterCardAccess());
                preparedStatement.setString(57,partnerDefaultConfigVO.getTokenMgtRegistrationHistoryAccess());
                preparedStatement.setString(58,partnerDefaultConfigVO.getMerchantMgtUserManagementAccess());
                preparedStatement.setString(59,partnerDefaultConfigVO.getIpValidationRequired());
                preparedStatement.setString(60, partnerDefaultConfigVO.getSettingWhiteListDetails());
                preparedStatement.setString(61, partnerDefaultConfigVO.getSettingBlacklistDetails());
                preparedStatement.setString(62, partnerDefaultConfigVO.getEmiConfiguration());
                preparedStatement.setString(63, partnerDefaultConfigVO.getLimitRouting());
                preparedStatement.setString(64, partnerDefaultConfigVO.getCheckoutTimer());
                preparedStatement.setString(65, partnerDefaultConfigVO.getCheckoutTimerTime());
                preparedStatement.setString(66, partnerDefaultConfigVO.getMarketplace());
                preparedStatement.setString(67, partnerDefaultConfigVO.getRejectedTransaction());
                preparedStatement.setString(68, partnerDefaultConfigVO.getVirtualCheckOut());
                preparedStatement.setString(69, partnerDefaultConfigVO.getIsVirtualCheckoutAllowed());
                preparedStatement.setString(70, partnerDefaultConfigVO.getIsMobileAllowedForVC());
                preparedStatement.setString(71, partnerDefaultConfigVO.getIsEmailAllowedForVC());
                preparedStatement.setString(72, partnerDefaultConfigVO.getIsCvvStore());
                preparedStatement.setString(73,partnerDefaultConfigVO.getReconciliationNotification());
                preparedStatement.setString(74,partnerDefaultConfigVO.getTransactionNotification());
                preparedStatement.setString(75,partnerDefaultConfigVO.getReconciliationNotification());
                preparedStatement.setString(76,partnerDefaultConfigVO.getChargebackNotification());
                preparedStatement.setString(77,partnerDefaultConfigVO.getMerchantRegistrationMail());
                preparedStatement.setString(78,partnerDefaultConfigVO.getMerchantChangePassword());
                preparedStatement.setString(79,partnerDefaultConfigVO.getMerchantChangeProfile());
                preparedStatement.setString(80,partnerDefaultConfigVO.getTransactionSuccessfulMail());
                preparedStatement.setString(81,partnerDefaultConfigVO.getTransactionFailMail());
                preparedStatement.setString(82,partnerDefaultConfigVO.getTransactionCapture());
                preparedStatement.setString(83,partnerDefaultConfigVO.getTransactionPayoutSuccess());
                preparedStatement.setString(84,partnerDefaultConfigVO.getTransactionPayoutFail());
                preparedStatement.setString(85,partnerDefaultConfigVO.getRefundMail());
                preparedStatement.setString(86,partnerDefaultConfigVO.getChargebackMail());
                preparedStatement.setString(87,partnerDefaultConfigVO.getTransactionInvoice());
                preparedStatement.setString(88,partnerDefaultConfigVO.getCardRegistration());
                preparedStatement.setString(89,partnerDefaultConfigVO.getPayoutReport());
                preparedStatement.setString(90,partnerDefaultConfigVO.getMonitoringAlertMail());
                preparedStatement.setString(91,partnerDefaultConfigVO.getMonitoringSuspensionMail());
                preparedStatement.setString(92,partnerDefaultConfigVO.getHighRiskRefunds());
                preparedStatement.setString(93,partnerDefaultConfigVO.getFraudFailedTxn());
                preparedStatement.setString(94,partnerDefaultConfigVO.getDailyFraudReport());
                preparedStatement.setString(95,partnerDefaultConfigVO.getCustomerTransactionSuccessfulMail());
                preparedStatement.setString(96,partnerDefaultConfigVO.getCustomerTransactionFailMail());
                preparedStatement.setString(97,partnerDefaultConfigVO.getCustomerTransactionPayoutSuccess());
                preparedStatement.setString(98,partnerDefaultConfigVO.getCustomerTransactionPayoutFail());
                preparedStatement.setString(99,partnerDefaultConfigVO.getCustomerRefundMail());
                preparedStatement.setString(100,partnerDefaultConfigVO.getCustomerTokenizationMail());
                preparedStatement.setString(101,partnerDefaultConfigVO.getIsUniqueOrderIdRequired());
                //maincontact_bccmailid,cbcontact_bccmailid,refundcontact_bccmailid,salescontact_bccmailid,fraudcontact_bccmailid,technicalcontact_bccmailid,billingcontact_bccmailid,support_bccmailid
                preparedStatement.setString(102,(String)details.get("maincontact_bccmailid"));
                preparedStatement.setString(103,(String)details.get("cbcontact_bccmailid"));
                preparedStatement.setString(104,(String)details.get("refundcontact_bccmailid"));
                preparedStatement.setString(105,(String)details.get("salescontact_bccmailid"));
                preparedStatement.setString(106,(String)details.get("fraudcontact_bccmailid"));
                preparedStatement.setString(107,(String)details.get("technicalcontact_bccmailid"));
                preparedStatement.setString(108,(String)details.get("billingcontact_bccmailid"));
                preparedStatement.setString(109,(String)details.get("support_bccmailid"));
                preparedStatement.setString(110,partnerDefaultConfigVO.getEmailTemplateLang());
                preparedStatement.setString(111,partnerDefaultConfigVO.getSuccessReconMail());
                preparedStatement.setString(112,partnerDefaultConfigVO.getRefundReconMail());
                preparedStatement.setString(113,partnerDefaultConfigVO.getChargebackReconMail());
                preparedStatement.setString(114,partnerDefaultConfigVO.getPayoutReconMail());
                preparedStatement.setString(115,partnerDefaultConfigVO.getIsMerchantLogoBO());
                preparedStatement.setString(116,partnerDefaultConfigVO.getCardExpiryDateCheck());
                preparedStatement.setString(117,partnerDefaultConfigVO.getPayoutNotification());
                preparedStatement.setString(118,partnerDefaultConfigVO.getVpaAddressLimitCheck());
                preparedStatement.setString(119,partnerDefaultConfigVO.getVpaAddressDailyCount());
                preparedStatement.setString(120,partnerDefaultConfigVO.getVpaAddressAmountLimitCheck());
                preparedStatement.setString(121,partnerDefaultConfigVO.getVpaAddressDailyAmountLimit());
                preparedStatement.setString(122,partnerDefaultConfigVO.getPayoutBankAccountNoLimitCheck());
                preparedStatement.setString(123,partnerDefaultConfigVO.getBankAccountNoDailyCount());
                preparedStatement.setString(124,partnerDefaultConfigVO.getPayoutBankAccountNoAmountLimitCheck());
                preparedStatement.setString(125,partnerDefaultConfigVO.getBankAccountNoDailyAmountLimit());
                preparedStatement.setString(126,partnerDefaultConfigVO.getIsDomainWhitelisted());
                preparedStatement.setString(127,partnerDefaultConfigVO.getCustomerIpLimitCheck());
                preparedStatement.setString(128,partnerDefaultConfigVO.getCustomerIpDailyCount());
                preparedStatement.setString(129,partnerDefaultConfigVO.getCustomerIpAmountLimitCheck());
                preparedStatement.setString(130,partnerDefaultConfigVO.getCustomerIpDailyAmountLimit());
                preparedStatement.setString(131,partnerDefaultConfigVO.getCustomerNameLimitCheck());
                preparedStatement.setString(132,partnerDefaultConfigVO.getCustomerNameDailyCount());
                preparedStatement.setString(133,partnerDefaultConfigVO.getCustomerNameAmountLimitCheck());
                preparedStatement.setString(134,partnerDefaultConfigVO.getCustomerNameDailyAmountLimit());
                preparedStatement.setString(135,partnerDefaultConfigVO.getCustomerEmailLimitCheck());
                preparedStatement.setString(136,partnerDefaultConfigVO.getCustomerEmailDailyCount());
                preparedStatement.setString(137,partnerDefaultConfigVO.getCustomerEmailAmountLimitCheck());
                preparedStatement.setString(138,partnerDefaultConfigVO.getCustomerEmailDailyAmountLimit());
                preparedStatement.setString(139,partnerDefaultConfigVO.getCustomerPhoneLimitCheck());
                preparedStatement.setString(140,partnerDefaultConfigVO.getCustomerPhoneDailyCount());
                preparedStatement.setString(141,partnerDefaultConfigVO.getCustomerPhoneAmountLimitCheck());
                preparedStatement.setString(142,partnerDefaultConfigVO.getCustomerPhoneDailyAmountLimit());
                preparedStatement.setString(143,partnerDefaultConfigVO.getInquiryNotification());
                preparedStatement.setString(144,partnerDefaultConfigVO.getPaybylink());
                preparedStatement.setString(145,partnerDefaultConfigVO.getTransMgtPayoutTransaction());
                preparedStatement.setString(146,partnerDefaultConfigVO.getIsOTPRequired());
                preparedStatement.setString(147,partnerDefaultConfigVO.getIsCardStorageRequired());
                preparedStatement.setString(148,partnerDefaultConfigVO.getVpaAddressMonthlyCount());
                preparedStatement.setString(149,partnerDefaultConfigVO.getVpaAddressMonthlyAmountLimit());
                preparedStatement.setString(150,partnerDefaultConfigVO.getCustomerEmailMonthlyCount());
                preparedStatement.setString(151,partnerDefaultConfigVO.getCustomerEmailMonthlyAmountLimit());
                preparedStatement.setString(152,partnerDefaultConfigVO.getCustomerPhoneMonthlyCount());
                preparedStatement.setString(153,partnerDefaultConfigVO.getCustomerPhoneMonthlyAmountLimit());
                preparedStatement.setString(154,partnerDefaultConfigVO.getBankAccountNoMonthlyCount());
                preparedStatement.setString(155,partnerDefaultConfigVO.getBankAccountNoMonthlyAmountLimit());
                preparedStatement.setString(156,partnerDefaultConfigVO.getCustomerIpMonthlyCount());
                preparedStatement.setString(157,partnerDefaultConfigVO.getCustomerIpMonthlyAmountLimit());
                preparedStatement.setString(158,partnerDefaultConfigVO.getCustomerNameMonthlyCount());
                preparedStatement.setString(159,partnerDefaultConfigVO.getCustomerNameMonthlyAmountLimit());

                logger.error("query :"+preparedStatement);

                int k1=preparedStatement.executeUpdate();
                if (k1>0)
                {
                    Merchants.refresh();
                    FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
                }
            }
        }
        catch (SystemError se)
        {
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
    public int updateOrganProfile(Hashtable details, HttpSession session) throws SystemError
    {
        StringBuffer query = new StringBuffer("update members set ");
        query.append("proprietor = ?");
        query.append(", proprietorAddress = ?");
        query.append(", proprietorPhNo = ? ");
        query.append(", OrganisationRegNo = ?");
        query.append(", partnerNameAddress = ? ");
        query.append(", directorsNameAddress = ? ");
        query.append(", pan = ? ");
        query.append(", directors = ? ");
        query.append(", employees = ? ");
        query.append(", potentialbusiness = ? ");
        query.append(", registeredaddress = ? ");
        query.append(", bussinessaddress = ? ");
        query.append(", notifyemail = ? ");
        query.append(", acdetails = ? ");
        query.append(" where memberid = ? ");
        query.append(" and modify_company_details = 'Y'");

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            int result;
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, (String) details.get("proprietor"));
            pstmt.setString(2, (String) details.get("proprietorAddress"));
            pstmt.setString(3, (String) details.get("proprietorPhNo"));
            pstmt.setString(4, (String) details.get("OrganisationRegNo"));
            pstmt.setString(5, (String) details.get("partnerNameAddress"));
            pstmt.setString(6, (String) details.get("directorsNameAddress"));
            pstmt.setString(7, (String) details.get("pan"));
            pstmt.setString(8, (String) details.get("directors"));
            pstmt.setString(9, (String) details.get("employees"));
            pstmt.setString(10, (String) details.get("potentialbusiness"));
            pstmt.setString(11, (String) details.get("registeredaddress"));
            pstmt.setString(12, (String) details.get("bussinessaddress"));
            pstmt.setString(13, (String) details.get("notifyemail"));
            pstmt.setString(14, (String) details.get("acdetails"));
            pstmt.setObject(15, session.getAttribute("merchantid"));
            result = pstmt.executeUpdate();

            String selquery = "update members set modify_company_details = 'N' where memberid = ? ";

            PreparedStatement ps = conn.prepareStatement(selquery);
            ps.setObject(1, session.getAttribute("merchantid"));
            result = ps.executeUpdate();
            return result;
        }
        catch (SystemError se)
        {
            logger.error("System error occure", se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public int isAuthorised(HttpSession session) throws SystemError
    {
        StringBuffer query = new StringBuffer("select modify_company_details from members");
        query.append(" where memberid = ?");
        query.append(" and modify_company_details = 'Y'");

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement p1 = conn.prepareStatement(query.toString());
            p1.setObject(1, session.getAttribute("merchantid"));
            ResultSet rs = p1.executeQuery();
            if (rs.next())
                return 1;
            else
                return 0;
        }
        catch (SystemError se)
        {
            logger.error("System Error", se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public String isPoweredBy(String merchantid)
    {
        String isPoweredBy="Y";
        Connection connection =null;
        String query2="select isPoweredBy from members where memberid= ?";
        try{
            connection= Database.getConnection();
            PreparedStatement pstmnt = connection.prepareStatement(query2);
            pstmnt.setString(1,merchantid);
            ResultSet x=pstmnt.executeQuery();
            if(x.next())
            {
                isPoweredBy=x.getString("isPoweredBy");
            }
        }
        catch(Exception e)
        {
            logger.error("Exception Occured",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return isPoweredBy;
    }

    public boolean isMerchantMemberMapped(String memberid, String partnerid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String mappedPartnerId = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "SELECT partnerid FROM members WHERE memberid = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, memberid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                mappedPartnerId = rs.getString("partnerid");
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        if (functions.isValueNull(mappedPartnerId) && mappedPartnerId.equals(partnerid))
        {
            return true;
        }
        return false;
    }


    public boolean ifTCRequired(String memberId)
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from members where memberid="+memberId + " and isPharma='Y'", con);
            return rs.next();
        }
        catch (Exception ex)
        {
            logger.error("exception",ex);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return false;
    }
    public Hashtable getMemberDetailsForTransaction(String toid)
    {
        Connection con =null;
        PreparedStatement p=null;
        ResultSet rs=null;
        Hashtable detailhash = new Hashtable();
        try
        {
            //con = Database.getConnection();
            con=Database.getRDBConnection();
            String query = "SELECT p.responseLength,p.responseType,p.partnerName,p.logoName,m.icici,m.check_limit,m.activation,m.hralertproof,m.datamismatchproof,m.isservice,m.notifyemail,m.aptprompt,m.company_name,m.brandname,m.sitename,m.chargeper,m.fixamount,m.currency,m.hrparameterised,m.taxper,m.checksumalgo,m.vbv,m.card_transaction_limit,m.card_check_limit,m.clkey,m.autoredirect,m.autoSelectTerminal,m.isIpWhitelisted,m.partnerid,m.isExcessCaptureAllowed,m.memberid,m.refundallowed_days FROM members AS m, partners AS p WHERE m.memberid =? AND m.partnerId = p.partnerId";
            p = con.prepareStatement(query);
            p.setString(1, toid);
            rs = p.executeQuery();
            logger.debug("Query : " + query);
            if (rs.next())
            {
                detailhash.put("responseLength",rs.getString("responseLength"));
                detailhash.put("responseType",rs.getString("responseType"));
                detailhash.put("partnerName",rs.getString("partnerName"));
                detailhash.put("logoName",rs.getString("logoName"));
                detailhash.put("icici", rs.getString("icici"));
                detailhash.put("check_limit", rs.getBoolean("check_limit"));
                detailhash.put("activation", rs.getString("activation"));
                detailhash.put("hralertproof",rs.getString("hralertproof"));
                detailhash.put("datamismatchproof",rs.getString("datamismatchproof"));
                detailhash.put("isservice",rs.getString("isservice"));
                if (rs.getString("notifyemail")==null)
                    detailhash.put("notifyemail","");
                else
                    detailhash.put("notifyemail",rs.getString("notifyemail"));
                detailhash.put("aptprompt",String.valueOf(rs.getInt("aptprompt")))  ;
                detailhash.put("company_name",rs.getString("company_name"));
                if(rs.getString("brandname")==null)
                    detailhash.put("brandname","");
                else
                    detailhash.put("brandname",rs.getString("brandname"));
                detailhash.put("sitename",rs.getString("sitename")) ;
                if(rs.getString("chargeper")==null)
                    detailhash.put("chargeper","");
                else
                    detailhash.put("chargeper",rs.getString("chargeper"));
                if(rs.getString("fixamount")==null)
                    detailhash.put("fixamount","");
                else
                    detailhash.put("fixamount",rs.getString("fixamount"));
                /*detailhash.put("custremindermail",rs.getBoolean("custremindermail"));*/
                detailhash.put("hrparameterised",rs.getBoolean("hrparameterised"));
                if(rs.getString("taxper")==null)
                    detailhash.put("taxper","");
                else
                    detailhash.put("taxper",rs.getString("taxper"));
                detailhash.put("checksumalgo",rs.getString("checksumalgo"));
                detailhash.put("vbv",rs.getBoolean("vbv"));
                detailhash.put("card_transaction_limit",rs.getBoolean("card_transaction_limit"));
                detailhash.put("card_check_limit",rs.getBoolean("card_check_limit"));
                detailhash.put("clkey",rs.getString("clkey"));
                detailhash.put("autoredirect", rs.getBoolean("autoredirect"));
                detailhash.put("autoSelectTerminal", rs.getString("autoSelectTerminal"));
                detailhash.put("isIpWhitelisted",rs.getString("isIpWhitelisted"));
                detailhash.put("partnerId",rs.getString("partnerid"));
                detailhash.put("isExcessCaptureAllowed",rs.getString("isExcessCaptureAllowed"));
                detailhash.put("memberid",rs.getString("memberid"));
                detailhash.put("refundallowed_days", rs.getString("refundallowed_days"));
            }
        }
        catch (SQLException s)
        {
            logger.error("Sqlerror", s);
            PZExceptionHandler.raiseAndHandleDBViolationException("Merchants.java","getMemberDetailsForTransaction()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,s.getMessage(),s.getCause(),toid,null);
        }
        catch (SystemError e)
        {
            logger.error("Systemerror", e);
            PZExceptionHandler.raiseAndHandleGenericViolationException("Merchants.java","getMemberDetailsForTransaction()",null,"common","System Error Exception Thrown:::",null,e.getMessage(),e.getCause(),toid,null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return detailhash;
    }
    public Hashtable getMemberPartnerDetailsForTransaction(String toid)
    {
        Connection con =null;
        PreparedStatement p= null;
        ResultSet rs=null;
        Hashtable detailhash = new Hashtable();
        try
        {
            con = Database.getConnection();
            String query = "select m.clkey,checksumalgo,m.autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?" ;
            p=con.prepareStatement(query);

            p.setString(1,toid);
            rs=p.executeQuery();
            if (rs.next())
            {
                detailhash.put("clkey",rs.getString("clkey"));
                detailhash.put("checksumAlgo",rs.getString("checksumalgo"));
                detailhash.put("autoredirect",rs.getString("autoredirect"));
                detailhash.put("isPowerBy",rs.getString("isPoweredBy"));
                detailhash.put("logoName",rs.getString("logoName"));
                detailhash.put("partnerName",rs.getString("partnerName"));
                detailhash.put("partnerId",rs.getString("partnerId"));
            }
        }
        catch (SQLException s)
        {
            logger.error("Sqlerror", s);
            PZExceptionHandler.raiseAndHandleDBViolationException("Merchants.java","getMemberDetailsForTransaction()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,s.getMessage(),s.getCause(),toid,null);
        }
        catch (SystemError e)
        {
            logger.error("Systemerror", e);
            PZExceptionHandler.raiseAndHandleGenericViolationException("Merchants.java","getMemberDetailsForTransaction()",null,"common","System Error Exception Thrown:::",null,e.getMessage(),e.getCause(),toid,null);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(con);
        }
        return detailhash;
    }
    public HashMap<String, MerchantTerminalVo> getMappingAccount(String memberid,String role)
    {
        HashMap<String, MerchantTerminalVo> accountList=new LinkedHashMap();

        Connection connection=null;
        try
        {
            connection=Database.getConnection();
            String selectStatement = "";
            if(role.equalsIgnoreCase("submerchant"))
            {
                selectStatement = "SELECT gt.currency,mu.accountid,mu.paymodeid,mu.cardtypeid,mu.terminalid,m.is_recurring,m.addressDetails,m.addressValidation,m.isActive FROM member_user_account_mapping AS mu,member_account_mapping AS m,gateway_accounts AS ga,gateway_type AS gt WHERE mu.userid=? AND mu.terminalid=m.terminalid AND m.`accountid`=ga.accountid AND ga.pgtypeid=gt.`pgtypeid` ORDER BY m.isActive";
            }
            else
            {
                selectStatement = "SELECT gt.currency,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.is_recurring,mam.addressDetails,mam.addressValidation,mam.isActive FROM member_account_mapping AS mam,gateway_accounts AS ga,gateway_type AS gt WHERE mam.memberid=? AND mam.`accountid`=ga.accountid AND ga.pgtypeid=gt.`pgtypeid` ORDER BY mam.isActive";
            }
            PreparedStatement p=connection.prepareStatement(selectStatement);
            p.setString(1,memberid);
            ResultSet rs=p.executeQuery();
            logger.debug("terminal query---"+p);
            while (rs.next())
            {
                MerchantTerminalVo merchantTerminalVo=new MerchantTerminalVo();
                merchantTerminalVo.setTerminalId(rs.getString("terminalid"));
                merchantTerminalVo.setCurrency(rs.getString("currency"));
                merchantTerminalVo.setAccountId(rs.getString("accountid"));
                merchantTerminalVo.setCardTypeName(getCardtype(rs.getString("cardtypeid")));
                merchantTerminalVo.setPaymodeName(getPaymode(rs.getString("paymodeid")));
                merchantTerminalVo.setCardType(rs.getString("cardtypeid"));
                merchantTerminalVo.setPaymodeId(rs.getString("paymodeid"));
                merchantTerminalVo.setIsRecurring(rs.getString("is_recurring"));
                merchantTerminalVo.setAddressDetails(rs.getString("addressDetails"));
                merchantTerminalVo.setAddressValidation(rs.getString("addressValidation"));
                merchantTerminalVo.setIsActive(rs.getString("isActive"));
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(rs.getString("accountid")));
                String fileName = paymentProcess.getSpecificVirtualTerminalJSP();

                if(fileName!=null)
                {
                    merchantTerminalVo.setFileName(fileName);
                }
                else
                {
                    merchantTerminalVo.setFileName("");
                }
                if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid") || (PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid")) && (CardTypeEnum.VISA_CARDTYPE.ordinal() == rs.getInt("cardtypeid") || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == rs.getInt("cardtypeid")) || CardTypeEnum.AMEX_CARDTYPE.ordinal() == rs.getInt("cardtypeid") || CardTypeEnum.DINER_CARDTYPE.ordinal() == rs.getInt("cardtypeid") || CardTypeEnum.JCB.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("ccpaymentPage.jsp");

                    GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(merchantTerminalVo.getAccountId());

                    if(gatewayAccount!=null && functions.isValueNull(gatewayAccount.getPgTypeId()))
                    {
                        if(PbsPaymentGateway.GATEWAY_TYPE.equals(gatewayAccount.getGateway()))
                        {
                            merchantTerminalVo.setcCPaymentFileName("pbsccPage.jsp");
                        }
                        if(AcqraPaymentGateway.GATEWAY_TYPE.equals(gatewayAccount.getGateway()))
                        {
                            merchantTerminalVo.setcCPaymentFileName("ccAcqraPaymentPage.jsp");
                        }
                    }
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.INPAY_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("inpaySpecificfields.jsp");
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.SOFORT_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("sofortSpecificfields.jsp");
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.PAYSEC_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("inpaySpecificfields.jsp");
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.DIRECT_DEBIT.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("p4Payment.jsp");
                }
                if(PaymentModeEnum.SEPA.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.DIRECT_DEBIT.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("p4SepaPayment.jsp");
                }
                if(PaymentModeEnum.ACH.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.ACH.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("ccPayMitcoPage.jsp");
                }
                if(PaymentModeEnum.CHK.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.CHK.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("ccPayMitcoPage.jsp");
                }
                else if(PaymentModeEnum.VOUCHERS_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.PAYSAFECARD_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("paysafespecificfields.jsp");
                }
                else if(PaymentModeEnum.SEPA.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.SEPA_EXPRESS.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("b4SepaExpress.jsp");
                }
                else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.NEOSURF.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.GIROPAY.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.ASTROPAY.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.POSTPAID_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.MULTIBANCO.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.JETON_VOUCHER.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("jetonVoucher.jsp");
                }
                else if(PaymentModeEnum.WALLET_PAYMODE.ordinal() == rs.getInt("paymodeid") && CardTypeEnum.NETELLER.ordinal() == rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("netellerCreditPage.jsp");
                }
                accountList.put(rs.getString("terminalid"),merchantTerminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
        }
        finally {
            Database.closeConnection(connection);
        }
        return accountList;
    }
    public String getCardtype(String id)
    {
        String cardName="";
        Connection connection=null;
        try
        {
            connection= Database.getConnection();
            String selectCard="SELECT cardType FROM card_type WHERE cardtypeid=?";
            PreparedStatement pstmt=connection.prepareStatement(selectCard);
            pstmt.setString(1,id);
            ResultSet resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                cardName=resultSet.getString("cardType");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting card type for Merchant",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception while getting card type for Merchant", e);
        }
        finally {
            Database.closeConnection(connection);
        }
        return cardName;
    }
    public String getPaymode(String id)
    {
        String payMode="";
        Connection connection=null;
        try
        {
            connection= Database.getConnection();
            String selectCard="SELECT paymode FROM payment_type WHERE paymodeid=?";
            PreparedStatement pstmt=connection.prepareStatement(selectCard);
            pstmt.setString(1,id);
            ResultSet resultSet=pstmt.executeQuery();
            if(resultSet.next())
            {
                payMode=resultSet.getString("paymode");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getting card type for Merchant",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL exception while getting card type for Merchant",e);
        }
        finally {
            Database.closeConnection(connection);
        }
        return payMode;
    }
    public boolean isAuthenticMember(String toid,String totype)
    {
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            transactionLogger.debug("Merchant.isAuthenticMember getting partner details:::DB Call:::");
            String query1 = "SELECT COUNT(*) AS cntPartner, logoName FROM partners WHERE partnerName=? AND partnerId=(SELECT partnerId FROM members WHERE memberid=?) GROUP BY logoName";
            PreparedStatement pstmt1 = conn.prepareStatement(query1);
            pstmt1.setString(1, totype);
            pstmt1.setString(2, toid);
            ResultSet rs1 = pstmt1.executeQuery();
            logger.error(toid+"     "+totype+"    "+query1);
            if(rs1.next())
            {
                if (rs1.getInt("cntPartner") > 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error:",systemError);
            return false;
        }
        catch (SQLException e)
        {
            logger.error("SQLexception : ",e);
            return false;
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public CommonValidatorVO Getaddressdetails(CommonValidatorVO commonValidatorVO)
    {
        DirectKitResponseVO directKitResponseVO = null;
        MerchantDetailsVO merchantDetailsVO = null;
        String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String query = "SELECT address,city,state,zip,country FROM members WHERE memberid=?";
            PreparedStatement p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, toid);
            ResultSet rs=p.executeQuery();
            logger.debug("query-----" + p);
            if (rs.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setAddress(rs.getString("address"));
                merchantDetailsVO.setCity(rs.getString("city"));
                merchantDetailsVO.setState(rs.getString("state"));
                merchantDetailsVO.setZip(rs.getString("zip"));
                merchantDetailsVO.setCountry(rs.getString("country"));
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return commonValidatorVO;
    }
    public boolean updateAddressDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        Functions functions = new Functions();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        boolean status = false;
        try
        {
            connection = Database.getConnection();
            StringBuilder query = new StringBuilder("update members set");
            if (functions.isValueNull(addressDetailsVO.getCity()))
            {
                query.append(" city = '"+addressDetailsVO.getCity()+"'");
            }
            if (functions.isValueNull(addressDetailsVO.getState()))
            {
                query.append(" ,state = '"+addressDetailsVO.getState()+"'");
            }
            if (functions.isValueNull(addressDetailsVO.getStreet()))
            {
                query.append(" ,address = '"+addressDetailsVO.getStreet()+"'");
            }
            if (functions.isValueNull(addressDetailsVO.getZipCode()))
            {
                query.append(" ,zip = '"+addressDetailsVO.getZipCode()+"'");
            }
            query.append(" where memberid = " + merchantDetailsVO.getMemberId());
            PreparedStatement preparedStatement =  connection.prepareStatement(query.toString());
            int i = preparedStatement.executeUpdate();
            if (i>0)
            {
                status = true;
            }
            logger.debug("update query----"+preparedStatement);
        }
        catch (SQLException e)
        {
            logger.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return status;
    }
    public  void DeleteBoth(String login) throws SystemError
    {
        Connection con=null;
        try
        {
            con=Database.getConnection();
            String query="delete from `user` where login=? and roles IN ('merchant','submerchant')";
            PreparedStatement ps=con.prepareStatement(query.toString());
            ps.setString(1,login);
            String dquery=" delete from `members` where login=?";
            PreparedStatement ps1=con.prepareStatement(dquery.toString());
            ps1.setString(1,login);
            String uquery="delete from `member_users` where login=?";
            PreparedStatement ps2=con.prepareStatement(uquery.toString());
            ps2.setString(1,login);
        }
        catch (SystemError systemError)
        {
            logger.error(" error ::", systemError);
            throw new SystemError("Error:"+systemError.getMessage());
        }
        catch (SQLException e)
        {
            logger.error(" ERROR", e);
            throw new SystemError("Error:"+e.getMessage());
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public Hashtable getPartnerDetails(String toid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection connection=null;
        try
        {
            connection=Database.getConnection();
            String qry1="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)";
            PreparedStatement preparedStatement1= connection.prepareStatement(qry1);
            preparedStatement1.setString(1,toid);
            partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }

        return partnerMailDetail;
    }
    public void updateFlag(int memberid)
    {
        String query = "UPDATE members SET icici='Y' WHERE memberid=?";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, memberid);
            ps.executeUpdate();
        }
        catch (Exception e)
        {
            transactionLogger.error("Error occured while updating flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public void updateFlags (String partnerid)
    {

        String query = "UPDATE members SET isappmanageractivate='Y',icici='Y' WHERE partnerid IN("+partnerid+")";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.executeUpdate();

        }
        catch (Exception e)
        {
            logger.error("Error occured while updating flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public void insertetoken(int memberid,String emailtoken)
    {
        //System.out.println("inside wtoken query");
        emailtoken= ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
        String query = "UPDATE members SET emailtoken=? WHERE memberid=?";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, emailtoken);
            ps.setInt(2,memberid);

            ps.executeUpdate();
            //System.out.println("etoken query----"+ps);

        }
        catch (Exception e)
        {
            transactionLogger.error("Error occured while updating flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public MerchantModuleAccessVO getMerchantModulesAccessDetail(String memberId){
        MerchantModuleAccessVO merchantModuleAccessVO=new MerchantModuleAccessVO();
        Connection conn=null;
        ResultSet  rs=null;
        try
        {
            conn=Database.getConnection();
            String query="SELECT dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,merchantmgt_access,isappmanageractivate,iscardregistrationallowed,is_recurring,settings_merchant_config_access,settings_fraudrule_config_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access, settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,merchantmgt_user_management_access,settings_whitelist_details,settings_blacklist_details,emi_configuration,rejected_transaction,virtual_checkout,paybylink,transmgt_payout_transactions,checkout_config,generateview FROM merchant_configuration as mc join members as m on mc.memberid=m.memberid WHERE mc.memberid=?";
            PreparedStatement preparedStatement1= conn.prepareStatement(query);
            preparedStatement1.setString(1,memberId);
            rs =preparedStatement1.executeQuery();
            if(rs.next()){
                merchantModuleAccessVO.setDashboardAccess(rs.getString("dashboard_access"));
                merchantModuleAccessVO.setAccountingAccess(rs.getString("accounting_access"));
                merchantModuleAccessVO.setSettingAccess(rs.getString("setting_access"));
                merchantModuleAccessVO.setTransactionsAccess(rs.getString("transactions_access"));
                merchantModuleAccessVO.setInvoicingAccess(rs.getString("invoicing_access"));
                merchantModuleAccessVO.setVirtualTerminalAccess(rs.getString("virtualterminal_access"));
                merchantModuleAccessVO.setMerchantMgtAccess(rs.getString("merchantmgt_access"));
                merchantModuleAccessVO.setApplicationManagerAccess(rs.getString("isappmanageractivate"));
                merchantModuleAccessVO.setTokenizationAccess(rs.getString("iscardregistrationallowed"));
                merchantModuleAccessVO.setRecurringAccess(rs.getString("is_recurring"));
                merchantModuleAccessVO.setSettingsMerchantConfigAccess(rs.getString("settings_merchant_config_access"));
                merchantModuleAccessVO.setSettingsFraudRuleConfigAccess(rs.getString("settings_fraudrule_config_access"));
                merchantModuleAccessVO.setAccountsAccountSummaryAccess(rs.getString("accounts_account_summary_access"));
                merchantModuleAccessVO.setAccountsChargesSummaryAccess(rs.getString("accounts_charges_summary_access"));
                merchantModuleAccessVO.setAccountsTransactionSummaryAccess(rs.getString("accounts_transaction_summary_access"));
                merchantModuleAccessVO.setAccountsReportsSummaryAccess(rs.getString("accounts_reports_summary_access"));
                merchantModuleAccessVO.setSettingsMerchantProfileAccess(rs.getString("settings_merchant_profile_access"));
                merchantModuleAccessVO.setSettingsOrganisationProfileAccess(rs.getString("settings_organisation_profile_access"));
                merchantModuleAccessVO.setSettingsCheckoutPageAccess(rs.getString("settings_checkout_page_access"));
                merchantModuleAccessVO.setSettingsGenerateKeyAccess(rs.getString("settings_generate_key_access"));
                merchantModuleAccessVO.setSettingsInvoiceConfigAccess(rs.getString("settings_invoice_config_access"));
                merchantModuleAccessVO.setTransMgtTransactionAccess(rs.getString("transmgt_transaction_access"));
                merchantModuleAccessVO.setTransMgtCaptureAccess(rs.getString("transmgt_capture_access"));
                merchantModuleAccessVO.setTransMgtReversalAccess(rs.getString("transmgt_reversal_access"));
                merchantModuleAccessVO.setTransMgtPayoutAccess(rs.getString("transmgt_payout_access"));
                merchantModuleAccessVO.setInvoiceGenerateAccess(rs.getString("invoice_generate_access"));
                merchantModuleAccessVO.setInvoiceHistoryAccess(rs.getString("invoice_history_access"));
                merchantModuleAccessVO.setTokenMgtRegistrationHistoryAccess(rs.getString("tokenmgt_registration_history_access"));
                merchantModuleAccessVO.setTokenMgtRegisterCardAccess(rs.getString("tokenmgt_register_card_access"));
                merchantModuleAccessVO.setMerchantMgtUserManagementAccess(rs.getString("merchantmgt_user_management_access"));
                merchantModuleAccessVO.setSettingWhiteListDetails(rs.getString("settings_whitelist_details"));
                merchantModuleAccessVO.setSettingBlacklistDetails(rs.getString("settings_blacklist_details"));
                merchantModuleAccessVO.setEmiConfiguration(rs.getString("emi_configuration"));
                merchantModuleAccessVO.setRejectedTransaction(rs.getString("rejected_transaction"));
                merchantModuleAccessVO.setVirtualCheckOut(rs.getString("virtual_checkout"));
                merchantModuleAccessVO.setPayByLink(rs.getString("paybylink"));
                merchantModuleAccessVO.setTransMgtPayoutTransaction(rs.getString("transmgt_payout_transactions"));
                merchantModuleAccessVO.setMerchantCheckoutConfig(rs.getString("checkout_config"));
                merchantModuleAccessVO.setSettingsGenerateviewaccess(rs.getString("generateview"));
            }
        }
        catch (SystemError systemError){
            logger.error("SystemError",systemError);
        }
        catch (SQLException e){
            logger.error("SQLException", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return merchantModuleAccessVO;
    }


    public String getMemberIcici(String login)
    {
        System.out.println("getMemberIcicigggggg-------------------------");
        Connection conn = null;
        String icici = "";
        try
        {
            conn = Database.getConnection();
            logger.debug("check getMemberIcici method");
            String selquery = "select memberid,icici,fdtstamp from members where login=? ";

            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            logger.debug("getMemberIcici query::::"+pstmt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                icici = rs.getString("icici");
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return icici;
    }

    public String getFdtstamp(String login, String role)
    {
        Connection conn = null;
        String fdtstamp = "";
        try
        {
            conn = Database.getConnection();
            logger.debug("check getFdtstamp method");
            String selquery = "";
            if (role.equals("submerchant"))
            {
                selquery = "select memberid,fdtstamp from member_users where login=?";
            }
            else
            {
                selquery = "select memberid,fdtstamp from members where login=?";
            }

            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            logger.debug("getFdtstamp query::::"+pstmt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                fdtstamp = rs.getString("fdtstamp");
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return fdtstamp;
    }

    public String getUserid(String login)
    {
        String userId =null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String userQuery = "SELECT mu.userid FROM  member_users AS mu WHERE mu.login = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(userQuery);
            pstmt1.setString(1, login);
            ResultSet rs = pstmt1.executeQuery();
            if (rs.next())
            {
                userId = rs.getString("userid");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError---", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return userId;
    }
    public boolean updateAuthtoken(String username,String role, String authToken)
    {
        String query = "";
        boolean isUpdate=false;
        if("submerchant".equalsIgnoreCase(role))
            query="UPDATE member_users SET authToken=? WHERE login=?";
        else
            query="UPDATE members SET authToken=? WHERE login=?";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, authToken);
            ps.setString(2, username);
            logger.error("updateAuthtoken--->"+ps);
            int n=ps.executeUpdate();
            if(n>0)
                isUpdate=true;
        }
        catch (Exception e)
        {
            transactionLogger.error("Error occured while updating flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isUpdate;
    }
    public String getAuthTokenFromMember(String username,String role)
    {
        //System.out.println("inside wtoken query");
        String query ="";
        if("submerchant".equalsIgnoreCase(role))
         query = "SELECT authToken FROM member_users WHERE login=?";
        else
         query = "SELECT authToken FROM members WHERE login=?";
        Connection conn = null;
        ResultSet rs=null;
        String authToken="";
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,username);
            
            rs=ps.executeQuery();
            if(rs.next())
            {
                authToken=rs.getString("authToken");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Error occured while updating flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return authToken;
    }
    public HashMap<String, MerchantTerminalVo> getPaybyLinkTerminals(String memberid,String role)
    {
        HashMap<String, MerchantTerminalVo> accountList=new LinkedHashMap();

        Connection connection=null;
        try
        {
            connection=Database.getConnection();
            String selectStatement = "";
            if(role.equalsIgnoreCase("submerchant"))
            {
                selectStatement = "SELECT gt.currency,mu.accountid,mu.paymodeid,mu.cardtypeid,mu.terminalid,m.is_recurring,m.addressDetails,m.addressValidation,m.isActive FROM member_user_account_mapping AS mu,member_account_mapping AS m,gateway_accounts AS ga,gateway_type AS gt WHERE mu.userid=? AND mu.cardtypeid=129 AND mu.terminalid=m.terminalid AND m.`accountid`=ga.accountid AND ga.pgtypeid=gt.`pgtypeid` ORDER BY m.isActive";
            }
            else
            {
                selectStatement = "SELECT gt.currency,mam.accountid,mam.paymodeid,mam.cardtypeid,mam.terminalid,mam.is_recurring,mam.addressDetails,mam.addressValidation,mam.isActive FROM member_account_mapping AS mam,gateway_accounts AS ga,gateway_type AS gt WHERE mam.memberid=? AND mam.cardtypeid=129 AND mam.`accountid`=ga.accountid AND ga.pgtypeid=gt.`pgtypeid` ORDER BY mam.isActive";
            }
            PreparedStatement p=connection.prepareStatement(selectStatement);
            p.setString(1,memberid);
            ResultSet rs=p.executeQuery();
            logger.debug("terminal query---"+p);
            while (rs.next())
            {
                MerchantTerminalVo merchantTerminalVo=new MerchantTerminalVo();
                merchantTerminalVo.setTerminalId(rs.getString("terminalid"));
                merchantTerminalVo.setCurrency(rs.getString("currency"));
                merchantTerminalVo.setAccountId(rs.getString("accountid"));
                merchantTerminalVo.setCardTypeName(getCardtype(rs.getString("cardtypeid")));
                merchantTerminalVo.setPaymodeName(getPaymode(rs.getString("paymodeid")));
                merchantTerminalVo.setCardType(rs.getString("cardtypeid"));
                merchantTerminalVo.setPaymodeId(rs.getString("paymodeid"));
                merchantTerminalVo.setIsRecurring(rs.getString("is_recurring"));
                merchantTerminalVo.setAddressDetails(rs.getString("addressDetails"));
                merchantTerminalVo.setAddressValidation(rs.getString("addressValidation"));
                merchantTerminalVo.setIsActive(rs.getString("isActive"));
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(rs.getString("accountid")));
                String fileName = paymentProcess.getSpecificVirtualTerminalJSP();

                if(fileName!=null)
                {
                    merchantTerminalVo.setFileName(fileName);
                }
                else
                {
                    merchantTerminalVo.setFileName("");
                }
                if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid") || (PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid")) && (CardTypeEnum.VISA_CARDTYPE.ordinal() == rs.getInt("cardtypeid") || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == rs.getInt("cardtypeid")) || CardTypeEnum.AMEX_CARDTYPE.ordinal() == rs.getInt("cardtypeid") || CardTypeEnum.DINER_CARDTYPE.ordinal() == rs.getInt("cardtypeid") || CardTypeEnum.JCB.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("ccpaymentPage.jsp");

                    GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(merchantTerminalVo.getAccountId());

                    if(gatewayAccount!=null && functions.isValueNull(gatewayAccount.getPgTypeId()))
                    {
                        if(PbsPaymentGateway.GATEWAY_TYPE.equals(gatewayAccount.getGateway()))
                        {
                            merchantTerminalVo.setcCPaymentFileName("pbsccPage.jsp");
                        }
                        if(AcqraPaymentGateway.GATEWAY_TYPE.equals(gatewayAccount.getGateway()))
                        {
                            merchantTerminalVo.setcCPaymentFileName("ccAcqraPaymentPage.jsp");
                        }
                    }
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.INPAY_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("inpaySpecificfields.jsp");
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.SOFORT_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("sofortSpecificfields.jsp");
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.PAYSEC_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("inpaySpecificfields.jsp");
                }
                if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.DIRECT_DEBIT.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("p4Payment.jsp");
                }
                if(PaymentModeEnum.SEPA.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.DIRECT_DEBIT.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("p4SepaPayment.jsp");
                }
                if(PaymentModeEnum.ACH.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.ACH.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("ccPayMitcoPage.jsp");
                }
                if(PaymentModeEnum.CHK.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.CHK.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("ccPayMitcoPage.jsp");
                }
                else if(PaymentModeEnum.VOUCHERS_PAYMODE.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.PAYSAFECARD_CARDTYPE.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("paysafespecificfields.jsp");
                }
                else if(PaymentModeEnum.SEPA.ordinal()==rs.getInt("paymodeid") && CardTypeEnum.SEPA_EXPRESS.ordinal()==rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("b4SepaExpress.jsp");
                }
                else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.NEOSURF.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.GIROPAY.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.ASTROPAY.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.POSTPAID_CARD_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.MULTIBANCO.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("apcopayCreditpage.jsp");
                }
                else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == rs.getInt("paymodeid") && (CardTypeEnum.JETON_VOUCHER.ordinal() == rs.getInt("cardtypeid")))
                {
                    merchantTerminalVo.setcCPaymentFileName("jetonVoucher.jsp");
                }
                else if(PaymentModeEnum.WALLET_PAYMODE.ordinal() == rs.getInt("paymodeid") && CardTypeEnum.NETELLER.ordinal() == rs.getInt("cardtypeid"))
                {
                    merchantTerminalVo.setcCPaymentFileName("netellerCreditPage.jsp");
                }
                accountList.put(rs.getString("terminalid"),merchantTerminalVo);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while collect terminalid",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while collect terminalid",e);
        }
        finally {
            Database.closeConnection(connection);
        }
        return accountList;
    }

    public MerchantDetailsVO getMemberDetilas(String memberId)
    {
        Connection connection               = null;
        MerchantDetailsVO merchantDetailsVO = null;
        try
        {
            connection          = Database.getConnection();
            String query        = "SELECT memberid,partnerId FROM members WHERE memberid=?";
            PreparedStatement p = connection.prepareStatement(String.valueOf(query));
            p.setString(1, memberId);
            ResultSet rs    = p.executeQuery();
            logger.error("getMemberDetilas----"+ p);
            if (rs.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVO.setPartnerId(rs.getString("partnerId"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return merchantDetailsVO;
    }

    public HashMap<String, String> geMemberTemplateDetails(String memberId)
    {
        Connection con          = null;
        ResultSet resultSet     = null;
        PreparedStatement pstmt = null;
        HashMap<String, String> merchantDetailsVOMap = new HashMap<String, String>();
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from template_preferences where memberid= ? and value != 'Y'  ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, memberId);
            resultSet       = pstmt.executeQuery();
            while (resultSet.next())
            {
                if (TemplatePreference.getEnum(resultSet.getString("name")) != null && !TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    transactionLogger.debug("inside First Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getString("value"));
                }
                else if (TemplatePreference.getEnum(resultSet.getString("name")) != null && TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    transactionLogger.debug("inside 2nd Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getBytes("value")+"");
                }else{
                    merchantDetailsVOMap.put(resultSet.getString("name"),resultSet.getString("value"));
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----", e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----", e);
        }
        finally
        {   Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOMap;
    }

    public HashMap<String, String> getMemberPartnerTemplateDetails(String partnerId) throws PZDBViolationException
    {
        Connection con          = null;
        ResultSet resultSet     = null;
        PreparedStatement pstmt = null;

        HashMap<String, String> partnerDetailsVOMap = new HashMap<String, String>();
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from partner_template_preference where partnerid=? ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, partnerId);
            resultSet       = pstmt.executeQuery();
            while (resultSet.next())
            {

                if (PartnerTemplatePreference.getEnum(resultSet.getString("name")) != null && !PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.name().equals(PartnerTemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    logger.debug("inside First Condition:::" + resultSet.getString("name") + " enum" + PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString());
                    partnerDetailsVOMap.put(PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getString("value"));
                }
                else if (PartnerTemplatePreference.getEnum(resultSet.getString("name")) != null && PartnerTemplatePreference.ATRANSACTIONPAGEMERCHANTLOGO.name().equals(PartnerTemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    logger.debug("inside 2nd Condition:::" + resultSet.getString("name") + " enum" + PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString());
                    partnerDetailsVOMap.put(PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getBytes("value")+"");
                }else{
                    partnerDetailsVOMap.put(resultSet.getString("name"),resultSet.getString("value"));
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", systemError);
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return partnerDetailsVOMap;
    }
    public boolean resetpasswordforrole(String login, String memberId) throws SQLException, SystemError
    {
        Connection con          = null;
        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        boolean b               = false;
        try
        {
            con             = Database.getRDBConnection();
            StringBuffer sb = new StringBuffer("select memberid,userid,fpasswd from member_users where login=? and memberid=?");
            pstmt           = con.prepareStatement(sb.toString());
            pstmt.setString(1, login);
            pstmt.setString(2, memberId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                b = true;
            }
        }
        finally
        {   Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }
    public String resetpassword(String login,User user,String remoteAddr,String Header,String actionExecuterId,String role,String memberid) throws SystemError
    {
        String flag = "false";
        String Value ="";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        if ((isMember(login) || isMemberUser(login)))
        {
            System.out.println("-------------- inside if merchants");
            logger.error("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            System.out.println("fpasswd12--------------------"+fpasswd);
            logger.error("Username::::" + login + "---Temporary Pasword :::::::::" + fpasswd);
            System.out.println("Username::::" + login + "---Temporary Pasword :::::::::" + fpasswd);
            String hashPassword = null;
            try
            {
                hashPassword = ESAPI.authenticator().hashPassword(fpasswd,login) ;
                System.out.println("hashPassword----------------------"+hashPassword);
            }
            catch(Exception e)
            {
                throw new SystemError("Error getting hashpassword : " + e.getMessage());
            }
            try
            {
                ESAPI.authenticator().changePassword(user, null, fpasswd, fpasswd);
                System.out.println("changepassword--------------------------------");
            }
            catch(Exception e)
            {
                logger.error("Change password throwing Authetication Exception ",e);
                return "false";
            }
            /*String role = MERCHANT;
            if(user==null)
            {
                role = getUserRole(user);
                System.out.println("role123------------"+getUserRole(user));

            }*/
            System.out.println("role-----------------"+role);
            logger.debug("user table updated with temporary password");
            System.out.println("user table updated with temporary password");
            Connection conn = null;
            try
            {
                conn = Database.getConnection();

                String tableName = "members";

                if(SUBMERCHANT.equalsIgnoreCase(role))
                {
                    tableName = "member_users";
                }
                String query = "update "+tableName+" set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and accid=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1,hashPassword);
                pstmt.setString(2,login);
                pstmt.setLong(3, user.getAccountId());
                logger.error("QUERY::: "+pstmt);
                int success = pstmt.executeUpdate();
                if (success > 0)
                {

                        flag="true";
                        Value= fpasswd;
                        activityTrackerVOs.setInterface(ActivityLogParameters.MERCHANT.toString());
                        activityTrackerVOs.setUser_name(login + "-" + memberid);
                        activityTrackerVOs.setRole(role);
                        activityTrackerVOs.setAction(ActivityLogParameters.FGTPASS.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.FGTPASS.toString());
                        activityTrackerVOs.setLable_values(Value);
                        activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberid);
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(Header);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception e)
                        {
                            logger.error("Exception while AsyncActivityLog::::", e);
                        }

                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating member table.");
                }
                logger.debug("query has been committed for forgotpassword  ");
            }//end try
            catch (SQLException e)
            {
                logger.error("Exception while forgot password",e);
                throw new SystemError("Error : " + e.getMessage());
            }
            finally
            {
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        System.out.println("value123"+flag+"_"+Value);
        return flag+"_"+Value;
    }

    public String getPartnerLoginfromUserinAdmin(String login)
    {
        System.out.println("partnerlogin123----------------");
        String userLogin = null;
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String userQuery = "SELECT p.login,pu.userid FROM partners AS p , partner_users AS pu WHERE p.partnerid=pu.partnerid AND pu.login = ?";
            pstmt1 = conn.prepareStatement(userQuery);
            pstmt1.setString(1, login);
            rs = pstmt1.executeQuery();
            if (rs.next())
            {
                userLogin = rs.getString("login");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError---", se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return userLogin;
    }
    public String getpartnericici(String login)
    {
        System.out.println("getpartnericicimethod-------------------------");
        Connection conn = null;
        String icici = "";
        try
        {
            conn = Database.getConnection();
            logger.debug("check getMemberIcici method");
            String selquery = "select memberid,icici,fdtstamp,partnerid from members where login=? ";

            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            logger.debug("getpartnericici query::::"+pstmt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                icici = rs.getString("icici");
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in getpartnericici method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return icici;
    }

    public String getRole(String userName)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String role = null;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("get user name method");
            String selquery = "SELECT roles FROM user WHERE login = ? ";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, userName);
            logger.debug("pstmt:::::"+pstmt);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                logger.debug("role inside rs:::"+rs.getString("roles"));
                if("superpartner".equals(rs.getString("roles")))
                    role = "superpartner";
                else if("partner".equals(rs.getString("roles")))
                    role = "partner";
                else if("subpartner".equals(rs.getString("roles")))
                    role = "subpartner";
                else if("childsuperpartner".equals(rs.getString("roles")))
                    role = "childsuperpartner";
            }

        }

        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        logger.debug("role::::::::"+role);
        return role;
    }


    public boolean isPartner(String login) throws SystemError
    {
        Merchants merchants=new Merchants();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String role = merchants.getRole(login);
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "select accountid from `user` where login= ? and roles=?";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            pstmt.setString(2, role);
            logger.debug("forgot pstmt:::::"+pstmt);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMember method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean isPartnerUser(String login) throws SystemError
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            // conn = Database.getConnection();
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "select userid from partner_users where login = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }
    public String partnerresetpassword(String login, User user,String remoteAddr,String Header,String actionExecuterId) throws SystemError
    {
        System.out.println("partnerresetpasswordmethod----------------------------");
        String flag = "false";
        String Value ="";
        String role = "partner";
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        if ((isPartner(login) || isPartnerUser(login)) && user != null)
        {

            System.out.println("Entering partnerresetpasswordmethod--------------------");
            logger.debug("Entering forgotPassword() method");
            String fpasswd = generateKey(8);
            logger.debug("Temporary Pasword :::::::::"+fpasswd);
            String hashPassword = null;
            String oldPasshash = null, accountid = null;
            role = getUserRolepartner(user);
            try
            {

                hashPassword = ESAPI.authenticator().hashPassword(fpasswd, login);
            }
            catch (Exception e)
            {
                throw new SystemError("Error : " + e.getMessage());
            }

            Connection conn = null;
            PreparedStatement pstmt2 = null;
            ResultSet rs = null;
            //Getting login name from member table
            try
            {
                conn = Database.getConnection();
                /*String query2 = "select passwd from partners where login = ?";*/
                String query2 = "select hashedpasswd,accountid from `user` where login= ? and roles=? ";
                pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1, login);
                pstmt2.setString(2, role);
                logger.debug("partner---" + pstmt2);
                rs = pstmt2.executeQuery();
                if (rs.next())
                {
                    oldPasshash = rs.getString(1);
                    accountid = rs.getString(2);
                }

            }
            catch (SystemError se)
            {
                logger.error("System error occure::::", se);
                return "false";
            }
            catch (Exception e)
            {
                logger.error("Getting Excrption in changepassword method ::::", e);
                return "false";
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt2);
                Database.closeConnection(conn);
            }

            //Updated the temporary password in user table
            try
            {
                ((PzAuthenticator) ESAPI.authenticator()).forgetPassword(user, oldPasshash, hashPassword, hashPassword);
            }
            catch (Exception e)
            {
                logger.error("Change password throwing Authetication Exception ", e);
                return "false";
            }

            logger.debug("user table updated with temporary password");

            PreparedStatement pstmt = null;
            try
            {
                conn = Database.getConnection();

                String qry = "";
                String forgotDate = "";
                if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
                {
                    qry = "select fdtstamp from partner_users where login= ? and accid=?";
                }
                else
                {
                    qry = "select fdtstamp from partners where login= ? and accid=?";
                }
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, login);
                pstmt.setString(2, accountid);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    forgotDate = rs.getString("fdtstamp");
                }
                long diffHours = 1;

               /* if (functions.isValueNull(forgotDate))
                {
                    forgotDate =  Functions.convertDtstampToDBFormat(forgotDate);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Date d1 = sdf.parse(forgotDate);
                    Date d2 = sdf.parse(currentDate);
                    long diff = d2.getTime() - d1.getTime();
                    diffHours = diff / (60  * 60 * 1000);

                }*/
               /* if (diffHours >= 1)
                {*/

                String tableName = "partners";

                if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
                {
                    tableName = "partner_users";
                }
                String query = "update " + tableName + " set fpasswd=?,fdtstamp=unix_timestamp(now()) where login= ? and accid=?";

                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, hashPassword);
                /*pstmt.setString(2,hashPassword);*/
                pstmt.setString(2, login);
                pstmt.setString(3, accountid);
                logger.error("update query----" + pstmt);
                int success = pstmt.executeUpdate();
                logger.error("Success::::" + success);
                if (success > 0)
                {
                    /*ResultSet rs = null;*/
                    logger.debug("new temporary password has been set");

                    // send mail to member warning to use this password within 24 hr.
                    String query1 = "";
                    if (SUBPARTNER.equalsIgnoreCase(role) || CHILDSUBPARTNER.equals(role))
                    {
                        query1 = "select partnerid,contact_emails,userid from partner_users where login= ? and accid=?";
                    }
                    else
                    {
                        query1 = "select partnerId,contact_emails from partners where login= ? and accid=?";
                    }
                    pstmt = conn.prepareStatement(query1);
                    pstmt.setString(1, login);
                    pstmt.setString(2, accountid);
                    rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String memberid = rs.getString("partnerId");

                        //MailService mailService=new MailService();
                        flag = "true";
                        Value = fpasswd;
                        System.out.println("Valuepartner------" + Value);
                        activityTrackerVOs.setInterface(ActivityLogParameters.PARTNER.toString());
                        activityTrackerVOs.setUser_name(login + "-" + memberid);
                        activityTrackerVOs.setRole(role);
                        activityTrackerVOs.setAction(ActivityLogParameters.FGTPASS.toString());
                        activityTrackerVOs.setModule_name(ActivityLogParameters.FGTPASS.toString());
                        activityTrackerVOs.setLable_values(Value);
                        activityTrackerVOs.setDescription(ActivityLogParameters.PARTNERID.toString() + "-" + memberid);
                        activityTrackerVOs.setIp(remoteAddr);
                        activityTrackerVOs.setHeader(Header);
                        try
                        {
                            AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                            asyncActivityTracker.asyncActivity(activityTrackerVOs);
                        }
                        catch (Exception e)
                        {
                            logger.error("Exception while AsyncActivityLog::::", e);
                        }
                        logger.debug("inside if forgot password greater than 1 hour");
                    }
                }
                else
                {
                    throw new SystemError("Error : Exception occurred while updating member table.");
                }

                //}
                /*else
                {
                    flag = "false";
                    logger.debug("inside else forgot password less than 1 hour");
                }*/
                logger.debug("query has been committed for forgotpassword ");
            }//end try
            catch (SQLException e)
            {
                logger.error("SQLException in PartnerFunction---", e);
                throw new SystemError("Error : " + e.getMessage());
            }
            catch (Exception e)
            {
                logger.error("Exception in PartnerFunction---", e);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
            logger.debug("return from forgotpassword  ");
        }
        return flag+"_"+Value;

    }
    public static String getPartnerLoginfromUser(String login)
    {
        System.out.println("getPartnerLoginfromUser");
        String userLogin = null;
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String userQuery = "SELECT p.login,pu.userid FROM partners AS p , partner_users AS pu WHERE p.partnerid=pu.partnerid AND pu.login = ?";
            pstmt1 = conn.prepareStatement(userQuery);
            pstmt1.setString(1, login);
            rs = pstmt1.executeQuery();
            if (rs.next())
            {
                userLogin = rs.getString("login");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException---", se);
        }
        catch (SystemError se)
        {
            logger.error("SystemError---", se);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(conn);
        }
        return userLogin;
    }
    public String getRoles(String userName)
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String role = null;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("get user name method");
            String selquery = "SELECT roles FROM user WHERE login = ? ";
            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, userName);
            logger.debug("pstmt:::::"+pstmt);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                logger.debug("role inside rs:::"+rs.getString("roles"));
                if("superpartner".equals(rs.getString("roles")))
                    role = "superpartner";
                else if("partner".equals(rs.getString("roles")))
                    role = "partner";
                else if("subpartner".equals(rs.getString("roles")))
                    role = "subpartner";
                else if("childsuperpartner".equals(rs.getString("roles")))
                    role = "childsuperpartner";
            }

        }

        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        logger.debug("role::::::::"+role);
        return role;
    }



}