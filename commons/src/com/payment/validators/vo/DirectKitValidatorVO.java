package com.payment.validators.vo;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/14
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectKitValidatorVO extends CommonValidatorVO
{
    private String status;
    private String statusMsg;
    private String action;
    private String version;
    private String isEncrypted;
    private String transactionType;

    @Override
    public String getTransactionType()
    {
        return transactionType;
    }

    @Override
    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusMsg()
    {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg)
    {
        this.statusMsg = statusMsg;
    }

    public String getIsEncrypted()
    {
        return isEncrypted;
    }

    public void setIsEncrypted(String isEncrypted)
    {
        this.isEncrypted = isEncrypted;
    }
}
