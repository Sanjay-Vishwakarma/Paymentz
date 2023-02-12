/**
 * Created by Nikhil Poojari on 10-May-18.
 */

function isint(form)
{
    if (isNaN(form.numrows.value))
        return false;
    else
        return true;
}

$('#sandbox-container input').datepicker({
});

$(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
});

$(function() {
    $('#datetimepicker12').datetimepicker({
        format: 'HH:mm:ss',
        useCurrent: true,
    });
});

$(function() {
    $('#datetimepicker13').datetimepicker({
        format: 'HH:mm:ss',
        useCurrent: true,
    });
});
function mySave()
{
    document.getElementById('Save').submit();
}
function myExcel()
{
    document.getElementById('Excel').submit();
}


$(document).ready(function(){

    /*Start onload checkbox*/
    var Transactions_Date               = document.getElementById("Transactions_Date").value;
    var Transactions_TimeZone           = document.getElementById("Transactions_TimeZone").value;
    var Transactions_TrackingID         = document.getElementById("Transactions_TrackingID").value;
    var Transactions_OrderId            = document.getElementById("Transactions_OrderId").value;
    var Transactions_OrderDescription   = document.getElementById("Transactions_OrderDescription").value;
    var Transactions_CardHoldername     = document.getElementById("Transactions_CardHoldername").value;
    var Transactions_CustomerEmail      = document.getElementById("Transactions_CustomerEmail").value;
    var Transactions_CustomerID         = document.getElementById("Transactions_CustomerID").value;
    var Transactions_PayMode            = document.getElementById("Transactions_PayMode").value;
    var Transactions_CardType           = document.getElementById("Transactions_CardType").value;
    var Transactions_Amount             = document.getElementById("Transactions_Amount").value;
    var Transactions_IssuingBank        = document.getElementById("Transactions_IssuingBank").value;
    var Transactions_RefundedAmt        = document.getElementById("Transactions_RefundedAmt").value;
    var Transactions_Currency           = document.getElementById("Transactions_Currency").value;
    var Transactions_Status             = document.getElementById("Transactions_Status").value;
    var Transactions_Remark             = document.getElementById("Transactions_Remark").value;
    var Transactions_Terminal           = document.getElementById("Transactions_Terminal").value;
    var Transactions_LastUpdateDate     = document.getElementById("Transactions_LastUpdateDate").value;
    var Transactions_Mode               = document.getElementById("Transactions_Mode").value;

    if(Transactions_Date=="Y")
    {

        $('#Transactions_Date').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Date').css('display','table-cell');
        $('#myTable tr > td.Transactions_Date').css('display','table-cell');
        document.getElementById('Transactions_Date_hidden').value="Y";

    }
    if(Transactions_Date=="")
    {
        $('#Transactions_Date').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Date').css('display','none');
        $('#myTable tr > td.Transactions_Date').css('display','none');
        document.getElementById('Transactions_Date_hidden').value='';

    }
    if(Transactions_TimeZone=="Y")
    {

        $('#Transactions_TimeZone').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_TimeZone').css('display','table-cell');
        $('#myTable tr > td.Transactions_TimeZone').css('display','table-cell');
        document.getElementById('Transactions_TimeZone_hidden').value="Y";

    }
    if(Transactions_TimeZone=="")
    {
        $('#Transactions_TimeZone').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_TimeZone').css('display','none');
        $('#myTable tr > td.Transactions_TimeZone').css('display','none');
        document.getElementById('Transactions_TimeZone_hidden').value='';

    }
    if(Transactions_TrackingID=="Y")
    {
        $('#Transactions_TrackingID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_TrackingID').css('display','table-cell');
        $('#myTable tr > td.Transactions_TrackingID').css('display','table-cell');
        document.getElementById('Transactions_TrackingID_hidden').value="Y";

    }
    if(Transactions_TrackingID=="")
    {
        $('#Transactions_TrackingID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_TrackingID').css('display','none');
        $('#myTable tr > td.Transactions_TrackingID').css('display','none');
        document.getElementById('Transactions_TrackingID_hidden').value='';

    }
    if(Transactions_PayMode=="Y")
    {
        $('#Transactions_PayMode').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_PayMode').css('display','table-cell');
        $('#myTable tr > td.Transactions_PayMode').css('display','table-cell');
        document.getElementById('Transactions_PayMode_hidden').value="Y";

    }
    if(Transactions_PayMode=="")
    {
        $('#Transactions_PayMode').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_PayMode').css('display','none');
        $('#myTable tr > td.Transactions_PayMode').css('display','none');
        document.getElementById('Transactions_PayMode_hidden').value='';

    }
    if(Transactions_CardType=="Y")
    {
        $('#Transactions_CardType').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_CardType').css('display','table-cell');
        $('#myTable tr > td.Transactions_CardType').css('display','table-cell');
        document.getElementById('Transactions_CardType_hidden').value="Y";

    }
    if(Transactions_CardType=="")
    {
        $('#Transactions_CardType').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_CardType').css('display','none');
        $('#myTable tr > td.Transactions_CardType').css('display','none');
        document.getElementById('Transactions_CardType_hidden').value='';

    }
    if(Transactions_Currency=="Y")
    {
        $('#Transactions_Currency').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Currency').css('display','table-cell');
        $('#myTable tr > td.Transactions_Currency').css('display','table-cell');
        document.getElementById('Transactions_Currency_hidden').value="Y";

    }
    if(Transactions_Currency=="")
    {
        $('#Transactions_Currency').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Currency').css('display','none');
        $('#myTable tr > td.Transactions_Currency').css('display','none');
        document.getElementById('Transactions_Currency_hidden').value='';

    }
    if(Transactions_OrderId=="Y")
    {
        $('#Transactions_OrderId').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_OrderId').css('display','table-cell');
        $('#myTable tr > td.Transactions_OrderId').css('display','table-cell');
        document.getElementById('Transactions_OrderId_hidden').value="Y";

    }
    if(Transactions_OrderId=="")
    {
        $('#Transactions_OrderId').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_OrderId').css('display','none');
        $('#myTable tr > td.Transactions_OrderId').css('display','none');
        document.getElementById('Transactions_OrderId_hidden').value='';

    }
    if(Transactions_OrderDescription=="Y")
    {
        $('#Transactions_OrderDescription').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_OrderDescription').css('display','table-cell');
        $('#myTable tr > td.Transactions_OrderDescription').css('display','table-cell');
        document.getElementById('Transactions_OrderDescription_hidden').value="Y";

    }
    if(Transactions_Mode=="Y")
    {
        $('#Transactions_Mode').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Mode').css('display','table-cell');
        $('#myTable tr > td.Transactions_Mode').css('display','table-cell');
        document.getElementById('Transactions_Mode_hidden').value="Y";

    }
    if(Transactions_OrderDescription == "")
    {
        $('#Transactions_OrderDescription').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_OrderDescription').css('display','none');
        $('#myTable tr > td.Transactions_OrderDescription').css('display','none');
        document.getElementById('Transactions_OrderDescription_hidden').value='';

    }
    if(Transactions_Mode == "")
    {
        $('#Transactions_Mode').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Mode').css('display','none');
        $('#myTable tr > td.Transactions_Mode').css('display','none');
        document.getElementById('Transactions_Mode_hidden').value='';

    }
    if(Transactions_Amount=="Y")
    {
        $('#Transactions_Amount').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Amount').css('display','table-cell');
        $('#myTable tr > td.Transactions_Amount').css('display','table-cell');
        document.getElementById('Transactions_Amount_hidden').value="Y";

    }
    if(Transactions_Amount=="")
    {
        $('#Transactions_Amount').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Amount').css('display','none');
        $('#myTable tr > td.Transactions_Amount').css('display','none');
        document.getElementById('Transactions_Amount_hidden').value='';

    }
    if(Transactions_IssuingBank=="Y")
    {
        $('#Transactions_IssuingBank').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_IssuingBank').css('display','table-cell');
        $('#myTable tr > td.Transactions_IssuingBank').css('display','table-cell');
        document.getElementById('Transactions_IssuingBank_hidden').value="Y";

    }
    if(Transactions_IssuingBank=="")
    {
        $('#Transactions_IssuingBank').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_IssuingBank').css('display','none');
        $('#myTable tr > td.Transactions_IssuingBank').css('display','none');
        document.getElementById('Transactions_IssuingBank_hidden').value='';

    }
    if(Transactions_RefundedAmt=="Y")
    {
        $('#Transactions_RefundedAmt').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_RefundedAmt').css('display','table-cell');
        $('#myTable tr > td.Transactions_RefundedAmt').css('display','table-cell');
        document.getElementById('Transactions_RefundedAmt_hidden').value="Y";

    }
    if(Transactions_RefundedAmt=="")
    {
        $('#Transactions_RefundedAmt').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_RefundedAmt').css('display','none');
        $('#myTable tr > td.Transactions_RefundedAmt').css('display','none');
        document.getElementById('Transactions_RefundedAmt_hidden').value='';

    }
    if(Transactions_Status=="Y")
    {
        $('#Transactions_Status').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Status').css('display','table-cell');
        $('#myTable tr > td.Transactions_Status').css('display','table-cell');
        document.getElementById('Transactions_Status_hidden').value="Y";

    }
    if(Transactions_Status=="")
    {
        $('#Transactions_Status').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Status').css('display','none');
        $('#myTable tr > td.Transactions_Status').css('display','none');
        document.getElementById('Transactions_Status_hidden').value='';

    }
    if(Transactions_Terminal=="Y")
    {
        $('#Transactions_Terminal').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Terminal').css('display','table-cell');
        $('#myTable tr > td.Transactions_Terminal').css('display','table-cell');
        document.getElementById('Transactions_Terminal_hidden').value="Y";

    }
    if(Transactions_Terminal=="")
    {
        $('#Transactions_Terminal').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Terminal').css('display','none');
        $('#myTable tr > td.Transactions_Terminal').css('display','none');
        document.getElementById('Transactions_Terminal_hidden').value='';

    }
    if(Transactions_CustomerID=="Y")
    {

        $('#Transactions_CustomerID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_CustomerID').css('display','table-cell');
        $('#myTable tr > td.Transactions_CustomerID').css('display','table-cell');
        document.getElementById('Transactions_CustomerID_hidden').value="Y";

    }
    if(Transactions_CustomerID=="")
    {
        $('#Transactions_CustomerID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_CustomerID').css('display','none');
        $('#myTable tr > td.Transactions_CustomerID').css('display','none');
        document.getElementById('Transactions_CustomerID_hidden').value='';

    }
    if(Transactions_Remark=="Y")
    {

        $('#Transactions_Remark').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Remark').css('display','table-cell');
        $('#myTable tr > td.Transactions_Remark').css('display','table-cell');
        document.getElementById('Transactions_Remark_hidden').value="Y";

    }
    if(Transactions_Remark=="")
    {
        $('#Transactions_Remark').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_Remark').css('display','none');
        $('#myTable tr > td.Transactions_Remark').css('display','none');
        document.getElementById('Transactions_Remark_hidden').value='';

    }
    if(Transactions_CardHoldername=="Y")
    {

        $('#Transactions_CardHoldername').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_CardHoldername').css('display','table-cell');
        $('#myTable tr > td.Transactions_CardHoldername').css('display','table-cell');
        document.getElementById('Transactions_CardHoldername_hidden').value="Y";

    }
    if(Transactions_CardHoldername=="")
    {
        $('#Transactions_CardHoldername').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_CardHoldername').css('display','none');
        $('#myTable tr > td.Transactions_CardHoldername').css('display','none');
        document.getElementById('Transactions_CardHoldername_hidden').value='';

    }
    if(Transactions_CustomerEmail=="Y")
    {

        $('#Transactions_CustomerEmail').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_CustomerEmail').css('display','table-cell');
        $('#myTable tr > td.Transactions_CustomerEmail').css('display','table-cell');
        document.getElementById('Transactions_CustomerEmail_hidden').value="Y";

    }
    if(Transactions_CustomerEmail=="")
    {
        $('#Transactions_CustomerEmail').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_CustomerEmail').css('display','none');
        $('#myTable tr > td.Transactions_CustomerEmail').css('display','none');
        document.getElementById('Transactions_CustomerEmail_hidden').value='';

    }
    if(Transactions_LastUpdateDate=="Y")
    {

        $('#Transactions_LastUpdateDate').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_LastUpdateDate').css('display','table-cell');
        $('#myTable tr > td.Transactions_LastUpdateDate').css('display','table-cell');
        document.getElementById('Transactions_LastUpdateDate_hidden').value="Y";

    }
    if(Transactions_LastUpdateDate=="")
    {
        $('#Transactions_LastUpdateDate').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.Transactions_LastUpdateDate').css('display','none');
        $('#myTable tr > td.Transactions_LastUpdateDate').css('display','none');
        document.getElementById('Transactions_LastUpdateDate_hidden').value='';

    }

    if(Transactions_Date == "" && Transactions_TimeZone == "" &&  Transactions_TrackingID == "" && Transactions_OrderId == "" && Transactions_OrderDescription == "" && Transactions_CardHoldername == "" && Transactions_CustomerEmail == "" && Transactions_CustomerID == "" && Transactions_PayMode == "" && Transactions_CardType == "" && Transactions_IssuingBank == "" && Transactions_Amount == "" && Transactions_RefundedAmt == "" && Transactions_Currency == "" && Transactions_Status == "" && Transactions_Remark == "" && Transactions_Terminal == "" && Transactions_LastUpdateDate == "" && Transactions_Mode =="")
    {
        //alert("Nothing Selected");
        /*$("#Excel").hide();
        $("#myTable").hide();
        $(".showingid").hide();
        $("#page_navigate").hide();
        $("#bginfo_checkbox").show();
        $("#grpChkBox").css("position","inherit");*/

        $('#Transactions_Date').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Date').css('display','table-cell');
        $('#myTable tr > td.Transactions_Date').css('display','table-cell');
        document.getElementById('Transactions_Date_hidden').value="Y";

        $('#Transactions_TimeZone').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_TimeZone').css('display','table-cell');
        $('#myTable tr > td.Transactions_TimeZone').css('display','table-cell');
        document.getElementById('Transactions_TimeZone_hidden').value="Y";

        $('#Transactions_TrackingID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_TrackingID').css('display','table-cell');
        $('#myTable tr > td.Transactions_TrackingID').css('display','table-cell');
        document.getElementById('Transactions_TrackingID_hidden').value="Y";

        $('#Transactions_OrderId').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_OrderId').css('display','table-cell');
        $('#myTable tr > td.Transactions_OrderId').css('display','table-cell');
        document.getElementById('Transactions_OrderId_hidden').value="Y";

        $('#Transactions_OrderDescription').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_OrderDescription').css('display','table-cell');
        $('#myTable tr > td.Transactions_OrderDescription').css('display','table-cell');
        document.getElementById('Transactions_OrderDescription_hidden').value="Y";


        $('#Transactions_Currency').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Currency').css('display','table-cell');
        $('#myTable tr > td.Transactions_Currency').css('display','table-cell');
        document.getElementById('Transactions_Currency_hidden').value="Y";

        $('#Transactions_Amount').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Amount').css('display','table-cell');
        $('#myTable tr > td.Transactions_Amount').css('display','table-cell');
        document.getElementById('Transactions_Amount_hidden').value="Y";

        $('#Transactions_RefundedAmt').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_RefundedAmt').css('display','table-cell');
        $('#myTable tr > td.Transactions_RefundedAmt').css('display','table-cell');
        document.getElementById('Transactions_RefundedAmt_hidden').value="Y";

        $('#Transactions_Status').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Status').css('display','table-cell');
        $('#myTable tr > td.Transactions_Status').css('display','table-cell');
        document.getElementById('Transactions_Status_hidden').value="Y";

        $('#Transactions_Remark').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Remark').css('display','table-cell');
        $('#myTable tr > td.Transactions_Remark').css('display','table-cell');
        document.getElementById('Transactions_Remark_hidden').value="Y";

        $('#Transactions_Terminal').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.Transactions_Terminal').css('display','table-cell');
        $('#myTable tr > td.Transactions_Terminal').css('display','table-cell');
        document.getElementById('Transactions_Terminal_hidden').value="Y";
    }
    else
    {
        //alert("Selected");
        $("#Excel").show();
        $("#myTable").show();
        $(".showingid").show();
        $("#page_navigate").show();
        $("#bginfo_checkbox").hide();
        $("#grpChkBox").css("position","absolute");
    }


    /*End onload checkbox*/

    /*Start Oncheck checkbox */

    $("input[name='Transactions_Date']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_Date').value="Y";
            document.getElementById('Transactions_Date_hidden').value="Y";
            $('#myTable th.Transactions_Date').css('display','table-cell');
            $('#myTable tr > td.Transactions_Date').css('display','table-cell');
        }

        else{
            document.getElementById('Transactions_Date').value='';
            document.getElementById('Transactions_Date_hidden').value='';
            $('#myTable th.Transactions_Date').css('display','none');
            $('#myTable tr > td.Transactions_Date').css('display','none');
        }
    });
    $("input[name='Transactions_TimeZone']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_TimeZone').value="Y";
            document.getElementById('Transactions_TimeZone_hidden').value="Y";
            $('#myTable th.Transactions_TimeZone').css('display','table-cell');
            $('#myTable tr > td.Transactions_TimeZone').css('display','table-cell');
        }

        else{
            document.getElementById('Transactions_TimeZone').value='';
            document.getElementById('Transactions_TimeZone_hidden').value='';
            $('#myTable th.Transactions_TimeZone').css('display','none');
            $('#myTable tr > td.Transactions_TimeZone').css('display','none');
        }
    });

    $("input[name='Transactions_TrackingID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_TrackingID').value="Y";
            document.getElementById('Transactions_TrackingID_hidden').value="Y";
            $('#myTable th.Transactions_TrackingID').css('display','table-cell');
            $('#myTable tr > td.Transactions_TrackingID').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_TrackingID').value='';
            document.getElementById('Transactions_TrackingID_hidden').value='';
            $('#myTable th.Transactions_TrackingID').css('display','none');
            $('#myTable tr > td.Transactions_TrackingID').css('display','none');
        }
    });

    $("input[name='Transactions_PayMode']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_PayMode').value="Y";
            document.getElementById('Transactions_PayMode_hidden').value="Y";
            $('#myTable th.Transactions_PayMode').css('display','table-cell');
            $('#myTable tr > td.Transactions_PayMode').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_PayMode').value='';
            document.getElementById('Transactions_PayMode_hidden').value='';
            $('#myTable th.Transactions_PayMode').css('display','none');
            $('#myTable tr > td.Transactions_PayMode').css('display','none');
        }
    });
    $("input[name='Transactions_CardType']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_CardType').value="Y";
            document.getElementById('Transactions_CardType_hidden').value="Y";
            $('#myTable th.Transactions_CardType').css('display','table-cell');
            $('#myTable tr > td.Transactions_CardType').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_CardType').value='';
            document.getElementById('Transactions_CardType_hidden').value='';
            $('#myTable th.Transactions_CardType').css('display','none');
            $('#myTable tr > td.Transactions_CardType').css('display','none');
        }
    });
    $("input[name='Transactions_IssuingBank']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_IssuingBank').value="Y";
            document.getElementById('Transactions_IssuingBank_hidden').value="Y";
            $('#myTable th.Transactions_IssuingBank').css('display','table-cell');
            $('#myTable tr > td.Transactions_IssuingBank').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_IssuingBank').value='';
            document.getElementById('Transactions_IssuingBank_hidden').value='';
            $('#myTable th.Transactions_IssuingBank').css('display','none');
            $('#myTable tr > td.Transactions_IssuingBank').css('display','none');
        }
    });
    $("input[name='Transactions_Currency']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_Currency').value="Y";
            document.getElementById('Transactions_Currency_hidden').value="Y";
            $('#myTable th.Transactions_Currency').css('display','table-cell');
            $('#myTable tr > td.Transactions_Currency').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Currency').value='';
            document.getElementById('Transactions_Currency_hidden').value='';
            $('#myTable th.Transactions_Currency').css('display','none');
            $('#myTable tr > td.Transactions_Currency').css('display','none');
        }
    });
    $("input[name='Transactions_OrderId']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_OrderId').value="Y";
            document.getElementById('Transactions_OrderId_hidden').value="Y";
            $('#myTable th.Transactions_OrderId').css('display','table-cell');
            $('#myTable tr > td.Transactions_OrderId').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_OrderId').value='';
            document.getElementById('Transactions_OrderId_hidden').value='';
            $('#myTable th.Transactions_OrderId').css('display','none');
            $('#myTable tr > td.Transactions_OrderId').css('display','none');
        }
    });
    $("input[name='Transactions_OrderDescription']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_OrderDescription').value="Y";
            document.getElementById('Transactions_OrderDescription_hidden').value="Y";
            $('#myTable th.Transactions_OrderDescription').css('display','table-cell');
            $('#myTable tr > td.Transactions_OrderDescription').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_OrderDescription').value='';
            document.getElementById('Transactions_OrderDescription_hidden').value='';
            $('#myTable th.Transactions_OrderDescription').css('display','none');
            $('#myTable tr > td.Transactions_OrderDescription').css('display','none');
        }
    });
    $("input[name='Transactions_Amount']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_Amount').value="Y";
            document.getElementById('Transactions_Amount_hidden').value="Y";
            $('#myTable th.Transactions_Amount').css('display','table-cell');
            $('#myTable tr > td.Transactions_Amount').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Amount').value='';
            document.getElementById('Transactions_Amount_hidden').value='';
            $('#myTable th.Transactions_Amount').css('display','none');
            $('#myTable tr > td.Transactions_Amount').css('display','none');
        }
    });
    $("input[name='Transactions_RefundedAmt']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_RefundedAmt').value="Y";
            document.getElementById('Transactions_RefundedAmt_hidden').value="Y";
            $('#myTable th.Transactions_RefundedAmt').css('display','table-cell');
            $('#myTable tr > td.Transactions_RefundedAmt').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_RefundedAmt').value='';
            document.getElementById('Transactions_RefundedAmt_hidden').value='';
            $('#myTable th.Transactions_RefundedAmt').css('display','none');
            $('#myTable tr > td.Transactions_RefundedAmt').css('display','none');
        }
    });
    $("input[name='Transactions_Currency']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_Currency').value="Y";
            document.getElementById('Transactions_Currency_hidden').value="Y";
            $('#myTable th.Transactions_Currency').css('display','table-cell');
            $('#myTable tr > td.Transactions_Currency').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Currency').value='';
            document.getElementById('Transactions_Currency_hidden').value='';
            $('#myTable th.Transactions_Currency').css('display','none');
            $('#myTable tr > td.Transactions_Currency').css('display','none');
        }
    });
    $("input[name='Transactions_Remark']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('Transactions_Remark').value="Y";
            document.getElementById('Transactions_Remark_hidden').value="Y";
            $('#myTable th.Transactions_Remark').css('display','table-cell');
            $('#myTable tr > td.Transactions_Remark').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Remark').value='';
            document.getElementById('Transactions_Remark_hidden').value='';
            $('#myTable th.Transactions_Remark').css('display','none');
            $('#myTable tr > td.Transactions_Remark').css('display','none');
        }
    });
    $("input[name='Transactions_Status']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_Status').value="Y";
            document.getElementById('Transactions_Status_hidden').value="Y";
            $('#myTable th.Transactions_Status').css('display','table-cell');
            $('#myTable tr > td.Transactions_Status').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Status').value='';
            document.getElementById('Transactions_Status_hidden').value='';
            $('#myTable th.Transactions_Status').css('display','none');
            $('#myTable tr > td.Transactions_Status').css('display','none');
        }
    });
    $("input[name='Transactions_Terminal']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_Terminal').value="Y";
            document.getElementById('Transactions_Terminal_hidden').value="Y";
            $('#myTable th.Transactions_Terminal').css('display','table-cell');
            $('#myTable tr > td.Transactions_Terminal').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Terminal').value='';
            document.getElementById('Transactions_Terminal_hidden').value='';
            $('#myTable th.Transactions_Terminal').css('display','none');
            $('#myTable tr > td.Transactions_Terminal').css('display','none');
        }
    });
    $("input[name='Transactions_CustomerID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){


            document.getElementById('Transactions_CustomerID').value="Y";
            document.getElementById('Transactions_CustomerID_hidden').value="Y";
            $('#myTable th.Transactions_CustomerID').css('display','table-cell');
            $('#myTable tr > td.Transactions_CustomerID').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_CustomerID').value='';
            document.getElementById('Transactions_CustomerID_hidden').value='';
            $('#myTable th.Transactions_CustomerID').css('display','none');
            $('#myTable tr > td.Transactions_CustomerID').css('display','none');
        }
    });
    $("input[name='Transactions_LastUpdateDate']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){

            document.getElementById('Transactions_LastUpdateDate').value="Y";
            document.getElementById('Transactions_LastUpdateDate_hidden').value="Y";
            $('#myTable th.Transactions_LastUpdateDate').css('display','table-cell');
            $('#myTable tr > td.Transactions_LastUpdateDate').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_LastUpdateDate').value='';
            document.getElementById('Transactions_LastUpdateDate_hidden').value='';
            $('#myTable th.Transactions_LastUpdateDate').css('display','none');
            $('#myTable tr > td.Transactions_LastUpdateDate').css('display','none');
        }
    });
    $("input[name='Transactions_Mode']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){

            document.getElementById('Transactions_Mode').value="Y";
            document.getElementById('Transactions_Mode_hidden').value="Y";
            $('#myTable th.Transactions_Mode').css('display','table-cell');
            $('#myTable tr > td.Transactions_Mode').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_Mode').value='';
            document.getElementById('Transactions_Mode_hidden').value='';
            $('#myTable th.Transactions_Mode').css('display','none');
            $('#myTable tr > td.Transactions_Mode').css('display','none');
        }
    });
    $("input[name='Transactions_CustomerEmail']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){

            document.getElementById('Transactions_CustomerEmail').value="Y";
            document.getElementById('Transactions_CustomerEmail_hidden').value="Y";
            $('#myTable th.Transactions_CustomerEmail').css('display','table-cell');
            $('#myTable tr > td.Transactions_CustomerEmail').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_CustomerEmail').value='';
            document.getElementById('Transactions_CustomerEmail_hidden').value='';
            $('#myTable th.Transactions_CustomerEmail').css('display','none');
            $('#myTable tr > td.Transactions_CustomerEmail').css('display','none');
        }
    });
    $("input[name='Transactions_CardHoldername']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){

            document.getElementById('Transactions_CardHoldername').value="Y";
            document.getElementById('Transactions_CardHoldername_hidden').value="Y";
            $('#myTable th.Transactions_CardHoldername').css('display','table-cell');
            $('#myTable tr > td.Transactions_CardHoldername').css('display','table-cell');
        }

        else{

            document.getElementById('Transactions_CardHoldername').value='';
            document.getElementById('Transactions_CardHoldername_hidden').value='';
            $('#myTable th.Transactions_CardHoldername').css('display','none');
            $('#myTable tr > td.Transactions_CardHoldername').css('display','none');
        }
    });


    $("#terminalbutton").click(function(request, response)
    {
        $.ajax( {
            url: "/merchant/servlet/TransactionDetails",
            dataType: "POST",
            data: {
                trackingid: $('#trackingid').val(),
                ctoken: $('#ctoken').val(),
                method:"POST",
                term: request.term

            },
            success: function( data ) {
                //response( data );

                response($.map(data, function (value, key) {
                    return {
                        label: value,
                        value: key
                    };
                }));
            }
        });
        alert("inside function..........")
    });


    /*End Oncheck checkbox */

});