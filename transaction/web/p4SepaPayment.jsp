<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 17/10/2015
  Time: 12:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
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
</style>

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
  final String CRE_ACCOUNTHOLDERFIRSTNAME=rb.getString("CRE_ACCOUNTHOLDERFIRSTNAME");
  final String CRE_ACCOUNTHOLDERLASTNAME=rb.getString("CRE_ACCOUNTHOLDERLASTNAME");

  final String CRE_IBAN=rb.getString("CRE_IBAN");
  final String CRE_BIC=rb.getString("CRE_BIC");
  final String CRE_MANDATE=rb.getString("CRE_MANDATE");
  final String CRE_MANDATEID=rb.getString("CRE_MANDATEID");
  final String CRE_P4NOTE=rb.getString("CRE_P4NOTE");

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

  final String CRE_BANKACCOUNTDETAILS=rb.getString("CRE_BANKACCOUNTDETAILS");
  final String CRE_BILLINGADDRESSFORBANK=rb.getString("CRE_BILLINGADDRESSFORBANK");

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
  String fileName = getValue((String) session.getAttribute("filename"));
  String email = getValue(genericAddressDetailsVO.getEmail());
  String city = getValue(genericAddressDetailsVO.getCity());
  String street = getValue(genericAddressDetailsVO.getStreet());
  String zip = getValue(genericAddressDetailsVO.getZipCode());
  String state = getValue(genericAddressDetailsVO.getState());
  String country = getValue(genericAddressDetailsVO.getCountry());
  String telno = getValue(genericAddressDetailsVO.getPhone());
  String telnocc = getValue(genericAddressDetailsVO.getTelnocc());
  String TMPL_COUNTRY = getValue(genericAddressDetailsVO.getCountry());
  String firstname = getValue(genericAddressDetailsVO.getFirstname());
  String lastname = getValue(genericAddressDetailsVO.getLastname());
  String addressDetails = getValue(standardKitValidatorVO.getTerminalVO().getAddressDetails());
  String addressValidation = getValue(standardKitValidatorVO.getTerminalVO().getAddressValidation());
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

    }
    #creditlyfield
    {
      margin-left: -53px;
    }
    #formfield
    {
      margin-left: -22px;
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
    #formfield
    {
      margin-left: -21px;
    }
  }

</style>
<section class="creditly-wrapper" style="margin-bottom: -46px;">
  <div class="credit-card-wrapper"  id="creditlyfield">
    <div class="first-row form-group" id="form-group">
      <div class=" form-group col-md-12 controls">
<div class="col-md-12 controls portlets ui-sortable" id="headtag">
  <label><%=CRE_BANKACCOUNTDETAILS%></label>

</div>
<div class="col-md-12 controls portlets ui-sortable" id="formfield">
    <div class="form-group col-md-6 controls">
  <label ><%=CRE_ACCOUNTHOLDERFIRSTNAME%></label>
  <input  class="form-control textbox_color"   name="firstname" style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=firstname%>" >
</div>
<div class="form-group col-md-6 controls">
  <label ><%=CRE_ACCOUNTHOLDERLASTNAME%></label>
  <input  class="form-control textbox_color"  name="lastname" id="lastname" style="border: 1px solid #b2b2b2;font-weight:bold"  value="<%=lastname%>" >
</div>
<div class="form-group col-md-6 controls" id="iban">
  <label ><%=CRE_IBAN%></label>
  <input class="form-control textbox_color"  name="iban" style="border: 1px solid #b2b2b2;font-weight:bold"  >
</div>
<div class="form-group col-md-6 controls" id="bic">
  <label ><%=CRE_BIC%></label>
  <input type="text" class="form-control textbox_color"   name="bic" style="border: 1px solid #b2b2b2;font-weight:bold"   >
</div>
<div class="form-group col-md-12 controls" style="display: none;" id="mandateId">
  <label ><%=CRE_MANDATE%>&nbsp;<%=CRE_MANDATEID%></label>
  <input type="text" class="form-control textbox_color" name="mandateId"   style="border: 1px solid #b2b2b2;font-weight:bold"  placeholder="<%=CRE_MANDATE%> <%=CRE_MANDATEID%>">
