<%--
  Created by IntelliJ IDEA.
  User: Uday
  Date: 8/11/17
  Time: 3:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  String email = getValue(genericAddressDetailsVO.getEmail());
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
  }

</style>
<div class="col-md-8 portlets ui-sortable" id="creditlyfield">
  <div class="form-group col-md-7">
    <label for="email">Email ID</label>
    <input type="text" name="emailaddr" id="email" size="30" placeholder="Email ID/User ID" value="<%=email%>" class="form-control textbox_color"/>

  </div>
</div>
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