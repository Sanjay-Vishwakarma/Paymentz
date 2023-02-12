
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.payment.cardinity.exceptions.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ include file="ietest.jsp" %>
<%@include file="Top.jsp" %>

<%
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String virtualCheckOut_virtual_checkout = StringUtils.isNotEmpty(rb1.getString("virtualCheckOut_virtual_checkout"))?rb1.getString("virtualCheckOut_virtual_checkout"): "Virtual Checkout";
    String virtualCheckOut_copy = StringUtils.isNotEmpty(rb1.getString("virtualCheckOut_copy"))?rb1.getString("virtualCheckOut_copy"): "Copy URL";
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("company"));
    session.setAttribute("submit", "virtualCheckOut");
    /*if (!merchants.isLoggedIn(session))
    {*/
%>

   <%--Created by Sanjeet on 30-10-2019.--%>

<html lang="en">
<head>
    <title><%=company%> Merchant Virtual CheckOut</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <style type="text/css">
        #myTable th {
            text-align: center;
        }

        #myTable td {
            font-family: Open Sans;
            font-size: 13px;
            font-weight: 600;
        }

        #myTable .button3 {
            text-indent: 0 !important;
            width: 100%;
            height: inherit !important;
            display: block;
            color: #000000;
            padding: 0px;
            background: transparent !important;
            border: 0px solid #dedede;
            text-align: center !important;
            font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
            font-size: 12px;
        }

    </style>
    <style type="text/css">
        @media (min-width: 992px) {
            #dateid, #monthid {width: initial;}
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
<body class="pace-done widescreen fixed-left-void bodybackground">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=virtualCheckOut_virtual_checkout%></strong></h2>

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
                                    String URL = "https://" + V_HOST_URL + "/transaction/VirtualCheckout?memberId=" + V_ENCRYPT_MEMBER + "&totype=" + company;
                                    out.println("<table>");
                                    out.println("<tr><td>Virtual Checkout URL</td><td> <button value=" + URL + " class=\"btn btn-default\" style=\"display:block;\" onclick=\"url.value=this.value; copyToClipboard();\">"+virtualCheckOut_copy+"</button></td></tr>");
                                    out.println("</table>");
                                    out.println("<table style=\"table-layout: fixed;  width: 100%;\">");
                                    out.println("<tr><td style=\"word-wrap: break-word;\"><a>" + URL + "</a></td></tr>");
                                    out.println("</table>");
                                    out.println("<br>");
                                    /*if (hash != null && hash.size() > 0)
                                    {*/
                                %>

                                <%
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
                                            out.println("<tr><td>Virtual Checkout URL for " + ESAPI.encoder().encodeForHTML((String) innerHash.get("CURRENCY")) + "</td><td><button value=" + URL_currency + " class=\"btn btn-default\" style=\"display block\" onclick=\"url.value=this.value; copyToClipboard();\">"+virtualCheckOut_copy+"</button></td><tr>");
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


            <%--<%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                }

            %>--%>


                        <form name="form" method="post" action="/merchant/servlet/VirtualCheckOut?ctoken=<%=ctoken%>">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                <%
                                 if(request.getParameter("MES")!=null)
                                    {
                                     String mes=request.getParameter("MES");
                  if(mes.equals("ERR"))
                  {
                    if(request.getAttribute("validationErrorList")!=null)
                    {
                      ValidationErrorList error= (ValidationErrorList) request.getAttribute("validationErrorList");
                      for(Object errorList : error.errors())
                      {
                        ValidationException ve = (ValidationException) errorList;
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                      }
                    }
                    else if(request.getAttribute("catchError")!=null)
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                    }
                  }
                }
              %>
                    </div>
                </div>
                </form>
            </div>
        </div>
    </div>
</form>
<input type="hidden" id="url">

<div style="visibility:hidden;position:fixed" id="tooltipDiv" class="tooltip"></div>
</div>
</div>
</div>
<%--<%
    }
    else
    {
    response.sendRedirect("/merchant/logout.jsp");
    return;
    }
%>--%>
</body>
</html>
</div>
</div>
</div>
</div>
