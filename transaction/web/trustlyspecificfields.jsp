<%--
  Created by IntelliJ IDEA.
  User: uday
  Date: 10/7/14
  Time: 4:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>

<%
  ResourceBundle rb = null;
  //zh-CN;q=0.8,en-US;q=0.5,en;q=0.3
  String multiLanguage = request.getHeader("Accept-Language");
  String sLanguage[] = multiLanguage.split(",");
  if("zh-CN".contains(sLanguage[0]))
  {
    rb = LoadProperties.getProperty("com.directi.pg.creditpage", "zh");
  }
  else
  {
    rb = LoadProperties.getProperty("com.directi.pg.creditpage");
  }
  final String CRE_FIRSTNAME=rb.getString("CRE_FIRSTNAME");
  final String CRE_LASTNAME=rb.getString("CRE_LASTNAME");

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

  final String CRE_BILLINGADDRESSFORBANK=rb.getString("CRE_BILLINGADDRESSFORBANK");

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  String firstname = getValue(genericAddressDetailsVO.getFirstname());
  String lastname = getValue(genericAddressDetailsVO.getLastname());
  String email = getValue(genericAddressDetailsVO.getEmail());
  String city = getValue(genericAddressDetailsVO.getCity());
  String street = getValue(genericAddressDetailsVO.getStreet());
  String zip = getValue(genericAddressDetailsVO.getZipCode());
  String state = getValue(genericAddressDetailsVO.getState());
  String country = getValue(genericAddressDetailsVO.getCountry());
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
      margin-left: -22px;
      width: 135%;

    }
    #creditlyfield
    {
      margin-left: -53px;
    }
    #formfield
    {
      margin-left: -20px;
      width: 130%;
    }



  }
  @media(min-width:768px)
  {
    #headtag
    {
      margin-left: -15px;
      margin-top: 13px;
      width: 100%;
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
    #formfield
    {
      margin-left: -15px;
    }
  }

</style>
<section class="creditly-wrapper" style="margin-bottom: -46px;">
  <div class="credit-card-wrapper"  id="creditlyfield">
    <div class="first-row form-group" id="form-group">
      <div class=" form-group col-md-12 controls">
        <div class="col-md-12 portlets ui-sortable" id="headtag">
          <label><%=CRE_BILLINGADDRESSFORBANK%></label>

        </div>
        <div class="col-md-12 controls portlets ui-sortable" id="formfield">


          <div class="form-group col-md-6 controls" >
            <label for="firstname"><%=CRE_FIRSTNAME%></label>
            <input type="text"  size="30" class="form-control textbox_color" id="firstname" name="firstname" placeholder="<%=CRE_FIRSTNAME%>" value="<%=firstname%>">
          </div>
          <div class="form-group col-md-6 controls">
            <label for="lastname"><%=CRE_LASTNAME%></label>
            <input type="text" class="form-control textbox_color"  id="lastname" name="lastname" size="30" placeholder="<%=CRE_LASTNAME%>" value="<%=lastname%>">
          </div>

          <div class="form-group col-md-6 controls">
            <label for="emailaddr"><%=CRE_EMAIL%></label>
            <input type="text" name="emailaddr" id="emailaddr" class="form-control textbox_color"  size="30" placeholder="<%=CRE_EMAIL%>" value="<%=email%>"/>
          </div>
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