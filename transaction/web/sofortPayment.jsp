<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 21/1/15
  Time: 9:24 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>
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

    final String CRE_PHONENO=rb.getString("CRE_PHONENO");
    final String CRE_PHONECC=rb.getString("CRE_PHONECC");
    final String CRE_EMAIL=rb.getString("CRE_EMAIL");

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

    String addressDetails = getValue(standardKitValidatorVO.getTerminalVO().getAddressDetails());
    String addressValidation = getValue(standardKitValidatorVO.getTerminalVO().getAddressValidation());
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
            margin-left: -68px;

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
            margin-left: -33px;
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

<section class="creditly-wrapper" style="margin-bottom: -46px;">
    <div class="credit-card-wrapper"  id="creditlyfield">
        <div class="first-row form-group" id="form-group">
            <div class=" form-group col-md-12 controls">
                <%if(fileName!=null){%>
                <jsp:include page="<%=fileName%>"></jsp:include>
                <%}%>
                <%
                    if(addressDetails.equalsIgnoreCase("Y") || /*addressValidation.equalsIgnoreCase("Y") ||*/ (addressDetails.equalsIgnoreCase("N") && addressValidation.equalsIgnoreCase("Y")))
                    {
                %>
                <div class="form-group  col-md-3 controls" id="fields" >
                    <label class="control-label headpanelfont_color telnocc" ><%=CRE_PHONECC%></label>
                    <input type="text" name="telnocc" size="5" id="telnocc" class="form-control textbox_color" value="<%=telnocc%>" title="allow only numeric value">

                </div>
                <div class="form-group  col-md-3 controls" id="fields">
                    <label class="control-label headpanelfont_color telnocc"><%=CRE_PHONENO%></label>
                    <input type="text" name="telno" size="20" id="telno" value="<%=telno%>" class="form-control textbox_color" title="allow only numeric value">

                </div>
                <%
                    }
                %>
                <div class="form-group col-md-6 controls " id="fields">
                    <label class="control-label headpanelfont_color" for="emailaddr"><%=CRE_EMAIL%></label>
                    <input type="text" name="emailaddr" id="emailaddr" class="form-control textbox_color"  size="30" placeholder="<%=CRE_EMAIL%>" value="<%=email%>"/>
                </div>

                <input type="hidden" name="street" value="<%=street%>">
                <input type="hidden" name="city" value="<%=city%>">
                <input type="hidden" name="zip" value="<%=zip%>">
                <input type="hidden" name="state" value="<%=state%>">
                <input type="hidden" name="countrycode" value="<%=country%>">

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