package com.payment.Mail;

/**
 * Created by admin on 3/23/2016.
 */
public class MailContactVO
{
    String name;
    String contactMailId;
    String contactCcMailId;
    String contactBccMailId;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContactMailId()
    {
        return contactMailId;
    }

    public void setContactMailId(String contactMailId)
    {
        this.contactMailId = contactMailId;
    }

    public String getContactCcMailId()
    {
        return contactCcMailId;
    }

    public void setContactCcMailId(String contactCcMailId)
    {
        this.contactCcMailId = contactCcMailId;
    }

    public String getContactBccMailId()
    {
        return contactBccMailId;
    }

    public void setContactBccMailId(String contactBccMailId)
    {
        this.contactBccMailId = contactBccMailId;
    }
}
