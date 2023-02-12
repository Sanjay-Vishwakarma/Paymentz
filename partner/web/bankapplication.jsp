<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="com.vo.applicationManagerVOs.ApplicationManagerVO" %>
<%@ page import="com.vo.applicationManagerVOs.BankProfileVO" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/13/15
  Time: 3:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

<script src="/partner/NewCss/am_multipleselection/jquery-multipleselection.js"></script>
<script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
<link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />

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
        $('#currencybank').multiselect({
            buttonText: function(options, select) {
                if (options.length === 0) {
                    return 'Select Currency';
                }

                else {
                    var labels = [];
                    console.log(options);
                    options.each(function() {
                        labels.push($(this).val());
                    });
                    document.getElementById('bank_account_currencies').value = labels;
                    return labels.join(', ') + '';
                }
            }
        });
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
        color: #333;
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



</style>
<style type="text/css">
    @media (min-width: 641px){
        #refundratioid, #chargebackratioid, #processingheader{
            width: 20%;
        }

    }


    /*        .glyphicon{
            display: block!important;
            color:#a94442!important;
            background-color: #ebccd1!important;
            width: 40px!important;
            margin-right: 16px!important;
            height: 32px!important;
            margin-top: -1px!important;
        }*/

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

    #myTable tr input{
        width: inherit;
    }


    /****************For checkbox overflow********************/
    .multiselect-selected-text {
        overflow: hidden;
        display: block;
    }

    /*    .input-group-addon, .input-group-btn{
            width: inherit;
        }*/


    /*   @media (max-width: 555px){
           #glipmedia {
               top: inherit!important;
               margin-top: -32px!important;
           }
       }*/

</style>


<%!
    private Functions functions = new Functions();
    private AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
    private Logger logger = new Logger("bankapplication.jsp");