</div>
<div class="form-group col-md-12 controls" align="center">
  <input type="checkbox" id="mandate" value="tc" name="mandate" onclick='mandateCHECK(this.checked)'>&nbsp;<b style="color: black"><%=CRE_P4NOTE%>.</b><br><br>
</div>


  <div class="col-md-12 controls portlets ui-sortable">
    <label><%=CRE_BILLINGADDRESSFORBANK%></label>

  </div>

<%if(fileName!=null){%>
<jsp:include page="<%=fileName%>"></jsp:include>
<%}%>

<%

  if(addressDetails.equalsIgnoreCase("Y") || /*addressValidation.equalsIgnoreCase("Y") ||*/ (addressDetails.equalsIgnoreCase("N") && addressValidation.equalsIgnoreCase("Y")))
  {


%>
<div class="form-group col-md-12 controls">
  <label class="control-label headpanelfont_color" for="street"><%=CRE_ADDRESS%></label>
  <input type="text" class="form-control textbox_color" id="street" name="street" size="40" value="<%=street%>" placeholder="<%=CRE_ADDRESS%>" >
</div>

<div class="form-group col-md-6 controls">
  <label class="control-label headpanelfont_color" for="city"><%=CRE_CITY%></label>
  <input type="text"  class="form-control textbox_color" name="city" id="city" size="20" value="<%=city%>" placeholder="<%=CRE_CITY%>">
</div>
<div class="form-group col-md-6 controls">
  <label class="control-label headpanelfont_color" for="zip"><%=CRE_ZIP%></label>
  <input type="text" class="form-control textbox_color"  id="zip" name="zip" size="10" title="allow only alphanumeric value" value="<%=zip%>" placeholder="<%=CRE_ZIP%>">
</div>
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
  <jsp:include page="2charcountrycode.jsp"></jsp:include>
  <%
    }
  %>

</div>
<div class="form-group col-md-6 controls">
  <label class="control-label headpanelfont_color" for="lastname"><%=CRE_COUNTRYCODE%></label>
  <input type="text" class="form-control textbox_color"  id="countrycode" name="countrycode" value="<%=country%>" readonly="readonly">
</div>
<div class="form-group col-md-6 controls">
  <label class="control-label headpanelfont_color State"><%=CRE_STATE%></label>
  <select name="us_states" id="us_states_id" onchange="usstate()" class="form-control textbox_color" disabled="true">
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
  <input name="state" class="form-control textbox_color"  value="<%=state%>" type="text" id="b_state" size="10"></td>

</div>

<div class="col-md-3 controls" >
  <label class="control-label headpanelfont_color telnocc" ><%=CRE_PHONECC%></label>
  <input type="text" name="telnocc" style="width: 80%;" size="5" id="telnocc" class="form-control textbox_color" value="<%=telnocc%>" readonly="readonly" title="allow only numeric value"> -

</div>
<div class="col-md-3 controls">
  <label class="control-label headpanelfont_color telnocc"><%=CRE_PHONENO%></label>
  <input type="text" name="telno" size="20" id="telno" value="<%=telno%>" class="form-control textbox_color" title="allow only numeric value">

</div>
<%
  }
%>
<div class="form-group col-md-6 controls">
  <label class="control-label headpanelfont_color" for="emailaddr"><%=CRE_EMAIL%></label>
  <input type="text" name="emailaddr" id="emailaddr" class="form-control textbox_color"  size="30" placeholder="Email" value="<%=email%>"/>
</div>
      </div>
        </div>
      </div>
    </div>
  </section>



<input type="hidden" name="street" value="<%=street%>">
<input type="hidden" name="city" value="<%=city%>">
<input type="hidden" name="zip" value="<%=zip%>">
<input type="hidden" name="state" value="<%=state%>">
<%--<input type="hidden" name="countrycode" value="<%=country%>">--%>
<script type="text/javascript">
  function mandateCHECK(status)
  {
    if(status==true)
    {
      document.getElementById('bic').style.display="none";
      document.getElementById('iban').style.display="none";
      document.getElementById('mandateId').style.display="block";
    }
    else
    {
      document.getElementById('bic').style.display="block";
      document.getElementById('iban').style.display="block";
      document.getElementById('mandateId').style.display="none";
    }
  }
</script>
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