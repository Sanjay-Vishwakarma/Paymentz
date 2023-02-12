/**
 * Created by Admin on 12-11-2022.
 */

function mySave()
{
    document.getElementById('Save').submit();
}
$(document).ready(function(){

    var partnerTransactions_Transaction_Date1               = document.getElementById("partnerTransactions_Transaction_Date1").value;
    var partnerTransactions_TimeZone           = document.getElementById("partnerTransactions_TimeZone").value;
    var partnerTransactions_TrackingID         = document.getElementById("partnerTransactions_TrackingID").value;
    var partnerTransactions_OrderID            = document.getElementById("partnerTransactions_OrderID").value;
    var partnerTransactions_OrderDescription   = document.getElementById("partnerTransactions_OrderDescription").value;
    var partnerTransactions_Card_Holder_Name     = document.getElementById("partnerTransactions_Card_Holder_Name").value;
    var partnerTransactions_Customer_Email      = document.getElementById("partnerTransactions_Customer_Email").value;
    var partnerTransactions_CustomerID         = document.getElementById("partnerTransactions_CustomerID").value;
    var partnerTransactions_PaymentMode            = document.getElementById("partnerTransactions_PaymentMode").value;
    var partnerTransactions_PaymentBrand           = document.getElementById("partnerTransactions_PaymentBrand").value;
    var partnerTransactions_Amount             = document.getElementById("partnerTransactions_Amount").value;
    var partnerTransactions_RefundAmount        = document.getElementById("partnerTransactions_RefundAmount").value;
    var partnerTransactions_Currency           = document.getElementById("partnerTransactions_Currency").value;
    var partnerTransactions_Status1             = document.getElementById("partnerTransactions_Status1").value;
    var partnerTransactions_Remark             = document.getElementById("partnerTransactions_Remark").value;
    var partnerTransactions_TerminalID           = document.getElementById("partnerTransactions_TerminalID").value;
    var partnerTransactions_LastUpdateDate     = document.getElementById("partnerTransactions_LastUpdateDate").value;
    var transactions_mode               = document.getElementById("transactions_mode").value;
    var partnerTransactions_PaymentID          = document.getElementById("partnerTransactions_PaymentID").value;
    var partnerTransactions_PartnerID          = document.getElementById("partnerTransactions_PartnerID").value;
    var partnerTransactions_MerchantID         = document.getElementById("partnerTransactions_MerchantID").value;

    if(partnerTransactions_Transaction_Date1=="Y")
    {
        $('#partnerTransactions_Transaction_Date1').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Transaction_Date1').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Transaction_Date1').css('display','table-cell');
        document.getElementById('partnerTransactions_Transaction_Date1_hidden').value="Y";
    }
    if(partnerTransactions_Transaction_Date1=="")
    {
        $('#partnerTransactions_Transaction_Date1').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Transaction_Date1').css('display','none');
        $('#myTable tr > td.partnerTransactions_Transaction_Date1').css('display','none');
        document.getElementById('partnerTransactions_Transaction_Date1_hidden').value='';
    }
    if(partnerTransactions_TimeZone=="Y")
    {
        $('#partnerTransactions_TimeZone').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_TimeZone').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_TimeZone').css('display','table-cell');
        document.getElementById('partnerTransactions_TimeZone_hidden').value="Y";
    }
    if(partnerTransactions_TimeZone=="")
    {
        $('#partnerTransactions_TimeZone').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_TimeZone').css('display','none');
        $('#myTable tr > td.partnerTransactions_TimeZone').css('display','none');
        document.getElementById('partnerTransactions_TimeZone_hidden').value='';
    }
    if(partnerTransactions_TrackingID=="Y")
    {
        $('#partnerTransactions_TrackingID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_TrackingID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_TrackingID').css('display','table-cell');
        document.getElementById('partnerTransactions_TrackingID_hidden').value="Y";
    }
    if(partnerTransactions_TrackingID=="")
    {
        $('#partnerTransactions_TrackingID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_TrackingID').css('display','none');
        $('#myTable tr > td.partnerTransactions_TrackingID').css('display','none');
        document.getElementById('partnerTransactions_TrackingID_hidden').value='';
    }
    if(partnerTransactions_PaymentMode=="Y")
    {
        $('#partnerTransactions_PaymentMode').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_PaymentMode').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_PaymentMode').css('display','table-cell');
        document.getElementById('partnerTransactions_PaymentMode_hidden').value="Y";
    }
    if(partnerTransactions_PaymentMode=="")
    {
        $('#partnerTransactions_PaymentMode').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_PaymentMode').css('display','none');
        $('#myTable tr > td.partnerTransactions_PaymentMode').css('display','none');
        document.getElementById('partnerTransactions_PaymentMode_hidden').value='';
    }
    if(partnerTransactions_PaymentBrand=="Y")
    {
        $('#partnerTransactions_PaymentBrand').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_PaymentBrand').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_PaymentBrand').css('display','table-cell');
        document.getElementById('partnerTransactions_PaymentBrand_hidden').value="Y";
    }
    if(partnerTransactions_PaymentBrand=="")
    {
        $('#partnerTransactions_PaymentBrand').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_PaymentBrand').css('display','none');
        $('#myTable tr > td.partnerTransactions_PaymentBrand').css('display','none');
        document.getElementById('partnerTransactions_PaymentBrand_hidden').value='';
    }
    if(partnerTransactions_Currency=="Y")
    {
        $('#partnerTransactions_Currency').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Currency').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Currency').css('display','table-cell');
        document.getElementById('partnerTransactions_Currency_hidden').value="Y";
    }
    if(partnerTransactions_Currency=="")
    {
        $('#partnerTransactions_Currency').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Currency').css('display','none');
        $('#myTable tr > td.partnerTransactions_Currency').css('display','none');
        document.getElementById('partnerTransactions_Currency_hidden').value='';
    }
    if(partnerTransactions_OrderID=="Y")
    {
        $('#partnerTransactions_OrderID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_OrderID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_OrderID').css('display','table-cell');
        document.getElementById('partnerTransactions_OrderID_hidden').value="Y";
    }
    if(partnerTransactions_OrderID=="")
    {
        $('#partnerTransactions_OrderID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_OrderID').css('display','none');
        $('#myTable tr > td.partnerTransactions_OrderID').css('display','none');
        document.getElementById('partnerTransactions_OrderID_hidden').value='';
    }
    if(partnerTransactions_OrderDescription=="Y")
    {
        $('#partnerTransactions_OrderDescription').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_OrderDescription').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_OrderDescription').css('display','table-cell');
        document.getElementById('partnerTransactions_OrderDescription_hidden').value="Y";
    }
    if(partnerTransactions_OrderDescription == "")
    {
        $('#partnerTransactions_OrderDescription').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_OrderDescription').css('display','none');
        $('#myTable tr > td.partnerTransactions_OrderDescription').css('display','none');
        document.getElementById('partnerTransactions_OrderDescription_hidden').value='';
    }
    if(transactions_mode=="Y")
    {
        $('#transactions_mode').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.transactions_mode').css('display','table-cell');
        $('#myTable tr > td.transactions_mode').css('display','table-cell');
        document.getElementById('transactions_mode_hidden').value="Y";
    }
    if(transactions_mode == "")
    {
        $('#transactions_mode').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.transactions_mode').css('display','none');
        $('#myTable tr > td.transactions_mode').css('display','none');
        document.getElementById('transactions_mode_hidden').value='';
    }
    if(partnerTransactions_Amount=="Y")
    {
        $('#partnerTransactions_Amount').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Amount').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Amount').css('display','table-cell');
        document.getElementById('partnerTransactions_Amount_hidden').value="Y";
    }
    if(partnerTransactions_Amount=="")
    {
        $('#partnerTransactions_Amount').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Amount').css('display','none');
        $('#myTable tr > td.partnerTransactions_Amount').css('display','none');
        document.getElementById('partnerTransactions_Amount_hidden').value='';
    }
    if(partnerTransactions_RefundAmount=="Y")
    {
        $('#partnerTransactions_RefundAmount').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_RefundAmount').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_RefundAmount').css('display','table-cell');
        document.getElementById('partnerTransactions_RefundAmount_hidden').value="Y";
    }
    if(partnerTransactions_RefundAmount=="")
    {
        $('#partnerTransactions_RefundAmount').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_RefundAmount').css('display','none');
        $('#myTable tr > td.partnerTransactions_RefundAmount').css('display','none');
        document.getElementById('partnerTransactions_RefundAmount_hidden').value='';
    }
    if(partnerTransactions_Status1=="Y")
    {
        $('#partnerTransactions_Status1').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Status1').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Status1').css('display','table-cell');
        document.getElementById('partnerTransactions_Status1_hidden').value="Y";
    }
    if(partnerTransactions_Status1=="")
    {
        $('#partnerTransactions_Status1').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Status1').css('display','none');
        $('#myTable tr > td.partnerTransactions_Status1').css('display','none');
        document.getElementById('partnerTransactions_Status1_hidden').value='';
    }
    if(partnerTransactions_TerminalID=="Y")
    {
        $('#partnerTransactions_TerminalID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_TerminalID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_TerminalID').css('display','table-cell');
        document.getElementById('partnerTransactions_TerminalID_hidden').value="Y";
    }
    if(partnerTransactions_TerminalID=="")
    {
        $('#partnerTransactions_TerminalID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_TerminalID').css('display','none');
        $('#myTable tr > td.partnerTransactions_TerminalID').css('display','none');
        document.getElementById('partnerTransactions_TerminalID_hidden').value='';
    }
    if(partnerTransactions_CustomerID=="Y")
    {

        $('#partnerTransactions_CustomerID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_CustomerID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_CustomerID').css('display','table-cell');
        document.getElementById('partnerTransactions_CustomerID_hidden').value="Y";
    }
    if(partnerTransactions_CustomerID=="")
    {
        $('#partnerTransactions_CustomerID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_CustomerID').css('display','none');
        $('#myTable tr > td.partnerTransactions_CustomerID').css('display','none');
        document.getElementById('partnerTransactions_CustomerID_hidden').value='';
    }
    if(partnerTransactions_Remark=="Y")
    {

        $('#partnerTransactions_Remark').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Remark').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Remark').css('display','table-cell');
        document.getElementById('partnerTransactions_Remark_hidden').value="Y";
    }
    if(partnerTransactions_Remark=="")
    {
        $('#partnerTransactions_Remark').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Remark').css('display','none');
        $('#myTable tr > td.partnerTransactions_Remark').css('display','none');
        document.getElementById('partnerTransactions_Remark_hidden').value='';
    }
    if(partnerTransactions_Card_Holder_Name=="Y")
    {
        $('#partnerTransactions_Card_Holder_Name').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Card_Holder_Name').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Card_Holder_Name').css('display','table-cell');
        document.getElementById('partnerTransactions_Card_Holder_Name_hidden').value="Y";
    }
    if(partnerTransactions_Card_Holder_Name=="")
    {
        $('#partnerTransactions_Card_Holder_Name').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Card_Holder_Name').css('display','none');
        $('#myTable tr > td.partnerTransactions_Card_Holder_Name').css('display','none');
        document.getElementById('partnerTransactions_Card_Holder_Name_hidden').value='';
    }
    if(partnerTransactions_Customer_Email=="Y")
    {
        $('#partnerTransactions_Customer_Email').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_Customer_Email').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_Customer_Email').css('display','table-cell');
        document.getElementById('partnerTransactions_Customer_Email_hidden').value="Y";
    }
    if(partnerTransactions_Customer_Email=="")
    {
        $('#partnerTransactions_Customer_Email').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_Customer_Email').css('display','none');
        $('#myTable tr > td.partnerTransactions_Customer_Email').css('display','none');
        document.getElementById('partnerTransactions_Customer_Email_hidden').value='';
    }
    if(partnerTransactions_LastUpdateDate=="Y")
    {
        $('#partnerTransactions_LastUpdateDate').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_LastUpdateDate').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_LastUpdateDate').css('display','table-cell');
        document.getElementById('partnerTransactions_LastUpdateDate_hidden').value="Y";
    }
    if(partnerTransactions_LastUpdateDate=="")
    {
        $('#partnerTransactions_LastUpdateDate').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_LastUpdateDate').css('display','none');
        $('#myTable tr > td.partnerTransactions_LastUpdateDate').css('display','none');
        document.getElementById('partnerTransactions_LastUpdateDate_hidden').value='';
    }
    if(partnerTransactions_PaymentID=="Y")
    {
        $('#partnerTransactions_PaymentID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_PaymentID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_PaymentID').css('display','table-cell');
        document.getElementById('partnerTransactions_PaymentID_hidden').value="Y";
    }
    if(partnerTransactions_PaymentID=="")
    {
        $('#partnerTransactions_PaymentID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_PaymentID').css('display','none');
        $('#myTable tr > td.partnerTransactions_PaymentID').css('display','none');
        document.getElementById('partnerTransactions_PaymentID_hidden').value='';
    }
    if(partnerTransactions_PartnerID=="Y")
    {
        $('#partnerTransactions_PartnerID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_PartnerID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_PartnerID').css('display','table-cell');
        document.getElementById('partnerTransactions_PartnerID_hidden').value="Y";
    }
    if(partnerTransactions_PartnerID=="")
    {
        $('#Transactions_PartnerID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_PartnerID').css('display','none');
        $('#myTable tr > td.partnerTransactions_PartnerID').css('display','none');
        document.getElementById('partnerTransactions_PartnerID_hidden').value='';
    }
    if(partnerTransactions_MerchantID=="Y")
    {
        $('#partnerTransactions_MerchantID').parent('.icheckbox_square-aero').addClass('checked');
        $('#myTable th.partnerTransactions_MerchantID').css('display','table-cell');
        $('#myTable tr > td.partnerTransactions_MerchantID').css('display','table-cell');
        document.getElementById('partnerTransactions_MerchantID_hidden').value="Y";
    }
    if(partnerTransactions_MerchantID=="")
    {
        $('#partnerTransactions_MerchantID').parent('.icheckbox_square-aero').addClass('');
        $('#myTable th.partnerTransactions_MerchantID').css('display','none');
        $('#myTable tr > td.partnerTransactions_MerchantID').css('display','none');
        document.getElementById('partnerTransactions_MerchantID_hidden').value='';
    }

     if(partnerTransactions_Transaction_Date1 == "" && partnerTransactions_TimeZone == "" &&  partnerTransactions_TrackingID == "" && partnerTransactions_OrderID == "" && partnerTransactions_OrderDescription == ""  && partnerTransactions_Amount == "" && partnerTransactions_RefundAmount == "" && partnerTransactions_Currency == "" && partnerTransactions_Status1 == "" && partnerTransactions_Remark == "" && partnerTransactions_TerminalID == "")
     {
     $('#partnerTransactions_Transaction_Date1').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_Transaction_Date1').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_Transaction_Date1').css('display','table-cell');
     document.getElementById('partnerTransactions_Transaction_Date1_hidden').value="Y";

     $('#partnerTransactions_TimeZone').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_TimeZone').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_TimeZone').css('display','table-cell');
     document.getElementById('partnerTransactions_TimeZone_hidden').value="Y";

     $('#partnerTransactions_TrackingID').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_TrackingID').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_TrackingID').css('display','table-cell');
     document.getElementById('partnerTransactions_TrackingID_hidden').value="Y";

     $('#partnerTransactions_OrderID').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_OrderID').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_OrderID').css('display','table-cell');
     document.getElementById('partnerTransactions_OrderID_hidden').value="Y";

     $('#partnerTransactions_OrderDescription').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_OrderDescription').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_OrderDescription').css('display','table-cell');
     document.getElementById('partnerTransactions_OrderDescription_hidden').value="Y";

     $('#partnerTransactions_Currency').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_Currency').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_Currency').css('display','table-cell');
     document.getElementById('partnerTransactions_Currency_hidden').value="Y";

     $('#partnerTransactions_Amount').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_Amount').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_Amount').css('display','table-cell');
     document.getElementById('partnerTransactions_Amount_hidden').value="Y";

     $('#partnerTransactions_RefundAmount').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_RefundAmount').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_RefundAmount').css('display','table-cell');
     document.getElementById('partnerTransactions_RefundAmount_hidden').value="Y";

     $('#partnerTransactions_Status1').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_Status1').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_Status1').css('display','table-cell');
     document.getElementById('partnerTransactions_Status1_hidden').value="Y";

     $('#partnerTransactions_Remark').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_Remark').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_Remark').css('display','table-cell');
     document.getElementById('partnerTransactions_Remark_hidden').value="Y";

     $('#partnerTransactions_TerminalID').parent('.icheckbox_square-aero').addClass('checked');
     $('#myTable th.partnerTransactions_TerminalID').css('display','table-cell');
     $('#myTable tr > td.partnerTransactions_TerminalID').css('display','table-cell');
     document.getElementById('partnerTransactions_TerminalID_hidden').value="Y";
     }
     else
     {
     //alert("Selected");
     $("#myTable").show();
     $("#showingid").show();
     $("#page_navigate").show();
     $("#bginfo_checkbox").hide();
     $("#grpChkBox").css("position","absolute");
     }

    $("input[name='partnerTransactions_Transaction_Date1']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){

            document.getElementById('partnerTransactions_Transaction_Date1').value="Y";
            document.getElementById('partnerTransactions_Transaction_Date1_hidden').value="Y";
            $('#myTable th.partnerTransactions_Transaction_Date1').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Transaction_Date1').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_Transaction_Date1').value='';
            document.getElementById('partnerTransactions_Transaction_Date1_hidden').value='';
            $('#myTable th.partnerTransactions_Transaction_Date1').css('display','none');
            $('#myTable tr > td.partnerTransactions_Transaction_Date1').css('display','none');
        }
    });

    $("input[name='partnerTransactions_TimeZone']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_TimeZone').value="Y";
            document.getElementById('partnerTransactions_TimeZone_hidden').value="Y";
            $('#myTable th.partnerTransactions_TimeZone').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_TimeZone').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_TimeZone').value='';
            document.getElementById('partnerTransactions_TimeZone_hidden').value='';
            $('#myTable th.partnerTransactions_TimeZone').css('display','none');
            $('#myTable tr > td.partnerTransactions_TimeZone').css('display','none');
        }
    });

    $("input[name='partnerTransactions_TrackingID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_TrackingID').value="Y";
            document.getElementById('partnerTransactions_TrackingID_hidden').value="Y";
            $('#myTable th.partnerTransactions_TrackingID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_TrackingID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_TrackingID').value='';
            document.getElementById('partnerTransactions_TrackingID_hidden').value='';
            $('#myTable th.partnerTransactions_TrackingID').css('display','none');
            $('#myTable tr > td.partnerTransactions_TrackingID').css('display','none');
        }
    });

    $("input[name='partnerTransactions_PaymentMode']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_PaymentMode').value="Y";
            document.getElementById('partnerTransactions_PaymentMode_hidden').value="Y";
            $('#myTable th.partnerTransactions_PaymentMode').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_PaymentMode').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_PaymentMode').value='';
            document.getElementById('partnerTransactions_PaymentMode_hidden').value='';
            $('#myTable th.partnerTransactions_PaymentMode').css('display','none');
            $('#myTable tr > td.partnerTransactions_PaymentMode').css('display','none');
        }
    });

    $("input[name='partnerTransactions_PaymentBrand']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_PaymentBrand').value="Y";
            document.getElementById('partnerTransactions_PaymentBrand_hidden').value="Y";
            $('#myTable th.partnerTransactions_PaymentBrand').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_PaymentBrand').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_PaymentBrand').value='';
            document.getElementById('partnerTransactions_PaymentBrand_hidden').value='';
            $('#myTable th.partnerTransactions_PaymentBrand').css('display','none');
            $('#myTable tr > td.partnerTransactions_PaymentBrand').css('display','none');
        }
    });

    $("input[name='partnerTransactions_Currency']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_Currency').value="Y";
            document.getElementById('partnerTransactions_Currency_hidden').value="Y";
            $('#myTable th.partnerTransactions_Currency').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Currency').css('display','table-cell');
        }

        else{
            document.getElementById('partnerTransactions_Currency').value='';
            document.getElementById('partnerTransactions_Currency_hidden').value='';
            $('#myTable th.partnerTransactions_Currency').css('display','none');
            $('#myTable tr > td.partnerTransactions_Currency').css('display','none');
        }
    });
    $("input[name='partnerTransactions_OrderID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_OrderID').value="Y";
            document.getElementById('partnerTransactions_OrderID_hidden').value="Y";
            $('#myTable th.partnerTransactions_OrderID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_OrderID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_OrderID').value='';
            document.getElementById('partnerTransactions_OrderID_hidden').value='';
            $('#myTable th.partnerTransactions_OrderID').css('display','none');
            $('#myTable tr > td.partnerTransactions_OrderID').css('display','none');
        }
    });
    $("input[name='partnerTransactions_OrderDescription']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_OrderDescription').value="Y";
            document.getElementById('partnerTransactions_OrderDescription_hidden').value="Y";
            $('#myTable th.partnerTransactions_OrderDescription').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_OrderDescription').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_OrderDescription').value='';
            document.getElementById('partnerTransactions_OrderDescription_hidden').value='';
            $('#myTable th.partnerTransactions_OrderDescription').css('display','none');
            $('#myTable tr > td.partnerTransactions_OrderDescription').css('display','none');
        }
    });
    $("input[name='partnerTransactions_Amount']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_Amount').value="Y";
            document.getElementById('partnerTransactions_Amount_hidden').value="Y";
            $('#myTable th.partnerTransactions_Amount').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Amount').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_Amount').value='';
            document.getElementById('partnerTransactions_Amount_hidden').value='';
            $('#myTable th.partnerTransactions_Amount').css('display','none');
            $('#myTable tr > td.partnerTransactions_Amount').css('display','none');
        }
    });
    $("input[name='partnerTransactions_RefundAmount']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_RefundAmount').value="Y";
            document.getElementById('partnerTransactions_RefundAmount_hidden').value="Y";
            $('#myTable th.partnerTransactions_RefundAmount').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_RefundAmount').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_RefundAmount').value='';
            document.getElementById('partnerTransactions_RefundAmount_hidden').value='';
            $('#myTable th.partnerTransactions_RefundAmount').css('display','none');
            $('#myTable tr > td.partnerTransactions_RefundAmount').css('display','none');
        }
    });
    $("input[name='partnerTransactions_Remark']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_Remark').value="Y";
            document.getElementById('partnerTransactions_Remark_hidden').value="Y";
            $('#myTable th.partnerTransactions_Remark').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Remark').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_Remark').value='';
            document.getElementById('partnerTransactions_Remark_hidden').value='';
            $('#myTable th.partnerTransactions_Remark').css('display','none');
            $('#myTable tr > td.partnerTransactions_Remark').css('display','none');
        }
    });
    $("input[name='partnerTransactions_Status1']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_Status1').value="Y";
            document.getElementById('partnerTransactions_Status1_hidden').value="Y";
            $('#myTable th.partnerTransactions_Status1').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Status1').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_Status1').value='';
            document.getElementById('partnerTransactions_Status1_hidden').value='';
            $('#myTable th.partnerTransactions_Status1').css('display','none');
            $('#myTable tr > td.partnerTransactions_Status1').css('display','none');
        }
    });
    $("input[name='partnerTransactions_TerminalID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_TerminalID').value="Y";
            document.getElementById('partnerTransactions_TerminalID_hidden').value="Y";
            $('#myTable th.partnerTransactions_TerminalID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_TerminalID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_TerminalID').value='';
            document.getElementById('partnerTransactions_TerminalID_hidden').value='';
            $('#myTable th.partnerTransactions_TerminalID').css('display','none');
            $('#myTable tr > td.partnerTransactions_TerminalID').css('display','none');
        }
    });
    $("input[name='partnerTransactions_CustomerID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_CustomerID').value="Y";
            document.getElementById('partnerTransactions_CustomerID_hidden').value="Y";
            $('#myTable th.partnerTransactions_CustomerID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_CustomerID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_CustomerID').value='';
            document.getElementById('partnerTransactions_CustomerID_hidden').value='';
            $('#myTable th.partnerTransactions_CustomerID').css('display','none');
            $('#myTable tr > td.partnerTransactions_CustomerID').css('display','none');
        }
    });
    $("input[name='partnerTransactions_LastUpdateDate']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_LastUpdateDate').value="Y";
            document.getElementById('partnerTransactions_LastUpdateDate_hidden').value="Y";
            $('#myTable th.partnerTransactions_LastUpdateDate').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_LastUpdateDate').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_LastUpdateDate').value='';
            document.getElementById('partnerTransactions_LastUpdateDate_hidden').value='';
            $('#myTable th.partnerTransactions_LastUpdateDate').css('display','none');
            $('#myTable tr > td.partnerTransactions_LastUpdateDate').css('display','none');
        }
    });
    $("input[name='transactions_mode']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('transactions_mode').value="Y";
            document.getElementById('transactions_mode_hidden').value="Y";
            $('#myTable th.transactions_mode').css('display','table-cell');
            $('#myTable tr > td.transactions_mode').css('display','table-cell');
        }
        else{
            document.getElementById('transactions_mode').value='';
            document.getElementById('transactions_mode_hidden').value='';
            $('#myTable th.transactions_mode').css('display','none');
            $('#myTable tr > td.transactions_mode').css('display','none');
        }
    });
    $("input[name='partnerTransactions_Customer_Email']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_Customer_Email').value="Y";
            document.getElementById('partnerTransactions_Customer_Email_hidden').value="Y";
            $('#myTable th.partnerTransactions_Customer_Email').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Customer_Email').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_Customer_Email').value='';
            document.getElementById('partnerTransactions_Customer_Email_hidden').value='';
            $('#myTable th.partnerTransactions_Customer_Email').css('display','none');
            $('#myTable tr > td.partnerTransactions_Customer_Email').css('display','none');
        }
    });
    $("input[name='partnerTransactions_Card_Holder_Name']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_Card_Holder_Name').value="Y";
            document.getElementById('partnerTransactions_Card_Holder_Name_hidden').value="Y";
            $('#myTable th.partnerTransactions_Card_Holder_Name').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_Card_Holder_Name').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_Card_Holder_Name').value='';
            document.getElementById('partnerTransactions_Card_Holder_Name_hidden').value='';
            $('#myTable th.partnerTransactions_Card_Holder_Name').css('display','none');
            $('#myTable tr > td.partnerTransactions_Card_Holder_Name').css('display','none');
        }
    });
    $("input[name='partnerTransactions_PaymentID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_PaymentID').value="Y";
            document.getElementById('partnerTransactions_PaymentID_hidden').value="Y";
            $('#myTable th.partnerTransactions_PaymentID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_PaymentID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_PaymentID').value='';
            document.getElementById('partnerTransactions_PaymentID_hidden').value='';
            $('#myTable th.partnerTransactions_PaymentID').css('display','none');
            $('#myTable tr > td.partnerTransactions_PaymentID').css('display','none');
        }
    });
    $("input[name='partnerTransactions_PartnerID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_PartnerID').value="Y";
            document.getElementById('partnerTransactions_PartnerID_hidden').value="Y";
            $('#myTable th.partnerTransactions_PartnerID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_PartnerID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_PartnerID').value='';
            document.getElementById('partnerTransactions_PartnerID_hidden').value='';
            $('#myTable th.partnerTransactions_PartnerID').css('display','none');
            $('#myTable tr > td.partnerTransactions_PartnerID').css('display','none');
        }
    });
    $("input[name='partnerTransactions_MerchantID']").next('.iCheck-helper').on( "click", function() {

        if($(this).parent().hasClass('checked')){
            document.getElementById('partnerTransactions_MerchantID').value="Y";
            document.getElementById('partnerTransactions_MerchantID_hidden').value="Y";
            $('#myTable th.partnerTransactions_MerchantID').css('display','table-cell');
            $('#myTable tr > td.partnerTransactions_MerchantID').css('display','table-cell');
        }
        else{
            document.getElementById('partnerTransactions_MerchantID').value='';
            document.getElementById('partnerTransactions_MerchantID_hidden').value='';
            $('#myTable th.partnerTransactions_MerchantID').css('display','none');
            $('#myTable tr > td.partnerTransactions_MerchantID').css('display','none');
        }
    });
});