%>
<%
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String bankapplication_Bank_Profile = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_Profile")) ? rb1.getString("bankapplication_Bank_Profile") : "Bank Profile";
    String bankapplication_currency = StringUtils.isNotEmpty(rb1.getString("bankapplication_currency")) ? rb1.getString("bankapplication_currency") : "In which currency would you like payment to be transferred to your bank account?";
    String bankapplication_Bank_Information = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_Information")) ? rb1.getString("bankapplication_Bank_Information") : "Bank Information";
    String bankapplication_Select_Currency = StringUtils.isNotEmpty(rb1.getString("bankapplication_Select_Currency")) ? rb1.getString("bankapplication_Select_Currency") : "Select Currency";
    String bankapplication_swift = StringUtils.isNotEmpty(rb1.getString("bankapplication_swift")) ? rb1.getString("bankapplication_swift") : "SWIFT/IFSC/BIC (Bank Identifier Code)*";
    String bankapplication_Bank_Name = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_Name")) ? rb1.getString("bankapplication_Bank_Name") : "Bank Name*";
    String bankapplication_Bank_Address = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_Address")) ? rb1.getString("bankapplication_Bank_Address") : "Bank Address";
    String bankapplication_Bank_phone = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_phone")) ? rb1.getString("bankapplication_Bank_phone") : "Bank Phone Number";
    String bankapplication_Bank_contact = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_contact")) ? rb1.getString("bankapplication_Bank_contact") : "Bank Contact Person";
    String bankapplication_Bank_Account_Number = StringUtils.isNotEmpty(rb1.getString("bankapplication_Bank_Account_Number")) ? rb1.getString("bankapplication_Bank_Account_Number") : "Bank Account Number*";
    String bankapplication_IBAN_Number = StringUtils.isNotEmpty(rb1.getString("bankapplication_IBAN_Number")) ? rb1.getString("bankapplication_IBAN_Number") : "IBAN Number";
    String bankapplication_ABA_routing = StringUtils.isNotEmpty(rb1.getString("bankapplication_ABA_routing")) ? rb1.getString("bankapplication_ABA_routing") : "ABA routing code (US)";
    String bankapplication_Account_Holder = StringUtils.isNotEmpty(rb1.getString("bankapplication_Account_Holder")) ? rb1.getString("bankapplication_Account_Holder") : "Account Holder*";
    String bankapplication_Payment_Acceptor = StringUtils.isNotEmpty(rb1.getString("bankapplication_Payment_Acceptor")) ? rb1.getString("bankapplication_Payment_Acceptor") : "Payment Acceptor Details:";
    String bankapplication_previous = StringUtils.isNotEmpty(rb1.getString("bankapplication_previous")) ? rb1.getString("bankapplication_previous") : "Previous or current payment acceptor?";
    String bankapplication_reason = StringUtils.isNotEmpty(rb1.getString("bankapplication_reason")) ? rb1.getString("bankapplication_reason") : "Reason for leaving the Previous/Current payment acceptor?";
    String bankapplication_customer = StringUtils.isNotEmpty(rb1.getString("bankapplication_customer")) ? rb1.getString("bankapplication_customer") : "What kind of customer and transaction data is stored?*";
    String bankapplication_Processing_History = StringUtils.isNotEmpty(rb1.getString("bankapplication_Processing_History")) ? rb1.getString("bankapplication_Processing_History") : "Processing History";
    String bankapplication_History = StringUtils.isNotEmpty(rb1.getString("bankapplication_History")) ? rb1.getString("bankapplication_History") : "PROCESSING HISTORY";
    String bankapplication_6month = StringUtils.isNotEmpty(rb1.getString("bankapplication_6month")) ? rb1.getString("bankapplication_6month") : "6 Months Ago";
    String bankapplication_5month = StringUtils.isNotEmpty(rb1.getString("bankapplication_5month")) ? rb1.getString("bankapplication_5month") : "5 Months Ago";
    String bankapplication_4month = StringUtils.isNotEmpty(rb1.getString("bankapplication_4month")) ? rb1.getString("bankapplication_4month") : "4 Months Ago";
    String bankapplication_3month = StringUtils.isNotEmpty(rb1.getString("bankapplication_3month")) ? rb1.getString("bankapplication_3month") : "3 Months Ago";
    String bankapplication_2month = StringUtils.isNotEmpty(rb1.getString("bankapplication_2month")) ? rb1.getString("bankapplication_2month") : "2 Months Ago";
    String bankapplication_1month = StringUtils.isNotEmpty(rb1.getString("bankapplication_1month")) ? rb1.getString("bankapplication_1month") : "1 Months Ago";
    String bankapplication_1year = StringUtils.isNotEmpty(rb1.getString("bankapplication_1year")) ? rb1.getString("bankapplication_1year") : "1 Year Ago";
    String bankapplication_2year = StringUtils.isNotEmpty(rb1.getString("bankapplication_2year")) ? rb1.getString("bankapplication_2year") : "2 Years Ago";
    String bankapplication_3year = StringUtils.isNotEmpty(rb1.getString("bankapplication_3year")) ? rb1.getString("bankapplication_3year") : "3 Years Ago";
    String bankapplication_Sales_volume = StringUtils.isNotEmpty(rb1.getString("bankapplication_Sales_volume")) ? rb1.getString("bankapplication_Sales_volume") : "Sales volume";
    String bankapplication_Number_transactions = StringUtils.isNotEmpty(rb1.getString("bankapplication_Number_transactions")) ? rb1.getString("bankapplication_Number_transactions") : "Number of transactions";
    String bankapplication_Chargeback_volume = StringUtils.isNotEmpty(rb1.getString("bankapplication_Chargeback_volume")) ? rb1.getString("bankapplication_Chargeback_volume") : "Chargeback volume";
    String bankapplication_Number_chargebacks = StringUtils.isNotEmpty(rb1.getString("bankapplication_Number_chargebacks")) ? rb1.getString("bankapplication_Number_chargebacks") : "Number of chargebacks";
    String bankapplication_Refunds_volume = StringUtils.isNotEmpty(rb1.getString("bankapplication_Refunds_volume")) ? rb1.getString("bankapplication_Refunds_volume") : "Refunds volume";
    String bankapplication_Number_refunds = StringUtils.isNotEmpty(rb1.getString("bankapplication_Number_refunds")) ? rb1.getString("bankapplication_Number_refunds") : "Number of refunds";
    String bankapplication_Chargeback_Ratio = StringUtils.isNotEmpty(rb1.getString("bankapplication_Chargeback_Ratio")) ? rb1.getString("bankapplication_Chargeback_Ratio") : "Chargeback Ratio";
    String bankapplication_Refund_ratio = StringUtils.isNotEmpty(rb1.getString("bankapplication_Refund_ratio")) ? rb1.getString("bankapplication_Refund_ratio") : "Refund ratio";
    String bankapplication_BankProfile = StringUtils.isNotEmpty(rb1.getString("bankapplication_BankProfile")) ? rb1.getString("bankapplication_BankProfile") : "Bank Profile";
    String bankapplication_Profile = StringUtils.isNotEmpty(rb1.getString("bankapplication_Profile")) ? rb1.getString("bankapplication_Profile") : "Profile";
    String bankapplication_details = StringUtils.isNotEmpty(rb1.getString("bankapplication_details")) ? rb1.getString("bankapplication_details") : "There is no details that has to be provided for this profile";

    ApplicationManager applicationManager = new ApplicationManager();
    ApplicationManagerVO applicationManagerVO=null;
    BankProfileVO bankProfileVO=null;
    ValidationErrorList validationErrorList=null;
    List<String> bankCurrencyList= new ArrayList();
    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }
    if(applicationManagerVO.getBankProfileVO()!=null)
    {
        bankProfileVO=applicationManagerVO.getBankProfileVO();
        if(functions.isValueNull(bankProfileVO.getBank_account_currencies()))
        {
            String arrBankaccountcurrencies[] = bankProfileVO.getBank_account_currencies().split(",");
            for(int i=0; i<arrBankaccountcurrencies.length; i++)
            {
                bankCurrencyList.add(arrBankaccountcurrencies[i]);
            }
        }

    }
    if(applicationManagerVO.getBankProfileVO()!=null)
    {
        bankProfileVO=applicationManagerVO.getBankProfileVO();
    }
    if(bankProfileVO==null)
    {
        bankProfileVO=new BankProfileVO();
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
%>
<%
    //for specific condition

    Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep=new HashMap<Boolean, Set<BankInputName>>();

    Set<BankInputName> bankProfileInputList=new HashSet<BankInputName>();

    //System.out.println("Current PageNO:::"+request.getParameter("currentPageNO"));

    if(request.getAttribute("fullValidationForStep")!=null)
    {
        //System.out.println("Inside FullValidationForStep:::");
        fullValidationForStep= (Map<Integer, Map<Boolean, Set<BankInputName>>>) request.getAttribute("fullValidationForStep");
        if(functions.isValueNull(request.getParameter("currentPageNO")) && fullValidationForStep.containsKey(Integer.valueOf(request.getParameter("currentPageNO"))))
        {
            //System.out.println("Inside PageViseFullValidationForStep:::");
            fullPageViseValidationForStep=fullValidationForStep.get(Integer.valueOf(request.getParameter("currentPageNO")));
            //System.out.println("PageViseFullValidationForStep::::"+fullPageViseValidationForStep);
            if(fullPageViseValidationForStep.containsKey(false))
                bankProfileInputList.addAll(fullPageViseValidationForStep.get(false));
            if(fullPageViseValidationForStep.containsKey(true))
                bankProfileInputList.addAll(fullPageViseValidationForStep.get(true));
        }
    }
    //end

    //For Currency
    List<String> currencyList = applicationManager.getcurrencyCode();
    String currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

%>


<%
    if(bankProfileInputList.contains(BankInputName.bank_account_currencies)|| bankProfileInputList.contains(BankInputName.bankinfo_bic)||
            bankProfileInputList.contains(BankInputName.bankinfo_bank_name)||
            bankProfileInputList.contains(BankInputName.bankinfo_bankaddress)||
            bankProfileInputList.contains(BankInputName.bankinfo_bankphonenumber)||
            bankProfileInputList.contains(BankInputName.bankinfo_contactperson)||
            bankProfileInputList.contains(BankInputName.bankinfo_accountnumber)||
            bankProfileInputList.contains(BankInputName.bankinfo_IBAN)||
            bankProfileInputList.contains(BankInputName.bankinfo_aba_routingcode)||
            bankProfileInputList.contains(BankInputName.bankinfo_accountholder)||
            bankProfileInputList.contains(BankInputName.aquirer)||
            bankProfileInputList.contains(BankInputName.reason_aquirer) ||
            bankProfileInputList.contains(BankInputName.customer_trans_data) ||
            bankProfileInputList.contains(BankInputName.salesvolume_lastmonth)||
            bankProfileInputList.contains(BankInputName.salesvolume_2monthsago)||
            bankProfileInputList.contains(BankInputName.salesvolume_3monthsago)||
            bankProfileInputList.contains(BankInputName.salesvolume_4monthsago)||
            bankProfileInputList.contains(BankInputName.salesvolume_5monthsago)||
            bankProfileInputList.contains(BankInputName.salesvolume_6monthsago)||
            bankProfileInputList.contains(BankInputName.salesvolume_12monthsago)||
            bankProfileInputList.contains(BankInputName.salesvolume_year2)||
            bankProfileInputList.contains(BankInputName.salesvolume_year3)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_lastmonth)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_2monthsago)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_3monthsago)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_4monthsago)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_5monthsago)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_6monthsago)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_12monthsago)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_year2)||
            bankProfileInputList.contains(BankInputName.numberoftransactions_year3)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_lastmonth)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_2monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_3monthsago) ||
            bankProfileInputList.contains(BankInputName.chargebackvolume_4monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_5monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_6monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_12monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_year2)||
            bankProfileInputList.contains(BankInputName.chargebackvolume_year3)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_lastmonth)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_2monthsago)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_3monthsago)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_4monthsago)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_5monthsago)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_6monthsago)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_12monthsago)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_year2)||
            bankProfileInputList.contains(BankInputName.numberofchargebacks_year3)||
            bankProfileInputList.contains(BankInputName.refundsvolume_lastmonth)||
            bankProfileInputList.contains(BankInputName.refundsvolume_2monthsago) ||
            bankProfileInputList.contains(BankInputName.refundsvolume_3monthsago)||
            bankProfileInputList.contains(BankInputName.refundsvolume_4monthsago)||
            bankProfileInputList.contains(BankInputName.refundsvolume_5monthsago)||
            bankProfileInputList.contains(BankInputName.refundsvolume_6monthsago)||
            bankProfileInputList.contains(BankInputName.refundsvolume_12monthsago)||
            bankProfileInputList.contains(BankInputName.refundsvolume_year2)||
            bankProfileInputList.contains(BankInputName.refundsvolume_year3)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_lastmonth)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_2monthsago)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_3monthsago) ||
            bankProfileInputList.contains(BankInputName.numberofrefunds_4monthsago)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_5monthsago)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_6monthsago)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_12monthsago)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_year2)||
            bankProfileInputList.contains(BankInputName.numberofrefunds_year3)||
            bankProfileInputList.contains(BankInputName.chargebackratio_lastmonth)||
            bankProfileInputList.contains(BankInputName.chargebackratio_2monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackratio_3monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackratio_4monthsago) ||
            bankProfileInputList.contains(BankInputName.chargebackratio_5monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackratio_6monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackratio_12monthsago)||
            bankProfileInputList.contains(BankInputName.chargebackratio_year2)||
            bankProfileInputList.contains(BankInputName.chargebackratio_year3)||
            bankProfileInputList.contains(BankInputName.refundratio_lastmonth)||
            bankProfileInputList.contains(BankInputName.refundratio_2monthsago)||
            bankProfileInputList.contains(BankInputName.refundratio_3monthsago)||
            bankProfileInputList.contains(BankInputName.refundratio_4monthsago)||
            bankProfileInputList.contains(BankInputName.refundratio_5monthsago)||
            bankProfileInputList.contains(BankInputName.refundratio_6monthsago)||
            bankProfileInputList.contains(BankInputName.refundratio_12monthsago)||
            bankProfileInputList.contains(BankInputName.refundratio_year2)||
            bankProfileInputList.contains(BankInputName.refundratio_year3)|| view)
    {

%>

<div class="content-page" id="bankappid">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=bankapplication_Bank_Profile%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%--<div class="container-fluid ">
                                    <div class="row" style="margin-top: 100px;margin-left: 226px;margin-bottom:1%;background-color: #ffffff">
                                        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">--%>

                                <div class="form foreground bodypanelfont_color panelbody_color">
                                    <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> bg-info" style="font-family: Open Sans; font-size: 13px; font-weight: 600;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Bank Profile after entering the data provided"%></h4></center>
                                    <%--<%
                                        if(bankProfileInputList.contains(BankInputName.currency_products_USD)|| bankProfileInputList.contains(BankInputName.currency_products_GBP)|| bankProfileInputList.contains(BankInputName.currency_products_EUR) || bankProfileInputList.contains(BankInputName.currency_products_JPY)|| bankProfileInputList.contains(BankInputName.currency_products_PEN)|| bankProfileInputList.contains(BankInputName.currency_products_HKD) ||bankProfileInputList.contains(BankInputName.currency_products_AUD)|| bankProfileInputList.contains(BankInputName.currency_products_CAD) || bankProfileInputList.contains(BankInputName.currency_products_DKK) || bankProfileInputList.contains(BankInputName.currency_products_SEK)|| bankProfileInputList.contains(BankInputName.currency_products_NOK)|| bankProfileInputList.contains(BankInputName.currency_products_INR)||view)
                                        {
                                    %>
                                    <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"> CURRENCY REQUESTED</h2>

                                    <div class="form-group col-md-12">
                                        <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;" >In which currency are your products sold?</label>
                                    </div>

                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">USD</label>
                                        <input type="checkbox" class="form-control" name="currency_products_USD" style="width: 50%;"  <%=globaldisabled%> value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_USD())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">GBP</label>
                                        <input type="checkbox" class="form-control"  name="currency_products_GBP" style="width: 50%;"  <%=globaldisabled%> value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_GBP())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">EUR</label>
                                        <input type="checkbox" class="form-control"    name="currency_products_EUR" <%=globaldisabled%> style="width: 50%;" style="border: 1px solid #b2b2b2;font-weight:bold"  value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_EUR())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">JPY</label>
                                        <input type="checkbox" class="form-control"   name="currency_products_JPY" style="width: 50%;" <%=globaldisabled%>  value="Y" <%="Y".equals(bankProfileVO.getCurrency_products_JPY())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">PEN</label>
                                        <input type="checkbox" class="form-control"  name="currency_products_PEN" style="width: 50%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_PEN())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">HKD</label>
                                        <input type="checkbox" class="form-control"   name="currency_products_HKD" style="width: 50%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_HKD())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">AUD</label>
                                        <input type="checkbox" class="form-control"   name="currency_products_AUD" style="width: 50%;"  <%=globaldisabled%>   value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_AUD())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">CAD</label>
                                        <input type="checkbox" class="form-control"    name="currency_products_CAD" style="width: 50%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_CAD())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">DKK</label>
                                        <input type="checkbox" class="form-control"    name="currency_products_DKK" style="width: 50%;"   <%=globaldisabled%> value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_DKK())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">SEK</label>
                                        <input type="checkbox" class="form-control"    name="currency_products_SEK" style="width: 50%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_SEK())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">NOK</label>
                                        <input type="checkbox" class="form-control"  name="currency_products_NOK" style="width: 50%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_NOK())?"checked":""%> />
                                    </div>
                                    <div class="form-group col-lg-2 col-md-4">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">INR</label>&nbsp;
                                        <input type="checkbox" class="form-control"    name="currency_products_INR" style="width: 50%;"  <%=globaldisabled%> value="Y"  <%="Y".equals(bankProfileVO.getCurrency_products_INR())?"checked":""%> />
                                    </div>
                                    <%
                                        }
                                    %>--%>
                                    <%--<hr class="hrnew1">--%>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bank_account_currencies) ||view)
                                        {
                                    %>
                                    <div class="form-group col-md-6">

                                        <label><%=bankapplication_currency%></label>
                                    </div>
                                    <div class="form-group col-md-6 has-feedback" style="padding-left:0;">
                                        <div class="form-group col-md-12 has-feedback">
                                            <select size="5" multiple="multiple" id="currencybank" class="form-group" <%=globaldisabled%>>
                                                <%
                                                    out.println(AppFunctionUtil.getcurrencybank(functions.isValueNull(bankProfileVO.getBank_account_currencies()) == true ? bankProfileVO.getBank_account_currencies() : "", bankCurrencyList));
                                                %>
                                            </select>
                                        </div>
                                        <input type="hidden" id="bank_account_currencies" name="bank_account_currencies" value="">

                                    </div>

                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%
                if(bankProfileInputList.contains(BankInputName.bankinfo_bic) || bankProfileInputList.contains(BankInputName.bankinfo_bank_name) || bankProfileInputList.contains(BankInputName.bankinfo_bankaddress) || bankProfileInputList.contains(BankInputName.bankinfo_bankphonenumber) || bankProfileInputList.contains(BankInputName.bankinfo_contactperson) || bankProfileInputList.contains(BankInputName.bankinfo_aba_routingcode) || bankProfileInputList.contains(BankInputName.bankinfo_accountholder) || bankProfileInputList.contains(BankInputName.bankinfo_IBAN) || bankProfileInputList.contains(BankInputName.bankinfo_accountnumber) || bankProfileInputList.contains(BankInputName.aquirer) || bankProfileInputList.contains(BankInputName.reason_aquirer) || bankProfileInputList.contains(BankInputName.customer_trans_data) || view)
                {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=bankapplication_Bank_Information%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="form foreground bodypanelfont_color panelbody_color">
                            <div class="widget-content padding" style="padding-top: 15px;">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=bankapplication_Select_Currency%></label>
                                        <select size="1" id="currency" name="currency" class="form-control" <%=globaldisabled%>>
                                            <%
                                                if(currencyList.size()>0)
                                                {
                                                    Iterator currencyIterator = currencyList.iterator();
                                                    String singleCurrency = "";
                                                    while(currencyIterator.hasNext())
                                                    {
                                                        singleCurrency = (String) currencyIterator.next();
                                                        String select = "";
                                                        if(functions.isValueNull(bankProfileVO.getBankinfo_currency()) && bankProfileVO.getBankinfo_currency().equalsIgnoreCase(singleCurrency))
                                                        {
                                                            select = "selected";
                                                        }
                                                        else if(currency.equalsIgnoreCase(singleCurrency))
                                                        {
                                                            select = "selected";
                                                        }
                                            %>
                                            <option value="<%=singleCurrency%>" <%=select%> ><%=singleCurrency%></option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="widget-content padding" style="overflow-y: auto;">
                                <div id="horizontal-form">

                                    <%--<h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;"> BANK INFORMATION</h2>--%>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_bic)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_bic" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_swift%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_bic" name="bankinfo_bic" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bic())==true?bankProfileVO.getBankinfo_bic():""%>" /><%if(validationErrorList.getError("bankinfo_bic")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_bank_name)||view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_bank_name" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_Bank_Name%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_bank_name" name="bankinfo_bank_name" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bank_name())==true?bankProfileVO.getBankinfo_bank_name():""%>" /><%if(validationErrorList.getError("bankinfo_bank_name")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_bankaddress)||view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_bankaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_Bank_Address%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_bankaddress" name="bankinfo_bankaddress" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bankaddress())==true?bankProfileVO.getBankinfo_bankaddress():""%>"/><%if(validationErrorList.getError("bankinfo_bankaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_bankphonenumber)||view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_bankphonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_Bank_phone%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_bankphonenumber" name="bankinfo_bankphonenumber" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_bankphonenumber())==true?bankProfileVO.getBankinfo_bankphonenumber():""%>" /><%if(validationErrorList.getError("bankinfo_bankphonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_contactperson)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankcontactperson" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_Bank_contact%></label>
                                        <input type="text"  class="form-control"  id="bankcontactperson" name="bankinfo_contactperson" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_contactperson())==true?bankProfileVO.getBankinfo_contactperson():""%>" /><%if(validationErrorList.getError("bankinfo_contactperson")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_accountnumber)||view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_accountnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_Bank_Account_Number%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_accountnumber" name="bankinfo_accountnumber" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountnumber())==true?bankProfileVO.getBankinfo_accountnumber():""%>"/><%if(validationErrorList.getError("bankinfo_accountnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_IBAN)||view)
                                        {
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_IBAN" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_IBAN_Number%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_IBAN" name="bankinfo_IBAN" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_IBAN())==true?bankProfileVO.getBankinfo_IBAN():""%>"/><%if(validationErrorList.getError("bankinfo_IBAN")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_aba_routingcode)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_aba_routingcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_ABA_routing%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_aba_routingcode" name="bankinfo_aba_routingcode" style="border: 1px solid #b2b2b2;font-weight:bold " <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_aba_routingcode())==true?bankProfileVO.getBankinfo_aba_routingcode():""%>"/><%if(validationErrorList.getError("bankinfo_aba_routingcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.bankinfo_accountholder)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="bankinfo_accountholder" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_Account_Holder%></label>
                                        <input type="text"  class="form-control"  id="bankinfo_accountholder" name="bankinfo_accountholder" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getBankinfo_accountholder())==true?bankProfileVO.getBankinfo_accountholder():""%>"/><%if(validationErrorList.getError("bankinfo_accountholder")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        if(bankProfileInputList.contains(BankInputName.aquirer)|| bankProfileInputList.contains(BankInputName.reason_aquirer)|| bankProfileInputList.contains(BankInputName.customer_trans_data)||view)
                                        {
                                    %>

                                    <div class="form-group col-md-12">
                                        <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u><%=bankapplication_Payment_Acceptor%></u></label>
                                    </div>

                                    <%
                                        if(bankProfileInputList.contains(BankInputName.aquirer)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="aquirer" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_previous%></label>
                                        <input type="text"  class="form-control"  id="aquirer" name="aquirer" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getAquirer())==true?bankProfileVO.getAquirer():""%>"/> <%if(validationErrorList.getError("aquirer")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>
                                    <%
                                        //System.out.println("Sneha-->"+bankProfileInputList.contains(BankInputName.reason_aquirer));
                                        if(bankProfileInputList.contains(BankInputName.reason_aquirer)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="reason_aquirer" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_reason%></label>
                                        <input type="text"  class="form-control"  id="reason_aquirer" name="reason_aquirer" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getReasonaquirer())==true?bankProfileVO.getReasonaquirer():""%>"/> <%if(validationErrorList.getError("reason_aquirer")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
                                    </div>
                                    <%
                                        }
                                    %>

                                    <%
                                        if(bankProfileInputList.contains(BankInputName.customer_trans_data)||view)
                                        {
                                    %>
                                    <div class="form-group col-md-6 has-feedback">
                                        <label for="customer_trans_data" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=bankapplication_customer%></label>
                                        <input type="text"  class="form-control"  id="customer_trans_data" name="customer_trans_data" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getCustomer_trans_data())==true?bankProfileVO.getCustomer_trans_data():""%>"/> <%if(validationErrorList.getError("customer_trans_data")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " id="glipmedia"></i><%}%>
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
            </div>
            <%
                }
            %>

            <%
                if(
                        bankProfileInputList.contains(BankInputName.salesvolume_lastmonth)||
                                bankProfileInputList.contains(BankInputName.salesvolume_2monthsago)||
                                bankProfileInputList.contains(BankInputName.salesvolume_3monthsago)||
                                bankProfileInputList.contains(BankInputName.salesvolume_4monthsago)||
                                bankProfileInputList.contains(BankInputName.salesvolume_5monthsago)||
                                bankProfileInputList.contains(BankInputName.salesvolume_6monthsago)||
                                bankProfileInputList.contains(BankInputName.salesvolume_12monthsago)||
                                bankProfileInputList.contains(BankInputName.salesvolume_year2)||
                                bankProfileInputList.contains(BankInputName.salesvolume_year3)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_lastmonth)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_2monthsago)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_3monthsago)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_4monthsago)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_5monthsago)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_6monthsago)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_12monthsago)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_year2)||
                                bankProfileInputList.contains(BankInputName.numberoftransactions_year3)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_lastmonth)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_2monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_3monthsago) ||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_4monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_5monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_6monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_12monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_year2)||
                                bankProfileInputList.contains(BankInputName.chargebackvolume_year3)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_lastmonth)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_2monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_3monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_4monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_5monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_6monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_12monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_year2)||
                                bankProfileInputList.contains(BankInputName.numberofchargebacks_year3)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_lastmonth)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_2monthsago) ||
                                bankProfileInputList.contains(BankInputName.refundsvolume_3monthsago)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_4monthsago)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_5monthsago)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_6monthsago)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_12monthsago)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_year2)||
                                bankProfileInputList.contains(BankInputName.refundsvolume_year3)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_lastmonth)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_2monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_3monthsago) ||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_4monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_5monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_6monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_12monthsago)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_year2)||
                                bankProfileInputList.contains(BankInputName.numberofrefunds_year3)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_lastmonth)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_2monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_3monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_4monthsago) ||
                                bankProfileInputList.contains(BankInputName.chargebackratio_5monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_6monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_12monthsago)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_year2)||
                                bankProfileInputList.contains(BankInputName.chargebackratio_year3)||
                                bankProfileInputList.contains(BankInputName.refundratio_lastmonth)||
                                bankProfileInputList.contains(BankInputName.refundratio_2monthsago)||
                                bankProfileInputList.contains(BankInputName.refundratio_3monthsago)||
                                bankProfileInputList.contains(BankInputName.refundratio_4monthsago)||
                                bankProfileInputList.contains(BankInputName.refundratio_5monthsago)||
                                bankProfileInputList.contains(BankInputName.refundratio_6monthsago)||
                                bankProfileInputList.contains(BankInputName.refundratio_12monthsago)||
                                bankProfileInputList.contains(BankInputName.refundratio_year2)||
                                bankProfileInputList.contains(BankInputName.refundratio_year3)|| view)
                {
            %>

            <div class="row" >
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=bankapplication_Processing_History%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content" style="padding-top: 15px;">
                            <div id="horizontal-form">
                                <div class="form-group col-md-4 has-feedback">
                                    <label><%=bankapplication_Select_Currency%></label>
                                    <select size="1" id="currency" name="currency" class="form-control" <%=globaldisabled%>>
                                        <%
                                            if(currencyList.size()>0)
                                            {
                                                Iterator currencyIterator = currencyList.iterator();
                                                String singleCurrency = "";
                                                while(currencyIterator.hasNext())
                                                {
                                                    singleCurrency = (String) currencyIterator.next();
                                                    String select = "";
                                                    if(currency.equalsIgnoreCase(singleCurrency))
                                                    {
                                                        select = "selected";
                                                    }
                                        %>
                                        <option value="<%=singleCurrency%>" <%=select%> ><%=singleCurrency%></option>
                                        <%
                                                    //System.out.println("Currency here -------->"+singleCurrency);
                                                }
                                            }
                                        %>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="widget-content padding" style="overflow-y: auto;">
                            <div id="horizontal-form">

                                <%
                                    if(
                                            bankProfileInputList.contains(BankInputName.salesvolume_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_3monthsago)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_year2)||
                                                    bankProfileInputList.contains(BankInputName.salesvolume_year3)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_3monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_year2)||
                                                    bankProfileInputList.contains(BankInputName.numberoftransactions_year3)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_3monthsago) ||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_year2)||
                                                    bankProfileInputList.contains(BankInputName.chargebackvolume_year3)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_3monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_year2)||
                                                    bankProfileInputList.contains(BankInputName.numberofchargebacks_year3)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_2monthsago) ||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_3monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_year2)||
                                                    bankProfileInputList.contains(BankInputName.refundsvolume_year3)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_3monthsago) ||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_year2)||
                                                    bankProfileInputList.contains(BankInputName.numberofrefunds_year3)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_3monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_4monthsago) ||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_year2)||
                                                    bankProfileInputList.contains(BankInputName.chargebackratio_year3)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_lastmonth)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_2monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_3monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_4monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_5monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_6monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_12monthsago)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_year2)||
                                                    bankProfileInputList.contains(BankInputName.refundratio_year3)|| view)
                                    {
                                %>

                                <%--<div class="container-fluid ">
                                    <div class="row" style="margin-left: 226px;background-color: #ffffff">
                                        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">--%>


                                <%-- <table align="center" width="90%" border="1">--%>
                                <%--<table class="table table-condensed table-striped table-bordered no-margin">--%>
                                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td align="center" class="texthead" data-label="PROCESSING HISTORY" id="processingheader" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_History%></td>
                                        <td align="center" class="texthead" data-label="6 MONTHS AGO" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_6month%></td>
                                        <td align="center" class="texthead" data-label="5 MONTHS" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_5month%></td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_4month%></td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_3month%></td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_2month%></td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_1month%></td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_1year%></td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_2year%>/td>
                                        <td align="center" class="texthead" width="" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_3year%></td>
                                    </tr>
                                    </thead>
                                    <tr>
                                        <td align="center" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"> <%=bankapplication_Sales_volume%></td>
                                        <td align="center" data-label="6 MONTHS AGO"><input type="tel" class="form-control" name="salesvolume_6monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate6monthsago()" onblur="calculate6monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_6monthsago())==true?bankProfileVO.getSalesvolume_6monthsago():""%>"/><%if(validationErrorList.getError("salesvolume_6monthsago")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>
                                        <td align="center" data-label="5 MONTHS AGO"><input type="tel" class="form-control" name="salesvolume_5monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate5monthsago()" onblur="calculate5monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_5monthsago())==true?bankProfileVO.getSalesvolume_5monthsago():""%>"/><%if(validationErrorList.getError("salesvolume_5monthsago")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>
                                        <td align="center" data-label="4 MONTHS AGO"><input type="tel" class="form-control" name="salesvolume_4monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate4monthsago()" onblur="calculate4monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_4monthsago())==true?bankProfileVO.getSalesvolume_4monthsago():""%>"/><%if(validationErrorList.getError("salesvolume_4monthsago")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>
                                        <td align="center" data-label="3 MONTHS AGO"><input type="tel" class="form-control" name="salesvolume_3monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate3monthsago()"onblur="calculate3monthsago()" <%=globaldisabled%>   value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_3monthsago())==true?bankProfileVO.getSalesvolume_3monthsago():""%>"/><%if(validationErrorList.getError("salesvolume_3monthsago")!=null){%><span class="apperrormsg">Invalid salesvolume</span><%}%></td>
                                        <td align="center" data-label="2 MONTHS AGO"><input type="tel" class="form-control" name="salesvolume_2monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate2monthsago()" onblur="calculate2monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago())==true?bankProfileVO.getSalesvolume_2monthsago():""%>"/><%if(validationErrorList.getError("salesvolume_2monthsago")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>
                                        <td align="center" data-label="1 MONTHS AGO"><input type="tel" class="form-control" name="salesvolume_lastmonth"  maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateLastMonth()" onblur="calculateLastMonth()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_lastmonth())==true?bankProfileVO.getSalesvolume_lastmonth():""%>"/><%if(validationErrorList.getError("salesvolume_lastmonth")!=null){%><span class="apperrormsg">Invalid salesvolume  </span><%}%></td>

                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO"><input type="tel" class="form-control" name="salesvolume_12monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate12monthsago()" onblur="calculate12monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_12monthsago())==true?bankProfileVO.getSalesvolume_12monthsago():""%>"/><%if(validationErrorList.getError("salesvolume_12monthsago")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>
                                        <td align="center" data-label="2 YEARS AGO"><input type="tel" class="form-control" name="salesvolume_year2" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear2()" onblur="calculateyear2()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_year2())==true?bankProfileVO.getSalesvolume_year2():""%>"/><%if(validationErrorList.getError("salesvolume_year2")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>
                                        <td align="center" data-label="3 YEARS AGO"><input type="tel" class="form-control" name="salesvolume_year3" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear3()" onblur="calculateyear3()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getSalesvolume_year3())==true?bankProfileVO.getSalesvolume_year3():""%>"/><%if(validationErrorList.getError("salesvolume_year3")!=null){%><span class="apperrormsg">Invalid salesvolume </span><%}%></td>

                                    </tr>
                                    <tr>
                                        <td align="center" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Number_transactions%></td>
                                        <td align="center" data-label="6 MONTHS AGO"><input type="tel" class="form-control" name="numberoftransactions_6monthsago" maxlength="10"   onkeypress="return isNumberKey(event)" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_6monthsago())==true?bankProfileVO.getNumberoftransactions_6monthsago():""%>"/><%if(validationErrorList.getError("numberoftransactions_6monthsago")!=null){%><span class="apperrormsg">Invalid numberoftransactions_6monthsago</span><%}%></td>
                                        <td align="center" data-label="5 MONTHS AGO"><input type="tel" class="form-control" name="numberoftransactions_5monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_5monthsago())==true?bankProfileVO.getNumberoftransactions_5monthsago():""%>"/><%if(validationErrorList.getError("numberoftransactions_5monthsago")!=null){%><span class="apperrormsg">Invalid numberoftransactions_5monthsago </span><%}%></td>
                                        <td align="center" data-label="4 MONTHS AGO"><input type="tel" class="form-control" name="numberoftransactions_4monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_4monthsago())==true?bankProfileVO.getNumberoftransactions_4monthsago():""%>"/><%if(validationErrorList.getError("numberoftransactions_4monthsago")!=null){%><span class="apperrormsg">Invalid numberoftransactions_4monthsago </span><%}%></td>
                                        <td align="center" data-label="3 MONTHS AGO"><input type="tel" class="form-control" name="numberoftransactions_3monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_3monthsago())==true?bankProfileVO.getNumberoftransactions_3monthsago():""%>"/><%if(validationErrorList.getError("numberoftransactions_3monthsago")!=null){%><span class="apperrormsg">Invalid numberoftransactions_3monthsago </span><%}%></td>
                                        <td align="center" data-label="2 MONTHS AGO"><input type="tel" class="form-control" name="numberoftransactions_2monthsago" maxlength="10" onkeypress="return isNumberKey(event)"   <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_2monthsago())==true?bankProfileVO.getNumberoftransactions_2monthsago():""%>" /><%if(validationErrorList.getError("numberoftransactions_2monthsago")!=null){%><span class="apperrormsg">Invalid numberoftransactions_2monthsago </span><%}%></td>
                                        <td align="center" data-label="1 MONTHS AGO"><input type="tel" class="form-control" name="numberoftransactions_lastmonth" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_lastmonth())==true?bankProfileVO.getNumberoftransactions_lastmonth():""%>" /><%if(validationErrorList.getError("numberoftransactions_lastmonth")!=null){%><span class="apperrormsg">Invalid numberoftransactions_lastmonth  </span><%}%></td>

                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO"><input type="tel" class="form-control" name="numberoftransactions_12monthsago" maxlength="10" onkeypress="return isNumberKey(event)" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_12monthsago())==true?bankProfileVO.getNumberoftransactions_12monthsago():""%>"/><%if(validationErrorList.getError("numberoftransactions_12monthsago")!=null){%><span class="apperrormsg">Invalid numberoftransactions_12monthsago </span><%}%></td>
                                        <td align="center" data-label="2 YEARS AGO"><input type="tel" class="form-control" name="numberoftransactions_year2" maxlength="10" onkeypress="return isNumberKey(event)" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_year2())==true?bankProfileVO.getNumberoftransactions_year2():""%>"/><%if(validationErrorList.getError("numberoftransactions_year2")!=null){%><span class="apperrormsg">Invalid numberoftransactions_2years </span><%}%></td>
                                        <td align="center" data-label="3 YEARS AGO"><input type="tel" class="form-control" name="numberoftransactions_year3" maxlength="10" onkeypress="return isNumberKey(event)" onchange="" onblur="" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberoftransactions_year3())==true?bankProfileVO.getNumberoftransactions_year3():""%>"/><%if(validationErrorList.getError("numberoftransactions_year3")!=null){%><span class="apperrormsg">Invalid numberoftransactions_3years </span><%}%></td>
                                    </tr>
                                    <tr>
                                        <td align="center" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Chargeback_volume%></td>
                                        <td align="center" data-label="6 MONTHS AGO"><input type="tel" class="form-control" name="chargebackvolume_6monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate6monthsago()"onblur="calculate6monthsago()"  <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_6monthsago())==true?bankProfileVO.getChargebackvolume_6monthsago():""%>"/><%if(validationErrorList.getError("chargebackvolume_6monthsago")!=null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>
                                        <td align="center" data-label="5 MONTHS AGO"><input type="tel" class="form-control" name="chargebackvolume_5monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate5monthsago()" onblur="calculate5monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_5monthsago())==true?bankProfileVO.getChargebackvolume_5monthsago():""%>"/><%if(validationErrorList.getError("chargebackvolume_5monthsago")!=null){%><span class="apperrormsg">Invalid chargebackvolume</span><%}%></td>
                                        <td align="center" data-label="4 MONTHS AGO"><input type="tel" class="form-control" name="chargebackvolume_4monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate4monthsago()"onblur="calculate4monthsago()"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_4monthsago())==true?bankProfileVO.getChargebackvolume_4monthsago():""%>"/><%if(validationErrorList.getError("chargebackvolume_4monthsago")!=null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>
                                        <td align="center" data-label="3 MONTHS AGO"><input type="tel" class="form-control" name="chargebackvolume_3monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate3monthsago()" onblur="calculate3monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_3monthsago())==true?bankProfileVO.getChargebackvolume_3monthsago():""%>"/><%if(validationErrorList.getError("chargebackvolume_3monthsago")!= null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>
                                        <td align="center" data-label="2 MONTHS AGO"><input type="tel" class="form-control" name="chargebackvolume_2monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate2monthsago()" onblur="calculate2monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_2monthsago())==true?bankProfileVO.getChargebackvolume_2monthsago():""%>"/><%if(validationErrorList.getError("chargebackvolume_2monthsago")!=null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>
                                        <td align="center" data-label="1 MONTHS AGO"><input type="tel" class="form-control" name="chargebackvolume_lastmonth" maxlength="10"  onkeypress="return isNumberKey(event)" onchange="calculateLastMonth()" onblur="calculateLastMonth()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_lastmonth())==true?bankProfileVO.getChargebackvolume_lastmonth():""%>"  /><%if(validationErrorList.getError("chargebackvolume_lastmonth")!=null){%><span class="apperrormsg">Invalid chargebackvolume</span><%}%></td>


                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO"><input type="tel" class="form-control" name="chargebackvolume_12monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate12monthsago()" onblur="calculate12monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_12monthsago())==true?bankProfileVO.getChargebackvolume_12monthsago():""%>"/><%if(validationErrorList.getError("chargebackvolume_12monthsago")!=null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>
                                        <td align="center" data-label="2 YEARS AGO"><input type="tel" class="form-control" name="chargebackvolume_year2" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear2()" onblur="calculateyear2()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_year2())==true?bankProfileVO.getChargebackvolume_year2():""%>"/><%if(validationErrorList.getError("chargebackvolume_year2")!=null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>
                                        <td align="center" data-label="3 YEARS AGO"><input type="tel" class="form-control" name="chargebackvolume_year3" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear3()" onblur="calculateyear3()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getChargebackvolume_year3())==true?bankProfileVO.getChargebackvolume_year3():""%>"/><%if(validationErrorList.getError("chargebackvolume_year3")!=null){%><span class="apperrormsg">Invalid chargebackvolume </span><%}%></td>

                                    </tr>
                                    <tr>
                                        <td align="center" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Number_chargebacks%></td>
                                        <td align="center" data-label="6 MONTHS AGO"><input type="tel" class="form-control" name="numberofchargebacks_6monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_6monthsago())==true?bankProfileVO.getNumberofchargebacks_6monthsago():""%>"/><%if(validationErrorList.getError("numberofchargebacks_6monthsago")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_6monthsago</span><%}%></td>
                                        <td align="center" data-label="5 MONTHS AGO"><input type="tel" class="form-control" name="numberofchargebacks_5monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_5monthsago())==true?bankProfileVO.getNumberofchargebacks_5monthsago():""%>"/><%if(validationErrorList.getError("numberofchargebacks_5monthsago")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_5monthsago</span><%}%></td>
                                        <td align="center" data-label="4 MONTHS AGO"><input type="tel" class="form-control" name="numberofchargebacks_4monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_4monthsago())==true?bankProfileVO.getNumberofchargebacks_4monthsago():""%>"/><%if(validationErrorList.getError("numberofchargebacks_4monthsago")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_4monthsago </span><%}%></td>
                                        <td align="center" data-label="3 MONTHS AGO"><input type="tel" class="form-control" name="numberofchargebacks_3monthsago" maxlength="10" onkeypress="return isNumberKey(event)"   <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_3monthsago())==true?bankProfileVO.getNumberofchargebacks_3monthsago():""%>"/><%if(validationErrorList.getError("numberofchargebacks_3monthsago")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_3monthsago </span><%}%></td>
                                        <td align="center" data-label="2 MONTHS AGO"><input type="tel" class="form-control" name="numberofchargebacks_2monthsago" maxlength="10" onkeypress="return isNumberKey(event)"   <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_2monthsago())==true?bankProfileVO.getNumberofchargebacks_2monthsago():""%>"/><%if(validationErrorList.getError("numberofchargebacks_2monthsago")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_2monthsago</span><%}%></td>
                                        <td align="center" data-label="1 MONTHS AGO"><input type="tel" class="form-control" name="numberofchargebacks_lastmonth" maxlength="10"  onkeypress="return isNumberKey(event)"   <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_lastmonth())==true?bankProfileVO.getNumberofchargebacks_lastmonth():""%>"/><%if(validationErrorList.getError("numberofchargebacks_lastmonth")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_lastmonth </span><%}%></td>


                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO"><input type="tel" class="form-control" name="numberofchargebacks_12monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate12monthsago()" onblur="calculate12monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_12monthsago())==true?bankProfileVO.getNumberofchargebacks_12monthsago():""%>"/><%if(validationErrorList.getError("numberofchargebacks_12monthsago")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_12monthsago </span><%}%></td>
                                        <td align="center" data-label="2 YEARS AGO"><input type="tel" class="form-control" name="numberofchargebacks_year2" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear2()" onblur="calculateyear2()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_year2())==true?bankProfileVO.getNumberofchargebacks_year2():""%>"/><%if(validationErrorList.getError("numberofchargebacks_year2")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_2years </span><%}%></td>
                                        <td align="center" data-label="3 YEARS AGO"><input type="tel" class="form-control" name="numberofchargebacks_year3" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear3()" onblur="calculateyear3()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofchargebacks_year3())==true?bankProfileVO.getNumberofchargebacks_year3():""%>"/><%if(validationErrorList.getError("numberofchargebacks_year3")!=null){%><span class="apperrormsg">Invalid numberofchargebacks_3years </span><%}%></td>
                                    </tr>
                                    <tr>
                                        <td align="center" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Refunds_volume%></td>
                                        <td align="center" data-label="6 MONTHS AGO"><input type="tel" class="form-control" name="refundsvolume_6monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate6monthsago()" onblur="calculate6monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_6monthsago())==true?bankProfileVO.getRefundsvolume_6monthsago():""%>"/><%if(validationErrorList.getError("refundsvolume_6monthsago")!=null){%><span class="apperrormsg">Invalid refundsvolume</span><%}%></td>
                                        <td align="center" data-label="5 MONTHS AGO"><input type="tel" class="form-control" name="refundsvolume_5monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate5monthsago()" onblur="calculate5monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_5monthsago())==true?bankProfileVO.getRefundsvolume_5monthsago():""%>"/><%if(validationErrorList.getError("refundsvolume_5monthsago")!=null){%><span class="apperrormsg">Invalid refundsvolume</span><%}%></td>
                                        <td align="center" data-label="4 MONTHS AGO"><input type="tel" class="form-control" name="refundsvolume_4monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate4monthsago()" onblur="calculate4monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_4monthsago())==true?bankProfileVO.getRefundsvolume_4monthsago():""%>"/><%if(validationErrorList.getError("refundsvolume_4monthsago")!=null){%><span class="apperrormsg">Invalid refundsvolume</span><%}%></td>
                                        <td align="center" data-label="3 MONTHS AGO"><input type="tel" class="form-control" name="refundsvolume_3monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate3monthsago()" onblur="calculate3monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_3monthsago())==true?bankProfileVO.getRefundsvolume_3monthsago():""%>"/><%if(validationErrorList.getError("refundsvolume_3monthsago")!=null){%><span class="apperrormsg">Invalid refundsvolume</span><%}%></td>
                                        <td align="center" data-label="2 MONTHS AGO"><input type="tel" class="form-control" name="refundsvolume_2monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate2monthsago()" onblur="calculate2monthsago()" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_2monthsago())==true?bankProfileVO.getRefundsvolume_2monthsago():""%>" /><%if(validationErrorList.getError("refundsvolume_2monthsago")!=null){%><span class="apperrormsg">Invalid refundsvolume </span><%}%></td>
                                        <td align="center" data-label="1 MONTHS AGO"><input type="tel" class="form-control" name="refundsvolume_lastmonth" maxlength="10"  onkeypress="return isNumberKey(event)" onchange="calculateLastMonth()" onblur="calculateLastMonth()"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_lastmonth())==true?bankProfileVO.getRefundsvolume_lastmonth():""%>"/><%if(validationErrorList.getError("refundsvolume_lastmonth")!=null){%><span class="apperrormsg">Invalid refundsvolume </span><%}%></td>


                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO"><input type="tel" class="form-control" name="refundsvolume_12monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate12monthsago()" onblur="calculate12monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_12monthsago())==true?bankProfileVO.getRefundsvolume_12monthsago():""%>"/><%if(validationErrorList.getError("refundsvolume_12monthsago")!=null){%><span class="apperrormsg">Invalid refundsvolume_12monthsago </span><%}%></td>
                                        <td align="center" data-label="2 YEARS AGO"><input type="tel" class="form-control" name="refundsvolume_year2" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear2()" onblur="calculateyear2()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_year2())==true?bankProfileVO.getRefundsvolume_year2():""%>"/><%if(validationErrorList.getError("refundsvolume_year2")!=null){%><span class="apperrormsg">Invalid refundsvolume_2years </span><%}%></td>
                                        <td align="center" data-label="3 YEARS AGO"><input type="tel" class="form-control" name="refundsvolume_year3" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear3()" onblur="calculateyear3()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getRefundsvolume_year3())==true?bankProfileVO.getRefundsvolume_year3():""%>"/><%if(validationErrorList.getError("refundsvolume_year3")!=null){%><span class="apperrormsg">Invalid refundsvolume_3years </span><%}%></td>
                                    </tr>
                                    <tr>
                                        <td align="center" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Number_refunds%></td>
                                        <td align="center" data-label="6 MONTHS AGO"><input type="tel" class="form-control" name="numberofrefunds_6monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_6monthsago())==true?bankProfileVO.getNumberofrefunds_6monthsago():""%>"/><%if(validationErrorList.getError("numberofrefunds_6monthsago")!=null){%><span class="apperrormsg">Invalid numberofrefunds_6monthsago</span><%}%></td>
                                        <td align="center" data-label="5 MONTHS AGO"><input type="tel" class="form-control" name="numberofrefunds_5monthsago" maxlength="10" onkeypress="return isNumberKey(event)"   <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_5monthsago())==true?bankProfileVO.getNumberofrefunds_5monthsago():""%>"/><%if(validationErrorList.getError("numberofrefunds_5monthsago")!=null){%><span class="apperrormsg">Invalid numberofrefunds_5monthsago</span><%}%></td>
                                        <td align="center" data-label="4 MONTHS AGO"><input type="tel" class="form-control" name="numberofrefunds_4monthsago" maxlength="10"   onkeypress="return isNumberKey(event)" <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_4monthsago())==true?bankProfileVO.getNumberofrefunds_4monthsago():""%>"/><%if(validationErrorList.getError("numberofrefunds_4monthsago")!=null){%><span class="apperrormsg">Invalid numberofrefunds_4monthsago</span><%}%></td>
                                        <td align="center" data-label="3 MONTHS AGO"><input type="tel" class="form-control" name="numberofrefunds_3monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_3monthsago())==true?bankProfileVO.getNumberofrefunds_3monthsago():""%>"/><%if(validationErrorList.getError("numberofrefunds_3monthsago")!=null){%><span class="apperrormsg">Invalid numberofrefunds_3monthsago</span><%}%></td>
                                        <td align="center" data-label="2 MONTHS AGO"><input type="tel" class="form-control" name="numberofrefunds_2monthsago" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_2monthsago())==true?bankProfileVO.getNumberofrefunds_2monthsago():""%>"/><%if(validationErrorList.getError("numberofrefunds_2monthsago")!=null){%><span class="apperrormsg">Invalid numberofrefunds_2monthsago</span><%}%></td>
                                        <td align="center" data-label="1 MONTHS AGO"><input type="tel" class="form-control" name="numberofrefunds_lastmonth" maxlength="10"  onkeypress="return isNumberKey(event)"  <%=globaldisabled%> value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_lastmonth())==true?bankProfileVO.getNumberofrefunds_lastmonth():""%>"/><%if(validationErrorList.getError("numberofrefunds_lastmonth")!=null){%><span class="apperrormsg">Invalid numberofrefunds_lastmonth</span><%}%></td>


                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO"><input type="tel" class="form-control" name="numberofrefunds_12monthsago" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculate12monthsago()" onblur="calculate12monthsago()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_12monthsago())==true?bankProfileVO.getNumberofrefunds_12monthsago():""%>"/><%if(validationErrorList.getError("numberofrefunds_12monthsago")!=null){%><span class="apperrormsg">Invalid numberofrefunds_12monthsago </span><%}%></td>
                                        <td align="center" data-label="2 YEARS AGO"><input type="tel" class="form-control" name="numberofrefunds_year2" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear2()" onblur="calculateyear2()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_year2())==true?bankProfileVO.getNumberofrefunds_year2():""%>"/><%if(validationErrorList.getError("numberofrefunds_year2")!=null){%><span class="apperrormsg">Invalid numberofrefunds_2years </span><%}%></td>
                                        <td align="center" data-label="3 YEARS AGO"><input type="tel" class="form-control" name="numberofrefunds_year3" maxlength="10" onkeypress="return isNumberKey(event)" onchange="calculateyear3()" onblur="calculateyear3()" <%=globaldisabled%>  value="<%=functions.isValueNull(bankProfileVO.getNumberofrefunds_year3())==true?bankProfileVO.getNumberofrefunds_year3():""%>"/><%if(validationErrorList.getError("numberofrefunds_year3")!=null){%><span class="apperrormsg">Invalid numberofrefunds_3years </span><%}%></td>
                                    </tr>

                                    <tr>
                                        <td style="background:#ffffff;"></td>
                                        <%--<td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>
                                        <td style="background:#ffffff;"></td>--%>
                                    </tr>

                                    <tr>
                                        <td align="center" id="chargebackratioid" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Chargeback_Ratio%></td>
                                        <td align="center" data-label="6 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_6monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_6monthsago())==true?bankProfileVO.getChargebackratio_6monthsago():""%>"onchange="return calculate6monthsago(this.value,'chargebackratio_6monthsago')" onblur="calculate6monthsago()"/><%if(validationErrorList.getError("chargebackratio_6monthsago")!=null){%><span class="apperrormsg">Invalid chargebackratio</span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_6monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_6monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_6monthsago"><%if(validationErrorList.getError("chargebackratio_6monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_6monthsago"))){%><%=validationErrorList.getError("chargebackratio_6monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="5 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_5monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_5monthsago())==true?bankProfileVO.getChargebackratio_5monthsago():""%>"onchange="return calculate5monthsago(this.value,'chargebackratio_5monthsago')" onblur="calculate5monthsago()"/><%if(validationErrorList.getError("chargebackratio_5monthsago")!=null){%><span class="apperrormsg">Invalid chargebackratio </span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_5monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_5monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_5monthsago"><%if(validationErrorList.getError("chargebackratio_5monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_5monthsago"))){%><%=validationErrorList.getError("chargebackratio_5monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="4 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_4monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_4monthsago())==true?bankProfileVO.getChargebackratio_4monthsago():""%>"onchange="return calculate4monthsago(this.value,'chargebackratio_4monthsago')" onblur="calculate4monthsago()"/><%if(validationErrorList.getError("chargebackratio_4monthsago")!=null){%><span class="apperrormsg">Invalid chargebackratio</span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_4monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_4monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_4monthsago"><%if(validationErrorList.getError("chargebackratio_4monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_4monthsago"))){%><%=validationErrorList.getError("chargebackratio_4monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="3 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_3monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_3monthsago())==true?bankProfileVO.getChargebackratio_3monthsago():""%>"onchange="return calculate3monthsago(this.value,'chargebackratio_3monthsago')" onblur="calculate3monthsago()"/><%if(validationErrorList.getError("chargebackratio_3monthsago")!=null){%><span class="apperrormsg">Invalid chargebackratio </span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_3monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_3monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_3monthsago"><%if(validationErrorList.getError("chargebackratio_3monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_3monthsago"))){%><%=validationErrorList.getError("chargebackratio_3monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="2 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_2monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_2monthsago())==true?bankProfileVO.getChargebackratio_2monthsago():""%>"onchange="return calculate2monthsago(this.value,'chargebackratio_2monthsago')" onblur="calculate2monthsago()"/><%if(validationErrorList.getError("chargebackratio_2monthsago")!=null){%><span class="apperrormsg">Invalid chargebackratio </span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_2monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_2monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_2monthsago"><%if(validationErrorList.getError("chargebackratio_2monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_2monthsago"))){%><%=validationErrorList.getError("chargebackratio_2monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="1 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_lastmonth" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_lastmonth())==true?bankProfileVO.getChargebackratio_lastmonth():""%>"onchange="return calculateLastMonth(this.value,'chargebackratio_lastmonth')" onblur="calculateLastMonth()"/><%if(validationErrorList.getError("chargebackratio_lastmonth")!=null){%><span class="apperrormsg">Invalid chargebackratio</span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_lastmonth")!=null && functions.isValueNull(request.getParameter("chargebackratio_lastmonth"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_lastmonth"><%if(validationErrorList.getError("chargebackratio_lastmonth")!=null && functions.isValueNull(request.getParameter("chargebackratio_lastmonth"))){%><%=validationErrorList.getError("chargebackratio_lastmonth").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>

                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_12monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_12monthsago())==true?bankProfileVO.getChargebackratio_12monthsago():""%>"onchange="return calculate12monthsago()" onblur="calculate12monthsago()"/><%if(validationErrorList.getError("chargebackratio_12monthsago")!=null){%><span class="apperrormsg">Invalid chargebackratio</span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_12monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_12monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_12monthsago"><%if(validationErrorList.getError("chargebackratio_12monthsago")!=null && functions.isValueNull(request.getParameter("chargebackratio_12monthsago"))){%><%=validationErrorList.getError("chargebackratio_12monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="2 YEARS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_year2" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_year2())==true?bankProfileVO.getChargebackratio_year2():""%>"onchange="return calculateyear2(this.value,'chargebackratio_year2')" onblur="calculateyear2()"/><%if(validationErrorList.getError("chargebackratio_year2")!=null){%><span class="apperrormsg">Invalid chargebackratio</span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_year2")!=null && functions.isValueNull(request.getParameter("chargebackratio_year2"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_year2"><%if(validationErrorList.getError("chargebackratio_year2")!=null && functions.isValueNull(request.getParameter("chargebackratio_year2"))){%><%=validationErrorList.getError("chargebackratio_year2").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="3 YEARS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="chargebackratio_year3" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getChargebackratio_year3())==true?bankProfileVO.getChargebackratio_year3():""%>"onchange="return calculateyear3(this.value,'chargebackratio_year3')" onblur="calculateyear3()"/><%if(validationErrorList.getError("chargebackratio_year3")!=null){%><span class="apperrormsg">Invalid chargebackratio</span><%}%> <span style="<%=validationErrorList.getError("chargebackratio_year3")!=null && functions.isValueNull(request.getParameter("chargebackratio_year3"))?"background-color: #f2dede":""%>"class="errormesage" id="chargebackratio_year3"><%if(validationErrorList.getError("chargebackratio_year3")!=null && functions.isValueNull(request.getParameter("chargebackratio_year3"))){%><%=validationErrorList.getError("chargebackratio_year3").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>


                                    </tr>

                                    <tr>
                                        <td align="center" id="refundratioid" class="texthead" style="background-color: #7eccad !important;color: white;vertical-align: middle;text-align: center;"><%=bankapplication_Refund_ratio%></td>
                                        <td align="center" data-label="6 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_6monthsago" maxlength="10" <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3"  value="<%=functions.isValueNull(bankProfileVO.getRefundratio_6monthsago())==true?bankProfileVO.getRefundratio_6monthsago():""%>"onchange="return calculate6monthsago(this.value,'refundratio_6monthsago')" onblur="calculate6monthsago()"/><%if(validationErrorList.getError("refundratio_6monthsago")!=null){%><span class="apperrormsg">Invalid refundratio</span><%}%> <span style="<%=validationErrorList.getError("refundratio_6monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_6monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_6monthsago"><%if(validationErrorList.getError("refundratio_6monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_6monthsago"))){%><%=validationErrorList.getError("refundratio_6monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="5 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_5monthsago" maxlength="10" <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3"  value="<%=functions.isValueNull(bankProfileVO.getRefundratio_5monthsago())==true?bankProfileVO.getRefundratio_5monthsago():""%>"onchange="return calculate5monthsago(this.value,'refundratio_5monthsago')" onblur="calculate5monthsago()"/><%if(validationErrorList.getError("refundratio_5monthsago")!=null){%><span class="apperrormsg">Invalid refundratio</span><%}%> <span style="<%=validationErrorList.getError("refundratio_5monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_5monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_5monthsago"><%if(validationErrorList.getError("refundratio_5monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_5monthsago"))){%><%=validationErrorList.getError("refundratio_5monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="4 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_4monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_4monthsago())==true?bankProfileVO.getRefundratio_4monthsago():""%>"onchange="return calculate4monthsago(this.value,'refundratio_4monthsago')" onblur="calculate4monthsago()"/><%if(validationErrorList.getError("refundratio_4monthsago")!=null){%><span class="apperrormsg">Invalid  refundratio </span><%}%> <span style="<%=validationErrorList.getError("refundratio_4monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_4monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_4monthsago"><%if(validationErrorList.getError("refundratio_4monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_4monthsago"))){%><%=validationErrorList.getError("refundratio_4monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="3 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_3monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_3monthsago())==true?bankProfileVO.getRefundratio_3monthsago():""%>"onchange="return calculate3monthsago(this.value,'refundratio_3monthsago')" onblur="calculate3monthsago()"/><%if(validationErrorList.getError("refundratio_3monthsago")!=null){%><span class="apperrormsg">Invalid refundratio </span><%}%> <span style="<%=validationErrorList.getError("refundratio_3monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_3monthsago"))?"background-color: #f2dede":""%>"class="errormesage"
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            id="refundratio_3monthsago"><%if(validationErrorList.getError("refundratio_3monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_3monthsago"))){%><%=validationErrorList.getError("refundratio_3monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="2 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_2monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_2monthsago())==true?bankProfileVO.getRefundratio_2monthsago():""%>"onchange="return calculate2monthsago(this.value,'refundratio_2monthsago')" onblur="calculate2monthsago()"/><%if(validationErrorList.getError("refundratio_2monthsago")!=null){%><span class="apperrormsg">Invalid refundratio </span><%}%> <span style="<%=validationErrorList.getError("refundratio_2monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_2monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_2monthsago"><%if(validationErrorList.getError("refundratio_2monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_2monthsago"))){%><%=validationErrorList.getError("refundratio_2monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="1 MONTHS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_lastmonth" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_lastmonth())==true?bankProfileVO.getRefundratio_lastmonth():""%>"onchange="return calculateLastMonth(this.value,'refundratio_lastmonth')" onblur="calculateLastMonth()"/><%if(validationErrorList.getError("refundratio_lastmonth")!=null){%><span class="apperrormsg">Invalid refundratio </span><%}%> <span style="<%=validationErrorList.getError("refundratio_lastmonth")!=null && functions.isValueNull(request.getParameter("refundratio_lastmonth"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_lastmonth"><%if(validationErrorList.getError("refundratio_lastmonth")!=null && functions.isValueNull(request.getParameter("refundratio_lastmonth"))){%><%=validationErrorList.getError("refundratio_lastmonth").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <!--NEW COLUMN ADDED-->
                                        <td align="center" data-label="1 YEAR AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_12monthsago" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_12monthsago())==true?bankProfileVO.getRefundratio_12monthsago():""%>"onchange="return calculate12monthsago(this.value,'getRefundratio_12monthsago')" onblur="calculate12monthsago()"/><%if(validationErrorList.getError("getRefundratio_12monthsago")!=null){%><span class="apperrormsg">Invalid refundratio</span><%}%> <span style="<%=validationErrorList.getError("refundratio_12monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_12monthsago"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_12monthsago"><%if(validationErrorList.getError("refundratio_12monthsago")!=null && functions.isValueNull(request.getParameter("refundratio_12monthsago"))){%><%=validationErrorList.getError("refundratio_12monthsago").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="2 YEARS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_year2" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_year2())==true?bankProfileVO.getRefundratio_year2():""%>"onchange="return calculateyear2(this.value,'refundratio_year2')" onblur="calculateyear2()"/><%if(validationErrorList.getError("refundratio_year2")!=null){%><span class="apperrormsg">Invalid refundratio</span><%}%> <span style="<%=validationErrorList.getError("refundratio_year2")!=null && functions.isValueNull(request.getParameter("refundratio_year2"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_year2"><%if(validationErrorList.getError("refundratio_year2")!=null && functions.isValueNull(request.getParameter("refundratio_year2"))){%><%=validationErrorList.getError("refundratio_year2").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>
                                        <td align="center" data-label="3 YEARS AGO">
                                            <div class="input-group">
                                                <input type="tel" class="form-control" name="refundratio_year3" maxlength="10"  <%=globaldisabled%> readonly="true" style="background-color: #d3d3d3" value="<%=functions.isValueNull(bankProfileVO.getRefundratio_year3())==true?bankProfileVO.getRefundratio_year3():""%>"onchange="return calculateyear3(this.value,'refundratio_year3')" onblur="calculateyear3()"/><%if(validationErrorList.getError("refundratio_year3")!=null){%><span class="apperrormsg">Invalid refundratio</span><%}%> <span style="<%=validationErrorList.getError("refundratio_year3")!=null && functions.isValueNull(request.getParameter("refundratio_year3"))?"background-color: #f2dede":""%>"class="errormesage" id="refundratio_year3"><%if(validationErrorList.getError("refundratio_year3")!=null && functions.isValueNull(request.getParameter("refundratio_year3"))){%><%=validationErrorList.getError("refundratio_year3").getLogMessage()%><%}%></span><span class="input-group-addon" style="font-weight: 800;">%</span>
                                            </div>
                                        </td>

                                    </tr>
                                </table>

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


        </div>
    </div>
    </div>


    <%
        }
    %>

    <%

        if(bankProfileInputList.size()==0 && !view)
        {
            out.println("<div class=\"content-page\">");
            out.println("<div class=\"content\">");
            out.println("<div class=\"page-heading\">");
            out.println("<div class=\"row\">");
            out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
            out.println("<div class=\"widget\">");
            out.println("<div class=\"widget-header transparent\">\n" +
                    "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+bankapplication_BankProfile+"</strong></h2>\n" +
                    "                                <div class=\"additional-btn\">\n" +
                    "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                    "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                    "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                    "                                </div>\n" +
                    "                            </div>");
            out.println("<div class=\"widget-content padding\">");
          /*out.println("<div class=\"table-responsive\">");*/
            out.println(Functions.NewShowConfirmation1(bankapplication_Profile,bankapplication_details));         /* out.println("</div>");*/
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
            out.println("</div>");
        }
    %>






