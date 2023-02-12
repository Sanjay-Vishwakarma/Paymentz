package com.fraud.vo;

import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;

/**
 * Created by SurajT on 12/20/2017.
 */
public class PZFraudDocVerifyRequestVO
{
    String customer_registration_id;
    String method;
    String filePath;
    String fileName;
    String fileName2;
    String fileName3;
    String fileName4;

    String partnerId;
    FraudAccountDetailsVO fraudAccountDetailsVO;
    PZCustomerDetailsVO pzCustomerDetailsVO;

    String notificationUrl;

    public String getCustomer_registration_id()
    {
        return customer_registration_id;
    }

    public void setCustomer_registration_id(String customer_registration_id)
    {
        this.customer_registration_id = customer_registration_id;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName2()
    {
        return fileName2;
    }

    public void setFileName2(String fileName2)
    {
        this.fileName2 = fileName2;
    }

    public String getFileName3()
    {
        return fileName3;
    }

    public void setFileName3(String fileName3)
    {
        this.fileName3 = fileName3;
    }

    public String getFileName4()
    {
        return fileName4;
    }

    public void setFileName4(String fileName4)
    {
        this.fileName4 = fileName4;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public FraudAccountDetailsVO getFraudAccountDetailsVO()
    {
        return fraudAccountDetailsVO;
    }

    public void setFraudAccountDetailsVO(FraudAccountDetailsVO fraudAccountDetailsVO)
    {
        this.fraudAccountDetailsVO = fraudAccountDetailsVO;
    }

    public PZCustomerDetailsVO getPzCustomerDetailsVO()
    {
        return pzCustomerDetailsVO;
    }

    public void setPzCustomerDetailsVO(PZCustomerDetailsVO pzCustomerDetailsVO)
    {
        this.pzCustomerDetailsVO = pzCustomerDetailsVO;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }
}
