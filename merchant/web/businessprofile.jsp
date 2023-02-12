<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.BusinessProfileVO" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/12/15
  Time: 7:48 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">


<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

<script src="/merchant/NewCss/am_multipleselection/jquery-multipleselection.js"></script>
<script src="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
<link href="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />

<script src="/merchant/stylenew01/businessprofile.js"></script>
<script>

    $(document).ready(function(){

        var w = $(window).width();

        //alert(w);

        if(w > 990){
            //alert("It's greater than 990px");
            $("body").removeClass("smallscreen").addClass("widescreen");
            $("#wrapper").removeClass("enlarged");
        }
        else{
            //alert("It's less than 990px");
            $("body").removeClass("widescreen").addClass("smallscreen");
            $("#wrapper").addClass("enlarged");
            $(".left ul").removeAttr("style");
        }
    });

</script>

<script>
    $(function () {
        $('#lstStates').multiselect({
            buttonText: function(options, select) {
                if (options.length === 0) {
                    return 'Select MCC Code...';
                }
                /*if (options.length === select[0].length) {
                 return 'All selected ('+select[0].length+')';
                 }
                 else if (options.length >= 4) {
                 return options.length + ' selected';
                 }*/
                else {
                    var labels = [];
                    console.log(options);
                    options.each(function() {
                        labels.push($(this).val());
                    });
                    document.getElementById('merchantcode').value = labels;


                    return labels.join(', ') + '';
                }
            }
        });

        $('#productsoldcurrency').multiselect({
            buttonText: function(options, select) {
                if (options.length === 0) {
                    return 'Select Currency';
                }
               /* if (options.length === select[0].length) {
                    return 'All selected ('+select[0].length+')';
                }
                else if (options.length >= 4) {
                    return options.length + ' selected';
                }*/
                else {
                    var labels = [];
                    console.log(options);
                    options.each(function() {
                        labels.push($(this).val());
                    });
                    document.getElementById('product_sold_currencies').value = labels;
                    return labels.join(', ') + '';
                }
            }
        });
    });
</script>
<script>
    $(function()
    {

        function myBusiness(selected, other1, other2)
        {
            if (this.document.forms["myformname"][selected].value >= 100)
            {
                if (this.document.forms["myformname"][other1].value == "")
                    this.document.forms["myformname"][other1].disabled = true;
                if (this.document.forms["myformname"][other2].value == "")
                    this.document.forms["myformname"][other2].disabled = true;
            }
            else
            {
                this.document.forms["myformname"][selected].disabled = false;
                this.document.forms["myformname"][other1].disabled = false;
                this.document.forms["myformname"][other2].disabled = false;
            }

            if (Number(this.document.forms["myformname"][selected].value) + Number(this.document.forms["myformname"][other1].value) + Number(this.document.forms["myformname"][other2].value) >= 100)
            {
                if (this.document.forms["myformname"][selected].value == "")
                    this.document.forms["myformname"][selected].disabled = true;
                if (this.document.forms["myformname"][other1].value == "")
                    this.document.forms["myformname"][other1].disabled = true;
                if (this.document.forms["myformname"][other2].value == "")
                    this.document.forms["myformname"][other2].disabled = true;
            }

        }

        function myForeignTransaction(selected, other1, other2, other3, other4, other5)
        {
            if (this.document.forms["myformname"][selected].value >= 100)
            {
                if (this.document.forms["myformname"][other1].value == "")
                    this.document.forms["myformname"][other1].disabled = true;
                if (this.document.forms["myformname"][other2].value == "")
                    this.document.forms["myformname"][other2].disabled = true;
                if (this.document.forms["myformname"][other3].value == "")
                    this.document.forms["myformname"][other3].disabled = true;
                if (this.document.forms["myformname"][other4].value == "")
                    this.document.forms["myformname"][other4].disabled = true;
                if (this.document.forms["myformname"][other5].value == "")
                    this.document.forms["myformname"][other5].disabled = true;
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

            if (Number(this.document.forms["myformname"][selected].value) + Number(this.document.forms["myformname"][other1].value) + Number(this.document.forms["myformname"][other2].value) + Number(this.document.forms["myformname"][other3].value) + Number(this.document.forms["myformname"][other4].value) + Number(this.document.forms["myformname"][other5].value) >= 100)
            {
                if (this.document.forms["myformname"][selected].value == "")
                    this.document.forms["myformname"][selected].disabled = true;
                if (this.document.forms["myformname"][other1].value == "")
                    this.document.forms["myformname"][other1].disabled = true;
                if (this.document.forms["myformname"][other2].value == "")
                    this.document.forms["myformname"][other2].disabled = true;
                if (this.document.forms["myformname"][other3].value == "")
                    this.document.forms["myformname"][other3].disabled = true;
                if (this.document.forms["myformname"][other4].value == "")
                    this.document.forms["myformname"][other4].disabled = true;
                if (this.document.forms["myformname"][other5].value == "")
                    this.document.forms["myformname"][other5].disabled = true;
            }

        }

        function myBusinessCheckBox(selected, other)
        {
            if (selected != "cardtypesaccepted_other")
            {
                this.document.forms["myformname"][other].checked = false;
                this.document.forms["myformname"]['cardtypesaccepted_other_yes'].disabled = true;
            }
            else
            {
                this.document.forms["myformname"]['cardtypesaccepted_visa'].checked = false;
                this.document.forms["myformname"]['cardtypesaccepted_mastercard'].checked = false;
                this.document.forms["myformname"]['cardtypesaccepted_americanexpress'].checked = false;
                this.document.forms["myformname"]['cardtypesaccepted_discover'].checked = false;

                this.document.forms["myformname"]['cardtypesaccepted_diners'].checked = false;
                this.document.forms["myformname"]['cardtypesaccepted_jcb'].checked = false;
            }
        }

        function myIncomeCompanyCheckBox(selected, other)
        {
            if (selected != "income_sources_other")
            {
                this.document.forms["myformname"][other].checked = false;
                this.document.forms["myformname"]['income_sources_other_yes'].disabled = true;
            }
            else
            {
                this.document.forms["myformname"]['loans'].checked = false;
                this.document.forms["myformname"]['income_economic_activity'].checked = false;
                this.document.forms["myformname"]['interest_income'].checked = false;
                this.document.forms["myformname"]['investments'].checked = false;
            }
        }
    });

</script>
<style>
    .multiselect-container>li {
        padding: 0;
        margin-left: 31px;
    }
    .open>#multiselect-id.dropdown-menu {
        display: block;
        overflow-y: scroll;
        height: 500%;
        /*width: 100%;*/
    }
    .multiselect-container>li>a>label {
        margin: 0;
        height: 24px;
        font-weight: 400;
        padding: 3px 20px 3px 40px;
    }
    span.multiselect-native-select {
        position: relative;
    }

    @supports (-ms-ime-align:auto) {

        span.multiselect-native-select {
            position: static!important;
            /*position: absolute!important;
            width: 100%!important;
            bottom: -35px!important;*/
        }
    }

    span.multiselect-native-select select {
        border: 0!important;
        clip: rect(0 0 0 0)!important;
        height: 1px!important;
        margin: -1px -1px -1px -3px!important;
        overflow: hidden!important;
        padding: 0!important;
        position: absolute!important;
        width: 1px!important;
        left: 50%;
        top: 30px;
    }
    select[multiple], select[size] {
        height: auto;
    }
    .widget .btn-group {
        z-index: 1;
    }

    .btn-group, .btn-group-vertical {
        position: relative;
        display: flex;
        vertical-align: middle;

    }
    .btn {

        display: inline-block;
        padding: 6px 12px;
        margin-bottom: 0;
        font-size: 14px;
        font-weight: normal;
        line-height: 1.428571429;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        cursor: pointer;
        background-image: none;
        border: 1px solid transparent;
        /*border-radius: 4px;*/
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        -o-user-select: none;
        user-select: none;
    }
    #mainbtn-id.btn-default {
        color: #333;
        background-color: #fff;
        border-color: #ccc;
    }
    .btn-group>.btn:first-child {
        margin-left: 0;
    }

    .btn-group>.btn:first-child {
        margin-left: 0;
    }

    .btn-group>.btn, .btn-group-vertical>.btn {
        position: relative;
        float: left;
    }
    .multiselect-container {
        position: absolute;
        list-style-type: none;
        margin: 0;
        padding: 0;
    }
    #multiselect-id.dropdown-menu {
        position: absolute;
        top: 100%;
        left: 0;
        z-index: 1000;
        display: none;
        float: left;
        min-width: 160px;
        padding: 5px 0;
        margin: 2px 0 0;
        font-size: 14px;
        list-style: none;
        background-color: #fff;
        border: 1px solid #ccc;
        border: 1px solid rgba(0,0,0,0.15);
        /*border-radius: 4px;*/
        -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
        box-shadow: 0 6px 12px rgba(0,0,0,0.175);
        background-clip: padding-box;
    }
    #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
        /*color: #333;*/
        color: #fff;
        background-color: white;
        border-color: #b2b2b2;
        text-align: left;
        width: 100%;
    }
    .caret {
        display: inline-block;
        width: 0;
        height: 0;
        margin-left: 2px;
        vertical-align: middle;
        border-top: 4px solid;
        border-right: 4px solid transparent;
        border-left: 4px solid transparent;
        float: right;
        margin-top: 8px;
    }

</style>
<style type="text/css">


    .glyphicon{
        display: block!important;
        color:#a94442!important;
        background-color: #ebccd1!important;
        width: 40px!important;
        margin-right: 16px!important;
        height: 32px!important;
        /*margin-top: -1px!important;*/
        top: inherit!important;
        margin-top: -33px!important;
    }

    .bg-infoorange {
        padding: 15px;
        margin-bottom: 20px;
        border: 1px solid transparent;
        border-radius: 4px;
        color: #474a54;
        background-color: #fdf7f7;
        border-left: 4px solid #f00;
        font-family: "Open Sans";
        font-size: 13px;
        font-weight: 600;
        text-align: center;
    }


    /***********************Dropdown For Currency****************************/

    .dropdown dd,
    .dropdown dt, .dropdown2 dd,
    .dropdown2 dt,  {
        margin: 0px;
        padding: 0px;
    }

    .dropdown ul, .dropdown2 ul {
        margin: -1px 0 0 0;
    }

    .dropdown li, .dropdown2 li {
        margin: 10px;
    }

    .dropdown dd, .dropdown2 dd {
        position: relative;
    }

    .dropdown a,
    .dropdown a:visited {
        color: #fff;
        text-decoration: none;
        outline: none;
        font-size: 12px;
    }

    .dropdown2 a,
    .dropdown2 a:visited {
        color: #fff;
        text-decoration: none;
        outline: none;
        font-size: 12px;
    }

    .dropdown dt a, .dropdown2 dt a {
        background-color: #ffffff;
        display: block;
    }

    .dropdown dt a span,
    .multiSel span, .dropdown2 dt a span,
    .multiSel2 span {
        cursor: pointer;
        display: inline-block;
        color:#555;
    }

    .dropdown dd ul, .dropdown2 dd ul {
        background-color: #ffffff;
        border: 0;
        color: #555;
        display: none;
        left: 0px;
        padding: 2px 15px 2px 5px;
        position: absolute;
        top: 2px;
        width: 100%;
        list-style: none;
        height: 100px;
        overflow: auto;
        font-size: 13px;
    }

    .dropdown span.value {
        display: none;
    }

    .dropdown2 span.value {
        display: none;
    }

    .dropdown dd ul li a, .dropdown2 dd ul li a {
        padding: 5px;
        display: block;
    }

    .dropdown dd ul li a:hover {
        background-color: #fff;
    }

    .dropdown2 dd ul li a:hover {
        background-color: #fff;
    }


    @media(max-width: 991px){
        #margintop10{
            margin-top: 10px;
        }
    }

    #align_check > .icheckbox_square-aero{
        display: table;
    }


    input[type=radio], input[type=checkbox] {
        -ms-transform: scale(1.6); /* IE */
        -moz-transform: scale(1.6); /* FF */
        -webkit-transform: scale(1.6); /* Safari and Chrome */
        -o-transform: scale(1.6); /* Opera */
        padding: 10px;
    }


    #businessid .checkbox-inline input[type=checkbox] {
        position: inherit!important;
        margin-left: inherit!important;
    }

    /*orderconfirmation_other_yes ID Css*/

    /*#orderconfirmation_other_yes{
        width: 60%;
    }*/

    @media(min-width: 992px){
        #orderconfirmation_div{
            width: 12%;
        }
    }

    @media(max-width: 991px){
        #orderconfirmation_other_yes{
            margin-left: inherit;
            /*margin-top: 10px;*/
            width: 100%;
        }
    }

    /****************For checkbox overflow********************/
    .multiselect-selected-text {
        overflow: hidden;
        display: block;
    }


</style>

<%!
    private Functions functions = new Functions();
    private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
