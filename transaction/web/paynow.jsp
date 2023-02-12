<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Functions" %>
<style>
    .panelheading_color
    {
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    }
    .headpanelfont_color
    {
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
    }
    .bodypanelfont_color
    {
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    }
    .panelbody_color
    {
    <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    }
    .mainbackgroundcolor
    {
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }
    .bodybackgroundcolor
    {
    <%=session.getAttribute("bodybgcolor")!=null?"background-color:"+session.getAttribute("bodybgcolor").toString()+"!important":""%>;
    }
    .bodyforegroundcolor
    {
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }
    .navigation_font_color
    {
    <%=session.getAttribute("navigation_font_color")!=null?"background-color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    }
    .textbox_color
    {
    <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
    }
    .icon_color
    {
    <%=session.getAttribute("icon_color")!=null?"background-color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
    }

</style>
<style>

    @media(max-width:768px)
    {
        #h2carddetails {
            margin-left: -38px;
            margin-bottom: 5px;
            margin-top: -20px;
        }

        #creditlyfield {
            margin-left: -59px;

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
    @media(min-width:890px) {
        #inlinefield {
            display:inline-flex
        }
    }

</style>
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
    final String CRE_CUSTOMERID=rb.getString("CRE_CUSTOMERID");
    Functions functions = new Functions();

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
    String customerId = "";
    //System.out.println("cust id-----"+standardKitValidatorVO.getCustomerId());
    if (functions.isValueNull(standardKitValidatorVO.getCustomerId()))
        customerId = standardKitValidatorVO.getCustomerId();

    String addressDetails = getValue(standardKitValidatorVO.getTerminalVO().getAddressDetails());
    String addressValidation = getValue(standardKitValidatorVO.getTerminalVO().getAddressValidation());

%>



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
