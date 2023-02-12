<%@ page import="com.payment.auxpay_payment.core.AuxPayPaymentGateway" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 17/10/2015
  Time: 12:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  String fileName = request.getParameter("filename");
  String addressDetails = request.getParameter("addressDetails");
  String addressValidation = request.getParameter("addressValidation");
%>
<style>
  .mandateId{
    display: none;

  }
</style>
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

      <div class="widget-content padding">
        <div id="horizontal-form">

          <div class="form-group col-md-4">
            <label>Account Holder First Name</label>
            <input type="text" title="" name="firstname" size="40" maxlength="150" class="form-control" >
          </div>

          <div class="form-group col-md-4">
            <label>Account Holder Last Name</label>
            <input type="text" title="" name="lastname" size="40" maxlength="150" class="form-control" >
          </div>

          <%--<%if(fileName!=null)
          {
          %>
          <jsp:include page="<%=fileName%>"></jsp:include>
          <%
            /*}
            if("Y".equalsIgnoreCase(isRecurring) && fileName==null)
            {*/
          %>
          &lt;%&ndash;<jsp:include page="pfsspecificfields.jsp"></jsp:include>&ndash;%&gt;
          <%
            }
          %>--%>

          <div class="form-group col-md-4" id="ibandiv">
            <label>IBAN</label>
            <input type="text" title="" name="iban" id="iban" size="40" maxlength="150" class="form-control" >
          </div>
          <div class="form-group col-md-4" id="bicdiv">
            <label>BIC</label>
            <input type="text" title="" name="bic" id="bic" size="40" maxlength="150" class="form-control" >
          </div>

          <div class="form-group col-md-4" id="mandatediv">
            <label>Mandate ID</label>
            <input type="text" title="" name="mandateid" id="mandateid" size="40" maxlength="150" class="form-control" >
          </div>

          <div class="form-group col-md-12 has-feedback">
            <label>Process Transaction Using Mandate.</label>
            <input type="checkbox" id="mandate" value="tc" name="mandate" <%--onclick='mandateCHECK(this.checked)'--%>>
          </div>



          <%--<tr><td colspan="4" class="form-control"><center><input type="checkbox" id="mandate" value="tc" name="mandate" onclick='mandateCHECK(this.checked)'>&nbsp;<b>Process Transaction Using Mandate.</b></center></td></tr>--%>

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
            <label>Zip Code</label>
            <input type="text" name="zip" size="10" title="" maxlength="15" class="form-control">
          </div>


          <%--add New--%>
          <div class="form-group col-md-3">
            <label>ISO Country</label>

            <%
              GatewayAccount account = GatewayAccountService.getGatewayAccount(request.getParameter("accountid"));
              String gatewayType=account.getGateway();
              if(AuxPayPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
              {
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
          <div class="form-group col-md-1">
            <label>Code</label>
            <input type="text" name="countrycode" class="form-control" readonly="readonly">

          </div>

          <div class="form-group col-md-3">
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
          <div class="form-group col-md-1">
            <label>Code</label>
            <input name="state" type="text" id="b_state" class="form-control" readonly="readonly">
          </div>


          <div class="form-group col-md-1">
            <label>Phone CC</label>
            <input type="text" name="telnocc" class="form-control" readonly="readonly" title="Example(Country Code - Phone Number)">
          </div>
          <div class="form-group col-md-3">
            <label>Phone Number</label>
            <input type="text" name="telno" size="20" maxlength="20" class="form-control" title="Example(Country Code - Phone Number)">
          </div>
          <%
            }
          %>

          <div class="form-group col-md-4">
            <label>Email ID</label>
            <input type="text" name="emailaddr" title="Ex: abc@xyz.com" value="" class="form-control"/>
          </div>


          <input type="hidden" name="filename" value="<%=fileName%>" class="textb">
        </div>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">


  $(document).ready(function(){

    document.getElementById('mandatediv').style.display = "none";
    $('#mandate').next('.iCheck-helper').click(function(){

      if($('#mandate').prop("checked") == true){

        document.getElementById('bicdiv').style.display = "none";
        document.getElementById('ibandiv').style.display = "none";
        document.getElementById('mandatediv').style.display = "";
      }
      else if($('#mandate').prop("checked") == false){

        document.getElementById('mandatediv').style.display = "none";
        document.getElementById('bicdiv').style.display = "";
        document.getElementById('ibandiv').style.display = "";

      }
    });
  });

</script>

<%--<script>
  function mandateCHECK(status)
  {
    alert("00000000");
    if(status==true)
    {
      alert("111111111");
      document.getElementById('td2').innerHTML="MANDATE ID";
      document.getElementById('td4').innerHTML="<input type='text' name='mandateId' size='40'  maxlength='150' class='form-control'>";
      document.getElementById('td5').style.display="none";
      document.getElementById('td6').style.display="none";
      document.getElementById('td7').style.display="none";
      document.getElementById('td8').style.display="none";
    }
    else
    {
      alert("2222222222");
      document.getElementById('td5').style.display="block";
      document.getElementById('td6').style.display="block";
      document.getElementById('td7').style.display="block";
      document.getElementById('td8').style.display="block";
      document.getElementById('td8').innerHTML="<input type='text'  name='bic' size='40'  maxlength='150' class='form-control' >";
      document.getElementById('td7').innerHTML=":";
      document.getElementById('td6').innerHTML="BIC";
      document.getElementById('td5').innerHTML="&nbsp;";
      document.getElementById('td4').innerHTML="<input type='text'  name='iban' size='40'  maxlength='150' class='form-control' >";
      document.getElementById('td3').innerHTML=":";
      document.getElementById('td2').innerHTML="IBAN";
      document.getElementById('td1').innerHTML="&nbsp;";
    }
  }
</script>--%>
