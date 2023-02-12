/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/9/15
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */

/*************************************Business Profile JS****************************************/

function myBusinessmyBusiness(selected,other1,other2)
{
    if(this.document.forms["myformname"][selected].value>=100)
    {
        if(this.document.forms["myformname"][other1].value=="")
            this.document.forms["myformname"][other1].disabled = true;
        if(this.document.forms["myformname"][other2].value=="")
            this.document.forms["myformname"][other2].disabled= true ;
    }
    else
    {
        this.document.forms["myformname"][selected].disabled = false;
        this.document.forms["myformname"][other1].disabled = false;
        this.document.forms["myformname"][other2].disabled = false;
    }

    if(Number(this.document.forms["myformname"][selected].value)+Number(this.document.forms["myformname"][other1].value)+Number(this.document.forms["myformname"][other2].value)>=100)
    {
        if(this.document.forms["myformname"][selected].value=="")
            this.document.forms["myformname"][selected].disabled = true;
        if(this.document.forms["myformname"][other1].value=="")
            this.document.forms["myformname"][other1].disabled = true;
        if(this.document.forms["myformname"][other2].value=="")
            this.document.forms["myformname"][other2].disabled= true ;
    }

}

function myForeignTransaction(selected,other1,other2,other3,other4,other5)
{
    if(this.document.forms["myformname"][selected].value>=100)
    {
        if(this.document.forms["myformname"][other1].value=="")
            this.document.forms["myformname"][other1].disabled = true;
        if(this.document.forms["myformname"][other2].value=="")
            this.document.forms["myformname"][other2].disabled= true ;
        if(this.document.forms["myformname"][other3].value=="")
            this.document.forms["myformname"][other3].disabled= true ;
        if(this.document.forms["myformname"][other4].value=="")
            this.document.forms["myformname"][other4].disabled= true ;
        if(this.document.forms["myformname"][other5].value=="")
            this.document.forms["myformname"][other5].disabled= true ;
    }
    else
    {
        this.document.forms["myformname"][selected].disabled = false;
        this.document.forms["myformname"][other1].disabled = false;
        this.document.forms["myformname"][other2].disabled = false;
        this.document.forms["myformname"][other3].disabled = false;
        this.document.forms["myformname"][other4].disabled = false;
        this.document.forms["myformname"][other5].disabled = false;
    }

    if(Number(this.document.forms["myformname"][selected].value)+Number(this.document.forms["myformname"][other1].value)+Number(this.document.forms["myformname"][other2].value)+Number(this.document.forms["myformname"][other3].value)+Number(this.document.forms["myformname"][other4].value)+Number(this.document.forms["myformname"][other5].value)>=100)
    {
        if(this.document.forms["myformname"][selected].value=="")
            this.document.forms["myformname"][selected].disabled = true;
        if(this.document.forms["myformname"][other1].value=="")
            this.document.forms["myformname"][other1].disabled = true;
        if(this.document.forms["myformname"][other2].value=="")
            this.document.forms["myformname"][other2].disabled= true ;
        if(this.document.forms["myformname"][other3].value=="")
            this.document.forms["myformname"][other3].disabled= true ;
        if(this.document.forms["myformname"][other4].value=="")
            this.document.forms["myformname"][other4].disabled= true ;
        if(this.document.forms["myformname"][other5].value=="")
            this.document.forms["myformname"][other5].disabled= true ;
    }

}

function myBusinessCheckBox(selected,other)
{
    if(selected!="cardtypesaccepted_other")
    {
        this.document.forms["myformname"][other].checked=false;
        this.document.forms["myformname"]['cardtypesaccepted_other_yes'].disabled=true;
    }
    else
    {
        this.document.forms["myformname"]['cardtypesaccepted_visa'].checked=false;
        this.document.forms["myformname"]['cardtypesaccepted_mastercard'].checked=false;
        this.document.forms["myformname"]['cardtypesaccepted_americanexpress'].checked=false;
        this.document.forms["myformname"]['cardtypesaccepted_discover'].checked=false;

        this.document.forms["myformname"]['cardtypesaccepted_diners'].checked=false;
        this.document.forms["myformname"]['cardtypesaccepted_jcb'].checked=false;
    }
}

function myIncomeCompanyCheckBox(selected,other)
{
    if(selected!="income_sources_other")
    {
        this.document.forms["myformname"][other].checked=false;
        this.document.forms["myformname"]['income_sources_other_yes'].disabled=true;
    }
    else
    {
        this.document.forms["myformname"]['loans'].checked=false;
        this.document.forms["myformname"]['income_economic_activity'].checked=false;
        this.document.forms["myformname"]['interest_income'].checked=false;
        this.document.forms["myformname"]['investments'].checked=false;
    }
}



function isNumberKey(evt)
{
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

    return true;
}

