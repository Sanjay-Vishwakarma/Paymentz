<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/14/2016
  Time: 4:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  ResourceBundle rb = null;
  String multiLanguage = request.getHeader("Accept-Language");
  String sLanguage[] = multiLanguage.split(",");
  if("zh-CN".contains(sLanguage[0]))
  {
    rb = LoadProperties.getProperty("com.directi.pg.creditpage", "zh");
  }
  else
  {
    rb = LoadProperties.getProperty("com.directi.pg.creditpage");
  }

  final String CRE_TERMSCONDITION=rb.getString("CRE_TERMSCONDITION");
  final String CRE_TNC=rb.getString("CRE_TNC");
  final String CRE_PRIVACYPOLICY=rb.getString("CRE_PRIVACYPOLICY");
  final String CRE_NOTE=rb.getString("CRE_NOTE");
  final String CRE_NOTE1=rb.getString("CRE_NOTE1");
  final String CRE_ORDER1=rb.getString("CRE_ORDER1");
  final String CRE_ORDER2=rb.getString("CRE_ORDER2");
  final String CRE_ORDER3=rb.getString("CRE_ORDER3");
  final String CRE_ORDER4=rb.getString("CRE_ORDER4");
  final String CRE_PAYNOW=rb.getString("CRE_PAYNOW");
%>
<%
  Logger log = new Logger("standardcheckout.jsp");

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  Functions functions = new Functions();

  GenericTransDetailsVO genericTransDetailsVO = standardKitValidatorVO.getTransDetailsVO();
  MerchantDetailsVO merchantDetailsVO= standardKitValidatorVO.getMerchantDetailsVO();
  RecurringBillingVO recurringBillingVO= standardKitValidatorVO.getRecurringBillingVO();
  String trackingid = getValue(standardKitValidatorVO.getTrackingid());
  String amount = getValue(genericTransDetailsVO.getAmount());
  String description = getValue(genericTransDetailsVO.getOrderId());
  String orderdescription = getValue(genericTransDetailsVO.getOrderDesc());
  String childFile=getValue((String)session.getAttribute("childfile"));
  String terminalid= getValue(standardKitValidatorVO.getTerminalId());
  String paymentType =getValue(standardKitValidatorVO.getPaymentType());
  log.debug("Card type in commonPayment----" + getValue(standardKitValidatorVO.getCardType()));
  log.debug("Child File ----" + childFile);
  String cardType= getValue(standardKitValidatorVO.getCardType());
  String fromid= getValue(standardKitValidatorVO.getTransDetailsVO().getFromid());
  String fromType = getValue(standardKitValidatorVO.getTransDetailsVO().getFromtype());
  String partnerId = getValue(standardKitValidatorVO.getParetnerId());
  String logoName = getValue(standardKitValidatorVO.getLogoName());
  String partnerName = getValue(standardKitValidatorVO.getPartnerName());
  String powerBy = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPoweredBy());

  String ip= getValue(standardKitValidatorVO.getAddressDetailsVO().getIp());
  String customerIp=standardKitValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
  String requestedIp=standardKitValidatorVO.getRequestedIP();
  log.debug("vo----"+getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()));
  log.debug("session----" + session.getAttribute("merchantLogoName"));
  String merchantLogo = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
  String merchantLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo());
  String fraudCheck = getValue(merchantDetailsVO.getOnlineFraudCheck());

  String tmplAmount = getValue(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount());
  String tmplCurrency = getValue(standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency());
  String currency = getValue(standardKitValidatorVO.getTransDetailsVO().getCurrency());
  String currencyConversion = getValue(standardKitValidatorVO.getCurrencyConversion());
  String conversionCurrency = getValue(standardKitValidatorVO.getConversionCurrency());
  //System.out.println("currency tnc---"+currency);
  log.debug("terms & condition---"+standardKitValidatorVO.getMerchantDetailsVO().getTcUrl());
  String isRecurring = "";
  String interval = "";
  String frequency = "";
  String runDate = "";

  if(recurringBillingVO!= null)
  {
    isRecurring = getValue(standardKitValidatorVO.getRecurringBillingVO().getRecurring());
    interval = getValue(standardKitValidatorVO.getRecurringBillingVO().getInterval());
    frequency = getValue(standardKitValidatorVO.getRecurringBillingVO().getFrequency());
    runDate = getValue(standardKitValidatorVO.getRecurringBillingVO().getRunDate());
  }

  if(merchantLogoFlag.equalsIgnoreCase("Y"))
  {

    merchantLogo = (String)session.getAttribute("merchantLogoName");
    log.debug("merchantLogo session----"+merchantLogo);

  }
  else
  {
    merchantLogo = standardKitValidatorVO.getLogoName();
    log.debug("merchantLogo vo----"+merchantLogo);

  }
  Calendar cal=Calendar.getInstance();
  cal.setTimeInMillis(session.getLastAccessedTime());
  String ctoken = (String) session.getAttribute("ctoken");

  String company = getValue(standardKitValidatorVO.getMerchantDetailsVO().getCompany_name());
  String address = getValue(standardKitValidatorVO.getMerchantDetailsVO().getAddress())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getState())+" "+
          getValue(standardKitValidatorVO.getMerchantDetailsVO().getCountry())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getZip());
  String telNo = getValue(standardKitValidatorVO.getMerchantDetailsVO().getTelNo());
  String email = getValue(standardKitValidatorVO.getMerchantDetailsVO().getContact_emails());
  String accountId = getValue(standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
  String supportSection = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportSection());
  //System.out.println("support---"+standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport());
  String billigDesc = "";
  String billingStatement = "";
  if(functions.isValueNull(accountId))
  {
    billigDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
    billingStatement = "The Charge Descriptor on your Bank statement will appear as <font style='font-weight: bold;'><br>"+billigDesc+"</font>";
  }