%>
<%
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String businessprofile_mcc = StringUtils.isNotEmpty(rb1.getString("businessprofile_mcc"))?rb1.getString("businessprofile_mcc"): "Which MCC categories do your products and services belong to?";
    String businessprofile_please = StringUtils.isNotEmpty(rb1.getString("businessprofile_please"))?rb1.getString("businessprofile_please"): "Please save Business Profile after entering the data provided";
    String businessprofile_ticket = StringUtils.isNotEmpty(rb1.getString("businessprofile_ticket"))?rb1.getString("businessprofile_ticket"): "Ticket Size of the Product";
    String businessprofile_lowest = StringUtils.isNotEmpty(rb1.getString("businessprofile_lowest"))?rb1.getString("businessprofile_lowest"): "Lowest";
    String businessprofile_ticket3 = StringUtils.isNotEmpty(rb1.getString("businessprofile_ticket3"))?rb1.getString("businessprofile_ticket3"): "ticket*";
    String businessprofile_average = StringUtils.isNotEmpty(rb1.getString("businessprofile_average"))?rb1.getString("businessprofile_average"): "Average";
    String businessprofile_ticket1 = StringUtils.isNotEmpty(rb1.getString("businessprofile_ticket1"))?rb1.getString("businessprofile_ticket1"): "ticket*";
    String businessprofile_highest = StringUtils.isNotEmpty(rb1.getString("businessprofile_highest"))?rb1.getString("businessprofile_highest"): "Highest";
    String businessprofile_ticket2 = StringUtils.isNotEmpty(rb1.getString("businessprofile_ticket2"))?rb1.getString("businessprofile_ticket2"): "ticket*";
    String businessprofile_Billing = StringUtils.isNotEmpty(rb1.getString("businessprofile_Billing"))?rb1.getString("businessprofile_Billing"): "Billing Type";
    String businessprofile_recurring = StringUtils.isNotEmpty(rb1.getString("businessprofile_recurring"))?rb1.getString("businessprofile_recurring"): "Recurring Services?*";
    String businessprofile_Yes1 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes1"))?rb1.getString("businessprofile_Yes1"): "Yes";
    String businessprofile_No1 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No1"))?rb1.getString("businessprofile_No1"): "No";
    String businessprofile_if = StringUtils.isNotEmpty(rb1.getString("businessprofile_if"))?rb1.getString("businessprofile_if"): "If yes, please provide more details :";
    String businessprofile_type = StringUtils.isNotEmpty(rb1.getString("businessprofile_type"))?rb1.getString("businessprofile_type"): "Billing Type?";
    String businessprofile_Recurring = StringUtils.isNotEmpty(rb1.getString("businessprofile_Recurring"))?rb1.getString("businessprofile_Recurring"): "Recurring";
    String businessprofile_One = StringUtils.isNotEmpty(rb1.getString("businessprofile_One"))?rb1.getString("businessprofile_One"): "One Time";
    String businessprofile_pay = StringUtils.isNotEmpty(rb1.getString("businessprofile_pay"))?rb1.getString("businessprofile_pay"): "Pay Per Minute";
    String businessprofile_download = StringUtils.isNotEmpty(rb1.getString("businessprofile_download"))?rb1.getString("businessprofile_download"): "Pay Per Download";
    String businessprofile_Other = StringUtils.isNotEmpty(rb1.getString("businessprofile_Other"))?rb1.getString("businessprofile_Other"): "Other";
    String businessprofile_billing = StringUtils.isNotEmpty(rb1.getString("businessprofile_billing"))?rb1.getString("businessprofile_billing"): "Billing Frequency?";
    String businessprofile_Daily = StringUtils.isNotEmpty(rb1.getString("businessprofile_Daily"))?rb1.getString("businessprofile_Daily"): "Daily";
    String businessprofile_Weekly = StringUtils.isNotEmpty(rb1.getString("businessprofile_Weekly"))?rb1.getString("businessprofile_Weekly"): "Weekly";
    String businessprofile_Monthly = StringUtils.isNotEmpty(rb1.getString("businessprofile_Monthly"))?rb1.getString("businessprofile_Monthly"): "Monthly";
    String businessprofile_Quarterly = StringUtils.isNotEmpty(rb1.getString("businessprofile_Quarterly"))?rb1.getString("businessprofile_Quarterly"): "Quarterly";
    String businessprofile_Annualy = StringUtils.isNotEmpty(rb1.getString("businessprofile_Annualy"))?rb1.getString("businessprofile_Annualy"): "Annualy";
    String businessprofile_Recurring_amount = StringUtils.isNotEmpty(rb1.getString("businessprofile_Recurring_amount"))?rb1.getString("businessprofile_Recurring_amount"): "Recurring amount";
    String businessprofile_require = StringUtils.isNotEmpty(rb1.getString("businessprofile_require"))?rb1.getString("businessprofile_require"): "Do you require confirmation of payment details after 6 months of automatic recurring payment?";
    String businessprofile_Yes2 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes2"))?rb1.getString("businessprofile_Yes2"): "Yes";
    String businessprofile_No2 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No2"))?rb1.getString("businessprofile_No2"): "No";
    String businessprofile_Seasonal = StringUtils.isNotEmpty(rb1.getString("businessprofile_Seasonal"))?rb1.getString("businessprofile_Seasonal"): "Seasonal / Fluctuating ?";
    String businessprofile_Yes3 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes3"))?rb1.getString("businessprofile_Yes3"): "Yes";
    String businessprofile_No3 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No3"))?rb1.getString("businessprofile_No3"): "No";
    String businessprofile_please1 = StringUtils.isNotEmpty(rb1.getString("businessprofile_please1"))?rb1.getString("businessprofile_please1"): "If yes, please provide more details :";
    String businessprofile_percentage = StringUtils.isNotEmpty(rb1.getString("businessprofile_percentage"))?rb1.getString("businessprofile_percentage"): "Percentage Of Foreign Transactions";
    String businessprofile_US = StringUtils.isNotEmpty(rb1.getString("businessprofile_US"))?rb1.getString("businessprofile_US"): "US";
    String businessprofile_Europe = StringUtils.isNotEmpty(rb1.getString("businessprofile_Europe"))?rb1.getString("businessprofile_Europe"): "Europe";
    String businessprofile_Asia = StringUtils.isNotEmpty(rb1.getString("businessprofile_Asia"))?rb1.getString("businessprofile_Asia"): "Asia";
    String businessprofile_cis = StringUtils.isNotEmpty(rb1.getString("businessprofile_cis"))?rb1.getString("businessprofile_cis"): "CIS";
    String businessprofile_canada = StringUtils.isNotEmpty(rb1.getString("businessprofile_canada"))?rb1.getString("businessprofile_canada"): "Canada";
    String businessprofile_uk = StringUtils.isNotEmpty(rb1.getString("businessprofile_uk"))?rb1.getString("businessprofile_uk"): "UK";
    String businessprofile_row = StringUtils.isNotEmpty(rb1.getString("businessprofile_row"))?rb1.getString("businessprofile_row"): "ROW";
    String businessprofile_WebsiteInformation = StringUtils.isNotEmpty(rb1.getString("businessprofile_WebsiteInformation"))?rb1.getString("businessprofile_WebsiteInformation"): "Website Information";
    String businessprofile_Website = StringUtils.isNotEmpty(rb1.getString("businessprofile_Website"))?rb1.getString("businessprofile_Website"): "Website Details";
    String businessprofile_url = StringUtils.isNotEmpty(rb1.getString("businessprofile_url"))?rb1.getString("businessprofile_url"): "Website URL(s)*";
    String businessprofile_descriptor = StringUtils.isNotEmpty(rb1.getString("businessprofile_descriptor"))?rb1.getString("businessprofile_descriptor"): "Descriptor(Doing Business As)";
    String businessprofile_ip = StringUtils.isNotEmpty(rb1.getString("businessprofile_ip"))?rb1.getString("businessprofile_ip"): "IP Address*";
    String businessprofile_login = StringUtils.isNotEmpty(rb1.getString("businessprofile_login"))?rb1.getString("businessprofile_login"): "Login ID";
    String businessprofile_password = StringUtils.isNotEmpty(rb1.getString("businessprofile_password"))?rb1.getString("businessprofile_password"): "Password";
    String businessprofile_Website1 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Website1"))?rb1.getString("businessprofile_Website1"): "Website Compliance";
    String businessprofile_Website_Review = StringUtils.isNotEmpty(rb1.getString("businessprofile_Website_Review"))?rb1.getString("businessprofile_Website_Review"): "Website Review";
    String businessprofile_price = StringUtils.isNotEmpty(rb1.getString("businessprofile_price"))?rb1.getString("businessprofile_price"): "Is the price and your fees or commission charged clearly displayed (separate from the price)?";
    String businessprofile_Yes4 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes4"))?rb1.getString("businessprofile_Yes4"): "Yes";
    String businessprofile_No4 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No4"))?rb1.getString("businessprofile_No4"): "No";
    String businessprofile_company = StringUtils.isNotEmpty(rb1.getString("businessprofile_company"))?rb1.getString("businessprofile_company"): "Is the company identifiable at all time to the card holder ?";
    String businessprofile_Yes5 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes5"))?rb1.getString("businessprofile_Yes5"): "Yes";
    String businessprofile_No5 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No5"))?rb1.getString("businessprofile_No5"): "No";
    String businessprofile_address = StringUtils.isNotEmpty(rb1.getString("businessprofile_address"))?rb1.getString("businessprofile_address"): "Are your company address and contact details clearly presented?";
    String businessprofile_Yes6 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes6"))?rb1.getString("businessprofile_Yes6"): "Yes";
    String businessprofile_No6 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No6"))?rb1.getString("businessprofile_No6"): "No";
    String businessprofile_tracking = StringUtils.isNotEmpty(rb1.getString("businessprofile_tracking"))?rb1.getString("businessprofile_tracking"): "Do you provide tracking number of goods being sent?";
    String businessprofile_Yes7 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes7"))?rb1.getString("businessprofile_Yes7"): "Yes";
    String businessprofile_No7 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No7"))?rb1.getString("businessprofile_No7"): "No";
    String businessprofile_domain = StringUtils.isNotEmpty(rb1.getString("businessprofile_domain"))?rb1.getString("businessprofile_domain"): "Are the domains owned by the company?";
    String businessprofile_Yes8 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes8"))?rb1.getString("businessprofile_Yes8"): "Yes";
    String businessprofile_No8 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No8"))?rb1.getString("businessprofile_No8"): "No";
    String businessprofile_details = StringUtils.isNotEmpty(rb1.getString("businessprofile_details"))?rb1.getString("businessprofile_details"): "If yes, please provide more details :";
    String businessprofile_currency = StringUtils.isNotEmpty(rb1.getString("businessprofile_currency"))?rb1.getString("businessprofile_currency"): "Is the transaction currency clearly presented to the cardholder?";
    String businessprofile_Yes9 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes9"))?rb1.getString("businessprofile_Yes9"): "Yes";
    String businessprofile_No9 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No9"))?rb1.getString("businessprofile_No9"): "No";
    String businessprofile_dynamic = StringUtils.isNotEmpty(rb1.getString("businessprofile_dynamic"))?rb1.getString("businessprofile_dynamic"): "Are you using dynamic descriptors?";
    String businessprofile_Yes10 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes10"))?rb1.getString("businessprofile_Yes10"): "Yes";
    String businessprofile_No10 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No10"))?rb1.getString("businessprofile_No10"): "No";
    String businessprofile_Purchase_Process = StringUtils.isNotEmpty(rb1.getString("businessprofile_Purchase_Process"))?rb1.getString("businessprofile_Purchase_Process"): "Purchase Process";
    String businessprofile_shopping = StringUtils.isNotEmpty(rb1.getString("businessprofile_shopping"))?rb1.getString("businessprofile_shopping"): "Do you have shopping cart?";
    String businessprofile_Yes11 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes11"))?rb1.getString("businessprofile_Yes11"): "Yes";
    String businessprofile_No11 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No11"))?rb1.getString("businessprofile_No11"): "No";
    String businessprofile_provide = StringUtils.isNotEmpty(rb1.getString("businessprofile_provide"))?rb1.getString("businessprofile_provide"): "If yes, please provide more details* :";
    String businessprofile_secure = StringUtils.isNotEmpty(rb1.getString("businessprofile_secure"))?rb1.getString("businessprofile_secure"): "Is 3D Secure  (Visa's VBV/MasterCard secured code ) Compulsory?*";
    String businessprofile_Yes12 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes12"))?rb1.getString("businessprofile_Yes12"): "Yes";
    String businessprofile_No12 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No12"))?rb1.getString("businessprofile_No12"): "No";
    String businessprofile_cardholder = StringUtils.isNotEmpty(rb1.getString("businessprofile_cardholder"))?rb1.getString("businessprofile_cardholder"): "Is the cardholder asked for his address, telephone number and email address?";
    String businessprofile_Yes13 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes13"))?rb1.getString("businessprofile_Yes13"): "Yes";
    String businessprofile_No13 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No13"))?rb1.getString("businessprofile_No13"): "No";
    String businessprofile_verify = StringUtils.isNotEmpty(rb1.getString("businessprofile_verify"))?rb1.getString("businessprofile_verify"): "How do you verify customers identification?";
    String businessprofile_Yes14 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes14"))?rb1.getString("businessprofile_Yes14"): "Yes";
    String businessprofile_No14 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No14"))?rb1.getString("businessprofile_No14"): "Yes";
    String businessprofile_please2 = StringUtils.isNotEmpty(rb1.getString("businessprofile_please2"))?rb1.getString("businessprofile_please2"): "If yes, please provide more details :";
    String businessprofile_kyc = StringUtils.isNotEmpty(rb1.getString("businessprofile_kyc"))?rb1.getString("businessprofile_kyc"): "Are there KYC processes in place to verify customer identities?";
    String businessprofile_Yes15 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes15"))?rb1.getString("businessprofile_Yes15"): "Yes";
    String businessprofile_No15 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No15"))?rb1.getString("businessprofile_No15"): "No";
    String businessprofile_ssl = StringUtils.isNotEmpty(rb1.getString("businessprofile_ssl"))?rb1.getString("businessprofile_ssl"): "Are your payment pages *SSL and HTTPS secured?";
    String businessprofile_Yes16 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes16"))?rb1.getString("businessprofile_Yes16"): "Yes";
    String businessprofile_No16 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No16"))?rb1.getString("businessprofile_No16"): "No";
    String businessprofile_product = StringUtils.isNotEmpty(rb1.getString("businessprofile_product"))?rb1.getString("businessprofile_product"): "Does your product requires age check?";
    String businessprofile_Yes17 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes17"))?rb1.getString("businessprofile_Yes17"): "Yes";
    String businessprofile_No17 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No17"))?rb1.getString("businessprofile_No17"): "No";
    String businessprofile_Affiliate = StringUtils.isNotEmpty(rb1.getString("businessprofile_Affiliate"))?rb1.getString("businessprofile_Affiliate"): "Affiliate Programs";
    String businessprofile_operate = StringUtils.isNotEmpty(rb1.getString("businessprofile_operate"))?rb1.getString("businessprofile_operate"): "Do you operate affiliate programs?*";
    String businessprofile_Yes18 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes18"))?rb1.getString("businessprofile_Yes18"): "Yes";
    String businessprofile_No18 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No18"))?rb1.getString("businessprofile_No18"): "No";
    String businessprofile_detail = StringUtils.isNotEmpty(rb1.getString("businessprofile_detail"))?rb1.getString("businessprofile_detail"): "If yes, please provide more details :";
    String businessprofile_Content = StringUtils.isNotEmpty(rb1.getString("businessprofile_Content"))?rb1.getString("businessprofile_Content"): "Content";
    String businessprofile_copyrighted = StringUtils.isNotEmpty(rb1.getString("businessprofile_copyrighted"))?rb1.getString("businessprofile_copyrighted"): "Is the content on website copyrighted?";
    String businessprofile_Yes19 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes19"))?rb1.getString("businessprofile_Yes19"): "Yes";
    String businessprofile_No19 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No19"))?rb1.getString("businessprofile_No19"): "No";
    String businessprofile_source = StringUtils.isNotEmpty(rb1.getString("businessprofile_source"))?rb1.getString("businessprofile_source"): "What is the source of content?";
    String businessprofile_Security = StringUtils.isNotEmpty(rb1.getString("businessprofile_Security"))?rb1.getString("businessprofile_Security"): "Security Policy";
    String businessprofile_cookies = StringUtils.isNotEmpty(rb1.getString("businessprofile_cookies"))?rb1.getString("businessprofile_cookies"): "Is your website clearly show that it is using *Cookies?";
    String businessprofile_Yes20 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes20"))?rb1.getString("businessprofile_Yes20"): "Yes";
    String businessprofile_No20 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No20"))?rb1.getString("businessprofile_No20"): "No";
    String businessprofile_customers = StringUtils.isNotEmpty(rb1.getString("businessprofile_customers"))?rb1.getString("businessprofile_customers"): "Can your customers easily read your Data Security policy & Privacy Policy on your website?";
    String businessprofile_Yes21 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes21"))?rb1.getString("businessprofile_Yes21"): "Yes";
    String businessprofile_No21 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No21"))?rb1.getString("businessprofile_No21"): "No";
    String businessprofile_responsibilities = StringUtils.isNotEmpty(rb1.getString("businessprofile_responsibilities"))?rb1.getString("businessprofile_responsibilities"): "Is the cardholder informed on his reponsibilities under the applicable jurisdictions?";
    String businessprofile_Yes22 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes22"))?rb1.getString("businessprofile_Yes22"): "Yes";
    String businessprofile_No22 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No22"))?rb1.getString("businessprofile_No22"): "No";
    String businessprofile_privacy = StringUtils.isNotEmpty(rb1.getString("businessprofile_privacy"))?rb1.getString("businessprofile_privacy"): "Any appropriate privacy/ anonymity/ data protection policy for customers?";
    String businessprofile_Yes23 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes23"))?rb1.getString("businessprofile_Yes23"): "Yes";
    String businessprofile_No23 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No23"))?rb1.getString("businessprofile_No23"): "No";
    String businessprofile_application = StringUtils.isNotEmpty(rb1.getString("businessprofile_application"))?rb1.getString("businessprofile_application"): "Do you provide Application Services (do customer need to register on website) ?";
    String businessprofile_Yes24 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes24"))?rb1.getString("businessprofile_Yes24"): "Yes";
    String businessprofile_No24 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No24"))?rb1.getString("businessprofile_No24"): "No";
    String businessprofile_risk = StringUtils.isNotEmpty(rb1.getString("businessprofile_risk"))?rb1.getString("businessprofile_risk"): "Do you handle fraud/Risk matters?";
    String businessprofile_Yes25 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes25"))?rb1.getString("businessprofile_Yes25"): "Yes";
    String businessprofile_No25 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No25"))?rb1.getString("businessprofile_No25"): "No";
    String businessprofile_please3 = StringUtils.isNotEmpty(rb1.getString("businessprofile_please3"))?rb1.getString("businessprofile_please3"): "If yes, please provide more details* :";
    String businessprofile_warehouse = StringUtils.isNotEmpty(rb1.getString("businessprofile_warehouse"))?rb1.getString("businessprofile_warehouse"): "Warehouse & Product Shipment Details :";
    String businessprofile_Shipping = StringUtils.isNotEmpty(rb1.getString("businessprofile_Shipping"))?rb1.getString("businessprofile_Shipping"): "Shipping";
    String businessprofile_policies = StringUtils.isNotEmpty(rb1.getString("businessprofile_policies"))?rb1.getString("businessprofile_policies"): "Are your shipping and delivery policies clearly visible to the customers on your website?";
    String businessprofile_Yes26 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes26"))?rb1.getString("businessprofile_Yes26"): "Yes";
    String businessprofile_No26 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No26"))?rb1.getString("businessprofile_No26"): "No";
    String businessprofile_please4 = StringUtils.isNotEmpty(rb1.getString("businessprofile_please4"))?rb1.getString("businessprofile_please4"): "If yes, please provide more details :";
    String businessprofile_timeframe = StringUtils.isNotEmpty(rb1.getString("businessprofile_timeframe"))?rb1.getString("businessprofile_timeframe"): "Is the cardholder informed about the delivery timeframe? :";
    String businessprofile_Yes27 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes27"))?rb1.getString("businessprofile_Yes27"): "Yes";
    String businessprofile_No27 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No27"))?rb1.getString("businessprofile_No27"): "No";
    String businessprofile_exclusive = StringUtils.isNotEmpty(rb1.getString("businessprofile_exclusive"))?rb1.getString("businessprofile_exclusive"): "Do you have shipping exclusion?*";
    String businessprofile_Yes28 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes28"))?rb1.getString("businessprofile_Yes28"): "Yes";
    String businessprofile_No28 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No28"))?rb1.getString("businessprofile_No28"): "No";
    String businessprofile_provide2 = StringUtils.isNotEmpty(rb1.getString("businessprofile_provide2"))?rb1.getString("businessprofile_provide2"): "If yes, please provide more details :";
    String businessprofile_Contact_person = StringUtils.isNotEmpty(rb1.getString("businessprofile_Contact_person"))?rb1.getString("businessprofile_Contact_person"): "Contact person";
    String businessprofile_ContactEmail = StringUtils.isNotEmpty(rb1.getString("businessprofile_ContactEmail"))?rb1.getString("businessprofile_ContactEmail"): "Contact Email";
    String businessprofile_location = StringUtils.isNotEmpty(rb1.getString("businessprofile_location"))?rb1.getString("businessprofile_location"): "If inhouse ,where is the warehouse location";
    String businessprofile_house = StringUtils.isNotEmpty(rb1.getString("businessprofile_house"))?rb1.getString("businessprofile_house"): "If other please specify the ware house location";
    String businessprofile_suppliers = StringUtils.isNotEmpty(rb1.getString("businessprofile_suppliers"))?rb1.getString("businessprofile_suppliers"): "Main suppliers come from";
    String businessprofile_shippment = StringUtils.isNotEmpty(rb1.getString("businessprofile_shippment"))?rb1.getString("businessprofile_shippment"): "Is shippment assured?";
    String businessprofile_Yes29 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes29"))?rb1.getString("businessprofile_Yes29"): "Yes";
    String businessprofile_No29 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No29"))?rb1.getString("businessprofile_No29"): "No";
    String businessprofile_fulfillment = StringUtils.isNotEmpty(rb1.getString("businessprofile_fulfillment"))?rb1.getString("businessprofile_fulfillment"): "Is a Fulfillment House used?*";
    String businessprofile_Yes30 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes30"))?rb1.getString("businessprofile_Yes30"): "Yes";
    String businessprofile_No30 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No30"))?rb1.getString("businessprofile_No30"): "No";
    String businessprofile_details4 = StringUtils.isNotEmpty(rb1.getString("businessprofile_details4"))?rb1.getString("businessprofile_details4"): "If yes, please provide more details :";
    String businessprofile_Returns = StringUtils.isNotEmpty(rb1.getString("businessprofile_Returns"))?rb1.getString("businessprofile_Returns"): "Returns";
    String businessprofile_cancellation = StringUtils.isNotEmpty(rb1.getString("businessprofile_cancellation"))?rb1.getString("businessprofile_cancellation"): "Is your Cancellation policy clearly visible to the customers on your website?";
    String businessprofile_goods = StringUtils.isNotEmpty(rb1.getString("businessprofile_goods"))?rb1.getString("businessprofile_goods"): "Is your Return of Goods policy clearly visible to the customers on your website?";
    String businessprofile_Yes31 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes31"))?rb1.getString("businessprofile_Yes31"): "Yes";
    String businessprofile_No31 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No31"))?rb1.getString("businessprofile_No31"): "No";
    String businessprofile_Customer = StringUtils.isNotEmpty(rb1.getString("businessprofile_Customer"))?rb1.getString("businessprofile_Customer"): "Customer Service Information";
    String businessprofile_support = StringUtils.isNotEmpty(rb1.getString("businessprofile_support"))?rb1.getString("businessprofile_support"): "Do you offer customer support ?*";
    String businessprofile_Yes32 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes32"))?rb1.getString("businessprofile_Yes32"): "Yes";
    String businessprofile_No32 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No32"))?rb1.getString("businessprofile_No32"): "No";
    String businessprofile_please5 = StringUtils.isNotEmpty(rb1.getString("businessprofile_please5"))?rb1.getString("businessprofile_please5"): "If yes, please provide more details :";
    String businessprofile_customer = StringUtils.isNotEmpty(rb1.getString("businessprofile_customer"))?rb1.getString("businessprofile_customer"): "Customer support email";
    String businessprofile_hours = StringUtils.isNotEmpty(rb1.getString("businessprofile_hours"))?rb1.getString("businessprofile_hours"): "Customer support working hours";
    String businessprofile_time = StringUtils.isNotEmpty(rb1.getString("businessprofile_time"))?rb1.getString("businessprofile_time"): "Time frame for answering customer request";
    String businessprofile_phoneno = StringUtils.isNotEmpty(rb1.getString("businessprofile_phoneno"))?rb1.getString("businessprofile_phoneno"): "Customer support phone number";
    String businessprofile_live = StringUtils.isNotEmpty(rb1.getString("businessprofile_live"))?rb1.getString("businessprofile_live"): "Do you use live Chat?";
    String businessprofile_Yes33 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes33"))?rb1.getString("businessprofile_Yes33"): "Yes";
    String businessprofile_No33 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No33"))?rb1.getString("businessprofile_No33"): "No";
    String businessprofile_market = StringUtils.isNotEmpty(rb1.getString("businessprofile_market"))?rb1.getString("businessprofile_market"): "Market Strategy";
    String businessprofile_strategy = StringUtils.isNotEmpty(rb1.getString("businessprofile_strategy"))?rb1.getString("businessprofile_strategy"): "Marketing strategy?";
    String businessprofile_DirectMail = StringUtils.isNotEmpty(rb1.getString("businessprofile_DirectMail"))?rb1.getString("businessprofile_DirectMail"): "DirectMail";
    String businessprofile_Yellowpages = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yellowpages"))?rb1.getString("businessprofile_Yellowpages"): "Yellowpages";
    String businessprofile_Radio = StringUtils.isNotEmpty(rb1.getString("businessprofile_Radio"))?rb1.getString("businessprofile_Radio"): "Radio/TV";
    String businessprofile_Internet = StringUtils.isNotEmpty(rb1.getString("businessprofile_Internet"))?rb1.getString("businessprofile_Internet"): "Internet";
    String businessprofile_Networking = StringUtils.isNotEmpty(rb1.getString("businessprofile_Networking"))?rb1.getString("businessprofile_Networking"): "Networking";
    String businessprofile_Outbound = StringUtils.isNotEmpty(rb1.getString("businessprofile_Outbound"))?rb1.getString("businessprofile_Outbound"): "Outbound Telemarketing";
    String businessprofile_Membership = StringUtils.isNotEmpty(rb1.getString("businessprofile_Membership"))?rb1.getString("businessprofile_Membership"): "Membership";
    String businessprofile_card = StringUtils.isNotEmpty(rb1.getString("businessprofile_card"))?rb1.getString("businessprofile_card"): "Are multiple membership allow for same card?";
    String businessprofile_Yes34 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes34"))?rb1.getString("businessprofile_Yes34"): "Yes";
    String businessprofile_No34 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No34"))?rb1.getString("businessprofile_No34"): "No";
    String businessprofile_member = StringUtils.isNotEmpty(rb1.getString("businessprofile_member"))?rb1.getString("businessprofile_member"): "Do you offer free membership trials?";
    String businessprofile_Yes35 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes35"))?rb1.getString("businessprofile_Yes35"): "Yes";
    String businessprofile_No35 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No35"))?rb1.getString("businessprofile_No35"): "No";
    String businessprofile_Other2 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Other2"))?rb1.getString("businessprofile_Other2"): "Other Information";
    String businessprofile_credit = StringUtils.isNotEmpty(rb1.getString("businessprofile_credit"))?rb1.getString("businessprofile_credit"): "Is a credit card  Required to start the free trials?";
    String businessprofile_Yes36 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes36"))?rb1.getString("businessprofile_Yes36"): "Yes";
    String businessprofile_No36 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No36"))?rb1.getString("businessprofile_No36"): "No";
    String businessprofile_automatically = StringUtils.isNotEmpty(rb1.getString("businessprofile_automatically"))?rb1.getString("businessprofile_automatically"): "Is the card holder automatically billed after the trial period?";
    String businessprofile_Yes37 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes37"))?rb1.getString("businessprofile_Yes37"): "Yes";
    String businessprofile_No37 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No37"))?rb1.getString("businessprofile_No37"): "No";
    String businessprofile_authorization = StringUtils.isNotEmpty(rb1.getString("businessprofile_authorization"))?rb1.getString("businessprofile_authorization"): "Is the credit card sent for pre authorization?";
    String businessprofile_Yes38 = StringUtils.isNotEmpty(rb1.getString("businessprofile_Yes38"))?rb1.getString("businessprofile_Yes38"): "Yes";
    String businessprofile_No38 = StringUtils.isNotEmpty(rb1.getString("businessprofile_No38"))?rb1.getString("businessprofile_No38"): "No";



    ApplicationManagerVO applicationManagerVO=null;
    BusinessProfileVO businessProfileVO=null;
    ValidationErrorList validationErrorList=null;
    List<String> merchantCodeList = new ArrayList();
    List<String> ProductSoldCurrencyList= new ArrayList();
    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }

    if(applicationManagerVO.getBusinessProfileVO()!=null)
    {
        businessProfileVO=applicationManagerVO.getBusinessProfileVO();
        if(functions.isValueNull(businessProfileVO.getMerchantCode()))
        {
            String arrMerchantCode[] = businessProfileVO.getMerchantCode().split(",");
            for(int i=0; i<arrMerchantCode.length; i++)
            {
                merchantCodeList.add(arrMerchantCode[i]);
            }
        }
    }
    if(applicationManagerVO.getBusinessProfileVO()!=null)
    {
        businessProfileVO=applicationManagerVO.getBusinessProfileVO();
        if(functions.isValueNull(businessProfileVO.getProduct_sold_currencies()))
        {
            String arrcurrency[] = businessProfileVO.getProduct_sold_currencies().split(",");
            for(int i=0; i<arrcurrency.length; i++)
            {
                ProductSoldCurrencyList.add(arrcurrency[i]);
            }
        }

    }
    if(businessProfileVO==null)
    {
        businessProfileVO=new BusinessProfileVO();
    }

    if(session.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) session.getAttribute("validationErrorList");
    }
    else if(request.getAttribute("validationErrorList")!=null)
    {
        validationErrorList = (ValidationErrorList) request.getAttribute("validationErrorList");
    }
    else
    {
        validationErrorList= new ValidationErrorList();
    }
    String disableOneTime="";
    String disableMoto="";
    String disableInternet="";
    String disableSwipe="";
    String disableRecurring="";
    String disableThreeDSecure="";
    String disableChannelOther="";
    String disableUS="";
    String disableAsia="";
    String disableCanada="";
    String disableUK="";
    String disableCis="";
    String disableEurope="";
    String disableRestoftheWorld="";
    String disableVisa="";
    String disableRupay="";
    String disableJcb="";
    String disableMastercard="";
    String disableAmericanexpress="";
    String disableDinner="";
    String disableDiscover="";
    String disableOther="";
    String disabled="";
    String customerSupportdisabled="";
    String recurringDisabled="";
    String paymentTypeDisabled="";
    String terminalOther="";
    String paymentDeliveryOther="";

    //paymenttype volume
    String disableCredit="";
    String disableDebit="";
    String disableNetbanking="";
    String disableWallet="";
    String disableAlternate="";

    boolean view=false;
    String globaldisabled="";
    if(functions.isValueNull(request.getParameter("view")))
    {
        globaldisabled="disabled";
        view=true;
    }
    else
    {
        globaldisabled="";
    }

    //for specific condition
    Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep=new HashMap<Boolean, Set<BankInputName>>();

    Set<BankInputName> businessProfileInputList=new HashSet<BankInputName>();

    if(request.getAttribute("fullValidationForStep")!=null)
    {
        fullValidationForStep= (Map<Integer, Map<Boolean, Set<BankInputName>>>) request.getAttribute("fullValidationForStep");
        if(functions.isValueNull(request.getParameter("currentPageNO")) && fullValidationForStep.containsKey(Integer.valueOf(request.getParameter("currentPageNO"))))
        {
            //System.out.println("inside full validation--->"+fullValidationForStep);
            fullPageViseValidationForStep=fullValidationForStep.get(Integer.valueOf(request.getParameter("currentPageNO")));
            if(fullPageViseValidationForStep.containsKey(false))
                businessProfileInputList.addAll(fullPageViseValidationForStep.get(false));
            //System.out.println("inside full validation--->"+(fullPageViseValidationForStep.containsKey(true)));
            if(fullPageViseValidationForStep.containsKey(true))
                businessProfileInputList.addAll(fullPageViseValidationForStep.get(true));
        }
    }

    //end
    if(Integer.parseInt(functions.isValueNull(businessProfileVO.getMethodofacceptance_moto())?businessProfileVO.getMethodofacceptance_moto():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getMethodofacceptance_internet())?businessProfileVO.getMethodofacceptance_internet():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getMethodofacceptance_swipe())?businessProfileVO.getMethodofacceptance_swipe():"0")>=100)
    {
        if(!functions.isValueNull(businessProfileVO.getMethodofacceptance_moto()))
            disableMoto="disabled";
        if(!functions.isValueNull(businessProfileVO.getMethodofacceptance_internet()))
            disableInternet="disabled";
        if(!functions.isValueNull(businessProfileVO.getMethodofacceptance_swipe()))
            disableSwipe="disabled";
    }
    if(Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_uk())?businessProfileVO.getForeigntransactions_uk():"0")+ Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_us())?businessProfileVO.getForeigntransactions_us():"0")+ Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_canada()) ? businessProfileVO.getForeigntransactions_canada() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_cis()) ? businessProfileVO.getForeigntransactions_cis() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()) ? businessProfileVO.getForeigntransactions_Europe() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()) ? businessProfileVO.getForeigntransactions_Asia() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()) ? businessProfileVO.getForeigntransactions_RestoftheWorld() : "0")>=100)
    {
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_us()))
            disableUS="disabled";
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_Asia()))
            disableAsia= "disabled";
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_canada()))
            disableCanada="disabled";
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_cis()))
            disableCis="disabled";
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_Europe()))
            disableEurope="disabled";
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_uk()))
            disableUK="disabled";
        if(!functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld()))
            disableRestoftheWorld="disabled";
    }

    if(Integer.parseInt(functions.isValueNull(businessProfileVO.getOne_time_percentage()) ? businessProfileVO.getOne_time_percentage():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getMoto_percentage())? businessProfileVO.getMoto_percentage() : "0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getInternet_percentage()) ? businessProfileVO.getInternet_percentage() :"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getSwipe_percentage())? businessProfileVO.getSwipe_percentage() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getRecurring_percentage())? businessProfileVO.getRecurring_percentage() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getThreedsecure_percentage())? businessProfileVO.getThreedsecure_percentage():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getTerminal_type_otheryes())? businessProfileVO.getTerminal_type_otheryes():"0")>=100)
    {
        if(!functions.isValueNull(businessProfileVO.getOne_time_percentage()))
            disableOneTime="disabled";
        if(!functions.isValueNull(businessProfileVO.getMoto_percentage()))
            disableMoto="disabled";
        if(!functions.isValueNull(businessProfileVO.getInternet_percentage()))
            disableInternet="disabled";
        if(!functions.isValueNull(businessProfileVO.getSwipe_percentage()))
            disableSwipe="disabled";
        if(!functions.isValueNull(businessProfileVO.getRecurring_percentage()))
            disableRecurring="disabled";
        if(!functions.isValueNull(businessProfileVO.getThreedsecure_percentage()))
            disableThreeDSecure="disabled";
        if(!functions.isValueNull(businessProfileVO.getTerminal_type_otheryes()))
            disableChannelOther="disabled";
    }

    if(Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_visa())?businessProfileVO.getCardvolume_visa():"0")+ Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_mastercard()) ? businessProfileVO.getCardvolume_mastercard() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()) ? businessProfileVO.getCardvolume_americanexpress() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_discover()) ? businessProfileVO.getCardvolume_discover() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_dinner()) ? businessProfileVO.getCardvolume_dinner() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_other()) ? businessProfileVO.getCardvolume_other() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_rupay()) ? businessProfileVO.getCardvolume_rupay() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getCardvolume_jcb()) ? businessProfileVO.getCardvolume_jcb() : "0") >=100)
    {
        if(!functions.isValueNull(businessProfileVO.getCardvolume_visa()))
            disableVisa="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_mastercard()))
            disableMastercard="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_americanexpress()))
            disableAmericanexpress="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_discover()))
            disableDiscover="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_dinner()))
            disableDinner="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_rupay()))
            disableRupay="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_jcb()))
            disableJcb="disabled";
        if(!functions.isValueNull(businessProfileVO.getCardvolume_other()))
            disableOther="disabled";
    }

    if(Integer.parseInt(functions.isValueNull(businessProfileVO.getPaymenttype_credit()) ? businessProfileVO.getPaymenttype_credit():"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getPaymenttype_debit())? businessProfileVO.getPaymenttype_debit() : "0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getPaymenttype_netbanking()) ? businessProfileVO.getPaymenttype_netbanking() :"0")+Integer.parseInt(functions.isValueNull(businessProfileVO.getPaymenttype_wallet())? businessProfileVO.getPaymenttype_wallet() : "0") + Integer.parseInt(functions.isValueNull(businessProfileVO.getPaymenttype_alternate())? businessProfileVO.getPaymenttype_alternate() : "0") >=100)
    {
        if(!functions.isValueNull(businessProfileVO.getPaymenttype_credit()))
            disableCredit="disabled";
        if(!functions.isValueNull(businessProfileVO.getPaymenttype_debit()))
            disableDebit="disabled";
        if(!functions.isValueNull(businessProfileVO.getPaymenttype_netbanking()))
            disableNetbanking="disabled";
        if(!functions.isValueNull(businessProfileVO.getPaymenttype_wallet()))
            disableWallet="disabled";
        if(!functions.isValueNull(businessProfileVO.getPaymenttype_alternate()))
            disableAlternate="disabled";
    }

    if(validationErrorList!=null && validationErrorList.getError("cardtypesaccepted_other_yes")!=null)
    {
        businessProfileVO.setCardtypesaccepted_other("Y");
    }
    else if("Y".equals(businessProfileVO.getCardtypesaccepted_americanexpress()) || "Y".equals(businessProfileVO.getCardtypesaccepted_diners()) || "Y".equals(businessProfileVO.getCardtypesaccepted_discover()) || "Y".equals(businessProfileVO.getCardtypesaccepted_mastercard()) || "Y".equals(businessProfileVO.getCardtypesaccepted_jcb()) || "Y".equals(businessProfileVO.getCardtypesaccepted_visa()))
    {
        businessProfileVO.setCardtypesaccepted_other(null);
    }
    else
    {
        businessProfileVO.setCardtypesaccepted_other("Y");
    }

    if(functions.isValueNull(businessProfileVO.getBillingModel()) && "recurring".equals(businessProfileVO.getBillingModel()))
    {
        recurringDisabled="";
    }
    else
    {
        recurringDisabled="disabled";
    }
    if(functions.isValueNull(businessProfileVO.getBillingModel()) && "payment_other".equals(businessProfileVO.getBillingModel()))
    {
        paymentTypeDisabled="";
    }
    else
    {
        paymentTypeDisabled="disabled";
    }
    if(functions.isValueNull(businessProfileVO.getTerminal_type()) && "other_terminal".equals(businessProfileVO.getTerminal_type()))
    {
        terminalOther="";
    }
    else
    {
        terminalOther="disabled";
    }
    if(functions.isValueNull(businessProfileVO.getPayment_delivery()) && "payment_delivery_other".equals(businessProfileVO.getPayment_delivery()))
    {
        paymentDeliveryOther="";
    }
    else
    {
        paymentDeliveryOther="disabled";
    }
    if(!functions.isValueNull(businessProfileVO.getCustomer_support()) || "N".equals(businessProfileVO.getCustomer_support()))
    {
        customerSupportdisabled="disabled";

    }
