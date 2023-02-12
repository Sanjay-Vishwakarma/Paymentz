package com.directi.pg;

import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

public class Mail
{
    static Logger cat = new Logger(Mail.class.getName());
    /*
    final static String SMTP_HOST = RB.getString("SMTP_HOST");
    final static String SMTP_PORT= RB.getString("SMTP_PORT");
    final static String SMTP_AUTH_USER= RB.getString("SMTP_AUTH_USER");
    final static String SMTP_AUTH_PWD= RB.getString("SMTP_AUTH_PWD");*/
    /*public static void main(String args[])
    {
        try
        {
            System.out.println("--------Started");
            Mail.sendHtmlMail_WhiteLabel("<emailaddress>","donotreply@transactworld.com",null,null,"test","test111111","donotreply@transactworld.com","578","donotreply@transactworld.com","^wQOnTd0");
            System.out.println("--------finished");
        }
        catch (SystemError systemError)
        {
            systemError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }*/
    public static void sendmail(String to, String from, String subject, String message) throws SystemError
    {
        sendmail(to, from, null, null, subject, message);
    }

    public static void sendAdminMail(String subject, String message) throws SystemError
    {
        MailService mailService=new MailService();
        HashMap adminMessage=new HashMap();
        adminMessage.put(MailPlaceHolder.MESSAGE,message);
        mailService.sendMail(MailEventEnum.ADMIN_MAIL,adminMessage);
    }

    public static void sendNotificationMail(String subject, String message) throws SystemError
    {
        /*Hashtable superAdmin=new Hashtable();
        MailService mailService=new MailService();
        superAdmin = mailService.getSuperAdminvalue();
        sendHtmlMail((String)superAdmin.get("companyadminid"), (String)superAdmin.get("companyfromemail"), null, null, subject, message);*/
        sendAdminMail(subject,message);
    }

    public static void sendmail(String to, String from, String cc, String bcc, String subject, String message)
    {
        try
        {
            cat.debug("Inside SendMail");
            Hashtable superAdmin=new Hashtable();
            MailService mailService=new MailService();
            superAdmin = mailService.getSuperAdminvalue();

               String host = (String)superAdmin.get("smtp_host");
               String port=  (String)superAdmin.get("smtp_port");
               String userName=  (String)superAdmin.get("smtp_user");
               String password=  (String)superAdmin.get("smtp_password");

               Properties props = new Properties();
               props.put("mail.smtp.auth", "true");
               props.put("mail.smtp.host", host);
               if((port!=null && !port.equals("")))
               {
                props.put("mail.smtp.port", port);
               }
               Authenticator auth = new SMTPAuthenticator(userName,password);
               boolean debug = false;
               Session session1 = Session.getInstance(props, auth);
            session1.setDebug(debug);

            MimeMessage msg = new MimeMessage(session1);
            msg.setFrom(new InternetAddress((String)superAdmin.get("companyfromemail")));

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
            msg.setSentDate(new java.util.Date());

            msg.setContent(message, "text/plain");

            cat.debug("SENDING MAIL TO " + to + " CC " + cc + " BCC " + bcc + " WITH SUBJECT " + subject);
            Transport.send(msg);
            cat.debug("MAIL SENT");
            cat.debug("Leaving sendmail");
        }
        catch (SendFailedException sfe)
        {
            cat.error("SendFailException",sfe);
        }
        catch (Exception e)
        {
            cat.error("Exception::::::::",e);
        }
    }

    public static void sendHtmlMail(String to, String from, String cc, String bcc, String subject, String message) throws SystemError
    {
        String contenttype = "text/html";
        try
        {
            cat.debug("Inside SendMail");

            MailService mailService=new MailService();
            Hashtable superAdmin = mailService.getSuperAdminvalue();

            Hashtable innerhash=(Hashtable)superAdmin.get(1+"");
            String host = (String)innerhash.get("smtp_host");
            String port=  (String)innerhash.get("smtp_port");
            String userName=  (String)innerhash.get("smtp_user");
            String password=  (String)innerhash.get("smtp_password");
            cat.debug("Host set to " + host);
               Properties props = new Properties();
               props.put("mail.smtp.auth", "true");
               props.put("mail.smtp.host", host);

               if((port!=null && !port.equals("")))
               {
                props.put("mail.smtp.port", port);
               }
               Authenticator auth = new SMTPAuthenticator(userName,password);
               boolean debug = false;
               Session session1 = Session.getInstance(props, auth);
            session1.setDebug(debug);

            MimeMessage msg = new MimeMessage(session1);
            msg.setFrom(new InternetAddress((String)innerhash.get("companyfromemail")));

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
            msg.setSentDate(new java.util.Date());
            msg.setContent(message, contenttype);

            Transport.send(msg);
            cat.debug("MAIL SENT");
            cat.debug("Leaving sendmail");
        }
        catch (SendFailedException sfe)
        {
            cat.error("SendFailException",sfe);
        }
        catch (Exception e)
        {
           cat.error("Exception::::::::",e);
        }
    }
}
