<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%--
  Created by IntelliJ IDEA.
  User: sagar
  Date: 8/25/14
  Time: 9:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    Functions functions=new Functions();
    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    String merchantSiteName = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain());
    //String partnerSiteUrl = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerDomain());
    Logger log = new Logger("commonPayment.jsp");

    String headerVal = "";

    if(functions.isValueNull((String)session.getAttribute("X-Frame-Options")) && "SAMEORIGIN".equals((String)session.getAttribute("X-Frame-Options")))
    {
        response.setHeader("X-Frame-Options", (String) session.getAttribute("X-Frame-Options"));
    }
    else
    {
        String responseOrigin = request.getHeader("referer");
        if(functions.isValueNull(responseOrigin) && functions.isValueNull(merchantSiteName) /*&& responseOrigin.contains(merchantSiteName)*/)
        {

            headerVal = functions.getIFrameHeaderValue(responseOrigin,merchantSiteName);
            if("SAMEORIGIN".equals(headerVal))
            {
                response.setHeader("X-Frame-Options",headerVal);
                session.setAttribute("X-Frame-Options", headerVal);
            }

        }
        else
        {
            headerVal = "SAMEORIGIN";
            response.setHeader("X-Frame-Options",headerVal);
            session.setAttribute("X-Frame-Options", headerVal);
        }
        log.debug("origin---"+responseOrigin);

    }

    log.debug("header---" + headerVal);
    response.setHeader("Cache-control", "no-store"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP1.0
    response.setDateHeader("Expire", 0); //prevents caching at the proxy server
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html");
%>

<html>
<head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />


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
        <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
        }
        .textbox_color
        {
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        }
        .icon_color
        {
        <%=session.getAttribute("icon_color")!=null?"background-color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
        }

        /*.nav-tabs li a {
            margin-right: 0;
            border: 0;
            background-color:  #7eccad!important;
        }
        .nav-tabs li a:hover {
            background-color: #abb7b7!important;
        }

        .nav-tabs > li > a{
            border-radius: 0;
            border-bottom: 1px solid #fff !important;
        }

        .nav-tabs .active .glyphicon{
            color: #fff!important;
        }

        .btn-default {
            color: #FFF!important;
            background-color: #7eccad!important;
            border-color: transparent!important;
        }

        .btn-default:hover, .btn-default:focus, .btn-default:active, .btn-
default.active, .open > .dropdown-toggle.btn-default {
            color: #FFF!important;
            background-color: #abb7b7!important;
            border-color: transparent!important;
        }

        #tabid{
            /!*margin-left: 15px;*!/
            margin-top: -25px;
            padding-left: 0;
            padding-right: 0;
        }

        @media(max-width:992px){
            #tabid{
                margin-left: -60px;
            }
        }*/

    </style>


    <style type="text/css">
        /*@media(min-width:900px)
        {
            #myTabContent
            {
                margin-bottom: 2%;
            }
            #navtabdiv
            {
                height:0%;
            }

        }
        @media(max-width: 1254px)
        {

        }*/
        @media(min-width:890px) {
            #boxsize
            {
             width:80%
            }

        }
        @media(min-width:1000px) {
            #boxsize
            {
                width:100%
            }

        }
            @media(max-width:768px)
        {
            #sessionTimer
            {
                margin-top: 72px;
                margin-left: 257px;

            }
            #logo
            {

                left:19px;
                margin-top: -16px;
                margin-left: -86px;
            }
            #timerdiv
            {
                margin-top: 50px;
                margin-left: 75px;
            }


            #navtabdiv
            {
                height: auto;
                width: 56%;
                margin-top: 17px;
                margin-left: 40px;
                display: none;
                z-index: 999;
                position: absolute;

            }
            #orderdetailsdiv
            {
                /*position: absolute;
                width: 102%;
                margin-left: -9px;
                padding: 5px;
                margin-top: -103px;
                border: 0px;
                border-top: 0px;
                float: right;*/

                position: absolute;
                width: 94%;
                /* width: 92%; */
                margin-top: -98px;
                border: 0px;
                border-top: 0px;
                float: right;
                margin-left: 14px;

            }
            #h4payusing
            {
                margin-top: 0px;
                margin-left: -14px;
            }
            #chktermsandcond
            {  margin-left: -20px;
            }
            #hideshowbutton
            {
                display:inherit;
                position: absolute;
                left: -6px;
                top: 17px;
                z-index: 2;
                padding: 10px 18px;
                height: 45px;
                font-size: 21px;
                background: gray;
                color: #fff;
                border: none;
                width: 45px;
            }
            #ctype
            {
                /*border: 1px solid #7eccad!important;
                margin-right: 94px;
                width: 41%;
                height: auto;
                margin-left: -23px;
                margin-bottom: 10px;*/
                border: 1px solid #ccc!important;
                margin-right: 34px;
                width: 45%;
                height: auto;
                margin-left: -24px;
                margin-bottom: 10px;
                /*background-color: white;*/



            }
            #radiocardtype
            {
                margin-left: 30px;
            }
            #maindiv
            {
                background-color: #ffffff;
                margin-top: 0px;
                height: auto;
                /*border: 2px solid #dadada!important;*/
            }
            #h2
            {
                margin-left: 36px;
            }
            #h2order
            {
                margin-top: 1px;
            <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
            <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
                margin-left: 32px;
            }
            #imgheaderlogo
            {
                /*height: 62px;*/
                margin-top: 0px;
            }
        }
        @media(min-width:768px)
        {
            #sessionTimer
            {
                position: absolute;
                top: 86px;
                z-index: 60;
                width: auto;
                height: auto;
                padding: 10;
                font-weight: bold;
                float: right;
                margin-left: 65%;

            }
            #logo
            {
                margin-left: -55px;
                position: absolute;
                left: 48%;
                top: 0;
                z-index: 60;
                width: auto;
                height:auto;
                padding: 0;
            }
            #timerdiv
            {
                margin-top: 50px;
                margin-left: 75px;
            }
            #navtabdiv
            {
                /*border: 2px solid #dadada!important;*/
                margin-left: -15px;
                /*background: #f4f4f4;*/
                display:inherit!important;
                height: auto;
            }
            #orderdetailsdiv
            {
                background-color: #ffffff;
                margin-top: -31px;
                padding: 0px;
                border: 2px solid #7eccad!important;
                border-top: 6px solid #7eccad!important;
                float: right;
                width: 38%;
                right: -14px;
            }
            #hideshowbutton
            {
                display:none;
            }
            #ctype
            {
                border: 1px solid #ccc!important;
                margin-right: 6px;
                width:auto;
                height: auto;
                margin-bottom: 10px;
                /*background-color: white;*/
            }
            #maindiv
            {
                background-color:#ffffff;
                margin-top: 141px;
                height:auto;
                /*border: 2px solid #dadada!important;*/
            }
            #h4payusing
            {
                margin-left: 10px;
            }
            #row
            {
                margin-top: 10px;
            }
            #sterror
            {
                margin-top: 126px;
                width: 50%;
                margin-bottom: -95px;
                margin-left: 25%;
            }
            #h2order
            {
                margin-top: 1px;
            <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
                color: white;

            }
            #imgheaderlogo
            {
               /* height: 32px;*/

            }

        }
        #poptuk_content{
            width: 475px!important;
        }

        @media (max-width:479px) {
            #poptuk_content{
                width: 100%!important;
                margin-top: 50%!important;
            }

            #poptuk{
                padding: 10px!important;
            }
        }

        #poptuk{
            z-index: 9999!important;
        }


    </style>




    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-
