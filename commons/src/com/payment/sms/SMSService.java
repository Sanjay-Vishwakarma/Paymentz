package com.payment.sms;

import com.directi.pg.*;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.payment.Mail.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 6/5/15
 * Time: 8:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSService
{
    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.template");
    static Logger log = new Logger(SMSService.class.getName());
    private static HashMap<Integer, String> SMSENTITIES = new HashMap();

    static
    {
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String qry = "select * from mailentity";
            PreparedStatement preparedStatement = con.prepareStatement(qry);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                SMSENTITIES.put(resultSet.getInt("mailEntityId"), resultSet.getString("placeHolderTagName"));
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException::::", e);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    String templatesFilePath = RBTemplate.getString("SMSTEMPLATEPATH");

    public String getMailEntityPlaceHolder(int mailEntityId)
    {
        return SMSENTITIES.get(mailEntityId);
    }

    public void sendSMS(MailEventEnum smsEventEnum, HashMap<MailPlaceHolder, String> smsContent)
    {
        log.error("enum---"+smsEventEnum+"--content---"+smsContent);
        Connection conn = null;
        String sms_service="N";
        try
        {
            conn = Database.getConnection();
            String qry = "SELECT mapping_id,event_id,from_entity_id,to_entity_id,template_id,`subject` FROM sms_template_event_entity_mapping WHERE event_id IN (SELECT mailEventId FROM mailevent WHERE mailEventName=?)";
            PreparedStatement preparedStatement = conn.prepareStatement(qry);
            preparedStatement.setString(1, smsEventEnum.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            log.debug("sms details query---"+preparedStatement);
            while (resultSet.next())
            {
                int fromEntityId                = resultSet.getInt("from_entity_id");
                int toEntityId                  = resultSet.getInt("to_entity_id");
                MailEntityEnum mailEntityEnum   = MailEntityEnum.valueOf(getMailEntityPlaceHolder(toEntityId));
                String vMemberId                = smsContent.get(MailPlaceHolder.TOID);
                if((smsEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION)) || (smsEventEnum.equals(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION)) || (smsEventEnum.equals(MailEventEnum.REFUND_TRANSACTION)) && vMemberId != null)
                {
                    String isCustomerMailFlag   = "SELECT smsactivation,customersmsactivation FROM members WHERE memberid=?";
                    PreparedStatement p         = conn.prepareStatement(isCustomerMailFlag);
                    p.setString(1, vMemberId);
                    ResultSet rs = p.executeQuery();
                    if (rs.next())
                    {
                        String customerSMSActivation = rs.getString("customersmsactivation");
                        if (mailEntityEnum.equals(MailEntityEnum.CustomerEmail))
                        {
                            if (customerSMSActivation.equalsIgnoreCase("N"))
                            {
                                continue;
                            }
                        }
                    }

                    this.getFromEntityEmailId(fromEntityId, smsContent);
                    this.getToEntityEmailIdBasedOnEvent(toEntityId, smsContent, smsEventEnum);
                }
                if((smsEventEnum.equals(MailEventEnum.MERCHANT_SIGNUP_OTP)) || (smsEventEnum.equals(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT)))
                {
                    this.getFromEntityEmailId(fromEntityId, smsContent);
                }
                int smsTemplateId = resultSet.getInt("template_id");
                String smsSubject = resultSet.getString("subject");
                String telNo = "";
                String telNoCc="";


                if (smsContent.get(MailPlaceHolder.SUBJECT) != null)
                {
                    smsSubject = smsSubject + " " + smsContent.get(MailPlaceHolder.SUBJECT);
                }

                if (smsContent.get(MailPlaceHolder.SMS_TO_TELNO) != null)
                {
                    telNo = smsContent.get(MailPlaceHolder.SMS_TO_TELNO);
                }

                if(smsContent.get(MailPlaceHolder.SMS_TO_TELNOCC)!=null){
                    telNoCc = smsContent.get(MailPlaceHolder.SMS_TO_TELNOCC);
                }
                if(smsContent.get(MailPlaceHolder.SMS_SERVICE)!=null){
                    sms_service = smsContent.get(MailPlaceHolder.SMS_SERVICE);
                }

                //prepare mail template and repale placeholde with template tag
                String template = this.getMailTemplate(smsTemplateId);
                //System.out.println("CC---"+smsContent.get(MailPlaceHolder.SMS_TO_TELNOCC)+"-TEL---"+smsContent.get(MailPlaceHolder.SMS_TO_TELNO));
                String msg = Functions.replaceTag1(template, smsContent);
                log.debug("sms msg::::" + msg);
                log.error("sms_service >>>>>>>>>> "+ sms_service);
                if("smsglobal".equalsIgnoreCase(sms_service)){
                    sendSMS_WhiteLabel(telNo, smsContent.get(MailPlaceHolder.SMS_FROM_NUMBER), telNoCc, smsSubject, msg, smsContent.get(MailPlaceHolder.SMS_USERNAME), smsContent.get(MailPlaceHolder.SMS_PASSWORD));
                }else if("fast2sms".equalsIgnoreCase(sms_service)){
                    sendSMS_WhiteLabel_Fast2SMSService(telNo, smsContent.get(MailPlaceHolder.SMS_FROM_NUMBER), telNoCc, smsSubject, msg, smsContent.get(MailPlaceHolder.SMS_USERNAME), smsContent.get(MailPlaceHolder.SMS_PASSWORD));
                }


            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::" , systemError);
        }
        catch (SQLException e)
        {
            log.error("SystemError:::::" , e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    private HashMap<MailPlaceHolder, String> getFromEntityEmailId(int intFromEntityId, HashMap<MailPlaceHolder, String> smsContent)
    {
        MailEntityEnum mailEntityEnum = MailEntityEnum.valueOf(getMailEntityPlaceHolder(intFromEntityId));
        MailService mailService = new MailService();
        Hashtable details = null;
        Functions functions = new Functions();
        //System.out.println("mailEntityEnum---"+mailEntityEnum.toString());
        switch (mailEntityEnum)
        {
            case AdminEmail:
                details = mailService.getSuperAdminvalue();
                setDefaultSMSContent(details, smsContent);
                break;

            case PartnerEmail:

                if (functions.isValueNull(smsContent.get(MailPlaceHolder.TOID)))
                {
                    details = mailService.getPartnersMerchantDetails(smsContent.get(MailPlaceHolder.TOID),"");
                }
                else if (functions.isValueNull(smsContent.get(MailPlaceHolder.PARTNERID)))
                {
                    details = mailService.getPartnerDetails(smsContent.get(MailPlaceHolder.PARTNERID));
                }
                setDefaultSMSContent(details, smsContent);
                break;
        }
        log.debug("default admin data---"+smsContent);

        return smsContent;
    }

    public HashMap<MailPlaceHolder, String> setDefaultSMSContent(Hashtable defaultValue, HashMap<MailPlaceHolder, String> smsContent)
    {
        Hashtable innerHash = (Hashtable) defaultValue.get(1 + "");
        smsContent.put(MailPlaceHolder.SMS_FROM_NUMBER, innerHash.get("from_sms") == null ? "" : (String) innerHash.get("from_sms"));
        smsContent.put(MailPlaceHolder.SMS_USERNAME, innerHash.get("sms_user") == null ? "" : (String) innerHash.get("sms_user"));
        smsContent.put(MailPlaceHolder.SMS_PASSWORD, innerHash.get("sms_password") == null ? "" : (String) innerHash.get("sms_password"));
        smsContent.put(MailPlaceHolder.COMPANYNAME, innerHash.get("partnerName") == null ? "" : (String) innerHash.get("partnerName"));
        smsContent.put(MailPlaceHolder.SMS_SERVICE, innerHash.get("sms_service") == null ? "" : (String) innerHash.get("sms_service"));
        return smsContent;
    }

    private HashMap<MailPlaceHolder, String> getToEntityEmailId(int strToEntityId, HashMap<MailPlaceHolder, String> smsContent)
    {
        MailService mailService = new MailService();
        Hashtable details = null;
        Hashtable innerHash = null;
        MailEntityEnum mailEntityEnum = MailEntityEnum.valueOf(getMailEntityPlaceHolder(Integer.valueOf(strToEntityId)));
        switch (mailEntityEnum)
        {
            case AdminEmail:
                details = mailService.getSuperAdminvalue();
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.NAME, (String) innerHash.get("contact_persons"));
                smsContent.put(MailPlaceHolder.EmailToAddress, (String) innerHash.get("companyadminid"));
                break;

            case PartnerEmail:
                details = mailService.getPartnerDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.NAME, (String) innerHash.get("contact_persons"));
                smsContent.put(MailPlaceHolder.EmailToAddress, (String) innerHash.get("companyadminid"));
                break;

            case MerchantEmail:
                details = mailService.getMerchantDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.MCOMNAME, (String) innerHash.get("sitename"));
                smsContent.put(MailPlaceHolder.NAME, (String) innerHash.get("contact_persons"));
                smsContent.put(MailPlaceHolder.EmailToAddress, (String) innerHash.get("contact_emails"));
                break;

            case MerchantUserEmail:
                details = mailService.getMerchantUserDetails(smsContent.get(MailPlaceHolder.USERID));
                innerHash = (Hashtable) details.get(1 + "");
                //vMailContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                smsContent.put(MailPlaceHolder.NAME, (String) innerHash.get("login"));
                smsContent.put(MailPlaceHolder.EmailToAddress, (String) innerHash.get("contact_emails"));
                break;

            case AgentEmail:
                details = mailService.getAgentDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.NAME, (String) innerHash.get("contact_persons"));
                smsContent.put(MailPlaceHolder.EmailToAddress, (String) innerHash.get("contact_emails"));
                break;

            case CustomerEmail:
                details = mailService.getMerchantDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.MCOMNAME, (String) innerHash.get("sitename"));
                smsContent.put(MailPlaceHolder.EmailToAddress, smsContent.get(MailPlaceHolder.CustomerEmail));
                break;

            case FraudSystemEmail:
                details = mailService.getFraudSystemAccountDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.NAME, (String) innerHash.get("contact_name"));
                smsContent.put(MailPlaceHolder.EmailToAddress, (String) innerHash.get("contact_email"));
                break;

            case BankEmail:
                details = mailService.getSuperAdminvalue();
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.URL, (String) innerHash.get("bankApplicationURL"));
                GatewayType gatewayType = GatewayTypeService.getGatewayType(smsContent.get(MailPlaceHolder.PGTYPEID));
                smsContent.put(MailPlaceHolder.NAME, gatewayType.getName());
                smsContent.put(MailPlaceHolder.EmailToAddress, gatewayType.getBank_emailid());
                break;
        }
        return smsContent;
    }

    private HashMap<MailPlaceHolder, String> getToEntityEmailIdBasedOnEvent(int strToEntityId, HashMap<MailPlaceHolder, String> smsContent, MailEventEnum smsEventEnum)
    {
        MailService mailService = new MailService();
        Hashtable details = null;
        Hashtable innerHash = null;
        MailEntityEnum mailEntityEnum = MailEntityEnum.valueOf(getMailEntityPlaceHolder(Integer.valueOf(strToEntityId)));
        SMSContactVO smsContactVO = null;
        switch (mailEntityEnum)
        {
            case AdminEmail:
                details = mailService.getSuperAdminvalue();
                innerHash = (Hashtable) details.get(1 + "");
                smsContactVO = getAdminToMailId(smsEventEnum, innerHash);
                smsContent.put(MailPlaceHolder.NAME, smsContactVO.getName());
                smsContent.put(MailPlaceHolder.SMS_TO_TELNO, smsContactVO.getTelNo());
                break;

            case PartnerEmail:
                details = mailService.getPartnerDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContactVO = getAdminToMailId(smsEventEnum, innerHash);
                smsContent.put(MailPlaceHolder.NAME, smsContactVO.getName());
                smsContent.put(MailPlaceHolder.SMS_TO_TELNO, smsContactVO.getTelNo());
                break;

            case MerchantEmail:
                details = mailService.getMerchantDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                smsContent.put(MailPlaceHolder.MCOMNAME, (String) innerHash.get("sitename"));
                smsContactVO = getMerchantToMailIdBasedOnEventWithCC(smsEventEnum, innerHash);
                smsContent.put(MailPlaceHolder.NAME, smsContactVO.getName());
                smsContent.put(MailPlaceHolder.SMS_TO_TELNO, smsContactVO.getTelNo());
                break;

            case CustomerEmail:
                details = mailService.getMerchantDetails(smsContent.get(MailPlaceHolder.TOID));
                innerHash = (Hashtable) details.get(1 + "");
                if (innerHash != null)
                {
                    smsContent.put(MailPlaceHolder.MCOMNAME, (String) innerHash.get("sitename"));
                }
                smsContent.put(MailPlaceHolder.SMS_TO_TELNO, smsContent.get(MailPlaceHolder.SMS_TO_TELNO));
                break;
        }
        return smsContent;
    }

    public String getMailTemplate(int smsTemplateId)
    {
        String loadSmsTemplateContent = "";
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String templateName = "";
            String qry = "SELECT sms_template_name,sms_emplate_filename FROM sms_template WHERE sms_template_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(qry);
            preparedStatement.setInt(1, smsTemplateId);
            ResultSet resultSet = preparedStatement.executeQuery();
            log.debug("sms template---"+preparedStatement);
            if (resultSet.next())
            {
                templateName = resultSet.getString("sms_emplate_filename");
            }
            else
            {
                throw new SystemError("SMS TEMPLATE NOT FOUND FOR MAIL ");
            }
            File templateFile = new File(templatesFilePath + templateName);
            loadSmsTemplateContent = getSmsContents(templateFile);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::", e);
        }
        catch (IOException e)
        {
            log.error("IOException::::", e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return loadSmsTemplateContent;
    }

    private String getSmsContents(File templateFile) throws IOException
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

    public Hashtable getPartnerDetails(String toId)
    {
        Hashtable partnerMailDetail = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String qry = "SELECT * FROM partners WHERE partnerId=?";
            PreparedStatement preparedStatement = conn.prepareStatement(qry);
            preparedStatement.setString(1, toId);
            partnerMailDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());
            if (partnerMailDetail.isEmpty())
            {
                String qry1 = "SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM members WHERE memberid=?)";
                PreparedStatement preparedStatement1 = conn.prepareStatement(qry1);
                preparedStatement1.setString(1, toId);
                partnerMailDetail = Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
            if (partnerMailDetail.isEmpty())
            {
                String qry1 = "SELECT * FROM partners WHERE partnerId=(SELECT partnerId FROM agents WHERE agentId=?)";
                PreparedStatement preparedStatement1 = conn.prepareStatement(qry1);
                preparedStatement1.setString(1, toId);
                partnerMailDetail = Database.getHashFromResultSet(preparedStatement1.executeQuery());
            }
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return partnerMailDetail;
    }

    public Hashtable getMerchantDetails(String memberId)
    {
        Hashtable merchantDetail = new Hashtable();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String qry = "SELECT m.*,mc.* FROM members AS m left JOIN merchant_configuration AS mc on m.memberid=mc.memberid where m.memberid=?";
            PreparedStatement preparedStatement = conn.prepareStatement(qry);
            preparedStatement.setString(1, memberId);
            merchantDetail = Database.getHashFromResultSet(preparedStatement.executeQuery());
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return merchantDetail;
    }

    public void sendSMS_WhiteLabel(String toNumber, String fromNumber, String telNoCc, String subject, String message, String userName, String password)
    {
        log.error("Inside send SMS toNumber----->"+toNumber);
        String cust_telnocc = null;
        if(telNoCc!=null && !telNoCc.isEmpty())
        {
            cust_telnocc = telNoCc.substring(1)+toNumber;
        }
        else
        {
        cust_telnocc = toNumber;
        }

        //String URL = "http://203.212.70.200/smpp/sendsms?username=" + userName + "&password=" + password + "&to=" + cust_telnocc + "&from=" + fromNumber + "&text=";
        //String URL = "https://www.smsglobal.com/http-api.php?action=sendsms&user=" + userName + "&password=" + password + "&to=" + cust_telnocc + "&from=" + fromNumber + "&text=";
        String URL = "https://api.smsglobal.com/http-api.php?action=sendsms&user=" + userName + "&password=" + password + "&to=" + cust_telnocc + "&from=" + fromNumber + "&text=";
        try
        {
            log.error("URL:::"+URL);
            String result   = SMSGateway.doPostHTTPURLConnection(URL, message);
            log.error(" SMS Global Result::::::" + result);
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException::::",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SMSService", "sendSMS_WhiteLabel()", null, "common", "Issue while getting SMS Template", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
    }

    /*
      * Description:Added new method for event base email segregation-Partner
     */
    public SMSContactVO getPartnerToMailId(MailEventEnum partnerDetail, Hashtable hashtable)
    {
        String name = "";
        String telNo = "";
        switch (partnerDetail)
        {
            case PARTNERS_MERCHANT_SALE_TRANSACTION:
                name = (String) hashtable.get("notifycontactname");
                telNo = (String) hashtable.get("telno");
                break;

            default:
                name = (String) hashtable.get("notifycontactname");
                telNo = (String) hashtable.get("telno");
        }

        Functions functions = new Functions();
        if (!functions.isValueNull(telNo) || !functions.isValueNull(name))
        {
            name = (String) hashtable.get("notifycontactname");
            telNo = (String) hashtable.get("telno");
        }
        SMSContactVO smsContactVO = new SMSContactVO();
        smsContactVO.setTelNo(telNo);
        smsContactVO.setName(name);
        return smsContactVO;
    }

    /*
     * Description:Added new method for event base email segregation with CC-Merchant
    */
    public SMSContactVO getMerchantToMailIdBasedOnEventWithCC(MailEventEnum merchantDetails, Hashtable hashtable)
    {
        String name = "";
        String telNo = "";
        switch (merchantDetails)
        {

            case PARTNERS_MERCHANT_SALE_TRANSACTION:
                name = (String) hashtable.get("contact_persons");
                telNo = (String) hashtable.get("maincontact_phone");
                break;

            default:
                name = (String) hashtable.get("contact_persons");
                telNo = (String) hashtable.get("maincontact_phone");
        }

        Functions functions = new Functions();
        if (!functions.isValueNull(name) || !functions.isValueNull(telNo))
        {
            name = (String) hashtable.get("contact_persons");
            telNo = (String) hashtable.get("maincontact_phone");
        }
        SMSContactVO smsContactVO = new SMSContactVO();
        smsContactVO.setTelNo(telNo);
        smsContactVO.setName(name);
        return smsContactVO;
    }

    /*
     * Description:Added new method for event base email segregation-Admin
    */
    public SMSContactVO getAdminToMailId(MailEventEnum adminEmailEvent, Hashtable hashtable)
    {
        String name = "";
        String telNo = "";
        switch (adminEmailEvent)
        {
            case PARTNERS_MERCHANT_SALE_TRANSACTION:
                name = (String) hashtable.get("notifycontactname");
                telNo = (String) hashtable.get("telno");
                break;

            case GENERATING_INVOICE_BY_MERCHANT:
                name = (String) hashtable.get("notifycontactname");
                telNo = (String) hashtable.get("telno");
                break;

            default:
                name = (String) hashtable.get("notifycontactname");
                telNo = (String) hashtable.get("telno");
        }

        Functions functions = new Functions();
        if (!functions.isValueNull(telNo) || !functions.isValueNull(name))
        {
            name = (String) hashtable.get("notifycontactname");
            telNo = (String) hashtable.get("telno");
        }
        SMSContactVO smsContactVO = new SMSContactVO();
        smsContactVO.setTelNo(telNo);
        smsContactVO.setName(name);
        return smsContactVO;
    }

    /*
     * Description:Added new method for event base email segregation-Agent
     */
    public MailContactVO getAgentToMailIdBasedOnEventWithCC(MailEventEnum agentDetails, Hashtable hashtable)
    {
        String agentContactName = "";
        String agentToMailId = "";
        String agentToCcMailId = "";
        switch (agentDetails)
        {
            case AGENT_FORGOT_PASSWORD:
                agentContactName = (String) hashtable.get("contact_persons");
                agentToMailId = (String) hashtable.get("contact_emails");
                agentToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                break;

            case AGENT_CHANGE_PASSWORD:
                agentContactName = (String) hashtable.get("contact_persons");
                agentToMailId = (String) hashtable.get("contact_emails");
                agentToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                break;

            case AGENT_REGISTRATION:
                agentContactName = (String) hashtable.get("contact_persons");
                agentToMailId = (String) hashtable.get("contact_emails");
                agentToCcMailId = (String) hashtable.get("maincontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT:
                agentContactName = (String) hashtable.get("salescontact_name");
                agentToMailId = (String) hashtable.get("salesemail");
                agentToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_CB:
                agentContactName = (String) hashtable.get("cbcontact_name");
                agentToMailId = (String) hashtable.get("cbcontact_mailid");
                agentToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_RF:
                agentContactName = (String) hashtable.get("refundcontact_name");
                agentToMailId = (String) hashtable.get("refundcontact_mailid");
                agentToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_FRAUD:
                agentContactName = (String) hashtable.get("fraudcontact_name");
                agentToMailId = (String) hashtable.get("fraudcontact_mailid");
                agentToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ALERT_TO_AGENT_TECH:
                agentContactName = (String) hashtable.get("technicalcontact_name");
                agentToMailId = (String) hashtable.get("technicalcontact_mailid");
                agentToCcMailId = (String) hashtable.get("technicalcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT:
                agentContactName = (String) hashtable.get("salescontact_name");
                agentToMailId = (String) hashtable.get("salesemail");
                agentToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_CB:
                agentContactName = (String) hashtable.get("cbcontact_name");
                agentToMailId = (String) hashtable.get("cbcontact_mailid");
                agentToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_RF:
                agentContactName = (String) hashtable.get("refundcontact_name");
                agentToMailId = (String) hashtable.get("refundcontact_mailid");
                agentToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_FRAUD:
                agentContactName = (String) hashtable.get("fraudcontact_name");
                agentToMailId = (String) hashtable.get("fraudcontact_mailid");
                agentToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_SUSPENSION_TO_AGENT_TECH:
                agentContactName = (String) hashtable.get("technicalcontact_name");
                agentToMailId = (String) hashtable.get("technicalcontact_mailid");
                agentToCcMailId = (String) hashtable.get("technicalcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_SALES:
                agentContactName = (String) hashtable.get("salescontact_name");
                agentToMailId = (String) hashtable.get("salesemail");
                agentToCcMailId = (String) hashtable.get("salescontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_CB:
                agentContactName = (String) hashtable.get("cbcontact_name");
                agentToMailId = (String) hashtable.get("cbcontact_mailid");
                agentToCcMailId = (String) hashtable.get("cbcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_RF:
                agentContactName = (String) hashtable.get("refundcontact_name");
                agentToMailId = (String) hashtable.get("refundcontact_mailid");
                agentToCcMailId = (String) hashtable.get("refundcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_FRAUD:
                agentContactName = (String) hashtable.get("fraudcontact_name");
                agentToMailId = (String) hashtable.get("fraudcontact_mailid");
                agentToCcMailId = (String) hashtable.get("fraudcontact_ccmailid");
                break;

            case MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_TECH:
                agentContactName = (String) hashtable.get("technicalcontact_name");
                agentToMailId = (String) hashtable.get("technicalcontact_mailid");
                agentToCcMailId = (String) hashtable.get("technicalcontact_ccmailid");
                break;

            default:
                agentToMailId = (String) hashtable.get("contact_emails");
                agentContactName = (String) hashtable.get("contact_persons");
                agentToCcMailId = (String) hashtable.get("maincontact_ccmailid");
        }
        Functions functions = new Functions();
        if (!functions.isValueNull(agentToMailId) || !functions.isValueNull(agentContactName))
        {
            agentToMailId = (String) hashtable.get("contact_emails");
            agentContactName = (String) hashtable.get("contact_persons");
            agentToCcMailId = (String) hashtable.get("maincontact_ccmailid");
        }

        MailContactVO mailContactVO = new MailContactVO();
        mailContactVO.setContactMailId(agentToMailId);
        mailContactVO.setContactCcMailId(agentToCcMailId);
        mailContactVO.setName(agentContactName);
        return mailContactVO;
    }



    public void sendSMS_WhiteLabel_Fast2SMSService(String toNumber, String fromNumber, String telNoCc, String subject, String message, String userName, String password)
    {
        log.error("Inside send SMS toNumber---->"+toNumber);
        String cust_telnocc = null;
        if(telNoCc!=null && !telNoCc.isEmpty())
        {
            cust_telnocc = telNoCc.substring(1)+toNumber;
        }
        else
        {
            cust_telnocc = toNumber;
        }

        JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("sender_id",userName);
            jsonObject.put("route","v3");
            jsonObject.put("message",message);
            jsonObject.put("language", "english");
            jsonObject.put("numbers", cust_telnocc);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        String URL = "https://www.fast2sms.com/dev/bulkV2";
        try
        {
            log.error("URL:::"+URL);
            String result   = SMSGateway.doPostJSONHTTPURLConnection(URL, jsonObject.toString(),password);
            log.error(" SMS Fast2SMS Result::::::" + result);
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException::::",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SMSService", "sendSMS_WhiteLabel()", null, "common", "Issue while getting SMS Template", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), null, null);
        }
    }


}
