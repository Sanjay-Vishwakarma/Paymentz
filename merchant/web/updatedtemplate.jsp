<%@ page language="java" import="com.directi.pg.Template,
                                 com.logicboxes.util.ApplicationProperties,
                                 java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>

<html>
<head>

    <title><%=company%> Merchant Settings > Customise Template > Updated</title>



    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>

    <link rel="stylesheet" type="text/css" href="style.css"/>

    <script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>--%>
</head>

<body class="pace-done widescreen fixed-left-void ">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Checkout Page</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/merchant/servlet/UpdateMerchantTemplate?ctoken=<%=ctoken%>" method="post" name="form1">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <div class="table table-responsive">

                                        <table class="table table-striped table-bordered" id="textheadid" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                        <%

                                            Enumeration enumeration = Template.defaulthash.keys();
                                            Hashtable memberTemplateHash1 = Template.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
                                            Hashtable memberTemplateHash2 = merchants.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
                                            Hashtable imageHash = new Hashtable();
                                            for (int i = 1; i <= Template.noofimages; i++)
                                            {
                                                String filename = (String) memberTemplateHash1.get("IMAGE" + i);
                                                if (filename != null)
                                                {
                                                    imageHash.put("IMAGE" + i, filename);
                                                }
                                            }
                                            while (enumeration.hasMoreElements())
                                            {
                                                String name = (String) enumeration.nextElement();
                                                String value = (String) memberTemplateHash1.get(name);
                                                String label = (String) Template.defaultlabelhash.get(name);

                                                if (name.equalsIgnoreCase("HEADER"))
                                                {
                                                    value = Template.replaceImageTag(value, imageHash);
                                                }
                                                else if (name.equalsIgnoreCase("BACKGROUND"))
                                                {
                                                    String img = Template.replaceBackgroundImageTag(value, imageHash);
                                                    if (img == null || img.length() == 0)
                                                        value = "None";
                                                    else
                                                        value = "<img border=\"0\" height=\"200\" width=\"200\" src=\"" + img + "\">";
                                                }
                                        %>

                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;"><%=label%></td>
                                            <td align="center" style="width: 50%">

                                                <%=ESAPI.encoder().encodeForHTML(value)%>
                                            </td>
                                        </tr>

                                        <%
                                            }
                                        %>
                                        <%
                                            String template = "No";
                                            if (((String) memberTemplateHash2.get("template")).equalsIgnoreCase("Y"))
                                                template = "Yes";
                                        %>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Enable Template</td>
                                            <td align="center" style="width: 50%">
                                                <%=template%>
                                            </td>
                                        </tr>
                                        <%
                                            String autoredirect = "No";
                                            if (((String) memberTemplateHash2.get("autoredirect")).equalsIgnoreCase("Y"))
                                                autoredirect = "Yes";
                                        %>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Auto Redirect</td>
                                            <td align="center" style="width: 50%">
                                                <%=autoredirect%>
                                            </td>
                                        </tr>

                                        <% String algorithm = null;
                                            if ("Adler32".equals(memberTemplateHash2.get("checksumalgo")))
                                            {
                                                algorithm = "Adler32";
                                            }
                                            else
                                            {
                                                algorithm = "MD5";
                                            }
                                        %>

                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Checksum Algorithm</td>
                                            <td align="center" style="width: 50%">
                                                <%=algorithm%>
                                            </td>
                                        </tr>

                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">PHONE NUMBER 1</td>
                                            <td align="center" style="width: 50%">
                                                <% if(memberTemplateHash1.get("PHONE1")!=null){%>
                                                <%=ESAPI.encoder().encodeForHTML((String)memberTemplateHash1.get("PHONE1"))%>
                                                <%
                                                    }
                                                %>
                                            </td>
                                        </tr>


                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">PHONE NUMBER 2</td>
                                            <td align="center" style="width: 50%">
                                                <% if(memberTemplateHash1.get("PHONE2")!=null){%>
                                                <%=ESAPI.encoder().encodeForHTML((String)memberTemplateHash1.get("PHONE2"))%>
                                                <%
                                                    }
                                                %>
                                            </td>
                                        </tr>


                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Email ID's</td>
                                            <td align="center" style="width: 50%">
                                                <% if(memberTemplateHash1.get("EMAILS")!=null){%>
                                                <%=ESAPI.encoder().encodeForHTML((String)memberTemplateHash1.get("EMAILS"))%>
                                                <%
                                                    }
                                                %>
                                            </td>
                                        </tr>

                                    </table>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
