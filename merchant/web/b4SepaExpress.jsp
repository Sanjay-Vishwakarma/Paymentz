
<%@ page import="com.payment.b4payment.B4PaymentGateway" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 09-01-2017
  Time: 18:38
  To change this template use File | Settings | File Templates.
--%>

<%
  String fileName = request.getParameter("filename");
  String addressDetails = request.getParameter("addressDetails");
  String addressValidation = request.getParameter("addressValidation");
%>

<style>

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
            <label>Given Name</label>
            <input type="text" title="" name="firstname" size="40" maxlength="150" class="form-control" >
          </div>

          <div class="form-group col-md-4">
            <label>Family Name</label>
            <input type="text" title="" name="lastname" size="40" maxlength="150" class="form-control">
          </div>

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
            <input type="text" title="" name="mandateId" id="mandateId" size="40" maxlength="150" class="form-control" >
          </div>

          <input type="hidden" name="filename" value="<%=fileName%>" class="textb">
        </div>
      </div>
    </div>
  </div>
</div>