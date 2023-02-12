<%@ page import="java.util.List" %>
<%@ page import="com.vo.applicationManagerVOs.AppKycTemplateVO" %>
<%@ page import="com.dao.ApplicationManagerDAO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp"%>
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","appKycTemplate");
  PartnerFunctions functions = new PartnerFunctions();
  String partnerid="";
  if(session.getAttribute("merchantid")!=null && !session.getAttribute("merchantid").equals(""))
  {
    partnerid = (String)session.getAttribute("merchantid");
  }

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);


  String appKycTemplate_KYC_Upload_Selection_Form = rb1.getString("appKycTemplate_KYC_Upload_Selection_Form");
  String appKycTemplate_Document_Name = rb1.getString("appKycTemplate_Document_Name");
  String appKycTemplate_Criteria = rb1.getString("appKycTemplate_Criteria");
  String appKycTemplate_Memorandum = rb1.getString("appKycTemplate_Memorandum");
  String appKycTemplate_Mandatory = rb1.getString("appKycTemplate_Mandatory");
  String appKycTemplate_Optional = rb1.getString("appKycTemplate_Optional");
  String appKycTemplate_Certificate_of_Incorporation = rb1.getString("appKycTemplate_Certificate_of_Incorporation");
  String appKycTemplate_Share_Certificate = rb1.getString("appKycTemplate_Share_Certificate");
  String appKycTemplate_Processing = rb1.getString("appKycTemplate_Processing");
  String appKycTemplate_Business_License = rb1.getString("appKycTemplate_Business_License");
  String appKycTemplate_Bank_statement = rb1.getString("appKycTemplate_Bank_statement");
  String appKycTemplate_Bank_reference = rb1.getString("appKycTemplate_Bank_reference");
  String appKycTemplate_Proof = rb1.getString("appKycTemplate_Proof");
  String appKycTemplate_Proof_of_address = rb1.getString("appKycTemplate_Proof_of_address");
  String appKycTemplate_Cross_Corporate_Guarantee = rb1.getString("appKycTemplate_Cross_Corporate_Guarantee");
  String appKycTemplate_Save = rb1.getString("appKycTemplate_Save");



%>
<%
  response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP 1.0
  response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

  <title><%=company%> Application Manager> Kyc configuration</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <link rel="manifest" href="manifest.json">

  <meta name="theme-color" content="#b3d234">

  <link rel="stylesheet" href="/partner/NewCss/kycCSS/pricing.css">

  <script src="/partner/NewCss/kycCSS/cleverTap.js"></script>
