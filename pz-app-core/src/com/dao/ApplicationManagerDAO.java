package com.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.enums.AppFileActionType;
import com.enums.ApplicationManagerTypes;
import com.enums.BankApplicationStatus;
import com.enums.ConsolidatedAppStatus;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.vo.applicationManagerVOs.*;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.map.HashedMap;

import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;

//import com.manager.vo.gatewayVOs.GatewayTypeVO;
/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/16/15
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationManagerDAO
{
 private static Logger logger = new Logger(ApplicationManagerDAO.class.getName());
 Functions functions = new Functions();

 public static List<String> loadPartnerId()
 {
  List<String> partnerIdList = new ArrayList<String>();
  Connection conn=null;
  try
  {
   conn = Database.getConnection();
   String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
   PreparedStatement pstmt = conn.prepareStatement(query);
   ResultSet rs = pstmt.executeQuery();
   while (rs.next())
   {
    String partnerName = rs.getString("partnerName");
    String partnerId = rs.getString("partnerId");
    partnerIdList.add(partnerId +" - "+ partnerName);
   }
  }
  catch(Exception e)
  {
   logger.error("Exception while loading paymodeids", e);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return partnerIdList;
 }

 public void populateApplicationVO(ApplicationManagerVO applicationManagerVO)
 {
  Connection con = null;
  PreparedStatement psPopulateApplicationData = null;
  ResultSet rsPopulateApplicationData = null;
  try
  {
   logger.debug("Populating application data---->");
   con = Database.getConnection();
   String query = "SELECT * FROM application_manager as AM left Join " +
           "`companyprofile` AS ACO on AM.application_id = ACO.application_id left JOIN " +
           "`ownershipprofile` AS AO ON AM.application_id=AO.application_id left JOIN " +
           "`applicationmanager_businessprofile` AS ABU ON AM.application_id=ABU.application_id left JOIN " +
           "`applicationmanager_bankprofile` AS ABA ON AM.application_id=ABA.application_id left JOIN" +
           "`applicationmanager_currencywisebankinfo` AS ACB ON AM.application_id=ACB.application_id left JOIN" +
           "`applicationmanager_processinghistory` AS APH ON AM.application_id=APH.application_id left JOIN" +
           " `applicationmanager_cardholderprofile` AS ACA ON AM.application_id=ACA.application_id left JOIN" +
           " `applicationmanager_extradetailsprofile` AS EDP ON AM.application_id=EDP.application_id " +
           " WHERE AM.member_id=?";

   psPopulateApplicationData = con.prepareStatement(query.toString());
   psPopulateApplicationData.setString(1, applicationManagerVO.getMemberId());
   rsPopulateApplicationData = psPopulateApplicationData.executeQuery();
   if (rsPopulateApplicationData.next())
   {
    CompanyProfileVO companyProfileVO = new CompanyProfileVO();
    OwnershipProfileVO ownershipProfileVO = new OwnershipProfileVO();
    BusinessProfileVO businessProfileVO = new BusinessProfileVO();
    BankProfileVO bankProfileVO = new BankProfileVO();
    CardholderProfileVO cardholderProfileVO = new CardholderProfileVO();
    ExtraDetailsProfileVO extraDetailsProfileVO = new ExtraDetailsProfileVO();

    applicationManagerVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    applicationManagerVO.setApplicationSaved(rsPopulateApplicationData.getString("isapplicationsaved"));
    applicationManagerVO.setStatus(rsPopulateApplicationData.getString("status"));
    applicationManagerVO.setMaf_Status(rsPopulateApplicationData.getString("MAF_status"));
    applicationManagerVO.setKyc_Status(rsPopulateApplicationData.getString("KYC_status"));
    applicationManagerVO.setSpeed_status(rsPopulateApplicationData.getString("speed_status"));
    applicationManagerVO.setAppliedToModify(rsPopulateApplicationData.getString("appliedToModify"));
    applicationManagerVO.setStandby_user(rsPopulateApplicationData.getString("maf_user"));
    applicationManagerVO.setUser(rsPopulateApplicationData.getString("maf_user"));
    applicationManagerVO.setSpeed_user(rsPopulateApplicationData.getString("speed_user"));

    //Set value for CompanyProfile
    companyProfileVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    companyProfileVO.setCompanyBankruptcy(rsPopulateApplicationData.getString("company_bankruptcy"));
    companyProfileVO.setCompanyBankruptcydate(rsPopulateApplicationData.getString("company_bankruptcydate"));
    companyProfileVO.setCompanyTypeOfBusiness(rsPopulateApplicationData.getString("company_typeofbusiness"));
    companyProfileVO.setCompanyLengthOfTimeInBusiness(rsPopulateApplicationData.getString("company_lengthoftime_business"));
    companyProfileVO.setCompanyCapitalResources(rsPopulateApplicationData.getString("company_capitalresources"));
    companyProfileVO.setCompany_currencylastyear(rsPopulateApplicationData.getString("company_currencylastyear"));
    companyProfileVO.setCompanyTurnoverLastYear(rsPopulateApplicationData.getString("company_turnoverlastyear"));
    companyProfileVO.setCompany_turnoverlastyear_unit(rsPopulateApplicationData.getString("company_turnoverlastyear_unit"));
    companyProfileVO.setCompanyNumberOfEmployees(rsPopulateApplicationData.getString("company_numberofemployees"));
    companyProfileVO.setLicense_required(rsPopulateApplicationData.getString("License_required"));
    companyProfileVO.setLicense_Permission(rsPopulateApplicationData.getString("License_Permission"));
    companyProfileVO.setLegalProceeding(rsPopulateApplicationData.getString("legal_proceeding"));
    companyProfileVO.setCompanyProfileSaved(rsPopulateApplicationData.getString("iscompanyprofilesaved"));
    companyProfileVO.setStartup_business(rsPopulateApplicationData.getString("startup_business"));
    companyProfileVO.setIscompany_insured(rsPopulateApplicationData.getString("iscompany_insured"));
    companyProfileVO.setInsured_companyname(rsPopulateApplicationData.getString("insured_companyname"));
    companyProfileVO.setMain_business_partner(rsPopulateApplicationData.getString("main_business_partner"));
    companyProfileVO.setLoans(rsPopulateApplicationData.getString("loans"));
    companyProfileVO.setIncome_economic_activity(rsPopulateApplicationData.getString("income_economic_activity"));
    companyProfileVO.setInterest_income(rsPopulateApplicationData.getString("interest_income"));
    companyProfileVO.setInvestments(rsPopulateApplicationData.getString("investments"));
    companyProfileVO.setIncome_sources_other(rsPopulateApplicationData.getString("income_sources_other"));
    companyProfileVO.setIncome_sources_other_yes(rsPopulateApplicationData.getString("income_sources_other_yes"));
    companyProfileVO.setInsured_amount(rsPopulateApplicationData.getString("insured_amount"));
    companyProfileVO.setInsured_currency(rsPopulateApplicationData.getString("insured_currency"));
    companyProfileVO.setCountryOfRegistration(rsPopulateApplicationData.getString("countryofregistration"));
    companyProfileVO.setCompanyProfile_addressVOMap(populateCompanyDetails(applicationManagerVO.getApplicationId()));
    companyProfileVO.setCompanyProfile_contactInfoVOMap(populateContactInformation(applicationManagerVO.getApplicationId()));

    //Set value for OwnershipProfile
    ownershipProfileVO.setApplicationid(rsPopulateApplicationData.getString("application_id"));
    ownershipProfileVO.setNumOfShareholders(rsPopulateApplicationData.getString("numOfShareholders"));
    ownershipProfileVO.setNumOfCorporateShareholders(rsPopulateApplicationData.getString("numOfCorporateShareholders"));
    ownershipProfileVO.setNumOfDirectors(rsPopulateApplicationData.getString("numOfDirectors"));
    ownershipProfileVO.setNumOfAuthrisedSignatory(rsPopulateApplicationData.getString("numOfAuthrisedSignatory"));
    ownershipProfileVO.setOwnerShipProfileSaved(rsPopulateApplicationData.getString("isownershipprofilesaved"));
    ownershipProfileVO.setOwnershipProfileDetailsVOMap(populateOwnershipProfileDetails(applicationManagerVO.getApplicationId()));

    //Set value for BusinessProfile
    businessProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    businessProfileVO.setForeigntransactions_us(rsPopulateApplicationData.getString("foreigntransactions_us"));
    businessProfileVO.setForeigntransactions_Europe(rsPopulateApplicationData.getString("foreigntransactions_Europe"));
    businessProfileVO.setForeigntransactions_Asia(rsPopulateApplicationData.getString("foreigntransactions_Asia"));
    businessProfileVO.setForeigntransactions_cis(rsPopulateApplicationData.getString("foreigntransactions_cis"));
    businessProfileVO.setForeigntransactions_canada(rsPopulateApplicationData.getString("foreigntransactions_canada"));
    businessProfileVO.setForeigntransactions_uk(rsPopulateApplicationData.getString("foreigntransactions_uk"));
    businessProfileVO.setForeigntransactions_RestoftheWorld(rsPopulateApplicationData.getString("foreigntransactions_RestoftheWorld"));
    businessProfileVO.setMethodofacceptance_moto(rsPopulateApplicationData.getString("methodofacceptance_moto"));
    businessProfileVO.setMethodofacceptance_internet(rsPopulateApplicationData.getString("methodofacceptance_internet"));
    businessProfileVO.setMethodofacceptance_swipe(rsPopulateApplicationData.getString("methodofacceptance_swipe"));
    businessProfileVO.setAverageticket(rsPopulateApplicationData.getString("averageticket"));
    businessProfileVO.setHighestticket(rsPopulateApplicationData.getString("highestticket"));
    businessProfileVO.setUrls(rsPopulateApplicationData.getString("urls"));
    businessProfileVO.setDescriptor(rsPopulateApplicationData.getString("descriptor_creditcardstmt"));
    businessProfileVO.setDescriptionofproducts(rsPopulateApplicationData.getString("descriptionofproducts"));
    //businessProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));
    businessProfileVO.setRecurringservices(rsPopulateApplicationData.getString("recurringservices"));
    businessProfileVO.setRecurringservicesyes(rsPopulateApplicationData.getString("recurringservicesyes"));
    businessProfileVO.setIsacallcenterusedyes(rsPopulateApplicationData.getString("isacallcenterusedyes"));
    businessProfileVO.setIsafulfillmenthouseused(rsPopulateApplicationData.getString("isafulfillmenthouseused"));
    businessProfileVO.setIsafulfillmenthouseused_yes(rsPopulateApplicationData.getString("isafulfillmenthouseused_yes"));
    businessProfileVO.setCardtypesaccepted_visa(rsPopulateApplicationData.getString("cardtypesaccepted_visa"));
    businessProfileVO.setCardtypesaccepted_mastercard(rsPopulateApplicationData.getString("cardtypesaccepted_mastercard"));
    businessProfileVO.setCardtypesaccepted_americanexpress(rsPopulateApplicationData.getString("cardtypesaccepted_americanexpress"));
    businessProfileVO.setCardtypesaccepted_discover(rsPopulateApplicationData.getString("cardtypesaccepted_discover"));
    businessProfileVO.setCardtypesaccepted_diners(rsPopulateApplicationData.getString("cardtypesaccepted_diners"));
    businessProfileVO.setCardtypesaccepted_jcb(rsPopulateApplicationData.getString("cardtypesaccepted_jcb"));
    businessProfileVO.setCardtypesaccepted_rupay(rsPopulateApplicationData.getString("cardtypesaccepted_rupay"));
    businessProfileVO.setCardtypesaccepted_other(rsPopulateApplicationData.getString("cardtypesaccepted_other"));
    businessProfileVO.setCardtypesaccepted_other_yes(rsPopulateApplicationData.getString("cardtypesaccepted_other_yes"));
    businessProfileVO.setBusinessProfileSaved(rsPopulateApplicationData.getString("isbuisnessprofilesaved"));

    businessProfileVO.setKyc_processes(rsPopulateApplicationData.getString("kyc_processes"));
    businessProfileVO.setVisa_cardlogos(rsPopulateApplicationData.getString("visa_cardlogos"));
    businessProfileVO.setMaster_cardlogos(rsPopulateApplicationData.getString("master_cardlogos"));
    businessProfileVO.setThreeD_secure_compulsory(rsPopulateApplicationData.getString("threeD_secure_compulsory"));
    businessProfileVO.setPrice_displayed(rsPopulateApplicationData.getString("price_displayed"));
    businessProfileVO.setTransaction_currency(rsPopulateApplicationData.getString("transaction_currency"));
    businessProfileVO.setCardholder_asked(rsPopulateApplicationData.getString("cardholder_asked"));
    businessProfileVO.setDynamic_descriptors(rsPopulateApplicationData.getString("dynamic_descriptors"));
    businessProfileVO.setShopping_cart(rsPopulateApplicationData.getString("shopping_cart"));
    businessProfileVO.setShopping_cart_details(rsPopulateApplicationData.getString("shopping_cart_details"));
    businessProfileVO.setPricing_policies_website(rsPopulateApplicationData.getString("pricing_policies_website"));
    businessProfileVO.setPricing_policies_website_yes(rsPopulateApplicationData.getString("pricing_policies_website_yes"));
    businessProfileVO.setFulfillment_timeframe(rsPopulateApplicationData.getString("fulfillment_timeframe"));
    businessProfileVO.setGoods_policy(rsPopulateApplicationData.getString("goods_policy"));
    businessProfileVO.setMCC_Ctegory(rsPopulateApplicationData.getString("MCC_Ctegory"));
    businessProfileVO.setMerchantCode(rsPopulateApplicationData.getString("merchantcode"));
    businessProfileVO.setCountries_blocked(rsPopulateApplicationData.getString("countries_blocked"));
    businessProfileVO.setCountries_blocked_details(rsPopulateApplicationData.getString("countries_blocked_details"));
    businessProfileVO.setCustomer_support(rsPopulateApplicationData.getString("customer_support"));

    businessProfileVO.setAffiliate_programs(rsPopulateApplicationData.getString("affiliate_programs"));
    businessProfileVO.setAffiliate_programs_details(rsPopulateApplicationData.getString("affiliate_programs_details"));
    businessProfileVO.setListfraudtools(rsPopulateApplicationData.getString("listfraudtools"));
    businessProfileVO.setListfraudtools_yes(rsPopulateApplicationData.getString("listfraudtools_yes"));
    businessProfileVO.setCustomers_identification(rsPopulateApplicationData.getString("customers_identification"));
    businessProfileVO.setCustomers_identification_yes(rsPopulateApplicationData.getString("customers_identification_yes"));
    businessProfileVO.setCoolingoffperiod(rsPopulateApplicationData.getString("coolingoffperiod"));
    businessProfileVO.setCustomersupport_email(rsPopulateApplicationData.getString("customersupport_email"));
    businessProfileVO.setCustsupportwork_hours(rsPopulateApplicationData.getString("custsupportwork_hours"));
    businessProfileVO.setTechnical_contact(rsPopulateApplicationData.getString("technical_contact"));

    businessProfileVO.setSecuritypolicy(rsPopulateApplicationData.getString("securitypolicy"));
    businessProfileVO.setConfidentialitypolicy(rsPopulateApplicationData.getString("confidentialitypolicy"));
    businessProfileVO.setApplicablejurisdictions(rsPopulateApplicationData.getString("applicablejurisdictions"));
    businessProfileVO.setPrivacy_anonymity_dataprotection(rsPopulateApplicationData.getString("privacy_anonymity_dataprotection"));

    businessProfileVO.setApp_Services(rsPopulateApplicationData.getString("App_Services"));
    businessProfileVO.setAgency_employed(rsPopulateApplicationData.getString("agency_employed"));
    businessProfileVO.setAgency_employed_yes(rsPopulateApplicationData.getString("agency_employed_yes"));
    businessProfileVO.setProduct_requires(rsPopulateApplicationData.getString("product_requires"));
    //ADD new
    businessProfileVO.setLowestticket(rsPopulateApplicationData.getString("lowestticket"));

    //add NEW
    businessProfileVO.setTimeframe(rsPopulateApplicationData.getString("timeframe"));
    businessProfileVO.setLivechat(rsPopulateApplicationData.getString("livechat"));

    //Add new
    businessProfileVO.setLoginId(rsPopulateApplicationData.getString("login_id"));
    businessProfileVO.setPassWord(rsPopulateApplicationData.getString("password"));
    businessProfileVO.setIs_website_live(rsPopulateApplicationData.getString("is_website_live"));
    businessProfileVO.setTest_link(rsPopulateApplicationData.getString("test_link"));
    businessProfileVO.setCompanyIdentifiable(rsPopulateApplicationData.getString("companyidentifiable"));
    businessProfileVO.setClearlyPresented(rsPopulateApplicationData.getString("clearlypresented"));
    businessProfileVO.setTrackingNumber(rsPopulateApplicationData.getString("trackingnumber"));
    businessProfileVO.setDomainsOwned(rsPopulateApplicationData.getString("domainsowned"));
    businessProfileVO.setDomainsOwned_no(rsPopulateApplicationData.getString("domainsowned_no"));
    businessProfileVO.setSslSecured(rsPopulateApplicationData.getString("sslsecured"));
    businessProfileVO.setCopyright(rsPopulateApplicationData.getString("copyright"));
    businessProfileVO.setSourceContent(rsPopulateApplicationData.getString("sourcecontent"));
    businessProfileVO.setDirectMail(rsPopulateApplicationData.getString("directmail"));
    businessProfileVO.setYellowPages(rsPopulateApplicationData.getString("Yellowpages"));
    businessProfileVO.setRadioTv(rsPopulateApplicationData.getString("radiotv"));
    businessProfileVO.setInternet(rsPopulateApplicationData.getString("internet"));
    businessProfileVO.setNetworking(rsPopulateApplicationData.getString("networking"));
    businessProfileVO.setOutboundTelemarketing(rsPopulateApplicationData.getString("outboundtelemarketing"));
    businessProfileVO.setInHouseLocation(rsPopulateApplicationData.getString("inhouselocation"));
    businessProfileVO.setContactPerson(rsPopulateApplicationData.getString("contactperson"));
    businessProfileVO.setShippingContactemail(rsPopulateApplicationData.getString("shipping_contactemail"));
    businessProfileVO.setOtherLocation(rsPopulateApplicationData.getString("otherlocation"));
    businessProfileVO.setMainSuppliers(rsPopulateApplicationData.getString("mainsuppliers"));
    businessProfileVO.setShipmentAssured(rsPopulateApplicationData.getString("shipmentassured"));
    businessProfileVO.setBillingModel(rsPopulateApplicationData.getString("billing_model"));
    businessProfileVO.setBillingTimeFrame(rsPopulateApplicationData.getString("billing_timeframe"));
    businessProfileVO.setRecurringAmount(rsPopulateApplicationData.getString("recurring_amount"));
    businessProfileVO.setAutomaticRecurring(rsPopulateApplicationData.getString("automatic_recurring"));
    businessProfileVO.setMultipleMembership(rsPopulateApplicationData.getString("multiple_membership"));
    businessProfileVO.setFreeMembership(rsPopulateApplicationData.getString("free_membership"));
    businessProfileVO.setCreditCardRequired(rsPopulateApplicationData.getString("creditcard_Required"));
    businessProfileVO.setAutomaticallyBilled(rsPopulateApplicationData.getString("automatically_billed"));
    businessProfileVO.setPreAuthorization(rsPopulateApplicationData.getString("pre_authorization"));
    businessProfileVO.setIpaddress(rsPopulateApplicationData.getString("ipaddress"));

    // Wirecard requirement added in Business Profile
    businessProfileVO.setShopsystem_plugin(rsPopulateApplicationData.getString("shopsystem_plugin"));
    businessProfileVO.setDirect_debit_sepa(rsPopulateApplicationData.getString("direct_debit_sepa"));
    businessProfileVO.setAlternative_payments(rsPopulateApplicationData.getString("alternative_payments"));
    businessProfileVO.setRisk_management(rsPopulateApplicationData.getString("risk_management"));
    businessProfileVO.setPayment_engine(rsPopulateApplicationData.getString("payment_engine"));
    businessProfileVO.setWebhost_company_name(rsPopulateApplicationData.getString("webhost_company_name"));
    businessProfileVO.setWebhost_phone(rsPopulateApplicationData.getString("webhost_phone"));
    businessProfileVO.setWebhost_email(rsPopulateApplicationData.getString("webhost_email"));
    businessProfileVO.setWebhost_website(rsPopulateApplicationData.getString("webhost_website"));
    businessProfileVO.setWebhost_address(rsPopulateApplicationData.getString("webhost_address"));
    businessProfileVO.setPayment_company_name(rsPopulateApplicationData.getString("payment_company_name"));
    businessProfileVO.setPayment_phone(rsPopulateApplicationData.getString("payment_phone"));
    businessProfileVO.setPayment_email(rsPopulateApplicationData.getString("payment_email"));
    businessProfileVO.setPayment_website(rsPopulateApplicationData.getString("payment_website"));
    businessProfileVO.setPayment_address(rsPopulateApplicationData.getString("payment_address"));
    businessProfileVO.setCallcenter_phone(rsPopulateApplicationData.getString("callcenter_phone"));
    businessProfileVO.setCallcenter_email(rsPopulateApplicationData.getString("callcenter_email"));
    businessProfileVO.setCallcenter_website(rsPopulateApplicationData.getString("callcenter_website"));
    businessProfileVO.setCallcenter_address(rsPopulateApplicationData.getString("callcenter_address"));
    businessProfileVO.setShoppingcart_company_name(rsPopulateApplicationData.getString("shoppingcart_company_name"));
    businessProfileVO.setShoppingcart_phone(rsPopulateApplicationData.getString("shoppingcart_phone"));
    businessProfileVO.setShoppingcart_email(rsPopulateApplicationData.getString("shoppingcart_email"));
    businessProfileVO.setShoppingcart_website(rsPopulateApplicationData.getString("shoppingcart_website"));
    businessProfileVO.setShoppingcart_address(rsPopulateApplicationData.getString("shoppingcart_address"));
    businessProfileVO.setSeasonal_fluctuating(rsPopulateApplicationData.getString("seasonal_fluctuating"));
    businessProfileVO.setSeasonal_fluctuating_yes(rsPopulateApplicationData.getString("seasonal_fluctuating_yes"));
    businessProfileVO.setPaymenttype_credit(rsPopulateApplicationData.getString("paymenttype_credit"));
    businessProfileVO.setPaymenttype_debit(rsPopulateApplicationData.getString("paymenttype_debit"));
    businessProfileVO.setPaymenttype_netbanking(rsPopulateApplicationData.getString("paymenttype_netbanking"));
    businessProfileVO.setPaymenttype_wallet(rsPopulateApplicationData.getString("paymenttype_wallet"));
    businessProfileVO.setPaymenttype_alternate(rsPopulateApplicationData.getString("paymenttype_alternate"));
    businessProfileVO.setCreditor_id(rsPopulateApplicationData.getString("creditor_id"));
    businessProfileVO.setPayment_delivery(rsPopulateApplicationData.getString("payment_delivery"));
    businessProfileVO.setPayment_delivery_otheryes(rsPopulateApplicationData.getString("payment_delivery_otheryes"));
    businessProfileVO.setGoods_delivery(rsPopulateApplicationData.getString("goods_delivery"));
    businessProfileVO.setTerminal_type(rsPopulateApplicationData.getString("terminal_type"));
    businessProfileVO.setTerminal_type_otheryes(rsPopulateApplicationData.getString("terminal_type_otheryes"));
    businessProfileVO.setOne_time_percentage(rsPopulateApplicationData.getString("one_time_percentage"));
    businessProfileVO.setMoto_percentage(rsPopulateApplicationData.getString("moto_percentage"));
    businessProfileVO.setInternet_percentage(rsPopulateApplicationData.getString("internet_percentage"));
    businessProfileVO.setSwipe_percentage(rsPopulateApplicationData.getString("swipe_percentage"));
    businessProfileVO.setRecurring_percentage(rsPopulateApplicationData.getString("recurring_percentage"));
    businessProfileVO.setThreedsecure_percentage(rsPopulateApplicationData.getString("threedsecure_percentage"));
    businessProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));

    businessProfileVO.setCardvolume_visa(rsPopulateApplicationData.getString("cardvolume_visa"));
    businessProfileVO.setCardvolume_mastercard(rsPopulateApplicationData.getString("cardvolume_mastercard"));
    businessProfileVO.setCardvolume_americanexpress(rsPopulateApplicationData.getString("cardvolume_americanexpress"));
    businessProfileVO.setCardvolume_dinner(rsPopulateApplicationData.getString("cardvolume_dinner"));
    businessProfileVO.setCardvolume_other(rsPopulateApplicationData.getString("cardvolume_other"));
    businessProfileVO.setCardvolume_discover(rsPopulateApplicationData.getString("cardvolume_discover"));
    businessProfileVO.setCardvolume_rupay(rsPopulateApplicationData.getString("cardvolume_rupay"));
    businessProfileVO.setCardvolume_jcb(rsPopulateApplicationData.getString("cardvolume_jcb"));
    businessProfileVO.setPayment_type_yes(rsPopulateApplicationData.getString("payment_type_yes"));
    businessProfileVO.setOrderconfirmation_post(rsPopulateApplicationData.getString("orderconfirmation_post"));
    businessProfileVO.setOrderconfirmation_email(rsPopulateApplicationData.getString("orderconfirmation_email"));
    businessProfileVO.setOrderconfirmation_sms(rsPopulateApplicationData.getString("orderconfirmation_sms"));
    businessProfileVO.setOrderconfirmation_other(rsPopulateApplicationData.getString("orderconfirmation_other"));
    businessProfileVO.setOrderconfirmation_other_yes(rsPopulateApplicationData.getString("orderconfirmation_other_yes"));
    businessProfileVO.setPhysicalgoods_delivered(rsPopulateApplicationData.getString("physicalgoods_delivered"));
    businessProfileVO.setViainternetgoods_delivered(rsPopulateApplicationData.getString("viainternetgoods_delivered"));

    //Set value for BankProfile
    //Set value for BankProfile
    bankProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    bankProfileVO.setCurrencyrequested_productssold(rsPopulateApplicationData.getString("currencyrequested_productssold"));
    bankProfileVO.setCurrencyrequested_bankaccount(rsPopulateApplicationData.getString("currencyrequested_bankaccount"));
    bankProfileVO.setBankinfo_bic(rsPopulateApplicationData.getString("bankinfo_bic"));
    bankProfileVO.setBankinfo_bank_name(rsPopulateApplicationData.getString("bankinfo_bank_name"));
    bankProfileVO.setBankinfo_bankaddress(rsPopulateApplicationData.getString("bankinfo_bankaddress"));
    bankProfileVO.setBankinfo_bankphonenumber(rsPopulateApplicationData.getString("bankinfo_bankphonenumber"));
    bankProfileVO.setBankinfo_aba_routingcode(rsPopulateApplicationData.getString("bankinfo_aba_routingcode"));
    bankProfileVO.setBankinfo_accountholder(rsPopulateApplicationData.getString("bankinfo_accountholder"));
    bankProfileVO.setBankinfo_accountnumber(rsPopulateApplicationData.getString("bankinfo_accountnumber"));
    bankProfileVO.setBankinfo_IBAN(rsPopulateApplicationData.getString("bankinfo_IBAN"));
    bankProfileVO.setBankinfo_contactperson(rsPopulateApplicationData.getString("bankinfo_contactperson"));
    bankProfileVO.setBankinfo_currency(rsPopulateApplicationData.getString("bankinfo_currency"));
    bankProfileVO.setAquirer(rsPopulateApplicationData.getString("aquirer"));
    bankProfileVO.setReasonaquirer(rsPopulateApplicationData.getString("reason_aquirer"));
    bankProfileVO.setCustomer_trans_data(rsPopulateApplicationData.getString("customer_trans_data"));
    bankProfileVO.setCurrencywisebankinfo_id(rsPopulateApplicationData.getString("currencywisebankinfo_id"));

    bankProfileVO.setIscurrencywisebankinfo(rsPopulateApplicationData.getString("iscurrencywisebankinfo"));
    bankProfileVO.setProcessinghistory_id(rsPopulateApplicationData.getString("processinghistory_id"));

    bankProfileVO.setSalesvolume_lastmonth(rsPopulateApplicationData.getString("salesvolume_lastmonth"));
    bankProfileVO.setSalesvolume_2monthsago(rsPopulateApplicationData.getString("salesvolume_2monthsago"));
    bankProfileVO.setSalesvolume_3monthsago(rsPopulateApplicationData.getString("salesvolume_3monthsago"));
    bankProfileVO.setSalesvolume_4monthsago(rsPopulateApplicationData.getString("salesvolume_4monthsago"));
    bankProfileVO.setSalesvolume_5monthsago(rsPopulateApplicationData.getString("salesvolume_5monthsago"));
    bankProfileVO.setSalesvolume_6monthsago(rsPopulateApplicationData.getString("salesvolume_6monthsago"));
    bankProfileVO.setSalesvolume_12monthsago(rsPopulateApplicationData.getString("salesvolume_12monthsago"));
    bankProfileVO.setSalesvolume_year2(rsPopulateApplicationData.getString("salesvolume_year2"));
    bankProfileVO.setSalesvolume_year3(rsPopulateApplicationData.getString("salesvolume_year3"));
    bankProfileVO.setNumberoftransactions_lastmonth(rsPopulateApplicationData.getString("numberoftransactions_lastmonth"));
    bankProfileVO.setNumberoftransactions_2monthsago(rsPopulateApplicationData.getString("numberoftransactions_2monthsago"));
    bankProfileVO.setNumberoftransactions_3monthsago(rsPopulateApplicationData.getString("numberoftransactions_3monthsago"));
    bankProfileVO.setNumberoftransactions_4monthsago(rsPopulateApplicationData.getString("numberoftransactions_4monthsago"));
    bankProfileVO.setNumberoftransactions_5monthsago(rsPopulateApplicationData.getString("numberoftransactions_5monthsago"));
    bankProfileVO.setNumberoftransactions_6monthsago(rsPopulateApplicationData.getString("numberoftransactions_6monthsago"));
    bankProfileVO.setNumberoftransactions_12monthsago(rsPopulateApplicationData.getString("numberoftransactions_12monthsago"));
    bankProfileVO.setNumberoftransactions_year2(rsPopulateApplicationData.getString("numberoftransactions_year2"));
    bankProfileVO.setNumberoftransactions_year3(rsPopulateApplicationData.getString("numberoftransactions_year3"));
    bankProfileVO.setChargebackvolume_lastmonth(rsPopulateApplicationData.getString("chargebackvolume_lastmonth"));
    bankProfileVO.setChargebackvolume_2monthsago(rsPopulateApplicationData.getString("chargebackvolume_2monthsago"));
    bankProfileVO.setChargebackvolume_3monthsago(rsPopulateApplicationData.getString("chargebackvolume_3monthsago"));
    bankProfileVO.setChargebackvolume_4monthsago(rsPopulateApplicationData.getString("chargebackvolume_4monthsago"));
    bankProfileVO.setChargebackvolume_5monthsago(rsPopulateApplicationData.getString("chargebackvolume_5monthsago"));
    bankProfileVO.setChargebackvolume_6monthsago(rsPopulateApplicationData.getString("chargebackvolume_6monthsago"));
    bankProfileVO.setChargebackvolume_12monthsago(rsPopulateApplicationData.getString("chargebackvolume_12monthsago"));
    bankProfileVO.setChargebackvolume_year2(rsPopulateApplicationData.getString("chargebackvolume_year2"));
    bankProfileVO.setChargebackvolume_year3(rsPopulateApplicationData.getString("chargebackvolume_year3"));
    bankProfileVO.setNumberofchargebacks_lastmonth(rsPopulateApplicationData.getString("numberofchargebacks_lastmonth"));
    bankProfileVO.setNumberofchargebacks_2monthsago(rsPopulateApplicationData.getString("numberofchargebacks_2monthsago"));
    bankProfileVO.setNumberofchargebacks_3monthsago(rsPopulateApplicationData.getString("numberofchargebacks_3monthsago"));
    bankProfileVO.setNumberofchargebacks_4monthsago(rsPopulateApplicationData.getString("numberofchargebacks_4monthsago"));
    bankProfileVO.setNumberofchargebacks_5monthsago(rsPopulateApplicationData.getString("numberofchargebacks_5monthsago"));
    bankProfileVO.setNumberofchargebacks_6monthsago(rsPopulateApplicationData.getString("numberofchargebacks_6monthsago"));
    bankProfileVO.setNumberofchargebacks_12monthsago(rsPopulateApplicationData.getString("numberofchargebacks_12monthsago"));
    bankProfileVO.setNumberofchargebacks_year2(rsPopulateApplicationData.getString("numberofchargebacks_year2"));
    bankProfileVO.setNumberofchargebacks_year3(rsPopulateApplicationData.getString("numberofchargebacks_year3"));
    bankProfileVO.setRefundsvolume_lastmonth(rsPopulateApplicationData.getString("refundsvolume_lastmonth"));
    bankProfileVO.setRefundsvolume_2monthsago(rsPopulateApplicationData.getString("refundsvolume_2monthsago"));
    bankProfileVO.setRefundsvolume_3monthsago(rsPopulateApplicationData.getString("refundsvolume_3monthsago"));
    bankProfileVO.setRefundsvolume_4monthsago(rsPopulateApplicationData.getString("refundsvolume_4monthsago"));
    bankProfileVO.setRefundsvolume_5monthsago(rsPopulateApplicationData.getString("refundsvolume_5monthsago"));
    bankProfileVO.setRefundsvolume_6monthsago(rsPopulateApplicationData.getString("refundsvolume_6monthsago"));
    bankProfileVO.setRefundsvolume_12monthsago(rsPopulateApplicationData.getString("refundsvolume_12monthsago"));
    bankProfileVO.setRefundsvolume_year2(rsPopulateApplicationData.getString("refundsvolume_year2"));
    bankProfileVO.setRefundsvolume_year3(rsPopulateApplicationData.getString("refundsvolume_year3"));
    bankProfileVO.setNumberofrefunds_lastmonth(rsPopulateApplicationData.getString("numberofrefunds_lastmonth"));
    bankProfileVO.setNumberofrefunds_2monthsago(rsPopulateApplicationData.getString("numberofrefunds_2monthsago"));
    bankProfileVO.setNumberofrefunds_3monthsago(rsPopulateApplicationData.getString("numberofrefunds_3monthsago"));
    bankProfileVO.setNumberofrefunds_4monthsago(rsPopulateApplicationData.getString("numberofrefunds_4monthsago"));
    bankProfileVO.setNumberofrefunds_5monthsago(rsPopulateApplicationData.getString("numberofrefunds_5monthsago"));
    bankProfileVO.setNumberofrefunds_6monthsago(rsPopulateApplicationData.getString("numberofrefunds_6monthsago"));
    bankProfileVO.setNumberofrefunds_12monthsago(rsPopulateApplicationData.getString("numberofrefunds_12monthsago"));
    bankProfileVO.setNumberofrefunds_year2(rsPopulateApplicationData.getString("numberofrefunds_year2"));
    bankProfileVO.setNumberofrefunds_year3(rsPopulateApplicationData.getString("numberofrefunds_year3"));

    bankProfileVO.setChargebackratio_lastmonth(rsPopulateApplicationData.getString("chargebackratio_lastmonth"));
    bankProfileVO.setChargebackratio_2monthsago(rsPopulateApplicationData.getString("chargebackratio_2monthsago"));
    bankProfileVO.setChargebackratio_3monthsago(rsPopulateApplicationData.getString("chargebackratio_3monthsago"));
    bankProfileVO.setChargebackratio_4monthsago(rsPopulateApplicationData.getString("chargebackratio_4monthsago"));
    bankProfileVO.setChargebackratio_5monthsago(rsPopulateApplicationData.getString("chargebackratio_5monthsago"));
    bankProfileVO.setChargebackratio_6monthsago(rsPopulateApplicationData.getString("chargebackratio_6monthsago"));
    bankProfileVO.setChargebackratio_12monthsago(rsPopulateApplicationData.getString("chargebackratio_12monthsago"));
    bankProfileVO.setChargebackratio_year2(rsPopulateApplicationData.getString("chargebackratio_year2"));
    bankProfileVO.setChargebackratio_year3(rsPopulateApplicationData.getString("chargebackratio_year3"));
    bankProfileVO.setRefundratio_lastmonth(rsPopulateApplicationData.getString("refundratio_lastmonth"));
    bankProfileVO.setRefundratio_2monthsago(rsPopulateApplicationData.getString("refundratio_2monthsago"));
    bankProfileVO.setRefundratio_3monthsago(rsPopulateApplicationData.getString("refundratio_3monthsago"));
    bankProfileVO.setRefundratio_4monthsago(rsPopulateApplicationData.getString("refundratio_4monthsago"));
    bankProfileVO.setRefundratio_5monthsago(rsPopulateApplicationData.getString("refundratio_5monthsago"));
    bankProfileVO.setRefundratio_6monthsago(rsPopulateApplicationData.getString("refundratio_6monthsago"));
    bankProfileVO.setRefundratio_12monthsago(rsPopulateApplicationData.getString("refundratio_12monthsago"));
    bankProfileVO.setRefundratio_year2(rsPopulateApplicationData.getString("refundratio_year2"));
    bankProfileVO.setRefundratio_year3(rsPopulateApplicationData.getString("refundratio_year3"));

    bankProfileVO.setCurrency(rsPopulateApplicationData.getString("currency"));

    bankProfileVO.setBank_account_currencies(rsPopulateApplicationData.getString("bank_account_currencies"));
    //bankProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));
    //bankProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));
    bankProfileVO.setBankProfileSaved(rsPopulateApplicationData.getString("isbankprofilesaved"));
    bankProfileVO.setIsProcessingHistory(rsPopulateApplicationData.getString("isprocessinghistory"));
    bankProfileVO.setCustomer_trans_data(rsPopulateApplicationData.getString("customer_trans_data"));
    bankProfileVO.setProcessinghistory_creation_time(rsPopulateApplicationData.getString("creation_time"));
    bankProfileVO.setProcessinghistory_updation_time(rsPopulateApplicationData.getString("updation_time"));

    //Set value for CardHolderProfile
    cardholderProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    cardholderProfileVO.setCompliance_swapp(rsPopulateApplicationData.getString("compliance_swapp"));
    cardholderProfileVO.setCompliance_thirdpartyappform(rsPopulateApplicationData.getString("compliance_thirdpartyappform"));
    cardholderProfileVO.setCompliance_thirdpartysoft(rsPopulateApplicationData.getString("compliance_thirdpartysoft"));
    cardholderProfileVO.setCompliance_version(rsPopulateApplicationData.getString("compliance_version"));
    cardholderProfileVO.setCompliance_companiesorgateways(rsPopulateApplicationData.getString("compliance_companiesorgateways"));
    cardholderProfileVO.setCompliance_companiesorgateways_yes(rsPopulateApplicationData.getString("compliance_companiesorgateways_yes"));
    cardholderProfileVO.setCompliance_electronically(rsPopulateApplicationData.getString("compliance_electronically"));
    cardholderProfileVO.setCompliance_carddatastored(rsPopulateApplicationData.getString("compliance_carddatastored"));
    cardholderProfileVO.setCompliance_cispcompliant(rsPopulateApplicationData.getString("compliance_cispcompliant"));
    cardholderProfileVO.setCompliance_cispcompliant_yes(rsPopulateApplicationData.getString("compliance_cispcompliant_yes"));
    cardholderProfileVO.setCompliance_pcidsscompliant(rsPopulateApplicationData.getString("compliance_pcidsscompliant"));
    cardholderProfileVO.setCompliance_pcidsscompliant_yes(rsPopulateApplicationData.getString("compliance_pcidsscompliant_yes"));
    cardholderProfileVO.setCompliance_qualifiedsecurityassessor(rsPopulateApplicationData.getString("compliance_qualifiedsecurityassessor"));
    cardholderProfileVO.setCompliance_dateofcompliance(rsPopulateApplicationData.getString("compliance_dateofcompliance"));
    cardholderProfileVO.setCompliance_dateoflastscan(rsPopulateApplicationData.getString("compliance_dateoflastscan"));
    cardholderProfileVO.setCompliance_datacompromise(rsPopulateApplicationData.getString("compliance_datacompromise"));
    cardholderProfileVO.setCompliance_datacompromise_yes(rsPopulateApplicationData.getString("compliance_datacompromise_yes"));
    cardholderProfileVO.setSiteinspection_merchant(rsPopulateApplicationData.getString("siteinspection_merchant"));
    cardholderProfileVO.setSiteinspection_landlord(rsPopulateApplicationData.getString("siteinspection_landlord"));
    cardholderProfileVO.setSiteinspection_buildingtype(rsPopulateApplicationData.getString("siteinspection_buildingtype"));
    cardholderProfileVO.setSiteinspection_areazoned(rsPopulateApplicationData.getString("siteinspection_areazoned"));
    cardholderProfileVO.setSiteinspection_squarefootage(rsPopulateApplicationData.getString("siteinspection_squarefootage"));
    cardholderProfileVO.setSiteinspection_operatebusiness(rsPopulateApplicationData.getString("siteinspection_operatebusiness"));
    cardholderProfileVO.setSiteinspection_principal1(rsPopulateApplicationData.getString("siteinspection_principal1"));
    cardholderProfileVO.setSiteinspection_principal1_date(rsPopulateApplicationData.getString("siteinspection_principal1_date"));
    cardholderProfileVO.setSiteinspection_principal2(rsPopulateApplicationData.getString("siteinspection_principal2"));
    cardholderProfileVO.setSiteinspection_principal2_date(rsPopulateApplicationData.getString("siteinspection_principal2_date"));
    cardholderProfileVO.setCardHolderProfileSaved(rsPopulateApplicationData.getString("iscardholderprofilesaved"));

    //Set value for ExtraDetailsProfile
    extraDetailsProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    extraDetailsProfileVO.setCompany_financialReport(rsPopulateApplicationData.getString("company_financialreport"));
    extraDetailsProfileVO.setCompany_financialReportYes(rsPopulateApplicationData.getString("company_financialreportyes"));
    extraDetailsProfileVO.setFinancialReport_institution(rsPopulateApplicationData.getString("financialreport_institution"));
    extraDetailsProfileVO.setFinancialReport_available(rsPopulateApplicationData.getString("financialreport_available"));
    extraDetailsProfileVO.setFinancialReport_availableYes(rsPopulateApplicationData.getString("financialReport_availableyes"));
    extraDetailsProfileVO.setOwnerSince(rsPopulateApplicationData.getString("ownersince"));
    extraDetailsProfileVO.setSocialSecurity(rsPopulateApplicationData.getString("socialsecurity"));
    extraDetailsProfileVO.setCompany_formParticipation(rsPopulateApplicationData.getString("company_formparticipation"));
    extraDetailsProfileVO.setFinancialObligation(rsPopulateApplicationData.getString("financialobligation"));
    extraDetailsProfileVO.setCompliance_punitiveSanction(rsPopulateApplicationData.getString("compliance_punitivesanction"));
    extraDetailsProfileVO.setCompliance_punitiveSanctionYes(rsPopulateApplicationData.getString("compliance_punitivesanctionyes"));
    extraDetailsProfileVO.setWorkingExperience(rsPopulateApplicationData.getString("workingexperience"));
    extraDetailsProfileVO.setGoodsInsuranceOffered(rsPopulateApplicationData.getString("goodsinsuranceoffered"));
    extraDetailsProfileVO.setFulfillment_productEmail(rsPopulateApplicationData.getString("fulfillment_productemail"));
    extraDetailsProfileVO.setFulfillment_productEmailYes(rsPopulateApplicationData.getString("fulfillment_productemailyes"));
    extraDetailsProfileVO.setBlacklistedAccountClosed(rsPopulateApplicationData.getString("blacklistedaccountclosed"));
    extraDetailsProfileVO.setBlacklistedAccountClosedYes(rsPopulateApplicationData.getString("blacklistedaccountclosedyes"));
    extraDetailsProfileVO.setShiping_deliveryMethod(rsPopulateApplicationData.getString("shiping_deliverymethod"));
    extraDetailsProfileVO.setTransactionMonitoringProcess(rsPopulateApplicationData.getString("transactionmonitoringprocess"));
    extraDetailsProfileVO.setOperationalLicense(rsPopulateApplicationData.getString("operationallicense"));
    extraDetailsProfileVO.setSupervisorregularcontrole(rsPopulateApplicationData.getString("supervisorregularcontrole"));
    extraDetailsProfileVO.setDeedOfAgreement(rsPopulateApplicationData.getString("deedofagreement"));
    extraDetailsProfileVO.setDeedOfAgreementYes(rsPopulateApplicationData.getString("deedofagreementyes"));
    extraDetailsProfileVO.setExtraDetailsProfileSaved(rsPopulateApplicationData.getString("isextradetailsprofile"));

    applicationManagerVO.setCompanyProfileVO(companyProfileVO);
    applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
    applicationManagerVO.setBusinessProfileVO(businessProfileVO);
    applicationManagerVO.setBankProfileVO(bankProfileVO);
    applicationManagerVO.setCardholderProfileVO(cardholderProfileVO);
    applicationManagerVO.setExtradetailsprofileVO(extraDetailsProfileVO);
   }
   else
   {
    CompanyProfileVO companyProfileVO = new CompanyProfileVO();
    AddressIdentificationVO identificationVO_company = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_business = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_euCompany = new AddressIdentificationVO();
    ContactDetailsVO contactDetailsVO_main = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_technical = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_billing = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_cbk = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_pci = new ContactDetailsVO();

    Map<String, AddressIdentificationVO> addressIdentificationVOMap = new HashMap();
    Map<String, ContactDetailsVO> contactDetailsVOMap = new HashMap();

    addressIdentificationVOMap.put(ApplicationManagerTypes.COMPANY,identificationVO_company);
    addressIdentificationVOMap.put(ApplicationManagerTypes.BUSINESS, identificationVO_business);
    addressIdentificationVOMap.put(ApplicationManagerTypes.EU_COMPANY, identificationVO_euCompany);
    contactDetailsVOMap.put(ApplicationManagerTypes.MAIN, contactDetailsVO_main);
    contactDetailsVOMap.put(ApplicationManagerTypes.BILLING, contactDetailsVO_billing);
    contactDetailsVOMap.put(ApplicationManagerTypes.TECHNICAL, contactDetailsVO_technical);
    contactDetailsVOMap.put(ApplicationManagerTypes.CBK, contactDetailsVO_cbk);
    contactDetailsVOMap.put(ApplicationManagerTypes.PCI, contactDetailsVO_pci);

    companyProfileVO.setCompanyProfile_addressVOMap(addressIdentificationVOMap);
    companyProfileVO.setCompanyProfile_contactInfoVOMap(contactDetailsVOMap);

    OwnershipProfileVO ownershipProfileVO = new OwnershipProfileVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory4 = new OwnershipProfileDetailsVO();

    Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap = new HashMap();

    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER1,ownershipProfileDetailsVO_shareholder1);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER2,ownershipProfileDetailsVO_shareholder2);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER3,ownershipProfileDetailsVO_shareholder3);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER4,ownershipProfileDetailsVO_shareholder4);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER1,ownershipProfileDetailsVO_corporateShareholder1);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER2,ownershipProfileDetailsVO_corporateShareholder2);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER3,ownershipProfileDetailsVO_corporateShareholder3);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER4,ownershipProfileDetailsVO_corporateShareholder4);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR1,ownershipProfileDetailsVO_director1);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR2,ownershipProfileDetailsVO_director2);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR3,ownershipProfileDetailsVO_director3);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR4,ownershipProfileDetailsVO_director4);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY1,ownershipProfileDetailsVO_authorizeSignatory1);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY2,ownershipProfileDetailsVO_authorizeSignatory2);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY3,ownershipProfileDetailsVO_authorizeSignatory3);
    ownershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY4,ownershipProfileDetailsVO_authorizeSignatory4);
    ownershipProfileVO.setOwnershipProfileDetailsVOMap(ownershipProfileDetailsVOMap);


    BusinessProfileVO businessProfileVO = new BusinessProfileVO();
    BankProfileVO bankProfileVO = new BankProfileVO();
    CardholderProfileVO cardholderProfileVO = new CardholderProfileVO();
    ExtraDetailsProfileVO extraDetailsProfileVO = new ExtraDetailsProfileVO();


    applicationManagerVO.setCompanyProfileVO(companyProfileVO);
    applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
    applicationManagerVO.setBusinessProfileVO(businessProfileVO);
    applicationManagerVO.setBankProfileVO(bankProfileVO);
    applicationManagerVO.setCardholderProfileVO(cardholderProfileVO);
    applicationManagerVO.setExtradetailsprofileVO(extraDetailsProfileVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error("System exception while getting Application information::", systemError);
  }
  catch (SQLException e)
  {
   logger.error("Sql Exception while getting Application information::", e);
  }
  finally
  {
   Database.closeResultSet(rsPopulateApplicationData);
   Database.closePreparedStatement(psPopulateApplicationData);
   Database.closeConnection(con);
  }
 }

 public ApplicationManagerVO getAppManagerDetails(ApplicationManagerVO applicationManagerVO)
 {
  Connection con = null;
  PreparedStatement psPopulateApplicationData = null;
  ResultSet rsPopulateApplicationData = null;
  try
  {
   con = Database.getConnection();
   String query = "SELECT * FROM application_manager as AM left Join " +
           " `companyprofile` AS ACO on AM.application_id = ACO.application_id left JOIN " +
           "`ownershipprofile` AS AO ON AM.application_id=AO.application_id left JOIN " +
           "`applicationmanager_businessprofile` AS ABU ON AM.application_id=ABU.application_id left JOIN " +
           "`applicationmanager_bankprofile` AS ABA ON AM.application_id=ABA.application_id left JOIN" +
           "`applicationmanager_currencywisebankinfo` AS ACB ON AM.application_id=ACB.application_id left JOIN" +
           "`applicationmanager_processinghistory` AS APH ON AM.application_id=APH.application_id left JOIN " +
           " `applicationmanager_cardholderprofile` AS ACA ON AM.application_id=ACA.application_id left JOIN" +
           " `applicationmanager_extradetailsprofile` AS EDP ON AM.application_id=EDP.application_id" +
                    /*" `speed_status` AS SS ON AM.application_id=SS.application_id " +*/
           " WHERE AM.member_id=?";

   psPopulateApplicationData = con.prepareStatement(query.toString());
   psPopulateApplicationData.setString(1, applicationManagerVO.getMemberId());
   logger.debug("000000");
   rsPopulateApplicationData = psPopulateApplicationData.executeQuery();
   if (rsPopulateApplicationData.next())
   {
    CompanyProfileVO companyProfileVO = new CompanyProfileVO();
    OwnershipProfileVO ownershipProfileVO = new OwnershipProfileVO();
    BusinessProfileVO businessProfileVO = new BusinessProfileVO();
    BankProfileVO bankProfileVO = new BankProfileVO();
    CardholderProfileVO cardholderProfileVO = new CardholderProfileVO();
    ExtraDetailsProfileVO extraDetailsProfileVO = new ExtraDetailsProfileVO();

    applicationManagerVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    applicationManagerVO.setApplicationSaved(rsPopulateApplicationData.getString("isapplicationsaved"));
    applicationManagerVO.setStatus(rsPopulateApplicationData.getString("status"));
    applicationManagerVO.setMaf_Status(rsPopulateApplicationData.getString("MAF_status"));
    applicationManagerVO.setKyc_Status(rsPopulateApplicationData.getString("KYC_status"));
    applicationManagerVO.setSpeed_status(rsPopulateApplicationData.getString("speed_status"));
    applicationManagerVO.setAppliedToModify(rsPopulateApplicationData.getString("appliedToModify"));
    applicationManagerVO.setStandby_user(rsPopulateApplicationData.getString("maf_user"));
    applicationManagerVO.setUser(rsPopulateApplicationData.getString("maf_user"));
    applicationManagerVO.setSpeed_user(rsPopulateApplicationData.getString("speed_user"));

    //Set value for CompanyProfile
    companyProfileVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    companyProfileVO.setCompanyBankruptcy(rsPopulateApplicationData.getString("company_bankruptcy"));
    companyProfileVO.setCompanyBankruptcydate(rsPopulateApplicationData.getString("company_bankruptcydate"));
    companyProfileVO.setCompanyTypeOfBusiness(rsPopulateApplicationData.getString("company_typeofbusiness"));
    companyProfileVO.setCompanyLengthOfTimeInBusiness(rsPopulateApplicationData.getString("company_lengthoftime_business"));
    companyProfileVO.setCompanyCapitalResources(rsPopulateApplicationData.getString("company_capitalresources"));
    companyProfileVO.setCompany_currencylastyear(rsPopulateApplicationData.getString("company_currencylastyear"));
    companyProfileVO.setCompanyTurnoverLastYear(rsPopulateApplicationData.getString("company_turnoverlastyear"));
    companyProfileVO.setCompany_turnoverlastyear_unit(rsPopulateApplicationData.getString("company_turnoverlastyear_unit"));
    companyProfileVO.setCompanyNumberOfEmployees(rsPopulateApplicationData.getString("company_numberofemployees"));
    companyProfileVO.setLicense_required(rsPopulateApplicationData.getString("License_required"));
    companyProfileVO.setLicense_Permission(rsPopulateApplicationData.getString("License_Permission"));
    companyProfileVO.setLegalProceeding(rsPopulateApplicationData.getString("legal_proceeding"));
    companyProfileVO.setCompanyProfileSaved(rsPopulateApplicationData.getString("iscompanyprofilesaved"));
    companyProfileVO.setStartup_business(rsPopulateApplicationData.getString("startup_business"));
    companyProfileVO.setIscompany_insured(rsPopulateApplicationData.getString("iscompany_insured"));
    companyProfileVO.setInsured_companyname(rsPopulateApplicationData.getString("insured_companyname"));
    companyProfileVO.setMain_business_partner(rsPopulateApplicationData.getString("main_business_partner"));
    companyProfileVO.setLoans(rsPopulateApplicationData.getString("loans"));
    companyProfileVO.setIncome_economic_activity(rsPopulateApplicationData.getString("income_economic_activity"));
    companyProfileVO.setInterest_income(rsPopulateApplicationData.getString("interest_income"));
    companyProfileVO.setInvestments(rsPopulateApplicationData.getString("investments"));
    companyProfileVO.setIncome_sources_other(rsPopulateApplicationData.getString("income_sources_other"));
    companyProfileVO.setIncome_sources_other_yes(rsPopulateApplicationData.getString("income_sources_other_yes"));
    companyProfileVO.setInsured_amount(rsPopulateApplicationData.getString("insured_amount"));
    companyProfileVO.setInsured_currency(rsPopulateApplicationData.getString("insured_currency"));
    companyProfileVO.setCountryOfRegistration(rsPopulateApplicationData.getString("countryofregistration"));
    companyProfileVO.setCompanyProfile_addressVOMap(populateCompanyDetails(applicationManagerVO.getApplicationId()));
    companyProfileVO.setCompanyProfile_contactInfoVOMap(populateContactInformation(applicationManagerVO.getApplicationId()));

    //Set value for OwnershipProfile
    ownershipProfileVO.setApplicationid(rsPopulateApplicationData.getString("application_id"));
    ownershipProfileVO.setNumOfShareholders(rsPopulateApplicationData.getString("numOfShareholders"));
    ownershipProfileVO.setNumOfCorporateShareholders(rsPopulateApplicationData.getString("numOfCorporateShareholders"));
    ownershipProfileVO.setNumOfDirectors(rsPopulateApplicationData.getString("numOfDirectors"));
    ownershipProfileVO.setNumOfAuthrisedSignatory(rsPopulateApplicationData.getString("numOfAuthrisedSignatory"));
    ownershipProfileVO.setOwnerShipProfileSaved(rsPopulateApplicationData.getString("isownershipprofilesaved"));
    ownershipProfileVO.setOwnershipProfileDetailsVOMap(populateOwnershipProfileDetails(applicationManagerVO.getApplicationId()));

    //Set value for BusinessProfile
    businessProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    businessProfileVO.setForeigntransactions_us(rsPopulateApplicationData.getString("foreigntransactions_us"));
    businessProfileVO.setForeigntransactions_Europe(rsPopulateApplicationData.getString("foreigntransactions_Europe"));
    businessProfileVO.setForeigntransactions_Asia(rsPopulateApplicationData.getString("foreigntransactions_Asia"));
    businessProfileVO.setForeigntransactions_cis(rsPopulateApplicationData.getString("foreigntransactions_cis"));
    businessProfileVO.setForeigntransactions_canada(rsPopulateApplicationData.getString("foreigntransactions_canada"));
    businessProfileVO.setForeigntransactions_uk(rsPopulateApplicationData.getString("foreigntransactions_uk"));
    businessProfileVO.setForeigntransactions_RestoftheWorld(rsPopulateApplicationData.getString("foreigntransactions_RestoftheWorld"));
    businessProfileVO.setMethodofacceptance_moto(rsPopulateApplicationData.getString("methodofacceptance_moto"));
    businessProfileVO.setMethodofacceptance_internet(rsPopulateApplicationData.getString("methodofacceptance_internet"));
    businessProfileVO.setMethodofacceptance_swipe(rsPopulateApplicationData.getString("methodofacceptance_swipe"));
    businessProfileVO.setAverageticket(rsPopulateApplicationData.getString("averageticket"));
    businessProfileVO.setHighestticket(rsPopulateApplicationData.getString("highestticket"));
    businessProfileVO.setUrls(rsPopulateApplicationData.getString("urls"));
    businessProfileVO.setDescriptor(rsPopulateApplicationData.getString("descriptor_creditcardstmt"));
    businessProfileVO.setDescriptionofproducts(rsPopulateApplicationData.getString("descriptionofproducts"));
    businessProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));
    businessProfileVO.setRecurringservices(rsPopulateApplicationData.getString("recurringservices"));
    businessProfileVO.setRecurringservicesyes(rsPopulateApplicationData.getString("recurringservicesyes"));
    businessProfileVO.setIsacallcenterusedyes(rsPopulateApplicationData.getString("isacallcenterusedyes"));
    businessProfileVO.setIsafulfillmenthouseused(rsPopulateApplicationData.getString("isafulfillmenthouseused"));
    businessProfileVO.setIsafulfillmenthouseused_yes(rsPopulateApplicationData.getString("isafulfillmenthouseused_yes"));
    businessProfileVO.setCardtypesaccepted_visa(rsPopulateApplicationData.getString("cardtypesaccepted_visa"));
    businessProfileVO.setCardtypesaccepted_mastercard(rsPopulateApplicationData.getString("cardtypesaccepted_mastercard"));
    businessProfileVO.setCardtypesaccepted_americanexpress(rsPopulateApplicationData.getString("cardtypesaccepted_americanexpress"));
    businessProfileVO.setCardtypesaccepted_discover(rsPopulateApplicationData.getString("cardtypesaccepted_discover"));
    businessProfileVO.setCardtypesaccepted_diners(rsPopulateApplicationData.getString("cardtypesaccepted_diners"));
    businessProfileVO.setCardtypesaccepted_jcb(rsPopulateApplicationData.getString("cardtypesaccepted_jcb"));
    businessProfileVO.setCardtypesaccepted_rupay(rsPopulateApplicationData.getString("cardtypesaccepted_rupay"));
    businessProfileVO.setCardtypesaccepted_other(rsPopulateApplicationData.getString("cardtypesaccepted_other"));
    businessProfileVO.setCardtypesaccepted_other_yes(rsPopulateApplicationData.getString("cardtypesaccepted_other_yes"));
    businessProfileVO.setBusinessProfileSaved(rsPopulateApplicationData.getString("isbuisnessprofilesaved"));

    //businessProfileVO.setSizeofcustomer_Database(rsPopulateApplicationData.getString("sizeofcustomer_Database"));
    //businessProfileVO.setTopfivecountries(rsPopulateApplicationData.getString("topfivecountries"));
    businessProfileVO.setKyc_processes(rsPopulateApplicationData.getString("kyc_processes"));
    //businessProfileVO.setCustomer_account(rsPopulateApplicationData.getString("customer_account"));
    businessProfileVO.setVisa_cardlogos(rsPopulateApplicationData.getString("visa_cardlogos"));
    businessProfileVO.setMaster_cardlogos(rsPopulateApplicationData.getString("master_cardlogos"));
    businessProfileVO.setThreeD_secure_compulsory(rsPopulateApplicationData.getString("threeD_secure_compulsory"));
    businessProfileVO.setPrice_displayed(rsPopulateApplicationData.getString("price_displayed"));
    businessProfileVO.setTransaction_currency(rsPopulateApplicationData.getString("transaction_currency"));
    businessProfileVO.setCardholder_asked(rsPopulateApplicationData.getString("cardholder_asked"));
    businessProfileVO.setDynamic_descriptors(rsPopulateApplicationData.getString("dynamic_descriptors"));
    businessProfileVO.setShopping_cart(rsPopulateApplicationData.getString("shopping_cart"));
    businessProfileVO.setShopping_cart_details(rsPopulateApplicationData.getString("shopping_cart_details"));
    businessProfileVO.setPricing_policies_website(rsPopulateApplicationData.getString("pricing_policies_website"));
    businessProfileVO.setPricing_policies_website_yes(rsPopulateApplicationData.getString("pricing_policies_website_yes"));
    businessProfileVO.setFulfillment_timeframe(rsPopulateApplicationData.getString("fulfillment_timeframe"));
    businessProfileVO.setGoods_policy(rsPopulateApplicationData.getString("goods_policy"));
    businessProfileVO.setMCC_Ctegory(rsPopulateApplicationData.getString("MCC_Ctegory"));
    // businessProfileVO.setTraffic_countries_us(rsPopulateApplicationData.getString("traffic_countries_us"));
    //businessProfileVO.setTraffic_countries_Europe(rsPopulateApplicationData.getString("traffic_countries_Europe"));
    // businessProfileVO.setTraffic_countries_Asia(rsPopulateApplicationData.getString("traffic_countries_Asia"));
    //businessProfileVO.setTraffic_countries_CIS(rsPopulateApplicationData.getString("traffic_countries_CIS"));
    //businessProfileVO.setTraffic_countries_canada(rsPopulateApplicationData.getString("traffic_countries_canada"));
    // businessProfileVO.setTraffic_countries_restworld(rsPopulateApplicationData.getString("traffic_countries_restworld"));
    businessProfileVO.setCountries_blocked(rsPopulateApplicationData.getString("countries_blocked"));
    businessProfileVO.setCountries_blocked_details(rsPopulateApplicationData.getString("countries_blocked_details"));
    businessProfileVO.setCustomer_support(rsPopulateApplicationData.getString("customer_support"));
    // businessProfileVO.setCustomer_support_details(rsPopulateApplicationData.getString("customer_support_details"));

    businessProfileVO.setAffiliate_programs(rsPopulateApplicationData.getString("affiliate_programs"));
    businessProfileVO.setAffiliate_programs_details(rsPopulateApplicationData.getString("affiliate_programs_details"));
    businessProfileVO.setListfraudtools(rsPopulateApplicationData.getString("listfraudtools"));
    businessProfileVO.setListfraudtools_yes(rsPopulateApplicationData.getString("listfraudtools_yes"));
    businessProfileVO.setCustomers_identification(rsPopulateApplicationData.getString("customers_identification"));
    businessProfileVO.setCustomers_identification_yes(rsPopulateApplicationData.getString("customers_identification_yes"));
    businessProfileVO.setCoolingoffperiod(rsPopulateApplicationData.getString("coolingoffperiod"));
    businessProfileVO.setCustomersupport_email(rsPopulateApplicationData.getString("customersupport_email"));
    businessProfileVO.setCustsupportwork_hours(rsPopulateApplicationData.getString("custsupportwork_hours"));
    businessProfileVO.setTechnical_contact(rsPopulateApplicationData.getString("technical_contact"));

    businessProfileVO.setSecuritypolicy(rsPopulateApplicationData.getString("securitypolicy"));
    businessProfileVO.setConfidentialitypolicy(rsPopulateApplicationData.getString("confidentialitypolicy"));
    businessProfileVO.setApplicablejurisdictions(rsPopulateApplicationData.getString("applicablejurisdictions"));
    businessProfileVO.setPrivacy_anonymity_dataprotection(rsPopulateApplicationData.getString("privacy_anonymity_dataprotection"));

    businessProfileVO.setApp_Services(rsPopulateApplicationData.getString("App_Services"));
    businessProfileVO.setAgency_employed(rsPopulateApplicationData.getString("agency_employed"));
    businessProfileVO.setAgency_employed_yes(rsPopulateApplicationData.getString("agency_employed_yes"));
    businessProfileVO.setProduct_requires(rsPopulateApplicationData.getString("product_requires"));
    businessProfileVO.setLowestticket(rsPopulateApplicationData.getString("lowestticket"));
    businessProfileVO.setTimeframe(rsPopulateApplicationData.getString("timeframe"));
    businessProfileVO.setLivechat(rsPopulateApplicationData.getString("livechat"));

    //Add new
    businessProfileVO.setLoginId(rsPopulateApplicationData.getString("login_id"));
    businessProfileVO.setPassWord(rsPopulateApplicationData.getString("password"));
    businessProfileVO.setIs_website_live(rsPopulateApplicationData.getString("is_website_live"));
    businessProfileVO.setTest_link(rsPopulateApplicationData.getString("test_link"));
    businessProfileVO.setCompanyIdentifiable(rsPopulateApplicationData.getString("companyidentifiable"));
    businessProfileVO.setClearlyPresented(rsPopulateApplicationData.getString("clearlypresented"));
    businessProfileVO.setTrackingNumber(rsPopulateApplicationData.getString("trackingnumber"));
    businessProfileVO.setDomainsOwned(rsPopulateApplicationData.getString("domainsowned"));
    businessProfileVO.setDomainsOwned_no(rsPopulateApplicationData.getString("domainsowned_no"));
    businessProfileVO.setSslSecured(rsPopulateApplicationData.getString("sslsecured"));
    businessProfileVO.setCopyright(rsPopulateApplicationData.getString("copyright"));
    businessProfileVO.setSourceContent(rsPopulateApplicationData.getString("sourcecontent"));
    businessProfileVO.setDirectMail(rsPopulateApplicationData.getString("directmail"));
    businessProfileVO.setYellowPages(rsPopulateApplicationData.getString("Yellowpages"));
    businessProfileVO.setRadioTv(rsPopulateApplicationData.getString("radiotv"));
    businessProfileVO.setInternet(rsPopulateApplicationData.getString("internet"));
    businessProfileVO.setNetworking(rsPopulateApplicationData.getString("networking"));
    businessProfileVO.setOutboundTelemarketing(rsPopulateApplicationData.getString("outboundtelemarketing"));
    businessProfileVO.setInHouseLocation(rsPopulateApplicationData.getString("inhouselocation"));
    businessProfileVO.setContactPerson(rsPopulateApplicationData.getString("contactperson"));
    businessProfileVO.setShippingContactemail(rsPopulateApplicationData.getString("shipping_contactemail"));
    businessProfileVO.setOtherLocation(rsPopulateApplicationData.getString("otherlocation"));
    businessProfileVO.setMainSuppliers(rsPopulateApplicationData.getString("mainsuppliers"));
    businessProfileVO.setShipmentAssured(rsPopulateApplicationData.getString("shipmentassured"));
    businessProfileVO.setBillingModel(rsPopulateApplicationData.getString("billing_model"));
    businessProfileVO.setBillingTimeFrame(rsPopulateApplicationData.getString("billing_timeframe"));
    businessProfileVO.setRecurringAmount(rsPopulateApplicationData.getString("recurring_amount"));
    businessProfileVO.setAutomaticRecurring(rsPopulateApplicationData.getString("automatic_recurring"));
    businessProfileVO.setMultipleMembership(rsPopulateApplicationData.getString("multiple_membership"));
    businessProfileVO.setFreeMembership(rsPopulateApplicationData.getString("free_membership"));
    businessProfileVO.setCreditCardRequired(rsPopulateApplicationData.getString("creditcard_Required"));
    businessProfileVO.setAutomaticallyBilled(rsPopulateApplicationData.getString("automatically_billed"));
    businessProfileVO.setPreAuthorization(rsPopulateApplicationData.getString("pre_authorization"));
    businessProfileVO.setMerchantCode(rsPopulateApplicationData.getString("merchantcode"));
    businessProfileVO.setIpaddress(rsPopulateApplicationData.getString("ipaddress"));

    // Wirecard requirement added in Business Profile
    businessProfileVO.setShopsystem_plugin(rsPopulateApplicationData.getString("shopsystem_plugin"));
    businessProfileVO.setDirect_debit_sepa(rsPopulateApplicationData.getString("direct_debit_sepa"));
    businessProfileVO.setAlternative_payments(rsPopulateApplicationData.getString("alternative_payments"));
    businessProfileVO.setRisk_management(rsPopulateApplicationData.getString("risk_management"));
    businessProfileVO.setPayment_engine(rsPopulateApplicationData.getString("payment_engine"));
    businessProfileVO.setWebhost_company_name(rsPopulateApplicationData.getString("webhost_company_name"));
    businessProfileVO.setWebhost_phone(rsPopulateApplicationData.getString("webhost_phone"));
    businessProfileVO.setWebhost_email(rsPopulateApplicationData.getString("webhost_email"));
    businessProfileVO.setWebhost_website(rsPopulateApplicationData.getString("webhost_website"));
    businessProfileVO.setWebhost_address(rsPopulateApplicationData.getString("webhost_address"));
    businessProfileVO.setPayment_company_name(rsPopulateApplicationData.getString("payment_company_name"));
    businessProfileVO.setPayment_phone(rsPopulateApplicationData.getString("payment_phone"));
    businessProfileVO.setPayment_email(rsPopulateApplicationData.getString("payment_email"));
    businessProfileVO.setPayment_website(rsPopulateApplicationData.getString("payment_website"));
    businessProfileVO.setPayment_address(rsPopulateApplicationData.getString("payment_address"));
    businessProfileVO.setCallcenter_phone(rsPopulateApplicationData.getString("callcenter_phone"));
    businessProfileVO.setCallcenter_email(rsPopulateApplicationData.getString("callcenter_email"));
    businessProfileVO.setCallcenter_website(rsPopulateApplicationData.getString("callcenter_website"));
    businessProfileVO.setCallcenter_address(rsPopulateApplicationData.getString("callcenter_address"));
    businessProfileVO.setShoppingcart_company_name(rsPopulateApplicationData.getString("shoppingcart_company_name"));
    businessProfileVO.setShoppingcart_phone(rsPopulateApplicationData.getString("shoppingcart_phone"));
    businessProfileVO.setShoppingcart_email(rsPopulateApplicationData.getString("shoppingcart_email"));
    businessProfileVO.setShoppingcart_website(rsPopulateApplicationData.getString("shoppingcart_website"));
    businessProfileVO.setShoppingcart_address(rsPopulateApplicationData.getString("shoppingcart_address"));
    businessProfileVO.setSeasonal_fluctuating(rsPopulateApplicationData.getString("seasonal_fluctuating"));
    businessProfileVO.setSeasonal_fluctuating_yes(rsPopulateApplicationData.getString("seasonal_fluctuating_yes"));
    businessProfileVO.setPaymenttype_credit(rsPopulateApplicationData.getString("paymenttype_credit"));
    businessProfileVO.setPaymenttype_debit(rsPopulateApplicationData.getString("paymenttype_debit"));
    businessProfileVO.setPaymenttype_netbanking(rsPopulateApplicationData.getString("paymenttype_netbanking"));
    businessProfileVO.setPaymenttype_wallet(rsPopulateApplicationData.getString("paymenttype_wallet"));
    businessProfileVO.setPaymenttype_alternate(rsPopulateApplicationData.getString("paymenttype_alternate"));
    businessProfileVO.setCreditor_id(rsPopulateApplicationData.getString("creditor_id"));
    businessProfileVO.setPayment_delivery(rsPopulateApplicationData.getString("payment_delivery"));
    businessProfileVO.setPayment_delivery_otheryes(rsPopulateApplicationData.getString("payment_delivery_otheryes"));
    businessProfileVO.setGoods_delivery(rsPopulateApplicationData.getString("goods_delivery"));
    businessProfileVO.setTerminal_type(rsPopulateApplicationData.getString("terminal_type"));
    businessProfileVO.setTerminal_type_otheryes(rsPopulateApplicationData.getString("terminal_type_otheryes"));
    businessProfileVO.setOne_time_percentage(rsPopulateApplicationData.getString("one_time_percentage"));
    businessProfileVO.setMoto_percentage(rsPopulateApplicationData.getString("moto_percentage"));
    businessProfileVO.setInternet_percentage(rsPopulateApplicationData.getString("internet_percentage"));
    businessProfileVO.setSwipe_percentage(rsPopulateApplicationData.getString("swipe_percentage"));
    businessProfileVO.setRecurring_percentage(rsPopulateApplicationData.getString("recurring_percentage"));
    businessProfileVO.setThreedsecure_percentage(rsPopulateApplicationData.getString("threedsecure_percentage"));

    businessProfileVO.setCardvolume_visa(rsPopulateApplicationData.getString("cardvolume_visa"));
    businessProfileVO.setCardvolume_mastercard(rsPopulateApplicationData.getString("cardvolume_mastercard"));
    businessProfileVO.setCardvolume_americanexpress(rsPopulateApplicationData.getString("cardvolume_americanexpress"));
    businessProfileVO.setCardvolume_dinner(rsPopulateApplicationData.getString("cardvolume_dinner"));
    businessProfileVO.setCardvolume_other(rsPopulateApplicationData.getString("cardvolume_other"));
    businessProfileVO.setCardvolume_discover(rsPopulateApplicationData.getString("cardvolume_discover"));
    businessProfileVO.setCardvolume_rupay(rsPopulateApplicationData.getString("cardvolume_rupay"));
    businessProfileVO.setCardvolume_jcb(rsPopulateApplicationData.getString("cardvolume_jcb"));
    businessProfileVO.setPayment_type_yes(rsPopulateApplicationData.getString("payment_type_yes"));
    businessProfileVO.setOrderconfirmation_post(rsPopulateApplicationData.getString("orderconfirmation_post"));
    businessProfileVO.setOrderconfirmation_email(rsPopulateApplicationData.getString("orderconfirmation_email"));
    businessProfileVO.setOrderconfirmation_sms(rsPopulateApplicationData.getString("orderconfirmation_sms"));
    businessProfileVO.setOrderconfirmation_other(rsPopulateApplicationData.getString("orderconfirmation_other"));
    businessProfileVO.setOrderconfirmation_other_yes(rsPopulateApplicationData.getString("orderconfirmation_other_yes"));
    businessProfileVO.setPhysicalgoods_delivered(rsPopulateApplicationData.getString("physicalgoods_delivered"));
    businessProfileVO.setViainternetgoods_delivered(rsPopulateApplicationData.getString("viainternetgoods_delivered"));

    //Set value for BankProfile
    applicationManagerVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    bankProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    bankProfileVO.setCurrencyrequested_productssold(rsPopulateApplicationData.getString("currencyrequested_productssold"));
    bankProfileVO.setCurrencyrequested_bankaccount(rsPopulateApplicationData.getString("currencyrequested_bankaccount"));
    bankProfileVO.setBankinfo_bic(rsPopulateApplicationData.getString("bankinfo_bic"));
    bankProfileVO.setBankinfo_bank_name(rsPopulateApplicationData.getString("bankinfo_bank_name"));
    bankProfileVO.setBankinfo_bankaddress(rsPopulateApplicationData.getString("bankinfo_bankaddress"));
    bankProfileVO.setBankinfo_bankphonenumber(rsPopulateApplicationData.getString("bankinfo_bankphonenumber"));
    bankProfileVO.setBankinfo_aba_routingcode(rsPopulateApplicationData.getString("bankinfo_aba_routingcode"));
    bankProfileVO.setBankinfo_accountholder(rsPopulateApplicationData.getString("bankinfo_accountholder"));
    bankProfileVO.setBankinfo_accountnumber(rsPopulateApplicationData.getString("bankinfo_accountnumber"));
    bankProfileVO.setBankinfo_IBAN(rsPopulateApplicationData.getString("bankinfo_IBAN"));
    bankProfileVO.setBankinfo_contactperson(rsPopulateApplicationData.getString("bankinfo_contactperson"));
    bankProfileVO.setIscurrencywisebankinfo(rsPopulateApplicationData.getString("iscurrencywisebankinfo"));
    bankProfileVO.setBankinfo_currency(rsPopulateApplicationData.getString("bankinfo_currency"));
    bankProfileVO.setCurrencywisebankinfo_id(rsPopulateApplicationData.getString("currencywisebankinfo_id"));

    bankProfileVO.setProcessinghistory_id(rsPopulateApplicationData.getString("processinghistory_id"));
    bankProfileVO.setSalesvolume_lastmonth(rsPopulateApplicationData.getString("salesvolume_lastmonth"));
    bankProfileVO.setSalesvolume_2monthsago(rsPopulateApplicationData.getString("salesvolume_2monthsago"));
    bankProfileVO.setSalesvolume_3monthsago(rsPopulateApplicationData.getString("salesvolume_3monthsago"));
    bankProfileVO.setSalesvolume_4monthsago(rsPopulateApplicationData.getString("salesvolume_4monthsago"));
    bankProfileVO.setSalesvolume_5monthsago(rsPopulateApplicationData.getString("salesvolume_5monthsago"));
    bankProfileVO.setSalesvolume_6monthsago(rsPopulateApplicationData.getString("salesvolume_6monthsago"));
    bankProfileVO.setSalesvolume_12monthsago(rsPopulateApplicationData.getString("salesvolume_12monthsago"));
    bankProfileVO.setSalesvolume_year2(rsPopulateApplicationData.getString("salesvolume_year2"));
    bankProfileVO.setSalesvolume_year3(rsPopulateApplicationData.getString("salesvolume_year3"));
    bankProfileVO.setNumberoftransactions_lastmonth(rsPopulateApplicationData.getString("numberoftransactions_lastmonth"));
    bankProfileVO.setNumberoftransactions_2monthsago(rsPopulateApplicationData.getString("numberoftransactions_2monthsago"));
    bankProfileVO.setNumberoftransactions_3monthsago(rsPopulateApplicationData.getString("numberoftransactions_3monthsago"));
    bankProfileVO.setNumberoftransactions_4monthsago(rsPopulateApplicationData.getString("numberoftransactions_4monthsago"));
    bankProfileVO.setNumberoftransactions_5monthsago(rsPopulateApplicationData.getString("numberoftransactions_5monthsago"));
    bankProfileVO.setNumberoftransactions_6monthsago(rsPopulateApplicationData.getString("numberoftransactions_6monthsago"));
    bankProfileVO.setNumberoftransactions_12monthsago(rsPopulateApplicationData.getString("numberoftransactions_12monthsago"));
    bankProfileVO.setNumberoftransactions_year2(rsPopulateApplicationData.getString("numberoftransactions_year2"));
    bankProfileVO.setNumberoftransactions_year3(rsPopulateApplicationData.getString("numberoftransactions_year3"));
    bankProfileVO.setChargebackvolume_lastmonth(rsPopulateApplicationData.getString("chargebackvolume_lastmonth"));
    bankProfileVO.setChargebackvolume_2monthsago(rsPopulateApplicationData.getString("chargebackvolume_2monthsago"));
    bankProfileVO.setChargebackvolume_3monthsago(rsPopulateApplicationData.getString("chargebackvolume_3monthsago"));
    bankProfileVO.setChargebackvolume_4monthsago(rsPopulateApplicationData.getString("chargebackvolume_4monthsago"));
    bankProfileVO.setChargebackvolume_5monthsago(rsPopulateApplicationData.getString("chargebackvolume_5monthsago"));
    bankProfileVO.setChargebackvolume_6monthsago(rsPopulateApplicationData.getString("chargebackvolume_6monthsago"));
    bankProfileVO.setChargebackvolume_12monthsago(rsPopulateApplicationData.getString("chargebackvolume_12monthsago"));
    bankProfileVO.setChargebackvolume_year2(rsPopulateApplicationData.getString("chargebackvolume_year2"));
    bankProfileVO.setChargebackvolume_year3(rsPopulateApplicationData.getString("chargebackvolume_year3"));
    bankProfileVO.setNumberofchargebacks_lastmonth(rsPopulateApplicationData.getString("numberofchargebacks_lastmonth"));
    bankProfileVO.setNumberofchargebacks_2monthsago(rsPopulateApplicationData.getString("numberofchargebacks_2monthsago"));
    bankProfileVO.setNumberofchargebacks_3monthsago(rsPopulateApplicationData.getString("numberofchargebacks_3monthsago"));
    bankProfileVO.setNumberofchargebacks_4monthsago(rsPopulateApplicationData.getString("numberofchargebacks_4monthsago"));
    bankProfileVO.setNumberofchargebacks_5monthsago(rsPopulateApplicationData.getString("numberofchargebacks_5monthsago"));
    bankProfileVO.setNumberofchargebacks_6monthsago(rsPopulateApplicationData.getString("numberofchargebacks_6monthsago"));
    bankProfileVO.setNumberofchargebacks_12monthsago(rsPopulateApplicationData.getString("numberofchargebacks_12monthsago"));
    bankProfileVO.setNumberofchargebacks_year2(rsPopulateApplicationData.getString("numberofchargebacks_year2"));
    bankProfileVO.setNumberofchargebacks_year3(rsPopulateApplicationData.getString("numberofchargebacks_year3"));
    bankProfileVO.setRefundsvolume_lastmonth(rsPopulateApplicationData.getString("refundsvolume_lastmonth"));
    bankProfileVO.setRefundsvolume_2monthsago(rsPopulateApplicationData.getString("refundsvolume_2monthsago"));
    bankProfileVO.setRefundsvolume_3monthsago(rsPopulateApplicationData.getString("refundsvolume_3monthsago"));
    bankProfileVO.setRefundsvolume_4monthsago(rsPopulateApplicationData.getString("refundsvolume_4monthsago"));
    bankProfileVO.setRefundsvolume_5monthsago(rsPopulateApplicationData.getString("refundsvolume_5monthsago"));
    bankProfileVO.setRefundsvolume_6monthsago(rsPopulateApplicationData.getString("refundsvolume_6monthsago"));
    bankProfileVO.setRefundsvolume_12monthsago(rsPopulateApplicationData.getString("refundsvolume_12monthsago"));
    bankProfileVO.setRefundsvolume_year2(rsPopulateApplicationData.getString("refundsvolume_year2"));
    bankProfileVO.setRefundsvolume_year3(rsPopulateApplicationData.getString("refundsvolume_year3"));
    bankProfileVO.setNumberofrefunds_lastmonth(rsPopulateApplicationData.getString("numberofrefunds_lastmonth"));
    bankProfileVO.setNumberofrefunds_2monthsago(rsPopulateApplicationData.getString("numberofrefunds_2monthsago"));
    bankProfileVO.setNumberofrefunds_3monthsago(rsPopulateApplicationData.getString("numberofrefunds_3monthsago"));
    bankProfileVO.setNumberofrefunds_4monthsago(rsPopulateApplicationData.getString("numberofrefunds_4monthsago"));
    bankProfileVO.setNumberofrefunds_5monthsago(rsPopulateApplicationData.getString("numberofrefunds_5monthsago"));
    bankProfileVO.setNumberofrefunds_6monthsago(rsPopulateApplicationData.getString("numberofrefunds_6monthsago"));
    bankProfileVO.setNumberofrefunds_12monthsago(rsPopulateApplicationData.getString("numberofrefunds_12monthsago"));
    bankProfileVO.setNumberofrefunds_year2(rsPopulateApplicationData.getString("numberofrefunds_year2"));
    bankProfileVO.setNumberofrefunds_year3(rsPopulateApplicationData.getString("numberofrefunds_year3"));

    bankProfileVO.setChargebackratio_lastmonth(rsPopulateApplicationData.getString("chargebackratio_lastmonth"));
    bankProfileVO.setChargebackratio_2monthsago(rsPopulateApplicationData.getString("chargebackratio_2monthsago"));
    bankProfileVO.setChargebackratio_3monthsago(rsPopulateApplicationData.getString("chargebackratio_3monthsago"));
    bankProfileVO.setChargebackratio_4monthsago(rsPopulateApplicationData.getString("chargebackratio_4monthsago"));
    bankProfileVO.setChargebackratio_5monthsago(rsPopulateApplicationData.getString("chargebackratio_5monthsago"));
    bankProfileVO.setChargebackratio_6monthsago(rsPopulateApplicationData.getString("chargebackratio_6monthsago"));
    bankProfileVO.setChargebackratio_12monthsago(rsPopulateApplicationData.getString("chargebackratio_12monthsago"));
    bankProfileVO.setChargebackratio_year2(rsPopulateApplicationData.getString("chargebackratio_year2"));
    bankProfileVO.setChargebackratio_year3(rsPopulateApplicationData.getString("chargebackratio_year3"));
    bankProfileVO.setRefundratio_lastmonth(rsPopulateApplicationData.getString("refundratio_lastmonth"));
    bankProfileVO.setRefundratio_2monthsago(rsPopulateApplicationData.getString("refundratio_2monthsago"));
    bankProfileVO.setRefundratio_3monthsago(rsPopulateApplicationData.getString("refundratio_3monthsago"));
    bankProfileVO.setRefundratio_4monthsago(rsPopulateApplicationData.getString("refundratio_4monthsago"));
    bankProfileVO.setRefundratio_5monthsago(rsPopulateApplicationData.getString("refundratio_5monthsago"));
    bankProfileVO.setRefundratio_6monthsago(rsPopulateApplicationData.getString("refundratio_6monthsago"));
    bankProfileVO.setRefundratio_12monthsago(rsPopulateApplicationData.getString("refundratio_12monthsago"));
    bankProfileVO.setRefundratio_year2(rsPopulateApplicationData.getString("refundratio_year2"));
    bankProfileVO.setRefundratio_year3(rsPopulateApplicationData.getString("refundratio_year3"));

    bankProfileVO.setCurrency(rsPopulateApplicationData.getString("currency"));
    bankProfileVO.setBank_account_currencies(rsPopulateApplicationData.getString("bank_account_currencies"));
    //bankProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));

    bankProfileVO.setAquirer(rsPopulateApplicationData.getString("aquirer"));
    bankProfileVO.setReasonaquirer(rsPopulateApplicationData.getString("reason_aquirer"));
    bankProfileVO.setBankProfileSaved(rsPopulateApplicationData.getString("isbankprofilesaved"));
    bankProfileVO.setIsProcessingHistory(rsPopulateApplicationData.getString("isprocessinghistory"));
    bankProfileVO.setCustomer_trans_data(rsPopulateApplicationData.getString("customer_trans_data"));
    bankProfileVO.setProcessinghistory_creation_time(rsPopulateApplicationData.getString("creation_time"));
    bankProfileVO.setProcessinghistory_updation_time(rsPopulateApplicationData.getString("updation_time"));

    //Set value for CardHolderProfile
    cardholderProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    cardholderProfileVO.setCompliance_swapp(rsPopulateApplicationData.getString("compliance_swapp"));
    cardholderProfileVO.setCompliance_thirdpartyappform(rsPopulateApplicationData.getString("compliance_thirdpartyappform"));
    cardholderProfileVO.setCompliance_thirdpartysoft(rsPopulateApplicationData.getString("compliance_thirdpartysoft"));
    cardholderProfileVO.setCompliance_version(rsPopulateApplicationData.getString("compliance_version"));
    cardholderProfileVO.setCompliance_companiesorgateways(rsPopulateApplicationData.getString("compliance_companiesorgateways"));
    cardholderProfileVO.setCompliance_companiesorgateways_yes(rsPopulateApplicationData.getString("compliance_companiesorgateways_yes"));
    cardholderProfileVO.setCompliance_electronically(rsPopulateApplicationData.getString("compliance_electronically"));
    cardholderProfileVO.setCompliance_carddatastored(rsPopulateApplicationData.getString("compliance_carddatastored"));
    cardholderProfileVO.setCompliance_cispcompliant(rsPopulateApplicationData.getString("compliance_cispcompliant"));
    cardholderProfileVO.setCompliance_cispcompliant_yes(rsPopulateApplicationData.getString("compliance_cispcompliant_yes"));
    cardholderProfileVO.setCompliance_pcidsscompliant(rsPopulateApplicationData.getString("compliance_pcidsscompliant"));
    cardholderProfileVO.setCompliance_pcidsscompliant_yes(rsPopulateApplicationData.getString("compliance_pcidsscompliant_yes"));
    cardholderProfileVO.setCompliance_qualifiedsecurityassessor(rsPopulateApplicationData.getString("compliance_qualifiedsecurityassessor"));
    cardholderProfileVO.setCompliance_dateofcompliance(rsPopulateApplicationData.getString("compliance_dateofcompliance"));
    cardholderProfileVO.setCompliance_dateoflastscan(rsPopulateApplicationData.getString("compliance_dateoflastscan"));
    cardholderProfileVO.setCompliance_datacompromise(rsPopulateApplicationData.getString("compliance_datacompromise"));
    cardholderProfileVO.setCompliance_datacompromise_yes(rsPopulateApplicationData.getString("compliance_datacompromise_yes"));
    cardholderProfileVO.setSiteinspection_merchant(rsPopulateApplicationData.getString("siteinspection_merchant"));
    cardholderProfileVO.setSiteinspection_landlord(rsPopulateApplicationData.getString("siteinspection_landlord"));
    cardholderProfileVO.setSiteinspection_buildingtype(rsPopulateApplicationData.getString("siteinspection_buildingtype"));
    cardholderProfileVO.setSiteinspection_areazoned(rsPopulateApplicationData.getString("siteinspection_areazoned"));
    cardholderProfileVO.setSiteinspection_squarefootage(rsPopulateApplicationData.getString("siteinspection_squarefootage"));
    cardholderProfileVO.setSiteinspection_operatebusiness(rsPopulateApplicationData.getString("siteinspection_operatebusiness"));
    cardholderProfileVO.setSiteinspection_principal1(rsPopulateApplicationData.getString("siteinspection_principal1"));
    cardholderProfileVO.setSiteinspection_principal1_date(rsPopulateApplicationData.getString("siteinspection_principal1_date"));
    cardholderProfileVO.setSiteinspection_principal2(rsPopulateApplicationData.getString("siteinspection_principal2"));
    cardholderProfileVO.setSiteinspection_principal2_date(rsPopulateApplicationData.getString("siteinspection_principal2_date"));
    cardholderProfileVO.setCardHolderProfileSaved(rsPopulateApplicationData.getString("iscardholderprofilesaved"));

    //Set value for ExtraDetailsProfile
    extraDetailsProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    extraDetailsProfileVO.setCompany_financialReport(rsPopulateApplicationData.getString("company_financialreport"));
    extraDetailsProfileVO.setCompany_financialReportYes(rsPopulateApplicationData.getString("company_financialreportyes"));
    extraDetailsProfileVO.setFinancialReport_institution(rsPopulateApplicationData.getString("financialreport_institution"));
    extraDetailsProfileVO.setFinancialReport_available(rsPopulateApplicationData.getString("financialreport_available"));
    extraDetailsProfileVO.setFinancialReport_availableYes(rsPopulateApplicationData.getString("financialReport_availableyes"));
    extraDetailsProfileVO.setOwnerSince(rsPopulateApplicationData.getString("ownersince"));
    extraDetailsProfileVO.setSocialSecurity(rsPopulateApplicationData.getString("socialsecurity"));
    extraDetailsProfileVO.setCompany_formParticipation(rsPopulateApplicationData.getString("company_formparticipation"));
    extraDetailsProfileVO.setFinancialObligation(rsPopulateApplicationData.getString("financialobligation"));
    extraDetailsProfileVO.setCompliance_punitiveSanction(rsPopulateApplicationData.getString("compliance_punitivesanction"));
    extraDetailsProfileVO.setCompliance_punitiveSanctionYes(rsPopulateApplicationData.getString("compliance_punitivesanctionyes"));
    extraDetailsProfileVO.setWorkingExperience(rsPopulateApplicationData.getString("workingexperience"));
    extraDetailsProfileVO.setGoodsInsuranceOffered(rsPopulateApplicationData.getString("goodsinsuranceoffered"));
    extraDetailsProfileVO.setFulfillment_productEmail(rsPopulateApplicationData.getString("fulfillment_productemail"));
    extraDetailsProfileVO.setFulfillment_productEmailYes(rsPopulateApplicationData.getString("fulfillment_productemailyes"));
    extraDetailsProfileVO.setBlacklistedAccountClosed(rsPopulateApplicationData.getString("blacklistedaccountclosed"));
    extraDetailsProfileVO.setBlacklistedAccountClosedYes(rsPopulateApplicationData.getString("blacklistedaccountclosedyes"));
    extraDetailsProfileVO.setShiping_deliveryMethod(rsPopulateApplicationData.getString("shiping_deliverymethod"));
    extraDetailsProfileVO.setTransactionMonitoringProcess(rsPopulateApplicationData.getString("transactionmonitoringprocess"));
    extraDetailsProfileVO.setOperationalLicense(rsPopulateApplicationData.getString("operationallicense"));
    extraDetailsProfileVO.setSupervisorregularcontrole(rsPopulateApplicationData.getString("supervisorregularcontrole"));
    extraDetailsProfileVO.setDeedOfAgreement(rsPopulateApplicationData.getString("deedofagreement"));
    extraDetailsProfileVO.setDeedOfAgreementYes(rsPopulateApplicationData.getString("deedofagreementyes"));
    extraDetailsProfileVO.setExtraDetailsProfileSaved(rsPopulateApplicationData.getString("isextradetailsprofile"));

    applicationManagerVO.setCompanyProfileVO(companyProfileVO);
    applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
    applicationManagerVO.setBusinessProfileVO(businessProfileVO);
    applicationManagerVO.setBankProfileVO(bankProfileVO);
    applicationManagerVO.setCardholderProfileVO(cardholderProfileVO);
    applicationManagerVO.setExtradetailsprofileVO(extraDetailsProfileVO);
    logger.debug("query-----" + psPopulateApplicationData);

   }

  }
  catch (SystemError systemError)
  {
   logger.error("System exception while getting Application information::", systemError);
  }
  catch (SQLException e)
  {
   logger.error("Sql Exception while getting Application information::", e);
  }
  finally
  {
   Database.closeConnection(con);
  }

  return applicationManagerVO;
 }

 public ApplicationManagerVO getApplicationManagerDetailsFromApplicationId(ApplicationManagerVO applicationManagerVO)
 {
  //System.out.println("app_id----"+applicationManagerVO.getApplicationId());
  Connection con = null;
  PreparedStatement psPopulateApplicationData = null;
  ResultSet rsPopulateApplicationData = null;
  try
  {
   con = Database.getConnection();
   String query = "SELECT * FROM application_manager as AM left Join " +
           " `companyprofile` AS ACO on AM.application_id = ACO.application_id left JOIN " +
           " `ownershipprofile` AS AO ON AM.application_id=AO.application_id left JOIN " +
           " `applicationmanager_businessprofile` AS ABU ON AM.application_id=ABU.application_id left JOIN " +
           " `applicationmanager_bankprofile` AS ABA ON AM.application_id=ABA.application_id left JOIN" +
           " `applicationmanager_currencywisebankinfo` AS ACB ON AM.application_id=ACB.application_id left JOIN" +
           " `applicationmanager_processinghistory` AS APH ON AM.application_id=APH.application_id left JOIN " +
           " `applicationmanager_cardholderprofile` AS ACA ON AM.application_id=ACA.application_id left JOIN" +
           " `applicationmanager_extradetailsprofile` AS EDP ON AM.application_id=EDP.application_id left JOIN " +
           " `speed_status` AS SS ON AM.application_id=SS.application_id " +
           " WHERE AM.member_id=? AND AM.application_id=?";

   psPopulateApplicationData = con.prepareStatement(query.toString());
   psPopulateApplicationData.setString(1, applicationManagerVO.getMemberId());
   psPopulateApplicationData.setString(2, applicationManagerVO.getApplicationId());
   rsPopulateApplicationData = psPopulateApplicationData.executeQuery();
   if (rsPopulateApplicationData.next())
   {
    CompanyProfileVO companyProfileVO = new CompanyProfileVO();
    OwnershipProfileVO ownershipProfileVO = new OwnershipProfileVO();
    BusinessProfileVO businessProfileVO = new BusinessProfileVO();
    BankProfileVO bankProfileVO = new BankProfileVO();
    CardholderProfileVO cardholderProfileVO = new CardholderProfileVO();
    ExtraDetailsProfileVO extraDetailsProfileVO = new ExtraDetailsProfileVO();

    applicationManagerVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    applicationManagerVO.setApplicationSaved(rsPopulateApplicationData.getString("isapplicationsaved"));
    applicationManagerVO.setStatus(rsPopulateApplicationData.getString("status"));
    applicationManagerVO.setMaf_Status(rsPopulateApplicationData.getString("MAF_status"));
    applicationManagerVO.setKyc_Status(rsPopulateApplicationData.getString("KYC_status"));
    applicationManagerVO.setSpeed_status(rsPopulateApplicationData.getString("speed_status"));
    applicationManagerVO.setAppliedToModify(rsPopulateApplicationData.getString("appliedToModify"));
    applicationManagerVO.setStandby_user(rsPopulateApplicationData.getString("maf_user"));
    applicationManagerVO.setUser(rsPopulateApplicationData.getString("maf_user"));
    applicationManagerVO.setSpeed_user(rsPopulateApplicationData.getString("speed_user"));

    //Set value for CompanyProfile
    companyProfileVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    companyProfileVO.setCompanyBankruptcy(rsPopulateApplicationData.getString("company_bankruptcy"));
    companyProfileVO.setCompanyBankruptcydate(rsPopulateApplicationData.getString("company_bankruptcydate"));
    companyProfileVO.setCompanyTypeOfBusiness(rsPopulateApplicationData.getString("company_typeofbusiness"));
    companyProfileVO.setCompanyLengthOfTimeInBusiness(rsPopulateApplicationData.getString("company_lengthoftime_business"));
    companyProfileVO.setCompanyCapitalResources(rsPopulateApplicationData.getString("company_capitalresources"));
    companyProfileVO.setCompany_currencylastyear(rsPopulateApplicationData.getString("company_currencylastyear"));
    companyProfileVO.setCompanyTurnoverLastYear(rsPopulateApplicationData.getString("company_turnoverlastyear"));
    companyProfileVO.setCompany_turnoverlastyear_unit(rsPopulateApplicationData.getString("company_turnoverlastyear_unit"));
    companyProfileVO.setCompanyNumberOfEmployees(rsPopulateApplicationData.getString("company_numberofemployees"));
    companyProfileVO.setLicense_required(rsPopulateApplicationData.getString("License_required"));
    companyProfileVO.setLicense_Permission(rsPopulateApplicationData.getString("License_Permission"));
    companyProfileVO.setLegalProceeding(rsPopulateApplicationData.getString("legal_proceeding"));
    companyProfileVO.setCompanyProfileSaved(rsPopulateApplicationData.getString("iscompanyprofilesaved"));
    companyProfileVO.setStartup_business(rsPopulateApplicationData.getString("startup_business"));
    companyProfileVO.setIscompany_insured(rsPopulateApplicationData.getString("iscompany_insured"));
    companyProfileVO.setInsured_companyname(rsPopulateApplicationData.getString("insured_companyname"));
    companyProfileVO.setMain_business_partner(rsPopulateApplicationData.getString("main_business_partner"));
    companyProfileVO.setLoans(rsPopulateApplicationData.getString("loans"));
    companyProfileVO.setIncome_economic_activity(rsPopulateApplicationData.getString("income_economic_activity"));
    companyProfileVO.setInterest_income(rsPopulateApplicationData.getString("interest_income"));
    companyProfileVO.setInvestments(rsPopulateApplicationData.getString("investments"));
    companyProfileVO.setIncome_sources_other(rsPopulateApplicationData.getString("income_sources_other"));
    companyProfileVO.setIncome_sources_other_yes(rsPopulateApplicationData.getString("income_sources_other_yes"));
    companyProfileVO.setInsured_amount(rsPopulateApplicationData.getString("insured_amount"));
    companyProfileVO.setInsured_currency(rsPopulateApplicationData.getString("insured_currency"));
    companyProfileVO.setCountryOfRegistration(rsPopulateApplicationData.getString("countryofregistration"));
    companyProfileVO.setCompanyProfile_addressVOMap(populateCompanyDetails(applicationManagerVO.getApplicationId()));
    companyProfileVO.setCompanyProfile_contactInfoVOMap(populateContactInformation(applicationManagerVO.getApplicationId()));

    //Set value for OwnershipProfile
    ownershipProfileVO.setApplicationid(rsPopulateApplicationData.getString("application_id"));
    ownershipProfileVO.setNumOfShareholders(rsPopulateApplicationData.getString("numOfShareholders"));
    ownershipProfileVO.setNumOfCorporateShareholders(rsPopulateApplicationData.getString("numOfCorporateShareholders"));
    ownershipProfileVO.setNumOfDirectors(rsPopulateApplicationData.getString("numOfDirectors"));
    ownershipProfileVO.setNumOfAuthrisedSignatory(rsPopulateApplicationData.getString("numOfAuthrisedSignatory"));
    ownershipProfileVO.setOwnerShipProfileSaved(rsPopulateApplicationData.getString("isownershipprofilesaved"));
    ownershipProfileVO.setOwnershipProfileDetailsVOMap(populateOwnershipProfileDetails(applicationManagerVO.getApplicationId()));

    //Set value for BusinessProfile
    businessProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    businessProfileVO.setForeigntransactions_us(rsPopulateApplicationData.getString("foreigntransactions_us"));
    businessProfileVO.setForeigntransactions_Europe(rsPopulateApplicationData.getString("foreigntransactions_Europe"));
    businessProfileVO.setForeigntransactions_Asia(rsPopulateApplicationData.getString("foreigntransactions_Asia"));
    businessProfileVO.setForeigntransactions_cis(rsPopulateApplicationData.getString("foreigntransactions_cis"));
    businessProfileVO.setForeigntransactions_canada(rsPopulateApplicationData.getString("foreigntransactions_canada"));
    businessProfileVO.setForeigntransactions_uk(rsPopulateApplicationData.getString("foreigntransactions_uk"));
    businessProfileVO.setForeigntransactions_RestoftheWorld(rsPopulateApplicationData.getString("foreigntransactions_RestoftheWorld"));
    businessProfileVO.setMethodofacceptance_moto(rsPopulateApplicationData.getString("methodofacceptance_moto"));
    businessProfileVO.setMethodofacceptance_internet(rsPopulateApplicationData.getString("methodofacceptance_internet"));
    businessProfileVO.setMethodofacceptance_swipe(rsPopulateApplicationData.getString("methodofacceptance_swipe"));
    businessProfileVO.setAverageticket(rsPopulateApplicationData.getString("averageticket"));
    businessProfileVO.setHighestticket(rsPopulateApplicationData.getString("highestticket"));
    businessProfileVO.setUrls(rsPopulateApplicationData.getString("urls"));
    businessProfileVO.setDescriptor(rsPopulateApplicationData.getString("descriptor_creditcardstmt"));
    businessProfileVO.setDescriptionofproducts(rsPopulateApplicationData.getString("descriptionofproducts"));
    businessProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));
    businessProfileVO.setRecurringservices(rsPopulateApplicationData.getString("recurringservices"));
    businessProfileVO.setRecurringservicesyes(rsPopulateApplicationData.getString("recurringservicesyes"));
    businessProfileVO.setIsacallcenterusedyes(rsPopulateApplicationData.getString("isacallcenterusedyes"));
    businessProfileVO.setIsafulfillmenthouseused(rsPopulateApplicationData.getString("isafulfillmenthouseused"));
    businessProfileVO.setIsafulfillmenthouseused_yes(rsPopulateApplicationData.getString("isafulfillmenthouseused_yes"));
    businessProfileVO.setCardtypesaccepted_visa(rsPopulateApplicationData.getString("cardtypesaccepted_visa"));
    businessProfileVO.setCardtypesaccepted_mastercard(rsPopulateApplicationData.getString("cardtypesaccepted_mastercard"));
    businessProfileVO.setCardtypesaccepted_americanexpress(rsPopulateApplicationData.getString("cardtypesaccepted_americanexpress"));
    businessProfileVO.setCardtypesaccepted_discover(rsPopulateApplicationData.getString("cardtypesaccepted_discover"));
    businessProfileVO.setCardtypesaccepted_diners(rsPopulateApplicationData.getString("cardtypesaccepted_diners"));
    businessProfileVO.setCardtypesaccepted_jcb(rsPopulateApplicationData.getString("cardtypesaccepted_jcb"));
    businessProfileVO.setCardtypesaccepted_rupay(rsPopulateApplicationData.getString("cardtypesaccepted_rupay"));
    businessProfileVO.setCardtypesaccepted_other(rsPopulateApplicationData.getString("cardtypesaccepted_other"));
    businessProfileVO.setCardtypesaccepted_other_yes(rsPopulateApplicationData.getString("cardtypesaccepted_other_yes"));
    businessProfileVO.setBusinessProfileSaved(rsPopulateApplicationData.getString("isbuisnessprofilesaved"));

    //businessProfileVO.setSizeofcustomer_Database(rsPopulateApplicationData.getString("sizeofcustomer_Database"));
    //businessProfileVO.setTopfivecountries(rsPopulateApplicationData.getString("topfivecountries"));
    businessProfileVO.setKyc_processes(rsPopulateApplicationData.getString("kyc_processes"));
    //businessProfileVO.setCustomer_account(rsPopulateApplicationData.getString("customer_account"));
    businessProfileVO.setVisa_cardlogos(rsPopulateApplicationData.getString("visa_cardlogos"));
    businessProfileVO.setMaster_cardlogos(rsPopulateApplicationData.getString("master_cardlogos"));
    businessProfileVO.setThreeD_secure_compulsory(rsPopulateApplicationData.getString("threeD_secure_compulsory"));
    businessProfileVO.setPrice_displayed(rsPopulateApplicationData.getString("price_displayed"));
    businessProfileVO.setTransaction_currency(rsPopulateApplicationData.getString("transaction_currency"));
    businessProfileVO.setCardholder_asked(rsPopulateApplicationData.getString("cardholder_asked"));
    businessProfileVO.setDynamic_descriptors(rsPopulateApplicationData.getString("dynamic_descriptors"));
    businessProfileVO.setShopping_cart(rsPopulateApplicationData.getString("shopping_cart"));
    businessProfileVO.setShopping_cart_details(rsPopulateApplicationData.getString("shopping_cart_details"));
    businessProfileVO.setPricing_policies_website(rsPopulateApplicationData.getString("pricing_policies_website"));
    businessProfileVO.setPricing_policies_website_yes(rsPopulateApplicationData.getString("pricing_policies_website_yes"));
    businessProfileVO.setFulfillment_timeframe(rsPopulateApplicationData.getString("fulfillment_timeframe"));
    businessProfileVO.setGoods_policy(rsPopulateApplicationData.getString("goods_policy"));
    businessProfileVO.setMCC_Ctegory(rsPopulateApplicationData.getString("MCC_Ctegory"));
    // businessProfileVO.setTraffic_countries_us(rsPopulateApplicationData.getString("traffic_countries_us"));
    //businessProfileVO.setTraffic_countries_Europe(rsPopulateApplicationData.getString("traffic_countries_Europe"));
    // businessProfileVO.setTraffic_countries_Asia(rsPopulateApplicationData.getString("traffic_countries_Asia"));
    //businessProfileVO.setTraffic_countries_CIS(rsPopulateApplicationData.getString("traffic_countries_CIS"));
    //businessProfileVO.setTraffic_countries_canada(rsPopulateApplicationData.getString("traffic_countries_canada"));
    // businessProfileVO.setTraffic_countries_restworld(rsPopulateApplicationData.getString("traffic_countries_restworld"));
    businessProfileVO.setCountries_blocked(rsPopulateApplicationData.getString("countries_blocked"));
    businessProfileVO.setCountries_blocked_details(rsPopulateApplicationData.getString("countries_blocked_details"));
    businessProfileVO.setCustomer_support(rsPopulateApplicationData.getString("customer_support"));
    // businessProfileVO.setCustomer_support_details(rsPopulateApplicationData.getString("customer_support_details"));

    businessProfileVO.setAffiliate_programs(rsPopulateApplicationData.getString("affiliate_programs"));
    businessProfileVO.setAffiliate_programs_details(rsPopulateApplicationData.getString("affiliate_programs_details"));
    businessProfileVO.setListfraudtools(rsPopulateApplicationData.getString("listfraudtools"));
    businessProfileVO.setListfraudtools_yes(rsPopulateApplicationData.getString("listfraudtools_yes"));
    businessProfileVO.setCustomers_identification(rsPopulateApplicationData.getString("customers_identification"));
    businessProfileVO.setCustomers_identification_yes(rsPopulateApplicationData.getString("customers_identification_yes"));
    businessProfileVO.setCoolingoffperiod(rsPopulateApplicationData.getString("coolingoffperiod"));
    businessProfileVO.setCustomersupport_email(rsPopulateApplicationData.getString("customersupport_email"));
    businessProfileVO.setCustsupportwork_hours(rsPopulateApplicationData.getString("custsupportwork_hours"));
    businessProfileVO.setTechnical_contact(rsPopulateApplicationData.getString("technical_contact"));

    businessProfileVO.setSecuritypolicy(rsPopulateApplicationData.getString("securitypolicy"));
    businessProfileVO.setConfidentialitypolicy(rsPopulateApplicationData.getString("confidentialitypolicy"));
    businessProfileVO.setApplicablejurisdictions(rsPopulateApplicationData.getString("applicablejurisdictions"));
    businessProfileVO.setPrivacy_anonymity_dataprotection(rsPopulateApplicationData.getString("privacy_anonymity_dataprotection"));

    businessProfileVO.setApp_Services(rsPopulateApplicationData.getString("App_Services"));
    businessProfileVO.setAgency_employed(rsPopulateApplicationData.getString("agency_employed"));
    businessProfileVO.setAgency_employed_yes(rsPopulateApplicationData.getString("agency_employed_yes"));
    businessProfileVO.setProduct_requires(rsPopulateApplicationData.getString("product_requires"));
    //ADD new
    businessProfileVO.setLowestticket(rsPopulateApplicationData.getString("lowestticket"));

    //add NEW
    businessProfileVO.setTimeframe(rsPopulateApplicationData.getString("timeframe"));
    businessProfileVO.setLivechat(rsPopulateApplicationData.getString("livechat"));

    //Add new
    businessProfileVO.setLoginId(rsPopulateApplicationData.getString("login_id"));
    businessProfileVO.setPassWord(rsPopulateApplicationData.getString("password"));
    businessProfileVO.setIs_website_live(rsPopulateApplicationData.getString("is_website_live"));
    businessProfileVO.setTest_link(rsPopulateApplicationData.getString("test_link"));
    businessProfileVO.setCompanyIdentifiable(rsPopulateApplicationData.getString("companyidentifiable"));
    businessProfileVO.setClearlyPresented(rsPopulateApplicationData.getString("clearlypresented"));
    businessProfileVO.setTrackingNumber(rsPopulateApplicationData.getString("trackingnumber"));
    businessProfileVO.setDomainsOwned(rsPopulateApplicationData.getString("domainsowned"));
    businessProfileVO.setDomainsOwned_no(rsPopulateApplicationData.getString("domainsowned_no"));
    businessProfileVO.setSslSecured(rsPopulateApplicationData.getString("sslsecured"));
    businessProfileVO.setCopyright(rsPopulateApplicationData.getString("copyright"));
    businessProfileVO.setSourceContent(rsPopulateApplicationData.getString("sourcecontent"));
    businessProfileVO.setDirectMail(rsPopulateApplicationData.getString("directmail"));
    businessProfileVO.setYellowPages(rsPopulateApplicationData.getString("Yellowpages"));
    businessProfileVO.setRadioTv(rsPopulateApplicationData.getString("radiotv"));
    businessProfileVO.setInternet(rsPopulateApplicationData.getString("internet"));
    businessProfileVO.setNetworking(rsPopulateApplicationData.getString("networking"));
    businessProfileVO.setOutboundTelemarketing(rsPopulateApplicationData.getString("outboundtelemarketing"));
    businessProfileVO.setInHouseLocation(rsPopulateApplicationData.getString("inhouselocation"));
    businessProfileVO.setContactPerson(rsPopulateApplicationData.getString("contactperson"));
    businessProfileVO.setShippingContactemail(rsPopulateApplicationData.getString("shipping_contactemail"));
    businessProfileVO.setOtherLocation(rsPopulateApplicationData.getString("otherlocation"));
    businessProfileVO.setMainSuppliers(rsPopulateApplicationData.getString("mainsuppliers"));
    businessProfileVO.setShipmentAssured(rsPopulateApplicationData.getString("shipmentassured"));
    businessProfileVO.setBillingModel(rsPopulateApplicationData.getString("billing_model"));
    businessProfileVO.setBillingTimeFrame(rsPopulateApplicationData.getString("billing_timeframe"));
    businessProfileVO.setRecurringAmount(rsPopulateApplicationData.getString("recurring_amount"));
    businessProfileVO.setAutomaticRecurring(rsPopulateApplicationData.getString("automatic_recurring"));
    businessProfileVO.setMultipleMembership(rsPopulateApplicationData.getString("multiple_membership"));
    businessProfileVO.setFreeMembership(rsPopulateApplicationData.getString("free_membership"));
    businessProfileVO.setCreditCardRequired(rsPopulateApplicationData.getString("creditcard_Required"));
    businessProfileVO.setAutomaticallyBilled(rsPopulateApplicationData.getString("automatically_billed"));
    businessProfileVO.setPreAuthorization(rsPopulateApplicationData.getString("pre_authorization"));
    businessProfileVO.setMerchantCode(rsPopulateApplicationData.getString("merchantcode"));
    businessProfileVO.setIpaddress(rsPopulateApplicationData.getString("ipaddress"));


    // Wirecard requirement added in Business Profile
    businessProfileVO.setShopsystem_plugin(rsPopulateApplicationData.getString("shopsystem_plugin"));
    businessProfileVO.setDirect_debit_sepa(rsPopulateApplicationData.getString("direct_debit_sepa"));
    businessProfileVO.setAlternative_payments(rsPopulateApplicationData.getString("alternative_payments"));
    businessProfileVO.setRisk_management(rsPopulateApplicationData.getString("risk_management"));
    businessProfileVO.setPayment_engine(rsPopulateApplicationData.getString("payment_engine"));
    businessProfileVO.setWebhost_company_name(rsPopulateApplicationData.getString("webhost_company_name"));
    businessProfileVO.setWebhost_phone(rsPopulateApplicationData.getString("webhost_phone"));
    businessProfileVO.setWebhost_email(rsPopulateApplicationData.getString("webhost_email"));
    businessProfileVO.setWebhost_website(rsPopulateApplicationData.getString("webhost_website"));
    businessProfileVO.setWebhost_address(rsPopulateApplicationData.getString("webhost_address"));
    businessProfileVO.setPayment_company_name(rsPopulateApplicationData.getString("payment_company_name"));
    businessProfileVO.setPayment_phone(rsPopulateApplicationData.getString("payment_phone"));
    businessProfileVO.setPayment_email(rsPopulateApplicationData.getString("payment_email"));
    businessProfileVO.setPayment_website(rsPopulateApplicationData.getString("payment_website"));
    businessProfileVO.setPayment_address(rsPopulateApplicationData.getString("payment_address"));
    businessProfileVO.setCallcenter_phone(rsPopulateApplicationData.getString("callcenter_phone"));
    businessProfileVO.setCallcenter_email(rsPopulateApplicationData.getString("callcenter_email"));
    businessProfileVO.setCallcenter_website(rsPopulateApplicationData.getString("callcenter_website"));
    businessProfileVO.setCallcenter_address(rsPopulateApplicationData.getString("callcenter_address"));
    businessProfileVO.setShoppingcart_company_name(rsPopulateApplicationData.getString("shoppingcart_company_name"));
    businessProfileVO.setShoppingcart_phone(rsPopulateApplicationData.getString("shoppingcart_phone"));
    businessProfileVO.setShoppingcart_email(rsPopulateApplicationData.getString("shoppingcart_email"));
    businessProfileVO.setShoppingcart_website(rsPopulateApplicationData.getString("shoppingcart_website"));
    businessProfileVO.setShoppingcart_address(rsPopulateApplicationData.getString("shoppingcart_address"));
    businessProfileVO.setSeasonal_fluctuating(rsPopulateApplicationData.getString("seasonal_fluctuating"));
    businessProfileVO.setSeasonal_fluctuating_yes(rsPopulateApplicationData.getString("seasonal_fluctuating_yes"));
    businessProfileVO.setPaymenttype_credit(rsPopulateApplicationData.getString("paymenttype_credit"));
    businessProfileVO.setPaymenttype_debit(rsPopulateApplicationData.getString("paymenttype_debit"));
    businessProfileVO.setPaymenttype_netbanking(rsPopulateApplicationData.getString("paymenttype_netbanking"));
    businessProfileVO.setPaymenttype_wallet(rsPopulateApplicationData.getString("paymenttype_wallet"));
    businessProfileVO.setPaymenttype_alternate(rsPopulateApplicationData.getString("paymenttype_alternate"));
    businessProfileVO.setCreditor_id(rsPopulateApplicationData.getString("creditor_id"));
    businessProfileVO.setPayment_delivery(rsPopulateApplicationData.getString("payment_delivery"));
    businessProfileVO.setPayment_delivery_otheryes(rsPopulateApplicationData.getString("payment_delivery_otheryes"));
    businessProfileVO.setGoods_delivery(rsPopulateApplicationData.getString("goods_delivery"));
    businessProfileVO.setTerminal_type(rsPopulateApplicationData.getString("terminal_type"));
    businessProfileVO.setTerminal_type_otheryes(rsPopulateApplicationData.getString("terminal_type_otheryes"));
    businessProfileVO.setOne_time_percentage(rsPopulateApplicationData.getString("one_time_percentage"));
    businessProfileVO.setMoto_percentage(rsPopulateApplicationData.getString("moto_percentage"));
    businessProfileVO.setInternet_percentage(rsPopulateApplicationData.getString("internet_percentage"));
    businessProfileVO.setSwipe_percentage(rsPopulateApplicationData.getString("swipe_percentage"));
    businessProfileVO.setRecurring_percentage(rsPopulateApplicationData.getString("recurring_percentage"));
    businessProfileVO.setThreedsecure_percentage(rsPopulateApplicationData.getString("threedsecure_percentage"));

    businessProfileVO.setCardvolume_visa(rsPopulateApplicationData.getString("cardvolume_visa"));
    businessProfileVO.setCardvolume_mastercard(rsPopulateApplicationData.getString("cardvolume_mastercard"));
    businessProfileVO.setCardvolume_americanexpress(rsPopulateApplicationData.getString("cardvolume_americanexpress"));
    businessProfileVO.setCardvolume_dinner(rsPopulateApplicationData.getString("cardvolume_dinner"));
    businessProfileVO.setCardvolume_other(rsPopulateApplicationData.getString("cardvolume_other"));
    businessProfileVO.setCardvolume_discover(rsPopulateApplicationData.getString("cardvolume_discover"));
    businessProfileVO.setCardvolume_rupay(rsPopulateApplicationData.getString("cardvolume_rupay"));
    businessProfileVO.setCardvolume_jcb(rsPopulateApplicationData.getString("cardvolume_jcb"));
    businessProfileVO.setPayment_type_yes(rsPopulateApplicationData.getString("payment_type_yes"));
    businessProfileVO.setOrderconfirmation_post(rsPopulateApplicationData.getString("orderconfirmation_post"));
    businessProfileVO.setOrderconfirmation_email(rsPopulateApplicationData.getString("orderconfirmation_email"));
    businessProfileVO.setOrderconfirmation_sms(rsPopulateApplicationData.getString("orderconfirmation_sms"));
    businessProfileVO.setOrderconfirmation_other(rsPopulateApplicationData.getString("orderconfirmation_other"));
    businessProfileVO.setOrderconfirmation_other_yes(rsPopulateApplicationData.getString("orderconfirmation_other_yes"));
    businessProfileVO.setPhysicalgoods_delivered(rsPopulateApplicationData.getString("physicalgoods_delivered"));
    businessProfileVO.setViainternetgoods_delivered(rsPopulateApplicationData.getString("viainternetgoods_delivered"));

    //Set value for BankProfile
    bankProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    bankProfileVO.setCurrencyrequested_productssold(rsPopulateApplicationData.getString("currencyrequested_productssold"));
    bankProfileVO.setCurrencyrequested_bankaccount(rsPopulateApplicationData.getString("currencyrequested_bankaccount"));
    bankProfileVO.setBankinfo_bic(rsPopulateApplicationData.getString("bankinfo_bic"));
    bankProfileVO.setBankinfo_bank_name(rsPopulateApplicationData.getString("bankinfo_bank_name"));
    bankProfileVO.setBankinfo_bankaddress(rsPopulateApplicationData.getString("bankinfo_bankaddress"));
    bankProfileVO.setBankinfo_bankphonenumber(rsPopulateApplicationData.getString("bankinfo_bankphonenumber"));
    bankProfileVO.setBankinfo_aba_routingcode(rsPopulateApplicationData.getString("bankinfo_aba_routingcode"));
    bankProfileVO.setBankinfo_accountholder(rsPopulateApplicationData.getString("bankinfo_accountholder"));
    bankProfileVO.setBankinfo_accountnumber(rsPopulateApplicationData.getString("bankinfo_accountnumber"));
    bankProfileVO.setBankinfo_IBAN(rsPopulateApplicationData.getString("bankinfo_IBAN"));
    bankProfileVO.setBankinfo_contactperson(rsPopulateApplicationData.getString("bankinfo_contactperson"));
    bankProfileVO.setIscurrencywisebankinfo(rsPopulateApplicationData.getString("iscurrencywisebankinfo"));
    bankProfileVO.setBankinfo_currency(rsPopulateApplicationData.getString("bankinfo_currency"));
    bankProfileVO.setCurrencywisebankinfo_id(rsPopulateApplicationData.getString("currencywisebankinfo_id"));

    bankProfileVO.setProcessinghistory_id(rsPopulateApplicationData.getString("processinghistory_id"));
    bankProfileVO.setSalesvolume_lastmonth(rsPopulateApplicationData.getString("salesvolume_lastmonth"));
    bankProfileVO.setSalesvolume_2monthsago(rsPopulateApplicationData.getString("salesvolume_2monthsago"));
    bankProfileVO.setSalesvolume_3monthsago(rsPopulateApplicationData.getString("salesvolume_3monthsago"));
    bankProfileVO.setSalesvolume_4monthsago(rsPopulateApplicationData.getString("salesvolume_4monthsago"));
    bankProfileVO.setSalesvolume_5monthsago(rsPopulateApplicationData.getString("salesvolume_5monthsago"));
    bankProfileVO.setSalesvolume_6monthsago(rsPopulateApplicationData.getString("salesvolume_6monthsago"));
    bankProfileVO.setSalesvolume_12monthsago(rsPopulateApplicationData.getString("salesvolume_12monthsago"));
    bankProfileVO.setSalesvolume_year2(rsPopulateApplicationData.getString("salesvolume_year2"));
    bankProfileVO.setSalesvolume_year3(rsPopulateApplicationData.getString("salesvolume_year3"));
    bankProfileVO.setNumberoftransactions_lastmonth(rsPopulateApplicationData.getString("numberoftransactions_lastmonth"));
    bankProfileVO.setNumberoftransactions_2monthsago(rsPopulateApplicationData.getString("numberoftransactions_2monthsago"));
    bankProfileVO.setNumberoftransactions_3monthsago(rsPopulateApplicationData.getString("numberoftransactions_3monthsago"));
    bankProfileVO.setNumberoftransactions_4monthsago(rsPopulateApplicationData.getString("numberoftransactions_4monthsago"));
    bankProfileVO.setNumberoftransactions_5monthsago(rsPopulateApplicationData.getString("numberoftransactions_5monthsago"));
    bankProfileVO.setNumberoftransactions_6monthsago(rsPopulateApplicationData.getString("numberoftransactions_6monthsago"));
    bankProfileVO.setNumberoftransactions_12monthsago(rsPopulateApplicationData.getString("numberoftransactions_12monthsago"));
    bankProfileVO.setNumberoftransactions_year2(rsPopulateApplicationData.getString("numberoftransactions_year2"));
    bankProfileVO.setNumberoftransactions_year3(rsPopulateApplicationData.getString("numberoftransactions_year3"));
    bankProfileVO.setChargebackvolume_lastmonth(rsPopulateApplicationData.getString("chargebackvolume_lastmonth"));
    bankProfileVO.setChargebackvolume_2monthsago(rsPopulateApplicationData.getString("chargebackvolume_2monthsago"));
    bankProfileVO.setChargebackvolume_3monthsago(rsPopulateApplicationData.getString("chargebackvolume_3monthsago"));
    bankProfileVO.setChargebackvolume_4monthsago(rsPopulateApplicationData.getString("chargebackvolume_4monthsago"));
    bankProfileVO.setChargebackvolume_5monthsago(rsPopulateApplicationData.getString("chargebackvolume_5monthsago"));
    bankProfileVO.setChargebackvolume_6monthsago(rsPopulateApplicationData.getString("chargebackvolume_6monthsago"));
    bankProfileVO.setChargebackvolume_12monthsago(rsPopulateApplicationData.getString("chargebackvolume_12monthsago"));
    bankProfileVO.setChargebackvolume_year2(rsPopulateApplicationData.getString("chargebackvolume_year2"));
    bankProfileVO.setChargebackvolume_year3(rsPopulateApplicationData.getString("chargebackvolume_year3"));
    bankProfileVO.setNumberofchargebacks_lastmonth(rsPopulateApplicationData.getString("numberofchargebacks_lastmonth"));
    bankProfileVO.setNumberofchargebacks_2monthsago(rsPopulateApplicationData.getString("numberofchargebacks_2monthsago"));
    bankProfileVO.setNumberofchargebacks_3monthsago(rsPopulateApplicationData.getString("numberofchargebacks_3monthsago"));
    bankProfileVO.setNumberofchargebacks_4monthsago(rsPopulateApplicationData.getString("numberofchargebacks_4monthsago"));
    bankProfileVO.setNumberofchargebacks_5monthsago(rsPopulateApplicationData.getString("numberofchargebacks_5monthsago"));
    bankProfileVO.setNumberofchargebacks_6monthsago(rsPopulateApplicationData.getString("numberofchargebacks_6monthsago"));
    bankProfileVO.setNumberofchargebacks_12monthsago(rsPopulateApplicationData.getString("numberofchargebacks_12monthsago"));
    bankProfileVO.setNumberofchargebacks_year2(rsPopulateApplicationData.getString("numberofchargebacks_year2"));
    bankProfileVO.setNumberofchargebacks_year3(rsPopulateApplicationData.getString("numberofchargebacks_year3"));
    bankProfileVO.setRefundsvolume_lastmonth(rsPopulateApplicationData.getString("refundsvolume_lastmonth"));
    bankProfileVO.setRefundsvolume_2monthsago(rsPopulateApplicationData.getString("refundsvolume_2monthsago"));
    bankProfileVO.setRefundsvolume_3monthsago(rsPopulateApplicationData.getString("refundsvolume_3monthsago"));
    bankProfileVO.setRefundsvolume_4monthsago(rsPopulateApplicationData.getString("refundsvolume_4monthsago"));
    bankProfileVO.setRefundsvolume_5monthsago(rsPopulateApplicationData.getString("refundsvolume_5monthsago"));
    bankProfileVO.setRefundsvolume_6monthsago(rsPopulateApplicationData.getString("refundsvolume_6monthsago"));
    bankProfileVO.setRefundsvolume_12monthsago(rsPopulateApplicationData.getString("refundsvolume_12monthsago"));
    bankProfileVO.setRefundsvolume_year2(rsPopulateApplicationData.getString("refundsvolume_year2"));
    bankProfileVO.setRefundsvolume_year3(rsPopulateApplicationData.getString("refundsvolume_year3"));
    bankProfileVO.setNumberofrefunds_lastmonth(rsPopulateApplicationData.getString("numberofrefunds_lastmonth"));
    bankProfileVO.setNumberofrefunds_2monthsago(rsPopulateApplicationData.getString("numberofrefunds_2monthsago"));
    bankProfileVO.setNumberofrefunds_3monthsago(rsPopulateApplicationData.getString("numberofrefunds_3monthsago"));
    bankProfileVO.setNumberofrefunds_4monthsago(rsPopulateApplicationData.getString("numberofrefunds_4monthsago"));
    bankProfileVO.setNumberofrefunds_5monthsago(rsPopulateApplicationData.getString("numberofrefunds_5monthsago"));
    bankProfileVO.setNumberofrefunds_6monthsago(rsPopulateApplicationData.getString("numberofrefunds_6monthsago"));
    bankProfileVO.setNumberofrefunds_12monthsago(rsPopulateApplicationData.getString("numberofrefunds_12monthsago"));
    bankProfileVO.setNumberofrefunds_year2(rsPopulateApplicationData.getString("numberofrefunds_year2"));
    bankProfileVO.setNumberofrefunds_year3(rsPopulateApplicationData.getString("numberofrefunds_year3"));

    bankProfileVO.setChargebackratio_lastmonth(rsPopulateApplicationData.getString("chargebackratio_lastmonth"));
    bankProfileVO.setChargebackratio_2monthsago(rsPopulateApplicationData.getString("chargebackratio_2monthsago"));
    bankProfileVO.setChargebackratio_3monthsago(rsPopulateApplicationData.getString("chargebackratio_3monthsago"));
    bankProfileVO.setChargebackratio_4monthsago(rsPopulateApplicationData.getString("chargebackratio_4monthsago"));
    bankProfileVO.setChargebackratio_5monthsago(rsPopulateApplicationData.getString("chargebackratio_5monthsago"));
    bankProfileVO.setChargebackratio_6monthsago(rsPopulateApplicationData.getString("chargebackratio_6monthsago"));
    bankProfileVO.setChargebackratio_12monthsago(rsPopulateApplicationData.getString("chargebackratio_12monthsago"));
    bankProfileVO.setChargebackratio_year2(rsPopulateApplicationData.getString("chargebackratio_year2"));
    bankProfileVO.setChargebackratio_year3(rsPopulateApplicationData.getString("chargebackratio_year3"));
    bankProfileVO.setRefundratio_lastmonth(rsPopulateApplicationData.getString("refundratio_lastmonth"));
    bankProfileVO.setRefundratio_2monthsago(rsPopulateApplicationData.getString("refundratio_2monthsago"));
    bankProfileVO.setRefundratio_3monthsago(rsPopulateApplicationData.getString("refundratio_3monthsago"));
    bankProfileVO.setRefundratio_4monthsago(rsPopulateApplicationData.getString("refundratio_4monthsago"));
    bankProfileVO.setRefundratio_5monthsago(rsPopulateApplicationData.getString("refundratio_5monthsago"));
    bankProfileVO.setRefundratio_6monthsago(rsPopulateApplicationData.getString("refundratio_6monthsago"));
    bankProfileVO.setRefundratio_12monthsago(rsPopulateApplicationData.getString("refundratio_12monthsago"));
    bankProfileVO.setRefundratio_year2(rsPopulateApplicationData.getString("refundratio_year2"));
    bankProfileVO.setRefundratio_year3(rsPopulateApplicationData.getString("refundratio_year3"));

    bankProfileVO.setCurrency(rsPopulateApplicationData.getString("currency"));
    bankProfileVO.setBank_account_currencies(rsPopulateApplicationData.getString("bank_account_currencies"));
    //bankProfileVO.setProduct_sold_currencies(rsPopulateApplicationData.getString("product_sold_currencies"));

    bankProfileVO.setAquirer(rsPopulateApplicationData.getString("aquirer"));
    bankProfileVO.setReasonaquirer(rsPopulateApplicationData.getString("reason_aquirer"));
    bankProfileVO.setCustomer_trans_data(rsPopulateApplicationData.getString("customer_trans_data"));
    bankProfileVO.setBankProfileSaved(rsPopulateApplicationData.getString("isbankprofilesaved"));
    bankProfileVO.setIsProcessingHistory(rsPopulateApplicationData.getString("isprocessinghistory"));
    bankProfileVO.setProcessinghistory_creation_time(rsPopulateApplicationData.getString("creation_time"));
    bankProfileVO.setProcessinghistory_updation_time(rsPopulateApplicationData.getString("updation_time"));

    //Set value for CardHolderProfile
    cardholderProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    cardholderProfileVO.setCompliance_swapp(rsPopulateApplicationData.getString("compliance_swapp"));
    cardholderProfileVO.setCompliance_thirdpartyappform(rsPopulateApplicationData.getString("compliance_thirdpartyappform"));
    cardholderProfileVO.setCompliance_thirdpartysoft(rsPopulateApplicationData.getString("compliance_thirdpartysoft"));
    cardholderProfileVO.setCompliance_version(rsPopulateApplicationData.getString("compliance_version"));
    cardholderProfileVO.setCompliance_companiesorgateways(rsPopulateApplicationData.getString("compliance_companiesorgateways"));
    cardholderProfileVO.setCompliance_companiesorgateways_yes(rsPopulateApplicationData.getString("compliance_companiesorgateways_yes"));
    cardholderProfileVO.setCompliance_electronically(rsPopulateApplicationData.getString("compliance_electronically"));
    cardholderProfileVO.setCompliance_carddatastored(rsPopulateApplicationData.getString("compliance_carddatastored"));
    cardholderProfileVO.setCompliance_cispcompliant(rsPopulateApplicationData.getString("compliance_cispcompliant"));
    cardholderProfileVO.setCompliance_cispcompliant_yes(rsPopulateApplicationData.getString("compliance_cispcompliant_yes"));
    cardholderProfileVO.setCompliance_pcidsscompliant(rsPopulateApplicationData.getString("compliance_pcidsscompliant"));
    cardholderProfileVO.setCompliance_pcidsscompliant_yes(rsPopulateApplicationData.getString("compliance_pcidsscompliant_yes"));
    cardholderProfileVO.setCompliance_qualifiedsecurityassessor(rsPopulateApplicationData.getString("compliance_qualifiedsecurityassessor"));
    cardholderProfileVO.setCompliance_dateofcompliance(rsPopulateApplicationData.getString("compliance_dateofcompliance"));
    cardholderProfileVO.setCompliance_dateoflastscan(rsPopulateApplicationData.getString("compliance_dateoflastscan"));
    cardholderProfileVO.setCompliance_datacompromise(rsPopulateApplicationData.getString("compliance_datacompromise"));
    cardholderProfileVO.setCompliance_datacompromise_yes(rsPopulateApplicationData.getString("compliance_datacompromise_yes"));
    cardholderProfileVO.setSiteinspection_merchant(rsPopulateApplicationData.getString("siteinspection_merchant"));
    cardholderProfileVO.setSiteinspection_landlord(rsPopulateApplicationData.getString("siteinspection_landlord"));
    cardholderProfileVO.setSiteinspection_buildingtype(rsPopulateApplicationData.getString("siteinspection_buildingtype"));
    cardholderProfileVO.setSiteinspection_areazoned(rsPopulateApplicationData.getString("siteinspection_areazoned"));
    cardholderProfileVO.setSiteinspection_squarefootage(rsPopulateApplicationData.getString("siteinspection_squarefootage"));
    cardholderProfileVO.setSiteinspection_operatebusiness(rsPopulateApplicationData.getString("siteinspection_operatebusiness"));
    cardholderProfileVO.setSiteinspection_principal1(rsPopulateApplicationData.getString("siteinspection_principal1"));
    cardholderProfileVO.setSiteinspection_principal1_date(rsPopulateApplicationData.getString("siteinspection_principal1_date"));
    cardholderProfileVO.setSiteinspection_principal2(rsPopulateApplicationData.getString("siteinspection_principal2"));
    cardholderProfileVO.setSiteinspection_principal2_date(rsPopulateApplicationData.getString("siteinspection_principal2_date"));
    cardholderProfileVO.setCardHolderProfileSaved(rsPopulateApplicationData.getString("iscardholderprofilesaved"));

    //Set value for ExtraDetailsProfile
    extraDetailsProfileVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    extraDetailsProfileVO.setCompany_financialReport(rsPopulateApplicationData.getString("company_financialreport"));
    extraDetailsProfileVO.setCompany_financialReportYes(rsPopulateApplicationData.getString("company_financialreportyes"));
    extraDetailsProfileVO.setFinancialReport_institution(rsPopulateApplicationData.getString("financialreport_institution"));
    extraDetailsProfileVO.setFinancialReport_available(rsPopulateApplicationData.getString("financialreport_available"));
    extraDetailsProfileVO.setFinancialReport_availableYes(rsPopulateApplicationData.getString("financialReport_availableyes"));
    extraDetailsProfileVO.setOwnerSince(rsPopulateApplicationData.getString("ownersince"));
    extraDetailsProfileVO.setSocialSecurity(rsPopulateApplicationData.getString("socialsecurity"));
    extraDetailsProfileVO.setCompany_formParticipation(rsPopulateApplicationData.getString("company_formparticipation"));
    extraDetailsProfileVO.setFinancialObligation(rsPopulateApplicationData.getString("financialobligation"));
    extraDetailsProfileVO.setCompliance_punitiveSanction(rsPopulateApplicationData.getString("compliance_punitivesanction"));
    extraDetailsProfileVO.setCompliance_punitiveSanctionYes(rsPopulateApplicationData.getString("compliance_punitivesanctionyes"));
    extraDetailsProfileVO.setWorkingExperience(rsPopulateApplicationData.getString("workingexperience"));
    extraDetailsProfileVO.setGoodsInsuranceOffered(rsPopulateApplicationData.getString("goodsinsuranceoffered"));
    extraDetailsProfileVO.setFulfillment_productEmail(rsPopulateApplicationData.getString("fulfillment_productemail"));
    extraDetailsProfileVO.setFulfillment_productEmailYes(rsPopulateApplicationData.getString("fulfillment_productemailyes"));
    extraDetailsProfileVO.setBlacklistedAccountClosed(rsPopulateApplicationData.getString("blacklistedaccountclosed"));
    extraDetailsProfileVO.setBlacklistedAccountClosedYes(rsPopulateApplicationData.getString("blacklistedaccountclosedyes"));
    extraDetailsProfileVO.setShiping_deliveryMethod(rsPopulateApplicationData.getString("shiping_deliverymethod"));
    extraDetailsProfileVO.setTransactionMonitoringProcess(rsPopulateApplicationData.getString("transactionmonitoringprocess"));
    extraDetailsProfileVO.setOperationalLicense(rsPopulateApplicationData.getString("operationallicense"));
    extraDetailsProfileVO.setSupervisorregularcontrole(rsPopulateApplicationData.getString("supervisorregularcontrole"));
    extraDetailsProfileVO.setDeedOfAgreement(rsPopulateApplicationData.getString("deedofagreement"));
    extraDetailsProfileVO.setDeedOfAgreementYes(rsPopulateApplicationData.getString("deedofagreementyes"));
    extraDetailsProfileVO.setExtraDetailsProfileSaved(rsPopulateApplicationData.getString("isextradetailsprofile"));

    applicationManagerVO.setCompanyProfileVO(companyProfileVO);
    applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
    applicationManagerVO.setBusinessProfileVO(businessProfileVO);
    applicationManagerVO.setBankProfileVO(bankProfileVO);
    applicationManagerVO.setCardholderProfileVO(cardholderProfileVO);
    applicationManagerVO.setExtradetailsprofileVO(extraDetailsProfileVO);
    logger.debug("query-----"+psPopulateApplicationData);

   }

  }
  catch (SystemError systemError)
  {
   logger.error("System exception while getting Application information::", systemError);
  }
  catch (SQLException e)
  {
   logger.error("Sql Exception while getting Application information::", e);
  }
  finally
  {
   Database.closeConnection(con);
  }

  return applicationManagerVO;
 }


 //insert Speed_status

    /*public boolean insertSpeedStatus(ApplicationManagerVO applicationManagerVO) throws SystemError, Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        int resultSet = 0;
        try
        {
            conn = Database.getConnection();
            query = "INSERT INTO speed_status(application_id,speed_status,speed_user)VALUES(?,?,?)";
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, applicationManagerVO.getApplicationId());
            pstmt.setString(2, applicationManagerVO.getSpeed_status());
            pstmt.setString(3, applicationManagerVO.getSpeed_user());

            resultSet = pstmt.executeUpdate();
            if (resultSet > 0)
            {
                return true;
            }
        }

        catch (SQLException e)
        {
            logger.error("System Error while inserting data", e);
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while inserting data", systemError);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return false;
    }*/

 //update SpeedOption status

   /* public boolean updateSpeedStatus(ApplicationManagerVO applicationManagerVO) throws SystemError, Exception
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        //String query=null;
        String status = "failed";
        try
        {
            conn = Database.getConnection();
            String updatespeedStatus = "update speed_status set speed_status=?,speed_user=? where application_id=? ";
            pstmt = conn.prepareStatement(updatespeedStatus);

            if (functions.isValueNull(applicationManagerVO.getSpeed_status()))
            {
                pstmt.setString(1, applicationManagerVO.getSpeed_status());
            }
            else
            {
                pstmt.setNull(1, Types.VARCHAR);
            }

            pstmt.setString(2, applicationManagerVO.getSpeed_user());
            pstmt.setString(3, applicationManagerVO.getApplicationId());

            int k = pstmt.executeUpdate();
            if (k > 0)
            {
                return true;
            }

        }
        catch (SQLException e)
        {
            logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        logger.debug("Leaving updateSpeedStatus");

        return false;
    }
*/

 //update for Application Manager Status

 public boolean insertApplicationManager(ApplicationManagerVO applicationManagerVO)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  int resultSet = 0;
  try
  {
   conn = Database.getConnection();
   //query = "INSERT INTO application_manager(member_id,status,userApp,MAF_status,KYC_status)VALUES(?,?,?,?,?)";
   query = "INSERT INTO application_manager(member_id,status,maf_user,MAF_status,KYC_status,speed_status,speed_user)VALUES(?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);

   pstmt.setString(1, applicationManagerVO.getMemberId());
   pstmt.setString(2, applicationManagerVO.getStatus());
   pstmt.setString(3, applicationManagerVO.getUser());
   if(functions.isValueNull(applicationManagerVO.getMaf_Status()))
    pstmt.setString(4, applicationManagerVO.getMaf_Status());
   else
    pstmt.setNull(4, Types.VARCHAR);

   if(functions.isValueNull(applicationManagerVO.getKyc_Status()))
    pstmt.setString(5, applicationManagerVO.getKyc_Status());
   else
    pstmt.setNull(5, Types.VARCHAR);

   //added for speed status
   if(functions.isValueNull(applicationManagerVO.getSpeed_status()))
    pstmt.setString(6, applicationManagerVO.getSpeed_status());
   else
    pstmt.setNull(6, Types.VARCHAR);

   if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
    pstmt.setString(7, applicationManagerVO.getSpeed_user());
   else
    pstmt.setNull(7, Types.VARCHAR);
   //end

   resultSet = pstmt.executeUpdate();
   if (resultSet > 0)
   {
    ResultSet result = pstmt.getGeneratedKeys();
    result.next();
    applicationManagerVO.setApplicationId(result.getString(1));
    applicationManagerVO.setApplicationSaved("Y");
    return true;

   }
  }

  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return false;
 }

 public boolean updateAppManagerStatus(ApplicationManagerVO applicationManagerVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  //String query=null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   //String updateappManagerStatus = "update application_manager set status=?,userApp=?,appliedToModify=?,MAF_status =?,KYC_status=? where member_id=? ";
   String updateappManagerStatus = "update application_manager set status=?,maf_user=?,appliedToModify=?,MAF_status =?,KYC_status=?,speed_status=?,speed_user=? where member_id=? ";
   pstmt = conn.prepareStatement(updateappManagerStatus);

   if (functions.isValueNull(applicationManagerVO.getStatus()))
   {
    pstmt.setString(1, applicationManagerVO.getStatus());
   }
   else
   {
    pstmt.setNull(1, Types.VARCHAR);
   }
   if (functions.isValueNull(applicationManagerVO.getUser()))
   {
    pstmt.setString(2, applicationManagerVO.getUser());
   }
   else
   {
    pstmt.setNull(2, Types.VARCHAR);
   }
   if (functions.isValueNull(applicationManagerVO.getAppliedToModify()))
   {
    pstmt.setString(3, applicationManagerVO.getAppliedToModify());
   }
   else
   {
    pstmt.setString(3, "N");
   }
   if (functions.isValueNull(applicationManagerVO.getMaf_Status()))
   {
    pstmt.setString(4, applicationManagerVO.getMaf_Status());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }

   if (functions.isValueNull(applicationManagerVO.getKyc_Status()))
   {
    pstmt.setString(5, applicationManagerVO.getKyc_Status());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   //added for speed status
   if (functions.isValueNull(applicationManagerVO.getSpeed_status()))
   {
    pstmt.setString(6, applicationManagerVO.getSpeed_status());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(applicationManagerVO.getSpeed_user()))
   {
    pstmt.setString(7, applicationManagerVO.getSpeed_user());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   //end
   if (functions.isValueNull(applicationManagerVO.getMemberId()))
   {
    pstmt.setString(8, applicationManagerVO.getMemberId());
   }
   else
   {
    pstmt.setNull(8, Types.INTEGER);
   }
   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateAppManagerStatus");

  return false;
 }

 public boolean insertCompanyProfile(CompanyProfileVO companyProfileVO) throws SystemError
 {
  logger.debug("in insertCompanyProfile");
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO companyprofile(application_id,company_bankruptcy,company_bankruptcydate,company_typeofbusiness,company_lengthoftime_business,company_capitalresources,company_currencylastyear,company_turnoverlastyear,company_turnoverlastyear_unit,company_numberofemployees,License_required,License_Permission,legal_proceeding,startup_business,iscompany_insured,insured_companyname,main_business_partner,loans,income_economic_activity,interest_income,investments,income_sources_other,income_sources_other_yes,insured_amount,insured_currency,countryofregistration)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);
   if (functions.isValueNull(companyProfileVO.getApplicationId()))
   {
    pstmt.setString(1, companyProfileVO.getApplicationId());
   }
   else
   {
    pstmt.setNull(1, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyBankruptcy()))
   {
    pstmt.setString(2, companyProfileVO.getCompanyBankruptcy());
   }
   else
   {
    pstmt.setString(2, "N");
   }
   if (functions.isValueNull(companyProfileVO.getCompanyBankruptcydate()))
   {
    pstmt.setString(3, companyProfileVO.getCompanyBankruptcydate());
   }
   else
   {
    pstmt.setNull(3, Types.TIMESTAMP);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()))
   {
    pstmt.setString(4, companyProfileVO.getCompanyTypeOfBusiness());
   }
   else
   {
    pstmt.setString(4, "NotforProfit");
   }
   if (functions.isValueNull(companyProfileVO.getCompanyLengthOfTimeInBusiness()))
   {
    pstmt.setString(5, companyProfileVO.getCompanyLengthOfTimeInBusiness());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyCapitalResources()))
   {
    pstmt.setString(6, companyProfileVO.getCompanyCapitalResources());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompany_currencylastyear()))
   {
    pstmt.setString(7, companyProfileVO.getCompany_currencylastyear());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyTurnoverLastYear()))
   {
    pstmt.setString(8, companyProfileVO.getCompanyTurnoverLastYear());
   }
   else
   {
    pstmt.setNull(8, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompany_turnoverlastyear_unit()))
   {
    pstmt.setString(9, companyProfileVO.getCompany_turnoverlastyear_unit());
   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyNumberOfEmployees()))
   {
    pstmt.setString(10, companyProfileVO.getCompanyNumberOfEmployees());
   }
   else
   {
    pstmt.setNull(10, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getLicense_required()))
   {
    pstmt.setString(11, companyProfileVO.getLicense_required());
   }
   else
   {
    pstmt.setString(11, "N");
   }
   if (functions.isValueNull(companyProfileVO.getLicense_Permission()))
   {
    pstmt.setString(12, companyProfileVO.getLicense_Permission());
   }
   else
   {
    pstmt.setString(12, "N");
   }
   if(functions.isValueNull(companyProfileVO.getLegalProceeding()))
   {
    pstmt.setString(13,companyProfileVO.getLegalProceeding());
   }
   else
   {
    pstmt.setString(13, "N");
   }
   if(functions.isValueNull(companyProfileVO.getStartup_business()))
   {
    pstmt.setString(14,companyProfileVO.getStartup_business());
   }
   else
   {
    pstmt.setString(14, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIscompany_insured()))
   {
    pstmt.setString(15,companyProfileVO.getIscompany_insured());
   }
   else
   {
    pstmt.setString(15, "N");
   }
   if(functions.isValueNull(companyProfileVO.getInsured_companyname()))
   {
    pstmt.setString(16,companyProfileVO.getInsured_companyname());
   }
   else
   {
    pstmt.setNull(16, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getMain_business_partner()))
   {
    pstmt.setString(17,companyProfileVO.getMain_business_partner());
   }
   else
   {
    pstmt.setNull(17, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getLoans()))
   {
    pstmt.setString(18,companyProfileVO.getLoans());
   }
   else
   {
    pstmt.setString(18, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIncome_economic_activity()))
   {
    pstmt.setString(19,companyProfileVO.getIncome_economic_activity());
   }
   else
   {
    pstmt.setString(19, "N");
   }
   if(functions.isValueNull(companyProfileVO.getInterest_income()))
   {
    pstmt.setString(20,companyProfileVO.getInterest_income());
   }
   else
   {
    pstmt.setString(20, "N");
   }
   if(functions.isValueNull(companyProfileVO.getInvestments()))
   {
    pstmt.setString(21,companyProfileVO.getInvestments());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIncome_sources_other()))
   {
    pstmt.setString(22,companyProfileVO.getIncome_sources_other());
   }
   else
   {
    pstmt.setString(22, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIncome_sources_other_yes()))
   {
    pstmt.setString(23,companyProfileVO.getIncome_sources_other_yes());
   }
   else
   {
    pstmt.setNull(23, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getInsured_amount()))
   {
    pstmt.setString(24,companyProfileVO.getInsured_amount());
   }
   else
   {
    pstmt.setNull(24, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getInsured_currency()))
   {
    pstmt.setString(25,companyProfileVO.getInsured_currency());
   }
   else
   {
    pstmt.setNull(25, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getCountryOfRegistration()))
   {
    pstmt.setString(26,companyProfileVO.getCountryOfRegistration());
   }
   else
   {
    pstmt.setNull(26, Types.VARCHAR);
   }
   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    logger.debug("addressVOMap from ApplicationManagerDAO----"+companyProfileVO.getCompanyProfile_addressVOMap());
    insertAddressAndIdentificationDetails(companyProfileVO.getApplicationId(), companyProfileVO.getCompanyProfile_addressVOMap());
    insertContactDetails(companyProfileVO.getApplicationId(), companyProfileVO.getCompanyProfile_contactInfoVOMap());
    companyProfileVO.setCompanyProfileSaved("Y");
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving createAgentWire");

  return false;
 }

 public boolean insertOwnershipProfile(OwnershipProfileVO ownershipProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO ownershipprofile(application_id,numOfShareholders,numOfCorporateShareholders,numOfDirectors,numOfAuthrisedSignatory,isownershipprofilesaved) VALUES(?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);

   if (functions.isValueNull(ownershipProfileVO.getApplicationid()))
   {
    pstmt.setString(1, ownershipProfileVO.getApplicationid());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfShareholders()))
   {
    pstmt.setString(2, ownershipProfileVO.getNumOfShareholders());
   }
   else
   {
    pstmt.setNull(2, Types.INTEGER);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfCorporateShareholders()))
   {
    pstmt.setString(3, ownershipProfileVO.getNumOfCorporateShareholders());
   }
   else
   {
    pstmt.setNull(3, Types.INTEGER);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfDirectors()))
   {
    pstmt.setString(4, ownershipProfileVO.getNumOfDirectors());
   }
   else
   {
    pstmt.setNull(4, Types.INTEGER);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfAuthrisedSignatory()))
   {
    pstmt.setString(5, ownershipProfileVO.getNumOfAuthrisedSignatory());
   }
   else
   {
    pstmt.setNull(5, Types.INTEGER);
   }
   pstmt.setString(6,"Y");
   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    ownershipProfileVO.setOwnerShipProfileSaved("Y");
    insertOwnershipProfileDetails(ownershipProfileVO.getApplicationid(), ownershipProfileVO.getOwnershipProfileDetailsVOMap());
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving Ownership Profile");

  return false;
 }

 public Map<String,List<BankApplicationMasterVO>> getPartnerBankApplicationMasterVOForMemberId(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy,String partnerID)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs=new HashMap<String, List<BankApplicationMasterVO>>();
  List<BankApplicationMasterVO> bankApplicationMasterVOList=null;
  int counter=1;
  try
  {

   StringBuffer query =new StringBuffer("SELECT bankapplicationid,application_id,member_id,bankfilename,STATUS,remark,bankapplicationmaster.dtstamp,TIMESTAMP,pgtypeid FROM bankapplicationmaster, members WHERE members.memberid = bankapplicationmaster.member_id");
   query.append(" and members.partnerId =?");
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     query.append(" and application_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     query.append(" and bankfilename =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     query.append(" and status =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     query.append(" and remark =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }

    if(functions.isValueNull(groupBy))
     query.append(" GROUP BY "+groupBy);
    if(functions.isValueNull(orderBy))
     query.append(" ORDER BY "+orderBy);
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   logger.debug("Query change 11::::"+query.toString());
   pstmt.setString(counter,partnerID);
   counter++;
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getApplication_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getMember_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getTimestamp() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getPgtypeid() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankapplicationid() );
     counter++;
    }
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    bankApplicationMasterVO=new BankApplicationMasterVO();
    bankApplicationMasterVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    bankApplicationMasterVO.setApplication_id(resultSet.getString("application_id"));
    bankApplicationMasterVO.setMember_id(resultSet.getString("member_id"));
    bankApplicationMasterVO.setPgtypeid(resultSet.getString("pgtypeid"));
    bankApplicationMasterVO.setBankfilename(resultSet.getString("bankfilename"));
    bankApplicationMasterVO.setStatus(resultSet.getString("status"));
    bankApplicationMasterVO.setRemark(resultSet.getString("remark"));
    bankApplicationMasterVO.setDtstamp(resultSet.getString("dtstamp"));
    bankApplicationMasterVO.setTimestamp(resultSet.getString("timestamp"));
    if(bankApplicationMasterVOs.containsKey(bankApplicationMasterVO.getMember_id()))
    {
     logger.debug("already present::::"+bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getMember_id(),bankApplicationMasterVOList);
    }
    else
    {
     logger.debug("1st time adding::::"+bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList=new ArrayList<BankApplicationMasterVO>();
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getMember_id(),bankApplicationMasterVOList);
    }
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return bankApplicationMasterVOs;
 }

 public Map<String,List<BankApplicationMasterVO>> getSuperPartnerBankApplicationMasterVOForMemberId(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy,String partnerID)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs=new HashMap<String, List<BankApplicationMasterVO>>();
  List<BankApplicationMasterVO> bankApplicationMasterVOList=null;
  int counter=1;
  try
  {
   StringBuffer query =new StringBuffer("SELECT bankapplicationid,application_id,member_id,bankfilename,STATUS,remark,ba.dtstamp,TIMESTAMP,pgtypeid FROM members m JOIN bankapplicationmaster ba ON m.memberid = ba.member_id JOIN partners p ON m.partnerId =p.partnerId ");
   query.append(" WHERE (m.partnerId =? OR p.superadminid=?)");
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     query.append(" and application_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     query.append(" and bankfilename =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     query.append(" and status =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     query.append(" and remark =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }

    if(functions.isValueNull(groupBy))
     query.append(" GROUP BY "+groupBy);
    if(functions.isValueNull(orderBy))
     query.append(" ORDER BY "+orderBy);
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   logger.debug("Query change 11::::"+query.toString());
   pstmt.setString(counter,partnerID);
   counter++;
   pstmt.setString(counter,partnerID);
   counter++;
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getApplication_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getMember_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getTimestamp() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getPgtypeid() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankapplicationid() );
     counter++;
    }
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    bankApplicationMasterVO=new BankApplicationMasterVO();
    bankApplicationMasterVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    bankApplicationMasterVO.setApplication_id(resultSet.getString("application_id"));
    bankApplicationMasterVO.setMember_id(resultSet.getString("member_id"));
    bankApplicationMasterVO.setPgtypeid(resultSet.getString("pgtypeid"));
    bankApplicationMasterVO.setBankfilename(resultSet.getString("bankfilename"));
    bankApplicationMasterVO.setStatus(resultSet.getString("status"));
    bankApplicationMasterVO.setRemark(resultSet.getString("remark"));
    bankApplicationMasterVO.setDtstamp(resultSet.getString("dtstamp"));
    bankApplicationMasterVO.setTimestamp(resultSet.getString("timestamp"));
    if(bankApplicationMasterVOs.containsKey(bankApplicationMasterVO.getMember_id()))
    {
     logger.debug("already present::::"+bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getMember_id(),bankApplicationMasterVOList);
    }
    else
    {
     logger.debug("1st time adding::::"+bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList=new ArrayList<BankApplicationMasterVO>();
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getMember_id(),bankApplicationMasterVOList);
    }
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return bankApplicationMasterVOs;

 }

 public boolean insertBusinessProfile(BusinessProfileVO businessProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO applicationmanager_businessprofile(application_id,foreigntransactions_us,foreigntransactions_Europe,foreigntransactions_Asia,foreigntransactions_RestoftheWorld,methodofacceptance_moto,methodofacceptance_internet,methodofacceptance_swipe,averageticket,highestticket,urls,descriptor_creditcardstmt,descriptionofproducts,recurringservices,recurringservicesyes,isacallcenterusedyes,isafulfillmenthouseused,isafulfillmenthouseused_yes,cardtypesaccepted_visa,cardtypesaccepted_mastercard,cardtypesaccepted_americanexpress,cardtypesaccepted_discover,cardtypesaccepted_diners,cardtypesaccepted_jcb,cardtypesaccepted_other,cardtypesaccepted_other_yes,kyc_processes,visa_cardlogos,threeD_secure_compulsory,price_displayed,transaction_currency,cardholder_asked,dynamic_descriptors,shopping_cart,shopping_cart_details,pricing_policies_website,fulfillment_timeframe,goods_policy,MCC_Ctegory,countries_blocked,countries_blocked_details,customer_support,affiliate_programs,affiliate_programs_details,listfraudtools,customers_identification,coolingoffperiod,customersupport_email,custsupportwork_hours,technical_contact,foreigntransactions_cis,foreigntransactions_canada,securitypolicy,confidentialitypolicy,applicablejurisdictions,privacy_anonymity_dataprotection,App_Services,product_requires,lowestticket,timeframe,livechat,login_id,password,companyidentifiable,clearlypresented,trackingnumber,domainsowned,sslsecured,copyright,sourcecontent,directmail,Yellowpages,radiotv,internet,networking,outboundtelemarketing,inhouselocation,contactperson,otherlocation,mainsuppliers,shipmentassured,billing_model,billing_timeframe,recurring_amount,automatic_recurring,multiple_membership,free_membership,creditcard_Required,automatically_billed,pre_authorization,merchantcode,ipaddress,shopsystem_plugin,direct_debit_sepa,alternative_payments,risk_management,payment_engine,webhost_company_name,webhost_phone,webhost_email,webhost_website,webhost_address,payment_company_name,payment_phone,payment_email,payment_website,payment_address,callcenter_phone,callcenter_email,callcenter_website,callcenter_address,shoppingcart_company_name,shoppingcart_phone,shoppingcart_email,shoppingcart_website,shoppingcart_address,seasonal_fluctuating,creditor_id,payment_delivery,payment_delivery_otheryes,goods_delivery,terminal_type,terminal_type_otheryes,cardvolume_visa,cardvolume_mastercard,cardvolume_americanexpress,cardvolume_dinner,cardvolume_other,cardvolume_discover,payment_type_yes,orderconfirmation_post,orderconfirmation_email,orderconfirmation_sms,orderconfirmation_other,orderconfirmation_other_yes,master_cardlogos,physicalgoods_delivered,viainternetgoods_delivered,shipping_contactemail,seasonal_fluctuating_yes,cardtypesaccepted_rupay,cardvolume_rupay,foreigntransactions_uk,is_website_live,test_link,listfraudtools_yes,domainsowned_no,agency_employed,agency_employed_yes,customers_identification_yes,pricing_policies_website_yes,one_time_percentage,moto_percentage,recurring_percentage,threedsecure_percentage,internet_percentage,swipe_percentage,cardvolume_jcb,paymenttype_credit,paymenttype_debit,paymenttype_netbanking,paymenttype_wallet,paymenttype_alternate,product_sold_currencies)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);
   if (functions.isValueNull(businessProfileVO.getApplication_id()))
   {
    pstmt.setString(1, businessProfileVO.getApplication_id());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
   {
    pstmt.setString(2, businessProfileVO.getForeigntransactions_us());
   }
   else
   {
    pstmt.setNull(2, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
   {
    pstmt.setString(3, businessProfileVO.getForeigntransactions_Europe());
   }
   else
   {
    pstmt.setNull(3, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
   {
    pstmt.setString(4, businessProfileVO.getForeigntransactions_Asia());
   }
   else
   {
    pstmt.setNull(4, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
   {
    pstmt.setString(5, businessProfileVO.getForeigntransactions_RestoftheWorld());
   }
   else
   {
    pstmt.setNull(5, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getMethodofacceptance_moto()))
   {
    pstmt.setString(6, businessProfileVO.getMethodofacceptance_moto());
   }
   else
   {
    pstmt.setNull(6, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getMethodofacceptance_internet()))
   {
    pstmt.setString(7, businessProfileVO.getMethodofacceptance_internet());
   }
   else
   {
    pstmt.setNull(7, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getMethodofacceptance_swipe()))
   {
    pstmt.setString(8, businessProfileVO.getMethodofacceptance_swipe());
   }
   else
   {
    pstmt.setNull(8, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getAverageticket()))
   {
    pstmt.setString(9, businessProfileVO.getAverageticket());
   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getHighestticket()))
   {
    pstmt.setString(10, businessProfileVO.getHighestticket());
   }
   else
   {
    pstmt.setNull(10, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getUrls()))
   {
    pstmt.setString(11, businessProfileVO.getUrls());
   }
   else
   {
    pstmt.setNull(11, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getDescriptor()))
   {
    pstmt.setString(12, businessProfileVO.getDescriptor());
   }
   else
   {
    pstmt.setNull(12, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getDescriptionofproducts()))
   {
    pstmt.setString(13, businessProfileVO.getDescriptionofproducts());
   }
   else
   {
    pstmt.setNull(13, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getRecurringservices()))
   {
    pstmt.setString(14, businessProfileVO.getRecurringservices());
   }
   else
   {
    pstmt.setString(14, "N");
   }
   if (functions.isValueNull(businessProfileVO.getRecurringservicesyes()))
   {
    pstmt.setString(15, businessProfileVO.getRecurringservicesyes());
   }
   else
   {
    pstmt.setNull(15, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getIsacallcenterusedyes()))
   {
    pstmt.setString(16, businessProfileVO.getIsacallcenterusedyes());
   }
   else
   {
    pstmt.setNull(16, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused()))
   {
    pstmt.setString(17, businessProfileVO.getIsafulfillmenthouseused());
   }
   else
   {
    pstmt.setString(17, "N");
   }
   if (functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused_yes()))
   {
    pstmt.setString(18, businessProfileVO.getIsafulfillmenthouseused_yes());
   }
   else
   {
    pstmt.setNull(18, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_visa()))
   {
    pstmt.setString(19, businessProfileVO.getCardtypesaccepted_visa());
   }
   else
   {
    pstmt.setString(19, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_mastercard()))
   {
    pstmt.setString(20, businessProfileVO.getCardtypesaccepted_mastercard());
   }
   else
   {
    pstmt.setString(20, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()))
   {
    pstmt.setString(21, businessProfileVO.getCardtypesaccepted_americanexpress());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()))
   {
    pstmt.setString(22, businessProfileVO.getCardtypesaccepted_discover());
   }
   else
   {
    pstmt.setString(22, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_diners()))
   {
    pstmt.setString(23, businessProfileVO.getCardtypesaccepted_diners());
   }
   else
   {
    pstmt.setString(23, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()))
   {
    pstmt.setString(24, businessProfileVO.getCardtypesaccepted_jcb());
   }
   else
   {
    pstmt.setString(24, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_other()))
   {
    pstmt.setString(25, businessProfileVO.getCardtypesaccepted_other());
   }
   else
   {
    pstmt.setString(25, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_other_yes()))
   {
    pstmt.setString(26, businessProfileVO.getCardtypesaccepted_other_yes());
   }
   else
   {
    pstmt.setNull(26, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getKyc_processes()))
   {
    pstmt.setString(27, businessProfileVO.getKyc_processes());
   }
   else
   {
    pstmt.setString(27, "N");
   }
   if (functions.isValueNull(businessProfileVO.getVisa_cardlogos()))
   {
    pstmt.setString(28, businessProfileVO.getVisa_cardlogos());
   }
   else
   {
    pstmt.setString(28, "N");
   }
   if (functions.isValueNull(businessProfileVO.getThreeD_secure_compulsory()))
   {
    pstmt.setString(29, businessProfileVO.getThreeD_secure_compulsory());
   }
   else
   {
    pstmt.setString(29, "N");
   }
   if (functions.isValueNull(businessProfileVO.getPrice_displayed()))
   {
    pstmt.setString(30, businessProfileVO.getPrice_displayed());
   }
   else
   {
    pstmt.setString(30, "N");
   }
   if (functions.isValueNull(businessProfileVO.getTransaction_currency()))
   {
    pstmt.setString(31, businessProfileVO.getTransaction_currency());
   }
   else
   {
    pstmt.setString(31, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardholder_asked()))
   {
    pstmt.setString(32, businessProfileVO.getCardholder_asked());
   }
   else
   {
    pstmt.setString(32, "N");
   }
   if (functions.isValueNull(businessProfileVO.getDynamic_descriptors()))
   {
    pstmt.setString(33, businessProfileVO.getDynamic_descriptors());
   }
   else
   {
    pstmt.setString(33, "N");
   }
   if (functions.isValueNull(businessProfileVO.getShopping_cart()))
   {
    pstmt.setString(34, businessProfileVO.getShopping_cart());
   }
   else
   {
    pstmt.setString(34, "N");
   }
   if (functions.isValueNull(businessProfileVO.getShopping_cart_details()))
   {
    pstmt.setString(35, businessProfileVO.getShopping_cart_details());
   }
   else
   {
    pstmt.setNull(35, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPricing_policies_website()))
   {
    pstmt.setString(36, businessProfileVO.getPricing_policies_website());
   }
   else
   {
    pstmt.setString(36, "N");
   }
   if (functions.isValueNull(businessProfileVO.getFulfillment_timeframe()))
   {
    pstmt.setString(37, businessProfileVO.getFulfillment_timeframe());
   }
   else
   {
    pstmt.setString(37, "N");
   }
   if (functions.isValueNull(businessProfileVO.getGoods_policy()))
   {
    pstmt.setString(38, businessProfileVO.getGoods_policy());
   }
   else
   {
    pstmt.setString(38, "N");
   }
   if (functions.isValueNull(businessProfileVO.getMCC_Ctegory()))
   {
    pstmt.setString(39, businessProfileVO.getMCC_Ctegory());
   }
   else
   {
    pstmt.setString(39, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCountries_blocked()))
   {
    pstmt.setString(40, businessProfileVO.getCountries_blocked());
   }
   else
   {
    pstmt.setString(40, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCountries_blocked_details()))
   {
    pstmt.setString(41, businessProfileVO.getCountries_blocked_details());
   }
   else
   {
    pstmt.setNull(41, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getCustomer_support()))
   {
    pstmt.setString(42, businessProfileVO.getCustomer_support());
   }
   else
   {
    pstmt.setString(42, "N");
   }
   if (functions.isValueNull(businessProfileVO.getAffiliate_programs()))
   {
    pstmt.setString(43, businessProfileVO.getAffiliate_programs());
   }
   else
   {
    pstmt.setString(43, "N");
   }
   if (functions.isValueNull(businessProfileVO.getAffiliate_programs_details()))
   {
    pstmt.setString(44, businessProfileVO.getAffiliate_programs_details());
   }
   else
   {
    pstmt.setNull(44, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getListfraudtools()))
   {
    pstmt.setString(45, businessProfileVO.getListfraudtools());
   }
   else
   {
    pstmt.setNull(45, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCustomers_identification()))
   {
    pstmt.setString(46, businessProfileVO.getCustomers_identification());
   }
   else
   {
    pstmt.setNull(46, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCoolingoffperiod()))
   {
    pstmt.setString(47, businessProfileVO.getCoolingoffperiod());
   }
   else
   {
    pstmt.setNull(47, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCustomersupport_email()))
   {
    pstmt.setString(48, businessProfileVO.getCustomersupport_email());
   }
   else
   {
    pstmt.setNull(48, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCustsupportwork_hours()))
   {
    pstmt.setString(49, businessProfileVO.getCustsupportwork_hours());
   }
   else
   {
    pstmt.setNull(49, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getTechnical_contact()))
   {
    pstmt.setString(50, businessProfileVO.getTechnical_contact());
   }
   else
   {
    pstmt.setNull(50, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
   {
    pstmt.setString(51, businessProfileVO.getForeigntransactions_cis());
   }
   else
   {
    pstmt.setNull(51, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
   {
    pstmt.setString(52, businessProfileVO.getForeigntransactions_canada());
   }
   else
   {
    pstmt.setNull(52, Types.INTEGER);
   }
   //add specific
   if (functions.isValueNull(businessProfileVO.getSecuritypolicy()))
   {
    pstmt.setString(53, businessProfileVO.getSecuritypolicy());
   }
   else
   {
    pstmt.setString(53, "N");
   }
   if (functions.isValueNull(businessProfileVO.getConfidentialitypolicy()))
   {
    pstmt.setString(54, businessProfileVO.getConfidentialitypolicy());
   }
   else
   {
    pstmt.setString(54, "N");
   }
   if (functions.isValueNull(businessProfileVO.getApplicablejurisdictions()))
   {
    pstmt.setString(55, businessProfileVO.getApplicablejurisdictions());
   }
   else
   {
    pstmt.setString(55, "N");
   }
   if (functions.isValueNull(businessProfileVO.getPrivacy_anonymity_dataprotection()))
   {
    pstmt.setString(56, businessProfileVO.getPrivacy_anonymity_dataprotection());
   }
   else
   {
    pstmt.setString(56, "N");
   }
   //Add Neww
   if (functions.isValueNull(businessProfileVO.getApp_Services()))
   {
    pstmt.setString(57, businessProfileVO.getApp_Services());
   }
   else
   {
    pstmt.setString(57, "N");
   }
   if (functions.isValueNull(businessProfileVO.getProduct_requires()))
   {
    pstmt.setString(58, businessProfileVO.getProduct_requires());
   }
   else
   {
    pstmt.setString(58, "N");
   }
   //ADD new
   if (functions.isValueNull(businessProfileVO.getLowestticket()))
   {
    pstmt.setString(59, businessProfileVO.getLowestticket());
   }
   else
   {
    pstmt.setNull(59, Types.INTEGER);
   }
   //add new
   if (functions.isValueNull(businessProfileVO.getTimeframe()))
   {
    pstmt.setString(60, businessProfileVO.getTimeframe());
   }
   else
   {
    pstmt.setNull(60, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getLivechat()))
   {
    pstmt.setString(61, businessProfileVO.getLivechat());
   }
   else
   {
    pstmt.setString(61, "N");
   }
   //Add new
   if (functions.isValueNull(businessProfileVO.getLoginId()))
   {
    pstmt.setString(62,businessProfileVO.getLoginId());
   }
   else
   {
    pstmt.setNull(62, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPassWord()))
   {
    pstmt.setString(63,businessProfileVO.getPassWord());
   }
   else
   {
    pstmt.setNull(63, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCompanyIdentifiable()))
   {
    pstmt.setString(64,businessProfileVO.getCompanyIdentifiable());
   }
   else
   {
    pstmt.setString(64,"N");
   }
   if(functions.isValueNull(businessProfileVO.getClearlyPresented()))
   {
    pstmt.setString(65,businessProfileVO.getClearlyPresented());
   }
   else
   {
    pstmt.setString(65,"N");
   }
   if(functions.isValueNull(businessProfileVO.getTrackingNumber()))
   {
    pstmt.setString(66,businessProfileVO.getTrackingNumber());
   }
   else
   {
    pstmt.setString(66,"N");
   }
   if(functions.isValueNull(businessProfileVO.getDomainsOwned()))
   {
    pstmt.setString(67,businessProfileVO.getDomainsOwned());
   }
   else
   {
    pstmt.setString(67,"N");
   }
   if(functions.isValueNull(businessProfileVO.getSslSecured()))
   {
    pstmt.setString(68,businessProfileVO.getSslSecured());
   }
   else
   {
    pstmt.setString(68,"N");
   }
   if(functions.isValueNull(businessProfileVO.getCopyright()))
   {
    pstmt.setString(69,businessProfileVO.getCopyright());
   }
   else
   {
    pstmt.setString(69,"N");
   }
   if(functions.isValueNull(businessProfileVO.getSourceContent()))
   {
    pstmt.setString(70,businessProfileVO.getSourceContent());
   }
   else
   {
    pstmt.setNull(70,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getDirectMail()))
   {
    pstmt.setString(71,businessProfileVO.getDirectMail());
   }
   else
   {
    pstmt.setString(71, "N");
   }
   if(functions.isValueNull(businessProfileVO.getYellowPages()))
   {
    pstmt.setString(72,businessProfileVO.getYellowPages());
   }
   else
   {
    pstmt.setString(72, "N");
   }
   if(functions.isValueNull(businessProfileVO.getRadioTv()))
   {
    pstmt.setString(73,businessProfileVO.getRadioTv());
   }
   else
   {
    pstmt.setString(73, "N");
   }
   if(functions.isValueNull(businessProfileVO.getInternet()))
   {
    pstmt.setString(74,businessProfileVO.getInternet());
   }
   else
   {
    pstmt.setString(74, "N");
   }
   if(functions.isValueNull(businessProfileVO.getNetworking()))
   {
    pstmt.setString(75,businessProfileVO.getNetworking());
   }
   else
   {
    pstmt.setString(75, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOutboundTelemarketing()))
   {
    pstmt.setString(76,businessProfileVO.getOutboundTelemarketing());
   }
   else
   {
    pstmt.setString(76, "N");
   }
   if (functions.isValueNull(businessProfileVO.getInHouseLocation()))
   {
    pstmt.setString(77,businessProfileVO.getInHouseLocation());
   }
   else
   {
    pstmt.setNull(77, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getContactPerson()))
   {
    pstmt.setString(78,businessProfileVO.getContactPerson());
   }
   else
   {
    pstmt.setNull(78, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getOtherLocation()))
   {
    pstmt.setString(79,businessProfileVO.getOtherLocation());
   }
   else
   {
    pstmt.setNull(79, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getMainSuppliers()))
   {
    pstmt.setString(80,businessProfileVO.getMainSuppliers());
   }
   else
   {
    pstmt.setNull(80, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getShipmentAssured()))
   {
    pstmt.setString(81,businessProfileVO.getShipmentAssured());
   }
   else
   {
    pstmt.setString(81, "N");
   }
   //ADD new
   if (functions.isValueNull(businessProfileVO.getBillingModel()))
   {
    pstmt.setString(82,businessProfileVO.getBillingModel());
   }
   else
   {
    pstmt.setString(82, "one_time");
   }
   if (functions.isValueNull(businessProfileVO.getBillingTimeFrame()))
   {
    pstmt.setString(83,businessProfileVO.getBillingTimeFrame());
   }
   else
   {
    pstmt.setNull(83, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getRecurringAmount()))
   {
    pstmt.setString(84,businessProfileVO.getRecurringAmount());
   }
   else
   {
    pstmt.setNull(84, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getAutomaticRecurring()))
   {
    pstmt.setString(85,businessProfileVO.getAutomaticRecurring());
   }
   else
   {
    pstmt.setString(85, "N");
   }
   if (functions.isValueNull(businessProfileVO.getMultipleMembership()))
   {
    pstmt.setString(86,businessProfileVO.getMultipleMembership());
   }
   else
   {
    pstmt.setString(86, "N");
   }
   if (functions.isValueNull(businessProfileVO.getFreeMembership()))
   {
    pstmt.setString(87,businessProfileVO.getFreeMembership());
   }
   else
   {
    pstmt.setString(87, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCreditCardRequired()))
   {
    pstmt.setString(88,businessProfileVO.getCreditCardRequired());
   }
   else
   {
    pstmt.setString(88, "N");
   }
   if (functions.isValueNull(businessProfileVO.getAutomaticallyBilled()))
   {
    pstmt.setString(89,businessProfileVO.getAutomaticallyBilled());
   }
   else
   {
    pstmt.setString(89, "N");
   }
   if (functions.isValueNull(businessProfileVO.getPreAuthorization()))
   {
    pstmt.setString(90,businessProfileVO.getPreAuthorization());
   }
   else
   {
    pstmt.setString(90, "N");
   }
   if (functions.isValueNull(businessProfileVO.getMerchantCode()))
   {
    pstmt.setString(91,businessProfileVO.getMerchantCode());
   }
   else
   {
    pstmt.setNull(91, Types.VARCHAR);
   }
   //ADD new
   if (functions.isValueNull(businessProfileVO.getIpaddress()))
   {
    pstmt.setString(92,businessProfileVO.getIpaddress());
   }
   else
   {
    pstmt.setNull(92, Types.VARCHAR);
   }

   // Wirecard requirement added in Business Profile

   if (functions.isValueNull(businessProfileVO.getShopsystem_plugin()))
   {
    pstmt.setString(93,businessProfileVO.getShopsystem_plugin());
   }
   else
   {
    pstmt.setNull(93, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getDirect_debit_sepa()))
   {
    pstmt.setString(94,businessProfileVO.getDirect_debit_sepa());
   }
   else
   {
    pstmt.setNull(94, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getAlternative_payments()))
   {
    pstmt.setString(95,businessProfileVO.getAlternative_payments());
   }
   else
   {
    pstmt.setNull(95, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getRisk_management()))
   {
    pstmt.setString(96,businessProfileVO.getRisk_management());
   }
   else
   {
    pstmt.setNull(96, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_engine()))
   {
    pstmt.setString(97,businessProfileVO.getPayment_engine());
   }
   else
   {
    pstmt.setNull(97, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_company_name()))
   {
    pstmt.setString(98,businessProfileVO.getWebhost_company_name());
   }
   else
   {
    pstmt.setNull(98, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_phone()))
   {
    pstmt.setString(99,businessProfileVO.getWebhost_phone());
   }
   else
   {
    pstmt.setNull(99, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_email()))
   {
    pstmt.setString(100,businessProfileVO.getWebhost_email());
   }
   else
   {
    pstmt.setNull(100, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_website()))
   {
    pstmt.setString(101,businessProfileVO.getWebhost_website());
   }
   else
   {
    pstmt.setNull(101, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_address()))
   {
    pstmt.setString(102,businessProfileVO.getWebhost_address());
   }
   else
   {
    pstmt.setNull(102, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_company_name()))
   {
    pstmt.setString(103,businessProfileVO.getPayment_company_name());
   }
   else
   {
    pstmt.setNull(103, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_phone()))
   {
    pstmt.setString(104,businessProfileVO.getPayment_phone());
   }
   else
   {
    pstmt.setNull(104, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_email()))
   {
    pstmt.setString(105,businessProfileVO.getPayment_email());
   }
   else
   {
    pstmt.setNull(105, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_website()))
   {
    pstmt.setString(106,businessProfileVO.getPayment_website());
   }
   else
   {
    pstmt.setNull(106, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_address()))
   {
    pstmt.setString(107,businessProfileVO.getPayment_address());
   }
   else
   {
    pstmt.setNull(107, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_phone()))
   {
    pstmt.setString(108,businessProfileVO.getCallcenter_phone());
   }
   else
   {
    pstmt.setNull(108, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_email()))
   {
    pstmt.setString(109,businessProfileVO.getCallcenter_email());
   }
   else
   {
    pstmt.setNull(109, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_website()))
   {
    pstmt.setString(110,businessProfileVO.getCallcenter_website());
   }
   else
   {
    pstmt.setNull(110, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_address()))
   {
    pstmt.setString(111,businessProfileVO.getCallcenter_address());
   }
   else
   {
    pstmt.setNull(111, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_company_name()))
   {
    pstmt.setString(112,businessProfileVO.getShoppingcart_company_name());
   }
   else
   {
    pstmt.setNull(112, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_phone()))
   {
    pstmt.setString(113,businessProfileVO.getShoppingcart_phone());
   }
   else
   {
    pstmt.setNull(113, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_email()))
   {
    pstmt.setString(114,businessProfileVO.getShoppingcart_email());
   }
   else
   {
    pstmt.setNull(114, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_website()))
   {
    pstmt.setString(115,businessProfileVO.getShoppingcart_website());
   }
   else
   {
    pstmt.setNull(115, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_address()))
   {
    pstmt.setString(116,businessProfileVO.getShoppingcart_address());
   }
   else
   {
    pstmt.setNull(116, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getSeasonal_fluctuating()))
   {
    pstmt.setString(117,businessProfileVO.getSeasonal_fluctuating());
   }
   else
   {
    pstmt.setString(117, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCreditor_id()))
   {
    pstmt.setString(118,businessProfileVO.getCreditor_id());
   }
   else
   {
    pstmt.setNull(118, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPayment_delivery()))
   {
    pstmt.setString(119,businessProfileVO.getPayment_delivery());
   }
   else
   {
    pstmt.setString(119, "upon_purchase");
   }
   if (functions.isValueNull(businessProfileVO.getPayment_delivery_otheryes()))
   {
    pstmt.setString(120,businessProfileVO.getPayment_delivery_otheryes());
   }
   else
   {
    pstmt.setNull(120, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getGoods_delivery()))
   {
    pstmt.setString(121,businessProfileVO.getGoods_delivery());
   }
   else
   {
    pstmt.setString(121, "over_internet");
   }
   if (functions.isValueNull(businessProfileVO.getTerminal_type()))
   {
    pstmt.setString(122,businessProfileVO.getTerminal_type());
   }
   else
   {
    pstmt.setString(122, "onetime_terminal");
   }
   if (functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
   {
    pstmt.setString(123,businessProfileVO.getTerminal_type_otheryes());
   }
   else
   {
    pstmt.setNull(123, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_visa()))
   {
    pstmt.setString(124,businessProfileVO.getCardvolume_visa());
   }
   else
   {
    pstmt.setNull(124,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_mastercard()))
   {
    pstmt.setString(125,businessProfileVO.getCardvolume_mastercard());
   }
   else
   {
    pstmt.setNull(125,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()))
   {
    pstmt.setString(126,businessProfileVO.getCardvolume_americanexpress());
   }
   else
   {
    pstmt.setNull(126,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_dinner()))
   {
    pstmt.setString(127,businessProfileVO.getCardvolume_dinner());
   }
   else
   {
    pstmt.setNull(127,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_other()))
   {
    pstmt.setString(128,businessProfileVO.getCardvolume_other());
   }
   else
   {
    pstmt.setNull(128,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_discover()))
   {
    pstmt.setString(129,businessProfileVO.getCardvolume_discover());
   }
   else
   {
    pstmt.setNull(129,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getPayment_type_yes()))
   {
    pstmt.setString(130,businessProfileVO.getPayment_type_yes());
   }
   else
   {
    pstmt.setNull(130,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_post()))
   {
    pstmt.setString(131,businessProfileVO.getOrderconfirmation_post());
   }
   else
   {
    pstmt.setString(131, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_email()))
   {
    pstmt.setString(132,businessProfileVO.getOrderconfirmation_email());
   }
   else
   {
    pstmt.setString(132, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_sms()))
   {
    pstmt.setString(133,businessProfileVO.getOrderconfirmation_sms());
   }
   else
   {
    pstmt.setString(133, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_other()))
   {
    pstmt.setString(134,businessProfileVO.getOrderconfirmation_other());
   }
   else
   {
    pstmt.setString(134, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_other_yes()))
   {
    pstmt.setString(135,businessProfileVO.getOrderconfirmation_other_yes());
   }
   else
   {
    pstmt.setNull(135, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getMaster_cardlogos()))
   {
    pstmt.setString(136, businessProfileVO.getMaster_cardlogos());
   }
   else
   {
    pstmt.setString(136, "N");
   }
   if (functions.isValueNull(businessProfileVO.getPhysicalgoods_delivered()))
   {
    pstmt.setString(137, businessProfileVO.getPhysicalgoods_delivered());
   }
   else
   {
    pstmt.setString(137, "N");
   }
   if (functions.isValueNull(businessProfileVO.getViainternetgoods_delivered()))
   {
    pstmt.setString(138, businessProfileVO.getViainternetgoods_delivered());
   }
   else
   {
    pstmt.setString(138, "N");
   }
   if (functions.isValueNull(businessProfileVO.getShippingContactemail()))
   {
    pstmt.setString(139, businessProfileVO.getShippingContactemail());
   }
   else
   {
    pstmt.setNull(139, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getSeasonal_fluctuating_yes()))
   {
    pstmt.setString(140, businessProfileVO.getSeasonal_fluctuating_yes());
   }
   else
   {
    pstmt.setNull(140, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCardtypesaccepted_rupay()))
   {
    pstmt.setString(141, businessProfileVO.getCardtypesaccepted_rupay());
   }
   else
   {
    pstmt.setString(141, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCardvolume_rupay()))
   {
    pstmt.setString(142, businessProfileVO.getCardvolume_rupay());
   }
   else
   {
    pstmt.setNull(142, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
   {
    pstmt.setString(143, businessProfileVO.getForeigntransactions_uk());
   }
   else
   {
    pstmt.setNull(143, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getIs_website_live()))
   {
    pstmt.setString(144, businessProfileVO.getIs_website_live());
   }
   else
   {
    pstmt.setString(144, "N");
   }
   if (functions.isValueNull(businessProfileVO.getTest_link()))
   {
    pstmt.setString(145, businessProfileVO.getTest_link());
   }
   else
   {
    pstmt.setNull(145, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getListfraudtools_yes()))
   {
    pstmt.setString(146, businessProfileVO.getListfraudtools_yes());
   }
   else
   {
    pstmt.setNull(146, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getDomainsOwned_no()))
   {
    pstmt.setString(147, businessProfileVO.getDomainsOwned_no());
   }
   else
   {
    pstmt.setNull(147, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getAgency_employed()))
   {
    pstmt.setString(148, businessProfileVO.getAgency_employed());
   }
   else
   {
    pstmt.setString(148, "N");
   }
   if (functions.isValueNull(businessProfileVO.getAgency_employed_yes()))
   {
    pstmt.setString(149, businessProfileVO.getAgency_employed_yes());
   }
   else
   {
    pstmt.setNull(149, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCustomers_identification_yes()))
   {
    pstmt.setString(150, businessProfileVO.getCustomers_identification_yes());
   }
   else
   {
    pstmt.setNull(150, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPricing_policies_website_yes()))
   {
    pstmt.setString(151, businessProfileVO.getPricing_policies_website_yes());
   }
   else
   {
    pstmt.setNull(151, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getOne_time_percentage()))
   {
    pstmt.setString(152, businessProfileVO.getOne_time_percentage());
   }
   else
   {
    pstmt.setNull(152, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getMoto_percentage()))
   {
    pstmt.setString(153, businessProfileVO.getMoto_percentage());
   }
   else
   {
    pstmt.setNull(153, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getRecurring_percentage()))
   {
    pstmt.setString(154, businessProfileVO.getRecurring_percentage());
   }
   else
   {
    pstmt.setNull(154, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getThreedsecure_percentage()))
   {
    pstmt.setString(155, businessProfileVO.getThreedsecure_percentage());
   }
   else
   {
    pstmt.setNull(155, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getInternet_percentage()))
   {
    pstmt.setString(156, businessProfileVO.getInternet_percentage());
   }
   else
   {
    pstmt.setNull(156, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getSwipe_percentage()))
   {
    pstmt.setString(157, businessProfileVO.getSwipe_percentage());
   }
   else
   {
    pstmt.setNull(157, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getCardvolume_jcb()))
   {
    pstmt.setString(158, businessProfileVO.getCardvolume_jcb());
   }
   else
   {
    pstmt.setNull(158, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_credit()))
   {
    pstmt.setString(159, businessProfileVO.getPaymenttype_credit());
   }
   else
   {
    pstmt.setNull(159, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_debit()))
   {
    pstmt.setString(160, businessProfileVO.getPaymenttype_debit());
   }
   else
   {
    pstmt.setNull(160, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_netbanking()))
   {
    pstmt.setString(161, businessProfileVO.getPaymenttype_netbanking());
   }
   else
   {
    pstmt.setNull(161, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_wallet()))
   {
    pstmt.setString(162, businessProfileVO.getPaymenttype_wallet());
   }
   else
   {
    pstmt.setNull(162, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_alternate()))
   {
    pstmt.setString(163, businessProfileVO.getPaymenttype_alternate());
   }
   else
   {
    pstmt.setNull(163, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
   {
    pstmt.setString(164, businessProfileVO.getProduct_sold_currencies());
   }
   else
   {
    pstmt.setNull(164, Types.VARCHAR);
   }

   int k = pstmt.executeUpdate();
   logger.debug("Inserstion of applicationManaget Business Profile-----"+pstmt);
   if (k > 0)
   {
    businessProfileVO.setBusinessProfileSaved("Y");
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   //throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertBusinessProfile");

  return false;
 }

 public boolean insertBankProfile(BankProfileVO bankProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO applicationmanager_bankprofile(application_id,currencyrequested_productssold,currencyrequested_bankaccount,customer_trans_data,aquirer,reason_aquirer,bank_account_currencies)VALUES(?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);

   if (functions.isValueNull(bankProfileVO.getApplication_id()))
   {
    pstmt.setString(1, bankProfileVO.getApplication_id());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getCurrencyrequested_productssold()))
   {
    pstmt.setString(2, bankProfileVO.getCurrencyrequested_productssold());
   }
   else
   {
    pstmt.setNull(2, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getCurrencyrequested_bankaccount()))
   {
    pstmt.setString(3, bankProfileVO.getCurrencyrequested_bankaccount());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getCustomer_trans_data()))
   {
    pstmt.setString(4,bankProfileVO.getCustomer_trans_data());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getAquirer()))
   {
    pstmt.setString(5,bankProfileVO.getAquirer());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getReasonaquirer()))
   {
    pstmt.setString(6,bankProfileVO.getReasonaquirer());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBank_account_currencies()))
   {
    pstmt.setString(7,bankProfileVO.getBank_account_currencies());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    bankProfileVO.setBankProfileSaved("Y");
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertBankProfile");

  return false;
 }

 public boolean insertCurrencyWiseBankInfo(BankProfileVO bankProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO applicationmanager_currencywisebankinfo(application_id,bankinfo_bic,bankinfo_bank_name,bankinfo_bankaddress,bankinfo_bankphonenumber,bankinfo_accountnumber,bankinfo_aba_routingcode,bankinfo_accountholder,bankinfo_IBAN,bankinfo_contactperson,bankinfo_currency)VALUES(?,?,?,?,?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);

   if (functions.isValueNull(bankProfileVO.getApplication_id()))
   {
    pstmt.setString(1, bankProfileVO.getApplication_id());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bic()))
   {
    pstmt.setString(2, bankProfileVO.getBankinfo_bic());
   }
   else
   {
    pstmt.setNull(2, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bank_name()))
   {
    pstmt.setString(3, bankProfileVO.getBankinfo_bank_name());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bankaddress()))
   {
    pstmt.setString(4, bankProfileVO.getBankinfo_bankaddress());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bankphonenumber()))
   {
    pstmt.setString(5, bankProfileVO.getBankinfo_bankphonenumber());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_accountnumber()))
   {
    pstmt.setString(6,bankProfileVO.getBankinfo_accountnumber());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_aba_routingcode()))
   {
    pstmt.setString(7, bankProfileVO.getBankinfo_aba_routingcode());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_accountholder()))
   {
    pstmt.setString(8, bankProfileVO.getBankinfo_accountholder());
   }
   else
   {
    pstmt.setNull(8, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_IBAN()))
   {
    pstmt.setString(9,bankProfileVO.getBankinfo_IBAN());

   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_contactperson()))
   {
    pstmt.setString(10,bankProfileVO.getBankinfo_contactperson());
   }
   else
   {
    pstmt.setNull(10, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_currency()))
   {
    pstmt.setString(11,bankProfileVO.getBankinfo_currency());
   }
   else
   {
    pstmt.setNull(11, Types.VARCHAR);
   }


   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    bankProfileVO.setIscurrencywisebankinfo("Y");
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertBankProfile");

  return false;
 }

 public boolean insertProcessingHistory(BankProfileVO bankProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   logger.debug("Inside insertProcessingHistory here---------->");
   conn = Database.getConnection();
   query = "INSERT INTO applicationmanager_processinghistory(salesvolume_lastmonth,salesvolume_2monthsago,salesvolume_3monthsago,salesvolume_4monthsago,salesvolume_5monthsago,salesvolume_6monthsago,salesvolume_12monthsago,salesvolume_year2,salesvolume_year3,numberoftransactions_lastmonth,numberoftransactions_2monthsago,numberoftransactions_3monthsago,numberoftransactions_4monthsago,numberoftransactions_5monthsago,numberoftransactions_6monthsago,numberoftransactions_12monthsago,numberoftransactions_year2,numberoftransactions_year3,chargebackvolume_lastmonth,chargebackvolume_2monthsago,chargebackvolume_3monthsago,chargebackvolume_4monthsago,chargebackvolume_5monthsago,chargebackvolume_6monthsago,chargebackvolume_12monthsago,chargebackvolume_year2,chargebackvolume_year3,numberofchargebacks_lastmonth,numberofchargebacks_2monthsago,numberofchargebacks_3monthsago,numberofchargebacks_4monthsago,numberofchargebacks_5monthsago,numberofchargebacks_6monthsago,numberofchargebacks_12monthsago,numberofchargebacks_year2,numberofchargebacks_year3,numberofrefunds_lastmonth,numberofrefunds_2monthsago,numberofrefunds_3monthsago,numberofrefunds_4monthsago,numberofrefunds_5monthsago,numberofrefunds_6monthsago,numberofrefunds_12monthsago,numberofrefunds_year2,numberofrefunds_year3,refundratio_lastmonth,refundratio_2monthsago,refundratio_3monthsago,refundratio_4monthsago,refundratio_5monthsago,refundratio_6monthsago,refundratio_12monthsago,refundratio_year2,refundratio_year3,chargebackratio_lastmonth,chargebackratio_2monthsago,chargebackratio_3monthsago,chargebackratio_4monthsago,chargebackratio_5monthsago,chargebackratio_6monthsago,chargebackratio_12monthsago,chargebackratio_year2,chargebackratio_year3,refundsvolume_lastmonth,refundsvolume_2monthsago,refundsvolume_3monthsago,refundsvolume_4monthsago,refundsvolume_5monthsago,refundsvolume_6monthsago,refundsvolume_12monthsago,refundsvolume_year2,refundsvolume_year3,currency,application_id,creation_time,updation_time)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";
   pstmt = conn.prepareStatement(query);

   if (functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
   {
    pstmt.setString(1, bankProfileVO.getSalesvolume_lastmonth());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
   {
    pstmt.setString(2, bankProfileVO.getSalesvolume_2monthsago());
   }
   else
   {
    pstmt.setNull(2, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
   {
    pstmt.setString(3, bankProfileVO.getSalesvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(3, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
   {
    pstmt.setString(4, bankProfileVO.getSalesvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(4, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
   {
    pstmt.setString(5, bankProfileVO.getSalesvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(5, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
   {
    pstmt.setString(6, bankProfileVO.getSalesvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(6, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
   {
    pstmt.setString(7, bankProfileVO.getSalesvolume_12monthsago());
    //logger.debug("ownersince" + bankProfileVO.getSalesvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(7, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
   {
    pstmt.setString(8, bankProfileVO.getSalesvolume_year2());
    //logger.debug("socialsecurity" + bankProfileVO.getSalesvolume_year2());
   }
   else
   {
    pstmt.setNull(8, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
   {
    pstmt.setString(9, bankProfileVO.getSalesvolume_year3());
    //logger.debug("company_formparticipation" + extraDetailsProfileVO.getCompany_formParticipation());
   }
   else
   {
    pstmt.setNull(9, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth()))
   {
    pstmt.setString(10, bankProfileVO.getNumberoftransactions_lastmonth());
    //logger.debug("financialobligation" + extraDetailsProfileVO.getFinancialObligation());
   }
   else
   {
    pstmt.setNull(10, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago()))
   {
    pstmt.setString(11, bankProfileVO.getNumberoftransactions_2monthsago());
   }
   else
   {
    pstmt.setNull(11, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago()))
   {
    pstmt.setString(12, bankProfileVO.getNumberoftransactions_3monthsago());
   }
   else
   {
    pstmt.setNull(12, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago()))
   {
    pstmt.setString(13, bankProfileVO.getNumberoftransactions_4monthsago());
    //logger.debug("workingexperience" + extraDetailsProfileVO.getWorkingExperience());
   }
   else
   {
    pstmt.setNull(13, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago()))
   {
    pstmt.setString(14, bankProfileVO.getNumberoftransactions_5monthsago());
   }
   else
   {
    pstmt.setNull(14, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago()))
   {
    pstmt.setString(15, bankProfileVO.getNumberoftransactions_6monthsago());
   }
   else
   {
    pstmt.setNull(15, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago()))
   {
    pstmt.setString(16, bankProfileVO.getNumberoftransactions_12monthsago());
   }
   else
   {
    pstmt.setNull(16, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_year2()))
   {
    pstmt.setString(17, bankProfileVO.getNumberoftransactions_year2());
   }
   else
   {
    pstmt.setNull(17, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_year3()))
   {
    pstmt.setString(18, bankProfileVO.getNumberoftransactions_year3());
   }
   else
   {
    pstmt.setNull(18, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth()))
   {
    pstmt.setString(19, bankProfileVO.getChargebackvolume_lastmonth());
    //logger.debug("shiping_deliverymethod" + extraDetailsProfileVO.getShiping_deliveryMethod());
   }
   else
   {
    pstmt.setNull(19, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago()))
   {
    pstmt.setString(20, bankProfileVO.getChargebackvolume_2monthsago());
    //logger.debug("transactionmonitoringprocess" + extraDetailsProfileVO.getTransactionMonitoringProcess());
   }
   else
   {
    pstmt.setNull(20, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago()))
   {
    pstmt.setString(21, bankProfileVO.getChargebackvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(21, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago()))
   {
    pstmt.setString(22, bankProfileVO.getChargebackvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(22, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago()))
   {
    pstmt.setString(23, bankProfileVO.getChargebackvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(23, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago()))
   {
    pstmt.setString(24, bankProfileVO.getChargebackvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(24, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago()))
   {
    pstmt.setString(25, bankProfileVO.getChargebackvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(25, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_year2()))
   {
    pstmt.setString(26, bankProfileVO.getChargebackvolume_year2());
   }
   else
   {
    pstmt.setNull(26, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_year3()))
   {
    pstmt.setString(27, bankProfileVO.getChargebackvolume_year3());
   }
   else
   {
    pstmt.setNull(27, Types.INTEGER);
   }

   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth()))
   {
    pstmt.setString(28, bankProfileVO.getNumberofchargebacks_lastmonth());
   }
   else
   {
    pstmt.setNull(28, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago()))
   {
    pstmt.setString(29, bankProfileVO.getNumberofchargebacks_2monthsago());
   }
   else
   {
    pstmt.setNull(29, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago()))
   {
    pstmt.setString(30, bankProfileVO.getNumberofchargebacks_3monthsago());
   }
   else
   {
    pstmt.setNull(30, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago()))
   {
    pstmt.setString(31, bankProfileVO.getNumberofchargebacks_4monthsago());
   }
   else
   {
    pstmt.setNull(31, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago()))
   {
    pstmt.setString(32, bankProfileVO.getNumberofchargebacks_5monthsago());
   }
   else
   {
    pstmt.setNull(32, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago()))
   {
    pstmt.setString(33, bankProfileVO.getNumberofchargebacks_6monthsago());
   }
   else
   {
    pstmt.setNull(33, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago()))
   {
    pstmt.setString(34, bankProfileVO.getNumberofchargebacks_12monthsago());
   }
   else
   {
    pstmt.setNull(34, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2()))
   {
    pstmt.setString(35, bankProfileVO.getNumberofchargebacks_year2());
   }
   else
   {
    pstmt.setNull(35, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3()))
   {
    pstmt.setString(36, bankProfileVO.getNumberofchargebacks_year3());
   }
   else
   {
    pstmt.setNull(36, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth()))
   {
    pstmt.setString(37, bankProfileVO.getNumberofrefunds_lastmonth());
   }
   else
   {
    pstmt.setNull(37, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago()))
   {
    pstmt.setString(38, bankProfileVO.getNumberofrefunds_2monthsago());
   }
   else
   {
    pstmt.setNull(38, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago()))
   {
    pstmt.setString(39, bankProfileVO.getNumberofrefunds_3monthsago());
   }
   else
   {
    pstmt.setNull(39, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_4monthsago()))
   {
    pstmt.setString(40, bankProfileVO.getNumberofrefunds_4monthsago());
   }
   else
   {
    pstmt.setNull(40, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_5monthsago()))
   {
    pstmt.setString(41, bankProfileVO.getNumberofrefunds_5monthsago());
   }
   else
   {
    pstmt.setNull(41, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_6monthsago()))
   {
    pstmt.setString(42, bankProfileVO.getNumberofrefunds_6monthsago());
   }
   else
   {
    pstmt.setNull(42, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_12monthsago()))
   {
    pstmt.setString(43, bankProfileVO.getNumberofrefunds_12monthsago());
   }
   else
   {
    pstmt.setNull(43, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_year2()))
   {
    pstmt.setString(44, bankProfileVO.getNumberofrefunds_year2());
   }
   else
   {
    pstmt.setNull(44, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_year3()))
   {
    pstmt.setString(45, bankProfileVO.getNumberofrefunds_year3());
   }
   else
   {
    pstmt.setNull(45, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_lastmonth()))
   {
    pstmt.setString(46, bankProfileVO.getRefundratio_lastmonth());
   }
   else
   {
    pstmt.setNull(46, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_2monthsago()))
   {
    pstmt.setString(47, bankProfileVO.getRefundratio_2monthsago());
   }
   else
   {
    pstmt.setNull(47, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_3monthsago()))
   {
    pstmt.setString(48, bankProfileVO.getRefundratio_3monthsago());
   }
   else
   {
    pstmt.setNull(48, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_4monthsago()))
   {
    pstmt.setString(49, bankProfileVO.getRefundratio_4monthsago());
   }
   else
   {
    pstmt.setNull(49, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_5monthsago()))
   {
    pstmt.setString(50, bankProfileVO.getRefundratio_5monthsago());
   }
   else
   {
    pstmt.setNull(50, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_6monthsago()))
   {
    pstmt.setString(51, bankProfileVO.getRefundratio_6monthsago());
   }
   else
   {
    pstmt.setNull(51, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_12monthsago()))
   {
    pstmt.setString(52, bankProfileVO.getRefundratio_12monthsago());
   }
   else
   {
    pstmt.setNull(52, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_year2()))
   {
    pstmt.setString(53, bankProfileVO.getRefundratio_year2());
   }
   else
   {
    pstmt.setNull(53, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_year3()))
   {
    pstmt.setString(54, bankProfileVO.getRefundratio_year3());
   }
   else
   {
    pstmt.setNull(54, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth()))
   {
    pstmt.setString(55, bankProfileVO.getChargebackratio_lastmonth());
   }
   else
   {
    pstmt.setNull(55, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago()))
   {
    pstmt.setString(56, bankProfileVO.getChargebackratio_2monthsago());
   }
   else
   {
    pstmt.setNull(56, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago()))
   {
    pstmt.setString(57, bankProfileVO.getChargebackratio_3monthsago());
   }
   else
   {
    pstmt.setNull(57, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago()))
   {
    pstmt.setString(58, bankProfileVO.getChargebackratio_4monthsago());
   }
   else
   {
    pstmt.setNull(58, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago()))
   {
    pstmt.setString(59, bankProfileVO.getChargebackratio_5monthsago());
   }
   else
   {
    pstmt.setNull(59, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago()))
   {
    pstmt.setString(60, bankProfileVO.getChargebackratio_6monthsago());
   }
   else
   {
    pstmt.setNull(60, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago()))
   {
    pstmt.setString(61, bankProfileVO.getChargebackratio_12monthsago());
   }
   else
   {
    pstmt.setNull(61, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_year2()))
   {
    pstmt.setString(62, bankProfileVO.getChargebackratio_year2());
   }
   else
   {
    pstmt.setNull(62, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_year3()))
   {
    pstmt.setString(63, bankProfileVO.getChargebackratio_year3());
   }
   else
   {
    pstmt.setNull(63, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth()))
   {
    pstmt.setString(64, bankProfileVO.getRefundsvolume_lastmonth());
   }
   else
   {
    pstmt.setNull(64, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago()))
   {
    pstmt.setString(65, bankProfileVO.getRefundsvolume_2monthsago());
   }
   else
   {
    pstmt.setNull(65, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago()))
   {
    pstmt.setString(66, bankProfileVO.getRefundsvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(66, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago()))
   {
    pstmt.setString(67, bankProfileVO.getRefundsvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(67, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago()))
   {
    pstmt.setString(68, bankProfileVO.getRefundsvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(68, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago()))
   {
    pstmt.setString(69, bankProfileVO.getRefundsvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(69, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago()))
   {
    pstmt.setString(70, bankProfileVO.getRefundsvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(70, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_year2()))
   {
    pstmt.setString(71, bankProfileVO.getRefundsvolume_year2());
   }
   else
   {
    pstmt.setNull(71, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_year3()))
   {
    pstmt.setString(72, bankProfileVO.getRefundsvolume_year3());
   }
   else
   {
    pstmt.setNull(72, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getCurrency()))
   {
    pstmt.setString(73, bankProfileVO.getCurrency());
   }
   else
   {
    pstmt.setNull(73, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getApplication_id()))
   {
    pstmt.setString(74, bankProfileVO.getApplication_id());
   }
   else
   {
    pstmt.setNull(74, Types.INTEGER);
   }

   int k = pstmt.executeUpdate();
   logger.debug("insertProcessingHistory qry--->"+pstmt);
   if (k > 0)
   {
    bankProfileVO.setIsProcessingHistory("Y");
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertProcessingHistory");

  return false;
 }

 public boolean insertCardholderProfile(CardholderProfileVO cardholderProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO applicationmanager_cardholderprofile(application_id,compliance_swapp,compliance_thirdpartyappform,compliance_thirdpartysoft,compliance_version,compliance_companiesorgateways,compliance_companiesorgateways_yes,compliance_electronically,compliance_carddatastored,compliance_pcidsscompliant,compliance_qualifiedsecurityassessor,compliance_dateofcompliance,compliance_dateoflastscan,compliance_datacompromise,compliance_datacompromise_yes,siteinspection_merchant,siteinspection_landlord,siteinspection_buildingtype,siteinspection_areazoned,siteinspection_squarefootage,siteinspection_operatebusiness,siteinspection_principal1,siteinspection_principal1_date,siteinspection_principal2,siteinspection_principal2_date,compliance_pcidsscompliant_yes,compliance_cispcompliant,compliance_cispcompliant_yes)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);
   logger.debug("1111111111" + cardholderProfileVO.getApplication_id());
   if (functions.isValueNull(cardholderProfileVO.getApplication_id()))
   {
    pstmt.setString(1, cardholderProfileVO.getApplication_id());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_swapp()))
   {
    pstmt.setString(2, cardholderProfileVO.getCompliance_swapp());
    logger.debug("getCompliance_swapp" + cardholderProfileVO.getCompliance_swapp());
   }
   else
   {
    pstmt.setString(2, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartyappform()))
   {
    pstmt.setString(3, cardholderProfileVO.getCompliance_thirdpartyappform());
    logger.debug("getCompliance_thirdpartyappform" + cardholderProfileVO.getCompliance_thirdpartyappform());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartysoft()))
   {
    pstmt.setString(4, cardholderProfileVO.getCompliance_thirdpartysoft());
    logger.debug("getCompliance_thirdpartysoft" + cardholderProfileVO.getCompliance_thirdpartysoft());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_version()))
   {
    pstmt.setString(5, cardholderProfileVO.getCompliance_version());
    logger.debug("getCompliance_version" + cardholderProfileVO.getCompliance_version());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways()))
   {
    pstmt.setString(6, cardholderProfileVO.getCompliance_companiesorgateways());
    logger.debug("getCompliance_companiesorgateways" + cardholderProfileVO.getCompliance_companiesorgateways());
   }
   else
   {
    pstmt.setString(6, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways_yes()))
   {
    pstmt.setString(7, cardholderProfileVO.getCompliance_companiesorgateways_yes());
    logger.debug("getCompliance_companiesorgateways_yes" + cardholderProfileVO.getCompliance_companiesorgateways_yes());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_electronically()))
   {
    pstmt.setString(8, cardholderProfileVO.getCompliance_electronically());

   }
   else
   {
    pstmt.setString(8, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_carddatastored()))
   {
    pstmt.setString(9, cardholderProfileVO.getCompliance_carddatastored());
    logger.debug("getCompliance_carddatastored" + cardholderProfileVO.getCompliance_carddatastored());
   }
   else
   {
    pstmt.setString(9, "Both");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant()))
   {
    pstmt.setString(10, cardholderProfileVO.getCompliance_pcidsscompliant());
   }
   else
   {
    pstmt.setString(10, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_qualifiedsecurityassessor()))
   {
    pstmt.setString(11, cardholderProfileVO.getCompliance_qualifiedsecurityassessor());
    logger.debug("getCompliance_qualifiedsecurityassessor" + cardholderProfileVO.getCompliance_qualifiedsecurityassessor());
   }
   else
   {
    pstmt.setNull(11, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_dateofcompliance()))
   {
    pstmt.setString(12, cardholderProfileVO.getCompliance_dateofcompliance());
    logger.debug("getCompliance_dateofcompliance" + cardholderProfileVO.getCompliance_dateofcompliance());
   }
   else
   {
    pstmt.setNull(12, Types.TIMESTAMP);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_dateoflastscan()))
   {
    pstmt.setString(13, cardholderProfileVO.getCompliance_dateoflastscan());
    logger.debug("getCompliance_dateoflastscan" + cardholderProfileVO.getCompliance_dateoflastscan());
   }
   else
   {
    pstmt.setNull(13, Types.TIMESTAMP);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise()))
   {
    pstmt.setString(14, cardholderProfileVO.getCompliance_datacompromise());
   }
   else
   {
    pstmt.setString(14, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise_yes()))
   {
    pstmt.setString(15, cardholderProfileVO.getCompliance_datacompromise_yes());
    logger.debug("getCompliance_datacompromise_yes" + cardholderProfileVO.getCompliance_datacompromise_yes());
   }
   else
   {
    pstmt.setNull(15, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_merchant()))
   {
    pstmt.setString(16, cardholderProfileVO.getSiteinspection_merchant());
    logger.debug("getSiteinspection_merchant" + cardholderProfileVO.getSiteinspection_merchant());
   }
   else
   {
    pstmt.setString(16, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_landlord()))
   {
    pstmt.setString(17, cardholderProfileVO.getSiteinspection_landlord());
    logger.debug("getSiteinspection_landlord" + cardholderProfileVO.getSiteinspection_landlord());
   }
   else
   {
    pstmt.setNull(17, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_buildingtype()))
   {
    pstmt.setString(18, cardholderProfileVO.getSiteinspection_buildingtype());
    logger.debug("getSiteinspection_buildingtype" + cardholderProfileVO.getSiteinspection_buildingtype());
   }
   else
   {
    pstmt.setString(18, "OfficeBldg");
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()))
   {
    pstmt.setString(19, cardholderProfileVO.getSiteinspection_areazoned());
    logger.debug("getSiteinspection_areazoned" + cardholderProfileVO.getSiteinspection_areazoned());

   }
   else
   {
    pstmt.setString(19, "Commercial");
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_squarefootage()))
   {
    pstmt.setString(20, cardholderProfileVO.getSiteinspection_squarefootage());
    logger.debug("getSiteinspection_squarefootage" + cardholderProfileVO.getSiteinspection_squarefootage());

   }
   else
   {
    pstmt.setString(20, "0-500");
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_operatebusiness()))
   {
    pstmt.setString(21, cardholderProfileVO.getSiteinspection_operatebusiness());
    logger.debug("getSiteinspection_operatebusiness" + cardholderProfileVO.getSiteinspection_operatebusiness());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1()))
   {
    pstmt.setString(22, cardholderProfileVO.getSiteinspection_principal1());
   }
   else
   {
    pstmt.setNull(22, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1_date()))
   {
    pstmt.setString(23, cardholderProfileVO.getSiteinspection_principal1_date());
    logger.debug("getSiteinspection_principal1_date" + cardholderProfileVO.getSiteinspection_principal1_date());
   }
   else
   {
    pstmt.setNull(23, Types.TIMESTAMP);
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2()))
   {
    pstmt.setString(24, cardholderProfileVO.getSiteinspection_principal2());
   }
   else
   {
    pstmt.setNull(24, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2_date()))
   {
    pstmt.setString(25, cardholderProfileVO.getSiteinspection_principal2_date());
    logger.debug("getSiteinspection_principal2_date" + cardholderProfileVO.getSiteinspection_principal2_date());
   }
   else
   {
    pstmt.setNull(25, Types.TIMESTAMP);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes()))
   {
    pstmt.setString(26, cardholderProfileVO.getCompliance_pcidsscompliant_yes());
   }
   else
   {
    pstmt.setNull(26, Types.VARCHAR);
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant()))
   {
    pstmt.setString(27, cardholderProfileVO.getCompliance_cispcompliant());
   }
   else
   {
    pstmt.setString(27, "N");
   }
   if (functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant_yes()))
   {
    pstmt.setString(28, cardholderProfileVO.getCompliance_cispcompliant_yes());
   }
   else
   {
    pstmt.setNull(28, Types.VARCHAR);
   }
   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    cardholderProfileVO.setCardHolderProfileSaved("Y");
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertCardholderProfile");

  return false;
 }

 //update CompanyProfile

 // add extra details profile
 public boolean insertExtraDetailsProfile(ExtraDetailsProfileVO extraDetailsProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO applicationmanager_extradetailsprofile(application_id,company_financialreport,company_financialreportyes,financialreport_institution,financialreport_available,financialReport_availableyes,ownersince,socialsecurity,company_formparticipation,financialobligation,compliance_punitivesanction,compliance_punitivesanctionyes,workingexperience,goodsinsuranceoffered,fulfillment_productemail,fulfillment_productemailyes,blacklistedaccountclosed,blacklistedaccountclosedyes,shiping_deliverymethod,transactionmonitoringprocess,operationallicense,supervisorregularcontrole,deedofagreement,deedofagreementyes)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   pstmt = conn.prepareStatement(query);

   if (functions.isValueNull(extraDetailsProfileVO.getApplication_id()))
   {
    pstmt.setString(1, extraDetailsProfileVO.getApplication_id());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport()))
   {
    pstmt.setString(2, extraDetailsProfileVO.getCompany_financialReport());
   }
   else
   {
    pstmt.setString(2, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getCompany_financialReportYes()))
   {
    pstmt.setString(3, extraDetailsProfileVO.getCompany_financialReportYes());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getFinancialReport_institution()))
   {
    pstmt.setString(4, extraDetailsProfileVO.getFinancialReport_institution());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available()))
   {
    pstmt.setString(5, extraDetailsProfileVO.getFinancialReport_available());
   }
   else
   {
    pstmt.setString(5, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getFinancialReport_availableYes()))
   {
    pstmt.setString(6, extraDetailsProfileVO.getFinancialReport_availableYes());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getOwnerSince()))
   {
    pstmt.setString(7, extraDetailsProfileVO.getOwnerSince());
    logger.debug("ownersince" + extraDetailsProfileVO.getOwnerSince());
   }
   else
   {
    pstmt.setNull(7, Types.TIMESTAMP);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getSocialSecurity()))
   {
    pstmt.setString(8, extraDetailsProfileVO.getSocialSecurity());
    logger.debug("socialsecurity" + extraDetailsProfileVO.getSocialSecurity());
   }
   else
   {
    pstmt.setNull(8, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getCompany_formParticipation()))
   {
    pstmt.setString(9, extraDetailsProfileVO.getCompany_formParticipation());
    logger.debug("company_formparticipation" + extraDetailsProfileVO.getCompany_formParticipation());
   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getFinancialObligation()))
   {
    pstmt.setString(10, extraDetailsProfileVO.getFinancialObligation());
    logger.debug("financialobligation" + extraDetailsProfileVO.getFinancialObligation());
   }
   else
   {
    pstmt.setNull(10, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction()))
   {
    pstmt.setString(11, extraDetailsProfileVO.getCompliance_punitiveSanction());
   }
   else
   {
    pstmt.setString(11, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanctionYes()))
   {
    pstmt.setString(12, extraDetailsProfileVO.getCompliance_punitiveSanctionYes());
   }
   else
   {
    pstmt.setNull(12, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getWorkingExperience()))
   {
    pstmt.setString(13, extraDetailsProfileVO.getWorkingExperience());
    logger.debug("workingexperience" + extraDetailsProfileVO.getWorkingExperience());
   }
   else
   {
    pstmt.setNull(13, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getGoodsInsuranceOffered()))
   {
    pstmt.setString(14, extraDetailsProfileVO.getGoodsInsuranceOffered());
   }
   else
   {
    pstmt.setString(14, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail()))
   {
    pstmt.setString(15, extraDetailsProfileVO.getFulfillment_productEmail());
   }
   else
   {
    pstmt.setString(15, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmailYes()))
   {
    pstmt.setString(16, extraDetailsProfileVO.getFulfillment_productEmailYes());
   }
   else
   {
    pstmt.setNull(16, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed()))
   {
    pstmt.setString(17, extraDetailsProfileVO.getBlacklistedAccountClosed());
   }
   else
   {
    pstmt.setString(17, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosedYes()))
   {
    pstmt.setString(18, extraDetailsProfileVO.getBlacklistedAccountClosedYes());
   }
   else
   {
    pstmt.setNull(18, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getShiping_deliveryMethod()))
   {
    pstmt.setString(19, extraDetailsProfileVO.getShiping_deliveryMethod());
    logger.debug("shiping_deliverymethod" + extraDetailsProfileVO.getShiping_deliveryMethod());
   }
   else
   {
    pstmt.setNull(19, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getTransactionMonitoringProcess()))
   {
    pstmt.setString(20, extraDetailsProfileVO.getTransactionMonitoringProcess());
    logger.debug("transactionmonitoringprocess" + extraDetailsProfileVO.getTransactionMonitoringProcess());
   }
   else
   {
    pstmt.setNull(20, Types.VARCHAR);
   }
   if (functions.isValueNull(extraDetailsProfileVO.getOperationalLicense()))
   {
    pstmt.setString(21, extraDetailsProfileVO.getOperationalLicense());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getSupervisorregularcontrole()))
   {
    pstmt.setString(22, extraDetailsProfileVO.getSupervisorregularcontrole());
   }
   else
   {
    pstmt.setString(22, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement()))
   {
    pstmt.setString(23, extraDetailsProfileVO.getDeedOfAgreement());
   }
   else
   {
    pstmt.setString(23, "N");
   }
   if (functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreementYes()))
   {
    pstmt.setString(24, extraDetailsProfileVO.getDeedOfAgreementYes());
   }
   else
   {
    pstmt.setNull(24, Types.VARCHAR);
   }
   int k = pstmt.executeUpdate();
   if (k>0)
   {
    extraDetailsProfileVO.setExtraDetailsProfileSaved("Y");
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertExtraDetailsProfile");

  return false;
 }

 public boolean updateCompanyProfile(CompanyProfileVO companyProfileVO)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   String updatecompanyprofile = "update companyprofile set company_bankruptcy=?,company_bankruptcydate=?,company_typeofbusiness=?,company_lengthoftime_business=?,company_capitalresources=?,company_currencylastyear=?,company_turnoverlastyear=?,company_turnoverlastyear_unit=?,company_numberofemployees=?,License_required=?,License_Permission=?,legal_proceeding=?,startup_business=?,iscompany_insured=?,insured_companyname=?,main_business_partner=?,loans=?,income_economic_activity=?,interest_income=?,investments=?,income_sources_other=?,income_sources_other_yes=?,insured_amount=?,insured_currency=?,countryofregistration=? where application_id =?";
   pstmt = conn.prepareStatement(updatecompanyprofile);

   if (functions.isValueNull(companyProfileVO.getCompanyBankruptcy()))
   {
    pstmt.setString(1, companyProfileVO.getCompanyBankruptcy());
   }
   else
   {
    pstmt.setString(1, "N");
   }
   if (functions.isValueNull(companyProfileVO.getCompanyBankruptcydate()))
   {
    pstmt.setString(2, companyProfileVO.getCompanyBankruptcydate());
   }
   else
   {
    pstmt.setNull(2, Types.TIMESTAMP);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()))
   {
    pstmt.setString(3, companyProfileVO.getCompanyTypeOfBusiness());
   }
   else
   {
    pstmt.setString(3, "NotforProfit");
   }
   if (functions.isValueNull(companyProfileVO.getCompanyLengthOfTimeInBusiness()))
   {
    pstmt.setString(4, companyProfileVO.getCompanyLengthOfTimeInBusiness());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyCapitalResources()))
   {
    pstmt.setString(5, companyProfileVO.getCompanyCapitalResources());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompany_currencylastyear()))
   {
    pstmt.setString(6, companyProfileVO.getCompany_currencylastyear());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyTurnoverLastYear()))
   {
    pstmt.setString(7, companyProfileVO.getCompanyTurnoverLastYear());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompany_turnoverlastyear_unit()))
   {
    pstmt.setString(8, companyProfileVO.getCompany_turnoverlastyear_unit());
   }
   else
   {
    pstmt.setNull(8, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getCompanyNumberOfEmployees()))
   {
    pstmt.setString(9, companyProfileVO.getCompanyNumberOfEmployees());
   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }
   if (functions.isValueNull(companyProfileVO.getLicense_required()))
   {
    pstmt.setString(10, companyProfileVO.getLicense_required());
   }
   else
   {
    pstmt.setString(10, "N");
   }
   if (functions.isValueNull(companyProfileVO.getLicense_Permission()))
   {
    pstmt.setString(11, companyProfileVO.getLicense_Permission());
   }
   else
   {
    pstmt.setString(11, "N");
   }
   if(functions.isValueNull(companyProfileVO.getLegalProceeding()))
   {
    pstmt.setString(12,companyProfileVO.getLegalProceeding());
   }
   else
   {
    pstmt.setString(12, "N");
   }
   if(functions.isValueNull(companyProfileVO.getStartup_business()))
   {
    pstmt.setString(13,companyProfileVO.getStartup_business());
   }
   else
   {
    pstmt.setString(13, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIscompany_insured()))
   {
    pstmt.setString(14,companyProfileVO.getIscompany_insured());
   }
   else
   {
    pstmt.setString(14, "N");
   }
   if(functions.isValueNull(companyProfileVO.getInsured_companyname()))
   {
    pstmt.setString(15,companyProfileVO.getInsured_companyname());
   }
   else
   {
    pstmt.setNull(15, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getMain_business_partner()))
   {
    pstmt.setString(16,companyProfileVO.getMain_business_partner());
   }
   else
   {
    pstmt.setNull(16, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getLoans()))
   {
    pstmt.setString(17,companyProfileVO.getLoans());
   }
   else
   {
    pstmt.setString(17, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIncome_economic_activity()))
   {
    pstmt.setString(18,companyProfileVO.getIncome_economic_activity());
   }
   else
   {
    pstmt.setString(18, "N");
   }
   if(functions.isValueNull(companyProfileVO.getInterest_income()))
   {
    pstmt.setString(19,companyProfileVO.getInterest_income());
   }
   else
   {
    pstmt.setString(19, "N");
   }
   if(functions.isValueNull(companyProfileVO.getInvestments()))
   {
    pstmt.setString(20,companyProfileVO.getInvestments());
   }
   else
   {
    pstmt.setString(20, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIncome_sources_other()))
   {
    pstmt.setString(21,companyProfileVO.getIncome_sources_other());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if(functions.isValueNull(companyProfileVO.getIncome_sources_other_yes()))
   {
    pstmt.setString(22,companyProfileVO.getIncome_sources_other_yes());
   }
   else
   {
    pstmt.setNull(22, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getInsured_amount()))
   {
    pstmt.setString(23,companyProfileVO.getInsured_amount());
   }
   else
   {
    pstmt.setNull(23, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getInsured_currency()))
   {
    pstmt.setString(24,companyProfileVO.getInsured_currency());
   }
   else
   {
    pstmt.setNull(24, Types.VARCHAR);
   }
   if(functions.isValueNull(companyProfileVO.getCountryOfRegistration()))
   {
    pstmt.setString(25,companyProfileVO.getCountryOfRegistration());
   }
   else
   {
    pstmt.setNull(25, Types.VARCHAR);
   }
   pstmt.setString(26, companyProfileVO.getApplicationId());

   int k = pstmt.executeUpdate();
   logger.debug("Update CompanyProfile------"+pstmt);
   if (k > 0)
   {
    updateAddressAndIdentificationDetails(companyProfileVO.getApplicationId(), companyProfileVO.getCompanyProfile_addressVOMap());
    updateContactDetails(companyProfileVO.getApplicationId(), companyProfileVO.getCompanyProfile_contactInfoVOMap());
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   //throw new SystemError("Error : " + e.getMessage());
  }
  catch (SystemError e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SystemError as System Error :::: ", e);
   //  throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateCompanyProfile");

  return false;
 }

 //update OwnerShipProfile
 public boolean updateOwnershipProfile(OwnershipProfileVO ownershipProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String status = "failed";
  try
  {
   conn = Database.getConnection();
   String updateownershipprofile = "update ownershipprofile set numOfShareholders=?,numOfCorporateShareholders=?,numOfDirectors=?,numOfAuthrisedSignatory=? where application_id=? ";

   pstmt = conn.prepareStatement(updateownershipprofile);
   if (functions.isValueNull(ownershipProfileVO.getNumOfShareholders()))
   {
    pstmt.setString(1, ownershipProfileVO.getNumOfShareholders());
   }
   else
   {
    pstmt.setNull(1, Types.VARCHAR);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfCorporateShareholders()))
   {
    pstmt.setString(2, ownershipProfileVO.getNumOfCorporateShareholders());
   }
   else
   {
    pstmt.setNull(2, Types.VARCHAR);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfDirectors()))
   {
    pstmt.setString(3, ownershipProfileVO.getNumOfDirectors());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if (functions.isValueNull(ownershipProfileVO.getNumOfAuthrisedSignatory()))
   {
    pstmt.setString(4, ownershipProfileVO.getNumOfAuthrisedSignatory());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   pstmt.setString(5, ownershipProfileVO.getApplicationid());

   int k = pstmt.executeUpdate();
   if (k > 0)
   {
    updateOwnershipProfileDetails(ownershipProfileVO.getApplicationid(), ownershipProfileVO.getOwnershipProfileDetailsVOMap());
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError(  "Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateOwnershipProfile");
  return false;
 }

 //update BusinessProfile
 public boolean updateBusinessProfile(BusinessProfileVO businessProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();
   String updatebusinessprofile="update applicationmanager_businessprofile set foreigntransactions_us=?,foreigntransactions_Europe=?,foreigntransactions_Asia=?,foreigntransactions_RestoftheWorld=?,methodofacceptance_moto=?,methodofacceptance_internet=?,methodofacceptance_swipe=?,averageticket=?,highestticket=?,urls=?,descriptor_creditcardstmt=?,descriptionofproducts=?,recurringservices=?,recurringservicesyes=?,isacallcenterusedyes=?,isafulfillmenthouseused=?,isafulfillmenthouseused_yes=?,cardtypesaccepted_visa=?,cardtypesaccepted_mastercard=?,cardtypesaccepted_americanexpress=?,cardtypesaccepted_discover=?,cardtypesaccepted_diners=?,cardtypesaccepted_jcb=?,cardtypesaccepted_other=?,cardtypesaccepted_other_yes=?,kyc_processes=?,visa_cardlogos=?,threeD_secure_compulsory=?,price_displayed=?,transaction_currency=?,cardholder_asked=?,dynamic_descriptors=?,shopping_cart=?,shopping_cart_details=?,pricing_policies_website=?,fulfillment_timeframe=?,goods_policy=?,MCC_Ctegory=?,countries_blocked=?,countries_blocked_details=?,customer_support=?,affiliate_programs=?,affiliate_programs_details=?,listfraudtools=?,customers_identification=?,coolingoffperiod=?,customersupport_email=?,custsupportwork_hours=?,technical_contact=?,foreigntransactions_cis=?,foreigntransactions_canada=? ,securitypolicy=?,confidentialitypolicy=?,applicablejurisdictions=?,privacy_anonymity_dataprotection=?,App_Services=?,product_requires=?,lowestticket=?,timeframe=?,livechat=?,login_id=?,password=?,companyidentifiable=?,clearlypresented=?,trackingnumber=?,domainsowned=?,sslsecured=?,copyright=?,sourcecontent=?,directmail=?,Yellowpages=?,radiotv=?,internet=?,networking=?,outboundtelemarketing=?,inhouselocation=?,contactperson=?,otherlocation=?,mainsuppliers=?,shipmentassured=?,billing_model=?,billing_timeframe=?,recurring_amount=?,automatic_recurring=?,multiple_membership=?,free_membership=?,creditcard_Required=?,automatically_billed=?,pre_authorization=?,merchantcode=?,ipaddress=?,shopsystem_plugin=?,direct_debit_sepa=?,alternative_payments=?,risk_management=?,payment_engine=?,webhost_company_name=?,webhost_phone=?,webhost_email=?,webhost_website=?,webhost_address=?,payment_company_name=?,payment_phone=?,payment_email=?,payment_website=?,payment_address=?,callcenter_phone=?,callcenter_email=?,callcenter_website=?,callcenter_address=?,shoppingcart_company_name=?,shoppingcart_phone=?,shoppingcart_email=?,shoppingcart_website=?,shoppingcart_address=?,seasonal_fluctuating=?,creditor_id=?,payment_delivery=?,payment_delivery_otheryes=?,goods_delivery=?,terminal_type=?,terminal_type_otheryes=?,cardvolume_visa=?,cardvolume_mastercard=?,cardvolume_americanexpress=?,cardvolume_dinner=?,cardvolume_other=?,cardvolume_discover=?,payment_type_yes=?,orderconfirmation_post=?,orderconfirmation_email=?,orderconfirmation_sms=?,orderconfirmation_other=?,orderconfirmation_other_yes=?,master_cardlogos=?,physicalgoods_delivered=?,viainternetgoods_delivered=?,shipping_contactemail=?,seasonal_fluctuating_yes=?,cardtypesaccepted_rupay=?,cardvolume_rupay=?,foreigntransactions_uk=?,is_website_live=?,test_link=?,listfraudtools_yes=?,domainsowned_no=?,agency_employed=?,agency_employed_yes=?,customers_identification_yes=?,pricing_policies_website_yes=?,one_time_percentage=?,moto_percentage=?,recurring_percentage=?,threedsecure_percentage=?,internet_percentage=?,swipe_percentage=?,cardvolume_jcb=?,paymenttype_credit=?,paymenttype_debit=?,paymenttype_netbanking=?,paymenttype_wallet=?,paymenttype_alternate=?,product_sold_currencies=? where application_id=?";
   pstmt= conn.prepareStatement(updatebusinessprofile);

   if(functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
   {
    pstmt.setString(1,businessProfileVO.getForeigntransactions_us());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
   {
    pstmt.setString(2,businessProfileVO.getForeigntransactions_Europe());
   }
   else
   {
    pstmt.setNull(2,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
   {
    pstmt.setString(3,businessProfileVO.getForeigntransactions_Asia());
   }
   else
   {
    pstmt.setNull(3,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
   {
    pstmt.setString(4,businessProfileVO.getForeigntransactions_RestoftheWorld());
   }
   else
   {
    pstmt.setNull(4,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getMethodofacceptance_moto()))
   {
    pstmt.setString(5,businessProfileVO.getMethodofacceptance_moto());
   }
   else
   {
    pstmt.setNull(5,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getMethodofacceptance_internet()))
   {
    pstmt.setString(6,businessProfileVO.getMethodofacceptance_internet());
   }
   else
   {
    pstmt.setNull(6,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getMethodofacceptance_swipe()))
   {
    pstmt.setString(7,businessProfileVO.getMethodofacceptance_swipe());
   }
   else
   {
    pstmt.setNull(7,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getAverageticket()))
   {
    pstmt.setString(8,businessProfileVO.getAverageticket());
   }
   else
   {
    pstmt.setNull(8,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getHighestticket()))
   {
    pstmt.setString(9,businessProfileVO.getHighestticket());
   }
   else
   {
    pstmt.setNull(9,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getUrls()))
   {
    pstmt.setString(10,businessProfileVO.getUrls());
   }
   else
   {
    pstmt.setNull(10,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getDescriptor()))
   {
    pstmt.setString(11,businessProfileVO.getDescriptor());
   }
   else
   {
    pstmt.setNull(11,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getDescriptionofproducts()))
   {
    pstmt.setString(12,businessProfileVO.getDescriptionofproducts());
   }
   else
   {
    pstmt.setNull(12,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getRecurringservices()))
   {
    pstmt.setString(13,businessProfileVO.getRecurringservices());
   }
   else
   {
    pstmt.setString(13, "N");
   }
   if(functions.isValueNull(businessProfileVO.getRecurringservicesyes()))
   {
    pstmt.setString(14,businessProfileVO.getRecurringservicesyes());
   }
   else
   {
    pstmt.setNull(14,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getIsacallcenterusedyes()))
   {
    pstmt.setString(15,businessProfileVO.getIsacallcenterusedyes());
   }
   else
   {
    pstmt.setNull(15,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused()))
   {
    pstmt.setString(16,businessProfileVO.getIsafulfillmenthouseused());
   }
   else
   {
    pstmt.setString(16, "N");
   }
   if(functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused_yes()))
   {
    pstmt.setString(17,businessProfileVO.getIsafulfillmenthouseused_yes());
   }
   else
   {
    pstmt.setNull(17,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_visa()))
   {
    pstmt.setString(18,businessProfileVO.getCardtypesaccepted_visa());
   }
   else
   {
    pstmt.setString(18, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_mastercard()))
   {
    pstmt.setString(19,businessProfileVO.getCardtypesaccepted_mastercard());
   }
   else
   {
    pstmt.setString(19, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_americanexpress()))
   {
    pstmt.setString(20,businessProfileVO.getCardtypesaccepted_americanexpress());
   }
   else
   {
    pstmt.setString(20, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_discover()))
   {
    pstmt.setString(21,businessProfileVO.getCardtypesaccepted_discover());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_diners()))
   {
    pstmt.setString(22,businessProfileVO.getCardtypesaccepted_diners());
   }
   else
   {
    pstmt.setString(22, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_jcb()))
   {
    pstmt.setString(23,businessProfileVO.getCardtypesaccepted_jcb());
   }
   else
   {
    pstmt.setString(23, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_other()))
   {
    pstmt.setString(24,businessProfileVO.getCardtypesaccepted_other());
   }
   else
   {
    pstmt.setString(24, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_other_yes()))
   {
    pstmt.setString(25,businessProfileVO.getCardtypesaccepted_other_yes());
   }
   else
   {
    pstmt.setNull(25,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getKyc_processes()))
   {
    pstmt.setString(26,businessProfileVO.getKyc_processes());
   }
   else
   {
    pstmt.setString(26, "N");
   }
   if(functions.isValueNull(businessProfileVO.getVisa_cardlogos()))
   {
    pstmt.setString(27,businessProfileVO.getVisa_cardlogos());
   }
   else
   {
    pstmt.setString(27, "N");
   }
   if(functions.isValueNull(businessProfileVO.getThreeD_secure_compulsory()))
   {
    pstmt.setString(28,businessProfileVO.getThreeD_secure_compulsory());
   }
   else
   {
    pstmt.setString(28, "N");
   }
   if(functions.isValueNull(businessProfileVO.getPrice_displayed()))
   {
    pstmt.setString(29,businessProfileVO.getPrice_displayed());
   }
   else
   {
    pstmt.setString(29, "N");
   }
   if(functions.isValueNull(businessProfileVO.getTransaction_currency()))
   {
    pstmt.setString(30,businessProfileVO.getTransaction_currency());
   }
   else
   {
    pstmt.setString(30, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardholder_asked()))
   {
    pstmt.setString(31,businessProfileVO.getCardholder_asked());
   }
   else
   {
    pstmt.setString(31, "N");
   }
   if(functions.isValueNull(businessProfileVO.getDynamic_descriptors()))
   {
    pstmt.setString(32,businessProfileVO.getDynamic_descriptors());
   }
   else
   {
    pstmt.setString(32, "N");
   }
   if(functions.isValueNull(businessProfileVO.getShopping_cart()))
   {
    pstmt.setString(33,businessProfileVO.getShopping_cart());
   }
   else
   {
    pstmt.setString(33, "N");
   }
   if(functions.isValueNull(businessProfileVO.getShopping_cart_details()))
   {
    pstmt.setString(34,businessProfileVO.getShopping_cart_details());
   }
   else
   {
    pstmt.setNull(34,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getPricing_policies_website()))
   {
    pstmt.setString(35,businessProfileVO.getPricing_policies_website());
   }
   else
   {
    pstmt.setString(35, "N");
   }
   if(functions.isValueNull(businessProfileVO.getFulfillment_timeframe()))
   {
    pstmt.setString(36,businessProfileVO.getFulfillment_timeframe());
   }
   else
   {
    pstmt.setString(36, "N");
   }
   if(functions.isValueNull(businessProfileVO.getGoods_policy()))
   {
    pstmt.setString(37,businessProfileVO.getGoods_policy());
   }
   else
   {
    pstmt.setString(37, "N");
   }
   if(functions.isValueNull(businessProfileVO.getMCC_Ctegory()))
   {
    pstmt.setString(38,businessProfileVO.getMCC_Ctegory());
   }
   else
   {
    pstmt.setString(38, "N");
   }

   if(functions.isValueNull(businessProfileVO.getCountries_blocked()))
   {
    pstmt.setString(39,businessProfileVO.getCountries_blocked());
   }
   else
   {
    pstmt.setString(39, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCountries_blocked_details()))
   {
    pstmt.setString(40,businessProfileVO.getCountries_blocked_details());
   }
   else
   {
    pstmt.setNull(40,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCustomer_support()))
   {
    pstmt.setString(41,businessProfileVO.getCustomer_support());
   }
   else
   {
    pstmt.setString(41, "N");
   }
   if(functions.isValueNull(businessProfileVO.getAffiliate_programs()))
   {
    pstmt.setString(42,businessProfileVO.getAffiliate_programs());
   }
   else
   {
    pstmt.setString(42, "N");
   }
   if(functions.isValueNull(businessProfileVO.getAffiliate_programs_details()))
   {
    pstmt.setString(43,businessProfileVO.getAffiliate_programs_details());
   }
   else
   {
    pstmt.setNull(43,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getListfraudtools()))
   {
    pstmt.setString(44,businessProfileVO.getListfraudtools());
   }
   else
   {
    pstmt.setNull(44,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCustomers_identification()))
   {
    pstmt.setString(45,businessProfileVO.getCustomers_identification());
   }
   else
   {
    pstmt.setNull(45,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCoolingoffperiod()))
   {
    pstmt.setString(46,businessProfileVO.getCoolingoffperiod());
   }
   else
   {
    pstmt.setNull(46,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCustomersupport_email()))
   {
    pstmt.setString(47,businessProfileVO.getCustomersupport_email());
   }
   else
   {
    pstmt.setNull(47,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCustsupportwork_hours()))
   {
    pstmt.setString(48,businessProfileVO.getCustsupportwork_hours());
   }
   else
   {
    pstmt.setNull(48,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getTechnical_contact()))
   {
    pstmt.setString(49,businessProfileVO.getTechnical_contact());
   }
   else
   {
    pstmt.setNull(49,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
   {
    pstmt.setString(50,businessProfileVO.getForeigntransactions_cis());
   }
   else
   {
    pstmt.setNull(50,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
   {
    pstmt.setString(51,businessProfileVO.getForeigntransactions_canada());
   }
   else
   {
    pstmt.setNull(51,Types.VARCHAR);
   }

   //add specific
   if(functions.isValueNull(businessProfileVO.getSecuritypolicy()))
   {
    pstmt.setString(52,businessProfileVO.getSecuritypolicy());
   }
   else
   {
    pstmt.setString(52,"N");
   }
   if(functions.isValueNull(businessProfileVO.getConfidentialitypolicy()))
   {
    pstmt.setString(53,businessProfileVO.getConfidentialitypolicy());
   }
   else
   {
    pstmt.setString(53,"N");
   }
   if(functions.isValueNull(businessProfileVO.getApplicablejurisdictions()))
   {
    pstmt.setString(54,businessProfileVO.getApplicablejurisdictions());
   }
   else
   {
    pstmt.setString(54,"N");
   }
   if(functions.isValueNull(businessProfileVO.getPrivacy_anonymity_dataprotection()))
   {
    pstmt.setString(55,businessProfileVO.getPrivacy_anonymity_dataprotection());
   }
   else
   {
    pstmt.setString(55,"N");
   }

   if(functions.isValueNull(businessProfileVO.getApp_Services()))
   {
    pstmt.setString(56,businessProfileVO.getApp_Services());
   }
   else
   {
    pstmt.setString(56,"N");
   }
   if(functions.isValueNull(businessProfileVO.getProduct_requires()))
   {
    pstmt.setString(57,businessProfileVO.getProduct_requires());
   }
   else
   {
    pstmt.setString(57,"N");
   }
   //ADD new
   if(functions.isValueNull(businessProfileVO.getLowestticket()))
   {
    pstmt.setString(58,businessProfileVO.getLowestticket());
   }
   else
   {
    pstmt.setNull(58,Types.VARCHAR);

   }
   //add new
   if(functions.isValueNull(businessProfileVO.getTimeframe()))
   {
    pstmt.setString(59,businessProfileVO.getTimeframe());
   }
   else
   {
    pstmt.setNull(59,Types.VARCHAR);

   }
   if(functions.isValueNull(businessProfileVO.getLivechat()))
   {
    pstmt.setString(60,businessProfileVO.getLivechat());
   }
   else
   {

    pstmt.setString(60, "N");
   }
   //Add new
   if (functions.isValueNull(businessProfileVO.getLoginId()))
   {
    pstmt.setString(61,businessProfileVO.getLoginId());
   }
   else
   {
    pstmt.setNull(61, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPassWord()))
   {
    pstmt.setString(62,businessProfileVO.getPassWord());
   }
   else
   {
    pstmt.setNull(62, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCompanyIdentifiable()))
   {
    pstmt.setString(63,businessProfileVO.getCompanyIdentifiable());
   }
   else
   {
    pstmt.setString(63,"N");
   }
   if(functions.isValueNull(businessProfileVO.getClearlyPresented()))
   {
    pstmt.setString(64,businessProfileVO.getClearlyPresented());
   }
   else
   {
    pstmt.setString(64,"N");
   }
   if(functions.isValueNull(businessProfileVO.getTrackingNumber()))
   {
    pstmt.setString(65,businessProfileVO.getTrackingNumber());
   }
   else
   {
    pstmt.setString(65,"N");
   }
   if(functions.isValueNull(businessProfileVO.getDomainsOwned()))
   {
    pstmt.setString(66,businessProfileVO.getDomainsOwned());
   }
   else
   {
    pstmt.setString(66,"N");
   }
   if(functions.isValueNull(businessProfileVO.getSslSecured()))
   {
    pstmt.setString(67,businessProfileVO.getSslSecured());
   }
   else
   {
    pstmt.setString(67,"N");
   }
   if(functions.isValueNull(businessProfileVO.getCopyright()))
   {
    pstmt.setString(68,businessProfileVO.getCopyright());
   }
   else
   {
    pstmt.setString(68,"N");
   }
   if(functions.isValueNull(businessProfileVO.getSourceContent()))
   {
    pstmt.setString(69,businessProfileVO.getSourceContent());
   }
   else
   {
    pstmt.setNull(69,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getDirectMail()))
   {
    pstmt.setString(70,businessProfileVO.getDirectMail());
   }
   else
   {
    pstmt.setString(70, "N");
   }
   if(functions.isValueNull(businessProfileVO.getYellowPages()))
   {
    pstmt.setString(71,businessProfileVO.getYellowPages());
   }
   else
   {
    pstmt.setString(71, "N");
   }
   if(functions.isValueNull(businessProfileVO.getRadioTv()))
   {
    pstmt.setString(72,businessProfileVO.getRadioTv());
   }
   else
   {
    pstmt.setString(72, "N");
   }
   if(functions.isValueNull(businessProfileVO.getInternet()))
   {
    pstmt.setString(73,businessProfileVO.getInternet());
   }
   else
   {
    pstmt.setString(73, "N");
   }
   if(functions.isValueNull(businessProfileVO.getNetworking()))
   {
    pstmt.setString(74,businessProfileVO.getNetworking());
   }
   else
   {
    pstmt.setString(74, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOutboundTelemarketing()))
   {
    pstmt.setString(75,businessProfileVO.getOutboundTelemarketing());
   }
   else
   {
    pstmt.setString(75, "N");
   }
   if (functions.isValueNull(businessProfileVO.getInHouseLocation()))
   {
    pstmt.setString(76,businessProfileVO.getInHouseLocation());
   }
   else
   {
    pstmt.setNull(76, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getContactPerson()))
   {
    pstmt.setString(77,businessProfileVO.getContactPerson());
   }
   else
   {
    pstmt.setNull(77, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getOtherLocation()))
   {
    pstmt.setString(78,businessProfileVO.getOtherLocation());
   }
   else
   {
    pstmt.setNull(78, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getMainSuppliers()))
   {
    pstmt.setString(79,businessProfileVO.getMainSuppliers());
   }
   else
   {
    pstmt.setNull(79, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getShipmentAssured()))
   {
    pstmt.setString(80,businessProfileVO.getShipmentAssured());
   }
   else
   {
    pstmt.setString(80, "N");
   }
   //ADD new
   if (functions.isValueNull(businessProfileVO.getBillingModel()))
   {
    pstmt.setString(81,businessProfileVO.getBillingModel());
   }
   else
   {
    pstmt.setString(81, "one_time");
   }
   if (functions.isValueNull(businessProfileVO.getBillingTimeFrame()))
   {
    pstmt.setString(82,businessProfileVO.getBillingTimeFrame());
   }
   else
   {
    pstmt.setNull(82, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getRecurringAmount()))
   {
    pstmt.setString(83,businessProfileVO.getRecurringAmount());
   }
   else
   {
    pstmt.setNull(83, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getAutomaticRecurring()))
   {
    pstmt.setString(84,businessProfileVO.getAutomaticRecurring());
   }
   else
   {
    pstmt.setString(84, "N");
   }
   if (functions.isValueNull(businessProfileVO.getMultipleMembership()))
   {
    pstmt.setString(85,businessProfileVO.getMultipleMembership());
   }
   else
   {
    pstmt.setString(85, "N");
   }
   if (functions.isValueNull(businessProfileVO.getFreeMembership()))
   {
    pstmt.setString(86,businessProfileVO.getFreeMembership());
   }
   else
   {
    pstmt.setString(86, "N");
   }
   if (functions.isValueNull(businessProfileVO.getCreditCardRequired()))
   {
    pstmt.setString(87,businessProfileVO.getCreditCardRequired());
   }
   else
   {
    pstmt.setString(87, "N");
   }
   if (functions.isValueNull(businessProfileVO.getAutomaticallyBilled()))
   {
    pstmt.setString(88,businessProfileVO.getAutomaticallyBilled());
   }
   else
   {
    pstmt.setString(88, "N");
   }
   if (functions.isValueNull(businessProfileVO.getPreAuthorization()))
   {
    pstmt.setString(89,businessProfileVO.getPreAuthorization());
   }
   else
   {
    pstmt.setString(89, "N");
   }
   if (functions.isValueNull(businessProfileVO.getMerchantCode()))
   {
    pstmt.setString(90,businessProfileVO.getMerchantCode());
   }
   else
   {
    pstmt.setNull(90, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getIpaddress()))
   {
    pstmt.setString(91,businessProfileVO.getIpaddress());
   }
   else
   {
    pstmt.setNull(91, Types.VARCHAR);
   }

   // Wirecard requirement added in Business Profile

   if (functions.isValueNull(businessProfileVO.getShopsystem_plugin()))
   {
    pstmt.setString(92,businessProfileVO.getShopsystem_plugin());
   }
   else
   {
    pstmt.setNull(92, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getDirect_debit_sepa()))
   {
    pstmt.setString(93,businessProfileVO.getDirect_debit_sepa());
   }
   else
   {
    pstmt.setNull(93, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getAlternative_payments()))
   {
    pstmt.setString(94,businessProfileVO.getAlternative_payments());
   }
   else
   {
    pstmt.setNull(94, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getRisk_management()))
   {
    pstmt.setString(95,businessProfileVO.getRisk_management());
   }
   else
   {
    pstmt.setNull(95, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_engine()))
   {
    pstmt.setString(96,businessProfileVO.getPayment_engine());
   }
   else
   {
    pstmt.setNull(96, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_company_name()))
   {
    pstmt.setString(97,businessProfileVO.getWebhost_company_name());
   }
   else
   {
    pstmt.setNull(97, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_phone()))
   {
    pstmt.setString(98,businessProfileVO.getWebhost_phone());
   }
   else
   {
    pstmt.setNull(98, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_email()))
   {
    pstmt.setString(99,businessProfileVO.getWebhost_email());
   }
   else
   {
    pstmt.setNull(99, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_website()))
   {
    pstmt.setString(100,businessProfileVO.getWebhost_website());
   }
   else
   {
    pstmt.setNull(100, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getWebhost_address()))
   {
    pstmt.setString(101,businessProfileVO.getWebhost_address());
   }
   else
   {
    pstmt.setNull(101, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_company_name()))
   {
    pstmt.setString(102,businessProfileVO.getPayment_company_name());
   }
   else
   {
    pstmt.setNull(102, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_phone()))
   {
    pstmt.setString(103,businessProfileVO.getPayment_phone());
   }
   else
   {
    pstmt.setNull(103, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_email()))
   {
    pstmt.setString(104,businessProfileVO.getPayment_email());
   }
   else
   {
    pstmt.setNull(104, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_website()))
   {
    pstmt.setString(105,businessProfileVO.getPayment_website());
   }
   else
   {
    pstmt.setNull(105, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getPayment_address()))
   {
    pstmt.setString(106,businessProfileVO.getPayment_address());
   }
   else
   {
    pstmt.setNull(106, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_phone()))
   {
    pstmt.setString(107,businessProfileVO.getCallcenter_phone());
   }
   else
   {
    pstmt.setNull(107, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_email()))
   {
    pstmt.setString(108,businessProfileVO.getCallcenter_email());
   }
   else
   {
    pstmt.setNull(108, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_website()))
   {
    pstmt.setString(109,businessProfileVO.getCallcenter_website());
   }
   else
   {
    pstmt.setNull(109, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getCallcenter_address()))
   {
    pstmt.setString(110,businessProfileVO.getCallcenter_address());
   }
   else
   {
    pstmt.setNull(110, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_company_name()))
   {
    pstmt.setString(111,businessProfileVO.getShoppingcart_company_name());
   }
   else
   {
    pstmt.setNull(111, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_phone()))
   {
    pstmt.setString(112,businessProfileVO.getShoppingcart_phone());
   }
   else
   {
    pstmt.setNull(112, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_email()))
   {
    pstmt.setString(113,businessProfileVO.getShoppingcart_email());
   }
   else
   {
    pstmt.setNull(113, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_website()))
   {
    pstmt.setString(114,businessProfileVO.getShoppingcart_website());
   }
   else
   {
    pstmt.setNull(114, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getShoppingcart_address()))
   {
    pstmt.setString(115,businessProfileVO.getShoppingcart_address());
   }
   else
   {
    pstmt.setNull(115, Types.VARCHAR);
   }

   if (functions.isValueNull(businessProfileVO.getSeasonal_fluctuating()))
   {
    pstmt.setString(116,businessProfileVO.getSeasonal_fluctuating());
   }
   else
   {
    pstmt.setString(116, "N");
   }

   if (functions.isValueNull(businessProfileVO.getCreditor_id()))
   {
    pstmt.setString(117,businessProfileVO.getCreditor_id());
   }
   else
   {
    pstmt.setNull(117, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPayment_delivery()))
   {
    pstmt.setString(118,businessProfileVO.getPayment_delivery());
   }
   else
   {
    pstmt.setString(118, "upon_purchase");
   }

   if (functions.isValueNull(businessProfileVO.getPayment_delivery_otheryes()))
   {
    pstmt.setString(119,businessProfileVO.getPayment_delivery_otheryes());
   }
   else
   {
    pstmt.setNull(119, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getGoods_delivery()))
   {
    pstmt.setString(120,businessProfileVO.getGoods_delivery());
   }
   else
   {
    pstmt.setString(120, "over_internet");
   }
   if (functions.isValueNull(businessProfileVO.getTerminal_type()))
   {
    pstmt.setString(121,businessProfileVO.getTerminal_type());
   }
   else
   {
    pstmt.setString(121, "onetime_terminal");
   }
   if (functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
   {
    pstmt.setString(122,businessProfileVO.getTerminal_type_otheryes());
   }
   else
   {
    pstmt.setNull(122, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_visa()))
   {
    pstmt.setString(123,businessProfileVO.getCardvolume_visa());
   }
   else
   {
    pstmt.setNull(123,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_mastercard()))
   {
    pstmt.setString(124,businessProfileVO.getCardvolume_mastercard());
   }
   else
   {
    pstmt.setNull(124,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()))
   {
    pstmt.setString(125,businessProfileVO.getCardvolume_americanexpress());
   }
   else
   {
    pstmt.setNull(125,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_dinner()))
   {
    pstmt.setString(126,businessProfileVO.getCardvolume_dinner());
   }
   else
   {
    pstmt.setNull(126,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_other()))
   {
    pstmt.setString(127,businessProfileVO.getCardvolume_other());
   }
   else
   {
    pstmt.setNull(127,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_discover()))
   {
    pstmt.setString(128,businessProfileVO.getCardvolume_discover());
   }
   else
   {
    pstmt.setNull(128,Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getPayment_type_yes()))
   {
    pstmt.setString(129,businessProfileVO.getPayment_type_yes());
   }
   else
   {
    pstmt.setNull(129,Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_post()))
   {
    pstmt.setString(130,businessProfileVO.getOrderconfirmation_post());
   }
   else
   {
    pstmt.setString(130, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_email()))
   {
    pstmt.setString(131,businessProfileVO.getOrderconfirmation_email());
   }
   else
   {
    pstmt.setString(131,"N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_sms()))
   {
    pstmt.setString(132,businessProfileVO.getOrderconfirmation_sms());
   }
   else
   {
    pstmt.setString(132, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_other()))
   {
    pstmt.setString(133,businessProfileVO.getOrderconfirmation_other());
   }
   else
   {
    pstmt.setString(133, "N");
   }
   if(functions.isValueNull(businessProfileVO.getOrderconfirmation_other_yes()))
   {
    pstmt.setString(134,businessProfileVO.getOrderconfirmation_other_yes());
   }
   else
   {
    pstmt.setNull(134, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getMaster_cardlogos()))
   {
    pstmt.setString(135,businessProfileVO.getMaster_cardlogos());
   }
   else
   {
    pstmt.setString(135, "N");
   }
   if(functions.isValueNull(businessProfileVO.getPhysicalgoods_delivered()))
   {
    pstmt.setString(136,businessProfileVO.getPhysicalgoods_delivered());
   }
   else
   {
    pstmt.setString(136, "N");
   }
   if(functions.isValueNull(businessProfileVO.getViainternetgoods_delivered()))
   {
    pstmt.setString(137,businessProfileVO.getViainternetgoods_delivered());
   }
   else
   {
    pstmt.setString(137, "N");
   }
   if(functions.isValueNull(businessProfileVO.getShippingContactemail()))
   {
    pstmt.setString(138,businessProfileVO.getShippingContactemail());
   }
   else
   {
    pstmt.setNull(138, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getSeasonal_fluctuating_yes()))
   {
    pstmt.setString(139,businessProfileVO.getSeasonal_fluctuating_yes());
   }
   else
   {
    pstmt.setNull(139, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getCardtypesaccepted_rupay()))
   {
    pstmt.setString(140,businessProfileVO.getCardtypesaccepted_rupay());
   }
   else
   {
    pstmt.setString(140, "N");
   }
   if(functions.isValueNull(businessProfileVO.getCardvolume_rupay()))
   {
    pstmt.setString(141,businessProfileVO.getCardvolume_rupay());
   }
   else
   {
    pstmt.setNull(141, Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
   {
    pstmt.setString(142,businessProfileVO.getForeigntransactions_uk());
   }
   else
   {
    pstmt.setNull(142, Types.INTEGER);
   }
   if(functions.isValueNull(businessProfileVO.getIs_website_live()))
   {
    pstmt.setString(143,businessProfileVO.getIs_website_live());
   }
   else
   {
    pstmt.setString(143, "N");
   }
   if(functions.isValueNull(businessProfileVO.getTest_link()))
   {
    pstmt.setString(144,businessProfileVO.getTest_link());
   }
   else
   {
    pstmt.setNull(144, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getListfraudtools_yes()))
   {
    pstmt.setString(145,businessProfileVO.getListfraudtools_yes());
   }
   else
   {
    pstmt.setNull(145, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getDomainsOwned_no()))
   {
    pstmt.setString(146,businessProfileVO.getDomainsOwned_no());
   }
   else
   {
    pstmt.setNull(146, Types.VARCHAR);
   }
   if(functions.isValueNull(businessProfileVO.getAgency_employed()))
   {
    pstmt.setString(147,businessProfileVO.getAgency_employed());
   }
   else
   {
    pstmt.setString(147, "N");
   }
   if(functions.isValueNull(businessProfileVO.getAgency_employed_yes()))
   {
    pstmt.setString(148,businessProfileVO.getAgency_employed_yes());
   }
   else
   {
    pstmt.setNull(148, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getCustomers_identification_yes()))
   {
    pstmt.setString(149,businessProfileVO.getCustomers_identification_yes());
   }
   else
   {
    pstmt.setNull(149, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getPricing_policies_website_yes()))
   {
    pstmt.setString(150,businessProfileVO.getPricing_policies_website_yes());
   }
   else
   {
    pstmt.setNull(150, Types.VARCHAR);
   }
   if (functions.isValueNull(businessProfileVO.getOne_time_percentage()))
   {
    pstmt.setString(151, businessProfileVO.getOne_time_percentage());
   }
   else
   {
    pstmt.setNull(151, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getMoto_percentage()))
   {
    pstmt.setString(152, businessProfileVO.getMoto_percentage());
   }
   else
   {
    pstmt.setNull(152, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getRecurring_percentage()))
   {
    pstmt.setString(153, businessProfileVO.getRecurring_percentage());
   }
   else
   {
    pstmt.setNull(153, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getThreedsecure_percentage()))
   {
    pstmt.setString(154, businessProfileVO.getThreedsecure_percentage());
   }
   else
   {
    pstmt.setNull(154, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getInternet_percentage()))
   {
    pstmt.setString(155, businessProfileVO.getInternet_percentage());
   }
   else
   {
    pstmt.setNull(155, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getSwipe_percentage()))
   {
    pstmt.setString(156, businessProfileVO.getSwipe_percentage());
   }
   else
   {
    pstmt.setNull(156, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getCardvolume_jcb()))
   {
    pstmt.setString(157, businessProfileVO.getCardvolume_jcb());
   }
   else
   {
    pstmt.setNull(157, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_credit()))
   {
    pstmt.setString(158, businessProfileVO.getPaymenttype_credit());
   }
   else
   {
    pstmt.setNull(158, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_debit()))
   {
    pstmt.setString(159, businessProfileVO.getPaymenttype_debit());
   }
   else
   {
    pstmt.setNull(159, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_netbanking()))
   {
    pstmt.setString(160, businessProfileVO.getPaymenttype_netbanking());
   }
   else
   {
    pstmt.setNull(160, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_wallet()))
   {
    pstmt.setString(161, businessProfileVO.getPaymenttype_wallet());
   }
   else
   {
    pstmt.setNull(161, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getPaymenttype_alternate()))
   {
    pstmt.setString(162, businessProfileVO.getPaymenttype_alternate());
   }
   else
   {
    pstmt.setNull(162, Types.INTEGER);
   }
   if (functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
   {
    pstmt.setString(163, businessProfileVO.getProduct_sold_currencies());
   }
   else
   {
    pstmt.setNull(163, Types.VARCHAR);
   }
   pstmt.setString(164,businessProfileVO.getApplication_id());
   logger.debug("pstmt update businessprofile---->"+pstmt);
   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }
  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateBusinessProfile");

  return false;
 }

 //update Bank Profile
 public boolean updateBankProfile(BankProfileVO bankProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();
   String updatebankprofile="update applicationmanager_bankprofile set currencyrequested_productssold=?,currencyrequested_bankaccount=?,customer_trans_data=?,aquirer=?,reason_aquirer=?,bank_account_currencies=? where application_id=? ";
   pstmt= conn.prepareStatement(updatebankprofile);

   if(functions.isValueNull(bankProfileVO.getCurrencyrequested_productssold()))
   {
    pstmt.setString(1,bankProfileVO.getCurrencyrequested_productssold());
   }
   else
   {
    pstmt.setNull(1,Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getCurrencyrequested_bankaccount()))
   {
    pstmt.setString(2,bankProfileVO.getCurrencyrequested_bankaccount());
   }
   else
   {
    pstmt.setNull(2,Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getCustomer_trans_data()))
   {
    pstmt.setString(3,bankProfileVO.getCustomer_trans_data());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getAquirer()))
   {
    pstmt.setString(4,bankProfileVO.getAquirer());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getReasonaquirer()))
   {
    pstmt.setString(5,bankProfileVO.getReasonaquirer());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBank_account_currencies()))
   {
    pstmt.setString(6,bankProfileVO.getBank_account_currencies());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   pstmt.setString(7, bankProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateBankProfile");

  return false;
 }

 public boolean updateCurrencyWiseBankInfo(BankProfileVO bankProfileVO) throws SystemError, Exception
 {
  logger.debug("inside updateCurrencyWiseBankInfo--");
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();

   String updatebankprofile="update applicationmanager_currencywisebankinfo set bankinfo_bic=?,bankinfo_bank_name=?,bankinfo_bankaddress=?,bankinfo_bankphonenumber=?,bankinfo_accountnumber=?,bankinfo_aba_routingcode=?,bankinfo_accountholder=?,bankinfo_IBAN=?,bankinfo_contactperson=? where bankinfo_currency=? and application_id=? ";
   pstmt= conn.prepareStatement(updatebankprofile);

   if (functions.isValueNull(bankProfileVO.getBankinfo_bic()))
   {
    pstmt.setString(1, bankProfileVO.getBankinfo_bic());
   }
   else
   {
    pstmt.setNull(1, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bank_name()))
   {
    pstmt.setString(2, bankProfileVO.getBankinfo_bank_name());
   }
   else
   {
    pstmt.setNull(2, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bankaddress()))
   {
    pstmt.setString(3, bankProfileVO.getBankinfo_bankaddress());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bankphonenumber()))
   {
    pstmt.setString(4, bankProfileVO.getBankinfo_bankphonenumber());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_accountnumber()))
   {
    pstmt.setString(5,bankProfileVO.getBankinfo_accountnumber());
   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_aba_routingcode()))
   {
    pstmt.setString(6, bankProfileVO.getBankinfo_aba_routingcode());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_accountholder()))
   {
    pstmt.setString(7, bankProfileVO.getBankinfo_accountholder());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_IBAN()))
   {
    pstmt.setString(8,bankProfileVO.getBankinfo_IBAN());

   }
   else
   {
    pstmt.setNull(8, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_contactperson()))
   {
    pstmt.setString(9,bankProfileVO.getBankinfo_contactperson());
   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }

   pstmt.setString(10,bankProfileVO.getBankinfo_currency());

   pstmt.setString(11, bankProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   logger.debug("pstmt UPDATE currencywisebankprofile-->"+pstmt);
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateBankProfile");

  return false;
 }

 public boolean updateCurrencyWiseBankInfoByID(BankProfileVO bankProfileVO) throws SystemError
 {
  logger.debug("inside updateCurrencyWiseBankInfoByID--");
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();

   String updatebankprofile="update applicationmanager_currencywisebankinfo set bankinfo_bic=?,bankinfo_bank_name=?,bankinfo_bankaddress=?,bankinfo_bankphonenumber=?,bankinfo_accountnumber=?,bankinfo_aba_routingcode=?,bankinfo_accountholder=?,bankinfo_IBAN=?,bankinfo_contactperson=?,bankinfo_currency=? where currencywisebankinfo_id=? and application_id=? ";
   pstmt= conn.prepareStatement(updatebankprofile);

   if (functions.isValueNull(bankProfileVO.getBankinfo_bic()))
   {
    pstmt.setString(1, bankProfileVO.getBankinfo_bic());
   }
   else
   {
    pstmt.setNull(1, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bank_name()))
   {
    pstmt.setString(2, bankProfileVO.getBankinfo_bank_name());
   }
   else
   {
    pstmt.setNull(2, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bankaddress()))
   {
    pstmt.setString(3, bankProfileVO.getBankinfo_bankaddress());
   }
   else
   {
    pstmt.setNull(3, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_bankphonenumber()))
   {
    pstmt.setString(4, bankProfileVO.getBankinfo_bankphonenumber());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_accountnumber()))
   {
    pstmt.setString(5,bankProfileVO.getBankinfo_accountnumber());

   }
   else
   {
    pstmt.setNull(5, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_aba_routingcode()))
   {
    pstmt.setString(6, bankProfileVO.getBankinfo_aba_routingcode());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if (functions.isValueNull(bankProfileVO.getBankinfo_accountholder()))
   {
    pstmt.setString(7, bankProfileVO.getBankinfo_accountholder());
   }
   else
   {
    pstmt.setNull(7, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_IBAN()))
   {
    pstmt.setString(8,bankProfileVO.getBankinfo_IBAN());

   }
   else
   {
    pstmt.setNull(8, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_contactperson()))
   {
    pstmt.setString(9,bankProfileVO.getBankinfo_contactperson());
   }
   else
   {
    pstmt.setNull(9, Types.VARCHAR);
   }
   if(functions.isValueNull(bankProfileVO.getBankinfo_currency()))
   {
    pstmt.setString(10,bankProfileVO.getBankinfo_currency());
   }
   else
   {
    pstmt.setNull(10, Types.VARCHAR);
   }

   pstmt.setString(11,bankProfileVO.getCurrencywisebankinfo_id());

   pstmt.setString(12, bankProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   logger.debug("pstmt UPDATE currencywisebankprofile-->"+pstmt);
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateBankProfile");

  return false;
 }

 public boolean updateProcessingHistory(BankProfileVO bankProfileVO) throws SystemError, Exception
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();

   String updatebankprofile="update applicationmanager_processinghistory set salesvolume_lastmonth=?,salesvolume_2monthsago=?,salesvolume_3monthsago=?,salesvolume_4monthsago=?,salesvolume_5monthsago=?,salesvolume_6monthsago=?,salesvolume_12monthsago=?,salesvolume_year2=?,salesvolume_year3=?,numberoftransactions_lastmonth=?,numberoftransactions_2monthsago=?,numberoftransactions_3monthsago=?,numberoftransactions_4monthsago=?,numberoftransactions_5monthsago=?,numberoftransactions_6monthsago=?,numberoftransactions_12monthsago=?,numberoftransactions_year2=?,numberoftransactions_year3=?,chargebackvolume_lastmonth=?,chargebackvolume_2monthsago=?,chargebackvolume_3monthsago=?,chargebackvolume_4monthsago=?,chargebackvolume_5monthsago=?,chargebackvolume_6monthsago=?,chargebackvolume_12monthsago=?,chargebackvolume_year2=?,chargebackvolume_year3=?,numberofchargebacks_lastmonth=?,numberofchargebacks_2monthsago=?,numberofchargebacks_3monthsago=?,numberofchargebacks_4monthsago=?,numberofchargebacks_5monthsago=?,numberofchargebacks_6monthsago=?,numberofchargebacks_12monthsago=?,numberofchargebacks_year2=?,numberofchargebacks_year3=?,numberofrefunds_lastmonth=?,numberofrefunds_2monthsago=?,numberofrefunds_3monthsago=?,numberofrefunds_4monthsago=?,numberofrefunds_5monthsago=?,numberofrefunds_6monthsago=?,numberofrefunds_12monthsago=?,numberofrefunds_year2=?,numberofrefunds_year3=?,refundratio_lastmonth=?,refundratio_2monthsago=?,refundratio_3monthsago=?,refundratio_4monthsago=?,refundratio_5monthsago=?,refundratio_6monthsago=?,refundratio_12monthsago=?,refundratio_year2=?,refundratio_year3=?,chargebackratio_lastmonth=?,chargebackratio_2monthsago=?,chargebackratio_3monthsago=?,chargebackratio_4monthsago=?,chargebackratio_5monthsago=?,chargebackratio_6monthsago=?,chargebackratio_12monthsago=?,chargebackratio_year2=?,chargebackratio_year3=?,refundsvolume_lastmonth=?,refundsvolume_2monthsago=?,refundsvolume_3monthsago=?,refundsvolume_4monthsago=?,refundsvolume_5monthsago=?,refundsvolume_6monthsago=?,refundsvolume_12monthsago=?,refundsvolume_year2=?,refundsvolume_year3=? where currency=? and application_id=? ";

   pstmt= conn.prepareStatement(updatebankprofile);

   if (functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
   {
    pstmt.setString(1, bankProfileVO.getSalesvolume_lastmonth());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
   {
    pstmt.setString(2, bankProfileVO.getSalesvolume_2monthsago());
   }
   else
   {
    pstmt.setNull(2, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
   {
    pstmt.setString(3, bankProfileVO.getSalesvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(3, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
   {
    pstmt.setString(4, bankProfileVO.getSalesvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(4, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
   {
    pstmt.setString(5, bankProfileVO.getSalesvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(5, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
   {
    pstmt.setString(6, bankProfileVO.getSalesvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(6, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
   {
    pstmt.setString(7, bankProfileVO.getSalesvolume_12monthsago());
    //logger.debug("ownersince" + bankProfileVO.getSalesvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(7, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
   {
    pstmt.setString(8, bankProfileVO.getSalesvolume_year2());
    //logger.debug("socialsecurity" + bankProfileVO.getSalesvolume_year2());
   }
   else
   {
    pstmt.setNull(8, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
   {
    pstmt.setString(9, bankProfileVO.getSalesvolume_year3());
    //logger.debug("company_formparticipation" + extraDetailsProfileVO.getCompany_formParticipation());
   }
   else
   {
    pstmt.setNull(9, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth()))
   {
    pstmt.setString(10, bankProfileVO.getNumberoftransactions_lastmonth());
    //logger.debug("financialobligation" + extraDetailsProfileVO.getFinancialObligation());
   }
   else
   {
    pstmt.setNull(10, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago()))
   {
    pstmt.setString(11, bankProfileVO.getNumberoftransactions_2monthsago());
   }
   else
   {
    pstmt.setNull(11, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago()))
   {
    pstmt.setString(12, bankProfileVO.getNumberoftransactions_3monthsago());
   }
   else
   {
    pstmt.setNull(12, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago()))
   {
    pstmt.setString(13, bankProfileVO.getNumberoftransactions_4monthsago());
    //logger.debug("workingexperience" + extraDetailsProfileVO.getWorkingExperience());
   }
   else
   {
    pstmt.setNull(13, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago()))
   {
    pstmt.setString(14, bankProfileVO.getNumberoftransactions_5monthsago());
   }
   else
   {
    pstmt.setNull(14, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago()))
   {
    pstmt.setString(15, bankProfileVO.getNumberoftransactions_6monthsago());
   }
   else
   {
    pstmt.setNull(15, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago()))
   {
    pstmt.setString(16, bankProfileVO.getNumberoftransactions_12monthsago());
   }
   else
   {
    pstmt.setNull(16, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_year2()))
   {
    pstmt.setString(17, bankProfileVO.getNumberoftransactions_year2());
   }
   else
   {
    pstmt.setNull(17, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_year3()))
   {
    pstmt.setString(18, bankProfileVO.getNumberoftransactions_year3());
   }
   else
   {
    pstmt.setNull(18, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth()))
   {
    pstmt.setString(19, bankProfileVO.getChargebackvolume_lastmonth());
    //logger.debug("shiping_deliverymethod" + extraDetailsProfileVO.getShiping_deliveryMethod());
   }
   else
   {
    pstmt.setNull(19, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago()))
   {
    pstmt.setString(20, bankProfileVO.getChargebackvolume_2monthsago());
    //logger.debug("transactionmonitoringprocess" + extraDetailsProfileVO.getTransactionMonitoringProcess());
   }
   else
   {
    pstmt.setNull(20, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago()))
   {
    pstmt.setString(21, bankProfileVO.getChargebackvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(21, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago()))
   {
    pstmt.setString(22, bankProfileVO.getChargebackvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(22, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago()))
   {
    pstmt.setString(23, bankProfileVO.getChargebackvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(23, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago()))
   {
    pstmt.setString(24, bankProfileVO.getChargebackvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(24, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago()))
   {
    pstmt.setString(25, bankProfileVO.getChargebackvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(25, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_year2()))
   {
    pstmt.setString(26, bankProfileVO.getChargebackvolume_year2());
   }
   else
   {
    pstmt.setNull(26, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_year3()))
   {
    pstmt.setString(27, bankProfileVO.getChargebackvolume_year3());
   }
   else
   {
    pstmt.setNull(27, Types.INTEGER);
   }

   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth()))
   {
    pstmt.setString(28, bankProfileVO.getNumberofchargebacks_lastmonth());
   }
   else
   {
    pstmt.setNull(28, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago()))
   {
    pstmt.setString(29, bankProfileVO.getNumberofchargebacks_2monthsago());
   }
   else
   {
    pstmt.setNull(29, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago()))
   {
    pstmt.setString(30, bankProfileVO.getNumberofchargebacks_3monthsago());
   }
   else
   {
    pstmt.setNull(30, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago()))
   {
    pstmt.setString(31, bankProfileVO.getNumberofchargebacks_4monthsago());
   }
   else
   {
    pstmt.setNull(31, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago()))
   {
    pstmt.setString(32, bankProfileVO.getNumberofchargebacks_5monthsago());
   }
   else
   {
    pstmt.setNull(32, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago()))
   {
    pstmt.setString(33, bankProfileVO.getNumberofchargebacks_6monthsago());
   }
   else
   {
    pstmt.setNull(33, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago()))
   {
    pstmt.setString(34, bankProfileVO.getNumberofchargebacks_12monthsago());
   }
   else
   {
    pstmt.setNull(34, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2()))
   {
    pstmt.setString(35, bankProfileVO.getNumberofchargebacks_year2());
   }
   else
   {
    pstmt.setNull(35, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3()))
   {
    pstmt.setString(36, bankProfileVO.getNumberofchargebacks_year3());
   }
   else
   {
    pstmt.setNull(36, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth()))
   {
    pstmt.setString(37, bankProfileVO.getNumberofrefunds_lastmonth());
   }
   else
   {
    pstmt.setNull(37, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago()))
   {
    pstmt.setString(38, bankProfileVO.getNumberofrefunds_2monthsago());
   }
   else
   {
    pstmt.setNull(38, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago()))
   {
    pstmt.setString(39, bankProfileVO.getNumberofrefunds_3monthsago());
   }
   else
   {
    pstmt.setNull(39, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_4monthsago()))
   {
    pstmt.setString(40, bankProfileVO.getNumberofrefunds_4monthsago());
   }
   else
   {
    pstmt.setNull(40, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_5monthsago()))
   {
    pstmt.setString(41, bankProfileVO.getNumberofrefunds_5monthsago());
   }
   else
   {
    pstmt.setNull(41, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_6monthsago()))
   {
    pstmt.setString(42, bankProfileVO.getNumberofrefunds_6monthsago());
   }
   else
   {
    pstmt.setNull(42, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_12monthsago()))
   {
    pstmt.setString(43, bankProfileVO.getNumberofrefunds_12monthsago());
   }
   else
   {
    pstmt.setNull(43, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_year2()))
   {
    pstmt.setString(44, bankProfileVO.getNumberofrefunds_year2());
   }
   else
   {
    pstmt.setNull(44, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_year3()))
   {
    pstmt.setString(45, bankProfileVO.getNumberofrefunds_year3());
   }
   else
   {
    pstmt.setNull(45, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_lastmonth()))
   {
    pstmt.setString(46, bankProfileVO.getRefundratio_lastmonth());
   }
   else
   {
    pstmt.setNull(46, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_2monthsago()))
   {
    pstmt.setString(47, bankProfileVO.getRefundratio_2monthsago());
   }
   else
   {
    pstmt.setNull(47, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_3monthsago()))
   {
    pstmt.setString(48, bankProfileVO.getRefundratio_3monthsago());
   }
   else
   {
    pstmt.setNull(48, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_4monthsago()))
   {
    pstmt.setString(49, bankProfileVO.getRefundratio_4monthsago());
   }
   else
   {
    pstmt.setNull(49, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_5monthsago()))
   {
    pstmt.setString(50, bankProfileVO.getRefundratio_5monthsago());
   }
   else
   {
    pstmt.setNull(50, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_6monthsago()))
   {
    pstmt.setString(51, bankProfileVO.getRefundratio_6monthsago());
   }
   else
   {
    pstmt.setNull(51, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_12monthsago()))
   {
    pstmt.setString(52, bankProfileVO.getRefundratio_12monthsago());
   }
   else
   {
    pstmt.setNull(52, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_year2()))
   {
    pstmt.setString(53, bankProfileVO.getRefundratio_year2());
   }
   else
   {
    pstmt.setNull(53, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_year3()))
   {
    pstmt.setString(54, bankProfileVO.getRefundratio_year3());
   }
   else
   {
    pstmt.setNull(54, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth()))
   {
    pstmt.setString(55, bankProfileVO.getChargebackratio_lastmonth());
   }
   else
   {
    pstmt.setNull(55, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago()))
   {
    pstmt.setString(56, bankProfileVO.getChargebackratio_2monthsago());
   }
   else
   {
    pstmt.setNull(56, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago()))
   {
    pstmt.setString(57, bankProfileVO.getChargebackratio_3monthsago());
   }
   else
   {
    pstmt.setNull(57, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago()))
   {
    pstmt.setString(58, bankProfileVO.getChargebackratio_4monthsago());
   }
   else
   {
    pstmt.setNull(58, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago()))
   {
    pstmt.setString(59, bankProfileVO.getChargebackratio_5monthsago());
   }
   else
   {
    pstmt.setNull(59, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago()))
   {
    pstmt.setString(60, bankProfileVO.getChargebackratio_6monthsago());
   }
   else
   {
    pstmt.setNull(60, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago()))
   {
    pstmt.setString(61, bankProfileVO.getChargebackratio_12monthsago());
   }
   else
   {
    pstmt.setNull(61, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_year2()))
   {
    pstmt.setString(62, bankProfileVO.getChargebackratio_year2());
   }
   else
   {
    pstmt.setNull(62, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_year3()))
   {
    pstmt.setString(63, bankProfileVO.getChargebackratio_year3());
   }
   else
   {
    pstmt.setNull(63, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth()))
   {
    pstmt.setString(64, bankProfileVO.getRefundsvolume_lastmonth());
   }
   else
   {
    pstmt.setNull(64, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago()))
   {
    pstmt.setString(65, bankProfileVO.getRefundsvolume_2monthsago());
   }
   else
   {
    pstmt.setNull(65, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago()))
   {
    pstmt.setString(66, bankProfileVO.getRefundsvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(66, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago()))
   {
    pstmt.setString(67, bankProfileVO.getRefundsvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(67, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago()))
   {
    pstmt.setString(68, bankProfileVO.getRefundsvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(68, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago()))
   {
    pstmt.setString(69, bankProfileVO.getRefundsvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(69, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago()))
   {
    pstmt.setString(70, bankProfileVO.getRefundsvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(70, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_year2()))
   {
    pstmt.setString(71, bankProfileVO.getRefundsvolume_year2());
   }
   else
   {
    pstmt.setNull(71, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_year3()))
   {
    pstmt.setString(72, bankProfileVO.getRefundsvolume_year3());
   }
   else
   {
    pstmt.setNull(72, Types.INTEGER);
   }


   pstmt.setString(73, bankProfileVO.getCurrency());

   pstmt.setString(74, bankProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateProcessingHistory");

  return false;
 }

 public boolean updateProcessingHistoryByID(BankProfileVO bankProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();

   String updatebankprofile="update applicationmanager_processinghistory set salesvolume_lastmonth=?,salesvolume_2monthsago=?,salesvolume_3monthsago=?,salesvolume_4monthsago=?,salesvolume_5monthsago=?,salesvolume_6monthsago=?,salesvolume_12monthsago=?,salesvolume_year2=?,salesvolume_year3=?,numberoftransactions_lastmonth=?,numberoftransactions_2monthsago=?,numberoftransactions_3monthsago=?,numberoftransactions_4monthsago=?,numberoftransactions_5monthsago=?,numberoftransactions_6monthsago=?,numberoftransactions_12monthsago=?,numberoftransactions_year2=?,numberoftransactions_year3=?,chargebackvolume_lastmonth=?,chargebackvolume_2monthsago=?,chargebackvolume_3monthsago=?,chargebackvolume_4monthsago=?,chargebackvolume_5monthsago=?,chargebackvolume_6monthsago=?,chargebackvolume_12monthsago=?,chargebackvolume_year2=?,chargebackvolume_year3=?,numberofchargebacks_lastmonth=?,numberofchargebacks_2monthsago=?,numberofchargebacks_3monthsago=?,numberofchargebacks_4monthsago=?,numberofchargebacks_5monthsago=?,numberofchargebacks_6monthsago=?,numberofchargebacks_12monthsago=?,numberofchargebacks_year2=?,numberofchargebacks_year3=?,numberofrefunds_lastmonth=?,numberofrefunds_2monthsago=?,numberofrefunds_3monthsago=?,numberofrefunds_4monthsago=?,numberofrefunds_5monthsago=?,numberofrefunds_6monthsago=?,numberofrefunds_12monthsago=?,numberofrefunds_year2=?,numberofrefunds_year3=?,refundratio_lastmonth=?,refundratio_2monthsago=?,refundratio_3monthsago=?,refundratio_4monthsago=?,refundratio_5monthsago=?,refundratio_6monthsago=?,refundratio_12monthsago=?,refundratio_year2=?,refundratio_year3=?,chargebackratio_lastmonth=?,chargebackratio_2monthsago=?,chargebackratio_3monthsago=?,chargebackratio_4monthsago=?,chargebackratio_5monthsago=?,chargebackratio_6monthsago=?,chargebackratio_12monthsago=?,chargebackratio_year2=?,chargebackratio_year3=?,refundsvolume_lastmonth=?,refundsvolume_2monthsago=?,refundsvolume_3monthsago=?,refundsvolume_4monthsago=?,refundsvolume_5monthsago=?,refundsvolume_6monthsago=?,refundsvolume_12monthsago=?,refundsvolume_year2=?,refundsvolume_year3=?,currency=? where processinghistory_id=? and application_id=? ";

   pstmt= conn.prepareStatement(updatebankprofile);

   if (functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth()))
   {
    pstmt.setString(1, bankProfileVO.getSalesvolume_lastmonth());
   }
   else
   {
    pstmt.setNull(1, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()))
   {
    pstmt.setString(2, bankProfileVO.getSalesvolume_2monthsago());
   }
   else
   {
    pstmt.setNull(2, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago()))
   {
    pstmt.setString(3, bankProfileVO.getSalesvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(3, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago()))
   {
    pstmt.setString(4, bankProfileVO.getSalesvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(4, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago()))
   {
    pstmt.setString(5, bankProfileVO.getSalesvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(5, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago()))
   {
    pstmt.setString(6, bankProfileVO.getSalesvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(6, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago()))
   {
    pstmt.setString(7, bankProfileVO.getSalesvolume_12monthsago());
    //logger.debug("ownersince" + bankProfileVO.getSalesvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(7, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_year2()))
   {
    pstmt.setString(8, bankProfileVO.getSalesvolume_year2());
    //logger.debug("socialsecurity" + bankProfileVO.getSalesvolume_year2());
   }
   else
   {
    pstmt.setNull(8, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getSalesvolume_year3()))
   {
    pstmt.setString(9, bankProfileVO.getSalesvolume_year3());
    //logger.debug("company_formparticipation" + extraDetailsProfileVO.getCompany_formParticipation());
   }
   else
   {
    pstmt.setNull(9, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth()))
   {
    pstmt.setString(10, bankProfileVO.getNumberoftransactions_lastmonth());
    //logger.debug("financialobligation" + extraDetailsProfileVO.getFinancialObligation());
   }
   else
   {
    pstmt.setNull(10, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago()))
   {
    pstmt.setString(11, bankProfileVO.getNumberoftransactions_2monthsago());
   }
   else
   {
    pstmt.setNull(11, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago()))
   {
    pstmt.setString(12, bankProfileVO.getNumberoftransactions_3monthsago());
   }
   else
   {
    pstmt.setNull(12, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago()))
   {
    pstmt.setString(13, bankProfileVO.getNumberoftransactions_4monthsago());
    //logger.debug("workingexperience" + extraDetailsProfileVO.getWorkingExperience());
   }
   else
   {
    pstmt.setNull(13, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago()))
   {
    pstmt.setString(14, bankProfileVO.getNumberoftransactions_5monthsago());
   }
   else
   {
    pstmt.setNull(14, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago()))
   {
    pstmt.setString(15, bankProfileVO.getNumberoftransactions_6monthsago());
   }
   else
   {
    pstmt.setNull(15, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago()))
   {
    pstmt.setString(16, bankProfileVO.getNumberoftransactions_12monthsago());
   }
   else
   {
    pstmt.setNull(16, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_year2()))
   {
    pstmt.setString(17, bankProfileVO.getNumberoftransactions_year2());
   }
   else
   {
    pstmt.setNull(17, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberoftransactions_year3()))
   {
    pstmt.setString(18, bankProfileVO.getNumberoftransactions_year3());
   }
   else
   {
    pstmt.setNull(18, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth()))
   {
    pstmt.setString(19, bankProfileVO.getChargebackvolume_lastmonth());
    //logger.debug("shiping_deliverymethod" + extraDetailsProfileVO.getShiping_deliveryMethod());
   }
   else
   {
    pstmt.setNull(19, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago()))
   {
    pstmt.setString(20, bankProfileVO.getChargebackvolume_2monthsago());
    //logger.debug("transactionmonitoringprocess" + extraDetailsProfileVO.getTransactionMonitoringProcess());
   }
   else
   {
    pstmt.setNull(20, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago()))
   {
    pstmt.setString(21, bankProfileVO.getChargebackvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(21, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago()))
   {
    pstmt.setString(22, bankProfileVO.getChargebackvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(22, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago()))
   {
    pstmt.setString(23, bankProfileVO.getChargebackvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(23, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago()))
   {
    pstmt.setString(24, bankProfileVO.getChargebackvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(24, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago()))
   {
    pstmt.setString(25, bankProfileVO.getChargebackvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(25, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_year2()))
   {
    pstmt.setString(26, bankProfileVO.getChargebackvolume_year2());
   }
   else
   {
    pstmt.setNull(26, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackvolume_year3()))
   {
    pstmt.setString(27, bankProfileVO.getChargebackvolume_year3());
   }
   else
   {
    pstmt.setNull(27, Types.INTEGER);
   }

   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth()))
   {
    pstmt.setString(28, bankProfileVO.getNumberofchargebacks_lastmonth());
   }
   else
   {
    pstmt.setNull(28, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago()))
   {
    pstmt.setString(29, bankProfileVO.getNumberofchargebacks_2monthsago());
   }
   else
   {
    pstmt.setNull(29, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago()))
   {
    pstmt.setString(30, bankProfileVO.getNumberofchargebacks_3monthsago());
   }
   else
   {
    pstmt.setNull(30, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago()))
   {
    pstmt.setString(31, bankProfileVO.getNumberofchargebacks_4monthsago());
   }
   else
   {
    pstmt.setNull(31, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago()))
   {
    pstmt.setString(32, bankProfileVO.getNumberofchargebacks_5monthsago());
   }
   else
   {
    pstmt.setNull(32, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago()))
   {
    pstmt.setString(33, bankProfileVO.getNumberofchargebacks_6monthsago());
   }
   else
   {
    pstmt.setNull(33, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago()))
   {
    pstmt.setString(34, bankProfileVO.getNumberofchargebacks_12monthsago());
   }
   else
   {
    pstmt.setNull(34, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2()))
   {
    pstmt.setString(35, bankProfileVO.getNumberofchargebacks_year2());
   }
   else
   {
    pstmt.setNull(35, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3()))
   {
    pstmt.setString(36, bankProfileVO.getNumberofchargebacks_year3());
   }
   else
   {
    pstmt.setNull(36, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth()))
   {
    pstmt.setString(37, bankProfileVO.getNumberofrefunds_lastmonth());
   }
   else
   {
    pstmt.setNull(37, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago()))
   {
    pstmt.setString(38, bankProfileVO.getNumberofrefunds_2monthsago());
   }
   else
   {
    pstmt.setNull(38, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago()))
   {
    pstmt.setString(39, bankProfileVO.getNumberofrefunds_3monthsago());
   }
   else
   {
    pstmt.setNull(39, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_4monthsago()))
   {
    pstmt.setString(40, bankProfileVO.getNumberofrefunds_4monthsago());
   }
   else
   {
    pstmt.setNull(40, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_5monthsago()))
   {
    pstmt.setString(41, bankProfileVO.getNumberofrefunds_5monthsago());
   }
   else
   {
    pstmt.setNull(41, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_6monthsago()))
   {
    pstmt.setString(42, bankProfileVO.getNumberofrefunds_6monthsago());
   }
   else
   {
    pstmt.setNull(42, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_12monthsago()))
   {
    pstmt.setString(43, bankProfileVO.getNumberofrefunds_12monthsago());
   }
   else
   {
    pstmt.setNull(43, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_year2()))
   {
    pstmt.setString(44, bankProfileVO.getNumberofrefunds_year2());
   }
   else
   {
    pstmt.setNull(44, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getNumberofrefunds_year3()))
   {
    pstmt.setString(45, bankProfileVO.getNumberofrefunds_year3());
   }
   else
   {
    pstmt.setNull(45, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_lastmonth()))
   {
    pstmt.setString(46, bankProfileVO.getRefundratio_lastmonth());
   }
   else
   {
    pstmt.setNull(46, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_2monthsago()))
   {
    pstmt.setString(47, bankProfileVO.getRefundratio_2monthsago());
   }
   else
   {
    pstmt.setNull(47, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_3monthsago()))
   {
    pstmt.setString(48, bankProfileVO.getRefundratio_3monthsago());
   }
   else
   {
    pstmt.setNull(48, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_4monthsago()))
   {
    pstmt.setString(49, bankProfileVO.getRefundratio_4monthsago());
   }
   else
   {
    pstmt.setNull(49, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_5monthsago()))
   {
    pstmt.setString(50, bankProfileVO.getRefundratio_5monthsago());
   }
   else
   {
    pstmt.setNull(50, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_6monthsago()))
   {
    pstmt.setString(51, bankProfileVO.getRefundratio_6monthsago());
   }
   else
   {
    pstmt.setNull(51, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_12monthsago()))
   {
    pstmt.setString(52, bankProfileVO.getRefundratio_12monthsago());
   }
   else
   {
    pstmt.setNull(52, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_year2()))
   {
    pstmt.setString(53, bankProfileVO.getRefundratio_year2());
   }
   else
   {
    pstmt.setNull(53, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundratio_year3()))
   {
    pstmt.setString(54, bankProfileVO.getRefundratio_year3());
   }
   else
   {
    pstmt.setNull(54, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth()))
   {
    pstmt.setString(55, bankProfileVO.getChargebackratio_lastmonth());
   }
   else
   {
    pstmt.setNull(55, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago()))
   {
    pstmt.setString(56, bankProfileVO.getChargebackratio_2monthsago());
   }
   else
   {
    pstmt.setNull(56, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago()))
   {
    pstmt.setString(57, bankProfileVO.getChargebackratio_3monthsago());
   }
   else
   {
    pstmt.setNull(57, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago()))
   {
    pstmt.setString(58, bankProfileVO.getChargebackratio_4monthsago());
   }
   else
   {
    pstmt.setNull(58, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago()))
   {
    pstmt.setString(59, bankProfileVO.getChargebackratio_5monthsago());
   }
   else
   {
    pstmt.setNull(59, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago()))
   {
    pstmt.setString(60, bankProfileVO.getChargebackratio_6monthsago());
   }
   else
   {
    pstmt.setNull(60, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago()))
   {
    pstmt.setString(61, bankProfileVO.getChargebackratio_12monthsago());
   }
   else
   {
    pstmt.setNull(61, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_year2()))
   {
    pstmt.setString(62, bankProfileVO.getChargebackratio_year2());
   }
   else
   {
    pstmt.setNull(62, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getChargebackratio_year3()))
   {
    pstmt.setString(63, bankProfileVO.getChargebackratio_year3());
   }
   else
   {
    pstmt.setNull(63, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth()))
   {
    pstmt.setString(64, bankProfileVO.getRefundsvolume_lastmonth());
   }
   else
   {
    pstmt.setNull(64, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago()))
   {
    pstmt.setString(65, bankProfileVO.getRefundsvolume_2monthsago());
   }
   else
   {
    pstmt.setNull(65, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago()))
   {
    pstmt.setString(66, bankProfileVO.getRefundsvolume_3monthsago());
   }
   else
   {
    pstmt.setNull(66, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago()))
   {
    pstmt.setString(67, bankProfileVO.getRefundsvolume_4monthsago());
   }
   else
   {
    pstmt.setNull(67, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago()))
   {
    pstmt.setString(68, bankProfileVO.getRefundsvolume_5monthsago());
   }
   else
   {
    pstmt.setNull(68, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago()))
   {
    pstmt.setString(69, bankProfileVO.getRefundsvolume_6monthsago());
   }
   else
   {
    pstmt.setNull(69, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago()))
   {
    pstmt.setString(70, bankProfileVO.getRefundsvolume_12monthsago());
   }
   else
   {
    pstmt.setNull(70, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_year2()))
   {
    pstmt.setString(71, bankProfileVO.getRefundsvolume_year2());
   }
   else
   {
    pstmt.setNull(71, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getRefundsvolume_year3()))
   {
    pstmt.setString(72, bankProfileVO.getRefundsvolume_year3());
   }
   else
   {
    pstmt.setNull(72, Types.INTEGER);
   }
   if (functions.isValueNull(bankProfileVO.getCurrency()))
   {
    pstmt.setString(73, bankProfileVO.getCurrency());
   }
   else
   {
    pstmt.setNull(73, Types.INTEGER);
   }

   pstmt.setString(74, bankProfileVO.getProcessinghistory_id());
   pstmt.setString(75, bankProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateProcessingHistory");

  return false;
 }

 //Update CardHolderProfile
 public boolean updateCardholderProfile(CardholderProfileVO cardholderProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();
   String updatecardholderprofile="update applicationmanager_cardholderprofile set compliance_swapp=?,compliance_thirdpartyappform=?,compliance_thirdpartysoft=?,compliance_version=?,compliance_companiesorgateways=?,compliance_companiesorgateways_yes=?,compliance_electronically=?,compliance_carddatastored=?,compliance_pcidsscompliant=?,compliance_qualifiedsecurityassessor=?,compliance_dateofcompliance=?,compliance_dateoflastscan=?,compliance_datacompromise=?,compliance_datacompromise_yes=?,siteinspection_merchant=?,siteinspection_landlord=?,siteinspection_buildingtype=?,siteinspection_areazoned=?,siteinspection_squarefootage=?,siteinspection_operatebusiness=?,siteinspection_principal1=?,siteinspection_principal1_date=?,siteinspection_principal2=?,siteinspection_principal2_date=?,compliance_pcidsscompliant_yes=?,compliance_cispcompliant=?,compliance_cispcompliant_yes=? where application_id=? ";
   pstmt= conn.prepareStatement(updatecardholderprofile);


   if(functions.isValueNull(cardholderProfileVO.getCompliance_swapp()))
   {
    pstmt.setString(1,cardholderProfileVO.getCompliance_swapp());
   }
   else
   {
    pstmt.setString(1,"N");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartyappform()))
   {
    pstmt.setString(2,cardholderProfileVO.getCompliance_thirdpartyappform());
   }
   else
   {
    pstmt.setNull(2,Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_thirdpartysoft()))
   {
    pstmt.setString(3,cardholderProfileVO.getCompliance_thirdpartysoft());
   }
   else
   {
    pstmt.setNull(3,Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_version()))
   {
    pstmt.setString(4,cardholderProfileVO.getCompliance_version());
   }
   else
   {
    pstmt.setNull(4, Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways()))
   {
    pstmt.setString(5,cardholderProfileVO.getCompliance_companiesorgateways());
   }
   else
   {
    pstmt.setString(5, "N");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_companiesorgateways_yes()))
   {
    pstmt.setString(6,cardholderProfileVO.getCompliance_companiesorgateways_yes());
   }
   else
   {
    pstmt.setNull(6,Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_electronically()))
   {
    pstmt.setString(7,cardholderProfileVO.getCompliance_electronically());
   }
   else
   {
    pstmt.setString(7, "N");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_carddatastored()))
   {
    pstmt.setString(8,cardholderProfileVO.getCompliance_carddatastored());
   }
   else
   {
    pstmt.setString(8, "Both");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant()))
   {
    pstmt.setString(9,cardholderProfileVO.getCompliance_pcidsscompliant());
   }
   else
   {
    pstmt.setString(9, "N");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_qualifiedsecurityassessor()))
   {
    pstmt.setString(10,cardholderProfileVO.getCompliance_qualifiedsecurityassessor());
   }
   else
   {
    pstmt.setNull(10, Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_dateofcompliance()))
   {
    pstmt.setString(11,cardholderProfileVO.getCompliance_dateofcompliance());
   }
   else
   {
    pstmt.setNull(11,Types.TIMESTAMP);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_dateoflastscan()))
   {
    pstmt.setString(12,cardholderProfileVO.getCompliance_dateoflastscan());
   }
   else
   {
    pstmt.setNull(12,Types.TIMESTAMP) ;
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise()))
   {
    pstmt.setString(13,cardholderProfileVO.getCompliance_datacompromise());
   }
   else
   {
    pstmt.setString(13,"N");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_datacompromise_yes()))
   {
    pstmt.setString(14,cardholderProfileVO.getCompliance_datacompromise_yes());
   }
   else
   {
    pstmt.setNull(14, Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_merchant()))
   {
    pstmt.setString(15,cardholderProfileVO.getSiteinspection_merchant());
   }
   else
   {
    pstmt.setString(15, "N");
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_landlord()))
   {
    pstmt.setString(16,cardholderProfileVO.getSiteinspection_landlord());
   }
   else
   {
    pstmt.setNull(16, Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_buildingtype()))
   {
    pstmt.setString(17,cardholderProfileVO.getSiteinspection_buildingtype());
   }
   else
   {
    pstmt.setString(17, "OfficeBldg");
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_areazoned()))
   {
    pstmt.setString(18,cardholderProfileVO.getSiteinspection_areazoned());
   }
   else
   {
    pstmt.setString(18, "Commercial");
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_squarefootage()))
   {
    pstmt.setString(19,cardholderProfileVO.getSiteinspection_squarefootage());
   }
   else
   {
    pstmt.setString(19, "0-500");
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_operatebusiness()))
   {
    pstmt.setString(20,cardholderProfileVO.getSiteinspection_operatebusiness());
   }
   else
   {
    pstmt.setString(20,"N");
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1()))
   {
    pstmt.setString(21,cardholderProfileVO.getSiteinspection_principal1());
   }
   else
   {
    pstmt.setNull(21,Types.VARCHAR) ;
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_principal1_date()))
   {
    pstmt.setString(22,cardholderProfileVO.getSiteinspection_principal1_date());
   }
   else
   {
    pstmt.setNull(22,Types.TIMESTAMP);
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2()))
   {
    pstmt.setString(23,cardholderProfileVO.getSiteinspection_principal2());
   }
   else
   {
    pstmt.setNull(23,Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getSiteinspection_principal2_date()))
   {
    pstmt.setString(24,cardholderProfileVO.getSiteinspection_principal2_date());
   }
   else
   {
    pstmt.setNull(24, Types.TIMESTAMP);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_pcidsscompliant_yes()))
   {
    pstmt.setString(25,cardholderProfileVO.getCompliance_pcidsscompliant_yes());
   }
   else
   {
    pstmt.setNull(25, Types.VARCHAR);
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant()))
   {
    pstmt.setString(26,cardholderProfileVO.getCompliance_cispcompliant());
   }
   else
   {
    pstmt.setString(26,"N");
   }
   if(functions.isValueNull(cardholderProfileVO.getCompliance_cispcompliant_yes()))
   {
    pstmt.setString(27,cardholderProfileVO.getCompliance_cispcompliant_yes());
   }
   else
   {
    pstmt.setNull(27,Types.VARCHAR);
   }
   pstmt.setString(28,cardholderProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateCardholderProfile");

  return false;
 }

 // update extra details profile
 public boolean updateExtraDetailsProfile(ExtraDetailsProfileVO extraDetailsProfileVO) throws SystemError
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();
   String updateextradetailsprofile="update applicationmanager_extradetailsprofile set company_financialreport=?,company_financialreportyes=?,financialreport_institution=?,financialreport_available=?,financialReport_availableyes=?,ownersince=?,socialsecurity=?,company_formparticipation=?,financialobligation=?,compliance_punitivesanction=?,compliance_punitivesanctionyes=?,workingexperience=?,goodsinsuranceoffered=?,fulfillment_productemail=?,fulfillment_productemailyes=?,blacklistedaccountclosed=?,blacklistedaccountclosedyes=?,shiping_deliverymethod=?,transactionmonitoringprocess=?,operationallicense=?,supervisorregularcontrole=?,deedofagreement=?,deedofagreementyes=? where application_id=? ";
   pstmt= conn.prepareStatement(updateextradetailsprofile);

   if(functions.isValueNull(extraDetailsProfileVO.getCompany_financialReport()))
   {
    pstmt.setString(1,extraDetailsProfileVO.getCompany_financialReport());
   }
   else
   {
    pstmt.setString(1, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getCompany_financialReportYes()))
   {
    pstmt.setString(2,extraDetailsProfileVO.getCompany_financialReportYes());
   }
   else
   {
    pstmt.setNull(2,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getFinancialReport_institution()))
   {
    pstmt.setString(3,extraDetailsProfileVO.getFinancialReport_institution());
   }
   else
   {
    pstmt.setNull(3,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getFinancialReport_available()))
   {
    pstmt.setString(4,extraDetailsProfileVO.getFinancialReport_available());
   }
   else
   {
    pstmt.setString(4, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getFinancialReport_availableYes()))
   {
    pstmt.setString(5,extraDetailsProfileVO.getFinancialReport_availableYes());
   }
   else
   {
    pstmt.setNull(5,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getOwnerSince()))
   {
    pstmt.setString(6,extraDetailsProfileVO.getOwnerSince());
   }
   else
   {
    pstmt.setNull(6,Types.TIMESTAMP);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getSocialSecurity()))
   {
    pstmt.setString(7,extraDetailsProfileVO.getSocialSecurity());
   }
   else
   {
    pstmt.setNull(7,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getCompany_formParticipation()))
   {
    pstmt.setString(8,extraDetailsProfileVO.getCompany_formParticipation());
   }
   else
   {
    pstmt.setNull(8,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getFinancialObligation()))
   {
    pstmt.setString(9,extraDetailsProfileVO.getFinancialObligation());
   }
   else
   {
    pstmt.setNull(9,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanction()))
   {
    pstmt.setString(10,extraDetailsProfileVO.getCompliance_punitiveSanction());
   }
   else
   {
    pstmt.setString(10, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getCompliance_punitiveSanctionYes()))
   {
    pstmt.setString(11,extraDetailsProfileVO.getCompliance_punitiveSanctionYes());
   }
   else
   {
    pstmt.setNull(11,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getWorkingExperience()))
   {
    pstmt.setString(12,extraDetailsProfileVO.getWorkingExperience());
   }
   else
   {
    pstmt.setNull(12,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getGoodsInsuranceOffered()))
   {
    pstmt.setString(13,extraDetailsProfileVO.getGoodsInsuranceOffered());
   }
   else
   {
    pstmt.setString(13, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmail()))
   {
    pstmt.setString(14,extraDetailsProfileVO.getFulfillment_productEmail());
   }
   else
   {
    pstmt.setString(14, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getFulfillment_productEmailYes()))
   {
    pstmt.setString(15,extraDetailsProfileVO.getFulfillment_productEmailYes());
   }
   else
   {
    pstmt.setNull(15,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosed()))
   {
    pstmt.setString(16,extraDetailsProfileVO.getBlacklistedAccountClosed());
   }
   else
   {
    pstmt.setString(16, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getBlacklistedAccountClosedYes()))
   {
    pstmt.setString(17,extraDetailsProfileVO.getBlacklistedAccountClosedYes());
   }
   else
   {
    pstmt.setNull(17,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getShiping_deliveryMethod()))
   {
    pstmt.setString(18,extraDetailsProfileVO.getShiping_deliveryMethod());
   }
   else
   {
    pstmt.setNull(18,Types.VARCHAR);
   }
   if(functions.isValueNull(extraDetailsProfileVO.getTransactionMonitoringProcess()))
   {
    pstmt.setString(19,extraDetailsProfileVO.getTransactionMonitoringProcess());
   }
   else
   {
    pstmt.setNull(19,Types.VARCHAR);
   }

   if(functions.isValueNull(extraDetailsProfileVO.getOperationalLicense()))
   {
    pstmt.setString(20,extraDetailsProfileVO.getOperationalLicense());
   }
   else
   {
    pstmt.setString(20, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getSupervisorregularcontrole()))
   {
    pstmt.setString(21,extraDetailsProfileVO.getSupervisorregularcontrole());
   }
   else
   {
    pstmt.setString(21, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreement()))
   {
    pstmt.setString(22,extraDetailsProfileVO.getDeedOfAgreement());
   }
   else
   {
    pstmt.setString(22, "N");
   }
   if(functions.isValueNull(extraDetailsProfileVO.getDeedOfAgreementYes()))
   {
    pstmt.setString(23,extraDetailsProfileVO.getDeedOfAgreementYes());
   }
   else
   {
    pstmt.setNull(23,Types.VARCHAR);
   }
   pstmt.setString(24,extraDetailsProfileVO.getApplication_id());

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateextradetailsprofile");

  return false;
 }

 //insert upload document
 public boolean insertUploadDocument(AppFileDetailsVO fileDetailsVO,ApplicationManagerVO applicationManagerVO)
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO member_document_mapping(member_id,document_name,document_type,replace_status,upload_status,alternate_name,label_id,mappingcreation_date,timestamp,application_id)VALUES(?,?,?,?,?,?,?,?,?,?)";
   pstmt= conn.prepareStatement(query);

   if(functions.isValueNull(applicationManagerVO.getMemberId()))
   {
    pstmt.setString(1,applicationManagerVO.getMemberId());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFilename()))
   {
    pstmt.setString(2,fileDetailsVO.getFilename());
   }
   else
   {
    pstmt.setNull(2,Types.VARCHAR);
   }
   if(functions.isValueNull(fileDetailsVO.getFileType()))
   {
    pstmt.setString(3,fileDetailsVO.getFileType());
   }
   else
   {
    pstmt.setNull(3,Types.VARCHAR);
   }

   if(AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(4,"Y");
   }
   else
   {

    pstmt.setString(4,"N");

   }
   if(AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(5,"Y");
   }
   else
   {

    pstmt.setString(5,"N");

   }
   if(functions.isValueNull(fileDetailsVO.getFieldName()))
   {
    pstmt.setString(6,fileDetailsVO.getFieldName());
   }
   else
   {
    pstmt.setNull(6,Types.VARCHAR);
   }
   if(functions.isValueNull(fileDetailsVO.getLabelId()))
   {
    pstmt.setString(7,fileDetailsVO.getLabelId());
   }
   else
   {
    pstmt.setNull(7,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getDtstamp()))
   {
    pstmt.setString(8,fileDetailsVO.getDtstamp());
   }
   else
   {
    pstmt.setNull(8,Types.TIMESTAMP);
   }
   if(functions.isValueNull(fileDetailsVO.getTimestamp()))
   {
    pstmt.setString(9,fileDetailsVO.getTimestamp());
   }
   else
   {
    pstmt.setNull(9,Types.TIMESTAMP);
   }
   if(fileDetailsVO.isSuccess())      //put the replace value
   {
    pstmt.setString(10,applicationManagerVO.getApplicationId());
   }
   else
   {
    pstmt.setNull(10,Types.INTEGER);
   }
   int k = pstmt.executeUpdate();
   if (k>0)
   {
    ResultSet resultSet= pstmt.getGeneratedKeys();
    resultSet.next();
    fileDetailsVO.setMappingId(resultSet.getString(1));
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",systemError);

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);

  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertUploadDocument");

  return false;
 }

 //update member Document Mapping
 public boolean updateMemberDocumentMapping(AppFileDetailsVO fileDetailsVO,ApplicationManagerVO applicationManagerVO)
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  String status="failed";
  try
  {
   conn = Database.getConnection();
   String updatememberdocumentmapping="update member_document_mapping set application_id=?,document_name=?,document_type=?,replace_status=?,upload_status=?,alternate_name=?,label_id=?,mappingcreation_date=?,timestamp=? where mapping_id=? ";
   pstmt= conn.prepareStatement(updatememberdocumentmapping);

   if(functions.isValueNull(applicationManagerVO.getApplicationId()))
   {
    pstmt.setString(1,applicationManagerVO.getApplicationId());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFilename()))
   {
    pstmt.setString(2,fileDetailsVO.getFilename());
   }
   else
   {
    pstmt.setNull(2,Types.VARCHAR);
   }
   if(functions.isValueNull(fileDetailsVO.getFileType()))
   {
    pstmt.setString(3,fileDetailsVO.getFileType());
   }
   else
   {
    pstmt.setNull(3,Types.VARCHAR);
   }

   if(AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(4,"Y");
   }
   else
   {

    pstmt.setString(4, "N");

   }
   if(AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(5,"Y");
   }
   else
   {

    pstmt.setString(5,"N");

   }
   if(functions.isValueNull(fileDetailsVO.getFieldName()))
   {
    pstmt.setString(6,fileDetailsVO.getFieldName());
   }
   else
   {
    pstmt.setNull(6, Types.VARCHAR);
   }
   if(functions.isValueNull(fileDetailsVO.getLabelId()))
   {
    pstmt.setString(7,fileDetailsVO.getLabelId());
   }
   else
   {
    pstmt.setNull(7, Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getDtstamp()))
   {
    pstmt.setString(8,fileDetailsVO.getDtstamp());
   }
   else
   {
    pstmt.setNull(8,Types.TIMESTAMP);
   }
   if(functions.isValueNull(fileDetailsVO.getTimestamp()))
   {
    pstmt.setString(9,fileDetailsVO.getTimestamp());
   }
   else
   {
    pstmt.setNull(9,Types.TIMESTAMP);
   }
   if(functions.isValueNull(fileDetailsVO.getMappingId()))
   {
    pstmt.setString(10,fileDetailsVO.getMappingId());
   }
   else
   {
    pstmt.setNull(10,Types.INTEGER);
   }


   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);

  }
  catch(SystemError e)
  {
   logger.error("Leaving ApplicationManagerDAO system error as System Error :::: ",e);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateAppManagerStatus");

  return false;
 }

 //insert member document History
 public boolean insertMemberDocumentHistory(AppFileDetailsVO fileDetailsVO,ApplicationManagerVO applicationManagerVO)
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO member_document_history(member_id,application_id,document_name,document_type,replace_status,upload_status,deleted,alternate_name,label_id,mappingcreation_date,timestamp,mapping_id,moved_document_name)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
   pstmt= conn.prepareStatement(query);

   if(functions.isValueNull(applicationManagerVO.getMemberId()))
   {
    pstmt.setString(1,applicationManagerVO.getMemberId());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }
   if(functions.isValueNull(applicationManagerVO.getApplicationId()))
   {
    pstmt.setString(2,applicationManagerVO.getApplicationId());
   }
   else
   {
    pstmt.setNull(2,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFilename()))
   {
    pstmt.setString(3,fileDetailsVO.getFilename());
   }
   else
   {
    pstmt.setNull(3,Types.VARCHAR);
   }
   if(functions.isValueNull(fileDetailsVO.getFileType()))
   {
    pstmt.setString(4,fileDetailsVO.getFileType());
   }
   else
   {
    pstmt.setNull(4,Types.VARCHAR);
   }

   if(AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(5,"Y");
   }
   else
   {

    pstmt.setString(5,"N");

   }
   if(AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(6,"Y");
   }
   else
   {

    pstmt.setString(6,"N");

   }
   if(AppFileActionType.DELETE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
   {
    pstmt.setString(7,"Y");
   }
   else
   {

    pstmt.setString(7,"N");

   }
   if(functions.isValueNull(fileDetailsVO.getFieldName()))
   {
    pstmt.setString(8,fileDetailsVO.getFieldName());
   }
   else
   {
    pstmt.setNull(8,Types.VARCHAR);
   }
   if(functions.isValueNull(fileDetailsVO.getLabelId()))
   {
    pstmt.setString(9,fileDetailsVO.getLabelId());
   }
   else
   {
    pstmt.setNull(9,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getDtstamp()))
   {
    pstmt.setString(10,fileDetailsVO.getDtstamp());
   }
   else
   {
    pstmt.setNull(10,Types.TIMESTAMP);
   }
   if(functions.isValueNull(fileDetailsVO.getTimestamp()))
   {
    pstmt.setString(11,fileDetailsVO.getTimestamp());
   }
   else
   {
    pstmt.setNull(11,Types.TIMESTAMP);
   }
   if(functions.isValueNull(fileDetailsVO.getMappingId()))
   {
    pstmt.setString(12,fileDetailsVO.getMappingId());
   }
   else
   {
    pstmt.setNull(12,Types.TIMESTAMP);
   }

   if(functions.isValueNull(fileDetailsVO.getMovedFileName()))
   {
    pstmt.setString(13,fileDetailsVO.getMovedFileName());
   }
   else
   {
    pstmt.setNull(13,Types.TIMESTAMP);
   }

   int k = pstmt.executeUpdate();
   if (k>0)
   {

    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",systemError);

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ",e);

  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertUploadDocument");

  return false;
 }



 //select for applicationID and MemberID from ApplicationManager

 //for list of partner's new signup specific  merchant applicationVO
 public List<ApplicationManagerVO> getPartnersNewSpecificMerchantApplicationManagerVO(String merchantId,String partnerId,PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  HashMap<String,ApplicationManagerVO> listOFMembers = new HashMap<String,ApplicationManagerVO>();
  try
  {
   StringBuffer query =new StringBuffer("SELECT memberid FROM members WHERE partnerId=? and memberid=? order by memberid desc");

   //add pagination
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-"+countQuery);
   query.append(" limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-"+query);
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(1, partnerId);
   pstmt.setString(2,merchantId);
   resultSet=pstmt.executeQuery();
   while (resultSet.next())
   {
    //String qry = "SELECT am.*,ss.* FROM application_manager AS am, speed_status AS ss WHERE  member_id=? AND am.application_id=ss.application_id";
    String qry = "SELECT am.* FROM application_manager AS am WHERE  member_id=?";
    pstmt=con.prepareStatement(qry.toString());
    pstmt.setString(1,resultSet.getString("memberid"));
    ResultSet rs = pstmt.executeQuery();
    if(rs.next())
    {
     if(!functions.isValueNull(rs.getString("MAF_status")) && !functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("MAF_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_MAF");
      applicationManagerVO.setSpeed_status(rs.getString("speed_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_SPEED");
      applicationManagerVO.setMaf_Status(rs.getString("MAF_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
    }
    else
    {
     applicationManagerVO=new ApplicationManagerVO();
     applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
     applicationManagerVOs.add(applicationManagerVO);
    }

    listOFMembers.put(resultSet.getString("memberid"),applicationManagerVO);

   }

   PreparedStatement preparedStatement = null;
   preparedStatement=con.prepareStatement(countQuery);
   if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, partnerId);
   }
   if(functions.isValueNull(merchantId))
   {
    preparedStatement.setString(2, merchantId);
   }


   resultSet=preparedStatement.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //select for applicationID and Partner's MemberID from ApplicationManager

 public  List<ApplicationManagerVO> getapplicationManagerVO(String memberid,String applicationId,PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null, psMerchantSettlementList = null;
  ResultSet resultSet=null;
  ApplicationManagerVO applicationManagerVO=null;
  List<ApplicationManagerVO> applicationManagerVOList=new ArrayList<ApplicationManagerVO>();
  int count = 1;
  try
  {
   //con=Database.getConnection();
   con=Database.getRDBConnection();
   //StringBuffer query = new StringBuffer("Select am.*,sp.speed_status,sp.speed_user from `application_manager` as am left join speed_status as sp on sp.application_id=am.application_id ");
   StringBuffer query = new StringBuffer("Select am.* from application_manager as am JOIN members as m ON am.member_id = m.memberid");
   if(functions.isValueNull(memberid) && functions.isValueNull(applicationId))
   {
    query.append(" where am.member_id = ? and am.application_id = ? and m.activation IN ('Y','T')");
   }
   else
   {
    if(functions.isValueNull(memberid))
    {
     query.append(" where am.member_id = ? and m.activation IN ('Y','T')");
    }
    if(functions.isValueNull(applicationId))
    {
     query.append(" where am.application_id = ? and m.activation IN ('Y','T')");
    }

   }
   query.append("order by application_id desc ");
   //add pagination
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-"+countQuery);
   query.append("limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-=="+query);

   psMerchantSettlementList = con.prepareStatement(query.toString());
   if(functions.isValueNull(memberid))
   {
    psMerchantSettlementList.setString(count, memberid);
    count++;
   }
   if(functions.isValueNull(applicationId))
   {
    psMerchantSettlementList.setString(count, applicationId);

   }
   resultSet=psMerchantSettlementList.executeQuery();
   logger.debug("querryyyy"+psMerchantSettlementList.toString());
   while (resultSet.next())
   {
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVO.setSpeed_status(resultSet.getString("speed_status"));
    applicationManagerVO.setSpeed_user(resultSet.getString("speed_user"));
    applicationManagerVOList.add(applicationManagerVO);
   }
   count=1;
   pstmt=con.prepareStatement(countQuery);
   if(functions.isValueNull(memberid))
   {
    pstmt.setString(count, memberid);
    count++;
   }
   if(functions.isValueNull(applicationId))
   {
    pstmt.setString(count, applicationId);

   }
   resultSet=pstmt.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closePreparedStatement(psMerchantSettlementList);
   Database.closeConnection(con);
  }
  return applicationManagerVOList;

 }

 public  List<ApplicationManagerVO> getapplicationManagerVOPARTNERS(String memberid,String applicationId,PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  ApplicationManagerVO applicationManagerVO=null;
  List<ApplicationManagerVO> applicationManagerVOList=new ArrayList<ApplicationManagerVO>();
  int count = 1;
  try
  {
   con=Database.getConnection();
   String qry="SELECT am.*,sp.speed_status,sp.speed_user FROM (SELECT pm.* FROM application_manager AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m WHERE m.partnerId=118)) AS am LEFT JOIN speed_status AS sp ON sp.application_id=am.application_id";
   StringBuffer query = new StringBuffer("Select am.*,sp.speed_status,sp.speed_user from `application_manager` as am left join speed_status as sp on sp.application_id=am.application_id ");
   if(functions.isValueNull(memberid) && functions.isValueNull(applicationId))
   {
    query.append(" where member_id = ? and application_id = ? ");
   }
   else
   {
    if(functions.isValueNull(memberid))
    {
     query.append(" where member_id = ? ");
    }
    if(functions.isValueNull(applicationId))
    {
     query.append(" where application_id = ? ");
    }

   }
   //add pagination
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-"+countQuery);
   query.append("limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-"+query);

   PreparedStatement psMerchantSettlementList = con.prepareStatement(query.toString());
   if(functions.isValueNull(memberid))
   {
    psMerchantSettlementList.setString(count, memberid);
    count++;
   }
   if(functions.isValueNull(applicationId))
   {
    psMerchantSettlementList.setString(count, applicationId);

   }
   resultSet=psMerchantSettlementList.executeQuery();
   while (resultSet.next())
   {
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVO.setSpeed_status(resultSet.getString("speed_status"));
    applicationManagerVO.setSpeed_user(resultSet.getString("speed_user"));
    applicationManagerVOList.add(applicationManagerVO);
   }
   count=1;
   pstmt=con.prepareStatement(countQuery);
   if(functions.isValueNull(memberid))
   {
    pstmt.setString(count, memberid);
    count++;
   }
   if(functions.isValueNull(applicationId))
   {
    pstmt.setString(count, applicationId);

   }
   resultSet=pstmt.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOList;

 }

 //for list of partner's  merchant applicationVO
 public List<ApplicationManagerVO> getPartnersMerchantApplicationManagerVO(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  try
  {

   String query ="SELECT am.* FROM (SELECT pm.* FROM application_manager AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m WHERE m.partnerId=? AND m.activation IN ('Y','T'))) AS am ORDER BY member_id";

   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(1, partnerId);

   logger.debug("query===="+query);
   resultSet=pstmt.executeQuery();
   int i =0;
   while(resultSet.next())
   {
    i++;
    //System.out.println("--memberID-->"+resultSet.getString("member_id")+"_"+resultSet.getString("application_id"));
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVO.setSpeed_status(resultSet.getString("speed_status"));
    applicationManagerVO.setSpeed_user(resultSet.getString("speed_user"));
    applicationManagerVOs.add(applicationManagerVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }


 //for list of partner's  merchant applicationVO
 public List<ApplicationManagerVO> getSuperPartnersMerchantApplicationManagerVO(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  try
  {

   String query ="SELECT am.* FROM (SELECT pm.* FROM application_manager AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m JOIN partners as p ON m.partnerId=p.partnerId WHERE (m.partnerId=? OR p.superadminid=?) AND m.activation IN ('Y','T'))) AS am ORDER BY member_id";

   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(1, partnerId);
   pstmt.setString(2, partnerId);

   logger.debug("query===="+query);
   resultSet=pstmt.executeQuery();
   int i =0;
   while(resultSet.next())
   {
    i++;
    //System.out.println("--memberID-->"+resultSet.getString("member_id")+"_"+resultSet.getString("application_id"));
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVO.setSpeed_status(resultSet.getString("speed_status"));
    applicationManagerVO.setSpeed_user(resultSet.getString("speed_user"));
    applicationManagerVOs.add(applicationManagerVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;
 }


 //for list of partner's  merchant applicationVO
 public List<ApplicationManagerVO> getPartnersMerchantApplicationManagerVO1(String partnerId, PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  try
  {

   StringBuffer query =new StringBuffer("SELECT am.* FROM (SELECT pm.* FROM application_manager AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m WHERE m.partnerId=? AND m.activation IN ('Y','T') )) AS am ORDER BY member_id desc");
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-"+countQuery);
   query.append(" limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-" + query);

   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(1,partnerId);

   resultSet=pstmt.executeQuery();
   int i =0;
   while(resultSet.next())
   {
    i++;
    //System.out.println("--memberID-->"+resultSet.getString("member_id")+"_"+resultSet.getString("application_id"));
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVO.setSpeed_status(resultSet.getString("speed_status"));
    applicationManagerVO.setSpeed_user(resultSet.getString("speed_user"));
    applicationManagerVOs.add(applicationManagerVO);
   }

   //int count=1;
   PreparedStatement preparedStatement = con.prepareStatement(countQuery);
   if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, partnerId);

   }
   resultSet = preparedStatement.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //for list of Superpartner's  merchant applicationVO
 public List<ApplicationManagerVO> getSuperPartnersMerchantApplicationManagerVO1(String partnerId, PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  try
  {

   StringBuffer query =new StringBuffer("SELECT am.* FROM (SELECT pm.* FROM application_manager AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m join partners p ON m.partnerId=p.partnerId WHERE (m.partnerId=? OR p.superadminid=?)  AND m.activation IN ('Y','T') )) AS am ORDER BY member_id desc");
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-" + countQuery);
   query.append(" limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-" + query);

   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(1,partnerId);
   pstmt.setString(2,partnerId);

   resultSet=pstmt.executeQuery();
   int i =0;
   while(resultSet.next())
   {
    i++;
    //System.out.println("--memberID-->"+resultSet.getString("member_id")+"_"+resultSet.getString("application_id"));
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVO.setSpeed_status(resultSet.getString("speed_status"));
    applicationManagerVO.setSpeed_user(resultSet.getString("speed_user"));
    applicationManagerVOs.add(applicationManagerVO);
   }

   //int count=1;
   PreparedStatement preparedStatement = con.prepareStatement(countQuery);
   if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, partnerId);
    preparedStatement.setString(2,partnerId);
   }
   resultSet = preparedStatement.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return applicationManagerVOs;
 }

 //for list of partner's new signup  merchant applicationVO
 public List<ApplicationManagerVO> getPartnersNewMerchantApplicationManagerVO(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  HashMap<String,ApplicationManagerVO> listOFMembers = new HashMap<String,ApplicationManagerVO>();
  try
  {
   //StringBuffer query =new StringBuffer("SELECT a.application_id,a.member_id,a.STATUS,a.userApp,a.isapplicationsaved,a.appliedToModify FROM application_manager AS a, members AS m WHERE m.partnerId=? AND m.memberid=a.member_id");
   StringBuffer query =new StringBuffer("SELECT memberid FROM members WHERE activation IN('Y','T') AND partnerId=?");
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
  /* pstmt.setString(1,"Y");*/
   pstmt.setString(1,partnerId);
   resultSet=pstmt.executeQuery();
   while (resultSet.next())
   {
    String qry = "SELECT am.* FROM application_manager AS am WHERE  member_id=?";
    pstmt=con.prepareStatement(qry.toString());
    pstmt.setString(1,resultSet.getString("memberid"));
    ResultSet rs = pstmt.executeQuery();
    if(rs.next())
    {
     if(!functions.isValueNull(rs.getString("MAF_status")) && !functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("MAF_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_MAF");
      applicationManagerVO.setSpeed_status(rs.getString("speed_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_SPEED");
      applicationManagerVO.setMaf_Status(rs.getString("MAF_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
    }
    else
    {
     applicationManagerVO=new ApplicationManagerVO();
     applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
     applicationManagerVOs.add(applicationManagerVO);
    }
    listOFMembers.put(resultSet.getString("memberid"),applicationManagerVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //for list of partner's new signup  merchant applicationVO
 public List<ApplicationManagerVO> getSuperPartnersNewMerchantApplicationManagerVO(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  HashMap<String,ApplicationManagerVO> listOFMembers = new HashMap<String,ApplicationManagerVO>();
  try
  {
   //StringBuffer query =new StringBuffer("SELECT a.application_id,a.member_id,a.STATUS,a.userApp,a.isapplicationsaved,a.appliedToModify FROM application_manager AS a, members AS m WHERE m.partnerId=? AND m.memberid=a.member_id");
   StringBuffer query =new StringBuffer("SELECT memberid FROM members As m JOIN partners AS p ON m.partnerId = p.partnerId WHERE m.activation IN('Y','T') AND (m.partnerId=? OR p.superadminid =?)");
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
  /* pstmt.setString(1,"Y");*/
   pstmt.setString(1,partnerId);
   pstmt.setString(2,partnerId);
   resultSet=pstmt.executeQuery();
   while (resultSet.next())
   {
    String qry = "SELECT am.* FROM application_manager AS am WHERE  member_id=?";
    pstmt=con.prepareStatement(qry.toString());
    pstmt.setString(1,resultSet.getString("memberid"));
    ResultSet rs = pstmt.executeQuery();
    if(rs.next())
    {
     if(!functions.isValueNull(rs.getString("MAF_status")) && !functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("MAF_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_MAF");
      applicationManagerVO.setSpeed_status(rs.getString("speed_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_SPEED");
      applicationManagerVO.setMaf_Status(rs.getString("MAF_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
    }
    else
    {
     applicationManagerVO=new ApplicationManagerVO();
     applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
     applicationManagerVOs.add(applicationManagerVO);
    }
    listOFMembers.put(resultSet.getString("memberid"),applicationManagerVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //for list of partner's new signup  merchant applicationVO
 public List<ApplicationManagerVO> getPartnersNewMerchantApplicationManagerVO(String partnerId, PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  HashMap<String,ApplicationManagerVO> listOFMembers = new HashMap<String,ApplicationManagerVO>();
  try
  {
   //StringBuffer query =new StringBuffer("SELECT a.application_id,a.member_id,a.STATUS,a.userApp,a.isapplicationsaved,a.appliedToModify FROM application_manager AS a, members AS m WHERE m.partnerId=? AND m.memberid=a.member_id");
   StringBuffer query =new StringBuffer("SELECT memberid FROM members WHERE activation IN('Y','T') AND partnerId=? order by memberid desc");
   //add pagination
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-"+countQuery);
   query.append(" limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-"+query);

   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   //pstmt.setString(1,"Y");
   pstmt.setString(1,partnerId);
   resultSet=pstmt.executeQuery();
   while (resultSet.next())
   {
    String qry = "SELECT am.* FROM application_manager AS am WHERE  member_id=?";
    pstmt=con.prepareStatement(qry.toString());
    pstmt.setString(1,resultSet.getString("memberid"));
    ResultSet rs = pstmt.executeQuery();
    if(rs.next())
    {
     if(!functions.isValueNull(rs.getString("MAF_status")) && !functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("MAF_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_MAF");
      applicationManagerVO.setSpeed_status(rs.getString("speed_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_SPEED");
      applicationManagerVO.setMaf_Status(rs.getString("MAF_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
    }
    else
    {
     applicationManagerVO=new ApplicationManagerVO();
     applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
     applicationManagerVOs.add(applicationManagerVO);
    }
    listOFMembers.put(resultSet.getString("memberid"),applicationManagerVO);
   }

   PreparedStatement preparedStatement = null;
   preparedStatement=con.prepareStatement(countQuery);

  /* if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, "Y");
   }*/
   if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, partnerId);
   }

   resultSet=preparedStatement.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //for list of partner's new signup  merchant applicationVO
 public List<ApplicationManagerVO> getSuperPartnersNewMerchantApplicationManagerVO(String partnerId, PaginationVO paginationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  ApplicationManagerVO applicationManagerVO=null;
  HashMap<String,ApplicationManagerVO> listOFMembers = new HashMap<String,ApplicationManagerVO>();
  try
  {
   //StringBuffer query =new StringBuffer("SELECT a.application_id,a.member_id,a.STATUS,a.userApp,a.isapplicationsaved,a.appliedToModify FROM application_manager AS a, members AS m WHERE m.partnerId=? AND m.memberid=a.member_id");
   StringBuffer query =new StringBuffer("SELECT memberid FROM members As m JOIN partners AS p ON m.partnerId = p.partnerId WHERE m.activation IN('Y','T') AND (m.partnerId=? OR p.superadminid =?) order by memberid desc");
   //add pagination
   String countQuery="Select Count(*) from ("+query.toString()+") as temp";
   logger.debug("countQuery:-"+countQuery);
   query.append(" limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("Query:-"+query);

   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   //pstmt.setString(1,"Y");
   pstmt.setString(1,partnerId);
   pstmt.setString(2,partnerId);
   resultSet=pstmt.executeQuery();
   while (resultSet.next())
   {
    String qry = "SELECT am.* FROM application_manager AS am WHERE  member_id=?";
    pstmt=con.prepareStatement(qry.toString());
    pstmt.setString(1,resultSet.getString("memberid"));
    ResultSet rs = pstmt.executeQuery();
    if(rs.next())
    {
     if(!functions.isValueNull(rs.getString("MAF_status")) && !functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("MAF_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_MAF");
      applicationManagerVO.setSpeed_status(rs.getString("speed_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
     else if(!functions.isValueNull(rs.getString("speed_status")))
     {
      applicationManagerVO=new ApplicationManagerVO();
      applicationManagerVO.setApplicationId(rs.getString("application_id"));
      applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_SPEED");
      applicationManagerVO.setMaf_Status(rs.getString("MAF_status"));
      applicationManagerVOs.add(applicationManagerVO);
     }
    }
    else
    {
     applicationManagerVO=new ApplicationManagerVO();
     applicationManagerVO.setMemberId(resultSet.getString("memberid")+"_CREATE");
     applicationManagerVOs.add(applicationManagerVO);
    }
    listOFMembers.put(resultSet.getString("memberid"),applicationManagerVO);
   }

   PreparedStatement preparedStatement = null;
   preparedStatement=con.prepareStatement(countQuery);

  /* if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, "Y");
   }*/
   if(functions.isValueNull(partnerId))
   {
    preparedStatement.setString(1, partnerId);
    preparedStatement.setString(2, partnerId);
   }

   resultSet=preparedStatement.executeQuery();
   if(resultSet.next())
   {
    paginationVO.setTotalRecords(resultSet.getInt(1));
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //for list of applicationVO
 public List<ApplicationManagerVO> getApplicationManagerVO(ApplicationManagerVO applicationManagerVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  int counter=1;
  try
  {
   //StringBuffer query =new StringBuffer("select application_id,member_id,status,userApp,isapplicationsaved,appliedToModify from application_manager where application_id>0");
   StringBuffer query =new StringBuffer("select application_id,member_id,status,maf_user,isapplicationsaved,appliedToModify from application_manager where application_id>0");
   if(applicationManagerVO!=null)
   {
    if(functions.isValueNull(applicationManagerVO.getStatus()))
    {
     query.append(" and status =?");
    }
   }
   query.append(" ORDER BY member_id");
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   if(applicationManagerVO!=null)
   {
    if(functions.isValueNull(applicationManagerVO.getStatus()))
    {
     pstmt.setString(counter,applicationManagerVO.getStatus());
    }
   }

   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVOs.add(applicationManagerVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;
 }

 public List<ApplicationManagerVO> getPartnerApplicationManagerVO(ApplicationManagerVO applicationManagerVO,String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  int counter=1;
  try
  {

   //StringBuffer query =new StringBuffer("select application_id,member_id,status,userApp,isapplicationsaved,appliedToModify from application_manager where application_id>0");
   StringBuffer query =new StringBuffer("SELECT application_id,member_id,STATUS,maf_user,isapplicationsaved,appliedToModify FROM application_manager, members WHERE members.memberid=application_manager.member_id AND members.partnerId=?");
   if(applicationManagerVO!=null)
   {
    if(functions.isValueNull(applicationManagerVO.getStatus()))
    {
     query.append(" and status =?");
    }
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(counter,partnerId);
   counter++;
   if(applicationManagerVO!=null)
   {
    if(functions.isValueNull(applicationManagerVO.getStatus()))
    {
     pstmt.setString(counter,applicationManagerVO.getStatus());
    }
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVOs.add(applicationManagerVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 public List<ApplicationManagerVO> getSuperPartnerApplicationManagerVO(ApplicationManagerVO applicationManagerVO,String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  List<ApplicationManagerVO> applicationManagerVOs=new ArrayList<ApplicationManagerVO>();
  int counter=1;
  try
  {

   //StringBuffer query =new StringBuffer("select application_id,member_id,status,userApp,isapplicationsaved,appliedToModify from application_manager where application_id>0");
   StringBuffer query =new StringBuffer("SELECT am.application_id,am.member_id,am.STATUS,am.maf_user,am.isapplicationsaved,am.appliedToModify FROM members m JOIN application_manager am ON m.memberid=am.member_id JOIN  partners p  ON  p.partnerId = m.partnerId WHERE m.partnerId=? OR p.superadminid=?");
   if(applicationManagerVO!=null)
   {
    if(functions.isValueNull(applicationManagerVO.getStatus()))
    {
     query.append(" and status =?");
    }
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   pstmt.setString(counter,partnerId);
   counter++;
   pstmt.setString(counter,partnerId);
   counter++;
   if(applicationManagerVO!=null)
   {
    if(functions.isValueNull(applicationManagerVO.getStatus()))
    {
     pstmt.setString(counter,applicationManagerVO.getStatus());
    }
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    applicationManagerVO=new ApplicationManagerVO();
    applicationManagerVO.setMemberId(resultSet.getString("member_id"));
    applicationManagerVO.setApplicationId(resultSet.getString("application_id"));
    applicationManagerVO.setStatus(resultSet.getString("status"));
    applicationManagerVO.setApplicationSaved(resultSet.getString("isapplicationsaved"));
    applicationManagerVO.setUser(resultSet.getString("maf_user"));
    applicationManagerVO.setAppliedToModify(resultSet.getString("appliedToModify"));
    applicationManagerVOs.add(applicationManagerVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getapplicationManager",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for getapplicationManager",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return applicationManagerVOs;

 }

 //getting uploaded file detail as per member
 public HashMap<String, FileDetailsListVO> getApplicationUploadedFileDetail(String memberId)  //Changes for Multiple KYC
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  HashMap<String,FileDetailsListVO> fileDetailsVOHashMap=new HashMap<String, FileDetailsListVO>();
  try
  {

   String query = "select * from member_document_mapping where member_id =?";

   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query);
   pstmt.setString(1,memberId);
   resultSet=pstmt.executeQuery();
   MultiHashMap mp = new MultiHashMap();
   while(resultSet.next())
   {
    AppFileDetailsVO fileDetailsVO=new AppFileDetailsVO();
    fileDetailsVO.setMappingId(resultSet.getString("mapping_id"));
    fileDetailsVO.setMemberid(resultSet.getString("member_id"));
    fileDetailsVO.setFilename(resultSet.getString("document_name"));
    fileDetailsVO.setFileType(resultSet.getString("document_type"));
    fileDetailsVO.setFieldName(resultSet.getString("alternate_name"));
    fileDetailsVO.setLabelId(resultSet.getString("label_id"));
    fileDetailsVO.setFileActionType(resultSet.getBoolean("upload_status") ? AppFileActionType.UPLOAD : resultSet.getBoolean("replace_status") ? AppFileActionType.REPLACE : AppFileActionType.EXCEPTION);
    fileDetailsVO.setSuccess(resultSet.getBoolean("upload_status") ? resultSet.getBoolean("upload_status") : resultSet.getBoolean("replace_status") ? resultSet.getBoolean("replace_status") : false);
    fileDetailsVO.setTimestamp(resultSet.getString("timestamp"));
    mp.put(fileDetailsVO.getFieldName(),fileDetailsVO);
   }

   Set set = mp.entrySet();
   Iterator i = set.iterator();
   List<AppFileDetailsVO> list = null;
   while(i.hasNext()) {
    Map.Entry me = (Map.Entry)i.next();
    list=(List<AppFileDetailsVO>)mp.get(me.getKey());
    FileDetailsListVO fileDetailsListVO = new FileDetailsListVO();
    fileDetailsListVO.setFiledetailsvo(list);
    fileDetailsVOHashMap.put(me.getKey().toString(),fileDetailsListVO);

    for(int j=0;j<list.size();j++)
    {   AppFileDetailsVO fd = list.get(j);
     //System.out.println(":Key :"+me.getKey()+": value :"+fd.getFilename());
    }
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getting uploaded document details",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for uploaded document details",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return fileDetailsVOHashMap;
 }



 //insert bankApplication Master

 public AppFileDetailsVO getSingleApplicationUploadedFileDetail(String mapping_id)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;

  AppFileDetailsVO fileDetailsVO=null;
  try
  {

   String query = "select * from member_document_mapping where mapping_id =?";

   con=Database.getConnection();
   pstmt=con.prepareStatement(query);
   pstmt.setString(1,mapping_id);
   resultSet=pstmt.executeQuery();
   if(resultSet.next())
   {
    fileDetailsVO=new AppFileDetailsVO();
    fileDetailsVO.setMappingId(resultSet.getString("mapping_id"));
    fileDetailsVO.setMemberid(resultSet.getString("member_id"));
    fileDetailsVO.setFilename(resultSet.getString("document_name"));
    fileDetailsVO.setFileType(resultSet.getString("document_type"));
    fileDetailsVO.setFieldName(resultSet.getString("alternate_name"));
    fileDetailsVO.setLabelId(resultSet.getString("label_id"));
    fileDetailsVO.setFileActionType(resultSet.getBoolean("upload_status")? AppFileActionType.UPLOAD:resultSet.getBoolean("replace_status")?AppFileActionType.REPLACE:AppFileActionType.EXCEPTION);
    fileDetailsVO.setSuccess(resultSet.getBoolean("upload_status")? resultSet.getBoolean("upload_status"):resultSet.getBoolean("replace_status")?resultSet.getBoolean("replace_status"):false);
    fileDetailsVO.setTimestamp(resultSet.getString("timestamp"));
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for getting uploaded document details",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for uploaded document details",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return fileDetailsVO;
 }

 public boolean insertbankApplicationMaster(ApplicationManagerVO applicationManagerVO,AppFileDetailsVO fileDetailsVO)
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  try

  {
   conn = Database.getConnection();
   query = "INSERT INTO bankapplicationmaster(application_id,pgtypeid,member_id,bankfilename,status,dtstamp,timestamp)VALUES(?,?,?,?,?,?,?)";
   pstmt= conn.prepareStatement(query);

   if(functions.isValueNull(applicationManagerVO.getApplicationId()))
   {
    pstmt.setString(1,applicationManagerVO.getApplicationId());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFieldName()))
   {
    pstmt.setString(2,fileDetailsVO.getFieldName());
   }
   else
   {
    pstmt.setNull(2,Types.INTEGER);
   }
   if(functions.isValueNull(applicationManagerVO.getMemberId()))
   {
    pstmt.setString(3,applicationManagerVO.getMemberId());
   }
   else
   {
    pstmt.setNull(3,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFilename()))
   {
    pstmt.setString(4,fileDetailsVO.getFilename());
   }
   else
   {
    pstmt.setNull(4,Types.VARCHAR);
   }

   pstmt.setString(5, BankApplicationStatus.GENERATED.name());

   if(functions.isValueNull(fileDetailsVO.getDtstamp()))
   {
    pstmt.setString(6,fileDetailsVO.getDtstamp());
   }
   else
   {
    pstmt.setNull(6,Types.TIMESTAMP);
   }
   if(functions.isValueNull(fileDetailsVO.getTimestamp()))
   {
    pstmt.setString(7,fileDetailsVO.getTimestamp());
   }
   else
   {
    pstmt.setNull(7,Types.TIMESTAMP);
   }


   int k = pstmt.executeUpdate();
   if (k>0)
   {
    ResultSet resultSet=pstmt.getGeneratedKeys();
    while(resultSet.next())
    {
     fileDetailsVO.setMappingId(resultSet.getString(1));
    }
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error("Leaving insertbankApplicationMaster throwing SQL Exception as System Error :::: ",systemError);

  }
  catch (SQLException e)
  {
   logger.error("Leaving insertbankApplicationMaster throwing SQL Exception as System Error :::: ",e);

  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertbankApplicationMaster");

  return false;
 }

 //populate bankApplicationMaster
 public Map<String,BankApplicationMasterVO> getBankApplicationMasterVO(BankApplicationMasterVO bankApplicationMasterVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,BankApplicationMasterVO> bankApplicationMasterVOs=new HashMap<String, BankApplicationMasterVO>();
  int counter=1;
  try
  {

   StringBuffer query =new StringBuffer("select bankapplicationid,application_id,member_id,bankfilename,status,remark,dtstamp,timestamp,pgtypeid from bankapplicationmaster where bankapplicationid>0");
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     query.append(" and application_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     query.append(" and bankfilename =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     query.append(" and status =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     query.append(" and remark =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getApplication_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getMember_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getTimestamp() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getPgtypeid() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankapplicationid() );
     counter++;
    }
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    bankApplicationMasterVO=new BankApplicationMasterVO();
    bankApplicationMasterVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    bankApplicationMasterVO.setApplication_id(resultSet.getString("application_id"));
    bankApplicationMasterVO.setMember_id(resultSet.getString("member_id"));
    bankApplicationMasterVO.setPgtypeid(resultSet.getString("pgtypeid"));
    bankApplicationMasterVO.setBankfilename(resultSet.getString("bankfilename"));
    bankApplicationMasterVO.setStatus(resultSet.getString("status"));
    bankApplicationMasterVO.setRemark(resultSet.getString("remark"));
    bankApplicationMasterVO.setDtstamp(resultSet.getString("dtstamp"));
    bankApplicationMasterVO.setTimestamp(resultSet.getString("timestamp"));
    bankApplicationMasterVOs.put(bankApplicationMasterVO.getBankapplicationid(),bankApplicationMasterVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return bankApplicationMasterVOs;

 }
    /*END Select Member ID and Status Wise*/

 /*Start Select Member ID and Status Wise*/
 public Map<String,List<BankApplicationMasterVO>> getBankApplicationMasterVOForGatewayIdandStatus(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs=new HashMap<String, List<BankApplicationMasterVO>>();
  List<BankApplicationMasterVO> bankApplicationMasterVOList=null;
  int counter=1;
  try
  {
   StringBuffer query =new StringBuffer("select bankapplicationid,application_id,member_id,bankfilename,status,remark,dtstamp,timestamp,pgtypeid from bankapplicationmaster where bankapplicationid>0");
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     query.append(" and application_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     query.append(" and bankfilename =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     query.append(" and status =?");
     logger.debug("Status....." + bankApplicationMasterVO.getStatus());
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     query.append(" and remark =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }
    if(functions.isValueNull(orderBy))
     query.append(" ORDER BY "+orderBy);
    if(functions.isValueNull(groupBy))
     query.append(" GROUP BY "+groupBy);
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());
   logger.debug("pstmt status 1--->"+pstmt);

   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getApplication_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getMember_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getTimestamp() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getPgtypeid() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankapplicationid() );
     counter++;
    }
   }
   resultSet=pstmt.executeQuery();
   logger.debug("pstmt Status 2--->"+pstmt);
   while(resultSet.next())
   {
    bankApplicationMasterVO=new BankApplicationMasterVO();
    bankApplicationMasterVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    bankApplicationMasterVO.setApplication_id(resultSet.getString("application_id"));
    bankApplicationMasterVO.setMember_id(resultSet.getString("member_id"));
    bankApplicationMasterVO.setPgtypeid(resultSet.getString("pgtypeid"));
    bankApplicationMasterVO.setBankfilename(resultSet.getString("bankfilename"));
    bankApplicationMasterVO.setStatus(resultSet.getString("status"));
    bankApplicationMasterVO.setRemark(resultSet.getString("remark"));
    bankApplicationMasterVO.setDtstamp(resultSet.getString("dtstamp"));
    bankApplicationMasterVO.setTimestamp(resultSet.getString("timestamp"));
    if(bankApplicationMasterVOs.containsKey(bankApplicationMasterVO.getPgtypeid()))
    {
     logger.debug("already present::::"+bankApplicationMasterVO.getPgtypeid());
     bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankApplicationMasterVO.getPgtypeid());
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getPgtypeid(),bankApplicationMasterVOList);
    }
    else
    {
     logger.debug("1st time adding::::"+bankApplicationMasterVO.getPgtypeid());
     bankApplicationMasterVOList=new ArrayList<BankApplicationMasterVO>();
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getPgtypeid(),bankApplicationMasterVOList);
    }
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return bankApplicationMasterVOs;

 }
 //Update Member ID in Consolidated Application

 public Map<String,List<BankApplicationMasterVO>> getBankApplicationMasterVOForGatewayId(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs=new HashMap<String, List<BankApplicationMasterVO>>();
  List<BankApplicationMasterVO> bankApplicationMasterVOList=null;
  int counter=1;
  try
  {
   StringBuffer query =new StringBuffer("select bankapplicationid,application_id,member_id,bankfilename,status,remark,dtstamp,timestamp,pgtypeid from bankapplicationmaster where bankapplicationid>0");
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     query.append(" and application_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     query.append(" and bankfilename =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     query.append(" and status =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     query.append(" and remark =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }
    if(functions.isValueNull(orderBy))
     query.append(" ORDER BY "+orderBy);
    if(functions.isValueNull(groupBy))
     query.append(" GROUP BY "+groupBy);
   }
   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());
   logger.debug("pstmt--->"+pstmt);

   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getApplication_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getMember_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getTimestamp() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getPgtypeid() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankapplicationid() );
     counter++;
    }
   }
   logger.debug("pstmt--->"+pstmt);
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    bankApplicationMasterVO=new BankApplicationMasterVO();
    bankApplicationMasterVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    bankApplicationMasterVO.setApplication_id(resultSet.getString("application_id"));
    bankApplicationMasterVO.setMember_id(resultSet.getString("member_id"));
    bankApplicationMasterVO.setPgtypeid(resultSet.getString("pgtypeid"));
    bankApplicationMasterVO.setBankfilename(resultSet.getString("bankfilename"));
    bankApplicationMasterVO.setStatus(resultSet.getString("status"));
    bankApplicationMasterVO.setRemark(resultSet.getString("remark"));
    bankApplicationMasterVO.setDtstamp(resultSet.getString("dtstamp"));
    bankApplicationMasterVO.setTimestamp(resultSet.getString("timestamp"));
    if(bankApplicationMasterVOs.containsKey(bankApplicationMasterVO.getPgtypeid()))
    {
     logger.debug("already present::::"+bankApplicationMasterVO.getPgtypeid());
     bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankApplicationMasterVO.getPgtypeid());
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getPgtypeid(),bankApplicationMasterVOList);
    }
    else
    {
     logger.debug("1st time adding::::"+bankApplicationMasterVO.getPgtypeid());
     bankApplicationMasterVOList=new ArrayList<BankApplicationMasterVO>();
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getPgtypeid(),bankApplicationMasterVOList);
    }
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return bankApplicationMasterVOs;

 }

 public Map<String,List<BankApplicationMasterVO>> getBankApplicationMasterVOForMemberId(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,List<BankApplicationMasterVO>> bankApplicationMasterVOs=new TreeMap<String, List<BankApplicationMasterVO>>();
  List<BankApplicationMasterVO> bankApplicationMasterVOList=null;
  int counter=1;
  try
  {

   StringBuffer query =new StringBuffer("select bankapplicationid,application_id,member_id,bankfilename,status,remark,dtstamp,timestamp,pgtypeid from bankapplicationmaster where bankapplicationid>0");
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     query.append(" and application_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     query.append(" and bankfilename =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     query.append(" and status =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     query.append(" and remark =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }

    if(functions.isValueNull(groupBy))
     query.append(" GROUP BY "+groupBy);
    if(functions.isValueNull(orderBy))
     query.append(" ORDER BY "+orderBy);
   }
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   logger.debug("Query change::::"+query.toString());
   if(bankApplicationMasterVO!=null)
   {
    if(functions.isValueNull(bankApplicationMasterVO.getApplication_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getApplication_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getMember_id()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getMember_id() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getTimestamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getTimestamp() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getPgtypeid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getPgtypeid() );
     counter++;
    }
    if(functions.isValueNull(bankApplicationMasterVO.getBankapplicationid()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankapplicationid() );
     counter++;
    }
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    bankApplicationMasterVO=new BankApplicationMasterVO();
    bankApplicationMasterVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    bankApplicationMasterVO.setApplication_id(resultSet.getString("application_id"));
    bankApplicationMasterVO.setMember_id(resultSet.getString("member_id"));
    bankApplicationMasterVO.setPgtypeid(resultSet.getString("pgtypeid"));
    bankApplicationMasterVO.setBankfilename(resultSet.getString("bankfilename"));
    bankApplicationMasterVO.setStatus(resultSet.getString("status"));
    bankApplicationMasterVO.setRemark(resultSet.getString("remark"));
    bankApplicationMasterVO.setDtstamp(resultSet.getString("dtstamp"));
    bankApplicationMasterVO.setTimestamp(resultSet.getString("timestamp"));
    if(bankApplicationMasterVOs.containsKey(bankApplicationMasterVO.getMember_id()))
    {
     logger.debug("already present::::"+bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList=bankApplicationMasterVOs.get(bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getMember_id(),bankApplicationMasterVOList);
    }
    else
    {
     logger.debug("1st time adding::::"+bankApplicationMasterVO.getMember_id());
     bankApplicationMasterVOList=new ArrayList<BankApplicationMasterVO>();
     bankApplicationMasterVOList.add(bankApplicationMasterVO);
     bankApplicationMasterVOs.put(bankApplicationMasterVO.getMember_id(),bankApplicationMasterVOList);
    }
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return bankApplicationMasterVOs;

 }

 //update bankApplicationMaster
 public boolean updateBankApplicationMasterVO(BankApplicationMasterVO bankApplicationMasterVO,String bankapplicationid,String memberId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  int resultSet=0;
  List<BankApplicationMasterVO> bankApplicationMasterVOs=new ArrayList<BankApplicationMasterVO>();
  int counter=1;
  boolean setStarted=false;

  try
  {
   logger.error("update query for updateBankApplicationMasterVO");
   StringBuffer query =new StringBuffer("update bankapplicationmaster set ");
   if(bankApplicationMasterVO!=null)
   {


    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" bankfilename =?");

     setStarted=true;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" status =?");
     logger.error("settttt Status-----"+bankApplicationMasterVO.getStatus());

     setStarted=true;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" remark =?");
     setStarted=true;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" dtstamp =?");
     setStarted=true;
    }

   }
   query.append(" where bankapplicationid >0");
   if(functions.isValueNull(bankapplicationid))
   {
    query.append(" and bankapplicationid =?");
    logger.error("applicationId test---"+bankapplicationid);
   }
   if(functions.isValueNull(memberId))
   {
    query.append(" and member_id =?");
   }

   logger.error("bank app master query"+query);
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   if(bankApplicationMasterVO!=null)
   {

    if(functions.isValueNull(bankApplicationMasterVO.getBankfilename()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getBankfilename() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getStatus()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getStatus() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getRemark()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getRemark() );
     counter++;
    }

    if(functions.isValueNull(bankApplicationMasterVO.getDtstamp()))
    {
     pstmt.setString(counter,bankApplicationMasterVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(bankapplicationid))
    {
     pstmt.setString(counter,bankapplicationid);
     counter++;
    }
    if(functions.isValueNull(memberId))
    {
     pstmt.setString(counter,memberId);
     counter++;
    }
   }

   resultSet=pstmt.executeUpdate();
   if(resultSet>0)
   {
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return false;

 }
 //Delete

 //Insert consolidated application
 public boolean insertconsolidated_application(AppFileDetailsVO fileDetailsVO,String adminId)
 {

  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO consolidated_application(member_id,pgtypeid,bankapplicationid,status,filename,dtstamp,adminId)VALUES(?,?,?,?,?,?,?)";
   pstmt= conn.prepareStatement(query);
   if(functions.isValueNull(fileDetailsVO.getMemberid()))
   {
    pstmt.setString(1,fileDetailsVO.getMemberid());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }

   if(functions.isValueNull(fileDetailsVO.getLabelId()))
   {
    pstmt.setString(2,fileDetailsVO.getLabelId());
   }
   else
   {
    pstmt.setNull(2,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFieldName()))
   {
    pstmt.setString(3,fileDetailsVO.getFieldName());
   }
   else
   {
    pstmt.setNull(3,Types.VARCHAR);
   }

   pstmt.setString(4, BankApplicationStatus.GENERATED.name());

   if(functions.isValueNull(fileDetailsVO.getFilename()))
   {
    pstmt.setString(5,fileDetailsVO.getFilename());
   }
   else
   {
    pstmt.setNull(5,Types.VARCHAR);
   }

   if(functions.isValueNull(fileDetailsVO.getDtstamp()))
   {
    pstmt.setString(6,fileDetailsVO.getDtstamp());
   }
   else
   {
    pstmt.setNull(6,Types.TIMESTAMP);
   }

   pstmt.setString(7, adminId);
   int k = pstmt.executeUpdate();
   if (k>0)
   {
    ResultSet resultSet=pstmt.getGeneratedKeys();
    while(resultSet.next())
    {
     fileDetailsVO.setMappingId(resultSet.getString(1));
    }
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error("Leaving insertConsolidatedApplication throwing SQL Exception as System Error :::: ",systemError);

  }
  catch (SQLException e)
  {
   logger.error("Leaving insertConsolidatedApplication throwing SQL Exception as System Error :::: ",e);

  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving insertConsolidatedApplication");

  return false;
 }

 //Insert Consolidated Application History

 public boolean deleteConsolidated_Application(String consolidated_id)
 {
  Connection connection=null;
  try
  {
   connection = Database.getConnection();
   String deleteQuery = "DELETE FROM consolidated_application WHERE consolidated_id=?";
   PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
   preparedStatement.setString(1,consolidated_id);
   int delete=preparedStatement.executeUpdate();

   if(delete>0)
   {
    return true;
   }

  }
  catch (SystemError se)
  {
   logger.debug("Exception----------"+se);
  }
  catch (SQLException e)
  {
   logger.debug("Exception................."+e);
  }
  finally {
   Database.closeConnection(connection);
  }
  return  false;
 }

 public boolean insertConsolidated_Application_History(AppFileDetailsVO fileDetailsVO,String adminId)
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO consolidated_application_history(consolidated_id,member_id,pgtypeid,bankapplicationid,status,adminId,filename,dtstamp)VALUES(?,?,?,?,?,?,?,?)";
   pstmt= conn.prepareStatement(query);

   if(functions.isValueNull(fileDetailsVO.getMappingId()))
   {
    pstmt.setString(1,fileDetailsVO.getMappingId());
   }
   else
   {
    pstmt.setNull(1,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getMemberid()))
   {
    pstmt.setString(2,fileDetailsVO.getMemberid());
   }
   else
   {
    pstmt.setNull(2,Types.INTEGER);
   }

   if(functions.isValueNull(fileDetailsVO.getLabelId()))
   {
    pstmt.setString(3,fileDetailsVO.getLabelId());
   }
   else
   {
    pstmt.setNull(3,Types.INTEGER);
   }
   if(functions.isValueNull(fileDetailsVO.getFieldName()))
   {
    pstmt.setString(4,fileDetailsVO.getFieldName());
   }
   else
   {
    pstmt.setNull(4,Types.VARCHAR);
   }

   pstmt.setString(5, BankApplicationStatus.GENERATED.name());

   if(functions.isValueNull(adminId))
   {
    pstmt.setString(6,adminId);
   }
   else
   {
    pstmt.setNull(6,Types.INTEGER);
   }


   if(functions.isValueNull(fileDetailsVO.getFilename()))
   {
    pstmt.setString(7,fileDetailsVO.getFilename());
   }
   else
   {
    pstmt.setNull(7,Types.VARCHAR);
   }

   if(functions.isValueNull(fileDetailsVO.getDtstamp()))
   {
    pstmt.setString(8,fileDetailsVO.getDtstamp());
   }
   else
   {
    pstmt.setNull(8,Types.TIMESTAMP);
   }
   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error("Leaving insertConsolidatedApplication throwing SQL Exception as System Error :::: ",systemError);

  }
  catch (SQLException e)
  {
   logger.error("Leaving insertConsolidatedApplication throwing SQL Exception as System Error :::: ",e);

  }
  finally
  {
   Database.closeConnection(conn);
  }

  return false;
 }

 //update Consolidated Application from Consildated ID
 public boolean updateConsolidatedAppHistory(ConsolidatedAppStatus consolidatedAppStatus,String consolidatedId)throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement pstmt=null;

  try
  {
   conn = Database.getConnection();
   String updateConsolidatedApp="update consolidated_application_history set status=? where consolidated_id=? ";

   pstmt= conn.prepareStatement(updateConsolidatedApp);

   if(consolidatedAppStatus!=null)
   {
    pstmt.setString(1,consolidatedAppStatus.INVALIDATED.toString());
    logger.debug("inside query....invalidated"+ConsolidatedAppStatus.INVALIDATED.toString());
   }
   else
   {

    pstmt.setString(1, ConsolidatedAppStatus.GENERATED.toString());
    logger.debug("inside query....GENERATED"+ConsolidatedAppStatus.GENERATED.toString());
   }
   pstmt.setString(2,consolidatedId);

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.debug("Exception__________"+e);
  }
  catch (SystemError systemError)
  {
   logger.debug("Exception__________"+systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateConsolidatedAppHistory");

  return false;
 }

 //Updated Consolidated Application

 //populate consolidated application
 public Map<String,ConsolidatedApplicationVO> getconsolidated_application(ConsolidatedApplicationVO consolidatedApplicationVO)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  try
  {

   StringBuffer query =new StringBuffer("select consolidated_id,member_id,pgtypeid,bankapplicationid,status,filename,dtstamp,timestamp from consolidated_application where consolidated_id>0");
   if(consolidatedApplicationVO!=null)
   {
    if(functions.isValueNull(consolidatedApplicationVO.getMemberid()))
    {
     query.append(" and member_id =?");
    }

    if(functions.isValueNull(consolidatedApplicationVO.getPgtypeid()))
    {
     query.append(" and pgtypeid =?");
    }

    if(functions.isValueNull(consolidatedApplicationVO.getBankapplicationid()))
    {
     query.append(" and bankapplicationid =?");
    }
    if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
    {
     query.append(" and status =?");
    }

    if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
    {
     query.append(" and filename =?");
    }

    if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
    {
     query.append(" and dtstamp =?");
    }
    if(functions.isValueNull(consolidatedApplicationVO.getTimestamp()))
    {
     query.append(" and timestamp =?");
    }
    if(functions.isValueNull(consolidatedApplicationVO.getConsolidated_id()))
    {
     query.append(" and consolidated_id =?");
    }
   }
   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());

   if(consolidatedApplicationVO!=null)
   {

    if(functions.isValueNull(consolidatedApplicationVO.getMemberid()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getMemberid());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getPgtypeid()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getPgtypeid() );
     counter++;
    }


    if(functions.isValueNull(consolidatedApplicationVO.getBankapplicationid()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getBankapplicationid());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getStatus());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getFilename());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getDtstamp());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getTimestamp()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getTimestamp());
     counter++;
    }
    if(functions.isValueNull(consolidatedApplicationVO.getConsolidated_id()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getConsolidated_id());
     counter++;
    }
   }

   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO=new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getPgtypeid(),consolidatedApplicationVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;

 }

 public boolean updateconsolidated_application(AppFileDetailsVO fileDetailsVO,String consolidated_id,String memberid)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  int resultSet=0;
  List<ConsolidatedApplicationVO> consolidatedApplicationVOs=new ArrayList<ConsolidatedApplicationVO>();
  int counter=1;
  boolean setStarted=false;

  try
  {
   logger.error("update query for updateconsolidatedApplicationVO");
   StringBuffer query =new StringBuffer("update bankapplicationmaster set ");
   if(fileDetailsVO!=null)
   {

    if(functions.isValueNull(fileDetailsVO.getFilename()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" filename =?");

     setStarted=true;
    }

                /*if(functions.isValueNull(BankApplicationStatus.))
                {
                    if(setStarted)
                    {
                        query.append(",");
                    }
                    query.append(" status =?");
                    logger.error("settttt Status-----"+consolidatedApplicationVO.getStatus());

                    setStarted=true;
                }
*/
    if(functions.isValueNull(fileDetailsVO.getDtstamp()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" dtstamp =?");
     setStarted=true;
    }

   }
   query.append(" where consolidated_id >0");
   if(functions.isValueNull(consolidated_id))
   {
    query.append(" and consolidated_id =?");
    logger.error("consolidated_id test---" + consolidated_id);
   }
   if(functions.isValueNull(memberid))
   {
    query.append(" and member_id =?");
   }

   logger.error("consolidated Application query"+query);
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   if(fileDetailsVO!=null)
   {

    if(functions.isValueNull(fileDetailsVO.getFilename()))
    {
     pstmt.setString(counter,fileDetailsVO.getFilename());
     counter++;
    }

                /*if(functions.isValueNull(fileDetailsVO.getStatus()))
                {
                    pstmt.setString(counter,consolidatedApplicationVO.getStatus() );
                    counter++;
                }*/

    if(functions.isValueNull(fileDetailsVO.getDtstamp()))
    {
     pstmt.setString(counter,fileDetailsVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(consolidated_id))
    {
     pstmt.setString(counter,consolidated_id);
     counter++;
    }
    if(functions.isValueNull(memberid))
    {
     pstmt.setString(counter,memberid);
     counter++;
    }
   }

   resultSet=pstmt.executeUpdate();
   if(resultSet>0)
   {
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Updated Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Updated Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return false;

 }

 //Update ConsolidatedApplication Status

 /**
  * Get Consolidated infornmation according to the Memberid and Pgtypeid
  * @param
  * @return
  */
 public Map<String,ConsolidatedApplicationVO> getconsolidated_applicationIDpgtypeID(String memberId,String name,String consolidatedId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  try
  {

   StringBuffer query =new StringBuffer("SELECT CA.consolidated_id,CA.member_id,CA.pgtypeid,CA.bankapplicationid,CA.status,CA.filename,CA.dtstamp,CA.timestamp,BTM.bank_name FROM consolidated_application AS CA JOIN bank_template_mapping AS BTM ON BTM.bank_id = CA.pgtypeid WHERE CA.consolidated_id>0");

   if(functions.isValueNull(memberId))
   {
    query.append(" and CA.member_id =?");
   }

   if(functions.isValueNull(name))
   {
    query.append(" and BTM.bank_name =?");
   }

   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and CA.consolidated_id =?");
   }
   query.append(" ORDER BY CA.member_id");

   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   logger.debug("pstmt--->"+pstmt);

   if(functions.isValueNull(memberId))
   {
    pstmt.setString(counter,memberId);
    counter++;
   }
   if(functions.isValueNull(name))
   {
    pstmt.setString(counter,name);
    counter++;
   }
   if(functions.isValueNull(consolidatedId))
   {
    pstmt.setString(counter,consolidatedId);
    counter++;
   }

   //System.out.println("mapped members : "+pstmt.toString());

   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {

    consolidatedApplicationVO=new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setGatewayname(resultSet.getString("bank_name"));
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));


    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(),consolidatedApplicationVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;
 }

 //This is for merchant Bank Mapping

 public boolean updateConsolidatedAppStatus(ConsolidatedAppStatus consolidatedAppStatus,String consolidatedId)throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement pstmt=null;

  try
  {
   conn = Database.getConnection();
   String updateconsolidatedAppStatus="update consolidated_application set status=? where consolidated_id=? ";
   pstmt= conn.prepareStatement(updateconsolidatedAppStatus);

   if(consolidatedAppStatus!=null)
   {
    pstmt.setString(1,consolidatedAppStatus.toString());
   }
   else
   {
    pstmt.setString(1, ConsolidatedAppStatus.GENERATED.toString());
   }
   pstmt.setString(2,consolidatedId);

   int k = pstmt.executeUpdate();
   if (k>0)
   {
    return true;
   }

  }
  catch (SQLException e)
  {
   logger.debug("Exception__________"+e);
  }
  catch (SystemError systemError)
  {
   logger.debug("Exception__________"+systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving updateConsolidatedAppStatus");

  return false;
 }

 /**
  * Inserting Mapping Details of pgType and merchant
  * @return
  * @throws PZDBViolationException
  */
 public boolean insertBankMerchantMappingForApplication(List<BankTypeVO> bankTypeVOs,String memberId) throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  int result[];
  try
  {
   conn = Database.getConnection();
   query = "INSERT INTO merchant_bankmapping(memberid,pgtypeid,creation_date) VALUES (?,?,UNIX_TIMESTAMP())";
   pstmt= conn.prepareStatement(query);
   if(bankTypeVOs!=null && functions.isValueNull(memberId))
   {
    for (BankTypeVO bankTypeVO : bankTypeVOs)
    {
     if(functions.isValueNull(memberId))
     {
      pstmt.setString(1,memberId);
     }
     if(functions.isValueNull(bankTypeVO.getBankId()))
     {
      pstmt.setString(2,bankTypeVO.getBankId());
     }

     pstmt.addBatch();
    }

    result=pstmt.executeBatch();

    if(result.length>0)
    {
     return true;
    }
   }
  }
  catch (SystemError systemError)
  {
   logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",systemError);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "insertBankMerchantMappingForApplication()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
  }
  catch (SQLException e)
  {
   logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",e);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
  }
  finally
  {
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(conn);
  }
  return false;
 }


    /*public Map<String,GatewayTypeVO> getBankMemberMappingWithGateway(String partnerId,String memberId) throws PZDBViolationException
    {
        Connection con=null;
        PreparedStatement preparedStatement=null;
        ResultSet resultSet=null;

        Map<String,GatewayTypeVO> gatewayTypeVOs=new HashMap<String, GatewayTypeVO>();

        int count=1;

        StringBuffer query=new StringBuffer("SELECT GWT.name as Name,GWT.pgtypeid as PGTYPEID,GWT.templatename as TEMPLATENAME FROM merchant_bankmapping as MBM JOIN members as MEM ON MEM.memberid=MBM.memberid JOIN partners as PSP ON MEM.partnerId =PSP.partnerId JOIN gateway_type as GWT  ON GWT.pgtypeid=MBM.pgtypeid where MBM.mappingid>0");
        try
        {
            con = Database.getConnection();
            if(functions.isValueNull(memberId))
            {
                query.append(" and MBM.memberid =?");
            }

            if(functions.isValueNull(partnerId))
            {
                query.append(" and PSP.partnerId =?");
            }


            preparedStatement= con.prepareStatement(query.toString());


            if(functions.isValueNull(memberId))
            {
                preparedStatement.setString(count,memberId);
                count++;
            }

            if(functions.isValueNull(partnerId))
            {
                preparedStatement.setString(count,partnerId);
                count++;
            }

            resultSet=preparedStatement.executeQuery();

            while(resultSet.next())
            {
                GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();
                GatewayType gatewayType =new GatewayType();

                gatewayTypeVO.setPgTYypeId(resultSet.getString("PGTYPEID"));

                gatewayType.setName(resultSet.getString("Name"));
                gatewayType.setTemplatename(resultSet.getString("TEMPLATENAME"));

                gatewayTypeVO.setGatewayType(gatewayType);

                gatewayTypeVOs.put(gatewayTypeVO.getPgTYypeId(),gatewayTypeVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return gatewayTypeVOs;
    }*/

 /**
  * Delete Mapping for member
  * @param memberId
  * @return
  * @throws PZDBViolationException
  */
 public boolean deleteBankMerchantMappingForApplication(String memberId) throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String query=null;
  int result;
  try
  {
   conn = Database.getConnection();
   query = "DELETE FROM merchant_bankmapping WHERE memberid=?";
   pstmt= conn.prepareStatement(query);

   pstmt.setString(1, memberId);

   result=pstmt.executeUpdate();

   if(result>0)
   {
    return true;
   }
  }
  catch (SystemError systemError)
  {
   logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",systemError);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
  }
  catch (SQLException e)
  {
   logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",e);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
  }
  finally
  {
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(conn);
  }
  return false;
 }

 public Map<String,List<BankTypeVO>> getBankMemberMappingWithGatewayMap(String memberId,PaginationVO paginationVO) throws PZDBViolationException
 {
  Connection con=null;
  PreparedStatement preparedStatement=null, psCountOfBusinessProfile=null;
  ResultSet resultSet=null;
  ResultSet resultSetCount=null;

  Map<String,List<BankTypeVO>> bankTypeVOs = new HashMap<String, List<BankTypeVO>>();
  List<BankTypeVO> bankTypeVOList = null;
  StringBuffer query=new StringBuffer("SELECT  MBM.`mappingid` AS MAPPING,PBM.`defaultApplication` AS isDefaultApplication ,PAR.partnerId AS PARTNERID,MEM.memberid AS MEMBERID,MEM.company_name AS COMPANYNAME,BTM.bank_name AS NAME,BTM.bank_id AS PGTYPEID,BTM.template_name AS TEMPLATENAME FROM (partner_bank_mapping AS PBM JOIN partners AS PAR ON PAR.partnerId=PBM.partner_id JOIN members AS MEM ON MEM.partnerId=PAR.partnerId JOIN bank_template_mapping AS BTM ON BTM.bank_id=PBM.bank_id) LEFT JOIN `merchant_bankmapping` AS MBM ON MBM.`pgtypeid`=PBM.`bank_id` AND MBM.`memberid`=MEM.`memberid`  WHERE  BTM.template_name IS NOT NULL ");
  String countQuery=null;
  try
  {
   //con = Database.getConnection();
   con = Database.getRDBConnection();
   if(functions.isValueNull(memberId))
   {
    query.append(" and MEM.memberid =?");
   }

   countQuery="Select Count(*) from ("+query.toString()+" GROUP BY MEMBERID) as temp  ";

   query.append(" order by PARTNERID,MEMBERID,MAPPING DESC,isDefaultApplication ");

   if(paginationVO!=null)
    query.append("  limit "+paginationVO.getStart()+","+paginationVO.getEnd());


   logger.debug("QUERY MAIN::::"+query.toString());

   preparedStatement= con.prepareStatement(query.toString());


   if(functions.isValueNull(memberId))
   {
    preparedStatement.setString(1,memberId);
   }
   logger.debug("preparedStatement-->"+preparedStatement);
   resultSet=preparedStatement.executeQuery();

   while(resultSet.next())
   {
    BankTypeVO bankTypeVO= new BankTypeVO();

    bankTypeVO.setBankId(resultSet.getString("PGTYPEID"));
    bankTypeVO.setDefaultApplication("Y".equals(resultSet.getString("isDefaultApplication")));
    bankTypeVO.setMappedForApplication(functions.isValueNull(resultSet.getString("MAPPING")));
    bankTypeVO.setBankName(resultSet.getString("Name"));
    bankTypeVO.setFileName(resultSet.getString("TEMPLATENAME"));

    logger.debug("gatewayName::::"+bankTypeVO.getBankName()+" isDefault:::"+bankTypeVO.isDefaultApplication());

    if (bankTypeVOs.containsKey(resultSet.getString("MEMBERID")))
    {
     bankTypeVOList = bankTypeVOs.get(resultSet.getString("MEMBERID"));
     logger.debug("1st CONDITION:::"+((bankTypeVOList.get(bankTypeVOList.size()-1).isMappedForApplication()  && bankTypeVO.isMappedForApplication()) || (!bankTypeVOList.get(0).isMappedForApplication() && !bankTypeVOList.get(0).isDefaultApplication())));
     logger.debug("2nd CONDITION:::"+((bankTypeVOList.get(bankTypeVOList.size()-1).isDefaultApplication() && !bankTypeVOList.get(bankTypeVOList.size()-1).isMappedForApplication() && bankTypeVO.isDefaultApplication()) || (!bankTypeVOList.get(0).isDefaultApplication() && !bankTypeVOList.get(0).isMappedForApplication())));
     if((((bankTypeVOList.get(bankTypeVOList.size()-1).isMappedForApplication()  && bankTypeVO.isMappedForApplication()) || (!bankTypeVOList.get(0).isMappedForApplication() && !bankTypeVOList.get(0).isDefaultApplication()))) || ((bankTypeVOList.get(bankTypeVOList.size()-1).isDefaultApplication() && !bankTypeVOList.get(bankTypeVOList.size()-1).isMappedForApplication() && bankTypeVO.isDefaultApplication()) || (!bankTypeVOList.get(0).isDefaultApplication() && !bankTypeVOList.get(0).isMappedForApplication())))
     {
      logger.debug("INSIDE gatewayName::::"+bankTypeVO.getBankName()+" isDefault:::"+bankTypeVO.isDefaultApplication());
      bankTypeVOList.add(bankTypeVO);
     }
    }
    else
    {
     bankTypeVOList=new ArrayList<BankTypeVO>();
     bankTypeVOList.add(bankTypeVO);
    }
    bankTypeVOs.put(resultSet.getString("MEMBERID"), bankTypeVOList);

   }

   logger.debug("MEMBER GATEWAY MAPPING:::"+bankTypeVOs);

   if(paginationVO!=null)
   {
    psCountOfBusinessProfile = con.prepareStatement(countQuery);
    if(functions.isValueNull(memberId))
    {
     psCountOfBusinessProfile.setString(1,memberId);
    }
    resultSetCount = psCountOfBusinessProfile.executeQuery();
    if (resultSetCount.next())
    {
     paginationVO.setTotalRecords(resultSetCount.getInt(1));
    }
   }
  }
  catch (SystemError systemError)
  {
   logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",systemError);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
  }
  catch (SQLException e)
  {
   logger.error("Leaving insertBankMerchantMappingForApplication throwing SQL Exception as System Error :::: ",e);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(),"insertBankMerchantMappingForApplication()",null,"Common","System error while connecting to  table ",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closeResultSet(resultSetCount);
   Database.closePreparedStatement(preparedStatement);
   Database.closePreparedStatement(psCountOfBusinessProfile);
   Database.closeConnection(con);
  }
  return bankTypeVOs;
 }


    /*update Consildated Application table consolidated ID wise*/

 //sagar changes in Dao
 public boolean updateconsolidated_application(ConsolidatedApplicationVO consolidatedApplicationVO,String consolidatedId,String memberId)   //Sagar
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  int resultSet=0;
  List<ConsolidatedApplicationVO> consolidatedApplicationVOList=new ArrayList<ConsolidatedApplicationVO>();
  int counter=1;
  boolean setStarted=false;

  try
  {
   logger.error("update query for updateBankApplicationMasterVO");
   StringBuffer query =new StringBuffer("update consolidated_application set ");
   if(consolidatedApplicationVO!=null)
   {


    if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" filename =?");

     setStarted=true;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" status =?");
     logger.error("consolidated Status-----"+consolidatedApplicationVO.getStatus());

     setStarted=true;
    }


    if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" dtstamp =?");
     setStarted=true;
    }

   }
   query.append(" where consolidated_id >0");
   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and consolidated_id =?");
    logger.error("consolidatedId test---"+consolidatedId);
   }
   if(functions.isValueNull(memberId))
   {
    query.append(" and member_id =?");
    logger.debug("member_id test-------"+memberId);
   }

   logger.error("Consolidated query"+query);
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   if(consolidatedApplicationVO!=null)
   {

    if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getFilename() );
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getStatus() );
     logger.debug("getStatus........"+consolidatedApplicationVO.getStatus());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(consolidatedId))
    {
     pstmt.setString(counter,consolidatedId);
     counter++;
    }
    if(functions.isValueNull(memberId))
    {
     pstmt.setString(counter,memberId);
     counter++;
    }
   }

   resultSet=pstmt.executeUpdate();
   if(resultSet>0)
   {
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return false;

 }

 public boolean updateconsolidated_applicationforConsolidatedID(ConsolidatedApplicationVO consolidatedApplicationVO)   //Sagar
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  int resultSet=0;
  List<ConsolidatedApplicationVO> consolidatedApplicationVOList=new ArrayList<ConsolidatedApplicationVO>();
  int counter=1;
  boolean setStarted=false;

  if(consolidatedApplicationVO==null)
  {
   return false;
  }

  try
  {
   logger.error("update query for updateBankApplicationMasterVO");
   StringBuffer query =new StringBuffer("update consolidated_application set ");


   if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
   {
    if(setStarted)
    {
     query.append(",");
    }
    query.append(" filename =?");

    setStarted=true;
   }

   if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
   {
    if(setStarted)
    {
     query.append(",");
    }
    query.append(" status =?");
    setStarted=true;
   }


   if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
   {
    if(setStarted)
    {
     query.append(",");
    }
    query.append(" dtstamp =?");
    setStarted=true;
   }

   if(functions.isValueNull(consolidatedApplicationVO.getConsolidated_id()))
   {
    query.append(" where consolidated_id =?");

   }





   logger.error("Consolidated query"+query);
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());


   if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
   {
    pstmt.setString(counter,consolidatedApplicationVO.getFilename() );
    counter++;
   }

   if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
   {
    pstmt.setString(counter,consolidatedApplicationVO.getStatus() );
    logger.debug("getStatus........"+consolidatedApplicationVO.getStatus());
    counter++;
   }

   if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
   {
    pstmt.setString(counter,consolidatedApplicationVO.getDtstamp() );
    counter++;
   }

   if(functions.isValueNull(consolidatedApplicationVO.getConsolidated_id()))
   {
    pstmt.setString(counter,consolidatedApplicationVO.getConsolidated_id());
    counter++;
   }



   resultSet=pstmt.executeUpdate();
   if(resultSet>0)
   {
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Bank Application Master",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Bank Application Master",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return false;

 }

 //update consolidated Application history status
 public boolean updateconsolidated_applicationHistoryStatus(ConsolidatedApplicationVO consolidatedApplicationVO,String consolidatedId,String memberId)   //Sagar
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  int resultSet=0;
  List<ConsolidatedApplicationVO> consolidatedApplicationVOList=new ArrayList<ConsolidatedApplicationVO>();
  int counter=1;
  boolean setStarted=false;

  try
  {
   logger.error("update query for updateconsolidated_applicationHistoryStatus");
   StringBuffer query =new StringBuffer("update consolidated_application_history set ");
   if(consolidatedApplicationVO!=null)
   {


    if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" filename =?");

     setStarted=true;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" status =?");
     logger.error("consolidated Status-----"+consolidatedApplicationVO.getStatus());

     setStarted=true;
    }


    if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
    {
     if(setStarted)
     {
      query.append(",");
     }
     query.append(" dtstamp =?");
     setStarted=true;
    }

   }
   query.append(" where consolidated_id >0");
   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and consolidated_id =?");
    logger.error("consolidatedId test---"+consolidatedId);
   }
   if(functions.isValueNull(memberId))
   {
    query.append(" and member_id =?");
    logger.debug("member_id test-------"+memberId);
   }

   logger.error("Consolidated query"+query);
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());

   if(consolidatedApplicationVO!=null)
   {

    if(functions.isValueNull(consolidatedApplicationVO.getFilename()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getFilename() );
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getStatus()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getStatus() );
     logger.debug("getStatus........"+consolidatedApplicationVO.getStatus());
     counter++;
    }

    if(functions.isValueNull(consolidatedApplicationVO.getDtstamp()))
    {
     pstmt.setString(counter,consolidatedApplicationVO.getDtstamp() );
     counter++;
    }

    if(functions.isValueNull(consolidatedId))
    {
     pstmt.setString(counter,consolidatedId);
     counter++;
    }
    if(functions.isValueNull(memberId))
    {
     pstmt.setString(counter,memberId);
     counter++;
    }
   }

   resultSet=pstmt.executeUpdate();
   if(resultSet>0)
   {
    return true;
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for updateconsolidated application History Status",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for updateconsolidated application History Status",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return false;

 }

 public Map<String,ConsolidatedApplicationVO> getconsolidated_applicationHistoryIDpgtypeID(String memberId,String name,String consolidatedId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  try
  {

   StringBuffer query =new StringBuffer("SELECT CA.consolidated_id,CA.member_id,CA.pgtypeid,CA.bankapplicationid,CA.status,CA.filename,CA.dtstamp,CA.timestamp,BTM.bank_name FROM consolidated_application_history AS CA JOIN bank_template_mapping AS BTM ON BTM.bank_id = CA.pgtypeid WHERE CA.consolidated_id>0");

   if(functions.isValueNull(memberId))
   {
    query.append(" and CA.member_id =?");
   }

   if(functions.isValueNull(name))
   {
    query.append(" and BTM.bank_name =?");
   }

   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and CA.consolidated_id =?");
   }
   query.append(" ORDER BY CA.member_id");
   con=Database.getConnection();
   pstmt=con.prepareStatement(query.toString());


   if(functions.isValueNull(memberId))
   {
    pstmt.setString(counter,memberId);
    counter++;
   }
   if(functions.isValueNull(name))
   {
    pstmt.setString(counter,name);
    counter++;
   }
   if(functions.isValueNull(consolidatedId))
   {
    pstmt.setString(counter,consolidatedId);
    counter++;
   }

   //System.out.println("query for history : "+pstmt.toString());
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO=new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setGatewayname(resultSet.getString("bank_name"));
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    //System.out.println(resultSet.getString("consolidated_id")+"==========>");

    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(),consolidatedApplicationVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;

 }

 public boolean deleteConsolidated_Application_History(String consolidated_id)
 {
  Connection connection=null;
  try
  {
   connection = Database.getConnection();
   String deleteQuery = "DELETE FROM consolidated_application_history WHERE consolidated_id=?";
   PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
   preparedStatement.setString(1,consolidated_id);
   int delete=preparedStatement.executeUpdate();

   if(delete>0)
   {
    return true;
   }

  }
  catch (SystemError se)
  {
   logger.debug("Exception----------" + se);
  }
  catch (SQLException e)
  {
   logger.debug("Exception................." + e);
  }
  finally {
   Database.closeConnection(connection);
  }
  return  false;
 }

 //End
 public boolean isApplicationExistForMember(String memberId)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  StringBuffer query = new StringBuffer();

  boolean isMemberExist = false;
  try
  {
   conn = Database.getConnection();
   query.append("SELECT application_id FROM application_manager WHERE member_id = ?");
   pstmt = conn.prepareStatement(query.toString());
   pstmt.setString(1, memberId);
   ResultSet resultSet = pstmt.executeQuery();
   if (resultSet.next())
   {
    isMemberExist = true;
   }
  }

  catch (SQLException e)
  {
   isMemberExist = false;
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   isMemberExist = false;
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return isMemberExist;
 }

 public List<String> getcurrencyCode()
 {
  Connection con = null;
  ResultSet rs=null;
  String currency = "";
  List<String> currencyList = new ArrayList<String>();

  try
  {
   //con = Database.getConnection();
   con=Database.getConnection();
   rs = Database.executeQuery("select * from currency_code",con);

   while(rs.next())
   {
    currency = rs.getString("currency");
    currencyList.add(currency);
   }
  }
  catch (SystemError e)
  {
   logger.error("Error while fetching data " + e.getMessage());
  }
  catch (SQLException se)
  {
   logger.error("Error while fetching data " + se.getMessage());
  }
  finally
  {
   Database.closeResultSet(rs);
   Database.closeConnection(con);
  }
  return currencyList;
 }

 public ContractualPartnerVO getContractualPartnerDetails(String memberId, String bankName)
 {
  ContractualPartnerVO contractualPartnerVO = null;
  Connection conn = null;
  PreparedStatement pstmt = null;
  StringBuffer query = new StringBuffer();

  try
  {
   conn = Database.getConnection();
   query.append("SELECT cp.contractualid,cp.partnerid,cp.bankname,cp.contractual_partnerid,cp.contractual_partnername FROM members AS m JOIN contractualpartner_appmanager AS cp ON m.partnerid=cp.partnerid WHERE m.memberid=? AND cp.bankname=?");
   pstmt = conn.prepareStatement(query.toString());
   pstmt.setString(1, memberId);
   pstmt.setString(2, bankName);
   ResultSet resultSet = pstmt.executeQuery();
   if (resultSet.next())
   {
    contractualPartnerVO = new ContractualPartnerVO();
    contractualPartnerVO.setContractual_partnerid(resultSet.getString("contractualid"));
    contractualPartnerVO.setContractual_partnername(resultSet.getString("contractual_partnername"));
   }
  }

  catch (SQLException e)
  {

   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return contractualPartnerVO;
 }

 public List<BankTypeVO> getAllGatewayForPartner(String partnerId) throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement preparedStatement=null;
  ResultSet resultSet=null;

  List<BankTypeVO> bankTypeVOArrayList = new ArrayList<BankTypeVO>();
  try
  {
   //conn = Database.getConnection();
   conn = Database.getRDBConnection();
   String updateQuery="SELECT PBM.bank_id AS pgtypeid,PBM.defaultApplication AS defaultApplication, BTM.bank_name FROM partner_bank_mapping AS PBM JOIN bank_template_mapping AS BTM ON BTM.bank_id=PBM.bank_id WHERE PBM.partner_id =? AND BTM.template_name IS NOT NULL ORDER BY pgtypeid";
   preparedStatement=conn.prepareStatement(updateQuery);
   preparedStatement.setString(1,partnerId);

   logger.debug("preparedStatement:::"+preparedStatement);
   resultSet=preparedStatement.executeQuery();

   while(resultSet.next())
   {
    BankTypeVO bankTypeVO = new BankTypeVO();

    bankTypeVO.setBankName(resultSet.getString("bank_name"));
    bankTypeVO.setBankId(resultSet.getString("pgtypeid"));
    bankTypeVO.setDefaultApplication("Y".equals(resultSet.getString("defaultApplication"))?true:false);

    bankTypeVOArrayList.add(bankTypeVO);
   }
  }
  catch (SQLException e)
  {
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "getAllGatewayForPartner()", null, "common", "SQL Exception:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
  }
  catch (SystemError systemError)
  {
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "getAllGatewayForPartner()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
  }
  catch (Exception e)
  {
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "getAllGatewayForPartner()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(preparedStatement);
   Database.closeConnection(conn);
  }
  return bankTypeVOArrayList;
 }

 public List<String> getPartnerBankDetail(String partnerid)
 {
  List<String> memberid = new ArrayList<String>();
  Connection con=null;
  try
  {
   con=Database.getRDBConnection();
   String qry="SELECT BTM.bank_name FROM bank_template_mapping AS BTM JOIN partner_bank_mapping AS PBM ON PBM.bank_id=BTM.bank_id WHERE partner_id=? ORDER BY bank_name ASC";
   PreparedStatement pstmt= con.prepareStatement(qry);
   pstmt.setString(1,partnerid);
   ResultSet rs = pstmt.executeQuery();
   logger.debug("Query getPartnerBankDetail--->"+pstmt);
   while(rs.next())
   {
    memberid.add(rs.getString("bank_name"));
   }
  }
  catch(Exception e)
  {
   logger.error("error", e);
  }
  finally {
   Database.closeConnection(con);
  }
  return memberid;
 }

 public TreeMap<Integer, String> getpartnerDetails()
 {
  TreeMap<Integer, String> partners = new TreeMap<Integer, String>();
  Connection con = null;
  try
  {
   con = Database.getRDBConnection();
   String qry = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
   PreparedStatement pstmt = con.prepareStatement(qry);
   //pstmt.setString(1,partnerid);
   ResultSet rs = pstmt.executeQuery();
   while (rs.next())
   {
    partners.put(rs.getInt("partnerId"), /*rs.getString("partnerId") + "-" + */rs.getString("partnerName"));
   }
  }
  catch (Exception e)
  {
   logger.error("error", e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return partners;
 }

 public List<String> getListOfBankId()
 {
  List<String> bankMappingVOList = new ArrayList<String>();
  Connection con = null;
  PreparedStatement ps = null;
  ResultSet rs = null;

  try
  {
   con = Database.getConnection();
   String query = "SELECT bank_id, bank_name FROM bank_template_mapping WHERE template_name IS NOT NULL ORDER BY bank_id";
   ps = con.prepareStatement(query);
   rs = ps.executeQuery();
   while (rs.next())
   {
    String bankId = String.valueOf(rs.getInt("bank_id"));
    String bankName = rs.getString("bank_name");
    bankMappingVOList.add(bankId +" - "+ bankName);
   }
  }
  catch(Exception e)
  {
   logger.error("Exception while loading bank details",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return bankMappingVOList;
 }

 public boolean updateDefaultApplicationGatewayForPartnerAndPgTypeId(String partnerId,String pgTypeId,boolean defaultApplication) throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement preparedStatement=null;
  int resultSet=0;

  //List<GatewayTypeVO> gatewayTypeVOs = new ArrayList<GatewayTypeVO>();
  try
  {
   conn = Database.getConnection();
   StringBuffer updateQuery=new StringBuffer("update partner_bank_mapping set defaultApplication=? where partner_id =? ");

   if(functions.isValueNull(pgTypeId))
   {
    updateQuery.append(" and bank_id=? ");
   }

   preparedStatement=conn.prepareStatement(updateQuery.toString());
   preparedStatement.setString(1,(defaultApplication?"Y":"N"));
   preparedStatement.setString(2,partnerId);
   if(functions.isValueNull(pgTypeId))
   {
    preparedStatement.setString(3, pgTypeId);
   }
   resultSet=preparedStatement.executeUpdate();

   while(resultSet>0)
   {
    return true;
   }
  }
  catch (SQLException e)
  {
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "updateDefaultApplicationGatewayForPartnerAndPgTypeId()", null, "common", "SQL Exception:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
  }
  catch (SystemError systemError)
  {
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "updateDefaultApplicationGatewayForPartnerAndPgTypeId()", null, "common", "System Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
  }
  finally
  {
   Database.closePreparedStatement(preparedStatement);
   Database.closeConnection(conn);
  }
  return false;
 }

 public boolean addBankTemplate(String bankName, String fileName) throws SystemError
 {
  logger.debug("inside addBankTemplate---");
  Connection conn = null;
  boolean status = false;
  try
  {
   conn = Database.getConnection();
   String selquery = "insert into bank_template_mapping(bank_name, template_name) values (?,?)";
   PreparedStatement pstmt= conn.prepareStatement(selquery);
   pstmt.setString(1,bankName);
   pstmt.setString(2,fileName);
   int i = pstmt.executeUpdate();
   logger.debug("pstmt--->"+pstmt);
   if(i > 0)
   {
    status = true;
   }
  }
  catch (SystemError se)
  {
   logger.error(" SystemError in isMember method: ", se);
   throw new SystemError("Error: " + se.getMessage());
  }
  catch (Exception e)
  {
   logger.error("Exception in isMember method: ", e);
   throw new SystemError("Error: " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return status;
 }

 public boolean updateBankTemplate(String bankName, String fileName) throws SystemError
 {
  logger.debug("inside updateBankTemplate---");
  Connection conn = null;
  boolean status = false;
  try
  {
   conn = Database.getConnection();
   String selquery = "UPDATE bank_template_mapping SET template_name = ? WHERE bank_name = ?";
   PreparedStatement pstmt= conn.prepareStatement(selquery);
   pstmt.setString(1,fileName);
   pstmt.setString(2,bankName);
   int i = pstmt.executeUpdate();
   logger.debug("pstmt--->"+pstmt);
   if(i > 0)
   {
    status = true;
   }
  }
  catch (SystemError se)
  {
   logger.error(" SystemError in isMember method: ", se);
   throw new SystemError("Error: " + se.getMessage());
  }
  catch (Exception e)
  {
   logger.error("Exception in isMember method: ", e);
   throw new SystemError("Error: " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return status;
 }

 public boolean isBankExist(String bankName) throws SystemError
 {
  logger.debug("inside isBankExist---"+bankName);
  Connection conn = null;
  boolean status = false;
  ResultSet rs = null;
  try
  {
   conn = Database.getConnection();
   String selquery = "SELECT * FROM bank_template_mapping WHERE bank_name=?";
   PreparedStatement pstmt= conn.prepareStatement(selquery);
   pstmt.setString(1,bankName);
   rs = pstmt.executeQuery();
   logger.debug("pstmt--->"+pstmt);
   if(rs.next())
   {
    status = true;
   }
  }
  catch (SystemError se)
  {
   logger.error(" SystemError in isMember method: ", se);
   throw new SystemError("Error: " + se.getMessage());
  }
  catch (Exception e)
  {
   logger.error("Exception in isMember method: ", e);
   throw new SystemError("Error: " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return status;
 }

 public BankTypeVO getBankTemplateDetails(String bankName) throws SystemError
 {
  logger.debug("inside isBankExist---"+bankName);
  Connection conn = null;
  BankTypeVO bankTypeVO = new BankTypeVO();
  ResultSet rs = null;
  try
  {
   conn = Database.getConnection();
   String selquery = "SELECT * FROM bank_template_mapping WHERE bank_name=?";
   PreparedStatement pstmt= conn.prepareStatement(selquery);
   pstmt.setString(1,bankName);
   rs = pstmt.executeQuery();
   logger.debug("pstmt--->"+pstmt);
   if(rs.next())
   {
    bankTypeVO.setFileName(rs.getString("template_name"));
    bankTypeVO.setBankId(rs.getString("bank_id"));
   }
  }
  catch (SystemError se)
  {
   logger.error(" SystemError in isMember method: ", se);
   throw new SystemError("Error: " + se.getMessage());
  }
  catch (Exception e)
  {
   logger.error("Exception in isMember method: ", e);
   throw new SystemError("Error: " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return bankTypeVO;
 }

 public TreeMap<String, String> selectPartnerIdAndPartnerName() throws PZDBViolationException
 {
  Connection con = null;
  PreparedStatement pstm = null;
  String query = "";
  ResultSet rs = null;

  TreeMap<String,String> partnerMap = new TreeMap<String, String>();

  try
  {
   con = Database.getConnection();
   query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
   pstm = con.prepareStatement(query);
   rs = pstm.executeQuery();
   while (rs.next())
   {
    partnerMap.put(rs.getString("partnerId"),rs.getString("partnerName"));
   }

  }
  catch (SystemError systemError)
  {
   logger.error("System Error :::: ",systemError);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "selectPartnerId()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
  }
  catch (SQLException e)
  {
   logger.error("System Error :::: ",e);
   PZExceptionHandler.raiseDBViolationException(ApplicationManagerDAO.class.getName(), "selectPartnerId()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
  }
  finally
  {
   Database.closeConnection(con);
  }

  return partnerMap;
 }

 public boolean addPartnerBankMapping(String bankId, String partnerId) throws SystemError
 {
  Connection conn = null;
  boolean status = false;
  try
  {
   conn = Database.getConnection();
   String insquery = "insert into partner_bank_mapping(bank_id, partner_id) values (?,?)";
   PreparedStatement pstmt = conn.prepareStatement(insquery);
   pstmt.setString(1,bankId);
   pstmt.setString(2,partnerId);
   int i = pstmt.executeUpdate();
   logger.debug("pstmt--->"+pstmt);
   if(i > 0)
   {
    status = true;
   }
  }
  catch (SystemError se)
  {
   logger.error(" SystemError in addPartnerBankMapping method: ", se);
   throw new SystemError("Error: " + se.getMessage());
  }
  catch (Exception e)
  {
   logger.error("Exception in addPartnerBankMapping method: ", e);
   throw new SystemError("Error: " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return status;
 }

 public boolean isPartnerBankMappingExist(String bankId, String partnerId) throws SystemError
 {
  Connection conn = null;
  boolean status = false;
  ResultSet resultSet = null;
  try
  {
   conn = Database.getConnection();
   String selectQuery = "SELECT * FROM partner_bank_mapping WHERE bank_id=? AND partner_id=? ";
   PreparedStatement pstmt = conn.prepareStatement(selectQuery);
   pstmt.setString(1,bankId);
   pstmt.setString(2,partnerId);
   resultSet = pstmt.executeQuery();
   logger.debug("pstmt--->"+pstmt);
   if(resultSet.next())
   {
    status = true;
   }
  }
  catch (SystemError se)
  {
   logger.error(" SystemError in addPartnerBankMapping method: ", se);
   throw new SystemError("Error: " + se.getMessage());
  }
  catch (Exception e)
  {
   logger.error("Exception in addPartnerBankMapping method: ", e);
   throw new SystemError("Error: " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  return status;
 }

 public BankTypeVO getBankTypeForBankId(String bankId)
 {
  BankTypeVO bankTypeVO = null;
  Connection conn = null;
  ResultSet rs = null;
  try
  {
   conn = Database.getConnection();
   StringBuffer selectQuery = new StringBuffer("SELECT * FROM bank_template_mapping WHERE bank_id=? ");
   PreparedStatement pstmt = conn.prepareStatement(selectQuery.toString());
   pstmt.setString(1,bankId);
   rs = pstmt.executeQuery();
   logger.debug("pstmt--->"+pstmt);
   if(rs.next())
   {
    bankTypeVO = new BankTypeVO();
    bankTypeVO.setBankId(rs.getString("bank_id"));
    bankTypeVO.setBankName(rs.getString("bank_name"));
    bankTypeVO.setFileName(rs.getString("template_name"));
   }
  }
  catch (SQLException e)
  {
   logger.error("SQLException--->",e);
  }
  catch (SystemError systemError)
  {
   logger.error("SystemError--->", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }

  return bankTypeVO;
 }

 public String getAppManagerStatus(String memberId) throws SystemError, Exception
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  ResultSet rs = null;
  String status = "";
  try
  {
   conn = Database.getConnection();
   //String updateappManagerStatus = "update application_manager set status=?,userApp=?,appliedToModify=?,MAF_status =?,KYC_status=? where member_id=? ";
   String getStatus = "SELECT status FROM application_manager WHERE member_id=?";
   pstmt = conn.prepareStatement(getStatus);
   pstmt.setString(1, memberId);
   rs = pstmt.executeQuery();

   if (rs.next())
   {
    status = rs.getString("status");
   }

  }
  catch (SQLException e)
  {
   logger.error("Leaving ApplicationManagerDAO throwing SQL Exception as System Error :::: ", e);
   throw new SystemError("Error : " + e.getMessage());
  }
  finally
  {
   Database.closeConnection(conn);
  }
  logger.debug("Leaving getAppManagerStatus");

  return status;
 }
 public boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
 {
  File f = new File(filepath);
  int length = 0;

  // Set browser download related headers
  response.setContentType("application/octat-stream");
  response.setContentLength((int) f.length());
  response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

  javax.servlet.ServletOutputStream op = response.getOutputStream();

  byte[] bbuf = new byte[1024];
  DataInputStream in = new DataInputStream(new FileInputStream(f));

  while ((in != null) && ((length = in.read(bbuf)) != -1))
  {
   op.write(bbuf, 0, length);
  }

  in.close();
  op.flush();
  op.close();

  // file must be deleted after transfer...
  // caution: select to download only files which are temporarily created zip files
  // do not call this servlets with any other files which may be required later on.
        /*File file = new File(filepath);
        file.delete();*/
  logger.info("Successful#######");
  return true;
 }

 public Map<String, AddressIdentificationVO> populateCompanyDetails(String applicationId)
 {
  Connection con = null;
  PreparedStatement psPopulateApplicationData = null;
  ResultSet rsPopulateApplicationData = null;
  Map<String, AddressIdentificationVO> stringCompanyProfile_addressVOMap = new HashedMap();
  try
  {
   logger.debug("Populating application data for AddressIdentification---->");
   con = Database.getConnection();
   String query = "SELECT * FROM appmanager_address_identfication WHERE application_id=?";

   psPopulateApplicationData = con.prepareStatement(query.toString());
   psPopulateApplicationData.setString(1, applicationId);
   rsPopulateApplicationData = psPopulateApplicationData.executeQuery();
   while (rsPopulateApplicationData.next())
   {
    AddressIdentificationVO addressIdentificationVO = new AddressIdentificationVO();
    addressIdentificationVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    addressIdentificationVO.setAddressId(rsPopulateApplicationData.getString("addressId"));
    addressIdentificationVO.setAddressProof(rsPopulateApplicationData.getString("addressproof"));
    addressIdentificationVO.setAddress(rsPopulateApplicationData.getString("address"));
    addressIdentificationVO.setStreet(rsPopulateApplicationData.getString("street"));
    addressIdentificationVO.setCity(rsPopulateApplicationData.getString("city"));
    addressIdentificationVO.setState(rsPopulateApplicationData.getString("state"));
    addressIdentificationVO.setCountry(rsPopulateApplicationData.getString("country"));
    addressIdentificationVO.setZipcode(rsPopulateApplicationData.getString("zipcode"));
    addressIdentificationVO.setVatidentification(rsPopulateApplicationData.getString("vatidentification"));
    addressIdentificationVO.setFederalTaxId(rsPopulateApplicationData.getString("federaltax_id"));
    addressIdentificationVO.setType(rsPopulateApplicationData.getString("type"));
    addressIdentificationVO.setCompany_name(rsPopulateApplicationData.getString("company_name"));
    addressIdentificationVO.setRegistration_number(rsPopulateApplicationData.getString("registration_number"));
    addressIdentificationVO.setDate_of_registration(rsPopulateApplicationData.getString("date_of_registration"));
    addressIdentificationVO.setPhone_cc(rsPopulateApplicationData.getString("phone_cc"));
    addressIdentificationVO.setPhone_number(rsPopulateApplicationData.getString("phone_number"));
    addressIdentificationVO.setFax(rsPopulateApplicationData.getString("fax"));
    addressIdentificationVO.setEmail_id(rsPopulateApplicationData.getString("email_id"));
    addressIdentificationVO.setRegistred_directors(rsPopulateApplicationData.getString("registred_directors"));

    stringCompanyProfile_addressVOMap.put(addressIdentificationVO.getType(), addressIdentificationVO);
   }

   if(stringCompanyProfile_addressVOMap.size()==0)
   {

    AddressIdentificationVO identificationVO_company = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_business = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_euCompany = new AddressIdentificationVO();

    stringCompanyProfile_addressVOMap.put(ApplicationManagerTypes.COMPANY,identificationVO_company);
    stringCompanyProfile_addressVOMap.put(ApplicationManagerTypes.BUSINESS, identificationVO_business);
    stringCompanyProfile_addressVOMap.put(ApplicationManagerTypes.EU_COMPANY, identificationVO_euCompany);

   }
  }
  catch (SQLException e)
  {
   logger.error("SQLException--->", e);
  }
  catch (SystemError systemError)
  {
   logger.error("SystemError--->", systemError);
  }
  finally
  {
   Database.closeConnection(con);
  }
  logger.debug("stringCompanyProfile_addressVOMap---"+stringCompanyProfile_addressVOMap);
  return stringCompanyProfile_addressVOMap;
 }

 public Map<String, ContactDetailsVO> populateContactInformation(String applicationId)
 {
  Connection con = null;
  PreparedStatement psPopulateApplicationData = null;
  ResultSet rsPopulateApplicationData = null;
  Map<String, ContactDetailsVO> contactDetailsVOMap = new HashedMap();
  try
  {
   logger.debug("Populating Contact Information from application---->");
   con = Database.getConnection();
   String query = "SELECT * FROM appmanager_contact_information WHERE application_id=?";

   psPopulateApplicationData = con.prepareStatement(query.toString());
   psPopulateApplicationData.setString(1, applicationId);
   rsPopulateApplicationData = psPopulateApplicationData.executeQuery();
   logger.debug("psPopulateApplicationData--"+psPopulateApplicationData);
   while (rsPopulateApplicationData.next())
   {
    ContactDetailsVO contactDetailsVO =  new ContactDetailsVO();
    contactDetailsVO.setApplicationId(rsPopulateApplicationData.getString("application_id"));
    contactDetailsVO.setName(rsPopulateApplicationData.getString("name"));
    contactDetailsVO.setEmailaddress(rsPopulateApplicationData.getString("emailaddress"));
    contactDetailsVO.setPhonecc1(rsPopulateApplicationData.getString("phonecc1"));
    contactDetailsVO.setSkypeIMaddress(rsPopulateApplicationData.getString("skypeaddress"));
    contactDetailsVO.setDesignation(rsPopulateApplicationData.getString("designation"));
    contactDetailsVO.setTelephonenumber(rsPopulateApplicationData.getString("telephonenumber"));
    contactDetailsVO.setType(rsPopulateApplicationData.getString("type"));

    contactDetailsVOMap.put(contactDetailsVO.getType(), contactDetailsVO);
   }
   if(contactDetailsVOMap.size()==0)
   {


    ContactDetailsVO contactDetailsVO_main = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_technical = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_billing = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_cbk = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_pci = new ContactDetailsVO();

    contactDetailsVOMap.put(ApplicationManagerTypes.MAIN, contactDetailsVO_main);
    contactDetailsVOMap.put(ApplicationManagerTypes.BILLING, contactDetailsVO_billing);
    contactDetailsVOMap.put(ApplicationManagerTypes.TECHNICAL, contactDetailsVO_technical);
    contactDetailsVOMap.put(ApplicationManagerTypes.CBK, contactDetailsVO_cbk);
    contactDetailsVOMap.put(ApplicationManagerTypes.PCI, contactDetailsVO_pci);

   }
  }
  catch (SQLException e)
  {
   logger.error("SQLException--->", e);
  }
  catch (SystemError systemError)
  {
   logger.error("SystemError--->", systemError);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return contactDetailsVOMap;
 }

 private void insertAddressAndIdentificationDetails(String applicationId, Map<String, AddressIdentificationVO> companyProfile_addressVOMap)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  int resultSet = 0;
  try
  {
   conn = Database.getConnection();
   Iterator it = companyProfile_addressVOMap.entrySet().iterator();

   while(it.hasNext())
   {
    Map.Entry pair = (Map.Entry)it.next();
    logger.debug("INSERTION OF ADDRESS DATA FOR----- "+pair.getKey());
    AddressIdentificationVO companyProfile_addressVO = (AddressIdentificationVO) pair.getValue();

    query = "INSERT INTO appmanager_address_identfication(application_id,type,addressId,addressproof,address,city,state,zipcode,country,street,vatidentification,federaltax_id,company_name,registration_number,date_of_registration,phone_cc,phone_number,fax,email_id,registred_directors)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    pstmt = conn.prepareStatement(query);

    pstmt.setString(1, applicationId);
    if (functions.isValueNull((String)pair.getKey()))
    {
     pstmt.setString(2, (String)pair.getKey());
    }
    else
    {
     pstmt.setNull(2, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getAddressId()))
    {
     pstmt.setString(3, companyProfile_addressVO.getAddressId());
    }
    else
    {
     pstmt.setNull(3, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getAddressProof()))
    {
     pstmt.setString(4, companyProfile_addressVO.getAddressProof());
    }
    else
    {
     pstmt.setNull(4, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getAddress()))
    {
     pstmt.setString(5, companyProfile_addressVO.getAddress());
    }
    else
    {
     pstmt.setNull(5, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getCity()))
    {
     pstmt.setString(6, companyProfile_addressVO.getCity());
    }
    else
    {
     pstmt.setNull(6, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getState()))
    {
     pstmt.setString(7, companyProfile_addressVO.getState());
    }
    else
    {
     pstmt.setNull(7, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getZipcode()))
    {
     pstmt.setString(8, companyProfile_addressVO.getZipcode());
    }
    else
    {
     pstmt.setNull(8, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getCountry()))
    {
     pstmt.setString(9, companyProfile_addressVO.getCountry());
    }
    else
    {
     pstmt.setNull(9, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getStreet()))
    {
     pstmt.setString(10, companyProfile_addressVO.getStreet());
    }
    else
    {
     pstmt.setNull(10, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getVatidentification()))
    {
     pstmt.setString(11, companyProfile_addressVO.getVatidentification());
    }
    else
    {
     pstmt.setNull(11, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getFederalTaxId()))
    {
     pstmt.setString(12, companyProfile_addressVO.getFederalTaxId());
    }
    else
    {
     pstmt.setNull(12, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getCompany_name()))
    {
     pstmt.setString(13, companyProfile_addressVO.getCompany_name());
    }
    else
    {
     pstmt.setNull(13, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getRegistration_number()))
    {
     pstmt.setString(14, companyProfile_addressVO.getRegistration_number());
    }
    else
    {
     pstmt.setNull(14, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getDate_of_registration()))
    {
     pstmt.setString(15, companyProfile_addressVO.getDate_of_registration());
    }
    else
    {
     pstmt.setNull(15, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getPhone_cc()))
    {
     pstmt.setString(16, companyProfile_addressVO.getPhone_cc());
    }
    else
    {
     pstmt.setNull(16, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getPhone_number()))
    {
     pstmt.setString(17, companyProfile_addressVO.getPhone_number());
    }
    else
    {
     pstmt.setNull(17, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getFax()))
    {
     pstmt.setString(18, companyProfile_addressVO.getFax());
    }
    else
    {
     pstmt.setNull(18, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getEmail_id()))
    {
     pstmt.setString(19, companyProfile_addressVO.getEmail_id());
    }
    else
    {
     pstmt.setNull(19, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getRegistred_directors()))
    {
     pstmt.setString(20, companyProfile_addressVO.getRegistred_directors());
    }
    else
    {
     pstmt.setNull(20, Types.VARCHAR);
    }
    resultSet = pstmt.executeUpdate();
   }
  }

  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
 }

 private void insertContactDetails(String applicationId, Map<String, ContactDetailsVO> companyProfile_contactInfoVOMap)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  int resultSet = 0;
  try
  {
   conn = Database.getConnection();
   Iterator it = companyProfile_contactInfoVOMap.entrySet().iterator();

   while(it.hasNext())
   {
    Map.Entry pair = (Map.Entry)it.next();
    ContactDetailsVO contactDetailsVO = (ContactDetailsVO)pair.getValue();

    query = "INSERT INTO appmanager_contact_information(application_id,type,name,emailaddress,phonecc1,telephonenumber,designation,skypeaddress)VALUES(?,?,?,?,?,?,?,?)";
    pstmt = conn.prepareStatement(query);

    pstmt.setString(1, applicationId);
    if (functions.isValueNull((String)pair.getKey()))
    {
     pstmt.setString(2, (String)pair.getKey());
    }
    else
    {
     pstmt.setNull(2, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getName()))
    {
     pstmt.setString(3, contactDetailsVO.getName());
    }
    else
    {
     pstmt.setNull(3, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getEmailaddress()))
    {
     pstmt.setString(4, contactDetailsVO.getEmailaddress());
    }
    else
    {
     pstmt.setNull(4, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getPhonecc1()))
    {
     pstmt.setString(5, contactDetailsVO.getPhonecc1());
    }
    else
    {
     pstmt.setNull(5, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getTelephonenumber()))
    {
     pstmt.setString(6, contactDetailsVO.getTelephonenumber());
    }
    else
    {
     pstmt.setNull(6, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getDesignation()))
    {
     pstmt.setString(7, contactDetailsVO.getDesignation());
    }
    else
    {
     pstmt.setNull(7, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getSkypeIMaddress()))
    {
     pstmt.setString(8, contactDetailsVO.getSkypeIMaddress());
    }
    else
    {
     pstmt.setNull(8, Types.VARCHAR);
    }
    resultSet = pstmt.executeUpdate();
   }
  }

  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
 }

 private void updateAddressAndIdentificationDetails(String applicationId, Map<String, AddressIdentificationVO> companyProfile_addressVOMap)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  int resultSet = 0;
  try
  {
   conn = Database.getConnection();
   logger.debug("companyProfile_addressVOMap--"+companyProfile_addressVOMap);
   Iterator it = companyProfile_addressVOMap.entrySet().iterator();

   while(it.hasNext())
   {
    Map.Entry pair = (Map.Entry)it.next();
    logger.debug("update for Type--->"+(String)pair.getKey());
    AddressIdentificationVO companyProfile_addressVO = (AddressIdentificationVO) pair.getValue();

    query = "update appmanager_address_identfication set addressId=?,addressproof=?,address=?,city=?,state=?,zipcode=?,country=?,street=?,vatidentification=?,federaltax_id=?,company_name=?,registration_number=?,date_of_registration=?,phone_cc=?,phone_number=?,fax=?,email_id=?,registred_directors=? where application_id=? AND type=?";
    pstmt = conn.prepareStatement(query);

    if (functions.isValueNull(companyProfile_addressVO.getAddressId()))
    {
     pstmt.setString(1, companyProfile_addressVO.getAddressId());
    }
    else
    {
     pstmt.setNull(1, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getAddressProof()))
    {
     pstmt.setString(2, companyProfile_addressVO.getAddressProof());
    }
    else
    {
     pstmt.setNull(2, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getAddress()))
    {
     pstmt.setString(3, companyProfile_addressVO.getAddress());
    }
    else
    {
     pstmt.setNull(3, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getCity()))
    {
     pstmt.setString(4, companyProfile_addressVO.getCity());
    }
    else
    {
     pstmt.setNull(4, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getState()))
    {
     pstmt.setString(5, companyProfile_addressVO.getState());
    }
    else
    {
     pstmt.setNull(5, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getZipcode()))
    {
     pstmt.setString(6, companyProfile_addressVO.getZipcode());
    }
    else
    {
     pstmt.setNull(6, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getCountry()))
    {
     pstmt.setString(7, companyProfile_addressVO.getCountry());
    }
    else
    {
     pstmt.setNull(7, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getStreet()))
    {
     pstmt.setString(8, companyProfile_addressVO.getStreet());
    }
    else
    {
     pstmt.setNull(8, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getVatidentification()))
    {
     pstmt.setString(9, companyProfile_addressVO.getVatidentification());
    }
    else
    {
     pstmt.setNull(9, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getFederalTaxId()))
    {
     pstmt.setString(10, companyProfile_addressVO.getFederalTaxId());
    }
    else
    {
     pstmt.setNull(10, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getCompany_name()))
    {
     pstmt.setString(11, companyProfile_addressVO.getCompany_name());
    }
    else
    {
     pstmt.setNull(11, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getRegistration_number()))
    {
     pstmt.setString(12, companyProfile_addressVO.getRegistration_number());
    }
    else
    {
     pstmt.setNull(12, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getDate_of_registration()))
    {
     pstmt.setString(13, companyProfile_addressVO.getDate_of_registration());
    }
    else
    {
     pstmt.setNull(13, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getPhone_cc()))
    {
     pstmt.setString(14, companyProfile_addressVO.getPhone_cc());
    }
    else
    {
     pstmt.setNull(14, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getPhone_number()))
    {
     pstmt.setString(15, companyProfile_addressVO.getPhone_number());
    }
    else
    {
     pstmt.setNull(15, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getFax()))
    {
     pstmt.setString(16, companyProfile_addressVO.getFax());
    }
    else
    {
     pstmt.setNull(16, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getEmail_id()))
    {
     pstmt.setString(17, companyProfile_addressVO.getEmail_id());
    }
    else
    {
     pstmt.setNull(17, Types.VARCHAR);
    }
    if (functions.isValueNull(companyProfile_addressVO.getRegistred_directors()))
    {
     pstmt.setString(18, companyProfile_addressVO.getRegistred_directors());
    }
    else
    {
     pstmt.setNull(18, Types.VARCHAR);
    }
    pstmt.setString(19, applicationId);
    pstmt.setString(20, (String)pair.getKey());

    logger.debug("Update appmanager_address_identfication---"+pstmt);

    int k = pstmt.executeUpdate();
   }
  }

  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
 }

 private void updateContactDetails(String application_id, Map<String, ContactDetailsVO> companyProfile_contactInfoVOMap)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  try
  {
   conn = Database.getConnection();
   Iterator it = companyProfile_contactInfoVOMap.entrySet().iterator();

   while(it.hasNext())
   {
    Map.Entry pair = (Map.Entry)it.next();
    ContactDetailsVO contactDetailsVO = (ContactDetailsVO)pair.getValue();

    query = "update appmanager_contact_information set name=?,emailaddress=?,phonecc1=?,telephonenumber=?,designation=?,skypeaddress=? where application_id=? and type=?";
    pstmt = conn.prepareStatement(query);

    if (functions.isValueNull(contactDetailsVO.getName()))
    {
     pstmt.setString(1, contactDetailsVO.getName());
    }
    else
    {
     pstmt.setNull(1, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getEmailaddress()))
    {
     pstmt.setString(2, contactDetailsVO.getEmailaddress());
    }
    else
    {
     pstmt.setNull(2, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getPhonecc1()))
    {
     pstmt.setString(3, contactDetailsVO.getPhonecc1());
    }
    else
    {
     pstmt.setNull(3, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getTelephonenumber()))
    {
     pstmt.setString(4, contactDetailsVO.getTelephonenumber());
    }
    else
    {
     pstmt.setNull(4, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getDesignation()))
    {
     pstmt.setString(5, contactDetailsVO.getDesignation());
    }
    else
    {
     pstmt.setNull(5, Types.VARCHAR);
    }
    if (functions.isValueNull(contactDetailsVO.getSkypeIMaddress()))
    {
     pstmt.setString(6, contactDetailsVO.getSkypeIMaddress());
    }
    else
    {
     pstmt.setNull(6, Types.VARCHAR);
    }
    pstmt.setString(7, application_id);
    pstmt.setString(8, (String)pair.getKey());
    logger.debug((String)pair.getKey()+ " Contact Details----");
    logger.debug("Update qry--"+pstmt);

    int k = pstmt.executeUpdate();
   }
  }

  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
 }

 public Map<String, OwnershipProfileDetailsVO> populateOwnershipProfileDetails(String applicationId)
 {
  Connection con = null;
  PreparedStatement psPopulateApplicationData = null;
  ResultSet rsPopulateApplicationData = null;
  Map<String, OwnershipProfileDetailsVO> stringOwnershipProfileDetailsVOMap = new HashedMap();
  try
  {
   con = Database.getConnection();
   String query = "SELECT * FROM ownershipprofile_details WHERE application_id=?";

   psPopulateApplicationData = con.prepareStatement(query.toString());
   psPopulateApplicationData.setString(1, applicationId);
   rsPopulateApplicationData = psPopulateApplicationData.executeQuery();
   while(rsPopulateApplicationData.next())
   {
    OwnershipProfileDetailsVO ownershipProfileDetailsVO =  new OwnershipProfileDetailsVO();
    ownershipProfileDetailsVO.setApplication_id(rsPopulateApplicationData.getString("application_id"));
    ownershipProfileDetailsVO.setAddressId(rsPopulateApplicationData.getString("addressId"));
    ownershipProfileDetailsVO.setAddressProof(rsPopulateApplicationData.getString("addressproof"));
    ownershipProfileDetailsVO.setAddress(rsPopulateApplicationData.getString("address"));
    ownershipProfileDetailsVO.setStreet(rsPopulateApplicationData.getString("street"));
    ownershipProfileDetailsVO.setCity(rsPopulateApplicationData.getString("city"));
    ownershipProfileDetailsVO.setState(rsPopulateApplicationData.getString("state"));
    ownershipProfileDetailsVO.setCountry(rsPopulateApplicationData.getString("country"));
    ownershipProfileDetailsVO.setZipcode(rsPopulateApplicationData.getString("zipcode"));
    ownershipProfileDetailsVO.setIdentificationtypeselect(rsPopulateApplicationData.getString("identificationtypeselect"));
    ownershipProfileDetailsVO.setIdentificationtype(rsPopulateApplicationData.getString("identificationtype"));
    ownershipProfileDetailsVO.setNationality(rsPopulateApplicationData.getString("nationality"));
    ownershipProfileDetailsVO.setPassportissuedate(rsPopulateApplicationData.getString("passportissuedate"));
    ownershipProfileDetailsVO.setPassportexpirydate(rsPopulateApplicationData.getString("passportexpirydate"));
    ownershipProfileDetailsVO.setTitle(rsPopulateApplicationData.getString("title"));
    ownershipProfileDetailsVO.setFirstname(rsPopulateApplicationData.getString("firstname"));
    ownershipProfileDetailsVO.setDateofbirth(rsPopulateApplicationData.getString("dateofbirth"));
    ownershipProfileDetailsVO.setTelnocc1(rsPopulateApplicationData.getString("telnocc1"));
    ownershipProfileDetailsVO.setEmailaddress(rsPopulateApplicationData.getString("emailaddress"));
    ownershipProfileDetailsVO.setTelephonenumber(rsPopulateApplicationData.getString("telephonenumber"));
    ownershipProfileDetailsVO.setOwned(rsPopulateApplicationData.getString("owned"));
    ownershipProfileDetailsVO.setPoliticallyexposed(rsPopulateApplicationData.getString("politicallyexposed"));
    ownershipProfileDetailsVO.setCriminalrecord(rsPopulateApplicationData.getString("criminalrecord"));
    ownershipProfileDetailsVO.setDesignation(rsPopulateApplicationData.getString("designation"));
    ownershipProfileDetailsVO.setType(rsPopulateApplicationData.getString("type"));
    ownershipProfileDetailsVO.setName(rsPopulateApplicationData.getString("name"));
    ownershipProfileDetailsVO.setRegistrationNumber(rsPopulateApplicationData.getString("registration_number"));
    ownershipProfileDetailsVO.setLastname(rsPopulateApplicationData.getString("lastname"));

    stringOwnershipProfileDetailsVOMap.put(ownershipProfileDetailsVO.getType(), ownershipProfileDetailsVO);
   }
   if(stringOwnershipProfileDetailsVOMap.size()==0)
   {


    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director4 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory4 = new OwnershipProfileDetailsVO();


    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER1,ownershipProfileDetailsVO_shareholder1);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER2,ownershipProfileDetailsVO_shareholder2);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER3,ownershipProfileDetailsVO_shareholder3);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER4,ownershipProfileDetailsVO_shareholder4);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER1,ownershipProfileDetailsVO_corporateShareholder1);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER2,ownershipProfileDetailsVO_corporateShareholder2);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER3,ownershipProfileDetailsVO_corporateShareholder3);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER4,ownershipProfileDetailsVO_corporateShareholder4);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR1,ownershipProfileDetailsVO_director1);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR2,ownershipProfileDetailsVO_director2);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR3,ownershipProfileDetailsVO_director3);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR4,ownershipProfileDetailsVO_director4);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY1,ownershipProfileDetailsVO_authorizeSignatory1);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY2,ownershipProfileDetailsVO_authorizeSignatory2);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY3,ownershipProfileDetailsVO_authorizeSignatory3);
    stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY4,ownershipProfileDetailsVO_authorizeSignatory4);

   }else
   {
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.SHAREHOLDER1)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER1,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.SHAREHOLDER2)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER2,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.SHAREHOLDER3)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER3,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.SHAREHOLDER4)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER4,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.CORPORATESHAREHOLDER1)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER1,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.CORPORATESHAREHOLDER2)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER2,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.CORPORATESHAREHOLDER3)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER3,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.CORPORATESHAREHOLDER4)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER4,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.DIRECTOR1)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR1,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.DIRECTOR2)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR2,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.DIRECTOR3)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR3,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.DIRECTOR4)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR4,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.AUTHORIZESIGNATORY1)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY1,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.AUTHORIZESIGNATORY2)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY2,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.AUTHORIZESIGNATORY3)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY3,new OwnershipProfileDetailsVO());
    if(stringOwnershipProfileDetailsVOMap.get(ApplicationManagerTypes.AUTHORIZESIGNATORY4)==null)
     stringOwnershipProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY4,new OwnershipProfileDetailsVO());
   }
  }
  catch (SQLException e)
  {
   logger.error("SQLException--->", e);
  }
  catch (SystemError systemError)
  {
   logger.error("SystemError--->", systemError);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return stringOwnershipProfileDetailsVOMap;
 }

 private void insertOwnershipProfileDetails(String applicationId, Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  int resultSet = 0;
  try
  {
   conn = Database.getConnection();
   Iterator it = ownershipProfileDetailsVOMap.entrySet().iterator();

   while(it.hasNext())
   {
    Map.Entry pair = (Map.Entry)it.next();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO = (OwnershipProfileDetailsVO)pair.getValue();

    query = "INSERT INTO ownershipprofile_details(application_id,type,addressproof,addressId,address,street,city,state,country,zipcode,identificationtypeselect,identificationtype,nationality,passportissuedate,passportexpirydate,title,firstname,dateofbirth,telnocc1,telephonenumber,emailaddress,owned,politicallyexposed,criminalrecord,designation,name,registration_number,lastname)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    pstmt = conn.prepareStatement(query);

    pstmt.setString(1, applicationId);
    if (functions.isValueNull((String)pair.getKey()))
    {
     pstmt.setString(2, (String)pair.getKey());
    }
    else
    {
     pstmt.setNull(2, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getAddressProof()))
    {
     pstmt.setString(3, ownershipProfileDetailsVO.getAddressProof());
    }
    else
    {
     pstmt.setNull(3, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getAddressId()))
    {
     pstmt.setString(4, ownershipProfileDetailsVO.getAddressId());
    }
    else
    {
     pstmt.setNull(4, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getAddress()))
    {
     pstmt.setString(5, ownershipProfileDetailsVO.getAddress());
    }
    else
    {
     pstmt.setNull(5, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getStreet()))
    {
     pstmt.setString(6, ownershipProfileDetailsVO.getStreet());
    }
    else
    {
     pstmt.setNull(6, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getCity()))
    {
     pstmt.setString(7, ownershipProfileDetailsVO.getCity());
    }
    else
    {
     pstmt.setNull(7, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getState()))
    {
     pstmt.setString(8, ownershipProfileDetailsVO.getState());
    }
    else
    {
     pstmt.setNull(8, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getCountry()))
    {
     pstmt.setString(9, ownershipProfileDetailsVO.getCountry());
    }
    else
    {
     pstmt.setNull(9, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getZipcode()))
    {
     pstmt.setString(10, ownershipProfileDetailsVO.getZipcode());
    }
    else
    {
     pstmt.setNull(10, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getIdentificationtypeselect()))
    {
     pstmt.setString(11, ownershipProfileDetailsVO.getIdentificationtypeselect());
    }
    else
    {
     pstmt.setNull(11, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getIdentificationtype()))
    {
     pstmt.setString(12, ownershipProfileDetailsVO.getIdentificationtype());
    }
    else
    {
     pstmt.setNull(12, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getNationality()))
    {
     pstmt.setString(13, ownershipProfileDetailsVO.getNationality());
    }
    else
    {
     pstmt.setNull(13, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getPassportissuedate()))
    {
     pstmt.setString(14, ownershipProfileDetailsVO.getPassportissuedate());
    }
    else
    {
     pstmt.setNull(14, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getPassportexpirydate()))
    {
     pstmt.setString(15, ownershipProfileDetailsVO.getPassportexpirydate());
    }
    else
    {
     pstmt.setNull(15, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getTitle()))
    {
     pstmt.setString(16, ownershipProfileDetailsVO.getTitle());
    }
    else
    {
     pstmt.setNull(16, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getFirstname()))
    {
     pstmt.setString(17, ownershipProfileDetailsVO.getFirstname());
    }
    else
    {
     pstmt.setNull(17, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getDateofbirth()))
    {
     pstmt.setString(18, ownershipProfileDetailsVO.getDateofbirth());
    }
    else
    {
     pstmt.setNull(18, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getTelnocc1()))
    {
     pstmt.setString(19, ownershipProfileDetailsVO.getTelnocc1());
    }
    else
    {
     pstmt.setNull(19, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getTelephonenumber()))
    {
     pstmt.setString(20, ownershipProfileDetailsVO.getTelephonenumber());
    }
    else
    {
     pstmt.setNull(20, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getEmailaddress()))
    {
     pstmt.setString(21, ownershipProfileDetailsVO.getEmailaddress());
    }
    else
    {
     pstmt.setNull(21, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getOwned()))
    {
     pstmt.setString(22, ownershipProfileDetailsVO.getOwned());
    }
    else
    {
     pstmt.setNull(22, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getPoliticallyexposed()))
    {
     pstmt.setString(23, ownershipProfileDetailsVO.getPoliticallyexposed());
    }
    else
    {
     pstmt.setNull(23, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getCriminalrecord()))
    {
     pstmt.setString(24, ownershipProfileDetailsVO.getCriminalrecord());
    }
    else
    {
     pstmt.setNull(24, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getDesignation()))
    {
     pstmt.setString(25, ownershipProfileDetailsVO.getDesignation());
    }
    else
    {
     pstmt.setNull(25, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getName()))
    {
     pstmt.setString(26, ownershipProfileDetailsVO.getName());
    }
    else
    {
     pstmt.setNull(26, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getRegistrationNumber()))
    {
     pstmt.setString(27, ownershipProfileDetailsVO.getRegistrationNumber());
    }
    else
    {
     pstmt.setNull(27, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getLastname()))
    {
     pstmt.setString(28, ownershipProfileDetailsVO.getLastname());
    }
    else
    {
     pstmt.setNull(28, Types.VARCHAR);
    }
    resultSet = pstmt.executeUpdate();
   }
  }
  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
 }

 private void updateOwnershipProfileDetails(String applicationId, Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap)
 {
  Connection conn = null;
  PreparedStatement pstmt = null;
  String query = null;
  int resultSet = 0;
  try
  {
   conn = Database.getConnection();
   Iterator it = ownershipProfileDetailsVOMap.entrySet().iterator();

   while(it.hasNext())
   {
    Map.Entry pair = (Map.Entry)it.next();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO = (OwnershipProfileDetailsVO) pair.getValue();

    query = "update ownershipprofile_details set addressproof=?,addressId=?,address=?,street=?,city=?,state=?,country=?,zipcode=?,identificationtypeselect=?,identificationtype=?,nationality=?,passportissuedate=?,passportexpirydate=?,title=?,firstname=?,dateofbirth=?,telnocc1=?,telephonenumber=?,emailaddress=?,owned=?,politicallyexposed=?,criminalrecord=?,designation=?,name=?,registration_number=?,lastname=? where application_id=? and type=?";
    pstmt = conn.prepareStatement(query);

    if (functions.isValueNull(ownershipProfileDetailsVO.getAddressProof()))
    {
     pstmt.setString(1, ownershipProfileDetailsVO.getAddressProof());
    }
    else
    {
     pstmt.setNull(1, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getAddressId()))
    {
     pstmt.setString(2, ownershipProfileDetailsVO.getAddressId());
    }
    else
    {
     pstmt.setNull(2, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getAddress()))
    {
     pstmt.setString(3, ownershipProfileDetailsVO.getAddress());
    }
    else
    {
     pstmt.setNull(3, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getStreet()))
    {
     pstmt.setString(4, ownershipProfileDetailsVO.getStreet());
    }
    else
    {
     pstmt.setNull(4, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getCity()))
    {
     pstmt.setString(5, ownershipProfileDetailsVO.getCity());
    }
    else
    {
     pstmt.setNull(5, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getState()))
    {
     pstmt.setString(6, ownershipProfileDetailsVO.getState());
    }
    else
    {
     pstmt.setNull(6, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getCountry()))
    {
     pstmt.setString(7, ownershipProfileDetailsVO.getCountry());
    }
    else
    {
     pstmt.setNull(7, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getZipcode()))
    {
     pstmt.setString(8, ownershipProfileDetailsVO.getZipcode());
    }
    else
    {
     pstmt.setNull(8, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getIdentificationtypeselect()))
    {
     pstmt.setString(9, ownershipProfileDetailsVO.getIdentificationtypeselect());
    }
    else
    {
     pstmt.setNull(9, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getIdentificationtype()))
    {
     pstmt.setString(10, ownershipProfileDetailsVO.getIdentificationtype());
    }
    else
    {
     pstmt.setNull(10, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getNationality()))
    {
     pstmt.setString(11, ownershipProfileDetailsVO.getNationality());
    }
    else
    {
     pstmt.setNull(11, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getPassportissuedate()))
    {
     pstmt.setString(12, ownershipProfileDetailsVO.getPassportissuedate());
    }
    else
    {
     pstmt.setNull(12, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getPassportexpirydate()))
    {
     pstmt.setString(13, ownershipProfileDetailsVO.getPassportexpirydate());
    }
    else
    {
     pstmt.setNull(13, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getTitle()))
    {
     pstmt.setString(14, ownershipProfileDetailsVO.getTitle());
    }
    else
    {
     pstmt.setNull(14, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getFirstname()))
    {
     pstmt.setString(15, ownershipProfileDetailsVO.getFirstname());
    }
    else
    {
     pstmt.setNull(15, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getDateofbirth()))
    {
     pstmt.setString(16, ownershipProfileDetailsVO.getDateofbirth());
    }
    else
    {
     pstmt.setNull(16, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getTelnocc1()))
    {
     pstmt.setString(17, ownershipProfileDetailsVO.getTelnocc1());
    }
    else
    {
     pstmt.setNull(17, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getTelephonenumber()))
    {
     pstmt.setString(18, ownershipProfileDetailsVO.getTelephonenumber());
    }
    else
    {
     pstmt.setNull(18, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getEmailaddress()))
    {
     pstmt.setString(19, ownershipProfileDetailsVO.getEmailaddress());
    }
    else
    {
     pstmt.setNull(19, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getOwned()))
    {
     pstmt.setString(20, ownershipProfileDetailsVO.getOwned());
    }
    else
    {
     pstmt.setNull(20, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getPoliticallyexposed()))
    {
     pstmt.setString(21, ownershipProfileDetailsVO.getPoliticallyexposed());
    }
    else
    {
     pstmt.setNull(21, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getCriminalrecord()))
    {
     pstmt.setString(22, ownershipProfileDetailsVO.getCriminalrecord());
    }
    else
    {
     pstmt.setNull(22, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getDesignation()))
    {
     pstmt.setString(23, ownershipProfileDetailsVO.getDesignation());
    }
    else
    {
     pstmt.setNull(23, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getName()))
    {
     pstmt.setString(24, ownershipProfileDetailsVO.getName());
    }
    else
    {
     pstmt.setNull(24, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getRegistrationNumber()))
    {
     pstmt.setString(25, ownershipProfileDetailsVO.getRegistrationNumber());
    }
    else
    {
     pstmt.setNull(25, Types.VARCHAR);
    }
    if (functions.isValueNull(ownershipProfileDetailsVO.getLastname()))
    {
     pstmt.setString(26, ownershipProfileDetailsVO.getLastname());
    }
    else
    {
     pstmt.setNull(26, Types.VARCHAR);
    }
    pstmt.setString(27, applicationId);
    pstmt.setString(28, (String)pair.getKey());
    logger.debug("Update OwnershipDetails for-------------"+(String)pair.getKey());
    logger.debug("Query-----"+pstmt);
    resultSet = pstmt.executeUpdate();
   }
  }
  catch (SQLException e)
  {
   logger.error("System Error while inserting data", e);
  }
  catch (SystemError systemError)
  {
   logger.error("System Error while inserting data", systemError);
  }
  finally
  {
   Database.closeConnection(conn);
  }
 }
 public List<BankTypeVO> getBankMappingDetails() throws PZDBViolationException
 {
  BankTypeVO bankTypeVO = null;
  Connection connection = null;
  PreparedStatement ps = null;
  ResultSet rs = null;
  StringBuffer query = new StringBuffer();
  Functions functions = new Functions();
  List<BankTypeVO> bankTypeVOList = new ArrayList<BankTypeVO>();

  try
  {
   connection = Database.getConnection();
   query.append("select bank_id, bank_name, template_name from bank_template_mapping");
   logger.debug("query=====" + query);

   ps = connection.prepareStatement(query.toString());
   rs = ps.executeQuery();
   while (rs.next())
   {
    bankTypeVO = new BankTypeVO();
    bankTypeVO.setBankId(rs.getString("bank_id"));
    bankTypeVO.setBankName(rs.getString("bank_name"));
    bankTypeVO.setFileName(rs.getString("template_name"));
    bankTypeVOList.add(bankTypeVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
  }
  catch (SQLException e)
  {
   logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
  }
  catch (Exception e)
  {
   logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
  }
  finally
  {
   Database.closeConnection(connection);
  }
  return bankTypeVOList;
 }


 public List<BankTypeVO> getBankMappingDetails(String bankId, String bankName, PaginationVO paginationVO) throws PZDBViolationException
 {
  BankTypeVO bankTypeVO1 = null;
  Connection connection = null;
  PreparedStatement ps = null;
  ResultSet rs = null;
  StringBuffer query = new StringBuffer();
  String countQuery = "";
  Functions functions = new Functions();
  List<BankTypeVO> bankTypeVOList = new ArrayList<BankTypeVO>();

  try
  {
   connection = Database.getConnection();
   query.append("select bank_id, bank_name, template_name from bank_template_mapping");
   if(functions.isValueNull(bankId) || functions.isValueNull(bankName))
   {
    query.append(" WHERE ");
    if(functions.isValueNull(bankId))
    {
     query.append(" bank_id = '"+bankId+ "'");
     if(functions.isValueNull(bankName))
      query.append(" and ");
    }
    if(functions.isValueNull(bankName))
    {
     query.append(" bank_name = '"+bankName+ "'");
    }
   }
   countQuery = "select count(*) from ("+query.toString()+") as temp";
   query.append(" limit "+paginationVO.getStart()+","+paginationVO.getEnd());
   logger.debug("query=====" + query);
   logger.debug("countQuery====="+countQuery);

   ps = connection.prepareStatement(query.toString());
   rs = ps.executeQuery();
   while (rs.next())
   {
    bankTypeVO1 = new BankTypeVO();
    bankTypeVO1.setBankId(rs.getString("bank_id"));
    bankTypeVO1.setBankName(rs.getString("bank_name"));
    bankTypeVO1.setFileName(rs.getString("template_name"));
    bankTypeVOList.add(bankTypeVO1);
   }

   PreparedStatement psCount = connection.prepareStatement(countQuery);
   rs = psCount.executeQuery();
   if (rs.next())
   {
    paginationVO.setTotalRecords(rs.getInt(1));
   }
  }
  catch (SystemError systemError)
  {
   logger.error("Leaving MerchantMonitoringDAO throwing System Exception as SystemError :::: ", systemError);
  }
  catch (SQLException e)
  {
   logger.error("Leaving MerchantMonitoringDAO throwing SQL Exception as SystemError :::: ", e);
  }
  catch (Exception e)
  {
   logger.error("Leaving TerminalDAO throwing SQL Exception as Exception :::: ", e);
  }
  finally
  {
   Database.closeConnection(connection);
  }
  return bankTypeVOList;
 }
 // consolidated memberid in manage application
 public Map<String,ConsolidatedApplicationVO> getconsolidated_memberList(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  ApplicationManagerVO applicationManagerVO=null;
  try {
   String query ="SELECT am.* FROM (SELECT pm.* FROM consolidated_application AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m WHERE m.partnerId=?)) AS am ORDER BY member_id";
   con=Database.getConnection();
   pstmt=con.prepareStatement(query);
   pstmt.setString(1,partnerId);
   ResultSet resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO= new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(), consolidatedApplicationVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;
 }

 public Map<String,ConsolidatedApplicationVO> SuperPartner_getconsolidated_memberList(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  ApplicationManagerVO applicationManagerVO=null;
  try {
   String query ="SELECT am.* FROM (SELECT pm.* FROM consolidated_application AS pm WHERE member_id IN(SELECT m.memberid FROM members m JOIN partners p ON m.partnerId=p.partnerId WHERE (m.partnerId=? OR p.superadminid=?))) AS am ORDER BY member_id";
   con=Database.getConnection();
   pstmt=con.prepareStatement(query);
   pstmt.setString(1,partnerId);
   pstmt.setString(2,partnerId);
   ResultSet resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO= new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(), consolidatedApplicationVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;
 }

 // consolidated memberid in consolidated application history
 public Map<String,ConsolidatedApplicationVO> getconsolidated_memberList_history(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  ApplicationManagerVO applicationManagerVO=null;
  try {
   String query ="SELECT am.* FROM (SELECT pm.* FROM consolidated_application_history AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m WHERE m.partnerId=?)) AS am ORDER BY member_id";
   con=Database.getConnection();
   pstmt=con.prepareStatement(query);
   pstmt.setString(1,partnerId);
   ResultSet resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO= new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(), consolidatedApplicationVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;
 }

 // consolidated memberid in consolidated application history for super partner
 public Map<String,ConsolidatedApplicationVO> getconsolidated_memberList_history_superpartner(String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  ApplicationManagerVO applicationManagerVO=null;
  try {
   String query ="SELECT am.* FROM (SELECT pm.* FROM consolidated_application_history AS pm WHERE member_id IN(SELECT m.memberid FROM members AS m JOIN partners AS p ON m.partnerId = p.partnerId WHERE (m.partnerId=? OR p.superadminid=?))) AS am ORDER BY member_id";
   con=Database.getConnection();
   pstmt=con.prepareStatement(query);
   pstmt.setString(1,partnerId);
   pstmt.setString(2,partnerId);
   ResultSet resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO= new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(), consolidatedApplicationVO);
   }
  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;
 }

 //get consolidate applicationn history of only mapped members
 public Map<String,ConsolidatedApplicationVO> getconsolidated_applicationHistoryForMappedMembers(String memberId,String name,String consolidatedId,String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  try
  {

   StringBuffer query =new StringBuffer("SELECT CA.consolidated_id,CA.member_id,CA.pgtypeid,CA.bankapplicationid,CA.status,CA.filename,CA.dtstamp,CA.timestamp,BTM.bank_name FROM consolidated_application_history AS CA JOIN bank_template_mapping AS BTM ON BTM.bank_id = CA.pgtypeid JOIN members AS M ON CA.`member_id`=M.memberid WHERE CA.consolidated_id>0 AND M.partnerId="+partnerId);

   if(functions.isValueNull(memberId))
   {
    query.append(" and CA.member_id =?");
   }

   if(functions.isValueNull(name))
   {
    query.append(" and BTM.bank_name =?");
   }

   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and CA.consolidated_id =?");
   }
   query.append(" ORDER BY CA.member_id");
   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());

   if(functions.isValueNull(memberId))
   {
    pstmt.setString(counter,memberId);
    counter++;
   }
   if(functions.isValueNull(name))
   {
    pstmt.setString(counter,name);
    counter++;
   }
   if(functions.isValueNull(consolidatedId))
   {
    pstmt.setString(counter,consolidatedId);
    counter++;
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO=new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setGatewayname(resultSet.getString("bank_name"));
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    //System.out.println(resultSet.getString("consolidated_id")+"==========>");

    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(),consolidatedApplicationVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;

 }

 //get consolidate applicationn history of only mapped members for superpartner
 public Map<String,ConsolidatedApplicationVO> getconsolidated_applicationHistoryForSuperPartner(String memberId,String name,String consolidatedId,String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  try
  {
   StringBuffer query =new StringBuffer("SELECT CA.consolidated_id,CA.member_id,CA.pgtypeid,CA.bankapplicationid,CA.status,CA.filename,CA.dtstamp,CA.timestamp,BTM.bank_name FROM consolidated_application_history AS CA JOIN bank_template_mapping AS BTM ON BTM.bank_id = CA.pgtypeid JOIN members AS M ON CA.`member_id`=M.memberid JOIN partners AS P ON M.partnerId = P.partnerId WHERE CA.consolidated_id>0 AND (M.partnerId="+partnerId+" OR P.superadminid="+partnerId+")");

   if(functions.isValueNull(memberId))
   {
    query.append(" and CA.member_id =?");
   }

   if(functions.isValueNull(name))
   {
    query.append(" and BTM.bank_name =?");
   }

   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and CA.consolidated_id =?");
   }
   query.append(" ORDER BY CA.member_id");
   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());
   if(functions.isValueNull(memberId))
   {
    pstmt.setString(counter,memberId);
    counter++;
   }
   if(functions.isValueNull(name))
   {
    pstmt.setString(counter,name);
    counter++;
   }
   if(functions.isValueNull(consolidatedId))
   {
    pstmt.setString(counter,consolidatedId);
    counter++;
   }
   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {
    consolidatedApplicationVO=new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setGatewayname(resultSet.getString("bank_name"));
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));
    //System.out.println(resultSet.getString("consolidated_id")+"==========>");

    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(),consolidatedApplicationVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;

 }


 // consolidated applications by mapped members    ........ Modified the query as per super partner access
 public Map<String,ConsolidatedApplicationVO> getconsolidated_applicationByMappedMembers(String memberId,String name,String consolidatedId,String partnerId)
 {
  Connection con = null;
  PreparedStatement pstmt=null;
  ResultSet resultSet=null;
  Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=new HashMap<String, ConsolidatedApplicationVO>();
  int counter=1;
  ConsolidatedApplicationVO consolidatedApplicationVO=null;
  try
  {

   StringBuffer query =new StringBuffer("SELECT CA.consolidated_id,CA.member_id,CA.pgtypeid,CA.bankapplicationid,CA.status,CA.filename,CA.dtstamp,CA.timestamp,BTM.bank_name FROM consolidated_application AS CA JOIN bank_template_mapping AS BTM ON BTM.bank_id = CA.pgtypeid JOIN members AS M ON CA.`member_id`=M.memberid JOIN partners P ON M.partnerId=P.partnerId WHERE CA.consolidated_id>0 AND (M.partnerId="+partnerId+" OR P.superadminid="+partnerId+")");

   if(functions.isValueNull(memberId))
   {
    query.append(" and CA.member_id =?");
   }

   if(functions.isValueNull(name))
   {
    query.append(" and BTM.bank_name =?");
   }

   if(functions.isValueNull(consolidatedId))
   {
    query.append(" and CA.consolidated_id =?");
   }
   query.append(" ORDER BY CA.member_id");

   //con=Database.getConnection();
   con=Database.getRDBConnection();
   pstmt=con.prepareStatement(query.toString());

   logger.debug("pstmt--->"+pstmt);

   if(functions.isValueNull(memberId))
   {
    pstmt.setString(counter,memberId);
    counter++;
   }
   if(functions.isValueNull(name))
   {
    pstmt.setString(counter,name);
    counter++;
   }
   if(functions.isValueNull(consolidatedId))
   {
    pstmt.setString(counter,consolidatedId);
    counter++;
   }

   resultSet=pstmt.executeQuery();
   while(resultSet.next())
   {

    consolidatedApplicationVO=new ConsolidatedApplicationVO();
    consolidatedApplicationVO.setMemberid(resultSet.getString("member_id"));
    consolidatedApplicationVO.setGatewayname(resultSet.getString("bank_name"));
    consolidatedApplicationVO.setConsolidated_id(resultSet.getString("consolidated_id"));
    consolidatedApplicationVO.setPgtypeid(resultSet.getString("pgtypeid"));
    consolidatedApplicationVO.setBankapplicationid(resultSet.getString("bankapplicationid"));
    consolidatedApplicationVO.setFilename(resultSet.getString("filename"));
    consolidatedApplicationVO.setStatus(resultSet.getString("status"));
    consolidatedApplicationVO.setDtstamp(resultSet.getString("dtstamp"));
    consolidatedApplicationVO.setTimestamp(resultSet.getString("timestamp"));


    consolidatedApplicationVOMap.put(consolidatedApplicationVO.getConsolidated_id(),consolidatedApplicationVO);
   }

  }
  catch (SystemError systemError)
  {
   logger.error(" System Error for Consolidated Application",systemError);
  }
  catch (SQLException e)
  {
   logger.error(" Sql exception for Consolidated Application",e);
  }
  finally
  {
   Database.closeResultSet(resultSet);
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(con);
  }
  return consolidatedApplicationVOMap;
 }

 //added methodfor kyc configuration --suraj
 public List<AppKycTemplateVO> gettemplatedetails(String partnerid)
 {
  List<AppKycTemplateVO> kycTemplateVO = new ArrayList<AppKycTemplateVO>();
  Connection connection = null;
  try
  {
   connection = Database.getConnection();
   String query = "SELECT label_name,ismandatory from uploadfile_label WHERE partnerid=?";
   PreparedStatement p = connection.prepareStatement(String.valueOf(query));
   p.setString(1, partnerid);
   ResultSet rs=p.executeQuery();
   while (rs.next())
   {
    AppKycTemplateVO  kycTemplateVO1 = new AppKycTemplateVO();
    kycTemplateVO1.setLabelName(rs.getString("label_name"));
    kycTemplateVO1.setCriteria(rs.getString("ismandatory"));
    kycTemplateVO.add(kycTemplateVO1);
   }
   logger.debug("query-----"+p);
  }
  catch (SQLException e)
  {
   logger.error("SQLException----", e);
  }
  catch (SystemError e)
  {
   logger.error("SystemError----", e);
  }
  finally
  {
   Database.closeConnection(connection);
  }
  return kycTemplateVO;
 }
 public void deleteUploadFile(String partnerid)
 {

  Connection conn = null;
  PreparedStatement pstmt=null;
  String status="";
  try
  {
   conn = Database.getConnection();
   String query = "delete from uploadfile_label where partnerid = ?";
   PreparedStatement p = conn.prepareStatement(query);
   p.setString(1, partnerid);

   p.executeUpdate();

  }
  catch (SystemError e)
  {
   logger.error("SystemError----", e);
  }
  catch (SQLException e)
  {
   logger.error("SQLException----", e);
  }
  finally
  {

   Database.closeConnection(conn);
  }
 }
 public String addnewkyctemplate(List<AppKycTemplateVO> kycTemplateVO)throws PZDBViolationException
 {
  Connection conn = null;
  PreparedStatement pstmt=null;
  String status="";
  String query=null;
  try
  {
   conn = Database.getConnection();

   for(AppKycTemplateVO appKycTemplateVO : kycTemplateVO)
   {
    query = "insert into uploadfile_label(partnerid,label_name,ismandatory,supportedfile_type,functional_usage,alternate_name,label_id)values(?,?,?,?,?,?,?)";
    pstmt = conn.prepareStatement(query);
    pstmt.setString(1, appKycTemplateVO.getPartnerid());
    pstmt.setString(2, appKycTemplateVO.getLabelName());
    pstmt.setString(3, appKycTemplateVO.getCriteria());
    pstmt.setString(4, appKycTemplateVO.getSupportedFileType());
    pstmt.setString(5, appKycTemplateVO.getFunctionalUsage());
    pstmt.setString(6, appKycTemplateVO.getAlternateName());
    pstmt.setString(7, appKycTemplateVO.getLabelId());

    int k = pstmt.executeUpdate();
    if (k > 0)
    {
     status = "success";
    }
   }
  }
  catch (SystemError se)
  {
   PZExceptionHandler.raiseDBViolationException("ApplicationManagerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
  }
  catch (SQLException e)
  {
   PZExceptionHandler.raiseDBViolationException("ApplicationManagerDAO.java", "addNewDefaultTheme()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
  }
  finally
  {
   Database.closePreparedStatement(pstmt);
   Database.closeConnection(conn);
  }
  return status;
 }

}