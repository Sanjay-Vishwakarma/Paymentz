/**
 * Created by Suraj on 4/16/2018.
 */

$(function()
{
    /*************************************ON LOAD FUNDTION STARTS*************************************/

    /******************************Shareholder***********************/

    var share1=$('#numOfShareholders').val();

    if(share1==0)
    {
        /*document.getElementById('shareholder').value="";*/
    }
    if(share1==1)
    {

        document.getElementsByClassName("share1")[0].style.visibility='inherit';
        document.getElementsByClassName("share1")[0].style.display='block';

        document.getElementsByClassName("share2")[0].style.display='none';
        document.getElementsByClassName("share3")[0].style.display='none';
        document.getElementsByClassName("share4")[0].style.display='none';

    }
    else if(share1==2)
    {
        //alert("22222");
        document.getElementsByClassName("share1")[0].style.visibility='inherit';
        document.getElementsByClassName("share1")[0].style.display='block';

        document.getElementsByClassName("share2")[0].style.visibility='inherit';
        document.getElementsByClassName("share2")[0].style.display='block';

        document.getElementsByClassName("share3")[0].style.display='none';
        document.getElementsByClassName("share4")[0].style.display='none';

    }
    else if(share1==3)
    {
        //alert("3333");
        document.getElementsByClassName("share1")[0].style.visibility='inherit';
        document.getElementsByClassName("share1")[0].style.display='block';

        document.getElementsByClassName("share2")[0].style.visibility='inherit';
        document.getElementsByClassName("share2")[0].style.display='block';

        document.getElementsByClassName("share3")[0].style.visibility='inherit';
        document.getElementsByClassName("share3")[0].style.display='block';

        document.getElementsByClassName("share4")[0].style.display='none';
    }
    else if(share1==4)
    {
        //alert("4444");
        document.getElementsByClassName("share1")[0].style.visibility='inherit';
        document.getElementsByClassName("share1")[0].style.display='block';

        document.getElementsByClassName("share2")[0].style.visibility='inherit';
        document.getElementsByClassName("share2")[0].style.display='block';

        document.getElementsByClassName("share3")[0].style.visibility='inherit';
        document.getElementsByClassName("share3")[0].style.display='block';

        document.getElementsByClassName("share4")[0].style.visibility='inherit';
        document.getElementsByClassName("share4")[0].style.display='block';
    }



    /******************************Director***********************/

    var dir1=$('#numOfDirectors').val()
    ///alert("director newcount= "+dir1);

    if(dir1==0)
    {
        //document.getElementsByClassName("directorAll")[0].style.display='none';
        //document.getElementsByClassName("dir1")[0].style.display='none';
        //document.getElementsByClassName("dir2")[0].style.display='none';
        //document.getElementsByClassName("dir3")[0].style.display='none';
    }
    else if(dir1==1)
    {
        document.getElementsByClassName("dir1")[0].style.visibility='inherit';
        document.getElementsByClassName("dir1")[0].style.display='block';

        document.getElementsByClassName("dir2")[0].style.display='none';
        document.getElementsByClassName("dir3")[0].style.display='none';
        document.getElementsByClassName("dir4")[0].style.display='none';

    }
    else if(dir1==2)
    {
        document.getElementsByClassName("dir1")[0].style.visibility='inherit';
        document.getElementsByClassName("dir1")[0].style.display='block';

        document.getElementsByClassName("dir2")[0].style.visibility='inherit';
        document.getElementsByClassName("dir2")[0].style.display='block';

        document.getElementsByClassName("dir3")[0].style.display='none';
        document.getElementsByClassName("dir4")[0].style.display='none';
    }
    else if(dir1==3)
    {
        document.getElementsByClassName("dir1")[0].style.visibility='inherit';
        document.getElementsByClassName("dir1")[0].style.display='block';

        document.getElementsByClassName("dir2")[0].style.visibility='inherit';
        document.getElementsByClassName("dir2")[0].style.display='block';

        document.getElementsByClassName("dir3")[0].style.visibility='inherit';
        document.getElementsByClassName("dir3")[0].style.display='block';

        document.getElementsByClassName("dir4")[0].style.display='none';
    }

    else if(dir1==4)
    {
        document.getElementsByClassName("dir1")[0].style.visibility='inherit';
        document.getElementsByClassName("dir1")[0].style.display='block';

        document.getElementsByClassName("dir2")[0].style.visibility='inherit';
        document.getElementsByClassName("dir2")[0].style.display='block';

        document.getElementsByClassName("dir3")[0].style.visibility='inherit';
        document.getElementsByClassName("dir3")[0].style.display='block';

        document.getElementsByClassName("dir4")[0].style.visibility='inherit';
        document.getElementsByClassName("dir4")[0].style.display='block';
    }

    /******************************Authorized***********************/
    var auth1=$('#numOfAuthrisedSignatory').val();
    //("authorized newcount= "+auth1);

    if(auth1==0)
    {
        //document.getElementsByClassName("authorizedAll")[0].style.display='none';
        document.getElementsByClassName("authorized1")[0].style.display='none';
        document.getElementsByClassName("authorized2")[0].style.display='none';
        document.getElementsByClassName("authorized3")[0].style.display='none';
        document.getElementsByClassName("authorized4")[0].style.display='none';
    }
    else if(auth1==1)
    {
        //alert("authorized 1111111111= "+auth1);
        document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized1")[0].style.display='block';

        document.getElementsByClassName("authorized2")[0].style.display='none';
        document.getElementsByClassName("authorized3")[0].style.display='none';
        document.getElementsByClassName("authorized4")[0].style.display='none';

    }
    else if(auth1==2)
    {
        document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized1")[0].style.display='block';

        document.getElementsByClassName("authorized2")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized2")[0].style.display='block';

        document.getElementsByClassName("authorized3")[0].style.display='none';
        document.getElementsByClassName("authorized4")[0].style.display='none';


    }
    else if(auth1==3)
    {
        document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized1")[0].style.display='block';

        document.getElementsByClassName("authorized2")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized2")[0].style.display='block';

        document.getElementsByClassName("authorized3")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized3")[0].style.display='block';

        document.getElementsByClassName("authorized4")[0].style.display='none';

    }
    else if(auth1==4)
    {
        document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized1")[0].style.display='block';

        document.getElementsByClassName("authorized2")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized2")[0].style.display='block';

        document.getElementsByClassName("authorized3")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized3")[0].style.display='block';

        document.getElementsByClassName("authorized4")[0].style.visibility='inherit';
        document.getElementsByClassName("authorized4")[0].style.display='block';

    }

    /******************************Corporate*****************************/

    var corp1=$('#numOfCorporateShareholders').val();
    // alert("corporate newcount= "+corp1);

    if(corp1==0)
    {

        //document.getElementsByClassName("corprateAll")[0].style.display='none';
        document.getElementsByClassName("corporate1")[0].style.display='none';
        document.getElementsByClassName("corporate2")[0].style.display='none';
        document.getElementsByClassName("corporate3")[0].style.display='none';
        document.getElementsByClassName("corporate4")[0].style.display='none';
    }
    else if(corp1==1)
    {
        document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate1")[0].style.display='block';

        document.getElementsByClassName("corporate2")[0].style.display='none';
        document.getElementsByClassName("corporate3")[0].style.display='none';
        document.getElementsByClassName("corporate4")[0].style.display='none';

    }
    else if(corp1==2)
    {
        document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate1")[0].style.display='block';

        document.getElementsByClassName("corporate2")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate2")[0].style.display='block';

        document.getElementsByClassName("corporate3")[0].style.display='none';
        document.getElementsByClassName("corporate4")[0].style.display='none';

    }
    else if(corp1==3)
    {
        document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate1")[0].style.display='block';

        document.getElementsByClassName("corporate2")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate2")[0].style.display='block';

        document.getElementsByClassName("corporate3")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate3")[0].style.display='block';

        document.getElementsByClassName("corporate4")[0].style.display='none';

    }
    else if(corp1==4)
    {
        document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate1")[0].style.display='block';

        document.getElementsByClassName("corporate2")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate2")[0].style.display='block';

        document.getElementsByClassName("corporate3")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate3")[0].style.display='block';

        document.getElementsByClassName("corporate4")[0].style.visibility='inherit';
        document.getElementsByClassName("corporate4")[0].style.display='block';


    }

});
/*************************************ON LOAD FUNDTION ENDS*************************************/