scale=1, user-scalable=no">

    <link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">

    <script type="text/javascript"
            src="/merchant/transactionCSS/js/creditly.js"></script>

    <link rel="stylesheet" type="text/css" href="/merchant/style/poptuk.css">
    <script type='text/javascript' src='/merchant/style/poptuk.js'></script>


    <link rel="stylesheet"
          href="/merchant/transactionCSS/css/bootstrap_tr.min.css">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/style_tr.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery-
1.11.1_tr.min.js"></script>
    <script type="text/javascript"
            src="/merchant/transactionCSS/js/bootstrap_tr.min.js"></script>
        <link href="/merchant/NewCss/libs/font-awesome/css/font-awesome.min.css"
    rel="stylesheet" />

  <%--  START TIMEOUT--%>
    <script type="text/javascript">
        var timeoutHandle = null;
        function startTimer(timeoutCount) {
            var maxtime=document.timeoutform.maxtime.value;
            if (timeoutCount <= 0) {
                document.getElementById('sessionout').submit();
            } else if (timeoutCount < maxtime) {
                document.getElementById('sessionTimer').innerHTML = '<div class=\"texthead\">' + ' TimeOut : '+'<b>' + Math.floor(timeoutCount /60)+':'+("0" + (timeoutCount %60)).slice(-2)+ '</b>'+ ' </div>' ;
            }
            timeoutHandle = setTimeout(function () { startTimer(timeoutCount-1);}, '1000');
            //for country code
            var hat = this.document.myformname.countrycode.value.trim();
            if(hat!='')
            {
                var hatto = this.document.myformname.country.options.length;
                for(i=0;i<hatto;i++)
                {
                    var countryList = this.document.myformname.country.options[i].value
                    var country = countryList.split("|")[0];

                    if(country==hat)
                    {
                        this.document.myformname.country.options[i].selected=true
                        this.document.myformname.telnocc.value= countryList.split("|")[1];
                        if(hat=='US')
                        {
                            $("#us_states_id").attr("disabled", false);
                            document.getElementById("b_state").readOnly = true;
                        }
                        else
                        {
                            $("#us_states_id").attr("disabled", true);
                            document.getElementById("b_state").readOnly = false;
                            //document.getElementById("b_state").value = "";
                        }
                    }
                }
            }

        }
        function refreshTimer() {
            killTimer(timeoutHandle);
            startTimer(0);
        }
    </script>
    <%--End session timeout--%>

    <script language="javascript">
        function lancer() {
            poptuk(document.getElementById('poptuk').style.display="");


        }
    </script>
    <script type="text/javascript">
        $(function ()
        {
            $(document).tooltip({
                position: {
                    my: "left",
                    at: "right",
                    using: function (position, feedback)
                    {
                        $(this).css(position);
                        $("<div>")
                                .addClass("arrow")
                                .addClass(feedback.vertical)
                                .addClass(feedback.horizontal)
                                .appendTo(this);
                    }
                }
            });




            var itemType = $('ul').find('li.active').children('a').attr("href");

            if (itemType === undefined || itemType === null)
            {
                itemType = document.getElementById("singlePayment").value;
                $('#paymenttype').val(itemType);
            }
            else
            {
                itemType = $('ul').find('li.active').children('a').attr("href");
                $('#paymenttype').val(itemType.replace("#", ""));
            }

            //


            $('input[type="radio"]').click(function () {

                $('#cardType').val($("input[name=cardtype]:checked").val());
                $('#myform').submit();

            });

            $("li").click(function()
            {
                var Type_id= $(this).children('a').attr("href");
                var card_id = $("input[name=cardtype]:checked").val();

                $('#paymenttype').val(Type_id.replace("#",""));
                $('#cardType').val("");
                $('#myform').submit();

            });
        });
    </script>
    <script type="text/javascript">

        /*function myjunk()
         {
         var hat = this.document.myformname.country.selectedIndex
         var hatto = this.document.myformname.country.options[hat].value

         if(hatto == 'US|001')
         {
         $("#us_states_id").attr("disabled", false);
         document.getElementById("b_state").readOnly = true;
         }
         else
         {
         $("#us_states_id").attr("disabled", true);
         document.getElementById("b_state").readOnly = false;
         document.getElementById("b_state").value = "";
         }

         if (hatto != 'Select one')
         {

         this.document.myformname.countrycode.value = hatto.split("|")[0];
         this.document.myformname.telnocc.value = hatto.split("|")[1];
         this.document.myformname.country.options[0].selected=true

         }


         }*/

        function myjunk1()
        {
            var hat = this.document.myformname.country.selectedIndex
            var hatto = this.document.myformname.country.options[hat].value

            if(hatto == 'USA|001')
            {
                $("#us_states_id").attr("disabled", false);
                document.getElementById("b_state").readOnly = true;
            }
            else
            {
                $("#us_states_id").attr("disabled", true);
                document.getElementById("b_state").readOnly = false;
                document.getElementById("b_state").value = "";
            }

            if (hatto != 'Select one')
            {
                this.document.myformname.countrycode.value = hatto.split("|")[0];
                this.document.myformname.telnocc.value = hatto.split("|")[1];
                this.document.myformname.country.options[0].selected=true
            }
        }
        function myjunk()
        {
            //var hat = this.document.myformname.country.selectedIndex
            //var hatto = this.document.myformname.country.options[hat].value

            var hatto = $('#country').val();
            if(hatto == 'US|001')
            {
                $("#us_states_id").attr("disabled", false);
                document.getElementById("b_state").readOnly = true;
            }
            else
            {
                $("#us_states_id").attr("disabled", true);
                document.getElementById("b_state").readOnly = false;
                document.getElementById("b_state").value = "";
            }

            if (hatto != 'Select one')
            {

                $("#countrycode").val(hatto.split("|")[0]);
                $("#telnocc").val(hatto.split("|")[1]);
                //$('#country option')[0].selected = true;

                $('select[name^="country"] option:selected').attr("selected",true);

                //this.document.myformname.countrycode.value = hatto.split("|")[0];
                //this.document.myformname.telnocc.value = hatto.split("|")[1];
                //this.document.myformname.country.options[0].selected=true

            }
        }
        function usstate()
        {
            var hat = this.document.myformname.us_states.selectedIndex
            var hatto = this.document.myformname.us_states.options[hat].value

            if (hatto != 'Select one') {

                this.document.myformname.b_state.value = hatto;
                this.document.myformname.us_states.options[0].selected=true

            }
        }
        function submitForm()
        {
            if(document.myformname.TC.checked)
            {
                document.getElementById('paynow').disabled=false;
            }
            else
            {
                alert("kindly accept terms and condition.");
                document.getElementById('paynow').disabled=true;
            }

        }

    </script>
    <script type="text/javascript">
        function clickIE4(){
            if (event.button==2){
                return false;
            }
        }
        function clickNS4(e){
            if (document.layers||document.getElementById&&!document.all){
                if (e.which==2||e.which==3){
                    return false;
                }
            }
        }
        if (document.layers){
            document.captureEvents(Event.MOUSEDOWN);
            document.onmousedown=clickNS4;
        }
        else if (document.all&&!document.getElementById){
            document.onmousedown=clickIE4;
        }
        document.oncontextmenu=new Function("alert(message);return false")
    </script>
