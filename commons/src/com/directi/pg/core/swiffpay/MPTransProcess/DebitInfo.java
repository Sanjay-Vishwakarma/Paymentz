/**
 * DebitInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class DebitInfo  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private java.lang.String ccname;

    private java.lang.String swipedata;

    private int cardpresent;

    private int cardreaderpresent;

    private java.lang.String customerid;

    private float cashbackamount;

    private float amount;

    private java.lang.String merchantordernumber;

    private java.lang.String companyname;

    private com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress;

    private com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress;

    private java.lang.String email;

    private java.lang.String dlnum;

    private java.lang.String ssnum;

    private java.lang.String phone;

    private java.lang.String memo;

    private java.lang.String dobday;

    private java.lang.String dobmonth;

    private java.lang.String dobyear;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail;

    private java.lang.String ipaddress;

    private java.lang.String merchantpin;

    private java.lang.String currencycode;

    private java.lang.String industrycode;

    private com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging;

    private com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    public DebitInfo() {
    }

    public DebitInfo(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           java.lang.String ccname,
           java.lang.String swipedata,
           int cardpresent,
           int cardreaderpresent,
           java.lang.String customerid,
           float cashbackamount,
           float amount,
           java.lang.String merchantordernumber,
           java.lang.String companyname,
           com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress,
           com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress,
           java.lang.String email,
           java.lang.String dlnum,
           java.lang.String ssnum,
           java.lang.String phone,
           java.lang.String memo,
           java.lang.String dobday,
           java.lang.String dobmonth,
           java.lang.String dobyear,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail,
           java.lang.String ipaddress,
           java.lang.String merchantpin,
           java.lang.String currencycode,
           java.lang.String industrycode,
           com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging,
           com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.ccname = ccname;
           this.swipedata = swipedata;
           this.cardpresent = cardpresent;
           this.cardreaderpresent = cardreaderpresent;
           this.customerid = customerid;
           this.cashbackamount = cashbackamount;
           this.amount = amount;
           this.merchantordernumber = merchantordernumber;
           this.companyname = companyname;
           this.billaddress = billaddress;
           this.shipaddress = shipaddress;
           this.email = email;
           this.dlnum = dlnum;
           this.ssnum = ssnum;
           this.phone = phone;
           this.memo = memo;
           this.dobday = dobday;
           this.dobmonth = dobmonth;
           this.dobyear = dobyear;
           this.customizedemail = customizedemail;
           this.ipaddress = ipaddress;
           this.merchantpin = merchantpin;
           this.currencycode = currencycode;
           this.industrycode = industrycode;
           this.hotellodging = hotellodging;
           this.autorental = autorental;
           this.customizedfields = customizedfields;
    }


    /**
     * Gets the acctid value for this DebitInfo.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this DebitInfo.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this DebitInfo.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this DebitInfo.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this DebitInfo.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this DebitInfo.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the ccname value for this DebitInfo.
     * 
     * @return ccname
     */
    public java.lang.String getCcname() {
        return ccname;
    }


    /**
     * Sets the ccname value for this DebitInfo.
     * 
     * @param ccname
     */
    public void setCcname(java.lang.String ccname) {
        this.ccname = ccname;
    }


    /**
     * Gets the swipedata value for this DebitInfo.
     * 
     * @return swipedata
     */
    public java.lang.String getSwipedata() {
        return swipedata;
    }


    /**
     * Sets the swipedata value for this DebitInfo.
     * 
     * @param swipedata
     */
    public void setSwipedata(java.lang.String swipedata) {
        this.swipedata = swipedata;
    }


    /**
     * Gets the cardpresent value for this DebitInfo.
     * 
     * @return cardpresent
     */
    public int getCardpresent() {
        return cardpresent;
    }


    /**
     * Sets the cardpresent value for this DebitInfo.
     * 
     * @param cardpresent
     */
    public void setCardpresent(int cardpresent) {
        this.cardpresent = cardpresent;
    }


    /**
     * Gets the cardreaderpresent value for this DebitInfo.
     * 
     * @return cardreaderpresent
     */
    public int getCardreaderpresent() {
        return cardreaderpresent;
    }


    /**
     * Sets the cardreaderpresent value for this DebitInfo.
     * 
     * @param cardreaderpresent
     */
    public void setCardreaderpresent(int cardreaderpresent) {
        this.cardreaderpresent = cardreaderpresent;
    }


    /**
     * Gets the customerid value for this DebitInfo.
     * 
     * @return customerid
     */
    public java.lang.String getCustomerid() {
        return customerid;
    }


    /**
     * Sets the customerid value for this DebitInfo.
     * 
     * @param customerid
     */
    public void setCustomerid(java.lang.String customerid) {
        this.customerid = customerid;
    }


    /**
     * Gets the cashbackamount value for this DebitInfo.
     * 
     * @return cashbackamount
     */
    public float getCashbackamount() {
        return cashbackamount;
    }


    /**
     * Sets the cashbackamount value for this DebitInfo.
     * 
     * @param cashbackamount
     */
    public void setCashbackamount(float cashbackamount) {
        this.cashbackamount = cashbackamount;
    }


    /**
     * Gets the amount value for this DebitInfo.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this DebitInfo.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the merchantordernumber value for this DebitInfo.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this DebitInfo.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the companyname value for this DebitInfo.
     * 
     * @return companyname
     */
    public java.lang.String getCompanyname() {
        return companyname;
    }


    /**
     * Sets the companyname value for this DebitInfo.
     * 
     * @param companyname
     */
    public void setCompanyname(java.lang.String companyname) {
        this.companyname = companyname;
    }


    /**
     * Gets the billaddress value for this DebitInfo.
     * 
     * @return billaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getBilladdress() {
        return billaddress;
    }


    /**
     * Sets the billaddress value for this DebitInfo.
     * 
     * @param billaddress
     */
    public void setBilladdress(com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress) {
        this.billaddress = billaddress;
    }


    /**
     * Gets the shipaddress value for this DebitInfo.
     * 
     * @return shipaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getShipaddress() {
        return shipaddress;
    }


    /**
     * Sets the shipaddress value for this DebitInfo.
     * 
     * @param shipaddress
     */
    public void setShipaddress(com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress) {
        this.shipaddress = shipaddress;
    }


    /**
     * Gets the email value for this DebitInfo.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this DebitInfo.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the dlnum value for this DebitInfo.
     * 
     * @return dlnum
     */
    public java.lang.String getDlnum() {
        return dlnum;
    }


    /**
     * Sets the dlnum value for this DebitInfo.
     * 
     * @param dlnum
     */
    public void setDlnum(java.lang.String dlnum) {
        this.dlnum = dlnum;
    }


    /**
     * Gets the ssnum value for this DebitInfo.
     * 
     * @return ssnum
     */
    public java.lang.String getSsnum() {
        return ssnum;
    }


    /**
     * Sets the ssnum value for this DebitInfo.
     * 
     * @param ssnum
     */
    public void setSsnum(java.lang.String ssnum) {
        this.ssnum = ssnum;
    }


    /**
     * Gets the phone value for this DebitInfo.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this DebitInfo.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the memo value for this DebitInfo.
     * 
     * @return memo
     */
    public java.lang.String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this DebitInfo.
     * 
     * @param memo
     */
    public void setMemo(java.lang.String memo) {
        this.memo = memo;
    }


    /**
     * Gets the dobday value for this DebitInfo.
     * 
     * @return dobday
     */
    public java.lang.String getDobday() {
        return dobday;
    }


    /**
     * Sets the dobday value for this DebitInfo.
     * 
     * @param dobday
     */
    public void setDobday(java.lang.String dobday) {
        this.dobday = dobday;
    }


    /**
     * Gets the dobmonth value for this DebitInfo.
     * 
     * @return dobmonth
     */
    public java.lang.String getDobmonth() {
        return dobmonth;
    }


    /**
     * Sets the dobmonth value for this DebitInfo.
     * 
     * @param dobmonth
     */
    public void setDobmonth(java.lang.String dobmonth) {
        this.dobmonth = dobmonth;
    }


    /**
     * Gets the dobyear value for this DebitInfo.
     * 
     * @return dobyear
     */
    public java.lang.String getDobyear() {
        return dobyear;
    }


    /**
     * Sets the dobyear value for this DebitInfo.
     * 
     * @param dobyear
     */
    public void setDobyear(java.lang.String dobyear) {
        this.dobyear = dobyear;
    }


    /**
     * Gets the customizedemail value for this DebitInfo.
     * 
     * @return customizedemail
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail getCustomizedemail() {
        return customizedemail;
    }


    /**
     * Sets the customizedemail value for this DebitInfo.
     * 
     * @param customizedemail
     */
    public void setCustomizedemail(com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail) {
        this.customizedemail = customizedemail;
    }


    /**
     * Gets the ipaddress value for this DebitInfo.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this DebitInfo.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the merchantpin value for this DebitInfo.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this DebitInfo.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the currencycode value for this DebitInfo.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this DebitInfo.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the industrycode value for this DebitInfo.
     * 
     * @return industrycode
     */
    public java.lang.String getIndustrycode() {
        return industrycode;
    }


    /**
     * Sets the industrycode value for this DebitInfo.
     * 
     * @param industrycode
     */
    public void setIndustrycode(java.lang.String industrycode) {
        this.industrycode = industrycode;
    }


    /**
     * Gets the hotellodging value for this DebitInfo.
     * 
     * @return hotellodging
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging getHotellodging() {
        return hotellodging;
    }


    /**
     * Sets the hotellodging value for this DebitInfo.
     * 
     * @param hotellodging
     */
    public void setHotellodging(com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging) {
        this.hotellodging = hotellodging;
    }


    /**
     * Gets the autorental value for this DebitInfo.
     * 
     * @return autorental
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.AutoRental getAutorental() {
        return autorental;
    }


    /**
     * Sets the autorental value for this DebitInfo.
     * 
     * @param autorental
     */
    public void setAutorental(com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental) {
        this.autorental = autorental;
    }


    /**
     * Gets the customizedfields value for this DebitInfo.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this DebitInfo.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DebitInfo)) return false;
        DebitInfo other = (DebitInfo) obj;
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
            ((this.ccname==null && other.getCcname()==null) || 
             (this.ccname!=null &&
              this.ccname.equals(other.getCcname()))) &&
            ((this.swipedata==null && other.getSwipedata()==null) || 
             (this.swipedata!=null &&
              this.swipedata.equals(other.getSwipedata()))) &&
            this.cardpresent == other.getCardpresent() &&
            this.cardreaderpresent == other.getCardreaderpresent() &&
            ((this.customerid==null && other.getCustomerid()==null) || 
             (this.customerid!=null &&
              this.customerid.equals(other.getCustomerid()))) &&
            this.cashbackamount == other.getCashbackamount() &&
            this.amount == other.getAmount() &&
            ((this.merchantordernumber==null && other.getMerchantordernumber()==null) || 
             (this.merchantordernumber!=null &&
              this.merchantordernumber.equals(other.getMerchantordernumber()))) &&
            ((this.companyname==null && other.getCompanyname()==null) || 
             (this.companyname!=null &&
              this.companyname.equals(other.getCompanyname()))) &&
            ((this.billaddress==null && other.getBilladdress()==null) || 
             (this.billaddress!=null &&
              this.billaddress.equals(other.getBilladdress()))) &&
            ((this.shipaddress==null && other.getShipaddress()==null) || 
             (this.shipaddress!=null &&
              this.shipaddress.equals(other.getShipaddress()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.dlnum==null && other.getDlnum()==null) || 
             (this.dlnum!=null &&
              this.dlnum.equals(other.getDlnum()))) &&
            ((this.ssnum==null && other.getSsnum()==null) || 
             (this.ssnum!=null &&
              this.ssnum.equals(other.getSsnum()))) &&
            ((this.phone==null && other.getPhone()==null) || 
             (this.phone!=null &&
              this.phone.equals(other.getPhone()))) &&
            ((this.memo==null && other.getMemo()==null) || 
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.dobday==null && other.getDobday()==null) || 
             (this.dobday!=null &&
              this.dobday.equals(other.getDobday()))) &&
            ((this.dobmonth==null && other.getDobmonth()==null) || 
             (this.dobmonth!=null &&
              this.dobmonth.equals(other.getDobmonth()))) &&
            ((this.dobyear==null && other.getDobyear()==null) || 
             (this.dobyear!=null &&
              this.dobyear.equals(other.getDobyear()))) &&
            ((this.customizedemail==null && other.getCustomizedemail()==null) || 
             (this.customizedemail!=null &&
              this.customizedemail.equals(other.getCustomizedemail()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress()))) &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
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
            ((this.customizedfields==null && other.getCustomizedfields()==null) || 
             (this.customizedfields!=null &&
              this.customizedfields.equals(other.getCustomizedfields())));
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
        if (getCcname() != null) {
            _hashCode += getCcname().hashCode();
        }
        if (getSwipedata() != null) {
            _hashCode += getSwipedata().hashCode();
        }
        _hashCode += getCardpresent();
        _hashCode += getCardreaderpresent();
        if (getCustomerid() != null) {
            _hashCode += getCustomerid().hashCode();
        }
        _hashCode += new Float(getCashbackamount()).hashCode();
        _hashCode += new Float(getAmount()).hashCode();
        if (getMerchantordernumber() != null) {
            _hashCode += getMerchantordernumber().hashCode();
        }
        if (getCompanyname() != null) {
            _hashCode += getCompanyname().hashCode();
        }
        if (getBilladdress() != null) {
            _hashCode += getBilladdress().hashCode();
        }
        if (getShipaddress() != null) {
            _hashCode += getShipaddress().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getDlnum() != null) {
            _hashCode += getDlnum().hashCode();
        }
        if (getSsnum() != null) {
            _hashCode += getSsnum().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getDobday() != null) {
            _hashCode += getDobday().hashCode();
        }
        if (getDobmonth() != null) {
            _hashCode += getDobmonth().hashCode();
        }
        if (getDobyear() != null) {
            _hashCode += getDobyear().hashCode();
        }
        if (getCustomizedemail() != null) {
            _hashCode += getCustomizedemail().hashCode();
        }
        if (getIpaddress() != null) {
            _hashCode += getIpaddress().hashCode();
        }
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
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
        if (getCustomizedfields() != null) {
            _hashCode += getCustomizedfields().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DebitInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "DebitInfo"));
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
        elemField.setFieldName("ccname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ccname"));
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
        elemField.setFieldName("cardpresent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardpresent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardreaderpresent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardreaderpresent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customerid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cashbackamount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cashbackamount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantordernumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantordernumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billaddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "address"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "shipaddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "address"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dlnum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dlnum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ssnum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ssnum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dobday");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dobday"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dobmonth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dobmonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dobyear");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dobyear"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customizedemail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedemail"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "customEmail"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipaddress"));
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
        elemField.setFieldName("customizedfields");
        elemField.setXmlName(new javax.xml.namespace.QName("", "customizedfields"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "CustomFields"));
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
