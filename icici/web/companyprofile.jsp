<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.utils.AppFunctionUtil" %>
<%@ page import="com.validators.BankInputName" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.vo.applicationManagerVOs.*" %>

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

<script type="text/javascript">
    $('#sandbox-container input').datepicker({});
</script>


<style type="text/css">
    .form-control {
        /*height: 35px;*/
    }

    /*.input-group{
        display: flex;
    }*/
    .input-group-addon {
        font-weight: 800;
        /*color: #a94442;
        background-color: #ebccd1;*/
        text-shadow: 1px 0px;
        border-radius: inherit;
        /*padding: 10px 25px 10px 15px;*/
    }

    .glyphicon {
        display: block !important;
        color: #a94442 !important;
        background-color: #ebccd1 !important;
        width: 40px !important;
        margin-right: 16px !important;
        height: 32px !important;
        /*margin-top: -1px !important;*/
    }

    #align_check > .icheckbox_square-aero {
        display: table;
    }

    @media (max-width: 364px) {
        #income_sources_other_yes {
            margin-top: 10px;
            margin-left: 20px;
            width: 90% !important;
        }

        #income_row {
            margin-left: -15px !important;
        }
    }

    #income_row {
        margin-left: 0 !important;
    }

    @media (max-width: 991px) {
        #margintop10 {
            margin-top: 10px;
        }

        #top10 {
            top: 10px;
        }
    }
</style>

<%!
    private Functions functions = new Functions();
    private AppFunctionUtil appFunctionUtil= new AppFunctionUtil();
%>
<%

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

    String selectedY="";
    String selectedN="";
    String disableEU="";
    String bgcolor="";
    ApplicationManagerVO applicationManagerVO=null;
    CompanyProfileVO companyProfileVO = null;

    //Start : Added to ensure OwnershipProfileVO is not null
   /* OwnershipProfileVO ownershipProfileVO=null;
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_shareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_corporateShareholder3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_director3 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory1 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory2 = new OwnershipProfileDetailsVO();
    OwnershipProfileDetailsVO ownershipProfileDetailsVO_authorizeSignatory3 = new OwnershipProfileDetailsVO();
    Map<String, OwnershipProfileDetailsVO> ownershipProfileDetailsVOMap = new HashMap();*/
    //End : Added to ensure OwnershipProfileVO is not null


    AddressIdentificationVO identificationVO_company = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_business = new AddressIdentificationVO();
    AddressIdentificationVO identificationVO_euCompany = new AddressIdentificationVO();
    ContactDetailsVO contactDetailsVO_main = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_technical = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_billing = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_cbk = new ContactDetailsVO();
    ContactDetailsVO contactDetailsVO_pci = new ContactDetailsVO();
    ValidationErrorList validationErrorList = null;

    Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    Map<Boolean, Set<BankInputName>> fullPageViseValidationForStep = new HashMap<Boolean, Set<BankInputName>>();
    Set<BankInputName> companyProfileInputList=new HashSet<BankInputName>();
    Map<String, AddressIdentificationVO> addressIdentificationVOMap = new HashMap();
    Map<String, ContactDetailsVO> contactDetailsVOMap = new HashMap();
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
                companyProfileInputList.addAll(fullPageViseValidationForStep.get(false));
            if(fullPageViseValidationForStep.containsKey(true))
                companyProfileInputList.addAll(fullPageViseValidationForStep.get(true));
        }
    }


    if(session.getAttribute("applicationManagerVO")!=null)
    {
        applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
    }

    if(applicationManagerVO.getCompanyProfileVO() != null)
    {
        companyProfileVO = applicationManagerVO.getCompanyProfileVO();
        if(companyProfileVO.getCompanyProfile_addressVOMap()!=null && companyProfileVO.getCompanyProfile_addressVOMap().size() > 0)
        {
            identificationVO_company = companyProfileVO.getCompanyProfile_addressVOMap().get("COMPANY");
            identificationVO_business = companyProfileVO.getCompanyProfile_addressVOMap().get("BUSINESS");
            identificationVO_euCompany = companyProfileVO.getCompanyProfile_addressVOMap().get("EU_COMPANY");
            contactDetailsVO_main = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("MAIN");
            contactDetailsVO_billing = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("BILLING");
            contactDetailsVO_technical = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("TECHNICAL");
            contactDetailsVO_cbk = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("CBK");
            contactDetailsVO_pci = companyProfileVO.getCompanyProfile_contactInfoVOMap().get("PCI");
        }
    }

   /* if(companyProfileVO == null)
    {
        companyProfileVO = new CompanyProfileVO();
        addressIdentificationVOMap.put("COMPANY",identificationVO_company);
        addressIdentificationVOMap.put("BUSINESS", identificationVO_business);
        addressIdentificationVOMap.put("EU_COMPANY", identificationVO_euCompany);
        contactDetailsVOMap.put("MAIN", contactDetailsVO_main);
        contactDetailsVOMap.put("BILLING", contactDetailsVO_billing);
        contactDetailsVOMap.put("TECHNICAL", contactDetailsVO_technical);
        contactDetailsVOMap.put("CBK", contactDetailsVO_cbk);
        contactDetailsVOMap.put("PCI", contactDetailsVO_pci);
    }


    if(applicationManagerVO.getOwnershipProfileVO()==null)
    {
        ownershipProfileVO = new OwnershipProfileVO();
        ownershipProfileDetailsVOMap.put("SHAREHOLDER1",ownershipProfileDetailsVO_shareholder1);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER2",ownershipProfileDetailsVO_shareholder2);
        ownershipProfileDetailsVOMap.put("SHAREHOLDER3",ownershipProfileDetailsVO_shareholder3);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER1",ownershipProfileDetailsVO_corporateShareholder1);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER2",ownershipProfileDetailsVO_corporateShareholder2);
        ownershipProfileDetailsVOMap.put("CORPORATESHAREHOLDER3",ownershipProfileDetailsVO_corporateShareholder3);
        ownershipProfileDetailsVOMap.put("DIRECTOR1",ownershipProfileDetailsVO_director1);
        ownershipProfileDetailsVOMap.put("DIRECTOR2",ownershipProfileDetailsVO_director2);
        ownershipProfileDetailsVOMap.put("DIRECTOR3",ownershipProfileDetailsVO_director3);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY1",ownershipProfileDetailsVO_authorizeSignatory1);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY2",ownershipProfileDetailsVO_authorizeSignatory2);
        ownershipProfileDetailsVOMap.put("AUTHORIZESIGNATORY3",ownershipProfileDetailsVO_authorizeSignatory3);
        ownershipProfileVO.setOwnershipProfileDetailsVOMap(ownershipProfileDetailsVOMap);
        applicationManagerVO.setOwnershipProfileVO(ownershipProfileVO);
    }*/

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
    if(functions.isValueNull(companyProfileVO.getCompanyRegisteredEU()) && "Y".equals(companyProfileVO.getCompanyRegisteredEU()))
    {
        selectedY="checked";
        selectedN="disabled";
    }
    else
    {
        selectedN="checked";
        disableEU="disabled";
        selectedY="disabled";
        bgcolor="background-color:#EBEBE4";
    }