</head>

<%
    /* final Locale SIMPLIFIED_CHINESE;

     Locale enLocale = new Locale("en", "SC");*/

   /* request.setCharacterEncoding("UTF-8");*/

    ResourceBundle rb = null;
    //zh-CN;q=0.8,en-US;q=0.5,en;q=0.3
    String multiLanguage = "";

    if(functions.isValueNull(request.getHeader("Accept-Language")))
    {
        multiLanguage = request.getHeader("Accept-Language");
        String sLanguage[] = multiLanguage.split(",");
        if ("zh-CN".contains(sLanguage[0]))
        {
            rb = LoadProperties.getProperty("com.directi.pg.creditpage", "zh");
        }
        else
        {
            rb = LoadProperties.getProperty("com.directi.pg.creditpage");
        }
    }
    else
    {
        rb = LoadProperties.getProperty("com.directi.pg.creditpage");
    }


    final String CRE_PAYMENTGATEWAY=rb.getString("CRE_PAYMENTGATEWAY");
    final String CRE_PAYMENTGATEWAY2=rb.getString("CRE_PAYMENTGATEWAY2");
%>

<%
    String timetikcer = null;
    try
    {
        timetikcer = ApplicationProperties.getProperty
                ("PAYMENT_TIMETICKER_INSECONDS");
    }
    catch (Exception e)
    {

    }
    String ctoken = "";

    ctoken = (String) session.getAttribute("ctoken");
    long time = 0;
    if (session.getAttribute("timeDiff") != null)
    {
        time = Long.valueOf(session.getAttribute("timeDiff").toString());
    }
    time = Long.valueOf(timetikcer) - time;

    Enumeration<String> enames = session.getAttributeNames();
    while (enames.hasMoreElements())
    {
        String name = enames.nextElement();
        log.error("Attribute=="+name+" Value=="+session.getAttribute(name));

    }

