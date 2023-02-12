<%@ page import="com.manager.PartnerModuleManager" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.manager.MerchantConfigManager" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.PartnerManager" %>
<%--
  Created by IntelliJ IDEA.
  User: sanjay
  Date: 2/24/2022
  Time: 4:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private static Logger log = new Logger("checkoutTransaction.jsp");
%>

<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    Functions functions = new Functions();
    PartnerFunctions partnerFunctions = new PartnerFunctions();

    Map<String, Object> merchantTemplateSetting = null;
    Map<String, Object> partnerTemplateSetting = null;
    if (request.getAttribute("merchantTemplateSetting") != null)
    {
        merchantTemplateSetting = (Map<String, Object>) request.getAttribute("merchantTemplateSetting");
    }
    if (request.getAttribute("partnerTemplateSetting") != null)
    {
        partnerTemplateSetting = (Map<String, Object>) request.getAttribute("partnerTemplateSetting");
    }
    MerchantDAO merchantDAO=new MerchantDAO();
    String partnerid = session.getAttribute("partnerId").toString();


%>
<html>
<head>
    <title><%=company%> Merchant Settings> Checkout Configuration </title>
    <style type="text/css">
        @media (max-width: 640px) {
            #smalltable {
                width: 100% !important;
            }
        }

        @media (min-width: 641px) {
            #flightid {
                width: inherit;
            }
        }

        .modal
        {
            position: relative !important;
            z-index:0;
            height:650px;
        }
        @media (min-width: 641px) {
            #flightid {
                width: inherit;
            }
        }
        .content-page > .content
        {
            margin-top: 35px !important;
        }

        .tabs-li-wallet{       /* payment type options border color */

        <%=session.getAttribute("panelbody_color")!=null?"border-right: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        <%=session.getAttribute("panelbody_color")!=null?"border-bottom: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        }

        .form-header{          /* form header label color(Personal,Address,Card)  */
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
        }

        .has-float-label label, .has-float-label>span{            /* input control label font color */
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
        }



        .selectedBank{ /*  selected bank option background and font color */
        <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        <%--<%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;--%>
        }

        .selectedBank .bank-label{
        <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
        }

        .wallet-title{
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString():""%>;
        }

        .list :checked+label { /* wallet selected option background color */
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
        }

        .list :checked+label .wallet-title{
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
        }


        .merchant-details{
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
            border: none !important;
            margin: 0px;
            padding: 4px;
        }

        .emi{
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
        }

        /* loader */
        .loader , .loader-edge{                    /* loader color*/
        <%=session.getAttribute("textbox_color")!=null?" color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
            opacity: .8;
        }

        .check_mark , .sa-icon.sa-success::before, .sa-icon.sa-success::after , .sa-fix{       /* success ( animation background color ) */
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        }
        /* end of loader */

        /* end of footer */
    </style>
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>

    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

</head>

<body class="bodybackground">
<div class="content-page">
    <div class="content">
        <div class="page-heading"  style="background-color: #ffffff">
            <%--logo satart--%>
            <div class="pull-right">
                <div class="btn-group" style="z-index:8">
                    <form action="/partner/addPartnerLogo.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" value="checkout_configuration" name="submit" name="B1"
                                style="background: grey;border: 0;width: 182px;height: 45px;color: whitesmoke;font-size: 15;padding-left: 15px;border-radius: 36px;"><i style="font-size: 13px;" class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;Add Merchant Logo
                        </button>
                    </form>
                </div>
            </div>

            <%--logo end--%>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget" style="margin-bottom: 0px !important;">
                        <div class="widget-header transparent" style="position: absolute;top:-60px;">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Checkout Configuration
                            </strong>
                            </h2>
                        </div>
                        <% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            session.setAttribute("submit", "checkout_configuration");

                            if (partner.isLoggedInPartner(session))
                            {
                                // LinkedHashMap memberidDetails= partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
                                String memberid = nullToStr(request.getParameter("memberid"));
                                String pid = nullToStr(request.getParameter("pid"));
                                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                String Config = null;
                                if (Roles.contains("superpartner"))
                                {

                                }
                                else
                                {
                                    pid = String.valueOf(session.getAttribute("merchantid"));
                                    Config = "readonly";
                                }
                        %>
                        <form action="/partner/net/CheckoutProcess?ctoken=<%=ctoken%>" method="post" name="F1"
                              onsubmit="" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <div class="widget-content padding" style="padding:0px !important;">
                                <div id="horizontal-form">
                                    <%
                                        String errormsg = (String) request.getAttribute("cbmessage");
                                        String errormsg1 = (String) request.getAttribute("error");
                                        String error = (String) request.getAttribute("errormessage");
                                        if (partnerFunctions.isValueNull(errormsg1))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                        }
                                    %>
                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label">Partner ID
                                        </label>

                                        <div class="col-sm-8">
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control"
                                                   autocomplete="on" <%=Config%>>
                                            <input name="pid" type="hidden" value="<%=pid%>">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label">Merchant ID
                                        </label>

                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>"
                                                   class="form-control"
                                                   autocomplete="on">
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>


                <%-- Starting checkout template --%>

                <form action="/partner/net/CheckoutConfiguration?ctoken=<%=ctoken%>" method="post" id="myformnameSave"
                      name="myformnameSave">
                    <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">

                        <%
                Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
                Hashtable tempHash = null;
                String memberId=null;
                int records = 0;

                       if(hash!=null && hash.size()>0)
                    {
                        try{
                            records = Integer.parseInt((String) hash.get("records"));
                            System.out.println("records ::::"+records);
                           }
                        catch(Exception ex){
                            log.error("Records & TotalRecords is found null",ex);
                        }
                    }
                    if(records > 0)
                    {
                    String style = "td1";
                    for (int pos = 1; pos <= records; pos++)
                    {
                        String id = Integer.toString(pos);

                        if (pos % 2 == 0)
                            style = "tr0";
                        else
                            style = "tr0";
                        tempHash = (Hashtable) hash.get(id);
                        memberId= (String) tempHash.get("memberid");
                        PartnerManager partnerManager=new PartnerManager();
                        MerchantDetailsVO merchantDetailsVO1= merchantDAO.getMemberAndPartnerDetails(memberId,(String) session.getAttribute("partnername"));
                        PartnerDetailsVO partnerDetailsVOs= partnerManager.getselfPartnerDetails(partnerid);
                        String partnertemplateValue = "";
                        if(partnerDetailsVOs != null)
                        {
                            partnertemplateValue = partnerDetailsVOs.getTemplate();
                        }


                        Hashtable hashtable =merchantDAO.getMemberTemplateDetails(memberId);
                        String isMerchantLogoName=(String)hashtable.get(memberId);
                        String merchantLogoFlag ="";
                        String consentFlag ="";
                        String checkoutTimerFlag ="";
                        String checkoutTimerTime ="";
                        String supportSection ="";
                        String supportNoNeeded ="";
                        String address ="";
                        String email ="";
                        String partnerLogoFlag ="";
                        String personalInfoDisplay ="";
                        String cardExpiryDateCheck="";
                        String telNo = "";
                        if (merchantDetailsVO1!= null)
                        {
                            merchantLogoFlag = merchantDetailsVO1.getMerchantLogo();

                            consentFlag = merchantDetailsVO1.getConsentFlag();
                            checkoutTimerFlag = merchantDetailsVO1.getCheckoutTimerFlag();
                            checkoutTimerTime = merchantDetailsVO1.getCheckoutTimerTime();
                            supportSection = merchantDetailsVO1.getSupportSection();
                            supportNoNeeded = merchantDetailsVO1.getSupportNoNeeded();
                            address = merchantDetailsVO1.getAddress() + " " + merchantDetailsVO1.getState() + " " +
                                    merchantDetailsVO1.getCountry() + " " + merchantDetailsVO1.getZip();

                            email = merchantDetailsVO1.getContact_emails();
                            partnerLogoFlag = merchantDetailsVO1.getPartnerLogoFlag();
                            cardExpiryDateCheck = merchantDetailsVO1.getCardExpiryDateCheck();
                            personalInfoDisplay = merchantDetailsVO1.getPersonalInfoDisplay();
                        }

            %>
                    <input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>">

                  <%--  <div class="row reporttable">
                        <div class="col-md-12">
                            <div class="widget">
                                <div class="widget-header">
                                    <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Checkout Template
                                        Configuration</strong>
                                    </h2>

                                    <div class="additional-btn">
                                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                    </div>
                                </div>
                                <%
                                    PartnerModuleManager partnerModuleManager = new PartnerModuleManager();
                                    String Hide = "true";
                                    String superAdminId = partnerModuleManager.getSuperAdminId(String.valueOf(session.getAttribute("merchantid")));

                                %>
                                <center>
                                   &lt;%&ndash; <div class="widget-content padding" style="overflow-x: auto;">

                                    </div>&ndash;%&gt;
                                </center>
                            </div>
                        </div>
                    </div>--%>

            <%--New checkout template color start--%>
            <div class="row reporttable" style="background: #fff;">
                <div class="col-md-6">
                    <div class="widget"  style="/*margin-top: -38px;*/width:109%">
                        <%
                            String style1 = "td1";
                            String error1 = (String) request.getAttribute("errormessage");
                            if (functions.isValueNull(error))
                            {
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                            }

                        %>
                       <%-- <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Checkout Template Colors</strong>
                            </h2>


                        </div>--%>
                        <center>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 90%;">
                                    <thead>
                                    <tr>
                                        <th colspan="2" style="font-size: 14px;text-align: center;color: #fff;">Header Colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <!----new--->
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b><%--Header&nbsp;--%>Font
                                                &nbsp;Color
                                            </b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%--Header&nbsp;--%>BackGround
                                                &nbsp;Color
                                            </b></td>
                                    </tr>
                                    </thead>

                                    <tbody>

                                    <tr>
                                        <td valign="middle" data-label="Header Font Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):""%>"/>
                                        <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):"#ffffff"%>">
                                        <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>" value="#ffffff">
                                        <input type="hidden" id="partnertemplateValue" value="<%=partnertemplateValue%>">
                                        </td>

                                        <td valign="middle" data-label="Header Background Color" align="center"
                                            class="<%=style%>">
                                      <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>'
                                                   class="form-control"  onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):"#7eccad"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>" value="#7eccad">

                                        </td>
                                    </tr>

                                    </tbody>
                                    <thead>
                                    <tr>
                                        <th colspan="2"  style="font-size: 14px;text-align: center;color: #fff;">Navigation Bar Colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b><%--Navigation&nbsp;Bar&nbsp;--%>Font&nbsp;Color
                                            </b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <%--<b>Navigation&nbsp;Bar
                                            </b>--%> <b><%--Header&nbsp;--%>BackGround
                                            &nbsp;Color
                                        </b></td>

                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Navigation Bar Font Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):"#000000"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>" value="#000000">

                                        </td>
                                        <td valign="middle" data-label="Navigation Bar" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):"#7eccad"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>" value="#7eccad">

                                        </td>

                                    </tr>
                                    </tbody>

                                    <tbody>
                                    <thead>
                                    <tr>
                                        <th colspan="2"  style="font-size: 14px;text-align: center;color: #fff;">Body Colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b>Font&nbsp;Color
                                            </b></td>

                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b>Body&nbsp;&nbsp;Color
                                            </b></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Label Font Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):"#000000"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>" value="#000000">

                                        </td>

                                        <td valign="middle" data-label="Body & Footer Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):"#000000"%>"></span>
                                            <input type="text" class="form-control"
                                                   id='<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>' onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):"#ffffff"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>" value="#ffffff">

                                        </td>

                                    </tr>
                                    </tbody>
                                    <thead>
                                    <tr>
                                        <th colspan="2" style="font-size: 14px;text-align: center;color: #fff;">Button Colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b><%--Button&nbsp;--%>Font
                                                &nbsp;Color
                                            </b></td>

                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <%-- <b>Button&nbsp;Color
                                             </b>--%><b><%--Header&nbsp;--%>BackGround
                                            &nbsp;Color
                                        </b></td>
                                    </tr>
                                    </tbody>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Button Font Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):"#000000"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>" value="#000000">

                                        </td>
                                        <td valign="middle" data-label="Button Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):"#ffffff"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>" value="#ffffff">

                                        </td>

                                    </tr>
                                    </tbody>
                                    <%------For footer colors--%>

                                    <thead>
                                    <tr>
                                        <th colspan="2" style="font-size: 14px;text-align: center;color: #fff;">Footer Colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b><%--Button&nbsp;--%>Font
                                                &nbsp;Color
                                            </b></td>

                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <%-- <b>Button&nbsp;Color
                                             </b>--%><b><%--Header&nbsp;--%>BackGround
                                            &nbsp;Color
                                        </b></td>
                                    </tr>
                                    </tbody>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Button Font Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()):"#000000"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>" value="#000000">

                                        </td>
                                        <td valign="middle" data-label="Button Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()):"#7eccad"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>" value="#7eccad">

                                        </td>

                                    </tr>
                                    </tbody>
                                    <tbody>
                                    <thead>
                                    <tr>
                                        <th colspan="2" style="font-size: 14px;text-align: center;color: #fff;">Background Colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Box&nbsp;Shadow
                                            </b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Full Background Color
                                            </b></td>
                                    </tr>
                                    </thead>
                                    </tbody>
                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Label Font Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;">
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):""%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>" value="rgba(0, 0, 0, 0.5)">

                                        </td>
                                        <td valign="middle" data-label="Full Background Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):"#000000"%>"></span>
                                            <input type="text" class="form-control"
                                                   id='<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>' onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):"#f3f3f3"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>" value="#f3f3f3">

                                        </td>

                                    </tr>
                                    </tbody>
                                    <tbody>
                                    <thead>
                                    <tr>
                                        <th colspan="2"  style="font-size: 14px;text-align: center;color: #fff;">Timer & Icon colors</th>
                                    </tr>
                                    </thead>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;border-right:1px solid #ddd;">
                                            <b>Timer&nbsp;Font Color
                                            </b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #fff;color:#000000;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Icon&nbsp;Color
                                            </b></td>

                                    </tr>
                                    </thead>
                                    </tbody>
                                    <tbody>
                                    <tr>
                                        <td valign="middle" data-label="Timer Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()).toString()) )?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):"#000000"%>"></span>
                                            <input type="text" class="form-control"
                                                   id='<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>'  onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):"#000000"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>" value="#000000">

                                        </td>
                                        <td valign="middle" data-label="Icon Color" align="center"
                                            class="<%=style%>">
                                        <span class="input-icon" style="width: 40%;position: absolute;";>
                                            <input type="color"
                                                   style="position: absolute;width: 30px;padding-left: -166px;height: 32px;margin-left: -25px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):"#000000"%>"></span>
                                            <input id='<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>'
                                                   name='<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>'
                                                   class="form-control" onblur="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>','<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>_picker');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):""%>"/>
                                            <input type="hidden" id="partner_<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>" value="<%=(partnerTemplateSetting!=null && partnerTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()))?partnerTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):"#7eccad"%>">
                                            <input type="hidden" id="default_<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>" value="#7eccad">

                                        </td>

                                    </tr>
                                    </tbody>
                                </table>


                                <%--<BR>--%>
                                <%--<BR>--%>
                            </div>
                        </center>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="widget">

                        <%--<div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Checkout Page</strong>
                            </h2>
                        </div>
