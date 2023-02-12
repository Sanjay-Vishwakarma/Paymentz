<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<style>
    @media(max-width:768px)
    {
        #h2carddetails
        {
            margin-left: -35px;
            margin-bottom: -29px;
            margin-top: -20px;
        }
        #creditlyfield
        {
            margin-left: -45px;

        }
        .creditly-wrapper .form-group {
            width: 100%;
            display: table;
        }
        #card_icon {

            margin-left: 275px;
            margin-top: -74px;

        }
        #fields
        {
            padding-left: 8px;
        }
        .form-group {
            margin-bottom: 10px;
        }




    }
    @media(min-width:768px)
    {
        #inputgrp
        {
            width:100%;
            display: -webkit-box;
        }
        #h2carddetails
        {
            margin-left: -14px;

        }
        #creditlyfield
        {
            margin-left: -45px;
            /*margin-top: 85px;*/
        }
        #card_icon
        {
            margin-left: -32px;
            margin-top: 6px;"
        }
        .creditly-wrapper .form-group {
            display: inline;
            width: 100%;
        }
        .form-group {
            margin-bottom: 10px;
        }
/*        #telnocc
        {width: 80%;}*/
    }

    @media(min-width:890px) {
        #inlinefield {
            display:inline-flex
        }
        .form-group {
            margin-bottom: 10px;
        }
    }

</style>
<style>
    .creditly-wrapper .form-control {
        display: block;
        width: 100%;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.428571429;
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
        vertical-align: middle;
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        background-image: none;
        border: 1px solid #ccc;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,0.075);
        box-shadow: inset 0 1px 1px rgba(0,0,0,0.075);
        -webkit-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
    }
</style>
<style>

    .panelheading_color
    {
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    }
    .headpanelfont_color
    {
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
    }
    .bodypanelfont_color
    {
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    }
    .panelbody_color
    {
    <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    }
    .mainbackgroundcolor /*box background*/
        {
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }
    .bodybackgroundcolor
    {
    <%=session.getAttribute("bodybgcolor")!=null?"background-color:"+session.getAttribute("bodybgcolor").toString()+"!important":""%>;
    }
    .bodyforegroundcolor
    {
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }
    .navigation_font_color
    {
    <%=session.getAttribute("navigation_font_color")!=null?"background-color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    }
    .textbox_color
    {
    <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
    }
    .icon_color
    {
    <%=session.getAttribute("icon_color")!=null?"background-color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
    }

</style>
<%
    ResourceBundle rb = null;
    //zh-CN;q=0.8,en-US;q=0.5,en;q=0.3
    String multiLanguage = request.getHeader("Accept-Language");
    String sLanguage[] = multiLanguage.split(",");
    if("zh-CN".contains(sLanguage[0]))
    {
        rb = LoadProperties.getProperty("com.directi.pg.creditpage","zh");
    }
    else
    {
        rb = LoadProperties.getProperty("com.directi.pg.creditpage");
    }
    final String CRE_FIRSTNAME=rb.getString("CRE_FIRSTNAME");
    final String CRE_LASTNAME=rb.getString("CRE_LASTNAME");

    final String CRE_CARDNUMBER=rb.getString("CRE_CARDNUMBER");
    final String CRE_CVV=rb.getString("CRE_CVV");
    final String CRE_EXPMONTH=rb.getString("CRE_EXPMONTH");
    final String CRE_EXPYEAR=rb.getString("CRE_EXPYEAR");

    final String CRE_ADDRESS=rb.getString("CRE_ADDRESS");
    final String CRE_CITY=rb.getString("CRE_CITY");
    final String CRE_ZIP=rb.getString("CRE_ZIP");
    final String CRE_COUNTRY=rb.getString("CRE_COUNTRY");
    final String CRE_COUNTRYCODE=rb.getString("CRE_COUNTRYCODE");
    final String CRE_STATE=rb.getString("CRE_STATE");
    final String CRE_STATECODE=rb.getString("CRE_STATECODE");
    final String CRE_PHONENO=rb.getString("CRE_PHONENO");
    final String CRE_PHONECC=rb.getString("CRE_PHONECC");
    final String CRE_EMAIL=rb.getString("CRE_EMAIL");

    final String CRE_CREDITCARDDEATILS=rb.getString("CRE_CREDITCARDDEATILS");

    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
    MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
    String fileName = getValue((String) session.getAttribute("filename"));
    String email = ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getEmail()));
    String city =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getCity()));
    String street =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getStreet()));
    String zip =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getZipCode()));
    String state =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getState()));
    String country =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getCountry()));
    String telno =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getPhone()));
    String telnocc =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getTelnocc()));
    String TMPL_COUNTRY =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getCountry()));
    String firstname =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getFirstname()));
    String lastname =  ESAPI.encoder().encodeForHTML(getValue(genericAddressDetailsVO.getLastname()));
    String cardType =  ESAPI.encoder().encodeForHTML(getValue(standardKitValidatorVO.getCardType()));
    String addressDetails =  ESAPI.encoder().encodeForHTML(getValue(standardKitValidatorVO.getTerminalVO
            ().getAddressDetails()));
    String addressValidation =  ESAPI.encoder().encodeForHTML(getValue(standardKitValidatorVO.getTerminalVO
            ().getAddressValidation()));

    String CRE_BILLINGADDRESS="";

    if(addressDetails.equalsIgnoreCase("Y") || (addressDetails.equalsIgnoreCase ("N") && addressValidation.equalsIgnoreCase("Y")))
    {
        CRE_BILLINGADDRESS=rb.getString("CRE_BILLINGADDRESS");
    }
    else
    {
        CRE_BILLINGADDRESS="Personal Details";
    }


    /*ResourceBundle rb = (ResourceBundle)request.getParameter("rb");
    String CRE_LASTNAME=rb.getString("CRE_LASTNAME");*/