%>
<body class="bodybackgroundcolor bodyforegroundcolor" onload="startTimer(<%=time%>)" >
<%
    String merchantLogo = "";
    //CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");

    GenericTransDetailsVO genericTransDetailsVO = standardKitValidatorVO.getTransDetailsVO();
    GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
    MerchantDetailsVO merchantDetailsVO= standardKitValidatorVO.getMerchantDetailsVO();
    RecurringBillingVO recurringBillingVO= standardKitValidatorVO.getRecurringBillingVO();
    String amount = getValue(genericTransDetailsVO.getAmount());
    String childFile=getValue((String)session.getAttribute("childfile"));
    String partnerName = getValue(standardKitValidatorVO.getPartnerName());
    TerminalVO terminalVO = standardKitValidatorVO.getTerminalVO();

    String paymode =getValue(standardKitValidatorVO.getPaymentType());
    log.debug("Card type in commonPayment----" + getValue(standardKitValidatorVO.getCardType()));
    log.debug("vo----"+getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()));
    log.debug("session----" + session.getAttribute("merchantLogoName"));
    //String merchantLogo = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
    String merchantLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo());
    String partnerLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag());
    String checksum = getValue(standardKitValidatorVO.getTransDetailsVO().getChecksum());
    HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
    LinkedHashMap<String,TerminalVO> terminalMapLimitRouting = standardKitValidatorVO.getTerminalMapLimitRouting();
    String invoicenumber = getValue(standardKitValidatorVO.getInvoiceId());
    String attemptThreeD = "";
    attemptThreeD = getValue(standardKitValidatorVO.getAttemptThreeD());
    String isEmailWhiteListed = "";
    String accountId = "";
    String isCardDetailsRequired = "";
    String isCardWhitelisted = "";
    String whitelisting="";
    String cardLimitCheckTerminalLevel="";
    String cardAmountLimitCheckTerminalLevel="";
    String amountLimitCheckTerminalLevel="";
    String cardLimitCheckAccountLevel="";
    String cardAmountLimitCheckAccountLevel="";
    String amountLimitCheckAccountLevel="";

    String firstName = getValue(genericAddressDetailsVO.getFirstname());
    String lastName = getValue(genericAddressDetailsVO.getLastname());
    String dob = getValue(genericAddressDetailsVO.getBirthdate());

    if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getAccountId()))
        accountId = merchantDetailsVO.getAccountId();

    if (terminalVO != null && functions.isValueNull(terminalVO.getCardDetailRequired()))
    isCardDetailsRequired = terminalVO.getCardDetailRequired();

    if (terminalVO != null && functions.isValueNull(terminalVO.getIsEmailWhitelisted()))
        isEmailWhiteListed = terminalVO.getIsEmailWhitelisted();

    if (terminalVO != null && functions.isValueNull(terminalVO.getIsCardWhitelisted()))
        isCardWhitelisted = terminalVO.getIsCardWhitelisted();

    if (terminalVO != null && functions.isValueNull(terminalVO.getWhitelisting()))
        whitelisting = terminalVO.getWhitelisting();

    if (terminalVO != null && functions.isValueNull(terminalVO.getCardLimitCheckTerminalLevel()))
        cardLimitCheckTerminalLevel = terminalVO.getCardLimitCheckTerminalLevel();

    if (terminalVO != null && functions.isValueNull(terminalVO.getCardAmountLimitCheckTerminalLevel()))
        cardAmountLimitCheckTerminalLevel = terminalVO.getCardAmountLimitCheckTerminalLevel();

    if (terminalVO != null && functions.isValueNull(terminalVO.getAmountLimitCheckTerminalLevel()))
        amountLimitCheckTerminalLevel = terminalVO.getAmountLimitCheckTerminalLevel();

    if (terminalVO != null && functions.isValueNull(terminalVO.getCardLimitCheckAccountLevel()))
        cardLimitCheckAccountLevel = terminalVO.getCardLimitCheckAccountLevel();

    if (terminalVO != null && functions.isValueNull(terminalVO.getCardAmountLimitCheckAccountLevel()))
        cardAmountLimitCheckAccountLevel = terminalVO.getCardAmountLimitCheckAccountLevel();

    if (terminalVO != null && functions.isValueNull(terminalVO.getAmountLimitCheckAccountLevel()))
        amountLimitCheckAccountLevel = terminalVO.getAmountLimitCheckAccountLevel();

    //String supportSection = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportSection());

    session.setAttribute("paymentMap",paymentMap);
    session.setAttribute("terminalMapLimitRouting",terminalMapLimitRouting);

    /*if(merchantLogoFlag.equalsIgnoreCase("Y"))
    {
        merchantLogo = (String)session.getAttribute("merchantLogoName");
        log.debug("merchantLogo session----"+merchantLogo);
    }
    else
    {
        merchantLogo = standardKitValidatorVO.getMerchantDetailsVO().getLogoName();
        log.debug("merchantLogo vo----"+merchantLogo);
    }*/
    log.debug("common payment----" + genericAddressDetailsVO.getBirthdate());
    log.debug("common payment----" + genericAddressDetailsVO.getFirstname());
    Calendar cal=Calendar.getInstance();
    cal.setTimeInMillis(session.getLastAccessedTime());
