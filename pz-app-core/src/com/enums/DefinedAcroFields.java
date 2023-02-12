package com.enums;

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
    CompanyStreet("CompanyStreet"),
    CompanyZip ("CompanyZip"),
    Companyphonecc1("Companyphonecc1"),
    CompanyTelephoneNO("CompanyTelephoneNO"),
    CompanyFax("CompanyFax"),
    CompanyEmailAddress("CompanyEmailAddress"),
    CompanyCountryofRegistration("CompanyCountryofRegistration"),
    CompanyRegistrationNumber("CompanyRegistrationNumber"),
    CompanyDateOfRegistration("CompanyDateOfRegistration"),
    CompanyVat("CompanyVat"),
    CompanyAddressProof("CompanyAddressProof"),
    CompanyAddressId("CompanyAddressId"),
    FederalTaxID("FederalTaxID"),
    TypeofBusiness("TypeofBusiness"),   //radio button

    BusinessName("BusinessName"),//BusinessName
    BusinessAddress("BusinessAddress"),//BusinessName
    BusinessCity("BusinessCity"),//BusinessName
    BusinessState("BusinessState"),//BusinessName
    BusinessCountry("BusinessCountry"),//BusinessName
    BusinessStreet("BusinessStreet"),//BusinessName
    BusinessZip("BusinessZip"),//BusinessName
    BusinessAddressproof("BusinessAddressproof"),
    BusinessAddressId("BusinessAddressId"),

    RegisteredCorporateNameEU("RegisteredCorporateNameEU"),
    RegisteredDirectorsEU("RegisteredDirectorsEU"),
    RegisteredDirectorsAddressEU("RegisteredDirectorsAddressEU"),
    RegisteredDirectorsCity("RegisteredDirectorsCity"),
    RegisteredDirectorsState("RegisteredDirectorsState"),
    RegisteredDirectorsCountry("RegisteredDirectorsCountry"),
    RegisteredDirectorsStreet("RegisteredDirectorsStreet"),
    RegisteredDirectorsZip("RegisteredDirectorsZip"),
    RegisteredDirectorsAddressproof("RegisteredDirectorsAddressproof"),
    RegisteredDirectorsAddressId("RegisteredDirectorsAddressId"),
    EURegistrationNumber("EURegistrationNumber"),
    paymentTypeYes("paymentTypeYes"),

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
    contactDesignation("contactDesignation"),
    TechnicalContactName("TechnicalContactName"),
    TechnicalEmailAddress("TechnicalEmailAddress"),
    technicalphonecc1("technicalphonecc1"),
    TechnicalTelephoneNumber("TechnicalTelephoneNumber"),
    TechnicalDesignation("TechnicalDesignation"),
    FinancialContactName("FinancialContactName"),
    FinancialEmailAddress("FinancialEmailAddress"),
    financialphonecc1("financialphonecc1"),
    FinancialTelephoneNumber("FinancialTelephoneNumber"),
    FinanacialDesignation("FinanacialDesignation"),

    cbkContactPerson("cbkContactPerson"),
    cbkEmail("cbkEmail"),
    cbkTelNumber("cbkTelNumber"),
    cbkPhonecc("cbkPhonecc"),
    cbkDesignation("cbkDesignation"),
    pciContactPerson("pciContactPerson"),
    pciEmail("pciEmail"),
    pciTelNumber("pciTelNumber"),
    pciPhonecc("pciPhonecc"),
    pciDesignation("pciDesignation"),
    iscompanyInsured("iscompanyInsured"),
    insuredCompanyName("insuredCompanyName"),
    insured_currency("insured_currency"),
    insured_amount("insured_amount"),
    main_business_partner("main_business_partner"),
    loans("loans"),
    income_economic_activity("income_economic_activity"),
    interest_income("interest_income"),
    investments("investments"),
    income_sources_other("income_sources_other"),
    income_sources_other_yes("income_sources_other_yes"),

    Bankruptcy("Bankruptcy"),    //radio button
    companybankruptcydate("companybankruptcydate"),
    Licenserequired("Licenserequired"),  //radio button
    LicensePermission("LicensePermission"), //radio button
    legalProceeding("legalProceeding"),//Radio button
    legalProceedingTxt("legalProceedingTxt"),//Radio button
    startupBusiness("startupBusiness"),//Radio button


    //Ownership Profile
    numOfShareholders("numOfShareholders"),
    numOfCorporateShareholders("numOfCorporateShareholders"),
    numOfDirectors("numOfDirectors"),
    numOfAuthrisedSignatory("numOfAuthrisedSignatory"),

    politicallyExposed1("politicallyExposed1"),
    criminalRecord1("criminalRecord1"),

    politicallyExposed2("politicallyExposed2"),
    criminalRecord2("criminalRecord2"),

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
    shareholderprofile1street("shareholderprofile1street"),
    shareholderprofile1telnocc1("shareholderprofile1telnocc1"),
    shareholderprofile1telephonenumber("shareholderprofile1telephonenumber"),
    shareholderprofile1emailaddress("shareholderprofile1emailaddress"),
    shareholderprofile1dateofbirth("shareholderprofile1dateofbirth"),
    shareholderprofile1identificationtypeselect("shareholderprofile1identificationtypeselect"),
    shareholderprofile1identificationtype("shareholderprofile1identificationtype"),
    shareholderprofile1nationality("shareholderprofile1nationality"),
    isShareholderProfileNationalityUS("isShareholderProfileNationalityUS"),
    shareholderprofile1Passportexpirydate("shareholderprofile1Passportexpirydate"),
    shareholderprofile1Passportissuedate("shareholderprofile1Passportissuedate"),
    shareholderprofile1Politicallyexposed("shareholderprofile1Politicallyexposed"),
    shareholderprofile1Criminalrecord("shareholderprofile1Criminalrecord"),
    shareholderprofile1addressproof("shareholderprofile1addressproof"),
    shareholderprofile1addressId("shareholderprofile1addressId"),
    shareholderprofile1IdentificationTypeForPassport("shareholderprofile1IdentificationTypeForPassport"),
    shareholderprofile2IdentificationTypeForPassport("shareholderprofile2IdentificationTypeForPassport"),
    directorsprofile1IdentificationTypeForPassport("directorsprofile1IdentificationTypeForPassport"),
    directorsprofile2IdentificationTypeForPassport("directorsprofile2IdentificationTypeForPassport"),

    //Individual Shareholder 2
    shareholderprofile2("shareholderprofile2"),
    shareholderprofile2lastname("shareholderprofile2lastname"),
    shareholderprofile2title("shareholderprofile2title"),
    shareholderprofile2State("shareholderprofile2State"),
    shareholderprofile2address("shareholderprofile2address"),
    shareholderprofile2city("shareholderprofile2city"),
    shareholderprofile2zip("shareholderprofile2zip"),
    shareholderprofile2country("shareholderprofile2country"),
    shareholderprofile2street("shareholderprofile2street"),
    shareholderprofile2owned("shareholderprofile2owned"),
    shareholderprofile2telnocc2("shareholderprofile2telnocc2"),
    shareholderprofile2telephonenumber("shareholderprofile2telephonenumber"),
    shareholderprofile2emailaddress("shareholderprofile2emailaddress"),
    shareholderprofile2dateofbirth("shareholderprofile2dateofbirth"),
    shareholderprofile2identificationtypeselect("shareholderprofile2identificationtypeselect"),
    shareholderprofile2identificationtype("shareholderprofile2identificationtype"),
    shareholderprofile2nationality("shareholderprofile2nationality"),
    shareholderprofile2Passportexpirydate("shareholderprofile2Passportexpirydate"),
    shareholderprofile2Passportissuedate("shareholderprofile2Passportissuedate"),
    shareholderprofile2Politicallyexposed("shareholderprofile2Politicallyexposed"),
    shareholderprofile2Criminalrecord("shareholderprofile2Criminalrecord"),
    shareholderprofile2addressproof("shareholderprofile2addressproof"),
    shareholderprofile2addressId("shareholderprofile2addressId"),

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
    shareholderprofile3street("shareholderprofile3street"),
    shareholderprofile3telnocc2("shareholderprofile3telnocc2"),
    shareholderprofile3telephonenumber("shareholderprofile3telephonenumber"),
    shareholderprofile3emailaddress("shareholderprofile3emailaddress"),
    shareholderprofile3dateofbirth("shareholderprofile3dateofbirth"),
    shareholderprofile3identificationtypeselect("shareholderprofile3identificationtypeselect"),
    shareholderprofile3identificationtype("shareholderprofile3identificationtype"),
    shareholderprofile3nationality("shareholderprofile3nationality"),
    shareholderprofile3Passportexpirydate("shareholderprofile3Passportexpirydate"),
    shareholderprofile3Passportissuedate("shareholderprofile3Passportissuedate"),
    shareholderprofile3Politicallyexposed("shareholderprofile3Politicallyexposed"),
    shareholderprofile3Criminalrecord("shareholderprofile3Criminalrecord"),
    shareholderprofile3addressproof("shareholderprofile3addressproof"),
    shareholderprofile3addressId("shareholderprofile3addressId"),

    // Corporate Shareholder 1
    corporateshareholder1Name("corporateshareholder1Name"),
    corporateshareholder1RegNumber("corporateshareholder1RegNumber"),
    corporateshareholder1DateofRegistration("corporateshareholder1DateofRegistration"),
    corporateshareholder1Address("corporateshareholder1Address"),
    corporateshareholder1City("corporateshareholder1City"),
    corporateshareholder1State("corporateshareholder1State"),
    corporateshareholder1ZipCode("corporateshareholder1ZipCode"),
    corporateshareholder1Country("corporateshareholder1Country"),
    corporateshareholder1Street("corporateshareholder1Street"),
    corporateshareholder1Owned("corporateshareholder1_owned"),
    corporateshareholder1addressproof("corporateshareholder1addressproof"),
    corporateshareholder1addressId("corporateshareholder1addressId"),
    corporateshareholder1identificationtypeselect("corporateshareholder1identificationtypeselect"),
    corporateshareholder1identificationtype("corporateshareholder1identificationtype"),

    // Corporate Shareholder 2
    corporateshareholder2Name("corporateshareholder2Name"),
    corporateshareholder2RegNumber("corporateshareholder2RegNumber"),
    corporateshareholder2DateofRegistration("corporateshareholder2DateofRegistration"),
    corporateshareholder2Address("corporateshareholder2Address"),
    corporateshareholder2City("corporateshareholder2City"),
    corporateshareholder2State("corporateshareholder2State"),
    corporateshareholder2ZipCode("corporateshareholder2ZipCode"),
    corporateshareholder2Country("corporateshareholder2Country"),
    corporateshareholder2Street("corporateshareholder2Street"),
    corporateshareholder2Owned("corporateshareholder2owned"),
    corporateshareholder2addressproof("corporateshareholder2addressproof"),
    corporateshareholder2addressId("corporateshareholder2addressId"),
    corporateshareholder2identificationtypeselect("corporateshareholder2identificationtypeselect"),
    corporateshareholder2identificationtype("corporateshareholder2identificationtype"),

    // Corporate Shareholder 3
    corporateshareholder3Name("corporateshareholder3Name"),
    corporateshareholder3RegNumber("corporateshareholder3RegNumber"),
    corporateshareholder3DateofRegistration("corporateshareholder3DateofRegistration"),
    corporateshareholder3Address("corporateshareholder3Address"),
    corporateshareholder3City("corporateshareholder3City"),
    corporateshareholder3State("corporateshareholder3State"),
    corporateshareholder3ZipCode("corporateshareholder3ZipCode"),
    corporateshareholder3Country("corporateshareholder3Country"),
    corporateshareholder3Street("corporateshareholder3Street"),
    corporateshareholder3Owned("corporateshareholder3_owned"),
    corporateshareholder3addressproof("corporateshareholder3addressproof"),
    corporateshareholder3addressId("corporateshareholder3addressId"),
    corporateshareholder3identificationtypeselect("corporateshareholder3identificationtypeselect"),
    corporateshareholder3identificationtype("corporateshareholder3identificationtype"),

    //Director 1
    directorsprofile("directorsprofile"),
    directorsprofilelastname("directorsprofilelastname"),
    directorsprofiletitle("directorsprofiletitle"),
    directorsprofileState("directorsprofileState"),
    directorsprofileaddress("directorsprofileaddress"),
    directorsprofilecity("directorsprofilecity"),
    directorsprofilezip("directorsprofilezip"),
    directorsprofilecountry("directorsprofilecountry"),
    directorsprofilestreet("directorsprofilestreet"),
    directorsprofiletelnocc1("directorsprofiletelnocc1"),
    directorsprofiletelephonenumber("directorsprofiletelephonenumber"),
    directorsprofileemailaddress("directorsprofileemailaddress"),
    directorsprofiledateofbirth("directorsprofiledateofbirth"),
    directorsprofileidentificationtypeselect("directorsprofileidentificationtypeselect"),
    directorsprofileidentificationtype("directorsprofileidentificationtype"),
    directorsprofilenationality("directorsprofilenationality"),
    directorsprofilePassportexpirydate("directorsprofilePassportexpirydate"),
    directorsprofilePassportissuedate("directorsprofilePassportissuedate"),
    isDirectorsProfileNationalityUS("isDirectorsProfileNationalityUS"),
    isNameprinciPal1natioNalityUS("isNameprinciPal1natioNalityUS"),
    directorsprofile_politicallyexposed("directorsprofile_politicallyexposed"),
    directorsprofile_criminalrecord("directorsprofile_criminalrecord"),
    directorsprofileaddressproof("directorsprofileaddressproof"),
    directorsprofileaddressId("directorsprofileaddressId"),
    directorsprofileowned("directorsprofileowned"),

    //Director 2
    directorsprofile2("directorsprofile2"),
    directorsprofile2lastname("directorsprofile2lastname"),
    directorsprofile2title("directorsprofile2title"),
    directorsprofile2State("directorsprofile2State"),
    directorsprofile2address("directorsprofile2address"),
    directorsprofile2city("directorsprofile2city"),
    directorsprofile2zip ("directorsprofile2zip"),
    directorsprofile2country("directorsprofile2country"),
    directorsprofile2street("directorsprofile2street"),
    directorsprofile2telnocc1("directorsprofile2telnocc1"),
    directorsprofile2telephonenumber("directorsprofile2telephonenumber"),
    directorsprofile2emailaddress("directorsprofile2emailaddress"),
    directorsprofile2dateofbirth("directorsprofile2dateofbirth"),
    directorsprofile2identificationtype("directorsprofileidentificationtype"),
    directorsprofile2identificationtypeselect("directorsprofile2identificationtypeselect"),
    directorsprofile2nationality("directorsprofile2nationality"),
    directorsprofile2Passportexpirydate("directorsprofile2Passportexpirydate"),
    directorsprofile2Passportissuedate("directorsprofile2Passportissuedate"),
    directorsprofile2_politicallyexposed("directorsprofile2_politicallyexposed"),
    directorsprofile2_criminalrecord("directorsprofile2_criminalrecord"),
    directorsprofile2addressproof("directorsprofile2addressproof"),
    directorsprofile2addressId("directorsprofile2addressId"),
    directorsprofile2owned("directorsprofile2owned"),

    //Director 3
    directorsprofile3("directorsprofile3"),
    directorsprofile3lastname("directorsprofile3lastname"),
    directorsprofile3title("directorsprofile3title"),
    directorsprofile3address("directorsprofile3address"),
    directorsprofile3city("directorsprofile3city"),
    directorsprofile3State("directorsprofile3State"),
    directorsprofile3zip("directorsprofile3zip"),
    directorsprofile3country("directorsprofile3country"),
    directorsprofile3street("directorsprofile3street"),
    directorsprofile3telnocc1("directorsprofile3telnocc1"),
    directorsprofile3telephonenumber("directorsprofile3telephonenumber"),
    directorsprofile3emailaddress("directorsprofile3emailaddress"),
    directorsprofile3dateofbirth("directorsprofile3dateofbirth"),
    directorsprofile3identificationtype("directorsprofile3identificationtype"),
    directorsprofile3identificationtypeselect("directorsprofile3identificationtypeselect"),
    directorsprofile3nationality("directorsprofile3nationality"),
    directorsprofile3Passportexpirydate("directorsprofile3Passportexpirydate"),
    directorsprofile3Passportissuedate("directorsprofile3Passportissuedate"),
    directorsprofile3_politicallyexposed("directorsprofile3_politicallyexposed"),
    directorsprofile3_criminalrecord("directorsprofile3_criminalrecord"),
    directorsprofile3addressproof("directorsprofile3addressproof"),
    directorsprofile3addressId("directorsprofile3addressId"),
    directorsprofile3owned("directorsprofile3owned"),

    //Authorized signatory 1
    authorizedsignatoryprofile("authorizedsignatoryprofile"),
    authorizedsignatoryprofilelastname("authorizedsignatoryprofilelastname"),
    authorizedsignatoryprofiletitle("authorizedsignatoryprofiletitle"),
    authorizedsignatoryprofileState("authorizedsignatoryprofileState"),
    authorizedsignatoryprofileaddress("authorizedsignatoryprofileaddress"),
    authorizedsignatoryprofilecity("authorizedsignatoryprofilecity"),
    authorizedsignatoryprofilezip("authorizedsignatoryprofilezip"),
    authorizedsignatoryprofilecountry("authorizedsignatoryprofilecountry"),
    authorizedsignatoryprofilestreet("authorizedsignatoryprofilestreet"),
    authorizedsignatoryprofiletelnocc1("authorizedsignatoryprofiletelnocc1"),
    authorizedsignatoryprofiletelephonenumber("authorizedsignatoryprofiletelephonenumber"),
    authorizedsignatoryprofileemailaddress("authorizedsignatoryprofileemailaddress"),
    authorizedsignatoryprofile1Designation("authorizedsignatoryprofile1Designation"),
    authorizedsignatoryprofiledateofbirth("authorizedsignatoryprofiledateofbirth"),
    authorizedsignatoryprofileidentificationtypeselect("authorizedsignatoryprofileidentificationtypeselect"),
    authorizedsignatoryprofileidentificationtype("authorizedsignatoryprofileidentificationtype"),
    authorizedsignatoryprofilenationality("authorizedsignatoryprofilenationality"),
    authorizedsignatoryprofilePassportexpirydate("authorizedsignatoryprofilePassportexpirydate"),
    authorizedsignatoryprofilePassportissuedate("authorizedsignatoryprofilePassportissuedate"),
    authorizedsignatoryprofile1Politicallyexposed("authorizedsignatoryprofile1Politicallyexposed"),
    authorizedsignatoryprofile1Criminalrecord("authorizedsignatoryprofile1Criminalrecord"),
    authorizedsignatoryprofile1addressproof("authorizedsignatoryprofile1addressproof"),
    authorizedsignatoryprofile1addressId("authorizedsignatoryprofile1addressId"),
    authorizedsignatoryprofile1owned("authorizedsignatoryprofile1owned"),


    //Authorized signatory 2
    authorizedsignatoryprofile2("authorizedsignatoryprofile2"),
    authorizedsignatoryprofile2lastname("authorizedsignatoryprofile2lastname"),
    authorizedsignatoryprofile2title("authorizedsignatoryprofile2title"),
    authorizedsignatoryprofile2State("authorizedsignatoryprofile2State"),
    authorizedsignatoryprofile2address("authorizedsignatoryprofile2address"),
    authorizedsignatoryprofile2city("authorizedsignatoryprofile2city"),
    authorizedsignatoryprofile2zip("authorizedsignatoryprofile2zip"),
    authorizedsignatoryprofile2country("authorizedsignatoryprofile2country"),
    authorizedsignatoryprofile2street("authorizedsignatoryprofile2street"),
    authorizedsignatoryprofile2telnocc2("authorizedsignatoryprofile2telnocc2"),
    authorizedsignatoryprofile2telephonenumber("authorizedsignatoryprofile2telephonenumber"),
    authorizedsignatoryprofile2emailaddress("authorizedsignatoryprofile2emailaddress"),
    authorizedsignatoryprofile2Designation("authorizedsignatoryprofile2Designation"),
    authorizedsignatoryprofile2dateofbirth("authorizedsignatoryprofile2dateofbirth"),
    authorizedsignatoryprofile2identificationtypeselect("authorizedsignatoryprofile2identificationtypeselect"),
    authorizedsignatoryprofile2identificationtype("authorizedsignatoryprofile2identificationtype"),
    authorizedsignatoryprofile2nationality("authorizedsignatoryprofile2nationality"),
    authorizedsignatoryprofile2Passportexpirydate("authorizedsignatoryprofile2Passportexpirydate"),
    authorizedsignatoryprofile2Passportissuedate("authorizedsignatoryprofile2Passportissuedate"),
    authorizedsignatoryprofile2Politicallyexposed("authorizedsignatoryprofile2Politicallyexposed"),
    authorizedsignatoryprofile2Criminalrecord("authorizedsignatoryprofile2Criminalrecord"),
    authorizedsignatoryprofile2addressproof("authorizedsignatoryprofile2addressproof"),
    authorizedsignatoryprofile2addressId("authorizedsignatoryprofile2addressId"),
    authorizedsignatoryprofile2owned("authorizedsignatoryprofile2owned"),

    //Authorized signatory 3
    authorizedsignatoryprofile3("authorizedsignatoryprofile3"),
    authorizedsignatoryprofile3lastname("authorizedsignatoryprofile3lastname"),
    authorizedsignatoryprofile3title("authorizedsignatoryprofile3title"),
    authorizedsignatoryprofile3State("authorizedsignatoryprofile3State"),
    authorizedsignatoryprofile3address("authorizedsignatoryprofile3address"),
    authorizedsignatoryprofile3city("authorizedsignatoryprofile3city"),
    authorizedsignatoryprofile3zip("authorizedsignatoryprofile3zip"),
    authorizedsignatoryprofile3country("authorizedsignatoryprofile3country"),
    authorizedsignatoryprofile3street("authorizedsignatoryprofile3street"),
    authorizedsignatoryprofile3telnocc2("authorizedsignatoryprofile3telnocc2"),
    authorizedsignatoryprofile3telephonenumber("authorizedsignatoryprofile3telephonenumber"),
    authorizedsignatoryprofile3emailaddress("authorizedsignatoryprofile3emailaddress"),
    authorizedsignatoryprofile3Designation("authorizedsignatoryprofile3Designation"),
    authorizedsignatoryprofile3dateofbirth("authorizedsignatoryprofile3dateofbirth"),
    authorizedsignatoryprofile3identificationtypeselect("authorizedsignatoryprofile3identificationtypeselect"),
    authorizedsignatoryprofile3identificationtype("authorizedsignatoryprofile3identificationtype"),
    authorizedsignatoryprofile3nationality("authorizedsignatoryprofile3nationality"),
    authorizedsignatoryprofile3Passportexpirydate("authorizedsignatoryprofile3Passportexpirydate"),
    authorizedsignatoryprofile3Passportissuedate("authorizedsignatoryprofile3Passportissuedate"),
    authorizedsignatoryprofile3Politicallyexposed("authorizedsignatoryprofile3Politicallyexposed"),
    authorizedsignatoryprofile3Criminalrecord("authorizedsignatoryprofile3Criminalrecord"),
    authorizedsignatoryprofile3addressproof("authorizedsignatoryprofile3addressproof"),
    authorizedsignatoryprofile3addressId("authorizedsignatoryprofile3addressId"),
    authorizedsignatoryprofile3owned("authorizedsignatoryprofile3owned"),

    //Business Profile
    urls("urls"),
    descriptorcreditcardstmt("descriptorcreditcardstmt"),
    ipaddress("ipaddress"),
    loginId("loginId"),
    passWord("passWord"),
    is_website_live("is_website_live"),
    test_link("test_link"),

    methodofacceptancemoto("methodofacceptancemoto"),
    methodofacceptanceinternet("methodofacceptanceinternet"),
    methodofacceptanceswipe("methodofacceptanceswipe"),
    methodofacceptancemotordbtn("methodofacceptancemotordbtn"),
    methodofacceptanceinternetrdbtn("methodofacceptanceinternetrdbtn"),

    averageticket("averageticket"),
    highestticket("highestticket"),
    lowestticket("lowestticket"),
    tenPerOfAvgTicket("tenPerOfAvgTicket"),
    transactionVolume("transactionVolume"),
    tenPerOfTransactionVolume("tenPerOfTransactionVolume"),

    ForeignTransactionText("ForeignTransactionText"),
    foreigntransactionsus("foreigntransactionsus"),
    foreigntransactionsEurope("foreigntransactionsEurope"),
    foreigntransactionsAsia("foreigntransactionsAsia"),
    foreigntransactionscis("foreigntransactionscis"),
    foreigntransactionscanada("foreigntransactionscanada"),
    foreigntransactionsuk("foreigntransactionsuk"),
    foreigntransactionsRestoftheWorld("foreigntransactionsRestoftheWorld"),
    foreigntransactionsRestoftheWorldForTW("foreigntransactionsRestoftheWorldForTW"),
    foreigntransactionsCountryName("foreigntransactionsCountryName"),
    //ForeignTransactionText("ForeignTransactionText"),

    monthlyCardVolumeForTW("monthlyCardVolumeForTW"),

    cardtypesacceptedvisa("cardtypesacceptedvisa"),
    cardtypesacceptedmastercard("cardtypesacceptedmastercard"),
    cardtypesacceptedamericanexpress("cardtypesacceptedamericanexpress"),
    cardtypesaccepteddiscover("cardtypesaccepteddiscover"),
    cardtypesaccepteddiners("cardtypesaccepteddiners"),
    cardtypesacceptedjcb("cardtypesacceptedjcb"),
    cardtypesacceptedrupay("cardtypesacceptedrupay"),
    cardtypesacceptedother("cardtypesacceptedother"),
    cardtypesacceptedotheryes("cardtypesacceptedotheryes"),

    descriptionofproducts("descriptionofproducts"),
    productSoldCurrencies("productSoldCurrencies"),
    recurringservices("recurringservices"),  //radio button
    recurringservicesyes("recurringservicesyes"),
    isacallcenterused("isacallcenterused"),      //radio button
    isacallcenterusedyes("isacallcenterusedyes"),
    isafulfillmenthouseused("isafulfillmenthouseused"),   //radio button
    isafulfillmenthouseusedyes("isafulfillmenthouseusedyes"),

    //Website Review
    visacardlogos("visacardlogos"),
    mastercardlogos("mastercardlogos"),
    pricedisplayed("pricedisplayed"),
    companyIdentifiable("companyIdentifiable"),
    clearlyPresented("clearlyPresented"),
    clearlyPresented1("clearlyPresented1"),
    clearlyPresented2("clearlyPresented2"),
    trackingNumber("trackingNumber"),
    domainsOwned("domainsOwned"),
    domainsOwned_no("domainsOwned_no"),
    cardholderasked("cardholderasked"),
    threeDsecurecompulsory("threeDsecurecompulsory"),
    transactioncurrency("transactioncurrency"),
    dynamicdescriptors("dynamicdescriptors"),
    shoppingcart("shoppingcart"),
    shoppingcartdetails("shoppingcartdetails"),

    //Shipping and Returns
    pricingpolicieswebsite("pricingpolicieswebsite"),
    pricingpolicieswebsite_yes("pricingpolicieswebsite_yes"),
    pricingpolicieswebsite2("pricingpolicieswebsite2"),
    fulfillmenttimeframe("fulfillmenttimeframe"),
    goodspolicy("goodspolicy"),
    goodspolicynew("goodspolicynew"),
    countriesblocked("countriesblocked"), //radio button
    countriesblockeddetails("countriesblockeddetails"),
    inHouseLocation("inHouseLocation"),
    ifInHouseLocation("ifInHouseLocation"),
    contactPerson("contactPerson"),
    otherLocation("otherLocation"),
    warehouseLocation("warehouseLocation"),
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
    listfraudtools_yes("listfraudtools_yes"),
    listfraudtools_yes2("listfraudtools_yes2"),
    applicationservices("applicationservices"),//new field  radio button
    agency_employed("agency_employed"),
    agency_employed_yes("agency_employed_yes"),

    //Customer service Information
    customersupport("customersupport"),   //radio button
    customersupportemail("customersupportemail"),
    customersupportemailrdbtn("customersupportemailrdbtn"),
    custsupportworkhours("custsupportworkhours"),
    technicalcontact("technicalcontact"),
    technicalcontactrdbtn("technicalcontactrdbtn"),
    timeframe("timeframe"),
    livechat("livechat"),

    //Other Information
    //MCCCtegory("MCCCtegory"),         //end radio button
    merchantCode("merchantCode"),
    coolingoffperiod("coolingoffperiod"),
    isCustomersidentification("isCustomersidentification"),
    customersidentification_yes("customersidentification_yes"),
    customersidentification("customersidentification"),
    directMail("directMail"),
    yellowPages("yellowPages"),
    radioTv("radioTv"),
    internet("internet"),
    networking("networking"),
    outboundTelemarketing("outboundTelemarketing"),

    //Billing model/Subscriptions
    billingModel("billingModel"),
    isRecurringBilling("isRecurringBilling"),
    billingModelText("billingModelText"),
    billingTimeFrame("billingTimeFrame"),
    recurringAmount("recurringAmount"),
    automaticRecurring("automaticRecurring"),
    multipleMembership("multipleMembership"),
    freeMembership("freeMembership"),
    creditCardRequired("creditCardRequired"),
    automaticallyBilled("automaticallyBilled"),
    preAuthorization("preAuthorization"),

    // Wirecard requirement added in Business Profile
    shopsystemPlugin("shopsystemPlugin"),
    directDebitSepa("directDebitSepa"),
    alternativePayments("alternativePayments"),
    riskManagement("riskManagement"),
    isRiskManagement("isRiskManagement"),
    paymentEngine("paymentEngine"),
    webhostCompanyName("webhostCompanyName"),
    webhostPhone("webhostPhone"),
    webhostEmail("webhostEmail"),
    webhostWebsite("webhostWebsite"),
    webhostAddress("webhostAddress"),
    paymentCompanyName("paymentCompanyName"),
    paymentPhone("paymentPhone"),
    paymentEmail("paymentEmail"),
    paymentWebsite("paymentWebsite"),
    paymentAddress("paymentAddress"),
    callcenterPhone("callcenterPhone"),
    callcenterEmail("callcenterEmail"),
    callcenterWebsite("callcenterWebsite"),
    callcenterAddress("callcenterAddress"),
    shoppingcartCompanyName("shoppingcartCompanyName"),
    shoppingcartPhone("shoppingcartPhone"),
    shoppingcartEmail("shoppingcartEmail"),
    shoppingcartWebsite("shoppingcartWebsite"),
    shoppingcartAddress("shoppingcartAddress"),
    seasonalFluctuating("seasonalFluctuating"),
    seasonalFluctuatingyes("seasonalFluctuatingyes"),
    paymentTypeCredit("paymentTypeCredit"),
    paymentTypeDebit("paymentTypeDebit"),
    paymentTypeNetbanking("paymentTypeNetbanking"),
    paymentTypeWallet("paymentTypeWallet"),
    paymentTypeAlternate("paymentTypeAlternate"),
    creditorId("creditorId"),
    paymentDelivery("paymentDelivery"),
    paymentDelivery_UponPurchase("paymentDelivery_UponPurchase"),
    paymentDelivery_OnDelivery("paymentDelivery_OnDelivery"),
    paymentDelivery_Download("paymentDelivery_Download"),
    paymentDelivery_otheryes("paymentDelivery_otheryes"),
    paymentDelivery_other("paymentDelivery_other"),
    goodsDelivery("goodsDelivery"),
    terminalType("terminalType"),
    terminalTypeOtherYes("terminalTypeOtherYes"),
    terminalTypeOther("terminalTypeOther"),
    oneTimePercentage("oneTimePercentage"),
    motoPercentage("motoPercentage"),
    internetPercentage("internetPercentage"),
    swipePercentage("swipePercentage"),
    recurringPercentage("recurringPercentage"),
    threedsecurePercentage("threedsecurePercentage"),
    totalChannelType("totalChannelType"),
    TerminalTypeAccepted("TerminalTypeAccepted"),

    cardvolumeVisa("cardvolumeVisa"),
    cardvolumeMasterCard("cardvolumeMasterCard"),
    expectedCardvolumeVisa("expectedCardvolumeVisa"),
    expectedCardvolumeMasterCard("expectedCardvolumeMasterCard"),
    cardvolumeAmericanExpress("cardvolumeAmericanExpress"),
    cardvolumeDinner("cardvolumeDinner"),
    cardvolumeOther("cardvolumeOther"),
    cardvolumeDiscover("cardvolumeDiscover"),
    cardvolumeRupay("cardvolumeRupay"),
    cardvolumeJCB("cardvolumeJCB"),
    cardvolumeVisaChk("cardvolumeVisaChk"),
    cardvolumeMasterCardChk("cardvolumeMasterCardChk"),
    cardvolume_AmericanExpress_Dinner_Discover_Other("cardvolume_AmericanExpress_Dinner_Discover_Other"),
    orderconfirmationPost("orderconfirmationPost"),
    orderconfirmationEmail("orderconfirmationEmail"),
    orderconfirmationSms("orderconfirmationSms"),
    orderconfirmationOther("orderconfirmationOther"),
    orderconfirmationOtherYes("orderconfirmationOtherYes"),
    orderconfirmationSMSOtherYes("orderconfirmationSMSOtherYes"),
    physicalGoodsDelivered("physicalGoodsDelivered"),
    viaInternetGoodsDelivered("viaInternetGoodsDelivered"),
    shippingContactEmail("shippingContactEmail"),

    // BANK PDF Fields
    ForeignTransaction("ForeignTransaction"),
    ForeignTransaction2("ForeignTransaction2"),
    ForeignTransaction_besidesAsia_Europe_US_other("ForeignTransaction_besidesAsia_Europe_US_other"),
    ForeignTransaction_All("ForeignTransaction_All"),
    ForeignTransaction_US_Canada("ForeignTransaction_US_Canada"),
    foreigntransactionsUKRestoftheWord("foreigntransactionsUKRestoftheWord"),
    ForeignTransactionCIS_CANADA_UK_ROW("ForeignTransactionCIS_CANADA_UK_ROW"),
    totalMarketCoverage("totalMarketCoverage"),
    cardtypesaccepted_all("cardtypesaccepted_all"),
    cardtypeaccepted_besidesVisaMasterCardDinners("cardtypeaccepted_besidesVisaMasterCardDinners"),
    CardTypeAcceptedBesides_Visa_MC("CardTypeAcceptedBesides_Visa_MC"),
    CardTypeAcceptedVisaText("CardTypeAcceptedVisaText"),//
    CardTypeAcceptedMasterCardText("CardTypeAcceptedMasterCardText"),
    MarketStrategy_All("MarketStrategy_All"),
    isCardTypeAcceptedMC("isCardTypeAcceptedMC"),
    isCardTypeAcceptedVISA("isCardTypeAcceptedVISA"),
    isCardTypeAcceptedMC2("isCardTypeAcceptedMC2"),
    isCardTypeAcceptedVISA2("isCardTypeAcceptedVISA2"),
    TerminalTypeAll("TerminalTypeAll"),
    PaymentTypeAll("PaymentTypeAll"),

    //Bank profile
    //Currency Requested
    //bank_account_currencies("bank_account_currencies"),
    currency_ProcessingHistory("currency_ProcessingHistory"),
    bankAccountCurrencies("bankAccountCurrencies"),
    product_sold_currencies("product_sold_currencies"),
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
    bankinfocontactperson("bankinfocontactperson"),
    bankinfoabaroutingcode("bankinfoabaroutingcode"),
    bankinfoaccountholder("bankinfoaccountholder"),
    bankinfoaccountnumber("bankinfoaccountnumber"),
    bankinfoIBAN("bankinfoIBAN"),
    bankinfocurrency("bankinfocurrency"),

    shippingBreaking("shippingBreaking"),

    //Aquirer Details
    aquirer("aquirer"),
    isAquirer("isAquirer"),
    reasonaquirer("reasonaquirer"),
    compliancecispcompliant("compliancecispcompliant"),
    compliancecispcompliantYes("compliancecispcompliantYes"),

    salesvolumelastmonth("salesvolumelastmonth"),
    salesvolume2monthsago("salesvolume2monthsago"),
    salesvolume3monthsago("salesvolume3monthsago"),
    salesvolume4monthsago("salesvolume4monthsago"),
    salesvolume5monthsago("salesvolume5monthsago"),
    salesvolume6monthsago("salesvolume6monthsago"),
    salesvolume12monthsago("salesvolume12monthsago"),
    salesvolumeyear2("salesvolumeyear2"),
    salesvolumeyear3("salesvolumeyear3"),
    processingFromDate("processingFromDate"),
    processingToDate("processingToDate"),

    numberoftransactionslastmonth("numberoftransactionslastmonth"),
    numberoftransactions2monthsago("numberoftransactions2monthsago"),
    numberoftransactions3monthsago("numberoftransactions3monthsago"),
    numberoftransactions4monthsago("numberoftransactions4monthsago"),
    numberoftransactions5monthsago("numberoftransactions5monthsago"),
    numberoftransactions6monthsago("numberoftransactions6monthsago"),
    numberoftransactions12monthsago("numberoftransactions12monthsago"),
    numberoftransactionsyear2("numberoftransactionsyear2"),
    numberoftransactionsyear3("numberoftransactionsyear3"),
    avgNumberOfTransaction("avgNumberOfTransaction"),
    avgExpectedNumberOfTrans("avgExpectedNumberOfTrans"),

    chargebackvolumelastmonth("chargebackvolumelastmonth"),
    chargebackvolume2monthsago("chargebackvolume2monthsago"),
    chargebackvolume3monthsago("chargebackvolume3monthsago"),
    chargebackvolume4monthsago("chargebackvolume4monthsago"),
    chargebackvolume5monthsago("chargebackvolume5monthsago"),
    chargebackvolume6monthsago("chargebackvolume6monthsago"),
    chargebackvolume12monthsago("chargebackvolume12monthsago"),
    chargebackvolumeyear2("chargebackvolumeyear2"),
    chargebackvolumeyear3("chargebackvolumeyear3"),
    avgChargebackRatio("avgChargebackRatio"),

    numberofchargebackslastmonth("numberofchargebackslastmonth"),
    numberofchargebacks2monthsago("numberofchargebacks2monthsago"),
    numberofchargebacks3monthsago("numberofchargebacks3monthsago"),
    numberofchargebacks4monthsago("numberofchargebacks4monthsago"),
    numberofchargebacks5monthsago("numberofchargebacks5monthsago"),
    numberofchargebacks6monthsago("numberofchargebacks6monthsago"),
    numberofchargebacks12monthsago("numberofchargebacks12monthsago"),
    numberofchargebacksyear2("numberofchargebacksyear2"),
    numberofchargebacksyear3("numberofchargebacksyear3"),

    numberofrefundslastmonth("numberofrefundslastmonth"),
    numberofrefunds2monthsago("numberofrefunds2monthsago"),
    numberofrefunds3monthsago("numberofrefunds3monthsago"),
    numberofrefunds4monthsago("numberofrefunds4monthsago"),
    numberofrefunds5monthsago("numberofrefunds5monthsago"),
    numberofrefunds6monthsago("numberofrefunds6monthsago"),
    numberofrefunds12monthsago("numberofrefunds12monthsago"),
    numberofrefundsyear2("numberofrefundsyear2"),
    numberofrefundsyear3("numberofrefundsyear3"),

    refundratiolastmonth("refundratiolastmonth"),
    refundratio2monthsago("refundratio2monthsago"),
    refundratio3monthsago("refundratio3monthsago"),
    refundratio4monthsago("refundratio4monthsago"),
    refundratio5monthsago("refundratio5monthsago"),
    refundratio6monthsago("refundratio6monthsago"),
    refundratio12monthsago("refundratio12monthsago"),
    refundratioyear2("refundratioyear2"),
    refundratioyear3("refundratioyear3"),

    chargebackratiolastmonth("chargebackratiolastmonth"),
    chargebackratio2monthsago("chargebackratio2monthsago"),
    chargebackratio3monthsago("chargebackratio3monthsago"),
    chargebackratio4monthsago("chargebackratio4monthsago"),
    chargebackratio5monthsago("chargebackratio5monthsago"),
    chargebackratio6monthsago("chargebackratio6monthsago"),
    chargebackratio12monthsago("chargebackratio12monthsago"),
    chargebackratioyear2("chargebackratioyear2"),
    chargebackratioyear3("chargebackratioyear3"),

    refundsvolumelastmonth("refundsvolumelastmonth"),
    refundsvolume2monthsago("refundsvolume2monthsago"),
    refundsvolume3monthsago("refundsvolume3monthsago"),
    refundsvolume4monthsago("refundsvolume4monthsago"),
    refundsvolume5monthsago("refundsvolume5monthsago"),
    refundsvolume6monthsago("refundsvolume6monthsago"),
    refundsvolume12monthsago("refundsvolume12monthsago"),
    refundsvolumeyear2("refundsvolumeyear2"),
    refundsvolumeyear3("refundsvolumeyear3"),
    currency("currency"),

    customertransdata("customertransdata"),

    //PDF Fields
    MultipleTransactioncurrencies("MultipleTransactioncurrencies"),//Account currency
    ProductSoldCurrency_All("ProductSoldCurrency_All"),
    currencyProductSoldJPY_PEN_HKD_AUD_DKK_SEK_NOK_INR("currencyProductSoldJPY_PEN_HKD_AUD_DKK_SEK_NOK_INR"), //for TW PROCESSING CURRENCIES other
    currencyTransferredbesides_EUR_USD_GBP_CAD("currencyTransferredbesides_EUR_USD_GBP_CAD"),
    currencyProductSoldbesides_INR_JPY_PEN_CAD_NOK("currencyProductSoldbesides_INR_JPY_PEN_CAD_NOK"),
    currencyProductSoldbesides_EUR_USD_GBP("currencyProductSoldbesides_EUR_USD_GBP"),
    currencyTransferredbesides_EUR_USD_GBP("currencyTransferredbesides_EUR_USD_GBP"),
    OtherTransactioncurrencies("OtherTransactioncurrencies"),
    OtherTransactioncurrencies2("OtherTransactioncurrencies2"),
    OtherProductSold_HKD_PEN_JPY_INR("OtherProductSold_HKD_PEN_JPY_INR"),
    AverageSalesVolume("AverageSalesVolume"),
    AverageNumberofTransaction("AverageNumberofTransaction"),
    Averagenumberofchargebacks("Averagenumberofchargebacks"),
    Its_done_by_PSP("Its_done_by_PSP"),
    Its_recommend_by_PSP("Its_recommend_by_PSP"),


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
    compliancepcidsscompliantYes("compliancepcidsscompliantYes"),
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

    monthlyDebitVolume("monthlyDebitVolume"),
    monthlyCreditVolume("monthlyCreditVolume"),

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
    supervisorregularcontrole("supervisorregularcontrole"),

    calAvgCbkRatio("calAvgCbkRatio"),
    avgOfCbk3MothsAgo("avgOfCbk3MothsAgo"),
    avgOfNumberOfCbk("avgOfNumberOfCbk"),
    avgCbkVolume("avgCbkVolume"),
    RecommendeByPSP("RecommendeByPSP"),
    avgSalesVolume("avgSalesVolume"),
    avgExpectedSalesVolume("avgExpectedSalesVolume"),
    expectedCompanyTurnOverLastYear("expectedCompanyTurnOverLastYear"),
    avgOfNumberOfRefund3MothsAgo("avgOfNumberOfRefund3MothsAgo"),
    avgRefundVolume("avgRefundVolume"),
    numberOfTransThroughMotoByMC("numberOfTransThroughMotoByMC"),
    numberOfTransThroughMotoByVISA("numberOfTransThroughMotoByVISA"),
    numberOfTransThroughInternetByMC("numberOfTransThroughInternetByMC"),
    numberOfTransThroughInternetByVISA("numberOfTransThroughInternetByVISA"),

    //KYC documents
    memorandomArticle("memorandomArticle"),
    incorporationCertificate("incorporationCertificate"),
    shareCertificate("shareCertificate"),
    processingHistory("processingHistory"),
    license("license"),
    bankStatement("bankStatement"),
    bankReferenceLetter("bankReferenceLetter"),
    proofOfIdentity("proofOfIdentity"),
    addressProof("addressProof"),
    crossCorporate("crossCorporate"),

    //Contractual Partner
    contractualPartnerId("contractualPartnerId"),
    contractualPartnerName("contractualPartnerName"),
    activeBusiness("activeBusiness"),
    merchantAcctTerminated("merchantAcctTerminated"),
    descriptionofprod("descriptionofprod");

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