%>
<script>
    $( document ).ready(function() {
        $('#billingtoo').click(function(){
            if($('#billingtoo').prop("checked") == true){
                document.getElementById('corporatename').value = document.getElementById('merchantname').value;

            }

        });
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        $("input[name='startup_business']").on("click", function () {
            if ($('input:radio[name=startup_business]:checked').val()=="N")
            {
                document.myformname.company_lengthoftime_business.disabled = false;
            }
            else
            {
                document.myformname.company_lengthoftime_business.disabled = true;
                document.myformname.company_lengthoftime_business.value = "";
            }
        });


        $( "input[name='company_bankruptcy']").on( "click", function() {
            if($('input:radio[name=company_bankruptcy]:checked').val()=="N")
            {
                document.myformname.company_bankruptcydate.disabled = true;
                document.myformname.company_bankruptcydate.value = "";
            }
            else
            {
                document.myformname.company_bankruptcydate.disabled = false;
            }
        });


        $( "input[name='iscompany_insured']").on( "click", function() {
            if($('input:radio[name=iscompany_insured]:checked').val()=="Y")
            {
                document.myformname.insured_companyname.disabled = false;
                document.myformname.insured_currency.disabled = false;
                document.myformname.insured_amount.disabled = false;
            }
            else
            {
                document.myformname.insured_companyname.disabled = true;
                document.myformname.insured_companyname.value = "";
                document.myformname.insured_currency.disabled = true;
                document.myformname.insured_currency.value = "";
                document.myformname.insured_amount.disabled = true;
                document.myformname.insured_amount.value = "";
            }
        });
        $( "input[name='income_sources_other']").on( "click", function() {
            if($('input:checkbox[name=income_sources_other]:checked').val()=="Y")
            {
                document.myformname.income_sources_other_yes.disabled = false;
            }
            else
            {
                document.myformname.income_sources_other_yes.disabled = true;
                document.myformname.income_sources_other_yes.value = "";
            }
        });

        $( "input[name='iscompany_insured']").on( "click", function() {
            if($('input:radio[name=iscompany_insured]:checked').val()=="Y")
            {
                document.myformname.insured_companyname.disabled = false;
                document.myformname.insured_currency.disabled = false;
                document.myformname.insured_amount.disabled = false;
            }
            else
            {
                document.myformname.insured_companyname.disabled = true;
                document.myformname.insured_companyname.value = "";
                document.myformname.insured_currency.disabled = true;
                document.myformname.insured_currency.value = "";
                document.myformname.insured_amount.disabled = true;
                document.myformname.insured_amount.value = "";
            }
        });
        $( "input[name='income_sources_other']").on( "click", function() {
            if($('input:checkbox[name=income_sources_other]:checked').val()=="Y")
            {
                document.myformname.income_sources_other_yes.disabled = false;
            }
            else
            {
                document.myformname.income_sources_other_yes.disabled = true;
                document.myformname.income_sources_other_yes.value = "";
            }
        });

        //On LOAD

        if($('input:radio[name=License_required]:checked').val()=="N")
        {
            document.getElementById('License_Permission').disabled = true;
            document.getElementById('License_Permission').disabled = true;
            $('input:radio[name=License_Permission]').each(function(){
                $(this).attr('disabled','disabled');
                $(this).parent('.iradio_square-aero').addClass('disabled');
            });
        }
        else
        {
            document.getElementById('License_Permission').disabled = false;
            document.getElementById('License_Permission').disabled = false;
            $('input:radio[name=License_Permission]').each(function(){
                $(this).removeAttr('disabled');
                $(this).parent('.iradio_square-aero').removeClass('disabled');
            });
        }


        //On CLICK

        $( "input[name='License_required']").on( "click", function() {
            if($('input:radio[name=License_required]:checked').val()=="N")
            {
                document.getElementById('License_Permission').disabled = true;
                document.getElementById('License_Permission').disabled = true;
                $('input:radio[name=License_Permission]').each(function(){
                    $(this).attr('disabled','disabled');
                    $(this).parent('.iradio_square-aero').addClass('disabled');
                });
            }
            else
            {
                document.getElementById('License_Permission').disabled = false;
                document.getElementById('License_Permission').disabled = false;
                $('input:radio[name=License_Permission]').each(function(){
                    $(this).removeAttr('disabled');
                    $(this).parent('.iradio_square-aero').removeClass('disabled');
                });
            }
        });



        $('#billingtoo').click(function(){
            if($('#billingtoo').prop("checked") == true){
                document.getElementById('corporatecountry').value = document.getElementById('merchantcountry').value;
                document.getElementById('corporatestreet').value = document.getElementById('merchantstreet').value;
                document.getElementById('corporatename').value = document.getElementById('merchantname').value;
                document.getElementById('corporateaddress').value = document.getElementById('locationaddress').value;
                document.getElementById('corporate_addressproof').value = document.getElementById('merchant_addressproof').value;
                document.getElementById('corporate_addressId').value = document.getElementById('merchant_addressId').value;
                document.getElementById('corporatecity').value = document.getElementById('merchantcity').value;
                document.getElementById('corporatestate').value = document.getElementById('merchantstate').value;
                document.getElementById('corporatezipcode').value = document.getElementById('merchantzipcode').value;
            }

        });

        $('#eucompany').click(function(){
            if($('#eucompany').prop("checked") == true){
                document.getElementById('registered_corporatename').value = document.getElementById('merchantname').value;
                document.getElementById('registered_directors_address').value = document.getElementById('locationaddress').value;
                document.getElementById('registered_directors_addressproof').value = document.getElementById('merchant_addressproof').value;
                document.getElementById('registered_directors_addressId').value = document.getElementById('merchant_addressId').value;
                document.getElementById('EURegistrationNumber').value = document.getElementById('companyregistrationnumber').value;
                document.getElementById('registered_directors_city').value = document.getElementById('merchantcity').value;
                document.getElementById('registered_directors_State').value = document.getElementById('merchantstate').value;
                document.getElementById('registered_directors_country').value = document.getElementById('merchantcountry').value;
                document.getElementById('registered_directors_street').value = document.getElementById('merchantstreet').value;
                document.getElementById('registered_directors_postalcode').value = document.getElementById('merchantzipcode').value;
            }

        });
    });

    function myjunk1(fromaction,toaction)
    {

        if(this.document.forms["myformname"][fromaction].value!=null)
        {

            var hat1 = this.document.forms["myformname"][fromaction].selectedIndex ;

            var hatto1 = this.document.forms["myformname"][fromaction].options[hat1].value;
            if (hatto1 != '')
            {

                document.forms["myformname"][toaction].value = hatto1.split("|")[0];
                this.document.forms["myformname"][toaction].value = hatto1.split("|")[1];
                if( hatto1.split("|")[2]=="Y")
                {
                    document.getElementById("eucompany").checked = false;
                    document.getElementById("eucompany").disabled = true;
                    document.myformname.EURegistrationNumber.disabled = true
                    document.myformname.registered_corporatename.disabled = true
                    document.myformname.registered_directors.disabled = true
                    document.myformname.registered_directors_country.disabled = true
                    document.myformname.registered_directors_street.disabled = true
                    $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#EBEBE4');
                    document.myformname.registered_directors_address.disabled = true
                    document.myformname.registered_directors_city.disabled = true
                    document.myformname.registered_directors_State.disabled = true
                    document.myformname.registered_directors_postalcode.disabled = true
                    document.myformname.registered_directors_addressproof.disabled = true
                    document.myformname.registered_directors_addressId.disabled = true


                }
                else
                {
                    document.getElementById("eucompany").checked = true;
                    document.getElementById("eucompany").disabled = false;
                    document.myformname.EURegistrationNumber.disabled = false
                    document.myformname.registered_corporatename.disabled = false
                    document.myformname.registered_directors.disabled = false
                    document.myformname.registered_directors_country.disabled = false
                    document.myformname.registered_directors_street.disabled = false
                    $(document.getElementsByName("registered_directors_country")[0]).css('background-color','#ffffff');
                    document.myformname.registered_directors_address.disabled = false
                    document.myformname.registered_directors_city.disabled = false
                    document.myformname.registered_directors_State.disabled = false
                    document.myformname.registered_directors_postalcode.disabled = false
                    document.myformname.registered_directors_addressproof.disabled = false
                    document.myformname.registered_directors_addressId.disabled = false

                }

                this.document.forms["myformname"][fromaction].options[hat1].selected=true
            }
            else
            {
                document.forms["myformname"][toaction].value = "";
                this.document.forms["myformname"][toaction].value = "";
            }
        }

    }
</script>

