<%--
  Created by IntelliJ IDEA.
  User: Vivek
  Date: 12/20/2019
  Time: 3:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.apache.xpath.SourceTree" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>

<%
  Functions functions = new Functions();
  String firstname="";
  String lastname="";
  String language="";
  String cardholderipaddress="";
  String street="";
  String us_states="";
  String city="";
  String zip="";
  String telno="";
  String emailaddr="";
  String country="";
  String state="";
  String fileName = request.getParameter("filename");
  String isRecurring = request.getParameter("isrecurring");
  String addressDetails = request.getParameter("addressDetails");
  String addressValidation = request.getParameter("addressValidation");
  if (functions.isValueNull(request.getParameter("firstname")))
  {
    firstname = ESAPI.encoder().encodeForHTML(request.getParameter("firstname"));
  }
  if (functions.isValueNull(request.getParameter("lastname")))
  {
    lastname = ESAPI.encoder().encodeForHTML(request.getParameter("lastname"));
  }
  if (functions.isValueNull(request.getParameter("language")))
  {
    language = ESAPI.encoder().encodeForHTML(request.getParameter("language"));
  }
  if (functions.isValueNull(request.getParameter("cardholderipaddress")))
  {
    cardholderipaddress = ESAPI.encoder().encodeForHTML(request.getParameter("cardholderipaddress"));
  }
  if (functions.isValueNull(request.getParameter("street")))
  {
    street = ESAPI.encoder().encodeForHTML(request.getParameter("street"));
  }
  if (functions.isValueNull(request.getParameter("us_states")))
  {
    us_states = ESAPI.encoder().encodeForHTML(request.getParameter("us_states"));
  }
  if (functions.isValueNull(request.getParameter("city")))
  {
    city = ESAPI.encoder().encodeForHTML(request.getParameter("city"));
  }
  if (functions.isValueNull(request.getParameter("zip")))
  {
    zip = ESAPI.encoder().encodeForHTML(request.getParameter("zip"));
  }
  if (functions.isValueNull(request.getParameter("telno")))
  {
    telno = ESAPI.encoder().encodeForHTML(request.getParameter("telno"));
  }
  if (functions.isValueNull(request.getParameter("emailaddr")))
  {
    emailaddr = ESAPI.encoder().encodeForHTML(request.getParameter("emailaddr"));
  }
  if (functions.isValueNull( request.getParameter("country")))
  {
    country = ESAPI.encoder().encodeForHTML(request.getParameter("country"));
  }
  if (functions.isValueNull(request.getParameter("state")))
  {
    state = ESAPI.encoder().encodeForHTML(request.getParameter("state"));
  }

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String ccAcqraPaymentPage_credit_card_details = rb1.getString("ccAcqraPaymentPage_credit_card_details");
  String ccAcqraPaymentPage_card_number = rb1.getString("ccAcqraPaymentPage_card_number");
  String ccAcqraPaymentPage_expiry_month = rb1.getString("ccAcqraPaymentPage_expiry_month");
  String ccAcqraPaymentPage_january = rb1.getString("ccAcqraPaymentPage_january");
  String ccAcqraPaymentPage_february = rb1.getString("ccAcqraPaymentPage_february");
  String ccAcqraPaymentPage_march = rb1.getString("ccAcqraPaymentPage_march");
  String ccAcqraPaymentPage_april = rb1.getString("ccAcqraPaymentPage_april");
  String ccAcqraPaymentPage_may = rb1.getString("ccAcqraPaymentPage_may");
  String ccAcqraPaymentPage_june = rb1.getString("ccAcqraPaymentPage_june");
  String ccAcqraPaymentPage_july = rb1.getString("ccAcqraPaymentPage_july");
  String ccAcqraPaymentPage_august = rb1.getString("ccAcqraPaymentPage_august");
  String ccAcqraPaymentPage_september = rb1.getString("ccAcqraPaymentPage_september");
  String ccAcqraPaymentPage_october = rb1.getString("ccAcqraPaymentPage_october");
  String ccAcqraPaymentPage_november = rb1.getString("ccAcqraPaymentPage_november");
  String ccAcqraPaymentPage_december = rb1.getString("ccAcqraPaymentPage_december");
  String ccAcqraPaymentPage_expiry_year = rb1.getString("ccAcqraPaymentPage_expiry_year");
  String ccAcqraPaymentPage_2019 = rb1.getString("ccAcqraPaymentPage_2019");
  String ccAcqraPaymentPage_2020 = rb1.getString("ccAcqraPaymentPage_2020");
  String ccAcqraPaymentPage_2021 = rb1.getString("ccAcqraPaymentPage_2021");
  String ccAcqraPaymentPage_2022 = rb1.getString("ccAcqraPaymentPage_2022");
  String ccAcqraPaymentPage_2023 = rb1.getString("ccAcqraPaymentPage_2023");
  String ccAcqraPaymentPage_2024 = rb1.getString("ccAcqraPaymentPage_2024");
  String ccAcqraPaymentPage_2025 = rb1.getString("ccAcqraPaymentPage_2025");
  String ccAcqraPaymentPage_2026 = rb1.getString("ccAcqraPaymentPage_2026");
  String ccAcqraPaymentPage_2027 = rb1.getString("ccAcqraPaymentPage_2027");
  String ccAcqraPaymentPage_2028 = rb1.getString("ccAcqraPaymentPage_2028");
  String ccAcqraPaymentPage_2029 = rb1.getString("ccAcqraPaymentPage_2029");
  String ccAcqraPaymentPage_2030 = rb1.getString("ccAcqraPaymentPage_2030");
  String ccAcqraPaymentPage_card_verification_number = rb1.getString("ccAcqraPaymentPage_card_verification_number");
  String ccAcqraPaymentPage_customer_details = rb1.getString("ccAcqraPaymentPage_customer_details");
  String ccAcqraPaymentPage_first_name = rb1.getString("ccAcqraPaymentPage_first_name");
  String ccAcqraPaymentPage_last_name = rb1.getString("ccAcqraPaymentPage_last_name");
  String ccAcqraPaymentPage_language = rb1.getString("ccAcqraPaymentPage_language");
  String ccAcqraPaymentPage_eng = rb1.getString("ccAcqraPaymentPage_eng");
  String ccAcqraPaymentPage_chn = rb1.getString("ccAcqraPaymentPage_chn");
  String ccAcqraPaymentPage_rus = rb1.getString("ccAcqraPaymentPage_rus");
  String ccAcqraPaymentPage_card_ip = rb1.getString("ccAcqraPaymentPage_card_ip");
  String ccAcqraPaymentPage_billing_address = rb1.getString("ccAcqraPaymentPage_billing_address");
  String ccAcqraPaymentPage_address1 = rb1.getString("ccAcqraPaymentPage_address1");
  String ccAcqraPaymentPage_city = rb1.getString("ccAcqraPaymentPage_city");
  String ccAcqraPaymentPage_zip_code = rb1.getString("ccAcqraPaymentPage_zip_code");
  String ccAcqraPaymentPage_iso_country = rb1.getString("ccAcqraPaymentPage_iso_country");
  String ccAcqraPaymentPage_country_code = rb1.getString("ccAcqraPaymentPage_country_code");
  String ccAcqraPaymentPage_state = rb1.getString("ccAcqraPaymentPage_state");
  String ccAcqraPaymentPage_select_a_state = rb1.getString("ccAcqraPaymentPage_select_a_state");
  String ccAcqraPaymentPage_state_code = rb1.getString("ccAcqraPaymentPage_state_code");
  String ccAcqraPaymentPage_phone_cc = rb1.getString("ccAcqraPaymentPage_phone_cc");
  String ccAcqraPaymentPage_phone_number = rb1.getString("ccAcqraPaymentPage_phone_number");
  String ccAcqraPaymentPage_email_id = rb1.getString("ccAcqraPaymentPage_email_id");
