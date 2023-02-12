package com.fraud.vo;

import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/30/14
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZFraudRequestVO
{
    String memberid;           //mandatory    Provided By Payment
    String accountid;          //mandatory    Provided By Payment
    String trackingid;         //mandatory    Transaction Id
    String description;        //mandatory    description at merchant site
    String amount;             //mandatory    Transaction Amount
    String status;             //optional     Transaction Current Status
    String currency;           //mandatory    Transaction processing currency
    String time;               //mandatory    Transaction execution time
    String firstname;          //mandatory    Customer First Name
    String lastname;           //mandatory    Customer Last Name
    String emailaddr;          //mandatory    Customer Email
    String city;               //mandatory    Customer City
    String countrycode;        //mandatory    Customer Country Code
    String state;              //optional     Customer state code
    String street;             //mandatory    Customer Street
    String zip;                //mandatory    Customer Postal Code
    String birthDate;          //optional     Customer birth date
    String phone;              //optional     Customer contact no
    String ipaddrs;            //mandatory    Customer Transaction Ip
    String deviceid;           //Optional     Customer Device ID(MAC Address) Set default 00:00:00:00:00:00 when we unknown .
    String firstsix;           //mandatory    Transaction Processing Card First Six Number
    String lastfour;           //mandatory    Transaction Processing Card Last Four Number
    String dailycardminlimit;  //mandatory    Card Daily Minimum Limit
    String dailycardlimit;     //mandatory    Card Daily Max Limit
    String weeklycardlimit;    //mandatory    Card Weekly Max Limit
    String monthlycardlimit;   //mandatory    Card Monthly Max Limit
    String paymenttype;        //mandatory    Payment Type like CC/EC/EW
    String pzfraudtransid;     //Optional
    String fstransid;          //Mandatory     fraud_transaction table primary key which we pass during first transaction submission
    String binCountry;         //Bin Country from BIN DB

    String errorMsg;
    String reason;
    String partnerid;


    String website;
    String billingfirstname;
    String billinglastname;
    String billingemail;
    String billingaddress1;
    String billingaddress2;
    String billingcity;
    String billingprovince;
    String billingcountry;
    String billingpostalcode;
    String billingphone1;
    String billingphone2;
    String username;
    String usernumber;
    String ip;
    String country;                         //    Customer Country
    String address1;                        //    Customer Address

    String binCardType;
    String binUsageType;

    FraudAccountDetailsVO fraudAccountDetailsVO;

    public FraudAccountDetailsVO getFraudAccountDetailsVO()
    {
        return fraudAccountDetailsVO;
    }
    public void setFraudAccountDetailsVO(FraudAccountDetailsVO fraudAccountDetailsVO) {this.fraudAccountDetailsVO = fraudAccountDetailsVO;}

    public String getMemberid()
    {
        return memberid;
    }
    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public String getAccountid()
    {
        return accountid;
    }
    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }

    public String getTrackingid()
    {
        return trackingid;
    }
    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAmount()
    {
        return amount;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFirstname()
    {
        return firstname;
    }
    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getEmailaddr()
    {
        return emailaddr;
    }
    public void setEmailaddr(String emailaddr)
    {
        this.emailaddr = emailaddr;
    }

    public String getCity()
    {
        return city;
    }
    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountrycode()
    {
        return countrycode;
    }
    public void setCountrycode(String countrycode)
    {
        this.countrycode = countrycode;
    }

    public String getStreet()
    {
        return street;
    }
    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZip()
    {
        return zip;
    }
    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getIpaddrs()
    {
        return ipaddrs;
    }
    public void setIpaddrs(String ipaddrs)
    {
        this.ipaddrs = ipaddrs;
    }

    public String getDeviceid()
    {
        return deviceid;
    }
    public void setDeviceid(String deviceid)
    {
        this.deviceid = deviceid;
    }

    public String getFirstsix()
    {
        return firstsix;
    }
    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getLastfour()
    {
        return lastfour;
    }
    public void setLastfour(String lastfour)
    {
        this.lastfour = lastfour;
    }

    public String getDailycardminlimit()
    {
        return dailycardminlimit;
    }
    public void setDailycardminlimit(String dailycardminlimit)
    {
        this.dailycardminlimit = dailycardminlimit;
    }

    public String getDailycardlimit()
    {
        return dailycardlimit;
    }
    public void setDailycardlimit(String dailycardlimit)
    {
        this.dailycardlimit = dailycardlimit;
    }

    public String getWeeklycardlimit()
    {
        return weeklycardlimit;
    }
    public void setWeeklycardlimit(String weeklycardlimit)
    {
        this.weeklycardlimit = weeklycardlimit;
    }

    public String getMonthlycardlimit()
    {
        return monthlycardlimit;
    }
    public void setMonthlycardlimit(String monthlycardlimit)
    {
        this.monthlycardlimit = monthlycardlimit;
    }

    public String getPaymenttype()
    {
        return paymenttype;
    }
    public void setPaymenttype(String paymenttype)
    {
        this.paymenttype = paymenttype;
    }

    public String getPzfraudtransid()
    {
        return pzfraudtransid;
    }
    public void setPzfraudtransid(String pzfraudtransid)
    {
        this.pzfraudtransid = pzfraudtransid;
    }

    public String getFstransid()
    {
        return fstransid;
    }
    public void setFstransid(String fstransid)
    {
        this.fstransid = fstransid;
    }

    public String getTime()
    {
        return time;
    }
    public void setTime(String time)
    {
        this.time = time;
    }

    public String getCurrency()
    {
        return currency;
    }
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getReason()
    {
        return reason;
    }
    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getState()
    {
        return state;
    }
    public void setState(String state)
    {
        this.state = state;
    }

    public String getBirthDate() {return birthDate;}
    public void setBirthDate(String birthDate) {this.birthDate = birthDate;}

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public String getPartnerid() {return partnerid;}
    public void setPartnerid(String partnerid) {this.partnerid = partnerid;}

    public String getWebsite() {return website;}
    public void setWebsite(String website) {this.website = website;}

    public String getBillingfirstname() {return billingfirstname;}
    public void setBillingfirstname(String billingfirstname) {this.billingfirstname = billingfirstname;}

    public String getBillinglastname() {return billinglastname;}
    public void setBillinglastname(String billinglastname) {this.billinglastname = billinglastname;}

    public String getBillingemail() {return billingemail;}
    public void setBillingemail(String billingemail) {this.billingemail = billingemail;}

    public String getBillingaddress1() {return billingaddress1;}
    public void setBillingaddress1(String billingaddress1) {this.billingaddress1 = billingaddress1;}

    public String getBillingaddress2() {return billingaddress2;}
    public void setBillingaddress2(String billingaddress2) {this.billingaddress2 = billingaddress2;}

    public String getBillingcity() {return billingcity;}
    public void setBillingcity(String billingcity) {this.billingcity = billingcity;}

    public String getBillingprovince() {return billingprovince;}
    public void setBillingprovince(String billingprovince) {this.billingprovince = billingprovince;}

    public String getBillingcountry() {return billingcountry;}
    public void setBillingcountry(String billingcountry) {this.billingcountry = billingcountry;}

    public String getBillingpostalcode() {return billingpostalcode;}
    public void setBillingpostalcode(String billingpostalcode) {this.billingpostalcode = billingpostalcode;}

    public String getBillingphone1() {return billingphone1;}
    public void setBillingphone1(String billingphone1) {this.billingphone1 = billingphone1;}

    public String getBillingphone2() {return billingphone2;}
    public void setBillingphone2(String billingphone2) {this.billingphone2 = billingphone2;}

    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsernumber()
    {
        return usernumber;
    }
    public void setUsernumber(String usernumber)
    {
        this.usernumber = usernumber;
    }

    public String getIp() {return ip;}
    public void setIp(String ip) {this.ip = ip;}

    public String getCountry()
    {
        return country;
    }
    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getAddress1()
    {
        return address1;
    }
    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    public String getBinCountry()
    {
        return binCountry;
    }
    public void setBinCountry(String binCountry)
    {
        this.binCountry = binCountry;
    }

    public String getBinCardType() {return binCardType;}
    public void setBinCardType(String binCardType) {this.binCardType = binCardType;}

    public String getBinUsageType() {return binUsageType;}
    public void setBinUsageType(String binUsageType) {this.binUsageType = binUsageType;}
}
