package com.payment.common.core;

import com.directi.pg.core.valueObjects.GenericVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/4/13
 * Time: 12:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommMerchantVO extends GenericVO
{
    private String merchantId;
    private String merchantUsername;
    private String password;
    private String merchantKey;
    private String aliasName ;
    private String displayName;
    private String accountId;
    private String isService;
    private String merchantOrganizationName;
    private String partnerSupportContactNumber;
    private String hostUrl;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String sitename;
    private String address;
    private String brandName;
    private String country;
    private String merchantSupportNumber;   //Currently Used For Duspay Descriptor
    private String supportName;


    public String getAliasName()
    {
        return aliasName;
    }

    public void setAliasName(String aliasName)
    {
        this.aliasName = aliasName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getMerchantUsername()
    {
        return merchantUsername;
    }

    public void setMerchantUsername(String merchantUsername)
    {
        this.merchantUsername = merchantUsername;
    }

    public String getMerchantKey()
    {
        return merchantKey;
    }

    public void setMerchantKey(String merchantKey)
    {
        this.merchantKey = merchantKey;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getIsService()
    {
        return isService;
    }

    public void setIsService(String isService)
    {
        this.isService = isService;
    }

    public String getMerchantOrganizationName()
    {
        return merchantOrganizationName;
    }

    public void setMerchantOrganizationName(String merchantOrganizationName)
    {
        this.merchantOrganizationName = merchantOrganizationName;
    }

    public String getPartnerSupportContactNumber()
    {
        return partnerSupportContactNumber;
    }

    public void setPartnerSupportContactNumber(String partnerSupportContactNumber)
    {
        this.partnerSupportContactNumber = partnerSupportContactNumber;
    }

    public String getHostUrl()
    {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl)
    {
        this.hostUrl = hostUrl;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getSitename()
    {
        return sitename;
    }

    public void setSitename(String sitename)
    {
        this.sitename = sitename;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getBrandName()
    {
        return brandName;
    }

    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }

    public String getMerchantSupportNumber()
    {
        return merchantSupportNumber;
    }

    public void setMerchantSupportNumber(String merchantSupportNumber)
    {
        this.merchantSupportNumber = merchantSupportNumber;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getSupportName()
    {
        return supportName;
    }

    public void setSupportName(String supportName)
    {
        this.supportName = supportName;
    }
}
