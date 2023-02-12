<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.google.zxing.qrcode.QRCodeWriter" %>
<%@ page import="com.google.zxing.common.BitMatrix" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.FileSystems" %>
<%@ page import="com.google.zxing.client.j2se.MatrixToImageWriter" %>
<%@ page import="com.google.zxing.BarcodeFormat" %>
<%@ page import="java.io.PrintWriter" %>

<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 1/23/2019
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");

  String paymodeId = "23";
  String payMode = "PLMP";

  Functions functions = new Functions();
  ResourceBundle rb = null;
  ResourceBundle rb1 = LoadProperties.getProperty("com.directi.pg.QRCode");
  String lang = "";
  String multiLanguage = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else if ("bg".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
    }
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else if(functions.isValueNull(request.getHeader("Accept-Language")))
  {
    multiLanguage = request.getHeader("Accept-Language");
    String sLanguage[] = multiLanguage.split(",");
    if(functions.isValueNull(sLanguage[0]))
    {
      if ("ja".equalsIgnoreCase(sLanguage[0]))
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else if ("bg".equalsIgnoreCase(sLanguage[0]))
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
      }
      else
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else
  {
    rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }



  String trackingId = standardKitValidatorVO.getTrackingid();
  String amount = standardKitValidatorVO.getTransDetailsVO().getAmount();
  String currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();

  String text = trackingId+" "+amount+" "+currency;
  String filePath = rb1.getString("PATH")+"MyQR_"+trackingId+".png";
  QRCodeWriter qrCodeWriter = new QRCodeWriter();
  BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200,200);

  Path path = FileSystems.getDefault().getPath(filePath);
  MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

%>

<input type="hidden" id="PLMP_ctoken" value="<%=ctoken%>" />

<div class="tab-pane " id="PLMP" style="text-align: center">
  <form id="PLMPForm" class="form-style" method="post" >
    <div class="tab" style="height: 100%">

      <%-- start of qr code --%>
      <div>
        <div class="form-label" style="font-weight: bold;">
          Scan the QR Code in your wallet
        </div>
        <div>
          <img src="images/merchant/QRCodes/MyQR_<%=trackingId%>.png" alt="MyQR" />
        </div>
      </div>
      <%-- end of qr code --%>

      <%-- start of confirmation --%>
      <div id="successMsg" style="font-weight: bold;font-size: 25px;" class="form-label hide">

      </div>
      <%-- end of confirmation --%>

    </div>
  </form>
</div>


<script>


  var confirm_status = "";
  function status()
  {
    console.log("id-----",document.getElementById("PLMP").classList.contains("active"));

    if(document.getElementById("PLMP").classList.contains("active"))
    {
      var token = document.getElementById("PLMP_ctoken").value;

      $.ajax({
        url: '/transaction/checkConfirmation?ctoken=' + token,
        async: false,
        type: 'POST',
        data: {trackingId: "<%=trackingId%>", param: "ajax"},
        success: function (data, status)
        {
          console.log(data);
          confirm_status = data;
        },
        error: function (xhr, status, error)
        {
          console.log(status, error);
        }
      });

      console.log("AFTER AJAX  ------- status ---------", confirm_status);

      if (confirm_status.status == "success" || confirm_status.status == "fail")
      {
        document.getElementById("successMsg").classList.remove("hide");
        document.getElementById("successMsg").innerHTML = confirm_status.status == "success" ? "Transaction Successful !" : "Transaction Failed !";
        document.getElementById("success_status").value=confirm_status.status;
        clearInterval(apiCall);
        document.successForm.submit();
      }

    }

  }

  var apiCall =  setInterval('status()',5000);

</script>


<form name="successForm" action="/transaction/checkConfirmation" method="post">
  <input type="hidden" name="success_status" id="success_status" value="" />
  <input type="hidden" name="trackingId" value="<%=trackingId%>" />
</form>