--%>
                        <%--<html><head>
                            <meta charset="utf-8">
                            <title></title>--%>
                        <%--  <base href="/">--%>
                        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
                        <meta http-equiv="cache-control" content="no-cache">
                        <meta http-equiv="expires" content="0">
                        <meta http-equiv="pragma" content="no-cache">
                        <!-- <link rel="icon" type="image/x-icon" href="favicon.ico"> -->
                        <link rel="stylesheet" href="/merchant/transactionCSS/css/raleway-font.css" type="text/css">
                        <link rel="stylesheet" href="/merchant/transactionCSS/css/orbitron-font.css" type="text/css">
                        <link rel="stylesheet" href="/merchant/transactionCSS/css/montserrat-font.css" type="text/css">
                        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
                        <%-- <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-4.4.1/css/bootstrap.min.css">--%>
                        <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css">
                        <%--
                                                        <link rel="stylesheet" href="/merchant/transactionCSS/css/success.css">
                        --%>

                        <%--  <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css">--%>
                        <%-- <link rel="stylesheet" href="/merchant/transactionCSS/css/step-form.css">
                         <link rel="stylesheet" href="/merchant/transactionCSS/css/autocomplete.css">--%>
                        <style>

                            .mainDiv{
                            <%=request.getAttribute("box_shadow")!=null?"box-shadow:"+request.getAttribute("box_shadow").toString():""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BOX_SHADOW") != null ? "box-shadow:"+request.getAttribute("NEW_CHECKOUT_BOX_SHADOW").toString() : "" %>;
                            }

                            .background {
                            <%=request.getAttribute("bodybgcolor")!=null?"background:"+request.getAttribute("bodybgcolor").toString():""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR") != null ? "background:"+request.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString() : "" %>;
                            }

                            /* header */
                            .header{
                            <%=request.getAttribute("panelheading_color")!=null?"background-color:"+request.getAttribute("panelheading_color").toString()+"!important":""%>;
                            <%=request.getAttribute("bodypanelfont_color")!=null?"color:"+request.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR") != null ? "background-color:"+request.getAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR").toString() + "!important" : "" %>;
                            <%=request.getAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR").toString()+"!important":""%>;
                            }

                            .close {
                            <%=request.getAttribute("bodypanelfont_color")!=null?"color:"+request.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BUTTON_COLOR")!=null?"background-color:"+request.getAttribute("NEW_CHECKOUT_BUTTON_COLOR").toString()+"!important":""%>;
                            }

                            .logo{                /* background color of logo  */
                                background:transparent !important;
                                box-shadow: none !important;
                            }

                            .timeout{
                            <%=request.getAttribute("timer_color")!=null?"color:"+request.getAttribute("timer_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_TIMER_COLOR") != null ? "color:"+request.getAttribute("NEW_CHECKOUT_TIMER_COLOR").toString()+"!important":""%>;
                            }
                            /* end of header */

                            /* body */
                            .top-bar{               /* navigation bar background and font color */
                            <%=request.getAttribute("panelbody_color")!=null?"background-color:"+request.getAttribute("panelbody_color").toString()+"!important":""%>;
                            <%=request.getAttribute("navigation_font_color")!=null?"color:"+request.getAttribute("navigation_font_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR") != null ? "background-color:"+request.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR").toString() + "!important" : "" %>;
                            <%=request.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR").toString()+"!important":""%>;
                            }

                            .prev-btn{                /* form previous button on navigation bar */
                            <%=request.getAttribute("navigation_font_color")!=null?"color:"+request.getAttribute("navigation_font_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BUTTON_COLOR")!=null?"background-color:"+request.getAttribute("NEW_CHECKOUT_BUTTON_COLOR").toString()+"!important":""%>;
                            }

                            .footer-tab, .options , #personalinfo .form-control, #cardinfo .form-control{             /* home page background color */
                            <%=request.getAttribute("mainbackgroundcolor")!=null?"background-color:"+request.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR") != null ? "background-color:"+request.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR").toString() + "!important" : "" %>;
                            }

                            .options{               /* body background color */
                            <%--<%=request.getAttribute("mainbackgroundcolor")!=null?"background-color:"+request.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR")!=null?"background-color:"+request.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString()+"!important":""%>;--%>
                            }

                            .tab-icon{               /* home page icon color */
                            <%=request.getAttribute("icon_color")!=null?"color:"+request.getAttribute("icon_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_ICON_COLOR")!=null ? "color:"+request.getAttribute("NEW_CHECKOUT_ICON_COLOR").toString()+"!important" : ""%>;
                            }

                            .label-style , .bank-label {            /* payment type label font color*/
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR").toString()+"!important":""%>;
                            }

                            .tabs-li-wallet{       /* payment type options border color */
                            <%=request.getAttribute("panelbody_color")!=null?"border-right: 1px solid "+request.getAttribute("panelbody_color").toString()+"!important":""%>;
                            <%=request.getAttribute("panelbody_color")!=null?"border-bottom: 1px solid "+request.getAttribute("panelbody_color").toString()+"!important":""%>;
                            }

                            .form-header{          /* form header label color(Personal,Address,Card)  */
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString()+"!important":""%>;
                            }



                            .has-float-label label, .has-float-label>span{            /* input control label font color */
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString()+"!important":""%>;
                            }

                            .form-label , .terms-checkbox{            /* for labels as well as terms & condition*/
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR").toString()+"!important":""%>;
                            }

                            .selectedBank{ /*  selected bank option background and font color */
                            <%=request.getAttribute("panelbody_color")!=null?"background-color:"+request.getAttribute("panelbody_color").toString()+"!important":""%>;
                            <%--<%=request.getAttribute("navigation_font_color")!=null?"color:"+request.getAttribute("navigation_font_color").toString()+"!important":""%>;--%>
                            }

                            .selectedBank .bank-label{
                            <%=request.getAttribute("navigation_font_color")!=null?"color:"+request.getAttribute("navigation_font_color").toString()+"!important":""%>;
                            }

                            .wallet-title{
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString():""%>;
                            }

                            .list :checked+label { /* wallet selected option background color */
                            <%=request.getAttribute("textbox_color")!=null?"background-color:"+request.getAttribute("textbox_color").toString()+"!important":""%>;
                            <%=request.getAttribute("headpanelfont_color")!=null?"color:"+request.getAttribute("headpanelfont_color").toString()+"!important":""%>;
                            }

                            .list :checked+label .wallet-title{
                            <%=request.getAttribute("headpanelfont_color")!=null?"color:"+request.getAttribute("headpanelfont_color").toString()+"!important":""%>;
                            }

                            .pay-button{             /* pay button background and font color */
                            <%=request.getAttribute("textbox_color")!=null?"background-color:"+request.getAttribute("textbox_color").toString()+"!important":""%>;
                            <%=request.getAttribute("headpanelfont_color")!=null?"color:"+request.getAttribute("headpanelfont_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_BUTTON_COLOR")!=null?"background-color:"+request.getAttribute("NEW_CHECKOUT_BUTTON_COLOR").toString()+"!important":""%>;
                            }

                            .merchant-details{
                            <%=request.getAttribute("textbox_color")!=null?"background-color:"+request.getAttribute("textbox_color").toString()+"!important":""%>;
                            <%=request.getAttribute("headpanelfont_color")!=null?"color:"+request.getAttribute("headpanelfont_color").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR")!=null?"background-color:"+request.getAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR").toString()+"!important":"background-color:#7eccad!important"%>;
                            <%=request.getAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR").toString()+"!important":""%>;
                                border: none !important;
                                margin: 0px;
                                padding: 4px;
                            }

                            .emi{
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString()+"!important":""%>;
                            }

                            /* loader */
                            .loader , .loader-edge{                    /* loader color*/
                            <%=request.getAttribute("textbox_color")!=null?" color:"+request.getAttribute("textbox_color").toString()+"!important":""%>;
                                opacity: .8;
                            }

                            .check_mark , .sa-icon.sa-success::before, .sa-icon.sa-success::after , .sa-fix{       /* success ( animation background color ) */
                            <%=request.getAttribute("mainbackgroundcolor")!=null?"background-color:"+request.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
                            }
                            /* end of loader */

                            .error_body{
                            <%=request.getAttribute("mainbackgroundcolor")!=null?"background-color:"+request.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
                            <%=request.getAttribute("bodyfgcolor")!=null?"color:"+request.getAttribute("bodyfgcolor").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR") != null ? "NEW_CHECKOUT_FULLBACKGROUND_COLOR:"+request.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString() : "" %>;
                            }
                            /* end of body */

                            /* footer */
                            .modal-footer {
                            <%=request.getAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR")!=null?"color:"+request.getAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR").toString()+"!important":""%>;
                            <%=request.getAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR")!=null?"background-color:"+request.getAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR").toString()+"!important":"background-color:#7eccad!important"%>;
                            }

                            .formcredit{
                                display: none;
                            }
                            .prev-btn
                            {
                                background-color: #7eccad;
                                color: #ffffff;
                            }
                            .widget table tr th, .widget table tr td
                            {
                                padding-left:0px !important;
                                padding-right:0px !important;
                                width:50%;
                            }
                            .table>thead>tr>td, .table>tbody>tr>td
                            {
                                padding: 0px !important;
                            }
                            .table>thead>tr>th
                            {
                                height:33px;
                            }
                            .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td
                            {
                                padding: 0px !important;
                            }
                            .modal {
                                overflow: auto
                            }


                            .modal-content{
                                border: none !important;
                                background-color: transparent !important;
                            }



                            .modal-body{
                                position: relative;
                                -ms-flex: 1 1 auto;
                                flex: 1 1 auto;
                                padding:0rem !important;
                                transition: .2s;
                                background-color: white
                            }
                            .merchant-details{
                                background-color: #3583d2;
                                color: white;
                                border: none !important;
                                margin: 0px;
                                padding: 4px;
                                text-align:center ;
                            }
                            .modal-footer {
                                background-color: #fff;
                                width: 100%;
                                padding: 7px 12px;
                                display: inline-flex;
                                flex-wrap: nowrap;
                                border: 0 !important;
                                border-radius: 0 !important;
                            }

                            /* CANCEL modal styles */

                            .modal.show .modal-dialog-cancel {
                                -webkit-transform: translate(0,0);
                                transform: translate(0,0);
                            }

                            .modal-dialog-cancel {
                                position: relative;
                                width: auto;
                                max-width: 250px;
                                /*margin: 1.75rem 3rem;*/
                                margin: 1.75rem auto;
                                pointer-events: none;
                            }

                            .modal-content-cancel {
                                position: relative;
                                display: -ms-flexbox;
                                display: flex;
                                -ms-flex-direction: column;
                                flex-direction: column;
                                width: 100%;
                                pointer-events: auto;
                                background-clip: padding-box;
                                border-radius: .3rem;
                                outline: 0;
                                border: none !important;
                                background-color: #f3f3f3 !important;
                                box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5);
                            }

                            .modal-header-cancel {
                                display: -ms-flexbox;
                                display: flex;
                                -ms-flex-align: start;
                                align-items: flex-start;
                                -ms-flex-pack: justify;
                                justify-content: space-between;
                                padding: 0.5rem 1rem;
                                border-top-left-radius: .3rem;
                                border-top-right-radius: .3rem;
                            }

                            .modal-title-cancel{
                                font-weight: bold;
                                font-size: 20px;
                            }

                            .modal-body-cancel {
                                position: relative;
                                -ms-flex: 1 1 auto;
                                flex: 1 1 auto;
                                padding: 0rem 1rem;
                                font-size: 14px;
                            }

                            .modal-footer-cancel{
                                padding: 0.5rem 1rem;
                                text-align: right;
                            }

                            .cancel-modal-button{
                                padding: .5rem;
                                background-color: transparent;
                                border: none;
                                color: #7795cb;
                                font-weight: bold;
                                font-size: 13px;
                            }

                            /*end of CANCEL modal styles*/
                            .mainDiv{
                                /*width: 345px;*/
                                width: 400px;
                                position: relative;
                                right: 30px;
                                box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5);
                            }


                            .header{
                                background-color: #7eccad;
                                /*border-radius: 3px 3px 0 0;*/
                                color: #fff;
                                padding: 15px;
                                overflow: hidden;
                                max-height: 128px;
                                position: relative;
                                z-index: 3;
                            }

                            .header::before{
                                content: "";
                                left: 0;
                                right: 0;
                                bottom: 0;
                                top: 0;
                                position: absolute;
                                /*background-image: -webkit-gradient(linear,left top, right bottom,from(rgba(255,255,255,0.2)),to(rgba(0,0,0,0.2)));*/
                                /*background-image: -webkit-linear-gradient(top left,rgba(255,255,255,0.2),rgba(0,0,0,0.2));*/
                                /*background-image: -o-linear-gradient(top left,rgba(255,255,255,0.2),rgba(0,0,0,0.2));*/
                                /*background-image: linear-gradient(to bottom right,rgba(255,255,255,0.2),rgba(0,0,0,0.2));*/
                            }


                            .logo{
                                text-align: center;
                                position: relative;
                                padding: 0px 8px;
                                width: 147px;
                                background: #fff;
                                -webkit-border-radius: 3px;
                                border-radius: 3px;
                                line-height: 79px;
                                float: left;
                                margin-right: 24px;
                                -webkit-box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                            }

                            .name{
                                white-space: nowrap;
                                position: relative;
                                text-shadow: 0 1px 3px rgba(0,0,0,0.12);
                            }


                            .client-name{
                                text-overflow: ellipsis;
                                overflow: hidden;
                                font-size: 17px;
                            }

                            .client-order{
                                font-size: 14px;
                                -o-text-overflow: ellipsis;
                                text-overflow: ellipsis;
                                overflow: hidden;
                                opacity: .8;
                            }

                            .client-amount{
                                font-size: 18px;
                            }

                            .tool .tooltiptext {
                                visibility: hidden;
                                /*width: 156px;*/
                                max-width: 100%;
                                height: auto;
                                white-space: normal;
                                text-overflow: unset;
                                word-wrap: break-word;
                                background-color: black;
                                color: #fff;
                                border-radius: 6px;
                                position: absolute;
                                line-height: 15px;
                                padding: 8px 8px;
                                font-size: 14px;
                                z-index: 99999;
                                right: 0;
                            }

                            .tool:hover .tooltiptext {
                                visibility: visible;
                            }

                            .tooltiptext::after {
                                content: "";
                                position: absolute;
                                width: 0;
                                height: 0;
                                border-width: 5px;
                                border-style: solid;
                                border-color: transparent transparent #000000;
                                bottom: 100%;
                                /* left: 20px; */
                                margin: 0 0 0px -9px;
                                /*margin: 0 0 -1px -10px;*/
                            }

                            .tooltiptextOrderId {
                                visibility: hidden;
                                /*width: 156px;*/
                                max-width: 100%;
                                height: auto;
                                white-space: normal;
                                text-overflow: unset;
                                word-wrap: break-word;
                                background-color: black;
                                color: #fff;
                                border-radius: 6px;
                                position: absolute;
                                line-height: 15px;
                                padding: 8px 8px;
                                font-size: 12px;
                                z-index: 99999;
                                right: 0;
                                /*top: 62px;*/
                                /*left: 179px;*/
                            }

                            .tooltiptextOrderId::after {
                                content: "";
                                position: absolute;
                                width: 0;
                                height: 0;
                                border-width: 5px;
                                border-style: solid;
                                border-color: transparent transparent #000000;
                                bottom: 100%;
                                /*left: 20px;*/
                                margin: 0 0 -1px -10px;
                            }
                            .close {
                                position: absolute;
                                right: 0;
                                top: 0;
                                cursor: pointer;
                                font-size: 18px;
                                opacity: .6;
                                width: 24px;
                                text-align: center;
                                line-height: 26px;
                                z-index: 9999;
                                color: #fff;
                            }

                            .timeout{
                                position: absolute;
                                font-size: 12px;
                                bottom: 0;
                                right: 6px;
                            }




                            .top-bar{
                                display: block;
                                background: #fcfbfa;
                                line-height: 33px;
                                /*border-bottom: 1px solid #e6e7e8;*/
                                -webkit-transition: .3s transform ease-in 0s,.3s opacity ease-in 0s,.1s color;
                                transition: .3s transform ease-in 0s,.3s opacity ease-in 0s,.1s color;
                                color: #757575;
                                height: 41px;
                                z-index: 2;
                            }

                            .top-right{
                                height: 100%;
                                float: right;
                                font-size: 12pt;
                            }

                            .top-left{
                                font-size: 17px;
                            }

                            .top-right , .top-left{
                                padding: 3px 24px;
                                -webkit-transition: .2s;
                                -o-transition: .2s;
                                transition: .2s;
                            }

                            /*  start of loader  */
                            .loader {
                                color: #3675b4;
                                font-size: 90px;
                                text-indent: -9999em;
                                overflow: hidden;
                                width: 1em;
                                height: 1em;
                                border-radius: 50%;
                                margin: 72px auto;
                                position: relative;
                                -webkit-transform: translateZ(0);
                                -ms-transform: translateZ(0);
                                transform: translateZ(0);
                                -webkit-animation: load6 1.7s infinite ease, round 1.7s infinite ease;
                                animation: load6 1.7s infinite ease, round 1.7s infinite ease;
                            }

                            .truncate{
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                            }
                            .footer-tab{
                                height: 305px;
                            }
                            .label-style{
                                text-transform: capitalize;
                                line-height: 1em;
                                padding: 19px 3px;
                                font-size: 11pt;
                            }
                            .widget .nav-tabs > li > a i
                            {
                                color: none;
                            }
                            .widget .nav-tabs > li > a
                            {
                                background-color: transparent !important;
                            }
                            .tab-icon {
                                color: #3583D2;
                                font-size: 42px;
                            }

                            .nav-tabs{
                                border: none;
                            }


                            .tabs-li-bank{
                                width: 33.333333%;
                                height: 86px !important;
                                text-align: center;
                                font-size: 11px;
                                padding: 15px 0px;
                                cursor: pointer;
                                border-right: 1px solid #e6e7e8;
                                border-bottom: 1px solid #e6e7e8;
                            }

                            .tabs-li-bank > a {
                                color:black !important;
                            }

                            .tabs-li{
                                width: 33.333333%;
                                height: 100px;
                                text-align: center;
                                font-size: 13px;
                                padding: 6px;
                                cursor: pointer;
                            }

                            .tabs-li > a {
                                color:black !important;
                            }


                            .tabs-li-wallet1{
                                width: 100% !important;
                                padding: 20px 16px !important;
                            }

                            .tabs-li-image{
                                float:left !important;
                            }

                            .tabs-li-label{
                                padding: 20px !important;
                            }

                            .tabs-li-wallet{
                                width: 33.333333%;
                                height: 96px;
                                text-align: center;
                                font-size: 11px;
                                padding: 5px 0px;
                                cursor: pointer;
                                border-right: 1px solid #e6e7e8;
                                border-bottom: 1px solid #e6e7e8;
                            }

                            .tabs-li-wallet > a {
                                color:black !important;
                            }

                            .background {
                                background:#fff;
                            ;
                            }
                            .pay-btn{
                                position: absolute;
                                width: 100%;
                                bottom: 10px;
                                color: white;
                                text-align: center;
                                border-radius: 0 0 3px 3px;
                                height: 34px;
                                line-height: 34px;
                                -webkit-transition: .3s transform,.3s opacity;
                                -o-transition: .3s transform,.3s opacity;
                                transition: .3s transform,.3s opacity;
                                /*-ms-transform: translateY(-100%);*/
                                font-size: 18px;
                                cursor: pointer;
                            }

                            .disabledbutton{
                                pointer-events: none;
                                /*opacity: 0.6;*/
                            }

                            .pay-button{
                                /*margin: 0px 10px;*/
                                margin: auto;
                                text-align: center;
                                width: 94%;
                                padding: 0px;
                                font-size: 18px;
                                border-radius: 4px;
                                border:none;
                                background-color:#7eccad;
                                color:#fff;
                            }

                            .footer-logo{
                                text-align: right;
                                width: 100%;
                                height: 35px;
                                margin: 0px;
                                line-height: 35px;
                            }

                            .footer-details{
                                font-size: 12px;
                            }

                            .logo-style{
                                max-width: 90px;
                                max-height:100%;
                               /* padding-top:10px;*/
                            }


                            .form-group{
                                height: 42px;
                            }

                            .form-header{
                                color: black;
                                margin: 0px 0px 10px 0px;
                            }

                            .form-control:focus {
                                outline: 0 !important;
                                box-shadow: none !important;
                            }

                            .input-control1{
                                box-shadow: none !important;
                                padding: 9px 0px 8px 0px!important;
                                border: none !important;
                                border-bottom: 1px solid #ced4da !important;
                                border-radius: 0px !important;
                                width: 99% !important;
                                font-size: 15px;
                            }

                            .input-control1-date{
                                margin-left: 10px !important;
                                padding: 6px 0px 2px 0px !important;
                                border: none !important;
                                border-bottom: 1px solid #ced4da !important;
                                border-radius: 0px !important;
                                width: 90% !important;
                                font-size: 15px;
                            }


                            .control-group-full{
                                width: 99%;
                                float: left;
                                margin: 10px 0px 0px 2px;
                            }

                            .control-group-half {
                                width: 49%;
                                float: left;
                                margin: 10px 0px 0px 2px;
                            }

                            .control-group-expiry{
                                width: 20%;
                                float: left;
                                margin: 10px 0px 0px 2px;
                            }

                            .control-group-cvv{
                                width: 32%;
                                float: left;
                                margin: 10px 0px 0px 12px;
                            }

                            .control-group-twenty-five{
                                width: 25%;
                                float: left;
                                margin: 10px 0px 0px 5px;
                            }

                            .control-group-twenty-six{
                                width: 26%;
                                float: left;
                                margin: 10px 0px 0px 5px;
                            }

                            .control-group-seventy{
                                width: 70%;
                                float: left;
                                margin: 10px 3px 0px;
                            }

                            .has-float-label label::after, .has-float-label>span::after {
                                content:none !important;
                                display: block;
                                position: absolute;
                                background: #fff;
                                height: 2px;
                                top: 50%;
                                left: -.2em;
                                right: -.2em;
                                z-index: -1;
                            }

                            .has-float-label .form-control:placeholder-shown:not(:focus)+*{
                                font-size: 100% !important;
                                top: 0.8em !important;
                                padding: 5px 0px !important;
                                /*  top: 0.6em !important;
                                  padding: 0px 0px !important;*/
                                left: 0px !important;
                            }

                            .has-float-label label{
                                left: 0px !important;
                                opacity: 0.8;
                                -webkit-transition: all .3s !important;
                                transition: all 0.3s !important;
                            }

                            .terms-checkbox{
                                width: 100%;
                                height: 58px;
                                padding: 4px 10px;
                                margin: 0px;
                            }

                            /* @media only screen
                             and (min-device-width : 768px)
                             and (max-device-width : 1024px){
                                 .mainDiv{
                                     width:100% !important;
                                 }
                                 .modal-dialog {
                                     *//*max-width: 100%;*//*
                                     margin: 1.75rem auto !important;
                                 }
                             }

                             @media (min-width: 1200px) {
                                 .mainDiv{
                                     *//*width: 345px;*//*
                                     width: 400px !important;
                                 }
                                 .modal-dialog {
                                     max-width: auto !important;
                                 }
                             }

                             @media (max-width:576px) {
                                 .mainDiv{
                                     width: 100% !important;
                                 }
                                 .modal-dialog {
                                     max-width: 100% !important;
                                     margin: 0px !important;
                                 }
                             }

                             @media (min-width: 576px) {
                                 .modal-sm {
                                     *//*max-width: 346px;*//*
                                     max-width: 400px !important;
                                 }
                                 .modal-sm-cancel {
                                     max-width: 250px !important;
                                 }
                             }*/

                            /* .mainDiv{
                             ;
                             ;
                             }

                             .background {
                                 background:#fff;
                             ;
                             }



                             .close {
                                 color:#ffffff!important;
                             ;
                             ;
                             }

                             .logo{                *//* background color of logo  *//*
                                 background:transparent !important;
                                 box-shadow: none !important;
                             }

                             .timeout{
                             ;
                             ;
                             }
                             *//* end of header *//*


                             .form-header{          *//* form header label color(Personal,Address,Card)  *//*
                                 color:#000000!important;
                             }

                             .form-control , .form-control:focus *//*, .form-control:disabled, .form-control[readonly]*//* {        *//* input control focus disabled background and font color *//*
                                 background-color:#ffffff!important;
                                 color:#000000!important;
                             }

                             .has-float-label label, .has-float-label>span{            *//* input control label font color *//*
                                 color:#000000!important;
                             }

                             .form-label , .terms-checkbox{            *//* for labels as well as terms & condition*//*
                                 color:#000000!important;
                             ;
                             }

                             .selectedBank{ *//*  selected bank option background and font color *//*
                                 background-color:#f3f3f3!important;

                             }

                             .selectedBank .bank-label{
                                 color:#000000!important;
                             }

                             .wallet-title{
                                 color:#000000;
                             }

                             .list :checked+label { *//* wallet selected option background color *//*
                                 background-color:#7eccad!important;
                                 color:#ffffff!important;
                             }

                             .list :checked+label .wallet-title{
                                 color:#ffffff!important;
                             }

                             .merchant-details{
                                 background-color:#7eccad!important;
                                 color:#ffffff!important;
                                 border: none !important;
                                 margin: 0px;
                                 padding: 4px;
                             }

                             .emi{
                                 color:#000000!important;
                             }

                             *//* loader *//*
                             .loader , .loader-edge{                    *//* loader color*//*
                                 color:#7eccad!important;
                                 opacity: .8;
                             }
*/

                            /* end of footer */
                            .widget .tab-content
                            {
                                padding-top: 0px !important;
                            }

                            #checkoutFlag > .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td
                            {
                                width:0%;
                            }
                            .table>thead>tr>th
                            {
                                background-color:#7eccad!important;
                            }

                        </style>

                        <link rel="stylesheet" type="text/css" href="/merchant/transactionCSS/css/fail.css"></head>


                        <div style="font-family: Montserrat;" class="background">


                            <!-- modal starts -->
                            <div class="modal show" style="display: inline-block" role="dialog">
                                <div class="modal-dialog modal-sm modal-dialog-centered">
                                    <div class="modal-content">
                                        <input type="hidden" id="merchantLogo" value="/images/merchant/<%=isMerchantLogoName%>">
                                        <%
                                            if(merchantDetailsVO1!= null)
                                            {
                                        %>
                                        <input type="hidden" id="partnerLogo" value="/images/merchant/<%=merchantDetailsVO1.getLogoName()%>">
                                        <%
                                            }
                                        %>

                                        <div id="target" class="mainDiv">
                                            <div class="header">

                                                <%
                                                    String padForAmt = "";
                                                    String supportName = "";
                                                     String merchantlogoDisplay = "display:none;";
                                                     String partnerlogoDisplay = "display:none;";
                                                    if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
                                                    {
                                                        merchantlogoDisplay = "display:block;";
                                                        partnerlogoDisplay = "display:none;";
                                                        supportName = merchantDetailsVO1.getCompany_name();
                                                        padForAmt = "line-height: 79px;";
                                                    }
                                                    else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
                                                    {
                                                        if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
                                                        {
                                                            merchantlogoDisplay = "display:block;";
                                                        }
                                                        else
                                                        {
                                                            partnerlogoDisplay = "display:block;";
                                                        }
                                                        supportName = merchantDetailsVO1.getPartnerName();
//            padForAmt = "padding: 20px 0px;";
                                                        padForAmt = "line-height: 79px;";
                                                    }
                                                    else
                                                    {
                                                        merchantlogoDisplay = "display:none;";
                                                        partnerlogoDisplay = "display:none;";
                                                    }

                                                %>
                                                <div class="logo logoImage1" style="padding-top: 25px;height:75px;<%=merchantlogoDisplay%>">
                                                    <img class="images-style1" style="max-width: 100%;max-height:100%;" src="/images/merchant/<%=isMerchantLogoName%>">
                                                </div>
                                                <%

                                                %>
                                                <div class="logo logoImage2" style="padding-top: 25px;height:75px;<%=partnerlogoDisplay%>">
                                                    <%
                                                        if (merchantDetailsVO1!= null)
                                                        {
                                                    %>
                                                    <img class="images-style1" style="max-width: 100%;max-height:100%" src="/images/merchant/<%=merchantDetailsVO1.getLogoName()%>">
                                                    <%
                                                        }
                                                    %>
                                                </div>


                                                <div class="name" style="text-align: right;">
                                                    <%
                                                        String visibility = "block";
                                                        String amountStyle= "";
                                                        if(merchantDetailsVO1!= null && merchantDetailsVO1.getMerchantOrderDetailsDisplay().equalsIgnoreCase("N"))
                                                        {
                                                            visibility  = "none";
//                amountStyle = "font-size: 25px;"+padForAmt;
                                                            amountStyle = padForAmt;
                                                        }
                                                    %>
                                                    <div class="client-name" style="display: <%=visibility%>" >
                                                        <%
                                                            if (merchantDetailsVO1!= null)
                                                            {
                                                        %>
              <span class="tool"><%=merchantDetailsVO1.getCompany_name()%>
                <div class="tooltiptext"><%=merchantDetailsVO1.getCompany_name()%> </div>
              </span>
                                                        <%
                                                            }
                                                        %>
                                                    </div>
                                                    <div class="client-order" style="display: <%=visibility%>">
              <span class="toolOrderID" id="toolOrderID">Order Id: 000121
                <div class="tooltiptextOrderId" id="tooltiptextOrderId"></div>
              </span>
                                                    </div>
                                                    <div class="client-amount" style="<%=amountStyle%>">$ 50.00</div>
                                                </div>

                                                <button type="button" id="closebtn" class="close hide" data-dismiss="modal" aria-label="Close" onclick="cancelTransaction()">
                                                    <span aria-hidden="true">×</span>
                                                </button>
                                                <%
                                                    System.out.println("checkoutTimerFlag++++"+checkoutTimerFlag);

                                                    String checkoutTimerFlagDisplay = "visibility:hidden;";
                                                    if (checkoutTimerFlag.equalsIgnoreCase("Y"))
                                                    {
                                                        checkoutTimerFlagDisplay = "visibility:inherit;";
                                                    }
                                                    else
                                                    {
                                                        checkoutTimerFlagDisplay = "visibility:hidden;";
                                                    }

                                                    %>

                                                <input type="hidden" id="timeoutValue" value="<%=checkoutTimerTime%>">
                                                <div class="timeout" style="<%=checkoutTimerFlagDisplay%>">Timeout = <span id="timer"><%=checkoutTimerTime%></span></div>


                                            </div>

                                            <div class="modal-body">
                                                <div class="top-bar">


                                                    <div class="top-left" id="topLeft">
                                                        <span <%--class="form-btn prev-btn"--%> id="backArrow" style="padding-top:8px;float: left; width: 20px;height:38px;padding-left:5px; cursor: pointer; display: none;" onclick="back()"><i class="fas fa-angle-left"></i></span>

                                                        <%--<span class="" id="cancelbackArrow" style="float: left;width: 20px;cursor: pointer" onclick="cancel()">
                                                          <i class="fas fa-angle-left"></i>
                                                        </span>--%>
                                                        <div id="payMethodDisplay" class="truncate" style="text-align: center">Select a Payment Method</div>
                                                        <div id="payMethod" class="truncate" style="display: none">CreditCards</div>
                                                    </div>
                                                </div>




                                                <div class="check_mark hide">
                                                    <div id="load" class="loader hide">
                                                    </div>
                                                </div>



                                                <div id="options" <%--style="display: none;"--%>>
                                                    <div class="footer-tab">
                                                        <ul class="nav nav-tabs" id="myTab" >


                                                            <li class="tabs-li" onclick="PaymentOptionHideShow('CreditCards', 'CreditCards')" style="">

                                                                <a href="#CreditCards" data-toggle="tab" title="CreditCards" aria-expanded="false">

                                                                            <span class="tab-icon">
                                                                                  <i class="far fa-credit-card tab-icon"></i>
                                                                            </span>
                                                                    <div class="label-style" id="labelHeadingName">CreditCards</div>
                                                                </a>
                                                            </li>

                                                        </ul>
                                                    </div>
                                                </div>


                                                <div class="tab-content">

                                                    <div class="tab-pane options active" id="CreditCards1">
                                                        <input type="hidden" name="VISA" class="" id="VISA" value="4538,Y,Y,USD">

                                                        <script>
                                                            var funcParam = {
                                                                city: 'cityLabel',
                                                                zip: 'zipLabel'
                                                            }
                                                        </script>


                                                        <div class="tab-pane" id="CreditCards" style="display:none;">
                                                            <form id="CreditCardsForm" class="form-style" method="post" autocomplete="off">
                                                                <input type="hidden" name="ctoken" id="CCToken" value="ZDyOY4pp7oauCtyGno8dZma21Azzhywp">
                                                                <input type="hidden" id="terminalid" name="terminalid" value="">
                                                                <input type="hidden" id="paymentOtionCode" name="paymentOtionCode" value="">
                                                                <input type="hidden" id="attemptThreeD" name="attemptThreeD" value="null">
                                                                <input type="hidden" id="consentStmnt" name="consentStmnt" value="I confirm that I ve read and agreed to thePrivacy PolicyTerms of Use">
                                                                <input type="hidden" name="merchantOrderDetails" id="merchantOrderDetails" value="Y">

                                                                <%
                                                                    if(personalInfoDisplay.equals("Y") || personalInfoDisplay.equals("E") || personalInfoDisplay.equals("M"))
                                                                    {
                                                                %>
                                                                <div class="tab" id="personalinfo" style="display:none;padding-top: 22px; display: inline-block;margin-left: 12px;margin-right: 12px;font-size: 14px;margin-bottom: 120px">
                                                                    <p class="form-header" style="display: none;">Personal Details</p>
                                                                    <%
                                                                        /*String widthField = "";
                                                                        if(personalInfoDisplay.equals("E") || personalInfoDisplay.equals("M"))
                                                                        {
                                                                            widthField ="width:190%!important;";
                                                                        }*/
                                                                    if(personalInfoDisplay.equals("Y") || personalInfoDisplay.equals("E"))
                                                                        {

                                                                    %>
                                                                    <div id="emailPersonal">
                                                                        <div class="form-group has-float-label control-group-full" id="CardEmail">
                                                                            <span class="input-icon" style="position: absolute;left: 338px;top: 15px;"><i class="far fa-envelope"></i></span>
                                                                            <input type="email" class="form-control input-control1" id="email" placeholder=" " onchange="validateEmail(event.target.value ,'CardEmail')" oninput="this.className = 'form-control input-control1'" name="emailaddr" value="" autofocus="" maxlength="50" autocomplete="off">
                                                                            <label for="email" class="form-label">Email</label>
                                                                        </div>
                                                                    </div>
                                                                    <%
                                                                        }
                                                                        if(personalInfoDisplay.equals("Y") || personalInfoDisplay.equals("M"))
                                                                        {
                                                                    %>
                                                                    <div id="mobilePersonal">
                                                                        <div class="form-group has-float-label control-group-full">
                                                                            <div class="dropdown">
                                                                                <input id="country_input_optional" class="form-control input-control1" placeholder=" " onblur="pincodecc('country_input_optional','country_optional','phonecc-id','phonecc','country_input','country','statelabel',funcParam);" oninput="this.className = 'form-control input-control1'" onkeypress="return isLetterKey(event)" autocomplete="off">
                                                                                <label for="country_input_optional" class="form-label">Country</label>
                                                                                <input type="hidden" id="country_optional" name="country_input_optional" value="IN">
                                                                                <script>
                                                                                    setCountry('GBP', 'country_input_optional');
                                                                                </script>
                                                                            </div>
                                                                        </div>
                                                                        <div class="form-group has-float-label control-group-half">
                                                                            <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc-id" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc-id','phonecc')" value="" oninput="this.className = 'form-control input-control1'">
                                                                            <label for="phonecc-id" class="form-label">Phone Country Code</label>
                                                                            <input type="hidden" id="phonecc" name="phone-CC" value="">
                                                                        </div>
                                                                        <div class="form-group has-float-label control-group-half" id="CardPhoneNum">
                                                                            <span class="input-icon-half" style="position: absolute;left: 150px;top: 15px;"><i class="fas fa-phone"></i> </span>
                                                                            <input type="text" class="form-control input-control1" id="phonenophonecc" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="">
                                                                            <label for="phonenophonecc" class="form-label">Phone No.</label>
                                                                        </div>
                                                                    </div>
                                                                    <%
                                                                        }
                                                                    %>

                                                                </div>
                                                                <%
                                                                    }
                                                                %>

                                                                <div class="tab" id="cardinfo" style="display:none;padding-top: 22px; display: inline-block;margin-left: 12px;margin-right: 12px;font-size: 14px;margin-bottom: 65px">
                                                                    <p class="form-header" style="display: none;">Card Details</p>

                                                                    <div class="form-group has-float-label control-group-seventy card" id="CardNumber">
                                                                        <input class="form-control input-control1" id="cardNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" onfocusout="isValidCardCheck('cardNo', 'CardNumber','onfocusout')" autocomplete="off" maxlength="19" oninput="this.className = 'form-control input-control1'" name="cardnumber">
                                                                        <label for="cardNo" class="form-label">Card Number</label>
                                                                        <span class="card_icon"></span>
                                                                    </div>
                                                                    <div class="form-group has-float-label control-group-twenty-five" id="CardExpiry">
                                                                        <input type="text" class="form-control input-control1" id="Expiry" placeholder="MM/YY" onkeypress="return isNumberKey(event)" onblur="expiryCheck('Expiry','CardExpiry')" autocomplete="off" onkeyup="addSlash(event,'Expiry')" oninput="this.className = 'form-control input-control1'" name="expiry" maxlength="5">
                                                                        <label for="Expiry" class="form-label">Expiry</label>
                                                                    </div>
                                                                    <div class="form-group has-float-label control-group-seventy" id="CardName">
                                                                        <input type="text" class="form-control input-control1" id="fname" placeholder=" " onblur="validateCardHolderName('fname','CardName')" oninput="this.className = 'form-control input-control1'" name="firstname">
                                                                        <label for="fname" class="form-label">Card Holder Name</label>
                                                                    </div>
                                                                    <div class="form-group has-float-label control-group-twenty-five" id="CardCVV">
                                                                        <input type="password" class="form-control input-control1" id="CVV" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="4" autocomplete="off" onblur="validateCVV('CVV','CardCVV')" oninput="this.className = 'form-control input-control1'" name="cvv">
                                                                        <label for="CVV" class="form-label">CVV</label>
                                                                    </div>

                                                                    <div class="form-group emi" id="EMIOption" style="float: left;padding: 10px 0px;width: 100%;margin: 0px;display: none">
                                                                        <input type="checkbox" disabled class="" style="width: 7%;" id="emi" checked onchange="toggleEMI()">
                                                                        <label for="emi" style="font-size:85%;/*position: absolute;*/width: 92%;float: right">
                                                                            <div class="form-group has-float-label control-group-full" style="margin: 0px;" id="emiSelect">
                                                                                <div style="/*float: left;*/">Pay Installment in</div>
                                                                                <div style="/*float: left;width: 16% !important;margin: 0px 9px;*/">
                                                                                    <select class="form-control input-control1" id="installment" name="emiCount" style="height: 26px;;padding: 0px !important;" disabled=""></select>
                                                                                </div>
                                                                            </div>
                                                                        </label>
                                                                    </div>

                                                                </div>
                                                                <%
                                                                    String consentFlagDisplay = "visibility:hidden;";
                                                                    if (consentFlag.equalsIgnoreCase("Y"))
                                                                    {
                                                                        consentFlagDisplay = "visibility:inherit;";
                                                                    }
                                                                    else
                                                                    {
                                                                        consentFlagDisplay = "visibility:hidden;";
                                                                    }
                                                                %>
                                                                <div class="form-group terms-checkbox" id="terms"  style="margin-left: 12px;margin-right: 12px;margin-bottom: 55px;<%=consentFlagDisplay%>">
                                                                    <input type="checkbox" class="" style="width: 5%" id="TC" onclick="disablePayButton(this)">
                                                                    <label for="TC" style="font-size: 105%;position: absolute;margin: 0px;">
                                                                        I confirm that I ve read and agreed to the <b> <a <%--href=""--%> <%--target="_blank"--%>>Privacy Policy
                                                                    </a> </b> and <b> <a <%--href="#"--%> <%--target="_blank"--%>>Terms of Use
                                                                    </a></b>.
                                                                    </label>
                                                                </div>


                                                                <div style="overflow:hidden">
                                                                    <div style="float:right;"> <!-- Previous button-->
                                                                        <%--<div class="form-btn prev-btn" id="prevBtn" onclick="nextPrev()" style="display: inline;">
                                                                            &lt;%&ndash; <i class="fas fa-angle-left"></i>&ndash;%&gt;
                                                                        </div>--%>
                                                                    </div>
                                                                </div>
                                                                <!--Passing parameters hidden for SinglecallPayment-->
                                                            </form>
                                                        </div>




                                                    </div>





                                                    <div class="pay-btn" id="paybutton" style="display: none;">
                                                        <button type="button" class="pay-button" onclick="nextPrev()" tabindex="0">
                                                            <span id="payLabel">PAY  $50.00</span>
                                                        </button>
                                                    </div>
                                                </div>


                                                <%--<div class="modal fade" id="CancelModal">
                                                    <div class="modal-dialog-cancel modal-sm-cancel modal-dialog-centered">
                                                        <div class="modal-content-cancel">
                                                            <div class="modal-header-cancel">
                                                                <div class="modal-title-cancel">Cancel Deposit </div>

                                                            </div>
                                                            <div class="modal-body-cancel">
                                                                Are you sure you want to cancel this deposit ? <br>
                                                                Cancelled deposits cannot be retrieved or continued at a later time.
                                                            </div>
                                                            <div class="modal-footer-cancel">
                                                                <div>
                                                                    <button type="button" class="btn cancel-modal-button" onclick="closeCancel()"> DISMISS</button>
                                                                </div>
                                                                <div>
                                                                    <button type="button" class="btn cancel-modal-button" onclick="cancelTransaction()">CANCEL DEPOSIT</button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>--%>


                                            </div>

                                            <!-- merchant details -->

                                            <%
                                                String billigDesc = "";
                                                String isSupportSection = "display:none;";
                                                String isSupportSectionNoNeeded = "visibility:hidden;";
                                                String billingStatement = "The Charge Descriptor on your Bank statement will appear as <font style='font-weight: bold;'><br>\"+billigDesc+\"</font>";

                                                if("Y".equalsIgnoreCase(supportSection))
                                                {
                                                    isSupportSection = "display:block;";
                                                    if("Y".equalsIgnoreCase(supportNoNeeded))
                                                    {
                                                        if (merchantDetailsVO1!= null)
                                                        {
                                                            telNo = merchantDetailsVO1.getTelNo();
                                                        }
                                                        isSupportSectionNoNeeded = "visibility:inherit;";
                                                    }
                                                }
                                            %>

                                            <div class="merchant-details " style="<%=isSupportSection%>">
                                                <div class="footer-details">
                                                    <%--The Charge Descriptor on your Bank statement will appear as <b> SafeChargeEUR1 </b>--%>
                                                    <%=billingStatement%>
                                                </div>
                                                <div class="footer-details">
                                                    <%--Aditya Industrial Estate Mindspace, Malad West,Mumbai ,Maharashtra 400064--%>
                                                    <%=address%>
                                                </div>
                                                <div class="footer-details ">

                                                    <i class="fas fa-phone supportTelNo" style="<%=isSupportSectionNoNeeded%>">   </i>      <span class="supportTelNo" style="<%=isSupportSectionNoNeeded%>"><%=telNo%></span>  &nbsp;&nbsp;&nbsp;&nbsp;
                                                    <i class="fas fa-envelope"></i>   <%=email%>

                                                </div>
                                            </div>
                                            <%

                                            %>
                                            <!-- end of merchant details -->


                                            <%
                                                String powerBy ="";
                                                String sisaLogoFlag ="";
                                                String securityLogoFlag ="";
                                                String logoName ="";

                                                if (merchantDetailsVO1!= null)
                                                {
                                                     powerBy = merchantDetailsVO1.getPoweredBy();
                                                     sisaLogoFlag = merchantDetailsVO1.getSisaLogoFlag();
                                                     securityLogoFlag = merchantDetailsVO1.getSecurityLogo();
                                                     logoName = merchantDetailsVO1.getLogoName();
                                                }
                                                log.debug("merchant logo name from checkoutPayment.jsp----"+logoName);

                                                String align = "";
                                            %>

                                            <div class="modal-footer" style="width: 100%;padding: 7px 12px;display: inline-flex;">

                                                <%
                                                    String partnerLogoDislay = "display:none;";
                                                    String powerByLogoDislay = "display:none;";
                                                    String sisaLogoDislay = "display:none;";
                                                    String securityLogoDislay = "display:none;";
                                                    if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                                                    {
                                                        partnerLogoDislay = "display:block;";
                                                    }
                                                    else if((functions.isValueNull(powerBy) && functions.isValueNull(securityLogoFlag)))
                                                    {
                                                        if(("Y").equalsIgnoreCase(powerBy) && ("Y").equalsIgnoreCase(securityLogoFlag)){
                                                            align="text-align: left;";
                                                        }
                                                        else if(("Y").equalsIgnoreCase(powerBy) && ("N").equalsIgnoreCase(securityLogoFlag)){
                                                            align="text-align: left;";
                                                        }
                                                        else if(("N").equalsIgnoreCase(powerBy) && ("Y").equalsIgnoreCase(securityLogoFlag)){
                                                            align="text-align: left;";
                                                        }
                                                        else{
                                                            align="text-align: center;";
                                                        }
                                                    }
                                                    else
                                                    {
                                                        align="text-align: left;";
                                                    }
                                                    if(functions.isValueNull(sisaLogoFlag) && sisaLogoFlag.equalsIgnoreCase("Y"))
                                                    {
                                                        sisaLogoDislay = "display:block;";
                                                        if((functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y")) &&
                                                                ((functions.isValueNull(powerBy) && ("Y").equalsIgnoreCase(powerBy)) ||
                                                                        (functions.isValueNull(securityLogoFlag) && ("Y").equalsIgnoreCase(securityLogoFlag))))
                                                        {
                                                            align="text-align: center;c6";
                                                        }

                                                    }
                                                    if(functions.isValueNull(powerBy) && powerBy.equalsIgnoreCase("Y"))
                                                    {
                                                        powerByLogoDislay = "display:block;";
                                                    }
                                                    else if(functions.isValueNull(securityLogoFlag) && securityLogoFlag.equalsIgnoreCase("Y"))
                                                    {
                                                        securityLogoDislay = "display:block;";
                                                    }


                                                %>
                                                <div class="footer-logo paymentzMainLogo" style="text-align: left;<%=partnerLogoDislay%>">
                                                    <img  class="logo-style" src="/images/merchant/<%=logoName%>">
                                                </div>

                                                <div class="footer-logo pciMainLogo" style="<%=align%><%=sisaLogoDislay%>">
                                                    <img  class="logo-style" src="/images/merchant/PCI.png">
                                                </div>

                                                <div class="footer-logo powerdByLogo" style="text-align: right;<%=powerByLogoDislay%>"> <%--<%=standardKitValidatorVO.getmerchantDetailsVO1().getLogoName()%>--%>
                                                    <img  class="logo-style"  src="/images/merchant/poweredBy_logo.png">
                                                </div>

                                                <div class="footer-logo securityLogo" style="text-align: right;<%=securityLogoDislay%>">
                                                    <img  class="logo-style" src="/images/merchant/security.png" style="width: auto;margin: 0px 15px;">
                                                </div>

                                            </div>
                                            <!-- footer  -->


                                        </div>

                                    </div>
                                </div>
                            </div>


                            <%--

                                                            <!--lporirxe//-->
                                                            <div id="flpx_659_957" style="display:none;"></div>

                                                            <ul id="ui-id-1" tabindex="0" class="ui-menu ui-widget ui-widget-content ui-autocomplete ui-front" style="width: 376.344px; top: 347px; left: 493px; display: none;"><li class="ui-menu-item"><div id="ui-id-3" tabindex="-1" class="ui-menu-item-wrapper">British Indian Ocean Territory</div></li><li class="ui-menu-item"><div id="ui-id-4" tabindex="-1" class="ui-menu-item-wrapper">India</div></li></ul><div role="status" aria-live="assertive" aria-relevant="additions" class="ui-helper-hidden-accessible"><div style="display: none;">2 results are available, use up and down arrow keys to navigate.</div><div style="display: none;">British Indian Ocean Territory</div><div>India</div></div><ul id="ui-id-2" tabindex="0" class="ui-menu ui-widget ui-widget-content ui-autocomplete ui-front" style="display: none;"></ul><div role="status" aria-live="assertive" aria-relevant="additions" class="ui-helper-hidden-accessible"></div></div>&lt;%&ndash;</html>&ndash;%&gt;

                            --%>

                        </div>
                    </div>
                </div>
                <div class="col-md-12">
                    <table id="checkoutFlag" align=center width="50%"
                           class="display table table table-striped table-bordered table-hover dataTable"
                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                        <thead>
                        <tr>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b>Is Pharma(Y/N)</b></td>

                                     <td valign="middle" align="center"
                                         style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                         <b>Is Powered By Logo</b></td>

                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b>Template</b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b>Is Merchant Logo</b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b>Is Security Logo</b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b>PCI Logo</b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b>Partner Logo</b></td>
                            <%--<td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b></b></td>--%>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td valign="middle" data-label="Is Pharma(Y/N)" align="center"
                                class="<%=style%>">
                                <select name='isPharma' class="form-control"
                                        onchange="ChangeFunction(this.value,'Is Pharma(Y/N)')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isPharma")))); %>
                                </select>
                            </td>
                              <td valign="middle" data-label="Is Powered By Logo" align="center"
                                  class="<%=style%>">
                                  <select name='isPoweredBy' class="form-control"  onchange="ChangeFunction(this.value,'Is Powered By Logo')">
                                      <%
                                          out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isPoweredBy")))); %>
                                  </select>
                              </td>

                            <td valign="middle" data-label="Template" align="center"
                                class="<%=style%>">
                                <select name='template' class="form-control"
                                        onchange="ChangeFunction(this.value,'Template')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("template")))); %>
                                </select>
                            </td>

                            <td valign="middle" data-label="Is Merchant Logo" align="center"
                                class="<%=style%>">
                                <select name='ismerchantlogo' class="form-control"
                                        onchange="ChangeFunction(this.value,'Is Merchant Logo')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("ismerchantlogo")) ? (String) tempHash.get("ismerchantlogo") : "N"))); %>
                                </select>
                            </td>
                            <td valign="middle" data-label="Template" align="center"
                                class="<%=style%>">
                                <select name='isSecurityLogo' class="form-control"
                                        onchange="ChangeFunction(this.value,'Is Security Logo')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("isSecurityLogo")) ? (String) tempHash.get("isSecurityLogo") : "N"))); %>
                                </select>
                            </td>
                            <td valign="middle" data-label="PCI Logo" align="center"
                                class="<%=style%>"
                                onchange="ChangeFunction(this.value,'PCI Logo')">
                                <select name='ispcilogo' class="form-control"
                                        &lt;%&ndash;&ndash;%&gt;>
                                    <%
                                        if ("Y".equals(tempHash.get("ispcilogo")))
                                        {
                                    %>
                                    <option value="Y">Y
                                    </option>
                                    <option value="N">N
                                    </option>
                                    <%
                                    }
                                    else
                                    {
                                    %>
                                    <option value="N">N
                                    </option>
                                    <option value="Y">Y
                                    </option>
                                    <% }%>
                                </select>
                            </td>
                            <td valign="middle" data-label="Partner Logo" align="center"
                                class="<%=style%>"
                                onchange="ChangeFunction(this.value,'Partner Logo')">
                                <select name='ispartnerlogo'
                                        class="form-control" &lt;%&ndash;&ndash;%&gt;>
                                    <%
                                        if ("Y".equals(tempHash.get("ispartnerlogo")))
                                        {
                                    %>
                                    <option value="Y">Y
                                    </option>
                                    <option value="N">N
                                    </option>
                                    <%
                                    }
                                    else
                                    {
                                    %>
                                    <option value="N">N
                                    </option>
                                    <option value="Y">Y
                                    </option>
                                    <% }%>
                                </select>
                            </td>
                        </tr>
                        </tbody>
                        <thead>
                        <tr>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>Visa Secure Logo</label>
                                </b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>Support Section</label> </b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>SupportNo Needed</label></b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>MC Secure Logo</label>
                                </b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>Consent</label>
                                </b></td>
                            <td valign="middle" align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>Checkout Timer</label>
                                </b></td>
                            <td valign="middle"  align="center"
                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                <b><label>Checkout Timer Time</label>
                                </b></td>

                            <%--<td valign="middle" align="center"--%>
                            <%--style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">--%>
                            <%--<b>&nbsp;</b></td>--%>
                        </tr>
                        </thead>
                        <tr>
                            <td valign="middle" data-label="Visa Secure Logo" align="center"
                                class="<%=style%>">
                                <select name='vbvLogo' class="form-control"
                                        onchange="ChangeFunction(this.value,'Visa Secure Logo')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("vbvLogo")) ? (String) tempHash.get("vbvLogo") : "N"))); %>
                                </select>
                            </td>
                            <td valign="middle" data-label="Support Section" align="center"
                                class="<%=style%>">
                                <select name='supportSection' class="form-control"  onchange="ChangeFunction(this.value,'Support Section')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("supportSection")))); %>
                                </select>
                            </td>
                            <td valign="middle" data-label="Support Number Needed" align="center"
                                class="<%=style%>">
                                <select name='supportNoNeeded' class="form-control"  onchange="ChangeFunction(this.value,'Support Number Needed')" id="supportNoNeeded"  <%if(tempHash.get("supportSection").equals("N")){  %>readonly<%}%>>
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("supportNoNeeded")))); %>
                                </select>
                                <input type=hidden size=10  name='supportNoNeeded' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("supportNoNeeded"))%>" >
                            </td>
                            <td valign="middle" data-label="Master Secure Logo" align="center"
                                class="<%=style%>">
                                <select name='masterSecureLogo' class="form-control"
                                        onchange="ChangeFunction(this.value,'Master Secure Logo')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("masterSecureLogo")) ? (String) tempHash.get("masterSecureLogo") : "N"))); %>
                                </select>
                            </td>
                            <td valign="middle" data-label="Consent" align="center"
                                class="<%=style%>">
                                <select name='consent' class="form-control"
                                        onchange="ChangeFunction(this.value,'Consent')">
                                    <%
                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("consent")) ? (String) tempHash.get("consent") : "Y"))); %>
                                </select>
                            </td>
                            <td valign="middle" data-label="Checkout Timer" align="center"
                                class="<%=style%>">
                                <select name='checkoutTimer' class="form-control"
                                        &lt;%&ndash;&ndash;%&gt;
                                        onchange="ChangeFunction(this.value,'Checkout Timer')">
                                    <%
                                        if ("Y".equals(tempHash.get("checkoutTimer")))
                                        {
                                    %>
                                    <option value="Y">Y
                                    </option>
                                    <option value="N">N
                                    </option>
                                    <%
                                    }
                                    else
                                    {
                                    %>
                                    <option value="N">N
                                    </option>
                                    <option value="Y">Y
                                    </option>
                                    <% }%>
                                </select>
                            </td>
                            <td valign="middle" data-label="Checkout Timer Time" align="center"
                                class="<%=style%>">
                                <input class="form-control" type="Text" placeholder="00:00"
                                       maxlength="5"
                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("checkoutTimerTime"))%>"
                                       name="checkoutTimerTime" size="50"
                                       onchange="ChangeFunction(this.value,'Checkout Timer Time')">
                            </td>
                        </tr>
                        <tbody>
                        <tr>
                        </tr>
                        </tbody>
                    </table>

                </div>
                <div class="col-md-6">
                    </div>
                <div class="col-md-2">
                    <%-- <td align="center" colspan="2">--%>
                    <button type="submit" value="Save" class="buttonform btn btn-default"
                            name="Save"
                            onclick="mySave()" style="background-color: #98A3A3;margin-top:30px;">
                        Save
                    </button>
                    <%-- </td>--%>
                </div>
                <div class="col-md-5">
                </div>
            </div>
                    <div class="row reporttable">
                        <div class="widget1">
                        <div class="widget1-content padding" style="overflow-x: auto;background-color: #fff;">

                        </div>
                        </div>
                    </div>


        <%--New checkout template color end--%>
        <%
            } //end for
        %>

        </form>

        <br>
        <%
        }
        else if (functions.isValueNull(errormsg))
        {%>
        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x: auto;">
                        <%
                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                        %>
                    </div>
                </div>
            </div>
        </div>
        <%
        }
        else
        {
        %>
        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x: auto;">
                        <%
                            out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found."));
                        %>
                    </div>
                    <br>
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
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
<script>
    //onload
    //onload
    function colorPicker(first, to)
    {
        console.log("first ++++" + first);
        console.log("to ++++" + to);
        $("#" + to).val($("#" + first).val());
        var isTemplateFlag =  $("[name=template]").val()
        if(isTemplateFlag == "Y")
        {
            if (first == "NEW_CHECKOUT_BODYNFOOTER_COLOR_picker" || first == "NEW_CHECKOUT_BODYNFOOTER_COLOR")
            {
                 var current = "display:none;"
             if($("#" + first).val() != "")
             {
                 if ($("#cardinfo").hasClass("activeTab"))
                 {
                     $(".tab-pane").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                     $("#CreditCards").css("display","inline-block");
                     $("#cardinfo").show();
                     $("#personalinfo").hide();
                 }
                 else if ($("#personalinfo").hasClass("activeTab"))
                 {
                     $(".tab-pane").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                     $("#CreditCards").css("display","inline-block");
                     $("#cardinfo").hide();
                     $("#personalinfo").show();
                 }
                 else
                 {
                     $(".tab-pane").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                     $("#CreditCards").css("display","none");
                 }
                 $(".footer-tab").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                 $("#personalinfo .form-control").attr('style', 'background-color:' + $("#" + first).val() + ' !important;');
                 $("#cardinfo .form-control").attr('style', 'background-color:' + $("#" + first).val() + ' !important;');
             }else{
                 if ($("#cardinfo").hasClass("activeTab"))
                 {
                     $(".tab-pane").attr('style','background-color:#fff !important;');
                     $("#CreditCards").css("display","inline-block");
                     $("#cardinfo").show();
                     $("#personalinfo").hide();
                 }
                 else if ($("#personalinfo").hasClass("activeTab"))
                 {
                     $(".tab-pane").attr('style','background-color:#fff !important;');
                     $("#CreditCards").css("display","inline-block");
                     $("#cardinfo").hide();
                     $("#personalinfo").show();
                 }
                 else
                 {
                     $(".tab-pane").attr('style','background-color:#fff !important;');
                     $("#CreditCards").css("display","none");
                 }
                 $(".footer-tab").attr('style', 'background-color:#fff !important;');
                 $("#personalinfo .form-control").attr('style', 'background-color: #fff!important;');
                 $("#cardinfo .form-control").attr('style', 'background-color: #fff!important;');
             }
            }
            else if (first == "NEW_CHECKOUT_HEADERBACKGROUND_COLOR_picker" || first == "NEW_CHECKOUT_HEADERBACKGROUND_COLOR")
            {
                var current = $('#NEW_CHECKOUT_HEADER_FONT_COLOR').val();
                if($("#" + first).val() != "")
                {
                    $(".header").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                }else{
                    $(".header").attr('style', 'background-color: #7eccad!important;color:' + current + ' !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_HEADER_FONT_COLOR_picker" || first == "NEW_CHECKOUT_HEADER_FONT_COLOR")
            {
                var current = $('#NEW_CHECKOUT_HEADERBACKGROUND_COLOR').val();
                if($("#" + first).val() != "")
                {
                    $(".header").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                }else
                {
                    $(".header").attr('style', 'color:#fff !important;background-color:' + current + ' !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_NAVIGATIONBAR_COLOR_picker" || first == "NEW_CHECKOUT_NAVIGATIONBAR_COLOR")
            {
                var current = $('#NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR').val();
                if($("#" + first).val() != "")
                {
                    $(".top-bar").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                }else
                {
                    $(".top-bar").attr('style', 'background-color:#7eccad !important;color:' + current + ' !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR_picker" || first == "NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR")
            {
                var current = $('#NEW_CHECKOUT_NAVIGATIONBAR_COLOR').val();
                //$(".top-bar").attr('style','color:'+$("#" + first).val()+' !important;');
                if($("#" + first).val() != "")
                {
                    $(".top-bar").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                }else
                {
                    $(".top-bar").attr('style', 'color:#000000 !important;background-color:' + current + ' !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_BUTTON_FONT_COLOR_picker" || first == "NEW_CHECKOUT_BUTTON_FONT_COLOR")
            {
                var current = $('#NEW_CHECKOUT_BUTTON_COLOR').val();
                if($("#" + first).val() != "")
                {
                    $(".pay-button").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                }else
                {
                    $(".pay-button").attr('style', 'color:#000000 !important;background-color:' + current + ' !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_BUTTON_COLOR_picker" || first == "NEW_CHECKOUT_BUTTON_COLOR")
            {
                var current = $('#NEW_CHECKOUT_BUTTON_FONT_COLOR').val();
                var current1 = "padding-top: 8px; padding-left: 5px; float: left; width: 20px; height: 36px; cursor: pointer;"
                if($("#" + first).val() != "")
                {
                    $(".pay-button").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                    if ($("#cardinfo").hasClass("activeTab") || $("#personalinfo").hasClass("activeTab"))
                    {
                        $(".prev-btn").attr('style', current1 + 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                    }
                }else
                {
                    {
                        $(".pay-button").attr('style', 'background-color:transparent !important;color:' + current + ' !important;');
                        if ($("#cardinfo").hasClass("activeTab") || $("#personalinfo").hasClass("activeTab"))
                        {
                            $(".prev-btn").attr('style', current1 + 'background-color:#7eccad  !important;color:' + current + ' !important;');
                        }
                    }
                }
            }

            else if (first == "NEW_CHECKOUT_FULLBACKGROUND_COLOR_picker" || first == "NEW_CHECKOUT_FULLBACKGROUND_COLOR")
            {
                var current = "font-family: Montserrat;"
                if($("#" + first).val() != "")
                {
                    $(".background").attr('style', current + 'background-color:' + $("#" + first).val() + ' !important;');
                }else
                {
                    $(".background").attr('style', current + 'background-color:#fff !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_LABEL_FONT_COLOR_picker" || first == "NEW_CHECKOUT_LABEL_FONT_COLOR")
            {
                var current = "margin-left: 12px;margin-right: 12px;margin-bottom: 55px;";
                if($("#" + first).val() != "")
                {
                    $(".label-style").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                    $(".form-label").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                }else{
                    $(".label-style").attr('style', 'color:#000000 !important;');
                    $(".form-label").attr('style', 'color:#000000 !important;');
                }

                var flagconsent = $("[name=consent]").val();
                if(flagconsent== "Y")
                {
                    if($("#" + first).val() != "")
                    {
                        $(".terms-checkbox").attr('style', current + 'color:' + $("#" + first).val() + ' !important;');
                    }else
                    {
                        $(".terms-checkbox").attr('style', current + 'color:#000000 !important;');
                    }
                }
            }
            else if (first == "NEW_CHECKOUT_BOX_SHADOW_picker" || first == "NEW_CHECKOUT_BOX_SHADOW")
            {
                if($("#" + first).val() != "")
                {
                    $(".mainDiv").attr('style', 'box-shadow: 0px 0px 20px ' + $("#" + first).val() + ' !important;');
                }else
                {
                    $(".mainDiv").attr('style', 'box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5) !important;');
                }
            }
            else if (first == "NEW_CHECKOUT_TIMER_COLOR_picker" || first == "NEW_CHECKOUT_TIMER_COLOR")
            {
                /*var current = "width: 100%;padding: 7px 12px;display: inline-flex;"*/
                var flagcheckoutTimer = $("[name=checkoutTimer]").val();
                if(flagcheckoutTimer== "Y")
                {
                    if($("#" + first).val() != "")
                    {
                        $(".timeout").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                    }else
                    {
                        $(".timeout").attr('style', 'color:#000000 !important;');
                    }
                }
            }
            else if (first == "NEW_CHECKOUT_ICON_COLOR_picker" || first == "NEW_CHECKOUT_ICON_COLOR")
            {
                if($("#" + first).val() != "")
                {
                    $(".tab-icon").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                }else
                {
                    $(".tab-icon").attr('style', 'color: #3583D2 !important;');
                }
            }
            else if(first == "NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR_picker" || first == "NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR")
            {
                var current = $('#NEW_CHECKOUT_FOOTER_FONT_COLOR').val();
                var flagSupportSection = $("[name=supportSection]").val();
                var current1 = "width: 100%;padding: 7px 12px;display: inline-flex;"
                if($("#" + first).val() != "")
                {
                    $(".modal-footer").attr('style', current1 + 'background-color:' + $("#" + first).val() + ' !important;');
                }
                else{
                    $(".modal-footer").attr('style', current1 + 'background-color:#fff !important;');
                }
                if(flagSupportSection== "Y")
                {
                    if($("#" + first).val() != "")
                    {
                        $(".merchant-details").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                    }else
                    {
                        $(".merchant-details").attr('style', 'background-color:#3583d2 !important;color:' + current + ' !important;');
                    }
                }
            }
            else if(first == "NEW_CHECKOUT_FOOTER_FONT_COLOR_picker" || first == "NEW_CHECKOUT_FOOTER_FONT_COLOR")
            {
                var flagSupportSection = $("[name=supportSection]").val();
                var current = $('#NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR').val();
                if(flagSupportSection== "Y")
                {
                    if($("#" + first).val() != "")
                    {
                        $(".merchant-details").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                    }else
                    {
                        $(".merchant-details").attr('style', 'color:#fff !important;background-color:' + current + ' !important;');

                    }
                }
                /*var current1 = "width: 100%;padding: 7px 12px;display: inline-flex;"
                 $(".modal-footer").attr('style',current1+'background-color:'+$("#" + first).val()+' !important;');*/
            }
        }
    }


    function PaymentOptionHideShow(method, engLabel)
    {
        pay_methods = engLabel;
        label = method;
        var no_of_options = $("#" + pay_methods).find(".tabs-li-wallet");

        if (pay_methods == 'CreditCards')
        {

            showTab(0, 'CreditCards');
            /*cardInfo('CreditCards');*/
        }

        document.getElementById('payMethodDisplay').innerText = label;
        document.getElementById('payMethod').innerText = pay_methods;
        $("#topRight").show();
        $("#backArrow").show();
        $(".tab-pane").show();
        $("#options").hide();
        /* $('#' + pay_methods).addClass('active');
         $("#" + pay_methods + "Option").show();

         backButtonConfig();*/
    }
    var  currentTab = 0;
    function showTab(n, method)
    {
        console.log("showTab >>> "+n, method);
        $("#CreditCards").css("display","inline-block");
        $("#personalinfo").show();
        if($("#personalinfo").is(":visible"))
        {
            $("#personalinfo").addClass("activeTab");
            $("#personalinfo").show();
            $("#cardinfo").hide();
            $("#terms").hide();
            $("#paybutton").show();
            $("#backArrow").show();
            $("#payLabel").text("Next");
        }else
        {

            $("#personalinfo").removeClass("activeTab");
            $("#cardinfo").addClass("activeTab");
            $("#personalinfo").hide();
            $("#cardinfo").show();
            $("#paybutton").show();
            $("#terms").show();
            $("#backArrow").show();
            $("#payLabel").text("PAY  $50.00");
        }
        //$("#" + formname).find("#terms").removeClass("hide");

        //fixStepIndicator(n)

    }

    function nextPrev()
    {
        $("#personalinfo").removeClass("activeTab");
        $("#cardinfo").addClass("activeTab");
        $("#personalinfo").hide();
        $("#cardinfo").show();
        $("#terms").show();
        $("#payLabel").text("PAY  $50.00");
    }

    function back()
    {
        if($("#cardinfo").hasClass("activeTab"))
        {
            $("#personalinfo").show();
            if($("#personalinfo").is(":visible"))
            {
                $("#cardinfo").hide();
                $("#personalinfo").show();
                $("#cardinfo").removeClass("activeTab");
                $("#personalinfo").addClass("activeTab");
                $("#payLabel").text("Next");
                $("#terms").hide();
            }
            else
            {
                $("#payMethodDisplay").text("Select a Payment Method");
                $("#cardinfo").hide();
                $("#personalinfo").hide();
                $("#options").show();
                $("#personalinfo").removeClass("activeTab");
                $("#cardinfo").removeClass("activeTab");
                $("#backArrow").hide();
                $("#paybutton").hide();
                $("#terms").hide();
            }
        }
        else if($("#personalinfo").hasClass("activeTab"))
        {
            $("#payMethodDisplay").text("Select a Payment Method");
            $("#cardinfo").hide();
            $("#personalinfo").hide();
            $("#options").show();
            $("#personalinfo").removeClass("activeTab");
            $("#cardinfo").removeClass("activeTab");
            $("#backArrow").hide();
            $("#paybutton").hide();
            $("#terms").hide();
        }
    }


    var landingTab = "CreditCards";
    function backButtonConfig()
    {
        var currentTab;
        if (document.getElementById("payMethod").innerText.indexOf(" > ") > -1)
        {
            currentTab = document.getElementById("payMethodDisplay").innerText.split(" > ")[0];
        }
        else
        {
            currentTab = document.getElementById("payMethodDisplay").innerText;
        }
    }

    var availableCardType = [];
    var terminal;

    function cardInfo(method)
    {
        typeLabel = label;
        if (document.getElementById("VISA"))
        {
            availableCardType.push("VISA");
        }
        if (document.getElementById("MC"))
        {
            availableCardType.push("MC");
        }
        if (document.getElementById("AMEX"))
        {
            availableCardType.push("AMEX");
        }
        if (document.getElementById("DISC"))
        {
            availableCardType.push("DISC");
        }
        if (document.getElementById("DINER"))
        {
            availableCardType.push("DINER");
        }
        if (document.getElementById("JCB"))
        {
            availableCardType.push("JCB");
        }
        if (document.getElementById("MAESTRO"))
        {
            availableCardType.push("MAESTRO");
        }
        if (document.getElementById("INSTAPAYMENT"))
        {
            availableCardType.push("INSTAPAYMENT");
        }
        if (document.getElementById("RUPAY"))
        {
            availableCardType.push("RUPAY");
        }

        $("#CreditCardsOption").hide();
        $("#card").addClass('active');
    }


</script>
<script>
    var lablevalues = new Array();
    function ChangeFunction(Value, lable)
    {
        console.log("Value" + Value + "lable" + lable);
        var finalvalue = lable + "=" + Value;
        console.log("finalvalue" + finalvalue);
        lablevalues.push(finalvalue);
        console.log(lablevalues);
        document.getElementById("onchangedvalue").value = lablevalues;

        displayCheckoutPage(lable,Value);
    }
    function displayCheckoutPage(label,value)
    {
        if(label=="Is Pharma(Y/N)")
        {

        }
        else if(label=="Is Powered By Logo")
        {
            var powerByLogo = $("[name=isPoweredBy]").val();
            var partnerLogo = $("[name=ispartnerlogo]").val();
            var securityLogo = $("[name=isSecurityLogo]").val();
            if(powerByLogo == "Y")
            {
                $(".securityLogo").hide();
                $(".powerdByLogo").show();
                if(partnerLogo == "Y")
                {
                    $(".pciMainLogo").css("text-align", "center");
                }else
                {
                    $(".pciMainLogo").css("text-align", "left");
                }
            }
            else
            {
                $(".powerdByLogo").hide();
                if(securityLogo == "Y")
                {
                    $(".securityLogo").show();
                }
                if(partnerLogo == "Y")
                {
                    $(".pciMainLogo").css("text-align", "right");
                }else
                {
                    $(".pciMainLogo").css("text-align", "center");
                }

            }
        }
        else if(label=="Template")
        {
            var isTemplate = $("[name=template]").val();
            var partnertemplateValue = $("[id=partnertemplateValue]").val();
            var supportSection = $("[name=supportSection]").val();
            var consentSection = $("[name=consent]").val();
            var timeoutSection = $("[name=checkoutTimer]").val();

            var nameColor= "";
            if (isTemplate == "N" && partnertemplateValue=="N")
            {
                nameColor="default_";
            }
            else if(isTemplate == "Y" && partnertemplateValue=="N")
            {
                nameColor="";
            }
            else if(isTemplate == "Y" && partnertemplateValue=="Y")
            {
                nameColor="";
            }
            else if(isTemplate == "N" && partnertemplateValue=="Y")
            {
                nameColor="partner_";
            }
            else
            {
                nameColor="default_";
            }
            partnertemplateValue


           /* if (isTemplate == "Y")
            {*/
              console.log("inside checkout page+++"+isTemplate)
                var color1 = nameColor+"NEW_CHECKOUT_BODYNFOOTER_COLOR";
                var color1current = "display:none;";
                var color1current1 = "width: 100%;padding: 7px 12px;display: inline-flex;"
            if ($("#cardinfo").hasClass("activeTab"))
            {
                $(".tab-pane").attr('style','background-color:' + $("#" + color1).val() + ' !important;');
                $("#CreditCards").css("display","inline-block");
                $("#cardinfo").show();
                $("#personalinfo").hide();
            }
            else if ($("#personalinfo").hasClass("activeTab"))
            {
                $(".tab-pane").attr('style','background-color:' + $("#" + color1).val() + ' !important;');
                $("#CreditCards").css("display","inline-block");
                $("#cardinfo").hide();
                $("#personalinfo").show();
            }
            else
            {
                $(".tab-pane").attr('style','background-color:' + $("#" + color1).val() + ' !important;');
                $("#CreditCards").css("display","none");
            }
                $(".footer-tab").attr('style', 'background-color:' + $("#" + color1).val() + ' !important;');
                $("#personalinfo .form-control").attr('style', 'background-color:' + $("#" + color1).val() + ' !important;');
                $("#cardinfo .form-control").attr('style', 'background-color:' + $("#" + color1).val() + ' !important;');
            if($("#cardinfo").hasClass("activeTab"))
            {
                $("#cardinfo").hide();
                $("#personalinfo").show();
                $("#cardinfo").removeClass("activeTab");
                $("#personalinfo").addClass("activeTab");
                $("#payLabel").text("Next");
                $("#terms").hide();
            }
            else if($("#personalinfo").hasClass("activeTab"))
            {
                $("#payMethodDisplay").text("Select a Payment Method");
                $("#cardinfo").hide();
                $("#personalinfo").hide();
                $("#options").show();
                $("#personalinfo").removeClass("activeTab");
                $("#cardinfo").removeClass("activeTab");
                $("#backArrow").hide();
                $("#paybutton").hide();
                $("#terms").hide();
            }

                var color2 = nameColor+"NEW_CHECKOUT_HEADERBACKGROUND_COLOR";

                var color2current = $('#'+nameColor+'NEW_CHECKOUT_HEADER_FONT_COLOR').val();
                $(".header").attr('style', 'background-color:' + $("#" + color2).val() + ' !important;color:' + color2current + ' !important;');

                var color3 = nameColor+"NEW_CHECKOUT_HEADER_FONT_COLOR";

                var color3current = $('#'+nameColor+'NEW_CHECKOUT_HEADERBACKGROUND_COLOR').val();
                $(".header").attr('style', 'color:' + $("#" + color3).val() + ' !important;background-color:' + color3current + ' !important;');

                var color4 = nameColor+"NEW_CHECKOUT_NAVIGATIONBAR_COLOR"

                var color4current = $('#'+nameColor+'NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR').val();
                $(".top-bar").attr('style', 'background-color:' + $("#" + color4).val() + ' !important;color:' + color4current + ' !important;');
                var color5 = nameColor+"NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR"

                var color5current = $('#'+nameColor+'NEW_CHECKOUT_NAVIGATIONBAR_COLOR').val();
                //$(".top-bar").attr('style','color:'+$("#" + first).val()+' !important;');
                $(".top-bar").attr('style', 'color:' + $("#" + color5).val() + ' !important;background-color:' + color5current + ' !important;');

                var color6 = nameColor+"NEW_CHECKOUT_BUTTON_FONT_COLOR";

                var color6current = $('#'+nameColor+'NEW_CHECKOUT_BUTTON_COLOR').val();
                var color6current1 = "padding-top: 8px; padding-left: 5px; float: left; width: 20px; height: 36px; cursor: pointer;"
                $(".pay-button").attr('style', 'color:' + $("#" + color6).val() + ' !important;background-color:' + color6current + ' !important;');
                $(".prev-btn").attr('style', color6current1+'color:' + $("#" + color6).val() + ' !important;background-color:' + color6current + ' !important;');

                var color7 = nameColor+"NEW_CHECKOUT_BUTTON_COLOR";

                var color7current = $('#'+nameColor+'NEW_CHECKOUT_BUTTON_FONT_COLOR').val();
                $(".pay-button").attr('style', 'background-color:' + $("#" + color7).val() + ' !important;color:' + color7current + ' !important;');
                $(".prev-btn").attr('style', color6current1+'background-color:' + $("#" + color7).val() + ' !important;color:' + color7current + ' !important;');
                var color8 = nameColor+"NEW_CHECKOUT_FULLBACKGROUND_COLOR";
                var color8current = "font-family: Montserrat;"
                $(".background").attr('style', color8current + 'background-color:' + $("#" + color8).val() + ' !important;');

                var color9 = nameColor+"NEW_CHECKOUT_LABEL_FONT_COLOR";

                var color9current = "margin-left: 12px;margin-right: 12px;margin-bottom: 55px;"
                $(".label-style").attr('style', 'color:' + $("#" + color9).val() + ' !important;');
                $(".form-label").attr('style', 'color:' + $("#" + color9).val() + ' !important;');
            if(consentSection == "Y")
            {
                $(".terms-checkbox").attr('style', color9current + 'color:' + $("#" + color9).val() + ' !important;');
            }
                var color10 = nameColor+"NEW_CHECKOUT_BOX_SHADOW";

                $(".mainDiv").attr('style', 'box-shadow: 0px 0px 20px ' + $("#" + color10).val() + ' !important;');
                var color11 = nameColor+"NEW_CHECKOUT_TIMER_COLOR_picker";


                /*var current = "width: 100%;padding: 7px 12px;display: inline-flex;"*/
            if(timeoutSection == "Y")
            {
                $(".timeout").attr('style', 'color:' + $("#" + color11).val() + ' !important;');
            }
                var color12 = nameColor+"NEW_CHECKOUT_ICON_COLOR";

                $(".tab-icon").attr('style', 'color:' + $("#" + color12).val() + ' !important;');
            var color13 = nameColor+"NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR";
            var current = $('#'+nameColor+'NEW_CHECKOUT_FOOTER_FONT_COLOR').val();
            var current1 = "width: 100%;padding: 7px 12px;display: inline-flex;";
            console.log("color13++++"+color13);
            if($('#'+color13).val() != "")
            {
                $(".modal-footer").attr('style', current1 + 'background-color:' + $("#" + color13).val() + ' !important;');
            }else
            {
                $(".modal-footer").attr('style', current1 + 'background-color:#fff !important;');
            }
            //$(".modal-footer").attr('style',current1+'background-color:'+$("#" + color13).val()+' !important;');
            if(supportSection == "Y")
            {
                if($('#'+color13).val() != "")
                {
                    $(".merchant-details").attr('style', 'background-color:' + $("#" + color13).val() + ' !important;color:' + current + ' !important;');
                }else{
                    $(".merchant-details").attr('style', 'background-color:#3583d2 !important;color:' + current + ' !important;');
                }

            }
        }
        else if(label=="Is Merchant Logo")
        {
            var partnerLogo = $("[name=ispartnerlogo]").val();
            var merchantLogo = $("[name=ismerchantlogo]").val();
            if(merchantLogo == "Y")
            {
                $(".logoImage1").show();
                $(".logoImage2").hide();
            }
            else
            {
                if(partnerLogo == "Y")
                {
                    $(".logoImage1").hide();
                    $(".logoImage2").show();
                }
                else
                {
                    $(".logoImage1").hide();
                    $(".logoImage2").hide();
                    $(".paymentzMainLogo").hide();
                }
            }
        }
        else if(label=="Is Security Logo")
        {
            var powerByLogo = $("[name=isPoweredBy]").val();
            var securityLogo = $("[name=isSecurityLogo]").val();
            if(securityLogo == "Y")
            {
                if(powerByLogo != "Y")
                {
                    $(".securityLogo").show();
                }

            }
            else
            {
                $(".securityLogo").hide();
            }
        }
        else if(label=="PCI Logo")
        {
            var pciLogo = $("[name=ispcilogo]").val();
            if(pciLogo == "Y")
            {
                $(".pciMainLogo").show();
            }
            else
            {
                $(".pciMainLogo").hide();
            }
        }
        else if(label=="Partner Logo")
        {
            var partnerLogo = $("[name=ispartnerlogo]").val();
            var merchantLogo = $("[name=ismerchantlogo]").val();
            var PCILogo = $("[name=ispcilogo]").val();
            var powerByLogo = $("[name=isPoweredBy]").val();

            if(partnerLogo == "Y")
            {
                if(merchantLogo == "Y")
                {
                    console.log("inside logo m if+++");
                    $(".logoImage1").show();
                    $(".logoImage2").hide();
                    $(".paymentzMainLogo").show();
                }
                else
                {
                    console.log("inside logo m else+++");
                    $(".logoImage1").hide();
                    $(".logoImage2").show();
                    $(".paymentzMainLogo").show();
                }
                if(powerByLogo == "Y")
                {
                    $(".pciMainLogo").css("text-align", "center");
                }else
                {
                    $(".pciMainLogo").css("text-align", "right");
                }
            }
            else
            {
                if(merchantLogo == "Y")
                {
                    $(".logoImage1").show();
                    $(".paymentzMainLogo").hide();
                    $(".logoImage2").hide();
                }
                else
                {
                    $(".logoImage1").hide();
                    $(".logoImage2").hide();
                    $(".paymentzMainLogo").hide();

                }
                $(".pciMainLogo").css("text-align","left");
            }
        }
        /*  else if(label=="Visa Secure Logo")
         {

         }
         else if(label=="isMerchantLogoBO")
         {

         }
         else if(label=="Master Secure Logo")
         {

         }*/
          else if(label=="Support Section")
         {
             var supportSection = $("[name=supportSection]").val();
             var supportNoNeeded = $("[name=supportNoNeeded]").val();
             if(supportSection == "Y")
             {
                 $(".merchant-details ").show();
                 $("[name=supportNoNeeded]").prop('readonly', true);
                 if(supportNoNeeded == "Y")
                 {
                     $(".supportTelNo").css("visibility","inherit");
                 }
                 else
                 {
                     $(".supportTelNo").css("visibility","hidden");
                 }
             }
             else
             {
                 $(".merchant-details ").hide();
                 $("[name=supportNoNeeded]").prop('readonly', false);
             }
         }
        else if(label=="Support Number Needed")
        {
            var supportSection = $("[name=supportNoNeeded]").val();
            if(supportSection == "Y")
            {
                $(".supportTelNo").css("visibility","inherit");
            }
            else
            {
                $(".supportTelNo").css("visibility","hidden");
            }
        }

        else if(label=="Consent")
        {
            var consentDiv = $("[name=consent]").val();
            var first = "NEW_CHECKOUT_LABEL_FONT_COLOR";
            if(consentDiv == "Y")
            {
                $("#terms").css("visibility","inherit");
                $(".terms-checkbox").attr('style', 'color:' + $("#" + first).val() + ' !important;');

            }
            else
            {
                $("#terms").css("visibility","hidden");
            }
        }
        else if(label=="Checkout Timer")
        {
            var checkoutTimer = $("[name=checkoutTimer]").val();
            var first = "NEW_CHECKOUT_TIMER_COLOR";
            if(checkoutTimer == "Y")
            {
                $(".timeout").css("visibility","inherit");
                $(".timeout").attr('style', 'color:' + $("#" + first).val() + ' !important;');

            }
            else
            {
                $(".timeout").css("visibility","hidden");
            }
        }
        else if(label=="Checkout Timer Time")
        {
            console.log("Value Timer++++"+value)
            $("#timer").text(value);
            /*if(checkoutTimerTime == "Y")
             {
             $("#terms").show();
             }
             else
             {
             $("#terms").hide();
             }*/
        }
    }

    function change(dropdown, input)
    {
        var val = dropdown.options[dropdown.selectedIndex].value;
        if (val.trim() === 'N')
        {

            document.getElementById(input).disabled = true;

        }
        else
        {
            document.getElementById(input).disabled = false;
        }
    }
    function mySave()
    {
        document.getElementById('myformnameSave').submit();
    }

</script>
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }
%>

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