%>
<%--<link rel="stylesheet" href="/merchant/transactionCSS/css/base.css">--%>
<%--<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>--%>
<script src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>
<script src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<%--
<h2 class="col-md-8 background panelheading_color headpanelfont_color" id="h2carddetails"><%=CRE_CREDITCARDDEATILS%></h2>
--%>
<script>
    $('.card input').bind('focus', function() {
        $('.card .status').hide();
    });

    $('.card input').bind('blur', function() {
        $('.card .status').show();
    });
</script>
<%--<script>
    $('.card input').cardcheck({
        callback: function(result) {

            var status = (result.validLen && result.validLuhn) ? 'valid' : 'invalid',
                    message = '',
                    types = '';

            // Get the names of all accepted card types to use in the status message.
            for (i in result.opts.types) {
                types += result.opts.types[i].name + ", ";
            }
            types = types.substring(0, types.length-2);

            // Set status message
            if (result.len < 1) {
                message = 'Please provide a credit card number.';
            } else if (!result.cardClass) {
                message = 'We accept the following types of cards: ' + types + '.';
            } else if (!result.validLen) {
                message = 'Please check that this number matches your ' + result.cardName + ' (it appears to be the wrong number of digits.)';
            } else if (!result.validLuhn) {
                message = 'Please check that this number matches your ' + result.cardName + ' (did you mistype a digit?)';
            } else {
                message = 'Great, looks like a valid ' + result.cardName + '.';
            }

            // Show credit card icon
            $('.card .card_icon').removeClass().addClass('card_icon ' + result.cardClass);

            // Show status message
            $('.card .status').removeClass('invalid valid').addClass(status).children('.status_message').text(message);
        }
    });

</script>--%>
<script>
    jQuery(function($) {

        // If JavaScript is enabled, hide fallback select field
        $('.no-js').removeClass('no-js').addClass('js');

        // When the user focuses on the credit card input field, hide the status
        $('.card input').bind('focus', function() {
            $('.card .status').hide();
        });

        // When the user tabs or clicks away from the credit card input field, show the status
        $('.card input').bind('blur', function() {
            $('.card .status').show();
        });

        // Run jQuery.cardcheck on the input
        $('.card input').cardcheck({
            callback: function(result) {

                var status = (result.validLen && result.validLuhn) ? 'valid' : 'invalid',
                        message = '',
                        types = '';

                // Get the names of all accepted card types to use in the status message.
                for (i in result.opts.types) {
                    types += result.opts.types[i].name + ", ";
                }
                types = types.substring(0, types.length-2);

                // Set status message
                /*if (result.len < 1) {
                    message = 'Please provide a credit card number.';
                } else if (!result.cardClass) {
                    message = 'We accept the following types of cards: ' + types + '.';
                } else if (!result.validLen) {
                    message = 'Please check that this number matches your ' + result.cardName + ' (it appears to be the wrong number of digits.)';
                } else if (!result.validLuhn) {
                    message = 'Please check that this number matches your ' + result.cardName + ' (did you mistype a digit?)';
                } else {
                    message = 'Great, looks like a valid ' + result.cardName + '.';
                }*/

                // Show credit card icon
                $('.card .card_icon').removeClass().addClass('card_icon ' + result.cardClass);

                // Show status message
                $('.card .status').removeClass('invalid valid').addClass(status).children('.status_message').text(message);
            }
        });
    });