%>


<%--<div class="col-sm-12 portlets ui-sortable" style="margin-top: 100px;margin-left:29px;margin-right: 14px;margin-bottom: 12px;background-color: #ffffff">
    <div class="widget">--%>
<%--<div class="container-fluid ">
    <div class="row rowadd"    style="margin-top: 10px;">
        <div class="form foreground bodypanelfont_color panelbody_color">
            <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Credit Card Details</h2>
            <hr class="hrform">
        </div>--%>

<div class="row">

  <div class="col-sm-12 portlets ui-sortable">

    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ccAcqraPaymentPage_credit_card_details%></strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <div id="horizontal-form">

          <div class="form-group col-md-3">
            <label><%=ccAcqraPaymentPage_card_number%></label>

            <%--
                                    <input name="password_user-password-2" type="password" id="user-password-2" name="user-password-2" id="password_user-password-2"  size="30" maxlength="16" title="( <%=cardtypename%> only )" onpaste="return pasted('CCCP');" autocomplete="OFF" class="txtbox">
            --%>
            <%--kindly do not change name Attribute for all textbox--%>
            <input id="user-password-2" name="cardnumber" autocomplete="OFF" maxlength="19"
                   class="form-control">
          </div>

          <div class="form-group col-md-3">
            <label><%=ccAcqraPaymentPage_expiry_month%></label>
            <select NAME="expiry_month" class="form-control">
              <option VALUE="01" selected><%=ccAcqraPaymentPage_january%></option>
              <option VALUE="02"><%=ccAcqraPaymentPage_february%></option>
              <option VALUE="03"><%=ccAcqraPaymentPage_march%></option>
              <option VALUE="04"><%=ccAcqraPaymentPage_april%></option>
              <option VALUE="05"><%=ccAcqraPaymentPage_may%></option>
              <option VALUE="06"><%=ccAcqraPaymentPage_june%></option>
              <option VALUE="07"><%=ccAcqraPaymentPage_july%></option>
              <option VALUE="08"><%=ccAcqraPaymentPage_august%></option>
              <option VALUE="09"><%=ccAcqraPaymentPage_september%></option>
              <option VALUE="10"><%=ccAcqraPaymentPage_october%></option>
              <option VALUE="11"><%=ccAcqraPaymentPage_november%></option>
              <option VALUE="12"><%=ccAcqraPaymentPage_december%></option>
            </select>
          </div>

          <div class="form-group col-md-3">
            <label><%=ccAcqraPaymentPage_expiry_year%></label>
            <select NAME="expiry_year" class="form-control">

              <%-- <option VALUE="2018" selected>2018</option>--%>
              <option VALUE="2019" selected><%=ccAcqraPaymentPage_2019%></option>
              <option VALUE="2020"><%=ccAcqraPaymentPage_2020%></option>
              <option VALUE="2021"><%=ccAcqraPaymentPage_2021%></option>
              <option VALUE="2022"><%=ccAcqraPaymentPage_2022%></option>
              <option VALUE="2023"><%=ccAcqraPaymentPage_2023%></option>
              <option VALUE="2024"><%=ccAcqraPaymentPage_2024%></option>
              <option VALUE="2025"><%=ccAcqraPaymentPage_2025%></option>
              <option VALUE="2026"><%=ccAcqraPaymentPage_2026%></option>
              <option VALUE="2027"><%=ccAcqraPaymentPage_2027%></option>
              <option VALUE="2028"><%=ccAcqraPaymentPage_2028%></option>
              <option VALUE="2029"><%=ccAcqraPaymentPage_2029%></option>
              <option VALUE="2030"><%=ccAcqraPaymentPage_2030%></option>
            </select>
          </div>

          <div class="form-group col-md-3">
            <label><%=ccAcqraPaymentPage_card_verification_number%></label>

            <input type="password" title="" class="form-control" name="cvv" size="2" maxlength="4" autocomplete="OFF" >
          </div>
        </div>
      </div>
    </div>
  </div>
