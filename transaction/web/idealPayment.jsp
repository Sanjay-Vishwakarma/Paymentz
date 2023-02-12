<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 21/1/15
  Time: 9:24 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.payment.sofort.IdealPaymentGateway" %>
<%@ page import="com.sofort.lib.products.response.IDealBanksResponse" %>
<%@ page import="com.sofort.lib.products.response.parts.IDealBank" %>
<%@ page import="java.util.List" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<html>
<head>
    <title></title>
</head>

<%
    ResourceBundle rb = null;
    //zh-CN;q=0.8,en-US;q=0.5,en;q=0.3
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
    final String CRE_BANK=rb.getString("CRE_BANK");

    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
    String fileName = getValue((String) session.getAttribute("filename"));
    String email = getValue(genericAddressDetailsVO.getEmail());
    String city = getValue(genericAddressDetailsVO.getCity());
    String street = getValue(genericAddressDetailsVO.getStreet());
    String zip = getValue(genericAddressDetailsVO.getZipCode());
    String state = getValue(genericAddressDetailsVO.getState());
    String country = getValue(genericAddressDetailsVO.getCountry());
    String telno = getValue(genericAddressDetailsVO.getPhone());
    String telnocc = getValue(genericAddressDetailsVO.getTelnocc());
    String TMPL_COUNTRY = getValue(genericAddressDetailsVO.getCountry());
    String firstname = getValue(genericAddressDetailsVO.getFirstname());
    String lastname = getValue(genericAddressDetailsVO.getLastname());

    MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
    String accountId = getValue(merchantDetailsVO.getAccountId());

    IdealPaymentGateway idealPaymentGateway = new IdealPaymentGateway(accountId);
    IDealBanksResponse iDealBanksResponse = idealPaymentGateway.getIDealBankDetails();
    
%>
<body>
<table  style="border:1px solid #34495e" bgcolor="white" align="center" width="50%" cellpadding="2" cellspacing="2">
    <tr>
        <td style="padding:2px ">
            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                    <td style="padding:3px "bgcolor="#007ACC" colspan="4" class="texthead"><i class="fa fa-table " style="float:left;padding-top:9px;"></i></td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>


                <tr>
                    <td style="padding:3px " class="textb">&nbsp;</td>
                    <td style="padding:3px " class="textb"><%=CRE_BANK%></td>
                    <td style="padding:3px " class="textb">:</td>
                    <td style="padding:3px ">

                        <select size="1" name="senderBankCode" class="txtbox textbox_color">
                            <option value="">Select Bank</option>
                            <%

                                String selected = "";
                                String key = "";
                                String value = "";
                                for (IDealBank bank : iDealBanksResponse.getBanks()) {
                                    // build a bank selection

                                    key = bank.getCode();
                                    value = bank.getName();

                            %>
                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>
                            <%
                                }
                            %>
                        </select>
                    </td>




                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

            </table>
        </td>
    </tr>
</table>
<input type="hidden" name="telnocc" value="<%=telnocc%>">
<input type="hidden" name="telno" value="<%=telno%>">
<input type="hidden" name="emailaddr" value="<%=email%>">
<input type="hidden" name="street" value="<%=street%>">
<input type="hidden" name="city" value="<%=city%>">
<input type="hidden" name="zip" value="<%=zip%>">
<input type="hidden" name="state" value="<%=state%>">
<input type="hidden" name="countrycode" value="<%=country%>">

</body>
</html>
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