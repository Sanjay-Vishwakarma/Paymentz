package com.fraud.fourstop;

import com.fraud.vo.FSGenericRequestVO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 11/10/16
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FourStopRequestVO extends FSGenericRequestVO
{
    FraudAccountDetailsVO fraudAccountDetailsVO;
    String session_id;
    String user_number;
    String user_name;
    String player_status;
    String reg_date;
    String reg_ip_address;
    String reg_device_id;
    String customer_information_first_name;
    String customer_information_middle_name;
    String customer_information_last_name ;
    String customer_information_email;
    String customer_information_address1 ;
    String customer_information_address2 ;
    String customer_information_city;
    String customer_information_province;
    String customer_information_country;
    String customer_information_postal_code;
    String customer_information_phone1;
    String customer_information_phone2;
    String customer_information_dob;
    String customer_information_gender;
    String customer_information_id_type;
    String customer_information_id_value;
    String customer_information_id_values;
    String first_dep_date;
    String first_with_date;
    String dep_count;
    String with_count;
    String current_balance;
    String deposit_limits_pay_method_type;
    String deposit_limits_dl_min;
    String deposit_limits_dl_daily;
    String deposit_limits_dl_weekly;
    String deposit_limits_dl_monthly;
    String trans_id;
    String payment_method_bin;
    String payment_method_last_digits;
    String payment_method_card_hash;
    String amount;
    String currency;
    String time;
    String status;
    String ip;
    String device_id;
    String quantity;
    String local_time;
    String source;
    String website;
    String custom_variable;
    String pass;
    String affiliate_name;
    String affiliate_id;
    String affiliate_sub_id;
    String mc_code;
    String billing_first_name;
    String billing_last_name;
    String billing_email;
    String billing_address1;
    String billing_address2;
    String billing_city;
    String billing_province;
    String billing_country;
    String billing_postal_code;
    String billing_phone1;
    String billing_phone2;
    String billing_gender;
    String memberId;

    //For Update Transaction Request
    String reason;
    String internal_trans_id;


    //extra parameters for create
     String merchant_id;
     String password;
     String billing_dob;
     String routing;
     String account;
     String ewallet_id;
     String device_fingerprint;
     String device_fingerprint_type;

    //For customerRegistration
    String bonus_code;
    String bonus_amount;
    String bonus_submission_date;
    String site_skin_name;
    String how_did_you_hear;
    String pfc_status;
    String pfc_type;
    String io_bb;
    String industry_type;
    String rule_context;
    String yt0;

    //For update customerRegistration
    String  processor;

    //** For DocumentIDVerify **//

    String customer_registration_id;
    String method;
    String filePath;
    String fileName;
    String fileName2;
    String fileName3;
    String fileName4;

    public FraudAccountDetailsVO getFraudAccountDetailsVO()
    {
        return fraudAccountDetailsVO;
    }

    public void setFraudAccountDetailsVO(FraudAccountDetailsVO fraudAccountDetailsVO)
    {
        this.fraudAccountDetailsVO = fraudAccountDetailsVO;
    }

    public String getSession_id()
    {
        return session_id;
    }

    public void setSession_id(String session_id)
    {
        this.session_id = session_id;
    }

    public String getUser_number()
    {
        return user_number;
    }

    public void setUser_number(String user_number)
    {
        this.user_number = user_number;
    }

    public String getUser_name()
    {
        return user_name;
    }

    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public String getPlayer_status()
    {
        return player_status;
    }

    public void setPlayer_status(String player_status)
    {
        this.player_status = player_status;
    }

    public String getReg_date()
    {
        return reg_date;
    }

    public void setReg_date(String reg_date)
    {
        this.reg_date = reg_date;
    }

    public String getReg_ip_address()
    {
        return reg_ip_address;
    }

    public void setReg_ip_address(String reg_ip_address)
    {
        this.reg_ip_address = reg_ip_address;
    }

    public String getReg_device_id()
    {
        return reg_device_id;
    }

    public void setReg_device_id(String reg_device_id)
    {
        this.reg_device_id = reg_device_id;
    }

    public String getCustomer_information_first_name()
    {
        return customer_information_first_name;
    }

    public void setCustomer_information_first_name(String customer_information_first_name)
    {
        this.customer_information_first_name = customer_information_first_name;
    }
    public String getCustomer_information_middle_name()
    {
        return customer_information_middle_name;
    }

    public void setCustomer_information_middle_name(String customer_information_middle_name)
    {
        this.customer_information_middle_name = customer_information_middle_name;
    }
    public String getCustomer_information_last_name()
    {
        return customer_information_last_name;
    }

    public void setCustomer_information_last_name(String customer_information_last_name)
    {
        this.customer_information_last_name = customer_information_last_name;
    }

    public String getCustomer_information_email()
    {
        return customer_information_email;
    }

    public void setCustomer_information_email(String customer_information_email)
    {
        this.customer_information_email = customer_information_email;
    }

    public String getCustomer_information_address1()
    {
        return customer_information_address1;
    }

    public void setCustomer_information_address1(String customer_information_address1)
    {
        this.customer_information_address1 = customer_information_address1;
    }

    public String getCustomer_information_address2()
    {
        return customer_information_address2;
    }

    public void setCustomer_information_address2(String customer_information_address2)
    {
        this.customer_information_address2 = customer_information_address2;
    }

    public String getCustomer_information_city()
    {
        return customer_information_city;
    }

    public void setCustomer_information_city(String customer_information_city)
    {
        this.customer_information_city = customer_information_city;
    }

    public String getCustomer_information_province()
    {
        return customer_information_province;
    }

    public void setCustomer_information_province(String customer_information_province)
    {
        this.customer_information_province = customer_information_province;
    }

    public String getCustomer_information_country()
    {
        return customer_information_country;
    }

    public void setCustomer_information_country(String customer_information_country)
    {
        this.customer_information_country = customer_information_country;
    }

    public String getCustomer_information_postal_code()
    {
        return customer_information_postal_code;
    }

    public void setCustomer_information_postal_code(String customer_information_postal_code)
    {
        this.customer_information_postal_code = customer_information_postal_code;
    }

    public String getCustomer_information_phone1()
    {
        return customer_information_phone1;
    }

    public void setCustomer_information_phone1(String customer_information_phone1)
    {
        this.customer_information_phone1 = customer_information_phone1;
    }

    public String getCustomer_information_phone2()
    {
        return customer_information_phone2;
    }

    public void setCustomer_information_phone2(String customer_information_phone2)
    {
        this.customer_information_phone2 = customer_information_phone2;
    }

    public String getCustomer_information_dob()
    {
        return customer_information_dob;
    }

    public void setCustomer_information_dob(String customer_information_dob)
    {
        this.customer_information_dob = customer_information_dob;
    }

    public String getCustomer_information_gender()
    {
        return customer_information_gender;
    }

    public void setCustomer_information_gender(String customer_information_gender)
    {
        this.customer_information_gender = customer_information_gender;
    }

    public String getCustomer_information_id_type()
    {
        return customer_information_id_type;
    }

    public void setCustomer_information_id_type(String customer_information_id_type)
    {
        this.customer_information_id_type = customer_information_id_type;
    }

    public String getCustomer_information_id_value()
    {
        return customer_information_id_value;
    }

    public void setCustomer_information_id_value(String customer_information_id_value)
    {
        this.customer_information_id_value = customer_information_id_value;
    }

    public String getCustomer_information_id_values()
    {
        return customer_information_id_values;
    }

    public void setCustomer_information_id_values(String customer_information_id_values)
    {
        this.customer_information_id_values = customer_information_id_values;
    }

    public String getFirst_dep_date()
    {
        return first_dep_date;
    }

    public void setFirst_dep_date(String first_dep_date)
    {
        this.first_dep_date = first_dep_date;
    }

    public String getFirst_with_date()
    {
        return first_with_date;
    }

    public void setFirst_with_date(String first_with_date)
    {
        this.first_with_date = first_with_date;
    }

    public String getDep_count()
    {
        return dep_count;
    }

    public void setDep_count(String dep_count)
    {
        this.dep_count = dep_count;
    }

    public String getWith_count()
    {
        return with_count;
    }

    public void setWith_count(String with_count)
    {
        this.with_count = with_count;
    }

    public String getCurrent_balance()
    {
        return current_balance;
    }

    public void setCurrent_balance(String current_balance)
    {
        this.current_balance = current_balance;
    }

    public String getDeposit_limits_pay_method_type()
    {
        return deposit_limits_pay_method_type;
    }

    public void setDeposit_limits_pay_method_type(String deposit_limits_pay_method_type)
    {
        this.deposit_limits_pay_method_type = deposit_limits_pay_method_type;
    }

    public String getDeposit_limits_dl_min()
    {
        return deposit_limits_dl_min;
    }

    public void setDeposit_limits_dl_min(String deposit_limits_dl_min)
    {
        this.deposit_limits_dl_min = deposit_limits_dl_min;
    }

    public String getDeposit_limits_dl_daily()
    {
        return deposit_limits_dl_daily;
    }

    public void setDeposit_limits_dl_daily(String deposit_limits_dl_daily)
    {
        this.deposit_limits_dl_daily = deposit_limits_dl_daily;
    }

    public String getDeposit_limits_dl_weekly()
    {
        return deposit_limits_dl_weekly;
    }

    public void setDeposit_limits_dl_weekly(String deposit_limits_dl_weekly)
    {
        this.deposit_limits_dl_weekly = deposit_limits_dl_weekly;
    }

    public String getDeposit_limits_dl_monthly()
    {
        return deposit_limits_dl_monthly;
    }

    public void setDeposit_limits_dl_monthly(String deposit_limits_dl_monthly)
    {
        this.deposit_limits_dl_monthly = deposit_limits_dl_monthly;
    }

    public String getTrans_id()
    {
        return trans_id;
    }

    public void setTrans_id(String trans_id)
    {
        this.trans_id = trans_id;
    }

    public String getPayment_method_bin()
    {
        return payment_method_bin;
    }

    public void setPayment_method_bin(String payment_method_bin)
    {
        this.payment_method_bin = payment_method_bin;
    }

    public String getPayment_method_last_digits()
    {
        return payment_method_last_digits;
    }

    public void setPayment_method_last_digits(String payment_method_last_digits)
    {
        this.payment_method_last_digits = payment_method_last_digits;
    }

    public String getPayment_method_card_hash()
    {
        return payment_method_card_hash;
    }

    public void setPayment_method_card_hash(String payment_method_card_hash)
    {
        this.payment_method_card_hash = payment_method_card_hash;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getDevice_id()
    {
        return device_id;
    }

    public void setDevice_id(String device_id)
    {
        this.device_id = device_id;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }

    public String getLocal_time()
    {
        return local_time;
    }

    public void setLocal_time(String local_time)
    {
        this.local_time = local_time;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getCustom_variable()
    {
        return custom_variable;
    }

    public void setCustom_variable(String custom_variable)
    {
        this.custom_variable = custom_variable;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public String getAffiliate_name()
    {
        return affiliate_name;
    }

    public void setAffiliate_name(String affiliate_name)
    {
        this.affiliate_name = affiliate_name;
    }

    public String getAffiliate_id()
    {
        return affiliate_id;
    }

    public void setAffiliate_id(String affiliate_id)
    {
        this.affiliate_id = affiliate_id;
    }

    public String getAffiliate_sub_id()
    {
        return affiliate_sub_id;
    }

    public void setAffiliate_sub_id(String affiliate_sub_id)
    {
        this.affiliate_sub_id = affiliate_sub_id;
    }

    public String getMc_code()
    {
        return mc_code;
    }

    public void setMc_code(String mc_code)
    {
        this.mc_code = mc_code;
    }

    public String getBilling_first_name()
    {
        return billing_first_name;
    }

    public void setBilling_first_name(String billing_first_name)
    {
        this.billing_first_name = billing_first_name;
    }

    public String getBilling_last_name()
    {
        return billing_last_name;
    }

    public void setBilling_last_name(String billing_last_name)
    {
        this.billing_last_name = billing_last_name;
    }

    public String getBilling_email()
    {
        return billing_email;
    }

    public void setBilling_email(String billing_email)
    {
        this.billing_email = billing_email;
    }

    public String getBilling_address1()
    {
        return billing_address1;
    }

    public void setBilling_address1(String billing_address1)
    {
        this.billing_address1 = billing_address1;
    }

    public String getBilling_address2()
    {
        return billing_address2;
    }

    public void setBilling_address2(String billing_address2)
    {
        this.billing_address2 = billing_address2;
    }

    public String getBilling_city()
    {
        return billing_city;
    }

    public void setBilling_city(String billing_city)
    {
        this.billing_city = billing_city;
    }

    public String getBilling_province()
    {
        return billing_province;
    }

    public void setBilling_province(String billing_province)
    {
        this.billing_province = billing_province;
    }

    public String getBilling_country()
    {
        return billing_country;
    }

    public void setBilling_country(String billing_country)
    {
        this.billing_country = billing_country;
    }

    public String getBilling_postal_code()
    {
        return billing_postal_code;
    }

    public void setBilling_postal_code(String billing_postal_code)
    {
        this.billing_postal_code = billing_postal_code;
    }

    public String getBilling_phone1()
    {
        return billing_phone1;
    }

    public void setBilling_phone1(String billing_phone1)
    {
        this.billing_phone1 = billing_phone1;
    }

    public String getBilling_phone2()
    {
        return billing_phone2;
    }

    public void setBilling_phone2(String billing_phone2)
    {
        this.billing_phone2 = billing_phone2;
    }

    public String getBilling_gender()
    {
        return billing_gender;
    }

    public void setBilling_gender(String billing_gender)
    {
        this.billing_gender = billing_gender;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getInternal_trans_id()
    {
        return internal_trans_id;
    }

    public void setInternal_trans_id(String internal_trans_id)
    {
        this.internal_trans_id = internal_trans_id;
    }

    @Override
    public String getMerchant_id()
    {
        return merchant_id;
    }

    @Override
    public void setMerchant_id(String merchant_id)
    {
        this.merchant_id = merchant_id;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getBilling_dob()
    {
        return billing_dob;
    }

    public void setBilling_dob(String billing_dob)
    {
        this.billing_dob = billing_dob;
    }

    public String getRouting()
    {
        return routing;
    }

    public void setRouting(String routing)
    {
        this.routing = routing;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getEwallet_id()
    {
        return ewallet_id;
    }

    public void setEwallet_id(String ewallet_id)
    {
        this.ewallet_id = ewallet_id;
    }

    public String getDevice_fingerprint()
    {
        return device_fingerprint;
    }

    public void setDevice_fingerprint(String device_fingerprint)
    {
        this.device_fingerprint = device_fingerprint;
    }

    public String getDevice_fingerprint_type()
    {
        return device_fingerprint_type;
    }

    public void setDevice_fingerprint_type(String device_fingerprint_type)
    {
        this.device_fingerprint_type = device_fingerprint_type;
    }

    public String getProcessor()
    {
        return processor;
    }

    public void setProcessor(String processor)
    {
        this.processor = processor;
    }

    /* new getter setter for customerRegistration  */

    public String getBonus_code()
    {
        return bonus_code;
    }

    public void setBonus_code(String bonus_code)
    {
        this.bonus_code = bonus_code;
    }

    public String getBonus_amount()
    {
        return bonus_amount;
    }

    public void setBonus_amount(String bonus_amount)
    {
        this.bonus_amount = bonus_amount;
    }

    public String getBonus_submission_date(String bonus_submission_date)
    {
        return this.bonus_submission_date;
    }

    public void setBonus_submission_date(String bonus_submission_date)
    {
        this.bonus_submission_date = bonus_submission_date;
    }

    public String getSite_skin_name()
    {
        return site_skin_name;
    }

    public void setSite_skin_name(String site_skin_name)
    {
        this.site_skin_name = site_skin_name;
    }

    public String getHow_did_you_hear()
    {
        return how_did_you_hear;
    }

    public void setHow_did_you_hear(String how_did_you_hear)
    {
        this.how_did_you_hear = how_did_you_hear;
    }

    public String getPfc_status()
    {
        return pfc_status;
    }

    public void setPfc_status(String pfc_status)
    {
        this.pfc_status = pfc_status;
    }

    public String getPfc_type()
    {
        return pfc_type;
    }

    public void setPfc_type(String pfc_type)
    {
        this.pfc_type = pfc_type;
    }

    public String getIo_bb()
    {
        return io_bb;
    }

    public void setIo_bb(String io_bb)
    {
        this.io_bb = io_bb;
    }

    public String getIndustry_type()
    {
        return industry_type;
    }

    public void setIndustry_type(String industry_type)
    {
        this.industry_type = industry_type;
    }

    public String getRule_context()
    {
        return rule_context;
    }

    public void setRule_context(String rule_context)
    {
        this.rule_context = rule_context;
    }

    public String getYt0()
    {
        return yt0;
    }

    public void setYt0(String yt0)
    {
        this.yt0 = yt0;
    }

    public String getBonus_submission_date()
    {
        return bonus_submission_date;
    }

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

    public HashMap getHashMap()
    {
        HashMap requestMap = new HashMap();


        /*requestMap.put("merchant_id",merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("customer_information[first_name]",this.customer_information_first_name);

        requestMap.put("customer_information[last_name]",this.customer_information_last_name);
        requestMap.put("customer_information[email]",this.customer_information_last_name);
        requestMap.put("customer_information[address1]",this.customer_information_address1);
        requestMap.put("customer_information[city]",this.customer_information_city);
        requestMap.put("customer_information[country]",this.customer_information_country);
        requestMap.put("customer_information[postal_code]",customer_information_postal_code);

        requestMap.put("deposit_limits[pay_method_type]",this);
        requestMap.put("deposit_limits[dl_min]","20");
        requestMap.put("deposit_limits[dl_daily]","20");
        requestMap.put("deposit_limits[dl_weekly]","20");
        requestMap.put("deposit_limits[dl_monthly]","20");
        requestMap.put("trans_id",this.trans_id);
        requestMap.put("payment_method[bin]",this.payment_method_bin);
        requestMap.put("payment_method[last_digits]",this.payment_method_last_digits);
        requestMap.put("amount","1000");
        requestMap.put("currency",this.currency);
        requestMap.put("time",this.time);
        requestMap.put("status",this.status);
        requestMap.put("ip",this.ip);*/




        requestMap.put("merchant_id",merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("session_id", this.session_id);
        requestMap.put("user_number", this.user_number);
        requestMap.put("user_name", this.user_name );
        requestMap.put("player_status", this.player_status);
        requestMap.put("reg_date", this.reg_date);
        requestMap.put("reg_ip_address", this.reg_ip_address);
        requestMap.put("reg_device_id", this.reg_device_id);
        requestMap.put("reason", this.reason);
        requestMap.put("internal_trans_id", this.internal_trans_id);

        requestMap.put("customer_information[first_name]", this.customer_information_first_name);
        requestMap.put("customer_information[middle_name]", this.customer_information_middle_name);
        requestMap.put("customer_information[last_name]", this.customer_information_last_name);
        requestMap.put("customer_information[email]", this.customer_information_email);
        requestMap.put("customer_information[address1]", this.customer_information_address1);
        requestMap.put("customer_information[address2]", this.customer_information_address2);
        requestMap.put("customer_information[city]", this.customer_information_city);
        requestMap.put("customer_information[province]", this.customer_information_province);
        requestMap.put("customer_information[country]", this.customer_information_country);

        requestMap.put("customer_information[postal_code]", this.customer_information_postal_code);
        requestMap.put("customer_information[phone1]", this.customer_information_phone1);
        requestMap.put("customer_information[phone2]", this.customer_information_phone2);
        requestMap.put("customer_information[dob]", this.customer_information_dob);
        requestMap.put("customer_information[gender]", this.customer_information_gender);
        requestMap.put("customer_information[id_type]", this.customer_information_id_type);
        requestMap.put("customer_information[id_value]", this.customer_information_id_value);
        requestMap.put("customer_information[id_values]", this.customer_information_id_values);

        requestMap.put("first_dep_date", this.first_dep_date);
        requestMap.put("first_with_date", this.first_with_date);
        requestMap.put("dep_count", this.dep_count);
        requestMap.put("with_count", this.with_count);
        requestMap.put("current_balance", this.current_balance);

        requestMap.put("deposit_limits[pay_method_type]", this.deposit_limits_pay_method_type);
        requestMap.put("deposit_limits[dl_min]", this.deposit_limits_dl_min);
        requestMap.put("deposit_limits[dl_daily]", this.deposit_limits_dl_daily);
        requestMap.put("deposit_limits[dl_weekly]", this.deposit_limits_dl_weekly);
        requestMap.put("deposit_limits[dl_monthly]", this.deposit_limits_dl_monthly);
        requestMap.put("trans_id", this.trans_id);

        requestMap.put("payment_method[bin]", this.payment_method_bin);
        requestMap.put("payment_method[last_digits]", this.payment_method_last_digits);
        requestMap.put("payment_method[card_hash]", this.payment_method_card_hash);
        requestMap.put("amount",this.amount);
        requestMap.put("currency", this.currency);
        requestMap.put("time", this.time);

        requestMap.put("status", this.status);
        requestMap.put("ip", this.ip);
        requestMap.put("device_id", this.device_id);
        requestMap.put("quantity", this.quantity);
        requestMap.put("local_time", this.local_time);
        requestMap.put("source", this.source);

        requestMap.put("website", this.website);
        requestMap.put("custom_variable", this.custom_variable);
        requestMap.put("pass", this.pass);
        requestMap.put("affiliate_name", this.affiliate_name);
        requestMap.put("affiliate_id", this.affiliate_id);
        requestMap.put("affiliate_sub_id", this.affiliate_sub_id);
        requestMap.put("affiliate_sub_id", this.affiliate_sub_id);
        requestMap.put("mc_code", this.mc_code);

        requestMap.put("billing[first_name]", this.billing_first_name);
        requestMap.put("billing[last_name]", this.billing_last_name);
        requestMap.put("billing[email]", this.billing_email);
        requestMap.put("billing[address1]", this.billing_address1);
        requestMap.put("billing[address2]", this.billing_address2);
        requestMap.put("billing[city]", this.billing_city);

        requestMap.put("billing[province]", this.billing_province);
        requestMap.put("billing[country]", this.billing_country);
        requestMap.put("billing[postal_code]", this.billing_postal_code);
        requestMap.put("billing[phone1]", this.billing_phone1);
        requestMap.put("billing[phone2]", this.billing_phone2);
        requestMap.put("billing[gender]", this.billing_gender);

        requestMap.put("device_fingerprint",this.device_fingerprint);
        requestMap.put("device_fingerprint_type",this.device_fingerprint_type);
        requestMap.put("bonus_code",this.bonus_code);
        requestMap.put("bonus_amount",this.bonus_amount);
        requestMap.put("bonus_submission_date",this.bonus_submission_date);
        requestMap.put("site_skin_name",this.site_skin_name);
        requestMap.put("how_did_you_hear",this.how_did_you_hear);
        requestMap.put("pfc_status",this.pfc_status);
        requestMap.put("pfc_type",this.pfc_type);
        requestMap.put("io_bb",this.io_bb);
        requestMap.put("industry_type",this.industry_type);
        requestMap.put("rule_context",this.rule_context);
        requestMap.put("yt0",this.yt0);


        requestMap.put("customer_registration_id",this.customer_registration_id);
        requestMap.put("method",this.method);

        return requestMap;

    }
    public void setHashMap(HashMap requestHash)
    {
        this.merchant_id   =  (String) requestHash.get("merchant_id");
        this.password      =  (String) requestHash.get("password");
        this.session_id    =  (String) requestHash.get("session_id");
        this.user_number   =  (String) requestHash.get("user_number");
        this.user_name     =  (String) requestHash.get("user_name");
        this.player_status =  (String) requestHash.get("player_status");
        this.reg_ip_address=  (String) requestHash.get("reg_ip_address");
        this.reg_device_id =  (String) requestHash.get("reg_device_id");
        this.reason =  (String) requestHash.get("reason");
        this.internal_trans_id =  (String) requestHash.get("internal_trans_id");

        this.customer_information_first_name  =  (String) requestHash.get("customer_information[first_name]");
        this.customer_information_middle_name  =  (String) requestHash.get("customer_information[middle_name]");
        this.customer_information_last_name   =  (String) requestHash.get("customer_information[last_name]");
        this.customer_information_email       =  (String) requestHash.get("customer_information[email]");
        this.customer_information_address1    =  (String) requestHash.get("customer_information[address1]");
        this.customer_information_address2    =  (String) requestHash.get("customer_information[address2]");
        this.customer_information_city        =  (String) requestHash.get("customer_information[city]");

        this.customer_information_province    =  (String) requestHash.get("customer_information[province]");
        this.customer_information_country     =  (String) requestHash.get("customer_information[country]");
        this.customer_information_postal_code =  (String) requestHash.get("customer_information[postal_code]");
        this.customer_information_phone1      =  (String) requestHash.get("customer_information[phone1]");
        this.customer_information_phone2      =  (String) requestHash.get("customer_information[phone2]");

        this.customer_information_dob         =  (String) requestHash.get("customer_information[dob]");
        this.customer_information_gender      =  (String) requestHash.get("customer_information[gender]");
        this.customer_information_id_type     =  (String) requestHash.get("customer_information[id_type]");
        this.customer_information_id_value    =  (String) requestHash.get("customer_information[id_value]");
        this.customer_information_id_values    =  (String) requestHash.get("customer_information[id_values]");

        this.first_dep_date    =  (String) requestHash.get("first_dep_date");
        this.first_with_date   =  (String) requestHash.get("first_with_date");
        this.dep_count         =  (String) requestHash.get("dep_count");
        this.with_count        =  (String) requestHash.get("with_count");
        this.current_balance   =  (String) requestHash.get("current_balance");

        this.deposit_limits_pay_method_type  =  (String) requestHash.get("deposit_limits[pay_method_type]");
        this.deposit_limits_dl_min           =  (String) requestHash.get("deposit_limits[dl_min]");
        this.deposit_limits_dl_daily         =  (String) requestHash.get("deposit_limits[dl_daily]");
        this.deposit_limits_dl_weekly        =  (String) requestHash.get("deposit_limits[dl_weekly]");
        this.deposit_limits_dl_monthly       =  (String) requestHash.get("deposit_limits[dl_monthly]");
        this.trans_id                        =  (String) requestHash.get("trans_id");

        this.payment_method_bin         =  (String) requestHash.get("payment_method[bin]");
        this.payment_method_last_digits =  (String) requestHash.get("payment_method[last_digits]");
        this.payment_method_card_hash   =  (String) requestHash.get("payment_method[card_hash]");

        this.amount     =  (String) requestHash.get("amount");
        this.currency   =  (String) requestHash.get("currency");
        this.time       =  (String) requestHash.get("time");
        this.status     =  (String) requestHash.get("status");
        this.ip         =  (String) requestHash.get("ip");
        this.device_id  =  (String) requestHash.get("device_id");
        this.quantity   =  (String) requestHash.get("quantity");
        this.local_time =  (String) requestHash.get("local_time");
        this.source     =  (String) requestHash.get("source");
        this.website    =  (String) requestHash.get("website");

        this.custom_variable     =  (String) requestHash.get("custom_variable");
        this.pass                =  (String) requestHash.get("pass");
        this.affiliate_name      =  (String) requestHash.get("affiliate_name");
        this.affiliate_id        =  (String) requestHash.get("affiliate_id");
        this.affiliate_sub_id    =  (String) requestHash.get("affiliate_sub_id");
        this.mc_code             =  (String) requestHash.get("mc_code");

        this.billing_first_name  =  (String) requestHash.get("billing_first_name");
        this.billing_last_name   =  (String) requestHash.get("billing_last_name");
        this.billing_email       =  (String) requestHash.get("billing_email");
        this.billing_address1    =  (String) requestHash.get("billing_address1");
        this.billing_address2    =  (String) requestHash.get("billing_address2");
        this.billing_city        =  (String) requestHash.get("billing_city");

        this.billing_province    =  (String) requestHash.get("billing_province");
        this.billing_country     =  (String) requestHash.get("billing_country");
        this.billing_postal_code =  (String) requestHash.get("billing_postal_code");
        this.billing_phone1      =  (String) requestHash.get("billing_phone1");
        this.billing_phone2 =  (String) requestHash.get("billing_phone2");
        this.billing_gender =  (String) requestHash.get("billing_gender");

        this.bonus_code=(String) requestHash.get("bonus_code");
        this.bonus_amount=(String) requestHash.get("bonus_amount");
        this.bonus_submission_date=(String) requestHash.get("bonus_submission_date");
        this.site_skin_name=(String) requestHash.get("site_skin_name");
        this.how_did_you_hear=(String) requestHash.get("how_did_you_hear");
        this.pfc_status=(String) requestHash.get("pfc_status");
        this.pfc_type=(String) requestHash.get("pfc_type");
        this.io_bb=(String) requestHash.get("io_bb");
        this.industry_type=(String) requestHash.get("industry_type");
        this.rule_context=(String) requestHash.get("rule_context");
        this.yt0=(String) requestHash.get("yt0");

        this.customer_registration_id=(String) requestHash.get("customer_registration_id");
        this.method=(String) requestHash.get("method");
    }


}
