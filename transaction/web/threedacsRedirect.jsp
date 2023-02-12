<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 10/9/2020
  Time: 5:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
  <script>
    function load(){
      console.log("--load--");
      var timeZoneOffSet=new Date().getTimezoneOffset();
      var w = window.innerWidth;
      var h = window.innerHeight;
      var lang = navigator.language;
      var colordepth=screen.colorDepth;
      var javaEnabled=navigator.javaEnabled();
      console.log("--browserTimezoneOffset--:"+timeZoneOffSet);
      console.log("--browserScreenHeight--:"+h);
      console.log("--browserScreenWidth--:"+w);
      console.log("--browserLanguage--:"+lang);
      console.log("--browserColorDepth--:"+colordepth);
      console.log("--browserJavaEnabled--:"+javaEnabled);
      document.getElementById("browserTimezoneOffset").value=timeZoneOffSet;
      document.getElementById("browserScreenHeight").value=h;
      document.getElementById("browserScreenWidth").value=w;
      document.getElementById("browserLanguage").value=lang;
      document.getElementById("browserColorDepth").value=colordepth;
      document.getElementById("browserJavaEnabled").value=javaEnabled;

      document.threeDForm.submit();
    }
  </script>
</head>
<%
  Functions functions   = new Functions();
  String trackingId     = request.getParameter("trackingId");
  String MD             = request.getParameter("MD");
  String PaRes          = "";
  String DM             = "";
  String DB             = "";
  String threeDSServerTransID = request.getParameter("threeDSServerTransID");
  if(!functions.isValueNull(threeDSServerTransID)){
   threeDSServerTransID=request.getParameter("threeDsTransId");
  }
  if(!functions.isValueNull(MD)){
    MD = request.getParameter("Md");
  }
  if(functions.isValueNull(request.getParameter("PaRes"))){
    PaRes = request.getParameter("PaRes");
  }
  if (functions.isValueNull(request.getParameter("DM")))
  {
    DM = request.getParameter("DM");
    if (functions.isValueNull(DM) && DM.contains(" "))
    {
      DM = DM.replace(" ","+");
    }
  }
  if (functions.isValueNull(request.getParameter("DB")))
  {
    DB = request.getParameter("DB");
    if (functions.isValueNull(DB) && DB.contains(" "))
    {
      DB = DB.replace(" ","+");
    }
  }
%>
<body onload="load()">
<form action="/transaction/ThreedacsRedirect" method="post" name="threeDForm">
  <input type="hidden" name="trackingId" value="<%=trackingId%>">
  <input type="hidden" name="MD" value="<%=MD%>">
  <input type="hidden" name="threeDSServerTransID" value="<%=threeDSServerTransID%>">
  <input type="hidden" name="PaRes" value="<%=PaRes%>">
  <input type="hidden" name="DM" value="<%=DM%>">
  <input type="hidden" name="DB" value="<%=DB%>">
  <input type="hidden" name="browserTimezoneOffset" id="browserTimezoneOffset" value="">
  <input type="hidden" name="browserScreenHeight" id="browserScreenHeight" value="">
  <input type="hidden" name="browserScreenWidth" id="browserScreenWidth" value="">
  <input type="hidden" name="browserLanguage" id="browserLanguage" value="">
  <input type="hidden" name="browserColorDepth" id="browserColorDepth" value="">
  <input type="hidden" name="browserJavaEnabled" id="browserJavaEnabled" value="">
</form>
</body>
</html>
