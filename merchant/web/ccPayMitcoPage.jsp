<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 12/30/15
  Time: 2:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  String fileName = request.getParameter("filename");
  String isRecurring = request.getParameter("isrecurring");
  String addressDetails = request.getParameter("addressDetails");
  String addressValidation = request.getParameter("addressValidation");

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  System.out.println("language_property1"+language_property1);
  rb1 = LoadProperties.getProperty(language_property1);
  String ccPayMitcoPage_account_details = rb1.getString("ccPayMitcoPage_account_details");
  String ccPayMitcoPage_account_number = rb1.getString("ccPayMitcoPage_account_number");
  String ccPayMitcoPage_routing_number = rb1.getString("ccPayMitcoPage_routing_number");
  String ccPayMitcoPage_account_type = rb1.getString("ccPayMitcoPage_account_type");
  String ccPayMitcoPage_personal_checking = rb1.getString("ccPayMitcoPage_personal_checking");
  String ccPayMitcoPage_personal_savings = rb1.getString("ccPayMitcoPage_personal_savings");
  String ccPayMitcoPage_commercial_checking = rb1.getString("ccPayMitcoPage_commercial_checking");
  String ccPayMitcoPage_customer_details = rb1.getString("ccPayMitcoPage_customer_details");
  String ccPayMitcoPage_first = rb1.getString("ccPayMitcoPage_first");
  String ccPayMitcoPage_last = rb1.getString("ccPayMitcoPage_last");
  String ccPayMitcoPage_language = rb1.getString("ccPayMitcoPage_language");
  String ccPayMitcoPage_eng = rb1.getString("ccPayMitcoPage_eng");
  String ccPayMitcoPage_chn = rb1.getString("ccPayMitcoPage_chn");
  String ccPayMitcoPage_rus = rb1.getString("ccPayMitcoPage_rus");
  String ccPayMitcoPage_cardholder = rb1.getString("ccPayMitcoPage_cardholder");
  String ccPayMitcoPage_billing = rb1.getString("ccPayMitcoPage_billing");
  String ccPayMitcoPage_address = rb1.getString("ccPayMitcoPage_address");
  String ccPayMitcoPage_city = rb1.getString("ccPayMitcoPage_city");
  String ccPayMitcoPage_zip_code = rb1.getString("ccPayMitcoPage_zip_code");
  String ccPayMitcoPage_iso_country = rb1.getString("ccPayMitcoPage_iso_country");
  String ccPayMitcoPage_country_code = rb1.getString("ccPayMitcoPage_country_code");
  String ccPayMitcoPage_state = rb1.getString("ccPayMitcoPage_state");
  String ccPayMitcoPage_select_a_state = rb1.getString("ccPayMitcoPage_select_a_state");
  String ccPayMitcoPage_state_code = rb1.getString("ccPayMitcoPage_state_code");
  String ccPayMitcoPage_phone_cc = rb1.getString("ccPayMitcoPage_phone_cc");
  String ccPayMitcoPage_phone = rb1.getString("ccPayMitcoPage_phone");
  String ccPayMitcoPage_email_id = rb1.getString("ccPayMitcoPage_email_id");
%>

<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">
      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ccPayMitcoPage_account_details%></strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <div id="horizontal-form">
          <div class="form-group col-md-3">
            <label><%=ccPayMitcoPage_account_number%></label>
            <%--kindly do not change name Attribute for all textbox--%>
            <input type="text" name="accountnumber" class="form-control" autocomplete="OFF" maxlength="20" title="Allow numbers only"/>
          </div>

          <div class="form-group col-md-3">
            <label><%=ccPayMitcoPage_routing_number%></label>
            <%--kindly do not change name Attribute for all textbox--%>
            <input type="text" name="routingnumber" class="form-control" autocomplete="OFF" maxlength="10" title="Allow numbers only"/>
          </div>

          <div class="form-group col-md-3">
            <label><%=ccPayMitcoPage_account_type%></label>
            <select NAME="accountType" class="form-control">
              <option VALUE="PC" selected><%=ccPayMitcoPage_personal_checking%></option>
              <option VALUE="PS"><%=ccPayMitcoPage_personal_savings%></option>
              <option VALUE="CC"><%=ccPayMitcoPage_commercial_checking%></option>
            </select>
          </div>

        </div>
      </div>
    </div>
  </div>
</div>

<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ccPayMitcoPage_customer_details%></strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <%--<div id="horizontal-form">--%>
        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_first%></label>
          <input name="firstname" size="30" value="" title="enter ur firstname" maxlength="30" class="form-control" >
        </div>

        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_last%></label>
          <input name="lastname" title="" size="30" value="" maxlength="30" class="form-control" >
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
        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_language%></label>

          <select NAME="language" class="form-control">
            <option VALUE="ENG" selected><%=ccPayMitcoPage_eng%></option>
            <option VALUE="CHN"><%=ccPayMitcoPage_chn%></option>
            <option VALUE="RUS"><%=ccPayMitcoPage_rus%></option>
          </select>
        </div>

          <div class="form-group col-md-4">
            <label><%=ccPayMitcoPage_cardholder%></label>

            <input type="text" name="cardholderipaddress" size="20" title="" maxlength="35" class="form-control">
          </div>

      </div>
    </div>
  </div>
</div>

<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=ccPayMitcoPage_billing%></strong></h2>
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

          <label><%=ccPayMitcoPage_address%></label>

          <input type="text" title="" name="street" size="40"  maxlength="150" class="form-control" >
        </div>


        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_city%></label>

          <input type="text" name="city" size="20" title="" maxlength="35" class="form-control">
        </div>


        <div class="form-group col-md-4">
          <label ><%=ccPayMitcoPage_zip_code%></label>
          <input type="text" name="zip" size="10" title="" maxlength="15" class="form-control">
        </div>

        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_iso_country%></label>

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
          <label><%=ccPayMitcoPage_country_code%></label>
          <input type="text" name="countrycode" class="form-control" readonly="readonly">

        </div>

        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_state%></label>
          <select name="us_states" id="us_states_id" onchange="usstate();" class="form-control">
            <option value="Select one"><%=ccPayMitcoPage_select_a_state%></option>
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
          <label><%=ccPayMitcoPage_state_code%></label>
          <input name="state" type="text" id="b_state" class="form-control" readonly="readonly">
        </div>

        <div class="form-group col-md-2">
          <label><%=ccPayMitcoPage_phone_cc%></label>
          <input type="text" name="telnocc" class="form-control" readonly="readonly" title="Example(Country Code - Phone Number)">
        </div>
        <div class="form-group col-md-4">
          <label><%=ccPayMitcoPage_phone%></label>
          <input type="text" name="telno" size="20" maxlength="20" class="form-control" title="Example(Country Code - Phone Number)">
        </div>
        <%
          }
        %>

        <div class="form-group col-md-6">
          <label><%=ccPayMitcoPage_email_id%></label>
          <input type="text" name="emailaddr" title="Ex: abc@xyz.com" value="" class="form-control"/>
        </div>


        <input type="hidden" name="filename" value="<%=fileName%>" class="textb">
      </div>
    </div>
  </div>
</div>