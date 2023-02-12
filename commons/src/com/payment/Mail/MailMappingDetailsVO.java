package com.payment.Mail;

/**
 * Created by sandip on 4/13/2017.
 */
public class MailMappingDetailsVO
{
    int mappingId;
    int mailEventId;
    int mailFromEntityId;
    int mailToEntityId;
    int mailTemplateId;
    String mailSubject;

    public int getMappingId()
    {
        return mappingId;
    }

    public void setMappingId(int mappingId)
    {
        this.mappingId = mappingId;
    }

    public int getMailEventId()
    {
        return mailEventId;
    }

    public void setMailEventId(int mailEventId)
    {
        this.mailEventId = mailEventId;
    }

    public int getMailToEntityId()
    {
        return mailToEntityId;
    }

    public void setMailToEntityId(int mailToEntityId)
    {
        this.mailToEntityId = mailToEntityId;
    }

    public int getMailFromEntityId()
    {
        return mailFromEntityId;
    }

    public void setMailFromEntityId(int mailFromEntityId)
    {
        this.mailFromEntityId = mailFromEntityId;
    }

    public int getMailTemplateId()
    {
        return mailTemplateId;
    }

    public void setMailTemplateId(int mailTemplateId)
    {
        this.mailTemplateId = mailTemplateId;
    }

    public String getMailSubject()
    {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject)
    {
        this.mailSubject = mailSubject;
    }
}