$(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    $('#shareholderprofile1_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#shareholderprofile2_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#shareholderprofile3_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#shareholderprofile4_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#directorsprofile_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#directorsprofile2_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#directorsprofile3_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#directorsprofile4_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#authorizedsignatoryprofile_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#authorizedsignatoryprofile2_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#authorizedsignatoryprofile3_Passportexpirydate').datepicker('setEndDate','+10y');
    $('#authorizedsignatoryprofile4_Passportexpirydate').datepicker('setEndDate','+10y');

    $('#Shareholder1').next('.iCheck-helper').click(function(){
        if($('#Shareholder1').prop("checked") == true){
            document.getElementById('shareholderprofile1').value = document.getElementById('nameprincipal1').value;
            document.getElementById('shareholderprofile1_title').value = document.getElementById('nameprincipal1_title').value;
            document.getElementById('shareholderprofile1_lastname').value = document.getElementById('nameprincipal1_lastname').value;
            document.getElementById('shareholderprofile1_owned').value = document.getElementById('nameprincipal1_owned').value;
            document.getElementById('shareholderprofile1_addressproof').value = document.getElementById('nameprincipal1_owned').value;
            document.getElementById('shareholderprofile1_addressId').value = document.getElementById('nameprincipal1_owned').value;
            document.getElementById('shareholderprofile1_address').value = document.getElementById('nameprincipal1_address').value;
            document.getElementById('shareholderprofile1_city').value = document.getElementById('nameprincipal1_city').value;
            document.getElementById('shareholderprofile1_State').value = document.getElementById('nameprincipal1_State').value;
            document.getElementById('shareholderprofile1_zip').value = document.getElementById('nameprincipal1_zip').value;
            document.getElementById('shareholderprofile1_country').value = document.getElementById('nameprincipal1_country').value;
            document.getElementById('shareholderprofile1_street').value = document.getElementById('nameprincipal1_street').value;
            document.getElementById('shareholderprofile1_telnocc1').value = document.getElementById('nameprincipal1_telnocc1').value;
            document.getElementById('shareholderprofile1_telephonenumber').value = document.getElementById('nameprincipal1_telephonenumber').value;
            document.getElementById('shareholderprofile1_emailaddress').value = document.getElementById('nameprincipal1_emailaddress').value;
            document.getElementById('shareholderprofile1_identificationtypeselect').value = document.getElementById('nameprincipal1_identificationtypeselect').value;
            document.getElementById('shareholderprofile1_identificationtype').value = document.getElementById('nameprincipal1_identificationtype').value;
            document.getElementById('shareholderprofile1_dateofbirth').value = document.getElementById('nameprincipal1_dateofbirth').value;
            document.getElementById('shareholderprofile1_nationality').value = document.getElementById('nameprincipal1_nationality').value;
            document.getElementById('shareholderprofile1_Passportexpirydate').value = document.getElementById('nameprincipal1_Passportexpirydate').value;
            document.getElementById('shareholderprofile1_passportissuedate').value = document.getElementById('nameprincipal1_passportissuedate').value;
        }
    });

    $('#Shareholder2').next('.iCheck-helper').click(function(){
        if($('#Shareholder2').prop("checked") == true){
            document.getElementById('shareholderprofile3').value = document.getElementById('nameprincipal2').value;
            document.getElementById('shareholderprofile2_title').value = document.getElementById('nameprincipal2_title').value;
            document.getElementById('shareholderprofile2_lastname').value = document.getElementById('nameprincipal2_lastname').value;
            document.getElementById('shareholderprofile2_owned').value = document.getElementById('nameprincipal2_owned').value;
            document.getElementById('shareholderprofile2_address').value = document.getElementById('nameprincipal2_address').value;
            document.getElementById('shareholderprofile2_city').value = document.getElementById('nameprincipal2_city').value;
            document.getElementById('shareholderprofile2_dateofbirth').value = document.getElementById('nameprincipal2_dateofbirth').value;
            document.getElementById('shareholderprofile2_State').value = document.getElementById('nameprincipal2_State').value;
            document.getElementById('shareholderprofile2_zip').value = document.getElementById('nameprincipal2_zip').value;
            document.getElementById('shareholderprofile2_country').value = document.getElementById('nameprincipal2_country').value;
            document.getElementById('shareholderprofile2_street').value = document.getElementById('nameprincipal2_street').value;
            document.getElementById('shareholderprofile2_telnocc2').value = document.getElementById('nameprincipal2_telnocc2').value;
            document.getElementById('shareholderprofile2_telephonenumber').value = document.getElementById('nameprincipal2_telephonenumber').value;
            document.getElementById('shareholderprofile2_emailaddress').value = document.getElementById('nameprincipal2_emailaddress').value;
            document.getElementById('shareholderprofile2_identificationtypeselect').value = document.getElementById('nameprincipal2_identificationtypeselect').value;
            document.getElementById('shareholderprofile2_identificationtype').value = document.getElementById('nameprincipal2_identificationtype').value;
            document.getElementById('shareholderprofile2_nationality').value = document.getElementById('nameprincipal2_nationality').value;
            document.getElementById('shareholderprofile2_Passportexpirydate').value = document.getElementById('nameprincipal2_Passportexpirydate').value;
            document.getElementById('shareholderprofile2_passportissuedate').value = document.getElementById('nameprincipal2_passportissuedate').value;
        }
    });

    $('#Shareholder3').next('.iCheck-helper').click(function(){
        if($('#Shareholder3').prop("checked") == true){
            document.getElementById('shareholderprofile3').value = document.getElementById('nameprincipal3').value;
            document.getElementById('shareholderprofile3_title').value = document.getElementById('nameprincipal3_title').value;
            document.getElementById('shareholderprofile3_lastname').value = document.getElementById('nameprincipal3_lastname').value;
            document.getElementById('shareholderprofile3_owned').value = document.getElementById('nameprincipal3_owned').value;
            document.getElementById('shareholderprofile3_address').value = document.getElementById('nameprincipal3_address').value;
            document.getElementById('shareholderprofile3_city').value = document.getElementById('nameprincipal3_city').value;
            document.getElementById('shareholderprofile3_dateofbirth').value = document.getElementById('nameprincipal3_dateofbirth').value;
            document.getElementById('shareholderprofile3_State').value = document.getElementById('nameprincipal3_State').value;
            document.getElementById('shareholderprofile3_zip').value = document.getElementById('nameprincipal3_zip').value;
            document.getElementById('shareholderprofile3_country').value = document.getElementById('nameprincipal3_country').value;
            document.getElementById('shareholderprofile3_street').value = document.getElementById('nameprincipal3_street').value;
            document.getElementById('shareholderprofile3_telnocc2').value = document.getElementById('nameprincipal3_telnocc1').value;
            document.getElementById('shareholderprofile3_telephonenumber').value = document.getElementById('nameprincipal3_telephonenumber').value;
            document.getElementById('shareholderprofile3_emailaddress').value = document.getElementById('nameprincipal3_emailaddress').value;
            document.getElementById('shareholderprofile3_identificationtypeselect').value = document.getElementById('nameprincipal3_identificationtypeselect').value;
            document.getElementById('shareholderprofile3_identificationtype').value = document.getElementById('nameprincipal3_identificationtype').value;
            document.getElementById('shareholderprofile3_nationality').value = document.getElementById('nameprincipal3_nationality').value;
            document.getElementById('shareholderprofile3_Passportexpirydate').value = document.getElementById('nameprincipal3_Passportexpirydate').value;
            document.getElementById('shareholderprofile3_passportissuedate').value = document.getElementById('nameprincipal3_passportissuedate').value;
        }
    });

    $('#Shareholder4').next('.iCheck-helper').click(function(){
        if($('#Shareholder4').prop("checked") == true){
            document.getElementById('shareholderprofile4').value = document.getElementById('nameprincipal4').value;
            document.getElementById('shareholderprofile4_title').value = document.getElementById('nameprincipal4_title').value;
            document.getElementById('shareholderprofile4_lastname').value = document.getElementById('nameprincipal4_lastname').value;
            document.getElementById('shareholderprofile4_owned').value = document.getElementById('nameprincipal4_owned').value;
            document.getElementById('shareholderprofile4_address').value = document.getElementById('nameprincipal4_address').value;
            document.getElementById('shareholderprofile4_city').value = document.getElementById('nameprincipal4_city').value;
            document.getElementById('shareholderprofile4_dateofbirth').value = document.getElementById('nameprincipal4_dateofbirth').value;
            document.getElementById('shareholderprofile4_State').value = document.getElementById('nameprincipal4_State').value;
            document.getElementById('shareholderprofile4_zip').value = document.getElementById('nameprincipal4_zip').value;
            document.getElementById('shareholderprofile4_country').value = document.getElementById('nameprincipal4_country').value;
            document.getElementById('shareholderprofile4_street').value = document.getElementById('nameprincipal4_street').value;
            document.getElementById('shareholderprofile4_telnocc2').value = document.getElementById('nameprincipal4_telnocc1').value;
            document.getElementById('shareholderprofile4_telephonenumber').value = document.getElementById('nameprincipal4_telephonenumber').value;
            document.getElementById('shareholderprofile4_emailaddress').value = document.getElementById('nameprincipal4_emailaddress').value;
            document.getElementById('shareholderprofile4_identificationtypeselect').value = document.getElementById('nameprincipal4_identificationtypeselect').value;
            document.getElementById('shareholderprofile4_identificationtype').value = document.getElementById('nameprincipal4_identificationtype').value;
            document.getElementById('shareholderprofile4_nationality').value = document.getElementById('nameprincipal4_nationality').value;
            document.getElementById('shareholderprofile4_Passportexpirydate').value = document.getElementById('nameprincipal4_Passportexpirydate').value;
            document.getElementById('shareholderprofile4_passportissuedate').value = document.getElementById('nameprincipal4_passportissuedate').value;
        }
    });


    $('#Director1').next('.iCheck-helper').click(function(){
        if($('#Director1').prop("checked") == true){
            document.getElementById('directorsprofile').value = document.getElementById('nameprincipal1').value;
            document.getElementById('directorsprofile_title').value = document.getElementById('nameprincipal1_title').value;
            document.getElementById('directorsprofile_lastname').value = document.getElementById('nameprincipal1_lastname').value;
            document.getElementById('directorsprofile_address').value = document.getElementById('nameprincipal1_address').value;
            document.getElementById('directorsprofile_city').value = document.getElementById('nameprincipal1_city').value;
            document.getElementById('directorsprofile_State').value = document.getElementById('nameprincipal1_State').value;
            document.getElementById('directorsprofile_zip').value = document.getElementById('nameprincipal1_zip').value;
            document.getElementById('directorsprofile_country').value = document.getElementById('nameprincipal1_country').value;
            document.getElementById('directorsprofile_street').value = document.getElementById('nameprincipal1_street').value;
            document.getElementById('directorsprofile_telnocc1').value = document.getElementById('nameprincipal1_telnocc1').value;
            document.getElementById('directorsprofile_telephonenumber').value = document.getElementById('nameprincipal1_telephonenumber').value;
            document.getElementById('directorsprofile_emailaddress').value = document.getElementById('nameprincipal1_emailaddress').value;
            document.getElementById('directorsprofile_identificationtypeselect').value = document.getElementById('nameprincipal1_identificationtypeselect').value;
            document.getElementById('directorsprofile_identificationtype').value = document.getElementById('nameprincipal1_identificationtype').value;
            document.getElementById('directorsprofile_dateofbirth').value = document.getElementById('nameprincipal1_dateofbirth').value;
            document.getElementById('directorsprofile_nationality').value = document.getElementById('nameprincipal1_nationality').value;
            document.getElementById('directorsprofile_Passportexpirydate').value = document.getElementById('nameprincipal1_Passportexpirydate').value;
            document.getElementById('directorsprofile_passportissuedate').value = document.getElementById('nameprincipal1_passportissuedate').value;
        }
    });

    $('#Director2').next('.iCheck-helper').click(function(){
        if($('#Director2').prop("checked") == true){
            document.getElementById('directorsprofile2').value = document.getElementById('nameprincipal2').value;
            document.getElementById('directorsprofile2_title').value = document.getElementById('nameprincipal2_title').value;
            document.getElementById('directorsprofile2_lastname').value = document.getElementById('nameprincipal2_lastname').value;
            document.getElementById('directorsprofile2_address').value = document.getElementById('nameprincipal2_address').value;
            document.getElementById('directorsprofile2_city').value = document.getElementById('nameprincipal2_city').value;
            document.getElementById('directorsprofile2_dateofbirth').value = document.getElementById('nameprincipal2_dateofbirth').value;
            document.getElementById('directorsprofile2_State').value = document.getElementById('nameprincipal2_State').value;
            document.getElementById('directorsprofile2_zip').value = document.getElementById('nameprincipal2_zip').value;
            document.getElementById('directorsprofile2_country').value = document.getElementById('nameprincipal2_country').value;
            document.getElementById('directorsprofile2_street').value = document.getElementById('nameprincipal2_street').value;
            document.getElementById('directorsprofile2_telnocc1').value = document.getElementById('nameprincipal2_telnocc2').value;
            document.getElementById('directorsprofile2_telephonenumber').value = document.getElementById('nameprincipal2_telephonenumber').value;
            document.getElementById('directorsprofile2_emailaddress').value = document.getElementById('nameprincipal2_emailaddress').value;
            document.getElementById('directorsprofile2_identificationtypeselect').value = document.getElementById('nameprincipal2_identificationtypeselect').value;
            document.getElementById('directorsprofile2_identificationtype').value = document.getElementById('nameprincipal2_identificationtype').value;
            document.getElementById('directorsprofile2_nationality').value = document.getElementById('nameprincipal2_nationality').value;
            document.getElementById('directorsprofile2_Passportexpirydate').value = document.getElementById('nameprincipal2_Passportexpirydate').value;
            document.getElementById('directorsprofile2_passportissuedate').value = document.getElementById('nameprincipal2_passportissuedate').value;
        }
    });

    $('#Director3').next('.iCheck-helper').click(function(){
        if($('#Director3').prop("checked") == true){
            document.getElementById('directorsprofile3').value = document.getElementById('nameprincipal3').value;
            document.getElementById('directorsprofile3_title').value = document.getElementById('nameprincipal3_title').value;
            document.getElementById('directorsprofile3_lastname').value = document.getElementById('nameprincipal3_lastname').value;
            document.getElementById('directorsprofile3_address').value = document.getElementById('nameprincipal3_address').value;
            document.getElementById('directorsprofile3_city').value = document.getElementById('nameprincipal3_city').value;
            document.getElementById('directorsprofile3_dateofbirth').value = document.getElementById('nameprincipal3_dateofbirth').value;
            document.getElementById('directorsprofile3_State').value = document.getElementById('nameprincipal3_State').value;
            document.getElementById('directorsprofile3_zip').value = document.getElementById('nameprincipal3_zip').value;
            document.getElementById('directorsprofile3_country').value = document.getElementById('nameprincipal3_country').value;
            document.getElementById('directorsprofile3_street').value = document.getElementById('nameprincipal3_street').value;
            document.getElementById('directorsprofile3_telnocc1').value = document.getElementById('nameprincipal3_telnocc1').value;
            document.getElementById('directorsprofile3_telephonenumber').value = document.getElementById('nameprincipal3_telephonenumber').value;
            document.getElementById('directorsprofile3_emailaddress').value = document.getElementById('nameprincipal3_emailaddress').value;
            document.getElementById('directorsprofile3_identificationtypeselect').value = document.getElementById('nameprincipal3_identificationtypeselect').value;
            document.getElementById('directorsprofile3_identificationtype').value = document.getElementById('nameprincipal3_identificationtype').value;
            document.getElementById('directorsprofile3_nationality').value = document.getElementById('nameprincipal3_nationality').value;
            document.getElementById('directorsprofile3_Passportexpirydate').value = document.getElementById('nameprincipal3_Passportexpirydate').value;
            document.getElementById('directorsprofile3_passportissuedate').value = document.getElementById('nameprincipal3_passportissuedate').value;
        }
    });

    $('#Director4').next('.iCheck-helper').click(function(){
        if($('#Director4').prop("checked") == true){
            document.getElementById('directorsprofile4').value = document.getElementById('nameprincipal4').value;
            document.getElementById('directorsprofile4_title').value = document.getElementById('nameprincipal4_title').value;
            document.getElementById('directorsprofile4_lastname').value = document.getElementById('nameprincipal4_lastname').value;
            document.getElementById('directorsprofile4_address').value = document.getElementById('nameprincipal4_address').value;
            document.getElementById('directorsprofile4_city').value = document.getElementById('nameprincipal4_city').value;
            document.getElementById('directorsprofile4_dateofbirth').value = document.getElementById('nameprincipal4_dateofbirth').value;
            document.getElementById('directorsprofile4_State').value = document.getElementById('nameprincipal4_State').value;
            document.getElementById('directorsprofile4_zip').value = document.getElementById('nameprincipal4_zip').value;
            document.getElementById('directorsprofile4_country').value = document.getElementById('nameprincipal4_country').value;
            document.getElementById('directorsprofile4_street').value = document.getElementById('nameprincipal4_street').value;
            document.getElementById('directorsprofile4_telnocc1').value = document.getElementById('nameprincipal4_telnocc1').value;
            document.getElementById('directorsprofile4_telephonenumber').value = document.getElementById('nameprincipal4_telephonenumber').value;
            document.getElementById('directorsprofile4_emailaddress').value = document.getElementById('nameprincipal4_emailaddress').value;
            document.getElementById('directorsprofile4_identificationtypeselect').value = document.getElementById('nameprincipal4_identificationtypeselect').value;
            document.getElementById('directorsprofile4_identificationtype').value = document.getElementById('nameprincipal4_identificationtype').value;
            document.getElementById('directorsprofile4_nationality').value = document.getElementById('nameprincipal4_nationality').value;
            document.getElementById('directorsprofile4_Passportexpirydate').value = document.getElementById('nameprincipal4_Passportexpirydate').value;
            document.getElementById('directorsprofile4_passportissuedate').value = document.getElementById('nameprincipal4_passportissuedate').value;
        }
    });

    $('#Authorize1').next('.iCheck-helper').click(function(){
        if($('#Authorize1').prop("checked") == true){
            document.getElementById('authorizedsignatoryprofile').value = document.getElementById('nameprincipal1').value;
            document.getElementById('authorizedsignatoryprofile_title').value = document.getElementById('nameprincipal1_title').value;
            document.getElementById('authorizedsignatoryprofile_lastname').value = document.getElementById('nameprincipal1_lastname').value;
            document.getElementById('authorizedsignatoryprofile_address').value = document.getElementById('nameprincipal1_address').value;
            document.getElementById('authorizedsignatoryprofile_city').value = document.getElementById('nameprincipal1_city').value;
            document.getElementById('authorizedsignatoryprofile_State').value = document.getElementById('nameprincipal1_State').value;
            document.getElementById('authorizedsignatoryprofile_zip').value = document.getElementById('nameprincipal1_zip').value;
            document.getElementById('authorizedsignatoryprofile_country').value = document.getElementById('nameprincipal1_country').value;
            document.getElementById('authorizedsignatoryprofile_street').value = document.getElementById('nameprincipal1_street').value;
            document.getElementById('authorizedsignatoryprofile_telnocc1').value = document.getElementById('nameprincipal1_telnocc1').value;
            document.getElementById('authorizedsignatoryprofile_telephonenumber').value = document.getElementById('nameprincipal1_telephonenumber').value;
            document.getElementById('authorizedsignatoryprofile_emailaddress').value = document.getElementById('nameprincipal1_emailaddress').value;
            document.getElementById('authorizedsignatoryprofile_identificationtypeselect').value = document.getElementById('nameprincipal1_identificationtypeselect').value;
            document.getElementById('authorizedsignatoryprofile_identificationtype').value = document.getElementById('nameprincipal1_identificationtype').value;
            document.getElementById('authorizedsignatoryprofile_dateofbirth').value = document.getElementById('nameprincipal1_dateofbirth').value;
            document.getElementById('authorizedsignatoryprofile_nationality').value = document.getElementById('nameprincipal1_nationality').value;
            document.getElementById('authorizedsignatoryprofile_Passportexpirydate').value = document.getElementById('nameprincipal1_Passportexpirydate').value;
            document.getElementById('authorizedsignatoryprofile_passportissuedate').value = document.getElementById('nameprincipal1_passportissuedate').value;
            document.getElementById('authorizedsignatoryprofile1_designation').value = document.getElementById('nameprincipal1_passportissuedate').value;
            document.getElementById('authorizedsignatoryprofile1_owned').value = document.getElementById('nameprincipal1_passportissuedate').value;
            document.getElementById('authorizedsignatoryprofile1_addressproof').value = document.getElementById('nameprincipal1_passportissuedate').value;
            document.getElementById('authorizedsignatoryprofile1_addressId').value = document.getElementById('nameprincipal1_passportissuedate').value;

        }
    });

    $('#Authorize2').next('.iCheck-helper').click(function(){
        if($('#Authorize2').prop("checked") == true){
            document.getElementById('authorizedsignatoryprofile2').value = document.getElementById('nameprincipal2').value;
            document.getElementById('authorizedsignatoryprofile2_title').value = document.getElementById('nameprincipal2_title').value;
            document.getElementById('authorizedsignatoryprofile2_lastname').value = document.getElementById('nameprincipal2_lastname').value;
            document.getElementById('authorizedsignatoryprofile2_address').value = document.getElementById('nameprincipal2_address').value;
            document.getElementById('authorizedsignatoryprofile2_city').value = document.getElementById('nameprincipal2_city').value;
            document.getElementById('authorizedsignatoryprofile2_dateofbirth').value = document.getElementById('nameprincipal2_dateofbirth').value;
            document.getElementById('authorizedsignatoryprofile2_State').value = document.getElementById('nameprincipal2_State').value;
            document.getElementById('authorizedsignatoryprofile2_zip').value = document.getElementById('nameprincipal2_zip').value;
            document.getElementById('authorizedsignatoryprofile2_country').value = document.getElementById('nameprincipal2_country').value;
            document.getElementById('authorizedsignatoryprofile2_street').value = document.getElementById('nameprincipal2_street').value;
            document.getElementById('authorizedsignatoryprofile2_telnocc1').value = document.getElementById('nameprincipal2_telnocc2').value;
            document.getElementById('authorizedsignatoryprofile2_telephonenumber').value = document.getElementById('nameprincipal2_telephonenumber').value;
            document.getElementById('authorizedsignatoryprofile2_emailaddress').value = document.getElementById('nameprincipal2_emailaddress').value;
            document.getElementById('authorizedsignatoryprofile2_identificationtypeselect').value = document.getElementById('nameprincipal2_identificationtypeselect').value;
            document.getElementById('authorizedsignatoryprofile2_identificationtype').value = document.getElementById('nameprincipal2_identificationtype').value;
            document.getElementById('authorizedsignatoryprofile2_nationality').value = document.getElementById('nameprincipal2_nationality').value;
            document.getElementById('authorizedsignatoryprofile2_Passportexpirydate').value = document.getElementById('nameprincipal2_Passportexpirydate').value;
            document.getElementById('authorizedsignatoryprofile2_passportissuedate').value = document.getElementById('nameprincipal2_passportissuedate').value;
        }
    });

    $('#Authorize3').next('.iCheck-helper').click(function(){
        if($('#Authorize3').prop("checked") == true){
            document.getElementById('authorizedsignatoryprofile3').value = document.getElementById('nameprincipal3').value;
            document.getElementById('authorizedsignatoryprofile3_title').value = document.getElementById('nameprincipal3_title').value;
            document.getElementById('authorizedsignatoryprofile3_lastname').value = document.getElementById('nameprincipal3_lastname').value;
            document.getElementById('authorizedsignatoryprofile3_address').value = document.getElementById('nameprincipal3_address').value;
            document.getElementById('authorizedsignatoryprofile3_city').value = document.getElementById('nameprincipal3_city').value;
            document.getElementById('authorizedsignatoryprofile3_dateofbirth').value = document.getElementById('nameprincipal3_dateofbirth').value;
            document.getElementById('authorizedsignatoryprofile3_State').value = document.getElementById('nameprincipal3_State').value;
            document.getElementById('authorizedsignatoryprofile3_zip').value = document.getElementById('nameprincipal3_zip').value;
            document.getElementById('authorizedsignatoryprofile3_country').value = document.getElementById('nameprincipal3_country').value;
            document.getElementById('authorizedsignatoryprofile3_street').value = document.getElementById('nameprincipal3_street').value;
            document.getElementById('authorizedsignatoryprofile3_telnocc1').value = document.getElementById('nameprincipal3_telnocc1').value;
            document.getElementById('authorizedsignatoryprofile3_telephonenumber').value = document.getElementById('nameprincipal3_telephonenumber').value;
            document.getElementById('authorizedsignatoryprofile3_emailaddress').value = document.getElementById('nameprincipal3_emailaddress').value;
            document.getElementById('authorizedsignatoryprofile3_identificationtypeselect').value = document.getElementById('nameprincipal3_identificationtypeselect').value;
            document.getElementById('authorizedsignatoryprofile3_identificationtype').value = document.getElementById('nameprincipal3_identificationtype').value;
            document.getElementById('authorizedsignatoryprofile3_nationality').value = document.getElementById('nameprincipal3_nationality').value;
            document.getElementById('authorizedsignatoryprofile3_Passportexpirydate').value = document.getElementById('nameprincipal3_Passportexpirydate').value;
            document.getElementById('authorizedsignatoryprofile3_passportissuedate').value = document.getElementById('nameprincipal3_passportissuedate').value;
        }
    });

    $('#Authorize4').next('.iCheck-helper').click(function(){
        if($('#Authorize3').prop("checked") == true){
            document.getElementById('authorizedsignatoryprofile4').value = document.getElementById('nameprincipal4').value;
            document.getElementById('authorizedsignatoryprofile4_title').value = document.getElementById('nameprincipal4_title').value;
            document.getElementById('authorizedsignatoryprofile4_lastname').value = document.getElementById('nameprincipal4_lastname').value;
            document.getElementById('authorizedsignatoryprofile4_address').value = document.getElementById('nameprincipal34_address').value;
            document.getElementById('authorizedsignatoryprofile4_city').value = document.getElementById('nameprincipal4_city').value;
            document.getElementById('authorizedsignatoryprofile4_dateofbirth').value = document.getElementById('nameprincipal4_dateofbirth').value;
            document.getElementById('authorizedsignatoryprofile4_State').value = document.getElementById('nameprincipal4_State').value;
            document.getElementById('authorizedsignatoryprofile4_zip').value = document.getElementById('nameprincipal4_zip').value;
            document.getElementById('authorizedsignatoryprofile4_country').value = document.getElementById('nameprincipal4_country').value;
            document.getElementById('authorizedsignatoryprofile4_street').value = document.getElementById('nameprincipal4_street').value;
            document.getElementById('authorizedsignatoryprofile4_telnocc1').value = document.getElementById('nameprincipal4_telnocc1').value;
            document.getElementById('authorizedsignatoryprofile4_telephonenumber').value = document.getElementById('nameprincipal4_telephonenumber').value;
            document.getElementById('authorizedsignatoryprofile4_emailaddress').value = document.getElementById('nameprincipal4_emailaddress').value;
            document.getElementById('authorizedsignatoryprofile4_identificationtypeselect').value = document.getElementById('nameprincipal4_identificationtypeselect').value;
            document.getElementById('authorizedsignatoryprofile4_identificationtype').value = document.getElementById('nameprincipal4_identificationtype').value;
            document.getElementById('authorizedsignatoryprofile4_nationality').value = document.getElementById('nameprincipal4_nationality').value;
            document.getElementById('authorizedsignatoryprofile4_Passportexpirydate').value = document.getElementById('nameprincipal4_Passportexpirydate').value;
            document.getElementById('authorizedsignatoryprofile4_passportissuedate').value = document.getElementById('nameprincipal4_passportissuedate').value;
        }
    });
});



