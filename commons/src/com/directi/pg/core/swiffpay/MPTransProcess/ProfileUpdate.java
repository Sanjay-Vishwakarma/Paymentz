/**
 * ProfileUpdate.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class ProfileUpdate  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private java.lang.String last4Digits;

    private java.lang.String userprofileid;

    private java.lang.String merchantpin;

    private java.lang.String ipaddress;

    private java.lang.String ckname;

    private java.lang.String ckaba;

    private java.lang.String ckacct;

    private java.lang.String ckno;

    private java.lang.String cktype;

    private java.lang.String ckaccttype;

    private java.lang.String ccname;

    private java.lang.String swipedata;

    private int cardpresent;

    private int cardreaderpresent;

    private java.lang.String track1;

    private java.lang.String track2;

    private java.lang.String ccnum;

    private java.lang.String cctype;

    private int expmon;

    private int expyear;

    private int cvv2;

    private java.lang.String cvv2_cid;

    private java.lang.String merchantordernumber;

    private java.lang.String companyname;

    private com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress;

    private com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress;

    private java.lang.String email;

    private java.lang.String dlnum;

    private java.lang.String ssnum;

    private java.lang.String phone;

    private java.lang.String memo;

    private java.lang.String currencycode;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    public ProfileUpdate() {
    }

    public ProfileUpdate(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           java.lang.String last4Digits,
           java.lang.String userprofileid,
           java.lang.String merchantpin,
           java.lang.String ipaddress,
           java.lang.String ckname,
           java.lang.String ckaba,
           java.lang.String ckacct,
           java.lang.String ckno,
           java.lang.String cktype,
           java.lang.String ckaccttype,
           java.lang.String ccname,
           java.lang.String swipedata,
           int cardpresent,
           int cardreaderpresent,
           java.lang.String track1,
           java.lang.String track2,
           java.lang.String ccnum,
           java.lang.String cctype,
           int expmon,
           int expyear,
           int cvv2,
           java.lang.String cvv2_cid,
           java.lang.String merchantordernumber,
           java.lang.String companyname,
           com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress,
           com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress,
           java.lang.String email,
           java.lang.String dlnum,
           java.lang.String ssnum,
           java.lang.String phone,
           java.lang.String memo,
           java.lang.String currencycode,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.last4Digits = last4Digits;
           this.userprofileid = userprofileid;
           this.merchantpin = merchantpin;
           this.ipaddress = ipaddress;
           this.ckname = ckname;
           this.ckaba = ckaba;
           this.ckacct = ckacct;
           this.ckno = ckno;
           this.cktype = cktype;
           this.ckaccttype = ckaccttype;
           this.ccname = ccname;
           this.swipedata = swipedata;
           this.cardpresent = cardpresent;
           this.cardreaderpresent = cardreaderpresent;
           this.track1 = track1;
           this.track2 = track2;
           this.ccnum = ccnum;
           this.cctype = cctype;
           this.expmon = expmon;
           this.expyear = expyear;
           this.cvv2 = cvv2;
           this.cvv2_cid = cvv2_cid;
           this.merchantordernumber = merchantordernumber;
           this.companyname = companyname;
           this.billaddress = billaddress;
           this.shipaddress = shipaddress;
           this.email = email;
           this.dlnum = dlnum;
           this.ssnum = ssnum;
           this.phone = phone;
           this.memo = memo;
           this.currencycode = currencycode;
           this.customizedfields = customizedfields;
    }


    /**
     * Gets the acctid value for this ProfileUpdate.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this ProfileUpdate.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this ProfileUpdate.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this ProfileUpdate.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this ProfileUpdate.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this ProfileUpdate.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the last4Digits value for this ProfileUpdate.
     * 
     * @return last4Digits
     */
    public java.lang.String getLast4Digits() {
        return last4Digits;
    }


    /**
     * Sets the last4Digits value for this ProfileUpdate.
     * 
     * @param last4Digits
     */
    public void setLast4Digits(java.lang.String last4Digits) {
        this.last4Digits = last4Digits;
    }


    /**
     * Gets the userprofileid value for this ProfileUpdate.
     * 
     * @return userprofileid
     */
    public java.lang.String getUserprofileid() {
        return userprofileid;
    }


    /**
     * Sets the userprofileid value for this ProfileUpdate.
     * 
     * @param userprofileid
     */
    public void setUserprofileid(java.lang.String userprofileid) {
        this.userprofileid = userprofileid;
    }


    /**
     * Gets the merchantpin value for this ProfileUpdate.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this ProfileUpdate.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the ipaddress value for this ProfileUpdate.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this ProfileUpdate.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the ckname value for this ProfileUpdate.
     * 
     * @return ckname
     */
    public java.lang.String getCkname() {
        return ckname;
    }


    /**
     * Sets the ckname value for this ProfileUpdate.
     * 
     * @param ckname
     */
    public void setCkname(java.lang.String ckname) {
        this.ckname = ckname;
    }


    /**
     * Gets the ckaba value for this ProfileUpdate.
     * 
     * @return ckaba
     */
    public java.lang.String getCkaba() {
        return ckaba;
    }


    /**
     * Sets the ckaba value for this ProfileUpdate.
     * 
     * @param ckaba
     */
    public void setCkaba(java.lang.String ckaba) {
        this.ckaba = ckaba;
    }


    /**
     * Gets the ckacct value for this ProfileUpdate.
     * 
     * @return ckacct
     */
    public java.lang.String getCkacct() {
        return ckacct;
    }


    /**
     * Sets the ckacct value for this ProfileUpdate.
     * 
     * @param ckacct
     */
    public void setCkacct(java.lang.String ckacct) {
        this.ckacct = ckacct;
    }


    /**
     * Gets the ckno value for this ProfileUpdate.
     * 
     * @return ckno
     */
    public java.lang.String getCkno() {
        return ckno;
    }


    /**
     * Sets the ckno value for this ProfileUpdate.
     * 
     * @param ckno
     */
    public void setCkno(java.lang.String ckno) {
        this.ckno = ckno;
    }


    /**
     * Gets the cktype value for this ProfileUpdate.
     * 
     * @return cktype
     */
    public java.lang.String getCktype() {
        return cktype;
    }


    /**
     * Sets the cktype value for this ProfileUpdate.
     * 
     * @param cktype
     */
    public void setCktype(java.lang.String cktype) {
        this.cktype = cktype;
    }


    /**
     * Gets the ckaccttype value for this ProfileUpdate.
     * 
     * @return ckaccttype
     */
    public java.lang.String getCkaccttype() {
        return ckaccttype;
    }


    /**
     * Sets the ckaccttype value for this ProfileUpdate.
     * 
     * @param ckaccttype
     */
    public void setCkaccttype(java.lang.String ckaccttype) {
        this.ckaccttype = ckaccttype;
    }


    /**
     * Gets the ccname value for this ProfileUpdate.
     * 
     * @return ccname
     */
    public java.lang.String getCcname() {
        return ccname;
    }


    /**
     * Sets the ccname value for this ProfileUpdate.
     * 
     * @param ccname
     */
    public void setCcname(java.lang.String ccname) {
        this.ccname = ccname;
    }


    /**
     * Gets the swipedata value for this ProfileUpdate.
     * 
     * @return swipedata
     */
    public java.lang.String getSwipedata() {
        return swipedata;
    }


    /**
     * Sets the swipedata value for this ProfileUpdate.
     * 
     * @param swipedata
     */
    public void setSwipedata(java.lang.String swipedata) {
        this.swipedata = swipedata;
    }


    /**
     * Gets the cardpresent value for this ProfileUpdate.
     * 
     * @return cardpresent
     */
    public int getCardpresent() {
        return cardpresent;
    }


    /**
     * Sets the cardpresent value for this ProfileUpdate.
     * 
     * @param cardpresent
     */
    public void setCardpresent(int cardpresent) {
        this.cardpresent = cardpresent;
    }


    /**
     * Gets the cardreaderpresent value for this ProfileUpdate.
     * 
     * @return cardreaderpresent
     */
    public int getCardreaderpresent() {
        return cardreaderpresent;
    }


    /**
     * Sets the cardreaderpresent value for this ProfileUpdate.
     * 
     * @param cardreaderpresent
     */
    public void setCardreaderpresent(int cardreaderpresent) {
        this.cardreaderpresent = cardreaderpresent;
    }


    /**
     * Gets the track1 value for this ProfileUpdate.
     * 
     * @return track1
     */
    public java.lang.String getTrack1() {
        return track1;
    }


    /**
     * Sets the track1 value for this ProfileUpdate.
     * 
     * @param track1
     */
    public void setTrack1(java.lang.String track1) {
        this.track1 = track1;
    }


    /**
     * Gets the track2 value for this ProfileUpdate.
     * 
     * @return track2
     */
    public java.lang.String getTrack2() {
        return track2;
    }


    /**
     * Sets the track2 value for this ProfileUpdate.
     * 
     * @param track2
     */
    public void setTrack2(java.lang.String track2) {
        this.track2 = track2;
    }


    /**
     * Gets the ccnum value for this ProfileUpdate.
     * 
     * @return ccnum
     */
    public java.lang.String getCcnum() {
        return ccnum;
    }


    /**
     * Sets the ccnum value for this ProfileUpdate.
     * 
     * @param ccnum
     */
    public void setCcnum(java.lang.String ccnum) {
        this.ccnum = ccnum;
    }


    /**
     * Gets the cctype value for this ProfileUpdate.
     * 
     * @return cctype
     */
    public java.lang.String getCctype() {
        return cctype;
    }


    /**
     * Sets the cctype value for this ProfileUpdate.
     * 
     * @param cctype
     */
    public void setCctype(java.lang.String cctype) {
        this.cctype = cctype;
    }


    /**
     * Gets the expmon value for this ProfileUpdate.
     * 
     * @return expmon
     */
    public int getExpmon() {
        return expmon;
    }


    /**
     * Sets the expmon value for this ProfileUpdate.
     * 
     * @param expmon
     */
    public void setExpmon(int expmon) {
        this.expmon = expmon;
    }


    /**
     * Gets the expyear value for this ProfileUpdate.
     * 
     * @return expyear
     */
    public int getExpyear() {
        return expyear;
    }


    /**
     * Sets the expyear value for this ProfileUpdate.
     * 
     * @param expyear
     */
    public void setExpyear(int expyear) {
        this.expyear = expyear;
    }


    /**
     * Gets the cvv2 value for this ProfileUpdate.
     * 
     * @return cvv2
     */
    public int getCvv2() {
        return cvv2;
    }


    /**
     * Sets the cvv2 value for this ProfileUpdate.
     * 
     * @param cvv2
     */
    public void setCvv2(int cvv2) {
        this.cvv2 = cvv2;
    }


    /**
     * Gets the cvv2_cid value for this ProfileUpdate.
     * 
     * @return cvv2_cid
     */
    public java.lang.String getCvv2_cid() {
        return cvv2_cid;
    }


    /**
     * Sets the cvv2_cid value for this ProfileUpdate.
     * 
     * @param cvv2_cid
     */
    public void setCvv2_cid(java.lang.String cvv2_cid) {
        this.cvv2_cid = cvv2_cid;
    }


    /**
     * Gets the merchantordernumber value for this ProfileUpdate.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this ProfileUpdate.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the companyname value for this ProfileUpdate.
     * 
     * @return companyname
     */
    public java.lang.String getCompanyname() {
        return companyname;
    }


    /**
     * Sets the companyname value for this ProfileUpdate.
     * 
     * @param companyname
     */
    public void setCompanyname(java.lang.String companyname) {
        this.companyname = companyname;
    }


    /**
     * Gets the billaddress value for this ProfileUpdate.
     * 
     * @return billaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getBilladdress() {
        return billaddress;
    }


    /**
     * Sets the billaddress value for this ProfileUpdate.
     * 
     * @param billaddress
     */
    public void setBilladdress(com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress) {
        this.billaddress = billaddress;
    }


    /**
     * Gets the shipaddress value for this ProfileUpdate.
     * 
     * @return shipaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getShipaddress() {
        return shipaddress;
    }


    /**
     * Sets the shipaddress value for this ProfileUpdate.
     * 
     * @param shipaddress
     */
    public void setShipaddress(com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress) {
        this.shipaddress = shipaddress;
    }


    /**
     * Gets the email value for this ProfileUpdate.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this ProfileUpdate.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the dlnum value for this ProfileUpdate.
     * 
     * @return dlnum
     */
    public java.lang.String getDlnum() {
        return dlnum;
    }


    /**
     * Sets the dlnum value for this ProfileUpdate.
     * 
     * @param dlnum
     */
    public void setDlnum(java.lang.String dlnum) {
        this.dlnum = dlnum;
    }


    /**
     * Gets the ssnum value for this ProfileUpdate.
     * 
     * @return ssnum
     */
    public java.lang.String getSsnum() {
        return ssnum;
    }


    /**
     * Sets the ssnum value for this ProfileUpdate.
     * 
     * @param ssnum
     */
    public void setSsnum(java.lang.String ssnum) {
        this.ssnum = ssnum;
    }


    /**
     * Gets the phone value for this ProfileUpdate.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this ProfileUpdate.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the memo value for this ProfileUpdate.
     * 
     * @return memo
     */
    public java.lang.String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this ProfileUpdate.
     * 
     * @param memo
     */
    public void setMemo(java.lang.String memo) {
        this.memo = memo;
    }


    /**
     * Gets the currencycode value for this ProfileUpdate.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this ProfileUpdate.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the customizedfields value for this ProfileUpdate.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this ProfileUpdate.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProfileUpdate)) return false;
        ProfileUpdate other = (ProfileUpdate) obj;
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
            ((this.ckname==null && other.getCkname()==null) || 
             (this.ckname!=null &&
              this.ckname.equals(other.getCkname()))) &&
            ((this.ckaba==null && other.getCkaba()==null) || 
             (this.ckaba!=null &&
              this.ckaba.equals(other.getCkaba()))) &&
            ((this.ckacct==null && other.getCkacct()==null) || 
             (this.ckacct!=null &&
              this.ckacct.equals(other.getCkacct()))) &&
            ((this.ckno==null && other.getCkno()==null) || 
             (this.ckno!=null &&
              this.ckno.equals(other.getCkno()))) &&
            ((this.cktype==null && other.getCktype()==null) || 
             (this.cktype!=null &&
              this.cktype.equals(other.getCktype()))) &&
            ((this.ckaccttype==null && other.getCkaccttype()==null) || 
             (this.ckaccttype!=null &&
              this.ckaccttype.equals(other.getCkaccttype()))) &&
            ((this.ccname==null && other.getCcname()==null) || 
             (this.ccname!=null &&
              this.ccname.equals(other.getCcname()))) &&
            ((this.swipedata==null && other.getSwipedata()==null) || 
             (this.swipedata!=null &&
              this.swipedata.equals(other.getSwipedata()))) &&
            this.cardpresent == other.getCardpresent() &&
            this.cardreaderpresent == other.getCardreaderpresent() &&
            ((this.track1==null && other.getTrack1()==null) || 
             (this.track1!=null &&
              this.track1.equals(other.getTrack1()))) &&
            ((this.track2==null && other.getTrack2()==null) || 
             (this.track2!=null &&
              this.track2.equals(other.getTrack2()))) &&
            ((this.ccnum==null && other.getCcnum()==null) || 
             (this.ccnum!=null &&
              this.ccnum.equals(other.getCcnum()))) &&
            ((this.cctype==null && other.getCctype()==null) || 
             (this.cctype!=null &&
              this.cctype.equals(other.getCctype()))) &&
            this.expmon == other.getExpmon() &&
            this.expyear == other.getExpyear() &&
            this.cvv2 == other.getCvv2() &&
            ((this.cvv2_cid==null && other.getCvv2_cid()==null) || 
             (this.cvv2_cid!=null &&
              this.cvv2_cid.equals(other.getCvv2_cid()))) &&
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
            ((this.currencycode==null && other.getCurrencycode()==null) || 
             (this.currencycode!=null &&
              this.currencycode.equals(other.getCurrencycode()))) &&
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
        if (getCkname() != null) {
            _hashCode += getCkname().hashCode();
        }
        if (getCkaba() != null) {
            _hashCode += getCkaba().hashCode();
        }
        if (getCkacct() != null) {
            _hashCode += getCkacct().hashCode();
        }
        if (getCkno() != null) {
            _hashCode += getCkno().hashCode();
        }
        if (getCktype() != null) {
            _hashCode += getCktype().hashCode();
        }
        if (getCkaccttype() != null) {
            _hashCode += getCkaccttype().hashCode();
        }
        if (getCcname() != null) {
            _hashCode += getCcname().hashCode();
        }
        if (getSwipedata() != null) {
            _hashCode += getSwipedata().hashCode();
        }
        _hashCode += getCardpresent();
        _hashCode += getCardreaderpresent();
        if (getTrack1() != null) {
            _hashCode += getTrack1().hashCode();
        }
        if (getTrack2() != null) {
            _hashCode += getTrack2().hashCode();
        }
        if (getCcnum() != null) {
            _hashCode += getCcnum().hashCode();
        }
        if (getCctype() != null) {
            _hashCode += getCctype().hashCode();
        }
        _hashCode += getExpmon();
        _hashCode += getExpyear();
        _hashCode += getCvv2();
        if (getCvv2_cid() != null) {
            _hashCode += getCvv2_cid().hashCode();
        }
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
        if (getCurrencycode() != null) {
            _hashCode += getCurrencycode().hashCode();
        }
        if (getCustomizedfields() != null) {
            _hashCode += getCustomizedfields().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProfileUpdate.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProfileUpdate"));
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
        elemField.setFieldName("ckname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckaba");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckaba"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckacct");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckacct"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckno");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cktype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cktype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckaccttype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckaccttype"));
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
        elemField.setFieldName("track1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "track1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("track2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "track2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccnum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ccnum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cctype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cctype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expmon");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expmon"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expyear");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expyear"));
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
        elemField.setFieldName("currencycode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currencycode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
