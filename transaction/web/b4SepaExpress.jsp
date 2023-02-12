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
  @media(max-width:768px)
  {
    #h2carddetails
    {

      margin-bottom: 5px;
      margin-top: 5px;
    }
    #headtag
    {
      margin-left: -38px;
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
      margin-left: -21px;
    }
  }

</style>

<section class="creditly-wrapper" style="margin-bottom: -46px;">
  <div class="credit-card-wrapper"  id="creditlyfield">
    <div class="first-row form-group" id="form-group">
      <div class=" form-group col-md-12 controls">
<div class="col-md-12 controls portlets ui-sortable" id="headtag">
  <label>Billing Address (where you receive your Credit Card bills)</label>

</div>
        <div class="col-md-12 controls portlets ui-sortable" id="formfield">


          <div class="form-group col-md-6 controls">
            <label>Given Name</label>
            <input type="text" title="" name="firstname" size="40" maxlength="150" class="form-control textbox_color" >
          </div>

          <div class="form-group col-md-6 controls">
            <label>Family Name</label>
            <input type="text" title="" name="lastname" size="40" maxlength="150" class="form-control textbox_color">
          </div>

          <div class="form-group col-md-6 controls" id="ibandiv">
            <label>IBAN</label>
            <input type="text" title="" name="iban" id="iban" size="40" maxlength="150" class="form-control textbox_color" >
          </div>
          <div class="form-group col-md-6 controls" id="bicdiv">
            <label>BIC</label>
            <input type="text" title="" name="bic" id="bic" size="40" maxlength="150" class="form-control textbox_color" >
          </div>

          <div class="form-group col-md-6 controls" id="mandatediv">
            <label>Mandate ID</label>
            <input type="text" title="" name="mandateId" id="mandateId" size="40" maxlength="150" class="form-control textbox_color" >
          </div>

          <input type="hidden" name="filename" value="<%=fileName%>" class="textb">
        </div>
</div>
      </div>
    </div>
  </section>