<body onload="myjunk1('merchantcountry','Companyphonecc1');">
<div class="container-fluid ">
    <%
        if(companyProfileInputList.contains(BankInputName.merchantname) || companyProfileInputList.contains(BankInputName.locationaddress) || companyProfileInputList.contains(BankInputName.merchantcity)||
                companyProfileInputList.contains(BankInputName.merchantstate)|| companyProfileInputList.contains(BankInputName.merchantcountry)|| companyProfileInputList.contains(BankInputName.merchantstreet)||
                companyProfileInputList.contains(BankInputName.merchantzipcode)|| companyProfileInputList.contains(BankInputName.Companyphonecc1)|| companyProfileInputList.contains(BankInputName.CompanyTelephoneNO)||
                companyProfileInputList.contains(BankInputName.CompanyFax) || companyProfileInputList.contains(BankInputName.CompanyEmailAddress)  || companyProfileInputList.contains(BankInputName.countryofregistration)  ||
                companyProfileInputList.contains(BankInputName.companyregistrationnumber)  || companyProfileInputList.contains(BankInputName.Company_Date_Registration)  || companyProfileInputList.contains(BankInputName.vatidentification)||
                companyProfileInputList.contains(BankInputName.FederalTaxID) || companyProfileInputList.contains(BankInputName.company_typeofbusiness)||
                companyProfileInputList.contains(BankInputName.corporatename) || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry) || companyProfileInputList.contains(BankInputName.corporatestreet) || companyProfileInputList.contains(BankInputName.corporatezipcode)  ||
                companyProfileInputList.contains(BankInputName.registered_corporatename)  || companyProfileInputList.contains(BankInputName.registered_directors)  || companyProfileInputList.contains(BankInputName.EURegistrationNumber) || companyProfileInputList.contains(BankInputName.registered_directors_country) || companyProfileInputList.contains(BankInputName.registered_directors_street) || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  ||
                companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode)||
                companyProfileInputList.contains(BankInputName.company_turnoverlastyear_unit)  ||companyProfileInputList.contains(BankInputName.company_currencylastyear)  || companyProfileInputList.contains(BankInputName.startup_business) ||companyProfileInputList.contains(BankInputName.company_lengthoftime_business) || companyProfileInputList.contains(BankInputName.company_numberofemployees) ||
                companyProfileInputList.contains(BankInputName.contactname)   || companyProfileInputList.contains(BankInputName.contactemailaddress)   || companyProfileInputList.contains(BankInputName.contactname_telnocc1)  || companyProfileInputList.contains(BankInputName.contactname_telephonenumber)  ||
                companyProfileInputList.contains(BankInputName.SkypeIMaddress)  || companyProfileInputList.contains(BankInputName.contact_designation)  ||
                companyProfileInputList.contains(BankInputName.technicalcontactname)  || companyProfileInputList.contains(BankInputName.technicalemailaddress)  || companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||  companyProfileInputList.contains(BankInputName.Technical_telephonenumber) || companyProfileInputList.contains(BankInputName.technical_designation)  ||
                companyProfileInputList.contains(BankInputName.billingcontactname)   || companyProfileInputList.contains(BankInputName.billingemailaddress)  || companyProfileInputList.contains(BankInputName.financialphonecc1) || companyProfileInputList.contains(BankInputName.Financial_telephonenumber)|| companyProfileInputList.contains(BankInputName.billing_designation)||
                companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) ||companyProfileInputList.contains(BankInputName.legal_proceeding)|| companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||
                companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) ||companyProfileInputList.contains(BankInputName.legal_proceeding)||
                companyProfileInputList.contains(BankInputName.cbk_contactperson) || companyProfileInputList.contains(BankInputName.cbk_email) ||companyProfileInputList.contains(BankInputName.cbk_telephonenumber)  ||companyProfileInputList.contains(BankInputName.cbk_phonecc) || companyProfileInputList.contains(BankInputName.cbk_designation) ||
                companyProfileInputList.contains(BankInputName.pci_contactperson) || companyProfileInputList.contains(BankInputName.pci_email) ||companyProfileInputList.contains(BankInputName.pci_telephonenumber)  ||companyProfileInputList.contains(BankInputName.pci_phonecc) || companyProfileInputList.contains(BankInputName.pci_designation) ||
                companyProfileInputList.contains(BankInputName.iscompany_insured)|| companyProfileInputList.contains(BankInputName.insured_companyname) ||
                companyProfileInputList.contains(BankInputName.main_business_partner)||
                companyProfileInputList.contains(BankInputName.loans) || companyProfileInputList.contains(BankInputName.income_economic_activity) || companyProfileInputList.contains(BankInputName.interest_income) || companyProfileInputList.contains(BankInputName.investments) || companyProfileInputList.contains(BankInputName.income_sources_other) || companyProfileInputList.contains(BankInputName.income_sources_other_yes) || view)
        {
    %>

</div>
<div class="content-page" id="companyid">
    <div class="content" id="companycontent">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row" style="background-color: #d9edf7;; margin-top: 15px;">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <%--<h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Welcome</strong></h2>--%>
                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7;text-align: center"> Welcome</h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <div class="bg-info "style="text-align: center;font-size:16px;/*color: #31708f;*/">
                                    Thank you for choosing us for processing your transactions, we would be happy to be your Payment Processor.
                                    <br> Short description for each step:<br>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        Step 1 : Company Profile
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        Step 2 : Ownership Profile
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        Step 3 : Business/Website Info
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        Step 4 : Bank Profile
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        Step 5 : Cardholder Profile
                                    </div>

                                    <div class="col-md-4" style="padding: 10px; border: 1px solid #53a585; color: rgba(0, 0, 0, 0.54); background-color: #f3f3f3; font-weight: 600;">
                                        Step 6 : KYC Doc Upload
                                    </div>
                                    <br>
                                    If you need more details, please do not hesitate to contact us, we would be more than happy to assist you. Our contact number is:<br> +44 20 3129 5664
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row" style="background-color: #ffffff; margin-top: 15px;">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <%--<h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Company Profile</strong></h2>--%>
                            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color: #9fabb7;text-align: center"> Company Profile</h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <center><h4 class="<%=functions.isValueNull(applicationManagerVO.getMessageColorClass())?applicationManagerVO.getMessageColorClass():"txtboxinfo"%> " style="font-family: Open Sans; font-size: 13px; font-weight: 600;background-color: transparent;"><%=functions.isValueNull(applicationManagerVO.getNotificationMessage())?applicationManagerVO.getNotificationMessage():"Please save Company Profile after entering the data provided"%></h4></center>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantname) || companyProfileInputList.contains(BankInputName.locationaddress) || companyProfileInputList.contains(BankInputName.merchantcity)||companyProfileInputList.contains(BankInputName.merchantstate)|| companyProfileInputList.contains(BankInputName.merchantcountry)||companyProfileInputList.contains(BankInputName.merchantzipcode)|| companyProfileInputList.contains(BankInputName.Companyphonecc1)|| companyProfileInputList.contains(BankInputName.CompanyTelephoneNO)|| companyProfileInputList.contains(BankInputName.CompanyFax) || companyProfileInputList.contains(BankInputName.CompanyEmailAddress)  || companyProfileInputList.contains(BankInputName.countryofregistration)  || companyProfileInputList.contains(BankInputName.companyregistrationnumber)  || companyProfileInputList.contains(BankInputName.Company_Date_Registration)  || companyProfileInputList.contains(BankInputName.vatidentification)|| companyProfileInputList.contains(BankInputName.FederalTaxID)  || companyProfileInputList.contains(BankInputName.company_typeofbusiness) || companyProfileInputList.contains(BankInputName.merchantstreet) || companyProfileInputList.contains(BankInputName.merchant_addressproof) || companyProfileInputList.contains(BankInputName.merchant_addressId)|| view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">Registered Company Information</h2>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantname) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Name*</label>
                                    <input  type="text" class="form-control" id="merchantname" name="merchantname" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getCompany_name())) ? identificationVO_company.getCompany_name():""%>" /><%if(validationErrorList.getError("merchantname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.companyregistrationnumber)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="companyregistrationnumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number*</label>
                                    <input type="text" class="form-control"  id="companyregistrationnumber" name="companyregistrationnumber" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_company.getRegistration_number())) ? identificationVO_company.getRegistration_number():""%>" /><%if(validationErrorList.getError("companyregistrationnumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.Company_Date_Registration)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Date of Registration*</label>
                                    <input type="text" name="Company_Date_Registration" class="form-control datepicker" style="border: 1px solid #b2b2b2;font-weight:bold;width:100%" <%=globaldisabled%> value="<%= (functions.isValueNull(identificationVO_company.getDate_of_registration())) ? appFunctionUtil.convertTimestampToDatepicker(identificationVO_company.getDate_of_registration()):""%>"/><%if(validationErrorList.getError("Company_Date_Registration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.Companyphonecc1)  || view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="Companyphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code*</label>
                                    <input type="text" class="form-control"  id="Companyphonecc1" name="Companyphonecc1" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getPhone_cc())) ? identificationVO_company.getPhone_cc():""%>"/><%if(validationErrorList.getError("Companyphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.CompanyTelephoneNO)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="CompanyTelephoneNO" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Phone No*</label>
                                    <input type="text" class="form-control"   id="CompanyTelephoneNO" name="CompanyTelephoneNO" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  value="<%=(functions.isValueNull(identificationVO_company.getPhone_number())) ? identificationVO_company.getPhone_number():""%>" /><%if(validationErrorList.getError("CompanyTelephoneNO")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.CompanyFax)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="CompanyFax" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Fax</label>
                                    <input type="text" class="form-control"  id="CompanyFax" name="CompanyFax" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getFax())) ? identificationVO_company.getFax():""%>" /><%if(validationErrorList.getError("CompanyFax")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.CompanyEmailAddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="CompanyEmailAddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address*</label>
                                    <input type="text" class="form-control"   id="CompanyEmailAddress" name="CompanyEmailAddress" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getEmail_id())) ? identificationVO_company.getEmail_id():""%>" /><%if(validationErrorList.getError("CompanyEmailAddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.vatidentification)||companyProfileInputList.contains(BankInputName.FederalTaxID)  || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;">Identification Details</h2>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.vatidentification) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="vatidentification" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">VAT Identification Number</label>
                                    <input type="text" class="form-control"  id="vatidentification" name="vatidentification" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getVatidentification()))?identificationVO_company.getVatidentification():""%>" /><%if(validationErrorList.getError("vatidentification")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.FederalTaxID)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="FederalTaxID" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">TIC/TIN Number/Federal Tax ID/PAN</label>
                                    <input type="text" class="form-control"  id="FederalTaxID" name="FederalTaxID" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getFederalTaxId()) )?identificationVO_company.getFederalTaxId():""%>" /><%if(validationErrorList.getError("FederalTaxID")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;">Address Details</h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchant_addressproof)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchant_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof*</label>
                                    <select class="form-control" name="merchant_addressproof" id="merchant_addressproof" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getAddressProofTypes((functions.isValueNull(identificationVO_company.getAddressProof())) ? identificationVO_company.getAddressProof() : ""));
                                        %>
                                    </select>
                                    <%--<%if(validationErrorList.getError("merchant_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>--%>
                                    <%if(validationErrorList.getError("merchant_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchant_addressId)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchant_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID*</label>
                                    <input type="text" class="form-control"  id="merchant_addressId" name="merchant_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getAddressId())) ? identificationVO_company.getAddressId():""%>"/><%if(validationErrorList.getError("merchant_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.locationaddress) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="locationaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Address(no p/o)*</label>
                                    <input type="text" class="form-control"  id="locationaddress" name="locationaddress"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getAddress())) ? StringEscapeUtils.escapeHtml(identificationVO_company.getAddress()):""%>"/><%if(validationErrorList.getError("locationaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantstreet)  || view )
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantstreet" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                    <input type="text" class="form-control"  id="merchantstreet" name="merchantstreet" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getStreet())) ? identificationVO_company.getStreet():""%>" /><%if(validationErrorList.getError("merchantstreet")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantcity)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantcity" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City/Town</label>
                                    <input type="text" class="form-control"   id="merchantcity" name="merchantcity"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getCity())) ? StringEscapeUtils.escapeHtml(identificationVO_company.getCity()):""%>"/><%if(validationErrorList.getError("merchantcity")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantstate)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantstate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County/Province</label>
                                    <input type="text" class="form-control"  id="merchantstate" name="merchantstate"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getState())) ? StringEscapeUtils.escapeHtml(identificationVO_company.getState()):""%>" /><%if(validationErrorList.getError("merchantstate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantcountry) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback ">
                                    <label for="merchantcountry" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                    <select  class="form-control" id="merchantcountry" name="merchantcountry" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  onchange="myjunk1('merchantcountry','Companyphonecc1');">
                                        <%=AppFunctionUtil.getCountryDetails((functions.isValueNull(identificationVO_company.getCountry())) ? identificationVO_company.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("merchantcountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                </div>
                                <%
                                    }
                                %>
                                <%--<%
                                    if(companyProfileInputList.contains(BankInputName.countryofregistration)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="countryofregistration" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country of Registration*</label>
                                    <select  class="form-control" id="countryofregistration" name="countryofregistration" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>>
                                        <%=AppFunctionUtil.getCountryDetails(functions.isValueNull(companyProfileVO.getCountryOfRegistration())?companyProfileVO.getCountryOfRegistration():"")%>
                                    </select><%if(validationErrorList.getError("countryofregistration")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>--%>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.merchantzipcode)  || view )
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="merchantzipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                    <input type="text" class="form-control"  id="merchantzipcode" name="merchantzipcode" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=( functions.isValueNull(identificationVO_company.getZipcode())) ? identificationVO_company.getZipcode():""%>" /><%if(validationErrorList.getError("merchantzipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_typeofbusiness)  || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label for="FederalTaxID" style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Type of Business :-</u></label>
                                </div>
                                <div class="col-md-12" style="padding: 0;">
                                    <div class="form-group col-md-4 col-lg-3 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio"   name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="Corporation" <%="Corporation".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                                            Corporation</label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio"  name="company_typeofbusiness" style="width:20%;" <%=globaldisabled%> value="LimitedLiabilityCompany" <%="LimitedLiabilityCompany".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                                            Limited Liability Company</label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display:inline;padding-left:0;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="SoleProprietor"<%="SoleProprietor".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%> />
                                            Sole&nbsp;Proprietor</label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="Partnership" <%="Partnership".equals(companyProfileVO.getCompanyTypeOfBusiness())?"checked":""%>/>
                                            Partnership</label>
                                    </div>
                                    <div class="form-group col-md-4 col-lg-3">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                            <input type="radio" name="company_typeofbusiness" style="width:30%;" <%=globaldisabled%> value="NotforProfit" <%="NotforProfit".equals(companyProfileVO.getCompanyTypeOfBusiness()) || !functions.isValueNull(companyProfileVO.getCompanyTypeOfBusiness()) ?"checked":""%>  />
                                            Nonprofit Organization</label>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <div class="form-group col-md-12"></div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.startup_business) || companyProfileInputList.contains(BankInputName.company_lengthoftime_business) || view)
                                    {
                                        String startupBusinessDisabled = "";
                                        if("Y".equals(companyProfileVO.getStartup_business()) || !functions.isValueNull(companyProfileVO.getStartup_business()))
                                        {
                                            startupBusinessDisabled = "disabled";
                                        }
                                        if("disabled".equals(globaldisabled)){
                                            startupBusinessDisabled = "disabled";
                                        }
                                %>
                                <div class="form-group col-md-12 has-feedback" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-3 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Is this a start-up business?</label>
                                    <div class="col-md-2">
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%>  id="startup_business" name="startup_business"  value="Y" <%="Y".equals(companyProfileVO.getStartup_business())  || !functions.isValueNull(companyProfileVO.getStartup_business()) ?"checked":""%>/><%--<%if(validationErrorList.getError("startup_business")!=null){%><span class="apperrormsg">Invalid Startup business</span><%}%>--%>
                                        Yes&nbsp;
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="startup_business" value="N"  <%="N".equals(companyProfileVO.getStartup_business())?"checked":""%> />
                                        No
                                    </div>
                                    <div class="form-group col-md-7" style="margin-bottom: 0;">
                                        <label class="col-md-6 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: initial;padding-right:0;">If no, how long have you been in business?</label>
                                        <div class="col-md-6" style="padding-left: 0;">
                                            <input type="text" class="form-control" placeholder="Example: 2 Years, 9 Months" id="company_lengthoftime_business" name="company_lengthoftime_business" style="border: 1px solid #b2b2b2;font-weight:bold"  <%=startupBusinessDisabled%> value="<%=functions.isValueNull(companyProfileVO.getCompanyLengthOfTimeInBusiness())==true?companyProfileVO.getCompanyLengthOfTimeInBusiness():""%>" name="company_lengthoftime_business" <%=!functions.isValueNull(companyProfileVO.getStartup_business()) && "Y".equals(companyProfileVO.getStartup_business())?"disabled":""%> /><%if(validationErrorList.getError("company_lengthoftime_business")!=null){%><%--<span class="apperrormsg">Invalid Company length of time Business</span>--%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="top:0!important;"></i><%}%>
                                        </div>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatename)    || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry)  || companyProfileInputList.contains(BankInputName.corporatezipcode) || companyProfileInputList.contains(BankInputName.corporatestreet) || view)
                                    {
                                %>
                                <%--<div class="col-md-12" style="padding: 0;">--%>
                                <div class="form-group col-md-12">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="checkbox" id="billingtoo" name="billingtoo" style="float: none;margin-left:0;" <%=globaldisabled%> <%--onclick="FillBilling(this.form)"--%>>
                                        If you want same data in <b>Business Information</b> Select <b>Checkbox</b></label>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_corporatename)  || companyProfileInputList.contains(BankInputName.registered_directors)  || companyProfileInputList.contains(BankInputName.EURegistrationNumber) || companyProfileInputList.contains(BankInputName.registered_directors_country)  || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  || companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode) || companyProfileInputList.contains(BankInputName.registered_directors_street) || view)
                                    {
                                %>
                                <div class="form-group col-md-6 col-lg-6">
                                    <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;padding-left:0;">
                                        <input type="checkbox" id="eucompany" name="eucompany" style="float: none;margin-left:0;" <%=globaldisabled%> <%--onclick="eucompanydetails(this.form)"--%>>
                                        If you want same data in <b>EU Company Details</b>, Select <b>Checkbox</b></label>
                                </div>
                                <%
                                        }
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatename)||companyProfileInputList.contains(BankInputName.corporate_addressproof) ||companyProfileInputList.contains(BankInputName.corporate_addressId) || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry)  || companyProfileInputList.contains(BankInputName.corporatezipcode) || companyProfileInputList.contains(BankInputName.corporatestreet) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">Business Information</h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatename)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Business Name/DBA*</label>
                                    <input type="text" class="form-control"   id="corporatename" name="corporatename" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_company.getCompany_name())) ? identificationVO_company.getCompany_name():""%>"/><%if(validationErrorList.getError("corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporate_addressproof) ||companyProfileInputList.contains(BankInputName.corporate_addressId) || companyProfileInputList.contains(BankInputName.corporateaddress)  || companyProfileInputList.contains(BankInputName.corporatecity)  || companyProfileInputList.contains(BankInputName.corporatestate)  || companyProfileInputList.contains(BankInputName.corporatecountry)  || companyProfileInputList.contains(BankInputName.corporatezipcode) || companyProfileInputList.contains(BankInputName.corporatestreet) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;">Address Details</h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporate_addressproof)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporate_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                    <select class="form-control" name="corporate_addressproof" id="corporate_addressproof" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getAddressProofTypes(( functions.isValueNull(identificationVO_business.getAddressProof())) ? identificationVO_business.getAddressProof() : ""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("corporate_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporate_addressId) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporate_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                    <input type="text" class="form-control"  id="corporate_addressId" name="corporate_addressId"style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%= ( functions.isValueNull(identificationVO_business.getAddressId())) ? identificationVO_business.getAddressId():""%>"/><%if(validationErrorList.getError("corporate_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporateaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporateaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Billing Address*</label>
                                    <input type="text" class="form-control"   id="corporateaddress" name="corporateaddress" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getAddress()))?StringEscapeUtils.escapeHtml(identificationVO_business.getAddress()):""%>" /><%if(validationErrorList.getError("corporateaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatestreet)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatestreet" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street*</label>
                                    <input type="text" class="form-control"   id="corporatestreet" <%=globaldisabled%> name="corporatestreet" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getStreet()))?identificationVO_business.getStreet():""%>" /><%if(validationErrorList.getError("corporatestreet")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatecity)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatecity" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City/Town</label>
                                    <input type="text" class="form-control" id="corporatecity" name="corporatecity" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getCity()))?StringEscapeUtils.escapeHtml(identificationVO_business.getCity()):""%>"/><%if(validationErrorList.getError("corporatecity")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatestate)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatestate" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County/Province</label>
                                    <input type="text" class="form-control"   id="corporatestate" name="corporatestate" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getState()))?StringEscapeUtils.escapeHtml(identificationVO_business.getState()):""%>" /><%if(validationErrorList.getError("corporatestate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatecountry)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country*</label>
                                    <select  class="form-control" id="corporatecountry" name="corporatecountry" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%>  >
                                        <%=AppFunctionUtil.getCountryDetails((functions.isValueNull(identificationVO_business.getCountry()))?identificationVO_business.getCountry():"")%>
                                    </select><%if(validationErrorList.getError("corporatecountry")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.corporatezipcode)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="corporatezipcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip Code</label>
                                    <input type="text" class="form-control"   id="corporatezipcode" <%=globaldisabled%> name="corporatezipcode" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_business.getZipcode())) ? identificationVO_business.getZipcode():""%>" /><%if(validationErrorList.getError("corporatezipcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_corporatename)  || companyProfileInputList.contains(BankInputName.registered_directors)  || companyProfileInputList.contains(BankInputName.EURegistrationNumber) || companyProfileInputList.contains(BankInputName.registered_directors_country)  || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  || companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode) || companyProfileInputList.contains(BankInputName.registered_directors_street) || view)
                                    {
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">EU company details</h2>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_corporatename)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_corporatename" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registered Corporate Name</label>
                                    <input type="text" class="form-control"  <%=globaldisabled%> id="registered_corporatename" name="registered_corporatename" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getCompany_name())) ? identificationVO_euCompany.getCompany_name():""%>" /><%if(validationErrorList.getError("registered_corporatename")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_addressproof)  || companyProfileInputList.contains(BankInputName.registered_directors_addressId)  || companyProfileInputList.contains(BankInputName.registered_directors_country)  || companyProfileInputList.contains(BankInputName.registered_directors_street) || companyProfileInputList.contains(BankInputName.registered_directors_address) || companyProfileInputList.contains(BankInputName.registered_directors_city)  || companyProfileInputList.contains(BankInputName.registered_directors_State )  || companyProfileInputList.contains(BankInputName.registered_directors_postalcode)  || view)
                                    {
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registered Directors</label>
                                    <input type="text" class="form-control"  <%=globaldisabled%> id="registered_directors" name="registered_directors" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getRegistred_directors())) ? identificationVO_euCompany.getRegistred_directors():""%>"/><%if(validationErrorList.getError("registered_directors")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.EURegistrationNumber) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="EURegistrationNumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registration Number</label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="EURegistrationNumber" name="EURegistrationNumber" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getRegistration_number())) ? identificationVO_euCompany.getRegistration_number():""%>" /><%if(validationErrorList.getError("EURegistrationNumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="font-family: 'Open Sans';font-size: 14px;font-weight: 400;background-color: #adadad !important;color: #ffffff;">Address Details</h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_addressproof)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_addressproof" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address Proof</label>
                                    <select class="form-control" name="registered_directors_addressproof" <%=disableEU%> id="registered_directors_addressproof" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getAddressProofTypes((functions.isValueNull(identificationVO_euCompany.getAddressProof())) ? identificationVO_euCompany.getAddressProof() : ""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("registered_directors_addressproof")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_addressId)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_addressId" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Address ID</label>
                                    <input type="text" class="form-control" <%=disableEU%> id="registered_directors_addressId" name="registered_directors_addressId" style="border: 1px solid #b2b2b2;font-weight:bold" <%=globaldisabled%> value="<%=(functions.isValueNull(identificationVO_euCompany.getAddressId())) ? identificationVO_euCompany.getAddressId():""%>"/><%if(validationErrorList.getError("registered_directors_addressId")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;background-color: #ebccd1;width: 40px;margin-right: 16px;height: 32px; margin-top: -1px;"></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_country)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Country</label>
                                    <select  name="registered_directors_country" id="registered_directors_country" class="form-control" <%=globaldisabled%> <%=bgcolor%> <%=disableEU%> <%=globaldisabled%>>
                                        <%=AppFunctionUtil.getCountryDetailsEU((functions.isValueNull(identificationVO_euCompany.getCountry())) ? identificationVO_euCompany.getCountry() : "")%>
                                    </select>  <%if(validationErrorList.getError("registered_directors_country")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>

                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_street) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_street" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Street</label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="registered_directors_street" name="registered_directors_street" <%=bgcolor%> <%=disableEU%> style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(identificationVO_euCompany.getStreet()))?identificationVO_euCompany.getStreet():""%>" /><%if(validationErrorList.getError("registered_directors_street")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_address) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_address" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Registered Office Address</label>
                                    <input type="text" class="form-control"   id="registered_directors_address" name="registered_directors_address" <%=globaldisabled%> style="border: 1px solid #b2b2b2;font-weight:bold"  <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getAddress()))?StringEscapeUtils.escapeHtml(identificationVO_euCompany.getAddress()):""%>" /><%if(validationErrorList.getError("registered_directors_address")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_city)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_city" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">City/Town</label>
                                    <input type="text" class="form-control"   id="registered_directors_city" <%=globaldisabled%> name="registered_directors_city" style="border: 1px solid #b2b2b2;font-weight:bold" <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getCity()))?StringEscapeUtils.escapeHtml(identificationVO_euCompany.getCity()):""%>"/><%if(validationErrorList.getError("registered_directors_city")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_State )  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_State" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">State/County/Province</label>
                                    <input type="text" class="form-control"   id="registered_directors_State" <%=globaldisabled%> name="registered_directors_State" style="border: 1px solid #b2b2b2;font-weight:bold" <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getState()))?StringEscapeUtils.escapeHtml(identificationVO_euCompany.getState()):""%>" /><%if(validationErrorList.getError("registered_directors_State")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.registered_directors_postalcode)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="registered_directors_postalcode" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Zip/Postal Code</label>
                                    <input type="text" class="form-control"   id="registered_directors_postalcode" <%=globaldisabled%> name="registered_directors_postalcode" style="border: 1px solid #b2b2b2;font-weight:bold" <%=disableEU%> value="<%=(functions.isValueNull(identificationVO_euCompany.getZipcode()))?identificationVO_euCompany.getZipcode():""%>"/><%if(validationErrorList.getError("registered_directors_postalcode")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                        }
                                    }
                                %>
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="background-color:#7eccad !important;font-family: 'Open Sans';font-size: 14px;font-weight: 400;color:white;">Other Information</h2>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_turnoverlastyear_unit)  ||companyProfileInputList.contains(BankInputName.company_currencylastyear)  || companyProfileInputList.contains(BankInputName.company_numberofemployees)   || companyProfileInputList.contains(BankInputName.contactname)   || companyProfileInputList.contains(BankInputName.contactemailaddress)   || companyProfileInputList.contains(BankInputName.contactname_telnocc1)  || companyProfileInputList.contains(BankInputName.contactname_telephonenumber)  ||  companyProfileInputList.contains(BankInputName.SkypeIMaddress)  || companyProfileInputList.contains(BankInputName.technicalcontactname)  || companyProfileInputList.contains(BankInputName.technicalemailaddress)  || companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||  companyProfileInputList.contains(BankInputName.Technical_telephonenumber)  || companyProfileInputList.contains(BankInputName.billingcontactname)   || companyProfileInputList.contains(BankInputName.billingemailaddress)  || companyProfileInputList.contains(BankInputName.financialphonecc1) || companyProfileInputList.contains(BankInputName.Financial_telephonenumber)|| companyProfileInputList.contains(BankInputName.contact_designation) ||
                                            companyProfileInputList.contains(BankInputName.technical_designation) || companyProfileInputList.contains(BankInputName.billing_designation) || companyProfileInputList.contains(BankInputName.cbk_designation) || companyProfileInputList.contains(BankInputName.pci_designation) || view)
                                    {
                                %>


                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_turnoverlastyear_unit)  || view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label for="company_turnoverlastyear_unit" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Turnover last year</label>
                                    <select name="company_turnoverlastyear_unit" id="company_turnoverlastyear_unit" class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getCurrencyUnit(functions.isValueNull(companyProfileVO.getCompany_turnoverlastyear_unit())?companyProfileVO.getCompany_turnoverlastyear_unit():""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("company_turnoverlastyear_unit")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="company_turnoverlastyear" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Amount</label>
                                    <input type="text" class="form-control" id="company_turnoverlastyear" name="company_turnoverlastyear" <%=globaldisabled%> value="<%=functions.isValueNull(companyProfileVO.getCompanyTurnoverLastYear())==true?companyProfileVO.getCompanyTurnoverLastYear():""%>"/><%if(validationErrorList.getError("company_turnoverlastyear")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_currencylastyear)  || view)
                                    {
                                %>
                                <div class="form-group col-md-3 has-feedback">
                                    <label for="company_turnoverlastyear_unit" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Capital Resources*</label>
                                    <select name="company_currencylastyear"  class="form-control" <%=globaldisabled%>>
                                        <%
                                            out.println(appFunctionUtil.getCurrencyUnit(functions.isValueNull(companyProfileVO.getCompany_currencylastyear())==true?companyProfileVO.getCompany_currencylastyear():""));
                                        %>
                                    </select>
                                    <%if(validationErrorList.getError("company_currencylastyear")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="company_capitalresources" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Amount*</label>
                                    <input type="text" class="form-control" id="company_capitalresources" <%=globaldisabled%> name="company_capitalresources"  value="<%=functions.isValueNull(companyProfileVO.getCompanyCapitalResources())==true?companyProfileVO.getCompanyCapitalResources():""%>"/><%if(validationErrorList.getError("company_capitalresources")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_numberofemployees)   || view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="company_numberofemployees" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">No. of Employees*</label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="company_numberofemployees" name="company_numberofemployees"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getCompanyNumberOfEmployees())==true?companyProfileVO.getCompanyNumberOfEmployees():""%>"/><%if(validationErrorList.getError("company_numberofemployees")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactname)   ||companyProfileInputList.contains(BankInputName.contactemailaddress)   ||companyProfileInputList.contains(BankInputName.contactname_telnocc1)   ||companyProfileInputList.contains(BankInputName.contactname_telephonenumber)   ||companyProfileInputList.contains(BankInputName.SkypeIMaddress) ||companyProfileInputList.contains(BankInputName.contact_designation) || view)
                                    {
                                %>

                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Contact Information : </u></label>
                                </div>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactname)   || view)
                                    {
                                %>

                                <div class="form-group col-md-4 has-feedback">
                                    <label for="contactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Name/Relationship</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contactname" name="contactname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_main.getName())) ? contactDetailsVO_main.getName():""%>"/><%if(validationErrorList.getError("contactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactemailaddress)   || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="contactemailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contactemailaddress" name="contactemailaddress" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_main.getEmailaddress())) ? contactDetailsVO_main.getEmailaddress():""%>" /><%if(validationErrorList.getError("contactemailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.contactname_telnocc1)  || companyProfileInputList.contains(BankInputName.contactname_telephonenumber)  ||  view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="contactname_telnocc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                    <input type="text" class="form-control" <%=globaldisabled%>  id="contactname_telnocc1" name="contactname_telnocc1"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_main.getPhonecc1())) ? contactDetailsVO_main.getPhonecc1():""%>"/> <%if(validationErrorList.getError("contactname_telnocc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="contactname_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contactname_telephonenumber" name="contactname_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_main.getTelephonenumber())) ? contactDetailsVO_main.getTelephonenumber():""%>" /><%if(validationErrorList.getError("contactname_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.contact_designation)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="contact_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="contact_designation" name="contact_designation" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_main.getDesignation())) ? contactDetailsVO_main.getDesignation():""%>" /><%if(validationErrorList.getError("contact_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.SkypeIMaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="SkypeIMaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Skype/IM Address</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="SkypeIMaddress" name="SkypeIMaddress" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_main.getSkypeIMaddress())) ? contactDetailsVO_main.getSkypeIMaddress():""%>" /><%if(validationErrorList.getError("SkypeIMaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalcontactname)  ||companyProfileInputList.contains(BankInputName.technicalemailaddress)  ||companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||companyProfileInputList.contains(BankInputName.Technical_telephonenumber) || companyProfileInputList.contains(BankInputName.technical_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Technical Contact : </u></label>
                                </div>
                                <%-- <%
                                     }
                                 %>--%>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalcontactname)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="technicalcontactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Name/Relationship</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technicalcontactname" name="technicalcontactname" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getName())) ? contactDetailsVO_technical.getName():""%>" /><%if(validationErrorList.getError("technicalcontactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalemailaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="technicalemailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technicalemailaddress" name="technicalemailaddress" style="border: 1px solid #b2b2b2;font-weight:bold"   value="<%=(functions.isValueNull(contactDetailsVO_technical.getEmailaddress())) ? contactDetailsVO_technical.getEmailaddress():""%>" /><%if(validationErrorList.getError("technicalemailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.technicalphonecc1)  ||  companyProfileInputList.contains(BankInputName.Technical_telephonenumber)  || view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="technicalphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technicalphonecc1" name="technicalphonecc1"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getPhonecc1())) ? contactDetailsVO_technical.getPhonecc1():""%>"/> <%if(validationErrorList.getError("technicalphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="Technical_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="Technical_telephonenumber" name="Technical_telephonenumber" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getTelephonenumber())) ? contactDetailsVO_technical.getTelephonenumber():""%>" /><%if(validationErrorList.getError("Technical_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.technical_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="technical_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="technical_designation" name="technical_designation"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_technical.getDesignation())) ? contactDetailsVO_technical.getDesignation():""%>"/> <%if(validationErrorList.getError("technical_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.billingcontactname)  ||companyProfileInputList.contains(BankInputName.billingemailaddress)  ||companyProfileInputList.contains(BankInputName.financialphonecc1)  ||companyProfileInputList.contains(BankInputName.Financial_telephonenumber) || companyProfileInputList.contains(BankInputName.billing_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Financial/billing Contact : </u></label>
                                </div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.billingcontactname) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="billingcontactname" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Name/Relationship</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="billingcontactname" name="billingcontactname"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_billing.getName())) ? contactDetailsVO_billing.getName():""%>" /><%if(validationErrorList.getError("billingcontactname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.billingemailaddress)  || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="billingemailaddress" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="billingemailaddress" name="billingemailaddress"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_billing.getEmailaddress())) ? contactDetailsVO_billing.getEmailaddress():""%>" /><%if(validationErrorList.getError("billingemailaddress")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.financialphonecc1) || companyProfileInputList.contains(BankInputName.Financial_telephonenumber)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="financialphonecc1" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="financialphonecc1" name="financialphonecc1"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_billing.getPhonecc1())) ? contactDetailsVO_billing.getPhonecc1():""%>"/> <%if(validationErrorList.getError("financialphonecc1")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="Financial_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="Financial_telephonenumber" name="Financial_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_billing.getTelephonenumber())) ? contactDetailsVO_billing.getTelephonenumber():""%>" /><%if(validationErrorList.getError("Financial_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.billing_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="billing_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="billing_designation" name="billing_designation"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_billing.getDesignation())) ? contactDetailsVO_billing.getDesignation():""%>"/> <%if(validationErrorList.getError("billing_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.cbk_contactperson) || companyProfileInputList.contains(BankInputName.cbk_email) ||companyProfileInputList.contains(BankInputName.cbk_telephonenumber) ||companyProfileInputList.contains(BankInputName.cbk_phonecc) ||companyProfileInputList.contains(BankInputName.cbk_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>Chargeback/Refund contacts : </u></label>
                                </div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.cbk_contactperson) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="cbk_contactperson" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Name/Relationship</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_contactperson" name="cbk_contactperson"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getName())) ? contactDetailsVO_cbk.getName():""%>" /><%if(validationErrorList.getError("cbk_contactperson")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.cbk_email) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="cbk_email" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_email" name="cbk_email"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getEmailaddress())) ? contactDetailsVO_cbk.getEmailaddress():""%>" /><%if(validationErrorList.getError("cbk_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.cbk_telephonenumber) ||companyProfileInputList.contains(BankInputName.cbk_phonecc)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="cbk_phonecc" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_phonecc" name="cbk_phonecc"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getPhonecc1())) ? contactDetailsVO_cbk.getPhonecc1():""%>"/> <%if(validationErrorList.getError("cbk_phonecc")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="cbk_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_telephonenumber" name="cbk_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_cbk.getTelephonenumber())) ? contactDetailsVO_cbk.getTelephonenumber():""%>" /><%if(validationErrorList.getError("cbk_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.cbk_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="cbk_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="cbk_designation" name="cbk_designation"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_cbk.getDesignation())) ? contactDetailsVO_cbk.getDesignation():""%>"/> <%if(validationErrorList.getError("cbk_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                        }
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.pci_contactperson) || companyProfileInputList.contains(BankInputName.pci_email) ||companyProfileInputList.contains(BankInputName.pci_telephonenumber) ||companyProfileInputList.contains(BankInputName.pci_phonecc) ||companyProfileInputList.contains(BankInputName.pci_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 15px;font-weight: 600;"><u>PCI Contacts : </u></label>
                                </div>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.pci_contactperson) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="pci_contactperson" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Name/Relationship</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_contactperson" name="pci_contactperson"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getName())) ? contactDetailsVO_pci.getName():""%>" /><%if(validationErrorList.getError("pci_contactperson")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.pci_email) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="pci_email" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Email Address</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_email" name="pci_email"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getEmailaddress())) ? contactDetailsVO_pci.getEmailaddress():""%>" /><%if(validationErrorList.getError("pci_email")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.pci_telephonenumber) ||companyProfileInputList.contains(BankInputName.pci_phonecc)|| view)
                                    {
                                %>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="pci_phonecc" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">STD code/Country Code</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_phonecc" name="pci_phonecc"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getPhonecc1())) ? contactDetailsVO_pci.getPhonecc1():""%>"/> <%if(validationErrorList.getError("pci_phonecc")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <div class="form-group col-md-2 has-feedback">
                                    <label for="pci_telephonenumber" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Phone No</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_telephonenumber" name="pci_telephonenumber"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=(functions.isValueNull(contactDetailsVO_pci.getTelephonenumber())) ? contactDetailsVO_pci.getTelephonenumber():""%>" /><%if(validationErrorList.getError("pci_telephonenumber")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                    if(companyProfileInputList.contains(BankInputName.pci_designation) || view)
                                    {
                                %>
                                <div class="form-group col-md-4 has-feedback">
                                    <label for="pci_designation" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Designation</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="pci_designation" name="pci_designation"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=(functions.isValueNull(contactDetailsVO_pci.getDesignation())) ? contactDetailsVO_pci.getDesignation():""%>"/> <%if(validationErrorList.getError("pci_designation")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                            }
                                        }
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) ||companyProfileInputList.contains(BankInputName.legal_proceeding)|| companyProfileInputList.contains(BankInputName.iscompany_insured)|| companyProfileInputList.contains(BankInputName.insured_companyname)|| companyProfileInputList.contains(BankInputName.main_business_partner)||
                                            companyProfileInputList.contains(BankInputName.loans)|| companyProfileInputList.contains(BankInputName.income_economic_activity)|| companyProfileInputList.contains(BankInputName.interest_income)|| companyProfileInputList.contains(BankInputName.investments)|| companyProfileInputList.contains(BankInputName.income_sources_other)|| companyProfileInputList.contains(BankInputName.income_sources_other_yes) || view)
                                    {
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.company_bankruptcy) || companyProfileInputList.contains(BankInputName.company_bankruptcydate) ||   view )
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Have you ever filed for bankruptcy ?*</label>
                                    <div class="col-md-2">
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="company_bankruptcy" value="Y" <%="Y".equals(companyProfileVO.getCompanyBankruptcy())?"checked":""%>/>
                                        Yes&nbsp;
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="company_bankruptcy" value="N"  <%=!functions.isValueNull(companyProfileVO.getCompanyBankruptcy())||"N".equals(companyProfileVO.getCompanyBankruptcy())?"checked":""%>/>
                                        No
                                    </div>
                                    <div class="form-group col-md-4 has-feedback" style="margin-bottom: 0;">
                                        <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: initial;">If yes when</label>
                                        <div class="col-md-8" style="padding-left:0;">
                                            <input style="width: 100%;" type="text" <%=globaldisabled%> name="company_bankruptcydate" style="display:initial;width:100%;" class="form-control datepicker" value="<%=functions.isValueNull(companyProfileVO.getCompanyBankruptcydate())==true?appFunctionUtil.convertTimestampToDatepicker(companyProfileVO.getCompanyBankruptcydate()):""%>"<%=!functions.isValueNull(companyProfileVO.getCompanyBankruptcy())||"N".equals(companyProfileVO.getCompanyBankruptcy())?"disabled":""%>>
                                            <%if(validationErrorList.getError("company_bankruptcydate")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="margin-top: inherit!important;height: inherit!important;top:0!important;"></i><%}%>
                                        </div>
                                    </div>
                                </div>

                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.License_required) || companyProfileInputList.contains(BankInputName.License_Permission) || view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">

                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Does your business require a license?*</label>

                                    <div class="col-md-4">
                                        <input type="radio" <%=globaldisabled%> name="License_required" value="Y" <%="Y".equals(companyProfileVO.getLicense_required())?"checked":""%> />
                                        Yes&nbsp;
                                        <input type="radio" <%=globaldisabled%> name="License_required"  value="N" <%="N".equals(companyProfileVO.getLicense_required()) || !functions.isValueNull(companyProfileVO.getLicense_required()) ?"checked":""%> />
                                        No</div>
                                </div>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Does the Company have a license/ permission?</label>

                                    <div class="col-md-4">
                                        <input type="radio" <%=globaldisabled%>  id="License_Permission" name="License_Permission"  value="Y" <%="Y".equals(companyProfileVO.getLicense_Permission())?"checked":""%> />
                                        Yes&nbsp;
                                        <input type="radio" <%=globaldisabled%>  id="License_Permission" name="License_Permission" value="N" <%="N".equals(companyProfileVO.getLicense_Permission()) || !functions.isValueNull(companyProfileVO.getLicense_Permission()) ?"checked":""%> />
                                        No</div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.legal_proceeding)|| view)
                                    {
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Was the Company involved in any legal proceeding?*</label>

                                    <div class="col-md-4">
                                        <input type="radio" name="legal_proceeding" <%=globaldisabled%> value="Y" <%="Y".equals(companyProfileVO.getLegalProceeding())?"checked":""%> />
                                        Yes&nbsp;
                                        <input type="radio"  name="legal_proceeding" <%=globaldisabled%> value="N" <%="N".equals(companyProfileVO.getLegalProceeding()) || !functions.isValueNull(companyProfileVO.getLegalProceeding()) ?"checked":""%> />
                                        No</div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.iscompany_insured) || companyProfileInputList.contains(BankInputName.insured_companyname) || companyProfileInputList.contains(BankInputName.insured_currency) ||companyProfileInputList.contains(BankInputName.insured_amount) || view)
                                    {
                                        String insuredDisable = "";
                                        if(functions.isValueNull(companyProfileVO.getIscompany_insured()) && "N".equals(companyProfileVO.getIscompany_insured()))
                                            insuredDisable="disabled";
                                        if("disabled".equals(globaldisabled))
                                            insuredDisable="disabled";
                                %>
                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <label class="col-md-4 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;"> Is the Company insured?</label>
                                    <div class="col-md-2">
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="iscompany_insured"  value="Y" <%="Y".equals(companyProfileVO.getIscompany_insured())?"checked":""%> />
                                        Yes&nbsp;
                                        <input style="font-family:Open Sans;font-size: 13px;font-weight: 600;" type="radio" <%=globaldisabled%> name="iscompany_insured"  value="N" <%="N".equals(companyProfileVO.getIscompany_insured()) || !functions.isValueNull(companyProfileVO.getIscompany_insured()) ?"checked":""%> />
                                        No
                                    </div>
                                    <div class="form-group col-md-6 has-feedback" style="margin-bottom: 0;">
                                        <label class="col-md-12 control-label" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left: initial;">If yes please provide the details below</label>
                                    </div>
                                </div>

                                <div class="form-group col-md-12" style="font-family:Open Sans;font-size: 13px;font-weight: 600;padding-left:0;">
                                    <div class="form-group col-md-6 has-feedback" style="margin-bottom: 0;">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Insured company name</label>
                                        <input type="text" class="form-control" <%=insuredDisable%> id="insured_companyname" name="insured_companyname"style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=functions.isValueNull(companyProfileVO.getInsured_companyname())==true?companyProfileVO.getInsured_companyname():""%>" /><%if(validationErrorList.getError("insured_companyname")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442;"></i><%}%>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Insured Currency</label>
                                        <select name="insured_currency"  class="form-control" <%=insuredDisable%>>
                                            <%
                                                out.println(appFunctionUtil.getCurrencyUnit(functions.isValueNull(companyProfileVO.getInsured_currency())? companyProfileVO.getInsured_currency():""));
                                            %>
                                        </select>
                                        <%if(validationErrorList.getError("insured_currency")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                    <div class="form-group col-md-3 has-feedback">
                                        <label for="insured_amount" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Insured Amount</label>
                                        <input type="text" class="form-control" id="insured_amount" <%=insuredDisable%> name="insured_amount"  value="<%=functions.isValueNull(companyProfileVO.getInsured_amount())?companyProfileVO.getInsured_amount():""%>"/><%if(validationErrorList.getError("insured_amount")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon "></i><%}%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                    if(companyProfileInputList.contains(BankInputName.main_business_partner) || view)
                                    {
                                %>
                                <div class="form-group col-md-6 has-feedback">
                                    <label for="main_business_partner" style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Main business partners of the Company?</label>
                                    <input type="text" class="form-control" <%=globaldisabled%> id="main_business_partner" name="main_business_partner"style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=functions.isValueNull(companyProfileVO.getMain_business_partner())==true?companyProfileVO.getMain_business_partner():""%>" /><%if(validationErrorList.getError("main_business_partner")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="display: block;color:#a94442"></i><%}%>
                                </div>
                                <%
                                    }
                                %>

                                <%
                                    if(companyProfileInputList.contains(BankInputName.loans)|| companyProfileInputList.contains(BankInputName.income_economic_activity) || companyProfileInputList.contains(BankInputName.interest_income)|| companyProfileInputList.contains(BankInputName.investments) || companyProfileInputList.contains(BankInputName.income_sources_other)||companyProfileInputList.contains(BankInputName.income_sources_other_yes) ||view)
                                    {
                                %>
                                <div class="form-group col-md-12">
                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Income sources of the Company:</label>
                                </div>

                                <div class="row" id="income_row">
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="checkbox"   name="loans" style="width:30%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(companyProfileVO.getLoans())?"checked":""%> />
                                            Loans</label>
                                    </div>
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;">
                                            <input type="checkbox" name="income_economic_activity" style="width:30%;display: table;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(companyProfileVO.getIncome_economic_activity())?"checked":""%> />
                                            Income from the economic activity</label>
                                    </div>

                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" id="align_check" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: flex;">
                                            <input type="checkbox"   name="interest_income" style="width:30%;"  <%=globaldisabled%>  value="Y"  <%="Y".equals(companyProfileVO.getInterest_income())?"checked":""%> />
                                            Dividends / interest income</label>
                                    </div>

                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;">
                                            <input type="checkbox"   name="investments" style="width:30%;"  <%=globaldisabled%>
                                                   value="Y"  <%="Y".equals(companyProfileVO.getInvestments())?"checked":""%> />
                                            Investments</label>
                                    </div>
                                    <div class="form-group col-lg-4 col-md-4 has-feedback">
                                        <label class="col-md-6 control-label checkbox-inline" style="font-family:Open Sans;font-size: 13px;font-weight: 600;display: inline;"><input type="checkbox"   name="income_sources_other" style="width:70%;"  <%=globaldisabled%> value="Y" <%="Y".equals(companyProfileVO.getIncome_sources_other())?"checked":""%> onchange="return myIncomeCompanyCheckBox('income_sources_other','income_sources_other')"/>
                                            Other&nbsp;&nbsp;</label>
                                        <div class="col-md-6" id="top10">
                                            <input type="text" <%=globaldisabled%> name="income_sources_other_yes" style="display:initial;width:100%;" class="form-control" value="<%=functions.isValueNull(companyProfileVO.getIncome_sources_other_yes())==true?companyProfileVO.getIncome_sources_other_yes():""%>"<%=!functions.isValueNull(companyProfileVO.getIncome_sources_other())||"N".equals(companyProfileVO.getIncome_sources_other())?"disabled":""%>>
                                            <%if(validationErrorList.getError("income_sources_other_yes")!=null){%><i class="form-control-feedback glyphicon glyphicon-remove erroricon " style="top: 0!important;margin-top: inherit!important;height: 28px!important;line-height: inherit;"></i><%}%>
                                        </div>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                                <%
                                        }
                                    }
                                    addressIdentificationVOMap.put("COMPANY",identificationVO_company);
                                    addressIdentificationVOMap.put("BUSINESS",identificationVO_business);
                                    addressIdentificationVOMap.put("EU_COMPANY",identificationVO_euCompany);
                                    companyProfileVO.setCompanyProfile_addressVOMap(addressIdentificationVOMap);

                                    contactDetailsVOMap.put("MAIN", contactDetailsVO_main);
                                    contactDetailsVOMap.put("BILLING", contactDetailsVO_billing);
                                    contactDetailsVOMap.put("TECHNICAL", contactDetailsVO_technical);
                                    contactDetailsVOMap.put("CBK", contactDetailsVO_cbk);
                                    contactDetailsVOMap.put("PCI", contactDetailsVO_pci);
                                    companyProfileVO.setCompanyProfile_contactInfoVOMap(contactDetailsVOMap);
                                    applicationManagerVO.setCompanyProfileVO(companyProfileVO);
                                %>

                                <%
                                    if(companyProfileInputList.size()==0 && !view)
                                    {
                                        out.println("<div class=\"content-page\">");
                                        out.println("<div class=\"content\">");
                                        out.println("<div class=\"page-heading\">");
                                        out.println("<div class=\"row\">");
                                        out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
                                        out.println("<div class=\"widget\">");
                                        out.println("<div class=\"widget-header transparent\">\n" +
                                                "                                <h2 class=\"col-md-12 background panelheading_color headpanelfont_color\" style=\"background-color: #9fabb7;text-align: center\"> Cardholder Profile</h2>\n" +
                                                "                                <div class=\"additional-btn\">\n" +
                                                "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                                                "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                                                "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                                                "                                </div>\n" +
                                                "                            </div>");
                                        out.println("<div class=\"widget-content padding\">");
          /*out.println("<div class=\"table-responsive\">");*/
                                        out.println(Functions.NewShowConfirmation1("Profile","There is no details that has to be provided for this profile"));
         /* out.println("</div>");*/
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");
                                        out.println("</div>");


                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--     </div> -->