</div>




<%--<div class="container-fluid ">
    <div class="row rowadd" style="margin-top: 10px;">
        <div style="/*margin-top: 100px;*//*margin-left:29px;margin-right: 14px;*/margin-bottom: 12px;/*background-color: #ffffff*/">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/color:#34495e;padding-top: 5px;font-size: 18px;align: initial;"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Billing Address (where you receive your Credit Card bills)</h2>
                <hr class="hrform">
            </div>--%>
<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ccAcqraPaymentPage_customer_details%></strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <%--<div id="horizontal-form">--%>
        <div class="form-group col-md-3">
          <label><%=ccAcqraPaymentPage_first_name%></label>
          <input name="firstname" size="30" title="enter ur firstname"  maxlength="50" class="form-control" value=<%=firstname%> >
        </div>



        <div class="form-group col-md-3">
          <label><%=ccAcqraPaymentPage_last_name%></label>
          <input name="lastname" title="" size="30" maxlength="50" class="form-control"  value=<%=lastname%>>
        </div>
        <%if(fileName!=null)
        {
        %>
        <jsp:include page="<%=fileName%>"></jsp:include>
        <%
          /*}
          if("Y".equalsIgnoreCase(isRecurring) && fileName==null)
          {*/
        %>
        <%--<jsp:include page="pfsspecificfields.jsp"></jsp:include>--%>
        <%
          }
        %>
        <div class="form-group col-md-3">
          <label><%=ccAcqraPaymentPage_language%></label>

          <select NAME="language" id="language" class="form-control" value=<%=language%>>
            <option VALUE="ENG" selected><%=ccAcqraPaymentPage_eng%></option>
            <option VALUE="CHN"><%=ccAcqraPaymentPage_chn%></option>
            <option VALUE="RUS"><%=ccAcqraPaymentPage_rus%></option>
          </select>
        </div>
          <div class="form-group col-md-3">
            <label><%=ccAcqraPaymentPage_card_ip%></label>
            <input type="text" name="cardholderipaddress" title="Ex: abc@xyz.com" value="<%=cardholderipaddress%>" class="form-control"/>
            </select>
          </div>
      </div>
    </div>
  </div>