</script>
<style type="text/css">

    .card .card_icon,  .card .status_icon {
        /* For a more robust cross-browser implementation, see http://bit.ly/aqZnl3 */
        display: inline-block;
        vertical-align: bottom;
        height: 23px;
        width: 27px;
    }

    /* --- Card Icon --- */

    .card .card_icon { background: transparent url('../images/transaction/cardscheme/credit_card_sprites.png') no-repeat 30px 0; }

    /* Need to support IE6? These four rules won't work, so rewrite 'em. */

    .card .card_icon.visa { background-position: 0 0 !important; }

    .card .card_icon.mastercard { background-position: -30px 0 !important; }

    .card .card_icon.amex { background-position: -60px 0 !important; }

    .card .card_icon.discover { background-position: -90px 0 !important; }

    /* --- Card Status --- */

    .card .status_icon { background: transparent url('../images/transaction/cardscheme/status_sprites.png') no-repeat 33px 0; }

    .card .invalid {
        color: #AD3333;
        background: #f8e7e7;
    }

    .card .valid {
        color: #33AD33;
        background: #e7f8e7;
    }

    .card .invalid .status_icon { background-position: 3px 0 !important; }

    .card .valid .status_icon { background-position: -27px 0 !important; }
</style>
<section class="creditly-wrapper" style="margin-bottom: -46px;">
    <div class="credit-card-wrapper"  id="creditlyfield">
        <div class="first-row form-group" id="first-row">
            <input type="hidden" name="cardtype" value="<%=cardType%>">
            <div class=" form-group col-md-12 controls" id="inlinefield">
                <div class=" form-group col-md-12 controls" >
                    <label class="control-label headpanelfont_color" style="width:100%"><%=CRE_CARDNUMBER%></label>

                        <div class="field card" id="inputgrp">

                            <input class="number credit-card-number form-control textbox_color" type="text"
                                   name="cardnumber" style="z-index: 0;" inputmode="numeric" autocomplete="OFF"
                                   maxlength="19"
                                   placeholder="&#149;&#149;&#149;&#149;&#149;&#149;&#149;&#149; &#149;&#149;&#149;&#149; &#149;&#149;&#149;&#149;">
                            <span class="card_icon" id="card_icon" ></span>
                        </div>
                </div>
            </div>
            <div class=" form-group col-md-12 controls" id="inlinefield">

            <div class="form-group col-md-4 controls">
                <label class="control-label headpanelfont_color"><%=CRE_EXPMONTH%></label>
                <select id="expiry_month" name="expiry_month" class="form-control textbox_color">
                    <option VALUE="01" selected>January</option>
                    <option VALUE="02">February</option>
                    <option VALUE="03">March</option>
                    <option VALUE="04">April</option>
                    <option VALUE="05">May</option>
                    <option VALUE="06">June</option>
                    <option VALUE="07">July</option>
                    <option VALUE="08">August</option>
                    <option VALUE="09">September</option>
                    <option VALUE="10">October</option>
                    <option VALUE="11">November</option>
                    <option VALUE="12">December</option>
                </select>
            </div>
            <div class="form-group col-md-4 controls">
                <label class="control-label headpanelfont_color"><%=CRE_EXPYEAR%></label>
                <select name="expiry_year" id="expiry_year" class="form-control textbox_color">
                    <option VALUE="2018" selected>2018</option>
                    <option VALUE="2019">2019</option>
                    <option VALUE="2020">2020</option>
                    <option VALUE="2021">2021</option>
                    <option VALUE="2022">2022</option>
                    <option VALUE="2023">2023</option>
                    <option VALUE="2024">2024</option>
                    <option VALUE="2025">2025</option>
                    <option VALUE="2026">2026</option>
                    <option VALUE="2027">2027</option>
                    <option VALUE="2028">2028</option>
                    <option VALUE="2029">2029</option>
                    <option VALUE="2030">2030</option>
                </select>
            </div>

            <div class="form-group col-md-4 controls">
                <label class="control-label headpanelfont_color"><%=CRE_CVV%></label>
                <input class="security-code form-control textbox_color" id="cvv" inputmode="numeric" name="cvv" size="2" maxlength="4" autocomplete="OFF" type="password" placeholder="&#149;&#149;&#149;">
            </div>
                </div>
            <div class=" form-group col-md-12 controls" id="inlinefield">

            <div class="form-group col-md-4 controls" >
                <label class="control-label headpanelfont_color" for="firstname"><%=CRE_FIRSTNAME%></label>
                <input type="text"  size="30" class="form-control textbox_color" id="firstname" name="firstname" placeholder="<%=CRE_FIRSTNAME%>" value="<%=firstname%>">
            </div>
            <div class="form-group col-md-4 controls">
                <label class="control-label headpanelfont_color" for="lastname"><%=CRE_LASTNAME%></label>
                <input type="text" class="form-control textbox_color"  id="lastname" name="lastname" size="30" placeholder="<%=CRE_LASTNAME%>" value="<%=lastname%>">
                <br>
            </div>
                <div class="form-group col-md-4 controls">
                    <label class="headpanelfont_color">Language</label>

                    <select NAME="language" class="form-control textbox_color">
                        <option VALUE="ENG" selected>ENG</option>
                        <option VALUE="CHN">CHN</option>
                        <option VALUE="RUS">RUS</option>
                    </select>
                </div>
                </div>
            <h2 class="col-md-12 controls headpanelfont_color"  style="margin-top: -14px;font-size: 16px;margin-left: 7px;" ><%=CRE_BILLINGADDRESS%></h2>


            <%if(fileName!=null){%>
            <jsp:include page="<%=fileName%>"></jsp:include>
            <%}%>

            <%

                if(addressDetails.equalsIgnoreCase("Y") ||/*addressValidation.equalsIgnoreCase("Y") ||*/ (addressDetails.equalsIgnoreCase ("N") && addressValidation.equalsIgnoreCase("Y")))
                {


            %>
            <div class=" form-group col-md-12 controls" id="inlinefield">
            <div class="form-group col-md-12 controls">
                <label class="control-label headpanelfont_color" for="street"><%=CRE_ADDRESS%></label>
                <input type="text" class="form-control textbox_color" id="street" name="street" size="40" value="<%=street%>" placeholder="<%=CRE_ADDRESS%>" >
            </div>
            </div>
            <div class=" form-group col-md-12 controls" id="inlinefield">
            <div class="form-group col-md-6 controls">
                <label class="control-label headpanelfont_color" for="city"><%=CRE_CITY%></label>
                <input type="text"  class="form-control textbox_color" name="city" id="city" size="20" value="<%=city%>" placeholder="<%=CRE_CITY%>">
            </div>
            <div class="form-group col-md-6 controls">
                <label class="control-label headpanelfont_color" for="zip"><%=CRE_ZIP%></label>
                <input type="text" class="form-control textbox_color"  id="zip" name="zip" size="10" title="allow only alphanumeric value" value="<%=zip%>" placeholder="<%=CRE_ZIP%>">
            </div>
                </div>
            <div class=" form-group col-md-12 controls" id="inlinefield">
            <%--add New--%>
            <div class="form-group col-md-6 controls">
                <label  class="control-label headpanelfont_color Country"><%=CRE_COUNTRY%></label>
                <%
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId());
                    String gatewayType=account.getGateway();
                    if(AuxPayPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
                    {
                        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

                        country=commonFunctionUtil.getCountry3LetterAbbreviation(country);
                %>
                <jsp:include page="3charcountrycode.jsp"></jsp:include>
                <%
                }
                else
                {
                %>
                <jsp:include page="2charcountrycode.jsp">
                    <jsp:param name="country" value="<%=country%>"/>
                </jsp:include>
                <%
                    }
                %>

            </div>
            <div class="form-group col-md-6 controls">
                <label class="control-label headpanelfont_color" for="lastname"><%=CRE_COUNTRYCODE%></label>
                <input type="text" class="form-control textbox_color"  id="countrycode" name="countrycode" value="<%=country%>" readonly="readonly">
            </div>
                </div>
            <div class=" form-group col-md-12 controls" id="inlinefield">
            <div class="form-group col-md-6 controls">
                <label class="control-label headpanelfont_color State"><%=CRE_STATE%></label>
                <select name="us_states" id="us_states_id" onchange="usstate();" class="form-control textbox_color" disabled="true">
                    <option value="Select one">Select a State for US only</option>
                    <option value="AL">ALABAMA</option>
                    <option value="AK">ALASKA</option>
                    <option value="AS">AMERICAN SAMOA</option>
                    <option value="AZ">ARIZONA</option>
                    <option value="AR">ARKANSAS</option>
                    <option value="CA">CALIFORNIA</option>
                    <option value="CO">COLORADO</option>
                    <option value="CT">CONNECTICUT</option>
                    <option value="DE">DELAWARE</option>
                    <option value="DC">DISTRICT OF COLUMBIA</option>
                    <option value="FM">FEDERATED STATES OF MICRONESIA</option>
                    <option value="FL">FLORIDA</option>
                    <option value="GA">GEORGIA</option>
                    <option value="GU">GUAM GU</option>
                    <option value="HI">HAWAII</option>
                    <option value="ID">IDAHO</option>
                    <option value="IL">ILLINOIS</option>
                    <option value="IN">INDIANA</option>
                    <option value="IA">IOWA</option>
                    <option value="KS">KANSAS</option>
                    <option value="KY">KENTUCKY</option>
                    <option value="LA">LOUISIANA</option>
                    <option value="ME">MAINE</option>
                    <option value="MH">MARSHALL ISLANDS</option>
                    <option value="MD">MARYLAND</option>
                    <option value="MA">MASSACHUSETTS</option>
                    <option value="MI">MICHIGAN</option>
                    <option value="MN">MINNESOTA</option>
                    <option value="MS">MISSISSIPPI</option>
                    <option value="MO">MISSOURI</option>
                    <option value="MT">MONTANA</option>
                    <option value="NE">NEBRASKA</option>
                    <option value="NV">NEVADA</option>
                    <option value="NH">NEW HAMPSHIRE</option>
                    <option value="NJ">NEW JERSEY</option>
                    <option value="NM">NEW MEXICO</option>
                    <option value="NY">NEW YORK</option>
                    <option value="NC">NORTH CAROLINA</option>
                    <option value="ND">NORTH DAKOTA</option>
                    <option value="MP">NORTHERN MARIANA ISLANDS</option>
                    <option value="OH">OHIO</option>
                    <option value="OK">OKLAHOMA</option>
                    <option value="OR">OREGON</option>
                    <option value="PW">PALAU</option>
                    <option value="PA">PENNSYLVANIA</option>
                    <option value="PR">PUERTO RICO</option>
                    <option value="RI">CRHODE ISLAND</option>
                    <option value="SC">SOUTH CAROLINA</option>
                    <option value="SD">SOUTH DAKOTA</option>
                    <option value="TN">TENNESSEE</option>
                    <option value="TX">TEXAS</option>
                    <option value="UT">UTAH</option>
                    <option value="VT">VERMONT</option>
                    <option value="VI">VIRGIN ISLANDS</option>
                    <option value="VA">VIRGINIA</option>
                    <option value="WA">WASHINGTON</option>
                    <option value="WV">WEST VIRGINIA</option>
                    <option value="WI">WISCONSIN</option>
                    <option value="WY">WYOMING</option>

                </select>

            </div>
            <div class="form-group col-md-6 controls">
                <label for="b_state" class="control-label headpanelfont_color"><%=CRE_STATECODE%></label>
                <input name="state" class="form-control textbox_color"  value="<%=state%>"  type="text" id="b_state" size="10">

            </div>
                </div>
            <div class=" form-group col-md-12 controls" id="inlinefield">
            <div class="form-group col-md-3 controls" >
                <label class="control-label headpanelfont_color telnocc" ><%=CRE_PHONECC%></label>
                <input type="text" name="telnocc" size="5"
                       id="telnocc" class="form-control textbox_color" value="<%=telnocc%>" readonly="readonly"
                       title="allow only numeric value">
            </div>

            <div class="form-group col-md-3 controls" id="phoneno">
                <label class="control-label headpanelfont_color telnocc"><%=CRE_PHONENO%></label>
                <input type="text" name="telno" size="20" id="telno" value="<%=telno%>" class="form-control textbox_color" title="allow only numeric value">

            </div>
                </div>
            <%
                }
            %>
            <div class=" form-group col-md-12 controls" id="inlinefield">
            <div class="form-group col-md-6 controls">
                <label class="control-label headpanelfont_color"
                       for="emailaddr"><%=CRE_EMAIL%></label>
                <input type="text" name="emailaddr" id="emailaddr"
                       class="form-control textbox_color"  size="30" placeholder="<%=CRE_EMAIL%>" value="<%=email%>"/>
            </div>
                </div>

        </div>
    </div>
</section>


<%!
    private String getValue(String data)
    {
        String tempVal="";
        if(data==null)
        {
            tempVal="";
        }
        else
        {
            tempVal= data;
        }
        return tempVal;
    }
%>
