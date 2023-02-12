package com.vo.applicationManagerVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="OwnershipProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class OwnershipProfileVO
{

    //ownership profile field
    @XmlElement(name="applicationid")
    String applicationid;
/*
    //Shareholder profile
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1")
    String shareholderprofile1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_title")
    String shareholderprofile1_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_owned")
    String shareholderprofile1_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_telnocc1")
    String shareholderprofile1_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_telephonenumber")
    String shareholderprofile1_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_emailaddress")
    String shareholderprofile1_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_dateofbirth")
    String shareholderprofile1_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_socialsecurity")
    String shareholderprofile1_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_identificationtype")
    String shareholderprofile1_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_State")
    String shareholderprofile1_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_address")
    String shareholderprofile1_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_city")
    String shareholderprofile1_city;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_zip")
    String shareholderprofile1_zip;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_country")
    String shareholderprofile1_country;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_street")
    String shareholderprofile1_street;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_identificationtypeselect")
    String shareholderprofile1_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_nationality")
    String shareholderprofile1_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_Passportexpirydate")
    String shareholderprofile1_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_addressproof")
    String shareholderprofile1_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_addressId")
    String shareholderprofile1_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2")
    String shareholderprofile2;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_title")
    String shareholderprofile2_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_owned")
    String shareholderprofile2_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_telnocc2")
    String shareholderprofile2_telnocc2;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_telephonenumber")
    String shareholderprofile2_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_emailaddress")
    String shareholderprofile2_emailaddress ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_dateofbirth")
    String shareholderprofile2_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_socialsecurity")
    String shareholderprofile2_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_identificationtype")
    String shareholderprofile2_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_State")
    String shareholderprofile2_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_address")
    String shareholderprofile2_address;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_city")
    String shareholderprofile2_city;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_zip")
    String shareholderprofile2_zip;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_country")
    String shareholderprofile2_country;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_street")
    String shareholderprofile2_street;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_identificationtypeselect")
    String shareholderprofile2_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_nationality")
    String shareholderprofile2_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_Passportexpirydate")
    String shareholderprofile2_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_addressproof")
    String shareholderprofile2_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_addressId")
    String shareholderprofile2_addressId;


    //Add specific Shareholder Profile 3
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3")
    String shareholderprofile3;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_title")
    String shareholderprofile3_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_owned")
    String shareholderprofile3_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_telnocc2")
    String shareholderprofile3_telnocc2;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_telephonenumber")
    String shareholderprofile3_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_emailaddress")
    String shareholderprofile3_emailaddress ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_dateofbirth")
    String shareholderprofile3_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_socialsecurity")
    String shareholderprofile3_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_identificationtype")
    String shareholderprofile3_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_State")
    String shareholderprofile3_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_address")
    String shareholderprofile3_address;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_city")
    String shareholderprofile3_city;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_zip")
    String shareholderprofile3_zip;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_country")
    String shareholderprofile3_country;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_street")
    String shareholderprofile3_street;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_identificationtypeselect")
    String shareholderprofile3_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_nationality")
    String shareholderprofile3_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_Passportexpirydate")
    String shareholderprofile3_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_addressproof")
    String shareholderprofile3_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_addressId")
    String shareholderprofile3_addressId;


    // Corporate shareholder 1
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_Name")
    String corporateshareholder1_Name;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_RegNumber")
    String corporateshareholder1_RegNumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_Address")
    String corporateshareholder1_Address;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_City")
    String corporateshareholder1_City;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_State")
    String corporateshareholder1_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_ZipCode")
    String corporateshareholder1_ZipCode;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_Country")
    String corporateshareholder1_Country;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_Street")
    String corporateshareholder1_Street;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_addressproof")
    String corporateshareholder1_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_addressId")
    String corporateshareholder1_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_identificationtypeselect")
    String corporateshareholder1_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_identificationtype")
    String corporateshareholder1_identificationtype;

    // corporate shareholder 2
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_Name")
    String corporateshareholder2_Name;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_RegNumber")
    String corporateshareholder2_RegNumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_Address")
    String corporateshareholder2_Address;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_City")
    String corporateshareholder2_City;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_State")
    String corporateshareholder2_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_ZipCode")
    String corporateshareholder2_ZipCode;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_Country")
    String corporateshareholder2_Country;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_Street")
    String corporateshareholder2_Street;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_addressproof")
    String corporateshareholder2_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_addressId")
    String corporateshareholder2_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_identificationtypeselect")
    String corporateshareholder2_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_identificationtype")
    String corporateshareholder2_identificationtype;

    // Corporate shareholder 3
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_Name")
    String corporateshareholder3_Name;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_RegNumber")
    String corporateshareholder3_RegNumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_Address")
    String corporateshareholder3_Address;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_City")
    String corporateshareholder3_City;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_State")
    String corporateshareholder3_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_ZipCode")
    String corporateshareholder3_ZipCode;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_Country")
    String corporateshareholder3_Country;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_Street")
    String corporateshareholder3_Street;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_addressproof")
    String corporateshareholder3_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_addressId")
    String corporateshareholder3_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_identificationtypeselect")
    String corporateshareholder3_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_identificationtype")
    String corporateshareholder3_identificationtype;

    //Directors Profile
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile")
    String directorsprofile;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_title")
    String directorsprofile_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_telnocc1")
    String directorsprofile_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_telephonenumber")
    String directorsprofile_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_emailaddress")
    String directorsprofile_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_dateofbirth")
    String directorsprofile_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_socialsecurity")
    String directorsprofile_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_identificationtype")
    String directorsprofile_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_State")
    String directorsprofile_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_address")
    String directorsprofile_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_city")
    String directorsprofile_city  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_zip")
    String directorsprofile_zip ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_country")
    String directorsprofile_country  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_street")
    String directorsprofile_street  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_identificationtypeselect")
    String directorsprofile_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_nationality")
    String directorsprofile_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_Passportexpirydate")
    String directorsprofile_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_politicallyexposed")
    String directorsprofile_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_criminalrecord")
    String directorsprofile_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_addressproof")
    String directorsprofile_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_addressId")
    String directorsprofile_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_owned")
    String directorsprofile_owned;

    //Directors Profile 2
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2")
    String directorsprofile2;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_title")
    String directorsprofile2_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_telnocc1")
    String directorsprofile2_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_telephonenumber")
    String directorsprofile2_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_emailaddress")
    String directorsprofile2_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_dateofbirth")
    String directorsprofile2_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_socialsecurity")
    String directorsprofile2_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_identificationtype")
    String directorsprofile2_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_State")
    String directorsprofile2_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_address")
    String directorsprofile2_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_city")
    String directorsprofile2_city  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_zip")
    String directorsprofile2_zip ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_country")
    String directorsprofile2_country  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_street")
    String directorsprofile2_street  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_identificationtypeselect")
    String directorsprofile2_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_nationality")
    String directorsprofile2_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_Passportexpirydate")
    String directorsprofile2_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_politicallyexposed")
    String directorsprofile2_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_criminalrecord")
    String directorsprofile2_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_addressproof")
    String directorsprofile2_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_addressId")
    String directorsprofile2_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_owned")
    String directorsprofile2_owned;

    //Directors Profile 3
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3")
    String directorsprofile3;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_title")
    String directorsprofile3_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_telnocc1")
    String directorsprofile3_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_telephonenumber")
    String directorsprofile3_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_emailaddress")
    String directorsprofile3_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_dateofbirth")
    String directorsprofile3_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_socialsecurity")
    String directorsprofile3_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_identificationtype")
    String directorsprofile3_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_State")
    String directorsprofile3_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_address")
    String directorsprofile3_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_city")
    String directorsprofile3_city  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_zip")
    String directorsprofile3_zip ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_country")
    String directorsprofile3_country  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_street")
    String directorsprofile3_street  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_identificationtypeselect")
    String directorsprofile3_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_nationality")
    String directorsprofile3_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_Passportexpirydate")
    String directorsprofile3_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_politicallyexposed")
    String directorsprofile3_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_criminalrecord")
    String directorsprofile3_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_addressproof")
    String directorsprofile3_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_addressId")
    String directorsprofile3_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_owned")
    String directorsprofile3_owned;

    //Authorized Signatory Profile
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile")
    String authorizedsignatoryprofile;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_title")
    String authorizedsignatoryprofile_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_telnocc1")
    String authorizedsignatoryprofile_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_telephonenumber")
    String authorizedsignatoryprofile_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_emailaddress")
    String authorizedsignatoryprofile_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_dateofbirth")
    String authorizedsignatoryprofile_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_socialsecurity")
    String authorizedsignatoryprofile_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_identificationtype")
    String authorizedsignatoryprofile_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_State")
    String authorizedsignatoryprofile_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_address")
    String authorizedsignatoryprofile_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_city")
    String authorizedsignatoryprofile_city  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_zip")
    String authorizedsignatoryprofile_zip ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_country")
    String authorizedsignatoryprofile_country  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_street")
    String authorizedsignatoryprofile_street  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_identificationtypeselect")
    String authorizedsignatoryprofile_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_nationality")
    String authorizedsignatoryprofile_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_Passportexpirydate")
    String authorizedsignatoryprofile_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile1_owned")
    String authorizedsignatoryprofile1_owned;

    //Authorize Signatory Profile2
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2")
    String authorizedsignatoryprofile2;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_title")
    String authorizedsignatoryprofile2_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_telnocc1")
    String authorizedsignatoryprofile2_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_telephonenumber")
    String authorizedsignatoryprofile2_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_emailaddress")
    String authorizedsignatoryprofile2_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_dateofbirth")
    String authorizedsignatoryprofile2_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_socialsecurity")
    String authorizedsignatoryprofile2_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_identificationtype")
    String authorizedsignatoryprofile2_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_State")
    String authorizedsignatoryprofile2_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_address")
    String authorizedsignatoryprofile2_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_city")
    String authorizedsignatoryprofile2_city  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_zip")
    String authorizedsignatoryprofile2_zip ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_country")
    String authorizedsignatoryprofile2_country  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_street")
    String authorizedsignatoryprofile2_street  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_identificationtypeselect")
    String authorizedsignatoryprofile2_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_nationality")
    String authorizedsignatoryprofile2_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_Passportexpirydate")
    String authorizedsignatoryprofile2_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_owned")
    String authorizedsignatoryprofile2_owned;

    //Authorize Signatory profile3
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3")
    String authorizedsignatoryprofile3;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_title")
    String authorizedsignatoryprofile3_title;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_telnocc1")
    String authorizedsignatoryprofile3_telnocc1;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_telephonenumber")
    String authorizedsignatoryprofile3_telephonenumber;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_emailaddress")
    String authorizedsignatoryprofile3_emailaddress;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_dateofbirth")
    String authorizedsignatoryprofile3_dateofbirth;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_socialsecurity")
    String authorizedsignatoryprofile3_socialsecurity;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_identificationtype")
    String authorizedsignatoryprofile3_identificationtype;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_State")
    String authorizedsignatoryprofile3_State;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_address")
    String authorizedsignatoryprofile3_address ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_city")
    String authorizedsignatoryprofile3_city  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_zip")
    String authorizedsignatoryprofile3_zip ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_country")
    String authorizedsignatoryprofile3_country  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_street")
    String authorizedsignatoryprofile3_street  ;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_identificationtypeselect")
    String authorizedsignatoryprofile3_identificationtypeselect;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_nationality")
    String authorizedsignatoryprofile3_nationality;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_Passportexpirydate")
    String authorizedsignatoryprofile3_Passportexpirydate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_addressproof")
    String authorizedsignatoryprofile3_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_addressId")
    String authorizedsignatoryprofile3_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_owned")
    String authorizedsignatoryprofile3_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_lastname")
    String shareholderprofile1_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_lastname")
    String shareholderprofile2_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_lastname")
    String shareholderprofile3_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile_lastname")
    String directorsprofile_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2_lastname")
    String directorsprofile2_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile3_lastname")
    String directorsprofile3_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile_lastname")
    String authorizedsignatoryprofile_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_lastname")
    String authorizedsignatoryprofile2_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_lastname")
    String authorizedsignatoryprofile3_lastname;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1PassportIssueDate")
    String shareholderprofile1PassportIssueDate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2PassportIssueDate")
    String shareholderprofile2PassportIssueDate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3PassportIssueDate")
    String shareholderprofile3PassportIssueDate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofilePassportissuedate")
    String directorsprofilePassportissuedate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2Passportissuedate")
    String directorsprofile2Passportissuedate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.directorsprofile2Passportissuedate")
    String directorsprofile3Passportissuedate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofilePassportissuedate")
    String authorizedsignatoryprofilePassportissuedate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2Passportissuedate")
    String authorizedsignatoryprofile2Passportissuedate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3Passportissuedate")
    String authorizedsignatoryprofile3Passportissuedate;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_politicallyexposed")
    String shareholderprofile1_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile1_criminalrecord")
    String shareholderprofile1_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_politicallyexposed")
    String shareholderprofile2_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile2_criminalrecord")
    String shareholderprofile2_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_addressproof")
    String authorizedsignatoryprofile2_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_addressId")
    String authorizedsignatoryprofile2_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_politicallyexposed")
    String shareholderprofile3_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.shareholderprofile3_criminalrecord")
    String shareholderprofile3_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile1_politicallyexposed")
    String authorizedsignatoryprofile1_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile1_criminalrecord")
    String authorizedsignatoryprofile1_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile1_addressproof")
    String authorizedsignatoryprofile1_addressproof;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile1_addressId")
    String authorizedsignatoryprofile1_addressId;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_politicallyexposed")
    String authorizedsignatoryprofile2_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_criminalrecord")
    String authorizedsignatoryprofile2_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_politicallyexposed")
    String authorizedsignatoryprofile3_politicallyexposed;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_criminalrecord")
    String authorizedsignatoryprofile3_criminalrecord;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile1_designation")
    String authorizedsignatoryprofile1_designation;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile2_designation")
    String authorizedsignatoryprofile2_designation;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.authorizedsignatoryprofile3_designation")
    String authorizedsignatoryprofile3_designation;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder1_owned")
    String corporateshareholder1_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder2_owned")
    String corporateshareholder2_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.corporateshareholder3_owned")
    String corporateshareholder3_owned;*/

    @XmlElement(name="ownerShipProfileSaved")
    String ownerShipProfileSaved;

    @XmlElement(name="numOfShareholders")
    String numOfShareholders;

    @XmlElement(name="numOfCorporateShareholders")
    String numOfCorporateShareholders;

    @XmlElement(name="numOfDirectors")
    String numOfDirectors;

    @XmlElement(name="numOfAuthrisedSignatory")
    String numOfAuthrisedSignatory;

    Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap;

    public Map<String, OwnershipProfileDetailsVO> getOwnershipProfileDetailsVOMap()
    {
        return ownershipProfileDetailsVOMap;
    }

    public void setOwnershipProfileDetailsVOMap(Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap)
    {
        this.ownershipProfileDetailsVOMap = ownershipProfileDetailsVOMap;
    }

    public String getApplicationid()
    {
        return applicationid;
    }

    public void setApplicationid(String applicationid)
    {
        this.applicationid = applicationid;
    }

    public String getOwnerShipProfileSaved()
    {
        return ownerShipProfileSaved;
    }

    public void setOwnerShipProfileSaved(String ownerShipProfileSaved)
    {
        this.ownerShipProfileSaved = ownerShipProfileSaved;
    }

    public String getNumOfShareholders()
    {
        return numOfShareholders;
    }

    public void setNumOfShareholders(String numOfShareholders)
    {
        this.numOfShareholders = numOfShareholders;
    }

    public String getNumOfCorporateShareholders()
    {
        return numOfCorporateShareholders;
    }

    public void setNumOfCorporateShareholders(String numOfCorporateShareholders)
    {
        this.numOfCorporateShareholders = numOfCorporateShareholders;
    }

    public String getNumOfDirectors()
    {
        return numOfDirectors;
    }

    public void setNumOfDirectors(String numOfDirectors)
    {
        this.numOfDirectors = numOfDirectors;
    }

    public String getNumOfAuthrisedSignatory()
    {
        return numOfAuthrisedSignatory;
    }

    public void setNumOfAuthrisedSignatory(String numOfAuthrisedSignatory)
    {
        this.numOfAuthrisedSignatory = numOfAuthrisedSignatory;
    }
}