/**
 * Created by Nikhil Poojari on 28-Apr-18.
 */



$(document).ready(function(){

    $("input[name='recurringservices']").on("click", function ()
    {

        if ($('input:radio[name=recurringservices]:checked').val() == "N")
        {
            document.myformname.recurringservicesyes.disabled = true;
            document.myformname.recurringservicesyes.value = "";

        }
        else
        {
            document.myformname.recurringservicesyes.disabled = false;
        }
    });

    $("input[name='billing_model']").on("click", function ()
    {
        if ($('input:radio[name=billing_model]:checked').val() == "payment_other")
        {
            document.myformname.payment_type_yes.disabled = false;
        }
        else
        {
            document.myformname.payment_type_yes.disabled = true;
            document.myformname.payment_type_yes.value = "";
        }
    });


    $("input[name='billing_model']").on("click", function ()
    {
        if ($('input:radio[name=billing_model]:checked').val() == "one_time" || $('input:radio[name=billing_model]:checked').val() == "pay_per_minute" || $('input:radio[name=billing_model]:checked').val() == "pay_per_download" || $('input:radio[name=billing_model]:checked').val() == "payment_other")
        {
            document.myformname.recurring_amount.disabled = true;
            $('input:radio[name=billing_timeframe]').each(function ()
            {
                $(this).attr('disabled', 'disabled');
                $(this).parent('.iradio_square-aero').addClass('disabled');
            });
        }
        else
        {
            document.myformname.recurring_amount.disabled = false;
            $('input:radio[name=billing_timeframe]').each(function ()
            {
                $(this).removeAttr('disabled');
                $(this).parent('.iradio_square-aero').removeClass('disabled');
            });
        }
    });
    $("input[name='seasonal_fluctuating']").on("click", function ()
    {
        if ($('input:radio[name=seasonal_fluctuating]:checked').val() == "N")
        {
            document.myformname.seasonal_fluctuating_yes.disabled = true;

        }
        else
        {
            document.myformname.seasonal_fluctuating_yes.disabled = false;
        }
    });


    /*$( "input[name='cardtypesaccepted_other']").on( "click", function() {
     if($('input:checkbox[name=cardtypesaccepted_other]:checked').val()=="Y")
     {
     document.myformname.cardtypesaccepted_other_yes.disabled = false;
     }
     else
     {
     document.myformname.cardtypesaccepted_other_yes.disabled = true;
     document.myformname.cardtypesaccepted_other_yes.value = "";
     }
     });*/
    $( "input[name='is_website_live']").on( "click", function() {
        if($('input:radio[name=is_website_live]:checked').val()=="N")
        {
            document.myformname.test_link.disabled = true;
            document.myformname.test_link.value = "";

        }
        else
        {
            document.myformname.test_link.disabled = false;
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


    $( "input[name='orderconfirmation_other']").on( "click", function() {
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


    $( "input[name='listfraudtools']").on( "click", function() {
        if($('input:radio[name=listfraudtools]:checked').val()=="N")
        {
            document.myformname.listfraudtools_yes.disabled = true;
            document.myformname.listfraudtools_yes.value = "";

        }
        else
        {
            document.myformname.listfraudtools_yes.disabled = false;
        }
    });


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

    /*$( "input[name='copyright']").on( "click", function() {
     if($('input:radio[name=copyright]:checked').val()=="N")
     {
     document.myformname.sourcecontent.disabled = true;
     document.myformname.sourcecontent.value = "";
     }
     else
     {
     document.myformname.sourcecontent.disabled = false;
     }
     });
     */

    $( "input[name='agency_employed']").on( "click", function() {
        if($('input:radio[name=agency_employed]:checked').val()=="N")
        {
            document.myformname.agency_employed_yes.disabled = true;
            document.myformname.agency_employed_yes.value = "";

        }
        else
        {
            document.myformname.agency_employed_yes.disabled = false;
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


    $( "input[name='customer_support']").on( "click", function() {
        if($('input:radio[name=customer_support]:checked').val()=="N")
        {
            document.myformname.customersupport_email.disabled = true;
            document.myformname.customersupport_email.value = "";

            document.myformname.custsupportwork_hours.disabled= true;
            document.myformname.custsupportwork_hours.value = "";

            document.myformname.timeframe.disabled= true;
            document.myformname.timeframe.value = "";

            document.myformname.technical_contact.disabled=true;
            document.myformname.technical_contact.value = "";

        }
        else
        {
            document.myformname.customersupport_email.disabled = false;
            document.myformname.custsupportwork_hours.disabled= false;
            document.myformname.timeframe.disabled= false;
            document.myformname.technical_contact.disabled=false;

        }
    });


    $( "input[name='MCC_Ctegory']").on( "click", function() {
        if($('input:radio[name=MCC_Ctegory]:checked').val()=="Y")
        {
            document.myformname.merchantcode.disabled = false;
            $(document.getElementsByName("merchantcode")[0]).css('background-color', '#FFFFFF');
        }
        else
        {
            document.myformname.merchantcode.disabled = true;
            $(document.getElementsByName("merchantcode")[0]).css('background-color', '#EBEBE4');
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


});

