package com.manager.enums;

import com.directi.pg.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 5/7/15
 *
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public enum DefinedAcroFields
{
    //Step 1 Company Profile
    CompanyName("CompanyName"),
    CompanyAddress("CompanyAddress"),
    CompanyCity("CompanyCity"),
    CompanyState("CompanyState"),
    CompanyCountry("CompanyCountry"),
    CompanyZip ("CompanyZip"),
    Companyphonecc1("Companyphonecc1"),
    CompanyTelephoneNO("CompanyTelephoneNO"),
    CompanyFax("CompanyFax"),
    CompanyEmailAddress("CompanyEmailAddress"),
    CompanyCountryofRegistration("CompanyCountryofRegistration"),
    CompanyRegistrationNumber("CompanyRegistrationNumber"),
    CompanyDateOfRegistration("CompanyDateOfRegistration"),
    CompanyVat("CompanyVat"),
    FederalTaxID("FederalTaxID"),
    TypeofBusiness("TypeofBusiness"),   //radio button

    BusinessName("BusinessName"),//BusinessName
    BusinessAddress("BusinessAddress"),//BusinessName
    BusinessCity("BusinessCity"),//BusinessName
    BusinessState("BusinessState"),//BusinessName
    BusinessCountry("BusinessCountry"),//BusinessName
    BusinessZip("BusinessZip"),//BusinessName

    RegisteredCorporateNameEU("RegisteredCorporateNameEU"),
    RegisteredDirectorsEU("RegisteredDirectorsEU"),
    RegisteredDirectorsAddressEU("RegisteredDirectorsAddressEU"),
    RegisteredDirectorsCity("RegisteredDirectorsCity"),
    RegisteredDirectorsState("RegisteredDirectorsState"),
    RegisteredDirectorsCountry("RegisteredDirectorsCountry"),
    RegisteredDirectorsZip("RegisteredDirectorsZip"),
    EURegistrationNumber("EURegistrationNumber"),

    companylengthoftimebusiness("companylengthoftimebusiness"),
    NumberofEmployees("NumberofEmployees"),
    CapitalResources("CapitalResources"),
    companycurrencylastyear("companycurrencylastyear"),
    TurnoverLastYear("TurnoverLastYear"),
    companyturnoverlastyearunit("companyturnoverlastyearunit"),

    contactname("contactname"),
    contactEmailAddress ("contactEmailAddress"),
    Contactnametelnocc1("Contactnametelnocc1"),
    ContactphoneNumber("ContactphoneNumber"),
    skypeIMaddress("skypeIMaddress"),
    TechnicalContactName("TechnicalContactName"),
    TechnicalEmailAddress("TechnicalEmailAddress"),
    technicalphonecc1("technicalphonecc1"),
    TechnicalTelephoneNumber("TechnicalTelephoneNumber"),
    FinancialContactName("FinancialContactName"),
    FinancialEmailAddress("FinancialEmailAddress"),
    financialphonecc1("financialphonecc1"),
    FinancialTelephoneNumber("FinancialTelephoneNumber"),

    Bankruptcy("Bankruptcy"),    //radio button
    companybankruptcydate("companybankruptcydate"),
    Licenserequired("Licenserequired"),  //radio button
    LicensePermission("LicensePermission"), //radio button
    legalProceeding("legalProceeding"),//Radio button


       //Ownership Profile

    //Nameprincipal 1
    nameprincipal1("nameprincipal1"),
    nameprincipal1title("nameprincipal1title"),
    nameprincipal1lastname("nameprincipal1lastname"),
    nameprincipal1owned("nameprincipal1owned"),
    nameprincipal1State("nameprincipal1State"),
    nameprincipal1address("nameprincipal1address"),
    nameprincipal1city("nameprincipal1city"),
    nameprincipal1zip("nameprincipal1zip"),
    nameprincipal1country("nameprincipal1country"),
    nameprincipal1telnocc1("nameprincipal1telnocc1"),
    nameprincipal1telephonenumber("nameprincipal1telephonenumber"),
    nameprincipal1emailaddress("nameprincipal1emailaddress"),
    nameprincipal1dateofbirth("nameprincipal1dateofbirth"),
    nameprincipal1identificationtypeselect("nameprincipal1identificationtypeselect"),
    nameprincipal1identificationtype("nameprincipal1identificationtype"),
    nameprincipal1nationality("nameprincipal1nationality"),
    nameprincipal1Passportexpirydate("nameprincipal1Passportexpirydate"),

    politicallyExposed1("politicallyExposed1"),
    criminalRecord1("criminalRecord1"),

    //Nameprincipal 2
    nameprincipal2("nameprincipal2"),
    nameprincipal2lastname("nameprincipal2lastname"),
    nameprincipal2title("nameprincipal2title"),
    nameprincipal2owned("nameprincipal2owned"),
    nameprincipal2State("nameprincipal2State"),
    nameprincipal2address("nameprincipal2address"),
    nameprincipal2city("nameprincipal2city"),
    nameprincipal2zip("nameprincipal2zip"),
    nameprincipal2country("nameprincipal2country"),
    nameprincipal2telnocc2("nameprincipal2telnocc2"),
    nameprincipal2telephonenumber("nameprincipal2telephonenumber"),
    nameprincipal2emailaddress("nameprincipal2emailaddress"),
    nameprincipal2dateofbirth("nameprincipal2dateofbirth"),
    nameprincipal2identificationtypeselect("nameprincipal2identificationtypeselect"),
    nameprincipal2identificationtype("nameprincipal2identificationtype"),
    nameprincipal2nationality("nameprincipal2nationality"),
    nameprincipal2Passportexpirydate("nameprincipal2Passportexpirydate"),

    politicallyExposed2("politicallyExposed2"),
    criminalRecord2("criminalRecord2"),

    //Nameprincipal 3
    nameprincipal3("nameprincipal3"),
    nameprincipal3lastname("nameprincipal3lastname"),
    nameprincipal3title("nameprincipal3title"),
    nameprincipal3owned("nameprincipal3owned"),
    nameprincipal3State("nameprincipal3State"),
    nameprincipal3address("nameprincipal3address"),
    nameprincipal3city("nameprincipal3city"),
    nameprincipal3zip("nameprincipal3zip"),
    nameprincipal3country("nameprincipal3country"),
    nameprincipal3telnocc2("nameprincipal3telnocc2"),
    nameprincipal3telephonenumber("nameprincipal3telephonenumber"),
    nameprincipal3emailaddress("nameprincipal3emailaddress"),
    nameprincipal3dateofbirth("nameprincipal3dateofbirth"),
    nameprincipal3identificationtypeselect("nameprincipal3identificationtypeselect"),
    nameprincipal3identificationtype("nameprincipal3identificationtype"),
    nameprincipal3nationality("nameprincipal3nationality"),
    nameprincipal3Passportexpirydate("nameprincipal3Passportexpirydate"),

    politicallyExposed3("politicallyExposed3"),
    criminalRecord3("criminalRecord3"),

    //Individual Shareholder 1
    shareholderprofile1("shareholderprofile1"),
    shareholderprofile1lastname("shareholderprofile1lastname"),
    shareholderprofile1title("shareholderprofile1title"),
    shareholderprofile1owned("shareholderprofile1owned"),
    shareholderprofile1State("shareholderprofile1State"),
    shareholderprofile1address("shareholderprofile1address"),
    shareholderprofile1city("shareholderprofile1city"),
    shareholderprofile1zip("shareholderprofile1zip"),
    shareholderprofile1country("shareholderprofile1country"),
    shareholderprofile1telnocc1("shareholderprofile1telnocc1"),
    shareholderprofile1telephonenumber("shareholderprofile1telephonenumber"),
    shareholderprofile1emailaddress("shareholderprofile1emailaddress"),
    shareholderprofile1dateofbirth("shareholderprofile1dateofbirth"),
    shareholderprofile1identificationtypeselect("shareholderprofile1identificationtypeselect"),
    shareholderprofile1identificationtype("shareholderprofile1identificationtype"),
    shareholderprofile1nationality("shareholderprofile1nationality"),
    shareholderprofile1Passportexpirydate("shareholderprofile1Passportexpirydate"),

    //Individual Shareholder 2
    shareholderprofile2("shareholderprofile2"),
    shareholderprofile2lastname("shareholderprofile2lastname"),
    shareholderprofile2title("shareholderprofile2title"),
    shareholderprofile2State("shareholderprofile2State"),
    shareholderprofile2address("shareholderprofile2address"),
    shareholderprofile2city("shareholderprofile2city"),
    shareholderprofile2zip("shareholderprofile2zip"),
    shareholderprofile2country("shareholderprofile2country"),
    shareholderprofile2owned("shareholderprofile2owned"),
    shareholderprofile2telnocc2("shareholderprofile2telnocc2"),
    shareholderprofile2telephonenumber("shareholderprofile2telephonenumber"),
    shareholderprofile2emailaddress("shareholderprofile2emailaddress"),
    shareholderprofile2dateofbirth("shareholderprofile2dateofbirth"),
    shareholderprofile2identificationtypeselect("shareholderprofile2identificationtypeselect"),
    shareholderprofile2identificationtype("shareholderprofile2identificationtype"),
    shareholderprofile2nationality("shareholderprofile2nationality"),
    shareholderprofile2Passportexpirydate("shareholderprofile2Passportexpirydate"),

    //Individual Shareholder 3
    shareholderprofile3("shareholderprofile3"),
    shareholderprofile3lastname("shareholderprofile3lastname"),
    shareholderprofile3title("shareholderprofile3title"),
    shareholderprofile3owned("shareholderprofile3owned"),
    shareholderprofile3State("shareholderprofile3State"),
    shareholderprofile3address("shareholderprofile3address"),
    shareholderprofile3city("shareholderprofile3city"),
    shareholderprofile3zip("shareholderprofile3zip"),
    shareholderprofile3country("shareholderprofile3country"),
    shareholderprofile3telnocc2("shareholderprofile3telnocc2"),
    shareholderprofile3telephonenumber("shareholderprofile3telephonenumber"),
    shareholderprofile3emailaddress("shareholderprofile3emailaddress"),
    shareholderprofile3dateofbirth("shareholderprofile3dateofbirth"),
    shareholderprofile3identificationtypeselect("shareholderprofile3identificationtypeselect"),
    shareholderprofile3identificationtype("shareholderprofile3identificationtype"),
    shareholderprofile3nationality("shareholderprofile3nationality"),
    shareholderprofile3Passportexpirydate("shareholderprofile3Passportexpirydate"),

    // Corporate Shareholder 1
    corporateshareholder1Name("corporateshareholder1Name"),
    corporateshareholder1RegNumber("corporateshareholder1RegNumber"),
    corporateshareholder1Address("corporateshareholder1Address"),
    corporateshareholder1City("corporateshareholder1City"),
    corporateshareholder1State("corporateshareholder1State"),
    corporateshareholder1ZipCode("corporateshareholder1ZipCode"),
    corporateshareholder1Country("corporateshareholder1Country"),

    // Corporate Shareholder 2
    corporateshareholder2Name("corporateshareholder2Name"),
    corporateshareholder2RegNumber("corporateshareholder2RegNumber"),
    corporateshareholder2Address("corporateshareholder2Address"),
    corporateshareholder2City("corporateshareholder2City"),
    corporateshareholder2State("corporateshareholder2State"),
    corporateshareholder2ZipCode("corporateshareholder2ZipCode"),
    corporateshareholder2Country("corporateshareholder2Country"),

    // Corporate Shareholder 3
    corporateshareholder3Name("corporateshareholder3Name"),
    corporateshareholder3RegNumber("corporateshareholder3RegNumber"),
    corporateshareholder3Address("corporateshareholder3Address"),
    corporateshareholder3City("corporateshareholder3City"),
    corporateshareholder3State("corporateshareholder3State"),
    corporateshareholder3ZipCode("corporateshareholder3ZipCode"),
    corporateshareholder3Country("corporateshareholder3Country"),

    //Director 1
    directorsprofile("directorsprofile"),
    directorsprofilelastname("directorsprofilelastname"),
    directorsprofiletitle("directorsprofiletitle"),
    directorsprofileState("directorsprofileState"),
    directorsprofileaddress("directorsprofileaddress"),
    directorsprofilecity("directorsprofilecity"),
    directorsprofilezip("directorsprofilezip"),
    directorsprofilecountry("directorsprofilecountry"),
    directorsprofiletelnocc1("directorsprofiletelnocc1"),
    directorsprofiletelephonenumber("directorsprofiletelephonenumber"),
    directorsprofileemailaddress("directorsprofileemailaddress"),
    directorsprofiledateofbirth("directorsprofiledateofbirth"),
    directorsprofileidentificationtypeselect("directorsprofileidentificationtypeselect"),
    directorsprofileidentificationtype("directorsprofileidentificationtype"),
    directorsprofilenationality("directorsprofilenationality"),
    directorsprofilePassportexpirydate("directorsprofilePassportexpirydate"),

    //Director 2
    directorsprofile2("directorsprofile2"),
    directorsprofile2lastname("directorsprofile2lastname"),
    directorsprofile2title("directorsprofile2title"),
    directorsprofile2State("directorsprofile2State"),
    directorsprofile2address("directorsprofile2address"),
    directorsprofile2city("directorsprofile2city"),
    directorsprofile2zip ("directorsprofile2zip"),
    directorsprofile2country("directorsprofile2country"),
    directorsprofile2telnocc1("directorsprofile2telnocc1"),
    directorsprofile2telephonenumber("directorsprofile2telephonenumber"),
    directorsprofile2emailaddress("directorsprofile2emailaddress"),
    directorsprofile2dateofbirth("directorsprofile2dateofbirth"),
    directorsprofile2identificationtype("directorsprofileidentificationtype"),
    directorsprofile2identificationtypeselect("directorsprofile2identificationtypeselect"),
    directorsprofile2nationality("directorsprofile2nationality"),
    directorsprofile2Passportexpirydate("directorsprofile2Passportexpirydate"),

    //Director 3
    directorsprofile3("directorsprofile3"),
    directorsprofile3lastname("directorsprofile3lastname"),
    directorsprofile3title("directorsprofile3title"),
    directorsprofile3address("directorsprofile3address"),
    directorsprofile3city("directorsprofile3city"),
    directorsprofile3State("directorsprofile3State"),
    directorsprofile3zip (" directorsprofile3zip "),
    directorsprofile3country("directorsprofile3country"),
    directorsprofile3telnocc1("directorsprofile3telnocc1"),
    directorsprofile3telephonenumber("directorsprofile3telephonenumber"),
    directorsprofile3emailaddress("directorsprofile3emailaddress"),
    directorsprofile3dateofbirth("directorsprofile3dateofbirth"),
    directorsprofile3identificationtype("directorsprofile3identificationtype"),
    directorsprofile3identificationtypeselect("directorsprofile3identificationtypeselect"),
    directorsprofile3nationality("directorsprofile3nationality"),
    directorsprofile3Passportexpirydate("directorsprofile3Passportexpirydate"),

    //Authorized signatory 1
    authorizedsignatoryprofile("authorizedsignatoryprofile"),
    authorizedsignatoryprofilelastname("authorizedsignatoryprofilelastname"),
    authorizedsignatoryprofiletitle("authorizedsignatoryprofiletitle"),
    authorizedsignatoryprofileState("authorizedsignatoryprofileState"),
    authorizedsignatoryprofileaddress("authorizedsignatoryprofileaddress"),
    authorizedsignatoryprofilecity("authorizedsignatoryprofilecity"),
    authorizedsignatoryprofilezip("authorizedsignatoryprofilezip"),
    authorizedsignatoryprofilecountry("authorizedsignatoryprofilecountry"),
    authorizedsignatoryprofiletelnocc1("authorizedsignatoryprofiletelnocc1"),
    authorizedsignatoryprofiletelephonenumber("authorizedsignatoryprofiletelephonenumber"),
    authorizedsignatoryprofileemailaddress("authorizedsignatoryprofileemailaddress"),
    authorizedsignatoryprofiledateofbirth("authorizedsignatoryprofiledateofbirth"),
    authorizedsignatoryprofileidentificationtypeselect("authorizedsignatoryprofileidentificationtypeselect"),
    authorizedsignatoryprofileidentificationtype("authorizedsignatoryprofileidentificationtype"),
    authorizedsignatoryprofilenationality("authorizedsignatoryprofilenationality"),
    authorizedsignatoryprofilePassportexpirydate("authorizedsignatoryprofilePassportexpirydate"),


    //Authorized signatory 2
    authorizedsignatoryprofile2("authorizedsignatoryprofile2"),
    authorizedsignatoryprofile2lastname("authorizedsignatoryprofile2lastname"),
    authorizedsignatoryprofile2title("authorizedsignatoryprofile2title"),
    authorizedsignatoryprofile2State("authorizedsignatoryprofile2State"),
    authorizedsignatoryprofile2address("authorizedsignatoryprofile2address"),
    authorizedsignatoryprofile2city("authorizedsignatoryprofile2city"),
    authorizedsignatoryprofile2zip("authorizedsignatoryprofile2zip"),
    authorizedsignatoryprofile2country("authorizedsignatoryprofile2country"),
    authorizedsignatoryprofile2telnocc2("authorizedsignatoryprofile2telnocc2"),
    authorizedsignatoryprofile2telephonenumber("authorizedsignatoryprofile2telephonenumber"),
    authorizedsignatoryprofile2emailaddress("authorizedsignatoryprofile2emailaddress"),
    authorizedsignatoryprofile2dateofbirth("authorizedsignatoryprofile2dateofbirth"),
    authorizedsignatoryprofile2identificationtypeselect("authorizedsignatoryprofile2identificationtypeselect"),
    authorizedsignatoryprofile2identificationtype("authorizedsignatoryprofile2identificationtype"),
    authorizedsignatoryprofile2nationality("authorizedsignatoryprofile2nationality"),
    authorizedsignatoryprofile2Passportexpirydate("authorizedsignatoryprofile2Passportexpirydate"),

    //Authorized signatory 3
    authorizedsignatoryprofile3("authorizedsignatoryprofile3"),
    authorizedsignatoryprofile3lastname("authorizedsignatoryprofile3lastname"),
    authorizedsignatoryprofile3title("authorizedsignatoryprofile3title"),
    authorizedsignatoryprofile3State("authorizedsignatoryprofile3State"),
    authorizedsignatoryprofile3address("authorizedsignatoryprofile3address"),
    authorizedsignatoryprofile3city("authorizedsignatoryprofile3city"),
    authorizedsignatoryprofile3zip("authorizedsignatoryprofile3zip"),
    authorizedsignatoryprofile3country("authorizedsignatoryprofile3country"),
    authorizedsignatoryprofile3telnocc2("authorizedsignatoryprofile3telnocc2"),
    authorizedsignatoryprofile3telephonenumber("authorizedsignatoryprofile3telephonenumber"),
    authorizedsignatoryprofile3emailaddress("authorizedsignatoryprofile3emailaddress"),
    authorizedsignatoryprofile3dateofbirth("authorizedsignatoryprofile3dateofbirth"),
    authorizedsignatoryprofile3identificationtypeselect("authorizedsignatoryprofile3identificationtypeselect"),
    authorizedsignatoryprofile3identificationtype("authorizedsignatoryprofile3identificationtype"),
    authorizedsignatoryprofile3nationality("authorizedsignatoryprofile3nationality"),
    authorizedsignatoryprofile3Passportexpirydate("authorizedsignatoryprofile3Passportexpirydate"),

    //Business Profile

    urls("urls"),
    descriptorcreditcardstmt("descriptorcreditcardstmt"),
    ipaddress("ipaddress"),
    loginId("loginId"),
    passWord("passWord"),

    methodofacceptancemoto("methodofacceptancemoto"),
    methodofacceptanceinternet("methodofacceptanceinternet"),
    methodofacceptanceswipe("methodofacceptanceswipe"),

    averageticket("averageticket"),
    highestticket("highestticket"),
    lowestticket("lowestticket"),

    foreigntransactionsus("foreigntransactionsus"),
    foreigntransactionsEurope("foreigntransactionsEurope"),
    foreigntransactionsAsia("foreigntransactionsAsia"),
    foreigntransactionscis("foreigntransactionscis"),
    foreigntransactionscanada("foreigntransactionscanada"),
    foreigntransactionsRestoftheWorld("foreigntransactionsRestoftheWorld"),

    cardtypesacceptedvisa("cardtypesacceptedvisa"),
    cardtypesacceptedmastercard("cardtypesacceptedmastercard"),
    cardtypesacceptedamericanexpress("cardtypesacceptedamericanexpress"),
    cardtypesaccepteddiscover("cardtypesaccepteddiscover"),
    cardtypesaccepteddiners("cardtypesaccepteddiners"),
    cardtypesacceptedjcb("cardtypesacceptedjcb"),
    cardtypesacceptedother("cardtypesacceptedother"),
    cardtypesacceptedotheryes("cardtypesacceptedotheryes"),

    descriptionofproducts("descriptionofproducts"),
    recurringservices("recurringservices"),  //radio button
    recurringservicesyes("recurringservicesyes"),
    isacallcenterused("isacallcenterused"),      //radio button
    isacallcenterusedyes("isacallcenterusedyes"),
    isafulfillmenthouseused("isafulfillmenthouseused"),   //radio button
    isafulfillmenthouseusedyes("isafulfillmenthouseusedyes"),

    //Website Review
    visamastercardlogos("visamastercardlogos"),
    pricedisplayed("pricedisplayed"),
    companyIdentifiable("companyIdentifiable"),
    clearlyPresented("clearlyPresented"),
    trackingNumber("trackingNumber"),
    domainsOwned("domainsOwned"),
    cardholderasked("cardholderasked"),
    threeDsecurecompulsory("threeDsecurecompulsory"),
    transactioncurrency("transactioncurrency"),
    dynamicdescriptors("dynamicdescriptors"),
    shoppingcart("shoppingcart"),
    shoppingcartdetails("shoppingcartdetails"),

    //Shipping and Returns
    pricingpolicieswebsite("pricingpolicieswebsite"),
    fulfillmenttimeframe("fulfillmenttimeframe"),
    goodspolicy("goodspolicy"),
    countriesblocked("countriesblocked"), //radio button
    countriesblockeddetails("countriesblockeddetails"),
    inHouseLocation("inHouseLocation"),
    contactPerson("contactPerson"),
    otherLocation("otherLocation"),
    mainSuppliers("mainSuppliers"),
    shipmentAssured("shipmentAssured"),

    //Contents
    sourceContent("sourceContent"),
    copyright("copyright"),

    //Security Policy
    kycprocesses("kycprocesses"),            //start radio button
    securitypolicy("securitypolicy"),     //radio button
    confidentialitypolicy("confidentialitypolicy"),     //radio button
    applicablejurisdictions("applicablejurisdictions"),    //radio button
    privacyanonymitydataprotection("privacyanonymitydataprotection"), //radio button
    sslSecured("sslSecured"),
    productrequireage("productrequireage"),//new field  radio button
    affiliateprograms("affiliateprograms"),   //radio button
    affiliateprogramsdetails("affiliateprogramsdetails"),
    listfraudtools("listfraudtools"),
    applicationservices("applicationservices"),//new field  radio button

    //Customer service Information
    customersupport("customersupport"),   //radio button
    customersupportemail("customersupportemail"),
    custsupportworkhours("custsupportworkhours"),
    technicalcontact("technicalcontact"),
    timeframe("timeframe"),
    livechat("livechat"),

    //Other Information
    MCCCtegory("MCCCtegory"),         //end radio button
    merchantCode("merchantCode"),
    coolingoffperiod("coolingoffperiod"),
    customersidentification("customersidentification"),
    directMail("directMail"),
    yellowPages("yellowPages"),
    radioTv("radioTv"),
    internet("internet"),
    networking("networking"),
    outboundTelemarketing("outboundTelemarketing"),

    //Billing model/Subscriptions
    billingModel("billingModel"),
    billingTimeFrame("billingTimeFrame"),
    recurringAmount("recurringAmount"),
    automaticRecurring("automaticRecurring"),
    multipleMembership("multipleMembership"),
    freeMembership("freeMembership"),
    creditCardRequired("creditCardRequired"),
    automaticallyBilled("automaticallyBilled"),
    preAuthorization("preAuthorization"),

    // BANK PDF Fields
    ForeignTransaction("ForeignTransaction"),
    ForeignTransaction2("ForeignTransaction2"),
    ForeignTransaction_besidesAsia_Europe_US_other("ForeignTransaction_besidesAsia_Europe_US_other"),
    ForeignTransaction_All("ForeignTransaction_All"),
    ForeignTransaction_US_Canada("ForeignTransaction_US_Canada"),
    cardtypesaccepted_all("cardtypesaccepted_all"),
    cardtypeaccepted_besidesVisaMasterCardDinners("cardtypeaccepted_besidesVisaMasterCardDinners"),
    CardTypeAcceptedBesides_Visa_MC("CardTypeAcceptedBesides_Visa_MC"),
    MarketStrategy_All("MarketStrategy_All"),

    //Bank profile

    //Currency Requested
    currencyProductSoldINR("currencyProductSoldINR"),
    currencyProductSoldUSD("currencyProductSoldUSD"),
    currencyProductSoldEUR("currencyProductSoldEUR"),
    currencyProductSoldGBP("currencyProductSoldGBP"),
    currencyProductSoldJPY("currencyProductSoldJPY"),
    currencyProductSoldPEN("currencyProductSoldPEN"),
    currencyProductSoldHKD("currencyProductSoldHKD"),
    currencyProductSoldCAD("currencyProductSoldCAD"),
    currencyProductSoldAUD("currencyProductSoldAUD"),
    currencyProductSoldDKK("currencyProductSoldDKK"),
    currencyProductSoldSEK("currencyProductSoldSEK"),
    currencyProductSoldNOK("currencyProductSoldNOK"),

    currencyTransferredINR("currencyTransferredINR"),
    currencyTransferredUSD("currencyTransferredUSD"),
    currencyTransferredEUR("currencyTransferredEUR"),
    currencyTransferredGBP("currencyTransferredGBP"),
    currencyTransferredJPY("currencyTransferredJPY"),
    currencyTransferredPEN("currencyTransferredPEN"),
    currencyTransferredHKD("currencyTransferredHKD"),
    currencyTransferredCAD("currencyTransferredCAD"),
    currencyTransferredAUD("currencyTransferredAUD"),
    currencyTransferredDKK("currencyTransferredDKK"),
    currencyTransferredSEK("currencyTransferredSEK"),
    currencyTransferredNOK("currencyTransferredNOK"),

    //Bank Information
    bankinfobic("bankinfobic"),
    bankinfobankname("bankinfobankname"),
    bankinfobankaddress("bankinfobankaddress"),
    bankinfobankphonenumber("bankinfobankphonenumber"),
    bankcontactperson("bankcontactperson"),
    bankinfoabaroutingcode("bankinfoabaroutingcode"),
    bankinfoaccountholder("bankinfoaccountholder"),
    bankaccountnumberIBAN("bankaccountnumberIBAN"),

    //Aquirer Details
    aquirer("aquirer"),
    reasonaquirer("reasonaquirer"),

    salesvolumelastmonth("salesvolumelastmonth"),
    salesvolume2monthsago("salesvolume2monthsago"),
    salesvolume3monthsago("salesvolume3monthsago"),
    salesvolume4monthsago("salesvolume4monthsago"),
    salesvolume5monthsago("salesvolume5monthsago"),
    salesvolume6monthsago("salesvolume6monthsago"),

    numberoftransactionslastmonth("numberoftransactionslastmonth"),
    numberoftransactions2monthsago("numberoftransactions2monthsago"),
    numberoftransactions3monthsago("numberoftransactions3monthsago"),
    numberoftransactions4monthsago("numberoftransactions4monthsago"),
    numberoftransactions5monthsago("numberoftransactions5monthsago"),
    numberoftransactions6monthsago("numberoftransactions6monthsago"),

    chargebackvolumelastmonth("chargebackvolumelastmonth"),
    chargebackvolume2monthsago("chargebackvolume2monthsago"),
    chargebackvolume3monthsago("chargebackvolume3monthsago"),
    chargebackvolume4monthsago("chargebackvolume4monthsago"),
    chargebackvolume5monthsago("chargebackvolume5monthsago"),
    chargebackvolume6monthsago("chargebackvolume6monthsago"),

    numberofchargebackslastmonth("numberofchargebackslastmonth"),
    numberofchargebacks2monthsago("numberofchargebacks2monthsago"),
    numberofchargebacks3monthsago("numberofchargebacks3monthsago"),
    numberofchargebacks4monthsago("numberofchargebacks4monthsago"),
    numberofchargebacks5monthsago("numberofchargebacks5monthsago"),
    numberofchargebacks6monthsago("numberofchargebacks6monthsago"),

    numberofrefundslastmonth("numberofrefundslastmonth"),
    numberofrefunds2monthsago("numberofrefunds2monthsago"),
    numberofrefunds3monthsago("numberofrefunds3monthsago"),
    numberofrefunds4monthsago("numberofrefunds4monthsago"),
    numberofrefunds5monthsago("numberofrefunds5monthsago"),
    numberofrefunds6monthsago("numberofrefunds6monthsago"),

    refundratiolastmonth("refundratiolastmonth"),
    refundratio2monthsago("refundratio2monthsago"),
    refundratio3monthsago("refundratio3monthsago"),
    refundratio4monthsago("refundratio4monthsago"),
    refundratio5monthsago("refundratio5monthsago"),
    refundratio6monthsago("refundratio6monthsago"),

    chargebackratiolastmonth("chargebackratiolastmonth"),
    chargebackratio2monthsago("chargebackratio2monthsago"),
    chargebackratio3monthsago("chargebackratio3monthsago"),
    chargebackratio4monthsago("chargebackratio4monthsago"),
    chargebackratio5monthsago("chargebackratio5monthsago"),
    chargebackratio6monthsago("chargebackratio6monthsago"),

    refundsvolumelastmonth("refundsvolumelastmonth"),
    refundsvolume2monthsago("refundsvolume2monthsago"),
    refundsvolume3monthsago("refundsvolume3monthsago"),
    refundsvolume4monthsago("refundsvolume4monthsago"),
    refundsvolume5monthsago("refundsvolume5monthsago"),
    refundsvolume6monthsago("refundsvolume6monthsago"),

    //PDF Fields
    MultipleTransactioncurrencies("MultipleTransactioncurrencies"),//Account currency
    ProductSoldCurrency_All("ProductSoldCurrency_All"),
    currencyTransferredbesides_EUR_USD_GBP_CAD("currencyTransferredbesides_EUR_USD_GBP_CAD"),
    currencyProductSoldbesides_EUR_USD_GBP_CAD("currencyProductSoldbesides_EUR_USD_GBP_CAD"),
    currencyProductSoldbesides_EUR_USD_GBP("currencyProductSoldbesides_EUR_USD_GBP"),
    currencyTransferredbesides_EUR_USD_GBP("currencyTransferredbesides_EUR_USD_GBP"),
    OtherTransactioncurrencies("OtherTransactioncurrencies"),
    OtherTransactioncurrencies2("OtherTransactioncurrencies2"),
    OtherProductSold_HKD_PEN_JPY_INR("OtherProductSold_HKD_PEN_JPY_INR"),
    AverageSalesVolume("AverageSalesVolume"),
    AverageNumberofTransaction("AverageNumberofTransaction"),
    Averagenumberofchargebacks("Averagenumberofchargebacks"),


    //CARDHOLDER DATA STORAGE COMPLIANCE

    complianceswapp("complianceswapp"),
    compliancethirdpartyappform("compliancethirdpartyappform"),
    compliancethirdpartysoft("compliancethirdpartysoft"),
    complianceversion("complianceversion"),
    compliancecompaniesorgateways("compliancecompaniesorgateways"),
    compliancecompaniesorgatewaysyes("compliancecompaniesorgatewaysyes"),
    complianceelectronically("complianceelectronically"),
    compliancecarddatastored("compliancecarddatastored"),
    compliancepcidsscompliant("compliancepcidsscompliant"),
    compliancequalifiedsecurityassessor("compliancequalifiedsecurityassessor"),
    compliancedateofcompliance("compliancedateofcompliance"),
    compliancedateoflastscan("compliancedateoflastscan"),
    compliancedatacompromise("compliancedatacompromise"),
    compliancedatacompromiseyes("compliancedatacompromiseyes"),

    //Site Inspection
    siteinspectionmerchant("siteinspectionmerchant"),
    siteinspectionlandlord("siteinspectionlandlord"),
    siteinspectionbuildingtype("siteinspectionbuildingtype"),
    siteinspectionareazoned("siteinspectionareazoned"),
    siteinspectionsquarefootage("siteinspectionsquarefootage"),
    siteinspectionoperatebusiness("siteinspectionoperatebusiness"),
    siteinspectionprincipal1("siteinspectionprincipal1"),
    siteinspectionprincipal1date("siteinspectionprincipal1date"),
    siteinspectionprincipal2("siteinspectionprincipal2"),
    siteinspectionprincipal2date("siteinspectionprincipal2date"),

    //EXTRA DETAILS PROFILE

    //Company Details
    ownerSince("ownerSince"),
    socialSecurity("socialSecurity"),
    companyformParticipation("companyformParticipation"),
    financialObligation("financialObligation"),
    workingExperience("workingExperience"),

    //Financial Reporting
    companyfinancialReport("companyfinancialReport"),
    companyfinancialReportYes("companyfinancialReportYes"),
    financialReportinstitution("financialReportinstitution"),
    financialReportavailable("financialReportavailable"),
    financialReportavailableYes("financialReportavailableYes"),

    //Legal Matters
    compliancepunitiveSanction("compliancepunitiveSanction"),
    compliancepunitiveSanctionYes("compliancepunitiveSanctionYes"),
    deedOfAgreement("deedOfAgreement"),
    deedOfAgreementYes("deedOfAgreementYes"),

    //Shipping Information
    goodsInsuranceOffered("goodsInsuranceOffered"),
    fulfillmentproductEmail("fulfillmentproductEmail"),
    fulfillmentproductEmailYes("fulfillmentproductEmailYes"),
    shipingdeliveryMethod("shipingdeliveryMethod"),

    //Account/Transaction Monitoring
    transactionMonitoringProcess("transactionMonitoringProcess"),
    operationalLicense("operationalLicense"),
    blacklistedAccountClosed("blacklistedAccountClosed"),
    blacklistedAccountClosedYes("blacklistedAccountClosedYes"),
    supervisorregularcontrole("supervisorregularcontrole");


    private String acroFieldName;
    private static Logger logger = new Logger(DefinedAcroFields.class.getName());
    DefinedAcroFields(String acroFieldName)
    {
        this.acroFieldName=acroFieldName;
    }

    public String toString()
    {
        return acroFieldName;
    }

    public static /*<E extends Enum<E>>*/ boolean isInEnum(String value)
    {
        try
        {
            DefinedAcroFields.valueOf(value);
            return true;
        } catch (IllegalArgumentException iae)
        {
            logger.error("IllegalArgumentException while checking the value for the enum in the Defined accroField:::::::::"+value,iae);
            return false;
        }
    }

    public static  DefinedAcroFields getEnum(String value)
    {
        try
        {
            return DefinedAcroFields.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            logger.error("IllegalArgumentException while checking the value for the enum in the Defined accroField:::::::::"+value,iae);
            return null;
        }
    }
}