%>
<script>
    function hideshowtab()
    {
            var x = document.getElementById('navtabdiv');
            if (x.style.display === 'none')
            {
                x.style.display = 'block';
            }
            else
            {
                x.style.display = 'none';
            }




    }
</script>
<style>
    .nav-tabs > li.active > a, .nav-tabs > li.active > a:focus {
        border: 0;
    <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;

    }
    .nav-tabs li a {
        margin-right: 0!important;
        border: 0;
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;

    }
    .creditly-wrapper .form-control:focus {
        border-color: #3e834b;
        outline: 0;
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,0.075), 0 0 8px rgba(102,175,233,0.6);
        box-shadow: inset 0 1px 1px rgba(0,0,0,0.075), 0 0 8px rgba(102,175,233,0.6);
    }
    .sterror {
        box-sizing: border-box;
        padding: 5px;
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
        height: auto;
        margin-left: 5px;
        margin-bottom: 8px;
       /* border: 1px solid #ebccd1;*/
        font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
        font-size: 12px;
        border-radius: 4px;
    }

</style>

<%--Start New Implemented--%>
<div class="container-fluid " id="boxsize">
    <div class="row">

        <div style="position: absolute;left: 45%;top: 0px;z-index: 60;width: auto;height:auto;/*background: lightgray; box-shadow: 0px 4px 6px lightgrey;*/padding: 0;"  id="logo">
            <%
                if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase
                ("Y"))
                {
            %>
            <br>
            <td width="10%" height="1">
                <%--<p align="center"><a href="http://www.PZ.com"><img border="0"
        height="30" src="/merchant/images/Pay_icon.png" width="65"></a></p>--%>
                <p align="center"><img border="0" id="imgheaderlogo" src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>"></p>

            </td>
            <%
                }
                else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
                {
            %>
            <br>
            <td width="10%" height="1">
                <%--<p align="center"><a href="http://www.Payment.com"><img border="0"
        height="30" src="/merchant/images/Pay_icon.png" width="65"></a></p>--%>
                <p align="center"><img border="0" id="imgheaderlogo" src="/images/merchant/<%=standardKitValidatorVO.getMerchantDetailsVO().getLogoName()%>"></p>
            </td>
            <%
                }
                else
                {
            %>
            <p align="center"><img border="0" id="imgheaderlogo" src="/images/merchant/<%=standardKitValidatorVO.getMerchantDetailsVO().getLogoName()%>"></p>
            <%
                }
            %>
        </div>

        <div id="sessionTimer">

        </div>
        <%
            String error="";
             error=(String)request.getAttribute("error");
            if( error!= null)
            {
            out.println("<div class=\"sterror\" id=\"sterror\">");
            out.println("You have <b>NOT FILLED</b>some of required details or some of details filled by you are incorrect.<br> Please fill following Invalid details completely before going for next step.");
            out.println(error);
            out.println("</div>");
            }
            Map paymentTypeMap = (HashMap)session.getAttribute("paymentMap");

        %>
        <div class="col-md-6 col-md-offset-3 mainbackgroundcolor headpanelfont_color" id="maindiv">

            <div id="tabid" class="col-md-12">

                <div class="tabs-left" style="margin-top: 0rem;">

                    <%
                        if(paymentTypeMap.size()>1)
                        {
                    %>

                    <ul class="nav nav-tabs mainbackgroundcolor navigation_font_color" id="navtabdiv" >
                        <%

                            int i=0;

                            log.debug("key map in commonPayment----"+paymentMap.keySet());
                            for(Object paymentId : paymentTypeMap.keySet())
                            {
                            i++;
                            String paymentType =
                            GatewayAccountService.getPaymentTypes(paymentId.toString());

                            if(i==1 && functions.isEmptyOrNull(paymode))
                            {

                        %>
                        <li class="active"><a href="#<%=paymentId%>" data-toggle="tab" ><img src="/images/transaction/paymenttype/<%=paymentType%>.png" class="icon_color">&nbsp;&nbsp;<span class="tabfontid"><%=rb.getString(paymentType)%></span></a></li>
                        <%
                            }
                            else if(paymentId.equals(paymode))
                            {
                        %>
                        <li class="active"><a href="#<%=paymentId%>" data-toggle="tab" ><img src="/images/transaction/paymenttype/<%=paymentType%>.png" class="icon_color">&nbsp;&nbsp;<span class="tabfontid"><%=rb.getString(paymentType)%></span></a></li>
                        <%
                            }
                            else
                            {
                        %>
                        <li><a href="#<%=paymentId%>" data-toggle="tab" ><img src="/images/transaction/paymenttype/<%=paymentType%>.png" class="icon_color">&nbsp;&nbsp;<span class="tabfontid"><%=rb.getString(paymentType)%></span></a></li>
                        <%
                            }

                            }

                        %>

                    </ul>

                    <%
                        }
                        else
                        {
                    %>
                        <input type="hidden" id="singlePayment" value="<%=paymentTypeMap.keySet().toArray()[0]%>">
                    <%
                        }
                    %>

                    <!--starting point (CreditCards) -->
                    <div id="myTabContent" class="tab-content" style="overflow: hidden;">
                        <%
                            int j=0;
                            for(Object paymentId : paymentTypeMap.keySet())
                            {
                            j++;
                            String paymentType = GatewayAccountService.getPaymentTypes(paymentId.toString());


                            if(j==1 && !functions.isValueNull(paymode))
                            {
                        %>
                        <div class="tab-pane active" id=<%=paymentId%>>
                            <%

                                }
                                else if( paymentId.equals( paymode))
                                {
                            %>
                            <div class="tab-pane active " id=<%=paymentId%>>
                                <%
                                    }
                                    else
                                    {
                                %>
                                <div class="tab-pane " id=<%=paymentId%>>
                                    <%
                                        }
                                    %>
                                    <button id="hideshowbutton" value="Menu" onclick="hideshowtab()" class="button-menu-mobile open-left">
                                        <i class="fa fa-bars" style="margin-left: -3px;"></i>
                                    </button>
                                    <h2 id="h2order" class="form foreground bodypanelfont_color panelbody_color">
                                    <%--<h2 id="h2order">--%>
                                        <jsp:include page="orderdetails.jsp"></jsp:include>
                                    </h2>
                                    <h4 id="h4payusing" class="headpanelfont_color" >Pay using <%=rb.getString(paymentType)%>

                                    </h4>


                                    <div>
                                        <%
                                            String cardId ="";
                                            String isChecked = "";
                                            List<String> cardList = (List<String>)paymentMap.get(paymentId);
                                            for (String cardTypeId : cardList)
                                            {
                                                String cardName = GatewayAccountService.getCardType(cardTypeId);
                                                cardId = cardTypeId;
                                                if(cardId.equals(standardKitValidatorVO.getCardType()) && paymentId.equals(paymode))
                                                {
                                                    isChecked = "checked";
                                                }
                                                else
                                                {
                                                    isChecked="";
                                                }
                                        %>

                                                <div class="col-xs-6 col-sm-2" id="ctype" >
                                                    <input type="radio" name="cardtype" id="radiocardtype"  value="<%=cardId%>" <%=isChecked%>>

                                                    <img src="/images/transaction/cardtype/<%=cardName%>.png">
                                                </div>

                                        <%
                                            }
                                        %>

                                        <%
                                            if(functions.isValueNull(childFile) && null!=standardKitValidatorVO.getPaymentType() && standardKitValidatorVO.getPaymentType().equals(paymentId) && functions.isValueNull(standardKitValidatorVO.getCardType()))
                                            {
                                        %>

                                            <br>

                                            <form action="/transaction/SingleCallPayment" method="post" name="myformname" style="margin-bottom: 0;">
                                                <input type="hidden" name="checksum" value="<%=checksum%>" />
                                                <input type="hidden" name="invoicenumber" value="<%=invoicenumber%>" />
                                                <input type="hidden" name="isEmailWhitelisted" value="<%=isEmailWhiteListed%>" />
                                                <input type="hidden" name="isCardWhitelisted" value="<%=isCardWhitelisted%>" />
                                                <input type="hidden" name="whitelisting" value="<%=whitelisting%>" />
                                                <input type="hidden" name="cardLimitCheckTerminalLevel" value="<%=cardLimitCheckTerminalLevel%>" />
                                                <input type="hidden" name="cardAmountLimitCheckTerminalLevel" value="<%=cardAmountLimitCheckTerminalLevel%>" />
                                                <input type="hidden" name="amountLimitCheckTerminalLevel" value="<%=amountLimitCheckTerminalLevel%>" />
                                                <input type="hidden" name="cardLimitCheckAccountLevel" value="<%=cardLimitCheckAccountLevel%>" />
                                                <input type="hidden" name="cardAmountLimitCheckAccountLevel" value="<%=cardAmountLimitCheckAccountLevel%>" />
                                                <input type="hidden" name="amountLimitCheckAccountLevel" value="<%=amountLimitCheckAccountLevel%>" />
                                                <input type="hidden" name="accountId" value="<%=accountId%>" />
                                                <input type="hidden" name="isCardDetailsRequired" value="<%=isCardDetailsRequired%>" />
                                                <input type="hidden" name="dateOfBirth" value="<%=dob%>">

                                                <jsp:include page="<%=childFile%>"></jsp:include>
                                                <jsp:include page="termsandcondition.jsp"></jsp:include>
                                            </form>

                                        <%
                                            }
                                        %>
                                    </div>
                                </div>

                                <%
                                    }
                                %>
                            </div> <!-- /tabbable -->
                        </div>

                    </div>

                    <!--ending point -->
                </div>

            </div>
        </div>

