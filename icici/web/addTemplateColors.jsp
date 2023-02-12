<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp"%>
<%@ page import="com.manager.dao.PartnerDAO,com.manager.enums.PartnerTemplatePreference"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 1/20/2018
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
</head>
<script type="text/javascript">
    function mySave()
    {
        document.getElementById('Save').submit();
    }
</script>
<%
    Functions functions = new Functions();
    PartnerDAO partnerDAO =new PartnerDAO();
    LinkedHashMap partneriddetails=partnerDAO.getpartnerDetail();
    String partnerId = "";
    String errormsg = (String)request.getAttribute("cbmessage");
    String error=(String) request.getAttribute("errormessage");

    if (functions.isValueNull(request.getParameter("partnerId")))
    {
        partnerId = request.getParameter("partnerId");
    }
    else
    {
        partnerId = "";
    }

    String successmsg = "";

%>

<body class="bodybackground" >
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Add Template Colors
                <div style="float: right;">
                    <form action="/icici/partnerInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Partner Details
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/AddTemplateColor?ctoken=<%=ctoken%>" method=post name="Save" id="Save">
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Partner Id*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="partnerId" id="pid1" value="<%=partnerId%>" class="txtbox" autocomplete="on">
                                        <%-- <select name="partnerId" class="txtbox">
                                             <option value="" selected>--Select PartnerID--</option>

                                             <%
                                                 String selected = "";
                                                 String key = "";
                                                 String value = "";
                                                 for(Object pid:partneriddetails.keySet())
                                                 {
                                                     key = String.valueOf(pid);
                                                     value = (String) partneriddetails.get(pid);
                                                     if (key.equals(partnerId))
                                                         selected = "selected";
                                                     else
                                                         selected = "";
                                             %>
                                             <option value="<%=key%>" <%=selected%> ><%=value%></option>
                                             <%
                                                 }
                                             %>
                                         </select>--%>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
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
<div class="reporttable">
    <%
        Map<String,Object> partnerTemplateColor=null;
        if(request.getAttribute("partnerTemplateColor")!=null)
        {
            partnerTemplateColor= (Map<String, Object>) request.getAttribute("partnerTemplateColor");
    %>
    <form action="/icici/servlet/SetReservesTemplate?ctoken=<%=ctoken%>" method=post>
        <input type="hidden" name="partnerId" value="<%=partnerId%>">
        <div>
            <table align=center border="1" cellpadding="2" cellspacing="0" width="10%" class="table table-striped table-bordered table-green dataTable">
                <tr></tr>
                <tr>
                    <td  valign="middle" align="center" class="th0" colspan=6 ><font size=2> Checkout Template Colors</font></td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center">Template BackGround Color </td>
                    <td data-label="Template BackGround Color">
                        <input type="text"id="backgroundcolor"class="txtbox" align="right" name='<%=PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.toString()%>' value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAINBACKGROUNDCOLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Panel Heading Color </td>
                    <td  data-label="Panel Heading Color" >
                        <input  type="text"id="panelheadingcolor"class="txtbox"name='<%=PartnerTemplatePreference.APANELHEADING_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.APANELHEADING_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.APANELHEADING_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Left Navigation Color </td>
                    <td  data-label="Left Navigation Color" >
                        <input  type="text"id="leftnavigationcolor"class="txtbox"name='<%=PartnerTemplatePreference.APANELBODY_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.APANELBODY_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.APANELBODY_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" >Label Font Color  </td>
                    <td data-label="Label Font Color">
                        <input type="text"id="fontcolor"  class="txtbox" name='<%=PartnerTemplatePreference.AHEADPANELFONT_COLOR.toString()%>' value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AHEADPANELFONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AHEADPANELFONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Heading Label Font Color </td>
                    <td  data-label="Panel Body Color">
                        <input type="text"id="hadinglabelfontcolor"class="txtbox" size="50" name='<%=PartnerTemplatePreference.ABODYPANELFONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.ABODYPANELFONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.ABODYPANELFONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Body BackGround Color </td>
                    <td  data-label="Body BackGround Color">
                        <input type="text"id="bodybackgroundcolor"class="txtbox"name='<%=PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.ABODY_BACKGROUND_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" >Body ForeGround Color </td>
                    <td  data-label="Body ForeGround Color">
                        <input type="text"id="bodyforegroundcolor"class="txtbox"name='<%=PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.ABODY_FOREGROUND_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Navigation Font Color </td>
                    <td  data-label="Navigation Font Color">
                        <input type="text"id="navigationfontcolor"class="txtbox"name='<%=PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.ANAVIGATION_FONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Textbox Color </td>
                    <td  data-label="Textbox Color">
                        <input type="text"id="textboxcolor"class="txtbox"name='<%=PartnerTemplatePreference.ATEXTBOX_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.ATEXTBOX_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.ATEXTBOX_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <td class="textb"  valign="middle" align="center" >Icon Vector Color  </td>
                <td  data-label="Textbox Color">
                    <input type="text"id="iconvectorcolor"class="txtbox"name='<%=PartnerTemplatePreference.AICON_VECTOR_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AICON_VECTOR_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AICON_VECTOR_COLOR.toString()):""%>"/>
                </td>
                </tr>

                <tr>
                    <td  valign="middle" align="center" class="th0" colspan=6 ><font size=2>New Checkout Template Colors</font></td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center">Body color </td>
                    <td data-label="Template BackGround Color">
                        <input type="text"id="newbackgroundcolor"class="txtbox" align="right" name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>' value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Header Background Color  </td>
                    <td  data-label="Panel Heading Color" >
                        <input  type="text"id="newpanelheadingcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Navigation Bar </td>
                    <td  data-label="Left Navigation Color" >
                        <input  type="text"id="newleftnavigationcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" > Button Font Color </td>
                    <td data-label="Label Font Color">
                        <input type="text"id="newfontcolor"  class="txtbox" name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>' value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Header Font Color</td>
                    <td  data-label="Panel Body Color">
                        <input type="text"id="newhadinglabelfontcolor"class="txtbox" size="50" name='<%=PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Full Background Color</td>
                    <td  data-label="Body BackGround Color">
                        <input type="text"id="newbodybackgroundcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" > Label Font Color </td>
                    <td  data-label="Body ForeGround Color">
                        <input type="text"id="newbodyforegroundcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Navigation Bar Font Color </td>
                    <td  data-label="Navigation Font Color">
                        <input type="text"id="newnavigationfontcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Button Color</td>
                    <td  data-label="Textbox Color">
                        <input type="text"id="newtextboxcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <td class="textb"  valign="middle" align="center" > Icon Color </td>
                <td  data-label="Textbox Color">
                    <input type="text"id="newiconvectorcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):""%>"/>
                </td>
                <td class="textb"  valign="middle" align="center" > Timer Color </td>
                <td  data-label="Timer Color">
                    <input type="text"id="newtimercolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):""%>"/>
                </td>
                <td class="textb"  valign="middle" align="center" > Box Shadow </td>
                <td  data-label="Box Shadow">
                    <input type="text"id="newboxshadow"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):""%>"/>
                </td>
                </tr>p
                <tr>
                    <td class="textb"  valign="middle" align="center" > Footer Font Color </td>
                    <td  data-label="Footer Color">
                        <input type="text"id="newfooterfontcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_FONT_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" > Footer Background Color </td>
                    <td  data-label="Footer Background Color">
                        <input type="text"id="newfooterbackgroundcolor"class="txtbox"name='<%=PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR.toString()):""%>"/>
                    </td>
                </tr>


                <tr>
                    <td  valign="middle" align="center" class="th0" colspan=6><font size=2>Mail Template Colors</font></td>
                </tr>

                <tr>
                    <td class="textb"  valign="middle" align="center" >Template BackGround Color</td>
                    <td  data-label="Template BackGround Color">
                        <input type="text"id="templatebackgroundcolor"class="txtbox"name='<%=PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Panel Heading Color</td>
                    <td  data-label="Panel Heading Color">
                        <input type="text"id="mailpanelheadingcolor"class="txtbox"name='<%=PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()):""%>"/>
                    </td>
                    <td class="textb"  valign="middle" align="center" >Label Font Color</td>
                    <td  data-label="Label Font Color">
                        <input type="text"id="maillabelfontcolor"class="txtbox"name='<%=PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <tr>
                    <td class="textb"  valign="middle" align="center" >Table Color</td>
                    <td  data-label="Table Color">
                        <input type="text"id="mailtablecolor"class="txtbox"name='<%=PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()%>' class="txtbox" value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()):""%>"/>
                    </td>
                </tr>
                <div>
                    <tr>
                        <td align="center" colspan="6">
                            <button type="submit" value="Save" class="buttonform btn btn-default" name="Save" onclick="mySave()">
                                Save
                            </button>
                        </td>
                    </tr>
                </div>
            </table>
        </div>
    </form>
    <% }
    else if (functions.isValueNull(error))
    {
        out.println("<h5 class=\"textb\" style=\"text-align: center;\">" + error+ "</h5>");

    }
    else if (functions.isValueNull(errormsg))
    {
        out.println("<h5 class=\"textb\" style=\"text-align: center;\">" + "Details Updated Succesfully"+ "</h5>");
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
    }
    %>
</div>
</div>
</div>
</body>
</html>
