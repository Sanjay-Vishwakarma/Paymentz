<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 2/13/2017
  Time: 1:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.ResourceBundle" %>

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
  .mainbackgroundcolor
  {
  <%=session.getAttribute("mainbackgroundcolor")!=null?"background:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
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
  final String CRE_LANGUAGE=rb.getString("CRE_LANGUAGE");
  final String CRE_BANKACCOUNTDETAILS=rb.getString("CRE_BANKACCOUNTDETAILS");

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  String cardType = getValue(standardKitValidatorVO.getCardType());
  String email = getValue(genericAddressDetailsVO.getEmail());
  String city = getValue(genericAddressDetailsVO.getCity());
  String street = getValue(genericAddressDetailsVO.getStreet());
  String zip = getValue(genericAddressDetailsVO.getZipCode());
  String state = getValue(genericAddressDetailsVO.getState());
  String country = getValue(genericAddressDetailsVO.getCountry());
  String firstname = getValue(genericAddressDetailsVO.getFirstname());
  String lastname = getValue(genericAddressDetailsVO.getLastname());
  String telno = getValue(genericAddressDetailsVO.getPhone());
  String telnocc = getValue(genericAddressDetailsVO.getTelnocc());

%>

<style>
  @media(max-width:768px)
  {
    #h2carddetails
    {

      margin-bottom: 5px;
      margin-top: 5px;
    }
    #headtag
    {
      margin-left: -15px;
      width: 135%;

    }
    #creditlyfield
    {
      margin-left: -53px;
    }



  }
  @media(min-width:768px)
  {
    #headtag
    {
      margin-left: -15px;
      margin-top: 13px;
      width: 61.333333%;
      margin-bottom: 15px;
    }
    #inputgrp
    {
      width:100%;
    }
    #h2carddetails
    {
      margin-left: -16px;
      font-size: 14px ;

    }
    #creditlyfield
    {
      margin-left: -30px;
    }
    #telnocc
    {width: 80%;}
  }

</style>
<section class="creditly-wrapper" style="margin-bottom: -46px;">
  <div class="credit-card-wrapper"  id="creditlyfield">
    <div class="first-row form-group" id="form-group">
      <div class=" form-group col-md-12 controls">
<div class="col-md-12 controls portlets ui-sortable" id="headtag">
  <label class="headpanelfont_color"><%=CRE_BANKACCOUNTDETAILS%></label>

</div>
<div class="col-md-12 controls cont portlets ui-sortable">

  <div class="form-group col-md-6 controls">
    <label for="email" class="headpanelfont_color">Email ID</label>
    <input type="text" name="emailaddr" id="email" size="30" placeholder="Email ID" value="<%=email%>" class="form-control textbox_color"/>
  </div>

      <div class="col-md-3 controls" id="col-3" style="padding: 0px 15px;padding-bottom: 15px;">
        <label class="control-label headpanelfont_color"><%=CRE_LANGUAGE%></label>
        <select id="language" name="language" class="form-control textbox_color" style="width: auto;">
          <option VALUE="da_DK" selected>Danish</option>
          <option VALUE="de_DE">German</option>
          <option VALUE="el_GR">Greek</option>
          <option VALUE="en_US">English</option>
          <option VALUE="es_ES">Spanish</option>
          <option VALUE="fr_FR">French</option>
          <option VALUE="it_IT">Italian</option>
          <option VALUE="ja_JP">Japanese</option>
          <option VALUE="ko_KR">Korean</option>
          <option VALUE="no_NO">Norwegian</option>
          <option VALUE="pl_PL">Polish</option>
          <option VALUE="pt_PT">Portuguese</option>
          <option VALUE="ru_RU">Russian</option>
          <option VALUE="sv_SE">Swedish</option>
          <option VALUE="tr_TR">Turkish</option>
        </select>
      </div>




</div>
      <input type="hidden" name="cardtype" value="<%=cardType%>">
      <input type="hidden" name="emailaddr" value="<%=email%>">
      <input type="hidden" name="street" value="<%=street%>">
      <input type="hidden" name="city" value="<%=city%>">
      <input type="hidden" name="zip" value="<%=zip%>">
      <input type="hidden" name="state" value="<%=state%>">
      <input type="hidden" name="countrycode" value="<%=country%>">
      <input type="hidden" name="firstname" value="<%=firstname%>">
      <input type="hidden" name="lastname" value="<%=lastname%>">
      <input type="hidden" name="telnocc" value="<%=telnocc%>">
      <input type="hidden" name="telno" value="<%=telno%>">
      <input type="hidden" name="key" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getKey()%>">
      <input type="hidden" name="redirecturl" value="<%=standardKitValidatorVO.getTransDetailsVO().getRedirectUrl()%>">
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