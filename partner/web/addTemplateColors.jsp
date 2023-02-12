<%--
  Created by IntelliJ IDEA.
  User: Namrata
  Date: 6/13/2020
  Time: 6:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.enums.PartnerTemplatePreference" %>
<%@ include file="top.jsp" %>

<%!
    private static Logger log = new Logger("partnerlist");
%>
<%
    // String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit", "partnerlist");
%>


<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String partnerid = request.getParameter("partnerId") == null ? "" : request.getParameter("partnerId");
%>
<html>
<head>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script>

        $(document).ready(function ()
        {

            var w = $(window).width();

            //alert(w);

            if (w > 990)
            {
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else
            {
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>
</head>
<title> Add Template Colors </title>

<body class="bodybackground">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>" method="post" name="form">
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br><br><br>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Add Template Colors
                            </strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/AddTemplateColor?ctoken=<%=ctoken%>" method="post"
                                      name="forms">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=String.valueOf(session.getAttribute("partnerId"))%>"
                                           name="superAdminId" id="partnerid">
                                    <%
                                        Functions functions = new Functions();
                                        String success = (String) request.getAttribute("cbmessage");
                                        if (functions.isValueNull((String) request.getAttribute("error")))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + (String) request.getAttribute("error") + "</h5>");
                                        }
                                        else if (functions.isValueNull(success))
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp; Details Updated Succesfully </h5>");
                                        }
                                    %>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-3 control-label">Partner ID</label>

                                        <div class="col-sm-8">
                                            <input name="partnerId" id="PID" value="<%=partnerid%>" class="form-control"
                                                   autocomplete="on">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-8">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <form action="/partner/net/SetReservesTemlate?ctoken=<%=ctoken%>" method=post>
                <input type="hidden" value="<%=partnerid%>" name="partnerId">

                <%
                    Map<String, Object> partnerTemplateColor = null;
                    if (request.getAttribute("partnerTemplateColor") != null)
                    {
                        partnerTemplateColor = (Map<String, Object>) request.getAttribute("partnerTemplateColor");
                %>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>New Checkout Template
                                    Colors</strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Body & Footer color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Header Background Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Navigation Bar
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Button Font Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Header Font Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Full Background Color
                                            </td>

                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Body & Footer color" align="center"

                                                class="tr0"><input type="text" id="newbackgroundcolor"
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>'
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):""%>"/>

                                            </td>

                                            <td valign="middle" data-label="Header Background Color" align="center"

                                                class="tr0"><input type="text" id="newpanelheadingcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Navigation Bar" align="center"

                                                class="tr0"><input type="text" id="newleftnavigationcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Button Font Color" align="center"

                                                class="tr0"><input type="text" id="newfontcolor"
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>'
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Header Font Color" align="center"

                                                class="tr0"><input type="text" id="newhadinglabelfontcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Full Background Color" align="center"

                                                class="tr0"><input type="text" id="newbodybackgroundcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):""%>"/>

                                            </td>


                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Label Font Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Navigation Bar Font Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Button Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Icon Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Timer Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Box Shadow
                                            </td>

                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Label Font Color" align="center"

                                                class="tr0"><input type="text" id="newbodyforegroundcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Navigation Bar Font Color" align="center"

                                                class="tr0"><input type="text" id="newnavigationfontcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):""%>"/>
                                            </td>
                                            <td valign="middle" data-label="Button Color" align="center"

                                                class="tr0"><input type="text" id="newtextboxcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):""%>"/>
                                            </td>
                                            <td valign="middle" data-label="Icon Color" align="center"

                                                class="tr0"><input type="text" id="newiconvectorcolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):""%>"/>
                                            </td>
                                            <td valign="middle" data-label="Timer Color" align="center"

                                                class="tr0"><input type="text" id="newtimercolor"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>'
                                                                   class="form-control"
                                                                   style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):""%>"/>
                                            </td>
                                            <td valign="middle" data-label="Box Shadow" align="center"

                                                class="tr0"><input type="text" id="newboxshadow"
                                                                   name='<%=PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>'
                                                                   class="form-control" style="background: #ffffff"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):""%>"/>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>


                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Mail Template Colors</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Template BackGround Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Panel Heading Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Label Font Color
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Table Color
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Template BackGround Color" align="center"

                                                class="tr0"><input type="text" id="templatebackgroundcolor"
                                                                   class="form-control" style="background: #ffffff"
                                                                   name='<%=PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()%>'
                                                                   class="txtbox"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAILBACKGROUNDCOLOR.toString()):""%>"/>

                                            </td>

                                            <td valign="middle" data-label="Panel Heading Color" align="center"

                                                class="tr0"><input type="text" id="mailpanelheadingcolor"
                                                                   class="form-control" style="background: #ffffff"
                                                                   name='<%=PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()%>'
                                                                   class="txtbox"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAIL_PANELHEADING_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Label Font Color" align="center"

                                                class="tr0"><input type="text" id="maillabelfontcolor"
                                                                   class="form-control" style="background: #ffffff"
                                                                   name='<%=PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()%>'
                                                                   class="txtbox"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAIL_HEADPANELFONT_COLOR.toString()):""%>"/>
                                            </td>

                                            <td valign="middle" data-label="Table Color" align="center"

                                                class="tr0"><input type="text" id="mailtablecolor" class="form-control"
                                                                   style="background: #ffffff"
                                                                   name='<%=PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()%>'
                                                                   class="txtbox"
                                                                   value="<%=(partnerTemplateColor!=null && partnerTemplateColor.containsKey(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()))?partnerTemplateColor.get(PartnerTemplatePreference.AMAIL_BODYPANELFONT_COLOR.toString()):""%>"/>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>

                <table align="center" id="smalltable" border="0"
                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <tr style="background-color: #ffffff!important;">
                        <td align="center" colspan="2">
                            <button type="submit" value="Save" class="btn btn-default">
                                Save
                            </button>
                        </td>
                    </tr>
                </table>
            </form>
            <br>
            <%
            }
            else
            {
            %>
            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%
                                out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found."));
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</body>
</html>