/**
 * C21ICLInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class C21ICLInfo  implements java.io.Serializable {
    private java.lang.String acctid;

    private java.lang.String accountkey;

    private java.lang.String subid;

    private java.lang.String ckname;

    private java.lang.String firstname;

    private java.lang.String lastname;

    private java.lang.String ckaba;

    private java.lang.String ckacct;

    private java.lang.String ckno;

    private java.lang.String cktype;

    private float amount;

    private java.lang.String ckmicrdata;

    private java.lang.String ckterminalcity;

    private java.lang.String ckterminalstate;

    private java.lang.String ckaccttype;

    private java.lang.String ckimagefront;

    private java.lang.String ckimageback;

    private java.lang.String ckimagetype;

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

    private java.lang.String memoline1;

    private java.lang.String memoline2;

    private java.lang.String memoline3;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail;

    private com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring;

    private java.lang.String ipaddress;

    private java.lang.String merchantpin;

    private java.lang.String currencycode;

    private java.lang.String industrycode;

    private java.lang.String billstreet;

    private java.lang.String housenumber;

    private java.lang.String zip4;

    private java.lang.String riskmodifier;

    private java.lang.String authenticationmodifier;

    private java.lang.String insurancemodifier;

    private int profileactiontype;

    private com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging;

    private com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental;

    private com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields;

    public C21ICLInfo() {
    }

    public C21ICLInfo(
           java.lang.String acctid,
           java.lang.String accountkey,
           java.lang.String subid,
           java.lang.String ckname,
           java.lang.String firstname,
           java.lang.String lastname,
           java.lang.String ckaba,
           java.lang.String ckacct,
           java.lang.String ckno,
           java.lang.String cktype,
           float amount,
           java.lang.String ckmicrdata,
           java.lang.String ckterminalcity,
           java.lang.String ckterminalstate,
           java.lang.String ckaccttype,
           java.lang.String ckimagefront,
           java.lang.String ckimageback,
           java.lang.String ckimagetype,
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
           java.lang.String memoline1,
           java.lang.String memoline2,
           java.lang.String memoline3,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail,
           com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring,
           java.lang.String ipaddress,
           java.lang.String merchantpin,
           java.lang.String currencycode,
           java.lang.String industrycode,
           java.lang.String billstreet,
           java.lang.String housenumber,
           java.lang.String zip4,
           java.lang.String riskmodifier,
           java.lang.String authenticationmodifier,
           java.lang.String insurancemodifier,
           int profileactiontype,
           com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging,
           com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental,
           com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
           this.acctid = acctid;
           this.accountkey = accountkey;
           this.subid = subid;
           this.ckname = ckname;
           this.firstname = firstname;
           this.lastname = lastname;
           this.ckaba = ckaba;
           this.ckacct = ckacct;
           this.ckno = ckno;
           this.cktype = cktype;
           this.amount = amount;
           this.ckmicrdata = ckmicrdata;
           this.ckterminalcity = ckterminalcity;
           this.ckterminalstate = ckterminalstate;
           this.ckaccttype = ckaccttype;
           this.ckimagefront = ckimagefront;
           this.ckimageback = ckimageback;
           this.ckimagetype = ckimagetype;
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
           this.memoline1 = memoline1;
           this.memoline2 = memoline2;
           this.memoline3 = memoline3;
           this.customizedemail = customizedemail;
           this.recurring = recurring;
           this.ipaddress = ipaddress;
           this.merchantpin = merchantpin;
           this.currencycode = currencycode;
           this.industrycode = industrycode;
           this.billstreet = billstreet;
           this.housenumber = housenumber;
           this.zip4 = zip4;
           this.riskmodifier = riskmodifier;
           this.authenticationmodifier = authenticationmodifier;
           this.insurancemodifier = insurancemodifier;
           this.profileactiontype = profileactiontype;
           this.hotellodging = hotellodging;
           this.autorental = autorental;
           this.customizedfields = customizedfields;
    }


    /**
     * Gets the acctid value for this C21ICLInfo.
     * 
     * @return acctid
     */
    public java.lang.String getAcctid() {
        return acctid;
    }


    /**
     * Sets the acctid value for this C21ICLInfo.
     * 
     * @param acctid
     */
    public void setAcctid(java.lang.String acctid) {
        this.acctid = acctid;
    }


    /**
     * Gets the accountkey value for this C21ICLInfo.
     * 
     * @return accountkey
     */
    public java.lang.String getAccountkey() {
        return accountkey;
    }


    /**
     * Sets the accountkey value for this C21ICLInfo.
     * 
     * @param accountkey
     */
    public void setAccountkey(java.lang.String accountkey) {
        this.accountkey = accountkey;
    }


    /**
     * Gets the subid value for this C21ICLInfo.
     * 
     * @return subid
     */
    public java.lang.String getSubid() {
        return subid;
    }


    /**
     * Sets the subid value for this C21ICLInfo.
     * 
     * @param subid
     */
    public void setSubid(java.lang.String subid) {
        this.subid = subid;
    }


    /**
     * Gets the ckname value for this C21ICLInfo.
     * 
     * @return ckname
     */
    public java.lang.String getCkname() {
        return ckname;
    }


    /**
     * Sets the ckname value for this C21ICLInfo.
     * 
     * @param ckname
     */
    public void setCkname(java.lang.String ckname) {
        this.ckname = ckname;
    }


    /**
     * Gets the firstname value for this C21ICLInfo.
     * 
     * @return firstname
     */
    public java.lang.String getFirstname() {
        return firstname;
    }


    /**
     * Sets the firstname value for this C21ICLInfo.
     * 
     * @param firstname
     */
    public void setFirstname(java.lang.String firstname) {
        this.firstname = firstname;
    }


    /**
     * Gets the lastname value for this C21ICLInfo.
     * 
     * @return lastname
     */
    public java.lang.String getLastname() {
        return lastname;
    }


    /**
     * Sets the lastname value for this C21ICLInfo.
     * 
     * @param lastname
     */
    public void setLastname(java.lang.String lastname) {
        this.lastname = lastname;
    }


    /**
     * Gets the ckaba value for this C21ICLInfo.
     * 
     * @return ckaba
     */
    public java.lang.String getCkaba() {
        return ckaba;
    }


    /**
     * Sets the ckaba value for this C21ICLInfo.
     * 
     * @param ckaba
     */
    public void setCkaba(java.lang.String ckaba) {
        this.ckaba = ckaba;
    }


    /**
     * Gets the ckacct value for this C21ICLInfo.
     * 
     * @return ckacct
     */
    public java.lang.String getCkacct() {
        return ckacct;
    }


    /**
     * Sets the ckacct value for this C21ICLInfo.
     * 
     * @param ckacct
     */
    public void setCkacct(java.lang.String ckacct) {
        this.ckacct = ckacct;
    }


    /**
     * Gets the ckno value for this C21ICLInfo.
     * 
     * @return ckno
     */
    public java.lang.String getCkno() {
        return ckno;
    }


    /**
     * Sets the ckno value for this C21ICLInfo.
     * 
     * @param ckno
     */
    public void setCkno(java.lang.String ckno) {
        this.ckno = ckno;
    }


    /**
     * Gets the cktype value for this C21ICLInfo.
     * 
     * @return cktype
     */
    public java.lang.String getCktype() {
        return cktype;
    }


    /**
     * Sets the cktype value for this C21ICLInfo.
     * 
     * @param cktype
     */
    public void setCktype(java.lang.String cktype) {
        this.cktype = cktype;
    }


    /**
     * Gets the amount value for this C21ICLInfo.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this C21ICLInfo.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }


    /**
     * Gets the ckmicrdata value for this C21ICLInfo.
     * 
     * @return ckmicrdata
     */
    public java.lang.String getCkmicrdata() {
        return ckmicrdata;
    }


    /**
     * Sets the ckmicrdata value for this C21ICLInfo.
     * 
     * @param ckmicrdata
     */
    public void setCkmicrdata(java.lang.String ckmicrdata) {
        this.ckmicrdata = ckmicrdata;
    }


    /**
     * Gets the ckterminalcity value for this C21ICLInfo.
     * 
     * @return ckterminalcity
     */
    public java.lang.String getCkterminalcity() {
        return ckterminalcity;
    }


    /**
     * Sets the ckterminalcity value for this C21ICLInfo.
     * 
     * @param ckterminalcity
     */
    public void setCkterminalcity(java.lang.String ckterminalcity) {
        this.ckterminalcity = ckterminalcity;
    }


    /**
     * Gets the ckterminalstate value for this C21ICLInfo.
     * 
     * @return ckterminalstate
     */
    public java.lang.String getCkterminalstate() {
        return ckterminalstate;
    }


    /**
     * Sets the ckterminalstate value for this C21ICLInfo.
     * 
     * @param ckterminalstate
     */
    public void setCkterminalstate(java.lang.String ckterminalstate) {
        this.ckterminalstate = ckterminalstate;
    }


    /**
     * Gets the ckaccttype value for this C21ICLInfo.
     * 
     * @return ckaccttype
     */
    public java.lang.String getCkaccttype() {
        return ckaccttype;
    }


    /**
     * Sets the ckaccttype value for this C21ICLInfo.
     * 
     * @param ckaccttype
     */
    public void setCkaccttype(java.lang.String ckaccttype) {
        this.ckaccttype = ckaccttype;
    }


    /**
     * Gets the ckimagefront value for this C21ICLInfo.
     * 
     * @return ckimagefront
     */
    public java.lang.String getCkimagefront() {
        return ckimagefront;
    }


    /**
     * Sets the ckimagefront value for this C21ICLInfo.
     * 
     * @param ckimagefront
     */
    public void setCkimagefront(java.lang.String ckimagefront) {
        this.ckimagefront = ckimagefront;
    }


    /**
     * Gets the ckimageback value for this C21ICLInfo.
     * 
     * @return ckimageback
     */
    public java.lang.String getCkimageback() {
        return ckimageback;
    }


    /**
     * Sets the ckimageback value for this C21ICLInfo.
     * 
     * @param ckimageback
     */
    public void setCkimageback(java.lang.String ckimageback) {
        this.ckimageback = ckimageback;
    }


    /**
     * Gets the ckimagetype value for this C21ICLInfo.
     * 
     * @return ckimagetype
     */
    public java.lang.String getCkimagetype() {
        return ckimagetype;
    }


    /**
     * Sets the ckimagetype value for this C21ICLInfo.
     * 
     * @param ckimagetype
     */
    public void setCkimagetype(java.lang.String ckimagetype) {
        this.ckimagetype = ckimagetype;
    }


    /**
     * Gets the merchantordernumber value for this C21ICLInfo.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this C21ICLInfo.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the companyname value for this C21ICLInfo.
     * 
     * @return companyname
     */
    public java.lang.String getCompanyname() {
        return companyname;
    }


    /**
     * Sets the companyname value for this C21ICLInfo.
     * 
     * @param companyname
     */
    public void setCompanyname(java.lang.String companyname) {
        this.companyname = companyname;
    }


    /**
     * Gets the billaddress value for this C21ICLInfo.
     * 
     * @return billaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getBilladdress() {
        return billaddress;
    }


    /**
     * Sets the billaddress value for this C21ICLInfo.
     * 
     * @param billaddress
     */
    public void setBilladdress(com.directi.pg.core.swiffpay.MPTransProcess.Address billaddress) {
        this.billaddress = billaddress;
    }


    /**
     * Gets the shipaddress value for this C21ICLInfo.
     * 
     * @return shipaddress
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Address getShipaddress() {
        return shipaddress;
    }


    /**
     * Sets the shipaddress value for this C21ICLInfo.
     * 
     * @param shipaddress
     */
    public void setShipaddress(com.directi.pg.core.swiffpay.MPTransProcess.Address shipaddress) {
        this.shipaddress = shipaddress;
    }


    /**
     * Gets the email value for this C21ICLInfo.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this C21ICLInfo.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the dlnum value for this C21ICLInfo.
     * 
     * @return dlnum
     */
    public java.lang.String getDlnum() {
        return dlnum;
    }


    /**
     * Sets the dlnum value for this C21ICLInfo.
     * 
     * @param dlnum
     */
    public void setDlnum(java.lang.String dlnum) {
        this.dlnum = dlnum;
    }


    /**
     * Gets the ssnum value for this C21ICLInfo.
     * 
     * @return ssnum
     */
    public java.lang.String getSsnum() {
        return ssnum;
    }


    /**
     * Sets the ssnum value for this C21ICLInfo.
     * 
     * @param ssnum
     */
    public void setSsnum(java.lang.String ssnum) {
        this.ssnum = ssnum;
    }


    /**
     * Gets the phone value for this C21ICLInfo.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this C21ICLInfo.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the memo value for this C21ICLInfo.
     * 
     * @return memo
     */
    public java.lang.String getMemo() {
        return memo;
    }


    /**
     * Sets the memo value for this C21ICLInfo.
     * 
     * @param memo
     */
    public void setMemo(java.lang.String memo) {
        this.memo = memo;
    }


    /**
     * Gets the dobday value for this C21ICLInfo.
     * 
     * @return dobday
     */
    public java.lang.String getDobday() {
        return dobday;
    }


    /**
     * Sets the dobday value for this C21ICLInfo.
     * 
     * @param dobday
     */
    public void setDobday(java.lang.String dobday) {
        this.dobday = dobday;
    }


    /**
     * Gets the dobmonth value for this C21ICLInfo.
     * 
     * @return dobmonth
     */
    public java.lang.String getDobmonth() {
        return dobmonth;
    }


    /**
     * Sets the dobmonth value for this C21ICLInfo.
     * 
     * @param dobmonth
     */
    public void setDobmonth(java.lang.String dobmonth) {
        this.dobmonth = dobmonth;
    }


    /**
     * Gets the dobyear value for this C21ICLInfo.
     * 
     * @return dobyear
     */
    public java.lang.String getDobyear() {
        return dobyear;
    }


    /**
     * Sets the dobyear value for this C21ICLInfo.
     * 
     * @param dobyear
     */
    public void setDobyear(java.lang.String dobyear) {
        this.dobyear = dobyear;
    }


    /**
     * Gets the memoline1 value for this C21ICLInfo.
     * 
     * @return memoline1
     */
    public java.lang.String getMemoline1() {
        return memoline1;
    }


    /**
     * Sets the memoline1 value for this C21ICLInfo.
     * 
     * @param memoline1
     */
    public void setMemoline1(java.lang.String memoline1) {
        this.memoline1 = memoline1;
    }


    /**
     * Gets the memoline2 value for this C21ICLInfo.
     * 
     * @return memoline2
     */
    public java.lang.String getMemoline2() {
        return memoline2;
    }


    /**
     * Sets the memoline2 value for this C21ICLInfo.
     * 
     * @param memoline2
     */
    public void setMemoline2(java.lang.String memoline2) {
        this.memoline2 = memoline2;
    }


    /**
     * Gets the memoline3 value for this C21ICLInfo.
     * 
     * @return memoline3
     */
    public java.lang.String getMemoline3() {
        return memoline3;
    }


    /**
     * Sets the memoline3 value for this C21ICLInfo.
     * 
     * @param memoline3
     */
    public void setMemoline3(java.lang.String memoline3) {
        this.memoline3 = memoline3;
    }


    /**
     * Gets the customizedemail value for this C21ICLInfo.
     * 
     * @return customizedemail
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail getCustomizedemail() {
        return customizedemail;
    }


    /**
     * Sets the customizedemail value for this C21ICLInfo.
     * 
     * @param customizedemail
     */
    public void setCustomizedemail(com.directi.pg.core.swiffpay.MPTransProcess.CustomEmail customizedemail) {
        this.customizedemail = customizedemail;
    }


    /**
     * Gets the recurring value for this C21ICLInfo.
     * 
     * @return recurring
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.Recur getRecurring() {
        return recurring;
    }


    /**
     * Sets the recurring value for this C21ICLInfo.
     * 
     * @param recurring
     */
    public void setRecurring(com.directi.pg.core.swiffpay.MPTransProcess.Recur recurring) {
        this.recurring = recurring;
    }


    /**
     * Gets the ipaddress value for this C21ICLInfo.
     * 
     * @return ipaddress
     */
    public java.lang.String getIpaddress() {
        return ipaddress;
    }


    /**
     * Sets the ipaddress value for this C21ICLInfo.
     * 
     * @param ipaddress
     */
    public void setIpaddress(java.lang.String ipaddress) {
        this.ipaddress = ipaddress;
    }


    /**
     * Gets the merchantpin value for this C21ICLInfo.
     * 
     * @return merchantpin
     */
    public java.lang.String getMerchantpin() {
        return merchantpin;
    }


    /**
     * Sets the merchantpin value for this C21ICLInfo.
     * 
     * @param merchantpin
     */
    public void setMerchantpin(java.lang.String merchantpin) {
        this.merchantpin = merchantpin;
    }


    /**
     * Gets the currencycode value for this C21ICLInfo.
     * 
     * @return currencycode
     */
    public java.lang.String getCurrencycode() {
        return currencycode;
    }


    /**
     * Sets the currencycode value for this C21ICLInfo.
     * 
     * @param currencycode
     */
    public void setCurrencycode(java.lang.String currencycode) {
        this.currencycode = currencycode;
    }


    /**
     * Gets the industrycode value for this C21ICLInfo.
     * 
     * @return industrycode
     */
    public java.lang.String getIndustrycode() {
        return industrycode;
    }


    /**
     * Sets the industrycode value for this C21ICLInfo.
     * 
     * @param industrycode
     */
    public void setIndustrycode(java.lang.String industrycode) {
        this.industrycode = industrycode;
    }


    /**
     * Gets the billstreet value for this C21ICLInfo.
     * 
     * @return billstreet
     */
    public java.lang.String getBillstreet() {
        return billstreet;
    }


    /**
     * Sets the billstreet value for this C21ICLInfo.
     * 
     * @param billstreet
     */
    public void setBillstreet(java.lang.String billstreet) {
        this.billstreet = billstreet;
    }


    /**
     * Gets the housenumber value for this C21ICLInfo.
     * 
     * @return housenumber
     */
    public java.lang.String getHousenumber() {
        return housenumber;
    }


    /**
     * Sets the housenumber value for this C21ICLInfo.
     * 
     * @param housenumber
     */
    public void setHousenumber(java.lang.String housenumber) {
        this.housenumber = housenumber;
    }


    /**
     * Gets the zip4 value for this C21ICLInfo.
     * 
     * @return zip4
     */
    public java.lang.String getZip4() {
        return zip4;
    }


    /**
     * Sets the zip4 value for this C21ICLInfo.
     * 
     * @param zip4
     */
    public void setZip4(java.lang.String zip4) {
        this.zip4 = zip4;
    }


    /**
     * Gets the riskmodifier value for this C21ICLInfo.
     * 
     * @return riskmodifier
     */
    public java.lang.String getRiskmodifier() {
        return riskmodifier;
    }


    /**
     * Sets the riskmodifier value for this C21ICLInfo.
     * 
     * @param riskmodifier
     */
    public void setRiskmodifier(java.lang.String riskmodifier) {
        this.riskmodifier = riskmodifier;
    }


    /**
     * Gets the authenticationmodifier value for this C21ICLInfo.
     * 
     * @return authenticationmodifier
     */
    public java.lang.String getAuthenticationmodifier() {
        return authenticationmodifier;
    }


    /**
     * Sets the authenticationmodifier value for this C21ICLInfo.
     * 
     * @param authenticationmodifier
     */
    public void setAuthenticationmodifier(java.lang.String authenticationmodifier) {
        this.authenticationmodifier = authenticationmodifier;
    }


    /**
     * Gets the insurancemodifier value for this C21ICLInfo.
     * 
     * @return insurancemodifier
     */
    public java.lang.String getInsurancemodifier() {
        return insurancemodifier;
    }


    /**
     * Sets the insurancemodifier value for this C21ICLInfo.
     * 
     * @param insurancemodifier
     */
    public void setInsurancemodifier(java.lang.String insurancemodifier) {
        this.insurancemodifier = insurancemodifier;
    }


    /**
     * Gets the profileactiontype value for this C21ICLInfo.
     * 
     * @return profileactiontype
     */
    public int getProfileactiontype() {
        return profileactiontype;
    }


    /**
     * Sets the profileactiontype value for this C21ICLInfo.
     * 
     * @param profileactiontype
     */
    public void setProfileactiontype(int profileactiontype) {
        this.profileactiontype = profileactiontype;
    }


    /**
     * Gets the hotellodging value for this C21ICLInfo.
     * 
     * @return hotellodging
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging getHotellodging() {
        return hotellodging;
    }


    /**
     * Sets the hotellodging value for this C21ICLInfo.
     * 
     * @param hotellodging
     */
    public void setHotellodging(com.directi.pg.core.swiffpay.MPTransProcess.HotelLodging hotellodging) {
        this.hotellodging = hotellodging;
    }


    /**
     * Gets the autorental value for this C21ICLInfo.
     * 
     * @return autorental
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.AutoRental getAutorental() {
        return autorental;
    }


    /**
     * Sets the autorental value for this C21ICLInfo.
     * 
     * @param autorental
     */
    public void setAutorental(com.directi.pg.core.swiffpay.MPTransProcess.AutoRental autorental) {
        this.autorental = autorental;
    }


    /**
     * Gets the customizedfields value for this C21ICLInfo.
     * 
     * @return customizedfields
     */
    public com.directi.pg.core.swiffpay.MPTransProcess.CustomFields getCustomizedfields() {
        return customizedfields;
    }


    /**
     * Sets the customizedfields value for this C21ICLInfo.
     * 
     * @param customizedfields
     */
    public void setCustomizedfields(com.directi.pg.core.swiffpay.MPTransProcess.CustomFields customizedfields) {
        this.customizedfields = customizedfields;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof C21ICLInfo)) return false;
        C21ICLInfo other = (C21ICLInfo) obj;
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
            ((this.ckname==null && other.getCkname()==null) || 
             (this.ckname!=null &&
              this.ckname.equals(other.getCkname()))) &&
            ((this.firstname==null && other.getFirstname()==null) || 
             (this.firstname!=null &&
              this.firstname.equals(other.getFirstname()))) &&
            ((this.lastname==null && other.getLastname()==null) || 
             (this.lastname!=null &&
              this.lastname.equals(other.getLastname()))) &&
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
            this.amount == other.getAmount() &&
            ((this.ckmicrdata==null && other.getCkmicrdata()==null) || 
             (this.ckmicrdata!=null &&
              this.ckmicrdata.equals(other.getCkmicrdata()))) &&
            ((this.ckterminalcity==null && other.getCkterminalcity()==null) || 
             (this.ckterminalcity!=null &&
              this.ckterminalcity.equals(other.getCkterminalcity()))) &&
            ((this.ckterminalstate==null && other.getCkterminalstate()==null) || 
             (this.ckterminalstate!=null &&
              this.ckterminalstate.equals(other.getCkterminalstate()))) &&
            ((this.ckaccttype==null && other.getCkaccttype()==null) || 
             (this.ckaccttype!=null &&
              this.ckaccttype.equals(other.getCkaccttype()))) &&
            ((this.ckimagefront==null && other.getCkimagefront()==null) || 
             (this.ckimagefront!=null &&
              this.ckimagefront.equals(other.getCkimagefront()))) &&
            ((this.ckimageback==null && other.getCkimageback()==null) || 
             (this.ckimageback!=null &&
              this.ckimageback.equals(other.getCkimageback()))) &&
            ((this.ckimagetype==null && other.getCkimagetype()==null) || 
             (this.ckimagetype!=null &&
              this.ckimagetype.equals(other.getCkimagetype()))) &&
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
            ((this.memoline1==null && other.getMemoline1()==null) || 
             (this.memoline1!=null &&
              this.memoline1.equals(other.getMemoline1()))) &&
            ((this.memoline2==null && other.getMemoline2()==null) || 
             (this.memoline2!=null &&
              this.memoline2.equals(other.getMemoline2()))) &&
            ((this.memoline3==null && other.getMemoline3()==null) || 
             (this.memoline3!=null &&
              this.memoline3.equals(other.getMemoline3()))) &&
            ((this.customizedemail==null && other.getCustomizedemail()==null) || 
             (this.customizedemail!=null &&
              this.customizedemail.equals(other.getCustomizedemail()))) &&
            ((this.recurring==null && other.getRecurring()==null) || 
             (this.recurring!=null &&
              this.recurring.equals(other.getRecurring()))) &&
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
            ((this.billstreet==null && other.getBillstreet()==null) || 
             (this.billstreet!=null &&
              this.billstreet.equals(other.getBillstreet()))) &&
            ((this.housenumber==null && other.getHousenumber()==null) || 
             (this.housenumber!=null &&
              this.housenumber.equals(other.getHousenumber()))) &&
            ((this.zip4==null && other.getZip4()==null) || 
             (this.zip4!=null &&
              this.zip4.equals(other.getZip4()))) &&
            ((this.riskmodifier==null && other.getRiskmodifier()==null) || 
             (this.riskmodifier!=null &&
              this.riskmodifier.equals(other.getRiskmodifier()))) &&
            ((this.authenticationmodifier==null && other.getAuthenticationmodifier()==null) || 
             (this.authenticationmodifier!=null &&
              this.authenticationmodifier.equals(other.getAuthenticationmodifier()))) &&
            ((this.insurancemodifier==null && other.getInsurancemodifier()==null) || 
             (this.insurancemodifier!=null &&
              this.insurancemodifier.equals(other.getInsurancemodifier()))) &&
            this.profileactiontype == other.getProfileactiontype() &&
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
        if (getCkname() != null) {
            _hashCode += getCkname().hashCode();
        }
        if (getFirstname() != null) {
            _hashCode += getFirstname().hashCode();
        }
        if (getLastname() != null) {
            _hashCode += getLastname().hashCode();
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
        _hashCode += new Float(getAmount()).hashCode();
        if (getCkmicrdata() != null) {
            _hashCode += getCkmicrdata().hashCode();
        }
        if (getCkterminalcity() != null) {
            _hashCode += getCkterminalcity().hashCode();
        }
        if (getCkterminalstate() != null) {
            _hashCode += getCkterminalstate().hashCode();
        }
        if (getCkaccttype() != null) {
            _hashCode += getCkaccttype().hashCode();
        }
        if (getCkimagefront() != null) {
            _hashCode += getCkimagefront().hashCode();
        }
        if (getCkimageback() != null) {
            _hashCode += getCkimageback().hashCode();
        }
        if (getCkimagetype() != null) {
            _hashCode += getCkimagetype().hashCode();
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
        if (getDobday() != null) {
            _hashCode += getDobday().hashCode();
        }
        if (getDobmonth() != null) {
            _hashCode += getDobmonth().hashCode();
        }
        if (getDobyear() != null) {
            _hashCode += getDobyear().hashCode();
        }
        if (getMemoline1() != null) {
            _hashCode += getMemoline1().hashCode();
        }
        if (getMemoline2() != null) {
            _hashCode += getMemoline2().hashCode();
        }
        if (getMemoline3() != null) {
            _hashCode += getMemoline3().hashCode();
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
        if (getMerchantpin() != null) {
            _hashCode += getMerchantpin().hashCode();
        }
        if (getCurrencycode() != null) {
            _hashCode += getCurrencycode().hashCode();
        }
        if (getIndustrycode() != null) {
            _hashCode += getIndustrycode().hashCode();
        }
        if (getBillstreet() != null) {
            _hashCode += getBillstreet().hashCode();
        }
        if (getHousenumber() != null) {
            _hashCode += getHousenumber().hashCode();
        }
        if (getZip4() != null) {
            _hashCode += getZip4().hashCode();
        }
        if (getRiskmodifier() != null) {
            _hashCode += getRiskmodifier().hashCode();
        }
        if (getAuthenticationmodifier() != null) {
            _hashCode += getAuthenticationmodifier().hashCode();
        }
        if (getInsurancemodifier() != null) {
            _hashCode += getInsurancemodifier().hashCode();
        }
        _hashCode += getProfileactiontype();
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
        new org.apache.axis.description.TypeDesc(C21ICLInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "C21ICLInfo"));
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
        elemField.setFieldName("ckname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastname"));
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
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckmicrdata");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckmicrdata"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckterminalcity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckterminalcity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckterminalstate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckterminalstate"));
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
        elemField.setFieldName("ckimagefront");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckimagefront"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckimageback");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckimageback"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ckimagetype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ckimagetype"));
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
        elemField.setFieldName("memoline1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memoline1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memoline2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memoline2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("memoline3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "memoline3"));
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
        elemField.setFieldName("billstreet");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billstreet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("housenumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "housenumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("zip4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "zip4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riskmodifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "riskmodifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationmodifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "authenticationmodifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("insurancemodifier");
        elemField.setXmlName(new javax.xml.namespace.QName("", "insurancemodifier"));
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
