package com.payment.Mail;

import com.directi.pg.*;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.dao.PartnerDAO;
import com.manager.enums.PartnerTemplatePreference;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.PartnerEmailNotificationVO;
import com.manager.vo.TokenDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.payoutVOs.MerchantRandomChargesVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 2/25/14
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailService
{
    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.template");
    static Logger log = new Logger(MailService.class.getName());
    private static HashMap<Integer,String> hMapMailEntity = new HashMap<Integer, String>();
    //static hash to identify entity for mails
    static
    {
        //select entityid, placeholder tagname from mailentity
        //put in hMapMailEntity hashmap
        Connection con=null;
        try
        {
            con= Database.getConnection();
            String qry="select * from mailentity";
            PreparedStatement preparedStatement=con.prepareStatement(qry);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next())
            {
                hMapMailEntity.put(resultSet.getInt("mailEntityId"),resultSet.getString("placeHolderTagName"));
            }
        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        catch (SystemError systemError)
        {
            log.error("System error",systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    Functions functions=new Functions();
    Connection connection=null;
    String templatesFilePath = RBTemplate.getString("MAILTEMPLATEPATH");
    String templateImagePath = RBTemplate.getString("IMGPATHFORMAIL");

    public static void main (String args[])
    {
        MailService mailService=new MailService();
    }

    public String getMailEntityPlaceHolder(int mailEntityId )
    {
        return hMapMailEntity.get(mailEntityId);
    }

    public void sendMail(MailEventEnum mailEventEnum, HashMap<MailPlaceHolder,String> vMailContent)
    {
        try
        {
            Functions functions =new Functions();
            MailServiceDAO mailServiceDAO = new MailServiceDAO();
            SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
            List<MailMappingDetailsVO> mailMappingDetailsVOList = mailServiceDAO.getMailMappingDetails(mailEventEnum);
            //how many mail tobe send
            for (MailMappingDetailsVO mappingDetailsVO : mailMappingDetailsVOList)
            {
                // collect all ID for prepare mail
                int fromEntityId = mappingDetailsVO.getMailFromEntityId();
                int toEntityId = mappingDetailsVO.getMailToEntityId();
                //if ToEntity Is of Customer and MailEvent is Transaction
                MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getMailEntityPlaceHolder(toEntityId));
                String vMemberId = vMailContent.get(MailPlaceHolder.TOID);
                String vPartnerId=null;
                if(functions.isValueNull(vMailContent.get(MailPlaceHolder.PARTNERID)))
                    vPartnerId = vMailContent.get(MailPlaceHolder.PARTNERID);
                if(!functions.isValueNull(vPartnerId)){
                    HashMap hashMap=sendTransactionEventMailUtil.getPartnerIdBasedOnMerchant(vMemberId);
                    if(hashMap!=null && hashMap.size()>0)
                    {
                        vPartnerId = (String) hashMap.get("partnerId");
                    }
                }
                /*if((mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION) ||  mailEventEnum.equals(MailEventEnum.REFUND_TRANSACTION) || mailEventEnum.equals(MailEventEnum.PAYOUT_TRANSACTION) || mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION))&& vMemberId!=null)
                {
                    Connection conn = null;
                    try
                    {
                        conn = Database.getConnection();
                        String isCustomerMailFlag = "SELECT custremindermail,emailSent FROM members WHERE memberid=?";
                        PreparedStatement p = conn.prepareStatement(isCustomerMailFlag);
                        p.setString(1, vMemberId);
                        ResultSet rs = p.executeQuery();
                        if (rs.next())
                        {
                            String flag = rs.getString("custremindermail");
                            String emailSent = rs.getString("emailSent");
                            if (mailEntityEnum.equals(MailEntityEnum.CustomerEmail))
                            {
                                if (flag.equalsIgnoreCase("N"))
                                {
                                    continue;
                                }
                            }
                            if (mailEntityEnum.equals(MailEntityEnum.MerchantEmail))
                            {
                                if (emailSent.equalsIgnoreCase("N"))
                                {
                                    continue;
                                }
                            }
                        }
                    }
                    finally
                    {
                        Database.closeConnection(conn);
                    }
                }*/
                log.debug("mailEventEnum:::" + mailEventEnum + "------PartnerId-----" + vPartnerId);
                if(mailEventEnum.equals(MailEventEnum.PARTNER_CHANGE_PASSWORD) ||mailEventEnum.equals(MailEventEnum.PARTNER_USER_CHANGE_PASSWORD) || mailEventEnum.equals(MailEventEnum.PARTNER_FORGOT_PASSWORD) || mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION) ||
                        mailEventEnum.equals(MailEventEnum.PARTNER_REGISTRATION) ||mailEventEnum.equals(MailEventEnum.PARTNER_USER_REGISTRATION) || mailEventEnum.equals(MailEventEnum.CHARGEBACK_TRANSACTION) || mailEventEnum.equals(MailEventEnum.REFUND_TRANSACTION) ||
                        mailEventEnum.equals(MailEventEnum.CAPTURE_TRANSACTION) || mailEventEnum.equals(MailEventEnum.BILLING_DESCRIPTOR_CHANGE_INTIMATION) || mailEventEnum.equals(MailEventEnum.EXCEPTION_DETAILS) ||
                        mailEventEnum.equals(MailEventEnum.FRAUDRULE_CHANGE_INTIMATION) || mailEventEnum.equals(MailEventEnum.PARTNER_USER_FORGOT_PASSWORD) || mailEventEnum.equals(MailEventEnum.PARTNER_CHANGE_IN_PROFILE_DETAILS) ||
                        mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION) || mailEventEnum.equals(MailEventEnum.ADMIN_FAILED_TRANSACTION_NOTIFICATION) || mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION) ||
                        mailEventEnum.equals(MailEventEnum.PARTNERS_LEVEL_CARD_REGISTRATION) || mailEventEnum.equals(MailEventEnum.MERCHANTS_LEVEL_CARD_REGISTRATION) || mailEventEnum.equals(MailEventEnum.PAYOUT_TRANSACTION) ||
                        mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER) || mailEventEnum.equals(MailEventEnum.MERCHANT_CB_MONITORING_ALERT_TO_PARTNER) || mailEventEnum.equals(MailEventEnum.MERCHANT_RF_MONITORING_ALERT_TO_PARTNER) ||
                        mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_SALES) ||
                        mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_CB) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_RF) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_FRAUD) ||
                        mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_TECH) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_SALES) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_CB) ||
                        mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_RF) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_FRAUD) || mailEventEnum.equals(MailEventEnum.MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_TECH)  || mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_USER_FORGOT_PASSWORD) && functions.isValueNull(vPartnerId))
                {
                    Connection conn = null;
                    try
                    {
                        conn = Database.getConnection();
                        String isCustomerMailFlag = "SELECT emailSent FROM partners WHERE partnerId=? OR partnerName=?";
                        PreparedStatement p = conn.prepareStatement(isCustomerMailFlag);
                        p.setString(1, vPartnerId);
                        p.setString(2, vPartnerId);
                        ResultSet rs = p.executeQuery();
                        if (rs.next())
                        {
                           // String emailSent = rs.getString("emailSent");
                            //log.error("emailSent::::"+emailSent);
                            log.debug("mailEntityEnum::::" + mailEntityEnum);
                            log.debug("fromEntityId::::" + fromEntityId);
                            log.debug("toEntityId::::" + toEntityId);
                            /*if(*//*emailSent.equalsIgnoreCase("N") &&*//* fromEntityId==1 && toEntityId==2){
                                continue;
                            }*/
                        }
                    }catch (Exception e){
                        log.error("Exception ::::::::::",e);
                    }
                    finally
                    {
                        Database.closeConnection(conn);
                    }
                }
                int mailTemplateId = mappingDetailsVO.getMailTemplateId();
                String strMailSubject = mappingDetailsVO.getMailSubject();
                if(vMailContent.containsKey(MailPlaceHolder.ADMINEMAIL)){
                if (functions.isValueNull(vMailContent.get(MailPlaceHolder.ADMINEMAIL)) & vMailContent.get(MailPlaceHolder.ADMINEMAIL).equals("true"))
                {
                    strMailSubject = "";
                }
              }
                String cc=null;
                String bcc=null;
                if(mailEventEnum.equals(MailEventEnum.EXCEPTION_DETAILS)){
                    vPartnerId=null;
                }

                this.getFromEntityEmailId(fromEntityId, vMailContent,vPartnerId);
                this.getToEntityEmailIdBasedOnEvent(toEntityId, vMailContent, mailEventEnum,vPartnerId);
                if(vMailContent.get(MailPlaceHolder.SUBJECT)!=null)
                {
                    strMailSubject = strMailSubject +" "+vMailContent.get(MailPlaceHolder.SUBJECT);
                }

                if(functions.isValueNull(vMailContent.get(MailPlaceHolder.CC)))
                {
                    cc =vMailContent.get(MailPlaceHolder.CC);
                    log.debug("cc mail :::::::::::::::::::::::::::::::::::::::" + cc);
                    vMailContent.put(MailPlaceHolder.CC, null);
                }
                if(functions.isValueNull(vMailContent.get(MailPlaceHolder.BCC)))
                {
                    bcc =vMailContent.get(MailPlaceHolder.BCC);
                    log.debug("bcc mail :::::::::::::::::::::::::::::::::::::::" + bcc);
                    vMailContent.put(MailPlaceHolder.BCC, null);
                }
                //prepare mail template and repale placeholde with template tag
                log.debug("Language::::" + vMailContent.get(MailPlaceHolder.TEMP_LANG));
                if(vMailContent.get(MailPlaceHolder.TEMP_LANG)==null || !functions.isValueNull(vMailContent.get(MailPlaceHolder.TEMP_LANG))){
                    vMailContent.put(MailPlaceHolder.TEMP_LANG,"EN");
                }
                String template=this.getMailTemplateWithLang(mailTemplateId, vMailContent.get(MailPlaceHolder.TEMP_LANG));
                String mailMessage = Functions.replaceTag1(template, vMailContent);
                log.debug("PROTOCOL::::" + vMailContent.get(MailPlaceHolder.PROTOCOL));
                log.debug("vMailContent.get(MailPlaceHolder.EmailToAddress)::::" + vMailContent.get(MailPlaceHolder.EmailToAddress));

                if(vMailContent.get(MailPlaceHolder.ATTACHMENTFILENAME)!=null && vMailContent.get(MailPlaceHolder.ATTACHMENTFILEPATH)!=null)
                {
                    sendHtmlMail_WhiteLabel_WithAttachment(vMailContent.get(MailPlaceHolder.EmailToAddress), vMailContent.get(MailPlaceHolder.EmailFormAddress), cc, bcc, strMailSubject, mailMessage, vMailContent.get(MailPlaceHolder.M_HOST), vMailContent.get(MailPlaceHolder.M_PORT), vMailContent.get(MailPlaceHolder.M_USERNAME), vMailContent.get(MailPlaceHolder.M_PASSWORD), vMailContent.get(MailPlaceHolder.ATTACHMENTFILENAME), vMailContent.get(MailPlaceHolder.ATTACHMENTFILEPATH),vMailContent.get(MailPlaceHolder.RELAY_WITH_AUTH),vMailContent.get(MailPlaceHolder.PROTOCOL));
                }
                else
                {
                    sendHtmlMail_WhiteLabel(vMailContent.get(MailPlaceHolder.EmailToAddress), vMailContent.get(MailPlaceHolder.EmailFormAddress), cc, bcc, strMailSubject, mailMessage,vMailContent.get(MailPlaceHolder.M_HOST),vMailContent.get(MailPlaceHolder.M_PORT),vMailContent.get(MailPlaceHolder.M_USERNAME),vMailContent.get(MailPlaceHolder.M_PASSWORD),vMailContent.get(MailPlaceHolder.RELAY_WITH_AUTH),vMailContent.get(MailPlaceHolder.PROTOCOL));
                }
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error",systemError);
        }
        /*catch (SQLException e)
        {
            log.error("Sql error",e);
        }*/
        catch (Exception e){
            log.error("Exception:::::",e);
        }
    }
    public void sendMailWithMultipleAttachment(MailEventEnum mailEventEnum, HashMap<MailPlaceHolder, String> vMailContent, List<FileAttachmentVO> fileAttachmentVOList)
    {
        //MailService mailService=new MailService();
        Connection conn = null;
        try
        {
            //connection=Database.getConnection();
            conn = Database.getConnection();
            String qry = "SELECT mappingId,mailEventId,mailFromEntityId, mailToEntityId, mailTemplateId, mailSubject FROM mappingmailtemplateevententity WHERE mailEventId IN (SELECT mailEventId FROM mailevent WHERE mailEventName=?)";
            PreparedStatement preparedStatement = conn.prepareStatement(qry);
            preparedStatement.setString(1, mailEventEnum.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            //how many mail tobe send
            while (resultSet.next())
            {
                // collect all ID for prepare mail
                int fromEntityId = resultSet.getInt("mailFromEntityId");
                int toEntityId = resultSet.getInt("mailToEntityId");
                //if ToEntity Is of Customer and MailEvent is Transaction
                MailEntityEnum mailEntityEnum = MailEntityEnum.valueOf(getMailEntityPlaceHolder(toEntityId));
                String vMemberId = vMailContent.get(MailPlaceHolder.TOID);
                String vPartnerId = vMailContent.get(MailPlaceHolder.PARTNERID);

                if ((mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION) || mailEventEnum.equals(MailEventEnum.REFUND_TRANSACTION) || mailEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION)) && vMemberId != null)
                {
                    //Connection con=Database.getConnection();
                    String isCustomerMailFlag = "SELECT custremindermail,emailSent FROM members WHERE memberid=?";
                    PreparedStatement p = conn.prepareStatement(isCustomerMailFlag);
                    p.setString(1, vMemberId);
                    ResultSet rs = p.executeQuery();
                    if (rs.next())
                    {
                        String flag = rs.getString("custremindermail");
                        /*String emailSent = rs.getString("emailSent");*/
                        if (mailEntityEnum.equals(MailEntityEnum.CustomerEmail))
                        {
                            /*if (flag.equalsIgnoreCase("N"))
                            {
                                continue;
                            }*/
                        }
                        if (mailEntityEnum.equals(MailEntityEnum.MerchantEmail))
                        {
                            /*if (emailSent.equalsIgnoreCase("N"))
                            {
                                continue;
                            }*/
                        }
                    }
                }
                int mailTemplateId = resultSet.getInt("mailTemplateId");
                String strMailSubject = resultSet.getString("mailSubject");
                String cc = null;

                this.getFromEntityEmailId(fromEntityId, vMailContent,vPartnerId);
                this.getToEntityEmailIdBasedOnEvent(toEntityId, vMailContent, mailEventEnum,vPartnerId);
                if (vMailContent.get(MailPlaceHolder.SUBJECT) != null)
                {
                    strMailSubject = strMailSubject + " " + vMailContent.get(MailPlaceHolder.SUBJECT);
                }
                if (vMailContent.get(MailPlaceHolder.CC) != null)
                {
                    cc = vMailContent.get(MailPlaceHolder.CC);
                    vMailContent.put(MailPlaceHolder.CC, null);
                }
                //prepare mail template and repale placeholde with template tag
                String template = this.getMailTemplate(mailTemplateId);
                String mailMessage = Functions.replaceTag1(template, vMailContent);
                sendHtmlMail_WhiteLabel_WithMultipleAttachment(vMailContent.get(MailPlaceHolder.EmailToAddress), vMailContent.get(MailPlaceHolder.EmailFormAddress), cc, null, strMailSubject, mailMessage, vMailContent.get(MailPlaceHolder.M_HOST), vMailContent.get(MailPlaceHolder.M_PORT), vMailContent.get(MailPlaceHolder.M_USERNAME), vMailContent.get(MailPlaceHolder.M_PASSWORD), fileAttachmentVOList,vMailContent.get(MailPlaceHolder.RELAY_WITH_AUTH));
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error", systemError);
        }
        catch (SQLException e)
        {
            log.error("Sql error", e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
    }
    private HashMap<MailPlaceHolder ,String> getFromEntityEmailId(int intFromEntityId,HashMap<MailPlaceHolder,String> vMailContent,String partnerId)
    {
        MailEntityEnum mailEntityEnum = MailEntityEnum.valueOf(getMailEntityPlaceHolder(intFromEntityId));

        MailService mailService = new MailService();
        PartnerDAO partnerDAO=new PartnerDAO();
        PartnerDetailsVO partnerDetailsVO=null;
        try
        {
            partnerDetailsVO =partnerDAO.getPartnerDetails(partnerId);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException :::::::",e);
        }
        if(partnerDetailsVO!=null)
        {
            log.debug("partnerName:::" + partnerDetailsVO.getCompanyName() + "--------HostUrl----" + partnerDetailsVO.getHostUrl());
            vMailContent.put(MailPlaceHolder.PARTNERNAME, partnerDetailsVO.getCompanyName());
            vMailContent.put(MailPlaceHolder.HOST_URL, partnerDetailsVO.getHostUrl());
        }
        Hashtable details = new Hashtable();
        log.debug(mailEntityEnum);
        String superAdminId=getSuperAdminId(partnerId);
        log.debug("SuperAdminId:::" + superAdminId);
        switch (mailEntityEnum)
        {
            case AdminEmail:
                //fetch Admin details
                details = mailService.getSuperAdminvalue1(superAdminId);
                mailService.setDefaultMailContent(details, vMailContent);
                break;

            case PartnerEmail:
                //Fetch Partner Details
                details = mailService.getPartnersMerchantDetails(vMailContent.get(MailPlaceHolder.TOID),partnerId);
                log.debug("partner logo---" + details);
                mailService.setDefaultMailContent(details, vMailContent);
                break;

            case MerchantEmail:
                //Fetch Merchant Details
                details = mailService.getFromMerchantUserDetails(vMailContent.get(MailPlaceHolder.TOID),partnerId);
                mailService.setDefaultMailContent(details, vMailContent);
                break;

            case AgentEmail:
                //Fetch
                break;

            case CustomerEmail:
                break;
        }
         log.debug("From content   "+vMailContent);
        return vMailContent;
    }

    private HashMap<MailPlaceHolder ,String> getToEntityEmailId(int strToEntityId,HashMap<MailPlaceHolder,String> vMailContent)
    {
        MailService mailService=new MailService();
        Hashtable details=new Hashtable();
        Hashtable innerhash=new Hashtable();
        MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getMailEntityPlaceHolder(Integer.valueOf(strToEntityId)));
        switch(mailEntityEnum)
        {
            case AdminEmail:
                details=mailService.getSuperAdminvalue();
                innerhash=(Hashtable)details.get(1+"");
                vMailContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vMailContent.put(MailPlaceHolder.EmailToAddress,(String)innerhash.get("companyadminid"));
                break;

            case PartnerEmail:
                details=mailService.getPartnerDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vMailContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vMailContent.put(MailPlaceHolder.EmailToAddress,(String)innerhash.get("companyadminid"));
                break;

            case MerchantEmail:
                details=mailService.getMerchantDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vMailContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                vMailContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vMailContent.put(MailPlaceHolder.EmailToAddress,(String)innerhash.get("contact_emails"));
                break;

            case MerchantUserEmail:
                details=mailService.getMerchantUserDetails(vMailContent.get(MailPlaceHolder.USERID));
                innerhash=(Hashtable)details.get(1+"");
                //vMailContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                vMailContent.put(MailPlaceHolder.NAME,(String)innerhash.get("login"));
                vMailContent.put(MailPlaceHolder.EmailToAddress,(String)innerhash.get("contact_emails"));
                break;

            case AgentEmail:
                details=mailService.getAgentDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vMailContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vMailContent.put(MailPlaceHolder.EmailToAddress,(String)innerhash.get("contact_emails"));
                break;

            case CustomerEmail:
                details = mailService.getMerchantDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                vMailContent.put(MailPlaceHolder.MCOMNAME, (String) innerhash.get("sitename"));
                vMailContent.put(MailPlaceHolder.EmailToAddress, vMailContent.get(MailPlaceHolder.CustomerEmail));
                break;

            case FraudSystemEmail:
                details=mailService.getFraudSystemAccountDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1 + "");
                vMailContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_name"));
                vMailContent.put(MailPlaceHolder.EmailToAddress,(String) innerhash.get("contact_email"));
                break;

            case BankEmail:
                details=mailService.getSuperAdminvalue();
                innerhash=(Hashtable)details.get(1+"");
                vMailContent.put(MailPlaceHolder.URL,(String)innerhash.get("bankApplicationURL"));
                GatewayType gatewayType= GatewayTypeService.getGatewayType(vMailContent.get(MailPlaceHolder.PGTYPEID));
                vMailContent.put(MailPlaceHolder.NAME,gatewayType.getName());
                vMailContent.put(MailPlaceHolder.EmailToAddress,gatewayType.getBank_emailid());
                break;
        }
        log.debug("TO content   "+vMailContent);
        return vMailContent;
    }

    private HashMap<MailPlaceHolder ,String> getToEntityEmailIdBasedOnEvent(int strToEntityId,HashMap<MailPlaceHolder,String> vMailContent,MailEventEnum mailEventEnum,String partnerId)
    {
        Functions functions = new Functions();
        MailService mailService = new MailService();
        Hashtable details = new Hashtable();
        Hashtable innerhash = new Hashtable();
        MailEntityEnum mailEntityEnum = MailEntityEnum.valueOf(getMailEntityPlaceHolder(Integer.valueOf(strToEntityId)));
        String superAdminId=getSuperAdminId(partnerId);
        log.debug("mailEntityEnum:::" + mailEntityEnum);
        switch (mailEntityEnum)
        {
            case AdminEmail:
                details = mailService.getSuperAdminvalue1(superAdminId);
                innerhash = (Hashtable) details.get(1 + "");
                if(functions.isValueNull((String)innerhash.get("emailTemplateLang"))){
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, (String) innerhash.get("emailTemplateLang"));
                }else {
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, "EN");
                }
                MailContactVO adminMailContactVO= getAdminToMailId(mailEventEnum, innerhash,superAdminId);
                vMailContent.put(MailPlaceHolder.NAME,adminMailContactVO.getName());
                vMailContent.put(MailPlaceHolder.EmailToAddress,adminMailContactVO.getContactMailId());
                vMailContent.put(MailPlaceHolder.CC,adminMailContactVO.getContactCcMailId());

                break;

            case PartnerEmail:
                details=mailService.getPartnerDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                if(functions.isValueNull((String)innerhash.get("emailTemplateLang"))){
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, (String) innerhash.get("emailTemplateLang"));
                }else {
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, "EN");
                }
                MailContactVO partnerMailContactVO =getPartnerToMailId(mailEventEnum,innerhash,partnerId);
                vMailContent.put(MailPlaceHolder.NAME,partnerMailContactVO.getName());
                vMailContent.put(MailPlaceHolder.EmailToAddress,partnerMailContactVO.getContactMailId());
                vMailContent.put(MailPlaceHolder.CC,partnerMailContactVO.getContactCcMailId());
                break;

            /*case MerchantEmail:
                details = mailService.getMerchantDetails((String) vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                vMailContent.put(MailPlaceHolder.MCOMNAME, (String) innerhash.get("sitename"));
                vMailContent.put(MailPlaceHolder.NAME, (String) innerhash.get("contact_persons"));
                vMailContent.put(MailPlaceHolder.EmailToAddress, (String) innerhash.get("contact_emails"));
                break;*/

            case MerchantEmail:
                details = mailService.getMerchantDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                if(functions.isValueNull((String) innerhash.get("sitename")))
                {
                    vMailContent.put(MailPlaceHolder.MCOMNAME, (String) innerhash.get("sitename"));
                }
                else
                {
                    vMailContent.put(MailPlaceHolder.MCOMNAME, (String) innerhash.get("contact_persons"));
                }
                if(functions.isValueNull((String)innerhash.get("emailTemplateLang"))){
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, (String) innerhash.get("emailTemplateLang"));
                }else {
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, "EN");
                }

                MailContactVO mailContactVO=getMerchantToMailIdBasedOnEventWithCC(vMailContent,mailEventEnum, innerhash);
                vMailContent.put(MailPlaceHolder.NAME,mailContactVO.getName());
                vMailContent.put(MailPlaceHolder.EmailToAddress,mailContactVO.getContactMailId());
                vMailContent.put(MailPlaceHolder.CC,mailContactVO.getContactCcMailId());
                vMailContent.put(MailPlaceHolder.BCC,mailContactVO.getContactBccMailId());
                break;

            case MerchantUserEmail:
                details = mailService.getMerchantUserDetails(vMailContent.get(MailPlaceHolder.USERID));
                innerhash = (Hashtable) details.get(1 + "");
                //vMailContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                vMailContent.put(MailPlaceHolder.NAME, (String) innerhash.get("login"));
                vMailContent.put(MailPlaceHolder.EmailToAddress, (String) innerhash.get("contact_emails"));
                break;

            case PartnerUserEmail:
                details = mailService.getPartnerUserDetails(vMailContent.get(MailPlaceHolder.USERID));
                innerhash = (Hashtable) details.get(1 + "");
                //vMailContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                if(functions.isValueNull(vMailContent.get(MailPlaceHolder.ADMINEMAIL)) && vMailContent.get(MailPlaceHolder.ADMINEMAIL).equals("true")){
                    vMailContent.put(MailPlaceHolder.NAME, vMailContent.get(MailPlaceHolder.USERNAME));
                    vMailContent.put(MailPlaceHolder.EmailToAddress, vMailContent.get(MailPlaceHolder.EmailToAddress));
                }else
                {
                    vMailContent.put(MailPlaceHolder.NAME, (String) innerhash.get("login"));
                    vMailContent.put(MailPlaceHolder.EmailToAddress, (String) innerhash.get("contact_emails"));
                }
                break;

            case AgentEmail:
                details = mailService.getAgentDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                if(functions.isValueNull((String)innerhash.get("emailTemplateLang"))){
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, (String) innerhash.get("emailTemplateLang"));
                }else {
                    vMailContent.put(MailPlaceHolder.TEMP_LANG, "EN");
                }
                MailContactVO AgentMailContactVO=getAgentToMailIdBasedOnEventWithCC(mailEventEnum, innerhash);
                vMailContent.put(MailPlaceHolder.NAME,AgentMailContactVO.getName());
                vMailContent.put(MailPlaceHolder.EmailToAddress,AgentMailContactVO.getContactMailId());
                vMailContent.put(MailPlaceHolder.CC,AgentMailContactVO.getContactCcMailId());
                break;

            case CustomerEmail:
                details = mailService.getMerchantDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                if(innerhash!=null){
                    if(functions.isValueNull((String) innerhash.get("sitename"))){
                        vMailContent.put(MailPlaceHolder.MCOMNAME, (String) innerhash.get("sitename"));
                    }
                    else
                    {
                        vMailContent.put(MailPlaceHolder.MCOMNAME, (String) innerhash.get("contact_persons"));
                    }
                    if (mailEventEnum.equals(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT)||mailEventEnum.equals(MailEventEnum.CANCELED_INVOICE))
                    {
                       /* if (functions.isValueNull((String) innerhash.get("defaultLanguage")))
                        {
                            vMailContent.put(MailPlaceHolder.TEMP_LANG, (String) innerhash.get("defaultLanguage"));
                        }*/
                        if (functions.isValueNull(vMailContent.get(MailPlaceHolder.LANG_FOR_INVOICE)))
                        {
                            vMailContent.put(MailPlaceHolder.TEMP_LANG,vMailContent.get(MailPlaceHolder.LANG_FOR_INVOICE));
                        }
                    }
                    else
                    {
                        if (functions.isValueNull((String) innerhash.get("emailTemplateLang")))
                        {
                            vMailContent.put(MailPlaceHolder.TEMP_LANG, (String) innerhash.get("emailTemplateLang"));
                        }
                        else
                        {
                            vMailContent.put(MailPlaceHolder.TEMP_LANG, "EN");
                        }
                    }
                    if ("Merchant".equals(vMailContent.get(MailPlaceHolder.SUPPORT_EMAIL_FOR_TRANSACTION_MAIL)))
                    {
                        vMailContent.put(MailPlaceHolder.SUPPORTEMAIL,(String)innerhash.get("contact_emails"));
                        vMailContent.put(MailPlaceHolder.SUPPORTMAILFORCUSTOMER,(String)innerhash.get("support_emails"));
                        vMailContent.put(MailPlaceHolder.COMPANYNAME, (String)innerhash.get("company_name"));
                    }
                }
               // vMailContent.put(MailPlaceHolder.EmailToAddress, vMailContent.get(MailPlaceHolder.CustomerEmail));
                mailContactVO=getCustomerToMailIdBasedOnEventWithCC(vMailContent, mailEventEnum, innerhash);
                log.debug("contact emailto address:::::::::" + mailContactVO.getContactMailId());
                String contactemail = mailContactVO.getContactMailId();
                log.debug("customer emailto address:::::::::2" + vMailContent.get(MailPlaceHolder.CustomerEmail));
                if(functions.isEmptyOrNull(mailContactVO.getContactMailId())){
                    log.debug("Inside customer Email");
                    vMailContent.put(MailPlaceHolder.EmailToAddress, vMailContent.get(MailPlaceHolder.CustomerEmail));
                }
                else{
                    log.debug("Inside contact Email");
                    vMailContent.put(MailPlaceHolder.EmailToAddress, mailContactVO.getContactMailId());
                }
                if (functions.isValueNull(mailContactVO.getName()))
                {
                    vMailContent.put(MailPlaceHolder.NAME, mailContactVO.getName());
                }
                /*if(mailContactVO.getName().equals(null)||mailContactVO.getName().equals("")){
                    vMailContent.put(MailPlaceHolder.NAME, vMailContent.get(MailPlaceHolder.CARDHOLDERNAME));*/
                else
                {
                    vMailContent.put(MailPlaceHolder.NAME, vMailContent.get(MailPlaceHolder.CARDHOLDERNAME));
                }
                //vMailContent.put(MailPlaceHolder.CC,mailContactVO.getContactCcMailId());
                //vMailContent.put(MailPlaceHolder.BCC,mailContactVO.getContactBccMailId());
                break;

            case OtpEmail:
                vMailContent.put(MailPlaceHolder.OTP,vMailContent.get(MailPlaceHolder.OTP));
                vMailContent.put(MailPlaceHolder.TOID,vMailContent.get(MailPlaceHolder.TOID));
                vMailContent.put(MailPlaceHolder.EmailToAddress, vMailContent.get(MailPlaceHolder.CustomerEmail));
                break;

            case FraudSystemEmail:
                details = mailService.getFraudSystemAccountDetails(vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                vMailContent.put(MailPlaceHolder.NAME, (String) innerhash.get("contact_name"));
                vMailContent.put(MailPlaceHolder.EmailToAddress, (String) innerhash.get("contact_email"));
                break;

            case BankEmail:
                details = mailService.getSuperAdminvalue();
                innerhash = (Hashtable) details.get(1 + "");
                vMailContent.put(MailPlaceHolder.URL, (String) innerhash.get("bankApplicationURL"));
                GatewayType gatewayType = GatewayTypeService.getGatewayType(vMailContent.get(MailPlaceHolder.PGTYPEID));
                vMailContent.put(MailPlaceHolder.NAME, gatewayType.getName());
                vMailContent.put(MailPlaceHolder.EmailToAddress, gatewayType.getBank_emailid());
                break;

            case CustomerSupportEmail:
                details = mailService.getCustomerSupportContent(vMailContent.get(MailPlaceHolder.TOID));
                innerhash = (Hashtable) details.get(1 + "");
                vMailContent.put(MailPlaceHolder.NAME, (String) innerhash.get("csName"));
                vMailContent.put(MailPlaceHolder.EmailToAddress, (String) innerhash.get("csEmail"));
                break;
        }
        log.debug("TO content" + vMailContent);
        return vMailContent;
    }

    public String getMailTemplate(int mailTemplateId)
    {
        String loadMailTemplateContent="";
        try
        {
            connection=Database.getConnection();
            String templateName="";
            String qry="SELECT templateName,templateFileName FROM mailtemplate WHERE templateId=?";
            PreparedStatement preparedStatement= connection.prepareStatement(qry);
            preparedStatement.setInt(1, mailTemplateId);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next())
            {
                templateName=resultSet.getString("templateFileName");
            }
            else
            {
                throw new SystemError("MAIL TEMPLATE NOT FOUND FOR MAIL ");
            }

            File templateFile = new File(templatesFilePath +templateName);
            loadMailTemplateContent=getMailContents(templateFile);
        }
        catch (SystemError systemError)
        {
            log.error("System error",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        catch (IOException e)
        {
            log.error("IO Exception", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return loadMailTemplateContent;
    }
    public String getMailTemplateWithLang(int mailTemplateId,String tempLang)
    {
        String loadMailTemplateContent="";
        try
        {
            connection=Database.getConnection();
            String templateName="";
            String qry="SELECT templateName,templateFileName FROM mailtemplate WHERE templateId=?";
            PreparedStatement preparedStatement= connection.prepareStatement(qry);
            preparedStatement.setInt(1, mailTemplateId);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next())
            {
                templateName=resultSet.getString("templateFileName");
            }
            else
            {
                throw new SystemError("MAIL TEMPLATE NOT FOUND FOR MAIL ");
            }
            File templateFile=null;
            if(functions.isValueNull(tempLang) && tempLang.equals("EN")){
                templateFile = new File(templatesFilePath+"/"+templateName);
            }else {
                templateFile = new File(templatesFilePath+tempLang+"/"+templateName);
            }
            log.debug("templateFile:::" + templateFile);
            //File templateFile = new File(templatesFilePath+tempLang +templateName);
            loadMailTemplateContent=getMailContents(templateFile);
        }
        catch (SystemError systemError)
        {
            log.error("System error",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Exception",e);
        }
        catch (IOException e)
        {
            log.error("IO Exception", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return loadMailTemplateContent;
    }

    private String getMailContents(File templateFile)throws IOException
    {
        FileReader fr = new FileReader(templateFile);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer mailContents = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null)
        {
            mailContents.append(line);
        }
        return mailContents.toString();
    }

    public Hashtable getPartnersMerchantDetails(String toid,String partnerId)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            String qry="SELECT * FROM partners WHERE partnerId=?";
            //String qry="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)";
            //Checking the partner id condition first then memeber id then agent id for Partner User Management
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,partnerId);
            partnerMailDetail =Database.getHashFromResultSet(preparedStatement.executeQuery());
            log.debug("inside Mailservide---"+preparedStatement);
            log.debug("partnerMailDetail.isEmpty()---"+partnerMailDetail.isEmpty());
            if (partnerMailDetail.isEmpty())
            {
                //System.out.println("inside else if---");
                qry = "SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)";
                PreparedStatement preparedStatement2 = conn.prepareStatement(qry);
                preparedStatement2.setString(1, toid);
                partnerMailDetail = Database.getHashFromResultSet(preparedStatement2.executeQuery());
                log.debug("inside if Mailservice1---" + preparedStatement2);
                log.debug("partnermailDetail else----"+partnerMailDetail.isEmpty());
            }
            if(partnerMailDetail.isEmpty())
            {
                String qry1="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM agents WHERE agentId=?)";
                PreparedStatement preparedStatement1= conn.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
                log.debug("inside Mailservice2---"+preparedStatement1);
                log.debug("partnermailDetail if----"+partnerMailDetail.isEmpty());
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        log.debug(partnerMailDetail);
        return partnerMailDetail;
    }
    public Hashtable getFromMerchantUserDetails(String toid ,String partnerId)
    {
        Hashtable memberUserMailDetail=new Hashtable();
        Connection conn=null;
        Functions function = new Functions();
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry = "";
            PreparedStatement preparedStatement= null;
            //String qry="SELECT * FROM members WHERE memberid=(SELECT memberid FROM member_users WHERE userid=?)";
           // String qry="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=(SELECT memberid FROM member_users WHERE userid=?))";
            if(functions.isValueNull(partnerId)){
                 qry="SELECT * FROM partners WHERE partnerId=?";
                 preparedStatement= conn.prepareStatement(qry);
                preparedStatement.setString(1,partnerId);
            }
            else{
                qry="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=(SELECT memberid FROM member_users WHERE userid=?))";
                preparedStatement= conn.prepareStatement(qry);
                preparedStatement.setString(1,toid);
            }
            memberUserMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());
            log.debug("fromMemberUserDetails---"+preparedStatement);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        log.debug(memberUserMailDetail);
        return memberUserMailDetail;
    }

    public Hashtable getPartnerDetails(String toid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="SELECT * FROM partners WHERE partnerId=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,toid);
            partnerMailDetail =Database.getHashFromResultSet(preparedStatement.executeQuery());

            if(partnerMailDetail.isEmpty())
            {
                String qry1="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)";
                PreparedStatement preparedStatement1= conn.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
            if(partnerMailDetail.isEmpty())
            {
                String qry1="SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM agents WHERE agentId=?)";
                PreparedStatement preparedStatement1= conn.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                partnerMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        log.debug(partnerMailDetail);
        return partnerMailDetail;
    }

    public Hashtable getAgentDetails(String toid)
    {
        Hashtable agentMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="SELECT * FROM agents WHERE agentId=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,toid);
            agentMailDetail =Database.getHashFromResultSet(preparedStatement.executeQuery());

            if(agentMailDetail.isEmpty())
            {
                String qry1="SELECT * FROM agents WHERE agentId=(SELECT agentId FROM members WHERE memberid=?)";
                PreparedStatement preparedStatement1= conn.prepareStatement(qry1);
                preparedStatement1.setString(1,toid);
                agentMailDetail =Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }

        return agentMailDetail;
    }

    public Hashtable getMerchantUserDetails(String memberid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="select * from member_users where userid=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,memberid);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());
            log.debug("member user query---"+preparedStatement);

        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return partnerMailDetail;
    }

    public Hashtable getPartnerUserDetails(String partnerid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="select * from partner_users where userid=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,partnerid);
            log.debug("getPartnerUserDetails---"+preparedStatement);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());

        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return partnerMailDetail;
    }

    public Hashtable getMerchantDetails(String memberid)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            //String qry="select * from members where memberid=?";
            String qry="SELECT m.*,mc.* FROM members AS m left JOIN merchant_configuration AS mc on m.memberid=mc.memberid where m.memberid=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,memberid);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());

        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return partnerMailDetail;
    }

    public Hashtable getFraudSystemAccountDetails(String fraudSystemAccountId)
    {
        Hashtable hashtable=new Hashtable();
        Connection conn=null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="select fsaccountid,contact_name,contact_email from fraudsystem_account_mapping where fsaccountid=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,fraudSystemAccountId);
            hashtable = Database.getHashFromResultSet(preparedStatement.executeQuery());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return hashtable;
    }

    public Hashtable getBankDetails(String pgTypeId)
    {
        Hashtable partnerMailDetail=new Hashtable();
        try
        {
            connection=Database.getConnection();
            String qry="select * from gateway_type where pgtypeid=?";
            PreparedStatement preparedStatement= connection.prepareStatement(qry);
            preparedStatement.setString(1,pgTypeId);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());

        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return partnerMailDetail;
    }

    public Hashtable  getSuperAdminvalue()
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select * from partners where partnerId=1 limit 1";
            partnerMailDetail =Database.getHashFromResultSet( Database.executeQuery(query3,conn));

        }
        catch (SystemError systemError)
        {
            log.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return partnerMailDetail;
    }

    public Hashtable  getSuperAdminvalue1(String partnerId)
    {
        Hashtable partnerMailDetail=new Hashtable();
        Connection conn=null;
        String partnerid=null;
        if(functions.isValueNull(partnerId) && !partnerId.equals("0")){
            partnerid=partnerId;
        }else
            partnerid="1";
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select * from partners where partnerId=? or partnerName=?";
            PreparedStatement preparedStatement=conn.prepareStatement(query3);
            preparedStatement.setString(1,partnerid);
            preparedStatement.setString(2,partnerid);
            log.debug("Query for getSuperAdminvalue1::::" + preparedStatement);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return partnerMailDetail;
    }
    public String  getSuperAdminId(String partnerId)
    {
        log.debug("Inside getSuperAdmiId::::" + partnerId);
        String superAdminId="";
        Connection conn=null;
        String partnerid=null;
        ResultSet rs=null;
        if(functions.isValueNull(partnerId) && !partnerId.equals("0")){
            partnerid=partnerId;
        }else
            partnerid="1";
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select superadminid from partners where partnerId=? or partnerName=?";
            PreparedStatement preparedStatement=conn.prepareStatement(query3);
            preparedStatement.setString(1,partnerid);
            preparedStatement.setString(2,partnerid);
            log.debug("Query for superadmin:::" + preparedStatement);
            rs=preparedStatement.executeQuery();
            if(rs.next()){
                superAdminId=rs.getString("superadminid");
            }
            log.debug("superadminId in mailService::::" + superAdminId);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return superAdminId;
    }

    public Hashtable  getCustomerSupportContent(String csId)
    {
        Hashtable customerSupportDetail=new Hashtable();
        Connection conn=null;
        try
        {
            //connection = Database.getConnection();
            conn = Database.getConnection();
            String query3 = "select csName,csEmail from customersupport where csId='"+csId+"'";
            customerSupportDetail =Database.getHashFromResultSet( Database.executeQuery(query3,conn));

        }
        catch (SystemError systemError)
        {
            log.error("SystemError", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return customerSupportDetail;
    }

    public boolean isMailRequiredForCustomer(int toEntityId,String memberid,MailEventEnum mailEvent)
    {
        boolean isMailSend=false;
        MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getMailEntityPlaceHolder(toEntityId));
        if(mailEntityEnum.equals(MailEntityEnum.CustomerEmail) && mailEvent.equals(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION) && memberid!=null)
        {

            MailService mailService=new MailService();
            Hashtable innerHash=new Hashtable();
            Hashtable mailConfigFlages= mailService.getMerchantDetails(memberid);
            innerHash=(Hashtable)mailConfigFlages.get(1+"");
            switch(mailEvent)
            {
                /*case PARTNERS_MERCHANT_SALE_TRANSACTION:
                    if(innerHash.get("custremindermail").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;*/
                case CHARGEBACK_TRANSACTION:
                    if(innerHash.get("isCBmailtocus").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;

                case REFUND_TRANSACTION:
                    if(innerHash.get("isRFmailtocus").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;

                case CAPTURE_TRANSACTION:
                    if(innerHash.get("isCapturemailtocus").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;
            }

        }
        return isMailSend;
    }

    public Hashtable getPartnerTemplateDetails(String partnerId)
    {
        Hashtable partnerTemplateDetail=new Hashtable();
        Connection conn=null;
        ResultSet resultSet = null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="select * from partner_template_preference where partnerid=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,partnerId);
            log.debug("getPartnerTemplateDetails---"+preparedStatement);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                if(functions.isValueNull(resultSet.getString("value")))
                    partnerTemplateDetail.put(PartnerTemplatePreference.getEnum(resultSet.getString("name")).toString(),resultSet.getString("value"));

                log.debug("partnerTemplateDetail---"+partnerTemplateDetail);
            }

        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return partnerTemplateDetail;
    }
    //Member Template Details

    public Hashtable getMemberTemplateDetails(String memberId)
    {
        Hashtable memberTemplateDetail=new Hashtable();
        Connection conn=null;
        ResultSet resultSet = null;
        try
        {
            //connection=Database.getConnection();
            conn=Database.getConnection();
            String qry="select * from template_preferences where memberid=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,memberId);
            log.debug("getMemberTemplateDetails---"+preparedStatement);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                memberTemplateDetail.put(resultSet.getString("name").toString(), resultSet.getString("value"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return memberTemplateDetail;
    }

    public HashMap<MailPlaceHolder ,String> setDefaultMailContent(Hashtable defaultValue,HashMap<MailPlaceHolder,String> vMailContent)
    {
        Hashtable innerhash=new Hashtable();
        //log.debug("default value----"+defaultValue.get(1+""));
        innerhash=(Hashtable)defaultValue.get(1+"");
        Hashtable partnertemplateHash = getPartnerTemplateDetails((String)innerhash.get("partnerId"));
        Hashtable membertemplateHash = getMemberTemplateDetails(vMailContent.get(MailPlaceHolder.TOID));
        String DefaultColor="#7eccad";
        String firstcolor="";
        //log.debug("logo---"+innerhash.get("logoName")+"---site---"+ innerhash.get("siteurl"));
        vMailContent.put(MailPlaceHolder.LOGO,"<table style=\"width:200px;\" width=\"100px\" height=\"100\" bgcolor=\"\"><tr><td height=\"100\"><img width=\"\" height=\"inherit\"  src=https://"+ innerhash.get("hosturl") +templateImagePath+innerhash.get("logoName")+"></td></tr></table>");
        if (functions.isValueNull((String) innerhash.get("partnerOrgnizationForWL_Invoice")))
        {
            vMailContent.put(MailPlaceHolder.COMPANYNAME, (String)innerhash.get("partnerOrgnizationForWL_Invoice"));
        }
        else
        {
            vMailContent.put(MailPlaceHolder.COMPANYNAME, (String)innerhash.get("partnerName"));
        }
        vMailContent.put(MailPlaceHolder.SUPPORTLINK,(String)innerhash.get("supporturl"));
        vMailContent.put(MailPlaceHolder.LIVEURL,(String)innerhash.get("siteurl"));
        vMailContent.put(MailPlaceHolder.EmailFormAddress,(String)innerhash.get("companyfromemail"));
        vMailContent.put(MailPlaceHolder.M_HOST,(String)innerhash.get("smtp_host"));
        vMailContent.put(MailPlaceHolder.M_PORT,(String)innerhash.get("smtp_port"));
        vMailContent.put(MailPlaceHolder.M_USERNAME,(String)innerhash.get("smtp_user"));
        vMailContent.put(MailPlaceHolder.M_PASSWORD,(String)innerhash.get("smtp_password"));
        vMailContent.put(MailPlaceHolder.FRAUDMAILID,innerhash.get("fraudemailid")==null?"":(String)innerhash.get("fraudemailid"));
        vMailContent.put(MailPlaceHolder.SUPPORTEMAIL, innerhash.get("companysupportmailid") == null ? "" : (String) innerhash.get("companysupportmailid"));
        vMailContent.put(MailPlaceHolder.SUPPORTMAILFORCUSTOMER, innerhash.get("companysupportmailid") == null ? "" : (String) innerhash.get("companysupportmailid"));
        //vMailContent.put(MailPlaceHolder.DEFAULTTHEME,(String)innerhash.get("default_theme"));
        //vMailContent.put(MailPlaceHolder.CURRENTTHEME,(String)innerhash.get("current_theme"));
        vMailContent.put(MailPlaceHolder.RELAY_WITH_AUTH,(String)innerhash.get("relayWithAuth"));
        vMailContent.put(MailPlaceHolder.PROTOCOL,(String)innerhash.get("protocol"));
        //vMailContent.put(MailPlaceHolder.HOST_URL,(String)innerhash.get("hosturl"));
        vMailContent.put(MailPlaceHolder.SUPPORT_EMAIL_FOR_TRANSACTION_MAIL,(String)innerhash.get("support_email_for_transaction_mail"));

        List<PartnerDetailsVO> partnerDetailsVOList=getListOfHostUrl(vMailContent.get(MailPlaceHolder.HOST_URL));
        String url="";
        if(partnerDetailsVOList!=null && partnerDetailsVOList.size()>1){
            url="https://"+vMailContent.get(MailPlaceHolder.HOST_URL)+"/merchant/index.jsp?fromtype="+vMailContent.get(MailPlaceHolder.PARTNERNAME)+"&partnerid="+vMailContent.get(MailPlaceHolder.PARTNERID);
            vMailContent.put(MailPlaceHolder.URL,url);
        }else{
            url="https://"+vMailContent.get(MailPlaceHolder.HOST_URL)+"/merchant/index.jsp";
            vMailContent.put(MailPlaceHolder.URL,url);
        }
        log.debug("URL:::::" + url);

        if(functions.isEmptyOrNull((String)partnertemplateHash.get("AMAILBACKGROUNDCOLOR")) && functions.isEmptyOrNull((String) membertemplateHash.get("MAILBACKGROUNDCOLOR")))
        {

            firstcolor=DefaultColor;
            //log.debug("color of default...."+firstcolor);

        }
        else if(functions.isEmptyOrNull((String) membertemplateHash.get("MAILBACKGROUNDCOLOR")) && functions.isValueNull((String) partnertemplateHash.get("AMAILBACKGROUNDCOLOR")))
        {
            firstcolor=(String)partnertemplateHash.get("AMAILBACKGROUNDCOLOR");
            //log.debug("color of partner default...."+firstcolor);
        }
        else if(functions.isValueNull((String) membertemplateHash.get("MAILBACKGROUNDCOLOR")) && functions.isEmptyOrNull((String) partnertemplateHash.get("AMAILBACKGROUNDCOLOR")))
        {
            firstcolor=(String) membertemplateHash.get("MAILBACKGROUNDCOLOR");
            //log.debug("color of member default...."+firstcolor);
        }
        else if(functions.isValueNull((String) partnertemplateHash.get("AMAILBACKGROUNDCOLOR")) && functions.isValueNull((String) membertemplateHash.get("MAILBACKGROUNDCOLOR")))
        {
            firstcolor=(String) membertemplateHash.get("MAILBACKGROUNDCOLOR");
            //log.debug("color of member when both empty default...."+firstcolor);
        }


        //log.debug("firstcolor of array...."+firstcolor);

        vMailContent.put(MailPlaceHolder.FIRSTCOLOR,firstcolor);

        //vMailContent.put(MailPlaceHolder.FIRSTCOLOR,(String)partnertemplateHash.get("AMAILBACKGROUNDCOLOR"));

        return vMailContent;
    }

    public String getDetailTable(Map<String,Map<String,String>> transDetail)
    {

        String style="class=td11";
        String previousicicitransid =" ";
        String previousStyle=" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" size=\"2px\"";
        String currentStyle=" font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";

        Set<String> trackingids = transDetail.keySet();
        StringBuffer table = new StringBuffer();
        int i =0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        for (String trackingid : trackingids)
        {
            Map<String,String> hashMap = transDetail.get(trackingid);
            Set<String> columns = hashMap.keySet();
            if(i==0)
            {
                table.append("<TR>");
                for(String column : columns)
                {
                    table.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
                    table.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">"+column+"</font></p></b>");
                    table.append("</TD>");
                }
                table.append("</TR>");
            }
            i++;
            table.append("<TR>");
            if(!previousicicitransid.equals(transDetail.get(trackingid)))
            {
                String tempStyle="";
                previousicicitransid =transDetail.get(trackingid).toString();
                tempStyle = previousStyle;
                previousStyle=currentStyle;
                currentStyle=tempStyle;
            }
            style=currentStyle;
            for(String column : columns)
            {
                table.append("<TD "+style+" >");
                table.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\">"+hashMap.get(column)+"</p>");
                table.append("</TD>");
            }
            table.append("</TR>");
        }
        table.append("</table>");
        return table.toString();
    }

    public String getDetailTableForSingleTrans(LinkedHashMap<String,String> transDetail)
    {
        StringBuffer table = new StringBuffer();
        int i =0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");

        Set<String> columns = transDetail.keySet();
        if(i==0)
        {
            table.append("<TR>");
            for(String column : columns)
            {
                table.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\">");
                table.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">"+column+"</font></p></b>");
                table.append("</TD>");
            }
            table.append("</TR>");
        }
        i++;
        table.append("<TR>");
        for(String column : columns)
        {
            table.append("<TD>");
            table.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+transDetail.get(column)+"</p>");
            table.append("</TD>");
        }
        table.append("</TR>");

        table.append("</table>");
        return table.toString();
    }

    public String getDetailTableForRandomCharges(List<MerchantRandomChargesVO> merchantRandomChargesVOList)
    {
        StringBuffer table = new StringBuffer();
        String style="class=td11";
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"15%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Name</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Rate</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Counter</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Amount</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Value</font></p></b></td>");
        table.append("<td width=\"5%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Type</font></p></b></td>");
        table.append("<td width=\"20%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Charge Remark</font></p></b></td>");
        table.append("</tr>");
        int i=0;

        String previousStyle=" bgcolor=\"\" color=\"#001963\" font\"sans-serif\" size=\"2px\"";
        String currentStyle=" font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";
        style=currentStyle;
        for(MerchantRandomChargesVO merchantRandomChargesVO: merchantRandomChargesVOList)
        {
            table.append("<tr>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
            table.append("<td "+style+" >"+merchantRandomChargesVO.getChargeName()+"</td>");
            if("Percentage".equals(merchantRandomChargesVO.getChargeValueType()))
            {
                table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+ Functions.round(merchantRandomChargesVO.getChargeRate(),2)+"%</td>");
            }
            else
            {
                table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+Functions.round(merchantRandomChargesVO.getChargeRate(),2)+"</td>");
            }
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+merchantRandomChargesVO.getChargeCounter()+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+Functions.round(merchantRandomChargesVO.getChargeAmount(),2)+"</td>");
            table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+Functions.round(merchantRandomChargesVO.getChargeValue(),2)+"</td>");
            if("Deducted".equals(merchantRandomChargesVO.getChargeType()))
            {
                table.append("<td "+style+" width=\"5%\" >Debited</td>");
            }
            else
            {
                table.append("<td "+style+" width=\"5%\" >Credited</td>");
            }
            table.append("<td "+style+" width=\"20%\">"+merchantRandomChargesVO.getChargeRemark()+"</td>");
            table.append("</tr>");
        }
        table.append("</table>");
        return table.toString();
    }
    public String getDetailTableForCardDetails(TokenDetailsVO tokenDetailsVO)
    {
        StringBuffer table = new StringBuffer();
        String style="class=td11";
        int i=0;
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\"  width=\"90%\">");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Sr No</font></p></b></td>");
        table.append("<td width=\"15%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Cardholder Name</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Card Type</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">CardNumber</b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Expiry</font></p></b></td>");
        // table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Is Active</font></p></b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";
        style=currentStyle;

        table.append("<tr>");
        table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\" >"+(++i)+"</td>");
        table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+tokenDetailsVO.getCommCardDetailsVO().getCardHolderFirstName()+ " "+tokenDetailsVO.getCommCardDetailsVO().getCardHolderSurname()+"</td>");
        table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+Functions.getCardType(tokenDetailsVO.getCommCardDetailsVO().getCardNum())+"</td>");
        table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(0, 6)+"******"+tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(tokenDetailsVO.getCommCardDetailsVO().getCardNum().length() - 4)+"</td>");
        table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+tokenDetailsVO.getCommCardDetailsVO().getExpMonth()+"/"+tokenDetailsVO.getCommCardDetailsVO().getExpYear()+"</td>");
        // table.append("<td "+style+" valign=\"middle\" align=\"center\" width=\"4%\">"+tokenDetailsVO.getIsActive()+"</td>");
        table.append("</tr>");
        table.append("</table>");
        return table.toString();
    }

    public String getDetailTableForMultipleTrans(List<TransactionDetailsVO> transactionDetailsVOList,String action,String remark)
    {
        StringBuffer table=new StringBuffer();
        String style="";

        //table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        table.append("<tr>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingID</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">CustName</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Amount</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Reason</font></p></b></td>");
        table.append("<td width=\"4%\" valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">ActionToBeTaken</font></p></b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";
        style=currentStyle;
        for(TransactionDetailsVO transactionDetailsVO:transactionDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getTrackingid()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getName()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getAmount()+" "+transactionDetailsVO.getCurrency()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getStatus()+"</td>");
            //table.append("<td align=\"center\" "+style+" >"+remark+"</td>");
            if(action.equals("Fraud Intimation"))
            {
                table.append("<td align=\"center\" "+style+" >"+"Fraudulent Transaction"+"</td>");
            }
            else if("Fraud Reversal & Intimation".equalsIgnoreCase(action)){
                if(!functions.isValueNull(remark)){
                    table.append("<td align=\"center\" "+style+" >"+"Fraudulent Transaction"+"</td>");
                }else
                {
                    table.append("<td align=\"center\" " + style + " >" + remark + "</td>");
                }
            }
            if(action.equals("Fraud Intimation"))
            {
                table.append("<td align=\"center\" "+style+" >"+"Refund Immediately"+"</td>");
            }
            else if("Fraud Reversal & Intimation".equalsIgnoreCase(action)){
                table.append("<td align=\"center\" "+style+" >"+"Refunded "+"</td>");
            }
            table.append("</tr>");
        }
        // table.append("</table>");
        /*table.append("<br>");
        table.append("<br>");*/
        return table.toString();
    }
    public String getMultipleRefundTransaction(List<TransactionDetailsVO> transactionDetailsVOList,String remark)
    {
        StringBuffer table=new StringBuffer();
        String style="";
        table.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        table.append("<tr>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingID</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MerchantId</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Order ID</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Amount</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Refund Amount</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Reason</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Customer Name</font></p></b></td>");
        table.append("<td valign=\"middle\" align=\"center\" bgcolor=\"#abb7b7\"><b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Customer Email</font></p></b></td>");
        table.append("</tr>");

        String currentStyle=" font bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\" ";
        style=currentStyle;
        log.debug("transactionDetailsVOList--->" + transactionDetailsVOList);
        for(TransactionDetailsVO transactionDetailsVO:transactionDetailsVOList)
        {
            table.append("<tr>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getTrackingid()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getToid()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getDescription()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getAmount()+" "+transactionDetailsVO.getCurrency()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getRefundAmount()+" "+transactionDetailsVO.getCurrency()+"</td>");
            table.append("<td align=\"center\" "+style+" >"+"successful"+"</td>");
            table.append("<td align=\"center\" "+style+" >"+remark+"</td>");
            table.append("<td align=\"center\" "+style+" >"+transactionDetailsVO.getName()+"</td>");
            table.append("<td align=\"center\" "+style + " >"+transactionDetailsVO.getEmailaddr()+"</td>");
            table.append("</tr>");
        }
        table.append("</table>");
        return table.toString();
    }
    public void sendHtmlMail_WhiteLabel(String to, String from, String cc, String bcc, String subject, String message,String host,String port,String userName,String password,String relayWithAuth,String protocol) throws SystemError
    {
        String contenttype = "text/html";
        try
        {
            log.debug("Inside SendMail12");

            /*MailService mailService=new MailService();
            Hashtable superAdmin = mailService.getSuperAdminvalue();
            Hashtable innerhash=(Hashtable)superAdmin.get(1+"");*/
            Properties props = new Properties();

            props.put("mail.debug", "true");
            props.put("mail.smtp.host", host);

            if((port!=null && !port.equals("")))
                props.put("mail.smtp.port", port);
            else
                props.put("mail.smtp.port", "25");

            props.put("mail.smtp.ssl.trust", "*."+host.substring(host.indexOf(".")+1));
            props.put("mail.smtp.localhost", "127.0.0.1");
            log.debug("Protocol::::" + protocol);
            if(!protocol.isEmpty() && protocol.equals("SSL"))
            {
                props.put("mail.smtp.EnableSSL.enable","true");
                props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.smtp.socketFactory.fallback", "false");
            }
            else{
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            }

            /*props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            props.put("mail.smtp.host", host);

            if((port!=null && !port.equals("")))
                props.put("mail.smtp.port", port);
            else
                props.put("mail.smtp.port", "25");

            props.put("mail.smtp.ssl.trust", "*."+host.substring(host.indexOf(".")+1));
            props.put("mail.debug", "true");
            props.put("mail.smtp.localhost", "127.0.0.1");
            *//*props.put("mail.smtp.EnableSSL.enable","true");
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");*//*
           */
            Session session = null;
            //Relay with Auth
            if("Y".equals(relayWithAuth))
            {
                props.put("mail.smtp.auth", "true");
                Authenticator auth = new SMTPAuthenticator(userName,password);
                session = Session.getInstance(props,auth);
            }
            else
            {
                props.put("mail.smtp.auth", "false");
                session = Session.getInstance(props);
            }


            try
            {
                MimeMessage msg= new MimeMessage(session);
                msg.setReplyTo(new Address[]{new InternetAddress(from)});
                msg.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                log.debug("CC--" + cc);
                if (cc != null && cc !="" && !cc.contains(";"))
                {
                    log.debug("inside cc first");
                    msg.addRecipient(Message.RecipientType.CC,new InternetAddress(cc));
                }
                if(cc != null && cc !="" && cc.contains(";")){
                    log.debug("inside second-");
                    String[] CC=cc.split("\\;");
                    for (String cd:CC){
                        log.debug("CD--" + cd);
                        if(functions.isValueNull(cd) && ESAPI.validator().isValidInput("email",cd,"Email", 250, false)){
                            msg.addRecipient(Message.RecipientType.CC,new InternetAddress(cd));
                        }
                        //.addRecipient(Message.RecipientType.CC,new InternetAddress(cd));
                    }
                }

                /*if (cc != null && cc !="")
                {
                    msg.addRecipient(Message.RecipientType.CC,new InternetAddress(cc));
                }*/

                /*if (bcc != null)
                {
                    msg.addRecipient(Message.RecipientType.BCC,new InternetAddress(bcc));
                }*/
                if (bcc != null && bcc !="" && !bcc.contains(";"))
                {
                    log.debug("inside bcc first");
                    msg.addRecipient(Message.RecipientType.BCC,new InternetAddress(bcc));
                }
                if(bcc != null && bcc !="" && bcc.contains(";")){
                    log.debug("inside bcc second-");
                    String[] CC=bcc.split("\\;");
                    for (String cd:CC){
                        log.debug("CD--" + cd);
                        if(functions.isValueNull(cd) && ESAPI.validator().isValidInput("email",cd,"Email", 250, false))
                        {
                            msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(cd));
                        }
                    }
                }

                msg.setSubject(subject);
                msg.setSentDate(new java.util.Date());
                msg.setFrom(new InternetAddress(from));
                msg.setContent(message, "text/html");
                Transport.send(msg);
                log.error("Mail Sent");

            }
            catch (MessagingException mex)
            {
                log.error("Exception::::::::", mex);
            }
        }
        catch (Exception e)
        {
            log.error("Exception::::::::", e);
        }
    }

    public void sendHtmlMail_WhiteLabel_WithAttachment(String to, String from, String cc, String bcc, String subject, String message,String host,String port,String userName,String password,String fileName,String filePath,String relayWithAuth,String protocol) throws SystemError
    {
        String contenttype = "text/html";
        try
        {
            log.debug("Inside SendMail");
            Properties props = new Properties();

            props.put("mail.debug", "true");
            props.put("mail.smtp.host", host);

            if((port!=null && !port.equals("")))
                props.put("mail.smtp.port", port);
            else
                props.put("mail.smtp.port", "25");

            props.put("mail.smtp.ssl.trust", "*."+host.substring(host.indexOf(".")+1));
            props.put("mail.smtp.localhost", "127.0.0.1");
            log.debug("Protocol::::" + protocol);
            if(!protocol.isEmpty() && protocol.equals("SSL"))
            {
                props.put("mail.smtp.EnableSSL.enable","true");
                props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.setProperty("mail.smtp.socketFactory.fallback", "false");
            }
            else{
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            }

           /* props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            props.put("mail.smtp.host", host);

            if((port!=null && !port.equals("")))
                props.put("mail.smtp.port", port);
            else
                props.put("mail.smtp.port", "25");

            props.put("mail.smtp.ssl.trust", "*."+host.substring(host.indexOf(".")+1));
            props.put("mail.debug", "true");
            props.put("mail.smtp.localhost", "127.0.0.1");
            props.put("mail.smtp.EnableSSL.enable","true");
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");*/

            Session session1 = null;
            //Relay with Auth
            if("Y".equals(relayWithAuth))
            {
                props.put("mail.smtp.auth", "true");
                Authenticator auth = new SMTPAuthenticator(userName,password);
                session1 = Session.getInstance(props,auth);
            }
            else
            {
                props.put("mail.smtp.auth", "false");
                session1 = Session.getInstance(props);
            }

            MimeMessage msg = new MimeMessage(session1);
            msg.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = InternetAddress.parse(to); // {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, toAddress);
            log.debug("CC--" + cc);
            if (cc != null && cc !="" && !cc.contains(";"))
            {
                log.debug("inside cc first");
                InternetAddress[] ccAddress = InternetAddress.parse(cc);//{new InternetAddress(cc)};
                msg.setRecipients(Message.RecipientType.CC, ccAddress);
            }
            if(cc != null && cc !="" && cc.contains(";")){
                log.debug("inside second-");
                String[] CC=cc.split("\\;");
                for (String cd:CC){
                    log.debug("CD--" + cd);
                    if(functions.isValueNull(cd) && ESAPI.validator().isValidInput("email",cd,"Email", 250, false))
                    {
                        InternetAddress[] ccAddress = InternetAddress.parse(cd);//{new InternetAddress(cc)};
                        msg.addRecipients(Message.RecipientType.CC, ccAddress);
                    }
                }
            }

            /*if (cc != null)
            {
                InternetAddress[] ccAddress = InternetAddress.parse(cc);//{new InternetAddress(cc)};
                msg.setRecipients(Message.RecipientType.CC, ccAddress);
            }*/

            /*if (bcc != null)
            {
                InternetAddress[] bccAddress = InternetAddress.parse(bcc);//{new InternetAddress(bcc)};
                msg.setRecipients(Message.RecipientType.BCC, bccAddress);
            }*/
            if (bcc != null && bcc !="" && !bcc.contains(";"))
            {
                log.debug("inside bcc first");
                InternetAddress[] bccAddress = InternetAddress.parse(bcc);//{new InternetAddress(bcc)};
                msg.setRecipients(Message.RecipientType.BCC, bccAddress);
            }
            if(bcc != null && bcc !="" && bcc.contains(";")){
                log.debug("inside bcc second-");
                String[] BCC=bcc.split("\\;");
                for (String bCC:BCC){
                    log.debug("bCC--" + bCC);
                    if(functions.isValueNull(bCC) && ESAPI.validator().isValidInput("email",bCC,"Email", 250, false))
                    {
                        InternetAddress[] bccAddress = InternetAddress.parse(bCC);//{new InternetAddress(bcc)};
                        msg.addRecipients(Message.RecipientType.BCC, bccAddress);
                    }
                }
            }

            msg.setSubject(subject);
            msg.setSentDate(new java.util.Date());

            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filePath);
            messageBodyPart.setContent(message, contenttype);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(fileName);

            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentBodyPart);
            msg.setContent(multipart);

            Transport.send(msg);
            log.debug("MAIL SENT WITH ATTACHMENT");
            log.debug("Leaving sendmail_with attachment");
        }
        catch (SendFailedException sfe)
        {
            log.error("SendFailException",sfe);
        }
        catch (Exception e)
        {
            log.error("Exception::::::::",e);
        }
    }

    public void sendHtmlMail_WhiteLabel_WithMultipleAttachment(String to, String from, String cc, String bcc, String subject, String message, String host, String port, String userName, String password, List<FileAttachmentVO> fileAttachmentVOList,String relayWithAuth) throws SystemError
    {
        String contenttype = "text/html";
        try
        {
            log.debug("Inside SendMail");
            Properties props = new Properties();

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            props.put("mail.smtp.host", host);

            if((port!=null && !port.equals("")))
                props.put("mail.smtp.port", port);
            else
                props.put("mail.smtp.port", "25");

            props.put("mail.smtp.ssl.trust", "*." + host.substring(host.indexOf(".") + 1));
            props.put("mail.debug", "true");
            props.put("mail.smtp.localhost", "127.0.0.1");
            props.put("mail.smtp.EnableSSL.enable","true");
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");

            Session session1 = null;
            //Relay with Auth
            if("Y".equals(relayWithAuth))
            {
                props.put("mail.smtp.auth", "true");
                Authenticator auth = new SMTPAuthenticator(userName,password);
                session1 = Session.getInstance(props,auth);
            }
            else
            {
                props.put("mail.smtp.auth", "false");
                session1 = Session.getInstance(props);
            }

            MimeMessage msg = new MimeMessage(session1);
            msg.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = InternetAddress.parse(to); // {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, toAddress);

            if (cc != null)
            {
                InternetAddress[] ccAddress = InternetAddress.parse(cc);//{new InternetAddress(cc)};
                msg.setRecipients(Message.RecipientType.CC, ccAddress);
            }

            if (bcc != null)
            {
                InternetAddress[] bccAddress = InternetAddress.parse(bcc);//{new InternetAddress(bcc)};
                msg.setRecipients(Message.RecipientType.BCC, bccAddress);
            }

            msg.setSubject(subject);
            msg.setSentDate(new Date());

            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(message, contenttype);
            multipart.addBodyPart(messageBodyPart);

            for (FileAttachmentVO fileAttachmentVO : fileAttachmentVOList)
            {
                DataSource source = new FileDataSource(fileAttachmentVO.getFilePath());
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(fileAttachmentVO.getFileName());
                multipart.addBodyPart(attachmentBodyPart);
            }

            msg.setContent(multipart);
            Transport.send(msg);
            log.debug("MAIL SENT WITH ATTACHMENT");
            log.debug("Leaving sendmail_with attachment");
        }
        catch (SendFailedException sfe)
        {
            log.error("SendFailException", sfe);
        }
        catch (Exception e)
        {
            log.error("Exception::::::::", e);
        }
    }

    /*public String getPartnerToMailId(MailEventEnum partnerDetail, Hashtable hashtable)
    {
        String toMailId="";
        switch (partnerDetail)
        {
            case PARTNERS_MERCHANT_SALE_TRANSACTION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case CAPTURE_TRANSACTION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case CHARGEBACK_TRANSACTION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case REFUND_TRANSACTION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case BILLING_DESCRIPTOR_CHANGE_INTIMATION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case MERCHANTS_LEVEL_CARD_REGISTRATION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case PARTNERS_LEVEL_CARD_REGISTRATION:
                toMailId=(String) hashtable.get("notifyemail");
                break;

            case PARTNER_REGISTRATION:
                toMailId=(String) hashtable.get("contact_emails");
                break;

            case PARTNERS_MERCHANT_REGISTRATION:
                toMailId=(String) hashtable.get("companyadminid");
                break;

            case PARTNER_CHANGE_PASSWORD:
                toMailId=(String) hashtable.get("contact_emails");
                break;

            case PARTNER_FORGOT_PASSWORD:
                toMailId=(String) hashtable.get("contact_emails");
                break;

            case PARTNER_USER_FORGOT_PASSWORD:
                toMailId=(String) hashtable.get("contact_emails");
                break;

            case MERCHANT_MONITORING_ALERT_TO_PARTNER:
                toMailId = (String) hashtable.get("salesemail");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_SALES:
                toMailId = (String) hashtable.get("salesemail");
                break;

            case MERCHANT_CB_MONITORING_ALERT_TO_PARTNER:
                toMailId = (String) hashtable.get("notifyemail");
                break;

            case MERCHANT_RF_MONITORING_ALERT_TO_PARTNER:
                toMailId = (String) hashtable.get("notifyemail");
                break;

            case MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD:
                toMailId = (String) hashtable.get("fraudemailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH:
                toMailId = (String) hashtable.get("notifyemail");
                break;

            default:
                toMailId=(String)hashtable.get("notifyemail");
        }
        return toMailId;
    }
*/
    /*
     * Description:Added new method for event base email segregation-Partner
    */
    public MailContactVO getPartnerToMailId(MailEventEnum partnerDetail, Hashtable hashtable,String partnerId)
    {
        log.debug("Inside getPartnerToMailId::::" + partnerId);
        String toMailId="";
        String name="";
        String partnerToCcMailId="";
        PartnerDAO partnerDAO=new PartnerDAO();
        PartnerEmailNotificationVO partnerEmailNotificationVO=null;
        MailContactVO mailContactVO=new MailContactVO();
        try
        {
            partnerEmailNotificationVO=partnerDAO.getEmailNotificationDetails(partnerId);
        }
        catch (SQLException e)
        {
            log.error("SQLException :::::::", e);
        }
        if(partnerEmailNotificationVO!=null)
        {
            switch (partnerDetail)
            {
                case PARTNERS_MERCHANT_SALE_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getMerchantSalesTransaction()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }else
                        return mailContactVO;
                    break;

                case CAPTURE_TRANSACTION:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case CHARGEBACK_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getChargebackTransaction()))
                    {
                        name = (String) hashtable.get("chargebackcontactname");
                        toMailId = (String) hashtable.get("chargebackemailid");
                        partnerToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case REFUND_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getRefundTransaction()))
                    {
                        name = (String) hashtable.get("refundcontactname");
                        toMailId = (String) hashtable.get("refundemailid");
                        partnerToCcMailId = (String) hashtable.get("refund_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case PAYOUT_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesPayoutTransaction()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case BILLING_DESCRIPTOR_CHANGE_INTIMATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesBillingDescriptor()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case MERCHANTS_LEVEL_CARD_REGISTRATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesMerchantCardRegistration()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case PARTNERS_LEVEL_CARD_REGISTRATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesPartnerCardRegistration()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case PARTNER_REGISTRATION:
                    name = (String) hashtable.get("contact_persons");
                    toMailId = (String) hashtable.get("contact_emails");
                    partnerToCcMailId = (String) hashtable.get("contact_ccmailid");
                    break;

                case PARTNERS_MERCHANT_REGISTRATION:
                    //toMailId=(String) hashtable.get("companyadminid");
                    name = (String) hashtable.get("contact_persons");
                    toMailId = (String) hashtable.get("contact_emails");
                    partnerToCcMailId = (String) hashtable.get("contact_ccmailid");
                    break;

                case PARTNER_CHANGE_PASSWORD:
                    name = (String) hashtable.get("contact_persons");
                    toMailId = (String) hashtable.get("contact_emails");
                    break;

                case PARTNER_FORGOT_PASSWORD:
                    name = (String) hashtable.get("contact_persons");
                    toMailId = (String) hashtable.get("contact_emails");
                    partnerToCcMailId = (String) hashtable.get("contact_ccmailid");
                    break;

                case PARTNER_USER_FORGOT_PASSWORD:
                    name = (String) hashtable.get("contact_persons");
                    toMailId = (String) hashtable.get("contact_emails");
                    partnerToCcMailId = (String) hashtable.get("contact_ccmailid");
                    break;

                case MERCHANT_MONITORING_ALERT_TO_PARTNER:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANT_CB_MONITORING_ALERT_TO_PARTNER:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    partnerToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;

                case MERCHANT_RF_MONITORING_ALERT_TO_PARTNER:
                    name = (String) hashtable.get("refundcontactname");
                    toMailId = (String) hashtable.get("refundemailid");
                    partnerToCcMailId = (String) hashtable.get("refund_ccemailid");
                    break;

                case MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    partnerToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    partnerToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_SALES:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_CB:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    partnerToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;

                case MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_RF:
                    name = (String) hashtable.get("refundcontactname");
                    toMailId = (String) hashtable.get("refundemailid");
                    partnerToCcMailId = (String) hashtable.get("refund_ccemailid");
                    break;

                case MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_FRAUD:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    partnerToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_TECH:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    partnerToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_SALES:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_CB:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    partnerToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_RF:
                    name = (String) hashtable.get("refundcontactname");
                    toMailId = (String) hashtable.get("refundemailid");
                    partnerToCcMailId = (String) hashtable.get("refund_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_FRAUD:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    partnerToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_TECH:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    partnerToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;
                case FRAUDRULE_CHANGE_INTIMATION:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    partnerToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getFraudFailedTransaction()))
                    {
                        name = (String) hashtable.get("fraudcontactname");
                        toMailId = (String) hashtable.get("fraudemailid");
                        partnerToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case FRAUD_NOTIFICATION:
                    toMailId = (String) hashtable.get("fraudemailid");
                    name = (String) hashtable.get("fraudcontactname");
                    partnerToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_APPLICATION_FORM_STATUS_CHANGE:
                    name = (String) hashtable.get("contact_persons");
                    toMailId = (String) hashtable.get("contact_emails");
                    break;

                case ADMIN_FAILED_TRANSACTION_NOTIFICATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesAdminFailedTransaction())){
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        partnerToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }else
                        return mailContactVO;
                    break;

                /*default:
                    toMailId = (String) hashtable.get("notifyemail");
                    name = (String) hashtable.get("notifycontactname");
                    partnerToCcMailId = (String) hashtable.get("notify_ccemailid");*/
            }
        }

        Functions functions=new Functions();
        if(!functions.isValueNull(toMailId) || !functions.isValueNull(name))
        {
            log.debug("inside function null::::");
            name=(String)hashtable.get("notifycontactname");
            toMailId=(String)hashtable.get("notifyemail");
            partnerToCcMailId=(String) hashtable.get("notify_ccemailid");
        }
        mailContactVO.setContactMailId(toMailId);
        mailContactVO.setContactCcMailId(partnerToCcMailId);
        mailContactVO.setName(name);
        return mailContactVO;
    }

    public MailContactVO getMerchantToMailIdBasedOnEvent(MailEventEnum merchantDetails, Hashtable hashtable)
    {
        String merchantToMailId="";
        String merchantContactName="";
        switch (merchantDetails)
        {
            case PARTNERS_MERCHANT_CHANGE_PASSWORD:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");
                break;

            case PARTNERS_MERCHANT_CHANGE_IN_PROFILE_DETAILS:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");
                break;

            case PARTNERS_MERCHANT_FORGOT_PASSWORD:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");
                break;

            case BILLING_DESCRIPTOR_CHANGE_INTIMATION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case PARTNERS_MERCHANT_REGISTRATION:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");

                break;

            case MERCHANTS_LEVEL_CARD_REGISTRATION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case MERCHANT_PAYOUT_ALERT_MAIL:
                merchantToMailId=(String) hashtable.get("salesmailid");
                merchantContactName=(String) hashtable.get("sales_contactperson");
                break;

            case GENERATING_INVOICE_BY_MERCHANT:
                merchantToMailId=(String) hashtable.get("billingmailid");
                merchantContactName=(String) hashtable.get("billing_contactperson");
                break;

            case REFUND_TRANSACTION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case CHARGEBACK_TRANSACTION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case PARTNERS_MERCHANT_SALE_TRANSACTION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case CAPTURE_TRANSACTION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION:
                merchantToMailId=(String) hashtable.get("notifyemail");
                merchantContactName=(String) hashtable.get("notify_contactperson");
                break;

            case FRAUD_NOTIFICATION:
                merchantToMailId = (String) hashtable.get("fraudemailid");
                merchantContactName = (String) hashtable.get("fraudcontactname");
                break;

            default:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");
        }

        Functions functions=new Functions();
        if(!functions.isValueNull(merchantToMailId) || !functions.isValueNull(merchantContactName))
        {
            merchantToMailId=(String)hashtable.get("contact_emails");
            merchantContactName=(String)hashtable.get("contact_persons");
        }
        MailContactVO mailContactVO=new MailContactVO();
        mailContactVO.setContactMailId(merchantToMailId);
        mailContactVO.setName(merchantContactName);
        return mailContactVO;
    }

    /*
     * Description:Added new method for event base email segregation with CC-Merchant
    */
    public MailContactVO getMerchantToMailIdBasedOnEventWithCC(HashMap<MailPlaceHolder,String> vMailContent,MailEventEnum merchantDetails, Hashtable hashtable)
    {
        String merchantToMailId="";
        String merchantToCcMailId="";
        String merchantToBccMailId="";
        String merchantContactName="";
        MailContactVO mailContactVO=new MailContactVO();
        switch (merchantDetails)
        {

            case PARTNERS_MERCHANT_CHANGE_PASSWORD:
                if("Y".equals(hashtable.get("merchantChangePassword")))
                {
                    merchantToMailId = (String) hashtable.get("contact_emails");
                    merchantContactName = (String) hashtable.get("contact_persons");
                    merchantToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("maincontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case PARTNERS_MERCHANT_CHANGE_IN_PROFILE_DETAILS:
                if("Y".equals(hashtable.get("merchantChangeProfile")))
                {
                    merchantToMailId = (String) hashtable.get("contact_emails");
                    merchantContactName = (String) hashtable.get("contact_persons");
                    merchantToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("maincontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case PARTNERS_MERCHANT_FORGOT_PASSWORD:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");
                merchantToCcMailId=(String) hashtable.get("maincontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("maincontact_bccmailid");
                break;

            case BILLING_DESCRIPTOR_CHANGE_INTIMATION:
                merchantToMailId=(String) hashtable.get("salescontact_mailid");
                merchantContactName=(String) hashtable.get("salescontact_name");
                merchantToCcMailId=(String) hashtable.get("salescontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                break;

            case PARTNERS_MERCHANT_REGISTRATION:
                if("Y".equals(hashtable.get("merchantRegistrationMail")))
                {
                    merchantToMailId = (String) hashtable.get("contact_emails");
                    merchantContactName = (String) hashtable.get("contact_persons");
                    merchantToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("maincontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case MERCHANTS_LEVEL_CARD_REGISTRATION:
                if("Y".equals(hashtable.get("cardRegistration")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case MERCHANT_PAYOUT_ALERT_MAIL:
                if("Y".equals(hashtable.get("payoutReport")))
                {
                    merchantToMailId = (String) hashtable.get("billingcontact_mailid");
                    merchantContactName = (String) hashtable.get("billingcontact_name");
                    merchantToCcMailId = (String) hashtable.get("billingcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("billingcontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case GENERATING_INVOICE_BY_MERCHANT:
                if("Y".equals(hashtable.get("transactionInvoice")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case REFUND_TRANSACTION:
                if("Y".equals(hashtable.get("refundMail")))
                {
                    merchantToMailId = (String) hashtable.get("refundcontact_mailid");
                    merchantContactName = (String) hashtable.get("refundcontact_name");
                    merchantToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("refundcontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case PAYOUT_TRANSACTION:
                if(vMailContent.get("Status")!="fail" && "Y".equals(hashtable.get("transactionPayoutSuccess")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else if(vMailContent.get("Status")=="fail" && "Y".equals(hashtable.get("transactionPayoutFail")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case CHARGEBACK_TRANSACTION:
                if("Y".equals(hashtable.get("chargebackMail")))
                {
                    merchantToMailId = (String) hashtable.get("cbcontact_mailid");
                    merchantContactName = (String) hashtable.get("cbcontact_name");
                    merchantToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("cbcontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case RECON_TRANSACTION:
                log.debug("inside RECON_TRANSACTION:::" + vMailContent.get("mailForEvent"));
                String mailForEvent=vMailContent.get("mailForEvent");
                if("Y".equals(hashtable.get("refundReconMail")) && mailForEvent.equals("refundMail"))
                {
                    merchantToMailId = (String) hashtable.get("refundcontact_mailid");
                    merchantContactName = (String) hashtable.get("refundcontact_name");
                    merchantToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("refundcontact_bccmailid");
                }else if("Y".equals(hashtable.get("successReconMail")) && mailForEvent.equals("transactionMail")){
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }else if("Y".equals(hashtable.get("chargebackReconMail")) && mailForEvent.equals("chargebackMail")){
                    merchantToMailId = (String) hashtable.get("cbcontact_mailid");
                    merchantContactName = (String) hashtable.get("cbcontact_name");
                    merchantToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("cbcontact_bccmailid");
                }else if("Y".equals(hashtable.get("payoutReconMail")) && mailForEvent.equals("payoutMail")){
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case PARTNERS_MERCHANT_SALE_TRANSACTION:
                if(vMailContent.get(MailPlaceHolder.STATUS)!="fail" && "Y".equals(hashtable.get("transactionSuccessfulMail")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else if(vMailContent.get(MailPlaceHolder.STATUS)=="fail" && "Y".equals(hashtable.get("transactionFailMail")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case CAPTURE_TRANSACTION:
                if("Y".equals(hashtable.get("transactionCapture")))
                {
                    merchantToMailId = (String) hashtable.get("salescontact_mailid");
                    merchantContactName = (String) hashtable.get("salescontact_name");
                    merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION:
                if("Y".equals(hashtable.get("fraudFailedTxn")))
                {
                    merchantToMailId=(String) hashtable.get("fraudcontact_mailid");
                    merchantContactName=(String) hashtable.get("fraudcontact_name");
                    merchantToCcMailId=(String) hashtable.get("fraudcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case FRAUDRULE_CHANGE_INTIMATION:
                merchantToMailId=(String) hashtable.get("fraudcontact_mailid");
                merchantContactName=(String) hashtable.get("fraudcontact_name");
                merchantToCcMailId=(String) hashtable.get("fraudcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                break;

            case FRAUD_NOTIFICATION:

                merchantToMailId = (String) hashtable.get("fraudcontact_mailid");
                merchantContactName = (String) hashtable.get("fraudcontact_name");
                merchantToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");

                break;

            case MEMBER_DAILY_STATUS_FRAUD_REPORT:
                if("Y".equals(hashtable.get("dailyFraudReport")))
                {
                    merchantToMailId = (String) hashtable.get("fraudcontact_mailid");
                    merchantContactName = (String) hashtable.get("fraudcontact_name");
                    merchantToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case HIGH_RISK_REFUND_TRANSACTION_INTIMATION:
                if("Y".equals(hashtable.get("highRiskRefunds")))
                {
                    merchantToMailId = (String) hashtable.get("fraudcontact_mailid");
                    merchantContactName = (String) hashtable.get("fraudcontact_name");
                    merchantToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                    merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                }
                else
                    return mailContactVO;
                break;

            case MERCHANT_MONITORING_ALERT_TO_MERCHANT:
                merchantToMailId = (String) hashtable.get("salescontact_mailid");
                merchantContactName = (String) hashtable.get("salescontact_name");
                merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");

                break;

            case MERCHANT_CB_MONITORING_ALERT_TO_MERCHANT:
                merchantToMailId = (String) hashtable.get("cbcontact_mailid");
                merchantContactName = (String) hashtable.get("cbcontact_name");
                merchantToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("cbcontact_bccmailid");

                break;

            case MERCHANT_RF_MONITORING_ALERT_TO_MERCHANT:
                merchantToMailId = (String) hashtable.get("refundcontact_mailid");
                merchantContactName = (String) hashtable.get("refundcontact_name");
                merchantToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("refundcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_MERCHANT_FRAUD:
                merchantToMailId = (String) hashtable.get("fraudcontact_mailid");
                merchantContactName = (String) hashtable.get("fraudcontact_name");
                merchantToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_MERCHANT_TECH:
                merchantToMailId = (String) hashtable.get("technicalcontact_mailid");
                merchantContactName = (String) hashtable.get("technicalcontact_name");
                merchantToCcMailId = (String) hashtable.get("technicalcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("technicalcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_SALES:
                merchantToMailId = (String) hashtable.get("salescontact_mailid");
                merchantContactName = (String) hashtable.get("salescontact_name");
                merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_CB:
                merchantToMailId = (String) hashtable.get("cbcontact_mailid");
                merchantContactName = (String) hashtable.get("cbcontact_name");
                merchantToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("cbcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_RF:
                merchantToMailId = (String) hashtable.get("refundcontact_mailid");
                merchantContactName = (String) hashtable.get("refundcontact_name");
                merchantToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("refundcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_FRAUD:
                merchantToMailId = (String) hashtable.get("fraudcontact_mailid");
                merchantContactName = (String) hashtable.get("fraudcontact_name");
                merchantToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_TECH:
                merchantToMailId = (String) hashtable.get("technicalcontact_mailid");
                merchantContactName = (String) hashtable.get("technicalcontact_name");
                merchantToCcMailId = (String) hashtable.get("technicalcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("technicalcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_SALES:
                merchantToMailId = (String) hashtable.get("salescontact_mailid");
                merchantContactName = (String) hashtable.get("salescontact_name");
                merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_CB:
                merchantToMailId = (String) hashtable.get("cbcontact_mailid");
                merchantContactName = (String) hashtable.get("cbcontact_name");
                merchantToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("cbcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_RF:
                merchantToMailId = (String) hashtable.get("refundcontact_mailid");
                merchantContactName = (String) hashtable.get("refundcontact_name");
                merchantToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("refundcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_FRAUD:
                merchantToMailId = (String) hashtable.get("fraudcontact_mailid");
                merchantContactName = (String) hashtable.get("fraudcontact_name");
                merchantToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("fraudcontact_bccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_TECH:
                merchantToMailId = (String) hashtable.get("technicalcontact_mailid");
                merchantContactName = (String) hashtable.get("technicalcontact_name");
                merchantToCcMailId = (String) hashtable.get("technicalcontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("technicalcontact_bccmailid");
                break;

            case MERCHANT_APPLICATION_FORM_STATUS_CHANGE:
                merchantToMailId = (String) hashtable.get("contact_emails");
                merchantContactName = (String) hashtable.get("contact_persons");
                merchantToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                merchantToBccMailId = (String) hashtable.get("maincontact_bccmailid");
                break;

            /*default:
                merchantToMailId=(String) hashtable.get("contact_emails");
                merchantContactName=(String) hashtable.get("contact_persons");
                merchantToCcMailId=(String) hashtable.get("maincontact_ccmailid");*/
        }

        Functions functions=new Functions();
        if(!functions.isValueNull(merchantToMailId) || !functions.isValueNull(merchantContactName))
        {
            merchantToMailId=(String)hashtable.get("contact_emails");
            merchantContactName=(String)hashtable.get("contact_persons");
            merchantToCcMailId=(String) hashtable.get("maincontact_ccmailid");
            merchantToBccMailId = (String) hashtable.get("maincontact_bccmailid");
        }
        //MailContactVO mailContactVO=new MailContactVO();
        mailContactVO.setContactMailId(merchantToMailId);
        mailContactVO.setContactCcMailId(merchantToCcMailId);
        mailContactVO.setContactBccMailId(merchantToBccMailId);
        mailContactVO.setName(merchantContactName);
        return mailContactVO;
    }

    public MailContactVO getCustomerToMailIdBasedOnEventWithCC(HashMap<MailPlaceHolder,String> vMailContent,MailEventEnum merchantDetails, Hashtable hashtable)
    {
        String merchantToMailId="";
        String merchantContactName="";
        MailContactVO mailContactVO=new MailContactVO();
        String trackingId=vMailContent.get(MailPlaceHolder.TRACKINGID);
        log.debug("TrackingId:::" + trackingId);
        TransactionDetailsVO transactionDetails=null;
        if(functions.isValueNull(trackingId)){
            transactionDetails=getTransactionDetails(trackingId);
        }
        if(transactionDetails!=null)
        {
            switch (merchantDetails)
            {
                case MERCHANTS_LEVEL_CARD_REGISTRATION:
                    if ("Y".equals(hashtable.get("customerTokenizationMail")))
                    {
                        merchantToMailId = transactionDetails.getEmailaddr();
                        merchantContactName = transactionDetails.getName();
                        //merchantToCcMailId = (String) hashtable.get("salescontact_ccmailid");
//                    merchantToBccMailId = (String) hashtable.get("salescontact_bccmailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case REFUND_TRANSACTION:
                    if ("Y".equals(hashtable.get("customerRefundMail")))
                    {
                        merchantToMailId = transactionDetails.getEmailaddr();
                        merchantContactName = transactionDetails.getName();
                    }
                    else
                        return mailContactVO;
                    break;

                case PAYOUT_TRANSACTION:
                    if (vMailContent.get("Status") != "fail" && "Y".equals(hashtable.get("customerTransactionPayoutSuccess")))
                    {
                        merchantToMailId = transactionDetails.getEmailaddr();
                        merchantContactName = transactionDetails.getName();
                    }
                    else if (vMailContent.get("Status") == "fail" && "Y".equals(hashtable.get("customerTransactionPayoutFail")))
                    {
                        merchantToMailId = transactionDetails.getEmailaddr();
                        merchantContactName = transactionDetails.getName();
                    }
                    else
                        return mailContactVO;
                    break;

                case PARTNERS_MERCHANT_SALE_TRANSACTION:
                    if (vMailContent.get(MailPlaceHolder.STATUS) != "fail" && "Y".equals(hashtable.get("customerTransactionSuccessfulMail")))
                    {
                        merchantToMailId = transactionDetails.getEmailaddr();
                        merchantContactName = transactionDetails.getName();
                    }
                    else if (vMailContent.get(MailPlaceHolder.STATUS) == "fail" && "Y".equals(hashtable.get("customerTransactionFailMail")))
                    {
                        merchantToMailId = transactionDetails.getEmailaddr();
                        merchantContactName = transactionDetails.getName();
                    }
                    else
                        return mailContactVO;
                    break;
            }
        }
        mailContactVO.setContactMailId(merchantToMailId);
        mailContactVO.setName(merchantContactName);
        return mailContactVO;
    }

    /*
     * Description:Added new method for event base email segregation-Admin
    */
    public MailContactVO getAdminToMailId(MailEventEnum adminEmailEvent, Hashtable hashtable,String partnerId)
    {
        String name = "";
        String toMailId ="";
        String adminToCcMailId="";
        PartnerDAO partnerDAO=new PartnerDAO();
        PartnerEmailNotificationVO partnerEmailNotificationVO=null;
        MailContactVO mailContactVO=new MailContactVO();
        try
        {
            partnerEmailNotificationVO=partnerDAO.getEmailNotificationDetails(partnerId);
        }
        catch (SQLException e)
        {
            log.error("SQLException :::::::", e);
        }
        if(partnerEmailNotificationVO!=null)
        {
            switch (adminEmailEvent)
            {
                case ALERT_DAILY_STATUS_SUMMERY_REPORT:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case CAPTURE_TRANSACTION:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case ALERT_WEEKLY_CHARGEBACK_SUMMERY_REPORT:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    adminToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;


                case FRAUD_NOTIFICATION:
                    toMailId = (String) hashtable.get("fraudemailid");
                    name = (String) hashtable.get("fraudcontactname");
                    adminToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case HIGH_RISK_FRAUD_TRANSACTION_INTIMATION:
                    name = (String) hashtable.get("notifycontactname");
                    toMailId = (String) hashtable.get("notifyemail");
                    adminToCcMailId = (String) hashtable.get("notify_ccemailid");
                    break;

                case BILLING_DESCRIPTOR_CHANGE_INTIMATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesBillingDescriptor()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case BANK_CONNECTION_CHECKING_REPORT:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    adminToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case ADMIN_STATUS_SYNCRONIZATION_CRON:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case EXCEPTION_DETAILS:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    adminToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case REJECTED_TRANSACTION:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    adminToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case ADMIN_MAIL:
                    name = (String) hashtable.get("notifycontactname");
                    toMailId = (String) hashtable.get("notifyemail");
                    adminToCcMailId = (String) hashtable.get("notify_ccemailid");
                    break;

                case MERCHANT_PAYOUT_ALERT_MAIL:
                    name = (String) hashtable.get("billingcontactname");
                    toMailId = (String) hashtable.get("billingemail");
                    adminToCcMailId = (String) hashtable.get("billing_ccemailid");
                    break;

                case PARTNERS_MERCHANT_REGISTRATION:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case PARTNER_REGISTRATION:
                    toMailId = (String) hashtable.get("salesemail");
                    name = (String) hashtable.get("salescontactname");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANTS_LEVEL_CARD_REGISTRATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesMerchantCardRegistration()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case PARTNERS_MERCHANT_SALE_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getMerchantSalesTransaction()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case ADMIN_FAILED_TRANSACTION_NOTIFICATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesAdminFailedTransaction()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case REFUND_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getRefundTransaction()))
                    {
                        name = (String) hashtable.get("refundcontactname");
                        toMailId = (String) hashtable.get("refundemailid");
                        adminToCcMailId = (String) hashtable.get("refund_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case PAYOUT_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesPayoutTransaction()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case CHARGEBACK_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getChargebackTransaction()))
                    {
                        name = (String) hashtable.get("chargebackcontactname");
                        toMailId = (String) hashtable.get("chargebackemailid");
                        adminToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case PARTNERS_LEVEL_CARD_REGISTRATION:
                    if ("Y".equals(partnerEmailNotificationVO.getSalesPartnerCardRegistration()))
                    {
                        name = (String) hashtable.get("salescontactname");
                        toMailId = (String) hashtable.get("salesemail");
                        adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case MERCHANT_MONITORING_ALERT_TO_ADMIN:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANT_CB_MONITORING_ALERT_TO_ADMIN:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    adminToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;

                case MERCHANT_RF_MONITORING_ALERT_TO_ADMIN:
                    name = (String) hashtable.get("refundcontactname");
                    toMailId = (String) hashtable.get("refundemailid");
                    adminToCcMailId = (String) hashtable.get("refund_ccemailid");
                    break;

                case MERCHANT_FRAUD_MONITORING_ALERT_TO_ADMIN:
                    toMailId = (String) hashtable.get("fraudemailid");
                    name = (String) hashtable.get("fraudcontactname");
                    adminToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_TECH_MONITORING_ALERT_TO_ADMIN:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    adminToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_SALES:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_CB:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    adminToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_RF:
                    name = (String) hashtable.get("refundcontactname");
                    toMailId = (String) hashtable.get("refundemailid");
                    adminToCcMailId = (String) hashtable.get("refund_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_FRAUD:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    adminToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_TECH:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    adminToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;

                case MERCHANT_MONITORING_SUSPENSION_TO_ADMIN:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case MERCHANT_CB_MONITORING_SUSPENSION_TO_ADMIN:
                    name = (String) hashtable.get("chargebackcontactname");
                    toMailId = (String) hashtable.get("chargebackemailid");
                    adminToCcMailId = (String) hashtable.get("chargeback_ccemailid");
                    break;

                case MERCHANT_RF_MONITORING_SUSPENSION_TO_ADMIN:
                    name = (String) hashtable.get("refundcontactname");
                    toMailId = (String) hashtable.get("refundemailid");
                    adminToCcMailId = (String) hashtable.get("refund_ccemailid");
                    break;

                case MERCHANT_FRAUD_MONITORING_SUSPENSION_TO_ADMIN:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    adminToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case MERCHANT_TECH_MONITORING_SUSPENSION_TO_ADMIN:
                    name = (String) hashtable.get("technicalcontactname");
                    toMailId = (String) hashtable.get("technicalemailid");
                    adminToCcMailId = (String) hashtable.get("technical_ccemailid");
                    break;
                case FRAUDRULE_CHANGE_INTIMATION:
                    name = (String) hashtable.get("fraudcontactname");
                    toMailId = (String) hashtable.get("fraudemailid");
                    adminToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    break;

                case PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION:
                    if ("Y".equals(partnerEmailNotificationVO.getFraudFailedTransaction()))
                    {
                        name = (String) hashtable.get("fraudcontactname");
                        toMailId = (String) hashtable.get("fraudemailid");
                        adminToCcMailId = (String) hashtable.get("fraud_ccemailid");
                    }
                    else
                        return mailContactVO;
                    break;

                case ADMIN_AUTHSTARTED_CRON_REPORT:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case ADMIN_MARKEDFORREVERSAL_CRON_REPORT:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case ADMIN_AUTHFAILED_CRON_REPORT:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case ADMIN_PAYOUTSTARTED_CRON_REPORT:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                case ADMIN_PAYOUTFAILED_CRON_REPORT:
                    name = (String) hashtable.get("salescontactname");
                    toMailId = (String) hashtable.get("salesemail");
                    adminToCcMailId = (String) hashtable.get("sales_ccemailid");
                    break;

                default:
                    toMailId = (String) hashtable.get("notifyemail");
                    name = (String) hashtable.get("notifycontactname");
                    adminToCcMailId = (String) hashtable.get("notify_ccemailid");
            }
        }

        Functions functions=new Functions();
        if(!functions.isValueNull(toMailId) || !functions.isValueNull(name))
        {
            log.debug("inside function null of Admin::::");
            name=(String)hashtable.get("notifycontactname");
            toMailId=(String)hashtable.get("notifyemail");
            adminToCcMailId=(String) hashtable.get("notify_ccemailid");
        }

        mailContactVO.setContactMailId(toMailId);
        mailContactVO.setContactCcMailId(adminToCcMailId);
        mailContactVO.setName(name);
        return mailContactVO;
    }

    /*
     * Description:Added new method for event base email segregation-Agent
     */
    public MailContactVO getAgentToMailIdBasedOnEventWithCC(MailEventEnum agentDetails, Hashtable hashtable)
    {
        String agentContactName="";
        String agentToMailId="";
        String agentToCcMailId="";
        switch (agentDetails)
        {
            case AGENT_FORGOT_PASSWORD:
                agentContactName=(String) hashtable.get("contact_persons");
                agentToMailId=(String) hashtable.get("contact_emails");
                agentToCcMailId=(String) hashtable.get("maincontact_ccmailid");
                break;

            case AGENT_CHANGE_PASSWORD:
                agentContactName=(String) hashtable.get("contact_persons");
                agentToMailId=(String) hashtable.get("contact_emails");
                agentToCcMailId=(String) hashtable.get("maincontact_ccmailid");
                break;

            case AGENT_REGISTRATION:
                agentContactName=(String) hashtable.get("contact_persons");
                agentToMailId=(String) hashtable.get("contact_emails");
                agentToCcMailId=(String) hashtable.get("maincontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT:
                agentContactName=(String) hashtable.get("salescontact_name");
                agentToMailId=(String) hashtable.get("salesemail");
                agentToCcMailId=(String) hashtable.get("salescontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_CB:
                agentContactName=(String) hashtable.get("cbcontact_name");
                agentToMailId=(String) hashtable.get("cbcontact_mailid");
                agentToCcMailId=(String) hashtable.get("cbcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_RF:
                agentContactName=(String) hashtable.get("refundcontact_name");
                agentToMailId=(String) hashtable.get("refundcontact_mailid");
                agentToCcMailId=(String) hashtable.get("refundcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_FRAUD:
                agentContactName=(String) hashtable.get("fraudcontact_name");
                agentToMailId=(String) hashtable.get("fraudcontact_mailid");
                agentToCcMailId=(String) hashtable.get("fraudcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_TECH:
                agentContactName=(String) hashtable.get("technicalcontact_name");
                agentToMailId=(String) hashtable.get("technicalcontact_mailid");
                agentToCcMailId=(String) hashtable.get("technicalcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT:
                agentContactName=(String) hashtable.get("salescontact_name");
                agentToMailId=(String) hashtable.get("salesemail");
                agentToCcMailId=(String) hashtable.get("salescontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_CB:
                agentContactName=(String) hashtable.get("cbcontact_name");
                agentToMailId=(String) hashtable.get("cbcontact_mailid");
                agentToCcMailId=(String) hashtable.get("cbcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_RF:
                agentContactName=(String) hashtable.get("refundcontact_name");
                agentToMailId=(String) hashtable.get("refundcontact_mailid");
                agentToCcMailId=(String) hashtable.get("refundcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_FRAUD:
                agentContactName=(String) hashtable.get("fraudcontact_name");
                agentToMailId=(String) hashtable.get("fraudcontact_mailid");
                agentToCcMailId=(String) hashtable.get("fraudcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_TECH:
                agentContactName=(String) hashtable.get("technicalcontact_name");
                agentToMailId=(String) hashtable.get("technicalcontact_mailid");
                agentToCcMailId=(String) hashtable.get("technicalcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_SALES:
                agentContactName=(String) hashtable.get("salescontact_name");
                agentToMailId=(String) hashtable.get("salesemail");
                agentToCcMailId=(String) hashtable.get("salescontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_CB:
                agentContactName=(String) hashtable.get("cbcontact_name");
                agentToMailId=(String) hashtable.get("cbcontact_mailid");
                agentToCcMailId=(String) hashtable.get("cbcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_RF:
                agentContactName=(String) hashtable.get("refundcontact_name");
                agentToMailId=(String) hashtable.get("refundcontact_mailid");
                agentToCcMailId=(String) hashtable.get("refundcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_FRAUD:
                agentContactName=(String) hashtable.get("fraudcontact_name");
                agentToMailId=(String) hashtable.get("fraudcontact_mailid");
                agentToCcMailId=(String) hashtable.get("fraudcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_TECH:
                agentContactName=(String) hashtable.get("technicalcontact_name");
                agentToMailId=(String) hashtable.get("technicalcontact_mailid");
                agentToCcMailId=(String) hashtable.get("technicalcontact_ccmailid");
                break;

            default:
                agentToMailId=(String) hashtable.get("contact_emails");
                agentContactName=(String) hashtable.get("contact_persons");
                agentToCcMailId=(String) hashtable.get("maincontact_ccmailid");
        }
        Functions functions=new Functions();
        if(!functions.isValueNull(agentToMailId) || !functions.isValueNull(agentContactName))
        {
            agentToMailId=(String)hashtable.get("contact_emails");
            agentContactName=(String)hashtable.get("contact_persons");
            agentToCcMailId=(String) hashtable.get("maincontact_ccmailid");
        }

        MailContactVO mailContactVO=new MailContactVO();
        mailContactVO.setContactMailId(agentToMailId);
        mailContactVO.setContactCcMailId(agentToCcMailId);
        mailContactVO.setName(agentContactName);
        return mailContactVO;
    }

    public void sendSettlementCron(StringBuffer data, String displayName)
            throws SystemError
    {
        SendTransactionEventMailUtil sendTransactionEventMailUtil = new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_SETTLEMENT_REPORT, "", displayName, data.toString(), null);
    }

    public List<PartnerDetailsVO> getListOfHostUrl(String hostUrl)
    {
        List<PartnerDetailsVO> partnerDetailsVOList=new ArrayList<>();
        PartnerDetailsVO partnerDetailsVO=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            String query="SELECT hosturl FROM partners WHERE hosturl=?";
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,hostUrl);
            rs=pstmt.executeQuery();
            while (rs.next()){
                partnerDetailsVO=new PartnerDetailsVO();
                partnerDetailsVO.setHostUrl(rs.getString("hosturl"));
                partnerDetailsVOList.add(partnerDetailsVO);
            }
        }catch (Exception e){
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return partnerDetailsVOList;
    }
    public TransactionDetailsVO getTransactionDetails(String trackingId){
        TransactionDetailsVO transactionDetailsVO=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        Connection conn=null;
        try
        {
            conn=Database.getConnection();
            String query="SELECT NAME,emailaddr FROM transaction_common WHERE trackingid=?";
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,trackingId);
            rs=pstmt.executeQuery();
            if (rs.next()){
                transactionDetailsVO=new TransactionDetailsVO();
                transactionDetailsVO.setName(rs.getString("NAME"));
                transactionDetailsVO.setEmailaddr(rs.getString("emailaddr"));
            }
        }catch (Exception e){
            log.error("Exception::::",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return  transactionDetailsVO;
    }
}