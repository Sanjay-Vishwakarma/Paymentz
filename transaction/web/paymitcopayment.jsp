<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
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
    rb = LoadProperties.getProperty("com.directi.pg.creditpage","zh");
  }
  else
  {
    rb = LoadProperties.getProperty("com.directi.pg.creditpage");
  }
  final String CRE_FIRSTNAME=rb.getString("CRE_FIRSTNAME");
  final String CRE_LASTNAME=rb.getString("CRE_LASTNAME");
  final String CRE_ACCOUNTNUMBER=rb.getString("CRE_ACCOUNTNUMBER");
  final String CRE_ROUTINGNUMBER=rb.getString("CRE_ROUTINGNUMBER");
  final String CRE_ACCOUNTTYPE=rb.getString("CRE_ACCOUNTTYPE");

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
  String firstname = getValue(genericAddressDetailsVO.getFirstname());
  String lastname = getValue(genericAddressDetailsVO.getLastname());
  String cardType = getValue(standardKitValidatorVO.getCardType());
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
    #headtag2
    {
      margin-top: 10;
      margin-bottom: 9px;
    }
    #form-firstrow
    {
      display: -webkit-inline-box;
    }
    #formfield
    {
      margin-left: -22px;
      width: 130%;
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
    #headtag2
    {
      width: 100%;
      margin-top: 14px;
    }
    #inputgrp
    {
      width:100%;
    }
    #h2carddetails
    {
      margin-left: 0px;
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
    <div class="first-row form-group" id="form-firstrow">
      <div class=" form-group col-md-12 controls">

<div class="col-md-12 portlets controls ui-sortable" id="headtag">
  <label><%=CRE_BANKACCOUNTDETAILS%></label>

</div>
<div class="col-md-12 portlets controls ui-sortable" id="formfield">
      <div class="form-group col-md-6 controls" >
        <label class="control-label headpanelfont_color" for="firstname"><%=CRE_FIRSTNAME%></label>
        <input type="text"   class="form-control textbox_color" id="firstname" name="firstname" placeholder="<%=CRE_FIRSTNAME%>" title="Allow alphabets and numbers only." value="<%=firstname%>">
      </div>
      <div class="form-group col-md-6 controls">
        <label class="control-label headpanelfont_color" for="lastname"><%=CRE_LASTNAME%></label>
        <input type="text" class="form-control textbox_color"  id="lastname" name="lastname"  placeholder="<%=CRE_LASTNAME%>" title="Allow alphabets and numbers only." value="<%=lastname%>">
      </div>
      <div class="form-group col-md-6 controls" >
        <label class="control-label headpanelfont_color"><%=CRE_ACCOUNTNUMBER%></label>
        <input class="number account-number form-control textbox_color"
                 type="text"
                 inputmode="numeric" name="accountnumber"  title="Allow numbers only." maxlength="17" autocomplete="OFF"
                 placeholder="<%=CRE_ACCOUNTNUMBER%>">
      </div>

      <input type="hidden" name="cardtype" value="<%=cardType%>">
      <div class="form-group col-md-6 controls">
        <label class="control-label headpanelfont_color"><%=CRE_ROUTINGNUMBER%></label>
        <input class="number account-number form-control textbox_color" type="text"
               inputmode="numeric" name="routingnumber"  title="Allow numbers only." maxlength="10" autocomplete="OFF"
               placeholder="<%=CRE_ROUTINGNUMBER%>">
      </div>
      <div class="col-md-6 controls">
        <label class="control-label headpanelfont_color"><%=CRE_ACCOUNTTYPE%></label>
        <select id="accountType" name="accountType" class="form-control textbox_color">
          <option VALUE="PC" selected>Personal Checking</option>
          <option VALUE="PS">Personal Savings</option>
          <option VALUE="CC">Commercial Checking</option>
        </select>
      </div>

  <div class="col-md-7 controls portlets ui-sortable" id="headtag2">
    <label><%=CRE_BILLINGADDRESSFORBANK%></label>

  </div>


      <%if(fileName!=null){%>
      <jsp:include page="<%=fileName%>"></jsp:include>
      <%}%>

      <%

        if(addressDetails.equalsIgnoreCase("Y") || addressValidation.equalsIgnoreCase("Y") || (addressDetails.equalsIgnoreCase("N") && addressValidation.equalsIgnoreCase("Y")))
        {


      %>
      <div class="form-group col-md-12 controls">
        <label class="control-label headpanelfont_color" for="street"><%=CRE_ADDRESS%></label>
        <input type="text" class="form-control textbox_color" id="street" name="street" title="Allow alphabets and numbers only" value="<%=street%>" placeholder="<%=CRE_ADDRESS%>" >
      </div>

      <div class="form-group col-md-6 controls">
        <label class="control-label headpanelfont_color" for="city"><%=CRE_CITY%></label>
        <input type="text"  class="form-control textbox_color" name="city" id="city"  title="Allow alphabets only" value="<%=city%>" placeholder="<%=CRE_CITY%>">
      </div>
      <div class="form-group col-md-6 controls">
        <label class="control-label headpanelfont_color" for="zip"><%=CRE_ZIP%></label>
        <input type="text" class="form-control textbox_color"  id="zip" name="zip"  title="Allow numbers only" value="<%=zip%>" placeholder="<%=CRE_ZIP%>">
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
            country = commonFunctionUtil.getCountry3LetterAbbreviation(country);

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
        <label class="control-label headpanelfont_color"  for="lastname"><%=CRE_COUNTRYCODE%></label>
        <input type="text" class="form-control textbox_color"  id="countrycode" name="countrycode" title="Allow two digit US country code only" value="<%=country%>" readonly="readonly">
      </div>
      <div class="form-group col-md-6 controls">
        <label class="control-label headpanelfont_color State"><%=CRE_STATE%></label>
        <select name="us_states" id="us_states_id" onchange="usstate();" class="form-control textbox_color" disabled="true">
          <option value="Select one">Select a State for US only</option>
          <option value="AL">ALABAMA</option>
          <option value="AK">ALASKA</option>
          <option value="AZ">ARIZONA</option>
          <option value="AR">ARKANSAS</option>
          <option value="CA">CALIFORNIA</option>
          <option value="CO">COLORADO</option>
          <option value="CT">CONNECTICUT</option>
          <option value="DE">DELAWARE</option>
          <option value="DC">DISTRICT OF COLUMBIA</option>
          <option value="FL">FLORIDA</option>
          <option value="GA">GEORGIA</option>
          <option value="HI">HAWAII</option>
          <option value="ID">IDAHO</option>
          <option value="IL">ILLINOIS</option>
          <option value="IN">INDIANA</option>
          <option value="IA">IOWA</option>
          <option value="KS">KANSAS</option>
          <option value="KY">KENTUCKY</option>
          <option value="LA">LOUISIANA</option>
          <option value="ME">MAINE</option>
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
          <option value="PA">PENNSYLVANIA</option>
          <option value="PR">PUERTO RICO</option>
          <option value="RI">RHODE ISLAND</option>
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
        <input name="state" class="form-control textbox_color"  title="Allow two digit US state code only" value="<%=state%>" type="text" id="b_state" ></td>

      </div>

      <div class="form-group col-md-3 controls">
        <label class="control-label headpanelfont_color telnocc" ><%=CRE_PHONECC%></label>
        <input type="text" name="telnocc"  id="telnocc" class="form-control textbox_color" value="<%=telnocc%>" readonly="readonly" title="Allow numbers only">

      </div>
      <div class="form-group col-md-3 controls">
        <label class="control-label headpanelfont_color telnocc"><%=CRE_PHONENO%></label>
        <input type="text" name="telno"  id="telno" value="<%=telno%>" class="form-control textbox_color" title="Allow numbers only">

      </div>
      <%
        }
      %>
      <div class="form-group col-md-6 controls">
        <label class="control-label headpanelfont_color" for="emailaddr"><%=CRE_EMAIL%></label>
        <input type="text" name="emailaddr" id="emailaddr" class="form-control textbox_color"   placeholder="<%=CRE_EMAIL%>" title="Allow alphabets, numbers and special characters only" value="<%=email%>"/>
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

