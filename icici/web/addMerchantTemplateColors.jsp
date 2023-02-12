<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.enums.TemplatePreference" %>


<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 9/4/2021
  Time: 3:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<script type="text/javascript">
    function getSave() {
        document.getElementById('Save').submit();
    }
</script>

<%!
    private static Logger log = new Logger("addMerchantTemplateColors.jsp");
%>
<%
    Functions functions = new Functions();
    String memberid = "";
    String errormsg = (String) request.getAttribute("cbmessage");
    String error = (String) request.getAttribute("errormessage");

    if (functions.isValueNull(request.getParameter("memberid")))
        memberid = request.getParameter("memberid");
    else
        memberid = "";
%>
<body background="bodybackground">
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session)){
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Add Template Colors
                <div style="float: right;">
                    <form action="/icici/servlet/MerchantDetails?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in" ></i> &nbsp;&nbsp; Merchant Details
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/AddMerchantTemplateColors?ctoken=<%=ctoken%>" method="POST" name="Save" id="Save">
                <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left: 1.5%;margin-right: 2.5%;">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb"> Merchant Id* </td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input class="txtbox" name="memberid" id="mid1" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable" style="overflow: auto">
    <%
        Map<String,Object> merchantTemplateColors = null;
        if (request.getAttribute("merchantTemplateColors") != null){
            merchantTemplateColors = (Map<String, Object>) request.getAttribute("merchantTemplateColors");
    %>
    <form action="/icici/servlet/SetMerchantReservesTemplate?ctoken=<%=ctoken%>" method="POST">
        <input type="hidden" name="memberid" value="<%=memberid%>">
        <div>
            <table align=center border="1" cellpadding="2" cellspacing="0" width="10%" class="table table-striped table-bordered table-green dataTable">
                <tr></tr>
                <tr>
                    <td  valign="middle" align="center" class="th0" colspan=6 ><font size=2>New Checkout Template Colors</font></td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center">Body color </td>
                    <td data-label="Template BackGround C22olor">
                        <input type="text"id="newbackgroundcolor"class="txtbox" align="right" name='<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>' value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()) : "" %>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Header Background Color  </td>
                    <td  data-label="Panel Heading Color" >
                        <input  type="text"id="newpanelheadingcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()) : "" %>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Navigation Bar </td>
                    <td  data-label="Left Navigation Color" >
                        <input  type="text"id="newleftnavigationcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()) : "" %>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" > Button Font Color </td>
                    <td data-label="Label Font Color">
                        <input type="text"id="newfontcolor"  class="txtbox" name='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>' value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()) : "" %>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Header Font Color</td>
                    <td  data-label="Panel Body Color">
                        <input type="text"id="newhadinglabelfontcolor"class="txtbox" size="50" name='<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()) : "" %>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Full Background Color</td>
                    <td  data-label="Body BackGround Color">
                        <input type="text"id="newbodybackgroundcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()) : "" %>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" > Label Font Color </td>
                    <td  data-label="Body ForeGround Color">
                        <input type="text"id="newbodyforegroundcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()) : "" %>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Navigation Bar Font Color </td>
                    <td  data-label="Navigation Font Color">
                        <input type="text"id="newnavigationfontcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()) : "" %>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Button Color</td>
                    <td  data-label="Textbox Color">
                        <input type="text"id="newtextboxcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()) : "" %>"/>
                    </td>
                </tr>
                <tr>
                <td class="textb"  valign="middle" align="center" > Icon Color </td>
                <td  data-label="Textbox Color">
                    <input type="text"id="newiconvectorcolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()) : "" %>"/>
                </td>
                <td class="textb"  valign="middle" align="center" > Timer Color </td>
                <td  data-label="Timer Color">
                    <input type="text"id="newtimercolor"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()) : "" %>"/>
                </td>
                <td class="textb"  valign="middle" align="center" > Box Shadow </td>
                <td  data-label="Box Shadow">
                    <input type="text"id="newboxshadow"class="txtbox"name='<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()) : "" %>"/>
                </td>
                </tr>
                <tr>

                    <td class="textb" valign="middle" align="center">Footer Font Color</td>

                    <td data-label="Footer Color">
                        <input type="text" id="newfooterfontcolor" class="txtbox" name='<%=TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()) : "" %>"/>
                    </td>

                    <td class="textb" valign="middle" align="center">Footer Background Color</td>
                    <td data-label="Footer Background Color">
                        <input type="text" id="newfooterbackgroundcolor" class="txtbox" name='<%=TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(merchantTemplateColors!=null && merchantTemplateColors.containsKey(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString())) ? merchantTemplateColors.get(TemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()) : "" %>"/>
                    </td>
                </tr>
            </table>
            <div align="center">
                <tr>
                    <td >
                        <button type="submit" value="Save" class="buttonform btn btn-default" name="Save" onclick="getSave()">
                            Save
                        </button>
                    </td>
                </tr>
            </div>
        </div>
    </form>
</div>
<%
        }
        else if (functions.isValueNull(error))
            out.println("<h5 class=\"textb\" style=\"text-align: center;\">" + error+ "</h5>");
        else if (functions.isValueNull(errormsg))
            out.println("<h5 class=\"textb\" style=\"text-align: center;\">" + "Details Updated Succesfully"+ "</h5>");
        else
            out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
    }
    else{
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>