</div>
<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ccAcqraPaymentPage_billing_address%></strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <br>

      <div class="widget-content padding">

        <div class="form-group col-md-4">
          <%
            if(addressDetails.equalsIgnoreCase("Y") || (addressDetails.equalsIgnoreCase("N") && addressValidation.equalsIgnoreCase("Y")))
            {
          %>

          <label><%=ccAcqraPaymentPage_address1%></label>

          <input type="text" title="" name="street" size="40"  maxlength="150" class="form-control" value=<%=street%>>
        </div>


        <div class="form-group col-md-4">
          <label><%=ccAcqraPaymentPage_city%></label>

          <input type="text" name="city" size="20" title=""  maxlength="35" class="form-control" value=<%=city%>>
        </div>


        <div class="form-group col-md-4">
          <label ><%=ccAcqraPaymentPage_zip_code%></label>
          <input type="text" name="zip" size="10" title="" maxlength="15" class="form-control" value=<%=zip%>>
        </div>

        <div class="form-group col-md-4">
          <label><%=ccAcqraPaymentPage_iso_country%></label>

          <%
            GatewayAccount account = GatewayAccountService.getGatewayAccount(request.getParameter("accountid"));
            String gatewayType=account.getGateway();
            if(AuxPayPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
            {%>
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
        <div class="form-group col-md-2">
          <label><%=ccAcqraPaymentPage_country_code%></label>
          <input type="text" name="countrycode" class="form-control" readonly="readonly">

        </div>

        <div class="form-group col-md-4">
          <label><%=ccAcqraPaymentPage_state%></label>
          <select name="us_states" id="us_states_id" onchange="usstate();" class="form-control">
            <option value="Select one"><%=ccAcqraPaymentPage_select_a_state%></option>
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
        <div class="form-group col-md-2">
          <label><%=ccAcqraPaymentPage_state_code%></label>
          <input name="state" type="text" id="b_state" class="form-control" readonly="readonly">
        </div>

        <div class="form-group col-md-2">
          <label><%=ccAcqraPaymentPage_phone_cc%></label>
          <input type="text" name="telnocc" class="form-control" readonly="readonly" title="Example(Country Code - Phone Number)">
        </div>
        <div class="form-group col-md-4">
          <label><%=ccAcqraPaymentPage_phone_number%></label>
          <input type="text" name="telno" size="20" maxlength="20"  class="form-control" title="Example(Country Code - Phone Number)" value=<%=telno%>>
        </div>
        <%
          }
        %>

        <div class="form-group col-md-6">
          <label><%=ccAcqraPaymentPage_email_id%></label>
          <input type="text" name="emailaddr" title="Ex: abc@xyz.com"  class="form-control" value=<%=emailaddr%>>
        </div>


        <input type="hidden" name="filename" value="<%=fileName%>" class="textb">
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
  var c = "<%=country%>";
  var lang ="<%=language%>"

  if(c)
  {
    setSelectedValue(document.getElementById("country") , c)
    if(c.split("|")[0].length == 2)
    {
      myjunk();
    }
    else if(c.split("|")[0].length == 3)
    {
      myjunk1();
    }

    document.getElementById("b_state").value = "<%=state%>";
  }
  if(lang)
  {
    setSelectedValue(document.getElementById("language"),lang)
  }

  function setSelectedValue(selectObj, valueToSet) {
    for (var i = 0; i < selectObj.options.length; i++) {
      if (selectObj.options[i].value== valueToSet) {
        selectObj.options[i].selected = true;
        return;
      }
    }
  }

</script>