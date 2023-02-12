package com.payment.Mail;

import com.directi.pg.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 6/5/15
 * Time: 8:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSService
{


    static Logger log = new Logger(SMSService.class.getName());
    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.template");
    String templatesFilePath = RBTemplate.getString("SMSTEMPLATEPATH");
    private static HashMap<Integer,String> hMapSMSEntity = new HashMap<Integer, String>();
    private static HashMap<Integer,String> hMapSMSTemplate = new HashMap<Integer, String>();
    private static MailServiceDAO mailServiceDAO = new MailServiceDAO();

    //static hash to identify entity for mails
    static
    {
        mailServiceDAO.loadMailEntiy(hMapSMSEntity);
        mailServiceDAO.loadMailTemplates(hMapSMSTemplate);
       
    }

    public String getSMSEntityPlaceHolder(int mailEntityId )
    {
        return hMapSMSEntity.get(mailEntityId);
    }

    public String getSMSTemplate(int smsTemplateId)
    {
        String loadSMSTemplateContent="";
        try
        {
            String templateName = hMapSMSTemplate.get(smsTemplateId);
            File templateFile = new File(templatesFilePath +templateName);
            loadSMSTemplateContent=getSMSContents(templateFile);
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SMSService","getSMSTemplate()",null,"common","Issue while getting SMS Template", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,null);
        }
        
        return loadSMSTemplateContent;
    }

    private String getSMSContents(File templateFile)throws IOException
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

    public void sendSMS(MailEventEnum mailEventEnum, HashMap<MailPlaceHolder,String> vSMSContent)
    {
        Hashtable smsMappingDetails=new Hashtable();
        Hashtable  innerHash =new Hashtable();

        smsMappingDetails = mailServiceDAO.getSMSMappingDetails(mailEventEnum);
        //how many mail tobe send
        Iterator iterator = smsMappingDetails.keySet().iterator();
        while (iterator.hasNext())
        {

            innerHash = (Hashtable)iterator.next();

            int fromEntityId=(Integer)innerHash.get("smsFromEntityId");
            int toEntityId=(Integer)innerHash.get("smsToEntityId");

            //if ToEntity Is of Customer and MailEvent is Transaction
            MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getSMSEntityPlaceHolder(toEntityId));
            String vMemberId = vSMSContent.get(MailPlaceHolder.TOID);

            if(mailEntityEnum.equals(MailEntityEnum.CustomerEmail) && (mailEventEnum.equals(mailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION) ||  mailEventEnum.equals(mailEventEnum.REFUND_TRANSACTION) || mailEventEnum.equals(mailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION))&& vMemberId!=null)
            {
                String flag=mailServiceDAO.getCustReminderSMSFlag(vMemberId);
                if(flag.equalsIgnoreCase("N"))
                {
                    continue;
                }
            }
            int smsTemplateId=(Integer)innerHash.get("smsTemplateId");
            String strSMSSubject=(String)innerHash.get("smsSubject");

            getFromEntityDetails(fromEntityId, vSMSContent);

            getToEntityDetails(toEntityId, vSMSContent);

            if(vSMSContent.get(MailPlaceHolder.SUBJECT)!=null)
            {
                strSMSSubject = strSMSSubject +" "+vSMSContent.get(MailPlaceHolder.SUBJECT);
            }
            //prepare mail template and repale placeholde with template tag
            String template=getSMSTemplate(smsTemplateId);
            String smsMessage = Functions.replaceTag1(template, vSMSContent);

           log.debug("SMTP:- "+vSMSContent.get(MailPlaceHolder.SMS_HOST)+vSMSContent.get(MailPlaceHolder.SMS_PORT)+vSMSContent.get(MailPlaceHolder.SMS_USERNAME)+vSMSContent.get(MailPlaceHolder.SMS_PASSWORD)+"TO address: "+vSMSContent.get(MailPlaceHolder.SMS_TO_TELNO)+vSMSContent.get(MailPlaceHolder.SMS_TO_TELNOCC)+" From address: "+vSMSContent.get(MailPlaceHolder.SMS_FROM_ADDRESS)+"  mail Content  "+smsMessage);
           sendSMS_WhiteLabel(vSMSContent.get(MailPlaceHolder.SMS_TO_TELNO), vSMSContent.get(MailPlaceHolder.SMS_FROM_ADDRESS), vSMSContent.get(MailPlaceHolder.SMS_TO_TELNOCC),strSMSSubject, smsMessage,vSMSContent.get(MailPlaceHolder.SMS_HOST),vSMSContent.get(MailPlaceHolder.SMS_PORT),vSMSContent.get(MailPlaceHolder.SMS_USERNAME),vSMSContent.get(MailPlaceHolder.SMS_PASSWORD));
        }

    }

    private void getToEntityDetails(int toEntityId, HashMap<MailPlaceHolder, String> vSMSContent)
    {

        Hashtable details=null;
        Hashtable innerhash=null;
        MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getSMSEntityPlaceHolder(Integer.valueOf(toEntityId)));
        switch(mailEntityEnum)
        {
            case AdminEmail:
                details=mailServiceDAO.getSuperAdminvalue();
                innerhash=(Hashtable)details.get(1+"");
                vSMSContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNO,(String)innerhash.get("telno"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNOCC,"");
                break;

            case PartnerEmail:
                details=mailServiceDAO.getPartnerDetails((String)vSMSContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vSMSContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNO,(String)innerhash.get("telno"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNOCC,"");
                break;

            case MerchantEmail:
                details=mailServiceDAO.getMerchantDetails((String)vSMSContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vSMSContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                vSMSContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNO,(String)innerhash.get("telno"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNOCC,"");
                break;

            case AgentEmail:
                details=mailServiceDAO.getAgentDetails((String) vSMSContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vSMSContent.put(MailPlaceHolder.NAME,(String)innerhash.get("contact_persons"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNO,(String)innerhash.get("telno"));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNOCC,"");
                break;

            case CustomerEmail:
                details=mailServiceDAO.getMerchantDetails((String) vSMSContent.get(MailPlaceHolder.TOID));
                innerhash=(Hashtable)details.get(1+"");
                vSMSContent.put(MailPlaceHolder.MCOMNAME,(String)innerhash.get("sitename"));
                vSMSContent.put(MailPlaceHolder.EmailToAddress,vSMSContent.get(MailPlaceHolder.CustomerEmail));
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNO,"");
                vSMSContent.put(MailPlaceHolder.SMS_TO_TELNOCC,"");
                break;
        }
        log.debug("TO content   "+vSMSContent);

    }

    private void getFromEntityDetails(int fromEntityId, HashMap<MailPlaceHolder, String> vSMSContent)
    {
        MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getSMSEntityPlaceHolder(fromEntityId));

        Hashtable details=null;
        log.debug(mailEntityEnum);
        switch(mailEntityEnum)
        {
            case AdminEmail:
                //fetch Admin details
                details=mailServiceDAO.getSuperAdminvalue();
                setDefaultSMSContent(details, vSMSContent);
                break;

            case PartnerEmail:
                //Fetch Partner Details
                details=mailServiceDAO.getPartnersMerchantDetails((String)vSMSContent.get(MailPlaceHolder.TOID));
                setDefaultSMSContent(details, vSMSContent);
                break;

            case MerchantEmail:
                //Fetch Merchant Details
                break;

            case AgentEmail:
                //Fetch
                break;

            case CustomerEmail:
                break;
        }
        log.debug("From content   "+vSMSContent);

    }

    public HashMap<MailPlaceHolder ,String> setDefaultSMSContent(Hashtable defaultValue,HashMap<MailPlaceHolder,String> vSMSContent)
    {
        Hashtable innerhash=(Hashtable)defaultValue.get(1+"");
        vSMSContent.put(MailPlaceHolder.COMPANYNAME,(String)innerhash.get("partnerName"));
        vSMSContent.put(MailPlaceHolder.SUPPORTLINK,(String)innerhash.get("supporturl"));
        vSMSContent.put(MailPlaceHolder.LIVEURL,(String)innerhash.get("siteurl"));
        vSMSContent.put(MailPlaceHolder.SMS_FROM_ADDRESS,(String)innerhash.get("partnerName"));
        vSMSContent.put(MailPlaceHolder.SMS_HOST,(String)innerhash.get("smtp_host"));
        vSMSContent.put(MailPlaceHolder.SMS_PORT,(String)innerhash.get("smtp_port"));
        vSMSContent.put(MailPlaceHolder.SMS_USERNAME,(String)innerhash.get("smtp_user"));
        vSMSContent.put(MailPlaceHolder.SMS_PASSWORD,(String)innerhash.get("smtp_password"));
        vSMSContent.put(MailPlaceHolder.SUPPORTEMAIL,innerhash.get("companysupportmailid")==null?"":(String)innerhash.get("companysupportmailid"));
        return vSMSContent;
    }

    public boolean isSMSRequiredForCustomer(int toEntityId,String memberid,MailEventEnum mailEvent)
    {
        boolean isMailSend=false;
        MailEntityEnum mailEntityEnum =  MailEntityEnum.valueOf(getSMSEntityPlaceHolder(toEntityId));
        if(mailEntityEnum.equals(MailEntityEnum.CustomerEmail) && mailEvent.equals(mailEvent.PARTNERS_MERCHANT_SALE_TRANSACTION) && memberid!=null)
        {


            Hashtable innerHash=new Hashtable();
            Hashtable mailConfigFlages= mailServiceDAO.getMerchantDetails(memberid);
            innerHash=(Hashtable)mailConfigFlages.get(1+"");

            switch(mailEvent)
            {
                case PARTNERS_MERCHANT_SALE_TRANSACTION:
                    if(innerHash.get("custremindersms").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;
                case CHARGEBACK_TRANSACTION:
                    if(innerHash.get("isCBsmstocus").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;

                case REFUND_TRANSACTION:
                    if(innerHash.get("isRFsmstocus").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;

                case CAPTURE_TRANSACTION:
                    if(innerHash.get("isCaptursmstocus").equals("N"))
                    {
                        isMailSend=false;
                    }
                    break;
            }

        }
        return isMailSend;
    }

    public void sendSMS_WhiteLabel(String telno, String from, String telnoocc,String subject, String message,String host,String port,String userName,String password)
    {

     String cust_telnocc = null;

     if(telnoocc!=null && !telnoocc.isEmpty())
     {
         cust_telnocc = telnoocc+telno;
     }
     else
     {
         cust_telnocc = telno;
     }
        
     String URL = "http://203.212.70.200/smpp/sendsms?username="+userName+"&password="+password+"&to="+cust_telnocc+"&from="+from+"&text="+message;

        try
        {
            SMSGateway.doPostHTTPURLConnection(URL,null);
        }
        catch (PZTechnicalViolationException e)
        {

            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SMSService","sendSMS_WhiteLabel()",null,"common","Issue while getting SMS Template", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,null);


        }
    }


}
