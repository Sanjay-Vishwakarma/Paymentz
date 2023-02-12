package com.vo.applicationManagerVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 08-09-2017.
 */
@XmlRootElement(name="OwnershipProfileVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class OwnershipProfileRequestVO
{
    @FormParam("MerchantApplicationForm.OwnershipProfileVO.applicationid")
    String applicationid;

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
    String corporateshareholder3_owned;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.ownerShipProfileSaved")
    String ownerShipProfileSaved;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.numOfShareholders")
    String numOfShareholders;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.numOfCorporateShareholders")
    String numOfCorporateShareholders;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.numOfDirectors")
    String numOfDirectors;

    @FormParam("MerchantApplicationForm.OwnershipProfileVO.numOfAuthrisedSignatory")
    String numOfAuthrisedSignatory;

    //Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap;

   /* public Map<String, OwnershipProfileDetailsVO> getOwnershipProfileDetailsVOMap()
    {
        return ownershipProfileDetailsVOMap;
    }

    public void setOwnershipProfileDetailsVOMap(Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap)
    {
        this.ownershipProfileDetailsVOMap = ownershipProfileDetailsVOMap;
    }
*/
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


    public String getShareholderprofile1()
    {
        return shareholderprofile1;
    }

    public void setShareholderprofile1(String shareholderprofile1)
    {
        this.shareholderprofile1 = shareholderprofile1;
    }

    public String getShareholderprofile1_title()
    {
        return shareholderprofile1_title;
    }

    public void setShareholderprofile1_title(String shareholderprofile1_title)
    {
        this.shareholderprofile1_title = shareholderprofile1_title;
    }

    public String getShareholderprofile1_owned()
    {
        return shareholderprofile1_owned;
    }

    public void setShareholderprofile1_owned(String shareholderprofile1_owned)
    {
        this.shareholderprofile1_owned = shareholderprofile1_owned;
    }

    public String getShareholderprofile1_telnocc1()
    {
        return shareholderprofile1_telnocc1;
    }

    public void setShareholderprofile1_telnocc1(String shareholderprofile1_telnocc1)
    {
        this.shareholderprofile1_telnocc1 = shareholderprofile1_telnocc1;
    }

    public String getShareholderprofile1_telephonenumber()
    {
        return shareholderprofile1_telephonenumber;
    }

    public void setShareholderprofile1_telephonenumber(String shareholderprofile1_telephonenumber)
    {
        this.shareholderprofile1_telephonenumber = shareholderprofile1_telephonenumber;
    }

    public String getShareholderprofile1_emailaddress()
    {
        return shareholderprofile1_emailaddress;
    }

    public void setShareholderprofile1_emailaddress(String shareholderprofile1_emailaddress)
    {
        this.shareholderprofile1_emailaddress = shareholderprofile1_emailaddress;
    }

    public String getShareholderprofile1_dateofbirth()
    {
        return shareholderprofile1_dateofbirth;
    }

    public void setShareholderprofile1_dateofbirth(String shareholderprofile1_dateofbirth)
    {
        this.shareholderprofile1_dateofbirth = shareholderprofile1_dateofbirth;
    }

    public String getShareholderprofile1_socialsecurity()
    {
        return shareholderprofile1_socialsecurity;
    }

    public void setShareholderprofile1_socialsecurity(String shareholderprofile1_socialsecurity)
    {
        this.shareholderprofile1_socialsecurity = shareholderprofile1_socialsecurity;
    }

    public String getShareholderprofile1_identificationtype()
    {
        return shareholderprofile1_identificationtype;
    }

    public void setShareholderprofile1_identificationtype(String shareholderprofile1_identificationtype)
    {
        this.shareholderprofile1_identificationtype = shareholderprofile1_identificationtype;
    }

    public String getShareholderprofile1_State()
    {
        return shareholderprofile1_State;
    }

    public void setShareholderprofile1_State(String shareholderprofile1_State)
    {
        this.shareholderprofile1_State = shareholderprofile1_State;
    }

    public String getShareholderprofile1_address()
    {
        return shareholderprofile1_address;
    }

    public void setShareholderprofile1_address(String shareholderprofile1_address)
    {
        this.shareholderprofile1_address = shareholderprofile1_address;
    }

    public String getShareholderprofile1_city()
    {
        return shareholderprofile1_city;
    }

    public void setShareholderprofile1_city(String shareholderprofile1_city)
    {
        this.shareholderprofile1_city = shareholderprofile1_city;
    }

    public String getShareholderprofile1_zip()
    {
        return shareholderprofile1_zip;
    }

    public void setShareholderprofile1_zip(String shareholderprofile1_zip)
    {
        this.shareholderprofile1_zip = shareholderprofile1_zip;
    }

    public String getShareholderprofile1_country()
    {
        return shareholderprofile1_country;
    }

    public void setShareholderprofile1_country(String shareholderprofile1_country)
    {
        this.shareholderprofile1_country = shareholderprofile1_country;
    }

    public String getShareholderprofile1_street()
    {
        return shareholderprofile1_street;
    }

    public void setShareholderprofile1_street(String shareholderprofile1_street)
    {
        this.shareholderprofile1_street = shareholderprofile1_street;
    }

    public String getShareholderprofile1_identificationtypeselect()
    {
        return shareholderprofile1_identificationtypeselect;
    }

    public void setShareholderprofile1_identificationtypeselect(String shareholderprofile1_identificationtypeselect)
    {
        this.shareholderprofile1_identificationtypeselect = shareholderprofile1_identificationtypeselect;
    }

    public String getShareholderprofile1_nationality()
    {
        return shareholderprofile1_nationality;
    }

    public void setShareholderprofile1_nationality(String shareholderprofile1_nationality)
    {
        this.shareholderprofile1_nationality = shareholderprofile1_nationality;
    }

    public String getShareholderprofile1_Passportexpirydate()
    {
        return shareholderprofile1_Passportexpirydate;
    }

    public void setShareholderprofile1_Passportexpirydate(String shareholderprofile1_Passportexpirydate)
    {
        this.shareholderprofile1_Passportexpirydate = shareholderprofile1_Passportexpirydate;
    }

    public String getShareholderprofile1_addressproof()
    {
        return shareholderprofile1_addressproof;
    }

    public void setShareholderprofile1_addressproof(String shareholderprofile1_addressproof)
    {
        this.shareholderprofile1_addressproof = shareholderprofile1_addressproof;
    }

    public String getShareholderprofile1_addressId()
    {
        return shareholderprofile1_addressId;
    }

    public void setShareholderprofile1_addressId(String shareholderprofile1_addressId)
    {
        this.shareholderprofile1_addressId = shareholderprofile1_addressId;
    }

    public String getShareholderprofile2()
    {
        return shareholderprofile2;
    }

    public void setShareholderprofile2(String shareholderprofile2)
    {
        this.shareholderprofile2 = shareholderprofile2;
    }

    public String getShareholderprofile2_title()
    {
        return shareholderprofile2_title;
    }

    public void setShareholderprofile2_title(String shareholderprofile2_title)
    {
        this.shareholderprofile2_title = shareholderprofile2_title;
    }

    public String getShareholderprofile2_owned()
    {
        return shareholderprofile2_owned;
    }

    public void setShareholderprofile2_owned(String shareholderprofile2_owned)
    {
        this.shareholderprofile2_owned = shareholderprofile2_owned;
    }

    public String getShareholderprofile2_telnocc2()
    {
        return shareholderprofile2_telnocc2;
    }

    public void setShareholderprofile2_telnocc2(String shareholderprofile2_telnocc2)
    {
        this.shareholderprofile2_telnocc2 = shareholderprofile2_telnocc2;
    }

    public String getShareholderprofile2_telephonenumber()
    {
        return shareholderprofile2_telephonenumber;
    }

    public void setShareholderprofile2_telephonenumber(String shareholderprofile2_telephonenumber)
    {
        this.shareholderprofile2_telephonenumber = shareholderprofile2_telephonenumber;
    }

    public String getShareholderprofile2_emailaddress()
    {
        return shareholderprofile2_emailaddress;
    }

    public void setShareholderprofile2_emailaddress(String shareholderprofile2_emailaddress)
    {
        this.shareholderprofile2_emailaddress = shareholderprofile2_emailaddress;
    }

    public String getShareholderprofile2_dateofbirth()
    {
        return shareholderprofile2_dateofbirth;
    }

    public void setShareholderprofile2_dateofbirth(String shareholderprofile2_dateofbirth)
    {
        this.shareholderprofile2_dateofbirth = shareholderprofile2_dateofbirth;
    }

    public String getShareholderprofile2_socialsecurity()
    {
        return shareholderprofile2_socialsecurity;
    }

    public void setShareholderprofile2_socialsecurity(String shareholderprofile2_socialsecurity)
    {
        this.shareholderprofile2_socialsecurity = shareholderprofile2_socialsecurity;
    }

    public String getShareholderprofile2_identificationtype()
    {
        return shareholderprofile2_identificationtype;
    }

    public void setShareholderprofile2_identificationtype(String shareholderprofile2_identificationtype)
    {
        this.shareholderprofile2_identificationtype = shareholderprofile2_identificationtype;
    }

    public String getShareholderprofile2_State()
    {
        return shareholderprofile2_State;
    }

    public void setShareholderprofile2_State(String shareholderprofile2_State)
    {
        this.shareholderprofile2_State = shareholderprofile2_State;
    }

    public String getShareholderprofile2_address()
    {
        return shareholderprofile2_address;
    }

    public void setShareholderprofile2_address(String shareholderprofile2_address)
    {
        this.shareholderprofile2_address = shareholderprofile2_address;
    }

    public String getShareholderprofile2_city()
    {
        return shareholderprofile2_city;
    }

    public void setShareholderprofile2_city(String shareholderprofile2_city)
    {
        this.shareholderprofile2_city = shareholderprofile2_city;
    }

    public String getShareholderprofile2_zip()
    {
        return shareholderprofile2_zip;
    }

    public void setShareholderprofile2_zip(String shareholderprofile2_zip)
    {
        this.shareholderprofile2_zip = shareholderprofile2_zip;
    }

    public String getShareholderprofile2_country()
    {
        return shareholderprofile2_country;
    }

    public void setShareholderprofile2_country(String shareholderprofile2_country)
    {
        this.shareholderprofile2_country = shareholderprofile2_country;
    }

    public String getShareholderprofile2_street()
    {
        return shareholderprofile2_street;
    }

    public void setShareholderprofile2_street(String shareholderprofile2_street)
    {
        this.shareholderprofile2_street = shareholderprofile2_street;
    }

    public String getShareholderprofile2_identificationtypeselect()
    {
        return shareholderprofile2_identificationtypeselect;
    }

    public void setShareholderprofile2_identificationtypeselect(String shareholderprofile2_identificationtypeselect)
    {
        this.shareholderprofile2_identificationtypeselect = shareholderprofile2_identificationtypeselect;
    }

    public String getShareholderprofile2_nationality()
    {
        return shareholderprofile2_nationality;
    }

    public void setShareholderprofile2_nationality(String shareholderprofile2_nationality)
    {
        this.shareholderprofile2_nationality = shareholderprofile2_nationality;
    }

    public String getShareholderprofile2_Passportexpirydate()
    {
        return shareholderprofile2_Passportexpirydate;
    }

    public void setShareholderprofile2_Passportexpirydate(String shareholderprofile2_Passportexpirydate)
    {
        this.shareholderprofile2_Passportexpirydate = shareholderprofile2_Passportexpirydate;
    }

    public String getShareholderprofile2_addressproof()
    {
        return shareholderprofile2_addressproof;
    }

    public void setShareholderprofile2_addressproof(String shareholderprofile2_addressproof)
    {
        this.shareholderprofile2_addressproof = shareholderprofile2_addressproof;
    }

    public String getShareholderprofile2_addressId()
    {
        return shareholderprofile2_addressId;
    }

    public void setShareholderprofile2_addressId(String shareholderprofile2_addressId)
    {
        this.shareholderprofile2_addressId = shareholderprofile2_addressId;
    }

    public String getShareholderprofile3()
    {
        return shareholderprofile3;
    }

    public void setShareholderprofile3(String shareholderprofile3)
    {
        this.shareholderprofile3 = shareholderprofile3;
    }

    public String getShareholderprofile3_title()
    {
        return shareholderprofile3_title;
    }

    public void setShareholderprofile3_title(String shareholderprofile3_title)
    {
        this.shareholderprofile3_title = shareholderprofile3_title;
    }

    public String getShareholderprofile3_owned()
    {
        return shareholderprofile3_owned;
    }

    public void setShareholderprofile3_owned(String shareholderprofile3_owned)
    {
        this.shareholderprofile3_owned = shareholderprofile3_owned;
    }

    public String getShareholderprofile3_telnocc2()
    {
        return shareholderprofile3_telnocc2;
    }

    public void setShareholderprofile3_telnocc2(String shareholderprofile3_telnocc2)
    {
        this.shareholderprofile3_telnocc2 = shareholderprofile3_telnocc2;
    }

    public String getShareholderprofile3_telephonenumber()
    {
        return shareholderprofile3_telephonenumber;
    }

    public void setShareholderprofile3_telephonenumber(String shareholderprofile3_telephonenumber)
    {
        this.shareholderprofile3_telephonenumber = shareholderprofile3_telephonenumber;
    }

    public String getShareholderprofile3_emailaddress()
    {
        return shareholderprofile3_emailaddress;
    }

    public void setShareholderprofile3_emailaddress(String shareholderprofile3_emailaddress)
    {
        this.shareholderprofile3_emailaddress = shareholderprofile3_emailaddress;
    }

    public String getShareholderprofile3_dateofbirth()
    {
        return shareholderprofile3_dateofbirth;
    }

    public void setShareholderprofile3_dateofbirth(String shareholderprofile3_dateofbirth)
    {
        this.shareholderprofile3_dateofbirth = shareholderprofile3_dateofbirth;
    }

    public String getShareholderprofile3_socialsecurity()
    {
        return shareholderprofile3_socialsecurity;
    }

    public void setShareholderprofile3_socialsecurity(String shareholderprofile3_socialsecurity)
    {
        this.shareholderprofile3_socialsecurity = shareholderprofile3_socialsecurity;
    }

    public String getShareholderprofile3_identificationtype()
    {
        return shareholderprofile3_identificationtype;
    }

    public void setShareholderprofile3_identificationtype(String shareholderprofile3_identificationtype)
    {
        this.shareholderprofile3_identificationtype = shareholderprofile3_identificationtype;
    }

    public String getShareholderprofile3_State()
    {
        return shareholderprofile3_State;
    }

    public void setShareholderprofile3_State(String shareholderprofile3_State)
    {
        this.shareholderprofile3_State = shareholderprofile3_State;
    }

    public String getShareholderprofile3_address()
    {
        return shareholderprofile3_address;
    }

    public void setShareholderprofile3_address(String shareholderprofile3_address)
    {
        this.shareholderprofile3_address = shareholderprofile3_address;
    }

    public String getShareholderprofile3_city()
    {
        return shareholderprofile3_city;
    }

    public void setShareholderprofile3_city(String shareholderprofile3_city)
    {
        this.shareholderprofile3_city = shareholderprofile3_city;
    }

    public String getShareholderprofile3_zip()
    {
        return shareholderprofile3_zip;
    }

    public void setShareholderprofile3_zip(String shareholderprofile3_zip)
    {
        this.shareholderprofile3_zip = shareholderprofile3_zip;
    }

    public String getShareholderprofile3_country()
    {
        return shareholderprofile3_country;
    }

    public void setShareholderprofile3_country(String shareholderprofile3_country)
    {
        this.shareholderprofile3_country = shareholderprofile3_country;
    }

    public String getShareholderprofile3_street()
    {
        return shareholderprofile3_street;
    }

    public void setShareholderprofile3_street(String shareholderprofile3_street)
    {
        this.shareholderprofile3_street = shareholderprofile3_street;
    }

    public String getShareholderprofile3_identificationtypeselect()
    {
        return shareholderprofile3_identificationtypeselect;
    }

    public void setShareholderprofile3_identificationtypeselect(String shareholderprofile3_identificationtypeselect)
    {
        this.shareholderprofile3_identificationtypeselect = shareholderprofile3_identificationtypeselect;
    }

    public String getShareholderprofile3_nationality()
    {
        return shareholderprofile3_nationality;
    }

    public void setShareholderprofile3_nationality(String shareholderprofile3_nationality)
    {
        this.shareholderprofile3_nationality = shareholderprofile3_nationality;
    }

    public String getShareholderprofile3_Passportexpirydate()
    {
        return shareholderprofile3_Passportexpirydate;
    }

    public void setShareholderprofile3_Passportexpirydate(String shareholderprofile3_Passportexpirydate)
    {
        this.shareholderprofile3_Passportexpirydate = shareholderprofile3_Passportexpirydate;
    }

    public String getShareholderprofile3_addressproof()
    {
        return shareholderprofile3_addressproof;
    }

    public void setShareholderprofile3_addressproof(String shareholderprofile3_addressproof)
    {
        this.shareholderprofile3_addressproof = shareholderprofile3_addressproof;
    }

    public String getShareholderprofile3_addressId()
    {
        return shareholderprofile3_addressId;
    }

    public void setShareholderprofile3_addressId(String shareholderprofile3_addressId)
    {
        this.shareholderprofile3_addressId = shareholderprofile3_addressId;
    }

    public String getCorporateshareholder1_Name()
    {
        return corporateshareholder1_Name;
    }

    public void setCorporateshareholder1_Name(String corporateshareholder1_Name)
    {
        this.corporateshareholder1_Name = corporateshareholder1_Name;
    }

    public String getCorporateshareholder1_RegNumber()
    {
        return corporateshareholder1_RegNumber;
    }

    public void setCorporateshareholder1_RegNumber(String corporateshareholder1_RegNumber)
    {
        this.corporateshareholder1_RegNumber = corporateshareholder1_RegNumber;
    }

    public String getCorporateshareholder1_Address()
    {
        return corporateshareholder1_Address;
    }

    public void setCorporateshareholder1_Address(String corporateshareholder1_Address)
    {
        this.corporateshareholder1_Address = corporateshareholder1_Address;
    }

    public String getCorporateshareholder1_City()
    {
        return corporateshareholder1_City;
    }

    public void setCorporateshareholder1_City(String corporateshareholder1_City)
    {
        this.corporateshareholder1_City = corporateshareholder1_City;
    }

    public String getCorporateshareholder1_State()
    {
        return corporateshareholder1_State;
    }

    public void setCorporateshareholder1_State(String corporateshareholder1_State)
    {
        this.corporateshareholder1_State = corporateshareholder1_State;
    }

    public String getCorporateshareholder1_ZipCode()
    {
        return corporateshareholder1_ZipCode;
    }

    public void setCorporateshareholder1_ZipCode(String corporateshareholder1_ZipCode)
    {
        this.corporateshareholder1_ZipCode = corporateshareholder1_ZipCode;
    }

    public String getCorporateshareholder1_Country()
    {
        return corporateshareholder1_Country;
    }

    public void setCorporateshareholder1_Country(String corporateshareholder1_Country)
    {
        this.corporateshareholder1_Country = corporateshareholder1_Country;
    }

    public String getCorporateshareholder1_Street()
    {
        return corporateshareholder1_Street;
    }

    public void setCorporateshareholder1_Street(String corporateshareholder1_Street)
    {
        this.corporateshareholder1_Street = corporateshareholder1_Street;
    }

    public String getCorporateshareholder1_addressproof()
    {
        return corporateshareholder1_addressproof;
    }

    public void setCorporateshareholder1_addressproof(String corporateshareholder1_addressproof)
    {
        this.corporateshareholder1_addressproof = corporateshareholder1_addressproof;
    }

    public String getCorporateshareholder1_addressId()
    {
        return corporateshareholder1_addressId;
    }

    public void setCorporateshareholder1_addressId(String corporateshareholder1_addressId)
    {
        this.corporateshareholder1_addressId = corporateshareholder1_addressId;
    }

    public String getCorporateshareholder1_identificationtypeselect()
    {
        return corporateshareholder1_identificationtypeselect;
    }

    public void setCorporateshareholder1_identificationtypeselect(String corporateshareholder1_identificationtypeselect)
    {
        this.corporateshareholder1_identificationtypeselect = corporateshareholder1_identificationtypeselect;
    }

    public String getCorporateshareholder1_identificationtype()
    {
        return corporateshareholder1_identificationtype;
    }

    public void setCorporateshareholder1_identificationtype(String corporateshareholder1_identificationtype)
    {
        this.corporateshareholder1_identificationtype = corporateshareholder1_identificationtype;
    }

    public String getCorporateshareholder2_Name()
    {
        return corporateshareholder2_Name;
    }

    public void setCorporateshareholder2_Name(String corporateshareholder2_Name)
    {
        this.corporateshareholder2_Name = corporateshareholder2_Name;
    }

    public String getCorporateshareholder2_RegNumber()
    {
        return corporateshareholder2_RegNumber;
    }

    public void setCorporateshareholder2_RegNumber(String corporateshareholder2_RegNumber)
    {
        this.corporateshareholder2_RegNumber = corporateshareholder2_RegNumber;
    }

    public String getCorporateshareholder2_Address()
    {
        return corporateshareholder2_Address;
    }

    public void setCorporateshareholder2_Address(String corporateshareholder2_Address)
    {
        this.corporateshareholder2_Address = corporateshareholder2_Address;
    }

    public String getCorporateshareholder2_City()
    {
        return corporateshareholder2_City;
    }

    public void setCorporateshareholder2_City(String corporateshareholder2_City)
    {
        this.corporateshareholder2_City = corporateshareholder2_City;
    }

    public String getCorporateshareholder2_State()
    {
        return corporateshareholder2_State;
    }

    public void setCorporateshareholder2_State(String corporateshareholder2_State)
    {
        this.corporateshareholder2_State = corporateshareholder2_State;
    }

    public String getCorporateshareholder2_ZipCode()
    {
        return corporateshareholder2_ZipCode;
    }

    public void setCorporateshareholder2_ZipCode(String corporateshareholder2_ZipCode)
    {
        this.corporateshareholder2_ZipCode = corporateshareholder2_ZipCode;
    }

    public String getCorporateshareholder2_Country()
    {
        return corporateshareholder2_Country;
    }

    public void setCorporateshareholder2_Country(String corporateshareholder2_Country)
    {
        this.corporateshareholder2_Country = corporateshareholder2_Country;
    }

    public String getCorporateshareholder2_Street()
    {
        return corporateshareholder2_Street;
    }

    public void setCorporateshareholder2_Street(String corporateshareholder2_Street)
    {
        this.corporateshareholder2_Street = corporateshareholder2_Street;
    }

    public String getCorporateshareholder2_addressproof()
    {
        return corporateshareholder2_addressproof;
    }

    public void setCorporateshareholder2_addressproof(String corporateshareholder2_addressproof)
    {
        this.corporateshareholder2_addressproof = corporateshareholder2_addressproof;
    }

    public String getCorporateshareholder2_addressId()
    {
        return corporateshareholder2_addressId;
    }

    public void setCorporateshareholder2_addressId(String corporateshareholder2_addressId)
    {
        this.corporateshareholder2_addressId = corporateshareholder2_addressId;
    }

    public String getCorporateshareholder2_identificationtypeselect()
    {
        return corporateshareholder2_identificationtypeselect;
    }

    public void setCorporateshareholder2_identificationtypeselect(String corporateshareholder2_identificationtypeselect)
    {
        this.corporateshareholder2_identificationtypeselect = corporateshareholder2_identificationtypeselect;
    }

    public String getCorporateshareholder2_identificationtype()
    {
        return corporateshareholder2_identificationtype;
    }

    public void setCorporateshareholder2_identificationtype(String corporateshareholder2_identificationtype)
    {
        this.corporateshareholder2_identificationtype = corporateshareholder2_identificationtype;
    }

    public String getCorporateshareholder3_Name()
    {
        return corporateshareholder3_Name;
    }

    public void setCorporateshareholder3_Name(String corporateshareholder3_Name)
    {
        this.corporateshareholder3_Name = corporateshareholder3_Name;
    }

    public String getCorporateshareholder3_RegNumber()
    {
        return corporateshareholder3_RegNumber;
    }

    public void setCorporateshareholder3_RegNumber(String corporateshareholder3_RegNumber)
    {
        this.corporateshareholder3_RegNumber = corporateshareholder3_RegNumber;
    }

    public String getCorporateshareholder3_Address()
    {
        return corporateshareholder3_Address;
    }

    public void setCorporateshareholder3_Address(String corporateshareholder3_Address)
    {
        this.corporateshareholder3_Address = corporateshareholder3_Address;
    }

    public String getCorporateshareholder3_City()
    {
        return corporateshareholder3_City;
    }

    public void setCorporateshareholder3_City(String corporateshareholder3_City)
    {
        this.corporateshareholder3_City = corporateshareholder3_City;
    }

    public String getCorporateshareholder3_State()
    {
        return corporateshareholder3_State;
    }

    public void setCorporateshareholder3_State(String corporateshareholder3_State)
    {
        this.corporateshareholder3_State = corporateshareholder3_State;
    }

    public String getCorporateshareholder3_ZipCode()
    {
        return corporateshareholder3_ZipCode;
    }

    public void setCorporateshareholder3_ZipCode(String corporateshareholder3_ZipCode)
    {
        this.corporateshareholder3_ZipCode = corporateshareholder3_ZipCode;
    }

    public String getCorporateshareholder3_Country()
    {
        return corporateshareholder3_Country;
    }

    public void setCorporateshareholder3_Country(String corporateshareholder3_Country)
    {
        this.corporateshareholder3_Country = corporateshareholder3_Country;
    }

    public String getCorporateshareholder3_Street()
    {
        return corporateshareholder3_Street;
    }

    public void setCorporateshareholder3_Street(String corporateshareholder3_Street)
    {
        this.corporateshareholder3_Street = corporateshareholder3_Street;
    }

    public String getCorporateshareholder3_addressproof()
    {
        return corporateshareholder3_addressproof;
    }

    public void setCorporateshareholder3_addressproof(String corporateshareholder3_addressproof)
    {
        this.corporateshareholder3_addressproof = corporateshareholder3_addressproof;
    }

    public String getCorporateshareholder3_addressId()
    {
        return corporateshareholder3_addressId;
    }

    public void setCorporateshareholder3_addressId(String corporateshareholder3_addressId)
    {
        this.corporateshareholder3_addressId = corporateshareholder3_addressId;
    }

    public String getCorporateshareholder3_identificationtypeselect()
    {
        return corporateshareholder3_identificationtypeselect;
    }

    public void setCorporateshareholder3_identificationtypeselect(String corporateshareholder3_identificationtypeselect)
    {
        this.corporateshareholder3_identificationtypeselect = corporateshareholder3_identificationtypeselect;
    }

    public String getCorporateshareholder3_identificationtype()
    {
        return corporateshareholder3_identificationtype;
    }

    public void setCorporateshareholder3_identificationtype(String corporateshareholder3_identificationtype)
    {
        this.corporateshareholder3_identificationtype = corporateshareholder3_identificationtype;
    }

    public String getDirectorsprofile()
    {
        return directorsprofile;
    }

    public void setDirectorsprofile(String directorsprofile)
    {
        this.directorsprofile = directorsprofile;
    }

    public String getDirectorsprofile_title()
    {
        return directorsprofile_title;
    }

    public void setDirectorsprofile_title(String directorsprofile_title)
    {
        this.directorsprofile_title = directorsprofile_title;
    }

    public String getDirectorsprofile_telnocc1()
    {
        return directorsprofile_telnocc1;
    }

    public void setDirectorsprofile_telnocc1(String directorsprofile_telnocc1)
    {
        this.directorsprofile_telnocc1 = directorsprofile_telnocc1;
    }

    public String getDirectorsprofile_telephonenumber()
    {
        return directorsprofile_telephonenumber;
    }

    public void setDirectorsprofile_telephonenumber(String directorsprofile_telephonenumber)
    {
        this.directorsprofile_telephonenumber = directorsprofile_telephonenumber;
    }

    public String getDirectorsprofile_emailaddress()
    {
        return directorsprofile_emailaddress;
    }

    public void setDirectorsprofile_emailaddress(String directorsprofile_emailaddress)
    {
        this.directorsprofile_emailaddress = directorsprofile_emailaddress;
    }

    public String getDirectorsprofile_dateofbirth()
    {
        return directorsprofile_dateofbirth;
    }

    public void setDirectorsprofile_dateofbirth(String directorsprofile_dateofbirth)
    {
        this.directorsprofile_dateofbirth = directorsprofile_dateofbirth;
    }

    public String getDirectorsprofile_socialsecurity()
    {
        return directorsprofile_socialsecurity;
    }

    public void setDirectorsprofile_socialsecurity(String directorsprofile_socialsecurity)
    {
        this.directorsprofile_socialsecurity = directorsprofile_socialsecurity;
    }

    public String getDirectorsprofile_identificationtype()
    {
        return directorsprofile_identificationtype;
    }

    public void setDirectorsprofile_identificationtype(String directorsprofile_identificationtype)
    {
        this.directorsprofile_identificationtype = directorsprofile_identificationtype;
    }

    public String getDirectorsprofile_State()
    {
        return directorsprofile_State;
    }

    public void setDirectorsprofile_State(String directorsprofile_State)
    {
        this.directorsprofile_State = directorsprofile_State;
    }

    public String getDirectorsprofile_address()
    {
        return directorsprofile_address;
    }

    public void setDirectorsprofile_address(String directorsprofile_address)
    {
        this.directorsprofile_address = directorsprofile_address;
    }

    public String getDirectorsprofile_city()
    {
        return directorsprofile_city;
    }

    public void setDirectorsprofile_city(String directorsprofile_city)
    {
        this.directorsprofile_city = directorsprofile_city;
    }

    public String getDirectorsprofile_zip()
    {
        return directorsprofile_zip;
    }

    public void setDirectorsprofile_zip(String directorsprofile_zip)
    {
        this.directorsprofile_zip = directorsprofile_zip;
    }

    public String getDirectorsprofile_country()
    {
        return directorsprofile_country;
    }

    public void setDirectorsprofile_country(String directorsprofile_country)
    {
        this.directorsprofile_country = directorsprofile_country;
    }

    public String getDirectorsprofile_street()
    {
        return directorsprofile_street;
    }

    public void setDirectorsprofile_street(String directorsprofile_street)
    {
        this.directorsprofile_street = directorsprofile_street;
    }

    public String getDirectorsprofile_identificationtypeselect()
    {
        return directorsprofile_identificationtypeselect;
    }

    public void setDirectorsprofile_identificationtypeselect(String directorsprofile_identificationtypeselect)
    {
        this.directorsprofile_identificationtypeselect = directorsprofile_identificationtypeselect;
    }

    public String getDirectorsprofile_nationality()
    {
        return directorsprofile_nationality;
    }

    public void setDirectorsprofile_nationality(String directorsprofile_nationality)
    {
        this.directorsprofile_nationality = directorsprofile_nationality;
    }

    public String getDirectorsprofile_Passportexpirydate()
    {
        return directorsprofile_Passportexpirydate;
    }

    public void setDirectorsprofile_Passportexpirydate(String directorsprofile_Passportexpirydate)
    {
        this.directorsprofile_Passportexpirydate = directorsprofile_Passportexpirydate;
    }

    public String getDirectorsprofile_politicallyexposed()
    {
        return directorsprofile_politicallyexposed;
    }

    public void setDirectorsprofile_politicallyexposed(String directorsprofile_politicallyexposed)
    {
        this.directorsprofile_politicallyexposed = directorsprofile_politicallyexposed;
    }

    public String getDirectorsprofile_criminalrecord()
    {
        return directorsprofile_criminalrecord;
    }

    public void setDirectorsprofile_criminalrecord(String directorsprofile_criminalrecord)
    {
        this.directorsprofile_criminalrecord = directorsprofile_criminalrecord;
    }

    public String getDirectorsprofile_addressproof()
    {
        return directorsprofile_addressproof;
    }

    public void setDirectorsprofile_addressproof(String directorsprofile_addressproof)
    {
        this.directorsprofile_addressproof = directorsprofile_addressproof;
    }

    public String getDirectorsprofile_addressId()
    {
        return directorsprofile_addressId;
    }

    public void setDirectorsprofile_addressId(String directorsprofile_addressId)
    {
        this.directorsprofile_addressId = directorsprofile_addressId;
    }

    public String getDirectorsprofile_owned()
    {
        return directorsprofile_owned;
    }

    public void setDirectorsprofile_owned(String directorsprofile_owned)
    {
        this.directorsprofile_owned = directorsprofile_owned;
    }

    public String getDirectorsprofile2()
    {
        return directorsprofile2;
    }

    public void setDirectorsprofile2(String directorsprofile2)
    {
        this.directorsprofile2 = directorsprofile2;
    }

    public String getDirectorsprofile2_title()
    {
        return directorsprofile2_title;
    }

    public void setDirectorsprofile2_title(String directorsprofile2_title)
    {
        this.directorsprofile2_title = directorsprofile2_title;
    }

    public String getDirectorsprofile2_telnocc1()
    {
        return directorsprofile2_telnocc1;
    }

    public void setDirectorsprofile2_telnocc1(String directorsprofile2_telnocc1)
    {
        this.directorsprofile2_telnocc1 = directorsprofile2_telnocc1;
    }

    public String getDirectorsprofile2_telephonenumber()
    {
        return directorsprofile2_telephonenumber;
    }

    public void setDirectorsprofile2_telephonenumber(String directorsprofile2_telephonenumber)
    {
        this.directorsprofile2_telephonenumber = directorsprofile2_telephonenumber;
    }

    public String getDirectorsprofile2_emailaddress()
    {
        return directorsprofile2_emailaddress;
    }

    public void setDirectorsprofile2_emailaddress(String directorsprofile2_emailaddress)
    {
        this.directorsprofile2_emailaddress = directorsprofile2_emailaddress;
    }

    public String getDirectorsprofile2_dateofbirth()
    {
        return directorsprofile2_dateofbirth;
    }

    public void setDirectorsprofile2_dateofbirth(String directorsprofile2_dateofbirth)
    {
        this.directorsprofile2_dateofbirth = directorsprofile2_dateofbirth;
    }

    public String getDirectorsprofile2_socialsecurity()
    {
        return directorsprofile2_socialsecurity;
    }

    public void setDirectorsprofile2_socialsecurity(String directorsprofile2_socialsecurity)
    {
        this.directorsprofile2_socialsecurity = directorsprofile2_socialsecurity;
    }

    public String getDirectorsprofile2_identificationtype()
    {
        return directorsprofile2_identificationtype;
    }

    public void setDirectorsprofile2_identificationtype(String directorsprofile2_identificationtype)
    {
        this.directorsprofile2_identificationtype = directorsprofile2_identificationtype;
    }

    public String getDirectorsprofile2_State()
    {
        return directorsprofile2_State;
    }

    public void setDirectorsprofile2_State(String directorsprofile2_State)
    {
        this.directorsprofile2_State = directorsprofile2_State;
    }

    public String getDirectorsprofile2_address()
    {
        return directorsprofile2_address;
    }

    public void setDirectorsprofile2_address(String directorsprofile2_address)
    {
        this.directorsprofile2_address = directorsprofile2_address;
    }

    public String getDirectorsprofile2_city()
    {
        return directorsprofile2_city;
    }

    public void setDirectorsprofile2_city(String directorsprofile2_city)
    {
        this.directorsprofile2_city = directorsprofile2_city;
    }

    public String getDirectorsprofile2_zip()
    {
        return directorsprofile2_zip;
    }

    public void setDirectorsprofile2_zip(String directorsprofile2_zip)
    {
        this.directorsprofile2_zip = directorsprofile2_zip;
    }

    public String getDirectorsprofile2_country()
    {
        return directorsprofile2_country;
    }

    public void setDirectorsprofile2_country(String directorsprofile2_country)
    {
        this.directorsprofile2_country = directorsprofile2_country;
    }

    public String getDirectorsprofile2_street()
    {
        return directorsprofile2_street;
    }

    public void setDirectorsprofile2_street(String directorsprofile2_street)
    {
        this.directorsprofile2_street = directorsprofile2_street;
    }

    public String getDirectorsprofile2_identificationtypeselect()
    {
        return directorsprofile2_identificationtypeselect;
    }

    public void setDirectorsprofile2_identificationtypeselect(String directorsprofile2_identificationtypeselect)
    {
        this.directorsprofile2_identificationtypeselect = directorsprofile2_identificationtypeselect;
    }

    public String getDirectorsprofile2_nationality()
    {
        return directorsprofile2_nationality;
    }

    public void setDirectorsprofile2_nationality(String directorsprofile2_nationality)
    {
        this.directorsprofile2_nationality = directorsprofile2_nationality;
    }

    public String getDirectorsprofile2_Passportexpirydate()
    {
        return directorsprofile2_Passportexpirydate;
    }

    public void setDirectorsprofile2_Passportexpirydate(String directorsprofile2_Passportexpirydate)
    {
        this.directorsprofile2_Passportexpirydate = directorsprofile2_Passportexpirydate;
    }

    public String getDirectorsprofile2_politicallyexposed()
    {
        return directorsprofile2_politicallyexposed;
    }

    public void setDirectorsprofile2_politicallyexposed(String directorsprofile2_politicallyexposed)
    {
        this.directorsprofile2_politicallyexposed = directorsprofile2_politicallyexposed;
    }

    public String getDirectorsprofile2_criminalrecord()
    {
        return directorsprofile2_criminalrecord;
    }

    public void setDirectorsprofile2_criminalrecord(String directorsprofile2_criminalrecord)
    {
        this.directorsprofile2_criminalrecord = directorsprofile2_criminalrecord;
    }

    public String getDirectorsprofile2_addressproof()
    {
        return directorsprofile2_addressproof;
    }

    public void setDirectorsprofile2_addressproof(String directorsprofile2_addressproof)
    {
        this.directorsprofile2_addressproof = directorsprofile2_addressproof;
    }

    public String getDirectorsprofile2_addressId()
    {
        return directorsprofile2_addressId;
    }

    public void setDirectorsprofile2_addressId(String directorsprofile2_addressId)
    {
        this.directorsprofile2_addressId = directorsprofile2_addressId;
    }

    public String getDirectorsprofile2_owned()
    {
        return directorsprofile2_owned;
    }

    public void setDirectorsprofile2_owned(String directorsprofile2_owned)
    {
        this.directorsprofile2_owned = directorsprofile2_owned;
    }

    public String getDirectorsprofile3()
    {
        return directorsprofile3;
    }

    public void setDirectorsprofile3(String directorsprofile3)
    {
        this.directorsprofile3 = directorsprofile3;
    }

    public String getDirectorsprofile3_title()
    {
        return directorsprofile3_title;
    }

    public void setDirectorsprofile3_title(String directorsprofile3_title)
    {
        this.directorsprofile3_title = directorsprofile3_title;
    }

    public String getDirectorsprofile3_telnocc1()
    {
        return directorsprofile3_telnocc1;
    }

    public void setDirectorsprofile3_telnocc1(String directorsprofile3_telnocc1)
    {
        this.directorsprofile3_telnocc1 = directorsprofile3_telnocc1;
    }

    public String getDirectorsprofile3_telephonenumber()
    {
        return directorsprofile3_telephonenumber;
    }

    public void setDirectorsprofile3_telephonenumber(String directorsprofile3_telephonenumber)
    {
        this.directorsprofile3_telephonenumber = directorsprofile3_telephonenumber;
    }

    public String getDirectorsprofile3_emailaddress()
    {
        return directorsprofile3_emailaddress;
    }

    public void setDirectorsprofile3_emailaddress(String directorsprofile3_emailaddress)
    {
        this.directorsprofile3_emailaddress = directorsprofile3_emailaddress;
    }

    public String getDirectorsprofile3_dateofbirth()
    {
        return directorsprofile3_dateofbirth;
    }

    public void setDirectorsprofile3_dateofbirth(String directorsprofile3_dateofbirth)
    {
        this.directorsprofile3_dateofbirth = directorsprofile3_dateofbirth;
    }

    public String getDirectorsprofile3_socialsecurity()
    {
        return directorsprofile3_socialsecurity;
    }

    public void setDirectorsprofile3_socialsecurity(String directorsprofile3_socialsecurity)
    {
        this.directorsprofile3_socialsecurity = directorsprofile3_socialsecurity;
    }

    public String getDirectorsprofile3_identificationtype()
    {
        return directorsprofile3_identificationtype;
    }

    public void setDirectorsprofile3_identificationtype(String directorsprofile3_identificationtype)
    {
        this.directorsprofile3_identificationtype = directorsprofile3_identificationtype;
    }

    public String getDirectorsprofile3_State()
    {
        return directorsprofile3_State;
    }

    public void setDirectorsprofile3_State(String directorsprofile3_State)
    {
        this.directorsprofile3_State = directorsprofile3_State;
    }

    public String getDirectorsprofile3_address()
    {
        return directorsprofile3_address;
    }

    public void setDirectorsprofile3_address(String directorsprofile3_address)
    {
        this.directorsprofile3_address = directorsprofile3_address;
    }

    public String getDirectorsprofile3_city()
    {
        return directorsprofile3_city;
    }

    public void setDirectorsprofile3_city(String directorsprofile3_city)
    {
        this.directorsprofile3_city = directorsprofile3_city;
    }

    public String getDirectorsprofile3_zip()
    {
        return directorsprofile3_zip;
    }

    public void setDirectorsprofile3_zip(String directorsprofile3_zip)
    {
        this.directorsprofile3_zip = directorsprofile3_zip;
    }

    public String getDirectorsprofile3_country()
    {
        return directorsprofile3_country;
    }

    public void setDirectorsprofile3_country(String directorsprofile3_country)
    {
        this.directorsprofile3_country = directorsprofile3_country;
    }

    public String getDirectorsprofile3_street()
    {
        return directorsprofile3_street;
    }

    public void setDirectorsprofile3_street(String directorsprofile3_street)
    {
        this.directorsprofile3_street = directorsprofile3_street;
    }

    public String getDirectorsprofile3_identificationtypeselect()
    {
        return directorsprofile3_identificationtypeselect;
    }

    public void setDirectorsprofile3_identificationtypeselect(String directorsprofile3_identificationtypeselect)
    {
        this.directorsprofile3_identificationtypeselect = directorsprofile3_identificationtypeselect;
    }

    public String getDirectorsprofile3_nationality()
    {
        return directorsprofile3_nationality;
    }

    public void setDirectorsprofile3_nationality(String directorsprofile3_nationality)
    {
        this.directorsprofile3_nationality = directorsprofile3_nationality;
    }

    public String getDirectorsprofile3_Passportexpirydate()
    {
        return directorsprofile3_Passportexpirydate;
    }

    public void setDirectorsprofile3_Passportexpirydate(String directorsprofile3_Passportexpirydate)
    {
        this.directorsprofile3_Passportexpirydate = directorsprofile3_Passportexpirydate;
    }

    public String getDirectorsprofile3_politicallyexposed()
    {
        return directorsprofile3_politicallyexposed;
    }

    public void setDirectorsprofile3_politicallyexposed(String directorsprofile3_politicallyexposed)
    {
        this.directorsprofile3_politicallyexposed = directorsprofile3_politicallyexposed;
    }

    public String getDirectorsprofile3_criminalrecord()
    {
        return directorsprofile3_criminalrecord;
    }

    public void setDirectorsprofile3_criminalrecord(String directorsprofile3_criminalrecord)
    {
        this.directorsprofile3_criminalrecord = directorsprofile3_criminalrecord;
    }

    public String getDirectorsprofile3_addressproof()
    {
        return directorsprofile3_addressproof;
    }

    public void setDirectorsprofile3_addressproof(String directorsprofile3_addressproof)
    {
        this.directorsprofile3_addressproof = directorsprofile3_addressproof;
    }

    public String getDirectorsprofile3_addressId()
    {
        return directorsprofile3_addressId;
    }

    public void setDirectorsprofile3_addressId(String directorsprofile3_addressId)
    {
        this.directorsprofile3_addressId = directorsprofile3_addressId;
    }

    public String getDirectorsprofile3_owned()
    {
        return directorsprofile3_owned;
    }

    public void setDirectorsprofile3_owned(String directorsprofile3_owned)
    {
        this.directorsprofile3_owned = directorsprofile3_owned;
    }

    public String getAuthorizedsignatoryprofile()
    {
        return authorizedsignatoryprofile;
    }

    public void setAuthorizedsignatoryprofile(String authorizedsignatoryprofile)
    {
        this.authorizedsignatoryprofile = authorizedsignatoryprofile;
    }

    public String getAuthorizedsignatoryprofile_title()
    {
        return authorizedsignatoryprofile_title;
    }

    public void setAuthorizedsignatoryprofile_title(String authorizedsignatoryprofile_title)
    {
        this.authorizedsignatoryprofile_title = authorizedsignatoryprofile_title;
    }

    public String getAuthorizedsignatoryprofile_telnocc1()
    {
        return authorizedsignatoryprofile_telnocc1;
    }

    public void setAuthorizedsignatoryprofile_telnocc1(String authorizedsignatoryprofile_telnocc1)
    {
        this.authorizedsignatoryprofile_telnocc1 = authorizedsignatoryprofile_telnocc1;
    }

    public String getAuthorizedsignatoryprofile_telephonenumber()
    {
        return authorizedsignatoryprofile_telephonenumber;
    }

    public void setAuthorizedsignatoryprofile_telephonenumber(String authorizedsignatoryprofile_telephonenumber)
    {
        this.authorizedsignatoryprofile_telephonenumber = authorizedsignatoryprofile_telephonenumber;
    }

    public String getAuthorizedsignatoryprofile_emailaddress()
    {
        return authorizedsignatoryprofile_emailaddress;
    }

    public void setAuthorizedsignatoryprofile_emailaddress(String authorizedsignatoryprofile_emailaddress)
    {
        this.authorizedsignatoryprofile_emailaddress = authorizedsignatoryprofile_emailaddress;
    }

    public String getAuthorizedsignatoryprofile_dateofbirth()
    {
        return authorizedsignatoryprofile_dateofbirth;
    }

    public void setAuthorizedsignatoryprofile_dateofbirth(String authorizedsignatoryprofile_dateofbirth)
    {
        this.authorizedsignatoryprofile_dateofbirth = authorizedsignatoryprofile_dateofbirth;
    }

    public String getAuthorizedsignatoryprofile_socialsecurity()
    {
        return authorizedsignatoryprofile_socialsecurity;
    }

    public void setAuthorizedsignatoryprofile_socialsecurity(String authorizedsignatoryprofile_socialsecurity)
    {
        this.authorizedsignatoryprofile_socialsecurity = authorizedsignatoryprofile_socialsecurity;
    }

    public String getAuthorizedsignatoryprofile_identificationtype()
    {
        return authorizedsignatoryprofile_identificationtype;
    }

    public void setAuthorizedsignatoryprofile_identificationtype(String authorizedsignatoryprofile_identificationtype)
    {
        this.authorizedsignatoryprofile_identificationtype = authorizedsignatoryprofile_identificationtype;
    }

    public String getAuthorizedsignatoryprofile_State()
    {
        return authorizedsignatoryprofile_State;
    }

    public void setAuthorizedsignatoryprofile_State(String authorizedsignatoryprofile_State)
    {
        this.authorizedsignatoryprofile_State = authorizedsignatoryprofile_State;
    }

    public String getAuthorizedsignatoryprofile_address()
    {
        return authorizedsignatoryprofile_address;
    }

    public void setAuthorizedsignatoryprofile_address(String authorizedsignatoryprofile_address)
    {
        this.authorizedsignatoryprofile_address = authorizedsignatoryprofile_address;
    }

    public String getAuthorizedsignatoryprofile_city()
    {
        return authorizedsignatoryprofile_city;
    }

    public void setAuthorizedsignatoryprofile_city(String authorizedsignatoryprofile_city)
    {
        this.authorizedsignatoryprofile_city = authorizedsignatoryprofile_city;
    }

    public String getAuthorizedsignatoryprofile_zip()
    {
        return authorizedsignatoryprofile_zip;
    }

    public void setAuthorizedsignatoryprofile_zip(String authorizedsignatoryprofile_zip)
    {
        this.authorizedsignatoryprofile_zip = authorizedsignatoryprofile_zip;
    }

    public String getAuthorizedsignatoryprofile_country()
    {
        return authorizedsignatoryprofile_country;
    }

    public void setAuthorizedsignatoryprofile_country(String authorizedsignatoryprofile_country)
    {
        this.authorizedsignatoryprofile_country = authorizedsignatoryprofile_country;
    }

    public String getAuthorizedsignatoryprofile_street()
    {
        return authorizedsignatoryprofile_street;
    }

    public void setAuthorizedsignatoryprofile_street(String authorizedsignatoryprofile_street)
    {
        this.authorizedsignatoryprofile_street = authorizedsignatoryprofile_street;
    }

    public String getAuthorizedsignatoryprofile_identificationtypeselect()
    {
        return authorizedsignatoryprofile_identificationtypeselect;
    }

    public void setAuthorizedsignatoryprofile_identificationtypeselect(String authorizedsignatoryprofile_identificationtypeselect)
    {
        this.authorizedsignatoryprofile_identificationtypeselect = authorizedsignatoryprofile_identificationtypeselect;
    }

    public String getAuthorizedsignatoryprofile_nationality()
    {
        return authorizedsignatoryprofile_nationality;
    }

    public void setAuthorizedsignatoryprofile_nationality(String authorizedsignatoryprofile_nationality)
    {
        this.authorizedsignatoryprofile_nationality = authorizedsignatoryprofile_nationality;
    }

    public String getAuthorizedsignatoryprofile_Passportexpirydate()
    {
        return authorizedsignatoryprofile_Passportexpirydate;
    }

    public void setAuthorizedsignatoryprofile_Passportexpirydate(String authorizedsignatoryprofile_Passportexpirydate)
    {
        this.authorizedsignatoryprofile_Passportexpirydate = authorizedsignatoryprofile_Passportexpirydate;
    }

    public String getAuthorizedsignatoryprofile1_owned()
    {
        return authorizedsignatoryprofile1_owned;
    }

    public void setAuthorizedsignatoryprofile1_owned(String authorizedsignatoryprofile1_owned)
    {
        this.authorizedsignatoryprofile1_owned = authorizedsignatoryprofile1_owned;
    }

    public String getAuthorizedsignatoryprofile2()
    {
        return authorizedsignatoryprofile2;
    }

    public void setAuthorizedsignatoryprofile2(String authorizedsignatoryprofile2)
    {
        this.authorizedsignatoryprofile2 = authorizedsignatoryprofile2;
    }

    public String getAuthorizedsignatoryprofile2_title()
    {
        return authorizedsignatoryprofile2_title;
    }

    public void setAuthorizedsignatoryprofile2_title(String authorizedsignatoryprofile2_title)
    {
        this.authorizedsignatoryprofile2_title = authorizedsignatoryprofile2_title;
    }

    public String getAuthorizedsignatoryprofile2_telnocc1()
    {
        return authorizedsignatoryprofile2_telnocc1;
    }

    public void setAuthorizedsignatoryprofile2_telnocc1(String authorizedsignatoryprofile2_telnocc1)
    {
        this.authorizedsignatoryprofile2_telnocc1 = authorizedsignatoryprofile2_telnocc1;
    }

    public String getAuthorizedsignatoryprofile2_telephonenumber()
    {
        return authorizedsignatoryprofile2_telephonenumber;
    }

    public void setAuthorizedsignatoryprofile2_telephonenumber(String authorizedsignatoryprofile2_telephonenumber)
    {
        this.authorizedsignatoryprofile2_telephonenumber = authorizedsignatoryprofile2_telephonenumber;
    }

    public String getAuthorizedsignatoryprofile2_emailaddress()
    {
        return authorizedsignatoryprofile2_emailaddress;
    }

    public void setAuthorizedsignatoryprofile2_emailaddress(String authorizedsignatoryprofile2_emailaddress)
    {
        this.authorizedsignatoryprofile2_emailaddress = authorizedsignatoryprofile2_emailaddress;
    }

    public String getAuthorizedsignatoryprofile2_dateofbirth()
    {
        return authorizedsignatoryprofile2_dateofbirth;
    }

    public void setAuthorizedsignatoryprofile2_dateofbirth(String authorizedsignatoryprofile2_dateofbirth)
    {
        this.authorizedsignatoryprofile2_dateofbirth = authorizedsignatoryprofile2_dateofbirth;
    }

    public String getAuthorizedsignatoryprofile2_socialsecurity()
    {
        return authorizedsignatoryprofile2_socialsecurity;
    }

    public void setAuthorizedsignatoryprofile2_socialsecurity(String authorizedsignatoryprofile2_socialsecurity)
    {
        this.authorizedsignatoryprofile2_socialsecurity = authorizedsignatoryprofile2_socialsecurity;
    }

    public String getAuthorizedsignatoryprofile2_identificationtype()
    {
        return authorizedsignatoryprofile2_identificationtype;
    }

    public void setAuthorizedsignatoryprofile2_identificationtype(String authorizedsignatoryprofile2_identificationtype)
    {
        this.authorizedsignatoryprofile2_identificationtype = authorizedsignatoryprofile2_identificationtype;
    }

    public String getAuthorizedsignatoryprofile2_State()
    {
        return authorizedsignatoryprofile2_State;
    }

    public void setAuthorizedsignatoryprofile2_State(String authorizedsignatoryprofile2_State)
    {
        this.authorizedsignatoryprofile2_State = authorizedsignatoryprofile2_State;
    }

    public String getAuthorizedsignatoryprofile2_address()
    {
        return authorizedsignatoryprofile2_address;
    }

    public void setAuthorizedsignatoryprofile2_address(String authorizedsignatoryprofile2_address)
    {
        this.authorizedsignatoryprofile2_address = authorizedsignatoryprofile2_address;
    }

    public String getAuthorizedsignatoryprofile2_city()
    {
        return authorizedsignatoryprofile2_city;
    }

    public void setAuthorizedsignatoryprofile2_city(String authorizedsignatoryprofile2_city)
    {
        this.authorizedsignatoryprofile2_city = authorizedsignatoryprofile2_city;
    }

    public String getAuthorizedsignatoryprofile2_zip()
    {
        return authorizedsignatoryprofile2_zip;
    }

    public void setAuthorizedsignatoryprofile2_zip(String authorizedsignatoryprofile2_zip)
    {
        this.authorizedsignatoryprofile2_zip = authorizedsignatoryprofile2_zip;
    }

    public String getAuthorizedsignatoryprofile2_country()
    {
        return authorizedsignatoryprofile2_country;
    }

    public void setAuthorizedsignatoryprofile2_country(String authorizedsignatoryprofile2_country)
    {
        this.authorizedsignatoryprofile2_country = authorizedsignatoryprofile2_country;
    }

    public String getAuthorizedsignatoryprofile2_street()
    {
        return authorizedsignatoryprofile2_street;
    }

    public void setAuthorizedsignatoryprofile2_street(String authorizedsignatoryprofile2_street)
    {
        this.authorizedsignatoryprofile2_street = authorizedsignatoryprofile2_street;
    }

    public String getAuthorizedsignatoryprofile2_identificationtypeselect()
    {
        return authorizedsignatoryprofile2_identificationtypeselect;
    }

    public void setAuthorizedsignatoryprofile2_identificationtypeselect(String authorizedsignatoryprofile2_identificationtypeselect)
    {
        this.authorizedsignatoryprofile2_identificationtypeselect = authorizedsignatoryprofile2_identificationtypeselect;
    }

    public String getAuthorizedsignatoryprofile2_nationality()
    {
        return authorizedsignatoryprofile2_nationality;
    }

    public void setAuthorizedsignatoryprofile2_nationality(String authorizedsignatoryprofile2_nationality)
    {
        this.authorizedsignatoryprofile2_nationality = authorizedsignatoryprofile2_nationality;
    }

    public String getAuthorizedsignatoryprofile2_Passportexpirydate()
    {
        return authorizedsignatoryprofile2_Passportexpirydate;
    }

    public void setAuthorizedsignatoryprofile2_Passportexpirydate(String authorizedsignatoryprofile2_Passportexpirydate)
    {
        this.authorizedsignatoryprofile2_Passportexpirydate = authorizedsignatoryprofile2_Passportexpirydate;
    }

    public String getAuthorizedsignatoryprofile2_owned()
    {
        return authorizedsignatoryprofile2_owned;
    }

    public void setAuthorizedsignatoryprofile2_owned(String authorizedsignatoryprofile2_owned)
    {
        this.authorizedsignatoryprofile2_owned = authorizedsignatoryprofile2_owned;
    }

    public String getAuthorizedsignatoryprofile3()
    {
        return authorizedsignatoryprofile3;
    }

    public void setAuthorizedsignatoryprofile3(String authorizedsignatoryprofile3)
    {
        this.authorizedsignatoryprofile3 = authorizedsignatoryprofile3;
    }

    public String getAuthorizedsignatoryprofile3_title()
    {
        return authorizedsignatoryprofile3_title;
    }

    public void setAuthorizedsignatoryprofile3_title(String authorizedsignatoryprofile3_title)
    {
        this.authorizedsignatoryprofile3_title = authorizedsignatoryprofile3_title;
    }

    public String getAuthorizedsignatoryprofile3_telnocc1()
    {
        return authorizedsignatoryprofile3_telnocc1;
    }

    public void setAuthorizedsignatoryprofile3_telnocc1(String authorizedsignatoryprofile3_telnocc1)
    {
        this.authorizedsignatoryprofile3_telnocc1 = authorizedsignatoryprofile3_telnocc1;
    }

    public String getAuthorizedsignatoryprofile3_telephonenumber()
    {
        return authorizedsignatoryprofile3_telephonenumber;
    }

    public void setAuthorizedsignatoryprofile3_telephonenumber(String authorizedsignatoryprofile3_telephonenumber)
    {
        this.authorizedsignatoryprofile3_telephonenumber = authorizedsignatoryprofile3_telephonenumber;
    }

    public String getAuthorizedsignatoryprofile3_emailaddress()
    {
        return authorizedsignatoryprofile3_emailaddress;
    }

    public void setAuthorizedsignatoryprofile3_emailaddress(String authorizedsignatoryprofile3_emailaddress)
    {
        this.authorizedsignatoryprofile3_emailaddress = authorizedsignatoryprofile3_emailaddress;
    }

    public String getAuthorizedsignatoryprofile3_dateofbirth()
    {
        return authorizedsignatoryprofile3_dateofbirth;
    }

    public void setAuthorizedsignatoryprofile3_dateofbirth(String authorizedsignatoryprofile3_dateofbirth)
    {
        this.authorizedsignatoryprofile3_dateofbirth = authorizedsignatoryprofile3_dateofbirth;
    }

    public String getAuthorizedsignatoryprofile3_socialsecurity()
    {
        return authorizedsignatoryprofile3_socialsecurity;
    }

    public void setAuthorizedsignatoryprofile3_socialsecurity(String authorizedsignatoryprofile3_socialsecurity)
    {
        this.authorizedsignatoryprofile3_socialsecurity = authorizedsignatoryprofile3_socialsecurity;
    }

    public String getAuthorizedsignatoryprofile3_identificationtype()
    {
        return authorizedsignatoryprofile3_identificationtype;
    }

    public void setAuthorizedsignatoryprofile3_identificationtype(String authorizedsignatoryprofile3_identificationtype)
    {
        this.authorizedsignatoryprofile3_identificationtype = authorizedsignatoryprofile3_identificationtype;
    }

    public String getAuthorizedsignatoryprofile3_State()
    {
        return authorizedsignatoryprofile3_State;
    }

    public void setAuthorizedsignatoryprofile3_State(String authorizedsignatoryprofile3_State)
    {
        this.authorizedsignatoryprofile3_State = authorizedsignatoryprofile3_State;
    }

    public String getAuthorizedsignatoryprofile3_address()
    {
        return authorizedsignatoryprofile3_address;
    }

    public void setAuthorizedsignatoryprofile3_address(String authorizedsignatoryprofile3_address)
    {
        this.authorizedsignatoryprofile3_address = authorizedsignatoryprofile3_address;
    }

    public String getAuthorizedsignatoryprofile3_city()
    {
        return authorizedsignatoryprofile3_city;
    }

    public void setAuthorizedsignatoryprofile3_city(String authorizedsignatoryprofile3_city)
    {
        this.authorizedsignatoryprofile3_city = authorizedsignatoryprofile3_city;
    }

    public String getAuthorizedsignatoryprofile3_zip()
    {
        return authorizedsignatoryprofile3_zip;
    }

    public void setAuthorizedsignatoryprofile3_zip(String authorizedsignatoryprofile3_zip)
    {
        this.authorizedsignatoryprofile3_zip = authorizedsignatoryprofile3_zip;
    }

    public String getAuthorizedsignatoryprofile3_country()
    {
        return authorizedsignatoryprofile3_country;
    }

    public void setAuthorizedsignatoryprofile3_country(String authorizedsignatoryprofile3_country)
    {
        this.authorizedsignatoryprofile3_country = authorizedsignatoryprofile3_country;
    }

    public String getAuthorizedsignatoryprofile3_street()
    {
        return authorizedsignatoryprofile3_street;
    }

    public void setAuthorizedsignatoryprofile3_street(String authorizedsignatoryprofile3_street)
    {
        this.authorizedsignatoryprofile3_street = authorizedsignatoryprofile3_street;
    }

    public String getAuthorizedsignatoryprofile3_identificationtypeselect()
    {
        return authorizedsignatoryprofile3_identificationtypeselect;
    }

    public void setAuthorizedsignatoryprofile3_identificationtypeselect(String authorizedsignatoryprofile3_identificationtypeselect)
    {
        this.authorizedsignatoryprofile3_identificationtypeselect = authorizedsignatoryprofile3_identificationtypeselect;
    }

    public String getAuthorizedsignatoryprofile3_nationality()
    {
        return authorizedsignatoryprofile3_nationality;
    }

    public void setAuthorizedsignatoryprofile3_nationality(String authorizedsignatoryprofile3_nationality)
    {
        this.authorizedsignatoryprofile3_nationality = authorizedsignatoryprofile3_nationality;
    }

    public String getAuthorizedsignatoryprofile3_Passportexpirydate()
    {
        return authorizedsignatoryprofile3_Passportexpirydate;
    }

    public void setAuthorizedsignatoryprofile3_Passportexpirydate(String authorizedsignatoryprofile3_Passportexpirydate)
    {
        this.authorizedsignatoryprofile3_Passportexpirydate = authorizedsignatoryprofile3_Passportexpirydate;
    }

    public String getAuthorizedsignatoryprofile3_addressproof()
    {
        return authorizedsignatoryprofile3_addressproof;
    }

    public void setAuthorizedsignatoryprofile3_addressproof(String authorizedsignatoryprofile3_addressproof)
    {
        this.authorizedsignatoryprofile3_addressproof = authorizedsignatoryprofile3_addressproof;
    }

    public String getAuthorizedsignatoryprofile3_addressId()
    {
        return authorizedsignatoryprofile3_addressId;
    }

    public void setAuthorizedsignatoryprofile3_addressId(String authorizedsignatoryprofile3_addressId)
    {
        this.authorizedsignatoryprofile3_addressId = authorizedsignatoryprofile3_addressId;
    }

    public String getAuthorizedsignatoryprofile3_owned()
    {
        return authorizedsignatoryprofile3_owned;
    }

    public void setAuthorizedsignatoryprofile3_owned(String authorizedsignatoryprofile3_owned)
    {
        this.authorizedsignatoryprofile3_owned = authorizedsignatoryprofile3_owned;
    }

    public String getShareholderprofile1_lastname()
    {
        return shareholderprofile1_lastname;
    }

    public void setShareholderprofile1_lastname(String shareholderprofile1_lastname)
    {
        this.shareholderprofile1_lastname = shareholderprofile1_lastname;
    }

    public String getShareholderprofile2_lastname()
    {
        return shareholderprofile2_lastname;
    }

    public void setShareholderprofile2_lastname(String shareholderprofile2_lastname)
    {
        this.shareholderprofile2_lastname = shareholderprofile2_lastname;
    }

    public String getShareholderprofile3_lastname()
    {
        return shareholderprofile3_lastname;
    }

    public void setShareholderprofile3_lastname(String shareholderprofile3_lastname)
    {
        this.shareholderprofile3_lastname = shareholderprofile3_lastname;
    }

    public String getDirectorsprofile_lastname()
    {
        return directorsprofile_lastname;
    }

    public void setDirectorsprofile_lastname(String directorsprofile_lastname)
    {
        this.directorsprofile_lastname = directorsprofile_lastname;
    }

    public String getDirectorsprofile2_lastname()
    {
        return directorsprofile2_lastname;
    }

    public void setDirectorsprofile2_lastname(String directorsprofile2_lastname)
    {
        this.directorsprofile2_lastname = directorsprofile2_lastname;
    }

    public String getDirectorsprofile3_lastname()
    {
        return directorsprofile3_lastname;
    }

    public void setDirectorsprofile3_lastname(String directorsprofile3_lastname)
    {
        this.directorsprofile3_lastname = directorsprofile3_lastname;
    }

    public String getAuthorizedsignatoryprofile_lastname()
    {
        return authorizedsignatoryprofile_lastname;
    }

    public void setAuthorizedsignatoryprofile_lastname(String authorizedsignatoryprofile_lastname)
    {
        this.authorizedsignatoryprofile_lastname = authorizedsignatoryprofile_lastname;
    }

    public String getAuthorizedsignatoryprofile2_lastname()
    {
        return authorizedsignatoryprofile2_lastname;
    }

    public void setAuthorizedsignatoryprofile2_lastname(String authorizedsignatoryprofile2_lastname)
    {
        this.authorizedsignatoryprofile2_lastname = authorizedsignatoryprofile2_lastname;
    }

    public String getAuthorizedsignatoryprofile3_lastname()
    {
        return authorizedsignatoryprofile3_lastname;
    }

    public void setAuthorizedsignatoryprofile3_lastname(String authorizedsignatoryprofile3_lastname)
    {
        this.authorizedsignatoryprofile3_lastname = authorizedsignatoryprofile3_lastname;
    }

    public String getShareholderprofile1PassportIssueDate()
    {
        return shareholderprofile1PassportIssueDate;
    }

    public void setShareholderprofile1PassportIssueDate(String shareholderprofile1PassportIssueDate)
    {
        this.shareholderprofile1PassportIssueDate = shareholderprofile1PassportIssueDate;
    }

    public String getShareholderprofile2PassportIssueDate()
    {
        return shareholderprofile2PassportIssueDate;
    }

    public void setShareholderprofile2PassportIssueDate(String shareholderprofile2PassportIssueDate)
    {
        this.shareholderprofile2PassportIssueDate = shareholderprofile2PassportIssueDate;
    }

    public String getShareholderprofile3PassportIssueDate()
    {
        return shareholderprofile3PassportIssueDate;
    }

    public void setShareholderprofile3PassportIssueDate(String shareholderprofile3PassportIssueDate)
    {
        this.shareholderprofile3PassportIssueDate = shareholderprofile3PassportIssueDate;
    }

    public String getDirectorsprofilePassportissuedate()
    {
        return directorsprofilePassportissuedate;
    }

    public void setDirectorsprofilePassportissuedate(String directorsprofilePassportissuedate)
    {
        this.directorsprofilePassportissuedate = directorsprofilePassportissuedate;
    }

    public String getDirectorsprofile2Passportissuedate()
    {
        return directorsprofile2Passportissuedate;
    }

    public void setDirectorsprofile2Passportissuedate(String directorsprofile2Passportissuedate)
    {
        this.directorsprofile2Passportissuedate = directorsprofile2Passportissuedate;
    }

    public String getDirectorsprofile3Passportissuedate()
    {
        return directorsprofile3Passportissuedate;
    }

    public void setDirectorsprofile3Passportissuedate(String directorsprofile3Passportissuedate)
    {
        this.directorsprofile3Passportissuedate = directorsprofile3Passportissuedate;
    }

    public String getAuthorizedsignatoryprofilePassportissuedate()
    {
        return authorizedsignatoryprofilePassportissuedate;
    }

    public void setAuthorizedsignatoryprofilePassportissuedate(String authorizedsignatoryprofilePassportissuedate)
    {
        this.authorizedsignatoryprofilePassportissuedate = authorizedsignatoryprofilePassportissuedate;
    }

    public String getAuthorizedsignatoryprofile2Passportissuedate()
    {
        return authorizedsignatoryprofile2Passportissuedate;
    }

    public void setAuthorizedsignatoryprofile2Passportissuedate(String authorizedsignatoryprofile2Passportissuedate)
    {
        this.authorizedsignatoryprofile2Passportissuedate = authorizedsignatoryprofile2Passportissuedate;
    }

    public String getAuthorizedsignatoryprofile3Passportissuedate()
    {
        return authorizedsignatoryprofile3Passportissuedate;
    }

    public void setAuthorizedsignatoryprofile3Passportissuedate(String authorizedsignatoryprofile3Passportissuedate)
    {
        this.authorizedsignatoryprofile3Passportissuedate = authorizedsignatoryprofile3Passportissuedate;
    }

    public String getShareholderprofile1_politicallyexposed()
    {
        return shareholderprofile1_politicallyexposed;
    }

    public void setShareholderprofile1_politicallyexposed(String shareholderprofile1_politicallyexposed)
    {
        this.shareholderprofile1_politicallyexposed = shareholderprofile1_politicallyexposed;
    }

    public String getShareholderprofile1_criminalrecord()
    {
        return shareholderprofile1_criminalrecord;
    }

    public void setShareholderprofile1_criminalrecord(String shareholderprofile1_criminalrecord)
    {
        this.shareholderprofile1_criminalrecord = shareholderprofile1_criminalrecord;
    }

    public String getShareholderprofile2_politicallyexposed()
    {
        return shareholderprofile2_politicallyexposed;
    }

    public void setShareholderprofile2_politicallyexposed(String shareholderprofile2_politicallyexposed)
    {
        this.shareholderprofile2_politicallyexposed = shareholderprofile2_politicallyexposed;
    }

    public String getShareholderprofile2_criminalrecord()
    {
        return shareholderprofile2_criminalrecord;
    }

    public void setShareholderprofile2_criminalrecord(String shareholderprofile2_criminalrecord)
    {
        this.shareholderprofile2_criminalrecord = shareholderprofile2_criminalrecord;
    }

    public String getAuthorizedsignatoryprofile2_addressproof()
    {
        return authorizedsignatoryprofile2_addressproof;
    }

    public void setAuthorizedsignatoryprofile2_addressproof(String authorizedsignatoryprofile2_addressproof)
    {
        this.authorizedsignatoryprofile2_addressproof = authorizedsignatoryprofile2_addressproof;
    }

    public String getAuthorizedsignatoryprofile2_addressId()
    {
        return authorizedsignatoryprofile2_addressId;
    }

    public void setAuthorizedsignatoryprofile2_addressId(String authorizedsignatoryprofile2_addressId)
    {
        this.authorizedsignatoryprofile2_addressId = authorizedsignatoryprofile2_addressId;
    }

    public String getShareholderprofile3_politicallyexposed()
    {
        return shareholderprofile3_politicallyexposed;
    }

    public void setShareholderprofile3_politicallyexposed(String shareholderprofile3_politicallyexposed)
    {
        this.shareholderprofile3_politicallyexposed = shareholderprofile3_politicallyexposed;
    }

    public String getShareholderprofile3_criminalrecord()
    {
        return shareholderprofile3_criminalrecord;
    }

    public void setShareholderprofile3_criminalrecord(String shareholderprofile3_criminalrecord)
    {
        this.shareholderprofile3_criminalrecord = shareholderprofile3_criminalrecord;
    }

    public String getAuthorizedsignatoryprofile1_politicallyexposed()
    {
        return authorizedsignatoryprofile1_politicallyexposed;
    }

    public void setAuthorizedsignatoryprofile1_politicallyexposed(String authorizedsignatoryprofile1_politicallyexposed)
    {
        this.authorizedsignatoryprofile1_politicallyexposed = authorizedsignatoryprofile1_politicallyexposed;
    }

    public String getAuthorizedsignatoryprofile1_criminalrecord()
    {
        return authorizedsignatoryprofile1_criminalrecord;
    }

    public void setAuthorizedsignatoryprofile1_criminalrecord(String authorizedsignatoryprofile1_criminalrecord)
    {
        this.authorizedsignatoryprofile1_criminalrecord = authorizedsignatoryprofile1_criminalrecord;
    }

    public String getAuthorizedsignatoryprofile1_addressproof()
    {
        return authorizedsignatoryprofile1_addressproof;
    }

    public void setAuthorizedsignatoryprofile1_addressproof(String authorizedsignatoryprofile1_addressproof)
    {
        this.authorizedsignatoryprofile1_addressproof = authorizedsignatoryprofile1_addressproof;
    }

    public String getAuthorizedsignatoryprofile1_addressId()
    {
        return authorizedsignatoryprofile1_addressId;
    }

    public void setAuthorizedsignatoryprofile1_addressId(String authorizedsignatoryprofile1_addressId)
    {
        this.authorizedsignatoryprofile1_addressId = authorizedsignatoryprofile1_addressId;
    }

    public String getAuthorizedsignatoryprofile2_politicallyexposed()
    {
        return authorizedsignatoryprofile2_politicallyexposed;
    }

    public void setAuthorizedsignatoryprofile2_politicallyexposed(String authorizedsignatoryprofile2_politicallyexposed)
    {
        this.authorizedsignatoryprofile2_politicallyexposed = authorizedsignatoryprofile2_politicallyexposed;
    }

    public String getAuthorizedsignatoryprofile2_criminalrecord()
    {
        return authorizedsignatoryprofile2_criminalrecord;
    }

    public void setAuthorizedsignatoryprofile2_criminalrecord(String authorizedsignatoryprofile2_criminalrecord)
    {
        this.authorizedsignatoryprofile2_criminalrecord = authorizedsignatoryprofile2_criminalrecord;
    }

    public String getAuthorizedsignatoryprofile3_politicallyexposed()
    {
        return authorizedsignatoryprofile3_politicallyexposed;
    }

    public void setAuthorizedsignatoryprofile3_politicallyexposed(String authorizedsignatoryprofile3_politicallyexposed)
    {
        this.authorizedsignatoryprofile3_politicallyexposed = authorizedsignatoryprofile3_politicallyexposed;
    }

    public String getAuthorizedsignatoryprofile3_criminalrecord()
    {
        return authorizedsignatoryprofile3_criminalrecord;
    }

    public void setAuthorizedsignatoryprofile3_criminalrecord(String authorizedsignatoryprofile3_criminalrecord)
    {
        this.authorizedsignatoryprofile3_criminalrecord = authorizedsignatoryprofile3_criminalrecord;
    }

    public String getAuthorizedsignatoryprofile1_designation()
    {
        return authorizedsignatoryprofile1_designation;
    }

    public void setAuthorizedsignatoryprofile1_designation(String authorizedsignatoryprofile1_designation)
    {
        this.authorizedsignatoryprofile1_designation = authorizedsignatoryprofile1_designation;
    }

    public String getAuthorizedsignatoryprofile2_designation()
    {
        return authorizedsignatoryprofile2_designation;
    }

    public void setAuthorizedsignatoryprofile2_designation(String authorizedsignatoryprofile2_designation)
    {
        this.authorizedsignatoryprofile2_designation = authorizedsignatoryprofile2_designation;
    }

    public String getAuthorizedsignatoryprofile3_designation()
    {
        return authorizedsignatoryprofile3_designation;
    }

    public void setAuthorizedsignatoryprofile3_designation(String authorizedsignatoryprofile3_designation)
    {
        this.authorizedsignatoryprofile3_designation = authorizedsignatoryprofile3_designation;
    }

    public String getCorporateshareholder1_owned()
    {
        return corporateshareholder1_owned;
    }

    public void setCorporateshareholder1_owned(String corporateshareholder1_owned)
    {
        this.corporateshareholder1_owned = corporateshareholder1_owned;
    }

    public String getCorporateshareholder2_owned()
    {
        return corporateshareholder2_owned;
    }

    public void setCorporateshareholder2_owned(String corporateshareholder2_owned)
    {
        this.corporateshareholder2_owned = corporateshareholder2_owned;
    }

    public String getCorporateshareholder3_owned()
    {
        return corporateshareholder3_owned;
    }

    public void setCorporateshareholder3_owned(String corporateshareholder3_owned)
    {
        this.corporateshareholder3_owned = corporateshareholder3_owned;
    }
}