%>
<style>
  @media(max-width:768px)
  {
    #paynow
    {
      border-color: #7eccad;
      background-color: #7eccad;
      margin-left: 0%;
      width: 100%;
    }

    #chkdiv
    {
      background-color: #fff;
      padding: 20px;
      padding-bottom: 0;
      margin-left: -35px;
      width: 118%;
    }
    #note
    {
      margin-left: -12px;
    }


  }
  @media(min-width:768px)
  {
    #paynow
    {
      border-color: #7eccad;
      background-color: #7eccad;
      width: 100%;
      margin-bottom: 30px;
    }
    #note
    {

      margin-left:8px;
    }
  }

  @media(max-width: 767px){
    #supportid{
      margin-left: -12px;
    }
  }

  #supportid{
    margin-left: -24px;
  }


</style>
<br>
<div class="row" id="supportid">
  <div  class="col-md-11">
    <div align="left" class="textb" id="note">

      <%
        //System.out.println("MerchantLogo----"+standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo()+"--PartnerLogo---"+standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag());
        if(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo().equalsIgnoreCase("Y"))
        {
          //System.out.println("inside Merchant---TCURL--"+standardKitValidatorVO.getMerchantDetailsVO().getTcUrl()+"----PrivacyPolicy---"+standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl());
          if(functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getTcUrl()) && functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl()))
          {
            if(supportSection.equalsIgnoreCase("Y"))
            {
      %>

      <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%><a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getTcUrl()%>" target="_blank"><%=CRE_TNC%></a><br><br>
      <input type="checkbox" id="policy" value="pp" name="PP"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl()%>" target="_blank"><%=CRE_PRIVACYPOLICY%></a><br><br>
        <%
            }
            else
            {
        %>
        <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getTcUrl()%>" target="_blank"><%=CRE_TNC%></a> & <a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl()%>" target="_blank"><%=CRE_PRIVACYPOLICY%></a><br><br>
        <%
            }
        }
        else
        {
          if(supportSection.equalsIgnoreCase("Y"))
          {
        %>
      <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%><a href="#"><%=CRE_TNC%></a><br>
      <input type="checkbox" id="policy" value="pp" name="PP"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="#"><%=CRE_PRIVACYPOLICY%></a><br><br>
      <%
          }
          else
          {
      %>
      <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="#"><%=CRE_TNC%></a> & <a href="#"><%=CRE_PRIVACYPOLICY%></a><br><br>
      <%
          }
        }
        }
         else if(standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag().equalsIgnoreCase("Y"))
         {
         //System.out.println("inside partner---TCURL--"+standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl()+"----PartenerPrivacyPolicy---"+standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl());
          if(functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl()) && functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl()))
          {
            if(supportSection.equalsIgnoreCase("Y"))
            {
      %>
      <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%><a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl()%>" target="_blank"><%=CRE_TNC%></a><br>
      <input type="checkbox" id="policy" value="pp" name="PP"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl()%>" target="_blank"><%=CRE_PRIVACYPOLICY%></a><br><br>
          <%
          }
          else
          {
          %>
          <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl()%>" target="_blank"><%=CRE_TNC%></a> & <a href="<%=standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl()%>" target="_blank"><%=CRE_PRIVACYPOLICY%></a><br><br>
          <%
          }
          }
          else
          {
          if(supportSection.equalsIgnoreCase("Y"))
          {
          %>
      <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%><a href="#"><%=CRE_TNC%></a><br>
      <input type="checkbox" id="policy" value="pp" name="PP"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="#"><%=CRE_PRIVACYPOLICY%></a><br><br>
          <%
              }
              else
              {
          %>
      <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="#"><%=CRE_TNC%></a> & <a href="#"><%=CRE_PRIVACYPOLICY%></a><br><br>
          <%
              }
          }
        }
        else
         {
           if(supportSection.equalsIgnoreCase("Y"))
           {
          %>
        <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%><a href="#"><%=CRE_TNC%></a><br></b>
        <input type="checkbox" id="policy" value="pp" name="PP"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="#"><%=CRE_PRIVACYPOLICY%></a><br><br></b>
          <%
              }
              else
              {
          %>
        <input type="checkbox" id="terms" value="tc" name="TC"  onclick="submitForm('<%=supportSection%>');">&nbsp;<b class="headpanelfont_color"><%=CRE_TERMSCONDITION%> <a href="#"><%=CRE_TNC%></a> & <a href="#"><%=CRE_PRIVACYPOLICY%></a><br><br>
          <%
              }
            }
          %>

          <input type="submit" id="paynow" name="Pay" value="<%=CRE_PAYNOW%>" onclick="lancer()" class="btn btn-info form foreground bodypanelfont_color panelbody_color" disabled="true">
          <div style="margin-bottom: 0px;"><%--<%=CRE_NOTE%>&nbsp;&nbsp;<%=CRE_NOTE1%>--%></div></b>
    </div>

    <%
      if("Y".equalsIgnoreCase(supportSection))
      {
    %>

    <div id="container" class="foreground bodypanelfont_color panelbody_color">
      <div class="" style="margin: 0!important; white-space: normal!important;">
        <div class="text" style="font-weight: normal!important;">
          <p class="" style="margin-bottom: 0!important;">
            <%=company%><br>
            <%=address%><br>
            <%=telNo%><br>
            <%=email%><br>
            <%=billingStatement%>
          </p>
        </div>
      </div>
    </div>
    <%
      }
    %>

    <input type="hidden" name="terminalid" value="<%=terminalid%>">
    <input type="hidden" name="paymenttype" value="<%=paymentType%>">
    <input type="hidden" name="cardtype" value="<%=cardType%>">
    <input type="hidden" name="paymentMode" value="<%=standardKitValidatorVO.getPaymentMode()%>">
    <input type="hidden" name="paymentBrand" value="<%=standardKitValidatorVO.getPaymentBrand()%>">
    <input type="hidden" name="TMPL_amount" value="<%=tmplAmount%>">
    <input type="hidden" name="TMPL_currency" value="<%=tmplCurrency%>">
    <input type="hidden" name="currency" value="<%=currency%>">
    <input type="hidden" value="<%=fromid%>" name="fromid">
    <input type="hidden" value="<%=fromType%>" name="fromtype">
    <input type="hidden" name="toid" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMemberId()%>">
    <input type="hidden" name="totype" value="<%=standardKitValidatorVO.getTransDetailsVO().getTotype()%>">
    <input type="hidden" name="redirecturl" value="<%=standardKitValidatorVO.getTransDetailsVO().getRedirectUrl()%>">
    <input type="hidden" name="trackingid" value="<%=trackingid%>">
    <input type="hidden" name="partnerid" value="<%=partnerId%>">
    <input type="hidden" name="logoname" value="<%=logoName%>">
    <input type="hidden" name="partnername" value="<%=partnerName%>">
    <input type="hidden" name="powerby" value="<%=powerBy%>">
    <input type="hidden" name="amount" value="<%=amount%>">
    <input type="hidden" name="orderdescription" value="<%=orderdescription%>">
    <input type="hidden" name="description" value="<%= description%>">
    <input type="hidden" name="ip" value="<%=ip%>">
    <input type="hidden" name="customerIp" value="<%=customerIp%>">
    <input type="hidden" name="requestedIp" value="<%=requestedIp%>">
    <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <input type="hidden" name="countrycode" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
    <input type="hidden" name="pgtypeid" value="<%=standardKitValidatorVO.getTerminalVO().getGateway_id()%>">

    <%-- added those fields for pbs--%>
    <input type="hidden" name="telno" value="<%=standardKitValidatorVO.getAddressDetailsVO().getPhone()%>"/>
    <input type="hidden" name="telnocc" value="<%=standardKitValidatorVO.getAddressDetailsVO().getTelnocc()%>"/>
    <input type="hidden" name="state" value="<%=standardKitValidatorVO.getAddressDetailsVO().getState()%>"/>
    <input type="hidden" name="street" value="<%=standardKitValidatorVO.getAddressDetailsVO().getStreet()%>"/>
    <input type="hidden" name="city" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCity()%>"/>
    <input type="hidden" name="zip" value="<%=standardKitValidatorVO.getAddressDetailsVO().getZipCode()%>"/>
    <%-- added those fields for pbs--%>

    <input type="hidden" name="isRecurring" value="<%=isRecurring%>">
    <input type="hidden" name="interval" value="<%=interval%>">
    <input type="hidden" name="frequency" value="<%=frequency%>">
    <input type="hidden" name="runDate" value="<%=runDate%>">
    <input type="hidden" name="onlineFraudCheck" value="<%=fraudCheck%>">
    <input type="hidden" name="addressValidation" value="<%=standardKitValidatorVO.getTerminalVO().getAddressValidation()%>">
    <input type="hidden" name="addressDetails" value="<%=standardKitValidatorVO.getTerminalVO().getAddressDetails()%>">
    <input type="hidden" name="reject3DCard" value="<%=standardKitValidatorVO.getTerminalVO().getReject3DCard()%>">
    <input type="hidden" name="isAutomaticRecurring" value="<%=standardKitValidatorVO.getTerminalVO().getIsRecurring()%>">
    <input type="hidden" name="isManualRecurring" value="<%=standardKitValidatorVO.getTerminalVO().getIsManualRecurring()%>">
    <input type="hidden" name="isPSTProcessingRequest" value="<%=standardKitValidatorVO.getIsPSTProcessingRequest()%>">
    <input type="hidden" name="accountid" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getAccountId()%>">
    <input type="hidden" name="paymentMap" value="<%=standardKitValidatorVO.getMapOfPaymentCardType()%>" />
    <input type="hidden" name="customerid" value="<%=standardKitValidatorVO.getCustomerId()%>" />
    <input type="hidden" name="customerBankId" value="<%=standardKitValidatorVO.getCustomerBankId()%>" />
    <input type="hidden" name="version" value="<%=standardKitValidatorVO.getVersion()%>" />
    <input type="hidden" name="multicurrencySupport" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport()%>">
    <input type="hidden" name="currencyConversion" value="<%=currencyConversion%>">
    <input type="hidden" name="conversionCurrency" value="<%=conversionCurrency%>">
    <input type="hidden" name="consentStmnt" value="<%=CRE_TERMSCONDITION%> <%=CRE_TNC%> & <%=CRE_PRIVACYPOLICY%>">
    <input type="hidden" name="binRouting" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getBinRouting()%>">

    <input type="hidden" value="" name="">
  </div>
