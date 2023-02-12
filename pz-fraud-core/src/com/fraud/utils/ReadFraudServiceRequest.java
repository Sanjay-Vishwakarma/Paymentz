package com.fraud.utils;

import com.directi.pg.Functions;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.fraud.at.ATRequestVO;
import com.fraud.fourstop.FourStopRequestVO;
import com.fraud.vo.*;
import com.fraud.vo.RestVO.RequestVO.Authentication;
import com.fraud.vo.RestVO.RequestVO.AuthenticationVO;
import com.fraud.vo.RestVO.RequestVO.Partner;
import com.fraud.vo.RestVO.RequestVO.PartnerVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.validators.vo.CommonValidatorVO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 11/1/14
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadFraudServiceRequest
{
    Functions functions=new Functions();

    private static String getValue(String sTag, HttpServletRequest req)
    {
        String value  ="";
        if(req.getParameter(sTag)!=null && !req.getParameter(sTag).equals(""))
        {
            value = req.getParameter(sTag);
        }

        return value;

    }

    private static String getValue(String sTag, Hashtable hashtable)
    {
        String value  ="";
        if(hashtable.get(sTag)!=null && !hashtable.get(sTag).equals(""))
        {
            value =(String)hashtable.get(sTag);
        }

        return value;

    }

    public PZFraudRequestVO getPZFraudRequestVO(HashMap transDetails)
    {
        PZFraudRequestVO pzFraudRequestVO=new PZFraudRequestVO();
        pzFraudRequestVO.setMemberid((String)transDetails.get("memberid"));
        pzFraudRequestVO.setTrackingid((String)transDetails.get("trans_id"));
        pzFraudRequestVO.setDescription((String)transDetails.get("description"));
        pzFraudRequestVO.setAmount((String)transDetails.get("amount"));
        pzFraudRequestVO.setStatus((String)transDetails.get("status"));
        pzFraudRequestVO.setCurrency((String)transDetails.get("currency"));
        pzFraudRequestVO.setTime((String)transDetails.get("time"));
        pzFraudRequestVO.setFirstname((String)transDetails.get("customer_information[first_name]"));
        pzFraudRequestVO.setLastname((String)transDetails.get("customer_information[last_name]"));
        pzFraudRequestVO.setEmailaddr((String)transDetails.get("customer_information[email]"));
        pzFraudRequestVO.setZip((String)transDetails.get("customer_information[postal_code]"));
        pzFraudRequestVO.setStreet((String)transDetails.get("customer_information[address1]"));
        pzFraudRequestVO.setCity((String)transDetails.get("customer_information[city]"));
        pzFraudRequestVO.setCountrycode((String)transDetails.get("customer_information[country]"));
        pzFraudRequestVO.setIpaddrs((String)transDetails.get("ip"));
        pzFraudRequestVO.setDeviceid((String)transDetails.get("device_id"));
        pzFraudRequestVO.setFirstsix((String)transDetails.get("bin"));
        pzFraudRequestVO.setLastfour((String)transDetails.get("last_4"));
        pzFraudRequestVO.setDailycardminlimit((String)transDetails.get("deposit_limits[dl_min]"));
        pzFraudRequestVO.setDailycardlimit((String)transDetails.get("deposit_limits[dl_daily]"));
        pzFraudRequestVO.setWeeklycardlimit((String)transDetails.get("deposit_limits[dl_weekly]"));
        pzFraudRequestVO.setMonthlycardlimit((String)transDetails.get("deposit_limits[dl_monthly]"));
        pzFraudRequestVO.setPaymenttype((String)transDetails.get("deposit_limits[pay_method_type]"));
        pzFraudRequestVO.setAccountid((String)transDetails.get("accountId"));
        return pzFraudRequestVO;
    }

    public PZFraudRequestVO getPZFraudRequestVO(HttpServletRequest request)
    {
        PZFraudRequestVO pzFraudRequestVO=new PZFraudRequestVO();
        pzFraudRequestVO.setMemberid(getValue("memberid", request));

        pzFraudRequestVO.setDescription(getValue("description", request));
        pzFraudRequestVO.setAmount(getValue("amount", request));
        pzFraudRequestVO.setStatus(getValue("status", request));
        pzFraudRequestVO.setCurrency(getValue("currency", request));
        pzFraudRequestVO.setTime(getValue("time", request));
        pzFraudRequestVO.setFirstname(getValue("firstname", request));
        pzFraudRequestVO.setLastname(getValue("lastname", request));
        pzFraudRequestVO.setEmailaddr(getValue("emailaddr", request));
        pzFraudRequestVO.setZip(getValue("zip", request));
        pzFraudRequestVO.setStreet(getValue("street", request));
        pzFraudRequestVO.setCity(getValue("city", request));
        pzFraudRequestVO.setCountrycode(getValue("countrycode", request));
        pzFraudRequestVO.setState(getValue("state", request));

        pzFraudRequestVO.setIpaddrs(getValue("ipaddrs", request));
        pzFraudRequestVO.setDeviceid(getValue("deviceid", request));
        pzFraudRequestVO.setFirstsix(getValue("firstsix", request));
        pzFraudRequestVO.setLastfour(getValue("lastfour", request));
        pzFraudRequestVO.setDailycardminlimit(getValue("dailycardminlimit", request));
        pzFraudRequestVO.setDailycardlimit(getValue("dailycardlimit", request));
        pzFraudRequestVO.setWeeklycardlimit(getValue("weeklycardlimit", request));
        pzFraudRequestVO.setMonthlycardlimit(getValue("monthlycardlimit", request));
        pzFraudRequestVO.setPaymenttype(getValue("paymenttype", request));
        pzFraudRequestVO.setPzfraudtransid(getValue("pzfraudtransid", request));
        pzFraudRequestVO.setReason(getValue("reason", request));


        return pzFraudRequestVO;
    }

    public void setATRequestParameter(PZFraudRequestVO requestVO,ATRequestVO atRequestVO)
    {
        atRequestVO.setTrans_id(requestVO.getPzfraudtransid());
        atRequestVO.setCurrency(requestVO.getCurrency());
        atRequestVO.setAmount(requestVO.getAmount() + "00");
        atRequestVO.setStatus(requestVO.getStatus());
        atRequestVO.setTime(requestVO.getTime());
        atRequestVO.setCustomer_information_first_name(requestVO.getFirstname());
        atRequestVO.setCustomer_information_last_name(requestVO.getLastname());
        atRequestVO.setCustomer_information_email(requestVO.getEmailaddr());
        atRequestVO.setCustomer_information_city(requestVO.getCity());
        atRequestVO.setCustomer_information_country(requestVO.getCountrycode());
        atRequestVO.setCustomer_information_address1(requestVO.getStreet());
        atRequestVO.setCustomer_information_postal_code(requestVO.getZip());
        atRequestVO.setIp(requestVO.getIpaddrs());

        //Newly added considering sophie recommendation
        atRequestVO.setReg_ip_address(requestVO.getIpaddrs());
        atRequestVO.setCustomer_information_province(requestVO.getState());
        atRequestVO.setCustomer_information_phone1(requestVO.getPhone());
        atRequestVO.setCustomer_information_dob(requestVO.getBirthDate());
        //till:Newly added considering sophie recommendation

        atRequestVO.setBilling_first_name(requestVO.getFirstname());
        atRequestVO.setBilling_last_name(requestVO.getLastname());
        atRequestVO.setBilling_email(requestVO.getEmailaddr());
        atRequestVO.setBilling_city(requestVO.getCity());
        atRequestVO.setBilling_country(requestVO.getCountrycode());
        atRequestVO.setBilling_address1(requestVO.getStreet());
        atRequestVO.setBilling_postal_code(requestVO.getZip());
        atRequestVO.setBilling_province(requestVO.getState());
        atRequestVO.setBilling_phone1(requestVO.getPhone());

        atRequestVO.setPayment_method_bin(requestVO.getFirstsix());
        atRequestVO.setPayment_method_last_digits(requestVO.getLastfour());
        atRequestVO.setDeposit_limits_dl_min(requestVO.getDailycardminlimit());
        atRequestVO.setDeposit_limits_dl_daily(requestVO.getDailycardlimit());
        atRequestVO.setDeposit_limits_dl_monthly(requestVO.getMonthlycardlimit());
        atRequestVO.setDeposit_limits_dl_weekly(requestVO.getWeeklycardlimit());
        atRequestVO.setDeposit_limits_pay_method_type(requestVO.getPaymenttype());

        atRequestVO.setInternal_tran_id(requestVO.getFstransid());
        atRequestVO.setReason(requestVO.getReason());
        atRequestVO.setMemberId(requestVO.getMemberid());

        //newly added for customerUpdatation


    }

    public void setFourStopRequestParameter(PZFraudRequestVO requestVO,FourStopRequestVO fourStopRequestVO)
    {
        fourStopRequestVO.setTrans_id(requestVO.getPzfraudtransid());
        fourStopRequestVO.setCurrency(requestVO.getCurrency());
        fourStopRequestVO.setAmount(requestVO.getAmount() + "00");
        fourStopRequestVO.setStatus(requestVO.getStatus());
        fourStopRequestVO.setTime(requestVO.getTime());
        fourStopRequestVO.setCustomer_information_first_name(requestVO.getFirstname());
        fourStopRequestVO.setCustomer_information_last_name(requestVO.getLastname());
        fourStopRequestVO.setCustomer_information_email(requestVO.getEmailaddr());
        fourStopRequestVO.setCustomer_information_city(requestVO.getCity());
        fourStopRequestVO.setCustomer_information_country(requestVO.getCountrycode());
        fourStopRequestVO.setCustomer_information_address1(requestVO.getStreet());
        fourStopRequestVO.setCustomer_information_postal_code(requestVO.getZip());
        fourStopRequestVO.setIp(requestVO.getIpaddrs());

        //Newly added considering sophie recommendation
        fourStopRequestVO.setReg_ip_address(requestVO.getIpaddrs());
        fourStopRequestVO.setCustomer_information_province(requestVO.getState());
        fourStopRequestVO.setCustomer_information_phone1(requestVO.getPhone());
        fourStopRequestVO.setCustomer_information_dob(requestVO.getBirthDate());
        //till:Newly added considering sophie recommendation

        fourStopRequestVO.setBilling_first_name(requestVO.getFirstname());
        fourStopRequestVO.setBilling_last_name(requestVO.getLastname());
        fourStopRequestVO.setBilling_email(requestVO.getEmailaddr());
        fourStopRequestVO.setBilling_city(requestVO.getCity());
        fourStopRequestVO.setBilling_country(requestVO.getCountrycode());
        fourStopRequestVO.setBilling_address1(requestVO.getStreet());
        fourStopRequestVO.setBilling_postal_code(requestVO.getZip());
        fourStopRequestVO.setBilling_province(requestVO.getState());
        fourStopRequestVO.setBilling_phone1(requestVO.getPhone());

        fourStopRequestVO.setPayment_method_bin(requestVO.getFirstsix());
        fourStopRequestVO.setPayment_method_last_digits(requestVO.getLastfour());
        fourStopRequestVO.setDeposit_limits_dl_min(requestVO.getDailycardminlimit());
        fourStopRequestVO.setDeposit_limits_dl_daily(requestVO.getDailycardlimit());
        fourStopRequestVO.setDeposit_limits_dl_monthly(requestVO.getMonthlycardlimit());
        fourStopRequestVO.setDeposit_limits_dl_weekly(requestVO.getWeeklycardlimit());
        fourStopRequestVO.setDeposit_limits_pay_method_type(requestVO.getPaymenttype());

        fourStopRequestVO.setInternal_trans_id(requestVO.getFstransid());
        fourStopRequestVO.setReason(requestVO.getReason());
        fourStopRequestVO.setMemberId(requestVO.getMemberid());
    }

    public void setFourStopCustDetailsParameter(PZFraudCustRegRequestVO requestVO,FourStopRequestVO fourStopRequestVO, int customerID)
    {
        fourStopRequestVO.setUser_number(String.valueOf(customerID));
        fourStopRequestVO.setReg_date(requestVO.getCustomerRegDate());
        fourStopRequestVO.setReg_ip_address(requestVO.getCustomerIpAddress());
        fourStopRequestVO.setReg_device_id(requestVO.getCustomerDeviceId());

        fourStopRequestVO.setCustomer_information_first_name(requestVO.getFirstName());
        fourStopRequestVO.setCustomer_information_last_name(requestVO.getLastName());
        fourStopRequestVO.setCustomer_information_email(requestVO.getEmailId());
        fourStopRequestVO.setCustomer_information_city(requestVO.getCity());
        fourStopRequestVO.setCustomer_information_country(requestVO.getCountryCode());
        fourStopRequestVO.setCustomer_information_address1(requestVO.getAddress1());
        fourStopRequestVO.setCustomer_information_address2(requestVO.getAddress2());
        fourStopRequestVO.setCustomer_information_postal_code(requestVO.getZip());
        fourStopRequestVO.setCustomer_information_province(requestVO.getState());
        fourStopRequestVO.setCustomer_information_phone1(requestVO.getPhone());
        fourStopRequestVO.setCustomer_information_dob(requestVO.getBirthDate());
        fourStopRequestVO.setCustomer_information_gender(requestVO.getGender());
        fourStopRequestVO.setCustomer_information_id_type(requestVO.getId_type());
        fourStopRequestVO.setCustomer_information_id_value(requestVO.getId_value());

        fourStopRequestVO.setReason(requestVO.getReason());
        fourStopRequestVO.setPfc_status(requestVO.getPfc_status());
        fourStopRequestVO.setPfc_type(requestVO.getPfc_type());

        fourStopRequestVO.setSite_skin_name(requestVO.getWebsite());
        fourStopRequestVO.setAffiliate_id(requestVO.getAffiliate_id());
        fourStopRequestVO.setDevice_fingerprint(requestVO.getDevice_fingerprint());
        fourStopRequestVO.setDevice_fingerprint_type(requestVO.getDevice_fingerprint_type());
        fourStopRequestVO.setBonus_code(requestVO.getBonus_code());
        fourStopRequestVO.setBonus_amount(requestVO.getBonus_amount());
        fourStopRequestVO.getBonus_submission_date(requestVO.getBonus_submission_date());
        fourStopRequestVO.setHow_did_you_hear(requestVO.getHow_did_you_hear());
        fourStopRequestVO.setPfc_status(requestVO.getRegStatus());
        fourStopRequestVO.setPfc_type(requestVO.getRegType());
        fourStopRequestVO.setIndustry_type(requestVO.getIndustry_type());
        fourStopRequestVO.setHow_did_you_hear(requestVO.getHow_did_you_hear());
        fourStopRequestVO.setRule_context(requestVO.getRule_context());

    }

    /*  public void setFourStopCustUpdateParameter(PZFraudCustomerUpdateVO requestVO,FourStopRequestVO fourStopRequestVO)
      {
          fourStopRequestVO.setInternal_trans_id(requestVO.getCustomerRegId());
          fourStopRequestVO.setReason(requestVO.getReason());
          fourStopRequestVO.setPfc_status(requestVO.getRegStatus());
          fourStopRequestVO.setPfc_type(requestVO.getRegType());

      }*/
    /*
    * Make Online Fraud Checking Using Payment Fraud Processor
    * This method set the required parameter for PZFraudProcessor for new transaction
    * Method call done from FraudChecker class
    * */
    public PZFraudRequestVO getPZFraudRequestVO(CommonValidatorVO commonValidatorVO,GatewayAccount gatewayAccount)
    {
        Functions functions                                 = new Functions();
        GenericTransDetailsVO genericTransDetailsVO         = commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO     = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO           = commonValidatorVO.getCardDetailsVO();
        MerchantDetailsVO merchantDetailsVO                 = commonValidatorVO.getMerchantDetailsVO();
        PZFraudRequestVO pzFraudRequestVO                   = new PZFraudRequestVO();

        pzFraudRequestVO.setMemberid(merchantDetailsVO.getMemberId());
        pzFraudRequestVO.setTrackingid(commonValidatorVO.getTrackingid());

        //Set The Transaction Details
        pzFraudRequestVO.setDescription(genericTransDetailsVO.getOrderId());
        pzFraudRequestVO.setAmount(genericTransDetailsVO.getAmount());
        pzFraudRequestVO.setStatus("0");
        pzFraudRequestVO.setCurrency(gatewayAccount.getCurrency());
        pzFraudRequestVO.setTime(commonValidatorVO.getTime());

        pzFraudRequestVO.setBinCardType(genericCardDetailsVO.getBin_card_type());// added for Internal Fraud Rules
        pzFraudRequestVO.setBinUsageType(genericCardDetailsVO.getBin_usage_type());

        //Set Customer Details
        pzFraudRequestVO.setFirstname(genericAddressDetailsVO.getFirstname());
        pzFraudRequestVO.setLastname(genericAddressDetailsVO.getLastname());
        pzFraudRequestVO.setEmailaddr(genericAddressDetailsVO.getEmail());
        pzFraudRequestVO.setZip(genericAddressDetailsVO.getZipCode());
        pzFraudRequestVO.setStreet(genericAddressDetailsVO.getStreet());
        pzFraudRequestVO.setCity(genericAddressDetailsVO.getCity());
        pzFraudRequestVO.setCountrycode(genericAddressDetailsVO.getCountry());
        pzFraudRequestVO.setState(genericAddressDetailsVO.getState());
        pzFraudRequestVO.setPhone(genericAddressDetailsVO.getPhone());
        //pzFraudRequestVO.setBirthDate(genericAddressDetailsVO.getBirthdate());

        if(functions.isValueNull(genericAddressDetailsVO.getCardHolderIpAddress()))
        {
           pzFraudRequestVO.setIpaddrs(genericAddressDetailsVO.getCardHolderIpAddress()); //set must be an customer ip address
        }
        else
        {
           pzFraudRequestVO.setIpaddrs(genericAddressDetailsVO.getIp()); //set must be an merchant ip asd customer ip address
        }
        //pzFraudRequestVO.setDeviceid(); Optional
        //Setting the Card Details information card limits
        pzFraudRequestVO.setFirstsix(Functions.getFirstSix(genericCardDetailsVO.getCardNum()));
        pzFraudRequestVO.setLastfour(Functions.getLastFour(genericCardDetailsVO.getCardNum()));
        pzFraudRequestVO.setDailycardminlimit(String.valueOf(gatewayAccount.getDailyCardLimit()));
        pzFraudRequestVO.setDailycardlimit(String.valueOf(gatewayAccount.getDailyCardLimit()));
        pzFraudRequestVO.setWeeklycardlimit(String.valueOf(gatewayAccount.getWeeklyCardLimit()));
        pzFraudRequestVO.setMonthlycardlimit(String.valueOf(gatewayAccount.getMonthlyCardLimit()));
        pzFraudRequestVO.setAccountid(String.valueOf(gatewayAccount.getAccountId()));
        pzFraudRequestVO.setBinCountry(genericCardDetailsVO.getCountry_code_A2());

        //Set The Payment Type
        pzFraudRequestVO.setPaymenttype(String.valueOf("CC"));
        return pzFraudRequestVO;
    }

    /*
    *  Make Online Fraud Checking Using Payment Fraud Processor
    *  This method is added for getting required parameter  for fraud checking
    *  Method call is done from CommonPaymentProcess transactionAPI();
    * */
    public CommonValidatorVO getCommonValidatorVO(Hashtable<String,String> transactionDetails)
    {
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();

        genericTransDetailsVO.setOrderId(getValue("description",transactionDetails));
        genericTransDetailsVO.setAmount(getValue("amount", transactionDetails));

        genericAddressDetailsVO.setFirstname(getValue("firstname", transactionDetails));
        genericAddressDetailsVO.setLastname(getValue("lastname", transactionDetails));
        genericAddressDetailsVO.setEmail(getValue("email", transactionDetails));
        genericAddressDetailsVO.setZipCode(getValue("zip", transactionDetails));
        genericAddressDetailsVO.setStreet(getValue("street", transactionDetails));
        genericAddressDetailsVO.setCity(getValue("city", transactionDetails));
        genericAddressDetailsVO.setCountry(getValue("TMPL_COUNTRY", transactionDetails));
        genericAddressDetailsVO.setState(getValue("state", transactionDetails));
        genericAddressDetailsVO.setPhone(getValue("telno", transactionDetails));
        genericAddressDetailsVO.setBirthdate(getValue("birthdate", transactionDetails));
        genericAddressDetailsVO.setCardHolderIpAddress(getValue("cardholderipaddress", transactionDetails));
        genericAddressDetailsVO.setIp(getValue("ipaddress",transactionDetails));

        genericCardDetailsVO.setCardNum(getValue("ccnum",transactionDetails));
        //commonValidatorVO.getMerchantDetailsVO().setAccountId(getValue("accountid",transactionDetails));
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        return commonValidatorVO;
    }

    public void setFourStopDocVerifyParameter(PZFraudDocVerifyRequestVO requestVO,FourStopRequestVO fourStopRequestVO)
    {
        fourStopRequestVO.setCustomer_registration_id(requestVO.getCustomer_registration_id());
        fourStopRequestVO.setMethod(requestVO.getMethod());
        fourStopRequestVO.setFilePath(requestVO.getFilePath());
        fourStopRequestVO.setFileName(requestVO.getFileName());
        fourStopRequestVO.setFileName2(requestVO.getFileName2());
        fourStopRequestVO.setFileName3(requestVO.getFileName3());
        fourStopRequestVO.setFileName4(requestVO.getFileName4());
    }

    public void readRequestForRestCustomerRegistration(FraudRequestVO fraudRequestVO, PZFraudCustRegRequestVO pzFraudCustRegRequestVO)
    {
        pzFraudCustRegRequestVO.setFirstName(fraudRequestVO.getFirstName());
        pzFraudCustRegRequestVO.setLastName(fraudRequestVO.getLastName());
        pzFraudCustRegRequestVO.setPhone(fraudRequestVO.getPhone());
        pzFraudCustRegRequestVO.setEmailId(fraudRequestVO.getEmailId());
        pzFraudCustRegRequestVO.setCountryCode(fraudRequestVO.getCountryCode());
        pzFraudCustRegRequestVO.setWebsite(fraudRequestVO.getWebsite());
        pzFraudCustRegRequestVO.setCustomerIpAddress(fraudRequestVO.getCustomerIpAddress());
        pzFraudCustRegRequestVO.setCustomerRegDate(fraudRequestVO.getCustomerRegDate());
        pzFraudCustRegRequestVO.setId_type(fraudRequestVO.getId_type());
        pzFraudCustRegRequestVO.setId_value(fraudRequestVO.getId_value());
        pzFraudCustRegRequestVO.setPartnerId(fraudRequestVO.getAuthenticationVO().getPartnerId());
        pzFraudCustRegRequestVO.setCust_request_id(fraudRequestVO.getCust_request_id());
        pzFraudCustRegRequestVO.setBirthDate(fraudRequestVO.getDob());
        pzFraudCustRegRequestVO.setZip(fraudRequestVO.getZip());
        pzFraudCustRegRequestVO.setAddress1(fraudRequestVO.getAddress1());
        pzFraudCustRegRequestVO.setAddress2(fraudRequestVO.getAddress2());
        pzFraudCustRegRequestVO.setProvince(fraudRequestVO.getProvince());
        pzFraudCustRegRequestVO.setCity(fraudRequestVO.getCity());
        pzFraudCustRegRequestVO.setState(fraudRequestVO.getState());

    }

    public void readRequestForRestFraudService(RestFraudRequest restFraudRequest, FraudRequestVO fraudRequestVO)
    {
        fraudRequestVO.setFirstName(restFraudRequest.getFirstName());
        fraudRequestVO.setLastName(restFraudRequest.getLastName());
        fraudRequestVO.setPhone(restFraudRequest.getPhone());
        fraudRequestVO.setEmailId(restFraudRequest.getEmailId());
        fraudRequestVO.setCountryCode(restFraudRequest.getCountryCode());
        fraudRequestVO.setWebsite(restFraudRequest.getWebsite());
        fraudRequestVO.setCustomerIpAddress(restFraudRequest.getCustomerIpAddress());
        fraudRequestVO.setCustomerRegDate(restFraudRequest.getCustomerRegDate());
        fraudRequestVO.setId_type(restFraudRequest.getId_type());
        fraudRequestVO.setId_value(restFraudRequest.getId_value());
        fraudRequestVO.setCustomer_registration_id(restFraudRequest.getCustomer_registration_id());
        fraudRequestVO.setMethod(restFraudRequest.getMethod());
        fraudRequestVO.setFileName(restFraudRequest.getFileName());
        fraudRequestVO.setFileName2(restFraudRequest.getFileName2());
        fraudRequestVO.setFileName3(restFraudRequest.getFileName3());
        fraudRequestVO.setFileName4(restFraudRequest.getFileName4());
        fraudRequestVO.setFilePath(restFraudRequest.getFilePath());
        fraudRequestVO.setCust_request_id(restFraudRequest.getCust_request_id());
        fraudRequestVO.setCustomerRegId(restFraudRequest.getCustomerRegId());
        //fraudRequestVO.getAuthenticationVO().setPartnerId(restFraudRequest.getAuthentication().getPartnerId());
        fraudRequestVO.setDob(restFraudRequest.getDob());
        fraudRequestVO.setZip(restFraudRequest.getZip());
        fraudRequestVO.setAddress1(restFraudRequest.getAddress1());
        fraudRequestVO.setAddress2(restFraudRequest.getAddress2());
        fraudRequestVO.setCity(restFraudRequest.getCity());
        fraudRequestVO.setProvince(restFraudRequest.getProvince());
        fraudRequestVO.setState(restFraudRequest.getState());
        AuthenticationVO authenticationVO=new AuthenticationVO();
        authenticationVO.setPartnerId(restFraudRequest.getAuthentication().getPartnerId());
        fraudRequestVO.setAuthenticationVO(authenticationVO);

    }

    public void readRequestForRestDocVerify(FraudRequestVO fraudRequestVO, PZFraudDocVerifyRequestVO pzFraudDocVerifyRequestVO)
    {
        pzFraudDocVerifyRequestVO.setCustomer_registration_id(fraudRequestVO.getCustomerRegId());
        pzFraudDocVerifyRequestVO.setMethod(fraudRequestVO.getMethod());
        pzFraudDocVerifyRequestVO.setFilePath(fraudRequestVO.getFilePath());
        pzFraudDocVerifyRequestVO.setFileName(fraudRequestVO.getFileName());
        pzFraudDocVerifyRequestVO.setFileName2(fraudRequestVO.getFileName2());
        pzFraudDocVerifyRequestVO.setFileName3(fraudRequestVO.getFileName3());
        pzFraudDocVerifyRequestVO.setFileName4(fraudRequestVO.getFileName4());
        pzFraudDocVerifyRequestVO.setPartnerId(fraudRequestVO.getAuthenticationVO().getPartnerId());
        pzFraudDocVerifyRequestVO.setNotificationUrl(fraudRequestVO.getNotificationUrl());

    }

    public FraudRequestVO getAuthTokenRequest(RestFraudRequest authRequest)
    {
        FraudRequestVO fraudRequestVO = new FraudRequestVO();

        AuthenticationVO authenticationVO=new AuthenticationVO();
        PartnerVO partnerVO=new PartnerVO();

        authenticationVO.setPartnerId(authRequest.getAuthentication().getPartnerId());
        authenticationVO.setsKey(authRequest.getAuthentication().getsKey());
        partnerVO.setLoginName(authRequest.getPartner().getLoginName());

        fraudRequestVO.setAuthenticationVO(authenticationVO);
        fraudRequestVO.setPartnerVO(partnerVO);

       // fraudRequestVO.setPartnerVO(getMerchantVO(authRequest.getPartner()));
        //fraudRequestVO.setAuthenticationVO(getAuthenticationVO(authRequest.getAuthentication()));

        return fraudRequestVO;
    }

    public FraudRequestVO getNewAuthTokenRequest(RestFraudRequest request)
    {
        FraudRequestVO fraudRequestVO = new FraudRequestVO();
        AuthenticationVO authenticationVO = new AuthenticationVO();
        fraudRequestVO.setAuthToken(request.getAuthToken());
        authenticationVO.setPartnerId(request.getAuthentication().getPartnerId());
        fraudRequestVO.setAuthenticationVO(authenticationVO);

        return fraudRequestVO;
    }


    /*public CommonValidatorVO readRequstForPartnerLogin(FraudRequestVO fraudRequestVO )
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setAuthToken(fraudRequestVO.getAuthToken());
        commonValidatorVO.setParetnerId(fraudRequestVO.getAuthenticationVO().getPartnerId());

        return commonValidatorVO;
    }*/

    private AuthenticationVO getAuthenticationVO(Authentication authentication)
    {
        AuthenticationVO authenticationVO=new AuthenticationVO();
        authenticationVO.setPartnerId(authentication.getPartnerId());
        authenticationVO.setChecksum(authentication.getChecksum());
        authenticationVO.setPassword(authentication.getPassword());

        return authenticationVO;
    }

    private PartnerVO getMerchantVO(Partner partner)
    {
        PartnerVO partnerVO= new PartnerVO();
        if (partner.getLoginName()!=null)
            partnerVO.setLoginName(partner.getLoginName());
        if (partner.getNewPassword()!=null)
            partnerVO.setNewPassword(partner.getNewPassword());
        if (partner.getEmail()!=null)
            partnerVO.setEmail(partner.getEmail());

        return partnerVO;
    }

    public CommonValidatorVO readRequstForPartnerLogin(FraudRequestVO fraudRequestVO )
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO   = new PartnerDetailsVO();

        merchantDetailsVO.setLogin(fraudRequestVO.getPartnerVO().getLoginName());
        merchantDetailsVO.setPassword(fraudRequestVO.getPartnerVO().getNewPassword());
        //merchantDetailsVO.setKey(fraudRequestVO.getPartnerVO().getKey());
        merchantDetailsVO.setKey(fraudRequestVO.getAuthenticationVO().getsKey());

        commonValidatorVO.setParetnerId(fraudRequestVO.getAuthenticationVO().getPartnerId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        commonValidatorVO.setPartnerName(fraudRequestVO.getPartnerVO().getLoginName());
        partnerDetailsVO.setPartnerKey(fraudRequestVO.getAuthenticationVO().getsKey());
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;

    }

    public CommonValidatorVO readRequestForgetNewAuthToken(FraudRequestVO requestVO)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setAuthToken(requestVO.getAuthToken());
        commonValidatorVO.setParetnerId(requestVO.getAuthenticationVO().getPartnerId());

        return commonValidatorVO;
    }


    public void readRequestForRestNewFraudService(RestFraudRequest restFraudRequest, FraudRequestVO fraudRequestVO)
    {
        fraudRequestVO.setMemberid(restFraudRequest.getMemberid());
        fraudRequestVO.setTrackingId(restFraudRequest.getTrackingId());
        fraudRequestVO.setDescription(restFraudRequest.getDescription());
        fraudRequestVO.setAmount(restFraudRequest.getAmount());
        fraudRequestVO.setStatus(restFraudRequest.getStatus());
        fraudRequestVO.setTime(restFraudRequest.getTime());
        fraudRequestVO.setAccountId(restFraudRequest.getAccountId());
        AuthenticationVO authenticationVO=new AuthenticationVO();
        authenticationVO.setPartnerId(restFraudRequest.getAuthentication().getPartnerId());
        fraudRequestVO.setAuthenticationVO(authenticationVO);
        fraudRequestVO.setCurrency(restFraudRequest.getCurrency());
        fraudRequestVO.setFirstName(restFraudRequest.getFirstName());
        fraudRequestVO.setLastName(restFraudRequest.getLastName());
        fraudRequestVO.setEmailId(restFraudRequest.getEmailId());
        fraudRequestVO.setCountryCode(restFraudRequest.getCountryCode());
        fraudRequestVO.setAddress1(restFraudRequest.getAddress1());
        fraudRequestVO.setZip(restFraudRequest.getZip());
        fraudRequestVO.setPhone(restFraudRequest.getPhone());
        fraudRequestVO.setCustomerIpAddress(restFraudRequest.getCustomerIpAddress());
        fraudRequestVO.setDeposit_limits_dl_daily(restFraudRequest.getDeposit_limits_dl_daily());
        fraudRequestVO.setDeposit_limits_dl_weekly(restFraudRequest.getDeposit_limits_dl_weekly());
        fraudRequestVO.setDeposit_limits_dl_monthly(restFraudRequest.getDeposit_limits_dl_monthly());
        fraudRequestVO.setDeposit_limits_dl_min(restFraudRequest.getDeposit_limits_dl_min());
        fraudRequestVO.setWebsite(restFraudRequest.getWebsite());
        fraudRequestVO.setFirstName(restFraudRequest.getFirstName());
        fraudRequestVO.setLastName(restFraudRequest.getLastName());
        fraudRequestVO.setUser_name(restFraudRequest.getUser_name());
        fraudRequestVO.setUser_number(restFraudRequest.getUser_number());
        fraudRequestVO.setCity(restFraudRequest.getCity());
        fraudRequestVO.setState(restFraudRequest.getState());
        fraudRequestVO.setFirstsix(restFraudRequest.getFirstsix());
        fraudRequestVO.setPayment_method_last_digits(restFraudRequest.getPayment_method_last_digits());
        fraudRequestVO.setPaymenttype(restFraudRequest.getPaymenttype());
        fraudRequestVO.setCustomer_country(restFraudRequest.getCustomer_country());
        fraudRequestVO.setStreet(restFraudRequest.getStreet());
    }

    public void readRequestForRestNewTransaction(FraudRequestVO fraudRequestVO, PZFraudRequestVO pzFraudRequestVO)
    {
        pzFraudRequestVO.setMemberid(fraudRequestVO.getMemberid());
        pzFraudRequestVO.setTrackingid(fraudRequestVO.getTrackingId());
        pzFraudRequestVO.setDescription(fraudRequestVO.getDescription());
        pzFraudRequestVO.setAmount(fraudRequestVO.getAmount());
        pzFraudRequestVO.setStatus(fraudRequestVO.getStatus());
        pzFraudRequestVO.setTime(fraudRequestVO.getTime());
        pzFraudRequestVO.setAccountid(fraudRequestVO.getAccountId());
        pzFraudRequestVO.setDailycardlimit(fraudRequestVO.getDeposit_limits_dl_daily());
        pzFraudRequestVO.setDailycardminlimit(fraudRequestVO.getDeposit_limits_dl_min());
        pzFraudRequestVO.setMonthlycardlimit(fraudRequestVO.getDeposit_limits_dl_monthly());
        pzFraudRequestVO.setWeeklycardlimit(fraudRequestVO.getDeposit_limits_dl_weekly());
        pzFraudRequestVO.setEmailaddr(fraudRequestVO.getEmailId());
        pzFraudRequestVO.setZip(fraudRequestVO.getZip());
        pzFraudRequestVO.setFirstname(fraudRequestVO.getFirstName());
        pzFraudRequestVO.setLastname(fraudRequestVO.getLastName());
        pzFraudRequestVO.setPhone(fraudRequestVO.getPhone());
        pzFraudRequestVO.setCity(fraudRequestVO.getCity());
        pzFraudRequestVO.setCurrency(fraudRequestVO.getCurrency());
        pzFraudRequestVO.setIpaddrs(fraudRequestVO.getCustomerIpAddress());
        pzFraudRequestVO.setLastfour(fraudRequestVO.getPayment_method_last_digits());
        pzFraudRequestVO.setState(fraudRequestVO.getState());
        pzFraudRequestVO.setPartnerid(fraudRequestVO.getAuthenticationVO().getPartnerId());
        pzFraudRequestVO.setPaymenttype(fraudRequestVO.getPaymenttype());
        pzFraudRequestVO.setStreet(fraudRequestVO.getStreet());
        pzFraudRequestVO.setCountrycode(fraudRequestVO.getCountryCode());
        pzFraudRequestVO.setFirstsix(fraudRequestVO.getFirstsix());
        pzFraudRequestVO.setUsername(fraudRequestVO.getUser_name());
        pzFraudRequestVO.setUsernumber(fraudRequestVO.getUser_number());
        pzFraudRequestVO.setIp(fraudRequestVO.getIp());
        pzFraudRequestVO.setCountry(fraudRequestVO.getCustomer_country());
        pzFraudRequestVO.setAddress1(fraudRequestVO.getAddress1());
        pzFraudRequestVO.setWebsite(fraudRequestVO.getWebsite());

        /*fraudRequestVO.setIp(pzFraudRequestVO.getIpaddrs());
        pzFraudRequestVO.setBillingemail(fraudRequestVO.getBillingemail());
        pzFraudRequestVO.setBillingcountry(fraudRequestVO.getBillingcountry());
        pzFraudRequestVO.setBillingcity(fraudRequestVO.getBillingcity());
        pzFraudRequestVO.setBillingaddress1(fraudRequestVO.getBillingaddress1());
        pzFraudRequestVO.setBillingpostalcode(fraudRequestVO.getBillingpostalcode());
        pzFraudRequestVO.setBillingfirstname(fraudRequestVO.getBillingfirstname());
        pzFraudRequestVO.setBillingphone1(fraudRequestVO.getBillingphone1());
        pzFraudRequestVO.setBillingprovince(fraudRequestVO.getBillingprovince());
        pzFraudRequestVO.setBillinglastname(fraudRequestVO.getBillinglastname());*/

        // return fraudRequestVO;

    }
}

