<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/10/2016
  Time: 12:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;chNapcoarset=UTF-8" language="java" %>

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

  final String KEY_MERCHANTTRANSACTIONID= rb.getString("CRE_MERCHANTTRANSACTIONID");
  final String CRE_TRANSACTIONDETAILS=rb.getString("CRE_TRANSACTIONDETAILS");
  final String KEY_PAYMENTID = rb.getString("CRE_PAYMENTID");
  final String KEY_DESCRIPTION=rb.getString("CRE_DESCRIPTION");
  final String KEY_ORDERDESCRIPTION= rb.getString("CRE_ORDERDESCRIPTION");
  final String CRE_AMOUNT= rb.getString("CRE_AMOUNT");
%>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");

  GenericTransDetailsVO genericTransDetailsVO = standardKitValidatorVO.getTransDetailsVO();
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  MerchantDetailsVO merchantDetailsVO= standardKitValidatorVO.getMerchantDetailsVO();
  RecurringBillingVO recurringBillingVO= standardKitValidatorVO.getRecurringBillingVO();

  String currency = getValue(standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency());
  String trackingid = getValue(standardKitValidatorVO.getTrackingid());
  String amount = getValue(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount());
  String description = getValue(genericTransDetailsVO.getOrderId());
  String orderdescription = getValue(genericTransDetailsVO.getOrderDesc());
  String accountid= getValue(merchantDetailsVO.getAccountId());
%>
<style type="text/css">

  @media(max-width:768px)
  {
    #fielddisabled
    {
      display: none;
    }
    #amtlabel
    {
      text-indent: -13px;
    }
    #orderamount
    {
      right: -13px;
    }
    #h2order
    {
      margin-top: 1px;
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
      margin-left: 32px;
    }
    #orderid
    {
      display:none;
    }


  }
  @media(min-width:768px)
  {
    #orderamount
    {
      left: 0%;
      top:1px;
    }
  }
  </style>
<div class="row" style="margin-top: 1;">
  <div class="col-md-6" id="orderid">

      <label for="description" style="font-weight: 100;font-size: 13px;"><%=KEY_MERCHANTTRANSACTIONID%><b>:</b> </label>

      <label id="description" style="font-weight: 100;font-size: 13px;word-break: break-all;"><%=description%></label>

  </div>
  <div class="col-md-6" id="orderamount">
    <label  id="amtlabel" style="font-weight: 100;font-size: 13px;padding: 0;">Order <%=CRE_AMOUNT%> <b>:</b></label>
    <label  style="font-weight: 100;text-indent: 0px;font-size: 13px;padding: 0;"><%=currency%>&nbsp;<%=amount%></label>
  </div>
</div>
<%--
<h4 id="fielddisabled" style="text-align: center;" class="col-md-12 background panelheading_color headpanelfont_color" style="margin-bottom:30px;"><%=CRE_TRANSACTIONDETAILS%></h4>
--%>
<%--<div class="form-group col-md-6" id="fielddisabled" style="font-size: 13px;">
  <label style="font-weight: 100;font-size: 13px;">Payment ID</label>
  </div>
<div class="form-group col-md-6" id="fielddisabled" style="font-size: 13px;">
  <label  style="font-weight: 100;font-size: 13px;"><%=trackingid%></label>
</div>--%>

<%--<div class="col-md-12" style="padding: 5px;">

</div>

<div class="col-md-12" style="padding: 5px;">
  <div class="form-group col-md-6 col-lg-6" id="fielddisabled" style="font-size: 13px;padding: 0;">
    <label  for="orderdescription" style="font-weight: 100;font-size: 13px;"><%=KEY_ORDERDESCRIPTION%></label>
  </div>
  <div class="form-group col-md-6 col-lg-6" id="fielddisabled" style="font-size: 13px;padding: 0;">
    <label  id="orderdescription" name="orderdescription" style="font-weight: 100;font-size: 13px;word-break: break-all;"><%=orderdescription%></label>
  </div>
</div>--%>



<%--<div class="form-group col-md-12" style="background:#7eccad!important;color: #ffffff;margin-bottom: 0px;padding-top: 10px;">


  </div>--%>

<%--<div class="form-group col-sm-6">
  &lt;%&ndash;<input type="text" class="form-control" disabled="" id="amount" placeholder="<%=CRE_AMOUNT%>"  value="<%=currency%>&nbsp;<%=amount%>" >&ndash;%&gt;
  <label  id="amount" style="font-weight: 100;"></label>
</div>--%>

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
