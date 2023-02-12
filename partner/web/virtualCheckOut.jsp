<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ include file="top.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Namrata
  Date: 25-09-2019
  Time: 14:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String str = "";
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    str = "&ctoken=" + ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
    String flaghash = Functions.checkStringNull(request.getParameter("memberid"));
    str = str + "&SRecords=" + pagerecords;
    session.setAttribute("submit", "member");

%>
<html>
<head>
    <title><%=company%> Merchant Management> Virual CheckOut</title>
    <style type="text/css">

        @media (max-width: 767px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit !important;
            }
        }

        body {
            margin: 10px;
        }


    </style>
    <script>

        function copyToClipboard()
        {
            var copyText = document.getElementById("url");
            var ConfFile = $("#url");
            ConfFile.attr('type', 'text');
            ConfFile.show();
            copyText.select();
            copyText.setSelectionRange(0, 99999)
            document.execCommand("copy");
            ConfFile.hide();
        }

    </script>
</head>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<body>
<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions = new Functions();
    if (partner.isLoggedInPartner(session))
    {
        TreeMap<String, String> memberidDetails = partner.getPartnerMemberDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
%>
<%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

<script type="text/javascript">
    $('#sandbox-container input').datepicker({});
</script>
<script>
    $(function ()
    {
        $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
    });
</script>


<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Virtual Checkout</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>

                        </div>
                        <form name="forms" method="post"
                              action="/partner/net/VirtualCheckOutList?ctoken=<%=ctoken%>">
                            <div class="widget-content padding">
                                <div id="horizontal-form">


                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <%
                                        String error = (String) request.getAttribute("errormessage");
                                        if (functions.isValueNull(error))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                        }
                                    %>


                                    <div class="form-group col-md-8">
                                        <label class="col-sm-3 control-label">Merchant ID</label>


                                        <div class="col-sm-4">
                                            <select class="form-control" name="memberid">
                                                <option value="">Select Merchant Id</option>

                                                <%
                                                    Set statusflagSet = memberidDetails.keySet();
                                                    Iterator it = statusflagSet.iterator();
                                                    String selected3 = "";
                                                    String key3 = "";
                                                    String value3 = "";
                                                    while (it.hasNext())
                                                    {
                                                        key3 = (String) it.next();
                                                        value3 = memberidDetails.get(key3);

                                                        if (key3.equals(flaghash))
                                                            selected3 = "selected";
                                                        else
                                                            selected3 = "";

                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key3)%>" <%=selected3%>><%=ESAPI.encoder().encodeForHTML(value3)%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>


                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
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
                        <br>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <%
                                    String V_HOST_URL = (String) request.getAttribute("v_host_url");
                                    String V_ENCRYPT_MEMBER = (String) request.getAttribute("encrypt_member");
                                    HashMap hash = (HashMap) request.getAttribute("currencyList");
                                    Functions function = new Functions();
                                      if (hash != null && hash.size() > 0)
                                    {
                                %>

                                <%
                                    String URL = "https://" + V_HOST_URL + "/transaction/VirtualCheckout?memberId=" + V_ENCRYPT_MEMBER + "&totype=" + company;
                                    out.println("<table>");
                                    out.println("<tr><td>Virtual Checkout URL</td><td> <button value=" + URL + " class=\"btn btn-default\" style=\"display:block;\" onclick=\"url.value=this.value; copyToClipboard();\">Copy URL</button></td></tr>");
                                    out.println("</table>");
                                    out.println("<table style=\"table-layout: fixed;  width: 100%;\">");
                                    out.println("<tr><td style=\"word-wrap: break-word;\"><a>" + URL + "</a></td></tr>");
                                    out.println("</table>");
                                    out.println("<br>");


                                    TreeMap mappingMap = new TreeMap(hash);
                                    Iterator itr2 = mappingMap.keySet().iterator();
                                    while (itr2.hasNext())
                                    {
                                        Object mappingId;
                                        mappingId = itr2.next();
                                        HashMap innerHash = (HashMap) mappingMap.get(mappingId);
                                        String URL_currency = "https://" + V_HOST_URL + "/transaction/VirtualCheckout?memberId=" + V_ENCRYPT_MEMBER + "&totype=" + company + "&amp;currency=" + ESAPI.encoder().encodeForHTML((String) innerHash.get("CURRENCY"));

                                        if (!ESAPI.encoder().encodeForHTML((String) innerHash.get("CURRENCY")).equals("ALL"))
                                        {
                                            out.println("<table>");
                                            out.println("<tr><td>Virtual Checkout URL for " + ESAPI.encoder().encodeForHTML((String) innerHash.get("CURRENCY")) + "</td><td><button value=" + URL_currency + " class=\"btn btn-default\" style=\"display block\" onclick=\"url.value=this.value; copyToClipboard();\">Copy URL</button></td><tr>");
                                            out.println("</table>");
                                            out.println("<table style=\"table-layout: fixed; width: 100%;\">");
                                            out.println("<tr><td style=\"word-wrap: break-word;\"><a>" + URL_currency + "</a></td><tr>");
                                            out.println("</table>");
                                            out.println("<br>");
                                        }
                                    }
                                %>
                            </div>
                        </div>
                    </div>

                </div>
            </div>


            <%
                }
                else if(function.isValueNull(V_HOST_URL))
                {
                    String URL = "https://" + V_HOST_URL + "/transaction/VirtualCheckout?memberId=" + V_ENCRYPT_MEMBER + "&totype=" + company;
                    out.println("<table>");
                    out.println("<tr><td>Virtual Checkout URL</td><td> <button value=" + URL + " class=\"btn btn-default\" style=\"display:block;\" onclick=\"url.value=this.value; copyToClipboard();\">Copy URL</button></td></tr>");
                    out.println("</table>");
                    out.println("<table style=\"table-layout: fixed;  width: 100%;\">");
                    out.println("<tr><td style=\"word-wrap: break-word;\"><a>" + URL + "</a></td></tr>");
                    out.println("</table>");
                    out.println("<br>");

                }else
                {
                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                }
            %>


            </form>
            <input type="hidden" id="url">

            <div style="visibility:hidden;position:fixed" id="tooltipDiv" class="tooltip"></div>
        </div>
    </div>
</div>
<%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
</body>
</html>