// old condition //
/*Shareholder Owned Condition*/
function myShareholder_old(val,secondname,thirdname,option)
{
    var value2 = this.document.forms["myformname"][secondname].value ;
    var value3 = this.document.forms["myformname"][thirdname].value ;
    var sum1= Number(val) + Number(value2);
    var sum2= Number(val) + Number(value2)+Number(value3);
    if((val >=100 || val <=0) && option==1)
    {
        this.document.myformname.shareholderprofile2_owned.value = "";
        this.document.myformname.shareholderprofile2_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile2_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile2.disabled = true;
        this.document.myformname.shareholderprofile2_lastname.disabled = true;
        this.document.myformname.shareholderprofile2_title.disabled = true;
        this.document.myformname.shareholderprofile2_identificationtypeselect.disabled = true;
        $(document.getElementsByName("shareholderprofile2_identificationtypeselect")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile2_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile2_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile2_address.disabled = true;
        this.document.myformname.shareholderprofile2_city.disabled = true;
        this.document.myformname.shareholderprofile2_State.disabled = true;
        this.document.myformname.shareholderprofile2_zip.disabled = true;
        this.document.myformname.shareholderprofile2_country.disabled = true;
        this.document.myformname.shareholderprofile2_street.disabled = true;
        $(document.getElementsByName("shareholderprofile2_country")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile2_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile2_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile2_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile2_nationality.disabled = true;
        this.document.myformname.shareholderprofile2_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile2_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile2_addressproof.disabled = true;
        this.document.myformname.shareholderprofile2_addressId.disabled = true;
        document.getElementById('shareholderprofile2_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile2_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);


        //Nameprinciple 3

        this.document.myformname.shareholderprofile3_owned.value="";
        this.document.myformname.shareholderprofile3_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile3.disabled = true;
        this.document.myformname.shareholderprofile3_lastname.disabled = true;
        this.document.myformname.shareholderprofile3_title.disabled = true;
        this.document.myformname.shareholderprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("shareholderprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile3_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile3_address.disabled = true;
        this.document.myformname.shareholderprofile3_city.disabled = true;
        this.document.myformname.shareholderprofile3_State.disabled = true;
        this.document.myformname.shareholderprofile3_zip.disabled = true;
        this.document.myformname.shareholderprofile3_country.disabled = true;
        this.document.myformname.shareholderprofile3_street.disabled = true;
        $(document.getElementsByName("shareholderprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile3_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile3_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile3_nationality.disabled = true;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile3_addressproof.disabled = true;
        this.document.myformname.shareholderprofile3_addressId.disabled = true;
        document.getElementById('shareholderprofile3_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile3_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(val>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
        }

        //document.getElementById('nameprincipal1_owned').innerHTML="";
        document.getElementById('shareholderprofile2_owned').innerHTML="";
        document.getElementById('shareholderprofile3_owned').innerHTML="";
        //$( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
        /*$( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
         $( document.getElementById('shareholderprofile3_owned')).css('background-color','#ffffff');*/
    }
    else if((val <100 || val <=0) && option==1)
    {
        if(this.document.myformname.shareholderprofile2_owned.value=="")
        {
            this.document.myformname.shareholderprofile2_owned.value = "";
            this.document.myformname.shareholderprofile2_owned.disabled = false;
            $(document.getElementsByName("shareholderprofile2_owned")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.shareholderprofile2.disabled = false;
            this.document.myformname.shareholderprofile2_lastname.disabled = false;
            this.document.myformname.shareholderprofile2_title.disabled = false;
            this.document.myformname.shareholderprofile2_identificationtypeselect.disabled = false;
            $(document.getElementsByName("shareholderprofile2_identificationtypeselect")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.shareholderprofile2_identificationtype.disabled = false;
            this.document.myformname.shareholderprofile2_dateofbirth.disabled = false;
            this.document.myformname.shareholderprofile2_address.disabled = false;
            this.document.myformname.shareholderprofile2_city.disabled = false;
            this.document.myformname.shareholderprofile2_State.disabled = false;
            this.document.myformname.shareholderprofile2_zip.disabled = false;
            this.document.myformname.shareholderprofile2_country.disabled = false;
            this.document.myformname.shareholderprofile2_street.disabled = false;
            $(document.getElementsByName("shareholderprofile2_country")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.shareholderprofile2_telnocc2.disabled = false;
            this.document.myformname.shareholderprofile2_telephonenumber.disabled = false;
            this.document.myformname.shareholderprofile2_emailaddress.disabled = false;
            this.document.myformname.shareholderprofile2_nationality.disabled = false;
            this.document.myformname.shareholderprofile2_passportissuedate.disabled = false;
            this.document.myformname.shareholderprofile2_Passportexpirydate.disabled = false;
            this.document.myformname.shareholderprofile2_addressproof.disabled = false;
            this.document.myformname.shareholderprofile2_addressId.disabled = false;
            document.getElementById('shareholderprofile2_politicallyexposed').disabled = false;
            document.getElementById('shareholderprofile2_criminalrecord').disabled = false;
            //$('input[id*=shareholderprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
            //$('input[id*=shareholderprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);

        }
        document.getElementById('shareholderprofile1_owned').innerHTML="";
        $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
    }
    else if( sum1>=100 && (option==2 || option==1))
    {

        //Nameprinciple 3

        this.document.myformname.shareholderprofile3_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile3.disabled = true;
        this.document.myformname.shareholderprofile3_lastname.disabled = true;
        this.document.myformname.shareholderprofile3_title.disabled = true;
        this.document.myformname.shareholderprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("shareholderprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile3_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile3_address.disabled = true;
        this.document.myformname.shareholderprofile3_city.disabled = true;
        this.document.myformname.shareholderprofile3_State.disabled = true;
        this.document.myformname.shareholderprofile3_zip.disabled = true;
        this.document.myformname.shareholderprofile3_country.disabled = true;
        this.document.myformname.shareholderprofile3_street.disabled = true;
        $(document.getElementsByName("shareholderprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile3_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile3_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile3_nationality.disabled = true;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile3_addressproof.disabled = true;
        this.document.myformname.shareholderprofile3_addressId.disabled = true;
        document.getElementById('shareholderprofile3_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile3_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(sum1>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('shareholderprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#f2dede');

        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            document.getElementById('shareholderprofile2_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
        }

    }
    else if( sum1<100 && (option==2 || option==1))
    {
        //shareholderprofile3
        this.document.myformname.shareholderprofile3_owned.disabled = false;
        $(document.getElementsByName("shareholderprofile3_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.shareholderprofile3.disabled = false;
        this.document.myformname.shareholderprofile3_lastname.disabled = false;
        this.document.myformname.shareholderprofile3_title.disabled = false;
        this.document.myformname.shareholderprofile3_identificationtypeselect.disabled=false;
        $(document.getElementsByName("shareholderprofile3_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile3_identificationtype.disabled = false;
        this.document.myformname.shareholderprofile3_dateofbirth.disabled = false;
        this.document.myformname.shareholderprofile3_address.disabled = false;
        this.document.myformname.shareholderprofile3_city.disabled = false;
        this.document.myformname.shareholderprofile3_State.disabled = false;
        this.document.myformname.shareholderprofile3_zip.disabled = false;
        this.document.myformname.shareholderprofile3_country.disabled = false;
        this.document.myformname.shareholderprofile3_street.disabled = false;
        $(document.getElementsByName("shareholderprofile3_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile3_telnocc2.disabled = false;
        this.document.myformname.shareholderprofile3_telephonenumber.disabled = false;
        this.document.myformname.shareholderprofile3_emailaddress.disabled = false;
        this.document.myformname.shareholderprofile3_nationality.disabled = false;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = false;
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = false;
        this.document.myformname.shareholderprofile3_addressproof.disabled = false;
        this.document.myformname.shareholderprofile3_addressId.disabled = false;
        document.getElementById('shareholderprofile3_politicallyexposed').disabled = false;
        document.getElementById('shareholderprofile3_criminalrecord').disabled = false;


        if(sum1>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('shareholderprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            document.getElementById('shareholderprofile2_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
        }


    }
    else if(sum2>100 && (option==2 || option==1 || option==3))
    {
        document.getElementById('shareholderprofile1_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile2_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile3_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#FFFFFF');
    }


    if(sum2<50)
    {
        if(this.document.myformname.shareholderprofile1_owned.value !="")
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile2_owned.value !="")
        {
            document.getElementById('shareholderprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile3_owned.value !="")
        {
            document.getElementById('shareholderprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum2<100)
    {
        document.getElementById('shareholderprofile1_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile2_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile3_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#FFFFFF');
    }


}

//function for shareholder 4

function myShareholder(val,secondname,thirdname,fourthname,option)
{
    var value2 = this.document.forms["myformname"][secondname].value ;
    var value3 = this.document.forms["myformname"][thirdname].value ;
    var value4 = this.document.forms["myformname"][fourthname].value ;
    var sum1= Number(val) + Number(value2);
    var sum2= Number(val) + Number(value2)+Number(value3);
    var sum3= Number(val) + Number(value2)+Number(value3)+Number(value4);
    if((val >=100 || val <=0) && option==1)
    {
        this.document.myformname.shareholderprofile2_owned.value = "";
        this.document.myformname.shareholderprofile2_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile2_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile2.disabled = true;
        this.document.myformname.shareholderprofile2_lastname.disabled = true;
        this.document.myformname.shareholderprofile2_title.disabled = true;
        this.document.myformname.shareholderprofile2_identificationtypeselect.disabled = true;
        $(document.getElementsByName("shareholderprofile2_identificationtypeselect")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile2_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile2_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile2_address.disabled = true;
        this.document.myformname.shareholderprofile2_city.disabled = true;
        this.document.myformname.shareholderprofile2_State.disabled = true;
        this.document.myformname.shareholderprofile2_zip.disabled = true;
        this.document.myformname.shareholderprofile2_country.disabled = true;
        this.document.myformname.shareholderprofile2_street.disabled = true;
        $(document.getElementsByName("shareholderprofile2_country")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile2_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile2_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile2_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile2_nationality.disabled = true;
        this.document.myformname.shareholderprofile2_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile2_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile2_addressproof.disabled = true;
        this.document.myformname.shareholderprofile2_addressId.disabled = true;
        document.getElementById('shareholderprofile2_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile2_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);


        //Nameprinciple 3

        this.document.myformname.shareholderprofile3_owned.value="";
        this.document.myformname.shareholderprofile3_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile3.disabled = true;
        this.document.myformname.shareholderprofile3_lastname.disabled = true;
        this.document.myformname.shareholderprofile3_title.disabled = true;
        this.document.myformname.shareholderprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("shareholderprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile3_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile3_address.disabled = true;
        this.document.myformname.shareholderprofile3_city.disabled = true;
        this.document.myformname.shareholderprofile3_State.disabled = true;
        this.document.myformname.shareholderprofile3_zip.disabled = true;
        this.document.myformname.shareholderprofile3_country.disabled = true;
        this.document.myformname.shareholderprofile3_street.disabled = true;
        $(document.getElementsByName("shareholderprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile3_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile3_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile3_nationality.disabled = true;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile3_addressproof.disabled = true;
        this.document.myformname.shareholderprofile3_addressId.disabled = true;
        document.getElementById('shareholderprofile3_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile3_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

     ////////////////Nameprinciple 4
        this.document.myformname.shareholderprofile4_owned.value="";
        this.document.myformname.shareholderprofile4_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile4_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile4.disabled = true;
        this.document.myformname.shareholderprofile4_lastname.disabled = true;
        this.document.myformname.shareholderprofile4_title.disabled = true;
        this.document.myformname.shareholderprofile4_identificationtypeselect.disabled=true;
        $(document.getElementsByName("shareholderprofile4_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile4_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile4_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile4_address.disabled = true;
        this.document.myformname.shareholderprofile4_city.disabled = true;
        this.document.myformname.shareholderprofile4_State.disabled = true;
        this.document.myformname.shareholderprofile4_zip.disabled = true;
        this.document.myformname.shareholderprofile4_country.disabled = true;
        this.document.myformname.shareholderprofile4_street.disabled = true;
        $(document.getElementsByName("shareholderprofile4_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile4_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile4_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile4_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile4_nationality.disabled = true;
        this.document.myformname.shareholderprofile4_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile4_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile4_addressproof.disabled = true;
        this.document.myformname.shareholderprofile4_addressId.disabled = true;
        document.getElementById('shareholderprofile4_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile4_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile4_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile4_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(val>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
        }

        //document.getElementById('nameprincipal1_owned').innerHTML="";
        document.getElementById('shareholderprofile2_owned').innerHTML="";
        document.getElementById('shareholderprofile3_owned').innerHTML="";
        document.getElementById('shareholderprofile4_owned').innerHTML="";
        //$( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
        /*$( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
         $( document.getElementById('shareholderprofile3_owned')).css('background-color','#ffffff');*/
    }
    else if((val <100 || val <=0) && option==1)
    {
        if(this.document.myformname.shareholderprofile2_owned.value=="")
        {
            this.document.myformname.shareholderprofile2_owned.value = "";
            this.document.myformname.shareholderprofile2_owned.disabled = false;
            $(document.getElementsByName("shareholderprofile2_owned")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.shareholderprofile2.disabled = false;
            this.document.myformname.shareholderprofile2_lastname.disabled = false;
            this.document.myformname.shareholderprofile2_title.disabled = false;
            this.document.myformname.shareholderprofile2_identificationtypeselect.disabled = false;
            $(document.getElementsByName("shareholderprofile2_identificationtypeselect")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.shareholderprofile2_identificationtype.disabled = false;
            this.document.myformname.shareholderprofile2_dateofbirth.disabled = false;
            this.document.myformname.shareholderprofile2_address.disabled = false;
            this.document.myformname.shareholderprofile2_city.disabled = false;
            this.document.myformname.shareholderprofile2_State.disabled = false;
            this.document.myformname.shareholderprofile2_zip.disabled = false;
            this.document.myformname.shareholderprofile2_country.disabled = false;
            this.document.myformname.shareholderprofile2_street.disabled = false;
            $(document.getElementsByName("shareholderprofile2_country")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.shareholderprofile2_telnocc2.disabled = false;
            this.document.myformname.shareholderprofile2_telephonenumber.disabled = false;
            this.document.myformname.shareholderprofile2_emailaddress.disabled = false;
            this.document.myformname.shareholderprofile2_nationality.disabled = false;
            this.document.myformname.shareholderprofile2_passportissuedate.disabled = false;
            this.document.myformname.shareholderprofile2_Passportexpirydate.disabled = false;
            this.document.myformname.shareholderprofile2_addressproof.disabled = false;
            this.document.myformname.shareholderprofile2_addressId.disabled = false;
            document.getElementById('shareholderprofile2_politicallyexposed').disabled = false;
            document.getElementById('shareholderprofile2_criminalrecord').disabled = false;
            //$('input[id*=shareholderprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
            //$('input[id*=shareholderprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);

        }
        document.getElementById('shareholderprofile1_owned').innerHTML="";
        $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
    }
    else if( sum1>=100 && (option==2 || option==1))
    {

        //Nameprinciple 3

        this.document.myformname.shareholderprofile3_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile3.disabled = true;
        this.document.myformname.shareholderprofile3_lastname.disabled = true;
        this.document.myformname.shareholderprofile3_title.disabled = true;
        this.document.myformname.shareholderprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("shareholderprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile3_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile3_address.disabled = true;
        this.document.myformname.shareholderprofile3_city.disabled = true;
        this.document.myformname.shareholderprofile3_State.disabled = true;
        this.document.myformname.shareholderprofile3_zip.disabled = true;
        this.document.myformname.shareholderprofile3_country.disabled = true;
        this.document.myformname.shareholderprofile3_street.disabled = true;
        $(document.getElementsByName("shareholderprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile3_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile3_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile3_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile3_nationality.disabled = true;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile3_addressproof.disabled = true;
        this.document.myformname.shareholderprofile3_addressId.disabled = true;
        document.getElementById('shareholderprofile3_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile3_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        //Nameprinciple 4
        this.document.myformname.shareholderprofile4_owned.disabled = true;
        $(document.getElementsByName("shareholderprofile4_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.shareholderprofile4.disabled = true;
        this.document.myformname.shareholderprofile4_lastname.disabled = true;
        this.document.myformname.shareholderprofile4_title.disabled = true;
        this.document.myformname.shareholderprofile4_identificationtypeselect.disabled=true;
        $(document.getElementsByName("shareholderprofile4_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile4_identificationtype.disabled = true;
        this.document.myformname.shareholderprofile4_dateofbirth.disabled = true;
        this.document.myformname.shareholderprofile4_address.disabled = true;
        this.document.myformname.shareholderprofile4_city.disabled = true;
        this.document.myformname.shareholderprofile4_State.disabled = true;
        this.document.myformname.shareholderprofile4_zip.disabled = true;
        this.document.myformname.shareholderprofile4_country.disabled = true;
        this.document.myformname.shareholderprofile4_street.disabled = true;
        $(document.getElementsByName("shareholderprofile4_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.shareholderprofile4_telnocc2.disabled = true;
        this.document.myformname.shareholderprofile4_telephonenumber.disabled = true;
        this.document.myformname.shareholderprofile4_emailaddress.disabled = true;
        this.document.myformname.shareholderprofile4_nationality.disabled = true;
        this.document.myformname.shareholderprofile4_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile4_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile4_addressproof.disabled = true;
        this.document.myformname.shareholderprofile4_addressId.disabled = true;
        document.getElementById('shareholderprofile4_politicallyexposed').disabled = true;
        document.getElementById('shareholderprofile4_criminalrecord').disabled = true;
        $('input[id*=shareholderprofile4_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=shareholderprofile4_criminalrecord][type=radio][value="N"]').prop('checked', true);
        if(sum1>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('shareholderprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#f2dede');

        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            document.getElementById('shareholderprofile2_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
        }

    }
    else if( sum1<100 && (option==2 || option==1))
    {
        //shareholderprofile3
        this.document.myformname.shareholderprofile3_owned.disabled = false;
        $(document.getElementsByName("shareholderprofile3_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.shareholderprofile3.disabled = false;
        this.document.myformname.shareholderprofile3_lastname.disabled = false;
        this.document.myformname.shareholderprofile3_title.disabled = false;
        this.document.myformname.shareholderprofile3_identificationtypeselect.disabled=false;
        $(document.getElementsByName("shareholderprofile3_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile3_identificationtype.disabled = false;
        this.document.myformname.shareholderprofile3_dateofbirth.disabled = false;
        this.document.myformname.shareholderprofile3_address.disabled = false;
        this.document.myformname.shareholderprofile3_city.disabled = false;
        this.document.myformname.shareholderprofile3_State.disabled = false;
        this.document.myformname.shareholderprofile3_zip.disabled = false;
        this.document.myformname.shareholderprofile3_country.disabled = false;
        this.document.myformname.shareholderprofile3_street.disabled = false;
        $(document.getElementsByName("shareholderprofile3_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile3_telnocc2.disabled = false;
        this.document.myformname.shareholderprofile3_telephonenumber.disabled = false;
        this.document.myformname.shareholderprofile3_emailaddress.disabled = false;
        this.document.myformname.shareholderprofile3_nationality.disabled = false;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = false;
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = false;
        this.document.myformname.shareholderprofile3_addressproof.disabled = false;
        this.document.myformname.shareholderprofile3_addressId.disabled = false;
        document.getElementById('shareholderprofile3_politicallyexposed').disabled = false;
        document.getElementById('shareholderprofile3_criminalrecord').disabled = false;

        //shareholderprofile4
        this.document.myformname.shareholderprofile4_owned.disabled = false;
        $(document.getElementsByName("shareholderprofile4_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.shareholderprofile4.disabled = false;
        this.document.myformname.shareholderprofile4_lastname.disabled = false;
        this.document.myformname.shareholderprofile4_title.disabled = false;
        this.document.myformname.shareholderprofile4_identificationtypeselect.disabled=false;
        $(document.getElementsByName("shareholderprofile4_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile4_identificationtype.disabled = false;
        this.document.myformname.shareholderprofile4_dateofbirth.disabled = false;
        this.document.myformname.shareholderprofile4_address.disabled = false;
        this.document.myformname.shareholderprofile4_city.disabled = false;
        this.document.myformname.shareholderprofile4_State.disabled = false;
        this.document.myformname.shareholderprofile4_zip.disabled = false;
        this.document.myformname.shareholderprofile4_country.disabled = false;
        this.document.myformname.shareholderprofile4_street.disabled = false;
        $(document.getElementsByName("shareholderprofile4_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile4_telnocc2.disabled = false;
        this.document.myformname.shareholderprofile4_telephonenumber.disabled = false;
        this.document.myformname.shareholderprofile4_emailaddress.disabled = false;
        this.document.myformname.shareholderprofile4_nationality.disabled = false;
        this.document.myformname.shareholderprofile4_passportissuedate.disabled = false;
        this.document.myformname.shareholderprofile4_Passportexpirydate.disabled = false;
        this.document.myformname.shareholderprofile4_addressproof.disabled = false;
        this.document.myformname.shareholderprofile4_addressId.disabled = false;
        document.getElementById('shareholderprofile4_politicallyexposed').disabled = false;
        document.getElementById('shareholderprofile4_criminalrecord').disabled = false;

        if(sum1>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('shareholderprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            document.getElementById('shareholderprofile2_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
        }


    }
    else if(sum2>100 && (option==2 || option==1 || option==3))
    {
        //shareholderprofile4
        this.document.myformname.shareholderprofile4_owned.disabled = false;
        $(document.getElementsByName("shareholderprofile4_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.shareholderprofile4.disabled = false;
        this.document.myformname.shareholderprofile4_lastname.disabled = false;
        this.document.myformname.shareholderprofile4_title.disabled = false;
        this.document.myformname.shareholderprofile4_identificationtypeselect.disabled=false;
        $(document.getElementsByName("shareholderprofile4_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile4_identificationtype.disabled = false;
        this.document.myformname.shareholderprofile4_dateofbirth.disabled = false;
        this.document.myformname.shareholderprofile4_address.disabled = false;
        this.document.myformname.shareholderprofile4_city.disabled = false;
        this.document.myformname.shareholderprofile4_State.disabled = false;
        this.document.myformname.shareholderprofile4_zip.disabled = false;
        this.document.myformname.shareholderprofile4_country.disabled = false;
        this.document.myformname.shareholderprofile4_street.disabled = false;
        $(document.getElementsByName("shareholderprofile4_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.shareholderprofile4_telnocc2.disabled = false;
        this.document.myformname.shareholderprofile4_telephonenumber.disabled = false;
        this.document.myformname.shareholderprofile4_emailaddress.disabled = false;
        this.document.myformname.shareholderprofile4_nationality.disabled = false;
        this.document.myformname.shareholderprofile4_passportissuedate.disabled = false;
        this.document.myformname.shareholderprofile4_Passportexpirydate.disabled = false;
        this.document.myformname.shareholderprofile4_addressproof.disabled = false;
        this.document.myformname.shareholderprofile4_addressId.disabled = false;
        document.getElementById('shareholderprofile4_politicallyexposed').disabled = false;
        document.getElementById('shareholderprofile4_criminalrecord').disabled = false;

        if(sum2>100)
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('shareholderprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#f2dede');
             document.getElementById('shareholderprofile3_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('shareholderprofile3_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('shareholderprofile1_owned').innerHTML="";
            document.getElementById('shareholderprofile2_owned').innerHTML="";
            document.getElementById('shareholderprofile3_owned').innerHTML="";
            $( document.getElementById('shareholderprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
            $( document.getElementById('shareholderprofile3_owned')).css('background-color','#ffffff');
        }

       /* document.getElementById('shareholderprofile1_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile2_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile3_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#FFFFFF');*/
    }
    else if(sum3>100 && (option==2 || option==1 || option==3 || option==4))
    {
        document.getElementById('shareholderprofile1_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile2_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile3_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile4_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile4_owned')).css('background-color', '#FFFFFF');
    }


    if(sum2<25)
    {
        if(this.document.myformname.shareholderprofile1_owned.value !="")
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile2_owned.value !="")
        {
            document.getElementById('shareholderprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile3_owned.value !="")
        {
            document.getElementById('shareholderprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile4_owned.value !="")
        {
            document.getElementById('shareholderprofile4_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile4_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum2<100)
    {
        document.getElementById('shareholderprofile1_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile2_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile3_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile4_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile4_owned')).css('background-color', '#FFFFFF');
    }

    if(sum3<25)
    {
        if(this.document.myformname.shareholderprofile1_owned.value !="")
        {
            this.document.forms["myformname"]['shareholderprofile1_owned'].focus();
            document.getElementById('shareholderprofile1_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile2_owned.value !="")
        {
            document.getElementById('shareholderprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile3_owned.value !="")
        {
            document.getElementById('shareholderprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.shareholderprofile4_owned.value !="")
        {
            document.getElementById('shareholderprofile4_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('shareholderprofile4_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum3<100)
    {
        document.getElementById('shareholderprofile1_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile2_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile3_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('shareholderprofile4_owned').innerHTML = "";
        $(document.getElementById('shareholderprofile4_owned')).css('background-color', '#FFFFFF');
    }


}

//Director owned condition ----SurajT.

function myDirector(val,secondname,thirdname,fourthname,option)
{

    var value2 = document.getElementById([secondname]).value;
    var value3 = document.getElementById([thirdname]).value;
    var value4 = document.getElementById([fourthname]).value;

    var sum1= Number(val) + Number(value2);
    var sum2= Number(val) + Number(value2)+Number(value3);
    var sum3= Number(val) + Number(value2)+Number(value3)+Number(value4);

    if((val >=100 || val <=0) && option==1)
    {
        this.document.myformname.directorsprofile2_owned.value = "";
        this.document.myformname.directorsprofile2_owned.disabled = true;
        $(document.getElementsByName("directorsprofile2_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.directorsprofile2.disabled = true;
        this.document.myformname.directorsprofile2_lastname.disabled = true;
        this.document.myformname.directorsprofile2_title.disabled = true;
        this.document.myformname.directorsprofile2_identificationtypeselect.disabled = true;
        $(document.getElementsByName("directorsprofile2_identificationtypeselect")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.directorsprofile2_identificationtype.disabled = true;
        this.document.myformname.directorsprofile2_dateofbirth.disabled = true;
        this.document.myformname.directorsprofile2_address.disabled = true;
        this.document.myformname.directorsprofile2_city.disabled = true;
        this.document.myformname.directorsprofile2_State.disabled = true;
        this.document.myformname.directorsprofile2_zip.disabled = true;
        this.document.myformname.directorsprofile2_country.disabled = true;
        this.document.myformname.directorsprofile2_street.disabled = true;
        $(document.getElementsByName("directorsprofile2_country")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.directorsprofile2_telnocc1.disabled = true;
        this.document.myformname.directorsprofile2_telephonenumber.disabled = true;
        this.document.myformname.directorsprofile2_emailaddress.disabled = true;
        this.document.myformname.directorsprofile2_nationality.disabled = true;
        this.document.myformname.directorsprofile2_passportissuedate.disabled = true;
        this.document.myformname.directorsprofile2_Passportexpirydate.disabled = true;
        this.document.myformname.directorsprofile2_addressproof.disabled = true;
        this.document.myformname.directorsprofile2_addressId.disabled = true;
        document.getElementById('directorsprofile2_politicallyexposed').disabled = true;
        document.getElementById('directorsprofile2_criminalrecord').disabled = true;
        $('input[id*=directorsprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=directorsprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);


        //Nameprinciple 3

        this.document.myformname.directorsprofile3_owned.value="";
        this.document.myformname.directorsprofile3_owned.disabled = true;
        $(document.getElementsByName("directorsprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.directorsprofile3.disabled = true;
        this.document.myformname.directorsprofile3_lastname.disabled = true;
        this.document.myformname.directorsprofile3_title.disabled = true;
        this.document.myformname.directorsprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("directorsprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.directorsprofile3_identificationtype.disabled = true;
        this.document.myformname.directorsprofile3_dateofbirth.disabled = true;
        this.document.myformname.directorsprofile3_address.disabled = true;
        this.document.myformname.directorsprofile3_city.disabled = true;
        this.document.myformname.directorsprofile3_State.disabled = true;
        this.document.myformname.directorsprofile3_zip.disabled = true;
        this.document.myformname.directorsprofile3_country.disabled = true;
        this.document.myformname.directorsprofile3_street.disabled = true;
        $(document.getElementsByName("directorsprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.directorsprofile3_telnocc1.disabled = true;
        this.document.myformname.directorsprofile3_telephonenumber.disabled = true;
        this.document.myformname.directorsprofile3_emailaddress.disabled = true;
        this.document.myformname.directorsprofile3_nationality.disabled = true;
        this.document.myformname.directorsprofile3_passportissuedate.disabled = true;
        this.document.myformname.directorsprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.directorsprofile3_addressproof.disabled = true;
        this.document.myformname.directorsprofile3_addressId.disabled = true;
        document.getElementById('directorsprofile3_politicallyexposed').disabled = true;
        document.getElementById('directorsprofile3_criminalrecord').disabled = true;
        $('input[id*=directorsprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=directorsprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        ////////////////Nameprinciple 4
        this.document.myformname.directorsprofile4_owned.value="";
        this.document.myformname.directorsprofile4_owned.disabled = true;
        $(document.getElementsByName("directorsprofile4_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.directorsprofile4.disabled = true;
        this.document.myformname.directorsprofile4.disabled = true;
        this.document.myformname.directorsprofile4.disabled = true;
        this.document.myformname.directorsprofile4_identificationtypeselect.disabled=true;
        $(document.getElementsByName("directorsprofile4_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.directorsprofile4_identificationtype.disabled = true;
        this.document.myformname.directorsprofile4_dateofbirth.disabled = true;
        this.document.myformname.directorsprofile4_address.disabled = true;
        this.document.myformname.directorsprofile4_city.disabled = true;
        this.document.myformname.directorsprofile4_State.disabled = true;
        this.document.myformname.directorsprofile4_zip.disabled = true;
        this.document.myformname.directorsprofile4_country.disabled = true;
        this.document.myformname.directorsprofile4_street.disabled = true;
        $(document.getElementsByName("directorsprofile4_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.directorsprofile4_telnocc1.disabled = true;
        this.document.myformname.directorsprofile4_telephonenumber.disabled = true;
        this.document.myformname.directorsprofile4_emailaddress.disabled = true;
        this.document.myformname.directorsprofile4_nationality.disabled = true;
        this.document.myformname.directorsprofile4_passportissuedate.disabled = true;
        this.document.myformname.directorsprofile4_Passportexpirydate.disabled = true;
        this.document.myformname.directorsprofile4_addressproof.disabled = true;
        this.document.myformname.directorsprofile4_addressId.disabled = true;
        document.getElementById('directorsprofile4_politicallyexposed').disabled = true;
        document.getElementById('directorsprofile4_criminalrecord').disabled = true;
        $('input[id*=directorsprofile4_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=directorsprofile4_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(val>100)
        {
            this.document.forms["myformname"]['directorsprofile_owned'].focus();
            document.getElementById('directorsprofile_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('directorsprofile_owned').innerHTML="";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#ffffff');
        }

        //document.getElementById('nameprincipal1_owned').innerHTML="";
        document.getElementById('directorsprofile2_owned').innerHTML="";
        document.getElementById('directorsprofile3_owned').innerHTML="";
        document.getElementById('directorsprofile4_owned').innerHTML="";
        //$( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
        /*$( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
         $( document.getElementById('shareholderprofile3_owned')).css('background-color','#ffffff');*/
    }
    else if((val <100 || val <=0) && option==1)
    {
        if(this.document.myformname.directorsprofile2_owned.value=="")
        {
            this.document.myformname.directorsprofile2_owned.value = "";
            this.document.myformname.directorsprofile2_owned.disabled = false;
            $(document.getElementsByName("directorsprofile2_owned")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.directorsprofile2.disabled = false;
            this.document.myformname.directorsprofile2_lastname.disabled = false;
            this.document.myformname.directorsprofile2_title.disabled = false;
            this.document.myformname.directorsprofile2_identificationtypeselect.disabled = false;
            $(document.getElementsByName("directorsprofile2_identificationtypeselect")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.directorsprofile2_identificationtype.disabled = false;
            this.document.myformname.directorsprofile2_dateofbirth.disabled = false;
            this.document.myformname.directorsprofile2_address.disabled = false;
            this.document.myformname.directorsprofile2_city.disabled = false;
            this.document.myformname.directorsprofile2_State.disabled = false;
            this.document.myformname.directorsprofile2_zip.disabled = false;
            this.document.myformname.directorsprofile2_country.disabled = false;
            this.document.myformname.directorsprofile2_street.disabled = false;
            $(document.getElementsByName("directorsprofile2_country")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.directorsprofile2_telnocc1.disabled = false;
            this.document.myformname.directorsprofile2_telephonenumber.disabled = false;
            this.document.myformname.directorsprofile2_emailaddress.disabled = false;
            this.document.myformname.directorsprofile2_nationality.disabled = false;
            this.document.myformname.directorsprofile2_passportissuedate.disabled = false;
            this.document.myformname.directorsprofile2_Passportexpirydate.disabled = false;
            this.document.myformname.directorsprofile2_addressproof.disabled = false;
            this.document.myformname.directorsprofile2_addressId.disabled = false;
            document.getElementById('directorsprofile2_politicallyexposed').disabled = false;
            document.getElementById('directorsprofile2_criminalrecord').disabled = false;
            //$('input[id*=shareholderprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
            //$('input[id*=shareholderprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);

        }
        document.getElementById('directorsprofile_owned').innerHTML="";
        $( document.getElementById('directorsprofile_owned')).css('background-color','#ffffff');
    }
    else if( sum1>=100 && (option==2 || option==1))
    {

        //Nameprinciple 3

        this.document.myformname.directorsprofile3_owned.disabled = true;
        $(document.getElementsByName("directorsprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.directorsprofile3.disabled = true;
        this.document.myformname.directorsprofile3_lastname.disabled = true;
        this.document.myformname.directorsprofile3_title.disabled = true;
        this.document.myformname.directorsprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("directorsprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.directorsprofile3_identificationtype.disabled = true;
        this.document.myformname.directorsprofile3_dateofbirth.disabled = true;
        this.document.myformname.directorsprofile3_address.disabled = true;
        this.document.myformname.directorsprofile3_city.disabled = true;
        this.document.myformname.directorsprofile3_State.disabled = true;
        this.document.myformname.directorsprofile3_zip.disabled = true;
        this.document.myformname.directorsprofile3_country.disabled = true;
        this.document.myformname.directorsprofile3_street.disabled = true;
        $(document.getElementsByName("directorsprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.directorsprofile3_telnocc1.disabled = true;
        this.document.myformname.directorsprofile3_telephonenumber.disabled = true;
        this.document.myformname.directorsprofile3_emailaddress.disabled = true;
        this.document.myformname.directorsprofile3_nationality.disabled = true;
        this.document.myformname.directorsprofile3_passportissuedate.disabled = true;
        this.document.myformname.directorsprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.directorsprofile3_addressproof.disabled = true;
        this.document.myformname.directorsprofile3_addressId.disabled = true;
        document.getElementById('directorsprofile3_politicallyexposed').disabled = true;
        document.getElementById('directorsprofile3_criminalrecord').disabled = true;
        $('input[id*=directorsprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=directorsprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(sum1>100)
        {
            this.document.forms["myformname"]['directorsprofile_owned'].focus();
            document.getElementById('directorsprofile_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#f2dede');
            document.getElementById('directorsprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile2_owned')).css('background-color','#f2dede');

        }
        else
        {
            document.getElementById('directorsprofile_owned').innerHTML="";
            document.getElementById('directorsprofile2_owned').innerHTML="";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#ffffff');
            $( document.getElementById('directorsprofile2_owned')).css('background-color','#ffffff');
        }

    }
    else if( sum1<100 && (option==2 || option==1))
    {
        //shareholderprofile3
        this.document.myformname.directorsprofile3_owned.disabled = false;
        $(document.getElementsByName("directorsprofile3_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.directorsprofile3.disabled = false;
        this.document.myformname.directorsprofile3_lastname.disabled = false;
        this.document.myformname.directorsprofile3_title.disabled = false;
        this.document.myformname.directorsprofile3_identificationtypeselect.disabled=false;
        $(document.getElementsByName("directorsprofile3_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.directorsprofile3_identificationtype.disabled = false;
        this.document.myformname.directorsprofile3_dateofbirth.disabled = false;
        this.document.myformname.directorsprofile3_address.disabled = false;
        this.document.myformname.directorsprofile3_city.disabled = false;
        this.document.myformname.directorsprofile3_State.disabled = false;
        this.document.myformname.directorsprofile3_zip.disabled = false;
        this.document.myformname.directorsprofile3_country.disabled = false;
        this.document.myformname.directorsprofile3_street.disabled = false;
        $(document.getElementsByName("directorsprofile3_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.directorsprofile3_telnocc1.disabled = false;
        this.document.myformname.directorsprofile3_telephonenumber.disabled = false;
        this.document.myformname.directorsprofile3_emailaddress.disabled = false;
        this.document.myformname.directorsprofile3_nationality.disabled = false;
        this.document.myformname.directorsprofile3_passportissuedate.disabled = false;
        this.document.myformname.directorsprofile3_Passportexpirydate.disabled = false;
        this.document.myformname.directorsprofile3_addressproof.disabled = false;
        this.document.myformname.directorsprofile3_addressId.disabled = false;
        document.getElementById('directorsprofile3_politicallyexposed').disabled = false;
        document.getElementById('directorsprofile3_criminalrecord').disabled = false;

        if(sum1>100)
        {
            this.document.forms["myformname"]['directorsprofile_owned'].focus();
            document.getElementById('directorsprofile_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#f2dede');
            document.getElementById('directorsprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile2_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('directorsprofile_owned').innerHTML="";
            document.getElementById('directorsprofile2_owned').innerHTML="";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#ffffff');
            $( document.getElementById('directorsprofile2_owned')).css('background-color','#ffffff');
        }


    }
    else if(sum2>=75 && (option==2 || option==1 || option==3))
    {
        this.document.myformname.directorsprofile4_owned.disabled = false;
        $(document.getElementsByName("directorsprofile4_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.directorsprofile4.disabled = false;
        this.document.myformname.directorsprofile4_lastname.disabled = false;
        this.document.myformname.directorsprofile4_title.disabled = false;
        this.document.myformname.directorsprofile4_identificationtypeselect.disabled=false;
        $(document.getElementsByName("directorsprofile4_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.directorsprofile4_identificationtype.disabled = false;
        this.document.myformname.directorsprofile4_dateofbirth.disabled = false;
        this.document.myformname.directorsprofile4_address.disabled = false;
        this.document.myformname.directorsprofile4_city.disabled = false;
        this.document.myformname.directorsprofile4_State.disabled = false;
        this.document.myformname.directorsprofile4_zip.disabled = false;
        this.document.myformname.directorsprofile4_country.disabled = false;
        this.document.myformname.directorsprofile4_street.disabled = false;
        $(document.getElementsByName("directorsprofile4_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.directorsprofile4_telnocc1.disabled = false;
        this.document.myformname.directorsprofile4_telephonenumber.disabled = false;
        this.document.myformname.directorsprofile4_emailaddress.disabled = false;
        this.document.myformname.directorsprofile4_nationality.disabled = false;
        this.document.myformname.directorsprofile4_passportissuedate.disabled = false;
        this.document.myformname.directorsprofile4_Passportexpirydate.disabled = false;
        this.document.myformname.directorsprofile4_addressproof.disabled = false;
        this.document.myformname.directorsprofile4_addressId.disabled = false;
        document.getElementById('directorsprofile4_politicallyexposed').disabled = false;
        document.getElementById('directorsprofile4_criminalrecord').disabled = false;

        if(sum2>100)
        {
            this.document.forms["myformname"]['directorsprofile_owned'].focus();
            document.getElementById('directorsprofile_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#f2dede');
            document.getElementById('directorsprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile2_owned')).css('background-color','#f2dede');
            document.getElementById('directorsprofile3_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('directorsprofile3_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('directorsprofile_owned').innerHTML="";
            document.getElementById('directorsprofile2_owned').innerHTML="";
            document.getElementById('directorsprofile3_owned').innerHTML="";
            $( document.getElementById('directorsprofile_owned')).css('background-color','#ffffff');
            $( document.getElementById('directorsprofile2_owned')).css('background-color','#ffffff');
            $( document.getElementById('directorsprofile3_owned')).css('background-color','#ffffff');
        }

    }
    else if(sum3>100 && (option==2 || option==1 || option==3 || option==4))
    {
        document.getElementById('directorsprofile_owned').innerHTML = "";
        $(document.getElementById('directorsprofile_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile2_owned').innerHTML = "";
        $(document.getElementById('directorsprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile3_owned').innerHTML = "";
        $(document.getElementById('directorsprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile4_owned').innerHTML = "";
        $(document.getElementById('directorsprofile4_owned')).css('background-color', '#FFFFFF');
    }


    if(sum2<25)
    {
        if(this.document.myformname.directorsprofile_owned.value !="")
        {
            this.document.forms["myformname"]['directorsprofile_owned'].focus();
            document.getElementById('directorsprofile_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.directorsprofile2_owned.value !="")
        {
            document.getElementById('directorsprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.directorsprofile3_owned.value !="")
        {
            document.getElementById('directorsprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile3_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.directorsprofile4_owned.value !="")
        {
            document.getElementById('directorsprofile4_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile4_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum2<100)
    {
        document.getElementById('directorsprofile_owned').innerHTML = "";
        $(document.getElementById('directorsprofile_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile2_owned').innerHTML = "";
        $(document.getElementById('directorsprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile3_owned').innerHTML = "";
        $(document.getElementById('directorsprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile4_owned').innerHTML = "";
        $(document.getElementById('directorsprofile4_owned')).css('background-color', '#FFFFFF');
    }

    if(sum3<25)
    {
        if(this.document.myformname.directorsprofile_owned.value !="")
        {
            this.document.forms["myformname"]['directorsprofile_owned'].focus();
            document.getElementById('directorsprofile_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.directorsprofile2_owned.value !="")
        {
            document.getElementById('directorsprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.directorsprofile3_owned.value !="")
        {
            document.getElementById('directorsprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile3_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.directorsprofile4_owned.value !="")
        {
            document.getElementById('directorsprofile4_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('directorsprofile4_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum3<100)
    {
        document.getElementById('directorsprofile_owned').innerHTML = "";
        $(document.getElementById('directorsprofile_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile2_owned').innerHTML = "";
        $(document.getElementById('directorsprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile3_owned').innerHTML = "";
        $(document.getElementById('directorsprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('directorsprofile4_owned').innerHTML = "";
        $(document.getElementById('directorsprofile4_owned')).css('background-color', '#FFFFFF');
    }


}

//AuthorizedSignatory owned condition

function myAuthSignatory(val,secondname,thirdname,fourthname,option)
{

    var value2 = document.getElementById([secondname]).value;
    var value3 = document.getElementById([thirdname]).value;
    var value4 = document.getElementById([fourthname]).value;

    var sum1= Number(val) + Number(value2);
    var sum2= Number(val) + Number(value2)+Number(value3);
    var sum3= Number(val) + Number(value2)+Number(value3)+Number(value4);

    if((val >=100 || val <=0) && option==1)
    {
        this.document.myformname.authorizedsignatoryprofile2_owned.value = "";
        this.document.myformname.authorizedsignatoryprofile2_owned.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile2_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile2.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_lastname.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_title.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_identificationtypeselect.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile2_identificationtypeselect")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile2_identificationtype.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_dateofbirth.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_designation.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_address.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_city.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_State.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_zip.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_country.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_street.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile2_country")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile2_telnocc1.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_telephonenumber.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_emailaddress.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_nationality.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_passportissuedate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_Passportexpirydate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_addressproof.disabled = true;
        this.document.myformname.authorizedsignatoryprofile2_addressId.disabled = true;
        document.getElementById('authorizedsignatoryprofile2_politicallyexposed').disabled = true;
        document.getElementById('authorizedsignatoryprofile2_criminalrecord').disabled = true;
        $('input[id*=authorizedsignatoryprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=authorizedsignatoryprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);


        //Nameprinciple 3

        this.document.myformname.authorizedsignatoryprofile3_owned.value="";
        this.document.myformname.authorizedsignatoryprofile3_owned.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile3.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_lastname.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_title.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("authorizedsignatoryprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile3_identificationtype.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_dateofbirth.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_designation.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_address.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_city.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_State.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_zip.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_country.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_street.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile3_telnocc1.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_telephonenumber.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_emailaddress.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_nationality.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_passportissuedate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_addressproof.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_addressId.disabled = true;
        document.getElementById('authorizedsignatoryprofile3_politicallyexposed').disabled = true;
        document.getElementById('authorizedsignatoryprofile3_criminalrecord').disabled = true;
        $('input[id*=authorizedsignatoryprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=authorizedsignatoryprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        ////////////////Nameprinciple 4
        this.document.myformname.authorizedsignatoryprofile4_owned.value="";
        this.document.myformname.authorizedsignatoryprofile4_owned.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile4_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile4.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_identificationtypeselect.disabled=true;
        $(document.getElementsByName("authorizedsignatoryprofile4_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile4_identificationtype.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_dateofbirth.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_designation.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_address.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_city.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_State.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_zip.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_country.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_street.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile4_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile4_telnocc1.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_telephonenumber.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_emailaddress.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_nationality.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_passportissuedate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_Passportexpirydate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile4_addressproof.disabled = true;
        this.document.myformname.authorizedsignaryprofile4_addressId.disabled = true;
        document.getElementById('authorizedsignatoryprofile4_politicallyexposed').disabled = true;
        document.getElementById('authorizedsignatoryprofile4_criminalrecord').disabled = true;
        $('input[id*=authorizedsignatoryprofile4_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=authorizedsignatoryprofile4_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(val>100)
        {
            this.document.forms["myformname"]['authorizedsignatoryprofile1_owned'].focus();
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#ffffff');
        }

        //document.getElementById('nameprincipal1_owned').innerHTML="";
        document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="";
        document.getElementById('authorizedsignatoryprofile3_owned').innerHTML="";
        document.getElementById('authorizedsignatoryprofile4_owned').innerHTML="";
    }
    else if((val <100 || val <=0) && option==1)
    {
        if(this.document.myformname.authorizedsignatoryprofile2_owned.value=="")
        {
            this.document.myformname.authorizedsignatoryprofile2_owned.value = "";
            this.document.myformname.authorizedsignatoryprofile2_owned.disabled = false;
            $(document.getElementsByName("authorizedsignatoryprofile2_owned")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.authorizedsignatoryprofile2.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_lastname.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_title.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_identificationtypeselect.disabled = false;
            $(document.getElementsByName("authorizedsignatoryprofile2_identificationtypeselect")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.authorizedsignatoryprofile2_identificationtype.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_dateofbirth.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_designation.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_address.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_city.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_State.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_zip.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_country.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_street.disabled = false;
            $(document.getElementsByName("authorizedsignatoryprofile2_country")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.authorizedsignatoryprofile2_telnocc1.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_telephonenumber.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_emailaddress.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_nationality.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_passportissuedate.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_Passportexpirydate.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_addressproof.disabled = false;
            this.document.myformname.authorizedsignatoryprofile2_addressId.disabled = false;
            document.getElementById('authorizedsignatoryprofile2_politicallyexposed').disabled = false;
            document.getElementById('authorizedsignatoryprofile2_criminalrecord').disabled = false;
            //$('input[id*=shareholderprofile2_politicallyexposed][type=radio][value="N"]').prop('checked', true);
            //$('input[id*=shareholderprofile2_criminalrecord][type=radio][value="N"]').prop('checked', true);

        }
        document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="";
        $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#ffffff');
    }
    else if( sum1>=100 && (option==2 || option==1))
    {
        //Nameprinciple 3
        this.document.myformname.authorizedsignatoryprofile3_owned.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile3.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_lastname.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_title.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("authorizedsignatoryprofile3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile3_identificationtype.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_dateofbirth.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_designation.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_address.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_city.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_State.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_zip.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_country.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_street.disabled = true;
        $(document.getElementsByName("authorizedsignatoryprofile3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.authorizedsignatoryprofile3_telnocc1.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_telephonenumber.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_emailaddress.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_nationality.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_passportissuedate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_addressproof.disabled = true;
        this.document.myformname.authorizedsignatoryprofile3_addressId.disabled = true;
        document.getElementById('authorizedsignatoryprofile3_politicallyexposed').disabled = true;
        document.getElementById('authorizedsignatoryprofile3_criminalrecord').disabled = true;
        $('input[id*=authorizedsignatoryprofile3_politicallyexposed][type=radio][value="N"]').prop('checked', true);
        $('input[id*=authorizedsignatoryprofile3_criminalrecord][type=radio][value="N"]').prop('checked', true);

        if(sum1>100)
        {
            this.document.forms["myformname"]['authorizedsignatoryprofile1_owned'].focus();
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color','#f2dede');

        }
        else
        {
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="";
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color','#ffffff');
        }

    }
    else if( sum1<100 && (option==2 || option==1))
    {
        //shareholderprofile3
        this.document.myformname.authorizedsignatoryprofile3_owned.disabled = false;
        $(document.getElementsByName("authorizedsignatoryprofile3_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.authorizedsignatoryprofile3.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_lastname.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_title.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_identificationtypeselect.disabled=false;
        $(document.getElementsByName("authorizedsignatoryprofile3_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.authorizedsignatoryprofile3_identificationtype.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_dateofbirth.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_designation.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_address.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_city.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_State.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_zip.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_country.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_street.disabled = false;
        $(document.getElementsByName("authorizedsignatoryprofile3_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.authorizedsignatoryprofile3_telnocc1.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_telephonenumber.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_emailaddress.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_nationality.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_passportissuedate.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_Passportexpirydate.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_addressproof.disabled = false;
        this.document.myformname.authorizedsignatoryprofile3_addressId.disabled = false;
        document.getElementById('authorizedsignatoryprofile3_politicallyexposed').disabled = false;
        document.getElementById('authorizedsignatoryprofile3_criminalrecord').disabled = false;

        if(sum1>100)
        {
            this.document.forms["myformname"]['authorizedsignatoryprofile1_owned'].focus();
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="";
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color','#ffffff');
        }


    }
    else if(sum2>=75 && (option==2 || option==1 || option==3))
    {
        //shareholderprofile4
        this.document.myformname.authorizedsignatoryprofile4_owned.disabled = false;
        $(document.getElementsByName("authorizedsignatoryprofile4_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.authorizedsignatoryprofile4.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_lastname.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_title.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_identificationtypeselect.disabled=false;
        $(document.getElementsByName("authorizedsignatoryprofile4_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.authorizedsignatoryprofile4_identificationtype.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_dateofbirth.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_designation.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_address.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_city.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_State.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_zip.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_country.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_street.disabled = false;
        $(document.getElementsByName("authorizedsignatoryprofile4_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.authorizedsignatoryprofile4_telnocc1.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_telephonenumber.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_emailaddress.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_nationality.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_passportissuedate.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_Passportexpirydate.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_addressproof.disabled = false;
        this.document.myformname.authorizedsignatoryprofile4_addressId.disabled = false;
        document.getElementById('authorizedsignatoryprofile4_politicallyexposed').disabled = false;
        document.getElementById('authorizedsignatoryprofile4_criminalrecord').disabled = false;

        if(sum2>100)
        {
            this.document.forms["myformname"]['authorizedsignatoryprofile1_owned'].focus();
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#f2dede');
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color','#f2dede');
            document.getElementById('authorizedsignatoryprofile3_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML="";
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML="";
            document.getElementById('authorizedsignatoryprofile3_owned').innerHTML="";
            $( document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color','#ffffff');
            $( document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color','#ffffff');
            $( document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color','#ffffff');
        }
    }
    else if(sum3>100 && (option==2 || option==1 || option==3 || option==4))
    {
        document.getElementById('authorizedsignatoryprofile1_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile2_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile3_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile4_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile4_owned')).css('background-color', '#FFFFFF');
    }


    if(sum2<25)
    {
        if(this.document.myformname.authorizedsignatoryprofile1_owned.value !="")
        {
            this.document.forms["myformname"]['authorizedsignatoryprofile1_owned'].focus();
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.authorizedsignatoryprofile2_owned.value !="")
        {
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.authorizedsignatoryprofile3_owned.value !="")
        {
            document.getElementById('authorizedsignatoryprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.authorizedsignatoryprofile4_owned.value !="")
        {
            document.getElementById('authorizedsignatoryprofile4_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile4_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum2<100)
    {
        document.getElementById('authorizedsignatoryprofile1_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile2_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile3_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile4_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile4_owned')).css('background-color', '#FFFFFF');
    }

    if(sum3<25)
    {
        if(this.document.myformname.authorizedsignatoryprofile1_owned.value !="")
        {
            this.document.forms["myformname"]['authorizedsignatoryprofile1_owned'].focus();
            document.getElementById('authorizedsignatoryprofile1_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.authorizedsignatoryprofile2_owned.value !="")
        {
            document.getElementById('authorizedsignatoryprofile2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.authorizedsignatoryprofile3_owned.value !="")
        {
            document.getElementById('authorizedsignatoryprofile3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.authorizedsignatoryprofile4_owned.value !="")
        {
            document.getElementById('authorizedsignatoryprofile4_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('authorizedsignatoryprofile4_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum3<100)
    {
        document.getElementById('authorizedsignatoryprofile1_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile2_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile3_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile3_owned')).css('background-color', '#FFFFFF');

        document.getElementById('authorizedsignatoryprofile4_owned').innerHTML = "";
        $(document.getElementById('authorizedsignatoryprofile4_owned')).css('background-color', '#FFFFFF');
    }


}

//Add new
//function for Calculating refund and chargeBack ratios last month
function isNumberKey(evt)
{
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

    return true;
}
function calculateLastMonth()
{

    var  salesVolume=this.document.forms["myformname"]['salesvolume_lastmonth'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_lastmonth'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_lastmonth'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if(Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_lastmonth'].value="";
            document.getElementById('refundratio_lastmonth').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_lastmonth')).css('background-color','#f2dede');
        }
        else
        {
            var refundRatio= (Number(refundVolume)/Number(salesVolume))*100;
            refundRatio=refundRatio.toFixed(2);
            this.document.forms["myformname"]['refundratio_lastmonth'].value="";
            document.getElementById('refundratio_lastmonth').innerHTML = "";
            $(document.getElementById('refundratio_lastmonth')).css('background-color','#FFFFFF');
            this.document.forms["myformname"]['refundratio_lastmonth'].value=refundRatio;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_lastmonth'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_lastmonth'].value = "";
            document.getElementById('chargebackratio_lastmonth').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_lastmonth')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio = (Number(chargeBackVolume)/Number(salesVolume)) * 100;
            chargeBackRatio = chargeBackRatio.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_lastmonth'].value = "";
            document.getElementById('chargebackratio_lastmonth').innerHTML = "";
            $(document.getElementById('chargebackratio_lastmonth')).css('background-color','#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_lastmonth'].value = chargeBackRatio;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_lastmonth'].value="";
    }
}

function calculate2monthsago() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_2monthsago'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_2monthsago'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_2monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_2monthsago'].value = "";
            document.getElementById('refundratio_2monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_2monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatio2 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio2 = refundRatio2.toFixed(2);
            this.document.forms["myformname"]['refundratio_2monthsago'].value = "";
            document.getElementById('refundratio_2monthsago').innerHTML = "";
            $(document.getElementById('refundratio_2monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_2monthsago'].value = refundRatio2;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_2monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_2monthsago'].value = "";
            document.getElementById('chargebackratio_2monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_2monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio2 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio2 = chargeBackRatio2.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_2monthsago'].value = "";
            document.getElementById('chargebackratio_2monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_2monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_2monthsago'].value = chargeBackRatio2;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_2monthsago'].value="";
    }
}

//Add new
//function for Calculating refund and chargeBack ratios 3 month
function calculate3monthsago() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_3monthsago'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_3monthsago'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_3monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_3monthsago'].value = "";
            document.getElementById('refundratio_3monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_3monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatio3 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio3 = refundRatio3.toFixed(2);
            this.document.forms["myformname"]['refundratio_3monthsago'].value = "";
            document.getElementById('refundratio_3monthsago').innerHTML = "";
            $(document.getElementById('refundratio_3monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_3monthsago'].value = refundRatio3;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_3monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_3monthsago'].value = "";
            document.getElementById('chargebackratio_3monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_3monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio3 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio3 = chargeBackRatio3.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_3monthsago'].value = "";
            document.getElementById('chargebackratio_3monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_3monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_3monthsago'].value = chargeBackRatio3;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_3monthsago'].value="";
    }
}
//function for Calculating refund and chargeBack ratios 4 month
function calculate4monthsago() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_4monthsago'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_4monthsago'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_4monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_4monthsago'].value = "";
            document.getElementById('refundratio_4monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_4monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatio4 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio4 = refundRatio4.toFixed(2);
            this.document.forms["myformname"]['refundratio_4monthsago'].value = "";
            document.getElementById('refundratio_4monthsago').innerHTML = "";
            $(document.getElementById('refundratio_4monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_4monthsago'].value = refundRatio4;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_4monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_4monthsago'].value = "";
            document.getElementById('chargebackratio_4monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_4monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio4 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio4 = chargeBackRatio4.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_4monthsago'].value = "";
            document.getElementById('chargebackratio_4monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_4monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_4monthsago'].value = chargeBackRatio4;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_4monthsago'].value="";
    }
}
//function for Calculating refund and chargeBack ratios 5 month
function calculate5monthsago() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_5monthsago'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_5monthsago'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_5monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_5monthsago'].value = "";
            document.getElementById('refundratio_5monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_5monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatio5 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio5 = refundRatio5.toFixed(2);
            this.document.forms["myformname"]['refundratio_5monthsago'].value = "";
            document.getElementById('refundratio_5monthsago').innerHTML = "";
            $(document.getElementById('refundratio_5monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_5monthsago'].value = refundRatio5;

        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_5monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_5monthsago'].value = "";
            document.getElementById('chargebackratio_5monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_5monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio5 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio5 = chargeBackRatio5.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_5monthsago'].value = "";
            document.getElementById('chargebackratio_5monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_5monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_5monthsago'].value = chargeBackRatio5;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_5monthsago'].value="";
    }
}
//function for Calculating refund and chargeBack ratios 6 month
function calculate6monthsago() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_6monthsago'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_6monthsago'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_6monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_6monthsago'].value = "";
            document.getElementById('refundratio_6monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_6monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatio6 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio6 = refundRatio6.toFixed(2);
            this.document.forms["myformname"]['refundratio_6monthsago'].value = "";
            document.getElementById('refundratio_6monthsago').innerHTML = "";
            $(document.getElementById('refundratio_6monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_6monthsago'].value = refundRatio6;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_6monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_6monthsago'].value = "";
            document.getElementById('chargebackratio_6monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_6monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio6 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio6 = chargeBackRatio6.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_6monthsago'].value = "";
            document.getElementById('chargebackratio_6monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_6monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_6monthsago'].value = chargeBackRatio6;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_6monthsago'].value="";
    }
}

/*
 function calculate12monthsago() {
 var  salesVolume=this.document.forms["myformname"]['salesvolume_12monthsago'].value;
 var  refundVolume=this.document.forms["myformname"]['refundsvolume_12monthsago'].value;
 var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_12monthsago'].value;

 if(salesVolume!="" && refundVolume!="")
 {
 if (Number(refundVolume) > Number(salesVolume))
 {
 this.document.forms["myformname"]['refundsvolume_12monthsago'].value = "";
 document.getElementById('refundsvolume_12monthsago').innerHTML = "Refund volume should be less than Sales volume";
 $(document.getElementById('refundsvolume_12monthsago')).css('background-color', '#f2dede');
 }
 else
 {
 var refundRatio12 = (Number(refundVolume) / Number(salesVolume)) * 100;
 refundRatio12 = refundRatio12.toFixed(2);
 this.document.forms["myformname"]['refundsvolume_12monthsago'].value = "";
 document.getElementById('refundsvolume_12monthsago').innerHTML = "";
 $(document.getElementById('refundsvolume_12monthsago')).css('background-color', '#FFFFFF');
 this.document.forms["myformname"]['refundsvolume_12monthsago'].value = refundRatio12;
 }
 }
 else
 {
 this.document.forms["myformname"]['refundsvolume_12monthsago'].value="";
 }

 if(salesVolume!="" && chargeBackVolume!="")
 {
 if (Number(chargeBackVolume) > Number(salesVolume))
 {
 this.document.forms["myformname"]['chargebackvolume_12monthsago'].value = "";
 document.getElementById('chargebackvolume_12monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
 $(document.getElementById('chargebackvolume_12monthsago')).css('background-color', '#f2dede');
 }
 else
 {
 var chargeBackRatio12= (Number(chargeBackVolume) / Number(salesVolume)) * 100;
 chargeBackRatio12 = chargeBackRatio12.toFixed(2);
 this.document.forms["myformname"]['chargebackvolume_12monthsago'].value = "";
 document.getElementById('chargebackvolume_12monthsago').innerHTML = "";
 $(document.getElementById('chargebackvolume_12monthsago')).css('background-color', '#FFFFFF');
 this.document.forms["myformname"]['chargebackvolume_12monthsago'].value = chargeBackRatio12;
 }
 }
 else
 {
 this.document.forms["myformname"]['chargebackvolume_12monthsago'].value="";
 }
 }*/

//function for Calculating refund and chargeBack ratios 12 month
function calculate12monthsago() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_12monthsago'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_12monthsago'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_12monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_12monthsago'].value = "";
            document.getElementById('refundratio_12monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_12monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatio12 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio12 = refundRatio12.toFixed(2);
            this.document.forms["myformname"]['refundratio_12monthsago'].value = "";
            document.getElementById('refundratio_12monthsago').innerHTML = "";
            $(document.getElementById('refundratio_12monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_12monthsago'].value = refundRatio12;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_12monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_12monthsago'].value = "";
            document.getElementById('chargebackratio_12monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_12monthsago')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatio12 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio12 = chargeBackRatio12.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_12monthsago'].value = "";
            document.getElementById('chargebackratio_12monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_12monthsago')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_12monthsago'].value = chargeBackRatio12;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_12monthsago'].value="";
    }
}

//function for Calculating refund and chargeBack ratios 2 years
function calculateyear2() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_year2'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_year2'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_year2'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_year2'].value = "";
            document.getElementById('refundratio_year2').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_year2')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatioyear2 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatioyear2 = refundRatioyear2.toFixed(2);
            this.document.forms["myformname"]['refundratio_year2'].value = "";
            document.getElementById('refundratio_year2').innerHTML = "";
            $(document.getElementById('refundratio_year2')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_year2'].value = refundRatioyear2;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_year2'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_year2'].value = "";
            document.getElementById('chargebackratio_year2').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_year2')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatioyear2 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatioyear2 = chargeBackRatioyear2.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_year2'].value = "";
            document.getElementById('chargebackratio_year2').innerHTML = "";
            $(document.getElementById('chargebackratio_year2')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_year2'].value = chargeBackRatioyear2;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_year2'].value="";
    }
}

//function for Calculating refund and chargeBack ratios 3 years
function calculateyear3() {
    var  salesVolume=this.document.forms["myformname"]['salesvolume_year3'].value;
    var  refundVolume=this.document.forms["myformname"]['refundsvolume_year3'].value;
    var  chargeBackVolume=this.document.forms["myformname"]['chargebackvolume_year3'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['refundratio_year3'].value = "";
            document.getElementById('refundratio_year3').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_year3')).css('background-color', '#f2dede');
        }
        else
        {
            var refundRatioyear3 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatioyear3 = refundRatioyear3.toFixed(2);
            this.document.forms["myformname"]['refundratio_year3'].value = "";
            document.getElementById('refundratio_year3').innerHTML = "";
            $(document.getElementById('refundratio_year3')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['refundratio_year3'].value = refundRatioyear3;
        }
    }
    else
    {
        this.document.forms["myformname"]['refundratio_year3'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.forms["myformname"]['chargebackratio_year3'].value = "";
            document.getElementById('chargebackratio_year3').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_year3')).css('background-color', '#f2dede');
        }
        else
        {
            var chargeBackRatioyear3 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatioyear3 = chargeBackRatioyear3.toFixed(2);
            this.document.forms["myformname"]['chargebackratio_year3'].value = "";
            document.getElementById('chargebackratio_year3').innerHTML = "";
            $(document.getElementById('chargebackratio_year3')).css('background-color', '#FFFFFF');
            this.document.forms["myformname"]['chargebackratio_year3'].value = chargeBackRatioyear3;
        }
    }
    else
    {
        this.document.forms["myformname"]['chargebackratio_year3'].value="";
    }
}

function myUpload(ctoken,iframefiled) {
    document.getElementById("myformname").enctype = "multipart/form-data";
    document.myformname.action="/merchant/servlet/UploadServlet?ctoken="+ctoken+"&pageNoInJSP=6&copyiframe="+iframefiled;
}
function myUploadNew(ctoken,alternateName,iframefiled) {
   // alert("inside myuploadNew" );
    //alert("alternate name : "+alternateName)
    document.getElementById("myformname").enctype = "multipart/form-data";
    document.myformname.action="/merchant/servlet/UploadServlet?ctoken="+ctoken+"&alternate_name="+alternateName+"&pageNoInJSP=6&copyiframe="+iframefiled;
}