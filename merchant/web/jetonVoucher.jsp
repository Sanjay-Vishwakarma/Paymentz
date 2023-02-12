<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 8/26/17
  Time: 3:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<style>

</style>
<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Please Fill The Voucher Details</strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <div class="widget-content padding">
        <div id="horizontal-form">
        <div class="form-group col-md-3">
          <label>Voucher Number</label>
          <input type="text" name="vouchernumber" id="vouchernumber" maxlength="30" placeholder="Voucher Number"  class="form-control"/>
        </div>

        <div class="form-group col-md-3">
          <label>Security Code</label>
          <input class="security-code form-control" id="securityCode" inputmode="numeric" name="securityCode" size="2" maxlength="10" autocomplete="OFF" type="text" placeholder="Security Code" class="form-control">
        </div>

        <div class="form-group col-md-3">

          <label class="control-label headpanelfont_color"> Expiry Month</label>
          <select id="expiry_month" name="expiry_month" class="form-control">
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

        <div class="form-group col-md-3 controls">

          <label class="control-label headpanelfont_color">Expiry Year</label>
          <select name="expiry_year" id="expiry_year" class="form-control">
            <option VALUE="2017" selected>2017</option>
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
      </div>
    </div>
  </div>
    </div>
  </div>