//////////

$(document).ready(function(){

    $('#numOfShareholders').change(function(e)
    {

        var share1=$('#numOfShareholders').val();
        if(share1==0)
        {
            /*document.getElementById('shareholder').value="";*/
        }
        else if(share1==1)
        {
            document.getElementsByClassName("share1")[0].style.visibility='inherit';
            document.getElementsByClassName("share1")[0].style.display='block';

            document.getElementsByClassName("share2")[0].style.display='none';
            document.getElementsByClassName("share3")[0].style.display='none';
            document.getElementsByClassName("share4")[0].style.display='none';
            //alert("11111");
            document.getElementById('shareholderprofile2_title').value = "";
            document.getElementById('shareholderprofile2').value = "";
            document.getElementById('shareholderprofile2_lastname').value = "";
            document.getElementById('shareholderprofile2_dateofbirth').value = "";
            document.getElementById('shareholderprofile2_telnocc2').value = "";
            document.getElementById('shareholderprofile2_telephonenumber').value = "";
            document.getElementById('shareholderprofile2_emailaddress').value = "";
            document.getElementById('shareholderprofile2_owned').value = "";
            document.getElementById('shareholderprofile2_addressproof').value = "";
            document.getElementById('shareholderprofile2_addressId').value = "";
            document.getElementById('shareholderprofile2_address').value ="";
            document.getElementById('shareholderprofile2_street').value = "";
            document.getElementById('shareholderprofile2_city').value = "";
            document.getElementById('shareholderprofile2_State').value = "";
            document.getElementById('shareholderprofile2_country').value = "";
            document.getElementById('shareholderprofile2_zip').value = "";
            document.getElementById('shareholderprofile2_identificationtypeselect').value = "";
            document.getElementById('shareholderprofile2_identificationtype').value = "";
            document.getElementById('shareholderprofile2_nationality').value = "";
            document.getElementById('shareholderprofile2_Passportexpirydate').value = "";
            document.getElementById('shareholderprofile2_passportissuedate').value = "";

            document.getElementById('shareholderprofile3_title').value = "";
            document.getElementById('shareholderprofile3').value = "";
            document.getElementById('shareholderprofile3_lastname').value = "";
            document.getElementById('shareholderprofile3_dateofbirth').value = "";
            document.getElementById('shareholderprofile3_telnocc2').value = "";
            document.getElementById('shareholderprofile3_telephonenumber').value = "";
            document.getElementById('shareholderprofile3_emailaddress').value = "";
            document.getElementById('shareholderprofile3_owned').value = "";
            document.getElementById('shareholderprofile3_addressproof').value = "";
            document.getElementById('shareholderprofile3_addressId').value = "";
            document.getElementById('shareholderprofile3_address').value ="";
            document.getElementById('shareholderprofile3_street').value = "";
            document.getElementById('shareholderprofile3_city').value = "";
            document.getElementById('shareholderprofile3_State').value = "";
            document.getElementById('shareholderprofile3_country').value = "";
            document.getElementById('shareholderprofile3_zip').value = "";
            document.getElementById('shareholderprofile3_identificationtypeselect').value = "";
            document.getElementById('shareholderprofile3_identificationtype').value = "";
            document.getElementById('shareholderprofile3_nationality').value = "";
            document.getElementById('shareholderprofile3_Passportexpirydate').value = "";
            document.getElementById('shareholderprofile3_passportissuedate').value = "";



            document.getElementById('shareholderprofile4_title').value = "";
            document.getElementById('shareholderprofile4').value = "";
            document.getElementById('shareholderprofile4_lastname').value = "";
            document.getElementById('shareholderprofile4_dateofbirth').value = "";
            document.getElementById('shareholderprofile4_telnocc2').value = "";
            document.getElementById('shareholderprofile4_telephonenumber').value = "";
            document.getElementById('shareholderprofile4_emailaddress').value = "";
            document.getElementById('shareholderprofile4_owned').value = "";
            document.getElementById('shareholderprofile4_addressproof').value = "";
            document.getElementById('shareholderprofile4_addressId').value = "";
            document.getElementById('shareholderprofile4_address').value ="";
            document.getElementById('shareholderprofile4_street').value = "";
            document.getElementById('shareholderprofile4_city').value = "";
            document.getElementById('shareholderprofile4_State').value = "";
            document.getElementById('shareholderprofile4_country').value = "";
            document.getElementById('shareholderprofile4_zip').value = "";
            document.getElementById('shareholderprofile4_identificationtypeselect').value = "";
            document.getElementById('shareholderprofile4_identificationtype').value = "";
            document.getElementById('shareholderprofile4_nationality').value = "";
            document.getElementById('shareholderprofile4_Passportexpirydate').value = "";
            document.getElementById('shareholderprofile4_passportissuedate').value = "";
        }
        else if(share1==2)
        {
            //alert("22222");
            document.getElementsByClassName("share1")[0].style.visibility='inherit';
            document.getElementsByClassName("share1")[0].style.display='block';

            document.getElementsByClassName("share2")[0].style.visibility='inherit';
            document.getElementsByClassName("share2")[0].style.display='block';

            document.getElementsByClassName("share3")[0].style.display='none';
            document.getElementsByClassName("share4")[0].style.display='none';

            document.getElementById('shareholderprofile3_title').value = "";
            document.getElementById('shareholderprofile3').value = "";
            document.getElementById('shareholderprofile3_lastname').value = "";
            document.getElementById('shareholderprofile3_dateofbirth').value = "";
            document.getElementById('shareholderprofile3_telnocc2').value = "";
            document.getElementById('shareholderprofile3_telephonenumber').value = "";
            document.getElementById('shareholderprofile3_emailaddress').value = "";
            document.getElementById('shareholderprofile3_owned').value = "";
            document.getElementById('shareholderprofile3_addressproof').value = "";
            document.getElementById('shareholderprofile3_addressId').value = "";
            document.getElementById('shareholderprofile3_address').value ="";
            document.getElementById('shareholderprofile3_street').value = "";
            document.getElementById('shareholderprofile3_city').value = "";
            document.getElementById('shareholderprofile3_State').value = "";
            document.getElementById('shareholderprofile3_country').value = "";
            document.getElementById('shareholderprofile3_zip').value = "";
            document.getElementById('shareholderprofile3_identificationtypeselect').value = "";
            document.getElementById('shareholderprofile3_identificationtype').value = "";
            document.getElementById('shareholderprofile3_nationality').value = "";
            document.getElementById('shareholderprofile3_Passportexpirydate').value = "";
            document.getElementById('shareholderprofile3_passportissuedate').value = "";

            document.getElementById('shareholderprofile4_title').value = "";
            document.getElementById('shareholderprofile4').value = "";
            document.getElementById('shareholderprofile4_lastname').value = "";
            document.getElementById('shareholderprofile4_dateofbirth').value = "";
            document.getElementById('shareholderprofile4_telnocc2').value = "";
            document.getElementById('shareholderprofile4_telephonenumber').value = "";
            document.getElementById('shareholderprofile4_emailaddress').value = "";
            document.getElementById('shareholderprofile4_owned').value = "";
            document.getElementById('shareholderprofile4_addressproof').value = "";
            document.getElementById('shareholderprofile4_addressId').value = "";
            document.getElementById('shareholderprofile4_address').value ="";
            document.getElementById('shareholderprofile4_street').value = "";
            document.getElementById('shareholderprofile4_city').value = "";
            document.getElementById('shareholderprofile4_State').value = "";
            document.getElementById('shareholderprofile4_country').value = "";
            document.getElementById('shareholderprofile4_zip').value = "";
            document.getElementById('shareholderprofile4_identificationtypeselect').value = "";
            document.getElementById('shareholderprofile4_identificationtype').value = "";
            document.getElementById('shareholderprofile4_nationality').value = "";
            document.getElementById('shareholderprofile4_Passportexpirydate').value = "";
            document.getElementById('shareholderprofile4_passportissuedate').value = "";

        }
        else if(share1==3)
        {
            //alert("3333");
            document.getElementsByClassName("share1")[0].style.visibility='inherit';
            document.getElementsByClassName("share1")[0].style.display='block';

            document.getElementsByClassName("share2")[0].style.visibility='inherit';
            document.getElementsByClassName("share2")[0].style.display='block';

            document.getElementsByClassName("share3")[0].style.visibility='inherit';
            document.getElementsByClassName("share3")[0].style.display='block';

            document.getElementsByClassName("share4")[0].style.display='none';

            document.getElementById('shareholderprofile4_title').value = "";
            document.getElementById('shareholderprofile4').value = "";
            document.getElementById('shareholderprofile4_lastname').value = "";
            document.getElementById('shareholderprofile4_dateofbirth').value = "";
            document.getElementById('shareholderprofile4_telnocc2').value = "";
            document.getElementById('shareholderprofile4_telephonenumber').value = "";
            document.getElementById('shareholderprofile4_emailaddress').value = "";
            document.getElementById('shareholderprofile4_owned').value = "";
            document.getElementById('shareholderprofile4_addressproof').value = "";
            document.getElementById('shareholderprofile4_addressId').value = "";
            document.getElementById('shareholderprofile4_address').value ="";
            document.getElementById('shareholderprofile4_street').value = "";
            document.getElementById('shareholderprofile4_city').value = "";
            document.getElementById('shareholderprofile4_State').value = "";
            document.getElementById('shareholderprofile4_country').value = "";
            document.getElementById('shareholderprofile4_zip').value = "";
            document.getElementById('shareholderprofile4_identificationtypeselect').value = "";
            document.getElementById('shareholderprofile4_identificationtype').value = "";
            document.getElementById('shareholderprofile4_nationality').value = "";
            document.getElementById('shareholderprofile4_Passportexpirydate').value = "";
            document.getElementById('shareholderprofile4_passportissuedate').value = "";
        }

        else if(share1==4)
        {
            //alert("3333");
            document.getElementsByClassName("share1")[0].style.visibility = 'inherit';
            document.getElementsByClassName("share1")[0].style.display = 'block';

            document.getElementsByClassName("share2")[0].style.visibility = 'inherit';
            document.getElementsByClassName("share2")[0].style.display = 'block';

            document.getElementsByClassName("share3")[0].style.visibility = 'inherit';
            document.getElementsByClassName("share3")[0].style.display = 'block';

            document.getElementsByClassName("share4")[0].style.visibility = 'inherit';
            document.getElementsByClassName("share4")[0].style.display = 'block';
        }

    });

    $('#numOfDirectors').change(function(e)
    {
        var dir1=$('#numOfDirectors').val();
        ///alert("director newcount= "+dir1);


        if(dir1==0)
        {

        }
        else if(dir1==1)
        {
            document.getElementsByClassName("dir1")[0].style.visibility='inherit';
            document.getElementsByClassName("dir1")[0].style.display='block';

            document.getElementsByClassName("dir2")[0].style.display='none';
            document.getElementsByClassName("dir4")[0].style.display='none';

            document.getElementById('directorsprofile2').value = "";
            document.getElementById('directorsprofile2_title').value = "";
            document.getElementById('directorsprofile2_lastname').value = "";
            document.getElementById('directorsprofile2_address').value = "";
            document.getElementById('directorsprofile2_city').value = "";
            document.getElementById('directorsprofile2_State').value = "";
            document.getElementById('directorsprofile2_zip').value = "";
            document.getElementById('directorsprofile2_country').value = "";
            document.getElementById('directorsprofile2_street').value = "";
            document.getElementById('directorsprofile2_telnocc1').value = "";
            document.getElementById('directorsprofile2_telephonenumber').value = "";
            document.getElementById('directorsprofile2_emailaddress').value = "";
            document.getElementById('directorsprofile2_identificationtypeselect').value = "";
            document.getElementById('directorsprofile2_identificationtype').value = "";
            document.getElementById('directorsprofile2_dateofbirth').value = "";
            document.getElementById('directorsprofile2_nationality').value = "";
            document.getElementById('directorsprofile2_Passportexpirydate').value = "";
            document.getElementById('directorsprofile2_passportissuedate').value = "";
            document.getElementById('directorsprofile2_addressproof').value = "";
            document.getElementById('directorsprofile2_addressId').value = "";
            document.getElementById('directorsprofile2_owned').value = "";

            document.getElementById('directorsprofile3').value = "";
            document.getElementById('directorsprofile3_title').value = "";
            document.getElementById('directorsprofile3_lastname').value = "";
            document.getElementById('directorsprofile3_address').value = "";
            document.getElementById('directorsprofile3_city').value = "";
            document.getElementById('directorsprofile3_State').value = "";
            document.getElementById('directorsprofile3_zip').value = "";
            document.getElementById('directorsprofile3_country').value = "";
            document.getElementById('directorsprofile3_street').value = "";
            document.getElementById('directorsprofile3_telnocc1').value = "";
            document.getElementById('directorsprofile3_telephonenumber').value = "";
            document.getElementById('directorsprofile3_emailaddress').value = "";
            document.getElementById('directorsprofile3_identificationtypeselect').value = "";
            document.getElementById('directorsprofile3_identificationtype').value = "";
            document.getElementById('directorsprofile3_dateofbirth').value = "";
            document.getElementById('directorsprofile3_nationality').value = "";
            document.getElementById('directorsprofile3_Passportexpirydate').value = "";
            document.getElementById('directorsprofile3_passportissuedate').value = "";
            document.getElementById('directorsprofile3_addressproof').value = "";
            document.getElementById('directorsprofile3_addressId').value = "";
            document.getElementById('directorsprofile3_owned').value = "";


            document.getElementById('directorsprofile4').value = "";
            document.getElementById('directorsprofile4_title').value = "";
            document.getElementById('directorsprofile4_lastname').value = "";
            document.getElementById('directorsprofile4_address').value = "";
            document.getElementById('directorsprofile4_city').value = "";
            document.getElementById('directorsprofile4_State').value = "";
            document.getElementById('directorsprofile4_zip').value = "";
            document.getElementById('directorsprofile4_country').value = "";
            document.getElementById('directorsprofile4_street').value = "";
            document.getElementById('directorsprofile4_telnocc1').value = "";
            document.getElementById('directorsprofile4_telephonenumber').value = "";
            document.getElementById('directorsprofile4_emailaddress').value = "";
            document.getElementById('directorsprofile4_identificationtypeselect').value = "";
            document.getElementById('directorsprofile4_identificationtype').value = "";
            document.getElementById('directorsprofile4_dateofbirth').value = "";
            document.getElementById('directorsprofile4_nationality').value = "";
            document.getElementById('directorsprofile4_Passportexpirydate').value = "";
            document.getElementById('directorsprofile4_passportissuedate').value = "";
            document.getElementById('directorsprofile4_addressproof').value = "";
            document.getElementById('directorsprofile4_addressId').value = "";
            document.getElementById('directorsprofile4_owned').value = "";

        }
        else if(dir1==2)
        {
            document.getElementsByClassName("dir1")[0].style.visibility='inherit';
            document.getElementsByClassName("dir1")[0].style.display='block';

            document.getElementsByClassName("dir2")[0].style.visibility='inherit';
            document.getElementsByClassName("dir2")[0].style.display='block';

            document.getElementsByClassName("dir3")[0].style.display='none';
            document.getElementsByClassName("dir4")[0].style.display='none';

            document.getElementById('directorsprofile3').value = "";
            document.getElementById('directorsprofile3_title').value = "";
            document.getElementById('directorsprofile3_lastname').value = "";
            document.getElementById('directorsprofile3_address').value = "";
            document.getElementById('directorsprofile3_city').value = "";
            document.getElementById('directorsprofile3_State').value = "";
            document.getElementById('directorsprofile3_zip').value = "";
            document.getElementById('directorsprofile3_country').value = "";
            document.getElementById('directorsprofile3_street').value = "";
            document.getElementById('directorsprofile3_telnocc1').value = "";
            document.getElementById('directorsprofile3_telephonenumber').value = "";
            document.getElementById('directorsprofile3_emailaddress').value = "";
            document.getElementById('directorsprofile3_identificationtypeselect').value = "";
            document.getElementById('directorsprofile3_identificationtype').value = "";
            document.getElementById('directorsprofile3_dateofbirth').value = "";
            document.getElementById('directorsprofile3_nationality').value = "";
            document.getElementById('directorsprofile3_Passportexpirydate').value = "";
            document.getElementById('directorsprofile3_passportissuedate').value = "";
            document.getElementById('directorsprofile3_addressproof').value = "";
            document.getElementById('directorsprofile3_addressId').value = "";
            document.getElementById('directorsprofile3_owned').value = "";

            document.getElementById('directorsprofile4').value = "";
            document.getElementById('directorsprofile4_title').value = "";
            document.getElementById('directorsprofile4_lastname').value = "";
            document.getElementById('directorsprofile4_address').value = "";
            document.getElementById('directorsprofile4_city').value = "";
            document.getElementById('directorsprofile4_State').value = "";
            document.getElementById('directorsprofile4_zip').value = "";
            document.getElementById('directorsprofile4_country').value = "";
            document.getElementById('directorsprofile4_street').value = "";
            document.getElementById('directorsprofile4_telnocc1').value = "";
            document.getElementById('directorsprofile4_telephonenumber').value = "";
            document.getElementById('directorsprofile4_emailaddress').value = "";
            document.getElementById('directorsprofile4_identificationtypeselect').value = "";
            document.getElementById('directorsprofile4_identificationtype').value = "";
            document.getElementById('directorsprofile4_dateofbirth').value = "";
            document.getElementById('directorsprofile4_nationality').value = "";
            document.getElementById('directorsprofile4_Passportexpirydate').value = "";
            document.getElementById('directorsprofile4_passportissuedate').value = "";
            document.getElementById('directorsprofile4_addressproof').value = "";
            document.getElementById('directorsprofile4_addressId').value = "";
            document.getElementById('directorsprofile4_owned').value = "";
        }
        else if(dir1==3)
        {
            document.getElementsByClassName("dir1")[0].style.visibility='inherit';
            document.getElementsByClassName("dir1")[0].style.display='block';

            document.getElementsByClassName("dir2")[0].style.visibility='inherit';
            document.getElementsByClassName("dir2")[0].style.display='block';

            document.getElementsByClassName("dir3")[0].style.visibility='inherit';
            document.getElementsByClassName("dir3")[0].style.display='block';

            document.getElementsByClassName("dir4")[0].style.display='none';

            document.getElementById('directorsprofile4').value = "";
            document.getElementById('directorsprofile4_title').value = "";
            document.getElementById('directorsprofile4_lastname').value = "";
            document.getElementById('directorsprofile4_address').value = "";
            document.getElementById('directorsprofile4_city').value = "";
            document.getElementById('directorsprofile4_State').value = "";
            document.getElementById('directorsprofile4_zip').value = "";
            document.getElementById('directorsprofile4_country').value = "";
            document.getElementById('directorsprofile4_street').value = "";
            document.getElementById('directorsprofile4_telnocc1').value = "";
            document.getElementById('directorsprofile4_telephonenumber').value = "";
            document.getElementById('directorsprofile4_emailaddress').value = "";
            document.getElementById('directorsprofile4_identificationtypeselect').value = "";
            document.getElementById('directorsprofile4_identificationtype').value = "";
            document.getElementById('directorsprofile4_dateofbirth').value = "";
            document.getElementById('directorsprofile4_nationality').value = "";
            document.getElementById('directorsprofile4_Passportexpirydate').value = "";
            document.getElementById('directorsprofile4_passportissuedate').value = "";
            document.getElementById('directorsprofile4_addressproof').value = "";
            document.getElementById('directorsprofile4_addressId').value = "";
            document.getElementById('directorsprofile4_owned').value = "";
        }
        else if(dir1==4)
        {
            document.getElementsByClassName("dir1")[0].style.visibility = 'inherit';
            document.getElementsByClassName("dir1")[0].style.display = 'block';

            document.getElementsByClassName("dir2")[0].style.visibility = 'inherit';
            document.getElementsByClassName("dir2")[0].style.display = 'block';

            document.getElementsByClassName("dir3")[0].style.visibility = 'inherit';
            document.getElementsByClassName("dir3")[0].style.display = 'block';

            document.getElementsByClassName("dir4")[0].style.visibility = 'inherit';
            document.getElementsByClassName("dir4")[0].style.display = 'block';
        }

    });

    $('#numOfAuthrisedSignatory').change(function(e)
    {
        var auth1=$('#numOfAuthrisedSignatory').val();
        //("authorized newcount= "+auth1);

        if(auth1==0)
        {
            //document.getElementsByClassName("authorizedAll")[0].style.display='none';
            document.getElementsByClassName("authorized1")[0].style.display='none';
            document.getElementsByClassName("authorized2")[0].style.display='none';
            document.getElementsByClassName("authorized3")[0].style.display='none';
            document.getElementsByClassName("authorized4")[0].style.display='none';

            document.getElementById('authorizedsignatoryprofile').value = "";
            document.getElementById('authorizedsignatoryprofile_title').value = "";
            document.getElementById('authorizedsignatoryprofile_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile_address').value = "";
            document.getElementById('authorizedsignatoryprofile_city').value = "";
            document.getElementById('authorizedsignatoryprofile_State').value = "";
            document.getElementById('authorizedsignatoryprofile_zip').value = "";
            document.getElementById('authorizedsignatoryprofile_country').value = "";
            document.getElementById('authorizedsignatoryprofile_street').value = "";
            document.getElementById('authorizedsignatoryprofile_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile1_designation').value = "";
            document.getElementById('authorizedsignatoryprofile1_owned').value = "";
            document.getElementById('authorizedsignatoryprofile1_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile1_addressId').value = "";

            document.getElementById('authorizedsignatoryprofile2').value = "";
            document.getElementById('authorizedsignatoryprofile2_title').value = "";
            document.getElementById('authorizedsignatoryprofile2_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile2_address').value = "";
            document.getElementById('authorizedsignatoryprofile2_city').value = "";
            document.getElementById('authorizedsignatoryprofile2_State').value = "";
            document.getElementById('authorizedsignatoryprofile2_zip').value = "";
            document.getElementById('authorizedsignatoryprofile2_country').value = "";
            document.getElementById('authorizedsignatoryprofile2_street').value = "";
            document.getElementById('authorizedsignatoryprofile2_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile2_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile2_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile2_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile2_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile2_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile2_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile2_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile2_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile2_designation').value = "";
            document.getElementById('authorizedsignatoryprofile2_owned').value = "";
            document.getElementById('authorizedsignatoryprofile2_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile2_addressId').value = "";

            document.getElementById('authorizedsignatoryprofile3').value = "";
            document.getElementById('authorizedsignatoryprofile3_title').value = "";
            document.getElementById('authorizedsignatoryprofile3_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile3_address').value = "";
            document.getElementById('authorizedsignatoryprofile3_city').value = "";
            document.getElementById('authorizedsignatoryprofile3_State').value = "";
            document.getElementById('authorizedsignatoryprofile3_zip').value = "";
            document.getElementById('authorizedsignatoryprofile3_country').value = "";
            document.getElementById('authorizedsignatoryprofile3_street').value = "";
            document.getElementById('authorizedsignatoryprofile3_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile3_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile3_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile3_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile3_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile3_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile3_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile3_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile3_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile3_designation').value = "";
            document.getElementById('authorizedsignatoryprofile3_owned').value = "";
            document.getElementById('authorizedsignatoryprofile3_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile3_addressId').value = "";

            document.getElementById('authorizedsignatoryprofile4').value = "";
            document.getElementById('authorizedsignatoryprofile4_title').value = "";
            document.getElementById('authorizedsignatoryprofile4_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile4_address').value = "";
            document.getElementById('authorizedsignatoryprofile4_city').value = "";
            document.getElementById('authorizedsignatoryprofile4_State').value = "";
            document.getElementById('authorizedsignatoryprofile4_zip').value = "";
            document.getElementById('authorizedsignatoryprofile4_country').value = "";
            document.getElementById('authorizedsignatoryprofile4_street').value = "";
            document.getElementById('authorizedsignatoryprofile4_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile4_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile4_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile4_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile4_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile4_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile4_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile4_designation').value = "";
            document.getElementById('authorizedsignatoryprofile4_owned').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressId').value = "";
        }
        else if(auth1==1)
        {
            //alert("authorized 1111111111= "+auth1);
            document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
            document.getElementsByClassName("authorized1")[0].style.display='block';

            document.getElementsByClassName("authorized2")[0].style.display='none';
            document.getElementsByClassName("authorized3")[0].style.display='none';
            document.getElementsByClassName("authorized4")[0].style.display='none';

            document.getElementById('authorizedsignatoryprofile2').value = "";
            document.getElementById('authorizedsignatoryprofile2_title').value = "";
            document.getElementById('authorizedsignatoryprofile2_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile2_address').value = "";
            document.getElementById('authorizedsignatoryprofile2_city').value = "";
            document.getElementById('authorizedsignatoryprofile2_State').value = "";
            document.getElementById('authorizedsignatoryprofile2_zip').value = "";
            document.getElementById('authorizedsignatoryprofile2_country').value = "";
            document.getElementById('authorizedsignatoryprofile2_street').value = "";
            document.getElementById('authorizedsignatoryprofile2_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile2_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile2_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile2_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile2_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile2_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile2_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile2_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile2_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile2_designation').value = "";
            document.getElementById('authorizedsignatoryprofile2_owned').value = "";
            document.getElementById('authorizedsignatoryprofile2_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile2_addressId').value = "";

            document.getElementById('authorizedsignatoryprofile3').value = "";
            document.getElementById('authorizedsignatoryprofile3_title').value = "";
            document.getElementById('authorizedsignatoryprofile3_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile3_address').value = "";
            document.getElementById('authorizedsignatoryprofile3_city').value = "";
            document.getElementById('authorizedsignatoryprofile3_State').value = "";
            document.getElementById('authorizedsignatoryprofile3_zip').value = "";
            document.getElementById('authorizedsignatoryprofile3_country').value = "";
            document.getElementById('authorizedsignatoryprofile3_street').value = "";
            document.getElementById('authorizedsignatoryprofile3_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile3_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile3_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile3_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile3_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile3_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile3_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile3_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile3_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile3_designation').value = "";
            document.getElementById('authorizedsignatoryprofile3_owned').value = "";
            document.getElementById('authorizedsignatoryprofile3_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile3_addressId').value = "";

            document.getElementById('authorizedsignatoryprofile4').value = "";
            document.getElementById('authorizedsignatoryprofile4_title').value = "";
            document.getElementById('authorizedsignatoryprofile4_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile4_address').value = "";
            document.getElementById('authorizedsignatoryprofile4_city').value = "";
            document.getElementById('authorizedsignatoryprofile4_State').value = "";
            document.getElementById('authorizedsignatoryprofile4_zip').value = "";
            document.getElementById('authorizedsignatoryprofile4_country').value = "";
            document.getElementById('authorizedsignatoryprofile4_street').value = "";
            document.getElementById('authorizedsignatoryprofile4_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile4_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile4_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile4_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile4_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile4_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile4_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile4_designation').value = "";
            document.getElementById('authorizedsignatoryprofile4_owned').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressId').value = "";

        }
        else if(auth1==2)
        {
            document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
            document.getElementsByClassName("authorized1")[0].style.display='block';

            document.getElementsByClassName("authorized2")[0].style.visibility='inherit';
            document.getElementsByClassName("authorized2")[0].style.display='block';

            document.getElementsByClassName("authorized3")[0].style.display='none';
            document.getElementsByClassName("authorized4")[0].style.display='none';

            document.getElementById('authorizedsignatoryprofile3').value = "";
            document.getElementById('authorizedsignatoryprofile3_title').value = "";
            document.getElementById('authorizedsignatoryprofile3_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile3_address').value = "";
            document.getElementById('authorizedsignatoryprofile3_city').value = "";
            document.getElementById('authorizedsignatoryprofile3_State').value = "";
            document.getElementById('authorizedsignatoryprofile3_zip').value = "";
            document.getElementById('authorizedsignatoryprofile3_country').value = "";
            document.getElementById('authorizedsignatoryprofile3_street').value = "";
            document.getElementById('authorizedsignatoryprofile3_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile3_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile3_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile3_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile3_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile3_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile3_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile3_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile3_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile3_designation').value = "";
            document.getElementById('authorizedsignatoryprofile3_owned').value = "";
            document.getElementById('authorizedsignatoryprofile3_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile3_addressId').value = "";

            document.getElementById('authorizedsignatoryprofile4').value = "";
            document.getElementById('authorizedsignatoryprofile4_title').value = "";
            document.getElementById('authorizedsignatoryprofile4_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile4_address').value = "";
            document.getElementById('authorizedsignatoryprofile4_city').value = "";
            document.getElementById('authorizedsignatoryprofile4_State').value = "";
            document.getElementById('authorizedsignatoryprofile4_zip').value = "";
            document.getElementById('authorizedsignatoryprofile4_country').value = "";
            document.getElementById('authorizedsignatoryprofile4_street').value = "";
            document.getElementById('authorizedsignatoryprofile4_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile4_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile4_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile4_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile4_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile4_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile4_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile4_designation').value = "";
            document.getElementById('authorizedsignatoryprofile4_owned').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressId').value = "";

        }
        else if(auth1==3)
        {
            document.getElementsByClassName("authorized1")[0].style.visibility='inherit';
            document.getElementsByClassName("authorized1")[0].style.display='block';

            document.getElementsByClassName("authorized2")[0].style.visibility='inherit';
            document.getElementsByClassName("authorized2")[0].style.display='block';

            document.getElementsByClassName("authorized3")[0].style.visibility='inherit';
            document.getElementsByClassName("authorized3")[0].style.display='block';

            document.getElementsByClassName("authorized4")[0].style.display='none';

            document.getElementById('authorizedsignatoryprofile4').value = "";
            document.getElementById('authorizedsignatoryprofile4_title').value = "";
            document.getElementById('authorizedsignatoryprofile4_lastname').value = "";
            document.getElementById('authorizedsignatoryprofile4_address').value = "";
            document.getElementById('authorizedsignatoryprofile4_city').value = "";
            document.getElementById('authorizedsignatoryprofile4_State').value = "";
            document.getElementById('authorizedsignatoryprofile4_zip').value = "";
            document.getElementById('authorizedsignatoryprofile4_country').value = "";
            document.getElementById('authorizedsignatoryprofile4_street').value = "";
            document.getElementById('authorizedsignatoryprofile4_telnocc1').value = "";
            document.getElementById('authorizedsignatoryprofile4_telephonenumber').value = "";
            document.getElementById('authorizedsignatoryprofile4_emailaddress').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtypeselect').value = "";
            document.getElementById('authorizedsignatoryprofile4_identificationtype').value = "";
            document.getElementById('authorizedsignatoryprofile4_dateofbirth').value = "";
            document.getElementById('authorizedsignatoryprofile4_nationality').value = "";
            document.getElementById('authorizedsignatoryprofile4_Passportexpirydate').value = "";
            document.getElementById('authorizedsignatoryprofile4_passportissuedate').value = "";
            document.getElementById('authorizedsignatoryprofile4_designation').value = "";
            document.getElementById('authorizedsignatoryprofile4_owned').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressproof').value = "";
            document.getElementById('authorizedsignatoryprofile4_addressId').value = "";

        }
        else if(auth1==4)
        {
            document.getElementsByClassName("authorized1")[0].style.visibility = 'inherit';
            document.getElementsByClassName("authorized1")[0].style.display = 'block';

            document.getElementsByClassName("authorized2")[0].style.visibility = 'inherit';
            document.getElementsByClassName("authorized2")[0].style.display = 'block';

            document.getElementsByClassName("authorized3")[0].style.visibility = 'inherit';
            document.getElementsByClassName("authorized3")[0].style.display = 'block';

            document.getElementsByClassName("authorized4")[0].style.visibility = 'inherit';
            document.getElementsByClassName("authorized4")[0].style.display = 'block';
        }

    });

    $('#numOfCorporateShareholders').change(function(e)
    {
        var corp1=$('#numOfCorporateShareholders').val();
        // alert("corporate newcount= "+corp1);


        if(corp1==0)
        {

            //document.getElementsByClassName("corprateAll")[0].style.display='none';
            document.getElementsByClassName("corporate1")[0].style.display='none';
            document.getElementsByClassName("corporate2")[0].style.display='none';
            document.getElementsByClassName("corporate3")[0].style.display='none';
            document.getElementsByClassName("corporate4")[0].style.display='none';

            document.getElementById('corporateshareholder1_Name').value = "";
            document.getElementById('corporateshareholder1_RegNumber').value = "";
            document.getElementById('corporateshareholder1_owned').value = "";
            document.getElementById('corporateshareholder1_addressproof').value = "";
            document.getElementById('corporateshareholder1_addressId').value = "";
            document.getElementById('corporateshareholder1_address').value ="";
            document.getElementById('corporateshareholder1_street').value = "";
            document.getElementById('corporateshareholder1_city').value = "";
            document.getElementById('corporateshareholder1_State').value = "";
            document.getElementById('corporateshareholder1_country').value = "";
            document.getElementById('corporateshareholder1_zip').value = "";
            document.getElementById('corporateshareholder1_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder1_identificationtype').value = "";

            document.getElementById('corporateshareholder2_Name').value = "";
            document.getElementById('corporateshareholder2_RegNumber').value = "";
            document.getElementById('corporateshareholder2_owned').value = "";
            document.getElementById('corporateshareholder2_addressproof').value = "";
            document.getElementById('corporateshareholder2_addressId').value = "";
            document.getElementById('corporateshareholder2_address').value ="";
            document.getElementById('corporateshareholder2_street').value = "";
            document.getElementById('corporateshareholder2_city').value = "";
            document.getElementById('corporateshareholder2_State').value = "";
            document.getElementById('corporateshareholder2_country').value = "";
            document.getElementById('corporateshareholder2_zip').value = "";
            document.getElementById('corporateshareholder2_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder2_identificationtype').value = "";

            document.getElementById('corporateshareholder3_Name').value = "";
            document.getElementById('corporateshareholder3_RegNumber').value = "";
            document.getElementById('corporateshareholder3_owned').value = "";
            document.getElementById('corporateshareholder3_addressproof').value = "";
            document.getElementById('corporateshareholder3_addressId').value = "";
            document.getElementById('corporateshareholder3_address').value ="";
            document.getElementById('corporateshareholder3_street').value = "";
            document.getElementById('corporateshareholder3_city').value = "";
            document.getElementById('corporateshareholder3_State').value = "";
            document.getElementById('corporateshareholder3_country').value = "";
            document.getElementById('corporateshareholder3_zip').value = "";
            document.getElementById('corporateshareholder3_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder3_identificationtype').value = "";

            document.getElementById('corporateshareholder4_Name').value = "";
            document.getElementById('corporateshareholder4_RegNumber').value = "";
            document.getElementById('corporateshareholder4_owned').value = "";
            document.getElementById('corporateshareholder4_addressproof').value = "";
            document.getElementById('corporateshareholder4_addressId').value = "";
            document.getElementById('corporateshareholder4_address').value ="";
            document.getElementById('corporateshareholder4_street').value = "";
            document.getElementById('corporateshareholder4_city').value = "";
            document.getElementById('corporateshareholder4_State').value = "";
            document.getElementById('corporateshareholder4_country').value = "";
            document.getElementById('corporateshareholder4_zip').value = "";
            document.getElementById('corporateshareholder4_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder4_identificationtype').value = "";


        }
        else if(corp1==1)
        {
            document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
            document.getElementsByClassName("corporate1")[0].style.display='block';

            document.getElementsByClassName("corporate2")[0].style.display='none';
            document.getElementsByClassName("corporate3")[0].style.display='none';
            document.getElementsByClassName("corporate4")[0].style.display='none';

            document.getElementById('corporateshareholder2_Name').value = "";
            document.getElementById('corporateshareholder2_RegNumber').value = "";
            document.getElementById('corporateshareholder2_owned').value = "";
            document.getElementById('corporateshareholder2_addressproof').value = "";
            document.getElementById('corporateshareholder2_addressId').value = "";
            document.getElementById('corporateshareholder2_address').value ="";
            document.getElementById('corporateshareholder2_city').value = "";
            document.getElementById('corporateshareholder2_State').value = "";
            document.getElementById('corporateshareholder2_zip').value = "";
            document.getElementById('corporateshareholder2_country').value = "";
            document.getElementById('corporateshareholder2_street').value = "";
            document.getElementById('corporateshareholder2_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder2_identificationtype').value = "";

            document.getElementById('corporateshareholder3_Name').value = "";
            document.getElementById('corporateshareholder3_RegNumber').value = "";
            document.getElementById('corporateshareholder3_owned').value = "";
            document.getElementById('corporateshareholder3_addressproof').value = "";
            document.getElementById('corporateshareholder3_addressId').value = "";
            document.getElementById('corporateshareholder3_address').value ="";
            document.getElementById('corporateshareholder3_city').value = "";
            document.getElementById('corporateshareholder3_State').value = "";
            document.getElementById('corporateshareholder3_zip').value = "";
            document.getElementById('corporateshareholder3_country').value = "";
            document.getElementById('corporateshareholder3_street').value = "";
            document.getElementById('corporateshareholder3_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder3_identificationtype').value = "";

            document.getElementById('corporateshareholder4_Name').value = "";
            document.getElementById('corporateshareholder4_RegNumber').value = "";
            document.getElementById('corporateshareholder4_owned').value = "";
            document.getElementById('corporateshareholder4_addressproof').value = "";
            document.getElementById('corporateshareholder4_addressId').value = "";
            document.getElementById('corporateshareholder4_address').value ="";
            document.getElementById('corporateshareholder4_city').value = "";
            document.getElementById('corporateshareholder4_State').value = "";
            document.getElementById('corporateshareholder4_zip').value = "";
            document.getElementById('corporateshareholder4_country').value = "";
            document.getElementById('corporateshareholder4_street').value = "";
            document.getElementById('corporateshareholder4_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder4_identificationtype').value = "";

        }
        else if(corp1==2)
        {
            document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
            document.getElementsByClassName("corporate1")[0].style.display='block';

            document.getElementsByClassName("corporate2")[0].style.visibility='inherit';
            document.getElementsByClassName("corporate2")[0].style.display='block';

            document.getElementsByClassName("corporate3")[0].style.display='none';
            document.getElementsByClassName("corporate4")[0].style.display='none';

            document.getElementById('corporateshareholder3_Name').value = "";
            document.getElementById('corporateshareholder3_RegNumber').value = "";
            document.getElementById('corporateshareholder3_owned').value = "";
            document.getElementById('corporateshareholder3_addressproof').value = "";
            document.getElementById('corporateshareholder3_addressId').value = "";
            document.getElementById('corporateshareholder3_address').value ="";
            document.getElementById('corporateshareholder3_city').value = "";
            document.getElementById('corporateshareholder3_State').value = "";
            document.getElementById('corporateshareholder3_zip').value = "";
            document.getElementById('corporateshareholder3_country').value = "";
            document.getElementById('corporateshareholder3_street').value = "";
            document.getElementById('corporateshareholder3_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder3_identificationtype').value = "";

            document.getElementById('corporateshareholder4_Name').value = "";
            document.getElementById('corporateshareholder4_RegNumber').value = "";
            document.getElementById('corporateshareholder4_owned').value = "";
            document.getElementById('corporateshareholder4_addressproof').value = "";
            document.getElementById('corporateshareholder4_addressId').value = "";
            document.getElementById('corporateshareholder4_address').value ="";
            document.getElementById('corporateshareholder4_city').value = "";
            document.getElementById('corporateshareholder4_State').value = "";
            document.getElementById('corporateshareholder4_zip').value = "";
            document.getElementById('corporateshareholder4_country').value = "";
            document.getElementById('corporateshareholder4_street').value = "";
            document.getElementById('corporateshareholder4_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder4_identificationtype').value = "";

        }
        else if(corp1==3)
        {
            document.getElementsByClassName("corporate1")[0].style.visibility='inherit';
            document.getElementsByClassName("corporate1")[0].style.display='block';

            document.getElementsByClassName("corporate2")[0].style.visibility='inherit';
            document.getElementsByClassName("corporate2")[0].style.display='block';

            document.getElementsByClassName("corporate3")[0].style.visibility='inherit';
            document.getElementsByClassName("corporate3")[0].style.display='block';

            document.getElementsByClassName("corporate4")[0].style.display='none';

            document.getElementById('corporateshareholder4_Name').value = "";
            document.getElementById('corporateshareholder4_RegNumber').value = "";
            document.getElementById('corporateshareholder4_owned').value = "";
            document.getElementById('corporateshareholder4_addressproof').value = "";
            document.getElementById('corporateshareholder4_addressId').value = "";
            document.getElementById('corporateshareholder4_address').value ="";
            document.getElementById('corporateshareholder4_city').value = "";
            document.getElementById('corporateshareholder4_State').value = "";
            document.getElementById('corporateshareholder4_zip').value = "";
            document.getElementById('corporateshareholder4_country').value = "";
            document.getElementById('corporateshareholder4_street').value = "";
            document.getElementById('corporateshareholder4_identificationtypeselect').value = "";
            document.getElementById('corporateshareholder4_identificationtype').value = "";

        }
        else if(corp1==4)
        {
            document.getElementsByClassName("corporate1")[0].style.visibility = 'inherit';
            document.getElementsByClassName("corporate1")[0].style.display = 'block';

            document.getElementsByClassName("corporate2")[0].style.visibility = 'inherit';
            document.getElementsByClassName("corporate2")[0].style.display = 'block';

            document.getElementsByClassName("corporate3")[0].style.visibility = 'inherit';
            document.getElementsByClassName("corporate3")[0].style.display = 'block';

            document.getElementsByClassName("corporate4")[0].style.visibility = 'inherit';
            document.getElementsByClassName("corporate4")[0].style.display = 'block';

        }
    });

});



