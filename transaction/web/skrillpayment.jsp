<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 23/12/17
  Time: 2:30 PM
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
        #h2carddetails {
            margin-left: -38px;
            margin-bottom: 5px;
            margin-top: -20px;
        }

        #creditlyfield {
            margin-left: -58px;

        }
        #form-group
        {
            width:100%
        }

        /*#orderdetailsdiv {

          position: absolute;
          width: 94%;
          *//* width: 92%; *//*
          margin-top: -98px;
          border: 0px;
          border-top: 0px;
          float: right;
          margin-left: 14px;
        }*/
        #fields
        {
            padding-left: 8px;
        }

    }
    @media(min-width:768px)
    {
        #creditlyfield
        {
            margin-left: -43px;
            margin-top: 29px;
        }
        #telnocc
        {
            width: 80%;
        }
        #form-group
        {

        }

    }

</style>
<style>
    @media(max-width:768px)
    {
        .creditly-wrapper .form-group
        {
            display: table;
        }
    }
</style>
<section class="creditly-wrapper" style="margin-bottom: -46px;">
    <div class="credit-card-wrapper"  id="creditlyfield">
        <div class="first-row form-group" id="form-group">
            <div class=" form-group col-md-12 controls">
<div class="form-group col-md-12 controls">
    <label for="email" class="headpanelfont_color">Email ID</label>
    <input type="text" name="emailaddr" id="email" size="30" placeholder="Email ID" value="<%=email%>" class="form-control textbox_color"/>
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