<%--  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>

  <!--Import Google Icon Font-->
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <!--Import materialize.css-->
  <link type="text/css" rel="stylesheet" href="/partner/NewCss/kycCSS/materialize.css"  media="screen,projection"/>
  <%--<script src="/partner/NewCss/App_Manager_Style/JS/jquery-3.2.1.min.js"></script>
  <script type="text/javascript" src="/partner/NewCss/App_Manager_Style/materialize/js/materialize.js"></script>--%>

  <%--  <script src="/partner/NewCss/App_Manager_Style/JS/main.js"></script>--%>

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

    function nochar (event) {
      if ((event.which >= 65 && event.which < 91) || (event.which > 96 && event.which < 123) || event.which === 32 || event.which===0 || event.which == 8)
      {
        return true;
      }
      else
      {
        event.preventDefault();
      }
    };

  </script>



  <style>

    input[type=radio]:checked ~ label{
      color: #7b7b7b;
    }

    .radio_center>label{
      color: rgba(0,0,0,0.38);
      font-size: 16px;
      font-weight: inherit;
    }


    form p.radio_center{
      text-align: center!important;
    }

    .form-group>label{
      margin-bottom: 30px;
      color: rgba(0,0,0,0.38);
      font-size: 16px;
      font-weight: inherit;
      display: block;
    }

    .form-control{
      color: #7b7b7b;
    }

    .pricing-info{
      padding: 30px 6% 30px!important;
    }

    .select-wrapper .caret{
      border-right: none!important;
      border-left: none!important;
    }

    .secondary-content{
      float: initial!important;
      -webkit-transition: .28s ease;
      transition: .28s ease;
      color: #26a69a!important;
    }


    select:focus {
      outline: none!important;
      border-bottom: 2px solid #26a69a;
    }

    select{
      border: none;
      border-bottom: 1px solid #9e9e9e;
    }

    .first-section h1{
      font-size: 21px;
    }

    #info_id{
      font-size: 12px;
      margin: 0 0 10px 0;
      color: #ec8720;
    }

    /*  input:not([type]), input[type=text]:not(.browser-default), input[type=password]:not(.browser-default), input[type=email]:not(.browser-default), input[type=url]:not(.browser-default), input[type=time]:not(.browser-default), input[type=date]:not(.browser-default), input[type=datetime]:not(.browser-default), input[type=datetime-local]:not(.browser-default), input[type=tel]:not(.browser-default), input[type=number]:not(.browser-default), input[type=search]:not(.browser-default), textarea.materialize-textarea{
        margin: 0!important;
      }*/

    .bg-info{
      padding: 15px;
      margin-bottom: 0px;
      border: 1px solid transparent;
      border-radius: 4px;
      color: #945c09;
      background-color: #FFE0B2!important;
      border-color: #FFE0B2!important;
      border-left: 4px solid #FF9800!important;
      font-family: "Open Sans";
      font-size: 15px;
      font-weight: 600;
      text-align: center;
    }

    .checkbox_class{
      display: flex;
      margin-top: 2rem;
      position: relative;
    }

    .textbox_class{
      margin-top: 2rem;
      position: relative;
    }

    .textbox_class input{
      margin: 1px;
    }

    .checkbox_class label{
      left: 0.75rem;
    }


    /***************Nikhil Css******************/

    .container{
      width: auto;
    }

    .logo h1 {
      margin: 10px auto;
      height: 50px;
      text-align: center;
    }


    h1 textarea{
      padding: 0 20px;
    }

    textarea.materialize-textarea{
      padding: 0 10px;
      margin-top: -6px;
      font-size: 1.4rem;
      overflow-y: auto;
    }

    .navbar-default {
      background-color: inherit;
      border-color: inherit;
    }

    .navbar-default .navbar-nav>li>a{
      color: #ffffff!important;
    }

    .topbar {
      background-color: #303030 !important;
    }

    .navbar-default {
      border: none;
      border-bottom: 4px solid #1e8b92!important;
    }

    .topbar-left {
      background: #1e8b92 !important;
    }


    .wrapmenu.enlarged #buttonid {
      margin-left: 30px;
    }

    .wrapmenu #buttonid {
      position: fixed;
      padding: 10px;
      background-color: #e2e2e2;
      bottom: -15px;
      width: 100%;
      margin-left: 130px;
    }

    .topbar-profile strong{
      font-weight: 700;
    }

    button:focus{
      background-color: inherit;
    }

    hr.divider{
      height: 0;
      overflow: inherit;
      background-color: inherit;
    }

    .input-field {
      position: relative;
    }

    textarea.text-questions{
      font-size: 1.1rem!important;
      margin-bottom: 0!important;
      text-align: center!important;
    }

    .form-group>label{
      line-height: 20px;
      padding-left: 20px;
    }


    @media (max-width: 767px){
      .navbar-nav {
        margin: 0;
      }

      .navbar-nav > li {
        display: inline-block!important;
      }

      textarea.materialize-textarea {
        padding: 0 10px;
        margin-top: -38px;
        font-size: 1.3rem;
      }

      textarea.text-questions{
        padding: 0px 6px!important;
        margin-top: 0px!important;
        overflow-x: hidden;
      }

      .form-group>label{
        line-height: 20px;
        padding-left: 15px;
      }

      .content-page > .content {
        padding: 20px 0;
      }

    }


    .change_text {
      display: block;
    }

    .change_text_mob {
      display: none;
    }

    @media(max-width: 600px){
      .change_text_mob {
        display: block;
        margin-top: 20px;
      }

      .change_text {
        display: none;
      }
      /*      .change_text:after {
              content: 'Please check those you use and What %age of orders use this method?';
              text-align: center;
            }*/

      #percentage_mob_hide{
        display: none;
      }

    }


    .websitedetailsid label{
      font-size: 14px;
    }

    input[type=radio], input[type=checkbox]{
      transform: scale(2);
      -ms-transform: scale(2);
      -webkit-transform: scale(2);
      padding: 10px;
    }


    #payments_section .col.m6{
      padding: 7px;
    }

    /*****************************KYC PAGE CSS*******************************/

    .section_class{
      display: inherit;
      width: 100%;
      border-bottom: 1px solid #c8c8c8;
      padding-bottom: 20px;
    }

    #title1 p{
      float:left;
      font-weight:bold;
      font-size: 16px;
    }

    #title2 p{
      text-align:center;
      font-weight:bold;
      font-size: 16px;
    }

    @media(max-width: 600px){
      #title1 p{
        text-align: center;
        font-weight:bold;
      }

      #title2{
        display: none;
      }

      .section_class .textbox_class:nth-of-type(2){
        padding: 15px 0 0 40px;
        display: flex;
      }

      .section_class .textbox_class:nth-of-type(3){
        padding: 0 0 0 40px;
        display: flex;
        margin-top: 1rem!important;
      }


    }


  </style>

  <script>

    $(document).ready(function(){

      //OnClick Slides (KYC Upload Selection Form)

      $("input[name='kyc']").on("change", function()
      {
        if ($('input:checkbox[name=kyc]:checked').val() == "Y") {
          document.getElementById("textarea_kyc").disabled = false;
          $('#textarea_kyc').css( 'cursor', 'auto');
          $('#textarea_kyc').css( 'opacity', '1');

          $('#turnover_kyc').css( 'opacity', '1');
          $('#turnover_kyc').css( 'pointer-events', 'inherit');
          $('#turnover_kyc').parent().css( 'cursor', 'auto');
        }
        else
        {
          document.getElementById("textarea_kyc").disabled = true;
          $('#textarea_kyc').css( 'cursor', 'not-allowed');
          $('#textarea_kyc').css( 'opacity', '0.5');

          $('#turnover_kyc').css( 'opacity', '0.5');
          $('#turnover_kyc').css( 'pointer-events', 'none');
          $('#turnover_kyc').parent().css( 'cursor', 'not-allowed');
        }
      });

      //OnLoad Slides (KYC Upload Selection Form)

      if ($('input:checkbox[name=kyc]:checked').val() == "Y") {



        document.getElementById("textarea_kyc").disabled = false;
        $('#textarea_kyc').css( 'cursor', 'auto');
        $('#textarea_kyc').css( 'opacity', '1');

        $('#turnover_kyc').css( 'opacity', '1');
        $('#turnover_kyc').css( 'pointer-events', 'inherit');
        $('#turnover_kyc').parent().css( 'cursor', 'auto');

      }
      else
      {

        document.getElementById("textarea_kyc").disabled = true;
        $('#textarea_kyc').css( 'cursor', 'not-allowed');
        $('#textarea_kyc').css( 'opacity', '0.5');

        $('#turnover_kyc').css( 'opacity', '0.5');
        $('#turnover_kyc').css( 'pointer-events', 'none');
        $('#turnover_kyc').parent().css( 'cursor', 'not-allowed');

      }

      /**********************************************************************************************/



    });


  </script>


  <%--<link href="css/bootstrap-multiselect.css" rel="stylesheet" type="text/css" />
  <script src="js/bootstrap-multiselect.js" type="text/javascript"></script>--%>
