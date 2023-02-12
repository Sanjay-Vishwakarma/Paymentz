/**
 * ProfileSale.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class ProfileSale  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private float amount;

    private java.lang.String last4Digits;

    private java.lang.String userprofileid;

    private java.lang.String merchantpin;

    private java.lang.String ipaddress;

    private int authonly;

    private int cvv2;

    private java.lang.String cvv2_cid;

    private java.lang.String swipedata;

    private java.lang.String voiceauth;

    private java.lang.String merchantordernumber;

    private int manualrecurring;

    private com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring;

    private java.lang.String currencycode;

    private java.lang.String industrycode;

    private com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging;

    private com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    private com.directi.pg.core.swiffpay.MPTransProcess.FSA fsa;

    private com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2;

    private java.lang.String otp;

    private java.lang.String deviceid;

    public ProfileSale() {
    }

    public ProfileSale(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           float amount,
           java.lang.String last4Digits,
           java.lang.String userprofileid,
           java.lang.String merchantpin,
           java.lang.String ipaddress,
           int authonly,
           int cvv2,
           java.lang.String cvv2_cid,
           java.lang.String swipedata,
           java.lang.String voiceauth,
           java.lang.String merchantordernumber,
           int manualrecurring,
           com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring,
           java.lang.String currencycode,
           java.lang.String industrycode,
           com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging,
           com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields,
           com.directi.pg.core.swiffpay.MPTransProcess.FSA fsa,
           com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2,
           java.lang.String otp,
           java.lang.String deviceid) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.amount = amount;
           this.last4Digits = last4Digits;
           this.userprofileid = userprofileid;
           this.merchantpin = merchantpin;
           this.ipaddress = ipaddress;
           this.authonly = authonly;
           this.cvv2 = cvv2;
           this.cvv2_cid = cvv2_cid;
           this.swipedata = swipedata;
           this.voiceauth = voiceauth;
           this.merchantordernumber = merchantordernumber;
           this.manualrecurring = manualrecurring;
           this.recurring = recurring;
           this.currencycode = currencycode;
           this.industrycode = industrycode;
           this.hotellodging = hotellodging;
           this.autorental = autorental;
           this.customizedemail = customizedemail;
           this.customizedfields = customizedfields;
           this.fsa = fsa;
           this.purchasecardlevel2 = purchasecardlevel2;
           this.otp = otp;
           this.deviceid = deviceid;
    }


    /**
     * Gets the acctid value for this ProfileSale.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this ProfileSale.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this ProfileSale.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this ProfileSale.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this ProfileSale.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this ProfileSale.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the amount value for this ProfileSale.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this ProfileSale.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the last4Digits value for this ProfileSale.
     * 
     * @return last4Digits
     */
    public java.lang.String getLast4Digits() {
        return last4Digits;
    }


    /**
     * Sets the last4Digits value for this ProfileSale.
     * 
     * @param last4Digits
     */
    public void setLast4Digits(java.lang.String last4Digits) {
        this.last4Digits = last4Digits;
    }


    /**
     * Gets the userprofileid value for this ProfileSale.
     * 
     * @return userprofileid
     */
    public java.lang.String getUserprofileid() {
        return userprofileid;
    }


    /**
     * Sets the userprofileid value for this ProfileSale.
     * 
     * @param userprofileid
     */
    public void setUserprofileid(java.lang.String userprofileid) {
        this.userprofileid = userprofileid;
    }


    /**
     * Gets the merchantpin value for this ProfileSale.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this ProfileSale.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the ipaddress value for this ProfileSale.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this ProfileSale.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the authonly value for this ProfileSale.
     * 
     * @return authonly
     */
    public int getAuthonly() {
        return authonly;
    }


    /**
     * Sets the authonly value for this ProfileSale.
     * 
     * @param authonly
     */
    public void setAuthonly(int authonly) {
        this.authonly = authonly;
    }


    /**
     * Gets the cvv2 value for this ProfileSale.
     * 
     * @return cvv2
     */
    public int getCvv2() {
        return cvv2;
    }


    /**
     * Sets the cvv2 value for this ProfileSale.
     * 
     * @param cvv2
     */
    public void setCvv2(int cvv2) {
        this.cvv2 = cvv2;
    }


    /**
     * Gets the cvv2_cid value for this ProfileSale.
     * 
     * @return cvv2_cid
     */
    public java.lang.String getCvv2_cid() {
        return cvv2_cid;
    }


    /**
     * Sets the cvv2_cid value for this ProfileSale.
     * 
     * @param cvv2_cid
     */
    public void setCvv2_cid(java.lang.String cvv2_cid) {
        this.cvv2_cid = cvv2_cid;
    }


    /**
     * Gets the swipedata value for this ProfileSale.
     * 
     * @return swipedata
     */
    public java.lang.String getSwipedata() {
        return swipedata;
    }


    /**
     * Sets the swipedata value for this ProfileSale.
     * 
     * @param swipedata
     */
    public void setSwipedata(java.lang.String swipedata) {
        this.swipedata = swipedata;
    }


    /**
     * Gets the voiceauth value for this ProfileSale.
     * 
     * @return voiceauth
     */
    public java.lang.String getVoiceauth() {
        return voiceauth;
    }


    /**
     * Sets the voiceauth value for this ProfileSale.
     * 
     * @param voiceauth
     */
    public void setVoiceauth(java.lang.String voiceauth) {
        this.voiceauth = voiceauth;
    }


    /**
     * Gets the merchantordernumber value for this ProfileSale.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this ProfileSale.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the manualrecurring value for this ProfileSale.
     * 
     * @return manualrecurring
     */
    public int getManualrecurring() {
        return manualrecurring;
    }


    /**
     * Sets the manualrecurring value for this ProfileSale.
     * 
     * @param manualrecurring
     */
    public void setManualrecurring(int manualrecurring) {
        this.manualrecurring = manualrecurring;
    }


    /**
     * Gets the recurring value for this ProfileSale.
     * 
     * @return recurring
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Recur getRecurring() {
        return recurring;
    }


    /**
     * Sets the recurring value for this ProfileSale.
     * 
     * @param recurring
     */
    public void setRecurring(com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring) {
        this.recurring = recurring;
    }


    /**
     * Gets the currencycode value for this ProfileSale.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this ProfileSale.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the industrycode value for this ProfileSale.
     * 
     * @return industrycode
     */
    public java.lang.String getIndustrycode() {
        return industrycode;
    }


    /**
     * Sets the industrycode value for this ProfileSale.
     * 
     * @param industrycode
     */
    public void setIndustrycode(java.lang.String industrycode) {
        this.industrycode = industrycode;
    }


    /**
     * Gets the hotellodging value for this ProfileSale.
     * 
     * @return hotellodging
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging getHotellodging() {
        return hotellodging;
    }


    /**
     * Sets the hotellodging value for this ProfileSale.
     * 
     * @param hotellodging
     */
    public void setHotellodging(com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging) {
        this.hotellodging = hotellodging;
    }


    /**
     * Gets the autorental value for this ProfileSale.
     * 
     * @return autorental
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.AutoRental getAutorental() {
        return autorental;
    }


    /**
     * Sets the autorental value for this ProfileSale.
     * 
     * @param autorental
     */
    public void setAutorental(com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental) {
        this.autorental = autorental;
    }


    /**
     * Gets the customizedemail value for this ProfileSale.
     * 
     * @return customizedemail
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail getCustomizedemail() {
        return customizedemail;
    }


    /**
     * Sets the customizedemail value for this ProfileSale.
     * 
     * @param customizedemail
     */
    public void setCustomizedemail(com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail) {
        this.customizedemail = customizedemail;
    }


    /**
     * Gets the customizedfields value for this ProfileSale.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this ProfileSale.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }


    /**
     * Gets the fsa value for this ProfileSale.
     * 
     * @return fsa
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.FSA getFsa() {
        return fsa;
    }


    /**
     * Sets the fsa value for this ProfileSale.
     * 
     * @param fsa
     */
    public void setFsa(com.directi.pg.core.swiffpay.MPTransProcess.FSA fsa) {
        this.fsa = fsa;
    }


    /**
     * Gets the purchasecardlevel2 value for this ProfileSale.
     * 
     * @return purchasecardlevel2
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 getPurchasecardlevel2() {
        return purchasecardlevel2;
    }


    /**
     * Sets the purchasecardlevel2 value for this ProfileSale.
     * 
     * @param purchasecardlevel2
     */
    public void setPurchasecardlevel2(com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2) {
        this.purchasecardlevel2 = purchasecardlevel2;
    }


    /**
     * Gets the otp value for this ProfileSale.
     * 
     * @return otp
     */
    public java.lang.String getOtp() {
        return otp;
    }


    /**
     * Sets the otp value for this ProfileSale.
     * 
     * @param otp
     */
    public void setOtp(java.lang.String otp) {
        this.otp = otp;
    }


    /**
     * Gets the deviceid value for this ProfileSale.
     * 
     * @return deviceid
     */
    public java.lang.String getDeviceid() {
        return deviceid;
    }


    /**
     * Sets the deviceid value for this ProfileSale.
     * 
     * @param deviceid
     */
    public void setDeviceid(java.lang.String deviceid) {
        this.deviceid = deviceid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProfileSale)) return false;
        ProfileSale other = (ProfileSale) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.acctid==null && other.getAcctid()==null) || 
             (this.acctid!=null &&
              this.acctid.equals(other.getAcctid()))) &&
            ((this.accountkey==null && other.getAccountkey()==null) || 
             (this.accountkey!=null &&
              this.accountkey.equals(other.getAccountkey()))) &&
            ((this.subid==null && other.getSubid()==null) || 
             (this.subid!=null &&
              this.subid.equals(other.getSubid()))) &&
            this.amount == other.getAmount() &&
            ((this.last4Digits==null && other.getLast4Digits()==null) || 
             (this.last4Digits!=null &&
              this.last4Digits.equals(other.getLast4Digits()))) &&
            ((this.userprofileid==null && other.getUserprofileid()==null) || 
             (this.userprofileid!=null &&
              this.userprofileid.equals(other.getUserprofileid()))) &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress()))) &&
            this.authonly == other.getAuthonly() &&
            this.cvv2 == other.getCvv2() &&
            ((this.cvv2_cid==null && other.getCvv2_cid()==null) || 
             (this.cvv2_cid!=null &&
              this.cvv2_cid.equals(other.getCvv2_cid()))) &&
            ((this.swipedata==null && other.getSwipedata()==null) || 
             (this.swipedata!=null &&
              this.swipedata.equals(other.getSwipedata()))) &&
            ((this.voiceauth==null && other.getVoiceauth()==null) || 
             (this.voiceauth!=null &&
              this.voiceauth.equals(other.getVoiceauth()))) &&
            ((this.merchantordernumber==null && other.getMerchantordernumber()==null) || 
             (this.merchantordernumber!=null &&
              this.merchantordernumber.equals(other.getMerchantordernumber()))) &&
            this.manualrecurring == other.getManualrecurring() &&
            ((this.recurring==null && other.getRecurring()==null) || 
             (this.recurring!=null &&
              this.recurring.equals(other.getRecurring()))) &&
            ((this.currencycode==null && other.getCurrencycode()==null) || 
             (this.currencycode!=null &&
              this.currencycode.equals(other.getCurrencycode()))) &&
            ((this.industrycode==null && other.getIndustrycode()==null) || 
             (this.industrycode!=null &&
              this.industrycode.equals(other.getIndustrycode()))) &&
            ((this.hotellodging==null && other.getHotellodging()==null) || 
             (this.hotellodging!=null &&
              this.hotellodging.equals(other.getHotellodging()))) &&
            ((this.autorental==null && other.getAutorental()==null) || 
             (this.autorental!=null &&
              this.autorental.equals(other.getAutorental()))) &&
            ((this.customizedemail==null && other.getCustomizedemail()==null) || 
             (this.customizedemail!=null &&
              this.customizedemail.equals(other.getCustomizedemail()))) &&
            ((this.customizedfields==null && other.getCustomizedfields()==null) || 
             (this.customizedfields!=null &&
              this.customizedfields.equals(other.getCustomizedfields()))) &&
            ((this.fsa==null && other.getFsa()==null) || 
             (this.fsa!=null &&
              this.fsa.equals(other.getFsa()))) &&
            ((this.purchasecardlevel2==null && other.getPurchasecardlevel2()==null) || 
             (this.purchasecardlevel2!=null &&
              this.purchasecardlevel2.equals(other.getPurchasecardlevel2()))) &&
            ((this.otp==null && other.getOtp()==null) || 
             (this.otp!=null &&
              this.otp.equals(other.getOtp()))) &&
            ((this.deviceid==null && other.getDeviceid()==null) || 
             (this.deviceid!=null &&
              this.deviceid.equals(other.getDeviceid())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAcctid() != null) {
            _hashCode += getAcctid().hashCode();
        }
        if (getAccountkey() != null) {
            _hashCode += getAccountkey().hashCode();
        }
        if (getSubid() != null) {
            _hashCode += getSubid().hashCode();
        }
        _hashCode += new Float(getAmount()).hashCode();
        if (getLast4Digits() != null) {
            _hashCode += getLast4Digits().hashCode();
        }
        if (getUserprofileid() != null) {
            _hashCode += getUserprofileid().hashCode();
        }
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
        }
        if (getIpaddress() != null) {
            _hashCode += getIpaddress().hashCode();
        }
        _hashCode += getAuthonly();
        _hashCode += getCvv2();
        if (getCvv2_cid() != null) {
            _hashCode += getCvv2_cid().hashCode();
        }
        if (getSwipedata() != null) {
            _hashCode += getSwipedata().hashCode();
        }
        if (getVoiceauth() != null) {
            _hashCode += getVoiceauth().hashCode();
        }
        if (getMerchantordernumber() != null) {
            _hashCode += getMerchantordernumber().hashCode();
        }
        _hashCode += getManualrecurring();
        if (getRecurring() != null) {
            _hashCode += getRecurring().hashCode();
        }
        if (getCurrencycode() != null) {
            _hashCode += getCurrencycode().hashCode();
        }
        if (getIndustrycode() != null) {
            _hashCode += getIndustrycode().hashCode();
        }
        if (getHotellodging() != null) {
            _hashCode += getHotellodging().hashCode();
        }
        if (getAutorental() != null) {
            _hashCode += getAutorental().hashCode();
        }
        if (getCustomizedemail() != null) {
            _hashCode += getCustomizedemail().hashCode();
        }
        if (getCustomizedfields() != null) {
            _hashCode += getCustomizedfields().hashCode();
        }
        if (getFsa() != null) {
            _hashCode += getFsa().hashCode();
        }
        if (getPurchasecardlevel2() != null) {
            _hashCode += getPurchasecardlevel2().hashCode();
        }
        if (getOtp() != null) {
            _hashCode += getOtp().hashCode();
        }
        if (getDeviceid() != null) {
            _hashCode += getDeviceid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProfileSale.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileSale"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acctid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acctid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountkey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountkey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "subid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last4Digits");
        elemField.setXmlName(new javax.xml.namespace.QName("", "last4digits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userprofileid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userprofileid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantpin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantpin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipaddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authonly");
        elemField.setXmlName(new javax.xml.namespace.QName("", "authonly"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvv2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cvv2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvv2_cid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cvv2_cid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("swipedata");
        elemField.setXmlName(new javax.xml.namespace.QName("", "swipedata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voiceauth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "voiceauth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantordernumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantordernumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manualrecurring");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manualrecurring"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recurring");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recurring"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "Recur"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currencycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("industrycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "industrycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotellodging");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hotellodging"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "HotelLodging"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("autorental");
        elemField.setXmlName(new javax.xml.namespace.QName("", "autorental"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "AutoRental"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customizedemail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedemail"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "customEmail"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customizedfields");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedfields"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "CustomFields"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fsa");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fsa"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "FSA"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchasecardlevel2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "purchasecardlevel2"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "PurchaseCardLevel2"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("otp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "otp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "deviceid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
