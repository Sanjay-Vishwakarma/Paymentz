/**
 * CreditCardInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class CreditCardInfo  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private java.lang.String ccname;

    private java.lang.String swipedata;

    private int cardpresent;

    private int cardreaderpresent;

    private java.lang.String voiceauth;

    private java.lang.String track1;

    private java.lang.String track2;

    private java.lang.String ccnum;

    private java.lang.String cctype;

    private int expmon;

    private int expyear;

    private int cvv2;

    private java.lang.String cvv2_cid;

    private float amount;

    private java.lang.String merchantordernumber;

    private java.lang.String companyname;

    private com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress;

    private com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress;

    private java.lang.String email;

    private java.lang.String dlnum;

    private java.lang.String ssnum;

    private java.lang.String phone;

    private java.lang.String dobday;

    private java.lang.String dobmonth;

    private java.lang.String dobyear;

    private java.lang.String memo;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail;

    private com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring;

    private java.lang.String ipaddress;

    private int accttype;

    private java.lang.String merchantpin;

    private java.lang.String currencycode;

    private java.lang.String industrycode;

    private java.lang.String dynamicdescriptor;

    private int profileactiontype;

    private int manualrecurring;

    private com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging;

    private com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    private com.directi.pg.core.swiffpay.MPTransProcess.AutoRepair autorepair;

    private com.directi.pg.core.swiffpay.MPTransProcess.FSA fsa;

    private com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2;

    private com.directi.pg.core.swiffpay.MPTransProcess.Restaurant restaurant;

    private java.lang.String otp;

    private java.lang.String deviceid;

    public CreditCardInfo() {
    }

    public CreditCardInfo(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           java.lang.String ccname,
           java.lang.String swipedata,
           int cardpresent,
           int cardreaderpresent,
           java.lang.String voiceauth,
           java.lang.String track1,
           java.lang.String track2,
           java.lang.String ccnum,
           java.lang.String cctype,
           int expmon,
           int expyear,
           int cvv2,
           java.lang.String cvv2_cid,
           float amount,
           java.lang.String merchantordernumber,
           java.lang.String companyname,
           com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress,
           com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress,
           java.lang.String email,
           java.lang.String dlnum,
           java.lang.String ssnum,
           java.lang.String phone,
           java.lang.String dobday,
           java.lang.String dobmonth,
           java.lang.String dobyear,
           java.lang.String memo,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail,
           com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring,
           java.lang.String ipaddress,
           int accttype,
           java.lang.String merchantpin,
           java.lang.String currencycode,
           java.lang.String industrycode,
           java.lang.String dynamicdescriptor,
           int profileactiontype,
           int manualrecurring,
           com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging,
           com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields,
           com.directi.pg.core.swiffpay.MPTransProcess.AutoRepair autorepair,
           com.directi.pg.core.swiffpay.MPTransProcess.FSA fsa,
           com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2,
           com.directi.pg.core.swiffpay.MPTransProcess.Restaurant restaurant,
           java.lang.String otp,
           java.lang.String deviceid) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.ccname = ccname;
           this.swipedata = swipedata;
           this.cardpresent = cardpresent;
           this.cardreaderpresent = cardreaderpresent;
           this.voiceauth = voiceauth;
           this.track1 = track1;
           this.track2 = track2;
           this.ccnum = ccnum;
           this.cctype = cctype;
           this.expmon = expmon;
           this.expyear = expyear;
           this.cvv2 = cvv2;
           this.cvv2_cid = cvv2_cid;
           this.amount = amount;
           this.merchantordernumber = merchantordernumber;
           this.companyname = companyname;
           this.billaddress = billaddress;
           this.shipaddress = shipaddress;
           this.email = email;
           this.dlnum = dlnum;
           this.ssnum = ssnum;
           this.phone = phone;
           this.dobday = dobday;
           this.dobmonth = dobmonth;
           this.dobyear = dobyear;
           this.memo = memo;
           this.customizedemail = customizedemail;
           this.recurring = recurring;
           this.ipaddress = ipaddress;
           this.accttype = accttype;
           this.merchantpin = merchantpin;
           this.currencycode = currencycode;
           this.industrycode = industrycode;
           this.dynamicdescriptor = dynamicdescriptor;
           this.profileactiontype = profileactiontype;
           this.manualrecurring = manualrecurring;
           this.hotellodging = hotellodging;
           this.autorental = autorental;
           this.customizedfields = customizedfields;
           this.autorepair = autorepair;
           this.fsa = fsa;
           this.purchasecardlevel2 = purchasecardlevel2;
           this.restaurant = restaurant;
           this.otp = otp;
           this.deviceid = deviceid;
    }


    /**
     * Gets the acctid value for this CreditCardInfo.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this CreditCardInfo.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this CreditCardInfo.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this CreditCardInfo.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this CreditCardInfo.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this CreditCardInfo.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the ccname value for this CreditCardInfo.
     * 
     * @return ccname
     */
    public java.lang.String getCcname() {
        return ccname;
    }


    /**
     * Sets the ccname value for this CreditCardInfo.
     * 
     * @param ccname
     */
    public void setCcname(java.lang.String ccname) {
        this.ccname = ccname;
    }


    /**
     * Gets the swipedata value for this CreditCardInfo.
     * 
     * @return swipedata
     */
    public java.lang.String getSwipedata() {
        return swipedata;
    }


    /**
     * Sets the swipedata value for this CreditCardInfo.
     * 
     * @param swipedata
     */
    public void setSwipedata(java.lang.String swipedata) {
        this.swipedata = swipedata;
    }


    /**
     * Gets the cardpresent value for this CreditCardInfo.
     * 
     * @return cardpresent
     */
    public int getCardpresent() {
        return cardpresent;
    }


    /**
     * Sets the cardpresent value for this CreditCardInfo.
     * 
     * @param cardpresent
     */
    public void setCardpresent(int cardpresent) {
        this.cardpresent = cardpresent;
    }


    /**
     * Gets the cardreaderpresent value for this CreditCardInfo.
     * 
     * @return cardreaderpresent
     */
    public int getCardreaderpresent() {
        return cardreaderpresent;
    }


    /**
     * Sets the cardreaderpresent value for this CreditCardInfo.
     * 
     * @param cardreaderpresent
     */
    public void setCardreaderpresent(int cardreaderpresent) {
        this.cardreaderpresent = cardreaderpresent;
    }


    /**
     * Gets the voiceauth value for this CreditCardInfo.
     * 
     * @return voiceauth
     */
    public java.lang.String getVoiceauth() {
        return voiceauth;
    }


    /**
     * Sets the voiceauth value for this CreditCardInfo.
     * 
     * @param voiceauth
     */
    public void setVoiceauth(java.lang.String voiceauth) {
        this.voiceauth = voiceauth;
    }


    /**
     * Gets the track1 value for this CreditCardInfo.
     * 
     * @return track1
     */
    public java.lang.String getTrack1() {
        return track1;
    }


    /**
     * Sets the track1 value for this CreditCardInfo.
     * 
     * @param track1
     */
    public void setTrack1(java.lang.String track1) {
        this.track1 = track1;
    }


    /**
     * Gets the track2 value for this CreditCardInfo.
     * 
     * @return track2
     */
    public java.lang.String getTrack2() {
        return track2;
    }


    /**
     * Sets the track2 value for this CreditCardInfo.
     * 
     * @param track2
     */
    public void setTrack2(java.lang.String track2) {
        this.track2 = track2;
    }


    /**
     * Gets the ccnum value for this CreditCardInfo.
     * 
     * @return ccnum
     */
    public java.lang.String getCcnum() {
        return ccnum;
    }


    /**
     * Sets the ccnum value for this CreditCardInfo.
     * 
     * @param ccnum
     */
    public void setCcnum(java.lang.String ccnum) {
        this.ccnum = ccnum;
    }


    /**
     * Gets the cctype value for this CreditCardInfo.
     * 
     * @return cctype
     */
    public java.lang.String getCctype() {
        return cctype;
    }


    /**
     * Sets the cctype value for this CreditCardInfo.
     * 
     * @param cctype
     */
    public void setCctype(java.lang.String cctype) {
        this.cctype = cctype;
    }


    /**
     * Gets the expmon value for this CreditCardInfo.
     * 
     * @return expmon
     */
    public int getExpmon() {
        return expmon;
    }


    /**
     * Sets the expmon value for this CreditCardInfo.
     * 
     * @param expmon
     */
    public void setExpmon(int expmon) {
        this.expmon = expmon;
    }


    /**
     * Gets the expyear value for this CreditCardInfo.
     * 
     * @return expyear
     */
    public int getExpyear() {
        return expyear;
    }


    /**
     * Sets the expyear value for this CreditCardInfo.
     * 
     * @param expyear
     */
    public void setExpyear(int expyear) {
        this.expyear = expyear;
    }


    /**
     * Gets the cvv2 value for this CreditCardInfo.
     * 
     * @return cvv2
     */
    public int getCvv2() {
        return cvv2;
    }


    /**
     * Sets the cvv2 value for this CreditCardInfo.
     * 
     * @param cvv2
     */
    public void setCvv2(int cvv2) {
        this.cvv2 = cvv2;
    }


    /**
     * Gets the cvv2_cid value for this CreditCardInfo.
     * 
     * @return cvv2_cid
     */
    public java.lang.String getCvv2_cid() {
        return cvv2_cid;
    }


    /**
     * Sets the cvv2_cid value for this CreditCardInfo.
     * 
     * @param cvv2_cid
     */
    public void setCvv2_cid(java.lang.String cvv2_cid) {
        this.cvv2_cid = cvv2_cid;
    }


    /**
     * Gets the amount value for this CreditCardInfo.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this CreditCardInfo.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the merchantordernumber value for this CreditCardInfo.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this CreditCardInfo.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the companyname value for this CreditCardInfo.
     * 
     * @return companyname
     */
    public java.lang.String getCompanyname() {
        return companyname;
    }


    /**
     * Sets the companyname value for this CreditCardInfo.
     * 
     * @param companyname
     */
    public void setCompanyname(java.lang.String companyname) {
        this.companyname = companyname;
    }


    /**
     * Gets the billaddress value for this CreditCardInfo.
     * 
     * @return billaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getBilladdress() {
        return billaddress;
    }


    /**
     * Sets the billaddress value for this CreditCardInfo.
     * 
     * @param billaddress
     */
    public void setBilladdress(com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress) {
        this.billaddress = billaddress;
    }


    /**
     * Gets the shipaddress value for this CreditCardInfo.
     * 
     * @return shipaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getShipaddress() {
        return shipaddress;
    }


    /**
     * Sets the shipaddress value for this CreditCardInfo.
     * 
     * @param shipaddress
     */
    public void setShipaddress(com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress) {
        this.shipaddress = shipaddress;
    }


    /**
     * Gets the email value for this CreditCardInfo.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this CreditCardInfo.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the dlnum value for this CreditCardInfo.
     * 
     * @return dlnum
     */
    public java.lang.String getDlnum() {
        return dlnum;
    }


    /**
     * Sets the dlnum value for this CreditCardInfo.
     * 
     * @param dlnum
     */
    public void setDlnum(java.lang.String dlnum) {
        this.dlnum = dlnum;
    }


    /**
     * Gets the ssnum value for this CreditCardInfo.
     * 
     * @return ssnum
     */
    public java.lang.String getSsnum() {
        return ssnum;
    }


    /**
     * Sets the ssnum value for this CreditCardInfo.
     * 
     * @param ssnum
     */
    public void setSsnum(java.lang.String ssnum) {
        this.ssnum = ssnum;
    }


    /**
     * Gets the phone value for this CreditCardInfo.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this CreditCardInfo.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the dobday value for this CreditCardInfo.
     * 
     * @return dobday
     */
    public java.lang.String getDobday() {
        return dobday;
    }


    /**
     * Sets the dobday value for this CreditCardInfo.
     * 
     * @param dobday
     */
    public void setDobday(java.lang.String dobday) {
        this.dobday = dobday;
    }


    /**
     * Gets the dobmonth value for this CreditCardInfo.
     * 
     * @return dobmonth
     */
    public java.lang.String getDobmonth() {
        return dobmonth;
    }


    /**
     * Sets the dobmonth value for this CreditCardInfo.
     * 
     * @param dobmonth
     */
    public void setDobmonth(java.lang.String dobmonth) {
        this.dobmonth = dobmonth;
    }


    /**
     * Gets the dobyear value for this CreditCardInfo.
     * 
     * @return dobyear
     */
    public java.lang.String getDobyear() {
        return dobyear;
    }


    /**
     * Sets the dobyear value for this CreditCardInfo.
     * 
     * @param dobyear
     */
    public void setDobyear(java.lang.String dobyear) {
        this.dobyear = dobyear;
    }


    /**
     * Gets the memo value for this CreditCardInfo.
     * 
     * @return memo
     */
    public java.lang.String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this CreditCardInfo.
     * 
     * @param memo
     */
    public void setMemo(java.lang.String memo) {
        this.memo = memo;
    }


    /**
     * Gets the customizedemail value for this CreditCardInfo.
     * 
     * @return customizedemail
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail getCustomizedemail() {
        return customizedemail;
    }


    /**
     * Sets the customizedemail value for this CreditCardInfo.
     * 
     * @param customizedemail
     */
    public void setCustomizedemail(com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail) {
        this.customizedemail = customizedemail;
    }


    /**
     * Gets the recurring value for this CreditCardInfo.
     * 
     * @return recurring
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Recur getRecurring() {
        return recurring;
    }


    /**
     * Sets the recurring value for this CreditCardInfo.
     * 
     * @param recurring
     */
    public void setRecurring(com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring) {
        this.recurring = recurring;
    }


    /**
     * Gets the ipaddress value for this CreditCardInfo.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this CreditCardInfo.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the accttype value for this CreditCardInfo.
     * 
     * @return accttype
     */
    public int getAccttype() {
        return accttype;
    }


    /**
     * Sets the accttype value for this CreditCardInfo.
     * 
     * @param accttype
     */
    public void setAccttype(int accttype) {
        this.accttype = accttype;
    }


    /**
     * Gets the merchantpin value for this CreditCardInfo.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this CreditCardInfo.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the currencycode value for this CreditCardInfo.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this CreditCardInfo.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the industrycode value for this CreditCardInfo.
     * 
     * @return industrycode
     */
    public java.lang.String getIndustrycode() {
        return industrycode;
    }


    /**
     * Sets the industrycode value for this CreditCardInfo.
     * 
     * @param industrycode
     */
    public void setIndustrycode(java.lang.String industrycode) {
        this.industrycode = industrycode;
    }


    /**
     * Gets the dynamicdescriptor value for this CreditCardInfo.
     * 
     * @return dynamicdescriptor
     */
    public java.lang.String getDynamicdescriptor() {
        return dynamicdescriptor;
    }


    /**
     * Sets the dynamicdescriptor value for this CreditCardInfo.
     * 
     * @param dynamicdescriptor
     */
    public void setDynamicdescriptor(java.lang.String dynamicdescriptor) {
        this.dynamicdescriptor = dynamicdescriptor;
    }


    /**
     * Gets the profileactiontype value for this CreditCardInfo.
     * 
     * @return profileactiontype
     */
    public int getProfileactiontype() {
        return profileactiontype;
    }


    /**
     * Sets the profileactiontype value for this CreditCardInfo.
     * 
     * @param profileactiontype
     */
    public void setProfileactiontype(int profileactiontype) {
        this.profileactiontype = profileactiontype;
    }


    /**
     * Gets the manualrecurring value for this CreditCardInfo.
     * 
     * @return manualrecurring
     */
    public int getManualrecurring() {
        return manualrecurring;
    }


    /**
     * Sets the manualrecurring value for this CreditCardInfo.
     * 
     * @param manualrecurring
     */
    public void setManualrecurring(int manualrecurring) {
        this.manualrecurring = manualrecurring;
    }


    /**
     * Gets the hotellodging value for this CreditCardInfo.
     * 
     * @return hotellodging
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging getHotellodging() {
        return hotellodging;
    }


    /**
     * Sets the hotellodging value for this CreditCardInfo.
     * 
     * @param hotellodging
     */
    public void setHotellodging(com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging) {
        this.hotellodging = hotellodging;
    }


    /**
     * Gets the autorental value for this CreditCardInfo.
     * 
     * @return autorental
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.AutoRental getAutorental() {
        return autorental;
    }


    /**
     * Sets the autorental value for this CreditCardInfo.
     * 
     * @param autorental
     */
    public void setAutorental(com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental) {
        this.autorental = autorental;
    }


    /**
     * Gets the customizedfields value for this CreditCardInfo.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this CreditCardInfo.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }


    /**
     * Gets the autorepair value for this CreditCardInfo.
     * 
     * @return autorepair
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.AutoRepair getAutorepair() {
        return autorepair;
    }


    /**
     * Sets the autorepair value for this CreditCardInfo.
     * 
     * @param autorepair
     */
    public void setAutorepair(com.directi.pg.core.swiffpay.MPTransProcess.AutoRepair autorepair) {
        this.autorepair = autorepair;
    }


    /**
     * Gets the fsa value for this CreditCardInfo.
     * 
     * @return fsa
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.FSA getFsa() {
        return fsa;
    }


    /**
     * Sets the fsa value for this CreditCardInfo.
     * 
     * @param fsa
     */
    public void setFsa(com.directi.pg.core.swiffpay.MPTransProcess.FSA fsa) {
        this.fsa = fsa;
    }


    /**
     * Gets the purchasecardlevel2 value for this CreditCardInfo.
     * 
     * @return purchasecardlevel2
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 getPurchasecardlevel2() {
        return purchasecardlevel2;
    }


    /**
     * Sets the purchasecardlevel2 value for this CreditCardInfo.
     * 
     * @param purchasecardlevel2
     */
    public void setPurchasecardlevel2(com.directi.pg.core.swiffpay.MPTransProcess.PurchaseCardLevel2 purchasecardlevel2) {
        this.purchasecardlevel2 = purchasecardlevel2;
    }


    /**
     * Gets the restaurant value for this CreditCardInfo.
     * 
     * @return restaurant
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Restaurant getRestaurant() {
        return restaurant;
    }


    /**
     * Sets the restaurant value for this CreditCardInfo.
     * 
     * @param restaurant
     */
    public void setRestaurant(com.directi.pg.core.swiffpay.MPTransProcess.Restaurant restaurant) {
        this.restaurant = restaurant;
    }


    /**
     * Gets the otp value for this CreditCardInfo.
     * 
     * @return otp
     */
    public java.lang.String getOtp() {
        return otp;
    }


    /**
     * Sets the otp value for this CreditCardInfo.
     * 
     * @param otp
     */
    public void setOtp(java.lang.String otp) {
        this.otp = otp;
    }


    /**
     * Gets the deviceid value for this CreditCardInfo.
     * 
     * @return deviceid
     */
    public java.lang.String getDeviceid() {
        return deviceid;
    }


    /**
     * Sets the deviceid value for this CreditCardInfo.
     * 
     * @param deviceid
     */
    public void setDeviceid(java.lang.String deviceid) {
        this.deviceid = deviceid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreditCardInfo)) return false;
        CreditCardInfo other = (CreditCardInfo) obj;
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
            ((this.voiceauth==null && other.getVoiceauth()==null) || 
             (this.voiceauth!=null &&
              this.voiceauth.equals(other.getVoiceauth()))) &&
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
            ((this.dobday==null && other.getDobday()==null) || 
             (this.dobday!=null &&
              this.dobday.equals(other.getDobday()))) &&
            ((this.dobmonth==null && other.getDobmonth()==null) || 
             (this.dobmonth!=null &&
              this.dobmonth.equals(other.getDobmonth()))) &&
            ((this.dobyear==null && other.getDobyear()==null) || 
             (this.dobyear!=null &&
              this.dobyear.equals(other.getDobyear()))) &&
            ((this.memo==null && other.getMemo()==null) || 
             (this.memo!=null &&
              this.memo.equals(other.getMemo()))) &&
            ((this.customizedemail==null && other.getCustomizedemail()==null) || 
             (this.customizedemail!=null &&
              this.customizedemail.equals(other.getCustomizedemail()))) &&
            ((this.recurring==null && other.getRecurring()==null) || 
             (this.recurring!=null &&
              this.recurring.equals(other.getRecurring()))) &&
            ((this.ipaddress==null && other.getIpaddress()==null) || 
             (this.ipaddress!=null &&
              this.ipaddress.equals(other.getIpaddress()))) &&
            this.accttype == other.getAccttype() &&
            ((this.merchantpin==null && other.getMerchantpin()==null) || 
             (this.merchantpin!=null &&
              this.merchantpin.equals(other.getMerchantpin()))) &&
            ((this.currencycode==null && other.getCurrencycode()==null) || 
             (this.currencycode!=null &&
              this.currencycode.equals(other.getCurrencycode()))) &&
            ((this.industrycode==null && other.getIndustrycode()==null) || 
             (this.industrycode!=null &&
              this.industrycode.equals(other.getIndustrycode()))) &&
            ((this.dynamicdescriptor==null && other.getDynamicdescriptor()==null) || 
             (this.dynamicdescriptor!=null &&
              this.dynamicdescriptor.equals(other.getDynamicdescriptor()))) &&
            this.profileactiontype == other.getProfileactiontype() &&
            this.manualrecurring == other.getManualrecurring() &&
            ((this.hotellodging==null && other.getHotellodging()==null) || 
             (this.hotellodging!=null &&
              this.hotellodging.equals(other.getHotellodging()))) &&
            ((this.autorental==null && other.getAutorental()==null) || 
             (this.autorental!=null &&
              this.autorental.equals(other.getAutorental()))) &&
            ((this.customizedfields==null && other.getCustomizedfields()==null) || 
             (this.customizedfields!=null &&
              this.customizedfields.equals(other.getCustomizedfields()))) &&
            ((this.autorepair==null && other.getAutorepair()==null) || 
             (this.autorepair!=null &&
              this.autorepair.equals(other.getAutorepair()))) &&
            ((this.fsa==null && other.getFsa()==null) || 
             (this.fsa!=null &&
              this.fsa.equals(other.getFsa()))) &&
            ((this.purchasecardlevel2==null && other.getPurchasecardlevel2()==null) || 
             (this.purchasecardlevel2!=null &&
              this.purchasecardlevel2.equals(other.getPurchasecardlevel2()))) &&
            ((this.restaurant==null && other.getRestaurant()==null) || 
             (this.restaurant!=null &&
              this.restaurant.equals(other.getRestaurant()))) &&
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
        if (getCcname() != null) {
            _hashCode += getCcname().hashCode();
        }
        if (getSwipedata() != null) {
            _hashCode += getSwipedata().hashCode();
        }
        _hashCode += getCardpresent();
        _hashCode += getCardreaderpresent();
        if (getVoiceauth() != null) {
            _hashCode += getVoiceauth().hashCode();
        }
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
        if (getDobday() != null) {
            _hashCode += getDobday().hashCode();
        }
        if (getDobmonth() != null) {
            _hashCode += getDobmonth().hashCode();
        }
        if (getDobyear() != null) {
            _hashCode += getDobyear().hashCode();
        }
        if (getMemo() != null) {
            _hashCode += getMemo().hashCode();
        }
        if (getCustomizedemail() != null) {
            _hashCode += getCustomizedemail().hashCode();
        }
        if (getRecurring() != null) {
            _hashCode += getRecurring().hashCode();
        }
        if (getIpaddress() != null) {
            _hashCode += getIpaddress().hashCode();
        }
        _hashCode += getAccttype();
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
        }
        if (getCurrencycode() != null) {
            _hashCode += getCurrencycode().hashCode();
        }
        if (getIndustrycode() != null) {
            _hashCode += getIndustrycode().hashCode();
        }
        if (getDynamicdescriptor() != null) {
            _hashCode += getDynamicdescriptor().hashCode();
        }
        _hashCode += getProfileactiontype();
        _hashCode += getManualrecurring();
        if (getHotellodging() != null) {
            _hashCode += getHotellodging().hashCode();
        }
        if (getAutorental() != null) {
            _hashCode += getAutorental().hashCode();
        }
        if (getCustomizedfields() != null) {
            _hashCode += getCustomizedfields().hashCode();
        }
        if (getAutorepair() != null) {
            _hashCode += getAutorepair().hashCode();
        }
        if (getFsa() != null) {
            _hashCode += getFsa().hashCode();
        }
        if (getPurchasecardlevel2() != null) {
            _hashCode += getPurchasecardlevel2().hashCode();
        }
        if (getRestaurant() != null) {
            _hashCode += getRestaurant().hashCode();
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
        new org.apache.axis.description.TypeDesc(CreditCardInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "CreditCardInfo"));
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
        elemField.setFieldName("voiceauth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "voiceauth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
        elemField.setFieldName("memo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memo"));
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
        elemField.setFieldName("recurring");
        elemField.setXmlName(new javax.xml.namespace.QName("", "recurring"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "Recur"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ipaddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ipaddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accttype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accttype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
        elemField.setFieldName("dynamicdescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dynamicdescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("profileactiontype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "profileactiontype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manualrecurring");
        elemField.setXmlName(new javax.xml.namespace.QName("", "manualrecurring"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("autorepair");
        elemField.setXmlName(new javax.xml.namespace.QName("", "autorepair"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "AutoRepair"));
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
        elemField.setFieldName("restaurant");
        elemField.setXmlName(new javax.xml.namespace.QName("", "restaurant"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "Restaurant"));
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