</head>


<body>

<!--[if lt IE 8]>
<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->

<%
  ApplicationManagerDAO applicationManagerDAO = new ApplicationManagerDAO();

  List<AppKycTemplateVO> kycTemplateVOs = applicationManagerDAO.gettemplatedetails(partnerid);
  String aChecked = "";
  String bChecked = "";
  String cChecked = "";
  String dChecked = "";
  String eChecked = "";
  String fChecked = "";
  String gChecked = "";
  String hChecked = "";
  String iChecked = "";
  String jChecked = "";

  String maChecked = "";
  String mbChecked = "";
  String mcChecked = "";
  String mdChecked = "";
  String meChecked = "";
  String mfChecked = "";
  String mgChecked = "";
  String mhChecked = "";
  String miChecked = "";
  String mjChecked = "";

  String maaChecked = "";
  String mbbChecked = "";
  String mccChecked = "";
  String mddChecked = "";
  String meeChecked = "";
  String mffChecked = "";
  String mggChecked = "";
  String mhhChecked = "";
  String miiChecked = "";
  String mjjChecked = "";

  for (AppKycTemplateVO appKycTemplateVO : kycTemplateVOs){
    if (appKycTemplateVO.getLabelName().equals("Memorandum Article of Association"))
    {
      aChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        maChecked="checked";
      }
      else{
        maaChecked="checked";
      }
    }
    if (appKycTemplateVO.getLabelName().equals("Certificate of Incorporation"))
    {
      bChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mbChecked="checked";
      }
      else{
        mbbChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Share Certificate"))
    {
      cChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mcChecked="checked";
      }
      else {
        mccChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Processing history for last 6 months"))
    {
      dChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mdChecked="checked";
      }
      else{
        mddChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Business License/Commercial License"))
    {
      eChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        meChecked="checked";
      }
      else{
        meeChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Bank statement of company for last 3 months"))
    {
      fChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mfChecked="checked";
      }
      else {
        mffChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Bank reference letter for company"))
    {
      gChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mgChecked="checked";
      }
      else {
        mggChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Proof Of Identity Of Owner"))
    {
      hChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mhChecked="checked";
      }
      else {
        mhhChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Proof of address"))
    {
      iChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        miChecked="checked";
      }
      else {
        miiChecked="checked";
      }
    }

    if (appKycTemplateVO.getLabelName().equals("Cross Corporate Guarantee"))
    {
      jChecked = "checked";
      if ("Y".equals(appKycTemplateVO.getCriteria())){
        mjChecked="checked";
      }
      else {
        mjjChecked="checked";
      }
    }
  }
%>
<%=functions.isValueNull(maChecked)&&functions.isValueNull(maaChecked)?"checked":""%>

<form action="/partner/net/AppKycTemplate?ctoken=<%=ctoken%>" method="post" name="form1" onsubmit="return check();" class="form-horizontal" style="padding: 10px;">
  <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
  <input  name="partnerid" type="hidden" value="<%=partnerid%>">

  <div class="content-page" style="background-color: #f3f3f3;">
    <div class="content">
      <div class="page-heading">

        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">
              <div class="widget-content padding">
                <div id="horizontal-form">

                  <article class="first-section" style="padding-bottom: 0px;min-height:0px;background: #ffffff;padding-top: 20px;">
                    <%--<div class="container">--%>

                    <%--<div class="col s12">
                      <h5 class="bg-info">*Login Name already exist, please try different Name.</h5>
                    </div>--%>



                    <div class="divs" id="group" value="1" name="currentPageNO" style="margin-bottom: 40px;/*margin-left: 240px;overflow: hidden;*/position: relative;">

                      <div class="cls9">
                        <section class="row">
                          <section class="col-md-12 text-center">
                            <input class="with-gap" name="kyc" type="checkbox" id="kyc" value="Y" checked />
                            <label for="kyc" style="width: 100%; height: inherit;"><h1 align="center"><%--<textarea> What kind of business have you set up?</textarea>--%><textarea id="textarea_kyc" class="materialize-textarea"><%=appKycTemplate_KYC_Upload_Selection_Form%></textarea></h1></label>


                            <section class="pricing-info" style="min-height: 50px;display: inline-block; width: 100%;max-width: inherit;">
                              <%
                                String successMsg = (String) request.getAttribute("message");
                                if (functions.isValueNull(successMsg)) {
                                  out.println("<h5 class=\"bg-info\" style=\"text-align: center;background-color: #d0f1e4 !important;\n" +
                                          "border-color: #d0f1e4 !important;\n" +
                                          "    border-left-color: rgb(208, 241, 228);\n" +
                                          "border-left: 4px solid #53a585 !important;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + successMsg + "</h5>");
                                }
                              %>
                              <section class="form-group" id="turnover_kyc" style="display: inherit; margin-top: 10px;width: 100%;">
                                <section class="input-field col s12 m6" id="title1">
                                  <p><%=appKycTemplate_Document_Name%></p>
                                </section>

                                <section class="input-field col s12 m6" id="title2">
                                  <p><%=appKycTemplate_Criteria%></p>
                                </section>
                                <%-- <section class="input-field col s6 m3">
                                 <p style="float:left; font-weight:bold;">Optional</p>
                               </section>--%>

                                <!--*****************Memorandum Article of Association********************-->

                                <section class="section_class">
                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="memorandum_doc" type="checkbox" id="memorandum_doc" value="Memorandum Article of Association" <%=aChecked%>/>
                                    <label for="memorandum_doc"><%=appKycTemplate_Memorandum%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="memorandum_criteria" type="radio" id="memorandum_mandatory" value="Y" <%=maChecked%> />
                                    <label for="memorandum_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="memorandum_criteria" type="radio" id="memorandum_optional" value="N" <%=maaChecked%> <%= "".equals(maChecked) && "".equals(maaChecked)?"checked":""%>/>
                                    <label for="memorandum_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>

                                <!--*****************Certificate of Incorporation********************-->

                                <section class="section_class">
                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="incorporation_doc" type="checkbox" id="incorporation_doc" value="Certificate of Incorporation" <%=bChecked%>/>
                                    <label for="incorporation_doc"><%=appKycTemplate_Certificate_of_Incorporation%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="incorporation_criteria" type="radio" id="incorporation_mandatory" value="Y" <%=mbChecked%>/>
                                    <label for="incorporation_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="incorporation_criteria" type="radio" id="incorporation_optional" value="N" <%=mbbChecked%> <%= "".equals(mbChecked) && "".equals(mbbChecked)?"checked":""%>/>
                                    <label for="incorporation_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>

                                <!--*****************Share Certificate********************-->

                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="share_doc" type="checkbox" id="share_doc" value="Share Certificate" <%=cChecked%>/>
                                    <label for="share_doc"><%=appKycTemplate_Share_Certificate%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="share_criteria" type="radio" id="share_mandatory" value="Y" <%=mcChecked%>/>
                                    <label for="share_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="share_criteria" type="radio" id="share_optional" value="N" <%=mccChecked%> <%= "".equals(mcChecked) && "".equals(mccChecked)?"checked":""%>/>
                                    <label for="share_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>


                                <!--*****************Processing history for last 6 months********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="processhistory_doc" type="checkbox" id="processhistory_doc" value="Processing history for last 6 months" <%=dChecked%>/>
                                    <label for="processhistory_doc"><%=appKycTemplate_Processing%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="processhistory_criteria" type="radio" id="processhistory_mandatory" value="Y" <%=mdChecked%>/>
                                    <label for="processhistory_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="processhistory_criteria" type="radio" id="processhistory_optional" value="N" <%=mddChecked%> <%= "".equals(mdChecked) && "".equals(mddChecked)?"checked":""%>/>
                                    <label for="processhistory_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>


                                <!--*****************Business License/Commercial License********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="license_doc" type="checkbox" id="license_doc" value="Business License/Commercial License"  <%=eChecked%>/>
                                    <label for="license_doc"><%=appKycTemplate_Business_License%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="license_criteria" type="radio" id="license_mandatory" value="Y" <%=meChecked%>/>
                                    <label for="license_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="license_criteria" type="radio" id="license_optional" value="N" <%=meeChecked%> <%= "".equals(meChecked) && "".equals(meeChecked)?"checked":""%>/>
                                    <label for="license_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>


                                <!--*****************Bank statement of company for last 3 months********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="bankstatement_doc" type="checkbox" id="bankstatement_doc" value="Bank statement of company for last 3 months"  <%=fChecked%>/>
                                    <label for="bankstatement_doc"><%=appKycTemplate_Bank_statement%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="bankstatement_criteria" type="radio" id="bankstatement_mandatory" value="Y" <%=mfChecked%>/>
                                    <label for="bankstatement_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="bankstatement_criteria" type="radio" id="bankstatement_optional" value="N" <%=mffChecked%> <%= "".equals(mfChecked) && "".equals(mffChecked)?"checked":""%>/>
                                    <label for="bankstatement_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>

                                <!--*****************Bank reference letter for company********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="bankreference_doc" type="checkbox" id="bankreference_doc" value="Bank reference letter for company"<%=gChecked%>/>
                                    <label for="bankreference_doc"><%=appKycTemplate_Bank_reference%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="bankreference_criteria" type="radio" id="bankreference_mandatory" value="Y" <%=mgChecked%>/>
                                    <label for="bankreference_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="bankreference_criteria" type="radio" id="bankreference_optional" value="N" <%=mggChecked%> <%= "".equals(mgChecked) && "".equals(mggChecked)?"checked":""%>/>
                                    <label for="bankreference_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>


                                <!--*****************Proof of Identity of owner********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="owneridentity_doc" type="checkbox" id="owneridentity_doc" value="Proof Of Identity Of Owner" <%=hChecked%>/>
                                    <label for="owneridentity_doc"><%=appKycTemplate_Proof%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="owneridentity_criteria" type="radio" id="owneridentity_mandatory" value="Y" <%=mhChecked%>/>
                                    <label for="owneridentity_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="owneridentity_criteria" type="radio" id="owneridentity_optional" value="N" <%=mhhChecked%> <%= "".equals(mhChecked) && "".equals(mhhChecked)?"checked":""%>/>
                                    <label for="owneridentity_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>


                                <!--*****************Proof of address********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="addressproof_doc" type="checkbox" id="addressproof_doc" value="Proof of address" <%=iChecked%>/>
                                    <label for="addressproof_doc"><%=appKycTemplate_Proof_of_address%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="addressproof_criteria" type="radio" id="addressproof_mandatory" value="Y" <%=miChecked%> />
                                    <label for="addressproof_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="addressproof_criteria" type="radio" id="addressproof_optional" value="N" <%=miiChecked%> <%= "".equals(miChecked) && "".equals(miiChecked)?"checked":""%>/>
                                    <label for="addressproof_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>


                                <!--*****************Cross Corporate Guarantee********************-->


                                <section class="section_class">

                                  <section class="checkbox_class col s12 m6" style="text-align: initial;display: inherit;">
                                    <input class="with-gap" name="crosscorporate_doc" type="checkbox" id="crosscorporate_doc" value="Cross Corporate Guarantee" <%=jChecked%>/>
                                    <label for="crosscorporate_doc"><%=appKycTemplate_Cross_Corporate_Guarantee%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="crosscorporate_criteria" type="radio" id="crosscorporate_mandatory" value="Y" <%=mjChecked%>/>
                                    <label for="crosscorporate_mandatory"><%=appKycTemplate_Mandatory%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                  <section class="textbox_class col s12 m3">
                                    <input class="with-gap" name="crosscorporate_criteria" type="radio" id="crosscorporate_optional" value="N" <%=mjjChecked%> <%= "".equals(mjChecked) && "".equals(mjjChecked)?"checked":""%>/>
                                    <label for="crosscorporate_optional"><%=appKycTemplate_Optional%></label>&nbsp;&nbsp;&nbsp;&nbsp;
                                  </section>

                                </section>
                              </section>
                            </section>
                          </section>
                        </section>
                      </div>
                    </div>
                    <%--</div>--%>
                  </article><!-- /.first-section -->
                </div>
                <div class="form-group col-md-12 has-feedback">
                  <center>
                    <label >&nbsp;</label>
                    <input type="hidden" value="1" name="step">
                    <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=appKycTemplate_Save%></button>
                  </center>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</form>
<%--
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
--%>
<script type="text/javascript" src="/partner/NewCss/kycCSS/materialize.js"></script>
</body>
</html>
<style>
  input:not([type]) + label:after, input[type=text]:not(.browser-default) + label:after, input[type=password]:not(.browser-default) + label:after, input[type=email]:not(.browser-default) + label:after, input[type=url]:not(.browser-default) + label:after, input[type=time]:not(.browser-default) + label:after, input[type=date]:not(.browser-default) + label:after, input[type=datetime]:not(.browser-default) + label:after, input[type=datetime-local]:not(.browser-default) + label:after, input[type=tel]:not(.browser-default) + label:after, input[type=number]:not(.browser-default) + label:after, input[type=search]:not(.browser-default) + label:after, textarea.materialize-textarea + label:after, .select-wrapper + label:after {
    display: block;
    content: "";
    position: absolute;
    top: 139%;
    left: 0;
    opacity: 0;
    -webkit-transition: .2s opacity ease-out, .2s color ease-out;
    transition: .2s opacity ease-out, .2s color ease-out;
    width: 121%;
  }
</style>
