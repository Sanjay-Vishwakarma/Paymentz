<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/17/14
  Time: 2:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  String fileName = request.getParameter("filename");
  String isRecurring = request.getParameter("isrecurring");
  String addressDetails = request.getParameter("addressDetails");
  String addressValidation = request.getParameter("addressValidation");

%>
<div class="row">

  <div class="col-sm-12 portlets ui-sortable">

    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Credit Card Details</strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <div id="horizontal-form">

          <div class="form-group col-md-3">
            <label>Card Number</label>

            <%--
                                    <input name="password_user-password-2" type="password" id="user-password-2" name="user-password-2" id="password_user-password-2"  size="30" maxlength="16" title="( <%=cardtypename%> only )" onpaste="return pasted('CCCP');" autocomplete="OFF" class="txtbox">
            --%>
            <%--kindly do not change name Attribute for all textbox--%>
            <input  id="user-password-2" name="cardnumber" autocomplete="OFF" maxlength="16" class="form-control">
          </div>

          <div class="form-group col-md-3">
            <label>Expiry Month</label>
            <select NAME="expiry_month" class="form-control">
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

          <div class="form-group col-md-3">
            <label>Expiry Year</label>
            <select NAME="expiry_year" class="form-control">
              <option VALUE="2014"SELECTED>2014</option>
              <option VALUE="2015">2015</option>
              <option VALUE="2016">2016</option>
              <option VALUE="2017">2017</option>
              <option VALUE="2018">2018</option>
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

          <div class="form-group col-md-3">
            <label>Card Verification Number</label>

            <input type="password" title="" class="form-control" name="cvv" size="2" maxlength="4" autocomplete="OFF" >
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
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Customer Details</strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <%--<div id="horizontal-form">--%>
        <div class="form-group col-md-4">
          <label>First Name</label>
          <input name="firstname" size="30" value="" title="enter ur firstname" maxlength="30" class="form-control" >
        </div>



        <div class="form-group col-md-4">
          <label>Last Name</label>
          <input name="lastname" title="" size="30" value="" maxlength="30" class="form-control" >
        </div>
        <%if(fileName!=null)
        {
        %>
        <jsp:include page="payforasiaspecificfields.jsp"></jsp:include>
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
          <label>Language</label>

          <select NAME="language" class="form-control">
            <option VALUE="ENG" selected>ENG</option>
            <option VALUE="CHN">CHN</option>
            <option VALUE="RUS">RUS</option>
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
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Billing Address (where you receive your Credit Card bills)</strong></h2>
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

          <label>Address</label>

          <input type="text" title="" name="street" size="40"  maxlength="150" class="form-control" >
        </div>


        <div class="form-group col-md-4">
          <label>City</label>

          <input type="text" name="city" size="20" title="" maxlength="35" class="form-control">
        </div>


        <div class="form-group col-md-4">
          <label >Zip Code</label>
          <input type="text" name="zip" size="10" title="" maxlength="15" class="form-control">
        </div>

        <div class="form-group col-md-4">
          <label>ISO Country Code</label>

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
          <label>Country Code</label>
          <input type="text" name="countrycode" class="form-control" readonly="readonly">

        </div>

        <div class="form-group col-md-4">
          <label>State</label>
          <select name="us_states" id="us_states_id" onchange="usstate();" class="form-control">
            <option value="Select one">Select a State for US</option>
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
          <label>State Code</label>
          <input name="state" type="text" id="b_state" class="form-control" readonly="readonly">
        </div>

        <div class="form-group col-md-2">
          <label>Phone CC</label>
          <input type="text" name="telnocc" class="form-control" readonly="readonly" title="Example(Country Code - Phone Number)">
        </div>
        <div class="form-group col-md-4">
          <label>Phone Number</label>
          <input type="text" name="telno" size="20" maxlength="20" class="form-control" title="Example(Country Code - Phone Number)">
        </div>
        <%
          }
        %>

        <div class="form-group col-md-6">
          <label>Email ID</label>
          <input type="text" name="emailaddr" title="Ex: abc@xyz.com" value="" class="form-control"/>
        </div>


        <input type="hidden" name="filename" value="<%=fileName%>" class="textb">
      </div>
    </div>
  </div>
</div>