<%--        <div class="bodybackgroundcolor bodyforegroundcolor" id="row" >
            <div class="col-md-6 col-md-offset-3 mainbackgroundcolor headpanelfont_color">
                <jsp:include page="supportsection.jsp"></jsp:include>
            </div>
        </div>--%>

        <%--</form>--%>
        <form action="/transaction/sessionout.jsp" name="timeoutform" id="sessionout">
            <input type="hidden" name="ctoken" value="<%=ctoken%>">
            <input type="hidden" name="maxtime" value="<%=timetikcer%>">
            <input type="hidden" name="panelheading_color" value="<%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;">
            <input type="hidden" name="headpanelfont_color" value="<%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute
            ("headpanelfont_color").toString()+"!important":""%>">
            <input type="hidden" name="bodypanelfont_color" value="<%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute
            ("bodypanelfont_color").toString()+"!important":""%>;">
            <input type="hidden" name="panelbody_color" value="<%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>">
            <input type="hidden" name="mainbackgroundcolor" value="<%=session.getAttribute("mainbackgroundcolor")!=null?"background:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>">
        </form>
        <form action="/transaction/PayProcessController" method="post"
              name="myform" id="myform" <%--style="margin-bottom: -106px;"--%>>
            <input type="hidden" name="ctoken" value="<%=ctoken%>">
            <input type="hidden" name="memberId" value="<%=merchantDetailsVO.getMemberId()%>" />
            <input type="hidden" name="totype" value="<%=genericTransDetailsVO.getTotype()%>" />
            <input type="hidden" name="street" value="<%=genericAddressDetailsVO.getStreet()%>" />
            <input type="hidden" name="city" value="<%=genericAddressDetailsVO.getCity()%>" />
            <input type="hidden" name="postcode" value="<%=genericAddressDetailsVO.getZipCode()%>" />
            <input type="hidden" name="country" value="<%=genericAddressDetailsVO.getCountry()%>" />
            <input type="hidden" name="state" value="<%=genericAddressDetailsVO.getState()%>" />
            <input type="hidden" name="phone" value="<%=genericAddressDetailsVO.getPhone()%>" />
            <input type="hidden" name="telnocc" value="<%=genericAddressDetailsVO.getTelnocc()%>" />
            <input type="hidden" name="email" value="<%=ESAPI.encoder().encodeForHTML(genericAddressDetailsVO.getEmail())%>" />
            <input type="hidden" name="checksum" value="<%=genericTransDetailsVO.getChecksum()%>" />
            <input type="hidden" name="description" value="<%=genericTransDetailsVO.getOrderId()%>" />
            <input type="hidden" name="amount" value="<%=amount%>" />
            <input type="hidden" name="TMPL_AMOUNT" value="<%=genericAddressDetailsVO.getTmpl_amount()%>" />
            <input type="hidden" name="currency" value="<%=genericTransDetailsVO.getCurrency()%>" />
            <input type="hidden" name="TMPL_CURRENCY" value="<%=genericAddressDetailsVO.getTmpl_currency()%>" />
            <input type="hidden" name="redirecturl" value="<%=genericTransDetailsVO.getRedirectUrl()%>" />
            <input type="hidden" name="notificationUrl" value="<%=genericTransDetailsVO.getNotificationUrl()%>" />
            <input type="hidden" name="paymenttype" id="paymenttype" value=""                    />
            <input type="hidden" name="ip" value="<%=genericAddressDetailsVO.getIp()%>" />
            <input type="hidden" name="isProcessed" value="t" />
            <input type="hidden" name="cardtype" id="cardType" value="" />
            <input type="hidden" name="orderdescription" value="<%=genericTransDetailsVO.getOrderDesc()%>" />
            <input type="hidden" name="version" value="<%=standardKitValidatorVO.getVersion()%>" />
            <input type="hidden" name="trackingid" value="<%=standardKitValidatorVO.getTrackingid()%>" />
            <input type="hidden" name="invoicenumber" value="<%=invoicenumber%>" />
            <input type="hidden" name="attemptThreeD" value="<%=attemptThreeD%>" />
            <input type="hidden" name="isEmailWhitelisted" value="<%=isEmailWhiteListed%>" />
            <input type="hidden" name="isCardWhitelisted" value="<%=isCardWhitelisted%>" />
            <input type="hidden" name="whitelisting" value="<%=whitelisting%>" />
            <input type="hidden" name="accountId" value="<%=accountId%>" />
            <input type="hidden" name="isCardDetailsRequired" value="<%=isCardDetailsRequired%>" />

            <input type="hidden" name="customerId" value="<%=standardKitValidatorVO.getCustomerId()%>" />
            <input type="hidden" name="customerid" value="<%=standardKitValidatorVO.getCustomerId()%>" />
            <input type="hidden" name="customerBankId" value="<%=standardKitValidatorVO.getCustomerBankId()%>" />
            <input type="hidden" name="firstName" value="<%=firstName%>">
            <input type="hidden" name="lastName" value="<%=lastName%>">
            <input type="hidden" name="dateOfBirth" value="<%=dob%>">
        </form>
            <%
            //String tlscFileName = "";
            if(genericTransDetailsVO!=null && functions.isValueNull(genericTransDetailsVO.getTotype()) && genericTransDetailsVO.getTotype().equalsIgnoreCase("paynetics"))
            {
              //tlscFileName = genericTransDetailsVO.getTotype().toLowerCase()+"tlsc.jsp";
            %>
                <jsp:include page="payneticstlsc.jsp"></jsp:include>
            <%
            }
        %>
</body>
</html>
<div class="row bodybackgroundcolor bodyforegroundcolor" id="row"  >
    <div class="col-md-6 col-md-offset-3" id="footerid">
        <jsp:include page="footerlogo.jsp"></jsp:include>
    </div>
</div>
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