%>
<div class="content-page" id="businessid">
    <div class="content">
        <div class="page-heading">
            <%
                if(businessProfileInputList.contains(BankInputName.merchantcode) ||
                        businessProfileInputList.contains(BankInputName.lowestticket) || businessProfileInputList.contains(BankInputName.averageticket)|| businessProfileInputList.contains(BankInputName.highestticket)||
                        businessProfileInputList.contains(BankInputName.foreigntransactions_us)|| businessProfileInputList.contains(BankInputName.foreigntransactions_uk)|| businessProfileInputList.contains(BankInputName.foreigntransactions_Europe)|| businessProfileInputList.contains(BankInputName.foreigntransactions_Asia)||
                        businessProfileInputList.contains(BankInputName.foreigntransactions_cis)|| businessProfileInputList.contains(BankInputName.foreigntransactions_canada)|| businessProfileInputList.contains(BankInputName.foreigntransactions_RestoftheWorld) || businessProfileInputList.contains(BankInputName.recurringservices)||
                        businessProfileInputList.contains(BankInputName.recurringservicesyes)|| businessProfileInputList.contains(BankInputName.billing_model) || businessProfileInputList.contains(BankInputName.billing_timeframe)|| businessProfileInputList.contains(BankInputName.recurring_amount)|| businessProfileInputList.contains(BankInputName.seasonal_fluctuating)||businessProfileInputList.contains(BankInputName.methodofacceptance_swipe)||
                        businessProfileInputList.contains(BankInputName.seasonal_fluctuating_yes)|| businessProfileInputList.contains(BankInputName.methodofacceptance_moto) || businessProfileInputList.contains(BankInputName.methodofacceptance_internet)  ||
                        businessProfileInputList.contains(BankInputName.terminal_type)||  businessProfileInputList.contains(BankInputName.terminal_type_otheryes)|| businessProfileInputList.contains(BankInputName.terminal_type_other)||
                        businessProfileInputList.contains(BankInputName.one_time_percentage)||  businessProfileInputList.contains(BankInputName.moto_percentage)||
                        businessProfileInputList.contains(BankInputName.internet_percentage)||  businessProfileInputList.contains(BankInputName.swipe_percentage)||
                        businessProfileInputList.contains(BankInputName.recurring_percentage)||  businessProfileInputList.contains(BankInputName.threedsecure_percentage)||
                        businessProfileInputList.contains(BankInputName.cardvolume_visa)||businessProfileInputList.contains(BankInputName.cardvolume_mastercard)|| businessProfileInputList.contains(BankInputName.cardvolume_americanexpress)||businessProfileInputList.contains(BankInputName.cardvolume_discover)||businessProfileInputList.contains(BankInputName.cardvolume_dinner)||
                        businessProfileInputList.contains(BankInputName.cardvolume_rupay)|| businessProfileInputList.contains(BankInputName.cardvolume_jcb)|| businessProfileInputList.contains(BankInputName.cardvolume_other)||
                        businessProfileInputList.contains(BankInputName.cardtypesaccepted_visa)|| businessProfileInputList.contains(BankInputName.cardtypesaccepted_mastercard) || businessProfileInputList.contains(BankInputName.cardtypesaccepted_americanexpress)|| businessProfileInputList.contains(BankInputName.cardtypesaccepted_discover) || businessProfileInputList.contains(BankInputName.cardtypesaccepted_diners)||
                        businessProfileInputList.contains(BankInputName.cardtypesaccepted_rupay)|| businessProfileInputList.contains(BankInputName.cardtypesaccepted_jcb) ||  businessProfileInputList.contains(BankInputName.cardtypesaccepted_other)|| businessProfileInputList.contains(BankInputName.cardtypesaccepted_other_yes)||businessProfileInputList.contains(BankInputName.descriptionofproducts)||
                        businessProfileInputList.contains(BankInputName.paymenttype_credit) || businessProfileInputList.contains(BankInputName.paymenttype_debit) ||
                        businessProfileInputList.contains(BankInputName.paymenttype_netbanking) || businessProfileInputList.contains(BankInputName.paymenttype_wallet) ||
                        businessProfileInputList.contains(BankInputName.paymenttype_alternate) || businessProfileInputList.contains(BankInputName.product_sold_currencies) || view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <%--<h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Product Details</strong></h2>--%>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():businessprofile_please%></h4></center>

                                <%--<h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">MCC Details</h2>--%>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.merchantcode) || view)
                                    {
                                %>


                                <div class="form-group col-md-6 has-feedback">
                                    <label>
                                        <p><%=businessprofile_mcc%></p>
                                    </label>
                                </div>
                                <div class="form-group col-md-6 has-feedback" style="padding-left:0;">

                                    <div class="form-group col-md-12 has-feedback">
                                        <select size="5" multiple="multiple" id="lstStates" class="form-group" <%=globaldisabled%>>
                                            <%=AppFunctionUtil.getmerchant_category_code(functions.isValueNull(businessProfileVO.getMerchantCode()) == true ? businessProfileVO.getMerchantCode() : "", merchantCodeList)%>
                                        </select>
                                    </div>

                                    <input type="hidden" id="merchantcode" name="merchantcode" value="">
                                </div>


                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.lowestticket) ||businessProfileInputList.contains(BankInputName.averageticket)||businessProfileInputList.contains(BankInputName.highestticket)||  view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color h2_font" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_ticket%></h2>
                                <div class="form-group col-md-12 has-feedback" style="padding-left:0;">
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="lowestticket" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_lowest%>&nbsp;<%=businessprofile_ticket%></label>
                                        <input  class="form-control" type="text" id="lowestticket" name="lowestticket" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getLowestticket())==true?businessProfileVO.getLowestticket():""%>"/><%if(validationErrorList.getError("lowestticket")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="z-index: 0!important;"></i><%}%>
                     <span style="<%=validationErrorList.getError("ltlessthan_AT")!=null?"background-color: #f2dede;":""%>" class="errormesage" width="50%">
                            <%=validationErrorList.getError("ltlessthan_AT")!=null?"Lowest ticket must be less than or equal to Average Ticket":""%>
                        </span>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="averageticket" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_average%>&nbsp;<%=businessprofile_ticket1%></label>
                                        <input  class="form-control" type="text" id="averageticket" name="averageticket" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getAverageticket())==true?businessProfileVO.getAverageticket():""%>"/><%if(validationErrorList.getError("averageticket")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="z-index: 0!important;"></i><%}%>
                   <span style="<%=validationErrorList.getError("atlessthan_HT")!=null?"background-color: #f2dede;":""%>" class="errormesage" width="50%">
                            <%=validationErrorList.getError("atlessthan_HT")!=null?"Average ticket must be less than or equal to Highest Ticket":""%>
                        </span>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="highestticket" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_highest%>&nbsp;<%=businessprofile_ticket2%></label>
                                        <input  class="form-control" type="text" id="highestticket" name="highestticket" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getHighestticket())==true?businessProfileVO.getHighestticket():""%>" name="highestticket" /><%if(validationErrorList.getError("highestticket")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="z-index: 0!important;"></i><%}%>
                                    </div>
                                </div>

                                <%
                                        }

                                %>
                                <%
                                    if (businessProfileInputList.contains(BankInputName.recurringservices)|| businessProfileInputList.contains(BankInputName.recurringservicesyes)|| businessProfileInputList.contains(BankInputName.billing_model) || businessProfileInputList.contains(BankInputName.billing_timeframe)|| businessProfileInputList.contains(BankInputName.recurring_amount)||
                                            businessProfileInputList.contains(BankInputName.automatic_recurring)|| businessProfileInputList.contains(BankInputName.seasonal_fluctuating) || businessProfileInputList.contains(BankInputName.seasonal_fluctuating_yes) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color h2_font" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Billing%></h2>

                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="form-group col-md-2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_recurring%></label>
                                    </div>
                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" id="recurringservices" name="recurringservices" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getRecurringservices())?"checked":""%>/><%if(validationErrorList.getError("recurringservices")!=null){%><%--<span class="apperrormsg">Invalid Recurring Services</span>--%><%}%>
                                        &nbsp; <%=businessprofile_Yes1%>
                                        &nbsp;&nbsp;<input type="radio" name="recurringservices" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getRecurringservices()) || !functions.isValueNull(businessProfileVO.getRecurringservices()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No1%> &nbsp;
                                    </div>

                                    <div class="col-md-3" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_if%></font>
                                    </div>
                                    <div class="col-md-5" style="padding-left: 0px;">
                                        <p>
                                            <input  class="form-control"  id="recurringservicesyes" name="recurringservicesyes" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getRecurringservicesyes())==true?businessProfileVO.getRecurringservicesyes():""%>" name="recurringservicesyes" <%=!functions.isValueNull(businessProfileVO.getRecurringservices())||"N".equals(businessProfileVO.getRecurringservices())?"disabled":""%> /><%if(validationErrorList.getError("recurringservicesyes")!=null){%><%--<span class="apperrormsg">Invalid description of Recurring Services </span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%> <%--//msg changed--%>
                                        </p>
                                    </div>
                                </div>

                                <div class="form-group col-md-12 has-feedback">

                                    <div class="form-group col-md-2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_type%></label>
                                    </div>

                                    <div class="col-md-8" style="padding: 0;">
                                        <div class="form-group col-md-4 col-lg-4 has-feedback" style="padding: 0;">
                                            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                                <input type="radio"   name="billing_model" <%=globaldisabled%> value="recurring" <%="recurring".equals(businessProfileVO.getBillingModel())?"checked":""%>/>
                                                &nbsp;<%=businessprofile_Recurring%>
                                            </label>
                                        </div>
                                        <div class="form-group col-md-4 col-lg-4" style="padding: 0;">
                                            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                                <input type="radio"  name="billing_model" <%=globaldisabled%> value="one_time" <%="one_time".equals(businessProfileVO.getBillingModel()) || !functions.isValueNull(businessProfileVO.getBillingModel()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_One%>
                                            </label>
                                        </div>
                                        <div class="form-group col-md-4 col-lg-4 has-feedback" style="padding: 0;">
                                            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;padding-left: 0;">
                                                <input type="radio" name="billing_model" <%=globaldisabled%> value="pay_per_minute" <%="pay_per_minute".equals(businessProfileVO.getBillingModel()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_pay%>
                                            </label>
                                        </div>
                                    </div>

                                    <div class="col-md-8" style="padding: 0;">
                                        <div class="form-group col-md-4 col-lg-4 has-feedback" style="padding: 0;">
                                            <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                                <input type="radio" name="billing_model" <%=globaldisabled%> value="pay_per_download" <%="pay_per_download".equals(businessProfileVO.getBillingModel()) ?"checked":""%>/>
                                                &nbsp;<%=businessprofile_download%>
                                            </label>
                                        </div>

                                        <div class="form-group col-md-4 col-lg-8" style="padding: 0;">

                                            <div class="col-md-5 col-lg-3" style="padding: 0;">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                                    <input type="radio" name="billing_model" <%=globaldisabled%> value="payment_other" <%="payment_other".equals(businessProfileVO.getBillingModel())?"checked":""%>  />
                                                    &nbsp;<%=businessprofile_Other%>
                                                </label>
                                            </div>

                                            <div class="col-md-7 col-lg-7" style="padding: 0;" id="margintop10">
                                                <input type="text" class="form-control"   id="payment_type_yes" name="payment_type_yes" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_type_yes())==true?businessProfileVO.getPayment_type_yes():""%>" <%=paymentTypeDisabled%>/><%if(validationErrorList.getError("payment_type_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                            </div>

                                        </div>
                                    </div>
                                </div>

                                <div class="form-group col-md-12 has-feedback">
                                    <div class="form-group col-md-2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_billing%></label>
                                    </div>


                                    <div class="form-group col-md-2 col-lg-2 has-feedback" style="padding: 0;">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                            <input type="radio" name="billing_timeframe" <%=globaldisabled%> value="daily" <%="daily".equals(businessProfileVO.getBillingTimeFrame())?"checked":(functions.isValueNull(recurringDisabled) && !functions.isValueNull(businessProfileVO.getBillingTimeFrame()) ?"checked":"")%> <%=recurringDisabled%> />
                                            &nbsp;<%=businessprofile_Daily%></label>
                                    </div>
                                    <div class="form-group col-md-2 col-lg-2" style="padding: 0;">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                            <input type="radio"  name="billing_timeframe" <%=globaldisabled%> value="weekly"   <%="weekly".equals(businessProfileVO.getBillingTimeFrame())?"checked":""%> <%=recurringDisabled%> />
                                            &nbsp;<%=businessprofile_Weekly%></label>
                                    </div>
                                    <div class="form-group col-md-2 col-lg-2 has-feedback" style="padding: 0;">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;padding-left: 0;">
                                            <input type="radio" name="billing_timeframe" <%=globaldisabled%> value="monthly"   <%="monthly".equals(businessProfileVO.getBillingTimeFrame())?"checked":""%> <%=recurringDisabled%> />
                                            &nbsp;<%=businessprofile_Monthly%></label>
                                    </div>
                                    <div class="form-group col-md-2 col-lg-2 has-feedback" style="padding: 0;">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                            <input type="radio" name="billing_timeframe" <%=globaldisabled%> value="quaterly"   <%="quaterly".equals(businessProfileVO.getBillingTimeFrame())?"checked":""%> <%=recurringDisabled%>/>
                                            &nbsp;<%=businessprofile_Quarterly%></label>
                                    </div>
                                    <div class="form-group col-md-2 col-lg-2" style="padding: 0;">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                            <input type="radio" name="billing_timeframe" <%=globaldisabled%> value="yearly"   <%="yearly".equals(businessProfileVO.getBillingTimeFrame())?"checked":""%> <%=recurringDisabled%>  />
                                            &nbsp;<%=businessprofile_Annualy%></label>
                                    </div>
                                </div>

                                <div class="form-group col-md-8 has-feedback">
                                    <div class="col-md-3" style="padding: 0;">
                                        <label for="recurring_amount" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_Recurring_amount%></label>
                                    </div>
                                    <div class="col-md-5" style="padding-left: 0;margin-bottom:0;">
                                        <input type="text" class="form-control"   id="recurring_amount" name="recurring_amount" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getRecurringAmount())==true?businessProfileVO.getRecurringAmount():""%>" <%=recurringDisabled%>/><%if(validationErrorList.getError("recurring_amount")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>


                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_require%></label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio" name="automatic_recurring"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getAutomaticRecurring())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes2%>&nbsp;&nbsp;
                                        <input type="radio" name="automatic_recurring"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getAutomaticRecurring()) || !functions.isValueNull(businessProfileVO.getAutomaticRecurring()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No2%></div>
                                </div>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="form-group col-md-2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_Seasonal%></label>
                                    </div>

                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" id="seasonal_fluctuating" name="seasonal_fluctuating" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getSeasonal_fluctuating())?"checked":""%>/><%if(validationErrorList.getError("seasonal_fluctuating")!=null){%><%--<span class="apperrormsg">Invalid Seasonal Fluctuating</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes3%>
                                        &nbsp;&nbsp;<input type="radio" name="seasonal_fluctuating" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getSeasonal_fluctuating()) || !functions.isValueNull(businessProfileVO.getSeasonal_fluctuating()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No3%>&nbsp;
                                    </div>

                                    <div class="col-md-3" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_please1%></font>
                                    </div>
                                    <div class="col-md-5" style="padding-left: 0px;">
                                        <p>
                                            <input  class="form-control"  id="seasonal_fluctuating_yes" name="seasonal_fluctuating_yes" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(businessProfileVO.getSeasonal_fluctuating_yes())==true?businessProfileVO.getSeasonal_fluctuating_yes():""%>" name="seasonal_fluctuating_yes" <%=!functions.isValueNull(businessProfileVO.getSeasonal_fluctuating())||"N".equals(businessProfileVO.getSeasonal_fluctuating())?"disabled":""%> /><%if(validationErrorList.getError("seasonal_fluctuating_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>
                                </div>
                                <%--<%
                                    }
                                %>--%>
                          <%--      <%
                                    if(businessProfileInputList.contains(BankInputName.seasonal_fluctuating) || businessProfileInputList.contains(BankInputName.seasonal_fluctuating_yes) || view)
                                    {
                                        String seasonalDisabled = "";
                                        if(functions.isValueNull(businessProfileVO.getSeasonal_fluctuating()) && "N".equals(businessProfileVO.getSeasonal_fluctuating()))
                                            seasonalDisabled = "disabled";
                                        if("disabled".equals(globaldisabled))
                                            seasonalDisabled = "disabled";
                                %>

                                <%
                                        }

                                %>--%>
                                <%
                                    }
                                %>


                                    <%
                                        if(businessProfileInputList.contains(BankInputName.paymenttype_credit) || businessProfileInputList.contains(BankInputName.paymenttype_debit) ||
                                                businessProfileInputList.contains(BankInputName.paymenttype_netbanking) || businessProfileInputList.contains(BankInputName.paymenttype_wallet) ||
                                                businessProfileInputList.contains(BankInputName.paymenttype_alternate) || view)
                                        {
                                    %>
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Payment Type Accepted and Volume</h2>


                                <script>

                                    $(document).ready(function(){

                                        //Payment Type Accepted and volume
                                        if ($('input:checkbox[name=credit]:checked').val() == "paymenttype_credit")
                                        {
                                            document.myformname.paymenttype_credit.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.paymenttype_credit.disabled = true;
                                            document.myformname.paymenttype_credit.value = "";
                                        }

                                        $("input[name='credit']").on("click", function ()
                                        {
                                            if ($('input:checkbox[name=credit]:checked').val() == "paymenttype_credit")
                                            {
                                                document.myformname.paymenttype_credit.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.paymenttype_credit.disabled = true;
                                                document.myformname.paymenttype_credit.value = "";
                                            }
                                        });
                                        if ($('input:checkbox[name=debit]:checked').val() == "paymenttype_debit")
                                        {
                                            document.myformname.paymenttype_debit.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.paymenttype_debit.disabled = true;
                                            document.myformname.paymenttype_debit.value = "";
                                        }

                                        $("input[name='debit']").on("click", function ()
                                        {
                                            if ($('input:checkbox[name=debit]:checked').val() == "paymenttype_debit")
                                            {
                                                document.myformname.paymenttype_debit.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.paymenttype_debit.disabled = true;
                                                document.myformname.paymenttype_debit.value = "";
                                            }
                                        });


                                        if ($('input:checkbox[name=netbanking]:checked').val() == "paymenttype_netbanking")
                                        {
                                            document.myformname.paymenttype_netbanking.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.paymenttype_netbanking.disabled = true;
                                            document.myformname.paymenttype_netbanking.value = "";
                                        }

                                        $("input[name='netbanking']").on("click", function ()
                                        {
                                            if ($('input:checkbox[name=netbanking]:checked').val() == "paymenttype_netbanking")
                                            {
                                                document.myformname.paymenttype_netbanking.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.paymenttype_netbanking.disabled = true;
                                                document.myformname.paymenttype_netbanking.value = "";
                                            }
                                        });


                                        if ($('input:checkbox[name=wallet]:checked').val() == "paymenttype_wallet")
                                        {
                                            document.myformname.paymenttype_wallet.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.paymenttype_wallet.disabled = true;
                                            document.myformname.paymenttype_wallet.value = "";
                                        }

                                        $("input[name='wallet']").on("click", function ()
                                        {
                                            if ($('input:checkbox[name=wallet]:checked').val() == "paymenttype_wallet")
                                            {
                                                document.myformname.paymenttype_wallet.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.paymenttype_wallet.disabled = true;
                                                document.myformname.paymenttype_wallet.value = "";
                                            }
                                        });


                                        if ($('input:checkbox[name=alternate_payment]:checked').val() == "paymenttype_alternate")
                                        {
                                            document.myformname.paymenttype_alternate.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.paymenttype_alternate.disabled = true;
                                            document.myformname.paymenttype_alternate.value = "";
                                        }

                                        $("input[name='alternate_payment']").on("click", function ()
                                        {
                                            if ($('input:checkbox[name=alternate_payment]:checked').val() == "paymenttype_alternate")
                                            {
                                                document.myformname.paymenttype_alternate.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.paymenttype_alternate.disabled = true;
                                                document.myformname.paymenttype_alternate.value = "";
                                            }
                                        });
                                        /*.................End...................*/

                                    });

                                </script>


                                    <div class="form-group col-md-4">
                                        <div class="col-md-6" style="padding:0;">
                                            <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                                <input type="checkbox" name="credit" value="paymenttype_credit" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getPaymenttype_credit())){%> checked <%}%> />
                                                &nbsp;&nbsp;Credit Card</label>
                                        </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group" <%--style="margin-left: 35%;margin-top: -9%;width: 150px;"--%>>
                                            <input type="text" class="form-control"  id="paymenttype_credit" <%=globaldisabled%> name="paymenttype_credit" style="font-weight:bold"  value="<%=functions.isValueNull(businessProfileVO.getPaymenttype_credit())==true?businessProfileVO.getPaymenttype_credit():""%>" maxlength="3" onkeypress="return isNumberKey(event)"  <%=disableCredit%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <span style="<%=validationErrorList.getError("paymenttype_credit")!=null && !functions.isValueNull(disableCredit)?"background-color: #f2dede;":""%>"class="errormesage" id="paymenttype_credit"><%if(validationErrorList.getError("paymenttype_credit")!=null && !functions.isValueNull(disableCredit)){%><%=validationErrorList.getError("paymenttype_credit").getLogMessage()%><%}%></span>
                                        </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="debit" value="paymenttype_debit" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getPaymenttype_debit())){%> checked <%}%> />
                                            &nbsp;&nbsp;Debit Card</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group" <%--style="margin-left: 35%;margin-top: -9%;width: 150px;"--%>>
                                            <input type="text" id="paymenttype_debit" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getPaymenttype_debit())==true?businessProfileVO.getPaymenttype_debit():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="paymenttype_debit" <%=disableDebit%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="netbanking" value="paymenttype_netbanking" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getPaymenttype_netbanking())){%> checked <%}%> />
                                            &nbsp;&nbsp;Netbanking</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group" <%--style="margin-left: 35%;margin-top: -9%;width: 150px;"--%>>
                                            <input type="text" id="paymenttype_netbanking" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getPaymenttype_netbanking())==true?businessProfileVO.getPaymenttype_netbanking():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="paymenttype_netbanking" <%=disableNetbanking%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        </div>
                                            </div>
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="wallet" value="paymenttype_wallet" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getPaymenttype_wallet())){%> checked <%}%> />
                                            &nbsp;&nbsp;EWallet</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group" <%--style="margin-left: 35%;margin-top: -9%;width: 150px;"--%>>
                                            <input type="text" id="paymenttype_wallet" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getPaymenttype_wallet())==true?businessProfileVO.getPaymenttype_wallet():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="paymenttype_wallet" <%=disableWallet%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline"  id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="alternate_payment" value="paymenttype_alternate" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getPaymenttype_alternate())){%> checked <%}%> />
                                            &nbsp;&nbsp;Alternate Payment</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group" <%--style="margin-left: 49%;margin-top: -9%;width: 107px;"--%>>
                                            <input type="text" id="paymenttype_alternate" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getPaymenttype_alternate())==true?businessProfileVO.getPaymenttype_alternate():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="paymenttype_alternate" <%=disableAlternate%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-12 has-feedback">

                                        <h2 style="<%=validationErrorList.getError("paymenttype_All")!=null?"padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; color: #474a54; background-color: #fdf7f7; border-left: 4px solid #f00; font-family: Open Sans; font-size: 13px; font-weight: 600; text-align: center;":""%>" class="errormesage" width="50%">
                                            <%=validationErrorList.getError("paymenttype_All")!=null?"Sum Of All Payment Type Accepted and Volume (i.e CC+DB+NB+EW+AP) should be equal to 100%":""%>

                                        </h2>
                                    </div>
                                    <%
                                        }
                                    %>


                                <%
                                    if(businessProfileInputList.contains(BankInputName.foreigntransactions_us)||businessProfileInputList.contains(BankInputName.foreigntransactions_Europe)|| businessProfileInputList.contains(BankInputName.foreigntransactions_Asia)||businessProfileInputList.contains(BankInputName.foreigntransactions_cis)||businessProfileInputList.contains(BankInputName.foreigntransactions_canada)||businessProfileInputList.contains(BankInputName.foreigntransactions_RestoftheWorld)||businessProfileInputList.contains(BankInputName.foreigntransactions_uk)|| view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_percentage%></h2>

                                <script>

                                    $(document).ready(function(){

                                        //Percentage of foreign transactions
                                        if ($('input:checkbox[name=us]:checked').val() == "foreigntransactions_us")
                                        {
                                            document.myformname.foreigntransactions_us.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_us.disabled = true;
                                            document.myformname.foreigntransactions_us.value = "";
                                        }

                                        $( "input[name='us']").on( "click", function() {
                                            if($('input:checkbox[name=us]:checked').val()=="foreigntransactions_us")
                                            {
                                                document.myformname.foreigntransactions_us.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_us.disabled = true;
                                                document.myformname.foreigntransactions_us.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=europe]:checked').val()=="foreigntransactions_Europe")
                                        {
                                            document.myformname.foreigntransactions_Europe.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_Europe.disabled = true;
                                            document.myformname.foreigntransactions_Europe.value = "";
                                        }

                                        $( "input[name='europe']").on( "click", function() {
                                            if($('input:checkbox[name=europe]:checked').val()=="foreigntransactions_Europe")
                                            {
                                                document.myformname.foreigntransactions_Europe.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_Europe.disabled = true;
                                                document.myformname.foreigntransactions_Europe.value = "";
                                            }
                                        });



                                        if($('input:checkbox[name=asia]:checked').val()=="foreigntransactions_Asia")
                                        {
                                            document.myformname.foreigntransactions_Asia.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_Asia.disabled = true;
                                            document.myformname.foreigntransactions_Asia.value = "";
                                        }

                                        $( "input[name='asia']").on( "click", function() {
                                            if($('input:checkbox[name=asia]:checked').val()=="foreigntransactions_Asia")
                                            {
                                                document.myformname.foreigntransactions_Asia.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_Asia.disabled = true;
                                                document.myformname.foreigntransactions_Asia.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=cis]:checked').val()=="foreigntransactions_cis")
                                        {
                                            document.myformname.foreigntransactions_cis.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_cis.disabled = true;
                                            document.myformname.foreigntransactions_cis.value = "";
                                        }

                                        $( "input[name='cis']").on( "click", function() {
                                            if($('input:checkbox[name=cis]:checked').val()=="foreigntransactions_cis")
                                            {
                                                document.myformname.foreigntransactions_cis.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_cis.disabled = true;
                                                document.myformname.foreigntransactions_cis.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=canada]:checked').val()=="foreigntransactions_canada")
                                        {
                                            document.myformname.foreigntransactions_canada.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_canada.disabled = true;
                                            document.myformname.foreigntransactions_canada.value = "";
                                        }

                                        $( "input[name='canada']").on( "click", function() {
                                            if($('input:checkbox[name=canada]:checked').val()=="foreigntransactions_canada")
                                            {
                                                document.myformname.foreigntransactions_canada.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_canada.disabled = true;
                                                document.myformname.foreigntransactions_canada.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=uk]:checked').val()=="foreigntransactions_uk")
                                        {
                                            document.myformname.foreigntransactions_uk.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_uk.disabled = true;
                                            document.myformname.foreigntransactions_uk.value = "";
                                        }

                                        $( "input[name='uk']").on( "click", function() {
                                            if($('input:checkbox[name=uk]:checked').val()=="foreigntransactions_uk")
                                            {
                                                document.myformname.foreigntransactions_uk.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_uk.disabled = true;
                                                document.myformname.foreigntransactions_uk.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=restofworld]:checked').val()=="foreigntransactions_RestoftheWorld")
                                        {
                                            document.myformname.foreigntransactions_RestoftheWorld.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.foreigntransactions_RestoftheWorld.disabled = true;
                                            document.myformname.foreigntransactions_RestoftheWorld.value = "";
                                        }

                                        $( "input[name='restofworld']").on( "click", function() {
                                            if($('input:checkbox[name=restofworld]:checked').val()=="foreigntransactions_RestoftheWorld")
                                            {
                                                document.myformname.foreigntransactions_RestoftheWorld.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.foreigntransactions_RestoftheWorld.disabled = true;
                                                document.myformname.foreigntransactions_RestoftheWorld.value = "";
                                            }
                                        });
                                        /*..................End..........................*/

                                    });

                                </script>


                                <div class="form-group col-md-4 ">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex; padding-left: 0;">
                                        <input type="checkbox" name="us" value="foreigntransactions_us" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_us())){%> checked <%}%> />
                                        &nbsp;&nbsp;<%=businessprofile_US%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_us" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_us())==true?businessProfileVO.getForeigntransactions_us():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_us" <%=disableUS%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_us")!=null){%>Invalid US<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                    </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="europe" value="foreigntransactions_Europe" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_Europe())){%> checked <%}%>/>
                                        &nbsp;&nbsp;<%=businessprofile_Europe%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_Europe" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_Europe())==true?businessProfileVO.getForeigntransactions_Europe():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_Europe" <%=disableEurope%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_Europe")!=null){%>Invalid Europe<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="asia" value="foreigntransactions_Asia" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_Asia())){%> checked <%}%> />
                                        &nbsp;&nbsp;<%=businessprofile_Asia%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_Asia" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_Asia())==true?businessProfileVO.getForeigntransactions_Asia():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_Asia" <%=disableAsia%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_Asia")!=null){%>Invalid Asia<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                        </div>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="cis" value="foreigntransactions_cis" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_cis())){%> checked <%}%> />
                                        &nbsp;&nbsp;<%=businessprofile_cis%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_cis" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_cis())==true?businessProfileVO.getForeigntransactions_cis():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_cis" <%=disableCis%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_cis")!=null){%>Invalid CIS<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="canada" value="foreigntransactions_canada" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_canada())){%> checked <%}%> />
                                        &nbsp;&nbsp;<%=businessprofile_canada%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_canada" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_canada())==true?businessProfileVO.getForeigntransactions_canada():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_canada" <%=disableCanada%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_canada")!=null){%>Invalid Canada<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="uk" value="foreigntransactions_uk" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_uk())){%> checked <%}%> />
                                        &nbsp;&nbsp;<%=businessprofile_uk%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_uk" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_uk())==true?businessProfileVO.getForeigntransactions_uk():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_uk" <%=disableUK%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_uk")!=null){%>Invalid UK<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                        </div>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="restofworld" value="foreigntransactions_RestoftheWorld" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld())){%> checked <%}%> />
                                        &nbsp;&nbsp;<%=businessprofile_row%></label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="foreigntransactions_RestoftheWorld" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getForeigntransactions_RestoftheWorld())==true?businessProfileVO.getForeigntransactions_RestoftheWorld():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="foreigntransactions_RestoftheWorld" <%=disableRestoftheWorld%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%if(validationErrorList.getError("foreigntransactions_RestoftheWorld")!=null){%>Invalid ROW<%--<i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i>--%><%}%>
                                    </div>
                                        </div>
                                </div>
                                <div class="form-group col-md-12 has-feedback">

                                    <h2 style="<%=validationErrorList.getError("foreigntransactions_All")!=null?"padding: 15px; margin-bottom: 20px; border: 1px solid transparent; border-radius: 4px; color: #474a54; background-color: #fdf7f7; border-left: 4px solid #f00; font-family: Open Sans; font-size: 13px; font-weight: 600; text-align: center;":""%>" class="errormesage" width="50%">
                                        <%=validationErrorList.getError("foreigntransactions_All")!=null?"Sum Of All Foreign Transaction (i.e US+Europe+Asia+Cis+Canada+UK+Rest of the world) should be equal to 100%":""%>
                                    </h2>

                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.one_time_percentage)|| businessProfileInputList.contains(BankInputName.moto_percentage)|| businessProfileInputList.contains(BankInputName.internet_percentage)|| businessProfileInputList.contains(BankInputName.swipe_percentage)|| businessProfileInputList.contains(BankInputName.recurring_percentage)|| businessProfileInputList.contains(BankInputName.threedsecure_percentage)|| businessProfileInputList.contains(BankInputName.terminal_type_otheryes) || businessProfileInputList.contains(BankInputName.terminal_type_other) ||view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Channel Type Accepted and Volume</h2>


                                <script>
                                    $(document).ready(function(){

                                        //Channel Type Accepted and volume
                                        if($('input:checkbox[name=one_time]:checked').val()=="one_time_percentage")
                                        {
                                            document.myformname.one_time_percentage.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.one_time_percentage.disabled = true;
                                            document.myformname.one_time_percentage.value = "";
                                        }

                                        $( "input[name='one_time']").on( "click", function() {
                                            if($('input:checkbox[name=one_time]:checked').val()=="one_time_percentage")
                                            {
                                                document.myformname.one_time_percentage.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.one_time_percentage.disabled = true;
                                                document.myformname.one_time_percentage.value = "";
                                            }
                                        });
                                        if($('input:checkbox[name=moto]:checked').val()=="moto_percentage")
                                        {
                                            document.myformname.moto_percentage.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.moto_percentage.disabled = true;
                                            document.myformname.moto_percentage.value = "";
                                        }

                                        $( "input[name='moto']").on( "click", function() {
                                            if($('input:checkbox[name=moto]:checked').val()=="moto_percentage")
                                            {
                                                document.myformname.moto_percentage.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.moto_percentage.disabled = true;
                                                document.myformname.moto_percentage.value = "";
                                            }
                                        });
                                        if($('input:checkbox[name=internetpercentage]:checked').val()=="internet_percentage")
                                        {
                                            document.myformname.internet_percentage.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.internet_percentage.disabled = true;
                                            document.myformname.internet_percentage.value = "";
                                        }

                                        $( "input[name='internetpercentage']").on( "click", function() {
                                            if($('input:checkbox[name=internetpercentage]:checked').val()=="internet_percentage")
                                            {
                                                document.myformname.internet_percentage.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.internet_percentage.disabled = true;
                                                document.myformname.internet_percentage.value = "";
                                            }
                                        });

                                        if($('input:checkbox[name=swipe]:checked').val()=="swipe_percentage")
                                        {
                                            document.myformname.swipe_percentage.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.swipe_percentage.disabled = true;
                                            document.myformname.swipe_percentage.value = "";
                                        }

                                        $( "input[name='swipe']").on( "click", function() {
                                            if($('input:checkbox[name=swipe]:checked').val()=="swipe_percentage")
                                            {
                                                document.myformname.swipe_percentage.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.swipe_percentage.disabled = true;
                                                document.myformname.swipe_percentage.value = "";
                                            }
                                        });
                                        if($('input:checkbox[name=recurring]:checked').val()=="recurring_percentage")
                                        {
                                            document.myformname.recurring_percentage.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.recurring_percentage.disabled = true;
                                            document.myformname.recurring_percentage.value = "";
                                        }
                                        $( "input[name='recurring']").on( "click", function() {
                                            if($('input:checkbox[name=recurring]:checked').val()=="recurring_percentage")
                                            {

                                                document.myformname.recurring_percentage.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.recurring_percentage.disabled = true;
                                                document.myformname.recurring_percentage.value = "";
                                            }
                                        });
                                        if($('input:checkbox[name=threed_secure]:checked').val()=="threedsecure_percentage")
                                        {
                                            document.myformname.threedsecure_percentage.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.threedsecure_percentage.disabled = true;
                                            document.myformname.threedsecure_percentage.value = "";
                                        }

                                        $( "input[name='threed_secure']").on( "click", function() {
                                            if($('input:checkbox[name=threed_secure]:checked').val()=="threedsecure_percentage")
                                            {

                                                document.myformname.threedsecure_percentage.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.threedsecure_percentage.disabled = true;
                                                document.myformname.threedsecure_percentage.value = "";
                                            }
                                        });

                                        if($('input:checkbox[name=terminalType]:checked').val()=="terminal_type_otheryes" )
                                        {
                                            document.myformname.terminal_type_otheryes.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.terminal_type_otheryes.disabled = true;
                                            document.myformname.terminal_type_otheryes.value = "";
                                        }

                                        $( "input[name='terminalType']").on( "click", function() {
                                            if($('input:checkbox[name=terminalType]:checked').val()=="terminal_type_otheryes" )
                                            {
                                                document.myformname.terminal_type_otheryes.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.terminal_type_otheryes.disabled = true;
                                                document.myformname.terminal_type_otheryes.value = "";
                                            }
                                        });

                                        /*if($('input:checkbox[name=terminalType]:checked').val()=="on")
                                         {
                                         document.myformname.terminal_type_other.disabled = false;
                                         }
                                         else
                                         {
                                         document.myformname.terminal_type_other.disabled = true;
                                         document.myformname.terminal_type_other.value = "";
                                         }

                                         $( "input[name='terminalType']").on( "click", function() {
                                         if($('input:checkbox[name=terminalType]:checked').val()=="on")
                                         {
                                         document.myformname.terminal_type_other.disabled = false;
                                         }
                                         else
                                         {
                                         document.myformname.terminal_type_other.disabled = true;
                                         document.myformname.terminal_type_other.value = "";
                                         }
                                         });*/
                                        /*................End...............................*/

                                    });

                                </script>

                                <div class="form-group col-md-4 ">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                         <input type="checkbox" name="one_time" value="one_time_percentage" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getOne_time_percentage())){%> checked <%}%> />
                                        &nbsp;&nbsp;One-time</label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="one_time_percentage" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getOne_time_percentage())==true?businessProfileVO.getOne_time_percentage():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="one_time_percentage" <%=disableOneTime%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%--<%if(validationErrorList.getError("one_time_percentage")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="moto" value="moto_percentage" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getMoto_percentage())){%> checked <%}%> />
                                        &nbsp;&nbsp;Moto</label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="moto_percentage" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getMoto_percentage())==true?businessProfileVO.getMoto_percentage():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="moto_percentage" <%=disableMoto%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%--<%if(validationErrorList.getError("moto_percentage")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="internetpercentage" value="internet_percentage" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getInternet_percentage())){%> checked <%}%> />
                                        &nbsp;&nbsp;Internet</label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="internet_percentage" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getInternet_percentage())==true?businessProfileVO.getInternet_percentage():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="internet_percentage" <%=disableInternet%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%--<%if(validationErrorList.getError("internet_percentage")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                    </div>
                                        </div>
                                </div>


                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="swipe" value="swipe_percentage" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getSwipe_percentage())){%> checked <%}%> />
                                        &nbsp;&nbsp;Swipe</label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="swipe_percentage" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getSwipe_percentage())==true?businessProfileVO.getSwipe_percentage():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="swipe_percentage" <%=disableSwipe%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%--<%if(validationErrorList.getError("swipe_percentage")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="recurring" value="recurring_percentage" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getRecurring_percentage())){%> checked <%}%> />
                                        &nbsp;&nbsp;Recurring</label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="recurring_percentage" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getRecurring_percentage())==true?businessProfileVO.getRecurring_percentage():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="recurring_percentage" <%=disableRecurring%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%--<%if(validationErrorList.getError("recurring_percentage")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="threed_secure" value="threedsecure_percentage" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getThreedsecure_percentage())){%> checked <%}%> />
                                        &nbsp;&nbsp;3D Secure</label>
                                        </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                    <div class="input-group">
                                        <input type="text" id="threedsecure_percentage" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getThreedsecure_percentage())==true?businessProfileVO.getThreedsecure_percentage():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="threedsecure_percentage" <%=disableThreeDSecure%>/>
                                        <span class="input-group-addon" style="font-weight: 800;">%</span>
                                        <%--<%if(validationErrorList.getError("threedsecure_percentage")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                    </div>
                                        </div>
                                </div>

                                <div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding:0;">
                                    <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                        <input type="checkbox" name="terminalType" value="terminal_type_otheryes" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getTerminal_type_otheryes())){%> checked <%}%> />
                                        &nbsp;&nbsp;Other</label>
                                     </div>
                                    <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="terminal_type_otheryes" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getTerminal_type_otheryes())==true?businessProfileVO.getTerminal_type_otheryes():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="terminal_type_otheryes" <%=disableChannelOther%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%--<%if(validationErrorList.getError("terminal_type_otheryes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>--%>
                                        </div>
                                    </div>

                                </div>

                                <%--<div class="form-group col-md-4 has-feedback">
                                    <div class="col-md-6" style="padding: 0;">
                                        <input placeholder="Channel Type" type="text" class="form-control"  id="terminal_type_other" name="terminal_type_other" style="font-weight:bold;"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getTerminal_type_other())?businessProfileVO.getTerminal_type_other():""%>" <%=disabled%> />
                                        <%if(validationErrorList.getError("terminal_type_other")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>&lt;%&ndash;terminal_type_otheryes&ndash;%&gt;--%>

                                <div class="form-group col-md-12 has-feedback">

                                    <h2 style="<%=validationErrorList.getError("terminaltypeAll")!=null?"font-family: Open Sans; font-size: 13px; font-weight: 600;text-align: center;padding: 15px;margin-bottom: 20px;border: 1px solid transparent;border-radius: 4px;color: #474a54;background-color: #fdf7f7;border-left: 4px solid #f00;":""%>" class="errormesage" width="50%">
                                        <%=validationErrorList.getError("terminaltypeAll")!=null?"Sum Of All Channel Type Accepted and Volume should be equal to 100%":""%>
                                    </h2>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.cardvolume_visa) || businessProfileInputList.contains(BankInputName.cardvolume_mastercard)|| businessProfileInputList.contains(BankInputName.cardvolume_americanexpress)||businessProfileInputList.contains(BankInputName.cardvolume_discover)||businessProfileInputList.contains(BankInputName.cardvolume_dinner)|| businessProfileInputList.contains(BankInputName.cardvolume_rupay) || businessProfileInputList.contains(BankInputName.cardvolume_jcb) || businessProfileInputList.contains(BankInputName.cardvolume_other) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Card Types Accepted and Volume</h2>

                                <script>
                                    $(document).ready(function(){

                                        //Card Type Accepted and Volume

                                        if($('input:checkbox[name=visa]:checked').val()=="cardvolume_visa")
                                        {
                                            document.myformname.cardvolume_visa.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_visa.disabled = true;
                                            document.myformname.cardvolume_visa.value = "";
                                        }

                                        $( "input[name='visa']").on( "click", function() {
                                            if($('input:checkbox[name=visa]:checked').val()=="cardvolume_visa")
                                            {
                                                document.myformname.cardvolume_visa.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_visa.disabled = true;
                                                document.myformname.cardvolume_visa.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=mastercard]:checked').val()=="cardvolume_mastercard")
                                        {
                                            document.myformname.cardvolume_mastercard.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_mastercard.disabled = true;
                                            document.myformname.cardvolume_mastercard.value = "";
                                        }

                                        $( "input[name='mastercard']").on( "click", function() {
                                            if($('input:checkbox[name=mastercard]:checked').val()=="cardvolume_mastercard")
                                            {
                                                document.myformname.cardvolume_mastercard.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_mastercard.disabled = true;
                                                document.myformname.cardvolume_mastercard.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=americanexpress]:checked').val()=="cardvolume_americanexpress")
                                        {
                                            document.myformname.cardvolume_americanexpress.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_americanexpress.disabled = true;
                                            document.myformname.cardvolume_americanexpress.value = "";
                                        }

                                        $( "input[name='americanexpress']").on( "click", function() {
                                            if($('input:checkbox[name=americanexpress]:checked').val()=="cardvolume_americanexpress")
                                            {
                                                document.myformname.cardvolume_americanexpress.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_americanexpress.disabled = true;
                                                document.myformname.cardvolume_americanexpress.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=discover]:checked').val()=="cardvolume_discover")
                                        {
                                            document.myformname.cardvolume_discover.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_discover.disabled = true;
                                            document.myformname.cardvolume_discover.value = "";
                                        }

                                        $( "input[name='discover']").on( "click", function() {
                                            if($('input:checkbox[name=discover]:checked').val()=="cardvolume_discover")
                                            {
                                                document.myformname.cardvolume_discover.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_discover.disabled = true;
                                                document.myformname.cardvolume_discover.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=dinner]:checked').val()=="cardvolume_dinner")
                                        {
                                            document.myformname.cardvolume_dinner.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_dinner.disabled = true;
                                            document.myformname.cardvolume_dinner.value = "";
                                        }

                                        $( "input[name='dinner']").on( "click", function() {
                                            if($('input:checkbox[name=dinner]:checked').val()=="cardvolume_dinner")
                                            {
                                                document.myformname.cardvolume_dinner.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_dinner.disabled = true;
                                                document.myformname.cardvolume_dinner.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=jcb]:checked').val()=="cardvolume_jcb")
                                        {
                                            document.myformname.cardvolume_jcb.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_jcb.disabled = true;
                                            document.myformname.cardvolume_jcb.value = "";
                                        }

                                        $( "input[name='jcb']").on( "click", function() {
                                            if($('input:checkbox[name=jcb]:checked').val()=="cardvolume_jcb")
                                            {
                                                document.myformname.cardvolume_jcb.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_jcb.disabled = true;
                                                document.myformname.cardvolume_jcb.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=rupay]:checked').val()=="cardvolume_rupay")
                                        {
                                            document.myformname.cardvolume_rupay.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_rupay.disabled = true;
                                            document.myformname.cardvolume_rupay.value = "";
                                        }

                                        $( "input[name='rupay']").on( "click", function() {
                                            if($('input:checkbox[name=rupay]:checked').val()=="cardvolume_rupay")
                                            {
                                                document.myformname.cardvolume_rupay.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_rupay.disabled = true;
                                                document.myformname.cardvolume_rupay.value = "";
                                            }
                                        });


                                        if($('input:checkbox[name=other]:checked').val()=="cardvolume_other")
                                        {
                                            document.myformname.cardvolume_other.disabled = false;
                                        }
                                        else
                                        {
                                            document.myformname.cardvolume_other.disabled = true;
                                            document.myformname.cardvolume_other.value = "";
                                        }

                                        $( "input[name='other']").on( "click", function() {
                                            if($('input:checkbox[name=other]:checked').val()=="cardvolume_other")
                                            {
                                                document.myformname.cardvolume_other.disabled = false;
                                            }
                                            else
                                            {
                                                document.myformname.cardvolume_other.disabled = true;
                                                document.myformname.cardvolume_other.value = "";
                                            }
                                        });
                                        /*..................End....................................*/

                                    });

                                </script>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="visa" value="cardvolume_visa" <%=globaldisabled%> <%if(functions.isValueNull(businessProfileVO.getCardvolume_visa())){%> checked <%}%> />
                                            &nbsp;&nbsp;Visa</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_visa" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getCardvolume_visa())==true?businessProfileVO.getCardvolume_visa():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="cardvolume_visa" <%=disableVisa%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_visa")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Visa</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="mastercard" <%=globaldisabled%>  value="cardvolume_mastercard"  <%if(functions.isValueNull(businessProfileVO.getCardvolume_mastercard())){%> checked <%}%> />
                                            &nbsp;&nbsp;MasterCard</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_mastercard" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getCardvolume_mastercard())==true?businessProfileVO.getCardvolume_mastercard():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="cardvolume_mastercard" <%=disableMastercard%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_mastercard")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Master Card</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox" name="americanexpress" <%=globaldisabled%>  value="cardvolume_americanexpress"  <%if(functions.isValueNull(businessProfileVO.getCardvolume_americanexpress())){%> checked <%}%> />
                                            &nbsp;&nbsp;American Express</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_americanexpress" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getCardvolume_americanexpress())==true?businessProfileVO.getCardvolume_americanexpress():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="cardvolume_americanexpress" <%=disableAmericanexpress%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_americanexpress")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid American Express</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox"   name="discover" <%=globaldisabled%> value="cardvolume_discover" <%if(functions.isValueNull(businessProfileVO.getCardvolume_discover())){%> checked <%}%> />
                                            &nbsp;&nbsp;Discover</label>
                                           </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_discover" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getCardvolume_discover())==true?businessProfileVO.getCardvolume_discover():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="cardvolume_discover" <%=disableDiscover%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_discover")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Discover</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox"  name="dinner" <%=globaldisabled%> value="cardvolume_dinner"  <%if(functions.isValueNull(businessProfileVO.getCardvolume_dinner())){%> checked <%}%> />
                                            &nbsp;&nbsp;Diners</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_dinner" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getCardvolume_dinner())==true?businessProfileVO.getCardvolume_dinner():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="cardvolume_dinner" <%=disableDinner%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_dinner")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Diner</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox"  name="jcb" <%=globaldisabled%> value="cardvolume_jcb"  <%if(functions.isValueNull(businessProfileVO.getCardvolume_jcb())){%> checked <%}%> />
                                            &nbsp;&nbsp;JCB</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_jcb" class="form-control"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getCardvolume_jcb())==true?businessProfileVO.getCardvolume_jcb():""%>" maxlength="3" onkeypress="return isNumberKey(event)" name="cardvolume_jcb" <%=disableJcb%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_jcb")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid JCB</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox"  name="rupay" <%=globaldisabled%> value="cardvolume_rupay"  <%if(functions.isValueNull(businessProfileVO.getCardvolume_rupay())){%> checked <%}%> />
                                            &nbsp;&nbsp;Rupay</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_rupay" class="form-control" name="cardvolume_rupay" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCardvolume_rupay())==true?businessProfileVO.getCardvolume_rupay():""%>" maxlength="3" onkeypress="return isNumberKey(event)" <%=disableRupay%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_rupay")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Rupay</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <div class="col-md-6" style="padding:0;">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;padding-left: 0;">
                                            <input type="checkbox"  name="other" <%=globaldisabled%> value="cardvolume_other"  <%if(functions.isValueNull(businessProfileVO.getCardvolume_other())){%> checked <%}%> />
                                            &nbsp;&nbsp;Other</label>
                                            </div>
                                        <div class="col-md-6" id="margintop10" style="padding:0;">
                                        <div class="input-group">
                                            <input type="text" id="cardvolume_other" class="form-control" name="cardvolume_other" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCardvolume_other())==true?businessProfileVO.getCardvolume_other():""%>" maxlength="3" onkeypress="return isNumberKey(event)" <%=disableOther%>/>
                                            <span class="input-group-addon" style="font-weight: 800;">%</span>
                                            <%if(validationErrorList.getError("cardvolume_other")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Others</span><%}%>
                                        </div>
                                            </div>
                                    </div>

                                    <div class="form-group col-md-12 has-feedback">

                                        <h2 style="<%=validationErrorList.getError("cardvolumeall")!=null?"font-family: Open Sans; font-size: 13px; font-weight: 600;text-align: center;padding: 15px;margin-bottom: 20px;border: 1px solid transparent;border-radius: 4px;color: #474a54;background-color: #fdf7f7;border-left: 4px solid #f00;":""%>" class="errormesage" width="50%">
                                            <%=validationErrorList.getError("cardvolumeall")!=null?"Sum Of All Card Types Accepted and Volume should be equal to 100%":""%>
                                        </h2>
                                    </div>

                                <%
                                    }
                                %>

                                <br>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.descriptionofproducts)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label for="descriptionofproducts" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Description of products/ services sold
                                            <%-- <br>--%>(include length of service and pricing)*:
                                        </label>
                                    </div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input  class="form-control"  id="descriptionofproducts" name="descriptionofproducts" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getDescriptionofproducts())==true?businessProfileVO.getDescriptionofproducts():""%>"/><%if(validationErrorList.getError("descriptionofproducts")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.product_sold_currencies)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label for="product_sold_currencies" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">In which currency the products are sold?</label>
                                    </div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <select size="5" multiple="multiple" id="productsoldcurrency" class="form-group" <%=globaldisabled%>>
                                            <%
                                                out.println(AppFunctionUtil.getCurrencyProductSold(functions.isValueNull(businessProfileVO.getProduct_sold_currencies()) == true ? businessProfileVO.getProduct_sold_currencies() : "", ProductSoldCurrencyList));
                                            %>

                                        </select>
                                        <input type="hidden" id="product_sold_currencies" name="product_sold_currencies" value="">
                                    </div>

                                </div>
                                <%
                                    }
                                %>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.urls)|| businessProfileInputList.contains(BankInputName.descriptor) || businessProfileInputList.contains(BankInputName.ipaddress) ||
                        businessProfileInputList.contains(BankInputName.login_id)|| businessProfileInputList.contains(BankInputName.password)|| businessProfileInputList.contains(BankInputName.is_website_live)||
                        businessProfileInputList.contains(BankInputName.test_link)|| businessProfileInputList.contains(BankInputName.listfraudtools)|| businessProfileInputList.contains(BankInputName.listfraudtools_yes)|| view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_WebsiteInformation%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Website%></h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.urls)|| view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="urls" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_url%></label>
                                    <input  class="form-control" type="text" id="urls" name="urls" style="border: 1px solid #b2b2b2;font-weight:bold"<%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getUrls())==true?businessProfileVO.getUrls():""%>"/><%if(validationErrorList.getError("urls")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.descriptor) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="descriptor" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_descriptor%></label>
                                    <input  class="form-control" type="text" id="descriptor" name="descriptor" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getDescriptor())==true?businessProfileVO.getDescriptor():""%>" /><%if(validationErrorList.getError("descriptor")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.ipaddress) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="ipaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_ip%></label>
                                    <input  class="form-control" type="text" id="ipaddress" name="ipaddress" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getIpaddress())==true?businessProfileVO.getIpaddress():""%>"/><%if(validationErrorList.getError("ipaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.login_id)|| view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="login_id" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_login%></label>
                                    <input  class="form-control" type="text" id="login_id" name="login_id" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getLoginId())==true?businessProfileVO.getLoginId():""%>"/><%if(validationErrorList.getError("login_id")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.password)|| view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="password" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_password%></label>
                                    <input  class="form-control" type="password"  id="password" name="password" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPassWord())==true?businessProfileVO.getPassWord():""%>" /><%if(validationErrorList.getError("password")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if (businessProfileInputList.contains(BankInputName.is_website_live)||businessProfileInputList.contains(BankInputName.test_link)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="form-group col-md-2" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website Live</label>
                                    </div>

                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" id="is_website_live" name="is_website_live" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getIs_website_live()) ?"checked":""%>/><%if(validationErrorList.getError("is_website_live")!=null){%><%--<span class="apperrormsg">Invalid Website Live</span>--%><%}%>
                                        &nbsp;Yes
                                        &nbsp;&nbsp;<input type="radio" name="is_website_live" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getIs_website_live()) || !functions.isValueNull(businessProfileVO.getIs_website_live()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp; No &nbsp;
                                    </div>

                                    <div class="col-md-3" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;">If yes, please provide more details :</font>
                                    </div>
                                    <div class="col-md-5" style="padding-left: 0px;">
                                        <p>
                                            <input  class="form-control"  id="test_link" name="test_link" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getTest_link())==true?businessProfileVO.getTest_link():""%>" name="test_link" <%=!functions.isValueNull(businessProfileVO.getIs_website_live())||"N".equals(businessProfileVO.getIs_website_live())?"disabled":""%> /><%if(validationErrorList.getError("test_link")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>



                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>
            <%
                if(businessProfileInputList.contains(BankInputName.visa_cardlogos) || businessProfileInputList.contains(BankInputName.master_cardlogos) || businessProfileInputList.contains(BankInputName.price_displayed)|| businessProfileInputList.contains(BankInputName.companyidentifiable)|| businessProfileInputList.contains(BankInputName.clearlypresented)|| businessProfileInputList.contains(BankInputName.trackingnumber)|| businessProfileInputList.contains(BankInputName.domainsowned)|| businessProfileInputList.contains(BankInputName.domainsowned_no)|| businessProfileInputList.contains(BankInputName.transaction_currency)|| businessProfileInputList.contains(BankInputName.dynamic_descriptors)||
                        businessProfileInputList.contains(BankInputName.copyright)||businessProfileInputList.contains(BankInputName.sourcecontent)|| businessProfileInputList.contains(BankInputName.kyc_processes)||businessProfileInputList.contains(BankInputName.securitypolicy)|| businessProfileInputList.contains(BankInputName.confidentialitypolicy)||businessProfileInputList.contains(BankInputName.applicablejurisdictions)||businessProfileInputList.contains(BankInputName.privacy_anonymity_dataprotection)||businessProfileInputList.contains(BankInputName.sslsecured)||businessProfileInputList.contains(BankInputName.product_requires)||businessProfileInputList.contains(BankInputName.listfraudtools)||businessProfileInputList.contains(BankInputName.listfraudtools_yes)||businessProfileInputList.contains(BankInputName.affiliate_programs)||businessProfileInputList.contains(BankInputName.affiliate_programs_details)||businessProfileInputList.contains(BankInputName.App_Services)||businessProfileInputList.contains(BankInputName.agency_employed)
                        ||businessProfileInputList.contains(BankInputName.agency_employed_yes)|| businessProfileInputList.contains(BankInputName.customers_identification)||businessProfileInputList.contains(BankInputName.customers_identification_yes) || view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_Website1%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%
                                    if(businessProfileInputList.contains(BankInputName.visa_cardlogos) || businessProfileInputList.contains(BankInputName.master_cardlogos) || businessProfileInputList.contains(BankInputName.price_displayed)|| businessProfileInputList.contains(BankInputName.companyidentifiable)|| businessProfileInputList.contains(BankInputName.clearlypresented)|| businessProfileInputList.contains(BankInputName.trackingnumber)|| businessProfileInputList.contains(BankInputName.domainsowned)|| businessProfileInputList.contains(BankInputName.domainsowned_no)|| businessProfileInputList.contains(BankInputName.transaction_currency)|| businessProfileInputList.contains(BankInputName.dynamic_descriptors)|| view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Website_Review%></h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.visa_cardlogos)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Is the type of accepted payment methods and *card logos are clearly displayed?</label>
                                    </div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="visa_cardlogos"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getVisa_cardlogos())?"checked":""%> />
                                        &nbsp;Yes&nbsp;&nbsp;
                                        <input type="radio" name="visa_cardlogos"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getVisa_cardlogos()) || !functions.isValueNull(businessProfileVO.getVisa_cardlogos()) ?"checked":""%> />
                                        &nbsp;No
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.master_cardlogos)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Are you using updated credit/debit card logos as suggested by the respective card schemes?</label>
                                    </div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="master_cardlogos"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getMaster_cardlogos())?"checked":""%> />
                                        &nbsp;Yes&nbsp;&nbsp;
                                        <input type="radio" name="master_cardlogos"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getMaster_cardlogos()) || !functions.isValueNull(businessProfileVO.getMaster_cardlogos()) ?"checked":""%> />
                                        &nbsp;No
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.price_displayed)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_price%></label>
                                    </div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="price_displayed"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getPrice_displayed())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes4%>&nbsp;&nbsp;
                                        <input type="radio" name="price_displayed"   <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getPrice_displayed()) || !functions.isValueNull(businessProfileVO.getPrice_displayed()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No4%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.companyidentifiable)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_company%></label></div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="companyidentifiable"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getCompanyIdentifiable())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes5%>&nbsp;&nbsp;
                                        <input type="radio" name="companyidentifiable"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getCompanyIdentifiable()) || !functions.isValueNull(businessProfileVO.getCompanyIdentifiable()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No5%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.clearlypresented)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_address%></label></div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="clearlypresented"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getClearlyPresented())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes6%>&nbsp;&nbsp;
                                        <input type="radio" name="clearlypresented"  <%=globaldisabled%>  value="N" <%="N".equals(businessProfileVO.getClearlyPresented()) || !functions.isValueNull(businessProfileVO.getClearlyPresented()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No6%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.trackingnumber)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_tracking%></label></div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="trackingnumber"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getTrackingNumber())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes7%>&nbsp;&nbsp;
                                        <input type="radio" name="trackingnumber"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getTrackingNumber()) || !functions.isValueNull(businessProfileVO.getTrackingNumber()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No7%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.domainsowned)||businessProfileInputList.contains(BankInputName.domainsowned_no)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;margin-bottom: 0;">

                                    <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_domain%></label>
                                    </div>


                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" name="domainsowned" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getDomainsOwned())?"checked":""%>/><%if(validationErrorList.getError("domainsowned")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid Domains Owned</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes8%>
                                        &nbsp;&nbsp;<input type="radio" name="domainsowned" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getDomainsOwned()) || !functions.isValueNull(businessProfileVO.getDomainsOwned()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No8%> &nbsp;
                                    </div>

                                    <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_details%></font>
                                    </div>
                                    <div class="col-md-2" style="padding-left: 0px;">
                                        <p>
                                            <input type="text" class="form-control" <%=globaldisabled%> name="domainsowned_no" value="<%=functions.isValueNull(businessProfileVO.getDomainsOwned_no())?businessProfileVO.getDomainsOwned_no():""%>" <%=!functions.isValueNull(businessProfileVO.getDomainsOwned())||"N".equals(businessProfileVO.getDomainsOwned())?"disabled":""%> /><%if(validationErrorList.getError("domainsowned_no")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>



                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.transaction_currency)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_currency%></label></div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="transaction_currency"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getTransaction_currency())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes9%>&nbsp;&nbsp;
                                        <input type="radio" name="transaction_currency"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getTransaction_currency()) || !functions.isValueNull(businessProfileVO.getTransaction_currency()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No9%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.dynamic_descriptors)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_dynamic%></label></div>
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <input type="radio" name="dynamic_descriptors"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getDynamic_descriptors())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes10%>&nbsp;&nbsp;
                                        <input type="radio" name="dynamic_descriptors"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getDynamic_descriptors()) || !functions.isValueNull(businessProfileVO.getDynamic_descriptors()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No10%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.cardholder_asked)|| businessProfileInputList.contains(BankInputName.shopping_cart)||businessProfileInputList.contains(BankInputName.shopping_cart_details)||businessProfileInputList.contains(BankInputName.threeD_secure_compulsory)||businessProfileInputList.contains(BankInputName.orderconfirmation_post) || businessProfileInputList.contains(BankInputName.orderconfirmation_email) || businessProfileInputList.contains(BankInputName.orderconfirmation_sms) || businessProfileInputList.contains(BankInputName.orderconfirmation_other) || businessProfileInputList.contains(BankInputName.orderconfirmation_other_yes) ||
                                            businessProfileInputList.contains(BankInputName.kyc_processes)||businessProfileInputList.contains(BankInputName.payment_delivery)|| businessProfileInputList.contains(BankInputName.payment_delivery_otheryes)||businessProfileInputList.contains(BankInputName.sslsecured)||businessProfileInputList.contains(BankInputName.product_requires)||businessProfileInputList.contains(BankInputName.customers_identification)||businessProfileInputList.contains(BankInputName.customers_identification_yes)||view)
                                    {
                                %>


                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=businessprofile_Purchase_Process%></h2>

                                <div class="widget-content padding">
                                    <div id="horizontal-form">

                                        <%
                                            if(businessProfileInputList.contains(BankInputName.shopping_cart)||businessProfileInputList.contains(BankInputName.shopping_cart_details)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">


                                            <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_shopping%></label>
                                            </div>


                                            <div class="col-md-2" style="padding: 0;">
                                                <input type="radio" name="shopping_cart" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getShopping_cart())?"checked":""%>/><%if(validationErrorList.getError("shopping_cart")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid shopping_cart</span>--%><%}%>
                                                &nbsp;<%=businessprofile_Yes11%>
                                                &nbsp;&nbsp;<input type="radio" name="shopping_cart" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getShopping_cart()) || !functions.isValueNull(businessProfileVO.getShopping_cart()) ?"checked":""%> /> <%--Condition for radio button--%>
                                                &nbsp;<%=businessprofile_No11%> &nbsp;
                                            </div>

                                            <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                                <font style="text-align: right;"><%=businessprofile_provide%></font>
                                            </div>
                                            <div class="col-md-2" style="padding-left: 0px;">
                                                <p>
                                                    <input type="text" class="form-control" <%=globaldisabled%> name="shopping_cart_details" value="<%=functions.isValueNull(businessProfileVO.getShopping_cart_details())?businessProfileVO.getShopping_cart_details():""%>" <%=!functions.isValueNull(businessProfileVO.getShopping_cart())||"N".equals(businessProfileVO.getShopping_cart())?"disabled":""%> /><%if(validationErrorList.getError("shopping_cart_details")!=null){%><%--<span style="width:18%" class="apperrormsg">Invalid shopping_cart_details</span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                                </p>
                                            </div>



                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.threeD_secure_compulsory)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_secure%></label></div>
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <input type="radio" name="threeD_secure_compulsory"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getThreeD_secure_compulsory())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes12%>&nbsp;&nbsp;
                                                <input type="radio" name="threeD_secure_compulsory" <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getThreeD_secure_compulsory()) || !functions.isValueNull(businessProfileVO.getThreeD_secure_compulsory()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No12%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.orderconfirmation_post) || businessProfileInputList.contains(BankInputName.orderconfirmation_email) || businessProfileInputList.contains(BankInputName.orderconfirmation_sms) || businessProfileInputList.contains(BankInputName.orderconfirmation_other) || businessProfileInputList.contains(BankInputName.orderconfirmation_other_yes) || view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="padding: 0;">
                                            <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">After purchase do you send order confirmation to the cardholder? (please provide us with a copy):</label>
                                        </div>

                                        <div class="row">
                                            <div class="form-group col-lg-4 col-md-4 has-feedback">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding: 5px;">
                                                    <input type="checkbox" name="orderconfirmation_post" <%="Y".equals(businessProfileVO.getOrderconfirmation_post())?"checked":""%> value ="Y" <%=globaldisabled%> />
                                                    &nbsp;By Post</label>
                                            </div>
                                            <div class="form-group col-lg-4 col-md-4 has-feedback">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding: 5px;">
                                                    <input type="checkbox"   name="orderconfirmation_email" <%=globaldisabled%> <%="Y".equals(businessProfileVO.getOrderconfirmation_email())?"checked":""%> value ="Y" />
                                                    &nbsp;By Email</label>
                                            </div>
                                            <div class="form-group col-lg-4 col-md-4 has-feedback">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding: 5px;">
                                                    <input type="checkbox"   name="orderconfirmation_sms" <%=globaldisabled%>  value="Y" <%="Y".equals(businessProfileVO.getOrderconfirmation_sms())?"checked":""%> />
                                                    &nbsp;By SMS</label>
                                            </div>

                                            <div class="form-group col-lg-2 col-md-2 has-feedback" id="orderconfirmation_div">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding: 5px;">
                                                    <input type="checkbox"   name="orderconfirmation_other" <%=globaldisabled%>  value="Y" <%="Y".equals(businessProfileVO.getOrderconfirmation_other())?"checked":""%> />
                                                    &nbsp;Other&nbsp;&nbsp;
                                                </label>
                                            </div>
                                            <div class="form-group col-lg-4 col-md-4 has-feedback">
                                                <input type="text" id="orderconfirmation_other_yes" style="cursor: initial;" class="checkbox-inline form-control" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getOrderconfirmation_other_yes())==true?businessProfileVO.getOrderconfirmation_other_yes():""%>" name="orderconfirmation_other_yes" <%=!functions.isValueNull(businessProfileVO.getOrderconfirmation_other())?"disabled":""%> /><%if(validationErrorList.getError("orderconfirmation_other_yes")!=null){%><%--<span class="apperrormsg" style="text-align: center;">Invalid Input</span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                            </div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.cardholder_asked)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_cardholder%></label></div>
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <input type="radio" name="cardholder_asked"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getCardholder_asked())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes13%>&nbsp;&nbsp;
                                                <input type="radio" name="cardholder_asked"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getCardholder_asked()) || !functions.isValueNull(businessProfileVO.getCardholder_asked()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No13%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.customers_identification)||(businessProfileInputList.contains(BankInputName.customers_identification_yes)|| view))
                                            {
                                        %>
                                        <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">

                                            <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_verify%></label>
                                            </div>


                                            <div class="col-md-2" style="padding: 0;">
                                                <input type="radio" name="customers_identification" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getCustomers_identification())?"checked":""%>/><%if(validationErrorList.getError("customers_identification")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid Customer Identification</span>--%><%}%>
                                                &nbsp;<%=businessprofile_Yes14%>
                                                &nbsp;&nbsp;<input type="radio" name="customers_identification" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getCustomers_identification()) || !functions.isValueNull(businessProfileVO.getCustomers_identification()) ?"checked":""%> /> <%--Condition for radio button--%>
                                                &nbsp;<%=businessprofile_No14%> &nbsp;
                                            </div>

                                            <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                                <font style="text-align: right;"><%=businessprofile_please2%></font>
                                            </div>
                                            <div class="col-md-2" style="padding-left: 0px;">
                                                <p>
                                                    <input type="text" class="form-control has-feedback" name="customers_identification_yes"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCustomers_identification_yes())==true?businessProfileVO.getCustomers_identification_yes():""%>" name="customers_identification_yes" <%=!functions.isValueNull(businessProfileVO.getCustomers_identification())||"N".equals(businessProfileVO.getCustomers_identification())?"disabled":""%> /><%if(validationErrorList.getError("customers_identification_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                                </p>
                                            </div>

                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.kyc_processes)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_kyc%></label></div>
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <input type="radio" name="kyc_processes"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getKyc_processes())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes15%>&nbsp;&nbsp;
                                                <input type="radio" name="kyc_processes"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getKyc_processes()) || !functions.isValueNull(businessProfileVO.getKyc_processes()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No15%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.payment_delivery)|| businessProfileInputList.contains(BankInputName.payment_delivery_otheryes)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12 "style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-2" style="padding-left: 0;">
                                                <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">When does the payment take place?</label>
                                            </div>

                                            <div class="form-group col-md-4 col-lg-2 has-feedback" style="padding: 0;">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                                    <input type="radio"   name="payment_delivery" <%=globaldisabled%> value="upon_purchase" <%="upon_purchase".equals(businessProfileVO.getPayment_delivery())  || !functions.isValueNull(businessProfileVO.getPayment_delivery()) ?"checked":""%>/>
                                                    &nbsp;Upon Purchase</label>
                                            </div>
                                            <div class="form-group col-md-4 col-lg-2" style="padding: 0;">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">

                                                    <input type="radio" name="payment_delivery" <%=globaldisabled%> value="on_delivery" <%="on_delivery".equals(businessProfileVO.getPayment_delivery()) ?"checked":""%> />
                                                    &nbsp;On Delivery</label>
                                            </div>
                                            <div class="form-group col-md-4 col-lg-2 has-feedback" style="padding: 0;">
                                                <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;padding-left: 0;">
                                                    <input type="radio"  name="payment_delivery" <%=globaldisabled%> value="with_download" <%="with_download".equals(businessProfileVO.getPayment_delivery()) ?"checked":""%> />
                                                    &nbsp;Download</label>
                                            </div>



                                            <div class="form-group col-md-6 col-lg-4" style="padding: 0;">

                                                <div class="col-md-3" style="padding: 0;">
                                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                                        <input type="radio" name="payment_delivery" <%=globaldisabled%> value="payment_delivery_other" <%="payment_delivery_other".equals(businessProfileVO.getPayment_delivery()) ?"checked":""%>  />
                                                        &nbsp;Other
                                                    </label>
                                                </div>

                                                <div class="col-md-9" style="padding: 0;" id="margintop10">
                                                    <input type="text" class="form-control"   id="payment_delivery_otheryes" name="payment_delivery_otheryes" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=functions.isValueNull(businessProfileVO.getPayment_delivery_otheryes())==true?businessProfileVO.getPayment_delivery_otheryes():""%>" name="payment_delivery_otheryes" <%=!functions.isValueNull(businessProfileVO.getPayment_delivery())||"payment_delivery_other".equals(businessProfileVO.getPayment_delivery())?"":"disabled"%> /><%if(validationErrorList.getError("payment_delivery_otheryes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                                </div>

                                            </div>



                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.sslsecured)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_ssl%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="sslsecured"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getSslSecured())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes16%>&nbsp;&nbsp;
                                                <input type="radio" name="sslsecured"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getSslSecured()) || !functions.isValueNull(businessProfileVO.getSslSecured()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No16%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.product_requires)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_product%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="product_requires"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getProduct_requires())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes17%>&nbsp;&nbsp;
                                                <input type="radio" name="product_requires"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getProduct_requires()) || !functions.isValueNull(businessProfileVO.getProduct_requires()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No17%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                    </div>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.affiliate_programs)||(businessProfileInputList.contains(BankInputName.affiliate_programs_details)|| view))
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Affiliate%></h2>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                                    <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_operate%></label>
                                    </div>


                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" name="affiliate_programs" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getAffiliate_programs())?"checked":""%>/><%if(validationErrorList.getError("affiliate_programs")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid affiliate_programs</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes18%>
                                        &nbsp;&nbsp;<input type="radio" name="affiliate_programs" <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getAffiliate_programs()) || !functions.isValueNull(businessProfileVO.getAffiliate_programs()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No18%> &nbsp;
                                    </div>

                                    <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_detail%></font>
                                    </div>
                                    <div class="col-md-2" style="padding-left: 0px;">
                                        <p>
                                            <input type="text" class="form-control has-feedback" name="affiliate_programs_details"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getAffiliate_programs_details())==true?businessProfileVO.getAffiliate_programs_details():""%>" name="affiliate_programs_details" <%=!functions.isValueNull(businessProfileVO.getAffiliate_programs())||"N".equals(businessProfileVO.getAffiliate_programs())?"disabled":""%> /><%if(validationErrorList.getError("affiliate_programs_details")!=null){%><%--<span style="width:18%" class="apperrormsg">Invalid affiliate_programs_details</span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>

                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.copyright)||businessProfileInputList.contains(BankInputName.sourcecontent)||view)
                                    {
                                %>

                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"><%=businessprofile_Content%></h2>


                                        <%
                                            if(businessProfileInputList.contains(BankInputName.copyright)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-4" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_copyrighted%></label>
                                            </div>
                                            <div class="col-md-4" style="padding: 0;">
                                                &nbsp;&nbsp;<input type="radio" name="copyright"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getCopyright())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes19%>&nbsp;
                                                &nbsp;&nbsp;<input type="radio" name="copyright"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getCopyright()) || !functions.isValueNull(businessProfileVO.getCopyright()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No19%>
                                            </div>

                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.sourcecontent)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12 has-feedback" style="padding: 0;">

                                            <div class="col-md-4" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_source%></label>
                                            </div>

                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="text" class="form-control" name="sourcecontent" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getSourceContent())==true?businessProfileVO.getSourceContent():""%>" name="sourcecontent" /> <%if(validationErrorList.getError("sourcecontent")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                            </div>
                                        </div>


                                        <%
                                            }
                                        %>


                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.securitypolicy)|| businessProfileInputList.contains(BankInputName.confidentialitypolicy)||businessProfileInputList.contains(BankInputName.applicablejurisdictions)||businessProfileInputList.contains(BankInputName.privacy_anonymity_dataprotection)||businessProfileInputList.contains(BankInputName.listfraudtools)||businessProfileInputList.contains(BankInputName.listfraudtools_yes)||businessProfileInputList.contains(BankInputName.App_Services)||businessProfileInputList.contains(BankInputName.agency_employed)||businessProfileInputList.contains(BankInputName.agency_employed_yes)||view)
                                    {
                                %>


                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Security%></h2>



                                        <%
                                            if(businessProfileInputList.contains(BankInputName.securitypolicy)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_cookies%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="securitypolicy"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getSecuritypolicy())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes20%>&nbsp;&nbsp;
                                                <input type="radio" name="securitypolicy"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getSecuritypolicy()) || !functions.isValueNull(businessProfileVO.getSecuritypolicy()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No20%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.confidentialitypolicy)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_customers%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="confidentialitypolicy"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getConfidentialitypolicy())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes21%>&nbsp;&nbsp;
                                                <input type="radio" name="confidentialitypolicy"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getConfidentialitypolicy()) || !functions.isValueNull(businessProfileVO.getConfidentialitypolicy()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No21%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.applicablejurisdictions)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_responsibilities%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="applicablejurisdictions"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getApplicablejurisdictions())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes22%>&nbsp;&nbsp;
                                                <input type="radio" name="applicablejurisdictions"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getApplicablejurisdictions()) || !functions.isValueNull(businessProfileVO.getApplicablejurisdictions()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No22%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.privacy_anonymity_dataprotection)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_privacy%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="privacy_anonymity_dataprotection"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getPrivacy_anonymity_dataprotection())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes23%>&nbsp;&nbsp;
                                                <input type="radio" name="privacy_anonymity_dataprotection"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getPrivacy_anonymity_dataprotection()) || !functions.isValueNull(businessProfileVO.getPrivacy_anonymity_dataprotection()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No23%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.App_Services)|| view)
                                            {
                                        %>

                                        <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">
                                            <div class="col-md-6" style="padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_application%></label></div>
                                            <div class="col-md-6" style="padding: 0;">
                                                <input type="radio" name="App_Services"   <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getApp_Services())?"checked":""%> />
                                                &nbsp;<%=businessprofile_Yes24%>&nbsp;&nbsp;
                                                <input type="radio" name="App_Services"  <%=globaldisabled%>  value="N" <%="N".equals(businessProfileVO.getApp_Services()) || !functions.isValueNull(businessProfileVO.getApp_Services()) ?"checked":""%> />
                                                &nbsp;<%=businessprofile_No24%></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <%
                                            if(businessProfileInputList.contains(BankInputName.listfraudtools)||businessProfileInputList.contains(BankInputName.listfraudtools_yes)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">

                                            <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_risk%></label>
                                            </div>


                                            <div class="col-md-2" style="padding: 0;">
                                                <input type="radio" name="listfraudtools" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getListfraudtools())?"checked":""%>/><%if(validationErrorList.getError("listfraudtools")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid Fraud Risk Matter</span>--%><%}%>
                                                &nbsp;<%=businessprofile_Yes25%>
                                                &nbsp;&nbsp;<input type="radio" name="listfraudtools" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getListfraudtools()) || !functions.isValueNull(businessProfileVO.getListfraudtools()) ?"checked":""%> /> <%--Condition for radio button--%>
                                                &nbsp;<%=businessprofile_No25%> &nbsp;
                                            </div>

                                            <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                                <font style="text-align: right;"><%=businessprofile_please3%></font>
                                            </div>
                                            <div class="col-md-2" style="padding-left: 0px;">
                                                <p>
                                                    <input type="text" class="form-control" <%=globaldisabled%> name="listfraudtools_yes" value="<%=functions.isValueNull(businessProfileVO.getListfraudtools_yes())?businessProfileVO.getListfraudtools_yes():""%>" <%=!functions.isValueNull(businessProfileVO.getListfraudtools())||"N".equals(businessProfileVO.getListfraudtools())?"disabled":""%> /><%if(validationErrorList.getError("listfraudtools_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                                </p>
                                            </div>

                                        </div>
                                        <%
                                            }
                                        %>

                                        <%
                                            if(businessProfileInputList.contains(BankInputName.agency_employed)||businessProfileInputList.contains(BankInputName.agency_employed_yes)|| view)
                                            {
                                        %>
                                        <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding: 0;">

                                            <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                                <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Has a collection agency been employed or an affirmation been made in lieu of an oath in the last 5 year?</label>
                                            </div>


                                            <div class="col-md-2" style="padding: 0;">
                                                <input type="radio" name="agency_employed" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getAgency_employed())?"checked":""%>/><%if(validationErrorList.getError("agency_employed")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid Agency Employed</span>--%><%}%>
                                                &nbsp;Yes
                                                &nbsp;&nbsp;<input type="radio" name="agency_employed" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getAgency_employed()) || !functions.isValueNull(businessProfileVO.getAgency_employed()) ?"checked":""%> /> <%--Condition for radio button--%>
                                                &nbsp;No &nbsp;
                                            </div>

                                            <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                                <font style="text-align: right;">If yes, please provide more details* :</font>
                                            </div>
                                            <div class="col-md-2" style="padding: 0px;">
                                                <p>
                                                    <input type="text" class="form-control" <%=globaldisabled%> name="agency_employed_yes" value="<%=functions.isValueNull(businessProfileVO.getAgency_employed_yes())?businessProfileVO.getAgency_employed_yes():""%>" <%=!functions.isValueNull(businessProfileVO.getAgency_employed())||"N".equals(businessProfileVO.getAgency_employed())?"disabled":""%> /><%if(validationErrorList.getError("agency_employed_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                                </p>
                                            </div>


                                        </div>

                                        <%
                                            }
                                        %>

                                <%
                                    }
                                %>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.pricing_policies_website)||businessProfileInputList.contains(BankInputName.pricing_policies_website_yes)||businessProfileInputList.contains(BankInputName.fulfillment_timeframe)|| businessProfileInputList.contains(BankInputName.goods_policy)|| businessProfileInputList.contains(BankInputName.countries_blocked) || businessProfileInputList.contains(BankInputName.countries_blocked_details)|| businessProfileInputList.contains(BankInputName.inhouselocation)|| businessProfileInputList.contains(BankInputName.contactperson)|| businessProfileInputList.contains(BankInputName.otherlocation)|| businessProfileInputList.contains(BankInputName.mainsuppliers)|| businessProfileInputList.contains(BankInputName.shipmentassured)|| businessProfileInputList.contains(BankInputName.goods_delivery) || view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_warehouse%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Shipping%></h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.physicalgoods_delivered) || businessProfileInputList.contains(BankInputName.viainternetgoods_delivered) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Goods being delivered and sold</label>
                                </div>
                                <div class="row" style="margin-left: 0%;">
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="checkbox" name="physicalgoods_delivered" <%="Y".equals(businessProfileVO.getPhysicalgoods_delivered())?"checked":""%> value ="Y" <%=globaldisabled%> />
                                            &nbsp;Physical Goods</label>
                                    </div>
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="checkbox" name="viainternetgoods_delivered" <%="Y".equals(businessProfileVO.getViainternetgoods_delivered())?"checked":""%> value ="Y" <%=globaldisabled%> />
                                            &nbsp;Digital Content Via Internet</label>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.pricing_policies_website)|| businessProfileInputList.contains(BankInputName.pricing_policies_website_yes)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                                    <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_policies%></label>
                                    </div>


                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" id="pricing_policies_website" name="pricing_policies_website" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getPricing_policies_website())?"checked":""%>/><%if(validationErrorList.getError("pricing_policies_website")!=null){%><%--<span class="apperrormsg">Invalid Shipping and Pricing Policies</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes26%>
                                        &nbsp;&nbsp;<input type="radio" name="pricing_policies_website" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getPricing_policies_website()) || !functions.isValueNull(businessProfileVO.getPricing_policies_website()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No26%>&nbsp;
                                    </div>

                                    <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_please4%></font>
                                    </div>
                                    <div class="col-md-2" style="padding-left: 0px;">
                                        <p>
                                            <input  class="form-control"  id="pricing_policies_website_yes" name="pricing_policies_website_yes" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPricing_policies_website_yes())==true?businessProfileVO.getPricing_policies_website_yes():""%>" name="pricing_policies_website_yes" <%=!functions.isValueNull(businessProfileVO.getPricing_policies_website())||"N".equals(businessProfileVO.getPricing_policies_website())?"disabled":""%> /><%if(validationErrorList.getError("pricing_policies_website_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>

                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.fulfillment_timeframe)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_timeframe%></label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio" name="fulfillment_timeframe"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getFulfillment_timeframe())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes27%>&nbsp;&nbsp;
                                        <input type="radio" name="fulfillment_timeframe"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getFulfillment_timeframe()) || !functions.isValueNull(businessProfileVO.getFulfillment_timeframe()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No27%></div>

                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.countries_blocked) || businessProfileInputList.contains(BankInputName.countries_blocked_details)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">

                                    <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_exclusive%></label>
                                    </div>

                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" name="countries_blocked" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getCountries_blocked())?"checked":""%>/><%if(validationErrorList.getError("countries_blocked")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid countries_blocked</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes28%>
                                        &nbsp;&nbsp;<input type="radio" name="countries_blocked" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getCountries_blocked()) || !functions.isValueNull(businessProfileVO.getCountries_blocked()) ?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No28%> &nbsp;
                                    </div>

                                    <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_provide2%></font>
                                    </div>
                                    <div class="col-md-2" style="padding-left: 0px;">
                                        <p>
                                            <input type="text" class="form-control" name="countries_blocked_details"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCountries_blocked_details())==true?businessProfileVO.getCountries_blocked_details():""%>" name="countries_blocked_details" <%=!functions.isValueNull(businessProfileVO.getCountries_blocked())||"N".equals(businessProfileVO.getCountries_blocked())?"disabled":""%> /><%if(validationErrorList.getError("countries_blocked_details")!=null){%><%--<span style="width:18%" class="apperrormsg">Invalid countries_blocked_details</span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>

                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.contactperson)|| view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="contactperson" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_Contact_person%></label>
                                    <input type="text" class="form-control"   id="contactperson" name="contactperson" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getContactPerson())==true?businessProfileVO.getContactPerson():""%>" <%=disabled%>/><%if(validationErrorList.getError("contactperson")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.shipping_contactemail)|| view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="shipping_contactemail" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_ContactEmail%></label>
                                    <input type="text" class="form-control"   id="shipping_contactemail" name="shipping_contactemail" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShippingContactemail())==true?businessProfileVO.getShippingContactemail():""%>" <%=disabled%>/><%if(validationErrorList.getError("shipping_contactemail")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.inhouselocation)|| view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="inhouselocation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_location%></label>
                                    <input type="text" class="form-control"   id="inhouselocation" name="inhouselocation" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getInHouseLocation())==true?businessProfileVO.getInHouseLocation():""%>" <%=disabled%>/><%if(validationErrorList.getError("inhouselocation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.otherlocation)|| view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="otherlocation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_house%></label>
                                    <input type="text" class="form-control"   id="otherlocation" name="otherlocation" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getOtherLocation())==true?businessProfileVO.getOtherLocation():""%>" <%=disabled%>/><%if(validationErrorList.getError("otherlocation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.mainsuppliers)|| view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="mainsuppliers" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_suppliers%></label>
                                    <input type="text" class="form-control"   id="mainsuppliers" name="mainsuppliers" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getMainSuppliers())==true?businessProfileVO.getMainSuppliers():""%>" <%=disabled%>/><%if(validationErrorList.getError("mainsuppliers")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.shipmentassured)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 "style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_shippment%></label>
                                    </div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio" name="shipmentassured"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getShipmentAssured())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes29%>&nbsp;
                                        &nbsp;&nbsp;<input type="radio" name="shipmentassured"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getShipmentAssured()) || !functions.isValueNull(businessProfileVO.getShipmentAssured()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No29%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.isafulfillmenthouseused)||businessProfileInputList.contains(BankInputName.isafulfillmenthouseused_yes)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">


                                    <div class="form-group col-md-6" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_fulfillment%></label>
                                    </div>


                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" name="isafulfillmenthouseused" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getIsafulfillmenthouseused())?"checked":""%>/><%if(validationErrorList.getError("isafulfillmenthouseused")!=null){%><%--<span class="apperrormsg">Invalid Is a Fulfillment House used</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes30%>
                                        &nbsp;&nbsp;<input type="radio" name="isafulfillmenthouseused" value="N" <%=globaldisabled%> <%="N".equals(businessProfileVO.getIsafulfillmenthouseused()) || !functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused())?"checked":""%> /> <%--Condition for radio button--%>
                                        &nbsp;<%=businessprofile_No30%> &nbsp;
                                    </div>

                                    <div class="col-md-2" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_details4%></font>
                                    </div>
                                    <div class="col-md-2" style="padding-left: 0px;">
                                        <p>
                                            <input type="text" class="form-control" style="width:100%"  <%=globaldisabled%>
                                                   value="<%=functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused_yes())==true?businessProfileVO.getIsafulfillmenthouseused_yes():""%>" name="isafulfillmenthouseused_yes" <%=!functions.isValueNull(businessProfileVO.getIsafulfillmenthouseused())||"N".equals(businessProfileVO.getIsafulfillmenthouseused())?"disabled":""%> /><%if(validationErrorList.getError("isafulfillmenthouseused_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                        </p>
                                    </div>

                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.goods_delivery)  || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Delivery time for goods/service</u></label>
                                </div>
                                <div class="form-group col-md-4 col-lg-3 has-feedback">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="radio"   name="goods_delivery" <%=globaldisabled%> value="over_internet" <%="over_internet".equals(businessProfileVO.getGoods_delivery())?"checked":""%>/>
                                        &nbsp;Over the Internet</label>
                                </div>
                                <div class="form-group col-md-4 col-lg-3">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="radio"  name="goods_delivery" <%=globaldisabled%> value="1_5days" <%="1_5days".equals(businessProfileVO.getGoods_delivery())?"checked":""%> />
                                        &nbsp;1-5 days</label>
                                </div>
                                <div class="form-group col-md-4 col-lg-3 has-feedback">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;padding-left:0;">
                                        <input type="radio" name="goods_delivery" <%=globaldisabled%> value="6_13days"<%="6_13days".equals(businessProfileVO.getGoods_delivery())?"checked":""%> />
                                        &nbsp;6-13 days</label>
                                </div>
                                <div class="form-group col-md-4 col-lg-3">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="radio" name="goods_delivery" <%=globaldisabled%> value="14days" <%="14days".equals(businessProfileVO.getGoods_delivery()) || !functions.isValueNull(businessProfileVO.getGoods_delivery()) ?"checked":""%>  />
                                        &nbsp;<14 days</label>
                                </div>
                                <br><br>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.coolingoffperiod)|| businessProfileInputList.contains(BankInputName.goods_policy) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;"><%=businessprofile_Returns%></h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.coolingoffperiod)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label for="coolingoffperiod" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_cancellation%></label>
                                    </div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="text" class="form-control"   id="coolingoffperiod" name="coolingoffperiod" style="border: 1px solid #b2b2b2;font-weight:bold;"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCoolingoffperiod())==true?businessProfileVO.getCoolingoffperiod():""%>" <%=disabled%>/><%if(validationErrorList.getError("coolingoffperiod")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.goods_policy)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_goods%></label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio" name="goods_policy"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getGoods_policy())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes31%>&nbsp;&nbsp;
                                        <input type="radio" name="goods_policy"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getGoods_policy()) || !functions.isValueNull(businessProfileVO.getGoods_policy()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No31%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.customer_support)||businessProfileInputList.contains(BankInputName.customersupport_email)||businessProfileInputList.contains(BankInputName.custsupportwork_hours)||businessProfileInputList.contains(BankInputName.timeframe)||businessProfileInputList.contains(BankInputName.technical_contact)|| businessProfileInputList.contains(BankInputName.livechat) || businessProfileInputList.contains(BankInputName.livechat)|| view)
                {
            %>

            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_Customer%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-3" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_support%></label>
                                    </div>
                                    <div class="col-md-2" style="padding: 0;">
                                        <input type="radio" name="customer_support"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getCustomer_support())?"checked":""%>/><%if(validationErrorList.getError("customer_support")!=null){%><%--<span style="width:13%" class="apperrormsg">Invalid customer_support</span>--%><%}%>
                                        &nbsp;<%=businessprofile_Yes32%>
                                        &nbsp; &nbsp; <input type="radio" name="customer_support"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getCustomer_support()) || !functions.isValueNull(businessProfileVO.getCustomer_support()) ?"checked":""%> />           <%--Condition for Radio--%>
                                        &nbsp;<%=businessprofile_No32%>&nbsp; &nbsp;&nbsp; &nbsp;
                                    </div>

                                    <div class="col-md-7" style="padding: 0px;" id="margintop10">
                                        <font style="text-align: right;"><%=businessprofile_please5%></font>
                                    </div>

                                </div>


                                <div class="form-group col-md-6 has-feedback">
                                    <label for="customersupport_email" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">24/7 <%=businessprofile_customer%></label>
                                    <input type="text" class="form-control"   id="customersupport_email" name="customersupport_email" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCustomersupport_email())==true?businessProfileVO.getCustomersupport_email():""%>" <%=customerSupportdisabled%>/><%if(validationErrorList.getError("customersupport_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label for="custsupportwork_hours" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_hours%></label>
                                    <input type="text" class="form-control"   id="custsupportwork_hours" name="custsupportwork_hours" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCustsupportwork_hours())==true?businessProfileVO.getCustsupportwork_hours():""%>" <%=customerSupportdisabled%>/><%if(validationErrorList.getError("custsupportwork_hours")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label for="timeframe" style= "font-family:Open Sans;font-size: 13px;font-weight: 600;"> <%=businessprofile_time%></label>
                                    <input type="text" class="form-control"   id="timeframe" name="timeframe" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getTimeframe())==true?businessProfileVO.getTimeframe():""%>" <%=customerSupportdisabled%>/><%if(validationErrorList.getError("timeframe")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label for="technical_contact" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_phoneno%></label>
                                    <input type="text" class="form-control "   id="technical_contact" name="technical_contact" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getTechnical_contact())==true?businessProfileVO.getTechnical_contact():""%>" <%=customerSupportdisabled%>/><%if(validationErrorList.getError("technical_contact")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0px;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_live%></label>
                                    </div>
                                    <div class="col-md-6" style="padding: 0px;">
                                        <input type="radio" name="livechat"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getLivechat())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes33%>
                                        &nbsp; &nbsp;<input type="radio" name="livechat"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getLivechat()) || !functions.isValueNull(businessProfileVO.getLivechat()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No33%>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.directmail)|| businessProfileInputList.contains(BankInputName.Yellowpages)|| businessProfileInputList.contains(BankInputName.radiotv)|| businessProfileInputList.contains(BankInputName.internet)|| businessProfileInputList.contains(BankInputName.networking)|| businessProfileInputList.contains(BankInputName.outboundtelemarketing) || view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_market%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%
                                    if(businessProfileInputList.contains(BankInputName.directmail)||businessProfileInputList.contains(BankInputName.Yellowpages)||businessProfileInputList.contains(BankInputName.radiotv)||businessProfileInputList.contains(BankInputName.internet)||businessProfileInputList.contains(BankInputName.networking)||businessProfileInputList.contains(BankInputName.outboundtelemarketing)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_strategy%></label>
                                </div>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.directmail)|| view)
                                    {
                                %>
                                <div class="form-group col-lg-4 col-md-4">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                        <input type="checkbox"  name="directmail" <%=globaldisabled%> value="Y"  <%="Y".equals(businessProfileVO.getDirectMail())?"checked":""%> />&nbsp;<%=businessprofile_DirectMail%></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.Yellowpages)|| view)
                                    {
                                %>
                                <div class="form-group col-lg-4 col-md-4">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                        <input type="checkbox" name="Yellowpages" <%=globaldisabled%> value="Y"  <%="Y".equals(businessProfileVO.getYellowPages())?"checked":""%> />&nbsp;<%=businessprofile_Yellowpages%></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.radiotv)|| view)
                                    {
                                %>

                                <div class="form-group col-lg-4 col-md-4">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                        <input type="checkbox"  name="radiotv" <%=globaldisabled%> value="Y"  <%="Y".equals(businessProfileVO.getRadioTv())?"checked":""%> /> &nbsp;<%=businessprofile_Radio%></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.internet)|| view)
                                    {
                                %>
                                <div class="form-group col-lg-4 col-md-4">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                        <input type="checkbox" name="internet" value="Y"  <%=globaldisabled%>  <%="Y".equals(businessProfileVO.getInternet())?"checked":""%> />&nbsp;<%=businessprofile_Internet%></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.networking)|| view)
                                    {
                                %>
                                <div class="form-group col-lg-4 col-md-4">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                        <input type="checkbox" name="networking" value="Y" <%=globaldisabled%> <%="Y".equals(businessProfileVO.getNetworking())?"checked":""%> />&nbsp;<%=businessprofile_Networking%></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.outboundtelemarketing)|| view)
                                    {
                                %>

                                <div class="form-group col-lg-4 col-md-4">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left: 0;">
                                        <input type="checkbox" name="outboundtelemarketing"  <%=globaldisabled%> value="Y"  <%="Y".equals(businessProfileVO.getOutboundTelemarketing())?"checked":""%> /> &nbsp;<%=businessprofile_Outbound%></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.multiple_membership) || businessProfileInputList.contains(BankInputName.free_membership)|| view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_Membership%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <%
                                    if(businessProfileInputList.contains(BankInputName.multiple_membership)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_card%>
                                        </label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio" name="multiple_membership"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getMultipleMembership())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes34%>&nbsp;&nbsp;
                                        <input type="radio" name="multiple_membership"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getMultipleMembership()) || !functions.isValueNull(businessProfileVO.getMultipleMembership()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No34%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.free_membership)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_member%>
                                        </label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio" name="free_membership"  <%=globaldisabled%> value="Y" <%="Y".equals(businessProfileVO.getFreeMembership())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes35%>&nbsp;&nbsp;
                                        <input type="radio" name="free_membership"  <%=globaldisabled%> value="N" <%="N".equals(businessProfileVO.getFreeMembership()) || !functions.isValueNull(businessProfileVO.getFreeMembership()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No35%></div>
                                </div>
                                <%
                                    }
                                %>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>
            <%
                if(businessProfileInputList.contains(BankInputName.automatic_recurring)||businessProfileInputList.contains(BankInputName.multiple_membership)||businessProfileInputList.contains(BankInputName.free_membership)||businessProfileInputList.contains(BankInputName.creditcard_Required)||businessProfileInputList.contains(BankInputName.automatically_billed)||businessProfileInputList.contains(BankInputName.pre_authorization)|| view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=businessprofile_Other2%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%
                                    if(businessProfileInputList.contains(BankInputName.creditcard_Required)||businessProfileInputList.contains(BankInputName.automatically_billed)||businessProfileInputList.contains(BankInputName.pre_authorization)||view)
                                    {
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.creditcard_Required)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_credit%>
                                        </label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio"  <%=globaldisabled%> name="creditcard_Required" value="Y" <%="Y".equals(businessProfileVO.getCreditCardRequired())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes36%>&nbsp;&nbsp;
                                        <input type="radio"  <%=globaldisabled%> name="creditcard_Required" value="N" <%="N".equals(businessProfileVO.getCreditCardRequired()) || !functions.isValueNull(businessProfileVO.getCreditCardRequired()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No36%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.automatically_billed)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_automatically%>
                                        </label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio"  <%=globaldisabled%> name="automatically_billed" value="Y" <%="Y".equals(businessProfileVO.getAutomaticallyBilled())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes37%>&nbsp;&nbsp;
                                        <input type="radio"  <%=globaldisabled%> name="automatically_billed" value="N" <%="N".equals(businessProfileVO.getAutomaticallyBilled()) || !functions.isValueNull(businessProfileVO.getAutomaticallyBilled()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No37%></div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.pre_authorization)|| view)
                                    {
                                %>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">
                                    <div class="col-md-6" style="padding-left: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=businessprofile_authorization%>
                                        </label></div>
                                    <div class="col-md-6" style="padding: 0;">
                                        <input type="radio"  <%=globaldisabled%> name="pre_authorization" value="Y" <%="Y".equals(businessProfileVO.getPreAuthorization())?"checked":""%> />
                                        &nbsp;<%=businessprofile_Yes38%>&nbsp;&nbsp;
                                        <input type="radio"  <%=globaldisabled%> name="pre_authorization" value="N" <%="N".equals(businessProfileVO.getPreAuthorization()) || !functions.isValueNull(businessProfileVO.getPreAuthorization()) ?"checked":""%> />
                                        &nbsp;<%=businessprofile_No38%></div>
                                </div>
                                <%
                                        }
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.shopsystem_plugin)|| businessProfileInputList.contains(BankInputName.direct_debit_sepa)|| businessProfileInputList.contains(BankInputName.creditor_id)|| businessProfileInputList.contains(BankInputName.alternative_payments)|| businessProfileInputList.contains(BankInputName.risk_management)|| businessProfileInputList.contains(BankInputName.payment_engine)|| view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Technical Solution</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">




                                <div class="form-group col-md-12 has-feedback">

                                    <%
                                        if(businessProfileInputList.contains(BankInputName.shopsystem_plugin)|| view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback" style="padding-left: 0;">
                                        <label for="shopsystem_plugin" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Shopsystem / Plug-in</label>
                                        <input type="text" class="form-control" id="shopsystem_plugin" name="shopsystem_plugin" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShopsystem_plugin())==true?businessProfileVO.getShopsystem_plugin():""%>" /><%if(validationErrorList.getError("shopsystem_plugin")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>

                                    <%
                                        }
                                    %>


                                    <%
                                        if(businessProfileInputList.contains(BankInputName.direct_debit_sepa)|| view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback" style="padding-left: 0;">
                                        <label for="direct_debit_sepa" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Processing SEPA Direct Debit (EE)</label>
                                        <input type="text" class="form-control"  id="direct_debit_sepa" name="direct_debit_sepa" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getDirect_debit_sepa())==true?businessProfileVO.getDirect_debit_sepa():""%>" /><%if(validationErrorList.getError("direct_debit_sepa")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(businessProfileInputList.contains(BankInputName.creditor_id)|| view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback" style="padding-left: 0;">
                                        <label for="creditor_id" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Creditor ID</label>
                                        <input type="text" class="form-control"  id="creditor_id" name="creditor_id" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCreditor_id())==true?businessProfileVO.getCreditor_id():""%>" /><%if(validationErrorList.getError("creditor_id")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.alternative_payments)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12 has-feedback">
                                    <div class="form-group col-md-4 has-feedback" style="padding-left: 0;">
                                        <label for="alternative_payments" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Processing Alternative Payments</label>
                                        <input type="text" class="form-control"  id="alternative_payments" name="alternative_payments" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getAlternative_payments())==true?businessProfileVO.getAlternative_payments():""%>" /><%if(validationErrorList.getError("alternative_payments")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(businessProfileInputList.contains(BankInputName.risk_management)|| view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback" style="padding-left: 0;">
                                        <label for="risk_management" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Risk Management</label>
                                        <input type="text" class="form-control"  id="risk_management" name="risk_management" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getRisk_management())==true?businessProfileVO.getRisk_management():""%>" /><%if(validationErrorList.getError("risk_management")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>

                                    <%
                                        }
                                    %>
                                    <%
                                        if(businessProfileInputList.contains(BankInputName.payment_engine)|| view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback" style="padding-left: 0;">
                                        <label for="payment_engine" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Payment Engine / Elastic Engine</label>
                                        <input type="text" class="form-control"   id="payment_engine" name="payment_engine" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_engine())==true?businessProfileVO.getPayment_engine():""%>" /><%if(validationErrorList.getError("payment_engine")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%
                if(businessProfileInputList.contains(BankInputName.webhost_company_name)|| businessProfileInputList.contains(BankInputName.webhost_phone)|| businessProfileInputList.contains(BankInputName.webhost_email)|| businessProfileInputList.contains(BankInputName.webhost_address)|| businessProfileInputList.contains(BankInputName.payment_company_name)|| businessProfileInputList.contains(BankInputName.payment_phone)|| businessProfileInputList.contains(BankInputName.payment_email)|| businessProfileInputList.contains(BankInputName.payment_address)|| businessProfileInputList.contains(BankInputName.isacallcenterusedyes)|| businessProfileInputList.contains(BankInputName.callcenter_phone)|| businessProfileInputList.contains(BankInputName.callcenter_email)|| businessProfileInputList.contains(BankInputName.callcenter_address)|| businessProfileInputList.contains(BankInputName.shoppingcart_company_name)|| businessProfileInputList.contains(BankInputName.shoppingcart_phone)|| businessProfileInputList.contains(BankInputName.shoppingcart_email)|| businessProfileInputList.contains(BankInputName.shoppingcart_address) || businessProfileInputList.contains(BankInputName.goods_delivery) ||  view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Service Provider</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <%
                                    if(businessProfileInputList.contains(BankInputName.webhost_company_name)|| businessProfileInputList.contains(BankInputName.webhost_phone)|| businessProfileInputList.contains(BankInputName.webhost_email)|| businessProfileInputList.contains(BankInputName.webhost_website)|| businessProfileInputList.contains(BankInputName.webhost_address)||  view)
                                    {
                                %>

                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Webhosting provider
                                    <%--<br>(Provider of Services in the field of electonic data processing and the internet e.g. the webspace of your website)--%>
                                </h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.webhost_company_name)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Company name</label>
                                    <input  type="text" class="form-control" id="webhost_company_name" name="webhost_company_name" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getWebhost_company_name())==true?businessProfileVO.getWebhost_company_name():""%>" /><%if(validationErrorList.getError("webhost_company_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.webhost_phone)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                    <input  type="text" class="form-control" id="webhost_phone" name="webhost_phone" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getWebhost_phone())==true?businessProfileVO.getWebhost_phone():""%>" /><%if(validationErrorList.getError("webhost_phone")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.webhost_email)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input  type="text" class="form-control" id="webhost_email" name="webhost_email" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getWebhost_email())==true?businessProfileVO.getWebhost_email():""%>" /><%if(validationErrorList.getError("webhost_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.webhost_website)||  view)
                                    {
                                %>

                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website Address</label>
                                    <input  type="text" class="form-control" id="webhost_website" name="webhost_website" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getWebhost_website())==true?businessProfileVO.getWebhost_website():""%>" /><%if(validationErrorList.getError("webhost_website")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.webhost_address)||  view)
                                    {
                                %>

                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                    <input  type="text" class="form-control" id="webhost_address" name="webhost_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getWebhost_address())==true?businessProfileVO.getWebhost_address():""%>" /><%if(validationErrorList.getError("webhost_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>



                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.payment_company_name)|| businessProfileInputList.contains(BankInputName.payment_phone)|| businessProfileInputList.contains(BankInputName.payment_email)|| businessProfileInputList.contains(BankInputName.payment_website)|| businessProfileInputList.contains(BankInputName.payment_address)||  view)
                                    {
                                %>

                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Current/Previous Payment Provider
                                    <%--<br>(Payment Service Provider; company with which you work for the credit card processing, e.g. provider of your payment page)--%></h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.payment_company_name)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Company name</label>
                                    <input  type="text" class="form-control" id="payment_company_name" name="payment_company_name" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_company_name())==true?businessProfileVO.getPayment_company_name():""%>" /><%if(validationErrorList.getError("payment_company_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.payment_phone)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                    <input  type="text" class="form-control" id="payment_phone" name="payment_phone" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_phone())==true?businessProfileVO.getPayment_phone():""%>" /><%if(validationErrorList.getError("payment_phone")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.payment_email)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input  type="text" class="form-control" id="payment_email" name="payment_email" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_email())==true?businessProfileVO.getPayment_email():""%>" /><%if(validationErrorList.getError("payment_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.payment_website)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website Address</label>
                                    <input  type="text" class="form-control" id="payment_website" name="payment_website" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_website())==true?businessProfileVO.getPayment_website():""%>" /><%if(validationErrorList.getError("payment_website")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.payment_address)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                    <input  type="text" class="form-control" id="payment_address" name="payment_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getPayment_address())==true?businessProfileVO.getPayment_address():""%>" /><%if(validationErrorList.getError("payment_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.isacallcenterusedyes)|| businessProfileInputList.contains(BankInputName.callcenter_phone)|| businessProfileInputList.contains(BankInputName.callcenter_email)|| businessProfileInputList.contains(BankInputName.callcenter_website)|| businessProfileInputList.contains(BankInputName.callcenter_address)||  view)
                                    {
                                %>

                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Call Center</h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.isacallcenterusedyes)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Company name</label>
                                    <input  type="text" class="form-control" id="isacallcenterusedyes" name="isacallcenterusedyes" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getIsacallcenterusedyes())==true?businessProfileVO.getIsacallcenterusedyes():""%>" /><%if(validationErrorList.getError("isacallcenterusedyes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.callcenter_phone)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                    <input  type="text" class="form-control" id="callcenter_phone" name="callcenter_phone" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCallcenter_phone())==true?businessProfileVO.getCallcenter_phone():""%>" /><%if(validationErrorList.getError("callcenter_phone")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.callcenter_email)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input  type="text" class="form-control" id="callcenter_email" name="callcenter_email" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCallcenter_email())==true?businessProfileVO.getCallcenter_email():""%>" /><%if(validationErrorList.getError("callcenter_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>


                                <%
                                    if(businessProfileInputList.contains(BankInputName.callcenter_website)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website Address</label>
                                    <input  type="text" class="form-control" id="callcenter_website" name="callcenter_website" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCallcenter_website())==true?businessProfileVO.getCallcenter_website():""%>" /><%if(validationErrorList.getError("callcenter_website")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.callcenter_address)||  view)
                                    {
                                %>

                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                    <input  type="text" class="form-control" id="callcenter_address" name="callcenter_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getCallcenter_address())==true?businessProfileVO.getCallcenter_address():""%>" /><%if(validationErrorList.getError("callcenter_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_company_name)|| businessProfileInputList.contains(BankInputName.shoppingcart_phone)|| businessProfileInputList.contains(BankInputName.callcenter_email)|| businessProfileInputList.contains(BankInputName.shoppingcart_website)|| businessProfileInputList.contains(BankInputName.callcenter_address)||  view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;margin-bottom: 20px;">Shopping cart provider
                                    <%--<br>Software for electronic shopping carts/consumer baskets within internet shops or within complete shop solutions)--%></h2>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_company_name)||  view)
                                    {
                                %>

                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Company name</label>
                                    <input  type="text" class="form-control" id="shoppingcart_company_name" name="shoppingcart_company_name" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShoppingcart_company_name())==true?businessProfileVO.getShoppingcart_company_name():""%>" /><%if(validationErrorList.getError("shoppingcart_company_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_phone)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone Number</label>
                                    <input  type="text" class="form-control" id="shoppingcart_phone" name="shoppingcart_phone" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShoppingcart_phone())==true?businessProfileVO.getShoppingcart_phone():""%>" /><%if(validationErrorList.getError("shoppingcart_phone")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_email)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input  type="text" class="form-control" id="shoppingcart_email" name="shoppingcart_email" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShoppingcart_email())==true?businessProfileVO.getShoppingcart_email():""%>" /><%if(validationErrorList.getError("shoppingcart_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_website)||  view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website Address</label>
                                    <input  type="text" class="form-control" id="shoppingcart_website" name="shoppingcart_website" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShoppingcart_website())==true?businessProfileVO.getShoppingcart_website():""%>" /><%if(validationErrorList.getError("shoppingcart_website")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_address)||  view)
                                    {
                                %>

                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address</label>
                                    <input  type="text" class="form-control" id="shoppingcart_address" name="shoppingcart_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShoppingcart_address())==true?businessProfileVO.getShoppingcart_address():""%>" /><%if(validationErrorList.getError("shoppingcart_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%--<%
                                    if(businessProfileInputList.contains(BankInputName.shoppingcart_address)||  view)
                                    {
                                %>

                                <div class="form-group col-md-3 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Website or Address</label>
                                    <input  type="text" class="form-control" id="shoppingcart_address" name="shoppingcart_address" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=functions.isValueNull(businessProfileVO.getShoppingcart_address())==true?businessProfileVO.getShoppingcart_address():""%>" /><%if(validationErrorList.getError("shoppingcart_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>--%>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <%


                if(businessProfileInputList.size()==0 && !view)
                {

                /*out.println("<div class=\"content-page\">");
                out.println("<div class=\"content\">");
                out.println("<div class=\"page-heading\">");*/
                    out.println("<div class=\"row\">");
                    out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
                    out.println("<div class=\"widget\">");
                    out.println("<div class=\"widget-header transparent\">\n" +
                            "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;Business Details</strong></h2>\n" +
                            "                                <div class=\"additional-btn\">\n" +
                            "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                            "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                            "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                            "                                </div>\n" +
                            "                            </div>");
                    out.println("<div class=\"widget-content padding\">");
          /*out.println("<div class=\"table-responsive\">");*/
                    out.println(Functions.NewShowConfirmation1("Profile","No details need to be provided"));         /* out.println("</div>");*/
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                    out.println("</div>");
                /*out.println("</div>");
                out.println("</div>");
                out.println("</div>");*/


                }
            %>
        </div>
    </div>
    </div>