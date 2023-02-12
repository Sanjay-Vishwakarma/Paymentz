<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/17/2016
  Time: 3:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Logger log = new Logger("footerlogo.jsp");

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  String powerBy = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPoweredBy());
  String sisaLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSisaLogoFlag());
  String partnerLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag());
  String vbvLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getVbvLogo());
  String masterSecureLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMasterSecureLogo());

  String logoName = getValue(standardKitValidatorVO.getMerchantDetailsVO().getLogoName());
  log.debug("merchant logo name from footerlogo.jsp----"+logoName);
  final ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.CertificateLink");
  String pciurl= RBundel1.getString("PCILINK");
%>
<style>


  .box {
    width: 30%;
    display:inline-block;
    margin:30px 0;
    border-radius:5px;
  }
  .text {
    padding: 10px 0;
    color:white;
    font-weight:bold;
    text-align:center;
  }
  #container {
    white-space:nowrap;
    text-align:center;
  }

  @media(max-width: 767px){
    #img1
    {
      height: 22px;
    }
    #img3
    {
      height: 21px;

    }
    #img2
    {
      margin-top: -25px;
    }

  }
  @media(min-width: 767px) {
    /*#img1
    {
      width: 146px;
      height: 30px;
    }
    #img3
    {
      height:30px;
    }*/

  }

</style>


<%
  String footerLogo1 = "";
  String divVisa = "";
  String divMaster = "";

  if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
  {
    if(vbvLogoFlag.equalsIgnoreCase("Y"))
    {
      logoName = "VISA_Verified.png";

    }
    else{
      divVisa = "width: 146px;height: 30px;";
    }
%>

<div id="container">
  <div class="box">
    <div class="text"><img border="0" style="<%=divVisa%>"  id="img1" src="/images/merchant/<%=logoName%>"></div>
  </div>

  <%
    }
    if(!sisaLogoFlag.equals(null) && sisaLogoFlag.equalsIgnoreCase("Y"))
    {
  %>
  <div class="box">
    <div class="text"><a target="_blank" href="<%=pciurl%>"><IMG
            border="0" height="50" id="img2" src="/images/merchant/pci_dss_logo.png" style="width: 70px;"></a></div>
  </div>
  <%
    }
    if(!powerBy.equals(null) && powerBy.equalsIgnoreCase("Y"))
    {
      String footerLogo2 = "";
      if(masterSecureLogoFlag.equalsIgnoreCase("Y"))
      {
        footerLogo2 = "MC_Secure.png";
      }
      else
      {
        footerLogo2 = "poweredBy_logo.png";
        divMaster = "height:30px;";
      }

  %>

  <div class="box">
    <div class="text"><a href="http://www.pz.com"><img border="0" style="<%=divMaster%>"  id="img3" src="/images/merchant/<%=footerLogo2%>" ></a></div>
  </div>
</div>
<%
  }
%>

<%--<%
  if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
  {
%>
<div class="col-xs-6 col-sm-2" id="logo1">
  <p align="center"><img border="0" height="" ID="IMAGE1" src="/images/merchant/<%=logoName%>"></p>
</div>
<%
  }
  if(!LogoFlag.equals(null) && LogoFlag.equalsIgnoreCase("Y"))
  {
%>
<div class="col-xs-6 col-sm-2" id="logo2">
  <p align="center"><a target="_blank" href="https://abc.com/site/certificate/33738421474266431556"><IMG
          border="0" height="" src="/images/merchant/pci_dss_logo.png" style="width: 70px;"></a></p>
</div>
<%
  }
  if(!powerBy.equals(null) && powerBy.equalsIgnoreCase("Y"))
  {
%>
<div class="col-xs-6 col-sm-2" id="logo3">
  <p align="center"><a href="http://www.<companyname>.com"><img border="0" height="" ID="IMAGE3" src="/images/merchant/poweredBy_logo.png" ></a></p>
</div>
<%
  }
%>--%>

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