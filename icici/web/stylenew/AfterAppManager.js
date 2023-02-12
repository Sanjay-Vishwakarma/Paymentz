/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/9/15
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */

function myBusiness(selected,other1,other2)
{
    if(Number(this.document.myformname[selected].value)+Number(this.document.myformname[other1].value)+Number(this.document.myformname[other2].value)>=100)
    {
        if(this.document.myformname[selected].value=="")
            this.document.myformname[selected].disabled = true;
        if(this.document.myformname[other1].value=="")
            this.document.myformname[other1].disabled = true;
        if(this.document.myformname[other2].value=="")
            this.document.myformname[other2].disabled= true ;
    }

    if(this.document.myformname[selected].value>=100)
    {

        if(this.document.myformname[other1].value=="")
            this.document.myformname[other1].disabled = true;
        if(this.document.myformname[other2].value=="")
            this.document.myformname[other2].disabled= true ;
    }
    else
    {
        this.document.myformname[selected].disabled = false;
        this.document.myformname[other1].disabled = false;
        this.document.myformname[other2].disabled = false;
    }

}

function myBusinessCheckBox(selected)
{
    if(selected=="cardtypesaccepted_other")
    {
        this.document.myformname['cardtypesaccepted_other_yes'].disabled=false;
    }
}