</div>

<div id="poptuk" style="easing: 'easeOutBack';speed: 450;transition:'slideDown'" >
  <div id="poptuk_content">
    <div class="poptuk_body icon_color headpanelfont_color"></div>
    <div align="center">
      <table border="0" cellpadding="5" cellspacing="0" width="12%">
        <tr>
          <td align="center"><img src="/merchant/images/transactionloader.gif" style="/*margin-left: 80%; */margin-top: 10%;"></td>
        </tr>
      </table>

      <table border="0" cellpadding="5" cellspacing="0" width="50%">
        <tr>
          <td align="center" class="textb">
            <%=CRE_ORDER1%>.<br>
            <%=CRE_ORDER2%> <b> <%=CRE_ORDER3%></b>  <%=CRE_ORDER4%>
          </td>
        </tr>


      </table>
    </div>
  </div>
</div>

<script>
  function submitForm(supportSection)
  {
    if(supportSection=='Y')
    {
      if (document.getElementById("terms").checked && document.getElementById("policy").checked)
      {
        document.getElementById('paynow').disabled = false;
      }
      else
      {
        document.getElementById('paynow').disabled = true;
      }
    }
    else
    {
      if(document.getElementById("terms").checked)
      {
        document.getElementById('paynow').disabled=false;
      }
      else
      {
        document.getElementById('paynow').disabled=true;
      }
    }
  }
</script>
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