$(document).ready(function(){

    $( "input[name='startup_business']").on( "change", function() {
        if($('input:radio[name=startup_business]:checked').val() == "Y")
        {
            document.myformname.company_lengthoftime_business.disabled = true;
            document.myformname.company_lengthoftime_business.value = "";
        }
        else
        {
            document.myformname.company_lengthoftime_business.disabled = false;
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

    /*$( "input[name='terminal_type']").on( "click", function() {
     if($('input:radio[name=terminal_type]:checked').val()=="other_terminal")
     {
     document.myformname.terminal_type_otheryes.disabled = false;
     }
     else
     {
     document.myformname.terminal_type_otheryes.disabled = true;
     document.myformname.terminal_type_otheryes.value = "";
     }
     });*/

    $( "input[name='iscompany_insured']").on( "change", function() {
        if($('input:radio[name=iscompany_insured]:checked').val() == "N")
        {
            document.myformname.insured_companyname.disabled = true;
            document.myformname.insured_companyname.value = "";
            document.myformname.insured_currency.disabled = true;
            document.myformname.insured_currency.value = "";
            document.myformname.insured_amount.disabled = true;
            document.myformname.insured_amount.value = "";
        }
        else
        {
            document.myformname.insured_companyname.disabled = false;
            document.myformname.insured_currency.disabled = false;
            document.myformname.insured_amount.disabled = false;
        }
    });

    $( "input[name='recurringservices']").on( "click", function() {
        if($('input:radio[name=recurringservices]:checked').val()=="N")
        {
            document.myformname.recurringservicesyes.disabled = true;
            document.myformname.recurringservicesyes.value = "";

        }
        else
        {
            document.myformname.recurringservicesyes.disabled = false;
        }
    });

    $( "input[name='isacallcenterused']").on( "click", function() {
        if($('input:radio[name=isacallcenterused]:checked').val()=="N")
        {
            document.myformname.isacallcenterusedyes.disabled = true;
            document.myformname.isacallcenterusedyes.value = "";
        }
        else
        {
            document.myformname.isacallcenterusedyes.disabled = false;
        }
    });

    $( "input[name='isafulfillmenthouseused']").on( "click", function() {
        if($('input:radio[name=isafulfillmenthouseused]:checked').val()=="N")
        {
            document.myformname.isafulfillmenthouseused_yes.disabled = true;
            document.myformname.isafulfillmenthouseused_yes.value = "";
        }
        else
        {
            document.myformname.isafulfillmenthouseused_yes.disabled = false;
        }
    });

    $( "input[name='cardtypesaccepted_other']").on( "click", function() {
        if($('input:checkbox[name=cardtypesaccepted_other]:checked').val()=="Y")
        {
            document.myformname.cardtypesaccepted_other_yes.disabled = false;
        }
        else
        {
            document.myformname.cardtypesaccepted_other_yes.disabled = true;
            document.myformname.cardtypesaccepted_other_yes.value = "";
        }
    });

    $( "input[name='income_sources_other']").on( "change", function() {
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

    $( "input[name='orderconfirmation_other']").on( "change", function() {
        if($('input:checkbox[name=orderconfirmation_other]:checked').val()=="Y")
        {
            document.myformname.orderconfirmation_other_yes.disabled = false;
        }
        else
        {
            document.myformname.orderconfirmation_other_yes.disabled = true;
            document.myformname.orderconfirmation_other_yes.value = "";
        }
    });

    $( "input[name='siteinspection_merchant']").on( "click", function() {
        if($('input:radio[name=siteinspection_merchant]:checked').val()=="N")
        {
            document.myformname.siteinspection_landlord.disabled = true;
            document.myformname.siteinspection_landlord.value = "";
        }
        else
        {
            document.myformname.siteinspection_landlord.disabled = false;
        }
    });

    $( "input[name='compliance_companiesorgateways']").on( "click", function() {
        if($('input:radio[name=compliance_companiesorgateways]:checked').val()=="N")
        {
            document.myformname.compliance_companiesorgateways_yes.disabled = true;
            document.myformname.compliance_companiesorgateways_yes.value = "";
        }
        else
        {
            document.myformname.compliance_companiesorgateways_yes.disabled = false;
        }
    });

    $( "input[name='compliance_electronically']").on( "click", function() {
        if($('input:radio[name=compliance_electronically]:checked').val()=="N")
        {
            document.getElementById('cd1').disabled = true;
            document.getElementById('cd2').disabled = true;
            document.getElementById('cd3').disabled = true;
        }
        else
        {
            document.getElementById('cd1').disabled = false;
            document.getElementById('cd2').disabled = false;
            document.getElementById('cd3').disabled = false;
        }
    });

    $( "input[name='License_required']").on( "click", function() {
        if($('input:radio[name=License_required]:checked').val()=="N")
        {
            document.getElementById('cd11').disabled = true;
            document.getElementById('cd12').disabled = true;
        }
        else
        {
            document.getElementById('cd11').disabled = false;
            document.getElementById('cd12').disabled = false;
        }
    });

    $( "input[name='compliance_datacompromise']").on( "click", function() {
        if($('input:radio[name=compliance_datacompromise]:checked').val()=="N")
        {
            document.myformname.compliance_datacompromise_yes.disabled = true;
            document.myformname.compliance_datacompromise_yes.value = "";
        }
        else
        {
            document.myformname.compliance_datacompromise_yes.disabled = false;
        }
    });

    $( "input[name='compliance_swapp']").on( "click", function() {
        if($('input:radio[name=compliance_swapp]:checked').val()=="N")
        {
            document.myformname.compliance_thirdpartyappform.disabled = true;
            document.myformname.compliance_thirdpartysoft.disabled= true;
            document.myformname.compliance_version.disabled= true;
            document.getElementById('compliance_companiesorgatewaysY').disabled = true;
            document.getElementById('compliance_companiesorgatewaysN').disabled = true;
            document.myformname.compliance_companiesorgateways_yes.disabled= true;
        }
        else
        {
            document.myformname.compliance_thirdpartyappform.disabled = false;
            document.myformname.compliance_thirdpartysoft.disabled= false;
            document.myformname.compliance_version.disabled= false;
            document.getElementById('compliance_companiesorgatewaysY').disabled = false;
            document.getElementById('compliance_companiesorgatewaysN').disabled = false;
            if($('input:radio[name=compliance_companiesorgateways]:checked').val()=="Y")
            {
                document.myformname.compliance_companiesorgateways_yes.disabled= false;
            }
        }
    });

//business profile

    //Channel Type and Accepted Volume

    if($('input:checkbox[name=terminal_type]:checked').val()=="on")
    {
        document.myformname.terminal_type_otheryes.disabled = false;
    }
    else
    {
        document.myformname.terminal_type_otheryes.disabled = true;
        document.myformname.terminal_type_otheryes.value = "";
    }
    $( "input[name='terminal_type']").on( "click", function() {
        if($('input:checkbox[name=terminal_type]:checked').val()=="on")
        {
            document.myformname.terminal_type_otheryes.disabled = false;
        }
        else
        {
            document.myformname.terminal_type_otheryes.disabled = true;
            document.myformname.terminal_type_otheryes.value = "";
        }
    });


    if($('input:checkbox[name=terminal_type]:checked').val()=="on")
    {
        document.myformname.terminal_type_other.disabled = false;
    }
    else
    {
        document.myformname.terminal_type_other.disabled = true;
        document.myformname.terminal_type_other.value = "";
    }
    $( "input[name='terminal_type']").on( "click", function() {
        if($('input:checkbox[name=terminal_type]:checked').val()=="on")
        {
            document.myformname.terminal_type_other.disabled = false;
        }
        else
        {
            document.myformname.terminal_type_other.disabled = true;
            document.myformname.terminal_type_other.value = "";
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

    if($('input:checkbox[name=internet]:checked').val()=="internet_percentage")
    {
        document.myformname.internet_percentage.disabled = false;
    }
    else
    {
        document.myformname.internet_percentage.disabled = true;
        document.myformname.internet_percentage.value = "";
    }
    $( "input[name='internet']").on( "click", function() {
        if($('input:checkbox[name=internet]:checked').val()=="internet_percentage")
        {
            document.myformname.internet_percentage.disabled = false;
        }
        else
        {
            document.myformname.internet_percentage.disabled = true;
            document.myformname.internet_percentage.value = "";
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

    //Percentage of foreign transactions

    if($('input:checkbox[name=us]:checked').val()=="foreigntransactions_us")
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
        alert("inside");
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


    if($('input:checkbox[name=debit]:checked').val()=="paymenttype_debit")
    {
        document.myformname.paymenttype_debit.disabled = false;
    }
    else
    {
        document.myformname.paymenttype_debit.disabled = true;
        document.myformname.paymenttype_debit.value = "";
    }
    $( "input[name='debit']").on( "click", function() {
        if($('input:checkbox[name=debit]:checked').val()=="paymenttype_debit")
        {
            document.myformname.paymenttype_debit.disabled = false;
        }
        else
        {
            document.myformname.paymenttype_debit.disabled = true;
            document.myformname.paymenttype_debit.value = "";
        }
    });


    if($('input:checkbox[name=netbanking]:checked').val()=="paymenttype_netbanking")
    {
        document.myformname.paymenttype_netbanking.disabled = false;
    }
    else
    {
        document.myformname.paymenttype_netbanking.disabled = true;
        document.myformname.paymenttype_netbanking.value = "";
    }
    $( "input[name='netbanking']").on( "click", function() {
        if($('input:checkbox[name=netbanking]:checked').val()=="paymenttype_netbanking")
        {
            document.myformname.paymenttype_netbanking.disabled = false;
        }
        else
        {
            document.myformname.paymenttype_netbanking.disabled = true;
            document.myformname.paymenttype_netbanking.value = "";
        }
    });

    if($('input:checkbox[name=wallet]:checked').val()=="paymenttype_wallet")
    {
        document.myformname.paymenttype_wallet.disabled = false;
    }
    else
    {
        document.myformname.paymenttype_wallet.disabled = true;
        document.myformname.paymenttype_wallet.value = "";
    }

    $( "input[name='wallet']").on( "click", function() {
        if($('input:checkbox[name=wallet]:checked').val()=="paymenttype_wallet")
        {
            document.myformname.paymenttype_wallet.disabled = false;
        }
        else
        {
            document.myformname.paymenttype_wallet.disabled = true;
            document.myformname.paymenttype_wallet.value = "";
        }
    });


    if($('input:checkbox[name=alternate_payment]:checked').val()=="paymenttype_alternate")
    {
        document.myformname.paymenttype_alternate.disabled = false;
    }
    else
    {
        document.myformname.paymenttype_alternate.disabled = true;
        document.myformname.paymenttype_alternate.value = "";
    }
    $( "input[name='alternate_payment']").on( "click", function() {
        if($('input:checkbox[name=alternate_payment]:checked').val()=="paymenttype_alternate")
        {
            document.myformname.paymenttype_alternate.disabled = false;
        }
        else
        {
            document.myformname.paymenttype_alternate.disabled = true;
            document.myformname.paymenttype_alternate.value = "";
        }
    });

    $( "input[name='shopping_cart']").on( "click", function() {
        if($('input:radio[name=shopping_cart]:checked').val()=="N")
        {
            document.myformname.shopping_cart_details.disabled = true;
            document.myformname.shopping_cart_details.value = "";
        }
        else
        {
            document.myformname.shopping_cart_details.disabled = false;
        }
    });

    $( "input[name='pricing_policies_website']").on( "click", function() {
        if($('input:radio[name=pricing_policies_website]:checked').val()=="N")
        {
            document.myformname.pricing_policies_website_yes.disabled = true;
            document.myformname.pricing_policies_website_yes.value = "";
        }
        else
        {
            document.myformname.pricing_policies_website_yes.disabled = false;
        }
    });

    $( "input[name='countries_blocked']").on( "click", function() {
        if($('input:radio[name=countries_blocked]:checked').val()=="N")
        {
            document.myformname.countries_blocked_details.disabled = true;
            document.myformname.countries_blocked_details.value = "";
        }
        else
        {
            document.myformname.countries_blocked_details.disabled = false;
        }
    });

    $( "input[name='domainsowned']").on( "click", function() {
        if($('input:radio[name=domainsowned]:checked').val()=="N")
        {
            document.myformname.domainsowned_no.disabled = true;
            document.myformname.domainsowned_no.value = "";
        }
        else
        {
            document.myformname.domainsowned_no.disabled = false;
        }
    });
    /*$( "input[name='customer_support']").on( "click", function() {
     if($('input:radio[name=customer_support]:checked').val()=="N")
     {
     document.myformname.customer_support_details.disabled = true;

     }
     else
     {
     document.myformname.customer_support_details.disabled = false;
     }
     });*/
    $( "input[name='affiliate_programs']").on( "click", function() {
        if($('input:radio[name=affiliate_programs]:checked').val()=="N")
        {
            document.myformname.affiliate_programs_details.disabled = true;
            document.myformname.affiliate_programs_details.value = "";
        }
        else
        {
            document.myformname.affiliate_programs_details.disabled = false;
        }
    });

    $( "input[name='billing_model']").on( "click", function() {
        if($('input:radio[name=billing_model]:checked').val()=="one_time" || $('input:radio[name=billing_model]:checked').val()=="pay_per_minute" || $('input:radio[name=billing_model]:checked').val()=="pay_per_download" || $('input:radio[name=billing_model]:checked').val()=="payment_other")
        {
            document.myformname.recurring_amount.disabled = true;
            $('input:radio[name=billing_timeframe]').each(function(){
                $(this).attr('disabled','disabled');
            });
        }
        else
        {
            document.myformname.recurring_amount.disabled = false;
            $('input:radio[name=billing_timeframe]').each(function(){
                $(this).removeAttr('disabled');
            });
        }
        if($('input:radio[name=billing_model]:checked').val()=="payment_other")
        {
            document.myformname.payment_type_yes.disabled = false;
        }
        else
        {
            document.myformname.payment_type_yes.disabled = true;
            document.myformname.payment_type_yes.value = "";
        }
    });


    $( "input[name='payment_delivery']").on( "click", function() {
        if($('input:radio[name=payment_delivery]:checked').val()=="payment_delivery_other")
        {
            document.myformname.payment_delivery_otheryes.disabled = false;
        }
        else
        {
            document.myformname.payment_delivery_otheryes.disabled = true;
            document.myformname.payment_delivery_otheryes.value = "";
        }
    });

    $( "input[name='MCC_Ctegory']").on( "click", function() {
        if($('input:radio[name=MCC_Ctegory]:checked').val()=="N")
        {
            document.myformname.merchantcode.disabled = true;
            document.myformname.merchantcode.value = "";
            $(document.getElementsByName("merchantcode")[0]).css('background-color', '#EBEBE4');
        }
        else
        {
            document.myformname.merchantcode.disabled = false;
            $(document.getElementsByName("merchantcode")[0]).css('background-color', '#FFFFFF');
        }
    });
    $( "input[name='customers_identification']").on( "click", function() {
        if($('input:radio[name=customers_identification]:checked').val()=="N")
        {
            document.myformname.customers_identification_yes.disabled = true;
            document.myformname.customers_identification_yes.value = "";
        }
        else
        {
            document.myformname.customers_identification_yes.disabled = false;
        }
    });
    $( "input[name='customer_support']").on( "click", function() {
        if($('input:radio[name=customer_support]:checked').val()=="N")
        {
            document.myformname.customersupport_email.disabled = true;
            document.myformname.customersupport_email.value = "";
            document.myformname.custsupportwork_hours.disabled= true;
            document.myformname.custsupportwork_hours.value= "";
            document.myformname.timeframe.disabled= true;
            document.myformname.timeframe.value= "";
            document.myformname.technical_contact.disabled=true;
            document.myformname.technical_contact.value="";
        }
        else
        {
            document.myformname.customersupport_email.disabled = false;
            document.myformname.custsupportwork_hours.disabled= false;
            document.myformname.timeframe.disabled= false;
            document.myformname.technical_contact.disabled=false;

        }
    });

//Extra details profile

    $( "input[name='company_financialreport']").on( "click", function() {
        if($('input:radio[name=company_financialreport]:checked').val()=="N")
        {
            document.myformname.company_financialreportyes.disabled = true;
            document.myformname.company_financialreportyes.value = "";
        }
        else
        {
            document.myformname.company_financialreportyes.disabled = false;
        }
    });

    $( "input[name='financialreport_available']").on( "click", function() {
        if($('input:radio[name=financialreport_available]:checked').val()=="N")
        {
            document.myformname.financialreport_availableyes.disabled = true;
            document.myformname.financialreport_availableyes.value = "";
        }
        else
        {
            document.myformname.financialreport_availableyes.disabled = false;
        }
    });

    $( "input[name='compliance_punitivesanction']").on( "click", function() {
        if($('input:radio[name=compliance_punitivesanction]:checked').val()=="N")
        {
            document.myformname.compliance_punitivesanctionyes.disabled = true;
            document.myformname.compliance_punitivesanctionyes.value = "";
        }
        else
        {
            document.myformname.compliance_punitivesanctionyes.disabled = false;
        }
    });

    $( "input[name='deedofagreement']").on( "click", function() {
        if($('input:radio[name=deedofagreement]:checked').val()=="N")
        {
            document.myformname.deedofagreementyes.disabled = true;
            document.myformname.deedofagreementyes.value = "";
        }
        else
        {
            document.myformname.deedofagreementyes.disabled = false;
        }
    });

    $( "input[name='fulfillment_productemail']").on( "click", function() {
        if($('input:radio[name=fulfillment_productemail]:checked').val()=="N")
        {
            document.myformname.fulfillment_productemailyes.disabled = true;
            document.myformname.fulfillment_productemailyes.value = "";
        }
        else
        {
            document.myformname.fulfillment_productemailyes.disabled = false;
        }
    });

    $( "input[name='blacklistedaccountclosed']").on( "click", function() {
        if($('input:radio[name=blacklistedaccountclosed]:checked').val()=="N")
        {
            document.myformname.blacklistedaccountclosedyes.disabled = true;
            document.myformname.blacklistedaccountclosedyes.value = "";
        }
        else
        {
            document.myformname.blacklistedaccountclosedyes.disabled = false;
        }
    });

    $( "input[name='compliance_cispcompliant']").on( "click", function() {
        if($ ('input:radio[name=compliance_cispcompliant]:checked').val()=="N")
        {
            document.myformname.compliance_cispcompliant_yes.disabled = true;
            document.myformname.compliance_cispcompliant_yes.value = "";
        }
        else
        {
            document.myformname.compliance_cispcompliant_yes.disabled = false;
        }
    })

    $( "input[name='compliance_pcidsscompliant']").on( "click", function() {
        if($ ('input:radio[name=compliance_pcidsscompliant]:checked').val()=="N")
        {
            document.myformname.compliance_pcidsscompliant_yes.disabled = true;
            document.myformname.compliance_pcidsscompliant_yes.value = "";
        }
        else
        {
            document.myformname.compliance_pcidsscompliant_yes.disabled = false;
        }
    });

    $( "input[name='seasonal_fluctuating']").on( "click", function() {
        if($ ('input:radio[name=seasonal_fluctuating]:checked').val()=="N")
        {
            document.myformname.seasonal_fluctuating_yes.disabled = true;
            document.myformname.seasonal_fluctuating_yes.value = "";
        }
        else
        {
            document.myformname.seasonal_fluctuating_yes.disabled = false;
        }
    });

    $( "input[name='is_website_live']").on( "click", function() {
        if($ ('input:radio[name=is_website_live]:checked').val()=="N")
        {
            document.myformname.test_link.disabled = true;
            document.myformname.test_link.value = "";
        }
        else
        {
            document.myformname.test_link.disabled = false;
        }
    });
    $( "input[name='listfraudtools']").on( "click", function() {
        if($ ('input:radio[name=listfraudtools]:checked').val()=="N")
        {
            document.myformname.listfraudtools_yes.disabled = true;
            document.myformname.listfraudtools_yes.value = "";
        }
        else
        {
            document.myformname.listfraudtools_yes.disabled = false;
        }
    });

    $( "input[name='agency_employed']").on( "click", function()
    {
        if ($('input:radio[name=agency_employed]:checked').val() == "N")
        {
            document.myformname.agency_employed_yes.disabled = true;
            document.myformname.agency_employed_yes.value = "";
        }
        else
        {
            document.myformname.agency_employed_yes.disabled = false;
        }
    })
});


function isNumberKey(evt)
{
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

    return true;
}

function myOwnerShip(val,secondname,thirdname,option)
{
    var value2 = this.document.myformname[secondname].value ;
    var value3 = this.document.myformname[thirdname].value ;
    var sum1= Number(val) + Number(value2);
    var sum2= Number(val) + Number(value2)+Number(value3);
    if((val >=100 || val <=0) && option==1)
    {
        this.document.myformname.nameprincipal2_owned.value = "";
        this.document.myformname.nameprincipal2_owned.disabled = true;
        $(document.getElementsByName("nameprincipal2_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.nameprincipal2.disabled = true;
        this.document.myformname.nameprincipal2_lastname.disabled = true;
        this.document.myformname.nameprincipal2_title.disabled = true;
        this.document.myformname.nameprincipal2_identificationtypeselect.disabled = true;
        $(document.getElementsByName("nameprincipal2_identificationtypeselect")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.nameprincipal2_identificationtype.disabled = true;
        this.document.myformname.nameprincipal2_dateofbirth.disabled = true;
        this.document.myformname.nameprincipal2_address.disabled = true;
        this.document.myformname.nameprincipal2_city.disabled = true;
        this.document.myformname.nameprincipal2_State.disabled = true;
        this.document.myformname.nameprincipal2_zip.disabled = true;
        this.document.myformname.nameprincipal2_country.disabled = true;
        this.document.myformname.nameprincipal2_street.disabled = true;
        $(document.getElementsByName("nameprincipal2_country")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.nameprincipal2_telnocc2.disabled = true;
        this.document.myformname.nameprincipal2_telephonenumber.disabled = true;
        this.document.myformname.nameprincipal2_emailaddress.disabled = true;
        this.document.myformname.nameprincipal2_nationality.disabled = true;
        this.document.myformname.nameprincipal2_Passportexpirydate.disabled = true;
        this.document.myformname.nameprincipal2_passportissuedate.disabled = true;

        //Nameprinciple 3

        this.document.myformname.nameprincipal3_owned.value="";
        this.document.myformname.nameprincipal3_owned.disabled = true;
        $(document.getElementsByName("nameprincipal3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.nameprincipal3.disabled = true;
        this.document.myformname.nameprincipal3_lastname.disabled = true;
        this.document.myformname.nameprincipal3_title.disabled = true;
        this.document.myformname.nameprincipal3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("nameprincipal3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.nameprincipal3_identificationtype.disabled = true;
        this.document.myformname.nameprincipal3_dateofbirth.disabled = true;
        this.document.myformname.nameprincipal3_address.disabled = true;
        this.document.myformname.nameprincipal3_city.disabled = true;
        this.document.myformname.nameprincipal3_State.disabled = true;
        this.document.myformname.nameprincipal3_zip.disabled = true;
        this.document.myformname.nameprincipal3_country.disabled = true;
        this.document.myformname.nameprincipal3_street.disabled = true;
        $(document.getElementsByName("nameprincipal3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.nameprincipal3_telnocc1.disabled = true;
        this.document.myformname.nameprincipal3_telephonenumber.disabled = true;
        this.document.myformname.nameprincipal3_emailaddress.disabled = true;
        this.document.myformname.nameprincipal3_nationality.disabled = true;
        this.document.myformname.nameprincipal3_Passportexpirydate.disabled = true;
        this.document.myformname.nameprincipal3_passportissuedate.disabled = true;

        if(val>100)
        {
            this.document.myformname['nameprincipal1_owned'].focus();
            document.getElementById('nameprincipal1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('nameprincipal1_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('nameprincipal1_owned').innerHTML="";
            $( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
        }

        //document.getElementById('nameprincipal1_owned').innerHTML="";
        document.getElementById('nameprincipal2_owned').innerHTML="";
        document.getElementById('nameprincipal3_owned').innerHTML="";
        //$( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
        //$( document.getElementById('nameprincipal2_owned')).css('background-color','#ffffff');
        //$( document.getElementById('nameprincipal3_owned')).css('background-color','#ffffff');
    }
    else if((val <100 || val <=0) && option==1)
    {
        if(this.document.myformname.nameprincipal2_owned.value=="")
        {
            this.document.myformname.nameprincipal2_owned.value = "";
            this.document.myformname.nameprincipal2_owned.disabled = false;
            $(document.getElementsByName("nameprincipal2_owned")[0]).css('background-color', '#FFFFFF')
            this.document.myformname.nameprincipal2.disabled = false;
            this.document.myformname.nameprincipal2_lastname.disabled = false;
            this.document.myformname.nameprincipal2_title.disabled = false;
            this.document.myformname.nameprincipal2_identificationtypeselect.disabled = false;
            $(document.getElementsByName("nameprincipal2_identificationtypeselect")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.nameprincipal2_identificationtype.disabled = false;
            this.document.myformname.nameprincipal2_dateofbirth.disabled = false;
            this.document.myformname.nameprincipal2_address.disabled = false;
            this.document.myformname.nameprincipal2_city.disabled = false;
            this.document.myformname.nameprincipal2_State.disabled = false;
            this.document.myformname.nameprincipal2_zip.disabled = false;
            this.document.myformname.nameprincipal2_country.disabled = false;
            this.document.myformname.nameprincipal2_street.disabled = false;
            $(document.getElementsByName("nameprincipal2_country")[0]).css('background-color', '#FFFFFF');
            this.document.myformname.nameprincipal2_telnocc2.disabled = false;
            this.document.myformname.nameprincipal2_telephonenumber.disabled = false;
            this.document.myformname.nameprincipal2_emailaddress.disabled = false;
            this.document.myformname.nameprincipal2_nationality.disabled = false;
            this.document.myformname.nameprincipal2_Passportexpirydate.disabled = false;
            this.document.myformname.nameprincipal2_passportissuedate.disabled = false;
        }
        document.getElementById('nameprincipal1_owned').innerHTML="";
        $( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
    }
    else if( sum1>=100 && (option==2 || option==1))
    {

        //Nameprinciple 3

        this.document.myformname.nameprincipal3_owned.disabled = true;
        $(document.getElementsByName("nameprincipal3_owned")[0]).css('background-color', '#EBEBE4');
        this.document.myformname.nameprincipal3.disabled = true;
        this.document.myformname.nameprincipal3_lastname.disabled = true;
        this.document.myformname.nameprincipal3_title.disabled = true;
        this.document.myformname.nameprincipal3_identificationtypeselect.disabled=true;
        $(document.getElementsByName("nameprincipal3_identificationtypeselect")[0]).css('background-color','#EBEBE4');
        this.document.myformname.nameprincipal3_identificationtype.disabled = true;
        this.document.myformname.nameprincipal3_dateofbirth.disabled = true;
        this.document.myformname.nameprincipal3_address.disabled = true;
        this.document.myformname.nameprincipal3_city.disabled = true;
        this.document.myformname.nameprincipal3_State.disabled = true;
        this.document.myformname.nameprincipal3_zip.disabled = true;
        this.document.myformname.nameprincipal3_country.disabled = true;
        this.document.myformname.nameprincipal3_street.disabled = true;
        $(document.getElementsByName("nameprincipal3_country")[0]).css('background-color','#EBEBE4');
        this.document.myformname.nameprincipal3_telnocc1.disabled = true;
        this.document.myformname.nameprincipal3_telephonenumber.disabled = true;
        this.document.myformname.nameprincipal3_emailaddress.disabled = true;
        this.document.myformname.nameprincipal3_nationality.disabled = true;
        this.document.myformname.nameprincipal3_Passportexpirydate.disabled = true;
        this.document.myformname.nameprincipal3_passportissuedate.disabled = true;

        if(sum1>100)
        {
            this.document.myformname['nameprincipal1_owned'].focus();
            document.getElementById('nameprincipal1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('nameprincipal1_owned')).css('background-color','#f2dede');
            document.getElementById('nameprincipal2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('nameprincipal2_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('nameprincipal1_owned').innerHTML="";
            document.getElementById('nameprincipal2_owned').innerHTML="";
            $( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
            $( document.getElementById('nameprincipal2_owned')).css('background-color','#ffffff');
        }

    }
    else if( sum1<100 && (option==2 || option==1))
    {
        //Nameprinciple 3
        this.document.myformname.nameprincipal3_owned.disabled = false;
        $(document.getElementsByName("nameprincipal3_owned")[0]).css('background-color', '#FFFFFF');
        this.document.myformname.nameprincipal3.disabled = false;
        this.document.myformname.nameprincipal3_lastname.disabled = false;
        this.document.myformname.nameprincipal3_title.disabled = false;
        this.document.myformname.nameprincipal3_identificationtypeselect.disabled=false;
        $(document.getElementsByName("nameprincipal3_identificationtypeselect")[0]).css('background-color','#FFFFFF');
        this.document.myformname.nameprincipal3_identificationtype.disabled = false;
        this.document.myformname.nameprincipal3_dateofbirth.disabled = false;
        this.document.myformname.nameprincipal3_address.disabled = false;
        this.document.myformname.nameprincipal3_city.disabled = false;
        this.document.myformname.nameprincipal3_State.disabled = false;
        this.document.myformname.nameprincipal3_zip.disabled = false;
        this.document.myformname.nameprincipal3_country.disabled = false;
        this.document.myformname.nameprincipal3_street.disabled = false;
        $(document.getElementsByName("nameprincipal3_country")[0]).css('background-color','#FFFFFF');
        this.document.myformname.nameprincipal3_telnocc1.disabled = false;
        this.document.myformname.nameprincipal3_telephonenumber.disabled = false;
        this.document.myformname.nameprincipal3_emailaddress.disabled = false;
        this.document.myformname.nameprincipal3_nationality.disabled = false;
        this.document.myformname.nameprincipal3_Passportexpirydate.disabled = false;
        this.document.myformname.nameprincipal3_passportissuedate.disabled = false;

        if(sum1>100)
        {
            this.document.myformname['nameprincipal1_owned'].focus();
            document.getElementById('nameprincipal1_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('nameprincipal1_owned')).css('background-color','#f2dede');
            document.getElementById('nameprincipal2_owned').innerHTML="total ownership < 100%";
            $( document.getElementById('nameprincipal2_owned')).css('background-color','#f2dede');
        }
        else
        {
            document.getElementById('nameprincipal1_owned').innerHTML="";
            document.getElementById('nameprincipal2_owned').innerHTML="";
            $( document.getElementById('nameprincipal1_owned')).css('background-color','#ffffff');
            $( document.getElementById('nameprincipal2_owned')).css('background-color','#ffffff');
        }


    }
    else if(sum2>100 && (option==2 || option==1 || option==3))
    {
        document.getElementById('nameprincipal1_owned').innerHTML = "";
        $(document.getElementById('nameprincipal1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('nameprincipal2_owned').innerHTML = "";
        $(document.getElementById('nameprincipal2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('nameprincipal3_owned').innerHTML = "";
        $(document.getElementById('nameprincipal3_owned')).css('background-color', '#FFFFFF');
    }


    if(sum2<50)
    {
        if(this.document.myformname.nameprincipal1_owned.value !="")
        {
            this.document.myformname['nameprincipal1_owned'].focus();
            document.getElementById('nameprincipal1_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('nameprincipal1_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.nameprincipal2_owned.value !="")
        {
            document.getElementById('nameprincipal2_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('nameprincipal2_owned')).css('background-color', '#f2dede');
        }
        if(this.document.myformname.nameprincipal3_owned.value !="")
        {
            document.getElementById('nameprincipal3_owned').innerHTML = "total ownership > 50%";
            $(document.getElementById('nameprincipal3_owned')).css('background-color', '#f2dede');
        }
    }
    else if(sum2<100)
    {
        document.getElementById('nameprincipal1_owned').innerHTML = "";
        $(document.getElementById('nameprincipal1_owned')).css('background-color', '#FFFFFF');

        document.getElementById('nameprincipal2_owned').innerHTML = "";
        $(document.getElementById('nameprincipal2_owned')).css('background-color', '#FFFFFF');

        document.getElementById('nameprincipal3_owned').innerHTML = "";
        $(document.getElementById('nameprincipal3_owned')).css('background-color', '#FFFFFF');
    }


}

/*Shareholder Owned Condition*/
function myShareholderold(val,secondname,thirdname,option)
{
    var value2 = this.document.myformname[secondname].value ;
    var value3 = this.document.myformname[thirdname].value ;
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
        this.document.myformname.shareholderprofile2_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile2_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile2_addressproof.disabled = true;
        this.document.myformname.shareholderprofile2_addressId.disabled = true;

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
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile3_addressproof.disabled = true;
        this.document.myformname.shareholderprofile3_addressId.disabled = true;

        if(val>100)
        {
            this.document.myformname['shareholderprofile1_owned'].focus();
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
        //$( document.getElementById('shareholderprofile2_owned')).css('background-color','#ffffff');
        //$( document.getElementById('shareholderprofile3_owned')).css('background-color','#ffffff');
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
            this.document.myformname.shareholderprofile2_Passportexpirydate.disabled = false;
            this.document.myformname.shareholderprofile2_passportissuedate.disabled = false;
            this.document.myformname.shareholderprofile2_addressproof.disabled = false;
            this.document.myformname.shareholderprofile2_addressId.disabled = false;
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
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = true;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = true;
        this.document.myformname.shareholderprofile3_addressproof.disabled = true;
        this.document.myformname.shareholderprofile3_addressId.disabled = true;

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
        this.document.myformname.shareholderprofile3_Passportexpirydate.disabled = false;
        this.document.myformname.shareholderprofile3_passportissuedate.disabled = false;
        this.document.myformname.shareholderprofile3_addressproof.disabled = false;
        this.document.myformname.shareholderprofile3_addressId.disabled = false;

        if(sum1>100)
        {
            this.document.myformname['shareholderprofile1_owned'].focus();
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
            this.document.myformname['shareholderprofile1_owned'].focus();
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

/* Shareholder Owned New Condition */
function myShareholder(firstname,secondname,thirdname,fourthname,option)
{

    var value1 = document.getElementById([firstname]).value;
    var value2 = document.getElementById([secondname]).value;
    var value3 = document.getElementById([thirdname]).value;
    var value4 = document.getElementById([fourthname]).value;

    var sum1= Number(value1) + Number(value2);
    var sum2= Number(value1) + Number(value2)+Number(value3);
    var sum3= Number(value1) + Number(value2)+Number(value3)+Number(value4);
    //alert("This Value= " + [value1]);
    //alert("VALUE= " + value2 +' '+ value3 +' '+ value4 );
    //alert("SUM= " + sum1 +' '+ sum2 +' '+ sum3 );
    /*var value2 = this.document.forms["myformname"][secondname].value ;
     var value3 = this.document.forms["myformname"][thirdname].value ;
     var value4 = this.document.forms["myformname"][fourthname].value ;
     var sum1= Number(value1) + Number(value2);
     var sum2= Number(value1) + Number(value2)+Number(value3);
     var sum3= Number(value1) + Number(value2)+Number(value3)+Number(value4);*/

    //alert("Value= "+ [value1]);

    if((value1>=100 || value1<=0) && option==1)
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

        if(value1>100)
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
    else if((value1<100 || value1<=0) && option==1)
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
        /*this.document.myformname.shareholderprofile4_owned.disabled = true;
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
         $('input[id*=shareholderprofile4_criminalrecord][type=radio][value="N"]').prop('checked', true);*/
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
        /*this.document.myformname.shareholderprofile4_owned.disabled = false;
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
         */
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
    else if(sum2>=100 && (option==2 || option==1 || option==3))
    {
        //shareholderprofile4
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
    else if(sum2<100 && (option==2 || option==1 || option==3))
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
    var  salesVolume=this.document.myformname['salesvolume_lastmonth'].value;
    var  refundVolume=this.document.myformname['refundsvolume_lastmonth'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_lastmonth'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_lastmonth'].value = "";
            document.getElementById('refundratio_lastmonth').innerHTML = "Refund volume1 should be less than Sales volume1";
            $(document.getElementById('refundratio_lastmonth')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_lastmonth').innerHTML = "";
            $(document.getElementById('refundratio_lastmonth')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_lastmonth'].value = "0.00";
        }
        else
        {
            var refundRatio1 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio1 = refundRatio1.toFixed(2);
            this.document.myformname['refundratio_lastmonth'].value = "";
            document.getElementById('refundratio_lastmonth').innerHTML = "";
            $(document.getElementById('refundratio_lastmonth')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_lastmonth'].value = refundRatio1;
        }
    }
    else
    {
        this.document.myformname['refundratio_lastmonth'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_lastmonth'].value = "";
            document.getElementById('chargebackratio_lastmonth').innerHTML = "Chargeback volume1 should be less than Sales volume1";
            $(document.getElementById('chargebackratio_lastmonth')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_lastmonth').innerHTML = "";
            $(document.getElementById('chargebackratio_lastmonth')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_lastmonth'].value = "0.00";
        }
        else
        {
            var chargeBackRatio1 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio1 = chargeBackRatio1.toFixed(2);
            this.document.myformname['chargebackratio_lastmonth'].value = "";
            document.getElementById('chargebackratio_lastmonth').innerHTML = "";
            $(document.getElementById('chargebackratio_lastmonth')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_lastmonth'].value = chargeBackRatio1;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_lastmonth'].value="";
    }
}
function calculate2monthsago() {
    var  salesVolume=this.document.myformname['salesvolume_2monthsago'].value;
    var  refundVolume=this.document.myformname['refundsvolume_2monthsago'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_2monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_2monthsago'].value = "";
            document.getElementById('refundratio_2monthsago').innerHTML = "Refund volume2 should be less than Sales volume2";
            $(document.getElementById('refundratio_2monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_2monthsago').innerHTML = "";
            $(document.getElementById('refundratio_2monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_2monthsago'].value = "0.00";
        }
        else
        {
            var refundRatio2 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio2 = refundRatio2.toFixed(2);
            this.document.myformname['refundratio_2monthsago'].value = "";
            document.getElementById('refundratio_2monthsago').innerHTML = "";
            $(document.getElementById('refundratio_2monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_2monthsago'].value = refundRatio2;
        }
    }
    else
    {
        this.document.myformname['refundratio_2monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_2monthsago'].value = "";
            document.getElementById('chargebackratio_2monthsago').innerHTML = "Chargeback volume2 should be less than Sales volume2";
            $(document.getElementById('chargebackratio_2monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_2monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_2monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_2monthsago'].value = "0.00";
        }
        else
        {
            var chargeBackRatio2 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio2 = chargeBackRatio2.toFixed(2);
            this.document.myformname['chargebackratio_2monthsago'].value = "";
            document.getElementById('chargebackratio_2monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_2monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_2monthsago'].value = chargeBackRatio2;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_2monthsago'].value="";
    }
}

//Add new
//function for Calculating refund and chargeBack ratios 3 month
function calculate3monthsago() {
    var  salesVolume=this.document.myformname['salesvolume_3monthsago'].value;
    var  refundVolume=this.document.myformname['refundsvolume_3monthsago'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_3monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_3monthsago'].value = "";
            document.getElementById('refundratio_3monthsago').innerHTML = "Refund volume3 should be less than Sales volume3";
            $(document.getElementById('refundratio_3monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_3monthsago').innerHTML = "";
            $(document.getElementById('refundratio_3monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_3monthsago'].value = "0.00";
        }
        else
        {
            var refundRatio3 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio3 = refundRatio3.toFixed(2);
            this.document.myformname['refundratio_3monthsago'].value = "";
            document.getElementById('refundratio_3monthsago').innerHTML = "";
            $(document.getElementById('refundratio_3monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_3monthsago'].value = refundRatio3;
        }
    }
    else
    {
        this.document.myformname['refundratio_3monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_3monthsago'].value = "";
            document.getElementById('chargebackratio_3monthsago').innerHTML = "Chargeback volume3 should be less than Sales volume3";
            $(document.getElementById('chargebackratio_3monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_3monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_3monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_3monthsago'].value = "0.00";
        }
        else
        {
            var chargeBackRatio3 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio3 = chargeBackRatio3.toFixed(2);
            this.document.myformname['chargebackratio_3monthsago'].value = "";
            document.getElementById('chargebackratio_3monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_3monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_3monthsago'].value = chargeBackRatio3;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_3monthsago'].value="";
    }
}
//function for Calculating refund and chargeBack ratios 4 month
function calculate4monthsago() {
    var  salesVolume=this.document.myformname['salesvolume_4monthsago'].value;
    var  refundVolume=this.document.myformname['refundsvolume_4monthsago'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_4monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_4monthsago'].value = "";
            document.getElementById('refundratio_4monthsago').innerHTML = "Refund volume4 should be less than Sales volume4";
            $(document.getElementById('refundratio_4monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_4monthsago').innerHTML = "";
            $(document.getElementById('refundratio_4monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_4monthsago'].value = "0.00";
        }
        else
        {
            var refundRatio4 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio4 = refundRatio4.toFixed(2);
            this.document.myformname['refundratio_4monthsago'].value = "";
            document.getElementById('refundratio_4monthsago').innerHTML = "";
            $(document.getElementById('refundratio_4monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_4monthsago'].value = refundRatio4;
        }
    }
    else
    {
        this.document.myformname['refundratio_4monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_4monthsago'].value = "";
            document.getElementById('chargebackratio_4monthsago').innerHTML = "Chargeback volume4 should be less than Sales volume4";
            $(document.getElementById('chargebackratio_4monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_4monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_4monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_4monthsago'].value = "0.00";
        }
        else
        {
            var chargeBackRatio4 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio4 = chargeBackRatio4.toFixed(2);
            this.document.myformname['chargebackratio_4monthsago'].value = "";
            document.getElementById('chargebackratio_4monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_4monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_4monthsago'].value = chargeBackRatio4;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_4monthsago'].value="";
    }
}
//function for Calculating refund and chargeBack ratios 5 month
function calculate5monthsago() {
    var  salesVolume=this.document.myformname['salesvolume_5monthsago'].value;
    var  refundVolume=this.document.myformname['refundsvolume_5monthsago'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_5monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_5monthsago'].value = "";
            document.getElementById('refundratio_5monthsago').innerHTML = "Refund volume5 should be less than Sales volume5";
            $(document.getElementById('refundratio_5monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_5monthsago').innerHTML = "";
            $(document.getElementById('refundratio_5monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_5monthsago'].value = "0.00";
        }
        else
        {
            var refundRatio5 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio5 = refundRatio5.toFixed(2);
            this.document.myformname['refundratio_5monthsago'].value = "";
            document.getElementById('refundratio_5monthsago').innerHTML = "";
            $(document.getElementById('refundratio_5monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_5monthsago'].value = refundRatio5;
        }
    }
    else
    {
        this.document.myformname['refundratio_5monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_5monthsago'].value = "";
            document.getElementById('chargebackratio_5monthsago').innerHTML = "Chargeback volume5 should be less than Sales volume5";
            $(document.getElementById('chargebackratio_5monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_5monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_5monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_5monthsago'].value = "0.00";
        }
        else
        {
            var chargeBackRatio5 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio5 = chargeBackRatio5.toFixed(2);
            this.document.myformname['chargebackratio_5monthsago'].value = "";
            document.getElementById('chargebackratio_5monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_5monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_5monthsago'].value = chargeBackRatio5;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_5monthsago'].value="";
    }
}
//function for Calculating refund and chargeBack ratios 6 month
function calculate6monthsago() {
    var  salesVolume=this.document.myformname['salesvolume_6monthsago'].value;
    var  refundVolume=this.document.myformname['refundsvolume_6monthsago'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_6monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_6monthsago'].value = "";
            document.getElementById('refundratio_6monthsago').innerHTML = "Refund volume6 should be less than Sales volume6";
            $(document.getElementById('refundratio_6monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_6monthsago').innerHTML = "";
            $(document.getElementById('refundratio_6monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_6monthsago'].value = "0.00";
        }
        else
        {
            var refundRatio6 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio6 = refundRatio6.toFixed(2);
            this.document.myformname['refundratio_6monthsago'].value = "";
            document.getElementById('refundratio_6monthsago').innerHTML = "";
            $(document.getElementById('refundratio_6monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_6monthsago'].value = refundRatio6;
        }
    }
    else
    {
        this.document.myformname['refundratio_6monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_6monthsago'].value = "";
            document.getElementById('chargebackratio_6monthsago').innerHTML = "Chargeback volume6 should be less than Sales volume6";
            $(document.getElementById('chargebackratio_6monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_6monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_6monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_6monthsago'].value = "0.00";
        }
        else
        {
            var chargeBackRatio6 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio6 = chargeBackRatio6.toFixed(2);
            this.document.myformname['chargebackratio_6monthsago'].value = "";
            document.getElementById('chargebackratio_6monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_6monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_6monthsago'].value = chargeBackRatio6;
        }

    }
    else
    {
        this.document.myformname['chargebackratio_6monthsago'].value="";
    }
}

//function for Calculating refund and chargeBack ratios 12 month
function calculate12monthsago() {
    var  salesVolume=this.document.myformname['salesvolume_12monthsago'].value;
    var  refundVolume=this.document.myformname['refundsvolume_12monthsago'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_12monthsago'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_12monthsago'].value = "";
            document.getElementById('refundratio_12monthsago').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_12monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_12monthsago').innerHTML = "";
            $(document.getElementById('refundratio_12monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_12monthsago'].value = "0.00";
        }
        else
        {
            var refundRatio12 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatio12 = refundRatio12.toFixed(2);
            this.document.myformname['refundratio_12monthsago'].value = "";
            document.getElementById('refundratio_12monthsago').innerHTML = "";
            $(document.getElementById('refundratio_12monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_12monthsago'].value = refundRatio12;
        }
    }
    else
    {
        this.document.myformname['refundratio_12monthsago'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_12monthsago'].value = "";
            document.getElementById('chargebackratio_12monthsago').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_12monthsago')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_12monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_12monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_12monthsago'].value = "0.00";
        }
        else
        {
            var chargeBackRatio12 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatio12 = chargeBackRatio12.toFixed(2);
            this.document.myformname['chargebackratio_12monthsago'].value = "";
            document.getElementById('chargebackratio_12monthsago').innerHTML = "";
            $(document.getElementById('chargebackratio_12monthsago')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_12monthsago'].value = chargeBackRatio12;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_12monthsago'].value="";
    }
}

//function for Calculating refund and chargeBack ratios 2 years
function calculateyear2() {

    var  salesVolume=this.document.myformname['salesvolume_year2'].value;
    var  refundVolume=this.document.myformname['refundsvolume_year2'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_year2'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_year2'].value = "";
            document.getElementById('refundratio_year2').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_year2')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_year2').innerHTML = "";
            $(document.getElementById('refundratio_year2')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_year2'].value = "0.00";
        }
        else
        {
            var refundRatioyear2 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatioyear2 = refundRatioyear2.toFixed(2);
            this.document.myformname['refundratio_year2'].value = "";
            document.getElementById('refundratio_year2').innerHTML = "";
            $(document.getElementById('refundratio_year2')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_year2'].value = refundRatioyear2;
        }
    }
    else
    {
        this.document.myformname['refundratio_year2'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_year2'].value = "";
            document.getElementById('chargebackratio_year2').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_year2')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_year2').innerHTML = "";
            $(document.getElementById('chargebackratio_year2')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_year2'].value = "0.00";
        }
        else
        {
            var chargeBackRatioyear2 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatioyear2 = chargeBackRatioyear2.toFixed(2);
            this.document.myformname['chargebackratio_year2'].value = "";
            document.getElementById('chargebackratio_year2').innerHTML = "";
            $(document.getElementById('chargebackratio_year2')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_year2'].value = chargeBackRatioyear2;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_year2'].value="";
    }
}

//function for Calculating refund and chargeBack ratios 3 years
function calculateyear3() {
    var  salesVolume=this.document.myformname['salesvolume_year3'].value;
    var  refundVolume=this.document.myformname['refundsvolume_year3'].value;
    var  chargeBackVolume=this.document.myformname['chargebackvolume_year3'].value;

    if(salesVolume!="" && refundVolume!="")
    {
        if (Number(refundVolume) > Number(salesVolume))
        {
            this.document.myformname['refundratio_year3'].value = "";
            document.getElementById('refundratio_year3').innerHTML = "Refund volume should be less than Sales volume";
            $(document.getElementById('refundratio_year3')).css('background-color', '#f2dede');
        }
        else if(Number(refundVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('refundratio_year3').innerHTML = "";
            $(document.getElementById('refundratio_year3')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_year3'].value = "0.00";
        }
        else
        {
            var refundRatioyear3 = (Number(refundVolume) / Number(salesVolume)) * 100;
            refundRatioyear3 = refundRatioyear3.toFixed(2);
            this.document.myformname['refundratio_year3'].value = "";
            document.getElementById('refundratio_year3').innerHTML = "";
            $(document.getElementById('refundratio_year3')).css('background-color', '#FFFFFF');
            this.document.myformname['refundratio_year3'].value = refundRatioyear3;
        }
    }
    else
    {
        this.document.myformname['refundratio_year3'].value="";
    }

    if(salesVolume!="" && chargeBackVolume!="")
    {
        if (Number(chargeBackVolume) > Number(salesVolume))
        {
            this.document.myformname['chargebackratio_year3'].value = "";
            document.getElementById('chargebackratio_year3').innerHTML = "Chargeback volume should be less than Sales volume";
            $(document.getElementById('chargebackratio_year3')).css('background-color', '#f2dede');
        }
        else if(Number(chargeBackVolume)=="0" && Number(salesVolume)=="0")
        {
            document.getElementById('chargebackratio_year3').innerHTML = "";
            $(document.getElementById('chargebackratio_year3')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_year3'].value = "0.00";
        }
        else
        {
            var chargeBackRatioyear3 = (Number(chargeBackVolume) / Number(salesVolume)) * 100;
            chargeBackRatioyear3 = chargeBackRatioyear3.toFixed(2);
            this.document.myformname['chargebackratio_year3'].value = "";
            document.getElementById('chargebackratio_year3').innerHTML = "";
            $(document.getElementById('chargebackratio_year3')).css('background-color', '#FFFFFF');
            this.document.myformname['chargebackratio_year3'].value = chargeBackRatioyear3;
        }
    }
    else
    {
        this.document.myformname['chargebackratio_year3'].value="";
    }
}

function isStartupBusiness(startupBusiness)
{
    alert(startupBusiness);
    if(startupBusiness=="Y")
    {
        document.myformname.company_lengthoftime_business.disabled = true;
        document.myformname.company_lengthoftime_business.value = "";
    }
    else
    {
        document.myformname.company_lengthoftime_business.disabled = false;
    }
}

//this is use for UploadBankTemplate
function myUploadBankTemplate(ctoken)
{
    document.getElementById("myformname").enctype = "multipart/form-data";
    document.getElementById("myformname").target = "";
    document.myformname.action="/icici/servlet/UploadBankTemplate?ctoken="+ctoken;
}

//This is use for Upload Bank template in admin
function uploadBankTemplate(ctoken)
{
    alert("Do you really want to replace file?");
    document.getElementById("myformname").enctype = "multipart/form-data";
    document.getElementById("myformname").target = "";
    document.myformname.action="/icici/servlet/UploadNewTemplate?action=upload&ctoken="+ctoken;
}

function myOpenBankGeneratedTemplate(ctoken)
{
    document.getElementById("myformname").target = "";
}

function myUpload(ctoken) {
    document.getElementById("myformname").enctype = "multipart/form-data";
    document.myformname.action="/icici/servlet/UploadServlet?ctoken="+ctoken;


}

function myCardVolume(cardVolumeVisa, cardVolumeMaster, cardVolumeAmericanExpress, cardVolumeDiscover, cardVolumeDinner, cardVolumeOther)
{
    alert("1");
    var cardVolume = Number(cardVolumeVisa) + Number(cardVolumeMaster) + Number(cardVolumeAmericanExpress) + Number(cardVolumeDiscover) + Number(cardVolumeDinner) + Number(cardVolumeOther);
    alert(Number(cardVolume));
}
